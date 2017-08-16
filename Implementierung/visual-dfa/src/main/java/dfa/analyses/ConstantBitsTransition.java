package dfa.analyses;

import java.util.Arrays;
import java.util.Set;

import dfa.analyses.ConstantBitsElement.BitValue;
import dfa.analyses.ConstantBitsElement.BitValueArray;
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

    private static final int SPECIAL_CHAR_TRESHOLD = 5;

    private ConstantBitsJoin join;

    @Override
    public ConstantBitsElement transition(ConstantBitsElement element, Unit unit) {
        Transitioner stmtSwitch = new Transitioner(element, join);
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

        private ConstantBitsJoin join;

        /**
         * Creates a {@code Transitioner} with the given input-{@code ConstantBitsElement}.
         * 
         * @param inputElement
         *        the input-{@code ConstantBitsElement}
         */
        public Transitioner(ConstantBitsElement inputElement, ConstantBitsJoin join) {
            this.inputElement = inputElement;
            this.join = join;
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
            Evaluator valueSwitch = new Evaluator(inputElement, join);
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

        private ConstantBitsJoin join;

        /**
         * Creates a new {@code Evaluator} with the given input-{@code ConstantBitsElement} and the given
         * {@code ConstantBitsJoin}.
         * 
         * @param inputElement
         *        the input for this {@code Evaluator}, on which the evaluation is based on
         * 
         * @param inputElement
         *        the join for this {@code Evaluator}, used for multiplication, division, etc
         */
        public Evaluator(ConstantBitsElement inputElement, ConstantBitsJoin join) {
            this.inputElement = inputElement;
            this.join = join;
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
         * carry
         * 
         * @param opAndcarry
         *        the {@code BitValue}s to check
         * @param check
         *        either TOP or BOTTOM, depending for which you want to check
         * @return if one of the bits was {@code check}, and the calculated carry
         */
        private BitValue[] checkAddTopBottom(BitValue[] opAndcarry, BitValue check) {
            // opAndcarry = { op1Values[i], op2Values[i], carry }
            // we will return resValcarry, with the first entry beeing the resulting bit
            // and the second entry beeing the calculated carry
            BitValue[] resValcarry = new BitValue[2];
            resValcarry[0] = BitValue.ZERO;

            for (int i = 0; i < 3; i++) {
                if (opAndcarry[i] == check) {
                    // if one of the three BitValues is TOP/BOTTOM, the resulting bit is the same
                    resValcarry[0] = check;

                    // calculating the carry:
                    boolean isOne = true;
                    boolean isZero = true;
                    for (int j = 0; j < 3; j++) {
                        if (j != i && opAndcarry[j] != BitValue.ONE) {
                            isOne = false;
                        }
                        if (j != i && opAndcarry[j] != BitValue.ZERO) {
                            isZero = false;
                        }
                    }
                    if (isOne) {
                        resValcarry[1] = BitValue.ONE;
                    } else if (isZero) {
                        resValcarry[1] = BitValue.ZERO;
                    } else {
                        resValcarry[1] = check;
                    }
                    break;
                }
            }
            return resValcarry;
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
                BitValue carry = BitValue.ZERO;

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
                    BitValue[] opAndcarry = { op1Values[i], op2Values[i], carry };
                    BitValue[] checkTop = checkAddTopBottom(opAndcarry, BitValue.TOP);
                    BitValue[] checkBottom = checkAddTopBottom(opAndcarry, BitValue.BOTTOM);

                    if (checkTop[0] == BitValue.TOP) {
                        bitValues[i] = BitValue.TOP;
                        carry = checkTop[1];
                    } else if (checkBottom[0] == BitValue.BOTTOM) {
                        bitValues[i] = BitValue.BOTTOM;
                        carry = checkBottom[1];

                    } else {
                        // both bits and the carry are ONE or ZERO so we convert them to int and add them
                        int op1Bit = BitValueArray.bitValueToBoolean(op1Values[i]) ? 1 : 0;
                        int op2Bit = BitValueArray.bitValueToBoolean(op2Values[i]) ? 1 : 0;
                        int carryBit = BitValueArray.bitValueToBoolean(carry) ? 1 : 0;
                        int res = op1Bit + op2Bit + carryBit;
                        carry = BitValueArray.booleanToBitValue(res > 1);
                        bitValues[i] = BitValueArray.booleanToBitValue((res & 1) != 0);
                    }
                }
                result = new BitValueArray(bitValues);
            }
        }

        /**
         * Checks for subtraction if one of the given {@code BitValue}s equals TOP or BOTTOM and if so, calculates the
         * carry
         * 
         * @param opAndcarry
         *        the {@code BitValue}s to check
         * @param check
         *        either TOP or BOTTOM, depending for which you want to check
         * @return if one of the bits was {@code check}, and the calculated carry
         */
        private BitValue[] checkSubTopBottom(BitValue[] opAndcarry, BitValue check) {
            // opAndcarry = { op1Values[i], op2Values[i], carry }
            // we will return resValcarry, with the first entry beeing the resulting bit
            // and the second entry beeing the calculated carry
            BitValue[] resValcarry = new BitValue[2];
            resValcarry[0] = BitValue.ZERO;

            for (int i = 0; i < 3; i++) {
                if (opAndcarry[i] == check) {
                    // if one of the three BitValues is TOP/BOTTOM, the resulting bit is the same
                    resValcarry[0] = check;

                    // calculating the carry:
                    boolean isOne = true;
                    boolean isZero = true;
                    if (opAndcarry[0] == BitValue.ONE) {
                        isOne = false;
                        if (opAndcarry[1] != BitValue.ZERO && opAndcarry[2] != BitValue.ZERO) {
                            isZero = false;
                        }
                    } else if (opAndcarry[0] == BitValue.ZERO) {
                        isZero = false;
                        if (opAndcarry[1] != BitValue.ONE && opAndcarry[2] != BitValue.ONE) {
                            isOne = false;
                        }
                    } else {
                        if (opAndcarry[1] != BitValue.ONE || opAndcarry[2] != BitValue.ONE) {
                            isOne = false;
                        }
                        if (opAndcarry[1] != BitValue.ZERO || opAndcarry[2] != BitValue.ZERO) {
                            isZero = false;
                        }
                    }
                    if (isOne) {
                        resValcarry[1] = BitValue.ONE;
                    } else if (isZero) {
                        resValcarry[1] = BitValue.ZERO;
                    } else {
                        resValcarry[1] = check;
                    }
                    break;
                }
            }
            return resValcarry;
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
                BitValue carry = BitValue.ZERO;

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
                    BitValue[] opAndcarry = { op1Values[i], op2Values[i], carry };
                    BitValue[] checkTop = checkSubTopBottom(opAndcarry, BitValue.TOP);
                    BitValue[] checkBottom = checkSubTopBottom(opAndcarry, BitValue.BOTTOM);

                    if (checkTop[0] == BitValue.TOP) {
                        bitValues[i] = BitValue.TOP;
                        carry = checkTop[1];
                    } else if (checkBottom[0] == BitValue.BOTTOM) {
                        bitValues[i] = BitValue.BOTTOM;
                        carry = checkBottom[1];

                    } else {
                        // both bits and the carry are ONE or ZERO so we convert them to int and subtract them
                        int op1Bit = BitValueArray.bitValueToBoolean(op1Values[i]) ? 1 : 0;
                        int op2Bit = BitValueArray.bitValueToBoolean(op2Values[i]) ? 1 : 0;
                        int carryBit = BitValueArray.bitValueToBoolean(carry) ? 1 : 0;
                        int res = op1Bit + op2Bit + carryBit;
                        carry = BitValueArray.booleanToBitValue(op1Bit < op2Bit + carryBit);
                        bitValues[i] = BitValueArray.booleanToBitValue((res & 1) != 0);
                    }
                }
                result = new BitValueArray(bitValues);
            }
        }

        private static int getNumberOfSpecialChars(BitValueArray op) {
            int count = 0;
            for (BitValue bit : op.getBitValues()) {
                if (bit == BitValue.TOP || bit == BitValue.BOTTOM) {
                    count++;
                }
            }
            return count;
        }

        @Override
        public void caseMulExpr(MulExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ConstantBitsElement.BitValueArray op1 = operandValues.getFirst();
            ConstantBitsElement.BitValueArray op2 = operandValues.getSecond();

            if (op1.equals(top) || op2.equals(top)) {
                // if one is top, the result is top
                result = top;

            } else if (op1.isConst() && op2.isConst()) {
                // if both are constants, we can just multiply the corresponding ArithmeticConstants
                result = new BitValueArray((ArithmeticConstant) op1.getConstant().multiply(op2.getConstant()));

            } else {
                // both are not top and at least one is not a constant
                // therefore we have to multiply using special cases or heuristics

                // Checking if one is Long:
                int l1 = op1.getLength();
                int l2 = op2.getLength();
                int length = Math.max(l1, l2);

                if (op1.isZero() || op2.isZero()) {
                    // If one of the factors is zero, the result of the multiplication is zero
                    result = new BitValueArray(length, BitValue.ZERO);

                } else if (op1.isPowerOfTwo()) {
                    // if one of the factors is a power of two, the other is simply shifted left
                    int shiftAmount = op1.getPositionOfOne();
                    result = shiftLeft(op2, shiftAmount);

                } else if (op2.isPowerOfTwo()) {
                    // if one of the factors is a power of two, the other is simply shifted left
                    int shiftAmount = op2.getPositionOfOne();
                    result = shiftLeft(op1, shiftAmount);

                } else {
                    // none of the special cases applied, so bring on those nasty heuristics 😛
                    BitValue[] op1Values = new BitValue[length];
                    BitValue[] op2Values = new BitValue[length];

                    // make both the same length
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
                    }

                    int numberOfSpecialChars1 = getNumberOfSpecialChars(op1);
                    int numberOfSpecialChars2 = getNumberOfSpecialChars(op2);
                    if (numberOfSpecialChars1 + numberOfSpecialChars2 <= SPECIAL_CHAR_TRESHOLD) {
                        // heuristic of multiplying each possibility:
                        int dim1 = (1 << numberOfSpecialChars1);
                        int dim2 = (1 << numberOfSpecialChars2);
                        BitValueArray[] possibilities = new BitValueArray[dim1 * dim2];
                        for (long counter1 = 0; counter1 < dim1; counter1++) {
                            for (long counter2 = 0; counter2 < dim2; counter2++) {
                                BitValue[] op1ValuesPossibility = new BitValue[length];
                                BitValue[] op2ValuesPossibility = new BitValue[length];
                                for (int j = 0; j < length; j++) {
                                    if (op1Values[j] == BitValue.TOP || op1Values[j] == BitValue.BOTTOM) {
                                        op1ValuesPossibility[j] =
                                                BitValueArray.booleanToBitValue((counter1 & ((long) 1 << j)) != 0);
                                    }
                                    if (op2Values[j] == BitValue.TOP || op2Values[j] == BitValue.BOTTOM) {
                                        op2ValuesPossibility[j] =
                                                BitValueArray.booleanToBitValue((counter2 & ((long) 1 << j)) != 0);
                                    }
                                }
                                BitValueArray op1Possibility = new BitValueArray(op1ValuesPossibility);
                                BitValueArray op2Possibility = new BitValueArray(op2ValuesPossibility);
                                possibilities[(int) (counter1 * counter2)] =
                                        new BitValueArray((ArithmeticConstant) op1Possibility.getConstant()
                                                .multiply(op2Possibility.getConstant()));
                            }
                        }
                        // joining the possibilities
                        BitValueArray refVal = possibilities[0];
                        BitValueArray top = BitValueArray.getTop(length);
                        BitValueArray bottom = BitValueArray.getBottom(length);
                        for (int j = 1; j < dim1 * dim2; j++) {
                            refVal = join.getJoinHelper().performSingleJoin(refVal, possibilities[j], length, top,
                                    bottom);
                        }
                        result = refVal;

                    } else {
                        // to many special characters (TOP/BOTTOM) so the last resort is counting zeros from the
                        // lowest
                        // and highest bit
                        int lowZeros = 0;
                        int highZeros = 0;
                        boolean op1FoundLow = false;
                        boolean op2FoundLow = false;
                        boolean op1FoundHigh = false;
                        boolean op2FoundHigh = false;
                        for (int k = 0; k < length; k++) {
                            if (!op1FoundLow) {
                                if (op1Values[k] == BitValue.ZERO) {
                                    lowZeros++;
                                } else {
                                    op1FoundLow = true;
                                }
                            }
                            if (!op2FoundLow) {
                                if (op2Values[k] == BitValue.ZERO) {
                                    lowZeros++;
                                } else {
                                    op2FoundLow = true;
                                }
                            }
                            if (!op1FoundHigh) {
                                if (op1Values[length - k - 1] == BitValue.ZERO) {
                                    highZeros++;
                                } else {
                                    op1FoundHigh = true;
                                }
                            }
                            if (!op2FoundHigh) {
                                if (op2Values[length - k - 1] == BitValue.ZERO) {
                                    highZeros++;
                                } else {
                                    op2FoundHigh = true;
                                }
                            }
                        }
                        int highIndex = Math.min(2 * length - highZeros, length);
                        BitValue[] sandwich = new BitValue[length];
                        for (int l = 0; l < lowZeros; l++) {
                            sandwich[l] = BitValue.ZERO;
                        }
                        for (int m = lowZeros; m < highZeros; m++) {
                            sandwich[m] = BitValue.TOP;
                        }
                        for (int n = highIndex; n < length; n++) {
                            sandwich[n] = BitValue.ZERO;
                        }
                        result = new BitValueArray(sandwich);
                    }
                }
            }
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
            Evaluator negSwitch = new Evaluator(inputElement, join);
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
                    BitValue carry = BitValue.ZERO;
                    BitValue[] bitValues = new BitValue[length];
                    for (int j = 0; j < length; j++) {
                        int oneBit = (j == 0) ? 1 : 0;
                        BitValue oneBitValue = BitValueArray.booleanToBitValue(oneBit == 1);
                        // oneBit and oneBitValue are the "1" that will be added, they are 1/ONE if and if only j is 0

                        // Checking if one of the bits is TOP/BOTTOM:
                        BitValue[] opAndcarry = { resBitValues[j], oneBitValue, carry };
                        BitValue[] checkTop = checkSubTopBottom(opAndcarry, BitValue.TOP);
                        BitValue[] checkBottom = checkSubTopBottom(opAndcarry, BitValue.BOTTOM);

                        if (checkTop[0] == BitValue.TOP) {
                            bitValues[j] = BitValue.TOP;
                            carry = checkTop[1];
                        } else if (checkBottom[0] == BitValue.BOTTOM) {
                            bitValues[j] = BitValue.BOTTOM;
                            carry = checkBottom[1];

                        } else {
                            // both bits and the carry are ONE or ZERO so we convert them to int and add them
                            int resBit = BitValueArray.bitValueToBoolean(resBitValues[j]) ? 1 : 0;
                            int carryBit = BitValueArray.bitValueToBoolean(carry) ? 1 : 0;
                            int res = resBit + oneBit + carryBit;
                            carry = BitValueArray.booleanToBitValue(resBit < oneBit + carryBit);
                            bitValues[j] = BitValueArray.booleanToBitValue((res & 1) != 0);
                        }
                    }
                    result = new BitValueArray(bitValues);
                }
            }
        }

        private void commutativeBitwiseOperation(BitValueArray op1, BitValueArray op2, BitOperation operation) {
            if (op1.equals(top) || op2.equals(top)) {
                result = top;
                // if one is top, the result is top

            } else if (op1.isConst() && op2.isConst()) {
                // if both are constants, we can just use the operation of the corresponding ArithmeticConstants
                switch (operation) {
                case AND:
                    result = new BitValueArray((ArithmeticConstant) op1.getConstant().and(op2.getConstant()));
                case OR:
                    result = new BitValueArray((ArithmeticConstant) op1.getConstant().or(op2.getConstant()));
                case XOR:
                    result = new BitValueArray((ArithmeticConstant) op1.getConstant().xor(op2.getConstant()));
                }

            } else {
                // both are not top and at least one is not a constant
                // therefore we have to calculate the operation all the bits one by one

                // Making sure we have the right length in both:
                int l1 = op1.getLength();
                int l2 = op2.getLength();
                int length = Math.max(l1, l2);
                BitValue[] op1Values = new BitValue[length];
                BitValue[] op2Values = new BitValue[length];
                BitValue[] bitValues = new BitValue[length];

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

                    // the actual operation bit by bit:
                    // first check for eliminating options (0 for AND, 1 for OR)
                    switch (operation) {
                    case AND:
                        if (op1Values[i] == BitValue.ZERO || op2Values[i] == BitValue.ZERO) {
                            bitValues[i] = BitValue.ZERO;
                            continue;
                        }
                    case OR:
                        if (op1Values[i] == BitValue.ONE || op2Values[i] == BitValue.ONE) {
                            bitValues[i] = BitValue.ONE;
                            continue;
                        }
                    case XOR:
                        // ignore, since there is no eliminating option for XOR
                    }

                    // then check if one of the bits is TOP or BOTTOM
                    if (op1Values[i] == BitValue.TOP || op2Values[i] == BitValue.TOP) {
                        bitValues[i] = BitValue.TOP;

                    } else if (op1Values[i] == BitValue.BOTTOM || op2Values[i] == BitValue.BOTTOM) {
                        bitValues[i] = BitValue.BOTTOM;

                    } else {
                        // the bits are both neither TOP nor BOTTOM nor eliminating, so we can convert them to boolean
                        // and use the & / | / ^
                        boolean b1 = BitValueArray.bitValueToBoolean(op1Values[i]);
                        boolean b2 = BitValueArray.bitValueToBoolean(op2Values[i]);
                        switch (operation) {
                        case AND:
                            bitValues[i] = BitValueArray.booleanToBitValue(b1 & b2);
                        case OR:
                            bitValues[i] = BitValueArray.booleanToBitValue(b1 | b2);
                        case XOR:
                            bitValues[i] = BitValueArray.booleanToBitValue(b1 ^ b2);
                        }
                    }
                }
                result = new BitValueArray(bitValues);
            }
        }

        @Override
        public void caseAndExpr(AndExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ConstantBitsElement.BitValueArray op1 = operandValues.getFirst();
            ConstantBitsElement.BitValueArray op2 = operandValues.getSecond();

            commutativeBitwiseOperation(op1, op2, BitOperation.AND);
        }

        @Override
        public void caseOrExpr(OrExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ConstantBitsElement.BitValueArray op1 = operandValues.getFirst();
            ConstantBitsElement.BitValueArray op2 = operandValues.getSecond();

            commutativeBitwiseOperation(op1, op2, BitOperation.OR);
        }

        @Override
        public void caseXorExpr(XorExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ConstantBitsElement.BitValueArray op1 = operandValues.getFirst();
            ConstantBitsElement.BitValueArray op2 = operandValues.getSecond();

            commutativeBitwiseOperation(op1, op2, BitOperation.XOR);
        }

        private static BitValueArray shiftLeft(BitValueArray op, int amount) {
            return op;
            // TODO implement shiftLeft
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
            Evaluator switch1 = new Evaluator(inputElement, join);
            op1.apply(switch1);

            Value op2 = binOpExpr.getOp2();
            Evaluator switch2 = new Evaluator(inputElement, join);
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

    /**
     * @author Nils Jessen
     * 
     *         Commutative bitwise operations.
     */
    enum BitOperation {
        /**
         * for AND
         */
        AND,

        /**
         * for OR
         */
        OR,

        /**
         * for XOR
         */
        XOR
    }
}
