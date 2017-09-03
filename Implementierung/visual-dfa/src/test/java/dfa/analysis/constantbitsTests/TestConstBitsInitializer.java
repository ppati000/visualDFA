package dfa.analysis.constantbitsTests;

import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import codeprocessor.CodeProcessor;
import codeprocessor.GraphBuilder;
import controller.Controller;
import dfa.analyses.ConstantBitsElement;
import dfa.analyses.ConstantBitsElement.BitValueArray;
import dfa.analyses.ConstantBitsInitializer;
import dfa.analyses.ConstantFoldingElement;
import dfa.analyses.ConstantFoldingInitializer;
import dfa.framework.BlockState;
import dfa.framework.SimpleBlockGraph;
import dfaTests.TestMethod;
import dfaTests.TestUtils;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.toolkits.graph.Block;

public class TestConstBitsInitializer {
    
    private static TestUtils<BitValueArray> tu = new TestUtils<BitValueArray>();

    private static SimpleBlockGraph bgSupportedTypes;
    private static SimpleBlockGraph bgUnsupportedTypes;

    @BeforeClass
    public static void setUp() {
        TestMethod testMethodSupportedTypes = getCodeSupportedTypes();
        CodeProcessor cp = new CodeProcessor(testMethodSupportedTypes.method);
        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        bgSupportedTypes = gb.buildGraph(testMethodSupportedTypes.signature);

        TestMethod testMethodUnsupportedTypes = getCodeUnsupportedTypes();
        cp = new CodeProcessor(testMethodUnsupportedTypes.method);
        Assert.assertTrue(cp.wasSuccessful());

        gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        bgUnsupportedTypes = gb.buildGraph(testMethodUnsupportedTypes.signature);
    }
    
    @Test
    public void testSupportedTypes() {
        Assert.assertEquals(1, bgSupportedTypes.getBlocks().size());

        Block onlyBlock = bgSupportedTypes.getBlocks().get(0);

        ConstantBitsInitializer cfInit = new ConstantBitsInitializer(bgSupportedTypes);
        Map<Block, BlockState<ConstantBitsElement>> initMap = cfInit.getInitialStates();

        ConstantBitsElement initInState = initMap.get(onlyBlock).getInState();
        ConstantBitsElement initOutState = initMap.get(onlyBlock).getOutState();
        
        tu.assertLocalValue(new BitValueArray(IntConstant.v(0)), "b", initInState);
        tu.assertLocalValue(new BitValueArray(IntConstant.v(0)), "by", initInState);
        tu.assertLocalValue(new BitValueArray(IntConstant.v(0)), "c", initInState);
        tu.assertLocalValue(new BitValueArray(IntConstant.v(0)), "s", initInState);
        tu.assertLocalValue(new BitValueArray(IntConstant.v(0)), "i", initInState);
        tu.assertLocalValue(new BitValueArray(LongConstant.v(0)), "l", initInState);

        tu.assertLocalValue(BitValueArray.getIntBottom(), "b", initOutState);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "by", initOutState);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "c", initOutState);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "s", initOutState);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "i", initOutState);
        tu.assertLocalValue(BitValueArray.getLongBottom(), "l", initOutState);
    }
    
    public static TestMethod getCodeSupportedTypes() {
        String signature = "void test_supportedTypes()";
        // @formatter:off
        String method = 
                "public void test_supportedTypes() {"
                + "boolean b = false;"
                + "byte by = 12;"
                + "char c = 'A';"
                + "short s = -1;"
                + "int i = 63;"
                + "long l = 18L;"
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }
    
    @Test
    public void testUnsupportedTypes() {
        Assert.assertEquals(1, bgUnsupportedTypes.getBlocks().size());

        Block onlyBlock = bgUnsupportedTypes.getBlocks().get(0);

        ConstantBitsInitializer cfInit = new ConstantBitsInitializer(bgUnsupportedTypes);
        Map<Block, BlockState<ConstantBitsElement>> initMap = cfInit.getInitialStates();

        ConstantBitsElement initInState = initMap.get(onlyBlock).getInState();
        ConstantBitsElement initOutState = initMap.get(onlyBlock).getOutState();

        Assert.assertTrue(initInState.getLocalMap().isEmpty());
        Assert.assertTrue(initOutState.getLocalMap().isEmpty());
    }
    
    public static TestMethod getCodeUnsupportedTypes() {
        String signature = "void test_unsupportedTypes()";
        // @formatter:off
        String method = 
                "public void test_unsupportedTypes() {"
                + "float f = 97.22F;"
                + "double d = 3.141592;"
                + "Object o = new Object();"
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }

}
