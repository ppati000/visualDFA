package dfa.analyses;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import dfa.framework.BlockState;
import dfa.framework.Initializer;
import dfa.framework.SimpleBlockGraph;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.PrimType;
import soot.ShortType;
import soot.Type;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.graph.Block;
import soot.util.Chain;

/**
 * A {@code ConstantBitsInitializer} performs the initialization for a {@code ConstantBitsAnalysis}.
 * 
 * @author Nils Jessen
 */
public class ConstantBitsInitializer implements Initializer<ConstantBitsElement> {

    private SimpleBlockGraph blockGraph;

    /**
     * Creates a {@code ConstantBitsInitializer} for the given {@code SimpleBlockGraph}.
     * 
     * @param blockGraph
     *        the {@code SimpleBlockGraph} the analysis to initialize is executed on
     */
    public ConstantBitsInitializer(SimpleBlockGraph blockGraph) {
        this.blockGraph = blockGraph;
    }

    @Override
    public Map<Block, BlockState<ConstantBitsElement>> getInitialStates() {
        List<Block> heads = blockGraph.getHeads();
        if (heads.size() < 1) {
            throw new IllegalStateException("no entry point found");
        } else if (heads.size() > 1) {
            throw new IllegalStateException("multiply entry points found");
        }

        Chain<Local> locals = blockGraph.getBody().getLocals();

        Map<JimpleLocal, ConstantBitsElement.BitValueArray> initialBottomMap =
                new TreeMap<>(LocalMapElement.DEFAULT_COMPARATOR);
        Map<JimpleLocal, ConstantBitsElement.BitValueArray> initialHeadMap =
                new TreeMap<>(LocalMapElement.DEFAULT_COMPARATOR);

        ConstantBitsElement.BitValueArray zeroInt = new ConstantBitsElement.BitValueArray(IntConstant.v(0));
        ConstantBitsElement.BitValueArray zeroLong = new ConstantBitsElement.BitValueArray(LongConstant.v(0));

        for (Local l : locals) {
            if (!(l instanceof JimpleLocal)) {
                throw new IllegalStateException("no jimple local");
            }
            Type t = l.getType();
            if (!(t instanceof PrimType)) {
                continue;
            }
            if (t instanceof BooleanType || t instanceof ByteType || t instanceof CharType || t instanceof ShortType
                    || t instanceof IntType) {
                initialBottomMap.put((JimpleLocal) l, ConstantBitsElement.BitValueArray.getIntBottom());
                initialHeadMap.put((JimpleLocal) l, zeroInt);
            } else if (t instanceof LongType) {
                initialBottomMap.put((JimpleLocal) l, ConstantBitsElement.BitValueArray.getLongBottom());
                initialHeadMap.put((JimpleLocal) l, zeroLong);
            }
        }

        Block head = heads.get(0);
        List<Block> blocks = blockGraph.getBlocks();

        ConstantBitsElement headIn = new ConstantBitsElement(initialHeadMap);
        ConstantBitsElement defaultIn = new ConstantBitsElement(initialBottomMap);

        BlockState<ConstantBitsElement> headState = new BlockState<ConstantBitsElement>(headIn, defaultIn);
        BlockState<ConstantBitsElement> defaultState = new BlockState<ConstantBitsElement>(defaultIn, defaultIn);

        Map<Block, BlockState<ConstantBitsElement>> initialMap = new HashMap<Block, BlockState<ConstantBitsElement>>();

        for (Block b : blocks) {
            if (b == head) {
                initialMap.put(b, headState);
            } else {
                initialMap.put(b, defaultState);
            }
        }

        return initialMap;
    }

}
