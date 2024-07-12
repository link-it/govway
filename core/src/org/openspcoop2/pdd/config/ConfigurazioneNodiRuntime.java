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

package org.openspcoop2.pdd.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.byok.BYOKCostanti;
import org.openspcoop2.utils.properties.PropertiesReader;
import org.openspcoop2.utils.transport.TransportUtils;
import org.slf4j.Logger;

/**
 * ConfigurazioneNodiRuntime
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneNodiRuntime {

	public static final String RESOURCE_NAME = "govway.nodirun.properties";
	
	public static final String RESOURCE_TIPOLOGIA_ACCESSO_JMX = "jmx";
	public static final String RESOURCE_TIPOLOGIA_ACCESSO_OPENSPCOOP = "openspcoop";
	public static final String RESOURCE_TIPOLOGIA_ACCESSO_GOVWAY = "govway";
	
	public static final String ALIAS_DEFAULT = "pdd";
	
	private static Map<String, ConfigurazioneNodiRuntime> staticInstanceMap = new HashMap<>();
	private static final String PREFIX_DEFAULT = "";
	public static void initialize(String path, ConfigurazioneNodiRuntimeProperties ... backwardCompatibilitiesProperties) throws UtilsException {
		
		ConfigurazioneNodiRuntimeProperties cpClasspath = null;
		try(InputStream is = ConfigurazioneNodiRuntime.class.getResourceAsStream("/"+RESOURCE_NAME);){
			if(is!=null) {
				Properties p = new Properties();
				p.load(is);
				cpClasspath = new ConfigurazioneNodiRuntimeProperties(PREFIX_DEFAULT, p);
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		
		ConfigurazioneNodiRuntimeProperties cpFile = null;
		if(path!=null && StringUtils.isNotEmpty(path)) {
			File f = new File(path);
			boolean read = false;
			if(cpClasspath!=null || (backwardCompatibilitiesProperties!=null && backwardCompatibilitiesProperties.length>0)) {
				// la configurazione di default e' opzionale, se siamo in backwardCompatibilities mode
				if(f.exists() && f.canRead()) {
					read = true;
				}
			}
			else {
				if(!f.exists()) {
					throw new UtilsException("Configuration file '"+f.getAbsolutePath()+"' not exists");
				}
				if(!f.canRead()) {
					throw new UtilsException("Configuration file '"+f.getAbsolutePath()+"' cannot read");
				}
				read = true;
			}
			if(read) {
				try(FileInputStream fin = new FileInputStream(f)){
					Properties p = new Properties();
					p.load(fin);
					cpFile = new ConfigurazioneNodiRuntimeProperties(PREFIX_DEFAULT, p);
				}catch(Exception e) {
					throw new UtilsException(e.getMessage(),e);
				}
			}
		}
		
		if(cpClasspath!=null || cpFile!=null) {
			ConfigurazioneNodiRuntime newInstance = new ConfigurazioneNodiRuntime(cpFile, cpClasspath);
			staticInstanceMap.put(PREFIX_DEFAULT, newInstance);
		}
		
		if(backwardCompatibilitiesProperties!=null && backwardCompatibilitiesProperties.length>0) {
			for (ConfigurazioneNodiRuntimeProperties bc : backwardCompatibilitiesProperties) {
				ConfigurazioneNodiRuntime newInstance = new ConfigurazioneNodiRuntime(bc, null);
				staticInstanceMap.put(bc.getPrefix(), newInstance);
			}
		}
			
	}
	public static ConfigurazioneNodiRuntime getConfigurazioneNodiRuntime() {
		return getConfigurazioneNodiRuntime(PREFIX_DEFAULT);
	}
	public static ConfigurazioneNodiRuntime getConfigurazioneNodiRuntime(String prefix) {
		if(staticInstanceMap!=null) {
			return staticInstanceMap.get(prefix);
		}
		return null;
	}
	public static List<String> getPrefixes(){
		List<String> l = null;
		if(staticInstanceMap!=null && !staticInstanceMap.isEmpty()) {
			l = new ArrayList<>();
			l.addAll(staticInstanceMap.keySet());
			return l;
		}
		return l;
	}
	
	private PropertiesReader reader;
	private PropertiesReader readerClasspath;
	private String prefix = PREFIX_DEFAULT;
	
	private boolean clusterDinamico = false;
	
	private List<String> aliases;
	
	private Map<String,List<String>> gruppiAliases;
	
	private Map<String, String> descrizione;
	
	private Map<String, String> tipoAccesso;
	
	private Map<String, String> username;
	private Map<String, String> password;
	
	private Map<String, Boolean> https;
	private Map<String, Boolean> httpsVerificaHostName;
	private Map<String, Boolean> httpsAutenticazioneServer;
	private Map<String, String> httpsAutenticazioneServerTruststorePath;
	private Map<String, String> httpsAutenticazioneServerTruststoreType;
	private Map<String, String> httpsAutenticazioneServerTruststorePassword;
	
	private Map<String, String> connectionTimeout;
	private Map<String, String> readConnectionTimeout;
	private Map<String, String> readConnectionTimeoutSlowOperation;
	
	private Map<String, String> as;
	private Map<String, String> factory;
	
	private Map<String, String> resourceUrl; // check con parametri o proxy in modalità dinamica
	private Map<String, String> forceResourceUrl = new HashMap<>();
	private Map<String, String> checkStatusUrl; // check senza parametri
	
	private Map<String, String> dominio;
	
	private ConfigurazioneNodiRuntime(ConfigurazioneNodiRuntimeProperties config, ConfigurazioneNodiRuntimeProperties configClasspath) throws UtilsException {
		if(config!=null) {
			this.reader = new PropertiesReader(config.getProperties(), false);
			this.prefix = config.getPrefix();
		}
		if(configClasspath!=null) {
			this.readerClasspath = new PropertiesReader(configClasspath.getProperties(), false);
			this.prefix = configClasspath.getPrefix();
		}
		if(this.reader==null && this.readerClasspath==null) {
			throw new UtilsException("Nessuna configurazione fornita");
		}
		
		this.initAliases();
		this.initGruppiAliases();
		this.initConfigAliases();
		
	}
	
	public boolean containsNode(String alias) {
		return this.aliases.contains(alias);
	}
	
	public void initClusterDinamico() throws UtilsException {
		String tipo = this.readProperty(false, "clusterDinamico");
		if(tipo!=null) {
			this.clusterDinamico = "true".equalsIgnoreCase(tipo);
		}
		else {
			// backward compatibility
			if(!PREFIX_DEFAULT.equals(this.prefix)){
				tipo = this.readProperty(false, "cluster_dinamico.enabled");
				if(tipo!=null) {
					this.clusterDinamico = "true".equalsIgnoreCase(tipo);
				}
			}
		}
	}
	public boolean isClusterDinamico() {
		return this.clusterDinamico;
	}
	
	public void initAliases() throws UtilsException {
		List<String> list = new ArrayList<>();
		String tipo = this.readProperty(false, "aliases");
		if(tipo!=null && !"".equals(tipo)){
			String [] tmp = tipo.split(",");
			for (int i = 0; i < tmp.length; i++) {
				list.add(tmp[i].trim());
			}
		}
		if(PREFIX_DEFAULT.equals(this.prefix) && list.isEmpty()) {
			list.add(ALIAS_DEFAULT);
		}
		this.aliases = list;
	}
	public List<String> getAliases() {
		return this.aliases;
	}
	
	private void initGruppiAliases() throws UtilsException {
		Map<String,List<String>> map = new HashMap<>();
		String nomeP = "aliases.";
		Properties p = null;
		if(this.readerClasspath!=null) {
			p = this.readerClasspath.readProperties(nomeP);
		}
		if(this.reader!=null) {
			if(p==null || p.isEmpty()) {
				p = this.reader.readProperties(nomeP);
			}
			else {
				Properties pConfig = this.reader.readProperties(nomeP);
				if(pConfig!=null && !pConfig.isEmpty()) {
					p.putAll(pConfig); // sovrascrivo
				}
			}
		}
		if(p!=null && !p.isEmpty()) {
			
			List<String> aliasesRegistrati = getAliases();
			
			Enumeration<?> en = p.keys();
			while (en.hasMoreElements()) {
				Object object = en.nextElement();
				if(object instanceof String) {
					String gruppo = (String) object;
					if(map.containsKey(gruppo)) {
						throw new UtilsException("Gruppo '"+gruppo+"' definito più di una volta nella proprietà '"+this.prefix+nomeP+"*'");
					}
					String aliasesGruppo = p.getProperty(gruppo);
					if(aliasesGruppo!=null && !"".equals(aliasesGruppo)){
						String [] tmp = aliasesGruppo.split(",");
						if(tmp!=null && tmp.length>0) {
							List<String> list = new ArrayList<>();
							for (int i = 0; i < tmp.length; i++) {
								String alias = tmp[i].trim();
								if(!aliasesRegistrati.contains(alias)) {
									throw new UtilsException("Alias '"+alias+"' indicato nella proprietà '"+nomeP+""+gruppo+"' non è uno degli alias censiti in '"+this.prefix+"aliases'");
								}
								list.add(alias);
							}
							if(!list.isEmpty()) {
								map.put(gruppo, list);
							}
						}
					}
				}
			}
		}
		this.gruppiAliases = map;
	}
	public Map<String, List<String>> getGruppi_aliases() {
		return this.gruppiAliases;
	}
	
	public void initConfigAliases() throws UtilsException {
		
		this.descrizione = new HashMap<>();
		
		this.tipoAccesso = new HashMap<>();
		
		this.username = new HashMap<>();
		this.password = new HashMap<>();
		
		this.https = new HashMap<>();
		this.httpsVerificaHostName = new HashMap<>();
		this.httpsAutenticazioneServer = new HashMap<>();
		this.httpsAutenticazioneServerTruststorePath = new HashMap<>();
		this.httpsAutenticazioneServerTruststoreType = new HashMap<>();
		this.httpsAutenticazioneServerTruststorePassword = new HashMap<>();
		
		this.connectionTimeout = new HashMap<>();
		this.readConnectionTimeout = new HashMap<>();
		this.readConnectionTimeoutSlowOperation = new HashMap<>();
		
		this.as = new HashMap<>();
		this.factory = new HashMap<>();
		
		this.resourceUrl = new HashMap<>();
		this.checkStatusUrl = new HashMap<>();
		
		this.dominio = new HashMap<>();
		
		if(this.aliases!=null && !this.aliases.isEmpty()) {
			for (String alias : this.aliases) {
				
				String descr = this.readProperty(false, alias+".descrizione");
				if(descr!=null) {
					this.descrizione.put(alias, descr);
				}
				
				boolean tipoAccessoRequired = PREFIX_DEFAULT.equals(this.prefix) ? false : true;
				String tipoAccessoCfg = getValueEngine(tipoAccessoRequired, alias, "tipoAccesso"); 
				if(tipoAccessoCfg==null) {
					tipoAccessoCfg = RESOURCE_TIPOLOGIA_ACCESSO_JMX;
				}
				if(RESOURCE_TIPOLOGIA_ACCESSO_GOVWAY.equals(tipoAccessoCfg)) {
					tipoAccessoCfg = RESOURCE_TIPOLOGIA_ACCESSO_OPENSPCOOP;
				}
				else {
					if(!RESOURCE_TIPOLOGIA_ACCESSO_JMX.equals(tipoAccessoCfg) && !RESOURCE_TIPOLOGIA_ACCESSO_OPENSPCOOP.equals(tipoAccessoCfg)){
						throw new UtilsException("Tipo di accesso ["+tipoAccessoCfg+"] non supportato per la proprietà '"+this.prefix+"tipoAccesso'");
					}
				}
				this.tipoAccesso.put(alias, tipoAccessoCfg);
				
				String user =getValueEngine(false, alias, "remoteAccess.username");
				if(user!=null) {
					this.username.put(alias, user);
				}
				String pwd =getValueEngine(false, alias, "remoteAccess.password");
				if(pwd!=null) {
					this.password.put(alias, pwd);
				}
				
				String v = getValueEngine(false, alias, "remoteAccess.https");
				boolean httpsEnabled = v!=null ? Boolean.valueOf(v.trim()) : false; // default false
				this.https.put(alias, httpsEnabled);
				
				v = getValueEngine(false, alias, "remoteAccess.https.verificaHostName");
				boolean httpsVerificaHostNameEnabled = v!=null ? Boolean.valueOf(v.trim()) : true; // default true
				this.httpsVerificaHostName.put(alias, httpsVerificaHostNameEnabled);

				v =  getValueEngine(false, alias, "remoteAccess.https.autenticazioneServer");
				boolean httpsAutenticazioneServerEnabled = v!=null ? Boolean.valueOf(v.trim()) : true; // default true
				this.httpsAutenticazioneServer.put(alias, httpsAutenticazioneServerEnabled);
				
				v = getValueEngine(false, alias, "remoteAccess.https.autenticazioneServer.truststorePath");
				if(v!=null) {
					this.httpsAutenticazioneServerTruststorePath.put(alias, v);
				}
				
				v = getValueEngine(false, alias, "remoteAccess.https.autenticazioneServer.truststoreType");
				if(v!=null) {
					this.httpsAutenticazioneServerTruststoreType.put(alias, v);
				}
				
				v = getValueEngine(false, alias, "remoteAccess.https.autenticazioneServer.truststorePassword");
				if(v!=null) {
					this.httpsAutenticazioneServerTruststorePassword.put(alias, v);
				}
				
				
				v = getValueEngine(false, alias, "remoteAccess.connectionTimeout");
				if(v!=null) {
					this.connectionTimeout.put(alias, v);
				}
				
				v = getValueEngine(false, alias, "remoteAccess.readConnectionTimeout");
				if(v!=null) {
					this.readConnectionTimeout.put(alias, v);
				}
				
				v = getValueEngine(false, alias, "remoteAccess.readConnectionTimeout.slowOperation");
				if(v!=null) {
					this.readConnectionTimeoutSlowOperation.put(alias, v);
				}
				
				v = getValueEngine(false, alias, "remoteAccess.as");
				if(v!=null) {
					this.as.put(alias, v);
				}
				
				v = getValueEngine(false, alias, "remoteAccess.factory");
				if(v!=null) {
					this.factory.put(alias, v);
				}
				
				v = getValueEngine(false, alias, "remoteAccess.url");
				if(v!=null) {
					this.resourceUrl.put(alias, v);
				}
				
				v = getValueEngine(false, alias, "remoteAccess.checkStatus.url");
				if(v!=null) {
					this.checkStatusUrl.put(alias, v);
				}
				
				v = getValueEngine(!PREFIX_DEFAULT.equals(this.prefix), alias, "dominio");
				if(v!=null) {
					this.dominio.put(alias, v);
				}
				else {
					this.dominio.put(alias, org.openspcoop2.utils.jmx.CostantiJMX.JMX_DOMINIO);
				}
			}
		}
	}
	
	public String getDescrizione(String alias) {
		return this.descrizione.get(alias);
	}
	
	public String getTipoAccesso(String alias)  {
		return this.tipoAccesso.get(alias);
	}
	
	public String getUsername(String alias) {
		return this.username.get(alias);
	}
	public String getPassword(String alias) {
		return this.password.get(alias);
	}
	
	public boolean isHttps(String alias) {
		return this.https.get(alias);
	}
	public boolean isHttps_verificaHostName(String alias) {
		return this.httpsVerificaHostName.get(alias);
	}
	public boolean isHttps_autenticazioneServer(String alias) {
		return this.httpsAutenticazioneServer.get(alias);
	}
	public String getHttps_autenticazioneServer_truststorePath(String alias) {
		return this.httpsAutenticazioneServerTruststorePath.get(alias);
	}
	public String getHttps_autenticazioneServer_truststoreType(String alias) {
		return this.httpsAutenticazioneServerTruststoreType.get(alias);
	}
	public String getHttps_autenticazioneServer_truststorePassword(String alias) {
		return this.httpsAutenticazioneServerTruststorePassword.get(alias);
	}
	
	public String getConnectionTimeout(String alias) {
		return this.connectionTimeout.get(alias);
	}
	public String getReadConnectionTimeout(String alias) {
		return this.readConnectionTimeout.get(alias);
	}
	public String getReadConnectionTimeout_slowOperation(String alias) {
		return this.readConnectionTimeoutSlowOperation.get(alias);
	}
	
	public String getAs(String alias) {
		return this.as.get(alias);
	}
	
	public String getFactory(String alias) {
		return this.factory.get(alias);
	}
	
	public void addForceResourceUrl(String alias, String url) {
		this.forceResourceUrl.put(alias, url);
	}
	public String getResourceUrl(String alias) {
		if(!this.forceResourceUrl.isEmpty() && this.forceResourceUrl.containsKey(alias)) {
			return this.forceResourceUrl.get(alias);
		}
		return this.resourceUrl.get(alias);
	}
	
	public String getCheckStatusUrl(String alias) {
		return this.checkStatusUrl.get(alias);
	}
	
	public String getDominio(String alias) {
		return this.dominio.get(alias);
	}
	
	
	private String getValueEngine(boolean required, String alias, String prop) throws UtilsException{
		String tmp = this.readProperty(false, alias+"."+prop);
		if(tmp==null || "".equals(tmp)){
			tmp = this.readProperty(required, prop);
		}
		return tmp;
	}
	private String readProperty(boolean required,String property) throws UtilsException{
		String tmp = null;
		if(this.reader!=null) {
			tmp = this.reader.getValue_convertEnvProperties(property);
		}
		if(tmp==null &&
			this.readerClasspath!=null) {
			tmp = this.readerClasspath.getValue_convertEnvProperties(property);
		}
		if(tmp==null){
			if(required){
				throw new UtilsException("Property ["+this.prefix+property+"] not found");
			}
			else{
				return null;
			}
		}else{
			return tmp.trim();
		}
	}
	@SuppressWarnings("unused")
	private BooleanNullable readBooleanProperty(boolean required,String property) throws UtilsException{
		String tmp = this.readProperty(required, property);
		if(tmp==null && !required) {
			return BooleanNullable.NULL(); // se e' required viene sollevata una eccezione dal metodo readProperty
		}
		if(!"true".equalsIgnoreCase(tmp) && !"false".equalsIgnoreCase(tmp)){
			throw new UtilsException("Property ["+this.prefix+property+"] with uncorrect value ["+tmp+"] (true/value expected)");
		}
		return Boolean.parseBoolean(tmp) ? BooleanNullable.TRUE() : BooleanNullable.FALSE();
	}
	@SuppressWarnings("unused")
	private Integer readIntegerProperty(boolean required,String property) throws UtilsException{
		String tmp = this.readProperty(required, property);
		if(tmp==null && !required) {
			return null; // se e' required viene sollevata una eccezione dal metodo readProperty
		}
		try{
			return Integer.parseInt(tmp);
		}catch(Exception e){
			throw new UtilsException("Property ["+this.prefix+property+"] with uncorrect value ["+tmp+"] (int value expected)");
		}
	}
	@SuppressWarnings("unused")
	private Long readLongProperty(boolean required,String property) throws UtilsException{
		String tmp = this.readProperty(required, property);
		if(tmp==null && !required) {
			return null; // se e' required viene sollevata una eccezione dal metodo readProperty
		}
		try{
			return Long.parseLong(tmp);
		}catch(Exception e){
			throw new UtilsException("Property ["+this.prefix+property+"] with uncorrect value ["+tmp+"] (long value expected)");
		}
	}
	
	
	public void initBYOKDynamicMapRemoteGovWayNode(Logger log, Map<String, Object> dynamicMap, boolean wrap, boolean unwrap, ConfigurazioneNodiRuntimeBYOKRemoteConfig remoteConfig) {
		if(this.aliases!=null && !this.aliases.isEmpty()){
			// prendo il primo nodo funzionante
			for (String alias : this.aliases) {
				if(isActiveNode(log, alias, remoteConfig) &&
					this.getResourceUrl(alias)!=null && !"".equals(this.getResourceUrl(alias))
					&& !InvokerNodiRuntime.RESOURCE_URL_LOCALE.equals(this.getResourceUrl(alias))
						){
					initBYOKDynamicMapRemoteGovWayNode(dynamicMap, wrap, unwrap, alias, remoteConfig);
					break;
				}
			}
		}
	}
	public boolean isAtLeastOneActiveNode(Logger log, ConfigurazioneNodiRuntimeBYOKRemoteConfig remoteConfig) {
		if(this.aliases!=null && !this.aliases.isEmpty()){
			// prendo il primo nodo funzionante
			for (String alias : this.aliases) {
				if(isActiveNode(log, alias, remoteConfig)){
					return true;
				}
			}
		}
		return false;
	}
	public boolean isActiveNode(Logger log, String alias, ConfigurazioneNodiRuntimeBYOKRemoteConfig remoteConfig) {
		try {
			InvokerNodiRuntime invoker = new InvokerNodiRuntime(null,this); // passo volutamente null per non registrare l'errore
			String value = invoker.readJMXAttribute(alias, remoteConfig.getType(), 
					remoteConfig.getResourceStatoServiziPdd(), 
					remoteConfig.getAttributeComponentePD());
			return value!=null && !"".equals(value) && !value.startsWith(InvokerNodiRuntime.PREFIX_HTTP_CODE) && 
					(CostantiConfigurazione.ABILITATO.getValue().equals(value) || CostantiConfigurazione.DISABILITATO.getValue().equals(value));
		}catch(Exception e) {
			log.debug("Nodo '"+alias+"' non non attivo?: "+e.getMessage(),e);
			return false;
		}
	}

	private void initBYOKDynamicMapRemoteGovWayNode(Map<String, Object> dynamicMap, boolean wrap, boolean unwrap, String alias, ConfigurazioneNodiRuntimeBYOKRemoteConfig remoteConfig)  {
		String remoteUrl = this.getResourceUrl(alias);
		
		Map<String, String> govwayContext = new HashMap<>();
		dynamicMap.put(BYOKCostanti.GOVWAY_RUNTIME_CONTEXT, govwayContext);
		
		Map<String, List<String>> p = new HashMap<>();
		TransportUtils.setParameter(p,CostantiPdD.CHECK_STATO_PDD_RESOURCE_NAME, remoteConfig.getResourceConfigurazioneSistema());
		if(wrap) {
			TransportUtils.setParameter(p,CostantiPdD.CHECK_STATO_PDD_METHOD_NAME, remoteConfig.getMethodWrap());
			String urlWrap = TransportUtils.buildUrlWithParameters(p, remoteUrl);
			govwayContext.put(BYOKCostanti.GOVWAY_RUNTIME_ENDPOINT_WRAP, urlWrap);
		}
		if(unwrap) {
			TransportUtils.setParameter(p,CostantiPdD.CHECK_STATO_PDD_METHOD_NAME, remoteConfig.getMethodUnwrap());
			String urlUnwrap = TransportUtils.buildUrlWithParameters(p, remoteUrl);
			govwayContext.put(BYOKCostanti.GOVWAY_RUNTIME_ENDPOINT_UNWRAP, urlUnwrap);
		}

		String user = this.getUsername(alias);
		if(user!=null) {
			govwayContext.put(BYOKCostanti.GOVWAY_RUNTIME_USERNAME, user);
		}
		String pwd = this.getPassword(alias);
		if(pwd!=null) {
			govwayContext.put(BYOKCostanti.GOVWAY_RUNTIME_PASSWORD, pwd);
		}
	}
}
