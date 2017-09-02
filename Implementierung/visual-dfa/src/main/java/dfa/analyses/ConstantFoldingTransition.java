package dfa.analyses;

import dfa.framework.Transition;
import dfa.framework.UnsupportedStatementException;
import dfa.framework.UnsupportedValueException;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.ShortType;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AddExpr;
import soot.jimple.AndExpr;
import soot.jimple.ArithmeticConstant;
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
import soot.jimple.Ref;
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
 * @author Sebastian Rauch
 * 
 *         A {@code ConstantFoldingTransition} performs the transition for a {@code ConstantFoldingAnalysis}.
 */
public class ConstantFoldingTransition implements Transition<ConstantFoldingElement> {

    @Override
    public ConstantFoldingElement transition(ConstantFoldingElement element, Unit unit) {
        Transitioner stmtSwitch = new Transitioner(element);
        unit.apply(stmtSwitch);

        return stmtSwitch.getOutputElement();
    }

    /**
     * @author Nils Jessen
     * @author Sebastian Rauch
     * 
     *         Handles the top-level statements.
     */
    static class Transitioner implements StmtSwitch {

        private ConstantFoldingElement inputElement;

        private ConstantFoldingElement outputElement;

        /**
         * Creates a {@code Transitioner} with the given input-{@code ConstantFoldingElement}.
         * 
         * @param inputElement
         *        the input-{@code ConstantFoldingElement}
         */
        public Transitioner(ConstantFoldingElement inputElement) {
            this.inputElement = inputElement;
            outputElement = new ConstantFoldingElement(inputElement.getLocalMap());
        }

        /**
         * Returns the result of the transition.
         * 
         * @return the result of the transition
         */
        public ConstantFoldingElement getOutputElement() {
            return outputElement;
        }

        @Override
        public void caseAssignStmt(AssignStmt stmt) {
            JimpleLocal lValLocal;
            if (stmt.getLeftOp() instanceof JimpleLocal) {
                lValLocal = (JimpleLocal) stmt.getLeftOp();
                if (!ConstantFoldingElement.isLocalTypeAccepted(lValLocal.getType())) {
                    return;
                }
            } else if (!(stmt.getLeftOp() instanceof Ref)) {
                assert false : "Something went horribly wrong!";
                return;
            } else {
                return; // ignore
            }

            Value rVal = stmt.getRightOp();
            Evaluator valueSwitch = new Evaluator(inputElement);
            rVal.apply(valueSwitch);
            ConstantFoldingElement.Value rhs = valueSwitch.getResult();

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
            // ignore (identity transition)
        }

        @Override
        public void caseIdentityStmt(IdentityStmt stmt) {
            // this is only used for parameter initialization (so set to TOP)

            JimpleLocal lValLocal;
            if (stmt.getLeftOp() instanceof JimpleLocal) {
                lValLocal = (JimpleLocal) stmt.getLeftOp();
                if (!ConstantFoldingElement.isLocalTypeAccepted(lValLocal.getType())) {
                    return;
                }
            } else if (!(stmt.getLeftOp() instanceof Ref)) {
                assert false : "Something went horribly wrong!";
                return;
            } else {
                return; // ignore
            }

            outputElement.setValue(lValLocal, ConstantFoldingElement.Value.getTop());
        }

        @Override
        public void caseIfStmt(IfStmt stmt) {
            // ignore
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
            assert false : "Something went horribly wrong!";
        }

    }

    /**
     * @author Nils Jessen
     * @author Sebastian Rauch
     * 
     *         A {@code Evaluator} evaluates {@code JimpleValue}s (e. g. {@code soot.jimple.Expr}).
     */
    static class Evaluator implements JimpleValueSwitch {
        private static ConstantFoldingElement.Value top = ConstantFoldingElement.Value.getTop();
        private static ConstantFoldingElement.Value bottom = ConstantFoldingElement.Value.getBottom();

        private ConstantFoldingElement inputElement;

        private ConstantFoldingElement.Value result;

        /**
         * Creates a new {@code Evaluator} with the given input-{@code ConstantFoldingElement}.
         * 
         * @param inputElement
         *        the input for this {@code Evaluator}, on which the evaluation is based on
         */
        public Evaluator(ConstantFoldingElement inputElement) {
            this.inputElement = inputElement;
        }

        /**
         * Returns the result.
         * 
         * @return the result
         */
        public ConstantFoldingElement.Value getResult() {
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
            result = new ConstantFoldingElement.Value(c);
        }

