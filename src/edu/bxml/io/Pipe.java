package edu.bxml.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

/**
 * Set up a pipeline between classes.  The output of each child will 
 * be the input of the next child.
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Pipe extends Filter {
	private static Log log = LogFactory.getLog(Pipe.class);
	List<XmlObject> preCommands = new ArrayList<XmlObject>();
	List<XmlObject> postCommands = new ArrayList<XmlObject>();
	List<XmlObject> attributes = new ArrayList<XmlObject>();

	/**
	 * check that all the fields are set correctly, especially required fields.
	 * Called when the end-tag of the element has been reached and processed.
	 */
	public void check() throws XMLBuildException {

	}

	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() throws XMLBuildException {

		log.debug("Pipe Execute");
		
		PipedOutputStream pout = null;

		InputStream pin = null;
		InputStream pipeInput = null;
		File archiveDir = null;
		try {
			archiveDir = new File(archive);
		} catch (RuntimeException e2) {}

		boolean found = false;
		File[] files = null;
		if (dir != null) {
			files = (new File(dir)).listFiles();
			if (files == null) {
				throw new XMLBuildException("can't open " +dir);
			}
		}
		else {
			this.file = "_output_";
			files = new File[] {new File(".", this.file)};
		}
		for (File file : files) {
			if (!file.getName().matches(this.file))
				continue;
			currentFile = file;
			String toFile = this.toFile;
			OutputStream out = this.out;
			if (this.toDir != null) {
				if (toFile == null) {
					toFile = file.getName();
				}
				try {
					out = new FileOutputStream(new File(toDir, toFile));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					throw new XMLBuildException(e.getMessage());
				}
			}
			if (out == null) 
				out = System.out;
			found = true;
			log.debug("pipe file is " + file.getAbsolutePath());
			if (file.exists())
				try {
					pipeInput = (InputStream) new FileInputStream(file);
				} catch (FileNotFoundException e2) {
					e2.printStackTrace();
				}
			else {
				pipeInput = (InputStream)new StringBufferInputStream("");
			}
			pin = pipeInput;
			log.debug("HERE");
			for (XmlObject att: attributes) {
				att.execute();
			}
			
			for (XmlObject command: preCommands) {
				command.execute();
			}
			
			for (Filter filter : filters) {
				try {
					log.debug("command = " + filter);
					pout = new PipedOutputStream();
					log.debug("pout = " + pout);
					filter.setInputStream(pin);
					filter.setOutputStream(pout);
					pin = new PipedInputStream(pout);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			List<Thread> threads = new ArrayList<Thread>();
			
			Filter filt2 = new Filter(pin, out);
			for (Filter filter : filters) {
				Thread t = new Thread(filter);
				threads.add(t);
				t.start();
			}
			Thread t = new Thread(filt2);
			t.start();
			try {
				t.join();
				for (Thread t1: threads) {
					t1.join();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			log.debug("archvie = " + archive);
			try {
				pipeInput.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			for (XmlObject command: postCommands) {
				command.execute();
			}
			if (archive != null) {
				if (!archiveDir.exists()) {
					archiveDir.mkdirs();
				}
				if (archiveDir.exists()) {
					log.debug("archive exists");
					//file.renameTo(new File(archiveDir, file.getName()));
					try {
						FileInputStream inArch = new FileInputStream(file);
						FileOutputStream outArch = new FileOutputStream(
								new File(archiveDir, file.getName()));
						Copy.copyBinaryFile(inArch, outArch);
						inArch.close();
						outArch.close();
						
						log.debug("delete " + file.getAbsolutePath());
						log.debug("delete result = " + file.delete());
						
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (!found) {
			log.debug("no file in " + new File(dir).getAbsolutePath() + 
					" matched " + file);
		}
		try {
			if (pout != null)
				pout.close();
			if (pin != null)
				pin.close();
			if (pipeInput != null)
				pipeInput.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new XMLBuildException(e.getMessage());
		}
		log.debug("DONE");
	}
	

	/**
	 * add gZip to the pipeline
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addGZip(GZip gZip) {
		filters.add(gZip);
	}

	/**
	 * add pgp encrypt or decrypt to the pipeline
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addPgp(Pgp pgp) {
		filters.add(pgp);
	}
	
	/**
	 * add pgp encrypt or decrypt to the pipeline
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addStreamDecryptVerify(StreamDecryptVerify pgp) {
		filters.add(pgp);
	}
	
	/**
	 * add pgp encrypt or decrypt to the pipeline
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addPgpEncrypt(PgpEncrypt pgp) {
		filters.add(pgp);
	}

	/**
	 * Add Processing of fields to a pipeline
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addLoad(Load load) {
		filters.add(load);
	}
	/**
	 * Add a copy of input to output in the pipeline.  Can be used
	 * to concatenate the output of multiple classes into the pipe.
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addFilter(Filter filter) {
		log.debug("Fiter adding is " + filter.getClass());
		filters.add(filter);
	}
	
	/**
	 * Add a pipe-splitting tee.  This sends the output to the next 
	 * object's standard in as well as writing a copy to a file.
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void addTee(Tee tee) {
		log.debug("Fiter adding is " + tee.getClass());
		filters.add(tee);
	}
	
	@attribute(value = "", required = false)
	public void addAttribute(Attribute attribute) {
		attributes.add(attribute);
	}
	
	public void addDelete(Delete delete) {
		if (filters.size() == 0)
			preCommands.add(delete);
		else
			postCommands.add(delete);
	}
}
