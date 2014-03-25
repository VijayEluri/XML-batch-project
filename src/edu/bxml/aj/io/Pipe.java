package edu.bxml.aj.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.io.FilterAJ;
import edu.bxml.io.FilterAJImpl;


/**
 * Set up a pipeline between classes.  The output of each child will 
 * be the input of the next child.
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class Pipe extends FilterAJImpl implements FilterAJ, UncaughtExceptionHandler {
	private static Log log = LogFactory.getLog(Pipe.class);
	List<XmlObject> preCommands = new ArrayList<XmlObject>();
	List<XmlObject> postCommands = new ArrayList<XmlObject>();
	List<XmlObject> attributes = new ArrayList<XmlObject>();
	private Exception exception;

	/**
	 * check that all the fields are set correctly, especially required fields.
	 * Called when the end-tag of the element has been reached and processed.
	 */
	public void check() throws XMLBuildException {

	}
	
	List<Thread> threads;
	List<OutputStream> pouts = new ArrayList<OutputStream>();
	List<InputStream> pins = new ArrayList<InputStream>();
	
	InputStream pin = null;
	InputStream pipeInput = null;
	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() throws XMLBuildException {

		log.debug("Pipe Execute");
		
		PipedOutputStream pout = null;


		File archiveDir = null;
		try {
			archiveDir = new File(archive);
		} catch (RuntimeException e2) {}

		boolean found = false;
		File[] files = null;
		if (dir != null) {
			files = (new File(dir)).listFiles();
			if (files == null) {
				throw new XMLBuildException("can't open " +dir, this);
			}
		}
		else {
			this.file = "_output_";
			files = new File[] {new File(".", this.file)};
		}
		if (true) {

//			currentFile = file;
//			String toFile = this.toFile;
//			OutputStream out = this.out;
//			if (this.toDir != null) {
//				if (toFile == null) {
//					toFile = file.getName();
//				}
//				try {
//					out = new FileOutputStream(new File(toDir, toFile));
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//					throw new XMLBuildException(e.getMessage());
//				}
//			}
//			if (out == null) 
//				out = System.out;
//			found = true;
//			log.debug("pipe file is " + file.getAbsolutePath());
//			if (file.exists())
//				try {
//					pipeInput = (InputStream) new FileInputStream(file);
//				} catch (FileNotFoundException e2) {
//					e2.printStackTrace();
//				}
//			else {
//				try {
//					pipeInput = new ByteArrayInputStream("".getBytes("UTF-8"));
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
//
//			}
//			Thread.setDefaultUncaughtExceptionHandler(this);
			
			pipeInput = getIn();
			log.debug("HERE");
			for (XmlObject att: attributes) {
				att.execute();
			}

			for (XmlObject command: preCommands) {
				command.execute();
			}
			
			filters.clear();
			for (FilterAJ filter : filtersMaster) {
				if (filter.isIff()) {
					filters.add(filter);
				}
			}
			for (FilterAJ filter : filters) {
						try {
							log.debug("command = " + filter);
							pout = new PipedOutputStream();
							log.debug("pout = " + pout);
							filter.setInputStream(pin);
							filter.setOutputStream(pout);
							pin = new PipedInputStream(pout);
							pins.add(pin);
							pouts.add(pout);
		
						} catch (IOException e) {
							e.printStackTrace();
						}
			}

			threads = new ArrayList<Thread>();
			log.debug("start threads");
			FilterAJ filt2 = new FilterAJImpl(pin, out);
			for (FilterAJ filter : filters) {

					filter.setLock(true);
					Thread t = new Thread(filter);
					threads.add(t);
					log.debug("start thread " + (threads.size() -1) + " for " + filter.getClass().getName());
					t.start();
				
			}
			filt2.setLock(true);
			Thread t = new Thread(filt2);
			log.debug("start thread for " + filt2.getClass().getName());
			log.debug("start threads 2");
			t.start();
			try {
				int index = -1;
				for(boolean allTerminated = false;!allTerminated;) {
					allTerminated = true;
					log.debug("Main thread (last in pipe) state = " + t.getState().toString());
					if (!t.getState().equals(Thread.State.TERMINATED)) {
						log.debug("main thread still running... not terminated");
						t.join(1000);
						allTerminated = false;
					}
					int i = 0;
					for (Thread t1: threads) {
						log.debug("  sub thread state = " + t1.getState().toString() );
						if (!t1.getState().equals(Thread.State.TERMINATED)) {
							log.debug("sub thread still running... not terminated    " + t1.getState() + "   " + Thread.State.TERMINATED);
							t1.join(1000);
							if (!t1.getState().equals(Thread.State.TIMED_WAITING)) 
								allTerminated = false;
							else if (allTerminated) {
								
								index = i;
							}
						}
						i++;
					}
				}
				if (index >= 0) {
					for (Thread t1: threads) {
						t1.interrupt();
					}
					log.debug("TIMEOUT on pipe .. waiting for IO (check stack trace for read or write block) on thread (" + index +  ") " + filters.get(index).getName() + "  " + filters.get(index).getClass().getName());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			log.debug("after threads");

			log.debug("archvie = " + archive);
			try {
				pipeInput.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			for (XmlObject command: postCommands) {
				command.execute();
			}
			closeIO();
//			if (archive != null) {
//				if (!archiveDir.exists()) {
//					archiveDir.mkdirs();
//				}
//				if (archiveDir.exists()) {
//					log.debug("archive exists");
//					//file.renameTo(new File(archiveDir, file.getName()));
//					try {
//						FileInputStream inArch = new FileInputStream(file);
//						FileOutputStream outArch = new FileOutputStream(
//								new File(archiveDir, file.getName()));
//						Copy.copyBinaryFile(inArch, outArch);
//						inArch.close();
//						outArch.close();
//						
//						log.debug("delete " + file.getAbsolutePath());
//						log.debug("delete result = " + file.delete());
//						
//					} catch (FileNotFoundException e) {
//						e.printStackTrace();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			}
		}
		if (!found) {
			log.debug("no file in directory '" + dir + 
					"' matched " + file);
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
			throw new XMLBuildException(e.getMessage(), this);
		}
		log.debug("DONE");
	}
	


	/**
	 * Add a copy of input to output in the pipeline.  Can be used
	 * to concatenate the output of multiple classes into the pipe.
	 * 
	 * @param file
	 * @throws XMLBuildException 
	 */
	@attribute(value = "", required = false)
	public void addFilterAJImpl(FilterAJImpl filter) throws XMLBuildException {
		log.debug("Fiter adding is " + filter.getClass());
		filtersMaster.add(filter);
	}


	public void uncaughtException(Thread t, Throwable exception) {
		log.debug("UNCAUGHT EXCEPTION from thread " + t + " for exception " + exception.getMessage());
		exception.printStackTrace();
		closeIO(); // If all input pipes are not closed, a thread may hang on reading from a dead thread
	}
	
	public void closeIO() {
		for (OutputStream pout: pouts) {
			try {
				pout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (InputStream pin: pins) {
			try {
				pin.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
