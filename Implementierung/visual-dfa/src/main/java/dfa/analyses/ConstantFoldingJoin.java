package dfa.analyses;

import java.util.Iterator;
import java.util.Set;

import dfa.analyses.ConstantFoldingElement.Value;
import dfa.framework.Join;
import soot.jimple.internal.JimpleLocal;

/**
 * A {@code ConstantFoldingJoin} performs the join for a {@code ConstantFoldingAnalysis}.
 * 
 * @author Nils Jessen
 * @author Sebastian Rauch
 */
public class ConstantFoldingJoin implements Join<ConstantFoldingElement> {

    private JoinHelper joinHelper = new JoinHelper();

    @Override
    public ConstantFoldingElement join(Set<ConstantFoldingElement> elements) {
        return joinHelper.performJoin(elements);
    }

    private static class JoinHelper extends LocalMapElementJoinHelper<Value, ConstantFoldingElement> {

        @Override
        public Value doValueJoin(Set<ConstantFoldingElement> elements, JimpleLocal local) {
            Iterator<? extends LocalMapElement<Value>> elementIt = elements.iterator();
            Value refVal = elementIt.next().getValue(local);

            if (refVal.equals(Value.getTop())) {
                return Value.getTop();
            }

            Value tmp = refVal;

            while (elementIt.hasNext()) {
                Value currentVal = elementIt.next().getValue(local);
                if (currentVal.equals(Value.getTop())) {
                    return Value.getTop();
                }

                if (currentVal.isConst()) {
                    if (tmp.equals(Value.getBottom())) {
                        tmp = currentVal;
                    } else if (!tmp.equals(currentVal)) {
                        return Value.getTop();
                    }
                }

            }

            return tmp;
        }
    }
}