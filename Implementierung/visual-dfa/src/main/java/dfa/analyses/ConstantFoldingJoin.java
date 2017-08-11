package dfa.analyses;

import java.util.Iterator;
import java.util.Set;

import dfa.analyses.ConstantFoldingElement.Value;
import dfa.framework.Join;
import soot.jimple.internal.JimpleLocal;

/**
 * @author Nils Jessen
 * @author Sebastian Rauch
 * 
 *         A {@code ConstantFoldingJoin} performs the join for a {@code ConstantFoldingAnalysis}.
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
            if (elements.isEmpty()) {
                throw new IllegalArgumentException("there must be at least one value to join");
            }

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

    // the old join
// @formatter:off
//    @Override
//    public ConstantFoldingElement join(Set<ConstantFoldingElement> elements) {
//
//        Iterator<ConstantFoldingElement> it = elements.iterator();
//        ConstantFoldingElement refElement = it.next();
//        Map<JimpleLocal, ConstantFoldingElement.Value> refMap = refElement.getLocalMap();
//        while (it.hasNext()) {
//            ConstantFoldingElement compElement = it.next();
//            Map<JimpleLocal, ConstantFoldingElement.Value> compMap = compElement.getLocalMap();
//
//            for (Map.Entry<JimpleLocal, ConstantFoldingElement.Value> entry : refMap.entrySet()) {
//                if (!compMap.containsKey(entry.getKey())) {
//                    throw new IllegalArgumentException("locals not matching");
//                }
//            }
//
//            for (Map.Entry<JimpleLocal, ConstantFoldingElement.Value> entry : compMap.entrySet()) {
//                if (!refMap.containsKey(entry.getKey())) {
//                    throw new IllegalArgumentException("locals not matching");
//                }
//            }
//        }
//
//        ConstantFoldingElement result = new ConstantFoldingElement();
//        for (Map.Entry<JimpleLocal, ConstantFoldingElement.Value> entry : refMap.entrySet()) {
//            Iterator<ConstantFoldingElement> elementIt = elements.iterator();
//            JimpleLocal local = entry.getKey();
//            Value tmp = Value.getBottom();
//
//            while (elementIt.hasNext()) {
//                ConstantFoldingElement current = elementIt.next();
//                Value currentVal = current.getValue(local);
//                if (currentVal.equals(Value.getTop())) {
//                    result.setValue(local, Value.getTop());
//                    break;
//                }
//
//                if (currentVal.isConst()) {
//                    if (tmp.equals(Value.getBottom())) {
//                        tmp = currentVal;
//                    } else if (!tmp.equals(currentVal)) {
//                        result.setValue(local, Value.getTop());
//                        break;
//                    }
//                }
//            }
//
//            if (!Value.getTop().equals(result.getValue(local))) {
//                result.setValue(local, tmp);
//            }
//        }
//
//        return result;
//    }
// @formatter:on

}
