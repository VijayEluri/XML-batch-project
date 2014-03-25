package edu.bxml.io;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;


public class Jsp extends Copy {
	private static Log log = LogFactory.getLog(Jsp.class);
	public List<Select> selectionList = new ArrayList<Select>();
	String command = null;
	String key = null;
	String refClass = null;
	String selection = null;
	Class refClassInstance = null;
	HashMap<String, Class> types = new HashMap<String, Class>();

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}
	
	public void addSelect(Select s) {
		selectionList.add(s);
	}

	public List<Select> getSelectionList() {
		return selectionList;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getRefClass() {
		return refClass;
	}

	public void setRefClass(String refClass) throws XMLBuildException {
		setRefClass(refClass, "");
	}
	
	public void setRefClass(String refClass, String path) throws XMLBuildException {
		Class refClassInstance = null;
		try {
			refClassInstance = Class.forName(refClass);
			if (this.refClassInstance == null)
				this.refClassInstance = refClassInstance;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		this.refClass = refClass;
		Field[] fields = refClassInstance.getDeclaredFields();
		for (Field field: fields) {
			log.debug("field = " + field.getName());
			log.debug("type = " + field.getType().getCanonicalName());
			if (field.getType().isPrimitive() || 
					field.getType().getCanonicalName().startsWith("java.")) {
				log.debug("PUT");
				types.put(path + field.getName(), field.getType());
			}
			else {
				log.debug("RECURSE");
				setRefClass(field.getType().getCanonicalName(), path + field.getType().getSimpleName() + ".");
			}
		}
		log.debug("Types = " + types);
		if ("".equals(path) && types.size() > 0)
			addSelections();
	}

	public String getSelection() {
		return selection;
	}

	public void setSelection(String selection) throws XMLBuildException {
		this.selection = selection;
		if (refClassInstance != null)
			addSelections();
	}
	
	public void addSelections() throws XMLBuildException{
		String[] selections = selection.split(" *, *");
		for (String sel: selections) {
			Class type = types.get(sel);
			if (type == null) {
				throw new XMLBuildException(sel + " not defined", this);
			}
			Select s = new Select();
			Date x;
			s.setField(sel);
			s.setType("string");
			if (type.getName().endsWith(".Date"))
				s.setType("date");
			selectionList.add(s);
		}
	}
}
