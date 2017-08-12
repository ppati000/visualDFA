package codeprocessor;

import soot.SootMethod;

/**
 * 
 * @author Anika Nietzer 
 * 			Implementation of the Interface {@code Filter}. Used if
 *          no methods should be filtered out.
 *
 */
public class NoFilter extends Filter {

	
	/**
	 * Filter for a given method, that decides if the method should be filtered
	 * out or not.
	 * 
	 * @param signature
	 *            signature of the method
	 * @return if the method passes the filter or not
	 */
	public boolean filter(SootMethod method) {
	    return super.filterTaint(method);
	}

}
