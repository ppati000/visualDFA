package dfa.analyses;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import dfa.framework.LatticeElement;
import soot.jimple.internal.JimpleLocal;

/**
 * A {@code LocalMapElement} is a {@code LatticeElement} that maps each {@code JimpleLocal} to a value of type
 * {@code V}.
 *
 * @param <V>
 *        the type of value
 *
 * @author Nils Jessen
 * @author Sebastian Rauch
 */
public abstract class LocalMapElement<V> implements LatticeElement, Cloneable {

    // note: the proper symbols for TOP and BOTTOM cause problems in some fonts
    public static final String BOTTOM_SYMBOL = "B"; // "\u22A5";
    public static final String TOP_SYMBOL = "T"; // "\u22A4";

    protected static final LocalComparator DEFAULT_COMPARATOR = new LocalComparator();

    protected SortedMap<JimpleLocal, V> localMap;

    /**
     * Creates a {@code LocalMapElement} with the given mapping and local-{@code Comparator}.
     * 
     * @param localMap
     *        a {@code Map} that maps a {@code JimpleLocal} to its corresponding value
     */
    public LocalMapElement(Map<JimpleLocal, V> localMap, Comparator<JimpleLocal> comparator) {
        if (localMap == null) {
            throw new IllegalArgumentException("localMap must not be null");
        }

        if (comparator == null) {
            throw new IllegalArgumentException("comparator must not be null");
        }

        this.localMap = new TreeMap<JimpleLocal, V>(comparator);
        this.localMap.putAll(localMap);
    }

    /**
     * Creates a {@code LocalMapElement} with an empty mapping.
     */
    public LocalMapElement() {
        this(new TreeMap<JimpleLocal, V>(), DEFAULT_COMPARATOR);
    }

    /**
     * Sets the value mapped to the given {@code JimpleLocal}.
     * 
     * @param local
     *        the {@code JimpleLocal} for which the value is set
     * @param val
     *        the value to set
     * 
     * @throws IllegalArgumentException
     *         if {@code local} or {@code val} is {@code null}
     */
    public void setValue(JimpleLocal local, V val) {
        if (val == null) {
            throw new IllegalArgumentException("value must not be null");
        }

        if (local == null) {
            throw new IllegalArgumentException("local must not be null");
        }

        localMap.put(local, val);
    }

    /**
     * Returns the value mapped to the given {@code JimpleLocal}.
     * 
     * @param local
     *        the {@code JimpleLocal} for which the value is retrieved
     * 
     * @return the value mapped to the given {@code JimpleLocal}
     * 
     * @throws IllegalArgumentException
     *         if there is no value mapping for {@code local}
     */
    public V getValue(JimpleLocal local) {
        return localMap.get(local);
    }

    /**
     * Returns a {@code Map} that maps a {@code JimpleLocal} to its corresponding value.
     * 
     * @return a {@code Map} that maps a {@code JimpleLocal} to its corresponding value
     */
    public Map<JimpleLocal, V> getLocalMap() {
        return localMap;
    }

    /* force subclasses to override this themselves */
    @Override
    public abstract boolean equals(Object o);

    /* force subclasses to override this themselves (to make sure the equals-hashCode-contract is fulfilled) */
    @Override
    public abstract int hashCode();

    @Override
    public abstract LocalMapElement<V> clone();

}
