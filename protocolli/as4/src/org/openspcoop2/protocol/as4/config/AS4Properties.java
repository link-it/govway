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

package org.openspcoop2.protocol.as4.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.openspcoop2.protocol.as4.properties.SecurityPolicyXSDValidator;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.xml.AbstractValidatoreXSD;
import org.slf4j.Logger;

/**
 * Classe che gestisce il file di properties 'as4.properties' del protocollo AS4
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AS4Properties {

	/** Logger utilizzato per errori eventuali. */
	private Logger log = null;


	/** Copia Statica */
	private static AS4Properties as4Properties = null;

	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'as4.properties' */
	private AS4InstanceProperties reader;





	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	public AS4Properties(String confDir,Logger log) throws ProtocolException{

		if(log != null)
			this.log = log;
		else
			this.log = LoggerWrapperFactory.getLogger("AS4Properties");

		/* ---- Lettura del cammino del file di configurazione ---- */

		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = AS4Properties.class.getResourceAsStream("/as4.properties");
			if(properties==null){
				throw new Exception("File '/as4.properties' not found");
			}
			propertiesReader.load(properties);
		}catch(Exception e) {
			this.log.error("Riscontrato errore durante la lettura del file 'as4.properties': "+e.getMessage());
			throw new ProtocolException("AS4Properties initialize error: "+e.getMessage(),e);
		}finally{
			try{
				if(properties!=null)
					properties.close();
			}catch(Exception er){}
		}
		try{
			this.reader = new AS4InstanceProperties(propertiesReader, this.log);
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}

	}

	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static synchronized void initialize(String confDir,Logger log) throws ProtocolException{

		if(AS4Properties.as4Properties==null)
			AS4Properties.as4Properties = new AS4Properties(confDir,log);	

	}

	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di OpenAS4Properties
	 * @throws Exception 
	 * 
	 */
	public static AS4Properties getInstance() throws ProtocolException{

		if(AS4Properties.as4Properties==null)
			throw new ProtocolException("AS4Properties not initialized (use init method in factory)");

		return AS4Properties.as4Properties;
	}




	public void validaConfigurazione(Loader loader) throws ProtocolException  {	
		try{  

			generateIDasUUID();
			
			isRiferimentoIDRichiesta_PD_Required();
			isRiferimentoIDRichiesta_PA_Required();
			
			this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo();
			this.isAggiungiDetailErroreApplicativo_SoapFaultPdD();
			
			getPayloadProfilesDefaultPayloads();
			getPayloadProfilesDefaultPayloadProfiles();
			
			getPropertiesDefault();
			getPropertiesSetDefault();
			
			File f = this.getSecurityPoliciesFolder();
			File[] list = f.listFiles();
			if(list==null || list.length<=0) {
				throw new Exception("Almeno una security policy deve essere definita all'interno del file '"+f.getAbsolutePath()+"'");
			}
			for(File file: list) {
				try {
					AbstractValidatoreXSD validator = SecurityPolicyXSDValidator.getXSDValidator(this.log);
					validator.valida(file);
				}catch(Exception e) {
					throw new Exception("SecurityPolicy '"+file.getAbsolutePath()+"' contiene un formato non corretto: "+e.getMessage(),e);
				}
			}
			
			String secPolicyDefault = getSecurityPolicyDefault();
			if(secPolicyDefault!=null) {
				boolean find = false;
				for(File file: list) {
					int lastIndexOf = file.getName().lastIndexOf(".");
					String fileWithoutExt = (lastIndexOf > 0) ? file.getName().substring(0, lastIndexOf) : file.getName();
					if(fileWithoutExt.equals(secPolicyDefault)) {
						find = true;
						break;
					}
				}
				if(!find) {
					throw new Exception("SecurityPolicy indicata come default '"+secPolicyDefault+"' non esiste tra le policy registrate nella directory '"+f.getAbsolutePath()+"'");
				}
			}
			
			// Per versione xml
			this.getPModeTranslatorPayloadProfilesFolder();
			this.getPModeTranslatorPropertiesFolder();
			
			// connettore
			if(this.isDomibusGatewayRegistry()) {
				this.getDomibusGatewayRegistrySoggettoDefault();
			}
			else {
				this.getDomibusGatewayConfigDefaultUrl();
				boolean https = this.isDomibusGatewayConfigDefaultHttpsEnabled();
				if(https) {
					this.getDomibusGatewayConfigDefaultHttpsProperties();
				}
			}
			
			// jms
			this.isDomibusGatewayJMS_debug();
			this.getDomibusGatewayJMS_threadsPoolSize();
			this.getDomibusGatewayJMS_threadCheckIntervalMs();
			this.getDomibusGatewayJMS_jndiContext();
			this.getDomibusGatewayJMS_connectionFactory();
			this.getDomibusGatewayJMS_username();
			this.getDomibusGatewayJMS_password();
			this.getDomibusGatewayJMS_queueReceiver();
			this.getDomibusGatewayJMS_queueSender();
			this.getDomibusGatewayJMS_jndiContextForAck();
			
			// ack trace
			if(this.isAckTraceEnabled()) {
				if(this.getAckTraceDatasource()==null) {
					throw new Exception("Datasource non definito per il tracing delle notifiche di ack");
				}
				this.getAckTraceDatasource_jndiContext();
				this.getAckTraceTipoDatabase();
				
				if(this.getAckDomibusDatasource()==null) {
					throw new Exception("Datasource non definito per il tracing delle notifiche di ack");
				}
				this.getAckDomibusDatasource_jndiContext();
				this.getAckDomibusTipoDatabase();
			}

		}catch(java.lang.Exception e) {
			String msg = "Riscontrato errore durante la validazione della proprieta' del protocollo as4, "+e.getMessage();
			this.log.error(msg,e);
			throw new ProtocolException(msg,e);
		}
	}


	/**
	 * Esempio di read property
	 *   
	 * @return Valore della property
	 * 
	 */
	private static Boolean generateIDasUUID = null;
	public Boolean generateIDasUUID(){
		if(AS4Properties.generateIDasUUID==null){
			
			Boolean defaultValue = true;
			String propertyName = "org.openspcoop2.protocol.as4.id.uuid";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					AS4Properties.generateIDasUUID = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue);
					AS4Properties.generateIDasUUID = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue+", errore:"+e.getMessage());
				AS4Properties.generateIDasUUID = defaultValue;
			}
		}

		return AS4Properties.generateIDasUUID;
	}
	
	
	
	
	/* **** CONFIGURAZIONE **** */
	
    /**
     * Restituisce l'indicazione se la funzionalita' 'Riferimento ID Richiesta' richiede che venga fornito obbligatoriamente l'informazione sull'identificativo della richiesta tramite i meccanismi di integrazione
	 * 
	 * @return True se la funzionalita' 'Riferimento ID Richiesta' richiede che venga fornito obbligatoriamente l'informazione sull'identificativo della richiesta tramite i meccanismi di integrazione
     * 
     */
	private static Boolean isRiferimentoIDRichiesta_PD_Required= null;
	private static Boolean isRiferimentoIDRichiesta_PD_RequiredRead= null;
    public Boolean isRiferimentoIDRichiesta_PD_Required(){
    	if(AS4Properties.isRiferimentoIDRichiesta_PD_RequiredRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.pd.riferimentoIdRichiesta.required"); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.isRiferimentoIDRichiesta_PD_Required = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.pd.riferimentoIdRichiesta.required' non impostata, viene utilizzato il default 'true'");
					AS4Properties.isRiferimentoIDRichiesta_PD_Required = true;
				}
				
				AS4Properties.isRiferimentoIDRichiesta_PD_RequiredRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.pd.riferimentoIdRichiesta.required' non impostata, viene utilizzato il default 'true', errore:"+e.getMessage());
				AS4Properties.isRiferimentoIDRichiesta_PD_Required = true;
				
				AS4Properties.isRiferimentoIDRichiesta_PD_RequiredRead = true;
			}
    	}
    	
    	return AS4Properties.isRiferimentoIDRichiesta_PD_Required;
	}
	
	private static Boolean isRiferimentoIDRichiesta_PA_Required= null;
	private static Boolean isRiferimentoIDRichiesta_PA_RequiredRead= null;
    public Boolean isRiferimentoIDRichiesta_PA_Required(){
    	if(AS4Properties.isRiferimentoIDRichiesta_PA_RequiredRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.pa.riferimentoIdRichiesta.required"); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.isRiferimentoIDRichiesta_PA_Required = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.pa.riferimentoIdRichiesta.required' non impostata, viene utilizzato il default 'true'");
					AS4Properties.isRiferimentoIDRichiesta_PA_Required = true;
				}
				
				AS4Properties.isRiferimentoIDRichiesta_PA_RequiredRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.pa.riferimentoIdRichiesta.required' non impostata, viene utilizzato il default 'true', errore:"+e.getMessage());
				AS4Properties.isRiferimentoIDRichiesta_PA_Required = true;
				
				AS4Properties.isRiferimentoIDRichiesta_PA_RequiredRead = true;
			}
    	}
    	
    	return AS4Properties.isRiferimentoIDRichiesta_PA_Required;
	}
	
	
	
	
	
	/* **** SOAP FAULT **** */
	
    /**
     * Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultApplicativo originale
     *   
     * @return Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultApplicativo originale
     * 
     */
	private static Boolean isAggiungiDetailErroreApplicativo_SoapFaultApplicativo= null;
	private static Boolean isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead= null;
    public Boolean isAggiungiDetailErroreApplicativo_SoapFaultApplicativo(){
    	if(AS4Properties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.erroreApplicativo.faultApplicativo.enrichDetails"); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.erroreApplicativo.faultApplicativo.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails)");
					AS4Properties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = null;
				}
				
				AS4Properties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.erroreApplicativo.faultApplicativo.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails), errore:"+e.getMessage());
				AS4Properties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = null;
				
				AS4Properties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead = true;
			}
    	}
    	
    	return AS4Properties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo;
	}
    
    /**
     * Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultPdD originale
     *   
     * @return Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultPdD originale
     * 
     */
	private static Boolean isAggiungiDetailErroreApplicativo_SoapFaultPdD= null;
	private static Boolean isAggiungiDetailErroreApplicativo_SoapFaultPdDRead= null;
    public Boolean isAggiungiDetailErroreApplicativo_SoapFaultPdD(){
    	if(AS4Properties.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.erroreApplicativo.faultPdD.enrichDetails"); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.isAggiungiDetailErroreApplicativo_SoapFaultPdD = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.erroreApplicativo.faultPdD.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultPdD.enrichDetails)");
					AS4Properties.isAggiungiDetailErroreApplicativo_SoapFaultPdD = null;
				}
				
				AS4Properties.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.erroreApplicativo.faultPdD.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultPdD.enrichDetails), errore:"+e.getMessage());
				AS4Properties.isAggiungiDetailErroreApplicativo_SoapFaultPdD = null;
				
				AS4Properties.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead = true;
			}
    	}
    	
    	return AS4Properties.isAggiungiDetailErroreApplicativo_SoapFaultPdD;
	}
    
	private File getDirectory(String fileName) throws Exception {
		File file = new File(fileName);
		
		if(!file.exists()) {
			FileSystemUtilities.mkdirParentDirectory(file);
			if(file.mkdir()==false) {
				throw new Exception("Directory ["+fileName+"] non esiste e creazione non riuscita");
			}
		}
		
		if(!file.isDirectory())
			throw new Exception("File ["+fileName+"] non e' una directory");
		
		if(!file.canRead())
			throw new Exception("Directory ["+fileName+"] non puo' essere letto");
		
		return file;
	}
	
	private File getFile(String fileName) throws Exception {
		File file = new File(fileName);
		
		if(!file.exists()) {
			throw new Exception("File ["+fileName+"] non esiste");
		}
		
		if(!file.isFile())
			throw new Exception("File ["+fileName+"] non e' un semplice file");
		
		if(!file.canRead())
			throw new Exception("File ["+fileName+"] non puo' essere letto");
		
		return file;
	}
	
	
	
	
	
	/* **** Domibus Configuration **** */
	
	private static byte[] payloadProfilesDefaultPayloads;
	private static Boolean payloadProfilesDefaultPayloadsRead;
	public byte[] getPayloadProfilesDefaultPayloads() throws ProtocolException {
		if(AS4Properties.payloadProfilesDefaultPayloadsRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.payloadProfiles.defaultPayloads"); 
				if (value != null){
					value = value.trim();
					File f  = this.getFile(value);
					payloadProfilesDefaultPayloads = FileSystemUtilities.readBytesFromFile(f);
				}
				
				payloadProfilesDefaultPayloadsRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.payloadProfiles.defaultPayloads', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return AS4Properties.payloadProfilesDefaultPayloads;
	}
	
	private static byte[] payloadProfilesDefaultPayloadProfiles;
	private static Boolean payloadProfilesDefaultPayloadProfilesRead;
	public byte[] getPayloadProfilesDefaultPayloadProfiles() throws ProtocolException {
		if(AS4Properties.payloadProfilesDefaultPayloadProfilesRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.payloadProfiles.defaultPayloadProfiles"); 
				if (value != null){
					value = value.trim();
					File f  = this.getFile(value);
					payloadProfilesDefaultPayloadProfiles = FileSystemUtilities.readBytesFromFile(f);
				}
				
				payloadProfilesDefaultPayloadProfilesRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.payloadProfiles.defaultPayloadProfiles', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return AS4Properties.payloadProfilesDefaultPayloadProfiles;
	}
	
	private static byte[] propertiesDefaultProperty;
	private static Boolean propertiesDefaultPropertyRead;
	public byte[] getPropertiesDefault() throws ProtocolException {
		if(AS4Properties.propertiesDefaultPropertyRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.properties.defaultProperty"); 
				if (value != null){
					value = value.trim();
					File f  = this.getFile(value);
					propertiesDefaultProperty = FileSystemUtilities.readBytesFromFile(f);
				}
				
				propertiesDefaultPropertyRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.properties.defaultProperty', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return AS4Properties.propertiesDefaultProperty;
	}
	
	private static byte[] propertiesDefaultPropertySet;
	private static Boolean propertiesDefaultPropertySetRead;
	public byte[] getPropertiesSetDefault() throws ProtocolException {
		if(AS4Properties.propertiesDefaultPropertySetRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.properties.defaultPropertySet"); 
				if (value != null){
					value = value.trim();
					File f  = this.getFile(value);
					propertiesDefaultPropertySet = FileSystemUtilities.readBytesFromFile(f);
				}
				
				propertiesDefaultPropertySetRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.properties.defaultPropertySet', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return AS4Properties.propertiesDefaultPropertySet;
	}
	
	private static File securityPoliciesFolder;
	public File getSecurityPoliciesFolder() throws ProtocolException {
		if(AS4Properties.securityPoliciesFolder==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.securityPolicies.folder"); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.securityPoliciesFolder = this.getDirectory(value);
				}else{
					throw new Exception("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.securityPolicies.folder' non impostata");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.securityPolicies.folder', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return AS4Properties.securityPoliciesFolder;
	}
	
	private static String securityPolicyDefault;
	public String getSecurityPolicyDefault() throws ProtocolException {
		if(AS4Properties.securityPolicyDefault==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.securityPolicies.default"); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.securityPolicyDefault = value;
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.securityPolicies.default', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return AS4Properties.securityPolicyDefault;
	}
	
	
	
	/* **** PModeTranslator **** */
		
	private static File pModeTranslatorPayloadProfilesFolder;
	public File getPModeTranslatorPayloadProfilesFolder() throws ProtocolException {
		if(AS4Properties.pModeTranslatorPayloadProfilesFolder==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.pmode.pModeTranslatorPayloadProfilesFolder"); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.pModeTranslatorPayloadProfilesFolder = this.getDirectory(value);
				}else{
					throw new Exception("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.pmode.pModeTranslatorPayloadProfilesFolder' non impostata");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.pmode.pModeTranslatorPayloadProfilesFolder', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return AS4Properties.pModeTranslatorPayloadProfilesFolder;
	}
	
	private static File pModeTranslatorPropertiesFolder;
	public File getPModeTranslatorPropertiesFolder() throws ProtocolException {
		if(AS4Properties.pModeTranslatorPropertiesFolder==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.pmode.pModeTranslatorPropertiesFolder"); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.pModeTranslatorPropertiesFolder = this.getDirectory(value);
				}else{
					throw new Exception("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.pmode.pModeTranslatorPropertiesFolder' non impostata");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.pmode.pModeTranslatorPropertiesFolder', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return AS4Properties.pModeTranslatorPropertiesFolder;
	}


	
	
	/* **** Comunicazione HTTP verso Gateway **** */
	
	private static Boolean isDomibusGatewayRegistry= null;
	private static Boolean isDomibusGatewayRegistryRead= null;
    public Boolean isDomibusGatewayRegistry() throws ProtocolException{
    	if(AS4Properties.isDomibusGatewayRegistryRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.domibusGateway.registry"); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.isDomibusGatewayRegistry = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' non impostata");
				}
				
				AS4Properties.isDomibusGatewayRegistryRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusGateway.registry' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				AS4Properties.isDomibusGatewayRegistryRead = true;
				throw new ProtocolException(e);
				
			}
    	}
    	
    	return AS4Properties.isDomibusGatewayRegistry;
	}
	
	
	private static String domibusGatewayRegistrySoggettoDefault;
	public String getDomibusGatewayRegistrySoggettoDefault() throws ProtocolException {
		if(AS4Properties.domibusGatewayRegistrySoggettoDefault==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.domibusGateway.registry.soggetto.default"); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.domibusGatewayRegistrySoggettoDefault = value;
				}
				else {
					throw new Exception("Proprieta' non impostata");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusGateway.registry.soggetto.default', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return AS4Properties.domibusGatewayRegistrySoggettoDefault;
	}
	
	private static List<String> domibusGatewayRegistrySoggettoCustom_Read = new ArrayList<String>();
	private static HashMap<String,String> domibusGatewayRegistrySoggettoCustom = new HashMap<String,String>();
	public String getDomibusGatewayRegistrySoggettoCustom(String nome) throws ProtocolException {
		if(AS4Properties.domibusGatewayRegistrySoggettoCustom_Read.contains(nome)==false){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.domibusGateway.registry.soggetto."+nome); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.domibusGatewayRegistrySoggettoCustom.put(nome, value);
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusGateway.registry.soggetto."+nome+"', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}finally {
				AS4Properties.domibusGatewayRegistrySoggettoCustom_Read.add(nome);
			}
    	}
		return AS4Properties.domibusGatewayRegistrySoggettoCustom.get(nome);
	}
    
	private static List<String> domibusGatewayRegistrySoggettoCustomList = new ArrayList<String>();
	public List<String> getDomibusGatewayRegistrySoggettoCustomList() throws ProtocolException {
		if(domibusGatewayRegistrySoggettoCustomList==null){
	    	try{  
	    		Properties p = this.reader.readProperties_convertEnvProperties("org.openspcoop2.protocol.as4.domibusGateway.registry.soggetto.");
	    		domibusGatewayRegistrySoggettoCustomList = new ArrayList<String>();
	    		Enumeration<?> keys = p.keys();
	    		while (keys.hasMoreElements()) {
					Object object = (Object) keys.nextElement();
					if(object instanceof String) {
						String key = (String) object;
						String value = p.getProperty(key);
						domibusGatewayRegistrySoggettoCustomList.add(value);
					}
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusGateway.registry.soggetto.*', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return domibusGatewayRegistrySoggettoCustomList;
	}
    
	
	private static String domibusGatewayConfigDefaultUrl;
	public String getDomibusGatewayConfigDefaultUrl() throws ProtocolException {
		if(AS4Properties.domibusGatewayConfigDefaultUrl==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.domibusGateway.config.default.url"); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.domibusGatewayConfigDefaultUrl = value;
				}
				else {
					throw new Exception("Proprieta' non impostata");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusGateway.config.default.url', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return AS4Properties.domibusGatewayConfigDefaultUrl;
	}
	
	private static Boolean isDomibusGatewayConfigDefaultHttpsEnabled= null;
	private static Boolean isDomibusGatewayConfigDefaultHttpsEnabledRead= null;
    public Boolean isDomibusGatewayConfigDefaultHttpsEnabled(){
    	if(AS4Properties.isDomibusGatewayConfigDefaultHttpsEnabledRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.domibusGateway.config.default.https.enabled"); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.isDomibusGatewayConfigDefaultHttpsEnabled = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusGateway.config.default.https.enabled' non impostata, viene utilizzato il default=false");
					AS4Properties.isDomibusGatewayConfigDefaultHttpsEnabled = false;
				}
				
				AS4Properties.isDomibusGatewayConfigDefaultHttpsEnabledRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusGateway.config.default.https.enabled' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				AS4Properties.isDomibusGatewayConfigDefaultHttpsEnabled = false;
				
				AS4Properties.isDomibusGatewayConfigDefaultHttpsEnabledRead = true;
			}
    	}
    	
    	return AS4Properties.isDomibusGatewayConfigDefaultHttpsEnabled;
	}
    
    private static Properties domibusGatewayConfigDefaultHttpsProperties;
	public Properties getDomibusGatewayConfigDefaultHttpsProperties() throws ProtocolException {
		if(AS4Properties.domibusGatewayConfigDefaultHttpsProperties==null){
	    	try{  
	    		Properties p = this.reader.readProperties_convertEnvProperties("org.openspcoop2.protocol.as4.domibusGateway.config.default.https.property.");
				
				if (p != null && p.size()>0){
					AS4Properties.domibusGatewayConfigDefaultHttpsProperties = p;
				}
				else {
					throw new Exception("Proprieta' non impostate");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusGateway.config.default.https.property.*', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return AS4Properties.domibusGatewayConfigDefaultHttpsProperties;
	}
	
	private static List<String> domibusGatewayConfigCustomUrl_Read = new ArrayList<String>();
	private static HashMap<String,String> domibusGatewayConfigCustomUrl = new HashMap<String,String>();
	public String getDomibusGatewayConfigCustomUrl(String nome) throws ProtocolException {
		if(AS4Properties.domibusGatewayConfigCustomUrl_Read.contains(nome)==false){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.domibusGateway.config."+nome+".url"); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.domibusGatewayConfigCustomUrl.put(nome, value);
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusGateway.config."+nome+".url', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}finally {
				AS4Properties.domibusGatewayConfigCustomUrl_Read.add(nome);
			}
    	}
		return AS4Properties.domibusGatewayConfigCustomUrl.get(nome);
	}
	
	private static List<String> domibusGatewayConfigCustomHttsEnabled_Read = new ArrayList<String>();
	private static HashMap<String,Boolean> domibusGatewayConfigCustomHttpsEnabled = new HashMap<String,Boolean>();
    public Boolean isDomibusGatewayConfigCustomHttpsEnabled(String nome) throws ProtocolException{
    	if(AS4Properties.domibusGatewayConfigCustomHttsEnabled_Read.contains(nome)==false){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.domibusGateway.config."+nome+".https.enabled"); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.domibusGatewayConfigCustomHttpsEnabled.put(nome, Boolean.parseBoolean(value));
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusGateway.config."+nome+".https.enabled' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
	    	finally {
	    		AS4Properties.domibusGatewayConfigCustomHttsEnabled_Read.add(nome);
	    	}
    	}
    	
    	return AS4Properties.domibusGatewayConfigCustomHttpsEnabled.get(nome);
	}
    
    private static List<String> domibusGatewayConfigCustomHttsProperties_Read = new ArrayList<String>();
	private static HashMap<String,Properties> domibusGatewayConfigCustomHttpsProperties = new HashMap<String,Properties>();
	public Properties getDomibusGatewayConfigCustomHttpsProperties(String nome) throws ProtocolException {
		if(AS4Properties.domibusGatewayConfigCustomHttsProperties_Read.contains(nome)==false){
	    	try{  
	    		Properties p = this.reader.readProperties_convertEnvProperties("org.openspcoop2.protocol.as4.domibusGateway.config."+nome+".https.property.");
				
				if (p != null && p.size()>0){
					AS4Properties.domibusGatewayConfigCustomHttpsProperties.put(nome, p);
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusGateway.config."+nome+".https.property.*', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
	    	finally {
	    		AS4Properties.domibusGatewayConfigCustomHttsProperties_Read.add(nome);
	    	}
    	}
		return AS4Properties.domibusGatewayConfigCustomHttpsProperties.get(nome);
	}
	

	
	
	
	
	/* **** Comunicazione JMS verso Broker **** */
	
	private static Boolean domibusGatewayJMS_debug= null;
	private static Boolean domibusGatewayJMS_debugRead= null;
    public Boolean isDomibusGatewayJMS_debug(){
    	if(AS4Properties.domibusGatewayJMS_debugRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.domibusJms.debug"); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.domibusGatewayJMS_debug = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusJms.debug' non impostata, viene utilizzato il default=false");
					AS4Properties.domibusGatewayJMS_debug = false;
				}
				
				AS4Properties.domibusGatewayJMS_debugRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusJms.debug' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				AS4Properties.isDomibusGatewayConfigDefaultHttpsEnabled = false;
				
				AS4Properties.domibusGatewayJMS_debug = false;
			}
    	}
    	
    	return AS4Properties.domibusGatewayJMS_debug;
	}
	
	private static Integer domibusGatewayJMS_threadsPoolSize;
	public Integer getDomibusGatewayJMS_threadsPoolSize() throws ProtocolException {
		if(AS4Properties.domibusGatewayJMS_threadsPoolSize==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.domibusJms.threadsPool.size"); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.domibusGatewayJMS_threadsPoolSize = Integer.parseInt(value);
				}
				else {
					throw new Exception("Proprieta' non impostata");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusJms.threadsPool.size', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return AS4Properties.domibusGatewayJMS_threadsPoolSize;
	}
	
	private static Integer domibusGatewayJMS_threadCheckIntervalMs;
	public Integer getDomibusGatewayJMS_threadCheckIntervalMs() throws ProtocolException {
		if(AS4Properties.domibusGatewayJMS_threadCheckIntervalMs==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.domibusJms.thread.checkIntervalMs"); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.domibusGatewayJMS_threadCheckIntervalMs = Integer.parseInt(value);
				}
				else {
					throw new Exception("Proprieta' non impostata");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusJms.thread.checkIntervalMs', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return AS4Properties.domibusGatewayJMS_threadCheckIntervalMs;
	}
	
	private static Properties domibusGatewayJMS_jndiContext = null;
	public Properties getDomibusGatewayJMS_jndiContext() throws ProtocolException {
		if(AS4Properties.domibusGatewayJMS_jndiContext==null){
	    	try{  
	    		AS4Properties.domibusGatewayJMS_jndiContext = this.reader.readProperties_convertEnvProperties("org.openspcoop2.protocol.as4.domibusJms.jndi.");
	    		if (AS4Properties.domibusGatewayJMS_jndiContext == null || AS4Properties.domibusGatewayJMS_jndiContext.size()<0){
	    			AS4Properties.domibusGatewayJMS_jndiContext = new Properties(); // context jndi vuoto
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusJms.jndi.*', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return AS4Properties.domibusGatewayJMS_jndiContext;
	}
	
	private static String domibusGatewayJMS_connectionFactory;
	public String getDomibusGatewayJMS_connectionFactory() throws ProtocolException {
		if(AS4Properties.domibusGatewayJMS_connectionFactory==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.domibusJms.connectionFactory"); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.domibusGatewayJMS_connectionFactory = value;
				}
				else {
					throw new Exception("Proprieta' non impostata");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusJms.connectionFactory', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return AS4Properties.domibusGatewayJMS_connectionFactory;
	}
	
	private static Boolean domibusGatewayJMS_username_read;
	private static String domibusGatewayJMS_username;
	public String getDomibusGatewayJMS_username() throws ProtocolException {
		if(AS4Properties.domibusGatewayJMS_username_read==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.domibusJms.username"); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.domibusGatewayJMS_username = value;
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusJms.username', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}finally {
				domibusGatewayJMS_username_read = true;
			}
    	}
		return AS4Properties.domibusGatewayJMS_username;
	}
	
	private static Boolean domibusGatewayJMS_password_read;
	private static String domibusGatewayJMS_password;
	public String getDomibusGatewayJMS_password() throws ProtocolException {
		if(AS4Properties.domibusGatewayJMS_password_read==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.domibusJms.password"); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.domibusGatewayJMS_password = value;
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusJms.password', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}finally {
				domibusGatewayJMS_password_read = true;
			}
    	}
		return AS4Properties.domibusGatewayJMS_password;
	}
	
	private static String domibusGatewayJMS_queueReceiver;
	public String getDomibusGatewayJMS_queueReceiver() throws ProtocolException {
		if(AS4Properties.domibusGatewayJMS_queueReceiver==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.domibusJms.queue.receiver"); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.domibusGatewayJMS_queueReceiver = value;
				}
				else {
					throw new Exception("Proprieta' non impostata");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusJms.queue.receiver', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return AS4Properties.domibusGatewayJMS_queueReceiver;
	}
	
	private static String domibusGatewayJMS_queueSender;
	public String getDomibusGatewayJMS_queueSender() throws ProtocolException {
		if(AS4Properties.domibusGatewayJMS_queueSender==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.domibusJms.queue.sender"); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.domibusGatewayJMS_queueSender = value;
				}
				else {
					throw new Exception("Proprieta' non impostata");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusJms.queue.sender', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return AS4Properties.domibusGatewayJMS_queueSender;
	}
	
	private static Boolean domibusGatewayJMS_AckProviderUrl_read;
	private static String domibusGatewayJMS_AckProviderUrl;
	private String getDomibusGatewayJMS_AckProviderUrl() throws ProtocolException {
		if(AS4Properties.domibusGatewayJMS_AckProviderUrl_read==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.domibusJms.queue.sender.provider.url"); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.domibusGatewayJMS_AckProviderUrl = value;
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusJms.queue.sender.provider.url', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}finally {
				domibusGatewayJMS_AckProviderUrl_read = true;
			}
    	}
		return AS4Properties.domibusGatewayJMS_AckProviderUrl;
	}
	
	private static Properties domibusGatewayJMS_jndiContextForAck = null;
	public Properties getDomibusGatewayJMS_jndiContextForAck() throws ProtocolException {
		if(AS4Properties.domibusGatewayJMS_jndiContextForAck==null){
			if(this.getDomibusGatewayJMS_AckProviderUrl()!=null) {
				domibusGatewayJMS_jndiContextForAck = new Properties();
				Properties p = this.getDomibusGatewayJMS_jndiContext();
				Enumeration<?> keys = p.keys();
				while (keys.hasMoreElements()) {
					Object object = (Object) keys.nextElement();
					if(object instanceof String) {
						String key = (String) object;
						if(!"java.naming.provider.url".equalsIgnoreCase(key)) {
							domibusGatewayJMS_jndiContextForAck.put(key, p.get(key));
						}
					}
				}
				domibusGatewayJMS_jndiContextForAck.put("java.naming.provider.url", this.getDomibusGatewayJMS_AckProviderUrl());
			}
			else {
				domibusGatewayJMS_jndiContextForAck = this.getDomibusGatewayJMS_jndiContext();
			}
			
    	}
		return AS4Properties.domibusGatewayJMS_jndiContextForAck;
	}
	
	
	
	
	
	
	
	/* **** Tracciamento delle Notifiche **** */
	
	private static Boolean isAckTraceEnabled= null;
    public Boolean isAckTraceEnabled(){
    	if(AS4Properties.isAckTraceEnabled==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.ack.trace.enabled"); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.isAckTraceEnabled = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.ack.trace.enabled' non impostata, viene utilizzato il default=true");
					AS4Properties.isAckTraceEnabled = true;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.ack.trace.enabled' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				AS4Properties.isAckTraceEnabled = true;
			}
    	}
    	
    	return AS4Properties.isAckTraceEnabled;
	}
    
    private static Boolean ackTraceDatasource_read;
	private static String ackTraceDatasource;
	public String getAckTraceDatasource() throws ProtocolException {
		if(AS4Properties.ackTraceDatasource_read==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.ack.trace.dataSource"); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.ackTraceDatasource = value;
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.ack.trace.dataSource', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}finally {
				ackTraceDatasource_read = true;
			}
    	}
		return AS4Properties.ackTraceDatasource;
	}
	
	private static Boolean ackTraceTipoDatabase_read;
	private static String ackTraceTipoDatabase;
	public String getAckTraceTipoDatabase() throws ProtocolException {
		if(AS4Properties.ackTraceTipoDatabase_read==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.ack.trace.tipoDatabase"); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.ackTraceTipoDatabase = value;
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.ack.trace.tipoDatabase', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}finally {
				ackTraceTipoDatabase_read = true;
			}
    	}
		return AS4Properties.ackTraceTipoDatabase;
	}
	
	private static Properties ackTraceDatasource_jndiContext = null;
	public Properties getAckTraceDatasource_jndiContext() throws ProtocolException {
		if(AS4Properties.ackTraceDatasource_jndiContext==null){
	    	try{  
	    		AS4Properties.ackTraceDatasource_jndiContext = this.reader.readProperties_convertEnvProperties("org.openspcoop2.protocol.as4.ack.trace.dataSource.property.");
	    		if (AS4Properties.ackTraceDatasource_jndiContext == null || AS4Properties.ackTraceDatasource_jndiContext.size()<0){
	    			AS4Properties.ackTraceDatasource_jndiContext = new Properties(); // context jndi vuoto
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.ack.trace.dataSource.property.*', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return AS4Properties.ackTraceDatasource_jndiContext;
	}
	
	
	private static Boolean ackDomibusDatasource_read;
	private static String ackDomibusDatasource;
	public String getAckDomibusDatasource() throws ProtocolException {
		if(AS4Properties.ackDomibusDatasource_read==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.ack.domibus.dataSource"); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.ackDomibusDatasource = value;
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.ack.domibus.dataSource', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}finally {
				ackDomibusDatasource_read = true;
			}
    	}
		return AS4Properties.ackDomibusDatasource;
	}
	
	private static Boolean ackDomibusTipoDatabase_read;
	private static String ackDomibusTipoDatabase;
	public String getAckDomibusTipoDatabase() throws ProtocolException {
		if(AS4Properties.ackDomibusTipoDatabase_read==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.as4.ack.domibus.tipoDatabase"); 
				
				if (value != null){
					value = value.trim();
					AS4Properties.ackDomibusTipoDatabase = value;
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.ack.domibus.tipoDatabase', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}finally {
				ackDomibusTipoDatabase_read = true;
			}
    	}
		return AS4Properties.ackDomibusTipoDatabase;
	}
	
	private static Properties ackDomibusDatasource_jndiContext = null;
	public Properties getAckDomibusDatasource_jndiContext() throws ProtocolException {
		if(AS4Properties.ackDomibusDatasource_jndiContext==null){
	    	try{  
	    		AS4Properties.ackDomibusDatasource_jndiContext = this.reader.readProperties_convertEnvProperties("org.openspcoop2.protocol.as4.ack.domibus.dataSource.property.");
	    		if (AS4Properties.ackDomibusDatasource_jndiContext == null || AS4Properties.ackDomibusDatasource_jndiContext.size()<0){
	    			AS4Properties.ackDomibusDatasource_jndiContext = new Properties(); // context jndi vuoto
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.ack.domibus.dataSource.property.*', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return AS4Properties.ackDomibusDatasource_jndiContext;
	}
}
