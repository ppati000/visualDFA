package dfa.analyses.reachingdefTests;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import dfa.analyses.ReachingDefinitionsElement;
import dfa.analyses.ReachingDefinitionsElement.DefinitionSet;
import dfa.analyses.ReachingDefinitionsJoin;
import dfaTests.ValueHelper;
import soot.ByteType;
import soot.IntType;
import soot.RefType;
import soot.jimple.IntConstant;
import soot.jimple.StringConstant;
import soot.jimple.internal.JimpleLocal;


public class TestReachingDefinitionsJoin {
    
    @Test (expected = IllegalArgumentException.class)
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
        DefinitionSet xDef = new DefinitionSet(IntConstant.v(12));
        
        JimpleLocal s = new JimpleLocal("s", RefType.v());
        DefinitionSet sDef = new DefinitionSet(StringConstant.v("text"));
        
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
        DefinitionSet xDef1 = new DefinitionSet(IntConstant.v(12));
        DefinitionSet xDef2 = new DefinitionSet(IntConstant.v(12));
        
        JimpleLocal y = new JimpleLocal("y", ByteType.v());
        DefinitionSet yDef1 = new DefinitionSet(IntConstant.v(-42));
        DefinitionSet yDef2 = DefinitionSet.getBottom();
        
        JimpleLocal s = new JimpleLocal("s", RefType.v());
        DefinitionSet sDef1 = new DefinitionSet(StringConstant.v("text"));
        DefinitionSet sDef2 = new DefinitionSet(StringConstant.v("test"));
        
        JimpleLocal t = new JimpleLocal("t", RefType.v());
        DefinitionSet tDef1 = new DefinitionSet(StringConstant.v("same text"));
        DefinitionSet tDef2 = new DefinitionSet(StringConstant.v("same text"));

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
        Assert.assertEquals(ValueHelper.getDefinitionSet("test", "text"), joinResult.getValue(s));
        Assert.assertEquals(tDef1, joinResult.getValue(t));
    }
    
    @Test
    public void testJoinMultiple02() {
        JimpleLocal a = new JimpleLocal("a", IntType.v());
        DefinitionSet aDef1 = DefinitionSet.getBottom();
        DefinitionSet aDef2 = new DefinitionSet(IntConstant.v(1));
        
        JimpleLocal b = new JimpleLocal("b", RefType.v());
        DefinitionSet bDef1 = DefinitionSet.getBottom();
        DefinitionSet bDef2 = new DefinitionSet(StringConstant.v("abc"));
        
        JimpleLocal c = new JimpleLocal("c", IntType.v());
        DefinitionSet cDef1 = new DefinitionSet(IntConstant.v(1));
        DefinitionSet cDef2 = new DefinitionSet(IntConstant.v(17));
        
        JimpleLocal d = new JimpleLocal("d", ByteType.v());
        DefinitionSet dDef1 = new DefinitionSet(IntConstant.v(12));
        DefinitionSet dDef2 = new DefinitionSet(IntConstant.v(12));;

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
        
        Assert.assertEquals(ValueHelper.getDefinitionSet("1"), joinResult.getValue(a));
        Assert.assertEquals(bDef2, joinResult.getValue(b));
        Assert.assertEquals(ValueHelper.getDefinitionSet("1", "17"), joinResult.getValue(c));
        Assert.assertEquals(dDef1, joinResult.getValue(d));
    }

}
