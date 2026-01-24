/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
package org.openspcoop2.utils.certificate;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.util.io.pem.PemReader;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.UtilsMultiException;
import org.openspcoop2.utils.UtilsRuntimeException;

/**	
 * KeyUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class KeyUtils {

	public static final String ALGO_RSA = "RSA";
	public static final String ALGO_DSA = "DSA";
	public static final String ALGO_DH = "DH"; // Diffie-Hellman
	public static final String ALGO_EC = "EC"; // Elliptic Curve Digital Signature Algorithm o ECDH (Elliptic Curve Diffie-Hellman).

	private static volatile boolean useBouncyCastleProvider = true;
	public static boolean isUseBouncyCastleProvider() {
		return useBouncyCastleProvider;
	}
	public static void setUseBouncyCastleProvider(boolean useBouncyCastleProvider) {
		KeyUtils.useBouncyCastleProvider = useBouncyCastleProvider;
	}
	
	private static Provider provider = null;
	private static synchronized Provider getProviderEngine() {
		if ( provider == null )
			provider = Security.getProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
		return provider;
	}
	private static Provider getProvider() {
		Provider p = null;
		if(!useBouncyCastleProvider) {
			return p;
		}
		if ( provider == null )
			return getProviderEngine();
		return provider;
	}

	public static KeyUtils getInstance() throws UtilsException {
		return new KeyUtils();
	}
	public static KeyUtils getInstance(String algo) throws UtilsException {
		return new KeyUtils(algo);
	}

	private String algorithm;
	private KeyFactory kf;
	private Map<String, KeyFactory> keyFactoryMap = new ConcurrentHashMap<>();

	public KeyUtils() throws UtilsException {
		this(ALGO_RSA);
	}
	public KeyUtils(String algo) throws UtilsException {
		try {
			this.algorithm = algo;
			this.kf = getKeyFactoryEngine(algo);
			this.keyFactoryMap.put(algo, this.kf);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}

	private KeyFactory getKeyFactory(String algo) {
		return this.keyFactoryMap.computeIfAbsent(algo, a -> {
			try {
				return getKeyFactoryEngine(a);
			} catch (Exception e) {
				throw new UtilsRuntimeException(e.getMessage(), e);
			}
		});
	}
	private KeyFactory getKeyFactoryEngine(String algo) throws NoSuchAlgorithmException {
		Provider p = getProvider();
		if(p!=null) {
			return KeyFactory.getInstance(algo, p);
		}
		else{
			return KeyFactory.getInstance(algo);
		}
	}
	
	public String getAlgorithm() {
		return this.algorithm;
	}
	
	// ** PUBLIC KEY **/
	
	public PublicKey readPublicKeyPEMFormat(byte[] publicKey) throws UtilsException {
		
		PEMReader pemArchive = new PEMReader(publicKey);
		if(pemArchive.getPublicKey()!=null) {
			publicKey = pemArchive.getPublicKey().getBytes();
		}
		
		try {
			try (ByteArrayInputStream bin = new ByteArrayInputStream(publicKey);
					InputStreamReader ir = new InputStreamReader(bin);
					PemReader pemReader = new PemReader(ir);){
				byte [] encoded = pemReader.readPemObject().getContent();
				X509EncodedKeySpec specPub = new X509EncodedKeySpec(encoded);
				return this.kf.generatePublic(specPub);
	        } 
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public PublicKey readPublicKeyDERFormat(byte[] publicKey) throws UtilsException {
		try {
			X509EncodedKeySpec specPub = new X509EncodedKeySpec(publicKey);
			return this.kf.generatePublic(specPub);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public PublicKey readCertificate(byte[] publicKey) throws UtilsException {
		
		PEMReader pemArchive = new PEMReader(publicKey);
		if(pemArchive.getCertificates()!=null && !pemArchive.getCertificates().isEmpty()) {
			String cert = pemArchive.getCertificates().get(0); // prendo il primo
			if(cert!=null && StringUtils.isNotEmpty(cert)) {
				publicKey = cert.getBytes();
			}
		}
		
		return ArchiveLoader.load(publicKey).getCertificate().getCertificate().getPublicKey();
	}
	
	public PublicKey getPublicKey(byte[] publicKey) throws UtilsException {
		
		PEMReader pemArchive = new PEMReader(publicKey);
		
		if(pemArchive.getPublicKey()!=null) {
			return this.readPublicKeyPEMFormat(pemArchive.getPublicKey().getBytes());
		}
		else if(pemArchive.getCertificates()!=null && !pemArchive.getCertificates().isEmpty()) {
			String cert = pemArchive.getCertificates().get(0); // prendo il primo
			if(cert!=null && StringUtils.isNotEmpty(cert)) {
				return this.readCertificate(cert.getBytes());
			}
		}
		
		try {
			return readPublicKeyDERFormat(publicKey);
		}catch(Exception eDer) {
			// provo X509
			try {
				return readCertificate(publicKey);
			}catch(Exception eX509) {
				// rilancio entrambe le eccezioni
				UtilsMultiException multi = new UtilsMultiException("Load public key failed (DER)", eDer, eX509);
				throw new UtilsException(multi.getMultiMessage(), multi);
			}
		}

	}
	
	
	// ** PRIVATE KEY **/
	
	public PrivateKey readPKCS1PrivateKeyPEMFormat(byte[] privateKey) throws UtilsException {
		
		PEMReader pemArchive = new PEMReader(privateKey,true,false,false);
		if(pemArchive.getPrivateKey()!=null) {
			privateKey = pemArchive.getPrivateKey().getBytes();
		}
		
		// Legge nel formato PEM PKCS1 e lo porta in PKCS8
		
		try {
			try (ByteArrayInputStream bin = new ByteArrayInputStream(privateKey);
					InputStreamReader ir = new InputStreamReader(bin);
					PEMParser pemParser = new PEMParser(ir);){
				JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
				Object object = pemParser.readObject();
				KeyPair kp = converter.getKeyPair((PEMKeyPair) object);
				return kp.getPrivate(); 
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public PrivateKey readPKCS8PrivateKeyPEMFormat(byte[] privateKey) throws UtilsException {
		
		PEMReader pemArchive = new PEMReader(privateKey,false,true,false);
		if(pemArchive.getPrivateKey()!=null) {
			privateKey = pemArchive.getPrivateKey().getBytes();
		}
		
		try {
			try (ByteArrayInputStream bin = new ByteArrayInputStream(privateKey);
					InputStreamReader ir = new InputStreamReader(bin);
					PemReader pemReader = new PemReader(ir);){
				byte [] encoded = pemReader.readPemObject().getContent();
				PKCS8EncodedKeySpec specPriv = new PKCS8EncodedKeySpec(encoded);
				return this.kf.generatePrivate(specPriv);
	        } 
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public PrivateKey readPKCS8PrivateKeyDERFormat(byte[] privateKey) throws UtilsException {
		try {
			PKCS8EncodedKeySpec specPriv = new PKCS8EncodedKeySpec(privateKey);
			return this.kf.generatePrivate(specPriv);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}

	public PrivateKey readSEC1PrivateKeyDERFormat(byte[] privateKey) throws UtilsException {
		// Legge chiavi EC in formato SEC1 DER (generato con: openssl ec -outform DER)
		// SEC1 è un formato specifico per chiavi EC, quindi usa sempre KeyFactory EC
		try {
			/**System.out.println("[DEBUG-SEC1] Input byte array length: " + (privateKey != null ? privateKey.length : "null"));*/

			/**System.out.println("[DEBUG-SEC1] Step 1: Parsing ASN1Sequence...");*/
			ASN1Sequence seq = ASN1Sequence.getInstance(privateKey);
			/**System.out.println("[DEBUG-SEC1] ASN1Sequence parsed, size: " + seq.size());*/

			/**System.out.println("[DEBUG-SEC1] Step 2: Creating ECPrivateKey...");*/
			ECPrivateKey ecPrivateKey = ECPrivateKey.getInstance(seq);
			/**System.out.println("[DEBUG-SEC1] ECPrivateKey created successfully");*/

			/**System.out.println("[DEBUG-SEC1] Step 3: Getting parameters object...");*/
			Object params = ecPrivateKey.getParametersObject();
			/**System.out.println("[DEBUG-SEC1] Parameters object: " + (params != null ? params.getClass().getName() + " = " + params : "null"));*/

			if(params == null) {
				throw new UtilsException("SEC1 EC private key does not contain curve parameters (tag [0])");
			}
			if(!(params instanceof ASN1ObjectIdentifier)) {
				throw new UtilsException("SEC1 EC private key parameters is not an OID, found: " + params.getClass().getName());
			}
			ASN1ObjectIdentifier curveOid = (ASN1ObjectIdentifier) params;
			/**System.out.println("[DEBUG-SEC1] Curve OID: " + curveOid.getId());*/

			/**System.out.println("[DEBUG-SEC1] Step 4: Creating AlgorithmIdentifier...");*/
			AlgorithmIdentifier algId = new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, curveOid);
			/**System.out.println("[DEBUG-SEC1] AlgorithmIdentifier created");*/

			/**System.out.println("[DEBUG-SEC1] Step 5: Creating PrivateKeyInfo...");*/
			PrivateKeyInfo privKeyInfo = new PrivateKeyInfo(algId, ecPrivateKey);
			/**System.out.println("[DEBUG-SEC1] PrivateKeyInfo created");*/

			/**System.out.println("[DEBUG-SEC1] Step 6: Creating PKCS8EncodedKeySpec...");*/
			PKCS8EncodedKeySpec specPriv = new PKCS8EncodedKeySpec(privKeyInfo.getEncoded());
			/**System.out.println("[DEBUG-SEC1] PKCS8EncodedKeySpec created, length: " + specPriv.getEncoded().length);*/

			/**System.out.println("[DEBUG-SEC1] Step 7: Generating private key with EC KeyFactory...");*/
			KeyFactory kfEC = getKeyFactory(ALGO_EC);
			/**System.out.println("[DEBUG-SEC1] KeyFactory algorithm: " + kfEC.getAlgorithm());*/
			PrivateKey result = kfEC.generatePrivate(specPriv);
			if(result!=null) {
				/**System.out.println("[DEBUG-SEC1] Private key generated successfully: " + result.getAlgorithm());*/
			}

			return result;
		}catch(UtilsException e) {
			throw e;
		}catch(Exception e) {
			/**System.out.println("[DEBUG-SEC1] ERROR: " + e.getClass().getName() + ": " + e.getMessage());*/
			/**e.printStackTrace(System.out);*/
			throw new UtilsException("Could not parse SEC1 EC private key: " + e.getMessage(), e);
		}
	}

	public PrivateKey getPrivateKey(byte[] privateKey) throws UtilsException {

		PEMReader pemArchive = new PEMReader(privateKey);
		if(pemArchive.getPrivateKey()!=null) {
			privateKey = pemArchive.getPrivateKey().getBytes();

			if(pemArchive.isPkcs1() || pemArchive.isSec1ec()) {
				return this.readPKCS1PrivateKeyPEMFormat(privateKey);
			}
			else if(pemArchive.isPkcs8()) {
				return this.readPKCS8PrivateKeyPEMFormat(privateKey);
			}
		}

		try {
			return readPKCS8PrivateKeyDERFormat(privateKey);
		}catch(Exception ePkcs8) {
			// provo con SEC1 DER (chiavi EC)
			try {
				return readSEC1PrivateKeyDERFormat(privateKey);
			}catch(Exception eSec1) {
				// rilancio entrambe le eccezioni
				UtilsMultiException multi = new UtilsMultiException("Load private key failed (DER)", ePkcs8, eSec1);
				throw new UtilsException(multi.getMultiMessage(), multi);
			}
		}
	}


	// ** PRIVATE KEY ENCRYPTED **/
	
	public PrivateKey readPKCS1EncryptedPrivateKeyPEMFormat(byte[] privateKey, String password) throws UtilsException{
		
		PEMReader pemArchive = new PEMReader(privateKey,true,false,false);
		if(pemArchive.getPrivateKey()!=null) {
			privateKey = pemArchive.getPrivateKey().getBytes();
		}
		
		// Legge nel formato PEM PKCS1 e lo porta in PKCS8
		
		try {
			try (ByteArrayInputStream bin = new ByteArrayInputStream(privateKey);
					InputStreamReader ir = new InputStreamReader(bin);
					PEMParser pemParser = new PEMParser(ir);){
				JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
				Object object = pemParser.readObject();
				PEMEncryptedKeyPair pair = (PEMEncryptedKeyPair) object;
				JcePEMDecryptorProviderBuilder jce = new JcePEMDecryptorProviderBuilder().setProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
				PEMDecryptorProvider decProv = jce.build(password.toCharArray());
				KeyPair kp = converter.getKeyPair(pair.decryptKeyPair(decProv));
				return kp.getPrivate(); 
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}

	}
		
	public PrivateKey readPKCS8EncryptedPrivateKeyPEMFormat(byte[] privateKey, String password) throws UtilsException{
			
		PEMReader pemArchive = new PEMReader(privateKey,false,false,true);
		if(pemArchive.getPrivateKey()!=null) {
			privateKey = pemArchive.getPrivateKey().getBytes();
		}
		
		PKCS8EncryptedPrivateKeyInfo pair = null;
		try {
			try (ByteArrayInputStream bin = new ByteArrayInputStream(privateKey);
					InputStreamReader ir = new InputStreamReader(bin);
					PEMParser parser = new PEMParser(ir);){
				pair = (PKCS8EncryptedPrivateKeyInfo)parser.readObject();
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		return readPKCS8EncryptedPrivateKey(pair, password);

	}
	
	public PrivateKey readPKCS8EncryptedPrivateKeyDERFormat(byte[] privateKey, String password) throws UtilsException{
		PKCS8EncryptedPrivateKeyInfo pair = null;
		try {
			pair = new PKCS8EncryptedPrivateKeyInfo(privateKey);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		return readPKCS8EncryptedPrivateKey(pair, password);
	}
	
	private PrivateKey readPKCS8EncryptedPrivateKey(PKCS8EncryptedPrivateKeyInfo pair, String password) throws UtilsException{
		try {
			JceOpenSSLPKCS8DecryptorProviderBuilder jce = new JceOpenSSLPKCS8DecryptorProviderBuilder().setProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
			InputDecryptorProvider decProv = jce.build(password.toCharArray());
			PrivateKeyInfo keyInfo = pair.decryptPrivateKeyInfo(decProv);
			JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
			return converter.getPrivateKey(keyInfo);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public PrivateKey getPrivateKey(byte[] privateKey, String password) throws UtilsException {

		PEMReader pemArchive = new PEMReader(privateKey);
		if(pemArchive.getPrivateKey()!=null) {
			byte[] pemPrivateKey = pemArchive.getPrivateKey().getBytes();
			return getPrivateKeyFromPEM(pemPrivateKey, password, pemArchive);
		}

		return getPrivateKeyFromDER(privateKey, password);
	}

	private PrivateKey getPrivateKeyFromPEM(byte[] privateKey, String password, PEMReader pemArchive) throws UtilsException {
		if(pemArchive.isPkcs8encrypted()) {
			return this.readPKCS8EncryptedPrivateKeyPEMFormat(privateKey, password);
		}
		else if(pemArchive.isPkcs1() || pemArchive.isSec1ec()) {
			return getPrivateKeyFromPEMPkcs1OrSec1(privateKey, password);
		}
		else if(pemArchive.isPkcs8()) {
			return this.readPKCS8PrivateKeyPEMFormat(privateKey);
		}
		throw new UtilsException("Unsupported PEM private key format");
	}

	private PrivateKey getPrivateKeyFromPEMPkcs1OrSec1(byte[] privateKey, String password) throws UtilsException {
		try {
			return this.readPKCS1EncryptedPrivateKeyPEMFormat(privateKey, password);
		}catch(Exception e) {
			// provo senza password
			try {
				return this.readPKCS1PrivateKeyPEMFormat(privateKey);
			}catch(Exception eNoPassword) {
				// rilancio entrambe le eccezioni
				UtilsMultiException multi = new UtilsMultiException("Load private key failed (PEM PKCS1/SEC1)", e, eNoPassword);
				throw new UtilsException(multi.getMultiMessage(), multi);
			}
		}
	}

	private PrivateKey getPrivateKeyFromDER(byte[] privateKey, String password) throws UtilsException {
		try {
			return readPKCS8EncryptedPrivateKeyDERFormat(privateKey, password);
		}catch(Exception ePkcs8Enc) {
			return getPrivateKeyFromDERUnencrypted(privateKey, ePkcs8Enc);
		}
	}

	private PrivateKey getPrivateKeyFromDERUnencrypted(byte[] privateKey, Exception ePkcs8Enc) throws UtilsException {
		try {
			return readPKCS8PrivateKeyDERFormat(privateKey);
		}catch(Exception ePkcs8) {
			// provo con SEC1 DER (chiavi EC)
			try {
				return readSEC1PrivateKeyDERFormat(privateKey);
			}catch(Exception eSec1) {
				// rilancio tutte le eccezioni
				UtilsMultiException multi = new UtilsMultiException("Load private key failed (DER)", ePkcs8Enc, ePkcs8, eSec1);
				throw new UtilsException(multi.getMultiMessage(), multi);
			}
		}
	}


}
