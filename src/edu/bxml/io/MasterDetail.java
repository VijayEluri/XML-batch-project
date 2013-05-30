package edu.bxml.io;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;

public class MasterDetail  extends XmlObjectImpl implements XmlObject  {
	String pojoName = null;
	String dir = ".";
	List<XmlObject> children = new ArrayList<XmlObject>();
	List<Pojo>  pojos = new ArrayList<Pojo>();
	
	private static Log log = LogFactory.getLog(MasterDetail.class);
	@Override
	public void check() throws XMLBuildException {
		
		
	}

	@Override
	public void execute() throws XMLBuildException {
		log.debug("HERE");
		for (XmlObject pojo: pojos) {
			pojo.execute();
		}

		for (XmlObject child: children) {
			child.execute();
		}
	}
	
	public void setDir(String dir) {
		this.dir = dir;
	}
	
	public void addPojo(Pojo pojo) {
		pojos.add(pojo);
	}
	
	public void addDaoHibernate(DaoHibernate dao) {
		children.add(dao);
	}
	
	public void addController(Controller controller) {
		children.add(controller);
	}
	
	public void addService(Service service) {
		children.add(service);
	}

	public List<Pojo> getPojos() {
		return pojos;
	}

	public String getDir() {
		return dir;
	}

}
