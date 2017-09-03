package dfa.analysis.constantbits;

import java.security.GeneralSecurityException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import codeprocessor.CodeProcessor;
import codeprocessor.GraphBuilder;
import dfa.TestMethod;
import dfa.TestUtils;
import dfa.ValueHelper;
import dfa.analyses.ConstantBitsElement;
import dfa.analyses.ConstantBitsElement.BitValueArray;
import dfa.analyses.ConstantBitsInitializer;
import dfa.analyses.ConstantBitsTransition;
import dfa.analyses.ConstantFoldingElement;
import dfa.analyses.ConstantFoldingElement.Value;
import dfa.analyses.ConstantFoldingInitializer;
import dfa.analyses.ConstantFoldingTransition;
import dfa.analyses.LocalAliasMap;
import dfa.framework.BlockState;
import dfa.framework.SimpleBlockGraph;
import soot.Body;
import soot.Local;
import soot.Unit;
import soot.toolkits.graph.Block;
import soot.util.Chain;

public class TestConstantBitsTransition {

    private static TestUtils<BitValueArray> tu = new TestUtils<BitValueArray>();

    private static SimpleBlockGraph bgAllConstant;

    @BeforeClass
    public static void setUp() {
        TestMethod testMethodAllConstant = getCodeAllConstant();
        CodeProcessor cp = new CodeProcessor(testMethodAllConstant.method);
        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        bgAllConstant = gb.buildGraph(testMethodAllConstant.signature);
    }

    @Test
    public void testAllConstant() {
        tu.setPrint(true);

        Assert.assertEquals(1, bgAllConstant.getBlocks().size());

        Block onlyBlock = bgAllConstant.getBlocks().get(0);

        tu.printInfo("---- this is the only block ---- ");
        List<Unit> units = tu.getUnitsFromBlock(onlyBlock);
        tu.printInfo(tu.unitsToString(units));

        LocalAliasMap<BitValueArray> aliasMap = buildVariableAliasMapAllConstant();
        tu.printInfo("\n" + aliasMap);

        ConstantBitsInitializer cbInit = new ConstantBitsInitializer(bgAllConstant);
        Map<Block, BlockState<ConstantBitsElement>> initMap = cbInit.getInitialStates();

        ConstantBitsElement initInState = initMap.get(onlyBlock).getInState();
        ConstantBitsElement initOutState = initMap.get(onlyBlock).getOutState();

        tu.printInfo("\n" + "---- the initial in-state ----");
        tu.printInfo(initInState.getStringRepresentation());
        tu.printInfo(cbInit.getInitialStates().get(onlyBlock).getInState().toString());

        ConstantBitsElement currentCbe = initInState;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "one", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "a", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "c", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", aliasMap, currentCbe);

        tu.printInfo("\n" + "---- the initial out-state ----");
        tu.printInfo(initOutState.getStringRepresentation());

        currentCbe = initOutState;
        tu.assertLocalValue(BitValueArray.getIntBottom(), "one", aliasMap, currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "x", aliasMap, currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "y", aliasMap, currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "z", aliasMap, currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "a", aliasMap, currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "b", aliasMap, currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "c", aliasMap, currentCbe);
        tu.assertLocalValue(BitValueArray.getIntBottom(), "d", aliasMap, currentCbe);

        ConstantBitsTransition cbTransition = new ConstantBitsTransition();

        ConstantBitsElement cbe01 = cbTransition.transition(initInState, units.get(0));
        currentCbe = cbe01;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "one", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "a", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "c", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", aliasMap, currentCbe);

        ConstantBitsElement cbe02 = cbTransition.transition(cbe01, units.get(1));
        currentCbe = cbe02;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "a", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "c", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", aliasMap, currentCbe);

        ConstantBitsElement cbe03 = cbTransition.transition(cbe02, units.get(2));
        currentCbe = cbe03;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "y", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "a", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "c", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", aliasMap, currentCbe);

        ConstantBitsElement cbe04 = cbTransition.transition(cbe03, units.get(3));
        currentCbe = cbe04;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "y", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "a", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "c", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", aliasMap, currentCbe);

        ConstantBitsElement cbe05 = cbTransition.transition(cbe04, units.get(4));
        currentCbe = cbe05;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "y", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "z", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-4), "a", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "c", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", aliasMap, currentCbe);

