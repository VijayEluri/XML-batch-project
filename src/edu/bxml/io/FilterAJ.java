package edu.bxml.io;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;

public interface FilterAJ extends Runnable, XmlObject {

	boolean isHereFile();
	void setHereFile(boolean isHereFile);
	boolean getLock();
	void setLock(boolean lock);
	Boolean getCloseIn();
	void setCloseIn(Boolean closeIn);
	Boolean getCloseOut();
	void setCloseOut(Boolean closeOut);
	void closeIn();
	void closeOut();
	InputStream getIn() throws XMLBuildException;
	OutputStream getOut();
	void setInputStream(InputStream in);
	void setOutputStream(OutputStream out);
	void finish(OutputStream out);
	void setFile(String file);
	void setToDir(String toDir);
	void setToFile(String toFile);
	File getOutFile();
	void setOutFile(File outFile);
	void setArchive(String archive);
	void setDir(String dir);
	Object getPojo();
	boolean isIff();
}
