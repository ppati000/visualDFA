package dfa.analyses.constantfolding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import codeprocessor.CodeProcessor;
import codeprocessor.GraphBuilder;
import dfa.analyses.ConstantFoldingElement;
import dfa.analyses.ConstantFoldingElement.Value;
import dfa.analyses.ConstantFoldingInitializer;
import dfa.analyses.ConstantFoldingJoin;
import dfa.analyses.ConstantFoldingTransition;
import dfa.analyses.LocalAliasMap;
import dfa.analyses.TestMethod;
import dfa.framework.BlockState;
import dfa.framework.SimpleBlockGraph;
import soot.Body;
import soot.Local;
import soot.Unit;
import soot.jimple.IntConstant;
import soot.toolkits.graph.Block;
import soot.util.Chain;

public class TestTransition01 {
    
    // TODO stop ignoring @Test and @BeforeClass tags but as long as GraphBuilder is broken this breaks the test

    private static SimpleBlockGraph bgAllConstant;
    private static SimpleBlockGraph bgAllConstantConflict;
    private static SimpleBlockGraph bgNonConstant;

    private static boolean print = true;

    @BeforeClass @Ignore
    public static void setUp() {
        TestMethod testMethodAllConstant = getCodeAllConstant();
        CodeProcessor cp = new CodeProcessor(testMethodAllConstant.method);
        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPathName(), cp.getClassName());
        bgAllConstant = gb.buildGraph(testMethodAllConstant.signature);
        
        TestMethod testMethodAllConstantConflict = getCodeAllConstantConflict();
        cp = new CodeProcessor(testMethodAllConstantConflict.method);
        Assert.assertTrue(cp.wasSuccessful());

        gb = new GraphBuilder(cp.getPathName(), cp.getClassName());
        bgAllConstantConflict = gb.buildGraph(testMethodAllConstantConflict.signature);
        
        TestMethod testMethodNonConstant = getCodeNonConstant();
        cp = new CodeProcessor(testMethodNonConstant.method);
        Assert.assertTrue(cp.wasSuccessful());
        
