package edu.misc.report;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;


public class Chunk extends ReportObject {
	private static Log log = LogFactory.getLog(Chunk.class);
		private com.itextpdf.text.Chunk chunk;

	private String font;
	
	@Override
	public void check() throws XMLBuildException {
	}
	
	
	public void setText(String text) {
		this.text = text;
	}
	
	public Chunk() {
		setName(null);
	}
	
	public Chunk(String name) {
		setName(name);
	}

	public void setFont(String strFont) {
		this.font = strFont;
	}
	
	
	public void addField(Field field) {
		
	}
	
	public com.itextpdf.text.Chunk getChunk() {
		return chunk;
	}

	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() {
		String text = getValue();
		if (text == null) {
			text = "";
		}
		chunk = new com.itextpdf.text.Chunk(text);
		
		Font font = (Font) this.getSymbolTable().get(this.font);
		if (font == null) {
			log.debug("Font '" + this.font + "' not found in environment");
		}
		else 
			chunk.setFont(font.getFont());
		log.debug("phrase execute text = " +text);
		
	}
	
	/**
	 * Retrieve the text that was contained inside the tag
	 */
	public void setFromTextContent(String text) {

		if (text == null) 
			return;
		setValue(text.trim());
	}
}
