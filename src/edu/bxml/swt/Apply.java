package edu.bxml.swt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXParseException;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.http.Get;
import edu.bxml.http.Post;

/**
 * Calls setters and adders for named objects using its own attributes and children.
 * Any attributes and children of the target object can be used by Apply.
 */
@attribute(value = "", required = false)
public class Apply extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(Apply.class);
	XmlObject object = null;
	String objectName = null;
	boolean set = false;
	Vector<XmlObject> adds = new Vector<XmlObject>();
	List<XmlObject> gets = new ArrayList<XmlObject>();
	Vector<Parameter> parameters = new Vector<Parameter>();
	org.xml.sax.helpers.AttributesImpl attrs = new org.xml.sax.helpers.AttributesImpl();
	
	private Class[] parameterTypes = new Class[]{String.class};
	private Object[] arguments = new Object[1];
	private Class c = null;
	
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) throws XMLBuildException {
		
		this.objectName = attrs.getValue("object");
		if (objectName == null) {
			return true;
		}
		
		XmlObject object = (XmlObject)symbolTableLookUp(objectName);
		this.object = object;
		
		this.attrs.setAttributes(attrs);
		this.attrs.removeAttribute(this.attrs.getIndex("object"));
		execute();
		
		return false;
	}
	
	/**
	 * Declare the object that will recieve properties and children
	 * @param object
	 */
	@attribute(value = "", required = false)
	public void setObject(String object) {
		//place holder for comment
	}

	
	/**
	 * Set the attributes of the target object and add children to the
	 * target object -- get any 'GET' operations first to get stuff
	 * off the web.
	 */
	@Override
	public void execute() throws XMLBuildException {
		if (object == null) {
			try {
				setAttributes();
			} catch (SAXParseException e) {
				e.printStackTrace();
				throw new XMLBuildException(e.getMessage());
			}
		}
		for (XmlObject get: gets) {
			// Any 'Data' items in the web page will be 'applied' to this Apply.
			// That makes the web page for data loading independent of the 
			// interface definition web page.
			get.execute();
		}
		try {
			setAttributes();
		} catch (SAXParseException e) {
			e.printStackTrace();
			return;
		}
		if (object != null)
			c = object.getClass();

		for (Parameter p:parameters) {
			String functionName = "set" + XmlParser.properName(p.getKey());
			arguments[0] = XmlParser.processMacros(this.getSymbolTable(), (String)p.getValue());
			try {
				Method m = c.getMethod(functionName, parameterTypes);
				m.invoke(object, arguments);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (XmlObject add:adds) {
			try {
				XmlParser.addToParent(object, add, null, true);
			} catch (SAXParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (object != null)
			object.execute();
	}
	
	public void setAttributes() throws XMLBuildException, SAXParseException {
		if (object == null) {
			this.object = (XmlObject)symbolTableLookUp(objectName);
		}
		if (this.object == null) {
			log.debug("can't find " + objectName);
			return;
		}
		XmlParser.setAttributes(attrs, this.object, getSymbolTable(), null);
	}
	
	@Override
	public void check() throws XMLBuildException {
		if (objectName == null) {
			throw new XMLBuildException("object must be specified");
		}
		if (object != null)
			object.check();
	}
	
	public void addGet(Get g) {
		gets.add(g);
	}
	
	public void addPost(Post g) {
		gets.add(g);
	}
	
	public void addParameter(Parameter p) {
		parameters.add(p);
	}
	
	public void addXmlObject(XmlObject p) {
		try {
			XmlParser.addToParent(object, p, null, false);
		} catch (SAXParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void addXmlObjectEnd(XmlObject p) throws XMLBuildException {
		adds.add(p);	
	}
}
