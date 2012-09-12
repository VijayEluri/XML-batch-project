package edu.bxml.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;


import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;
/**
 * Get an http web page
 * 
 */
@attribute(value = "", required = false)
public class Call extends XmlObject {
	private URL url = null;
	private String uri = null;
	private String method = null;
	private String soapAction = "";
	
	Vector<Parameter> params = new Vector<Parameter>();

	@Override
	public void execute() throws XMLBuildException {
/*
	    // Build the Call object
	    org.apache.soap.rpc.Call call = new org.apache.soap.rpc.Call(  );

	    call.setTargetObjectURI(uri);
	    call.setMethodName(method);
	    call.setEncodingStyleURI(Constants.NS_URI_SOAP_ENC);

	    // Set up parameters

	    Vector<org.apache.soap.rpc.Parameter> callParams = new Vector<org.apache.soap.rpc.Parameter>(  );
	    for (Enumeration e = params.elements(); e.hasMoreElements();) {
	    	Parameter p = (Parameter) e.nextElement();
	    	org.apache.soap.rpc.Parameter rpcParam = new org.apache.soap.rpc.Parameter(
    				p.getKey(), p.getType(), p.getValue(), p.getEncodingStyle());
	    	callParams.addElement(rpcParam);
	    }
	    
	    call.setParams(callParams);
	    
	    Header h = new Header();
	   // h.setHeaderEntries(arg0)
	    log.debug(call.buildEnvelope());
	    //h.setAttribute(arg0, arg1);

	    log.debug(call);
	    // Invoke the call
	    Response response;
	    try {
			response = call.invoke(url, soapAction);
		} catch (SOAPException e) {
			throw new XMLBuildException(e.getMessage());
		}
	    
	    log.debug("RESPONSE: " + response);
	    if (!response.generatedFault(  )) {
	        log.debug("Successful call to " + method + ".");
	        org.apache.soap.rpc.Parameter returnValue = response.getReturnValue(  );
	        log.debug("return = " + returnValue);
	    } else {
	        Fault fault = response.getFault(  );
	        log.debug("Error encountered: " + fault.getFaultString(  ));
	    }
	    */
	}
	@Override
	public void check() throws XMLBuildException {
		if (url == null) {
			throw new XMLBuildException("the url must be set");
		}
		if (uri == null) {
			throw new XMLBuildException("the uri must be set");
		}
		if (method == null) {
			throw new XMLBuildException("the method must be set");
		}
	}
	
	/**
	 *  The URL of the page to get
	 */
	@attribute(value = "", required = true)
	public void setUrl(String url) throws XMLBuildException {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			throw new XMLBuildException(e.getMessage());
		}
	}
	
	public void setSoapAction(String action) {
		this.soapAction = action;
	}
	
	/**
	 *  The URI of the call
	 */
	@attribute(value = "", required = true)
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	/**
	 *  The Method of the call
	 */
	@attribute(value = "", required = true)
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 *  Add a key/value pair as a parameter to the call
	 */
	@attribute(value = "", required = false)
    public void addParameter(Parameter p) {
    	params.addElement(p);
    }
}
