package edu.bxml.swt;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

/**
 * Add a cell of data to a table.  The data is contained in the body of the tag.
 */
@attribute(value = "", required = false)
public class Td extends XmlObject {
	String text = "";
	org.eclipse.swt.graphics.Color color = null;
	
	public void setFromTextContent(String text) {
		this.text = text;
	}
	
	public void setForeground(String fg) {
		Color x = (Color) getSymbolTable().get(fg);
		color = x.getColor();
	}
	
	public org.eclipse.swt.graphics.Color getColor() {
		return color;
	}
	
	public String getText() {
		return text;
	}
	
	@Override
	public void execute() throws XMLBuildException {
		
	}
	@Override
	public void check() throws XMLBuildException {
	}
}
