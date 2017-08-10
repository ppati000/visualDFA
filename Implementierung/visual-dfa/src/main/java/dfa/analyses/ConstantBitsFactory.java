package dfa.analyses;

import dfa.framework.DFADirection;
import dfa.framework.DFAFactory;
import dfa.framework.DataFlowAnalysis;
import dfa.framework.SimpleBlockGraph;

/** 
 * @author Nils Jessen
 * 
 *         A {@code ConstantBitsAnalysis} creates {@code ConstantBitsAnalysis}-Instances from {@code SimpleBlockGraph}.
 *
 */
public class ConstantBitsFactory extends DFAFactory<ConstantBitsElement> {

    @Override
    public String getName() {
        return "Constant-Bits";
    }

    @Override
    public DFADirection getDirection() {
        return DFADirection.FORWARD;
    }

    @Override
    public DataFlowAnalysis<ConstantBitsElement> getAnalysis(SimpleBlockGraph blockGraph) {
        return new ConstantBitsAnalysis(blockGraph);
    }

}
