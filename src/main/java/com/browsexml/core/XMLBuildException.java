package com.browsexml.core;

import edu.bxml.io.FilterAJ;

public class XMLBuildException extends RuntimeException {
	 private static final long serialVersionUID = 71446761993856547L;
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}
	
	public XMLBuildException(String message) {
		super(message);
	}
	
	public XMLBuildException(String message, XmlObject object) {
		super(
				((object != null)?(object.getSource() + ":" + object.getLineNumber()):"null object") + ": " + message);
	}

	public XMLBuildException(String message, Object pojo) throws Exception {
		super(
				((XmlParser.getWrapper(pojo) != null)?(XmlParser.getWrapper(pojo).getSource()+ ":" + XmlParser.getWrapper(pojo).getLineNumber()):"null object") + ": " + message);
	}
	
}
