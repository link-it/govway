package org.openspcoop2.security.keystore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.Properties;

import org.apache.wss4j.common.crypto.PasswordEncryptor;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;

/**
 * Implementazione che estente l'implementazione di default e permette di caricare keystore utilizzando la cache o il binario passato direttamente.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Merlin extends org.apache.wss4j.common.crypto.Merlin {

	public Merlin() {
		super();
	}

	public Merlin(Properties properties, ClassLoader loader, PasswordEncryptor passwordEncryptor)
			throws WSSecurityException, IOException {
		super(properties, loader, passwordEncryptor);
	}

	@Override
	public void loadProperties(Properties properties, ClassLoader loader, PasswordEncryptor passwordEncryptor)
			throws WSSecurityException, IOException {

		if (properties == null) {
			return;
		}
		this.properties = properties;
		this.passwordEncryptor = passwordEncryptor;

		String prefix = PREFIX;
		for (Object key : properties.keySet()) {
			if (key instanceof String) {
				String propKey = (String)key;
				if (propKey.startsWith(PREFIX)) {
					break;
				} else if (propKey.startsWith(OLD_PREFIX)) {
					prefix = OLD_PREFIX;
					break;
				}
			}
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
		if(keyStoreArchiveObject!=null && keyStoreArchiveObject instanceof byte[]) {
			keyStoreArchive = (byte[]) keyStoreArchiveObject;
		}
		
		String keyStorePassword = properties.getProperty(prefix + KEYSTORE_PASSWORD, "security");
		if (keyStorePassword != null) {
			keyStorePassword = keyStorePassword.trim();
			keyStorePassword = decryptPassword(keyStorePassword, passwordEncryptor);
		}
		String keyStoreType = properties.getProperty(prefix + KEYSTORE_TYPE, KeyStore.getDefaultType());
		if (keyStoreType != null) {
			keyStoreType = keyStoreType.trim();
		}
				
		if (keyStoreLocation != null) {
			
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
				MerlinKeystore merlinKs = GestoreKeystoreCache.getMerlinKeystore(keyStoreLocation, keyStoreType, 
						keyStorePassword);
				if(merlinKs==null) {
					throw new Exception("Accesso al keystore '"+keyStoreLocation+"' non riuscito");
				}
				this.keystore = merlinKs.getKeyStore();
			}catch(Exception e) {
				throw new IOException("[Keystore-File] '"+keyStoreLocation+"' "+e.getMessage(),e);
			}

		}
		else if (keyStoreArchive != null) {
			try {
				this.keystore = KeyStore.getInstance(keyStoreType);
				try(ByteArrayInputStream bin = new ByteArrayInputStream(keyStoreArchive)){
					this.keystore.load(bin, keyStorePassword.toCharArray());
				}
				FixTrustAnchorsNotEmpty.addCertificate(this.keystore);
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
		if(trustStoreArchiveObject!=null && trustStoreArchiveObject instanceof byte[]) {
			trustStoreArchive = (byte[]) trustStoreArchiveObject;
		}
		
		String trustStorePassword = properties.getProperty(prefix + TRUSTSTORE_PASSWORD, "security");
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
				MerlinTruststore merlinTs = GestoreKeystoreCache.getMerlinTruststore(trustStoreLocation, trustStoreType, 
						trustStorePassword);
				if(merlinTs==null) {
					throw new Exception("Accesso al truststore '"+trustStoreLocation+"' non riuscito");
				}
				this.truststore = merlinTs.getTrustStore();
			}catch(Exception e) {
				throw new IOException("[Truststore-File] '"+trustStoreLocation+"' "+e.getMessage(),e);
			}

		}
		else if (trustStoreArchive != null) {
			try {
				this.truststore = KeyStore.getInstance(trustStoreType);
				try(ByteArrayInputStream bin = new ByteArrayInputStream(trustStoreArchive)){
					this.truststore.load(bin, trustStorePassword.toCharArray());
				}
				FixTrustAnchorsNotEmpty.addCertificate(this.truststore);
			}catch(Exception e) {
				throw new IOException("[Truststore-Archive] "+e.getMessage(),e);
			}
		}
		
		if(this.truststore!=null) {
			
			// Devo evitare che venga eseguito il loadCerts quando invoco super.loadProperties
			
			properties.remove(prefix + LOAD_CA_CERTS);
			properties.setProperty(prefix + LOAD_CA_CERTS, "false");
			
		}
		


		//
		// Load the CRL file(s)
		//
		String crlLocations = properties.getProperty(prefix + X509_CRL_FILE);
		if (crlLocations != null) {
			
			// rimuovo la proprietà per non farla trovare quando chiamo il super.loadProperties
			this.properties.remove(prefix + X509_CRL_FILE);
			
			try {
				CRLCertstore crlCertstore = GestoreKeystoreCache.getCRLCertstore(crlLocations);
				if(crlCertstore==null) {
					throw new Exception("Accesso alle crl '"+crlLocations+"' non riuscito");
				}
				this.crlCertStore = crlCertstore.getCertStore();
			}catch(Exception e) {
				throw new IOException("[CRLCertstore] "+e.getMessage(),e);
			}
			
		}



		//
		// Super
		//
		super.loadProperties(properties, loader, passwordEncryptor);
	}

}
