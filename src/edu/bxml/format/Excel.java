package edu.bxml.format;

import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.io.FilterAJ;

/**
 * Output the results of a select query to an Excel xml format
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Excel extends FilterAJ {
	
	PrintStream localOut = null;
	public static String newline = System.getProperty("line.separator");
	private ArrayList<Column> columns = new ArrayList<Column>();
	private String dateFormat = null;
	private SimpleDateFormat format = null;

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
		format = new SimpleDateFormat(dateFormat);
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	private static Log log = LogFactory.getLog(Excel.class);
	String queryName;

	@Override
	public void execute() {
		localOut = new PrintStream(out);
		Query query = this.getAncestorOfType(Query.class);
		

			Sql sql = query.getSql(queryName);
			
			Connection connection = sql.getConnection();
			Statement stmt = null;
			ResultSet rs = null;
			java.sql.Connection c = null;

			try {
				log.debug("sql = " + sql.getQuery());
				c = connection.getConnection();
				stmt = c.createStatement();
				rs = stmt.executeQuery(sql.getQuery());

				printHeader();
				printTable(rs);
				printFooter();
				

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
					c.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				localOut.close();
			}	
	}

	public void printTable(ResultSet rs) throws SQLException, XMLBuildException {

		HashMap<String, Integer> metaDataColumns = new HashMap<String, Integer>();
		for (int j = 1; j < rs.getMetaData().getColumnCount()+1; j++) {
			String field = rs.getMetaData().getColumnName(j);
			metaDataColumns.put(rs.getMetaData().getColumnName(j), j);
		}
		log.debug(metaDataColumns.toString());
		if (columns.size() == 0) {
			for (int j = 1; j < rs.getMetaData().getColumnCount()+1; j++) {
				columns.add(new Column(rs.getMetaData().getColumnName(j), j));
			}
		}
		localOut.println(""
				+ "  <Table ss:ExpandedColumnCount=\"1024\" "+ newline 
				+ "   x:FullColumns=\"1\" x:FullRows=\"1\" ss:StyleID=\"s62\" ss:DefaultRowHeight=\"15\"> ");
		int i = 1;
		for (Column column:columns) {
			String width = column.getWidth();
			if (width != null) {
				localOut.println("   <Column ss:Index=\"" + i + "\" ss:StyleID=\"s62\" ss:Width=\"" + width + "\"/> ");
			}
			i++;
		}
		localOut.println("   <Row ss:AutoFitHeight=\"0\" ss:Height=\"12.75\"> " + "");
		for (Column column: columns) {
			column.setIndex(metaDataColumns.get(column.getFieldName()));
			log.debug("column = " + column);
			printHeader(column.getHeader());
		}
		localOut.println("</Row>");
log.debug("rs count = " + rs.getFetchSize());
		while (rs.next()) {
			localOut.println("<Row ss:AutoFitHeight=\"0\" ss:Height=\"12.75\">");
			for (Column column: columns) {
				printDataLine(rs.getObject(column.getIndex()));
			}
			localOut.println("</Row>");
		}
		localOut.println("</Table>");
	}

	public void printDataLine(Object data) {
		String strData = "";
		if (data != null) {
			strData = data.toString();
			//log.debug("CLASS: " + data.getClass().getCanonicalName());
			if (data instanceof java.sql.Timestamp) {
				strData = format.format(data);
			}
		}
		
		localOut.println("<Cell ss:StyleID=\"s70\"><Data ss:Type=\"String\"><![CDATA["
				+ strData + "]]></Data></Cell>");
	}

	public void printHeader(String headerName) {
		localOut.println("<Cell ss:StyleID=\"s70\"><Data ss:Type=\"String\"><![CDATA["
				+ headerName + "]]></Data></Cell>");
	}
	
	public void printFooter() {
		localOut.println(""
				+ "  <WorksheetOptions xmlns=\"urn:schemas-microsoft-com:office:excel\"> " + newline 
				+ "   <Selected/> "+ newline 
				+ "   <ProtectObjects>False</ProtectObjects> "+ newline 
				+ "   <ProtectScenarios>False</ProtectScenarios> "+ newline 
				+ "  </WorksheetOptions> " + "  <x:WorksheetOptions/> "+ newline 
				+ " </Worksheet> " + newline 
				+ "</Workbook> " + newline 
				+ "");
	}

	public void printHeader() {
		localOut.println(""
				+ "<?xml version=\"1.0\"?> "+ newline 
				+ "<?mso-application progid=\"Excel.Sheet\"?> "+ newline 
				+ "<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\" "+ newline 
				+ " xmlns:o=\"urn:schemas-microsoft-com:office:office\" "+ newline 
				+ " xmlns:x=\"urn:schemas-microsoft-com:office:excel\" "+ newline 
				+ " xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\" "+ newline 
				+ " xmlns:c=\"urn:schemas-microsoft-com:office:component:spreadsheet\" "+ newline 
				+ " xmlns:html=\"http://www.w3.org/TR/REC-html40\" "+ newline 
				+ " xmlns:x2=\"http://schemas.microsoft.com/office/excel/2003/xml\" "+ newline 
				+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"> "+ newline 
				+ " <DocumentProperties xmlns=\"urn:schemas-microsoft-com:office:office\"> "+ newline 
				+ "  <Version>12.00</Version> "+ newline 
				+ " </DocumentProperties> "+ newline 
				+ " <OfficeDocumentSettings xmlns=\"urn:schemas-microsoft-com:office:office\"> "+ newline 
				+ "  <Colors> "+ newline 
				+ "   <Color> "+ newline 
				+ "    <Index>3</Index> "+ newline 
				+ "    <RGB>#C0C0C0</RGB> "+ newline 
				+ "   </Color> "+ newline 
				+ "   <Color> "+ newline 
				+ "    <Index>4</Index> "+ newline 
				+ "    <RGB>#FF0000</RGB> "+ newline 
				+ "   </Color> "+ newline 
				+ "  </Colors> "+ newline 
				+ " </OfficeDocumentSettings> "
				+ " <ExcelWorkbook xmlns=\"urn:schemas-microsoft-com:office:excel\"> "+ newline 
				+ "  <WindowHeight>9000</WindowHeight> "+ newline 
				+ "  <WindowWidth>13860</WindowWidth> "+ newline 
				+ "  <WindowTopX>240</WindowTopX> "+ newline 
				+ "  <WindowTopY>75</WindowTopY> "+ newline 
				+ "  <ProtectStructure>False</ProtectStructure> "+ newline 
				+ "  <ProtectWindows>False</ProtectWindows> "+ newline 
				+ " </ExcelWorkbook> "+ newline 
				+ " <Styles> "+ newline 
				+ "  <Style ss:ID=\"Default\" ss:Name=\"Normal\"> "+ newline 
				+ "   <Alignment ss:Vertical=\"Bottom\"/> "
				+ "   <Borders/> "+ newline 
				+ "   <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"11\" ss:Color=\"#000000\"/> "+ newline 
				+ "   <Interior/> " + "   <NumberFormat/> "+ newline 
				+ "   <Protection/> " + "  </Style> "+ newline 
				+ "  <Style ss:ID=\"s62\" ss:Name=\"Default\"> "+ newline 
				+ "  </Style> "+ newline 
				+ "  <Style ss:ID=\"s67\" ss:Name=\"Excel Built-in Normal\"> "+ newline 
				+ "   <Alignment ss:Vertical=\"Bottom\"/> " + "   <Borders/> "+ newline 
				+ "   <Font ss:FontName=\"MS Sans Serif\"/> "+ newline 
				+ "   <NumberFormat ss:Format=\"Fixed\"/> " + "  </Style> "+ newline 
				+ "  <Style ss:ID=\"s70\" ss:Parent=\"s67\"> "+ newline 
				+ "   <Alignment ss:Vertical=\"Bottom\"/> " + "   <Borders/> "+ newline 
				+ "   <Font ss:FontName=\"MS Sans Serif\"/> "+ newline 
				+ "   <Interior/> " + "   <NumberFormat ss:Format=\"Fixed\"/> "+ newline 
				+ "  </Style> " + " </Styles>"+ newline 
				+ "<Worksheet ss:Name=\"" + queryName + "\">"+ newline );
	}
	
	public void addColumn(Column column) {
		columns.add(column);
	}

}
