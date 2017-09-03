package dfa.analysis.constantbitsTests;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import dfa.analyses.ConstantBitsFactory;
import dfa.framework.DFADirection;

public class TestConstantBitsFactory {
    
    private static ConstantBitsFactory factory;
    
    @BeforeClass
    public static void setUp() {
        factory = new ConstantBitsFactory();
    }
    
    @Test
    public void basicTests() {
        Assert.assertEquals(DFADirection.FORWARD, factory.getDirection());
        Assert.assertEquals("Constant-Bits", factory.getName());
    }

}
