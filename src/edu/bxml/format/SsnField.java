package edu.bxml.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.javalobby.tnt.annotation.attribute;

/**
 * Hold information about how to format one field if embedded in a Select object or 
 * how to expect data to be formated in a flat file if embedded in a Load object.
 * <p>Also has all the attributes and nested elements that Field has.</p>
 * 
 */
@attribute(value = "", required = true)
public class SsnField extends CharField {
	private static Log log = LogFactory.getLog(SsnField.class);	
	Select select = null;
	Pattern p = Pattern.compile("(\\d{3})-?(\\d{2})-?(\\d{4})");
	
	public SsnField() {
		type = Types.CHAR;
	}
	
	@Override
	public String format(Object v) throws XMLBuildException {
		String returnValue = null;
		boolean quoteStrings = false;
		Select s = (Select)this.getAncestorOfType(edu.bxml.format.Select.class);
		if (s!= null) {
			quoteStrings = s.getQuoteStrings();
		}
		if (v == null) {
			returnValue = defaultValue;
		}
		else {
			returnValue = v.toString();
			
			 Matcher m = p.matcher(returnValue);
			 if (m.matches()) {
				 returnValue = m.group(1) + "-" + m.group(2) + "-" + m.group(3);
			 }
		}
		
		if (pretext != null) {
			returnValue = pretext + returnValue;
		}
		if (postText != null) {
			returnValue = returnValue  + postText;
		}
		if (quoteStrings) {
			return "\"" + returnValue + "\"";
		}
		return returnValue;
	}
}
