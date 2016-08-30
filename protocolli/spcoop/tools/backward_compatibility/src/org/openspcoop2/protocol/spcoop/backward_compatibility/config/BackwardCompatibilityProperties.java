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



package org.openspcoop2.protocol.spcoop.backward_compatibility.config;


import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.pdd.config.OpenSPCoop2ConfigurationException;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.utils.LoggerWrapperFactory;


/**
* BackwardCompatibilityProperties
*
* @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
*/


public class BackwardCompatibilityProperties {	

	/** Logger utilizzato per errori eventuali. */
	private Logger log = null;



	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'backwardCompatibility.properties' */
	private BackwardCompatibilityInstanceProperties reader;

	/** Copia Statica */
	private static BackwardCompatibilityProperties backwardCompatibilityProperties = null;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	public BackwardCompatibilityProperties(String confDir) throws Exception {

		if(OpenSPCoop2Startup.initialize)
			this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		else
			this.log = LoggerWrapperFactory.getLogger(BackwardCompatibilityProperties.class);
		
		/* ---- Lettura del cammino del file di configurazione ---- */
		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = BackwardCompatibilityProperties.class.getResourceAsStream("/backwardCompatibility.properties");
			if(properties==null){
				throw new Exception("File '/backwardCompatibility.properties' not found");
			}
			propertiesReader.load(properties);
		}catch(Exception e) {
			this.log.error("Riscontrato errore durante la lettura del file 'backwardCompatibility.properties': \n\n"+e.getMessage());
		    throw new Exception("BackwardCompatibilityProperties initialize error: "+e.getMessage());
		}finally{
		    try{
				if(properties!=null)
				    properties.close();
		    }catch(Exception er){}
		}

