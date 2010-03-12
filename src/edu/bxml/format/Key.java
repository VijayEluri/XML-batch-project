package edu.bxml.format;

import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;
/**
 * Specify the unique id after all groups have been
 * aggregated.  This is similar to a 'group by' clause
 * in an sql query.  The sql must return this field in sorted
 * order for the aggregation to work properly.
 * @author ritcheyg
 *
 */
@attribute(value = "", required = true)
public class Key extends XmlObject {
	
	public String name = null;
	
	/**
	 * let the parent identify itself to this object
	 */
	public void setParent(XmlObject parent) {
		
	}
	
	/**
	 * Sets the name of the field that will 
	 * uniquely determine each record AFTER
	 * all groups have been applied.
	 */
	@attribute(value = "", required = true)	
	public void setField(String fieldName) {
		name = fieldName;
	}
	/**
	 * check that all the fields are set correctly, especially
	 * required fields.  Called when the end-tag of the 
	 * element has been reached and processed.
	 */
	public void check() {
		
	}
	/**
	 * Called after complete parsing of XML document
	 * to evaluate the document.
	 */
	public void execute() {
		
	}
	/**
	 * Retrieve the text that was contained inside the tag
	 */
	public void setText(String text) {
		
	}
}
