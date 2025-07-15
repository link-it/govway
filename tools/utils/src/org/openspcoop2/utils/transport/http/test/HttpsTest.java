/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openspcoop2.utils.transport.http.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;

import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpLibrary;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * HttpsTest
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpsTest extends HttpTest {
	private static HttpServerTest server = null;
	
	
	private static final String PKCS12 = "PKCS12";
	private static final String KEYSTORE_PASSWORD = "123456";
	
	private static final String KEYSTORE_NAME = "keystore.p12";
	private static final String TRUSTSTORE_ERROR_NAME = "truststoreError.p12";
	
	private static String getPassword() {
		return KEYSTORE_PASSWORD;
	}
	
	private static SSLContext getServerContext() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException {		
		KeyStore keyStore = KeyStore.getInstance(PKCS12);
		
		Resource keystore = new ClassPathResource(KEYSTORE_NAME, HttpsTest.class);
		try (FileInputStream keyStoreStream = new FileInputStream(keystore.getFile())) {
		    keyStore.load(keyStoreStream, getPassword().toCharArray());
		}
		
		
		return SSLContextBuilder.create()
				.loadKeyMaterial(keyStore, KEYSTORE_PASSWORD.toCharArray())
				.build();
	}
	
	private static KeyStore getTrustStore(String fileName) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException {		
		KeyStore trustStore = KeyStore.getInstance(PKCS12);
		
		Resource keystore = new ClassPathResource(fileName, HttpsTest.class);
		try (FileInputStream trustStoreStream = new FileInputStream(keystore.getFile())) {
			trustStore.load(trustStoreStream, getPassword().toCharArray());
		}
		
		return trustStore;
	}
	
	public static void startServers() throws IOException {
		if (server != null) {
			server.close();
		}
		
		
		try {
			server = new HttpServerTest(getServerContext());
		} catch (KeyManagementException | UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException
				| CertificateException | IOException e) {
			throw new IOException(e);
		}
	}
	
	public static void stopServers() {
		if (server != null) {
			server.close();
		}
	}
	
	public static void main(String[] arg) throws UtilsException {		
				
		HttpsTest test = new HttpsTest();
		try {
			startServers();

			test.testHttps(HttpLibrary.HTTPCORE);
		} catch (Exception e) {
			throw new UtilsException(e);
		} finally {
			stopServers();
		}
	}
	
	@Override
	public HttpRequest createBaseRequest(HttpLibrary library) {
		HttpRequest req = new HttpRequest();
		req.setHttpLibrary(library);
		req.setUrl("https://localhost:" + server.getPort() + "/print");
		req.addHeader("User-Agent", "Java");
		req.addHeader("Accept-Encoding", "gzip, x-gzip, deflate");
		req.addHeader("Accept", "*/*");
		req.addHeader("Host", "localhost:" + server.getPort());
		req.setUsername(server.getUsername());
		req.setPassword(server.getPassword());
		req.setMethod(HttpRequestMethod.GET);
		
		return req;
	}
	
	public void testHttps(HttpLibrary library) throws UtilsException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
		HttpRequest req = createBaseRequest(library);
		req.setConnectTimeout(1000);
		req.setReadTimeout(1000);
		//req.setTrustAllCerts(true);
		req.setTrustStore(getTrustStore(TRUSTSTORE_ERROR_NAME));
		req.setTrustStorePassword(KEYSTORE_PASSWORD);
		req.setTrustStoreType(PKCS12);
		req.setHostnameVerifier(true);
		
		HttpResponse res = HttpUtilities.httpInvoke(req);
		
		System.out.println(res.getResultHTTPOperation());
		
		/*req.setCrlPath(null);
		req.setCrlStore(null);
		
		req.setHostnameVerifier(false);
		
		req.setKeyAlias(null);
		req.setKeyStore(null);
		req.setKeyStorePassword(null);
		req.setKeyStorePassword(null);
		req.setKeyStorePath(null);
		req.setKeyStoreType(null);
		
		req.setOcspPolicy(null);
		req.setOcspValidator(null);
		
		req.setTrustAllCerts(true);
		req.setTrustStore(null);
		req.setTrustStorePassword(null);
		req.setTrustStorePath(null);
		req.setTrustStoreType(null);*/
		
	}
}
