package dfa.analyses;

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

    private static final int TOP_TRESHOLD = 5;

    private ConstantBitsJoin join = new ConstantBitsJoin();

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
                if (!ConstantBitsElement.isLocalTypeAccepted(type)) {
                    // ignore
                    return;
                }
            } else {
                // ignore
                return;
            }

            Value rVal = stmt.getRightOp();
            Evaluator valueSwitch = new Evaluator(inputElement, join);
            rVal.apply(valueSwitch);
            BitValueArray rhs = valueSwitch.getResult();
            if (rhs == BitValueArray.getTop()) {
                Type t = lValLocal.getType();
                if (t instanceof BooleanType || t instanceof ByteType || t instanceof CharType || t instanceof ShortType
                        || t instanceof IntType) {
                    rhs = BitValueArray.getIntTop();
                } else if (t instanceof LongType) {
                    rhs = BitValueArray.getLongTop();
                } else {
                    throw new IllegalStateException("Cannot assign to local that is not int or long");
                }
            }
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
            JimpleLocal lValLocal;
            if (stmt.getLeftOp() instanceof JimpleLocal) {
                lValLocal = (JimpleLocal) stmt.getLeftOp();
                Type type = lValLocal.getType();
                if (!ConstantBitsElement.isLocalTypeAccepted(type)) {
                    // ignore
                    return;
                }
            } else {
                // ignore
                return;
            }
            BitValueArray rhs;
            Type t = lValLocal.getType();
            if (t instanceof BooleanType || t instanceof ByteType || t instanceof CharType || t instanceof ShortType
                    || t instanceof IntType) {
                rhs = ConstantBitsElement.BitValueArray.getIntTop();
            } else if (t instanceof LongType) {
                rhs = ConstantBitsElement.BitValueArray.getLongTop();
            } else {
                throw new IllegalStateException("Cannot assign to local that is not int or long");
            }
            outputElement.setValue(lValLocal, rhs);
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
         * Checks for addition if one of the given {@code BitValue}s equals TOP and if so, calculates the carry
         * 
         * @param opAndCarry
         *        the {@code BitValue}s to check
         * @return if one of the bits was TOP, and the calculated carry
         */
        private BitValue[] checkAddTop(BitValue[] opAndCarry) {
            // opAndcarry = { op1Values[i], op2Values[i], carry }
            // we will return resValcarry, with the first entry beeing the resulting bit
            // and the second entry beeing the calculated carry
            BitValue[] resValCarry = new BitValue[2];
            resValCarry[0] = BitValue.ZERO;
            resValCarry[1] = BitValue.ZERO;

            // counting the ONEs and TOPs in opAndCarry
            int numberOfOnes = 0;
            int numberOfTops = 0;
            for (BitValue entry : opAndCarry) {
                switch (entry) {
                case ONE:
                    numberOfOnes++;
                    break;
                case TOP:
                    numberOfTops++;
                    break;
                case ZERO:
                    // ignore
                    break;
                case BOTTOM:
                    throw new IllegalStateException("Transition of BOTTOM not possible!");
                }
            }

            // Calculating the resulting bit
            if (numberOfTops > 0) {
                // If one of the 3 bits is TOP, the resulting bit is also TOP
                resValCarry[0] = BitValue.TOP;
            } else if (numberOfOnes == 1 || numberOfOnes == 3) {
                // None of the bits is TOP, so we have only ONEs and ZEROs
                // If the number of ONEs is odd, the result is ONE, if it is even, it remains ZERO
                resValCarry[0] = BitValue.ONE;
            }

            // Calculating the carry bit
            if (numberOfOnes >= 2) {
                // If at least 2 of the 3 bits are ONE, the carry is definitely ONE
                resValCarry[1] = BitValue.ONE;
            } else if (numberOfOnes + numberOfTops >= 2) {
                // The carry bit is not definitely ONE
                // If the number of ONEs and TOPs combined are at least two, the carry bit is TOP, since there could be
                // two or more ONEs
                // If not, the carry bit remains ZERO
                resValCarry[1] = BitValue.TOP;
            }

            return resValCarry;
        }

        @Override
        public void caseAddExpr(AddExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            BitValueArray op1 = operandValues.getFirst();
            BitValueArray op2 = operandValues.getSecond();
            if (op1.containsBOTTOM() || op2.containsBOTTOM()) {
                throw new IllegalStateException("Transition of BOTTOM not possible!");
            }

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
                BitValue op1HighBit = op1.getBitValues()[l1 - 1];
                BitValue op2HighBit = op2.getBitValues()[l2 - 1];
                BitValue[] op1Values = new BitValue[length];
                BitValue[] op2Values = new BitValue[length];
                BitValue[] bitValues = new BitValue[length];
                BitValue carry = BitValue.ZERO;

                for (int i = 0; i < length; i++) {
                    if (i >= l1) {
                        op1Values[i] = op1HighBit;
                    } else {
                        op1Values[i] = op1.getBitValues()[i];
                    }
                    if (i >= l2) {
                        op2Values[i] = op2HighBit;
                    } else {
                        op2Values[i] = op2.getBitValues()[i];
                    }

                    // the actual addition bit by bit:
                    // first checking if one of the bits is TOP
                    BitValue[] opAndCarry = { op1Values[i], op2Values[i], carry };
                    BitValue[] checkTop = checkAddTop(opAndCarry);

                    if (checkTop[0] == BitValue.TOP) {
                        bitValues[i] = BitValue.TOP;
                        carry = checkTop[1];

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
         * Checks for subtraction if one of the given {@code BitValue}s equals TOP and if so, calculates the carry
         * 
         * @param opAndCarry
         *        the {@code BitValue}s to check
         * @return if one of the bits was TOP, and the calculated carry
         */
        private BitValue[] checkSubTop(BitValue[] opAndCarry) {
            // opAndcarry = { op1Values[i], op2Values[i], carry }
            // we will return resValcarry, with the first entry beeing the resulting bit
            // and the second entry beeing the calculated carry
            BitValue[] resValCarry = new BitValue[2];
            resValCarry[0] = BitValue.ZERO;
            resValCarry[1] = BitValue.ZERO;

            boolean subFromOne = (opAndCarry[0] == BitValue.ONE);
            boolean subFromTop = (opAndCarry[0] == BitValue.TOP);

            // counting the ONEs and TOPs in (op2Values[i] and carry) as well as in all of opAndCarry
            int numberOfAllOnes = 0;
            int numberOfAllTops = 0;
            int numberOfSubOnes = 0;
            int numberOfSubTops = 0;
            for (int i = 0; i < 3; i++) {
                switch (opAndCarry[i]) {
                case ONE:
                    numberOfAllOnes++;
                    if (i > 0) {
                        numberOfSubOnes++;
                    }
                    break;
                case TOP:
                    numberOfAllTops++;
                    if (i > 0) {
                        numberOfSubTops++;
                    }
                    break;
                case ZERO:
                    // ignore
                    break;
                case BOTTOM:
                    throw new IllegalStateException("Transition of BOTTOM not possible!");
                }
            }

            // Calculating the resulting bit
            if (numberOfAllTops > 0) {
                // If one of the 3 bits is TOP, the resulting bit is also TOP
                resValCarry[0] = BitValue.TOP;
            } else {
                // None of the bits is TOP, so we have only ONEs and ZEROs
                if (numberOfAllOnes == 1 || numberOfAllOnes == 3) {
                    // An odd number of bits is ONE so the resulting bit is ONE
                    resValCarry[0] = BitValue.ONE;
                }
            }

            // Calculating the carry bit
            if (subFromOne || subFromTop) {
                // We subtract from ONE or from TOP
                if (numberOfSubOnes == 2) {
                    // All 3 bits are ONE, or only the two sub-bits are one, so the carry is definitely ONE
                    resValCarry[1] = BitValue.ONE;
                } else if (subFromOne && (numberOfSubOnes + numberOfSubTops == 2)) {
                    // Not all 3 bits are ONE, so the carry is not definitely ONE
                    // All 3 bits are either TOP or ONE, so the carry is TOP
                    resValCarry[1] = BitValue.TOP;
                } else if (subFromTop && (numberOfSubOnes + numberOfSubTops >= 1)) {
                    //
                    resValCarry[1] = BitValue.TOP;
                }
            } else {
                // We subtract from ZERO
                if (numberOfSubOnes >= 1) {
                    // We subtract at least 1 ONE from ZERO so the carry is definitely ONE
                    resValCarry[1] = BitValue.ONE;
                } else if (numberOfSubTops >= 1) {
                    // We subtract no ONEs, so only TOPs and ZEROs
                    // We subtract at least 1 TOP from ZERO so the carry is TOP
                    resValCarry[1] = BitValue.TOP;
                }
            }
            return resValCarry;
        }

        @Override
        public void caseSubExpr(SubExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ConstantBitsElement.BitValueArray op1 = operandValues.getFirst();
            ConstantBitsElement.BitValueArray op2 = operandValues.getSecond();
            if (op1.containsBOTTOM() || op2.containsBOTTOM()) {
                throw new IllegalStateException("Transition of BOTTOM not possible!");
            }

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
                BitValue op1HighBit = op1.getBitValues()[l1 - 1];
                BitValue op2HighBit = op2.getBitValues()[l2 - 1];
                BitValue[] op1Values = new BitValue[length];
                BitValue[] op2Values = new BitValue[length];
                BitValue[] bitValues = new BitValue[length];
                BitValue carry = BitValue.ZERO;

                for (int i = 0; i < length; i++) {
                    if (i >= l1) {
                        op1Values[i] = op1HighBit;
                    } else {
                        op1Values[i] = op1.getBitValues()[i];
                    }
                    if (i >= l2) {
                        op2Values[i] = op2HighBit;
                    } else {
                        op2Values[i] = op2.getBitValues()[i];
                    }

                    // the actual subtraction bit by bit:
                    // first checking if one of the bits is TOP or BOTTOM
                    BitValue[] opAndcarry = { op1Values[i], op2Values[i], carry };
                    BitValue[] checkTop = checkSubTop(opAndcarry);

                    if (checkTop[0] == BitValue.TOP) {
                        bitValues[i] = BitValue.TOP;
                        carry = checkTop[1];

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

        /**
         * Returns the number of bits of the {@code BitValueArray} op that are TOP.
         * 
         * @param op
         *        the {@code BitValueArray} to get the number of TOP bits from
         * @return the number of bits of the {@code BitValueArray} op that are TOP
         */
        private static int getNumberOfTOP(BitValueArray op) {
            int count = 0;
            for (BitValue bit : op.getBitValues()) {
                if (bit == BitValue.TOP) {
                    count++;
                }
            }
            return count;
        }

        /**
         * Returns the maximum and minimum value the {@code BitValue} {@code opValue} can have.
         * 
         * @param opValue
         *        The {@code BitValue} to get the maximum and minimum value of
         * @param opHighBit
         *        The MSB of the {@code BitValueArray} that {@code opValue} is part of
         * @return the maximum and minimum value the {@code BitValue} {@code opValue} can have
         */
        private BitValue[] maxMinBit(BitValue opValue, BitValue opHighBit) {
            BitValue opMax = null;
            BitValue opMin = null;
            switch (opValue) {
            case ONE:
                opMax = BitValue.ONE;
                opMin = BitValue.ONE;
                break;
            case ZERO:
                opMax = BitValue.ZERO;
                opMin = BitValue.ZERO;
                break;
            case TOP:
                opMax = BitValueArray.booleanToBitValue(opHighBit == BitValue.ZERO);
                opMin = BitValueArray.booleanToBitValue(opHighBit == BitValue.ONE);
                break;
            case BOTTOM:
                throw new IllegalStateException("Transition of BOTTOM not possible!");
            }
            BitValue[] actualMinMax = { opMax, opMin };
            return actualMinMax;
        }

        /**
         * Returns the maximum and minimum absolute values that op1 and op2 can have.
         * 
         * @param op1Values
         *        the {@code BitValue}s of op1
         * @param op2Values
         *        the {@code BitValue}s of op2
         * @param op1HighBit
         *        the sign of op1
         * @param op2HighBit
         *        the sign of op2
         * @param length
         *        the length of op1 and op2
         * @return the maximum and minimum absolute values that op1 and op2 can have
         */
        private long[] getMaxMinAbs(BitValue[] op1Values, BitValue[] op2Values, BitValue op1HighBit,
                BitValue op2HighBit, int length) {
            if (op1HighBit == BitValue.TOP || op1HighBit == BitValue.BOTTOM || op2HighBit == BitValue.TOP
                    || op2HighBit == BitValue.BOTTOM) {
                throw new IllegalArgumentException("HighBits of both BitValues must not be TOP/BOTTOM");
            }

            BitValue[] op1Max = new BitValue[length];
            BitValue[] op1Min = new BitValue[length];
            BitValue[] op2Max = new BitValue[length];
            BitValue[] op2Min = new BitValue[length];

            // calculating the the bitValues of the maximum and minimum op1 and op2 can become
            // if the sign is ZERO the operand is positive, so maximizing means putting ONE's instead of TOP/BOTTOM
            // if the sign is ONE the operand is negative, so maximizing means putting ZERO's instead of TOP/BOTTOM
            for (int k = 0; k < length; k++) {
                BitValue[] op1ActualMaxMinAbs = maxMinBit(op1Values[k], op1HighBit);
                op1Max[k] = op1ActualMaxMinAbs[0];
                op1Min[k] = op1ActualMaxMinAbs[1];

                BitValue[] op2ActualMaxMinAbs = maxMinBit(op2Values[k], op2HighBit);
                op2Max[k] = op2ActualMaxMinAbs[0];
                op2Min[k] = op2ActualMaxMinAbs[1];
            }

            // converting from BitValue[] to BitValueArray
            BitValueArray op1MaxArray = new BitValueArray(op1Max);
            BitValueArray op1MinArray = new BitValueArray(op1Min);
            BitValueArray op2MaxArray = new BitValueArray(op2Max);
            BitValueArray op2MinArray = new BitValueArray(op2Min);

            // converting from BitValueArray to ArithmeticConstant
            ArithmeticConstant op1MaxConst = op1MaxArray.getConstant();
            ArithmeticConstant op1MinConst = op1MinArray.getConstant();
            ArithmeticConstant op2MaxConst = op2MaxArray.getConstant();
            ArithmeticConstant op2MinConst = op2MinArray.getConstant();

            // converting from ArithmeticConstant to long
            ConstantRetriever constantSwitch = new ConstantRetriever();
            op1MaxConst.apply(constantSwitch);
            long op1MaxAbs = Math.abs(constantSwitch.getValue());
            op1MinConst.apply(constantSwitch);
            long op1MinAbs = Math.abs(constantSwitch.getValue());
            op2MaxConst.apply(constantSwitch);
            long op2MaxAbs = Math.abs(constantSwitch.getValue());
            op2MinConst.apply(constantSwitch);
            long op2MinAbs = Math.abs(constantSwitch.getValue());

            long[] maxMinAbs = { op1MaxAbs, op1MinAbs, op2MaxAbs, op2MinAbs };
            return maxMinAbs;
        }

        /**
         * Returns the maximum and minimum absolute value the multiplication of op1 and op2 can result in.
         * 
         * @param op1Values
         *        the bitValues of op1
         * @param op2Values
         *        the bitValues of op2
         * @param op1HighBit
         *        the sign of op1
         * @param op2HighBit
         *        the sign of op2
         * @param length
         *        the length of op1 and op2
         * @return the maximum and minimum absolute value the multiplication of op1 and op2 can result in
         */
        private long[] getMaxMinMultAbs(BitValue[] op1Values, BitValue[] op2Values, BitValue op1HighBit,
                BitValue op2HighBit, int length) {

            long[] minMaxAbs = getMaxMinAbs(op1Values, op2Values, op1HighBit, op2HighBit, length);

            long op1MaxAbs = minMaxAbs[0];
            long op1MinAbs = minMaxAbs[1];
            long op2MaxAbs = minMaxAbs[2];
            long op2MinAbs = minMaxAbs[3];

            // calculating the actual minAbs and maxAbs of the division
            long resultMaxAbs;
            long resultMinAbs;
            if (op2MinAbs == 0) {
                resultMaxAbs = Long.MAX_VALUE;
            } else {
                resultMaxAbs = op1MaxAbs * op2MaxAbs;
            }
            if (op2MaxAbs == 0) {
                resultMinAbs = Long.MAX_VALUE;
            } else {
                resultMinAbs = op1MinAbs * op2MinAbs;
            }

            long[] maxMinAbs = { resultMaxAbs, resultMinAbs };
            return maxMinAbs;
        }

        @Override
        public void caseMulExpr(MulExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ConstantBitsElement.BitValueArray op1 = operandValues.getFirst();
            ConstantBitsElement.BitValueArray op2 = operandValues.getSecond();
            if (op1.containsBOTTOM() || op2.containsBOTTOM()) {
                throw new IllegalStateException("Transition of BOTTOM not possible!");
            }

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
                BitValue op1HighBit = op1.getBitValues()[l1 - 1];
                BitValue op2HighBit = op2.getBitValues()[l2 - 1];

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

                    // none of the special cases applied, so bring on those nasty heuristics :P
                    BitValue[] op1Values = new BitValue[length];
                    BitValue[] op2Values = new BitValue[length];

                    // make both the same length
                    for (int i = 0; i < length; i++) {
                        if (i >= l1) {
                            op1Values[i] = op1HighBit;

                        } else {
                            op1Values[i] = op1.getBitValues()[i];
                        }
                        if (i >= l2) {
                            op2Values[i] = op2HighBit;

                        } else {
                            op2Values[i] = op2.getBitValues()[i];
                        }
                    }

                    int numberOfTops1 = getNumberOfTOP(op1);
                    int numberOfTops2 = getNumberOfTOP(op2);
                    if (numberOfTops1 + numberOfTops2 <= TOP_TRESHOLD) {
                        // heuristic of multiplying each possibility:
                        int dim1 = (1 << numberOfTops1);
                        int dim2 = (1 << numberOfTops2);
                        int dimGes = dim1 * dim2;
                        BitValueArray[] possibilities = new BitValueArray[dimGes];

                        for (long counter1 = 0; counter1 < dim1; counter1++) {
                            for (long counter2 = 0; counter2 < dim2; counter2++) {
                                BitValue[] op1ValuesPossibility = new BitValue[length];
                                BitValue[] op2ValuesPossibility = new BitValue[length];
                                int topCounter1 = 0;
                                int topCounter2 = 0;
                                for (int j = 0; j < length; j++) {
                                    if (op1Values[j] == BitValue.TOP) {
                                        topCounter1++;
                                        op1ValuesPossibility[j] = BitValueArray
                                                .booleanToBitValue((counter1 & (1L << (topCounter1 - 1))) != 0);
                                    } else {
                                        op1ValuesPossibility[j] = op1Values[j];
                                    }
                                    if (op2Values[j] == BitValue.TOP) {
                                        topCounter2++;
                                        op2ValuesPossibility[j] = BitValueArray
                                                .booleanToBitValue((counter2 & (1L << (topCounter2 - 1))) != 0);
                                    } else {
                                        op2ValuesPossibility[j] = op2Values[j];
                                    }
                                }
                                BitValueArray op1Possibility = new BitValueArray(op1ValuesPossibility);
                                BitValueArray op2Possibility = new BitValueArray(op2ValuesPossibility);
                                possibilities[(int) (counter1 * dim2 + counter2)] =
                                        new BitValueArray((ArithmeticConstant) op1Possibility.getConstant()
                                                .multiply(op2Possibility.getConstant()));
                            }
                        }
                        BitValueArray refVal = possibilities[0];
                        BitValueArray top = BitValueArray.getTop(length);
                        BitValueArray bottom = BitValueArray.getBottom(length);
                        for (int j = 1; j < dimGes; j++) {
                            refVal = join.getJoinHelper().performSingleJoin(refVal, possibilities[j], length, top,
                                    bottom);
                        }
                        result = refVal;

                    } else if (op1HighBit == BitValue.TOP || op2HighBit == BitValue.TOP) {
                        // If we can not apply the heuristic, and one of the HighBits is TOP, we have no information
                        result = top;

                    } else if (op1HighBit == BitValue.ZERO && op2HighBit == BitValue.ZERO) {
                        // Both operands are positive, but have too many TOP bits so the next idea is counting zeros
                        // from the lowest and highest bit
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
                        for (int l = 0; l < Math.min(lowZeros, length); l++) {
                            sandwich[l] = BitValue.ZERO;
                        }
                        for (int m = lowZeros; m < Math.min(highZeros, length); m++) {
                            sandwich[m] = BitValue.TOP;
                        }
                        for (int n = highIndex; n < length; n++) {
                            sandwich[n] = BitValue.ZERO;
                        }
                        result = new BitValueArray(sandwich);

                    } else {
                        // Range Analysis with getMaxMinAbs
                        // The HighBits of both operands are either ONE or ZERO so we can calculate the sign of our
                        // result
                        // Calculate max abs of op1 and min abs of op2 to get max abs of result
                        // also min abs of op1 and max abs of op2 to get min abs of result
                        long[] maxMinAbs = getMaxMinMultAbs(op1Values, op2Values, op1HighBit, op2HighBit, length);

                        BitValue[] resValues = new BitValue[length];
                        boolean foundOne = false;
                        boolean foundZero = false;
                        int onePos = -1;
                        int zeroPos = -1;
                        if (op1HighBit == op2HighBit) {
                            // the result will be positive so we are checking:
                            // How many ONE's can we put from the lowest bit up
                            // How many ZERO's can we put from the highest bit down

                            long maxVal = maxMinAbs[0];
                            long minVal = maxMinAbs[1];

                            for (int l = 0; l < length; l++) {
                                if (!foundZero && (minVal & (1L << l)) == 0) {
                                    foundZero = true;
                                    zeroPos = l;
                                    break;
                                }
                            }
                            for (int p = length - 1; p >= 0; p--) {
                                if (!foundOne && (maxVal & (1L << p)) != 0) {
                                    foundOne = true;
                                    onePos = p;
                                    break;
                                }
                            }
                            for (int q = 0; q < length; q++) {
                                if (foundZero && q < zeroPos) {
                                    resValues[q] = BitValue.ONE;
                                } else if (foundOne && q > onePos) {
                                    resValues[q] = BitValue.ZERO;
                                } else {
                                    resValues[q] = BitValue.TOP;
                                }
                            }

                        } else {
                            // the result will be negative so we are checking:
                            // How many ZERO's can we put from the lowest bit up
                            // How many ONE's can we put from the highest bit down
                            long maxVal = -maxMinAbs[1];
                            long minVal = -maxMinAbs[0];
                            if (maxVal == 0 || minVal == 0) {
                                result = top;
                                return;
                            }
                            for (int m = 0; m < length; m++) {
                                if (!foundOne && (maxVal & (1L << m)) != 0) {
                                    foundOne = true;
                                    onePos = m;
                                    break;
                                }
                            }
                            for (int n = length - 1; n >= 0; n--) {
                                if (!foundZero && (minVal & (1L << n)) == 0) {
                                    foundZero = true;
                                    zeroPos = n;
                                    break;
                                }
                            }
                            for (int o = 0; o < length; o++) {
                                if (foundOne && o < onePos) {
                                    resValues[o] = BitValue.ZERO;
                                } else if (foundZero && o > zeroPos) {
                                    resValues[o] = BitValue.ONE;
                                } else {
                                    resValues[o] = BitValue.TOP;
                                }
                            }
                        }
                        result = new BitValueArray(resValues);
                    }
                }
            }
        }

        /**
         * Returns the maximum and minimum absolute value the division of op1 and op2 can result in.
         * 
         * @param op1Values
         *        the bitValues of op1
         * @param op2Values
         *        the bitValues of op2
         * @param op1HighBit
         *        the sign of op1
         * @param op2HighBit
         *        the sign of op2
         * @param length
         *        the length of op1 and op2
         * @return the maximum and minimum absolute value the division of op1 and op2 can result in
         */
        private long[] getMaxMinDivAbs(BitValue[] op1Values, BitValue[] op2Values, BitValue op1HighBit,
                BitValue op2HighBit, int length) {

            long[] minMaxAbs = getMaxMinAbs(op1Values, op2Values, op1HighBit, op2HighBit, length);

            long op1MaxAbs = minMaxAbs[0];
            long op1MinAbs = minMaxAbs[1];
            long op2MaxAbs = minMaxAbs[2];
            long op2MinAbs = minMaxAbs[3];

            // calculating the actual minAbs and maxAbs of the division
            long resultMaxAbs;
            long resultMinAbs;
            if (op2MinAbs == 0) {
                resultMaxAbs = Long.MAX_VALUE;
            } else {
                resultMaxAbs = (long) Math.floor((double) op1MaxAbs / op2MinAbs);
            }
            if (op2MaxAbs == 0) {
                resultMinAbs = Long.MAX_VALUE;
            } else {
                resultMinAbs = (long) Math.floor((double) op1MinAbs / op2MaxAbs);
            }

            long[] maxMinAbs = { resultMaxAbs, resultMinAbs };
            return maxMinAbs;
        }

        @Override
        public void caseDivExpr(DivExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ConstantBitsElement.BitValueArray op1 = operandValues.getFirst();
            ConstantBitsElement.BitValueArray op2 = operandValues.getSecond();
            if (op1.containsBOTTOM() || op2.containsBOTTOM()) {
                throw new IllegalStateException("Transition of BOTTOM not possible!");
            }

            if (op1.equals(top) || op2.equals(top)) {
                // if one is top, the result is top
                result = top;

            } else if (op2.isConst() && op1.isConst()) {
                // if both are constants, we can just divide the corresponding ArithmeticConstants
                result = new BitValueArray((ArithmeticConstant) op1.getConstant().divide(op2.getConstant()));

            } else {
                // both are not top and at least one is not a constant
                // therefore we have to divide using special cases or heuristics

                // checking if one is Long:
                int l1 = op1.getLength();
                int l2 = op2.getLength();
                int length = Math.max(l1, l2);
                BitValue op1HighBit = op1.getBitValues()[l1 - 1];
                BitValue op2HighBit = op2.getBitValues()[l2 - 1];

                // dividing by ZERO gives TOP
                if (op2.isZero()) {
                    result = top;
                    return;

                } else if (op2.isPowerOfTwo()) {
                    // if op2 is a power of two, and abs(op2) <= abs(op1), op1 is simply shifted right
                    // if op2Abs <= op1MaxAbs, the result is zero
                    int shiftAmount = op2.getPositionOfOne();
                    int op2Abs = (int) Math.pow(2, shiftAmount);
                    long op1MinAbs;
                    long op1MaxAbs;

                    if (op1HighBit == BitValue.TOP) {
                        // if op1HighBit is TOP we have to check both cases for 0 and 1 to get op1MinAbs and op1MaxAbs
                        BitValue[] op1ONEValues = op1.getBitValues();
                        BitValue[] op1ZEROValues = op1.getBitValues();
                        op1ONEValues[l1 - 1] = BitValue.ONE;
                        op1ZEROValues[l1 - 1] = BitValue.ZERO;
                        BitValue[] op1ONEMax = new BitValue[l1];
                        BitValue[] op1ONEMin = new BitValue[l1];
                        BitValue[] op1ZEROMax = new BitValue[l1];
                        BitValue[] op1ZEROMin = new BitValue[l1];

                        for (int i = 0; i < l1; i++) {
                            // calculating the bitValues of the min and max op1ONE and op1ZERO can become
                            BitValue[] op1ONEActualMaxMinAbs = maxMinBit(op1ONEValues[i], op1HighBit);
                            op1ONEMax[i] = op1ONEActualMaxMinAbs[0];
                            op1ONEMin[i] = op1ONEActualMaxMinAbs[1];
                            BitValue[] op1ZEROActualMaxMinAbs = maxMinBit(op1ZEROValues[i], op1HighBit);
                            op1ZEROMax[i] = op1ZEROActualMaxMinAbs[0];
                            op1ZEROMin[i] = op1ZEROActualMaxMinAbs[1];
                        }

                        // converting from BitValue[] to BitValueArray
                        BitValueArray op1ONEMaxArray = new BitValueArray(op1ONEMax);
                        BitValueArray op1ONEMinArray = new BitValueArray(op1ONEMin);
                        BitValueArray op1ZEROMaxArray = new BitValueArray(op1ZEROMax);
                        BitValueArray op1ZEROMinArray = new BitValueArray(op1ZEROMin);

                        // converting from BitValueArray to ArithmeticConstant
                        ArithmeticConstant op1ONEMaxConst = op1ONEMaxArray.getConstant();
                        ArithmeticConstant op1ONEMinConst = op1ONEMinArray.getConstant();
                        ArithmeticConstant op1ZEROMaxConst = op1ZEROMaxArray.getConstant();
                        ArithmeticConstant op1ZEROMinConst = op1ZEROMinArray.getConstant();

                        // converting from ArithmeticConstant to long
                        ConstantRetriever constantSwitch = new ConstantRetriever();
                        op1ONEMaxConst.apply(constantSwitch);
                        long op1ONEMaxAbs = Math.abs(constantSwitch.getValue());
                        op1ONEMinConst.apply(constantSwitch);
                        long op1ONEMinAbs = Math.abs(constantSwitch.getValue());
                        op1ZEROMaxConst.apply(constantSwitch);
                        long op1ZEROMaxAbs = Math.abs(constantSwitch.getValue());
                        op1ZEROMinConst.apply(constantSwitch);
                        long op1ZEROMinAbs = Math.abs(constantSwitch.getValue());

                        // Comparing the two cases and getting the actual minimum and maximum
                        op1MaxAbs = Math.max(op1ONEMaxAbs, op1ZEROMaxAbs);
                        op1MinAbs = Math.min(op1ONEMinAbs, op1ZEROMinAbs);

                    } else {
                        BitValue[] op1Values = op1.getBitValues();
                        BitValue[] op1Max = new BitValue[l1];
                        BitValue[] op1Min = new BitValue[l1];

                        for (int i = 0; i < l1; i++) {
                            // calculating the the bitValues of the minimum op1 can become
                            BitValue[] op1ActualMaxMinAbs = maxMinBit(op1Values[i], op1HighBit);
                            op1Max[i] = op1ActualMaxMinAbs[0];
                            op1Min[i] = op1ActualMaxMinAbs[1];
                        }

                        // converting from BitValue[] to BitValueArray
                        BitValueArray op1MaxArray = new BitValueArray(op1Max);
                        BitValueArray op1MinArray = new BitValueArray(op1Min);

                        // converting from BitValueArray to ArithmeticConstant
                        ArithmeticConstant op1MaxConst = op1MaxArray.getConstant();
                        ArithmeticConstant op1MinConst = op1MinArray.getConstant();

                        // converting from ArithmeticConstant to long
                        ConstantRetriever constantSwitch = new ConstantRetriever();
                        op1MaxConst.apply(constantSwitch);
                        op1MaxAbs = Math.abs(constantSwitch.getValue());
                        op1MinConst.apply(constantSwitch);
                        op1MinAbs = Math.abs(constantSwitch.getValue());
                    }

                    if (op1MinAbs >= op2Abs) {
                        // if op2 is a power of two, and abs(op2) =< abs(op1), op1 is simply shifted right
                        result = signedShiftRight(op1, shiftAmount);
                        return;

                    } else if (op1MaxAbs < op2Abs) {
                        // if op1Abs < op2MaxAbs, the result is zero
                        BitValue[] zeros = new BitValue[l1];
                        for (int j = 0; j < l1; j++) {
                            zeros[j] = BitValue.ZERO;
                        }
                        result = new BitValueArray(zeros);
                        return;
                    }

                }
                // none of the special cases applied, so bring on those nasty heuristics :P
                BitValue[] op1Values = new BitValue[length];
                BitValue[] op2Values = new BitValue[length];

                // make both the same length
                for (int i = 0; i < length; i++) {
                    if (i >= l1) {
                        op1Values[i] = op1HighBit;
                    } else {
                        op1Values[i] = op1.getBitValues()[i];
                    }
                    if (i >= l2) {
                        op2Values[i] = op2HighBit;
                    } else {
                        op2Values[i] = op2.getBitValues()[i];
                    }
                }

                int numberOfTops1 = getNumberOfTOP(op1);
                int numberOfTops2 = getNumberOfTOP(op2);
                if (numberOfTops1 + numberOfTops2 <= TOP_TRESHOLD) {
                    // heuristic of dividing each possibility:
                    int dim1 = (1 << numberOfTops1);
                    int dim2 = (1 << numberOfTops2);
                    int dimGes = dim1 * dim2;
                    BitValueArray[] possibilities = new BitValueArray[dimGes];

                    for (long counter1 = 0; counter1 < dim1; counter1++) {
                        for (long counter2 = 0; counter2 < dim2; counter2++) {
                            BitValue[] op1ValuesPossibility = new BitValue[length];
                            BitValue[] op2ValuesPossibility = new BitValue[length];
                            int topCounter1 = 0;
                            int topCounter2 = 0;
                            for (int j = 0; j < length; j++) {
                                if (op1Values[j] == BitValue.TOP) {
                                    topCounter1++;
                                    op1ValuesPossibility[j] = BitValueArray
                                            .booleanToBitValue((counter1 & (1L << (topCounter1 - 1))) != 0);
                                } else {
                                    op1ValuesPossibility[j] = op1Values[j];
                                }
                                if (op2Values[j] == BitValue.TOP) {
                                    topCounter2++;
                                    op2ValuesPossibility[j] = BitValueArray
                                            .booleanToBitValue((counter2 & (1L << (topCounter2 - 1))) != 0);
                                } else {
                                    op2ValuesPossibility[j] = op2Values[j];
                                }
                            }
                            BitValueArray op1Possibility = new BitValueArray(op1ValuesPossibility);
                            BitValueArray op2Possibility = new BitValueArray(op2ValuesPossibility);
                            possibilities[(int) (counter1 * dim2 + counter2)] =
                                    new BitValueArray((ArithmeticConstant) op1Possibility.getConstant()
                                            .divide(op2Possibility.getConstant()));
                        }
                    }
                    // joining the possibilities
                    BitValueArray refVal = possibilities[0];
                    BitValueArray top = BitValueArray.getTop(length);
                    BitValueArray bottom = BitValueArray.getBottom(length);
                    for (int j = 1; j < dimGes; j++) {
                        refVal = join.getJoinHelper().performSingleJoin(refVal, possibilities[j], length, top, bottom);
                    }
                    result = refVal;

                } else {
                    // too many TOP bits so the last resort is counting zeros from the lowest and highest bit

                    if (op1HighBit == BitValue.TOP || op2HighBit == BitValue.TOP) {
                        // If we have no clue what the sign of one of the operands is, we have no better guess than
                        // to make everything TOP
                        result = top;

                    } else {
                        // The HighBits of both operands are either ONE or ZERO so we can calculate the sign of our
                        // result
                        // Calculate max abs of op1 and min abs of op2 to get max abs of result
                        // also min abs of op1 and max abs of op2 to get min abs of result
                        long[] maxMinAbs = getMaxMinDivAbs(op1Values, op2Values, op1HighBit, op2HighBit, length);

                        BitValue[] resValues = new BitValue[length];
                        boolean foundOne = false;
                        boolean foundZero = false;
                        int onePos = -1;
                        int zeroPos = -1;
                        if (op1HighBit == op2HighBit) {
                            // the result will be positive so we are checking:
                            // How many ONE's can we put from the lowest bit up
                            // How many ZERO's can we put from the highest bit down

                            long maxVal = maxMinAbs[0];
                            long minVal = maxMinAbs[1];

                            for (int l = 0; l < length; l++) {
                                if (!foundZero && (minVal & (1L << l)) == 0) {
                                    foundZero = true;
                                    zeroPos = l;
                                    break;
                                }
                            }
                            for (int p = length - 1; p >= 0; p--) {
                                if (!foundOne && (maxVal & (1L << p)) != 0) {
                                    foundOne = true;
                                    onePos = p;
                                    break;
                                }
                            }
                            for (int q = 0; q < length; q++) {
                                if (foundZero && q < zeroPos) {
                                    resValues[q] = BitValue.ONE;
                                } else if (foundOne && q > onePos) {
                                    resValues[q] = BitValue.ZERO;
                                } else {
                                    resValues[q] = BitValue.TOP;
                                }
                            }

                        } else {
                            // the result will be negative so we are checking:
                            // How many ZERO's can we put from the lowest bit up
                            // How many ONE's can we put from the highest bit down
                            long maxVal = -maxMinAbs[1];
                            long minVal = -maxMinAbs[0];
                            if (maxVal == 0 || minVal == 0) {
                                // result could be 0, but we are negative, so all bits could be anything
                                result = top;
                                return;
                            }
                            for (int m = 0; m < length; m++) {
                                if (!foundOne && (maxVal & (1L << m)) != 0) {
                                    foundOne = true;
                                    onePos = m;
                                    break;
                                }
                            }
                            for (int n = length - 1; n >= 0; n--) {
                                if (!foundZero && (minVal & (1L << n)) == 0) {
                                    foundZero = true;
                                    zeroPos = n;
                                    break;
                                }
                            }
                            for (int o = 0; o < length; o++) {
                                if (foundOne && o < onePos) {
                                    resValues[o] = BitValue.ZERO;
                                } else if (foundZero && o > zeroPos) {
                                    resValues[o] = BitValue.ONE;
                                } else {
                                    resValues[o] = BitValue.TOP;
                                }
                            }
                        }
                        result = new BitValueArray(resValues);
                    }
                }
            }
        }

        @Override
        public void caseRemExpr(RemExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ConstantBitsElement.BitValueArray op1 = operandValues.getFirst();
            ConstantBitsElement.BitValueArray op2 = operandValues.getSecond();
            if (op1.containsBOTTOM() || op2.containsBOTTOM()) {
                throw new IllegalStateException("Transition of BOTTOM not possible!");
            }

            if (op1.equals(top) || op2.equals(top)) {
                // if one is top, the result is top
                result = top;

            } else if (op2.isConst() && op1.isConst()) {
                // if both are constants, we can just divide the corresponding ArithmeticConstants
                result = new BitValueArray((ArithmeticConstant) op1.getConstant().remainder(op2.getConstant()));

            } else {
                // both are not top and at least one is not a constant
                // therefore we have to calculate remainder using special cases or heuristics

                // Checking if one is Long:
                int l1 = op1.getLength();
                int l2 = op2.getLength();
                int length = Math.max(l1, l2);
                BitValue op1HighBit = op1.getBitValues()[l1 - 1];
                BitValue op2HighBit = op2.getBitValues()[l2 - 1];

                if (op2.isZero()) {
                    result = top;

                } else if (op2.isPowerOfTwo() && op1.isNotNegative()) {
                    // if op2 is a power of two, the abs of op1 is simply cut left of the power
                    int cutPosition = op2.getPositionOfOne();
                    BitValue[] bitValues = new BitValue[l1];
                    for (int a = 0; a < cutPosition; a++) {
                        bitValues[a] = op1.getBitValues()[a];
                    }
                    for (int b = cutPosition; b < l1; b++) {
                        bitValues[b] = BitValue.ZERO;
                    }
                    result = new BitValueArray(bitValues);

                } else {
                    // none of the special cases applied, so bring on those nasty heuristics :P
                    BitValue[] op1Values = new BitValue[length];
                    BitValue[] op2Values = new BitValue[length];

                    // make both the same length
                    for (int i = 0; i < length; i++) {
                        if (i >= l1) {
                            op1Values[i] = op1HighBit;
                        } else {
                            op1Values[i] = op1.getBitValues()[i];
                        }
                        if (i >= l2) {
                            op2Values[i] = op2HighBit;
                        } else {
                            op2Values[i] = op2.getBitValues()[i];
                        }
                    }

                    int numberOfTops1 = getNumberOfTOP(op1);
                    int numberOfTops2 = getNumberOfTOP(op2);
                    if (numberOfTops1 + numberOfTops2 <= TOP_TRESHOLD) {
                        // heuristic of calculationg the remainder for each possibility:
                        int dim1 = (1 << numberOfTops1);
                        int dim2 = (1 << numberOfTops2);
                        int dimGes = dim1 * dim2;
                        BitValueArray[] possibilities = new BitValueArray[dimGes];

                        for (long counter1 = 0; counter1 < dim1; counter1++) {
                            for (long counter2 = 0; counter2 < dim2; counter2++) {
                                BitValue[] op1ValuesPossibility = new BitValue[length];
                                BitValue[] op2ValuesPossibility = new BitValue[length];
                                int topCounter1 = 0;
                                int topCounter2 = 0;
                                for (int j = 0; j < length; j++) {
                                    if (op1Values[j] == BitValue.TOP) {
                                        topCounter1++;
                                        op1ValuesPossibility[j] = BitValueArray
                                                .booleanToBitValue((counter1 & (1L << (topCounter1 - 1))) != 0);
                                    } else {
                                        op1ValuesPossibility[j] = op1Values[j];
                                    }
                                    if (op2Values[j] == BitValue.TOP) {
                                        topCounter2++;
                                        op2ValuesPossibility[j] = BitValueArray
                                                .booleanToBitValue((counter2 & (1L << (topCounter2 - 1))) != 0);
                                    } else {
                                        op2ValuesPossibility[j] = op2Values[j];
                                    }
                                }
                                BitValueArray op1Possibility = new BitValueArray(op1ValuesPossibility);
                                BitValueArray op2Possibility = new BitValueArray(op2ValuesPossibility);
                                possibilities[(int) (counter1 * dim2 + counter2)] =
                                        new BitValueArray((ArithmeticConstant) op1Possibility.getConstant()
                                                .remainder(op2Possibility.getConstant()));
                            }
                        }
                        // joining the possibilities
                        BitValueArray refVal = possibilities[0];
                        BitValueArray top = BitValueArray.getTop(length);
                        BitValueArray bottom = BitValueArray.getBottom(length);
                        for (int j = 1; j < dimGes; j++) {
                            refVal = join.getJoinHelper().performSingleJoin(refVal, possibilities[j], length, top,
                                    bottom);
                        }
                        result = refVal;

                    } else {
                        // we are looking for the max abs of op2 and op1 cause the abs of the result will be
                        // smaller than both of these
                        BitValue[] op1MaxValues = new BitValue[length];
                        BitValue[] op2MaxValues = new BitValue[length];
                        BitValue[] op2MinValues = new BitValue[length];
                        BitValueArray op1MaxArray;
                        BitValueArray op2MaxArray;
                        BitValueArray op2MinArray;
                        ArithmeticConstant op1MaxConst;
                        ArithmeticConstant op2MaxConst;
                        ArithmeticConstant op2MinConst;
                        long op1MaxAbs = -1;
                        long op2MaxAbs = -1;
                        long op2MinAbs = -1;
                        boolean op1Computable = false;
                        boolean op2Computable = false;

                        // Calculating the actual long values of op1MinAbs und op2MaxAbs
                        if (!(op1HighBit == BitValue.TOP)) {
                            op1Computable = true;
                            for (int k = 0; k < length; k++) {
                                switch (op1Values[k]) {
                                case ONE:
                                    op1MaxValues[k] = BitValue.ONE;
                                    break;
                                case ZERO:
                                    op1MaxValues[k] = BitValue.ZERO;
                                    break;
                                case TOP:
                                    op1MaxValues[k] = BitValueArray.booleanToBitValue(op1HighBit == BitValue.ZERO);
                                    break;
                                case BOTTOM:
                                    throw new IllegalStateException("Transition of BOTTOM not possible!");
                                }
                            }
                            op1MaxArray = new BitValueArray(op1MaxValues);
                            op1MaxConst = op1MaxArray.getConstant();
                            ConstantRetriever constantSwitch = new ConstantRetriever();
                            op1MaxConst.apply(constantSwitch);
                            op1MaxAbs = Math.abs(constantSwitch.getValue());
                        }
                        if (!(op2HighBit == BitValue.TOP)) {
                            op2Computable = true;
                            for (int k = 0; k < length; k++) {
                                switch (op2Values[k]) {
                                case ONE:
                                    op2MaxValues[k] = BitValue.ONE;
                                    op2MinValues[k] = BitValue.ONE;
                                    break;
                                case ZERO:
                                    op2MaxValues[k] = BitValue.ZERO;
                                    op2MinValues[k] = BitValue.ZERO;
                                    break;
                                case TOP:
                                    op2MaxValues[k] = BitValueArray.booleanToBitValue(op2HighBit == BitValue.ZERO);
                                    op2MinValues[k] = BitValueArray.booleanToBitValue(op2HighBit == BitValue.ONE);
                                    break;
                                case BOTTOM:
                                    throw new IllegalStateException("Transition of BOTTOM not possible!");
                                }
                            }
                            op2MaxArray = new BitValueArray(op2MaxValues);
                            op2MinArray = new BitValueArray(op2MinValues);
                            op2MaxConst = op2MaxArray.getConstant();
                            op2MinConst = op2MinArray.getConstant();
                            ConstantRetriever constantSwitch = new ConstantRetriever();
                            op2MaxConst.apply(constantSwitch);
                            op2MaxAbs = Math.abs(constantSwitch.getValue());
                            constantSwitch = new ConstantRetriever();
                            op2MinConst.apply(constantSwitch);
                            op2MinAbs = Math.abs(constantSwitch.getValue());
                        }
                        if (op1HighBit == BitValue.ONE && op1MaxAbs >= op2MinAbs) {
                            // the result could be 0, but we are negative, so all bits could be anything
                            result = top;
                            return;
                        }

                        // Calculating the minimum if existing
                        long resultingMaxAbs;
                        if (op1Computable) {
                            if (op2Computable) {
                                resultingMaxAbs = Math.min(op1MaxAbs, op2MaxAbs);
                            } else {
                                resultingMaxAbs = op1MaxAbs;
                            }
                        } else {
                            if (op2Computable) {
                                resultingMaxAbs = op2MaxAbs;
                            } else {
                                // if we don't know the sign of op1 or op2 we have no information at all
                                result = top;
                                return;
                            }
                        }
                        if (op1HighBit != BitValue.ZERO) {
                            // Since we could in some cases have another resulting TOP Bit because of 2-Complement
                            resultingMaxAbs++;
                        }

                        // Putting together the result which has the same sign as op1 and can be anything which's abs is
                        // smaller than the resulting abs
                        BitValue[] resultingValues = new BitValue[length];
                        int highestTopBitPos = -1;
                        for (int a = length - 1; a >= 0; a--) {
                            if ((resultingMaxAbs & (1L << a)) != 0) {
                                highestTopBitPos = a;
                                break;
                            } else {
                                resultingValues[a] = op1HighBit;
                            }
                        }
                        for (int b = 0; b <= highestTopBitPos; b++) {
                            resultingValues[b] = BitValue.TOP;
                        }
                        result = new BitValueArray(resultingValues);
                    }
                }
            }
        }

        @Override
        public void caseNegExpr(NegExpr expr) {
            Value negOp = expr.getOp();
            Evaluator negSwitch = new Evaluator(inputElement, join);
            negOp.apply(negSwitch);
            BitValueArray val = negSwitch.getResult();
            if (val.containsBOTTOM()) {
                throw new IllegalStateException("Transition of BOTTOM not possible!");
            }

            if (val.equals(top)) {
                // neg(top) = top
                result = top;

            } else {
                // not top, so we negate the bits one by one
                int length = val.getLength();
                BitValue[] resBitValues = new BitValue[length];
                BitValue[] opBitValues = val.getBitValues();
                for (int i = 0; i < length; i++) {
                    if (opBitValues[i].equals(BitValue.TOP)) {
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
                        BitValue[] checkTop = checkSubTop(opAndcarry);

                        if (checkTop[0] == BitValue.TOP) {
                            bitValues[j] = BitValue.TOP;
                            carry = checkTop[1];

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

        /**
         * Performs a commutative bitwise operation like (AND/OR/XOR) on the the two given {@code BitValueArray}s
         * 
         * @param op1
         *        first {@code BitValueArray} to perform the commutative bitwise operation on
         * @param op2
         *        second {@code BitValueArray} to perform the commutative bitwise operation on
         * @param operation
         *        the commutative bitwise operation to perform
         */
        private void commutativeBitwiseOperation(BitValueArray op1, BitValueArray op2, BitOperation operation) {
            if (op1.containsBOTTOM() || op2.containsBOTTOM()) {
                throw new IllegalStateException("Transition of BOTTOM not possible!");
            }

            if (op1.equals(top) || op2.equals(top)) {
                result = top;
                // if one is top, the result is top

            } else if (op1.isConst() && op2.isConst()) {
                // if both are constants, we can just use the operation of the corresponding ArithmeticConstants
                switch (operation) {
                case AND:
                    result = new BitValueArray((ArithmeticConstant) op1.getConstant().and(op2.getConstant()));
                    break;
                case OR:
                    result = new BitValueArray((ArithmeticConstant) op1.getConstant().or(op2.getConstant()));
                    break;
                case XOR:
                    result = new BitValueArray((ArithmeticConstant) op1.getConstant().xor(op2.getConstant()));
                    break;
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
                        break;
                    case OR:
                        if (op1Values[i] == BitValue.ONE || op2Values[i] == BitValue.ONE) {
                            bitValues[i] = BitValue.ONE;
                            continue;
                        }
                        break;
                    case XOR:
                        // ignore, since there is no eliminating option for XOR
                    }

                    // then check if one of the bits is TOP
                    if (op1Values[i] == BitValue.TOP || op2Values[i] == BitValue.TOP) {
                        bitValues[i] = BitValue.TOP;

                    } else {
                        // the bits are both neither TOP nor eliminating, so we can convert them to boolean
                        // and use the & / | / ^
                        boolean b1 = BitValueArray.bitValueToBoolean(op1Values[i]);
                        boolean b2 = BitValueArray.bitValueToBoolean(op2Values[i]);
                        switch (operation) {
                        case AND:
                            bitValues[i] = BitValueArray.booleanToBitValue(b1 & b2);
                            break;
                        case OR:
                            bitValues[i] = BitValueArray.booleanToBitValue(b1 | b2);
                            break;
                        case XOR:
                            bitValues[i] = BitValueArray.booleanToBitValue(b1 ^ b2);
                            break;
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

        /**
         * Shifts the bits of {@code op} by {@code shiftAmount} left.
         * 
         * @param op
         *        the {@code BitValueArray} which is to be shifted left
         * @param shiftAmount
         *        the amount of which to shift the bits of {@code op} left
         * @return the bits of {@code op} shifted left by {@code shiftAmount}
         */
        private static BitValueArray shiftLeft(BitValueArray op, int shiftAmount) {
            int length = op.getBitValues().length;
            BitValue[] opValues = op.getBitValues();
            BitValue[] shiftedValues = new BitValue[length];
            for (int i = 0; i < shiftAmount; i++) {
                shiftedValues[i] = BitValue.ZERO;
            }
            for (int j = shiftAmount; j < length; j++) {
                shiftedValues[j] = opValues[j - shiftAmount];
            }
            return new BitValueArray(shiftedValues);
        }

        /**
         * Signed-shifts the bits of {@code op} by {@code shiftAmount} right.
         * 
         * @param op
         *        the {@code BitValueArray} which is to be signed-shifted right
         * @param shiftAmount
         *        the amount of which to signed-shift the bits of {@code op} right
         * @return the bits of {@code op} signed-shifted right by {@code shiftAmount}
         */
        private BitValueArray signedShiftRight(BitValueArray op, int shiftAmount) {
            int length = op.getBitValues().length;
            BitValue sign = op.getBitValues()[length - 1];
            BitValue[] opValues = op.getBitValues();
            BitValue[] shiftedValues = new BitValue[length];
            for (int i = length - 1; i >= length - shiftAmount; i--) {
                shiftedValues[i] = sign;
            }
            for (int j = length - shiftAmount - 1; j >= 0; j--) {
                shiftedValues[j] = opValues[j + shiftAmount];
            }
            return new BitValueArray(shiftedValues);
        }

        /**
         * Unsigned-shifts the bits of {@code op} by {@code shiftAmount} right.
         * 
         * @param op
         *        the {@code BitValueArray} which is to be unsigned-shifted right
         * @param shiftAmount
         *        the amount of which to unsigned-shift the bits of {@code op} right
         * @return the bits of {@code op} unsigned-shifted right by {@code shiftAmount}
         */
        private BitValueArray unsignedShiftRight(BitValueArray op, int shiftAmount) {
            int length = op.getBitValues().length;
            BitValue[] opValues = op.getBitValues();
            BitValue[] shiftedValues = new BitValue[length];
            for (int i = length - 1; i >= length - shiftAmount; i--) {
                shiftedValues[i] = BitValue.ZERO;
            }
            for (int j = length - shiftAmount - 1; j >= 0; j--) {
                shiftedValues[j] = opValues[j + shiftAmount];
            }
            return new BitValueArray(shiftedValues);
        }

        /**
         * Returns the {@code int} that is represented by the lowest {@code cutLength} {@code BitValue}s in
         * {@code op2Values}.
         * 
         * @param op2Values
         *        The array of {@code BitValue}s of which to take the lowest entries
         * @param cutLength
         *        the number of entries to take from {@code op2Values}
         * @return the {@code int} that is represented by the lowest {@code cutLength} {@code BitValue}s in
         *         {@code op2Values}
         */
        private int getShiftAmount(BitValue[] op2Values, int cutLength) {
            BitValue[] shiftAmountValues = new BitValue[op2Values.length];
            for (int i = 0; i < cutLength; i++) {
                shiftAmountValues[i] = op2Values[i];
            }
            for (int j = cutLength; j < shiftAmountValues.length; j++) {
                shiftAmountValues[j] = BitValue.ZERO;
            }
            BitValueArray shiftAmountArray = new BitValueArray(shiftAmountValues);
            ArithmeticConstant c = shiftAmountArray.getConstant();
            ConstantRetriever constantSwitch = new ConstantRetriever();
            c.apply(constantSwitch);
            int shiftAmount = (int) constantSwitch.getValue();
            return shiftAmount;
        }

        @Override
        public void caseShlExpr(ShlExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ConstantBitsElement.BitValueArray op1 = operandValues.getFirst();
            ConstantBitsElement.BitValueArray op2 = operandValues.getSecond();
            if (op1.containsBOTTOM() || op2.containsBOTTOM()) {
                throw new IllegalStateException("Transition of BOTTOM not possible!");
            }
            if (op1.equals(top) || op2.equals(top)) {
                // if one is top, the result is top
                result = top;

            } else if (op2.isConst()) {
                // if op2 is constant, we can just calculate the shift amount and shift op1 by that amount
                int cutLength = (op1.getLength() == 32) ? 5 : 6;
                int shiftAmount = getShiftAmount(op2.getBitValues(), cutLength);
                result = shiftLeft(op1, shiftAmount);

            } else {
                // no special case applies and we have TOP bits in op2
                int numberOfTOPs = getNumberOfTOP(op2);
                int op1Length = op1.getLength();
                int op2Length = op2.getLength();
                int cutLength = (op1.getLength() == 32) ? 5 : 6;

                if (numberOfTOPs <= TOP_TRESHOLD) {
                    // the number of TOP bits in op2 is small enough to use the heuristic brute force approach
                    int dim = (1 << numberOfTOPs);
                    BitValue[] op2Values = op2.getBitValues();
                    BitValueArray[] op2Possibilities = new BitValueArray[dim];
                    for (long counter = 0; counter < dim; counter++) {
                        BitValue[] op2ValuesPossibility = new BitValue[op2Length];
                        for (int j = 0; j < op2Length; j++) {
                            if (op2Values[j] == BitValue.TOP) {
                                op2ValuesPossibility[j] = BitValueArray.booleanToBitValue((counter & (1L << j)) != 0);
                            } else {
                                op2ValuesPossibility[j] = op2Values[j];
                            }
                        }
                        int shiftAmount = getShiftAmount(op2ValuesPossibility, cutLength);
                        op2Possibilities[(int) counter] = shiftLeft(op1, shiftAmount);
                    }

                    // joining the possibilities
                    BitValueArray refVal = op2Possibilities[0];
                    BitValueArray top = BitValueArray.getTop(op1Length);
                    BitValueArray bottom = BitValueArray.getBottom(op1Length);
                    for (int j = 1; j < dim; j++) {
                        refVal = join.getJoinHelper().performSingleJoin(refVal, op2Possibilities[j], op1Length, top,
                                bottom);
                    }
                    result = refVal;
                } else {
                    result = top;
                }
            }
        }

        @Override
        public void caseShrExpr(ShrExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ConstantBitsElement.BitValueArray op1 = operandValues.getFirst();
            ConstantBitsElement.BitValueArray op2 = operandValues.getSecond();
            if (op1.containsBOTTOM() || op2.containsBOTTOM()) {
                throw new IllegalStateException("Transition of BOTTOM not possible!");
            }
            if (op1.equals(top) || op2.equals(top)) {
                // if one is top, the result is top
                result = top;

            } else if (op2.isConst()) {
                // if op2 is constant, we can just calculate the shift amount and shift op1 by that amount
                int cutLength = (op1.getLength() == 32) ? 5 : 6;
                int shiftAmount = getShiftAmount(op2.getBitValues(), cutLength);
                result = signedShiftRight(op1, shiftAmount);
            } else {
                // no special case applies and we have TOP bits in op2
                int numberOfTOPs = getNumberOfTOP(op2);
                int op1Length = op1.getLength();
                int op2Length = op2.getLength();
                int cutLength = (op1.getLength() == 32) ? 5 : 6;

                if (numberOfTOPs <= TOP_TRESHOLD) {
                    // the number of TOP bits in op2 is small enough to use the heuristic brute force approach
                    int dim = (1 << numberOfTOPs);
                    BitValue[] op2Values = op2.getBitValues();
                    BitValueArray[] op2Possibilities = new BitValueArray[dim];
                    for (long counter = 0; counter < dim; counter++) {
                        BitValue[] op2ValuesPossibility = new BitValue[op2Length];
                        for (int j = 0; j < op2Length; j++) {
                            if (op2Values[j] == BitValue.TOP) {
                                op2ValuesPossibility[j] = BitValueArray.booleanToBitValue((counter & (1L << j)) != 0);
                            } else {
                                op2ValuesPossibility[j] = op2Values[j];
                            }
                        }
                        int shiftAmount = getShiftAmount(op2ValuesPossibility, cutLength);
                        op2Possibilities[(int) counter] = signedShiftRight(op1, shiftAmount);
                    }

                    // joining the possibilities
                    BitValueArray refVal = op2Possibilities[0];
                    BitValueArray top = BitValueArray.getTop(op1Length);
                    BitValueArray bottom = BitValueArray.getBottom(op1Length);
                    for (int j = 1; j < dim; j++) {
                        refVal = join.getJoinHelper().performSingleJoin(refVal, op2Possibilities[j], op1Length, top,
                                bottom);
                    }
                    result = refVal;
                } else {
                    result = top;
                }
            }
        }

        @Override
        public void caseUshrExpr(UshrExpr expr) {
            ValuePair operandValues = calcOperands(expr);
            ConstantBitsElement.BitValueArray op1 = operandValues.getFirst();
            ConstantBitsElement.BitValueArray op2 = operandValues.getSecond();
            if (op1.containsBOTTOM() || op2.containsBOTTOM()) {
                throw new IllegalStateException("Transition of BOTTOM not possible!");
            }
            if (op1.equals(top) || op2.equals(top)) {
                // if one is top, the result is top
                result = top;

            } else if (op2.isConst()) {
                // if op2 is constant, we can just calculate the shift amount and shift op1 by that amount
                int cutLength = (op1.getLength() == 32) ? 5 : 6;
                int shiftAmount = getShiftAmount(op2.getBitValues(), cutLength);
                result = unsignedShiftRight(op1, shiftAmount);

            } else {
                // no special case applies and we have TOP bits in op2
                int numberOfTOPs = getNumberOfTOP(op2);
                int op1Length = op1.getLength();
                int op2Length = op2.getLength();
                int cutLength = (op1.getLength() == 32) ? 5 : 6;

                if (numberOfTOPs <= TOP_TRESHOLD) {
                    // the number of TOP bits in op2 is small enough to use the heuristic brute force approach
                    int dim = (1 << numberOfTOPs);
                    BitValue[] op2Values = op2.getBitValues();
                    BitValueArray[] op2Possibilities = new BitValueArray[dim];
                    for (long counter = 0; counter < dim; counter++) {
                        BitValue[] op2ValuesPossibility = new BitValue[op2Length];
                        for (int j = 0; j < op2Length; j++) {
                            if (op2Values[j] == BitValue.TOP) {
                                op2ValuesPossibility[j] = BitValueArray.booleanToBitValue((counter & (1L << j)) != 0);
                            } else {
                                op2ValuesPossibility[j] = op2Values[j];
                            }
                        }
                        int shiftAmount = getShiftAmount(op2ValuesPossibility, cutLength);
                        op2Possibilities[(int) counter] = unsignedShiftRight(op1, shiftAmount);
                    }

                    // joining the possibilities
                    BitValueArray refVal = op2Possibilities[0];
                    BitValueArray top = BitValueArray.getTop(op1Length);
                    BitValueArray bottom = BitValueArray.getBottom(op1Length);
                    for (int j = 1; j < dim; j++) {
                        refVal = join.getJoinHelper().performSingleJoin(refVal, op2Possibilities[j], op1Length, top,
                                bottom);
                    }
                    result = refVal;
                } else {
                    result = top;
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
            Type castType = expr.getCastType();
            Value op = expr.getOp();
            Type opType = op.getType();
            if (ConstantBitsElement.isLocalTypeAccepted(castType) && ConstantBitsElement.isLocalTypeAccepted(opType)) {
                // check if the type of the cast and the type of op are accepted
                Evaluator ev = new Evaluator(inputElement, join);
                op.apply(ev);
                BitValueArray opArray = ev.getResult();
                BitValue[] opValues = opArray.getBitValues();
                int length = 0;

                // get length of the resulting BitValueArray
                if (castType instanceof BooleanType || castType instanceof ByteType || castType instanceof CharType
                        || castType instanceof ShortType || castType instanceof IntType) {
                    length = BitValueArray.INT_SIZE;
                } else if (castType instanceof LongType) {
                    length = BitValueArray.LONG_SIZE;
                }

                if (length != opValues.length) {
                    // if the length of op and the length determined by the cast are not the same, cut or expand op
                    BitValue[] resultingBits = new BitValue[length];
                    for (int i = 0; i < Math.min(length, opValues.length); i++) {
                        resultingBits[i] = opValues[i];
                    }
                    for (int j = Math.min(length, opValues.length); j < length; j++) {
                        resultingBits[j] = opValues[opValues.length - 1];
                    }
                    result = new BitValueArray(opValues);

                } else {
                    // if the length of op ant the length determined by the cast are the same, the result is just op
                    result = opArray;
                }

            } else {
                // if one of the types is not accepted, the result is top
                result = top;
            }
        }

        @Override
        public void caseEqExpr(EqExpr expr) {
            // ignore
        }

        @Override
        public void caseGeExpr(GeExpr expr) {
            // ignore
        }

        @Override
        public void caseGtExpr(GtExpr expr) {
            // ignore
        }

        @Override
        public void caseLeExpr(LeExpr expr) {
            // ignore
        }

        @Override
        public void caseLtExpr(LtExpr expr) {
            // ignore
        }

        @Override
        public void caseNeExpr(NeExpr expr) {
            // ignore
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
