package edu.misc.report;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
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
	Float[] marginArray = new Float[4];
	String file;
	
	public void setText(String text) {
		this.text = text;
	}
	
	public Pdf() {
		setName(null);
	}
	
	public void init(XmlObject parent) throws XMLBuildException {
		super.init(parent);

			
	}
	
	List<ReportObject> objects = new ArrayList<ReportObject>();
	
	@attribute(value = "", required = false)
	public void setLandscape(Boolean landscape) {
		this.landscape = landscape;
	}
	public void setLandscape(String landscape) {
		setLandscape(Boolean.parseBoolean(landscape));
	}
	
	public void setFile(String file) {
		this.file = file;
	}
	
	public void setMargins(String margins) {
		 String[] marginArray = margins.split(" *, *");
		 Float defaultValue = 0f;
		 try {
			defaultValue = Float.parseFloat(marginArray[0]);
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		 this.marginArray[0] = defaultValue;
		 for (int i = 1; i < 4; i++) {
			 this.marginArray[i] = defaultValue;
			 if (i < marginArray.length) {
				 try {
					this.marginArray[i] = Float.parseFloat(marginArray[i]);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			 }
		 }
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
		objects.add(paragraph);
	}

	
	public void addTable(edu.misc.report.Table table) {
		objects.add(table);
	}
	
	public void addNewpage(Newpage newpage) {
		objects.add(newpage);
	}
	
	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() throws XMLBuildException {
		log.debug("Execute PDF");
//		try {
			//workFile = File.createTempFile("pdf", ".pdf");
			workFile = new File(".", "x.pdf");
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//			throw new XMLBuildException(
//					"Could not create report file " + workFile + 
//					" on your computer");
//		};
		log.debug("Work file PDF = " + workFile);

			//String size = attrs.getValue("size");
			try {
				if (landscape) {
					size = size.rotate();
				}
				document = new Document(size, marginArray[0], marginArray[1], marginArray[2], marginArray[3]);
				writer = PdfWriter.getInstance(document, new FileOutputStream(
						workFile));
			} catch (Exception e) {
				e.printStackTrace();
			};

			document.open();
			FontFactory.registerDirectory("/Library/Fonts/Microsoft/");
			float pageWidth = document.right() - document.left();
			cb = writer.getDirectContent();
			ct = new ColumnText(cb);
			ct.setSimpleColumn(document.left(), document.bottom(),
					document.right(), document.top(), 24,
					Element.ALIGN_JUSTIFIED);
			System.err.println("document.open();");
			
			for (ReportObject object:objects) {
				object.setDocument(document);
				object.execute();
			}

			log.debug("margin set = " + marginArray[0]);

		try {
			document.close();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		log.debug("document.close();");

		//Program prog = Program.findProgram("PDF");
		//log.debug("prog = " + prog);
		//prog.execute(workFile.getPath());
		log.debug("file = " + workFile.getPath());
	}
}
