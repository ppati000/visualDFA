package dfa.analyses.constantfoldingTests;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import dfa.analyses.ConstantFoldingFactory;
import dfa.framework.DFADirection;

public class TestConstantFoldingFactory {
    
    private static ConstantFoldingFactory factory;
   
    @BeforeClass
    public static void setUp() {
        factory = new ConstantFoldingFactory();
    }
    
    @Test
    public void basicTests() {
        Assert.assertEquals(DFADirection.FORWARD, factory.getDirection());
        Assert.assertEquals("Constant-Folding", factory.getName());
    }

}
