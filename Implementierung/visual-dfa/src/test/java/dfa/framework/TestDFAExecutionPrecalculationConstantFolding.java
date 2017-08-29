package dfa.framework;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import codeprocessor.CodeProcessor;
import codeprocessor.GraphBuilder;
import dfa.TestMethod;
import dfa.TestUtils;
import dfa.analyses.ConstantFoldingElement;
import dfa.analyses.ConstantFoldingElement.Value;
import dfa.analyses.ConstantFoldingFactory;
import dfa.analyses.LocalAliasMap;
import dfa.framework.DFAPrecalcController.ResultState;
import soot.Body;
import soot.Local;
import soot.util.Chain;

public class TestDFAExecutionPrecalculationConstantFolding {
    
    private static TestUtils<Value> tu = new TestUtils<Value>();
    
    private static SimpleBlockGraph bgConstantFoldingSimple;
    private static SimpleBlockGraph bgConstantFoldingProgSpec;
    
    @BeforeClass
    public static void setUp() {
        TestMethod tstMethodConstantFoldingSimple = getCodeSimple();
        CodeProcessor cp = new CodeProcessor(tstMethodConstantFoldingSimple.method);
        Assert.assertTrue(cp.wasSuccessful());

        GraphBuilder gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        bgConstantFoldingSimple = gb.buildGraph(tstMethodConstantFoldingSimple.signature);
        
        TestMethod tstMethodConstantFoldingProgSpec = getCodeProductSpec();
        cp = new CodeProcessor(tstMethodConstantFoldingProgSpec.method);
        Assert.assertTrue(cp.wasSuccessful());

        gb = new GraphBuilder(cp.getPath(), cp.getClassName());
        bgConstantFoldingProgSpec = gb.buildGraph(tstMethodConstantFoldingProgSpec.signature);
    }
    
    
    @Test
    public void testDFAPrecalcConstantFoldingSimple() {
        Worklist worklist = WorklistManager.getInstance().getWorklist("naive", bgConstantFoldingSimple);
        DFAPrecalcController precalcCtrl = new DFAPrecalcController();
        ConstantFoldingFactory cfFactory = new ConstantFoldingFactory();
        
        DFAExecution<ConstantFoldingElement> dfaExecution = new DFAExecution<>(cfFactory, worklist, bgConstantFoldingSimple, precalcCtrl);
        Assert.assertEquals(ResultState.COMPLETE_RESULT, precalcCtrl.getResultState());
        
        Assert.assertEquals(0, dfaExecution.getCurrentElementaryStep());
        Assert.assertEquals(0, dfaExecution.getCurrentBlockStep());
        
        ControlFlowGraph cfg = dfaExecution.getCFG();
        LocalAliasMap<Value> aliasMap = buildAliasMapSimple();
        
        BasicBlock startBlock = cfg.getStartBlock();
        BasicBlock endBlock = cfg.getEndBlock();
        
        AnalysisState<ConstantFoldingElement> initialAnalysisState = dfaExecution.getCurrentAnalysisState();
        BlockState<ConstantFoldingElement> initStartBlockState = initialAnalysisState.getBlockState(startBlock);
        ConstantFoldingElement initStartBlockInState = initStartBlockState.getInState();
        
        tu.assertLocalValue(tu.getCfIntValue(0), "top", aliasMap, initStartBlockInState);
        tu.assertLocalValue(tu.getCfIntValue(0), "one", aliasMap, initStartBlockInState);
        tu.assertLocalValue(tu.getCfIntValue(0), "x", aliasMap, initStartBlockInState);
        tu.assertLocalValue(tu.getCfIntValue(0), "y", aliasMap, initStartBlockInState);
        tu.assertLocalValue(tu.getCfIntValue(0), "z", aliasMap, initStartBlockInState);
        
        ConstantFoldingElement initStartBlockOutState = initStartBlockState.getOutState();
        tu.assertLocalValue(Value.getBottom(), "top", aliasMap, initStartBlockOutState);
        tu.assertLocalValue(Value.getBottom(), "one", aliasMap, initStartBlockOutState);
        tu.assertLocalValue(Value.getBottom(), "x", aliasMap, initStartBlockOutState);
        tu.assertLocalValue(Value.getBottom(), "y", aliasMap, initStartBlockOutState);
        tu.assertLocalValue(Value.getBottom(), "z", aliasMap, initStartBlockOutState);

        // on to the next block-step
        dfaExecution.nextBlockStep();
        
        Assert.assertEquals(1, dfaExecution.getCurrentBlockStep());
        AnalysisState<ConstantFoldingElement> aState = dfaExecution.getCurrentAnalysisState();
       
        ConstantFoldingElement startBlockFinalOutState = aState.getBlockState(startBlock).getOutState();
        tu.assertLocalValue(Value.getTop(), "top", aliasMap, startBlockFinalOutState);
        tu.assertLocalValue(tu.getCfIntValue(1), "one", aliasMap, startBlockFinalOutState);
        tu.assertLocalValue(tu.getCfIntValue(17), "x", aliasMap, startBlockFinalOutState);
        tu.assertLocalValue(tu.getCfIntValue(0), "y", aliasMap, startBlockFinalOutState);
        tu.assertLocalValue(tu.getCfIntValue(0), "z", aliasMap, startBlockFinalOutState);
        
        // skip the two branches of the if
        dfaExecution.nextBlockStep();
        dfaExecution.nextBlockStep();
        
        Assert.assertEquals(3, dfaExecution.getCurrentBlockStep());
        aState = dfaExecution.getCurrentAnalysisState();
        
        ConstantFoldingElement endBlockInState = aState.getBlockState(endBlock).getInState();
        tu.assertLocalValue(Value.getTop(), "top", aliasMap, endBlockInState);
        tu.assertLocalValue(tu.getCfIntValue(1), "one", aliasMap, endBlockInState);
        tu.assertLocalValue(Value.getTop(), "x", aliasMap, endBlockInState);
        tu.assertLocalValue(tu.getCfIntValue(0), "y", aliasMap, endBlockInState);
        tu.assertLocalValue(tu.getCfIntValue(0), "z", aliasMap, endBlockInState);
        
        // on to the very end
        while (dfaExecution.nextElementaryStep());
        
        Assert.assertEquals(3, dfaExecution.getCurrentBlockStep()); // we still are in the 4th block-step (index 3)
        Assert.assertEquals(dfaExecution.getTotalElementarySteps() - 1, dfaExecution.getCurrentElementaryStep());
        aState = dfaExecution.getCurrentAnalysisState();
        
        ConstantFoldingElement endBlockOutState = aState.getBlockState(endBlock).getOutState();
        tu.assertLocalValue(Value.getTop(), "top", aliasMap, endBlockOutState);
        tu.assertLocalValue(tu.getCfIntValue(1), "one", aliasMap, endBlockOutState);
        tu.assertLocalValue(Value.getTop(), "x", aliasMap, endBlockOutState);
        tu.assertLocalValue(tu.getCfIntValue(0), "y", aliasMap, endBlockOutState);
        tu.assertLocalValue(Value.getTop(), "z", aliasMap, endBlockOutState);
    }
    
