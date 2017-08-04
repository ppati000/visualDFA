package dfa.analyses;

import dfa.framework.Join;

import java.util.Set;

/**
 * @author Patrick Petrovic
 *
 *         A {@code DummyJoin} performs the join for a {@code DummyAnalysis}.
 */
public class DummyJoin implements Join<DummyElement> {

    @Override
    public DummyElement join(Set<DummyElement> elements) {
        return new DummyElement(DummyElement.ValueType.SOMETHING);
    }

}
