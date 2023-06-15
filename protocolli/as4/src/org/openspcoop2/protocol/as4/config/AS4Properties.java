/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
import org.openspcoop2.protocol.basic.BasicStaticInstanceConfig;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.BooleanNullable;
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
			}catch(Throwable er){
				// close
			}
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
			
			this.useConfigStaticInstance();
			this.useErroreApplicativoStaticInstance();
			this.useEsitoStaticInstance();
			this.getStaticInstanceConfig();

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
	private Boolean generateIDasUUID = null;
	public Boolean generateIDasUUID(){
		if(this.generateIDasUUID==null){
			
			Boolean defaultValue = true;
			String propertyName = "org.openspcoop2.protocol.as4.id.uuid";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.generateIDasUUID = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue);
					this.generateIDasUUID = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue+", errore:"+e.getMessage());
				this.generateIDasUUID = defaultValue;
			}
		}

		return this.generateIDasUUID;
	}
	
	
	
	
	/* **** CONFIGURAZIONE **** */
	
    /**
     * Restituisce l'indicazione se la funzionalita' 'Riferimento ID Richiesta' richiede che venga fornito obbligatoriamente l'informazione sull'identificativo della richiesta tramite i meccanismi di integrazione
	 * 
	 * @return True se la funzionalita' 'Riferimento ID Richiesta' richiede che venga fornito obbligatoriamente l'informazione sull'identificativo della richiesta tramite i meccanismi di integrazione
     * 
     */
	private Boolean isRiferimentoIDRichiesta_PD_Required= null;
	private Boolean isRiferimentoIDRichiesta_PD_RequiredRead= null;
    public Boolean isRiferimentoIDRichiesta_PD_Required(){
    	if(this.isRiferimentoIDRichiesta_PD_RequiredRead==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.pd.riferimentoIdRichiesta.required"); 
				
				if (value != null){
					value = value.trim();
					this.isRiferimentoIDRichiesta_PD_Required = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.pd.riferimentoIdRichiesta.required' non impostata, viene utilizzato il default 'true'");
					this.isRiferimentoIDRichiesta_PD_Required = true;
				}
				
				this.isRiferimentoIDRichiesta_PD_RequiredRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.pd.riferimentoIdRichiesta.required' non impostata, viene utilizzato il default 'true', errore:"+e.getMessage());
				this.isRiferimentoIDRichiesta_PD_Required = true;
				
				this.isRiferimentoIDRichiesta_PD_RequiredRead = true;
			}
    	}
    	
    	return this.isRiferimentoIDRichiesta_PD_Required;
	}
	
	private Boolean isRiferimentoIDRichiesta_PA_Required= null;
	private Boolean isRiferimentoIDRichiesta_PA_RequiredRead= null;
    public Boolean isRiferimentoIDRichiesta_PA_Required(){
    	if(this.isRiferimentoIDRichiesta_PA_RequiredRead==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.pa.riferimentoIdRichiesta.required"); 
				
				if (value != null){
					value = value.trim();
					this.isRiferimentoIDRichiesta_PA_Required = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.pa.riferimentoIdRichiesta.required' non impostata, viene utilizzato il default 'true'");
					this.isRiferimentoIDRichiesta_PA_Required = true;
				}
				
				this.isRiferimentoIDRichiesta_PA_RequiredRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.pa.riferimentoIdRichiesta.required' non impostata, viene utilizzato il default 'true', errore:"+e.getMessage());
				this.isRiferimentoIDRichiesta_PA_Required = true;
				
				this.isRiferimentoIDRichiesta_PA_RequiredRead = true;
			}
    	}
    	
    	return this.isRiferimentoIDRichiesta_PA_Required;
	}
	
	
	
	
	
	/* **** SOAP FAULT **** */
	
    /**
     * Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultApplicativo originale
     *   
     * @return Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultApplicativo originale
     * 
     */
	private BooleanNullable isAggiungiDetailErroreApplicativo_SoapFaultApplicativo= null;
	private Boolean isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead= null;
    public BooleanNullable isAggiungiDetailErroreApplicativo_SoapFaultApplicativo(){
    	if(this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.erroreApplicativo.faultApplicativo.enrichDetails"); 
				
				if (value != null){
					value = value.trim();
					Boolean b = Boolean.parseBoolean(value);
					this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = b ? BooleanNullable.TRUE() : BooleanNullable.FALSE();
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.erroreApplicativo.faultApplicativo.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails)");
					this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = BooleanNullable.NULL();
				}
				
				this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.erroreApplicativo.faultApplicativo.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails), errore:"+e.getMessage());
				this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = BooleanNullable.NULL();
				
				this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead = true;
			}
    	}
    	
    	return this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo;
	}
    
    /**
     * Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultPdD originale
     *   
     * @return Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultPdD originale
     * 
     */
	private BooleanNullable isAggiungiDetailErroreApplicativo_SoapFaultPdD= null;
	private Boolean isAggiungiDetailErroreApplicativo_SoapFaultPdDRead= null;
    public BooleanNullable isAggiungiDetailErroreApplicativo_SoapFaultPdD(){
    	if(this.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.erroreApplicativo.faultPdD.enrichDetails"); 
				
				if (value != null){
					value = value.trim();
					Boolean b = Boolean.parseBoolean(value);
					this.isAggiungiDetailErroreApplicativo_SoapFaultPdD = b ? BooleanNullable.TRUE() : BooleanNullable.FALSE();
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.erroreApplicativo.faultPdD.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultPdD.enrichDetails)");
					this.isAggiungiDetailErroreApplicativo_SoapFaultPdD = BooleanNullable.NULL();
				}
				
				this.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.erroreApplicativo.faultPdD.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultPdD.enrichDetails), errore:"+e.getMessage());
				this.isAggiungiDetailErroreApplicativo_SoapFaultPdD = BooleanNullable.NULL();
				
				this.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead = true;
			}
    	}
    	
    	return this.isAggiungiDetailErroreApplicativo_SoapFaultPdD;
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
	
	private byte[] payloadProfilesDefaultPayloads;
	private Boolean payloadProfilesDefaultPayloadsRead;
	public byte[] getPayloadProfilesDefaultPayloads() throws ProtocolException {
		if(this.payloadProfilesDefaultPayloadsRead==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.payloadProfiles.defaultPayloads"); 
				if (value != null){
					value = value.trim();
					File f  = this.getFile(value);
					this.payloadProfilesDefaultPayloads = FileSystemUtilities.readBytesFromFile(f);
				}
				
				this.payloadProfilesDefaultPayloadsRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.payloadProfiles.defaultPayloads', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return this.payloadProfilesDefaultPayloads;
	}
	
	private byte[] payloadProfilesDefaultPayloadProfiles;
	private Boolean payloadProfilesDefaultPayloadProfilesRead;
	public byte[] getPayloadProfilesDefaultPayloadProfiles() throws ProtocolException {
		if(this.payloadProfilesDefaultPayloadProfilesRead==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.payloadProfiles.defaultPayloadProfiles"); 
				if (value != null){
					value = value.trim();
					File f  = this.getFile(value);
					this.payloadProfilesDefaultPayloadProfiles = FileSystemUtilities.readBytesFromFile(f);
				}
				
				this.payloadProfilesDefaultPayloadProfilesRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.payloadProfiles.defaultPayloadProfiles', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return this.payloadProfilesDefaultPayloadProfiles;
	}
	
	private byte[] propertiesDefaultProperty;
	private Boolean propertiesDefaultPropertyRead;
	public byte[] getPropertiesDefault() throws ProtocolException {
		if(this.propertiesDefaultPropertyRead==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.properties.defaultProperty"); 
				if (value != null){
					value = value.trim();
					File f  = this.getFile(value);
					this.propertiesDefaultProperty = FileSystemUtilities.readBytesFromFile(f);
				}
				
				this.propertiesDefaultPropertyRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.properties.defaultProperty', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return this.propertiesDefaultProperty;
	}
	
	private byte[] propertiesDefaultPropertySet;
	private Boolean propertiesDefaultPropertySetRead;
	public byte[] getPropertiesSetDefault() throws ProtocolException {
		if(this.propertiesDefaultPropertySetRead==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.properties.defaultPropertySet"); 
				if (value != null){
					value = value.trim();
					File f  = this.getFile(value);
					this.propertiesDefaultPropertySet = FileSystemUtilities.readBytesFromFile(f);
				}
				
				this.propertiesDefaultPropertySetRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.properties.defaultPropertySet', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return this.propertiesDefaultPropertySet;
	}
	
	private File securityPoliciesFolder;
	public File getSecurityPoliciesFolder() throws ProtocolException {
		if(this.securityPoliciesFolder==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.securityPolicies.folder"); 
				
				if (value != null){
					value = value.trim();
					this.securityPoliciesFolder = this.getDirectory(value);
				}else{
					throw new Exception("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.securityPolicies.folder' non impostata");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.securityPolicies.folder', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return this.securityPoliciesFolder;
	}
	
	private String securityPolicyDefault;
	public String getSecurityPolicyDefault() throws ProtocolException {
		if(this.securityPolicyDefault==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.securityPolicies.default"); 
				
				if (value != null){
					value = value.trim();
					this.securityPolicyDefault = value;
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.securityPolicies.default', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return this.securityPolicyDefault;
	}
	
	
	
	/* **** PModeTranslator **** */
		
	private File pModeTranslatorPayloadProfilesFolder;
	public File getPModeTranslatorPayloadProfilesFolder() throws ProtocolException {
		if(this.pModeTranslatorPayloadProfilesFolder==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.pmode.pModeTranslatorPayloadProfilesFolder"); 
				
				if (value != null){
					value = value.trim();
					this.pModeTranslatorPayloadProfilesFolder = this.getDirectory(value);
				}else{
					throw new Exception("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.pmode.pModeTranslatorPayloadProfilesFolder' non impostata");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.pmode.pModeTranslatorPayloadProfilesFolder', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return this.pModeTranslatorPayloadProfilesFolder;
	}
	
	private File pModeTranslatorPropertiesFolder;
	public File getPModeTranslatorPropertiesFolder() throws ProtocolException {
		if(this.pModeTranslatorPropertiesFolder==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.pmode.pModeTranslatorPropertiesFolder"); 
				
				if (value != null){
					value = value.trim();
					this.pModeTranslatorPropertiesFolder = this.getDirectory(value);
				}else{
					throw new Exception("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.pmode.pModeTranslatorPropertiesFolder' non impostata");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.pmode.pModeTranslatorPropertiesFolder', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return this.pModeTranslatorPropertiesFolder;
	}


	
	
	/* **** Comunicazione HTTP verso Gateway **** */
	
	private Boolean isDomibusGatewayRegistry= null;
	private Boolean isDomibusGatewayRegistryRead= null;
    public Boolean isDomibusGatewayRegistry() throws ProtocolException{
    	if(this.isDomibusGatewayRegistryRead==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.domibusGateway.registry"); 
				
				if (value != null){
					value = value.trim();
					this.isDomibusGatewayRegistry = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' non impostata");
				}
				
				this.isDomibusGatewayRegistryRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusGateway.registry' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				this.isDomibusGatewayRegistryRead = true;
				throw new ProtocolException(e);
				
			}
    	}
    	
    	return this.isDomibusGatewayRegistry;
	}
	
	
	private String domibusGatewayRegistrySoggettoDefault;
	public String getDomibusGatewayRegistrySoggettoDefault() throws ProtocolException {
		if(this.domibusGatewayRegistrySoggettoDefault==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.domibusGateway.registry.soggetto.default"); 
				
				if (value != null){
					value = value.trim();
					this.domibusGatewayRegistrySoggettoDefault = value;
				}
				else {
					throw new Exception("Proprieta' non impostata");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusGateway.registry.soggetto.default', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return this.domibusGatewayRegistrySoggettoDefault;
	}
	
	private List<String> domibusGatewayRegistrySoggettoCustom_Read = new ArrayList<>();
	private HashMap<String,String> domibusGatewayRegistrySoggettoCustom = new HashMap<>();
	public String getDomibusGatewayRegistrySoggettoCustom(String nome) throws ProtocolException {
		if(this.domibusGatewayRegistrySoggettoCustom_Read.contains(nome)==false){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.domibusGateway.registry.soggetto."+nome); 
				
				if (value != null){
					value = value.trim();
					this.domibusGatewayRegistrySoggettoCustom.put(nome, value);
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusGateway.registry.soggetto."+nome+"', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}finally {
				this.domibusGatewayRegistrySoggettoCustom_Read.add(nome);
			}
    	}
		return this.domibusGatewayRegistrySoggettoCustom.get(nome);
	}
    
	private List<String> domibusGatewayRegistrySoggettoCustomList = new ArrayList<>();
	public List<String> getDomibusGatewayRegistrySoggettoCustomList() throws ProtocolException {
		if(this.domibusGatewayRegistrySoggettoCustomList==null){
	    	try{  
	    		Properties p = this.reader.readPropertiesConvertEnvProperties("org.openspcoop2.protocol.as4.domibusGateway.registry.soggetto.");
	    		this.domibusGatewayRegistrySoggettoCustomList = new ArrayList<>();
	    		Enumeration<?> keys = p.keys();
	    		while (keys.hasMoreElements()) {
					Object object = (Object) keys.nextElement();
					if(object instanceof String) {
						String key = (String) object;
						String value = p.getProperty(key);
						this.domibusGatewayRegistrySoggettoCustomList.add(value);
					}
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusGateway.registry.soggetto.*', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return this.domibusGatewayRegistrySoggettoCustomList;
	}
    
	
	private String domibusGatewayConfigDefaultUrl;
	public String getDomibusGatewayConfigDefaultUrl() throws ProtocolException {
		if(this.domibusGatewayConfigDefaultUrl==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.domibusGateway.config.default.url"); 
				
				if (value != null){
					value = value.trim();
					this.domibusGatewayConfigDefaultUrl = value;
				}
				else {
					throw new Exception("Proprieta' non impostata");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusGateway.config.default.url', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return this.domibusGatewayConfigDefaultUrl;
	}
	
	private Boolean isDomibusGatewayConfigDefaultHttpsEnabled= null;
	private Boolean isDomibusGatewayConfigDefaultHttpsEnabledRead= null;
    public Boolean isDomibusGatewayConfigDefaultHttpsEnabled(){
    	if(this.isDomibusGatewayConfigDefaultHttpsEnabledRead==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.domibusGateway.config.default.https.enabled"); 
				
				if (value != null){
					value = value.trim();
					this.isDomibusGatewayConfigDefaultHttpsEnabled = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusGateway.config.default.https.enabled' non impostata, viene utilizzato il default=false");
					this.isDomibusGatewayConfigDefaultHttpsEnabled = false;
				}
				
				this.isDomibusGatewayConfigDefaultHttpsEnabledRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusGateway.config.default.https.enabled' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				this.isDomibusGatewayConfigDefaultHttpsEnabled = false;
				
				this.isDomibusGatewayConfigDefaultHttpsEnabledRead = true;
			}
    	}
    	
    	return this.isDomibusGatewayConfigDefaultHttpsEnabled;
	}
    
    private Properties domibusGatewayConfigDefaultHttpsProperties;
	public Properties getDomibusGatewayConfigDefaultHttpsProperties() throws ProtocolException {
		if(this.domibusGatewayConfigDefaultHttpsProperties==null){
	    	try{  
	    		Properties p = this.reader.readPropertiesConvertEnvProperties("org.openspcoop2.protocol.as4.domibusGateway.config.default.https.property.");
				
				if (p != null && p.size()>0){
					this.domibusGatewayConfigDefaultHttpsProperties = p;
				}
				else {
					throw new Exception("Proprieta' non impostate");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusGateway.config.default.https.property.*', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return this.domibusGatewayConfigDefaultHttpsProperties;
	}
	
	private List<String> domibusGatewayConfigCustomUrl_Read = new ArrayList<>();
	private HashMap<String,String> domibusGatewayConfigCustomUrl = new HashMap<>();
	public String getDomibusGatewayConfigCustomUrl(String nome) throws ProtocolException {
		if(this.domibusGatewayConfigCustomUrl_Read.contains(nome)==false){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.domibusGateway.config."+nome+".url"); 
				
				if (value != null){
					value = value.trim();
					this.domibusGatewayConfigCustomUrl.put(nome, value);
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusGateway.config."+nome+".url', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}finally {
				this.domibusGatewayConfigCustomUrl_Read.add(nome);
			}
    	}
		return this.domibusGatewayConfigCustomUrl.get(nome);
	}
	
	private List<String> domibusGatewayConfigCustomHttsEnabled_Read = new ArrayList<>();
	private HashMap<String,Boolean> domibusGatewayConfigCustomHttpsEnabled = new HashMap<String,Boolean>();
    public Boolean isDomibusGatewayConfigCustomHttpsEnabled(String nome) throws ProtocolException{
    	if(this.domibusGatewayConfigCustomHttsEnabled_Read.contains(nome)==false){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.domibusGateway.config."+nome+".https.enabled"); 
				
				if (value != null){
					value = value.trim();
					this.domibusGatewayConfigCustomHttpsEnabled.put(nome, Boolean.parseBoolean(value));
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusGateway.config."+nome+".https.enabled' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
	    	finally {
	    		this.domibusGatewayConfigCustomHttsEnabled_Read.add(nome);
	    	}
    	}
    	
    	return this.domibusGatewayConfigCustomHttpsEnabled.get(nome);
	}
    
    private List<String> domibusGatewayConfigCustomHttsProperties_Read = new ArrayList<>();
	private HashMap<String,Properties> domibusGatewayConfigCustomHttpsProperties = new HashMap<String,Properties>();
	public Properties getDomibusGatewayConfigCustomHttpsProperties(String nome) throws ProtocolException {
		if(this.domibusGatewayConfigCustomHttsProperties_Read.contains(nome)==false){
	    	try{  
	    		Properties p = this.reader.readPropertiesConvertEnvProperties("org.openspcoop2.protocol.as4.domibusGateway.config."+nome+".https.property.");
				
				if (p != null && p.size()>0){
					this.domibusGatewayConfigCustomHttpsProperties.put(nome, p);
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusGateway.config."+nome+".https.property.*', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
	    	finally {
	    		this.domibusGatewayConfigCustomHttsProperties_Read.add(nome);
	    	}
    	}
		return this.domibusGatewayConfigCustomHttpsProperties.get(nome);
	}
	

	
	
	
	
	/* **** Comunicazione JMS verso Broker **** */
	
	private Boolean domibusGatewayJMS_debug= null;
	private Boolean domibusGatewayJMS_debugRead= null;
    public Boolean isDomibusGatewayJMS_debug(){
    	if(this.domibusGatewayJMS_debugRead==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.domibusJms.debug"); 
				
				if (value != null){
					value = value.trim();
					this.domibusGatewayJMS_debug = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusJms.debug' non impostata, viene utilizzato il default=false");
					this.domibusGatewayJMS_debug = false;
				}
				
				this.domibusGatewayJMS_debugRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusJms.debug' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				this.isDomibusGatewayConfigDefaultHttpsEnabled = false;
				
				this.domibusGatewayJMS_debug = false;
			}
    	}
    	
    	return this.domibusGatewayJMS_debug;
	}
	
	private Integer domibusGatewayJMS_threadsPoolSize;
	public Integer getDomibusGatewayJMS_threadsPoolSize() throws ProtocolException {
		if(this.domibusGatewayJMS_threadsPoolSize==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.domibusJms.threadsPool.size"); 
				
				if (value != null){
					value = value.trim();
					this.domibusGatewayJMS_threadsPoolSize = Integer.parseInt(value);
				}
				else {
					throw new Exception("Proprieta' non impostata");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusJms.threadsPool.size', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return this.domibusGatewayJMS_threadsPoolSize;
	}
	
	private Integer domibusGatewayJMS_threadCheckIntervalMs;
	public Integer getDomibusGatewayJMS_threadCheckIntervalMs() throws ProtocolException {
		if(this.domibusGatewayJMS_threadCheckIntervalMs==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.domibusJms.thread.checkIntervalMs"); 
				
				if (value != null){
					value = value.trim();
					this.domibusGatewayJMS_threadCheckIntervalMs = Integer.parseInt(value);
				}
				else {
					throw new Exception("Proprieta' non impostata");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusJms.thread.checkIntervalMs', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return this.domibusGatewayJMS_threadCheckIntervalMs;
	}
	
	private Properties domibusGatewayJMS_jndiContext = null;
	public Properties getDomibusGatewayJMS_jndiContext() throws ProtocolException {
		if(this.domibusGatewayJMS_jndiContext==null){
	    	try{  
	    		this.domibusGatewayJMS_jndiContext = this.reader.readPropertiesConvertEnvProperties("org.openspcoop2.protocol.as4.domibusJms.jndi.");
	    		if (this.domibusGatewayJMS_jndiContext == null || this.domibusGatewayJMS_jndiContext.size()<0){
	    			this.domibusGatewayJMS_jndiContext = new Properties(); // context jndi vuoto
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusJms.jndi.*', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return this.domibusGatewayJMS_jndiContext;
	}
	
	private String domibusGatewayJMS_connectionFactory;
	public String getDomibusGatewayJMS_connectionFactory() throws ProtocolException {
		if(this.domibusGatewayJMS_connectionFactory==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.domibusJms.connectionFactory"); 
				
				if (value != null){
					value = value.trim();
					this.domibusGatewayJMS_connectionFactory = value;
				}
				else {
					throw new Exception("Proprieta' non impostata");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusJms.connectionFactory', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return this.domibusGatewayJMS_connectionFactory;
	}
	
	private Boolean domibusGatewayJMS_username_read;
	private String domibusGatewayJMS_username;
	public String getDomibusGatewayJMS_username() throws ProtocolException {
		if(this.domibusGatewayJMS_username_read==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.domibusJms.username"); 
				
				if (value != null){
					value = value.trim();
					this.domibusGatewayJMS_username = value;
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusJms.username', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}finally {
				this.domibusGatewayJMS_username_read = true;
			}
    	}
		return this.domibusGatewayJMS_username;
	}
	
	private Boolean domibusGatewayJMS_password_read;
	private String domibusGatewayJMS_password;
	public String getDomibusGatewayJMS_password() throws ProtocolException {
		if(this.domibusGatewayJMS_password_read==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.domibusJms.password"); 
				
				if (value != null){
					value = value.trim();
					this.domibusGatewayJMS_password = value;
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusJms.password', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}finally {
				this.domibusGatewayJMS_password_read = true;
			}
    	}
		return this.domibusGatewayJMS_password;
	}
	
	private String domibusGatewayJMS_queueReceiver;
	public String getDomibusGatewayJMS_queueReceiver() throws ProtocolException {
		if(this.domibusGatewayJMS_queueReceiver==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.domibusJms.queue.receiver"); 
				
				if (value != null){
					value = value.trim();
					this.domibusGatewayJMS_queueReceiver = value;
				}
				else {
					throw new Exception("Proprieta' non impostata");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusJms.queue.receiver', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return this.domibusGatewayJMS_queueReceiver;
	}
	
	private String domibusGatewayJMS_queueSender;
	public String getDomibusGatewayJMS_queueSender() throws ProtocolException {
		if(this.domibusGatewayJMS_queueSender==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.domibusJms.queue.sender"); 
				
				if (value != null){
					value = value.trim();
					this.domibusGatewayJMS_queueSender = value;
				}
				else {
					throw new Exception("Proprieta' non impostata");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusJms.queue.sender', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return this.domibusGatewayJMS_queueSender;
	}
	
	private Boolean domibusGatewayJMS_AckProviderUrl_read;
	private String domibusGatewayJMS_AckProviderUrl;
	private String getDomibusGatewayJMS_AckProviderUrl() throws ProtocolException {
		if(this.domibusGatewayJMS_AckProviderUrl_read==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.domibusJms.queue.sender.provider.url"); 
				
				if (value != null){
					value = value.trim();
					this.domibusGatewayJMS_AckProviderUrl = value;
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.domibusJms.queue.sender.provider.url', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}finally {
				this.domibusGatewayJMS_AckProviderUrl_read = true;
			}
    	}
		return this.domibusGatewayJMS_AckProviderUrl;
	}
	
	private Properties domibusGatewayJMS_jndiContextForAck = null;
	public Properties getDomibusGatewayJMS_jndiContextForAck() throws ProtocolException {
		if(this.domibusGatewayJMS_jndiContextForAck==null){
			if(this.getDomibusGatewayJMS_AckProviderUrl()!=null) {
				this.domibusGatewayJMS_jndiContextForAck = new Properties();
				Properties p = this.getDomibusGatewayJMS_jndiContext();
				Enumeration<?> keys = p.keys();
				while (keys.hasMoreElements()) {
					Object object = (Object) keys.nextElement();
					if(object instanceof String) {
						String key = (String) object;
						if(!"java.naming.provider.url".equalsIgnoreCase(key)) {
							this.domibusGatewayJMS_jndiContextForAck.put(key, p.get(key));
						}
					}
				}
				this.domibusGatewayJMS_jndiContextForAck.put("java.naming.provider.url", this.getDomibusGatewayJMS_AckProviderUrl());
			}
			else {
				this.domibusGatewayJMS_jndiContextForAck = this.getDomibusGatewayJMS_jndiContext();
			}
			
    	}
		return this.domibusGatewayJMS_jndiContextForAck;
	}
	
	
	
	
	
	
	
	/* **** Tracciamento delle Notifiche **** */
	
	private Boolean isAckTraceEnabled= null;
    public Boolean isAckTraceEnabled(){
    	if(this.isAckTraceEnabled==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.ack.trace.enabled"); 
				
				if (value != null){
					value = value.trim();
					this.isAckTraceEnabled = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.ack.trace.enabled' non impostata, viene utilizzato il default=true");
					this.isAckTraceEnabled = true;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.ack.trace.enabled' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				this.isAckTraceEnabled = true;
			}
    	}
    	
    	return this.isAckTraceEnabled;
	}
    
    private Boolean ackTraceDatasource_read;
	private String ackTraceDatasource;
	public String getAckTraceDatasource() throws ProtocolException {
		if(this.ackTraceDatasource_read==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.ack.trace.dataSource"); 
				
				if (value != null){
					value = value.trim();
					this.ackTraceDatasource = value;
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.ack.trace.dataSource', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}finally {
				this.ackTraceDatasource_read = true;
			}
    	}
		return this.ackTraceDatasource;
	}
	
	private Boolean ackTraceTipoDatabase_read;
	private String ackTraceTipoDatabase;
	public String getAckTraceTipoDatabase() throws ProtocolException {
		if(this.ackTraceTipoDatabase_read==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.ack.trace.tipoDatabase"); 
				
				if (value != null){
					value = value.trim();
					this.ackTraceTipoDatabase = value;
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.ack.trace.tipoDatabase', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}finally {
				this.ackTraceTipoDatabase_read = true;
			}
    	}
		return this.ackTraceTipoDatabase;
	}
	
	private Properties ackTraceDatasource_jndiContext = null;
	public Properties getAckTraceDatasource_jndiContext() throws ProtocolException {
		if(this.ackTraceDatasource_jndiContext==null){
	    	try{  
	    		this.ackTraceDatasource_jndiContext = this.reader.readPropertiesConvertEnvProperties("org.openspcoop2.protocol.as4.ack.trace.dataSource.property.");
	    		if (this.ackTraceDatasource_jndiContext == null || this.ackTraceDatasource_jndiContext.size()<0){
	    			this.ackTraceDatasource_jndiContext = new Properties(); // context jndi vuoto
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.ack.trace.dataSource.property.*', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return this.ackTraceDatasource_jndiContext;
	}
	
	
	private Boolean ackDomibusDatasource_read;
	private String ackDomibusDatasource;
	public String getAckDomibusDatasource() throws ProtocolException {
		if(this.ackDomibusDatasource_read==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.ack.domibus.dataSource"); 
				
				if (value != null){
					value = value.trim();
					this.ackDomibusDatasource = value;
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.ack.domibus.dataSource', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}finally {
				this.ackDomibusDatasource_read = true;
			}
    	}
		return this.ackDomibusDatasource;
	}
	
	private Boolean ackDomibusTipoDatabase_read;
	private String ackDomibusTipoDatabase;
	public String getAckDomibusTipoDatabase() throws ProtocolException {
		if(this.ackDomibusTipoDatabase_read==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.as4.ack.domibus.tipoDatabase"); 
				
				if (value != null){
					value = value.trim();
					this.ackDomibusTipoDatabase = value;
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.ack.domibus.tipoDatabase', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}finally {
				this.ackDomibusTipoDatabase_read = true;
			}
    	}
		return this.ackDomibusTipoDatabase;
	}
	
	private Properties ackDomibusDatasource_jndiContext = null;
	public Properties getAckDomibusDatasource_jndiContext() throws ProtocolException {
		if(this.ackDomibusDatasource_jndiContext==null){
	    	try{  
	    		this.ackDomibusDatasource_jndiContext = this.reader.readPropertiesConvertEnvProperties("org.openspcoop2.protocol.as4.ack.domibus.dataSource.property.");
	    		if (this.ackDomibusDatasource_jndiContext == null || this.ackDomibusDatasource_jndiContext.size()<0){
	    			this.ackDomibusDatasource_jndiContext = new Properties(); // context jndi vuoto
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.as4.ack.domibus.dataSource.property.*', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return this.ackDomibusDatasource_jndiContext;
	}
	
	
	
	private Boolean useConfigStaticInstance = null;
	private Boolean useConfigStaticInstance(){
		if(this.useConfigStaticInstance==null){
			
			Boolean defaultValue = true;
			String propertyName = "org.openspcoop2.protocol.as4.factory.config.staticInstance";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.useConfigStaticInstance = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue);
					this.useConfigStaticInstance = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.log.debug("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue+", errore:"+e.getMessage());
				this.useConfigStaticInstance = defaultValue;
			}
		}

		return this.useConfigStaticInstance;
	}
	
	private Boolean useErroreApplicativoStaticInstance = null;
	private Boolean useErroreApplicativoStaticInstance(){
		if(this.useErroreApplicativoStaticInstance==null){
			
			Boolean defaultValue = true;
			String propertyName = "org.openspcoop2.protocol.as4.factory.erroreApplicativo.staticInstance";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.useErroreApplicativoStaticInstance = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue);
					this.useErroreApplicativoStaticInstance = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.log.debug("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue+", errore:"+e.getMessage());
				this.useErroreApplicativoStaticInstance = defaultValue;
			}
		}

		return this.useErroreApplicativoStaticInstance;
	}
	
	private Boolean useEsitoStaticInstance = null;
	private Boolean useEsitoStaticInstance(){
		if(this.useEsitoStaticInstance==null){
			
			Boolean defaultValue = true;
			String propertyName = "org.openspcoop2.protocol.as4.factory.esito.staticInstance";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.useEsitoStaticInstance = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue);
					this.useEsitoStaticInstance = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.log.debug("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue+", errore:"+e.getMessage());
				this.useEsitoStaticInstance = defaultValue;
			}
		}

		return this.useEsitoStaticInstance;
	}
	
	private BasicStaticInstanceConfig staticInstanceConfig = null;
	public BasicStaticInstanceConfig getStaticInstanceConfig(){
		if(this.staticInstanceConfig==null){
			this.staticInstanceConfig = new BasicStaticInstanceConfig();
			if(useConfigStaticInstance()!=null) {
				this.staticInstanceConfig.setStaticConfig(useConfigStaticInstance());
			}
			if(useErroreApplicativoStaticInstance()!=null) {
				this.staticInstanceConfig.setStaticErrorBuilder(useErroreApplicativoStaticInstance());
			}
			if(useEsitoStaticInstance()!=null) {
				this.staticInstanceConfig.setStaticEsitoBuilder(useEsitoStaticInstance());
			}
		}
		return this.staticInstanceConfig;
	}
}
