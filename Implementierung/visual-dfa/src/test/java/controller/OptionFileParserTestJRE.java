package controller;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;

import gui.ProgramFrame;

@Ignore
public class OptionFileParserTestJRE {

    @Before
    public void setup() {
        System.setProperty("java.home", "C:/Program Files/Java/jre7");
    }

    /**
     * korrektes File vorhanden
     */
    @Test
    public void test00() {
        File dir = new File(System.getProperty("user.home") + System.getProperty("file.separator") + "visualDfa");

        deleteFolder(dir);
        Controller controller = new Controller();
        ProgramFrame frame = new ProgramFrame(controller);
        OptionFileParser parser = new OptionFileParser(controller.getProgramOutputPath(), frame);
        parser.setShowBox(true);
        assertTrue(parser.shouldShowBox());
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