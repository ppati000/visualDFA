package dfa.framework;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 * @author Sebastian Rauch
 *
 *         A {@code RandomWorklist} is a {@code Worklist} which chooses the next {@code BasicBlock} to be retrieved
 *         (pseudo-) randomly among the {@code BasicBlock}s on the {@code RandomWorklist}.
 */
public class RandomWorklist implements Worklist {

    private Random random;

    private LinkedList<BasicBlock> basicBlocks;

    /**
     * Creates an empty {@code RandomWorklist}.
     */
    public RandomWorklist() {
        this(new Random(), new LinkedList<BasicBlock>());
    }

    /*
     * @param random a {@code Random} that is used by the new {@code RandomWorklist} no copy is made, {@code random} is
     * used directly
     * 
     * @param basicBlocks the {@code BasicBlock}s that should be on the new {@code RandomWorklist} a copy of {@code
     * basicBlocks} is made and used by the new {@code RandomWorklist}
     */
    private RandomWorklist(Random random, LinkedList<BasicBlock> basicBlocks) {
        this.random = random;
        this.basicBlocks = new LinkedList<BasicBlock>(basicBlocks);
    }

    /**
     * {@inheritDoc}
     * 
     * If {@code basicBlock} is already on this {@code RandomWorklist}, this has no effect.
     * 
     * @throws {
     * @code IllegalArgumentException}, if {@code basicBlock} is {@code null}
     */
    public boolean add(BasicBlock basicBlock) {
        if (basicBlock == null) {
            throw new IllegalArgumentException("basicBlock is null");
        }

        if (contains(basicBlock)) {
            return false;
        }

        return basicBlocks.add(basicBlock);
    }

    /**
     * Retrieves and removes a (pseudo-) randomly chosen {@code BasicBlock} from this {@code RandomWorklist}.
     * 
     * @return a (pseudo-) randomly chosen {@code BasicBlock} from this {@code RandomWorklist} or {@code null} if this
     *         {@code RandomWorklist} is empty
     */
    public BasicBlock poll() {
        if (isEmpty()) {
            return null;
        }

        int index = random.nextInt(basicBlocks.size());
        return basicBlocks.remove(index);
    }

    /**
     * Retrieves a (pseudo-) randomly chosen {@code BasicBlock} from this {@code RandomWorklist}. The retrieved
     * {@code BasicBlock} is not removed form this {@code RandomWorklist}.
     * 
     * @return a (pseudo-) randomly chosen {@code BasicBlock} from this {@code RandomWorklist} or {@code null} if this
     *         {@code RandomWorklist} is empty
     */
    public BasicBlock peek() {
        if (isEmpty()) {
            return null;
        }

        int index = random.nextInt(basicBlocks.size());
        return basicBlocks.get(index);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return basicBlocks.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(BasicBlock basicBlock) {
        return basicBlocks.contains(basicBlock);
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<BasicBlock> iterator() {
        return basicBlocks.iterator();
    }

    /**
     * {@inheritDoc}
     */
    public Worklist clone() {
        return new RandomWorklist(new Random(), basicBlocks);
    }

}
