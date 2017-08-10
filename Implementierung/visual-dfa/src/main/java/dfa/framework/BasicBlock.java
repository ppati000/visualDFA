package dfa.framework;

import java.util.ArrayList;
import java.util.List;

import soot.toolkits.graph.Block;

/**
 * @author Sebastian Rauch 
 *
 *         A {@code BasicBlock} represents a basic block in a {@code ControlFlowGraph}. It consists of multiple
 *         (possible zero) {@code ElementaryBlock}s.
 */
public class BasicBlock extends AbstractBlock {

    private List<ElementaryBlock> elementaryBlocks;

    private Block sootBlock = null;

    /**
     * Creates a {@code BasicBlock} from a {@code List} of {@code ElementaryBlock}s and a {@code Block}.
     * 
     * @param elementaryBlocks
     *        the {@code ElementaryBlock}s the new {@code BasicBlock} should consist of
     * @param sootBlock
     *        the {@code Block} the new {@code BasicBlock} corresponds to, can be {@code null}
     */
    public BasicBlock(List<ElementaryBlock> elementaryBlocks, Block sootBlock) {
        if (elementaryBlocks == null) {
            elementaryBlocks = new ArrayList<ElementaryBlock>();
        }

        setElementaryBlocks(new ArrayList<ElementaryBlock>(elementaryBlocks));
        setSootBlock(sootBlock);
    }

    /**
     * Returns the number of {@code ElementaryBlock}s in this {@code BasicBlock}.
     * 
     * @return the number of {@code ElementaryBlock}s in this {@code BasicBlock} (possibly {@code 0})
     */
    public int getElementaryBlockCount() {
        return elementaryBlocks.size();
    }

    /**
     * Returns a {@code List} of all {@code ElementaryBlock}s in this {@code BasicBlock}.
     * 
     * @return a {@code List} of all {@code ElementaryBlock}s in this {@code BasicBlock} (the returned {@code List} can
     *         be empty)
     */
    public List<ElementaryBlock> getElementaryBlocks() {
        return elementaryBlocks;
    }

    /**
     * Returns the {@code ElementaryBlock} at the given {@code index}.
     * 
     * @param index
     *        the index of the {@code ElementaryBlock} to be returned
     * 
     * @return the {@code ElementaryBlock} at the given {@code index}
     * 
     * @throws IndexOutOfBoundsException
     *         iff {@code index} is not in range {@code [0, getElementaryBlockCount() - 1]}
     */
    public ElementaryBlock getElementaryBlock(int index) {
        if (index < 0 || getElementaryBlockCount() <= index) {
            throw new IndexOutOfBoundsException("index is out of bounds: " + index);
        }

        return getElementaryBlocks().get(index);
    }

    /**
     * Sets the {@code ElementaryBlock}s for this {@code BasicBlock}.
     * 
     * @param elementaryBlocks
     *        the {@code ElementaryBlock}s for this {@code BasicBlock}
     */
    protected void setElementaryBlocks(List<ElementaryBlock> elementaryBlocks) {
        if (elementaryBlocks == null) {
            elementaryBlocks = new ArrayList<ElementaryBlock>();
        }

        this.elementaryBlocks = elementaryBlocks;
    }

    /**
     * Returns the (Soot-) {@code Block} corresponding to this {@code BasicBlock}.
     * 
     * @return the (Soot-) {@code Block} corresponding to this {@code BasicBlock} (possibly {@code null})
     */
    protected Block getSootBlock() {
        return sootBlock;
    }

    /**
     * Sets the (Soot-) {@code Block} corresponding to this {@code BasicBlock}.
     * 
     * @param sootBlock
     *        the (Soot-) {@code Block} corresponding to this {@code BasicBlock}
     */
    protected void setSootBlock(Block sootBlock) {
        this.sootBlock = sootBlock;
    }

}
