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
package org.openspcoop2.pdd.logger.filetrace;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.certificate.KeystoreType;
import org.openspcoop2.utils.certificate.byok.BYOKCostanti;
import org.openspcoop2.utils.certificate.hsm.HSMManager;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.properties.PropertiesReader;
import org.openspcoop2.utils.security.JwtHeaders;

/**     
 * FileTraceEncryptConfig
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FileTraceEncryptConfig {

	private String name;
	private String encryptionEngine;
	
	private KeystoreType  keystoreType;
	private String keystoreHsmType;
	private String keystorePath;
	private String keystorePassword;
	
	private String keyInline;
	private String keyPath;
	private String keyEncoding; 
	private String keyAlgorithm;
	private String keyAlias;
	private String keyPassword;
	private String keyId;
	private boolean keyWrap = false;
	
	private String pw; // password
	private String pwType; // passwordType
	private Integer pwIteration; // passwordIteration
	
	private String contentAlgorithm;
	
	private String encoding;

	private boolean joseIncludeCert;
	private boolean joseIncludePublicKey;
	private boolean joseIncludeKeyId;
	private boolean joseIncludeCertSha1;
	private boolean joseIncludeCertSha256;
	
	private String kmsId;
	private Map<String, String> kmsInput;
	
	private static final String ENCRYPTIONT_ENGINE_JAVA = BYOKCostanti.PROPERTY_LOCAL_ENCRYPTION_ENGINE_JAVA;
	private static final String ENCRYPTIONT_ENGINE_JOSE = BYOKCostanti.PROPERTY_LOCAL_ENCRYPTION_ENGINE_JOSE;
	private static final String ENCRYPTIONT_ENGINE_OPENSSL = BYOKCostanti.PROPERTY_LOCAL_ENCRYPTION_ENGINE_OPENSSL;
	
	private static final String ENCODING_BASE64 = BYOKCostanti.PROPERTY_LOCAL_ENCODING_BASE64;
	private static final String ENCODING_HEX = BYOKCostanti.PROPERTY_LOCAL_ENCODING_HEX;
	
	private static final String PREFIX_ENCRYPT = "encrypt.";
	private static final String DEBUG_PREFIX = "Property '"+PREFIX_ENCRYPT;
	private static final String UNDEFINED = " undefined";
	private static final String INVALID = " invalid";
	
	private static final String MODE = ".mode";
	
	private static final String KEYSTORE_TYPE = ".keystore.type";
	private static final String KEYSTORE_PATH = ".keystore.path";
	private static final String KEYSTORE_PASSWORD = ".keystore.password";
	
	private static final String KEY_PATH = ".key.path";
	private static final String KEY_INLINE = ".key.inline";
	private static final String KEY_ENCODING = ".key.encoding";
	private static final String KEY_ALGORITHM = ".key.algorithm";
	private static final String KEY_ALIAS = ".key.alias";
	private static final String KEY_PASSWORD = ".key.password";
	private static final String KEY_ID = ".key.id";
	private static final String KEY_WRAP = ".key.wrap";
	
	private static final String PW_VALUE = ".password";
	private static final String PW_TYPE = ".password.type";
	/**public static final String PW_TYPE_OPENSSL_AES_128_CBC = BYOKCostanti.PROPERTY_LOCAL_PW_TYPE_OPENSSL_AES_128_CBC;
	public static final String PW_TYPE_OPENSSL_AES_192_CBC = BYOKCostanti.PROPERTY_LOCAL_PW_TYPE_OPENSSL_AES_192_CBC;*/
	public static final String PW_TYPE_OPENSSL_AES_256_CBC = BYOKCostanti.PROPERTY_LOCAL_PW_TYPE_OPENSSL_AES_256_CBC;
	public static final String PW_TYPE_OPENSSL_PBKDF2_AES_128_CBC = BYOKCostanti.PROPERTY_LOCAL_PW_TYPE_OPENSSL_PBKDF2_AES_128_CBC;
	public static final String PW_TYPE_OPENSSL_PBKDF2_AES_192_CBC = BYOKCostanti.PROPERTY_LOCAL_PW_TYPE_OPENSSL_PBKDF2_AES_192_CBC;
	public static final String PW_TYPE_OPENSSL_PBKDF2_AES_256_CBC = BYOKCostanti.PROPERTY_LOCAL_PW_TYPE_OPENSSL_PBKDF2_AES_256_CBC;
	public static List<String> getLocalPasswordTypes() {
		return BYOKCostanti.getLocalPasswordTypes();
	}
	public static boolean isOpenSSLPasswordDerivationKeyMode(String mode) {
		return BYOKCostanti.isOpenSSLPasswordDerivationKeyMode(mode);
	}
	public static boolean isOpenSSLPBKDF2PasswordDerivationKeyMode(String mode) {
		return BYOKCostanti.isOpenSSLPBKDF2PasswordDerivationKeyMode(mode);
	}
	private static final String PW_TYPE_DEFAULT = BYOKCostanti.PROPERTY_LOCAL_PW_TYPE_DEFAULT;
	private static final String PW_ITERATION = ".password.iter";
	
	private static final String CONTENT_ALGORITHM = ".algorithm";
	
	private static final String ENCODING_OUTPUT = ".encoding";

	private static final String JOSE_INCLUDE_CERT = ".include.cert";
	private static final String JOSE_INCLUDE_PUBLIC_KEY = ".include.public.key";
	private static final String JOSE_INCLUDE_KEY_ID = ".include.key.id";
	private static final String JOSE_INCLUDE_CERT_SHA1 = ".include.cert.sha1";
	private static final String JOSE_INCLUDE_CERT_SHA256 = ".include.cert.sha256";
	
	private static final String KMS = ".kms";
	private static final String KMS_PARAM = ".kms.param.";
	private static final String KSM_DEPRECATED = ".ksm";
	private static final String KSM_PARAM_DEPRECATED = ".ksm.param.";

	
	public static Map<String, FileTraceEncryptConfig> parse(PropertiesReader reader) throws UtilsException{
		
		
		Properties propertiesMap = reader.readProperties_convertEnvProperties(PREFIX_ENCRYPT);
		
		Map<String, FileTraceEncryptConfig> result = new HashMap<>();
		
		List<String> keys = new ArrayList<>();
		if(propertiesMap!=null && !propertiesMap.isEmpty()) {
			for (Object s : propertiesMap.keySet()) {
				String key = (String) s;
				if(key.endsWith(MODE) && key.length()>MODE.length()) {
					String keyMode = key.substring(0,key.length()-MODE.length());
					if(keys.contains(keyMode)) {
						throw new UtilsException(DEBUG_PREFIX+key+"' already defined");
					}
					keys.add(keyMode);
				}
			}
		}
		
		parse(result, propertiesMap, keys);
		
		return result;
	}
	private static void parse(Map<String, FileTraceEncryptConfig> result, Properties propertiesMap, List<String> keys) throws UtilsException {
		if(propertiesMap!=null && !keys.isEmpty()) {
			for (String encMode : keys) {
				FileTraceEncryptConfig c = new FileTraceEncryptConfig();
				c.name = encMode;
				
				parseEngine(encMode, propertiesMap, c);
				
				parseKestore(encMode, propertiesMap, c);
				
				parseKms(encMode, propertiesMap, c);
				
				result.put(encMode, c);
			}
		}
	}
	private static void parseEngine(String encMode, Properties propertiesMap, 
			FileTraceEncryptConfig c) throws UtilsException {
		String modePName = encMode+MODE;
		c.encryptionEngine = propertiesMap.getProperty(modePName);
		if(!ENCRYPTIONT_ENGINE_JAVA.equals(c.encryptionEngine) &&
				!ENCRYPTIONT_ENGINE_JOSE.equals(c.encryptionEngine) &&
				!ENCRYPTIONT_ENGINE_OPENSSL.equals(c.encryptionEngine)) {
			throw new UtilsException(DEBUG_PREFIX+modePName+"' with unsupported engine mode '"+c.encryptionEngine+"'");
		}
		
		String algoPName = encMode+CONTENT_ALGORITHM;
		String algo = propertiesMap.getProperty(algoPName);
		if(algo==null || StringUtils.isEmpty(algo.trim())) {
			if(!ENCRYPTIONT_ENGINE_OPENSSL.equals(c.encryptionEngine)) {
				throw new UtilsException(DEBUG_PREFIX+algoPName+"'"+UNDEFINED);
			}
		}
		else {
			c.contentAlgorithm = algo.trim();
		}
		
		if(ENCRYPTIONT_ENGINE_JAVA.equals(c.encryptionEngine) ||
				ENCRYPTIONT_ENGINE_OPENSSL.equals(c.encryptionEngine)) {
			parseEncoding(encMode, propertiesMap, c);
		}
		
		if(ENCRYPTIONT_ENGINE_JAVA.equals(c.encryptionEngine)) {	
			String keyWrapPName = encMode+KEY_WRAP;
			String keyWrap = propertiesMap.getProperty(keyWrapPName);
			if(keyWrap!=null && StringUtils.isNotEmpty(keyWrap.trim())) {
				c.keyWrap = "true".equalsIgnoreCase(keyWrap);
			}
		}
		
		if(ENCRYPTIONT_ENGINE_JOSE.equals(c.encryptionEngine)) {
			parseEngineJose(encMode, propertiesMap, c);
		}
	}
	private static void parseEncoding(String encMode, Properties propertiesMap, 
			FileTraceEncryptConfig c) throws UtilsException {
		String encodingPName = encMode+ENCODING_OUTPUT;
		String encoding = propertiesMap.getProperty(encodingPName);
		if(encoding==null || StringUtils.isEmpty(encoding.trim())) {
			/**if(ENCRYPTIONT_ENGINE_JAVA.equals(c.encryptionEngine)) {
			 * In file trace e' sempre necessario produrre un output leggibile*/
			throw new UtilsException(DEBUG_PREFIX+encodingPName+"'"+UNDEFINED);
			/**}*/
		}
		else {
			c.encoding = encoding.trim();
			if(!ENCODING_BASE64.equals(c.encoding) &&
					!ENCODING_HEX.equals(c.encoding)) {
				throw new UtilsException(DEBUG_PREFIX+encodingPName+"' with unsupported encryption mode '"+c.encoding+"'");
			}
		}
	}
	private static void parseEngineJose(String encMode, Properties propertiesMap, 
			FileTraceEncryptConfig c)  {
		c.joseIncludeCert = parseEngineJoseProperty(encMode+JOSE_INCLUDE_CERT, propertiesMap);
		c.joseIncludePublicKey = parseEngineJoseProperty(encMode+JOSE_INCLUDE_PUBLIC_KEY, propertiesMap);
		c.joseIncludeKeyId = parseEngineJoseProperty(encMode+JOSE_INCLUDE_KEY_ID, propertiesMap);
		c.joseIncludeCertSha1 = parseEngineJoseProperty(encMode+JOSE_INCLUDE_CERT_SHA1, propertiesMap);
		c.joseIncludeCertSha256 = parseEngineJoseProperty(encMode+JOSE_INCLUDE_CERT_SHA256, propertiesMap);
		
		String keyIdPName = encMode+KEY_ID;
		String keyId = propertiesMap.getProperty(keyIdPName);
		if(keyId!=null) {
			c.keyId = keyId.trim();
		}
	}
	private static boolean parseEngineJoseProperty(String pName, Properties propertiesMap) {
		String v = propertiesMap.getProperty(pName);
		return "true".equalsIgnoreCase(v);
	}
	private static void parseKestore(String encMode, Properties propertiesMap, 
			FileTraceEncryptConfig c) throws UtilsException {
		String keystoreTypePName = encMode+KEYSTORE_TYPE;
		String keystoreType = propertiesMap.getProperty(keystoreTypePName);
		if(keystoreType==null || StringUtils.isEmpty(keystoreType.trim())) {
			throw new UtilsException(DEBUG_PREFIX+keystoreTypePName+"'"+UNDEFINED);
		}
		keystoreType = keystoreType.trim();
		c.keystoreType = KeystoreType.toEnumFromName(keystoreType);
		if(c.keystoreType==null) {
			if(keystoreType!=null) {
				HSMManager hsmManager = HSMManager.getInstance();
				if(hsmManager.existsKeystoreType(keystoreType)) {
					c.keystoreType = KeystoreType.PKCS11;
					c.keystoreHsmType = keystoreType;
				}
				else {
					throw new UtilsException(DEBUG_PREFIX+keystoreTypePName+"' with unsupported value '"+keystoreType+"'");
				}
			}
			else {
				throw new UtilsException(DEBUG_PREFIX+keystoreTypePName+"'"+UNDEFINED);
			}
		}
		parseKestore(encMode, propertiesMap, 
				c, keystoreTypePName, keystoreType);
	}
	private static void parseKestore(String encMode, Properties propertiesMap, 
			FileTraceEncryptConfig c, String keystoreTypePName, String keystoreType) throws UtilsException {
		String errorUnsupported = "' unsupported with "+encMode+MODE;
		switch (c.keystoreType) {
		case JKS:
		case PKCS12:
		case PKCS11:
		case JWK_SET:
		case JCEKS:
			
			if(c.isOpenSSLEngine()) {
				throw new UtilsException(DEBUG_PREFIX+keystoreTypePName+"' '"+keystoreType+errorUnsupported+" '"+ENCRYPTIONT_ENGINE_OPENSSL+"'");
			}
			
			parseKeystore(encMode, propertiesMap, c);
			
			break;
		case PUBLIC_KEY:
		case SYMMETRIC_KEY:
			
			if(c.isOpenSSLEngine()) {
				throw new UtilsException(DEBUG_PREFIX+keystoreTypePName+"' '"+keystoreType+errorUnsupported+" '"+ENCRYPTIONT_ENGINE_OPENSSL+"'");
			}
			
			parseKey(encMode, propertiesMap, c);
			
			break;
			
		case PASSWORD_KEY_DERIVATION:
			
			if(c.isJoseEngine()) {
				throw new UtilsException(DEBUG_PREFIX+keystoreTypePName+"' '"+keystoreType+errorUnsupported+" '"+ENCRYPTIONT_ENGINE_JOSE+"'");
			}
			
			parsePassword(encMode, propertiesMap, c);
			
			break;
		
		default:
			throw new UtilsException(DEBUG_PREFIX+encMode+KEYSTORE_TYPE+"' with unsupported value '"+keystoreType+"'");
		}
	}
	private static void parseKeystore(String encMode, Properties propertiesMap, 
			FileTraceEncryptConfig c) throws UtilsException {
		
		if(KeystoreType.PKCS11.equals(c.getKeystoreType())) {
			c.keystorePath = HSMUtils.KEYSTORE_HSM_STORE_PASSWORD_UNDEFINED;
		}
		else {
			String keystorePathPName = encMode+KEYSTORE_PATH;
			String keystorePath = propertiesMap.getProperty(keystorePathPName);
			if(keystorePath==null || StringUtils.isEmpty(keystorePath.trim())) {
				throw new UtilsException(DEBUG_PREFIX+keystorePathPName+"'"+UNDEFINED);
			}
			c.keystorePath = keystorePath.trim();
		}
		
		if(KeystoreType.PKCS11.equals(c.getKeystoreType())) {
			c.keystorePassword = HSMUtils.KEYSTORE_HSM_STORE_PASSWORD_UNDEFINED;
		}
		else if(!KeystoreType.JWK_SET.equals(c.getKeystoreType())) {
			String keystorePasswordPName = encMode+KEYSTORE_PASSWORD;
			String keystorePassword = propertiesMap.getProperty(keystorePasswordPName);
			if(keystorePassword==null) {
				throw new UtilsException(DEBUG_PREFIX+keystorePasswordPName+"'"+UNDEFINED);
			}
			c.keystorePassword = keystorePassword.trim();
		}
		
		parseKeystoreKey(encMode, propertiesMap, c);
	}
	private static void parseKeystoreKey(String encMode, Properties propertiesMap, 
			FileTraceEncryptConfig c) throws UtilsException {
		String keyAliasPName = encMode+KEY_ALIAS;
		String keyAlias = propertiesMap.getProperty(keyAliasPName);
		if(keyAlias==null || StringUtils.isEmpty(keyAlias.trim())) {
			throw new UtilsException(DEBUG_PREFIX+keyAliasPName+"'"+UNDEFINED);
		}
		c.keyAlias = keyAlias.trim();
		
		if(KeystoreType.JCEKS.equals(c.getKeystoreType())) {
			String keyPasswordPName = encMode+KEY_PASSWORD;
			String keyPassword = propertiesMap.getProperty(keyPasswordPName);
			if(keyPassword!=null) {
				c.keyPassword = keyPassword.trim();
			}
		}
		
		String keyAlgoPName = encMode+KEY_ALGORITHM;
		String keyAlgo = propertiesMap.getProperty(keyAlgoPName);
		if(keyAlgo==null || StringUtils.isEmpty(keyAlgo.trim())) {
			throw new UtilsException(DEBUG_PREFIX+keyAlgoPName+"'"+UNDEFINED);
		}
		c.keyAlgorithm = keyAlgo.trim();
		
	}
	private static void parseKey(String encMode, Properties propertiesMap, 
			FileTraceEncryptConfig c) throws UtilsException {
		
		String keyInLinePName = encMode+KEY_INLINE;
		String keyInLine = propertiesMap.getProperty(keyInLinePName);
		if(keyInLine!=null && StringUtils.isNotEmpty(keyInLine.trim())) {
			c.keyInline = keyInLine.trim();
		}
		else {
			String keyPathPName = encMode+KEY_PATH;
			String keyPath = propertiesMap.getProperty(keyPathPName);
			if(keyPath==null || StringUtils.isEmpty(keyPath.trim())) {
				throw new UtilsException(DEBUG_PREFIX+keyPathPName+"'"+UNDEFINED);
			}
			c.keyPath = keyPath.trim();
		}
		
		String keyEncodingPName = encMode+KEY_ENCODING;
		String keyEncoding = propertiesMap.getProperty(keyEncodingPName);
		if(keyEncoding!=null && StringUtils.isNotEmpty(keyEncoding.trim())) {
			c.keyEncoding = keyEncoding.trim();
			if(!ENCODING_BASE64.equals(c.keyEncoding) &&
					!ENCODING_HEX.equals(c.keyEncoding)) {
				throw new UtilsException(DEBUG_PREFIX+keyEncodingPName+"' with unsupported encryption mode '"+c.keyEncoding+"'");
			}
		}
		
		String keyAlgoPName = encMode+KEY_ALGORITHM;
		String keyAlgo = propertiesMap.getProperty(keyAlgoPName);
		if(keyAlgo==null || StringUtils.isEmpty(keyAlgo.trim())) {
			throw new UtilsException(DEBUG_PREFIX+keyAlgoPName+"'"+UNDEFINED);
		}
		c.keyAlgorithm = keyAlgo.trim();
	}
	
	private static void parsePassword(String encMode, Properties propertiesMap, 
			FileTraceEncryptConfig c) throws UtilsException {
		
		String passwordPName = encMode+PW_VALUE;
		String password = propertiesMap.getProperty(passwordPName);
		if(password==null || StringUtils.isEmpty(password.trim())) {
			throw new UtilsException(DEBUG_PREFIX+passwordPName+"'"+UNDEFINED);
		}
		c.pw = password.trim();
		
		String passwordTypePName = encMode+PW_TYPE;
		String passwordType = propertiesMap.getProperty(passwordTypePName);
		if(passwordType==null || StringUtils.isEmpty(passwordType.trim())) {
			c.pwType = PW_TYPE_DEFAULT;
		}
		else {
			c.pwType = passwordType.trim();
			if(!getLocalPasswordTypes().contains(c.pwType)) {
				throw new UtilsException(DEBUG_PREFIX+passwordTypePName+"'"+INVALID);
			}
		}
		
		String passwordIterationPName = encMode+PW_ITERATION;
		String passwordIteration = propertiesMap.getProperty(passwordIterationPName);
		if(passwordIteration!=null && StringUtils.isNotEmpty(passwordIteration.trim())) {
			try {
				c.pwIteration = Integer.valueOf(passwordIteration);
			}catch(Exception e) {
				throw new UtilsException(DEBUG_PREFIX+passwordIterationPName+"'"+INVALID+": "+e.getMessage());
			}
		}
	}
	
	private static void parseKms(String encMode, Properties propertiesMap, 
			FileTraceEncryptConfig c) {
		
		String prefixKMSparams = KMS_PARAM;
		
		String kmsPName = encMode+KMS;
		String kms = propertiesMap.getProperty(kmsPName);
		if(kms!=null && StringUtils.isNotEmpty(kms.trim())) {
			c.kmsId = kms.trim();
		}
		else {
			// provo precedente modalità
			kmsPName = encMode+KSM_DEPRECATED;
			kms = propertiesMap.getProperty(kmsPName);
			if(kms!=null && StringUtils.isNotEmpty(kms.trim())) {
				c.kmsId = kms.trim();
				prefixKMSparams = KSM_PARAM_DEPRECATED;
			}
		}
		
		List<String> inputParams = new ArrayList<>();
		initKmsParamsInput(encMode, propertiesMap, inputParams, prefixKMSparams);
		if(!inputParams.isEmpty()) {
			c.kmsInput = new HashMap<>();
			for (String inputId : inputParams) {
				String value = propertiesMap.getProperty(encMode+prefixKMSparams+inputId);
				if(value!=null) {
					c.kmsInput.put(inputId, value.trim());
				}
			}
		}
	}
	private static void initKmsParamsInput(String encMode, Properties propertiesMap, List<String> idKeystore, String prefixKMSparams) {
		String kmsParam = encMode+prefixKMSparams;
		Enumeration<?> enKeys = propertiesMap.keys();
		while (enKeys.hasMoreElements()) {
			Object object = enKeys.nextElement();
			if(object instanceof String) {
				String key = (String) object;
				initKmsParamsInput(key, kmsParam, idKeystore);	
			}
		}
	}
	private static void initKmsParamsInput(String key, String prefix, List<String> idKeystore) {
		if(key.startsWith(prefix) && key.length()>(prefix.length())) {
			String tmp = key.substring(prefix.length());
			if(tmp!=null && StringUtils.isNotEmpty(tmp) &&
				!idKeystore.contains(tmp)) {
				idKeystore.add(tmp);
			}
		}
	}
	
	
	public String getName() {
		return this.name;
	}
	
	public boolean isJavaEngine() {
		return ENCRYPTIONT_ENGINE_JAVA.equals(this.encryptionEngine);
	}
	public boolean isJoseEngine() {
		return ENCRYPTIONT_ENGINE_JOSE.equals(this.encryptionEngine);
	}
	public boolean isOpenSSLEngine() {
		return ENCRYPTIONT_ENGINE_OPENSSL.equals(this.encryptionEngine);
	}
	
	public KeystoreType getKeystoreType() {
		return this.keystoreType;
	}
	
	public String getKeystoreHsmType() {
		return this.keystoreHsmType;
	}

	public String getKeystorePath() {
		return this.keystorePath;
	}
	
	public String getKeystorePassword() {
		return this.keystorePassword;
	}

	public String getKeyInline() {
		return this.keyInline;
	}
	public String getKeyPath() {
		return this.keyPath;
	}
	public boolean isKeyBase64Encoding() {
		return ENCODING_BASE64.equals(this.keyEncoding);
	}
	public boolean isKeyHexEncoding() {
		return ENCODING_HEX.equals(this.keyEncoding);
	}

	public String getKeyAlgorithm() {
		return this.keyAlgorithm;
	}

	public String getKeyAlias() {
		return this.keyAlias;
	}
	public void setKeyAlias(String keyAlias) {
		this.keyAlias = keyAlias;
	}
	public void generateKeyAlias() {
		this.keyAlias = UUID.randomUUID().toString();
	}
	
	public String getKeyPassword() {
		return this.keyPassword;
	}
	
	public String getKeyId() {
		return this.keyId;
	}
	
	public boolean isKeyWrap() {
		return this.keyWrap;
	}
	
	public String getPassword() {
		return this.pw;
	}
	
	public String getPasswordType() {
		return this.pwType;
	}
	
	public Integer getPasswordIteration() {
		return this.pwIteration;
	}
	
	public String getContentAlgorithm() {
		return this.contentAlgorithm;
	}
	
	public boolean isBase64Encoding() {
		return ENCODING_BASE64.equals(this.encoding);
	}
	public boolean isHexEncoding() {
		return ENCODING_HEX.equals(this.encoding);
	}

	public JwtHeaders getJwtHeaders(KeyStore ks) throws UtilsException {
		return getJwtHeaders(ks, null);
	}
	public JwtHeaders getJwtHeaders(JsonWebKeys jsonWebKeys) throws UtilsException {
		return getJwtHeaders(null, jsonWebKeys);
	}
	private JwtHeaders getJwtHeaders(KeyStore ks, JsonWebKeys jsonWebKeys) throws UtilsException {
		JwtHeaders jwtHeaders = new JwtHeaders();
		if(this.joseIncludeKeyId) {
			jwtHeaders.setKid(this.keyAlias);
		}

		if(this.joseIncludeCert) {
			jwtHeaders.setAddX5C(true);
		}
		if(this.joseIncludeCertSha1) {
			jwtHeaders.setX509IncludeCertSha1(true);
		}
		if(this.joseIncludeCertSha256) {
			jwtHeaders.setX509IncludeCertSha256(true);
		}
		if(ks!=null && (this.joseIncludeCert || this.joseIncludeCertSha1 || this.joseIncludeCertSha256)) {
			Certificate cert = ks.getCertificate(this.keyAlias);
			if(cert instanceof X509Certificate) {
				jwtHeaders.addX509cert((X509Certificate)cert);
			}
		}
		
		if(jsonWebKeys!=null && this.joseIncludePublicKey) {
			jwtHeaders.setJwKey(jsonWebKeys, this.keyAlias);
		}
		
		return jwtHeaders;
	}

	public String getKmsId() {
		return this.kmsId;
	}
	public Map<String, String> getKmsInput() {
		return this.kmsInput;
	}

}
