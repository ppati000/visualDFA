package dfa.analyses.taintTests;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import dfa.analyses.TaintElement;
import dfa.analyses.TaintJoin;
import dfa.analyses.TaintElement.TaintState;
import dfa.analyses.TaintElement.Value;
import soot.DoubleType;
import soot.IntType;
import soot.LongType;
import soot.jimple.internal.JimpleLocal;

public class TestJoin {
    
    @Test(expected = IllegalArgumentException.class)
    public void testJoinEmpty() {
        Set<TaintElement> emptySet = new HashSet<>();
        TaintJoin join = new TaintJoin();
        join.join(emptySet);
    }
    
    @Test
    public void testJoinSingle() {
        Set<TaintElement> toJoin = new HashSet<>();
        
        TaintElement te = new TaintElement();
        
        TaintJoin join = new TaintJoin();
        toJoin.add(te);
        
        TaintElement joinResult = join.join(toJoin);
        Assert.assertEquals(te, joinResult);
    }
    
    @Test
    public void testJoinMultiple() {
        JimpleLocal x = new JimpleLocal("x", IntType.v());
        JimpleLocal y = new JimpleLocal("y", LongType.v());
        JimpleLocal z = new JimpleLocal("z", DoubleType.v());
        JimpleLocal b = new JimpleLocal("b", IntType.v());

        
        Value xVal1 = new Value(TaintState.CLEAN, false);
        Value xVal2 = new Value(TaintState.BOTTOM, false);
        
        Value yVal1 = new Value(TaintState.TAINTED, false);
        Value yVal2 = new Value(TaintState.CLEAN, true);
        
        Value zVal1 = new Value(TaintState.CLEAN, true);
        Value zVal2 = new Value(TaintState.CLEAN, false);
        
        Value bVal1 = new Value(TaintState.BOTTOM, false);
        Value bVal2 = new Value(TaintState.CLEAN, false);
        
        TaintElement te1 = new TaintElement();
        te1.setValue(x, xVal1);
        te1.setValue(y, yVal1);
        te1.setValue(z, zVal1);
        te1.setValue(b, bVal1);
        
        TaintElement te2 = new TaintElement();
        te2.setValue(x, xVal2);
        te2.setValue(y, yVal2);
        te2.setValue(z, zVal2);
        te2.setValue(b, bVal2);

        
        Set<TaintElement> toJoin = new HashSet<>();
        toJoin.add(te1);
        toJoin.add(te2);
        
        TaintJoin join = new TaintJoin();
        TaintElement joinResult = join.join(toJoin);
        
        Assert.assertEquals(new Value(TaintState.CLEAN,  false), joinResult.getValue(x));
        Assert.assertEquals(new Value(TaintState.TAINTED,  true), joinResult.getValue(y));
        Assert.assertEquals(new Value(TaintState.CLEAN,  true), joinResult.getValue(z));
        Assert.assertEquals(new Value(TaintState.CLEAN,  false), joinResult.getValue(b));

    }

}
