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

package org.openspcoop2.utils.certificate.hsm;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.properties.PropertiesReader;
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
	public static synchronized void init(File f, boolean throwNotExists, Logger log, boolean accessKeystore) throws UtilsException {
		if(staticInstance==null) {
			staticInstance = new HSMManager(f, throwNotExists, log, accessKeystore);
		}
	}
	public static HSMManager getInstance() {
		// spotbugs warning 'SING_SINGLETON_GETTER_NOT_SYNCHRONIZED': l'istanza viene creata allo startup
		if (staticInstance == null) {
	        synchronized (HSMManager.class) {
	            if (staticInstance == null) {
	                return null;
	            }
	        }
	    }
		return staticInstance;
	}
	
	/*
	 * Consente di inizializzare una serie di keystore hardware
	 * 
	 * La configurazione di ogni keystore deve essere definita nel file hsm.properties fornito come argomento dove la sintassi utilizzabile è la seguente
	 * 
	 * hsm.<idKeystore>.provider: [required su nodo run] classe del provider che deve essere stata registrata in JVM/conf/security/java.security o andrà aggiunta dinamicamente tramite opzione successiva
	 * hsm.<idKeystore>.provider.add: [optional, default false] indica se il provider fornito deve essere aggiunto (se non già presente)
	 * hsm.<idKeystore>.provider.configFile: [optional] se fornito verrà utilizzato per configurare il provider tramite l'istruzione 'configure(configFile)'
	 * hsm.<idKeystore>.provider.config: [optional] se fornito verrà utilizzato per configurare il provider tramite l'istruzione 'configure(config)'
	 * hsm.<idKeystore>.pin: [required su nodo run] pin per accedere al keystore
	 * hsm.<idKeystore>.keystoreType.label: [required su nodo run] label associata al keystore e visualizzata nelle console
	 * hsm.<idKeystore>.keystoreType: [required su nodo run] tipo associato al keystore ed utilizzato per istanziarlo tramite l'istruzione 'KeyStore.getInstance(keystoreType, provider)'
	 * hsm.<idKeystore>.usableAsTrustStore: [optional, default false] indica se il keystore è utilizzabile anche come truststore di certificati
	 * hsm.<idKeystore>.usableAsSecretKeyStore: [optional, default false] indica se il keystore è utilizzabile anche come repository di chiavi segrete
	 * 
	 * Un disponibile in HSM.example
	 * 
	 **/
	
	private HashMap<String, HSMKeystore> hsmKeystoreMapIDtoConfig = new HashMap<>();
	
	private HashMap<String, String> hsmKeystoreMapKeystoreTypeLabelToID = new HashMap<>();
	
	private HSMManager(File f, boolean throwNotExists, Logger log, boolean accessKeystore) throws UtilsException {
		String prefixFile = "File '"+f.getAbsolutePath()+"'";
		if(!f.exists()) {
			if(throwNotExists) {
				throw new UtilsException(prefixFile+" not exists");
			}
		}
		else {
			if(!f.canRead()) {
				throw new UtilsException(prefixFile+" cannot read");
			}
			Properties p = new Properties();
			try {
				try(FileInputStream fin = new FileInputStream(f)){
					p.load(fin);
				}
			}catch(Exception t) {
				throw new UtilsException(prefixFile+"; initialize error: "+t.getMessage(),t);
			}
			init(p, log, accessKeystore);
		}
	}
	/**private HSMManager(Properties p, Logger log, boolean accessKeystore) throws UtilsException {
		init(p, log, accessKeystore);
	}*/
	private void init(Properties p, Logger log, boolean accessKeystore) throws UtilsException {
		
		List<String> idKeystore = new ArrayList<>();
		
		if(p!=null && !p.isEmpty()) {
			init(p, idKeystore);
		}
		
		if(!idKeystore.isEmpty()) {
			for (String idK : idKeystore) {
				init(p, log, accessKeystore, idK);		
			}
		}
		else {
			log.warn("La configurazione fornita per HSM non contiene alcun keystore");
		}
	}
	private void init(Properties p, List<String> idKeystore) {
		Enumeration<?> enKeys = p.keys();
		while (enKeys.hasMoreElements()) {
			Object object = enKeys.nextElement();
			if(object instanceof String) {
				String key = (String) object;
				init(key, idKeystore);	
			}
		}
	}
	private void init(String key, List<String> idKeystore) {
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
	private void init(Properties p, Logger log, boolean accessKeystore, String idK) throws UtilsException {
		String prefix = HSMCostanti.PROPERTY_PREFIX + idK + ".";
		PropertiesReader pReader = new PropertiesReader(p, true);
		Properties pKeystore = pReader.readProperties_convertEnvProperties(prefix);
		HSMKeystore hsmKeystore = new HSMKeystore(idK, pKeystore, log, accessKeystore);
		
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
		String d = "HSMKeystore "+idK+" registrato (keystoreType:"+hsmKeystore.getKeystoreTypeLabel()+")";
		log.info(d);	
	}
	
	public void providerInit(Logger log, boolean uniqueProviderInstance) throws UtilsException {
		if(this.hsmKeystoreMapIDtoConfig!=null && !this.hsmKeystoreMapIDtoConfig.isEmpty()) {
			for (Map.Entry<String,HSMKeystore> entry : this.hsmKeystoreMapIDtoConfig.entrySet()) {
				String idK = entry.getKey();
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
		return this.hsmKeystoreMapIDtoConfig.get(idK);
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
		List<String> l = new ArrayList<>();
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
