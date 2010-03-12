package edu.bxml.swt;

import org.eclipse.swt.SWT;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.swt.Enumerations.layout;

/**
 * 
 */
@attribute(value = "", required = false)
public class RowLayout extends Layout {
	org.eclipse.swt.layout.RowLayout rowLayout = 
		new org.eclipse.swt.layout.RowLayout();
	
	public org.eclipse.swt.widgets.Layout getGUIObject() {
		return rowLayout;
	}
	
	@Override
	public void execute() throws XMLBuildException {
		
	}
	@Override
	public void check() throws XMLBuildException {

	}
	
	/**
	 * wrap the contents of the row to the next line
	 * @param strWrap
	 */
	@attribute(value = "", required = false)
	public void setWrap(Boolean wrap) {
		rowLayout.wrap = wrap;
	}
	public void setWrap(String wrap) {
		setWrap(Boolean.parseBoolean(wrap));
	}
	
	/**
	 * 
	 * @param fill
	 */
	@attribute(value = "", required = false)
	public void setFill(Boolean fill) {
		rowLayout.fill = fill;
	}
	public void setFill(String fill) {
		setFill(Boolean.parseBoolean(fill));
	}
	/**
	 * put any extra space placed between the controls to justify
	 * @param strWrap
	 */
	@attribute(value = "", required = false)
	public void setJustify(Boolean justify) {
		rowLayout.justify = justify;
	}
	public void setJustify(String justify) {
		setJustify(Boolean.parseBoolean(justify));
	}
	
	/**
	 * HORIZONTAL or VERTICAL layout
	 * 
	 * @param strWrap
	 */
	@attribute(value = "", required = false, defaultValue="HORIZONTAL")
	public void setType(layout type) {
		rowLayout.type = XmlParser.getFieldValues(SWT.class, type.toString());
	}
	public void setType(String type) {
		setType(layout.valueOf(type));
	}
	
	/**
	 * number of pixels between the edge of one cell and the edge of its neighbouring cell.
	 * 
	 * @param strWrap
	 */
	@attribute(value = "", required = false)
	public void setSpacing(Integer spacing) {
		rowLayout.spacing = spacing;
	}
	public void setSpacing(String spacing) {
		setSpacing(Integer.parseInt(spacing));
	}
	
	/**
	 * all controls same size
	 * 
	 * @param strWrap
	 */
	@attribute(value = "", required = false, defaultValue="true")
	public void setPack(Boolean pack) {
		rowLayout.pack = pack;
	}
	public void setPack(String pack) {
		setPack(Boolean.parseBoolean(pack));
	}

}
