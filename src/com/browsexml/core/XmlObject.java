package com.browsexml.core;


import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Locator;

import com.javalobby.tnt.annotation.attribute;

public abstract class XmlObject {
	private static Log log = LogFactory.getLog(XmlObject.class);
	protected String _uniqName = null;
	protected XmlObject parent = null;
	HashMap symbolTable = null;
	private XmlParser parser = null;
	private String iff = "true";
	private HashMap<Method, String> variableParameters = new HashMap<Method, String>();
	private String value;
	
	public HashMap<Method, String> getVariableParameters() {
		return variableParameters;
	}
	
	public void setParent(XmlObject parent) {
		this.parent = parent;
	}
	public boolean isIff() throws XMLBuildException {
		Boolean ret = Boolean.parseBoolean(XmlParser.processMacros((HashMap)this.symbolTable, iff));
		return ret;
	}
	/**
	 * @param iff
	 */
	public void setIff(String iff) {
		this.iff = iff;
	}
	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		XmlObject.log = log;
	}

	/**
	 * Called just after object created with all attribute and
	 * before any macro substitution.  Use this to obtain any 
	 * attributes that are needed before the call to init or any
	 * other setter functions.  This call was created for SWT GUI
	 * because the attribute 'style' was needed to create the GUI object;
	 * with that GUI object existing, the other attributes could be 
	 * applied directly to the GUI object.
	 * @param attrs
	 */
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) throws XMLBuildException {
		return true;
	}
	
	/**
	 * called after all attribute setter methods are called but before 
	 * the end-tag is encountered.
	 * @param parent
	 * @throws XMLBuildException
	 */
	public void init(XmlObject parent) throws XMLBuildException {
		setName(_uniqName);
	}
	
	/**
	 * Called when object and all children should be completely defined
	 * on the XML end-tag 
	 * @throws XMLBuildException
	 */
	public abstract void check() throws XMLBuildException;
	/**
	 * Only called automatically on the root object when XML document
	 * has been completely processed.  It is up to the root to call execute
	 * on any of the children either directly or indirectly.  In past experience
	 * the parent usually calls execute on his children; the ultimate parent
	 * being the root object.  In a GUI application, listener objects may call 
	 * execute.
	 * @throws XMLBuildException
	 */
	public abstract void execute() throws XMLBuildException;
	public void setFromTextContent(String text) throws XMLBuildException {
		log.debug("set from text Context " + this.getName() + " to " + text);
		this.value = text;
	}
	
	protected Locator locator = null;
	protected String source = null;
	
	public void setLocator(Locator locator) {
		this.locator = locator;
	}
	public Locator getLocator() {
		return this.locator;
	}
	public String getLineNumber() {
		return "" + this.locator.getLineNumber();
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}
	public String getName() {
		return _uniqName;
	}
	public void setSymbolTable(HashMap st) {
		this.symbolTable = st;
	}
	
	public HashMap getSymbolTable() {
		return this.symbolTable;
	}
	
	public Object symbolTableLookUp(String object) {
		return symbolTable.get(object);
	}
	

	/**
	 * Add Processing of fields to a pipeline
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false, defaultValue="name of class as an instance name (initial character not capitalized) plus a unique number")
	public void setName(String name) {
		if (name == null)
			this._uniqName = (proper(getClass().getSimpleName()) + Main.getID());
		else
			this._uniqName=(name);
	}
	public XmlObject getParent() {
		return parent;
	}
	public int getInt(String strInt) throws XMLBuildException {
		try {
			return Integer.parseInt(strInt);
		}
		catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			throw new XMLBuildException(nfe.getMessage());
		}
	}
	public <T> T getAncestorOfType(Class<T> type) {
		XmlObject x = this;
		if (type == null) {
			log.debug("type = " + type);
			return null;
		}
		log.trace("current x = " + x.getName());
		log.trace("current x.getParent() = " + x.getParent());
		while (x != null && (x=x.getParent()) != null) {
			log.trace("current x = " + x.getName());
			if (type.equals(x.getClass()))
					break;
			if (type.equals(x.getClass().getGenericSuperclass()))
					break;
		}
		return type.cast(x);
	}
	
	 /* Return a name with the first character lower case. This is used for
	 * source code generation to convert a class name into part of a default
	 * variable name.
	 * 
	 * @param name
	 *            a class name
	 * @return
	 */
	public static String proper(String name) {
		return name.substring(0,1).toLowerCase() + 
			name.substring(1);
	}
	
	public static String uname(String name) {
		return name.substring(0,1).toUpperCase() + 
			name.substring(1);
	}
	
	public void setParser(XmlParser x) {
		this.parser = x;
	}
	
	public XmlParser getParser() {
		return parser;
	}
	
	public String getValue() throws XMLBuildException {
		log.debug("get value = '" + value + "'");
		return value;
	}
	
	public void setValue(String value) {
		log.debug("set value = '" + value + "'");
		this.value = value;
	}
}