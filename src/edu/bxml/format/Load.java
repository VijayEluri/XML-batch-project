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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.io.FilterAJ;
/**
 * Load data from a flat file into a database table
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Load extends FilterAJ {
	private static Log log = LogFactory.getLog(Load.class);
	public String delimit = "\t";
	public boolean fixedWidth = false;
	boolean header = false;
	boolean quoteText = false;
	boolean printFieldValue = false;
	boolean printCreateTable = false;
	boolean upperCaseHeader = false;
	int stopAfterErrors = 0;
	String[] headers = null;
	File dir = null;
	String file = null;
	List<File>files = null;
	String archive = null;
	private String sqlOpenQuote = "[";
	private String sqlCloseQuote = "]";


	public String table = null;
	public HashMap<String, Field> fields = new HashMap<String, Field>();
	String connectionString = null;
	Connection connection = null;
	public String getSqlOpenQuote() {
		return sqlOpenQuote;
	}

	public void setSqlOpenQuote(String sqlOpenQuote) {
		this.sqlOpenQuote = sqlOpenQuote;
	}

	public String getSqlCloseQuote() {
		return sqlCloseQuote;
	}

	public void setSqlCloseQuote(String sqlCloseQuote) {
		this.sqlCloseQuote = sqlCloseQuote;
	}

	int fieldCount = 0;
	private boolean truncate = false;
	private int errCount = 0;
	private boolean binaryFile = false;
	private String archiveName = null;
	
	private String deleteFirstRaw = null;
	private String[] deleteFirst = null;
	private HashMap<String, Parameter> deleteFirstTypes = new HashMap<String, Parameter>();
	private String deleteFirstSQL = null;
	PreparedStatement pstmt = null;
	StringBuffer fieldList = new StringBuffer("");
	private String onerror = null;
	private String msg = "";
	
	boolean outputToFile = false;
	
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
	@attribute(value = "", required = false)	
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
			throw new XMLBuildException("Illegal characters in delete keys (--)");
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
	 * Headers in source files will be matched as if they were uppercase.
	 * This is a good way to ignore the case the headers may come in as.
	 */
	@attribute(value = "", required = false)	
	public void setUpperCaseHeader(Boolean upper) throws XMLBuildException {
		upperCaseHeader = upper;
	}
	public void setUpperCaseHeader(String upper) throws XMLBuildException {
		
		try {
			setUpperCaseHeader(Boolean.parseBoolean(upper));
		} catch (RuntimeException e) {
			throw new XMLBuildException(e.getMessage());
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
		setStopAfterErrors(stopAfterErrors);
	}
	public void setStopAfterErrors(String stopAfterErrors) {
		this.stopAfterErrors = Integer.parseInt(stopAfterErrors);
	}
	
	/**
	 * Information on connection to database
	 */
	@attribute(value = "", required = true)	
	public void setConnection(String connection) {
		this.connectionString = connection;
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
	 * Output goes to a file if true
	 */
	@attribute(value = "", required = false, defaultValue = "false")
	public void setOutputToFile(Boolean outputToFile) {
		this.outputToFile = outputToFile;
	}
	public void setOutputToFile(String outputToFile) {
		setOutputToFile(Boolean.parseBoolean(outputToFile));
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
		this.binaryFile =  Boolean.parseBoolean(data);
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
		files = null;
		if (dir == null) 
			throw new XMLBuildException("dir not set");
		if (!dir.exists()){
			throw new XMLBuildException(dir.getAbsolutePath() + " does not exist");
		}
		if (file == null) 
			throw new XMLBuildException("file not set");
		
		if (outputToFile) 
			return;

		if (connectionString == null) {
			throw new XMLBuildException("no connection");
		}
		if (table == null) {
			throw new XMLBuildException("no table");
		}
	}
	
	public void setOnError(String query) {
		this.onerror = query;
	}
	
	public void printCreateTable() {
		System.err.println("create table [" + table + "] (");
		for (int i = 0; i < fieldCount; i++) {
			Field f = fields.get("" + i);
			System.err.print("[" + f.getFieldName() + "] ");
			System.err.println(f.getSQLType() + ", ");
		}
		
		System.err.println(")");
		System.exit(1);
	}
	
	public List<File> getFileList() throws XMLBuildException  {
		List<File> files = new ArrayList<File>();
		File[] flist = dir.listFiles();
		for (int i = 0; i < flist.length; i++ ){
			String name = flist[i].getName();
			log.debug("file name = " + name);
			if (name.matches(file)) {
				log.debug("adding " + name);
				files.add(flist[i]);
			}
		}
		if (files.size() == 0 )
			throw new XMLBuildException("no files to load");
		return files;
	}
	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() throws XMLBuildException {
		log.debug("execute");
		files = getFileList();

		Query query = (Query) getParent();
		connection = query.findConnection(connectionString);
		//log.debug("connection  = " + this.connection);
		if (connection == null) {
			throw new XMLBuildException("connection not found");
		}
		java.sql.Connection c = null;
		Statement stmt = null;
		try {
			c = connection.getConnection();
			stmt = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new XMLBuildException(e.getMessage());
		}
		// Prepare delete query -- don't actually delete anything yet

		if (printCreateTable)
			printCreateTable();
		for (File currentFile: files) {
			if (truncate) {
				truncateTable(stmt);
			}
			truncate = false;
			this.currentFile = currentFile;
			if (binaryFile) 
				loadBinaryData(stmt);
			else
				loadIntoDatabase(c, currentFile, stmt);

			if (archive != null) {
				Util.move(currentFile, archive, archiveName, true);
			}
			if (onerror != null && !msg.equals("")) {
				handleError();
			}
			msg = "";
		}
		try {
			connection.close();
		}
		catch (Exception e) {
			throw new XMLBuildException(e.getMessage());
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
						throw new XMLBuildException (fio.getMessage());
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
	
	public void loadIntoDatabase(java.sql.Connection c, 
			File input, Statement stmt) throws XMLBuildException  {
		log.debug("load " + input.getAbsolutePath());
		BufferedReader br = null;

		try {
			Date createDate = new Date(input.lastModified());
			log.debug("time stamp on file is " + createDate);
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					input.getAbsoluteFile())));
			String line = "";
			int lineCount = 0;
			//log.debug("header = " + header);
			if (header) {
				//log.debug("in header");
				lineCount++;
				line = br.readLine();
				if (upperCaseHeader) 
					line = line.toUpperCase();
				log.debug("header line = " + line); // Header
				if (line == null) {
					log.debug("empty file");
					return;
				}
				log.debug("delimit = '" + delimit + "'");
				if (fixedWidth) 
					headers = splitFixedWidth(line);
				else {
					if (delimit.equals("\\|"))
						headers = line.split("\\|");
					else
						headers = line.split(delimit);
				}
				int size = fields.size();
				for (int i = 0; i < size; i++) {
					Field f = fields.get(""+i);
					if (f == null) {
						log.debug("field " + i + " is null");
					}
					else {
						//log.debug("PUT field " + i + " as '" + f.getLabelName() + "'");
						fields.put(f.getLabelName(), f);
						log.debug("adding header " + f.getFieldName());
					}
				}
			}
			else {
				loadDefaultHeaders();
			}
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
								" from deleteFirst does not exist");
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
					ResultSet r = stmt.executeQuery(delFirstQuery);
					ResultSetMetaData m = r.getMetaData();
					for (int i = 0; i < deleteFirst.length; i++) {
						deleteFirstTypes.put(m.getColumnName(i+1).toUpperCase(), 
								new Parameter(i+1, m.getColumnType(i+1)));
					}
					r.close();
					log.debug("del first sql = " + deleteFirstSQL);
					pstmt = c.prepareStatement(deleteFirstSQL.toString());
				}
				catch (SQLException s) {
					log.debug("query = " + delFirstQuery);
					throw new XMLBuildException("deleteFirst does not look like a valid comma separated list of fields in " + table);
				}
			}
			log.debug("deleteFirstTypes = " + deleteFirstTypes);
			
			
			if (onerror != null) {
				for (int i = 0; i < headers.length; i++) {
					if (null == fields.get(headers[i])) {
						msg += "Field " + headers[i] + " not loaded.  ";
					}
				}
			}
			
			String values[] = null;

			while ((line = br.readLine()) != null) {
				lineCount++;
				
				System.out.println(line);
				if (fixedWidth) {
					log.debug("fixedWidth");
					values = splitFixedWidth(line);
				}
				else {
					log.debug("NOT fixedWidth");
					line = line.replaceAll("'", "''");
					values = split(line, delimit+"");
				}
				//log.debug(line);
				StringBuffer sql = new StringBuffer("");
				sql.append(" values (");
				StringBuffer fieldListSql = new StringBuffer();
				for (int i = 0; i < values.length; i++) {
					
					String fieldName = null;
					try {
						if (i >= headers.length) 
							continue;
						fieldName = headers[i];
						log.debug(fieldName + "  value [ " + i + "] = " + values[i]);
						if (fieldName == null) 
							continue;
					} catch (RuntimeException e1) {
						for (int j = 0; j < headers.length; j++) 
							log.debug("headers[" + j + "] = " + headers[j]);
						for (int j = 0; j < values.length; j++) 
							log.debug("values[" + j + "] = " + values[j]);
						log.debug("values = " + values);
						e1.printStackTrace();
						System.exit(1);
					}
					//log.debug("header[" + i + "] = '" + headers[i] + "'");
					Field f = fields.get(headers[i]);
					
					if (f == null) {
						log.debug("Fields = " + fields);
						if (onerror != null) {
							continue;
						}
						else
							throw new XMLBuildException("no field defininition for header[" + i + "] " + headers[i]);
					}
					// System.out.println("type of " + i + " is " +
					// strTypes[i]);
					String value = values[i].trim();
					if (quoteText) {
						if (value.startsWith("\"") && value.endsWith("\"")) {
							value = value.substring(1,value.length()-1);
						}
					}
					
					try {
						Parameter parm = null;
						if ((parm = deleteFirstTypes.get(f.getFieldName())) != null) {
							log.debug("setting delete parameter for " + f.getFieldName() + 
									" value to " + value + " with params " + parm);
							log.debug("SQL type = " + f.getSQLType());
							if (f.getSQLType().equals("datetime")) {
								String form = f.getFormat();
								log.debug("form = " + form);
								log.debug(f.insertFormat(value));
								try {pstmt.setObject(parm.index, f.getObject(value), parm.type);}
								catch (Exception s) {throw new XMLBuildException(s.getMessage());};
							}
							else 
								try {pstmt.setObject(parm.index, value, parm.type);}
								catch (SQLException s) {throw new XMLBuildException(s.getMessage());};
						}
						
						fieldListSql.append(sqlOpenQuote).append(f.getFieldName()).append(sqlCloseQuote).append(",");
						sql.append(f.insertFormat(value));
					} catch (NullPointerException e) {
						e.printStackTrace();
						throw new XMLBuildException("line: " + lineCount + " field: " +
								fieldName + "  value: " + value + "   "+ e.getMessage());
					} catch (XMLBuildException e) {
						e.printStackTrace();
						throw new XMLBuildException("line: " + lineCount + "  " + e.getMessage());
					}
					if (printFieldValue	)
						log.debug(f.getFieldName() + "  value = " + value + "  insert value = " + f.insertFormat(value));
				}
				sql.setLength(sql.length() - 2);
				sql.append(")");
				fieldListSql.setLength(fieldListSql.length()-1);
				if (deleteFirst != null && pstmt != null) {
					try {
						pstmt.execute();
						pstmt.clearParameters();
					}
					catch (SQLException s) {
						throw new XMLBuildException(s.getMessage());
					}
				}
				sql = new StringBuffer("insert into " + table).
					append("(" + fieldListSql + ") ").append(sql);
				try {
					log.debug(sql.toString());
					stmt.executeUpdate(sql.toString());
				} catch (SQLException sqle) {
					log.debug("sql = " + sql);
					sqle.printStackTrace();
					if (stopAfterErrors > 0 ) {
						errCount ++;
						if (errCount >= stopAfterErrors)
							System.exit(1);
					}
				}
			}
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
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
	
	public String[] splitFixedWidth(String line) {
		log.trace("line = " + line);
		String[] a = null;
		Vector<String> lines = new Vector<String>();
		int beginIndex = 0;
		for (int i = 0; i < fieldCount; i++ ){
			Field f = fields.get("" + i);
			log.trace("begin index = " + beginIndex);
			log.trace("field = " + f.getName());
			
			String data = line.substring(beginIndex, beginIndex+f.getSize());
			log.trace("data = " + data);
			beginIndex += f.getSize();
			data = data.replaceAll("'", "''");
			lines.add(data);
		}
		return lines.toArray(new String[lines.size()]); 
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
		log.trace("line = " + line);
		String[] a = null;
		Vector<String> lines = new Vector<String>();
		line = line.replaceAll("\\\\" + delimit, "\\e");
		a = line.split(delimit);
		for (int i = 0; i < a.length; i++) {
			String thisLine = a[i];
			log.trace("thisLine[" + i + "] = " + thisLine);
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
	
	public String toString() {
		return dir + File.separator + file;
	}
	
	public void handleError() throws XMLBuildException {
		Query query = (Query) getAncestorOfType(Query.class);
		Sql sql = query.getSql(onerror);
		if (sql == null) {
			throw new XMLBuildException("Could not find " + onerror);
		}
		Connection connection = (Connection)sql.getConnection();
		Statement stmt = null;
		try {
			stmt = connection.getStatement();
		} catch (SQLException e1) {
			throw new XMLBuildException(e1.getMessage());
		} catch (ClassNotFoundException e1) {
			throw new XMLBuildException(e1.getMessage());
		}
		
		String strQuery = sql.getQuery().replace("{error}", "'" + msg + "'");
		log.debug("run query = " + strQuery);
		
		try {
			stmt.executeUpdate(strQuery);
		} catch (SQLException e) {
			throw new XMLBuildException(e.getMessage());
		}
		finally {

		}
		
	}

	public boolean isFixedWidth() {
		return fixedWidth;
	}

	public void setFixedWidth(String fixedWidth) {
		log.debug("FIXED WIDTH set to " + fixedWidth);
		this.fixedWidth = Boolean.parseBoolean(fixedWidth);
	}
}

class Parameter {
	int type = 0;
	int index = 0;
	
	Parameter(int index, int type) {
		this.type = type;
		this.index = index;
	}
	
	public String toString() {
		return "  index: " + index + "    type: " + type;
	}
}
