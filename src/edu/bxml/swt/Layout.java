package edu.bxml.swt;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;

public abstract class Layout extends XmlObject {
	public abstract org.eclipse.swt.widgets.Layout getGUIObject();
}
