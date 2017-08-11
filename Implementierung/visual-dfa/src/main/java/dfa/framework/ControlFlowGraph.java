package dfa.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import soot.Unit;
import soot.toolkits.graph.Block;

/**
 * @author Sebastian Rauch 
 *
 *         A {@code ControlFlowGraph} is a graph that consists of basic-blocks and the edges between those indicate the
 *         possible control flow. A {@code ControlFlowGraph} has exactly one start-block and at most one end-block (no
 *         end-block is possible).
 */
public class ControlFlowGraph {

    private SimpleBlockGraph blockGraph;

    private LinkedList<BasicBlock> basicBlocks = new LinkedList<BasicBlock>();

    private BasicBlock startBlock = null;
    private BasicBlock endBlock = null;

    private Map<BasicBlock, List<BasicBlock>> predecessors;
    private Map<BasicBlock, List<BasicBlock>> successors;

    /**
     * Creates a new {@code ControlFlowGraph} from a {@code SimpleBlockGraph}.
     * 
     * @param blockGraph
     *        the {@code SimpleBlockGraph} this {@code ControlFlowGraph} is based on
     */
    public ControlFlowGraph(SimpleBlockGraph blockGraph) {
        Map<Block, BasicBlock> blockMapping = new HashMap<Block, BasicBlock>();

        List<Block> blocks = blockGraph.getBlocks();
        if (blocks.isEmpty()) {
            throw new IllegalArgumentException("the given BlockGraph is empty");
        }

        predecessors = new HashMap<BasicBlock, List<BasicBlock>>();
        successors = new HashMap<BasicBlock, List<BasicBlock>>();

        for (Block block : blocks) {
            BasicBlock basicBlock = buildBasicBlock(block);
            addBasicBlock(basicBlock);
            blockMapping.put(block, basicBlock);
        }

        List<Block> heads = blockGraph.getHeads();
        if (heads.size() != 1) {
            throw new IllegalArgumentException("there must be exactly one entry-point");
        }

        setStartBlock(blockMapping.get(heads.get(0)));

        List<Block> tails = blockGraph.getTails();
        if (tails.size() > 1) {
            throw new IllegalArgumentException("there must be at most one exit-point");
        }

        if (tails.size() == 1) {
            setEndBlock(blockMapping.get(tails.get(0)));
        }

        for (Block block : blocks) {
            BasicBlock basicBlock = blockMapping.get(block);

            List<BasicBlock> basicPreds = new LinkedList<BasicBlock>();
            List<Block> preds = blockGraph.getPredsOf(block);
            for (Block p : preds) {
                basicPreds.add(blockMapping.get(p));
            }

            setPredecessors(basicBlock, basicPreds);

            List<BasicBlock> basicSuccs = new LinkedList<BasicBlock>();
            List<Block> succs = blockGraph.getSuccsOf(block);
            for (Block s : succs) {
                basicSuccs.add(blockMapping.get(s));
            }

            setSuccessors(basicBlock, basicSuccs);
        }
    }

    /**
     * Returns a {@code List} of all {@code BasicBlock}s. The returned {@code List} is unmodifiable.
     * 
     * @return a {@code List} of all {@code BasicBlock}s
     */
    public List<BasicBlock> getBasicBlocks() {
        return Collections.unmodifiableList(basicBlocks);
    }

    /**
     * Returns the start-block of this {@code ControlFlowGraph}.
     * 
     * @return the start-block
     */
    public BasicBlock getStartBlock() {
        return startBlock;
    }

    /**
     * Sets the start-block of this {@code ControlFlowGraph}. {@code startBlock} must be a {@code BasicBlock} of this
     * {@code ControlFlowGraph}.
     * 
     * @param startBlock
     *        the new start-block
     * 
     * @throws IllegalArgumentException
     *         if {@code startBlock} does not belong to this {@code ControlFlowGraph}
     */
    protected void setStartBlock(BasicBlock startBlock) {
        if (!basicBlocks.contains(startBlock)) {
            throw new IllegalArgumentException("startBlock is not a BasciBlock of this CFG");
        }

        this.startBlock = startBlock;
    }

    /**
     * Returns the end-block of this {@code ControlFlowGraph}.
     * 
     * @return the end-block
     */
    public BasicBlock getEndBlock() {
        return endBlock;
    }

    /**
     * Sets the end-block of this {@code ControlFlowGraph}. {@code endBlock} must be a {@code BasicBlock} of this
     * {@code ControlFlowGraph}.
     * 
     * @param endBlock
     *        the new end-block
     * 
     * @throws IllegalArgumentException
     *         if {@code endBlock} does not belong to this {@code ControlFlowGraph}
     */
    protected void setEndBlock(BasicBlock endBlock) {
        if (!basicBlocks.contains(endBlock)) {
            throw new IllegalArgumentException("endBlock is not a BasicBlock of this CFG");
        }

        this.endBlock = endBlock;
    }

    /**
     * Returns a {@code List} of all predecessors of {@code basicBlock}.
     * 
     * @param basicBlock
     *        the {@code BasicBlock} for which the predecessors should be returned
     * 
     * @return a {@code List} of all predecessors of {@code basicBlock}.
     * 
     * @throws IllegalArgumentException
     *         if {@code endBlock} does not belong to this {@code ControlFlowGraph}
     */
    public List<BasicBlock> getPredecessors(BasicBlock basicBlock) {
        List<BasicBlock> preds = predecessors.get(basicBlock);
        if (preds == null) {
            throw new IllegalArgumentException("basicBlock is not in this CFG");
        }

        return Collections.unmodifiableList(preds);
    }

