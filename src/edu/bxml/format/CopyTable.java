package edu.bxml.format;

import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.javalobby.tnt.annotation.attribute;

import edu.bxml.io.FilterAJ;
import edu.bxml.io.FilterAJImpl;

/**
 * Copy an sql tables data from one database to another
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class CopyTable extends FilterAJImpl implements FilterAJ {
	private static Log log = LogFactory.getLog(CopyTable.class);
	PrintStream localOut = null;
	public static String newline = System.getProperty("line.separator");
	private ArrayList<Column> columns = new ArrayList<Column>();
	String fromConnection;
	String toConnection;
	String fromTable;
	String toTable;
	
	public String getFromTable() {
		return fromTable;
	}

	public void setFromTable(String fromTable) {
		this.fromTable = fromTable;
	}

	public String getToTable() {
		return toTable;
	}

	public void setToTable(String toTable) {
		this.toTable = toTable;
	}

	@Override
	public void execute() {
		localOut = new PrintStream(out);
		Query query = this.getAncestorOfType(Query.class);
		
			Connection connFrom = query.findConnection(fromConnection);
			Connection connTo = query.findConnection(toConnection);
			
			Statement stmt = null;
			ResultSet rs = null;
			
			try {
				java.sql.Connection c = connFrom.getConnection();
				stmt = c.createStatement();
				rs = stmt.executeQuery("select * from " + fromTable);
				//TODO: finish logic to copy table
				
				rs.close();
				stmt.close();
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
	}

	public String getFromConnection() {
		return fromConnection;
	}

	public void setFromConnection(String fromConnection) {
		this.fromConnection = fromConnection;
	}

	public String getToConnection() {
		return toConnection;
	}

	public void setToConnection(String toConnection) {
		this.toConnection = toConnection;
	}

}
