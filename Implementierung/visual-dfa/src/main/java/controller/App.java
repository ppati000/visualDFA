package controller;

import java.awt.EventQueue;

import gui.ProgramFrame;

/**
 * @author Anika Nietzer Class that contains the main method for the program.
 * 
 *
 */
public final class App {

    /**
     * The main method is responsible to create a new {@code Controller} and the
     * {@code programFrame}.
     * 
     * @param args
     *            command line input
     */
    public static void main(String[] args) {
        final Controller ctrl = new Controller();
        final ProgramFrame frame = new ProgramFrame(ctrl);
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    frame.setVisible(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        
        ctrl.setProgramFrame(frame);
        ctrl.visibilityInput();
    }
}
