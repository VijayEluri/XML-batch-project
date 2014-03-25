package edu.bxml.format;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.io.Select;
import edu.bxml.io.TypeTemplate;

/**
 * Allow regular expression matching and replacement by callling
 * java.lang.String replaceAll(expression, replacement) on the contents of a
 * field of data coming out of the database before it is formatted. This can be
 * used to strip out unexpected delimiter characcters embedded in data or remove
 * extraneous spaces from names or addresses.
 * 
 */
@attribute(value = "", required = true)
public class Replace extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(Replace.class);

	public List<TypeTemplate> typeList = new ArrayList<TypeTemplate>();
	private Boolean typed = false;
	public List<Select> list = new ArrayList<Select>();
	private String expression = null;
	private String replacement = null;
	String text = null;

	public List<Replace> replacements = new ArrayList<Replace>();

	/**
	 * let the parent identify itself to this object
	 */
	public void setParent(XmlObject parent) {

	}

	public String getText() {
		return text;
	}

	/**
	 * Regular expression to match contents of field. The expression is used by
	 * java.lang.String replaceAll(expression, replacement), so any valid
	 * 'replaceAll' expression will work here.
	 */
	@attribute(value = "", required = true)
	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getExpression() {
		return expression;
	}

	/**
	 * String to use as a replacement. The expression is used by
	 * java.lang.String replaceAll(expression, replacement), so any valid
	 * 'replaceAll' replacement will work here.
	 */
	@attribute(value = "", required = true)
	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}

	public String getReplacement() throws XMLBuildException {
		String ret = text;
		if (ret == null) 
			ret = XmlParser.processAttributes(this, replacement);
		
		for (Replace r : replacements) {
			log.debug(r.getExpression());
			log.debug(r.getReplacement());
			ret.replaceAll(r.getExpression(), r.getReplacement());
		}
		return ret;
	}

	public String getReplacement(String pre, String post)
			throws XMLBuildException {
		String ret = pre + XmlParser.processAttributes(this, replacement)
				+ post;
		for (Replace r : replacements) {
			log.debug("REPLACE replace = " + r.getExpression() + " with "
					+ r.getReplacement());
			ret = ret.replaceAll(r.getExpression(), r.getReplacement());
		}
		return ret;
	}

	public String getReplacement(String replacement) throws XMLBuildException {
		String ret = XmlParser.processAttributes(this, replacement);
		for (Replace r : replacements) {
			log.debug("REPLACE replace = " + r.getExpression() + " with "
					+ r.getReplacement());
			ret = ret.replaceAll(r.getExpression(), r.getReplacement());
		}
		return ret;
	}

	/**
	 * check that all the fields are set correctly, especially required fields.
	 * Called when the end-tag of the element has been reached and processed.
	 */
	public void check() throws XMLBuildException {
		if (expression == null) {
			throw new XMLBuildException("You must set expression", this);
		}
		if (text == null && replacement == null) {
			throw new XMLBuildException(
					"You must set either replacement or text content", this);
		}
		if (text != null && replacement != null) {
			throw new XMLBuildException(
					"Replacement and text content can't both be set", this);
		}

	}

	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() throws XMLBuildException {
		if (text != null) {
			String rep = XmlParser.processAttributes(this, replacement);
			log.debug("replacement text = " + text);
			text = text.replaceAll(expression, rep);
		}
	}

	/**
	 * Retrieve the text that was contained inside the tag
	 */
	public void setText(String text) {
		this.text = text;
	}

	public void setFromTextContent(String text) {
		this.text = text;
	}

	public List<Select> getList() {
		return list;
	}

	public void setList(String list) throws XMLBuildException {
		log.debug("list = " + list);
		this.list = (List<Select>) XmlParser.getAttributeFromPath(this, list);
	}

	public Boolean getTyped() {
		return typed;
	}

	public void setTyped(String typed) {
		this.typed = Boolean.parseBoolean(typed);
	}

	public void addReplace(Replace replace) {
		replacements.add(replace);
	}

	public void addTypeTemplate(TypeTemplate type) {
		typeList.add(type);
	}
	
	public TypeTemplate getTypeTemplate(String type) {
		if (type == null) 
			return null;
		for (TypeTemplate tt: typeList) {
			if (type.equals(tt.getType()))
				return tt;
		}
		return null;
	}
}
