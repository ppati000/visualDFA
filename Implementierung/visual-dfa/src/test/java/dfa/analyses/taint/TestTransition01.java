package dfa.analyses.taint;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import codeprocessor.CodeProcessor;
import codeprocessor.Filter;
import codeprocessor.GraphBuilder;
import codeprocessor.NoFilter;
import dfa.TestMethod;
import dfa.TestUtils;
import dfa.analyses.TaintElement;
import dfa.analyses.TaintElement.TaintState;
import dfa.analyses.TaintInitializer;
import dfa.analyses.TaintJoin;
import dfa.analyses.TaintTransition;
import dfa.framework.BlockState;
import dfa.framework.SimpleBlockGraph;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.toolkits.graph.Block;

public class TestTransition01 {

    private static TestUtils<TaintElement.Value> tu = new TestUtils<TaintElement.Value>();

    private static final TaintElement.Value VAL_BOTTOM = new TaintElement.Value(TaintState.BOTTOM, false);
    private static final TaintElement.Value VAL_CLEAN = new TaintElement.Value(TaintState.CLEAN, false);
    private static final TaintElement.Value VAL_TAINTED = new TaintElement.Value(TaintState.TAINTED, false);
    // private static final TaintElement.Value VAL_BOTTOM_V = new TaintElement.Value(TaintState.BOTTOM, true);
    // private static final TaintElement.Value VAL_CLEAN_V = new TaintElement.Value(TaintState.CLEAN, true);
    private static final TaintElement.Value VAL_TAINTED_V = new TaintElement.Value(TaintState.TAINTED, true);

    private static void addTags(SootClass c) {
        Filter filter = new NoFilter();
        List<SootMethod> methods = c.getMethods();
        for (SootMethod method : methods) {
            filter.filter(method);
        }
    }

    @Test
    public void testParamsTainted() {
        // we need to do setup here, because soot acts weird otherwise
        TestMethod testMethod = getCodeParamsTainted();
        CodeProcessor cp = new CodeProcessor(testMethod.method);
        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        SimpleBlockGraph bgParamsTainted = gb.buildGraph(testMethod.signature);

        // add the necessary tags
        addTags(bgParamsTainted.getBody().getMethod().getDeclaringClass());

        // the real test begins
        tu.setPrint(false);

        Assert.assertEquals(1, bgParamsTainted.getBlocks().size());

        Block onlyBlock = bgParamsTainted.getBlocks().get(0);

        tu.printInfo("---- this is the only block ---- ");
        List<Unit> units = tu.getUnitsFromBlock(onlyBlock);
        tu.printInfo(tu.unitsToString(units));

        TaintInitializer taintInit = new TaintInitializer(bgParamsTainted);
        Map<Block, BlockState<TaintElement>> initMap = taintInit.getInitialStates();

        TaintElement initInState = initMap.get(onlyBlock).getInState();
        TaintElement initOutState = initMap.get(onlyBlock).getOutState();

        tu.printInfo("\n" + "---- the initial in-state ----");
        tu.printInfo(initInState.getStringRepresentation());

        TaintElement currentElement = initInState;
        tu.assertLocalValue(VAL_CLEAN, "i", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "d", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "f", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "n", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "m", currentElement);

        tu.printInfo("\n" + "---- the initial out-state ----");
        tu.printInfo(initOutState.getStringRepresentation());

        currentElement = initOutState;
        tu.assertLocalValue(VAL_BOTTOM, "i", currentElement);
        tu.assertLocalValue(VAL_BOTTOM, "d", currentElement);
        tu.assertLocalValue(VAL_BOTTOM, "f", currentElement);
        tu.assertLocalValue(VAL_BOTTOM, "n", currentElement);
        tu.assertLocalValue(VAL_BOTTOM, "m", currentElement);

        TaintTransition transition = new TaintTransition();

        TaintElement te01 = transition.transition(initInState, units.get(0));
        currentElement = te01;
        tu.assertLocalValue(VAL_CLEAN, "i", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "d", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "f", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "n", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "m", currentElement);

        TaintElement te02 = transition.transition(te01, units.get(1));
        currentElement = te02;
        tu.assertLocalValue(VAL_TAINTED, "i", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "d", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "f", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "n", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "m", currentElement);

        TaintElement te03 = transition.transition(te02, units.get(2));
        currentElement = te03;
        tu.assertLocalValue(VAL_TAINTED, "i", currentElement);
        tu.assertLocalValue(VAL_TAINTED, "d", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "f", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "n", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "m", currentElement);

        TaintElement te04 = transition.transition(te03, units.get(3));
        currentElement = te04;
        tu.assertLocalValue(VAL_TAINTED, "i", currentElement);
        tu.assertLocalValue(VAL_TAINTED, "d", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "f", currentElement);
        tu.assertLocalValue(VAL_TAINTED, "n", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "m", currentElement);

        TaintElement te05 = transition.transition(te04, units.get(4));
        currentElement = te05;
        tu.assertLocalValue(VAL_TAINTED, "i", currentElement);
        tu.assertLocalValue(VAL_TAINTED, "d", currentElement);
        tu.assertLocalValue(VAL_TAINTED, "f", currentElement);
        tu.assertLocalValue(VAL_TAINTED, "n", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "m", currentElement);

        TaintElement te06 = transition.transition(te05, units.get(5));
        currentElement = te06;
        tu.assertLocalValue(VAL_CLEAN, "i", currentElement);
        tu.assertLocalValue(VAL_TAINTED, "d", currentElement);
        tu.assertLocalValue(VAL_TAINTED, "f", currentElement);
        tu.assertLocalValue(VAL_TAINTED, "n", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "m", currentElement);

        TaintElement te07 = transition.transition(te06, units.get(6));
        currentElement = te07;
        tu.assertLocalValue(VAL_CLEAN, "i", currentElement);
        tu.assertLocalValue(VAL_TAINTED, "d", currentElement);
        tu.assertLocalValue(VAL_TAINTED, "f", currentElement);
        tu.assertLocalValue(VAL_TAINTED, "n", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "m", currentElement);

        TaintElement te08 = transition.transition(te07, units.get(7));
        currentElement = te08;
        tu.assertLocalValue(VAL_CLEAN, "i", currentElement);
        tu.assertLocalValue(VAL_TAINTED_V, "d", currentElement);
        tu.assertLocalValue(VAL_TAINTED_V, "f", currentElement);
        tu.assertLocalValue(VAL_TAINTED_V, "n", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "m", currentElement);
    }

