package edu.bxml.format;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.javalobby.tnt.annotation.attribute;

/**
 * Represent how to format all the fields of a query.
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Exists extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(Exists.class);
	public String queryName = null;
	public Key key = null;
	private String trueCmd = null;
	private String falseCmd = null;
	
	Vector allFields = null;
	private Query query = null;
	private int recordCount = 0;  //record count before grouping
	
	public Exists() {
		
	}

	/**
	 * The name of the query to check for existance of data.
	 * 
	 */
	@attribute(value = "", required = true)
	public void setQueryName(String name) {
		this.queryName = name;
	}

	/**
	 * Do the command specified if results are returned by the query.  The 
	 * only command supported is 'exit'.
	 * @param falseCmd
	 */
	@attribute(value = "", required = false)
	public void setTrue(String trueCmd) {
		this.trueCmd = trueCmd;
	}
	
	/**
	 * Do the command specified if no results are returned by the query.  The 
	 * only command supported is 'exit'.
	 * @param falseCmd
	 */
	@attribute(value = "", required = false)
	public void setFalse(String falseCmd) {
		this.falseCmd = falseCmd;
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
	}

	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	@Override
	public void execute() throws XMLBuildException {
		java.sql.Connection c = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {
			Sql sql = query.getSql(queryName);
			
			Connection connection = null;
			try {
				connection = sql.getConnection();
			} catch (RuntimeException e) {
				e.printStackTrace();
				throw new XMLBuildException("The connection '" + queryName + "' could not be found");
			}
			log.debug("sql = " + sql.getQuery());
			//java.sql.Connection con = connection.getConnection();
			/* TODO put dataoutput into sub objects */
			c = connection.getConnection();
			stmt = c.createStatement();//con.createStatement();

			recordCount = 0;

			// log.debug(sql.query);

			try {
			rs = stmt.executeQuery(sql.getQuery());
			if (rs.next()) {
				recordCount++;
			}
			}
			catch (SQLException s) {
				
			}
			if (recordCount > 0) {
				if (trueCmd != null) {
					if (trueCmd.toLowerCase().trim().equals("exit")) {
						log.debug("Exit caused by XML object at line " + this.getLocator().getLineNumber());
						System.exit(0);
					}
				}
			}
			else {
				if (falseCmd != null) {
					if (falseCmd.toLowerCase().trim().equals("exit")) {
						log.debug("Exit caused by XML object at line " + this.getLocator().getLineNumber());
						System.exit(0);
					}
				}
			}
		} catch (ClassNotFoundException cnfe) {
			throw new XMLBuildException(cnfe.getMessage());
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw new XMLBuildException(sqle.getMessage());
		}
		finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
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
		}
	}
}
