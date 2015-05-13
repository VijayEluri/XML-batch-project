package edu.bxml.http;

import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class BlindTrustManager {
	TrustManager[] blindTrustMan = new TrustManager[] { new X509TrustManager() {
		public X509Certificate[] getAcceptedIssuers() { return null; }
		public void checkClientTrusted(X509Certificate[] c, String a) { }
		public void checkServerTrusted(X509Certificate[] c, String a) { }
	} };
}
