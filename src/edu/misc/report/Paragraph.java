package edu.misc.report;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Font.FontFamily;

public class Paragraph extends ReportObject {
	private static Log log = LogFactory.getLog(Paragraph.class);
	Font font = null;
	String text = "";
	String alignment;

	
	
	public void setAlignment(String alignment) {
		//com.itextpdf.text.Font font = FontFactory.getFont("Calibri",  11, com.itextpdf.text.Font.NORMAL);
		this.alignment = alignment;
	}
	
	public void addFont(Font font) {
		
	}
	
	public void addFontEnd(Font font) {
		log.debug("endAddFont");
		this.font = font;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public Paragraph() {
		setName(null);
	}
	
	public Paragraph(String name) {
		setName(name);
	}
	
	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() throws XMLBuildException {
		try {
			log.debug("paragraph execute");
			com.itextpdf.text.Paragraph paragraph = new com.itextpdf.text.Paragraph(text, font==null?null:font.getFont());
			int align = XmlParser.getFieldValues(Element.class, alignment);
			log.debug("Align = " + alignment);
			log.debug("Align = " + align);
			paragraph.setAlignment(align);
			document.add(paragraph);
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new XMLBuildException(e.getMessage(), this);
		}
	}
}
