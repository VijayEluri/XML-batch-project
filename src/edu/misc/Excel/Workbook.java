package edu.misc.Excel;

import java.util.Vector;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.format.ExcelData;

/**
 * Specify the query that needs formatting
 * @author ritcheyg
 *
 */
@attribute(value = "", required = true)
public class Workbook extends XmlObject {
	Vector<Worksheet> worksheets = new Vector<Worksheet>();
	String separator = "|";
	
	/**
	 * check that all the fields are set correctly, especially
	 * required fields.  Called when the end-tag of the 
	 * element has been reached and processed.
	 */
	public void check() throws XMLBuildException {

	}
	public void print() {
		int worksheetCount = 0;
		for (Worksheet ws: worksheets) {
			worksheetCount++;
			int tableCount = 0;
			for (Table table:ws.getTables()) {
				tableCount++;
				int rowCount = 0;
				for (Row row:table.getRows()) {
					System.err.println("");
					rowCount++;
					System.err.print(worksheetCount + separator + tableCount + 
							separator + rowCount);
					for (Cell cell:row.getCells()) {
						System.err.print(separator);
						for (Data d:cell.getData()) {
							System.err.print(d.getText());
						}
					}
				}
			}
		}
		System.err.println("");
	}
	/**
	 * Called after complete parsing of XML document
	 * to evaluate the document.
	 */
	public void execute() {
	
	}
	
	public String getLine(ExcelData data) {//int sheetIndex, int tableIndex, int rowIndex, 
			//int cellIndex, int maxCellIndex) {
		//d.getSheet(), d.getTable(), 
		//	d.getRow(), d.getCell(), d.getMaxCell());
		int sheetIndex = data.getSheet();
		int tableIndex = data.getTable();
		int rowIndex = data.getRow();
		int cellIndex = data.getCell();
		int maxCellIndex = data.getMaxCell();
		StringBuffer retData = new StringBuffer("");
		Row row = worksheets.get(sheetIndex).getTables().get(tableIndex).getRows().get(rowIndex);
		System.err.println("Cell Index = " + cellIndex);
		if (cellIndex < 0) {
			for (Cell cell:row.getCells()) {
				retData.append(separator);
				for (Data d:cell.getData()) {
					retData.append(d.getText());
				}
			}
		}
		else {
			if (maxCellIndex < cellIndex) 
				maxCellIndex = cellIndex;
			System.err.println(data.getName() + 
					"  cell = " + cellIndex + "  max = " + maxCellIndex );
			for (int i = cellIndex; i <= maxCellIndex; i++) {
				Cell cell = null;
				try {
					cell = row.getCells().get(i);
				} catch (RuntimeException e) {
					e.printStackTrace();
					return "";
				}
				retData.append(separator);
				for (Data d:cell.getData()) {
					retData.append(d.getText());
				}
			}
			return retData.substring(1).toString();
		}
		return retData.substring(1).toString();
	}
	
	/**
	 * Retrieve the text that was contained inside the tag
	 */
	public void setText(String text) {

	}
	public void addWorksheet(Worksheet w) {
		System.err.println("Add worksheet ");
		worksheets.add(w);
	}
}
