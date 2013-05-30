package edu.bxml.swt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;

/**
 * A debug tool that sends the contents of the xml tag's body to System.out
 */
@attribute(value = "", required = false)
public class Print extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(Print.class);
	String name = null;
	Object object = null;
	String text = null;
	
	public void setFromTextContent(String text) throws XMLBuildException {
		this.text = text;
	}
	@Override
	public void execute() throws XMLBuildException {
		System.out.println(XmlParser.processMacros(this.getSymbolTable(), text));
	}
	@Override
	public void check() throws XMLBuildException {

	}

	public void setObject(String name) {
		this.name = name;
		this.object = symbolTableLookUp(name);
	}
}
