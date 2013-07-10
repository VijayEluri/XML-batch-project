package edu.bxml.format;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.io.FilterAJ;
import edu.bxml.io.FilterAJImpl;
/**
 * Represent how to format all the fields of a query.
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Select extends FilterAJImpl implements FilterAJ {
	private static Log log = LogFactory.getLog(Select.class);
	public String delimit = "\t";
	public Header header = null;
	public Footer footer = null;
	public String queryName = null;
	public Key key = null;
	private boolean groupsExist = false;
	private ResultSetMetaData md = null;
	Vector<Field> vctFields = new Vector<Field>();
	Vector<Replace> replacements = new Vector<Replace>();
	private String concatSep = " ";
	PrintStream localOut = null;

	public boolean quoteStrings = false;

	public boolean isQuoteStrings() {
		return quoteStrings;
	}
	
	public boolean getQuoteStrings() {
		return quoteStrings;
	}

	public void setQuoteStrings(boolean quoteStrings) {
		this.quoteStrings = quoteStrings;
	}
	
	public void setQuoteStrings(String quoteStrings) {
		try {
			this.quoteStrings = Boolean.parseBoolean(quoteStrings);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	Vector allFields = null;
	private Query query = null;
	private int recordCount = 0;  //record count before grouping
	private int count = 0;		//count of records eventually printed to output 
	
	public Select() {
		header = new Header();
		header.setParent(this);
	}

	HashMap values = null;
	HashMap workingValues = null;
	String lastKey = null;
	
	String filename = null;
	private String archive = null;
	
	File lastFileName = null;
	File dir = null;
	String filenameField = null;

	/**
	 * let the parent identify itself to this object
	 */
	/*public void setParent(XmlObject parent) {
		query = (Query)parent;
	}*/
	
	/**
	 * Set the directory where output files will be placed.
	 */
	@attribute(value = "", required = false, defaultValue = "Will go into current working directory if not specified")
	public void setDir(String dir) {
		this.dir = new File(dir);
	}
	
	/**
	 * Set the directory where a copy of the output files will be made, relative
	 * to the files' directory.
	 */
	@attribute(value = "", required = false, defaultValue = "Will go into current working directory if not specified")
	public void setArchive(String dir) {
		this.archive = dir;
	}
	
	public void setMD(ResultSetMetaData md) {
		this.md = md;
	}

	/**
	 * Sets the field delimiter. The delimiter must be a single character and
	 * should not be space.
	 * 
	 */
	@attribute(value = "", required = false, defaultValue = "defaults to a tab character")
	public void setDelimit(String delimit) throws XMLBuildException {
		this.delimit = Query.processDelimit(delimit);
	}

	/**
	 * The name of the query to use to populate the output file
	 * 
	 */
	@attribute(value = "", required = true)
	public void setQueryName(String name) {
		this.queryName = name;
	}

	/**
	 * Set the string to separate concatinated fields.
	 * 
	 */
	@attribute(value = "", required = false, defaultValue = "defaults to a single space.")
	public void setConcatSep(String sep) {
		this.concatSep = sep;
	}
	
	public void addImageField(ImageField field) {
		vctFields.add(field);
		header.addColumn(field);
	}
	/**
	 * Declare the field that will uniquely identify each record in the output file.
	 * 
	 */
	@attribute(value = "", required = false, defaultValue = "no key, but there must be a key defined if any"
			+ "field is a member of a group")
	public void addKey(Key key) {
		this.key = key;
	}
	
	/**
	 * Reqular expression replacement to be done to all character fields
	 * as the data comes out of the database.
	 * @param r
	 */
	@attribute(value = "", required = false)
	public void addReplace(edu.bxml.format.Replace r) {
		replacements.add(r);
	}
	
	/**
	 * Get the list of replacements to be made on all character fields.
	 * @return list of string replacements to be done
	 * across all character fields.
	 */
	public Vector<Replace> getReplace() {
		return this.replacements;
	}

	/**
	 * Specify that a field from the query is to be output and how that field is
	 * to be formatted.
	 * 
	 */
	@attribute(value = "", required = false, defaultValue = "no field's value displayed in the output file")
	public void addField(Field field) {
		vctFields.add(field);
		header.addColumn(field);
	}
	
	/**
	 * Total of all size values in the body of the select.
	 * @return
	 */
	public int getLength() {
		int length = 0;
		for (Field f:vctFields) {
			length += f.getSize();
		}
		return length;
	}

	/**
	 * Specify that a field from the query is to be output and how that field is
	 * to be formatted.
	 * 
	 */
	@attribute(value = "", required = false, defaultValue = "no field's value displayed in the output file")
	public void addHiddenField(HiddenField field) {
		vctFields.add(field);
		header.addColumn(field);
	}
	/**
	 * Specify that a field from the query is to be output and how that field is
	 * to be formatted.
	 * 
	 */
	@attribute(value = "", required = false, defaultValue = "no field's value displayed in the output file")
	public void addTextField(TextField field) {
		vctFields.add(field);
		header.addColumn(field);
	}
	
	/**
	 * Specify that a field from the query is to be output and how that field is
	 * to be formatted.
	 * 
	 */
	@attribute(value = "", required = false, defaultValue = "no field's value displayed in the output file")
	public void addNumberField(NumberField field) {
		vctFields.add(field);
		header.addColumn(field);
	}

	/**
	 * Specify that a field from the query is to be output and how that field is
	 * to be formatted.
	 * 
	 */
	@attribute(value = "", required = false, defaultValue = "no field's value displayed in the output file")
	public void addCharField(CharField field) {
		vctFields.add(field);
		header.addColumn(field);
	}
	
	/**
	 * Specify that a field from the query is to be output and how that field is
	 * to be formatted.
	 * 
	 */
	@attribute(value = "", required = false, defaultValue = "no field's value displayed in the output file")
	public void addDateField(DateField field) {
		vctFields.add(field);
		header.addColumn(field);
	}
	
	/**
	 * Declare what to print as the header record
	 * 
	 */
	@attribute(value = "", required = true)
	public void addHeader(Header header) {
		this.header = header;
		header.setDelimit(delimit);
	}

	/**
	 * Declare what is to print as the footer
	 */
	@attribute(value = "", required = true)
	public void addFooter(Footer footer) {
		this.footer = footer;
		footer.setDelimiter(delimit);	
		footer.setParent(this);
	}
	
	/**
	 * check that all the fields are set correctly, especially required fields.
	 * Called when the end-tag of the element has been reached and processed.
	 */
	@Override
	public void check() throws XMLBuildException {
		XmlObject x = this;
		while (x != null && !((x = x.getParent()) instanceof Query));
		query = (Query) x;
		log.debug(getName() + ": query = " + query);
		
		if (query == null) {
			throw new XMLBuildException("Parent query did not identify itself");
		}
		if (dir == null) {
			dir = new File(".");
		}
		if (!dir.exists()) {
			dir.mkdirs();
			log.debug("The output directory " + dir + " did not exist.  It has been created.");
		}
		if (queryName == null)
			throw new XMLBuildException("queryName can't be null");
		/*if (key == null) {// && groupsExist) {
			throw new XMLBuildException("no key defined");// but there is grouping");
		}*/

		if (filename == null && archive != null) {
			throw new XMLBuildException("you must set filename if archive is set");
		}
		//formatString = getFormatString();
		if (filename != null && filenameField != null) {
			throw new XMLBuildException("file name and field name field are both set");
		}
		if (archive != null && filenameField != null) {
			// Could keep a list of all files opened; then copy to 
			// archive all files in the list
			throw new XMLBuildException("you can't archive if you set filenameField");
		}
	}

	/**
	 * Create a set of field names from a Vector of field names.  
	 * @return
	 */
	public HashSet getFields() {
		//log.debug("vct Fields = " + vctFields);
		HashSet s = new HashSet();
		for (Field f : vctFields) {
			s.add(f.getFieldName());
		}
		return s;
	}
	
	/**
	 * The output will go to a file specified by the value in this field.
	 * The query should sort by this field first so that all the data
	 * intended for one file will be completely written before the file is closed.
	 */
	@attribute(value = "", required = false, defaultValue = "Will use standard output by default.  Only one of filename or filenameField can be specified.")
	public void setFilenameField(String filenameField) {
		this.filenameField = filenameField;
	}
	
	/**
	 * The output will go to a file specified.  Date formats can be embedded 
	 * in currly brackets {} to include the current date in the file name.
	 * filename="wbu_grades_{yyyymmdd}.csv" will evaluate to 'wbu_grades_20060801.csv'
	 * on August 1, 2006.
	 * @param filenameField
	 */
	@attribute(value = "", required = false, defaultValue = "Will use standard output by default.  Only one of filename or filenameField can be specified.")
	public void setFilename(String filename) {
		this.filename = filename;
		//log.debug("output set to " + this.filename);
	}
	
	public String getFilename() {
		return this.dir + "/" + this.filename;
	}
	
	public String getFile() {
		return this.filename;
	}
	
	public String getDir() {
		if (this.dir != null)
			return this.dir.getAbsolutePath();
		return ".";
	}
	
	public void setOutput(File outFile) {
		if (outFile != null) {
			try {
				//log.debug("out redirected to " + outFile);
				out = new PrintStream(outFile);
				count = 0;
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	public void setOutput(OutputStream outFile) {
		setLock(true);
		out = new PrintStream(outFile);
	}
	
	/**
	 * Primarity for debugging to reset the output reguardless of other settings
	 * @param o
	 */
//	public void setOutput(PrintStream o) {
//		outFile = null;
//		//log.debug("set output to " + o);
//		out = o;
//	}

	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	@Override
	public void execute() throws XMLBuildException {
		//log.debug("SELECT:");
		localOut = new PrintStream(this.getOut());

		File arch = null;
		

		if (!lock) {
			if (outFile != null) {
				setOutput(outFile);
			}
			else {
				setOutput(query.getOut());
			}
		}

		try {
			Sql sql = query.getSql(queryName);
			
			Connection connection = sql.getConnection();
			java.sql.Connection c = null;
			try {
				c = connection.getConnection();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			Statement stmt = null;
			try {
			log.debug("sql = " + sql.getQuery());
			stmt = c.createStatement();
			final ResultSet rs = stmt.executeQuery(sql.getQuery());

			
			//java.sql.Connection con = connection.getConnection();
			/* TODO put dataoutput into sub objects */
			
			recordCount = 0;

			// log.debug(sql.query);

			setMD(rs.getMetaData());
			checkHeader();
			
			log.debug("filename = " + filename);
			if (filenameField == null) {
				printHeader(localOut, workingValues);//PROBLEM LINE
			}
			while (rs.next()) {
				recordCount++;
				output(rs);
				// System.exit(0);
			}
			}
			catch (SQLException s) {
				s.printStackTrace();
			}
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			flushWorkingValues();
			printFooter();
			if (archive != null && localOut != System.out) {
				localOut.close();
				arch = new File(outFile.getParent() + "/" + archive, 
						outFile.getName());
				arch.createNewFile();
				Util.copy(outFile, arch);
			}
			else {
				log.warn("can't archive because output isn't going to a file");
			}
		} catch (ClassNotFoundException cnfe) {
			throw new XMLBuildException("class not found exception: " + cnfe.getMessage());
		} catch (IOException io) {
			io.printStackTrace();
			throw new XMLBuildException(arch.getAbsolutePath() + ": " + io.getMessage());
		}
		finally {
		}
	}

	/**
	 * Send a record of data to the currenct output file.  Change the 
	 * output file if that file is based on a field's value and that field's 
	 * value has changed; write a header or 
	 * footer if this is the first or last record to the current file.
	 * @param rs
	 * @throws XMLBuildException
	 * @throws SQLException
	 */
	public void output(ResultSet rs) throws XMLBuildException, SQLException {
		String thisKey = getResultSetToHashMap(rs);
		// log.debug("this key = " + thisKey);
		// log.debug("values = " + values);
		// log.debug("working values = " + workingValues);
		if (key == null || thisKey == null || lastKey == null || !lastKey.equals(thisKey)) {
			flushWorkingValues();
			// log.debug("WORKING VALUES NULL(this,last):" + thisKey +
			// " " + lastKey );
			lastKey = thisKey;
			workingValues = null;
			
			lastFileName = outFile;
		}
		mergeValues(values);
	}
	
	void flushWorkingValues() throws XMLBuildException {
		if (workingValues != null) {
			if (filenameField != null) {
				outFile = new File(dir, (String)workingValues.get(filenameField));
				if (outFile != null && !outFile.equals(lastFileName)) {
					if (lastFileName != null)
						printFooter();
					setOutput(outFile);
					//System.out.println("out output was " + lastFileName);
					printHeader(localOut, workingValues);
					//System.out.println("just printed header 2");
				}
			}
			String outLine = format(workingValues);
			if (outLine != null) {
				localOut.println(outLine);
				log.debug("printed (" + out + "): " + outLine);
			}
			else
				log.debug("out is null");
			count++;
		}
	}
	
	/**
	 * Print the footer.
	 * @throws XMLBuildException
	 * @throws SQLException 
	 */
	public void printFooter() throws XMLBuildException {
		if (footer == null) {
			return;
		}
		footer.execute();
		try {
			localOut.println(footer.output());
		} catch (SQLException e) {
			throw new XMLBuildException(e.getMessage());
		}
	}
	
	/**
	 * Make sure all the columns of a query have a specification of 
	 * how to format it.  Exit with a stack trace if something doesn't 
	 * have a format specification.
	 * @param rs
	 * @throws SQLException
	 */
	public void checkHeader() throws SQLException {

		
		try {
			HashSet s = getFields();
			HashSet s1 = header.getFields();
			log.debug(s.toString());
			log.debug(s1.toString());
			for (int i = 1; i <= md.getColumnCount(); i++) {
				String name = md.getColumnName(i);
				if (s.contains(name) || s1.contains(name)) {
					continue;
				}
				else {
					log.warn("column '" + name
							+ "'(" + i + ") was included in the select of the sql command but no format for it " +
									"is specified in the 'select' XML object.");
					//System.exit(1);
				}
			}
		} catch (SQLException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Print the header row.
	 * @throws SQLException
	 */
	public void printHeader(PrintStream out, HashMap workingValues) throws XMLBuildException {
		out.print(getHeader().output(workingValues));
	}

	/**
	 * Merge records that are specified as CONCAT on the key.
	 * @param v
	 */
	void mergeValues(HashMap v) {
		// log.debug("BEGIN mergeValues");
		if (workingValues == null) {
			// log.debug("NEW WORKING VALUES");
			workingValues = new HashMap();
		}
	
		if (allFields == null) {
			allFields = new Vector();
			allFields.addAll(vctFields);
			allFields.addAll(header.getFieldsVector());
		}
		for (Enumeration e = allFields.elements(); e.hasMoreElements();) {
			Field f = (Field) e.nextElement();

			Object wvValue = workingValues.get(f.getFieldName());
			if (f.group.equals(Field.Grouping.CONCAT)) {
				// log.debug(f.name + " CONCAT");
				if (wvValue == null) {
					wvValue = v.get(f.getFieldName());
					// log.debug("initialize to " + wvValue);
				} else {
					wvValue = workingValues.get(f.getFieldName()) + concatSep + v.get(f.getFieldName());
					// log.debug("Append to value of " + wvValue);
				}
			} else {
				wvValue = v.get(f.getFieldName());
			}
			// log.debug("wv = " + wvValue);
			workingValues.put(f.getFieldName(), wvValue);
		}
		// log.debug("END mergeValues");
	}
	
	/**
	 * There is a (?bug?) in the MS-SQL java driver that doesn't allow random
	 * access of the SQL fields. We copy the resultset to a hashmap to avoid
	 * problems.
	 * 
	 * @param rs
	 * @return the key value that uniquely identifies each row to be output
	 * @throws SQLException
	 */
	String getResultSetToHashMap(ResultSet rs) throws SQLException {
		values = new HashMap();
		// /log.debug("md columns = " + md.getColumnCount());
		for (int i = 1; i <= md.getColumnCount(); i++) {
			if ("nvarchar".equals(md.getColumnTypeName(i))) {
				// log.debug("replace all called on " + r);
				byte[] regBytes = rs.getBytes(i);
				try {
					String x = new String(regBytes, "UTF-16LE");
					log.debug("length1(" + i + ") = " + x.length());
					values.put(md.getColumnName(i),x);
					
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				//r = ((String) r).replaceAll("[\\s\\|]+", " ");
			}
			else {
				Object r = rs.getObject(i);
				values.put(md.getColumnName(i), r);
				//log.debug("value put(" + md.getColumnName(i) + ") = " + r);
			}
			
		}
		//log.debug("key = " + key.name);
		if (key == null) {
			return count + "";
		}
		Object value = values.get(key.name);
		if (value != null)
			return value.toString();
		return null;
	}

	/**
	 * Use a previously obtained format string to print the values of the
	 * current record retrieved from the database
	 * 
	 * @param values
	 *            the current record from the database
	 * @return
	 * @throws SQLException
	 */
	public String format(HashMap values) throws XMLBuildException {
		Vector toPrint = new Vector();
		String delimit = null;
		StringBuffer outline = new StringBuffer();
		for (Field f : vctFields) {
			Object value = values.get(f.getFieldName());
			//log.debug("length2 = " + ((String)value).length());
			if (value == null && "{skipline}".equals(f.defaultValue))
				return null;
			if (!(f instanceof HiddenField)) {
				delimit = f.getDelimit();
				if (delimit == null) 
					delimit = this.delimit;
				String formattedValue = f.format(value);
				log.debug(f.fieldName + " column = " + outline.length());
				outline.append(formattedValue).append(delimit);
				f.isColumnValid(outline.toString());
			}
		}
		outline.setLength(outline.length()-delimit.length());
		return outline.toString();
	}

	/**
	 * No-op function
	 */
	public void setText(String text) {

	}

	/**
	 * Get the header information
	 * 
	 * @return
	 */
	public Header getHeader() {
		return header;
	}

	/**
	 * return a count of all records sent to output
	 * @return
	 */
	public int getCount() {
		return count;
	}
}
