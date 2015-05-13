package edu.bxml.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.javalobby.tnt.annotation.attribute;

import cryptix.message.EncryptedMessage;
import cryptix.message.KeyBundleMessage;
import cryptix.message.LiteralMessage;
import cryptix.message.Message;
import cryptix.message.MessageException;
import cryptix.message.MessageFactory;
import cryptix.message.NotEncryptedToParameterException;
import cryptix.pki.KeyBundle;

/**
 * Encrypt or decrypt a file 
 * 
 * @param file
 */
@attribute(value = "", required = false)
public class Pgp extends Filter {
	private static Log log = LogFactory.getLog(Pgp.class);
	private String privateArmouredKeyFile = null;
	private String passPhrase = null;
	
	public Pgp() {
		
	}
	
	public Pgp(InputStream in, OutputStream out) {
		super(in, out);
	}

	public void check() throws XMLBuildException {
		if (privateArmouredKeyFile == null) {
			throw new XMLBuildException("You must specify privateArmouredKeyFile", this);
		}
		if (passPhrase == null) {
			throw new XMLBuildException("You must specify passPhrase", this);
		}
	}

	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() throws XMLBuildException {
		log.debug("PGP execute");
	       // **********************************************************************
        // Dynamically register both the Cryptix JCE and Cryptix OpenPGP
        // providers.
        // **********************************************************************
        java.security.Security.addProvider(
            new cryptix.jce.provider.CryptixCrypto() );
        java.security.Security.addProvider(
            new cryptix.openpgp.provider.CryptixOpenPGP() );


        // **********************************************************************
        // First read the key.
        // **********************************************************************
        KeyBundle bundle = null;
        MessageFactory mf = null;
        
        log.debug("pgp execute");
        FileInputStream inKey = null;
        try {
            inKey = new FileInputStream(privateArmouredKeyFile);
            log.debug("paf = " + privateArmouredKeyFile);

            mf = MessageFactory.getInstance("OpenPGP");
            Collection msgs = mf.generateMessages(inKey);
            
            KeyBundleMessage kbm = (KeyBundleMessage)msgs.iterator().next();
            
            bundle = kbm.getKeyBundle();
        
        } catch (IOException ioe) {
        	throw new XMLBuildException(ioe.getMessage(), this);
        } catch (NoSuchAlgorithmException nsae) {
        	throw new XMLBuildException("Cannot find the OpenPGP MessageFactory. "+
                "This usually means that the Cryptix OpenPGP provider is not "+
                "installed correctly.", this);
        } catch (MessageException me) {
        	throw new XMLBuildException("Reading keybundle failed.", this);
        }
        finally {
        	try {
        		if (inKey != null) {
        			inKey.close();
        		}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        log.debug("pgp execute 2");

        // **********************************************************************
        // Read the message.
        // **********************************************************************
        EncryptedMessage em = null;
        
        try {
        	if (in == null) {
        		in = new FileInputStream(new File(dir, file));
        	}
            Collection msgs = mf.generateMessages(in);
            em = (EncryptedMessage)msgs.iterator().next();

            //in.close();
        
        } catch (IOException ioe) {
            log.debug(ioe.getMessage());
            ioe.printStackTrace();
            System.exit(-1);
        } catch (MessageException me) {
            log.debug("Reading message failed.");
            me.printStackTrace();
            System.exit(-1);
        }
        

        // **********************************************************************
        // Decrypt the message.
        // **********************************************************************
        try {
            Message msg = em.decrypt(bundle, passPhrase.toCharArray());
            if (this.out == null && this.toFile==null) {
            	out = System.out;
            }
            if (this.out != null) {
            	try {
					out.write(((LiteralMessage)msg).getBinaryData());
				} catch (UnsupportedOperationException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
            	return;
            }

            String outFileName = null;
            if (this.toFile == null)
            	outFileName = new File(file).getName().replace(".pgp", "");
            else 
            	outFileName = toFile;
            File outFile = new File(toDir, outFileName);
            
            if (outFile.exists()) {
            	System.out.println(((LiteralMessage)msg).getTextData());
            	throw new XMLBuildException(outFile.getAbsolutePath() + " already exists.", this);
            }
            else {
            	//PrintStream out = null;
            	
					//out = new PrintStream(new FilterOutputStream(new FileOutputStream(outFile)));
            		
					try {
						out.write(((LiteralMessage)msg).getBinaryData());
					} catch (IOException e) {
						e.printStackTrace();
					}
					//out.close();
				
            }
            if (archive != null) {
	            File archiveDir = new File(archive);
	            if (!archiveDir.exists()) {
	            	archiveDir.mkdirs();
	            }
	            File archiveFile = new File(archiveDir, outFileName);
	            try {
					Copy.copyBinaryFile(outFile, archiveFile, System.out);
					outFile.delete();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
            
        
        } catch (NotEncryptedToParameterException netpe) {
        	throw new XMLBuildException("Not encrypted to this key.", this);
        } catch (UnrecoverableKeyException uke) {
        	throw new XMLBuildException("Invalid passphrase.", this);
        } catch (MessageException me) {
        	throw new XMLBuildException("Decrypting message failed.", this);
        }  
	}
	
	/**
	 * An .asc file conaining either a public or private key.  If
	 * decrypting it must match the key used to encrypt.
	 * 
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void setPrivateArmouredKeyFile(String privateArmouredKeyFile) {
		this.privateArmouredKeyFile = privateArmouredKeyFile;
	}

	/**
	 * The phrase used to lock your key file
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void setPassPhrase(String passPhrase) {
		this.passPhrase = passPhrase;
	}

}
