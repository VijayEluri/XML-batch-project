package edu.bxml.io;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;

/**
 * A class must extend filter to participate in a pipeline.  Filter can
 * be used stand alone as a basic copy from System.in to System.out.
 * @author geoff.ritchey
 *
 */
@attribute(value = "", required = true)
public class Filter extends XmlObjectImpl implements XmlObject, Runnable {
	private static Log log = LogFactory.getLog(Filter.class);
	protected InputStream in;
	protected OutputStream out;
	protected String text = null;

	protected String file = null;
	protected String dir = null;
	protected String toFile = null;
	protected String toDir = null;
	protected String archive = null;
	protected File currentFile = null;

	protected List<Filter> filters = new ArrayList<Filter>();
	

	public InputStream getIn() {
		return in;
	}

	public OutputStream getOut() {
		return out;
	}

	public Filter() {
	}

	public Filter(InputStream is, OutputStream os) {
		in = is;
		out = os;
	}

	public void setInputStream(InputStream in) {
		log.debug(" set in to " + in);
		this.in = in;
	}

	public void setOutputStream(OutputStream out) {
		this.out = out;
	}
	
	public void finish(OutputStream out) {
		try {
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * Standard input will come from the dir/file combination.
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false, defaultValue="input will come from another class in the pipeline.")
	public void setFile(String file) {
		this.file = file;
	}
	/**
	 * Standard output will go to the toDir/toFile combination.
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false, defaultValue="output will go to the next class in the pipeline or standard output.")
	public void setToDir(String toDir) {
		log.debug("to dir set to " + toDir);
		this.toDir = toDir;
	}
	
	/**
	 * Standard output will go to the toDir/toFile combination.
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false, defaultValue="output will go to the next class in the pipeline or standard output.")
	public void setToFile(String toFile) {
		this.toFile = toFile;
	}

	/**
	 * Move input file to an archive directory.  The archive directory
	 * will be created if it does not exist.
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false, defaultValue="archive directory.  The input file, if specified, will be moved to the directory named.")
	public void setArchive(String archive) {
		this.archive = archive;
	}

	/**
	 * Standard input will come from the dir/file combination.
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false, defaultValue="input will come from another class in the pipeline.")
	public void setDir(java.lang.String dir) {
		this.dir = dir;
	}

	public void run() {
		try {
			log.debug("Starting THREAD: " + getName() + "  " + this.getClass().getName());
			execute();
		} catch (XMLBuildException e) {
			e.printStackTrace();
		} finally {
			log.debug("Ending THREAD: " + getName() + "  " + this.getClass().getName());
			try {
				out.flush();
				if (!(out instanceof FileOutputStream))
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void execute() throws XMLBuildException  {
		InputStream in = this.in;
		OutputStream out = this.out;
		String toFile = this.toFile;
		if (this.toDir != null) {
			if (this.toFile != null) {
				try {
					toFile = XmlParser.processAttributes(this, this.toFile);
					out = new FileOutputStream(new File(toDir, toFile));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					throw new XMLBuildException(e.getMessage());
				}
			}
			log.debug("Filter toFile = " + toDir + "/" + toFile);
			
		}
		if (out == null) {
			log.debug("To stdout");
			out = System.out;
		}

		if (this.dir != null && this.file != null) {
			try {
				in = new FileInputStream(new File(dir, file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new XMLBuildException(e.getMessage());
			}
		}
		else {
			if (this.text != null) {
				in = new ByteArrayInputStream(this.text.getBytes());
			}
		}

		try {
			in = getInFilter(in);
			out = getOutFilter(out);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (in != null) {
			try {
				 byte[] buf = new byte[4096];
				for (int len = in.read(buf); len > 0; len = in.read(buf)) {
					out.write(buf, 0, len);
				}
				finish(out);

			} catch (IOException e) {
				e.printStackTrace();
				throw new XMLBuildException(e.getMessage());
			}
		}
		for (Filter filter : filters) {
			log.debug("Filter = " + filter);
			log.debug("Filter in = " + in);
			log.debug("Filter out = " + out);
			filter.setInputStream(in);
			filter.setOutputStream(out);
			filter.execute();
		}
		try {
			in.close();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void check() throws XMLBuildException {
	}

	public void setText(String text) {
	}

	public void setFromTextContent(String text) {
		this.text = text;
	}

	/**
	 * Decrypt/Encrypt a PGP document.
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addPgp(Pgp pgp) {
		filters.add(pgp);
	}

	/**
	 * Parse a csv file (any character sequence can 
	 * be used as the delimiter) into separate fields
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addLoad(Load load) {
		filters.add(load);
	}

	/**
	 * Connect classes in a pipeline
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addPipe(Pipe pipe) {
		filters.add(pipe);
	}

	/**
	 * Copy a file 
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addCopy(Copy copy) {
		filters.add(copy);
	}
	
	/**
	 * Print a message
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addPrint(Print copy) {
		filters.add(copy);
	}
	
	public InputStream getInFilter(InputStream in) throws IOException {
		return in;
	}
	
	public OutputStream getOutFilter(OutputStream out) throws IOException {
		return out;
	}
	
	public File getCurrentFile() {
		return currentFile;
	}

}
