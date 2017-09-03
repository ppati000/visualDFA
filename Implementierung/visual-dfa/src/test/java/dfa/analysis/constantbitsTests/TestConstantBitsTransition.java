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
import dfaTests.TestMethod;
import dfaTests.TestUtils;
import dfaTests.ValueHelper;
import dfa.analyses.ConstantBitsElement;
import dfa.analyses.ConstantBitsElement.BitValue;
import dfa.analyses.ConstantBitsElement.BitValueArray;
import dfa.analyses.ConstantBitsInitializer;
import dfa.analyses.ConstantBitsJoin;
import dfa.analyses.ConstantBitsTransition;
import dfa.framework.BlockState;
import dfa.framework.SimpleBlockGraph;
import soot.Unit;
import soot.toolkits.graph.Block;

public class TestConstantBitsTransition {

    private static TestUtils<BitValueArray> tu = new TestUtils<BitValueArray>();

    private static SimpleBlockGraph bgAllConstant;
    private static SimpleBlockGraph bgHeuristic;

    @BeforeClass
    public static void setUp() {
        TestMethod testMethodAllConstant = getCodeAllConstant();
        CodeProcessor cp = new CodeProcessor(testMethodAllConstant.method);
        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        bgAllConstant = gb.buildGraph(testMethodAllConstant.signature);

        TestMethod testMethodHeuristic = getCodeHeuristic();
        cp = new CodeProcessor(testMethodHeuristic.method);
        // tu.printInfo(cp.getErrorMessage());
        Assert.assertTrue(cp.wasSuccessful());

        gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        bgHeuristic = gb.buildGraph(testMethodHeuristic.signature);
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
                + "}";
		// @formatter:on
        return new TestMethod(signature, method);
    }

    @Test
    public void testHeuristic() {
        tu.setPrint(true);

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
        for(int i = 0; i < BitValueArray.INT_SIZE; i++) {
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
        for(int i = 0; i < BitValueArray.INT_SIZE; i++) {
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
        for(int i = 0; i < BitValueArray.INT_SIZE; i++) {
            expVal4[i] = BitValue.ZERO;
        }
        for(int j = 2; j < 13; j++) {
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
        for(int i = 0; i < BitValueArray.INT_SIZE; i++) {
            expVal5[i] = BitValue.ZERO;
        }
        
        expVal5[0] = BitValue.TOP;
        expVal5[1] = BitValue.TOP;
        expVal5[2] = BitValue.TOP;
        BitValueArray expArr5 = new BitValueArray(expVal5);
        ConstantBitsElement cbe13 = cbTransition.transition(cbe12, endUnits.get(4));
        currentCbe = cbe13;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", currentCbe);
        tu.assertLocalValue(BitValueArray.getIntTop(), "p", currentCbe);
        tu.assertLocalValue(expArr1, "x", currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(26), "y", currentCbe);
        tu.assertLocalValue(expArr5, "z", currentCbe);
    }

    private static TestMethod getCodeHeuristic() {
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
                        + "z = x / y;"
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
