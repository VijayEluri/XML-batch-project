package edu.bxml.swt;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;

/**
 * Lay out the control children of a Composite in a grid. 
 */
@attribute(value = "", required = false)
public class GridLayout extends Layout {
	org.eclipse.swt.layout.GridLayout gridLayout = 
		new org.eclipse.swt.layout.GridLayout();
	
	public org.eclipse.swt.widgets.Layout getGUIObject() {
		return gridLayout;
	}

	@Override
	public void execute() throws XMLBuildException {
		
	}
	@Override
	public void check() throws XMLBuildException {

	}
	
	/**
	 * Specifies the number of cell columns in the layout 
	 */
	@attribute(value = "", required = false, defaultValue="1")
	public void setNumColumns(Integer numColumns) {
		gridLayout.numColumns = numColumns;
	}
	public void setNumColumns(String numColumns) throws XMLParseException {
		setNumColumns(Integer.parseInt(numColumns));
	}

	/**
	 * specifies whether all columns in the layout will be forced to have the same width.
	 */
	@attribute(value = "", required = false, defaultValue="false")
	public void setMakeColumnsEqualWidth(Boolean equal) {
		gridLayout.makeColumnsEqualWidth = equal;
	}
	public void setMakeColumnsEqualWidth(String equal) throws XMLParseException {
		setMakeColumnsEqualWidth(Boolean.parseBoolean(equal));
	}
	
	/**
	 * specifies the number of pixels of horizontal margin that will be placed along the left and right edges of the layout
	 */
	@attribute(value = "", required = false, defaultValue="5")
	public void setMarginWidth(Integer width) {
		gridLayout.marginWidth = width;
	}
	public void setMarginWidth(String width) throws XMLParseException {
		gridLayout.marginWidth = Integer.parseInt(width);
	}
	
	/**
	 * specifies the number of pixels of vertical margin that will be placed along the top and bottom edges of the layout
	 */
	@attribute(value = "", required = false, defaultValue="5")
	public void setMarginHeight(Integer height) {
		gridLayout.marginHeight = height;
	}
	public void setMarginHeight(String height) throws XMLParseException {
		gridLayout.marginHeight = Integer.parseInt(height);
	}
	
	/**
	 * specifies the number of pixels between the bottom edge of one cell and the top edge of its neighbouring cell underneath
	 */
	@attribute(value = "", required = false, defaultValue="5")
	public void setVerticalSpacing(Integer space) {
		gridLayout.verticalSpacing = space;
	}
	public void setVerticalSpacing(String space) throws XMLParseException {
		gridLayout.verticalSpacing = Integer.parseInt(space);
	}
	
	/**
	 * specifies the number of pixels between the right edge of one cell and the left edge of its neighbouring cell to the right
	 */
	@attribute(value = "", required = false, defaultValue="5")
	public void setHorizontalSpacing(Integer space) {
		gridLayout.horizontalSpacing = space;
	}
	public void setHorizontalSpacing(String space) throws XMLParseException {
		setHorizontalSpacing(Integer.parseInt(space));
	}
	
	public org.eclipse.swt.layout.GridLayout getGridLayout() {
		return gridLayout;
	}
	
}
