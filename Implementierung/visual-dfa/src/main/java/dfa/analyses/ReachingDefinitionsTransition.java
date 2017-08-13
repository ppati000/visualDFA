package dfa.analyses;

import dfa.framework.Transition;
import dfa.framework.UnsupportedStatementException;
import dfa.framework.UnsupportedValueException;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.jimple.AddExpr;
import soot.jimple.AndExpr;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.BreakpointStmt;
import soot.jimple.CastExpr;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.ClassConstant;
import soot.jimple.CmpExpr;
import soot.jimple.CmpgExpr;
import soot.jimple.CmplExpr;
import soot.jimple.ConstantSwitch;
import soot.jimple.DivExpr;
import soot.jimple.DoubleConstant;
import soot.jimple.DynamicInvokeExpr;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.EqExpr;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.FloatConstant;
import soot.jimple.GeExpr;
import soot.jimple.GotoStmt;
import soot.jimple.GtExpr;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceOfExpr;
import soot.jimple.IntConstant;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.JimpleValueSwitch;
import soot.jimple.LeExpr;
import soot.jimple.LengthExpr;
import soot.jimple.LongConstant;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.LtExpr;
import soot.jimple.MethodHandle;
import soot.jimple.MulExpr;
import soot.jimple.NeExpr;
import soot.jimple.NegExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.NopStmt;
import soot.jimple.NullConstant;
import soot.jimple.OrExpr;
import soot.jimple.ParameterRef;
import soot.jimple.RemExpr;
import soot.jimple.RetStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.ShlExpr;
import soot.jimple.ShrExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.StmtSwitch;
import soot.jimple.StringConstant;
import soot.jimple.SubExpr;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThisRef;
import soot.jimple.ThrowStmt;
import soot.jimple.UshrExpr;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.XorExpr;
import soot.jimple.internal.JimpleLocal;

/**
 * @author Nils Jessen
 * 
 *         A {@code ReachingDefinitionsTransition} performs the transition for a {@code ReachingDefinitionsAnalysis}.
 */
public class ReachingDefinitionsTransition implements Transition<ReachingDefinitionsElement> {

    @Override
    public ReachingDefinitionsElement transition(ReachingDefinitionsElement element, Unit unit) {
        Transitioner stmtSwitch = new Transitioner(element);
        unit.apply(stmtSwitch);

        return stmtSwitch.getOutputElement();
    }

    /**
     * @author Nils Jessen
     * 
     *         Handles the top-level statements.
     */
    static class Transitioner implements StmtSwitch {

        private ReachingDefinitionsElement inputElement;

        private ReachingDefinitionsElement outputElement;

        /**
         * Creates a {@code Transitioner} with the given input-{@code ReachingDefinitionsElement}.
         * 
         * @param inputElement
         *        the input-{@code ReachingDefinitionsElement}
         */
        public Transitioner(ReachingDefinitionsElement inputElement) {
            this.inputElement = inputElement;
            outputElement = new ReachingDefinitionsElement(inputElement.getLocalMap());
        }

        /**
         * Returns the result of the transition.
         * 
         * @return the result of the transition
         */
        public ReachingDefinitionsElement getOutputElement() {
            return outputElement;
        }

        @Override
        public void caseAssignStmt(AssignStmt stmt) {
            // TODO Auto-generated method stub
        }

        @Override
        public void caseBreakpointStmt(BreakpointStmt stmt) {
            throw new UnsupportedStatementException("BreakpointStmt", stmt);
        }

        @Override
        public void caseEnterMonitorStmt(EnterMonitorStmt stmt) {
            // ignore
        }

        @Override
        public void caseExitMonitorStmt(ExitMonitorStmt stmt) {
            // ignore
        }

        @Override
        public void caseGotoStmt(GotoStmt stmt) {
            // ignore (identity transition)
        }

        @Override
        public void caseIdentityStmt(IdentityStmt stmt) {
            // TODO Auto-generated method stub
        }

