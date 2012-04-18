package edu.misc.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.javalobby.tnt.annotation.attribute;


public class Pdf extends ReportObject {
	private static Log log = LogFactory.getLog(Pdf.class);
	String text = null;
	boolean landscape = false;
	Document document = null;
	PdfWriter writer = null;
	PdfContentByte cb = null;
	ColumnText ct = null;
	File workFile = null;
	Rectangle size = PageSize.LETTER;
	
	public void setText(String text) {
		this.text = text;
	}
	
	public Pdf() {
		setName(null);
	}
	
	public void init(XmlObject parent) throws XMLBuildException {
		super.init(parent);
		try {
			workFile = File.createTempFile("pdf", ".pdf");
		} catch (IOException e) {
			e.printStackTrace();
			throw new XMLBuildException(
					"Could not create report file " + workFile + 
					" on your computer");
		};
		log.debug("Work file PDF = " + workFile);

			//String size = attrs.getValue("size");
			try {
				if (landscape) {
					size = size.rotate();
				}
				document = new Document(size, 20, 20, 20,20);
				writer = PdfWriter.getInstance(document, new FileOutputStream(
						workFile));
			} catch (Exception e) {
				e.printStackTrace();
			};

			document.open();
			float pageWidth = document.right() - document.left();
			cb = writer.getDirectContent();
			ct = new ColumnText(cb);
			ct.setSimpleColumn(document.left(), document.bottom(),
					document.right(), document.top(), 24,
					Element.ALIGN_JUSTIFIED);
			System.err.println("document.open();");
			
	}
	
	@attribute(value = "", required = false)
	public void setLandscape(Boolean landscape) {
		this.landscape = landscape;
	}
	public void setLandscape(String landscape) {
		setLandscape(Boolean.parseBoolean(landscape));
	}
	
	/**
	 * Set the size to one of the predefined page sizes; LETTER, LEGAL..
	 * @param size
	 * @throws XMLBuildException
	 */
	@attribute(value = "", required = false, defaultValue="LETTER")
	public void setSize(String size) throws XMLBuildException {
		Field f = null;
		try {
			Class c = new PageSize().getClass();
			f = c.getField(size);
			this.size = (Rectangle) f.get(c);
		} catch (NoSuchFieldException nsfe) {
			nsfe.printStackTrace();
			throw new XMLBuildException (nsfe.getMessage());
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
			throw new XMLBuildException (iae.getMessage());
		}
	}
	
	public void addPageFooter(edu.misc.report.PageFooter footer) {
		
	}
	
	/**
	 * Add another font to the list of fonts usable within this document.
	 * Don't do anything with font; we'll get it off the symbolTable
	 * @param font
	 */
	@attribute(value = "", required = false)
	public void addFont(Font font) {
		
	}
	
	public void addParagraph(Paragraph paragraph) {
		//paragraph.setDocument(document);
	}
	
	public void addParagraphEnd(Paragraph paragraph) throws XMLBuildException {
		//paragraph.execute();
	}
	
	public void addTable(edu.misc.report.Table table) {
		//table.setDocument(document);
	}
	
	public void addTableEnd(edu.misc.report.Table table) throws XMLBuildException {
		table.execute();
	}
	
	public void addNewpage(Newpage newpage) {
		//newpage.setDocument(document);
	}
	
	public void addNewpageEnd(Newpage newpage) {
		newpage.execute();
	}
	
	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() throws XMLBuildException {
		log.debug("Execute PDF");

		try {
			document.close();
		} catch (RuntimeException e) {
			 MessageBox messageBox =
				   new MessageBox(Display.getDefault().getShells()[0],
				    SWT.OK|SWT.ICON_ERROR);
			 messageBox.setMessage("The report is empty");
			 messageBox.open();
			e.printStackTrace();
		}
		log.debug("document.close();");

		Program prog = Program.findProgram("PDF");
		log.debug("prog = " + prog);
		prog.execute(workFile.getPath());
	}
}
