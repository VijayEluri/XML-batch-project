package com.browsexml.core;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class xDefaultErrorHandler extends ErrorHandler {
 	   JFrame frame = null;
 	   JTextArea label = null;
 	   
 	  // TODO: figure out where this 'invokeLater' call goes
 	  //EventQueue.invokeLater(runner);
 	   
        public void run() {
          try {
              Thread.sleep(1500);
          } catch (InterruptedException e) {
          }
          frame = new JFrame("BrowseXML Console");
          //frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
          label = new JTextArea("Starting BrowseXML...");
          label.setLineWrap(true);
          label.setWrapStyleWord(true);
          label.setFont(new Font("Serif", Font.BOLD, 16));
          label.setEditable(false);
          label.setMargin(new Insets(10,10,10,10));
          frame.add(label, BorderLayout.CENTER);
          frame.setSize(300, 150);
       }
        public void noErrors() {
     	   waitFrame();
            frame.setState(Frame.ICONIFIED);
            frame.setVisible(true);
            frame.setVisible(false);
        }
        public void viewErrors(boolean x, String error) {
     	   waitFrame();
     	   frame.setState(Frame.NORMAL);
     	   label.setText(error);
     	   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     	   frame.setVisible(true);
        }
        public void waitFrame() {
     	   while (frame == null) 
	             try {
	                 Thread.sleep(1500);
	             } catch (InterruptedException e) {
	             }
        }
}
