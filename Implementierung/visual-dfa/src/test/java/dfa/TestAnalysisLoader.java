package dfa;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import dfa.analyses.ConstantBitsFactory;
import dfa.analyses.ConstantFoldingFactory;
import dfa.analyses.ReachingDefinitionsFactory;
import dfa.analyses.TaintFactory;
import dfa.framework.AnalysisLoader;
import dfa.framework.DFAFactory;
import dfa.framework.LatticeElement;

public class TestAnalysisLoader {
    
    private static File classesDir;
    
    @BeforeClass
    public static void setUp() {
        classesDir = new File("src/test/resources");
    }
    
    @Test @Ignore
    public void test01() {
        AnalysisLoader loader = new AnalysisLoader(classesDir.getAbsolutePath());
        
        loader.loadAnalyses(null);
        
        List<String> analysisNames = loader.getAnalysesNames();
        
        Assert.assertTrue(analysisNames.contains("Constant-Folding"));
        Assert.assertTrue(analysisNames.contains("Constant-Bits"));
        Assert.assertTrue(analysisNames.contains("Reaching-Definitions"));
        Assert.assertTrue(analysisNames.contains("Taint-Analysis"));

        DFAFactory<? extends LatticeElement> constantFoldingFactory = loader.getDFAFactory("Constant-Folding");
        Assert.assertTrue(constantFoldingFactory instanceof ConstantFoldingFactory);
        
        DFAFactory<? extends LatticeElement> constantBitsFactory = loader.getDFAFactory("Constant-Bits");
        Assert.assertTrue(constantBitsFactory instanceof ConstantBitsFactory);
        
        DFAFactory<? extends LatticeElement> reachingDefFactory = loader.getDFAFactory("Reaching-Definitions");
        Assert.assertTrue(reachingDefFactory instanceof ReachingDefinitionsFactory);
        
        DFAFactory<? extends LatticeElement> taintFactory = loader.getDFAFactory("Taint-Analysis");
        Assert.assertTrue(taintFactory instanceof TaintFactory);
    }
    

}
