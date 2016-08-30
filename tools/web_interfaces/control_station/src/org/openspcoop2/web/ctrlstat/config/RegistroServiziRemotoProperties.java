/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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



package org.openspcoop2.web.ctrlstat.config;


import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.pdd.config.OpenSPCoop2ConfigurationException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;


/**
* ConsoleProperties
*
* @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
*/


public class RegistroServiziRemotoProperties {	

	/** Logger utilizzato per errori eventuali. */
	private Logger log = null;



	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'console.registroServiziRemoto.properties' */
	private RegistroServiziRemotoInstanceProperties reader;

	/** Copia Statica */
	private static RegistroServiziRemotoProperties registroServiziRemotoProperties = null;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	public RegistroServiziRemotoProperties(String confDir, String confPropertyName, String confLocalPathPrefix,Logger log) throws Exception {

		if(log!=null)
			this.log = log;
		else
			this.log = LoggerWrapperFactory.getLogger(RegistroServiziRemotoProperties.class);
		
		/* ---- Lettura del cammino del file di configurazione ---- */
		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = RegistroServiziRemotoProperties.class.getResourceAsStream("/console.registroServiziRemoto.properties");
			if(properties==null){
				throw new Exception("File '/console.registroServiziRemoto.properties' not found");
			}
			propertiesReader.load(properties);
		}catch(Exception e) {
			this.log.error("Riscontrato errore durante la lettura del file 'console.registroServiziRemoto.properties': \n\n"+e.getMessage());
		    throw new Exception("ConsoleProperties initialize error: "+e.getMessage());
		}finally{
		    try{
				if(properties!=null)
				    properties.close();
		    }catch(Exception er){}
		}

		this.reader = new RegistroServiziRemotoInstanceProperties(propertiesReader, this.log, confDir,confPropertyName,confLocalPathPrefix);
	}


	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static boolean initialize(String confDir, String confPropertyName, String confLocalPathPrefix,Logger log){

		try {
		    RegistroServiziRemotoProperties.registroServiziRemotoProperties = new RegistroServiziRemotoProperties(confDir,confPropertyName,confLocalPathPrefix,log);	
		    return true;
		}
		catch(Exception e) {
			log.error("Inizializzazione fallita: "+e.getMessage(),e);
		    return false;
		}
	}
    
	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di ClassNameProperties
	 * 
	 */
	public static RegistroServiziRemotoProperties getInstance() throws OpenSPCoop2ConfigurationException{
		if(RegistroServiziRemotoProperties.registroServiziRemotoProperties==null){
	    	throw new OpenSPCoop2ConfigurationException("DatasourceProperties non inizializzato");
	    }
	    return RegistroServiziRemotoProperties.registroServiziRemotoProperties;
	}
    
	public static void updateLocalImplementation(Properties prop){
		RegistroServiziRemotoProperties.registroServiziRemotoProperties.reader.setLocalObjectImplementation(prop);
	}








	/* ********  M E T O D I  ******** */

	private String readProperty(boolean required,String property) throws UtilsException{
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

	
	
	public String getTipoRegistroServiziRemoto() throws UtilsException{
		return this.readProperty(true, "registroServizi.tipo");
	}

	
	/* ----- RegistroServiziXML -------- */
	
	public String getRegistroServiziXML_Location() throws UtilsException{
		return this.readProperty(CostantiConfigurazione.REGISTRO_XML.equals(this.getTipoRegistroServiziRemoto()), "registroServizi.xml.location");
	}
	
	
	
	/* ----- RegistroServiziWS -------- */
	
	public String getRegistroServiziWS_Location() throws UtilsException{
		return this.readProperty(CostantiConfigurazione.REGISTRO_WS.equals(this.getTipoRegistroServiziRemoto()), "registroServizi.ws.location");
	}
	
	public String getRegistroServiziWS_Username() throws UtilsException{
		return this.readProperty(false, "registroServizi.ws.username");
	}
	
	public String getRegistroServiziWS_Password() throws UtilsException{
		return this.readProperty(false, "registroServizi.ws.password");
	}
	
	
	
	/* ----- RegistroServiziDB -------- */
	
	public String getRegistroServiziDB_DataSource() throws UtilsException{
		return this.readProperty(CostantiConfigurazione.REGISTRO_DB.equals(this.getTipoRegistroServiziRemoto()), "registroServizi.db.dataSource");
	}
	
	public Properties getRegistroServiziDB_DataSourceContext() throws UtilsException{
		return this.reader.readProperties_convertEnvProperties("registroServizi.db.context.");
	}
	
	public String getRegistroServiziDB_TipoDatabase() throws UtilsException{
		return this.readProperty(CostantiConfigurazione.REGISTRO_DB.equals(this.getTipoRegistroServiziRemoto()), "registroServizi.db.tipo");
	}
	
	
	/* ----- RegistroServiziUDDI -------- */
	
	public String getRegistroServiziUDDI_InquiryURL() throws UtilsException{
		return this.readProperty(CostantiConfigurazione.REGISTRO_UDDI.equals(this.getTipoRegistroServiziRemoto()), "registroServizi.uddi.inquiryUrl");
	}
	
	public String getRegistroServiziUDDI_Username() throws UtilsException{
		return this.readProperty(false, "registroServizi.uddi.username");
	}
	
	public String getRegistroServiziUDDI_Password() throws UtilsException{
		return this.readProperty(false, "registroServizi.uddi.password");
	}
	
	
	/* ----- RegistroServiziWEB -------- */
	
	public String getRegistroServiziWEB_URLPrefix() throws UtilsException{
		return this.readProperty(CostantiConfigurazione.REGISTRO_UDDI.equals(this.getTipoRegistroServiziRemoto()), "registroServizi.web.urlPrefix");
	}

}
