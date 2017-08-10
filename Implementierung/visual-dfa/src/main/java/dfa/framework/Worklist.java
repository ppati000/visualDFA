package dfa.framework;

import java.util.Iterator;

/**
 * @author Sebastian Rauch 
 * 
 *         A {@code Worklist} contains {@code BasicBlock}s that need to be processed.
 */
public interface Worklist extends Cloneable, Iterable<BasicBlock> {

    /**
     * Adds a {@code BasicBlock} to this {@code Worklist}.
     * 
     * @param basicBlock
     *        the {@code BasicBlock} to be added
     * @return {@code true} iff {@code basicBlock} could be added
     */
    boolean add(BasicBlock basicBlock);

    /**
     * Retrieves and removes the next {@code BasicBlock} from this {@code Worklist}.
     * 
     * @return the next {@code BasicBlock} from this {@code Worklist}
     */
    BasicBlock poll();

    /**
     * Retrieves the next {@code BasicBlock} from this {@code Worklist}. The retrieved {@code BasicBlock} is not removed
     * from this {@code Worklist}.
     * 
     * @return the next {@code BasicBlock} from this {@code Worklist}
     */
    BasicBlock peek();

    /**
     * Returns {@code true} iff there are no more {@code BasicBlock}s on this {@code Worklist}.
     * 
     * @return {@code true} iff there are no more {@code BasicBlock}s on this {@code Worklist}
     */
    boolean isEmpty();

    /**
     * returns {@code true} iff this {@code Worklist} contains a {@code BasicBlock} equal to {@code basicBlock}.
     * 
     * @param basicBlock
     *        the {@code BasicBlock} to be checked whether it is on this {@code Worklist}
     * @return {@code true} iff this {@code Worklist} contains a {@code BasicBlock} equal to {@code basicBlock}
     */
    boolean contains(BasicBlock basicBlock);

    /**
     * Returns an {@code Iterator} over this {@code Worklist}.
     * 
     * @return an {@code Iterator} over this {@code Worklist}
     */
    Iterator<BasicBlock> iterator();

    /**
     * Makes a copy of this {@code Worklist}.
     * 
     * @return a copy of this {@code Worklist}
     */
    Worklist clone();

}
