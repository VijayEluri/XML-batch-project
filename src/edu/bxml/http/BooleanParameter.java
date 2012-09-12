package edu.bxml.http;

import org.apache.commons.httpclient.methods.PostMethod;

import com.browsexml.core.XMLBuildException;
import com.javalobby.tnt.annotation.attribute;
/**
 * Get an http web page
 * 
 */
@attribute(value = "", required = false)
public class BooleanParameter extends Parameter {

	boolean value = false;
	
	public void setValue(String value) {
		this.value = Boolean.parseBoolean(value);
	}
	
	@Override
	public void execute() throws XMLBuildException {
		Post post = (Post) getAncestorOfType(Post.class);
		PostMethod filePost = post.getPost();
		//filePost.getParams().setBooleanParameter(getFieldValue(), value);

	}
}
