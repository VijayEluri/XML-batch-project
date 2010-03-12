/* $Id: StreamDecryptVerify.java,v 1.1 2008/07/31 16:05:14 geoff.ritchey Exp $
 *
 * Copyright (C) 1999-2005 The Cryptix Foundation Limited.
 * All rights reserved.
 * 
 * Use, modification, copying and distribution of this software is subject 
 * the terms and conditions of the Cryptix General Licence. You should have 
 * received a copy of the Cryptix General License along with this library; 
 * if not, you can download a copy from http://www.cryptix.org/ .
 */

package edu.bxml.io;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cryptix.message.stream.DecryptionKeyCallback;
import cryptix.message.stream.VerificationKeyCallback;


/**
 * Detailed example for decrypting and verifying a message through streaming. 
 * This is especially handy for processing large files that do not fit in 
 * memory. 
 * This example assumes that the GenerateAndWriteKey and StreamEncryptSign 
 * examples have been run first.
 *
 * @author  Edwin Woudt <edwin@cryptix.org>
 * @version $Revision: 1.1 $
 */
public class StreamDecryptVerify  extends PgpEncrypt 
    implements DecryptionKeyCallback, VerificationKeyCallback
{
	private static Log log = LogFactory.getLog(StreamDecryptVerify.class);
	public void initialize() {
		//decrypt = true;
		java.security.Security
				.addProvider(new cryptix.jce.provider.CryptixCrypto());
		java.security.Security
				.addProvider(new cryptix.openpgp.provider.CryptixOpenPGP());
	}

	public StreamDecryptVerify() {
		initialize();
	}


    
}
