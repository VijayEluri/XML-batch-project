package com.browsexml.core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.javalobby.tnt.annotation.attribute;

public class Browser extends JFrame implements HyperlinkListener, 
		ActionListener {
	  //private JIconButton homeButton;
	  private JTextField urlField;
	  private JEditorPane htmlPane;
	  private String initialURL = null;
	  private String type;
	  private String text;

	  public Browser(String type, String text) {
		  super("Html Viewer");
		  this.type = type;
		  this.text = text;
		  doIt(null);
	  }
	  
	  public Browser(String initialURL) {
		  super("Html Viewer");
		  this.initialURL = initialURL;
		  doIt(initialURL);
	  }
	  
	  public void doIt(String initialURL) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    this.initialURL = initialURL;
	    //addWindowListener(new ExitListener());
	    //WindowUtilities.setNativeLookAndFeel();

	    JPanel topPanel = new JPanel();
	    topPanel.setBackground(Color.lightGray);
	    //homeButton = new JIconButton("home.gif");
	    //homeButton.addActionListener(this);
	    JLabel urlLabel = new JLabel("URL:");
	    urlField = new JTextField(30);
	    urlField.setText(initialURL);
	    urlField.addActionListener(this);
	    //topPanel.add(homeButton);
	    topPanel.add(urlLabel);
	    topPanel.add(urlField);
	    getContentPane().add(topPanel, BorderLayout.NORTH);

	    try {
	    	if (initialURL == null)
	    		htmlPane = new JEditorPane(type, text);
	    	else
	    		htmlPane = new JEditorPane(initialURL);
	        htmlPane.setEditable(false);
	        htmlPane.addHyperlinkListener(this);
	        JScrollPane scrollPane = new JScrollPane(htmlPane);
	        getContentPane().add(scrollPane, BorderLayout.CENTER);
	    } catch(IOException ioe) {
	       warnUser("Can't build HTML pane for " + initialURL 
	                + ": " + ioe);
	    }

	    Dimension screenSize = getToolkit().getScreenSize();
	    int width = screenSize.width * 8 / 10;
	    int height = screenSize.height * 8 / 10;
	    setBounds(width/8, height/8, width, height);
	    setVisible(true);
	  }
	  
	  public void actionPerformed(ActionEvent event) {
		    String url;
		    if (event.getSource() == urlField) 
		      url = urlField.getText();
		    else  // Clicked "home" button instead of entering URL
		      url = initialURL;
		    try {
		      htmlPane.setPage(new URL(url));
		      urlField.setText(url);
		    } catch(IOException ioe) {
		      warnUser("Can't follow link to " + url + ": " + ioe);
		    }
		  }

		  public void hyperlinkUpdate(HyperlinkEvent event) {
		    if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
		      try {
		        htmlPane.setPage(event.getURL());
		        urlField.setText(event.getURL().toExternalForm());
		      } catch(IOException ioe) {
		        warnUser("Can't follow link to " 
		                 + event.getURL().toExternalForm() + ": " + ioe);
		      }
		    }
		  }

		  private void warnUser(String message) {
		    JOptionPane.showMessageDialog(this, message, "Error", 
		                                  JOptionPane.ERROR_MESSAGE);
		  }


}
