package edu.bxml.generator;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.javalobby.tnt.annotation.attribute;

import edu.bxml.io.FilterAJ;
/**
 * Specify the query that needs formatting
 * @author ritcheyg
 *
 */
/**
 * @author geoffreyritchey
 *
 */
@attribute(value = "", required = true)
public class Field extends FilterAJ {
	private static Log log = LogFactory.getLog(Field.class);

	SqlAnalyzer analyzer;
	String fieldName;
	Boolean key = false;
	String label;
	Integer width = 20;
	Boolean visible = true;
	Boolean sortable = false;
	String javaType = "String";
	Boolean appendWildcard = false;	
	String separator = ",";
	
	public Map getFields() {
		HashMap fields = new HashMap();
		fields.put("label", label);
		fields.put("sortable", sortable.toString());
		fields.put("width", width.toString());
		fields.put("visible", visible.toString());
		fields.put("separator", separator);
		return fields;
	}
	
	
	public Boolean getAppendWildcard() {
		return appendWildcard;
	}
	public void setAppendWildcard(String appendWildcard) {
		setAppendWildcard(Boolean.parseBoolean(appendWildcard));
	}
	public void setAppendWildcard(Boolean appendWildcard) {
		this.appendWildcard = appendWildcard;
	}
	public String getSeparator() {
		return separator;
	}
	public void setSeparator(String separator) {
		this.separator = separator;
	}
	public String getJavaType() {
		return javaType;
	}
	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}
	public void setSortable(String sortable) {
		setSortable(Boolean.parseBoolean(sortable));
	}
	public Boolean getSortable() {
		return sortable;
	}
	public void setSortable(Boolean sortable) {
		this.sortable = sortable;
	}
	public Boolean getVisible() {
		return visible;
	}
	public void setVisible(String visible) {
		setVisible(Boolean.parseBoolean(visible));
	}
	public void setVisible(Boolean visible) {
		this.visible = visible;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public Boolean getKey() {
		return key;
	}
	public void setKey(Boolean key) {
		this.key = key;
	}
	public void setKey(String key) {
		setKey(Boolean.parseBoolean(key));
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Integer getWidth() {
		return width;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	public void setWidth(String width) {
		setWidth(Integer.parseInt(width));
	}
	
	
}
