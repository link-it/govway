/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

package org.openspcoop2.pdd.logger.filetrace.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.transazioni.CredenzialeMittente;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.core.transazioni.constants.TipoAPI;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.core.transazioni.utils.CredenzialiMittente;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeTokenClient;
import org.openspcoop2.monitor.sdk.transaction.FaseTracciamento;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.token.InformazioniJWTClientAssertion;
import org.openspcoop2.pdd.core.token.InformazioniNegoziazioneToken;
import org.openspcoop2.pdd.core.token.InformazioniNegoziazioneToken_DatiRichiesta;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.core.token.SorgenteInformazioniToken;
import org.openspcoop2.pdd.core.token.attribute_authority.InformazioniAttributi;
import org.openspcoop2.pdd.core.token.parser.BasicNegoziazioneTokenParser;
import org.openspcoop2.pdd.core.token.parser.BasicTokenParser;
import org.openspcoop2.pdd.core.token.parser.TipologiaClaims;
import org.openspcoop2.pdd.core.token.parser.TipologiaClaimsNegoziazione;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.logger.filetrace.FileTraceConfig;
import org.openspcoop2.pdd.logger.filetrace.FileTraceManager;
import org.openspcoop2.pdd.logger.transazioni.TransazioneUtilities;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.ChannelSecurityToken;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.PDNDTokenInfo;
import org.openspcoop2.protocol.sdk.PDNDTokenInfoDetails;
import org.openspcoop2.protocol.sdk.RestMessageSecurityToken;
import org.openspcoop2.protocol.sdk.SecurityToken;
import org.openspcoop2.protocol.sdk.dump.Messaggio;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.MerlinKeystore;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.ArchiveType;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.certificate.JWK;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.certificate.KeyUtils;
import org.openspcoop2.utils.certificate.KeystoreType;
import org.openspcoop2.utils.certificate.SymmetricKeyUtils;
import org.openspcoop2.utils.certificate.hsm.HSMManager;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.certificate.test.KeystoreTest;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.CompressorType;
import org.openspcoop2.utils.io.CompressorUtilities;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.io.HexBinaryUtilities;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.resources.MapReader;
import org.openspcoop2.utils.security.Decrypt;
import org.openspcoop2.utils.security.DecryptWrapKey;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWTOptions;
import org.openspcoop2.utils.security.JsonDecrypt;
import org.openspcoop2.utils.security.JsonUtils;
import org.openspcoop2.utils.security.test.EncryptTest;
import org.openspcoop2.utils.security.test.SignatureTest;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;

import com.nimbusds.jose.jwk.KeyUse;

