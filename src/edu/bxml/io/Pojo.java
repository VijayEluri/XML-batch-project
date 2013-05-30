package edu.bxml.io;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;

public class Pojo extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(Pojo.class);
	String className = null;
	String key = null;
	Class pojo = null;
	Class keyFieldType = null;
	
	@Override
	public void check() throws XMLBuildException {

	}

	@Override
	public void execute() throws XMLBuildException {
		Method keyField = null;
		try {
			log.debug("key field name = " + key);
			keyField = pojo.getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), 
					(java.lang.Class[])null);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {

			e.printStackTrace();
		}
		keyFieldType = keyField.getReturnType();
	}
	
	public void setClassName(String className) {
		this.className = className;
		try {
			pojo = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public Class getPojo() {
		return pojo;
	}

	public String getClassName() {
		return className;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKeyFieldType() {
		return keyFieldType.getSimpleName();
	}


}
