package edu.bxml.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

/**
 * Support connecting to a web server
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Http extends XmlObject {
	private static Log log = LogFactory.getLog(Http.class);
	private HttpClient client;
	private boolean mutithreaded = false;
	private int timeout = 30000;
	private String policy = CookiePolicy.RFC_2109;
	private Vector<XmlObject> addresses = new Vector<XmlObject>();
	private Vector<XmlObject> generic = new Vector<XmlObject>();
	private String encode = "UTF-8";
	private String root = null;
	
	/*
    client = new HttpClient(new MultiThreadedHttpConnectionManager());
    client.getHttpConnectionManager().
        getParams().setConnectionTimeout(30000);

    */
	
	/**
	 *  Allow the client to run in the background
	 */
	@attribute(value = "", required = true)
	public void setMultiThreaded(Boolean text) {
		mutithreaded = text;
	}
	public void setMultiThreaded(String text) throws XMLBuildException {
		try {
			setMultiThreaded(Boolean.parseBoolean(text));
		} catch (RuntimeException e) {
			throw new XMLBuildException(e.getMessage());
		}
	}
	
	/**
	 *  The maximum time the client will wait
	 */
	@attribute(value = "", required = true)
	public void setTimeout(Integer text) {
		timeout = text;
	}
	public void setTimeout(String text) throws XMLBuildException {
		try {
			setTimeout(Integer.parseInt(text));
		} catch (NumberFormatException e) {
			throw new XMLBuildException(e.getMessage());
		}
	}
    
	/**
	 *  Set the string encoding charater set
	 */
	@attribute(value = "", required = false, defaultValue="UTF-8")
	public void setEncode(String encode) {
		this.encode = encode;
	}
    
	/**
	 *  Get the string encoding charater set
	 */
	public String getEncoded(String value) throws XMLBuildException {
		String ret = null;
		if (value == null) 
			value = "";
		try {
			ret = URLEncoder.encode(value, encode);
		} catch (UnsupportedEncodingException e) {
			throw new XMLBuildException(e.getMessage());
		}
		return ret;
	}
	
	
	/**
	 *  File containing certificates of trusted sites
	 */
	@attribute(value = "", required = false)
	public void setTrustStore(String file) {
		System.setProperty("javax.net.ssl.trustStore", file);
	}
	
	/**
	 *  Http method 'Get' to the web site
	 */
	@attribute(value = "", required = false)
	public void addGet(Get g) {
		addresses.add(g);
	}
	
	/**
	 *  Http method 'Post' to the web site
	 */
	@attribute(value = "", required = false)
	public void addPost(Post p) {
		addresses.add(p);
	}
	
	@Override
	public void check() throws XMLBuildException {
		/*Protocol.registerProtocol("https", new Protocol("https",
				new EasySSLProtocolSocketFactory(), 443));*/
		//System.setProperty("javax.net.ssl.keyStore", "/jssecacerts");
		//System.setProperty("javax.net.ssl.keyStorePassword", "changeit");
		//System.setProperty("javax.net.ssl.keyStoreType", "jks"); 
		
		//log.debug("keystore: " + System.getProperty("javax.net.ssl.keyStore"));

		

	}

	@Override
	public void execute() throws XMLBuildException {
        for (Enumeration e = addresses.elements();e.hasMoreElements();) {
        	XmlObject g = (XmlObject)e.nextElement();
        	g.execute();
        }
        for (XmlObject g: generic) {
        	g.execute();
        }
	}
	
	/**
	 *  Set the cookie policy.
	 */
	@attribute(value = "", required = false, defaultValue = "RFC_2109")
	public void setCookiePolity(String policy) throws XMLBuildException {
		Class c = CookiePolicy.class;
		try {
			this.policy = (String) c.getField(policy).get(c);
		} catch (IllegalArgumentException e) {
			throw new XMLBuildException(e.getMessage());
		} catch (SecurityException e) {
			throw new XMLBuildException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new XMLBuildException(e.getMessage());
		} catch (NoSuchFieldException e) {
			throw new XMLBuildException(e.getMessage());
		}
	}
	
	public HttpClient getClient() {
		return client;
	}
	
	public void init(XmlObject parent) throws XMLBuildException {
		super.init(parent);
		if (mutithreaded) {
			log.debug("muti true");
			client = new HttpClient(new MultiThreadedHttpConnectionManager());
		}
		else {
			log.debug("muti false");
			client = new HttpClient();
		}
		
		HttpState initialState = new HttpState();

		client.getParams().setCookiePolicy(policy);
		this.client.setState(initialState);
		
		HttpConnectionManager m = client.getHttpConnectionManager();
		
		m.getParams().setConnectionTimeout(timeout);
	}
	
	public String toString() {
		return getName();
	}
	
	/**
	 *  Call a soap method 
	 */
	@attribute(value = "", required = false)
	public void addCall(Call c) {
		addresses.add(c);
	}
	
	public void addXmlObject(XmlObject p) {
		log.debug("add GENERIC object " + p);
		generic.add(p);
	}
	
	public void addInstallCert(InstallCert c) {
		addresses.add(c);
	}
	
	public String getRoot() {
		return root;
	}
	
	public void setRoot(String url) {
		//  http://www.nnn.com:8080/timesheet
		new Exception().printStackTrace();
		log.debug("url = " + url);
		 Pattern p = Pattern.compile("\\A([^:]*://[^:/]+(?::[0-9]+)?/[^/]*)(?:/.*)?\\Z");
		 Matcher m = p.matcher(url);
		 boolean b = m.matches();
		 if (b) {
			 root = m.group(1);
			 log.debug("root = " + root);
			 this.getSymbolTable().put("_#root", root);
		 }
	}
}
