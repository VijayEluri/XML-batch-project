package edu.bxml.swt;

import org.eclipse.swt.SWT;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;

import edu.bxml.swt.Enumerations.align;

/**
 * Grid data for a Grid type of layout
 */
@attribute(value = "", required = false)
public class GridData extends LayoutData {
	org.eclipse.swt.layout.GridData gridData = null;
	int style = 0;
	
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
		String style = attrs.getValue("style");
		this.style = XmlParser.getFieldValues(org.eclipse.swt.layout.GridData.class, style);
		gridData = new org.eclipse.swt.layout.GridData(this.style);
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
	
	public org.eclipse.swt.layout.GridData getGUIObject() {
		return gridData;
	}
	
	/**
	 *  Minimum height for the row.
	 */
	@attribute(value = "", required = false)
	public void setHeightHint(Integer hint) {
		gridData.heightHint = hint;
	}
	public void setHeightHint(String hint) throws XMLParseException {
		setHeightHint(Integer.parseInt(hint));
	}
	
	/**
	 *  Minimum width for the column
	 */
	@attribute(value = "", required = false)
	public void setWidthHint(Integer hint) {
		gridData.widthHint = hint;
	}
	public void setWidthHint(String hint) throws XMLParseException {
		setWidthHint(Integer.parseInt(hint));
	}
	
	/**
	 * Tell the layout how many cells this area spans horizontally
	 */
	@attribute(value = "", required = false, defaultValue="1")
	public void setHorizontalSpan(Integer span) {
		gridData.horizontalSpan = span;
	}
	public void setHorizontalSpan(java.lang.String span) throws XMLParseException {
		setHorizontalSpan(Integer.parseInt(span));
	}

	/**
	 * Exclude
	 */
	@attribute(value = "", required = false, defaultValue="false")
	public void setExclude(Boolean exclude) {
		gridData.exclude = exclude;
	}
	public void setExclude(String exclude) throws XMLParseException {
		setExclude(Boolean.parseBoolean(exclude));
	}

	/**
	 * How controls will be positioned horizontally within a cell. (BEGINNING, CENTER, END, FILL)
	 */
	@attribute(value = "", required = false, defaultValue="BEGINNING")
	public void setHorizontalAlignment(String horizontalAlignment) throws XMLParseException {
		gridData.horizontalAlignment = XmlParser.getFieldValues(SWT.class, horizontalAlignment);
	}

	/**
	 *  The number of pixels of indentation that will be placed along the left side of the cell.
	 */
	@attribute(value = "", required = false)
	public void setHorizontalIndent(Integer horizontalIndent) {
		gridData.horizontalIndent = horizontalIndent;
	}
	public void setHorizontalIndent(String horizontalIndent) throws XMLParseException{
		gridData.horizontalIndent = Integer.parseInt(horizontalIndent);
	}

	/**
	 *  Minimum height for the row.
	 */
	@attribute(value = "", required = false)
	public void setMinimumHeight(Integer minimumHeight) {
		gridData.minimumHeight = minimumHeight;
	}
	public void setMinimumHeight(String minimumHeight) throws XMLParseException{
		setMinimumHeight(Integer.parseInt(minimumHeight));
	}

	/**
	 *  Minimum width for the column.
	 */
	@attribute(value = "", required = false)
	public void setMinimumWidth(Integer minimumWidth) {
		gridData.minimumWidth = minimumWidth;
	}
	public void setMinimumWidth(String minimumWidth) throws XMLParseException{
		gridData.minimumWidth = Integer.parseInt(minimumWidth);
	}
	
	/**
	 *  How controls will be positioned vertically within a cell.  (BEGINNING, CENTER, END, FILL)
	 */
	@attribute(value = "", required = false, defaultValue="CENTER")
	public void setVerticalAlignment(align verticalAlignment) {
		gridData.verticalAlignment = XmlParser.getFieldValues(SWT.class, verticalAlignment.toString());
	}
	public void setVerticalAlignment(String verticalAlignment) throws XMLParseException{
		setVerticalAlignment(align.valueOf(verticalAlignment));
	};

	/**
	 *  The number of pixels of indentation that will be placed along the top side of the cell.
	 */
	@attribute(value = "", required = false)
	public void setVerticalIndent(Integer verticalIndent) {
		gridData.verticalIndent = verticalIndent;
	}
	public void setVerticalIndent(String verticalIndent) throws XMLParseException{
		setVerticalIndent(Integer.parseInt(verticalIndent));
	}

	/**
	 * The number of row cells that the control will take up.
	 */
	@attribute(value = "", required = false, defaultValue="1")
	public void setVerticalSpan(Integer verticalSpan) {
		gridData.verticalSpan = verticalSpan;
	}
	public void setVerticalSpan(String verticalSpan) throws XMLParseException{
		setVerticalSpan(Integer.parseInt(verticalSpan));
	}
	
	/**
	 * Specifies whether the cell will be made wide enough to fit the remaining horizontal space.
	 */
	@attribute(value = "", required = false, defaultValue="false")
	public void  setGrabExcessHorizontalSpace (Boolean grabExcessHorizontalSpace) {
		gridData.grabExcessHorizontalSpace = grabExcessHorizontalSpace;
	}
	public void  setGrabExcessHorizontalSpace (String grabExcessHorizontalSpace) throws XMLParseException {
		setGrabExcessHorizontalSpace(Boolean.parseBoolean(grabExcessHorizontalSpace));
	}
	
	/**
	 * Specifies whether the cell will be made tall enough to fit the remaining vertical space
	 */
	@attribute(value = "", required = false, defaultValue="false")
	public void  setGrabExcessVerticalSpace (Boolean grabExcessVerticalSpace) {
		gridData.grabExcessVerticalSpace = grabExcessVerticalSpace;
	}
	public void  setGrabExcessVerticalSpace (String grabExcessVerticalSpace) throws XMLParseException {
		setGrabExcessVerticalSpace(Boolean.parseBoolean(grabExcessVerticalSpace));
	}
	
	/**
	 * BEGINNING, CENTER, END, FILL, VERTICAL_ALIGN_BEGINNING, VERTICAL_ALIGN_CENTER, VERTICAL_ALIGN_END, VERTICAL_ALIGN_FILL, HORIZONTAL_ALIGN_BEGINNING, HORIZONTAL_ALIGN_CENTER, HORIZONTAL_ALIGN_END, HORIZONTAL_ALIGN_FILL, GRAB_HORIZONTAL, GRAB_VERTICAL, FILL_VERTICAL = VERTICAL_ALIGN_FILL | GRAB_VERTICAL , FILL_HORIZONTAL = HORIZONTAL_ALIGN_FILL | GRAB_HORIZONTAL , FILL_BOTH = FILL_VERTICAL | FILL_HORIZONTAL
	 */
	@attribute(value = "", required = false)
	public void setStyle(java.lang.String text) {}

}
