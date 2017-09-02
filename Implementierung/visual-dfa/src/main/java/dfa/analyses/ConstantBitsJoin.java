package dfa.analyses;

import java.util.Iterator;
import java.util.Set;

import dfa.analyses.ConstantBitsElement.BitValue;
import dfa.analyses.ConstantBitsElement.BitValueArray;
import dfa.framework.Join;
import soot.jimple.internal.JimpleLocal;

/**
 * @author Nils Jessen
 * 
 *         A {@code ConstantBitsJoin} performs the join for a {@code ConstantBitsAnalysis}.
 */
public class ConstantBitsJoin implements Join<ConstantBitsElement> {

    private JoinHelper joinHelper = new JoinHelper();

    @Override
    public ConstantBitsElement join(Set<ConstantBitsElement> elements) {
        return joinHelper.performJoin(elements);
    }

    public JoinHelper getJoinHelper() {
        return joinHelper;
    }

    protected static class JoinHelper extends LocalMapElementJoinHelper<BitValueArray, ConstantBitsElement> {

        @Override
        public BitValueArray doValueJoin(Set<ConstantBitsElement> elements, JimpleLocal local) {
            if (elements.isEmpty()) {
                throw new IllegalArgumentException("there must be at least one value to join");
            }

            Iterator<? extends LocalMapElement<BitValueArray>> elementIt = elements.iterator();

            BitValueArray refVal = elementIt.next().getValue(local);
            
            // TODO refactor
            int length = refVal.getLength();
            BitValueArray top;
            BitValueArray bottom;
            if (length == BitValueArray.INT_SIZE) {
                top = BitValueArray.getIntTop();
                bottom = BitValueArray.getIntBottom();
            } else if (length == BitValueArray.LONG_SIZE) {
                top = BitValueArray.getLongTop();
                bottom = BitValueArray.getLongBottom();
            } else {
                throw new IllegalStateException("BitValueArrays must be of size INT_SIZE or LONG_SIZE");
            }

            while (elementIt.hasNext()) {
                BitValueArray currentVal = elementIt.next().getValue(local);
                refVal = performSingleJoin(refVal, currentVal, length, top, bottom);
            }
            return refVal;
        }

        // TODO refactor to remove parameters length, top, bottom
        protected BitValueArray performSingleJoin(BitValueArray refVal, BitValueArray currentVal, int length,
                BitValueArray top, BitValueArray bottom) {
            if (currentVal.getLength() != length) {
                // First check if BitValueArrays to be joined are of same size
                throw new IllegalStateException("Unable to join BitValueArrays of different size!");
            }

            BitValue[] result = new BitValue[length];
            for (int i = 0; i < length; i++) {
                BitValue currentBit = currentVal.getBitValues()[i];
                BitValue tmpBit = refVal.getBitValues()[i];

                if (currentBit == BitValue.TOP || tmpBit == BitValue.TOP) {
                    result[i] = BitValue.TOP;
                } else if (currentBit == BitValue.BOTTOM) {
                    result[i] = tmpBit;
                } else if (tmpBit == BitValue.BOTTOM) {
                    result[i] = currentBit;
                } else if (currentBit == tmpBit) {
                    result[i] = currentBit;
                } else {
                    result[i] = BitValue.TOP;
                }
            }

            return new BitValueArray(result);
        }
    }
}
