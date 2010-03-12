package edu.bxml.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableItem;
import org.xml.sax.SAXParseException;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

/**
 * Add a row of data to a table.
 */
@attribute(value = "", required = false)
public class Tr extends XmlObject {
	org.eclipse.swt.widgets.Table GUIParent = null;
	int style = 0;
	TableItem item = null;
	int count = 0;
	Apply apply = null;
	
	public org.eclipse.swt.widgets.TableItem getGUIObject() {
		return item;
	}
	
	public void setForeground(String fg) {
		Color x = (Color) getSymbolTable().get(fg);
		item.setForeground(x.getColor());
	}

	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
		String style = attrs.getValue("style");
		this.style = XmlParser.getFieldValues(SWT.class, style);
		
		item = new TableItem(getGUIParent(), this.style);
		return true;
	}

	@Override
	public void execute() throws XMLBuildException {
		
	}
	@Override
	public void check() throws XMLBuildException {
	}
	
	/**
	 * Add a cell of data
	 */
	@attribute(value = "", required = false)
	public void addTd(Td td) {}
	
	public void addTdEnd(Td td) {
		String value = td.getText();
		if (value == null) 
			value = "";
		if (td.getColor() != null) {
			item.setForeground(count, td.getColor());
		}
		item.setText(count++, value);
	}
	
	public org.eclipse.swt.widgets.Table getGUIParent() {
		return GUIParent;
	}

	public void setGUIParent(org.eclipse.swt.widgets.Table parent) {
		GUIParent = parent;
	}
}
