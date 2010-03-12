package edu.bxml.swt;


import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;

/**
 * All controls are subclassed from this.
 * See also GUIObject
 */
@attribute(value = "", required = false)
public abstract class ControlObject extends GUIObject {  
	private static Log log = LogFactory.getLog(ControlObject.class);
	public abstract org.eclipse.swt.widgets.Control getGUIObject();
	LayoutData layoutData = null;
	boolean showUrl = true;
	String loc = null;
	
	
	public void init(XmlObject parent) throws XMLBuildException {
		super.init(parent);
		loc = this.getSource() + ":" + this.getLineNumber();
		getGUIObject().addMouseListener(new MouseAdapter() {
			   public void mouseDown(MouseEvent e) {
				   if (e.button > 1) 
					   System.err.println("Name: " + getName() + "   " + loc);
			}
		}
			);
	}
	
	/**
	 * BORDER, LEFT_TO_RIGHT, RIGHT_TO_LEFT 
	 * events: FocusIn, FocusOut, Help, KeyDown, KeyUp, MouseDoubleClick, MouseDown, MouseEnter, MouseExit, MouseHover, MouseUp, MouseMove, Move, Paint, Resize, Traverse, DragDetect, MenuDetect 
	 */
	public void setStyle(String style) {
		//dummy
	}
	/**
	 * Set the text to pop up when the mouse hovers over a control.
	 */
	@attribute(value = "", required = false)
	public void setToolTipText(String text) {
		this.getGUIObject().setToolTipText(text);
	}
	
	//called on check() of child gridData object
	public void gridDataDefined() {
		if (layoutData != null) {
			this.getGUIObject().setLayoutData(layoutData.getGUIObject());
		}
	}
	
	/**
	 * If part of a grid layout, add the layout data for this control
	 */
	@attribute(value = "", required = false)
	public void addLayoutData(LayoutData layoutData) {
		this.layoutData = layoutData;
	}
	
	/**
	 * Makes the control visible or invisible.
	 */
	@attribute(value = "", required = false, defaultValue="true")
	public void setVisible(Boolean visible) {
		this.getGUIObject().setVisible(visible);
	}
	public void setVisible(String visible) throws XMLParseException {
		setVisible(Boolean.parseBoolean(visible));
	}
	
	/**
	 * Enable the control.
	 */
	@attribute(value = "", required = false, defaultValue="true")
	public void setEnabled(Boolean enabled) {
		this.getGUIObject().setEnabled(enabled);
	}
	public void setEnabled(String enabled) throws XMLParseException {
		setEnabled(Boolean.parseBoolean(enabled));
	}
	
	/**
	 * Size as a comma separated width and height
	 */
	@attribute(value = "", required = false)
	public void setSize(String size) throws XMLParseException {
		String[] sizes = size.split("\\s*,\\s*");
		int width = SWT.DEFAULT;
		try {
			width = Integer.parseInt(sizes[0]);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		int height = SWT.DEFAULT;
		try {
			height = Integer.parseInt(sizes[1]);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		log.debug("set Size(" + width + ", " + height + ")");
		this.getGUIObject().setSize(width, height);
		
	}
	public boolean isShowUrl() {
		return showUrl;
	}
	public void setShowUrl(boolean showUrl) {
		this.showUrl = showUrl;
	}
}
