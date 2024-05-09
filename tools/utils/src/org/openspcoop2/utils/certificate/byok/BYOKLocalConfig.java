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

package org.openspcoop2.utils.certificate.byok;

import java.io.Serializable;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeystoreType;
import org.openspcoop2.utils.certificate.hsm.HSMManager;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.slf4j.Logger;

/**
 * BYOKLocalConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BYOKLocalConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3572589461109860459L;
			
	protected String encryptionEngine;
	
	protected KeystoreType keystoreType;
	protected String keystoreHsmType;
	protected String keystorePath;
	protected String keystorePassword;
	
	protected String keyPath;
	protected String keyInline;
	protected String keyEncoding;
	protected String keyAlgorithm;
	protected String keyAlias;
	protected String keyPassword;
	protected String keyId;
	protected boolean keyWrap = false;
	
	protected String publicKeyPath;
	protected String publicKeyInline;
	protected String publicKeyEncoding;
	
	protected String contentAlgorithm;
	
	protected String javaEncoding;

	protected boolean joseIncludeCert;
	protected boolean joseIncludePublicKey;
	protected boolean joseIncludeKeyId;
	protected boolean joseIncludeCertSha1;
	protected boolean joseIncludeCertSha256;
	
	protected BYOKLocalConfig() {}
	protected BYOKLocalConfig(String id, Properties p, Logger log, BYOKConfig config) throws UtilsException {
				
		if(p==null || p.isEmpty()) {
			log.error("Properties is null");
			throw new UtilsException("Properties '"+BYOKCostanti.PROPERTY_PREFIX+id+".*' undefined");
		}
		
		initEngine(id, p);
		
		initKeystore(id, p, config);
	}

	private void initEngine(String id, Properties p) throws UtilsException {
		this.encryptionEngine = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_LOCAL_IMPL, true);
		if(!BYOKCostanti.PROPERTY_LOCAL_ENCRYPTION_ENGINE_JAVA.equals(this.encryptionEngine) &&
				!BYOKCostanti.PROPERTY_LOCAL_ENCRYPTION_ENGINE_JOSE.equals(this.encryptionEngine)) {
			throw new UtilsException("Unsupported property '"+BYOKCostanti.PROPERTY_PREFIX+id+"."+BYOKCostanti.PROPERTY_SUFFIX_LOCAL_IMPL+"' value '"+this.encryptionEngine+"'");
		}
		
		this.contentAlgorithm = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_LOCAL_CONTENT_ALGORITHM, true);
		
		if(BYOKCostanti.PROPERTY_LOCAL_ENCRYPTION_ENGINE_JAVA.equals(this.encryptionEngine)) {
			
			this.javaEncoding = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_LOCAL_JAVA_ENCODING, true);
			if(!BYOKCostanti.PROPERTY_LOCAL_JAVA_ENCODING_BASE64.equals(this.javaEncoding) &&
					!BYOKCostanti.PROPERTY_LOCAL_JAVA_ENCODING_HEX.equals(this.javaEncoding)) {
				throw new UtilsException("Unsupported property '"+BYOKCostanti.PROPERTY_PREFIX+id+"."+BYOKCostanti.PROPERTY_SUFFIX_LOCAL_JAVA_ENCODING+"' value '"+this.javaEncoding+"'");
			}
			
			this.keyWrap = BYOKConfig.getBooleanProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_LOCAL_KEY_WRAP, false, false);
		}
		else {
			
			this.joseIncludeCert = BYOKConfig.getBooleanProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_LOCAL_JOSE_INCLUDE_CERT, false, false);
			this.joseIncludePublicKey = BYOKConfig.getBooleanProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_LOCAL_JOSE_INCLUDE_PUBLIC_KEY, false, false);
			this.joseIncludeKeyId = BYOKConfig.getBooleanProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_LOCAL_JOSE_INCLUDE_KEY_ID, false, false);
			this.joseIncludeCertSha1 = BYOKConfig.getBooleanProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_LOCAL_JOSE_INCLUDE_CERT_SHA1, false, false);
			this.joseIncludeCertSha256 = BYOKConfig.getBooleanProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_LOCAL_JOSE_INCLUDE_CERT_SHA256, false, false);
			
			String keyIdTmp = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_LOCAL_KEY_ID, false);
			if(keyIdTmp!=null) {
				this.keyId = keyIdTmp.trim();
			}
			
		}
	}
	
	private void initKeystore(String id, Properties p, BYOKConfig config) throws UtilsException {
		
		String tmpKeystoreType = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_LOCAL_KEYSTORE_TYPE, true);
		this.keystoreType = KeystoreType.toEnumFromName(tmpKeystoreType);
		if(this.keystoreType==null) {
			HSMManager hsmManager = HSMManager.getInstance();
			if(hsmManager.existsKeystoreType(tmpKeystoreType)) {
				this.keystoreType = KeystoreType.PKCS11;
				this.keystoreHsmType = tmpKeystoreType;
			}
			else {
				throw new UtilsException("Unsupported keystore '"+BYOKCostanti.PROPERTY_PREFIX+id+"."+BYOKCostanti.PROPERTY_SUFFIX_LOCAL_KEYSTORE_TYPE+"' type '"+tmpKeystoreType+"'");
			}
		}
		switch (this.keystoreType) {
		case JKS:
		case PKCS12:
		case PKCS11:
		case JWK_SET:
		case JCEKS:
			initKeystoreEngine(id, p, config);
			break;
		case KEY_PAIR:
		case PUBLIC_KEY:
		case SYMMETRIC_KEY:
			initKeyEngine(id, p);
			break;
		default:
			throw new UtilsException("Unsupported keystore '"+BYOKCostanti.PROPERTY_PREFIX+id+"."+BYOKCostanti.PROPERTY_SUFFIX_LOCAL_KEYSTORE_TYPE+"' type '"+this.keystoreType+"'");
		}
	}
	private void initKeystoreEngine(String id, Properties p, BYOKConfig config) throws UtilsException {
		
		if(KeystoreType.PKCS11.equals(this.keystoreType)) {
			this.keystorePath = HSMUtils.KEYSTORE_HSM_STORE_PASSWORD_UNDEFINED;
		}
		else {
			this.keystorePath = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_LOCAL_KEYSTORE_PATH, true);
		}
		
		if(KeystoreType.PKCS11.equals(this.keystoreType)) {
			this.keystorePassword = HSMUtils.KEYSTORE_HSM_STORE_PASSWORD_UNDEFINED;
		}
		else if(!KeystoreType.JWK_SET.equals(this.keystoreType)) {
			this.keystorePassword = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_LOCAL_KEYSTORE_PASSWORD, true);
		}
		
		initKeystoreKey(id, p, config);
	}
	private void initKeystoreKey(String id, Properties p, BYOKConfig config) throws UtilsException {
		
		this.keyAlias = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_LOCAL_KEY_ALIAS, true);
				
		if(KeystoreType.JCEKS.equals(this.keystoreType) || BYOKMode.UNWRAP.equals(config.getMode())) {
			
			this.keyPassword = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_LOCAL_KEY_PASSWORD, false);
			
		}
		
		this.keyAlgorithm = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_LOCAL_KEY_ALGORITHM, true);
		
	}
	
	private void initKeyEngine(String id, Properties p) throws UtilsException {
		
		this.keyInline = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_LOCAL_KEY_INLINE, false);
		this.keyPath = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_LOCAL_KEY_PATH, (this.keyInline==null || StringUtils.isEmpty(this.keyInline)));
		
		this.keyEncoding = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_LOCAL_KEY_ENCODING, false);
		if(this.keyEncoding!=null && StringUtils.isNotEmpty(this.keyEncoding) &&
			!BYOKCostanti.PROPERTY_LOCAL_JAVA_ENCODING_BASE64.equals(this.keyEncoding) &&
			!BYOKCostanti.PROPERTY_LOCAL_JAVA_ENCODING_HEX.equals(this.keyEncoding)) {
			throw new UtilsException("Unsupported property '"+BYOKCostanti.PROPERTY_PREFIX+id+"."+BYOKCostanti.PROPERTY_SUFFIX_LOCAL_KEY_ENCODING+"' value '"+this.keyEncoding+"'");
		}
		
		this.keyAlgorithm = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_LOCAL_KEY_ALGORITHM, true);
		
		if(KeystoreType.KEY_PAIR.equals(this.keystoreType)) {
			this.publicKeyInline = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_LOCAL_PUBLIC_KEY_INLINE, false);
			this.publicKeyPath = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_LOCAL_PUBLIC_KEY_PATH, (this.publicKeyInline==null || StringUtils.isEmpty(this.publicKeyInline)));
			
			this.publicKeyEncoding = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_LOCAL_PUBLIC_KEY_ENCODING, false);
			if(this.publicKeyEncoding!=null && StringUtils.isNotEmpty(this.publicKeyEncoding) &&
				!BYOKCostanti.PROPERTY_LOCAL_JAVA_ENCODING_BASE64.equals(this.publicKeyEncoding) &&
				!BYOKCostanti.PROPERTY_LOCAL_JAVA_ENCODING_HEX.equals(this.publicKeyEncoding)) {
				throw new UtilsException("Unsupported property '"+BYOKCostanti.PROPERTY_PREFIX+id+"."+BYOKCostanti.PROPERTY_SUFFIX_LOCAL_PUBLIC_KEY_ENCODING+"' value '"+this.publicKeyEncoding+"'");
			}
			
			this.keyPassword = BYOKConfig.getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_LOCAL_KEY_PASSWORD, false);
		}
	
	}
	
	
	public boolean isJavaEngine() {
		return BYOKCostanti.PROPERTY_LOCAL_ENCRYPTION_ENGINE_JAVA.equals(this.encryptionEngine);
	}
	public boolean isJoseEngine() {
		return BYOKCostanti.PROPERTY_LOCAL_ENCRYPTION_ENGINE_JOSE.equals(this.encryptionEngine);
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

	public String getKeyPath() {
		return this.keyPath;
	}
	public String getKeyInline() {
		return this.keyInline;
	}
	public boolean isKeyBase64Encoding() {
		return BYOKCostanti.PROPERTY_LOCAL_JAVA_ENCODING_BASE64.equals(this.keyEncoding);
	}
	public boolean isKeyHexEncoding() {
		return BYOKCostanti.PROPERTY_LOCAL_JAVA_ENCODING_HEX.equals(this.keyEncoding);
	}
	public String getKeyEncoding() {
		return this.keyEncoding;
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
	
	public String getPublicKeyPath() {
		return this.publicKeyPath;
	}
	public String getPublicKeyInline() {
		return this.publicKeyInline;
	}
	public boolean isPublicKeyBase64Encoding() {
		return BYOKCostanti.PROPERTY_LOCAL_JAVA_ENCODING_BASE64.equals(this.publicKeyEncoding);
	}
	public boolean isPublicKeyHexEncoding() {
		return BYOKCostanti.PROPERTY_LOCAL_JAVA_ENCODING_HEX.equals(this.publicKeyEncoding);
	}
	public String getPublicKeyEncoding() {
		return this.publicKeyEncoding;
	}
	
	public String getContentAlgorithm() {
		return this.contentAlgorithm;
	}
	
	public boolean isJavaBase64Encoding() {
		return BYOKCostanti.PROPERTY_LOCAL_JAVA_ENCODING_BASE64.equals(this.javaEncoding);
	}
	public boolean isJavaHexEncoding() {
		return BYOKCostanti.PROPERTY_LOCAL_JAVA_ENCODING_HEX.equals(this.javaEncoding);
	}
	public String getJavaEncoding() {
		return this.javaEncoding;
	}

	public boolean isJoseIncludeCert() {
		return this.joseIncludeCert;
	}
	public boolean isJoseIncludePublicKey() {
		return this.joseIncludePublicKey;
	}
	public boolean isJoseIncludeKeyId() {
		return this.joseIncludeKeyId;
	}
	public boolean isJoseIncludeCertSha1() {
		return this.joseIncludeCertSha1;
	}
	public boolean isJoseIncludeCertSha256() {
		return this.joseIncludeCertSha256;
	}
}
