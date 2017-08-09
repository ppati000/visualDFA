package gui.visualgraph;

import dfa.framework.AbstractBlock;

/**
 * @author Patrick Petrovic
 *
 *         Represents a parent ({@code BasicBlock}) or child block ({@code LineBlock}) in the visual graph.
 */
abstract class UIAbstractBlock extends VisualGraphElement {
    /**
     * Returns the corresponding DFAFramework block.
     *
     * @return the corresponding DFAFramework block
     */
    abstract public AbstractBlock getDFABlock();
}
