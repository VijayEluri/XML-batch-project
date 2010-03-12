package edu.misc.report;

import com.browsexml.core.XmlObject;

public class PageFooter extends ReportObject {

	String text = null;
	public void setText(String text) {
		this.text = text;
	}
	
	public PageFooter() {
		setName(null);
	}
	
	public PageFooter(String name) {
		setName(name);
	}
	
	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() {
		System.out.println(text);
	}
	
}
