package edu.bxml.io;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.javalobby.tnt.annotation.attribute;

/**
 * A debug tool that sends the contents of the xml tag's body to System.out
 */
@attribute(value = "", required = false)
public class Redirect extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(Redirect.class);
	String name = null;
	Object object = null;
	String text = null;
	PrintStream out = System.out;
	
	public Redirect() {
	}
	
	public void setFromTextContent(String text) throws XMLBuildException {
		this.text = text;
	}
	@Override
	public void execute() throws XMLBuildException {
	}
	@Override
	public void check() throws XMLBuildException {
	}
	public void setStderr(String filename) {
		try {
			filename = filename;
			log.debug("redirect stderr to " + filename);
			System.setErr(new PrintStream(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
	}
	public void setStdout(String filename) {
		try {
			filename = filename;
			log.debug("redirect stdout to " + filename);
			System.setOut(new PrintStream(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
	}
}
