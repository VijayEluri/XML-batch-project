package edu.bxml.format;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

/**
 * Execute SQL, usually a stored procedure.
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Execute extends XmlObject {
	public String mainSql = null;
	public String alternateSql = null;
	private Query query = null;
	private Sql sql = null;
	private String connectionString = null;
	File dir = new File(".");
	String file = null;
	Vector<File>files = new Vector<File>();
	Connection connection = null;

	/**
	 * let the parent identify itself to this object
	 */
	public void setParent(XmlObject parent) {
		query = (Query)parent;
	}

	/**
	 * check that all the fields are set correctly, especially required fields.
	 * Called when the end-tag of the element has been reached and processed.
	 */
	public void check() throws XMLBuildException {
		if (query == null) {
			throw new XMLBuildException("Parent query did not identify itself");
		}
		files.setSize(0);
		if (dir == null) 
			throw new XMLBuildException("dir not set");
		if (!dir.exists()){
			throw new XMLBuildException(dir.getAbsolutePath() + " does not exist");
		}
		if ((file == null)  && (mainSql == null))
			throw new XMLBuildException("Either file or sql must be set");
		if (file != null) { 
			File[] flist = dir.listFiles();
			for (int i = 0; i < flist.length; i++ ){
				String name = flist[i].getName();
				//System.err.println("file name = " + name);
				if (name.matches(file)) {
					System.err.println("adding " + name);
					files.add(flist[i]);
				}
			}

	
			if (connectionString == null) {
				throw new XMLBuildException("no connection");
			}
		}
	}

	/**
	 * The name of the sql object to execute.  This should usually by a 
	 * stored procedure.
	 */
	@attribute(value = "", required = true)
	public void setSql(String sql) {
		this.mainSql = sql;
	}
	
	/**
	 * Execute this alternate sql object if zero records are affected by 
	 * the primary 'sql' command.  May be the word 'exit' to terminate 
	 * program execution.
	 * 
	 */
	@attribute(value = "", required = false)
	public void setOnZero(String alternateSql) {
		this.alternateSql = alternateSql;
	}
	
	/**
	 * Information on connection to database
	 */
	@attribute(value = "", required = false, defaultValue="Connection sql is embedded in.")	
	public void setConnection(String connection) {
		this.connectionString = connection;
	}
	
	/**
	 * The name of the file containing sql commands to execute.  Can be a regular expression
	 * but it must match at least one file name.
	 */
	@attribute(value = "", required = false, defaultValue="Sql must be specified if the file is not.")	
	public void setFile(String file) {
		this.file = file;
	}
	
	/**
	 * The directory where the file(s) to execute can be found.
	 */
	@attribute(value = "", required = false, defaultValue = "Must be specified if neither file nor sql is specified.  Can be used with file.")	
	public void setDir(String dir) {
		this.dir = new File(dir);
	}

	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() throws XMLBuildException {
		System.err.println("EXECUTE ");
		if ((file != null && dir != null) && files.size() == 0 ){
			System.err.println(file + ": no matching files exist" + System.getProperty("line.separator") + "*** Error from object in XML at line " + this.getLocator().getLineNumber());
			return;
		}
		if (connectionString != null && files != null && files.size() > 0) {
			executeFiles();
		}

		sql = query.getSql(mainSql);
		if (sql == null) {
			throw new XMLBuildException("Could not find " + mainSql);
		}
		Connection connection = (Connection)sql.getConnection();
		Sql s = query.getSql(alternateSql);
		
		String alt = null;
		if (s != null) 
			alt = s.query;
		doExecute(connection, sql.query, alt);
	}
	
	public void doExecute(Connection connection, String query, String altQuery) 
			throws XMLBuildException {
		System.err.println("EXECUTE query=" + query);
		Statement stmt = null;
		try {
			//java.sql.Connection con = connection.getConnection();
			/* TODO put dataoutput into sub objects */
			stmt = connection.getStatement();//con.createStatement();
	
			System.err.println("queryX = " + query);
			int recordCount =  stmt.executeUpdate(query);
			System.err.println("record count = " + recordCount);
			System.err.println("alt = " + altQuery);
			if (recordCount == 0 && altQuery != null) {
				System.err.println("here");
				if (altQuery.trim().toLowerCase().equals("exit")) {
					System.err.println("exit called by XML object at " + this.getLocator().getLineNumber());
					System.err.flush();
					System.exit(0);
				}
				stmt.executeUpdate(altQuery);
			}
		} catch (SQLException sqle) {			
			sqle.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			throw new XMLBuildException(cnfe.getMessage());
		} finally {

		}
	}
	
	public void executeFiles() throws XMLBuildException {
		connection = query.findConnection(connectionString);
		//System.err.println("connection  = " + this.connection);
		if (connection == null) {
			throw new XMLBuildException("connection not found");
		}
		for (File f: files) {
			try {
				doExecute(connection, ReadSQL(f), null);
			}
			catch (XMLBuildException n) {
				System.err.println(f + " is a file that does not contain valid SQL");
				n.printStackTrace();
			}
		}
	}
	
	public String ReadSQL(File input) {
		BufferedReader br = null;
		String line = null;
		StringBuffer lineBuffer = new StringBuffer();
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					input.getAbsoluteFile())));
			while ((line = br.readLine()) != null) {
				lineBuffer.append(line).append(System.getProperty("line.separator"));
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
		return lineBuffer.toString();
	}

	/**
	 * No-op function
	 */
	public void setText(String text) {

	}
}
