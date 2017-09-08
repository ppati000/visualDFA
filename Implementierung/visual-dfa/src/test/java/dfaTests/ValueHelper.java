package dfaTests;

import java.util.Set;
import java.util.TreeSet;

import dfa.analyses.ConstantBitsElement;
import dfa.analyses.ConstantFoldingElement;
import dfa.analyses.ReachingDefinitionsElement;
import dfa.analyses.ReachingDefinitionsElement.DefinitionSet;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;

public class ValueHelper {

    public static ConstantFoldingElement.Value getCfIntValue(int i) {
        return new ConstantFoldingElement.Value(IntConstant.v(i));
    }

    public static ConstantFoldingElement.Value getCfLongValue(long l) {
        return new ConstantFoldingElement.Value(LongConstant.v(l));
    }

    public static ReachingDefinitionsElement.DefinitionSet getRDDefInt(int i) {
        return new ReachingDefinitionsElement.DefinitionSet(IntConstant.v(i));
    }

    public static ReachingDefinitionsElement.DefinitionSet getRDDefLong(int l) {
        return new ReachingDefinitionsElement.DefinitionSet(LongConstant.v(l));
    }

    public static ConstantBitsElement.BitValueArray getCbIntBitValueArray(int i) {
        return new ConstantBitsElement.BitValueArray(IntConstant.v(i));
    }

    public static ConstantBitsElement.BitValueArray getCbLongBitValueArray(long l) {
        return new ConstantBitsElement.BitValueArray(LongConstant.v(l));
    }
    
    public static DefinitionSet getDefinitionSet(String... strings) {
    	Set<String> defs = new TreeSet<>();
    	for (String s : strings) {
    		defs.add(s);
    	}
    	return new DefinitionSet(defs);
    }

}
