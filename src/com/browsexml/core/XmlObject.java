package com.browsexml.core;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;

public interface XmlObject {

	XmlObject getParent();
	public <T> T getAncestorOfType(Class<T> type);
	String getValue() throws XMLBuildException;
	void setValue(String value);
	void setParser(XmlParser x);
	XmlParser getParser();
	void setName(String name);
	void setTypeName(String typeName);
	HashMap<Method, String> getVariableParameters();
	void setParent(XmlObject parent);
	boolean isIff() throws XMLBuildException;
	void setIff(String iff);
	void setLocator(Locator locator);
	Locator getLocator();
	String getLineNumber();
	String getSource();
	void setSource(String source);
	String getName();
	String getTypeName();
	void setSymbolTable(HashMap st);
	HashMap getSymbolTable();
	Object symbolTableLookUp(String object);
	void execute() throws XMLBuildException;
	void init(XmlObject parent) throws XMLBuildException;
	boolean processRawAttributes(Attributes attrs);
	void check() throws XMLBuildException;
	void setFromTextContent(String text) throws XMLBuildException;
}
