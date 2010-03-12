package edu.bxml.swt;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

/**
 * 
 */
@attribute(value = "", required = false)
public class Close extends XmlObject {

	
	@Override
	public void execute() throws XMLBuildException {
		Shell s = (Shell) getAncestorOfType(Shell.class);
		org.eclipse.swt.widgets.Shell shell = (org.eclipse.swt.widgets.Shell) s.getGUIObject();
		shell.close();
	}
	@Override
	public void check() throws XMLBuildException {

	}

	
}
