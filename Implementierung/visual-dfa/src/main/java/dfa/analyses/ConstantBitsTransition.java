package dfa.analyses;

import dfa.analyses.ConstantBitsElement.BitValue;
import dfa.analyses.ConstantBitsElement.BitValueArray;
import dfa.analyses.ConstantFoldingTransition.Evaluator;
import dfa.framework.Transition;
import dfa.framework.UnsupportedStatementException;
import dfa.framework.UnsupportedValueException;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.PrimType;
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
 * 
 *         A {@code ConstantBitsTransition} performs the transition for a {@code ConstantBitsAnalysis}.
 */
public class ConstantBitsTransition implements Transition<ConstantBitsElement> {

    @Override
    public ConstantBitsElement transition(ConstantBitsElement element, Unit unit) {
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

        private ConstantBitsElement inputElement;

        private ConstantBitsElement outputElement;

        /**
         * Creates a {@code Transitioner} with the given input-{@code ConstantBitsElement}.
         * 
         * @param inputElement
         *        the input-{@code ConstantBitsElement}
         */
        public Transitioner(ConstantBitsElement inputElement) {
            this.inputElement = inputElement;
            outputElement = new ConstantBitsElement(inputElement.getLocalMap());
        }

        /**
         * Returns the result of the transition.
         * 
         * @return the result of the transition
         */
        public ConstantBitsElement getOutputElement() {
            return outputElement;
        }

        @Override
        public void caseAssignStmt(AssignStmt stmt) {
            JimpleLocal lValLocal;
            if (stmt.getLeftOp() instanceof JimpleLocal) {
                lValLocal = (JimpleLocal) stmt.getLeftOp();
                Type type = lValLocal.getType();
                if (!(type instanceof PrimType)) {
                    // ignore
                    return;
                }
            } else if (!(stmt.getLeftOp() instanceof Ref)) {
                assert false : "Cannot assign to sth that is neither JimpleLocal nor Ref!";
                return;
            } else {
                // ignore
                return;
            }

            Value rVal = stmt.getRightOp();
            Evaluator valueSwitch = new Evaluator(inputElement);
            rVal.apply(valueSwitch);
            ConstantBitsElement.BitValueArray rhs = valueSwitch.getResult();
            if (rhs == ConstantBitsElement.BitValueArray.getTop()) {
                Type t = lValLocal.getType();
                if (t instanceof BooleanType || t instanceof ByteType || t instanceof CharType || t instanceof ShortType
                        || t instanceof IntType) {
                    rhs = ConstantBitsElement.BitValueArray.getIntTop();
                } else if (t instanceof LongType) {
                    rhs = ConstantBitsElement.BitValueArray.getLongTop();
                } else {
                    throw new IllegalStateException("Cannot assign to local that is not int or long");
                }
            }
            outputElement.setValue(lValLocal, rhs);
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
        private static ConstantBitsElement.BitValueArray top = ConstantBitsElement.BitValueArray.getTop();

        private ConstantBitsElement inputElement;

        private ConstantBitsElement.BitValueArray result;

        /**
         * Creates a new {@code Evaluator} with the given input-{@code ConstantBitsElement}.
         * 
         * @param inputElement
         *        the input for this {@code Evaluator}, on which the evaluation is based on
         */
        public Evaluator(ConstantBitsElement inputElement) {
            this.inputElement = inputElement;
        }

        /**
         * Returns the result.
         * 
         * @return the result
         */
        public ConstantBitsElement.BitValueArray getResult() {
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
            result = new ConstantBitsElement.BitValueArray(c);
        }

        @Override
        public void caseLongConstant(LongConstant c) {
            result = new ConstantBitsElement.BitValueArray(c);
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

        /**
         * Checks for addition if one of the given {@code BitValue}s equals TOP or BOTTOM and if so, calculates the
         * transfer
         * 
         * @param opAndTransfer
         *        the {@code BitValue}s to check
         * @param check
         *        either TOP or BOTTOM, depending for which you want to check
         * @return if one of the bits was {@code check}, and the calculated transfer
         */
        private BitValue[] checkAddTopBottom(BitValue[] opAndTransfer, BitValue check) {
            // opAndTransfer = { op1Values[i], op2Values[i], transfer }
            // we will return resValTransfer, with the first entry beeing the resulting bit
            // and the second entry beeing the calculated transfer
            BitValue[] resValTransfer = new BitValue[2];
            resValTransfer[0] = BitValue.ZERO;

            for (int i = 0; i < 3; i++) {
                if (opAndTransfer[i] == check) {
                    // if one of the three BitValues is TOP/BOTTOM, the resulting bit is the same
                    resValTransfer[0] = check;

                    // calculating the transfer:
                    boolean isOne = true;
                    boolean isZero = true;
                    for (int j = 0; j < 3; j++) {
                        if (j != i && opAndTransfer[j] != BitValue.ONE) {
                            isOne = false;
                        }
                        if (j != i && opAndTransfer[j] != BitValue.ZERO) {
                            isZero = false;
                        }
                    }
                    if (isOne) {
                        resValTransfer[1] = BitValue.ONE;
                    } else if (isZero) {
                        resValTransfer[1] = BitValue.ZERO;
                    } else {
                        resValTransfer[1] = check;
                    }
                    break;
                }
            }
            return resValTransfer;
        }

        @Override
        public void caseAddExpr(AddExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            BitValueArray op1 = operandValues.getFirst();
            BitValueArray op2 = operandValues.getSecond();

            if (op1.equals(top) || op2.equals(top)) {
                // if one is top, the result is top
                result = top;

            } else if (op1.isConst() && op2.isConst()) {
                // if both are constants, we can just add the corresponding ArithmeticConstants
                result = new BitValueArray((ArithmeticConstant) op1.getConstant().add(op2.getConstant()));

            } else {
                // both are not top and at least one is not a constant
                // therefore we have to add all the bits one by one

                // Making sure we have the right length in both:
                int l1 = op1.getLength();
                int l2 = op2.getLength();
                int length = Math.max(l1, l2);
                BitValue[] op1Values = new BitValue[length];
                BitValue[] op2Values = new BitValue[length];
                BitValue[] bitValues = new BitValue[length];
                BitValue transfer = BitValue.ZERO;

                for (int i = 0; i < length; i++) {
                    if (i >= l1) {
                        op1Values[i] = BitValue.ZERO;
                    } else {
                        op1Values[i] = op1.getBitValues()[i];
                    }
                    if (i >= l2) {
                        op2Values[i] = BitValue.ZERO;
                    } else {
                        op2Values[i] = op2.getBitValues()[i];
                    }

                    // the actual addition bit by bit:
                    // first checking if one of the bits is TOP or BOTTOM
                    BitValue[] opAndTransfer = { op1Values[i], op2Values[i], transfer };
                    BitValue[] checkTop = checkAddTopBottom(opAndTransfer, BitValue.TOP);
                    BitValue[] checkBottom = checkAddTopBottom(opAndTransfer, BitValue.BOTTOM);

                    if (checkTop[0] == BitValue.TOP) {
                        bitValues[i] = BitValue.TOP;
                        transfer = checkTop[1];
                    } else if (checkBottom[0] == BitValue.BOTTOM) {
                        bitValues[i] = BitValue.BOTTOM;
                        transfer = checkBottom[1];

                    } else {
                        // both bits and the transfer are ONE or ZERO so we convert them to int and add them
                        int op1Bit = BitValueArray.bitValueToBoolean(op1Values[i]) ? 1 : 0;
                        int op2Bit = BitValueArray.bitValueToBoolean(op2Values[i]) ? 1 : 0;
                        int transferBit = BitValueArray.bitValueToBoolean(transfer) ? 1 : 0;
                        int res = op1Bit + op2Bit + transferBit;
                        transfer = BitValueArray.booleanToBitValue(res > 1);
                        bitValues[i] = BitValueArray.booleanToBitValue((res & 1) != 0);
                    }
                }
                result = new BitValueArray(bitValues);
            }
        }

        /**
         * Checks for subtraction if one of the given {@code BitValue}s equals TOP or BOTTOM and if so, calculates the
         * transfer
         * 
         * @param opAndTransfer
         *        the {@code BitValue}s to check
         * @param check
         *        either TOP or BOTTOM, depending for which you want to check
         * @return if one of the bits was {@code check}, and the calculated transfer
         */
        private BitValue[] checkSubTopBottom(BitValue[] opAndTransfer, BitValue check) {
            // opAndTransfer = { op1Values[i], op2Values[i], transfer }
            // we will return resValTransfer, with the first entry beeing the resulting bit
            // and the second entry beeing the calculated transfer
            BitValue[] resValTransfer = new BitValue[2];
            resValTransfer[0] = BitValue.ZERO;

            for (int i = 0; i < 3; i++) {
                if (opAndTransfer[i] == check) {
                    // if one of the three BitValues is TOP/BOTTOM, the resulting bit is the same
                    resValTransfer[0] = check;

                    // calculating the transfer:
                    boolean isOne = true;
                    boolean isZero = true;
                    if (opAndTransfer[0] == BitValue.ONE) {
                        isOne = false;
                        if (opAndTransfer[1] != BitValue.ZERO && opAndTransfer[2] != BitValue.ZERO) {
                            isZero = false;
                        }
                    } else if (opAndTransfer[0] == BitValue.ZERO) {
                        isZero = false;
                        if (opAndTransfer[1] != BitValue.ONE && opAndTransfer[2] != BitValue.ONE) {
                            isOne = false;
                        }
                    } else {
                        if (opAndTransfer[1] != BitValue.ONE || opAndTransfer[2] != BitValue.ONE) {
                            isOne = false;
                        }
                        if (opAndTransfer[1] != BitValue.ZERO || opAndTransfer[2] != BitValue.ZERO) {
                            isZero = false;
                        }
                    }
                    if (isOne) {
                        resValTransfer[1] = BitValue.ONE;
                    } else if (isZero) {
                        resValTransfer[1] = BitValue.ZERO;
                    } else {
                        resValTransfer[1] = check;
                    }
                    break;
                }
            }
            return resValTransfer;
        }

        @Override
        public void caseSubExpr(SubExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ConstantBitsElement.BitValueArray op1 = operandValues.getFirst();
            ConstantBitsElement.BitValueArray op2 = operandValues.getSecond();

            if (op1.equals(top) || op2.equals(top)) {
                result = top;
                // if one is top, the result is top

            } else if (op1.isConst() && op2.isConst()) {
                result = new BitValueArray((ArithmeticConstant) op1.getConstant().subtract(op2.getConstant()));
                // if both are constants, we can just subtract the corresponding ArithmeticConstants

            } else {
                // both are not top and at least one is not a constant
                // therefore we have to subtract all the bits one by one

                // Making sure we have the right length in both:
                int l1 = op1.getLength();
                int l2 = op2.getLength();
                int length = Math.max(l1, l2);
                BitValue[] op1Values = new BitValue[length];
                BitValue[] op2Values = new BitValue[length];
                BitValue[] bitValues = new BitValue[length];
                BitValue transfer = BitValue.ZERO;

                for (int i = 0; i < length; i++) {
                    if (i >= l1) {
                        op1Values[i] = BitValue.ZERO;
                    } else {
                        op1Values[i] = op1.getBitValues()[i];
                    }
                    if (i >= l2) {
                        op2Values[i] = BitValue.ZERO;
                    } else {
                        op2Values[i] = op2.getBitValues()[i];
                    }

                    // the actual subtraction bit by bit:
                    // first checking if one of the bits is TOP or BOTTOM
                    BitValue[] opAndTransfer = { op1Values[i], op2Values[i], transfer };
                    BitValue[] checkTop = checkSubTopBottom(opAndTransfer, BitValue.TOP);
                    BitValue[] checkBottom = checkSubTopBottom(opAndTransfer, BitValue.BOTTOM);

                    if (checkTop[0] == BitValue.TOP) {
                        bitValues[i] = BitValue.TOP;
                        transfer = checkTop[1];
                    } else if (checkBottom[0] == BitValue.BOTTOM) {
                        bitValues[i] = BitValue.BOTTOM;
                        transfer = checkBottom[1];

                    } else {
                        // both bits and the transfer are ONE or ZERO so we convert them to int and subtract them
                        int op1Bit = BitValueArray.bitValueToBoolean(op1Values[i]) ? 1 : 0;
                        int op2Bit = BitValueArray.bitValueToBoolean(op2Values[i]) ? 1 : 0;
                        int transferBit = BitValueArray.bitValueToBoolean(transfer) ? 1 : 0;
                        int res = op1Bit + op2Bit + transferBit;
                        transfer = BitValueArray.booleanToBitValue(op1Bit < op2Bit + transferBit);
                        bitValues[i] = BitValueArray.booleanToBitValue((res & 1) != 0);
                    }
                }
                result = new BitValueArray(bitValues);
            }
        }

        @Override
        public void caseMulExpr(MulExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ConstantBitsElement.BitValueArray op1 = operandValues.getFirst();
            ConstantBitsElement.BitValueArray op2 = operandValues.getSecond();

            // TODO implement Expression
        }

        @Override
        public void caseDivExpr(DivExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ConstantBitsElement.BitValueArray op1 = operandValues.getFirst();
            ConstantBitsElement.BitValueArray op2 = operandValues.getSecond();

            // TODO implement Expression
        }

        @Override
        public void caseRemExpr(RemExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ConstantBitsElement.BitValueArray op1 = operandValues.getFirst();
            ConstantBitsElement.BitValueArray op2 = operandValues.getSecond();

            // TODO implement Expression
        }

        @Override
        public void caseNegExpr(NegExpr expr) {
            Value negOp = expr.getOp();
            Evaluator negSwitch = new Evaluator(inputElement);
            negOp.apply(negSwitch);
            BitValueArray val = negSwitch.getResult();

            if (val.equals(top)) {
                result = top;
                // neg(top) = top

            } else {
                // not top, so we negate the bits one by one
                int length = val.getLength();
                BitValue[] resBitValues = new BitValue[length];
                BitValue[] opBitValues = val.getBitValues();
                for (int i = 0; i < length; i++) {
                    if (opBitValues[i].equals(BitValue.TOP) || opBitValues[i].equals(BitValue.BOTTOM)) {
                        resBitValues[i] = opBitValues[i];
                    } else {
                        resBitValues[i] = (opBitValues[i] == BitValue.ZERO) ? BitValue.ONE : BitValue.ZERO;
                    }
                }

                // resValue is negOp inverted bitwise, now we add 1 because 2-complement
                BitValueArray resValue = new BitValueArray(resBitValues);
                if (resValue.isConst()) {
                    // if constant, we can just add the Arithmetic Constant 1
                    ArithmeticConstant c;
                    if (length == 32) {
                        c = IntConstant.v(1);
                    } else {
                        c = LongConstant.v(1);
                    }
                    result = new BitValueArray((ArithmeticConstant) resValue.getConstant().add(c));

                } else {
                    // if not Constant we have to add bitwise like in AddExpression
                    BitValue transfer = BitValue.ZERO;
                    BitValue[] bitValues = new BitValue[length];
                    for (int j = 0; j < length; j++) {
                        int oneBit = (j == 0) ? 1 : 0;
                        BitValue oneBitValue = BitValueArray.booleanToBitValue(oneBit == 1);
                        // oneBit and oneBitValue are the "1" that will be added, they are 1/ONE if and if only j is 0

                        // Checking if one of the bits is TOP/BOTTOM:
                        BitValue[] opAndTransfer = { resBitValues[j], oneBitValue, transfer };
                        BitValue[] checkTop = checkSubTopBottom(opAndTransfer, BitValue.TOP);
                        BitValue[] checkBottom = checkSubTopBottom(opAndTransfer, BitValue.BOTTOM);

                        if (checkTop[0] == BitValue.TOP) {
                            bitValues[j] = BitValue.TOP;
                            transfer = checkTop[1];
                        } else if (checkBottom[0] == BitValue.BOTTOM) {
                            bitValues[j] = BitValue.BOTTOM;
                            transfer = checkBottom[1];

                        } else {
                            // both bits and the transfer are ONE or ZERO so we convert them to int and add them
                            int resBit = BitValueArray.bitValueToBoolean(resBitValues[j]) ? 1 : 0;
                            int transferBit = BitValueArray.bitValueToBoolean(transfer) ? 1 : 0;
                            int res = resBit + oneBit + transferBit;
                            transfer = BitValueArray.booleanToBitValue(resBit < oneBit + transferBit);
                            bitValues[j] = BitValueArray.booleanToBitValue((res & 1) != 0);
                        }
                    }
                    result = new BitValueArray(bitValues);
                }
            }
        }

        @Override
        public void caseAndExpr(AndExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ConstantBitsElement.BitValueArray op1 = operandValues.getFirst();
            ConstantBitsElement.BitValueArray op2 = operandValues.getSecond();

            // TODO implement Expression
        }

        @Override
        public void caseOrExpr(OrExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ConstantBitsElement.BitValueArray op1 = operandValues.getFirst();
            ConstantBitsElement.BitValueArray op2 = operandValues.getSecond();

            // TODO implement Expression
        }

        @Override
        public void caseXorExpr(XorExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ConstantBitsElement.BitValueArray op1 = operandValues.getFirst();
            ConstantBitsElement.BitValueArray op2 = operandValues.getSecond();

            // TODO implement Expression
        }

        @Override
        public void caseShlExpr(ShlExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ConstantBitsElement.BitValueArray op1 = operandValues.getFirst();
            ConstantBitsElement.BitValueArray op2 = operandValues.getSecond();

            // TODO implement Expression
        }

        @Override
        public void caseShrExpr(ShrExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ConstantBitsElement.BitValueArray op1 = operandValues.getFirst();
            ConstantBitsElement.BitValueArray op2 = operandValues.getSecond();

            // TODO implement Expression
        }

        @Override
        public void caseUshrExpr(UshrExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ConstantBitsElement.BitValueArray op1 = operandValues.getFirst();
            ConstantBitsElement.BitValueArray op2 = operandValues.getSecond();

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
            ConstantBitsElement.BitValueArray op1 = operandValues.getFirst();
            ConstantBitsElement.BitValueArray op2 = operandValues.getSecond();

            // TODO implement Expression
        }

        @Override
        public void caseGeExpr(GeExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ConstantBitsElement.BitValueArray op1 = operandValues.getFirst();
            ConstantBitsElement.BitValueArray op2 = operandValues.getSecond();

            // TODO implement Expression
        }

        @Override
        public void caseGtExpr(GtExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ConstantBitsElement.BitValueArray op1 = operandValues.getFirst();
            ConstantBitsElement.BitValueArray op2 = operandValues.getSecond();

            // TODO implement Expression
        }

        @Override
        public void caseLeExpr(LeExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ConstantBitsElement.BitValueArray op1 = operandValues.getFirst();
            ConstantBitsElement.BitValueArray op2 = operandValues.getSecond();

            // TODO implement Expression
        }

        @Override
        public void caseLtExpr(LtExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ConstantBitsElement.BitValueArray op1 = operandValues.getFirst();
            ConstantBitsElement.BitValueArray op2 = operandValues.getSecond();

            // TODO implement Expression
        }

        @Override
        public void caseNeExpr(NeExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ConstantBitsElement.BitValueArray op1 = operandValues.getFirst();
            ConstantBitsElement.BitValueArray op2 = operandValues.getSecond();

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
        private ConstantBitsElement.BitValueArray val1;
        private ConstantBitsElement.BitValueArray val2;

        public ValuePair(ConstantBitsElement.BitValueArray val1, ConstantBitsElement.BitValueArray val2) {
            this.val1 = val1;
            this.val2 = val2;
        }

        public ConstantBitsElement.BitValueArray getFirst() {
            return val1;
        }

        public ConstantBitsElement.BitValueArray getSecond() {
            return val2;
        }
    }

}
