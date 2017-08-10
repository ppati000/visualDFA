package dfa.framework;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Sebastian Rauch 
 * 
 *         A {@code NaiveWorklist} is a {@code Worklist} which chooses the next {@code BasicBlock} to be retrieved
 *         according to the first-in-first-out principle. This means that {@code BasicBlock}s are retrieved in the order
 *         they are inserted. A {@code BasicBlock} cannot be inserted if it is still on the {@code NaiveWorklist}.
 */
public class NaiveWorklist implements Worklist {

    private LinkedList<BasicBlock> basicBlocks;

    /**
     * Creates an empty {@code NaiveWorklist}.
     */
    public NaiveWorklist() {
        basicBlocks = new LinkedList<BasicBlock>();
    }

    private NaiveWorklist(LinkedList<BasicBlock> basicBlocks) {
        this.basicBlocks = new LinkedList<BasicBlock>(basicBlocks);
    }

    /**
     * {@inheritDoc}
     * 
     * If {@code basicBlock} is already on this {@code NaiveWorklist}, this has no effect.
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
     * Retrieves and removes the next {@code BasicBlock} according to the first-in-first-out principle.
     * 
     * @return the next {@code BasicBlock} according to the first-in-first-out principle or {@code null} if this
     *         {@code NaiveWorklist} is empty
     */
    public BasicBlock poll() {
        return isEmpty() ? null : basicBlocks.removeFirst();
    }

    /**
     * Retrieves the next {@code BasicBlock} according to the first-in-first-out principle. The retrieved
     * {@code BasicBlock} is not removed from this {@code NaiveWorklist}.
     * 
     * @return the next {@code BasicBlock} according to the first-in-first-out principle or {@code null} if this
     *         {@code NaiveWorklist} is empty
     */
    public BasicBlock peek() {
        return isEmpty() ? null : basicBlocks.getFirst();
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

    @Override
    public Worklist clone() {
        return new NaiveWorklist(basicBlocks);
    }

}
