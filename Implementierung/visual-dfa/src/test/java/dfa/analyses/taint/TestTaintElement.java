package dfa.analyses.taint;

import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;

import dfa.analyses.LocalComparator;
import dfa.analyses.LocalMapElement;
import dfa.analyses.TaintElement;
import dfa.analyses.TaintElement.TaintState;
import dfa.analyses.TaintElement.Value;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.ShortType;
import soot.UnknownType;
import soot.VoidType;
import soot.jimple.internal.JimpleLocal;

public class TestTaintElement {

    @Test
    public void testEquals() {
        TaintElement te1 = new TaintElement();
        TaintElement te2 = new TaintElement(new TreeMap<JimpleLocal, Value>(new LocalComparator()));

        Assert.assertEquals(te1, te2);
        
        JimpleLocal l1 = new JimpleLocal("x", IntType.v());
        te1.setValue(l1, new Value(TaintState.CLEAN, false));
        
        Assert.assertNotEquals(te1, te2);
        
        String s = "wrong type";
        Assert.assertNotEquals(te1, s);
        Assert.assertNotEquals(te2, s);
    }
    
    @Test
    public void testValueEquals() {
        Value v1 = new Value(TaintState.CLEAN, false);
        Value v2 = new Value(TaintState.TAINTED, false);
        Assert.assertNotEquals(v1, v2);
        
        v2 = new Value(TaintState.CLEAN, true);
        Assert.assertNotEquals(v1, v2);

        String s = "wrong type";
        Assert.assertNotEquals(v1, s);
        Assert.assertNotEquals(v2, s);

        Assert.assertEquals(v1, new Value(TaintState.CLEAN, false));
        Assert.assertEquals(v2, new Value(TaintState.CLEAN, true));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetLocalNull() {
        TaintElement te = new TaintElement();
        te.setValue(null, new Value(TaintState.CLEAN, false));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetValueNull() {
        TaintElement te = new TaintElement();
        te.setValue(new JimpleLocal("x", IntType.v()), null);
    }
    
    @Test
    public void testSetValidLoaclAndValue() {
        TaintElement te = new TaintElement();
        JimpleLocal x = new JimpleLocal("x", IntType.v());
        Value xVal = new Value(TaintState.BOTTOM, false);
        
        te.setValue(x, xVal);
        
        Assert.assertFalse(te.getValue(x).wasViolated());
        Assert.assertEquals(TaintState.BOTTOM, te.getValue(x).getTaintState());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testValueContsructor01() {
        new Value(null, true);
    }
    
    @Test
    public void testValueContsructor02() {
        Value v1 = new Value(TaintState.TAINTED, true);
        Assert.assertTrue(v1.wasViolated());
        Assert.assertEquals(TaintState.TAINTED, v1.getTaintState());
    }
    
    
    @Test
    public void testStringRepresentation() {
        TaintElement te = new TaintElement();
        Assert.assertEquals("", te.getStringRepresentation());
        
        JimpleLocal x = new JimpleLocal("x", LongType.v());
        Value xVal = new Value(TaintState.BOTTOM, false);
        
        JimpleLocal y = new JimpleLocal("y", ByteType.v());
        Value yVal = new Value(TaintState.TAINTED, true);
        
        te.setValue(x, xVal);
        te.setValue(y, yVal);
        
        String BOTTOM = LocalMapElement.BOTTOM_SYMBOL;
        String expected = "x: " + BOTTOM + "\ny: tainted (v)";
        
        Assert.assertEquals(expected, te.getStringRepresentation());
    }
    
    @Test
    public void testLocalTypeAccepted() {
        Assert.assertTrue(TaintElement.isLocalTypeAccepted(BooleanType.v()));
        Assert.assertTrue(TaintElement.isLocalTypeAccepted(ByteType.v()));
        Assert.assertTrue(TaintElement.isLocalTypeAccepted(CharType.v()));
        Assert.assertTrue(TaintElement.isLocalTypeAccepted(ShortType.v()));
        Assert.assertTrue(TaintElement.isLocalTypeAccepted(IntType.v()));
        Assert.assertTrue(TaintElement.isLocalTypeAccepted(LongType.v()));
        Assert.assertTrue(TaintElement.isLocalTypeAccepted(FloatType.v()));
        Assert.assertTrue(TaintElement.isLocalTypeAccepted(DoubleType.v()));
        
        Assert.assertFalse(TaintElement.isLocalTypeAccepted(UnknownType.v()));
        Assert.assertFalse(TaintElement.isLocalTypeAccepted(VoidType.v()));
    }
    
}
