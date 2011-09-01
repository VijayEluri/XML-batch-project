package com.browsexml.core;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.eclipse.swt.layout.FillLayout;

public class DefaultErrorHandler extends JFrame implements ErrorHandler {
	public void noErrors() {};
	public void viewErrors(boolean console, String msg) {
		
		if (console) {
			System.err.println(msg);
		}
		else {
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		    Dimension screenSize = getToolkit().getScreenSize();
		    int width = screenSize.width / 2;
		    int height = screenSize.height / 10;
		    setBounds(screenSize.width/2-width/2, 
		    			screenSize.height/2 - height/2, width, height);
		    JLabel label = new JLabel("<html><b><left>" + msg + "</left></b></html>");
		    getContentPane().add(label, BorderLayout.NORTH);
		    setVisible(true);
		}
	}
}
