package dfa.analysis.constantbitsTests;

import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;

import dfa.analyses.ConstantBitsElement;
import dfa.analyses.ConstantBitsElement.BitValue;
import dfa.analyses.ConstantBitsElement.BitValueArray;
import dfa.analyses.LocalComparator;
import soot.CharType;
import soot.IntType;
import soot.LongType;
import soot.ShortType;
import soot.jimple.ArithmeticConstant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.internal.JimpleLocal;

public class TestConstantBitsElement {

    @Test
    public void testEquals() {
        ConstantBitsElement cbe1 = new ConstantBitsElement();
        ConstantBitsElement cbe2 =
                new ConstantBitsElement(new TreeMap<JimpleLocal, BitValueArray>(new LocalComparator()));

        Assert.assertEquals(cbe1, cbe2);

        String s = "wrong type";
        Assert.assertNotEquals(cbe1, s);
        Assert.assertNotEquals(cbe2, s);
        
        JimpleLocal x = new JimpleLocal("x", ShortType.v());
        BitValueArray xVal = BitValueArray.getIntTop();
        
        cbe1.setValue(x, xVal);
        cbe2.setValue(x, xVal);

        Assert.assertEquals(cbe1, cbe2);

        JimpleLocal yInt = new JimpleLocal("y", IntType.v());
        JimpleLocal yLong = new JimpleLocal("y", LongType.v());
        
        BitValueArray yValInt = new BitValueArray(IntConstant.v(12));
        BitValueArray yValLong = new BitValueArray(LongConstant.v(12));
        
        cbe1.setValue(yInt, yValInt);
        cbe2.setValue(yLong, yValLong);
        
        Assert.assertNotEquals(cbe1, cbe2);
    }
    
    @Test
    public void testStringRepresentation() {
        JimpleLocal x = new JimpleLocal("x", IntType.v());
        JimpleLocal y = new JimpleLocal("y", CharType.v());
        JimpleLocal z = new JimpleLocal("z", LongType.v());
        
        BitValueArray xVal = new BitValueArray(IntConstant.v(-943));
        BitValueArray yVal = new BitValueArray(IntConstant.v(6));
        yVal.getBitValues()[0] = BitValue.TOP;
        
        BitValueArray zVal = BitValueArray.getLongTop();
        zVal.getBitValues()[0] = BitValue.BOTTOM;
        
        ConstantBitsElement cbe = new ConstantBitsElement();
        cbe.setValue(x, xVal);
        cbe.setValue(y, yVal);
        cbe.setValue(z, zVal);
        
        // @formatter:off
        String header = "00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 57 58 59 60 61 62 63";
        String xValStr = " 1  0  0  0  1  0  1  0  0  0  1  1  1  1  1  1  1  1  1  1  1  1  1  1  1  1  1  1  1  1  1  1";
        String yValStr = " T  1  1  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0";
        String zValStr = " B  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T  T";
        // @formatter:on
        
         String expected = header + "\nx =\n" + xValStr + "\ny =\n" + yValStr + "\nz =\n" + zValStr;
         Assert.assertEquals(expected, cbe.getStringRepresentation());
    }
    
