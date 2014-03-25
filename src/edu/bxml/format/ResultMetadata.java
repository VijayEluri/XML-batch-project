package edu.bxml.format;

import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.io.Filter;
/**
 * Retrieve table meta-data in pipe-separated format.  The fields retrieved are
 * columnName, columnName as a Java variable name (all '_' removed and next character
 * upper-cased), type of data, size, and isNullable
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class ResultMetadata extends Filter  {
	private static Log log = LogFactory.getLog(ResultMetadata.class);
	String queryName = null;
	Query query = null;
	ResultSetMetaData rsMeta = null;
	String separator = "|";

	
	public ResultMetadata() {
	}


	@Override
	public void execute() throws XMLBuildException {
		query = (Query) this.getAncestorOfType(Query.class);
		log.debug(this.getClass().getName() + " executing...");
		Sql sql = query.getSql(queryName);
		
		Connection connection = null;
		ResultSet rs = null;
		Statement stmt = null;
		java.sql.Connection c = null;
		try {
			connection = sql.getConnection();
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new XMLBuildException("The connection '" + queryName + "' could not be found", this);
		}
		try {
			
			c = connection.getConnection();
			stmt = c.createStatement();
			rs = stmt.executeQuery(sql.getQuery());
			PrintStream out = new PrintStream(this.out);
            if (this.out == null && this.toFile==null) {
            	out = System.out;
            }
            
			rsMeta = rs.getMetaData();
			for (int i = 0; i < rsMeta.getColumnCount(); i++ ) {
				log.debug(" i = " + i);
				log.debug(" name = " + rsMeta.getColumnName(i+1));
				out.print(rsMeta.getColumnName(i+1));
				
				String tomatch = rsMeta.getColumnName(i+1);
				boolean matched = false;
				boolean uCase = tomatch.toUpperCase().equals(tomatch);
				
				// Uppercase letter after underscore to translate names like
				// FIRST_NAME to names like FirstName
				Pattern p = Pattern.compile("_(.)");
				Matcher m = p.matcher(tomatch.toLowerCase());
				 StringBuffer sb = new StringBuffer();
				 while (m.find()) {
					 matched = true;
				     m.appendReplacement(sb, m.group(1).toUpperCase());
				 }
				 m.appendTail(sb);
				 
				 if (matched || uCase)
					 out.print(separator + sb);
				 else
					 out.print(separator + tomatch);

				out.print(separator + rsMeta.getColumnTypeName(i+1));
				out.print(separator + rsMeta.getColumnDisplaySize(i+1));
				
				if (ResultSetMetaData.columnNoNulls == rsMeta.isNullable(i+1)) {
					out.print(separator + "not null");
				}
				else {
					out.print(separator + "null");
				}
				out.print(separator + (i+1));
				out.println("");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new XMLBuildException(e.getMessage(), this);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new XMLBuildException(e.getMessage(), this);
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

	public String getQueryName() {
		return queryName;
	}

	/**
	 * query to get meta data from
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}
}
