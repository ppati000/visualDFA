package codeprocessor;

import dfa.framework.TaintAnalysisTag;
import soot.SootMethod;

/**
 * 
 * @author Anika Nietzer Interface, that represents a filter for methods.
 *
 */
public abstract class Filter {

    /**
     * Filter for a given method, that decides whether the method should be
     * filtered out or not.
     * 
     * @param method
     *            test this method
     * @return whether the method should be filtered out or not
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
     * @param method
     *            to be checked
     * @return if the method passes the filter or not
     */
    public boolean filter(SootMethod method) {
        return false;
    }

}
