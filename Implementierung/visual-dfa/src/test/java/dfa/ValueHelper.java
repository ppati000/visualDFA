package dfa;

import dfa.analyses.ConstantFoldingElement;
import dfa.analyses.ReachingDefinitionsElement;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;

public class ValueHelper {

    public static ConstantFoldingElement.Value getCfIntValue(int i) {
        return new ConstantFoldingElement.Value(IntConstant.v(i));
    }

    public static ConstantFoldingElement.Value getCfLongValue(long l) {
        return new ConstantFoldingElement.Value(LongConstant.v(l));
    }
    
    public static ReachingDefinitionsElement.Definition getRDDefInt(int i) {
        return new ReachingDefinitionsElement.Definition(IntConstant.v(i));
    }
    
    public static ReachingDefinitionsElement.Definition getRDDefLong(int l) {
        return new ReachingDefinitionsElement.Definition(LongConstant.v(l));
    }

}
