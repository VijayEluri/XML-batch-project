package edu.bxml.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class Producer extends Thread {
	private OutputStream out;
	private InputStream in;

	public Producer(OutputStream os) {
		out = os;
	}

	public void run() {
		File file = new File("C:/Users/geoff.ritchey/Desktop/PGP", 
				"prod_lcu_eod200807171700.dat.pgp");
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
	    try {
			for (int b = in.read();b != -1;b = in.read()) {
			    out.write(b);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}