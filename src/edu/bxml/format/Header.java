package edu.bxml.format;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.io.FilterAJ;

/**
 * Hold information about how to format the header
 * @author ritcheyg
 *
 */
@attribute(value = "", required = true)
public class Header extends XmlObject {
	private static Log log = LogFactory.getLog(Header.class);
	
	private StringBuffer outBuffer;
	boolean setToColumnNames = false;
	String separator = null;
	private Vector<Field> columns = new Vector<Field>();
	private Vector<FootField> footerFields = new Vector<FootField>();
	Query s = null;
	HashMap workingValues;
	
	
	/**
	 * Is the header to contain column names, true or false
	 * @param booleanValue
	 */
	@attribute(value = "", required = false, defaultValue="false")	
	public void setColumnsNames(Boolean booleanValue) {
		setToColumnNames = booleanValue;
	}
	public void setColumnsNames(String booleanValue) {
		setColumnsNames(Boolean.parseBoolean(booleanValue));
	}
	
	/**
	 * Get a Set containing the names of the headers.
	 * @return
	 */
	public HashSet getFields() {
		HashSet s = new HashSet();
		for (FootField f: footerFields) {
			Field i = f.subField;
			if (i != null) {
				s.add(i.getName());
			}
		}
		return s;
	}
	
	/**
	 * Get a Vector containing the Fields of the header.
	 * @return
	 */
	public Vector getFieldsVector() {
		Vector s = new Vector();
		for (FootField f: footerFields) {
			Field i = f.subField;
			if (i != null) {
				s.add(i);
			}
		}
		return s;
	}
	
	/**
	 * check that all the fields are set correctly, especially
	 * required fields.  Called when the end-tag of the 
	 * element has been reached and processed.
	 */
	public void check() throws XMLBuildException {
		
		if (separator == null) {
			throw new XMLBuildException("Delimiter has not been set");
		}
		
		/*if (setToColumnNames  && footerFields.size() != 0) {
			throw new XMLBuildException("Only one of ColumnNames and footer fields may be used");
		}*/
	}
	
	/**
	 * Add a field to the footer
	 */
	@attribute(value = "", required = false, defaultValue="May only be added if columnNames is false.")
	public void addFootField(FootField field) {
		footerFields.add(field);
	}
	
	/**
	 * Called after complete parsing of XML document
	 * to evaluate the document.
	 * @throws XMLBuildException 
	 */
	public void execute() throws XMLBuildException {
		log.debug("print header for working = " + workingValues);
		//s = (Query) getAncestorOfType(Query.class);
		log.debug(" s = " + s);
		outBuffer = new StringBuffer();
		String delimit = null;
		if (footerFields.size() > 0) {
			for (FootField field:footerFields){
				field.execute();  // All fields macro-replacement done by aspectj
				if (field.subField != null) {
					log.debug("subField not null");
					String name = field.subField.getName();
					Object object = workingValues.get(name);
					String thisOut = field.subField.format(object);
					outBuffer.append(thisOut);
				}
				else {
					log.debug("append");
					outBuffer.append(field.getValue());
				}
				delimit = field.getDelimit();
				if (delimit == null) {
					delimit = separator;
				}
				outBuffer.append(delimit);
			}
			if (outBuffer.length() > 1) {
				outBuffer.setLength(outBuffer.length()-delimit.length());
				outBuffer.append("\n");
				log.debug("output header as '" + outBuffer.toString() + "'");
			}
		}
		if (setToColumnNames) {
			for (Field column:columns){
				log.debug("colunn = " + column);
				if (!(column instanceof HiddenField)) {
					outBuffer.append(separator);
					outBuffer.append(column.getFieldName());
				}
			}
			if (outBuffer.length() > delimit.length()) {
				outBuffer.delete(0, delimit.length());
			}
		}
	}
	/**
	 * Retrieve the text that was contained inside the tag
	 */
	public void setText(String text) {
		
	}
	
	/**
	 * Set the header delimiter.  This should probably be 
	 * the same as the data delimiter.
	 * @param s
	 */
	public void setDelimit(String s) {
		if ("\\n".equals(s))
			separator = System.getProperty("line.separator");
		else
			separator = s;
	}
	
	/**
	 * Add a column name to the header
	 * @param name
	 */
	public void addColumn(Field field) {
		columns.add(field);
	}
	
	public void addCharField(CharField field) {
		footerFields.add(new FootField(field));
	}
	public void addNumberField(NumberField field) {
		footerFields.add(new FootField(field));
	}
	/**
	 * generate the header to standard out
	 *
	 */
	public String output(HashMap workingValues) throws XMLBuildException {
		this.workingValues = workingValues;
		execute();
		return outBuffer.toString();
	}
}
