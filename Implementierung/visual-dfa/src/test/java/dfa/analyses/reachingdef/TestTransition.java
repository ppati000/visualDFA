package dfa.analyses.reachingdef;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import codeprocessor.CodeProcessor;
import codeprocessor.GraphBuilder;
import controller.Controller;
import dfa.TestMethod;
import dfa.TestUtils;
import dfa.analyses.ReachingDefinitionsElement;
import dfa.analyses.ReachingDefinitionsElement.Definition;
import dfa.analyses.ReachingDefinitionsInitializer;
import dfa.analyses.ReachingDefinitionsJoin;
import dfa.analyses.ReachingDefinitionsTransition;
import dfa.framework.BlockState;
import dfa.framework.SimpleBlockGraph;
import soot.Unit;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.graph.Block;

public class TestTransition {
    
    private static TestUtils<Definition> tu = new TestUtils<Definition>();
   
    @Test
    public void testPrimitiveTypes() {
        TestMethod testMethodPrimitiveTypes = getCodePrimitiveTypes();
        CodeProcessor cp = new CodeProcessor(testMethodPrimitiveTypes.method);
        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        SimpleBlockGraph bgPrimitiveTypes = gb.buildGraph(testMethodPrimitiveTypes.signature);
        
        tu.setPrint(false);
        
        Assert.assertEquals(1, bgPrimitiveTypes.getBlocks().size());
        
        Block onlyBlock = bgPrimitiveTypes.getBlocks().get(0);

        tu.printInfo("---- this is the only block ---- ");
        tu.printInfo(tu.blockToString(onlyBlock));
        
        ReachingDefinitionsInitializer cfInit = new ReachingDefinitionsInitializer(bgPrimitiveTypes);
        Map<Block, BlockState<ReachingDefinitionsElement>> initMap = cfInit.getInitialStates();

        ReachingDefinitionsElement initInState = initMap.get(onlyBlock).getInState();
        ReachingDefinitionsElement initOutState = initMap.get(onlyBlock).getOutState();
        
        Set<JimpleLocal> localsIn = initInState.getLocalMap().keySet();
        Set<JimpleLocal> localsOut = initOutState.getLocalMap().keySet();
        
        Assert.assertEquals(localsIn, localsOut);
        
        for (JimpleLocal l : localsIn) {
            Assert.assertEquals(Definition.getBottom(), initInState.getValue(l));
            Assert.assertEquals(Definition.getBottom(), initOutState.getValue(l));
        }
        
        ReachingDefinitionsTransition transition = new ReachingDefinitionsTransition();
        ReachingDefinitionsElement result = transitionThroughBlock(onlyBlock, initInState, transition);
        
        Set<JimpleLocal> localsResult = result.getLocalMap().keySet();
        for (JimpleLocal l : localsResult) {
            Assert.assertTrue(result.getValue(l).isActualDefinition());
        }
        
        String expected = "b = 1\nby = 123\nc = 99\nd = 3.14159265\nf = 66.77\ni = 459853\nl = 55\ns = -4766\nthis = this";
        Assert.assertEquals(expected, result.getStringRepresentation());
    }
    
    private static TestMethod getCodePrimitiveTypes() {
        String signature = "void test_primTypes()";
        // @formatter:off
        String method = 
                "public void test_primTypes() {"
                + "boolean b = true;"
                + "byte by = 123;"
                + "short s = -4766;"
                + "char c = 'c';"
                + "int i = 459853;"
                + "long l = 55L;"
                + "float f = 66.77f;"
                + "double d = 3.14159265d;"
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }
    
