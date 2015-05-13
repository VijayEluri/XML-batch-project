package com.browsexml.core;


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Locator;

import com.javalobby.tnt.annotation.attribute;

import edu.bxml.io.FilterAJ;

public abstract class XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(XmlObjectImpl.class);
	protected String _uniqName = null;
	protected String typeName = null;
	protected XmlObject parent = null;
	ConcurrentHashMap symbolTable = null;
	private XmlParser parser = null;
	private String iff = "true";
	private HashMap<Method, String> variableParameters = new HashMap<Method, String>();
	private String value;
	
	@Override
	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		log.debug("set type name to " + typeName);
		this.typeName = typeName;
	}

	@Override
	public HashMap<Method, String> getVariableParameters() {
		return variableParameters;
	}
	
	@Override
	public void setParent(XmlObject parent) {
		this.parent = parent;
	}
	@Override
	public boolean isIff() throws XMLBuildException {
		String booleanValue = XmlParser.processMacros((Map)this.symbolTable, iff);
		log.debug("inside isIff  boolean string returned is " + booleanValue);
		Boolean ret = Boolean.parseBoolean(booleanValue);
		log.debug("so isiff returns " + ret);
		return ret;
	}
	/**
	 * @param iff
	 */
	@Override
	public void setIff(String iff) {
		this.iff = iff;
	}

	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		XmlObjectImpl.log = log;
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
	@Override
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) throws XMLBuildException {
		return true;
	}
	
	/**
	 * called after all attribute setter methods are called but before 
	 * the end-tag is encountered.
	 * @param parent
	 * @throws XMLBuildException
	 */
	@Override
	public void init(XmlObject parent) throws XMLBuildException {
		setName(_uniqName);
	}
	
	/**
	 * Called when object and all children should be completely defined
	 * on the XML end-tag 
	 * @throws XMLBuildException
	 */
	@Override
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
	@Override
	public abstract void execute() throws XMLBuildException;
	
	@Override
	public void setFromTextContent(String text) throws XMLBuildException {
		log.debug("set from text Context " + this.getName() + " to " + text);
		this.value = text;
	}
	
	protected int locator = 0;
	protected String source = null;
	
	@Override
	public void setLocator(Locator locator) {
		this.locator = locator.getLineNumber();
	}

	@Override
	public String getLineNumber() {
		return "line: " + locator;
	}

	/**
	 * @return the source
	 */
	@Override
	public String getSource() {
		if (source == null) 
			return "<Xml File is Standard Input>";
		return source;
	}

	/**
	 * @param source the source to set
	 */
	@Override
	public void setSource(String source) {
		this.source = source;
	}
	@Override
	public String getName() {
		return _uniqName;
	}
	@Override
	public void setSymbolTable(ConcurrentHashMap st) {
		this.symbolTable = st;
	}
	
	@Override
	public ConcurrentHashMap getSymbolTable() {
		return this.symbolTable;
	}
	
	@Override
	public Object symbolTableLookUp(String object) {
		return symbolTable.get(object);
	}
	

	/**
	 * Add Processing of fields to a pipeline
	 * 
	 * @param file
	 */
	@Override
	@attribute(value = "", required = false, defaultValue="name of class as an instance name (initial character not capitalized) plus a unique number")
	public void setName(String name) {
		if (name == null)
			this._uniqName = (proper(getClass().getSimpleName()) + Main.getID());
		else
			this._uniqName=(name);
	}
	@Override
	public XmlObject getParent() {
		return parent;
	}
	public int getInt(String strInt) throws XMLBuildException {
		try {
			return Integer.parseInt(strInt);
		}
		catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			throw new XMLBuildException(nfe.getMessage(), this);
		}
	}
	
	@Override
	public Boolean hasAncestorWithName(String name) {
		XmlObject x = this;
		while (x != null && (x=x.getParent()) != null) {
			if (x.getName().equals(name))
				return true;
		}
		return false;
	}
	
	@Override
	public <T> T getAncestorOfType(Class<T> type) {
		XmlObject x = this;
		if (type == null) {
			log.debug("type = " + type);
			return null;
		}
		String typeName = type.getName();
		log.trace("current x = " + x.getName());
		log.trace("current x.getParent() = " + x.getParent());
		while (x != null && (x=x.getParent()) != null) {
			
			if (x instanceof FilterAJ) {
				Object y = ((FilterAJ) x).getPojo();
				if (y != null && type.equals(y.getClass())) {
					return type.cast(((FilterAJ) x).getPojo());
				}
			}
			
			log.trace("current x = " + x.getName());
			if (type.equals(x.getClass()))
					break;
			if (type.equals(x.getClass().getGenericSuperclass()))
					break;
			log.debug("type name (" + typeName + " equals type " + x.getTypeName() + "  ???");
			if (typeName.equals(x.getTypeName())) {
				log.debug("YES");
				return type.cast( ((FilterAJ) x).getPojo());
			}
			log.debug("NO");
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
	
	@Override
	public void setParser(XmlParser x) {
		this.parser = x;
	}
	@Override
	public XmlParser getParser() {
		return parser;
	}
	
	@Override
	public String getValue() throws XMLBuildException {
		log.debug("get value = '" + value + "'");
		return value;
	}
	@Override
	public void setValue(String value) {
		log.debug("set value = '" + value + "'");
		this.value = value;
	}
}