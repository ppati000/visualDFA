package dfa.analyses;

import java.util.Iterator;
import java.util.Set;

import dfa.analyses.ReachingDefinitionsElement.Definition;
import dfa.framework.Join;
import soot.jimple.internal.JimpleLocal;

/**
 * @author Nils Jessen
 * 
 *         A {@code ReachingDefinitionsJoin} performs the join for a {@code ReachingDefinitionsAnalysis}.
 */
public class ReachingDefinitionsJoin implements Join<ReachingDefinitionsElement> {

    private JoinHelper joinHelper = new JoinHelper();

    @Override
    public ReachingDefinitionsElement join(Set<ReachingDefinitionsElement> elements) {
        return joinHelper.performJoin(elements);
    }

    private static class JoinHelper extends LocalMapElementJoinHelper<Definition, ReachingDefinitionsElement> {

        @Override
        public Definition doValueJoin(Set<ReachingDefinitionsElement> elements, JimpleLocal local) {
            if (elements.isEmpty()) {
                throw new IllegalArgumentException("there must be at least one value to join");
            }

            Iterator<? extends LocalMapElement<Definition>> elementIt = elements.iterator();
            Definition refVal = elementIt.next().getValue(local);

            if (refVal.equals(Definition.getTop())) {
                return Definition.getTop();
            }

            Definition tmp = refVal;

            while (elementIt.hasNext()) {
                Definition currentVal = elementIt.next().getValue(local);
                if (currentVal.equals(Definition.getTop())) {
                    return Definition.getTop();
                }

                if (currentVal.isActualDefinition()) {
                    if (tmp.equals(Definition.getBottom())) {
                        tmp = currentVal;
                    } else if (!tmp.equals(currentVal)) {
                        return Definition.getTop();
                    }
                }

            }
            return tmp;
        }
    }
}
