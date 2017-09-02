package dfa.analyses;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import dfa.analyses.ReachingDefinitionsElement.Definition;
import dfa.framework.UnsupportedValueException;
import soot.ArrayType;
import soot.Local;
import soot.Type;
import soot.Value;
import soot.jimple.AddExpr;
import soot.jimple.AndExpr;
import soot.jimple.ArrayRef;
import soot.jimple.CastExpr;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.ClassConstant;
import soot.jimple.CmpExpr;
import soot.jimple.CmpgExpr;
import soot.jimple.CmplExpr;
import soot.jimple.DivExpr;
import soot.jimple.DoubleConstant;
import soot.jimple.DynamicInvokeExpr;
import soot.jimple.EqExpr;
import soot.jimple.FloatConstant;
import soot.jimple.GeExpr;
import soot.jimple.GtExpr;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceOfExpr;
import soot.jimple.IntConstant;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.JimpleValueSwitch;
import soot.jimple.LeExpr;
import soot.jimple.LengthExpr;
import soot.jimple.LongConstant;
import soot.jimple.LtExpr;
import soot.jimple.MethodHandle;
import soot.jimple.MulExpr;
import soot.jimple.NeExpr;
import soot.jimple.NegExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.NullConstant;
import soot.jimple.OrExpr;
import soot.jimple.ParameterRef;
import soot.jimple.RemExpr;
import soot.jimple.ShlExpr;
import soot.jimple.ShrExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.StringConstant;
import soot.jimple.SubExpr;
import soot.jimple.ThisRef;
import soot.jimple.UshrExpr;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.XorExpr;
import soot.jimple.internal.JimpleLocal;

/**
 * @author Nils Jessen
 * 
 *         A {@code ReachingDefinitionsElement} is a {@code LatticeElement} used by {@code ReachingDefinitionsAnalysis}.
 *
 */
public class ReachingDefinitionsElement extends LocalMapElement<Definition> {

    public ReachingDefinitionsElement(Map<JimpleLocal, Definition> localMap) {
        super(localMap, LocalMapElement.DEFAULT_COMPARATOR);
    }

