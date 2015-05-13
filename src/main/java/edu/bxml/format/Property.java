package edu.bxml.format;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.javalobby.tnt.annotation.attribute;
/**
 * Declare a property (variable).  The property is created as soon as the end-tag
 * is read.
 * @author ritcheyg
 *
 */
@attribute(value = "", required = true)
public class Property extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(Property.class);

	public String value = "";
	private String list[] = null;
	private String index = null;

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String[] getList() {
		return list;
	}

	public void setList(String list) {
		setList(list.split(","));
	}
	
	public void setList(String[] list) {
		this.list = list;
	}

	public Connection getConnection() {
		return (Connection)this.getParent();
	}
	
	/**
	 * check that all the fields are set correctly, especially
	 * required fields.  Called when the end-tag of the 
	 * element has been reached and processed.
	 */
	public void check() throws XMLBuildException {
		Map m = getSymbolTable(); 
		if (m.get("_#env") != null) {
			log.debug("env not null");
			Object value = ((Map)(m.get("_#env"))).get(getName());
			if (value != null) {
				if (value instanceof Object[]) {
					value = ((Object[]) value)[0];
				}
				log.debug("value is a " + value.getClass().getName());
				this.value = value.toString();
				log.debug("value set from env to " + this.value);
			}
		}
		else {
			log.debug("env is null");
		}
		m.put("_#" + getName(), value);
		log.debug("put " + getName() + " " + value);
		log.debug("st = " + m);
	}
	/**
	 * Called after complete parsing of XML document
	 * to evaluate the document.
	 */
	public void execute() {
		log.debug("EXECUTE PROPERTY " + this.getName() + " value = " + value);
		if (list != null) {
			for (int n = 0; n < list.length; n++) {
				if (list[n].equals(value)) {
					value = n+"";
				}
			}
		}
	}

	/**
	 * set the value of the variable.  The 'name' property of the
	 * variable can be used to reference its value elsewhere using 
	 * the format ${name}
	 * @param text
	 */
	@attribute(value = "", required = false, defaultValue="an empty string will be used by default")
	public void setText(String text) {
		if (text != null) {
			log.debug("value = " + text);
			value = text;
		}
	}
	
	public String getText() {
		log.debug("returning value = " + value);
		return value;
	}
	/**
	 * Retrieve the text that was contained inside the tag
	 */
	public void setFromTextContent(String text) {
		if (text != null)
			value = text;
	}
	
	public String toString() {
		log.debug("returning value = " + value);
		return value;
	}
}
