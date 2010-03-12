package edu.bxml.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

/**
 * 
 */
@attribute(value = "", required = false)
public class Color extends XmlObject {

	org.eclipse.swt.graphics.Color color = null;
	org.eclipse.swt.widgets.Shell s = null;
	
	@Override
	public void execute() throws XMLBuildException {
		
	}
	@Override
	public void check() throws XMLBuildException {
		if (color == null) {
			throw new XMLBuildException("systemColor must be set.");
		}
	}
	
	public org.eclipse.swt.graphics.Color getColor() {
		return color;
	}
	
	/**
	 * 
	 * System Color may be:
	 * COLOR_WHITE COLOR_BLACK COLOR_RED COLOR_DARK_RED 
	 * COLOR_GREEN COLOR_DARK_GREEN COLOR_YELLOW COLOR_DARK_YELLOW 
	 * COLOR_BLUE COLOR_DARK_BLUE COLOR_MAGENTA COLOR_DARK_MAGENTA 
	 * COLOR_CYAN COLOR_DARK_CYAN COLOR_GRAY COLOR_DARK_GRAY
	 */
	@attribute(value = "", required = true)
	public void setSystemColor(String c) {
		color = Display.getDefault().getSystemColor(
				XmlParser.getFieldValues(SWT.class, c));
	}
	


}