        @Override
        public void caseIfStmt(IfStmt stmt) {
            // TODO Auto-generated method stub
        }

        @Override
        public void caseInvokeStmt(InvokeStmt stmt) {
            // ignore
        }

        @Override
        public void caseLookupSwitchStmt(LookupSwitchStmt stmt) {
            throw new UnsupportedStatementException("LookupSwitchStmt", stmt);
        }

        @Override
        public void caseNopStmt(NopStmt stmt) {
            // ignore
        }

        @Override
        public void caseRetStmt(RetStmt stmt) {
            // ignore
        }

        @Override
        public void caseReturnStmt(ReturnStmt stmt) {
            // ignore
        }

        @Override
        public void caseReturnVoidStmt(ReturnVoidStmt stmt) {
            // ignore
        }

        @Override
        public void caseTableSwitchStmt(TableSwitchStmt stmt) {
            throw new UnsupportedStatementException("TableSwitchStmt", stmt);
        }

        @Override
        public void caseThrowStmt(ThrowStmt stmt) {
            throw new UnsupportedStatementException("ThrowStmt", stmt);
        }

        @Override
        public void defaultCase(Object arg0) {
            assert false : "No Soot Statement - You fucked up!";
        }

    }

    /**
     * @author Nils Jessen
     * 
     *         A {@code Evaluator} evaluates {@code JimpleValue}s (e. g. {@code soot.jimple.Expr}).
     */
    static class Evaluator implements JimpleValueSwitch {
        private static ReachingDefinitionsElement.Definition top = ReachingDefinitionsElement.Definition.getTop();

        private ReachingDefinitionsElement inputElement;

        private ReachingDefinitionsElement.Definition result;

        /**
         * Creates a new {@code Evaluator} with the given input-{@code ReachingDefinitionsElement}.
         * 
         * @param inputElement
         *        the input for this {@code Evaluator}, on which the evaluation is based on
         */
        public Evaluator(ReachingDefinitionsElement inputElement) {
            this.inputElement = inputElement;
        }

        /**
         * Returns the result.
         * 
         * @return the result
         */
        public ReachingDefinitionsElement.Definition getResult() {
            return result;
        }

        // ConstantSwitch

        @Override
        public void caseClassConstant(ClassConstant c) {
            result = top;
        }

        @Override
        public void caseDoubleConstant(DoubleConstant c) {
            result = top;
        }

        @Override
        public void caseFloatConstant(FloatConstant c) {
            result = top;
        }

        @Override
        public void caseIntConstant(IntConstant c) {
            result = new ReachingDefinitionsElement.Definition(c);
        }

        @Override
        public void caseLongConstant(LongConstant c) {
            result = new ReachingDefinitionsElement.Definition(c);
        }

        @Override
        public void caseMethodHandle(MethodHandle c) {
            result = top;
        }

        @Override
        public void caseNullConstant(NullConstant c) {
            result = top;
        }

        @Override
        public void caseStringConstant(StringConstant c) {
            result = top;
        }

        // ExprSwitch

        /*
         * constant # BOTTON = BOTTOM for # in {+,-,*,/,%,&,|,^,<<,<<<,>>}
         */

        @Override
        public void caseAddExpr(AddExpr expr) {
            // TODO implement Expression
        }

        @Override
        public void caseSubExpr(SubExpr expr) {
            // TODO implement Expression
        }

        @Override
        public void caseMulExpr(MulExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ReachingDefinitionsElement.Definition op1 = operandValues.getFirst();
            ReachingDefinitionsElement.Definition op2 = operandValues.getSecond();

            // TODO implement Expression
        }

        @Override
        public void caseDivExpr(DivExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ReachingDefinitionsElement.Definition op1 = operandValues.getFirst();
            ReachingDefinitionsElement.Definition op2 = operandValues.getSecond();

            // TODO implement Expression
        }

        @Override
        public void caseRemExpr(RemExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ReachingDefinitionsElement.Definition op1 = operandValues.getFirst();
            ReachingDefinitionsElement.Definition op2 = operandValues.getSecond();

            // TODO implement Expression
        }

