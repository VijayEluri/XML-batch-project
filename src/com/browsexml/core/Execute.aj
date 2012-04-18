package com.browsexml.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.bxml.io.FilterAJ;
import edu.bxml.io.SortedCsvDiff;


public aspect Execute {
	pointcut execute():
	    call(void *.execute());
	    
	    
	    /*
	    	All variables referenced by foreign objects 
	    	get macro replacement on a call to execute.
	    */
	void around(XmlObject x): execute() && target(x) {
		Log log = LogFactory.getLog(this.getClass().getName());
		log.debug("AJ Execute");
	    try {
	    	if (x.isIff()){
		    	Set c = x.getVariableParameters().entrySet();   
		    	log.debug("size of variable parameters = " + c.size());
		    	Iterator itr = c.iterator();
		    	
		    	while(itr.hasNext()) {
		    		Object[] arguments = new Object[1];
		    		Entry<Method, String> hmItem = (Entry<Method, String>) itr.next();
		    		
		    		arguments[0] = XmlParser.processMacros(x.getSymbolTable(), XmlParser.replacePoundMacros(hmItem.getValue()));
		    		log.debug("aspect before execute " + x.getName() + ": " + x.getClass().getName() + ": " + hmItem.getValue() + " = " + arguments[0]);
		    		try {
						hmItem.getKey().invoke(x, arguments);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
		    	}
		    	if (x.isIff()){
		    		proceed(x);
		    	}
		    	else {
		    		log.debug(x.getName() + ": " + x.getClass().getName() + ": NOT EXECUTED: iff is false");
		    	}
	    	}
		} catch (XMLBuildException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * for future direction; filters should be handled with aspect
	 * Too big a project for now; future filters should extend FilterAJ
	 * rather than Filter
	 */
	void around(FilterAJ x) throws XMLBuildException: execute() && target(x) {
		Log log = LogFactory.getLog(this.getClass().getName());
		if (x.getLock()) {
			System.err.println("LOCK");
			proceed(x);
			x.setLock(false);
			return;
		}
		Boolean closeIn = false;
		Boolean closeOut = false;
		InputStream in = x.getIn();
		OutputStream out = x.getOut();

		log.debug("proceed with no lock on " + x.getClass().getName());
		proceed(x);
		log.debug("proceed DONE on " + x.getClass().getName());
		x.closeIn();
		x.closeOut();
	}
}
