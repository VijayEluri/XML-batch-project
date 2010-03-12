package edu.bxml.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Write two copies of the output.
 * @author geoff.ritchey
 *
 */
public class TeeOutputStream extends OutputStream {
	private OutputStream os = null;
	private OutputStream teeStream = null;
	
	public TeeOutputStream(OutputStream teeFile, OutputStream out) {
		os = out;
		teeStream = teeFile;
	}
	
	public void write(int b) throws IOException {
		os.write(b);	
		teeStream.write(b);
	}
	
	public void close() throws IOException {
		os.close();
		teeStream.close();
	}
}
