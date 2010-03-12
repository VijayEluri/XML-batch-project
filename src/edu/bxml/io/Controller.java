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

public class Controller extends XmlObject {
	private static Log log = LogFactory.getLog(Controller.class);
	String packageName = null;
	final String classExtendedName = "Controller";
	final String formClassExtendedName = "FormController";

	@Override
	public void check() throws XMLBuildException {

	}

	@Override
	public void execute() throws XMLBuildException {
		MasterDetail container = (MasterDetail) this.getParent();

		log.debug("OPEN");

		for (Pojo pojo : container.getPojos()) {
			String simpleName = pojo.getPojo().getSimpleName();
			String keyFieldType = pojo.getKeyFieldType();

			String baseDir = container.getDir();

			makeController(baseDir, packageName, simpleName, keyFieldType);
			makeFormController(baseDir, packageName, simpleName, keyFieldType);
		}
	}

	public void makeFormController(String baseDir, String packageName,
			String simpleName, String keyFieldType) {
		String directory = baseDir + "/" + packageName.replace('.', '/');
		FileOutputStream out = null;
		PrintStream p = null;
		File dir = new File(directory);
		dir.mkdirs();

		String className = simpleName + formClassExtendedName;
		String properName = proper(className);
		String backingName = properName + "Backing";

		File file = new File(dir, className + ".java");

		try {
			out = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			log.debug(e.getMessage());
			e.printStackTrace();
			return;
		}

		p = new PrintStream(out);
		
		p.println("package " + packageName + ";");
		p.println("");

		p.println("");
		p.println("public class " + className
				+ " extends SimpleFormController {");
		p.println("	private static Log log = LogFactory.getLog(" + className
				+ ".class);");
		p.println("	");
		p.println("	public " + className + "() {");
		p.println("		log.debug(\"" + className + " loaded\");");
		p.println("");
		p.println("	private " + properName + "Manager mgr = null;");
		p.println("	private CodeTermManager termsMgr = null;");
		p.println("	");
		p.println("	public " + properName + "FormController() {");
		p.println("		log.debug(\"LOADED \" + this.getClass().getName());");
		p.println("	}");
		p.println("	public void set" + properName + "Manager(" + properName + "Manager " + properName + "Manager) {");
		p.println("		this.mgr = " + properName + "Manager;");
		p.println("	}");
		p.println("	");
		p.println("	@Override");
		p.println("	protected Map referenceData(HttpServletRequest request) {");
		p.println("	}");
		p.println("	");
		p.println("	protected void initBinder(HttpServletRequest request,");
		p.println("			ServletRequestDataBinder binder) {");
		p.println("		");
		p.println("		NumberFormat nf = NumberFormat.getNumberInstance();");
		p.println("		binder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class, nf, true));");
		p.println("		");
		p.println("		SimpleDateFormat dayFormat = Constants.displayDate;");
		p.println("		dayFormat.setLenient(false);");
		p.println("		binder.registerCustomEditor(Date.class, new CustomDateEditor(dayFormat, true));");
		p.println("	}");
		p.println("	");
		p.println("	@Override");
		p.println("	public Object formBackingObject(HttpServletRequest request) {");
		p.println("		" + properName + "FormBacking backing = new " + properName + "FormBacking();");
		p.println("		String id = request.getParameter(\"" + properName + ".payId\");");
		p.println("		if (id != null && !id.trim().equals(\"\")) {");
		p.println("			log.debug(\"id = '\" + id + \"'\");");
		p.println("			" + simpleName + " " + simpleName + " = mgr.get" + properName + "(id);");
		p.println("			backing.set" + properName + "(" + simpleName + ");");
		p.println("		}");
		p.println("		else {");
		p.println("			backing.set" + properName + "(new " + simpleName + "());");
		p.println("		}");
		p.println("		return backing;");
		p.println("	}");
		p.println("	");
		p.println("	public ModelAndView processFormSubmission(HttpServletRequest request, ");
		p.println("			HttpServletResponse response,");
		p.println("			Object command,");
		p.println("			BindException errors) throws Exception {");
		p.println("		log.debug(\"entering 'processFormSubmission' for \" + this.getClass().getName() + \"...\");");
		p.println("		" + properName + "FormBacking backing = (" + properName + "FormBacking) command;");
		p.println("		");
		p.println("		if (request.getParameter(\"cancel\") != null) {");
		p.println("			log.debug(\"CANCEL  command = \" + command);");
		p.println("			return getSuccess(backing);");
		p.println("		}");
		p.println("		");
		p.println("		return  super.processFormSubmission(request, response, command, errors);");
		p.println("	}");
		p.println("	");
		p.println("	@Override");
		p.println("	 public ModelAndView onSubmit(");
		p.println("		        HttpServletRequest request,");
		p.println("		        HttpServletResponse response,");
		p.println("		        Object command,");
		p.println("		        BindException errors) {");
		p.println("		if (log.isDebugEnabled()) {");
		p.println("			log.debug(\"entering 'onSubmit' for \" + this.getClass().getName() + \"...\");");
		p.println("		}");
		p.println("		" + properName + "FormBacking backing = (" + properName + "FormBacking) command;");
		p.println("		Boolean success = true;");
		p.println("		if (request.getParameter(\"delete\") != null) {");
		p.println("			log.debug(\"ON SUBMIT delete \" + backing.get" + properName + "().getPayId());");
		p.println("			try {");
		p.println("				mgr.remove" + properName + "(backing.get" + properName + "().getPayId());");
		p.println("			} catch (Exception e) {");
		p.println("				success = false;");
		p.println("				e.printStackTrace();");
		p.println("				request.getSession().setAttribute(\"message\",");
		p.println("						getMessageSourceAccessor().getMessage(\"" + properName + ".timesheetsOn" + properName + "\"));");
		p.println("			}");
		p.println("			if (success)");
		p.println("				request.getSession().setAttribute(\"message\",");
		p.println("						getMessageSourceAccessor().getMessage(\"" + properName + ".deleted\"));");
		p.println("		}");
		p.println("		else {");
		p.println("			try {");
		p.println("				log.debug(\"pay period = \" + Constants.toReflexString(backing.get" + properName + "()));");
		p.println("				mgr.save" + properName + "(backing.get" + properName + "());");
		p.println("			} catch (Exception e) {");
		p.println("				success = false;");
		p.println("				e.printStackTrace();");
		p.println("				request.getSession().setAttribute(\"message\", e.getMessage());");
		p.println("			}");
		p.println("			if (success)");
		p.println("				request.getSession().setAttribute(\"message\",");
		p.println("					getMessageSourceAccessor().getMessage(\"" + properName + ".saved\"));");
		p.println("		}");
		p.println("		");
		p.println("		return getSuccess(backing);");
		p.println("	}");
		p.println("	");
		p.println("	public ModelAndView getSuccess(" + properName + "FormBacking backing) {");
		p.println("		return new ModelAndView(getSuccessView())");
		p.println("			.addObject(\"" + properName + "Backing\", parentBacking);");
		p.println("	}");
		p.println("");
		p.println("	public static class " + properName + "FormBacking extends BaseObject {");
		p.println("		" + simpleName + " " + properName + " = new " + simpleName + "();");
		p.println("		String save = \"\";");
		p.println("		");
		p.println("		public " + simpleName + " get" + properName + "() {");
		p.println("			return " + properName + ";");
		p.println("		}");
		p.println("");
		p.println("		public void set" + properName + "(" + simpleName + " " + properName + ") {");
		p.println("			if (" + properName + " == null) {");
		p.println("				log.debug(\"TRYING TO SET NULL\");");
		p.println("				new Exception().printStackTrace();");
		p.println("				return;");
		p.println("			}");
		p.println("			this." + properName + " = " + properName + ";");
		p.println("		}");
		p.println("");
		p.println("		public String getSave() {");
		p.println("			return save;");
		p.println("		}");
		p.println("");
		p.println("		public void setSave(String save) {");
		p.println("			this.save = save;");
		p.println("		}");
		p.println("");
		p.println("	}");
		p.println("	");
		p.println("	public static class " + properName + "FormControllerValidator implements Validator {");
		p.println("		private static Log log = LogFactory.getLog(" + properName + "FormControllerValidator.class);");
		p.println("		private " + properName + "Manager mgr = null;");
		p.println("		");
		p.println("		public void set" + properName + "Manager(" + properName + "Manager " + properName + "Manager) {");
		p.println("			this.mgr = " + properName + "Manager;");
		p.println("		}");
		p.println("		");
		p.println("		@Override");
		p.println("		public boolean supports(Class theClass) {");
		p.println("			return " + properName + "FormBacking.class.equals(theClass);");
		p.println("		}");
		p.println("");
		p.println("		@Override");
		p.println("		public void validate(Object obj, Errors errors) {");
		p.println("			" + properName + "FormBacking " + properName + "Backing = (" + properName + "FormBacking) obj;");
		p.println("			" + simpleName + " " + properName + " = " + properName + "Backing.get" + properName + "();");
		p.println("			");
		p.println("			// only validate on save");
		p.println("			if (\"\".equals(" + properName + "Backing.getSave()))");
		p.println("					return;");
		p.println("		}");
		p.println("	}");
		p.println("");

		p.println("}");
		
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void makeController(String baseDir, String packageName,
			String simpleName, String keyFieldType) {
		String directory = baseDir + "/" + packageName.replace('.', '/');
		FileOutputStream out = null;
		PrintStream p = null;
		File dir = new File(directory);
		dir.mkdirs();

		String className = simpleName + classExtendedName;
		String properName = proper(className);
		String backingName = properName + "Backing";

		File file = new File(dir, className + ".java");

		try {
			out = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			log.debug(e.getMessage());
			e.printStackTrace();
			return;
		}

		p = new PrintStream(out);

		p.println("package " + packageName + ";");
		p.println("");

		p.println("");
		p.println("public class " + className
				+ " extends SimpleFormController {");
		p.println("	private static Log log = LogFactory.getLog(" + className
				+ ".class);");
		p.println(simpleName + "Manager mgr = null;");
		p.println("	");
		p.println("	public " + className + "() {");
		p.println("		log.debug(\"" + className + " loaded\");");
		p.println("");
		p.println("");
		p.println("	public void set" + simpleName + "Manager(" + simpleName + "Manager tempManager) {");
		p.println("		this.mgr = tempManager;");
		p.println("	}");
		p.println("	");
		p.println("	protected void initBinder(HttpServletRequest request,");
		p.println("			ServletRequestDataBinder binder) {");
		p.println("");
		p.println("		SimpleDateFormat dayFormat = Constants.displayDate;");
		p.println("		dayFormat.setLenient(false);");
		p.println("		binder.registerCustomEditor(Date.class, new CustomDateEditor(dayFormat, true));");
		p.println("	}");
		p.println("	");
		p.println("	@Override");
		p.println("	public Object formBackingObject(HttpServletRequest request) {");
		p.println("		" + simpleName + "Backing backing = new " + simpleName + "Backing();");
		p.println("	");
		p.println("		// for each backing item");
		p.println("		String year = request.getParameter(\"year\");");
		p.println("		backing.setYear(year);");
		p.println("");
		p.println("		backing.setList(mgr.get" + simpleName + "s());");
		p.println("		return backing;");
		p.println("	}");
		p.println("	");
		p.println("	@Override");
		p.println("	public ModelAndView handleRequest(HttpServletRequest request,");
		p.println("			HttpServletResponse arg1) throws Exception {");
		p.println("		");
		p.println("		if (log.isDebugEnabled()) {");
		p.println("			log.debug(\"entering 'handleRequest' for \" + this.getClass().getName() + \"...\");");
		p.println("		}");
		p.println("		" + simpleName + "Backing backing = (" + simpleName + "Backing) this.getCommand(request);");
		p.println("");
		p.println("		// get parameters");
		p.println("		backing.setList(mgr.get" + simpleName + "s());");
		p.println("		");
		p.println("		return new ModelAndView(\"" + properName + "\", \"" + backingName + "\", backing);");
		p.println("	}");
		p.println("	");
		p.println("	public static class " + simpleName + "Backing extends BaseObject {");
		p.println("		// Add all elements of form ojbect and getters/setters");
		p.println("		List<" + simpleName + "> list = null;");
		p.println("");
		p.println("		public " + simpleName + "Backing() {");
		p.println("			");
		p.println("		}");
		p.println("		");
		p.println("		public List<" + simpleName + "> getList() {");
		p.println("			return list;");
		p.println("		}");
		p.println("");
		p.println("		public void setList(List<" + simpleName + "> list) {");
		p.println("			this.list = list;");
		p.println("		}");
		p.println("	}");
		p.println("	");
		p.println("	public static class " + simpleName + "ControllerValidator implements Validator {");
		p.println("		private static Log log = LogFactory.getLog(" + simpleName + "ControllerValidator.class);");
		p.println("		private " + simpleName + "Manager mgr = null;");
		p.println("		");
		p.println("		public void set" + simpleName + "Manager(" + simpleName + "Manager tempManager) {");
		p.println("			this.mgr = tempManager;");
		p.println("		}");
		p.println("		");
		p.println("		@Override");
		p.println("		public boolean supports(Class theClass) {");
		p.println("			return " + simpleName + "Backing.class.equals(theClass);");
		p.println("		}");
		p.println("");
		p.println("		@Override");
		p.println("		public void validate(Object obj, Errors errors) {");
		p.println("");
		p.println("");
		p.println("		}");
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