    private static TestMethod getCodeSimple() {
        String signature = "void test_constantFoldingSimple(int)";
        // formatter:off
        String method =
        "void test_constantFoldingSimple(int top) {"
                + "int one = 1;"        // to prevent Java from narrowing small constant ints to byte
                + "int x = 17 * one;" 
                + "if (top < 0) {"
                +     "x = one;"
                + "} else {"
                +     "x = 0 * one;"
                + "}"
                + "int y = 0 * x;"
                + "int z = x + y;"
        + "}";
        // formatter:off
        return new TestMethod(signature, method);
    }
    
    private LocalAliasMap<Value> buildAliasMapSimple() {
        Body body = bgConstantFoldingSimple.getBlocks().get(0).getBody();
        Chain<Local> locals = body.getLocals();
        
        LocalAliasMap<Value> aliasMap = new LocalAliasMap<Value>(body.getLocals());
        Iterator<Local> localIt = locals.iterator();
        while (localIt.hasNext()) {
            Local l = localIt.next();
            String localName = l.getName();
            if (localName.equals("l1")) {
                aliasMap.setAlias("l1", "top");
            } else if (localName.equals("l2")) {
                aliasMap.setAlias("l2", "one");
            } else if (localName.equals("l3")) {
                aliasMap.setAlias("l3", "x");
            } else if (localName.equals("l4")) {
                aliasMap.setAlias("l4", "y");
            } else if (localName.equals("l5")) {
                aliasMap.setAlias("l5", "z");
            }
        }
        
        return aliasMap;
    }
    
