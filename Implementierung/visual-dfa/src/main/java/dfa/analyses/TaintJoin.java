package dfa.analyses;

import java.util.Iterator;
import java.util.Set;

import dfa.analyses.TaintElement.TaintState;
import dfa.analyses.TaintElement.Value;
import dfa.framework.Join;
import soot.jimple.internal.JimpleLocal;

/**
 * @author Sebastian Rauch
 * 
 *         A {@code TaintJoin} performs the join for a {@code TaintAnalysis}.
 */
public class TaintJoin implements Join<TaintElement> {

    private JoinHelper joinHelper = new JoinHelper();

    @Override
    public TaintElement join(Set<TaintElement> elements) {
        return joinHelper.performJoin(elements);
    }

    private static class JoinHelper extends LocalMapElementJoinHelper<Value, TaintElement> {

        @Override
        public Value doValueJoin(Set<TaintElement> elements, JimpleLocal local) {
            if (elements.isEmpty()) {
                throw new IllegalArgumentException("there must be at least one value to join");
            }

            Iterator<? extends LocalMapElement<Value>> elementIt = elements.iterator();
            Value refVal = elementIt.next().getValue(local);
            boolean refViolation = refVal.wasViolated();
            Value result = new Value(null, refViolation);
            while (elementIt.hasNext()) {
                Value currentVal = elementIt.next().getValue(local);

                if (currentVal.wasViolated()) {
                    refViolation = true;
                }

                switch (currentVal.getTaintState()) {
                case TAINTED:
                    result.setTaintState(TaintState.TAINTED);
                    break;
                case CLEAN:
                    if (result.getTaintState().equals(TaintState.BOTTOM)) {
                        result.setTaintState(TaintState.CLEAN);
                    }
                    break;
                default: // ignore (also bottom)
                }

            }

            return result;
        }
    }

}