    /**
     * Sets the predecessors for {@code basicBlock}.
     * 
     * @param basicBlock
     *        the {@code BasicBlock} for which the predecessors should be set
     * 
     * @param preds
     *        the new predecessors of {@code basicBlock}
     * 
     * @throws IllegalArgumentException
     *         if {@code basicBlock} or any element of {@code preds} does not belong to this {@code ControlFlowGraph}
     */
    protected void setPredecessors(BasicBlock basicBlock, List<BasicBlock> preds) {
        if (!(basicBlocks.contains(basicBlock) && basicBlocks.containsAll(preds))) {
            throw new IllegalArgumentException("all involved BasicBlocks must be in thus CFG");
        }

        List<BasicBlock> newPreds = new ArrayList<BasicBlock>(preds);
        predecessors.put(basicBlock, newPreds);
    }

    /**
     * Returns a {@code List} of all successors of {@code basicBlock}.
     * 
     * @param basicBlock
     *        the {@code BasicBlock} for which the successors should be returned
     * 
     * @return a {@code List} of all successors of {@code basicBlock}.
     * 
     * @throws IllegalArgumentException
     *         if {@code endBlock} does not belong to this {@code ControlFlowGraph}
     */
    public List<BasicBlock> getSuccessors(BasicBlock basicBlock) {
        List<BasicBlock> succs = successors.get(basicBlock);
        if (succs == null) {
            throw new IllegalArgumentException("basicBlock is not in this CFG");
        }

        return Collections.unmodifiableList(succs);
    }

    /**
     * Sets the successors for {@code basicBlock}.
     * 
     * @param basicBlock
     *        the {@code BasicBlock} for which the successors should be set
     * 
     * @param succs
     *        the new successors of {@code basicBlock}
     * 
     * @throws IllegalArgumentException
     *         if {@code basicBlock} or any element of {@code succs} does not belong to this {@code ControlFlowGraph}
     */
    protected void setSuccessors(BasicBlock basicBlock, List<BasicBlock> succs) {
        if (!(basicBlocks.contains(basicBlock) && basicBlocks.containsAll(succs))) {
            throw new IllegalArgumentException("all involved BasicBlocks must be in this CFG");
        }

        List<BasicBlock> newSuccs = new ArrayList<BasicBlock>(succs);
        successors.put(basicBlock, newSuccs);
    }

    /**
     * Adds {@code basicBlock} to this {@code ControlFlowGraph}.
     * 
     * @param basicBlock
     *        the {@code BasicBlock} to add
     * 
     * @return {@code true} if {@code basicBlock} was added to this {@code ControlFlowGraph}, {@code false} otherwise
     * 
     * @throws IllegalArgumentException
     *         if {@code basicBlock} does not belong to this {@code ControlFlowGraph}
     */
    protected boolean addBasicBlock(BasicBlock basicBlock) {
        if (basicBlock == null) {
            throw new IllegalArgumentException("basicBlock must not be null");
        }

        if (basicBlocks.contains(basicBlock)) {
            return false;
        }

        predecessors.put(basicBlock, new LinkedList<BasicBlock>());
        successors.put(basicBlock, new LinkedList<BasicBlock>());
        return basicBlocks.add(basicBlock); // returns true
    }

    /**
     * Removes {@code basicBlock} from this {@code ControlFlowGraph}.
     * 
     * @param basicBlock
     *        the {@code BasicBlock} to remove
     * 
     * @return {@code true} if {@code basicBlock} was successfully removed, {@code false} otherwise
     */
    protected boolean removeBasicBlock(BasicBlock basicBlock) {
        if (!basicBlocks.contains(basicBlock)) {
            return false;
        }

        for (BasicBlock bBlock : basicBlocks) {
            predecessors.get(bBlock).remove(basicBlock);
            successors.get(bBlock).remove(basicBlock);
        }

        predecessors.remove(basicBlock);
        successors.remove(basicBlock);
        return basicBlocks.remove(basicBlock); // returns true
    }

    /**
     * Returns the {@code SimpleBlockGraph} this {@code ControlFlowGraph} is based on.
     * 
     * @return the {@code SimpleBlockGraph} this {@code ControlFlowGraph} is based on
     */
    protected SimpleBlockGraph getBlockGraph() {
        return blockGraph;
    }

    /*
     * Creates a {@code BasicBlock} from a given (Soot-) {@code Block}. The corresponding (Soot-) {@code Block} of the
     * newly creates {@code BasicBlock} is set to {@code block}.
     * 
     * @param block the (Soot-) {@code Block} the newly created {@code BasicBlock} is based on
     * 
     * @return a {@code BasicBlock} for the given (Soot-) {@code Block}
     */
    private BasicBlock buildBasicBlock(Block block) {
        List<ElementaryBlock> elementaryBlocks = new LinkedList<ElementaryBlock>();
        Iterator<Unit> unitIterator = block.iterator();

        while (unitIterator.hasNext()) {
            elementaryBlocks.add(new ElementaryBlock(unitIterator.next()));
        }

        return new BasicBlock(elementaryBlocks, block);
    }

}
