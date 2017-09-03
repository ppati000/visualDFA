package dfa.analysis.constantbitsTests;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import dfa.analyses.ConstantBitsElement;
import dfa.analyses.ConstantBitsElement.BitValue;
import dfa.analyses.ConstantBitsElement.BitValueArray;
import dfa.analyses.ConstantBitsJoin;
import soot.ByteType;
import soot.IntType;
import soot.LongType;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.internal.JimpleLocal;

public class TestJoin {
    
    @Test(expected = IllegalArgumentException.class)
    public void testJoinEmpty() {
        Set<ConstantBitsElement> emptySet = new HashSet<>();
        ConstantBitsJoin join = new ConstantBitsJoin();
        join.join(emptySet);
    }
    
    @Test
    public void testJoinSingle() {
        ConstantBitsElement cbe = new ConstantBitsElement();
        
        JimpleLocal x = new JimpleLocal("x", IntType.v());
        BitValueArray bvaX = new BitValueArray(IntConstant.v(-6477));
        
        BitValueArray bvaY = new BitValueArray(bvaX.getBitValues());
        JimpleLocal y = new JimpleLocal("y", IntType.v());

        BitValueArray bvaZ = new BitValueArray(64, BitValue.TOP);
        JimpleLocal z = new JimpleLocal("z", LongType.v());
        
        cbe.setValue(x, bvaX);
        cbe.setValue(y, bvaY);
        cbe.setValue(z, bvaZ);
        
        ConstantBitsJoin join = new ConstantBitsJoin();
        ConstantBitsElement joinResult = join(join, cbe);
        
        Assert.assertEquals(cbe, joinResult);
    }
    
   @Test
    public void testJoinMultiple01() {
        JimpleLocal a = new JimpleLocal("a", IntType.v());
        JimpleLocal b = new JimpleLocal("b", ByteType.v());
        JimpleLocal c = new JimpleLocal("c", LongType.v());
        JimpleLocal d = new JimpleLocal("d", LongType.v());

        
        BitValueArray valA1 = new BitValueArray(IntConstant.v(-33));
        BitValueArray valA2 = new BitValueArray(32, BitValue.BOTTOM);
        
        BitValueArray valB1 = new BitValueArray(32, BitValue.TOP);
        BitValueArray valB2 = BitValueArray.getIntBottom();
        
        BitValueArray valC1 = new BitValueArray(LongConstant.v(7));
        BitValueArray valC2 = new BitValueArray(LongConstant.v(5));

        BitValueArray valD1 = new BitValueArray(LongConstant.v(-6655));
        BitValueArray valD2 = new BitValueArray(LongConstant.v(-6655));
        
        ConstantBitsElement cbe1 = new ConstantBitsElement();
        cbe1.setValue(a, valA1);
        cbe1.setValue(b, valB1);
        cbe1.setValue(c, valC1);
        cbe1.setValue(d, valD1);
        
        ConstantBitsElement cbe2 = new ConstantBitsElement();
        cbe2.setValue(a, valA2);
        cbe2.setValue(b, valB2);
        cbe2.setValue(c, valC2);
        cbe2.setValue(d, valD2);
        
        ConstantBitsJoin join = new ConstantBitsJoin();
        
        ConstantBitsElement joinResult = join(join, cbe1, cbe2);

        BitValueArray expectedC = new BitValueArray(LongConstant.v(5));
        expectedC.getBitValues()[1] = BitValue.TOP;
        
        Assert.assertEquals(valA1, joinResult.getValue(a));
        Assert.assertEquals(valB1, joinResult.getValue(b));
        Assert.assertEquals(expectedC, joinResult.getValue(c));
        Assert.assertEquals(valD1, joinResult.getValue(d));
    }
    
    
    
    
    private ConstantBitsElement join(ConstantBitsJoin join, ConstantBitsElement... toJoin) {
        Set<ConstantBitsElement> setToJoin = new HashSet<>();
        for (ConstantBitsElement e : toJoin) {
            setToJoin.add(e);
        }

        return join.join(setToJoin);
    }

}
