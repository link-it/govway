/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.certificate.ocsp.test;

import java.io.File;
import java.io.InputStream;
import java.security.Security;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.CRLDistributionPoint;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.certificate.ocsp.CertificateStatus;
import org.openspcoop2.utils.certificate.ocsp.CertificateStatusCode;
import org.openspcoop2.utils.certificate.ocsp.OCSPConfig;
import org.openspcoop2.utils.certificate.ocsp.OCSPManager;
import org.openspcoop2.utils.certificate.ocsp.OCSPRequestParams;
import org.openspcoop2.utils.certificate.ocsp.OCSPResourceReader;
import org.openspcoop2.utils.certificate.ocsp.OCSPValidator;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.SSLUtilities;
import org.slf4j.Logger;

/**
 * OCSPTest
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OCSPTest {

	public static void main(String[] args) throws Exception {

		LoggerWrapperFactory.setDefaultConsoleLogConfiguration(LEVEL_LOG, "%p %m %n %n");
		//LoggerWrapperFactory.setDefaultLogConfiguration(LEVEL_LOG, true, "%p %m %n %n", new File("/tmp/ocsptest.log"), null);
		
		checkGovWay();
		
		checkGoogle();
		
		checkAlternativeCrlCheck();
		
		String com = null;
		if(args!=null && args.length>0) {
			String tmp = args[0];
			if(tmp!=null && StringUtils.isNotEmpty(tmp)) {
				com = tmp;
			}
		}
		
		String waitStartupServer = null;
		if(args!=null && args.length>1) {
			String tmp = args[1];
			if(tmp!=null && StringUtils.isNotEmpty(tmp)) {
				waitStartupServer = tmp;
			}
		}
		
		checkOCSPResponse_signedByResponderCertificate_case2(com, waitStartupServer);
		
		checkOCSPResponse_signedByResponderCertificate_case2_differentNonce(com, waitStartupServer);
		
		checkOCSPResponse_signedByResponderCertificate_case3(com, waitStartupServer);
		
		System.out.println("\n\nTestsuite completata con successo");
	}

	public static Level LEVEL_LOG = Level.DEBUG;
	
	private static Logger log = null;
	private static boolean initialized = false;
	private static OCSPResourceReader ocspResourceReader;
	private static OCSPManager ocspManager;
	private static synchronized void _initialize() throws Exception {
		if(!initialized) {
			
			log = LoggerWrapperFactory.getLogger(OCSPTest.class);
			
			File f = null;
			try(InputStream is = OCSPTest.class.getResourceAsStream("/org/openspcoop2/utils/certificate/ocsp/test/ocsp_test.properties")){
				
				byte[] content = Utilities.getAsByteArray(is);
				
				f = File.createTempFile("test", ".properties");
				FileSystemUtilities.writeFile(f, content);
			
				OCSPManager.init(f.getAbsoluteFile(), true, log);
			}
			finally {
				if(f!=null) {
					f.delete();
				}
			}
						
			ocspResourceReader = new OCSPResourceReader();
			
			ocspManager = OCSPManager.getInstance();
			
			initialized = true;
		}
	}
	private static void initialize() throws Exception {
		if(!initialized) {
			_initialize();
		}
	}
	
	public static void checkGovWay() throws Exception {
		
		Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 2); // lasciare alla posizione 1 il provider 'SUN'
		try {
			
			initialize();
			
			String host = "govway.org";
			int port = 443;
			
			_check(host, port, "govway");
			
			System.out.println("\n\nTest 'checkGovWay' terminato");
			
		}finally {
			Security.removeProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
		}
	}
	
	public static void checkGoogle() throws Exception {
		
		Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 2); // lasciare alla posizione 1 il provider 'SUN'
		try {
			
			initialize();
			
			String host = "google.it";
			int port = 443;
			
			_check(host, port, "google");
			
			System.out.println("\n\nTest 'checkGoogle' terminato");
			
		}finally {
			Security.removeProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
		}
	}
		
	public static void checkAlternativeCrlCheck() throws Exception {
		
		Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 2); // lasciare alla posizione 1 il provider 'SUN'
		try {
			
			initialize();
			
			String configType = "alternativeCrlCheck";
			
			KeyStore trustStoreCA = null;
			try(InputStream is = OCSPTest.class.getResourceAsStream("/org/openspcoop2/utils/certificate/ocsp/test/crl/trustStore_ca.jks")){
				byte[] content = Utilities.getAsByteArray(is);
				trustStoreCA = new KeyStore(content, "jks", "123456");
			}
			
			File fCrl = null;
			try(InputStream is = OCSPTest.class.getResourceAsStream("/org/openspcoop2/utils/certificate/ocsp/test/crl/ExampleCA.crl")){
				byte[] content = Utilities.getAsByteArray(is);
				fCrl = File.createTempFile("test", ".crl");
				FileSystemUtilities.writeFile(fCrl, content);
				
				String crl = fCrl.getAbsolutePath();
				
				// certificato valido
				try(InputStream isCert = OCSPTest.class.getResourceAsStream("/org/openspcoop2/utils/certificate/ocsp/test/crl/ExampleClient1.crt")){
					String certificate = Utilities.getAsString(isCert, Charset.UTF_8.getValue());
					_check(certificate, "AlternativeCrlCheck-ExampleClient1", trustStoreCA, crl, configType, CertificateStatusCode.GOOD );
				}
				
				// certificato scaduto
				try(InputStream isCert = OCSPTest.class.getResourceAsStream("/org/openspcoop2/utils/certificate/ocsp/test/crl/ExampleClientScaduto.crt")){
					String certificate = Utilities.getAsString(isCert, Charset.UTF_8.getValue());
					_check(certificate, "AlternativeCrlCheck-ExampleClientScaduto", trustStoreCA, crl, configType, CertificateStatusCode.EXPIRED );
				}
				
				// certificato revocato
				try(InputStream isCert = OCSPTest.class.getResourceAsStream("/org/openspcoop2/utils/certificate/ocsp/test/crl/ExampleClientRevocato.crt")){
					String certificate = Utilities.getAsString(isCert, Charset.UTF_8.getValue());
					_check(certificate, "AlternativeCrlCheck-ExampleClientRevocato", trustStoreCA, crl, configType, CertificateStatusCode.REVOKED );
				}
			}
			finally {
				if(fCrl!=null) {
					fCrl.delete();
				}
			}
	
			System.out.println("\n\nTest 'checkAlternativeCrlCheck' terminato");
			
		}finally {
			Security.removeProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
		}
	}
	
	private static String normalizeOpensslCommand(String opensslCmd) {
		String com = "/usr/bin/openssl";
		if(opensslCmd!=null && StringUtils.isNotEmpty(opensslCmd) && !"${opensslCmd}".equals(opensslCmd)) {
			String tmp = opensslCmd;
			if(tmp!=null && StringUtils.isNotEmpty(tmp)) {
				com = tmp;
			}
		}
		return com;
	}
	private static int normalizeWaitStartupServer(String waitStartupServer) {
		int WAIT_STARTUP_SERVER = 3000;
		if(waitStartupServer!=null && StringUtils.isNotEmpty(waitStartupServer) && !"${waitStartupServerMs}".equals(waitStartupServer)) {
			String tmp = waitStartupServer;
			if(tmp!=null && StringUtils.isNotEmpty(tmp)) {
				WAIT_STARTUP_SERVER = Integer.valueOf(tmp);
			}
		}
		return WAIT_STARTUP_SERVER;
	}
	
	public static void checkOCSPResponse_signedByResponderCertificate_case2(String opensslCommandParam, String waitStartupServerParam) throws Exception {
		
		Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 2); // lasciare alla posizione 1 il provider 'SUN'
		try {
		
			initialize();
			
			String configType_nonce = "signedByResponderCertificate_case2";
			KeyStore trustStore_nonce = null;
			
			String configType_no_nonce = "signedByResponderCertificate_case2" + "-no-nonce";
			KeyStore trustStore_no_nonce = null;
			try(InputStream is = OCSPTest.class.getResourceAsStream("/org/openspcoop2/utils/certificate/ocsp/test/ocsp/ca_TEST.jks")){
				byte[] content = Utilities.getAsByteArray(is);
				trustStore_no_nonce = new KeyStore(content, "jks", "123456");
			}
					
			OpenSSLThread sslThread = new OpenSSLThread(normalizeOpensslCommand(opensslCommandParam), 64900, "ocsp/ocsp_TEST.cert.pem", "ocsp/ocsp_TEST.key.pem", "ocsp/ca_TEST.cert.pem", "ocsp/index.txt", false);
			sslThread.start();
			//System.out.println("START");
			
			Utilities.sleep(normalizeWaitStartupServer(waitStartupServerParam));
			
			try {
				// generazione nonce request e response
				_checkOCSPResponse_signedByResponderCertificate("checkOCSPResponse_signedByResponderCertificate_case2", configType_nonce, trustStore_nonce);
				
				// non vengono generati i nonce values
				_checkOCSPResponse_signedByResponderCertificate("checkOCSPResponse_signedByResponderCertificate_case2_no-nonce", configType_no_nonce, trustStore_no_nonce);
			}
			finally {
				//System.out.println("STOP...");
				sslThread.setStop(true);
				sslThread.waitShutdown(200, 10000);
				sslThread.close();
				//System.out.println("STOP");
			}
			
			System.out.println("\n\nTest 'checkOCSPResponse_signedByResponderCertificate_case2' terminato");
			
		}finally {
			Security.removeProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
		}
	}
	
	public static void checkOCSPResponse_signedByResponderCertificate_case2_differentNonce(String opensslCommandParam, String waitStartupServerParam) throws Exception {
		
		Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 2); // lasciare alla posizione 1 il provider 'SUN'
		try {
		
			initialize();
					
			String configType_different_nonce = "signedByResponderCertificate_case2-different-nonce";
			KeyStore trustStore_different_nonce = null;
			
			OpenSSLThread sslThread = new OpenSSLThread(normalizeOpensslCommand(opensslCommandParam), 64902, "ocsp/ocsp_TEST.cert.pem", "ocsp/ocsp_TEST.key.pem", "ocsp/ca_TEST.cert.pem", "ocsp/index.txt", true);
			sslThread.start();
			//System.out.println("START");
			
			Utilities.sleep(normalizeWaitStartupServer(waitStartupServerParam));
			
			try {
				// vengono generati nonce values differenti tra richiesta e risposta
				try {
					_checkOCSPResponse_signedByResponderCertificate("checkOCSPResponse_signedByResponderCertificate_case2_different-nonce", configType_different_nonce, trustStore_different_nonce);
					throw new Exception("Attesa eccezione");
				}catch(Exception e) {
					if(e.getMessage().contains("OCSP Response not valid: nonces do not match")) {
						System.out.println("Generata eccezione attesa: "+e.getMessage());
					}
					else {
						throw e;
					}
				}
			}
			finally {
				//System.out.println("STOP...");
				sslThread.setStop(true);
				sslThread.waitShutdown(200, 10000);
				sslThread.close();
				//System.out.println("STOP");
			}
			
			System.out.println("\n\nTest 'checkOCSPResponse_signedByResponderCertificate_case2_differentNonce' terminato");
			
		}finally {
			Security.removeProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
		}
	}
	
	public static void checkOCSPResponse_signedByResponderCertificate_case3(String opensslCommandParam, String waitStartupServerParam) throws Exception {
		
		Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 2); // lasciare alla posizione 1 il provider 'SUN'
		try {
		
			initialize();
			
			String configType_nonce = "signedByResponderCertificate_case3";
			KeyStore trustStore_nonce = null;
			
			String configType_no_nonce = "signedByResponderCertificate_case3" + "-no-nonce";
			KeyStore trustStore_no_nonce = null;
			try(InputStream is = OCSPTest.class.getResourceAsStream("/org/openspcoop2/utils/certificate/ocsp/test/ocsp/ca_TEST.jks")){
				byte[] content = Utilities.getAsByteArray(is);
				trustStore_no_nonce = new KeyStore(content, "jks", "123456");
			}
			
			String configType_noextendedkeyusage = "signedByResponderCertificate_case3"+ "-noextendedkeyusage";
			KeyStore trustStore_noextendedkeyusage = null;
			
			String configType_noca = "signedByResponderCertificate_case3"+ "-noca";
			KeyStore trustStore_noca = null;
			
			OpenSSLThread sslThread = new OpenSSLThread(normalizeOpensslCommand(opensslCommandParam), 64901, "crl/ExampleClient1.crt", "crl/ExampleClient1.key", "ocsp/ca_TEST.cert.pem", "ocsp/index.txt", false);
			sslThread.start();
			//System.out.println("START");
			
			Utilities.sleep(normalizeWaitStartupServer(waitStartupServerParam));
			
			try {
				// generazione nonce request e response
				_checkOCSPResponse_signedByResponderCertificate("checkOCSPResponse_signedByResponderCertificate_case3", configType_nonce, trustStore_nonce);
				
				// non vengono generati i nonce values
				_checkOCSPResponse_signedByResponderCertificate("checkOCSPResponse_signedByResponderCertificate_case3_no-nonce", configType_no_nonce, trustStore_no_nonce);
				
				// il certificato di firma utilizzato per firmare la risposta non contiene l'extended key usage OCSP_SIGNING
				try {
					_checkOCSPResponse_signedByResponderCertificate("checkOCSPResponse_signedByResponderCertificate_case3_noextendedkeyusage", configType_noextendedkeyusage, trustStore_noextendedkeyusage);
					throw new Exception("Attesa eccezione");
				}catch(Exception e) {
					if(e.getMessage().contains("Signing certificate not valid for signing OCSP responses: extended key usage 'OCSP_SIGNING' not found")) {
						System.out.println("Generata eccezione attesa: "+e.getMessage());
					}
					else {
						throw e;
					}
				}
				
				// utilizzo un truststore che non contiene la CA del ocsp responder
				try {
					_checkOCSPResponse_signedByResponderCertificate("checkOCSPResponse_signedByResponderCertificate_case3_noca", configType_noca, trustStore_noca);
					throw new Exception("Attesa eccezione");
				}catch(Exception e) {
					if(e.getMessage().contains("Signing certificate is not authorized to sign OCSP responses: unauthorized different issuer certificate 'C=IT,ST=Italy,L=Pisa,O=Example,CN=ExampleCA'")) {
						System.out.println("Generata eccezione attesa: "+e.getMessage());
					}
					else {
						throw e;
					}
				}
				
			}
			finally {
				//System.out.println("STOP...");
				sslThread.setStop(true);
				sslThread.waitShutdown(200, 10000);
				sslThread.close();
				//System.out.println("STOP");
			}
			
			System.out.println("\n\nTest 'checkOCSPResponse_signedByResponderCertificate_case3' terminato");
			
		}finally {
			Security.removeProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
		}
	}
	
	private static void _checkOCSPResponse_signedByResponderCertificate(String scenario, String configType, KeyStore trustStore) throws Exception {
		// certificato valido
		try(InputStream isCert = OCSPTest.class.getResourceAsStream("/org/openspcoop2/utils/certificate/ocsp/test/ocsp/ee_TEST_Client-test.esempio.it.cert.pem")){
			String certificate = Utilities.getAsString(isCert, Charset.UTF_8.getValue());
			_check(certificate, scenario+"+ExampleCertificatoValido", trustStore, null, configType, CertificateStatusCode.GOOD );
		}
		
		// certificato revocato
		try(InputStream isCert = OCSPTest.class.getResourceAsStream("/org/openspcoop2/utils/certificate/ocsp/test/ocsp/ee_TEST_test.esempio.it.cert.pem")){
			String certificate = Utilities.getAsString(isCert, Charset.UTF_8.getValue());
			_check(certificate, scenario+"-ExampleCertificatoRevocato", trustStore, null, configType, CertificateStatusCode.REVOKED );
		}
	}
	
	private static void _check(String host, int port, String configType) throws Exception {
		
		String certificate = SSLUtilities.readPeerCertificates(host, port);
		_check(certificate, ("Address: "+host+":"+port), null, null, configType, null );
		
	}
	
	private static void _check(String certificate, String description, 
			KeyStore trustStore, String crlInput, String configType, CertificateStatusCode expected) throws Exception {
		
		System.out.println("\n\n===================================================");
		System.out.println(description);
		System.out.println("");
		System.out.println("PEM: \n"+certificate);
		
		OCSPConfig config = ocspManager.getOCSPConfig(configType);
		
		Certificate cert = ArchiveLoader.loadChain(certificate.getBytes());
		
		CertificateInfo certificatePrincipal = cert.getCertificate();
		
		if(expected==null) {
			expected = CertificateStatusCode.GOOD;
		}
		
		System.out.println("****************");
		System.out.println("");
		System.out.println("Principal Certificate");
		System.out.println("Subject: "+certificatePrincipal.getSubject().toString());
		System.out.println("Issuer: "+certificatePrincipal.getIssuer().toString());
		System.out.println("CAIssuer: "+(certificatePrincipal.getAuthorityInformationAccess()!=null ? certificatePrincipal.getAuthorityInformationAccess().getCAIssuers() : null));
		System.out.println("OCSP: "+(certificatePrincipal.getAuthorityInformationAccess()!=null ? certificatePrincipal.getAuthorityInformationAccess().getOCSPs() : null));
		if(certificatePrincipal.getCRLDistributionPoints()!=null && 
				certificatePrincipal.getCRLDistributionPoints().getCRLDistributionPoints()!=null && 
				!certificatePrincipal.getCRLDistributionPoints().getCRLDistributionPoints().isEmpty()) {
			for (CRLDistributionPoint point : certificatePrincipal.getCRLDistributionPoints().getCRLDistributionPoints()) {
				System.out.println("CRLIssuer: "+point.getCRLIssuers());
				System.out.println("CRL: "+point.getDistributionPointNames());
			}
		}
		else {
			System.out.println("CRL: null");
		}
		
		OCSPRequestParams params = OCSPRequestParams.build(log, cert.getCertificate().getCertificate(), trustStore, config, ocspResourceReader);
		CertificateStatus certificatePrincipalStatus = OCSPValidator.check(log, params, crlInput);
		System.out.println("Stato: "+certificatePrincipalStatus);
		if(!expected.equals(certificatePrincipalStatus.getCode())) {
			throw new Exception("Atteso stato '"+expected+"'");
		}
		if(certificatePrincipalStatus.isREVOKED()) {
			if(certificatePrincipalStatus.getRevocationTime()==null) {
				throw new Exception("Atteso revocation time");
			}
			if(certificatePrincipalStatus.getRevocationReason()==null) {
				throw new Exception("Atteso revocation reason");
			}
		}
		if(certificatePrincipalStatus.isEXPIRED()) {
			if(certificatePrincipalStatus.getRevocationTime()==null) {
				throw new Exception("Atteso revocation time");
			}
			if(certificatePrincipalStatus.getDetails()==null) {
				throw new Exception("Atteso details");
			}
		}
		
		System.out.println("");
		System.out.println("Certificate Chain: "+(cert.getCertificateChain()!=null ? cert.getCertificateChain().size() : 0));
		int index = 1;
		for (CertificateInfo c : cert.getCertificateChain()) {
			System.out.println("****************");
			System.out.println("");
			System.out.println("Certificate Chain '"+index+"'");
			System.out.println("Subject: "+c.getSubject().toString());
			System.out.println("Issuer: "+c.getIssuer().toString());
			System.out.println("CAIssuer: "+(c.getAuthorityInformationAccess()!=null ? c.getAuthorityInformationAccess().getCAIssuers() : null));
			System.out.println("OCSP: "+(c.getAuthorityInformationAccess()!=null ? c.getAuthorityInformationAccess().getOCSPs() : null));
			if(c.getCRLDistributionPoints()!=null && 
					c.getCRLDistributionPoints().getCRLDistributionPoints()!=null && 
					!c.getCRLDistributionPoints().getCRLDistributionPoints().isEmpty()) {
				for (CRLDistributionPoint point : c.getCRLDistributionPoints().getCRLDistributionPoints()) {
					System.out.println("CRLIssuer: "+point.getCRLIssuers());
					System.out.println("CRL: "+point.getDistributionPointNames());
				}
			}
			else {
				System.out.println("CRL: null");
			}
			
			params = OCSPRequestParams.build(log, c.getCertificate(), trustStore, config, ocspResourceReader);
			certificatePrincipalStatus = OCSPValidator.check(log, params, crlInput);
			System.out.println("Stato: "+certificatePrincipalStatus);
			if(!expected.equals(certificatePrincipalStatus.getCode())) {
				throw new Exception("Atteso stato '"+expected+"'");
			}
			
			index++;
		}
	}
}
