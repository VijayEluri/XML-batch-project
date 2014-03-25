package edu.bxml.format;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.javalobby.tnt.annotation.attribute;


/**
 * Load data from a flat file into a database table
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class XmlLoad extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(XmlLoad.class);
	public String delimit = "\t";
	boolean header = false;
	boolean quoteText = false;
	boolean printFieldValue = false;
	boolean printCreateTable = false;
	boolean upperCaseHeader = false;
	int stopAfterErrors = 0;
	String[] headers = null;
	File dir = null;
	String file = null;
	Vector<File>files = new Vector<File>();
	String archive = null;
//	Parse parse = null;
	java.sql.Connection c = null;
	Statement stmt = null;
	
	public String table = null;
	public HashMap<String, Field> fields = new HashMap<String, Field>();
	String connectionString = null;
	Connection connection = null;
	int fieldCount = 0;
	private boolean truncate = false;
	private int errCount = 0;
	private boolean binaryFile = false;
	private String archiveName = null;
	
	private String deleteFirstRaw = null;
	private String[] deleteFirst = null;
	private HashMap<String, LoadParameter> deleteFirstTypes = new HashMap<String, LoadParameter>();
	private String deleteFirstSQL = null;
	PreparedStatement pstmt = null;
	StringBuffer fieldList = new StringBuffer("");
	private String onerror = null;
	private String msg = "";
	
	File currentFile = null;
	
	/**
	 * Fields to be loaded from text file.  
	 * If the text file contains a header, the
	 * field name must match the name in the header.  The field name must also 
	 * match the a field's name in the destination database table.
	 * If the file has no header, then the data is associated by fields in order.
	 */
	@attribute(value = "", required = true)	
	public void addField(Field f) {
		fields.put("" + fieldCount++, f);
	}
	
	/**
	 * Name of the file when it is copied to the archive directory.  Can
	 * be used to include a date in the name to avoid overwrite.
	 * @param archiveName
	 */
	public void setArchiveName(String archiveName) {
		this.archiveName = archiveName;
	}
	
	/**
	 * A comma separated list of fields to delete on before a new
	 * record with the same values for those fields is loaded.
	 * Each 'name' must match a field child element with the name
	 * parameter set to a matching value.
	 * @param deleteFirst
	 */
	@attribute(value = "", required = false)	
	public void setDeleteFirst(String deleteFirst) throws XMLBuildException {
		if (deleteFirst.indexOf("--") > -1) {
			throw new XMLBuildException("Illegal characters in delete keys (--)", this);
		}
		deleteFirstRaw = deleteFirst;
		this.deleteFirst = deleteFirst.split(" *, *");
	}
	
	/**
	 * Fields to be loaded from text file.  
	 * If the text file contains a header, the
	 * fieldname must match the name in the header.  The field name must also 
	 * match the a field's name in the destination database table.
	 * If the file has no header, then the data is associated by fields in order.
	 */
	@attribute(value = "", required = false)	
	public void addImageField(ImageField f) {
		fields.put("" + fieldCount++, f);
	}
	
	/**
	 * Fields to be loaded from text file.  
	 * If the text file contains a header, the
	 * field name must match the name in the header.  The field name must also 
	 * match the a field's name in the destination database table.
	 * If the file has no header, then the data is associated by fields in order.
	 */
	@attribute(value = "", required = true)	
	public void addCharField(CharField f) {
		fields.put("" + fieldCount++, f);
	}
	
	/**
	 * Fields to be loaded from text file.  
	 * If the text file contains a header, the
	 * field name must match the name in the header.  The field name must also 
	 * match the a field's name in the destination database table.
	 * If the file has no header, then the data is associated by fields in order.
	 */
	@attribute(value = "", required = true)	
	public void addDateField(DateField f) {
		fields.put("" + fieldCount++, f);
	}
	
	/**
	 * Fields to be loaded from text file.  
	 * If the text file contains a header, the
	 * field name must match the name in the header.  The field name must also 
	 * match the a field's name in the destination database table.
	 * If the file has no header, then the data is associated by fields in order.
	 */
	@attribute(value = "", required = true)	
	public void addNumberField(NumberField f) {
		fields.put("" + fieldCount++, f);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Headers in source files will be matched as if they were uppercase.
	 * This is a good way to ignore the case the headers may come in as.
	 */
	@attribute(value = "", required = false)
	public void setUpperCaseHeader(Boolean upper)  {
		upperCaseHeader = upper;
	}
	public void setUpperCaseHeader(String upper) throws XMLBuildException {
		
		try {
			setUpperCaseHeader(Boolean.parseBoolean(upper));
		} catch (RuntimeException e) {
			throw new XMLBuildException(e.getMessage(), this);
		}
	}
	
	/**
	 * Sets the regular expressoin value of the field delimiter.  
	 * Uses the java 'String.split' command, so the regular expression follows
	 * that documentation.
	 */
	@attribute(value = "", required = false)	
	public void setDelimit(String delimit) {
		this.delimit = delimit; 
	}

	/**
	 * The load command won't automatically create a table that doesn't exist
	 * in the database, but if printCreateTable is true, then the SQL command 
	 * to create the table will be printed to standard error before the program 
	 * exits. 
	 */
	@attribute(value = "", required = false)
	public void setPrintCreateTable(Boolean printCreateTable) {
		this.printCreateTable = printCreateTable;
	}
	public void setPrintCreateTable(String printCreateTable) {
		setPrintCreateTable(Boolean.parseBoolean(printCreateTable));
	}
	
	/**
	 * Boolean determins if strings will be enclosed in quotes
	 */
	@attribute(value = "", required = false)	
	public void setQuoteText(Boolean quote) {
		this.quoteText = quote;
	}
	public void setQuoteText(String quote) {
		setQuoteText(Boolean.parseBoolean(quote));
	}
	
	/**
	 * Print the values parsed out of the file and what
	 * field they are associated with if true.  Useful for debugging.
	 */
	@attribute(value = "", required = false)	
	public void setPrintFieldValue(Boolean printFieldValue) {
		this.printFieldValue = printFieldValue;
	}
	public void setPrintFieldValue(String printFieldValue) {
		setPrintFieldValue(Boolean.parseBoolean(printFieldValue));
	}
	
	/**
	 * Stops after the given number of errors.
	 */
	@attribute(value = "", required = false)	
	public void setStopAfterErrors(Integer stopAfterErrors) {
		this.stopAfterErrors = stopAfterErrors;
	}
	public void setStopAfterErrors(String stopAfterErrors) {
		setStopAfterErrors(Integer.parseInt(stopAfterErrors));
	}
	
	/**
	 * Information on connection to database
	 */
	@attribute(value = "", required = true)	
	public void setConnection(String connection) {
		this.connectionString = connection;
	}
	
	public String getConnectionString() {
		return connectionString;
	}
	
	/**
	 * Truncate the table before loading new data only-if a 
	 * new data source file exists
	 */
	@attribute(value = "", required = false, defaultValue = "default value is false")
	public void setTruncateFirst(Boolean truncate) {
		this.truncate = truncate;
	}
	public void setTruncateFirst(String truncate) {
		setTruncateFirst(Boolean.parseBoolean(truncate));
	}
	
	/**
	 * Does the first line contain header information.
	 */
	@attribute(value = "", required = false, defaultValue = "default value is false")
	public void setHeader(Boolean isHeader) {
		this.header = isHeader;
	}
	public void setHeader(String isHeader) {
		setHeader(Boolean.parseBoolean(isHeader));
	}
	
	/**
	 * The directory where the file(s) to load can be found.
	 */
	@attribute(value = "", required = true)	
	public void setDir(String dir) {
		this.dir = new File(dir);
	}
	
	/**
	 * The name of the file containing data to load.  Can be a regular expression
	 * but it must match at least one file name.
	 */
	@attribute(value = "", required = true)	
	public void setFile(String file) {
		this.file = file;
	}
	
	public File getCurrentFile() {
		return currentFile;
	}
	
	/**
	 * The name of a directory relative to 'dir' where the file is moved 
	 * if the loaded file is saved for archive purposes.  Since this is a move 
	 * operation, the file will not be in the original location after the load.
	 */
	@attribute(value = "", required = false)	
	public void setArchive(String archive) {
		this.archive =  archive;
	}
	
	/**
	 * The name of a directory relative to 'dir' where the file is moved 
	 * if the loaded file is saved for archive purposes.  Since this is a move 
	 * operation, the file will not be in the original location after the load.
	 */
	@attribute(value = "", required = false)	
	public void setBinaryData(Boolean data) {
		this.binaryFile = data;
	}
	public void setBinaryData(String data) {
		setBinaryData(Boolean.parseBoolean(data));
	}
	
	/**
	 * Database table to recieve the data.
	 */
	@attribute(value = "", required = true)	
	public void setTable(String table) {
		this.table = table;
	}
	
	/**
	 * check that all the fields are set correctly, especially
	 * required fields.  Called when the end-tag of the 
	 * element has been reached and processed.
	 */
	public void check() throws XMLBuildException {

	}
	
	public void setOnError(String query) {
		this.onerror = query;
	}
	
	public void printCreateTable() {
		log.debug("create table [" + table + "] (");
		for (int i = 0; i < fieldCount; i++) {
			Field f = fields.get("" + i);
			System.err.print("[" + f.getFieldName() + "] ");
			log.debug(f.getSQLType() + ", ");
		}
		
		log.debug(")");
		System.exit(1);
	}
	
	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() throws XMLBuildException {
		ResultSet r = null;
		Query query = (Query) getParent();
		connection = query.findConnection(connectionString);
		//log.debug("connection  = " + this.connection);
		if (connection == null) {
			throw new XMLBuildException("connection not found", this);
		}
		
		try {
			c = connection.getConnection();
			stmt = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new XMLBuildException(e.getMessage(), this);
		}
		// Prepare delete query -- don't actually delete anything yet

		if (printCreateTable)
			printCreateTable();
		//
		// Delete First stuff goes here
		//
		if (deleteFirst != null) {
			log.debug("fields = " + fields);
			StringBuffer deleteFirstSelect = new StringBuffer("");
			StringBuffer deleteFirstSQL = new StringBuffer("delete from " + table + " where ");
			for (int i = 0; i < deleteFirst.length; i++) {
				Field f = fields.get(deleteFirst[i]);
				if (f == null) {
					throw new XMLBuildException("field " + deleteFirst[i] +
							" from deleteFirst does not exist", this);
				}
				deleteFirstSQL.append("[" + f.getFieldName() + "] = ? and ");
				deleteFirstSelect.append(", [" + f.getFieldName() + "]");
			}
			deleteFirstSQL.setLength(deleteFirstSQL.length()-4);
			this.deleteFirstSQL = deleteFirstSQL.toString();
			log.debug("Delete first SQL = " + this.deleteFirstSQL);	

			String delFirstQuery = "select " + deleteFirstSelect.substring(2) + 
				" from " + table + " where 1=0";
			
			try {
				r = stmt.executeQuery(delFirstQuery);
				ResultSetMetaData m = r.getMetaData();
				for (int i = 0; i < deleteFirst.length; i++) {
					deleteFirstTypes.put(m.getColumnName(i+1).toUpperCase(), 
							new LoadParameter(i+1, m.getColumnType(i+1)));
				}
				r.close();
				log.debug("del first sql = " + deleteFirstSQL);
				pstmt = c.prepareStatement(deleteFirstSQL.toString());
			}
			catch (SQLException s) {
				log.debug("query = " + delFirstQuery);
				throw new XMLBuildException("deleteFirst does not look like a valid comma separated list of fields in " + table, this);
			}
		}
		log.debug("deleteFirstTypes = " + deleteFirstTypes);
		if (truncate)
			truncateTable(stmt);
		//parse.execute();
		try {
			r.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			stmt.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			c.close();
		}
		catch (Exception e) {
			throw new XMLBuildException(e.getMessage(), this);
		}
	}
	
	public void truncateTable(Statement stmt) throws XMLBuildException {
		try {
			log.debug("TUNCATING TABLE");
			stmt.executeUpdate("truncate table " + table);
		} catch (SQLException sqle) {
			log.debug("Truncate Table:");
			sqle.printStackTrace();
			if (stopAfterErrors > 0 ) {
				errCount ++;
				if (errCount >= stopAfterErrors)
					System.exit(1);
			}
		}
	}
	
	private void loadDefaultHeaders() {
		log.debug("no header");
		headers = new String[fields.size()];
		for (int i = 0; i < fields.size();i++) {
			Field f = fields.get("" + i);
			if (f == null) {
				log.debug("field " + i + " is null");
			}
			else {
				headers[i] = f.getFieldName();
				fields.put(f.getFieldName(), f);
				fieldList.append(f.getFieldName()).append(",");
			}
		}
		log.debug("xxx fieldlist = " + fieldList);
	}

	public void loadBinaryData(Statement stmt) throws XMLBuildException {
		loadDefaultHeaders();
		
		log.debug("xx2 fieldlist = " + fieldList);
		fieldList.setLength(fieldList.length()-1);
		
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery("select " + fieldList + " from " + table);
			rs.moveToInsertRow();
			for (int i = 0; i < headers.length; i++) {
				String fieldName = headers[i];
				Field f = fields.get(headers[i]);
				if (f == null) 
					continue;
				
				if (Field.Types.IMAGE.equals(f.getType())) {
					ImageField imageF = (ImageField)f;
					File input = imageF.getFile();
					BufferedReader br = null;
					
					try {
						br = new BufferedReader(new InputStreamReader(new FileInputStream(
							input.getAbsoluteFile())));
						
					}
					catch (IOException fio) {
						throw new XMLBuildException (fio.getMessage(), this);
					}
				}
				else if (Field.Types.CHAR.equals(f.getType())) {
					rs.updateString(i+1, "yyy");
				}
			}
			rs.insertRow();
		}
		catch (SQLException sql) {
			sql.printStackTrace();
		}
		System.exit(0);
	}
	
