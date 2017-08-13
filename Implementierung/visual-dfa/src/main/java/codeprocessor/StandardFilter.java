package codeprocessor;

import soot.SootMethod;

/**
 * 
 * @author Anika Nietzer This class extends the {@code Filter}-class. Used if
 *         all Java library methods should be filtered out.
 *
 */
public class StandardFilter extends Filter {

    /**
     * Filter for a given method, that decides if the method passes the filter
     * or not.
     * 
     * @param method
     *            SootMethod that should be tested
     * @return if the method passes the filter or not
     */
    public boolean filter(SootMethod method) {
        return !method.isJavaLibraryMethod() && super.filterTaint(method);
    }

}
