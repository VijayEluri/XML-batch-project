/*
 * Created on Jun 2, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.browsexml.core;

import java.awt.SplashScreen;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;

import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * @author ritcheyg
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class Main {
	private static Log log = LogFactory.getLog(Main.class);
	Connection con = null;
	static String[] args = null;
	
	public static String[] getArgs() {
		return args;
	}

	Statement stmt = null;

	static int id = 0;

	public static void main(String[] args) {
		System.setProperty("sun.awt.noerasebackground","true");
		Main p = new Main(args);
	}

	Main(String[] args) {
		
		String file = args[0];
		Protocol.registerProtocol( "bxml", Protocol.getProtocol("http") );
		Protocol.registerProtocol( "bxmls", Protocol.getProtocol("https") );

		URL x = getClass().getClassLoader().getResource("log4j.xml");
		if (x == null) 
			x = getClass().getClassLoader().getResource("log4j.xml");
		if (x == null) {
			File f = new File(".", "log4j.xml");
			try {
				x = new URL("file:///" + f.getAbsolutePath());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		log.debug("log file url = " + x);

		try {
			System.err.println(new java.io.File(".").getCanonicalPath());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (x != null) {
			DOMConfigurator.configure(x);
		}
		else
			System.err.println("log4j.xml not found in path");
		
		ErrorHandler runner = new DefaultErrorHandler();
		

		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlParser f = null;

		boolean console = SplashScreen.getSplashScreen() == null;

		try {
			log.debug("PARSE");
			f = new XmlParser(file, factory, args);
			log.debug("PARSE DONE");
			runner.noErrors();
		}
		catch (java.net.ConnectException e) {
			runner.viewErrors(console, e.getMessage() + "  Make sure your server is up and running.");
		}
		catch (Exception p) {
				p.printStackTrace();
				runner.viewErrors(console, p.getClass().getName() + "  " + p.getMessage());
		}

		try {
			
			
			if (f != null) {
				log.debug("MAIN execute f is a " + f.getClass().getName());
				f.execute();
			}
			else {
				log.debug("MAIN execute f is null");
			}
		} catch (Exception e) {
			e.printStackTrace();
			runner.viewErrors(console, e.getMessage());
		} 
	}

	public static int getID() {
		return id++;
	}
}
