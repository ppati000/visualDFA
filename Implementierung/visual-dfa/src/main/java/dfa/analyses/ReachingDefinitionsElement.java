package dfa.analyses;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import dfa.analyses.ReachingDefinitionsElement.Definition;
import soot.jimple.internal.JimpleLocal;

/**
 * @author Nils Jessen
 * 
 *         A {@code ReachingDefinitionsElement} is a {@code LatticeElement} used by {@code ReachingDefinitionsAnalysis}.
 *
 */
public class ReachingDefinitionsElement extends LocalMapElement<Definition> {

    public ReachingDefinitionsElement(Map<JimpleLocal, Definition> localMap) {
        super(localMap, LocalMapElement.DEFAULT_COMPARATOR);
    }

    /**
     * Creates a {@code ReachingDefinitionsElement} with an empty mapping.
     */
    public ReachingDefinitionsElement() {
        super();
    }

    /**
     * Sets the {@code Definition} mapped to the given {@code JimpleLocal}.
     * 
     * @param local
     *        the {@code JimpleLocal} for which the {@code Definition} is set
     * @param def
     *        the {@code Definition} to set
     * @throws IllegalArgumentException
     *         if {@code local} is {@code null} or {@code def} is null
     */
    public void setDefinition(JimpleLocal local, Definition def) {
        if (def == null) {
            throw new IllegalArgumentException("der must not be null!");
        }
        if (local == null) {
            throw new IllegalArgumentException("local must not be null!");
        }
        localMap.put(local, def);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ReachingDefinitionsElement)) {
            return false;
        }

        ReachingDefinitionsElement e = (ReachingDefinitionsElement) o;
        return localMap.equals(e.getLocalMap());
    }

    @Override
    public int hashCode() {
        return Objects.hash(localMap);
    }

    @Override
    public LocalMapElement clone() {
        return new ReachingDefinitionsElement(getLocalMap());
    }

    @Override
    public String getStringRepresentation() {
        StringBuilder sb = new StringBuilder();

        Iterator<Map.Entry<JimpleLocal, Definition>> entryIt = localMap.entrySet().iterator();
        Map.Entry<JimpleLocal, Definition> entry;
        if (entryIt.hasNext()) {
            entry = entryIt.next();
            sb.append(entry.getKey().getName()).append(" = ").append(entry.getValue());
        }

        while (entryIt.hasNext()) {
            entry = entryIt.next();
            sb.append('\n');
            sb.append(entry.getKey().getName()).append(" = ").append(entry.getValue());
        }

        return sb.toString();
    }

    /**
     * @author Nils Jessen
     * 
     *         The "Value" of the Reaching Definition Analysis
     */
    public static class Definition {
        private static final Definition BOTTOM = new Definition(DefinitionType.BOTTOM);
        private static final Definition TOP = new Definition(DefinitionType.TOP);

        private soot.Value val;
        private DefinitionType type;

        public Definition(DefinitionType type) {
            this.type = type;
        }

        public Definition(soot.Value val) {
            this.val = val;
        }

        public soot.Value getValue() {
            return val;
        }

        public static Definition getBottom() {
            return BOTTOM;
        }

        public static Definition getTop() {
            return TOP;
        }

        public boolean isActualDefinition() {
            return (this.type == DefinitionType.DEFINITION);
        }
    }

    /**
     * @author Nils Jessen
     * 
     *         Distinguishes actual Definitions from top and bottom.
     */
    enum DefinitionType {
        /**
         * for bottom
         */
        BOTTOM,

        /**
         * for top
         */
        TOP,

        /**
         * for actual defintions
         */
        DEFINITION
    }
}
