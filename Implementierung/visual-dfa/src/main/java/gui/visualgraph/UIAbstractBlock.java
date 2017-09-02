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

    /**
     * Returns the text content of this block.
     *
     * @return block text
     */
    abstract public String getText();

    /**
     * Returns the (parent) block number of this block.
     *
     * @return block number
     */
    abstract public int getBlockNumber();

    /**
     * Returns the line number of the block, if applicable, else -1.
     *
     * @return line number if applicable
     */
    abstract public int getLineNumber();

    /**
     * Sets this block's line or block number depending on subclass.
     *
     * @param blockNumber
     *         block or line number
     */
    public void setBlockNumber(int blockNumber) {
        this.blockNumber = blockNumber;
    }
}
