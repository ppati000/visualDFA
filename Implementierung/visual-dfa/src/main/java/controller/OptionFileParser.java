package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.tools.ToolProvider;

import gui.GenericBox;
import gui.Option;
import gui.ProgramFrame;

/**
 * @author Anika
 *
 */
public class OptionFileParser {

    private static final String PATH_SELECTION = "Select the path to your JDK 1.7 folder. "
            + "Example: C:\\Programme\\Java\\jdk1.7.0_76";
    private static final String NO_COMPILER_FOUND = "No Java compiler could be found in your selected directory. "
            + "Please set the path correctly as the program needs a functional java compiler for analysing code. "
            + "Example: C:\\Programme\\Java\\jdk1.7.0_76";
    private static final String OPTION_FILE_NAME = "visualDfaOptions.txt";
    private boolean showBox = true;
    private String compilerPath = "";
    private String programOutputPath;
    private ProgramFrame programFrame;
    private File optionFile;

    /**
     * @param programOutputPath
     * @param programFrame
     */
    public OptionFileParser(String programOutputPath, ProgramFrame programFrame) {
        this.programOutputPath = programOutputPath;
        this.programFrame = programFrame;

        File fileDirectory = new File(this.programOutputPath);

        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }
        String completePath = this.programOutputPath + System.getProperty("file.separator") + OPTION_FILE_NAME;
        this.optionFile = new File(completePath.toString());

        if (optionFile.exists()) {
            String content = readOutFile(optionFile);
            if (!validateOptionFile(content)) {
                optionFile.delete();
            } else {
                return;
            }
        }

        askForJDKPath();
        writeNewFile();

    }

    private String readOutFile(File optionFile) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(optionFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String input;
        String text = new String();
        try {
            while ((input = reader.readLine()) != null) {
                text += input.trim();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    private boolean validateOptionFile(String content) {
        if (content == null || !content.contains("jdkpath=") || !content.contains("closebox=")) {
            return false;
        }
        int start = content.indexOf("jdkpath=") + 8;
        int stop = content.indexOf(";");
        String jdkPath = content.substring(start, stop).trim();
        start = content.indexOf("closebox=") + 9;
        stop = content.indexOf(";", start);
        String closeBoxBool = content.substring(start, stop).trim();
        if (closeBoxBool.equals("true")) {
            this.showBox = true;
        } else if (closeBoxBool.equals("false")) {
            this.showBox = false;
        } else {
            System.out.println("hier");
            return false;

        }
        File selectedJDKPath = new File(jdkPath);
        if (!validJREPath(selectedJDKPath)) {
            return false;
        }
        this.compilerPath = selectedJDKPath.getAbsolutePath();
        System.setProperty("java.home", this.compilerPath);
        if (ToolProvider.getSystemJavaCompiler() == null) {
            return false;
        }
        return true;
    }

    private boolean validJREPath(File path) {
        if ((path == null) || !path.exists()) {
            return false;
        }
        // TODO does it really look like that in other OS
        String testIfJREPath = path.getAbsolutePath().trim() + System.getProperty("file.separator") + "bin";
        File jreFile = new File(testIfJREPath.toString());
        if (!jreFile.exists()) {
            return false;
        }
        String[] files = jreFile.list();
        for (String name: files) {
            if (name.startsWith("java")) {
                return true;
            }
        }
        return false;
    }

    private void askForJDKPath() {
        if (ToolProvider.getSystemJavaCompiler() != null) {
            this.compilerPath = System.getProperty("java.home");
            return;
        }

        GenericBox box = new GenericBox(this.programFrame, "JDK Path", PATH_SELECTION, "Select", "Close Program", null,
                false, Option.YES_OPTION);
        if (box.getOption() == Option.NO_OPTION) {
            System.exit(0);
        }
        File selectedPath = modifyInput(this.programFrame.getCompilerPath());
        boolean isJREPath = validJREPath(selectedPath);
        if (isJREPath) {
            System.setProperty("java.home", selectedPath.getAbsolutePath());
        }
        while (!isJREPath || (ToolProvider.getSystemJavaCompiler() == null)) {
            box = new GenericBox(this.programFrame, "JDK Path", NO_COMPILER_FOUND, "Select", "Close Program", null,
                    false, Option.YES_OPTION);
            if (box.getOption() == Option.NO_OPTION) {
                System.exit(0);
            }

            selectedPath = modifyInput(this.programFrame.getCompilerPath());
            isJREPath = validJREPath(selectedPath);
            if (isJREPath) {
                System.setProperty("java.home", selectedPath.getAbsolutePath());
            }
        }
        this.compilerPath = selectedPath.getAbsolutePath();
    }

    private File modifyInput(File selectedPath) {
        File windowsPath = new File(selectedPath.getAbsolutePath() + System.getProperty("file.separator") + "jre");
        System.out.println("windoes:" + windowsPath);
        File macPath = new File(selectedPath.getAbsolutePath() + System.getProperty("file.separator") + "Contents"
                
                + System.getProperty("file.separator") + "Home" + System.getProperty("file.separator") + "jre");
        System.out.println("mac: " + macPath);
        if (windowsPath.exists()) {
            return windowsPath;
        } else if (macPath.exists()) {
            return macPath;
        }
        return null;
    }

    private void writeNewFile() {
        File fileDirectory = new File(this.programOutputPath);
        this.optionFile = new File(fileDirectory, OPTION_FILE_NAME);
        FileWriter writer;
        try { 
            writer = new FileWriter(optionFile);
            writer.write("jdkpath=" + this.compilerPath + ";" + System.lineSeparator());
            writer.write("closebox=" + this.showBox + ";" + System.lineSeparator());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return shouldshowbox
     */
    public boolean shouldShowBox() {
        return this.showBox;
    }

    /**
     * @param showMessageBox
     */
    public void setShowBox(boolean showMessageBox) {
        this.showBox = showMessageBox;
        this.optionFile.delete();
        writeNewFile();
    }


}
