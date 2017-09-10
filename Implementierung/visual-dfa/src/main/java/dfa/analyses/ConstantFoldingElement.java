package dfa.analyses;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import dfa.analyses.ConstantFoldingElement.Value;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.IntType;
import soot.LongType;
import soot.ShortType;
import soot.Type;
import soot.jimple.ArithmeticConstant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.internal.JimpleLocal;

/**
 * A {@code ConstantFoldingElement} is a {@code LatticeElement} used by {@code ConstantFoldingAnalysis}.
 *
 * @author Nils Jessen
 * @author Sebastian Rauch
 */
public class ConstantFoldingElement extends LocalMapElement<Value> {

    /**
     * Determines whether a certain type of local is accepted (can be contained in) a {@code ConstantFoldingElement}.
     * 
     * @param type
     *        the {@code Type} in question
     * @return {@code true} if the given {@code Type} is accepted, {@code false} otherwise
     */
    public static boolean isLocalTypeAccepted(Type type) {
        return type instanceof BooleanType || type instanceof ByteType || type instanceof CharType
                || type instanceof ShortType || type instanceof IntType || type instanceof LongType;
    }

    /**
     * Creates a {@code LocalMapElement} with the given mapping and local-{@code Comparator}.
     * 
     * @param localMap
     *        a {@code Map} that maps a {@code JimpleLocal} to its corresponding value
     */
    public ConstantFoldingElement(Map<JimpleLocal, Value> localMap) {
        super(localMap, LocalMapElement.DEFAULT_COMPARATOR);
    }

    /**
     * Creates a {@code LocalMapElement} with an empty mapping.
     */
    public ConstantFoldingElement() {
        super();
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
    public int hashCode() {
        return Objects.hash(localMap);
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
     *         A {@code Value} represents the value of a {@code JimpleLocal}.
     *
     */
    public static class Value {
        private static final Value BOTTOM = new Value(ValueType.BOTTOM);
        private static final Value TOP = new Value(ValueType.TOP);

        private ValueType type;
        private ArithmeticConstant constant;

        /**
         * Creates a {@code Value} representing the given {@code ArithmeticConstant}.
         * 
         * @param constant
         *        the {@code ArithmeticConstant} for the new {@code Value}
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
                return LocalMapElement.BOTTOM_SYMBOL;
            case TOP:
                return LocalMapElement.TOP_SYMBOL;
            case CONST:
                return constToString(getConstant());
            default:
                return ""; // ignore
            }
        }

        private String constToString(ArithmeticConstant c) {
            if (c instanceof IntConstant) {
                return String.valueOf(((IntConstant) c).value);
            } else if (c instanceof LongConstant) {
                return String.valueOf(((LongConstant) c).value);
            } else {
                throw new IllegalStateException("unknown ArithmeticConstant");
            }
        }

    }

    @Override
    public ConstantFoldingElement clone() {
        return new ConstantFoldingElement(getLocalMap());
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
