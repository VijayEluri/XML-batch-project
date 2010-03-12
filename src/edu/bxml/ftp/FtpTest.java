package edu.bxml.ftp;

import com.browsexml.core.XMLBuildException;
import com.javalobby.tnt.annotation.attribute;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test harness
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class FtpTest extends TestCase {

    public static Test suite() {

        TestSuite suite = new TestSuite();

        suite.addTestSuite(FtpTest.class);
        return suite;
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
	
	protected void setUp() {
	}
	
	protected void tearDown() {
	}
	
	public void testEmpty() {
		Ftp f = new Ftp();
		/*
		  	<execute connection="billing">
				<put toDir="/" toFile="{MMddyyyy}"/>
			</execute>
		 	<connection name="billing" trust="false" 
				login="geoff" 
				passPhrase="LCU2006QuickBill"
				keyfile = "C:\sshHome\LCU_SSH_Private"
				host="localhost" port="8022" channelType="sftp" >
			</connection>
		 */
		Connection c = new Connection();
		c.setLogin("geoff");
		c.setTrust("false");
		c.setName("billing");
		c.setPassPhrase("LCU2006QuickBill");
		c.setKeyfile("C:/sshHome/LCU_SSH_Private");
		c.setHost("localhost");
		c.setPort("8022");
		c.setChannelType("sftp");
		
		f.addConnection(c);

		Execute e = new Execute();
		e.setConnection("billing");
		f.addExecute(e);

		Put p = new Put();
		p.setToDir("/");
		p.setToFile("{MMddyyyy}");
		e.addPut(p);
		
		try {
			p.init(e);
			c.init(f);
			e.init(f);
			
			c.check();
			e.check();
			p.check();
			f.check();
		} catch (XMLBuildException e2) {
			assertTrue("check failed", false);
			e2.printStackTrace();
		}
		
		try {
			f.execute();
		} catch (XMLBuildException e1) {
			e1.printStackTrace();
		}
		
	}

}
