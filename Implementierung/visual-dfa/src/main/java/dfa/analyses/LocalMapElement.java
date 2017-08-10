package dfa.analyses;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import dfa.framework.LatticeElement;
import soot.jimple.internal.JimpleLocal;

/**
 * @author Nils Jessen
 * @author Sebastian Rauch
 * 
 *         A {@code LocalMapElement} is a {@code LatticeElement} that maps each {@code JimpleLocal} to a value of type
 *         {@code V}.
 *
 * @param <V>
 *        the type of value
 */
public abstract class LocalMapElement<V> implements LatticeElement {

    protected static final LocalComparator DEFAULT_COMPARATOR = new LocalComparator();

    protected SortedMap<JimpleLocal, V> localMap;

    /**
     * Creates a {@code LocalMapElement} with the given mapping and local-{@code Comparator}.
     * 
     * @param localMap
     *        a {@code Map} that maps a {@code JimpleLocal} to its corresponding value
     */
    public LocalMapElement(Map<JimpleLocal, V> localMap, Comparator<JimpleLocal> comparator) {
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
        if (!localMap.containsKey(local)) {
            throw new IllegalArgumentException("local not found");
        }

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
    public String getStringRepresentation() {
        StringBuilder sb = new StringBuilder();

        Iterator<Map.Entry<JimpleLocal, V>> entryIt = localMap.entrySet().iterator();
        Map.Entry<JimpleLocal, V> entry;
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

}