    private static TestMethod getCodeParamsTainted() {
        String signature = "void test_paramsTainted(int,double)";
        // @formatter:off
        String method = 
                "public void test_paramsTainted(int i, double d) {"
                        + "int n = i;"      
                        + "float f = (float) d;" 
                        + "__clean(i);"
                        + "int m = 2 * i;"
                        + "__sensitive();"
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }

    @Test
    public void testConstantsClean() {
        // we need to do setup here, because soot acts weird
        TestMethod testMethod = getCodeConstantsClean();
        CodeProcessor cp = new CodeProcessor(testMethod.method);
        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        SimpleBlockGraph bgConstantsClean = gb.buildGraph(testMethod.signature);

        // add the necessary tags
        addTags(bgConstantsClean.getBody().getMethod().getDeclaringClass());

        // the real test begins
        tu.setPrint(false);

        Assert.assertEquals(1, bgConstantsClean.getBlocks().size());

        Block onlyBlock = bgConstantsClean.getBlocks().get(0);

        tu.printInfo("---- this is the only block ---- ");
        List<Unit> units = tu.getUnitsFromBlock(onlyBlock);
        tu.printInfo(tu.unitsToString(units));

        TaintInitializer taintInit = new TaintInitializer(bgConstantsClean);
        Map<Block, BlockState<TaintElement>> initMap = taintInit.getInitialStates();

        TaintElement initInState = initMap.get(onlyBlock).getInState();
        TaintElement initOutState = initMap.get(onlyBlock).getOutState();

        tu.printInfo("\n" + "---- the initial in-state ----");
        tu.printInfo(initInState.getStringRepresentation());

        TaintElement currentElement = initInState;
        tu.assertLocalValue(VAL_CLEAN, "n", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "m", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "f", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "b", currentElement);

        tu.printInfo("\n" + "---- the initial out-state ----");
        tu.printInfo(initOutState.getStringRepresentation());

        currentElement = initOutState;
        tu.assertLocalValue(VAL_BOTTOM, "n", currentElement);
        tu.assertLocalValue(VAL_BOTTOM, "m", currentElement);
        tu.assertLocalValue(VAL_BOTTOM, "f", currentElement);
        tu.assertLocalValue(VAL_BOTTOM, "b", currentElement);

        TaintTransition transition = new TaintTransition();

        TaintElement te01 = transition.transition(initInState, units.get(0));
        TaintElement te02 = transition.transition(te01, units.get(1));
        TaintElement te03 = transition.transition(te02, units.get(2));
        TaintElement te04 = transition.transition(te03, units.get(3));
        TaintElement te05 = transition.transition(te04, units.get(4));
        TaintElement te06 = transition.transition(te05, units.get(5));
        TaintElement te07 = transition.transition(te06, units.get(6));

        tu.assertLocalValue(VAL_CLEAN, "n", te07);
        tu.assertLocalValue(VAL_CLEAN, "m", te07);
        tu.assertLocalValue(VAL_CLEAN, "f", te07);
        tu.assertLocalValue(VAL_CLEAN, "b", te07);
    }