        @Override
        public void caseLongConstant(LongConstant c) {
            result = new ConstantFoldingElement.Value(c);
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
         * constant # BOTTON = BOTTOM for # in {+,-,*,/,%,&,|,^,<<,>>,>>>}
         */

        @Override
        public void caseAddExpr(AddExpr expr) {
            Pair<ConstantFoldingElement.Value> operandValues = calcOperands(expr);
            ConstantFoldingElement.Value op1 = operandValues.getFirst();
            ConstantFoldingElement.Value op2 = operandValues.getSecond();

            if (op1.equals(top) || op2.equals(top)) {
                result = top;
            } else if (op1.isConst() && op2.isConst()) {
                result = new ConstantFoldingElement.Value(
                        (ArithmeticConstant) op1.getConstant().add(op2.getConstant()));
            } else {
                result = bottom;
            }
        }

        @Override
        public void caseSubExpr(SubExpr expr) {
            Pair<ConstantFoldingElement.Value> operandValues = calcOperands(expr);
            ConstantFoldingElement.Value op1 = operandValues.getFirst();
            ConstantFoldingElement.Value op2 = operandValues.getSecond();

            if (op1.equals(top) || op2.equals(top)) {
                result = top;
            } else if (op1.isConst() && op2.isConst()) {
                result = new ConstantFoldingElement.Value(
                        (ArithmeticConstant) op1.getConstant().subtract(op2.getConstant()));
            } else {
                result = bottom;
            }
        }

        @Override
        public void caseMulExpr(MulExpr expr) {
            Pair<ConstantFoldingElement.Value> operandValues = calcOperands(expr);
            ConstantFoldingElement.Value op1 = operandValues.getFirst();
            ConstantFoldingElement.Value op2 = operandValues.getSecond();

            ConstantRetriever constSwitch = new ConstantRetriever();
            if (op1.isConst()) {
                op1.getConstant().apply(constSwitch);
                if (constSwitch.getValue() == 0) {
                    if (op2.isConst()) {
                        result = new ConstantFoldingElement.Value(
                                (ArithmeticConstant) op1.getConstant().multiply(op2.getConstant()));
                    } else if (op2.equals(bottom)) {
                        result = bottom;
                    } else {
                        result = op1;
                    }
                } else {
                    if (op2.isConst()) {
                        result = new ConstantFoldingElement.Value(
                                (ArithmeticConstant) op1.getConstant().multiply(op2.getConstant()));
                    } else {
                        // op2 is TOP or BOTTOM
                        result = op2;
                    }
                }
            } else {
                if (op2.isConst()) {
                    op2.getConstant().apply(constSwitch);
                    if (constSwitch.getValue() == 0) {
                        if (op1.equals(bottom)) {
                            result = bottom;
                        } else {
                            result = op2;
                        }
                    } else {
                        // op1 is TOP or BOTTOM
                        result = op1;
                    }
                } else {
                    // op1 and op2 are either TOP or BOTTOM
                    if (op1.equals(top) || op2.equals(top)) {
                        result = top;
                    } else {
                        result = bottom;
                    }
                }
            }
        }

        @Override
        public void caseDivExpr(DivExpr expr) {
            Pair<ConstantFoldingElement.Value> operandValues = calcOperands(expr);
            ConstantFoldingElement.Value op1 = operandValues.getFirst();
            ConstantFoldingElement.Value op2 = operandValues.getSecond();

            ConstantRetriever constSwitch = new ConstantRetriever();
            if (op2.equals(top)) {
                result = top;
            } else if (op2.isConst()) {
                op2.getConstant().apply(constSwitch);
                if (constSwitch.getValue() == 0) {
                    if (op1.equals(bottom)) {
                        result = bottom;
                    } else {
                        result = top;
                    }
                } else {
                    if (op1.isConst()) {
                        result = new ConstantFoldingElement.Value(
                                (ArithmeticConstant) op1.getConstant().divide(op2.getConstant()));
                    } else {
                        // op1 is TOP or BOTTOM
                        result = op1;
                    }
                }
            } else {
                // op2 is BOTTOM
                if (op1.equals(top)) {
                    result = top;
                } else {
                    result = bottom;
                }
            }
        }

        @Override
        public void caseRemExpr(RemExpr expr) {
            Pair<ConstantFoldingElement.Value> operandValues = calcOperands(expr);
            ConstantFoldingElement.Value op1 = operandValues.getFirst();
            ConstantFoldingElement.Value op2 = operandValues.getSecond();

            ConstantRetriever constSwitch = new ConstantRetriever();
            if (op2.equals(top)) {
                result = top;
            } else if (op2.isConst()) {
                op2.getConstant().apply(constSwitch);
                if (constSwitch.getValue() == 0) {
                    if (op1.equals(bottom)) {
                        result = bottom;
                    } else {
                        result = top;
                    }
                } else {
                    if (op1.isConst()) {
                        result = new ConstantFoldingElement.Value(
                                (ArithmeticConstant) op1.getConstant().remainder(op2.getConstant()));
                    } else {
                        // op1 is TOP or BOTTOM
                        result = op1;
                    }
                }
            } else {
                // op2 is BOTTOM
                if (op1.equals(top)) {
                    result = top;
                } else {
                    result = bottom;
                }
            }
        }

        @Override
        public void caseNegExpr(NegExpr expr) {
            Value negOp = expr.getOp();
            Evaluator negSwitch = new Evaluator(inputElement);
            negOp.apply(negSwitch);
            ConstantFoldingElement.Value val = negSwitch.getResult();
            if (val.isConst()) {
                ArithmeticConstant res = (ArithmeticConstant) val.getConstant().negate();
                result = new ConstantFoldingElement.Value(res);
            } else {
                result = val;
            }
        }

        @Override
        public void caseAndExpr(AndExpr expr) {
            Pair<ConstantFoldingElement.Value> operandValues = calcOperands(expr);
            ConstantFoldingElement.Value op1 = operandValues.getFirst();
            ConstantFoldingElement.Value op2 = operandValues.getSecond();

            ConstantRetriever constSwitch = new ConstantRetriever();
            if (op1.isConst()) {
                op1.getConstant().apply(constSwitch);
                if (constSwitch.getValue() == 0) {
                    if (op2.isConst()) {
                        result = new ConstantFoldingElement.Value(op1.getConstant().and(op2.getConstant()));
                    } else if (op2.equals(bottom)) {
                        result = bottom;
                    } else {
                        result = op1;
                    }
                } else {
                    if (op2.isConst()) {
                        result = new ConstantFoldingElement.Value(op1.getConstant().and(op2.getConstant()));
                    } else {
                        // op2 is TOP or BOTTOM
                        result = op2;
                    }
                }
            } else {
                if (op2.isConst()) {
                    op2.getConstant().apply(constSwitch);
                    if (constSwitch.getValue() == 0) {
                        if (op1.equals(bottom)) {
                            result = bottom;
                        } else {
                            result = op2;
                        }
                    } else {
                        // op1 is TOP or BOTTOM
                        result = op1;
                    }
                } else {
                    // op1 and op2 are either TOP or BOTTOM
                    if (op1.equals(top) || op2.equals(top)) {
                        result = top;
                    } else {
                        result = bottom;
                    }
                }
            }
        }

        @Override
        public void caseOrExpr(OrExpr expr) {
            Pair<ConstantFoldingElement.Value> operandValues = calcOperands(expr);
            ConstantFoldingElement.Value op1 = operandValues.getFirst();
            ConstantFoldingElement.Value op2 = operandValues.getSecond();

            ConstantRetriever constSwitch = new ConstantRetriever();
            if (op1.isConst()) {
                op1.getConstant().apply(constSwitch);
                // (long) -1 = 0xFFFFFFFFFFFFFFFF
                if (constSwitch.getValue() == -1) {
                    if (op2.isConst()) {
                        result = new ConstantFoldingElement.Value(op1.getConstant().or(op2.getConstant()));
                    } else if (op2.equals(bottom)) {
                        result = bottom;
                    } else {
                        result = op1;
                    }
                } else {
                    if (op2.isConst()) {
                        result = new ConstantFoldingElement.Value(op1.getConstant().or(op2.getConstant()));
                    } else {
                        // op2 is TOP or BOTTOM
                        result = op2;
                    }
                }
            } else {
                if (op2.isConst()) {
                    op2.getConstant().apply(constSwitch);
                    // (long) -1 = 0xFFFFFFFFFFFFFFFF
                    if (constSwitch.getValue() == -1) {
                        if (op1.equals(bottom)) {
                            result = bottom;
                        } else {
                            result = op2;
                        }
                    } else {
                        // op1 is TOP or BOTTOM
                        result = op1;
                    }
                } else {
                    // op1 and op2 are either TOP or BOTTOM
                    if (op1.equals(top) || op2.equals(top)) {
                        result = top;
                    } else {
                        result = bottom;
                    }
                }
            }
        }

        @Override
        public void caseXorExpr(XorExpr expr) {
            Pair<ConstantFoldingElement.Value> operandValues = calcOperands(expr);
            ConstantFoldingElement.Value op1 = operandValues.getFirst();
            ConstantFoldingElement.Value op2 = operandValues.getSecond();

            if (op1.equals(top) || op2.equals(top)) {
                result = top;
            } else if (op1.equals(bottom) || op2.equals(bottom)) {
                result = bottom;
            } else {
                // both constant
                result = new ConstantFoldingElement.Value(op1.getConstant().xor(op2.getConstant()));
            }
        }

        @Override
        public void caseShlExpr(ShlExpr expr) {
            Pair<ConstantFoldingElement.Value> operandValues = calcOperands(expr);
            ConstantFoldingElement.Value op1 = operandValues.getFirst();
            ConstantFoldingElement.Value op2 = operandValues.getSecond();

            if (op1.equals(top) || op1.equals(bottom)) {
                result = op1;
            } else {
                // op1 constant
                ConstantRetriever constSwitch = new ConstantRetriever();
                op1.getConstant().apply(constSwitch);
                if (constSwitch.getValue() == 0) {
                    if (op2.equals(bottom)) {
                        result = bottom;
                    } else {
                        result = op1;
                    }
                } else {
                    if (op2.equals(top) || op2.equals(bottom)) {
                        result = op2;
                    } else {
                        result = new ConstantFoldingElement.Value(op1.getConstant().shiftLeft(op2.getConstant()));
                    }
                }
            }
        }

        @Override
        public void caseShrExpr(ShrExpr expr) {
            Pair<ConstantFoldingElement.Value> operandValues = calcOperands(expr);
            ConstantFoldingElement.Value op1 = operandValues.getFirst();
            ConstantFoldingElement.Value op2 = operandValues.getSecond();

            if (op1.equals(top) || op1.equals(bottom)) {
                result = op1;
            } else {
                // op1 constant
                ConstantRetriever constSwitch = new ConstantRetriever();
                op1.getConstant().apply(constSwitch);
                if (constSwitch.getValue() == 0) {
                    if (op2.equals(bottom)) {
                        result = bottom;
                    } else {
                        result = op1;
                    }
                } else {
                    if (op2.equals(top) || op2.equals(bottom)) {
                        result = op2;
                    } else {
                        result = new ConstantFoldingElement.Value(op1.getConstant().shiftRight(op2.getConstant()));
                    }
                }
            }
        }

        @Override
        public void caseUshrExpr(UshrExpr expr) {
            Pair<ConstantFoldingElement.Value> operandValues = calcOperands(expr);
            ConstantFoldingElement.Value op1 = operandValues.getFirst();
            ConstantFoldingElement.Value op2 = operandValues.getSecond();

            if (op1.equals(top) || op1.equals(bottom)) {
                result = op1;
            } else {
                // op1 constant
                ConstantRetriever constSwitch = new ConstantRetriever();
                op1.getConstant().apply(constSwitch);
                if (constSwitch.getValue() == 0) {
                    if (op2.equals(bottom)) {
                        result = bottom;
                    } else {
                        result = op1;
                    }
                } else {
                    if (op2.equals(top) || op2.equals(bottom)) {
                        result = op2;
                    } else {
                        result = new ConstantFoldingElement.Value(
                                op1.getConstant().unsignedShiftRight(op2.getConstant()));
                    }
                }
            }
        }

        @Override
        public void caseCmplExpr(CmplExpr expr) {
            // ignore
        }

        @Override
        public void caseCmpgExpr(CmpgExpr expr) {
            // ignore
        }

        @Override
        public void caseCmpExpr(CmpExpr expr) {
            // ignore
        }

        @Override
        public void caseCastExpr(CastExpr expr) {
            Value castOp = expr.getOp();
            Evaluator castSwitch = new Evaluator(inputElement);
            castOp.apply(castSwitch);
            ConstantFoldingElement.Value val = castSwitch.getResult();
            if (val.isConst()) {
                ConstantRetriever constSwitch = new ConstantRetriever();
                val.getConstant().apply(constSwitch);
                long numericVal = constSwitch.value.longValue();
                ArithmeticConstant res = castIntegerValue(numericVal, expr.getCastType());
                result = new ConstantFoldingElement.Value(res);
            } else {
                result = val; // TOP and BOTTOM are unchanged
            }
        }

        @Override
        public void caseEqExpr(EqExpr expr) {
            Pair<ConstantFoldingElement.Value> operandValues = calcOperands(expr);
            ConstantFoldingElement.Value op1 = operandValues.getFirst();
            ConstantFoldingElement.Value op2 = operandValues.getSecond();

            if (op1.equals(top) || op2.equals(top)) {
                result = top;
            } else if (op1.equals(bottom) || op2.equals(bottom)) {
                result = bottom;
            } else {
                result = new ConstantFoldingElement.Value(
                        (ArithmeticConstant) op1.getConstant().equalEqual(op2.getConstant()));
            }
        }

        @Override
        public void caseGeExpr(GeExpr expr) {
            Pair<ConstantFoldingElement.Value> operandValues = calcOperands(expr);
            ConstantFoldingElement.Value op1 = operandValues.getFirst();
            ConstantFoldingElement.Value op2 = operandValues.getSecond();

            if (op1.equals(top) || op2.equals(top)) {
                result = top;
            } else if (op1.equals(bottom) || op2.equals(bottom)) {
                result = bottom;
            } else {
                result = new ConstantFoldingElement.Value(
                        (ArithmeticConstant) op1.getConstant().greaterThanOrEqual(op2.getConstant()));
            }
        }

        @Override
        public void caseGtExpr(GtExpr expr) {
            Pair<ConstantFoldingElement.Value> operandValues = calcOperands(expr);
            ConstantFoldingElement.Value op1 = operandValues.getFirst();
            ConstantFoldingElement.Value op2 = operandValues.getSecond();

            if (op1.equals(top) || op2.equals(top)) {
                result = top;
            } else if (op1.equals(bottom) || op2.equals(bottom)) {
                result = bottom;
            } else {
                result = new ConstantFoldingElement.Value(
                        (ArithmeticConstant) op1.getConstant().greaterThan(op2.getConstant()));
            }
        }

        @Override
        public void caseLeExpr(LeExpr expr) {
            Pair<ConstantFoldingElement.Value> operandValues = calcOperands(expr);
            ConstantFoldingElement.Value op1 = operandValues.getFirst();
            ConstantFoldingElement.Value op2 = operandValues.getSecond();

            if (op1.equals(top) || op2.equals(top)) {
                result = top;
            } else if (op1.equals(bottom) || op2.equals(bottom)) {
                result = bottom;
            } else {
                result = new ConstantFoldingElement.Value(
                        (ArithmeticConstant) op1.getConstant().lessThanOrEqual(op2.getConstant()));
            }
        }

        @Override
        public void caseLtExpr(LtExpr expr) {
            Pair<ConstantFoldingElement.Value> operandValues = calcOperands(expr);
            ConstantFoldingElement.Value op1 = operandValues.getFirst();
            ConstantFoldingElement.Value op2 = operandValues.getSecond();

            if (op1.equals(top) || op2.equals(top)) {
                result = top;
            } else if (op1.equals(bottom) || op2.equals(bottom)) {
                result = bottom;
            } else {
                result = new ConstantFoldingElement.Value(
                        (ArithmeticConstant) op1.getConstant().lessThan(op2.getConstant()));
            }
        }

        @Override
        public void caseNeExpr(NeExpr expr) {
            Pair<ConstantFoldingElement.Value> operandValues = calcOperands(expr);
            ConstantFoldingElement.Value op1 = operandValues.getFirst();
            ConstantFoldingElement.Value op2 = operandValues.getSecond();

            if (op1.equals(top) || op2.equals(top)) {
                result = top;
            } else if (op1.equals(bottom) || op2.equals(bottom)) {
                result = bottom;
            } else {
                result = new ConstantFoldingElement.Value(
                        (ArithmeticConstant) op1.getConstant().notEqual(op2.getConstant()));
            }
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
                assert false : "Something went horribly wrong!";
            }

            result = inputElement.getValue((JimpleLocal) local);
        }