//	public void loadIntoDatabase(HashMap<java.lang.String, StaticField> fields) 
//				throws XMLBuildException  {
//		
//		StringBuffer fieldListSql = new StringBuffer("");
//		StringBuffer sql = new StringBuffer("");
//
//		for (java.lang.String i: fields.keySet()) {
//			log.debug(i + ": " + fields.get(i).getValue());
//			fieldListSql.append(", [").append(i).append("]");
//			sql.append(", ").append(fields.get(i).getValue());
//		}
//		sql = new StringBuffer("insert into " + table).append("(" +
//				fieldListSql.substring(2) + ") values (").append(sql.substring(2)).append(")");
//			
//			try {
//				log.debug(sql.toString());
//				stmt.executeUpdate(sql.toString());
//			} catch (SQLException sqle) {
//				log.debug("sql = " + sql);
//				sqle.printStackTrace();
//				if (stopAfterErrors > 0 ) {
//					errCount ++;
//					if (errCount >= stopAfterErrors)
//						System.exit(1);
//				}
//			}
//		
//		/*
//		try {
//			int lineCount = 0;
//
//			String values[] = null;
//
//			while ((line = br.readLine()) != null) {
//				lineCount++;
//				// System.out.println(line);
//				line = line.replaceAll("'", "''");
//				values = split(line, delimit+"");
//				//log.debug(line);
//				StringBuffer sql = new StringBuffer("");
//				sql.append(" values (");
//				StringBuffer fieldListSql = new StringBuffer();
//				for (int i = 0; i < values.length; i++) {
//					String fieldName = null;
//					try {
//						fieldName = headers[i];
//						if (fieldName == null) 
//							continue;
//					} catch (RuntimeException e1) {
//						for (int j = 0; j < headers.length; j++) 
//							log.debug("headers[" + j + "] = " + headers[j]);
//						for (int j = 0; j < values.length; j++) 
//							log.debug("values[" + j + "] = " + values[j]);
//						log.debug("values = " + values);
//						e1.printStackTrace();
//						System.exit(1);
//					}
//					//log.debug("header[" + i + "] = '" + headers[i] + "'");
//					Field f = fields.get(headers[i]);
//					
//					if (f == null) {
//						log.debug("Fields = " + fields);
//						if (onerror != null) {
//							continue;
//						}
//						else
//							throw new XMLBuildException("no field defininition for header[" + i + "] " + headers[i]);
//					}
//					// System.out.println("type of " + i + " is " +
//					// strTypes[i]);
//					String value = values[i].trim(, this);
//					if (quoteText) {
//						if (value.startsWith("\"") && value.endsWith("\"")) {
//							value = value.substring(1,value.length()-1);
//						}
//					}
//					
//					try {
//						Parameter parm = null;
//						if ((parm = deleteFirstTypes.get(f.getFieldName())) != null) {
//							log.debug("setting delete parameter for " + f.getFieldName() + 
//									" value to " + value + " with params " + parm);
//							log.debug("SQL type = " + f.getSQLType());
//							if (f.getSQLType().equals("datetime")) {
//								String form = f.getDateFormat();
//								log.debug("form = " + form);
//								log.debug(f.insertFormat(value));
//								try {pstmt.setObject(parm.index, f.getObject(value), parm.type);}
//								catch (Exception s) {throw new XMLBuildException(s.getMessage(), this);};
//							}
//							else 
//								try {pstmt.setObject(parm.index, value, parm.type);}
//								catch (SQLException s) {throw new XMLBuildException(s.getMessage(), this);};
//						}
//						fieldListSql.append("[").append(f.getFieldName()).append("],");
//						sql.append(f.insertFormat(value));
//					} catch (NullPointerException e) {
//						e.printStackTrace();
//						throw new XMLBuildException("line: " + lineCount + " field: " +
//								fieldName + "  value: " + value + "   "+ e.getMessage());
//					} catch (XMLBuildException e) {
//						e.printStackTrace();
//						throw new XMLBuildException("line: " + lineCount + "  " + e.getMessage());
//					}
//					if (printFieldValue	)
//						log.debug(f.getFieldName() + "  value = " + value + "  insert value = " + f.insertFormat(value));
//				}
//				sql.setLength(sql.length() - 2);
//				sql.append(")");
//				fieldListSql.setLength(fieldListSql.length()-1);
//				if (deleteFirst != null && pstmt != null) {
//					try {
//						pstmt.execute();
//						pstmt.clearParameters();
//					}
//					catch (SQLException s) {
//						throw new XMLBuildException(s.getMessage());
//					}
//				}
//*/
//	}
	
	public String printHeaders(String[] headers) {
		StringBuffer ret = new StringBuffer();
		for (int i = 0; i < headers.length; i++){
			ret.append("[" + headers[i] + "], ");
		}
		ret.setLength(ret.length()-2);
		return ret.toString();
	}

	/**
	 * No-op function
	 */
	public void setText(String text) {

	}
	
	/**
	 * Split a string on the separator character.  Escaped separators (that are 
	 * preceded with a backslash (\)) are ignored.  If quoted text is allowed
	 * as in "Comma Separated Values" format, then separator characters within
	 * quoted text are ignored as well; but the quotes must surround the entire
	 * string, not just the separator character.
	 * @param line
	 * @param delimit
	 * @return
	 */
	public String[] split(String line, String delimit) {
		log.debug("line = " + line);
		String[] a = null;
		Vector<String> lines = new Vector<String>();
		line = line.replaceAll("\\\\" + delimit, "\\e");
		a = line.split(delimit);
		for (int i = 0; i < a.length; i++) {
			String thisLine = a[i];
			log.debug("thisLine[" + i + "] = " + thisLine);
			if (quoteText && thisLine.startsWith("\"")) {
				while (!thisLine.endsWith("\"") && i < a.length) {
					thisLine += "," + a[++i];
				}
				if (i == line.length()) {
					throw new RuntimeException("unterminated string quote");
				}
				thisLine = thisLine.substring(1, thisLine.length()-2);
				thisLine.replaceAll("\\e", delimit);
			}
			lines.add(thisLine);
		}
		a = new String[1];
		return lines.toArray(a);
	}
	
	public void handleError() throws XMLBuildException {
		Query query = (Query) getAncestorOfType(Query.class);
		Sql sql = query.getSql(onerror);
		if (sql == null) {
			throw new XMLBuildException("Could not find " + onerror, this);
		}
		Connection connection = (Connection)sql.getConnection();
		Statement stmt = null;
		java.sql.Connection c = null;
		try {
			c = connection.getConnection();
			stmt = c.createStatement();
		} catch (SQLException e1) {
			throw new XMLBuildException(e1.getMessage(), this);
		} catch (ClassNotFoundException e1) {
			throw new XMLBuildException(e1.getMessage(), this);
		}
		
		String strQuery = sql.getQuery().replace("{error}", "'" + msg + "'");
		log.debug("run query = " + strQuery);
		
		try {
			stmt.executeUpdate(strQuery);
		} catch (SQLException e) {
			throw new XMLBuildException(e.getMessage(), this);
		}
		finally {
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
		}
		
	}
}

class LoadParameter {
	int type = 0;
	int index = 0;
	
	LoadParameter(int index, int type) {
		this.type = type;
		this.index = index;
	}
	
	public String toString() {
		return "  index: " + index + "    type: " + type;
	}
}
