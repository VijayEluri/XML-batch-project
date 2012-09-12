package edu.bxml.generator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.javalobby.tnt.annotation.attribute;

import edu.bxml.io.FilterAJ;
/**
 * Specify the query that needs formatting
 * @author ritcheyg
 *
 */
@attribute(value = "", required = true)
public class Select extends FilterAJ {
	private static Log log = LogFactory.getLog(Select.class);
	List<Field> fields = new ArrayList<Field>();

	
	public List<Field> getFields() {
		return fields;
	}
	public void addField(Field field) {
		fields.add(field);
	}
}
