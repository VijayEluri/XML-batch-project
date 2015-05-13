package edu.bxml.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.lang.reflect.Field;

import edu.bxml.format.CharField;

public class PipeTest {
	public static void main(String args[]) {
		try { /* set up pipes */
			PipedOutputStream pout1 = new PipedOutputStream();
			
			/* construct threads */

			File file = new File("C:/Users/geoff.ritchey/Desktop/PGP",
					"prod_lcu_eod200807171700.dat.pgp");
			FileInputStream in = null;
			try {
				in = new FileInputStream(file);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}

			//Producer prod = new Producer(pout1);
			Filter prod = new Filter(in, pout1);
			
			Pgp filt = new Pgp();
			
			PipedInputStream pin1 = new PipedInputStream(pout1);
			PipedOutputStream pout2 = new PipedOutputStream();
			filt.setInputStream(pin1);
			filt.setOutputStream(pout2);
			filt.setPrivateArmouredKeyFile("C:/pgp262i/LCU.asc");
			filt.setPassPhrase("LCU2006QuickBill");
			
			PipedInputStream pin2 = new PipedInputStream(pout2);
			Load cons = new Load(pin2, System.out);
			cons.setDelimit("\\|");
			SkipField f = new SkipField();
			f.setCount("15");
			cons.addField(f);
			CharField f1 = new CharField();
			f1.setName("PeopleId");
			f1.setSize("9");
			cons.setText("<Row>\n" +
	"<Cell><Data ss:Type=\"String\">%{PeopleId}</Data></Cell>\n" +
    "<Cell><Data ss:Type=\"String\">%{id}</Data></Cell>\n" +
    "<Cell><Data ss:Type=\"String\">%{name}</Data></Cell>\n" +
    "<Cell><Data ss:Type=\"String\">%{description}</Data></Cell>\n" +
    "<Cell><Data ss:Type=\"Number\">%{cost}</Data></Cell>\n" +
   "</Row>");
			//Consumer cons = new Consumer(pin2);

			/* start threads */

			//prod.start();
			Thread prodThread = new Thread(prod);
			prodThread.start();
			Thread filtThread = new Thread(filt);
			filtThread.start();
			Thread consThread = new Thread(cons);
			consThread.start();
			//cons.start();
		} catch (IOException e) {
		}
		/*
		 * try { PipedOutputStream pout1 = new PipedOutputStream(); Filter filt1 =
		 * new Filter( (InputStream)new FileInputStream( new
		 * File("C:/Users/geoff.ritchey/Desktop/PGP",
		 * "prod_lcu_eod200807171700.dat.pgp")), pout1); //filt1.start();
		 * 
		 * 
		 * PipedInputStream pin1 = new PipedInputStream(pout1);
		 * PipedOutputStream pout2 = new PipedOutputStream();
		 * 
		 * log.debug("Creating PGP"); Pgp pgp = new Pgp();//new
		 * Filter(pin1, pout2);
		 * pgp.setPrivateArmouredKeyFile("C:/pgp262i/LCU.asc");
		 * pgp.setPassPhrase("LCU2006QuickBill"); pgp.setInputStream(pin1);
		 * pgp.setOutputStream((OutputStream) System.err);
		 * log.debug("DONE");
		 * 
		 * Thread two = new Thread(filt1); two.start();
		 * 
		 * Thread three = new Thread(pgp); log.debug("Start three");
		 * three.start();
		 * 
		 * 
		 * 
		 * PipedInputStream pin2 = new PipedInputStream(pout2); Filter filt2 =
		 * new Filter(pin2, (OutputStream) System.out); Thread four = new
		 * Thread(filt2); four.start();
		 *  } catch (IOException e) { } }
		 */
	}
}
