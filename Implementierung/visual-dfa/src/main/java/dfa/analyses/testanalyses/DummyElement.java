package dfa.analyses.testanalyses;

import dfa.framework.LatticeElement;

/**
 * A dummy element for {@code DummyAnalysis} which can be either TOP, BOTTOM or SOMETHING.
 * 
 * @author Patrick Petrovic
 */
public class DummyElement implements LatticeElement {

    public final ValueType type;

    /**
     * Creates a {@code DummyElement} with an empty mapping.
     */
    public DummyElement(ValueType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DummyElement)) {
            return false;
        }

        DummyElement e = (DummyElement) o;
        return e.type == this.type;
    }

    @Override
    public String getStringRepresentation() {
        switch (type) {
        case BOTTOM:
            return "\u22A5";
        case TOP:
            return "\u22A4";
        case SOMETHING:
            return "something";
        default:
            throw new IllegalStateException();
        }
    }

    /**
     * @author Patrick Petrovic
     *
     *         Distinguishes TOP, BOTTOM and SOMETHING for the dummy analysis.
     */
    enum ValueType {
        /**
         * for bottom
         */
        BOTTOM,

        /**
         * for top
         */
        TOP,

        /**
         * for dummy analysis join and transition
         */
        SOMETHING
    }

}