        @Override
        public void caseNegExpr(NegExpr expr) {
            // TODO implement Expression
        }

        @Override
        public void caseAndExpr(AndExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ReachingDefinitionsElement.Definition op1 = operandValues.getFirst();
            ReachingDefinitionsElement.Definition op2 = operandValues.getSecond();

            // TODO implement Expression
        }

        @Override
        public void caseOrExpr(OrExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ReachingDefinitionsElement.Definition op1 = operandValues.getFirst();
            ReachingDefinitionsElement.Definition op2 = operandValues.getSecond();

            // TODO implement Expression
        }

        @Override
        public void caseXorExpr(XorExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ReachingDefinitionsElement.Definition op1 = operandValues.getFirst();
            ReachingDefinitionsElement.Definition op2 = operandValues.getSecond();

            // TODO implement Expression
        }

        @Override
        public void caseShlExpr(ShlExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ReachingDefinitionsElement.Definition op1 = operandValues.getFirst();
            ReachingDefinitionsElement.Definition op2 = operandValues.getSecond();

            // TODO implement Expression
        }

        @Override
        public void caseShrExpr(ShrExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ReachingDefinitionsElement.Definition op1 = operandValues.getFirst();
            ReachingDefinitionsElement.Definition op2 = operandValues.getSecond();

            // TODO implement Expression
        }

        @Override
        public void caseUshrExpr(UshrExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ReachingDefinitionsElement.Definition op1 = operandValues.getFirst();
            ReachingDefinitionsElement.Definition op2 = operandValues.getSecond();

            // TODO implement Expression
        }

        @Override
        public void caseCmplExpr(CmplExpr expr) {
            // TODO Auto-generated method stub

        }

        @Override
        public void caseCmpgExpr(CmpgExpr expr) {
            // TODO Auto-generated method stub

        }

        @Override
        public void caseCmpExpr(CmpExpr expr) {
            // TODO Auto-generated method stub

        }

        @Override
        public void caseCastExpr(CastExpr expr) {
            // TODO Auto-generated method stub
        }

        @Override
        public void caseEqExpr(EqExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ReachingDefinitionsElement.Definition op1 = operandValues.getFirst();
            ReachingDefinitionsElement.Definition op2 = operandValues.getSecond();

            // TODO implement Expression
        }

        @Override
        public void caseGeExpr(GeExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ReachingDefinitionsElement.Definition op1 = operandValues.getFirst();
            ReachingDefinitionsElement.Definition op2 = operandValues.getSecond();

            // TODO implement Expression
        }

        @Override
        public void caseGtExpr(GtExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ReachingDefinitionsElement.Definition op1 = operandValues.getFirst();
            ReachingDefinitionsElement.Definition op2 = operandValues.getSecond();

            // TODO implement Expression
        }

        @Override
        public void caseLeExpr(LeExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ReachingDefinitionsElement.Definition op1 = operandValues.getFirst();
            ReachingDefinitionsElement.Definition op2 = operandValues.getSecond();

            // TODO implement Expression
        }

        @Override
        public void caseLtExpr(LtExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ReachingDefinitionsElement.Definition op1 = operandValues.getFirst();
            ReachingDefinitionsElement.Definition op2 = operandValues.getSecond();

            // TODO implement Expression
        }

        @Override
        public void caseNeExpr(NeExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ReachingDefinitionsElement.Definition op1 = operandValues.getFirst();
            ReachingDefinitionsElement.Definition op2 = operandValues.getSecond();

            // TODO implement Expression
        }

        @Override
        public void caseInstanceOfExpr(InstanceOfExpr expr) {
            result = top;
        }

        @Override
        public void caseInterfaceInvokeExpr(InterfaceInvokeExpr expr) {
            result = top;
        }

        @Override
        public void caseDynamicInvokeExpr(DynamicInvokeExpr expr) {
            result = top;
        }

        @Override
        public void caseLengthExpr(LengthExpr expr) {
            result = top;
        }