/**     
 * Test
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FileTraceTest {

	private static final String DIR_TMP = "/tmp/logs/";
	
	private static final String ENV_NAME_1 = "FILETRACE-TEST-ENV";
	private static final String ENV_VALUE_1 = "envValue";
	private static final String ENV_NAME_2 = "FILETRACE-TEST-ENV2";
	private static final String ENV_VALUE_2 = "envValue2";
	
	private static final List<String> ENCRYPT_TEST = new ArrayList<>();
	static {
		ENCRYPT_TEST.add("encJavaSymm");
		ENCRYPT_TEST.add("encJoseSymm");
		ENCRYPT_TEST.add("encJoseSymmDirect");
		
		ENCRYPT_TEST.add("encJavaJCEKS");
		ENCRYPT_TEST.add("encJoseJCEKS");
		ENCRYPT_TEST.add("encJoseJCEKSDirect");
		
		ENCRYPT_TEST.add("encJavaPublic");
		ENCRYPT_TEST.add("encJavaPublicWrapKey");
		ENCRYPT_TEST.add("encJavaPublicWrapKeyHex");
		ENCRYPT_TEST.add("encJosePublic");
		
		ENCRYPT_TEST.add("encJavaJWKsSymm");
		ENCRYPT_TEST.add("encJavaJWKsAsymm");
		ENCRYPT_TEST.add("encJoseJWKsSymm");
		ENCRYPT_TEST.add("encJoseJWKsAsymm");
		
		ENCRYPT_TEST.add("encJavaJKS");
		ENCRYPT_TEST.add("encJoseJKS");
		ENCRYPT_TEST.add("encJavaPKCS12");
		ENCRYPT_TEST.add("encJosePKCS12");
		ENCRYPT_TEST.add("encJavaPKCS11");
		ENCRYPT_TEST.add("encJosePKCS11");
	}
	
	public static void initDir() throws UtilsException, ReflectiveOperationException {
		boolean deleteDir = FileSystemUtilities.deleteDirNotEmpty(DIR_TMP,3);
		if(!deleteDir) {
			throw new UtilsException("Directory ["+DIR_TMP+"] non eliminata");
		}
		System.out.println("Directory inizializzata");
		
		updateEnv(ENV_NAME_1, ENV_VALUE_1);
		updateEnv(ENV_NAME_2, ENV_VALUE_2);
		System.out.println("ENV inizializzata ("+ENV_NAME_1+":"+System.getenv(ENV_NAME_1)+") ("+ENV_NAME_2+":"+System.getenv(ENV_NAME_2)+")");
		
		FileSystemUtilities.mkdir(DIR_TMP);
	}
	public static void emptyDir() throws UtilsException, ReflectiveOperationException, IOException {	
		
		File dir = new File(DIR_TMP);
		File [] childs = dir.listFiles();
		if(childs!=null && childs.length>0) {
			for (File file : childs) {
	            if(file.isDirectory()) {
	            	//System.out.println("ELIMINO DIR ["+file.getAbsolutePath()+"]");
	            	boolean deleteDir = FileSystemUtilities.deleteDirNotEmpty(file,3);
	        		if(!deleteDir) {
	        			throw new UtilsException("Directory ["+file+"] non eliminata");
	        		}
				}
				else {
					// Sovrascrittura del contenuto del file con un FileOutputStream
					//System.out.println("SVUOTO FILE ["+file.getAbsolutePath()+"]");
	            	FileOutputStream fos = new FileOutputStream(file, false);
	            	//fos.write("REINIT".getBytes());
					fos.close();
				}
	        }
		}
		
		System.out.println("Directory svuotata");
		
		updateEnv(ENV_NAME_1, ENV_VALUE_1);
		updateEnv(ENV_NAME_2, ENV_VALUE_2);
		System.out.println("ENV inizializzata ("+ENV_NAME_1+":"+System.getenv(ENV_NAME_1)+") ("+ENV_NAME_2+":"+System.getenv(ENV_NAME_2)+")");
		
		FileSystemUtilities.mkdir(DIR_TMP);
	}
	@SuppressWarnings({ "unchecked" })
	private static void updateEnv(String name, String val) throws ReflectiveOperationException {
		Map<String, String> env = System.getenv();
		Field field = env.getClass().getDeclaredField("m");
	    field.setAccessible(true);
	    ((Map<String, String>) field.get(env)).put(name, val);
	}
	
	
	
	public static void main(String [] args) throws Exception{

		initDir();
		
		boolean log4j = true;
		boolean requestWithPayload = true;
		
		test(TipoPdD.APPLICATIVA, !log4j, 0, requestWithPayload);
		test(TipoPdD.APPLICATIVA, !log4j, 16, requestWithPayload); // errore autenticazione
		
		test(TipoPdD.DELEGATA, !log4j, 0, requestWithPayload);
		test(TipoPdD.DELEGATA, !log4j, 16, requestWithPayload); // errore autenticazione
		
		test(TipoPdD.APPLICATIVA, log4j, 0, requestWithPayload);
		test(TipoPdD.APPLICATIVA, log4j, 16, requestWithPayload); // errore autenticazione
		
		test(TipoPdD.DELEGATA, log4j, 0, requestWithPayload);
		test(TipoPdD.DELEGATA, log4j, 16, requestWithPayload); // errore autenticazione
		
		test(TipoPdD.APPLICATIVA, !log4j, 0, !requestWithPayload);
		test(TipoPdD.APPLICATIVA, !log4j, 16, !requestWithPayload); // errore autenticazione
		
		test(TipoPdD.DELEGATA, !log4j, 0, !requestWithPayload);
		test(TipoPdD.DELEGATA, !log4j, 16, !requestWithPayload); // errore autenticazione
		
		test(TipoPdD.APPLICATIVA, log4j, 0, !requestWithPayload);
		test(TipoPdD.APPLICATIVA, log4j, 16, !requestWithPayload); // errore autenticazione
		
		test(TipoPdD.DELEGATA, log4j, 0, !requestWithPayload);
		test(TipoPdD.DELEGATA, log4j, 16, !requestWithPayload); // errore autenticazione
		
	}
	
	private static String hdr = "ewogICJhbGciOiAiUlMyNTYiLAogICJ0eXAiOiAiSldUIiwKICAia2lkIjogInByb3ZhIiwKICAieDVjIjogWwogICAgIk1JSS4uLi4uZjZ1UXhjbGhVaDBOaXZqNmhJSitDZDNSMS9GdncxejY5RXllT3ROd3FZU2JzdzJnamlkbzhHUGdEVWtxZFVaSThyYnRjbDIyK2x0S2VXbURhUXZOUUZnTlUreUJObTBBPSIKICBdCn0=";
	private static String payload = "ewogICJpYXQiOiAxNjUwMDMyOTAzLAogICJuYmYiOiAxNjUwMDMyOTAzLAogICJleHAiOiAxNjUwMDMzMDAzLAogICJqdGkiOiAiNGU2MWRjMTQtYmNjOC0xMWVjLTllOTktMDA1MDU2YWUwMzA3IiwKICAiYXVkIjogInRlc3QiLAogICJjbGllbnRfaWQiOiAiY1Rlc3QiLAogICJpc3MiOiAiaXNzVGVzdCIsCiAgInN1YiI6ICJzdWJUZXN0Igp9";
	private static String clientAssertionBase64 = hdr+"."+payload+".PDdXpT5htzB6JI0TdYsfsIBjH8tSV0IkIiKAI0S1IYkqcS6pOs84MsfVk3wnd1_dSiR-2KSpGzZU9s8TuGoXcdR-4oa6EN0RNJJsF8zC1KHVx1IBl4jcZGRY5vAgtKwBC87bPz7EaYXtesS3Go-fl5HTFWvZ4OR3yxvsrCfTy_ehQwVJwJy9yKrIpQFq_dSQr_xQbRBL495D9Fp4p54vNdP3IRtoDq16NUhwkH_dbQJGUJdYZ2M31bBZUvgu9RRZz_ftjI78Swwq5FIwIG7r5trwgmVebZtdLF2Ni5Vc2rL7ZNuBpH7Y_knRgRYbH4HxnMoHOU6nU8yM_ZPZyhHneA";
	
	// CertificateTest.class.getResourceAsStream(CertificateTest.PREFIX+"govway_test.pem")
	private static final String PEMCERTIFICATE = "-----BEGIN CERTIFICATE-----\n"+
		"MIIDqzCCApMCBFx+TB4wDQYJKoZIhvcNAQELBQAwgZkxGzAZBgkqhkiG9w0BCQEW\n"+
		"DGluZm9AbGluay5pdDELMAkGA1UEBhMCSVQxDjAMBgNVBAgMBUl0YWx5MRYwFAYD\n"+
		"VQQHDA1nb3Z3YXlfdGVzdF9sMRYwFAYDVQQKDA1nb3Z3YXlfdGVzdF9vMRcwFQYD\n"+
		"VQQLDA5nb3Z3YXlfdGVzdF9vdTEUMBIGA1UEAwwLZ292d2F5X3Rlc3QwHhcNMTkw\n"+
		"MzA1MTAxNDU0WhcNMzkwMjI4MTAxNDU0WjCBmTEbMBkGCSqGSIb3DQEJARYMaW5m\n"+
		"b0BsaW5rLml0MQswCQYDVQQGEwJJVDEOMAwGA1UECAwFSXRhbHkxFjAUBgNVBAcM\n"+
		"DWdvdndheV90ZXN0X2wxFjAUBgNVBAoMDWdvdndheV90ZXN0X28xFzAVBgNVBAsM\n"+
		"DmdvdndheV90ZXN0X291MRQwEgYDVQQDDAtnb3Z3YXlfdGVzdDCCASIwDQYJKoZI\n"+
		"hvcNAQEBBQADggEPADCCAQoCggEBAIYKIiJf5v4On8XusNe0kMmbkMEz4fd0aLjf\n"+
		"H795IT3MF9mLL/3QK1Lie724aRLEEIsxmHsYVxLP6gGf4rTCmC7NA5XjmHWMqPRz\n"+
		"8fNqNraJ6lVRHLDmBi8Rfr9tS/HYlNtTYo4aTwUua0WXo+UuiuC4gqS8e9Ns+liz\n"+
		"yzNHveS9/kZ2sKmUu+qmRGK6mMd0NfMcPGv/wOK6QHHjGH+5+5yGkgjX8MIAufC5\n"+
		"SZmmadOhsgttxk56AJEaybZTbCfXuSSuTtxNo6ldT4gsPdPo2pFIT02ld0wmkxaY\n"+
		"tl6gADMUuhN91/WZKQTzxPVMqekdcpPil9pBwQ6x8eqh4Z2GdvECAwEAATANBgkq\n"+
		"hkiG9w0BAQsFAAOCAQEAfYvf74G75VrQJwjSu3Q59jdwpFcdJlitG9MK1/WUVuVm\n"+
		"YURTCHOUs1XPeKFCffNpEqh3X5JAz6ir2rSCwhKNQ5I73IDvKAE/PE1ojYKqwQr2\n"+
		"QHxfkBlmfslX3XChnejE6yHkjWOYlDDWA01bEtEocCrH89mAxWACVfysCt/qkWrJ\n"+
		"76hXtZ/nLnVIE7v+f9WujRHNUPOJRjMAU7Je0/hSq0o4+7AhQhaAZg9XDDzDdX+5\n"+
		"OuJcjmVhtLqmByTErfO4euoYslflgMSEDbmNoUslZzpZqlyZKe4S8+PuzmHWzs5k\n"+
		"Zq21s4Yl5IIZPl8ZAxThukMw9oX3TCBfdgZ8eQurlw==\n"+
		"-----END CERTIFICATE-----";
	
	private static final String PDND_CLIENT_ID = "12345678-cccc-4a60-aaaa-12345678f8dd";
	private static final String PDND_CLIENT_CONSUMER_ID = "12345678-254d-bbbb-aaaa-82e210e12345";
	private static final String PDND_JSON_CLIENT = "{\"consumerId\":\""+PDND_CLIENT_CONSUMER_ID+"\",\"id\":\""+PDND_CLIENT_ID+"\"}";
			
	private static final String PDND_ORGANIZATION_CATEGORY = "Comuni e loro Consorzi e Associazioni";
	private static final String PDND_ORGANIZATION_NAME = "Comune di Test";
	private static final String PDND_ORGANIZATION_ID = "12345678-254d-bbbb-aaaa-82e210e12345";
	private static final String PDND_ORGANIZATIONEXTERNAL_ID = "c001";
	private static final String PDND_ORGANIZATION_EXTERNAL_ORIGIN = "IPA";
	private static final String PDND_JSON_ORGANIZATION = "{\"category\":\""+PDND_ORGANIZATION_CATEGORY+"\",\"externalId\":{\"id\":\""+PDND_ORGANIZATIONEXTERNAL_ID+"\",\"origin\":\""+PDND_ORGANIZATION_EXTERNAL_ORIGIN+"\"},\"id\":\""+PDND_ORGANIZATION_ID+"\",\"name\":\""+PDND_ORGANIZATION_NAME+"\"}";
	
	private static final String SERVIZIO_APPLICATIVO_FRUITORE = "AppXde23";
	
	private static final String CONTENT_TYPE_TEXT_XML_CHARSET = "text/xml; charset=\"UTF8\"";
	
	private static final String HEADER_CONTENT_XXX = "Content-XXX";
	private static final String HEADER_CONTENT_XXX_VALUE = "ADEDE";
	
	private static final String HEADER_TIPO_MESSAGGIO = "TipoMessaggio";
	
	private static final String HEADER_REQ_IN_NAME = "Req-In";
	private static final String HEADER_REQ_IN_EXAMPLE_VALUE = "ReqInExampleValue";
	private static final String HEADER_REQ_IN_2_NAME = "Req-In-2";
	private static final String HEADER_REQ_IN_2_EXAMPLE_VALUE = "ReqInExampleValue2";
	
	private static final String HEADER_REQ_OUT_NAME = "Req-Out";
	private static final String HEADER_REQ_OUT_EXAMPLE_VALUE = "ReqOutExampleValue";
	private static final String HEADER_REQ_OUT_2_NAME = "Req-Out-2";
	private static final String HEADER_REQ_OUT_2_EXAMPLE_VALUE = "ReqOutExampleValue2";
	
	private static final String HEADER_RES_IN_NAME = "Res-In";
	private static final String HEADER_RES_IN_EXAMPLE_VALUE = "ResInExampleValue";
	private static final String HEADER_RES_IN_2_NAME = "Res-In-2";
	private static final String HEADER_RES_IN_2_EXAMPLE_VALUE = "ResInExampleValue2";
	
	private static final String HEADER_RES_OUT_NAME = "Res-Out";
	private static final String HEADER_RES_OUT_EXAMPLE_VALUE = "ResOutExampleValue";
	private static final String HEADER_RES_OUT_2_NAME = "Res-Out-2";
	private static final String HEADER_RES_OUT_2_EXAMPLE_VALUE = "ResOutExampleValue2";
	
	private static final String MSG_FAILED_PREFIX = "FAILED!! \nAtteso:\n";
	
	private static final String TRACKING_PHASE = "TRACKING_PHASE"; 
	
	private static final String SECRET_KEY = "6d1d6fdeec3d92829cbadc000d3901a7"; 
	
	public static void test(TipoPdD tipoPdD, boolean log4j, int esito, boolean requestWithPayload) throws Exception{
		
		File fSecret = null;
		File fKeystoreJCEKS = null;
		File fPublic = null;
		File fPrivate = null;
		File fJWKsSymm = null;
		File fJWKsPub = null;
		File fJWKsPriv = null;
		File fJKSPub = null;
		File fJKSPriv = null;
		File fP12 = null;
		File fKeystoreP11 = null;
		try {
			Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 2); // lasciare alla posizione 1 il provider 'SUN'
			
			// // Chiave AES deve essere di 16, 24 o 32 byte che corrisponde a: 
			// AES-128: Utilizza chiavi di 128 bit (16 byte).
			// AES-192: Utilizza chiavi di 192 bit (24 byte).
			// AES-256: Utilizza chiavi di 256 bit (32 byte).
			fSecret = new File("/tmp/symmetric.secretkey");
			FileSystemUtilities.writeFile(fSecret, SECRET_KEY.getBytes()); // test con 32
			
			try( InputStream isKeystoreJCEKS = EncryptTest.class.getResourceAsStream("/org/openspcoop2/utils/security/test/example.jceks")){
				byte [] b = Utilities.getAsByteArray(isKeystoreJCEKS);
				fKeystoreJCEKS = new File("/tmp/keystore.jceks");
				FileSystemUtilities.writeFile(fKeystoreJCEKS, b);
			}
			
			byte [] publicKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsa.publicKey.pem"));
			fPublic = new File("/tmp/publicKey.pem");
			FileSystemUtilities.writeFile(fPublic, publicKey);
			
			byte [] privateKey = Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"client-test.rsa.pkcs8.privateKey.der"));
			fPrivate = new File("/tmp/privateKey.pem");
			FileSystemUtilities.writeFile(fPrivate, privateKey);
						
			try( InputStream isKeystoreJWKs = SignatureTest.class.getResourceAsStream("/org/openspcoop2/utils/security/test/keystore_symmetricKey_example.jwks") ){
				byte [] b = Utilities.getAsByteArray(isKeystoreJWKs);
				fJWKsSymm = new File("/tmp/symmetric.jwk");
				FileSystemUtilities.writeFile(fJWKsSymm, b);
			}
			
			try( InputStream isKeystoreJWKs = SignatureTest.class.getResourceAsStream("/org/openspcoop2/utils/security/test/truststore_example.jwks") ){
				byte [] b = Utilities.getAsByteArray(isKeystoreJWKs);
				fJWKsPub = new File("/tmp/public.jwk");
				FileSystemUtilities.writeFile(fJWKsPub, b);
			}
			
			try( InputStream isKeystoreJWKs = SignatureTest.class.getResourceAsStream("/org/openspcoop2/utils/security/test/keystore_example.jwks") ){
				byte [] b = Utilities.getAsByteArray(isKeystoreJWKs);
				fJWKsPriv = new File("/tmp/private.jwk");
				FileSystemUtilities.writeFile(fJWKsPriv, b);
			}
			
			try( InputStream isKeystoreJWKs = SignatureTest.class.getResourceAsStream("/org/openspcoop2/utils/security/test/truststore_example.jks") ){
				byte [] b = Utilities.getAsByteArray(isKeystoreJWKs);
				fJKSPub = new File("/tmp/truststore.jks");
				FileSystemUtilities.writeFile(fJKSPub, b);
			}
			
			try( InputStream isKeystoreJWKs = SignatureTest.class.getResourceAsStream("/org/openspcoop2/utils/security/test/keystore_example.jks") ){
				byte [] b = Utilities.getAsByteArray(isKeystoreJWKs);
				fJKSPriv = new File("/tmp/keystore.jks");
				FileSystemUtilities.writeFile(fJKSPriv, b);
			}
			
			try( InputStream isKeystoreJWKs = SignatureTest.class.getResourceAsStream("/org/openspcoop2/utils/security/test/keystore_example.p12") ){
				byte [] b = Utilities.getAsByteArray(isKeystoreJWKs);
				fP12 = new File("/tmp/keystore.p12");
				FileSystemUtilities.writeFile(fP12, b);
			}
		
			fKeystoreP11 = File.createTempFile("keystore_hsm", ".properties");
			FileSystemUtilities.writeFile(fKeystoreP11, Utilities.getAsByteArray(SignatureTest.class.getResourceAsStream(KeystoreTest.PREFIX+"govway_test_hsm.properties")) );
			HSMManager.init(fKeystoreP11, true, LoggerWrapperFactory.getLogger(FileTraceTest.class), true);
			HSMManager hsmManager = HSMManager.getInstance();
			boolean uniqueProviderInstance = true;		
			hsmManager.providerInit(LoggerWrapperFactory.getLogger(FileTraceTest.class), uniqueProviderInstance);
			System.out.println("PKCS11 Keystore registered: "+hsmManager.getKeystoreTypes());
			org.openspcoop2.utils.certificate.KeyStore keystoreP11 = hsmManager.getKeystore(KeystoreTest.PKCS11_CLIENT1);
			if(!KeystoreType.PKCS11.getNome().equalsIgnoreCase(keystoreP11.getKeystoreType())) {
				throw new Exception("Atteso tipo PKCS11, trovato '"+keystoreP11.getKeystoreType()+"'");
			}
			
			for (FaseTracciamento fase : FaseTracciamento.values()) {
				
				emptyDir();
				
				test(tipoPdD, log4j, esito, requestWithPayload, fase);
			}
		}finally {
			Security.removeProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
			
			FileSystemUtilities.deleteFile(fSecret);
			
			FileSystemUtilities.deleteFile(fKeystoreJCEKS);
			
			FileSystemUtilities.deleteFile(fPublic);
			FileSystemUtilities.deleteFile(fPrivate);
			
			FileSystemUtilities.deleteFile(fJWKsSymm);
			FileSystemUtilities.deleteFile(fJWKsPub);
			FileSystemUtilities.deleteFile(fJWKsPriv);
			
			FileSystemUtilities.deleteFile(fJKSPub);
			FileSystemUtilities.deleteFile(fJKSPriv);
			FileSystemUtilities.deleteFile(fP12);
			FileSystemUtilities.deleteFile(fKeystoreP11);
		}
	}
	
	private static void test(TipoPdD tipoPdD, boolean log4j, int esito, boolean requestWithPayload,
			FaseTracciamento faseTracciamento) throws Exception{
		
		Logger log = LoggerWrapperFactory.getLogger(FileTraceTest.class);
		ConfigurazionePdD confPdD = new ConfigurazionePdD();
		confPdD.setLoader(new Loader());
		confPdD.setLog(log);
		Map<String, IProtocolFactory<?>> m = new HashMap<>();
		m.put(CostantiLabel.TRASPARENTE_PROTOCOL_NAME, Utilities.newInstance("org.openspcoop2.protocol.trasparente.TrasparenteFactory"));
		MapReader<String, IProtocolFactory<?>> map = new MapReader<>(m, false);
		EsitiProperties.initialize(null, log, new Loader(), map);
		
		String testData = "2020-06-25_15:09:05.825";
		Date dataIngressoRichiesta = DateUtils.getSimpleDateFormatMs().parse(testData);
		Date dataIngressoRichiestaStream = new Date(dataIngressoRichiesta.getTime()+50);
		Date dataUscitaRichiesta = new Date(dataIngressoRichiesta.getTime()+100);
		Date dataUscitaRichiestaStream = new Date(dataIngressoRichiesta.getTime()+200);
		Date dataIngressoRisposta = new Date(dataIngressoRichiesta.getTime()+1200);
		Date dataIngressoRispostaStream = new Date(dataIngressoRichiesta.getTime()+1300);
		Date dataUscitaRispostaStream = new Date(dataIngressoRichiesta.getTime()+1400);
		Date dataUscitaRisposta = new Date(dataIngressoRichiesta.getTime()+1665);
		
		Transazione transazioneDTO = new Transazione();
		transazioneDTO.setProtocollo(CostantiLabel.TRASPARENTE_PROTOCOL_NAME);
		transazioneDTO.setEsito(esito);
		transazioneDTO.setIdTransazione("UUIDXX");
		transazioneDTO.setIdCorrelazioneApplicativa("XX-deXXX");
		transazioneDTO.setIdCorrelazioneApplicativaRisposta("RR-deXXXRest");
		
		transazioneDTO.setDataIngressoRichiesta(dataIngressoRichiesta);
		transazioneDTO.setDataIngressoRichiestaStream(dataIngressoRichiestaStream);
		transazioneDTO.setDataUscitaRichiesta(dataUscitaRichiesta);
		transazioneDTO.setDataUscitaRichiestaStream(dataUscitaRichiestaStream);
		transazioneDTO.setDataIngressoRisposta(dataIngressoRisposta);
		transazioneDTO.setDataIngressoRispostaStream(dataIngressoRispostaStream);
		transazioneDTO.setDataUscitaRispostaStream(dataUscitaRispostaStream);
		transazioneDTO.setDataUscitaRisposta(dataUscitaRisposta);
		
		transazioneDTO.setSocketClientAddress("127.0.0.1");
		transazioneDTO.setTransportClientAddress("10.113.13.122");
		if(requestWithPayload) {
			transazioneDTO.setTipoRichiesta("PUT");
		}
		else {
			transazioneDTO.setTipoRichiesta("GET");
		}
		String function = null;
		if(TipoPdD.APPLICATIVA.equals(tipoPdD)) {
			transazioneDTO.setPddRuolo(PddRuolo.APPLICATIVA);
			transazioneDTO.setTipoApi(TipoAPI.REST.getValoreAsInt());
			function = "in";
		}
		else {
			transazioneDTO.setPddRuolo(PddRuolo.DELEGATA);
			transazioneDTO.setTipoApi(TipoAPI.SOAP.getValoreAsInt());
			function = "out";
		}
		transazioneDTO.setUrlInvocazione("["+function+"] /govway/in/EnteEsempio/AAASOAPS1/v1/a1?dklejde=ded&adds=deded");
		transazioneDTO.setLocationConnettore("[DELETE] http://127.0.0.1:8080/govwayAPIConfig/api/PetStorec0c4269e2ebe413dadb79071/1?dad=ddede&adadad=dede");
		transazioneDTO.setClusterId("hostC1");
		transazioneDTO.setVersioneServizio(1);
		transazioneDTO.setNomeServizio("APIEsempio");
		transazioneDTO.setAzione("azioneDiProva");
		transazioneDTO.setNomeSoggettoErogatore("EnteErogatore");
		transazioneDTO.setNomeSoggettoFruitore("EnteFruitore");
		transazioneDTO.setIdMessaggioRichiesta("idMsgReqXXX");
		transazioneDTO.setServizioApplicativoFruitore(SERVIZIO_APPLICATIVO_FRUITORE);
		transazioneDTO.setCodiceRispostaIngresso("202");
		transazioneDTO.setCodiceRispostaUscita("204");
		transazioneDTO.setCredenziali("C=IT, O=Prova");
		
		InformazioniToken informazioniToken = new InformazioniToken(SorgenteInformazioniToken.JWT, new String(Base64Utilities.decode(payload.getBytes())), new BasicTokenParser(TipologiaClaims.INTROSPECTION_RESPONSE_RFC_7662));
		
		InformazioniNegoziazioneToken_DatiRichiesta datiRichiesta = new InformazioniNegoziazioneToken_DatiRichiesta();
		datiRichiesta.setPolicy("TEST");
		datiRichiesta.setTransactionId(transazioneDTO.getIdTransazione());
		datiRichiesta.setClientId("clientIdXX");
		datiRichiesta.setClientToken("clientBEARERTOKEN");
		datiRichiesta.setUsername("USERNAME");
		datiRichiesta.setJwtClientAssertion(new InformazioniJWTClientAssertion(log, clientAssertionBase64, false));
		datiRichiesta.setEndpoint("http://retrieveToken");
		datiRichiesta.setGrantType("rfc7523_x509");
		String accessToken = "AAAAATOKEN";
		String refreshToken = "AAAAAREFESHTOKEN";
		String rawResponseAccessToken = "{\"access_token\":\""+accessToken+"\",\"refresh_token\":\""+refreshToken+"\",\"scope\":\"s1 s2\",\"token_type\":\"JWT\",\"expires_in\":3700,\"refresh_expires_in\":4000}";
		InformazioniNegoziazioneToken informazioniNegoziazioneToken = new InformazioniNegoziazioneToken(datiRichiesta, rawResponseAccessToken, new BasicNegoziazioneTokenParser(TipologiaClaimsNegoziazione.OAUTH2_RFC_6749));
		
		SecurityToken securityToken = new SecurityToken();
		Certificate cert = 
				ArchiveLoader.load(ArchiveType.CER, PEMCERTIFICATE.getBytes(), 0, null);
		RestMessageSecurityToken restToken = new RestMessageSecurityToken();
		restToken.setCertificate(cert.getCertificate());
		restToken.setToken(clientAssertionBase64);
		securityToken.setAccessToken(restToken);
		securityToken.setAuthorization(restToken);
		securityToken.setIntegrity(restToken);
		securityToken.setAudit(restToken);
		ChannelSecurityToken channel = new ChannelSecurityToken();
		channel.setCertificate(cert.getCertificate());
		securityToken.setChannel(channel);
		
		transazioneDTO.setTokenInfo("ADEDADEAD.DEADADEADAD.dEADEADADEA");
		
		CredenzialiMittente credenzialiMittente = new CredenzialiMittente();

		// Differenzio per avere un requester differente!!
		boolean registraIDApplicativoToken = true;
		boolean registraUsername = true;
		boolean registraInformazioniPDND = true;
		if(requestWithPayload) {
			// POST
			if(TipoPdD.APPLICATIVA.equals(tipoPdD)) {
				registraIDApplicativoToken = false;
				registraUsername = false;
			}
		}
		else {
			// GET
			if(TipoPdD.APPLICATIVA.equals(tipoPdD)) {
				registraIDApplicativoToken = false;
				registraUsername = false;
				registraInformazioniPDND = false;
			}
			else {
				registraUsername = false;
			}
		}
		
		CredenzialeMittente tokenIssuer = new CredenzialeMittente();
		tokenIssuer.setCredenziale("issuerGoogle");
		credenzialiMittente.setTokenIssuer(tokenIssuer);
		
		CredenzialeMittente tokenSubject = new CredenzialeMittente();
		tokenSubject.setCredenziale("subjectAD5432h43242");
		credenzialiMittente.setTokenSubject(tokenSubject);
		
		CredenzialeMittente tokenClientId = new CredenzialeMittente();
		String clientIdToken = "3456ClientId";
		CredenzialeTokenClient c = null;
		if(registraIDApplicativoToken) {
			IDServizioApplicativo idSAToken = new IDServizioApplicativo();
			idSAToken.setNome("SAToken");
			idSAToken.setIdSoggettoProprietario(new IDSoggetto("gw", "SoggettoProprietarioSAToken"));
			c = new CredenzialeTokenClient(clientIdToken, idSAToken);
		}
		else {
			c = new CredenzialeTokenClient(clientIdToken, null); 
		}		
		tokenClientId.setCredenziale(c.getCredenziale());
		credenzialiMittente.setTokenClientId(tokenClientId);
		
		CredenzialeMittente tokenMail = new CredenzialeMittente();
		tokenMail.setCredenziale("info@link.it");
		credenzialiMittente.setTokenEMail(tokenMail);
		
		if(registraUsername) {
			CredenzialeMittente tokenUsername = new CredenzialeMittente();
			tokenUsername.setCredenziale("rossi");
			credenzialiMittente.setTokenUsername(tokenUsername);
		}
		
		CredenzialeMittente trasporto = new CredenzialeMittente();
		trasporto.setCredenziale("C=IT, O=Prova");
		credenzialiMittente.setTrasporto(trasporto);
		
		if(registraInformazioniPDND) {
			if(!TipoPdD.APPLICATIVA.equals(tipoPdD)) {
				securityToken.setPdnd(new PDNDTokenInfo());
			}
			
			if(TipoPdD.APPLICATIVA.equals(tipoPdD)) {
				CredenzialeMittente pdndClientJson = new CredenzialeMittente();
				pdndClientJson.setCredenziale(PDND_JSON_CLIENT);
				credenzialiMittente.setTokenPdndClientJson(pdndClientJson);
			}
			else {
				PDNDTokenInfoDetails d = new PDNDTokenInfoDetails();
				d.setDetails(PDND_JSON_CLIENT);
				d.setId(PDND_CLIENT_ID);
				securityToken.getPdnd().setClient(d);	
			}
			
			if(TipoPdD.APPLICATIVA.equals(tipoPdD)) {
				CredenzialeMittente pdndOrganizationJson = new CredenzialeMittente();
				pdndOrganizationJson.setCredenziale(PDND_JSON_ORGANIZATION);
				credenzialiMittente.setTokenPdndOrganizationJson(pdndOrganizationJson);
			}
			else {
				PDNDTokenInfoDetails d = new PDNDTokenInfoDetails();
				d.setDetails(PDND_JSON_ORGANIZATION);
				d.setId(PDND_ORGANIZATION_ID);
				securityToken.getPdnd().setOrganization(d);			
			}
			
			if(TipoPdD.APPLICATIVA.equals(tipoPdD)) {
				CredenzialeMittente pdndOrganizationName = new CredenzialeMittente();
				pdndOrganizationName.setCredenziale(FileTraceTest.PDND_ORGANIZATION_NAME);
				credenzialiMittente.setTokenPdndOrganizationName(pdndOrganizationName);
			}
		}
		
		Traccia tracciaRichiesta = new Traccia();
		tracciaRichiesta.setBusta(new Busta(CostantiLabel.TRASPARENTE_PROTOCOL_NAME));
		tracciaRichiesta.getBusta().addProperty("ProprietaTest", "Andrea");
		
		Messaggio richiestaIngresso = new Messaggio();
		richiestaIngresso.setTipoMessaggio(TipoMessaggio.RICHIESTA_INGRESSO_DUMP_BINARIO);
		if(requestWithPayload) {
			richiestaIngresso.setContentType(CONTENT_TYPE_TEXT_XML_CHARSET);
			richiestaIngresso.setBody(DumpByteArrayOutputStream.newInstance("<prova>TEST RICHIESTA_INGRESSO_DUMP_BINARIO</prova>".getBytes()));
			TransportUtils.addHeader(richiestaIngresso.getHeaders(),HttpConstants.CONTENT_TYPE, "text/xml; charset=\"UTF8\"; tipo=inRequest");
		}
		TransportUtils.addHeader(richiestaIngresso.getHeaders(),HEADER_CONTENT_XXX, HEADER_CONTENT_XXX_VALUE);
		TransportUtils.addHeader(richiestaIngresso.getHeaders(),HEADER_TIPO_MESSAGGIO, "RICHIESTA_INGRESSO_DUMP_BINARIO");
		TransportUtils.addHeader(richiestaIngresso.getHeaders(),HEADER_REQ_IN_NAME, HEADER_REQ_IN_EXAMPLE_VALUE);
		TransportUtils.addHeader(richiestaIngresso.getHeaders(),HEADER_REQ_IN_2_NAME, HEADER_REQ_IN_2_EXAMPLE_VALUE);		
		
		Messaggio richiestaUscita = new Messaggio();
		richiestaUscita.setTipoMessaggio(TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO);
		if(requestWithPayload) {
			richiestaUscita.setContentType(CONTENT_TYPE_TEXT_XML_CHARSET);
			richiestaUscita.setBody(DumpByteArrayOutputStream.newInstance("<prova>TEST RICHIESTA_USCITA_DUMP_BINARIO</prova>".getBytes()));
			TransportUtils.addHeader(richiestaUscita.getHeaders(),HttpConstants.CONTENT_TYPE, "text/xml; charset=\"UTF8\"; tipo=outRequest");
		}
		TransportUtils.addHeader(richiestaUscita.getHeaders(),HEADER_CONTENT_XXX, HEADER_CONTENT_XXX_VALUE);
		TransportUtils.addHeader(richiestaUscita.getHeaders(),HEADER_TIPO_MESSAGGIO, "RICHIESTA_USCITA_DUMP_BINARIO");
		TransportUtils.addHeader(richiestaUscita.getHeaders(),HEADER_REQ_OUT_NAME, HEADER_REQ_OUT_EXAMPLE_VALUE);
		TransportUtils.addHeader(richiestaUscita.getHeaders(),HEADER_REQ_OUT_2_NAME, HEADER_REQ_OUT_2_EXAMPLE_VALUE);		
		
		Messaggio rispostaIngresso = new Messaggio();
		rispostaIngresso.setTipoMessaggio(TipoMessaggio.RISPOSTA_INGRESSO_DUMP_BINARIO);
		rispostaIngresso.setContentType(CONTENT_TYPE_TEXT_XML_CHARSET);
		rispostaIngresso.setBody(DumpByteArrayOutputStream.newInstance("<prova>TEST RISPOSTA_INGRESSO_DUMP_BINARIO</prova>".getBytes()));
		TransportUtils.addHeader(rispostaIngresso.getHeaders(),HttpConstants.CONTENT_TYPE, "text/xml; charset=\"UTF8\"; tipo=inResponse");
		TransportUtils.addHeader(rispostaIngresso.getHeaders(),HEADER_CONTENT_XXX, HEADER_CONTENT_XXX_VALUE);
		TransportUtils.addHeader(rispostaIngresso.getHeaders(),HEADER_TIPO_MESSAGGIO, "RISPOSTA_INGRESSO_DUMP_BINARIO");
		TransportUtils.addHeader(rispostaIngresso.getHeaders(),HEADER_RES_IN_NAME, HEADER_RES_IN_EXAMPLE_VALUE);
		TransportUtils.addHeader(rispostaIngresso.getHeaders(),HEADER_RES_IN_2_NAME, HEADER_RES_IN_2_EXAMPLE_VALUE);		
		
		Messaggio rispostaUscita = new Messaggio();
		rispostaUscita.setTipoMessaggio(TipoMessaggio.RISPOSTA_USCITA_DUMP_BINARIO);
		rispostaUscita.setContentType(CONTENT_TYPE_TEXT_XML_CHARSET);
		rispostaUscita.setBody(DumpByteArrayOutputStream.newInstance("<prova>TEST RISPOSTA_USCITA_DUMP_BINARIO</prova>".getBytes()));
		TransportUtils.addHeader(rispostaUscita.getHeaders(),HttpConstants.CONTENT_TYPE, "text/xml; charset=\"UTF8\"; tipo=outResponse");
		TransportUtils.addHeader(rispostaUscita.getHeaders(),HEADER_CONTENT_XXX, HEADER_CONTENT_XXX_VALUE);
		TransportUtils.addHeader(rispostaUscita.getHeaders(),HEADER_TIPO_MESSAGGIO, "RISPOSTA_USCITA_DUMP_BINARIO");
		TransportUtils.addHeader(rispostaUscita.getHeaders(),HEADER_RES_OUT_NAME, HEADER_RES_OUT_EXAMPLE_VALUE);
		TransportUtils.addHeader(rispostaUscita.getHeaders(),HEADER_RES_OUT_2_NAME, HEADER_RES_OUT_2_EXAMPLE_VALUE);	
		
		System.setProperty("javaProperty.1", "p1");
		System.setProperty("javaProperty.2", "p2");
		
/**		Properties pTest = new Properties();
//		pTest.put("format.escape.\"", "\\\"");
//		pTest.put("format.escape.=", "\\=");
//		pTest.put("format.headers.separator","|");
//		pTest.put("format.headers.header.prefix","\"");
//		pTest.put("format.headers.header.suffix","\"");
//		pTest.put("format.headers.header.separator","=");
//		
//		pTest.put("format.property.1.campiLiberi","versione_api = ${log:versioneServizio}; api = ${log:nomeServizio}; operazione = ${log:idOperazione}; erogatore = ${log:nomeSoggettoErogatore}; soggetto_fruitore = ${log:nomeSoggettoFruitore}; applicativo_fruitore = ${log:applicativoFruitore}; id_messaggio = ${log:idMessaggioRichiesta}; id_collaborazione = ${log:idCollaborazione}; esito = ${log:esito};");
//		pTest.put("format.property.2.campiCustom","\"X-WT-IP-APP-SERVER=${log:javaProperty(jboss.server.ip)}\"|\"X-WT-HOSTNAME-APP-SERVER=${log:javaProperty(jboss.server.name)}\"|\"X-WT-SERVER-ENCODING=${log:javaProperty(file.encoding)}\"|\"X-WT-APP-SERVER-PORT=8445\"|\"X-WT-CAMPI-LIBERI=${log:property(campiLiberi)}\"");
//		pTest.put("format.property.3.campiCustomBody","X-WT-IP-APP-SERVER: ${log:javaProperty(jboss.server.ip)}\nX-WT-HOSTNAME-APP-SERVER: ${log:javaProperty(jboss.server.name)}\nX-WT-SERVER-ENCODING: ${log:javaProperty(file.encoding)}\nX-WT-APP-SERVER-PORT: 8445\nX-WT-CAMPI-LIBERI: ${log:property(campiLiberi)}");
//		pTest.put("format.property.4.httpStatusErogazioni","HTTP/1.1 ${log:httpStatusRispostaUscita} ${log:httpReasonRispostaUscita}");
//		pTest.put("format.property.5.httpStatusFruizioni","HTTP/1.1 ${log:httpStatusRispostaIngresso} ${log:httpReasonRispostaIngresso}");
//		pTest.put("format.property.6.headersRispostaErogazioni","${log:property(httpStatusErogazioni)}\n${log:rispostaUscitaHeaders(\n,: ,,)}\n${log:property(campiCustomBody)}");
//		pTest.put("format.property.7.headersRispostaFruizioni","${log:property(httpStatusFruizioni)}\n${log:rispostaIngressoHeaders(\n,: ,,)}\n${log:property(campiCustomBody)}");
//		
//		File fTmp = File.createTempFile("test", ".properties");
//		try(FileOutputStream fout = new FileOutputStream(fTmp)){
//			pTest.store(fout, "test");
//		}
//		System.out.println("\n\n\nFile prodotto:\n"+FileSystemUtilities.readFile(fTmp)+"\n\n\n");
//		//LogTraceConfig config = LogTraceConfig.getConfig(fTmp);
//		fTmp.delete();*/
		
		String path = "/org/openspcoop2/pdd/logger/filetrace/test/testFileTrace.properties";
		InputStream is = FileTraceTest.class.getResourceAsStream(path);
		FileTraceConfig.init(is, path, true);
		FileTraceConfig config = FileTraceConfig.getConfig(new File(path), true); // inizializzato sopra
			
		Map<String, Serializable> attributes = new HashMap<>();
		attributes.put("a1", "v1");
		ArrayList<String> l2 = new ArrayList<>();
		l2.add("v2a");
		l2.add("v2b");
		attributes.put("a2", l2);
		InformazioniAttributi informazioniAttributi = null;
		if(TipoPdD.APPLICATIVA.equals(tipoPdD)) {
			informazioniAttributi = new InformazioniAttributi();
			informazioniAttributi.setAttributes(attributes);
		}
		else {
			l2.add("v2c");
			InformazioniAttributi informazioniAttributiAA1 = new InformazioniAttributi("AA1");
			informazioniAttributiAA1.setAttributes(attributes);
			InformazioniAttributi informazioniAttributiAA2 = new InformazioniAttributi("AA2");
			informazioniAttributiAA2.setAttributes(attributes);
			informazioniAttributi = new InformazioniAttributi(false, informazioniAttributiAA1,informazioniAttributiAA2);
		}
		
		
		test(tipoPdD, log4j, requestWithPayload,
				faseTracciamento,
				log, config, credenzialiMittente, 
				informazioniToken,
				informazioniAttributi,
				informazioniNegoziazioneToken,
				securityToken,
				transazioneDTO, 
				tracciaRichiesta, richiestaIngresso, richiestaUscita, rispostaIngresso, rispostaUscita);
		
	}
	
	// *** TOPIC 'request' ***
	private static final String RETRIEVED_TOKEN_INFO = "\"AAAAATOKEN\"|\"JWT\"|\"UUIDXX\"|\"rfc7523_x509\"|\""+clientAssertionBase64+"\"|\"rfc7523_x509\"|\"clientIdXX\"|\"clientBEARERTOKEN\"|\"USERNAME\"|\"http://retrieveToken\"";
	private static final String CERT_CLIENT_INFO = "\"CN=govway_test, OU=govway_test_ou, O=govway_test_o, L=govway_test_l, ST=Italy, C=IT, EMAILADDRESS=info@link.it\"|\"govway_test\"|\"govway_test_ou\"|\"CN=govway_test, OU=govway_test_ou, O=govway_test_o, L=govway_test_l, ST=Italy, C=IT, EMAILADDRESS=info@link.it\"";
	private static final String TOKEN_INFO = "\"ewogICJhbGciOiAiUlMyNTYiLAogICJ0eXAiOiAiSldUIiwKICAia2lkIjogInByb3ZhIiwKICAieDVjIjogWwogICAgIk1JSS4uLi4uZjZ1UXhjbGhVaDBOaXZqNmhJSitDZDNSMS9GdncxejY5RXllT3ROd3FZU2JzdzJnamlkbzhHUGdEVWtxZFVaSThyYnRjbDIyK2x0S2VXbURhUXZOUUZnTlUreUJObTBBPSIKICBdCn0=.ewogICJpYXQiOiAxNjUwMDMyOTAzLAogICJuYmYiOiAxNjUwMDMyOTAzLAogICJleHAiOiAxNjUwMDMzMDAzLAogICJqdGkiOiAiNGU2MWRjMTQtYmNjOC0xMWVjLTllOTktMDA1MDU2YWUwMzA3IiwKICAiYXVkIjogInRlc3QiLAogICJjbGllbnRfaWQiOiAiY1Rlc3QiLAogICJpc3MiOiAiaXNzVGVzdCIsCiAgInN1YiI6ICJzdWJUZXN0Igp9.PDdXpT5htzB6JI0TdYsfsIBjH8tSV0IkIiKAI0S1IYkqcS6pOs84MsfVk3wnd1_dSiR-2KSpGzZU9s8TuGoXcdR-4oa6EN0RNJJsF8zC1KHVx1IBl4jcZGRY5vAgtKwBC87bPz7EaYXtesS3Go-fl5HTFWvZ4OR3yxvsrCfTy_ehQwVJwJy9yKrIpQFq_dSQr_xQbRBL495D9Fp4p54vNdP3IRtoDq16NUhwkH_dbQJGUJdYZ2M31bBZUvgu9RRZz_ftjI78Swwq5FIwIG7r5trwgmVebZtdLF2Ni5Vc2rL7ZNuBpH7Y_knRgRYbH4HxnMoHOU6nU8yM_ZPZyhHneA\"|\"ewogICJhbGciOiAiUlMyNTYiLAogICJ0eXAiOiAiSldUIiwKICAia2lkIjogInByb3ZhIiwKICAieDVjIjogWwogICAgIk1JSS4uLi4uZjZ1UXhjbGhVaDBOaXZqNmhJSitDZDNSMS9GdncxejY5RXllT3ROd3FZU2JzdzJnamlkbzhHUGdEVWtxZFVaSThyYnRjbDIyK2x0S2VXbURhUXZOUUZnTlUreUJObTBBPSIKICBdCn0=\"|\"ewogICJhbGciOiAiUlMyNTYiLAogICJ0eXAiOiAiSldUIiwKICAia2lkIjogInByb3ZhIiwKICAieDVjIjogWwogICAgIk1JSS4uLi4uZjZ1UXhjbGhVaDBOaXZqNmhJSitDZDNSMS9GdncxejY5RXllT3ROd3FZU2JzdzJnamlkbzhHUGdEVWtxZFVaSThyYnRjbDIyK2x0S2VXbURhUXZOUUZnTlUreUJObTBBPSIKICBdCn0=\"|\"RS256\"|\"kid=prova,x5c=MII.....f6uQxclhUh0Nivj6hIJ+Cd3R1/Fvw1z69EyeOtNwqYSbsw2gjido8GPgDUkqdUZI8rbtcl22+ltKeWmDaQvNQFgNU+yBNm0A=,typ=JWT,alg=RS256\"|\"ewogICJpYXQiOiAxNjUwMDMyOTAzLAogICJuYmYiOiAxNjUwMDMyOTAzLAogICJleHAiOiAxNjUwMDMzMDAzLAogICJqdGkiOiAiNGU2MWRjMTQtYmNjOC0xMWVjLTllOTktMDA1MDU2YWUwMzA3IiwKICAiYXVkIjogInRlc3QiLAogICJjbGllbnRfaWQiOiAiY1Rlc3QiLAogICJpc3MiOiAiaXNzVGVzdCIsCiAgInN1YiI6ICJzdWJUZXN0Igp9\"|\"ewogICJpYXQiOiAxNjUwMDMyOTAzLAogICJuYmYiOiAxNjUwMDMyOTAzLAogICJleHAiOiAxNjUwMDMzMDAzLAogICJqdGkiOiAiNGU2MWRjMTQtYmNjOC0xMWVjLTllOTktMDA1MDU2YWUwMzA3IiwKICAiYXVkIjogInRlc3QiLAogICJjbGllbnRfaWQiOiAiY1Rlc3QiLAogICJpc3MiOiAiaXNzVGVzdCIsCiAgInN1YiI6ICJzdWJUZXN0Igp9\"|\"test\"|\"aud=test,sub=subTest,nbf=1650032903,iss=issTest,exp=1650033003,iat=1650032903,jti=4e61dc14-bcc8-11ec-9e99-005056ae0307,client_id=cTest\"";
	private static final String TLS_CLIENT_INFO = CERT_CLIENT_INFO;
	private static final String ACCESS_TOKEN_JWT = TOKEN_INFO+"|"+CERT_CLIENT_INFO;
	private static final String MODI_AUTHORIZATION = TOKEN_INFO+"|"+CERT_CLIENT_INFO;
	private static final String MODI_INTEGRITY = TOKEN_INFO+"|"+CERT_CLIENT_INFO;
	private static final String MODI_AUDIT = TOKEN_INFO+"|"+CERT_CLIENT_INFO;
	private static final String PDND_CLIENT = "\""+PDND_JSON_CLIENT.replace("\"", "\\\"")+"\"|\""+PDND_CLIENT_ID+"\"|\""+PDND_CLIENT_CONSUMER_ID+"\"";
	private static final String PDND_CLIENT_EMPTY = "\"\"|\"\"|\"\"";
	private static final String PDND_ORGANIZATION = "\""+PDND_JSON_ORGANIZATION.replace("\"", "\\\"")+"\"|\""+PDND_ORGANIZATION_NAME+"\"|\""+PDND_ORGANIZATION_ID+"\"|\""+PDND_ORGANIZATION_CATEGORY+"\"|\""+PDND_ORGANIZATION_EXTERNAL_ORIGIN+"\"|\""+PDND_ORGANIZATIONEXTERNAL_ID+"\"";
	private static final String PDND_ORGANIZATION_EMPTY = "\"\"|\"\"|\"\"|\"\"|\"\"|\"\"";
		
	private static final String DATE = "\"2020-06-25 13:09:05:825\"|\"2020-06-25 13:09:05:875\"|\"2020-06-25 13:09:05:925\"|\"2020-06-25 13:09:06:025\"|\"2020-06-25 13:09:07:025\"|\"2020-06-25 13:09:07:125\"|\"2020-06-25 13:09:07:225\"|\"2020-06-25 13:09:07:490\"";
	private static final String LOG_REQUEST_PA_PUT = "\"in\"|\"rest\"|\"erogazione\"|\"esempioCostanteRichiestaErogazione\"|\"UUIDXX\"|\"XX-deXXXRR-deXXXRest\"|\"p1\"|\"p2\"|\""+ENV_VALUE_1+"\"|\""+ENV_VALUE_2+"\"|\"2020-06-25 13:09:05:825\"|\"+0200\"|\"127.0.0.1\"|\"10.113.13.122\"|\"10.113.13.122\"|\"HTTP/1.1\"|\"PUT\"|\"C=IT, O=Prova\"|\"C=IT, O=Prova\"|\"issuerGoogle\"|\"subjectAD5432h43242\"|\"3456ClientId\"|\"\"|\"info@link.it\"|\"issTest\"|\"1650033003\"|\"\"|\"\"|\"AppXde23\"|\"EnteFruitore\"|\""+PDND_ORGANIZATION_NAME+"\"|\"https://prova:8443/govway/in/EnteEsempio/AAASOAPS1/v1/a1?dklejde=ded&adds=deded\"|\"text/xml; charset=\\\"UTF8\\\"\"|\"51\"|\"HEADERS\"|\"Content-XXX=ADEDE\"|\"TipoMessaggio=RICHIESTA_INGRESSO_DUMP_BINARIO\"|\"Content-Type=text/xml; charset=\\\"UTF8\\\"; tipo=inRequest\"|\"v1\"|\"v2a,v2b\"|"+RETRIEVED_TOKEN_INFO+"|"+TLS_CLIENT_INFO+"|"+ACCESS_TOKEN_JWT+"|"+MODI_AUTHORIZATION+"|"+MODI_INTEGRITY+"|"+MODI_AUDIT+"|"+PDND_CLIENT+"|"+PDND_ORGANIZATION+"|"+DATE;
	private static final String LOG_REQUEST_PA_GET = "\"in\"|\"rest\"|\"erogazione\"|\"esempioCostanteRichiestaErogazione\"|\"UUIDXX\"|\"XX-deXXXRR-deXXXRest\"|\"p1\"|\"p2\"|\""+ENV_VALUE_1+"\"|\""+ENV_VALUE_2+"\"|\"2020-06-25 13:09:05:825\"|\"+0200\"|\"127.0.0.1\"|\"10.113.13.122\"|\"10.113.13.122\"|\"HTTP/1.1\"|\"GET\"|\"C=IT, O=Prova\"|\"C=IT, O=Prova\"|\"issuerGoogle\"|\"subjectAD5432h43242\"|\"3456ClientId\"|\"\"|\"info@link.it\"|\"issTest\"|\"1650033003\"|\"\"|\"\"|\"AppXde23\"|\"EnteFruitore\"|\""+SERVIZIO_APPLICATIVO_FRUITORE+"\"|\"https://prova:8443/govway/in/EnteEsempio/AAASOAPS1/v1/a1?dklejde=ded&adds=deded\"|\"\"|\"0\"|\"HEADERS\"|\"Content-XXX=ADEDE\"|\"TipoMessaggio=RICHIESTA_INGRESSO_DUMP_BINARIO\"|\"v1\"|\"v2a,v2b\"|"+RETRIEVED_TOKEN_INFO+"|"+TLS_CLIENT_INFO+"|"+ACCESS_TOKEN_JWT+"|"+MODI_AUTHORIZATION+"|"+MODI_INTEGRITY+"|"+MODI_AUDIT+"|"+PDND_CLIENT_EMPTY+"|"+PDND_ORGANIZATION_EMPTY+"|"+DATE;
	private static final String LOG_REQUEST_PD_PUT = "\"out\"|\"soap\"|\"fruizione\"|\"esempioCostanteRichiestaFruizione\"|\"UUIDXX\"|\"XX-deXXXRR-deXXXRest\"|\"p1\"|\"p2\"|\""+ENV_VALUE_1+"\"|\""+ENV_VALUE_2+"\"|\"2020-06-25 13:09:05:925\"|\"+0200\"|\"127.0.0.1\"|\"10.113.13.122\"|\"10.113.13.122\"|\"HTTP/1.1\"|\"PUT\"|\"C=IT, O=Prova\"|\"C=IT, O=Prova\"|\"issuerGoogle\"|\"subjectAD5432h43242\"|\"3456ClientId\"|\"rossi\"|\"info@link.it\"|\"issTest\"|\"1650033003\"|\"SAToken\"|\"SoggettoProprietarioSAToken\"|\"AppXde23\"|\"EnteFruitore\"|\"rossi\"|\"http://127.0.0.1:8080/govwayAPIConfig/api/PetStorec0c4269e2ebe413dadb79071/1?dad=ddede&adadad=dede\"|\"text/xml; charset=\\\"UTF8\\\"\"|\"49\"|\"HEADERS\"|\"Content-XXX=ADEDE\"|\"TipoMessaggio=RICHIESTA_USCITA_DUMP_BINARIO\"|\"Content-Type=text/xml; charset=\\\"UTF8\\\"; tipo=outRequest\"|\"v1\"|\"v2a,v2b,v2c\"|"+RETRIEVED_TOKEN_INFO+"|"+TLS_CLIENT_INFO+"|"+ACCESS_TOKEN_JWT+"|"+MODI_AUTHORIZATION+"|"+MODI_INTEGRITY+"|"+MODI_AUDIT+"|"+PDND_CLIENT+"|"+PDND_ORGANIZATION+"|"+DATE;
	private static final String LOG_REQUEST_PD_GET = "\"out\"|\"soap\"|\"fruizione\"|\"esempioCostanteRichiestaFruizione\"|\"UUIDXX\"|\"XX-deXXXRR-deXXXRest\"|\"p1\"|\"p2\"|\""+ENV_VALUE_1+"\"|\""+ENV_VALUE_2+"\"|\"2020-06-25 13:09:05:925\"|\"+0200\"|\"127.0.0.1\"|\"10.113.13.122\"|\"10.113.13.122\"|\"HTTP/1.1\"|\"GET\"|\"C=IT, O=Prova\"|\"C=IT, O=Prova\"|\"issuerGoogle\"|\"subjectAD5432h43242\"|\"3456ClientId\"|\"\"|\"info@link.it\"|\"issTest\"|\"1650033003\"|\"SAToken\"|\"SoggettoProprietarioSAToken\"|\"AppXde23\"|\"EnteFruitore\"|\"SAToken\"|\"http://127.0.0.1:8080/govwayAPIConfig/api/PetStorec0c4269e2ebe413dadb79071/1?dad=ddede&adadad=dede\"|\"\"|\"0\"|\"HEADERS\"|\"Content-XXX=ADEDE\"|\"TipoMessaggio=RICHIESTA_USCITA_DUMP_BINARIO\"|\"v1\"|\"v2a,v2b,v2c\"|"+RETRIEVED_TOKEN_INFO+"|"+TLS_CLIENT_INFO+"|"+ACCESS_TOKEN_JWT+"|"+MODI_AUTHORIZATION+"|"+MODI_INTEGRITY+"|"+MODI_AUDIT+"|"+PDND_CLIENT+"|"+PDND_ORGANIZATION+"|"+DATE;
	
	// *** TOPIC 'requestBody' ***
	private static final String LOG_REQUEST_BODY_PA = "UUIDXX.XX-deXXXRR-deXXXRest.MjAyMC0wNi0yNVQxNTowOTowNS44MjUrMDIwMA==.dGV4dC94bWw7IGNoYXJzZXQ9IlVURjgi.PHByb3ZhPlRFU1QgUklDSElFU1RBX0lOR1JFU1NPX0RVTVBfQklOQVJJTzwvcHJvdmE+";
	private static final String LOG_REQUEST_BODY_PD = "UUIDXX.XX-deXXXRR-deXXXRest.MjAyMC0wNi0yNVQxNTowOTowNS45MjUrMDIwMA==.dGV4dC94bWw7IGNoYXJzZXQ9IlVURjgi.PHByb3ZhPlRFU1QgUklDSElFU1RBX1VTQ0lUQV9EVU1QX0JJTkFSSU88L3Byb3ZhPg==";
	
	// *** TOPIC 'response' ***
	private static final String LOG_RESPONSE_PA = "\"esempioCostanteRispostaErogazione\"|\"ADEDADEAD.DEADADEADAD.dEADEADADEA\"|\"UUIDXX\"|\"XX-deXXXRR-deXXXRest\"|\"p1\"|\"p2\"|\""+ENV_VALUE_1+"\"|\""+ENV_VALUE_2+"\"|\"2020-06-25 13:09:07:490\"|\"+0200\"|\"127.0.0.1\"|\"10.113.13.122\"|\"10.113.13.122\"|\"HTTP/1.1\"|\"PUT\"|\"https://prova:8443/govway/in/EnteEsempio/AAASOAPS1/v1/a1?dklejde=ded&adds=deded\"|\"204\"|\"1665000\"|\"text/xml; charset=\\\"UTF8\\\"\"|\"48\"|\"HEADERS\"|\"Content-XXX=ADEDE\"|\"TipoMessaggio=RISPOSTA_USCITA_DUMP_BINARIO\"|\"Content-Type=text/xml; charset=\\\"UTF8\\\"; tipo=outResponse\"|\"X-GovWay-APP-SERVER=10.114.32.21\"|\"X-GovWay-HOSTNAME-APP-SERVER=prova\"|\"X-GovWay-SERVER-ENCODING=UTF-8\"|\"X-GovWay-APP-SERVER-PORT=8443\"|\"X-GovWay-USER=Andrea\"|\"X-GovWay-COMPLEX=versione_api = 1; api = APIEsempio; operazione = azioneDiProva; erogatore = EnteErogatore; soggetto_fruitore = EnteFruitore; applicativo_fruitore = AppXde23; id_messaggio_richiesta = idMsgReqXXX; id_messaggio_risposta = ; id_collaborazione = ; esito = OK;\"";
	private static final String LOG_RESPONSE_PD = "\"esempioCostanteRispostaFruizione\"|\"ADEDADEAD.DEADADEADAD.dEADEADADEA\"|\"UUIDXX\"|\"XX-deXXXRR-deXXXRest\"|\"p1\"|\"p2\"|\""+ENV_VALUE_1+"\"|\""+ENV_VALUE_2+"\"|\"2020-06-25 13:09:07:025\"|\"+0200\"|\"127.0.0.1\"|\"10.113.13.122\"|\"10.113.13.122\"|\"HTTP/1.1\"|\"PUT\"|\"http://127.0.0.1:8080/govwayAPIConfig/api/PetStorec0c4269e2ebe413dadb79071/1?dad=ddede&adadad=dede\"|\"202\"|\"1100000\"|\"text/xml; charset=\\\"UTF8\\\"\"|\"50\"|\"HEADERS\"|\"Content-XXX=ADEDE\"|\"TipoMessaggio=RISPOSTA_INGRESSO_DUMP_BINARIO\"|\"Content-Type=text/xml; charset=\\\"UTF8\\\"; tipo=inResponse\"|\"X-GovWay-APP-SERVER=10.114.32.21\"|\"X-GovWay-HOSTNAME-APP-SERVER=prova\"|\"X-GovWay-SERVER-ENCODING=UTF-8\"|\"X-GovWay-APP-SERVER-PORT=8443\"|\"X-GovWay-USER=Andrea\"|\"X-GovWay-COMPLEX=versione_api = 1; api = APIEsempio; operazione = azioneDiProva; erogatore = EnteErogatore; soggetto_fruitore = EnteFruitore; applicativo_fruitore = AppXde23; id_messaggio_richiesta = idMsgReqXXX; id_messaggio_risposta = ; id_collaborazione = ; esito = OK;\"";
	
	// *** TOPIC 'responseBody' ***
	private static final String LOG_RESPONSE_BODY_PA = "UUIDXX.XX-deXXXRR-deXXXRest.MjAyMC0wNi0yNVQxNTowOTowNy40OTArMDIwMA==.SFRUUC8xLjEgMjA0IE5vIENvbnRlbnQKQ29udGVudC1YWFg6IEFERURFClRpcG9NZXNzYWdnaW86IFJJU1BPU1RBX1VTQ0lUQV9EVU1QX0JJTkFSSU8KQ29udGVudC1UeXBlOiB0ZXh0L3htbDsgY2hhcnNldD1cIlVURjhcIjsgdGlwbz1vdXRSZXNwb25zZQpYLUdvdldheS1BUFAtU0VSVkVSOiAxMC4xMTQuMzIuMjEKWC1Hb3ZXYXktSE9TVE5BTUUtQVBQLVNFUlZFUjogcHJvdmEKWC1Hb3ZXYXktU0VSVkVSLUVOQ09ESU5HOiBVVEYtOApYLUdvdldheS1BUFAtU0VSVkVSLVBPUlQ6IDg0NDMKWC1Hb3ZXYXktVVNFUjogQW5kcmVhClgtR292V2F5LUNPTVBMRVg6IHZlcnNpb25lX2FwaSA9IDE7IGFwaSA9IEFQSUVzZW1waW87IG9wZXJhemlvbmUgPSBhemlvbmVEaVByb3ZhOyBlcm9nYXRvcmUgPSBFbnRlRXJvZ2F0b3JlOyBzb2dnZXR0b19mcnVpdG9yZSA9IEVudGVGcnVpdG9yZTsgYXBwbGljYXRpdm9fZnJ1aXRvcmUgPSBBcHBYZGUyMzsgaWRfbWVzc2FnZ2lvX3JpY2hpZXN0YSA9IGlkTXNnUmVxWFhYOyBpZF9tZXNzYWdnaW9fcmlzcG9zdGEgPSA7IGlkX2NvbGxhYm9yYXppb25lID0gOyBlc2l0byA9IE9LOw==.PHByb3ZhPlRFU1QgUklTUE9TVEFfVVNDSVRBX0RVTVBfQklOQVJJTzwvcHJvdmE+";
	private static final String LOG_RESPONSE_BODY_PD = "UUIDXX.XX-deXXXRR-deXXXRest.MjAyMC0wNi0yNVQxNTowOTowNy4wMjUrMDIwMA==.SFRUUC8xLjEgMjAyIEFjY2VwdGVkCkNvbnRlbnQtWFhYOiBBREVERQpUaXBvTWVzc2FnZ2lvOiBSSVNQT1NUQV9JTkdSRVNTT19EVU1QX0JJTkFSSU8KQ29udGVudC1UeXBlOiB0ZXh0L3htbDsgY2hhcnNldD1cIlVURjhcIjsgdGlwbz1pblJlc3BvbnNlClgtR292V2F5LUFQUC1TRVJWRVI6IDEwLjExNC4zMi4yMQpYLUdvdldheS1IT1NUTkFNRS1BUFAtU0VSVkVSOiBwcm92YQpYLUdvdldheS1TRVJWRVItRU5DT0RJTkc6IFVURi04ClgtR292V2F5LUFQUC1TRVJWRVItUE9SVDogODQ0MwpYLUdvdldheS1VU0VSOiBBbmRyZWEKWC1Hb3ZXYXktQ09NUExFWDogdmVyc2lvbmVfYXBpID0gMTsgYXBpID0gQVBJRXNlbXBpbzsgb3BlcmF6aW9uZSA9IGF6aW9uZURpUHJvdmE7IGVyb2dhdG9yZSA9IEVudGVFcm9nYXRvcmU7IHNvZ2dldHRvX2ZydWl0b3JlID0gRW50ZUZydWl0b3JlOyBhcHBsaWNhdGl2b19mcnVpdG9yZSA9IEFwcFhkZTIzOyBpZF9tZXNzYWdnaW9fcmljaGllc3RhID0gaWRNc2dSZXFYWFg7IGlkX21lc3NhZ2dpb19yaXNwb3N0YSA9IDsgaWRfY29sbGFib3JhemlvbmUgPSA7IGVzaXRvID0gT0s7.PHByb3ZhPlRFU1QgUklTUE9TVEFfSU5HUkVTU09fRFVNUF9CSU5BUklPPC9wcm92YT4=";

	
	private static void test(TipoPdD tipoPdD, boolean log4j, boolean requestWithPayload,
			FaseTracciamento faseTracciamento,
			Logger log, FileTraceConfig config, 
			CredenzialiMittente credenzialiMittente, 
			InformazioniToken informazioniToken,
			InformazioniAttributi informazioniAttributi,
			InformazioniNegoziazioneToken informazioniNegoziazioneToken,
			SecurityToken securityToken,
			Transazione transazioneDTO, Traccia tracciaRichiesta, 
			Messaggio richiestaIngresso, Messaggio richiestaUscita,
			Messaggio rispostaIngresso, Messaggio rispostaUscita) throws Exception {
		
		boolean onlyLogFileTraceHeaders = TipoPdD.APPLICATIVA.equals(tipoPdD) || (TipoPdD.DELEGATA.equals(tipoPdD) && transazioneDTO.getEsito()==16);
		boolean onlyLogFileTraceBody = TipoPdD.APPLICATIVA.equals(tipoPdD) && transazioneDTO.getEsito()!=16;
		
		System.out.println("\n\n ---------------------------- ("+tipoPdD+") (esito:"+transazioneDTO.getEsito()+") (httpMethod:"+transazioneDTO.getTipoRichiesta()+") (onlyLogFileTrace headers:"+onlyLogFileTraceHeaders+" body:"+onlyLogFileTraceBody+") -----------------------------");
		
		boolean erogazioni = TipoPdD.APPLICATIVA.equals(tipoPdD);
		Transaction transaction = new Transaction("UUIDXX", "FileTraceTest", false);
		transaction.setCredenzialiMittente(credenzialiMittente);
		transaction.setTracciaRichiesta(tracciaRichiesta);
		transaction.addMessaggio(richiestaIngresso, onlyLogFileTraceHeaders, onlyLogFileTraceBody);
		transaction.addMessaggio(richiestaUscita, onlyLogFileTraceHeaders, onlyLogFileTraceBody);
		transaction.addMessaggio(rispostaIngresso, onlyLogFileTraceHeaders, onlyLogFileTraceBody);
		transaction.addMessaggio(rispostaUscita, onlyLogFileTraceHeaders, onlyLogFileTraceBody);
		
		System.out.println("Messaggi presenti prima: "+transaction.sizeMessaggi());
		
		ConfigurazionePdD confPdD = new ConfigurazionePdD();
		confPdD.setLoader(new Loader());
		confPdD.setLog(log);
		ProtocolFactoryManager.initialize(log, confPdD, CostantiLabel.TRASPARENTE_PROTOCOL_NAME);
		
		boolean requestSent = true;
		if(transazioneDTO.getEsito()!=0) {
			requestSent = false;
		}
		
		RequestInfo requestInfo = new RequestInfo();
		
		PdDContext context = new PdDContext();
		if(requestSent) {
			context.addObject(Costanti.RICHIESTA_INOLTRATA_BACKEND, Costanti.RICHIESTA_INOLTRATA_BACKEND_VALORE);
		}
		
		context.addObject(org.openspcoop2.utils.Map.newMapKey(TRACKING_PHASE), faseTracciamento.name());
		
		FileTraceManager manager = new FileTraceManager(log, config);
		IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getDefaultProtocolFactory();
		manager.buildTransazioneInfo(protocolFactory, transazioneDTO, transaction,
				informazioniToken,
				informazioniAttributi,
				informazioniNegoziazioneToken,
				securityToken,
				context,
				null,
				faseTracciamento);
		
		Busta busta = TransazioneUtilities.convertToBusta(transazioneDTO);
						
		if(log4j) {
			
			/** OUTPUT PRIMA INVOCAZIONE: 
			 *    [testng] Attuale situazione log
   [testng] 	request:11.96kb
   [testng] 	requestBody:171b
   [testng] 	response:911b
   [testng] 	responseBody:903b
			 * */
			
			System.out.println("Iterazione master ...");
			manager.invoke(tipoPdD, context, requestInfo, busta, faseTracciamento);	
			Utilities.sleep(500);
			System.out.println("Iterazione master ok");
			
			boolean compressExpected = true;
			
			Date now = DateManager.getDate();
			String formatDir = "yyyy-MM";
			String formatFile = "MM-dd-yyyy";
			String dirName = DateUtils.getSimpleDateFormat(formatDir).format(now)+"-"+ENV_VALUE_2;
			String dirFile = DateUtils.getSimpleDateFormat(formatFile).format(now);
			
			System.out.println("Verifica log prodotti ...");
			verificaFile(dirName, dirFile, 
					erogazioni, requestWithPayload, !compressExpected,
					transazioneDTO,
					true); // tutte FaseTracciamento.POST_OUT_RESPONSE.equals(faseTracciamento));
			verificaFileTrackingPhase(dirName,dirFile,
					erogazioni, faseTracciamento, !compressExpected);
			verificaFileEncrypted(dirName,dirFile, 
					requestWithPayload, !compressExpected,
					transazioneDTO);
			System.out.println("Verifica log prodotti completata");
			
			int numeroInvocazioni = 2;
			for (int i = 0; i < numeroInvocazioni; i++) {
				System.out.println("Iterazione "+(i+1)+"/"+numeroInvocazioni+" ...");
				manager.invoke(tipoPdD, context, requestInfo, busta, faseTracciamento);	
				Utilities.sleep(500);
				System.out.println("Iterazione "+(i+1)+"/"+numeroInvocazioni+" ok");
			}
			
			System.out.println("Verifica log compressi prodotti ...");
			
			verificaFile(dirName, dirFile,
					erogazioni, requestWithPayload, compressExpected, // tutte (compressExpected && FaseTracciamento.POST_OUT_RESPONSE.equals(faseTracciamento)),
					transazioneDTO,
					true); // tutte FaseTracciamento.POST_OUT_RESPONSE.equals(faseTracciamento));
			verificaFileTrackingPhase(dirName,dirFile,
					erogazioni, faseTracciamento, compressExpected);
			verificaFileEncrypted(dirName,dirFile, 
					requestWithPayload, compressExpected,
					transazioneDTO);
			
			System.out.println("Verifica log compressi prodotti completata");
		}
		else {
		
			Map<String, String> outputMap = new HashMap<>();
			manager.invoke(tipoPdD, context, requestInfo, busta, faseTracciamento, outputMap);
			
			int res = outputMap.size();
			System.out.println("Terminato con "+res+" risultati");
			int risultatiAttesi = 4;
			//if(!FaseTracciamento.POST_OUT_RESPONSE.equals(faseTracciamento)) {
			//	risultatiAttesi = 0;
			//}
			//	else 
			if(!erogazioni && !requestSent) {
				risultatiAttesi = 0;
			}
			else if(!requestWithPayload) {
				risultatiAttesi = 3;
			}
			
			// aggiungo topic tracking phase
			risultatiAttesi++;
			
			// aggiungo topic encrypted
			risultatiAttesi = risultatiAttesi + ENCRYPT_TEST.size();
			
			if(res!=risultatiAttesi) {
				throw new UtilsException("Attesi "+risultatiAttesi+" risultati, trovati '"+res+"'");
			}
			
			if(outputMap.size()>0) {
				List<String> l = new ArrayList<>();
				for (String topic : outputMap.keySet()) {
					l.add(topic);
				}
				Collections.sort(l);
				for (String topic : l) {
					System.out.println("\n\n *** TOPIC '"+topic+"' ***");
					String logMsg = outputMap.get(topic);
					System.out.println(logMsg);
					
					if(erogazioni) {
						if("request".equals(topic)) {
							if(requestWithPayload) {
								if(!logMsg.equals(LOG_REQUEST_PA_PUT)) {
									throw new UtilsException(MSG_FAILED_PREFIX+LOG_REQUEST_PA_PUT+"\nTrovato:\n"+logMsg);
								}
							}
							else {
								if(!logMsg.equals(LOG_REQUEST_PA_GET)) {
									throw new UtilsException(MSG_FAILED_PREFIX+LOG_REQUEST_PA_GET+"\nTrovato:\n"+logMsg);
								}
							}
						}
						else if("requestBody".equals(topic)) {
							if(!logMsg.equals(LOG_REQUEST_BODY_PA)) {
								throw new UtilsException(MSG_FAILED_PREFIX+LOG_REQUEST_BODY_PA+"\nTrovato:\n"+logMsg);
							}
						}
						else if("response".equals(topic)) {
							String atteso = LOG_RESPONSE_PA;
							if(transazioneDTO.getEsito()!=0) {
								atteso = atteso.replace("esito = OK", "esito = ERRORE_AUTENTICAZIONE");
							}
							if(!requestWithPayload) {
								atteso = atteso.replace("PUT", "GET");
							}
							if(!logMsg.equals(atteso)) {
								throw new UtilsException(MSG_FAILED_PREFIX+atteso+"\nTrovato:\n"+logMsg);
							}
						}
						else if("responseBody".equals(topic)) {
							String atteso = LOG_RESPONSE_BODY_PA;
							if(transazioneDTO.getEsito()!=0) {
								atteso = atteso.replace("9yYXppb25lID0gOyBlc2l0byA9IE9LOw==", "9yYXppb25lID0gOyBlc2l0byA9IEVSUk9SRV9BVVRFTlRJQ0FaSU9ORTs=");
							}
							if(!logMsg.equals(atteso)) {
								throw new UtilsException(MSG_FAILED_PREFIX+atteso+"\nTrovato:\n"+logMsg);
							}
						}
						else if("trackingPhaseInRequest".equals(topic) 
								||
								"trackingPhaseOutRequest".equals(topic)
								||
								"trackingPhaseOutResponse".equals(topic) 
								||
								"trackingPhasePostOutResponse".equals(topic)){
							String atteso = getExpectedTrackingPhaseContent(erogazioni, faseTracciamento);
							if(!logMsg.equals(atteso)) {
								throw new UtilsException(MSG_FAILED_PREFIX+atteso+"\nTrovato:\n"+logMsg);
							}
						}
						else if(ENCRYPT_TEST.contains(topic)){
							verificaExpectedEncryptContent(topic, requestWithPayload, logMsg);
						}
						else {
							throw new UtilsException("FAILED!! topic sconosciuto");
						}
					}
					else {
						if("request".equals(topic)) {
							if(requestWithPayload) {
								if(!logMsg.equals(LOG_REQUEST_PD_PUT)) {
									throw new UtilsException(MSG_FAILED_PREFIX+LOG_REQUEST_PD_PUT+"\nTrovato:\n"+logMsg);
								}
							}
							else {
								if(!logMsg.equals(LOG_REQUEST_PD_GET)) {
									throw new UtilsException(MSG_FAILED_PREFIX+LOG_REQUEST_PD_GET+"\nTrovato:\n"+logMsg);
								}
							}
						}
						else if("requestBody".equals(topic)) {
							if(!logMsg.equals(LOG_REQUEST_BODY_PD)) {
								throw new UtilsException(MSG_FAILED_PREFIX+LOG_REQUEST_BODY_PD+"\nTrovato:\n"+logMsg);
							}
						}
						else if("response".equals(topic)) {
							String atteso = LOG_RESPONSE_PD;
							if(!requestWithPayload) {
								atteso = atteso.replace("PUT", "GET");
							}
							if(!logMsg.equals(atteso)) {
								throw new UtilsException(MSG_FAILED_PREFIX+atteso+"\nTrovato:\n"+logMsg);
							}
						}
						else if("responseBody".equals(topic)) {
							if(!logMsg.equals(LOG_RESPONSE_BODY_PD)) {
								throw new UtilsException(MSG_FAILED_PREFIX+LOG_RESPONSE_BODY_PD+"\nTrovato:\n"+logMsg);
							}
						}
						else if("trackingPhaseRequest".equals(topic) 
								||
								"trackingPhaseResponse".equals(topic)){
							String atteso = getExpectedTrackingPhaseContent(erogazioni, faseTracciamento);
							if(!logMsg.equals(atteso)) {
								throw new UtilsException(MSG_FAILED_PREFIX+atteso+"\nTrovato:\n"+logMsg);
							}
						}
						else if(ENCRYPT_TEST.contains(topic)){
							verificaExpectedEncryptContent(topic, requestWithPayload, logMsg);
						}
						else {
							throw new UtilsException("FAILED!! topic sconosciuto");
						}
					}
				}
			}
		}
		
		manager.cleanResourcesForOnlyFileTrace(transaction);
		
		int sizeAfter = transaction.sizeMessaggi();
		System.out.println("Messaggi presenti dopo: "+sizeAfter);
		if(onlyLogFileTraceHeaders && onlyLogFileTraceBody) {
			if(sizeAfter!=0) {
				throw new UtilsException("Attesi 0 messaggi");
			}
		}
		else if(onlyLogFileTraceHeaders || onlyLogFileTraceBody) {
			if(sizeAfter!=4) {
				throw new UtilsException("Attesi 4 messaggi");
			}
			if(onlyLogFileTraceHeaders) {
				for (Messaggio msg : transaction.getMessaggi()) {
					if(msg.getHeaders()!=null && !msg.getHeaders().isEmpty()) {
						throw new UtilsException("Heaeders non attesi ("+msg.getTipoMessaggio()+")");
					}
				}
			}	
			else {
				for (Messaggio msg : transaction.getMessaggi()) {
					if(msg.getBody()!=null) {
						throw new UtilsException("Body non atteso ("+msg.getTipoMessaggio()+")");
					}
					if(msg.getContentType()!=null) {
						throw new UtilsException("ContentType non atteso ("+msg.getTipoMessaggio()+")");
					}
				}
			}
		}
		else {
			if(sizeAfter!=4) {
				throw new UtilsException("Attesi 4 messaggi");
			}
		}
	}
	
	private static String getContentFile(String file, boolean compress, boolean expected) throws UtilsException, FileNotFoundException {
		File f = new File(file);
		if(!f.exists()) {
			if(expected) {
				throw new UtilsException("File atteso ["+f.getAbsolutePath()+"] non trovato");
			}
			else {
				return null;
			}
		}
		if(!f.canRead()) {
			throw new UtilsException("File atteso ["+f.getAbsolutePath()+"] non leggibile");
		}
		byte [] c = FileSystemUtilities.readBytesFromFile(f);
		if(compress) {
			byte [] cDecompressed = CompressorUtilities.decompress(c, CompressorType.GZIP);
			return new String(cDecompressed);
		}
		else {
			return new String(c);
		}
	}
	
	private static void verificaFile(String dirName,String dirFile,
			boolean erogazioni, boolean requestWithPayload, boolean compressExpected,
			Transazione transazioneDTO,
			boolean fileExpected) throws FileNotFoundException, UtilsException {
		
		boolean requestSent = true;
		if(transazioneDTO.getEsito()!=0) {
			requestSent = false;
		}
		if(!erogazioni && !requestSent) {
			compressExpected = false;
		}
		
		String fileSuffix = ".log";
		String fileGzSuffix = "-1.log.gz";
		String fileGzSuffix2 = "-2.log.gz";	
		
		String dirCompress = dirName+"/";
		String dateFileCompress = "-"+dirFile;
		
		
		String fileRequest = DIR_TMP+""+"fileTrace-"+ENV_VALUE_1+".request"+""+fileSuffix;
		String request = getContentFile(fileRequest,false, true);
		String sizeRequest = request!=null ? Utilities.convertBytesToFormatString(request.length()) : null;
		
		String fileRequestCompress = DIR_TMP+dirCompress+"fileTrace-"+ENV_VALUE_1+".request"+dateFileCompress+fileGzSuffix;
		String requestCompress = getContentFile(fileRequestCompress,true,compressExpected);
		
		
		String fileRequestBody = DIR_TMP+""+"fileTrace-"+ENV_VALUE_1+".requestBody"+""+fileSuffix;
		String requestBody = getContentFile(fileRequestBody,false,true);
		String sizeRequestBody = requestBody!=null ? Utilities.convertBytesToFormatString(requestBody.length()) : null;
		
		String fileRequestBodyCompress = DIR_TMP+dirCompress+"fileTrace-"+ENV_VALUE_1+".requestBody"+dateFileCompress+fileGzSuffix;
		String requestBodyCompress = null;
		if(!requestWithPayload) {
			requestBodyCompress = getContentFile(fileRequestBodyCompress,true,false);
		}
		else { 
			requestBodyCompress = getContentFile(fileRequestBodyCompress,true,compressExpected);
		}
		
		
		String fileResponse = DIR_TMP+""+"fileTrace-"+ENV_VALUE_1+".response"+""+fileSuffix;
		String response = getContentFile(fileResponse,false, true);
		String sizeResponse = response!=null ? Utilities.convertBytesToFormatString(response.length()) : null;
		
		String fileResponseCompress = DIR_TMP+dirCompress+"fileTrace-"+ENV_VALUE_1+".response"+dateFileCompress+fileGzSuffix;
		String responseCompress = getContentFile(fileResponseCompress,true,compressExpected);
		
		String fileResponseCompress2 = DIR_TMP+dirCompress+"fileTrace-"+ENV_VALUE_1+".response"+dateFileCompress+fileGzSuffix2;
		String responseCompress2 = getContentFile(fileResponseCompress2,true,
				false); // può avvenire dopo
		
		
		String fileResponseBody = DIR_TMP+""+"fileTrace-"+ENV_VALUE_1+".responseBody"+""+fileSuffix;
		String responseBody = getContentFile(fileResponseBody,false, true);
		String sizeResponseBody = responseBody!=null ? Utilities.convertBytesToFormatString(responseBody.length()) : null;
		
		String fileResponseBodyCompress = DIR_TMP+dirCompress+"fileTrace-"+ENV_VALUE_1+".responseBody"+dateFileCompress+fileGzSuffix;
		String responseBodyCompress = getContentFile(fileResponseBodyCompress,true,compressExpected);
		
		if(!fileExpected) {
			if(request!=null && request.length()>0) {
				throw new UtilsException("File '"+fileRequest+"' not expected");
			}
			if(requestCompress!=null && requestCompress.length()>0) {
				throw new UtilsException("File '"+fileRequestCompress+"' not expected");
			}
			return;
		}
		
		
		if(!compressExpected) {
			System.out.println("Attuale situazione log\n\trequest:"+sizeRequest+"\n\trequestBody:"+sizeRequestBody+"\n\tresponse:"+sizeResponse+"\n\tresponseBody:"+sizeResponseBody+"");
		}
		
		String nonTrovatoInFileMessage="\nNon trovato in file:";
		String nonTrovatoInFileCompressMessage=" e nemmeno in file compresso:";
		
		String decompressed = ".decompressed";
		String fileRequestDecompressed = fileRequest + decompressed;
		String fileRequestBodyDecompressed = fileRequestBody + decompressed;
		String fileResponseDecompressed = fileResponse + decompressed;
		String fileResponseDecompressed2 = fileResponse + decompressed + "2";
		String fileResponseBodyDecompressed = fileResponseBody + decompressed;
		if(requestCompress!=null) {
			FileSystemUtilities.writeFile(fileRequestDecompressed, requestCompress.getBytes());
		}
		if(requestBodyCompress!=null) {
			FileSystemUtilities.writeFile(fileRequestBodyDecompressed, requestBodyCompress.getBytes());
		}
		if(responseCompress!=null) {
			FileSystemUtilities.writeFile(fileResponseDecompressed, responseCompress.getBytes());
		}
		if(responseCompress2!=null) {
			FileSystemUtilities.writeFile(fileResponseDecompressed2, responseCompress2.getBytes());
		}
		if(responseBodyCompress!=null) {
			FileSystemUtilities.writeFile(fileResponseBodyDecompressed, responseBodyCompress.getBytes());
		}
			
		
		if(erogazioni) {
			
			// request
			if(requestWithPayload) {
				if(!request.contains(LOG_REQUEST_PA_PUT) && 
						(requestCompress==null || !requestCompress.contains(LOG_REQUEST_PA_PUT))
				) {
					throw new UtilsException(MSG_FAILED_PREFIX+LOG_REQUEST_PA_PUT+nonTrovatoInFileMessage+fileRequest+nonTrovatoInFileCompressMessage+fileRequestDecompressed);
				}
			}
			else {
				if(!request.contains(LOG_REQUEST_PA_GET) && 
						(requestCompress == null || !requestCompress.contains(LOG_REQUEST_PA_GET)
				)) {
					throw new UtilsException(MSG_FAILED_PREFIX+LOG_REQUEST_PA_GET+nonTrovatoInFileMessage+fileRequest+nonTrovatoInFileCompressMessage+fileRequestDecompressed);
				}
			}

			// requestBody
			if(requestWithPayload) {
				if(!requestBody.contains(LOG_REQUEST_BODY_PA) && 
						(requestBodyCompress == null || !requestBodyCompress.contains(LOG_REQUEST_BODY_PA)
				)) {
					throw new UtilsException(MSG_FAILED_PREFIX+LOG_REQUEST_BODY_PA+nonTrovatoInFileMessage+fileRequestBody+nonTrovatoInFileCompressMessage+fileRequestBodyDecompressed);
				}
			}else if(requestBody.length()>0) {
				throw new UtilsException("Content in '"+fileRequestBody+"' not expected");
			}
	
			// response
			String atteso = LOG_RESPONSE_PA;
			if(transazioneDTO.getEsito()!=0) {
				atteso = atteso.replace("esito = OK", "esito = ERRORE_AUTENTICAZIONE");
			}
			if(!requestWithPayload) {
				atteso = atteso.replace("PUT", "GET");
			}
			if(!response.contains(atteso) &&
					(responseCompress==null || !responseCompress.contains(atteso)) &&
					(responseCompress2==null || !responseCompress2.contains(atteso))) {
				throw new UtilsException(MSG_FAILED_PREFIX+atteso+nonTrovatoInFileMessage+fileResponse+nonTrovatoInFileCompressMessage+fileResponseDecompressed);
			}
			
			// responseBody
			atteso = LOG_RESPONSE_BODY_PA;
			if(transazioneDTO.getEsito()!=0) {
				atteso = atteso.replace("9yYXppb25lID0gOyBlc2l0byA9IE9LOw==", "9yYXppb25lID0gOyBlc2l0byA9IEVSUk9SRV9BVVRFTlRJQ0FaSU9ORTs=");
			}
			if(!responseBody.contains(atteso) &&
					(responseBodyCompress==null || !responseBodyCompress.contains(atteso))) {
				throw new UtilsException(MSG_FAILED_PREFIX+atteso+nonTrovatoInFileMessage+fileResponseBody+nonTrovatoInFileCompressMessage+fileResponseBodyDecompressed);
			}

		}
		else {
			// request
			if(requestSent) {
				if(requestWithPayload) {
					if(!request.contains(LOG_REQUEST_PD_PUT) && 
							(requestCompress==null || !requestCompress.contains(LOG_REQUEST_PD_PUT))
					) {
						throw new UtilsException(MSG_FAILED_PREFIX+LOG_REQUEST_PD_PUT+nonTrovatoInFileMessage+fileRequest+nonTrovatoInFileCompressMessage+fileRequestDecompressed);
					}
				}
				else {
					if(!request.contains(LOG_REQUEST_PD_GET) && 
							(requestCompress==null || !requestCompress.contains(LOG_REQUEST_PD_GET))
					) {
						throw new UtilsException(MSG_FAILED_PREFIX+LOG_REQUEST_PD_GET+nonTrovatoInFileMessage+fileRequest+nonTrovatoInFileCompressMessage+fileRequestDecompressed);
					}
				}
			}
			else if(request.length()>0) {
				throw new UtilsException("Content in '"+fileRequest+"' not expected");
			}
			
			// requestBody
			if(requestSent && requestWithPayload) {
				if(!requestBody.contains(LOG_REQUEST_BODY_PD) && 
						(requestBodyCompress == null || !requestBodyCompress.contains(LOG_REQUEST_BODY_PD)
				)) {
					throw new UtilsException(MSG_FAILED_PREFIX+LOG_REQUEST_BODY_PD+nonTrovatoInFileMessage+fileRequestBody+nonTrovatoInFileCompressMessage+fileRequestBodyDecompressed);
				}
			}else if(requestBody.length()>0) {
				throw new UtilsException("Content in '"+fileRequestBody+"' not expected");
			}
			
			// response
			if(requestSent) {
				String atteso = LOG_RESPONSE_PD;
				if(!requestWithPayload) {
					atteso = atteso.replace("PUT", "GET");
				}
				if(!response.contains(atteso) &&
						(responseCompress==null || !responseCompress.contains(atteso)) &&
						(responseCompress2==null || !responseCompress2.contains(atteso))) {
					throw new UtilsException(MSG_FAILED_PREFIX+atteso+nonTrovatoInFileMessage+fileResponse+nonTrovatoInFileCompressMessage+fileResponseDecompressed);
				}
			}else if(response.length()>0) {
				throw new UtilsException("Content in '"+fileResponse+"' not expected");
			}
			
			// responseBody
			if(requestSent) {
				if(!responseBody.contains(LOG_RESPONSE_BODY_PD) && 
						(responseBodyCompress == null || !responseBodyCompress.contains(LOG_RESPONSE_BODY_PD)
				)) {
					throw new UtilsException(MSG_FAILED_PREFIX+LOG_RESPONSE_BODY_PD+nonTrovatoInFileMessage+fileResponseBody+nonTrovatoInFileCompressMessage+fileResponseBodyDecompressed);
				}
			}else if(responseBody.length()>0) {
				throw new UtilsException("Content in '"+fileResponseBody+"' not expected");
			}

		}
	}
	
	
	private static void verificaFileTrackingPhase(String dirName,String dirFile,
			boolean erogazioni, FaseTracciamento faseTracciamento, boolean compressExpected) throws FileNotFoundException, UtilsException {
		for (FaseTracciamento check : FaseTracciamento.values()) {
			boolean fileExpected = true;
			if(!check.equals(faseTracciamento)) {
				if(erogazioni) {
					fileExpected = false;
				}
				else if(FaseTracciamento.IN_REQUEST.equals(faseTracciamento) || FaseTracciamento.OUT_REQUEST.equals(faseTracciamento)) {
					if(FaseTracciamento.OUT_RESPONSE.equals(check) || FaseTracciamento.POST_OUT_RESPONSE.equals(check)) {
						fileExpected = false;
					}	
				}
				else if(FaseTracciamento.OUT_RESPONSE.equals(faseTracciamento) || FaseTracciamento.POST_OUT_RESPONSE.equals(faseTracciamento)) {
					if(FaseTracciamento.IN_REQUEST.equals(check) || FaseTracciamento.OUT_REQUEST.equals(check)) {
						fileExpected = false;
					}	
				}
			}
			System.out.println("Controllo log tracking phase '"+faseTracciamento+"'; controllo per log '"+check+"', expected: "+fileExpected);
			verificaFileTrackingPhase(dirName,dirFile,
					erogazioni, faseTracciamento, check, fileExpected, compressExpected);
		}
	}
	private static void verificaFileTrackingPhase(String dirName,String dirFile,
			boolean erogazioni, FaseTracciamento faseTracciamentoInCorso, FaseTracciamento faseTracciamentoCheck, boolean fileExpected, boolean compressExpected) throws FileNotFoundException, UtilsException {
		
		String fileSuffix = ".log";
		String fileGzSuffix = "-1.log.gz";
		
		String dirCompress = dirName+"/";
		String dateFileCompress = "-"+dirFile;
		
		String expected = getExpectedTrackingPhaseContent(erogazioni, fileExpected ? faseTracciamentoInCorso : faseTracciamentoCheck);
		
		String topicName = "";
		if(erogazioni) {
			switch (faseTracciamentoInCorso) {
			case IN_REQUEST:
				topicName = "trackingPhaseInRequest";
				break;
			case OUT_REQUEST:
				topicName = "trackingPhaseOutRequest";
				break;
			case OUT_RESPONSE:
				topicName = "trackingPhaseOutResponse";
				break;
			case POST_OUT_RESPONSE:
				topicName = "trackingPhasePostOutResponse";
				break;
			}
		}
		else {
			switch (faseTracciamentoInCorso) {
			case IN_REQUEST:
			case OUT_REQUEST:
				topicName = "trackingPhaseRequest";
				break;
			case OUT_RESPONSE:
			case POST_OUT_RESPONSE:
				topicName = "trackingPhaseResponse";
				break;
			}
		}
		
		System.out.println("Verifico contenuto seguente in topic '"+topicName+"' : ["+expected+"]");
		
		String fileRequest = DIR_TMP+""+"fileTrace-"+ENV_VALUE_1+"."+topicName+""+""+fileSuffix;
		String request = getContentFile(fileRequest,false, fileExpected);
		String sizeRequest = request!=null ? Utilities.convertBytesToFormatString(request.length()) : null;
		
		String fileRequestCompress = DIR_TMP+dirCompress+"fileTrace-"+ENV_VALUE_1+"."+topicName+""+dateFileCompress+fileGzSuffix;
		String requestCompress = getContentFile(fileRequestCompress,true,compressExpected);
		
		if(!compressExpected) {
			System.out.println("Attuale situazione log\n\t"+topicName+":"+sizeRequest);
		}
		
		String nonTrovatoInFileMessage="\nNon trovato in file:";
		String nonTrovatoInFileCompressMessage=" e nemmeno in file compresso:";
		
		String decompressed = ".decompressed";
		String fileRequestDecompressed = fileRequest + decompressed;
		if(requestCompress!=null) {
			FileSystemUtilities.writeFile(fileRequestDecompressed, requestCompress.getBytes());
		}
			
		if(!fileExpected) {
			if(request!=null && request.length()>0 && request.contains(expected)) {
				throw new UtilsException("File '"+fileRequest+"' contains content not expected: "+expected);
			}
			if(requestCompress!=null && requestCompress.length()>0 && requestCompress.contains(expected)) {
				throw new UtilsException("File '"+fileRequestCompress+"' contains content not expected: "+expected);
			}
		}
		else {
		
			if(!request.contains(expected) && 
					(requestCompress==null || !requestCompress.contains(expected))
			) {
				throw new UtilsException(MSG_FAILED_PREFIX+expected+nonTrovatoInFileMessage+fileRequest+nonTrovatoInFileCompressMessage+fileRequestDecompressed);
			}
			
		}
		
	}
	
	private static String getExpectedTrackingPhaseContent(boolean erogazioni, FaseTracciamento faseTracciamento) {
		
		String suffix = "\"|\"UUIDXX\"|\"XX-deXXXRR-deXXXRest\"|\"p1\"|\"p2\"|\"envValue\"|\"envValue2\"";
		
		String expected = "";
		if(erogazioni) {
			switch (faseTracciamento) {
			case IN_REQUEST:
				expected = "\"*FASE_IN_REQUEST*\"|\""+faseTracciamento.name()+suffix;
				break;
			case OUT_REQUEST:
				expected = "\"*FASE_OUT_REQUEST*\"|\""+faseTracciamento.name()+suffix;
				break;
			case OUT_RESPONSE:
				expected = "\"*FASE_OUT_RESPONSE*\"|\""+faseTracciamento.name()+suffix;
				break;
			case POST_OUT_RESPONSE:
				expected = "\"*FASE_POST_OUT_RESPONSE*\"|\""+faseTracciamento.name()+suffix;
				break;
			}
		}
		else {
			switch (faseTracciamento) {
			case IN_REQUEST:
			case OUT_REQUEST:
				expected = "\"*FASE_REQUEST*\"|\""+faseTracciamento.name()+suffix;
				break;
			case OUT_RESPONSE:
			case POST_OUT_RESPONSE:
				expected = "\"*FASE_RESPONSE*\"|\""+faseTracciamento.name()+suffix;
				break;
			}
		}
		return expected;
	}
	
	
	private static void verificaFileEncrypted(String dirName,String dirFile, boolean requestWithPayload, boolean compressExpected,
			Transazione transazioneDTO) throws FileNotFoundException, UtilsException {
		for (String tipoTest : ENCRYPT_TEST) {
			verificaFileEncrypted(dirName,dirFile, tipoTest, requestWithPayload, compressExpected,
				transazioneDTO);
		}
	}
	private static void verificaFileEncrypted(String dirName,String dirFile, String tipoTest, boolean requestWithPayload, boolean compressExpected,
			Transazione transazioneDTO) throws FileNotFoundException, UtilsException {
		
		System.out.println("Controllo log encoding '"+tipoTest+"'; compressExpected: "+compressExpected);
				
		String fileSuffix = ".log";
		String fileGzSuffix = "-1.log.gz";
		
		String dirCompress = dirName+"/";
		String dateFileCompress = "-"+dirFile;
		
		
		String fileRequest = DIR_TMP+""+"fileTrace-"+ENV_VALUE_1+"."+tipoTest+""+""+fileSuffix;
		String request = getContentFile(fileRequest,false, true);
		String sizeRequest = request!=null ? Utilities.convertBytesToFormatString(request.length()) : null;
		
		String fileRequestCompress = DIR_TMP+dirCompress+"fileTrace-"+ENV_VALUE_1+"."+tipoTest+""+dateFileCompress+fileGzSuffix;
		String requestCompress = getContentFile(fileRequestCompress,true,compressExpected);
		
		
		
		if(!compressExpected) {
			System.out.println("Attuale situazione log\n\t"+tipoTest+":"+sizeRequest);
		}
		
		String nonTrovatoInFileMessage="\nNon atteso in file:";
		String nonTrovatoInFileCompressMessage="\nNon atteso in file compresso:";
		
		String decompressed = ".decompressed";
		String fileRequestDecompressed = fileRequest + decompressed;
		if(requestCompress!=null) {
			FileSystemUtilities.writeFile(fileRequestDecompressed, requestCompress.getBytes());
		}
			
		
		String atteso = getExpectedEncryptContent(tipoTest, requestWithPayload);
		
		
		if(request!=null && request.contains(atteso)) {
			throw new UtilsException(MSG_FAILED_PREFIX+atteso+nonTrovatoInFileMessage+fileRequest);
		}
		if(compressExpected &&
			requestCompress!=null && requestCompress.contains(atteso)) {
			throw new UtilsException(MSG_FAILED_PREFIX+atteso+nonTrovatoInFileCompressMessage+fileRequestDecompressed);
		}
		
	}
	private static String getExpectedEncryptContent(String tipoTest, boolean requestWithPayload) {
		
		if("encJavaPublic".equals(tipoTest)) {
			// supporta solo una stringa corta di dati. Se troppo lunga si ottiene l'errore: 'too much data for RSA block'
			return tipoTest+"|informazioneCorta";
		}
		
		if(requestWithPayload) {
			return tipoTest+"|\"Req-In: ReqInExampleValue\"|\"Req-In-2: ReqInExampleValue2\"|\"Content-XXX: ADEDE\"|**HDR-REQ-IN**|Q29udGVudC1YWFg6IEFERURFClRpcG9NZXNzYWdnaW86IFJJQ0hJRVNUQV9JTkdSRVNTT19EVU1QX0JJTkFSSU8KQ29udGVudC1UeXBlOiB0ZXh0L3htbDsgY2hhcnNldD1cIlVURjhcIjsgdGlwbz1pblJlcXVlc3Q=|\"Req-Out: ReqOutExampleValue\"|\"Req-Out-2: ReqOutExampleValue2\"|\"Content-XXX: ADEDE\"|**HDR-REQ-OUT**|Q29udGVudC1YWFg6IEFERURFClRpcG9NZXNzYWdnaW86IFJJQ0hJRVNUQV9VU0NJVEFfRFVNUF9CSU5BUklPCkNvbnRlbnQtVHlwZTogdGV4dC94bWw7IGNoYXJzZXQ9XCJVVEY4XCI7IHRpcG89b3V0UmVxdWVzdA==|\"Res-In: ResInExampleValue\"|\"Res-In-2: ResInExampleValue2\"|\"Content-XXX: ADEDE\"|**HDR-RES-IN**|Q29udGVudC1YWFg6IEFERURFClRpcG9NZXNzYWdnaW86IFJJU1BPU1RBX0lOR1JFU1NPX0RVTVBfQklOQVJJTwpDb250ZW50LVR5cGU6IHRleHQveG1sOyBjaGFyc2V0PVwiVVRGOFwiOyB0aXBvPWluUmVzcG9uc2U=|\"Res-Out: ResOutExampleValue\"|\"Res-Out-2: ResOutExampleValue2\"|\"Content-XXX: ADEDE\"|**HDR-RES-OUT**|Q29udGVudC1YWFg6IEFERURFClRpcG9NZXNzYWdnaW86IFJJU1BPU1RBX1VTQ0lUQV9EVU1QX0JJTkFSSU8KQ29udGVudC1UeXBlOiB0ZXh0L3htbDsgY2hhcnNldD1cIlVURjhcIjsgdGlwbz1vdXRSZXNwb25zZQ==";
		}
		else {
			return tipoTest+"|\"Req-In: ReqInExampleValue\"|\"Req-In-2: ReqInExampleValue2\"|\"Content-XXX: ADEDE\"|**HDR-REQ-IN**|Q29udGVudC1YWFg6IEFERURFClRpcG9NZXNzYWdnaW86IFJJQ0hJRVNUQV9JTkdSRVNTT19EVU1QX0JJTkFSSU8=|\"Req-Out: ReqOutExampleValue\"|\"Req-Out-2: ReqOutExampleValue2\"|\"Content-XXX: ADEDE\"|**HDR-REQ-OUT**|Q29udGVudC1YWFg6IEFERURFClRpcG9NZXNzYWdnaW86IFJJQ0hJRVNUQV9VU0NJVEFfRFVNUF9CSU5BUklP|\"Res-In: ResInExampleValue\"|\"Res-In-2: ResInExampleValue2\"|\"Content-XXX: ADEDE\"|**HDR-RES-IN**|Q29udGVudC1YWFg6IEFERURFClRpcG9NZXNzYWdnaW86IFJJU1BPU1RBX0lOR1JFU1NPX0RVTVBfQklOQVJJTwpDb250ZW50LVR5cGU6IHRleHQveG1sOyBjaGFyc2V0PVwiVVRGOFwiOyB0aXBvPWluUmVzcG9uc2U=|\"Res-Out: ResOutExampleValue\"|\"Res-Out-2: ResOutExampleValue2\"|\"Content-XXX: ADEDE\"|**HDR-RES-OUT**|Q29udGVudC1YWFg6IEFERURFClRpcG9NZXNzYWdnaW86IFJJU1BPU1RBX1VTQ0lUQV9EVU1QX0JJTkFSSU8KQ29udGVudC1UeXBlOiB0ZXh0L3htbDsgY2hhcnNldD1cIlVURjhcIjsgdGlwbz1vdXRSZXNwb25zZQ==";
		}
	}
	private static void verificaExpectedEncryptContent(String tipoTest, boolean requestWithPayload, String trovato) throws UtilsException, SecurityException, FileNotFoundException {
		if("encJavaSymm".equals(tipoTest)) {
			verificaExpectedEncryptContentEncJavaSymm(tipoTest, requestWithPayload, trovato);
		}
		else if("encJoseSymm".equals(tipoTest) || "encJoseSymmDirect".equals(tipoTest)) {
			verificaExpectedEncryptContentEncJose(tipoTest, requestWithPayload, trovato);
		}
		
		else if("encJavaJCEKS".equals(tipoTest)) {
			verificaExpectedEncryptContentEncJavaJCEKS(tipoTest, requestWithPayload, trovato);
		}
		else if("encJoseJCEKS".equals(tipoTest) || "encJoseJCEKSDirect".equals(tipoTest)) {
			verificaExpectedEncryptContentEncJose(tipoTest, requestWithPayload, trovato);
		}
		
		else if("encJavaPublic".equals(tipoTest)) {
			verificaExpectedEncryptContentEncJavaPublic(tipoTest, requestWithPayload, trovato);
		}
		else if("encJavaPublicWrapKey".equals(tipoTest) || "encJavaPublicWrapKeyHex".equals(tipoTest)) {
			verificaExpectedEncryptContentEncJavaPublicWrapKey(tipoTest, requestWithPayload, trovato);
		}
		else if("encJosePublic".equals(tipoTest)) {
			verificaExpectedEncryptContentEncJose(tipoTest, requestWithPayload, trovato);
		}
		
		else if("encJavaJWKsSymm".equals(tipoTest)) {
			verificaExpectedEncryptContentEncJavaJWKsSymm(tipoTest, requestWithPayload, trovato);
		}
		else if("encJavaJWKsAsymm".equals(tipoTest)) {
			verificaExpectedEncryptContentEncJavaJWKsPublicWrapKey(tipoTest, requestWithPayload, trovato);
		}
		else if("encJoseJWKsSymm".equals(tipoTest) || "encJoseJWKsAsymm".equals(tipoTest)) {
			verificaExpectedEncryptContentEncJose(tipoTest, requestWithPayload, trovato);
		}
		
		else if("encJavaJKS".equals(tipoTest) || "encJavaPKCS12".equals(tipoTest) || "encJavaPKCS11".equals(tipoTest)) {
			verificaExpectedEncryptContentEncJavaKeystorePublicWrapKey(tipoTest, requestWithPayload, trovato);
		}
		else if("encJoseJKS".equals(tipoTest) || "encJosePKCS12".equals(tipoTest) || "encJosePKCS11".equals(tipoTest)) {
			verificaExpectedEncryptContentEncJose(tipoTest, requestWithPayload, trovato);
		}
		
	}
	private static void verificaExpectedEncryptContentEncJavaSymm(String tipoTest, boolean requestWithPayload, String trovato) throws UtilsException {
		String [] tmp = trovato.split("\\.");
		if(tmp==null || tmp.length!=2) {
			throw new UtilsException("Atteso formato iv.secret (base64)");
		}
		byte[]iv = Base64Utilities.decode(tmp[0]);
		byte[]dataEncrypted = Base64Utilities.decode(tmp[1]);
		Decrypt d = new Decrypt(SECRET_KEY.getBytes(), "AES", iv);
		String decrypted = new String(d.decrypt(dataEncrypted, "AES/CBC/PKCS5Padding"));
		
		verifica(tipoTest, requestWithPayload, decrypted);
	}
	private static void verificaExpectedEncryptContentEncJose(String tipoTest, boolean requestWithPayload, String trovato) throws UtilsException, SecurityException, FileNotFoundException {
		
		String contentAlgo = "A256GCM";
		
		String keyAlgo = "DIRECT";
		String kid = null;
		boolean expectedKid = true;
		if("encJoseSymm".equals(tipoTest)) {
			// Devo levare la prima parte
			trovato = trovato.substring("encJoseSymm|".length());
			keyAlgo = "A256KW";
			kid = "myKEY";
		}
		else if("encJoseJCEKS".equals(tipoTest)) {
			keyAlgo = "A256KW";
			kid = "openspcoop";
		}
		else if("encJoseJCEKSDirect".equals(tipoTest)) {
			expectedKid = false;
		}
		else if("encJosePublic".equals(tipoTest)) {
			keyAlgo = "RSA-OAEP-256";
			contentAlgo = "A256GCM";
			kid = "myKEY";
		}
		else if("encJoseJWKsSymm".equals(tipoTest) ) {
			keyAlgo = "A256KW";
			contentAlgo = "A256GCM";
			expectedKid = false;
		}
		else if("encJoseJWKsAsymm".equals(tipoTest) || "encJoseJKS".equals(tipoTest) || "encJosePKCS12".equals(tipoTest)) {
			keyAlgo = "RSA-OAEP-256";
			contentAlgo = "A256GCM";
			kid = "openspcoop";
		}
		else if("encJosePKCS11".equals(tipoTest)) {
			keyAlgo = "RSA1_5";
			contentAlgo = "A256GCM";
			kid = KeystoreTest.ALIAS_PKCS11_CLIENT1;
		}
		
		// check header
		Map<String, Object> check = new HashMap<>();
		check.put("alg", "DIRECT".equals(keyAlgo) ? "dir" : keyAlgo);
		check.put("enc", contentAlgo);
		if(kid!=null) {
			check.put("kid", kid);
		}
		else {
			check.put("kid", expectedKid);
		}
		if("encJosePublic".equals(tipoTest) || "encJoseJWKsAsymm".equals(tipoTest)) {
			check.put("jwk", true);
		}
		if("encJoseJKS".equals(tipoTest)) {
			check.put("x5t", true);
		}
		if("encJosePKCS12".equals(tipoTest)) {
			check.put("x5t#S256", true);
		}
		verifica(trovato, check);
		
		JsonDecrypt decryptJose = null;
		if("encJoseSymm".equals(tipoTest) || "encJoseSymmDirect".equals(tipoTest)) {
			SecretKey s = SymmetricKeyUtils.getInstance("AES").getSecretKey(SECRET_KEY.getBytes());
			JWK jwk = new JWK(s, "kid", KeyUse.ENCRYPTION);
			JWKSet jwkSet = new JWKSet();
			jwkSet.addJwk(jwk);
			jwkSet.getJson(); // rebuild	
			decryptJose = new JsonDecrypt(jwkSet.getJsonWebKeys(), true, "kid", keyAlgo, contentAlgo,
					new JWTOptions(JOSESerialization.COMPACT));
		}
		else if("encJoseJCEKS".equals(tipoTest) || "encJoseJCEKSDirect".equals(tipoTest)) {
			MerlinKeystore merlinKs = GestoreKeystoreCache.getMerlinKeystore(new RequestInfo(),
					"/tmp/keystore.jceks", "jceks", 
					"123456");
			decryptJose = new JsonDecrypt(merlinKs.getKeyStore(), true, "openspcoop", "key123456", keyAlgo, contentAlgo,
					new JWTOptions(JOSESerialization.COMPACT));
		}
		else if("encJosePublic".equals(tipoTest)) {
			byte [] privateKey = FileSystemUtilities.readBytesFromFile("/tmp/privateKey.pem");
			PrivateKey privKey = KeyUtils.getInstance().readPKCS8PrivateKeyDERFormat(privateKey);
			
			byte [] publicKey = FileSystemUtilities.readBytesFromFile("/tmp/publicKey.pem");
			PublicKey pKey = KeyUtils.getInstance().readPublicKeyPEMFormat(publicKey);
			
			JWK jwk = new JWK(pKey, privKey, "kid", KeyUse.ENCRYPTION);
			JWKSet jwkSet = new JWKSet();
			jwkSet.addJwk(jwk);
			jwkSet.getJson(); // rebuild	
			decryptJose = new JsonDecrypt(jwkSet.getJsonWebKeys(), false, "kid", keyAlgo, contentAlgo,
					new JWTOptions(JOSESerialization.COMPACT));
		}
		else if("encJoseJWKsSymm".equals(tipoTest) ) {
			JWKSet jwkSet = new JWKSet(FileSystemUtilities.readFile("/tmp/symmetric.jwk"));
			decryptJose = new JsonDecrypt(jwkSet.getJsonWebKeys(), true, "openspcoop", keyAlgo, contentAlgo,
					new JWTOptions(JOSESerialization.COMPACT));
		}
		else if("encJoseJWKsAsymm".equals(tipoTest) ) {
			JWKSet jwkSet = new JWKSet(FileSystemUtilities.readFile("/tmp/private.jwk"));
			decryptJose = new JsonDecrypt(jwkSet.getJsonWebKeys(), false, "openspcoop", keyAlgo, contentAlgo,
					new JWTOptions(JOSESerialization.COMPACT));
		}
		else if("encJoseJKS".equals(tipoTest) || "encJosePKCS12".equals(tipoTest) ) {
			MerlinKeystore merlinKs = GestoreKeystoreCache.getMerlinKeystore(new RequestInfo(),
					"encJoseJKS".equals(tipoTest) ? "/tmp/keystore.jks" : "/tmp/keystore.p12", 
					"encJoseJKS".equals(tipoTest) ? "jks" : "pkcs12",		
					"123456");
			decryptJose = new JsonDecrypt(merlinKs.getKeyStore(), false, "openspcoop", "key123456", keyAlgo, contentAlgo,
					new JWTOptions(JOSESerialization.COMPACT));
		}
		else if("encJosePKCS11".equals(tipoTest) ) {
			MerlinKeystore merlinKs = GestoreKeystoreCache.getMerlinKeystore(new RequestInfo(),
					HSMUtils.KEYSTORE_HSM_STORE_PASSWORD_UNDEFINED, 
					KeystoreTest.PKCS11_CLIENT1,		
					HSMUtils.KEYSTORE_HSM_STORE_PASSWORD_UNDEFINED);
			decryptJose = new JsonDecrypt(merlinKs.getKeyStore(), false, KeystoreTest.ALIAS_PKCS11_CLIENT1, KeystoreTest.PASSWORD, keyAlgo, contentAlgo,
					new JWTOptions(JOSESerialization.COMPACT));
		}
		else {
			throw new UtilsException("Tipo '"+tipoTest+"' non supportato");
		}
		
		decryptJose.decrypt(trovato);
		String decrypted = decryptJose.getDecodedPayload();
			
		verifica(tipoTest, requestWithPayload, 
				"encJoseSymm".equals(tipoTest) ? "encJoseSymm|"+decrypted : decrypted);
	}
	
	private static void verificaExpectedEncryptContentEncJavaJCEKS(String tipoTest, boolean requestWithPayload, String trovato) throws UtilsException, SecurityException {
		String [] tmp = trovato.split("\\.");
		if(tmp==null || tmp.length!=2) {
			throw new UtilsException("Atteso formato iv.secret (base64)");
		}
		byte[]iv = HexBinaryUtilities.decode(tmp[0]);
		byte[]dataEncrypted = HexBinaryUtilities.decode(tmp[1]);
		
		MerlinKeystore merlinKs = GestoreKeystoreCache.getMerlinKeystore(new RequestInfo(),
				"/tmp/keystore.jceks", "jceks", 
				"123456");
		SecretKey secretKey = merlinKs.getKeyStore().getSecretKey("openspcoop", "key123456");
		Decrypt d = new Decrypt(secretKey, iv);
		String decrypted = new String(d.decrypt(dataEncrypted, "AES/CBC/PKCS5Padding"));
		
		verifica(tipoTest, requestWithPayload, decrypted);
	}
	private static void verificaExpectedEncryptContentEncJavaPublic(String tipoTest, boolean requestWithPayload, String trovato) throws UtilsException, FileNotFoundException {
		byte[]dataEncrypted = Base64Utilities.decode(trovato);
		byte [] privateKey = FileSystemUtilities.readBytesFromFile("/tmp/privateKey.pem");
		PrivateKey privKey = KeyUtils.getInstance().readPKCS8PrivateKeyDERFormat(privateKey);
		Decrypt d = new Decrypt(privKey);
		String decrypted = new String(d.decrypt(dataEncrypted, "RSA"));
		
		verifica(tipoTest, requestWithPayload, decrypted);
	}
	private static void verificaExpectedEncryptContentEncJavaPublicWrapKey(String tipoTest, boolean requestWithPayload, String trovato) throws UtilsException, SecurityException, FileNotFoundException {
		
		boolean base64 = true;
		String mode = "base64";
		if("encJavaPublicWrapKeyHex".equals(tipoTest)) {
			base64 = false;
			mode = "hex";
		}
		
		String [] tmp = trovato.split("\\.");
		if(tmp==null || tmp.length!=3) {
			throw new UtilsException("Atteso formato wrappedKey.iv.secret ("+mode+")");
		}
		byte[]wrappedKey = null;
		byte[]iv = null;
		byte[]dataEncrypted = null;
		if(base64) {
			wrappedKey = Base64Utilities.decode(tmp[0]);
			iv = Base64Utilities.decode(tmp[1]);
			dataEncrypted = Base64Utilities.decode(tmp[2]);
		}
		else {
			wrappedKey = HexBinaryUtilities.decode(tmp[0]);
			iv = HexBinaryUtilities.decode(tmp[1]);
			dataEncrypted = HexBinaryUtilities.decode(tmp[2]);
		}
		
		byte [] privateKey = FileSystemUtilities.readBytesFromFile("/tmp/privateKey.pem");
		PrivateKey privKey = KeyUtils.getInstance().readPKCS8PrivateKeyDERFormat(privateKey);
		
		DecryptWrapKey d = new DecryptWrapKey(privKey);
		String decrypted = new String(d.decrypt(dataEncrypted, wrappedKey, iv, "RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "AES/CBC/PKCS5Padding"));
		
		verifica(tipoTest, requestWithPayload, decrypted);
	}
	
	private static void verificaExpectedEncryptContentEncJavaJWKsSymm(String tipoTest, boolean requestWithPayload, String trovato) throws UtilsException, FileNotFoundException {
		String [] tmp = trovato.split("\\.");
		if(tmp==null || tmp.length!=2) {
			throw new UtilsException("Atteso formato iv.secret (base64)");
		}
		byte[]iv = Base64Utilities.decode(tmp[0]);
		byte[]dataEncrypted = Base64Utilities.decode(tmp[1]);
		
		JWKSet jwkSet = new JWKSet(FileSystemUtilities.readFile("/tmp/symmetric.jwk"));
		JsonWebKey jwk = JsonUtils.readKey(jwkSet.getJsonWebKeys(),"openspcoop");
		if(jwk.getAlgorithm()==null) {
			jwk.setAlgorithm("A256GCM");
		}
		SecretKey key = JwkUtils.toSecretKey(jwk);
		
		Decrypt d = new Decrypt(key, iv);
		String decrypted = new String(d.decrypt(dataEncrypted, "AES/CBC/PKCS5Padding"));
		
		verifica(tipoTest, requestWithPayload, decrypted);
	}
	
	private static void verificaExpectedEncryptContentEncJavaJWKsPublicWrapKey(String tipoTest, boolean requestWithPayload, String trovato) throws UtilsException, SecurityException, FileNotFoundException {
		
		String mode = "base64";
		String [] tmp = trovato.split("\\.");
		if(tmp==null || tmp.length!=3) {
			throw new UtilsException("Atteso formato wrappedKey.iv.secret ("+mode+")");
		}
		byte[]wrappedKey = null;
		byte[]iv = null;
		byte[]dataEncrypted = null;
		wrappedKey = Base64Utilities.decode(tmp[0]);
		iv = Base64Utilities.decode(tmp[1]);
		dataEncrypted = Base64Utilities.decode(tmp[2]);
		
		JWKSet jwkSet = new JWKSet(FileSystemUtilities.readFile("/tmp/private.jwk"));
		JsonWebKey jwk = JsonUtils.readKey(jwkSet.getJsonWebKeys(),"openspcoop");
		if(jwk.getAlgorithm()==null) {
			jwk.setAlgorithm("A256GCM");
		}
		PrivateKey privKey = JwkUtils.toRSAPrivateKey(jwk);
		
		DecryptWrapKey d = new DecryptWrapKey(privKey);
		String decrypted = new String(d.decrypt(dataEncrypted, wrappedKey, iv, "RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "AES/CBC/PKCS5Padding"));
		
		verifica(tipoTest, requestWithPayload, decrypted);
	}
	
	private static void verificaExpectedEncryptContentEncJavaKeystorePublicWrapKey(String tipoTest, boolean requestWithPayload, String trovato) throws UtilsException, SecurityException, FileNotFoundException {
		
		String mode = "base64";
		String [] tmp = trovato.split("\\.");
		if(tmp==null || tmp.length!=3) {
			throw new UtilsException("Atteso formato wrappedKey.iv.secret ("+mode+")");
		}
		byte[]wrappedKey = null;
		byte[]iv = null;
		byte[]dataEncrypted = null;
		wrappedKey = Base64Utilities.decode(tmp[0]);
		iv = Base64Utilities.decode(tmp[1]);
		dataEncrypted = Base64Utilities.decode(tmp[2]);
		
		String path = HSMUtils.KEYSTORE_HSM_STORE_PASSWORD_UNDEFINED;
		String tipo = KeystoreTest.PKCS11_CLIENT1;
		String pwd = HSMUtils.KEYSTORE_HSM_STORE_PASSWORD_UNDEFINED;
		String alias = KeystoreTest.ALIAS_PKCS11_CLIENT1;
		String keyPWD = KeystoreTest.PASSWORD;
		String keyAlgo = "RSA/ECB/PKCS1Padding";
		if("encJavaJKS".equals(tipoTest)) {
			path = "/tmp/keystore.jks";
			tipo = "jks";
			pwd = "123456";
			alias = "openspcoop";
			keyPWD = "key123456";
			keyAlgo = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
		}
		else if("encJavaPKCS12".equals(tipoTest)) {
			path = "/tmp/keystore.p12";
			tipo = "pkcs12";
			pwd = "123456";
			alias = "openspcoop";
			keyPWD = "key123456";
			keyAlgo = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
		}
		MerlinKeystore merlinKs = GestoreKeystoreCache.getMerlinKeystore(new RequestInfo(),
				path, 
				tipo,		
				pwd);

		DecryptWrapKey d = new DecryptWrapKey(merlinKs.getKeyStore(), alias, keyPWD);
		String decrypted = new String(d.decrypt(dataEncrypted, wrappedKey, iv, keyAlgo, "AES/CBC/PKCS5Padding"));
		
		verifica(tipoTest, requestWithPayload, decrypted);
	}
	
	private static void verifica(String tipoTest, boolean requestWithPayload, String decrypted) throws UtilsException {
		String atteso = getExpectedEncryptContent(tipoTest, requestWithPayload);
		if(!decrypted.equals(atteso)) {
			throw new UtilsException(MSG_FAILED_PREFIX+atteso+"\nTrovato:\n"+decrypted);
		}		
	}
	private static void verifica(String token, Map<String, Object> check) throws UtilsException {
		/**System.out.println("----token ["+token+"] ");*/
		String [] tmp = token.split("\\.");
		if(tmp==null || tmp.length<2) {
			throw new UtilsException("Atteso formato JWE");
		}
		String hdr = new String(Base64Utilities.decode(tmp[0]));
		try {
			for (String claim : check.keySet()) {
				/**System.out.println("CERCO ["+hdr+"] in ["+"$."+claim+"]");*/
				String v = null;
				try {
					v = JsonPathExpressionEngine.extractAndConvertResultAsString(hdr, "$."+claim, LoggerWrapperFactory.getLogger(FileTraceTest.class));
				}catch(org.openspcoop2.utils.json.JsonPathNotFoundException notFound) {
					// ignore
				}
				Object o = check.get(claim);
				if(o instanceof Boolean) {
					boolean res = (Boolean) o;
					if(res) {
						if(v==null || StringUtils.isEmpty(v)) {
							throw new UtilsException("Atteso nell'header JWE ("+hdr+") il claim '"+claim+"'");
						}
					}
					else {
						if(v!=null && StringUtils.isNotEmpty(v)) {
							throw new UtilsException("Nell'header JWE ("+hdr+") non è atteso il claim '"+claim+"'");
						}
					}
				}
				else {
					if(!v.equals(o)) {
						throw new UtilsException("Atteso nell'header JWE ("+hdr+") il claim '"+claim+"' valorizzato con '"+o+"'");
					}
				}
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
}
