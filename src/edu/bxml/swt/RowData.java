package edu.bxml.swt;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;

/**
 * Grid data for a Grid type of layout
 */
@attribute(value = "", required = false)
public class RowData extends LayoutData {
	org.eclipse.swt.layout.RowData rowData = null;
	int style = 0;
	
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
		String style = attrs.getValue("style");
		this.style = XmlParser.getFieldValues(org.eclipse.swt.layout.RowData.class, style);
		rowData = new org.eclipse.swt.layout.RowData();
		return true;
	}
	
	public void init(XmlObject parent) throws XMLBuildException {
		super.init(parent);
	}
	
	@Override
	public void execute() throws XMLBuildException {
		
	
	}
	@Override
	public void check() throws XMLBuildException {
		((ControlObject) parent).gridDataDefined();
	}
	
	public org.eclipse.swt.layout.RowData getGUIObject() {
		return rowData;
	}
	
	/**
	 *  height for the item.
	 */
	@attribute(value = "", required = false)
	public void setHeight(Integer height) {
		rowData.height = height;
	}
	public void setHeight(String height) throws XMLParseException {
		setHeight(Integer.parseInt(height));
	}
	
	/**
	 *  width for the item
	 */
	@attribute(value = "", required = false)
	public void setWidth(Integer width) {
		rowData.width = width;
	}
	public void setWidth(String width) throws XMLParseException {
		setWidth(Integer.parseInt(width));
	}

}
