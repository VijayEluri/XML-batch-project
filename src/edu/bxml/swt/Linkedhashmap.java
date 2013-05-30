package edu.bxml.swt;

import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.http.Get;
import edu.bxml.http.Post;

/**
 * 
 */
@attribute(value = "", required = false)
public class Linkedhashmap extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(Linkedhashmap.class);
	Get get = null;
	Post post = null;
	Item item = null;
	LinkedHashMap keys = new LinkedHashMap();
	LinkedHashMap values = new LinkedHashMap();
	private Print print = null;
	
	public String toString() {
		return keys.toString();
	}
	
	public LinkedHashMap getKeys() {
		return keys;
	}

	@Override
	public void execute() throws XMLBuildException {
		if (get != null)
			get.execute();
		if (post != null)
			post.execute();
		if (print != null) 
			print.execute();
	}
	@Override
	public void check() throws XMLBuildException {
		if (this.item != null) {
			keys.put(this.item.getKey(), this.item.getValue());
			values.put(this.item.getValue(), this.item.getKey());
		}
		this.item = null;
	}
	
	public void addGet(Get get) {
		this.get = get;
	}
	
	public void addPost(Post post) {
		this.post = post;
	}
	
	public void addItem(Item item) {
		log.debug("lhm addItem " + item);
		if (this.item != null) {
			keys.put(this.item.getKey(), this.item.getValue());
			values.put(this.item.getValue(), this.item.getKey());
		}
		this.item = item;
	}
	
	public void addPrint(edu.bxml.swt.Print p) {
		this.print = p;
	}
	
}
