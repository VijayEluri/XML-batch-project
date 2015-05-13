package edu.bxml.io;
import com.browsexml.core.XMLBuildException;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.format.Field;

/**
 * Used in a Load that doen't have a header line to skip unneeded data.
 * 
 * @param file
 */
@attribute(value = "", required = false)
public class SkipField extends Field {
	int count = 0;
	
	public String insertFormat(String value) throws XMLBuildException {
		return null;
	}
	
	public Object getObject(String value) throws XMLBuildException {
		return null;
	}
	
	public String getSQLType() {
		return null;
	}
	
	/**
	 * Retrieve the text that was contained inside the tag
	 */
	public String format(Object value) throws XMLBuildException {
		return "";
	}
	
	/**
	 * set the number of fields to skip
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void setCount(Integer count) {
		this.count = count;
	}
	public void setCount(String count) {
		setCount( Integer.parseInt(count));
	}
	
	public int getCount() {
		return count;
	}
}
