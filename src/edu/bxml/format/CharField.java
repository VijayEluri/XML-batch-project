package edu.bxml.format;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

/**
 * Hold information about how to format one field if embedded in a Select object or 
 * how to expect data to be formated in a flat file if embedded in a Load object.
 * <p>Also has all the attributes and nested elements that Field has.</p>
 * 
 */
@attribute(value = "", required = true)
public class CharField extends Field {
	private static Log log = LogFactory.getLog(CharField.class);	
	Select select = null;
	
	public CharField() {
		type = Types.CHAR;
	}
	
	@Override
	public String format(Object v) throws XMLBuildException {
		String returnValue = null;
		if (v == null)
			returnValue = defaultValue;
		else {
			returnValue = v.toString();

			if (select != null) {
				Vector<Replace> parentReplace = select.getReplace();
				for (Replace r:parentReplace) {
					returnValue = 
						returnValue.replaceAll(r.getExpression(), r.getReplacement());
				}
			}
			for (Replace r:replacements) {
				returnValue = 
					returnValue.replaceAll(r.getExpression(), r.getReplacement());
			}

			if (returnValue.length() > size && size >= 0) {
				log.debug(getFieldName() + ": " + " truncated, value = '" + returnValue + "'.  size = " + size + "  len of data is " + returnValue.length());
				returnValue = returnValue.substring(0, size);
			}
			
			else if (returnValue.length() < size) {
				if (leftPadding) {
					returnValue = (padding + returnValue).substring(returnValue.length());
				} 
				else if (rightPadding) {
					returnValue = (returnValue + padding).substring(0, size);
				}
			}
		}
		if (pretext != null) {
			returnValue = pretext + returnValue;
		}
		if (postText != null) {
			returnValue = returnValue  + postText;
		}
		return returnValue;
	}

	public String insertFormat(String value) throws XMLBuildException {
		// TODO Auto-generated method stub
		StringBuffer retValue = new StringBuffer();
		if (value == null)
			retValue.append("null, ");
		else {
			retValue.append("'").append(format(value)).append("', ");
		}
		if (value.length() > size) {
			log.debug(getFieldName() + ": " + " truncated, value = '" + value + "'.");
		}
		return retValue.toString();
	}

	
	public void check() throws XMLBuildException {
		super.check();

		XmlObject x = this;
		while (x != null && !((x = x.getParent()) instanceof Select));
		select = (Select) x;
	}
	
	public String getSQLType() {
		return "varchar(" + size + ")";
	}
	
	public String getObject(String value) {
		return value;
	}
}
