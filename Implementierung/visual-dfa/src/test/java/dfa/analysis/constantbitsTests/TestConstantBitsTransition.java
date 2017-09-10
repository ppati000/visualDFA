package dfa.analysis.constantbitsTests;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import codeprocessor.CodeProcessor;
import codeprocessor.GraphBuilder;
import dfa.analyses.ConstantBitsElement;
import dfa.analyses.ConstantBitsElement.BitValue;
import dfa.analyses.ConstantBitsElement.BitValueArray;
import dfa.analyses.ConstantBitsInitializer;
import dfa.analyses.ConstantBitsJoin;
import dfa.analyses.ConstantBitsTransition;
import dfa.framework.BlockState;
import dfa.framework.SimpleBlockGraph;
import dfaTests.TestMethod;
import dfaTests.TestUtils;
import dfaTests.ValueHelper;
import soot.Unit;
import soot.toolkits.graph.Block;

public class TestConstantBitsTransition {

    private static TestUtils<BitValueArray> tu = new TestUtils<BitValueArray>();

    private static SimpleBlockGraph bgAllConstant;
    private static SimpleBlockGraph bgHeuristic;
    private static SimpleBlockGraph bgLastResort;
    private static SimpleBlockGraph bgLastResortNegative;
    private static SimpleBlockGraph bgPowerOfTwo;
    private static SimpleBlockGraph bgDivideByOne;
    private static SimpleBlockGraph bgBitOps;
    private static SimpleBlockGraph bgShifts;
    private static SimpleBlockGraph bgUshr;

    @BeforeClass
    public static void setUp() {
        TestMethod testMethodAllConstant = getCodeAllConstant();
        CodeProcessor cp = new CodeProcessor(testMethodAllConstant.method);
        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        bgAllConstant = gb.buildGraph(testMethodAllConstant.signature);

        TestMethod testMethodHeuristic = getCodeHeuristic();
        cp = new CodeProcessor(testMethodHeuristic.method);
        Assert.assertTrue(cp.wasSuccessful());

        gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        bgHeuristic = gb.buildGraph(testMethodHeuristic.signature);

        TestMethod testMethodLastResort = getCodeLastResort();
        cp = new CodeProcessor(testMethodLastResort.method);
        Assert.assertTrue(cp.wasSuccessful());

        gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        bgLastResort = gb.buildGraph(testMethodLastResort.signature);

        TestMethod testMethodLastResortNegative = getCodeLastResortNegative();
        cp = new CodeProcessor(testMethodLastResortNegative.method);
        Assert.assertTrue(cp.wasSuccessful());

        gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        bgLastResortNegative = gb.buildGraph(testMethodLastResortNegative.signature);

        TestMethod testMethodPowerOfTwo = getCodePowerOfTwo();
        cp = new CodeProcessor(testMethodPowerOfTwo.method);
        Assert.assertTrue(cp.wasSuccessful());

        gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        bgPowerOfTwo = gb.buildGraph(testMethodPowerOfTwo.signature);

        TestMethod testMethodDivideByOne = getCodeDivideByOne();
        cp = new CodeProcessor(testMethodDivideByOne.method);
        Assert.assertTrue(cp.wasSuccessful());

        gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        bgDivideByOne = gb.buildGraph(testMethodDivideByOne.signature);

        TestMethod testMethodBitOps = getCodeBitOps();
        cp = new CodeProcessor(testMethodBitOps.method);
        Assert.assertTrue(cp.wasSuccessful());

        gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        bgBitOps = gb.buildGraph(testMethodBitOps.signature);

        TestMethod testMethodShifts = getCodeSignedShifts();
        cp = new CodeProcessor(testMethodShifts.method);
        Assert.assertTrue(cp.wasSuccessful());

        gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        bgShifts = gb.buildGraph(testMethodShifts.signature);

        TestMethod testMethodUshr = getCodeUnsignedShiftRight();
        cp = new CodeProcessor(testMethodUshr.method);
        Assert.assertTrue(cp.wasSuccessful());

        gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        bgUshr = gb.buildGraph(testMethodUshr.signature);
    }

    @Test
    public void testAllConstant() {
        tu.setPrint(false);

        Assert.assertEquals(1, bgAllConstant.getBlocks().size());

        Block onlyBlock = bgAllConstant.getBlocks().get(0);

        tu.printInfo("---- this is the only block ---- ");
        List<Unit> units = tu.getUnitsFromBlock(onlyBlock);
        tu.printInfo(tu.unitsToString(units));

        ConstantBitsInitializer cbInit = new ConstantBitsInitializer(bgAllConstant);
        Map<Block, BlockState<ConstantBitsElement>> initMap = cbInit.getInitialStates();

        ConstantBitsElement initInState = initMap.get(onlyBlock).getInState();
        ConstantBitsElement initOutState = initMap.get(onlyBlock).getOutState();

        tu.printInfo("\n" + "---- the initial in-state ----");
        tu.printInfo(initInState.getStringRepresentation());

        ConstantBitsElement currentCbe = initInState;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);

        tu.printInfo("\n" + "---- the initial out-state ----");
        tu.printInfo(initOutState.getStringRepresentation());

        currentCbe = initOutState;
        tu.assertLocalValue(BitValueArray.getIntBottom(), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "x", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "y", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "z", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "a", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "b", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "c", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "d", currentCbe);

        ConstantBitsTransition cbTransition = new ConstantBitsTransition();