    private static TestMethod getCodeConstantsClean() {
        String signature = "void test_constantsClean()";
        // @formatter:off
        String method = 
                "public void test_constantsClean() {"
                        + "int n = 0;"      
                        + "int m = 2 * n;"
                        + "float f = m;"
                        + "byte b = (byte) f;"
                        + "__sensitive();"
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }

    @Test
    public void testCallClean() {
        // we need to do setup here, because soot acts weird
        TestMethod testMethod = getCodeCallClean();
        CodeProcessor cp = new CodeProcessor(testMethod.method);
        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        SimpleBlockGraph bgCallClean = gb.buildGraph(testMethod.signature);

        // add the necessary tags
        addTags(bgCallClean.getBody().getMethod().getDeclaringClass());

        // the real test begins
        tu.setPrint(false);

        Assert.assertEquals(1, bgCallClean.getBlocks().size());

        Block onlyBlock = bgCallClean.getBlocks().get(0);

        tu.printInfo("---- this is the only block ---- ");
        List<Unit> units = tu.getUnitsFromBlock(onlyBlock);
        tu.printInfo(tu.unitsToString(units));

        TaintInitializer taintInit = new TaintInitializer(bgCallClean);
        Map<Block, BlockState<TaintElement>> initMap = taintInit.getInitialStates();

        TaintElement initInState = initMap.get(onlyBlock).getInState();
        TaintElement initOutState = initMap.get(onlyBlock).getOutState();

        tu.printInfo("\n" + "---- the initial in-state ----");
        tu.printInfo(initInState.getStringRepresentation());

        TaintElement currentElement = initInState;
        tu.assertLocalValue(VAL_CLEAN, "b", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "by", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "c", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "s", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "i", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "l", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "f", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "d", currentElement);

        tu.printInfo("\n" + "---- the initial out-state ----");
        tu.printInfo(initOutState.getStringRepresentation());

        currentElement = initOutState;
        tu.assertLocalValue(VAL_BOTTOM, "b", currentElement);
        tu.assertLocalValue(VAL_BOTTOM, "by", currentElement);
        tu.assertLocalValue(VAL_BOTTOM, "c", currentElement);
        tu.assertLocalValue(VAL_BOTTOM, "s", currentElement);
        tu.assertLocalValue(VAL_BOTTOM, "i", currentElement);
        tu.assertLocalValue(VAL_BOTTOM, "l", currentElement);
        tu.assertLocalValue(VAL_BOTTOM, "f", currentElement);
        tu.assertLocalValue(VAL_BOTTOM, "d", currentElement);

        TaintTransition transition = new TaintTransition();

        TaintElement te01 = transition.transition(initInState, units.get(0));
        TaintElement te02 = transition.transition(te01, units.get(1));
        TaintElement te03 = transition.transition(te02, units.get(2));
        TaintElement te04 = transition.transition(te03, units.get(3));
        TaintElement te05 = transition.transition(te04, units.get(4));
        TaintElement te06 = transition.transition(te05, units.get(5));
        TaintElement te07 = transition.transition(te06, units.get(6));
        TaintElement te08 = transition.transition(te07, units.get(7));
        TaintElement te09 = transition.transition(te08, units.get(8));

        tu.assertLocalValue(VAL_TAINTED, "b", te09);
        tu.assertLocalValue(VAL_TAINTED, "by", te09);
        tu.assertLocalValue(VAL_TAINTED, "c", te09);
        tu.assertLocalValue(VAL_TAINTED, "s", te09);
        tu.assertLocalValue(VAL_TAINTED, "i", te09);
        tu.assertLocalValue(VAL_TAINTED, "l", te09);
        tu.assertLocalValue(VAL_TAINTED, "f", te09);
        tu.assertLocalValue(VAL_TAINTED, "d", te09);

        TaintElement te10 = transition.transition(te09, units.get(9));
        TaintElement te11 = transition.transition(te10, units.get(10));
        TaintElement te12 = transition.transition(te11, units.get(11));
        TaintElement te13 = transition.transition(te12, units.get(12));
        TaintElement te14 = transition.transition(te13, units.get(13));
        TaintElement te15 = transition.transition(te14, units.get(14));
        TaintElement te16 = transition.transition(te15, units.get(15));
        TaintElement te17 = transition.transition(te16, units.get(16));

        tu.assertLocalValue(VAL_CLEAN, "b", te17);
        tu.assertLocalValue(VAL_CLEAN, "by", te17);
        tu.assertLocalValue(VAL_CLEAN, "c", te17);
        tu.assertLocalValue(VAL_CLEAN, "s", te17);
        tu.assertLocalValue(VAL_CLEAN, "i", te17);
        tu.assertLocalValue(VAL_CLEAN, "l", te17);
        tu.assertLocalValue(VAL_CLEAN, "f", te17);
        tu.assertLocalValue(VAL_CLEAN, "d", te17);
    }

