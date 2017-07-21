package controller;

import java.awt.EventQueue;

import gui.ProgramFrame;

public class App {

    /**
     * Launch the application.
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
    }
}
