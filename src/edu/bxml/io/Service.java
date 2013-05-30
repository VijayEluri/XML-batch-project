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

public class Service extends XmlObjectImpl implements XmlObject  {
	private static Log log = LogFactory.getLog(Service.class);
	String packageName = null;
	final String classExtendedName = "Manager";

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

			String baseDir = container.getDir();
			
			makeService(baseDir, packageName, simpleName, keyFieldType);
		}
	}

	
	public void makeService(String baseDir, 
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

		p.println("");
		p.println("public class " + className + " {");
		p.println("	private static Log log = LogFactory.getLog(" + className + ".class);");
		p.println("	");
		p.println("	public " + className + "() {");
		p.println("		log.debug(\"" + className + " loaded\");");
		p.println("");
		p.println("	private " + simpleName + "Dao dao;");
		p.println("	");
		p.println("	public void set" + simpleName + "Dao(" + simpleName + "Dao dao) {");
		p.println("		this.dao = dao;");
		p.println("	}");
		p.println("");
		p.println("	public " + simpleName + " get" + "(String id) {");
		p.println("		if (id == null || \"\".equals(id)) {");
		p.println("			return null;");
		p.println("		}");
		p.println("		" + simpleName + " temp = dao.get(" + keyFieldType + ".valueOf(id));");
		p.println("		if (temp == null) {");
		p.println("			log.warn(id + \" not found in database.\");");
		p.println("		}");
		p.println("		return temp;");
		p.println("	}");
		p.println("	");
		p.println("	public List<" + simpleName + "> find" + "() {");
		p.println("		return dao.find();");
		p.println("	}");
		p.println("");
		p.println("	public void remove" + "(" + keyFieldType +" id) {");
		p.println("		dao.remove" + "(id);");
		p.println("	}");
		p.println("");
		p.println("	public " + simpleName + " save" + "(" + simpleName + " temp) {");
		p.println("		dao.save(temp);");
		p.println("		return temp;");
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
