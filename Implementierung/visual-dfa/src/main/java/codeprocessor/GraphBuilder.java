package codeprocessor;

import java.util.List;
import soot.toolkits.graph.BlockGraph;

/**
 * @author Anika Nietzer 
 * 			A {@code GraphBuilder} represents a unit, that
 *          translates java-bytecode of one method to a {@code SimpleBlockGraph}.
 */
public class GraphBuilder {

	/**
	 * Creates a new {@code GraphBuilder} that works with the file present in
	 * the package with {@code packageName} with the name {@code className}.
	 * 
	 * @param packageName
	 *            name of the package
	 * @param className
	 *            name of the class
	 */
	public GraphBuilder(String packageName, String className) {

	}

	/**
	 * Builds a {@code SimpleBlockGraph} of a method within the defined
	 * class-file.
	 * 
	 * @param methodSignature
	 *            name of the method
	 * @return SimpleBlockGraph of the method
	 */
	public SimpleBlockGraph buildGraph(String methodSignature) {
		return null;
	}

	/**
	 * Returns a list of methods after the usage of filter.
	 * 
	 * @param filter
	 *            filter, that decides which methods will be filtered out
	 * @return list of all remaining methods
	 */
	public List<String> getMethods(Filter filter) {
		return null;
	}
}
