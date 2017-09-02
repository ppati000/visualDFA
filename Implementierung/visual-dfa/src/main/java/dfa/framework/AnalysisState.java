package dfa.framework;

import java.util.HashMap;
import java.util.Map;

/**
 * An {@code AnalysisState} represents the state of a dataflow analysis in one specific step. It holds the current
 * {@code Worklist} and the current {@code BlockState} for each {@code AbstractBlock}.
 * 
 * @param <E>
 *        the type of {@code LatticeElement} used in this {@code AnalysisState}
 * 
 * @author Sebastian Rauch
 * 
 */
public class AnalysisState<E extends LatticeElement> {

    private Map<AbstractBlock, BlockState<E>> stateMap;

    private Map<BasicBlock, LogicalColor> colorMap;

    private Worklist worklist;

    private BasicBlock currentBasicBlock;

    private int eBlockIndex;

    /**
     * Construct a new {@code AnalysisState} from a given {@code Worklist}, a current {@code BasicBlock}, an index of an
     * {@code ElementaryBlock}, a {@code Map} of {@code AbstractBlock}s to {@code BlockState}s and a {@code Map} of
     * {@code AbstractBlock}s to {@code LogicalColor}s.
     * 
     * @param worklist
     *        the {@code Worklist} in this {@code AnalysisState}
     * @param currentBasicBlock
     *        the current {@code BasicBlock}
     * @param eBlockIndex
     *        the index of the current {@code ElementaryBlock}
     * @param stateMap
     *        a {@code Map} determining the {@code BlockState} of each {@code AbstractBlock}
     * @param colorMap
     *        a {@code Map} determining the {@code LogicalColor} of each {@code BasicBlock}
     */
    protected AnalysisState(Worklist worklist, BasicBlock currentBasicBlock, int eBlockIndex,
            Map<AbstractBlock, BlockState<E>> stateMap, Map<BasicBlock, LogicalColor> colorMap) {
        setWorklist(worklist);
        setCurrentBasicBlock(currentBasicBlock);
        setCurrentElementaryBlockIndex(eBlockIndex);
        setStateMap(stateMap);
        setColorMap(colorMap);
    }

    /**
     * Constructs a new {@code AnalysisState} from a given {@code Worklist}, a current {@code BasicBlock} and an index
     * of an {@code ElementaryBlock}.
     * 
     * @param worklist
     *        the {@code Worklist} in this {@code AnalysisState}
     * @param currentBasicBlock
     *        the current {@code BasicBlock}
     * @param eBlockIndex
     *        the index of the current {@code ElementaryBlock}
     */
    public AnalysisState(Worklist worklist, BasicBlock currentBasicBlock, int eBlockIndex) {
        this(worklist, currentBasicBlock, eBlockIndex, new HashMap<AbstractBlock, BlockState<E>>(),
                new HashMap<BasicBlock, LogicalColor>());
    }

    /**
     * Returns the {@code LogicalColor} of the given {@code BasicBlock}.
     * 
     * @param basicBlock
     *        the {@code BasicBlock} for which the {@code LogicalColor} is to be determined
     * 
     * @return the {@code LogicalColor} associated with {@code basicBlock}
     * 
     * @throws IllegalArgumentException
     *         if {@code basicBlock} is not associated with any {@code LogicalColor}
     */
    public LogicalColor getColor(BasicBlock basicBlock) {
        return colorMap.get(basicBlock);
    }

    /**
     * Sets the {@code LogicalColor} associated with the {@code BasicBlock}.
     * 
     * @param basicBlock
     *        the {@code BasicBlock} to set the {@code LogicalColor} for
     * @param color
     *        the {@code LogicalColor}
     * 
     * @throws IllegalArgumentException
     *         if {@code basicBlock} or {@code color} is {@code null}
     */
    public void setColor(BasicBlock basicBlock, LogicalColor color) {
        if (basicBlock == null) {
            throw new IllegalArgumentException("basicBlock must not be null");
        }

        if (color == null) {
            throw new IllegalArgumentException("color must not be null");
        }

        colorMap.put(basicBlock, color);
    }

