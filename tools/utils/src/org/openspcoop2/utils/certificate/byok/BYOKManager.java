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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

/**
 * BYOKManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BYOKManager {

	private static BYOKManager staticInstance;
	public static synchronized void init(File f, boolean throwNotExists, Logger log) throws UtilsException {
		if(staticInstance==null) {
			staticInstance = new BYOKManager(f, throwNotExists, log);
		}
	}
	public static BYOKManager getInstance() {
		return staticInstance;
	}
	
	/*
	 * Consente di inizializzare una serie di keystore hardware
	 * 
	 * La configurazione di ogni keystore deve essere definita nel file ksm.properties fornito come argomento dove la sintassi utilizzabile è la seguente
	 * 
	 * ksm.<idKSM>.type: [required] identificativo univoco del ksm utilizzato nelle configurazioni di govway
	 * ksm.<idKSM>.label: [required] etichetta associata al ksm e visualizzata nelle maschere di configurazione
	 * ksm.<idKSM>.mode: [required] indica il tipo di operazione wrap e unwrap
	 * ksm.<idKSM>.input.<idParam>.name: [optional] se fornito definisce un parametro richiesto alla configurazione della credenziale a cui il ksm viene associato 
	 * ksm.<idKSM>.input.<idParam>.label: [optional] definisce l'etichetta associata al parametro di configurazione
	 * 
	 * Le restanti proprietà definiscono come accedere al ksm possono essere valorizzate utilizzando anche le seguenti variabili:
	 * ${ksm-key}: byte[] dell'archivio indicato
	 * ${ksm-base64-key}: byte[] dell'archivio indicato codificato in base64
	 * ${ksm-hex-key}: byte[] dell'archivio indicato codificato in base64
	 * ${ksm:<nomeparametro>}: parametro indicato nella configurazione del ksm
	 * 
	 * L'operazione di wrap/unwrap può essere realizzata invocando un ksm remoto (via http) o cifrando/decifrando tramite keystore locali.
	 * 
	 * ksm.<idKSM>.encryptionMode: [optional; default:remote] indica il tipo di encryption via ksm remoto (remote) o locale (local)
	 * 
	 * Configurazioni per ksm remoto:
	 * 
	 * ksm.<idKSM>.http.endpoint: [required] definisce l'endpoint del ksm
	 * ksm.<idKSM>.http.method: [required] definisce il metodo HTTP utilzzato per connettersi al ksm
	 * ksm.<idKSM>.http.header.<nome>: definisce un header HTTP che possiede il nome indicato nella proprietà stessa
	 * ksm.<idKSM>.http.payload.inline [optional] defnisce il payload da utilizzare nella richiesta http
	 * ksm.<idKSM>.http.payload.path [optional] alternativa alla precedente proprietà defnisce il path ad un file contenente il payload da utilizzare nella richiesta http
	 * ksm.<idKSM>.http.username [optional] definisce la credenziale http-basic (username)
	 * ksm.<idKSM>.http.password [optional] definisce la credenziale http-basic (password)
	 * ksm.<idKSM>.http.connectionTimeout [optional; int] tempo massimo in millisecondi di attesa per stabilire una connessione con il server ksm
	 * ksm.<idKSM>.http.readTimeout [optional; int] tempo massimo in millisecondi di attesa per la ricezione di una risposta dal server
	 * 
	 * Le seguenti proprietà opzionali consentono invece di utilizzare e configurare un connettore di tipo https
	 * ksm.<idKSM>.https [optional; boolean] indica se utilizzare o meno un connettore di tipo https 
	 * ksm.<idKSM>.https.hostnameVerifier  [optional; boolean] indica se deve essere verificato l'hostname rispetto al certificato server
	 * 
	 * ksm.<idKSM>.https.serverAuth  [optional; boolean] indica se deve essere effettuata l'autenticazione del certificato server
	 * ksm.<idKSM>.https.serverAuth.trustStore.path: truststore per effettuare l'autenticazione
	 * ksm.<idKSM>.https.serverAuth.trustStore.type: tipo di truststore
	 * ksm.<idKSM>.https.serverAuth.trustStore.password password del truststore
	 * ksm.<idKSM>.https.serverAuth.trustStore.crls: crl
	 * ksm.<idKSM>.https.serverAuth.trustStore.ocspPolicy: OCSP Policy
	 * 
	 * ksm.<idKSM>.https.clientAuth  [optional; boolean] indica se deve essere inviato un certificato client
	 * ksm.<idKSM>.https.clientAuth.keyStore.path: keystore per effettuare l'autenticazione client
	 * ksm.<idKSM>.https.clientAuth.keyStore.type: tipo di keystore
	 * ksm.<idKSM>.https.clientAuth.keyStore.password password del keystore
	 * ksm.<idKSM>.https.clientAuth.key.alias: identifica la chiave privata
	 * ksm.<idKSM>.https.clientAuth.key.password: password della chiave privata
     *
     * Configurazioni per ksm locale:
     * 
     * ksm.<idKSM>.TODO
	 * 
	 **/
	

	private HashMap<String, BYOKConfig> ksmKeystoreMapIDtoConfig = new HashMap<>();
	
	private HashMap<String, String> ksmKeystoreMapLabelToID = new HashMap<>();
	private HashMap<String, String> ksmKeystoreMapTypeToID = new HashMap<>();
	
	private List<String> unwrapTypes = new ArrayList<>();
	private List<String> unwrapLabels = new ArrayList<>();
	private List<String> wrapTypes = new ArrayList<>();
	private List<String> wrapLabels = new ArrayList<>();

	private HashMap<String, BYOKSecurityConfig> securityMapIDtoConfig = new HashMap<>();
	
	private BYOKManager(File f, boolean throwNotExists, Logger log) throws UtilsException {
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
			init(p, log);
		}
	}
	/**private KSMManager(Properties p, Logger log, boolean accessKeystore) throws UtilsException {
		init(p, log, accessKeystore);
	}*/
	private void init(Properties p, Logger log) throws UtilsException {
		
		List<String> idKeystore = new ArrayList<>();
		List<String> securityKeystore = new ArrayList<>();
		
		if(p!=null && !p.isEmpty()) {
			init(p, idKeystore, securityKeystore);
		}
		
		if(!idKeystore.isEmpty()) {
			for (String idK : idKeystore) {
				init(p, log, idK);		
			}
		}
		else {
			log.warn("La configurazione fornita per KSM non contiene alcun keystore");
		}
		
		if(!securityKeystore.isEmpty()) {
			for (String idK : securityKeystore) {
				initSecurity(p, log, idK);		
			}
		}
		else {
			log.warn("La configurazione fornita per KSM non contiene alcun security manager");
		}
	}
	private void init(Properties p, List<String> idKeystore, List<String> securityKeystore) {
		Enumeration<?> enKeys = p.keys();
		while (enKeys.hasMoreElements()) {
			Object object = enKeys.nextElement();
			if(object instanceof String) {
				String key = (String) object;
				init(key, idKeystore, securityKeystore);	
			}
		}
	}
	private void init(String key, List<String> idKeystore, List<String> securityKeystore) {
		boolean isIdKeystore = initEngine(key, idKeystore, BYOKCostanti.PROPERTY_PREFIX);
		if(!isIdKeystore) {
			initEngine(key, securityKeystore, BYOKCostanti.SECURITY_PROPERTY_PREFIX);
		}
	}
	private boolean initEngine(String key, List<String> list, String prefix) {
		if(key.startsWith(prefix) && key.length()>(prefix.length())) {
			String tmp = key.substring(prefix.length());
			if(tmp!=null && tmp.contains(".")) {
				int indeoOf = tmp.indexOf(".");
				if(indeoOf>0) {
					String idK = tmp.substring(0,indeoOf);
					if(!list.contains(idK)) {
						list.add(idK);
					}
				}
			}
			return true;
		}
		return false;
	}
	
	private void init(Properties p, Logger log, String idK) throws UtilsException {
		String prefix = BYOKCostanti.PROPERTY_PREFIX + idK + ".";
		Properties pKeystore = Utilities.readProperties(prefix, p);
		BYOKConfig ksmKeystore = new BYOKConfig(idK, pKeystore, log);
		
		// check label
		boolean alreadyExists = false;
		for (String l : this.ksmKeystoreMapLabelToID.keySet()) {
			if(ksmKeystore.getLabel().equalsIgnoreCase(l)) {
				alreadyExists = true;
				break;
			}
		}
		if(alreadyExists) {
			throw new UtilsException("Same label found for ksm '"+this.ksmKeystoreMapLabelToID.get(ksmKeystore.getLabel())+"' e '"+idK+"'");
		}
		this.ksmKeystoreMapLabelToID.put(ksmKeystore.getLabel(), idK);
		
		// check type
		alreadyExists = false;
		for (String type : this.ksmKeystoreMapTypeToID.keySet()) {
			if(ksmKeystore.getType().equalsIgnoreCase(type)) {
				alreadyExists = true;
				break;
			}
		}
		if(alreadyExists) {
			throw new UtilsException("Same type found for ksm '"+this.ksmKeystoreMapTypeToID.get(ksmKeystore.getLabel())+"' e '"+idK+"'");
		}
		this.ksmKeystoreMapTypeToID.put(ksmKeystore.getType(), idK);
		
		// registro nelle liste
		if(BYOKMode.UNWRAP.equals(ksmKeystore.getMode())) {
			this.unwrapTypes.add(ksmKeystore.getType());
			this.unwrapLabels.add(ksmKeystore.getLabel());
		}
		else {
			this.wrapTypes.add(ksmKeystore.getType());
			this.wrapLabels.add(ksmKeystore.getLabel());
		}
		
		this.ksmKeystoreMapIDtoConfig.put(idK, ksmKeystore);
		String d = "KSM "+idK+" registrato (type:"+ksmKeystore.getType()+") label:"+ksmKeystore.getLabel()+"";
		log.info(d);	
	}
	
	private void initSecurity(Properties p, Logger log, String idK) throws UtilsException {
		String prefix = BYOKCostanti.SECURITY_PROPERTY_PREFIX + idK + ".";
		Properties pKeystore = Utilities.readProperties(prefix, p);
		BYOKSecurityConfig securityConfig = new BYOKSecurityConfig(idK, pKeystore, log);
				
		this.securityMapIDtoConfig.put(idK, securityConfig);
		String d = "Security manager "+idK+" registrato";
		log.info(d);	
	}
	
	
	public BYOKConfig getKSMConfigByType(String type) throws UtilsException {
		if(!this.ksmKeystoreMapTypeToID.containsKey(type)) {
			throw new UtilsException("KSM type '"+type+"' unknown");
		}
		String idK = this.ksmKeystoreMapTypeToID.get(type);
		if(!this.ksmKeystoreMapIDtoConfig.containsKey(idK)) {
			throw new UtilsException("KSM config for type '"+type+"' unknown ? (id:"+idK+")");
		}
		return this.ksmKeystoreMapIDtoConfig.get(idK);
	}
	
	public BYOKConfig getKSMConfigByLabel(String label) throws UtilsException {
		if(!this.ksmKeystoreMapLabelToID.containsKey(label)) {
			throw new UtilsException("KSM label '"+label+"' unknown");
		}
		String idK = this.ksmKeystoreMapLabelToID.get(label);
		if(!this.ksmKeystoreMapIDtoConfig.containsKey(idK)) {
			throw new UtilsException("KSM config for label '"+label+"' unknown ? (id:"+idK+")");
		}
		return this.ksmKeystoreMapIDtoConfig.get(idK);
	}
	
	public List<String> getKeystoreTypes() {
		List<String> l = new ArrayList<>();
		if(!this.ksmKeystoreMapLabelToID.isEmpty()) {
			for (String type : this.ksmKeystoreMapLabelToID.keySet()) {
				l.add(type);
			}
		}
		return l;
	}
	
	public boolean existsKSMConfigByType(String type) {
		if(type==null) {
			return false;
		}
		for (String i : this.ksmKeystoreMapTypeToID.keySet()) {
			if(type.equalsIgnoreCase(i)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean existsKSMConfigByLabel(String label) {
		if(label==null) {
			return false;
		}
		for (String i : this.ksmKeystoreMapLabelToID.keySet()) {
			if(label.equalsIgnoreCase(i)) {
				return true;
			}
		}
		return false;
	}
	
	public List<String> getUnwrapTypes() {
		return this.unwrapTypes;
	}
	public List<String> getUnwrapLabels() {
		return this.unwrapLabels;
	}
	public List<String> getWrapTypes() {
		return this.wrapTypes;
	}
	public List<String> getWrapLabels() {
		return this.wrapLabels;
	}
}
