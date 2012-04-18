package edu.bxml.jetty;

import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.io.FilterAJ;

public class New extends FilterAJ {
	private static Log log = LogFactory.getLog(New.class);
	
	String id;
	String clazz;

	List<Arg> args = new ArrayList<Arg>();
	
	public String getClazz() {
		return clazz;
	}

	public void setClass(String clazz) {
		this.clazz = clazz;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void check() throws XMLBuildException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute() throws XMLBuildException {
		// TODO Auto-generated method stub
		
	}
	
	@attribute(value = "", required = true)
	public void addSetEnd(Set set) {
		try {
			System.err.println("Set name=" + set.getName() + ", value=" +   
					XmlParser.processMacros(this.getSymbolTable(), XmlParser.replacePoundMacros(set.getValue())));
		} catch (XMLBuildException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void createSubContext(Context c) {
		try {
			Arg firstParameter = args.get(0);
			String[] contextNames = null;
			try {
				contextNames = firstParameter.getValue().split("/");
			} catch (XMLBuildException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.debug("createSubContext firstParameter = " + contextNames[0] + "/" + contextNames[1]);
			Arg x = args.get(1);
			DataSource y = x.getDataSource();
			log.debug("c = " + c);
			log.debug("y = " + y);
			log.debug("con[0]  = " + contextNames[0]);
			log.debug("con[1]  = " + contextNames[1]);
			Context z = c.createSubcontext(contextNames[0]);
			log.debug("Z = " + z);
			z.bind(contextNames[1], y);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public void setFromTextContent(String text) {
		if (text != null) {
			log.debug("New set Value from Text = " + text);	
			setValue(text);
		}
	}
	
	public void addArg(Arg a) {
		args.add(a);
	}
}
