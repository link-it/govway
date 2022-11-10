/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.properties.PropertiesReader;

/**
 * ConfigurazioneNodiRuntime
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneNodiRuntime {

	public final static String RESOURCE_NAME = "govway.nodirun.properties";
	
	public final static String RESOURCE_TIPOLOGIA_ACCESSO_JMX = "jmx";
	public final static String RESOURCE_TIPOLOGIA_ACCESSO_OPENSPCOOP = "openspcoop";
	public final static String RESOURCE_TIPOLOGIA_ACCESSO_GOVWAY = "govway";
	
	public final static String ALIAS_DEFAULT = "pdd";
	
	private static Map<String, ConfigurazioneNodiRuntime> staticInstanceMap = new HashMap<String, ConfigurazioneNodiRuntime>();
	private static final String PREFIX_DEFAULT = "";
	public static void initialize(String path, ConfigurazioneNodiRuntimeProperties ... backwardCompatibilitiesProperties) throws Exception {
		
		ConfigurazioneNodiRuntimeProperties cpClasspath = null;
		try(InputStream is = ConfigurazioneNodiRuntime.class.getResourceAsStream("/"+RESOURCE_NAME);){
			if(is!=null) {
				Properties p = new Properties();
				p.load(is);
				cpClasspath = new ConfigurazioneNodiRuntimeProperties(PREFIX_DEFAULT, p);
			}
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
					throw new Exception("Configuration file '"+f.getAbsolutePath()+"' not exists");
				}
				if(!f.canRead()) {
					throw new Exception("Configuration file '"+f.getAbsolutePath()+"' cannot read");
				}
				read = true;
			}
			if(read) {
				try(FileInputStream fin = new FileInputStream(f)){
					Properties p = new Properties();
					p.load(fin);
					cpFile = new ConfigurazioneNodiRuntimeProperties(PREFIX_DEFAULT, p);
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
		if(staticInstanceMap!=null && !staticInstanceMap.isEmpty()) {
			List<String> l = new ArrayList<String>();
			l.addAll(staticInstanceMap.keySet());
			return l;
		}
		return null;
	}
	
	private PropertiesReader reader;
	private PropertiesReader readerClasspath;
	private String prefix = PREFIX_DEFAULT;
	
	private boolean clusterDinamico = false;
	
	private List<String> aliases;
	
	private Map<String,List<String>> gruppi_aliases;
	
	private Map<String, String> descrizione;
	
	private Map<String, String> tipoAccesso;
	
	private Map<String, String> username;
	private Map<String, String> password;
	
	private Map<String, Boolean> https;
	private Map<String, Boolean> https_verificaHostName;
	private Map<String, Boolean> https_autenticazioneServer;
	private Map<String, String> https_autenticazioneServer_truststorePath;
	private Map<String, String> https_autenticazioneServer_truststoreType;
	private Map<String, String> https_autenticazioneServer_truststorePassword;
	
	private Map<String, String> connectionTimeout;
	private Map<String, String> readConnectionTimeout;
	private Map<String, String> readConnectionTimeout_slowOperation;
	
	private Map<String, String> as;
	private Map<String, String> factory;
	
	private Map<String, String> resourceUrl; // check con parametri o proxy in modalità dinamica
	private Map<String, String> forceResourceUrl = new HashMap<String, String>();
	private Map<String, String> checkStatusUrl; // check senza parametri
	
	private Map<String, String> dominio;
	
	private ConfigurazioneNodiRuntime(ConfigurazioneNodiRuntimeProperties config, ConfigurazioneNodiRuntimeProperties configClasspath) throws Exception {
		if(config!=null) {
			this.reader = new PropertiesReader(config.getProperties(), false);
			this.prefix = config.getPrefix();
		}
		if(configClasspath!=null) {
			this.readerClasspath = new PropertiesReader(configClasspath.getProperties(), false);
			this.prefix = configClasspath.getPrefix();
		}
		if(this.reader==null && this.readerClasspath==null) {
			throw new Exception("Nessuna configurazione fornita");
		}
		
		this.initAliases();
		this.initGruppi_aliases();
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
		List<String> list = new ArrayList<String>();
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
	
	public void initGruppi_aliases() throws UtilsException {
		Map<String,List<String>> map = new HashMap<String, List<String>>();
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
				Object object = (Object) en.nextElement();
				if(object instanceof String) {
					String gruppo = (String) object;
					if(map.containsKey(gruppo)) {
						throw new UtilsException("Gruppo '"+gruppo+"' definito più di una volta nella proprietà '"+this.prefix+nomeP+"*'");
					}
					String aliases = p.getProperty(gruppo);
					if(aliases!=null && !"".equals(aliases)){
						String [] tmp = aliases.split(",");
						if(tmp!=null && tmp.length>0) {
							List<String> list = new ArrayList<String>();
							for (int i = 0; i < tmp.length; i++) {
								String alias = tmp[i].trim();
								if(aliasesRegistrati.contains(alias)==false) {
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
		this.gruppi_aliases = map;
	}
	public Map<String, List<String>> getGruppi_aliases() {
		return this.gruppi_aliases;
	}
	
	public void initConfigAliases() throws UtilsException {
		
		this.descrizione = new HashMap<String, String>();
		
		this.tipoAccesso = new HashMap<String, String>();
		
		this.username = new HashMap<String, String>();
		this.password = new HashMap<String, String>();
		
		this.https = new HashMap<String, Boolean>();
		this.https_verificaHostName = new HashMap<String, Boolean>();
		this.https_autenticazioneServer = new HashMap<String, Boolean>();
		this.https_autenticazioneServer_truststorePath = new HashMap<String, String>();
		this.https_autenticazioneServer_truststoreType = new HashMap<String, String>();
		this.https_autenticazioneServer_truststorePassword = new HashMap<String, String>();
		
		this.connectionTimeout = new HashMap<String, String>();
		this.readConnectionTimeout = new HashMap<String, String>();
		this.readConnectionTimeout_slowOperation = new HashMap<String, String>();
		
		this.as = new HashMap<String, String>();
		this.factory = new HashMap<String, String>();
		
		this.resourceUrl = new HashMap<String, String>();
		this.checkStatusUrl = new HashMap<String, String>();
		
		this.dominio = new HashMap<String, String>();
		
		if(this.aliases!=null && !this.aliases.isEmpty()) {
			for (String alias : this.aliases) {
				
				String descrizione = this.readProperty(false, alias+".descrizione");
				if(descrizione!=null) {
					this.descrizione.put(alias, descrizione);
				}
				
				boolean tipoAccessoRequired = PREFIX_DEFAULT.equals(this.prefix) ? false : true;
				String tipoAccesso = _getValue(tipoAccessoRequired, alias, "tipoAccesso"); 
				if(tipoAccesso==null) {
					tipoAccesso = RESOURCE_TIPOLOGIA_ACCESSO_JMX;
				}
				if(RESOURCE_TIPOLOGIA_ACCESSO_GOVWAY.equals(tipoAccesso)) {
					tipoAccesso = RESOURCE_TIPOLOGIA_ACCESSO_OPENSPCOOP;
				}
				else {
					if(!RESOURCE_TIPOLOGIA_ACCESSO_JMX.equals(tipoAccesso) && !RESOURCE_TIPOLOGIA_ACCESSO_OPENSPCOOP.equals(tipoAccesso)){
						throw new UtilsException("Tipo di accesso ["+tipoAccesso+"] non supportato per la proprietà '"+this.prefix+"tipoAccesso'");
					}
				}
				this.tipoAccesso.put(alias, tipoAccesso);
				
				String username =_getValue(false, alias, "remoteAccess.username");
				if(username!=null) {
					this.username.put(alias, username);
				}
				String password =_getValue(false, alias, "remoteAccess.password");
				if(password!=null) {
					this.password.put(alias, password);
				}
				
				String v = _getValue(false, alias, "remoteAccess.https");
				boolean https = v!=null ? Boolean.valueOf(v.trim()) : false; // default false
				this.https.put(alias, https);
				
				v = _getValue(false, alias, "remoteAccess.https.verificaHostName");
				boolean https_verificaHostName = v!=null ? Boolean.valueOf(v.trim()) : true; // default true
				this.https_verificaHostName.put(alias, https_verificaHostName);

				v =  _getValue(false, alias, "remoteAccess.https.autenticazioneServer");
				boolean https_autenticazioneServer = v!=null ? Boolean.valueOf(v.trim()) : true; // default true
				this.https_autenticazioneServer.put(alias, https_autenticazioneServer);
				
				v = _getValue(false, alias, "remoteAccess.https.autenticazioneServer.truststorePath");
				if(v!=null) {
					this.https_autenticazioneServer_truststorePath.put(alias, v);
				}
				
				v = _getValue(false, alias, "remoteAccess.https.autenticazioneServer.truststoreType");
				if(v!=null) {
					this.https_autenticazioneServer_truststoreType.put(alias, v);
				}
				
				v = _getValue(false, alias, "remoteAccess.https.autenticazioneServer.truststorePassword");
				if(v!=null) {
					this.https_autenticazioneServer_truststorePassword.put(alias, v);
				}
				
				
				v = _getValue(false, alias, "remoteAccess.connectionTimeout");
				if(v!=null) {
					this.connectionTimeout.put(alias, v);
				}
				
				v = _getValue(false, alias, "remoteAccess.readConnectionTimeout");
				if(v!=null) {
					this.readConnectionTimeout.put(alias, v);
				}
				
				v = _getValue(false, alias, "remoteAccess.readConnectionTimeout.slowOperation");
				if(v!=null) {
					this.readConnectionTimeout_slowOperation.put(alias, v);
				}
				
				v = _getValue(false, alias, "remoteAccess.as");
				if(v!=null) {
					this.as.put(alias, v);
				}
				
				v = _getValue(false, alias, "remoteAccess.factory");
				if(v!=null) {
					this.factory.put(alias, v);
				}
				
				v = _getValue(false, alias, "remoteAccess.url");
				if(v!=null) {
					this.resourceUrl.put(alias, v);
				}
				
				v = _getValue(false, alias, "remoteAccess.checkStatus.url");
				if(v!=null) {
					this.checkStatusUrl.put(alias, v);
				}
				
				v = _getValue(!PREFIX_DEFAULT.equals(this.prefix), alias, "dominio");
				if(v!=null) {
					this.dominio.put(alias, v);
				}
				else {
					this.dominio.put(alias, org.openspcoop2.utils.jmx.CostantiJMX.JMX_DOMINIO);
				}
			}
		}
	}
	
	public String getDescrizione(String alias) throws UtilsException {
		return this.descrizione.get(alias);
	}
	
	public String getTipoAccesso(String alias) throws UtilsException {
		return this.tipoAccesso.get(alias);
	}
	
	public String getUsername(String alias) throws UtilsException {
		return this.username.get(alias);
	}
	public String getPassword(String alias) throws UtilsException {
		return this.password.get(alias);
	}
	
	public boolean isHttps(String alias) throws UtilsException {
		return this.https.get(alias);
	}
	public boolean isHttps_verificaHostName(String alias) throws UtilsException {
		return this.https_verificaHostName.get(alias);
	}
	public boolean isHttps_autenticazioneServer(String alias) throws UtilsException {
		return this.https_autenticazioneServer.get(alias);
	}
	public String getHttps_autenticazioneServer_truststorePath(String alias) throws UtilsException {
		return this.https_autenticazioneServer_truststorePath.get(alias);
	}
	public String getHttps_autenticazioneServer_truststoreType(String alias) throws UtilsException {
		return this.https_autenticazioneServer_truststoreType.get(alias);
	}
	public String getHttps_autenticazioneServer_truststorePassword(String alias) throws UtilsException {
		return this.https_autenticazioneServer_truststorePassword.get(alias);
	}
	
	public String getConnectionTimeout(String alias) throws UtilsException {
		return this.connectionTimeout.get(alias);
	}
	public String getReadConnectionTimeout(String alias) throws UtilsException {
		return this.readConnectionTimeout.get(alias);
	}
	public String getReadConnectionTimeout_slowOperation(String alias) throws UtilsException {
		return this.readConnectionTimeout_slowOperation.get(alias);
	}
	
	public String getAs(String alias) throws UtilsException {
		return this.as.get(alias);
	}
	
	public String getFactory(String alias) throws UtilsException {
		return this.factory.get(alias);
	}
	
	public void addForceResourceUrl(String alias, String url) {
		this.forceResourceUrl.put(alias, url);
	}
	public String getResourceUrl(String alias) throws UtilsException {
		if(!this.forceResourceUrl.isEmpty() && this.forceResourceUrl.containsKey(alias)) {
			return this.forceResourceUrl.get(alias);
		}
		return this.resourceUrl.get(alias);
	}
	
	public String getCheckStatusUrl(String alias) throws UtilsException {
		return this.checkStatusUrl.get(alias);
	}
	
	public String getDominio(String alias) throws UtilsException {
		return this.dominio.get(alias);
	}
	
	
	private String _getValue(boolean required, String alias, String prop) throws UtilsException{
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
		if(tmp==null){
			if(this.readerClasspath!=null) {
				tmp = this.readerClasspath.getValue_convertEnvProperties(property);
			}
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
		if("true".equalsIgnoreCase(tmp)==false && "false".equalsIgnoreCase(tmp)==false){
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
}