    /**
     * Returns the {@code BlockState} associated with the given {@code BasicBlock}.
     * 
     * @param block
     *        the {@code BasicBlock} for which to retrieve the {@code BasicBlock}
     * 
     * @return the {@code BlockState} associated with {@code basicBlock}
     * 
     * @throws IllegalArgumentException
     *         if {@code basicBlock} is not associated with any {@code BlockState}
     */
    public BlockState<E> getBlockState(AbstractBlock block) {
        return stateMap.get(block);
    }

    public void setBlockState(AbstractBlock block, BlockState<E> blockState) {
        stateMap.put(block, blockState);
    }

    /**
     * Returns the current {@code Worklist}.
     * 
     * @return the current {@code Worklist}
     */
    public Worklist getWorklist() {
        return worklist;
    }

    /**
     * Sets the current {@code Worklist}
     * 
     * @param worklist
     *        the new current {@code Worklist}
     */
    protected void setWorklist(Worklist worklist) {
        if (worklist == null) {
            throw new IllegalArgumentException("worklist must not be null");
        }

        this.worklist = worklist;
    }

    /**
     * Returns the current {@code BasicBlock}.
     * 
     * @return the current {@code BasicBlock}
     */
    public BasicBlock getCurrentBasicBlock() {
        return currentBasicBlock;
    }

    /**
     * Returns the index to the current {@code ElementaryBlock}.
     * 
     * @return the index to the current {@code ElementaryBlock}
     */
    public int getCurrentElementaryBlockIndex() {
        return eBlockIndex;
    }

    /**
     * Returns the current {@code ElementaryBlock}.
     * 
     * @return the current {@code ElementaryBlock} (can be {@code null})
     */
    public ElementaryBlock getCurrentElementaryBlock() {
        BasicBlock currentBB = getCurrentBasicBlock();
        if (currentBB == null) {
            return null;
        }

        int eBlockIndex = getCurrentElementaryBlockIndex();
        if (0 <= eBlockIndex && eBlockIndex < currentBB.getElementaryBlockCount()) {
            return currentBB.getElementaryBlock(getCurrentElementaryBlockIndex());
        }

        return null;
    }

    /**
     * Sets the current {@code BasicBlock}.
     * 
     * @param basicBlock
     *        the new current {@code BasicBlock}
     */
    public void setCurrentBasicBlock(BasicBlock basicBlock) {
        this.currentBasicBlock = basicBlock;
    }

    /**
     * Sets the index to the current {@code ElementaryBlock}.
     * 
     * @param eBlockIndex
     *        the index to the current {@code ElementaryBlock}
     */
    public void setCurrentElementaryBlockIndex(int eBlockIndex) {
        this.eBlockIndex = eBlockIndex;
    }

    /**
     * Sets the {@code Map} that assigns {@code BlockState}s to {@code AbstractBlock}s.
     * 
     * @param stateMap
     *        the {@code Map} that assigns {@code BlockState}s to {@code AbstractBlock}s
     */
    protected void setStateMap(Map<AbstractBlock, BlockState<E>> stateMap) {
        if (stateMap == null) {
            throw new IllegalArgumentException("stateMap must not be null");
        }

        this.stateMap = stateMap;
    }

    protected Map<AbstractBlock, BlockState<E>> getStateMap() {
        return stateMap;
    }

    /**
     * Sets the {@code Map} that assigns {@code LogicalColor}s to {@code BasicBlock}s.
     * 
     * @param colorMap
     *        the {@code Map} that assigns {@code LogicalColor}s to {@code BasicBlock}s
     */
    protected void setColorMap(Map<BasicBlock, LogicalColor> colorMap) {
        if (colorMap == null) {
            throw new IllegalArgumentException("colorMap must not be null");
        }

        this.colorMap = colorMap;
    }

    /**
     * Returns the {@code Map} that assigns {@code LogicalColor}s to {@code BasicBlock}s.
     * 
     * @return the {@code Map} that assigns {@code LogicalColor}s to {@code BasicBlock}s
     */
    protected Map<BasicBlock, LogicalColor> getColorMap() {
        return colorMap;
    }

}
