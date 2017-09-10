package dfa.analyses;

import dfa.framework.DFADirection;
import dfa.framework.DFAFactory;
import dfa.framework.DataFlowAnalysis;
import dfa.framework.SimpleBlockGraph;

/**
 * A {@code TaintFactory} creates {@code TaintAnalysis}-Instances from
 * {@code SimpleBlockGraph}. 
 * 
 * @author Sebastian Rauch
 */
public class TaintFactory extends DFAFactory<TaintElement> {

	@Override
	public String getName() {
		return "Taint-Analysis";
	}

	@Override
	public DFADirection getDirection() {
		return DFADirection.FORWARD;
	}

	@Override
	public DataFlowAnalysis<TaintElement> getAnalysis(SimpleBlockGraph blockGraph) {
		return new TaintAnalysis(blockGraph);
	}

}
