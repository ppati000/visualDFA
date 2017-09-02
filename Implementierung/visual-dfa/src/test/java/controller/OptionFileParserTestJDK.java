package controller;

import static org.junit.Assert.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import gui.ProgramFrame;

@SuppressWarnings("javadoc")
@Ignore
public class OptionFileParserTestJDK {

    @BeforeClass
    public static void setup() {
        System.setProperty("java.home", "C:/Program Files/Java/jdk1.7.0_76/jre");
    }

    @Test
    public void writeFileTrueOption() {
        Controller controller = new Controller();
        File fileDirectory = new File(Controller.getProgramOutputPath());
        File optionFile = new File(fileDirectory, "visualDfaOptions.txt");
        FileWriter writer;
        try {
            writer = new FileWriter(optionFile);
            writer.write("jdkpath=" + "C:/Program Files/Java/jdk1.7.0_76/jre" + ";" + System.lineSeparator());
            writer.write("closebox=" + true + ";" + System.lineSeparator());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ProgramFrame frame = new ProgramFrame(controller);
        OptionFileParser parser = new OptionFileParser(Controller.getProgramOutputPath(), frame);
        parser.setShowBox(true);
        assertTrue(parser.shouldShowBox());
    }

    @Test
    public void writeFileFalseOption() {
        Controller controller = new Controller();
        File fileDirectory = new File(Controller.getProgramOutputPath());
        File optionFile = new File(fileDirectory, "visualDfaOptions.txt");
        FileWriter writer;
        try {
            writer = new FileWriter(optionFile);
            writer.write("jdkpath=" + "C:/Program Files/Java/jdk1.7.0_76/jre" + ";" + System.lineSeparator());
            writer.write("closebox=" + false + ";" + System.lineSeparator());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ProgramFrame frame = new ProgramFrame(controller);
        OptionFileParser parser = new OptionFileParser(Controller.getProgramOutputPath(), frame);
        parser.setShowBox(true);
        assertTrue(parser.shouldShowBox());
    }

    @Test
    public void writeWrongFile() {
        Controller controller = new Controller();
        File fileDirectory = new File(Controller.getProgramOutputPath());
        File optionFile = new File(fileDirectory, "visualDfaOptions.txt");
        FileWriter writer;
        try {
            writer = new FileWriter(optionFile);
            writer.write("jdkpath=" + "C:/Program Files/Java/jdk1.7.0_76/jreX" + ";" + System.lineSeparator());
            writer.write("closebox=" + false + ";" + System.lineSeparator());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ProgramFrame frame = new ProgramFrame(controller);
        OptionFileParser parser = new OptionFileParser(Controller.getProgramOutputPath(), frame);
        parser.setShowBox(true);
        assertTrue(parser.shouldShowBox());
    }

    @Test
    public void deleteFile() {
        File dir = new File(System.getProperty("user.home") + System.getProperty("file.separator") + "visualDfa");
        ;
        deleteFolder(dir);
        Controller controller = new Controller();
        ProgramFrame frame = new ProgramFrame(controller);
        OptionFileParser parser = new OptionFileParser(Controller.getProgramOutputPath(), frame);
        parser.setShowBox(true);
        assertTrue(parser.shouldShowBox());
        parser.setShowBox(false);
        assertFalse(parser.shouldShowBox());
    }

    private void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

}
