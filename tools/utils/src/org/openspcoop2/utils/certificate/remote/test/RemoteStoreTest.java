/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
package org.openspcoop2.utils.certificate.remote.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.security.PublicKey;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.certificate.JWK;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.certificate.KeyUtils;
import org.openspcoop2.utils.certificate.remote.RemoteKeyIdMode;
import org.openspcoop2.utils.certificate.remote.RemoteStoreConfig;
import org.openspcoop2.utils.certificate.remote.RemoteStoreUtils;
import org.openspcoop2.utils.certificate.test.CertificateTest;
import org.openspcoop2.utils.certificate.test.KeystoreTest;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**	
 * RemoteStoreTest
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RemoteStoreTest {

	public static void main(String[] args) throws UtilsException {
		
		testJWK();
		
		testPublicKey();
		
		testX509();
		
		print("\n\nTestsuite finita");
	}
	
	private static void print(String msg) {
		System.out.println(msg);
	}
	
	public static void testJWK() throws UtilsException {
	
		print("========================= remote store JWK ==============================");
		
		byte[] jwk = Utilities.getAsByteArray(CertificateTest.class.getResourceAsStream("/org/openspcoop2/utils/security/test/keystore_example.jwks"));
		File file = null;
		try {
			JWKSet jwkSet = new JWKSet(new String(jwk));
			JWK jwkSingle = jwkSet.getJwks().get(0);
			
			file = File.createTempFile("test", ".jwk");
			FileSystemUtilities.writeFile(file, jwkSingle.getJson().getBytes());
			
			RemoteStoreConfig remoteConfig = new RemoteStoreConfig("TEST");
			remoteConfig.setBaseUrl(file.getParentFile().getAbsolutePath());
			
			remoteConfig.setIdMode(RemoteKeyIdMode.URL_SUFFIX);
			getAndCheckJWK(file, remoteConfig);
			
			remoteConfig.setBaseUrl(file.getAbsolutePath());
			remoteConfig.setIdMode(RemoteKeyIdMode.URL_PARAMETER);
			remoteConfig.setParameterName("test");
			getAndCheckJWK(file, remoteConfig);
			
			remoteConfig.setBaseUrl(file.getAbsolutePath());
			remoteConfig.setIdMode(RemoteKeyIdMode.HEADER);
			remoteConfig.setParameterName("test");
			remoteConfig.setKeyAlgorithm(KeyUtils.ALGO_RSA);
			getAndCheckJWK(file, remoteConfig);
			
		}
		catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		finally {
			try {
				if(file!=null) {
					Files.delete(file.toPath());
				}
			}catch(Exception e) {
				// ignore
			}
		}
		
	}
	
	private static void getAndCheckJWK(File file, RemoteStoreConfig remoteConfig) throws UtilsException {
		JWK jwk = RemoteStoreUtils.readJWK(file.getName(), remoteConfig);
		if(jwk==null) {
			throw new UtilsException("JWK not read");
		}
		String kidPrefix = "kid: ";
		String kid = jwk.getJWK().getKeyID();
		print(kidPrefix+kid);
		String expected = "openspcoop";
		if(!expected.equals(kid)) {
			throw newExceptionJWKDiff(expected,kid);
		}
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		jwk = RemoteStoreUtils.readJWK(file.getName(), remoteConfig, bout);
		if(jwk==null) {
			throw new UtilsException("JWK not read");
		}
		kid = jwk.getJWK().getKeyID();
		print(kidPrefix+kid);
		if(!expected.equals(kid)) {
			throw newExceptionJWKDiff(expected,kid);
		}
		
		if(bout.size()<=0) {
			throw new UtilsException("JWK byte[] not read");
		}
		jwk = new JWK(bout.toString());
		kid = jwk.getJWK().getKeyID();
		print(kidPrefix+kid);
		if(!expected.equals(kid)) {
			throw newExceptionJWKDiff(expected,kid);
		}
	}
	private static UtilsException newExceptionJWKDiff(String expected, String kid) {
		return new UtilsException("Kid expected '"+expected+"', found kid '"+kid+"'");
	}
	
	
	
	public static void testPublicKey() throws UtilsException {
		
		print("========================= remote store PublicKey ==============================");
		
		byte[] publicKey = Utilities.getAsByteArray(CertificateTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsa.publicKey.pem"));
		File file = null;
		try {
			file = File.createTempFile("test", ".pem");
			FileSystemUtilities.writeFile(file, publicKey);
			
			RemoteStoreConfig remoteConfig = new RemoteStoreConfig("TEST");
			remoteConfig.setBaseUrl(file.getParentFile().getAbsolutePath());
			
			remoteConfig.setIdMode(RemoteKeyIdMode.URL_SUFFIX);
			getAndCheckPublicKey(file, remoteConfig);
			
			remoteConfig.setBaseUrl(file.getAbsolutePath());
			remoteConfig.setIdMode(RemoteKeyIdMode.URL_PARAMETER);
			remoteConfig.setParameterName("test");
			getAndCheckPublicKey(file, remoteConfig);
			
			remoteConfig.setBaseUrl(file.getAbsolutePath());
			remoteConfig.setIdMode(RemoteKeyIdMode.HEADER);
			remoteConfig.setParameterName("test");
			remoteConfig.setKeyAlgorithm(KeyUtils.ALGO_RSA);
			getAndCheckPublicKey(file, remoteConfig);
			
		}
		catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		finally {
			try {
				if(file!=null) {
					Files.delete(file.toPath());
				}
			}catch(Exception e) {
				// ignore
			}
		}
		
	}
	
	private static void getAndCheckPublicKey(File file, RemoteStoreConfig remoteConfig) throws UtilsException {
		PublicKey publicKey = RemoteStoreUtils.readPublicKey(file.getName(), remoteConfig);
		if(publicKey==null) {
			throw new UtilsException("PublicKey not read");
		}
		String algoPrefix = "algorithm: ";
		String algo = publicKey.getAlgorithm();
		print(algoPrefix+algo);
		String expected = "RSA";
		if(!expected.equals(algo)) {
			throw newExceptionPublicKeyDiff(expected,algo);
		}
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		publicKey = RemoteStoreUtils.readPublicKey(file.getName(), remoteConfig, bout);
		if(publicKey==null) {
			throw new UtilsException("PublicKey not read");
		}
		algo = publicKey.getAlgorithm();
		print(algoPrefix+algo);
		if(!expected.equals(algo)) {
			throw newExceptionPublicKeyDiff(expected,algo);
		}
		
		if(bout.size()<=0) {
			throw new UtilsException("PublicKey byte[] not read");
		}
		publicKey = KeyUtils.getInstance(remoteConfig.getKeyAlgorithm()).getPublicKey(bout.toByteArray());
		if(publicKey==null) {
			throw new UtilsException("PublicKey not read from bytes");
		}
		algo = publicKey.getAlgorithm();
		print(algoPrefix+algo);
		if(!expected.equals(algo)) {
			throw newExceptionPublicKeyDiff(expected,algo);
		}
	}
	private static UtilsException newExceptionPublicKeyDiff(String expected, String cn) {
		return new UtilsException("PublicKey algorithm expected '"+expected+"', found '"+cn+"'");
	}
	
	
	
	public static void testX509() throws UtilsException {
		
		print("========================= remote store X509 ==============================");
		
		byte[] certificate = Utilities.getAsByteArray(CertificateTest.class.getResourceAsStream(CertificateTest.PREFIX+"govway_test.cer"));
		File file = null;
		try {
			file = File.createTempFile("x509", ".pem");
			FileSystemUtilities.writeFile(file, certificate);
			
			RemoteStoreConfig remoteConfig = new RemoteStoreConfig("TEST");
			print("StoreName: "+remoteConfig.getStoreName());
			remoteConfig.setBaseUrl(file.getParentFile().getAbsolutePath());
			
			remoteConfig.setIdMode(RemoteKeyIdMode.URL_SUFFIX);
			getAndCheckX509(file, remoteConfig);
			
			remoteConfig.setBaseUrl(file.getAbsolutePath());
			remoteConfig.setIdMode(RemoteKeyIdMode.URL_PARAMETER);
			remoteConfig.setParameterName("test");
			getAndCheckX509(file, remoteConfig);
			
			remoteConfig.setBaseUrl(file.getAbsolutePath());
			remoteConfig.setIdMode(RemoteKeyIdMode.HEADER);
			remoteConfig.setParameterName("test");
			remoteConfig.setKeyAlgorithm(KeyUtils.ALGO_RSA);
			getAndCheckX509(file, remoteConfig);
			
		}
		catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		finally {
			try {
				if(file!=null) {
					Files.delete(file.toPath());
				}
			}catch(Exception e) {
				// ignore
			}
		}
		
	}
	private static void getAndCheckX509(File file, RemoteStoreConfig remoteConfig) throws UtilsException {
		Certificate certificate = RemoteStoreUtils.readX509(file.getName(), remoteConfig);
		if(certificate==null) {
			throw new UtilsException("Certificate not read");
		}
		String cn = certificate.getCertificate().getSubject().getCN();
		print("CN: "+cn);
		String expected = "govway_test";
		if(!expected.equals(cn)) {
			throw newExceptionX509Diff(expected,cn);
		}
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		certificate = RemoteStoreUtils.readX509(file.getName(), remoteConfig, bout);
		if(certificate==null) {
			throw new UtilsException("Certificate not read");
		}
		cn = certificate.getCertificate().getSubject().getCN();
		print("CN: "+cn);
		if(!expected.equals(cn)) {
			throw newExceptionX509Diff(expected,cn);
		}
		
		if(bout.size()<=0) {
			throw new UtilsException("Certificate byte[] not read");
		}
		certificate = ArchiveLoader.load(bout.toByteArray());
		if(certificate==null) {
			throw new UtilsException("Certificate not read from bytes");
		}
		cn = certificate.getCertificate().getSubject().getCN();
		print("CN: "+cn);
		if(!expected.equals(cn)) {
			throw newExceptionX509Diff(expected,cn);
		}
	}
	private static UtilsException newExceptionX509Diff(String expected, String cn) {
		return new UtilsException("Certificate CN expected '"+expected+"', found '"+cn+"'");
	}
}
