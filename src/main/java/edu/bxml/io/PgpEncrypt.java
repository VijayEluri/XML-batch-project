package edu.bxml.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.browsexml.core.XMLBuildException;
import com.javalobby.tnt.annotation.attribute;

import cryptix.message.KeyBundleMessage;
import cryptix.message.MessageException;
import cryptix.message.MessageFactory;
import cryptix.message.stream.DecodedMessageInputStream;
import cryptix.message.stream.DecryptionKeyCallback;
import cryptix.message.stream.DecryptionKeyRequest;
import cryptix.message.stream.DecryptionKeyReturnValue;
import cryptix.message.stream.EncryptedMessageOutputStream;
import cryptix.message.stream.LiteralMessageOutputStream;
import cryptix.message.stream.VerificationKeyCallback;
import cryptix.message.stream.VerificationKeyRequest;
import cryptix.message.stream.VerificationKeyReturnValue;
import cryptix.openpgp.PGPKeyBundle;
import cryptix.pki.KeyBundle;
import cryptix.pki.KeyID;
import cryptix.pki.KeyIDFactory;

/**
 * Compress or Decompress a file
 * 
 * @author ritcheyg
 * 
 */
@attribute(value = "", required = true)
public class PgpEncrypt extends Filter 
		implements DecryptionKeyCallback, VerificationKeyCallback {
	private static Log log = LogFactory.getLog(PgpEncrypt.class);
	boolean decrypt = false;
	protected KeyBundle key = null;
	protected KeyBundle signersPublicKey = null;
	private String passPhrase = null;

	public void initialize() {
		java.security.Security
				.addProvider(new cryptix.jce.provider.CryptixCrypto());
		java.security.Security
				.addProvider(new cryptix.openpgp.provider.CryptixOpenPGP());
	}

	public PgpEncrypt() {
		initialize();
	}

	public void check() {
		if (key != null) {

		}
	}
	
	public InputStream getInFilter(InputStream in) throws IOException {
		if (in == null) 
			return null;
		if (decrypt) {
            DecodedMessageInputStream decodedInputStream;
			try {
				decodedInputStream = DecodedMessageInputStream.getInstance("OpenPGP");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				throw new IOException(e.getMessage());
			}
            decodedInputStream.init(in, this, this);
            return decodedInputStream;
		}
		return in;
	}
	
	public OutputStream getOutFilter(OutputStream out) throws IOException {
		if (out == null) 
			return null;
		if (!decrypt) {
			SecureRandom sr = new SecureRandom();
			LiteralMessageOutputStream litmos = null;
			EncryptedMessageOutputStream encmos = null;
			try {
				litmos = LiteralMessageOutputStream.getInstance("OpenPGP");
				encmos = EncryptedMessageOutputStream.getInstance("OpenPGP");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				throw new IOException(e.getMessage());
			}
			litmos.init(encmos, sr); // Literal writes to Encrypted
			encmos.init(out, sr); // Encrypted writes to output
			encmos.addRecipient(key);
			return litmos;
		}
		return out;
	}
	
	public void finish(OutputStream out) {
		if (out instanceof LiteralMessageOutputStream)
			try {
				LiteralMessageOutputStream x = (LiteralMessageOutputStream)out;
				x.getSpi().engineClose();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	/**
	 * Should we decrypt the input
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false, defaultValue = "false")
	public void setDecrypt(Boolean decrypt) {
		this.decrypt = decrypt;
	}
	public void setDecrypt(String decrypt) {
		setDecrypt(Boolean.parseBoolean(decrypt));
	}

	/**
	 * An .asc file conaining either a public or private key. If decrypting it
	 * must match the key used to encrypt.  A passphrase must be supplied 
	 * for a private key or a null pointer error will be thrown.
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void setKeyFile(String keyfile)
			throws XMLBuildException {
		
		try {
			File keyFile = new File(keyfile);
			key = readKey(keyFile);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new XMLBuildException(e.getMessage(), this);
		} catch (IOException e) {
			e.printStackTrace();
			throw new XMLBuildException(e.getMessage(), this);
		} catch (MessageException e) {
			e.printStackTrace();
			throw new XMLBuildException(e.getMessage(), this);
		}
	}
	
	/**
	 * The public key of the message signer if that is to be verified.
	 * This should be a public key and not require a passphrase.
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void setSignersKeyFile(String keyfile)
			throws XMLBuildException {
		
		try {
			File keyFile = new File(keyfile);
			this.signersPublicKey = readKey(keyFile);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new XMLBuildException(e.getMessage(), this);
		} catch (IOException e) {
			e.printStackTrace();
			throw new XMLBuildException(e.getMessage(), this);
		} catch (MessageException e) {
			e.printStackTrace();
			throw new XMLBuildException(e.getMessage(), this);
		}
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
	
	 


	public KeyBundle readKey(File keyFile) throws IOException,
			NoSuchAlgorithmException, MessageException {
		FileInputStream in;
		Collection msgs;
		KeyBundleMessage kbm;

		MessageFactory mf = MessageFactory.getInstance("OpenPGP");

		in = new FileInputStream(keyFile);
		msgs = mf.generateMessages(in);
		kbm = (KeyBundleMessage) msgs.iterator().next();
		in.close();
		log.debug("read key file " + keyFile.getAbsolutePath());
		return kbm.getKeyBundle();
	}

	   public VerificationKeyReturnValue getVerificationKey(
		        VerificationKeyRequest request)
		    {
		        try {
		            
		            // If something goes wrong, this method is called again. In that 
		            // case we could for example try another key. For this example, no
		            // more keys are available, so return an error.

		            if (request.getRetryCount() >= 1)
		                return new VerificationKeyReturnValue(
		                    VerificationKeyReturnValue.IGNORE);
		            
		            // The code below is just to check the keyid hints. If you know for 
		            // sure what key to use, just return it without checking, like:
		            //   return new VerificationKeyReturnValue(publicAlice);
		            
		            KeyID[] hintIDs = request.getKeyIDHints();
		            
		            KeyIDFactory kidf = KeyIDFactory.getInstance("OpenPGP");
		            KeyID aliceID = kidf.generateKeyID(
		                (PublicKey)signersPublicKey.getPublicKeys().next());
		            
		            for(int i=0; i<hintIDs.length; i++)
		                if (hintIDs[i].match(aliceID))
		                    return new VerificationKeyReturnValue(signersPublicKey);
		    
		            return new VerificationKeyReturnValue(
		                VerificationKeyReturnValue.IGNORE);

		        } catch (NoSuchAlgorithmException nsae) {
		            System.err.println("Cannot find OpenPGP implementation."+
		                " This usually means that the Cryptix OpenPGP provider is not "+
		                "installed correctly.");
		            nsae.printStackTrace();
		            throw new RuntimeException();
		        } catch (InvalidKeyException ike) {
		            System.err.println("Invalid key.");
		            ike.printStackTrace();
		            throw new RuntimeException();
		        }
		    }
		    
		    public DecryptionKeyReturnValue getDecryptionKey(
		        DecryptionKeyRequest request)
		    {
		        try {

		            // If something goes wrong, this method is called again. In that 
		            // case we could for example try another key. For this example, no
		            // more keys are available, so return an error.
		            
		            if (request.getRetryCount() >= 1)
		                return new DecryptionKeyReturnValue(
		                    DecryptionKeyReturnValue.FAIL);
		            
		            // The code below is just to check the keyid hints. If you know for 
		            // sure what key to use, just return it without checking, like:
		            //   return new VerificationKeyReturnValue(secretBob);
		            
		            KeyID[] hintIDs = request.getKeyIDHints();
		            
		            KeyIDFactory kidf = KeyIDFactory.getInstance("OpenPGP");
		            KeyID bobID = kidf.generateKeyID(
		                (PublicKey)key.getPublicKeys().next());
		            
		            for(int i=0; i<hintIDs.length; i++) 
		            {
		                if (hintIDs[i].match(bobID))
		                    return new DecryptionKeyReturnValue(key,
		                    		passPhrase.toCharArray());
		                
		                Iterator it = ((PGPKeyBundle)key).getPublicSubKeys();
		                while (it.hasNext())
		                {
		                    KeyID bobSubID = kidf.generateKeyID(
		                        (PublicKey)it.next());
		    
		                    if (hintIDs[i].match(bobSubID))
		                        return new DecryptionKeyReturnValue(key,
		                        		passPhrase.toCharArray());
		                }
		            }
		            
		            return new DecryptionKeyReturnValue(DecryptionKeyReturnValue.FAIL);
		    
		        } catch (NoSuchAlgorithmException nsae) {
		            System.err.println("Cannot find OpenPGP implementation."+
		                " This usually means that the Cryptix OpenPGP provider is not "+
		                "installed correctly.");
		            nsae.printStackTrace();
		            throw new RuntimeException();
		        } catch (InvalidKeyException ike) {
		            System.err.println("Invalid key.");
		            ike.printStackTrace();
		            throw new RuntimeException();
		        }
		    }
}
