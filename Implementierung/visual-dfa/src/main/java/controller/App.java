package controller;

import java.awt.EventQueue;

import gui.ProgramFrame;

/**
 * @author Anika Nietzer This class contains the main method for the program.
 * 
 *
 */
public final class App {

    /**
     * The main method is responsible to create a new {@code Controller} and the
     * {@code programFrame}. Furthermore it asks the user for the path to the
     * JDK and sets the visibility of the different panels.
     * 
     * @param args
     *            command line input
     */
    public static void main(String[] args) {
        final Controller controller = new Controller();
        final ProgramFrame programFrame = new ProgramFrame(controller);
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    programFrame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        controller.setProgramFrame(programFrame);
        controller.parseOptionFile();
        controller.setDefaultCode();
    }
}
