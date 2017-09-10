package dfa.analyses;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import dfa.analyses.ReachingDefinitionsElement.DefinitionSet;
import dfa.analyses.ReachingDefinitionsElement.DefinitionType;
import dfa.framework.Join;
import soot.jimple.internal.JimpleLocal;

/**
 * A {@code ReachingDefinitionsJoin} performs the join for a {@code ReachingDefinitionsAnalysis}.
 * 
 * @author Nils Jessen
 */
public class ReachingDefinitionsJoin implements Join<ReachingDefinitionsElement> {

    private JoinHelper joinHelper = new JoinHelper();

    @Override
    public ReachingDefinitionsElement join(Set<ReachingDefinitionsElement> elements) {
        return joinHelper.performJoin(elements);
    }

    private static class JoinHelper extends LocalMapElementJoinHelper<DefinitionSet, ReachingDefinitionsElement> {

        @Override
        public DefinitionSet doValueJoin(Set<ReachingDefinitionsElement> elements, JimpleLocal local) {

            Iterator<? extends LocalMapElement<DefinitionSet>> elementIt = elements.iterator();
            DefinitionSet refVal = elementIt.next().getValue(local);

            Set<String> joinResult = new TreeSet<String>();
            joinResult.addAll(refVal.getValues());

            while (elementIt.hasNext()) {
                DefinitionSet currentVal = elementIt.next().getValue(local);
                if (currentVal.getDefType() != DefinitionType.BOTTOM) {
                    joinResult.addAll(currentVal.getValues());
                } else {
                }
            }

            return new DefinitionSet(joinResult);
        }
    }
}
