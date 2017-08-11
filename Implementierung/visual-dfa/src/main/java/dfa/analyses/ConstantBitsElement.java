package dfa.analyses;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import dfa.analyses.ConstantBitsElement.BitValueArray;
import soot.jimple.ArithmeticConstant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.internal.JimpleLocal;

/**
 * @author Nils Jessen
 * 
 *         A {@code ConstantBitsElement} is a {@code LatticeElement} used by {@code ConstantBitsAnalysis}.
 *
 */
public class ConstantBitsElement extends LocalMapElement<BitValueArray> {

    /**
     * a {@code Comparator} to define an order on {@code JimpleLocal}s
     */
    // public static final LocalComparator COMPARATOR = new LocalComparator();

    // private SortedMap<JimpleLocal, BitValueArray> localMap;

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
        super();
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
        // TODO implement
        return null;
    }

    // TODO at the moment: String representation has lowest bit in the left
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

        // TODO use proper symbols (after testing)
        private static final String BOTTOM_SYMBOL = /* "\u22A5" */ "B";
        private static final String TOP_SYMBOL = /* "\u22A4" */ "T";

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
                val = Long.valueOf(((IntConstant) c).value);
                length = 32;
            } else {
                val = ((LongConstant) c).value;
                length = 64;
            }
            BitValue[] values = new BitValue[length];
            if (val == 0) {
                for (int j = 0; j < length; j++) {
                    values[j] = BitValue.ZERO;
                }
            } else if (val > 0) {
                values[length - 1] = BitValue.ZERO;
                for (int i = length - 2; i >= 0; i--) {
                    if (val >= (int) Math.pow(2, i)) {
                        values[i] = BitValue.ONE;
                        val -= (int) Math.pow(2, i);
                    } else {
                        values[i] = BitValue.ZERO;
                    }
                }
            } else {
                values[length - 1] = BitValue.ONE;
                for (int k = length - 2; k >= 0; k--) {
                    if (val <= (-1) * ((int) Math.pow(2, k))) {
                        values[k] = BitValue.ZERO;
                        val += (int) Math.pow(2, k);
                    } else {
                        values[k] = BitValue.ONE;
                    }
                }
            }
            this.bitValues = values;
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

        public BitValueArray(int length, BitValue init) {
            BitValue[] bottomArray = new BitValue[length];
            for (int i = 0; i < length; i++) {
                bottomArray[i] = init;
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

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof BitValueArray)) {
                return false;
            }

            BitValueArray val = (BitValueArray) o;
            return getBitValues().equals(val.getBitValues());
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
                case TOP:
                    sb.append("  ").append(TOP_SYMBOL);
                case ONE:
                    sb.append("  1");
                case ZERO:
                    sb.append("  0");
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
    enum BitValue {
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