    private static TestMethod getCodeCallClean() {
        String signature = "void test_callClean(boolean,byte,char,short,int,long,float,double)";
        // @formatter:off
        String method = 
                "public void test_callClean(boolean b, byte by, char c, short s, int i, long l, float f, double d) {"
                + "__clean(b);"
                + "__clean(by);"
                + "__clean(c);"
                + "__clean(s);"
                + "__clean(i);"
                + "__clean(l);"
                + "__clean(f);"
                + "__clean(d);"
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }

    @Test
    public void testCallTaint() {
        // we need to do setup here, because soot acts weird
        TestMethod testMethod = getCodeCallTaint();
        CodeProcessor cp = new CodeProcessor(testMethod.method);

        System.out.println(cp.getErrorMessage());

        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        SimpleBlockGraph bgCallTaint = gb.buildGraph(testMethod.signature);

        // add the necessary tags
        addTags(bgCallTaint.getBody().getMethod().getDeclaringClass());

        // the real test begins
        tu.setPrint(false);

        Assert.assertEquals(1, bgCallTaint.getBlocks().size());

        Block onlyBlock = bgCallTaint.getBlocks().get(0);

        tu.printInfo("---- this is the only block ---- ");
        List<Unit> units = tu.getUnitsFromBlock(onlyBlock);
        tu.printInfo(tu.unitsToString(units));

        TaintInitializer taintInit = new TaintInitializer(bgCallTaint);
        Map<Block, BlockState<TaintElement>> initMap = taintInit.getInitialStates();

        TaintElement initInState = initMap.get(onlyBlock).getInState();
        TaintElement initOutState = initMap.get(onlyBlock).getOutState();

        tu.printInfo("\n" + "---- the initial in-state ----");
        tu.printInfo(initInState.getStringRepresentation());

        TaintElement currentElement = initInState;
        tu.assertLocalValue(VAL_CLEAN, "b", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "by", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "c", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "s", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "i", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "l", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "f", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "d", currentElement);

        tu.printInfo("\n" + "---- the initial out-state ----");
        tu.printInfo(initOutState.getStringRepresentation());

        currentElement = initOutState;
        tu.assertLocalValue(VAL_BOTTOM, "b", currentElement);
        tu.assertLocalValue(VAL_BOTTOM, "by", currentElement);
        tu.assertLocalValue(VAL_BOTTOM, "c", currentElement);
        tu.assertLocalValue(VAL_BOTTOM, "s", currentElement);
        tu.assertLocalValue(VAL_BOTTOM, "i", currentElement);
        tu.assertLocalValue(VAL_BOTTOM, "l", currentElement);
        tu.assertLocalValue(VAL_BOTTOM, "f", currentElement);
        tu.assertLocalValue(VAL_BOTTOM, "d", currentElement);

        TaintTransition transition = new TaintTransition();

        TaintElement te01 = transition.transition(initInState, units.get(0));
        TaintElement te02 = transition.transition(te01, units.get(1));
        TaintElement te03 = transition.transition(te02, units.get(2));
        TaintElement te04 = transition.transition(te03, units.get(3));
        TaintElement te05 = transition.transition(te04, units.get(4));
        TaintElement te06 = transition.transition(te05, units.get(5));
        TaintElement te07 = transition.transition(te06, units.get(6));
        TaintElement te08 = transition.transition(te07, units.get(7));
        TaintElement te09 = transition.transition(te08, units.get(8));

        tu.assertLocalValue(VAL_CLEAN, "b", te09);
        tu.assertLocalValue(VAL_CLEAN, "by", te09);
        tu.assertLocalValue(VAL_CLEAN, "c", te09);
        tu.assertLocalValue(VAL_CLEAN, "s", te09);
        tu.assertLocalValue(VAL_CLEAN, "i", te09);
        tu.assertLocalValue(VAL_CLEAN, "l", te09);
        tu.assertLocalValue(VAL_CLEAN, "f", te09);
        tu.assertLocalValue(VAL_CLEAN, "d", te09);

        TaintElement te10 = transition.transition(te09, units.get(9));
        TaintElement te11 = transition.transition(te10, units.get(10));
        TaintElement te12 = transition.transition(te11, units.get(11));
        TaintElement te13 = transition.transition(te12, units.get(12));
        TaintElement te14 = transition.transition(te13, units.get(13));
        TaintElement te15 = transition.transition(te14, units.get(14));
        TaintElement te16 = transition.transition(te15, units.get(15));
        TaintElement te17 = transition.transition(te16, units.get(16));

        tu.assertLocalValue(VAL_TAINTED, "b", te17);
        tu.assertLocalValue(VAL_TAINTED, "by", te17);
        tu.assertLocalValue(VAL_TAINTED, "c", te17);
        tu.assertLocalValue(VAL_TAINTED, "s", te17);
        tu.assertLocalValue(VAL_TAINTED, "i", te17);
        tu.assertLocalValue(VAL_TAINTED, "l", te17);
        tu.assertLocalValue(VAL_TAINTED, "f", te17);
        tu.assertLocalValue(VAL_TAINTED, "d", te17);

        TaintElement te18 = transition.transition(te17, units.get(17));

        tu.assertLocalValue(VAL_TAINTED_V, "b", te18);
        tu.assertLocalValue(VAL_TAINTED_V, "by", te18);
        tu.assertLocalValue(VAL_TAINTED_V, "c", te18);
        tu.assertLocalValue(VAL_TAINTED_V, "s", te18);
        tu.assertLocalValue(VAL_TAINTED_V, "i", te18);
        tu.assertLocalValue(VAL_TAINTED_V, "l", te18);
        tu.assertLocalValue(VAL_TAINTED_V, "f", te18);
        tu.assertLocalValue(VAL_TAINTED_V, "d", te18);
    }

