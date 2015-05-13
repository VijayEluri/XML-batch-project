package com.browsexml.core;



public class DefaultErrorHandler  implements ErrorHandler {
	public void noErrors() {};
	public void viewErrors(boolean console, String msg) {
		
		if (true) {
			System.err.println(msg);
		}
		else {
//			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		    Dimension screenSize = getToolkit().getScreenSize();
//		    int width = screenSize.width / 2;
//		    int height = screenSize.height / 10;
//		    setBounds(screenSize.width/2-width/2, 
//		    			screenSize.height/2 - height/2, width, height);
//		    JLabel label = new JLabel("<html><b><left>" + msg + "</left></b></html>");
//		    getContentPane().add(label, BorderLayout.NORTH);
//		    setVisible(true);
		}
	}
}