        @Override
        public void caseNewArrayExpr(NewArrayExpr expr) {
            result = top;
        }

        @Override
        public void caseNewExpr(NewExpr expr) {
            result = top;
        }

        @Override
        public void caseNewMultiArrayExpr(NewMultiArrayExpr expr) {
            result = top;
        }

        @Override
        public void caseSpecialInvokeExpr(SpecialInvokeExpr expr) {
            result = top;
        }

        @Override
        public void caseStaticInvokeExpr(StaticInvokeExpr expr) {
            result = top;
        }

        @Override
        public void caseVirtualInvokeExpr(VirtualInvokeExpr expr) {
            result = top;
        }

        // RefSwitch

        @Override
        public void caseArrayRef(ArrayRef ref) {
            result = top;
        }

        @Override
        public void caseCaughtExceptionRef(CaughtExceptionRef ref) {
            throw new UnsupportedValueException("CaughtExceptionRef", ref);
        }

        @Override
        public void caseInstanceFieldRef(InstanceFieldRef ref) {
            result = top;
        }

        @Override
        public void caseParameterRef(ParameterRef ref) {
            result = top;
        }

        @Override
        public void caseStaticFieldRef(StaticFieldRef ref) {
            result = top;
        }

        @Override
        public void caseThisRef(ThisRef ref) {
            result = top;
        }

        // JimpleValueSwitch

        @Override
        public void caseLocal(Local local) {
            if (!(local instanceof JimpleLocal)) {
                throw new IllegalStateException("No Jimple local!");
            }

            result = inputElement.getValue((JimpleLocal) local);
        }

        @Override
        public void defaultCase(Object arg0) {
            assert false : "No soot Value - You fucked up!";
        }

        private ValuePair calcOperands(BinopExpr binOpExpr) {
            Value op1 = binOpExpr.getOp1();
            Evaluator switch1 = new Evaluator(inputElement);
            op1.apply(switch1);

            Value op2 = binOpExpr.getOp2();
            Evaluator switch2 = new Evaluator(inputElement);
            op2.apply(switch2);

            return new ValuePair(switch1.getResult(), switch2.getResult());
        }

    }

    /**
     * @author Nils Jessen
     * 
     *         Retrieves the (numeric) values of {@code IntConstant}s or {@code LongConstant}s.
     *
     */
    private static class ConstantRetriever implements ConstantSwitch {

        private Long value;

        public long getValue() {
            if (value == null) {
                throw new IllegalStateException("not constant");
            }
            return value;
        }

        @Override
        public void caseClassConstant(ClassConstant c) {
            value = null;
        }

        @Override
        public void caseDoubleConstant(DoubleConstant c) {
            value = null;
        }

        @Override
        public void caseFloatConstant(FloatConstant c) {
            value = null;
        }

        @Override
        public void caseIntConstant(IntConstant c) {
            value = Long.valueOf(c.value);
        }

        @Override
        public void caseLongConstant(LongConstant c) {
            value = c.value;
        }

        @Override
        public void caseMethodHandle(MethodHandle c) {
            value = null;
        }

        @Override
        public void caseNullConstant(NullConstant c) {
            value = null;
        }

        @Override
        public void caseStringConstant(StringConstant c) {
            value = null;
        }

        @Override
        public void defaultCase(Object arg0) {
            value = null;
        }
    }

    /**
     * @author Nils Jessen
     * 
     *         A {@code ValuePair} represents a pair of {@code soot.Value}s.
     */
    private static class ValuePair {
        private ReachingDefinitionsElement.Definition val1;
        private ReachingDefinitionsElement.Definition val2;

        public ValuePair(ReachingDefinitionsElement.Definition val1, ReachingDefinitionsElement.Definition val2) {
            this.val1 = val1;
            this.val2 = val2;
        }

        public ReachingDefinitionsElement.Definition getFirst() {
            return val1;
        }

        public ReachingDefinitionsElement.Definition getSecond() {
            return val2;
        }
    }
}
