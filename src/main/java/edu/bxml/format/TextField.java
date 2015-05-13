package edu.bxml.format;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.javalobby.tnt.annotation.attribute;
/**
 * Hold information about how to format one field if embedded in a Select object or 
 * how to expect data to be formated in a flat file if embedded in a Load object.  This
 * type is not bound by a size constraint.
 * See Field.
 * 
 */
@attribute(value = "", required = true)
public class TextField extends Field {
	private static Log log = LogFactory.getLog(TextField.class);
	Select select = null;
	
	public TextField() {
		type = Types.TEXT;
	}
	@Override
	public String format(Object v) throws XMLBuildException {
		log.debug("Text format: v = " + v);
		
		String value = v.toString();
		if (value == null)
			return defaultValue;
		if (select != null) {
			Vector<Replace> parentReplace = select.getReplace();
			for (Replace r:parentReplace) {
				value = value.replaceAll(r.getExpression(), r.getReplacement());
			}
		}
		for (Replace r:replacements) {
			value = value.replaceAll(r.getExpression(), r.getReplacement());
		}
		return value;
	}

	public void check() throws XMLBuildException {
		super.check();
		
		select = (Select) getAncestorOfType(Select.class);
	}
	
	public String insertFormat(String value) throws XMLBuildException {
		// TODO Auto-generated method stub
		StringBuffer retValue = new StringBuffer();
		if (value == null)
			retValue.append("null, ");
		else {
			retValue.append("'").append(format(value)).append("', ");
		}
		return retValue.toString();
	}
	
	public String getSQLType() {
		return "text";
	}
	
	public String getObject(String value) {
		return value;
	}
}
