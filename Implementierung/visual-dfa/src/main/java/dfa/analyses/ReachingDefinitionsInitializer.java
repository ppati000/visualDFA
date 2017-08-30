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
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.graph.Block;
import soot.util.Chain;
import dfa.analyses.ReachingDefinitionsElement.Definition;

/**
 * @author Nils Jessen
 * 
 *         A {@code ReachingDefinitionsInitializer} performs the initialization for a
 *         {@code ReachingDefinitionsAnalysis}.
 */
public class ReachingDefinitionsInitializer implements Initializer<ReachingDefinitionsElement> {

    private SimpleBlockGraph blockGraph;

    /**
     * Creates a {@code ReachingDefinitionsInitializer} for the given {@code SimpleBlockGraph}.
     * 
     * @param blockGraph
     *        the {@code SimpleBlockGraph} the analysis to initialize is executed on
     */
    public ReachingDefinitionsInitializer(SimpleBlockGraph blockGraph) {
        this.blockGraph = blockGraph;
    }

    @Override
    public Map<Block, BlockState<ReachingDefinitionsElement>> getInitialStates() {
        List<Block> heads = blockGraph.getHeads();
        if (heads.size() < 1) {
            throw new IllegalStateException("no entry point found");
        } else if (heads.size() > 1) {
            throw new IllegalStateException("multiple entry points found");
        }

        Chain<Local> locals = blockGraph.getBody().getLocals();

        Map<JimpleLocal, ReachingDefinitionsElement.Definition> initialBottomMap =
                new TreeMap<>(LocalMapElement.DEFAULT_COMPARATOR);
        Map<JimpleLocal, ReachingDefinitionsElement.Definition> initialHeadMap =
                new TreeMap<>(LocalMapElement.DEFAULT_COMPARATOR);

        for (Local l : locals) {
            Type t = l.getType();
            if (!(t instanceof PrimType)) {
                continue;
            }
            if (!(l instanceof JimpleLocal)) {
                throw new IllegalStateException("no jimple local");
            }
            if (t instanceof BooleanType || t instanceof ByteType || t instanceof CharType || t instanceof ShortType
                    || t instanceof IntType) {
                initialBottomMap.put((JimpleLocal) l, ReachingDefinitionsElement.Definition.getBottom());
                initialHeadMap.put((JimpleLocal) l, new Definition(soot.jimple.IntConstant.v(0)));
            } else if (t instanceof LongType) {
                initialBottomMap.put((JimpleLocal) l, ReachingDefinitionsElement.Definition.getBottom());
                initialHeadMap.put((JimpleLocal) l, new Definition(soot.jimple.LongConstant.v(0)));
            }
        }

        Block head = heads.get(0);
        List<Block> blocks = blockGraph.getBlocks();

        ReachingDefinitionsElement headIn = new ReachingDefinitionsElement(initialHeadMap);
        ReachingDefinitionsElement defaultIn = new ReachingDefinitionsElement(initialBottomMap);

        BlockState<ReachingDefinitionsElement> headState =
                new BlockState<ReachingDefinitionsElement>(headIn, defaultIn);
        BlockState<ReachingDefinitionsElement> defaultState =
                new BlockState<ReachingDefinitionsElement>(defaultIn, defaultIn);

        Map<Block, BlockState<ReachingDefinitionsElement>> initialMap =
                new HashMap<Block, BlockState<ReachingDefinitionsElement>>();

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
