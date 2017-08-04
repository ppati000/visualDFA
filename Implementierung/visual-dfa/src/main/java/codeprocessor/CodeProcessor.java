package codeprocessor;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

//TODO update JavaDoc

/**
 * @author Anika Nietzer A {@code CodeProcessor} represents a unit for compiling
 *         and processing java-sourcecode to java-bytecode.
 */
public class CodeProcessor {

    private String className;
    private String errorMessage = "";
    private String pathName;
    private boolean success = false;
    private static final String defaultClassName = "defaultClassName";
    private static final String defaultClassSignature = "public class defaultClassName {";
    private static final String defaulMethodSignature = "public void defaultMethodName() {";

    /**
     * Creates a {@code CodeProcessor} with the given code fragment and compiles
     * it to java-bytecode.
     * 
     * @param originalCode
     *            the java-code fragment, that will be compiled
     */
    public CodeProcessor(String originalCode) {
        if (originalCode == null) {
            throw new IllegalStateException("String must not be null");
        }

        String codeToCompile = originalCode;
        // TODO testen auf platformunabhängigkeit
        this.pathName = System.getProperty("user.home") + "\\visualDfa\\";
        File dir = new File(this.pathName);
        if (!dir.exists()) {
            dir.mkdir();
        }

        // remove one line comments
        // in Windows
        codeToCompile = codeToCompile.replaceAll("//.*?\r\n", "");
        // in Unix, Linux, MAC (OS 10+)
        codeToCompile = codeToCompile.replaceAll("//.*?\n", "");
        // in MAC (OS 9-)
        codeToCompile = codeToCompile.replaceAll("//.*?\r", "");

        // remove several line comments
        codeToCompile = codeToCompile.replaceAll("//.*|(\"(?:\\\\[^\"]|\\\\\"|.)*?\")|(?s)/\\*.*?\\*/", "");

        // tries to compile the user input
        DiagnosticCollector<JavaFileObject> diagnosticCollector = null;
        if (codeToCompile.contains(" class ") || codeToCompile.startsWith("class ")) {
            this.className = getClassNameOfCode(codeToCompile);
            diagnosticCollector = compile(this.className, codeToCompile);
        } else {
            if (!this.success) {
                String codeToCompileWrapClass = defaultClassSignature + codeToCompile + "}";
                this.className = defaultClassName;
                diagnosticCollector = compile(defaultClassName, codeToCompileWrapClass);
            }
            if (!this.success) {
                String codeToCompileWrapMethodClass = defaultClassSignature + defaulMethodSignature + codeToCompile
                        + "}}";
                this.className = defaultClassName;
                compile(defaultClassName, codeToCompileWrapMethodClass);
            }
        }
        if (!success) {
            List<Diagnostic<? extends JavaFileObject>> diagnostics = diagnosticCollector.getDiagnostics();
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
                this.errorMessage = this.errorMessage + diagnostic.getMessage(null) + System.lineSeparator();
            }
            return;
        }

        // TODO braucht man diesen Teil überhaupt
        /**
         * load class ClassLoader classLoader = null; try { classLoader = new
         * URLClassLoader(new URL[] { new
         * File(".").getAbsoluteFile().toURI().toURL() }); } catch
         * (MalformedURLException e) { System.out.println("no new class loader"
         * ); } try { Class.forName(this.className, true, classLoader); } catch
         * (ClassNotFoundException e) { System.out.println("no class found"); }
         **/
    }

    private String getClassNameOfCode(String codeToCompile) {
        int start = codeToCompile.indexOf("class");
        String tryToFindName = codeToCompile.substring(start + 5).trim();
        String[] split = tryToFindName.split(" ");
        tryToFindName = split[0].trim();
        // case of no break between className and {
        split = tryToFindName.split("\\{");
        tryToFindName = split[0].trim();
        // delete generic type argument
        split = tryToFindName.split("<");
        String nameOfClass = split[0].trim();
        return nameOfClass;
    }

    private class StringJavaFileObject extends SimpleJavaFileObject {
        private final CharSequence code;

        private StringJavaFileObject(String name, CharSequence code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }

    private DiagnosticCollector<JavaFileObject> compile(String name, String codeFragment) {
        StringJavaFileObject javaFile = new StringJavaFileObject(name, codeFragment);
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnosticsCollectorLocal = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticsCollectorLocal, null, null);
        Iterable<? extends JavaFileObject> units = Arrays.asList(javaFile);
        try {
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(new File(this.pathName)));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnosticsCollectorLocal, null, null,
                units);
        this.success = task.call();
        try {
            fileManager.close();
        } catch (IOException e) {
            System.out.println("file manager could not be closed");
        }
        return diagnosticsCollectorLocal;
    }

    /**
     * Returns the name of the compiled class.
     * 
     * @return the className
     */
    public String getClassName() {
        if (success && this.className == null) {
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
        if (!success && this.errorMessage == null) {
            throw new IllegalStateException("errorMessage must be set if succes is false");
        }
        return this.errorMessage;
    }

    /**
     * Returns the name of the package of the compiled class.
     * 
     * @return the pathName.
     */
    public String getPathName() {
        if (success && this.pathName == null) {
            throw new IllegalStateException("packageName must be set if succes is true");
        }
        return this.pathName;
    }

    /**
     * Returns {@code true} if the compiling process was successful or
     * {@code false} in the other case.
     * 
     * @return if the compiling process was successful
     */
    public boolean wasSuccessful() {
        return this.success;
    }
}