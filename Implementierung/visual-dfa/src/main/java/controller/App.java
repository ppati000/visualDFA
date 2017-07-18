package controller;

import java.awt.EventQueue;

import gui.ProgramFrame;

public class App {
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		ProgramFrame frame;
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new ProgramFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
