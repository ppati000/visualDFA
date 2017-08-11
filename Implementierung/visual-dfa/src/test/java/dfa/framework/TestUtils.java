package dfa.framework;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;

import dfa.analyses.ConstantFoldingElement;
import dfa.analyses.LocalAliasMap;
import dfa.analyses.ConstantFoldingElement.Value;
import soot.Unit;
import soot.jimple.IntConstant;
import soot.toolkits.graph.Block;

public class TestUtils {
    
    public static Value getCfIntValue(int c) {
        return new Value(IntConstant.v(c));
    }
    
    public static void assertLocalValue(Value expected, String name, LocalAliasMap aliasMap, ConstantFoldingElement e) {
        Assert.assertEquals(expected, aliasMap.getValueByAliasOrOriginalName(name, e.getLocalMap()));
    }
    
    public static List<Unit> getUnitsFromBlock(Block block) {
        List<Unit> units = new ArrayList<Unit>();
        
        Iterator<Unit> unitIt = block.iterator();
        while (unitIt.hasNext()) {
            Unit u = unitIt.next();
            units.add(u);
        }
        
        return units;
    }
    
    public static String unitsToString(List<Unit> units) {
        if (units.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        Iterator<Unit> unitIt = units.iterator();
        sb.append(unitIt.next());
        while (unitIt.hasNext()) {
            sb.append('\n');
            sb.append(unitIt.next());
        }
        
        return sb.toString();
    }
    
    public static String blockToString(Block block) {
        Iterator<Unit> unitIt = block.iterator();
        if (!unitIt.hasNext()) {
            return new String();
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(unitIt.next());
        
        while (unitIt.hasNext()) {
            sb.append('\n').append(unitIt.next());
        }
        
        return sb.toString();
    }
    

}
