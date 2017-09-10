package dfa.analyses;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import dfa.analyses.TaintElement.TaintState;
import dfa.framework.BlockState;
import dfa.framework.Initializer;
import dfa.framework.SimpleBlockGraph;
import soot.Local;
import soot.Type;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.graph.Block;
import soot.util.Chain;

/**
 * A {@code TaintInitializer} performs the initialization for a {@code TaintAnalysis}.
 * 
 * @author Sebastian Rauch
 */
public class TaintInitializer implements Initializer<TaintElement> {

    private Map<Block, BlockState<TaintElement>> initialMap;

    /**
     * Creates a {@code TaintInitializer} for the given {@code SimpleBlockGraph}.
     * 
     * @param blockGraph
     *        the {@code SimpleBlockGraph} the analysis to initialize is executed on
     */
    public TaintInitializer(SimpleBlockGraph blockGraph) {
        List<Block> heads = blockGraph.getHeads();
        if (heads.size() < 1) {
            throw new IllegalStateException("no entry point found");
        } else if (heads.size() > 1) {
            throw new IllegalStateException("multiply entry points found");
        }

        Map<JimpleLocal, TaintElement.Value> initialBottomMap = new TreeMap<>(LocalMapElement.DEFAULT_COMPARATOR);
        Map<JimpleLocal, TaintElement.Value> initialHeadMap = new TreeMap<>(LocalMapElement.DEFAULT_COMPARATOR);

        TaintElement.Value bottom = new TaintElement.Value(TaintState.BOTTOM, false);
        TaintElement.Value clean = new TaintElement.Value(TaintState.CLEAN, false);

        Chain<Local> locals = blockGraph.getBody().getLocals();
        for (Local l : locals) {
            if (!(l instanceof JimpleLocal)) {
                throw new IllegalStateException("no jimple local");
            }

            Type t = l.getType();

            if (!TaintElement.isLocalTypeAccepted(t)) {
                continue;
            }

            initialBottomMap.put((JimpleLocal) l, bottom);
            initialHeadMap.put((JimpleLocal) l, clean);
        }

        Block head = heads.get(0);
        List<Block> blocks = blockGraph.getBlocks();

        TaintElement headIn = new TaintElement(initialHeadMap);
        TaintElement defaultInOut = new TaintElement(initialBottomMap);

        BlockState<TaintElement> headState = new BlockState<TaintElement>(headIn, defaultInOut);
        BlockState<TaintElement> defaultState = new BlockState<TaintElement>(defaultInOut, defaultInOut);

        initialMap = new HashMap<Block, BlockState<TaintElement>>();

        for (Block b : blocks) {
            if (b == head) {
                initialMap.put(b, headState);
            } else {
                initialMap.put(b, defaultState);
            }
        }
    }

    @Override
    public Map<Block, BlockState<TaintElement>> getInitialStates() {
        return Collections.unmodifiableMap(initialMap);
    }

}
