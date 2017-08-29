package dfa.analyses.constantfolding;

import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import codeprocessor.CodeProcessor;
import codeprocessor.GraphBuilder;
import dfa.TestMethod;
import dfa.analyses.ConstantFoldingElement;
import dfa.analyses.ConstantFoldingInitializer;
import dfa.framework.BlockState;
import dfa.framework.SimpleBlockGraph;
import soot.toolkits.graph.Block;

public class TestInitializer01 {
    
    
    private static SimpleBlockGraph bgSupportedTypes;
    private static SimpleBlockGraph bgUnsupportedTypes;
    
    @BeforeClass
    public static void setUp() {
        TestMethod testMethodSupportedTypes = getCodeSupportedTypes();
        CodeProcessor cp = new CodeProcessor(testMethodSupportedTypes.method);
        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        bgSupportedTypes = gb.buildGraph(testMethodSupportedTypes.signature);
        
        TestMethod testMethodUnsupportedTypes = getCodeSupportedTypes();
        cp = new CodeProcessor(testMethodUnsupportedTypes.method);
        Assert.assertTrue(cp.wasSuccessful());

        gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        bgUnsupportedTypes = gb.buildGraph(testMethodUnsupportedTypes.signature);
    }
    
    @Test
    public void testSupportedTypes() {
        Assert.assertEquals(1, bgSupportedTypes.getBlocks().size());

        Block onlyBlock = bgSupportedTypes.getBlocks().get(0);
        
        ConstantFoldingInitializer cfInit = new ConstantFoldingInitializer(bgSupportedTypes);
        Map<Block, BlockState<ConstantFoldingElement>> initMap = cfInit.getInitialStates();

        ConstantFoldingElement initInState = initMap.get(onlyBlock).getInState();
        ConstantFoldingElement initOutState = initMap.get(onlyBlock).getOutState();
        
        // TODO use the new TestUtils to check the values
    }
    
    public static TestMethod getCodeSupportedTypes() {
        String signature = "void test_supportedTypes()";
        // @formatter:off
        String method = 
                "public void test_supportedTypes() {"
                + "boolean b = false;"
                + "byte by = 12;"
                + "char c = 'd';"
                + "short s = -3342;"
                + "int i = 42;"
                + "long l = 9945723638L;"
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }
    
    @Test
    public void testUnsupportedTypes() {
        Assert.assertEquals(1, bgUnsupportedTypes.getBlocks().size());

        Block onlyBlock = bgUnsupportedTypes.getBlocks().get(0);
        
        ConstantFoldingInitializer cfInit = new ConstantFoldingInitializer(bgUnsupportedTypes);
        Map<Block, BlockState<ConstantFoldingElement>> initMap = cfInit.getInitialStates();

        ConstantFoldingElement initInState = initMap.get(onlyBlock).getInState();
        ConstantFoldingElement initOutState = initMap.get(onlyBlock).getOutState();
        
        // TODO use the new TestUtils to check the values
    }
    
    public static TestMethod getCodeUnsupportedTypes() {
        String signature = "void test_supportedTypes";
        // @formatter:off
        String method = 
                "public void test_unsupportedTypes() {"
                + "float f = 32.99;"
                + "double d = 3.141592;"
                + "String s = \"hi\";"
                + "Object o = new Object();"
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }
    

}
