/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.core.config.rs.server.config;

import java.util.Enumeration;
import java.util.Properties;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jaxrs.impl.AuthorizationConfig;
import org.slf4j.Logger;

/**     
 * ServerProperties
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServerProperties  {

	/** Logger utilizzato per errori eventuali. */
	private Logger log = null;
	
	/** Reader delle proprieta' impostate nel file 'rs-api-config.properties' */
	private ServerInstanceProperties reader;
	
	/** Copia Statica */
	private static ServerProperties serverProperties = null;
	
	
	public ServerProperties(String confDir,Logger log) throws Exception {

		if(log!=null)
			this.log = log;
		else
			this.log = LoggerWrapperFactory.getLogger(ServerProperties.class);
		
		/* ---- Lettura del cammino del file di configurazione ---- */
		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = DatasourceProperties.class.getResourceAsStream("/rs-api-config.properties");
			if(properties==null){
				throw new Exception("File '/rs-api-config.properties' not found");
			}
			propertiesReader.load(properties);
		}catch(Exception e) {
			this.log.error("Riscontrato errore durante la lettura del file 'rs-api-config.properties': \n\n"+e.getMessage());
		    throw new Exception("RS Api ConfigProperties initialize error: "+e.getMessage());
		}finally{
		    try{
				if(properties!=null)
				    properties.close();
		    }catch(Exception er){}
		}

		this.reader = new ServerInstanceProperties(propertiesReader, this.log, confDir);
	}


	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static boolean initialize(String confDir,Logger log){

		try {
		    ServerProperties.serverProperties = new ServerProperties(confDir,log);	
		    return true;
		}
		catch(Exception e) {
			log.error("Errore durante l'inizializzazione del ServerProperties: "+e.getMessage(),e);		   
		    return false;
		}
	}
    
	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di Properties
	 * 
	 */
	public static ServerProperties getInstance() throws UtilsException{
		if(ServerProperties.serverProperties==null){
	    	throw new UtilsException("ServerProperties non inizializzato");
	    }
	    return ServerProperties.serverProperties;
	}
    
	public static void updateLocalImplementation(Properties prop){
		ServerProperties.serverProperties.reader.setLocalObjectImplementation(prop);
	}








	/* ********  M E T O D I  ******** */

	public String readProperty(boolean required,String property) throws UtilsException{
		String tmp = this.reader.getValue_convertEnvProperties(property);
		if(tmp==null){
			if(required){
				throw new UtilsException("Property ["+property+"] not found");
			}
			else{
				return null;
			}
		}else{
			return tmp.trim();
		}
	}
	
	public Properties getProperties() throws UtilsException{
		Properties p = new Properties();
		Enumeration<?> names = this.reader.propertyNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			p.put(name, this.reader.getValue_convertEnvProperties(name));
		}
		return p;
	}
	
	private AuthorizationConfig authConfig = null;
	private synchronized void initAuthorizationConfig() throws UtilsException {
		if(this.authConfig==null) {
			this.authConfig = new AuthorizationConfig(getProperties());
		}
	}
	public AuthorizationConfig getAuthorizationConfig() throws UtilsException {
		if(this.authConfig==null) {
			this.initAuthorizationConfig();
		}
		return this.authConfig;
	}

	
	public String getConfDirectory() throws UtilsException {
		return this.readProperty(false, "confDirectory");
	}
	public String getProtocolloDefault() throws UtilsException {
		return this.readProperty(true, "protocolloDefault");
	}
	
	
	public boolean isMultitenant() throws UtilsException {
		return Boolean.parseBoolean(this.readProperty(true, "multitenant"));
	}
	
	
	public boolean isEnabledAutoMapping() throws UtilsException {
		return Boolean.parseBoolean(this.readProperty(true, "enableAutoMapping"));
	}
	
	public boolean isEnabledAutoMappingEstraiXsdSchemiFromWsdlTypes() throws UtilsException {
		return Boolean.parseBoolean(this.readProperty(true, "enableAutoMapping_estraiXsdSchemiFromWsdlTypes"));
	}
	
	public boolean isValidazioneDocumenti() throws UtilsException {
		return Boolean.parseBoolean(this.readProperty(true, "validazioneDocumenti"));
	}
	
	
	
	
	
	public String getSoggettoDefault(String protocollo) throws UtilsException {
		String p = this.readProperty(false, protocollo+".soggetto");
		if(p!=null) {
			return p;
		}
		return this.readProperty(true, "soggetto");
	}

}