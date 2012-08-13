package edu.misc.report;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.itextpdf.text.DocumentException;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.format.Connection;
import edu.bxml.format.Query;
import edu.bxml.format.Sql;


public class Table extends ReportObject {
	private static Log log = LogFactory.getLog(Table.class);
	
	float pageWidth = 0;
	int[] widths = null;
	int rowCount = 0;
	
	com.itextpdf.text.pdf.PdfPTable table = null;

	private PrintStream localOut;

	private String queryName;
	
	@Override
	public boolean processRawAttributes(Attributes attrs)
			throws XMLBuildException {
		String strWidths = attrs.getValue("widths");
		if (strWidths == null) {
			throw new XMLBuildException("you must set the widths of the table.");
		}
		implSetWidths(strWidths);
		table = new com.itextpdf.text.pdf.PdfPTable(widths.length);
		
		try {
			table.setWidths(widths);
		} catch (DocumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
//		pageWidth = document.right() - document.left();
//		log.debug("right = " + document.right());
//		log.debug("left = " + document.left());
//		log.debug("width = " + pageWidth);

		//table.setTotalWidth(pageWidth);

		
		return true;
	}
	
	@attribute(value = "", required = false)
	public void setWidth(Float width) {
		//pageWidth = width;
		//table.setTotalWidth(pageWidth);
		table.setWidthPercentage(width);
	}
	public void setWidth(String width) {
		setWidth(Float.parseFloat(width));
	}
	
	public com.itextpdf.text.pdf.PdfPTable getTable() {
		return table;
	}
	
	public int getWidthCount() {
		if (widths != null) 
			return widths.length;
		return 0;
	}
	
	public Table() {
		setName(null);
	}
	
	public Table(String name) {
		setName(name);
	}
	
	/**
	 * The percentage of the page this table will occupy as a percentage
	 * @param percentage
	 * @throws XMLBuildException
	 */
	@attribute(value = "", required = false, defaultValue="100")
	public void setPercentage(Integer percentage) {
		table.setWidthPercentage(percentage);
	}
	public void setPercentage(java.lang.String strPercentage) throws XMLBuildException {
		try {
			setPercentage(Integer.parseInt(strPercentage));
		}
		catch (NumberFormatException n) {
			n.printStackTrace(); 
			throw new XMLBuildException(n.getMessage());
		}
	}
	
	/**
	 * Command separated list of integers specifying the width of
	 * each column of the table as a percentage where the total of all
	 * the numbers is 100%.  The number of columns the table will have 
	 * is determined by the number of widths specified.
	 * @param widths
	 */
	@attribute(value = "", required = false, defaultValue="1")
	public void setWidths(String widths) throws XMLBuildException {
	}
	
	public void implSetWidths(String widths) throws XMLBuildException {
		String[] widthArray = widths.split(",");
		this.widths = new int[widthArray.length];
		for (int i = 0; i < widthArray.length; i++) {
			try {
				this.widths[i] = Integer.parseInt(widthArray[i]);
			}
			catch (NumberFormatException e) {
				e.printStackTrace();
				throw new XMLBuildException (e.getMessage());
			}
		}

	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	/**
	 * End current page and move to a new one.
	 * @param newPage
	 */
	@attribute(value = "", required = false)
	public void addNewpage(Newpage newPage) {
		newPage.setDocument(document);
	}
	
	public void addNewpageEnd(Newpage newpage) {
//		newpage.execute();
	}
	
	Header header;
	public void addHeader(Header header) throws XMLBuildException {
		this.header = header;
	}
	
	public void addTable(Table t) {
		
	}
	
	public void addTableEnd(Table t) {
		
	}
	
	List<Tr> rows = new ArrayList<Tr>();
	/**
	 * A row of the table
	 * @param newPage
	 */
	@attribute(value = "", required = false)
	public void addTr(Tr tr) {
		rows.add(tr);
		rowCount++;
	}
	
	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() throws XMLBuildException {
		//localOut = new PrintStream(out);
		Query query = this.getAncestorOfType(Query.class);
		

			Sql sql = null;
			
			if (query != null) 
				sql = query.getSql(queryName);
			else {
				log.debug("query name = " + queryName);
				log.debug("st = " + this.getSymbolTable());
				sql  = (Sql) this.getSymbolTable().get(queryName);
			}	
			
			if (header != null) {
				header.execute();
			}
			
			if (sql != null) {
				doAsTemplate(sql);
			}
			else {
				for (Tr tr: rows) {
					tr.execute();
				}
			}

			
		try {
			log.debug("table execute");
			document.add(table);
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new XMLBuildException(e.getMessage());
		}
			
	}
			
		private void doAsTemplate(Sql sql) {
			Connection connection = sql.getConnection();
			Statement stmt = null;
			ResultSet rs = null;
			java.sql.Connection c = null;
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			
			XmlParser f = new XmlParser(factory);

			log.debug("f = " + f);
			f.setParent(this);
			HashMap map = this.getSymbolTable();

			try {
				log.debug("sql = " + sql.getQuery());
				c = connection.getConnection();
				stmt = c.createStatement();
				rs = stmt.executeQuery(sql.getQuery());
				
				//printHeader();
				printTable(rs);
				///printFooter();
				

			} catch (XMLBuildException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			finally {
				try {
					log.debug("Close Record Set");
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					log.debug("Close Statement");
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					log.debug("Close Connection");
//					new Exception().printStackTrace();
					c.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				//localOut.close();
			}	
		}
	
	public void printTable(ResultSet rs) throws SQLException, XMLBuildException {

		
		while (rs.next()) {
			log.debug("LOOP ");
			HashMap newSymbolTable = this.getSymbolTable();

			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				newSymbolTable.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
			}
			
			for (Tr tr: rows) {
				tr.execute();
			}
			
			log.debug("st = " + newSymbolTable);
			log.debug("var par = "  + this.getVariableParameters());

			
			
			

		}
		

	}
	

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

}
