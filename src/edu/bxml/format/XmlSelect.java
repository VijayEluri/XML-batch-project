package edu.bxml.format;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;
/**
 * Represent how to format all the fields of a query.
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class XmlSelect extends XmlObject {
	private static Log log = LogFactory.getLog(XmlSelect.class);
	public String delimit = "\t";
	public Header header = new Header();
	public Footer footer = null;
	public String queryName = null;
	public Key key = null;
	private boolean groupsExist = false;
	private ResultSetMetaData md = null;
	Vector<Replace> replacements = new Vector<Replace>();
	private String concatSep = " ";
	private String root = null;
	private String record = null;
	private SimpleDateFormat outDateFormat = null;
	private String timezone = (new SimpleDateFormat("Z").format(new Date())).substring(0,3) + ":00";

	Vector allFields = null;
	private Query query = null;
	private int recordCount = 0;  //record count before grouping
	private int count = 0;		//count of records eventually printed to output 
	

	public XmlSelect() {
	}

	public void setDateFormat(String dateFormat) {
		outDateFormat = new SimpleDateFormat(dateFormat);
	}
	
	HashMap values = null;
	HashMap workingValues = null;
	String lastKey = null;
	
	String filename = null;
	private String archive = null;
	
	File outFile = null;
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
	
	public void setMD(ResultSetMetaData md) throws SQLException {
		this.md = md;
		int count = md.getColumnCount();
		for (int i = 1; i <= count; i++) {
			
		}
	}
	
	/**
	 * This will be the root element name in the XML output.
	 * @param root
	 */
	@attribute(value = "", required = true)
	public void setRoot(String root) {
		this.root = root;
	}

	/**
	 * This will be the name of each record element in the XML output.
	 * @param root
	 */
	@attribute(value = "", required = true)
	public void setRecord(String record) {
		this.record = record;
	}
	/**
	 * Sets the field delimiter. The delimiter must be a single character and
	 * should not be space.
	 * 
	 */
	@attribute(value = "", required = false, defaultValue = "defaults to a tab character")
	public void setDelimit(String delimit) throws XMLBuildException {
		log.debug(delimit + " = delmit" );
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
	 * Declare what to print as the header record
	 * 
	 */
	@attribute(value = "", required = true)
	public void addHeader(Header header) {
		this.header = header;
		header.setDelimit(delimit);
		log.debug("add header " + header);
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
		if (root == null) {
			throw new XMLBuildException("You must specify the name of the root.");
		}
		if (record == null) {
			throw new XMLBuildException("You must specify the name of each record.");
		}
		if (dir == null) {
			dir = new File(".");
		}
		if (!dir.exists()) {
			dir.mkdirs();
			log.debug("The output directory " + dir + " did not exist.  It has been created.");
		}
		if (filename != null) {
			outFile = new File(dir, filename);
			log.debug("file = " + outFile.getAbsolutePath());
		}
		if (queryName == null)
			throw new XMLBuildException("queryName can't be null");
		/*if (key == null) {// && groupsExist) {
			throw new XMLBuildException("no key defined");// but there is grouping");
		}*/

		//formatString = getFormatString();
		if (filename != null && filenameField != null) {
			throw new XMLBuildException("file name and field name field are both set");
		}
		if (archive != null & filenameField != null) {
			// Could keep a list of all files opened; then copy to 
			// archive all files in the list
			throw new XMLBuildException("you can't archive if you set filenameField");
		}
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
		log.debug("output set to " + this.filename);
	}
	
	/**
	 * Change the file where output is being directed.
	 * @param outFile
	 */
	public void setOutput(File outFile) {
		if (outFile != null) {
			try {
				System.out.flush();
				log.debug("out redirected to " + outFile);
				System.setOut ( new PrintStream(outFile) );
				count = 0;
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	/**
	 * Primarity for debugging to reset the output reguardless of other settings
	 * @param o
	 */
	public void setOutput(PrintStream o) {
		outFile = null;
		log.debug("set output to " + o);
		System.setOut(o);
	}

	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	@Override
	public void execute() throws XMLBuildException {
		
		log.debug("SELECT:");
		if (outFile != null) {
			setOutput(outFile);
		}
		try {
			Sql sql = query.getSql(queryName);
			
			Connection connection = null;
			ResultSet rs = null;
			Statement stmt = null;
			try {
				connection = sql.getConnection();
			} catch (RuntimeException e) {
				e.printStackTrace();
				throw new XMLBuildException("The connection '" + queryName + "' could not be found");
			}
			log.debug("sql = " + sql.query);
			//java.sql.Connection con = connection.getConnection();
			/* TODO put dataoutput into sub objects */
			stmt = connection.getStatement();//con.createStatement();

			recordCount = 0;

			// log.debug(sql.query);

			try {
			rs = stmt.executeQuery(sql.query);
			setMD(rs.getMetaData());
			log.debug("filename = " + filename);
			printHeader(workingValues);
			while (rs.next()) {
				recordCount++;
//log.debug("Record Count = " + recordCount);
				output(rs);
				// System.exit(0);
			}
			printFooter();
			}
			catch (SQLException s) {
				s.printStackTrace();
			}
		} catch (ClassNotFoundException cnfe) {
			throw new XMLBuildException(cnfe.getMessage());
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw new XMLBuildException(sqle.getMessage());
		}
		finally {
		}
	}

	/**
	 * Send a record of data to the currenct output file.  Change the 
	 * output file if that file is based on a field's value and that field's 
	 * value has changed; write a header or 
	 * footer if this is the first or last record to the current file.
	 * ZZ in the literal of a timeformat will produce -HH:mm time zone formatted info.
	 * @param rs
	 * @throws XMLBuildException
	 * @throws SQLException
	 */
	public void output(ResultSet rs) throws XMLBuildException, SQLException {
		System.out.println("<" + record + ">");
		try {
			for (int i = 1; i <= md.getColumnCount(); i++) {
				Object data = rs.getObject(i);
				log.debug(" class name = " + md.getColumnClassName(i));
				if (data == null) {
					System.out.println("<"+ md.getColumnName(i) + "/>");
				}
				else if (md.getColumnClassName(i).equals("java.sql.Timestamp")) {
					String format = outDateFormat.format(data);
					format = format.replace("ZZ", timezone);
					System.out.println("<"+ md.getColumnName(i) + ">"+ 
							format +
							"</" + md.getColumnName(i) + ">");
				}
				else if (md.getColumnClassName(i).equals("byte[]")) {
					byte[] x = (byte[]) data;
					System.out.println("<"+ md.getColumnName(i) + ">"+ x[0] +
							"</" + md.getColumnName(i) + ">");
				}
				else
					System.out.println("<"+ md.getColumnName(i) + ">"+ data +
						"</" + md.getColumnName(i) + ">");
			}
		} catch (SQLException e) {
			throw new XMLBuildException(e.getMessage());
		}
		System.out.println("</" + record + ">");
	}
	
	/**
	 * Print the footer.
	 * @throws XMLBuildException
	 */
	public void printFooter() throws XMLBuildException {
		System.out.println("</" + root + ">");
	}
	
	/**
	 * Print the header row.
	 * @throws SQLException
	 */
	public void printHeader(HashMap workingValues) throws XMLBuildException {
		System.out.println("<" + root + ">");
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