    @Test
    public void testRefTypes() {
        TestMethod testMethodRefTypes = getCodeRefTypes();
        CodeProcessor cp = new CodeProcessor(testMethodRefTypes.method);
        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        SimpleBlockGraph bgRefTypes = gb.buildGraph(testMethodRefTypes.signature);
        
        tu.setPrint(false);
        
        Assert.assertEquals(1, bgRefTypes.getBlocks().size());
        
        Block onlyBlock = bgRefTypes.getBlocks().get(0);

        tu.printInfo("---- this is the only block ---- ");
        tu.printInfo(tu.blockToString(onlyBlock));
        
        ReachingDefinitionsInitializer cfInit = new ReachingDefinitionsInitializer(bgRefTypes);
        Map<Block, BlockState<ReachingDefinitionsElement>> initMap = cfInit.getInitialStates();

        ReachingDefinitionsElement initInState = initMap.get(onlyBlock).getInState();
        ReachingDefinitionsElement initOutState = initMap.get(onlyBlock).getOutState();
        
        Set<JimpleLocal> localsIn = initInState.getLocalMap().keySet();
        Set<JimpleLocal> localsOut = initOutState.getLocalMap().keySet();
        
        Assert.assertEquals(localsIn, localsOut);
        
        for (JimpleLocal l : localsIn) {
            Assert.assertEquals(Definition.getBottom(), initInState.getValue(l));
            Assert.assertEquals(Definition.getBottom(), initOutState.getValue(l));
        }
        
        ReachingDefinitionsTransition transition = new ReachingDefinitionsTransition();
        ReachingDefinitionsElement result = transitionThroughBlock(onlyBlock, initInState, transition);
        
        Set<JimpleLocal> localsResult = result.getLocalMap().keySet();
        for (JimpleLocal l : localsResult) {
            Assert.assertTrue(result.getValue(l).isActualDefinition());
        }
        
        tu.printInfo("\n" + result.getStringRepresentation());
    
    }
    private static TestMethod getCodeRefTypes() {
        String signature = "int test_refTypes(int)";
        // @formatter:off
        String method = 
                "class C {"
                + "private void use(boolean b) {};"
                + ""
                + "public int test_refTypes(int i) {"
                + "String str = null;"
                + "Object o = new Object();"
                + "int[] intArray = new int[12];"
                + "Object[] objectArray = new Object[4 * i];"
                + "Object[][] array2d = new Object[3][i];"
                + "Object[][][] array3d = new Object[i][2][4];"
                + "boolean b = o instanceof String;"
                + "use(b);"
                + "return intArray.length;"
                + "}"
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }
    
    @Test
    public void testSimpleStatements() {
        TestMethod testMethodSimpleStatements = getCodeSimpleStatements();
        CodeProcessor cp = new CodeProcessor(testMethodSimpleStatements.method);
        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        SimpleBlockGraph bgSimpleStatements = gb.buildGraph(testMethodSimpleStatements.signature);
        
        tu.setPrint(false);
        
        Assert.assertEquals(1, bgSimpleStatements.getBlocks().size());
        
        Block onlyBlock = bgSimpleStatements.getBlocks().get(0);

        tu.printInfo("---- this is the only block ---- ");
        List<Unit> units = tu.getUnitsFromBlock(onlyBlock);
        tu.printInfo(tu.unitsToString(units));
        
        ReachingDefinitionsInitializer cfInit = new ReachingDefinitionsInitializer(bgSimpleStatements);
        Map<Block, BlockState<ReachingDefinitionsElement>> initMap = cfInit.getInitialStates();

        ReachingDefinitionsElement initInState = initMap.get(onlyBlock).getInState();
        ReachingDefinitionsElement initOutState = initMap.get(onlyBlock).getOutState();
        
        Set<JimpleLocal> localsIn = initInState.getLocalMap().keySet();
        Set<JimpleLocal> localsOut = initOutState.getLocalMap().keySet();
        
        Assert.assertEquals(localsIn, localsOut);

        for (JimpleLocal l : localsIn) {
            Assert.assertEquals(Definition.getBottom(), initInState.getValue(l));
            Assert.assertEquals(Definition.getBottom(), initOutState.getValue(l));
        }
        
        ReachingDefinitionsTransition transition = new ReachingDefinitionsTransition();
        ReachingDefinitionsElement result = transitionThroughBlock(onlyBlock, initInState, transition);
        
        Set<JimpleLocal> localsResult = result.getLocalMap().keySet();
        for (JimpleLocal l : localsResult) {
            Assert.assertTrue(result.getValue(l).isActualDefinition());
        }
        
        tu.printInfo("\n" + result.getStringRepresentation());
    }
    
    private static TestMethod getCodeSimpleStatements() {
        String signature = "void test_simpleStatements(int)";
        // @formatter:off
        String method = 
                "public void test_simpleStatements(int i) {"
                + "boolean b = true;"
                + "long x = i % 4L;"
                + "long l = 3 * i;"
                + "double d = l * 3.14159265d + x;"
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }
    
