package dfa.analyses.testanalyses;

import dfa.framework.Transition;
import soot.Unit;

/**
 * A {@code DummyTransition} performs the transition for a {@code DummyAnalysis}.
 * 
 * @author Patrick Petrovic
 */
public class DummyTransition implements Transition<DummyElement> {
    @Override
    public DummyElement transition(DummyElement element, Unit unit) {
        return new DummyElement(DummyElement.ValueType.SOMETHING);
    }
}
