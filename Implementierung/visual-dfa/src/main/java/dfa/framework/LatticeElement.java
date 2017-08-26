package dfa.framework;

/**
 * A {@code LatticeElement} represents the elements of a lattice used for dataflow-analysis.
 * 
 * @author Sebastian Rauch
 *
 */
public interface LatticeElement {

    /**
     * Returns a string-representation of this {@code LatticeElement}.
     * 
     * @return a string-representation of this {@code LatticeElement}
     */
    String getStringRepresentation();

}
