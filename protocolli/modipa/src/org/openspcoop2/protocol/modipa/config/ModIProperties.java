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

package org.openspcoop2.protocol.modipa.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.PDNDResolver;
import org.openspcoop2.pdd.core.keystore.KeystoreException;
import org.openspcoop2.pdd.core.keystore.RemoteStoreConfigPropertiesUtils;
import org.openspcoop2.pdd.core.token.Costanti;
import org.openspcoop2.pdd.core.token.parser.Claims;
import org.openspcoop2.protocol.basic.BasicStaticInstanceConfig;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.modipa.utils.ModISecurityConfig;
import org.openspcoop2.protocol.sdk.ModIPDNDClientConfig;
import org.openspcoop2.protocol.sdk.ModIPDNDOrganizationConfig;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.utils.ModIUtils;
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeystoreParams;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.certificate.remote.RemoteKeyType;
import org.openspcoop2.utils.certificate.remote.RemoteStoreConfig;
import org.openspcoop2.utils.digest.DigestEncoding;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
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

	private static final String PREFIX_PROPRIETA = "Proprietà '";
	private static final String SUFFIX_NON_TROVATA = " non trovata";
	
	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'modipa.properties' */
	private ModIInstanceProperties reader;





	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	private ModIProperties(String confDir,Logger log) throws ProtocolException{

		if(log != null)
			this.log = log;
		else
			this.log = LoggerWrapperFactory.getLogger("ModIProperties");

		/* ---- Lettura del cammino del file di configurazione ---- */

		if(confDir!=null) {
			// nop
		}
		
		Properties propertiesReader = new Properties();
		try (java.io.InputStream properties = ModIProperties.class.getResourceAsStream("/modipa.properties");){  
			if(properties==null){
				throw new ProtocolException("File '/modipa.properties' not found");
			}
			propertiesReader.load(properties);
		}catch(Exception e) {
			this.logError("Riscontrato errore durante la lettura del file 'modipa.properties': "+e.getMessage());
			throw new ProtocolException("ModIProperties initialize error: "+e.getMessage(),e);
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

		if(ModIProperties.modipaProperties==null) {
			// spotbugs warning 'SING_SINGLETON_GETTER_NOT_SYNCHRONIZED': l'istanza viene creata allo startup
			synchronized (ModIProperties.class) {
				throw new ProtocolException("ModIProperties not initialized (use init method in factory)");
			}
		}

		return ModIProperties.modipaProperties;
	}

	private void logDebug(String msg) {
		this.log.debug(msg);
	}
	private void logWarn(String msg) {
		this.log.warn(msg);
	}
	private void logError(String msg, Exception e) {
		this.log.error(msg,e);
	}
	private void logError(String msg) {
		this.log.error(msg);
	}
	private String getPrefixProprieta(String propertyName) {
		return PREFIX_PROPRIETA+propertyName+"'";
	}
	private String getPrefixValoreIndicatoProprieta(String value, String name) {
		return "Valore '"+value+"' indicato nella proprietà '"+name+"'";
	}
	private String getSuffixSuperioreMassimoConsentitoControlloDisabilitato(long maxLongValue) {
		return " superiore al massimo consentito '"+maxLongValue+"'; il controllo viene disabilitato";
	}
	private String getMessaggioVerificaDisabilitata(String name) {
		return "Verifica gestita tramite la proprietà '"+name+"' disabilitata.";
	}
	private String getMessaggioErroreProprietaNonImpostata(String propertyName, Boolean defaultValue) {
		return getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue.toString());
	}
	private String getMessaggioErroreProprietaNonImpostata(String propertyName, Integer defaultValue) {
		return getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue.toString());
	}
	private String getMessaggioErroreProprietaNonImpostata(String propertyName, String defaultValue) {
		return getPrefixProprieta(propertyName)+" non impostata, viene utilizzato il default="+defaultValue;
	}
	private String getMessaggioErroreProprietaNonImpostata(String pName, Exception e) {
		return getPrefixProprieta(pName)+" non impostata, errore:"+e.getMessage();
	}
	private String getMessaggioErroreProprietaNonCorretta(String pName, Exception e) {
		return getPrefixProprieta(pName)+" non corretta, errore:"+e.getMessage();
	}
		
	private String getSuffixErrore(Exception e) {
		return ", errore:"+e.getMessage();
	}

	private ProtocolException newProtocolExceptionPropertyNonDefinita() {
		return new ProtocolException("non definita");
	}
	
	private static final String INVALID_VALUE = "Invalid value";
	

	public void validaConfigurazione(Loader loader) throws ProtocolException  {	
		try{  

			if(loader!=null) {
				// nop
			}
			
			generateIDasUUID();
			
			/* **** TRUST STORE **** */
			
			String trustStoreType = getSicurezzaMessaggioCertificatiTrustStoreTipo();
			if(trustStoreType!=null) {
				if(!HSMUtils.isKeystoreHSM(trustStoreType)) {
					getSicurezzaMessaggioCertificatiTrustStorePath();
					getSicurezzaMessaggioCertificatiTrustStorePassword();
				}
				getSicurezzaMessaggioCertificatiTrustStoreCrls();
				getSicurezzaMessaggioCertificatiTrustStoreOcspPolicy();
			}
			
			String sslTrustStoreType = getSicurezzaMessaggioSslTrustStoreTipo();
			if(sslTrustStoreType!=null) {
				if(!HSMUtils.isKeystoreHSM(sslTrustStoreType)) {
					getSicurezzaMessaggioSslTrustStorePath();
					getSicurezzaMessaggioSslTrustStorePassword();
				}
				getSicurezzaMessaggioSslTrustStoreCrls();
				getSicurezzaMessaggioSslTrustStoreOcspPolicy();
			}
			
			List<RemoteStoreConfig> rsc = getRemoteStoreConfig();
			if(rsc!=null && !rsc.isEmpty()) {
				for (RemoteStoreConfig remoteStoreConfigCheck : rsc) {
					readAPIPDNDVersionKeys(remoteStoreConfigCheck.getStoreName());
					readAPIPDNDVersionEvents(remoteStoreConfigCheck.getStoreName());
					readAPIPDNDVersionClients(remoteStoreConfigCheck.getStoreName());
					readAPIPDNDVersionOrganizations(remoteStoreConfigCheck.getStoreName());
				}
			}
			
			getValidazioneTokenOAuthClaimsRequired();
			getValidazioneTokenPDNDClaimsRequired();
			this.isValidazioneTokenPDNDProducerIdCheck();
			this.isPdndProducerIdCheckUnique();
			this.isValidazioneTokenPDNDEServiceIdCheck();
			this.isPdndEServiceIdCheckUnique();
			this.isValidazioneTokenPDNDDescriptorIdCheck();
			this.isPdndDescriptorIdCheckUnique();
			
			this.getApiPDNDBaseUrlVersionPattern();
			
			// version
			int [] apiPdndVersion = new int[] {1 , 2};
			for (int v : apiPdndVersion) {
				getApiPDNDClientsVersionPatttern(v);
				getApiPDNDOrganizationsVersionPatttern(v);
				// keys
				getApiPDNDClientKeysPath(v);
				getApiPDNDClientKeysJsonPath(v);
				getApiPDNDServerKeysPath(v);
				getApiPDNDServerKeysJsonPath(v);
				isApiPDNDServerKeysFaultClientCheck(v);
				// events
				getApiPDNDEventKeysPath(v);
				getApiPDNDEventKeysParameterLastEventId(v);
				getApiPDNDEventKeysParameterLimit(v);
				// clients
				getApiPDNDClientsPath(v);
				getApiPDNDClientsIdJsonPath(v);
				getApiPDNDClientsOrganizationJsonPath(v);
				getApiPDNDClientsNameJsonPath(v);
				getApiPDNDClientsDescriptionJsonPath(v);
				// organizations
				getApiPDNDOrganizationsPath(v);
				getApiPDNDOrganizationsIdJsonPath(v);
				getApiPDNDOrganizationsNameJsonPath(v);
				getApiPDNDOrganizationsExternalOriginJsonPath(v);
				getApiPDNDOrganizationsExternalIdJsonPath(v);
				getApiPDNDOrganizationsCategoryJsonPath(v);
				getApiPDNDOrganizationsSubunitJsonPath(v);
			}
			
			/* **** KEY STORE **** */
			
			String keystoreType = getSicurezzaMessaggioCertificatiKeyStoreTipo();
			if(keystoreType!=null) {
				if(!HSMUtils.isKeystoreHSM(keystoreType)) {
					getSicurezzaMessaggioCertificatiKeyStorePath();
					getSicurezzaMessaggioCertificatiKeyStorePassword();
				}
				getSicurezzaMessaggioCertificatiKeyAlias();
				if(!HSMUtils.isKeystoreHSM(keystoreType) || HSMUtils.isHsmConfigurableKeyPassword()) {
					getSicurezzaMessaggioCertificatiKeyPassword();
				}
				getSicurezzaMessaggioCertificatiKeyClientId();
				getSicurezzaMessaggioCertificatiKeyKid();
			}
			
			/* **** CORNICE SICUREZZA **** */
			
			if(isSicurezzaMessaggioCorniceSicurezzaEnabled()!=null && isSicurezzaMessaggioCorniceSicurezzaEnabled().booleanValue()) {
				getSicurezzaMessaggioCorniceSicurezzaRestCodiceEnte();
				getSicurezzaMessaggioCorniceSicurezzaRestUser();
				getSicurezzaMessaggioCorniceSicurezzaRestIpuser();	
				getSicurezzaMessaggioCorniceSicurezzaSoapCodiceEnte();
				getSicurezzaMessaggioCorniceSicurezzaSoapUser();
				getSicurezzaMessaggioCorniceSicurezzaSoapIpuser();	
				getSicurezzaMessaggioCorniceSicurezzaDynamicCodiceEnte();
				getSicurezzaMessaggioCorniceSicurezzaDynamicUser();
				getSicurezzaMessaggioCorniceSicurezzaDynamicIpuser();	
			}
			
			/* **** AUDIT **** */
			
			getAuditConfig();
			getSecurityTokenHeaderModIAudit();
			isSecurityTokenAuditX509AddKid();
			isSecurityTokenAuditApiSoapX509RiferimentoX5c();
			isSecurityTokenAuditApiSoapX509RiferimentoX5cSingleCertificate();
			isSecurityTokenAuditApiSoapX509RiferimentoX5u();
			isSecurityTokenAuditApiSoapX509RiferimentoX5t();
			isSecurityTokenAuditProcessArrayModeEnabled();
			isSecurityTokenAuditAddPurposeId();
			isSecurityTokenAuditExpectedPurposeId();
			isSecurityTokenAuditCompareAuthorizationPurposeId();
			getSecurityTokenAuditDnonceSize();
			getSecurityTokenAuditDigestAlgorithm();
					
			/* **** CACHE **** */
			
			this.isTokenAuthCacheable();
			this.isTokenAuditCacheable();
			this.getGestioneRetrieveTokenRefreshTokenBeforeExpirePercent();
			this.getGestioneRetrieveTokenRefreshTokenBeforeExpireSeconds();
			
			/* **** TRACCE **** */ 
			
			this.isGenerazioneTracce();
			this.isGenerazioneTracceRegistraToken();
			this.isGenerazioneTracceRegistraCustomClaims();
			this.getGenerazioneTracceRegistraCustomClaimsBlackList();
			
			/* **** Versionamento **** */ 
			
			this.isModIVersioneBozza();
			
			/* **** REST **** */ 
			
			getRestSecurityTokenHeaderModI();
			isSecurityTokenX509AddKid();
			isSecurityTokenIntegrity01AddPurposeId();
			isSecurityTokenIntegrity02AddPurposeId();
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
			getRestSecurityTokenClaimsIatTimeCheckMilliseconds();
			getRestSecurityTokenClaimsIatTimeCheckFutureToleranceMilliseconds();
			isRestSecurityTokenClaimsExpTimeCheck();
			getRestSecurityTokenClaimsExpTimeCheckToleranceMilliseconds();
			getRestSecurityTokenClaimsNbfTimeCheckToleranceMilliseconds();
			getRestSecurityTokenDigestDefaultEncoding();
			isRestSecurityTokenDigestEncodingChoice();
			getRestSecurityTokenDigestEncodingAccepted();
			isRestSecurityTokenRequestDigestClean();
			isRestSecurityTokenResponseDigestClean();
			isRestSecurityTokenResponseDigestHEADuseServerHeader();
			isRestSecurityTokenFaultProcessEnabled();
			isRestSecurityTokenAudienceProcessArrayModeEnabled();
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
			getSoapSecurityTokenTimestampCreatedTimeCheckMilliseconds();
			getSoapSecurityTokenTimestampCreatedTimeCheckFutureToleranceMilliseconds();
			isSoapSecurityTokenTimestampExpiresTimeCheck();
			getSoapSecurityTokenTimestampExpiresTimeCheckToleranceMilliseconds();
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
			isRiferimentoIDRichiestaPortaDelegataRequired();
			isRiferimentoIDRichiestaPortaApplicativaRequired();
			isTokenOAuthUseJtiIntegrityAsMessageId();
			
			/* **** SOAP FAULT (Generati dagli attori esterni) **** */
			
			this.isAggiungiDetailErroreApplicativoSoapFaultApplicativo();
			this.isAggiungiDetailErroreApplicativoSoapFaultPdD();
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
			
			this.isPortaApplicativaBustaErrorePersonalizzaElementiFault();
			this.isPortaApplicativaBustaErroreAggiungiErroreApplicativo();
			
			/* **** Static instance config **** */
			
			this.useConfigStaticInstance();
			this.useErroreApplicativoStaticInstance();
			this.useEsitoStaticInstance();
			this.getStaticInstanceConfig();
			
			/* **** Signal Hub **** */
			if(isSignalHubEnabled()) {
				this.getSignalHubAlgorithms();
				this.getSignalHubDefaultAlgorithm();
				this.getSignalHubSeedSize();
				this.getSignalHubDefaultSeedSize();
				this.isSignalHubSeedLifetimeUnlimited();
				this.getSignalHubDeSeedSeedLifetimeDaysDefault();
				this.getSignalHubSoapNamespace();
				this.getSignalHubApiName();
				this.getSignalHubApiVersion();
				this.getSignalHubConfig();
				this.getSignalHubSeedSize();
				this.getSignalHubDigestHistroy();
			}
			

			this.getStaticInstanceConfig();
			
			/* **** TracingPDND **** */
			isTracingPDNDEnabled();

		}catch(java.lang.Exception e) {
			String msg = "Riscontrato errore durante la validazione della proprieta' del protocollo modipa, "+e.getMessage();
			this.logError(msg,e);
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
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.generateIDasUUID = Boolean.parseBoolean(value);
				}else{
					this.logWarn(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.generateIDasUUID = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logWarn(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
				this.generateIDasUUID = defaultValue;
			}
		}

		return this.generateIDasUUID;
	}
	
	
	
	
	
	
	
	/* **** TRUST STORE **** */
		
	// riferito in org.openspcoop2.protocol.utils.ModIUtils
	// non modificare il nome
	public KeystoreParams getSicurezzaMessaggioCertificatiTrustStore() throws ProtocolException {
		KeystoreParams params = null;
		String trustStoreType = getSicurezzaMessaggioCertificatiTrustStoreTipo();
		if(trustStoreType!=null) {
			params = new KeystoreParams();
			params.setType(trustStoreType);
			params.setPath(getSicurezzaMessaggioCertificatiTrustStorePath());
			params.setPassword(getSicurezzaMessaggioCertificatiTrustStorePassword());
			params.setCrls(getSicurezzaMessaggioCertificatiTrustStoreCrls());
			params.setOcspPolicy(getSicurezzaMessaggioCertificatiTrustStoreOcspPolicy());
		}
		return params;
	}
	
	private String sicurezzaMessaggioCertificatiTrustStoreTipo= null;
	private Boolean sicurezzaMessaggioCertificatiTrustStoreTipoReaded= null;
	public String getSicurezzaMessaggioCertificatiTrustStoreTipo() {
    	if(this.sicurezzaMessaggioCertificatiTrustStoreTipoReaded==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.trustStore.tipo"); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggioCertificatiTrustStoreTipo = value;
				}
				
				this.sicurezzaMessaggioCertificatiTrustStoreTipoReaded = true;
				
			}catch(java.lang.Exception e) {
				this.logError("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.trustStore.tipo' non impostata, errore:"+e.getMessage());
				this.sicurezzaMessaggioCertificatiTrustStoreTipoReaded = true;
			}
    	}
    	
    	return this.sicurezzaMessaggioCertificatiTrustStoreTipo;
	}	
	
	private String sicurezzaMessaggioCertificatiTrustStorePath= null;
	public String getSicurezzaMessaggioCertificatiTrustStorePath() throws ProtocolException{
    	if(this.sicurezzaMessaggioCertificatiTrustStorePath==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.trustStore.path"); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggioCertificatiTrustStorePath = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				this.logError("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.trustStore.path' non impostata, errore:"+e.getMessage());
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.sicurezzaMessaggioCertificatiTrustStorePath;
	}
	
	private String sicurezzaMessaggioCertificatiTrustStorePassword= null;
	public String getSicurezzaMessaggioCertificatiTrustStorePassword() throws ProtocolException{
    	if(this.sicurezzaMessaggioCertificatiTrustStorePassword==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.trustStore.password"); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggioCertificatiTrustStorePassword = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				this.logError("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.trustStore.password' non impostata, errore:"+e.getMessage());
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.sicurezzaMessaggioCertificatiTrustStorePassword;
	}	
	
	private Boolean sicurezzaMessaggioCertificatiTrustStoreCrlsReaded= null;
	private String sicurezzaMessaggioCertificatiTrustStoreCrls= null;
	public String getSicurezzaMessaggioCertificatiTrustStoreCrls() throws ProtocolException{
    	if(this.sicurezzaMessaggioCertificatiTrustStoreCrlsReaded==null){
    		String pName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.trustStore.crls";
	    	try{  
				String value = this.reader.getValueConvertEnvProperties(pName); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggioCertificatiTrustStoreCrls = value;
				}
				
				this.sicurezzaMessaggioCertificatiTrustStoreCrlsReaded = true;
				
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.sicurezzaMessaggioCertificatiTrustStoreCrls;
	}	
	
	private Boolean sicurezzaMessaggioCertificatiTrustStoreOcspPolicyReaded= null;
	private String sicurezzaMessaggioCertificatiTrustStoreOcspPolicy= null;
	public String getSicurezzaMessaggioCertificatiTrustStoreOcspPolicy() throws ProtocolException{
    	if(this.sicurezzaMessaggioCertificatiTrustStoreOcspPolicyReaded==null){
    		String pName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.trustStore.ocspPolicy";
	    	try{  
				String value = this.reader.getValueConvertEnvProperties(pName); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggioCertificatiTrustStoreOcspPolicy = value;
				}
				
				this.sicurezzaMessaggioCertificatiTrustStoreOcspPolicyReaded = true;
				
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.sicurezzaMessaggioCertificatiTrustStoreOcspPolicy;
	}	
	
	
	
	// riferito in org.openspcoop2.protocol.utils.ModIUtils
	// non modificare il nome
	public KeystoreParams getSicurezzaMessaggioSslTrustStore() throws ProtocolException {
		KeystoreParams params = null;
		String sslTrustStoreType = getSicurezzaMessaggioSslTrustStoreTipo();
		if(sslTrustStoreType!=null) {
			params = new KeystoreParams();
			params.setType(sslTrustStoreType);
			params.setPath(getSicurezzaMessaggioSslTrustStorePath());
			params.setPassword(getSicurezzaMessaggioSslTrustStorePassword());
			params.setCrls(getSicurezzaMessaggioSslTrustStoreCrls());
			params.setOcspPolicy(getSicurezzaMessaggioSslTrustStoreOcspPolicy());
		}
		return params;
	}
		
	private String sicurezzaMessaggioSslTrustStoreTipo= null;
	private Boolean sicurezzaMessaggioSslTrustStoreTipoReaded= null;
	public String getSicurezzaMessaggioSslTrustStoreTipo() {
    	if(this.sicurezzaMessaggioSslTrustStoreTipoReaded==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.ssl.trustStore.tipo"); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggioSslTrustStoreTipo = value;
				}
				
				this.sicurezzaMessaggioSslTrustStoreTipoReaded = true;
				
			}catch(java.lang.Exception e) {
				this.logError("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.ssl.trustStore.tipo' non impostata, errore:"+e.getMessage());
				this.sicurezzaMessaggioSslTrustStoreTipoReaded = true;
			}
    	}
    	
    	return this.sicurezzaMessaggioSslTrustStoreTipo;
	}	
	
	private String sicurezzaMessaggioSslTrustStorePath= null;
	public String getSicurezzaMessaggioSslTrustStorePath() throws ProtocolException{
    	if(this.sicurezzaMessaggioSslTrustStorePath==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.ssl.trustStore.path"); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggioSslTrustStorePath = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				this.logError("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.ssl.trustStore.path' non impostata, errore:"+e.getMessage());
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.sicurezzaMessaggioSslTrustStorePath;
	}
	
	private String sicurezzaMessaggioSslTrustStorePassword= null;
	public String getSicurezzaMessaggioSslTrustStorePassword() throws ProtocolException{
    	if(this.sicurezzaMessaggioSslTrustStorePassword==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.ssl.trustStore.password"); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggioSslTrustStorePassword = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				this.logError("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.ssl.trustStore.password' non impostata, errore:"+e.getMessage());
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.sicurezzaMessaggioSslTrustStorePassword;
	}	
	
	private Boolean sicurezzaMessaggioSslTrustStoreCrlsReaded= null;
	private String sicurezzaMessaggioSslTrustStoreCrls= null;
	public String getSicurezzaMessaggioSslTrustStoreCrls() throws ProtocolException{
		if(this.sicurezzaMessaggioSslTrustStoreCrlsReaded==null){
			String pName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.ssl.trustStore.crls";
	    	try{  
				String value = this.reader.getValueConvertEnvProperties(pName); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggioSslTrustStoreCrls = value;
				}
				
				this.sicurezzaMessaggioSslTrustStoreCrlsReaded = true;
				
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.sicurezzaMessaggioSslTrustStoreCrls;
	}
	
	
	private Boolean sicurezzaMessaggioSslTrustStoreOcspPolicyReaded= null;
	private String sicurezzaMessaggioSslTrustStoreOcspPolicy= null;
	public String getSicurezzaMessaggioSslTrustStoreOcspPolicy() throws ProtocolException{
    	if(this.sicurezzaMessaggioSslTrustStoreOcspPolicyReaded==null){
    		String pName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.ssl.trustStore.ocspPolicy";
        	try{  
				String value = this.reader.getValueConvertEnvProperties(pName); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggioSslTrustStoreOcspPolicy = value;
				}
				
				this.sicurezzaMessaggioSslTrustStoreOcspPolicyReaded = true;
				
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.sicurezzaMessaggioSslTrustStoreOcspPolicy;
	}
	
	
	
	
	
	
	
	
	/* **** REMOTE TRUST STORE **** */
	
	private static final String REMOTE_STORE_PREFIX_PROPERTY = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.remoteStore.";
	
	private Map<String, Integer> apiPDNDVersion = new HashMap<>();
	public int readAPIPDNDVersionKeys(String remoteStore) throws ProtocolException, UtilsException {
		return readAPIPDNDVersionEngine( remoteStore, "api.keys.version");
	}
	public int readAPIPDNDVersionEvents(String remoteStore) throws ProtocolException, UtilsException {
		return readAPIPDNDVersionEngine(remoteStore, "api.events.version");
	}
	public int readAPIPDNDVersionClients(String remoteStore) throws ProtocolException, UtilsException {
		return readAPIPDNDVersionEngine(remoteStore, "api.clients.version");
	}
	public int readAPIPDNDVersionOrganizations(String remoteStore) throws ProtocolException, UtilsException {
		return readAPIPDNDVersionEngine(remoteStore, "api.organizations.version");
	}
	private int readAPIPDNDVersionEngine(String remoteStore, String pName) throws ProtocolException, UtilsException {
		String key = remoteStore+"_"+pName;
		if(!this.apiPDNDVersion.containsKey(key)) {
			String p = REMOTE_STORE_PREFIX_PROPERTY+remoteStore+"."+pName;
			String pValue = this.reader.getValueConvertEnvProperties(p);
			if(pValue!=null) {
				try {
					int i = Integer.parseInt(pValue);
					if(i>0) {
						this.apiPDNDVersion.put(key, i);
					}
				}catch(Exception e) {
					throw new ProtocolException("Property '"+p+"' non valida: "+e.getMessage(),e);
				}
			}
			if(!this.apiPDNDVersion.containsKey(key)) {
				this.apiPDNDVersion.put(key, -1);
			}
		}
		return this.apiPDNDVersion.get(key);
	}
	
	
	private List<RemoteStoreConfig> remoteStoreConfig = null;
	private Map<String,RemoteKeyType> remoteStoreKeyTypeMap = null;
	private Map<String,RemoteKeyType> getRemoteStoreKeyTypeMap() throws ProtocolException{
		if(this.remoteStoreKeyTypeMap==null){
			getRemoteStoreConfig();
		}
		return this.remoteStoreKeyTypeMap;
	}
	// riferito in org.openspcoop2.protocol.utils.ModIUtils
	// non modificare il nome
	public List<RemoteStoreConfig> getRemoteStoreConfig() throws ProtocolException{
    	if(this.remoteStoreConfig==null){
    		String pName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.remoteStores";
        	try{  
				String value = this.reader.getValueConvertEnvProperties(pName); 
				
				if (value != null){
					value = value.trim();
					
					this.remoteStoreConfig= new ArrayList<>();
					this.remoteStoreKeyTypeMap=new HashMap<>();
					
					readRemoteStores(value);
					
				}

			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonCorretta(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.remoteStoreConfig;
	}
	private void readRemoteStores(String value) throws UtilsException, ProtocolException, KeystoreException {
		String [] tmp = value.split(",");
		if(tmp!=null && tmp.length>0) {
			for (String rsc : tmp) {
				rsc = rsc.trim();
				
				String debugPrefix = "Configurazione per remoteStore '"+rsc+"'";
				
				String propertyPrefix = REMOTE_STORE_PREFIX_PROPERTY+rsc+".";
				Properties p = this.reader.readPropertiesConvertEnvProperties(propertyPrefix);
				if(p==null || p.isEmpty()) {
					throw new ProtocolException(debugPrefix+SUFFIX_NON_TROVATA);
				}
				try {
					
					RemoteStoreConfig config = RemoteStoreConfigPropertiesUtils.read(p, null);
					
					setUrlPDND(rsc, config);
					this.remoteStoreConfig.add(config);
					
					readKeyType(p, debugPrefix, config);
					
				}catch(Exception e) {
					throw new ProtocolException("["+propertyPrefix+"] "+e.getMessage(),e);
				}
			}
		}
	}
	
	// rimuove l'eventuale suffisso /keys
	private static final String URL_CHAR_DELIMITER = "/"; 
	public String buildBaseUrlPDND(String baseUrl) throws ProtocolException {
		
		int apiVersion = extractVersionFromBaseUrl(baseUrl);
		
		// elimino path keys dalla url
		String pathKeys = getApiPDNDClientKeysPath(apiVersion);
		if(!pathKeys.startsWith(URL_CHAR_DELIMITER)) {
			pathKeys = URL_CHAR_DELIMITER + pathKeys;
		}
		if(baseUrl.endsWith(pathKeys)) {
			baseUrl = baseUrl.substring(0,baseUrl.length()-pathKeys.length());
		}
		else {
			if(pathKeys.endsWith(URL_CHAR_DELIMITER)) {
				// provo senza
				pathKeys = pathKeys.substring(0, (pathKeys.length()-1));
			}
			else {
				// provo con
				pathKeys = pathKeys + URL_CHAR_DELIMITER;
			}
			if(baseUrl.endsWith(pathKeys)) {
				baseUrl = baseUrl.substring(0,baseUrl.length()-pathKeys.length());
			}
		}
			
		return baseUrl;
	}
	
	public int extractVersionFromBaseUrl(String baseUrl) {
		String pattern = null;
		try {
			pattern = this.getApiPDNDBaseUrlVersionPattern();
			String s = RegularExpressionEngine.getStringMatchPattern(baseUrl, pattern);
			int i = Integer.parseInt(s);
			if(i>0) {
				return i;
			}
		}catch(Exception e) {
			this.log.error("extractVersionFromBaseUrl("+baseUrl+") with pattern '"+pattern+"' failed: "+e.getMessage(),e);
		}
		return 1; // default
	}

	private void setUrlPDND(String rsc, RemoteStoreConfig config) throws ProtocolException, UtilsException {
		
		// Base Url Orig
		String baseUrlOrig = config.getBaseUrl();
		Map<String, String> baseUrlMultitenantOrig = new HashMap<>();
		if(config.getMultiTenantBaseUrl()!=null && !config.getMultiTenantBaseUrl().isEmpty()) {
			baseUrlMultitenantOrig.putAll(config.getMultiTenantBaseUrl());
		}
		int versionInBaseUrl = extractVersionFromBaseUrl(baseUrlOrig); 
		
		// keys
		// imposta la baseurl direttamente in config e anche il baseUrlFaultCheck per i server keys
		// dopo questa chiamata config.getBaseUrl() e config.getMultiTenantBaseUrl() sarà aggiornata
		// es. una versione differente solo per le keys
		forceBaseUrlPDNDEndsWithKeys(rsc, config, baseUrlOrig, versionInBaseUrl);
		
		// BaseUrl senza suffisso keys
		String baseUrlWithoutKeys = this.buildBaseUrlPDND(baseUrlOrig);
		Map<String, String> baseUrlMultitenantWithoutKeys = new HashMap<>();
		if(!baseUrlMultitenantOrig.isEmpty()) {
			for (Map.Entry<String,String> entry : baseUrlMultitenantOrig.entrySet()) {
				baseUrlMultitenantWithoutKeys.put(entry.getKey(), this.buildBaseUrlPDND(entry.getValue()));
			}
		}
		
		// events
		buildUrlCheckEventi(rsc, config, versionInBaseUrl, baseUrlWithoutKeys, baseUrlMultitenantWithoutKeys);
		
		// clients
		buildUrlCheckClients(rsc, config, versionInBaseUrl, baseUrlWithoutKeys, baseUrlMultitenantWithoutKeys);
		
		// organization
		buildUrlCheckOrganizations(rsc, config, versionInBaseUrl, baseUrlWithoutKeys, baseUrlMultitenantWithoutKeys);
		
		/**System.out.println("==============================");
		System.out.println("RC ["+config.getStoreName()+"]");
		System.out.println("RC ["+config.getBaseUrl()+"]");
		System.out.println("RC ["+config.getMultiTenantBaseUrl()+"]");
		System.out.println("RC ["+config.getMetadati()+"]");
		System.out.println("RC ["+config.getMultiTenantMetadati()+"]");*/
	}
	
	private void forceBaseUrlPDNDEndsWithKeys(String rsc, RemoteStoreConfig config, String baseUrl, int versionInBaseUrl) throws ProtocolException, UtilsException {
		
		if(isForceBaseUrlPDNDEndsWithKeys(rsc)) {
			config.setBaseUrl(normalizeBaseUrlApiPDNDKeys(baseUrl, versionInBaseUrl));
			
			if(config.getMultiTenantBaseUrl()!=null && !config.getMultiTenantBaseUrl().isEmpty()) {
				Map<String, String> multiTenantBaseUrlNormalized = config.getMultiTenantBaseUrl();
				if(multiTenantBaseUrlNormalized==null) {
					multiTenantBaseUrlNormalized = new HashMap<>();
				}
				for (Map.Entry<String,String> entry : config.getMultiTenantBaseUrl().entrySet()) {
					String baseUrlTenant = entry.getValue();
					multiTenantBaseUrlNormalized.put(entry.getKey(), normalizeBaseUrlApiPDNDKeys(baseUrlTenant, versionInBaseUrl));
				}
				config.setMultiTenantBaseUrl(multiTenantBaseUrlNormalized);
			}
		}
		
		int apiPdndVersionOverride = this.readAPIPDNDVersionKeys(rsc); // consente di sovrascrivere il default
		upgradeBaseUrlApiPDNDKeys(config, versionInBaseUrl, apiPdndVersionOverride);
				
	}
	private String normalizeBaseUrlApiPDNDKeys(String baseUrl, int versionInBaseUrl) throws ProtocolException {
		
		String suffix = null;
		try{
			suffix = this.getApiPDNDClientKeysPath(versionInBaseUrl);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		String pathWithoutSlash = null;
		if(!suffix.startsWith("/")){
			pathWithoutSlash = suffix;
			suffix = "/" + suffix;
		}
		else {
			pathWithoutSlash = suffix.substring(1);
		}
		
		if(!baseUrl.endsWith(suffix)) {
			if(!baseUrl.endsWith("/")) {
				baseUrl+="/";
			}
			baseUrl+=pathWithoutSlash;
		}
		return baseUrl;
	}
	private void upgradeBaseUrlApiPDNDKeys(RemoteStoreConfig config, int versionInBaseUrl, int apiPdndVersionOverride) throws ProtocolException {
		
		int apiVersion = (apiPdndVersionOverride>0 && apiPdndVersionOverride!=versionInBaseUrl) ? apiPdndVersionOverride : versionInBaseUrl;
		boolean faultCheck = isApiPDNDServerKeysFaultClientCheck(apiVersion);
		
		if(apiPdndVersionOverride>0 && apiPdndVersionOverride!=versionInBaseUrl) {
			config.setBaseUrl(upgradeBaseUrlApiPDNDKeys(config.getBaseUrl(), versionInBaseUrl, apiPdndVersionOverride));
		}
		if(faultCheck) {
			config.setBaseUrlFaultCheck(upgradeBaseUrlApiPDNDKeysFaultCheck(config.getBaseUrl(), apiVersion));
		}
		config.setResponseJsonPath(getApiPDNDClientKeysJsonPath(apiVersion));
		config.setResponseJsonPathFaultCheck(getApiPDNDServerKeysJsonPath(apiVersion));
		
		upgradeBaseUrlApiPDNDKeysMultitenant(config, versionInBaseUrl, apiPdndVersionOverride, faultCheck, apiVersion);
	}
	private void upgradeBaseUrlApiPDNDKeysMultitenant(RemoteStoreConfig config, int versionInBaseUrl, int apiPdndVersionOverride, boolean faultCheck, int apiVersion) throws ProtocolException {
		if(config.getMultiTenantBaseUrl()!=null && !config.getMultiTenantBaseUrl().isEmpty()) {
			Map<String, String> multiTenantBaseUrlUpgraded = config.getMultiTenantBaseUrl();
			if(multiTenantBaseUrlUpgraded==null) {
				multiTenantBaseUrlUpgraded = new HashMap<>();
			}
			Map<String, String> multiTenantBaseUrlFaultCheckUpgraded = new HashMap<>();
			for (Map.Entry<String,String> entry : config.getMultiTenantBaseUrl().entrySet()) {
				String baseUrlTenant = entry.getValue();
				if(apiPdndVersionOverride>0 && apiPdndVersionOverride!=versionInBaseUrl) {
					multiTenantBaseUrlUpgraded.put(entry.getKey(), upgradeBaseUrlApiPDNDKeys(baseUrlTenant, versionInBaseUrl, apiPdndVersionOverride));
				}
				if(faultCheck) {
					multiTenantBaseUrlFaultCheckUpgraded.put(entry.getKey(), upgradeBaseUrlApiPDNDKeysFaultCheck(config.getBaseUrl(), apiVersion));
				}
			}
			config.setMultiTenantBaseUrl(multiTenantBaseUrlUpgraded);
			config.setMultiTenantBaseUrlFaultCheck(multiTenantBaseUrlFaultCheckUpgraded);
		}
	}
	private String upgradeBaseUrlApiPDNDKeys(String orig, int versionInBaseUrl, int apiPdndVersionOverride) throws ProtocolException {
		String suffixInBaseUrl = buildSuffixClientKeysPathByVersionl(versionInBaseUrl);
		String newSuffixInBaseUrl = buildSuffixClientKeysPathByVersionl(apiPdndVersionOverride);
		return orig.replace(suffixInBaseUrl, newSuffixInBaseUrl);
	}
	private String buildSuffixClientKeysPathByVersionl(int version) throws ProtocolException {
		String suffixUrl = null;
		try{
			suffixUrl = this.getApiPDNDClientKeysPath(version);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		if(!suffixUrl.startsWith("/")){
			suffixUrl = "/" + suffixUrl;
		}
		return "/v" +version+suffixUrl;
	}
	private boolean isForceBaseUrlPDNDEndsWithKeys(String rsc) {
		String propertyPrefix = REMOTE_STORE_PREFIX_PROPERTY+rsc+"."+
				RemoteStoreConfigPropertiesUtils.PROPERTY_STORE_URL+".forceEndsWithKeys";
		try{  
			boolean force = true;
			String value = this.reader.getValueConvertEnvProperties(propertyPrefix); 
			if (value != null){
				value = value.trim();
				force = "false".equals(value);
			}
			return force;			
		}catch(java.lang.Exception e) {
			this.logWarn(PREFIX_PROPRIETA+propertyPrefix+"' non impostata; viene forzato il suffisso /keys");
			return true;
		}
	}
	private String upgradeBaseUrlApiPDNDKeysFaultCheck(String original, int version) throws ProtocolException {
		String suffixClientUrl = null;
		try{
			suffixClientUrl = this.getApiPDNDClientKeysPath(version);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		if(!suffixClientUrl.startsWith("/")){
			suffixClientUrl = "/" + suffixClientUrl;
		}
		
		String suffixServerUrl = null;
		try{
			suffixServerUrl = this.getApiPDNDServerKeysPath(version);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		if(!suffixServerUrl.startsWith("/")){
			suffixServerUrl = "/" + suffixServerUrl;
		}
		
		if (original.endsWith(suffixClientUrl)) {
            return original.substring(0, original.length() - suffixClientUrl.length()) + suffixServerUrl;
        }
        return original; // se non termina con suffixClientUrl, ritorna invariata
	}

	private String concatenateUrl(String baseUrlWithoutKeys, String path) {
		if(baseUrlWithoutKeys.endsWith(URL_CHAR_DELIMITER)) {
			if(path.startsWith(URL_CHAR_DELIMITER)) {
				return baseUrlWithoutKeys + path.substring(1);
			}
			else {
				return baseUrlWithoutKeys + path;
			}
		}
		else {
			if(path.startsWith(URL_CHAR_DELIMITER)) {
				return baseUrlWithoutKeys + path;
			}
			else {
				return baseUrlWithoutKeys + URL_CHAR_DELIMITER + path;
			}
		}
	}
	private void buildUrlCheckEventi(String rsc,RemoteStoreConfig remoteStore, int versionInBaseUrl, String baseUrlWithoutKeys, Map<String, String> baseUrlMultitenantWithoutKeys) throws ProtocolException, UtilsException {

		int apiPdndVersionOverride = this.readAPIPDNDVersionEvents(rsc); // consente di sovrascrivere il default
		int apiVersion = (apiPdndVersionOverride>0 && apiPdndVersionOverride!=versionInBaseUrl) ? apiPdndVersionOverride : versionInBaseUrl;
			
		String pathEventKeys = this.getApiPDNDEventKeysPath(apiVersion);
		if(!pathEventKeys.startsWith(URL_CHAR_DELIMITER)) {
			pathEventKeys = URL_CHAR_DELIMITER + pathEventKeys;
		}
		String urlEventi = concatenateUrl(baseUrlWithoutKeys, pathEventKeys);
		if(apiPdndVersionOverride>0 && apiPdndVersionOverride!=versionInBaseUrl) {
			urlEventi = upgradeBaseUrlApiPDND(urlEventi, versionInBaseUrl, apiPdndVersionOverride, pathEventKeys);
		}
		remoteStore.getMetadati().put(ModIUtils.API_PDND_EVENTS_KEYS_PATH, urlEventi);
		remoteStore.getMetadati().put(ModIUtils.API_PDND_EVENTS_KEYS_PARAMETER_LASTEVENTID, this.getApiPDNDEventKeysParameterLastEventId(apiVersion));
		remoteStore.getMetadati().put(ModIUtils.API_PDND_EVENTS_KEYS_PARAMETER_LIMIT, this.getApiPDNDEventKeysParameterLimit(apiVersion));
		
		buildUrlCheckEventiMultitenant(remoteStore,  versionInBaseUrl, baseUrlMultitenantWithoutKeys, 
				apiPdndVersionOverride, apiVersion, pathEventKeys);
	}
	private void buildUrlCheckEventiMultitenant(RemoteStoreConfig remoteStore,  int versionInBaseUrl, Map<String, String> baseUrlMultitenantWithoutKeys, 
			int apiPdndVersionOverride, int apiVersion, String pathEventKeys) throws ProtocolException {
		if(baseUrlMultitenantWithoutKeys!=null && !baseUrlMultitenantWithoutKeys.isEmpty()) {
			Map<String, Map<String, String>> multiTenantUpgraded = remoteStore.getMultiTenantMetadati(); 
			if(multiTenantUpgraded==null) {
				multiTenantUpgraded = new HashMap<>();
			}
			for (Map.Entry<String,String> entry : baseUrlMultitenantWithoutKeys.entrySet()) {
				String baseUrlTenant = entry.getValue();
				String urlEventiTenant = concatenateUrl(baseUrlTenant, pathEventKeys);
				Map<String, String> multiTenantValues =  multiTenantUpgraded.get(entry.getKey());
				if(multiTenantValues==null) {
					multiTenantValues = new HashMap<>();
				}
				if(apiPdndVersionOverride>0 && apiPdndVersionOverride!=versionInBaseUrl) {
					urlEventiTenant = upgradeBaseUrlApiPDND(urlEventiTenant, versionInBaseUrl, apiPdndVersionOverride, pathEventKeys);
				}
				multiTenantValues.put(ModIUtils.API_PDND_EVENTS_KEYS_PATH, urlEventiTenant);
				multiTenantValues.put(ModIUtils.API_PDND_EVENTS_KEYS_PARAMETER_LASTEVENTID, this.getApiPDNDEventKeysParameterLastEventId(apiVersion));
				multiTenantValues.put(ModIUtils.API_PDND_EVENTS_KEYS_PARAMETER_LIMIT, this.getApiPDNDEventKeysParameterLimit(apiVersion));
				multiTenantUpgraded.put(entry.getKey(), multiTenantValues);
			}
			remoteStore.setMultiTenantMetadati(multiTenantUpgraded);
		}
	}
	private void buildUrlCheckClients(String rsc,RemoteStoreConfig remoteStore, int versionInBaseUrl, String baseUrlWithoutKeys, Map<String, String> baseUrlMultitenantWithoutKeys) throws ProtocolException, UtilsException {

		int apiPdndVersionOverride = this.readAPIPDNDVersionClients(rsc); // consente di sovrascrivere il default
		int apiVersion = (apiPdndVersionOverride>0 && apiPdndVersionOverride!=versionInBaseUrl) ? apiPdndVersionOverride : versionInBaseUrl;
			
		String pathClients = this.getApiPDNDClientsPath(apiVersion);
		if(!pathClients.startsWith(URL_CHAR_DELIMITER)) {
			pathClients = URL_CHAR_DELIMITER + pathClients;
		}
		String urlClients = concatenateUrl(baseUrlWithoutKeys, pathClients);
		if(apiPdndVersionOverride>0 && apiPdndVersionOverride!=versionInBaseUrl) {
			urlClients = upgradeBaseUrlApiPDND(urlClients, versionInBaseUrl, apiPdndVersionOverride, pathClients);
		}
		remoteStore.getMetadati().put(ModIUtils.API_PDND_CLIENTS_PATH, urlClients);
		remoteStore.getMetadati().put(ModIUtils.API_PDND_CLIENTS_ORGANIZATION_JSON_PATH, this.getApiPDNDClientsOrganizationJsonPath(apiVersion));
		
		buildUrlCheckClientsMultitenant(remoteStore,  versionInBaseUrl, baseUrlMultitenantWithoutKeys, 
				apiPdndVersionOverride, apiVersion, pathClients);
	}
	private void buildUrlCheckClientsMultitenant(RemoteStoreConfig remoteStore,  int versionInBaseUrl, Map<String, String> baseUrlMultitenantWithoutKeys, 
			int apiPdndVersionOverride, int apiVersion, String pathClients) throws ProtocolException {
		if(baseUrlMultitenantWithoutKeys!=null && !baseUrlMultitenantWithoutKeys.isEmpty()) {
			Map<String, Map<String, String>> multiTenantUpgraded = remoteStore.getMultiTenantMetadati(); 
			if(multiTenantUpgraded==null) {
				multiTenantUpgraded = new HashMap<>();
			}
			for (Map.Entry<String,String> entry : baseUrlMultitenantWithoutKeys.entrySet()) {
				String baseUrlTenant = entry.getValue();
				String urlClientTenant =  concatenateUrl(baseUrlTenant, pathClients);
				Map<String, String> multiTenantValues =  multiTenantUpgraded.get(entry.getKey());
				if(multiTenantValues==null) {
					multiTenantValues = new HashMap<>();
				}
				if(apiPdndVersionOverride>0 && apiPdndVersionOverride!=versionInBaseUrl) {
					urlClientTenant = upgradeBaseUrlApiPDND(urlClientTenant, versionInBaseUrl, apiPdndVersionOverride, pathClients);
				}
				multiTenantValues.put(ModIUtils.API_PDND_CLIENTS_PATH, urlClientTenant);
				multiTenantValues.put(ModIUtils.API_PDND_CLIENTS_ORGANIZATION_JSON_PATH, this.getApiPDNDClientsOrganizationJsonPath(apiVersion));
				multiTenantUpgraded.put(entry.getKey(), multiTenantValues);
			}
			remoteStore.setMultiTenantMetadati(multiTenantUpgraded);
		}
	}
	private void buildUrlCheckOrganizations(String rsc,RemoteStoreConfig remoteStore, int versionInBaseUrl, String baseUrlWithoutKeys, Map<String, String> baseUrlMultitenantWithoutKeys) throws ProtocolException, UtilsException {

		int apiPdndVersionOverride = this.readAPIPDNDVersionOrganizations(rsc); // consente di sovrascrivere il default
		int apiVersion = (apiPdndVersionOverride>0 && apiPdndVersionOverride!=versionInBaseUrl) ? apiPdndVersionOverride : versionInBaseUrl;
			
		String pathOrganizations = this.getApiPDNDOrganizationsPath(apiVersion);
		if(!pathOrganizations.startsWith(URL_CHAR_DELIMITER)) {
			pathOrganizations = URL_CHAR_DELIMITER + pathOrganizations;
		}
		String urlOrganizations =  concatenateUrl(baseUrlWithoutKeys, pathOrganizations);
		if(apiPdndVersionOverride>0 && apiPdndVersionOverride!=versionInBaseUrl) {
			urlOrganizations = upgradeBaseUrlApiPDND(urlOrganizations, versionInBaseUrl, apiPdndVersionOverride, pathOrganizations);
		}
		remoteStore.getMetadati().put(ModIUtils.API_PDND_ORGANIZATIONS_PATH, urlOrganizations);
		
		buildUrlCheckOrganizationsMultitenant(remoteStore, versionInBaseUrl, baseUrlMultitenantWithoutKeys, 
				apiPdndVersionOverride, pathOrganizations);
	}
	private void buildUrlCheckOrganizationsMultitenant(RemoteStoreConfig remoteStore,  int versionInBaseUrl, Map<String, String> baseUrlMultitenantWithoutKeys, 
			int apiPdndVersionOverride, String pathOrganizations)  {
		if(baseUrlMultitenantWithoutKeys!=null && !baseUrlMultitenantWithoutKeys.isEmpty()) {
			Map<String, Map<String, String>> multiTenantUpgraded = remoteStore.getMultiTenantMetadati(); 
			if(multiTenantUpgraded==null) {
				multiTenantUpgraded = new HashMap<>();
			}
			for (Map.Entry<String,String> entry : baseUrlMultitenantWithoutKeys.entrySet()) {
				String baseUrlTenant = entry.getValue();
				String urlOrganizationsTenant =  concatenateUrl(baseUrlTenant, pathOrganizations);
				Map<String, String> multiTenantValues =  multiTenantUpgraded.get(entry.getKey());
				if(multiTenantValues==null) {
					multiTenantValues = new HashMap<>();
				}
				if(apiPdndVersionOverride>0 && apiPdndVersionOverride!=versionInBaseUrl) {
					urlOrganizationsTenant = upgradeBaseUrlApiPDND(urlOrganizationsTenant, versionInBaseUrl, apiPdndVersionOverride, pathOrganizations);
				}
				multiTenantValues.put(ModIUtils.API_PDND_ORGANIZATIONS_PATH, urlOrganizationsTenant);
				multiTenantUpgraded.put(entry.getKey(), multiTenantValues);
			}
			remoteStore.setMultiTenantMetadati(multiTenantUpgraded);
		}
	}
	private String upgradeBaseUrlApiPDND(String orig, int versionInBaseUrl, int apiPdndVersionOverride, String path) {
		String suffixOrig = "/v"+versionInBaseUrl+path;
		String suffixNew = "/v"+apiPdndVersionOverride+path;
		return orig.replace(suffixOrig, suffixNew);
	}
	
	
	private void readKeyType(Properties p, String debugPrefix, RemoteStoreConfig config) throws ProtocolException {
		String keyType = p.getProperty("keyType");
		if(keyType!=null) {
			keyType = keyType.trim();
		}
		if(keyType==null || StringUtils.isEmpty(keyType)) {
			throw new ProtocolException(debugPrefix+" non completa; key type non indicato");
		}
		try {
			RemoteKeyType rkt = RemoteKeyType.toEnumFromName(keyType);
			if(rkt==null) {
				throw new ProtocolException("Non valido");
			}
			this.remoteStoreKeyTypeMap.put(config.getStoreName(), rkt);
		}catch(Exception e) {
			throw new ProtocolException(debugPrefix+" non completa; key type indicato '"+keyType+"' non valido",e);
		}
	}
	
	public boolean isRemoteStore(String name) throws ProtocolException {
		return PDNDResolver.isRemoteStore(name, getRemoteStoreConfig());
	}
	public RemoteStoreConfig getRemoteStoreConfig(String name, IDSoggetto idDominio) throws ProtocolException {
		return PDNDResolver.getRemoteStoreConfig(name, idDominio, getRemoteStoreConfig());
	}
	public RemoteStoreConfig getRemoteStoreConfigByTokenPolicy(String name, IDSoggetto idDominio) throws ProtocolException {
		return PDNDResolver.getRemoteStoreConfigByTokenPolicy(name, idDominio, getRemoteStoreConfig());
	}
	// riferito in org.openspcoop2.protocol.utils.ModIUtils
	// non modificare il nome
	public RemoteKeyType getRemoteKeyType(String name) throws ProtocolException {
		return getRemoteStoreKeyTypeMap().get(name);
	}
	
	
	
	/* **** TOKEN OAUTH **** */
	
	private List<String> validazioneTokenOAuthClaimsRequired= null;
	private List<String> getValidazioneTokenOAuthClaimsRequired() throws ProtocolException{
    	if(this.validazioneTokenOAuthClaimsRequired==null){
    		String propertyName = "org.openspcoop2.protocol.modipa.token.oauth.claims.required";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName);
				if(value!=null && StringUtils.isNotEmpty(value)) {
					this.validazioneTokenOAuthClaimsRequired = ModISecurityConfig.convertToList(value);
				}
				else {
					this.validazioneTokenOAuthClaimsRequired = new ArrayList<>();
				}
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(propertyName, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.validazioneTokenOAuthClaimsRequired;
	}
	private Map<String,List<String>> validazioneTokenOAuthClaimsRequiredSoggetto = new HashMap<>();
	public List<String> getValidazioneTokenOAuthClaimsRequired(String soggetto) throws ProtocolException{
		if(!this.validazioneTokenOAuthClaimsRequiredSoggetto.containsKey(soggetto)){
    		String propertyName = "org.openspcoop2.protocol.modipa."+soggetto+".token.oauth.claims.required";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName);
				if(value!=null && StringUtils.isNotEmpty(value)) {
					this.validazioneTokenOAuthClaimsRequiredSoggetto.put(soggetto, ModISecurityConfig.convertToList(value));
				}
				else {
					this.validazioneTokenOAuthClaimsRequiredSoggetto.put(soggetto, getValidazioneTokenOAuthClaimsRequired());
				}
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(propertyName, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.validazioneTokenOAuthClaimsRequiredSoggetto.get(soggetto);
	}
	
	
	private List<String> validazioneTokenPDNDClaimsRequired= null;
	private List<String> getValidazioneTokenPDNDClaimsRequired() throws ProtocolException{
    	if(this.validazioneTokenPDNDClaimsRequired==null){
    		String propertyName = "org.openspcoop2.protocol.modipa.token.pdnd.claims.required";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName);
				if(value!=null && StringUtils.isNotEmpty(value)) {
					this.validazioneTokenPDNDClaimsRequired = ModISecurityConfig.convertToList(value);
				}
				else {
					this.validazioneTokenOAuthClaimsRequired = new ArrayList<>();
				}
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(propertyName, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.validazioneTokenPDNDClaimsRequired;
	}
	private Map<String,List<String>> validazioneTokenPDNDClaimsRequiredSoggetto = new HashMap<>();
	public List<String> getValidazioneTokenPDNDClaimsRequired(String soggetto) throws ProtocolException{
		if(!this.validazioneTokenPDNDClaimsRequiredSoggetto.containsKey(soggetto)){
    		String propertyName = "org.openspcoop2.protocol.modipa."+soggetto+".token.pdnd.claims.required";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName);
				if(value!=null && StringUtils.isNotEmpty(value)) {
					this.validazioneTokenPDNDClaimsRequiredSoggetto.put(soggetto, ModISecurityConfig.convertToList(value));
				}
				else {
					this.validazioneTokenPDNDClaimsRequiredSoggetto.put(soggetto, getValidazioneTokenPDNDClaimsRequired());
				}
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(propertyName, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.validazioneTokenPDNDClaimsRequiredSoggetto.get(soggetto);
	}
	
	private static final String PREFIX_PROPERTY_MODIPA_PDND = "org.openspcoop2.protocol.modipa.pdnd.";
	
	private Boolean isValidazioneTokenPDNDProducerIdCheck= null;
	private boolean isValidazioneTokenPDNDProducerIdCheck() throws ProtocolException{
    	if(this.isValidazioneTokenPDNDProducerIdCheck==null){
    		String propertyName = "org.openspcoop2.protocol.modipa.pdnd.producerId.check";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName);
				if(value!=null && StringUtils.isNotEmpty(value)) {
					this.isValidazioneTokenPDNDProducerIdCheck = Boolean.parseBoolean(value);
				}
				else {
					this.isValidazioneTokenPDNDProducerIdCheck = true;
				}
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(propertyName, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.isValidazioneTokenPDNDProducerIdCheck;
	}
	private Map<String,Boolean> isValidazioneTokenPDNDProducerIdCheckSoggetto = new HashMap<>();
	public boolean isValidazioneTokenPDNDProducerIdCheck(String soggetto) throws ProtocolException{
		if(!this.isValidazioneTokenPDNDProducerIdCheckSoggetto.containsKey(soggetto)){
    		String propertyName = PREFIX_PROPERTY_MODIPA_PDND+soggetto+".producerId.check"; 
    		try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName);
				if(value!=null && StringUtils.isNotEmpty(value)) {
					this.isValidazioneTokenPDNDProducerIdCheckSoggetto.put(soggetto, Boolean.parseBoolean(value));
				}
				else {
					this.isValidazioneTokenPDNDProducerIdCheckSoggetto.put(soggetto, isValidazioneTokenPDNDProducerIdCheck());
				}
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(propertyName, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.isValidazioneTokenPDNDProducerIdCheckSoggetto.get(soggetto);
	}
	
	private Boolean isPdndProducerIdCheckUnique = null;
	public boolean isPdndProducerIdCheckUnique(){
		if(this.isPdndProducerIdCheckUnique==null){
			
			Boolean defaultValue =false;
			String propertyName = "org.openspcoop2.protocol.modipa.pdnd.producerId.console.checkUnique";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isPdndProducerIdCheckUnique = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.isPdndProducerIdCheckUnique = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
				this.isPdndProducerIdCheckUnique = defaultValue;
			}
		}

		return this.isPdndProducerIdCheckUnique;
	}
	
	
	
	private Boolean isValidazioneTokenPDNDEServiceIdCheck= null;
	private boolean isValidazioneTokenPDNDEServiceIdCheck() throws ProtocolException{
    	if(this.isValidazioneTokenPDNDEServiceIdCheck==null){
    		String propertyName = "org.openspcoop2.protocol.modipa.pdnd.eServiceId.check";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName);
				if(value!=null && StringUtils.isNotEmpty(value)) {
					this.isValidazioneTokenPDNDEServiceIdCheck = Boolean.parseBoolean(value);
				}
				else {
					this.isValidazioneTokenPDNDEServiceIdCheck = true;
				}
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(propertyName, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.isValidazioneTokenPDNDEServiceIdCheck;
	}
	private Map<String,Boolean> isValidazioneTokenPDNDEServiceIdCheckSoggetto = new HashMap<>();
	public boolean isValidazioneTokenPDNDEServiceIdCheck(String soggetto) throws ProtocolException{
		if(!this.isValidazioneTokenPDNDEServiceIdCheckSoggetto.containsKey(soggetto)){
    		String propertyName = PREFIX_PROPERTY_MODIPA_PDND+soggetto+".eServiceId.check"; 
    		try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName);
				if(value!=null && StringUtils.isNotEmpty(value)) {
					this.isValidazioneTokenPDNDEServiceIdCheckSoggetto.put(soggetto, Boolean.parseBoolean(value));
				}
				else {
					this.isValidazioneTokenPDNDEServiceIdCheckSoggetto.put(soggetto, isValidazioneTokenPDNDEServiceIdCheck());
				}
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(propertyName, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.isValidazioneTokenPDNDEServiceIdCheckSoggetto.get(soggetto);
	}
	
	private Boolean isPdndEServiceIdCheckUnique = null;
	public boolean isPdndEServiceIdCheckUnique(){
		if(this.isPdndEServiceIdCheckUnique==null){
			
			Boolean defaultValue =false;
			String propertyName = "org.openspcoop2.protocol.modipa.pdnd.eServiceId.console.checkUnique";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isPdndEServiceIdCheckUnique = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.isPdndEServiceIdCheckUnique = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
				this.isPdndEServiceIdCheckUnique = defaultValue;
			}
		}

		return this.isPdndEServiceIdCheckUnique;
	}
	
	
	private Boolean isValidazioneTokenPDNDDescriptorIdCheck= null;
	private boolean isValidazioneTokenPDNDDescriptorIdCheck() throws ProtocolException{
    	if(this.isValidazioneTokenPDNDDescriptorIdCheck==null){
    		String propertyName = "org.openspcoop2.protocol.modipa.pdnd.descriptorId.check";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName);
				if(value!=null && StringUtils.isNotEmpty(value)) {
					this.isValidazioneTokenPDNDDescriptorIdCheck = Boolean.parseBoolean(value);
				}
				else {
					this.isValidazioneTokenPDNDDescriptorIdCheck = true;
				}
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(propertyName, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.isValidazioneTokenPDNDDescriptorIdCheck;
	}
	private Map<String,Boolean> isValidazioneTokenPDNDDescriptorIdCheckSoggetto = new HashMap<>();
	public boolean isValidazioneTokenPDNDDescriptorIdCheck(String soggetto) throws ProtocolException{
		if(!this.isValidazioneTokenPDNDDescriptorIdCheckSoggetto.containsKey(soggetto)){
    		String propertyName = PREFIX_PROPERTY_MODIPA_PDND+soggetto+".eServiceId.check"; 
    		try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName);
				if(value!=null && StringUtils.isNotEmpty(value)) {
					this.isValidazioneTokenPDNDDescriptorIdCheckSoggetto.put(soggetto, Boolean.parseBoolean(value));
				}
				else {
					this.isValidazioneTokenPDNDDescriptorIdCheckSoggetto.put(soggetto, isValidazioneTokenPDNDDescriptorIdCheck());
				}
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(propertyName, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.isValidazioneTokenPDNDDescriptorIdCheckSoggetto.get(soggetto);
	}
	
	private Boolean isPdndDescriptorIdCheckUnique = null;
	public boolean isPdndDescriptorIdCheckUnique(){
		if(this.isPdndDescriptorIdCheckUnique==null){
			
			Boolean defaultValue =false;
			String propertyName = "org.openspcoop2.protocol.modipa.pdnd.descriptorId.checkUnique";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isPdndDescriptorIdCheckUnique = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.isPdndDescriptorIdCheckUnique = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
				this.isPdndDescriptorIdCheckUnique = defaultValue;
			}
		}

		return this.isPdndDescriptorIdCheckUnique;
	}
	
	
	// API PDND
	
	private static final String PREFIX_API_PDND = "org.openspcoop2.protocol.modipa.pdnd.api";
	
	private String getApiPDNDBaseUrlVersionPattern = null;
	public String getApiPDNDBaseUrlVersionPattern() throws ProtocolException {
		if(this.getApiPDNDBaseUrlVersionPattern == null){
			String pName = PREFIX_API_PDND+".baseUrl.version.pattern";
			try{ 
				String name = null;
				name = this.reader.getValueConvertEnvProperties(pName);
				if(name==null){
					throw new ProtocolException("Non configurata");
				}
				name = name.trim();
				this.getApiPDNDBaseUrlVersionPattern = name;
			} catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}    
		}

		return this.getApiPDNDBaseUrlVersionPattern;
	}
		
	
	private static final String PREFIX_API_PDND_V = PREFIX_API_PDND+".v";
	
	// keys
	
	private Map<String,String> getApiPDNDClientKeysPath = new HashMap<>();
	public String getApiPDNDClientKeysPath(int version) throws ProtocolException {
		String key = version+"";
		if(!this.getApiPDNDClientKeysPath.containsKey(key)){
			String pName = PREFIX_API_PDND_V+version+".keys.client.path";
			try{ 
				String name = null;
				name = this.reader.getValueConvertEnvProperties(pName);
				if(name==null){
					throw new ProtocolException(PREFIX_PROPRIETA+pName+"'"+SUFFIX_NON_TROVATA);
				}
				name = name.trim();
				this.getApiPDNDClientKeysPath.put(key, name);
			} catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}    
		}

		return this.getApiPDNDClientKeysPath.get(key);
	}
	
	private Map<String,String> getApiPDNDClientKeysJsonPath = new HashMap<>();
	public String getApiPDNDClientKeysJsonPath(int version) throws ProtocolException {
		String key = version+"";
		if(!this.getApiPDNDClientKeysJsonPath.containsKey(key)){
			String pName = PREFIX_API_PDND_V+version+".keys.client.jsonPath";
			try{ 
				String name = null;
				name = this.reader.getValueConvertEnvProperties(pName);
				if(name==null){
					name="";
				}
				name = name.trim();
				this.getApiPDNDClientKeysJsonPath.put(key, name);
			} catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}    
		}

		String s = this.getApiPDNDClientKeysJsonPath.get(key);
		return s!=null && "".equals(s) ? null : s;
	}
	
	private Map<String,String> getApiPDNDServerKeysPath = new HashMap<>();
	public String getApiPDNDServerKeysPath(int version) throws ProtocolException {
		String key = version+"";
		if(!this.getApiPDNDServerKeysPath.containsKey(key)){
			String pName = PREFIX_API_PDND_V+version+".keys.server.path";
			try{ 
				String name = null;
				name = this.reader.getValueConvertEnvProperties(pName);
				if(name==null){
					throw new ProtocolException(PREFIX_PROPRIETA+pName+"'"+SUFFIX_NON_TROVATA);
				}
				name = name.trim();
				this.getApiPDNDServerKeysPath.put(key, name);
			} catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}    
		}

		return this.getApiPDNDServerKeysPath.get(key);
	}
	
	private Map<String,String> getApiPDNDServerKeysJsonPath = new HashMap<>();
	public String getApiPDNDServerKeysJsonPath(int version) throws ProtocolException {
		String key = version+"";
		if(!this.getApiPDNDServerKeysJsonPath.containsKey(key)){
			String pName = PREFIX_API_PDND_V+version+".keys.server.jsonPath";
			try{ 
				String name = null;
				name = this.reader.getValueConvertEnvProperties(pName);
				if(name==null){
					name="";
				}
				name = name.trim();
				this.getApiPDNDServerKeysJsonPath.put(key, name);
			} catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}    
		}

		String s = this.getApiPDNDServerKeysJsonPath.get(key);
		return s!=null && "".equals(s) ? null : s;
	}
	
	private Map<String,Boolean> isApiPDNDServerKeysFaultClientCheck = new HashMap<>();
	public boolean isApiPDNDServerKeysFaultClientCheck(int version) throws ProtocolException {
		String key = version+"";
		if(!this.isApiPDNDServerKeysFaultClientCheck.containsKey(key)){
			String pName = PREFIX_API_PDND_V+version+".keys.server.faultClientCheck";
			try{ 
				String name = null;
				name = this.reader.getValueConvertEnvProperties(pName);
				if(name==null){
					name="false";
				}
				name = name.trim();
				this.isApiPDNDServerKeysFaultClientCheck.put(key, "true".equalsIgnoreCase(name));
			} catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}    
		}

		Boolean s = this.isApiPDNDServerKeysFaultClientCheck.get(key);
		return s!=null && s.booleanValue();
	}
	
	// events
	
	private Map<String,String> getApiPDNDEventKeysPath = new HashMap<>();
	public String getApiPDNDEventKeysPath(int version) throws ProtocolException {
		String key = version+"";
		if(!this.getApiPDNDEventKeysPath.containsKey(key)){
			String pName = PREFIX_API_PDND_V+version+".events.keys.path";
			try{ 
				String name = null;
				name = this.reader.getValueConvertEnvProperties(pName);
				if(name==null){
					throw new ProtocolException(PREFIX_PROPRIETA+pName+"'"+SUFFIX_NON_TROVATA);
				}
				name = name.trim();
				this.getApiPDNDEventKeysPath.put(key, name);
			} catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}    
		}

		return this.getApiPDNDEventKeysPath.get(key);
	}
	
	private Map<String,String> getApiPDNDEventKeysParameterLastEventId = new HashMap<>();
	public String getApiPDNDEventKeysParameterLastEventId(int version) throws ProtocolException {
		String key = version+"";
		if(!this.getApiPDNDEventKeysParameterLastEventId.containsKey(key)){
			String pName = PREFIX_API_PDND_V+version+".events.keys.parameter.lastEventId";
			try{ 
				String name = null;
				name = this.reader.getValueConvertEnvProperties(pName);
				if(name==null){
					throw new ProtocolException(PREFIX_PROPRIETA+pName+"'"+SUFFIX_NON_TROVATA);
				}
				name = name.trim();
				this.getApiPDNDEventKeysParameterLastEventId.put(key, name);
			} catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}    
		}

		return this.getApiPDNDEventKeysParameterLastEventId.get(key);
	}
	
	private Map<String,String> getApiPDNDEventKeysParameterLimit = new HashMap<>();
	public String getApiPDNDEventKeysParameterLimit(int version) throws ProtocolException {
		String key = version+"";
		if(!this.getApiPDNDEventKeysParameterLimit.containsKey(key)){
			String pName = PREFIX_API_PDND_V+version+".events.keys.parameter.limit";
			try{ 
				String name = null;
				name = this.reader.getValueConvertEnvProperties(pName);
				if(name==null){
					throw new ProtocolException(PREFIX_PROPRIETA+pName+"'"+SUFFIX_NON_TROVATA);
				}
				name = name.trim();
				this.getApiPDNDEventKeysParameterLimit.put(key, name);
			} catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}    
		}

		return this.getApiPDNDEventKeysParameterLimit.get(key);
	}
	
	// clients
	
	private Map<String,String> getApiPDNDClientsVersionPatttern = new HashMap<>();
	public String getApiPDNDClientsVersionPatttern(int version) throws ProtocolException {
		String key = version+"";
		if(!this.getApiPDNDClientsVersionPatttern.containsKey(key)){
			String pName = PREFIX_API_PDND_V+version+".clients.versionIdentifier.jsonPath";
			try{ 
				String name = null;
				name = this.reader.getValueConvertEnvProperties(pName);
				if(name==null){
					throw new ProtocolException(PREFIX_PROPRIETA+pName+"'"+SUFFIX_NON_TROVATA);
				}
				name = name.trim();
				this.getApiPDNDClientsVersionPatttern.put(key, name);
			} catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}    
		}

		return this.getApiPDNDClientsVersionPatttern.get(key);
	}
		
	private Map<String,String> getApiPDNDClientsPath = new HashMap<>();
	public String getApiPDNDClientsPath(int version) throws ProtocolException {
		String key = version+"";
		if(!this.getApiPDNDClientsPath.containsKey(key)){
			String pName = PREFIX_API_PDND_V+version+".clients.path";
			try{ 
				String name = null;
				name = this.reader.getValueConvertEnvProperties(pName);
				if(name==null){
					throw new ProtocolException(PREFIX_PROPRIETA+pName+"'"+SUFFIX_NON_TROVATA);
				}
				name = name.trim();
				this.getApiPDNDClientsPath.put(key, name);
			} catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}    
		}

		return this.getApiPDNDClientsPath.get(key);
	}
	
	private Map<String,String> getApiPDNDClientsIdJsonPath = new HashMap<>();
	public String getApiPDNDClientsIdJsonPath(int version) throws ProtocolException {
		String key = version+"";
		if(!this.getApiPDNDClientsIdJsonPath.containsKey(key)){
			String pName = PREFIX_API_PDND_V+version+".clients.id.jsonPath";
			try{ 
				String name = null;
				name = this.reader.getValueConvertEnvProperties(pName);
				if(name==null){
					throw new ProtocolException(PREFIX_PROPRIETA+pName+"'"+SUFFIX_NON_TROVATA);
				}
				name = name.trim();
				this.getApiPDNDClientsIdJsonPath.put(key, name);
			} catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}    
		}

		return this.getApiPDNDClientsIdJsonPath.get(key);
	}
	
	private Map<String,String> getApiPDNDClientsOrganizationJsonPath = new HashMap<>();
	public String getApiPDNDClientsOrganizationJsonPath(int version) throws ProtocolException {
		String key = version+"";
		if(!this.getApiPDNDClientsOrganizationJsonPath.containsKey(key)){
			String pName = PREFIX_API_PDND_V+version+".clients.organization.jsonPath";
			try{ 
				String name = null;
				name = this.reader.getValueConvertEnvProperties(pName);
				if(name==null){
					throw new ProtocolException(PREFIX_PROPRIETA+pName+"'"+SUFFIX_NON_TROVATA);
				}
				name = name.trim();
				this.getApiPDNDClientsOrganizationJsonPath.put(key, name);
			} catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}    
		}

		return this.getApiPDNDClientsOrganizationJsonPath.get(key);
	}
	
	private Map<String,String> getApiPDNDClientsNameJsonPath = new HashMap<>();
	public String getApiPDNDClientsNameJsonPath(int version) throws ProtocolException {
		String key = version+"";
		if(!this.getApiPDNDClientsNameJsonPath.containsKey(key)){
			String pName = PREFIX_API_PDND_V+version+".clients.name.jsonPath";
			try{ 
				String name = null;
				name = this.reader.getValueConvertEnvProperties(pName);
				if(name==null){
					name = ""; // nella v1 non esiste
				}
				name = name.trim();
				this.getApiPDNDClientsNameJsonPath.put(key, name);
			} catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}    
		}

		return this.getApiPDNDClientsNameJsonPath.get(key);
	}
	
	private Map<String,String> getApiPDNDClientsDescriptionJsonPath = new HashMap<>();
	public String getApiPDNDClientsDescriptionJsonPath(int version) throws ProtocolException {
		String key = version+"";
		if(!this.getApiPDNDClientsDescriptionJsonPath.containsKey(key)){
			String pName = PREFIX_API_PDND_V+version+".clients.description.jsonPath";
			try{ 
				String name = null;
				name = this.reader.getValueConvertEnvProperties(pName);
				if(name==null){
					name = ""; // nella v1 non esiste
				}
				name = name.trim();
				this.getApiPDNDClientsDescriptionJsonPath.put(key, name);
			} catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}    
		}

		return this.getApiPDNDClientsDescriptionJsonPath.get(key);
	}
	
	// riferito in org.openspcoop2.protocol.utils.ModIUtils
	public ModIPDNDClientConfig getAPIPDNDClientConfig() throws ProtocolException {
		ModIPDNDClientConfig instance = new ModIPDNDClientConfig(this.log);
		fillAPIPDNDClientConfig(instance);
		return instance;
	}
	// riferito in org.openspcoop2.protocol.utils.ModIUtils
	public ModIPDNDClientConfig getAPIPDNDClientConfig(Logger log) throws ProtocolException {
		ModIPDNDClientConfig instance = new ModIPDNDClientConfig(log);
		fillAPIPDNDClientConfig(instance);
		return instance;
	}
	// riferito in org.openspcoop2.protocol.utils.ModIUtils
	public ModIPDNDClientConfig getAPIPDNDClientConfig(String details) throws ProtocolException  {
		ModIPDNDClientConfig instance = new ModIPDNDClientConfig(details, this.log);
		fillAPIPDNDClientConfig(instance);
		return instance;
	}
	// riferito in org.openspcoop2.protocol.utils.ModIUtils
	public ModIPDNDClientConfig getAPIPDNDClientConfig(String details, Logger log) throws ProtocolException  {
		ModIPDNDClientConfig instance = new ModIPDNDClientConfig(details, log);
		fillAPIPDNDClientConfig(instance);
		return instance;
	}
	private ModIPDNDClientConfig fillAPIPDNDClientConfig(ModIPDNDClientConfig c) throws ProtocolException  {
		
		c.setVersion1JsonPathMatch(this.getApiPDNDClientsVersionPatttern(1));
		c.setVersion2JsonPathMatch(this.getApiPDNDClientsVersionPatttern(2));
		
		c.setIdJsonPath(this.getApiPDNDClientsIdJsonPath);
		c.setOrganizationJsonPath(this.getApiPDNDClientsOrganizationJsonPath);
		c.setNameJsonPath(this.getApiPDNDClientsNameJsonPath);
		c.setDescriptionJsonPath(this.getApiPDNDClientsDescriptionJsonPath);
		return c;
	}
	
	// organizations
	
	private Map<String,String> getApiPDNDOrganizationsVersionPatttern = new HashMap<>();
	public String getApiPDNDOrganizationsVersionPatttern(int version) throws ProtocolException {
		String key = version+"";
		if(!this.getApiPDNDOrganizationsVersionPatttern.containsKey(key)){
			String pName = PREFIX_API_PDND_V+version+".organizations.versionIdentifier.jsonPath";
			try{ 
				String name = null;
				name = this.reader.getValueConvertEnvProperties(pName);
				if(name==null){
					throw new ProtocolException(PREFIX_PROPRIETA+pName+"'"+SUFFIX_NON_TROVATA);
				}
				name = name.trim();
				this.getApiPDNDOrganizationsVersionPatttern.put(key, name);
			} catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}    
		}

		return this.getApiPDNDOrganizationsVersionPatttern.get(key);
	}
	
	private Map<String,String> getApiPDNDOrganizationsPath = new HashMap<>();
	public String getApiPDNDOrganizationsPath(int version) throws ProtocolException {
		String key = version+"";
		if(!this.getApiPDNDOrganizationsPath.containsKey(key)){
			String pName = PREFIX_API_PDND_V+version+".organizations.path";
			try{ 
				String name = null;
				name = this.reader.getValueConvertEnvProperties(pName);
				if(name==null){
					throw new ProtocolException(PREFIX_PROPRIETA+pName+"'"+SUFFIX_NON_TROVATA);
				}
				name = name.trim();
				this.getApiPDNDOrganizationsPath.put(key, name);
			} catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}    
		}

		return this.getApiPDNDOrganizationsPath.get(key);
	}
	
	private Map<String,String> getApiPDNDOrganizationsIdJsonPath = new HashMap<>();
	public String getApiPDNDOrganizationsIdJsonPath(int version) throws ProtocolException {
		String key = version+"";
		if(!this.getApiPDNDOrganizationsIdJsonPath.containsKey(key)){
			String pName = PREFIX_API_PDND_V+version+".organizations.id.jsonPath";
			try{ 
				String name = null;
				name = this.reader.getValueConvertEnvProperties(pName);
				if(name==null){
					throw new ProtocolException(PREFIX_PROPRIETA+pName+"'"+SUFFIX_NON_TROVATA);
				}
				name = name.trim();
				this.getApiPDNDOrganizationsIdJsonPath.put(key, name);
			} catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}    
		}

		return this.getApiPDNDOrganizationsIdJsonPath.get(key);
	}
	
	private Map<String,String> getApiPDNDOrganizationsNameJsonPath = new HashMap<>();
	public String getApiPDNDOrganizationsNameJsonPath(int version) throws ProtocolException {
		String key = version+"";
		if(!this.getApiPDNDOrganizationsNameJsonPath.containsKey(key)){
			String pName = PREFIX_API_PDND_V+version+".organizations.name.jsonPath";
			try{ 
				String name = null;
				name = this.reader.getValueConvertEnvProperties(pName);
				if(name==null){
					throw new ProtocolException(PREFIX_PROPRIETA+pName+"'"+SUFFIX_NON_TROVATA);
				}
				name = name.trim();
				this.getApiPDNDOrganizationsNameJsonPath.put(key, name);
			} catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}    
		}

		return this.getApiPDNDOrganizationsNameJsonPath.get(key);
	}
	
	private Map<String,String> getApiPDNDOrganizationsExternalOriginJsonPath = new HashMap<>();
	public String getApiPDNDOrganizationsExternalOriginJsonPath(int version) throws ProtocolException {
		String key = version+"";
		if(!this.getApiPDNDOrganizationsExternalOriginJsonPath.containsKey(key)){
			String pName = PREFIX_API_PDND_V+version+".organizations.external.origin.jsonPath";
			try{ 
				String name = null;
				name = this.reader.getValueConvertEnvProperties(pName);
				if(name==null){
					throw new ProtocolException(PREFIX_PROPRIETA+pName+"'"+SUFFIX_NON_TROVATA);
				}
				name = name.trim();
				this.getApiPDNDOrganizationsExternalOriginJsonPath.put(key, name);
			} catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}    
		}

		return this.getApiPDNDOrganizationsExternalOriginJsonPath.get(key);
	}
	
	private Map<String,String> getApiPDNDOrganizationsExternalIdJsonPath = new HashMap<>();
	public String getApiPDNDOrganizationsExternalIdJsonPath(int version) throws ProtocolException {
		String key = version+"";
		if(!this.getApiPDNDOrganizationsExternalIdJsonPath.containsKey(key)){
			String pName = PREFIX_API_PDND_V+version+".organizations.external.id.jsonPath";
			try{ 
				String name = null;
				name = this.reader.getValueConvertEnvProperties(pName);
				if(name==null){
					throw new ProtocolException(PREFIX_PROPRIETA+pName+"'"+SUFFIX_NON_TROVATA);
				}
				name = name.trim();
				this.getApiPDNDOrganizationsExternalIdJsonPath.put(key, name);
			} catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}    
		}

		return this.getApiPDNDOrganizationsExternalIdJsonPath.get(key);
	}
	
	private Map<String,String> getApiPDNDOrganizationsCategoryJsonPath = new HashMap<>();
	public String getApiPDNDOrganizationsCategoryJsonPath(int version) throws ProtocolException {
		String key = version+"";
		if(!this.getApiPDNDOrganizationsCategoryJsonPath.containsKey(key)){
			String pName = PREFIX_API_PDND_V+version+".organizations.category.jsonPath";
			try{ 
				String name = null;
				name = this.reader.getValueConvertEnvProperties(pName);
				if(name==null){
					throw new ProtocolException(PREFIX_PROPRIETA+pName+"'"+SUFFIX_NON_TROVATA);
				}
				name = name.trim();
				this.getApiPDNDOrganizationsCategoryJsonPath.put(key, name);
			} catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}    
		}

		return this.getApiPDNDOrganizationsCategoryJsonPath.get(key);
	}
	
	private Map<String,String> getApiPDNDOrganizationsSubunitJsonPath = new HashMap<>();
	public String getApiPDNDOrganizationsSubunitJsonPath(int version) throws ProtocolException {
		String key = version+"";
		if(!this.getApiPDNDOrganizationsSubunitJsonPath.containsKey(key)){
			String pName = PREFIX_API_PDND_V+version+".organizations.subUnit.jsonPath";
			try{ 
				String name = null;
				name = this.reader.getValueConvertEnvProperties(pName);
				if(name==null){
					name = ""; // nella v1 non esiste
				}
				name = name.trim();
				this.getApiPDNDOrganizationsSubunitJsonPath.put(key, name);
			} catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}    
		}

		return this.getApiPDNDOrganizationsSubunitJsonPath.get(key);
	}
	
	// riferito in org.openspcoop2.protocol.utils.ModIUtils
	public ModIPDNDOrganizationConfig getAPIPDNDOrganizationConfig() throws ProtocolException {
		ModIPDNDOrganizationConfig instance = new ModIPDNDOrganizationConfig(this.log);
		fillAPIPDNDOrganizationConfig(instance);
		return instance;
	}
	// riferito in org.openspcoop2.protocol.utils.ModIUtils
	public ModIPDNDOrganizationConfig getAPIPDNDOrganizationConfig(Logger log) throws ProtocolException {
		ModIPDNDOrganizationConfig instance = new ModIPDNDOrganizationConfig(log);
		fillAPIPDNDOrganizationConfig(instance);
		return instance;
	}
	// riferito in org.openspcoop2.protocol.utils.ModIUtils
	public ModIPDNDOrganizationConfig getAPIPDNDOrganizationConfig(String details) throws ProtocolException  {
		ModIPDNDOrganizationConfig instance = new ModIPDNDOrganizationConfig(details, this.log);
		fillAPIPDNDOrganizationConfig(instance);
		return instance;
	}
	// riferito in org.openspcoop2.protocol.utils.ModIUtils
	public ModIPDNDOrganizationConfig getAPIPDNDOrganizationConfig(String details, Logger log) throws ProtocolException  {
		ModIPDNDOrganizationConfig instance = new ModIPDNDOrganizationConfig(details, log);
		fillAPIPDNDOrganizationConfig(instance);
		return instance;
	}
	private ModIPDNDOrganizationConfig fillAPIPDNDOrganizationConfig(ModIPDNDOrganizationConfig c) throws ProtocolException  {
		
		c.setVersion1JsonPathMatch(this.getApiPDNDOrganizationsVersionPatttern(1));
		c.setVersion2JsonPathMatch(this.getApiPDNDOrganizationsVersionPatttern(2));
		
		c.setIdJsonPath(this.getApiPDNDOrganizationsIdJsonPath);
		c.setNameJsonPath(this.getApiPDNDOrganizationsNameJsonPath);
		c.setExternalOriginJsonPath(this.getApiPDNDOrganizationsExternalOriginJsonPath);
		c.setExternalIdJsonPath(this.getApiPDNDOrganizationsExternalIdJsonPath);
		c.setCategoryJsonPath(this.getApiPDNDOrganizationsCategoryJsonPath);
		c.setSubUnitJsonPath(this.getApiPDNDOrganizationsSubunitJsonPath);
		return c;
	}
	
	
	/* **** KEY STORE **** */
		
	// riferito in org.openspcoop2.protocol.utils.ModIUtils
	// non modificare il nome
	public KeystoreParams getSicurezzaMessaggioCertificatiKeyStore() throws ProtocolException {
		KeystoreParams params = null;
		String keystoreType = getSicurezzaMessaggioCertificatiKeyStoreTipo();
		if(keystoreType!=null) {
			params = new KeystoreParams();
			params.setType(keystoreType);
			params.setPath(getSicurezzaMessaggioCertificatiKeyStorePath());
			params.setPassword(getSicurezzaMessaggioCertificatiKeyPassword());
			params.setKeyAlias(getSicurezzaMessaggioCertificatiKeyAlias());
			params.setKeyPassword(getSicurezzaMessaggioCertificatiKeyPassword());
		}
		return params;
	}
	
	private String sicurezzaMessaggioCertificatiKeyStoreTipo= null;
	private Boolean sicurezzaMessaggioCertificatiKeyStoreTipoReaded= null;
	public String getSicurezzaMessaggioCertificatiKeyStoreTipo() {
    	if(this.sicurezzaMessaggioCertificatiKeyStoreTipoReaded==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.keyStore.tipo"); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggioCertificatiKeyStoreTipo = value;
				}
				
				this.sicurezzaMessaggioCertificatiKeyStoreTipoReaded = true;
								
			}catch(java.lang.Exception e) {
				this.logError("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.keyStore.tipo' non impostata, errore:"+e.getMessage());
				this.sicurezzaMessaggioCertificatiKeyStoreTipoReaded = true;
			}
    	}
    	
    	return this.sicurezzaMessaggioCertificatiKeyStoreTipo;
	}	
	
	private String sicurezzaMessaggioCertificatiKeyStorePath= null;
	public String getSicurezzaMessaggioCertificatiKeyStorePath() throws ProtocolException{
    	if(this.sicurezzaMessaggioCertificatiKeyStorePath==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.keyStore.path"); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggioCertificatiKeyStorePath = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				this.logError("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.keyStore.path' non impostata, errore:"+e.getMessage());
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.sicurezzaMessaggioCertificatiKeyStorePath;
	}
	
	private String sicurezzaMessaggioCertificatiKeyStorePassword= null;
	public String getSicurezzaMessaggioCertificatiKeyStorePassword() throws ProtocolException{
    	if(this.sicurezzaMessaggioCertificatiKeyStorePassword==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.keyStore.password"); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggioCertificatiKeyStorePassword = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				this.logError("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.keyStore.password' non impostata, errore:"+e.getMessage());
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.sicurezzaMessaggioCertificatiKeyStorePassword;
	}	
	
	private String sicurezzaMessaggioCertificatiKeyAlias= null;
	public String getSicurezzaMessaggioCertificatiKeyAlias() throws ProtocolException{
    	if(this.sicurezzaMessaggioCertificatiKeyAlias==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.key.alias"); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggioCertificatiKeyAlias = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				this.logError("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.key.alias' non impostata, errore:"+e.getMessage());
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.sicurezzaMessaggioCertificatiKeyAlias;
	}	
	
	private String sicurezzaMessaggioCertificatiKeyPassword= null;
	public String getSicurezzaMessaggioCertificatiKeyPassword() throws ProtocolException{
    	if(this.sicurezzaMessaggioCertificatiKeyPassword==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.key.password"); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggioCertificatiKeyPassword = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				this.logError("Proprietà 'org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.key.password' non impostata, errore:"+e.getMessage());
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.sicurezzaMessaggioCertificatiKeyPassword;
	}	
	
	private Boolean sicurezzaMessaggioCertificatiKeyClientIdRead = null;
	private String sicurezzaMessaggioCertificatiKeyClientId= null;
	public String getSicurezzaMessaggioCertificatiKeyClientId() throws ProtocolException{
    	if(this.sicurezzaMessaggioCertificatiKeyClientIdRead==null){
	    	String pName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.key.clientId";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(pName); 
				
				if (value != null){
					value = value.trim();
					if(StringUtils.isNotEmpty(value)) {
						this.sicurezzaMessaggioCertificatiKeyClientId = value;
					}
				}

				this.sicurezzaMessaggioCertificatiKeyClientIdRead = true;
				
			}catch(java.lang.Exception e) {
				this.logError(getPrefixProprieta(pName)+"non impostata, errore:"+e.getMessage());
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.sicurezzaMessaggioCertificatiKeyClientId;
	}
	
	private Boolean sicurezzaMessaggioCertificatiKeyKidRead = null;
	private String sicurezzaMessaggioCertificatiKeyKid= null;
	public String getSicurezzaMessaggioCertificatiKeyKid() throws ProtocolException{
    	if(this.sicurezzaMessaggioCertificatiKeyKidRead==null){
	    	String pName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.certificati.key.kid";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(pName); 
				
				if (value != null){
					value = value.trim();
					if(StringUtils.isNotEmpty(value)) {
						this.sicurezzaMessaggioCertificatiKeyKid = value;
					}
				}

				this.sicurezzaMessaggioCertificatiKeyKidRead = true;
				
			}catch(java.lang.Exception e) {
				this.logError(getPrefixProprieta(pName)+"non impostata, errore:"+e.getMessage());
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.sicurezzaMessaggioCertificatiKeyKid;
	}
	
	
	
	
	/* **** CORNICE SICUREZZA **** */
	
	private Boolean isSicurezzaMessaggioCorniceSicurezzaEnabled = null;
	public Boolean isSicurezzaMessaggioCorniceSicurezzaEnabled(){
		if(this.isSicurezzaMessaggioCorniceSicurezzaEnabled==null){
			
			Boolean defaultValue = false;
			String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isSicurezzaMessaggioCorniceSicurezzaEnabled = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.isSicurezzaMessaggioCorniceSicurezzaEnabled = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
				this.isSicurezzaMessaggioCorniceSicurezzaEnabled = defaultValue;
			}
		}

		return this.isSicurezzaMessaggioCorniceSicurezzaEnabled;
	}
	
	private String sicurezzaMessaggioCorniceSicurezzaRestCodiceEnte= null;
	public String getSicurezzaMessaggioCorniceSicurezzaRestCodiceEnte() throws ProtocolException{
    	if(this.sicurezzaMessaggioCorniceSicurezzaRestCodiceEnte==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza.rest.codice_ente";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggioCorniceSicurezzaRestCodiceEnte = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(propertyName, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.sicurezzaMessaggioCorniceSicurezzaRestCodiceEnte;
	}
	
	private String sicurezzaMessaggioCorniceSicurezzaRestUser= null;
	public String getSicurezzaMessaggioCorniceSicurezzaRestUser() throws ProtocolException{
    	if(this.sicurezzaMessaggioCorniceSicurezzaRestUser==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza.rest.user";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggioCorniceSicurezzaRestUser = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(propertyName, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.sicurezzaMessaggioCorniceSicurezzaRestUser;
	}
	
	private String sicurezzaMessaggioCorniceSicurezzaRestIpuser= null;
	public String getSicurezzaMessaggioCorniceSicurezzaRestIpuser() throws ProtocolException{
    	if(this.sicurezzaMessaggioCorniceSicurezzaRestIpuser==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza.rest.ipuser";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggioCorniceSicurezzaRestIpuser = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(propertyName, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.sicurezzaMessaggioCorniceSicurezzaRestIpuser;
	}
	
	private String sicurezzaMessaggioCorniceSicurezzaSoapCodiceEnte= null;
	private Boolean sicurezzaMessaggioCorniceSicurezzaSoapCodiceEnteReaded= null;
	public String getSicurezzaMessaggioCorniceSicurezzaSoapCodiceEnte() throws ProtocolException{
    	if(this.sicurezzaMessaggioCorniceSicurezzaSoapCodiceEnteReaded==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza.soap.codice_ente";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggioCorniceSicurezzaSoapCodiceEnte = value;
				}
				// In soap il codice utente viene inserito anche in saml2:Subject
/**				else {
//					throw newProtocolExceptionPropertyNonDefinita();
//				}*/
				
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(propertyName, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    		
    		this.sicurezzaMessaggioCorniceSicurezzaSoapCodiceEnteReaded = true;
    	}
    	
    	return this.sicurezzaMessaggioCorniceSicurezzaSoapCodiceEnte;
	}
	
	private String sicurezzaMessaggioCorniceSicurezzaSoapUser= null;
	public String getSicurezzaMessaggioCorniceSicurezzaSoapUser() throws ProtocolException{
    	if(this.sicurezzaMessaggioCorniceSicurezzaSoapUser==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza.soap.user";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggioCorniceSicurezzaSoapUser = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(propertyName, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.sicurezzaMessaggioCorniceSicurezzaSoapUser;
	}
	
	private String sicurezzaMessaggioCorniceSicurezzaSoapIpuser= null;
	public String getSicurezzaMessaggioCorniceSicurezzaSoapIpuser() throws ProtocolException{
    	if(this.sicurezzaMessaggioCorniceSicurezzaSoapIpuser==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza.soap.ipuser";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 
				
				if (value != null){
					value = value.trim();
					this.sicurezzaMessaggioCorniceSicurezzaSoapIpuser = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(propertyName, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.sicurezzaMessaggioCorniceSicurezzaSoapIpuser;
	}
	
	private List<String> sicurezzaMessaggioCorniceSicurezzaDynamicCodiceEnte= null;
	public List<String> getSicurezzaMessaggioCorniceSicurezzaDynamicCodiceEnte() throws ProtocolException{
    	if(this.sicurezzaMessaggioCorniceSicurezzaDynamicCodiceEnte==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza.codice_ente";
    		try{  
				/**String value = this.reader.getValue_convertEnvProperties(propertyName);*/
    			String value = this.reader.getValue(propertyName); // contiene ${} da non risolvere
				this.sicurezzaMessaggioCorniceSicurezzaDynamicCodiceEnte = ModISecurityConfig.convertToList(value);
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(propertyName, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.sicurezzaMessaggioCorniceSicurezzaDynamicCodiceEnte;
	}
	
	private List<String> sicurezzaMessaggioCorniceSicurezzaDynamicUser= null;
	public List<String> getSicurezzaMessaggioCorniceSicurezzaDynamicUser() throws ProtocolException{
    	if(this.sicurezzaMessaggioCorniceSicurezzaDynamicUser==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza.user";
    		try{  
    			/**String value = this.reader.getValue_convertEnvProperties(propertyName);*/
    			String value = this.reader.getValue(propertyName); // contiene ${} da non risolvere
				this.sicurezzaMessaggioCorniceSicurezzaDynamicUser = ModISecurityConfig.convertToList(value);
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(propertyName, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.sicurezzaMessaggioCorniceSicurezzaDynamicUser;
	}
	
	private List<String> sicurezzaMessaggioCorniceSicurezzaDynamicIpuser= null;
	public List<String> getSicurezzaMessaggioCorniceSicurezzaDynamicIpuser() throws ProtocolException{
    	if(this.sicurezzaMessaggioCorniceSicurezzaDynamicIpuser==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.corniceSicurezza.ipuser";
    		try{  
    			/**String value = this.reader.getValue_convertEnvProperties(propertyName);*/
    			String value = this.reader.getValue(propertyName); // contiene ${} da non risolvere
				this.sicurezzaMessaggioCorniceSicurezzaDynamicIpuser = ModISecurityConfig.convertToList(value);
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(propertyName, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.sicurezzaMessaggioCorniceSicurezzaDynamicIpuser;
	}
	
	
	
	
	
	private List<ModIAuditConfig> auditConfig = null;
	public List<ModIAuditConfig> getAuditConfig() throws ProtocolException{
    	if(this.auditConfig==null){
    		String pName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.audit.pattern";
        	try{  
				String value = this.reader.getValueConvertEnvProperties(pName); 
				
				if (value != null){
					value = value.trim();
					
					this.auditConfig= new ArrayList<>();
					
					readAuditConf(value);
					
				}

			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonCorretta(pName, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.auditConfig;
	}
	private void readAuditConf(String value) throws UtilsException, ProtocolException {
		String [] tmp = value.split(",");
		if(tmp!=null && tmp.length>0) {
			for (String auditConf : tmp) {
				auditConf = auditConf.trim();
				
				String debugPrefix = "Pattern audit '"+auditConf+"'";
				
				String propertyPrefix = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.audit.pattern."+auditConf;
				Properties p = this.reader.readProperties(propertyPrefix+"."); // non devo convertire le properties poiche' possoono contenere ${ che useremo per la risoluzione dinamica
				if(p==null || p.isEmpty()) {
					throw new ProtocolException(debugPrefix+SUFFIX_NON_TROVATA);
				}
				ModIAuditConfig config = new ModIAuditConfig(propertyPrefix, propertyPrefix, p);
				this.auditConfig.add(config);
			}
		}
	}
	
	private String getSecurityTokenHeaderAudit= null;
	public String getSecurityTokenHeaderModIAudit() throws ProtocolException{
    	if(this.getSecurityTokenHeaderAudit==null){
	    	String name = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.audit.securityToken.header";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSecurityTokenHeaderAudit = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getSecurityTokenHeaderAudit;
	}
	
    private Boolean isSecurityTokenAuditX509AddKid = null;
	public boolean isSecurityTokenAuditX509AddKid(){
		if(this.isSecurityTokenAuditX509AddKid==null){
			
			boolean defaultValue = false;
			String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.audit.x509.kid";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isSecurityTokenAuditX509AddKid = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.isSecurityTokenAuditX509AddKid = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
				this.isSecurityTokenAuditX509AddKid = defaultValue;
			}
		}

		return this.isSecurityTokenAuditX509AddKid;
	}
	
    private Boolean isSecurityTokenAuditApiSoapX509RiferimentoX5c = null;
	public boolean isSecurityTokenAuditApiSoapX509RiferimentoX5c(){
		if(this.isSecurityTokenAuditApiSoapX509RiferimentoX5c==null){
			
			boolean defaultValue = true;
			String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.audit.soap.x509.x5c";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isSecurityTokenAuditApiSoapX509RiferimentoX5c = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.isSecurityTokenAuditApiSoapX509RiferimentoX5c = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
				this.isSecurityTokenAuditApiSoapX509RiferimentoX5c = defaultValue;
			}
		}

		return this.isSecurityTokenAuditApiSoapX509RiferimentoX5c;
	}
	
    private Boolean isSecurityTokenAuditApiSoapX509RiferimentoX5cSingleCertificate = null;
	public boolean isSecurityTokenAuditApiSoapX509RiferimentoX5cSingleCertificate(){
		if(this.isSecurityTokenAuditApiSoapX509RiferimentoX5cSingleCertificate==null){
			
			boolean defaultValue = true;
			String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.audit.soap.x509.x5c.singleCertificate";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isSecurityTokenAuditApiSoapX509RiferimentoX5cSingleCertificate = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.isSecurityTokenAuditApiSoapX509RiferimentoX5cSingleCertificate = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
				this.isSecurityTokenAuditApiSoapX509RiferimentoX5cSingleCertificate = defaultValue;
			}
		}

		return this.isSecurityTokenAuditApiSoapX509RiferimentoX5cSingleCertificate;
	}
	
    private Boolean isSecurityTokenAuditApiSoapX509RiferimentoX5u = null;
	public boolean isSecurityTokenAuditApiSoapX509RiferimentoX5u(){
		if(this.isSecurityTokenAuditApiSoapX509RiferimentoX5u==null){
			
			boolean defaultValue = false;
			String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.audit.soap.x509.x5u";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isSecurityTokenAuditApiSoapX509RiferimentoX5u = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.isSecurityTokenAuditApiSoapX509RiferimentoX5u = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
				this.isSecurityTokenAuditApiSoapX509RiferimentoX5u = defaultValue;
			}
		}

		return this.isSecurityTokenAuditApiSoapX509RiferimentoX5u;
	}
	
    private Boolean isSecurityTokenAuditApiSoapX509RiferimentoX5t = null;
	public boolean isSecurityTokenAuditApiSoapX509RiferimentoX5t(){
		if(this.isSecurityTokenAuditApiSoapX509RiferimentoX5t==null){
			
			boolean defaultValue = false;
			String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.audit.soap.x509.x5t";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isSecurityTokenAuditApiSoapX509RiferimentoX5t = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.isSecurityTokenAuditApiSoapX509RiferimentoX5t = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
				this.isSecurityTokenAuditApiSoapX509RiferimentoX5t = defaultValue;
			}
		}

		return this.isSecurityTokenAuditApiSoapX509RiferimentoX5t;
	}
	
	private Boolean getSecurityTokenAuditProcessArrayModeReaded= null;
	private Boolean getSecurityTokenAuditProcessArrayModeEnabled= null;
	public boolean isSecurityTokenAuditProcessArrayModeEnabled() throws ProtocolException{
    	if(this.getSecurityTokenAuditProcessArrayModeReaded==null){
	    	String name = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.audit.audience.processArrayMode";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSecurityTokenAuditProcessArrayModeEnabled = Boolean.valueOf(value);
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getSecurityTokenAuditProcessArrayModeReaded = true;
    	}
    	
    	return this.getSecurityTokenAuditProcessArrayModeEnabled;
	}
	
    private Boolean isSecurityTokenAuditAddPurposeId = null;
	public boolean isSecurityTokenAuditAddPurposeId(){
		if(this.isSecurityTokenAuditAddPurposeId==null){
			
			boolean defaultValue = true;
			String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.audit.addPurposeId";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isSecurityTokenAuditAddPurposeId = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.isSecurityTokenAuditAddPurposeId = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
				this.isSecurityTokenAuditAddPurposeId = defaultValue;
			}
		}

		return this.isSecurityTokenAuditAddPurposeId;
	}
	
    private Boolean isSecurityTokenAuditExpectedPurposeId = null;
	public boolean isSecurityTokenAuditExpectedPurposeId(){
		if(this.isSecurityTokenAuditExpectedPurposeId==null){
			
			boolean defaultValue = true;
			String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.audit.expectedPurposeId";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isSecurityTokenAuditExpectedPurposeId = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.isSecurityTokenAuditExpectedPurposeId = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
				this.isSecurityTokenAuditExpectedPurposeId = defaultValue;
			}
		}

		return this.isSecurityTokenAuditExpectedPurposeId;
	}
	
	private Boolean isSecurityTokenAuditCompareAuthorizationPurposeId = null;
	public boolean isSecurityTokenAuditCompareAuthorizationPurposeId(){
		if(this.isSecurityTokenAuditCompareAuthorizationPurposeId==null){
			
			boolean defaultValue = true;
			String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.audit.compareAuthorizationPurposeId";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isSecurityTokenAuditCompareAuthorizationPurposeId = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.isSecurityTokenAuditCompareAuthorizationPurposeId = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
				this.isSecurityTokenAuditCompareAuthorizationPurposeId = defaultValue;
			}
		}

		return this.isSecurityTokenAuditCompareAuthorizationPurposeId;
	}
	
    private Integer getSecurityTokenAuditDnonceSize = null;
	public int getSecurityTokenAuditDnonceSize(){
		if(this.getSecurityTokenAuditDnonceSize==null){
			
			int defaultValue = 13;
			String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.audit.dnonce.size";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.getSecurityTokenAuditDnonceSize = Integer.valueOf(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.getSecurityTokenAuditDnonceSize = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
				this.getSecurityTokenAuditDnonceSize = defaultValue;
			}
		}

		return this.getSecurityTokenAuditDnonceSize;
	}
	
    private String getSecurityTokenAuditDigestAlgorithm = null;
	public String getSecurityTokenAuditDigestAlgorithm(){
		if(this.getSecurityTokenAuditDigestAlgorithm==null){
			
			String defaultValue = Costanti.PDND_DIGEST_ALG_DEFAULT_VALUE;
			String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.audit.digest.algo";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.getSecurityTokenAuditDigestAlgorithm = value;
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.getSecurityTokenAuditDigestAlgorithm = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
				this.getSecurityTokenAuditDigestAlgorithm = defaultValue;
			}
		}

		return this.getSecurityTokenAuditDigestAlgorithm;
	}
	
	
	
	 /* **** CACHE **** */ 
	
	private Boolean isTokenAuthCacheable = null;
	public Boolean isTokenAuthCacheable(){
		if(this.isTokenAuthCacheable==null){
			
			Boolean defaultValue = true;
			String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.auth.cacheable";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isTokenAuthCacheable = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.isTokenAuthCacheable = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
				this.isTokenAuthCacheable = defaultValue;
			}
		}

		return this.isTokenAuthCacheable;
	}
	
	private Boolean isTokenAuditCacheable = null;
	public Boolean isTokenAuditCacheable(){
		if(this.isTokenAuditCacheable==null){
			
			Boolean defaultValue = true;
			String propertyName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.audit.cacheable";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isTokenAuditCacheable = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.isTokenAuditCacheable = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
				this.isTokenAuditCacheable = defaultValue;
			}
		}

		return this.isTokenAuditCacheable;
	}
	
	private Integer isGestioneTokenCacheableRefreshTokenBeforeExpirePercent = null;
	private Boolean isGestioneTokenCacheableRefreshTokenBeforeExpirePercentRead = null;
	private String isGestioneTokenCacheableRefreshTokenBeforeExpirePercentPName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.cache.refreshTokenBeforeExpire.percent";
	public Integer getGestioneRetrieveTokenRefreshTokenBeforeExpirePercent(){

		if(this.isGestioneTokenCacheableRefreshTokenBeforeExpirePercentRead==null){
			try{  
				String value = this.reader.getValueConvertEnvProperties(this.isGestioneTokenCacheableRefreshTokenBeforeExpirePercentPName); 

				if (value != null){
					value = value.trim();
					this.isGestioneTokenCacheableRefreshTokenBeforeExpirePercent = Integer.parseInt(value);
				}
				
				this.isGestioneTokenCacheableRefreshTokenBeforeExpirePercentRead=true;

			}catch(java.lang.Exception e) {
				this.logError("Proprieta' di openspcoop '"+this.isGestioneTokenCacheableRefreshTokenBeforeExpirePercentPName+"' non impostata, errore:"+e.getMessage(),e);
				this.isGestioneTokenCacheableRefreshTokenBeforeExpirePercent = null;
			}
		}

		return this.isGestioneTokenCacheableRefreshTokenBeforeExpirePercent;
	}
	
	private Integer sGestioneTokenCacheableRefreshTokenBeforeExpireSeconds = null;
	private Boolean sGestioneTokenCacheableRefreshTokenBeforeExpireSecondsRead = null;
	private String sGestioneTokenCacheableRefreshTokenBeforeExpireSecondsPName = "org.openspcoop2.protocol.modipa.sicurezzaMessaggio.cache.refreshTokenBeforeExpire.seconds";
	public Integer getGestioneRetrieveTokenRefreshTokenBeforeExpireSeconds(){

		if(this.sGestioneTokenCacheableRefreshTokenBeforeExpireSecondsRead==null){
			try{  
				String value = this.reader.getValueConvertEnvProperties(this.sGestioneTokenCacheableRefreshTokenBeforeExpireSecondsPName); 

				if (value != null){
					value = value.trim();
					this.sGestioneTokenCacheableRefreshTokenBeforeExpireSeconds = Integer.parseInt(value);
				}
				
				this.sGestioneTokenCacheableRefreshTokenBeforeExpireSecondsRead=true;

			}catch(java.lang.Exception e) {
				this.logError("Proprieta' di openspcoop '"+this.sGestioneTokenCacheableRefreshTokenBeforeExpireSecondsPName+"' non impostata, errore:"+e.getMessage(),e);
				this.sGestioneTokenCacheableRefreshTokenBeforeExpireSeconds = null;
			}
		}

		return this.sGestioneTokenCacheableRefreshTokenBeforeExpireSeconds;
	}
	
	
	
    /* **** TRACCE **** */ 
    
    private Boolean isGenerazioneTracce = null;
	public Boolean isGenerazioneTracce(){
		if(this.isGenerazioneTracce==null){
			
			Boolean defaultValue = false;
			String propertyName = "org.openspcoop2.protocol.modipa.generazioneTracce.enabled";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isGenerazioneTracce = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.isGenerazioneTracce = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
				this.isGenerazioneTracce = defaultValue;
			}
		}

		return this.isGenerazioneTracce;
	}
	
    private Boolean isGenerazioneTracceRegistraToken = null;
	public Boolean isGenerazioneTracceRegistraToken(){
		if(this.isGenerazioneTracceRegistraToken==null){
			
			Boolean defaultValue = false;
			String propertyName = "org.openspcoop2.protocol.modipa.generazioneTracce.registrazioneToken.enabled";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isGenerazioneTracceRegistraToken = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.isGenerazioneTracceRegistraToken = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
				this.isGenerazioneTracceRegistraToken = defaultValue;
			}
		}

		return this.isGenerazioneTracceRegistraToken;
	}
	
    private Boolean isGenerazioneTracceRegistraCustomClaims = null;
	public boolean isGenerazioneTracceRegistraCustomClaims(){
		if(this.isGenerazioneTracceRegistraCustomClaims==null){
			
			Boolean defaultValue = false;
			String propertyName = "org.openspcoop2.protocol.modipa.generazioneTracce.registrazioneCustomClaims.enabled";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isGenerazioneTracceRegistraCustomClaims = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.isGenerazioneTracceRegistraCustomClaims = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
				this.isGenerazioneTracceRegistraCustomClaims = defaultValue;
			}
		}

		return this.isGenerazioneTracceRegistraCustomClaims;
	}
	
	private List<String> getGenerazioneTracceRegistraCustomClaimsBlackList= null;
	public List<String> getGenerazioneTracceRegistraCustomClaimsBlackList() throws ProtocolException{
    	if(this.getGenerazioneTracceRegistraCustomClaimsBlackList==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.generazioneTracce.registrazioneCustomClaims.blackList";
    		try{  
    			String value = this.reader.getValueConvertEnvProperties(propertyName); 
    			if(value!=null && StringUtils.isNotEmpty(value)) {
    				this.getGenerazioneTracceRegistraCustomClaimsBlackList = ModISecurityConfig.convertToList(value);
    			}
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(propertyName, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.getGenerazioneTracceRegistraCustomClaimsBlackList;
	}
	
	
	
	
    /* **** Nomenclatura **** */ 
    
    private Boolean isModIVersioneBozza = null;
	public Boolean isModIVersioneBozza(){
		if(this.isModIVersioneBozza==null){
			
			Boolean defaultValue = false;
			String propertyName = "org.openspcoop2.protocol.modipa.usaVersioneBozza";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isModIVersioneBozza = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.isModIVersioneBozza = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
				this.isModIVersioneBozza = defaultValue;
			}
		}

		return this.isModIVersioneBozza;
	}
	
	
	
	
	/* **** REST **** */ 
	
	private String getRestSecurityTokenHeader= null;
	// riferito in org.openspcoop2.protocol.utils.ModIUtils
	// non modificare il nome
	public String getRestSecurityTokenHeaderModI() throws ProtocolException{
    	if(this.getRestSecurityTokenHeader==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.header";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenHeader = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getRestSecurityTokenHeader;
	}
	
    private Boolean isSecurityTokenX509AddKid = null;
	public boolean isSecurityTokenX509AddKid(){
		if(this.isSecurityTokenX509AddKid==null){
			
			boolean defaultValue = false;
			String propertyName = "org.openspcoop2.protocol.modipa.rest.securityToken.x509.kid";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isSecurityTokenX509AddKid = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.isSecurityTokenX509AddKid = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
				this.isSecurityTokenX509AddKid = defaultValue;
			}
		}

		return this.isSecurityTokenX509AddKid;
	}
	
    private Boolean isSecurityTokenIntegrity01AddPurposeId = null;
	public boolean isSecurityTokenIntegrity01AddPurposeId(){
		if(this.isSecurityTokenIntegrity01AddPurposeId==null){
			
			boolean defaultValue = false;
			String propertyName = "org.openspcoop2.protocol.modipa.rest.securityToken.integrity_01.addPurposeId";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isSecurityTokenIntegrity01AddPurposeId = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.isSecurityTokenIntegrity01AddPurposeId = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
				this.isSecurityTokenIntegrity01AddPurposeId = defaultValue;
			}
		}

		return this.isSecurityTokenIntegrity01AddPurposeId;
	}
	
    private Boolean isSecurityTokenIntegrity02AddPurposeId = null;
	public boolean isSecurityTokenIntegrity02AddPurposeId(){
		if(this.isSecurityTokenIntegrity02AddPurposeId==null){
			
			boolean defaultValue = false;
			String propertyName = "org.openspcoop2.protocol.modipa.rest.securityToken.integrity_02.addPurposeId";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isSecurityTokenIntegrity02AddPurposeId = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.isSecurityTokenIntegrity02AddPurposeId = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
				this.isSecurityTokenIntegrity02AddPurposeId = defaultValue;
			}
		}

		return this.isSecurityTokenIntegrity02AddPurposeId;
	}
	
	private Boolean getRestSecurityTokenClaimsIssuerEnabledReaded= null;
	private Boolean getRestSecurityTokenClaimsIssuerEnabled= null;
	public boolean isRestSecurityTokenClaimsIssuerEnabled() throws ProtocolException{
    	if(this.getRestSecurityTokenClaimsIssuerEnabledReaded==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.iss.enabled";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenClaimsIssuerEnabled = Boolean.valueOf(value);
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getRestSecurityTokenClaimsIssuerEnabledReaded = true;
    	}
    	
    	return this.getRestSecurityTokenClaimsIssuerEnabled;
	}
	private Boolean getRestSecurityTokenClaimsIssuerHeaderValueReaded= null;
	private String getRestSecurityTokenClaimsIssuerHeaderValue= null;
	public String getRestSecurityTokenClaimsIssuerHeaderValue() throws ProtocolException{
    	if(this.getRestSecurityTokenClaimsIssuerHeaderValueReaded==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.iss";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenClaimsIssuerHeaderValue = value;
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getRestSecurityTokenClaimsIssuerHeaderValueReaded = true;
    	}
    	
    	return this.getRestSecurityTokenClaimsIssuerHeaderValue;
	}
	
	private Boolean getRestSecurityTokenClaimsSubjectEnabledReaded= null;
	private Boolean getRestSecurityTokenClaimsSubjectEnabled= null;
	public boolean isRestSecurityTokenClaimsSubjectEnabled() throws ProtocolException{
    	if(this.getRestSecurityTokenClaimsSubjectEnabledReaded==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.sub.enabled";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenClaimsSubjectEnabled = Boolean.valueOf(value);
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getRestSecurityTokenClaimsSubjectEnabledReaded = true;
    	}
    	
    	return this.getRestSecurityTokenClaimsSubjectEnabled;
	}
	private Boolean getRestSecurityTokenClaimsSubjectHeaderValueReaded= null;
	private String getRestSecurityTokenClaimsSubjectHeaderValue= null;
	public String getRestSecurityTokenClaimsSubjectHeaderValue() throws ProtocolException{
    	if(this.getRestSecurityTokenClaimsSubjectHeaderValueReaded==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.sub";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenClaimsSubjectHeaderValue = value;
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getRestSecurityTokenClaimsSubjectHeaderValueReaded = true;
    	}
    	
    	return this.getRestSecurityTokenClaimsSubjectHeaderValue;
	}
	
	private String getRestSecurityTokenClaimsClientIdHeader= null;
	public String getRestSecurityTokenClaimsClientIdHeader() throws ProtocolException{
    	if(this.getRestSecurityTokenClaimsClientIdHeader==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.client_id";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenClaimsClientIdHeader = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getRestSecurityTokenClaimsClientIdHeader;
	}
	
	private String getRestSecurityTokenClaimSignedHeaders= null;
	public String getRestSecurityTokenClaimSignedHeaders() throws ProtocolException{
    	if(this.getRestSecurityTokenClaimSignedHeaders==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.signedHeaders";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenClaimSignedHeaders = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getRestSecurityTokenClaimSignedHeaders;
	}
	
	
	private String getRestSecurityTokenClaimRequestDigest= null;
	public String getRestSecurityTokenClaimRequestDigest() throws ProtocolException{
    	if(this.getRestSecurityTokenClaimRequestDigest==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.requestDigest";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenClaimRequestDigest = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getRestSecurityTokenClaimRequestDigest;
	}
	
	
	private String [] getRestSecurityTokenSignedHeaders = null;
	public String [] getRestSecurityTokenSignedHeaders() throws ProtocolException{
    	if(this.getRestSecurityTokenSignedHeaders==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.signedHeaders";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					this.getRestSecurityTokenSignedHeaders = new String[tmp.length];
					for (int i = 0; i < tmp.length; i++) {
						this.getRestSecurityTokenSignedHeaders[i] = tmp[i].trim();
					}
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getRestSecurityTokenSignedHeaders;
	}
	public String  getRestSecurityTokenSignedHeadersAsString() {
		StringBuilder bf = new StringBuilder();
		for (String hdr : this.getRestSecurityTokenSignedHeaders) {
			if(bf.length()>0) {
				bf.append(",");
			}
			bf.append(hdr);
		}
		return bf.toString();
	}
	
	private Boolean getRestSecurityTokenClaimsIatTimeCheckFutureToleranceMillisecondsReaded = null;
	private Long getRestSecurityTokenClaimsIatTimeCheckFutureToleranceMilliseconds = null;
	public Long getRestSecurityTokenClaimsIatTimeCheckFutureToleranceMilliseconds() throws ProtocolException{

		if(this.getRestSecurityTokenClaimsIatTimeCheckFutureToleranceMillisecondsReaded==null){
			
			String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.iat.future.toleranceMilliseconds";
			try{  
				String value = this.reader.getValueConvertEnvProperties(name); 

				if (value != null){
					value = value.trim();
					long tmp = Long.parseLong(value);
					if(tmp>0) {
						long maxLongValue = Long.MAX_VALUE;
						if(tmp>maxLongValue) {
							this.logWarn(getPrefixValoreIndicatoProprieta(value,name)+getSuffixSuperioreMassimoConsentitoControlloDisabilitato(maxLongValue));
						}
						else {
							this.getRestSecurityTokenClaimsIatTimeCheckFutureToleranceMilliseconds = tmp;
						}
					}
					else {
						this.logWarn(getMessaggioVerificaDisabilitata(name));
					}
				}
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(name, e),e);
				throw new ProtocolException(e.getMessage(),e);
			}
			
			this.getRestSecurityTokenClaimsIatTimeCheckFutureToleranceMillisecondsReaded = true;
		}

		return this.getRestSecurityTokenClaimsIatTimeCheckFutureToleranceMilliseconds;
	}
	
	private Boolean getRestSecurityTokenClaimsIatTimeCheckMillisecondsReaded = null;
	private Long getRestSecurityTokenClaimsIatTimeCheckMilliseconds = null;
	public Long getRestSecurityTokenClaimsIatTimeCheckMilliseconds() throws ProtocolException{

		if(this.getRestSecurityTokenClaimsIatTimeCheckMillisecondsReaded==null){
			
			String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.iat.minutes";
			try{  
				String value = this.reader.getValueConvertEnvProperties(name); 

				if (value != null){
					value = value.trim();
					long tmp = Long.parseLong(value); // minuti
					if(tmp>0) {
						long maxLongValue = ((Long.MAX_VALUE)/60000l);
						if(tmp>maxLongValue) {
							this.logWarn(getPrefixValoreIndicatoProprieta(value,name)+getSuffixSuperioreMassimoConsentitoControlloDisabilitato(maxLongValue));
						}
						else {
							this.getRestSecurityTokenClaimsIatTimeCheckMilliseconds = tmp * 60 * 1000;
						}
					}
					else {
						this.logWarn(getMessaggioVerificaDisabilitata(name));
					}
				}
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(name, e),e);
				throw new ProtocolException(e.getMessage(),e);
			}
			
			this.getRestSecurityTokenClaimsIatTimeCheckMillisecondsReaded = true;
		}

		return this.getRestSecurityTokenClaimsIatTimeCheckMilliseconds;
	}
	
	private Boolean isRestSecurityTokenClaimsExpTimeCheck= null;
	public boolean isRestSecurityTokenClaimsExpTimeCheck() throws ProtocolException{
    	if(this.isRestSecurityTokenClaimsExpTimeCheck==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.exp.checkEnabled";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.isRestSecurityTokenClaimsExpTimeCheck = Boolean.valueOf(value);
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    	}
    	
    	return this.isRestSecurityTokenClaimsExpTimeCheck;
	}	
	
	private Boolean getRestSecurityTokenClaimsExpTimeCheckToleranceMillisecondsReaded = null;
	private Long getRestSecurityTokenClaimsExpTimeCheckToleranceMilliseconds = null;
	public Long getRestSecurityTokenClaimsExpTimeCheckToleranceMilliseconds() throws ProtocolException{

		if(this.getRestSecurityTokenClaimsExpTimeCheckToleranceMillisecondsReaded==null){
			
			String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.exp.toleranceMilliseconds";
			try{  
				String value = this.reader.getValueConvertEnvProperties(name); 

				if (value != null){
					value = value.trim();
					long tmp = Long.parseLong(value); // già in millisecondi
					if(tmp>0) {
						long maxLongValue = Long.MAX_VALUE;
						if(tmp>maxLongValue) {
							this.logWarn(getPrefixValoreIndicatoProprieta(value,name)+getSuffixSuperioreMassimoConsentitoControlloDisabilitato(maxLongValue));
						}
						else {
							this.getRestSecurityTokenClaimsExpTimeCheckToleranceMilliseconds = tmp;
						}
					}
					else {
						this.logWarn(getMessaggioVerificaDisabilitata(name));
					}
				}
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(name, e),e);
				throw new ProtocolException(e.getMessage(),e);
			}
			
			this.getRestSecurityTokenClaimsExpTimeCheckToleranceMillisecondsReaded = true;
		}

		return this.getRestSecurityTokenClaimsExpTimeCheckToleranceMilliseconds;
	}
	
	private Boolean getRestSecurityTokenClaimsNbfTimeCheckToleranceMillisecondsReaded = null;
	private Long getRestSecurityTokenClaimsNbfTimeCheckToleranceMilliseconds = null;
	public Long getRestSecurityTokenClaimsNbfTimeCheckToleranceMilliseconds() throws ProtocolException{

		if(this.getRestSecurityTokenClaimsNbfTimeCheckToleranceMillisecondsReaded==null){
			
			String name = "org.openspcoop2.protocol.modipa.rest.securityToken.claims.nbf.toleranceMilliseconds";
			try{  
				String value = this.reader.getValueConvertEnvProperties(name); 

				if (value != null){
					value = value.trim();
					long tmp = Long.parseLong(value); // già in millisecondi
					if(tmp>0) {
						long maxLongValue = Long.MAX_VALUE;
						if(tmp>maxLongValue) {
							this.logWarn(getPrefixValoreIndicatoProprieta(value,name)+getSuffixSuperioreMassimoConsentitoControlloDisabilitato(maxLongValue));
						}
						else {
							this.getRestSecurityTokenClaimsNbfTimeCheckToleranceMilliseconds = tmp;
						}
					}
					else {
						this.logWarn(getMessaggioVerificaDisabilitata(name));
					}
				}
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(name, e),e);
				throw new ProtocolException(e.getMessage(),e);
			}
			
			this.getRestSecurityTokenClaimsNbfTimeCheckToleranceMillisecondsReaded = true;
		}

		return this.getRestSecurityTokenClaimsNbfTimeCheckToleranceMilliseconds;
	}

	private DigestEncoding getRestSecurityTokenDigestDefaultEncoding= null;
	public DigestEncoding getRestSecurityTokenDigestDefaultEncoding() throws ProtocolException{
    	if(this.getRestSecurityTokenDigestDefaultEncoding==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.digest.encoding";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenDigestDefaultEncoding = DigestEncoding.valueOf(value.toUpperCase());
					if(this.getRestSecurityTokenDigestDefaultEncoding==null) {
						throw new ProtocolException(INVALID_VALUE);
					}
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getPrefixProprieta(name)+" non impostata, errore (valori ammessi: "+DigestEncoding.BASE64.name().toLowerCase()+","+DigestEncoding.HEX.name().toLowerCase()+"):"+e.getMessage(); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getRestSecurityTokenDigestDefaultEncoding;
	}
	
	private Boolean isRestSecurityTokenDigestEncodingChoice= null;
	public boolean isRestSecurityTokenDigestEncodingChoice() throws ProtocolException{
    	if(this.isRestSecurityTokenDigestEncodingChoice==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.digest.encoding.choice";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.isRestSecurityTokenDigestEncodingChoice = Boolean.valueOf(value);
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.isRestSecurityTokenDigestEncodingChoice;
	}
	
	private List<DigestEncoding> getRestSecurityTokenDigestEncodingAccepted= null;
	public List<DigestEncoding> getRestSecurityTokenDigestEncodingAccepted() throws ProtocolException{
    	if(this.getRestSecurityTokenDigestEncodingAccepted==null){
    		String name = "org.openspcoop2.protocol.modipa.rest.securityToken.digest.encoding.accepted";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					
					this.getRestSecurityTokenDigestEncodingAccepted = new ArrayList<>();
					if(value.contains(",")) {
						readRestSecurityTokenDigestEncodingAcceptedSplitValue(value);
					}
					else {
						DigestEncoding tmp = DigestEncoding.valueOf(value.toUpperCase());
						if(tmp==null) {
							throw new ProtocolException(INVALID_VALUE);
						}
						this.getRestSecurityTokenDigestEncodingAccepted.add(tmp);
					}
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getPrefixProprieta(name)+" non impostata, errore (valori ammessi: "+DigestEncoding.BASE64.name().toLowerCase()+","+DigestEncoding.HEX.name().toLowerCase()+"):"+e.getMessage(); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getRestSecurityTokenDigestEncodingAccepted;
	}
	private void readRestSecurityTokenDigestEncodingAcceptedSplitValue(String value) throws ProtocolException {
		String [] split = value.split(",");
		if(split==null || split.length<=0) {
			throw new ProtocolException("Empty value");
		}
		for (String s : split) {
			if(s==null) {
				throw new ProtocolException("Null value");
			}
			else {
				s = s.trim();
			}
			DigestEncoding tmp = DigestEncoding.valueOf(s.toUpperCase());
			if(tmp==null) {
				throw new ProtocolException(INVALID_VALUE);
			}
			this.getRestSecurityTokenDigestEncodingAccepted.add(tmp);
		}
	}
	
	private Boolean getRestSecurityTokenRequestDigestCleanReaded= null;
	private Boolean getRestSecurityTokenRequestDigestClean= null;
	public boolean isRestSecurityTokenRequestDigestClean() throws ProtocolException{
    	if(this.getRestSecurityTokenRequestDigestCleanReaded==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.request.digest.clean";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenRequestDigestClean = Boolean.valueOf(value);
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getRestSecurityTokenRequestDigestCleanReaded = true;
    	}
    	
    	return this.getRestSecurityTokenRequestDigestClean;
	}
	
	private Boolean getRestSecurityTokenResponseDigestCleanReaded= null;
	private Boolean getRestSecurityTokenResponseDigestClean= null;
	public boolean isRestSecurityTokenResponseDigestClean() throws ProtocolException{
    	if(this.getRestSecurityTokenResponseDigestCleanReaded==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.response.digest.clean";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenResponseDigestClean = Boolean.valueOf(value);
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getRestSecurityTokenResponseDigestCleanReaded = true;
    	}
    	
    	return this.getRestSecurityTokenResponseDigestClean;
	}
	
	private Boolean getRestSecurityTokenResponseDigestHEADuseServerHeaderReaded= null;
	private Boolean getRestSecurityTokenResponseDigestHEADuseServerHeader= null;
	public boolean isRestSecurityTokenResponseDigestHEADuseServerHeader() throws ProtocolException{
    	if(this.getRestSecurityTokenResponseDigestHEADuseServerHeaderReaded==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.response.digest.HEAD.useServerHeader";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenResponseDigestHEADuseServerHeader = Boolean.valueOf(value);
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getRestSecurityTokenResponseDigestHEADuseServerHeaderReaded = true;
    	}
    	
    	return this.getRestSecurityTokenResponseDigestHEADuseServerHeader;
	}
	
	private Boolean getRestSecurityTokenFaultProcessEnabledReaded= null;
	private Boolean getRestSecurityTokenFaultProcessEnabled= null;
	public boolean isRestSecurityTokenFaultProcessEnabled() throws ProtocolException{
    	if(this.getRestSecurityTokenFaultProcessEnabledReaded==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.fault.securityToken";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenFaultProcessEnabled = Boolean.valueOf(value);
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getRestSecurityTokenFaultProcessEnabledReaded = true;
    	}
    	
    	return this.getRestSecurityTokenFaultProcessEnabled;
	}
	
	private Boolean getRestSecurityTokenAudienceProcessArrayModeReaded= null;
	private Boolean getRestSecurityTokenAudienceProcessArrayModeEnabled= null;
	public boolean isRestSecurityTokenAudienceProcessArrayModeEnabled() throws ProtocolException{
    	if(this.getRestSecurityTokenAudienceProcessArrayModeReaded==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.securityToken.audience.processArrayMode";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenAudienceProcessArrayModeEnabled = Boolean.valueOf(value);
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getRestSecurityTokenAudienceProcessArrayModeReaded = true;
    	}
    	
    	return this.getRestSecurityTokenAudienceProcessArrayModeEnabled;
	}
	
	
	private Boolean getRestResponseSecurityTokenAudienceDefaultReaded= null;
	private String getRestResponseSecurityTokenAudienceDefault= null;
	public String getRestResponseSecurityTokenAudienceDefault(String soggettoMittente) throws ProtocolException{
    	if(this.getRestResponseSecurityTokenAudienceDefaultReaded==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.response.securityToken.audience.default";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				if (value != null){
					value = value.trim();
					this.getRestResponseSecurityTokenAudienceDefault = value;
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getRestResponseSecurityTokenAudienceDefaultReaded = true;
    	}
    	
    	if(ModICostanti.CONFIG_MODIPA_SOGGETTO_MITTENTE_KEYWORD.equalsIgnoreCase(this.getRestResponseSecurityTokenAudienceDefault) && soggettoMittente!=null && !StringUtils.isEmpty(soggettoMittente)) {
			return soggettoMittente;
		}
    	else {
    		return this.getRestResponseSecurityTokenAudienceDefault;
    	}
	}	
	
	public List<String> getUsedRestSecurityClaims(boolean request, boolean integrita) throws ProtocolException{
		List<String> l = new ArrayList<>();
		
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
		
		/**
		 ** Possono sempre essere definiti, poiche' utilizzati per sovrascrivere i default
		boolean addIss = true;
		boolean addSub = true;
		if(corniceSicurezza) {
			v = getSicurezzaMessaggioCorniceSicurezzaRestCodiceEnte();
			if(v!=null && StringUtils.isNotEmpty(v)) {
				if(Claims.INTROSPECTION_RESPONSE_RFC_7662_ISSUER.equals(v)) {
					addIss = false;
				}
				l.add(v);
			}
			v = getSicurezzaMessaggioCorniceSicurezzaRestUser();
			if(v!=null && StringUtils.isNotEmpty(v)) {
				if(Claims.INTROSPECTION_RESPONSE_RFC_7662_SUBJECT.equals(v)) {
					addSub = false;
				}
				l.add(v);
			}
			v = getSicurezzaMessaggioCorniceSicurezzaRestIpuser();
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
	public String getRestCorrelationIdHeader() throws ProtocolException{
    	if(this.getRestCorrelationIdHeader==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.correlationId.header";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestCorrelationIdHeader = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getRestCorrelationIdHeader;
	}	
	
	private String getRestReplyToHeader= null;
	public String getRestReplyToHeader() throws ProtocolException{
    	if(this.getRestReplyToHeader==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.replyTo.header";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestReplyToHeader = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getRestReplyToHeader;
	}
	
	private String getRestLocationHeader= null;
	public String getRestLocationHeader() throws ProtocolException{
    	if(this.getRestLocationHeader==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.location.header";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestLocationHeader = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getRestLocationHeader;
	}
	
	private Boolean getRestProfiliInterazioneCheckCompatibilityReaded= null;
	private Boolean getRestProfiliInterazioneCheckCompatibility= null;
	public boolean isRestProfiliInterazioneCheckCompatibility() throws ProtocolException{
    	if(this.getRestProfiliInterazioneCheckCompatibilityReaded==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.profiliInterazione.checkCompatibility";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestProfiliInterazioneCheckCompatibility = Boolean.valueOf(value);
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getRestProfiliInterazioneCheckCompatibilityReaded = true;
    	}
    	
    	return this.getRestProfiliInterazioneCheckCompatibility;
	}
	
	// .. BLOCCANTE ..
	
	private Integer [] getRestBloccanteHttpStatus = null;
	public Integer [] getRestBloccanteHttpStatus() throws ProtocolException{
    	if(this.getRestBloccanteHttpStatus==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.bloccante.httpStatus";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
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
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
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
    			this.getRestBloccanteHttpMethod = new ArrayList<>();
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					for (int i = 0; i < tmp.length; i++) {
						this.getRestBloccanteHttpMethod.add(HttpRequestMethod.valueOf(tmp[i].trim().toUpperCase()));
					}
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonCorretta(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getRestBloccanteHttpMethod;
	}
	
	
	// .. PUSH ..
	
	private Boolean getRestSecurityTokenPushReplyToUpdateOrCreate = null;
	public boolean isRestSecurityTokenPushReplyToUpdateOrCreateInFruizione() throws ProtocolException{
    	if(this.getRestSecurityTokenPushReplyToUpdateOrCreate==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.push.replyTo.header.updateOrCreate";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenPushReplyToUpdateOrCreate = Boolean.valueOf(value);
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getRestSecurityTokenPushReplyToUpdateOrCreate;
	}
	
	private Boolean getRestSecurityTokenPushReplyToUpdate = null;
	public boolean isRestSecurityTokenPushReplyToUpdateInErogazione() throws ProtocolException{
    	if(this.getRestSecurityTokenPushReplyToUpdate==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.push.replyTo.header.update";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenPushReplyToUpdate = Boolean.valueOf(value);
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getRestSecurityTokenPushReplyToUpdate;
	}
	
	private Boolean getRestSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists = null;
	public boolean isRestSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists() throws ProtocolException{
    	if(this.getRestSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.push.request.correlationId.header.useTransactionIdIfNotExists";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getRestSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists = Boolean.valueOf(value);
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getRestSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists;
	}
	
	private Integer [] getRestSecurityTokenPushRequestHttpStatus = null;
	public Integer [] getRestNonBloccantePushRequestHttpStatus() throws ProtocolException{
    	if(this.getRestSecurityTokenPushRequestHttpStatus==null){
	    	String name = "org.openspcoop2.protocol.modipa.rest.push.request.httpStatus";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					this.getRestSecurityTokenPushRequestHttpStatus = new Integer[tmp.length];
					for (int i = 0; i < tmp.length; i++) {
						this.getRestSecurityTokenPushRequestHttpStatus[i] = Integer.valueOf(tmp[i].trim());
					}
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
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
    			this.getRestNonBloccantePushRequestHttpMethod = new ArrayList<>();
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					for (int i = 0; i < tmp.length; i++) {
						this.getRestNonBloccantePushRequestHttpMethod.add(HttpRequestMethod.valueOf(tmp[i].trim().toUpperCase()));
					}
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonCorretta(name, e); 
				this.logError(msgErrore);
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
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					this.getRestSecurityTokenPushResponseHttpStatus = new Integer[tmp.length];
					for (int i = 0; i < tmp.length; i++) {
						this.getRestSecurityTokenPushResponseHttpStatus[i] = Integer.valueOf(tmp[i].trim());
					}
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
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
    			this.getRestNonBloccantePushResponseHttpMethod = new ArrayList<>();
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					for (int i = 0; i < tmp.length; i++) {
						this.getRestNonBloccantePushResponseHttpMethod.add(HttpRequestMethod.valueOf(tmp[i].trim().toUpperCase()));
					}
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonCorretta(name, e); 
				this.logError(msgErrore);
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
		
		this.getRestNonBloccantePushHttpMethod = new ArrayList<>();
		
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
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					this.getRestSecurityTokenPullRequestHttpStatus = new Integer[tmp.length];
					for (int i = 0; i < tmp.length; i++) {
						this.getRestSecurityTokenPullRequestHttpStatus[i] = Integer.valueOf(tmp[i].trim());
					}
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
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
    			this.getRestNonBloccantePullRequestHttpMethod = new ArrayList<>();
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					for (int i = 0; i < tmp.length; i++) {
						this.getRestNonBloccantePullRequestHttpMethod.add(HttpRequestMethod.valueOf(tmp[i].trim().toUpperCase()));
					}
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonCorretta(name, e); 
				this.logError(msgErrore);
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
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					this.getRestSecurityTokenPullRequestStateNotReadyHttpStatus = new Integer[tmp.length];
					for (int i = 0; i < tmp.length; i++) {
						this.getRestSecurityTokenPullRequestStateNotReadyHttpStatus[i] = Integer.valueOf(tmp[i].trim());
					}
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
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
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					this.getRestSecurityTokenPullRequestStateOkHttpStatus = new Integer[tmp.length];
					for (int i = 0; i < tmp.length; i++) {
						this.getRestSecurityTokenPullRequestStateOkHttpStatus[i] = Integer.valueOf(tmp[i].trim());
					}
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
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
    			this.getRestNonBloccantePullRequestStateHttpMethod = new ArrayList<>();
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					for (int i = 0; i < tmp.length; i++) {
						this.getRestNonBloccantePullRequestStateHttpMethod.add(HttpRequestMethod.valueOf(tmp[i].trim().toUpperCase()));
					}
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonCorretta(name, e); 
				this.logError(msgErrore);
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
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					this.getRestSecurityTokenPullResponseHttpStatus = new Integer[tmp.length];
					for (int i = 0; i < tmp.length; i++) {
						this.getRestSecurityTokenPullResponseHttpStatus[i] = Integer.valueOf(tmp[i].trim());
					}
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
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
    			this.getRestNonBloccantePullResponseHttpMethod = new ArrayList<>();
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					for (int i = 0; i < tmp.length; i++) {
						this.getRestNonBloccantePullResponseHttpMethod.add(HttpRequestMethod.valueOf(tmp[i].trim().toUpperCase()));
					}
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonCorretta(name, e); 
				this.logError(msgErrore);
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
		
		this.getRestNonBloccantePullHttpMethod = new ArrayList<>();
		
		readRestNonBloccantePullHttpMethodRequest();
		
		readRestNonBloccantePullHttpMethodResponse();
		
		return this.getRestNonBloccantePullHttpMethod;
	}
	private void readRestNonBloccantePullHttpMethodRequest() throws ProtocolException {
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
	}
	private void readRestNonBloccantePullHttpMethodResponse() throws ProtocolException {
		List<HttpRequestMethod> res = getRestNonBloccantePullResponseHttpMethod();
		if(res!=null && !res.isEmpty()){
			for (HttpRequestMethod httpRequestMethod : res) {
				if(!this.getRestNonBloccantePullHttpMethod.contains(httpRequestMethod)) {
					this.getRestNonBloccantePullHttpMethod.add(httpRequestMethod);
				}
			}
		}
	}
	
	
	/* **** SOAP **** */ 
	
	private Boolean getSoapSecurityTokenMustUnderstandReaded= null;
	private Boolean getSoapSecurityTokenMustUnderstand= null;
	public boolean isSoapSecurityTokenMustUnderstand() throws ProtocolException{
    	if(this.getSoapSecurityTokenMustUnderstandReaded==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.securityToken.mustUnderstand";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapSecurityTokenMustUnderstand = Boolean.valueOf(value);
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getSoapSecurityTokenMustUnderstandReaded = true;
    	}
    	
    	return this.getSoapSecurityTokenMustUnderstand;
	}	
	
	private Boolean getSoapSecurityTokenActorReaded= null;
	private String getSoapSecurityTokenActor= null;
	public String getSoapSecurityTokenActor() throws ProtocolException{
    	if(this.getSoapSecurityTokenActorReaded==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.securityToken.actor";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					if(!"".equals(value)) {
						this.getSoapSecurityTokenActor = value;
					}
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getSoapSecurityTokenActorReaded = true;
    	}
    	
    	return this.getSoapSecurityTokenActor;
	}
	
	private Boolean getSoapSecurityTokenTimestampCreatedTimeCheckMillisecondsReaded = null;
	private Long getSoapSecurityTokenTimestampCreatedTimeCheckMilliseconds = null;
	public Long getSoapSecurityTokenTimestampCreatedTimeCheckMilliseconds() throws ProtocolException{

		if(this.getSoapSecurityTokenTimestampCreatedTimeCheckMillisecondsReaded==null){
			
			String name = "org.openspcoop2.protocol.modipa.soap.securityToken.timestamp.created.minutes";
			try{  
				String value = this.reader.getValueConvertEnvProperties(name); 

				if (value != null){
					value = value.trim();
					long tmp = Long.parseLong(value); // minuti
					if(tmp>0) {
						long maxLongValue = ((Long.MAX_VALUE)/60000l);
						if(tmp>maxLongValue) {
							this.logWarn(getPrefixValoreIndicatoProprieta(value,name)+getSuffixSuperioreMassimoConsentitoControlloDisabilitato(maxLongValue));
						}
						else {
							this.getSoapSecurityTokenTimestampCreatedTimeCheckMilliseconds = tmp * 60 * 1000;
						}
					}
					else {
						this.logWarn(getMessaggioVerificaDisabilitata(name));
					}
				}
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(name, e),e);
				throw new ProtocolException(e.getMessage(),e);
			}
			
			this.getSoapSecurityTokenTimestampCreatedTimeCheckMillisecondsReaded = true;
		}

		return this.getSoapSecurityTokenTimestampCreatedTimeCheckMilliseconds;
	}
	
	private Boolean getSoapSecurityTokenTimestampCreatedTimeCheckFutureToleranceMillisecondsReaded = null;
	private Long getSoapSecurityTokenTimestampCreatedTimeCheckFutureToleranceMilliseconds = null;
	public Long getSoapSecurityTokenTimestampCreatedTimeCheckFutureToleranceMilliseconds() throws ProtocolException{

		if(this.getSoapSecurityTokenTimestampCreatedTimeCheckFutureToleranceMillisecondsReaded==null){
			
			String name = "org.openspcoop2.protocol.modipa.soap.securityToken.timestamp.created.future.toleranceMilliseconds";
			try{  
				String value = this.reader.getValueConvertEnvProperties(name); 

				if (value != null){
					value = value.trim();
					long tmp = Long.parseLong(value); // già in millisecondi
					if(tmp>0) {
						long maxLongValue = Long.MAX_VALUE;
						if(tmp>maxLongValue) {
							this.logWarn(getPrefixValoreIndicatoProprieta(value,name)+getSuffixSuperioreMassimoConsentitoControlloDisabilitato(maxLongValue));
						}
						else {
							this.getSoapSecurityTokenTimestampCreatedTimeCheckFutureToleranceMilliseconds = tmp;
						}
					}
					else {
						this.logWarn(getMessaggioVerificaDisabilitata(name));
					}
				}
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(name, e),e);
				throw new ProtocolException(e.getMessage(),e);
			}
			
			this.getSoapSecurityTokenTimestampCreatedTimeCheckFutureToleranceMillisecondsReaded = true;
		}

		return this.getSoapSecurityTokenTimestampCreatedTimeCheckFutureToleranceMilliseconds;
	}
	
	private Boolean getSoapSecurityTokenFaultProcessEnabledReaded= null;
	private Boolean getSoapSecurityTokenFaultProcessEnabled= null;
	public boolean isSoapSecurityTokenFaultProcessEnabled() throws ProtocolException{
    	if(this.getSoapSecurityTokenFaultProcessEnabledReaded==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.fault.securityToken";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapSecurityTokenFaultProcessEnabled = Boolean.valueOf(value);
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getSoapSecurityTokenFaultProcessEnabledReaded = true;
    	}
    	
    	return this.getSoapSecurityTokenFaultProcessEnabled;
	}
	
	private Boolean isSoapSecurityTokenTimestampExpiresTimeCheck= null;
	public boolean isSoapSecurityTokenTimestampExpiresTimeCheck() throws ProtocolException{
    	if(this.isSoapSecurityTokenTimestampExpiresTimeCheck==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.securityToken.timestamp.expires.checkEnabled";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.isSoapSecurityTokenTimestampExpiresTimeCheck = Boolean.valueOf(value);
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    	}
    	
    	return this.isSoapSecurityTokenTimestampExpiresTimeCheck;
	}	
	
	private Boolean getSoapSecurityTokenTimestampExpiresTimeCheckToleranceMillisecondsReaded = null;
	private Long getSoapSecurityTokenTimestampExpiresTimeCheckToleranceMilliseconds = null;
	public Long getSoapSecurityTokenTimestampExpiresTimeCheckToleranceMilliseconds() throws ProtocolException{

		if(this.getSoapSecurityTokenTimestampExpiresTimeCheckToleranceMillisecondsReaded==null){
			
			String name = "org.openspcoop2.protocol.modipa.soap.securityToken.timestamp.expires.toleranceMilliseconds";
			try{  
				String value = this.reader.getValueConvertEnvProperties(name); 

				if (value != null){
					value = value.trim();
					long tmp = Long.parseLong(value); // già in millisecondi
					if(tmp>0) {
						long maxLongValue = Long.MAX_VALUE;
						if(tmp>maxLongValue) {
							this.logWarn(getPrefixValoreIndicatoProprieta(value,name)+getSuffixSuperioreMassimoConsentitoControlloDisabilitato(maxLongValue));
						}
						else {
							this.getSoapSecurityTokenTimestampExpiresTimeCheckToleranceMilliseconds = tmp;
						}
					}
					else {
						this.logWarn(getMessaggioVerificaDisabilitata(name));
					}
				}
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(name, e),e);
				throw new ProtocolException(e.getMessage(),e);
			}
			
			this.getSoapSecurityTokenTimestampExpiresTimeCheckToleranceMillisecondsReaded = true;
		}

		return this.getSoapSecurityTokenTimestampExpiresTimeCheckToleranceMilliseconds;
	}
	
	private Boolean getSoapWSAddressingMustUnderstandReaded= null;
	private Boolean getSoapWSAddressingMustUnderstand= null;
	public boolean isSoapWSAddressingMustUnderstand() throws ProtocolException{
    	if(this.getSoapWSAddressingMustUnderstandReaded==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.wsaddressing.mustUnderstand";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapWSAddressingMustUnderstand = Boolean.valueOf(value);
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getSoapWSAddressingMustUnderstandReaded = true;
    	}
    	
    	return this.getSoapWSAddressingMustUnderstand;
	}	
	
	private Boolean getSoapWSAddressingActorReaded= null;
	private String getSoapWSAddressingActor= null;
	public String getSoapWSAddressingActor() throws ProtocolException{
    	if(this.getSoapWSAddressingActorReaded==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.wsaddressing.actor";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					if(!"".equals(value)) {
						this.getSoapWSAddressingActor = value;
					}
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getSoapWSAddressingActorReaded = true;
    	}
    	
    	return this.getSoapWSAddressingActor;
	}
	
	private Boolean getSoapWSAddressingSchemaValidationReaded= null;
	private Boolean getSoapWSAddressingSchemaValidation= null;
	public boolean isSoapWSAddressingSchemaValidation() throws ProtocolException{
    	if(this.getSoapWSAddressingSchemaValidationReaded==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.wsaddressing.schemaValidation";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapWSAddressingSchemaValidation = Boolean.valueOf(value);
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getSoapWSAddressingSchemaValidationReaded = true;
    	}
    	
    	return this.getSoapWSAddressingSchemaValidation;
	}	
	
	
	private String getSoapCorrelationIdName= null;
	public String getSoapCorrelationIdName() throws ProtocolException{
    	if(this.getSoapCorrelationIdName==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.correlationId.name";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapCorrelationIdName = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getSoapCorrelationIdName;
	}
	
	private String getSoapCorrelationIdNamespace= null;
	public String getSoapCorrelationIdNamespace() throws ProtocolException{
    	if(this.getSoapCorrelationIdNamespace==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.correlationId.namespace";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapCorrelationIdNamespace = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getSoapCorrelationIdNamespace;
	}
	
	public boolean useSoapBodyCorrelationIdNamespace() throws ProtocolException {
		return ModICostanti.MODIPA_USE_BODY_NAMESPACE.equals(this.getSoapCorrelationIdNamespace());
	}
	
	private String getSoapCorrelationIdPrefix= null;
	public String getSoapCorrelationIdPrefix() throws ProtocolException{
    	if(this.getSoapCorrelationIdPrefix==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.correlationId.prefix";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapCorrelationIdPrefix = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getSoapCorrelationIdPrefix;
	}
	
	private Boolean getSoapCorrelationIdMustUnderstandReaded= null;
	private Boolean getSoapCorrelationIdMustUnderstand= null;
	public boolean isSoapCorrelationIdMustUnderstand() throws ProtocolException{
    	if(this.getSoapCorrelationIdMustUnderstandReaded==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.correlationId.mustUnderstand";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapCorrelationIdMustUnderstand = Boolean.valueOf(value);
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getSoapCorrelationIdMustUnderstandReaded = true;
    	}
    	
    	return this.getSoapCorrelationIdMustUnderstand;
	}	
	
	private Boolean getSoapCorrelationIdActorReaded= null;
	private String getSoapCorrelationIdActor= null;
	public String getSoapCorrelationIdActor() throws ProtocolException{
    	if(this.getSoapCorrelationIdActorReaded==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.correlationId.actor";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					if(!"".equals(value)) {
						this.getSoapCorrelationIdActor = value;
					}
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getSoapCorrelationIdActorReaded = true;
    	}
    	
    	return this.getSoapCorrelationIdActor;
	}
	
	
	
	
	private String getSoapReplyToName= null;
	public String getSoapReplyToName() throws ProtocolException{
    	if(this.getSoapReplyToName==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.replyTo.name";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapReplyToName = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getSoapReplyToName;
	}
	
	private String getSoapReplyToNamespace= null;
	public String getSoapReplyToNamespace() throws ProtocolException{
    	if(this.getSoapReplyToNamespace==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.replyTo.namespace";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapReplyToNamespace = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getSoapReplyToNamespace;
	}
	
	public boolean useSoapBodyReplyToNamespace() throws ProtocolException {
		return ModICostanti.MODIPA_USE_BODY_NAMESPACE.equals(this.getSoapReplyToNamespace());
	}
	
	private String getSoapReplyToPrefix= null;
	public String getSoapReplyToPrefix() throws ProtocolException{
    	if(this.getSoapReplyToPrefix==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.replyTo.prefix";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapReplyToPrefix = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getSoapReplyToPrefix;
	}
	
	private Boolean getSoapReplyToMustUnderstandReaded= null;
	private Boolean getSoapReplyToMustUnderstand= null;
	public boolean isSoapReplyToMustUnderstand() throws ProtocolException{
    	if(this.getSoapReplyToMustUnderstandReaded==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.replyTo.mustUnderstand";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapReplyToMustUnderstand = Boolean.valueOf(value);
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getSoapReplyToMustUnderstandReaded = true;
    	}
    	
    	return this.getSoapReplyToMustUnderstand;
	}	
	
	private Boolean getSoapReplyToActorReaded= null;
	private String getSoapReplyToActor= null;
	public String getSoapReplyToActor() throws ProtocolException{
    	if(this.getSoapReplyToActorReaded==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.replyTo.actor";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					if(!"".equals(value)) {
						this.getSoapReplyToActor = value;
					}
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getSoapReplyToActorReaded = true;
    	}
    	
    	return this.getSoapReplyToActor;
	}
	
	
	private String getSoapRequestDigestName= null;
	public String getSoapRequestDigestName() throws ProtocolException{
    	if(this.getSoapRequestDigestName==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.requestDigest.name";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapRequestDigestName = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getSoapRequestDigestName;
	}
	
	private String getSoapRequestDigestNamespace= null;
	public String getSoapRequestDigestNamespace() throws ProtocolException{
    	if(this.getSoapRequestDigestNamespace==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.requestDigest.namespace";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapRequestDigestNamespace = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getSoapRequestDigestNamespace;
	}
	
	public boolean useSoapBodyRequestDigestNamespace() throws ProtocolException {
		return ModICostanti.MODIPA_USE_BODY_NAMESPACE.equals(this.getSoapRequestDigestNamespace());
	}
	
	private String getSoapRequestDigestPrefix= null;
	public String getSoapRequestDigestPrefix() throws ProtocolException{
    	if(this.getSoapRequestDigestPrefix==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.requestDigest.prefix";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapRequestDigestPrefix = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getSoapRequestDigestPrefix;
	}
	
	private Boolean getSoapRequestDigestMustUnderstandReaded= null;
	private Boolean getSoapRequestDigestMustUnderstand= null;
	public boolean isSoapRequestDigestMustUnderstand() throws ProtocolException{
    	if(this.getSoapRequestDigestMustUnderstandReaded==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.requestDigest.mustUnderstand";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapRequestDigestMustUnderstand = Boolean.valueOf(value);
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getSoapRequestDigestMustUnderstandReaded = true;
    	}
    	
    	return this.getSoapRequestDigestMustUnderstand;
	}	
	
	private Boolean getSoapRequestDigestActorReaded= null;
	private String getSoapRequestDigestActor= null;
	public String getSoapRequestDigestActor() throws ProtocolException{
    	if(this.getSoapRequestDigestActorReaded==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.requestDigest.actor";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					if(!"".equals(value)) {
						this.getSoapRequestDigestActor = value;
					}
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getSoapRequestDigestActorReaded = true;
    	}
    	
    	return this.getSoapRequestDigestActor;
	}
	
	private Boolean getSoapSecurityTokenWsaToReaded= null;
	private String getSoapSecurityTokenWsaTo= null;
	private String getSoapSecurityTokenWsaTo() throws ProtocolException{
    	if(this.getSoapSecurityTokenWsaToReaded==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.securityToken.wsaTo";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				if (value != null){
					value = value.trim();
					this.getSoapSecurityTokenWsaTo = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getSoapSecurityTokenWsaToReaded = true;
    	}
    	
    	return this.getSoapSecurityTokenWsaTo;
	}
	private Boolean getSoapSecurityTokenWsaToSoapAction= null;
	private Boolean getSoapSecurityTokenWsaToOperation= null;
	private Boolean getSoapSecurityTokenWsaToNone= null;
	public boolean isSoapSecurityTokenWsaToSoapAction() throws ProtocolException {
		if(this.getSoapSecurityTokenWsaToSoapAction==null) {
			this.getSoapSecurityTokenWsaToSoapAction = ModICostanti.CONFIG_MODIPA_SOAP_SECURITY_TOKEN_WSA_TO_KEYWORD_SOAP_ACTION.equalsIgnoreCase(getSoapSecurityTokenWsaTo());
		}
		return this.getSoapSecurityTokenWsaToSoapAction;
	}
	public boolean isSoapSecurityTokenWsaToOperation() throws ProtocolException {
		if(this.getSoapSecurityTokenWsaToOperation==null) {
			this.getSoapSecurityTokenWsaToOperation = ModICostanti.CONFIG_MODIPA_SOAP_SECURITY_TOKEN_WSA_TO_KEYWORD_OPERATION.equalsIgnoreCase(getSoapSecurityTokenWsaTo());
		}
		return this.getSoapSecurityTokenWsaToOperation;
	}
	public boolean isSoapSecurityTokenWsaToDisabled() throws ProtocolException {
		if(this.getSoapSecurityTokenWsaToNone==null) {
			this.getSoapSecurityTokenWsaToNone = ModICostanti.CONFIG_MODIPA_SOAP_SECURITY_TOKEN_WSA_TO_KEYWORD_NONE.equalsIgnoreCase(getSoapSecurityTokenWsaTo());
		}
		return this.getSoapSecurityTokenWsaToNone;
	}
	
	private Boolean getSoapResponseSecurityTokenAudienceDefaultReaded= null;
	private String getSoapResponseSecurityTokenAudienceDefault= null;
	public String getSoapResponseSecurityTokenAudienceDefault(String soggettoMittente) throws ProtocolException{
    	if(this.getSoapResponseSecurityTokenAudienceDefaultReaded==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.response.securityToken.audience.default";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				if (value != null){
					value = value.trim();
					this.getSoapResponseSecurityTokenAudienceDefault = value;
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    		
    		this.getSoapResponseSecurityTokenAudienceDefaultReaded = true;
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
	public boolean isSoapSecurityTokenPushReplyToUpdateOrCreateInFruizione() throws ProtocolException{
    	if(this.getSoapSecurityTokenPushReplyToUpdateOrCreate==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.push.replyTo.header.updateOrCreate";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapSecurityTokenPushReplyToUpdateOrCreate = Boolean.valueOf(value);
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getSoapSecurityTokenPushReplyToUpdateOrCreate;
	}
	
	private Boolean getSoapSecurityTokenPushReplyToUpdate = null;
	public boolean isSoapSecurityTokenPushReplyToUpdateInErogazione() throws ProtocolException{
    	if(this.getSoapSecurityTokenPushReplyToUpdate==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.push.replyTo.header.update";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapSecurityTokenPushReplyToUpdate = Boolean.valueOf(value);
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getSoapSecurityTokenPushReplyToUpdate;
	}
	
	private Boolean getSoapSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists = null;
	public boolean isSoapSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists() throws ProtocolException{
    	if(this.getSoapSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.push.request.correlationId.header.useTransactionIdIfNotExists";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists = Boolean.valueOf(value);
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.getSoapSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists;
	}
	
	private Boolean getSoapSecurityTokenPullCorrelationIdUseTransactionIdIfNotExists = null;
	public boolean isSoapSecurityTokenPullCorrelationIdUseTransactionIdIfNotExists() throws ProtocolException{
    	if(this.getSoapSecurityTokenPullCorrelationIdUseTransactionIdIfNotExists==null){
	    	String name = "org.openspcoop2.protocol.modipa.soap.pull.request.correlationId.header.useTransactionIdIfNotExists";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.getSoapSecurityTokenPullCorrelationIdUseTransactionIdIfNotExists = Boolean.valueOf(value);
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
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
				String value = this.reader.getValueConvertEnvProperties(pName); 
				
				if (value != null){
					value = value.trim();
					this.isReadByPathBufferEnabled = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(pName, true));
					this.isReadByPathBufferEnabled = true;
				}

			}catch(java.lang.Exception e) {
				this.logWarn(getMessaggioErroreProprietaNonImpostata(pName, true)+getSuffixErrore(e));
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
				String value = this.reader.getValueConvertEnvProperties(pName); 
				
				if (value != null){
					value = value.trim();
					this.isValidazioneBufferEnabled = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(pName, true));
					this.isValidazioneBufferEnabled = true;
				}

			}catch(java.lang.Exception e) {
				this.logWarn(getMessaggioErroreProprietaNonImpostata(pName, true)+getSuffixErrore(e));
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
	private Boolean isRiferimentoIDRichiestaPortaDelegataRequired= null;
	private Boolean isRiferimentoIDRichiestaPortaDelegataRequiredRead= null;
    public Boolean isRiferimentoIDRichiestaPortaDelegataRequired(){
    	if(this.isRiferimentoIDRichiestaPortaDelegataRequiredRead==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.modipa.pd.riferimentoIdRichiesta.required"); 
				
				if (value != null){
					value = value.trim();
					this.isRiferimentoIDRichiestaPortaDelegataRequired = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata("org.openspcoop2.protocol.modipa.pd.riferimentoIdRichiesta.required", true));
					this.isRiferimentoIDRichiestaPortaDelegataRequired = true;
				}
				
				this.isRiferimentoIDRichiestaPortaDelegataRequiredRead = true;
				
			}catch(java.lang.Exception e) {
				this.logWarn("Proprietà 'org.openspcoop2.protocol.modipa.pd.riferimentoIdRichiesta.required' non impostata, viene utilizzato il default 'true', errore:"+e.getMessage());
				this.isRiferimentoIDRichiestaPortaDelegataRequired = true;
				
				this.isRiferimentoIDRichiestaPortaDelegataRequiredRead = true;
			}
    	}
    	
    	return this.isRiferimentoIDRichiestaPortaDelegataRequired;
	}
	
	private Boolean isRiferimentoIDRichiestaPortaApplicativaRequired= null;
	private Boolean isRiferimentoIDRichiestaPortaApplicativaRequiredRead= null;
    public Boolean isRiferimentoIDRichiestaPortaApplicativaRequired(){
    	if(this.isRiferimentoIDRichiestaPortaApplicativaRequiredRead==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.modipa.pa.riferimentoIdRichiesta.required"); 
				
				if (value != null){
					value = value.trim();
					this.isRiferimentoIDRichiestaPortaApplicativaRequired = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata("org.openspcoop2.protocol.modipa.pa.riferimentoIdRichiesta.required", true));
					this.isRiferimentoIDRichiestaPortaApplicativaRequired = true;
				}
				
				this.isRiferimentoIDRichiestaPortaApplicativaRequiredRead = true;
				
			}catch(java.lang.Exception e) {
				this.logWarn("Proprietà 'org.openspcoop2.protocol.modipa.pa.riferimentoIdRichiesta.required' non impostata, viene utilizzato il default 'true', errore:"+e.getMessage());
				this.isRiferimentoIDRichiestaPortaApplicativaRequired = true;
				
				this.isRiferimentoIDRichiestaPortaApplicativaRequiredRead = true;
			}
    	}
    	
    	return this.isRiferimentoIDRichiestaPortaApplicativaRequired;
	}
    
	private Boolean isTokenOAuthUseJtiIntegrityAsMessageId= null;
	private Boolean isTokenOAuthUseJtiIntegrityAsMessageIdRead= null;
    public Boolean isTokenOAuthUseJtiIntegrityAsMessageId(){
    	if(this.isTokenOAuthUseJtiIntegrityAsMessageIdRead==null){
    		String pName = "org.openspcoop2.protocol.modipa.tokenOAuthIntegrity.useJtiIntegrityAsMessageId";
	    	try{  
				String value = this.reader.getValueConvertEnvProperties(pName); 
				
				if (value != null){
					value = value.trim();
					this.isTokenOAuthUseJtiIntegrityAsMessageId = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(pName, true));
					this.isTokenOAuthUseJtiIntegrityAsMessageId = true;
				}
				
				this.isTokenOAuthUseJtiIntegrityAsMessageIdRead = true;
				
			}catch(java.lang.Exception e) {
				this.logWarn(PREFIX_PROPRIETA+pName+"' non impostata, viene utilizzato il default 'true', errore:"+e.getMessage());
				this.isTokenOAuthUseJtiIntegrityAsMessageId = true;
				
				this.isTokenOAuthUseJtiIntegrityAsMessageIdRead = true;
			}
    	}
    	
    	return this.isTokenOAuthUseJtiIntegrityAsMessageId;
	}
	
	

	/* **** SOAP FAULT (Protocollo, Porta Applicativa) **** */
	
    /**
     * Indicazione se ritornare un soap fault personalizzato nel codice/actor/faultString per i messaggi di errore di protocollo (Porta Applicativa)
     *   
     * @return Indicazione se ritornare un soap fault personalizzato nel codice/actor/faultString per i messaggi di errore di protocollo (Porta Applicativa)
     * 
     */
	private Boolean isPortaApplicativaBustaErrorePersonalizzaElementiFault= null;
	private Boolean isPortaApplicativaBustaErrorePersonalizzaElementiFaultRead= null;
    public Boolean isPortaApplicativaBustaErrorePersonalizzaElementiFault(){
    	if(this.isPortaApplicativaBustaErrorePersonalizzaElementiFaultRead==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.modipa.pa.bustaErrore.personalizzaElementiFault"); 
				
				if (value != null){
					value = value.trim();
					this.isPortaApplicativaBustaErrorePersonalizzaElementiFault = Boolean.parseBoolean(value);
				}else{
					this.logDebug("Proprietà 'org.openspcoop2.protocol.modipa.pa.bustaErrore.personalizzaElementiFault' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails)");
					this.isPortaApplicativaBustaErrorePersonalizzaElementiFault = null;
				}
				
				this.isPortaApplicativaBustaErrorePersonalizzaElementiFaultRead = true;
				
			}catch(java.lang.Exception e) {
				this.logWarn("Proprietà 'org.openspcoop2.protocol.modipa.pa.bustaErrore.personalizzaElementiFault' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails), errore:"+e.getMessage());
				this.isPortaApplicativaBustaErrorePersonalizzaElementiFault = null;
				
				this.isPortaApplicativaBustaErrorePersonalizzaElementiFaultRead = true;
			}
    	}
    	
    	return this.isPortaApplicativaBustaErrorePersonalizzaElementiFault;
	}
    
    
    /**
     * Indicazione se deve essere aggiunto un errore-applicativo nei details di un messaggio di errore di protocollo (Porta Applicativa)
     *   
     * @return Indicazione se deve essere aggiunto un errore-applicativo nei details di un messaggio di errore di protocollo (Porta Applicativa)
     * 
     */
	private Boolean isPortaApplicativaBustaErroreAggiungiErroreApplicativo= null;
	private Boolean isPortaApplicativaBustaErroreAggiungiErroreApplicativoRead= null;
    public Boolean isPortaApplicativaBustaErroreAggiungiErroreApplicativo(){
    	if(this.isPortaApplicativaBustaErroreAggiungiErroreApplicativoRead==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.modipa.pa.bustaErrore.aggiungiErroreApplicativo"); 
				
				if (value != null){
					value = value.trim();
					this.isPortaApplicativaBustaErroreAggiungiErroreApplicativo = Boolean.parseBoolean(value);
				}else{
					this.logDebug("Proprietà 'org.openspcoop2.protocol.modipa.pa.bustaErrore.aggiungiErroreApplicativo' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails)");
					this.isPortaApplicativaBustaErroreAggiungiErroreApplicativo = null;
				}
				
				this.isPortaApplicativaBustaErroreAggiungiErroreApplicativoRead = true;
				
			}catch(java.lang.Exception e) {
				this.logWarn("Proprietà 'org.openspcoop2.protocol.modipa.pa.bustaErrore.aggiungiErroreApplicativo' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails), errore:"+e.getMessage());
				this.isPortaApplicativaBustaErroreAggiungiErroreApplicativo = null;
				
				this.isPortaApplicativaBustaErroreAggiungiErroreApplicativoRead = true;
			}
    	}
    	
    	return this.isPortaApplicativaBustaErroreAggiungiErroreApplicativo;
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
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.eccezioneIntestazione"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultProtocolValidazione = Boolean.parseBoolean(value);
				}else{
					this.logWarn(getMessaggioErroreProprietaNonImpostata("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.eccezioneIntestazione", false));
					this.isGenerazioneDetailsSOAPFaultProtocolValidazione = false;
				}
				
			}catch(java.lang.Exception e) {
				this.logWarn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.eccezioneIntestazione' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.eccezioneProcessamento"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultProtocolProcessamento = Boolean.parseBoolean(value);
				}else{
					this.logWarn(getMessaggioErroreProprietaNonImpostata("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.eccezioneProcessamento", true));
					this.isGenerazioneDetailsSOAPFaultProtocolProcessamento = true;
				}
				
			}catch(java.lang.Exception e) {
				this.logWarn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.eccezioneProcessamento' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.stackTrace"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = Boolean.parseBoolean(value);
				}else{
					this.logWarn(getMessaggioErroreProprietaNonImpostata("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.stackTrace", false));
					this.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = false;
				}
				
			}catch(java.lang.Exception e) {
				this.logWarn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.stackTrace' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.informazioniGeneriche"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = Boolean.parseBoolean(value);
				}else{
					this.logWarn(getMessaggioErroreProprietaNonImpostata("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.informazioniGeneriche", true));
					this.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = true;
				}
				
			}catch(java.lang.Exception e) {
				this.logWarn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.protocol.informazioniGeneriche' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.serverError"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultIntegrationServerError = Boolean.parseBoolean(value);
				}else{
					this.logWarn(getMessaggioErroreProprietaNonImpostata("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.serverError", true));
					this.isGenerazioneDetailsSOAPFaultIntegrationServerError = true;
				}
				
			}catch(java.lang.Exception e) {
				this.logWarn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.serverError' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.clientError"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultIntegrationClientError = Boolean.parseBoolean(value);
				}else{
					this.logWarn(getMessaggioErroreProprietaNonImpostata("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.clientError", false));
					this.isGenerazioneDetailsSOAPFaultIntegrationClientError = false;
				}
				
			}catch(java.lang.Exception e) {
				this.logWarn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.clientError' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.stackTrace"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = Boolean.parseBoolean(value);
				}else{
					this.logWarn(getMessaggioErroreProprietaNonImpostata("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.stackTrace", false));
					this.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = false;
				}
				
			}catch(java.lang.Exception e) {
				this.logWarn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.stackTrace' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.informazioniGeneriche"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche = Boolean.parseBoolean(value);
				}else{
					this.logDebug("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.informazioniGeneriche' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultAsGenericCode)");
					this.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche = null;
				}
				
				this.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGenericheRead = true;
				
			}catch(java.lang.Exception e) {
				this.logWarn("Proprietà 'org.openspcoop2.protocol.modipa.generazioneDetailsSoapFault.integration.informazioniGeneriche' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultAsGenericCode), errore:"+e.getMessage());
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
	private BooleanNullable isAggiungiDetailErroreApplicativoSoapFaultApplicativo= null;
	private Boolean isAggiungiDetailErroreApplicativoSoapFaultApplicativoRead= null;
    public BooleanNullable isAggiungiDetailErroreApplicativoSoapFaultApplicativo(){
    	if(this.isAggiungiDetailErroreApplicativoSoapFaultApplicativoRead==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.modipa.erroreApplicativo.faultApplicativo.enrichDetails"); 
				
				if (value != null){
					value = value.trim();
					Boolean b = Boolean.parseBoolean(value);
					this.isAggiungiDetailErroreApplicativoSoapFaultApplicativo = b.booleanValue() ? BooleanNullable.TRUE() : BooleanNullable.FALSE();
				}else{
					this.logDebug("Proprietà 'org.openspcoop2.protocol.modipa.erroreApplicativo.faultApplicativo.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails)");
					this.isAggiungiDetailErroreApplicativoSoapFaultApplicativo = BooleanNullable.NULL();
				}
				
				this.isAggiungiDetailErroreApplicativoSoapFaultApplicativoRead = true;
				
			}catch(java.lang.Exception e) {
				this.logWarn("Proprietà 'org.openspcoop2.protocol.modipa.erroreApplicativo.faultApplicativo.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails), errore:"+e.getMessage());
				this.isAggiungiDetailErroreApplicativoSoapFaultApplicativo = BooleanNullable.NULL();
				
				this.isAggiungiDetailErroreApplicativoSoapFaultApplicativoRead = true;
			}
    	}
    	
    	return this.isAggiungiDetailErroreApplicativoSoapFaultApplicativo;
	}
    
    /**
     * Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultPdD originale
     *   
     * @return Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultPdD originale
     * 
     */
	private BooleanNullable isAggiungiDetailErroreApplicativoSoapFaultPdD= null;
	private Boolean isAggiungiDetailErroreApplicativoSoapFaultPdDRead= null;
    public BooleanNullable isAggiungiDetailErroreApplicativoSoapFaultPdD(){
    	if(this.isAggiungiDetailErroreApplicativoSoapFaultPdDRead==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.modipa.erroreApplicativo.faultPdD.enrichDetails"); 
				
				if (value != null){
					value = value.trim();
					Boolean b = Boolean.parseBoolean(value);
					this.isAggiungiDetailErroreApplicativoSoapFaultPdD = b.booleanValue() ? BooleanNullable.TRUE() : BooleanNullable.FALSE();
				}else{
					this.logDebug("Proprietà 'org.openspcoop2.protocol.modipa.erroreApplicativo.faultPdD.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultPdD.enrichDetails)");
					this.isAggiungiDetailErroreApplicativoSoapFaultPdD = BooleanNullable.NULL();
				}
				
				this.isAggiungiDetailErroreApplicativoSoapFaultPdDRead = true;
				
			}catch(java.lang.Exception e) {
				this.logWarn("Proprietà 'org.openspcoop2.protocol.modipa.erroreApplicativo.faultPdD.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultPdD.enrichDetails), errore:"+e.getMessage());
				this.isAggiungiDetailErroreApplicativoSoapFaultPdD = BooleanNullable.NULL();
				
				this.isAggiungiDetailErroreApplicativoSoapFaultPdDRead = true;
			}
    	}
    	
    	return this.isAggiungiDetailErroreApplicativoSoapFaultPdD;
	}

    
    /* **** Static instance config **** */
    
	private Boolean useConfigStaticInstance = null;
	private Boolean useConfigStaticInstance(){
		if(this.useConfigStaticInstance==null){
			
			Boolean defaultValue = true;
			String propertyName = "org.openspcoop2.protocol.modipa.factory.config.staticInstance";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.useConfigStaticInstance = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.useConfigStaticInstance = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
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
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.useErroreApplicativoStaticInstance = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.useErroreApplicativoStaticInstance = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
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
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.useEsitoStaticInstance = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.useEsitoStaticInstance = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
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
	
	
	
	
    /* **** Signal Hub **** */
	
	// riferito in org.openspcoop2.protocol.utils.ModIUtils
	private Boolean signalHubEnabled = null;
	public boolean isSignalHubEnabled(){
		if(this.signalHubEnabled==null){
			
			Boolean defaultValue =false;
			String propertyName = "org.openspcoop2.protocol.modipa.signalHub.enabled";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.signalHubEnabled = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.signalHubEnabled = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
				this.signalHubEnabled = defaultValue;
			}
		}

		return this.signalHubEnabled;
	}
	
	private List<String> signalHubAlgorithms= null;
	public List<String> getSignalHubAlgorithms() throws ProtocolException{
    	if(this.signalHubAlgorithms==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.signalHub.algorithms";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName);
    			this.signalHubAlgorithms = ModISecurityConfig.convertToList(value);
    			if(this.signalHubAlgorithms==null || this.signalHubAlgorithms.isEmpty()) {
    				throw new ProtocolException("SignalHub algorithms undefined");
    			}
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(propertyName, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.signalHubAlgorithms;
	}
	
	private String signalHubDefaultAlgorithm= null;
	public String getSignalHubDefaultAlgorithm() throws ProtocolException{
    	if(this.signalHubDefaultAlgorithm==null){
	    	String name = "org.openspcoop2.protocol.modipa.signalHub.algorithms.default";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.signalHubDefaultAlgorithm = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.signalHubDefaultAlgorithm;
	}
	
	private List<Integer> signalHubSeedSize= null;
	public List<Integer> getSignalHubSeedSize() throws ProtocolException{
    	if(this.signalHubSeedSize==null){
	    	
    		String propertyName = "org.openspcoop2.protocol.modipa.signalHub.seed.size";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName);
    			this.signalHubSeedSize = ModISecurityConfig.convertToList(value)
    					.stream()
    					.map(Integer::parseInt)
    					.collect(Collectors.toList());
    			if(this.signalHubSeedSize==null || this.signalHubSeedSize.isEmpty()) {
    				throw new ProtocolException("SignalHub algorithms undefined");
    			}
    			for (Integer s : this.signalHubSeedSize) {
    				validateSignalHubInteger("Signal Hub - Seed size",s);
				}
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonImpostata(propertyName, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.signalHubSeedSize;
	}
	
	private static void validateSignalHubInteger(String objectTitle, Integer i) throws ProtocolException {
		try {
			if(i<=0) {
				throw new ProtocolException("must be a positive integer greater than 0");
			}
		}catch(Exception e) {
			throw new ProtocolException(objectTitle+" '"+i+"' invalid; must be a positive integer greater than 0");
		}
	}
	
	private Integer signalHubDefaultSeedSize= null;
	public Integer getSignalHubDefaultSeedSize() throws ProtocolException{
    	if(this.signalHubDefaultSeedSize==null){
	    	String name = "org.openspcoop2.protocol.modipa.signalHub.seed.size.default";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					Integer valueInt = Integer.parseInt(value.trim());
					validateSignalHubInteger("Signal Hub - Default seed size", valueInt);
					this.signalHubDefaultSeedSize = valueInt;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.signalHubDefaultSeedSize;
	}
	
	private Boolean signalHubSeedLifetimeUnlimited = null;
	public boolean isSignalHubSeedLifetimeUnlimited(){
		if(this.signalHubSeedLifetimeUnlimited==null){
			
			Boolean defaultValue =false;
			String propertyName = "org.openspcoop2.protocol.modipa.signalHub.seed.lifetime.unlimited";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.signalHubSeedLifetimeUnlimited = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.signalHubSeedLifetimeUnlimited = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
				this.signalHubSeedLifetimeUnlimited = defaultValue;
			}
		}

		return this.signalHubSeedLifetimeUnlimited;
	}
	
	private Integer signalHubDefaultSeedLifetimeDaysDefault= null;
	public Integer getSignalHubDeSeedSeedLifetimeDaysDefault() throws ProtocolException{
    	if(this.signalHubDefaultSeedLifetimeDaysDefault==null){
	    	String name = "org.openspcoop2.protocol.modipa.signalHub.seed.lifetime.days.default";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					Integer valueInt = Integer.parseInt(value.trim());
					validateSignalHubInteger("Signal Hub - Default lifetime days", valueInt);
					this.signalHubDefaultSeedLifetimeDaysDefault = valueInt;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.signalHubDefaultSeedLifetimeDaysDefault;
	}
	
	private String signalHubSoapNamespace = null;
	public String getSignalHubSoapNamespace() throws ProtocolException{
    	if(this.signalHubSoapNamespace==null){
	    	String name = "org.openspcoop2.protocol.modipa.signalHub.soap.namespace";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.signalHubSoapNamespace = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.signalHubSoapNamespace;
	}
	
	private String signalHubApiName= null;
	public String getSignalHubApiName() throws ProtocolException{
    	if(this.signalHubApiName==null){
	    	String name = "org.openspcoop2.protocol.modipa.signalHub.api.name";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					value = value.trim();
					this.signalHubApiName = value;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.signalHubApiName;
	}
	
	private Integer signalHubApiVersion= null;
	public int getSignalHubApiVersion() throws ProtocolException{
    	if(this.signalHubApiVersion==null){
	    	String name = "org.openspcoop2.protocol.modipa.signalHub.api.version";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(name); 
				
				if (value != null){
					Integer valueInt = Integer.parseInt(value.trim());
					validateSignalHubInteger("Signal Hub - API Version", valueInt);
					this.signalHubApiVersion = valueInt;
				}
				else {
					throw newProtocolExceptionPropertyNonDefinita();
				}
				
			}catch(java.lang.Exception e) {
				String msgErrore = getMessaggioErroreProprietaNonImpostata(name, e); 
				this.logError(msgErrore);
				throw new ProtocolException(msgErrore,e);
			}
    	}
    	
    	return this.signalHubApiVersion;
	}
	
	
	
	private ModISignalHubConfig signalHubConfig = null;
	public ModISignalHubConfig getSignalHubConfig() throws ProtocolException{
    	if(this.signalHubConfig==null){
    		String propertyPrefix = "org.openspcoop2.protocol.modipa.signalHub";
        	try{
        		String debugPrefix = "Param signal hub '"+propertyPrefix+"'";
				Properties p = this.reader.readProperties(propertyPrefix+"."); // non devo convertire le properties poiche' possoono contenere ${ che useremo per la risoluzione dinamica
				if(p==null || p.isEmpty()) {
					throw new ProtocolException(debugPrefix+SUFFIX_NON_TROVATA);
				}
        		this.signalHubConfig = new ModISignalHubConfig(propertyPrefix, p);
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonCorretta(propertyPrefix, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.signalHubConfig;
	}
	
	private String signalHubHashCompose = null;
	public String getSignalHubHashCompose() throws ProtocolException{
    	if(this.signalHubHashCompose==null){
    		String name = "org.openspcoop2.protocol.modipa.signalHub.hash.composition";
        	try{
				String value = this.reader.getValue(name); // non devo convertire le properties poiche' possoono contenere ${ che useremo per la risoluzione dinamica
				if(value == null || value.isBlank()) {
					throw new ProtocolException(name + SUFFIX_NON_TROVATA);
				}
        		this.signalHubHashCompose = value;
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonCorretta(name, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.signalHubHashCompose;
	}
	
	private Integer signalHubDigestHistroy = null;
	public int getSignalHubDigestHistroy() throws ProtocolException{
    	if(this.signalHubDigestHistroy==null){
    		String name = "org.openspcoop2.protocol.modipa.signalHub.seed.history";
        	try{
				String rawValue = this.reader.getValue(name); // non devo convertire le properties poiche' possoono contenere ${ che useremo per la risoluzione dinamica
				if(rawValue == null || rawValue.isBlank()) {
					
					throw new ProtocolException(name + SUFFIX_NON_TROVATA);
				}
				Integer value = Integer.valueOf(rawValue);
				this.signalHubDigestHistroy = value;
			}catch(java.lang.Exception e) {
				this.logError(getMessaggioErroreProprietaNonCorretta(name, e));
				throw new ProtocolException(e.getMessage(),e);
			}
    	}
    	
    	return this.signalHubDigestHistroy;
	}

	
	
	
	
    /* **** TracingPDND **** */
	
	// riferito in org.openspcoop2.protocol.utils.ModIUtils
	private Boolean tracingPDNDEnabled = null;
	public boolean isTracingPDNDEnabled(){
		if(this.tracingPDNDEnabled==null){
			
			Boolean defaultValue =false;
			String propertyName = "org.openspcoop2.protocol.modipa.tracingPDND.enabled";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.tracingPDNDEnabled = Boolean.parseBoolean(value);
				}else{
					this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue));
					this.tracingPDNDEnabled = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.logDebug(getMessaggioErroreProprietaNonImpostata(propertyName, defaultValue)+getSuffixErrore(e));
				this.tracingPDNDEnabled = defaultValue;
			}
		}

		return this.tracingPDNDEnabled;
	}
}
