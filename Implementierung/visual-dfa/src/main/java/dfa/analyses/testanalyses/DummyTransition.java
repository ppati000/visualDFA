package dfa.analyses.testanalyses;

import dfa.framework.Transition;
import soot.Unit;

/**
 * @author Patrick Petrovic
 *
 *         A {@code DummyTransition} performs the transition for a {@code DummyAnalysis}.
 */
public class DummyTransition implements Transition<DummyElement> {
    @Override
    public DummyElement transition(DummyElement element, Unit unit) {
        return new DummyElement(DummyElement.ValueType.SOMETHING);
    }
}
