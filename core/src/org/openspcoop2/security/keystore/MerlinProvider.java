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

package org.openspcoop2.security.keystore;

import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Properties;

import org.apache.wss4j.common.crypto.PasswordEncryptor;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.common.ext.WSSecurityException.ErrorCode;
import org.openspcoop2.core.config.MessageSecurity;
import org.openspcoop2.core.config.MessageSecurityFlow;
import org.openspcoop2.core.config.MessageSecurityFlowParameter;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.utils.certificate.KeystoreType;
import org.openspcoop2.utils.certificate.byok.BYOKProvider;
import org.openspcoop2.utils.certificate.byok.BYOKRequestParams;

/**
 * Implementazione che estente l'implementazione di default e permette di caricare keystore utilizzando la cache o il binario passato direttamente.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MerlinProvider extends org.apache.wss4j.common.crypto.Merlin {

	private static boolean useBouncyCastleProvider = false;
	public static boolean isUseBouncyCastleProvider() {
		return useBouncyCastleProvider;
	}
	public static void setUseBouncyCastleProvider(boolean useBouncyCastleProvider) {
		MerlinProvider.useBouncyCastleProvider = useBouncyCastleProvider;
	}
	
	public MerlinProvider() {
		super();
	}

	public MerlinProvider(Properties properties, ClassLoader loader, PasswordEncryptor passwordEncryptor)
			throws WSSecurityException, IOException {
		super(properties, loader, passwordEncryptor);
	}

	private org.openspcoop2.utils.certificate.KeyStore op2KeyStore;
	private org.openspcoop2.utils.certificate.KeyStore op2TrustStore;
	public org.openspcoop2.utils.certificate.KeyStore getOp2KeyStore() {
		return this.op2KeyStore;
	}
	public org.openspcoop2.utils.certificate.KeyStore getOp2TrustStore() {
		return this.op2TrustStore;
	}

	private Boolean useBouncyCastleProviderDirective = null;
	
	public static String readPrefix(Properties properties) {
		for (Object key : properties.keySet()) {
			if (key instanceof String) {
				String propKey = (String)key;
				if (propKey.startsWith(PREFIX)) {
					return PREFIX;
				} else if (propKey.startsWith(OLD_PREFIX)) {
					return OLD_PREFIX;
				}
			}
		}
		return PREFIX;
	}
	
	private static final String TRUST_STORE_REF = "truststore";
	private static final String KEY_STORE_REF = "keystore";
	private String getError(String tipoStore, String location) {
		if(location!=null) {
			return "Accesso al "+tipoStore+" '"+location+"' non riuscito";
		}
		else {
			return "Accesso al "+tipoStore+" non riuscito";
		}
	}
	
    public static final String SUFFIX_BYOK = ".byok";
	
    public static final String X509_CRL_FILE_VALIDATE_ONLY_END_ENTITY = X509_CRL_FILE + ".validateOnlyEndEntity";
    
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    
	@Override
	public void loadProperties(Properties properties, ClassLoader loader, PasswordEncryptor passwordEncryptor)
			throws WSSecurityException, IOException {

		if (properties == null) {
			return;
		}
		this.properties = properties;
		this.passwordEncryptor = passwordEncryptor;

		String prefix = readPrefix(properties);

		
		//
		// Load the RequestInfo
		//
		RequestInfo requestInfo = null;
		Object requestInfoObject = properties.get(KeystoreConstants.PROPERTY_REQUEST_INFO);
		if(requestInfoObject instanceof RequestInfo) {
			requestInfo = (RequestInfo) requestInfoObject;
		}
		

		//
		// Load the KeyStore
		//
		String keyStoreLocation = properties.getProperty(prefix + KEYSTORE_FILE);
		if (keyStoreLocation == null) {
			keyStoreLocation = properties.getProperty(prefix + OLD_KEYSTORE_FILE);
		}
		Object keyStoreArchiveObject = properties.get(prefix + KeystoreConstants.KEYSTORE);
		byte [] keyStoreArchive = null;
		if(keyStoreArchiveObject instanceof byte[]) {
			keyStoreArchive = (byte[]) keyStoreArchiveObject;
		}
		
		String keyStorePassword = properties.getProperty(prefix + KEYSTORE_PASSWORD);
		if (keyStorePassword != null) {
			keyStorePassword = keyStorePassword.trim();
			keyStorePassword = decryptPassword(keyStorePassword, passwordEncryptor);
		}
		String keyStoreType = properties.getProperty(prefix + KEYSTORE_TYPE, KeyStore.getDefaultType());
		if (keyStoreType != null) {
			keyStoreType = keyStoreType.trim();
		}
				
		if (keyStoreLocation != null) {
		
			String keyStoreByokPolicy = properties.getProperty(prefix + KEYSTORE_FILE+SUFFIX_BYOK);
			BYOKRequestParams byokParams = null;
			if (keyStoreByokPolicy == null) {
				keyStoreByokPolicy = properties.getProperty(prefix + OLD_KEYSTORE_FILE+SUFFIX_BYOK);
			}
			if (keyStoreByokPolicy != null) {
				keyStoreByokPolicy = keyStoreByokPolicy.trim();
				if(BYOKProvider.isPolicyDefined(keyStoreByokPolicy)){
					try {
						byokParams = BYOKProvider.getBYOKRequestParamsByUnwrapBYOKPolicy(keyStoreByokPolicy, 
								requestInfo!=null && requestInfo.getDynamicMap()!=null ? requestInfo.getDynamicMap() : new HashMap<>() );
					}catch(Exception e) {
						throw new IOException(e.getMessage(),e);
					}
				}
			}
			
			// rimuovo la proprietà per non farla trovare quando chiamo il super.loadProperties
			this.properties.remove(prefix + KEYSTORE_FILE);
			this.properties.remove(prefix + OLD_KEYSTORE_FILE);
		
			// il set 'privatePasswordSet' e' stato riportato poichè eliminando sopra le due proprietà, questo codice non verra utilizzato nel super.loadProperties
			String privatePasswd = properties.getProperty(prefix + KEYSTORE_PRIVATE_PASSWORD);
			if (privatePasswd != null) {
				this.privatePasswordSet = true;
			}
			
			
			keyStoreLocation = keyStoreLocation.trim();

			try {
				MerlinKeystore merlinKs = GestoreKeystoreCache.getMerlinKeystore(requestInfo, keyStoreLocation, keyStoreType, 
						keyStorePassword, byokParams);
				if(merlinKs==null) {
					throw new IOException(getError(KEY_STORE_REF,keyStoreLocation));
				}
				if(merlinKs.getKeyStore()==null) {
					throw new IOException(getError(KEY_STORE_REF,keyStoreLocation));
				}
				this.op2KeyStore = merlinKs.getKeyStore();
				this.keystore = this.op2KeyStore.getKeystore();
			}catch(Exception e) {
				throw new IOException("[Keystore-File] '"+keyStoreLocation+"' "+e.getMessage(),e);
			}

		}
		else if (keyStoreArchive != null) {
			try {
				MerlinKeystore merlinKs = GestoreKeystoreCache.getMerlinKeystore(requestInfo, keyStoreArchive, keyStoreType, 
						keyStorePassword);
				if(merlinKs==null) {
					throw new IOException(getError(KEY_STORE_REF,null));
				}
				if(merlinKs.getKeyStore()==null) {
					throw new IOException(getError(KEY_STORE_REF,null));
				}
				this.op2KeyStore = merlinKs.getKeyStore();
				this.keystore = this.op2KeyStore.getKeystore();
			}catch(Exception e) {
				throw new IOException("[Keystore-Archive] "+e.getMessage(),e);
			}
		}
		

		//
		// Load the TrustStore
		//
		
		String trustStoreLocation = properties.getProperty(prefix + TRUSTSTORE_FILE);
		Object trustStoreArchiveObject = properties.get(prefix + KeystoreConstants.TRUSTSTORE);
		byte [] trustStoreArchive = null;
		if(trustStoreArchiveObject instanceof byte[]) {
			trustStoreArchive = (byte[]) trustStoreArchiveObject;
		}
		
		String trustStorePassword = properties.getProperty(prefix + TRUSTSTORE_PASSWORD);
		if (trustStorePassword != null) {
			trustStorePassword = trustStorePassword.trim();
			trustStorePassword = decryptPassword(trustStorePassword, passwordEncryptor);
		}
		String trustStoreType = properties.getProperty(prefix + TRUSTSTORE_TYPE, KeyStore.getDefaultType());
		if (trustStoreType != null) {
			trustStoreType = trustStoreType.trim();
		}
		
		if (trustStoreLocation != null) {
			
			// rimuovo la proprietà per non farla trovare quando chiamo il super.loadProperties
			this.properties.remove(prefix + TRUSTSTORE_FILE);
		
			// il set 'loadCACerts' e' stato riportato poichè eliminando sopra le due proprietà, questo codice non verra utilizzato nel super.loadProperties
			this.loadCACerts = false;
			
			trustStoreLocation = trustStoreLocation.trim();

			try {
				MerlinTruststore merlinTs = GestoreKeystoreCache.getMerlinTruststore(requestInfo, trustStoreLocation, trustStoreType, 
						trustStorePassword);
				if(merlinTs==null) {
					throw new IOException(getError(TRUST_STORE_REF,trustStoreLocation));
				}
				if(merlinTs.getTrustStore()==null) {
					throw new IOException(getError(TRUST_STORE_REF,trustStoreLocation));
				}
				this.op2TrustStore = merlinTs.getTrustStore();
				this.truststore = this.op2TrustStore.getKeystore();
			}catch(Exception e) {
				throw new IOException("[Truststore-File] '"+trustStoreLocation+"' "+e.getMessage(),e);
			}

		}
		else if (trustStoreArchive != null) {
			try {
				MerlinTruststore merlinTs = GestoreKeystoreCache.getMerlinTruststore(requestInfo, trustStoreArchive, trustStoreType, 
						trustStorePassword);
				if(merlinTs==null) {
					throw new IOException(getError(TRUST_STORE_REF,null));
				}
				if(merlinTs.getTrustStore()==null) {
					throw new IOException(getError(TRUST_STORE_REF,null));
				}
				this.op2TrustStore = merlinTs.getTrustStore();
				this.truststore = this.op2TrustStore.getKeystore();
			}catch(Exception e) {
				throw new IOException("[Truststore-Archive] "+e.getMessage(),e);
			}
		}
		
		if(this.truststore!=null) {
			
			// Devo evitare che venga eseguito il loadCerts quando invoco super.loadProperties
			
			properties.remove(prefix + LOAD_CA_CERTS);
			properties.setProperty(prefix + LOAD_CA_CERTS, FALSE);
			
		}
		


		//
		// Load the CRL file(s)
		//
		String crlLocations = properties.getProperty(prefix + X509_CRL_FILE);
		if (crlLocations != null) {
			
			// rimuovo la proprietà per non farla trovare quando chiamo il super.loadProperties
			this.properties.remove(prefix + X509_CRL_FILE);
			
			CRLCertstore crlCertstore = null;
			try {
				crlCertstore = GestoreKeystoreCache.getCRLCertstore(requestInfo, crlLocations);
				if(crlCertstore==null) {
					throw new IOException("Accesso alle crl '"+crlLocations+"' non riuscito");
				}
				this.crlCertStore = crlCertstore.getCertStore();
			}catch(Exception e) {
				throw new IOException("[CRLCertstore] "+e.getMessage(),e);
			}

			String useBouncyCastleProviderProperty = properties.getProperty(prefix + "useBouncyCastleProvider");
			if (useBouncyCastleProviderProperty != null) {
				if(TRUE.equalsIgnoreCase(useBouncyCastleProviderProperty.trim())) {
					this.useBouncyCastleProviderDirective = true;
				}
				else if(FALSE.equalsIgnoreCase(useBouncyCastleProviderProperty.trim())) {
					this.useBouncyCastleProviderDirective = false;
				}
			}
			
			String validateOnlyEndEntity = properties.getProperty(prefix + X509_CRL_FILE_VALIDATE_ONLY_END_ENTITY);
			if (validateOnlyEndEntity != null) {
				if(TRUE.equalsIgnoreCase(validateOnlyEndEntity.trim())) {
					this.setValidateOnlyEndEntity(true);
				}
				else if(FALSE.equalsIgnoreCase(validateOnlyEndEntity.trim())) {
					this.setValidateOnlyEndEntity(false);
				}
			}
			else if(crlCertstore!=null && crlCertstore.getWrappedCRLCertStore()!=null && crlCertstore.getWrappedCRLCertStore().countCrls()==1) {
				// per default si assume che venga fornito solo il file CRL del certificato finale
				this.setValidateOnlyEndEntity(true);
			}
			
		}



		//
		// Super
		//
		super.loadProperties(properties, loader, passwordEncryptor);
	}

	/**@Override
	public PrivateKey getPrivateKey(PublicKey publicKey, CallbackHandler callbackHandler) throws WSSecurityException {
		System.out.println("@@@ getPrivateKey PUBLIC KEY, CALLBACK");
		return super.getPrivateKey(publicKey, callbackHandler);
	}*/

	@Override
	public PrivateKey getPrivateKey(String alias, String password) throws WSSecurityException {
		/**System.out.println("@@@ getPrivateKey arg0["+alias+"] arg1["+password+"]");*/
		if(this.op2KeyStore!=null) {
			try {
				return this.op2KeyStore.getPrivateKey(alias, password);
			}catch(Exception e) {
				throw new WSSecurityException(ErrorCode.SECURITY_ERROR,e);
			}
		}
		return super.getPrivateKey(alias, password);
	}

	/**@Override
	public PrivateKey getPrivateKey(X509Certificate x509, CallbackHandler callbackHandler) throws WSSecurityException {
		System.out.println("@@@ getPrivateKey X509Certificatw, CALLBACK");
		return super.getPrivateKey(x509, callbackHandler);
	}*/
	
	@Override
	public String getCryptoProvider() {
		boolean useBC = (this.useBouncyCastleProviderDirective!=null && this.useBouncyCastleProviderDirective) 
				||
				(MerlinProvider.useBouncyCastleProvider);
		if(useBC) {
			if(this.truststore!=null && this.truststore.getType()!=null && this.truststore.getType().equalsIgnoreCase(KeystoreType.PKCS11.getNome())) {
				useBC=false;
			}
			if(this.keystore!=null && this.keystore.getType()!=null && this.keystore.getType().equalsIgnoreCase(KeystoreType.PKCS11.getNome())) {
				useBC=false;
			}
		}
		if(useBC) {
			return org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME;
		}
		else {
			return super.getCryptoProvider();
		}
	}
	
	public static void correctProviderName(MessageSecurity messageSecurity){
		if(messageSecurity!=null) {
			MessageSecurityFlow request = messageSecurity.getRequestFlow();
			correctProviderNameRequestFlow(request);
			
			MessageSecurityFlow response = messageSecurity.getResponseFlow();
			correctProviderNameResponseFlow(response);
		}
	}
	private static void correctProviderNameRequestFlow(MessageSecurityFlow request){
		if(request!=null && request.sizeParameterList()>0) {
			for (MessageSecurityFlowParameter param : request.getParameterList()) {
				if(param.getNome()!=null && param.getNome().trim().endsWith(KeystoreConstants.PROPERTY_PROVIDER) && 
						param.getValore()!=null && KeystoreConstants.OLD_PROVIDER_GOVWAY.equals(param.getValore().trim()) ) {
					param.setValore(KeystoreConstants.PROVIDER_GOVWAY);
				}
			}
		}
	}
	private static void correctProviderNameResponseFlow(MessageSecurityFlow response){
		if(response!=null && response.sizeParameterList()>0) {
			for (MessageSecurityFlowParameter param : response.getParameterList()) {
				if(param.getNome()!=null && param.getNome().trim().endsWith(KeystoreConstants.PROPERTY_PROVIDER) && 
						param.getValore()!=null && KeystoreConstants.OLD_PROVIDER_GOVWAY.equals(param.getValore().trim()) ) {
					param.setValore(KeystoreConstants.PROVIDER_GOVWAY);
				}
			}
		}
	}
}
