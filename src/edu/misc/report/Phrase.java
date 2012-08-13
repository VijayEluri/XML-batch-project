package edu.misc.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.javalobby.tnt.annotation.attribute;


public class Phrase extends ReportObject {
	private static Log log = LogFactory.getLog(Phrase.class);
		private com.itextpdf.text.Phrase phrase;
		float fltLeading = 0;

	String text = null;
	private String font;
	private List<Chunk> chunks = new ArrayList<Chunk>();
	
	@Override
	public void check() throws XMLBuildException {
	}
	
	@attribute(value = "", required = false)
	public void setLeading(Float leading) {
		fltLeading = leading;
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
		this.font = strFont;
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
		phrase = new com.itextpdf.text.Phrase();
		phrase.setLeading(fltLeading);
		String text = getValue();
		log.debug("phrase text = " + text);
		if (text == null) {
			text = "";
		}
		Font font = (Font) this.getSymbolTable().get(this.font);
		if (font == null) {
			log.debug("Font '" + this.font + "' not found in environment");
		}
		else 
			phrase.setFont(font.getFont());
		log.debug("st = " + getSymbolTable());
		log.debug("phrase execute text = " +text);
		new Exception().printStackTrace();
		phrase.add(new com.itextpdf.text.Chunk(text));
		for (Chunk chunk: chunks) {
			chunk.execute();
			phrase.add(chunk.getChunk());
		}
	}
	
	/**
	 * Retrieve the text that was contained inside the tag
	 */
	public void setFromTextContent(String text) {

		if (text == null) 
			return;
		setValue(text.trim());
	}
	
	public void addChunk(Chunk chunk) {
		chunks.add(chunk);
	}
}
