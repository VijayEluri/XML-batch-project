package edu.bxml.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;

public class DaoHibernate extends XmlObjectImpl implements XmlObject  {
	private static Log log = LogFactory.getLog(DaoHibernate.class);
	String packageName = null;
	final String classExtendedName = "DaoHibernate";
	final String interfaceExtendedName = "Dao";

	@Override
	public void check() throws XMLBuildException {

		
	}

	@Override
	public void execute() throws XMLBuildException {
		MasterDetail container = (MasterDetail) this.getParent();
		
		log.debug("OPEN");

		for (Pojo pojo: container.getPojos()) {
			String simpleName = pojo.getPojo().getSimpleName();
			String keyFieldType = pojo.getKeyFieldType();
			
			String directory = container.getDir() + "/" + 
				packageName.replace('.', '/');
			
			String implPackage = packageName + ".hibernate";
			String baseDir = container.getDir();
			
			makeImpl(baseDir, implPackage, simpleName, keyFieldType);
			makeInterface(baseDir, packageName, simpleName, keyFieldType);
		}
	}

	public void makeInterface(String baseDir, String packageName, 
			String simpleName, String keyFieldType) {
		String directory = baseDir + "/" + 
		packageName.replace('.', '/'); 
	FileOutputStream out = null;
	PrintStream p = null;
	File dir = new File(directory);
	dir.mkdirs();
	
	String className = simpleName + interfaceExtendedName;
	
	File file = new File(dir, className + ".java");
	
	try {
		out = new FileOutputStream(file);
	} catch (FileNotFoundException e) {
		log.debug(e.getMessage());
		e.printStackTrace();
		return;
	}

	p = new PrintStream( out );
	
	p.println("package " + packageName + ";");
	p.println("");
	p.println("");
	p.println("import org.browsexml.timesheetjob.model.PayPer;");
	p.println("");
	p.println("public interface " + simpleName + "Dao extends Dao{");
	p.println("");

		p.println("");
		p.println("	public void remove" + "(Integer id);");
		p.println("	public void save(" + simpleName + " period); ");
		p.println("	public " + simpleName + " get(Integer id);");
		p.println("	public List<" + simpleName + "> find();");
		p.println("");


		p.println("}");
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void makeImpl(String baseDir, 
			String packageName, String simpleName, String keyFieldType) {
		String directory = baseDir + "/" + 
			packageName.replace('.', '/'); 
		FileOutputStream out = null;
		PrintStream p = null;
		File dir = new File(directory);
		dir.mkdirs();
		
		String className = simpleName + classExtendedName;
		
		File file = new File(dir, className + ".java");
		
		try {
			out = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			log.debug(e.getMessage());
			e.printStackTrace();
			return;
		}

		p = new PrintStream( out );

		p.println("package " + packageName + ";");
		p.println("");
		p.println("import org.springframework.orm.hibernate3.support.HibernateDaoSupport;");
		p.println("");
		p.println("public class " + className + 
				" extends HibernateDaoSupport implements " + simpleName + interfaceExtendedName + " {");
		p.println("	private static Log log = LogFactory.getLog(" + className + ".class);");
		p.println("	");
		p.println("	public " + className + "() {");
		p.println("		log.debug(\"" + className + " loaded\");");
		p.println("	}");
		p.println("	");
		p.println("	public void remove" + "(" + keyFieldType + " id) {");
		p.println("		Object payperiod = getHibernateTemplate().load(" + simpleName + ".class, id);");
		p.println("		getHibernateTemplate().delete(payperiod);");
		p.println("	}");
		p.println("	");
		p.println("	public void save(" + simpleName + " period) {");
		p.println("		getHibernateTemplate().save(period);");
		p.println("	}");
		p.println("");
		p.println("	public " + simpleName + " get(" + keyFieldType + " id) {");
		p.println("		return (" + simpleName + ") getHibernateTemplate().get(" + simpleName + ".class, id);");
		p.println("	}");
		p.println("	");
		p.println("	public List<" + simpleName + "> find() {");
		p.println("		return ( List<" + simpleName + ">) getHibernateTemplate().find(");
		p.println("				\"from " + simpleName + " p \" +");
		p.println("				\"\", ");
		p.println("				new Object[]{});");
		p.println("	}");
		p.println("");

		p.println("}");
		
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setPackage(String packageName) {
		this.packageName = packageName;
	}

}
