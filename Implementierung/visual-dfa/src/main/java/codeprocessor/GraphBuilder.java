package codeprocessor;

import java.util.ArrayList;
import java.util.List;

import dfa.framework.SimpleBlockGraph;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;

/**
 * @author Anika Nietzer A {@code GraphBuilder} represents a unit, that
 *         translates java-bytecode of one method to a {@code SimpleBlockGraph}.
 */
public class GraphBuilder {

    private SootClass test;
    private List<SootMethod> methods;

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
        Scene.v().setSootClassPath(pathName);
        // TODO make it work for all pcs
        String jdkPath = "C:\\Program Files\\Java\\jdk1.7.0_76\\jre\\lib\\rt.jar";
        Scene.v().extendSootClassPath(jdkPath);
        this.test = Scene.v().loadClassAndSupport(className);
        test.setApplicationClass();
        this.methods = test.getMethods();
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
        SootMethod method = this.test.getMethod(methodSignature);
        JimpleBody body = Jimple.v().newBody(method);
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
    // TODO Konstruktoren rausnehmen?
    public List<String> getMethods(Filter filter) {
        List<String> filteredMethods = new ArrayList<String>();
        for (SootMethod methodTest : this.methods) {
            if (filter.filter(methodTest)) {
                filteredMethods.add(methodTest.getSubSignature());
            }
        }
        return filteredMethods;
    }
}
