package dfa.analyses;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import dfa.analyses.ConstantBitsElement.BitValueArray;
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
 * A {@code ConstantBitsElement} is a {@code LatticeElement} used by {@code ConstantBitsAnalysis}.
 * 
 * @author Nils Jessen
 */
public class ConstantBitsElement extends LocalMapElement<BitValueArray> {

    /**
     * Determines whether a certain type of Local is accepted (can be contained in) a {@code ConstantBitsElement}.
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
     * Creates a {@code ConstantBitsElement} with the given mapping.
     * 
     * @param localMap
     *        a {@code Map} that maps a {@code JimpleLocal} to its corresponding {@code BitValueArray}
     * 
     * @throws IllegalArgumentException
     *         if one of the {@code BitValueArray}s is not of the right size
     */
    public ConstantBitsElement(Map<JimpleLocal, BitValueArray> localMap) {
        super(localMap, LocalMapElement.DEFAULT_COMPARATOR);
        for (Map.Entry<JimpleLocal, BitValueArray> entry : localMap.entrySet()) {
            int l = entry.getValue().getLength();
            if (!(l == 32 || l == 64)) {
                throw new IllegalArgumentException("each BitValueArray must have 32 or 64 entries");
            }
        }
    }

    /**
     * Creates a {@code ConstantBitsElement} with an empty mapping.
     */
    public ConstantBitsElement() {
        super();
    }