        ConstantBitsElement cbe06 = cbTransition.transition(cbe05, units.get(5));
        currentCbe = cbe06;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "y", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "z", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-4), "a", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "b", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "c", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", aliasMap, currentCbe);

        ConstantBitsElement cbe07 = cbTransition.transition(cbe06, units.get(6));
        currentCbe = cbe07;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "y", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "z", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-4), "a", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(2), "b", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "c", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", aliasMap, currentCbe);

        ConstantBitsElement cbe08 = cbTransition.transition(cbe07, units.get(7));
        currentCbe = cbe08;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "y", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "z", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-4), "a", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(2), "b", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-3), "c", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", aliasMap, currentCbe);

        ConstantBitsElement cbe09 = cbTransition.transition(cbe08, units.get(8));
        currentCbe = cbe09;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "y", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "z", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-4), "a", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(2), "b", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-3), "c", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", aliasMap, currentCbe);

        ConstantBitsElement cbe10 = cbTransition.transition(cbe09, units.get(9));
        currentCbe = cbe10;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "y", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "z", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-4), "a", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(2), "b", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-3), "c", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", aliasMap, currentCbe);

        ConstantBitsElement cbe11 = cbTransition.transition(cbe10, units.get(10));
        currentCbe = cbe11;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "y", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "z", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-2), "a", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(2), "b", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-3), "c", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", aliasMap, currentCbe);

        ConstantBitsElement cbe12 = cbTransition.transition(cbe11, units.get(11));
        currentCbe = cbe12;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "y", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(2), "z", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-2), "a", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(2), "b", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-3), "c", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", aliasMap, currentCbe);

        ConstantBitsElement cbe13 = cbTransition.transition(cbe12, units.get(12));
        currentCbe = cbe13;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "y", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(2), "z", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-2), "a", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "b", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-3), "c", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", aliasMap, currentCbe);

        ConstantBitsElement cbe14 = cbTransition.transition(cbe13, units.get(13));
        currentCbe = cbe14;
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(1), "one", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "x", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "y", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(2), "z", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(-2), "a", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(4), "b", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(7), "c", aliasMap, currentCbe);
        tu.assertLocalValue(ValueHelper.getCbIntBitValueArray(0), "d", aliasMap, currentCbe);
    }

    private static TestMethod getCodeAllConstant() {
        String signature = "void test_allConstant()";
        // @formatter:off
		String method = 
		        "public void test_allConstant() {"
		                + "int one = 1;"      // to prevent Java from narrowing small constant ints to byte
		                + "int x = 0 * one;"  // x = 0;         cbe03 Mult 0
		                + "int y = 4 * one;"  // y = 4;         cbe04 Mult pos
		                + "int a = -y;"       // a = -4         cbe05 Mult neg
                        + "int z = x + y;"    // z = 4;         cbe06 Add 0
		                + "int b = one + one;"// b = 2;         cbe07 Add pos
		                + "int c = one + a;"  // c = -3;        cbe08 Add neg
		                + "int d = x / a;"    // d = 0;         cbe09 Div 0
		                + "y = y / one;"      // y = 4;         cbe10 Div pos
		                + "a = a / b;"        // a = -2;        cbe11 Div neg
		                + "z = y - b;"        // z = 2;         cbe12 Sub pos
		                + "b = y - d;"        // b = 4;         cbe13 Sub 0
		                + "c = b - c;"        // c = 7;         cbe14 Sub neg
                + "}";
		// @formatter:on
        return new TestMethod(signature, method);
    }

    private LocalAliasMap<BitValueArray> buildVariableAliasMapAllConstant() {
        Block onlyBlock = bgAllConstant.getBlocks().get(0);
        Body body = onlyBlock.getBody();
        Chain<Local> locals = body.getLocals();

        LocalAliasMap<BitValueArray> aliasMap = new LocalAliasMap<BitValueArray>(body.getLocals());
        Iterator<Local> localIt = locals.iterator();
        while (localIt.hasNext()) {
            Local l = localIt.next();
            String localName = l.getName();
            if (localName.equals("l1")) {
                aliasMap.setAlias("l1", "one");
            } else if (localName.equals("l2")) {
                aliasMap.setAlias("l2", "x");
            } else if (localName.equals("l3")) {
                aliasMap.setAlias("l3", "y");
            } else if (localName.equals("l4")) {
                aliasMap.setAlias("l4", "z");
            } else if (localName.equals("l5")) {
                aliasMap.setAlias("l5", "a");
            } else if (localName.equals("l6")) {
                aliasMap.setAlias("l6", "b");
            } else if (localName.equals("l7")) {
                aliasMap.setAlias("l7", "c");
            } else if (localName.equals("l8")) {
                aliasMap.setAlias("l8", "d");
            }
        }

        return aliasMap;
    }

}
