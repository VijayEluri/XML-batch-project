package edu.bxml.jetty;

import java.sql.SQLException;
import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;

import edu.bxml.io.FilterAJ;

public class Arg extends FilterAJ {
	private static Log log = LogFactory.getLog(Arg.class);

	HashMap parameters = new HashMap();

	@Override
	public void check() throws XMLBuildException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute() throws XMLBuildException {
		// TODO Auto-generated method stub
		
	}

	public DataSource getDataSource() {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName((String)parameters.get("driverClassName"));
		ds.setUrl((String)parameters.get("url"));
		ds.setUsername((String)parameters.get("username"));
		ds.setPassword((String)parameters.get("password"));
		return (DataSource) ds;
	}
	
	public void addNew(New n) {
		// Gives class name to create
		// and to apply 'Set's to.
		// for us, this will always be a datasource.
	}
	
	public void addSet(Set s) {
		try {
			parameters.put(s.getName(), s.getValue());
		} catch (XMLBuildException e) {
			e.printStackTrace();
		}
	}
	
	public void setFromTextContent(String text) {
		if (text != null) {
			log.debug("Arg set Value from Text = " + text);	
			setValue(text);
		}
	}
}
