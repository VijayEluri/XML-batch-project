package edu.bxml.generator;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XmlObject;

public class Constants extends XmlObject {
	private static Log log = LogFactory.getLog(Constants.class);

	Set<String> items = new HashSet<String>();
	
	public void addItemEnd(Item item) {
		log.debug("add item '" + item.getValue() + "'");
		items.add(item.getValue());
	}
	
	public Set<String> getItems() {
		return items;
	}

	public void setItems(Set<String> items) {
		this.items = items;
	}

	@Override
	public void check() {
		
	}
	@Override
	public void execute() {
		
	}
}
