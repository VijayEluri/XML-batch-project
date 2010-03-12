package edu.bxml.swt;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;

/**
 * Allow the user to enter and modify date or time values
 */
@attribute(value = "", required = false)
public class DateTime extends ControlObject {
	private static Log log = LogFactory.getLog(DateTime.class);
	private org.eclipse.swt.widgets.Composite GUIParent = null;
	private org.eclipse.swt.widgets.DateTime dateTime = null;
	SimpleDateFormat formatter = null;

	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
		String style = attrs.getValue("style");
		this.style = XmlParser.getFieldValues(SWT.class, style);
		dateTime = new org.eclipse.swt.widgets.DateTime(getGUIParent(), this.style);
		return true;
	}
	
	/**
	 *  DATE, TIME, CALENDAR, SHORT, MEDIUM, LONG  
	 *  Events: Selection  
	 */
	@attribute(value = "", required = false)
	public void setStyle(String style) {
		//dummy 
	}
	
	public Calendar getCalendar() {
		return new GregorianCalendar(
				dateTime.getYear(), dateTime.getMonth(), dateTime.getDay(), 
				dateTime.getHours(), dateTime.getMinutes());
	}
	
	public void setFormat(String format) {
		formatter = new SimpleDateFormat(format);
	}
	
	public org.eclipse.swt.widgets.DateTime getGUIObject() {
		return dateTime;
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
	
	public String getFormat() {
		log.debug("datetime to string");
		if (formatter != null) {
			return formatter.format(getCalendar().getTime());
		}
		return dateTime.toString();
	}
	
	public String toString() {
		log.debug("datetime to string");
		if (formatter != null) {
			return formatter.format(getCalendar().getTime());
		}
		return dateTime.toString();
	}
	
}

