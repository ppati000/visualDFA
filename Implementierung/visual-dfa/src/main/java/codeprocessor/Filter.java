package codeprocessor;

import dfa.framework.TaintAnalysisTag;
import soot.SootMethod;

/**
 * 
 * @author Anika Nietzer Abstract class, that represents a filter for methods.
 *
 */
public abstract class Filter {

    /**
     * {@code Filter} for a given SootMethod, that decides whether the method
     * should be filtered out or not. This method is responsible for filtering
     * out all the methods that are added to the user input to enable
     * Taint-Analysis.
     * 
     * @param method
     *            method, that is tested
     * @return whether the method passes the filter or not
     */
    public boolean filterTaint(SootMethod method) {
        String signature = method.getSubSignature();
        if (signature.startsWith("void __taint")) {
            method.addTag(TaintAnalysisTag.TAINT_TAG);
            return false;
        } else if (signature.startsWith("void __clean")) {
            method.addTag(TaintAnalysisTag.CLEAN_TAG);
            return false;
        } else if (signature.startsWith("void __sensitive")) {
            method.addTag(TaintAnalysisTag.SENSITIVE_TAG);
            return false;
        }
        return true;
    }

    /**
     * Method for filtering a SootMethod. The full implementation is done by the
     * classes {@code NoFilter} and {@code StandardFiltler} that extend this
     * abstract class.
     * 
     * @param method
     *            SootMethod that should be tested
     * 
     * @return if the method passes the filter or not
     */
    public boolean filter(SootMethod method) {
        return false;
    }

}
