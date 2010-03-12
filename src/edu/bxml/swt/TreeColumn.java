package edu.bxml.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeItem;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.swt.Enumerations.columnAlign;

/**
 * Instances of this class represent a column in a table widget. 
 */
@attribute(value = "", required = false)
public class TreeColumn extends GUIObject {
	org.eclipse.swt.widgets.Composite GUIParent = null;
	org.eclipse.swt.widgets.TreeColumn column = null;
	int style = 0;
	private String dataSep = ":";
	
	public org.eclipse.swt.widgets.TreeColumn getGUIObject() {
		return column;
	}
	
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
		String style = attrs.getValue("style");
		this.style = XmlParser.getFieldValues(SWT.class, style);
		column = new org.eclipse.swt.widgets.TreeColumn(
				(org.eclipse.swt.widgets.Tree)getGUIParent(), this.style);
		return true;
	}
	
	/**
	 * Use this string to separate values if multiple columns are selected 
	 */
	@attribute(value = "", required = false, defaultValue = ":")
	public void setDataSeparation(String text) {
		dataSep = text;
	}
	
	
	/**
	 * LEFT, RIGHT, CENTER 
	 * Events: Move, Resize, Selection  
	 */
	@attribute(value = "", required = false)
	public void setStyle(String style) {
		
	}
	
	@Override
	public void execute() throws XMLBuildException {
	}
	
	@Override
	public void check() throws XMLBuildException {

	}

	public org.eclipse.swt.widgets.Composite getGUIParent() {
		return GUIParent;
	}

	public void setGUIParent(org.eclipse.swt.widgets.Widget parent) {
		GUIParent = (org.eclipse.swt.widgets.Composite) parent;
	}
	
	/**
	 * Set the column's alignment LEFT, RIGHT, CENTER 
	 */
	@attribute(value = "", required = false)
	public void setAlignment(columnAlign a) {
		column.setAlignment(XmlParser.getFieldValues(SWT.class, a.toString()));
	}
	
	public void setAlignment(String a) {
		setAlignment(columnAlign.valueOf(a));
	}
	
	/**
	 * Sets the moveable attribute
	 */
	@attribute(value = "", required = false)
	public void setMoveable(Boolean moveable) {
		column.setMoveable(moveable);
	}
	public void setMoveable(String moveable) {
		setMoveable(moveable);
	}
	
	/**
	 * Sets the width attribute
	 */
	@attribute(value = "", required = false)
	public void setWidth(Integer w) {
		column.setWidth(w);
	}
	public void setWidth(String w) {
		setWidth(Integer.parseInt(w));
	}
	
	/**
	 * Sets the resize attribute
	 */
	@attribute(value = "", required = false)
	public void setResizable(String resize) {
		boolean size = Boolean.parseBoolean(resize);
		column.setResizable(size);
	}
	
	/**
	 * Set the column's header text 
	 */
	@attribute(value = "", required = false)
	public void setText(String text) {
		column.setText(text);
	}
	
	public String toString() {
		String ret = "";
		org.eclipse.swt.widgets.Tree t = (org.eclipse.swt.widgets.Tree)getGUIParent();
		int colIndex = t.indexOf(column);
		String string = "";
		TreeItem [] selection = t.getSelection ();
		for (int k=0; k<selection.length; k++) 
			string += selection[k].getText(colIndex) + " ";
		for (int k = 0; k < selection.length; k++) {
			ret += dataSep + selection[k].getText(colIndex);
		}
		if (ret.length()>=dataSep.length())
			return ret.substring(dataSep.length());
		return ret;
	}

}
