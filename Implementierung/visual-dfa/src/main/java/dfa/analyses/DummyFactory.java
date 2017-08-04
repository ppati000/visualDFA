package dfa.analyses;

import dfa.framework.*;

/**
 * @author Patrick Petrovic
 *
 *         Dummy analysis for tests.
 */
public class DummyFactory extends DFAFactory {

    @Override
    public String getName() {
        return "Dummy Analysis";
    }

    @Override
    public DFADirection getDirection() {
        return DFADirection.FORWARD;
    }

    @Override
    public DataFlowAnalysis<? extends LatticeElement> getAnalysis(SimpleBlockGraph blockGraph) {
        return new DummyAnalysis(blockGraph);
    }

}
