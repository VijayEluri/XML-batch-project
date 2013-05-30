package edu.bxml.swt;

import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;

public abstract class Layout extends XmlObjectImpl implements XmlObject {
	public abstract org.eclipse.swt.widgets.Layout getGUIObject();
}
