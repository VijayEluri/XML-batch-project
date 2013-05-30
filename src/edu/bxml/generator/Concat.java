package edu.bxml.generator;

import java.util.ArrayList;
import java.util.List;

import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;

public class Concat extends XmlObjectImpl implements XmlObject {
	private String groupedName;
	private List<String> items = new ArrayList<String>();

	public String getGroupedName() {
		return groupedName;
	}

	public void setGroupedName(String groupedName) {
		this.groupedName = groupedName;
	}

	public void addItem(Item item) {
		items.add(item.getValue());
	}
	
	@Override
	public void check() {
		
	}
	@Override
	public void execute() {
		
	}
}
