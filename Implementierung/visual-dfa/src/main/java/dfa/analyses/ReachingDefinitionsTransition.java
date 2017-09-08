package dfa.analyses;

import dfa.analyses.ReachingDefinitionsElement.DefinitionSet;
import dfa.framework.Transition;
import dfa.framework.UnsupportedStatementException;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.BreakpointStmt;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.NopStmt;
import soot.jimple.RetStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.StmtSwitch;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThrowStmt;
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

        private ReachingDefinitionsElement outputElement;

        /**
         * Creates a {@code Transitioner} with the given input-{@code ReachingDefinitionsElement}.
         * 
         * @param inputElement
         *        the input-{@code ReachingDefinitionsElement}
         */
        public Transitioner(ReachingDefinitionsElement inputElement) {
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
            JimpleLocal lValLocal;
            if (stmt.getLeftOp() instanceof JimpleLocal) {
                lValLocal = (JimpleLocal) stmt.getLeftOp();
            } else {
                return;     // ignore
            }

            Value rVal = stmt.getRightOp();
            DefinitionSet rhs = new DefinitionSet(rVal);

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
            } else {
                return;     // ignore
            }

            Value rVal = stmt.getRightOp();
            DefinitionSet rhs = new DefinitionSet(rVal);

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
}
