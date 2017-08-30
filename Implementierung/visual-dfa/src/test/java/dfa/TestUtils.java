package dfa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Assert;

import dfa.analyses.LocalAliasMap;
import dfa.analyses.LocalMapElement;
import dfa.framework.SimpleBlockGraph;
import soot.Unit;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.graph.Block;

public class TestUtils<V> {

    private boolean print = true;

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

    public String simpleBlockGraphToString(SimpleBlockGraph bg) {
        List<Block> blocks = bg.getBlocks();

        int blockNumber = 1;
        Map<Block, Integer> blockNumberMap = new HashMap<>();
        for (Block b : blocks) {
            blockNumberMap.put(b, new Integer(blockNumber++));
        }

        StringBuilder sb = new StringBuilder();
        for (Block b : blocks) {
            sb.append("blockNumber: ").append(blockNumberMap.get(b)).append('\n');

            List<Block> preds = bg.getPredsOf(b);
            List<Integer> predNums = new ArrayList<>(preds.size());
            for (Block p : preds) {
                predNums.add(blockNumberMap.get(p));
            }
            sb.append("preds: ").append(listToString(predNums)).append('\n');

            sb.append(blockToString(b)).append('\n');

            List<Block> succs = bg.getSuccsOf(b);
            List<Integer> succNums = new ArrayList<>(succs.size());
            for (Block s : succs) {
                predNums.add(blockNumberMap.get(s));
            }
            sb.append("succs: ").append(listToString(succNums));
            sb.append('\n');
        }

        return sb.toString();
    }

    private String listToString(List<?> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        Iterator<? extends Object> it = list.iterator();
        if (it.hasNext()) {
            sb.append(it.next());
        }

        while (it.hasNext()) {
            sb.append(',').append(it.next());
        }

        sb.append("]");

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
