package edu.misc.report;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.javalobby.tnt.annotation.attribute;
import com.itextpdf.text.Chunk;


public class Phrase extends ReportObject {
	private static Log log = LogFactory.getLog(Phrase.class);
		private com.itextpdf.text.Phrase phrase = new com.itextpdf.text.Phrase();
		float fltLeading = 0;

	String text = null;
	
	@Override
	public void check() throws XMLBuildException {
	}
	
	@attribute(value = "", required = false)
	public void setLeading(Float leading) {
		fltLeading = leading;
		phrase.setLeading(fltLeading);
	}
	public void setLeading(String leading) {
		try {
			setLeading(Float.parseFloat(leading));
		} catch (NumberFormatException nfe) {
		};
		
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public Phrase() {
		setName(null);
	}
	
	public Phrase(String name) {
		setName(name);
	}

	public void setFont(String strFont) {
		Font font = (Font) this.getSymbolTable().get(strFont);
		log.debug("set font(" + strFont + "  size: " + font.getFont().getSize());
		phrase.setFont(font.getFont());
	}
	
	public void addField(Field field) {
		
	}
	
	public com.itextpdf.text.Phrase getPhrase() {
		return phrase;
	}

	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() {
		
	}
	
	/**
	 * Retrieve the text that was contained inside the tag
	 */
	public void setFromTextContent(String text) {

		if (text == null) 
			return;
		this.text = text.trim();
		phrase.add(new Chunk(text));
	}
}
