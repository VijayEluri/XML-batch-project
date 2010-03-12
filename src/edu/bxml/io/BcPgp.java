package edu.bxml.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;

import com.browsexml.core.XMLBuildException;
import com.javalobby.tnt.annotation.attribute;

/**
 * Encrypt or decrypt a file - Bouncy Castle
 * 
 * @param file
 */
@attribute(value = "", required = false)
public class BcPgp extends Filter {
	private static Log log = LogFactory.getLog(BcPgp.class);
	// implies decrypt with your own private key
	private String secretKeyringFile = null; 
	private String passPhrase = null;
	// implies encrypt (with someone else's public key) 
	private String publicKeyringFile = null; 

	public BcPgp() {
		Security.addProvider(new BouncyCastleProvider());
	}

	public BcPgp(InputStream in, OutputStream out) {
		super(in, out);
		Security.addProvider(new BouncyCastleProvider());
	}

	public void check() throws XMLBuildException {
		if (secretKeyringFile == null && publicKeyringFile == null) {
			throw new XMLBuildException("You must specify a secretKeyringFile to decrypt or a publicKeyringFile to encrypt");
		}
		if (passPhrase == null) {
			throw new XMLBuildException("You must specify passPhrase");
		}
	}

	/**
	 * Called after complete parsing of XML document to evaluate the document.
	 */
	public void execute() throws XMLBuildException {

		try {

			// **********************************************************************
			// Decrypt the message.
			// **********************************************************************

			if (this.out == null && this.toFile == null) {
				out = System.out;
			}
			if (this.out != null) {
				if (secretKeyringFile != null)
					decryptFile(in, secretKeyringFile, passPhrase.toCharArray(), this.out);
				else
				{};
				return;
			}

			String outFileName = null;
			if (this.toFile == null)
				outFileName = new File(file).getName().replace(".pgp", "");
			else
				outFileName = toFile;
			File outFile = new File(toDir, outFileName);

			if (outFile.exists()) {
				throw new XMLBuildException(outFile.getAbsolutePath()
						+ " already exists.");
			} else {
				if (secretKeyringFile != null)
					decryptFile(in, secretKeyringFile, passPhrase.toCharArray(), outFileName);
				else 
				{};
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * An .asc file conaining either a public or private key. If decrypting it
	 * must match the key used to encrypt.
	 * 
	 * 
	 * @param file
	 */
	@attribute(value = "", required = false)
	public void setSecretKeyringFile(String secretKeyringFile) {
		this.secretKeyringFile = secretKeyringFile;
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

	/**
	 * A simple routine that opens a key ring file and loads the first available
	 * key suitable for encryption.
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 * @throws PGPException
	 */
	private static PGPPublicKey readPublicKey(InputStream in)
			throws IOException, PGPException {
		in = PGPUtil.getDecoderStream(in);

		PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(in);

		//
		// we just loop through the collection till we find a key suitable for
		// encryption, in the real
		// world you would probably want to be a bit smarter about this.
		//

		//
		// iterate through the key rings.
		//
		Iterator rIt = pgpPub.getKeyRings();

		while (rIt.hasNext()) {
			PGPPublicKeyRing kRing = (PGPPublicKeyRing) rIt.next();
			Iterator kIt = kRing.getPublicKeys();

			while (kIt.hasNext()) {
				PGPPublicKey k = (PGPPublicKey) kIt.next();

				if (k.isEncryptionKey()) {
					return k;
				}
			}
		}

		throw new IllegalArgumentException(
				"Can't find encryption key in key ring.");
	}

	/**
	 * Search a secret key ring collection for a secret key corresponding to
	 * keyID if it exists.
	 * 
	 * @param pgpSec
	 *            a secret key ring collection.
	 * @param keyID
	 *            keyID we want.
	 * @param pass
	 *            passphrase to decrypt secret key with.
	 * @return
	 * @throws PGPException
	 * @throws NoSuchProviderException
	 */
	private static PGPPrivateKey findSecretKey(
			PGPSecretKeyRingCollection pgpSec, long keyID, char[] pass)
			throws PGPException, NoSuchProviderException {
		PGPSecretKey pgpSecKey = pgpSec.getSecretKey(keyID);

		if (pgpSecKey == null) {
			return null;
		}

		return pgpSecKey.extractPrivateKey(pass, "BC");
	}

	/**
	 * decrypt the passed in message stream
	 */
	private static void decryptFile(InputStream in, String secretKeyringFile,
			char[] passwd, String outFileName) throws Exception {
		FileOutputStream fOut = new FileOutputStream(outFileName);
		decryptFile(in, secretKeyringFile, passwd, fOut);
		fOut.close();
	}

	/**
	 * decrypt the passed in message stream
	 */
	private static void decryptFile(InputStream in, String secretKeyringFile,
			char[] passwd, OutputStream fOut) throws Exception {
		in = PGPUtil.getDecoderStream(in);

		try {
			FileInputStream keyIn = new FileInputStream(secretKeyringFile);
			
			PGPObjectFactory pgpF = new PGPObjectFactory(in);
			PGPEncryptedDataList enc;

			Object o = pgpF.nextObject();
			//
			// the first object might be a PGP marker packet.
			//
			if (o instanceof PGPEncryptedDataList) {
				enc = (PGPEncryptedDataList) o;
			} else {
				enc = (PGPEncryptedDataList) pgpF.nextObject();
			}

			//
			// find the secret key
			//
			Iterator it = enc.getEncryptedDataObjects();
			PGPPrivateKey sKey = null;
			PGPPublicKeyEncryptedData pbe = null;
			PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(
					PGPUtil.getDecoderStream(keyIn));

			while (sKey == null && it.hasNext()) {
				pbe = (PGPPublicKeyEncryptedData) it.next();

				sKey = findSecretKey(pgpSec, pbe.getKeyID(), passwd);
			}

			if (sKey == null) {
				throw new IllegalArgumentException(
						"secret key for message not found.");
			}

			InputStream clear = pbe.getDataStream(sKey, "BC");

			PGPObjectFactory plainFact = new PGPObjectFactory(clear);

			Object message = plainFact.nextObject();

			if (message instanceof PGPCompressedData) {
				PGPCompressedData cData = (PGPCompressedData) message;
				PGPObjectFactory pgpFact = new PGPObjectFactory(cData
						.getDataStream());

				message = pgpFact.nextObject();
			}

			if (message instanceof PGPLiteralData) {
				PGPLiteralData ld = (PGPLiteralData) message;

				InputStream unc = ld.getInputStream();
				int ch;

				while ((ch = unc.read()) >= 0) {
					fOut.write(ch);
				}
			} else if (message instanceof PGPOnePassSignatureList) {
				throw new PGPException(
						"encrypted message contains a signed message - not literal data.");
			} else {
				throw new PGPException(
						"message is not a simple encrypted file - type unknown.");
			}

			if (pbe.isIntegrityProtected()) {
				if (!pbe.verify()) {
					System.err.println("message failed integrity check");
				} else {
					System.err.println("message integrity check passed");
				}
			} else {
				System.err.println("no message integrity check");
			}
		} catch (PGPException e) {
			System.err.println(e);
			if (e.getUnderlyingException() != null) {
				e.getUnderlyingException().printStackTrace();
			}
		}
	}

	private static void encryptFile(OutputStream out, String fileName,
			PGPPublicKey encKey, boolean armor, boolean withIntegrityCheck)
			throws IOException, NoSuchProviderException {
		if (armor) {
			out = new ArmoredOutputStream(out);
		}

		try {
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();

			PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(
					PGPCompressedData.ZIP);

			PGPUtil.writeFileToLiteralData(comData.open(bOut),
					PGPLiteralData.BINARY, new File(fileName));

			comData.close();

			PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(
					PGPEncryptedData.CAST5, withIntegrityCheck,
					new SecureRandom(), "BC");

			cPk.addMethod(encKey);

			byte[] bytes = bOut.toByteArray();

			OutputStream cOut = cPk.open(out, bytes.length);

			cOut.write(bytes);

			cOut.close();

			out.close();
		} catch (PGPException e) {
			System.err.println(e);
			if (e.getUnderlyingException() != null) {
				e.getUnderlyingException().printStackTrace();
			}
		}
	}

	public void setPublicKeyringFile(String publicKeyringFile) {
		this.publicKeyringFile = publicKeyringFile;
	}

}
