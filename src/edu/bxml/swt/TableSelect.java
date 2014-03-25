package edu.bxml.swt;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.javalobby.tnt.annotation.attribute;

/**
 * Select rows of a table 
 */
@attribute(value = "", required = false)
public class TableSelect extends XmlObjectImpl implements XmlObject {
	
	private int index = -1;
	
	@Override
	public void execute() throws XMLBuildException {

	}
	@Override
	public void check() throws XMLBuildException {
		if (index < 0) {
			throw new XMLBuildException("The index must be set", this);
		}
	}
	
	/**
	 * get the index number of the row to select
	 * @return
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * The line number of the row to select
	 * @return
	 */
	@attribute(value = "", required = true)
	public void setIndex(Integer index) {
		this.index = index;
	}
	public void setIndex(String index) throws XMLBuildException {
		setIndex(Integer.parseInt(index));
	}
}