    private static TestMethod getCodeCallTaint() {
        String signature = "void test_callTaint()";
        // @formatter:off
        String method = 
                "public void test_callTaint() {"
                + "boolean b = true;"
                + "byte by = 12;"
                + "char c = 'h';"
                + "short s = 23456;"
                + "int i = -445;"
                + "long l = 6745339L;"
                + "float f = 12.25f;"
                + "double d = 3.14159265d;"
                + "__taint(b);"
                + "__taint(by);"
                + "__taint(c);"
                + "__taint(s);"
                + "__taint(i);"
                + "__taint(l);"
                + "__taint(f);"
                + "__taint(d);"
                + "__sensitive();"
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }

    @Test @Ignore // this needs the fix for the TaintJoin to work
    public void testIf() {
        // we need to do setup here, because soot acts weird
        TestMethod testMethod = getCodeIf();
        CodeProcessor cp = new CodeProcessor(testMethod.method);

        System.out.println(cp.getErrorMessage());

        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        SimpleBlockGraph bgIf = gb.buildGraph(testMethod.signature);

        // add the necessary tags
        addTags(bgIf.getBody().getMethod().getDeclaringClass());

        // the real test begins
        tu.setPrint(true);

        Assert.assertEquals(4, bgIf.getBlocks().size());

        Block startBlock = bgIf.getBlocks().get(0);

        Block ifBranch = startBlock.getSuccs().get(0);
        List<Unit> unitsIfBlock = tu.getUnitsFromBlock(ifBranch);

        Block elseBranch = startBlock.getSuccs().get(1);
        List<Unit> unitsElseBlock = tu.getUnitsFromBlock(elseBranch);

        Block endBlock = ifBranch.getSuccs().get(0);
        List<Unit> unitsEndBlock = tu.getUnitsFromBlock(endBlock);

        tu.printInfo("---- this is the start block ---- ");
        List<Unit> unitsStartBlock = tu.getUnitsFromBlock(startBlock);
        tu.printInfo(tu.unitsToString(unitsStartBlock));

        TaintInitializer taintInit = new TaintInitializer(bgIf);
        Map<Block, BlockState<TaintElement>> initMap = taintInit.getInitialStates();

        TaintElement initInStateStartBlock = initMap.get(startBlock).getInState();
        TaintElement initOutStateStartBlock = initMap.get(startBlock).getOutState();

        tu.printInfo("\n" + "---- the initial in-state ----");
        tu.printInfo(initInStateStartBlock.getStringRepresentation());

        TaintElement currentElement = initInStateStartBlock;
        tu.assertLocalValue(VAL_CLEAN, "i", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "x", currentElement);

        tu.printInfo("\n" + "---- the initial out-state ----");
        tu.printInfo(initOutStateStartBlock.getStringRepresentation());

        currentElement = initOutStateStartBlock;
        tu.assertLocalValue(VAL_BOTTOM, "i", currentElement);
        tu.assertLocalValue(VAL_BOTTOM, "x", currentElement);

        TaintTransition transition = new TaintTransition();

        TaintElement te01 = transition.transition(initInStateStartBlock, unitsStartBlock.get(0));
        TaintElement te02 = transition.transition(te01, unitsStartBlock.get(1));
        TaintElement te03 = transition.transition(te02, unitsStartBlock.get(2));
        TaintElement te04 = transition.transition(te03, unitsStartBlock.get(3));

        tu.assertLocalValue(VAL_TAINTED, "i", te04);
        tu.assertLocalValue(VAL_CLEAN, "x", te04);

        TaintElement teIf01 = transition.transition(te04, unitsIfBlock.get(0));
        TaintElement teIf02 = transition.transition(teIf01, unitsIfBlock.get(1));

        tu.assertLocalValue(VAL_TAINTED, "i", teIf02);
        tu.assertLocalValue(VAL_TAINTED, "x", teIf02);

        TaintElement teElse01 = transition.transition(te04, unitsElseBlock.get(0));
        TaintElement teElse02 = transition.transition(teElse01, unitsElseBlock.get(1));

        tu.assertLocalValue(VAL_CLEAN, "i", teElse02);
        tu.assertLocalValue(VAL_CLEAN, "x", teElse02);

        TaintJoin join = new TaintJoin();
        Set<TaintElement> toJoin = new HashSet<>();
        toJoin.add(teIf02);
        toJoin.add(teElse02);
        TaintElement joinResult = join.join(toJoin);
        
        tu.assertLocalValue(VAL_TAINTED, "i", joinResult);
        tu.assertLocalValue(VAL_TAINTED, "x", joinResult);

        TaintElement teEnd01 = transition.transition(joinResult, unitsEndBlock.get(0));
        TaintElement teEnd02 = transition.transition(teEnd01, unitsEndBlock.get(1));

        tu.assertLocalValue(VAL_TAINTED_V, "i", teEnd02);
        tu.assertLocalValue(VAL_TAINTED_V, "x", teEnd02);
    }

    private static TestMethod getCodeIf() {
        String signature = "int test_if(int)";
        // @formatter:off
        String method = 
                "public int test_if(int i) {"
                        + "int one = 1;"
                        + "int x = 0 * one;" // just to prevent narrowing
                        + "if (i < 0) {"
                        + "x = i;"
                        + "} else {"
                        + "x = 0;"
                        + "__clean(i);"
                        + "}"
                        + "__sensitive();"
                        + "return x;"      
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }

}
