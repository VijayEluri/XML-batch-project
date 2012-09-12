package edu.bxml.generator;

import java.util.ArrayList;
import java.util.List;

import com.browsexml.core.XmlObject;

public class Interface extends XmlObject {
	String label;
	String operator;
	String width;
	String separator;
	
	
	public String getSeparator() {
		return separator;
	}
	public void setSeparator(String separator) {
		this.separator = separator;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	@Override
	public void check() {
		
	}
	@Override
	public void execute() {
		
	}
	List<Field> fields = new ArrayList<Field>();
	
	public List<Field> getFields() {
		return fields;
	}
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
	public void addField(Field field) {
		fields.add(field);
	}
}
