package codeprocessor;

import java.util.ArrayList;
import java.util.List;

import dfa.framework.SimpleBlockGraph;
import soot.Body;
import soot.G;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.options.Options;

/**
 * @author Anika Nietzer A {@code GraphBuilder} represents a unit, that
 *         translates java-bytecode of one method to a {@code SimpleBlockGraph}.
 */
public class GraphBuilder {

    private SootClass sootClass;
    private List<SootMethod> methods;
    private static final String PATH_SEPARATOR = System.getProperty("os.name").contains("windows") ? "\\" : "/";

    /**
     * Creates a new {@code GraphBuilder} that works with the file present in
     * the path with {@code pathName} with the name {@code className} and
     * processes the class to a SootClass.
     * 
     * @param pathName
     *            name of the package
     * @param className
     *            name of the class
     */
    public GraphBuilder(String pathName, String className) {
        G.v();
        G.reset();
        Scene.v().setSootClassPath(pathName.toString());
        String jdkPath = System.getProperty("java.home") + PATH_SEPARATOR + "lib" + PATH_SEPARATOR + "rt.jar";
        Scene.v().extendSootClassPath(jdkPath);
        Scene.v().loadNecessaryClasses();
        Scene.v().addBasicClass(className, SootClass.BODIES);
        Scene.v().forceResolve(className, SootClass.BODIES);
        this.sootClass = Scene.v().loadClassAndSupport(className);
        sootClass.setApplicationClass();
        Options.v().setPhaseOption("jb", "use-original-names:true");
        Options.v().set_keep_line_number(true);
        this.methods = sootClass.getMethods();
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
        SootMethod method = this.sootClass.getMethod(methodSignature);
        Body body = method.retrieveActiveBody();
        SimpleBlockGraph blockGraph = new SimpleBlockGraph(body);
        return blockGraph;
    }

    /**
     * Returns a list of methods after the usage of filter.
     * 
     * @param filter
     *            filter, that decides which methods will be filtered out
     * @return list of all remaining methods
     */
    // TODO do we want to have constructors in our method selection?
    public List<String> getMethods(Filter filter) {
        List<String> filteredMethods = new ArrayList<String>();
        for (SootMethod method : this.methods) {
            if (filter.filter(method)) {
                filteredMethods.add(method.getSubSignature());
            }
        }
        return filteredMethods;
    }
}
