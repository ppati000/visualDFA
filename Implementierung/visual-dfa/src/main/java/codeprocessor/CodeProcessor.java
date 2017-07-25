package codeprocessor;

/**
 * @author Anika Nietzer 
 * 			A {@code CodeProcessor} represents a unit for compiling
 *         	and processing java-sourcecode to java-bytecode.
 */
public class CodeProcessor {

	private String className;
	private String errorMessage;
	private String packageName;
	private boolean success;

	/**
	 * Creates a {@code CodeProcessor} with the given code fragment and compiles
	 * it to java-bytecode.
	 * 
	 * @param code
	 *            the java-code fragment, that will be compiled
	 */
	public CodeProcessor(String code) {
	}

	/**
	 * Returns the name of the compiled class.
	 * 
	 * @return the className
	 */
	public String getClassName() {
	    if(success && this.className == null) {
            throw new IllegalStateException("className must be set if succes is true");
        }
		return this.className;
	}

	/**
	 * Returns an error message, in case of failure.
	 * 
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
	    if(!success && this.errorMessage == null) {
            throw new IllegalStateException("errorMessage must be set if succes is false");
        }
		return this.errorMessage;
	}

	/**
	 * Returns the name of the package of the compiled class.
	 * 
	 * @return the packageName.
	 */
	public String getPackageName() {
	    if(success && this.packageName == null) {
	        throw new IllegalStateException("packageName must be set if succes is true");
	    }
		return this.packageName;
	}

	/**
	 * Returns {@code true} if the compiling process was successful or {@code false} in the other case.
	 * 
	 * @return if the compiling process was successful
	 */
	public boolean wasSuccessful() {
		return this.success;
	}
}