    @Test
    public void testBitValueArray01() {
        Assert.assertFalse(BitValueArray.bitValueToBoolean(BitValue.ZERO));
        Assert.assertTrue(BitValueArray.bitValueToBoolean(BitValue.ONE));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testBitValueArray02() {
        Assert.assertFalse(BitValueArray.bitValueToBoolean(BitValue.BOTTOM));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testBitValueArray03() {
        Assert.assertFalse(BitValueArray.bitValueToBoolean(BitValue.TOP));
    }
    
    @Test
    public void testBitValueArrayConstructor01() {
        BitValueArray bottom32 = new BitValueArray(32, BitValue.BOTTOM);
        BitValueArray bottom64 = new BitValueArray(64, BitValue.BOTTOM);

        BitValueArray top32 = new BitValueArray(32, BitValue.TOP);
        BitValueArray top64 = new BitValueArray(64, BitValue.TOP);

        Assert.assertEquals(bottom32, BitValueArray.getIntBottom());
        Assert.assertEquals(bottom64, BitValueArray.getLongBottom());
        
        Assert.assertEquals(top32, BitValueArray.getIntTop());
        Assert.assertEquals(top64, BitValueArray.getLongTop());
        
        Assert.assertEquals(BitValueArray.getIntBottom(), BitValueArray.getBottom(32));
        Assert.assertEquals(BitValueArray.getLongBottom(), BitValueArray.getBottom(64));

        Assert.assertEquals(BitValueArray.getIntTop(), BitValueArray.getTop(32));
        Assert.assertEquals(BitValueArray.getLongTop(), BitValueArray.getTop(64));
    }

    @Test
    public void testBitValueArrayIsConst() {
        BitValueArray bva = new BitValueArray(IntConstant.v(-8360));
        Assert.assertTrue(bva.isConst());
        
        bva.getBitValues()[4] = BitValue.TOP;
        Assert.assertFalse(bva.isConst());
    }
    
    @Test
    public void testBitValueArrayIsPowerOfTwo() {
        Assert.assertTrue(new BitValueArray(IntConstant.v(128)).isPowerOfTwo());
        Assert.assertFalse(new BitValueArray(LongConstant.v(77777)).isPowerOfTwo());
        Assert.assertFalse(new BitValueArray(IntConstant.v(-Integer.MIN_VALUE)).isPowerOfTwo());

        BitValueArray bva = new BitValueArray(IntConstant.v(100));
        bva.getBitValues()[7] = BitValue.TOP;
        Assert.assertFalse(bva.isPowerOfTwo());
    }
    
    @Test
    public void testBitValueArrayGetPositionOfOne() {
        Assert.assertEquals(7, new BitValueArray(IntConstant.v(128)).getPositionOfOne());
        Assert.assertEquals(-1, new BitValueArray(LongConstant.v(-666)).getPositionOfOne());
        Assert.assertEquals(-1, BitValueArray.getLongTop().getPositionOfOne());
    }
    
    @Test
    public void testBitValueArrayIsZero() {
        Assert.assertTrue(new BitValueArray(IntConstant.v(0)).isZero());
        Assert.assertTrue(new BitValueArray(LongConstant.v(0)).isZero());
        Assert.assertFalse(new BitValueArray(IntConstant.v(-1)).isZero());
        Assert.assertFalse(new BitValueArray(LongConstant.v(17)).isZero());
        Assert.assertFalse(BitValueArray.getIntTop().isZero());
        Assert.assertFalse(BitValueArray.getLongBottom().isNotNegative());
    }
    
    @Test
    public void testBitValueArrayIsNotNegative() {
        Assert.assertTrue(new BitValueArray(IntConstant.v(0)).isNotNegative());
        Assert.assertTrue(new BitValueArray(LongConstant.v(0)).isNotNegative());
        Assert.assertTrue(new BitValueArray(IntConstant.v(10077)).isNotNegative());
        Assert.assertTrue(new BitValueArray(LongConstant.v(56)).isNotNegative());
        Assert.assertFalse(new BitValueArray(IntConstant.v(-1)).isNotNegative());
        Assert.assertFalse(new BitValueArray(LongConstant.v(-88)).isNotNegative());
        Assert.assertFalse(BitValueArray.getIntTop().isNotNegative());
        Assert.assertFalse(BitValueArray.getLongBottom().isNotNegative());
    }
    
    @Test
    public void testBitValueArrayGetConstant() {
        ArithmeticConstant c1 = IntConstant.v(1);
        ArithmeticConstant c2 = IntConstant.v(1864);
        ArithmeticConstant c3 = LongConstant.v(Integer.MAX_VALUE + 1);

        Assert.assertEquals(c1, new BitValueArray(c1).getConstant());
        Assert.assertEquals(c2, new BitValueArray(c2).getConstant());
        Assert.assertEquals(c3, new BitValueArray(c3).getConstant());
        
        Assert.assertEquals(null, BitValueArray.getLongTop().getConstant());
    }
    
    @Test
    public void testBitValueContainsBottom() {
        Assert.assertTrue(BitValueArray.getIntBottom().containsBOTTOM());
        Assert.assertFalse(new BitValueArray(IntConstant.v(55)).containsBOTTOM());
        Assert.assertFalse(BitValueArray.getLongTop().containsBOTTOM());
    }
    
    @Test
    public void testBitValueEquals() {
        BitValueArray bva1 = new BitValueArray(IntConstant.v(12));
        BitValueArray bva2 = new BitValueArray(LongConstant.v(12));
        BitValueArray bva3 = new BitValueArray(IntConstant.v(-5));
        BitValueArray bva4 = new BitValueArray(IntConstant.v(-5));
        bva4.getBitValues()[2] = BitValue.TOP;
        
        BitValueArray intTwelve = new BitValueArray(32, BitValue.ZERO);
        intTwelve.getBitValues()[2] = BitValue.ONE;
        intTwelve.getBitValues()[3] = BitValue.ONE;
        
        Assert.assertEquals(bva1, intTwelve);
        
        Assert.assertNotEquals(bva2, intTwelve);
        Assert.assertNotEquals(bva2, bva3);
        Assert.assertNotEquals(bva3, new String());
        Assert.assertNotEquals(bva3, BitValueArray.getIntTop());
    }
    
}
