package edu.bxml.io;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.format.Field;

/**
 * Process lines for Load that match a regular expression.
 * 
 * @param file
 */
@attribute(value = "", required = false)
public class Matches extends Filter {
	private static Log log = LogFactory.getLog(Matches.class);
	String delimit = null;
	boolean header = false;
	List<Field> fields = new ArrayList<Field>();
	String text = null;
	String[] fieldsArray = null;
	Pattern pattern = Pattern.compile("%\\{(\\w*)(:.*)?\\}");
	HashMap<String, FieldValues> values = null;
	Pattern expression = null;
	String line = null;
	
	public void execute() throws XMLBuildException {
		Load load = this.getAncestorOfType(Load.class);
		PrintStream out = new PrintStream(this.out);
		if (line == null) 
			return;
		fieldsArray = line.split(delimit);
		processLine(fieldsArray);
		Matcher m = pattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        while(m.find()){
        	String key = m.group(1);
        	FieldValues fv = values.get(key);
        	if (fv == null) {
        		if (load != null && load.getValues() != null) {
        			fv = load.getValues().get(key);
        		}
        		if (fv == null) {
        			throw new XMLBuildException("key '" + key + "' not declared.", this);
        		}
        	}
        	String value = fv.value;
        	String format = m.group(2);
        	if (format == null || value == null) {
        		value = fv.field.format(value);
        	}
        	else {
        		Field f = fv.field;
        		Object o = f.getObject(value);
        		String ff = f.getFormat();
        		f.setFormat(format.substring(1));
        		value = f.format(o);
        		f.setFormat(ff);
        	}
        	value = value.replace("$", "\\$");
            m.appendReplacement(sb, ""+ value);
        }
        m.appendTail(sb);
        out.print(sb);
	}
	
	public void setLine(String line) {
		this.line = line;
	}
	
	public void check() throws XMLBuildException {
		if (delimit == null) {
			delimit = ((Load)getParent()).delimit;
		}
		if (delimit == null) {
			throw new XMLBuildException("must set delimiter", this);
		}
	}
	public void setText(String text) {
		this.text = text;
	}
	
	/**
	 * regular expression to use as the delimiter between fields.  
	 * for a cvs file this will be ','.  For a pipe separated file it will be
	 * '\|' since pipe is a special character ('or' symbol) in regular expressions.
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void setDelimit(String delimit) {
		this.delimit = delimit;
	}
	
	/**
	 * indicate if there is a header line that names the fields.
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void setHeader(Boolean header) {
		this.header = header;
	}
	public void setHeader(String header) {
		setHeader(Boolean.parseBoolean(header));
	}
	
	public HashMap processLine(String[] fieldValues) throws XMLBuildException {
		int index = 0;
		values = new HashMap<String, FieldValues>();
		for (Field field:fields) {
			if (index > fieldValues.length)
				return values;
			if (field instanceof SkipField) {
				index += ((SkipField) field).getCount();
			}
			else {
				String name = field.getFieldName();
				if (field.getIndex() != null) {
					index = field.getIndex();
				}
				values.put(name, new FieldValues(field, fieldValues[index]));
				index++;
			}
		}
		return values;
	}
	
	/**
	 * determine how to process and name a field.
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addField(Field column) {
		fields.add(column);
	}
	
	public void setFromTextContent(String text) {
		log.debug("text = " + text);
		if (text == null) {
			new Exception("null text from context").printStackTrace();
		}
		else
			this.text = text;
	}
	
	public boolean matches(String line) {
		boolean ret = expression.matcher(line).find();
		this.line = line;
		return ret;
	}
	
	/**
	 * set the regular express used to determine if a line will be processed
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void setExpression(String expression) {
		this.expression = Pattern.compile(expression);
	}

	public HashMap<String, FieldValues> getValues() {
		return values;
	}

	public void setValues(HashMap<String, FieldValues> values) {
		this.values = values;
	}
	
}

class FieldValues {
	Field field = null;
	String value = null;
	public FieldValues(Field field, String value) {
		this.field = field;
		this.value = value;
	}
}