        gb = new GraphBuilder(cp.getPathName(), cp.getClassName());
        bgNonConstant = gb.buildGraph(testMethodNonConstant.signature);
    }
    
    @Test @Ignore
    public void testAllConstant() {
        print = false;
        
        Assert.assertEquals(1, bgAllConstant.getBlocks().size());

        Block onlyBlock = bgAllConstant.getBlocks().get(0);

        printInfo("---- this is the only block ---- ");
        List<Unit> units = getUnitsFromBlock(onlyBlock, true);
        
        LocalAliasMap aliasMap = buildVariableAliasMapAllConstant();
        printInfo("\n" + aliasMap);

        ConstantFoldingInitializer cfInit = new ConstantFoldingInitializer(bgAllConstant);
        Map<Block, BlockState<ConstantFoldingElement>> initMap = cfInit.getInitialStates();

        ConstantFoldingElement initInState = initMap.get(onlyBlock).getInState();
        ConstantFoldingElement initOutState = initMap.get(onlyBlock).getOutState();

        printInfo("\n" + "---- the initial in-state ----");
        printInfo(initInState.getStringRepresentation());
        
        ConstantFoldingElement currentCfe = initInState;
        assertLocalValue(getIntValue(0), "one", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "x", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "y", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "z", aliasMap, currentCfe);
        
        printInfo("\n" + "---- the initial out-state ----");
        printInfo(initOutState.getStringRepresentation());
        
        currentCfe = initOutState;
        assertLocalValue(Value.getBottom(), "one", aliasMap, currentCfe);
        assertLocalValue(Value.getBottom(), "x", aliasMap, currentCfe);
        assertLocalValue(Value.getBottom(), "y", aliasMap, currentCfe);
        assertLocalValue(Value.getBottom(), "z", aliasMap, currentCfe);
        
        ConstantFoldingTransition cfTransition = new ConstantFoldingTransition();

        ConstantFoldingElement cfe01 = cfTransition.transition(initInState, units.get(0));
        currentCfe = cfe01;
        assertLocalValue(getIntValue(0), "one", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "x", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "y", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "z", aliasMap, currentCfe);
        
        ConstantFoldingElement cfe02 = cfTransition.transition(cfe01, units.get(1));
        currentCfe = cfe02;
        assertLocalValue(getIntValue(1), "one", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "x", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "y", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "z", aliasMap, currentCfe);
        
        ConstantFoldingElement cfe03 = cfTransition.transition(cfe02, units.get(2));
        currentCfe = cfe03;
        assertLocalValue(getIntValue(1), "one", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "x", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "y", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "z", aliasMap, currentCfe);
        
        ConstantFoldingElement cfe04 = cfTransition.transition(cfe03, units.get(3));
        currentCfe = cfe04;
        assertLocalValue(getIntValue(1), "one", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "x", aliasMap, currentCfe);
        assertLocalValue(getIntValue(4), "y", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "z", aliasMap, currentCfe);
        
        ConstantFoldingElement cfe05 = cfTransition.transition(cfe04, units.get(4));
        currentCfe = cfe05;
        assertLocalValue(getIntValue(1), "one", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "x", aliasMap, currentCfe);
        assertLocalValue(getIntValue(4), "y", aliasMap, currentCfe);
        assertLocalValue(getIntValue(4), "z", aliasMap, currentCfe);
        
        ConstantFoldingElement cfe06 = cfTransition.transition(cfe05, units.get(5));
        currentCfe = cfe06;
        assertLocalValue(getIntValue(1), "one", aliasMap, currentCfe);
        assertLocalValue(getIntValue(16), "x", aliasMap, currentCfe);
        assertLocalValue(getIntValue(4), "y", aliasMap, currentCfe);
        assertLocalValue(getIntValue(4), "z", aliasMap, currentCfe);
        
        
        ConstantFoldingElement cfe07 = cfTransition.transition(cfe06, units.get(6));
        currentCfe = cfe07;
        assertLocalValue(getIntValue(1), "one", aliasMap, currentCfe);
        assertLocalValue(getIntValue(16), "x", aliasMap, currentCfe);
        assertLocalValue(getIntValue(4), "y", aliasMap, currentCfe);
        assertLocalValue(getIntValue(4), "z", aliasMap, currentCfe);
    }
    
    private static TestMethod getCodeAllConstant() {
        String signature = "void test_allConstant()";
        // @formatter:off
		String method = 
		        "public void test_allConstant() {"
		                + "// to prevent Java from narrowing small constant ints to byte \n"
		                + "int one = 1;"
		                + "int x = 0 * one;" 
		                + "int y = 4 * one;" 
		                + "int z = x + y;" 
		                + "x = z * y;" 
                + "}";
		// @formatter:on
        return new TestMethod(signature, method);
    }
    
    private LocalAliasMap buildVariableAliasMapAllConstant() {
        Block onlyBlock = bgAllConstant.getBlocks().get(0);
        Body body = onlyBlock.getBody();
        Chain<Local> locals = body.getLocals();

        LocalAliasMap aliasMap = new LocalAliasMap(body.getLocals());
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
            }
        }
     
        return aliasMap;
    }

    @Test @Ignore
    public void testAllConstantConflict() {
        print = false;
        
        List<Block> blocks = bgAllConstantConflict.getBlocks();
        Assert.assertEquals(4, blocks.size());
        
        Block startBlock = bgAllConstantConflict.getHeads().get(0);
        Block trueBranchBlock = bgAllConstantConflict.getBlocks().get(1);
        Block falseBranchBlock = bgAllConstantConflict.getBlocks().get(2);
        Block endBlock = bgAllConstantConflict.getTails().get(0);
        
        printInfo("---- this is the start-block ---- ");
        List<Unit> unitsStartBlock = getUnitsFromBlock(startBlock, true);
        
        printInfo("\n ---- this is the true-branch-block ---- ");
        List<Unit> unitsTrueBranchBlock = getUnitsFromBlock(trueBranchBlock, true);

        printInfo("\n ---- this is the false-branch-block ---- ");
        List<Unit> unitsFalseBranchBlock = getUnitsFromBlock(falseBranchBlock, true);
        
        printInfo("\n ---- this is the end-block ---- ");
        List<Unit> unitsEndBlock = getUnitsFromBlock(endBlock, true);
        
        LocalAliasMap aliasMap = buildVariableAliasMapAllConstantConflict();
        printInfo("\n" + aliasMap);
        
        ConstantFoldingInitializer cfInit = new ConstantFoldingInitializer(bgAllConstantConflict);
        Map<Block, BlockState<ConstantFoldingElement>> initMap = cfInit.getInitialStates();

        // begin testing transitions for start-block
        ConstantFoldingElement initInStateStartBlock = initMap.get(startBlock).getInState();
        ConstantFoldingElement initOutStateStartBlock = initMap.get(startBlock).getOutState();
        
        printInfo("\n" + "---- the initial in-state of start-block ----");
        printInfo(initInStateStartBlock.getStringRepresentation());
        
        ConstantFoldingElement currentCfe = initInStateStartBlock;
        assertLocalValue(getIntValue(0), "one", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "x", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "y", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "z", aliasMap, currentCfe);
        
        printInfo("\n" + "---- the initial out-state of start-block ----");
        printInfo(initOutStateStartBlock.getStringRepresentation());
        
        currentCfe = initOutStateStartBlock;
        assertLocalValue(Value.getBottom(), "one", aliasMap, currentCfe);
        assertLocalValue(Value.getBottom(), "x", aliasMap, currentCfe);
        assertLocalValue(Value.getBottom(), "y", aliasMap, currentCfe);
        assertLocalValue(Value.getBottom(), "z", aliasMap, currentCfe);
        
        ConstantFoldingTransition cfTransition = new ConstantFoldingTransition();
        
        ConstantFoldingElement cfeStart01 = cfTransition.transition(initInStateStartBlock, unitsStartBlock.get(0));
        currentCfe = cfeStart01;
        assertLocalValue(getIntValue(0), "one", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "x", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "y", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "z", aliasMap, currentCfe);
        
        
        ConstantFoldingElement cfeStart02 = cfTransition.transition(cfeStart01, unitsStartBlock.get(1));
        currentCfe = cfeStart02;
        assertLocalValue(getIntValue(1), "one", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "x", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "y", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "z", aliasMap, currentCfe);
        
        ConstantFoldingElement cfeStart03 = cfTransition.transition(cfeStart02, unitsStartBlock.get(2));
        currentCfe = cfeStart03;
        assertLocalValue(getIntValue(1), "one", aliasMap, currentCfe);
        assertLocalValue(getIntValue(17), "x", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "y", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "z", aliasMap, currentCfe);
        // end testing transitions for start-block

        // begin testing transitions for true-branch
        ConstantFoldingElement cfeTrueBranch01 = cfTransition.transition(cfeStart03, unitsTrueBranchBlock.get(0));
        currentCfe = cfeTrueBranch01;
        assertLocalValue(getIntValue(1), "one", aliasMap, currentCfe);
        assertLocalValue(getIntValue(1), "x", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "y", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "z", aliasMap, currentCfe);
        
        ConstantFoldingElement cfeTrueBranch02 = cfTransition.transition(cfeTrueBranch01, unitsTrueBranchBlock.get(1));
        currentCfe = cfeTrueBranch02;
        assertLocalValue(getIntValue(1), "one", aliasMap, currentCfe);
        assertLocalValue(getIntValue(1), "x", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "y", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "z", aliasMap, currentCfe);
        // end testing transitions for true-branch

        // begin testing transitions for false-branch
        ConstantFoldingElement cfeFalseBranch01 = cfTransition.transition(cfeStart03, unitsFalseBranchBlock.get(0));
        currentCfe = cfeFalseBranch01;
        assertLocalValue(getIntValue(1), "one", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "x", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "y", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "z", aliasMap, currentCfe);
        // end testing transitions for false-branch

        // do the join of the branches
        ConstantFoldingJoin cfJoin = new ConstantFoldingJoin();
        Set<ConstantFoldingElement> toJoin = new HashSet<ConstantFoldingElement>();
        toJoin.add(cfeTrueBranch02);
        toJoin.add(cfeFalseBranch01);
        
        ConstantFoldingElement joinResult = cfJoin.join(toJoin);
        currentCfe = joinResult;
        assertLocalValue(getIntValue(1), "one", aliasMap, currentCfe);
        assertLocalValue(Value.getTop(), "x", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "y", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "z", aliasMap, currentCfe);
        
        // begin testing transitions for end-block
        ConstantFoldingElement cfeEnd01 = cfTransition.transition(joinResult, unitsEndBlock.get(0));
        currentCfe = cfeEnd01;
        assertLocalValue(getIntValue(1), "one", aliasMap, currentCfe);
        assertLocalValue(Value.getTop(), "x", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "y", aliasMap, currentCfe);        // here 0 * TOP should be 0
        assertLocalValue(getIntValue(0), "z", aliasMap, currentCfe);
        
        ConstantFoldingElement cfeEnd02 = cfTransition.transition(cfeEnd01, unitsEndBlock.get(1));
        currentCfe = cfeEnd02;
        assertLocalValue(getIntValue(1), "one", aliasMap, currentCfe);
        assertLocalValue(Value.getTop(), "x", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "y", aliasMap, currentCfe);
        assertLocalValue(Value.getTop(), "z", aliasMap, currentCfe);         // here TOP + y should be TOP
        
        ConstantFoldingElement cfeEnd03 = cfTransition.transition(cfeEnd02, unitsEndBlock.get(2));
        currentCfe = cfeEnd03;
        assertLocalValue(getIntValue(1), "one", aliasMap, currentCfe);
        assertLocalValue(Value.getTop(), "x", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "y", aliasMap, currentCfe);
        assertLocalValue(Value.getTop(), "z", aliasMap, currentCfe); 
        // end testing transitions for end-block
    }
    
    private static TestMethod getCodeAllConstantConflict() {
        String signature = "void test_allConstantConflict()";
        // formatter:off
        String method = 
                "void test_allConstantConflict() {"
                        + "// to prevent Java from narrowing small constant ints to byte \n"
                        + "int one = 1;"
                        + "int x = 17 * one;" 
                        + "if (one == 1) {"
                        +     "x = one;"
                        + "} else {"
                        +     "x = 0 * one;"
                        + "}"
                        + "int y = 0 * x;"
                        + "int z = x + y;"
                + "}";
        // formatter:on
        return new TestMethod(signature, method);
    }
    
    private LocalAliasMap buildVariableAliasMapAllConstantConflict() {
        Block onlyBlock = bgAllConstant.getBlocks().get(0);
        Body body = onlyBlock.getBody();
        Chain<Local> locals = body.getLocals();

        LocalAliasMap aliasMap = new LocalAliasMap(body.getLocals());
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
            }
        }
     
        return aliasMap;
    }
    
    @Test @Ignore
    public void testNonConstant() {
        print = true;
        
        List<Block> blocks = bgNonConstant.getBlocks();
        Assert.assertEquals(1, blocks.size());
        
        Block onlyBlock = blocks.get(0);
        
        printInfo("---- this is the only block ---- ");
        List<Unit> units = getUnitsFromBlock(onlyBlock, true);
        
        LocalAliasMap aliasMap = buildVariableAliasMapNonConstant();
        printInfo("\n" + aliasMap);
        
        ConstantFoldingInitializer cfInit = new ConstantFoldingInitializer(bgNonConstant);
        Map<Block, BlockState<ConstantFoldingElement>> initMap = cfInit.getInitialStates();
        
        ConstantFoldingElement initInStateStartBlock = initMap.get(onlyBlock).getInState();
        ConstantFoldingElement initOutStateStartBlock = initMap.get(onlyBlock).getOutState();
        
        printInfo("\n" + "---- the initial in-state of start-block ----");
        printInfo(initInStateStartBlock.getStringRepresentation());
        
        ConstantFoldingElement currentCfe = initInStateStartBlock;
        assertLocalValue(getIntValue(0), "p", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "one", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "x", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "y", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "$i0", aliasMap, currentCfe);

        printInfo("\n" + "---- the initial out-state of start-block ----");
        printInfo(initOutStateStartBlock.getStringRepresentation());
        
        currentCfe = initOutStateStartBlock;
        assertLocalValue(Value.getBottom(), "p", aliasMap, currentCfe);
        assertLocalValue(Value.getBottom(), "one", aliasMap, currentCfe);
        assertLocalValue(Value.getBottom(), "x", aliasMap, currentCfe);
        assertLocalValue(Value.getBottom(), "y", aliasMap, currentCfe);
        assertLocalValue(Value.getBottom(), "$i0", aliasMap, currentCfe);
        
        ConstantFoldingTransition cfTransition = new ConstantFoldingTransition();
        
        ConstantFoldingElement cfe01 = cfTransition.transition(initInStateStartBlock, units.get(0));
        currentCfe = cfe01;
        assertLocalValue(getIntValue(0), "p", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "one", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "x", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "y", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "$i0", aliasMap, currentCfe);
        
        ConstantFoldingElement cfe02 = cfTransition.transition(cfe01, units.get(1));
        currentCfe = cfe02;
        assertLocalValue(Value.getTop(), "p", aliasMap, currentCfe);        // p is a parameter and should become TOP
        assertLocalValue(getIntValue(0), "one", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "x", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "y", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "$i0", aliasMap, currentCfe);
        
        ConstantFoldingElement cfe03 = cfTransition.transition(cfe02, units.get(2));
        currentCfe = cfe03;
        assertLocalValue(Value.getTop(), "p", aliasMap, currentCfe);        
        assertLocalValue(getIntValue(1), "one", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "x", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "y", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "$i0", aliasMap, currentCfe);
        
        ConstantFoldingElement cfe04 = cfTransition.transition(cfe03, units.get(3));
        currentCfe = cfe04;
        assertLocalValue(Value.getTop(), "p", aliasMap, currentCfe);        
        assertLocalValue(getIntValue(1), "one", aliasMap, currentCfe);
        assertLocalValue(Value.getTop(), "x", aliasMap, currentCfe);        // 1 + TOP = TOP
        assertLocalValue(getIntValue(0), "y", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "$i0", aliasMap, currentCfe);
        
        ConstantFoldingElement cfe05 = cfTransition.transition(cfe04, units.get(4));
        currentCfe = cfe05;
        assertLocalValue(Value.getTop(), "p", aliasMap, currentCfe);        
        assertLocalValue(getIntValue(1), "one", aliasMap, currentCfe);
        assertLocalValue(Value.getTop(), "x", aliasMap, currentCfe);      
        assertLocalValue(getIntValue(0), "y", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "$i0", aliasMap, currentCfe);      // 0 * TOP = 0
        
        ConstantFoldingElement cfe06 = cfTransition.transition(cfe05, units.get(5));
        currentCfe = cfe06;
        assertLocalValue(Value.getTop(), "p", aliasMap, currentCfe);        
        assertLocalValue(getIntValue(1), "one", aliasMap, currentCfe);
        assertLocalValue(Value.getTop(), "x", aliasMap, currentCfe);      
        assertLocalValue(getIntValue(5), "y", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "$i0", aliasMap, currentCfe);
        
        ConstantFoldingElement cfe07 = cfTransition.transition(cfe06, units.get(6));
        currentCfe = cfe07;
        assertLocalValue(Value.getTop(), "p", aliasMap, currentCfe);        
        assertLocalValue(getIntValue(1), "one", aliasMap, currentCfe);
        assertLocalValue(Value.getTop(), "x", aliasMap, currentCfe);      
        assertLocalValue(getIntValue(5), "y", aliasMap, currentCfe);
        assertLocalValue(getIntValue(0), "$i0", aliasMap, currentCfe);
    }
    
    
    private static TestMethod getCodeNonConstant() {
        String signature = "int test_nonConstant(int)";
        // formatter:off
        String method = 
                "int test_nonConstant(int p) {"
                        + "// to prevent Java from narrowing small constant ints to byte \n"
                        + "int one = 1;"
                        + "int x = p + one;"
                        + "int y = p * 0 + 5;"
                        + "return y;"
                + "}";
        // formatter:on
        return new TestMethod(signature, method);
    }

    private LocalAliasMap buildVariableAliasMapNonConstant() {
        Block onlyBlock = bgNonConstant.getBlocks().get(0);
        Body body = onlyBlock.getBody();
        Chain<Local> locals = body.getLocals();

        LocalAliasMap aliasMap = new LocalAliasMap(body.getLocals());
        Iterator<Local> localIt = locals.iterator();
        while (localIt.hasNext()) {
            Local l = localIt.next();
            String localName = l.getName();
            if (localName.equals("l1")) {
                aliasMap.setAlias("l1", "p");
            } else if (localName.equals("l2")) {
                aliasMap.setAlias("l2", "one");
            } else if (localName.equals("l3")) {
                aliasMap.setAlias("l3", "x");
            } else if (localName.equals("l4")) {
                aliasMap.setAlias("l4", "y");
            } 
        }
     
        return aliasMap;
    }
    
    // only if printInfo is true, it prints to the console
    private void printInfo(Object o) {
        if (print) {
            System.out.println(o);
        }
    }
    
    private Value getIntValue(int c) {
        return new Value(IntConstant.v(c));
    }
    
    private void assertLocalValue(Value expected, String name, LocalAliasMap aliasMap, ConstantFoldingElement e) {
        Assert.assertEquals(expected, aliasMap.getValueByAliasOrOriginalName(name, e.getLocalMap()));
    }
    
    private List<Unit> getUnitsFromBlock(Block block, boolean printBlock) {
        List<Unit> units = new ArrayList<Unit>();
        
        Iterator<Unit> unitIt = block.iterator();
        while (unitIt.hasNext()) {
            Unit u = unitIt.next();
            units.add(u);
            
            if (printBlock) {
                printInfo(u);
            }
        }
        
        return units;
    }

}
