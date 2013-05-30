package edu.bxml.format;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.javalobby.tnt.annotation.attribute;

/**
 * Specify the query that needs formatting
 * @author ritcheyg
 *
 */
@attribute(value = "", required = true)
public class ExcelData extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(ExcelData.class);
	String text = null;
	int sheet = 0;
	int table = 0;
	int row = -1;
	int maxRow = -1;
	int cell = -1;
	int maxCell = -1;
	int cellFoundAt = -1;
	Pattern pattern = null;
	String result = null;
	ExcelData rowData = null;
	ExcelData cellData = null;
	
	public int getMaxRow() {
		return maxRow;
	}
	public void setCellFoundAt(int cell) {
		cellFoundAt = cell;
	}
	public Pattern getPattern() {
		return pattern;
	}
	public void setRegex(String regex) {
		log.debug("ExcelData regex = " + regex);
		pattern = Pattern.compile(regex);
	}
	public String getResult(Matcher m) {
		StringBuffer ret = new StringBuffer("");
		String result = this.result;
		if (result == null) 
			return null;
		for (int i = m.groupCount(); i > 0 ; i--) {
			result = result.replaceAll("\\\\"+i, m.group(i));
		}
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public int getCell() {
		if (cellData != null) 
			return cellData.cellFoundAt;
		return cell;
	}
	public int getMaxCell() {
		if (cellData != null) 
			return cellData.cellFoundAt;
		return maxCell;
	}
	public void setCell(String cell) {
		if (Character.isDigit(cell.charAt(0))) {
			String[] cells = cell.split("-");
			this.cell = Integer.parseInt(cells[0]);
			if (cells.length>1) {
				this.maxCell = Integer.parseInt(cells[1]);
			}
		}
		else {
			HashMap st = this.getSymbolTable();
			if (st == null) {
				new Exception("Symbol table is null").printStackTrace();
			}
			cellData = (ExcelData)st.get(cell);
		}
	}
	public int getRow() {
		if (rowData != null) 
			return rowData.getRow();
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public void setRow(String row) throws XMLBuildException {
		if (Character.isDigit(row.charAt(0))) {
			String[] rows = row.split("-");
			this.row = Integer.parseInt(rows[0]);
			if (rows.length>1) {
				this.maxRow = Integer.parseInt(rows[1]);
				if (this.row < maxRow) {
					HashMap st = this.getSymbolTable();
					if (st.get("iterator") != null) {
						throw new XMLBuildException("more than one iterator row");
					}
					st.put("iterator", this);
				}
			}
		}
		else {
			HashMap st = this.getSymbolTable();
			if (st == null) {
				new Exception("Symbol table is null").printStackTrace();
			}
			rowData = (ExcelData)st.get(row);
		}
	}
	public int getSheet() {
		return sheet;
	}
	public void setSheet(String sheet) {
		this.sheet = Integer.parseInt(sheet);
	}
	public int getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = Integer.parseInt(table);
	}
	/**
	 * check that all the fields are set correctly, especially
	 * required fields.  Called when the end-tag of the 
	 * element has been reached and processed.
	 */
	public void check() throws XMLBuildException {

	}
	/**
	 * Called after complete parsing of XML document
	 * to evaluate the document.
	 */
	public void execute() {
		
	}
	/**
	 * Store text set from an attribute
	 */
	public void setText(String text) {
		
	}
	/**
	 * Store text set from within the begin-tag/End-tag area
	 */
	public void setFromTextContent(String text) {
		this.text = text;
	}
	/**
	 * Get the data held in this data item
	 * @return
	 */
	public String getText() {
		return text;
	}
}
