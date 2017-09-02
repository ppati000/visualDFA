package controller;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import gui.ProgramFrame;

@SuppressWarnings("javadoc")
@Ignore
public class OptionFileParserTestJRE {

    @Before
    public void setup() {
        System.setProperty("java.home", "C:/Program Files/Java/jre7");
    }

    @Test
    public void createFileIfJavaHomeIsJRE() {
        File dir = new File(System.getProperty("user.home") + System.getProperty("file.separator") + "visualDfa");

        deleteFolder(dir);
        Controller controller = new Controller();
        ProgramFrame frame = new ProgramFrame(controller);
        OptionFileParser parser = new OptionFileParser(Controller.getProgramOutputPath(), frame);
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