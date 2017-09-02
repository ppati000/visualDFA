package dfa.analyses.taint;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import codeprocessor.CodeProcessor;
import codeprocessor.GraphBuilder;
import controller.Controller;
import dfa.TestMethod;
import dfa.analyses.TaintElement;
import dfa.analyses.TaintFactory;
import dfa.framework.DFADirection;
import dfa.framework.DataFlowAnalysis;
import dfa.framework.SimpleBlockGraph;

public class TestTaintFactory {

    private static TaintFactory factory;

    @BeforeClass
    public static void setUp() {
        factory = new TaintFactory();
    }

    @Test
    public void basicTests() {
        Assert.assertEquals(DFADirection.FORWARD, factory.getDirection());
        Assert.assertEquals("Taint-Analysis", factory.getName());
    }
    
    @Test
    public void testGetAnalysis() {
        // we need to do setup here, because soot acts weird
        TestMethod testMethod = getSomeCode();
        CodeProcessor cp = new CodeProcessor(testMethod.method);

        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        SimpleBlockGraph bg = gb.buildGraph(testMethod.signature);

        DataFlowAnalysis<TaintElement> analysis = factory.getAnalysis(bg);
        Assert.assertTrue(analysis instanceof DataFlowAnalysis);
    }
    
    
    private static TestMethod getSomeCode() {
        String signature = "void f()";
        // @formatter:off
        String method = 
                "public void f() {"
                + "int x = 0;"
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }

}
