package dfa.framework;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import dfa.analyses.ConstantBitsFactory;
import dfa.analyses.ConstantFoldingFactory;
import dfa.analyses.ReachingDefinitionsFactory;
import dfa.analyses.TaintFactory;

// TODO replace with something that actually works ... thank's soot
public class AnalysisLoader {

    private String packageName;
    private String searchPath;

    private List<String> analysisNames;
    private Map<String, DFAFactory<? extends LatticeElement>> analyses;

    public AnalysisLoader(String packageName, String searchPath) {
        if (packageName == null) {
            throw new IllegalArgumentException("packageName must not be null");
        }

        if (searchPath == null) {
            throw new IllegalArgumentException("searchPath must not be null");
        }

        this.packageName = packageName;
        this.searchPath = searchPath;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getSearchPath() {
        return searchPath;
    }

    /**
     * If the static initializer block of a {@code DFAFactory} throws an exception, this is not handled.
     * 
     * If a {@code RuntimeException} is thrown in the (parameterless) constructor of a {@code DFAFactory}, this
     * exception is caught and the class not loaded. If a {@code Throwable} other than a {@code RuntimeException} is
     * thrown, this is not handled.
     * 
     * 
     * @param logger
     *        a {@code Logger} used to report problems to (if {@code null} is given, problems are not reported)
     */
    public void loadAnalyses(Logger logger) {
        // this is just a temporary solution

        analysisNames = new LinkedList<String>();
        analyses = new HashMap<String, DFAFactory<? extends LatticeElement>>();

        //DummyFactory dummyFactory = new DummyFactory();
        //String dummyName = dummyFactory.getName();

        ConstantFoldingFactory cfFactory = new ConstantFoldingFactory();
        ConstantBitsFactory cbFactory = new ConstantBitsFactory();
        ReachingDefinitionsFactory rdFactory = new ReachingDefinitionsFactory();
        TaintFactory tFactory = new TaintFactory();

        //analysisNames.add(dummyName);
        //analyses.put(dummyName, dummyFactory);
        

        analysisNames.add(cfFactory.getName());
        analyses.put(cfFactory.getName(), cfFactory);
        
        analysisNames.add(cbFactory.getName());
        analyses.put(cbFactory.getName(), cbFactory);
        
        analysisNames.add(rdFactory.getName());
        analyses.put(rdFactory.getName(), rdFactory);
        
        analysisNames.add(tFactory.getName());
        analyses.put(tFactory.getName(), tFactory);

        // TODO resolve NoSuchMethodError on Reflections(packageName)
        // or more likely: find another way to load analyses
        /*
         * Reflections reflections = new Reflections(packageName);
         * 
         * List<Class<? extends DFAFactory>> analysisClasses = new ArrayList<Class<? extends
         * DFAFactory>>(reflections.getSubTypesOf(DFAFactory.class));
         * 
         * if (analysisNames == null) { analysisNames = new LinkedList<String>(); analyses = new HashMap<String,
         * DFAFactory>(); }
         * 
         * for (Class<? extends DFAFactory> analysisClass : analysisClasses) { Constructor<?> constructor = null;
         * 
         * String className = analysisClass.getCanonicalName(); try { constructor = analysisClass.getConstructor(); }
         * catch (NoSuchMethodException | SecurityException e) { logWarning(logger,
         * "cannot retrieve parameterless constructor of " + className, "ignoring " + className); e.printStackTrace(); }
         * 
         * if (constructor == null) { // skip this DFAFactory continue; }
         * 
         * DFAFactory dfaFactory = null; try { dfaFactory = (DFAFactory) constructor.newInstance(); } catch
         * (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
         * logWarning(logger, "cannot invoke constructor of " + className, "ignoring " + className);
         * e.printStackTrace(); } catch (RuntimeException e) { logWarning(logger, "constructor of " + className +
         * " threw " + e.getMessage(), "ignoring " + className); }
         * 
         * if (dfaFactory == null) { // skip this DFAFactory continue; }
         * 
         * String analysisName = dfaFactory.getName();
         * 
         * if (analysisNames.contains(analysisName)) { logWarning(logger, "name collision for " + analysisName,
         * "ignoring last one"); continue; }
         * 
         * analysisNames.add(analysisName); analyses.put(analysisName, dfaFactory); }
         */
    }

    public List<String> getAnalysesNames() {
        if (analysisNames == null) {
            throw new IllegalStateException("no analyses have been loaded");
        }

        return Collections.unmodifiableList(analysisNames);
    }

    public DFAFactory getDFAFactory(String analysisName) {
        if (analysisNames == null) {
            throw new IllegalStateException("no analyses have been loaded");
        }

        if (!analysisNames.contains(analysisName)) {
            return null;
        }

        return analyses.get(analysisName);
    }

    private void logWarning(Logger logger, String... warningMsg) {
        if (logger == null) {
            return;
        }

        if (warningMsg.length == 0) {
            return;
        }

        StringBuilder sb = new StringBuilder("warning: ");
        sb.append(warningMsg[0]);
        for (int i = 1; i < warningMsg.length; ++i) {
            sb.append('\n');
            sb.append(warningMsg[i]);
        }

        logger.warning(sb.toString());
    }

}
