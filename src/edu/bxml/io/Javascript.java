package edu.bxml.io;

import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;

public class Javascript extends XmlObject {
	private String script = "ECMAScript";
	private String text = "";
	private Object value = null;

	@Override
	public void check() throws XMLBuildException {
		
	}

	@Override
	public void execute() throws XMLBuildException {
	    ScriptEngineManager sem = new ScriptEngineManager();
	    ScriptEngine e = sem.getEngineByName(script);
	    ScriptEngineFactory f = e.getFactory();

	    e.setBindings(new SimpleBindings(this.getSymbolTable()), ScriptContext.ENGINE_SCOPE );
	    try {
	    	value = e.eval(text);
	    } catch (ScriptException ex) {
	      ex.printStackTrace();
	    }
		
	}
	
	@Override
	public void setFromTextContent(String text) {
		this.text = text;
	}
	
	public String toString() {
		if (value == null) 
			return "null";
		return value.toString();
	}
}
