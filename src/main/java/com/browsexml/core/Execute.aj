package com.browsexml.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public aspect Execute {
	pointcut execute(): call(void *.execute());

	    
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

}