		this.reader = new BackwardCompatibilityInstanceProperties(propertiesReader, this.log, confDir);
	}


	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static boolean initialize(String confDir){

		try {
		    BackwardCompatibilityProperties.backwardCompatibilityProperties = new BackwardCompatibilityProperties(confDir);	
		    return true;
		}
		catch(Exception e) {
		    return false;
		}
	}
    
	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di ClassNameProperties
	 * 
	 */
	protected static BackwardCompatibilityProperties getInstance() throws OpenSPCoop2ConfigurationException{
		return getInstance(false);
	}
	// Viene inizializzato in modo da permettere di installare sempre il modulo BackwardCompatibilty e per disabiltarlo basta eliminarlo/commentarlo dal META-INF/application.xml
	// Tutte le classi cmq agganciate via openspcoop2.properties vengono utilizzate lo stesso (handler, integrazione ...)
	// Però non arrivando dal canale di backward compatibility sono semplici NOP
	public static BackwardCompatibilityProperties getInstance(boolean inizializeIfNotExists) throws OpenSPCoop2ConfigurationException{
	    if(BackwardCompatibilityProperties.backwardCompatibilityProperties==null){
	    	if(inizializeIfNotExists){
	    		initialize(null);
	    	}else{
	    		throw new OpenSPCoop2ConfigurationException("BackwardCompatibilityProperties non inizializzato");
	    	}
	    }
	    return BackwardCompatibilityProperties.backwardCompatibilityProperties;
	}
    
	public static void updateLocalImplementation(Properties prop){
		BackwardCompatibilityProperties.backwardCompatibilityProperties.reader.setLocalObjectImplementation(prop);
	}








	/* ********  M E T O D I  ******** */
	
	private static Boolean isSwitchOpenSPCoopV2PortaDelegata = null;
	public boolean isSwitchOpenSPCoopV2PortaDelegata(){
		if(BackwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaDelegata==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.backwardCompatibility.switchOpenSPCoopV2.pd"); 
	
				if (value != null){
					value = value.trim();
					BackwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaDelegata = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.backwardCompatibility.switchOpenSPCoopV2.pd' non impostata, viene utilizzato il default=true");
					BackwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaDelegata = true;
				}
	
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.backwardCompatibility.switchOpenSPCoopV2.pd' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				BackwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaDelegata = true;
			}
		}
	
		return BackwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaDelegata;
	}
	
	private static Boolean isSwitchOpenSPCoopV2PortaApplicativa = null;
	public boolean isSwitchOpenSPCoopV2PortaApplicativa(){
		if(BackwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.backwardCompatibility.switchOpenSPCoopV2.pa"); 
	
				if (value != null){
					value = value.trim();
					BackwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.backwardCompatibility.switchOpenSPCoopV2.pa' non impostata, viene utilizzato il default=false");
					BackwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa = false;
				}
	
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.backwardCompatibility.switchOpenSPCoopV2.pa' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				BackwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa = false;
			}
		}
	
		return BackwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa;
	}
	
	
	/**
	 * Restituisce le proprieta' che identificano gli header di integrazione in caso di 'trasporto' 
	 *
	 * @return Restituisce le proprieta' che identificano gli header di integrazione in caso di 'trasporto'
	 *  
	 */
	private static String protocolSPCoopName = null;
	public String getProtocolName() {	
		if(BackwardCompatibilityProperties.protocolSPCoopName==null){

			try{ 

				String tmp = this.reader.getValue_convertEnvProperties("org.openspcoop2.backwardCompatibility.protocol.name");
				if(tmp==null){
					throw new Exception("Proprieta' non definita");
				}
				BackwardCompatibilityProperties.protocolSPCoopName = tmp.trim();

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.backwardCompatibility.protocol.name' (viene usato il default 'spcoop'): "+e.getMessage());
				BackwardCompatibilityProperties.protocolSPCoopName = "spcoop";
			}    
		}

		return BackwardCompatibilityProperties.protocolSPCoopName;
	}
	
	private static String faultActor = null;
	public String getFaultActor() {	
		if(BackwardCompatibilityProperties.faultActor==null){

			try{ 

				String tmp = this.reader.getValue_convertEnvProperties("org.openspcoop2.backwardCompatibility.faultActor");
				if(tmp==null){
					throw new Exception("Proprieta' non definita");
				}
				BackwardCompatibilityProperties.faultActor = tmp.trim();

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.backwardCompatibility.faultActor' (viene usato il default 'OpenSPCoop'): "+e.getMessage());
				BackwardCompatibilityProperties.faultActor = "OpenSPCoop";
			}    
		}

		return BackwardCompatibilityProperties.faultActor;
	}
	
	private static String identificativoPortaDefault = null;
	public String getIdentificativoPortaDefault() {	
		if(BackwardCompatibilityProperties.identificativoPortaDefault==null){

			try{ 

				String tmp = this.reader.getValue_convertEnvProperties("org.openspcoop2.backwardCompatibility.identificativoPortaDefault");
				if(tmp==null){
					throw new Exception("Proprieta' non definita");
				}
				BackwardCompatibilityProperties.identificativoPortaDefault = tmp.trim();

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.backwardCompatibility.identificativoPortaDefault' (viene usato il default 'OpenSPCoopSPCoopIT'): "+e.getMessage());
				BackwardCompatibilityProperties.identificativoPortaDefault = "OpenSPCoopSPCoopIT";
			}    
		}

		return BackwardCompatibilityProperties.identificativoPortaDefault;
	}
	
	private static java.util.Properties readCodeMapping = null;
	public java.util.Properties readCodeMapping() {	
		if(BackwardCompatibilityProperties.readCodeMapping==null){

			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.backwardCompatibility.codeMapping.");
				BackwardCompatibilityProperties.readCodeMapping = prop;

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'oorg.openspcoop2.backwardCompatibility.codeMapping.*': "+e.getMessage());
				BackwardCompatibilityProperties.readCodeMapping = null;
			}    
		}

		return BackwardCompatibilityProperties.readCodeMapping;
	}
	
	private static String getPrefixFaultCode = null;
	public String getPrefixFaultCode() {	
		if(BackwardCompatibilityProperties.getPrefixFaultCode==null){

			try{ 

				String tmp = this.reader.getValue_convertEnvProperties("org.openspcoop2.backwardCompatibility.prefixFaultCode");
				if(tmp==null){
					throw new Exception("Proprieta' non definita");
				}
				BackwardCompatibilityProperties.getPrefixFaultCode = tmp.trim();

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.backwardCompatibility.prefixFaultCode' (viene usato il default 'OPENSPCOOP_ORG_'): "+e.getMessage());
				BackwardCompatibilityProperties.getPrefixFaultCode = "OPENSPCOOP_ORG_";
			}    
		}

		return BackwardCompatibilityProperties.getPrefixFaultCode;
	}

	
	/**
	 * Restituisce le proprieta' che identificano gli header di integrazione in caso di 'trasporto' 
	 *
	 * @return Restituisce le proprieta' che identificano gli header di integrazione in caso di 'trasporto'
	 *  
	 */
	private static java.util.Properties keyValue_HeaderIntegrazioneTrasporto = null;
	public java.util.Properties getKeyValue_HeaderIntegrazioneTrasporto() {	
		if(BackwardCompatibilityProperties.keyValue_HeaderIntegrazioneTrasporto==null){

			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.backwardCompatibility.integrazione.trasporto.keyword.");
				BackwardCompatibilityProperties.keyValue_HeaderIntegrazioneTrasporto = prop;

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.backwardCompatibility.integrazione.trasporto.keyword.*': "+e.getMessage());
				BackwardCompatibilityProperties.keyValue_HeaderIntegrazioneTrasporto = null;
			}    
		}

		return BackwardCompatibilityProperties.keyValue_HeaderIntegrazioneTrasporto;
	}
	
	/**
	 * Restituisce le proprieta' che identificano gli header di integrazione in caso di 'urlBased'.
	 *
	 * @return Restituisce le proprieta' che identificano gli header di integrazione in caso di 'urlBased'.
	 *  
	 */
	private static java.util.Properties keyValue_HeaderIntegrazioneUrlBased = null;
	public java.util.Properties getKeyValue_HeaderIntegrazioneUrlBased() {	
		if(BackwardCompatibilityProperties.keyValue_HeaderIntegrazioneUrlBased==null){

			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.backwardCompatibility.integrazione.urlBased.keyword.");
				BackwardCompatibilityProperties.keyValue_HeaderIntegrazioneUrlBased = prop;

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.backwardCompatibility.integrazione.urlBased.keyword.*': "+e.getMessage());
				BackwardCompatibilityProperties.keyValue_HeaderIntegrazioneUrlBased = null;
			}    
		}

		return BackwardCompatibilityProperties.keyValue_HeaderIntegrazioneUrlBased;
	}
	
	/**
	 * Restituisce le proprieta' che identificano gli header di integrazione in caso di 'soap'.
	 *
	 * @return Restituisce le proprieta' che identificano gli header di integrazione in caso di 'soap'.
	 *  
	 */
	private static java.util.Properties keyValue_HeaderIntegrazioneSoap = null;
	public java.util.Properties getKeyValue_HeaderIntegrazioneSoap() {	
		if(BackwardCompatibilityProperties.keyValue_HeaderIntegrazioneSoap==null){

			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.backwardCompatibility.integrazione.soap.keyword.");
				BackwardCompatibilityProperties.keyValue_HeaderIntegrazioneSoap = prop;

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.backwardCompatibility.integrazione.soap.keyword.*': "+e.getMessage());
				BackwardCompatibilityProperties.keyValue_HeaderIntegrazioneSoap = null;
			}    
		}

		return BackwardCompatibilityProperties.keyValue_HeaderIntegrazioneSoap;
	}
	
	
	/**
	 * Restituisce il nome dell'header Soap di integrazione di default
	 * 
	 * @return Restituisce il nome dell'header Soap di integrazione di default
	 */
	private static String headerSoapNameIntegrazione = null;
	public String getHeaderSoapNameIntegrazione() {
		if(BackwardCompatibilityProperties.headerSoapNameIntegrazione==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.backwardCompatibility.integrazione.soap.headerName");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				BackwardCompatibilityProperties.headerSoapNameIntegrazione = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.backwardCompatibility.integrazione.soap.headerName': "+e.getMessage());
				BackwardCompatibilityProperties.headerSoapNameIntegrazione = null;
			}    
		}

		return BackwardCompatibilityProperties.headerSoapNameIntegrazione;
	}

	/**
	 * Restituisce l'actord dell'header Soap di integrazione di default
	 * 
	 * @return Restituisce l'actor dell'header Soap di integrazione di default
	 */
	private static String headerSoapActorIntegrazione = null;
	public String getHeaderSoapActorIntegrazione() {
		if(BackwardCompatibilityProperties.headerSoapActorIntegrazione==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.backwardCompatibility.integrazione.soap.headerActor");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				BackwardCompatibilityProperties.headerSoapActorIntegrazione = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.backwardCompatibility.integrazione.soap.headerActor': "+e.getMessage());
				BackwardCompatibilityProperties.headerSoapActorIntegrazione = null;
			}    
		}

		return BackwardCompatibilityProperties.headerSoapActorIntegrazione;
	}

	/**
	 * Restituisce il prefix dell'header Soap di integrazione di default
	 * 
	 * @return Restituisce il prefix dell'header Soap di integrazione di default
	 */
	private static String headerSoapPrefixIntegrazione = null;
	public String getHeaderSoapPrefixIntegrazione() {
		if(BackwardCompatibilityProperties.headerSoapPrefixIntegrazione==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.backwardCompatibility.integrazione.soap.headerPrefix");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				BackwardCompatibilityProperties.headerSoapPrefixIntegrazione = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.backwardCompatibility.integrazione.soap.headerPrefix': "+e.getMessage());
				BackwardCompatibilityProperties.headerSoapPrefixIntegrazione = null;
			}    
		}

		return BackwardCompatibilityProperties.headerSoapPrefixIntegrazione;
	}
	
	private static Boolean isBackwardCompatibilityHeaderIntegrazione = null;
	public boolean isBackwardCompatibilityHeaderIntegrazione(){
		if(BackwardCompatibilityProperties.isBackwardCompatibilityHeaderIntegrazione==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.backwardCompatibility.integrazione.keywords.backwardCompatibility"); 
	
				if (value != null){
					value = value.trim();
					BackwardCompatibilityProperties.isBackwardCompatibilityHeaderIntegrazione = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.backwardCompatibility.integrazione.keywords.backwardCompatibility' non impostata, viene utilizzato il default=false");
					BackwardCompatibilityProperties.isBackwardCompatibilityHeaderIntegrazione = false;
				}
	
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.backwardCompatibility.integrazione.keywords.backwardCompatibility' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				BackwardCompatibilityProperties.isBackwardCompatibilityHeaderIntegrazione = false;
			}
		}
	
		return BackwardCompatibilityProperties.isBackwardCompatibilityHeaderIntegrazione;
	}

}
