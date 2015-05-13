package edu.misc.report;


public class Field extends ReportObject {

	String text = null;
	public void setText(String text) {
		this.text = text;
	}
	
	public Field() {
		setName(null);
	}
	
	public Field(String name) {
		setName(name);
	}
	
	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() {
		System.out.println(text);
	}
	
}
