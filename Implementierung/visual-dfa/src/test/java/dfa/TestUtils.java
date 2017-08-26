package dfa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Assert;

import dfa.analyses.ConstantFoldingElement.Value;
import dfa.analyses.LocalAliasMap;
import dfa.analyses.LocalMapElement;
import soot.Unit;
import soot.jimple.IntConstant;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.graph.Block;

public class TestUtils<V> {
    
    private boolean print = true;
    
    public Value getCfIntValue(int c) {
        return new Value(IntConstant.v(c));
    }
    
    public void assertLocalValue(Object expected, String name, LocalMapElement<V> e) {
        Set<Entry<JimpleLocal, V>> entries = e.getLocalMap().entrySet();
        for (Entry<JimpleLocal, V> entry : entries) {
            if (entry.getKey().getName().equals(name)) {
                Assert.assertEquals(expected, entry.getValue());
            }
        }
    }
    
    public void assertLocalValue(Object expected, String name, LocalAliasMap<V> aliasMap, LocalMapElement<V> e) {
        Assert.assertEquals(expected, aliasMap.getValueByAliasOrOriginalName(name, e.getLocalMap()));
    }
    
    public List<Unit> getUnitsFromBlock(Block block) {
        List<Unit> units = new ArrayList<Unit>();
        
        Iterator<Unit> unitIt = block.iterator();
        while (unitIt.hasNext()) {
            Unit u = unitIt.next();
            units.add(u);
        }
        
        return units;
    }
    
    public String unitsToString(List<Unit> units) {
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
    
    public String blockToString(Block block) {
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
    
    public void setPrint(boolean p) {
        this.print = p;
    }
    
    public void printInfo(Object o) {
        if (print) {
            System.out.println(o);
        }
    }
    

}
