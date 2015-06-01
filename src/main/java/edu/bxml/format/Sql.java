package edu.bxml.format;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;
/**
 * Specify the query that needs formatting
 * @author ritcheyg
 *
 */
@attribute(value = "", required = true)
public class Sql extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(Sql.class);

	private String query = "";
	
	public String getQuery() throws XMLBuildException {
		log.debug("sql 32  query = " + query);
		return XmlParser.processMacros(this.getSymbolTable(), XmlParser.replacePoundMacros(query));
	}

	public String getRawQuery() {
		return query;
	}

	public Connection getConnection() {
		return (Connection)this.getParent();
	}
	
	/**
	 * check that all the fields are set correctly, especially
	 * required fields.  Called when the end-tag of the 
	 * element has been reached and processed.
	 */
	public void check() throws XMLBuildException {
		if (! (this.getParent() instanceof Connection)) {
			throw new XMLBuildException("Parent of SQL is not a connection.", this);
		}
		log.debug("query = " + query);
	}
	/**
	 * Called after complete parsing of XML document
	 * to evaluate the document.
	 */
	public void execute() {
		
	}
	
	public List<Map<String, String>> getHashMap() {
		java.sql.Connection c = null;
		List<Map<String, String>> ret = new ArrayList<Map<String, String>>();
		try {
			c = this.getConnection().getConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		String strQuery = this.getQuery();
		Statement stmt = null;
		try {
			log.debug("sql = " + strQuery);
			stmt = c.createStatement();
			final ResultSet rs = stmt.executeQuery(strQuery);
			ResultSetMetaData md = rs.getMetaData();

			while (rs.next()) {
				Map<String, String> m = new HashMap<String, String>();
				for (int i = 1; i <= md.getColumnCount(); i++) {
					Object value = rs.getObject(i);
					m.put(md.getColumnName(i), value + "");
				}
				ret.add(m);
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
		return ret;
	}
	/**
	 * Retrieve the text that was contained inside the tag
	 */
	public void setText(String text) {
		if (text != null) 
			query = text;
	}
	public void setFromTextContent(String text) {
		if (text != null)
			query = text;
	}
}