    @Test
    public void testCalls() {
        TestMethod testMethodCalls = getCodeCall();
        CodeProcessor cp = new CodeProcessor(testMethodCalls.method);
        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        SimpleBlockGraph bgCalls = gb.buildGraph(testMethodCalls.signature);
        
        tu.setPrint(false);
        
        Assert.assertEquals(1, bgCalls.getBlocks().size());
        
        Block onlyBlock = bgCalls.getBlocks().get(0);

        tu.printInfo("---- this is the only block ---- ");
        List<Unit> units = tu.getUnitsFromBlock(onlyBlock);
        tu.printInfo(tu.unitsToString(units));
        
        ReachingDefinitionsInitializer cfInit = new ReachingDefinitionsInitializer(bgCalls);
        Map<Block, BlockState<ReachingDefinitionsElement>> initMap = cfInit.getInitialStates();

        ReachingDefinitionsElement initInState = initMap.get(onlyBlock).getInState();
        ReachingDefinitionsElement initOutState = initMap.get(onlyBlock).getOutState();
        
        Set<JimpleLocal> localsIn = initInState.getLocalMap().keySet();
        Set<JimpleLocal> localsOut = initOutState.getLocalMap().keySet();
        
        Assert.assertEquals(localsIn, localsOut);
        
        for (JimpleLocal l : localsIn) {
            Assert.assertEquals(Definition.getBottom(), initInState.getValue(l));
            Assert.assertEquals(Definition.getBottom(), initOutState.getValue(l));
        }
        
        ReachingDefinitionsTransition transition = new ReachingDefinitionsTransition();
        ReachingDefinitionsElement result = transitionThroughBlock(onlyBlock, initInState, transition);

        tu.printInfo("\n" + result.getStringRepresentation());
    }
    
    private static TestMethod getCodeCall() {
        String signature = "void test_call(int)";
        // @formatter:off
        String method =
                "public class C implements Comparable<C> {"
                + "private int f() {return 0;}"
                + "public int compareTo(C c) {return 0;}"
                + ""
                + "public void test_call(int i) {"
                + "int min = Math.min(i, 0);"
                + "Object o = new Object();"
                + "String s = new String();"
                + "boolean b = o.equals(s);"
                + "int a = f();"
                + "C c = new C();"
                + "int cmp = this.compareTo(c);"
                + "}"
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }
    
    @Test
    public void testArithmeticStmts() {
        TestMethod testMethodArithmeticStmts = getCodeArithmeticStatements();
        CodeProcessor cp = new CodeProcessor(testMethodArithmeticStmts.method);
        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        SimpleBlockGraph bgArithmetic = gb.buildGraph(testMethodArithmeticStmts.signature);
        
        tu.setPrint(false);
        
        Assert.assertEquals(1, bgArithmetic.getBlocks().size());
        
        Block onlyBlock = bgArithmetic.getBlocks().get(0);

        tu.printInfo("---- this is the only block ---- ");
        List<Unit> units = tu.getUnitsFromBlock(onlyBlock);
        tu.printInfo(tu.unitsToString(units));
        
        ReachingDefinitionsInitializer cfInit = new ReachingDefinitionsInitializer(bgArithmetic);
        Map<Block, BlockState<ReachingDefinitionsElement>> initMap = cfInit.getInitialStates();

        ReachingDefinitionsElement initInState = initMap.get(onlyBlock).getInState();
        ReachingDefinitionsElement initOutState = initMap.get(onlyBlock).getOutState();
        
        Set<JimpleLocal> localsIn = initInState.getLocalMap().keySet();
        Set<JimpleLocal> localsOut = initOutState.getLocalMap().keySet();
        
        Assert.assertEquals(localsIn, localsOut);
        
        for (JimpleLocal l : localsIn) {
            Assert.assertEquals(Definition.getBottom(), initInState.getValue(l));
            Assert.assertEquals(Definition.getBottom(), initOutState.getValue(l));
        }
        
        ReachingDefinitionsTransition transition = new ReachingDefinitionsTransition();
        ReachingDefinitionsElement result = transitionThroughBlock(onlyBlock, initInState, transition);

        tu.printInfo("\n" + result.getStringRepresentation());
    }
    
    private static TestMethod getCodeArithmeticStatements() {
        String signature = "void test_arithmeticStmts(int)";
        // @formatter:off
        String method =
                "public void test_arithmeticStmts(int i) {"
                + "int sum = 4 + i;"
                + "int diff = sum - i;"
                + "long mul = 2 * i;"
                + "int div = 5 / i;"
                + "long mod = i % mul;"
                + "int and = 0x74C & i;"
                + "int or = and | i;"
                + "int xor = 0xFFFF ^ i;"
                + "int not = ~i;"
                + "int lsh = i << 5;"
                + "int rsh = i >> 3;"
                + "int ursh = i >>> 4;"
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }
    
    
    private ReachingDefinitionsElement transitionThroughBlock(Block block, ReachingDefinitionsElement in, ReachingDefinitionsTransition transition) {
        List<Unit> units = tu.getUnitsFromBlock(block);
        ReachingDefinitionsElement currentElement = in;
        for (Unit u : units) {
            currentElement = transition.transition(currentElement, u);
        }

        return currentElement;
    }
}
