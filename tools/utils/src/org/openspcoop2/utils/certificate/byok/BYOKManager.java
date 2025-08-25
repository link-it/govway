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

package org.openspcoop2.utils.certificate.byok;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.utils.SortedMap;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.properties.PropertiesReader;
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
		// spotbugs warning 'SING_SINGLETON_GETTER_NOT_SYNCHRONIZED': l'istanza viene creata allo startup
		if (staticInstance == null) {
	        synchronized (BYOKManager.class) {
	            if (staticInstance == null) {
	                return null;
	            }
	        }
	    }
		return staticInstance;
	}
	public static String getSecurityEngineGovWayPolicy() {
		if(staticInstance!=null) {
			return staticInstance.getSecurityEngineGovWay();
		}
		return null;
	}
	public static String getSecurityRemoteEngineGovWayPolicy() {
		if(staticInstance!=null) {
			return staticInstance.getSecurityRemoteEngineGovWay();
		}
		return null;
	}
	public static boolean isEnabledBYOK() {
		String securityManagerPolicy = BYOKManager.getSecurityEngineGovWayPolicy();
		return securityManagerPolicy!=null && StringUtils.isNotEmpty(securityManagerPolicy);
	}
	
	/*
	 * Consente di inizializzare una serie di keystore hardware
	 * 
	 * La configurazione di ogni keystore deve essere definita nel file kms.properties fornito come argomento dove la sintassi utilizzabile è la seguente
	 * 
	 * kms.<idKMS>.type: [required] identificativo univoco del kms utilizzato nelle configurazioni di govway
	 * kms.<idKMS>.label: [required] etichetta associata al kms e visualizzata nelle maschere di configurazione
	 * kms.<idKMS>.mode: [required] indica il tipo di operazione wrap e unwrap
	 * kms.<idKMS>.input.<idParam>.name: [optional] se fornito definisce un parametro richiesto alla configurazione della credenziale a cui il kms viene associato 
	 * kms.<idKMS>.input.<idParam>.label: [optional] definisce l'etichetta associata al parametro di configurazione
	 * 
	 * Le restanti proprietà definiscono come accedere al kms possono essere valorizzate utilizzando anche le seguenti variabili:
	 * ${kms-key}: byte[] dell'archivio indicato
	 * ${kms-base64-key}: byte[] dell'archivio indicato codificato in base64
	 * ${kms-hex-key}: byte[] dell'archivio indicato codificato in base64
	 * ${kms:<nomeparametro>}: parametro indicato nella configurazione del kms
	 * 
	 * L'operazione di wrap/unwrap può essere realizzata invocando un kms remoto (via http) o cifrando/decifrando tramite keystore locali.
	 * 
	 * kms.<idKMS>.encryptionMode: [optional; default:remote] indica il tipo di encryption via kms remoto (remote) o locale (local)
	 * 
	 * Configurazioni per kms remoto:
	 * 
	 * kms.<idKMS>.http.endpoint: [required] definisce l'endpoint del kms
	 * kms.<idKMS>.http.method: [required] definisce il metodo HTTP utilzzato per connettersi al kms
	 * kms.<idKMS>.http.header.<nome>: definisce un header HTTP che possiede il nome indicato nella proprietà stessa
	 * kms.<idKMS>.http.payload.inline [optional] definisce il payload da utilizzare nella richiesta http
	 * kms.<idKMS>.http.payload.path [optional] alternativa alla precedente proprietà defnisce il path ad un file contenente il payload da utilizzare nella richiesta http
	 * kms.<idKMS>.http.username [optional] definisce la credenziale http-basic (username)
	 * kms.<idKMS>.http.password [optional] definisce la credenziale http-basic (password)
	 * kms.<idKMS>.http.connectionTimeout [optional; int] tempo massimo in millisecondi di attesa per stabilire una connessione con il server kms
	 * kms.<idKMS>.http.readTimeout [optional; int] tempo massimo in millisecondi di attesa per la ricezione di una risposta dal server
	 * 
	 * Le seguenti proprietà opzionali consentono invece di utilizzare e configurare un connettore di tipo https
	 * kms.<idKMS>.https [optional; boolean] indica se utilizzare o meno un connettore di tipo https 
	 * kms.<idKMS>.https.hostnameVerifier  [optional; boolean] indica se deve essere verificato l'hostname rispetto al certificato server
	 * 
	 * kms.<idKMS>.https.serverAuth  [optional; boolean] indica se deve essere effettuata l'autenticazione del certificato server
	 * kms.<idKMS>.https.serverAuth.trustStore.path: truststore per effettuare l'autenticazione
	 * kms.<idKMS>.https.serverAuth.trustStore.type: tipo di truststore
	 * kms.<idKMS>.https.serverAuth.trustStore.password password del truststore
	 * kms.<idKMS>.https.serverAuth.trustStore.crls: crl
	 * kms.<idKMS>.https.serverAuth.trustStore.ocspPolicy: OCSP Policy
	 * 
	 * kms.<idKMS>.https.clientAuth  [optional; boolean] indica se deve essere inviato un certificato client
	 * kms.<idKMS>.https.clientAuth.keyStore.path: keystore per effettuare l'autenticazione client
	 * kms.<idKMS>.https.clientAuth.keyStore.type: tipo di keystore
	 * kms.<idKMS>.https.clientAuth.keyStore.password password del keystore
	 * kms.<idKMS>.https.clientAuth.key.alias: identifica la chiave privata
	 * kms.<idKMS>.https.clientAuth.key.password: password della chiave privata
     *
     * kms.<idKMS>.http.response.base64Encoded [optional; boolean] indicazione se la risposta è codificata in base64
     * kms.<idKMS>.http.response.hexEncoded [optional; boolean] indicazione se la risposta è codificata tramite una rappresentazione esadecimale
     * kms.<idKMS>.http.response.jsonPath [optional] se la risposta è un json (eventualmente dopo la decodificata base64/hex) consente di indicare un jsonPath per estrarre l'informazione da un singolo elemento
	 * 
	 **/
	

	private HashMap<String, BYOKConfig> kmsKeystoreMapIDtoConfig = new HashMap<>();
	
	private HashMap<String, String> kmsKeystoreMapLabelToID = new HashMap<>();
	private HashMap<String, String> kmsKeystoreMapTypeToID = new HashMap<>();
	
	private List<String> unwrapTypes = new ArrayList<>();
	private List<String> unwrapLabels = new ArrayList<>();
	private List<String> wrapTypes = new ArrayList<>();
	private List<String> wrapLabels = new ArrayList<>();

	private static final String UNKNOWN = "unknown";
	
	private HashMap<String, BYOKSecurityConfig> securityMapIDtoConfig = new HashMap<>();
	
	private String securityEngineGovWay = null;
	private String securityRemoteEngineGovWay = null;
	
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
	/**private BYOKManager(Properties p, Logger log, boolean accessKeystore) throws UtilsException {
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
			log.warn("La configurazione fornita per KMS non contiene alcun keystore");
		}
		
		if(!securityKeystore.isEmpty()) {
			for (String idK : securityKeystore) {
				initSecurity(p, log, idK);		
			}
		}
		else {
			log.warn("La configurazione fornita per KMS non contiene alcun security manager");
		}
		
		initSecurityGovWay(p);
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
		boolean isIdKeystore = initEngine(key, idKeystore, BYOKCostanti.PROPERTY_PREFIX_KMS);
		if(!isIdKeystore) {
			isIdKeystore = initEngine(key, idKeystore, BYOKCostanti.PROPERTY_PREFIX_KSM_DEPRECATED);
		}
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
		String byokPropertyPrefix = BYOKCostanti.PROPERTY_PREFIX_KMS;
		String prefix = byokPropertyPrefix + idK + ".";
		Properties pKeystore = Utilities.readProperties(prefix, p);
		if(pKeystore==null || pKeystore.isEmpty()) {
			byokPropertyPrefix = BYOKCostanti.PROPERTY_PREFIX_KSM_DEPRECATED;
			prefix = byokPropertyPrefix + idK + ".";
			pKeystore = Utilities.readProperties(prefix, p);
		}
		BYOKConfig kmsKeystore = new BYOKConfig(idK, pKeystore, log, byokPropertyPrefix);
		
		// check label
		boolean alreadyExists = false;
		for (String l : this.kmsKeystoreMapLabelToID.keySet()) {
			if(kmsKeystore.getLabel().equalsIgnoreCase(l)) {
				alreadyExists = true;
				break;
			}
		}
		if(alreadyExists) {
			throw new UtilsException("Same label found for kms '"+this.kmsKeystoreMapLabelToID.get(kmsKeystore.getLabel())+"' e '"+idK+"'");
		}
		this.kmsKeystoreMapLabelToID.put(kmsKeystore.getLabel(), idK);
		
		// check type
		alreadyExists = false;
		for (String type : this.kmsKeystoreMapTypeToID.keySet()) {
			if(kmsKeystore.getType().equalsIgnoreCase(type)) {
				alreadyExists = true;
				break;
			}
		}
		if(alreadyExists) {
			throw new UtilsException("Same type found for kms '"+this.kmsKeystoreMapTypeToID.get(kmsKeystore.getType())+"' e '"+idK+"'");
		}
		this.kmsKeystoreMapTypeToID.put(kmsKeystore.getType(), idK);
		
		// registro nelle liste
		if(BYOKMode.UNWRAP.equals(kmsKeystore.getMode())) {
			this.unwrapTypes.add(kmsKeystore.getType());
			this.unwrapLabels.add(kmsKeystore.getLabel());
		}
		else {
			this.wrapTypes.add(kmsKeystore.getType());
			this.wrapLabels.add(kmsKeystore.getLabel());
		}
		
		this.kmsKeystoreMapIDtoConfig.put(idK, kmsKeystore);
		String d = "KMS "+idK+" registrato (type:"+kmsKeystore.getType()+") label:"+kmsKeystore.getLabel()+"";
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
	
	private void initSecurityGovWay(Properties p) throws UtilsException {
		
		PropertiesReader pReader = new PropertiesReader(p, true);
		
		this.securityEngineGovWay = pReader.getValue_convertEnvProperties(BYOKCostanti.PROPERTY_GOVWAY_SECURITY);
		if(this.securityEngineGovWay!=null && StringUtils.isEmpty(this.securityEngineGovWay)) {
			this.securityEngineGovWay = null;
		}
		
		this.securityRemoteEngineGovWay = pReader.getValue_convertEnvProperties(BYOKCostanti.PROPERTY_GOVWAY_SECURITY_RUNTIME);
		if(this.securityRemoteEngineGovWay!=null && StringUtils.isEmpty(this.securityRemoteEngineGovWay)) {
			this.securityRemoteEngineGovWay = null;
		}
	}
	
	public BYOKConfig getKMSConfigByType(String type) throws UtilsException {
		if(!this.kmsKeystoreMapTypeToID.containsKey(type)) {
			throw new UtilsException("KMS type '"+type+"' "+UNKNOWN);
		}
		String idK = this.kmsKeystoreMapTypeToID.get(type);
		if(!this.kmsKeystoreMapIDtoConfig.containsKey(idK)) {
			throw new UtilsException("KMS config for type '"+type+"' unknown ? (id:"+idK+")");
		}
		return this.kmsKeystoreMapIDtoConfig.get(idK);
	}
	
	public BYOKConfig getKMSConfigByLabel(String label) throws UtilsException {
		if(!this.kmsKeystoreMapLabelToID.containsKey(label)) {
			throw new UtilsException("KMS label '"+label+"' "+UNKNOWN);
		}
		String idK = this.kmsKeystoreMapLabelToID.get(label);
		if(!this.kmsKeystoreMapIDtoConfig.containsKey(idK)) {
			throw new UtilsException("KMS config for label '"+label+"' unknown ? (id:"+idK+")");
		}
		return this.kmsKeystoreMapIDtoConfig.get(idK);
	}
	
	public List<String> getKeystoreTypes() {
		List<String> l = new ArrayList<>();
		if(!this.kmsKeystoreMapLabelToID.isEmpty()) {
			for (String type : this.kmsKeystoreMapLabelToID.keySet()) {
				l.add(type);
			}
		}
		return l;
	}
	
	public SortedMap<String> getKeystoreWrapConfigTypesLabels() throws UtilsException {
		return getKeystoreConfigTypesLabels(true);
	}
	public SortedMap<String> getKeystoreUnwrapConfigTypesLabels() throws UtilsException {
		return getKeystoreConfigTypesLabels(false);
	}
	private SortedMap<String> getKeystoreConfigTypesLabels(boolean wrap) throws UtilsException {
		
		SortedMap<String> sMap = new SortedMap<>();
		
		List<String> types = wrap ? this.wrapTypes : this.unwrapTypes;
		List<String> labels = wrap ? this.wrapLabels : this.unwrapLabels;
		
		// SortedMap by label
		if(types!=null && !types.isEmpty()) {
			List<String> labelsDaOrdinare = new ArrayList<>(); 
			Map<String, String> m = new HashMap<>();
			for (int i = 0; i < types.size(); i++) {
				String type = types.get(i);
				String label = labels.get(i);
				m.put(label, type);
				labelsDaOrdinare.add(label);
			}
			
			Collections.sort(labelsDaOrdinare);
			for (String l : labelsDaOrdinare) {
				sMap.add(m.get(l), l);	
			}
		}
		
		return sMap;
	}
	
	public boolean isKMSUsedInSecurityWrapConfig(String id, StringBuilder securityId) {
		return isKMSUsedInSecurityConfig(true, id, securityId);
	}
	public boolean isKMSUsedInSecurityUnwrapConfig(String id, StringBuilder securityId) {
		return isKMSUsedInSecurityConfig(false, id, securityId	);
	}
	private boolean isKMSUsedInSecurityConfig(boolean wrap, String id, StringBuilder securityId) {
		if(!this.securityMapIDtoConfig.isEmpty()) {
			for (Map.Entry<String,BYOKSecurityConfig> entry : this.securityMapIDtoConfig.entrySet()) {
				String confKmsId = wrap ? entry.getValue().getWrapId() : entry.getValue().getUnwrapId();
				if(id.equals(confKmsId)){
					if(securityId!=null) {
						securityId.append(entry.getKey());
					}
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean existsKMSConfigByType(String type) {
		if(type==null) {
			return false;
		}
		for (String i : this.kmsKeystoreMapTypeToID.keySet()) {
			if(type.equalsIgnoreCase(i)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean existsKMSConfigByLabel(String label) {
		if(label==null) {
			return false;
		}
		for (String i : this.kmsKeystoreMapLabelToID.keySet()) {
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
	
	public BYOKSecurityConfig getKMSSecurityConfig(String type) throws UtilsException {
		if(!this.securityMapIDtoConfig.containsKey(type)) {
			throw new UtilsException("KMS security config type '"+type+"' "+UNKNOWN);
		}
		BYOKSecurityConfig c = this.securityMapIDtoConfig.get(type);
		if(c==null) {
			throw new UtilsException("KMS security config type '"+type+"' "+UNKNOWN);
		}
		return c;
	}
	public List<String> getSecurityEngineTypes() {
		List<String> l = new ArrayList<>();
		if(!this.securityMapIDtoConfig.isEmpty()) {
			for (String type : this.securityMapIDtoConfig.keySet()) {
				l.add(type);
			}
		}
		return l;
	}
	
	public boolean existsSecurityEngineByType(String type) {
		if(type==null) {
			return false;
		}
		for (String i : this.securityMapIDtoConfig.keySet()) {
			if(type.equalsIgnoreCase(i)) {
				return true;
			}
		}
		return false;
	}
	
	
	public boolean isBYOKRemoteGovWayNodeUnwrapConfig() throws UtilsException {
		return isBYOKRemoteGovWayNodeConfig(BYOKManager.getSecurityEngineGovWayPolicy(), false, true);
	}
	public boolean isBYOKRemoteGovWayNodeWrapConfig() throws UtilsException {
		return isBYOKRemoteGovWayNodeConfig(BYOKManager.getSecurityEngineGovWayPolicy(), true, false);
		
	}
	
	public boolean isBYOKRemoteGovWayNodeUnwrapConfig(String securityManagerPolicy) throws UtilsException {
		return isBYOKRemoteGovWayNodeConfig(securityManagerPolicy, false, true);
	}
	public boolean isBYOKRemoteGovWayNodeWrapConfig(String securityManagerPolicy) throws UtilsException {
		return isBYOKRemoteGovWayNodeConfig(securityManagerPolicy, true, false);
		
	}
	public boolean isBYOKRemoteGovWayNodeConfig(String securityManagerPolicy) throws UtilsException {
		return isBYOKRemoteGovWayNodeConfig(securityManagerPolicy, true, true);
	}
	public boolean isBYOKRemoteGovWayNodeConfig(String securityManagerPolicy, boolean wrap, boolean unwrap) throws UtilsException {
		
		if(securityManagerPolicy==null || StringUtils.isEmpty(securityManagerPolicy)) {
			return false;
		}
		
		BYOKSecurityConfig secConfig = this.getKMSSecurityConfig(securityManagerPolicy);
		
		boolean govwayRuntime = false;
		if(secConfig.getInputParameters()!=null && !secConfig.getInputParameters().isEmpty()) {
			for (BYOKSecurityConfigParameter sec : secConfig.getInputParameters()) {
				if(sec.getValue().contains(("${"+BYOKCostanti.GOVWAY_RUNTIME_CONTEXT+":"))) {
					govwayRuntime = true;
					break;
				}
			}
		}
		if(!govwayRuntime) {
			return false;
		}
		
		return isBYOKRemoteGovWayNodeConfig(secConfig, wrap, unwrap);		
	}
	private boolean isBYOKRemoteGovWayNodeConfig(BYOKSecurityConfig secConfig, boolean wrap, boolean unwrap) throws UtilsException {
		// ne basta uno
		
		if(wrap) {
			BYOKConfig c = this.getKMSConfigByType(secConfig.getWrapId());
			if(BYOKEncryptionMode.REMOTE.equals(c.getEncryptionMode())) {
				return true;
			}
		}
		
		if(unwrap) {
			BYOKConfig c = this.getKMSConfigByType(secConfig.getUnwrapId());
			if(BYOKEncryptionMode.REMOTE.equals(c.getEncryptionMode())) {
				return true;
			}
		}
		
		return false;
	}
	
	private String getSecurityEngineGovWay() {
		if(this.securityEngineGovWay==null || StringUtils.isEmpty(this.securityEngineGovWay)) {
			return null;
		}
		return this.securityEngineGovWay;
	}
	private String getSecurityRemoteEngineGovWay() {
		if(this.securityRemoteEngineGovWay==null || StringUtils.isEmpty(this.securityRemoteEngineGovWay)) {
			return null;
		}
		return this.securityRemoteEngineGovWay;
	}
	public String getSecurityEngineGovWayDescription() {
		if(this.securityEngineGovWay==null || StringUtils.isEmpty(this.securityEngineGovWay)) {
			return "unactive";
		}
		else {
			StringBuilder sb = new StringBuilder(this.securityEngineGovWay);
			if(this.securityRemoteEngineGovWay!=null && StringUtils.isNotEmpty(this.securityRemoteEngineGovWay) && 
					!this.securityEngineGovWay.equals(this.securityRemoteEngineGovWay)) {
				sb.append(" (remote:").append(this.securityRemoteEngineGovWay).append(")");
			}
			return sb.toString();
		}
	}
}
