package dfa.analyses.reachingdefTests;

import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import codeprocessor.CodeProcessor;
import codeprocessor.GraphBuilder;
import dfa.analyses.ReachingDefinitionsElement;
import dfa.analyses.ReachingDefinitionsElement.DefinitionSet;
import dfa.analyses.ReachingDefinitionsInitializer;
import dfa.framework.BlockState;
import dfa.framework.SimpleBlockGraph;
import dfaTests.TestMethod;
import dfaTests.TestUtils;
import soot.toolkits.graph.Block;

public class TestReachingDefinitionsInitializer {
    
    private static TestUtils<ReachingDefinitionsElement.DefinitionSet> tu = new TestUtils<ReachingDefinitionsElement.DefinitionSet>();
    
    private static SimpleBlockGraph bgPrimitiveTypes;
    private static SimpleBlockGraph bgRefTypes;
    
    @BeforeClass
    public static void setUp() {
        TestMethod testMethodPrimitiveTypes = getCodePrimitiveTypes();
        CodeProcessor cp = new CodeProcessor(testMethodPrimitiveTypes.method);
        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        bgPrimitiveTypes = gb.buildGraph(testMethodPrimitiveTypes.signature);
        
        TestMethod testMethodRefTypes = getCodeRefTypes();
        cp = new CodeProcessor(testMethodRefTypes.method);
        System.out.println(cp.getErrorMessage());
        Assert.assertTrue(cp.wasSuccessful());

        gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        bgRefTypes = gb.buildGraph(testMethodRefTypes.signature);
    }
    
    @Test
    public void testPrimitiveTypes() {
        Assert.assertEquals(1, bgPrimitiveTypes.getBlocks().size());

        Block onlyBlock = bgPrimitiveTypes.getBlocks().get(0);
        
        ReachingDefinitionsInitializer rfInit = new ReachingDefinitionsInitializer(bgPrimitiveTypes);
        Map<Block, BlockState<ReachingDefinitionsElement>> initMap = rfInit.getInitialStates();

        ReachingDefinitionsElement initInState = initMap.get(onlyBlock).getInState();
        ReachingDefinitionsElement initOutState = initMap.get(onlyBlock).getOutState();
        
        DefinitionSet bottom = ReachingDefinitionsElement.DefinitionSet.getBottom();
        tu.assertLocalValue(bottom, "b", initInState);
        tu.assertLocalValue(bottom, "by", initInState);
        tu.assertLocalValue(bottom, "c", initInState);
        tu.assertLocalValue(bottom, "s", initInState);
        tu.assertLocalValue(bottom, "i", initInState);
        tu.assertLocalValue(bottom, "l", initInState);
        
        tu.assertLocalValue(bottom, "b", initOutState);
        tu.assertLocalValue(bottom, "by", initOutState);
        tu.assertLocalValue(bottom, "c", initOutState);
        tu.assertLocalValue(bottom, "s", initOutState);
        tu.assertLocalValue(bottom, "i", initOutState);
        tu.assertLocalValue(bottom, "l", initOutState);
    }
    
    public static TestMethod getCodePrimitiveTypes() {
        String signature = "void test_primitiveTypes()";
        // @formatter:off
        String method = 
                "public void test_primitiveTypes() {"
                + "boolean b = true;"
                + "byte by = -123;"
                + "char c = 'g';"
                + "short s = 0;"
                + "int i = -56;"
                + "long l = 9635668L;"
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }
    
    @Test
    public void testRefTypes() {
        Assert.assertEquals(1, bgRefTypes.getBlocks().size());

        Block onlyBlock = bgRefTypes.getBlocks().get(0);
        
        ReachingDefinitionsInitializer rfInit = new ReachingDefinitionsInitializer(bgRefTypes);
        Map<Block, BlockState<ReachingDefinitionsElement>> initMap = rfInit.getInitialStates();

        ReachingDefinitionsElement initInState = initMap.get(onlyBlock).getInState();
        ReachingDefinitionsElement initOutState = initMap.get(onlyBlock).getOutState();
        
        DefinitionSet bottom = ReachingDefinitionsElement.DefinitionSet.getBottom();
        tu.assertLocalValue(bottom, "f", initInState);
        tu.assertLocalValue(bottom, "d", initInState);
        tu.assertLocalValue(bottom, "s", initInState);
        tu.assertLocalValue(bottom, "o", initInState);
        
        tu.assertLocalValue(bottom, "f", initOutState);
        tu.assertLocalValue(bottom, "d", initOutState);
        tu.assertLocalValue(bottom, "s", initOutState);
        tu.assertLocalValue(bottom, "o", initOutState);
    }
    
    public static TestMethod getCodeRefTypes() {
        String signature = "void test_refTypes()";
        // @formatter:off
        String method = 
                "public void test_refTypes() {"
                + "float f = 3.99F;"
                + "double d = 3.141592;"
                + "Object o = new Object();"
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }

}
