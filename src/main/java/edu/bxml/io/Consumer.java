package edu.bxml.io;

import java.io.IOException;
import java.io.InputStream;

class Consumer extends Thread {

	  private InputStream in;
	  
	  public Consumer(InputStream is) {
	    in = is;
	  }

	  public void run() {
		    try {
				for (int b = in.read();b != -1;b = in.read()) {
				    System.out.write(b);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				System.out.flush();
			}
	  }
}