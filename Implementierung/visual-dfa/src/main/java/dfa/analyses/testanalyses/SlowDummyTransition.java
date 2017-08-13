package dfa.analyses.testanalyses;

import dfa.framework.Transition;
import soot.Unit;

/**
 * @author Sebastian Rauch
 *
 *         A {@code DummyTransition} performs the transition for a {@code DummyAnalysis}, but takes a long time.
 */
public class SlowDummyTransition implements Transition<DummyElement> {
    
    private int waitTime;
    
    public SlowDummyTransition(int waitTime) {
        this.waitTime = waitTime < 0 ? 0 : waitTime;
    }
    
    @Override
    public DummyElement transition(DummyElement element, Unit unit) {
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        return new DummyElement(DummyElement.ValueType.SOMETHING);
    }
}