package codeprocessor;

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
	 * @param signature
	 *            signature of the method
	 * @return whether the method should be filtered out or not
	 */
	public boolean filter(String signature);

}
