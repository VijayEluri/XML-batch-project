package edu.bxml.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;

public class Filter extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(Filter.class);
	private String searchText;
	
	public String getSearchTextType() {
		for (Interface i: interfaces) {
			if (searchText != null && searchText.equals(i.getName())) {
				Field f = i.getFields().get(0);
				return f.getJavaType();
			}
		}
		return null;
	}
	public String getSearchText() {
		return searchText;
	}
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	@Override
	public void check() {

	}
	@Override
	public void execute() {
		
	}
	
	/**
	 * Given a database field name return the interface name to filter on that field
	 * @param fieldName
	 * @return
	 */
	public Map<String, String> getFilterName(String fieldName) {
		for (Interface i: interfaces) {
			int place = 0;
			for (Field f: i.getFields()) {
				if (f.getFieldName() != null && f.getFieldName().equals(fieldName)) {
					Map<String, String> ret = new HashMap<String, String>();
					ret.put("filterName", i.getName());
					log.debug("interface " + i.getName() + " size = " + i.getFields().size());
					if (i.getFields().size() > 1) {
						ret.put("index", "" + place);
						ret.put("separator", i.getSeparator());
						
						if (i.getOperator() == null)
							log.error("operator for filter '" + getName() + "', interface '" + i.getName() + "', should not be null");
						if ("like".equals(i.getOperator()))
							ret.put("wildcard", "%");
						else
							ret.put("wildcard", "");
					}
					else {
						ret.put("index", "0");
						ret.put("separator", "");
						ret.put("wildcard", "");
					}
					
					return ret;
				}
				place++;
			}
		}
		return null;
	}
	
	List<Interface> interfaces = new ArrayList<Interface>();
	
	public List<Interface> getInterfaces() {
		return interfaces;
	}
	public void setInterfaces(List<Interface> interfaces) {
		this.interfaces = interfaces;
	}
	public void addInterface(Interface i) {
		interfaces.add(i);
	}
}
