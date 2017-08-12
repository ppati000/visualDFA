package codeprocessor;

import soot.SootMethod;
import soot.tagkit.Tag;

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
        if (signature.equals("void taint(java.lang.Object)")) {
            Tag tag = null;
            // TODO tag = TaintAnalysisTag.TAINT_TAG
            method.addTag(tag);
            return false;
        } else if (signature.equals("void clean(java.lang.Object)")) {
            Tag tag = null;
            // TODO tag = TaintAnalysisTag.CLEAN_TAG
            method.addTag(tag);
            return false;
        } else if (signature.equals("void sensitive()")) {
            Tag tag = null;
            // TODO tag = TaintAnalysisTag.SENSITIVE_TAG
            method.addTag(tag);
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
