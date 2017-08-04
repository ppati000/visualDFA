package codeprocessor;

import soot.SootMethod;

/**
 * 
 * @author Anika Nietzer 
 * 			Interface, that represents a filter for methods.
 *
 */
public interface Filter {

	/**
	 * Filter for a given method, that decides whether the method should be filtered
	 * out or not.
	 * 
	 * @param method
	 *            test this method
	 * @return whether the method should be filtered out or not
	 */
	public boolean filter(SootMethod method);

}
