package dfa.analyses.reachingdefTests;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import dfa.analyses.ReachingDefinitionsElement;
import dfa.analyses.ReachingDefinitionsElement.Definition;
import dfa.analyses.ReachingDefinitionsJoin;
import soot.ByteType;
import soot.IntType;
import soot.RefType;
import soot.jimple.IntConstant;
import soot.jimple.StringConstant;
import soot.jimple.internal.JimpleLocal;


public class TestReachingDefinitionsJoin {
    
    @Test(expected = IllegalArgumentException.class)
    public void testJoinEmpty() {
        Set<ReachingDefinitionsElement> emptySet = new HashSet<>();
        ReachingDefinitionsJoin join = new ReachingDefinitionsJoin();
        join.join(emptySet);
    }
    
    @Test
    public void testJoinSingle01() {
        Set<ReachingDefinitionsElement> toJoin = new HashSet<>();
        
        ReachingDefinitionsElement rde = new ReachingDefinitionsElement();
        
        ReachingDefinitionsJoin join = new ReachingDefinitionsJoin();
        toJoin.add(rde);
        
        ReachingDefinitionsElement joinResult = join.join(toJoin);
        Assert.assertEquals(rde, joinResult);
    }
    
    @Test
    public void testJoinSingle02() {
        JimpleLocal x = new JimpleLocal("x", IntType.v());
        Definition xDef = new Definition(IntConstant.v(12));
        
        JimpleLocal s = new JimpleLocal("s", RefType.v());
        Definition sDef = new Definition(StringConstant.v("text"));
        
        ReachingDefinitionsElement rde = new ReachingDefinitionsElement();
        rde.setValue(x, xDef);
        rde.setValue(s, sDef);
        
        ReachingDefinitionsJoin join = new ReachingDefinitionsJoin();
        Set<ReachingDefinitionsElement> toJoin = new HashSet<>();
        toJoin.add(rde);
        
        ReachingDefinitionsElement joinResult = join.join(toJoin);
        Assert.assertEquals(rde, joinResult);
    }
    
    @Test
    public void testJoinMultiple01() {
        JimpleLocal x = new JimpleLocal("x", ByteType.v());
        Definition xDef1 = new Definition(IntConstant.v(12));
        Definition xDef2 = new Definition(IntConstant.v(12));
        
        JimpleLocal y = new JimpleLocal("y", ByteType.v());
        Definition yDef1 = new Definition(IntConstant.v(-42));
        Definition yDef2 = Definition.getBottom();
        
        JimpleLocal s = new JimpleLocal("s", RefType.v());
        Definition sDef1 = new Definition(StringConstant.v("text"));
        Definition sDef2 = new Definition(StringConstant.v("test"));
        
        JimpleLocal t = new JimpleLocal("t", RefType.v());
        Definition tDef1 = new Definition(StringConstant.v("same text"));
        Definition tDef2 = new Definition(StringConstant.v("same text"));

        ReachingDefinitionsElement rde1 = new ReachingDefinitionsElement();
        rde1.setValue(x, xDef1);
        rde1.setValue(y, yDef1);
        rde1.setValue(s, sDef1);
        rde1.setValue(t, tDef1);
        
        ReachingDefinitionsElement rde2 = new ReachingDefinitionsElement();
        rde2.setValue(x, xDef2);
        rde2.setValue(y, yDef2);
        rde2.setValue(s, sDef2);
        rde2.setValue(t, tDef2);
        
        Set<ReachingDefinitionsElement> toJoin = new HashSet<>();
        toJoin.add(rde1);
        toJoin.add(rde2);
        
        ReachingDefinitionsJoin join = new ReachingDefinitionsJoin();
        ReachingDefinitionsElement joinResult = join.join(toJoin);

        Assert.assertEquals(xDef1, joinResult.getValue(x));
        Assert.assertEquals(yDef1, joinResult.getValue(y));
        Assert.assertEquals(Definition.getTop(), joinResult.getValue(s));
        Assert.assertEquals(tDef1, joinResult.getValue(t));
    }
    
    @Test
    public void testJoinMultiple02() {
        JimpleLocal a = new JimpleLocal("a", IntType.v());
        Definition aDef1 = Definition.getTop();
        Definition aDef2 = new Definition(IntConstant.v(1));
        
        JimpleLocal b = new JimpleLocal("b", RefType.v());
        Definition bDef1 = Definition.getBottom();
        Definition bDef2 = new Definition(StringConstant.v("abc"));
        
        JimpleLocal c = new JimpleLocal("c", IntType.v());
        Definition cDef1 = new Definition(IntConstant.v(1));
        Definition cDef2 = Definition.getTop();
        
        JimpleLocal d = new JimpleLocal("d", ByteType.v());
        Definition dDef1 = new Definition(IntConstant.v(12));
        Definition dDef2 = Definition.getBottom();

        ReachingDefinitionsElement rde1 = new ReachingDefinitionsElement();
        rde1.setValue(a, aDef1);
        rde1.setValue(b, bDef1);
        rde1.setValue(c, cDef1);
        rde1.setValue(d, dDef1);
        
        ReachingDefinitionsElement rde2 = new ReachingDefinitionsElement();
        rde2.setValue(a, aDef2);
        rde2.setValue(b, bDef2);
        rde2.setValue(c, cDef2);
        rde2.setValue(d, dDef2);
        
        Set<ReachingDefinitionsElement> toJoin = new HashSet<>();
        toJoin.add(rde1);
        toJoin.add(rde2);
        
        ReachingDefinitionsJoin join = new ReachingDefinitionsJoin();
        ReachingDefinitionsElement joinResult = join.join(toJoin);
        
        Assert.assertEquals(Definition.getTop(), joinResult.getValue(a));
        Assert.assertEquals(bDef2, joinResult.getValue(b));
        Assert.assertEquals(Definition.getTop(), joinResult.getValue(c));
        Assert.assertEquals(dDef1, joinResult.getValue(d));
    }

}
