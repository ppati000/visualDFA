package dfa.analyses.reachingdefTests;

import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;

import dfa.analyses.LocalComparator;
import dfa.analyses.ReachingDefinitionsElement;
import dfa.analyses.ReachingDefinitionsElement.Definition;
import dfa.analyses.ReachingDefinitionsElement.DefinitionType;
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
        ReachingDefinitionsElement rde2 = new ReachingDefinitionsElement(new TreeMap<JimpleLocal, Definition>(new LocalComparator()));

        Assert.assertEquals(rde1, rde2);
        
        JimpleLocal l1 = new JimpleLocal("x", IntType.v());
        rde1.setValue(l1, new Definition(IntConstant.v(33)));
        
        Assert.assertNotEquals(rde1, rde2);
        
        rde2.setValue(l1, new Definition(IntConstant.v(33)));
        
        Assert.assertEquals(rde1, rde2);
        
        String s = "wrong type";
        Assert.assertNotEquals(rde1, s);
        Assert.assertNotEquals(rde1, s);
    }
    
    @Test
    public void testDefinitionEquals() {
        Definition def1 = new Definition(IntConstant.v(0));
        Definition def2 = new Definition(IntConstant.v(1));
        
        Assert.assertNotEquals(def1, def2);

        String s = "wrong type";
        Assert.assertNotEquals(def1, s);
        Assert.assertNotEquals(def2, s);

        Assert.assertEquals(IntConstant.v(0), def1.getValue());
        Assert.assertEquals(IntConstant.v(1), def2.getValue());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetLocalNull() {
        ReachingDefinitionsElement rde = new ReachingDefinitionsElement();
        rde.setValue(null, new Definition(StringConstant.v("text")));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetValueNull() {
        ReachingDefinitionsElement rde = new ReachingDefinitionsElement();
        rde.setValue(new JimpleLocal("x", IntType.v()), null);
    }
    
    @Test
    public void testSetValidLoaclAndDefinition() {
        ReachingDefinitionsElement rde = new ReachingDefinitionsElement();
        JimpleLocal x = new JimpleLocal("x", RefType.v());
        Definition xDef = Definition.getBottom();
        
        rde.setValue(x, xDef);
        
        Assert.assertEquals(Definition.getBottom(), rde.getValue(x));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDefinitionContsructor01() {
        DefinitionType defType = null;
        new Definition(defType);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDefinitionContsructor02() {
        Value val = null;
        new Definition(val);
    }
    
    @Test
    public void testValueContsructor03() {
        Definition def = new Definition(LongConstant.v(1416));
        Assert.assertEquals(DefinitionType.DEFINITION, def.getDefType());
        Assert.assertTrue(def.isActualDefinition());
        Assert.assertEquals(LongConstant.v(1416), def.getValue());
    }
    
    @Test
    public void testStringRepresentation() {
        ReachingDefinitionsElement rde = new ReachingDefinitionsElement();
        
        JimpleLocal a = new JimpleLocal("a", IntType.v());
        Definition aDef = new Definition(IntConstant.v(12));
        rde.setValue(a, aDef);
        
        JimpleLocal b = new JimpleLocal("b", ByteType.v());
        Definition bDef = new Definition(IntConstant.v(127));
        rde.setValue(b, bDef);
        
        JimpleLocal c = new JimpleLocal("c", DoubleType.v());
        Definition cDef = new Definition(DefinitionType.BOTTOM);
        rde.setValue(c, cDef);
        
        JimpleLocal s = new JimpleLocal("s", RefType.v());
        Definition sDef = new Definition(StringConstant.v("some \"string\""));
        rde.setValue(s, sDef);
        
        String expected = "a = 12\nb = 127\ns = some \"string\"";
        Assert.assertEquals(expected, rde.getStringRepresentation());
    }

}
