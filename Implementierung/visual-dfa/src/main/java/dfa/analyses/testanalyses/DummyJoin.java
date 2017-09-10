package dfa.analyses.testanalyses;

import dfa.framework.Join;

import java.util.Set;

/**
 * A {@code DummyJoin} performs the join for a {@code DummyAnalysis}.
 * 
 * @author Patrick Petrovic
 */
public class DummyJoin implements Join<DummyElement> {

    @Override
    public DummyElement join(Set<DummyElement> elements) {
        return new DummyElement(DummyElement.ValueType.SOMETHING);
    }

}
