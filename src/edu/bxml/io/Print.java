package edu.bxml.io;

import java.io.PrintStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

/**
 * A debug tool that sends the contents of the xml tag's body to System.out
 */
@attribute(value = "", required = false)
public class Print extends Filter {
	private static Log log = LogFactory.getLog(Print.class);
	String name = null;
	Object object = null;
	String text = "";
	PrintStream out = System.out;
	
	public void setFromTextContent(String text) {
		new Exception("").printStackTrace();
		log.debug("PRINT text " + text);
		this.text = text;
	}
	@Override
	public void execute() throws XMLBuildException {
		log.debug("EXECUTE PRINT " + text);
		out.println(XmlParser.processMacros(this.getSymbolTable(), text));
	}
	@Override
	public void check() throws XMLBuildException {

	}

	public void setObject(String name) {
		this.name = name;
		this.object = symbolTableLookUp(name);
	}
}
