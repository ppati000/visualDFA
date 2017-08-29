package dfa.framework;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * An {@code AnalysisLoader} loads {@code DFAFactory}-classes from a given directory and instantiates them. The folder
 * structure must match the package structure. This means, that if the base-directory given to an {@code AnalysisLoader}
 * is .../baseDir, a class compiled under pkg.subpkg must be put into .../baseDir/pkg/subpkg in order to be loaded.
 * 
 * @author Sebastian Rauch
 */
public class AnalysisLoader {

    private static final String CLASS_FILE_EXTENSION = "class";

    private String basePath;

    private List<String> analysisNames;
    private Map<String, DFAFactory<? extends LatticeElement>> analyses;

    /**
     * Creates a {@code AnalysisLoader} with the given path to search for {@code DFAFactory}s.
     * 
     * @param basePath
     *        the path which is the root of the package structure
     */
    public AnalysisLoader(String basePath) {
        if (basePath == null) {
            throw new IllegalArgumentException("searchPath must not be null");
        }

        setBasePath(basePath);
    }

    /**
     * Returns the current base path.
     * 
     * @return the current base path
     */
    public String getBasePath() {
        return basePath;
    }

    /**
     * Sets the current base path.
     * 
     * @param basePath
     *        the new base path
     */
    protected void setBasePath(String basePath) {
        this.basePath = basePath;
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
    @SuppressWarnings("unchecked")
    public void loadAnalyses(Logger logger) {

        File baseDir = new File(basePath);

        Map<String, DFAFactory<? extends LatticeElement>> factoryMap = new HashMap<>();

        List<ClassInfoPack> candidates = new LinkedList<ClassInfoPack>();
        getClassFiles(baseDir, candidates, null);

        ClassLoader classLoader = null;
        try {
            classLoader = new URLClassLoader(new URL[] { baseDir.toURI().toURL() });
        } catch (MalformedURLException e) {
            logWarning(logger, e.getMessage(), "no analyses could be loaded");
        }

        if (classLoader == null) {
            return;
        }

        for (ClassInfoPack p : candidates) {
            Class<?> cls = null;
            try {
                cls = classLoader.loadClass(p.getFullClassName());
            } catch (ClassNotFoundException e) {
                logWarning(logger, e.getMessage(), "ignoring " + p.getFullClassName());
            }

            if (cls == null || !DFAFactory.class.isAssignableFrom(cls)) {
                continue;
            }

            Class<DFAFactory<? extends LatticeElement>> factoryClass = null;
            try {
                factoryClass = (Class<DFAFactory<? extends LatticeElement>>) cls;
            } catch (ClassCastException e) {
                logWarning(logger, e.getMessage(), "ignoring " + p.getFullClassName());
            }

            if (factoryClass == null) {
                continue;
            }

            String className = factoryClass.getCanonicalName();
            Constructor<?> constructor = null;
            try {
                constructor = factoryClass.getConstructor();
            } catch (NoSuchMethodException | SecurityException e) {
                logWarning(logger, "cannot retrieve parameterless constructor of " + className,
                        "ignoring " + className);
            }

            if (constructor == null) {
                continue; // skip this DFAFactory
            }

            DFAFactory<? extends LatticeElement> dfaFactory = null;
            try {
                dfaFactory = (DFAFactory<? extends LatticeElement>) constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                logWarning(logger, "cannot invoke constructor of " + className, "ignoring " + className);
            } catch (RuntimeException e) {
                logWarning(logger, "constructor of " + className + " threw " + e.getMessage(), "ignoring " + className);
            }

            if (dfaFactory == null) {
                continue; // skip this DFAFactory
            }

            String analysisName = dfaFactory.getName();

            if (factoryMap.containsKey(analysisName)) {
                logWarning(logger, "name collision for " + analysisName, "ignoring second one");
                continue;
            }

            factoryMap.put(analysisName, dfaFactory);
        }

        setAnalyses(factoryMap);
    }

    /**
     * Returns a {@code List} of the names of the {@code DFAFactory}-instances known to this {@code AnalysisLoader}.
     * 
     * @return a {@code List} of the names of all known {@code DFAFactory}-instances
     */
    public List<String> getAnalysesNames() {
        if (analysisNames == null) {
            throw new IllegalStateException("no analyses have been loaded");
        }

        return Collections.unmodifiableList(analysisNames);
    }

    /**
     * Fetches a {@code DFAFactory} by name.
     * 
     * @param analysisName
     *        the name of the {@code DFAFactory} to return
     * @return a {@code DFAFactory} with the given name or {@code null} if no such {@code DFAFactory} is known to this
     *         {@code AnalysisLoader}
     * 
     * @throws IllegalStateException
     *         if {@code loadAnalyses} has not been called yet
     */
    public DFAFactory<? extends LatticeElement> getDFAFactory(String analysisName) {
        if (analysisNames == null) {
            throw new IllegalStateException("no analyses have been loaded");
        }

        if (!analysisNames.contains(analysisName)) {
            return null;
        }

        return analyses.get(analysisName);
    }

    /**
     * Sets the {@code DFAFactory}-instances for this {@code AnalysisLoader}.
     * 
     * @param factoryMap
     *        a {@code Map} that maps names to {@code DFAFactory}-instances
     */
    protected void setAnalyses(Map<String, DFAFactory<? extends LatticeElement>> factoryMap) {
        this.analysisNames = new LinkedList<>();
        this.analyses = new HashMap<>();

        Set<Map.Entry<String, DFAFactory<? extends LatticeElement>>> entries = factoryMap.entrySet();
        for (Map.Entry<String, DFAFactory<? extends LatticeElement>> entry : entries) {
            this.analysisNames.add(entry.getKey());
            this.analyses.put(entry.getKey(), entry.getValue());
        }
    }

    private void getClassFiles(File dir, List<ClassInfoPack> candidates, String packagePrefix) {
        File[] files = dir.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                String newPackagePrefix = "";
                if (packagePrefix != null) {
                    newPackagePrefix = packagePrefix + ".";
                }

                newPackagePrefix += file.getName();
                getClassFiles(file, candidates, newPackagePrefix);
            } else {
                String fileName = file.getName();
                int lastDotIdx = fileName.lastIndexOf('.');
                if (lastDotIdx < 0 || lastDotIdx >= fileName.length() - 1) {
                    continue;
                }

                String fileExtension = fileName.substring(lastDotIdx + 1);
                if (CLASS_FILE_EXTENSION.equals(fileExtension)) {
                    String className = fileName.substring(0, lastDotIdx);
                    candidates.add(new ClassInfoPack(file, packagePrefix, className));
                }
            }
        }
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

    private class ClassInfoPack {

        public final String packagePrefix;
        public final String className;

        public ClassInfoPack(File file, String packagePrefix, String className) {
            this.packagePrefix = packagePrefix;
            this.className = className;
        }

        public String getFullClassName() {
            return packagePrefix + "." + className;
        }
    }

}
