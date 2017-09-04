package dfa.analyses;

import java.util.Map;
import java.util.Set;

import dfa.analyses.TaintElement.TaintState;
import dfa.framework.TaintAnalysisTag;
import dfa.framework.Transition;
import soot.Local;
import soot.SootMethod;
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
import soot.jimple.InvokeExpr;
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
import soot.jimple.UnopExpr;
import soot.jimple.UshrExpr;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.XorExpr;
import soot.jimple.internal.JimpleLocal;

/**
 * @author Sebastian Rauch
 *
 *         A {@code TaintTransition} performs the transition for a {@code TaintAnalysis}.
 *
 */
public class TaintTransition implements Transition<TaintElement> {

    @Override
    public TaintElement transition(TaintElement element, Unit unit) {
        Transitioner stmtSwitch = new Transitioner(element);
        unit.apply(stmtSwitch);

        return stmtSwitch.getOutputElement();
    }

    static class Transitioner implements StmtSwitch {

        private TaintElement inputElement;

        private TaintElement outputElement;

        /**
         * Creates a {@code Transitioner} with the given input-{@code TaintElement}.
         * 
         * @param inputElement
         *        the input-{@code TaintElement}
         */
        public Transitioner(TaintElement element) {
            this.inputElement = element;
            outputElement = new TaintElement(inputElement.getLocalMap());
        }

        /**
         * Returns the result of the transition.
         * 
         * @return the result of the transition
         */
        public TaintElement getOutputElement() {
            return outputElement;
        }

        @Override
        public void caseAssignStmt(AssignStmt stmt) {
            JimpleLocal lValLocal;
            if (stmt.getLeftOp() instanceof JimpleLocal) {
                lValLocal = (JimpleLocal) stmt.getLeftOp();
                if (!TaintElement.isLocalTypeAccepted(lValLocal.getType())) {
                    return;
                }
            } else {
                return; // ignore
            }

            soot.Value rVal = stmt.getRightOp();
            Evaluator valueSwitch = new Evaluator(inputElement);
            rVal.apply(valueSwitch);
            TaintElement.Value rhs = valueSwitch.getResult();

            outputElement.setValue(lValLocal, rhs);
        }

        @Override
        public void caseBreakpointStmt(BreakpointStmt stmt) {
            // ignore
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
            // ignore
        }

        @Override
        public void caseIdentityStmt(IdentityStmt stmt) {
            // this is only used for parameter initialization (so set to TAINTED)

            JimpleLocal lValLocal;
            if (stmt.getLeftOp() instanceof JimpleLocal) {
                lValLocal = (JimpleLocal) stmt.getLeftOp();
                if (!TaintElement.isLocalTypeAccepted(lValLocal.getType())) {
                    return;
                }
            } else {
                return; // ignore
            }

            boolean violated = inputElement.getValue(lValLocal).wasViolated(); // should be false
            outputElement.setValue(lValLocal, new TaintElement.Value(TaintState.TAINTED, violated));
        }

        @Override
        public void caseIfStmt(IfStmt stmt) {
            // ignore
        }

        @Override
        public void caseInvokeStmt(InvokeStmt stmt) {

            InvokeExpr invokeExpr = stmt.getInvokeExpr();
            if (invokeExpr.getArgCount() > 1) {
                // ignore since this can't be any special method for taint-analysis
                return;
            }

            if (invokeExpr.getArgCount() == 0) {

                // this handles the parameterless sensitive
                SootMethod method = invokeExpr.getMethod();
                if (method.hasTag(TaintAnalysisTag.SENSITIVE_TAG.getName())) {
                    Set<Map.Entry<JimpleLocal, TaintElement.Value>> entries = inputElement.getLocalMap().entrySet();
                    for (Map.Entry<JimpleLocal, TaintElement.Value> e : entries) {
                        TaintElement.Value val = e.getValue();
                        if (val.getTaintState() == TaintState.TAINTED && !val.wasViolated()) {
                            // we only need to do something if this is the first violation
                            JimpleLocal local = e.getKey();
                            TaintElement.Value inValue = inputElement.getValue(local);
                            outputElement.setValue(local, new TaintElement.Value(inValue.getTaintState(), true));
                        }
                    }
                }
                return;
            }

            if (!(invokeExpr.getArg(0) instanceof JimpleLocal)) {
                // ignore, since we can only taint locals
                return;
            }

            JimpleLocal local = (JimpleLocal) invokeExpr.getArg(0);
            if (!TaintElement.isLocalTypeAccepted(local.getType())) {
                return;
            }

            SootMethod method = invokeExpr.getMethod();
            TaintElement.Value inValue = inputElement.getValue(local);
            if (method.hasTag(TaintAnalysisTag.TAINT_TAG.getName())) {
                // taint
                if (inValue.getTaintState() != TaintState.TAINTED) {
                    outputElement.setValue(local, new TaintElement.Value(TaintState.TAINTED, inValue.wasViolated()));
                }
            } else if (method.hasTag(TaintAnalysisTag.CLEAN_TAG.getName())) {
                // clean
                if (inValue.getTaintState() != TaintState.CLEAN) {
                    outputElement.setValue(local, new TaintElement.Value(TaintState.CLEAN, inValue.wasViolated()));
                }
            } else if (method.hasTag(TaintAnalysisTag.SENSITIVE_TAG.getName())) {
                // sensitive
                if (!inValue.wasViolated() && inValue.getTaintState() == TaintState.TAINTED) {
                    outputElement.setValue(local, new TaintElement.Value(TaintState.TAINTED, true));
                }
            }

        }

