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

/**
 * @author Anika Nietzer A {@code CodeProcessor} represents a unit for compiling
 *         and processing java-sourcecode to java-bytecode.
 */
public class CodeProcessor {

    private String className = "";
    private String errorMessage = "";
    private String pathName = "";
    private boolean success = false;
    private static final String DEFAULT_CLASS_NAME = "DefaultClass";
    private static final String DEFAULT_CLASS_SIGNATURE = "public class DefaultClass {";
    private static final String DEFAULT_METHOD_SIGNATURE = "public void defaultMethod() {";
    private static final String PATH_SEPARATOR = System.getProperty("os.name").contains("windows") ? "\\" : "/";

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
        this.pathName = System.getProperty("user.home") + PATH_SEPARATOR + "visualDfa" + PATH_SEPARATOR;
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
        if (codeToCompile.contains(" class") || codeToCompile.startsWith("class ")) {
            this.className = getClassNameOfCode(codeToCompile);
            diagnosticCollector = compile(this.className, codeToCompile);
        }
        if (!this.success) {
            String codeToCompileWrapClass = DEFAULT_CLASS_SIGNATURE + codeToCompile + "}";
            this.className = DEFAULT_CLASS_NAME;
            diagnosticCollector = compile(DEFAULT_CLASS_NAME, codeToCompileWrapClass);
        }
        if (!this.success) {
            String codeToCompileWrapMethodClass = DEFAULT_CLASS_SIGNATURE + DEFAULT_METHOD_SIGNATURE + codeToCompile
                    + "}}";
            this.className = DEFAULT_CLASS_NAME;
            compile(DEFAULT_CLASS_NAME, codeToCompileWrapMethodClass);
        }

        if (!success) {
            List<Diagnostic<? extends JavaFileObject>> diagnostics = diagnosticCollector.getDiagnostics();
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
                this.errorMessage = this.errorMessage + diagnostic.getMessage(null) + System.lineSeparator();
            }
            return;
        }

    }

    private String getClassNameOfCode(String codeToCompile) {
        int startOfClass = codeToCompile.indexOf("class");
        String tryToFindName = codeToCompile.substring(startOfClass + 5).trim();
        while(!Character.isAlphabetic(tryToFindName.charAt(0))) {
            tryToFindName = tryToFindName.substring(1).trim();
        }
        int endOfClassName;
        for (endOfClassName = 0; endOfClassName < tryToFindName.length(); ++endOfClassName) {
            char c = tryToFindName.charAt(endOfClassName);
            if (c == '{' || c == '<' || Character.isWhitespace(c)) {
                break;
            }
        }
        String nameOfClass = tryToFindName.substring(0, endOfClassName).trim();
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
        return this.className;
    }

    /**
     * Returns an error message, in case of failure.
     * 
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * Returns the name of the package of the compiled class.
     * 
     * @return the pathName.
     */
    public String getPathName() {
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