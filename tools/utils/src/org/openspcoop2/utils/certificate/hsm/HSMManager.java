/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.certificate.hsm;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeyStore;
import org.slf4j.Logger;

/**
 * HSMManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HSMManager {

	private static HSMManager staticInstance;
	public static synchronized void init(File f, boolean throwNotExists, Logger log) throws UtilsException {
		if(staticInstance==null) {
			staticInstance = new HSMManager(f, throwNotExists, log);
		}
	}
	public static HSMManager getInstance() {
		return staticInstance;
	}
	
	/*
	 * Consente di inizializzare una serie di keystore hardware
	 * 
	 * La configurazione di ogni keystore deve essere definita nel file hsm.properties fornito come argomento dove la sintassi utilizzabile è la seguente
	 * 
	 * hsm.<idKeystore>.provider: [required] classe del provider che deve essere stata registrata in JVM/conf/security/java.security o andrà aggiunta dinamicamente tramite opzione successiva
	 * hsm.<idKeystore>.provider.add: [optional, default false] indica se il provider fornito deve essere aggiunto (se non già presente)
	 * hsm.<idKeystore>.provider.configFile: [optional] se fornito verrà utilizzato per configurare il provider tramite l'istruzione 'configure(configFile)'
	 * hsm.<idKeystore>.provider.config: [optional] se fornito verrà utilizzato per configurare il provider tramite l'istruzione 'configure(config)'
	 * hsm.<idKeystore>.keystoreType.label: [required] label associata al keystore e visualizzata nelle console
	 * hsm.<idKeystore>.keystoreType: [required] tipo associato al keystore ed utilizzato per istanziarlo tramite l'istruzione 'KeyStore.getInstance(keystoreType, provider)'
	 * hsm.<idKeystore>.usableAsTruststore: [optional, default false] indica se il keystore è utilizzabile anche come truststore di certificati
	 * 
	 * Ad esempio seguendo quando descritto in HSM.example, la configurazione potrebbe essere:
	 * 
	 * hsm.keystoreClient1.provider=SunPKCS11
	 * hsm.keystoreClient1.provider.add=true
	 * hsm.keystoreClient1.provider.configFile=$HOME/lib/softhsm/softhsm_java_client1.conf
	 * hsm.keystoreClient1.keystoreType.label=pkcs11-client1
	 * hsm.keystoreClient1.keystoreType=pkcs11
	 *
	 * hsm.keystoreClient2.provider=SunPKCS11
	 * hsm.keystoreClient2.provider.add=true
	 * hsm.keystoreClient2.provider.config=--name = softhsm-client2\nlibrary = /usr/lib64/libsofthsm2.so\nslotListIndex = 1
	 * hsm.keystoreClient2.keystoreType.label=pkcs11-client2
	 * hsm.keystoreClient2.keystoreType=pkcs11
	 * 
	 * hsm.keystoreServer.provider=SunPKCS11
	 * hsm.keystoreServer.provider.add=true
	 * hsm.keystoreServer.provider.configFile=$HOME/lib/softhsm/softhsm_java_server.conf
	 * hsm.keystoreServer.keystoreType.label=pkcs11-server
	 * hsm.keystoreServer.keystoreType=pkcs11
	 * hsm.keystoreServer.usableAsTruststore=true
	 **/
	
	private HashMap<String, HSMKeystore> hsmKeystoreMapIDtoConfig = new HashMap<String, HSMKeystore>();
	
	private HashMap<String, String> hsmKeystoreMapKeystoreTypeLabelToID = new HashMap<String, String>();
	
	private HSMManager(File f, boolean throwNotExists, Logger log) throws UtilsException {
		if(!f.exists()) {
			if(throwNotExists) {
				throw new UtilsException("File '"+f.getAbsolutePath()+"' not exists");
			}
		}
		else {
			if(!f.canRead()) {
				throw new UtilsException("File '"+f.getAbsolutePath()+"' cannot read");
			}
			Properties p = new Properties();
			try {
				try(FileInputStream fin = new FileInputStream(f)){
					p.load(fin);
				}
			}catch(Throwable t) {
				throw new UtilsException("File '"+f.getAbsolutePath()+"'; initialize error: "+t.getMessage(),t);
			}
			init(p, log);
		}
	}
	private HSMManager(Properties p, Logger log) throws UtilsException {
		init(p, log);
	}
	private void init(Properties p, Logger log) throws UtilsException {
		
		List<String> idKeystore = new ArrayList<String>();
		
		if(p!=null && !p.isEmpty()) {
			
			Enumeration<?> enKeys = p.keys();
			while (enKeys.hasMoreElements()) {
				Object object = (Object) enKeys.nextElement();
				if(object instanceof String) {
					String key = (String) object;
					
					if(key.startsWith(HSMCostanti.PROPERTY_PREFIX) && key.length()>(HSMCostanti.PROPERTY_PREFIX.length())) {
						String tmp = key.substring(HSMCostanti.PROPERTY_PREFIX.length());
						if(tmp!=null && tmp.contains(".")) {
							int indeoOf = tmp.indexOf(".");
							if(indeoOf>0) {
								String idK = tmp.substring(0,indeoOf);
								if(!idKeystore.contains(idK)) {
									idKeystore.add(idK);
								}
							}
						}
					}
				}
			}
			
		}
		
		if(!idKeystore.isEmpty()) {
			for (String idK : idKeystore) {
				String prefix = HSMCostanti.PROPERTY_PREFIX + idK + ".";
				Properties pKeystore = Utilities.readProperties(prefix, p);
				HSMKeystore hsmKeystore = new HSMKeystore(idK, pKeystore, log);
				
				boolean alreadyExists = false;
				for (String type : this.hsmKeystoreMapKeystoreTypeLabelToID.keySet()) {
					if(hsmKeystore.getKeystoreTypeLabel().equalsIgnoreCase(type)) {
						alreadyExists = true;
					}
				}
				if(alreadyExists) {
					throw new UtilsException("Same keystore type label found for keystore '"+this.hsmKeystoreMapKeystoreTypeLabelToID.get(hsmKeystore.getKeystoreTypeLabel())+"' e '"+idK+"'");
				}
				this.hsmKeystoreMapKeystoreTypeLabelToID.put(hsmKeystore.getKeystoreTypeLabel(), idK);
				
				this.hsmKeystoreMapIDtoConfig.put(idK, hsmKeystore);
				log.info("HSMKeystore "+idK+" registrato (keystoreType:"+hsmKeystore.getKeystoreTypeLabel()+")");				
			}
		}
		else {
			log.warn("La configurazione fornita per HSM non contiene alcun keystore");
		}
	}
	
	public void providerInit(Logger log, boolean uniqueProviderInstance) throws UtilsException {
		if(this.hsmKeystoreMapIDtoConfig!=null && !this.hsmKeystoreMapIDtoConfig.isEmpty()) {
			for (String idK : this.hsmKeystoreMapIDtoConfig.keySet()) {
				HSMKeystore hsmKeystore = this.hsmKeystoreMapIDtoConfig.get(idK);
				hsmKeystore.init(log, uniqueProviderInstance);
			}
		}
	}
	
	private HSMKeystore getHSMKeystore(String keystoreTypeLabel) throws UtilsException {
		if(!this.hsmKeystoreMapKeystoreTypeLabelToID.containsKey(keystoreTypeLabel)) {
			throw new UtilsException("Keystore type '"+keystoreTypeLabel+"' unknown");
		}
		String idK = this.hsmKeystoreMapKeystoreTypeLabelToID.get(keystoreTypeLabel);
		if(!this.hsmKeystoreMapIDtoConfig.containsKey(idK)) {
			throw new UtilsException("Keystore config for type '"+keystoreTypeLabel+"' unknown ? (id:"+idK+")");
		}
		HSMKeystore hsmKeystore = this.hsmKeystoreMapIDtoConfig.get(idK);
		return hsmKeystore;
	}
	
	public KeyStore getKeystore(String keystoreTypeLabel) throws UtilsException {
		HSMKeystore hsmKeystore = getHSMKeystore(keystoreTypeLabel);
		return hsmKeystore.getInstance();
	}
	
	public boolean isUsableAsTrustStore(String keystoreTypeLabel) throws UtilsException {
		HSMKeystore hsmKeystore = getHSMKeystore(keystoreTypeLabel);
		return hsmKeystore.isUsableAsTrustStore();
	}
	
	public boolean isUsableAsSecretKeyStore(String keystoreTypeLabel) throws UtilsException {
		HSMKeystore hsmKeystore = getHSMKeystore(keystoreTypeLabel);
		return hsmKeystore.isUsableAsSecretKeyStore();
	}
	
	public List<String> getKeystoreTypes() {
		List<String> l = new ArrayList<String>();
		if(!this.hsmKeystoreMapKeystoreTypeLabelToID.isEmpty()) {
			for (String type : this.hsmKeystoreMapKeystoreTypeLabelToID.keySet()) {
				l.add(type);
			}
		}
		return l;
	}
	
	public boolean existsKeystoreType(String keystoreTypeLabel) {
		if(keystoreTypeLabel==null) {
			return false;
		}
		for (String type : this.hsmKeystoreMapKeystoreTypeLabelToID.keySet()) {
			if(keystoreTypeLabel.equalsIgnoreCase(type)) {
				return true;
			}
		}
		return false;
	}
}
