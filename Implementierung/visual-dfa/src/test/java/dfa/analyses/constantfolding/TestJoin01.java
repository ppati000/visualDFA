package dfa.analyses.constantfolding;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import dfa.analyses.ConstantFoldingElement;
import dfa.analyses.ConstantFoldingElement.Value;
import dfa.analyses.ConstantFoldingJoin;
import soot.IntType;
import soot.LongType;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.internal.JimpleLocal;

public class TestJoin01 {
    
    @Test(expected = IllegalArgumentException.class)
    public void testJoinEmpty() {
        Set<ConstantFoldingElement> emptySet = new HashSet<>();
        ConstantFoldingJoin join = new ConstantFoldingJoin();
        join.join(emptySet);
    }
    
    @Test
    public void testJoinSingle() {
        Set<ConstantFoldingElement> toJoin = new HashSet<>();
        
        ConstantFoldingElement cfe = new ConstantFoldingElement();
        
        ConstantFoldingJoin join = new ConstantFoldingJoin();
        toJoin.add(cfe);
        
        ConstantFoldingElement joinResult = join.join(toJoin);
        Assert.assertEquals(cfe, joinResult);
    }
    
    @Test
    public void testJoinMultiple() {
        JimpleLocal x = new JimpleLocal("x", IntType.v());
        JimpleLocal y = new JimpleLocal("y", LongType.v());
        JimpleLocal z = new JimpleLocal("z", LongType.v());
        
        Value xVal1 = Value.getBottom();
        Value xVal2 = new Value(IntConstant.v(-33));
        
        Value yVal1 = new Value(LongConstant.v(12));
        Value yVal2 = new Value(LongConstant.v(-88));
        
        Value zVal1 = new Value(LongConstant.v(72));
        Value zVal2 = new Value(LongConstant.v(72));

        ConstantFoldingElement cfe1 = new ConstantFoldingElement();
        cfe1.setValue(x, xVal1);
        cfe1.setValue(y, yVal1);
        cfe1.setValue(z, zVal1);
        
        ConstantFoldingElement cfe2 = new ConstantFoldingElement();
        cfe2.setValue(x, xVal2);
        cfe2.setValue(y, yVal2);
        cfe2.setValue(z, zVal2);
        
        Set<ConstantFoldingElement> toJoin = new HashSet<>();
        toJoin.add(cfe1);
        toJoin.add(cfe2);
        
        ConstantFoldingJoin join = new ConstantFoldingJoin();
        ConstantFoldingElement joinResult = join.join(toJoin);
        
        Assert.assertEquals(new Value(IntConstant.v(-33)), joinResult.getValue(x));
        Assert.assertEquals(Value.getTop(), joinResult.getValue(y));
        Assert.assertEquals(new Value(LongConstant.v(72)), joinResult.getValue(z));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testLocalsNotMatching01() {
        JimpleLocal x = new JimpleLocal("x", IntType.v());
        JimpleLocal y = new JimpleLocal("y", LongType.v());
        
        ConstantFoldingElement cfe1 = new ConstantFoldingElement();
        ConstantFoldingElement cfe2 = new ConstantFoldingElement();

        
        Value xVal1 = Value.getBottom();
        cfe1.setValue(x, xVal1);
        
        Value xVal2 = new Value(IntConstant.v(17));
        cfe2.setValue(x, xVal2);
        
        Value yVal2 = new Value(LongConstant.v(22));
        cfe2.setValue(y, yVal2);
        
        Set<ConstantFoldingElement> toJoin = new HashSet<>();
        toJoin.add(cfe1);
        toJoin.add(cfe2);
        
        ConstantFoldingJoin join = new ConstantFoldingJoin();
        join.join(toJoin);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testLocalsNotMatching02() {
        JimpleLocal x = new JimpleLocal("x", LongType.v());
        
        ConstantFoldingElement cfe1 = new ConstantFoldingElement();
        ConstantFoldingElement cfe2 = new ConstantFoldingElement();

        Value xVal2 = new Value(LongConstant.v(22));
        cfe2.setValue(x, xVal2);
        
        Set<ConstantFoldingElement> toJoin = new HashSet<>();
        toJoin.add(cfe1);
        toJoin.add(cfe2);
        
        ConstantFoldingJoin join = new ConstantFoldingJoin();
        join.join(toJoin);
    }

}
