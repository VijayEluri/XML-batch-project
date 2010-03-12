package edu.bxml.swt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

/**
 * 
 */
@attribute(value = "", required = false)
public class MessageBox extends XmlObject {
	private static Log log = LogFactory.getLog(MessageBox.class);
	org.eclipse.swt.widgets.MessageBox messageBox = null;
	int style = 0;
	Integer continueStyle = null;
	Integer result = null;
	
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
		String style = attrs.getValue("style");
		this.style = XmlParser.getFieldValues(SWT.class, style);
		Shell s = (Shell) getAncestorOfType(Shell.class);
		log.debug("s = " + s);
		org.eclipse.swt.widgets.Shell shell = 
			(org.eclipse.swt.widgets.Shell) s.getGUIObject();
		messageBox = new org.eclipse.swt.widgets.MessageBox(shell, this.style);
		return true;
	}
	
	public void setFromTextContent(String text) {
		messageBox.setMessage(text);
	}
	@Override
	public void execute() throws XMLBuildException {
		result = messageBox.open();
		if (continueStyle != null && continueStyle != result){
			throw new XMLBuildException("chose not to continue");
		}
	}
	@Override
	public void check() throws XMLBuildException {
		
	}
	
	/**
	 * Continue if the message box returns a press from this button type (see styles)
	 * @param c
	 */
	@attribute(value = "", required = false, defaultValue="Will not thow an exception but toString() will return true or false.")
	public void setContinue(String c) {
		continueStyle = XmlParser.getFieldValues(SWT.class, c);
	}
	
	/**
	 * ICON_ERROR, ICON_INFORMATION, ICON_QUESTION, ICON_WARNING, ICON_WORKING 
	 * OK, OK | CANCEL 
	 * YES | NO, YES | NO | CANCEL 
	 * RETRY | CANCEL 
	 * ABORT | RETRY | IGNORE 
	 * 
	 * @param style
	 */
	@attribute(value = "", required = false)
	public void setStyle(java.lang.String style) {
		//dummy
	}
	
	public String toString() {
		return "" + (continueStyle != result);
	}
}
