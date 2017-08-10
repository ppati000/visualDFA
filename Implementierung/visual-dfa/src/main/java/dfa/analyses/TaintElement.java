package dfa.analyses;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import dfa.framework.LatticeElement;
import soot.jimple.internal.JimpleLocal;

/**
 * @author Sebastian Rauch
 * 
 *         A {@code TaintElement} is a {@code LatticeElement} for Taint-Analysis.
 *
 */
public class TaintElement implements LatticeElement {

    public static final LocalComparator COMPARATOR = new LocalComparator();

    private SortedMap<JimpleLocal, Value> localMap;

    public TaintElement(Map<JimpleLocal, Value> localMap) {
        this.localMap = new TreeMap<>(COMPARATOR);
        this.localMap.putAll(localMap);
    }

    public TaintElement() {
        this(new TreeMap<JimpleLocal, Value>());
    }

    public void setValue(JimpleLocal local, Value val) {
        if (val == null) {
            throw new IllegalArgumentException("value must not be null");
        }

        if (local == null) {
            throw new IllegalArgumentException("local must not be null");
        }

        localMap.put(local, val);
    }

    public Value getValue(JimpleLocal local) {
        if (!localMap.containsKey(local)) {
            throw new IllegalArgumentException("local not found");
        }

        return localMap.get(local);
    }

    @Override
    public String getStringRepresentation() {
        // TODO Auto-generated method stub
        return null;
    }

    enum Value {
        TAINTED, CLEAN
    }

}
