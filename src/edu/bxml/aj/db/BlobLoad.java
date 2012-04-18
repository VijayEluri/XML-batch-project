package edu.bxml.aj.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.aj.format.Query;
import edu.bxml.format.Connection;
import edu.bxml.io.FilterAJ;

/**
 * Copy a file
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class BlobLoad extends FilterAJ  {
	private static Log log = LogFactory.getLog(BlobLoad.class);
	private Connection connection = null;
	private String connectionString = null;
	
	public Connection getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connectionString = connection;
	}

	private String table;
	private String field;
	
	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	@Override
	public void execute() throws XMLBuildException  {
		Query query = (Query) this.getAncestorOfType(Query.class);
		connection = query.findConnection(connectionString);
		
		if (connection == null) {
			throw new XMLBuildException("connection not found");
		}
		java.sql.Connection c = null;
		try {
			c = connection.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
			throw new XMLBuildException(e.getMessage());
		}
		loadBinaryData(c);
		try {
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new XMLBuildException(e.getMessage());
		}
	}

	/**
	 * check that all the fields are set correctly, especially required fields.
	 * Called when the end-tag of the element has been reached and processed.
	 */
	public void check() throws XMLBuildException {
		if (table == null) {
			throw new XMLBuildException("table must be set");
		}
		if (field == null) {
			throw new XMLBuildException("table must be set");
		}
		if (connectionString == null) {
			throw new XMLBuildException("You must set the connection");
		}
	}
	
	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public void loadBinaryData(java.sql.Connection c) throws XMLBuildException {

		PreparedStatement sqlStatement = null;

		StringBuffer query = new StringBuffer("insert into " + table + " (" + field);
		StringBuffer values = new StringBuffer("");
		
		//		+ ") values (?, ?)";
		if (fields != null) {

			for (Field field: fields) {
				query.append(", ").append(field.getFieldName());
				values.append(", ?");
			}
		}
		query.append(") values (?").append(values).append(")");
		

		log.debug(" query = " + query);
		try {
			// create prepared statement
			sqlStatement = c.prepareStatement(query.toString());
		} catch (SQLException s) {
			s.printStackTrace();
			throw new XMLBuildException(s.getMessage());
		}
		InputStream inputFileInputStream = in;

		try {
			sqlStatement.setBinaryStream(1, inputFileInputStream);
			int i = 2;
			for (Field field: fields) {
				if (field.getType().equals("java.lang.String"))
					sqlStatement.setString(i, field.getValue());
				else if (field.getType().equals("java.lang.Integer"))
					sqlStatement.setInt(i, Integer.parseInt(field.getValue()));
				i++;
			}
			sqlStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
//		try {
//			sqlStatement.executeUpdate();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
		
	}
	
	List<Field> fields = new ArrayList<Field>();
	
	public void addField(Field f) {
		fields.add(f);
	}
}
