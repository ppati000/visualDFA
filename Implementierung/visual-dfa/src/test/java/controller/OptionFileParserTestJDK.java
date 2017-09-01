package controller;

import org.junit.runners.MethodSorters;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;

import gui.ProgramFrame;

@Ignore
public class OptionFileParserTestJDK {
    
    @BeforeClass
    public static void setup() {
        System.setProperty("java.home", "C:/Program Files/Java/jdk1.7.0_76/jre");
    }

    
    /**
     * korrektes File vorhanden
     */
    @Test
    public void test01() {
        Controller controller = new Controller();
        File fileDirectory = new File(controller.getProgramOutputPath());
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
        OptionFileParser parser = new OptionFileParser(controller.getProgramOutputPath(), frame);
        parser.setShowBox(true);
        assertTrue(parser.shouldShowBox());
    }
    
    /**
     * korrektes File vorhanden
     */
    @Test
    public void test02() {
        Controller controller = new Controller();
        File fileDirectory = new File(controller.getProgramOutputPath());
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
        OptionFileParser parser = new OptionFileParser(controller.getProgramOutputPath(), frame);
        parser.setShowBox(true);
        assertTrue(parser.shouldShowBox());
    }
    
    
    /**
     * korrektes File vorhanden
     */
    @Test
    public void test04() {
        Controller controller = new Controller();
        File fileDirectory = new File(controller.getProgramOutputPath());
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
        OptionFileParser parser = new OptionFileParser(controller.getProgramOutputPath(), frame);
        parser.setShowBox(true);
        assertTrue(parser.shouldShowBox());
    }
    
    

    /**
     * kein File vorhanden
     */
    @Test
    public void test05() {
        File dir = new File(System.getProperty("user.home") + System.getProperty("file.separator") + "visualDfa");               ;
        deleteFolder(dir);
        Controller controller = new Controller();
        ProgramFrame frame = new ProgramFrame(controller);
        OptionFileParser parser = new OptionFileParser(controller.getProgramOutputPath(), frame);
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
