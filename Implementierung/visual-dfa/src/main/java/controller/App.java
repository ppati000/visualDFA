package controller;

import java.awt.EventQueue;

import gui.ProgramFrame;

public class App {
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		final ProgramFrame frame = new ProgramFrame();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
