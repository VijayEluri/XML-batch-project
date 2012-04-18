package edu.misc.report;

import org.xml.sax.Attributes;

import com.browsexml.core.XMLBuildException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.javalobby.tnt.annotation.attribute;


public class Table extends ReportObject {
	float pageWidth = 0;
	int[] widths = null;
	int rowCount = 0;
	
	com.itextpdf.text.pdf.PdfPTable table = null;
	
	@Override
	public boolean processRawAttributes(Attributes attrs)
			throws XMLBuildException {
		String strWidths = attrs.getValue("widths");
		if (strWidths == null) {
			throw new XMLBuildException("you must set the widths of the table.");
		}
		implSetWidths(strWidths);
		table = new com.itextpdf.text.pdf.PdfPTable(widths.length);
		
		pageWidth = document.right() - document.left();
		table.setTotalWidth(pageWidth);
		
		return true;
	}
	
	@attribute(value = "", required = false)
	public void setWidth(Float width) {
		pageWidth = width;
		table.setTotalWidth(pageWidth);
	}
	public void setWidth(String width) {
		setWidth(Float.parseFloat(width));
	}
	
	@Override
	public void setDocument(Document document) {
		super.setDocument(document);
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
		newpage.execute();
	}
	
	public void addHeader(Header header) throws XMLBuildException {
		header.setTable(this);
		header.execute();
	}
	
	public void addTable(Table t) {
		
	}
	
	public void addTableEnd(Table t) {
		
	}
	
	/**
	 * A row of the table
	 * @param newPage
	 */
	@attribute(value = "", required = false)
	public void addTr(Tr tr) {
		rowCount++;
	}
	
	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() throws XMLBuildException {
		try {
			document.add(table);
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new XMLBuildException(e.getMessage());
		}
	}
}
