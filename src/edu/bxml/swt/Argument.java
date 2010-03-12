package edu.bxml.swt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

/**
 * 
 */
@attribute(value = "", required = false)
public class Argument extends XmlObject {
	private static Log log = LogFactory.getLog(Argument.class);
	String text = null;
	
	public void setFromTextContent(String text) {
		this.text = text;
	}
	@Override
	public void execute() throws XMLBuildException {
		
	}
	@Override
	public void check() throws XMLBuildException {

	}
	
	public String getText() {
		String ret = null;
		try {
			log.debug("process: " + text);
			log.debug("st: " + this.getSymbolTable());
			ret = XmlParser.processMacros(this.getSymbolTable(), text);
			log.debug("process result: " + ret);
		} catch (XMLBuildException e) {
			e.printStackTrace();
		}
		return ret;
	}
}