        ConstantBitsElement cbe01 = cbTransition.transition(initInState, units.get(0));
        currentCbe = cbe01;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);

        ConstantBitsElement cbe02 = cbTransition.transition(cbe01, units.get(1));
        currentCbe = cbe02;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);

        ConstantBitsElement cbe03 = cbTransition.transition(cbe02, units.get(2));
        currentCbe = cbe03;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);

        ConstantBitsElement cbe04 = cbTransition.transition(cbe03, units.get(3));
        currentCbe = cbe04;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);

        ConstantBitsElement cbe05 = cbTransition.transition(cbe04, units.get(4));
        currentCbe = cbe05;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-4), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);

        ConstantBitsElement cbe06 = cbTransition.transition(cbe05, units.get(5));
        currentCbe = cbe06;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-4), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);

        ConstantBitsElement cbe07 = cbTransition.transition(cbe06, units.get(6));
        currentCbe = cbe07;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-4), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(2), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);

        ConstantBitsElement cbe08 = cbTransition.transition(cbe07, units.get(7));
        currentCbe = cbe08;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-4), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(2), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-3), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);

        ConstantBitsElement cbe09 = cbTransition.transition(cbe08, units.get(8));
        currentCbe = cbe09;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-4), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(2), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-3), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);

        ConstantBitsElement cbe10 = cbTransition.transition(cbe09, units.get(9));
        currentCbe = cbe10;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-4), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(2), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-3), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);

        ConstantBitsElement cbe11 = cbTransition.transition(cbe10, units.get(10));
        currentCbe = cbe11;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-2), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(2), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-3), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);

        ConstantBitsElement cbe12 = cbTransition.transition(cbe11, units.get(11));
        currentCbe = cbe12;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(2), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-2), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(2), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-3), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);

        ConstantBitsElement cbe13 = cbTransition.transition(cbe12, units.get(12));
        currentCbe = cbe13;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(2), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-2), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-3), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);

        ConstantBitsElement cbe14 = cbTransition.transition(cbe13, units.get(13));
        currentCbe = cbe14;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(2), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-2), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(7), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);

        ConstantBitsElement cbe15 = cbTransition.transition(cbe14, units.get(14));
        currentCbe = cbe15;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-14), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(2), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-2), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(7), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);

        ConstantBitsElement cbe16 = cbTransition.transition(cbe15, units.get(15));
        currentCbe = cbe16;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-14), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(512), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-2), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(7), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);

        ConstantBitsElement cbe17 = cbTransition.transition(cbe16, units.get(16));
        currentCbe = cbe17;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-14), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(512), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(512), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(7), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);

        ConstantBitsElement cbe18 = cbTransition.transition(cbe17, units.get(17));
        currentCbe = cbe18;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-14), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(512), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(7), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);

        ConstantBitsElement cbe19 = cbTransition.transition(cbe18, units.get(18));
        currentCbe = cbe19;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-14), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(512), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(7), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);

        ConstantBitsElement cbe20 = cbTransition.transition(cbe19, units.get(19));
        currentCbe = cbe20;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-14), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-7), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(512), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(7), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);

        ConstantBitsElement cbe21 = cbTransition.transition(cbe20, units.get(20));
        currentCbe = cbe21;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-14), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-7), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(512), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(2), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(7), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);

        ConstantBitsElement cbe22 = cbTransition.transition(cbe21, units.get(21));
        currentCbe = cbe22;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-14), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1073741820), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(512), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(2), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(7), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);

        ConstantBitsElement cbe23 = cbTransition.transition(cbe22, units.get(22));
        currentCbe = cbe23;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-14), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1073741820), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(512), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(508), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(7), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);

        ConstantBitsElement cbe24 = cbTransition.transition(cbe23, units.get(23));
        currentCbe = cbe24;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-14), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1073741820), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(512), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(7), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);

        ConstantBitsElement cbe25 = cbTransition.transition(cbe24, units.get(24));
        currentCbe = cbe25;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-14), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1073741820), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(512), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-2), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(7), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);

        ConstantBitsElement cbe26 = cbTransition.transition(cbe25, units.get(25));
        currentCbe = cbe26;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-14), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1073741820), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(512), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-2), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(7), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);

        ConstantBitsElement cbe27 = cbTransition.transition(cbe26, units.get(26));
        currentCbe = cbe27;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-14), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1073741820), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(512), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-2), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(7), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);

        ConstantBitsElement cbe28 = cbTransition.transition(cbe27, units.get(27));
        currentCbe = cbe28;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-14), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1073741820), "z", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(512), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-1073741822), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(7), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", currentCbe);
    }

    private static TestMethod getCodeAllConstant() {
        String signature = "void test_allConstant()";
        // @formatter:off
		String method = 
		        "public void test_allConstant() {"
		                + "int one = 1;"         // to prevent Java from narrowing small constant ints to byte
		                + "int x = 0 * one;"     // x = 0;           cbe03 Mult 0
		                + "int y = 4 * one;"     // y = 4;           cbe04 Mult pos
		                + "int a = -y;"          // a = -4           cbe05 Mult neg
                        + "int z = x + y;"       // z = 4;           cbe06 Add 0
		                + "int b = one + one;"   // b = 2;           cbe07 Add pos
		                + "int c = one + a;"     // c = -3;          cbe08 Add neg
		                + "int d = x / a;"       // d = 0;           cbe09 Div 0
		                + "y = y / one;"         // y = 4;           cbe10 Div pos
		                + "a = a / b;"           // a = -2;          cbe11 Div neg
		                + "z = y - b;"           // z = 2;           cbe12 Sub pos
		                + "b = y - d;"           // b = 4;           cbe13 Sub 0
		                + "c = b - c;"           // c = 7;           cbe14 Sub neg
		                + "y = a * c;"           // y = -14          cbe15
		                + "z = b << c;"          // z = 512;         cbe16 lSh pos
		                + "a = z;"               // a = 512;         cbe17
		                + "z = z >> c;"          // z = 4;           cbe18 srSh pos
		                + "z = a >>> c;"         // z = 4;           cbe19 usrSh pos
		                + "z = y >> one;"        // z = -7;          cbe20 srSh neg
		                + "b = 2 * one;"         // b = 2;           cbe21
		                + "z = y >>> b;"         // z = 1073741820;  cbe22 usrSh neg
		                + "b = z % a;"           // b = 508          cbe23 Rem pos
		                + "b = b % y;"           // b = 4            cbe24 Rem neg2
		                + "b = y % b;"           // b = -2           cbe25 Rem neg1
		                + "z = b & z;"           // z = 1073741820   cbe26 AND
		                + "b = b | z;"           // b = -2           cbe27 OR
		                + "b = b ^ z;"           // b = 3221225474   cbe28 XOR
                + "}";
		// @formatter:on
        return new TestMethod(signature, method);
    }

    @Test
    public void testHeuristic() {
        tu.setPrint(false);

        tu.printInfo(bgHeuristic.getBlocks().size());
        Assert.assertEquals(4, bgHeuristic.getBlocks().size());

        Block startBlock = bgHeuristic.getBlocks().get(0);
        Block leftBlock = bgHeuristic.getBlocks().get(1);
        Block rightBlock = bgHeuristic.getBlocks().get(2);
        Block endBlock = bgHeuristic.getBlocks().get(3);

        tu.printInfo("---- this is the start block ---- ");
        List<Unit> startUnits = tu.getUnitsFromBlock(startBlock);
        tu.printInfo(tu.unitsToString(startUnits));

        tu.printInfo("---- this is the left block ---- ");
        List<Unit> leftUnits = tu.getUnitsFromBlock(leftBlock);
        tu.printInfo(tu.unitsToString(leftUnits));

        tu.printInfo("---- this is the right block ---- ");
        List<Unit> rightUnits = tu.getUnitsFromBlock(rightBlock);
        tu.printInfo(tu.unitsToString(rightUnits));

        tu.printInfo("---- this is the end block ---- ");
        List<Unit> endUnits = tu.getUnitsFromBlock(endBlock);
        tu.printInfo(tu.unitsToString(endUnits));

        ConstantBitsInitializer cbInit = new ConstantBitsInitializer(bgHeuristic);
        Map<Block, BlockState<ConstantBitsElement>> initMap = cbInit.getInitialStates();

        ConstantBitsElement initInState = initMap.get(startBlock).getInState();
        ConstantBitsElement initOutState = initMap.get(startBlock).getOutState();

        tu.printInfo("\n" + "---- the initial in-state ----");
        tu.printInfo(initInState.getStringRepresentation());

        ConstantBitsElement currentCbe = initInState;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        tu.printInfo("\n" + "---- the initial out-state ----");
        tu.printInfo(initOutState.getStringRepresentation());

        currentCbe = initOutState;
        tu.assertLocalValue(BitValueArray.getIntBottom(), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "p", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "x", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "y", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "z", currentCbe);

        ConstantBitsTransition cbTransition = new ConstantBitsTransition();

        ConstantBitsElement cbe01 = cbTransition.transition(initInState, startUnits.get(0));
        currentCbe = cbe01;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe02 = cbTransition.transition(cbe01, startUnits.get(1));
        currentCbe = cbe02;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe03 = cbTransition.transition(cbe02, startUnits.get(2));
        currentCbe = cbe03;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe04 = cbTransition.transition(cbe03, startUnits.get(3));
        currentCbe = cbe04;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe05 = cbTransition.transition(cbe04, startUnits.get(4));
        currentCbe = cbe05;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe06 = cbTransition.transition(cbe05, leftUnits.get(0));
        currentCbe = cbe06;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(12), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe07 = cbTransition.transition(cbe05, rightUnits.get(0));
        currentCbe = cbe07;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(158), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsJoin join = new ConstantBitsJoin();

        ConstantBitsElement cbe08 = join(join, cbe06, cbe07);
        currentCbe = cbe08;
        BitValue[] expVal1 = new BitValue[BitValueArray.INT_SIZE];
        for (int i = 0; i < BitValueArray.INT_SIZE; i++) {
            expVal1[i] = BitValue.ZERO;
        }
        expVal1[1] = BitValue.TOP;
        expVal1[2] = BitValue.ONE;
        expVal1[3] = BitValue.ONE;
        expVal1[4] = BitValue.TOP;
        expVal1[7] = BitValue.TOP;
        BitValueArray expArr1 = new BitValueArray(expVal1);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe09 = cbTransition.transition(cbe08, endUnits.get(0));
        currentCbe = cbe09;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(26), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        BitValue[] expVal2 = new BitValue[BitValueArray.INT_SIZE];
        for (int i = 0; i < BitValueArray.INT_SIZE; i++) {
            expVal2[i] = BitValue.ZERO;
        }
        expVal2[1] = BitValue.TOP;
        expVal2[2] = BitValue.TOP;
        expVal2[3] = BitValue.TOP;
        expVal2[4] = BitValue.TOP;
        expVal2[5] = BitValue.ONE;
        expVal2[7] = BitValue.TOP;
        BitValueArray expArr2 = new BitValueArray(expVal2);
        ConstantBitsElement cbe10 = cbTransition.transition(cbe09, endUnits.get(1));
        currentCbe = cbe10;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(26), "y", currentCbe);
        tu.assertLocalValue(expArr2, "z", currentCbe);

        BitValue[] expVal3 = BitValueArray.getIntTop().getBitValues();
        expVal3[0] = BitValue.ZERO;
        expVal3[3] = BitValue.ZERO;
        BitValueArray expArr3 = new BitValueArray(expVal3);
        ConstantBitsElement cbe11 = cbTransition.transition(cbe09, endUnits.get(2));
        currentCbe = cbe11;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(26), "y", currentCbe);
        tu.assertLocalValue(expArr3, "z", currentCbe);

        BitValue[] expVal4 = new BitValue[BitValueArray.INT_SIZE];
        for (int i = 0; i < BitValueArray.INT_SIZE; i++) {
            expVal4[i] = BitValue.ZERO;
        }
        for (int j = 2; j < 13; j++) {
            expVal4[j] = BitValue.TOP;
        }
        expVal4[3] = BitValue.ONE;
        BitValueArray expArr4 = new BitValueArray(expVal4);
        // tu.printInfo(endUnits.get(3));
        ConstantBitsElement cbe12 = cbTransition.transition(cbe09, endUnits.get(3));
        currentCbe = cbe12;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(26), "y", currentCbe);
        tu.assertLocalValue(expArr4, "z", currentCbe);

        BitValue[] expVal5 = new BitValue[BitValueArray.INT_SIZE];
        for (int i = 0; i < BitValueArray.INT_SIZE; i++) {
            expVal5[i] = BitValue.ZERO;
        }

        expVal5[0] = BitValue.TOP;
        expVal5[1] = BitValue.TOP;
        expVal5[2] = BitValue.TOP;
        BitValueArray expArr5 = new BitValueArray(expVal5);
        ConstantBitsElement cbe13 = cbTransition.transition(cbe09, endUnits.get(4));
        currentCbe = cbe13;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(26), "y", currentCbe);
        tu.assertLocalValue(expArr5, "z", currentCbe);

        BitValue[] expVal6 = new BitValue[BitValueArray.INT_SIZE];
        for (int i = 0; i < BitValueArray.INT_SIZE; i++) {
            expVal6[i] = BitValue.ZERO;
        }

        expVal6[1] = BitValue.TOP;
        expVal6[2] = BitValue.TOP;
        expVal6[3] = BitValue.TOP;
        BitValueArray expArr6 = new BitValueArray(expVal6);
        ConstantBitsElement cbe14 = cbTransition.transition(cbe09, endUnits.get(5));
        currentCbe = cbe14;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(26), "y", currentCbe);
        tu.assertLocalValue(expArr6, "z", currentCbe);

        BitValue[] expVal7 = new BitValue[BitValueArray.INT_SIZE];
        for (int i = 0; i < BitValueArray.INT_SIZE; i++) {
            expVal7[i] = BitValue.ZERO;
        }
    }

    private static TestMethod getCodeHeuristic() {
        // We want to genereate two ints that have less then TOP_TRESHOLD TOP bits combined to test the heuristic-parts
        // of Mult, Div and Rem and the Bitwise Parts including TOP bits of Add and Sub
        String signature = "void test_Heuristic(int)";
        // @formatter:off
        String method = 
                "public void test_Heuristic(int parameter) {"
                        + "int one = 1;"      // to prevent Java from narrowing small constant ints to byte
                        + "int x = 0 * one;"                 // x = 0                                 cbe04
                        + "int p = parameter;"               // p = IntTop                            cbe05 assign param ref
                        + "if(p > 0) {x = 12 * one;}"        // x = 4;                                cbe06 
                        + "else {x = 158 * one;}"            // x = 158;                              cbe07
                        + "int y = 26 * one;"                // y = 26;                               cbe08
                                                             // x = join(x_left, x_rigth)             cbe09
                        + "int z = x + y;"                   // z = <0 T T T T 1 0 T 0 0 0 ...>       cbe10
                        + "z = x - y;"                       // z = <0 T T T T ...>                   cbe11
                        + "z = x * y;"                       // z = <0 0 T 1 T T T T T T T T T 0 ...> cbe12
                        + "z = x / y;"                       // z = <T T T 0 0 0 ...>                 cbe13
                        + "z = x % y;"                       // z = <0 T T T 0 0 ...>                 cbe14
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }

    @Test
    public void testLastResort() {
        tu.setPrint(false);

        tu.printInfo(bgLastResort.getBlocks().size());
        Assert.assertEquals(4, bgLastResort.getBlocks().size());

        Block startBlock = bgLastResort.getBlocks().get(0);
        Block leftBlock = bgLastResort.getBlocks().get(1);
        Block rightBlock = bgLastResort.getBlocks().get(2);
        Block endBlock = bgLastResort.getBlocks().get(3);

        tu.printInfo("---- this is the start block ---- ");
        List<Unit> startUnits = tu.getUnitsFromBlock(startBlock);
        tu.printInfo(tu.unitsToString(startUnits));

        tu.printInfo("---- this is the left block ---- ");
        List<Unit> leftUnits = tu.getUnitsFromBlock(leftBlock);
        tu.printInfo(tu.unitsToString(leftUnits));

        tu.printInfo("---- this is the right block ---- ");
        List<Unit> rightUnits = tu.getUnitsFromBlock(rightBlock);
        tu.printInfo(tu.unitsToString(rightUnits));

        tu.printInfo("---- this is the end block ---- ");
        List<Unit> endUnits = tu.getUnitsFromBlock(endBlock);
        tu.printInfo(tu.unitsToString(endUnits));

        ConstantBitsInitializer cbInit = new ConstantBitsInitializer(bgLastResort);
        Map<Block, BlockState<ConstantBitsElement>> initMap = cbInit.getInitialStates();

        ConstantBitsElement initInState = initMap.get(startBlock).getInState();
        ConstantBitsElement initOutState = initMap.get(startBlock).getOutState();

        tu.printInfo("\n" + "---- the initial in-state ----");
        tu.printInfo(initInState.getStringRepresentation());

        ConstantBitsElement currentCbe = initInState;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "i", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "l", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "lx", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "ly", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        tu.printInfo("\n" + "---- the initial out-state ----");
        tu.printInfo(initOutState.getStringRepresentation());

        currentCbe = initOutState;
        tu.assertLocalValue(BitValueArray.getIntBottom(), "i", currentCbe);
        tu.assertLocalValue(BitValueArray.getLongBottom(), "l", currentCbe);
        tu.assertLocalValue(BitValueArray.getLongBottom(), "lx", currentCbe);
        tu.assertLocalValue(BitValueArray.getLongBottom(), "ly", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "p", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "x", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "y", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "z", currentCbe);

        ConstantBitsTransition cbTransition = new ConstantBitsTransition();

        ConstantBitsElement cbe01 = cbTransition.transition(initInState, startUnits.get(0));
        currentCbe = cbe01;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "i", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "l", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "lx", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "ly", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe02 = cbTransition.transition(cbe01, startUnits.get(1));
        currentCbe = cbe02;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "i", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "l", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "lx", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "ly", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe03 = cbTransition.transition(cbe02, startUnits.get(2));
        currentCbe = cbe03;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "i", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "l", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "lx", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "ly", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe04 = cbTransition.transition(cbe03, startUnits.get(3));
        currentCbe = cbe04;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "i", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "l", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "lx", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "ly", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe05 = cbTransition.transition(cbe04, startUnits.get(4));
        currentCbe = cbe05;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "i", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "l", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "lx", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "ly", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe06 = cbTransition.transition(cbe05, startUnits.get(5));
        currentCbe = cbe06;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "i", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "l", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "lx", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "ly", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe07 = cbTransition.transition(cbe06, leftUnits.get(0));
        currentCbe = cbe07;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "i", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "l", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "lx", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "ly", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(12), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe08 = cbTransition.transition(cbe07, leftUnits.get(1));
        currentCbe = cbe08;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "i", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "l", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "lx", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "ly", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(12), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(26), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe09 = cbTransition.transition(cbe06, rightUnits.get(0));
        currentCbe = cbe09;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "i", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "l", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "lx", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "ly", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(158), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe10 = cbTransition.transition(cbe09, rightUnits.get(1));
        currentCbe = cbe10;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "i", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "l", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "lx", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "ly", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(158), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(118), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsJoin join = new ConstantBitsJoin();

        ConstantBitsElement cbe11 = join(join, cbe08, cbe10);
        currentCbe = cbe11;

        BitValue[] expVal1 = new BitValue[BitValueArray.INT_SIZE];
        for (int i = 0; i < BitValueArray.INT_SIZE; i++) {
            expVal1[i] = BitValue.ZERO;
        }
        expVal1[1] = BitValue.TOP;
        expVal1[2] = BitValue.ONE;
        expVal1[3] = BitValue.ONE;
        expVal1[4] = BitValue.TOP;
        expVal1[7] = BitValue.TOP;
        BitValueArray expArr1 = new BitValueArray(expVal1);

        BitValue[] expVal2 = new BitValue[BitValueArray.INT_SIZE];
        for (int i = 0; i < BitValueArray.INT_SIZE; i++) {
            expVal2[i] = BitValue.ZERO;
        }
        expVal2[1] = BitValue.ONE;
        expVal2[2] = BitValue.TOP;
        expVal2[3] = BitValue.TOP;
        expVal2[4] = BitValue.ONE;
        expVal2[5] = BitValue.TOP;
        expVal2[6] = BitValue.TOP;
        BitValueArray expArr2 = new BitValueArray(expVal2);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "i", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "l", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "lx", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "ly", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(expArr2, "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        BitValue[] expVal3 = new BitValue[BitValueArray.INT_SIZE];
        for (int i = 0; i < BitValueArray.INT_SIZE; i++) {
            expVal3[i] = BitValue.ZERO;
        }
        for (int j = 1; j < 9; j++) {
            expVal3[j] = BitValue.TOP;
        }
        BitValueArray expArr3 = new BitValueArray(expVal3);
        ConstantBitsElement cbe12 = cbTransition.transition(cbe11, endUnits.get(0));
        currentCbe = cbe12;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "i", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "l", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "lx", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "ly", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(expArr2, "y", currentCbe);
        tu.assertLocalValue(expArr3, "z", currentCbe);

        BitValue[] expVal4 = new BitValue[BitValueArray.INT_SIZE];
        for (int i = 0; i < BitValueArray.INT_SIZE; i++) {
            expVal4[i] = BitValue.TOP;
        }
        expVal4[0] = BitValue.ZERO;
        BitValueArray expArr4 = new BitValueArray(expVal4);
        ConstantBitsElement cbe13 = cbTransition.transition(cbe11, endUnits.get(1));
        currentCbe = cbe13;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "i", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "l", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "lx", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "ly", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(expArr2, "y", currentCbe);
        tu.assertLocalValue(expArr4, "z", currentCbe);

        BitValue[] expVal5 = new BitValue[BitValueArray.INT_SIZE];
        for (int i = 0; i < BitValueArray.INT_SIZE; i++) {
            expVal5[i] = BitValue.ZERO;
        }
        for (int j = 2; j < 15; j++) {
            expVal5[j] = BitValue.TOP;
        }
        BitValueArray expArr5 = new BitValueArray(expVal5);
        ConstantBitsElement cbe14 = cbTransition.transition(cbe11, endUnits.get(2));
        currentCbe = cbe14;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "i", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "l", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "lx", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "ly", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(expArr2, "y", currentCbe);
        tu.assertLocalValue(expArr5, "z", currentCbe);

        BitValue[] expVal6 = new BitValue[BitValueArray.INT_SIZE];
        for (int i = 0; i < BitValueArray.INT_SIZE; i++) {
            expVal6[i] = BitValue.ZERO;
        }
        for (int j = 0; j < 4; j++) {
            expVal6[j] = BitValue.TOP;
        }
        BitValueArray expArr6 = new BitValueArray(expVal6);
        ConstantBitsElement cbe15 = cbTransition.transition(cbe11, endUnits.get(3));
        currentCbe = cbe15;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "i", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "l", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "lx", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "ly", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(expArr2, "y", currentCbe);
        tu.assertLocalValue(expArr6, "z", currentCbe);

        BitValue[] expVal7 = new BitValue[BitValueArray.INT_SIZE];
        for (int i = 0; i < BitValueArray.INT_SIZE; i++) {
            expVal7[i] = BitValue.ZERO;
        }
        for (int j = 0; j < 7; j++) {
            expVal7[j] = BitValue.TOP;
        }
        BitValueArray expArr7 = new BitValueArray(expVal7);
        ConstantBitsElement cbe16 = cbTransition.transition(cbe11, endUnits.get(4));
        currentCbe = cbe16;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "i", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "l", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "lx", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "ly", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(expArr2, "y", currentCbe);
        tu.assertLocalValue(expArr7, "z", currentCbe);

        BitValue[] expVal8 = new BitValue[BitValueArray.LONG_SIZE];
        for (int i = 0; i < BitValueArray.LONG_SIZE; i++) {
            expVal8[i] = BitValue.ZERO;
        }
        expVal8[1] = BitValue.TOP;
        expVal8[2] = BitValue.ONE;
        expVal8[3] = BitValue.ONE;
        expVal8[4] = BitValue.TOP;
        expVal8[7] = BitValue.TOP;
        BitValueArray expArr8 = new BitValueArray(expVal8);
        ConstantBitsElement cbe17 = cbTransition.transition(cbe11, endUnits.get(5));
        currentCbe = cbe17;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "i", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "l", currentCbe);
        tu.assertLocalValue(expArr8, "lx", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "ly", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(expArr2, "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        BitValue[] expVal9 = new BitValue[BitValueArray.LONG_SIZE];
        for (int i = 0; i < BitValueArray.LONG_SIZE; i++) {
            expVal9[i] = BitValue.ZERO;
        }
        expVal9[1] = BitValue.ONE;
        expVal9[2] = BitValue.TOP;
        expVal9[3] = BitValue.TOP;
        expVal9[4] = BitValue.ONE;
        expVal9[5] = BitValue.TOP;
        expVal9[6] = BitValue.TOP;
        BitValueArray expArr9 = new BitValueArray(expVal9);
        ConstantBitsElement cbe18 = cbTransition.transition(cbe17, endUnits.get(6));
        currentCbe = cbe18;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "i", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "l", currentCbe);
        tu.assertLocalValue(expArr8, "lx", currentCbe);
        tu.assertLocalValue(expArr9, "ly", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(expArr2, "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        BitValue[] expVal10 = new BitValue[BitValueArray.LONG_SIZE];
        for (int i = 0; i < BitValueArray.LONG_SIZE; i++) {
            expVal10[i] = BitValue.ZERO;
        }
        for (int j = 1; j < 9; j++) {
            expVal10[j] = BitValue.TOP;
        }
        BitValueArray expArr10 = new BitValueArray(expVal10);
        ConstantBitsElement cbe19 = cbTransition.transition(cbe18, endUnits.get(7));
        currentCbe = cbe19;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "i", currentCbe);
        tu.assertLocalValue(expArr10, "l", currentCbe);
        tu.assertLocalValue(expArr8, "lx", currentCbe);
        tu.assertLocalValue(expArr9, "ly", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(expArr2, "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        BitValue[] expVal11 = new BitValue[BitValueArray.INT_SIZE];
        for (int i = 0; i < BitValueArray.INT_SIZE; i++) {
            expVal11[i] = BitValue.ZERO;
        }
        for (int j = 1; j < 9; j++) {
            expVal11[j] = BitValue.TOP;
        }
        BitValueArray expArr11 = new BitValueArray(expVal11);
        ConstantBitsElement cbe20 = cbTransition.transition(cbe19, endUnits.get(8));
        currentCbe = cbe20;
        tu.assertLocalValue(expArr11, "i", currentCbe);
        tu.assertLocalValue(expArr10, "l", currentCbe);
        tu.assertLocalValue(expArr8, "lx", currentCbe);
        tu.assertLocalValue(expArr9, "ly", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(expArr2, "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);
    }

    private static TestMethod getCodeLastResort() {
        // We want to genereate two ints that have more then TOP_TRESHOLD TOP bits combined to test the "Last
        // Resort"-part of Mult, Div and Rem and again the Bitwise Parts including TOP bits of Add and Sub
        String signature = "void test_LastResort(int)";
        // @formatter:off
        String method = 
                "public void test_LastResort(int parameter) {"
                        + "int one = 1;"      // to prevent Java from narrowing small constant ints to byte
                        + "int x = 0 * one;"                 // x = 0                                            cbe04
                        + "int y = 0 * one;"                 // y = 26;                                          cbe05
                        + "int p = parameter;"               // p = IntTop                                       cbe06 assign param ref
                        + "if(p > 0) {x = 12 * one;"         // x = 12;                                          cbe07
                        + "y = 26 * one;}"                   // y = 26;                                          cbe08 
                        + "else {x = 158 * one;"             // x = 158;                                         cbe09
                        + "y = 118 * one;}"                  // y = 118;                                         cbe10
                                                             // x = join([x_left, x_rigth], [y_left, y_right]);  cbe11
                        + "int z = x + y;"                   // z = <0 T T T T T T T T 0 0 0 0 ...>              cbe12
                        + "z = x - y;"                       // z = <0 T T T T T T T T T T T T ...>              cbe13
                        + "z = x * y;"                       // z = <0 0 T T T T T T T T T T T T T 0 0 0 0 ...>  cbe14
                        + "z = x / y;"                       // z = <T T T T 0 0 0 ...>                          cbe15
                        + "z = x % y;"                       // z = <T T T T T T T 0 0 0 ...>                    cbe16
                        + "long lx = (long) x;"              // lx = (long) <0 T 1 1 T 0 0 T 0 0 0 ...>          cbe17
                        + "long ly = (long) y;"              // ly = (long) <0 1 T T 1 T T 0 0 0 0 ...>          cbe18
                        + "long l = lx + ly;"                // l = (long) <0 T T T T T T T T 0 0 0 0 ...>       cbe19
                        + "int i = (int) l;"                 // i = <0 T T T T T T T T 0 0 0 0 ...>              cbe20
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }

    @Test
    public void testLastResortNegative() {
        tu.setPrint(false);

        Assert.assertEquals(4, bgLastResortNegative.getBlocks().size());

        Block startBlock = bgLastResortNegative.getBlocks().get(0);
        Block leftBlock = bgLastResortNegative.getBlocks().get(1);
        Block rightBlock = bgLastResortNegative.getBlocks().get(2);
        Block endBlock = bgLastResortNegative.getBlocks().get(3);

        tu.printInfo("---- this is the start block ---- ");
        List<Unit> startUnits = tu.getUnitsFromBlock(startBlock);
        tu.printInfo(tu.unitsToString(startUnits));

        tu.printInfo("---- this is the left block ---- ");
        List<Unit> leftUnits = tu.getUnitsFromBlock(leftBlock);
        tu.printInfo(tu.unitsToString(leftUnits));

        tu.printInfo("---- this is the right block ---- ");
        List<Unit> rightUnits = tu.getUnitsFromBlock(rightBlock);
        tu.printInfo(tu.unitsToString(rightUnits));

        tu.printInfo("---- this is the end block ---- ");
        List<Unit> endUnits = tu.getUnitsFromBlock(endBlock);
        tu.printInfo(tu.unitsToString(endUnits));

        ConstantBitsInitializer cbInit = new ConstantBitsInitializer(bgLastResortNegative);
        Map<Block, BlockState<ConstantBitsElement>> initMap = cbInit.getInitialStates();

        ConstantBitsElement initInState = initMap.get(startBlock).getInState();
        ConstantBitsElement initOutState = initMap.get(startBlock).getOutState();

        tu.printInfo("\n" + "---- the initial in-state ----");
        tu.printInfo(initInState.getStringRepresentation());

        ConstantBitsElement currentCbe = initInState;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        tu.printInfo("\n" + "---- the initial out-state ----");
        tu.printInfo(initOutState.getStringRepresentation());

        currentCbe = initOutState;
        tu.assertLocalValue(BitValueArray.getIntBottom(), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "p", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "x", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "y", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "z", currentCbe);

        ConstantBitsTransition cbTransition = new ConstantBitsTransition();

        ConstantBitsElement cbe01 = cbTransition.transition(initInState, startUnits.get(0));
        currentCbe = cbe01;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe02 = cbTransition.transition(cbe01, startUnits.get(1));
        currentCbe = cbe02;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe03 = cbTransition.transition(cbe02, startUnits.get(2));
        currentCbe = cbe03;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe04 = cbTransition.transition(cbe03, startUnits.get(3));
        currentCbe = cbe04;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe05 = cbTransition.transition(cbe04, startUnits.get(4));
        currentCbe = cbe05;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe06 = cbTransition.transition(cbe05, startUnits.get(5));
        currentCbe = cbe06;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe07 = cbTransition.transition(cbe06, leftUnits.get(0));
        currentCbe = cbe07;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-12), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe08 = cbTransition.transition(cbe07, leftUnits.get(1));
        currentCbe = cbe08;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-12), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(26), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe09 = cbTransition.transition(cbe06, rightUnits.get(0));
        currentCbe = cbe09;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-158), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe10 = cbTransition.transition(cbe09, rightUnits.get(1));
        currentCbe = cbe10;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-158), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(118), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsJoin join = new ConstantBitsJoin();

        ConstantBitsElement cbe11 = join(join, cbe08, cbe10);
        currentCbe = cbe11;

        BitValue[] expVal1 = new BitValue[BitValueArray.INT_SIZE];
        for (int i = 0; i < BitValueArray.INT_SIZE; i++) {
            expVal1[i] = BitValue.ONE;
        }
        expVal1[0] = BitValue.ZERO;
        expVal1[1] = BitValue.TOP;
        expVal1[2] = BitValue.TOP;
        expVal1[3] = BitValue.ZERO;
        expVal1[4] = BitValue.TOP;
        expVal1[7] = BitValue.TOP;
        BitValueArray expArr1 = new BitValueArray(expVal1);

        BitValue[] expVal2 = new BitValue[BitValueArray.INT_SIZE];
        for (int i = 0; i < BitValueArray.INT_SIZE; i++) {
            expVal2[i] = BitValue.ZERO;
        }
        expVal2[1] = BitValue.ONE;
        expVal2[2] = BitValue.TOP;
        expVal2[3] = BitValue.TOP;
        expVal2[4] = BitValue.ONE;
        expVal2[5] = BitValue.TOP;
        expVal2[6] = BitValue.TOP;
        BitValueArray expArr2 = new BitValueArray(expVal2);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(expArr2, "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        BitValue[] expVal3 = new BitValue[BitValueArray.INT_SIZE];
        for (int i = 0; i < BitValueArray.INT_SIZE; i++) {
            expVal3[i] = BitValue.TOP;
        }
        expVal3[0] = BitValue.ZERO;
        BitValueArray expArr3 = new BitValueArray(expVal3);
        ConstantBitsElement cbe12 = cbTransition.transition(cbe11, endUnits.get(0));
        currentCbe = cbe12;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(expArr2, "y", currentCbe);
        tu.assertLocalValue(expArr3, "z", currentCbe);

        BitValue[] expVal4 = new BitValue[BitValueArray.INT_SIZE];
        expVal4[0] = BitValue.ZERO;
        for (int i = 1; i < 9; i++) {
            expVal4[i] = BitValue.TOP;
        }
        for (int j = 9; j < BitValueArray.INT_SIZE; j++) {
            expVal4[j] = BitValue.ONE;
        }
        BitValueArray expArr4 = new BitValueArray(expVal4);
        ConstantBitsElement cbe13 = cbTransition.transition(cbe11, endUnits.get(1));
        currentCbe = cbe13;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(expArr2, "y", currentCbe);
        tu.assertLocalValue(expArr4, "z", currentCbe);

        BitValue[] expVal5 = new BitValue[BitValueArray.INT_SIZE];
        expVal5[0] = BitValue.ZERO;
        expVal5[1] = BitValue.ZERO;
        for (int i = 2; i < 15; i++) {
            expVal5[i] = BitValue.TOP;
        }
        for (int j = 15; j < BitValueArray.INT_SIZE; j++) {
            expVal5[j] = BitValue.ONE;
        }
        BitValueArray expArr5 = new BitValueArray(expVal5);
        ConstantBitsElement cbe14 = cbTransition.transition(cbe11, endUnits.get(2));
        currentCbe = cbe14;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(expArr2, "y", currentCbe);
        tu.assertLocalValue(expArr5, "z", currentCbe);

        ConstantBitsElement cbe15 = cbTransition.transition(cbe11, endUnits.get(3));
        currentCbe = cbe15;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(expArr2, "y", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "z", currentCbe);

        ConstantBitsElement cbe16 = cbTransition.transition(cbe11, endUnits.get(4));
        currentCbe = cbe16;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(expArr2, "y", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "z", currentCbe);
    }

    private static TestMethod getCodeLastResortNegative() {
        // We want to genereate two ints that have more then TOP_TRESHOLD TOP bits combined to test the "Last
        // Resort"-part of Mult, Div and Rem and again the Bitwise Parts including TOP bits of Add and Sub
        String signature = "void test_LastResortNegative(int)";
        // @formatter:off
        String method = 
                "public void test_LastResortNegative(int parameter) {"
                        + "int one = 1;"      // to prevent Java from narrowing small constant ints to byte
                        + "int x = 0 * one;"                 // x = 0                                            cbe04
                        + "int y = 0 * one;"                 // y = 0;                                          cbe05
                        + "int p = parameter;"               // p = IntTop                                       cbe06 assign param ref
                        + "if(p > 0) {x = -12 * one;"        // x = -12;                                         cbe07
                        + "y = 26 * one;}"                   // y = 26;                                          cbe08 
                        + "else {x = -158 * one;"            // x = -158;                                        cbe09
                        + "y = 118 * one;}"                  // y = 118;                                         cbe10
                                                             // x = join([x_left, x_rigth], [y_left, y_right]);  cbe11
                        + "int z = x + y;"                   // z = <0 T T T T T T T T T T T T ...>              cbe12
                        + "z = x - y;"                       // z = <0 T T T T T T T T 1 1 1 1 ...>              cbe13
                        + "z = x * y;"                       // z = <0 0 T T T T T T T T T T T T T 1 1 1 ...>    cbe14
                        + "z = x / y;"                       // z = top;                                         cbe15
                        + "z = x % y;"                       // z = top;                                         cbe16
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }

    @Test
    public void testPowerOfTwo() {
        tu.setPrint(false);

        Assert.assertEquals(4, bgPowerOfTwo.getBlocks().size());

        Block startBlock = bgPowerOfTwo.getBlocks().get(0);
        Block leftBlock = bgPowerOfTwo.getBlocks().get(1);
        Block rightBlock = bgPowerOfTwo.getBlocks().get(2);
        Block endBlock = bgPowerOfTwo.getBlocks().get(3);

        tu.printInfo("---- this is the start block ---- ");
        List<Unit> startUnits = tu.getUnitsFromBlock(startBlock);
        tu.printInfo(tu.unitsToString(startUnits));

        tu.printInfo("---- this is the left block ---- ");
        List<Unit> leftUnits = tu.getUnitsFromBlock(leftBlock);
        tu.printInfo(tu.unitsToString(leftUnits));

        tu.printInfo("---- this is the right block ---- ");
        List<Unit> rightUnits = tu.getUnitsFromBlock(rightBlock);
        tu.printInfo(tu.unitsToString(rightUnits));

        tu.printInfo("---- this is the end block ---- ");
        List<Unit> endUnits = tu.getUnitsFromBlock(endBlock);
        tu.printInfo(tu.unitsToString(endUnits));

        ConstantBitsInitializer cbInit = new ConstantBitsInitializer(bgPowerOfTwo);
        Map<Block, BlockState<ConstantBitsElement>> initMap = cbInit.getInitialStates();

        ConstantBitsElement initInState = initMap.get(startBlock).getInState();
        ConstantBitsElement initOutState = initMap.get(startBlock).getOutState();

        tu.printInfo("\n" + "---- the initial in-state ----");
        tu.printInfo(initInState.getStringRepresentation());

        ConstantBitsElement currentCbe = initInState;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        tu.printInfo("\n" + "---- the initial out-state ----");
        tu.printInfo(initOutState.getStringRepresentation());

        currentCbe = initOutState;
        tu.assertLocalValue(BitValueArray.getIntBottom(), "a", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "b", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "p", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "x", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "y", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "z", currentCbe);

        ConstantBitsTransition cbTransition = new ConstantBitsTransition();

        ConstantBitsElement cbe01 = cbTransition.transition(initInState, startUnits.get(0));
        currentCbe = cbe01;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe02 = cbTransition.transition(cbe01, startUnits.get(1));
        currentCbe = cbe02;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe03 = cbTransition.transition(cbe02, startUnits.get(2));
        currentCbe = cbe03;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe04 = cbTransition.transition(cbe03, startUnits.get(3));
        currentCbe = cbe04;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(8), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe05 = cbTransition.transition(cbe04, startUnits.get(4));
        currentCbe = cbe05;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(8), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe06 = cbTransition.transition(cbe05, startUnits.get(5));
        currentCbe = cbe06;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(8), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe07 = cbTransition.transition(cbe06, startUnits.get(6));
        currentCbe = cbe07;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(8), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe08 = cbTransition.transition(cbe06, startUnits.get(7));
        currentCbe = cbe08;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(8), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe09 = cbTransition.transition(cbe08, leftUnits.get(0));
        currentCbe = cbe09;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(8), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(12), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe10 = cbTransition.transition(cbe09, leftUnits.get(1));
        currentCbe = cbe10;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(8), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(12), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(52), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe11 = cbTransition.transition(cbe10, leftUnits.get(2));
        currentCbe = cbe11;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(8), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-2), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(12), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(52), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe12 = cbTransition.transition(cbe08, rightUnits.get(0));
        currentCbe = cbe12;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(8), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-146), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe13 = cbTransition.transition(cbe12, rightUnits.get(1));
        currentCbe = cbe13;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(8), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-146), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(118), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe14 = cbTransition.transition(cbe13, rightUnits.get(2));
        currentCbe = cbe14;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(8), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-5), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-146), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(118), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsJoin join = new ConstantBitsJoin();

        ConstantBitsElement cbe15 = join(join, cbe11, cbe14);
        currentCbe = cbe15;

        BitValue[] expVal1 = new BitValue[BitValueArray.INT_SIZE];
        for (int i = 0; i < BitValueArray.INT_SIZE; i++) {
            expVal1[i] = BitValue.TOP;
        }
        expVal1[0] = BitValue.ZERO;
        expVal1[2] = BitValue.ONE;
        expVal1[3] = BitValue.ONE;
        expVal1[4] = BitValue.ZERO;
        expVal1[7] = BitValue.ZERO;
        BitValueArray expArr1 = new BitValueArray(expVal1);

        BitValue[] expVal2 = new BitValue[BitValueArray.INT_SIZE];
        for (int i = 0; i < BitValueArray.INT_SIZE; i++) {
            expVal2[i] = BitValue.ZERO;
        }
        expVal2[1] = BitValue.TOP;
        expVal2[2] = BitValue.ONE;
        expVal2[4] = BitValue.ONE;
        expVal2[5] = BitValue.ONE;
        expVal2[6] = BitValue.TOP;
        BitValueArray expArr2 = new BitValueArray(expVal2);

        BitValue[] expVal3 = new BitValue[BitValueArray.INT_SIZE];
        for (int i = 0; i < BitValueArray.INT_SIZE; i++) {
            expVal3[i] = BitValue.ONE;
        }
        expVal3[0] = BitValue.TOP;
        expVal3[2] = BitValue.TOP;
        BitValueArray expArr3 = new BitValueArray(expVal3);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(8), "a", currentCbe);
        tu.assertLocalValue(expArr3, "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(expArr2, "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        BitValue[] expVal4 = new BitValue[BitValueArray.INT_SIZE];
        for (int i = 0; i < BitValueArray.INT_SIZE; i++) {
            expVal4[i] = BitValue.TOP;
        }
        expVal4[0] = BitValue.ONE;
        expVal4[1] = BitValue.ZERO;
        expVal4[4] = BitValue.ZERO;
        BitValueArray expArr4 = new BitValueArray(expVal4);
        ConstantBitsElement cbe16 = cbTransition.transition(cbe15, endUnits.get(0));
        currentCbe = cbe16;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(8), "a", currentCbe);
        tu.assertLocalValue(expArr3, "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(expArr2, "y", currentCbe);
        tu.assertLocalValue(expArr4, "z", currentCbe);

        ConstantBitsElement cbe17 = cbTransition.transition(cbe16, endUnits.get(1));
        currentCbe = cbe17;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(8), "a", currentCbe);
        tu.assertLocalValue(expArr3, "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(expArr2, "y", currentCbe);
        tu.assertLocalValue(expArr4, "z", currentCbe);

        BitValue[] expVal5 = new BitValue[BitValueArray.INT_SIZE];
        for (int i = 0; i < BitValueArray.INT_SIZE; i++) {
            expVal5[i] = BitValue.ZERO;
        }
        expVal5[1] = BitValue.ONE;
        expVal5[2] = BitValue.ONE;
        expVal5[3] = BitValue.TOP;
        BitValueArray expArr5 = new BitValueArray(expVal5);
        ConstantBitsElement cbe18 = cbTransition.transition(cbe17, endUnits.get(2));
        currentCbe = cbe18;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(8), "a", currentCbe);
        tu.assertLocalValue(expArr3, "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(expArr2, "y", currentCbe);
        tu.assertLocalValue(expArr5, "z", currentCbe);

        ConstantBitsElement cbe19 = cbTransition.transition(cbe18, endUnits.get(3));
        currentCbe = cbe19;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(8), "a", currentCbe);
        tu.assertLocalValue(expArr3, "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(expArr2, "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", currentCbe);

        ConstantBitsElement cbe20 = cbTransition.transition(cbe19, endUnits.get(4));
        currentCbe = cbe20;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(8), "a", currentCbe);
        tu.assertLocalValue(expArr3, "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(expArr2, "y", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "z", currentCbe);

        BitValue[] expVal6 = new BitValue[BitValueArray.INT_SIZE];
        for (int i = 0; i < BitValueArray.INT_SIZE; i++) {
            expVal6[i] = BitValue.ZERO;
        }
        expVal6[1] = BitValue.TOP;
        expVal6[2] = BitValue.ONE;
        BitValueArray expArr6 = new BitValueArray(expVal6);
        ConstantBitsElement cbe21 = cbTransition.transition(cbe20, endUnits.get(5));
        currentCbe = cbe21;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(8), "a", currentCbe);
        tu.assertLocalValue(expArr3, "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(expArr2, "y", currentCbe);
        tu.assertLocalValue(expArr6, "z", currentCbe);

        ConstantBitsElement cbe22 = cbTransition.transition(cbe21, endUnits.get(6));
        currentCbe = cbe22;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(8), "a", currentCbe);
        tu.assertLocalValue(expArr3, "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(expArr2, "y", currentCbe);
        tu.assertLocalValue(expArr2, "z", currentCbe);
    }

    private static TestMethod getCodePowerOfTwo() {
        String signature = "void test_PowerOfTwo(int)";
        // @formatter:off
        String method = 
                "public void test_PowerOfTwo(int parameter) {"
                        + "int one = 1;"      // to prevent Java from narrowing small constant ints to byte
                        + "int a = 8 * one;"                 // a = 8                                                               cbe04
                        + "int x = 0 * one;"                 // x = 0                                                               cbe05
                        + "int y = 0 * one;"                 // y = 0;                                                              cbe06
                        + "int b = 0 * one;"                 // b = 0;                                                              cbe07
                        + "int p = parameter;"               // p = IntTop                                                          cbe08 assign param ref
                        + "if(p > 0) {x = 12 * one;"         // x = 12;                                                             cbe09
                        + "y = 52 * one;"                    // y = 26;                                                             cbe10 
                        + "b = -2 * one;}"                   // b = -2                                                              cbe11
                        + "else {x = -146 * one;"            // x = -146;                                                           cbe12
                        + "y = 118 * one;"                   // y = 118;                                                            cbe13
                        + "b = -5 * one;}"                   // b = -5                                                              cbe14
                                                             // x = join([x_left, x_rigth], [y_left, y_right], [b_left, b_right]);  cbe15
                        + "int z = x / a;"                   // z = <1 0 T T 0 T T T ...>                                           cbe16
                        + "z = z / one;"                     // z = <1 0 T T 0 T T T ...>                                           cbe17
                        + "z = y / a;"                       // z = <0 1 1 T 0 0 0 0 ...>                                           cbe18
                        + "z = b / a;"                       // z = 0;                                                              cbe19
                        + "z = y % z;"                       // z = top;                                                            cbe20
                        + "z = y % a;"                       // z = <0 T 1 0 0 0 0 ...>                                             cbe21
                        + "z = y / one;"                     // z = y;                                                              cbe22
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }

    @Test
    public void testDivideByOne() {
        tu.setPrint(true);

        Assert.assertEquals(1, bgDivideByOne.getBlocks().size());

        Block onlyBlock = bgDivideByOne.getBlocks().get(0);

        tu.printInfo("---- this is the only block ---- ");
        List<Unit> startUnits = tu.getUnitsFromBlock(onlyBlock);
        tu.printInfo(tu.unitsToString(startUnits));

        ConstantBitsInitializer cbInit = new ConstantBitsInitializer(bgDivideByOne);
        Map<Block, BlockState<ConstantBitsElement>> initMap = cbInit.getInitialStates();

        ConstantBitsElement initInState = initMap.get(onlyBlock).getInState();
        ConstantBitsElement initOutState = initMap.get(onlyBlock).getOutState();

        tu.printInfo("\n" + "---- the initial in-state ----");
        tu.printInfo(initInState.getStringRepresentation());

        ConstantBitsElement currentCbe = initInState;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "r", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x#2", currentCbe);

        tu.printInfo("\n" + "---- the initial out-state ----");
        tu.printInfo(initOutState.getStringRepresentation());

        currentCbe = initOutState;
        tu.assertLocalValue(BitValueArray.getIntBottom(), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "r", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "x", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "x#2", currentCbe);

        ConstantBitsTransition cbTransition = new ConstantBitsTransition();

        ConstantBitsElement cbe01 = cbTransition.transition(initInState, startUnits.get(0));
        currentCbe = cbe01;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "r", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x#2", currentCbe);

        ConstantBitsElement cbe02 = cbTransition.transition(cbe01, startUnits.get(1));
        currentCbe = cbe02;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "r", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x#2", currentCbe);

        ConstantBitsElement cbe03 = cbTransition.transition(cbe02, startUnits.get(2));
        currentCbe = cbe03;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "r", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x#2", currentCbe);

        BitValue[] expVal1 = new BitValue[BitValueArray.INT_SIZE];
        for (int i = 0; i < BitValueArray.INT_SIZE; i++) {
            expVal1[i] = BitValue.TOP;
        }
        for (int j = 8; j < 16; j++) {
            expVal1[j] = BitValue.ZERO;
        }
        BitValueArray expArr1 = new BitValueArray(expVal1);
        ConstantBitsElement cbe04 = cbTransition.transition(cbe03, startUnits.get(3));
        currentCbe = cbe04;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "r", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "x", currentCbe);
        tu.assertLocalValue(expArr1, "x#2", currentCbe);

        ConstantBitsElement cbe05 = cbTransition.transition(cbe04, startUnits.get(4));
        currentCbe = cbe05;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(expArr1, "r", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "x", currentCbe);
        tu.assertLocalValue(expArr1, "x#2", currentCbe);
    }

    private static TestMethod getCodeDivideByOne() {
        String signature = "void test_DivideByOne(int)";
        // @formatter:off
        String method = 
                "public void test_DivideByOne(int x) {"
                        + "int one = 1;"      // to prevent Java from narrowing small constant ints to byte
                        + "x &= 0xffff00ff;"   // x#2 = <TTTT TTTT TTTT TTTT 0000 0000 TTTT TTTT>    cbe04
                        + "int r = x / one;"  // r = <TTTT TTTT TTTT TTTT 0000 0000 TTTT TTTT>      cbe05
                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }

    @Test
    public void testBitOps() {
        tu.setPrint(false);

        tu.printInfo(bgBitOps.getBlocks().size());
        Assert.assertEquals(4, bgBitOps.getBlocks().size());

        Block startBlock = bgBitOps.getBlocks().get(0);
        Block leftBlock = bgBitOps.getBlocks().get(1);
        Block rightBlock = bgBitOps.getBlocks().get(2);
        Block endBlock = bgBitOps.getBlocks().get(3);

        tu.printInfo("---- this is the start block ---- ");
        List<Unit> startUnits = tu.getUnitsFromBlock(startBlock);
        tu.printInfo(tu.unitsToString(startUnits));

        tu.printInfo("---- this is the left block ---- ");
        List<Unit> leftUnits = tu.getUnitsFromBlock(leftBlock);
        tu.printInfo(tu.unitsToString(leftUnits));

        tu.printInfo("---- this is the right block ---- ");
        List<Unit> rightUnits = tu.getUnitsFromBlock(rightBlock);
        tu.printInfo(tu.unitsToString(rightUnits));

        tu.printInfo("---- this is the end block ---- ");
        List<Unit> endUnits = tu.getUnitsFromBlock(endBlock);
        tu.printInfo(tu.unitsToString(endUnits));

        ConstantBitsInitializer cbInit = new ConstantBitsInitializer(bgBitOps);
        Map<Block, BlockState<ConstantBitsElement>> initMap = cbInit.getInitialStates();

        ConstantBitsElement initInState = initMap.get(startBlock).getInState();
        ConstantBitsElement initOutState = initMap.get(startBlock).getOutState();

        tu.printInfo("\n" + "---- the initial in-state ----");
        tu.printInfo(initInState.getStringRepresentation());

        ConstantBitsElement currentCbe = initInState;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "n", currentCbe);

        tu.printInfo("\n" + "---- the initial out-state ----");
        tu.printInfo(initOutState.getStringRepresentation());

        currentCbe = initOutState;
        tu.assertLocalValue(BitValueArray.getIntBottom(), "a", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "b", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "c", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "x", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "y", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "n", currentCbe);

        ConstantBitsTransition transition = new ConstantBitsTransition();

        ConstantBitsElement cbeStartOut = transitionThroughBlock(startBlock, initInState, transition);
        currentCbe = cbeStartOut;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(27), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(69), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(57), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "n", currentCbe);

        ConstantBitsElement cbeLeftBranchOut = transitionThroughBlock(leftBlock, cbeStartOut, transition);
        currentCbe = cbeLeftBranchOut;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(27), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(69), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(57), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(27), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(27), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "n", currentCbe);

        ConstantBitsElement cbeRightBranchOut = transitionThroughBlock(rightBlock, cbeStartOut, transition);
        currentCbe = cbeRightBranchOut;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(27), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(69), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(57), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(69), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(57), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "n", currentCbe);

        ConstantBitsJoin join = new ConstantBitsJoin();
        ConstantBitsElement cbeJoinResult = join(join, cbeLeftBranchOut, cbeRightBranchOut);

        BitValueArray expectedX = ValueHelper.getCbIntBitValueArray(1);
        expectedX.getBitValues()[1] = BitValue.TOP;
        expectedX.getBitValues()[2] = BitValue.TOP;
        expectedX.getBitValues()[3] = BitValue.TOP;
        expectedX.getBitValues()[4] = BitValue.TOP;
        expectedX.getBitValues()[6] = BitValue.TOP;

        BitValueArray expectedY = ValueHelper.getCbIntBitValueArray(25);
        expectedY.getBitValues()[1] = BitValue.TOP;
        expectedY.getBitValues()[5] = BitValue.TOP;

        ConstantBitsElement cbe01 = transition.transition(cbeJoinResult, endUnits.get(0));
        currentCbe = cbe01;

        BitValueArray expected = ValueHelper.getCbIntBitValueArray(23);
        expected.getBitValues()[3] = BitValue.TOP;
        expected.getBitValues()[6] = BitValue.TOP;

        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(27), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(69), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(57), "c", currentCbe);
        tu.assertLocalValue(expectedX, "x", currentCbe);
        tu.assertLocalValue(expectedY, "y", currentCbe);
        tu.assertLocalValue(expected, "n", currentCbe);

        ConstantBitsElement cbe02 = transition.transition(cbe01, endUnits.get(1));
        currentCbe = cbe02;

        expected = ValueHelper.getCbIntBitValueArray(25);
        expected.getBitValues()[1] = BitValue.TOP;
        expected.getBitValues()[2] = BitValue.TOP;
        expected.getBitValues()[5] = BitValue.TOP;
        expected.getBitValues()[6] = BitValue.TOP;

        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(27), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(69), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(57), "c", currentCbe);
        tu.assertLocalValue(expectedX, "x", currentCbe);
        tu.assertLocalValue(expectedY, "y", currentCbe);
        tu.assertLocalValue(expected, "n", currentCbe);

        ConstantBitsElement cbe03 = transition.transition(cbe02, endUnits.get(2));
        currentCbe = cbe03;

        expected = ValueHelper.getCbIntBitValueArray(1);
        expected.getBitValues()[2] = BitValue.TOP;
        expected.getBitValues()[3] = BitValue.TOP;

        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(27), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(69), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(57), "c", currentCbe);
        tu.assertLocalValue(expectedX, "x", currentCbe);
        tu.assertLocalValue(expectedY, "y", currentCbe);
        tu.assertLocalValue(expected, "n", currentCbe);

        ConstantBitsElement cbe04 = transition.transition(cbe03, endUnits.get(3));
        currentCbe = cbe04;

        expected = ValueHelper.getCbIntBitValueArray(1);
        expected.getBitValues()[1] = BitValue.TOP;
        expected.getBitValues()[3] = BitValue.TOP;
        expected.getBitValues()[4] = BitValue.TOP;

        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(27), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(69), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(57), "c", currentCbe);
        tu.assertLocalValue(expectedX, "x", currentCbe);
        tu.assertLocalValue(expectedY, "y", currentCbe);
        tu.assertLocalValue(expected, "n", currentCbe);

        ConstantBitsElement cbe05 = transition.transition(cbe04, endUnits.get(4));
        currentCbe = cbe05;

        expected = ValueHelper.getCbIntBitValueArray(32);
        expected.getBitValues()[1] = BitValue.TOP;
        expected.getBitValues()[2] = BitValue.TOP;
        expected.getBitValues()[3] = BitValue.TOP;
        expected.getBitValues()[4] = BitValue.TOP;
        expected.getBitValues()[6] = BitValue.TOP;

        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(27), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(69), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(57), "c", currentCbe);
        tu.assertLocalValue(expectedX, "x", currentCbe);
        tu.assertLocalValue(expectedY, "y", currentCbe);
        tu.assertLocalValue(expected, "n", currentCbe);

        ConstantBitsElement cbe06 = transition.transition(cbe05, endUnits.get(5));
        currentCbe = cbe06;

        expected = ValueHelper.getCbIntBitValueArray(0);
        expected.getBitValues()[1] = BitValue.TOP;
        expected.getBitValues()[2] = BitValue.TOP;
        expected.getBitValues()[3] = BitValue.TOP;
        expected.getBitValues()[4] = BitValue.TOP;
        expected.getBitValues()[5] = BitValue.TOP;
        expected.getBitValues()[6] = BitValue.TOP;

        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(27), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(69), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(57), "c", currentCbe);
        tu.assertLocalValue(expectedX, "x", currentCbe);
        tu.assertLocalValue(expectedY, "y", currentCbe);
        tu.assertLocalValue(expected, "n", currentCbe);
    }

    private static TestMethod getCodeBitOps() {
        String signature = "void test_bitOps(int)";
        // @formatter:off
        String method = 
                "public void test_bitOps(int parameter) {"
                        + "int one = 1;"      // to prevent Java from narrowing small constant ints to byte
                        + "int a = 27 * one;"
                        + "int b = 69 * one;"
                        + "int c = 57 * one;"
                        + "int x, y;"
                        + "if (parameter > 0) {"
                        + "x = a;"
                        + "y = a;"
                        + "} else {"
                        + "x = b;"
                        + "y = c;"
                        + "}"
                        						// x = <1 T T T T 0 T 0 0 ...>
                        						// y = <1 T 0 1 1 T 0 0 0 ...>
    					+ "int n = x | 22;"		// n = <1 1 1 T 1 0 T 0 0 ...>
    					+ "n = x | y;"			// n = <1 T T 1 1 T T 0 0 ...>
    					+ "n = x & 45;"			// n = <1 0 T T 0 0 0 0 0 ...>
    					+ "n = x & y;"			// n = <1 T 0 T T 0 0 0 0 ...>
    					+ "n = x ^ 45;"			// n = <0 T T T T 1 T 0 0 ...>
    					+ "n = x ^ y;"			// n = <0 T T T T T T 0 0 ...>

                + "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }

    @Test
    public void testSignedShifts() {
        tu.setPrint(false);

        tu.printInfo(bgShifts.getBlocks().size());
        Assert.assertEquals(4, bgShifts.getBlocks().size());

        Block startBlock = bgShifts.getBlocks().get(0);
        Block leftBlock = bgShifts.getBlocks().get(1);
        Block rightBlock = bgShifts.getBlocks().get(2);
        Block endBlock = bgShifts.getBlocks().get(3);

        tu.printInfo("---- this is the start block ---- ");
        List<Unit> startUnits = tu.getUnitsFromBlock(startBlock);
        tu.printInfo(tu.unitsToString(startUnits));

        tu.printInfo("---- this is the left block ---- ");
        List<Unit> leftUnits = tu.getUnitsFromBlock(leftBlock);
        tu.printInfo(tu.unitsToString(leftUnits));

        tu.printInfo("---- this is the right block ---- ");
        List<Unit> rightUnits = tu.getUnitsFromBlock(rightBlock);
        tu.printInfo(tu.unitsToString(rightUnits));

        tu.printInfo("---- this is the end block ---- ");
        List<Unit> endUnits = tu.getUnitsFromBlock(endBlock);
        tu.printInfo(tu.unitsToString(endUnits));

        ConstantBitsInitializer cbInit = new ConstantBitsInitializer(bgShifts);
        Map<Block, BlockState<ConstantBitsElement>> initMap = cbInit.getInitialStates();

        ConstantBitsElement initInState = initMap.get(startBlock).getInState();
        ConstantBitsElement initOutState = initMap.get(startBlock).getOutState();

        tu.printInfo("\n" + "---- the initial in-state ----");
        tu.printInfo(initInState.getStringRepresentation());

        ConstantBitsElement currentCbe = initInState;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "n", currentCbe);

        tu.printInfo("\n" + "---- the initial out-state ----");
        tu.printInfo(initOutState.getStringRepresentation());

        currentCbe = initOutState;
        tu.assertLocalValue(BitValueArray.getIntBottom(), "a", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "b", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "c", currentCbe);
        tu.assertLocalValue(BitValueArray.getLongBottom(), "x", currentCbe);
        tu.assertLocalValue(BitValueArray.getLongBottom(), "y", currentCbe);
        tu.assertLocalValue(BitValueArray.getLongBottom(), "n", currentCbe);

        ConstantBitsTransition transition = new ConstantBitsTransition();

        ConstantBitsElement cbeStartOut = transitionThroughBlock(startBlock, initInState, transition);
        currentCbe = cbeStartOut;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(6), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(35), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(235), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "n", currentCbe);

        tu.printInfo(currentCbe.getStringRepresentation());

        ConstantBitsElement cbeLeftBranchOut = transitionThroughBlock(leftBlock, cbeStartOut, transition);
        currentCbe = cbeLeftBranchOut;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(6), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(35), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(235), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(6), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(6), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "n", currentCbe);

        ConstantBitsElement cbeRightBranchOut = transitionThroughBlock(rightBlock, cbeStartOut, transition);
        currentCbe = cbeRightBranchOut;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(6), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(35), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(235), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(35), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(235), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "n", currentCbe);

        ConstantBitsJoin join = new ConstantBitsJoin();
        ConstantBitsElement cbeJoinResult = join(join, cbeLeftBranchOut, cbeRightBranchOut);

        BitValueArray expectedX = ValueHelper.getCbLongBitValueArray(2);
        expectedX.getBitValues()[0] = BitValue.TOP;
        expectedX.getBitValues()[2] = BitValue.TOP;
        expectedX.getBitValues()[5] = BitValue.TOP;

        BitValueArray expectedY = ValueHelper.getCbLongBitValueArray(2);
        expectedY.getBitValues()[0] = BitValue.TOP;
        expectedY.getBitValues()[2] = BitValue.TOP;
        expectedY.getBitValues()[3] = BitValue.TOP;
        expectedY.getBitValues()[5] = BitValue.TOP;
        expectedY.getBitValues()[6] = BitValue.TOP;
        expectedY.getBitValues()[7] = BitValue.TOP;

        currentCbe = cbeJoinResult;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(6), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(35), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(235), "c", currentCbe);
        tu.assertLocalValue(expectedX, "x", currentCbe);
        tu.assertLocalValue(expectedY, "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbLongBitValueArray(0), "n", currentCbe);

        ConstantBitsElement cbe01 = transition.transition(cbeJoinResult, endUnits.get(0));
        ConstantBitsElement cbe02 = transition.transition(cbe01, endUnits.get(1));
        currentCbe = cbe02;

        BitValueArray expectedN = ValueHelper.getCbLongBitValueArray(0);
        for (int i = 2; i <= 14; ++i) {
            expectedN.getBitValues()[i] = BitValue.TOP;
        }

        for (int i = 34; i <= 46; ++i) {
            expectedN.getBitValues()[i] = BitValue.TOP;
        }

        currentCbe = cbe02;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(6), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(35), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(235), "c", currentCbe);
        tu.assertLocalValue(expectedX, "x", currentCbe);
        tu.assertLocalValue(expectedY, "y", currentCbe);
        tu.assertLocalValue(expectedN, "n", currentCbe);

        ConstantBitsElement cbe03 = transition.transition(cbe02, endUnits.get(2));
        ConstantBitsElement cbe04 = transition.transition(cbe03, endUnits.get(3));
        currentCbe = cbe04;

        expectedN = ValueHelper.getCbLongBitValueArray(0);
        for (int i = 0; i < 64; ++i) {
            if (i >= 2 && i <= 17) {
                expectedN.getBitValues()[i] = BitValue.TOP;
            } else if (i >= 34 && i <= 49) {
                expectedN.getBitValues()[i] = BitValue.TOP;
            } else if (i == 19 || i == 20 || i == 51 || i == 52) {
                expectedN.getBitValues()[i] = BitValue.TOP;
            }
        }

        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(6), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(35), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(235), "c", currentCbe);
        tu.assertLocalValue(expectedX, "x", currentCbe);
        tu.assertLocalValue(expectedY, "y", currentCbe);
        tu.assertLocalValue(expectedN, "n", currentCbe);

        ConstantBitsElement cbe05 = transition.transition(cbe04, endUnits.get(4));
        ConstantBitsElement cbe06 = transition.transition(cbe05, endUnits.get(5));
        currentCbe = cbe06;

        expectedN = ValueHelper.getCbLongBitValueArray(0);
        for (int i = 0; i <= 5; ++i) {
            expectedN.getBitValues()[i] = BitValue.TOP;
        }

        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(6), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(35), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(235), "c", currentCbe);
        tu.assertLocalValue(expectedX, "x", currentCbe);
        tu.assertLocalValue(expectedY, "y", currentCbe);
        tu.assertLocalValue(expectedN, "n", currentCbe);

        ConstantBitsElement cbe07 = transition.transition(cbe06, endUnits.get(6));
        ConstantBitsElement cbe08 = transition.transition(cbe07, endUnits.get(7));
        currentCbe = cbe08;

        expectedN = ValueHelper.getCbLongBitValueArray(0);
        expectedN.getBitValues()[0] = BitValue.TOP;
        expectedN.getBitValues()[2] = BitValue.TOP;
        expectedN.getBitValues()[3] = BitValue.TOP;

        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(6), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(35), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(235), "c", currentCbe);
        tu.assertLocalValue(expectedX, "x", currentCbe);
        tu.assertLocalValue(expectedY, "y", currentCbe);
        tu.assertLocalValue(expectedN, "n", currentCbe);

        tu.printInfo(currentCbe.getStringRepresentation());
    }

    private static TestMethod getCodeSignedShifts() {
        String signature = "void test_signedShifts(int)";
        // @formatter:off
        String method = 
                "public void test_signedShifts(int parameter) {"
                        + "int one = 1;"      // to prevent Java from narrowing small constant ints to byte
                        + "int a = 6 * one;"
                        + "int b = 35 * one;"
                        + "int c = 235 * one;"
                        + "long x, y;"
                        + "if (parameter > 0) {"
                        + "x = a;"
                        + "y = a;"
                        + "} else {"
                        + "x = b;"
                        + "y = c;"
                        + "}"
												// x = <T 1 T 0 0 T 0 0 0 ...>
												// y = <T 1 T T 0 T T T 0 ...>
        				+ "long n = y << x;"	// n = <...> too long :)
        				+ "n = x << y;"			// n = <...> too long :)
        				+ "n = y >> x;"			// n = <T T T T T T 0 0 0 ...>
        				+ "n = x >> y;"			// n = <T 0 T T 0 0 0 0 0 ...>
				+ "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }

    @Test
    public void testUnsignedShiftRight() {
        tu.setPrint(false);

        tu.printInfo(bgUshr.getBlocks().size());
        Assert.assertEquals(4, bgUshr.getBlocks().size());

        Block startBlock = bgUshr.getBlocks().get(0);
        Block leftBlock = bgUshr.getBlocks().get(1);
        Block rightBlock = bgUshr.getBlocks().get(2);
        Block endBlock = bgUshr.getBlocks().get(3);

        tu.printInfo("---- this is the start block ---- ");
        List<Unit> startUnits = tu.getUnitsFromBlock(startBlock);
        tu.printInfo(tu.unitsToString(startUnits));

        tu.printInfo("---- this is the left block ---- ");
        List<Unit> leftUnits = tu.getUnitsFromBlock(leftBlock);
        tu.printInfo(tu.unitsToString(leftUnits));

        tu.printInfo("---- this is the right block ---- ");
        List<Unit> rightUnits = tu.getUnitsFromBlock(rightBlock);
        tu.printInfo(tu.unitsToString(rightUnits));

        tu.printInfo("---- this is the end block ---- ");
        List<Unit> endUnits = tu.getUnitsFromBlock(endBlock);
        tu.printInfo(tu.unitsToString(endUnits));

        ConstantBitsInitializer cbInit = new ConstantBitsInitializer(bgUshr);
        Map<Block, BlockState<ConstantBitsElement>> initMap = cbInit.getInitialStates();

        ConstantBitsElement initInState = initMap.get(startBlock).getInState();
        ConstantBitsElement initOutState = initMap.get(startBlock).getOutState();

        tu.printInfo("\n" + "---- the initial in-state ----");
        tu.printInfo(initInState.getStringRepresentation());

        ConstantBitsElement currentCbe = initInState;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "n", currentCbe);

        tu.printInfo("\n" + "---- the initial out-state ----");
        tu.printInfo(initOutState.getStringRepresentation());

        currentCbe = initOutState;
        tu.assertLocalValue(BitValueArray.getIntBottom(), "a", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "b", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "c", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "x", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "y", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "n", currentCbe);

        ConstantBitsTransition transition = new ConstantBitsTransition();

        ConstantBitsElement cbeStartOut = transitionThroughBlock(startBlock, initInState, transition);
        currentCbe = cbeStartOut;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-2), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-5), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(39), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "n", currentCbe);

        tu.printInfo(currentCbe.getStringRepresentation());

        ConstantBitsElement cbeLeftBranchOut = transitionThroughBlock(leftBlock, cbeStartOut, transition);
        currentCbe = cbeLeftBranchOut;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-2), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-5), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(39), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-2), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-2), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "n", currentCbe);

        ConstantBitsElement cbeRightBranchOut = transitionThroughBlock(rightBlock, cbeStartOut, transition);
        currentCbe = cbeRightBranchOut;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-2), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-5), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(39), "c", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-5), "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(39), "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "n", currentCbe);

        ConstantBitsJoin join = new ConstantBitsJoin();
        ConstantBitsElement cbeJoinResult = join(join, cbeLeftBranchOut, cbeRightBranchOut);

        BitValueArray xArr = new BitValueArray(32, BitValue.ONE);
        xArr.getBitValues()[0] = BitValue.TOP;
        xArr.getBitValues()[2] = BitValue.TOP;

        BitValueArray yArr = new BitValueArray(32, BitValue.TOP);
        yArr.getBitValues()[1] = BitValue.ONE;
        yArr.getBitValues()[2] = BitValue.ONE;
        yArr.getBitValues()[5] = BitValue.ONE;

        currentCbe = cbeJoinResult;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-2), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-5), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(39), "c", currentCbe);
        tu.assertLocalValue(xArr, "x", currentCbe);
        tu.assertLocalValue(yArr, "y", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "n", currentCbe);

        ConstantBitsElement cbe01 = transition.transition(cbeJoinResult, endUnits.get(0));
        currentCbe = cbe01;

        BitValueArray expectedN = ValueHelper.getCbIntBitValueArray(0);
        for (int i = 0; i <= 5; ++i) {
            expectedN.getBitValues()[i] = BitValue.TOP;
        }

        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-2), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-5), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(39), "c", currentCbe);
        tu.assertLocalValue(xArr, "x", currentCbe);
        tu.assertLocalValue(yArr, "y", currentCbe);
        tu.assertLocalValue(expectedN, "n", currentCbe);

        ConstantBitsElement cbe02 = transition.transition(cbe01, endUnits.get(1));
        currentCbe = cbe02;

        expectedN = new BitValueArray(32, BitValue.TOP);
        expectedN.getBitValues()[0] = BitValue.ONE;
        expectedN.getBitValues()[26] = BitValue.ZERO;
        expectedN.getBitValues()[27] = BitValue.ZERO;
        expectedN.getBitValues()[28] = BitValue.ZERO;
        expectedN.getBitValues()[29] = BitValue.ZERO;
        expectedN.getBitValues()[30] = BitValue.ZERO;
        expectedN.getBitValues()[31] = BitValue.ZERO;

        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-2), "a", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-5), "b", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(39), "c", currentCbe);
        tu.assertLocalValue(xArr, "x", currentCbe);
        tu.assertLocalValue(yArr, "y", currentCbe);
        tu.assertLocalValue(expectedN, "n", currentCbe);

        tu.printInfo(currentCbe.getStringRepresentation());
    }

    private static TestMethod getCodeUnsignedShiftRight() {
        String signature = "void test_unsignedShiftRight(int)";
        // @formatter:off
        String method = 
                "public void test_unsignedShiftRight(int parameter) {"
                        + "int one = 1;"      // to prevent Java from narrowing small constant ints to byte
                        + "int a = -2 * one;"
                        + "int b = -5 * one;"
                        + "int c = 39 * one;"
                        + "int x, y;"
                        + "if (parameter > 0) {"
                        + "x = a;"
                        + "y = a;"
                        + "} else {"
                        + "x = b;"
                        + "y = c;"
                        + "}"
											// x = <T 1 T 1 1 1 1 1 ...>
											// y = <T 1 1 T T 1 T T ...>
        				+ "int n = y >>> x;"
        				+ "n = x >>> y;"
				+ "}";
        // @formatter:on
        return new TestMethod(signature, method);
    }

    private ConstantBitsElement transitionThroughBlock(Block block, ConstantBitsElement in,
            ConstantBitsTransition transition) {
        List<Unit> units = tu.getUnitsFromBlock(block);
        ConstantBitsElement currentElement = in;
        for (Unit u : units) {
            currentElement = transition.transition(currentElement, u);
        }

        return currentElement;
    }

    private ConstantBitsElement join(ConstantBitsJoin join, ConstantBitsElement... toJoin) {
        Set<ConstantBitsElement> setToJoin = new HashSet<>();
        for (ConstantBitsElement e : toJoin) {
            setToJoin.add(e);
        }

        return join.join(setToJoin);
    }
}
