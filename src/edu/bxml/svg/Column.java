package edu.bxml.svg;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;

public class Column extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(Column.class);
	String id = null;
	public enum Type {STRING, DATE, NUMBER};
	private Type type = Type.STRING;
	private boolean isPrimaryKey = false;
	
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
		return true;
	}
	
	public Column() {
		
	}

	@Override
	public void execute() throws XMLBuildException {

	}
	public void check() throws XMLBuildException {
		
		
	}
	
	public void setId(String name) {
		log.debug("column name = " + name);
		this.id = name.toString();
	}
	
	public void setType(String type) {
		this.type = Type.valueOf(type.toUpperCase());
	}
	
	public Type getType() {
		return type;
	}

	public String getId() {
		return id;
	}
	
	public void setPrimaryKey(Boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}
	public void setPrimaryKey(String isPrimaryKey) {
		setPrimaryKey(Boolean.parseBoolean(isPrimaryKey));
	}
}
