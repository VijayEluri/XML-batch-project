package edu.bxml.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.javalobby.tnt.annotation.attribute;

/**
 * Pop up a message box showing an exception message or 
 * other informative message contained in the body of the tag.
 */
@attribute(value = "", required = false)
public class Exception extends XmlObjectImpl implements XmlObject {
	private String message = null;
	private boolean throwIt = false;
	
	public void setFromTextContent(String text) {
		String message = text.trim();
		if (!message.equals(""))
			this.message = message;
	}
	@Override
	public void execute() throws XMLBuildException {
		
	}
	@Override
	public void check() throws XMLBuildException {
		 MessageBox messageBox =
			   new MessageBox(Display.getDefault().getShells()[0],
			    SWT.OK|SWT.ICON_ERROR);
		 messageBox.setMessage(message);
		 messageBox.open();
		 if (throwIt)
			 throw new XMLBuildException(message);
	}
	
	/**
	 * Actually throw an exception.  If true this will cause the remainder
	 * of a page currently being processed not to be interpreted.
	 */
	@attribute(value = "", required = false, defaultValue="false")
	public void setThrow(Boolean throwIt) {
		this.throwIt = throwIt;
	}
	public void setThrow(String throwIt) {
		this.throwIt = Boolean.parseBoolean(throwIt);
	}

}
