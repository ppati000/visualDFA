package dfa.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import soot.Body;
import soot.Unit;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.BriefBlockGraph;

/**
 * @author Sebastian Rauch
 *
 *         A {@code SimpleBlockGraph} is a {@code BriefBlockGraph} that has at most one tail (the {@code List} returned
 *         by {@code getTails()} contains at most one {@code Block}). A {@code SimpleBlockGraph} also has at most one
 *         head (the {@code List} returned by {@code getHeads} contains at most one {@code Block}).
 */
public class SimpleBlockGraph extends BriefBlockGraph {

    private Block singleHead = null;
    private Block singleTail = null;

    /**
     * Creates a new {@code SimpleBlockGraph} from a given {@code Body}.
     * 
     * @param body
     *        the {@code Body} this {@code SimpleBlockGraph} is based on
     */
    public SimpleBlockGraph(Body body) {
        super(body);

        List<Block> newBlocks = new ArrayList<Block>(mBlocks.size() + 2);
        List<Block> oldHeads = getHeads();
        if (oldHeads.size() > 1) {
            singleHead = new EmptyBlock(body, -1, 0, this);
            singleHead.setPreds(new ArrayList<Block>());
            singleHead.setSuccs(oldHeads);

            for (Block oldHead : oldHeads) {
                List<Block> newPreds = new LinkedList<Block>(oldHead.getPreds());
                newPreds.add(singleHead);
                oldHead.setPreds(Collections.unmodifiableList(newPreds));
            }

            newBlocks.add(singleHead);

            mHeads = new ArrayList<Block>(1);
            mHeads.add(singleHead);
        }

        newBlocks.addAll(mBlocks);

        List<Block> oldTails = getTails();
        if (oldTails.size() > 1) {
            singleTail = new EmptyBlock(body, -1, 0, this);
            singleTail.setPreds(oldTails);
            singleTail.setSuccs(new ArrayList<Block>());

            for (Block oldTail : oldTails) {
                List<Block> newSuccs = new LinkedList<Block>(oldTail.getSuccs());
                newSuccs.add(singleTail);
                oldTail.setSuccs(Collections.unmodifiableList(newSuccs));
            }

            newBlocks.add(singleTail);

            mTails = new ArrayList<Block>(1);
            mTails.add(singleTail);
        }

        mBlocks = Collections.unmodifiableList(newBlocks);
    }

    /**
     * @author Sebastian Rauch
     * 
     *         An {@code EmptyBlock} is a {@code Block} with no {@code Unit}s.
     *
     */
    private static class EmptyBlock extends Block {

        public EmptyBlock(Body aBody, int aIndexInMethod, int aBlockLength, BlockGraph aBlockGraph) {
            super(null, null, aBody, aIndexInMethod, aBlockLength, aBlockGraph);
        }

        @Override
        public Iterator<Unit> iterator() {
            return new Iterator<Unit>() {

                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public Unit next() {
                    throw new NoSuchElementException();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        @Override
        public void insertBefore(Unit toInsert, Unit point) {
            throw new UnsupportedOperationException("this is an empty block");
        }

        @Override
        public void insertAfter(Unit toInsert, Unit point) {
            throw new UnsupportedOperationException("this is an empty block");
        }

        @Override
        public Unit getHead() {
            return null;
        }

        @Override
        public Unit getTail() {
            return null;
        }

        @Override
        public boolean remove(Unit item) {
            return false;
        }

        @Override
        public String toString() {
            StringBuffer strBuf = new StringBuffer();

            strBuf.append("Block " + getIndexInMethod() + ":" + System.getProperty("line.separator"));
            strBuf.append("[preds: ");
            if (getPreds() != null) {
                Iterator<Block> it = getPreds().iterator();
                while (it.hasNext()) {

                    strBuf.append(it.next().getIndexInMethod() + " ");
                }
            }
            strBuf.append("] [succs: ");
            if (getSuccs() != null) {
                Iterator<Block> it = getSuccs().iterator();
                while (it.hasNext()) {

                    strBuf.append(it.next().getIndexInMethod() + " ");
                }
            }

            return strBuf.toString();
        }

    }

}