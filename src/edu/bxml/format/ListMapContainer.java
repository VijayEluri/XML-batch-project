package edu.bxml.format;

import java.util.List;
import java.util.Map;

import com.browsexml.core.XMLBuildException;

public interface ListMapContainer {
	public List<Map<String, String>> getListMap() throws XMLBuildException;
}
