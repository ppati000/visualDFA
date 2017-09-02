package dfa.analyses.constantfolding;

import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import codeprocessor.CodeProcessor;
import codeprocessor.GraphBuilder;
import controller.Controller;
import dfa.TestMethod;
import dfa.TestUtils;
import dfa.analyses.ConstantFoldingElement;
import dfa.analyses.ConstantFoldingInitializer;
import dfa.framework.BlockState;
import dfa.framework.SimpleBlockGraph;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.toolkits.graph.Block;

public class TestInitializer01 {

    private static TestUtils<ConstantFoldingElement.Value> tu = new TestUtils<ConstantFoldingElement.Value>();

    private static SimpleBlockGraph bgSupportedTypes;
    private static SimpleBlockGraph bgUnsupportedTypes;

    @BeforeClass
    public static void setUp() {
        Controller controller = new Controller();
        TestMethod testMethodSupportedTypes = getCodeSupportedTypes();
        CodeProcessor cp = new CodeProcessor(testMethodSupportedTypes.method, controller.getProgramOutputPath());
        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        bgSupportedTypes = gb.buildGraph(testMethodSupportedTypes.signature);

        TestMethod testMethodUnsupportedTypes = getCodeUnsupportedTypes();
        cp = new CodeProcessor(testMethodUnsupportedTypes.method, controller.getProgramOutputPath());
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

        ConstantFoldingElement.Value intZero = new ConstantFoldingElement.Value(IntConstant.v(0));
        ConstantFoldingElement.Value longZero = new ConstantFoldingElement.Value(LongConstant.v(0));
        tu.assertLocalValue(intZero, "b", initInState);
        tu.assertLocalValue(intZero, "by", initInState);
        tu.assertLocalValue(intZero, "c", initInState);
        tu.assertLocalValue(intZero, "s", initInState);
        tu.assertLocalValue(intZero, "i", initInState);
        tu.assertLocalValue(longZero, "l", initInState);

        ConstantFoldingElement.Value bottom = ConstantFoldingElement.Value.getBottom();
        tu.assertLocalValue(bottom, "b", initOutState);
        tu.assertLocalValue(bottom, "by", initOutState);
        tu.assertLocalValue(bottom, "c", initOutState);
        tu.assertLocalValue(bottom, "s", initOutState);
        tu.assertLocalValue(bottom, "i", initOutState);
        tu.assertLocalValue(bottom, "l", initOutState);
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

        Assert.assertTrue(initInState.getLocalMap().isEmpty());
        Assert.assertTrue(initOutState.getLocalMap().isEmpty());
    }

    public static TestMethod getCodeUnsupportedTypes() {
        String signature = "void test_unsupportedTypes()";
        // @formatter:off
        String method = 
                "public void test_unsupportedTypes() {"
                + "float f = 32.99F;"
                + "double d = 3.141592;"
                + "Object o = new Object();"
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }

}
