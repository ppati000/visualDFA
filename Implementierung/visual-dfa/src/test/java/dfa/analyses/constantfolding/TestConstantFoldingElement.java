package dfa.analyses.constantfolding;

import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;

import dfa.analyses.ConstantFoldingElement;
import dfa.analyses.ConstantFoldingElement.Value;
import dfa.analyses.LocalComparator;
import dfa.analyses.LocalMapElement;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.RefType;
import soot.ShortType;
import soot.UnknownType;
import soot.VoidType;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.internal.JimpleLocal;

public class TestConstantFoldingElement {

    @Test
    public void testEquals() {
        ConstantFoldingElement cfe1 = new ConstantFoldingElement();
        ConstantFoldingElement cfe2 =
                new ConstantFoldingElement(new TreeMap<JimpleLocal, Value>(new LocalComparator()));

        Assert.assertEquals(cfe1, cfe2);

        String s = "wrong type";
        Assert.assertNotEquals(cfe1, s);
        Assert.assertNotEquals(cfe2, s);
    }

    @Test
    public void testValueEquals() {
        Value v1 = new Value(IntConstant.v(0));
        Value v2 = new Value(LongConstant.v(0));
        Assert.assertNotEquals(v1, v2);

        String s = "wrong type";
        Assert.assertNotEquals(v1, s);
        Assert.assertNotEquals(v2, s);

        Assert.assertEquals(v1, new Value(IntConstant.v(0)));
        Assert.assertEquals(v2, new Value(LongConstant.v(0)));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetLocalNull() {
        ConstantFoldingElement cfe = new ConstantFoldingElement();
        cfe.setValue(null, new Value(IntConstant.v(0)));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetValueNull() {
        ConstantFoldingElement cfe = new ConstantFoldingElement();
        cfe.setValue(new JimpleLocal("x", IntType.v()), null);
    }
    
    @Test
    public void testSetValidLoaclAndValue() {
        ConstantFoldingElement cfe = new ConstantFoldingElement();
        JimpleLocal x = new JimpleLocal("x", IntType.v());
        Value xVal = new Value(IntConstant.v(13));
        
        cfe.setValue(x, xVal);
        
        Assert.assertTrue(cfe.getValue(x).isConst());
        Assert.assertEquals(IntConstant.v(13), cfe.getValue(x).getConstant());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueContsructor01() {
        new Value(null);
    }

    @Test
    public void testValueContsructor02() {
        Value v1 = new Value(LongConstant.v(0));
        Assert.assertTrue(v1.isConst());
        Assert.assertEquals(v1.getConstant(), LongConstant.v(0));
    }

    @Test
    public void testStringRepresentation() {
        ConstantFoldingElement cfe = new ConstantFoldingElement();
        Assert.assertEquals("", cfe.getStringRepresentation());

        JimpleLocal x = new JimpleLocal("x", IntType.v());
        Value xVal = new Value(IntConstant.v(12));

        JimpleLocal y = new JimpleLocal("y", LongType.v());
        Value yVal = new Value(LongConstant.v(-654));

        JimpleLocal b = new JimpleLocal("b", IntType.v());
        Value bVal = ConstantFoldingElement.Value.getBottom();

        JimpleLocal t = new JimpleLocal("t", IntType.v());
        Value tVal = ConstantFoldingElement.Value.getTop();

        cfe.setValue(x, xVal);
        cfe.setValue(y, yVal);
        cfe.setValue(b, bVal);
        cfe.setValue(t, tVal);

        
        String BOTTOM = LocalMapElement.BOTTOM_SYMBOL;
        String TOP = LocalMapElement.TOP_SYMBOL;
        String expectedString =
                "b = " + BOTTOM + "\nt = " + TOP + "\nx = 12\ny = -654";

        Assert.assertEquals(expectedString, cfe.getStringRepresentation());
    }

    @Test
    public void testLocalTypeAccepted() {
        Assert.assertTrue(ConstantFoldingElement.isLocalTypeAccepted(BooleanType.v()));
        Assert.assertTrue(ConstantFoldingElement.isLocalTypeAccepted(ByteType.v()));
        Assert.assertTrue(ConstantFoldingElement.isLocalTypeAccepted(CharType.v()));
        Assert.assertTrue(ConstantFoldingElement.isLocalTypeAccepted(ShortType.v()));
        Assert.assertTrue(ConstantFoldingElement.isLocalTypeAccepted(IntType.v()));
        Assert.assertTrue(ConstantFoldingElement.isLocalTypeAccepted(LongType.v()));

        Assert.assertFalse(ConstantFoldingElement.isLocalTypeAccepted(FloatType.v()));
        Assert.assertFalse(ConstantFoldingElement.isLocalTypeAccepted(DoubleType.v()));
        Assert.assertFalse(ConstantFoldingElement.isLocalTypeAccepted(RefType.v()));
        Assert.assertFalse(ConstantFoldingElement.isLocalTypeAccepted(UnknownType.v()));
        Assert.assertFalse(ConstantFoldingElement.isLocalTypeAccepted(VoidType.v()));
    }

}
