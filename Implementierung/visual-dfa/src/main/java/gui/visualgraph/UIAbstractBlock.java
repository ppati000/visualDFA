package gui.visualgraph;

import dfa.framework.AbstractBlock;

/**
 * @author Patrick Petrovic
 *
 *         Represents a parent ({@code BasicBlock}) or child block ({@code LineBlock}) in the visual graph.
 */
abstract class UIAbstractBlock extends VisualGraphElement {
    protected int blockNumber = -1;

    /**
     * Returns the corresponding DFAFramework block.
     *
     * @return the corresponding DFAFramework block
     */
    abstract public AbstractBlock getDFABlock();

    abstract public String getText();

    abstract public int[] getBlockAndLineNumbers();

    public void setBlockNumber(int blockNumber) {
        this.blockNumber = blockNumber;
    }
}
