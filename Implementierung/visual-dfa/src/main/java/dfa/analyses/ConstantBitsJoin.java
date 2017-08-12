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

    private static class JoinHelper extends LocalMapElementJoinHelper<BitValueArray, ConstantBitsElement> {

        @Override
        public BitValueArray doValueJoin(Set<ConstantBitsElement> elements, JimpleLocal local) {
            if (elements.isEmpty()) {
                throw new IllegalArgumentException("there must be at least one value to join");
            }

            Iterator<? extends LocalMapElement<BitValueArray>> elementIt = elements.iterator();

            BitValueArray refVal = elementIt.next().getValue(local);
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

                if (currentVal.getLength() != length) {
                    // First check if BitValueArrays to be joined are of same size
                    throw new IllegalStateException("Unable to join BitValueArrays of different size!");
                }

                if (refVal.equals(top) || currentVal.equals(top)) {
                    // Second if one is TOP, if so, no need to check further
                    return top;

                } else if (refVal.equals(bottom)) {
                    // Third if refVal is bottom, new refVal is currentVal since join(bottom, x) = x for all x
                    refVal = currentVal;

                    // Fourth if currentVal is bottom, refVal remains untouched since join(bottom, x) = x for all x
                    // Also if refVal equals currentVal, refVal remains untouched, since nothing would change
                    // But if refVal does not equal currentVal and currentVal is not bottom, we have to compare them bit
                    // by bit
                } else if (!refVal.equals(currentVal) && !currentVal.equals(bottom)) {
                    BitValue[] bitValues = new BitValue[length];
                    for (int i = 0; i < length; i++) {
                        BitValue currentValBit = currentVal.getBitValues()[i];
                        BitValue tmpBit = refVal.getBitValues()[i];

                        if (currentValBit.equals(BitValue.TOP) || tmpBit.equals(BitValue.TOP)) {
                            // If one of the two bits is TOP, the resulting bit is TOP
                            bitValues[i] = BitValue.TOP;

                        } else if (currentValBit.equals(BitValue.BOTTOM)) {
                            // If one of the bits is BOTTOM, the resulting bit is whatever the other bit was
                            bitValues[i] = tmpBit;
                        } else if (refVal.getBitValues()[i].equals(BitValue.BOTTOM)) {
                            // If one of the bits is BOTTOM, the resulting bit is whatever the other bit was
                            bitValues[i] = currentValBit;

                        } else if (currentValBit.equals(tmpBit)) {
                            // If they are the same, we just take one of them
                            bitValues[i] = currentValBit;

                        } else {
                            // If they are both neither bottom nor the same, the resulting bit is TOP
                            bitValues[i] = BitValue.TOP;
                        }
                    }
                    refVal = new BitValueArray(bitValues);
                }
            }
            return refVal;
        }
    }
}
