package dfa.analyses;

import java.util.Collections;
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
import soot.ShortType;
import soot.Type;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.graph.Block;
import soot.util.Chain;

/**
 * A {@code ConstantFoldingInitializer} performs the initialization for a {@code ConstantFoldingAnalysis}.
 * 
 * @author Nils Jessen
 * @author Sebastian Rauch
 */
public class ConstantFoldingInitializer implements Initializer<ConstantFoldingElement> {

    private Map<Block, BlockState<ConstantFoldingElement>> initialMap;

    /**
     * Creates a {@code ConstantFoldingInitializer} for the given {@code SimpleBlockGraph}.
     * 
     * @param blockGraph
     *        the {@code SimpleBlockGraph} the analysis to initialize is executed on
     */
    public ConstantFoldingInitializer(SimpleBlockGraph blockGraph) {
        List<Block> heads = blockGraph.getHeads();
        if (heads.size() < 1) {
            throw new IllegalStateException("no entry point found");
        } else if (heads.size() > 1) {
            throw new IllegalStateException("multiple entry points found");
        }

        Map<JimpleLocal, ConstantFoldingElement.Value> initialBottomMap =
                new TreeMap<>(LocalMapElement.DEFAULT_COMPARATOR);
        Map<JimpleLocal, ConstantFoldingElement.Value> initialHeadMap =
                new TreeMap<>(LocalMapElement.DEFAULT_COMPARATOR);

        ConstantFoldingElement.Value nullInt = new ConstantFoldingElement.Value(IntConstant.v(0));
        ConstantFoldingElement.Value nullLong = new ConstantFoldingElement.Value(LongConstant.v(0));

        Chain<Local> locals = blockGraph.getBody().getLocals();
        for (Local l : locals) {
            if (!(l instanceof JimpleLocal)) {
                throw new IllegalStateException("no jimple local");
            }
            Type t = l.getType();
            if (!ConstantFoldingElement.isLocalTypeAccepted(t)) {
                continue;
            }
            if (t instanceof BooleanType || t instanceof ByteType || t instanceof CharType || t instanceof ShortType
                    || t instanceof IntType) {
                initialBottomMap.put((JimpleLocal) l, ConstantFoldingElement.Value.getBottom());
                initialHeadMap.put((JimpleLocal) l, nullInt);
            } else if (t instanceof LongType) {
                initialBottomMap.put((JimpleLocal) l, ConstantFoldingElement.Value.getBottom());
                initialHeadMap.put((JimpleLocal) l, nullLong);
            }
        }

        Block head = heads.get(0);
        List<Block> blocks = blockGraph.getBlocks();

        ConstantFoldingElement headIn = new ConstantFoldingElement(initialHeadMap);
        ConstantFoldingElement defaultInOut = new ConstantFoldingElement(initialBottomMap);

        BlockState<ConstantFoldingElement> headState = new BlockState<ConstantFoldingElement>(headIn, defaultInOut);
        BlockState<ConstantFoldingElement> defaultState =
                new BlockState<ConstantFoldingElement>(defaultInOut, defaultInOut);

        initialMap = new HashMap<Block, BlockState<ConstantFoldingElement>>();

        for (Block b : blocks) {
            if (b == head) {
                initialMap.put(b, headState);
            } else {
                initialMap.put(b, defaultState);
            }
        }
    }

    @Override
    public Map<Block, BlockState<ConstantFoldingElement>> getInitialStates() {
        return Collections.unmodifiableMap(initialMap);
    }

}
