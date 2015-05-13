package edu.bxml.jetty;

import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;

import edu.bxml.io.FilterAJ;
import edu.bxml.io.FilterAJImpl;

public class Configure extends FilterAJImpl implements FilterAJ {
	private static Log log = LogFactory.getLog(Configure.class);
	
	String clazz;
	List<New> resourceList = new ArrayList<New>();

	public String getClazz() {
		return clazz;
	}

	public void setClass(String clazz) {
		this.clazz = clazz;
	}

	@Override
	public void check() throws XMLBuildException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute() throws XMLBuildException {
		// TODO Auto-generated method stub
		
	}

	public void getContext(Context initialContext) {
		log.debug("initialContext = " + initialContext);
		Context c = null;
		try {
			c = initialContext.createSubcontext("java:comp/env");
		} catch (NamingException e) {
			try {
				log.debug(e.getMessage());
				e.printStackTrace();
				c = (Context) initialContext.lookup("java:comp/env");
			} catch (NamingException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		for (New resource: resourceList) {
			resource.createSubContext(c);
		}
	}
	
	public void addNew(New n) {
		resourceList.add(n);
	}
	
}
