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

package org.openspcoop2.protocol.modipa.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.pdd.core.token.parser.Claims;
import org.openspcoop2.protocol.basic.BasicStaticInstanceConfig;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.modipa.utils.ModISecurityConfig;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.digest.DigestEncoding;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
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
			}catch(Throwable er){
				// close
			}
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
			
			String trustStoreType = getSicurezzaMessaggio_certificati_trustStore_tipo();
			if(trustStoreType!=null) {
				if(!HSMUtils.isKeystoreHSM(trustStoreType)) {
					getSicurezzaMessaggio_certificati_trustStore_path();
					getSicurezzaMessaggio_certificati_trustStore_password();
				}
				getSicurezzaMessaggio_certificati_trustStore_crls();
			}
			
			String sslTrustStoreType = getSicurezzaMessaggio_ssl_trustStore_tipo();
			if(sslTrustStoreType!=null) {
				if(!HSMUtils.isKeystoreHSM(sslTrustStoreType)) {
					getSicurezzaMessaggio_ssl_trustStore_path();
					getSicurezzaMessaggio_ssl_trustStore_password();
				}
				getSicurezzaMessaggio_ssl_trustStore_crls();
			}
			
			/* **** KEY STORE **** */
			
			String keystoreType = getSicurezzaMessaggio_certificati_keyStore_tipo();
			if(keystoreType!=null) {
				if(!HSMUtils.isKeystoreHSM(keystoreType)) {
					getSicurezzaMessaggio_certificati_keyStore_path();
					getSicurezzaMessaggio_certificati_keyStore_password();
				}
				getSicurezzaMessaggio_certificati_key_alias();
				if(!HSMUtils.isKeystoreHSM(keystoreType) || HSMUtils.HSM_CONFIGURABLE_KEY_PASSWORD) {
					getSicurezzaMessaggio_certificati_key_password();
				}
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
			
			/* **** Versionamento **** */ 
			
			this.isModIVersioneBozza();
			
			/* **** REST **** */ 
			
			getRestSecurityTokenHeaderModI();
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
			getRestSecurityTokenClaimsIatTimeCheck_futureToleranceMilliseconds();
			isRestSecurityTokenClaimsExpTimeCheck();
			getRestSecurityTokenClaimsExpTimeCheck_toleranceMilliseconds();
			getRestSecurityTokenDigestDefaultEncoding();
			isRestSecurityTokenDigestEncodingChoice();
			getRestSecurityTokenDigestEncodingAccepted();
			isRestSecurityTokenRequestDigestClean();
			isRestSecurityTokenResponseDigestClean();
			isRestSecurityTokenResponseDigestHEADuseServerHeader();
			isRestSecurityTokenFaultProcessEnabled();
			getRestResponseSecurityTokenAudienceDefault(null);
			getRestCorrelationIdHeader();
			getRestReplyToHeader();
			getRestLocationHeader();
			isRestProfiliInterazioneCheckCompatibility();
			
			// .. Bloccante ..
			getRestBloccanteHttpStatus();
			getRestBloccanteHttpMethod();
			
			// .. PUSH ..
			isRestSecurityTokenPushReplyToUpdateOrCreateInFruizione();
			isRestSecurityTokenPushReplyToUpdateInErogazione();
			isRestSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists();
			getRestNonBloccantePushRequestHttpStatus();
			getRestNonBloccantePushRequestHttpMethod();
			getRestNonBloccantePushResponseHttpStatus();
			getRestNonBloccantePushResponseHttpMethod();
			
			// .. PULL ..
			getRestNonBloccantePullRequestHttpStatus();
			getRestNonBloccantePullRequestHttpMethod();
			getRestNonBloccantePullRequestStateNotReadyHttpStatus();
			getRestNonBloccantePullRequestStateOkHttpStatus();
			getRestNonBloccantePullRequestStateHttpMethod();
			getRestNonBloccantePullResponseHttpStatus();
			getRestNonBloccantePullResponseHttpMethod();
			
			/* **** SOAP **** */
			
			isSoapSecurityTokenMustUnderstand();
			getSoapSecurityTokenActor();
			getSoapSecurityTokenTimestampCreatedTimeCheck_milliseconds();
			getSoapSecurityTokenTimestampCreatedTimeCheck_futureToleranceMilliseconds();
			isSoapSecurityTokenTimestampExpiresTimeCheck();
			getSoapSecurityTokenTimestampExpiresTimeCheck_toleranceMilliseconds();
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
			
			getSoapResponseSecurityTokenAudienceDefault(null);
			
			isSoapSecurityTokenWsaToSoapAction();
			isSoapSecurityTokenWsaToOperation();
			isSoapSecurityTokenWsaToDisabled();
			
			// .. PUSH ..
			isSoapSecurityTokenPushReplyToUpdateOrCreateInFruizione();
			isSoapSecurityTokenPushReplyToUpdateInErogazione();
			isSoapSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists();
			
			/* **** CONFIGURAZIONE **** */
			
			isReadByPathBufferEnabled();
			isValidazioneBufferEnabled();
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
			
			/* **** Static instance config **** */
			
			this.useConfigStaticInstance();
			this.useErroreApplicativoStaticInstance();
			this.useEsitoStaticInstance();
			this.getStaticInstanceConfig();

			
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
	private Boolean generateIDasUUID = null;
	public Boolean generateIDasUUID(){
		if(this.generateIDasUUID==null){
			
			Boolean defaultValue = true;
			String propertyName = "org.openspcoop2.protocol.modipa.id.uuid";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.generateIDasUUID = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprietà '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue);
					this.generateIDasUUID = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue+", errore:"+e.getMessage());
				this.generateIDasUUID = defaultValue;
			}
		}

		return this.generateIDasUUID;
	}
	
	
	
	
	
	
	
	/* **** TRUST STORE **** */
		
	private String sicurezzaMessaggio_certificati_trustStore_tipo= null;
	private Boolean sicurezzaMessaggio_certificati_trustStore_tipo_read= null;
	public String getSicurezzaMessaggio_certificati_trustStore_tipo() {
    	if(this.sicurezzaMessaggio_certificati_trustStore_tipo_read==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.trustStore.tipo"); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggio_certificati_trustStore_tipo = value;
				}
				
				this.sicurezzaMessaggio_certificati_trustStore_tipo_read = true;
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.trustStore.tipo' non impostata, errore:"+e.getMessage());
				this.sicurezzaMessaggio_certificati_trustStore_tipo_read = true;
			}
    	}
    	
    	return this.sicurezzaMessaggio_certificati_trustStore_tipo;
	}	
	
	private String sicurezzaMessaggio_certificati_trustStore_path= null;
	public String getSicurezzaMessaggio_certificati_trustStore_path() throws Exception{
    	if(this.sicurezzaMessaggio_certificati_trustStore_path==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.trustStore.path"); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggio_certificati_trustStore_path = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.trustStore.path' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return this.sicurezzaMessaggio_certificati_trustStore_path;
	}
	
	private String sicurezzaMessaggio_certificati_trustStore_password= null;
	public String getSicurezzaMessaggio_certificati_trustStore_password() throws Exception{
    	if(this.sicurezzaMessaggio_certificati_trustStore_password==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.trustStore.password"); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggio_certificati_trustStore_password = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.trustStore.password' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return this.sicurezzaMessaggio_certificati_trustStore_password;
	}	
	
	private Boolean sicurezzaMessaggio_certificati_trustStore_crls_read= null;
	private String sicurezzaMessaggio_certificati_trustStore_crls= null;
	public String getSicurezzaMessaggio_certificati_trustStore_crls() throws Exception{
    	if(this.sicurezzaMessaggio_certificati_trustStore_crls_read==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.trustStore.crls"); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggio_certificati_trustStore_crls = value;
				}
				
				this.sicurezzaMessaggio_certificati_trustStore_crls_read = true;
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.trustStore.password' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return this.sicurezzaMessaggio_certificati_trustStore_crls;
	}	
	
	
		
	private String sicurezzaMessaggio_ssl_trustStore_tipo= null;
	private Boolean sicurezzaMessaggio_ssl_trustStore_tipo_read= null;
	public String getSicurezzaMessaggio_ssl_trustStore_tipo() {
    	if(this.sicurezzaMessaggio_ssl_trustStore_tipo_read==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.ssl.trustStore.tipo"); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggio_ssl_trustStore_tipo = value;
				}
				
				this.sicurezzaMessaggio_ssl_trustStore_tipo_read = true;
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.ssl.trustStore.tipo' non impostata, errore:"+e.getMessage());
				this.sicurezzaMessaggio_ssl_trustStore_tipo_read = true;
			}
    	}
    	
    	return this.sicurezzaMessaggio_ssl_trustStore_tipo;
	}	
	
	private String sicurezzaMessaggio_ssl_trustStore_path= null;
	public String getSicurezzaMessaggio_ssl_trustStore_path() throws Exception{
    	if(this.sicurezzaMessaggio_ssl_trustStore_path==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.ssl.trustStore.path"); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggio_ssl_trustStore_path = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.ssl.trustStore.path' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return this.sicurezzaMessaggio_ssl_trustStore_path;
	}
	
	private String sicurezzaMessaggio_ssl_trustStore_password= null;
	public String getSicurezzaMessaggio_ssl_trustStore_password() throws Exception{
    	if(this.sicurezzaMessaggio_ssl_trustStore_password==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.ssl.trustStore.password"); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggio_ssl_trustStore_password = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.ssl.trustStore.password' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return this.sicurezzaMessaggio_ssl_trustStore_password;
	}	
	
	private Boolean sicurezzaMessaggio_ssl_trustStore_crls_read= null;
	private String sicurezzaMessaggio_ssl_trustStore_crls= null;
	public String getSicurezzaMessaggio_ssl_trustStore_crls() throws Exception{
    	if(this.sicurezzaMessaggio_ssl_trustStore_crls_read==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.ssl.trustStore.crls"); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggio_ssl_trustStore_crls = value;
				}
				
				this.sicurezzaMessaggio_ssl_trustStore_crls_read = true;
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.ssl.trustStore.password' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return this.sicurezzaMessaggio_ssl_trustStore_crls;
	}
	
	
	
	
	
	
	
	/* **** KEY STORE **** */
		
	private String sicurezzaMessaggio_certificati_keyStore_tipo= null;
	private Boolean sicurezzaMessaggio_certificati_keyStore_tipo_read= null;
	public String getSicurezzaMessaggio_certificati_keyStore_tipo() {
    	if(this.sicurezzaMessaggio_certificati_keyStore_tipo_read==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.keyStore.tipo"); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggio_certificati_keyStore_tipo = value;
				}
				
				this.sicurezzaMessaggio_certificati_keyStore_tipo_read = true;
								
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.keyStore.tipo' non impostata, errore:"+e.getMessage());
				this.sicurezzaMessaggio_certificati_keyStore_tipo_read = true;
			}
    	}
    	
    	return this.sicurezzaMessaggio_certificati_keyStore_tipo;
	}	
	
	private String sicurezzaMessaggio_certificati_keyStore_path= null;
	public String getSicurezzaMessaggio_certificati_keyStore_path() throws Exception{
    	if(this.sicurezzaMessaggio_certificati_keyStore_path==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.keyStore.path"); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggio_certificati_keyStore_path = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.keyStore.path' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return this.sicurezzaMessaggio_certificati_keyStore_path;
	}
	
	private String sicurezzaMessaggio_certificati_keyStore_password= null;
	public String getSicurezzaMessaggio_certificati_keyStore_password() throws Exception{
    	if(this.sicurezzaMessaggio_certificati_keyStore_password==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.keyStore.password"); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggio_certificati_keyStore_password = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.keyStore.password' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return this.sicurezzaMessaggio_certificati_keyStore_password;
	}	
	
	private String sicurezzaMessaggio_certificati_key_alias= null;
	public String getSicurezzaMessaggio_certificati_key_alias() throws Exception{
    	if(this.sicurezzaMessaggio_certificati_key_alias==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.key.alias"); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggio_certificati_key_alias = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.key.alias' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return this.sicurezzaMessaggio_certificati_key_alias;
	}	
	
	private String sicurezzaMessaggio_certificati_key_password= null;
	public String getSicurezzaMessaggio_certificati_key_password() throws Exception{
    	if(this.sicurezzaMessaggio_certificati_key_password==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.key.password"); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggio_certificati_key_password = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.key.password' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return this.sicurezzaMessaggio_certificati_key_password;
	}	
	
	
	
	
	/* **** CORNICE SICUREZZA **** */
	
	private Boolean isSicurezzaMessaggio_corniceSicurezza_enabled = null;
	public Boolean isSicurezzaMessaggio_corniceSicurezza_enabled(){
		if(this.isSicurezzaMessaggio_corniceSicurezza_enabled==null){
			
			Boolean defaultValue = false;
			String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isSicurezzaMessaggio_corniceSicurezza_enabled = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprietà '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue);
					this.isSicurezzaMessaggio_corniceSicurezza_enabled = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.log.debug("Proprietà '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue+", errore:"+e.getMessage());
				this.isSicurezzaMessaggio_corniceSicurezza_enabled = defaultValue;
			}
		}

		return this.isSicurezzaMessaggio_corniceSicurezza_enabled;
	}
	
	private String sicurezzaMessaggio_corniceSicurezza_rest_codice_ente= null;
	public String getSicurezzaMessaggio_corniceSicurezza_rest_codice_ente() throws Exception{
    	if(this.sicurezzaMessaggio_corniceSicurezza_rest_codice_ente==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza.rest.codice_ente";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggio_corniceSicurezza_rest_codice_ente = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà '"+propertyName+"' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return this.sicurezzaMessaggio_corniceSicurezza_rest_codice_ente;
	}
	
	private String sicurezzaMessaggio_corniceSicurezza_rest_user= null;
	public String getSicurezzaMessaggio_corniceSicurezza_rest_user() throws Exception{
    	if(this.sicurezzaMessaggio_corniceSicurezza_rest_user==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza.rest.user";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggio_corniceSicurezza_rest_user = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà '"+propertyName+"' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return this.sicurezzaMessaggio_corniceSicurezza_rest_user;
	}
	
	private String sicurezzaMessaggio_corniceSicurezza_rest_ipuser= null;
	public String getSicurezzaMessaggio_corniceSicurezza_rest_ipuser() throws Exception{
    	if(this.sicurezzaMessaggio_corniceSicurezza_rest_ipuser==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza.rest.ipuser";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggio_corniceSicurezza_rest_ipuser = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà '"+propertyName+"' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return this.sicurezzaMessaggio_corniceSicurezza_rest_ipuser;
	}
	
	private String sicurezzaMessaggio_corniceSicurezza_soap_codice_ente= null;
	private Boolean sicurezzaMessaggio_corniceSicurezza_soap_codice_ente_read= null;
	public String getSicurezzaMessaggio_corniceSicurezza_soap_codice_ente() throws Exception{
    	if(this.sicurezzaMessaggio_corniceSicurezza_soap_codice_ente_read==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza.soap.codice_ente";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggio_corniceSicurezza_soap_codice_ente = value;
				}
				// In soap il codice utente viene inserito anche in saml2:Subject
//				else {
//					throw new Exception("non definita");
//				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà '"+propertyName+"' non impostata, errore:"+e.getMessage());
				throw e;
			}
    		
    		this.sicurezzaMessaggio_corniceSicurezza_soap_codice_ente_read = true;
    	}
    	
    	return this.sicurezzaMessaggio_corniceSicurezza_soap_codice_ente;
	}
	
	private String sicurezzaMessaggio_corniceSicurezza_soap_user= null;
	public String getSicurezzaMessaggio_corniceSicurezza_soap_user() throws Exception{
    	if(this.sicurezzaMessaggio_corniceSicurezza_soap_user==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza.soap.user";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggio_corniceSicurezza_soap_user = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà '"+propertyName+"' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return this.sicurezzaMessaggio_corniceSicurezza_soap_user;
	}
	
	private String sicurezzaMessaggio_corniceSicurezza_soap_ipuser= null;
	public String getSicurezzaMessaggio_corniceSicurezza_soap_ipuser() throws Exception{
    	if(this.sicurezzaMessaggio_corniceSicurezza_soap_ipuser==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza.soap.ipuser";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggio_corniceSicurezza_soap_ipuser = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà '"+propertyName+"' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return this.sicurezzaMessaggio_corniceSicurezza_soap_ipuser;
	}
	
	private List<String> sicurezzaMessaggio_corniceSicurezza_dynamic_codice_ente= null;
	public List<String> getSicurezzaMessaggio_corniceSicurezza_dynamic_codice_ente() throws Exception{
    	if(this.sicurezzaMessaggio_corniceSicurezza_dynamic_codice_ente==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza.codice_ente";
    		try{  
				//String value = this.reader.getValue_convertEnvProperties(propertyName);
    			String value = this.reader.getValue(propertyName); // contiene ${} da non risolvere
				this.sicurezzaMessaggio_corniceSicurezza_dynamic_codice_ente = ModISecurityConfig.convertToList(value);
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà '"+propertyName+"' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return this.sicurezzaMessaggio_corniceSicurezza_dynamic_codice_ente;
	}
	
	private List<String> sicurezzaMessaggio_corniceSicurezza_dynamic_user= null;
	public List<String> getSicurezzaMessaggio_corniceSicurezza_dynamic_user() throws Exception{
    	if(this.sicurezzaMessaggio_corniceSicurezza_dynamic_user==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza.user";
    		try{  
    			//String value = this.reader.getValue_convertEnvProperties(propertyName);
    			String value = this.reader.getValue(propertyName); // contiene ${} da non risolvere
				this.sicurezzaMessaggio_corniceSicurezza_dynamic_user = ModISecurityConfig.convertToList(value);
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà '"+propertyName+"' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return this.sicurezzaMessaggio_corniceSicurezza_dynamic_user;
	}
	
	private List<String> sicurezzaMessaggio_corniceSicurezza_dynamic_ipuser= null;
	public List<String> getSicurezzaMessaggio_corniceSicurezza_dynamic_ipuser() throws Exception{
    	if(this.sicurezzaMessaggio_corniceSicurezza_dynamic_ipuser==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza.ipuser";
    		try{  
    			//String value = this.reader.getValue_convertEnvProperties(propertyName);
    			String value = this.reader.getValue(propertyName); // contiene ${} da non risolvere
				this.sicurezzaMessaggio_corniceSicurezza_dynamic_ipuser = ModISecurityConfig.convertToList(value);
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà '"+propertyName+"' non impostata, errore:"+e.getMessage());
				throw e;
			}
    	}
    	
    	return this.sicurezzaMessaggio_corniceSicurezza_dynamic_ipuser;
	}
	
	
	
	
    /* **** TRACCE **** */ 
    
    private Boolean isGenerazioneTracce = null;
	public Boolean isGenerazioneTracce(){
		if(this.isGenerazioneTracce==null){
			
			Boolean defaultValue = false;
			String propertyName = "org.openspcoop2.protocol.modipa.generazioneTracce.enabled";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isGenerazioneTracce = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprietà '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue);
					this.isGenerazioneTracce = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.log.debug("Proprietà '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue+", errore:"+e.getMessage());
				this.isGenerazioneTracce = defaultValue;
			}
		}

		return this.isGenerazioneTracce;
	}
	
    private Boolean isGenerazioneTracce_registraToken = null;
	public Boolean isGenerazioneTracce_registraToken(){
		if(this.isGenerazioneTracce_registraToken==null){
			
			Boolean defaultValue = false;
			String propertyName = "org.openspcoop2.protocol.modipa.generazioneTracce.registrazioneToken.enabled";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isGenerazioneTracce_registraToken = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprietà '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue);
					this.isGenerazioneTracce_registraToken = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.log.debug("Proprietà '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue+", errore:"+e.getMessage());
				this.isGenerazioneTracce_registraToken = defaultValue;
			}
		}

		return this.isGenerazioneTracce_registraToken;
	}
	
	
	
	
    /* **** Nomenclatura **** */ 
    
    private Boolean isModIVersioneBozza = null;
	public Boolean isModIVersioneBozza(){
		if(this.isModIVersioneBozza==null){
			
			Boolean defaultValue = false;
			String propertyName = "org.openspcoop2.protocol.modipa.usaVersioneBozza";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isModIVersioneBozza = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprietà '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue);
					this.isModIVersioneBozza = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.log.debug("Proprietà '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue+", errore:"+e.getMessage());
				this.isModIVersioneBozza = defaultValue;
			}
		}

		return this.isModIVersioneBozza;
	}
	
	
	
	
	/* **** REST **** */ 
	
	private String getRestSecurityTokenHeader= null;
	public String getRestSecurityTokenHeaderModI() throws ProtocolException{
    	if(this.getRestSecurityTokenHeader==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.header";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenHeader = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getRestSecurityTokenHeader;
	}
	
	private Boolean getRestSecurityTokenClaimsIssuerEnabled_read= null;
	private Boolean getRestSecurityTokenClaimsIssuerEnabled= null;
	public boolean isRestSecurityTokenClaimsIssuerEnabled() throws Exception{
    	if(this.getRestSecurityTokenClaimsIssuerEnabled_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.iss.enabled";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenClaimsIssuerEnabled = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		this.getRestSecurityTokenClaimsIssuerEnabled_read = true;
    	}
    	
    	return this.getRestSecurityTokenClaimsIssuerEnabled;
	}
	private Boolean getRestSecurityTokenClaimsIssuerHeaderValue_read= null;
	private String getRestSecurityTokenClaimsIssuerHeaderValue= null;
	public String getRestSecurityTokenClaimsIssuerHeaderValue() throws Exception{
    	if(this.getRestSecurityTokenClaimsIssuerHeaderValue_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.iss";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenClaimsIssuerHeaderValue = value;
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		this.getRestSecurityTokenClaimsIssuerHeaderValue_read = true;
    	}
    	
    	return this.getRestSecurityTokenClaimsIssuerHeaderValue;
	}
	
	private Boolean getRestSecurityTokenClaimsSubjectEnabled_read= null;
	private Boolean getRestSecurityTokenClaimsSubjectEnabled= null;
	public boolean isRestSecurityTokenClaimsSubjectEnabled() throws Exception{
    	if(this.getRestSecurityTokenClaimsSubjectEnabled_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.sub.enabled";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenClaimsSubjectEnabled = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		this.getRestSecurityTokenClaimsSubjectEnabled_read = true;
    	}
    	
    	return this.getRestSecurityTokenClaimsSubjectEnabled;
	}
	private Boolean getRestSecurityTokenClaimsSubjectHeaderValue_read= null;
	private String getRestSecurityTokenClaimsSubjectHeaderValue= null;
	public String getRestSecurityTokenClaimsSubjectHeaderValue() throws Exception{
    	if(this.getRestSecurityTokenClaimsSubjectHeaderValue_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.sub";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenClaimsSubjectHeaderValue = value;
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		this.getRestSecurityTokenClaimsSubjectHeaderValue_read = true;
    	}
    	
    	return this.getRestSecurityTokenClaimsSubjectHeaderValue;
	}
	
	private String getRestSecurityTokenClaimsClientIdHeader= null;
	public String getRestSecurityTokenClaimsClientIdHeader() throws Exception{
    	if(this.getRestSecurityTokenClaimsClientIdHeader==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.client_id";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenClaimsClientIdHeader = value;
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
    	
    	return this.getRestSecurityTokenClaimsClientIdHeader;
	}
	
	private String getRestSecurityTokenClaimSignedHeaders= null;
	public String getRestSecurityTokenClaimSignedHeaders() throws Exception{
    	if(this.getRestSecurityTokenClaimSignedHeaders==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.signedHeaders";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenClaimSignedHeaders = value;
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
    	
    	return this.getRestSecurityTokenClaimSignedHeaders;
	}
	
	
	private String getRestSecurityTokenClaimRequestDigest= null;
	public String getRestSecurityTokenClaimRequestDigest() throws Exception{
    	if(this.getRestSecurityTokenClaimRequestDigest==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.requestDigest";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenClaimRequestDigest = value;
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
    	
    	return this.getRestSecurityTokenClaimRequestDigest;
	}
	
	
	private String [] getRestSecurityTokenSignedHeaders = null;
	public String [] getRestSecurityTokenSignedHeaders() throws Exception{
    	if(this.getRestSecurityTokenSignedHeaders==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.signedHeaders";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					this.getRestSecurityTokenSignedHeaders = new String[tmp.length];
					for (int i = 0; i < tmp.length; i++) {
						this.getRestSecurityTokenSignedHeaders[i] = tmp[i].trim();
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
    	
    	return this.getRestSecurityTokenSignedHeaders;
	}
	public String  getRestSecurityTokenSignedHeadersAsString() throws Exception{
		StringBuilder bf = new StringBuilder();
		for (String hdr : this.getRestSecurityTokenSignedHeaders) {
			if(bf.length()>0) {
				bf.append(",");
			}
			bf.append(hdr);
		}
		return bf.toString();
	}
	
	private Boolean getRestSecurityTokenClaimsIatTimeCheck_futureToleranceMilliseconds_read = null;
	private Long getRestSecurityTokenClaimsIatTimeCheck_futureToleranceMilliseconds = null;
	public Long getRestSecurityTokenClaimsIatTimeCheck_futureToleranceMilliseconds() throws Exception{

		if(this.getRestSecurityTokenClaimsIatTimeCheck_futureToleranceMilliseconds_read==null){
			
			String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.iat.future.toleranceMilliseconds";
			try{  
				String value = this.reader.getValue_convertEnvProperties(name); 

				if (value != null){
					value = value.trim();
					long tmp = Long.valueOf(value);
					if(tmp>0) {
						long maxLongValue = Long.MAX_VALUE;
						if(tmp>maxLongValue) {
							this.log.warn("Valore '"+value+"' indicato nella proprietà '"+name+"' superiore al massimo consentito '"+maxLongValue+"'; il controllo viene disabilitato");
						}
						else {
							this.getRestSecurityTokenClaimsIatTimeCheck_futureToleranceMilliseconds = tmp;
						}
					}
					else {
						this.log.warn("Verifica gestita tramite la proprietà '"+name+"' disabilitata.");
					}
				}
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà '"+name+"' non impostata, errore:"+e.getMessage(),e);
				throw e;
			}
			
			this.getRestSecurityTokenClaimsIatTimeCheck_futureToleranceMilliseconds_read = true;
		}

		return this.getRestSecurityTokenClaimsIatTimeCheck_futureToleranceMilliseconds;
	}
	
	private Boolean getRestSecurityTokenClaimsIatTimeCheck_milliseconds_read = null;
	private Long getRestSecurityTokenClaimsIatTimeCheck_milliseconds = null;
	public Long getRestSecurityTokenClaimsIatTimeCheck_milliseconds() throws Exception{

		if(this.getRestSecurityTokenClaimsIatTimeCheck_milliseconds_read==null){
			
			String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.iat.minutes";
			try{  
				String value = this.reader.getValue_convertEnvProperties(name); 

				if (value != null){
					value = value.trim();
					long tmp = Long.valueOf(value); // minuti
					if(tmp>0) {
						long maxLongValue = (((Long.MAX_VALUE)/60000l));
						if(tmp>maxLongValue) {
							this.log.warn("Valore '"+value+"' indicato nella proprietà '"+name+"' superiore al massimo consentito '"+maxLongValue+"'; il controllo viene disabilitato");
						}
						else {
							this.getRestSecurityTokenClaimsIatTimeCheck_milliseconds = tmp * 60 * 1000;
						}
					}
					else {
						this.log.warn("Verifica gestita tramite la proprietà '"+name+"' disabilitata.");
					}
				}
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà '"+name+"' non impostata, errore:"+e.getMessage(),e);
				throw e;
			}
			
			this.getRestSecurityTokenClaimsIatTimeCheck_milliseconds_read = true;
		}

		return this.getRestSecurityTokenClaimsIatTimeCheck_milliseconds;
	}
	
	private Boolean isRestSecurityTokenClaimsExpTimeCheck= null;
	public boolean isRestSecurityTokenClaimsExpTimeCheck() throws Exception{
    	if(this.isRestSecurityTokenClaimsExpTimeCheck==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.exp.checkEnabled";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.isRestSecurityTokenClaimsExpTimeCheck = Boolean.valueOf(value);
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
    	
    	return this.isRestSecurityTokenClaimsExpTimeCheck;
	}	
	
	private Boolean getRestSecurityTokenClaimsExpTimeCheck_toleranceMilliseconds_read = null;
	private Long getRestSecurityTokenClaimsExpTimeCheck_toleranceMilliseconds = null;
	public Long getRestSecurityTokenClaimsExpTimeCheck_toleranceMilliseconds() throws Exception{

		if(this.getRestSecurityTokenClaimsExpTimeCheck_toleranceMilliseconds_read==null){
			
			String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.exp.toleranceMilliseconds";
			try{  
				String value = this.reader.getValue_convertEnvProperties(name); 

				if (value != null){
					value = value.trim();
					long tmp = Long.valueOf(value); // già in millisecondi
					if(tmp>0) {
						long maxLongValue = Long.MAX_VALUE;
						if(tmp>maxLongValue) {
							this.log.warn("Valore '"+value+"' indicato nella proprietà '"+name+"' superiore al massimo consentito '"+maxLongValue+"'; il controllo viene disabilitato");
						}
						else {
							this.getRestSecurityTokenClaimsExpTimeCheck_toleranceMilliseconds = tmp;
						}
					}
					else {
						this.log.warn("Verifica gestita tramite la proprietà '"+name+"' disabilitata.");
					}
				}
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà '"+name+"' non impostata, errore:"+e.getMessage(),e);
				throw e;
			}
			
			this.getRestSecurityTokenClaimsExpTimeCheck_toleranceMilliseconds_read = true;
		}

		return this.getRestSecurityTokenClaimsExpTimeCheck_toleranceMilliseconds;
	}

	private DigestEncoding getRestSecurityTokenDigestDefaultEncoding= null;
	public DigestEncoding getRestSecurityTokenDigestDefaultEncoding() throws Exception{
    	if(this.getRestSecurityTokenDigestDefaultEncoding==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.digest.encoding";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenDigestDefaultEncoding = DigestEncoding.valueOf(value.toUpperCase());
					if(this.getRestSecurityTokenDigestDefaultEncoding==null) {
						throw new Exception("Invalid value");
					}
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore (valori ammessi: "+DigestEncoding.BASE64.name().toLowerCase()+","+DigestEncoding.HEX.name().toLowerCase()+"):"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return this.getRestSecurityTokenDigestDefaultEncoding;
	}
	
	private Boolean isRestSecurityTokenDigestEncodingChoice= null;
	public boolean isRestSecurityTokenDigestEncodingChoice() throws Exception{
    	if(this.isRestSecurityTokenDigestEncodingChoice==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.digest.encoding.choice";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.isRestSecurityTokenDigestEncodingChoice = Boolean.valueOf(value);
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
    	
    	return this.isRestSecurityTokenDigestEncodingChoice;
	}
	
	private List<DigestEncoding> getRestSecurityTokenDigestEncodingAccepted= null;
	public List<DigestEncoding> getRestSecurityTokenDigestEncodingAccepted() throws Exception{
    	if(this.getRestSecurityTokenDigestEncodingAccepted==null){
    		String name = "org.openspcoop2.protocol.modipa.rest.securityToken.digest.encoding.accepted";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					
					this.getRestSecurityTokenDigestEncodingAccepted = new ArrayList<DigestEncoding>();
					if(value.contains(",")) {
						String [] split = value.split(",");
						if(split==null || split.length<=0) {
							throw new Exception("Empty value");
						}
						for (String s : split) {
							if(s==null) {
								throw new Exception("Null value");
							}
							else {
								s = s.trim();
							}
							DigestEncoding tmp = DigestEncoding.valueOf(s.toUpperCase());
							if(tmp==null) {
								throw new Exception("Invalid value");
							}
							this.getRestSecurityTokenDigestEncodingAccepted.add(tmp);
						}
					}
					else {
						DigestEncoding tmp = DigestEncoding.valueOf(value.toUpperCase());
						if(tmp==null) {
							throw new Exception("Invalid value");
						}
						this.getRestSecurityTokenDigestEncodingAccepted.add(tmp);
					}
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore (valori ammessi: "+DigestEncoding.BASE64.name().toLowerCase()+","+DigestEncoding.HEX.name().toLowerCase()+"):"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    	}
    	
    	return this.getRestSecurityTokenDigestEncodingAccepted;
	}
	
	private Boolean getRestSecurityTokenRequestDigestClean_read= null;
	private Boolean getRestSecurityTokenRequestDigestClean= null;
	public boolean isRestSecurityTokenRequestDigestClean() throws Exception{
    	if(this.getRestSecurityTokenRequestDigestClean_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.request.digest.clean";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenRequestDigestClean = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		this.getRestSecurityTokenRequestDigestClean_read = true;
    	}
    	
    	return this.getRestSecurityTokenRequestDigestClean;
	}
	
	private Boolean getRestSecurityTokenResponseDigestClean_read= null;
	private Boolean getRestSecurityTokenResponseDigestClean= null;
	public boolean isRestSecurityTokenResponseDigestClean() throws Exception{
    	if(this.getRestSecurityTokenResponseDigestClean_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.response.digest.clean";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenResponseDigestClean = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		this.getRestSecurityTokenResponseDigestClean_read = true;
    	}
    	
    	return this.getRestSecurityTokenResponseDigestClean;
	}
	
	private Boolean getRestSecurityTokenResponseDigestHEADuseServerHeader_read= null;
	private Boolean getRestSecurityTokenResponseDigestHEADuseServerHeader= null;
	public boolean isRestSecurityTokenResponseDigestHEADuseServerHeader() throws Exception{
    	if(this.getRestSecurityTokenResponseDigestHEADuseServerHeader_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.response.digest.HEAD.useServerHeader";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenResponseDigestHEADuseServerHeader = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		this.getRestSecurityTokenResponseDigestHEADuseServerHeader_read = true;
    	}
    	
    	return this.getRestSecurityTokenResponseDigestHEADuseServerHeader;
	}
	
	private Boolean getRestSecurityTokenFaultProcessEnabled_read= null;
	private Boolean getRestSecurityTokenFaultProcessEnabled= null;
	public boolean isRestSecurityTokenFaultProcessEnabled() throws Exception{
    	if(this.getRestSecurityTokenFaultProcessEnabled_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.fault.securityToken";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenFaultProcessEnabled = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		this.getRestSecurityTokenFaultProcessEnabled_read = true;
    	}
    	
    	return this.getRestSecurityTokenFaultProcessEnabled;
	}
	
	private Boolean getRestResponseSecurityTokenAudienceDefault_read= null;
	private String getRestResponseSecurityTokenAudienceDefault= null;
	public String getRestResponseSecurityTokenAudienceDefault(String soggettoMittente) throws ProtocolException{
    	if(this.getRestResponseSecurityTokenAudienceDefault_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.response.securityToken.audience.default";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				if (value != null){
					value = value.trim();
					this.getRestResponseSecurityTokenAudienceDefault = value;
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getRestResponseSecurityTokenAudienceDefault_read = true;
    	}
    	
    	if(ModICostanti.CONFIG_MODIPA_SOGGETTO_MITTENTE_KEYWORD.equalsIgnoreCase(this.getRestResponseSecurityTokenAudienceDefault) && soggettoMittente!=null && !StringUtils.isEmpty(soggettoMittente)) {
			return soggettoMittente;
		}
    	else {
    		return this.getRestResponseSecurityTokenAudienceDefault;
    	}
	}	
	
	public List<String> getUsedRestSecurityClaims(boolean request, boolean integrita, boolean corniceSicurezza) throws Exception{
		List<String> l = new ArrayList<String>();
		
		l.add(Claims.JSON_WEB_TOKEN_RFC_7519_ISSUED_AT);
		l.add(Claims.JSON_WEB_TOKEN_RFC_7519_NOT_TO_BE_USED_BEFORE);
		l.add(Claims.JSON_WEB_TOKEN_RFC_7519_EXPIRED);
		l.add(Claims.JSON_WEB_TOKEN_RFC_7519_JWT_ID);
		
		if(request) {
			l.add(Claims.JSON_WEB_TOKEN_RFC_7519_AUDIENCE); // si configura sulla fruizione
			
			String v = getRestSecurityTokenClaimsClientIdHeader();
			if(v!=null && StringUtils.isNotEmpty(v)) {
				l.add(v); // si configura sull'applicativo
			}
		}
		
		if(!request) {
			String v = getRestSecurityTokenClaimRequestDigest();
			if(v!=null && StringUtils.isNotEmpty(v)) {
				l.add(v);
			}
		}
		
		/*
		 * Possono sempre essere definiti, poiche' utilizzati per sovrascrivere i default
		boolean addIss = true;
		boolean addSub = true;
		if(corniceSicurezza) {
			v = getSicurezzaMessaggio_corniceSicurezza_rest_codice_ente();
			if(v!=null && StringUtils.isNotEmpty(v)) {
				if(Claims.INTROSPECTION_RESPONSE_RFC_7662_ISSUER.equals(v)) {
					addIss = false;
				}
				l.add(v);
			}
			v = getSicurezzaMessaggio_corniceSicurezza_rest_user();
			if(v!=null && StringUtils.isNotEmpty(v)) {
				if(Claims.INTROSPECTION_RESPONSE_RFC_7662_SUBJECT.equals(v)) {
					addSub = false;
				}
				l.add(v);
			}
			v = getSicurezzaMessaggio_corniceSicurezza_rest_ipuser();
			if(v!=null && StringUtils.isNotEmpty(v)) {
				l.add(v);
			}
		}
		if(addIss) {
			l.add(Claims.INTROSPECTION_RESPONSE_RFC_7662_ISSUER);
		}
		if(addSub) {
			l.add(Claims.INTROSPECTION_RESPONSE_RFC_7662_SUBJECT);
		}*/
		
		if(integrita) {
			String v = getRestSecurityTokenClaimSignedHeaders();
			if(v!=null && StringUtils.isNotEmpty(v)) {
				l.add(v);
			}
		}
		
		return l;
	}
	
	private String getRestCorrelationIdHeader= null;
	public String getRestCorrelationIdHeader() throws Exception{
    	if(this.getRestCorrelationIdHeader==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.correlationId.header";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestCorrelationIdHeader = value;
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
    	
    	return this.getRestCorrelationIdHeader;
	}	
	
	private String getRestReplyToHeader= null;
	public String getRestReplyToHeader() throws Exception{
    	if(this.getRestReplyToHeader==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.replyTo.header";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestReplyToHeader = value;
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
    	
    	return this.getRestReplyToHeader;
	}
	
	private String getRestLocationHeader= null;
	public String getRestLocationHeader() throws Exception{
    	if(this.getRestLocationHeader==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.location.header";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestLocationHeader = value;
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
    	
    	return this.getRestLocationHeader;
	}
	
	private Boolean getRestProfiliInterazioneCheckCompatibility_read= null;
	private Boolean getRestProfiliInterazioneCheckCompatibility= null;
	public boolean isRestProfiliInterazioneCheckCompatibility() throws ProtocolException{
    	if(this.getRestProfiliInterazioneCheckCompatibility_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.profiliInterazione.checkCompatibility";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestProfiliInterazioneCheckCompatibility = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getRestProfiliInterazioneCheckCompatibility_read = true;
    	}
    	
    	return this.getRestProfiliInterazioneCheckCompatibility;
	}
	
	// .. BLOCCANTE ..
	
	private Integer [] getRestBloccanteHttpStatus = null;
	public Integer [] getRestBloccanteHttpStatus() throws ProtocolException{
    	if(this.getRestBloccanteHttpStatus==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.bloccante.httpStatus";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_HTTP_CODE_2XX.equalsIgnoreCase(value)) {
						this.getRestBloccanteHttpStatus = new Integer[1];
						this.getRestBloccanteHttpStatus[0] = ModICostanti.MODIPA_PROFILO_INTERAZIONE_HTTP_CODE_2XX_INT_VALUE;
					}
					else {
						String [] tmp = value.split(",");
						this.getRestBloccanteHttpStatus = new Integer[tmp.length];
						for (int i = 0; i < tmp.length; i++) {
							this.getRestBloccanteHttpStatus[i] = Integer.valueOf(tmp[i].trim());
						}
					}
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getRestBloccanteHttpStatus;
	}
	
	private List<HttpRequestMethod> getRestBloccanteHttpMethod = null;
	public List<HttpRequestMethod> getRestBloccanteHttpMethod() throws ProtocolException{
    	if(this.getRestBloccanteHttpMethod==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.bloccante.httpMethod";
    		try{
    			this.getRestBloccanteHttpMethod = new ArrayList<HttpRequestMethod>();
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					for (int i = 0; i < tmp.length; i++) {
						this.getRestBloccanteHttpMethod.add(HttpRequestMethod.valueOf(tmp[i].trim().toUpperCase()));
					}
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non corretta, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getRestBloccanteHttpMethod;
	}
	
	
	// .. PUSH ..
	
	private Boolean getRestSecurityTokenPushReplyToUpdateOrCreate = null;
	public boolean isRestSecurityTokenPushReplyToUpdateOrCreateInFruizione() throws Exception{
    	if(this.getRestSecurityTokenPushReplyToUpdateOrCreate==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.push.replyTo.header.updateOrCreate";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenPushReplyToUpdateOrCreate = Boolean.valueOf(value);
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
    	
    	return this.getRestSecurityTokenPushReplyToUpdateOrCreate;
	}
	
	private Boolean getRestSecurityTokenPushReplyToUpdate = null;
	public boolean isRestSecurityTokenPushReplyToUpdateInErogazione() throws Exception{
    	if(this.getRestSecurityTokenPushReplyToUpdate==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.push.replyTo.header.update";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenPushReplyToUpdate = Boolean.valueOf(value);
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
    	
    	return this.getRestSecurityTokenPushReplyToUpdate;
	}
	
	private Boolean getRestSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists = null;
	public boolean isRestSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists() throws Exception{
    	if(this.getRestSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.push.request.correlationId.header.useTransactionIdIfNotExists";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists = Boolean.valueOf(value);
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
    	
    	return this.getRestSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists;
	}
	
	private Integer [] getRestSecurityTokenPushRequestHttpStatus = null;
	public Integer [] getRestNonBloccantePushRequestHttpStatus() throws ProtocolException{
    	if(this.getRestSecurityTokenPushRequestHttpStatus==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.push.request.httpStatus";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					this.getRestSecurityTokenPushRequestHttpStatus = new Integer[tmp.length];
					for (int i = 0; i < tmp.length; i++) {
						this.getRestSecurityTokenPushRequestHttpStatus[i] = Integer.valueOf(tmp[i].trim());
					}
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getRestSecurityTokenPushRequestHttpStatus;
	}
	
	private List<HttpRequestMethod> getRestNonBloccantePushRequestHttpMethod = null;
	public List<HttpRequestMethod> getRestNonBloccantePushRequestHttpMethod() throws ProtocolException{
    	if(this.getRestNonBloccantePushRequestHttpMethod==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.push.request.httpMethod";
    		try{
    			this.getRestNonBloccantePushRequestHttpMethod = new ArrayList<HttpRequestMethod>();
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					for (int i = 0; i < tmp.length; i++) {
						this.getRestNonBloccantePushRequestHttpMethod.add(HttpRequestMethod.valueOf(tmp[i].trim().toUpperCase()));
					}
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non corretta, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getRestNonBloccantePushRequestHttpMethod;
	}
	
	private Integer [] getRestSecurityTokenPushResponseHttpStatus = null;
	public Integer [] getRestNonBloccantePushResponseHttpStatus() throws ProtocolException{
    	if(this.getRestSecurityTokenPushResponseHttpStatus==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.push.response.httpStatus";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					this.getRestSecurityTokenPushResponseHttpStatus = new Integer[tmp.length];
					for (int i = 0; i < tmp.length; i++) {
						this.getRestSecurityTokenPushResponseHttpStatus[i] = Integer.valueOf(tmp[i].trim());
					}
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getRestSecurityTokenPushResponseHttpStatus;
	}
	
	private List<HttpRequestMethod> getRestNonBloccantePushResponseHttpMethod = null;
	public List<HttpRequestMethod> getRestNonBloccantePushResponseHttpMethod() throws ProtocolException{
    	if(this.getRestNonBloccantePushResponseHttpMethod==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.push.response.httpMethod";
    		try{
    			this.getRestNonBloccantePushResponseHttpMethod = new ArrayList<HttpRequestMethod>();
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					for (int i = 0; i < tmp.length; i++) {
						this.getRestNonBloccantePushResponseHttpMethod.add(HttpRequestMethod.valueOf(tmp[i].trim().toUpperCase()));
					}
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non corretta, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getRestNonBloccantePushResponseHttpMethod;
	}
	
	private List<HttpRequestMethod> getRestNonBloccantePushHttpMethod = null;
	public List<HttpRequestMethod> getRestNonBloccantePushHttpMethod() throws ProtocolException{
		
		if(this.getRestNonBloccantePushHttpMethod!=null) {
			return this.getRestNonBloccantePushHttpMethod;
		}
		
		this.getRestNonBloccantePushHttpMethod = new ArrayList<HttpRequestMethod>();
		
		List<HttpRequestMethod> req = getRestNonBloccantePushRequestHttpMethod();
		if(req!=null && !req.isEmpty()){
			this.getRestNonBloccantePushHttpMethod.addAll(req);
		}
		
		List<HttpRequestMethod> res = getRestNonBloccantePushResponseHttpMethod();
		if(res!=null && !res.isEmpty()){
			for (HttpRequestMethod httpRequestMethod : res) {
				if(!this.getRestNonBloccantePushHttpMethod.contains(httpRequestMethod)) {
					this.getRestNonBloccantePushHttpMethod.add(httpRequestMethod);
				}
			}
		}
		
		return this.getRestNonBloccantePushHttpMethod;
	}
	
	// .. PULL ..
	
	private Integer [] getRestSecurityTokenPullRequestHttpStatus = null;
	public Integer [] getRestNonBloccantePullRequestHttpStatus() throws ProtocolException{
    	if(this.getRestSecurityTokenPullRequestHttpStatus==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.pull.request.httpStatus";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					this.getRestSecurityTokenPullRequestHttpStatus = new Integer[tmp.length];
					for (int i = 0; i < tmp.length; i++) {
						this.getRestSecurityTokenPullRequestHttpStatus[i] = Integer.valueOf(tmp[i].trim());
					}
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getRestSecurityTokenPullRequestHttpStatus;
	}
	
	private List<HttpRequestMethod> getRestNonBloccantePullRequestHttpMethod = null;
	public List<HttpRequestMethod> getRestNonBloccantePullRequestHttpMethod() throws ProtocolException{
    	if(this.getRestNonBloccantePullRequestHttpMethod==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.pull.request.httpMethod";
    		try{
    			this.getRestNonBloccantePullRequestHttpMethod = new ArrayList<HttpRequestMethod>();
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					for (int i = 0; i < tmp.length; i++) {
						this.getRestNonBloccantePullRequestHttpMethod.add(HttpRequestMethod.valueOf(tmp[i].trim().toUpperCase()));
					}
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non corretta, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getRestNonBloccantePullRequestHttpMethod;
	}
	
	private Integer [] getRestSecurityTokenPullRequestStateNotReadyHttpStatus = null;
	public Integer [] getRestNonBloccantePullRequestStateNotReadyHttpStatus() throws ProtocolException{
    	if(this.getRestSecurityTokenPullRequestStateNotReadyHttpStatus==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.pull.requestState.notReady.httpStatus";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					this.getRestSecurityTokenPullRequestStateNotReadyHttpStatus = new Integer[tmp.length];
					for (int i = 0; i < tmp.length; i++) {
						this.getRestSecurityTokenPullRequestStateNotReadyHttpStatus[i] = Integer.valueOf(tmp[i].trim());
					}
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getRestSecurityTokenPullRequestStateNotReadyHttpStatus;
	}
	
	private Integer [] getRestSecurityTokenPullRequestStateOkHttpStatus = null;
	public Integer [] getRestNonBloccantePullRequestStateOkHttpStatus() throws ProtocolException{
    	if(this.getRestSecurityTokenPullRequestStateOkHttpStatus==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.pull.requestState.ok.httpStatus";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					this.getRestSecurityTokenPullRequestStateOkHttpStatus = new Integer[tmp.length];
					for (int i = 0; i < tmp.length; i++) {
						this.getRestSecurityTokenPullRequestStateOkHttpStatus[i] = Integer.valueOf(tmp[i].trim());
					}
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getRestSecurityTokenPullRequestStateOkHttpStatus;
	}
	
	private List<HttpRequestMethod> getRestNonBloccantePullRequestStateHttpMethod = null;
	public List<HttpRequestMethod> getRestNonBloccantePullRequestStateHttpMethod() throws ProtocolException{
    	if(this.getRestNonBloccantePullRequestStateHttpMethod==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.pull.requestState.httpMethod";
    		try{
    			this.getRestNonBloccantePullRequestStateHttpMethod = new ArrayList<HttpRequestMethod>();
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					for (int i = 0; i < tmp.length; i++) {
						this.getRestNonBloccantePullRequestStateHttpMethod.add(HttpRequestMethod.valueOf(tmp[i].trim().toUpperCase()));
					}
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non corretta, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getRestNonBloccantePullRequestStateHttpMethod;
	}
	
	private Integer [] getRestSecurityTokenPullResponseHttpStatus = null;
	public Integer [] getRestNonBloccantePullResponseHttpStatus() throws ProtocolException{
    	if(this.getRestSecurityTokenPullResponseHttpStatus==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.pull.response.httpStatus";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					this.getRestSecurityTokenPullResponseHttpStatus = new Integer[tmp.length];
					for (int i = 0; i < tmp.length; i++) {
						this.getRestSecurityTokenPullResponseHttpStatus[i] = Integer.valueOf(tmp[i].trim());
					}
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getRestSecurityTokenPullResponseHttpStatus;
	}
	
	private List<HttpRequestMethod> getRestNonBloccantePullResponseHttpMethod = null;
	public List<HttpRequestMethod> getRestNonBloccantePullResponseHttpMethod() throws ProtocolException{
    	if(this.getRestNonBloccantePullResponseHttpMethod==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.pull.response.httpMethod";
    		try{
    			this.getRestNonBloccantePullResponseHttpMethod = new ArrayList<HttpRequestMethod>();
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					for (int i = 0; i < tmp.length; i++) {
						this.getRestNonBloccantePullResponseHttpMethod.add(HttpRequestMethod.valueOf(tmp[i].trim().toUpperCase()));
					}
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non corretta, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getRestNonBloccantePullResponseHttpMethod;
	}
	
	private List<HttpRequestMethod> getRestNonBloccantePullHttpMethod = null;
	public List<HttpRequestMethod> getRestNonBloccantePullHttpMethod() throws ProtocolException{
		
		if(this.getRestNonBloccantePullHttpMethod!=null) {
			return this.getRestNonBloccantePullHttpMethod;
		}
		
		this.getRestNonBloccantePullHttpMethod = new ArrayList<HttpRequestMethod>();
		
		List<HttpRequestMethod> req = getRestNonBloccantePullRequestHttpMethod();
		if(req!=null && !req.isEmpty()){
			this.getRestNonBloccantePullHttpMethod.addAll(req);
		}
		
		List<HttpRequestMethod> reqState = getRestNonBloccantePullRequestStateHttpMethod();
		if(reqState!=null && !reqState.isEmpty()){
			for (HttpRequestMethod httpRequestMethod : reqState) {
				if(!this.getRestNonBloccantePullHttpMethod.contains(httpRequestMethod)) {
					this.getRestNonBloccantePullHttpMethod.add(httpRequestMethod);
				}
			}
		}
		
		List<HttpRequestMethod> res = getRestNonBloccantePullResponseHttpMethod();
		if(res!=null && !res.isEmpty()){
			for (HttpRequestMethod httpRequestMethod : res) {
				if(!this.getRestNonBloccantePullHttpMethod.contains(httpRequestMethod)) {
					this.getRestNonBloccantePullHttpMethod.add(httpRequestMethod);
				}
			}
		}
		
		return this.getRestNonBloccantePullHttpMethod;
	}
	
	
	/* **** SOAP **** */ 
	
	private Boolean getSoapSecurityTokenMustUnderstand_read= null;
	private Boolean getSoapSecurityTokenMustUnderstand= null;
	public boolean isSoapSecurityTokenMustUnderstand() throws Exception{
    	if(this.getSoapSecurityTokenMustUnderstand_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.securityToken.mustUnderstand";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapSecurityTokenMustUnderstand = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		this.getSoapSecurityTokenMustUnderstand_read = true;
    	}
    	
    	return this.getSoapSecurityTokenMustUnderstand;
	}	
	
	private Boolean getSoapSecurityTokenActor_read= null;
	private String getSoapSecurityTokenActor= null;
	public String getSoapSecurityTokenActor() throws Exception{
    	if(this.getSoapSecurityTokenActor_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.securityToken.actor";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					if(!"".equals(value)) {
						this.getSoapSecurityTokenActor = value;
					}
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		this.getSoapSecurityTokenActor_read = true;
    	}
    	
    	return this.getSoapSecurityTokenActor;
	}
	
	private Boolean getSoapSecurityTokenTimestampCreatedTimeCheck_milliseconds_read = null;
	private Long getSoapSecurityTokenTimestampCreatedTimeCheck_milliseconds = null;
	public Long getSoapSecurityTokenTimestampCreatedTimeCheck_milliseconds() throws Exception{

		if(this.getSoapSecurityTokenTimestampCreatedTimeCheck_milliseconds_read==null){
			
			String name = "org.openspcoop2.protocol.modipa.soap.securityToken.timestamp.created.minutes";
			try{  
				String value = this.reader.getValue_convertEnvProperties(name); 

				if (value != null){
					value = value.trim();
					long tmp = Long.valueOf(value); // minuti
					if(tmp>0) {
						long maxLongValue = (((Long.MAX_VALUE)/60000l));
						if(tmp>maxLongValue) {
							this.log.warn("Valore '"+value+"' indicato nella proprietà '"+name+"' superiore al massimo consentito '"+maxLongValue+"'; il controllo viene disabilitato");
						}
						else {
							this.getSoapSecurityTokenTimestampCreatedTimeCheck_milliseconds = tmp * 60 * 1000;
						}
					}
					else {
						this.log.warn("Verifica gestita tramite la proprietà '"+name+"' disabilitata.");
					}
				}
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà '"+name+"' non impostata, errore:"+e.getMessage(),e);
				throw e;
			}
			
			this.getSoapSecurityTokenTimestampCreatedTimeCheck_milliseconds_read = true;
		}

		return this.getSoapSecurityTokenTimestampCreatedTimeCheck_milliseconds;
	}
	
	private Boolean getSoapSecurityTokenTimestampCreatedTimeCheck_futureToleranceMilliseconds_read = null;
	private Long getSoapSecurityTokenTimestampCreatedTimeCheck_futureToleranceMilliseconds = null;
	public Long getSoapSecurityTokenTimestampCreatedTimeCheck_futureToleranceMilliseconds() throws Exception{

		if(this.getSoapSecurityTokenTimestampCreatedTimeCheck_futureToleranceMilliseconds_read==null){
			
			String name = "org.openspcoop2.protocol.modipa.soap.securityToken.timestamp.created.future.toleranceMilliseconds";
			try{  
				String value = this.reader.getValue_convertEnvProperties(name); 

				if (value != null){
					value = value.trim();
					long tmp = Long.valueOf(value); // già in millisecondi
					if(tmp>0) {
						long maxLongValue = Long.MAX_VALUE;
						if(tmp>maxLongValue) {
							this.log.warn("Valore '"+value+"' indicato nella proprietà '"+name+"' superiore al massimo consentito '"+maxLongValue+"'; il controllo viene disabilitato");
						}
						else {
							this.getSoapSecurityTokenTimestampCreatedTimeCheck_futureToleranceMilliseconds = tmp;
						}
					}
					else {
						this.log.warn("Verifica gestita tramite la proprietà '"+name+"' disabilitata.");
					}
				}
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà '"+name+"' non impostata, errore:"+e.getMessage(),e);
				throw e;
			}
			
			this.getSoapSecurityTokenTimestampCreatedTimeCheck_futureToleranceMilliseconds_read = true;
		}

		return this.getSoapSecurityTokenTimestampCreatedTimeCheck_futureToleranceMilliseconds;
	}
	
	private Boolean getSoapSecurityTokenFaultProcessEnabled_read= null;
	private Boolean getSoapSecurityTokenFaultProcessEnabled= null;
	public boolean isSoapSecurityTokenFaultProcessEnabled() throws Exception{
    	if(this.getSoapSecurityTokenFaultProcessEnabled_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.fault.securityToken";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapSecurityTokenFaultProcessEnabled = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		this.getSoapSecurityTokenFaultProcessEnabled_read = true;
    	}
    	
    	return this.getSoapSecurityTokenFaultProcessEnabled;
	}
	
	private Boolean isSoapSecurityTokenTimestampExpiresTimeCheck= null;
	public boolean isSoapSecurityTokenTimestampExpiresTimeCheck() throws Exception{
    	if(this.isSoapSecurityTokenTimestampExpiresTimeCheck==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.securityToken.timestamp.expires.checkEnabled";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.isSoapSecurityTokenTimestampExpiresTimeCheck = Boolean.valueOf(value);
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
    	
    	return this.isSoapSecurityTokenTimestampExpiresTimeCheck;
	}	
	
	private Boolean getSoapSecurityTokenTimestampExpiresTimeCheck_toleranceMilliseconds_read = null;
	private Long getSoapSecurityTokenTimestampExpiresTimeCheck_toleranceMilliseconds = null;
	public Long getSoapSecurityTokenTimestampExpiresTimeCheck_toleranceMilliseconds() throws Exception{

		if(this.getSoapSecurityTokenTimestampExpiresTimeCheck_toleranceMilliseconds_read==null){
			
			String name = "org.openspcoop2.protocol.modipa.soap.securityToken.timestamp.expires.toleranceMilliseconds";
			try{  
				String value = this.reader.getValue_convertEnvProperties(name); 

				if (value != null){
					value = value.trim();
					long tmp = Long.valueOf(value); // già in millisecondi
					if(tmp>0) {
						long maxLongValue = Long.MAX_VALUE;
						if(tmp>maxLongValue) {
							this.log.warn("Valore '"+value+"' indicato nella proprietà '"+name+"' superiore al massimo consentito '"+maxLongValue+"'; il controllo viene disabilitato");
						}
						else {
							this.getSoapSecurityTokenTimestampExpiresTimeCheck_toleranceMilliseconds = tmp;
						}
					}
					else {
						this.log.warn("Verifica gestita tramite la proprietà '"+name+"' disabilitata.");
					}
				}
			}catch(java.lang.Exception e) {
				this.log.error("Proprietà '"+name+"' non impostata, errore:"+e.getMessage(),e);
				throw e;
			}
			
			this.getSoapSecurityTokenTimestampExpiresTimeCheck_toleranceMilliseconds_read = true;
		}

		return this.getSoapSecurityTokenTimestampExpiresTimeCheck_toleranceMilliseconds;
	}
	
	private Boolean getSoapWSAddressingMustUnderstand_read= null;
	private Boolean getSoapWSAddressingMustUnderstand= null;
	public boolean isSoapWSAddressingMustUnderstand() throws Exception{
    	if(this.getSoapWSAddressingMustUnderstand_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.wsaddressing.mustUnderstand";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapWSAddressingMustUnderstand = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		this.getSoapWSAddressingMustUnderstand_read = true;
    	}
    	
    	return this.getSoapWSAddressingMustUnderstand;
	}	
	
	private Boolean getSoapWSAddressingActor_read= null;
	private String getSoapWSAddressingActor= null;
	public String getSoapWSAddressingActor() throws Exception{
    	if(this.getSoapWSAddressingActor_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.wsaddressing.actor";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					if(!"".equals(value)) {
						this.getSoapWSAddressingActor = value;
					}
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		this.getSoapWSAddressingActor_read = true;
    	}
    	
    	return this.getSoapWSAddressingActor;
	}
	
	private Boolean getSoapWSAddressingSchemaValidation_read= null;
	private Boolean getSoapWSAddressingSchemaValidation= null;
	public boolean isSoapWSAddressingSchemaValidation() throws Exception{
    	if(this.getSoapWSAddressingSchemaValidation_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.wsaddressing.schemaValidation";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapWSAddressingSchemaValidation = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		this.getSoapWSAddressingSchemaValidation_read = true;
    	}
    	
    	return this.getSoapWSAddressingSchemaValidation;
	}	
	
	
	private String getSoapCorrelationIdName= null;
	public String getSoapCorrelationIdName() throws Exception{
    	if(this.getSoapCorrelationIdName==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.correlationId.name";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapCorrelationIdName = value;
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
    	
    	return this.getSoapCorrelationIdName;
	}
	
	private String getSoapCorrelationIdNamespace= null;
	public String getSoapCorrelationIdNamespace() throws Exception{
    	if(this.getSoapCorrelationIdNamespace==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.correlationId.namespace";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapCorrelationIdNamespace = value;
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
    	
    	return this.getSoapCorrelationIdNamespace;
	}
	
	public boolean useSoapBodyCorrelationIdNamespace() throws Exception {
		return ModICostanti.MODIPA_USE_BODY_NAMESPACE.equals(this.getSoapCorrelationIdNamespace());
	}
	
	private String getSoapCorrelationIdPrefix= null;
	public String getSoapCorrelationIdPrefix() throws Exception{
    	if(this.getSoapCorrelationIdPrefix==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.correlationId.prefix";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapCorrelationIdPrefix = value;
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
    	
    	return this.getSoapCorrelationIdPrefix;
	}
	
	private Boolean getSoapCorrelationIdMustUnderstand_read= null;
	private Boolean getSoapCorrelationIdMustUnderstand= null;
	public boolean isSoapCorrelationIdMustUnderstand() throws Exception{
    	if(this.getSoapCorrelationIdMustUnderstand_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.correlationId.mustUnderstand";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapCorrelationIdMustUnderstand = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		this.getSoapCorrelationIdMustUnderstand_read = true;
    	}
    	
    	return this.getSoapCorrelationIdMustUnderstand;
	}	
	
	private Boolean getSoapCorrelationIdActor_read= null;
	private String getSoapCorrelationIdActor= null;
	public String getSoapCorrelationIdActor() throws Exception{
    	if(this.getSoapCorrelationIdActor_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.correlationId.actor";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					if(!"".equals(value)) {
						this.getSoapCorrelationIdActor = value;
					}
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		this.getSoapCorrelationIdActor_read = true;
    	}
    	
    	return this.getSoapCorrelationIdActor;
	}
	
	
	
	
	private String getSoapReplyToName= null;
	public String getSoapReplyToName() throws Exception{
    	if(this.getSoapReplyToName==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.replyTo.name";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapReplyToName = value;
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
    	
    	return this.getSoapReplyToName;
	}
	
	private String getSoapReplyToNamespace= null;
	public String getSoapReplyToNamespace() throws Exception{
    	if(this.getSoapReplyToNamespace==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.replyTo.namespace";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapReplyToNamespace = value;
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
    	
    	return this.getSoapReplyToNamespace;
	}
	
	public boolean useSoapBodyReplyToNamespace() throws Exception {
		return ModICostanti.MODIPA_USE_BODY_NAMESPACE.equals(this.getSoapReplyToNamespace());
	}
	
	private String getSoapReplyToPrefix= null;
	public String getSoapReplyToPrefix() throws Exception{
    	if(this.getSoapReplyToPrefix==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.replyTo.prefix";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapReplyToPrefix = value;
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
    	
    	return this.getSoapReplyToPrefix;
	}
	
	private Boolean getSoapReplyToMustUnderstand_read= null;
	private Boolean getSoapReplyToMustUnderstand= null;
	public boolean isSoapReplyToMustUnderstand() throws Exception{
    	if(this.getSoapReplyToMustUnderstand_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.replyTo.mustUnderstand";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapReplyToMustUnderstand = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		this.getSoapReplyToMustUnderstand_read = true;
    	}
    	
    	return this.getSoapReplyToMustUnderstand;
	}	
	
	private Boolean getSoapReplyToActor_read= null;
	private String getSoapReplyToActor= null;
	public String getSoapReplyToActor() throws Exception{
    	if(this.getSoapReplyToActor_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.replyTo.actor";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					if(!"".equals(value)) {
						this.getSoapReplyToActor = value;
					}
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		this.getSoapReplyToActor_read = true;
    	}
    	
    	return this.getSoapReplyToActor;
	}
	
	
	private String getSoapRequestDigestName= null;
	public String getSoapRequestDigestName() throws Exception{
    	if(this.getSoapRequestDigestName==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.requestDigest.name";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapRequestDigestName = value;
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
    	
    	return this.getSoapRequestDigestName;
	}
	
	private String getSoapRequestDigestNamespace= null;
	public String getSoapRequestDigestNamespace() throws Exception{
    	if(this.getSoapRequestDigestNamespace==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.requestDigest.namespace";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapRequestDigestNamespace = value;
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
    	
    	return this.getSoapRequestDigestNamespace;
	}
	
	public boolean useSoapBodyRequestDigestNamespace() throws Exception {
		return ModICostanti.MODIPA_USE_BODY_NAMESPACE.equals(this.getSoapRequestDigestNamespace());
	}
	
	private String getSoapRequestDigestPrefix= null;
	public String getSoapRequestDigestPrefix() throws Exception{
    	if(this.getSoapRequestDigestPrefix==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.requestDigest.prefix";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapRequestDigestPrefix = value;
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
    	
    	return this.getSoapRequestDigestPrefix;
	}
	
	private Boolean getSoapRequestDigestMustUnderstand_read= null;
	private Boolean getSoapRequestDigestMustUnderstand= null;
	public boolean isSoapRequestDigestMustUnderstand() throws Exception{
    	if(this.getSoapRequestDigestMustUnderstand_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.requestDigest.mustUnderstand";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapRequestDigestMustUnderstand = Boolean.valueOf(value);
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		this.getSoapRequestDigestMustUnderstand_read = true;
    	}
    	
    	return this.getSoapRequestDigestMustUnderstand;
	}	
	
	private Boolean getSoapRequestDigestActor_read= null;
	private String getSoapRequestDigestActor= null;
	public String getSoapRequestDigestActor() throws Exception{
    	if(this.getSoapRequestDigestActor_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.requestDigest.actor";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					if(!"".equals(value)) {
						this.getSoapRequestDigestActor = value;
					}
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new Exception(msgErrore,e);
			}
    		
    		this.getSoapRequestDigestActor_read = true;
    	}
    	
    	return this.getSoapRequestDigestActor;
	}
	
	private Boolean getSoapSecurityTokenWsaTo_read= null;
	private String getSoapSecurityTokenWsaTo= null;
	private String getSoapSecurityTokenWsaTo() throws ProtocolException{
    	if(this.getSoapSecurityTokenWsaTo_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.securityToken.wsaTo";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				if (value != null){
					value = value.trim();
					this.getSoapSecurityTokenWsaTo = value;
				}
				else {
					throw new Exception("non definita");
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getSoapSecurityTokenWsaTo_read = true;
    	}
    	
    	return this.getSoapSecurityTokenWsaTo;
	}
	private Boolean getSoapSecurityTokenWsaTo_soapAction= null;
	private Boolean getSoapSecurityTokenWsaTo_operation= null;
	private Boolean getSoapSecurityTokenWsaTo_none= null;
	public boolean isSoapSecurityTokenWsaToSoapAction() throws ProtocolException {
		if(this.getSoapSecurityTokenWsaTo_soapAction==null) {
			this.getSoapSecurityTokenWsaTo_soapAction = ModICostanti.CONFIG_MODIPA_SOAP_SECURITY_TOKEN_WSA_TO_KEYWORD_SOAP_ACTION.equalsIgnoreCase(getSoapSecurityTokenWsaTo());
		}
		return this.getSoapSecurityTokenWsaTo_soapAction;
	}
	public boolean isSoapSecurityTokenWsaToOperation() throws ProtocolException {
		if(this.getSoapSecurityTokenWsaTo_operation==null) {
			this.getSoapSecurityTokenWsaTo_operation = ModICostanti.CONFIG_MODIPA_SOAP_SECURITY_TOKEN_WSA_TO_KEYWORD_OPERATION.equalsIgnoreCase(getSoapSecurityTokenWsaTo());
		}
		return this.getSoapSecurityTokenWsaTo_operation;
	}
	public boolean isSoapSecurityTokenWsaToDisabled() throws ProtocolException {
		if(this.getSoapSecurityTokenWsaTo_none==null) {
			this.getSoapSecurityTokenWsaTo_none = ModICostanti.CONFIG_MODIPA_SOAP_SECURITY_TOKEN_WSA_TO_KEYWORD_NONE.equalsIgnoreCase(getSoapSecurityTokenWsaTo());
		}
		return this.getSoapSecurityTokenWsaTo_none;
	}
	
	private Boolean getSoapResponseSecurityTokenAudienceDefault_read= null;
	private String getSoapResponseSecurityTokenAudienceDefault= null;
	public String getSoapResponseSecurityTokenAudienceDefault(String soggettoMittente) throws ProtocolException{
    	if(this.getSoapResponseSecurityTokenAudienceDefault_read==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.response.securityToken.audience.default";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				if (value != null){
					value = value.trim();
					this.getSoapResponseSecurityTokenAudienceDefault = value;
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = "Proprietà '"+name+"' non impostata, errore:"+e.getMessage(); 
				this.log.error(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getSoapResponseSecurityTokenAudienceDefault_read = true;
    	}
    	
    	if(ModICostanti.CONFIG_MODIPA_SOGGETTO_MITTENTE_KEYWORD.equalsIgnoreCase(this.getSoapResponseSecurityTokenAudienceDefault) && soggettoMittente!=null && !StringUtils.isEmpty(soggettoMittente)) {
			return soggettoMittente;
		}
    	else {
    		return this.getSoapResponseSecurityTokenAudienceDefault;
    	}
	}
	
	// .. PUSH ..
	
	private Boolean getSoapSecurityTokenPushReplyToUpdateOrCreate = null;
	public boolean isSoapSecurityTokenPushReplyToUpdateOrCreateInFruizione() throws Exception{
    	if(this.getSoapSecurityTokenPushReplyToUpdateOrCreate==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.push.replyTo.header.updateOrCreate";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapSecurityTokenPushReplyToUpdateOrCreate = Boolean.valueOf(value);
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
    	
    	return this.getSoapSecurityTokenPushReplyToUpdateOrCreate;
	}
	
	private Boolean getSoapSecurityTokenPushReplyToUpdate = null;
	public boolean isSoapSecurityTokenPushReplyToUpdateInErogazione() throws Exception{
    	if(this.getSoapSecurityTokenPushReplyToUpdate==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.push.replyTo.header.update";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapSecurityTokenPushReplyToUpdate = Boolean.valueOf(value);
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
    	
    	return this.getSoapSecurityTokenPushReplyToUpdate;
	}
	
	private Boolean getSoapSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists = null;
	public boolean isSoapSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists() throws Exception{
    	if(this.getSoapSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.push.request.correlationId.header.useTransactionIdIfNotExists";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists = Boolean.valueOf(value);
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
    	
    	return this.getSoapSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists;
	}
	
	private Boolean getSoapSecurityTokenPullCorrelationIdUseTransactionIdIfNotExists = null;
	public boolean isSoapSecurityTokenPullCorrelationIdUseTransactionIdIfNotExists() throws Exception{
    	if(this.getSoapSecurityTokenPullCorrelationIdUseTransactionIdIfNotExists==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.pull.request.correlationId.header.useTransactionIdIfNotExists";
    		try{  
				String value = this.reader.getValue_convertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapSecurityTokenPullCorrelationIdUseTransactionIdIfNotExists = Boolean.valueOf(value);
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
    	
    	return this.getSoapSecurityTokenPullCorrelationIdUseTransactionIdIfNotExists;
	}
	
	
	
	
	/* **** CONFIGURAZIONE **** */
	
	private Boolean isReadByPathBufferEnabled = null;
	public Boolean isReadByPathBufferEnabled(){
    	if(this.isReadByPathBufferEnabled==null){
    		String pName = "org.openspcoop2.protocol.modipa.readByPath.buffer";
	    	try{  
				String value = this.reader.getValue_convertEnvProperties(pName); 
				
				if (value != null){
					value = value.trim();
					this.isReadByPathBufferEnabled = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprietà '"+pName+"' non impostata, viene utilizzato il default 'true'");
					this.isReadByPathBufferEnabled = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà '"+pName+"' non impostata, viene utilizzato il default 'true', errore:"+e.getMessage());
				this.isReadByPathBufferEnabled = true;
			}
    	}
    	
    	return this.isReadByPathBufferEnabled;
	}
	
	private Boolean isValidazioneBufferEnabled = null;
	public Boolean isValidazioneBufferEnabled(){
    	if(this.isValidazioneBufferEnabled==null){
    		String pName = "org.openspcoop2.protocol.modipa.validazione.buffer";
	    	try{  
				String value = this.reader.getValue_convertEnvProperties(pName); 
				
				if (value != null){
					value = value.trim();
					this.isValidazioneBufferEnabled = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprietà '"+pName+"' non impostata, viene utilizzato il default 'true'");
					this.isValidazioneBufferEnabled = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà '"+pName+"' non impostata, viene utilizzato il default 'true', errore:"+e.getMessage());
				this.isValidazioneBufferEnabled = true;
			}
    	}
    	
    	return this.isValidazioneBufferEnabled;
	}
	
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
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.pd.riferimentoIdRichiesta.required"); 
				
				if (value != null){
					value = value.trim();
					this.isRiferimentoIDRichiesta_PD_Required = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprietà 'org.openspcoop2.protocol.modipa.pd.riferimentoIdRichiesta.required' non impostata, viene utilizzato il default 'true'");
					this.isRiferimentoIDRichiesta_PD_Required = true;
				}
				
				this.isRiferimentoIDRichiesta_PD_RequiredRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.pd.riferimentoIdRichiesta.required' non impostata, viene utilizzato il default 'true', errore:"+e.getMessage());
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
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.pa.riferimentoIdRichiesta.required"); 
				
				if (value != null){
					value = value.trim();
					this.isRiferimentoIDRichiesta_PA_Required = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprietà 'org.openspcoop2.protocol.modipa.pa.riferimentoIdRichiesta.required' non impostata, viene utilizzato il default 'true'");
					this.isRiferimentoIDRichiesta_PA_Required = true;
				}
				
				this.isRiferimentoIDRichiesta_PA_RequiredRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.pa.riferimentoIdRichiesta.required' non impostata, viene utilizzato il default 'true', errore:"+e.getMessage());
				this.isRiferimentoIDRichiesta_PA_Required = true;
				
				this.isRiferimentoIDRichiesta_PA_RequiredRead = true;
			}
    	}
    	
    	return this.isRiferimentoIDRichiesta_PA_Required;
	}
	
	

	/* **** SOAP FAULT (Protocollo, Porta Applicativa) **** */
	
    /**
     * Indicazione se ritornare un soap fault personalizzato nel codice/actor/faultString per i messaggi di errore di protocollo (Porta Applicativa)
     *   
     * @return Indicazione se ritornare un soap fault personalizzato nel codice/actor/faultString per i messaggi di errore di protocollo (Porta Applicativa)
     * 
     */
	private Boolean isPortaApplicativaBustaErrore_personalizzaElementiFault= null;
	private Boolean isPortaApplicativaBustaErrore_personalizzaElementiFaultRead= null;
    public Boolean isPortaApplicativaBustaErrore_personalizzaElementiFault(){
    	if(this.isPortaApplicativaBustaErrore_personalizzaElementiFaultRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.pa.bustaErrore.personalizzaElementiFault"); 
				
				if (value != null){
					value = value.trim();
					this.isPortaApplicativaBustaErrore_personalizzaElementiFault = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprietà 'org.openspcoop2.protocol.modipa.pa.bustaErrore.personalizzaElementiFault' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails)");
					this.isPortaApplicativaBustaErrore_personalizzaElementiFault = null;
				}
				
				this.isPortaApplicativaBustaErrore_personalizzaElementiFaultRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.pa.bustaErrore.personalizzaElementiFault' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails), errore:"+e.getMessage());
				this.isPortaApplicativaBustaErrore_personalizzaElementiFault = null;
				
				this.isPortaApplicativaBustaErrore_personalizzaElementiFaultRead = true;
			}
    	}
    	
    	return this.isPortaApplicativaBustaErrore_personalizzaElementiFault;
	}
    
    
    /**
     * Indicazione se deve essere aggiunto un errore-applicativo nei details di un messaggio di errore di protocollo (Porta Applicativa)
     *   
     * @return Indicazione se deve essere aggiunto un errore-applicativo nei details di un messaggio di errore di protocollo (Porta Applicativa)
     * 
     */
	private Boolean isPortaApplicativaBustaErrore_aggiungiErroreApplicativo= null;
	private Boolean isPortaApplicativaBustaErrore_aggiungiErroreApplicativoRead= null;
    public Boolean isPortaApplicativaBustaErrore_aggiungiErroreApplicativo(){
    	if(this.isPortaApplicativaBustaErrore_aggiungiErroreApplicativoRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.pa.bustaErrore.aggiungiErroreApplicativo"); 
				
				if (value != null){
					value = value.trim();
					this.isPortaApplicativaBustaErrore_aggiungiErroreApplicativo = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprietà 'org.openspcoop2.protocol.modipa.pa.bustaErrore.aggiungiErroreApplicativo' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails)");
					this.isPortaApplicativaBustaErrore_aggiungiErroreApplicativo = null;
				}
				
				this.isPortaApplicativaBustaErrore_aggiungiErroreApplicativoRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.pa.bustaErrore.aggiungiErroreApplicativo' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails), errore:"+e.getMessage());
				this.isPortaApplicativaBustaErrore_aggiungiErroreApplicativo = null;
				
				this.isPortaApplicativaBustaErrore_aggiungiErroreApplicativoRead = true;
			}
    	}
    	
    	return this.isPortaApplicativaBustaErrore_aggiungiErroreApplicativo;
	}
	
    /**
     * Indicazione se generare i details in caso di SOAPFault *_001 (senza buste Errore)
     *   
     * @return Indicazione se generare i details in caso di SOAPFault *_001 (senza buste Errore)
     * 
     */
	private Boolean isGenerazioneDetailsSOAPFaultProtocolValidazione = null;
    public boolean isGenerazioneDetailsSOAPFaultProtocolValidazione(){
    	if(this.isGenerazioneDetailsSOAPFaultProtocolValidazione==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.eccezioneIntestazione"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultProtocolValidazione = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.eccezioneIntestazione' non impostata, viene utilizzato il default=false");
					this.isGenerazioneDetailsSOAPFaultProtocolValidazione = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.eccezioneIntestazione' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				this.isGenerazioneDetailsSOAPFaultProtocolValidazione = false;
			}
    	}
    	
    	return this.isGenerazioneDetailsSOAPFaultProtocolValidazione;
	}
    
    /**
     * Indicazione se generare i details in caso di SOAPFault *_300
     *   
     * @return Indicazione se generare i details in caso di SOAPFault *_300
     * 
     */
	private Boolean isGenerazioneDetailsSOAPFaultProtocolProcessamento = null;
    public boolean isGenerazioneDetailsSOAPFaultProtocolProcessamento(){
    	if(this.isGenerazioneDetailsSOAPFaultProtocolProcessamento==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.eccezioneProcessamento"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultProtocolProcessamento = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.eccezioneProcessamento' non impostata, viene utilizzato il default=true");
					this.isGenerazioneDetailsSOAPFaultProtocolProcessamento = true;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.eccezioneProcessamento' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				this.isGenerazioneDetailsSOAPFaultProtocolProcessamento = true;
			}
    	}
    	
    	return this.isGenerazioneDetailsSOAPFaultProtocolProcessamento;
	}
    
    
    /**
     * Indicazione se generare nei details in caso di SOAPFault *_300 lo stack trace
     *   
     * @return Indicazione se generare nei details in caso di SOAPFault *_300 lo stack trace
     * 
     */
	private Boolean isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = null;
    public boolean isGenerazioneDetailsSOAPFaultProtocolWithStackTrace(){
    	if(this.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.stackTrace"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.stackTrace' non impostata, viene utilizzato il default=false");
					this.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.stackTrace' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				this.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = false;
			}
    	}
    	
    	return this.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace;
	}
    
    /**
     * Indicazione se generare nei details in caso di SOAPFault informazioni generiche
     *   
     * @return Indicazione se generare nei details in caso di SOAPFault informazioni generiche
     * 
     */
	private Boolean isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = null;
    public boolean isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche(){
    	if(this.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.informazioniGeneriche"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.informazioniGeneriche' non impostata, viene utilizzato il default=true");
					this.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = true;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.informazioniGeneriche' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				this.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = true;
			}
    	}
    	
    	return this.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche;
	}
    
    
    
    /* **** SOAP FAULT (Integrazione, Porta Delegata) **** */
    
    /**
     * Indicazione se generare i details in Casi di errore 5XX
     *   
     * @return Indicazione se generare i details in Casi di errore 5XX
     * 
     */
	private Boolean isGenerazioneDetailsSOAPFaultIntegrationServerError = null;
    public boolean isGenerazioneDetailsSOAPFaultIntegrationServerError(){
    	if(this.isGenerazioneDetailsSOAPFaultIntegrationServerError==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.serverError"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultIntegrationServerError = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.serverError' non impostata, viene utilizzato il default=true");
					this.isGenerazioneDetailsSOAPFaultIntegrationServerError = true;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.serverError' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				this.isGenerazioneDetailsSOAPFaultIntegrationServerError = true;
			}
    	}
    	
    	return this.isGenerazioneDetailsSOAPFaultIntegrationServerError;
	}
    
    /**
     * Indicazione se generare i details in Casi di errore 4XX
     *   
     * @return Indicazione se generare i details in Casi di errore 4XX
     * 
     */
	private Boolean isGenerazioneDetailsSOAPFaultIntegrationClientError = null;
    public boolean isGenerazioneDetailsSOAPFaultIntegrationClientError(){
    	if(this.isGenerazioneDetailsSOAPFaultIntegrationClientError==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.clientError"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultIntegrationClientError = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.clientError' non impostata, viene utilizzato il default=false");
					this.isGenerazioneDetailsSOAPFaultIntegrationClientError = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.clientError' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				this.isGenerazioneDetailsSOAPFaultIntegrationClientError = false;
			}
    	}
    	
    	return this.isGenerazioneDetailsSOAPFaultIntegrationClientError;
	}
    
    /**
     * Indicazione se generare nei details lo stack trace all'interno
     *   
     * @return Indicazione se generare nei details lo stack trace all'interno
     * 
     */
	private Boolean isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = null;
    public boolean isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace(){
    	if(this.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.stackTrace"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.stackTrace' non impostata, viene utilizzato il default=false");
					this.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.stackTrace' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				this.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = false;
			}
    	}
    	
    	return this.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace;
	}
    
    /**
     * Indicazione se generare nei details informazioni dettagliate o solo di carattere generale
     *   
     * @return Indicazione se generare nei details informazioni dettagliate o solo di carattere generale
     * 
     */
	private Boolean isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche= null;
	private Boolean isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGenericheRead= null;
    public Boolean isGenerazioneDetailsSOAPFaultIntegrazionConInformazioniGeneriche(){
    	if(this.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGenericheRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.informazioniGeneriche"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.informazioniGeneriche' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultAsGenericCode)");
					this.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche = null;
				}
				
				this.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGenericheRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.informazioniGeneriche' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultAsGenericCode), errore:"+e.getMessage());
				this.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche = null;
				
				this.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGenericheRead = true;
			}
    	}
    	
    	return this.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche;
	}
    
    
    
    
    /* **** SOAP FAULT (Generati dagli attori esterni) **** */
    
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
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.erroreApplicativo.faultApplicativo.enrichDetails"); 
				
				if (value != null){
					value = value.trim();
					Boolean b = Boolean.parseBoolean(value);
					this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = b ? BooleanNullable.TRUE() : BooleanNullable.FALSE();
				}else{
					this.log.debug("Proprietà 'org.openspcoop2.protocol.modipa.erroreApplicativo.faultApplicativo.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails)");
					this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = BooleanNullable.NULL();
				}
				
				this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.erroreApplicativo.faultApplicativo.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails), errore:"+e.getMessage());
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
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.modipa.erroreApplicativo.faultPdD.enrichDetails"); 
				
				if (value != null){
					value = value.trim();
					Boolean b = Boolean.parseBoolean(value);
					this.isAggiungiDetailErroreApplicativo_SoapFaultPdD = b ? BooleanNullable.TRUE() : BooleanNullable.FALSE();
				}else{
					this.log.debug("Proprietà 'org.openspcoop2.protocol.modipa.erroreApplicativo.faultPdD.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultPdD.enrichDetails)");
					this.isAggiungiDetailErroreApplicativo_SoapFaultPdD = BooleanNullable.NULL();
				}
				
				this.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprietà 'org.openspcoop2.protocol.modipa.erroreApplicativo.faultPdD.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultPdD.enrichDetails), errore:"+e.getMessage());
				this.isAggiungiDetailErroreApplicativo_SoapFaultPdD = BooleanNullable.NULL();
				
				this.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead = true;
			}
    	}
    	
    	return this.isAggiungiDetailErroreApplicativo_SoapFaultPdD;
	}

    
    /* **** Static instance config **** */
    
	private Boolean useConfigStaticInstance = null;
	private Boolean useConfigStaticInstance(){
		if(this.useConfigStaticInstance==null){
			
			Boolean defaultValue = true;
			String propertyName = "org.openspcoop2.protocol.modipa.factory.config.staticInstance";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

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
			String propertyName = "org.openspcoop2.protocol.modipa.factory.erroreApplicativo.staticInstance";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

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
			String propertyName = "org.openspcoop2.protocol.modipa.factory.esito.staticInstance";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

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
