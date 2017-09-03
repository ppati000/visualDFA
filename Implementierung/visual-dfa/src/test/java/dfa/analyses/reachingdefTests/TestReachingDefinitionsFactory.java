package dfa.analyses.reachingdefTests;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import codeprocessor.CodeProcessor;
import codeprocessor.GraphBuilder;
import controller.Controller;
import dfa.analyses.ReachingDefinitionsElement;
import dfa.analyses.ReachingDefinitionsFactory;
import dfa.framework.DFADirection;
import dfa.framework.DataFlowAnalysis;
import dfa.framework.SimpleBlockGraph;
import dfaTests.TestMethod;

public class TestReachingDefinitionsFactory {
    
    private static ReachingDefinitionsFactory factory;

    @BeforeClass
    public static void setUp() {
        factory = new ReachingDefinitionsFactory();
    }
    
    @Test
    public void basicTests() {
        Assert.assertEquals(DFADirection.FORWARD, factory.getDirection());
        Assert.assertEquals("Reaching-Definitions", factory.getName());
    }
    
    @Test
    public void testGetAnalysis() {
        // we need to do setup here, because soot acts weird
        TestMethod testMethod = getSomeCode();
        CodeProcessor cp = new CodeProcessor(testMethod.method);

        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        SimpleBlockGraph bg = gb.buildGraph(testMethod.signature);

        DataFlowAnalysis<ReachingDefinitionsElement> analysis = factory.getAnalysis(bg);
        Assert.assertTrue(analysis instanceof DataFlowAnalysis);
    }
    
    private static TestMethod getSomeCode() {
        String signature = "void f()";
        // @formatter:off
        String method = 
                "public void f() {"
                + "long n = -1465;;"
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }

}
