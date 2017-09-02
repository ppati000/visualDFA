package dfa.analyses;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import dfa.analyses.ReachingDefinitionsElement.Definition;
import dfa.framework.BlockState;
import dfa.framework.Initializer;
import dfa.framework.SimpleBlockGraph;
import soot.Local;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.graph.Block;
import soot.util.Chain;

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

        for (Local l : locals) {
            if (! (l instanceof JimpleLocal)) {
              throw new IllegalStateException("no jimple local");
            }
            initialBottomMap.put((JimpleLocal) l, Definition.getBottom());
        }

        List<Block> blocks = blockGraph.getBlocks();

        ReachingDefinitionsElement defaultIn = new ReachingDefinitionsElement(initialBottomMap);
        BlockState<ReachingDefinitionsElement> defaultState =
                new BlockState<ReachingDefinitionsElement>(defaultIn, defaultIn);

        Map<Block, BlockState<ReachingDefinitionsElement>> initialMap =
                new HashMap<Block, BlockState<ReachingDefinitionsElement>>();

        for (Block b : blocks) {
                initialMap.put(b, defaultState);
        }

        return initialMap;
    }
}
