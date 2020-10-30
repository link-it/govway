/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.modipa.config;

import java.util.List;
import java.util.Properties;

import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.modipa.utils.ModISecurityConfig;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.Loader;
import org.slf4j.Logger;

/**
 * Classe che gestisce il file di properties 'modipa.properties' del protocollo ModI
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIProperties {

	/** Logger utilizzato per errori eventuali. */
	private Logger log = null;


	/** Copia Statica */
	private static ModIProperties modipaProperties = null;

	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'modipa.properties' */
	private ModIInstanceProperties reader;





	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	public ModIProperties(String confDir,Logger log) throws ProtocolException{

		if(log != null)
			this.log = log;
		else
			this.log = LoggerWrapperFactory.getLogger("ModIProperties");

		/* ---- Lettura del cammino del file di configurazione ---- */

		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = ModIProperties.class.getResourceAsStream("/modipa.properties");
			if(properties==null){
				throw new Exception("File '/modipa.properties' not found");
			}
			propertiesReader.load(properties);
		}catch(Exception e) {
			this.log.error("Riscontrato errore durante la lettura del file 'modipa.properties': "+e.getMessage());
			throw new ProtocolException("ModIProperties initialize error: "+e.getMessage(),e);
		}finally{
			try{
				if(properties!=null)
					properties.close();
			}catch(Exception er){}
		}
		try{
			this.reader = new ModIInstanceProperties(propertiesReader, this.log);
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

		if(ModIProperties.modipaProperties==null)
			ModIProperties.modipaProperties = new ModIProperties(confDir,log);	

	}

	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di ModIProperties
	 * @throws Exception 
	 * 
	 */
	public static ModIProperties getInstance() throws ProtocolException{

		if(ModIProperties.modipaProperties==null)
			throw new ProtocolException("ModIProperties not initialized (use init method in factory)");

		return ModIProperties.modipaProperties;
	}




	public void validaConfigurazione(Loader loader) throws ProtocolException  {	
		try{  

			generateIDasUUID();
			
			/* **** TRUST STORE **** */
			
			if(getSicurezzaMessaggio_certificati_trustStore_path()!=null) {
				getSicurezzaMessaggio_certificati_trustStore_tipo();
				getSicurezzaMessaggio_certificati_trustStore_password();
				getSicurezzaMessaggio_certificati_trustStore_crls();
			}
			
			if(getSicurezzaMessaggio_ssl_trustStore_path()!=null) {
				getSicurezzaMessaggio_ssl_trustStore_tipo();
				getSicurezzaMessaggio_ssl_trustStore_password();
				getSicurezzaMessaggio_ssl_trustStore_crls();
			}
			
			/* **** KEY STORE **** */
			
			if(getSicurezzaMessaggio_certificati_keyStore_path()!=null) {
				getSicurezzaMessaggio_certificati_keyStore_tipo();
				getSicurezzaMessaggio_certificati_keyStore_password();
				getSicurezzaMessaggio_certificati_key_alias();
				getSicurezzaMessaggio_certificati_key_password();
			}
			
			/* **** CORNICE SICUREZZA **** */
			
			if(isSicurezzaMessaggio_corniceSicurezza_enabled()) {
				getSicurezzaMessaggio_corniceSicurezza_rest_codice_ente();
				getSicurezzaMessaggio_corniceSicurezza_rest_user();
				getSicurezzaMessaggio_corniceSicurezza_rest_ipuser();	
				getSicurezzaMessaggio_corniceSicurezza_soap_codice_ente();
				getSicurezzaMessaggio_corniceSicurezza_soap_user();
				getSicurezzaMessaggio_corniceSicurezza_soap_ipuser();	
				getSicurezzaMessaggio_corniceSicurezza_dynamic_codice_ente();
				getSicurezzaMessaggio_corniceSicurezza_dynamic_user();
				getSicurezzaMessaggio_corniceSicurezza_dynamic_ipuser();	
			}
			
			/* **** TRACCE **** */ 
			
			this.isGenerazioneTracce();
			
			/* **** REST **** */ 
			
			getRestSecurityTokenHeader();
			if(isRestSecurityTokenClaimsIssuerEnabled()) {
				getRestSecurityTokenClaimsIssuerHeaderValue();
			}
			if(isRestSecurityTokenClaimsSubjectEnabled()) {
				getRestSecurityTokenClaimsSubjectHeaderValue();
			}
			getRestSecurityTokenClaimsClientIdHeader();
			getRestSecurityTokenClaimSignedHeaders();
			getRestSecurityTokenClaimRequestDigest();
			getRestSecurityTokenSignedHeaders();
			getRestSecurityTokenClaimsIatTimeCheck_milliseconds();
			isRestSecurityTokenRequestDigestClean();
			isRestSecurityTokenResponseDigestClean();
			isRestSecurityTokenResponseDigestHEADuseServerHeader();
			isRestSecurityTokenFaultProcessEnabled();
			getRestCorrelationIdHeader();
			getRestReplyToHeader();
			getRestLocationHeader();
			
			// .. PUSH ..
			isRestSecurityTokenPushReplyToUpdateOrCreateInFruizione();
			isRestSecurityTokenPushReplyToUpdateInErogazione();
			isRestSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists();
			getRestSecurityTokenPushRequestHttpStatus();
			getRestSecurityTokenPushResponseHttpStatus();
			
			// .. PULL ..
			getRestSecurityTokenPullRequestHttpStatus();
			getRestSecurityTokenPullRequestStateNotReadyHttpStatus();
			getRestSecurityTokenPullRequestStateOkHttpStatus();
			getRestSecurityTokenPullResponseHttpStatus();
			
			/* **** SOAP **** */
			
			isSoapSecurityTokenMustUnderstand();
			getSoapSecurityTokenActor();
			getSoapSecurityTokenTimestampCreatedTimeCheck_milliseconds();
			isSoapSecurityTokenFaultProcessEnabled();
			
			isSoapWSAddressingMustUnderstand();
			getSoapWSAddressingActor();
			
			getSoapCorrelationIdName();
			getSoapCorrelationIdNamespace();
			getSoapCorrelationIdPrefix();
			useSoapBodyCorrelationIdNamespace();
			isSoapCorrelationIdMustUnderstand();
			getSoapCorrelationIdActor();
			
			getSoapReplyToName();
			getSoapReplyToNamespace();
			getSoapReplyToPrefix();
			useSoapBodyReplyToNamespace();
			isSoapReplyToMustUnderstand();
			getSoapReplyToActor();
			
			getSoapRequestDigestName();
			getSoapRequestDigestNamespace();
			getSoapRequestDigestPrefix();
			useSoapBodyRequestDigestNamespace();
			isSoapRequestDigestMustUnderstand();
			getSoapRequestDigestActor();
			
			// .. PUSH ..
			isSoapSecurityTokenPushReplyToUpdateOrCreateInFruizione();
			isSoapSecurityTokenPushReplyToUpdateInErogazione();
			isSoapSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists();
			
			/* **** CONFIGURAZIONE **** */
			
			isRiferimentoIDRichiesta_PD_Required();
			isRiferimentoIDRichiesta_PA_Required();
			
			/* **** SOAP FAULT (Generati dagli attori esterni) **** */
			
			this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo();
			this.isAggiungiDetailErroreApplicativo_SoapFaultPdD();
			this.isGenerazioneDetailsSOAPFaultProtocolValidazione();
			this.isGenerazioneDetailsSOAPFaultProtocolProcessamento();
			this.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace();
			this.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche();
			
			 /* **** SOAP FAULT (Integrazione, Porta Delegata) **** */
			
			this.isGenerazioneDetailsSOAPFaultIntegrationServerError();
			this.isGenerazioneDetailsSOAPFaultIntegrationClientError();
			this.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace();
			this.isGenerazioneDetailsSOAPFaultIntegrazionConInformazioniGeneriche();
			
			/* **** SOAP FAULT (Protocollo, Porta Applicativa) **** */
			
			this.isPortaApplicativaBustaErrore_personalizzaElementiFault();
			this.isPortaApplicativaBustaErrore_aggiungiErroreApplicativo();
			
		}catch(java.lang.Exception e) {
			String msg = "Riscontrato errore durante la validazione della proprieta' del protocollo modipa, "+e.getMessage();
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
		if(ModIProperties.generateIDasUUID==null){
			
			Boolean defaultValue = true;
			String propertyName = "org.openspcoop2.protocol.modipa.id.uuid";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					ModIProperties.generateIDasUUID = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprietà '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue);
					ModIProperties.generateIDasUUID = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue+", errore:"+e.getMessage());
				ModIProperties.generateIDasUUID = defaultValue;
			}
		}

		return ModIProperties.generateIDasUUID;
	}
	
	
	
	
	
	
	
	/* **** TRUST STORE **** */
	
	private static String sicurezzaMessaggio_certificati_trustStore_path= null;
	private static Boolean sicurezzaMessaggio_certificati_trustStore_path_read= null;
	public String getSicurezzaMessaggio_certificati_trustStore_path(){
    	if(ModIProperties.sicurezzaMessaggio_certificati_trustStore_path_read==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.trustStore.path"); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.sicurezzaMessaggio_certificati_trustStore_path = value;
				}
				
				ModIProperties.sicurezzaMessaggio_certificati_trustStore_path_read = true;
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.trustStore.path' non impostata, errore:"+e.getMessage());
				ModIProperties.sicurezzaMessaggio_certificati_trustStore_path_read = true;
			}
    	}
    	
    	return ModIProperties.sicurezzaMessaggio_certificati_trustStore_path;
	}
	
	private static String sicurezzaMessaggio_certificati_trustStore_tipo= null;
	public String getSicurezzaMessaggio_certificati_trustStore_tipo() throws Exception{
    	if(ModIProperties.sicurezzaMessaggio_certificati_trustStore_tipo==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.trustStore.tipo"); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.sicurezzaMessaggio_certificati_trustStore_tipo = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.trustStore.tipo' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return ModIProperties.sicurezzaMessaggio_certificati_trustStore_tipo;
	}	
	
	private static String sicurezzaMessaggio_certificati_trustStore_password= null;
	public String getSicurezzaMessaggio_certificati_trustStore_password() throws Exception{
    	if(ModIProperties.sicurezzaMessaggio_certificati_trustStore_password==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.trustStore.password"); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.sicurezzaMessaggio_certificati_trustStore_password = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.trustStore.password' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return ModIProperties.sicurezzaMessaggio_certificati_trustStore_password;
	}	
	
	private static Boolean sicurezzaMessaggio_certificati_trustStore_crls_read= null;
	private static String sicurezzaMessaggio_certificati_trustStore_crls= null;
	public String getSicurezzaMessaggio_certificati_trustStore_crls() throws Exception{
    	if(ModIProperties.sicurezzaMessaggio_certificati_trustStore_crls_read==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.trustStore.crls"); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.sicurezzaMessaggio_certificati_trustStore_crls = value;
				}
				
				sicurezzaMessaggio_certificati_trustStore_crls_read = true;
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.trustStore.password' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return ModIProperties.sicurezzaMessaggio_certificati_trustStore_crls;
	}	
	
	
	
	private static String sicurezzaMessaggio_ssl_trustStore_path= null;
	private static Boolean sicurezzaMessaggio_ssl_trustStore_path_read= null;
	public String getSicurezzaMessaggio_ssl_trustStore_path(){
    	if(ModIProperties.sicurezzaMessaggio_ssl_trustStore_path_read==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.ssl.trustStore.path"); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.sicurezzaMessaggio_ssl_trustStore_path = value;
				}
				
				ModIProperties.sicurezzaMessaggio_ssl_trustStore_path_read = true;
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.ssl.trustStore.path' non impostata, errore:"+e.getMessage());
				ModIProperties.sicurezzaMessaggio_ssl_trustStore_path_read = true;
			}
    	}
    	
    	return ModIProperties.sicurezzaMessaggio_ssl_trustStore_path;
	}
	
	private static String sicurezzaMessaggio_ssl_trustStore_tipo= null;
	public String getSicurezzaMessaggio_ssl_trustStore_tipo() throws Exception{
    	if(ModIProperties.sicurezzaMessaggio_ssl_trustStore_tipo==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.ssl.trustStore.tipo"); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.sicurezzaMessaggio_ssl_trustStore_tipo = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.ssl.trustStore.tipo' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return ModIProperties.sicurezzaMessaggio_ssl_trustStore_tipo;
	}	
	
	private static String sicurezzaMessaggio_ssl_trustStore_password= null;
	public String getSicurezzaMessaggio_ssl_trustStore_password() throws Exception{
    	if(ModIProperties.sicurezzaMessaggio_ssl_trustStore_password==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.ssl.trustStore.password"); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.sicurezzaMessaggio_ssl_trustStore_password = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.ssl.trustStore.password' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return ModIProperties.sicurezzaMessaggio_ssl_trustStore_password;
	}	
	
	private static Boolean sicurezzaMessaggio_ssl_trustStore_crls_read= null;
	private static String sicurezzaMessaggio_ssl_trustStore_crls= null;
	public String getSicurezzaMessaggio_ssl_trustStore_crls() throws Exception{
    	if(ModIProperties.sicurezzaMessaggio_ssl_trustStore_crls_read==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.ssl.trustStore.crls"); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.sicurezzaMessaggio_ssl_trustStore_crls = value;
				}
				
				sicurezzaMessaggio_ssl_trustStore_crls_read = true;
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.ssl.trustStore.password' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return ModIProperties.sicurezzaMessaggio_ssl_trustStore_crls;
	}
	
	
	
	
	
	
	
	/* **** KEY STORE **** */
	
	private static String sicurezzaMessaggio_certificati_keyStore_path= null;
	private static Boolean sicurezzaMessaggio_certificati_keyStore_path_read= null;
	public String getSicurezzaMessaggio_certificati_keyStore_path(){
    	if(ModIProperties.sicurezzaMessaggio_certificati_keyStore_path_read==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.keyStore.path"); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.sicurezzaMessaggio_certificati_keyStore_path = value;
				}
				
				ModIProperties.sicurezzaMessaggio_certificati_keyStore_path_read = true;
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.keyStore.path' non impostata, errore:"+e.getMessage());
				ModIProperties.sicurezzaMessaggio_certificati_keyStore_path_read = true;
			}
    	}
    	
    	return ModIProperties.sicurezzaMessaggio_certificati_keyStore_path;
	}
	
	private static String sicurezzaMessaggio_certificati_keyStore_tipo= null;
	public String getSicurezzaMessaggio_certificati_keyStore_tipo() throws Exception{
    	if(ModIProperties.sicurezzaMessaggio_certificati_keyStore_tipo==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.keyStore.tipo"); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.sicurezzaMessaggio_certificati_keyStore_tipo = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.keyStore.tipo' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return ModIProperties.sicurezzaMessaggio_certificati_keyStore_tipo;
	}	
	
	private static String sicurezzaMessaggio_certificati_keyStore_password= null;
	public String getSicurezzaMessaggio_certificati_keyStore_password() throws Exception{
    	if(ModIProperties.sicurezzaMessaggio_certificati_keyStore_password==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.keyStore.password"); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.sicurezzaMessaggio_certificati_keyStore_password = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.keyStore.password' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return ModIProperties.sicurezzaMessaggio_certificati_keyStore_password;
	}	
	
	private static String sicurezzaMessaggio_certificati_key_alias= null;
	public String getSicurezzaMessaggio_certificati_key_alias() throws Exception{
    	if(ModIProperties.sicurezzaMessaggio_certificati_key_alias==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.key.alias"); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.sicurezzaMessaggio_certificati_key_alias = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.key.alias' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return ModIProperties.sicurezzaMessaggio_certificati_key_alias;
	}	
	
	private static String sicurezzaMessaggio_certificati_key_password= null;
	public String getSicurezzaMessaggio_certificati_key_password() throws Exception{
    	if(ModIProperties.sicurezzaMessaggio_certificati_key_password==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.key.password"); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.sicurezzaMessaggio_certificati_key_password = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.key.password' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return ModIProperties.sicurezzaMessaggio_certificati_key_password;
	}	
	
	
	
	
	/* **** CORNICE SICUREZZA **** */
	
	private static Boolean isSicurezzaMessaggio_corniceSicurezza_enabled = null;
	public Boolean isSicurezzaMessaggio_corniceSicurezza_enabled(){
		if(ModIProperties.isSicurezzaMessaggio_corniceSicurezza_enabled==null){
			
			Boolean defaultValue = false;
			String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					ModIProperties.isSicurezzaMessaggio_corniceSicurezza_enabled = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprietà '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue);
					ModIProperties.isSicurezzaMessaggio_corniceSicurezza_enabled = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.log.debug("Proprietà '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue+", errore:"+e.getMessage());
				ModIProperties.isSicurezzaMessaggio_corniceSicurezza_enabled = defaultValue;
			}
		}

		return ModIProperties.isSicurezzaMessaggio_corniceSicurezza_enabled;
	}
	
	private static String sicurezzaMessaggio_corniceSicurezza_rest_codice_ente= null;
	public String getSicurezzaMessaggio_corniceSicurezza_rest_codice_ente() throws Exception{
    	if(ModIProperties.sicurezzaMessaggio_corniceSicurezza_rest_codice_ente==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza.rest.codice_ente";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.sicurezzaMessaggio_corniceSicurezza_rest_codice_ente = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà '"+propertyName+"' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return ModIProperties.sicurezzaMessaggio_corniceSicurezza_rest_codice_ente;
	}
	
	private static String sicurezzaMessaggio_corniceSicurezza_rest_user= null;
	public String getSicurezzaMessaggio_corniceSicurezza_rest_user() throws Exception{
    	if(ModIProperties.sicurezzaMessaggio_corniceSicurezza_rest_user==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza.rest.user";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.sicurezzaMessaggio_corniceSicurezza_rest_user = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà '"+propertyName+"' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return ModIProperties.sicurezzaMessaggio_corniceSicurezza_rest_user;
	}
	
	private static String sicurezzaMessaggio_corniceSicurezza_rest_ipuser= null;
	public String getSicurezzaMessaggio_corniceSicurezza_rest_ipuser() throws Exception{
    	if(ModIProperties.sicurezzaMessaggio_corniceSicurezza_rest_ipuser==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza.rest.ipuser";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.sicurezzaMessaggio_corniceSicurezza_rest_ipuser = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà '"+propertyName+"' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return ModIProperties.sicurezzaMessaggio_corniceSicurezza_rest_ipuser;
	}
	
	private static String sicurezzaMessaggio_corniceSicurezza_soap_codice_ente= null;
	private static Boolean sicurezzaMessaggio_corniceSicurezza_soap_codice_ente_read= null;
	public String getSicurezzaMessaggio_corniceSicurezza_soap_codice_ente() throws Exception{
    	if(ModIProperties.sicurezzaMessaggio_corniceSicurezza_soap_codice_ente_read==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza.soap.codice_ente";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.sicurezzaMessaggio_corniceSicurezza_soap_codice_ente = value;
				}
				// In soap il codice utente viene inserito anche in saml2:Subject
//				else {
//					throw new Exception("non definita");
//				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà '"+propertyName+"' non impostata, errore:"+e.getMessage());
				throw e;
			}
    		
    		sicurezzaMessaggio_corniceSicurezza_soap_codice_ente_read = true;
    	}
    	
    	return ModIProperties.sicurezzaMessaggio_corniceSicurezza_soap_codice_ente;
	}
	
	private static String sicurezzaMessaggio_corniceSicurezza_soap_user= null;
	public String getSicurezzaMessaggio_corniceSicurezza_soap_user() throws Exception{
    	if(ModIProperties.sicurezzaMessaggio_corniceSicurezza_soap_user==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza.soap.user";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.sicurezzaMessaggio_corniceSicurezza_soap_user = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà '"+propertyName+"' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return ModIProperties.sicurezzaMessaggio_corniceSicurezza_soap_user;
	}
	
	private static String sicurezzaMessaggio_corniceSicurezza_soap_ipuser= null;
	public String getSicurezzaMessaggio_corniceSicurezza_soap_ipuser() throws Exception{
    	if(ModIProperties.sicurezzaMessaggio_corniceSicurezza_soap_ipuser==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza.soap.ipuser";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.sicurezzaMessaggio_corniceSicurezza_soap_ipuser = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà '"+propertyName+"' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return ModIProperties.sicurezzaMessaggio_corniceSicurezza_soap_ipuser;
	}
	
	private static List<String> sicurezzaMessaggio_corniceSicurezza_dynamic_codice_ente= null;
	public List<String> getSicurezzaMessaggio_corniceSicurezza_dynamic_codice_ente() throws Exception{
    	if(ModIProperties.sicurezzaMessaggio_corniceSicurezza_dynamic_codice_ente==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza.codice_ente";
    		try{  
				//String value = this.reader.getValue_convertEnvProperties(propertyName);
    			String value = this.reader.getValue(propertyName); // contiene ${} da non risolvere
				ModIProperties.sicurezzaMessaggio_corniceSicurezza_dynamic_codice_ente = ModISecurityConfig.convertToList(value);
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà '"+propertyName+"' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return ModIProperties.sicurezzaMessaggio_corniceSicurezza_dynamic_codice_ente;
	}
	
	private static List<String> sicurezzaMessaggio_corniceSicurezza_dynamic_user= null;
	public List<String> getSicurezzaMessaggio_corniceSicurezza_dynamic_user() throws Exception{
    	if(ModIProperties.sicurezzaMessaggio_corniceSicurezza_dynamic_user==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza.user";
    		try{  
    			//String value = this.reader.getValue_convertEnvProperties(propertyName);
    			String value = this.reader.getValue(propertyName); // contiene ${} da non risolvere
				ModIProperties.sicurezzaMessaggio_corniceSicurezza_dynamic_user = ModISecurityConfig.convertToList(value);
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà '"+propertyName+"' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return ModIProperties.sicurezzaMessaggio_corniceSicurezza_dynamic_user;
	}
	
	private static List<String> sicurezzaMessaggio_corniceSicurezza_dynamic_ipuser= null;
	public List<String> getSicurezzaMessaggio_corniceSicurezza_dynamic_ipuser() throws Exception{
    	if(ModIProperties.sicurezzaMessaggio_corniceSicurezza_dynamic_ipuser==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza.ipuser";
    		try{  
    			//String value = this.reader.getValue_convertEnvProperties(propertyName);
    			String value = this.reader.getValue(propertyName); // contiene ${} da non risolvere
				ModIProperties.sicurezzaMessaggio_corniceSicurezza_dynamic_ipuser = ModISecurityConfig.convertToList(value);
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà '"+propertyName+"' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return ModIProperties.sicurezzaMessaggio_corniceSicurezza_dynamic_ipuser;
	}
	
	
	
	
    /* **** TRACCE **** */ 
    
    private static Boolean isGenerazioneTracce = null;
	public Boolean isGenerazioneTracce(){
		if(ModIProperties.isGenerazioneTracce==null){
			
			Boolean defaultValue = false;
			String propertyName = "org.openspcoop2.protocol.modipa.generazioneTracce.enabled";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					ModIProperties.isGenerazioneTracce = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprietà '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue);
					ModIProperties.isGenerazioneTracce = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.log.debug("Proprietà '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue+", errore:"+e.getMessage());
				ModIProperties.isGenerazioneTracce = defaultValue;
			}
		}

		return ModIProperties.isGenerazioneTracce;
	}
	
	
	
	
	/* **** REST **** */ 
	
	private static String getRestSecurityTokenHeader= null;
	public String getRestSecurityTokenHeader() throws Exception{
    	if(ModIProperties.getRestSecurityTokenHeader==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.header";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getRestSecurityTokenHeader = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getRestSecurityTokenHeader;
	}
	
	private static Boolean getRestSecurityTokenClaimsIssuerEnabled_read= null;
	private static Boolean getRestSecurityTokenClaimsIssuerEnabled= null;
	public boolean isRestSecurityTokenClaimsIssuerEnabled() throws Exception{
    	if(ModIProperties.getRestSecurityTokenClaimsIssuerEnabled_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.iss.enabled";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getRestSecurityTokenClaimsIssuerEnabled = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		ModIProperties.getRestSecurityTokenClaimsIssuerEnabled_read = true;
    	}
    	
    	return ModIProperties.getRestSecurityTokenClaimsIssuerEnabled;
	}
	private static Boolean getRestSecurityTokenClaimsIssuerHeaderValue_read= null;
	private static String getRestSecurityTokenClaimsIssuerHeaderValue= null;
	public String getRestSecurityTokenClaimsIssuerHeaderValue() throws Exception{
    	if(ModIProperties.getRestSecurityTokenClaimsIssuerHeaderValue_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.iss";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getRestSecurityTokenClaimsIssuerHeaderValue = value;
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		getRestSecurityTokenClaimsIssuerHeaderValue_read = true;
    	}
    	
    	return ModIProperties.getRestSecurityTokenClaimsIssuerHeaderValue;
	}
	
	private static Boolean getRestSecurityTokenClaimsSubjectEnabled_read= null;
	private static Boolean getRestSecurityTokenClaimsSubjectEnabled= null;
	public boolean isRestSecurityTokenClaimsSubjectEnabled() throws Exception{
    	if(ModIProperties.getRestSecurityTokenClaimsSubjectEnabled_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.sub.enabled";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getRestSecurityTokenClaimsSubjectEnabled = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		ModIProperties.getRestSecurityTokenClaimsSubjectEnabled_read = true;
    	}
    	
    	return ModIProperties.getRestSecurityTokenClaimsSubjectEnabled;
	}
	private static Boolean getRestSecurityTokenClaimsSubjectHeaderValue_read= null;
	private static String getRestSecurityTokenClaimsSubjectHeaderValue= null;
	public String getRestSecurityTokenClaimsSubjectHeaderValue() throws Exception{
    	if(ModIProperties.getRestSecurityTokenClaimsSubjectHeaderValue_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.sub";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getRestSecurityTokenClaimsSubjectHeaderValue = value;
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		getRestSecurityTokenClaimsSubjectHeaderValue_read = true;
    	}
    	
    	return ModIProperties.getRestSecurityTokenClaimsSubjectHeaderValue;
	}
	
	private static String getRestSecurityTokenClaimsClientIdHeader= null;
	public String getRestSecurityTokenClaimsClientIdHeader() throws Exception{
    	if(ModIProperties.getRestSecurityTokenClaimsClientIdHeader==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.client_id";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getRestSecurityTokenClaimsClientIdHeader = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getRestSecurityTokenClaimsClientIdHeader;
	}
	
	private static String getRestSecurityTokenClaimSignedHeaders= null;
	public String getRestSecurityTokenClaimSignedHeaders() throws Exception{
    	if(ModIProperties.getRestSecurityTokenClaimSignedHeaders==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.signedHeaders";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getRestSecurityTokenClaimSignedHeaders = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getRestSecurityTokenClaimSignedHeaders;
	}
	
	
	private static String getRestSecurityTokenClaimRequestDigest= null;
	public String getRestSecurityTokenClaimRequestDigest() throws Exception{
    	if(ModIProperties.getRestSecurityTokenClaimRequestDigest==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.requestDigest";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getRestSecurityTokenClaimRequestDigest = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getRestSecurityTokenClaimRequestDigest;
	}
	
	
	private static String [] getRestSecurityTokenSignedHeaders = null;
	public String [] getRestSecurityTokenSignedHeaders() throws Exception{
    	if(ModIProperties.getRestSecurityTokenSignedHeaders==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.signedHeaders";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					ModIProperties.getRestSecurityTokenSignedHeaders = new String[tmp.length];
					for (int i = 0; i < tmp.length; i++) {
						ModIProperties.getRestSecurityTokenSignedHeaders[i] = tmp[i].trim();
					}
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getRestSecurityTokenSignedHeaders;
	}
	public String  getRestSecurityTokenSignedHeadersAsString() throws Exception{
		StringBuilder bf = new StringBuilder();
		for (String hdr : getRestSecurityTokenSignedHeaders) {
			if(bf.length()>0) {
				bf.append(",");
			}
			bf.append(hdr);
		}
		return bf.toString();
	}
	
	private static Boolean getRestSecurityTokenClaimsIatTimeCheck_milliseconds_read = null;
	private static Integer getRestSecurityTokenClaimsIatTimeCheck_milliseconds = null;
	public Integer getRestSecurityTokenClaimsIatTimeCheck_milliseconds() throws Exception{

		if(ModIProperties.getRestSecurityTokenClaimsIatTimeCheck_milliseconds_read==null){
			
			String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.iat.minutes";
			try{  
				String value = this.reader.getValue_convertEnvProperties(name); 

				if (value != null){
					value = value.trim();
					ModIProperties.getRestSecurityTokenClaimsIatTimeCheck_milliseconds = Integer.valueOf(value); // minuti
					ModIProperties.getRestSecurityTokenClaimsIatTimeCheck_milliseconds = ModIProperties.getRestSecurityTokenClaimsIatTimeCheck_milliseconds * 60 * 1000;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà '"+name+"' non impostata, errore:"+e.getMessage(),e);
				throw e;
			}
			
			ModIProperties.getRestSecurityTokenClaimsIatTimeCheck_milliseconds_read = true;
		}

		return ModIProperties.getRestSecurityTokenClaimsIatTimeCheck_milliseconds;
	}
	
	private static Boolean getRestSecurityTokenRequestDigestClean_read= null;
	private static Boolean getRestSecurityTokenRequestDigestClean= null;
	public boolean isRestSecurityTokenRequestDigestClean() throws Exception{
    	if(ModIProperties.getRestSecurityTokenRequestDigestClean_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.request.digest.clean";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getRestSecurityTokenRequestDigestClean = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		ModIProperties.getRestSecurityTokenRequestDigestClean_read = true;
    	}
    	
    	return ModIProperties.getRestSecurityTokenRequestDigestClean;
	}
	
	private static Boolean getRestSecurityTokenResponseDigestClean_read= null;
	private static Boolean getRestSecurityTokenResponseDigestClean= null;
	public boolean isRestSecurityTokenResponseDigestClean() throws Exception{
    	if(ModIProperties.getRestSecurityTokenResponseDigestClean_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.response.digest.clean";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getRestSecurityTokenResponseDigestClean = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		ModIProperties.getRestSecurityTokenResponseDigestClean_read = true;
    	}
    	
    	return ModIProperties.getRestSecurityTokenResponseDigestClean;
	}
	
	private static Boolean getRestSecurityTokenResponseDigestHEADuseServerHeader_read= null;
	private static Boolean getRestSecurityTokenResponseDigestHEADuseServerHeader= null;
	public boolean isRestSecurityTokenResponseDigestHEADuseServerHeader() throws Exception{
    	if(ModIProperties.getRestSecurityTokenResponseDigestHEADuseServerHeader_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.response.digest.HEAD.useServerHeader";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getRestSecurityTokenResponseDigestHEADuseServerHeader = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		ModIProperties.getRestSecurityTokenResponseDigestHEADuseServerHeader_read = true;
    	}
    	
    	return ModIProperties.getRestSecurityTokenResponseDigestHEADuseServerHeader;
	}
	
	private static Boolean getRestSecurityTokenFaultProcessEnabled_read= null;
	private static Boolean getRestSecurityTokenFaultProcessEnabled= null;
	public boolean isRestSecurityTokenFaultProcessEnabled() throws Exception{
    	if(ModIProperties.getRestSecurityTokenFaultProcessEnabled_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.fault.securityToken";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getRestSecurityTokenFaultProcessEnabled = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		ModIProperties.getRestSecurityTokenFaultProcessEnabled_read = true;
    	}
    	
    	return ModIProperties.getRestSecurityTokenFaultProcessEnabled;
	}
	
	private static String getRestCorrelationIdHeader= null;
	public String getRestCorrelationIdHeader() throws Exception{
    	if(ModIProperties.getRestCorrelationIdHeader==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.correlationId.header";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getRestCorrelationIdHeader = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getRestCorrelationIdHeader;
	}	
	
	private static String getRestReplyToHeader= null;
	public String getRestReplyToHeader() throws Exception{
    	if(ModIProperties.getRestReplyToHeader==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.replyTo.header";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getRestReplyToHeader = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getRestReplyToHeader;
	}
	
	private static String getRestLocationHeader= null;
	public String getRestLocationHeader() throws Exception{
    	if(ModIProperties.getRestLocationHeader==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.location.header";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getRestLocationHeader = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getRestLocationHeader;
	}
	
	// .. PUSH ..
	
	private static Boolean getRestSecurityTokenPushReplyToUpdateOrCreate = null;
	public boolean isRestSecurityTokenPushReplyToUpdateOrCreateInFruizione() throws Exception{
    	if(ModIProperties.getRestSecurityTokenPushReplyToUpdateOrCreate==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.push.replyTo.header.updateOrCreate";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getRestSecurityTokenPushReplyToUpdateOrCreate = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getRestSecurityTokenPushReplyToUpdateOrCreate;
	}
	
	private static Boolean getRestSecurityTokenPushReplyToUpdate = null;
	public boolean isRestSecurityTokenPushReplyToUpdateInErogazione() throws Exception{
    	if(ModIProperties.getRestSecurityTokenPushReplyToUpdate==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.push.replyTo.header.update";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getRestSecurityTokenPushReplyToUpdate = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getRestSecurityTokenPushReplyToUpdate;
	}
	
	private static Boolean getRestSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists = null;
	public boolean isRestSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists() throws Exception{
    	if(ModIProperties.getRestSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.push.request.correlationId.header.useTransactionIdIfNotExists";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getRestSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getRestSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists;
	}
	
	private static Integer [] getRestSecurityTokenPushRequestHttpStatus = null;
	public Integer [] getRestSecurityTokenPushRequestHttpStatus() throws Exception{
    	if(ModIProperties.getRestSecurityTokenPushRequestHttpStatus==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.push.request.httpStatus";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					ModIProperties.getRestSecurityTokenPushRequestHttpStatus = new Integer[tmp.length];
					for (int i = 0; i < tmp.length; i++) {
						ModIProperties.getRestSecurityTokenPushRequestHttpStatus[i] = Integer.valueOf(tmp[i].trim());
					}
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getRestSecurityTokenPushRequestHttpStatus;
	}
	
	private static Integer [] getRestSecurityTokenPushResponseHttpStatus = null;
	public Integer [] getRestSecurityTokenPushResponseHttpStatus() throws Exception{
    	if(ModIProperties.getRestSecurityTokenPushResponseHttpStatus==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.push.response.httpStatus";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					ModIProperties.getRestSecurityTokenPushResponseHttpStatus = new Integer[tmp.length];
					for (int i = 0; i < tmp.length; i++) {
						ModIProperties.getRestSecurityTokenPushResponseHttpStatus[i] = Integer.valueOf(tmp[i].trim());
					}
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getRestSecurityTokenPushResponseHttpStatus;
	}
	
	// .. PULL ..
	
	private static Integer [] getRestSecurityTokenPullRequestHttpStatus = null;
	public Integer [] getRestSecurityTokenPullRequestHttpStatus() throws Exception{
    	if(ModIProperties.getRestSecurityTokenPullRequestHttpStatus==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.pull.request.httpStatus";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					ModIProperties.getRestSecurityTokenPullRequestHttpStatus = new Integer[tmp.length];
					for (int i = 0; i < tmp.length; i++) {
						ModIProperties.getRestSecurityTokenPullRequestHttpStatus[i] = Integer.valueOf(tmp[i].trim());
					}
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getRestSecurityTokenPullRequestHttpStatus;
	}
	
	private static Integer [] getRestSecurityTokenPullRequestStateNotReadyHttpStatus = null;
	public Integer [] getRestSecurityTokenPullRequestStateNotReadyHttpStatus() throws Exception{
    	if(ModIProperties.getRestSecurityTokenPullRequestStateNotReadyHttpStatus==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.pull.requestState.notReady.httpStatus";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					ModIProperties.getRestSecurityTokenPullRequestStateNotReadyHttpStatus = new Integer[tmp.length];
					for (int i = 0; i < tmp.length; i++) {
						ModIProperties.getRestSecurityTokenPullRequestStateNotReadyHttpStatus[i] = Integer.valueOf(tmp[i].trim());
					}
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getRestSecurityTokenPullRequestStateNotReadyHttpStatus;
	}
	
	private static Integer [] getRestSecurityTokenPullRequestStateOkHttpStatus = null;
	public Integer [] getRestSecurityTokenPullRequestStateOkHttpStatus() throws Exception{
    	if(ModIProperties.getRestSecurityTokenPullRequestStateOkHttpStatus==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.pull.requestState.ok.httpStatus";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					ModIProperties.getRestSecurityTokenPullRequestStateOkHttpStatus = new Integer[tmp.length];
					for (int i = 0; i < tmp.length; i++) {
						ModIProperties.getRestSecurityTokenPullRequestStateOkHttpStatus[i] = Integer.valueOf(tmp[i].trim());
					}
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getRestSecurityTokenPullRequestStateOkHttpStatus;
	}
	
	private static Integer [] getRestSecurityTokenPullResponseHttpStatus = null;
	public Integer [] getRestSecurityTokenPullResponseHttpStatus() throws Exception{
    	if(ModIProperties.getRestSecurityTokenPullResponseHttpStatus==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.pull.response.httpStatus";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					ModIProperties.getRestSecurityTokenPullResponseHttpStatus = new Integer[tmp.length];
					for (int i = 0; i < tmp.length; i++) {
						ModIProperties.getRestSecurityTokenPullResponseHttpStatus[i] = Integer.valueOf(tmp[i].trim());
					}
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getRestSecurityTokenPullResponseHttpStatus;
	}
	
	
	/* **** SOAP **** */ 
	
	private static Boolean getSoapSecurityTokenMustUnderstand_read= null;
	private static Boolean getSoapSecurityTokenMustUnderstand= null;
	public boolean isSoapSecurityTokenMustUnderstand() throws Exception{
    	if(ModIProperties.getSoapSecurityTokenMustUnderstand_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.securityToken.mustUnderstand";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getSoapSecurityTokenMustUnderstand = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		ModIProperties.getSoapSecurityTokenMustUnderstand_read = true;
    	}
    	
    	return ModIProperties.getSoapSecurityTokenMustUnderstand;
	}	
	
	private static Boolean getSoapSecurityTokenActor_read= null;
	private static String getSoapSecurityTokenActor= null;
	public String getSoapSecurityTokenActor() throws Exception{
    	if(ModIProperties.getSoapSecurityTokenActor_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.securityToken.actor";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					if(!"".equals(value)) {
						ModIProperties.getSoapSecurityTokenActor = value;
					}
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		ModIProperties.getSoapSecurityTokenActor_read = true;
    	}
    	
    	return ModIProperties.getSoapSecurityTokenActor;
	}
	
	private static Boolean getSoapSecurityTokenTimestampCreatedTimeCheck_milliseconds_read = null;
	private static Integer getSoapSecurityTokenTimestampCreatedTimeCheck_milliseconds = null;
	public Integer getSoapSecurityTokenTimestampCreatedTimeCheck_milliseconds() throws Exception{

		if(ModIProperties.getSoapSecurityTokenTimestampCreatedTimeCheck_milliseconds_read==null){
			
			String name = "org.openspcoop2.protocol.modipa.soap.securityToken.timestamp.created.minutes";
			try{  
				String value = this.reader.getValue_convertEnvProperties(name); 

				if (value != null){
					value = value.trim();
					ModIProperties.getSoapSecurityTokenTimestampCreatedTimeCheck_milliseconds = Integer.valueOf(value); // minuti
					ModIProperties.getSoapSecurityTokenTimestampCreatedTimeCheck_milliseconds = ModIProperties.getSoapSecurityTokenTimestampCreatedTimeCheck_milliseconds * 60 * 1000;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà '"+name+"' non impostata, errore:"+e.getMessage(),e);
				throw e;
			}
			
			ModIProperties.getSoapSecurityTokenTimestampCreatedTimeCheck_milliseconds_read = true;
		}

		return ModIProperties.getSoapSecurityTokenTimestampCreatedTimeCheck_milliseconds;
	}
	
	private static Boolean getSoapSecurityTokenFaultProcessEnabled_read= null;
	private static Boolean getSoapSecurityTokenFaultProcessEnabled= null;
	public boolean isSoapSecurityTokenFaultProcessEnabled() throws Exception{
    	if(ModIProperties.getSoapSecurityTokenFaultProcessEnabled_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.fault.securityToken";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getSoapSecurityTokenFaultProcessEnabled = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		ModIProperties.getSoapSecurityTokenFaultProcessEnabled_read = true;
    	}
    	
    	return ModIProperties.getSoapSecurityTokenFaultProcessEnabled;
	}
	
	private static Boolean getSoapWSAddressingMustUnderstand_read= null;
	private static Boolean getSoapWSAddressingMustUnderstand= null;
	public boolean isSoapWSAddressingMustUnderstand() throws Exception{
    	if(ModIProperties.getSoapWSAddressingMustUnderstand_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.wsaddressing.mustUnderstand";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getSoapWSAddressingMustUnderstand = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		ModIProperties.getSoapWSAddressingMustUnderstand_read = true;
    	}
    	
    	return ModIProperties.getSoapWSAddressingMustUnderstand;
	}	
	
	private static Boolean getSoapWSAddressingActor_read= null;
	private static String getSoapWSAddressingActor= null;
	public String getSoapWSAddressingActor() throws Exception{
    	if(ModIProperties.getSoapWSAddressingActor_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.wsaddressing.actor";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					if(!"".equals(value)) {
						ModIProperties.getSoapWSAddressingActor = value;
					}
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		ModIProperties.getSoapWSAddressingActor_read = true;
    	}
    	
    	return ModIProperties.getSoapWSAddressingActor;
	}
	
	
	
	
	private static String getSoapCorrelationIdName= null;
	public String getSoapCorrelationIdName() throws Exception{
    	if(ModIProperties.getSoapCorrelationIdName==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.correlationId.name";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getSoapCorrelationIdName = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getSoapCorrelationIdName;
	}
	
	private static String getSoapCorrelationIdNamespace= null;
	public String getSoapCorrelationIdNamespace() throws Exception{
    	if(ModIProperties.getSoapCorrelationIdNamespace==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.correlationId.namespace";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getSoapCorrelationIdNamespace = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getSoapCorrelationIdNamespace;
	}
	
	public boolean useSoapBodyCorrelationIdNamespace() throws Exception {
		return ModICostanti.MODIPA_USE_BODY_NAMESPACE.equals(this.getSoapCorrelationIdNamespace());
	}
	
	private static String getSoapCorrelationIdPrefix= null;
	public String getSoapCorrelationIdPrefix() throws Exception{
    	if(ModIProperties.getSoapCorrelationIdPrefix==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.correlationId.prefix";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getSoapCorrelationIdPrefix = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getSoapCorrelationIdPrefix;
	}
	
	private static Boolean getSoapCorrelationIdMustUnderstand_read= null;
	private static Boolean getSoapCorrelationIdMustUnderstand= null;
	public boolean isSoapCorrelationIdMustUnderstand() throws Exception{
    	if(ModIProperties.getSoapCorrelationIdMustUnderstand_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.correlationId.mustUnderstand";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getSoapCorrelationIdMustUnderstand = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		ModIProperties.getSoapCorrelationIdMustUnderstand_read = true;
    	}
    	
    	return ModIProperties.getSoapCorrelationIdMustUnderstand;
	}	
	
	private static Boolean getSoapCorrelationIdActor_read= null;
	private static String getSoapCorrelationIdActor= null;
	public String getSoapCorrelationIdActor() throws Exception{
    	if(ModIProperties.getSoapCorrelationIdActor_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.correlationId.actor";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					if(!"".equals(value)) {
						ModIProperties.getSoapCorrelationIdActor = value;
					}
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		ModIProperties.getSoapCorrelationIdActor_read = true;
    	}
    	
    	return ModIProperties.getSoapCorrelationIdActor;
	}
	
	
	
	
	private static String getSoapReplyToName= null;
	public String getSoapReplyToName() throws Exception{
    	if(ModIProperties.getSoapReplyToName==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.replyTo.name";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getSoapReplyToName = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getSoapReplyToName;
	}
	
	private static String getSoapReplyToNamespace= null;
	public String getSoapReplyToNamespace() throws Exception{
    	if(ModIProperties.getSoapReplyToNamespace==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.replyTo.namespace";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getSoapReplyToNamespace = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getSoapReplyToNamespace;
	}
	
	public boolean useSoapBodyReplyToNamespace() throws Exception {
		return ModICostanti.MODIPA_USE_BODY_NAMESPACE.equals(this.getSoapReplyToNamespace());
	}
	
	private static String getSoapReplyToPrefix= null;
	public String getSoapReplyToPrefix() throws Exception{
    	if(ModIProperties.getSoapReplyToPrefix==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.replyTo.prefix";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getSoapReplyToPrefix = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getSoapReplyToPrefix;
	}
	
	private static Boolean getSoapReplyToMustUnderstand_read= null;
	private static Boolean getSoapReplyToMustUnderstand= null;
	public boolean isSoapReplyToMustUnderstand() throws Exception{
    	if(ModIProperties.getSoapReplyToMustUnderstand_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.replyTo.mustUnderstand";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getSoapReplyToMustUnderstand = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		ModIProperties.getSoapReplyToMustUnderstand_read = true;
    	}
    	
    	return ModIProperties.getSoapReplyToMustUnderstand;
	}	
	
	private static Boolean getSoapReplyToActor_read= null;
	private static String getSoapReplyToActor= null;
	public String getSoapReplyToActor() throws Exception{
    	if(ModIProperties.getSoapReplyToActor_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.replyTo.actor";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					if(!"".equals(value)) {
						ModIProperties.getSoapReplyToActor = value;
					}
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		ModIProperties.getSoapReplyToActor_read = true;
    	}
    	
    	return ModIProperties.getSoapReplyToActor;
	}
	
	
	private static String getSoapRequestDigestName= null;
	public String getSoapRequestDigestName() throws Exception{
    	if(ModIProperties.getSoapRequestDigestName==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.requestDigest.name";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getSoapRequestDigestName = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getSoapRequestDigestName;
	}
	
	private static String getSoapRequestDigestNamespace= null;
	public String getSoapRequestDigestNamespace() throws Exception{
    	if(ModIProperties.getSoapRequestDigestNamespace==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.requestDigest.namespace";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getSoapRequestDigestNamespace = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getSoapRequestDigestNamespace;
	}
	
	public boolean useSoapBodyRequestDigestNamespace() throws Exception {
		return ModICostanti.MODIPA_USE_BODY_NAMESPACE.equals(this.getSoapRequestDigestNamespace());
	}
	
	private static String getSoapRequestDigestPrefix= null;
	public String getSoapRequestDigestPrefix() throws Exception{
    	if(ModIProperties.getSoapRequestDigestPrefix==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.requestDigest.prefix";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getSoapRequestDigestPrefix = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getSoapRequestDigestPrefix;
	}
	
	private static Boolean getSoapRequestDigestMustUnderstand_read= null;
	private static Boolean getSoapRequestDigestMustUnderstand= null;
	public boolean isSoapRequestDigestMustUnderstand() throws Exception{
    	if(ModIProperties.getSoapRequestDigestMustUnderstand_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.requestDigest.mustUnderstand";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getSoapRequestDigestMustUnderstand = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		ModIProperties.getSoapRequestDigestMustUnderstand_read = true;
    	}
    	
    	return ModIProperties.getSoapRequestDigestMustUnderstand;
	}	
	
	private static Boolean getSoapRequestDigestActor_read= null;
	private static String getSoapRequestDigestActor= null;
	public String getSoapRequestDigestActor() throws Exception{
    	if(ModIProperties.getSoapRequestDigestActor_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.requestDigest.actor";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					if(!"".equals(value)) {
						ModIProperties.getSoapRequestDigestActor = value;
					}
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		ModIProperties.getSoapRequestDigestActor_read = true;
    	}
    	
    	return ModIProperties.getSoapRequestDigestActor;
	}
	
	// .. PUSH ..
	
	private static Boolean getSoapSecurityTokenPushReplyToUpdateOrCreate = null;
	public boolean isSoapSecurityTokenPushReplyToUpdateOrCreateInFruizione() throws Exception{
    	if(ModIProperties.getSoapSecurityTokenPushReplyToUpdateOrCreate==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.push.replyTo.header.updateOrCreate";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getSoapSecurityTokenPushReplyToUpdateOrCreate = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getSoapSecurityTokenPushReplyToUpdateOrCreate;
	}
	
	private static Boolean getSoapSecurityTokenPushReplyToUpdate = null;
	public boolean isSoapSecurityTokenPushReplyToUpdateInErogazione() throws Exception{
    	if(ModIProperties.getSoapSecurityTokenPushReplyToUpdate==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.push.replyTo.header.update";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getSoapSecurityTokenPushReplyToUpdate = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getSoapSecurityTokenPushReplyToUpdate;
	}
	
	private static Boolean getSoapSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists = null;
	public boolean isSoapSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists() throws Exception{
    	if(ModIProperties.getSoapSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.push.request.correlationId.header.useTransactionIdIfNotExists";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getSoapSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getSoapSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists;
	}
	
	private static Boolean getSoapSecurityTokenPullCorrelationIdUseTransactionIdIfNotExists = null;
	public boolean isSoapSecurityTokenPullCorrelationIdUseTransactionIdIfNotExists() throws Exception{
    	if(ModIProperties.getSoapSecurityTokenPullCorrelationIdUseTransactionIdIfNotExists==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.pull.request.correlationId.header.useTransactionIdIfNotExists";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.getSoapSecurityTokenPullCorrelationIdUseTransactionIdIfNotExists = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return ModIProperties.getSoapSecurityTokenPullCorrelationIdUseTransactionIdIfNotExists;
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
    	if(ModIProperties.isRiferimentoIDRichiesta_PD_RequiredRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.pd.riferimentoIdRichiesta.required"); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.isRiferimentoIDRichiesta_PD_Required = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprietà 'org.openspcoop2.protocol.modipa.pd.riferimentoIdRichiesta.required' non impostata, viene utilizzato il default 'true'");
					ModIProperties.isRiferimentoIDRichiesta_PD_Required = true;
				}
				
				ModIProperties.isRiferimentoIDRichiesta_PD_RequiredRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.pd.riferimentoIdRichiesta.required' non impostata, viene utilizzato il default 'true', errore:"+e.getMessage());
				ModIProperties.isRiferimentoIDRichiesta_PD_Required = true;
				
				ModIProperties.isRiferimentoIDRichiesta_PD_RequiredRead = true;
			}
    	}
    	
    	return ModIProperties.isRiferimentoIDRichiesta_PD_Required;
	}
	
	private static Boolean isRiferimentoIDRichiesta_PA_Required= null;
	private static Boolean isRiferimentoIDRichiesta_PA_RequiredRead= null;
    public Boolean isRiferimentoIDRichiesta_PA_Required(){
    	if(ModIProperties.isRiferimentoIDRichiesta_PA_RequiredRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.pa.riferimentoIdRichiesta.required"); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.isRiferimentoIDRichiesta_PA_Required = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprietà 'org.openspcoop2.protocol.modipa.pa.riferimentoIdRichiesta.required' non impostata, viene utilizzato il default 'true'");
					ModIProperties.isRiferimentoIDRichiesta_PA_Required = true;
				}
				
				ModIProperties.isRiferimentoIDRichiesta_PA_RequiredRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.pa.riferimentoIdRichiesta.required' non impostata, viene utilizzato il default 'true', errore:"+e.getMessage());
				ModIProperties.isRiferimentoIDRichiesta_PA_Required = true;
				
				ModIProperties.isRiferimentoIDRichiesta_PA_RequiredRead = true;
			}
    	}
    	
    	return ModIProperties.isRiferimentoIDRichiesta_PA_Required;
	}
	
	

	/* **** SOAP FAULT (Protocollo, Porta Applicativa) **** */
	
    /**
     * Indicazione se ritornare un soap fault personalizzato nel codice/actor/faultString per i messaggi di errore di protocollo (Porta Applicativa)
     *   
     * @return Indicazione se ritornare un soap fault personalizzato nel codice/actor/faultString per i messaggi di errore di protocollo (Porta Applicativa)
     * 
     */
	private static Boolean isPortaApplicativaBustaErrore_personalizzaElementiFault= null;
	private static Boolean isPortaApplicativaBustaErrore_personalizzaElementiFaultRead= null;
    public Boolean isPortaApplicativaBustaErrore_personalizzaElementiFault(){
    	if(ModIProperties.isPortaApplicativaBustaErrore_personalizzaElementiFaultRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.pa.bustaErrore.personalizzaElementiFault"); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.isPortaApplicativaBustaErrore_personalizzaElementiFault = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprietà 'org.openspcoop2.protocol.modipa.pa.bustaErrore.personalizzaElementiFault' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails)");
					ModIProperties.isPortaApplicativaBustaErrore_personalizzaElementiFault = null;
				}
				
				ModIProperties.isPortaApplicativaBustaErrore_personalizzaElementiFaultRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.pa.bustaErrore.personalizzaElementiFault' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails), errore:"+e.getMessage());
				ModIProperties.isPortaApplicativaBustaErrore_personalizzaElementiFault = null;
				
				ModIProperties.isPortaApplicativaBustaErrore_personalizzaElementiFaultRead = true;
			}
    	}
    	
    	return ModIProperties.isPortaApplicativaBustaErrore_personalizzaElementiFault;
	}
    
    
    /**
     * Indicazione se deve essere aggiunto un errore-applicativo nei details di un messaggio di errore di protocollo (Porta Applicativa)
     *   
     * @return Indicazione se deve essere aggiunto un errore-applicativo nei details di un messaggio di errore di protocollo (Porta Applicativa)
     * 
     */
	private static Boolean isPortaApplicativaBustaErrore_aggiungiErroreApplicativo= null;
	private static Boolean isPortaApplicativaBustaErrore_aggiungiErroreApplicativoRead= null;
    public Boolean isPortaApplicativaBustaErrore_aggiungiErroreApplicativo(){
    	if(ModIProperties.isPortaApplicativaBustaErrore_aggiungiErroreApplicativoRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.pa.bustaErrore.aggiungiErroreApplicativo"); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.isPortaApplicativaBustaErrore_aggiungiErroreApplicativo = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprietà 'org.openspcoop2.protocol.modipa.pa.bustaErrore.aggiungiErroreApplicativo' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails)");
					ModIProperties.isPortaApplicativaBustaErrore_aggiungiErroreApplicativo = null;
				}
				
				ModIProperties.isPortaApplicativaBustaErrore_aggiungiErroreApplicativoRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.pa.bustaErrore.aggiungiErroreApplicativo' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails), errore:"+e.getMessage());
				ModIProperties.isPortaApplicativaBustaErrore_aggiungiErroreApplicativo = null;
				
				ModIProperties.isPortaApplicativaBustaErrore_aggiungiErroreApplicativoRead = true;
			}
    	}
    	
    	return ModIProperties.isPortaApplicativaBustaErrore_aggiungiErroreApplicativo;
	}
	
    /**
     * Indicazione se generare i details in caso di SOAPFault *_001 (senza buste Errore)
     *   
     * @return Indicazione se generare i details in caso di SOAPFault *_001 (senza buste Errore)
     * 
     */
	private static Boolean isGenerazioneDetailsSOAPFaultProtocolValidazione = null;
    public boolean isGenerazioneDetailsSOAPFaultProtocolValidazione(){
    	if(ModIProperties.isGenerazioneDetailsSOAPFaultProtocolValidazione==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.eccezioneIntestazione"); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.isGenerazioneDetailsSOAPFaultProtocolValidazione = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.eccezioneIntestazione' non impostata, viene utilizzato il default=false");
					ModIProperties.isGenerazioneDetailsSOAPFaultProtocolValidazione = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.eccezioneIntestazione' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				ModIProperties.isGenerazioneDetailsSOAPFaultProtocolValidazione = false;
			}
    	}
    	
    	return ModIProperties.isGenerazioneDetailsSOAPFaultProtocolValidazione;
	}
    
    /**
     * Indicazione se generare i details in caso di SOAPFault *_300
     *   
     * @return Indicazione se generare i details in caso di SOAPFault *_300
     * 
     */
	private static Boolean isGenerazioneDetailsSOAPFaultProtocolProcessamento = null;
    public boolean isGenerazioneDetailsSOAPFaultProtocolProcessamento(){
    	if(ModIProperties.isGenerazioneDetailsSOAPFaultProtocolProcessamento==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.eccezioneProcessamento"); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.isGenerazioneDetailsSOAPFaultProtocolProcessamento = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.eccezioneProcessamento' non impostata, viene utilizzato il default=true");
					ModIProperties.isGenerazioneDetailsSOAPFaultProtocolProcessamento = true;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.eccezioneProcessamento' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				ModIProperties.isGenerazioneDetailsSOAPFaultProtocolProcessamento = true;
			}
    	}
    	
    	return ModIProperties.isGenerazioneDetailsSOAPFaultProtocolProcessamento;
	}
    
    
    /**
     * Indicazione se generare nei details in caso di SOAPFault *_300 lo stack trace
     *   
     * @return Indicazione se generare nei details in caso di SOAPFault *_300 lo stack trace
     * 
     */
	private static Boolean isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = null;
    public boolean isGenerazioneDetailsSOAPFaultProtocolWithStackTrace(){
    	if(ModIProperties.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.stackTrace"); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.stackTrace' non impostata, viene utilizzato il default=false");
					ModIProperties.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.stackTrace' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				ModIProperties.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = false;
			}
    	}
    	
    	return ModIProperties.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace;
	}
    
    /**
     * Indicazione se generare nei details in caso di SOAPFault informazioni generiche
     *   
     * @return Indicazione se generare nei details in caso di SOAPFault informazioni generiche
     * 
     */
	private static Boolean isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = null;
    public boolean isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche(){
    	if(ModIProperties.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.informazioniGeneriche"); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.informazioniGeneriche' non impostata, viene utilizzato il default=true");
					ModIProperties.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = true;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.informazioniGeneriche' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				ModIProperties.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = true;
			}
    	}
    	
    	return ModIProperties.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche;
	}
    
    
    
    /* **** SOAP FAULT (Integrazione, Porta Delegata) **** */
    
    /**
     * Indicazione se generare i details in Casi di errore 5XX
     *   
     * @return Indicazione se generare i details in Casi di errore 5XX
     * 
     */
	private static Boolean isGenerazioneDetailsSOAPFaultIntegrationServerError = null;
    public boolean isGenerazioneDetailsSOAPFaultIntegrationServerError(){
    	if(ModIProperties.isGenerazioneDetailsSOAPFaultIntegrationServerError==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.serverError"); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.isGenerazioneDetailsSOAPFaultIntegrationServerError = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.serverError' non impostata, viene utilizzato il default=true");
					ModIProperties.isGenerazioneDetailsSOAPFaultIntegrationServerError = true;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.serverError' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				ModIProperties.isGenerazioneDetailsSOAPFaultIntegrationServerError = true;
			}
    	}
    	
    	return ModIProperties.isGenerazioneDetailsSOAPFaultIntegrationServerError;
	}
    
    /**
     * Indicazione se generare i details in Casi di errore 4XX
     *   
     * @return Indicazione se generare i details in Casi di errore 4XX
     * 
     */
	private static Boolean isGenerazioneDetailsSOAPFaultIntegrationClientError = null;
    public boolean isGenerazioneDetailsSOAPFaultIntegrationClientError(){
    	if(ModIProperties.isGenerazioneDetailsSOAPFaultIntegrationClientError==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.clientError"); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.isGenerazioneDetailsSOAPFaultIntegrationClientError = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.clientError' non impostata, viene utilizzato il default=false");
					ModIProperties.isGenerazioneDetailsSOAPFaultIntegrationClientError = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.clientError' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				ModIProperties.isGenerazioneDetailsSOAPFaultIntegrationClientError = false;
			}
    	}
    	
    	return ModIProperties.isGenerazioneDetailsSOAPFaultIntegrationClientError;
	}
    
    /**
     * Indicazione se generare nei details lo stack trace all'interno
     *   
     * @return Indicazione se generare nei details lo stack trace all'interno
     * 
     */
	private static Boolean isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = null;
    public boolean isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace(){
    	if(ModIProperties.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.stackTrace"); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.stackTrace' non impostata, viene utilizzato il default=false");
					ModIProperties.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.stackTrace' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				ModIProperties.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = false;
			}
    	}
    	
    	return ModIProperties.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace;
	}
    
    /**
     * Indicazione se generare nei details informazioni dettagliate o solo di carattere generale
     *   
     * @return Indicazione se generare nei details informazioni dettagliate o solo di carattere generale
     * 
     */
	private static Boolean isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche= null;
	private static Boolean isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGenericheRead= null;
    public Boolean isGenerazioneDetailsSOAPFaultIntegrazionConInformazioniGeneriche(){
    	if(ModIProperties.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGenericheRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.informazioniGeneriche"); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.informazioniGeneriche' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultAsGenericCode)");
					ModIProperties.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche = null;
				}
				
				ModIProperties.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGenericheRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.informazioniGeneriche' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultAsGenericCode), errore:"+e.getMessage());
				ModIProperties.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche = null;
				
				ModIProperties.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGenericheRead = true;
			}
    	}
    	
    	return ModIProperties.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche;
	}
    
    
    
    
    /* **** SOAP FAULT (Generati dagli attori esterni) **** */
    
    /**
     * Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultApplicativo originale
     *   
     * @return Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultApplicativo originale
     * 
     */
	private static Boolean isAggiungiDetailErroreApplicativo_SoapFaultApplicativo= null;
	private static Boolean isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead= null;
    public Boolean isAggiungiDetailErroreApplicativo_SoapFaultApplicativo(){
    	if(ModIProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.erroreApplicativo.faultApplicativo.enrichDetails"); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprietà 'org.openspcoop2.protocol.modipa.erroreApplicativo.faultApplicativo.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails)");
					ModIProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = null;
				}
				
				ModIProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.erroreApplicativo.faultApplicativo.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails), errore:"+e.getMessage());
				ModIProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = null;
				
				ModIProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead = true;
			}
    	}
    	
    	return ModIProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo;
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
    	if(ModIProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.erroreApplicativo.faultPdD.enrichDetails"); 
				
				if (value != null){
					value = value.trim();
					ModIProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdD = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprietà 'org.openspcoop2.protocol.modipa.erroreApplicativo.faultPdD.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultPdD.enrichDetails)");
					ModIProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdD = null;
				}
				
				ModIProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.erroreApplicativo.faultPdD.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultPdD.enrichDetails), errore:"+e.getMessage());
				ModIProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdD = null;
				
				ModIProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead = true;
			}
    	}
    	
    	return ModIProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdD;
	}

    
    
}
