package dfa.analyses.taint;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
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

    @Test
    public void testIf() {
        // we need to do setup here, because soot acts weird
        TestMethod testMethod = getCodeIf();
        CodeProcessor cp = new CodeProcessor(testMethod.method);

        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        SimpleBlockGraph bgIf = gb.buildGraph(testMethod.signature);

        // add the necessary tags
        addTags(bgIf.getBody().getMethod().getDeclaringClass());

        // the real test begins
        tu.setPrint(false);

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
        TaintElement joinResult = join(join, teIf02, teElse02);

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

    @Test
    public void testSwitch() {
        // we need to do setup here, because soot acts weird
        TestMethod testMethod = getCodeSwitch();
        CodeProcessor cp = new CodeProcessor(testMethod.method);

        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        SimpleBlockGraph bgSwitch = gb.buildGraph(testMethod.signature);

        // add the necessary tags
        addTags(bgSwitch.getBody().getMethod().getDeclaringClass());

        // the real test begins
        tu.setPrint(false);

        Assert.assertEquals(5, bgSwitch.getBlocks().size());

        Block startBlock = bgSwitch.getHeads().get(0);
        Block endBlock = bgSwitch.getTails().get(0);

        Block case0Block = bgSwitch.getSuccsOf(startBlock).get(0);
        Block case1Block = bgSwitch.getSuccsOf(startBlock).get(1);
        Block caseDefaultBlock = bgSwitch.getSuccsOf(startBlock).get(2);

        TaintInitializer taintInit = new TaintInitializer(bgSwitch);
        Map<Block, BlockState<TaintElement>> initMap = taintInit.getInitialStates();

        TaintTransition transition = new TaintTransition();

        TaintElement startBlockIn = initMap.get(startBlock).getInState();
        tu.assertLocalValue(VAL_CLEAN, "i", startBlockIn);
        tu.assertLocalValue(VAL_CLEAN, "l", startBlockIn);
        tu.assertLocalValue(VAL_CLEAN, "n", startBlockIn);
        tu.assertLocalValue(VAL_CLEAN, "m", startBlockIn);

        TaintElement startBlockOut = initMap.get(startBlock).getOutState();
        tu.assertLocalValue(VAL_BOTTOM, "i", startBlockOut);
        tu.assertLocalValue(VAL_BOTTOM, "l", startBlockOut);
        tu.assertLocalValue(VAL_BOTTOM, "n", startBlockOut);
        tu.assertLocalValue(VAL_BOTTOM, "m", startBlockOut);

        List<Unit> unitsStartBlock = tu.getUnitsFromBlock(startBlock);
        TaintElement te01 = transition.transition(startBlockIn, unitsStartBlock.get(0));
        TaintElement te02 = transition.transition(te01, unitsStartBlock.get(1));
        TaintElement te03 = transition.transition(te02, unitsStartBlock.get(2));
        TaintElement te04 = transition.transition(te03, unitsStartBlock.get(3));
        TaintElement te05 = transition.transition(te04, unitsStartBlock.get(4));
        TaintElement te06 = transition.transition(te05, unitsStartBlock.get(5));

        TaintElement currentElement = te06;
        tu.assertLocalValue(VAL_TAINTED, "i", currentElement);
        tu.assertLocalValue(VAL_CLEAN, "l", currentElement);
        tu.assertLocalValue(VAL_TAINTED, "n", currentElement);
        tu.assertLocalValue(VAL_TAINTED, "m", currentElement);

        List<Unit> unitsCase0 = tu.getUnitsFromBlock(case0Block);
        TaintElement case0In = te06;
        TaintElement teCase0_01 = transition.transition(case0In, unitsCase0.get(0));
        TaintElement teCase0_02 = transition.transition(teCase0_01, unitsCase0.get(1));
        TaintElement teCase0_03 = transition.transition(teCase0_02, unitsCase0.get(2));
        TaintElement teCase0_out = transition.transition(teCase0_03, unitsCase0.get(3));

        tu.assertLocalValue(VAL_TAINTED, "i", teCase0_out);
        tu.assertLocalValue(VAL_TAINTED, "l", teCase0_out);
        tu.assertLocalValue(VAL_TAINTED, "n", teCase0_out);
        tu.assertLocalValue(VAL_CLEAN, "m", teCase0_out);

        List<Unit> unitsCase1 = tu.getUnitsFromBlock(case1Block);
        TaintElement case1In = te06;
        TaintElement teCase1_01 = transition.transition(case1In, unitsCase1.get(0));
        TaintElement teCase1_02 = transition.transition(teCase1_01, unitsCase1.get(1));
        TaintElement teCase1_out = transition.transition(teCase1_02, unitsCase1.get(2));

        tu.assertLocalValue(VAL_TAINTED, "i", teCase1_out);
        tu.assertLocalValue(VAL_CLEAN, "l", teCase1_out);
        tu.assertLocalValue(VAL_TAINTED, "n", teCase1_out);
        tu.assertLocalValue(VAL_CLEAN, "m", teCase1_out);

        List<Unit> unitsCaseDefault = tu.getUnitsFromBlock(caseDefaultBlock);
        TaintElement caseDefaultIn = te06;
        TaintElement teCaseDefault_out = transition.transition(caseDefaultIn, unitsCaseDefault.get(0));

        tu.assertLocalValue(VAL_TAINTED, "i", teCaseDefault_out);
        tu.assertLocalValue(VAL_CLEAN, "l", teCaseDefault_out);
        tu.assertLocalValue(VAL_TAINTED, "n", teCaseDefault_out);
        tu.assertLocalValue(VAL_CLEAN, "m", teCaseDefault_out);

        TaintJoin join = new TaintJoin();
        TaintElement joinResult = join(join, teCase0_out, teCase1_out, teCaseDefault_out);

        tu.assertLocalValue(VAL_TAINTED, "i", joinResult);
        tu.assertLocalValue(VAL_TAINTED, "l", joinResult);
        tu.assertLocalValue(VAL_TAINTED, "n", joinResult);
        tu.assertLocalValue(VAL_CLEAN, "m", joinResult);

        List<Unit> unitsEndBlock = tu.getUnitsFromBlock(endBlock);
        TaintElement teEnd01 = transition.transition(joinResult, unitsEndBlock.get(0));
        TaintElement teEnd02 = transition.transition(teEnd01, unitsEndBlock.get(1));

        tu.assertLocalValue(VAL_TAINTED_V, "i", teEnd02);
        tu.assertLocalValue(VAL_TAINTED_V, "l", teEnd02);
        tu.assertLocalValue(VAL_TAINTED_V, "n", teEnd02);
        tu.assertLocalValue(VAL_CLEAN, "m", teEnd02);
    }

    private static TestMethod getCodeSwitch() {
        String signature = "int test_switch(int)";
        // @formatter:off
        String method = 
                "private int test_switch(int i) {"
                    + "long l = 12;"
                    + "int n = i;"
                    + "int m = i - 1;"
                    + "switch(i) {"
                    + "case 0:"
                    + "l = i * i;"
                    + "m = 0;"
                    + "break;"
                    + "case 1:"
                    + "l = 0;"
                    + "m = 1;"
                    + "break;"
                    + "default:"
                    + "__clean(m);"
                    + "break;"
                    + "}"
                    + "__sensitive();"
                    + "return m;"
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }

    @Test
    public void testSimpleOpsTainted() {
        // we need to do setup here, because soot acts weird
        TestMethod testMethod = getCodeSimpleOpsTaint();
        CodeProcessor cp = new CodeProcessor(testMethod.method);

        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        SimpleBlockGraph bgSimpleOpsTainted = gb.buildGraph(testMethod.signature);

        // add the necessary tags
        addTags(bgSimpleOpsTainted.getBody().getMethod().getDeclaringClass());

        // the real test begins
        tu.setPrint(true);

        Assert.assertEquals(1, bgSimpleOpsTainted.getBlocks().size());

        Block onlyBlock = bgSimpleOpsTainted.getBlocks().get(0);

        TaintInitializer taintInit = new TaintInitializer(bgSimpleOpsTainted);
        Map<Block, BlockState<TaintElement>> initMap = taintInit.getInitialStates();

        TaintElement startIn = initMap.get(onlyBlock).getInState();

        TaintTransition transition = new TaintTransition();

        TaintElement out = transitionThroughBlock(onlyBlock, startIn, transition);

        tu.assertLocalValue(VAL_TAINTED, "s", out);
        tu.assertLocalValue(VAL_CLEAN, "l", out);

        tu.assertLocalValue(VAL_TAINTED, "neg", out);

        tu.assertLocalValue(VAL_TAINTED, "sum", out);
        tu.assertLocalValue(VAL_TAINTED, "dif", out);
        tu.assertLocalValue(VAL_TAINTED, "mul", out);
        tu.assertLocalValue(VAL_TAINTED, "div", out);
        tu.assertLocalValue(VAL_TAINTED, "rem", out);

        tu.assertLocalValue(VAL_TAINTED, "shl", out);
        tu.assertLocalValue(VAL_TAINTED, "shr", out);
        tu.assertLocalValue(VAL_TAINTED, "ushr", out);

        tu.assertLocalValue(VAL_TAINTED, "or", out);
        tu.assertLocalValue(VAL_TAINTED, "and", out);
        tu.assertLocalValue(VAL_TAINTED, "xor", out);
        tu.assertLocalValue(VAL_TAINTED, "complement", out);

        tu.assertLocalValue(VAL_TAINTED, "cast", out);
    }

    private static TestMethod getCodeSimpleOpsTaint() {
        String signature = "void test_simpleOpsTaint(short)";
        // @formatter:off
        String method = 
                "private void test_simpleOpsTaint(short s) {"
                    + "long l = 123456789;"
                    + "long neg = -s;"
                    + "long sum = l + s;"
                    + "long dif = s - l;"
                    + "long mul = l * s;"
                    + "long div = s / l;"
                    + "long rem = s % l;"
                    + "long shl = s << l;"
                    + "long shr = l >> s;"
                    + "long ushr = s >>> l;"
                    + "long or = s | l;"
                    + "long and = l & s;"
                    + "long xor = l ^ s;"
                    + "long complements = ~s;"
                    + "byte cast = (byte) s;"
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }

    @Test
    public void testSimpleOpsClean() {
        // we need to do setup here, because soot acts weird
        TestMethod testMethod = getCodeSimpleOpsClean();
        CodeProcessor cp = new CodeProcessor(testMethod.method);

        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        SimpleBlockGraph bgSimpleOpsClean = gb.buildGraph(testMethod.signature);

        // add the necessary tags
        addTags(bgSimpleOpsClean.getBody().getMethod().getDeclaringClass());

        // the real test begins
        tu.setPrint(false);

        Assert.assertEquals(1, bgSimpleOpsClean.getBlocks().size());

        Block onlyBlock = bgSimpleOpsClean.getBlocks().get(0);

        TaintInitializer taintInit = new TaintInitializer(bgSimpleOpsClean);
        Map<Block, BlockState<TaintElement>> initMap = taintInit.getInitialStates();

        TaintElement startIn = initMap.get(onlyBlock).getInState();

        TaintTransition transition = new TaintTransition();

        TaintElement out = transitionThroughBlock(onlyBlock, startIn, transition);

        tu.assertLocalValue(VAL_CLEAN, "s", out);
        tu.assertLocalValue(VAL_CLEAN, "l", out);

        tu.assertLocalValue(VAL_CLEAN, "neg", out);

        tu.assertLocalValue(VAL_CLEAN, "sum", out);
        tu.assertLocalValue(VAL_CLEAN, "dif", out);
        tu.assertLocalValue(VAL_CLEAN, "mul", out);
        tu.assertLocalValue(VAL_CLEAN, "div", out);
        tu.assertLocalValue(VAL_CLEAN, "rem", out);

        tu.assertLocalValue(VAL_CLEAN, "shl", out);
        tu.assertLocalValue(VAL_CLEAN, "shr", out);
        tu.assertLocalValue(VAL_CLEAN, "ushr", out);

        tu.assertLocalValue(VAL_CLEAN, "or", out);
        tu.assertLocalValue(VAL_CLEAN, "and", out);
        tu.assertLocalValue(VAL_CLEAN, "xor", out);
        tu.assertLocalValue(VAL_CLEAN, "complement", out);

        tu.assertLocalValue(VAL_CLEAN, "cast", out);
    }

    private static TestMethod getCodeSimpleOpsClean() {
        String signature = "void test_simpleOpsCleans()";
        // @formatter:off
        String method = 
                "private void test_simpleOpsCleans() {"
                    + "long l = 123456789;"
                    + "short s = 1476;"
                    + "long neg = -s;"
                    + "final long sum = l + s;"
                    + "long dif = s - l;"
                    + "long mul = l * s;"
                    + "long div = s / l;"
                    + "long rem = s % l;"
                    + "long shl = s << l;"
                    + "long shr = l >> s;"
                    + "long ushr = s >>> l;"
                    + "long or = s | l;"
                    + "long and = l & s;"
                    + "long xor = l ^ s;"
                    + "long complements = ~s;"
                    + "byte cast = (byte) s;"
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }

    @Test
    public void testIntEq() {
        // we need to do setup here, because soot acts weird
        TestMethod testMethod = getCodeIntEq();
        CodeProcessor cp = new CodeProcessor(testMethod.method);

        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        SimpleBlockGraph bgIntEq = gb.buildGraph(testMethod.signature);

        // add the necessary tags
        addTags(bgIntEq.getBody().getMethod().getDeclaringClass());

        // the real test begins
        tu.setPrint(false);

        Block startBlock = bgIntEq.getHeads().get(0);

        Block branchEq = bgIntEq.getSuccsOf(startBlock).get(0);
        Block branchNeq = bgIntEq.getSuccsOf(startBlock).get(1);

        Block endBlock = bgIntEq.getTails().get(0);

        TaintInitializer taintInit = new TaintInitializer(bgIntEq);
        Map<Block, BlockState<TaintElement>> initMap = taintInit.getInitialStates();

        TaintTransition transition = new TaintTransition();

        TaintElement startBlockIn = initMap.get(startBlock).getInState();
        TaintElement startBlockOut = transitionThroughBlock(startBlock, startBlockIn, transition);

        TaintElement branchEqOut = transitionThroughBlock(branchEq, startBlockOut, transition);
        tu.assertLocalValue(VAL_TAINTED, "n", branchEqOut);
        tu.assertLocalValue(VAL_CLEAN, "b", branchEqOut); // this is clean because the '==' decomposes to branches

        TaintElement branchNeqOut = transitionThroughBlock(branchNeq, startBlockOut, transition);
        tu.assertLocalValue(VAL_TAINTED, "n", branchNeqOut);
        tu.assertLocalValue(VAL_CLEAN, "b", branchNeqOut); // this is clean because the '==' decomposes to branches

        TaintJoin join = new TaintJoin();
        TaintElement joinResult = join(join, branchEqOut, branchNeqOut);

        TaintElement out = transitionThroughBlock(endBlock, joinResult, transition);
        tu.assertLocalValue(VAL_TAINTED, "n", out);
        tu.assertLocalValue(VAL_CLEAN, "b", out);
    }

    private static TestMethod getCodeIntEq() {
        String signature = "boolean test_intEq(int)";
        // @formatter:off
        String method = 
                "private boolean test_intEq(int n) {"
                + "boolean b = (n == 0);"
                + "return b;"
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }

    @Test
    public void testFloatLess() {
        // we need to do setup here, because soot acts weird
        TestMethod testMethod = getCodeFloatLess();
        CodeProcessor cp = new CodeProcessor(testMethod.method);

        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        SimpleBlockGraph bgFloatLess = gb.buildGraph(testMethod.signature);

        // add the necessary tags
        addTags(bgFloatLess.getBody().getMethod().getDeclaringClass());

        // the real test begins
        tu.setPrint(false);

        Block startBlock = bgFloatLess.getHeads().get(0);

        Block branchTrue = bgFloatLess.getSuccsOf(startBlock).get(0);
        Block branchFalse = bgFloatLess.getSuccsOf(startBlock).get(1);

        Block endBlock = bgFloatLess.getTails().get(0);

        TaintInitializer taintInit = new TaintInitializer(bgFloatLess);
        Map<Block, BlockState<TaintElement>> initMap = taintInit.getInitialStates();

        TaintTransition transition = new TaintTransition();

        TaintElement startBlockIn = initMap.get(startBlock).getInState();
        TaintElement startBlockOut = transitionThroughBlock(startBlock, startBlockIn, transition);

        TaintElement branchEqOut = transitionThroughBlock(branchTrue, startBlockOut, transition);
        tu.assertLocalValue(VAL_TAINTED, "f", branchEqOut);
        tu.assertLocalValue(VAL_CLEAN, "b", branchEqOut); // this is clean because the '==' decomposes to branches

        TaintElement branchNeqOut = transitionThroughBlock(branchFalse, startBlockOut, transition);
        tu.assertLocalValue(VAL_TAINTED, "f", branchNeqOut);
        tu.assertLocalValue(VAL_CLEAN, "b", branchNeqOut); // this is clean because the '==' decomposes to branches

        TaintJoin join = new TaintJoin();
        TaintElement joinResult = join(join, branchEqOut, branchNeqOut);

        TaintElement out = transitionThroughBlock(endBlock, joinResult, transition);
        tu.assertLocalValue(VAL_TAINTED, "f", out);
        tu.assertLocalValue(VAL_CLEAN, "b", out);
    }

    private static TestMethod getCodeFloatLess() {
        String signature = "boolean test_floatLess(float)";
        // @formatter:off
        String method = 
                "private boolean test_floatLess(float f) {"
                + "return f < 0;"
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }

    @Test
    public void testFloatGreater() {
        // we need to do setup here, because soot acts weird
        TestMethod testMethod = getCodeFloatGreater();
        CodeProcessor cp = new CodeProcessor(testMethod.method);

        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        SimpleBlockGraph bgFloatGreater = gb.buildGraph(testMethod.signature);

        // add the necessary tags
        addTags(bgFloatGreater.getBody().getMethod().getDeclaringClass());

        // the real test begins
        tu.setPrint(true);

        Block startBlock = bgFloatGreater.getHeads().get(0);

        Block branchTrue = bgFloatGreater.getSuccsOf(startBlock).get(0);
        Block branchFalse = bgFloatGreater.getSuccsOf(startBlock).get(1);

        Block endBlock = bgFloatGreater.getTails().get(0);

        TaintInitializer taintInit = new TaintInitializer(bgFloatGreater);
        Map<Block, BlockState<TaintElement>> initMap = taintInit.getInitialStates();

        TaintTransition transition = new TaintTransition();

        TaintElement startBlockIn = initMap.get(startBlock).getInState();
        TaintElement startBlockOut = transitionThroughBlock(startBlock, startBlockIn, transition);

        TaintElement branchEqOut = transitionThroughBlock(branchTrue, startBlockOut, transition);
        tu.assertLocalValue(VAL_TAINTED, "d", branchEqOut);
        tu.assertLocalValue(VAL_CLEAN, "b", branchEqOut); // this is clean because the '==' decomposes to branches

        TaintElement branchNeqOut = transitionThroughBlock(branchFalse, startBlockOut, transition);
        tu.assertLocalValue(VAL_TAINTED, "d", branchNeqOut);
        tu.assertLocalValue(VAL_CLEAN, "b", branchNeqOut); // this is clean because the '==' decomposes to branches

        TaintJoin join = new TaintJoin();
        TaintElement joinResult = join(join, branchEqOut, branchNeqOut);

        TaintElement out = transitionThroughBlock(endBlock, joinResult, transition);
        tu.assertLocalValue(VAL_TAINTED, "d", out);
        tu.assertLocalValue(VAL_CLEAN, "b", out);
    }

    private static TestMethod getCodeFloatGreater() {
        // double also is an float-type, so also test with it here
        String signature = "boolean test_floatGreater(double)";
        // @formatter:off
        String method = 
                "private boolean test_floatGreater(double d) {"
                + "boolean b = d > 0;"
                + "return b;"
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }

    @Test
    public void testFloatEq() {
        // we need to do setup here, because soot acts weird
        TestMethod testMethod = getCodeFloatEq();
        CodeProcessor cp = new CodeProcessor(testMethod.method);

        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        SimpleBlockGraph bgFloatEq = gb.buildGraph(testMethod.signature);

        // add the necessary tags
        addTags(bgFloatEq.getBody().getMethod().getDeclaringClass());

        // the real test begins
        tu.setPrint(false);

        Block startBlock = bgFloatEq.getHeads().get(0);

        Block branchTrue = bgFloatEq.getSuccsOf(startBlock).get(0);
        Block branchFalse = bgFloatEq.getSuccsOf(startBlock).get(1);

        Block endBlock = bgFloatEq.getTails().get(0);

        TaintInitializer taintInit = new TaintInitializer(bgFloatEq);
        Map<Block, BlockState<TaintElement>> initMap = taintInit.getInitialStates();

        TaintTransition transition = new TaintTransition();

        TaintElement startBlockIn = initMap.get(startBlock).getInState();
        TaintElement startBlockOut = transitionThroughBlock(startBlock, startBlockIn, transition);

        TaintElement branchEqOut = transitionThroughBlock(branchTrue, startBlockOut, transition);
        tu.assertLocalValue(VAL_TAINTED, "d", branchEqOut);
        tu.assertLocalValue(VAL_CLEAN, "b", branchEqOut); // this is clean because the '==' decomposes to branches

        TaintElement branchNeqOut = transitionThroughBlock(branchFalse, startBlockOut, transition);
        tu.assertLocalValue(VAL_TAINTED, "d", branchNeqOut);
        tu.assertLocalValue(VAL_CLEAN, "b", branchNeqOut); // this is clean because the '==' decomposes to branches

        TaintJoin join = new TaintJoin();
        TaintElement joinResult = join(join, branchEqOut, branchNeqOut);

        TaintElement out = transitionThroughBlock(endBlock, joinResult, transition);
        tu.assertLocalValue(VAL_TAINTED, "d", out);
        tu.assertLocalValue(VAL_CLEAN, "b", out);
    }

    private static TestMethod getCodeFloatEq() {
        // double also is an float-type, so also test with it here
        String signature = "boolean test_floatEq(double)";
        // @formatter:off
        String method = 
                "private boolean test_floatEq(double d) {"
                + "boolean b = d == 0;"
                + "return b;"
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }

    private TaintElement transitionThroughBlock(Block block, TaintElement in, TaintTransition transition) {
        List<Unit> units = tu.getUnitsFromBlock(block);
        TaintElement currentElement = in;
        for (Unit u : units) {
            currentElement = transition.transition(currentElement, u);
        }

        return currentElement;
    }

    private TaintElement join(TaintJoin join, TaintElement... toJoin) {
        Set<TaintElement> setToJoin = new HashSet<>();
        for (TaintElement e : toJoin) {
            setToJoin.add(e);
        }

        return join.join(setToJoin);
    }

}