        @Override
        public void defaultCase(Object arg0) {
            assert false : "Something went horribly wrong!";
        }

        private Pair<ConstantFoldingElement.Value> calcOperands(BinopExpr binOpExpr) {
            Value op1 = binOpExpr.getOp1();
            Evaluator switch1 = new Evaluator(inputElement);
            op1.apply(switch1);

            Value op2 = binOpExpr.getOp2();
            Evaluator switch2 = new Evaluator(inputElement);
            op2.apply(switch2);

            return new Pair<ConstantFoldingElement.Value>(switch1.getResult(), switch2.getResult());
        }

        private ArithmeticConstant castIntegerValue(long value, Type castType) {
            if (castType == LongType.v()) {
                return LongConstant.v(value);
            } else if (castType == IntType.v()) {
                return IntConstant.v((int) value);
            } else if (castType == ShortType.v()) {
                return IntConstant.v((short) value);
            } else if (castType == CharType.v()) {
                return IntConstant.v((char) value);
            } else if (castType == ByteType.v()) {
                return IntConstant.v((byte) value);
            } else if (castType == BooleanType.v()) {
                int boolVal = value == 0 ? 0 : 1;
                return IntConstant.v(boolVal);
            }

            return null;
        }

    }

    /**
     * @author Nils Jessen
     * @author Sebastian Rauch
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

}
