package dfa.framework;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import dfa.analyses.ConstantBitsFactory;
import dfa.analyses.ConstantFoldingFactory;
import dfa.analyses.ReachingDefinitionsFactory;
import dfa.analyses.TaintFactory;
import dfa.analyses.testanalyses.DummyFactory;
import dfa.analyses.testanalyses.DummyFactoryBackwards;
import dfa.analyses.testanalyses.SlowDummyFactory;

/**
 * A {@code StaticAnalysiLoader} is a {@code AnalysisLoader} that loads the analyses statically. This facilitates
 * development and testing because with the dynamic version the class-files to load must be always kept in sync with
 * the current code. In the final release of the program, the analyses most likely won't be included in the jar-file but
 * loaded dynamically, what removes the need for synchronization.
 * 
 * @author Sebastian Rauch
 */
public class StaticAnalysisLoader extends AnalysisLoader {

    public StaticAnalysisLoader(String searchPath) {
        super(searchPath);
    }

    /**
     * This loads the analyses in a static way to facilitate development and testing.
     */
    @Override
    public void loadAnalyses(Logger logger) {
        Map<String, DFAFactory<? extends LatticeElement>> factoryMap =
                new HashMap<String, DFAFactory<? extends LatticeElement>>();

        ConstantFoldingFactory cfFactory = new ConstantFoldingFactory();
        factoryMap.put(cfFactory.getName(), cfFactory);

        ConstantBitsFactory cbFactory = new ConstantBitsFactory();
        factoryMap.put(cbFactory.getName(), cbFactory);

        ReachingDefinitionsFactory rdFactory = new ReachingDefinitionsFactory();
        factoryMap.put(rdFactory.getName(), rdFactory);

        TaintFactory tFactory = new TaintFactory();
        factoryMap.put(tFactory.getName(), tFactory);

        DummyFactory dummyFactory = new DummyFactory();
        factoryMap.put(dummyFactory.getName(), dummyFactory);
        
        DummyFactoryBackwards dummyFactoryBackwards = new DummyFactoryBackwards();
        factoryMap.put(dummyFactoryBackwards.getName(), dummyFactoryBackwards);

        SlowDummyFactory slowDummyFactory = new SlowDummyFactory();
        factoryMap.put(slowDummyFactory.getName(), slowDummyFactory);

        setAnalyses(factoryMap);
    }

}
