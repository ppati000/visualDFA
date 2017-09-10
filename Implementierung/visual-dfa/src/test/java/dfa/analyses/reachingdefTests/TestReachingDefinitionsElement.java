package dfa.analyses.reachingdefTests;

import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;

import dfa.analyses.LocalComparator;
import dfa.analyses.ReachingDefinitionsElement;
import dfa.analyses.ReachingDefinitionsElement.DefinitionSet;
import dfa.analyses.ReachingDefinitionsElement.DefinitionType;
import dfaTests.ValueHelper;
import soot.ByteType;
import soot.DoubleType;
import soot.IntType;
import soot.RefType;
import soot.Value;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.StringConstant;
import soot.jimple.internal.JimpleLocal;

public class TestReachingDefinitionsElement {
    
    @Test 
    public void testEquals() {
        ReachingDefinitionsElement rde1 = new ReachingDefinitionsElement();
        ReachingDefinitionsElement rde2 = new ReachingDefinitionsElement(new TreeMap<JimpleLocal, DefinitionSet>(new LocalComparator()));

        Assert.assertEquals(rde1, rde2);
        
        JimpleLocal l1 = new JimpleLocal("x", IntType.v());
        rde1.setValue(l1, new DefinitionSet(IntConstant.v(33)));
        
        Assert.assertNotEquals(rde1, rde2);
        
        rde2.setValue(l1, new DefinitionSet(IntConstant.v(33)));
        
        Assert.assertEquals(rde1, rde2);
        
        String s = "wrong type";
        Assert.assertNotEquals(rde1, s);
        Assert.assertNotEquals(rde1, s);
    }
    
    @Test
    public void testDefinitionEquals() {
        DefinitionSet def1 = new DefinitionSet(IntConstant.v(0));
        DefinitionSet def2 = new DefinitionSet(IntConstant.v(1));
        
        Assert.assertNotEquals(def1, def2);

        String s = "wrong type";
        Assert.assertNotEquals(def1, s);
        Assert.assertNotEquals(def2, s);

        Assert.assertEquals(ValueHelper.getDefinitionSet("0"), def1);
        Assert.assertEquals(ValueHelper.getDefinitionSet("1"), def2);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testSetLocalNull() {
        ReachingDefinitionsElement rde = new ReachingDefinitionsElement();
        rde.setValue(null, new DefinitionSet(StringConstant.v("text")));
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testSetValueNull() {
        ReachingDefinitionsElement rde = new ReachingDefinitionsElement();
        rde.setValue(new JimpleLocal("x", IntType.v()), null);
    }
    
    @Test
    public void testSetValidLoaclAndDefinition() {
        ReachingDefinitionsElement rde = new ReachingDefinitionsElement();
        JimpleLocal x = new JimpleLocal("x", RefType.v());
        DefinitionSet xDef = DefinitionSet.getBottom();
        
        rde.setValue(x, xDef);
        
        Assert.assertEquals(DefinitionSet.getBottom(), rde.getValue(x));
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testDefinitionContsructor02() {
        Value val = null;
        new DefinitionSet(val);
    }
    
    @Test
    public void testValueContsructor03() {
        DefinitionSet def = new DefinitionSet(LongConstant.v(1416));
        Assert.assertEquals(DefinitionType.DEFINITION, def.getDefType());
        Assert.assertTrue(def.isActualDefinition());
        Assert.assertEquals(ValueHelper.getDefinitionSet("1416"), def);
    }
    
    @Test
    public void testStringRepresentation() {
        ReachingDefinitionsElement rde = new ReachingDefinitionsElement();
        
        JimpleLocal a = new JimpleLocal("a", IntType.v());
        DefinitionSet aDef = new DefinitionSet(IntConstant.v(12));
        rde.setValue(a, aDef);
        
        JimpleLocal b = new JimpleLocal("b", ByteType.v());
        DefinitionSet bDef = new DefinitionSet(IntConstant.v(127));
        rde.setValue(b, bDef);
        
        JimpleLocal c = new JimpleLocal("c", DoubleType.v());
        DefinitionSet cDef = DefinitionSet.getBottom();
        rde.setValue(c, cDef);
        
        JimpleLocal s = new JimpleLocal("s", RefType.v());
        DefinitionSet sDef = new DefinitionSet(StringConstant.v("some \"string\""));
        rde.setValue(s, sDef);
        
        String expected = "a = {\n  12\n}\nb = {\n  127\n}\ns = {\n  some \"string\"\n}\n";
        Assert.assertEquals(expected, rde.getStringRepresentation());
    }

}
