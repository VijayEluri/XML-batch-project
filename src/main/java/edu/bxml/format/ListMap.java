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
public class ListMap extends FilterAJImpl implements FilterAJ, ListMapContainer {
	private static Log log = LogFactory.getLog(Select.class);

	public String queryName = null;

	private ResultSetMetaData md = null;

	private int count = 0; // count of records eventually printed to output

	private ArrayList<Map<String, String>> results;

	public ListMap() {

	}


	public void setMD(ResultSetMetaData md) {
		this.md = md;
	}

	/**
	 * The name of the query to use to populate the output file
	 * 
	 */
	@attribute(value = "", required = true)
	public void setQueryName(String name) {
		this.queryName = name;
	}


	public List<Map<String, String>> getListMap() throws XMLBuildException {
		try {
			results = new ArrayList<Map<String, String>>();
	
			Sql sql = (Sql) this.getSymbolTable().get(queryName);

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
				
				if (stmt.execute(sql.getQuery()) ) {
					log.debug("get result list");
					final ResultSet rs = stmt.getResultSet();

					// log.debug(sql.query);
	
					setMD(rs.getMetaData());
	
					while (rs.next()) {

						Map<String, String> m = new HashMap<String, String>();
						for (int i = 1; i <= md.getColumnCount(); i++) {
							Object value = rs.getObject(i);
							m.put(md.getColumnName(i), value + "");
						}
						results.add(m);
					}
				}
				else {
					Map<String, String> m = new HashMap<String, String>();
					String updateCount = Integer.toString(stmt.getUpdateCount());
					log.debug("update count = " + updateCount);
					m.put("updateCount", updateCount);
					results.add(m);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}

	/**
	 * return a count of all records sent to output
	 * 
	 * @return
	 */
	public int getCount() {
		return count;
	}
}