        @Override
        public void caseLookupSwitchStmt(LookupSwitchStmt stmt) {
            // ignore
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
            // ignore
        }

        @Override
        public void caseThrowStmt(ThrowStmt stmt) {
            // ignore
        }

        @Override
        public void defaultCase(Object arg0) {
            assert false : "Something went horribly wrong!";
        }

    }

    /**
     * @author Sebastian Rauch
     * 
     *         A {@code Evaluator} evaluates {@code JimpleValue}s (e. g. {@code soot.jimple.Expr}).
     */
    static class Evaluator implements JimpleValueSwitch {

        TaintElement inputElement;

        TaintElement.Value result;

        /**
         * Creates a new {@code Evaluator} with the given input-{@code TaintElement}.
         * 
         * @param inputElement
         *        the input for this {@code Evaluator}, on which the evaluation is based on
         */
        public Evaluator(TaintElement inputElement) {
            this.inputElement = inputElement;
        }

        /**
         * Returns the result.
         * 
         * @return the result
         */
        public TaintElement.Value getResult() {
            return result;
        }

        // ConstantSwitch (constants are always clean, other always tainted)

        @Override
        public void caseClassConstant(ClassConstant c) {
            result = new TaintElement.Value(TaintState.TAINTED, false);
        }

        @Override
        public void caseDoubleConstant(DoubleConstant c) {
            result = new TaintElement.Value(TaintState.CLEAN, false);
        }

        @Override
        public void caseFloatConstant(FloatConstant c) {
            result = new TaintElement.Value(TaintState.CLEAN, false);
        }

        @Override
        public void caseIntConstant(IntConstant c) {
            result = new TaintElement.Value(TaintState.CLEAN, false);
        }

        @Override
        public void caseLongConstant(LongConstant c) {
            result = new TaintElement.Value(TaintState.CLEAN, false);
        }

        @Override
        public void caseMethodHandle(MethodHandle c) {
            result = new TaintElement.Value(TaintState.TAINTED, false);
        }

        @Override
        public void caseNullConstant(NullConstant c) {
            result = new TaintElement.Value(TaintState.TAINTED, false);
        }

        @Override
        public void caseStringConstant(StringConstant c) {
            result = new TaintElement.Value(TaintState.TAINTED, false);
        }

        // ExprSwitch

        /*
         * note that we are doing pure taint-analysis, without any notion of constants (this means that even 'y = 0 * x'
         * taints y when x is tainted)
         */

        @Override
        public void caseAddExpr(AddExpr expr) {
            binOpExprDefault(expr);
        }

        @Override
        public void caseSubExpr(SubExpr expr) {
            binOpExprDefault(expr);
        }

        @Override
        public void caseMulExpr(MulExpr expr) {
            binOpExprDefault(expr);
        }

        @Override
        public void caseDivExpr(DivExpr expr) {
            binOpExprDefault(expr);
        }

        @Override
        public void caseRemExpr(RemExpr expr) {
            binOpExprDefault(expr);
        }

        @Override
        public void caseNegExpr(NegExpr expr) {
            unOpExprDefault(expr);
        }

        @Override
        public void caseAndExpr(AndExpr expr) {
            binOpExprDefault(expr);
        }

        @Override
        public void caseOrExpr(OrExpr expr) {
            binOpExprDefault(expr);
        }

        @Override
        public void caseXorExpr(XorExpr expr) {
            binOpExprDefault(expr);
        }

        @Override
        public void caseShlExpr(ShlExpr expr) {
            binOpExprDefault(expr);
        }

        @Override
        public void caseShrExpr(ShrExpr expr) {
            binOpExprDefault(expr);
        }

        @Override
        public void caseUshrExpr(UshrExpr expr) {
            binOpExprDefault(expr);
        }

        @Override
        public void caseCmpExpr(CmpExpr expr) {
            binOpExprDefault(expr);
        }

        @Override
        public void caseCmpgExpr(CmpgExpr expr) {
            binOpExprDefault(expr);
        }

        @Override
        public void caseCmplExpr(CmplExpr expr) {
            binOpExprDefault(expr);
        }

        @Override
        public void caseCastExpr(CastExpr expr) {
            Value op = expr.getOp();
            Evaluator eval = new Evaluator(inputElement);
            op.apply(eval);

            TaintElement.Value opVal = eval.getResult();
            result = new TaintElement.Value(opVal.getTaintState(), opVal.wasViolated());
        }

        @Override
        public void caseDynamicInvokeExpr(DynamicInvokeExpr expr) {
            result = new TaintElement.Value(TaintState.TAINTED, false);
        }

        @Override
        public void caseEqExpr(EqExpr expr) {
            binOpExprDefault(expr);
        }