    @Test 
    public void testDFAPrecalcConstantFoldingProgSpec() {
        Worklist worklist = WorklistManager.getInstance().getWorklist("naive", bgConstantFoldingProgSpec);
        DFAPrecalcController precalcCtrl = new DFAPrecalcController();
        ConstantFoldingFactory cfFactory = new ConstantFoldingFactory();
        
        DFAExecution<ConstantFoldingElement> dfaExecution = new DFAExecution<>(cfFactory, worklist, bgConstantFoldingProgSpec, precalcCtrl);
        Assert.assertEquals(ResultState.COMPLETE_RESULT, precalcCtrl.getResultState());
        
        Assert.assertEquals(0, dfaExecution.getCurrentElementaryStep());
        Assert.assertEquals(0, dfaExecution.getCurrentBlockStep());
        
        ControlFlowGraph cfg = dfaExecution.getCFG();
        LocalAliasMap<Value> aliasMap = buildAliasMapProductSpec();
        
        BasicBlock startBlock = cfg.getStartBlock();
        BasicBlock endBlock = cfg.getEndBlock();
        
        AnalysisState<ConstantFoldingElement> initialAnalysisState = dfaExecution.getCurrentAnalysisState();
        BlockState<ConstantFoldingElement> initStartBlockState = initialAnalysisState.getBlockState(startBlock);
        ConstantFoldingElement initStartBlockInState = initStartBlockState.getInState();
        
        tu.assertLocalValue(tu.getCfIntValue(0), "n", aliasMap, initStartBlockInState);
        tu.assertLocalValue(tu.getCfIntValue(0), "one", aliasMap, initStartBlockInState);
        tu.assertLocalValue(tu.getCfIntValue(0), "x", aliasMap, initStartBlockInState);
        tu.assertLocalValue(tu.getCfIntValue(0), "y", aliasMap, initStartBlockInState);
        
        ConstantFoldingElement initStartBlockOutState = initStartBlockState.getOutState();
        tu.assertLocalValue(Value.getBottom(), "n", aliasMap, initStartBlockOutState);
        tu.assertLocalValue(Value.getBottom(), "one", aliasMap, initStartBlockOutState);
        tu.assertLocalValue(Value.getBottom(), "x", aliasMap, initStartBlockOutState);
        tu.assertLocalValue(Value.getBottom(), "y", aliasMap, initStartBlockOutState);

        // on to the next block-step
        dfaExecution.nextBlockStep();
        
        Assert.assertEquals(1, dfaExecution.getCurrentBlockStep());
        AnalysisState<ConstantFoldingElement> aState = dfaExecution.getCurrentAnalysisState();
       
        ConstantFoldingElement startBlockFinalOutState = aState.getBlockState(startBlock).getOutState();
        tu.assertLocalValue(Value.getTop(), "n", aliasMap, startBlockFinalOutState);
        tu.assertLocalValue(tu.getCfIntValue(1), "one", aliasMap, startBlockFinalOutState);
        tu.assertLocalValue(tu.getCfIntValue(6), "x", aliasMap, startBlockFinalOutState);
        tu.assertLocalValue(Value.getTop(), "y", aliasMap, startBlockFinalOutState);
        
        // forwards to the end-block (final iteration)
        dfaExecution.nextBlockStep();
        dfaExecution.nextBlockStep();
        dfaExecution.nextBlockStep();
        dfaExecution.nextBlockStep();
        
        aState = dfaExecution.getCurrentAnalysisState();
        
        ConstantFoldingElement endBlockInState = aState.getBlockState(endBlock).getInState();
        tu.assertLocalValue(Value.getTop(), "n", aliasMap, endBlockInState);
        tu.assertLocalValue(tu.getCfIntValue(1), "one", aliasMap, endBlockInState);
        tu.assertLocalValue(tu.getCfIntValue(6), "x", aliasMap, endBlockInState);
        tu.assertLocalValue(Value.getTop(), "y", aliasMap, endBlockInState);

        // on to the very end
        while (dfaExecution.nextElementaryStep());
        
        Assert.assertEquals(6, dfaExecution.getCurrentBlockStep()); // we still are in the 7th block-step (index 6)
        Assert.assertEquals(dfaExecution.getTotalElementarySteps() - 1, dfaExecution.getCurrentElementaryStep());
        aState = dfaExecution.getCurrentAnalysisState();
        
        ConstantFoldingElement endBlockOutState = aState.getBlockState(endBlock).getOutState();
        tu.assertLocalValue(Value.getTop(), "n", aliasMap, endBlockOutState);
        tu.assertLocalValue(tu.getCfIntValue(1), "one", aliasMap, endBlockOutState);
        tu.assertLocalValue(tu.getCfIntValue(6), "x", aliasMap, endBlockOutState);
        tu.assertLocalValue(Value.getTop(), "y", aliasMap, endBlockOutState);
    }
    
    private static TestMethod getCodeProductSpec() {
        // this is an actual test case from the product specification
        String signature = "int test_constantFoldingProgSpec(int)";
        // formatter:off
        String method =
        "int test_constantFoldingProgSpec(int n) {"
                + "int one = 1;"        // to prevent Java from narrowing small constant ints to byte
                + "int x = 6 * one;"
                + "int y = 2 * n;"
                + "while (n > 0) {"
                +     "y = y - x;"
                +     "x = 3 * (x % 4);"
                +     "n = n - 1;"
                + "}"
                + "return y;" 
        + "}";
        // formatter:off
        return new TestMethod(signature, method);
    }
    
    private LocalAliasMap<Value> buildAliasMapProductSpec() {
        Body body = bgConstantFoldingProgSpec.getBlocks().get(0).getBody();
        Chain<Local> locals = body.getLocals();
        
        LocalAliasMap<Value> aliasMap = new LocalAliasMap<Value>(body.getLocals());
        Iterator<Local> localIt = locals.iterator();
        while (localIt.hasNext()) {
            Local l = localIt.next();
            String localName = l.getName();
            if (localName.equals("l1")) {
                aliasMap.setAlias("l1", "n");
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

}
