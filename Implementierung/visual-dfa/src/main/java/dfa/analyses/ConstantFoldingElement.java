package dfa.analyses;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import dfa.framework.LatticeElement;
import soot.jimple.ArithmeticConstant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.internal.JimpleLocal;

/**
 * @author Nils Jessen
 * @author Sebastian Rauch
 * 
 *         A {@code ConstantFoldingElement} is a {@code LatticeElement} used by {@code ConstantFoldingAnalysis}.
 *
 */
public class ConstantFoldingElement implements LatticeElement {
    
    /**
     * a {@code Comparator} to define an order on {@code JimpleLocal}s
     */
    public static final LocalComparator COMPARATOR = new LocalComparator();

    private SortedMap<JimpleLocal, Value> localMap;

    /**
     * Creates a {@code ConstantFoldingElement} with the given mapping.
     * 
     * @param localMap
     *        a {@code Map} that maps a {@code JimpleLocal} to its corresponding {@code Value}
     */
    public ConstantFoldingElement(Map<JimpleLocal, Value> localMap) {
        this.localMap = new TreeMap<>(COMPARATOR);
        this.localMap.putAll(localMap);
    }

    /**
     * Creates a {@code ConstantFoldingElement} with an empty mapping.
     */
    public ConstantFoldingElement() {
        this(new TreeMap<JimpleLocal, Value>());
    }

    /**
     * Sets the {@code Value} mapped to the given {@code JimpleLocal}.
     * 
     * @param local
     *        the {@code JimpleLocal} for which the {@code Value} is set
     * @param val
     *        the {@code Value} to set
     * 
     * @throws IllegalArgumentException
     *         if {@code local} or {@code val} is {@code null}
     */
    public void setValue(JimpleLocal local, Value val) {
        if (val == null) {
            throw new IllegalArgumentException("value must not be null");
        }

        if (local == null) {
            throw new IllegalArgumentException("local must not be null");
        }

        localMap.put(local, val);
    }

    /**
     * Returns the {@code Value} mapped to the given {@code JimpleLocal}.
     * 
     * @param local
     *        the {@code JimpleLocal} for which the {@code Value} is retrieved
     * 
     * @return the {@code Value} mapped to the given {@code JimpleLocal}
     * 
     * @throws IllegalArgumentException
     *         if there is no {@code Value} mapping for {@code local}
     */
    public Value getValue(JimpleLocal local) {
        if (!localMap.containsKey(local)) {
            throw new IllegalArgumentException("local not found");
        }

        return localMap.get(local);
    }

    /**
     * Returns a {@code Map} that maps a {@code JimpleLocal} to its corresponding {@code Value}.
     * 
     * @return a {@code Map} that maps a {@code JimpleLocal} to its corresponding {@code Value}
     */
    public Map<JimpleLocal, Value> getLocalMap() {
        return localMap;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ConstantFoldingElement)) {
            return false;
        }

        ConstantFoldingElement e = (ConstantFoldingElement) o;
        return localMap.equals(e.getLocalMap());
    }

    @Override
    public String getStringRepresentation() {
        StringBuilder sb = new StringBuilder();

        Iterator<Map.Entry<JimpleLocal, Value>> entryIt = localMap.entrySet().iterator();
        Map.Entry<JimpleLocal, Value> entry;
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
     * @author Sebastian Rauch
     * 
     *         A {@code Comparator} that compares {@code JimpleLocal}s by their name ({@code getName()}).
     */
    static class LocalComparator implements Comparator<JimpleLocal> {

        @Override
        public int compare(JimpleLocal l1, JimpleLocal l2) {
            return l1.getName().compareTo(l2.getName());
        }

    }

    /**
     * @author Nils Jessen
     * @author Sebastian Rauch
     *
     * A {@code Value} represents the value of a {@code JimpleLocal}.
     *
     */
    static class Value {
        private static final Value BOTTOM = new Value(ValueType.BOTTOM);
        private static final Value TOP = new Value(ValueType.TOP);

        private ValueType type;
        private ArithmeticConstant constant;

        /**
         * Creates a {@code Value} representing the given {@code ArithmeticConstant}.
         * 
         * @param constant the {@code ArithmeticConstant} for the new {@code Value}
         */
        public Value(ArithmeticConstant constant) {
            if (constant == null) {
                throw new IllegalArgumentException("type must be specified");
            }

            this.type = ValueType.CONST;
            this.constant = constant;
        }

        private Value(ValueType type) {
            this.type = type;
            this.constant = null;
        }
        
        /**
         * Returns the bottom-{@code Value}.
         * 
         * @return the bottom-{@code Value}
         */
        public static Value getBottom() {
            return BOTTOM;
        }

        /**
         * Returns the top-{@code Value}.
         * 
         * @return the top-{@code Value}
         */
        public static Value getTop() {
            return TOP;
        }

        /**
         * Returns the {@code ValueType} of this {@code Value}.
         * 
         * @return the {@code ValueType} of this {@code Value}
         */
        public ValueType getType() {
            return type;
        }

        /**
         * Returns whether this {@code Value} represents a constant.
         * 
         * @return whether this {@code Value} represents a constant
         */
        public boolean isConst() {
            return type == ValueType.CONST;
        }

        /**
         * Returns the {@code ArithmeticConstant} represented by this {@code Value} or {@code null} if no constant.
         * 
         * @return the {@code ArithmeticConstant} represented by this {@code Value} or {@code null} if no constant
         */
        public ArithmeticConstant getConstant() {
            return constant;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Value)) {
                return false;
            }

            Value val = (Value) o;
            if (isConst() && val.isConst()) {
                return getConstant().equals(val.getConstant());
            }
            return getType() == val.getType();
        }

        @Override
        public int hashCode() {
            return Objects.hash(constant);
        }

        @Override
        public String toString() {
            switch (getType()) {
            case BOTTOM:
                return "\u22A5";
            case TOP:
                return "\u22A4";
            case CONST:
                return constToString(getConstant());
            default:
                throw new IllegalStateException();
            }
        }

        private String constToString(ArithmeticConstant c) {
            if (c instanceof IntConstant) {
                return String.valueOf(((IntConstant) c).value);
            } else if (c instanceof LongConstant) {
                return String.valueOf(((LongConstant) c).value);
            } else {
                throw new IllegalStateException();
            }
        }
    }

    /**
     * @author Nils Jessen
     * @author Sebastian Rauch
     * 
     *         Distinguishes constants from top and bottom.
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
         * for constants
         */
        CONST
    }

}