        @Override
        public void caseGeExpr(GeExpr expr) {
            binOpExprDefault(expr);
        }

        @Override
        public void caseGtExpr(GtExpr expr) {
            binOpExprDefault(expr);
        }

        @Override
        public void caseLeExpr(LeExpr expr) {
            binOpExprDefault(expr);
        }

        @Override
        public void caseLtExpr(LtExpr expr) {
            binOpExprDefault(expr);
        }

        @Override
        public void caseNeExpr(NeExpr expr) {
            binOpExprDefault(expr);
        }

        @Override
        public void caseInstanceOfExpr(InstanceOfExpr expr) {
            result = new TaintElement.Value(TaintState.TAINTED, false);
        }

        @Override
        public void caseInterfaceInvokeExpr(InterfaceInvokeExpr expr) {
            result = new TaintElement.Value(TaintState.TAINTED, false);
        }

        @Override
        public void caseLengthExpr(LengthExpr expr) {
            // array-length is not handled, so it is always tainted
            result = new TaintElement.Value(TaintState.TAINTED, false);
        }

        @Override
        public void caseNewArrayExpr(NewArrayExpr expr) {
            result = new TaintElement.Value(TaintState.TAINTED, false);
        }

        @Override
        public void caseNewExpr(NewExpr expr) {
            result = new TaintElement.Value(TaintState.TAINTED, false);
        }

        @Override
        public void caseNewMultiArrayExpr(NewMultiArrayExpr expr) {
            result = new TaintElement.Value(TaintState.TAINTED, false);
        }

        @Override
        public void caseSpecialInvokeExpr(SpecialInvokeExpr expr) {
            result = new TaintElement.Value(TaintState.TAINTED, false);
        }

        @Override
        public void caseStaticInvokeExpr(StaticInvokeExpr expr) {
            result = new TaintElement.Value(TaintState.TAINTED, false);
        }

        @Override
        public void caseVirtualInvokeExpr(VirtualInvokeExpr expr) {
            result = new TaintElement.Value(TaintState.TAINTED, false);
        }

        // RefSwitch

        @Override
        public void caseArrayRef(ArrayRef ref) {
            result = new TaintElement.Value(TaintState.TAINTED, false);
        }

        @Override
        public void caseCaughtExceptionRef(CaughtExceptionRef ref) {
            result = new TaintElement.Value(TaintState.TAINTED, false);
        }

        @Override
        public void caseInstanceFieldRef(InstanceFieldRef ref) {
            result = new TaintElement.Value(TaintState.TAINTED, false);
        }

        @Override
        public void caseParameterRef(ParameterRef ref) {
            result = new TaintElement.Value(TaintState.TAINTED, false);
        }

        @Override
        public void caseStaticFieldRef(StaticFieldRef ref) {
            result = new TaintElement.Value(TaintState.TAINTED, false);
        }

        @Override
        public void caseThisRef(ThisRef ref) {
            result = new TaintElement.Value(TaintState.TAINTED, false);
        }

        // JimpleValueSwitch

        @Override
        public void caseLocal(Local local) {
            if (!(local instanceof JimpleLocal)) {
                assert false : "Something went horribly wrong!";
            }

            result = inputElement.getValue((JimpleLocal) local);
        }

        @Override
        public void defaultCase(Object arg0) {
            assert false : "Something went horribly wrong!";
        }

        private void binOpExprDefault(BinopExpr expr) {
            Pair<TaintElement.Value> opValues = calcOperands(expr);
            TaintElement.Value opVal1 = opValues.getFirst();
            TaintElement.Value opVal2 = opValues.getSecond();
            boolean violated = opVal1.wasViolated() | opVal2.wasViolated();

            if (isTainted(opVal1) || isTainted(opVal2)) {
                result = new TaintElement.Value(TaintState.TAINTED, violated);
            } else if (isBottom(opVal1) || isBottom(opVal2)) {
                result = new TaintElement.Value(TaintState.BOTTOM, violated);
            } else {
                result = new TaintElement.Value(TaintState.CLEAN, violated);
            }
        }

        private void unOpExprDefault(UnopExpr unOpExpr) {
            Value op = unOpExpr.getOp();
            Evaluator eval = new Evaluator(inputElement);
            op.apply(eval);

            TaintElement.Value opVal = eval.getResult();
            result = new TaintElement.Value(opVal.getTaintState(), opVal.wasViolated());
        }

        private Pair<TaintElement.Value> calcOperands(BinopExpr binOpExpr) {
            Value op1 = binOpExpr.getOp1();
            Evaluator eval1 = new Evaluator(inputElement);
            op1.apply(eval1);

            Value op2 = binOpExpr.getOp2();
            Evaluator eval2 = new Evaluator(inputElement);
            op2.apply(eval2);

            return new Pair<TaintElement.Value>(eval1.getResult(), eval2.getResult());
        }

        private boolean isTainted(TaintElement.Value val) {
            return val.getTaintState().equals(TaintState.TAINTED);
        }

        private boolean isBottom(TaintElement.Value val) {
            return val.getTaintState().equals(TaintState.BOTTOM);
        }

    }

}