    /**
     * Sets the {@code BitValueArray} mapped to the given {@code JimpleLocal}.
     * 
     * @param local
     *        the {@code JimpleLocal} for which the {@code BitValueArray} is set
     * @param val
     *        the {@code BitValueArray} to set
     * 
     * @throws IllegalArgumentException
     *         if {@code local} is {@code null} or {@code val} is null or not of the right size
     */
    public void setValue(JimpleLocal local, BitValueArray val) {
        if (val == null) {
            throw new IllegalArgumentException("val must not be null");
        }
        int l = val.getLength();
        if (!(l == 32 || l == 64)) {
            throw new IllegalArgumentException("val must have 32 or 64 entries");
        }

        if (local == null) {
            throw new IllegalArgumentException("local must not be null");
        }

        localMap.put(local, val);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ConstantBitsElement)) {
            return false;
        }

        ConstantBitsElement e = (ConstantBitsElement) o;
        return localMap.equals(e.getLocalMap());
    }

    @Override
    public int hashCode() {
        return Objects.hash(localMap);
    }

    @Override
    public LocalMapElement<BitValueArray> clone() {
        TreeMap<JimpleLocal, BitValueArray> newMap =
                new TreeMap<JimpleLocal, BitValueArray>(LocalMapElement.DEFAULT_COMPARATOR);
        for (JimpleLocal local : localMap.keySet()) {
            newMap.put(local, localMap.get(local));
        }
        return new ConstantBitsElement(newMap);
    }

    @Override
    public String getStringRepresentation() {
        StringBuilder sb = new StringBuilder();

        // add column numbers
        sb.append(
                "00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 57 58 59 60 61 62 63");

        Iterator<Map.Entry<JimpleLocal, BitValueArray>> entryIt = localMap.entrySet().iterator();
        Map.Entry<JimpleLocal, BitValueArray> entry;
        while (entryIt.hasNext()) {
            entry = entryIt.next();
            sb.append('\n');
            sb.append(entry.getKey().getName()).append(" =").append('\n').append(entry.getValue());
        }

        return sb.toString();
    }

    /**
     * @author Nils Jessen
     *
     *         A {@code BitValue} represents the BitValue of one bit of a {@code JimpleLocal}.
     *
     */
    public static class BitValueArray {
        private BitValue[] bitValues;

        public static final BitValueArray TOP = new BitValueArray(new BitValue[0]);
        public static final int INT_SIZE = 32;
        public static final int LONG_SIZE = 64;

        /**
         * Creates a {@code BitValueArray} representing the given {@code ArithmeticConstant}.
         * 
         * @param c
         *        the {@code ArithmeticConstant} for the new {@code BitValueArray}
         */
        public BitValueArray(ArithmeticConstant c) {
            if (c == null) {
                throw new IllegalArgumentException("c must not be null");
            }
            int length;
            long val;
            if (c instanceof IntConstant) {
                val = ((IntConstant) c).value;
                length = 32;
            } else {
                val = ((LongConstant) c).value;
                length = 64;
            }
            BitValue[] values = new BitValue[length];
            for (int j = 0; j < length; j++) {
                values[j] = booleanToBitValue((val & (1L << j)) != 0);
            }
            bitValues = values;
        }

        /**
         * Creates a {@code BitValueArray} representing the given array of {@code BitValue}s
         * 
         * @param bitValues
         *        the {@code BitValue}s for the new {@code BitValueArray}
         */
        public BitValueArray(BitValue[] bitValues) {
            if (bitValues == null) {
                throw new IllegalArgumentException("bitValues must not be null");
            }

            this.bitValues = bitValues;
        }

        /**
         * Creates a {@code BitValueArray} representing an array of the given length with every entry beeing the given
         * {@code BitValue}.
         * 
         * @param length
         *        length of the array
         * @param init
         *        value for the array
         */
        public BitValueArray(int length, BitValue init) {
            this.bitValues = new BitValue[length];
            for (int i = 0; i < length; i++) {
                this.bitValues[i] = init;
            }
        }

        /**
         * Converts a {@code boolean} into a {@code BitValue}.
         * 
         * @param b
         *        the {@code boolean} to be converted
         * @return a {@code BitValue} created from a {@code boolean}
         */
        public static BitValue booleanToBitValue(boolean b) {
            if (b) {
                return BitValue.ONE;
            } else {
                return BitValue.ZERO;
            }
        }

        /**
         * Converts a {@code BitValue} into a {@code boolean}.
         * 
         * @param val
         *        the {@code BitValue} to be converted
         * @return a {@code boolean} created from a {@code BitValue}
         */
        public static boolean bitValueToBoolean(BitValue val) {
            if (val == BitValue.TOP || val == BitValue.BOTTOM) {
                throw new IllegalArgumentException("val must be ZERO or ONE");
            } else if (val == BitValue.ONE) {
                return true;
            } else {
                return false;
            }
        }

        /**
         * Returns a symbolic TOP.
         * 
         * @return a symbolic TOP
         */
        public static BitValueArray getTop() {
            return TOP;
        }

        /**
         * Returns a {@code BitValueArray} of the length of {@code INT_SIZE} with only top.
         * 
         * @return a {@code BitValueArray} of the length of {@code INT_SIZE} with only top
         */
        public static BitValueArray getIntTop() {
            return new BitValueArray(INT_SIZE, BitValue.TOP);
        }

        /**
         * Returns a {@code BitValueArray} of the length of {@code LONG_SIZE} with only top.
         * 
         * @return a {@code BitValueArray} of the length of {@code LONG_SIZE} with only top
         */
        public static BitValueArray getLongTop() {
            return new BitValueArray(LONG_SIZE, BitValue.TOP);
        }

        /**
         * Returns a {@code BitValueArray} of the length of {@code INT_SIZE} with only bottom.
         * 
         * @return a {@code BitValueArray} of the length of {@code INT_SIZE} with only bottom
         */
        public static BitValueArray getIntBottom() {
            return new BitValueArray(INT_SIZE, BitValue.BOTTOM);
        }

        /**
         * Returns a {@code BitValueArray} of the length of {@code LONG_SIZE} with only bottom.
         * 
         * @return a {@code BitValueArray} of the length of {@code LONG_SIZE} with only bottom
         */
        public static BitValueArray getLongBottom() {
            return new BitValueArray(LONG_SIZE, BitValue.BOTTOM);
        }

        public static BitValueArray getTop(int length) {
            if (length == INT_SIZE) {
                return getIntTop();
            } else if (length == LONG_SIZE) {
                return getLongTop();
            } else {
                throw new IllegalArgumentException("length must be INT_SIZE or LONG_SIZE");
            }
        }

        public static BitValueArray getBottom(int length) {
            if (length == INT_SIZE) {
                return getIntBottom();
            } else if (length == LONG_SIZE) {
                return getLongBottom();
            } else {
                throw new IllegalArgumentException("length must be INT_SIZE or LONG_SIZE");
            }
        }

        /**
         * Returns the length of {@code bitValues}.
         * 
         * @return the length of {@code bitValues}
         */
        public int getLength() {
            return bitValues.length;
        }

        /**
         * Returns the array of {@code BitValue}s represented by this {@code BitValueArray}
         * 
         * @return the array of {@code BitValue}s represented by this {@code BitValueArray}
         */
        public BitValue[] getBitValues() {
            return bitValues;
        }

        /**
         * Returns if this {@code BitValueArray} represents a constant.
         * 
         * @return if this {@code BitValueArray} represents a constant
         */
        public boolean isConst() {
            for (BitValue val : bitValues) {
                if (val == BitValue.BOTTOM || val == BitValue.TOP) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Returns if the constant represented by this {@code BitValueArray} is a power of two.
         * 
         * @return if the constant represented by this {@code BitValueArray} is a power of two
         */
        public boolean isPowerOfTwo() {
            if (bitValues[bitValues.length - 1] != BitValue.ZERO) {
                return false;
            }
            int foundOnes = 0;
            for (BitValue bit : bitValues) {
                switch (bit) {
                case TOP:
                    return false;
                case BOTTOM:
                    return false;
                case ONE:
                    foundOnes++;
                    break;
                case ZERO: // ignore
                    break;
                }
            }
            return (foundOnes == 1);
        }

        /**
         * Returns the position of the only ONE-bit in {@code bitValues}.
         * 
         * @return the position of the only ONE-bit in {@code bitValues}
         */
        public int getPositionOfOne() {
            if (!isPowerOfTwo()) {
                return -1;
            }
            for (int i = 0; i < getLength(); i++) {
                if (bitValues[i] == BitValue.ONE) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * Returns if the constant represented by this {@code BitValueArray} is zero.
         * 
         * @return if the constant represented by this {@code BitValueArray} is azero
         */
        public boolean isZero() {
            for (BitValue bit : bitValues) {
                if (bit != BitValue.ZERO) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Returns the {@code ArithmeticConstant} this {@code BitValueArray} represents, if it represents a constant.
         * 
         * @return the {@code ArithmeticConstant} this {@code BitValueArray}, if it represents a constant
         */
        public ArithmeticConstant getConstant() {
            if (!isConst()) {
                return null;
            } else {
                long result = 0;
                int length = bitValues.length;
                for (int i = length - 1; i >= 0; i--) {
                    result = (result << 1) | (bitValueToBoolean(bitValues[i]) ? 1 : 0);
                }
                if (length == 32) {
                    return IntConstant.v((int) result);
                } else if (length == 64) {
                    return LongConstant.v(result);
                } else {
                    return null;
                }
            }
        }

        /**
         * Returns if the constant represented by this {@code BitValueArray} is not negative.
         * 
         * @return if the constant represented by this {@code BitValueArray} is not negative
         */
        public boolean isNotNegative() {
            return (bitValues[getLength() - 1] == BitValue.ZERO);
        }

        /**
         * Returns if this {@code BitValueArray} contains a bit with the {@code BitValue} BOTTOM.
         * 
         * @return if this {@code BitValueArray} contains a bit with the {@code BitValue} BOTTOM
         */
        public boolean containsBOTTOM() {
            for (BitValue bit : bitValues) {
                if (bit == BitValue.BOTTOM) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof BitValueArray)) {
                return false;
            }

            BitValueArray val = (BitValueArray) o;
            return Arrays.equals(getBitValues(), val.getBitValues());
        }

        @Override
        public int hashCode() {
            return Objects.hash((Object[]) bitValues);
        }

        @Override
        public String toString() {
            return bitsToString(getBitValues());
        }

        /**
         * Returns the String Representation of {@code bitValues}.
         * 
         * @param bitValues
         * @return the String Representation of {@code bitValues}
         */
        private String bitsToString(BitValue[] bitValues) {
            if (bitValues == null) {
                throw new IllegalArgumentException("bitValues must not be null");
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bitValues.length; i++) {
                switch (bitValues[i]) {
                case BOTTOM:
                    sb.append("  ").append(BOTTOM_SYMBOL);
                    break;
                case TOP:
                    sb.append("  ").append(TOP_SYMBOL);
                    break;
                case ONE:
                    sb.append("  1");
                    break;
                case ZERO:
                    sb.append("  0");
                    break;
                }
            }
            sb.deleteCharAt(0);
            return sb.toString();
        }
    }

    /**
     * @author Nils Jessen
     * 
     *         All possible values a bit can have.
     */
    public enum BitValue {
        /**
         * for bottom
         */
        BOTTOM,

        /**
         * for top
         */
        TOP,

        /**
         * for 1
         */
        ONE,

        /**
         * for 0
         */
        ZERO
    }

}