    /**
     * Creates a {@code ReachingDefinitionsElement} with an empty mapping.
     */
    public ReachingDefinitionsElement() {
        super();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ReachingDefinitionsElement)) {
            return false;
        }

        ReachingDefinitionsElement e = (ReachingDefinitionsElement) o;
        return localMap.equals(e.getLocalMap());
    }

    @Override
    public int hashCode() {
        return Objects.hash(localMap);
    }

    @Override
    public LocalMapElement<Definition> clone() {
        return new ReachingDefinitionsElement(getLocalMap());
    }

    @Override
    public String getStringRepresentation() {
        StringBuilder sb = new StringBuilder();

        Iterator<Map.Entry<JimpleLocal, Definition>> entryIt = localMap.entrySet().iterator();
        Map.Entry<JimpleLocal, Definition> entry;
        boolean firstOutput = false;
        while (entryIt.hasNext()) {
            entry = entryIt.next();
            Definition def = entry.getValue();
            if (def.isActualDefinition()) {
                if (firstOutput) {
                    sb.append('\n');
                }
                sb.append(entry.getKey().getName());
                sb.append(" = ");
                sb.append(def);
                firstOutput = true;
            }
        }

        return sb.toString();
    }

    /**
     * @author Nils Jessen
     * 
     *         The "Value" of the Reaching Definition Analysis
     */
    public static class Definition {
        private static final Definition BOTTOM = new Definition(DefinitionType.BOTTOM);
        private static final Definition TOP = new Definition(DefinitionType.TOP);

        private Value val = null;
        private DefinitionType type;

        public Definition(DefinitionType type) {
            if (type == null) {
                throw new IllegalArgumentException("type must not be null");
            }
            
            if (type == DefinitionType.DEFINITION) {
                throw new IllegalArgumentException("DEFINITION is not a valid type");
            }
            
            this.type = type;
        }

        public Definition(Value val) {
            if (val == null) {
                throw new IllegalArgumentException("val must not be null");
            }
            
            this.val = val;
            this.type = DefinitionType.DEFINITION;
        }

        public Value getValue() {
            return val;
        }

        public static Definition getBottom() {
            return BOTTOM;
        }

        public static Definition getTop() {
            return TOP;
        }

        public boolean isActualDefinition() {
            return this.type == DefinitionType.DEFINITION;
        }
        
        public DefinitionType getDefType() {
            return this.type;
        }

        @Override
        public String toString() {
            if (isActualDefinition()) {
                StringRepresentation valueSwitch = new StringRepresentation(this);
                val.apply(valueSwitch);
                return valueSwitch.getResult();
            } else {
                return "";
            }
        }
        
        @Override
        public boolean equals(Object o) {
            if (! (o instanceof Definition)) {
                return false;
            }
            
            Definition def = (Definition) o;
            if (getDefType() != def.getDefType()) {
                return false;
            }
            
            if (getDefType() == DefinitionType.DEFINITION) {
                return getValue().equals(def.getValue());
            } 
            
            return true;
        }
        
        
    }

    /**
     * @author Nils Jessen
     * 
     *         A {@code StringRepresentation} evaluates {@code JimpleValue}s (e. g. {@code soot.jimple.Expr}).
     */
    static class StringRepresentation implements JimpleValueSwitch {

        private Definition inputDefinition;

        private String result;

        /**
         * Creates a new {@code StringRepresentation} with the given input-{@code ReachingDefinitionsElement}.
         * 
         * @param inputDefinition
         *        the input for this {@code StringRepresentaton}, on which the evaluation is based on
         */
        public StringRepresentation(Definition inputDefinition) {
            this.inputDefinition = inputDefinition;
        }

        /**
         * Returns the result.
         * 
         * @return the result
         */
        public String getResult() {
            return result;
        }

        // ConstantSwitch

        @Override
        public void caseClassConstant(ClassConstant c) {
            // ignore
        }

        @Override
        public void caseDoubleConstant(DoubleConstant c) {
            result = Double.toString(c.value);
        }

        @Override
        public void caseFloatConstant(FloatConstant c) {
            result = Float.toString(c.value);
        }

        @Override
        public void caseIntConstant(IntConstant c) {
            result = Integer.toString(c.value);
        }

        @Override
        public void caseLongConstant(LongConstant c) {
            result = Long.toString(c.value);
        }

        @Override
        public void caseMethodHandle(MethodHandle c) {
            result = c.toString();
            // TODO What does MethodHandle.toString() do?
        }

        @Override
        public void caseNullConstant(NullConstant c) {
            result = "null";
        }

        @Override
        public void caseStringConstant(StringConstant c) {
            result = c.value;
        }

        // ExprSwitch

        /*
         * constant # BOTTON = BOTTOM for # in {+,-,*,/,%,&,|,^,<<,<<<,>>}
         */

        private String[] retrieveStringPair(soot.jimple.BinopExpr expr) {
            Value op1 = expr.getOp1();
            Value op2 = expr.getOp2();

            StringRepresentation valueSwitch1 = new StringRepresentation(inputDefinition);
            op1.apply(valueSwitch1);
            StringRepresentation valueSwitch2 = new StringRepresentation(inputDefinition);
            op2.apply(valueSwitch2);

            String[] stringPair = { valueSwitch1.getResult(), valueSwitch2.getResult() };
            return stringPair;
        }

        @Override
        public void caseAddExpr(AddExpr expr) {
            String[] opString = retrieveStringPair(expr);
            result = "(" + opString[0] + ") + (" + opString[1] + ")";
        }

        @Override
        public void caseSubExpr(SubExpr expr) {
            String[] opString = retrieveStringPair(expr);
            result = "(" + opString[0] + ") - (" + opString[1] + ")";
        }

        @Override
        public void caseMulExpr(MulExpr expr) {
            String[] opString = retrieveStringPair(expr);
            result = "(" + opString[0] + ") * (" + opString[1] + ")";
        }

        @Override
        public void caseDivExpr(DivExpr expr) {
            String[] opString = retrieveStringPair(expr);
            result = "(" + opString[0] + ") / (" + opString[1] + ")";
        }

        @Override
        public void caseRemExpr(RemExpr expr) {
            String[] opString = retrieveStringPair(expr);
            result = "(" + opString[0] + ") % (" + opString[1] + ")";
        }

        @Override
        public void caseNegExpr(NegExpr expr) {
            Value op = expr.getOp();
            StringRepresentation valueSwitch = new StringRepresentation(inputDefinition);
            op.apply(valueSwitch);
            result = "(-1) * " + valueSwitch.getResult();
        }

        @Override
        public void caseAndExpr(AndExpr expr) {
            String[] opString = retrieveStringPair(expr);
            result = "(" + opString[0] + ") & (" + opString[1] + ")";
        }

        @Override
        public void caseOrExpr(OrExpr expr) {
            String[] opString = retrieveStringPair(expr);
            result = "(" + opString[0] + ") | (" + opString[1] + ")";
        }

        @Override
        public void caseXorExpr(XorExpr expr) {
            String[] opString = retrieveStringPair(expr);
            result = "(" + opString[0] + ") ^ (" + opString[1] + ")";
        }

        @Override
        public void caseShlExpr(ShlExpr expr) {
            String[] opString = retrieveStringPair(expr);
            result = "(" + opString[0] + ") << (" + opString[1] + ")";
        }

        @Override
        public void caseShrExpr(ShrExpr expr) {
            String[] opString = retrieveStringPair(expr);
            result = "(" + opString[0] + ") >> (" + opString[1] + ")";
        }

        @Override
        public void caseUshrExpr(UshrExpr expr) {
            String[] opString = retrieveStringPair(expr);
            result = "(" + opString[0] + ") >>> (" + opString[1] + ")";
        }

        @Override
        public void caseCmplExpr(CmplExpr expr) {
            String[] opString = retrieveStringPair(expr);
            result = "(" + opString[0] + ") cmpl (" + opString[1] + ")";
        }

        @Override
        public void caseCmpgExpr(CmpgExpr expr) {
            String[] opString = retrieveStringPair(expr);
            result = "(" + opString[0] + ") cmpg (" + opString[1] + ")";
        }

        @Override
        public void caseCmpExpr(CmpExpr expr) {
            String[] opString = retrieveStringPair(expr);
            result = "(" + opString[0] + ") cmp (" + opString[1] + ")";
        }

        @Override
        public void caseCastExpr(CastExpr expr) {
            StringRepresentation valueSwitch = new StringRepresentation(inputDefinition);
            Value op = expr.getOp();
            op.apply(valueSwitch);
            Type type = expr.getCastType();
            result = "(" + type.toString() + ") " + valueSwitch.getResult();
        }

        @Override
        public void caseEqExpr(EqExpr expr) {
            String[] opString = retrieveStringPair(expr);
            result = "(" + opString[0] + ") == (" + opString[1] + ")";
        }

        @Override
        public void caseGeExpr(GeExpr expr) {
            String[] opString = retrieveStringPair(expr);
            result = "(" + opString[0] + ") >= (" + opString[1] + ")";
        }

        @Override
        public void caseGtExpr(GtExpr expr) {
            String[] opString = retrieveStringPair(expr);
            result = "(" + opString[0] + ") > (" + opString[1] + ")";
        }

        @Override
        public void caseLeExpr(LeExpr expr) {
            String[] opString = retrieveStringPair(expr);
            result = "(" + opString[0] + ") <= (" + opString[1] + ")";
        }

        @Override
        public void caseLtExpr(LtExpr expr) {
            String[] opString = retrieveStringPair(expr);
            result = "(" + opString[0] + ") < (" + opString[1] + ")";
        }

        @Override
        public void caseNeExpr(NeExpr expr) {
            String[] opString = retrieveStringPair(expr);
            result = "(" + opString[0] + ") != (" + opString[1] + ")";
        }

        @Override
        public void caseInstanceOfExpr(InstanceOfExpr expr) {
            Value op = expr.getOp();
            StringRepresentation valueSwitch = new StringRepresentation(inputDefinition);
            op.apply(valueSwitch);
            result = valueSwitch.getResult() + " instanceof " + expr.getCheckType();
        }

        @Override
        public void caseInterfaceInvokeExpr(InterfaceInvokeExpr expr) {
            String methodName = expr.getMethod().getName();
            List<Value> args = expr.getArgs();
            dynamicInvokeExpr(expr.getBase().toString(), methodName, args);
        }

        @Override
        public void caseDynamicInvokeExpr(DynamicInvokeExpr expr) {
            // TODO is this correct?
            String methodName = expr.getMethod().getName();
            List<Value> args = expr.getArgs();
            staticInvokeExpr(methodName, args);
        }

        @Override
        public void caseLengthExpr(LengthExpr expr) {
            StringRepresentation valueSwitch = new StringRepresentation(inputDefinition);
            Value op = expr.getOp();
            op.apply(valueSwitch);
            result = "arraylength(" + valueSwitch.getResult() + ")";
        }

        @Override
        public void caseNewArrayExpr(NewArrayExpr expr) {
            StringRepresentation valueSwitch = new StringRepresentation(inputDefinition);
            Value size = expr.getSize();
            size.apply(valueSwitch);
            result = "new " + expr.getBaseType().toString() + "[" + valueSwitch.getResult() + "]";
        }

        @Override
        public void caseNewExpr(NewExpr expr) {
            result = "new " + expr.getBaseType().toString();
        }

        @Override
        public void caseNewMultiArrayExpr(NewMultiArrayExpr expr) {
            StringBuilder sb = new StringBuilder();
            ArrayType arrayType = expr.getBaseType();
            while (arrayType.numDimensions > 1) {
                arrayType = (ArrayType) arrayType.getElementType();
            }
            
            Type type = arrayType.getElementType();
            sb.append("new " + type.toString());
            int sizeCount = expr.getSizeCount();
            for (int i = 0; i < sizeCount; i++) {
                StringRepresentation valueSwitch = new StringRepresentation(inputDefinition);
                Value size = expr.getSize(i);
                size.apply(valueSwitch);
                sb.append("[" + valueSwitch.getResult() + "]");
            }
            result = sb.toString();
        }

        @Override
        public void caseSpecialInvokeExpr(SpecialInvokeExpr expr) {
            String methodName = expr.getMethod().getName();
            List<Value> args = expr.getArgs();
            dynamicInvokeExpr(expr.getBase().toString(), methodName, args);
        }

        @Override
        public void caseStaticInvokeExpr(StaticInvokeExpr expr) {
            String methodName = expr.getMethod().getName();
            List<Value> args = expr.getArgs();
            staticInvokeExpr(methodName, args);
        }

        @Override
        public void caseVirtualInvokeExpr(VirtualInvokeExpr expr) {
            String methodName = expr.getMethod().getName();
            List<Value> args = expr.getArgs();
            dynamicInvokeExpr(expr.getBase().toString(), methodName, args);
        }

        // RefSwitch

        @Override
        public void caseArrayRef(ArrayRef ref) {
            StringRepresentation valueSwitch = new StringRepresentation(inputDefinition);
            Value op = ref.getBase();
            op.apply(valueSwitch);
            String base = valueSwitch.getResult();
            valueSwitch = new StringRepresentation(inputDefinition);
            Value idx = ref.getIndex();
            idx.apply(valueSwitch);
            String index = valueSwitch.getResult();
            result = base + "[" + index + "]";
        }

        @Override
        public void caseCaughtExceptionRef(CaughtExceptionRef ref) {
            throw new UnsupportedValueException("CaughtExceptionRef", ref);
        }

        @Override
        public void caseInstanceFieldRef(InstanceFieldRef ref) {
            StringRepresentation valueSwitch = new StringRepresentation(inputDefinition);
            Value op = ref.getBase();
            op.apply(valueSwitch);
            result = valueSwitch.getResult();
        }

        @Override
        public void caseParameterRef(ParameterRef ref) {
            result = "@parameter" + ref.getIndex();
        }

        @Override
        public void caseStaticFieldRef(StaticFieldRef ref) {
            result = ref.getField().getName();
        }

        @Override
        public void caseThisRef(ThisRef ref) {
            result = "this";
        }

        // JimpleValueSwitch

        @Override
        public void caseLocal(Local local) {
            if (!(local instanceof JimpleLocal)) {
                throw new IllegalStateException("No Jimple local!");
            }
            result = ((JimpleLocal) local).getName();
        }

        @Override
        public void defaultCase(Object arg0) {
            assert false : "No soot Value - You fucked up!";
        }
        
        /**
         * Combines the cases of all different dynamic invoke expressions 
         * 
         * @param methodName
         *        the name of the method to be invoked
         * @param args
         *        the arguments of the method to be
         */
        private void dynamicInvokeExpr(String objectName, String methodName, List<Value> args) {
            StringBuilder sb = new StringBuilder(objectName);
            sb.append(".");
            sb.append(methodName + "(");
            StringRepresentation valueSwitch = new StringRepresentation(inputDefinition);
            String prefix = "";
            for (Value v : args) {
                v.apply(valueSwitch);
                sb.append(prefix).append(valueSwitch.getResult());
                prefix = ", ";
            }
            sb.append(")");
            result = sb.toString();
        }
        
        private void staticInvokeExpr(String methodName, List<Value> args) {
            StringBuilder sb = new StringBuilder();
            sb.append(methodName + "(");
            StringRepresentation valueSwitch = new StringRepresentation(inputDefinition);
            String prefix = "";
            for (Value v : args) {
                v.apply(valueSwitch);
                sb.append(prefix).append(valueSwitch.getResult());
                prefix = ", ";
            }
            sb.append(")");
            result = sb.toString();
        }
        
    }

    /**
     * @author Nils Jessen
     * 
     *         Distinguishes actual Definitions from top and bottom.
     */
    public enum DefinitionType {
        /**
         * for bottom
         */
        BOTTOM,

        /**
         * for top
         */
        TOP,

        /**
         * for actual defintions
         */
        DEFINITION
    }
}
