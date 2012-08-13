package edu.misc.report;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;
import com.itextpdf.text.FontFactory;

public class Font extends ReportObject {
	private static Log log = LogFactory.getLog(Font.class);
	private com.itextpdf.text.Font font = null;

	String text = null;
	String face = "HELVETICA";
	float size = 12;
	int fontStyle = com.itextpdf.text.Font.NORMAL;

	public Font() {
		setName(null);
	}
	
	public Font(String name) {
		setName(name);
	}
	
	/**
	 * The font face (HELVETICA, TIMES_ROMAN...)
	 * @param face
	 */
	@attribute(value = "", required = false, defaultValue="HELVETICA")
	public void setFace(String face) {
		this.face = face;
	}
	
	/**
	 * The font point size
	 * @param face
	 */
	@attribute(value = "", required = false, defaultValue="12")
	public void setSize(Float size)  {
		this.size = size;
	}
	public void setSize(String size)  throws XMLBuildException {
		try {
			setSize(Float.parseFloat(size));
		}
		catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			throw new XMLBuildException (nfe.getMessage());
		}
	}
	
	/**
	 * Font style (BOLD, ITALIC, NORMAL)
	 * @param style
	 * @throws XMLBuildException
	 */
	@attribute(value = "", required = false, defaultValue="NORMAL")
	public void setStyle(java.lang.String style) {
		
		this.fontStyle = XmlParser.getFieldValues(
				com.itextpdf.text.Font.class, style);
		log.debug("set style to " + style + " = " + fontStyle);
	}
	
	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() {
		
	}
	
	
	public com.itextpdf.text.Font getFont() {
		log.debug("get font " + face + "  " + size + "  " + fontStyle);
		return FontFactory.getFont(face, size, fontStyle);
	}
}
