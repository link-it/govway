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
package org.openspcoop2.pdd.core.token;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.dynamic.DynamicException;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.core.dynamic.Template;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.utils.ModIKeystoreUtils;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.MerlinKeystore;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;

/**
 * AttributeAuthorityDynamicParameter
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NegoziazioneTokenDynamicParameters extends AbstractDynamicParameters {

	@SuppressWarnings("unused")
	private PolicyNegoziazioneToken policyNegoziazioneToken;
	
	private String endpoint;
	private String basicUsername;
	private String basicPassword;
	private String bearerToken;
	
	private String usernamePasswordGrantUsername;
	private String usernamePasswordGrantPassword;
	
	private String signedJwtIssuer;
	private String signedJwtClientId;
	private String signedJwtSubject;
	private String signedJwtAudience;
	private String signedJwtJti;
	private String signedJwtPurposeId;
	private String signedJwtSessionInfo;
	private String signedJwtClaims;
	
	private String signedJwtCustomId; 
	private String signedJwtX509Url;
	
	private String scope;
	private String audience;
	private String formClientId;
	private String formResource;
	private String parameters;
	
	private HttpRequestMethod httpMethod;
	private String httpContentType;
	private String httpHeaders;
	private String httpPayloadTemplateType;
	private String httpPayload;
	
	private IDServizioApplicativo idApplicativoRichiedente;
	private ServizioApplicativo applicativoRichiedente;
	private String kidApplicativoModI; // nella chiave della cache non viene aggiunto il kid ma l'applicativo richiedente, poichè il kid potrebbe essere condiviso tra più applicativi
	private ModIKeystoreUtils keystoreApplicativoModI;
	
	private IDFruizione idFruizione;
	private String kidFruizioneModI; // nella chiave della cache non viene aggiunto il kid ma l'identificativo della fruizione, poichè il kid potrebbe essere condiviso tra più fruizioni
	private ModIKeystoreUtils keystoreFruizioneModI;
	
	
	// static config
	
	private static boolean signedJwtIssuerCacheKey;
	private static boolean signedJwtClientIdCacheKey;
	private static boolean signedJwtSubjectCacheKey;
	private static boolean signedJwtAudienceCacheKey;
	private static boolean signedJwtJtiCacheKey;
	private static boolean signedJwtPurposeIdCacheKey;
	private static boolean signedJwtSessionInfoCacheKey;
	private static boolean signedJwtClaimsCacheKey;
	
	private static boolean signedJwtCustomIdCacheKey; 
	private static boolean signedJwtX509UrlCacheKey;
	
	private static boolean scopeCacheKey;
	private static boolean audienceCacheKey;
	private static boolean formClientIdCacheKey;
	private static boolean formResourceCacheKey;
	private static boolean parametersCacheKey; 
	
	private static boolean httpMethodCacheKey; 
	private static boolean httpContentTypeCacheKey; 
	private static boolean httpHeadersCacheKey; 
	private static boolean httpPayloadTemplateTypeCacheKey; 
	private static boolean httpPayloadCacheKey; 
	
	private static boolean applicativoRichiedenteCacheKey; 
	
	private static boolean fruizioneCacheKey; 
	
	private static Boolean initCacheKey = null;
	private static synchronized void initCacheKey() {
		if(initCacheKey==null) {
			OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
			
			signedJwtIssuerCacheKey = op2Properties.isGestioneRetrieveTokenCacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_ISSUER);
			signedJwtClientIdCacheKey = op2Properties.isGestioneRetrieveTokenCacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_CLIENT_ID);
			signedJwtSubjectCacheKey = op2Properties.isGestioneRetrieveTokenCacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_SUBJECT);
			signedJwtAudienceCacheKey = op2Properties.isGestioneRetrieveTokenCacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_AUDIENCE);
			signedJwtJtiCacheKey = op2Properties.isGestioneRetrieveTokenCacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_IDENTIFIER);
			signedJwtPurposeIdCacheKey = op2Properties.isGestioneRetrieveTokenCacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_PURPOSE_ID);
			signedJwtSessionInfoCacheKey = op2Properties.isGestioneRetrieveTokenCacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_SESSION_INFO);
			signedJwtClaimsCacheKey = op2Properties.isGestioneRetrieveTokenCacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_CLAIMS);
			
			signedJwtCustomIdCacheKey = op2Properties.isGestioneRetrieveTokenCacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_HEADER_KID); 
			signedJwtX509UrlCacheKey = op2Properties.isGestioneRetrieveTokenCacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_HEADER_X509_URL);
			
			scopeCacheKey = op2Properties.isGestioneRetrieveTokenCacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FORM_REQUEST_SCOPE);
			audienceCacheKey = op2Properties.isGestioneRetrieveTokenCacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FORM_REQUEST_AUDIENCE);
			formClientIdCacheKey = op2Properties.isGestioneRetrieveTokenCacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FORM_REQUEST_CLIENT_ID);
			formResourceCacheKey = op2Properties.isGestioneRetrieveTokenCacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FORM_REQUEST_RESOURCE);
			parametersCacheKey = op2Properties.isGestioneRetrieveTokenCacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FORM_REQUEST_PARAMETERS);
			
			httpMethodCacheKey = op2Properties.isGestioneRetrieveTokenCacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_HTTP_METHOD); 
			httpContentTypeCacheKey = op2Properties.isGestioneRetrieveTokenCacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_HTTP_CONTENT_TYPE); 
			httpHeadersCacheKey = op2Properties.isGestioneRetrieveTokenCacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_HTTP_HEADERS); 
			httpPayloadTemplateTypeCacheKey = op2Properties.isGestioneRetrieveTokenCacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_HTTP_PAYLOAD_TEMPLATE_TYPE); 
			httpPayloadCacheKey = op2Properties.isGestioneRetrieveTokenCacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_HTTP_PAYLOAD); 

			applicativoRichiedenteCacheKey = op2Properties.isGestioneRetrieveTokenCacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FORM_APPLICATIVE_REQUESTER); 
			
			fruizioneCacheKey = op2Properties.isGestioneRetrieveTokenCacheKey(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FORM_OUTBOUND_INTERFACE); 
						
			initCacheKey = true;
		}
	}
	private static void checkInitCacheKey() {
		if(initCacheKey==null) {
			initCacheKey();
		}
	}
	

	public NegoziazioneTokenDynamicParameters(Map<String, Object> dynamicMap, 
			PdDContext pddContext, RequestInfo requestInfo, Busta busta, IState state, IProtocolFactory<?> protocolFactory,
			PolicyNegoziazioneToken policyNegoziazioneToken) throws TokenException, DynamicException, ProviderException, ProviderValidationException, ProtocolException {
		super(dynamicMap, pddContext, requestInfo);
		
		this.policyNegoziazioneToken = policyNegoziazioneToken;
		
		this.endpoint = policyNegoziazioneToken.getEndpoint();
		if(this.endpoint!=null && !"".equals(this.endpoint)) {
			this.endpoint = DynamicUtils.convertDynamicPropertyValue("endpoint.gwt", this.endpoint, dynamicMap, pddContext);	
		}
		
		boolean basic = policyNegoziazioneToken.isBasicAuthentication();
		if(basic) {
			this.basicUsername = policyNegoziazioneToken.getBasicAuthenticationUsername();
			if(this.basicUsername!=null && !"".equals(this.basicUsername)) {
				this.basicUsername = DynamicUtils.convertDynamicPropertyValue("username.gwt", this.basicUsername, dynamicMap, pddContext);	
			}
			
			this.basicPassword = policyNegoziazioneToken.getBasicAuthenticationPassword();
			if(this.basicPassword!=null && !"".equals(this.basicPassword)) {
				this.basicPassword = DynamicUtils.convertDynamicPropertyValue("password.gwt", this.basicPassword, dynamicMap, pddContext);	
			}
		}
		
		boolean bearer = policyNegoziazioneToken.isBearerAuthentication();
		if(bearer) {
			this.bearerToken = policyNegoziazioneToken.getBeareAuthenticationToken();
			if(this.bearerToken!=null && !"".equals(this.bearerToken)) {
				this.bearerToken = DynamicUtils.convertDynamicPropertyValue("bearerToken.gwt", this.bearerToken, dynamicMap, pddContext);	
			}
		}
		
		if(policyNegoziazioneToken.isUsernamePasswordGrant()) {
			this.usernamePasswordGrantUsername = policyNegoziazioneToken.getUsernamePasswordGrantUsername();
			if(this.usernamePasswordGrantUsername!=null && !"".equals(this.usernamePasswordGrantUsername)) {
				this.usernamePasswordGrantUsername = DynamicUtils.convertDynamicPropertyValue("usernamePasswordGrant_username.gwt", this.usernamePasswordGrantUsername, dynamicMap, pddContext);	
			}
			
			this.usernamePasswordGrantPassword = policyNegoziazioneToken.getUsernamePasswordGrantPassword();
			if(this.usernamePasswordGrantPassword!=null && !"".equals(this.usernamePasswordGrantPassword)) {
				this.usernamePasswordGrantPassword = DynamicUtils.convertDynamicPropertyValue("usernamePasswordGrant_password.gwt", this.usernamePasswordGrantPassword, dynamicMap, pddContext);	
			}
		}
		
		String clientIdApplicativoModI = null;
		String idApp = null;
		
		String clientIdFruizioneModI = null;
		String idFruizioneLabel = null;
		List<ProtocolProperty> listProtocolPropertiesFruizione = null;
		
		if(policyNegoziazioneToken.isRfc7523x509Grant() || policyNegoziazioneToken.isRfc7523ClientSecretGrant()) {
			
			if(policyNegoziazioneToken.isRfc7523x509Grant()) {
				if(policyNegoziazioneToken.isJwtSignKeystoreApplicativoModI()) {
					String prefixError = "Il tipo di keystore indicato nella token policy '"+policyNegoziazioneToken.getName()+"' "; 
					if(busta.getServizioApplicativoFruitore()==null) {
						throw new TokenException(prefixError+"richiede l'autenticazione e l'identificazione di un applicativo fruitore");
					}
					if(busta.getTipoMittente()==null || busta.getMittente()==null) {
						throw new TokenException(prefixError+"richiede l'autenticazione e l'identificazione di un applicativo fruitore: dominio non identificato");
					}
					
					ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(state);
					
					this.idApplicativoRichiedente = new IDServizioApplicativo();
					this.idApplicativoRichiedente.setNome(busta.getServizioApplicativoFruitore());
					this.idApplicativoRichiedente.setIdSoggettoProprietario(new IDSoggetto(busta.getTipoMittente(), busta.getMittente()));
					try {
						this.applicativoRichiedente = configurazionePdDManager.getServizioApplicativo(this.idApplicativoRichiedente, this.getRequestInfo());
					}catch(Exception t) {
						throw new TokenException(prefixError+"richiede l'autenticazione e l'identificazione di un applicativo fruitore: "+t.getMessage(),t);
					}
					
					if(!org.openspcoop2.protocol.engine.constants.Costanti.MODIPA_PROTOCOL_NAME.equals(protocolFactory.getProtocol())) {
						throw new TokenException(prefixError+"è utilizzabile solamente con il profilo di interoperabilità 'ModI'");
					}
					
					idApp = this.idApplicativoRichiedente.getNome() + " (Soggetto: "+this.idApplicativoRichiedente.getIdSoggettoProprietario().getNome()+")";
					
					try {
						this.keystoreApplicativoModI = new ModIKeystoreUtils(this.applicativoRichiedente, "Token Policy Negoziazione - Signed JWT");
					}catch(Exception t) {
						throw new TokenException(prefixError+"non è utilizzabile: "+t.getMessage(),t);
					}
				}
				else if(policyNegoziazioneToken.isJwtSignKeystoreFruizioneModI()) {
					String prefixError = "Il tipo di keystore indicato nella token policy '"+policyNegoziazioneToken.getName()+"' "; 
					
					if(busta.getTipoMittente()==null || busta.getMittente()==null) {
						throw new TokenException(prefixError+"non è utilizzabile: soggetto fruitore non identificato");
					}
					if(busta.getTipoDestinatario()==null || busta.getDestinatario()==null) {
						throw new TokenException(prefixError+"non è utilizzabile: soggetto erogatore non identificato");
					}
					if(busta.getTipoServizio()==null || busta.getServizio()==null || busta.getVersioneServizio()==null) {
						throw new TokenException(prefixError+"non è utilizzabile: dati servizio non identificati");
					}
					
					RegistroServiziManager registroServiziManager = RegistroServiziManager.getInstance(state);
					
					IDSoggetto idFruitore = new IDSoggetto(busta.getTipoMittente(), busta.getMittente());
					IDServizio idServizio = null;
					try {
						idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(busta.getTipoServizio(), busta.getServizio(), 
								busta.getTipoDestinatario(), busta.getDestinatario(), 
								busta.getVersioneServizio());
					}catch(Exception e) {
						throw new TokenException(e.getMessage(),e);
					}
					this.idFruizione = new IDFruizione();
					this.idFruizione.setIdFruitore(idFruitore);
					this.idFruizione.setIdServizio(idServizio);
					
					if(!org.openspcoop2.protocol.engine.constants.Costanti.MODIPA_PROTOCOL_NAME.equals(protocolFactory.getProtocol())) {
						throw new TokenException(prefixError+"è utilizzabile solamente con il profilo di interoperabilità 'ModI'");
					}
					
					AccordoServizioParteSpecifica asps = null;
					try {
						asps = registroServiziManager.getAccordoServizioParteSpecifica(this.idFruizione.getIdServizio(), null, false, this.getRequestInfo());
					}catch(Exception t) {
						throw new TokenException(prefixError+"richiede l'identificazione di servizio: "+t.getMessage(),t);
					}
										
					idFruizioneLabel = this.idFruizione.getIdFruitore().getNome() + " -> "+this.idFruizione.getIdServizio().getNome()+" (Soggetto: "+this.idFruizione.getIdServizio().getSoggettoErogatore().getNome()+")";
					
					try {
						this.keystoreFruizioneModI = new ModIKeystoreUtils(true, this.idFruizione.getIdFruitore(), asps, "Token Policy Negoziazione - Signed JWT",
								NegoziazioneTokenDynamicParametersModIUtils.getSicurezzaMessaggioCertificatiKeyStoreTipo(),
								NegoziazioneTokenDynamicParametersModIUtils.getSicurezzaMessaggioCertificatiKeyStorePath(),
								NegoziazioneTokenDynamicParametersModIUtils.getSicurezzaMessaggioCertificatiKeyStorePassword(),
								NegoziazioneTokenDynamicParametersModIUtils.getSicurezzaMessaggioCertificatiKeyAlias(),
								NegoziazioneTokenDynamicParametersModIUtils.getSicurezzaMessaggioCertificatiKeyPassword());
					}catch(Exception t) {
						throw new TokenException(prefixError+"non è utilizzabile: "+t.getMessage(),t);
					}
					
					listProtocolPropertiesFruizione = ProtocolPropertiesUtils.getProtocolProperties(true, this.idFruizione.getIdFruitore(), asps);
				}
			}
			
			if(policyNegoziazioneToken.isJwtSignIncludeKeyIdApplicativoModI()) {
				String prefixError = "La modalità di generazione del Key Id (kid), indicata nella token policy '"+policyNegoziazioneToken.getName()+"', "; 
				if(this.applicativoRichiedente==null) {
					throw new TokenException(prefixError+"richiede l'autenticazione e l'identificazione di un applicativo fruitore");
				}
				String kidApplicativoModIConfig = ProtocolPropertiesUtils.getOptionalStringValuePropertyConfig(this.applicativoRichiedente.getProtocolPropertyList(), CostantiDB.MODIPA_SICUREZZA_TOKEN_KID_ID);
				if(kidApplicativoModIConfig==null || StringUtils.isEmpty(kidApplicativoModIConfig)) {
					throw new TokenException(prefixError+"non è utilizzabile con l'applicativo identificato '"+idApp+
							"': nella configurazione dell'applicativo non è stato definito un '"+CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_KID+
							"' nella sezione '"+CostantiLabel.MODIPA_SICUREZZA_TOKEN_SUBTITLE_LABEL+"'");
				}
				this.kidApplicativoModI = kidApplicativoModIConfig;
				if(this.kidApplicativoModI!=null && !"".equals(this.kidApplicativoModI) && !Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(this.kidApplicativoModI)) {
					this.kidApplicativoModI = DynamicUtils.convertDynamicPropertyValue("kid.gwt", this.kidApplicativoModI, dynamicMap, pddContext);	
				}
			}
			else if(policyNegoziazioneToken.isJwtSignIncludeKeyIdFruizioneModI()) {
				String prefixError = "La modalità di generazione del Key Id (kid), indicata nella token policy '"+policyNegoziazioneToken.getName()+"', "; 
				if(this.idFruizione==null) {
					throw new TokenException(prefixError+"richiede l'identificazione dei dati di una fruizione");
				}
				String kidFruizioneModIConfig = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(listProtocolPropertiesFruizione, CostantiDB.MODIPA_PROFILO_SICUREZZA_OAUTH_KID);
				if(kidFruizioneModIConfig==null || StringUtils.isEmpty(kidFruizioneModIConfig)) {
					throw new TokenException(prefixError+"non è utilizzabile con la fruizione '"+idFruizioneLabel+
							"': nella configurazione 'ModI' non è stato definito un '"+CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_KID+"'");
				}
				this.kidFruizioneModI = kidFruizioneModIConfig;
				if(this.kidFruizioneModI!=null && !"".equals(this.kidFruizioneModI) && !Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(this.kidFruizioneModI)) {
					this.kidFruizioneModI = DynamicUtils.convertDynamicPropertyValue("kid.gwt", this.kidFruizioneModI, dynamicMap, pddContext);	
				}
			}
			
			if(policyNegoziazioneToken.isJwtIssuerApplicativoModI()) {
				String prefixError = "La modalità di generazione dell'Issuer, indicata nella token policy '"+policyNegoziazioneToken.getName()+"', "; 
				if(this.applicativoRichiedente==null) {
					throw new TokenException(prefixError+"richiede l'autenticazione e l'identificazione di un applicativo fruitore");
				}
				clientIdApplicativoModI = ProtocolPropertiesUtils.getOptionalStringValuePropertyConfig(this.applicativoRichiedente.getProtocolPropertyList(), CostantiDB.MODIPA_SICUREZZA_TOKEN_CLIENT_ID);
				if(clientIdApplicativoModI==null || StringUtils.isEmpty(clientIdApplicativoModI)) {
					throw new TokenException(prefixError+"non è utilizzabile con l'applicativo identificato '"+idApp+
							"': nella configurazione dell'applicativo non è stato definito un '"+CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_CLIENT_ID+
							"' nella sezione '"+CostantiLabel.MODIPA_SICUREZZA_TOKEN_SUBTITLE_LABEL+"'");
				}
				this.signedJwtIssuer = clientIdApplicativoModI;
			}
			else if(policyNegoziazioneToken.isJwtIssuerFruizioneModI()) {
				String prefixError = "La modalità di generazione dell'Issuer, indicata nella token policy '"+policyNegoziazioneToken.getName()+"', "; 
				if(this.idFruizione==null) {
					throw new TokenException(prefixError+"richiede l'identificazione dei dati di una fruizione");
				}
				clientIdFruizioneModI = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(listProtocolPropertiesFruizione, CostantiDB.MODIPA_PROFILO_SICUREZZA_OAUTH_IDENTIFICATIVO);
				if(clientIdFruizioneModI==null || StringUtils.isEmpty(clientIdFruizioneModI)) {
					throw new TokenException(prefixError+"non è utilizzabile con la fruizione '"+idFruizioneLabel+
							"': nella configurazione 'ModI' non è stato definito un '"+CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_CLIENT_ID+
							" "+CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_CLIENT_ID_SEARCH+"'");
				}
				this.signedJwtIssuer = clientIdFruizioneModI;
			}
			else {
				this.signedJwtIssuer = policyNegoziazioneToken.getJwtIssuer();
			}
			if(this.signedJwtIssuer!=null && !"".equals(this.signedJwtIssuer) && !Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(this.signedJwtIssuer)) {
				this.signedJwtIssuer = DynamicUtils.convertDynamicPropertyValue("issuer.gwt", this.signedJwtIssuer, dynamicMap, pddContext);	
			}
			
			
			if(policyNegoziazioneToken.isJwtClientIdApplicativoModI()) {
				if(clientIdApplicativoModI==null) {
					String prefixError = "La modalità di generazione del Client ID, indicata nella token policy '"+policyNegoziazioneToken.getName()+"', "; 
					if(this.applicativoRichiedente==null) {
						throw new TokenException(prefixError+"richiede l'autenticazione e l'identificazione di un applicativo fruitore");
					}
					String clientId = ProtocolPropertiesUtils.getOptionalStringValuePropertyConfig(this.applicativoRichiedente.getProtocolPropertyList(), CostantiDB.MODIPA_SICUREZZA_TOKEN_CLIENT_ID);
					if(clientId==null || StringUtils.isEmpty(clientId)) {
						throw new TokenException(prefixError+"non è utilizzabile con l'applicativo identificato '"+idApp+
								"': nella configurazione dell'applicativo non è stato definito un '"+CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_CLIENT_ID+
								"' nella sezione '"+CostantiLabel.MODIPA_SICUREZZA_TOKEN_SUBTITLE_LABEL+"'");
					}
					clientIdApplicativoModI = clientId;
				}
				this.signedJwtClientId = clientIdApplicativoModI;
			}
			else if(policyNegoziazioneToken.isJwtClientIdFruizioneModI()) {
				if(clientIdFruizioneModI==null) {
					String prefixError = "La modalità di generazione del Client ID, indicata nella token policy '"+policyNegoziazioneToken.getName()+"', "; 
					if(this.idFruizione==null) {
						throw new TokenException(prefixError+"richiede l'identificazione dei dati di una fruizione");
					}
					String clientId = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(listProtocolPropertiesFruizione, CostantiDB.MODIPA_PROFILO_SICUREZZA_OAUTH_IDENTIFICATIVO);
					if(clientId==null || StringUtils.isEmpty(clientId)) {
						throw new TokenException(prefixError+"non è utilizzabile con la fruizione '"+idFruizioneLabel+
								"': nella configurazione 'ModI' non è stato definito un '"+CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_CLIENT_ID+
								" "+CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_CLIENT_ID_SEARCH+"'");
					}
					clientIdFruizioneModI = clientId;
				}
				this.signedJwtClientId = clientIdFruizioneModI;
			}
			else {
				this.signedJwtClientId = policyNegoziazioneToken.getJwtClientId();
			}
			if(this.signedJwtClientId!=null && !"".equals(this.signedJwtClientId) && !Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(this.signedJwtClientId)) {
				this.signedJwtClientId = DynamicUtils.convertDynamicPropertyValue("clientId.gwt", this.signedJwtClientId, dynamicMap, pddContext);	
			}
			
			
			if(policyNegoziazioneToken.isJwtSubjectApplicativoModI()) {
				if(clientIdApplicativoModI==null) {
					String prefixError = "La modalità di generazione del Subject, indicata nella token policy '"+policyNegoziazioneToken.getName()+"', "; 
					if(this.applicativoRichiedente==null) {
						throw new TokenException(prefixError+"richiede l'autenticazione e l'identificazione di un applicativo fruitore");
					}
					String clientId = ProtocolPropertiesUtils.getOptionalStringValuePropertyConfig(this.applicativoRichiedente.getProtocolPropertyList(), CostantiDB.MODIPA_SICUREZZA_TOKEN_CLIENT_ID);
					if(clientId==null || StringUtils.isEmpty(clientId)) {
						throw new TokenException(prefixError+"non è utilizzabile con l'applicativo identificato '"+idApp+
								"': nella configurazione dell'applicativo non è stato definito un '"+CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_CLIENT_ID+
								"' nella sezione '"+CostantiLabel.MODIPA_SICUREZZA_TOKEN_SUBTITLE_LABEL+"'");
					}
					clientIdApplicativoModI = clientId;
				}
				this.signedJwtSubject = clientIdApplicativoModI;
			}
			else if(policyNegoziazioneToken.isJwtSubjectFruizioneModI()) {
				if(clientIdFruizioneModI==null) {
					String prefixError = "La modalità di generazione del Subject, indicata nella token policy '"+policyNegoziazioneToken.getName()+"', "; 
					if(this.idFruizione==null) {
						throw new TokenException(prefixError+"richiede l'identificazione dei dati di una fruizione");
					}
					String clientId = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(listProtocolPropertiesFruizione, CostantiDB.MODIPA_PROFILO_SICUREZZA_OAUTH_IDENTIFICATIVO);
					if(clientId==null || StringUtils.isEmpty(clientId)) {
						throw new TokenException(prefixError+"non è utilizzabile con la fruizione '"+idFruizioneLabel+
								"': nella configurazione 'ModI' non è stato definito un '"+CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_CLIENT_ID+
								" "+CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_CLIENT_ID_SEARCH+"'");
					}
					clientIdFruizioneModI = clientId;
				}
				this.signedJwtSubject = clientIdFruizioneModI;
			}
			else {
				this.signedJwtSubject = policyNegoziazioneToken.getJwtSubject();
			}
			if(this.signedJwtSubject!=null && !"".equals(this.signedJwtSubject) && !Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(this.signedJwtSubject)) {
				this.signedJwtSubject = DynamicUtils.convertDynamicPropertyValue("subject.gwt", this.signedJwtSubject, dynamicMap, pddContext);	
			}
			
			this.signedJwtAudience = policyNegoziazioneToken.getJwtAudience();
			if(this.signedJwtAudience!=null && !"".equals(this.signedJwtAudience) && !Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(this.signedJwtAudience)) {
				this.signedJwtAudience = DynamicUtils.convertDynamicPropertyValue("jwtAudience.gwt", this.signedJwtAudience, dynamicMap, pddContext);	
			}
			
			this.signedJwtJti = policyNegoziazioneToken.getJwtIdentifier();
			if(this.signedJwtJti!=null && !"".equals(this.signedJwtJti) && !Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(this.signedJwtJti)) {
				this.signedJwtJti = DynamicUtils.convertDynamicPropertyValue("jti.gwt", this.signedJwtJti, dynamicMap, pddContext);	
			}
			
			if(policyNegoziazioneToken.isPDND()) {
				
				this.signedJwtPurposeId = policyNegoziazioneToken.getJwtPurposeId();
				if(this.signedJwtPurposeId!=null && !"".equals(this.signedJwtPurposeId)) {
					this.signedJwtPurposeId = DynamicUtils.convertDynamicPropertyValue("jwtPurposeId.gwt", this.signedJwtPurposeId, dynamicMap, pddContext);	
				}
				
				this.signedJwtSessionInfo = policyNegoziazioneToken.getJwtSessionInfo();
				if(this.signedJwtSessionInfo!=null && !"".equals(this.signedJwtSessionInfo)) {
					this.signedJwtSessionInfo = DynamicUtils.convertDynamicPropertyValue("sessionInfo.gwt", this.signedJwtSessionInfo, dynamicMap, pddContext);	
				}
				
			}
			
			this.signedJwtClaims = policyNegoziazioneToken.getJwtClaims();
			if(this.signedJwtClaims!=null && !"".equals(this.signedJwtClaims)) {
				this.signedJwtClaims = DynamicUtils.convertDynamicPropertyValue("claims.gwt", this.signedJwtClaims, dynamicMap, pddContext);	
			}
			
			this.signedJwtCustomId = policyNegoziazioneToken.getJwtSignIncludeKeyIdCustom();
			if(this.signedJwtCustomId!=null && !"".equals(this.signedJwtCustomId)) {
				this.signedJwtCustomId = DynamicUtils.convertDynamicPropertyValue("kid.customId.gwt", this.signedJwtCustomId, dynamicMap, pddContext);	
			}
			
			this.signedJwtX509Url = policyNegoziazioneToken.getJwtSignIncludeX509URL();
			if(this.signedJwtX509Url!=null && !"".equals(this.signedJwtX509Url)) {
				this.signedJwtX509Url = DynamicUtils.convertDynamicPropertyValue("url-x5u.gwt", this.signedJwtX509Url, dynamicMap, pddContext);	
			}
		}
		
		this.scope = policyNegoziazioneToken.getScopeString();
		if(this.scope!=null && !"".equals(this.scope)) {
			this.scope = DynamicUtils.convertDynamicPropertyValue("scopes.gwt", this.scope, dynamicMap, pddContext);	
		}
		
		this.audience = policyNegoziazioneToken.getAudience();
		if(this.audience!=null && !"".equals(this.audience)) {
			this.audience = DynamicUtils.convertDynamicPropertyValue("aud.gwt", this.audience, dynamicMap, pddContext);	
		}
		
		if(policyNegoziazioneToken.isPDND()) {
			
			if(policyNegoziazioneToken.isFormClientIdApplicativoModI()) {
				if(clientIdApplicativoModI==null) {
					String prefixError = "La modalità di generazione del Client ID nei dati della richiesta, indicata nella token policy '"+policyNegoziazioneToken.getName()+"', "; 
					if(this.applicativoRichiedente==null) {
						throw new TokenException(prefixError+"richiede l'autenticazione e l'identificazione di un applicativo fruitore");
					}
					String clientId = ProtocolPropertiesUtils.getOptionalStringValuePropertyConfig(this.applicativoRichiedente.getProtocolPropertyList(), CostantiDB.MODIPA_SICUREZZA_TOKEN_CLIENT_ID);
					if(clientId==null || StringUtils.isEmpty(clientId)) {
						throw new TokenException(prefixError+"non è utilizzabile con l'applicativo identificato '"+idApp+
								"': nella configurazione dell'applicativo non è stato definito un '"+CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_CLIENT_ID+
								"' nella sezione '"+CostantiLabel.MODIPA_SICUREZZA_TOKEN_SUBTITLE_LABEL+"'");
					}
					clientIdApplicativoModI = clientId;
				}
				this.formClientId = clientIdApplicativoModI;
			}
			else if(policyNegoziazioneToken.isFormClientIdFruizioneModI()) {
				if(clientIdFruizioneModI==null) {
					String prefixError = "La modalità di generazione del Client ID nei dati della richiesta, indicata nella token policy '"+policyNegoziazioneToken.getName()+"', "; 
					if(this.idFruizione==null) {
						throw new TokenException(prefixError+"richiede l'identificazione dei dati di una fruizione");
					}
					String clientId = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(listProtocolPropertiesFruizione, CostantiDB.MODIPA_PROFILO_SICUREZZA_OAUTH_IDENTIFICATIVO);
					if(clientId==null || StringUtils.isEmpty(clientId)) {
						throw new TokenException(prefixError+"non è utilizzabile con la fruizione '"+idFruizioneLabel+
								"': nella configurazione 'ModI' non è stato definito un '"+CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_CLIENT_ID+
								" "+CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_CLIENT_ID_SEARCH+"'");
					}
					clientIdFruizioneModI = clientId;
				}
				this.formClientId = clientIdFruizioneModI;
			}
			else {
				this.formClientId = policyNegoziazioneToken.getFormClientId();
				if(this.formClientId==null || "".equals(this.formClientId)) {
					this.formClientId = policyNegoziazioneToken.getJwtClientId();
				}
			}
			if(this.formClientId!=null && !"".equals(this.formClientId) && !Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(this.formClientId)) {
				this.formClientId = DynamicUtils.convertDynamicPropertyValue("formClientId.gwt", this.formClientId, dynamicMap, pddContext);	
			}
			
			this.formResource = policyNegoziazioneToken.getFormResource();
			if(this.formResource!=null && !"".equals(this.formResource) && !Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(this.formResource)) {
				this.formResource = DynamicUtils.convertDynamicPropertyValue("formResource.gwt", this.formResource, dynamicMap, pddContext);	
			}
		}
		
		this.parameters = policyNegoziazioneToken.getFormParameters();
		if(this.parameters!=null && !"".equals(this.parameters)) {
			this.parameters = DynamicUtils.convertDynamicPropertyValue("parameters.gwt", this.parameters, dynamicMap, pddContext);	
		}
		
		String sHttpMethod = policyNegoziazioneToken.getHttpMethod();
		if(sHttpMethod!=null && !"".equals(sHttpMethod)) {
			// per adesso viene preso da una select list
			/** this.httpMethod = DynamicUtils.convertDynamicPropertyValue("httpMethod.gwt", sHttpMethod, dynamicMap, pddContext); */
			this.httpMethod = HttpRequestMethod.valueOf(sHttpMethod);
		}
		
		this.httpContentType = policyNegoziazioneToken.getHttpContentType();
		if(this.httpContentType!=null && !"".equals(this.httpContentType)) {
			this.httpContentType = DynamicUtils.convertDynamicPropertyValue("httpContentType.gwt", this.httpContentType, dynamicMap, pddContext);	
		}
		
		this.httpHeaders = policyNegoziazioneToken.getHttpHeaders();
		if(this.httpHeaders!=null && !"".equals(this.httpHeaders)) {
			this.httpHeaders = DynamicUtils.convertDynamicPropertyValue("httpHeaders.gwt", this.httpHeaders, dynamicMap, pddContext);	
		}
		
		this.httpPayloadTemplateType = policyNegoziazioneToken.getDynamicPayloadType();
		if(this.httpPayloadTemplateType!=null && !"".equals(this.httpPayloadTemplateType)) {
			// Non è un valore dinamico, ma serve come info da far apparire nella chiave per tipizzare il payload
			/** this.httpPayloadTemplateType = DynamicUtils.convertDynamicPropertyValue("httpPayloadTemplateType.gwt", this.httpPayloadTemplateType, dynamicMap, pddContext);*/	
			String httpPayloadDynamic = policyNegoziazioneToken.getDynamicPayload();
			if(httpPayloadDynamic!=null && !"".equals(httpPayloadDynamic)) {
				if(policyNegoziazioneToken.isDynamicPayloadTemplate()) {
					this.httpPayload = DynamicUtils.convertDynamicPropertyValue("httpPayload.gwt", httpPayloadDynamic, dynamicMap, pddContext);
				}
				else if(policyNegoziazioneToken.isDynamicPayloadFreemarkerTemplate()) {
					ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(state);
						
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					try {
						Template template = configurazionePdDManager.getTemplatePolicyNegoziazioneRequest(policyNegoziazioneToken.getName(), httpPayloadDynamic.getBytes(), requestInfo);
						DynamicUtils.convertFreeMarkerTemplate(template, dynamicMap, bout);
						bout.flush();
						bout.close();
					}catch(Exception e) {
						throw new TokenException(e.getMessage(),e);
					}
					this.httpPayload = bout.toString();
				}
				else if(policyNegoziazioneToken.isDynamicPayloadVelocityTemplate()) {
					ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(state);
						
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					try {
						Template template = configurazionePdDManager.getTemplatePolicyNegoziazioneRequest(policyNegoziazioneToken.getName(), httpPayloadDynamic.getBytes(), requestInfo);
						DynamicUtils.convertVelocityTemplate(template, dynamicMap, bout);
						bout.flush();
						bout.close();
					}catch(Exception e) {
						throw new TokenException(e.getMessage(),e);
					}
					this.httpPayload = bout.toString();
				}
			}
		}
	}
	
	@Override
	protected String toStringRepresentation() {
		return null; // viene ridefinito il metodo toString
	}
	@Override
	public String toString() {
		return toString("\n", false);
	}
	public String toString(String separator, boolean cacheKey) {
		StringBuilder sb = new StringBuilder();
		String superS = super.toString();
		if(StringUtils.isNotEmpty(superS)) {
			sb.append(superS);
		}
		
		if(cacheKey) {
			checkInitCacheKey();
		}
		
		if(StringUtils.isNotEmpty(this.endpoint)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("endpoint:").append(this.endpoint);
		}
		if(StringUtils.isNotEmpty(this.basicUsername)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("user:").append(this.basicUsername);
		}
		if(StringUtils.isNotEmpty(this.basicPassword) && (!cacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("password:").append(this.basicPassword);
		}
		if(StringUtils.isNotEmpty(this.bearerToken)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("token:").append(this.bearerToken);
		}
		
		if(StringUtils.isNotEmpty(this.usernamePasswordGrantUsername)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("usernamePasswordGrant_username:").append(this.usernamePasswordGrantUsername);
		}
		if(StringUtils.isNotEmpty(this.usernamePasswordGrantPassword) && (!cacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("usernamePasswordGrant_password:").append(this.usernamePasswordGrantPassword);
		}
		
		if(StringUtils.isNotEmpty(this.signedJwtIssuer) && (!cacheKey || signedJwtIssuerCacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("iss:").append(this.signedJwtIssuer);
		}
		if(StringUtils.isNotEmpty(this.signedJwtClientId) && (!cacheKey || signedJwtClientIdCacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("clientId:").append(this.signedJwtClientId);
		}
		if(StringUtils.isNotEmpty(this.signedJwtSubject) && (!cacheKey || signedJwtSubjectCacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("sub:").append(this.signedJwtSubject);
		}
		if(StringUtils.isNotEmpty(this.signedJwtAudience) && (!cacheKey || signedJwtAudienceCacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("aud:").append(this.signedJwtAudience);
		}
		if(StringUtils.isNotEmpty(this.signedJwtJti) && (!cacheKey || signedJwtJtiCacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("jti:").append(this.signedJwtJti);
		}
		if(StringUtils.isNotEmpty(this.signedJwtPurposeId) && (!cacheKey || signedJwtPurposeIdCacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("purposeId:").append(this.signedJwtPurposeId);
		}
		if(StringUtils.isNotEmpty(this.signedJwtSessionInfo) && (!cacheKey || signedJwtSessionInfoCacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("sInfo:").append(this.signedJwtSessionInfo);
		}
		if(StringUtils.isNotEmpty(this.signedJwtClaims) && (!cacheKey || signedJwtClaimsCacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("claims:").append(this.signedJwtClaims);
		}
		
		if(StringUtils.isNotEmpty(this.signedJwtCustomId) && (!cacheKey || signedJwtCustomIdCacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("kid:").append(this.signedJwtCustomId);
		}
		if(StringUtils.isNotEmpty(this.signedJwtX509Url) && (!cacheKey || signedJwtX509UrlCacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("x5u:").append(this.signedJwtX509Url);
		}
		
		if(StringUtils.isNotEmpty(this.scope) && (!cacheKey || scopeCacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("scope:").append(this.scope);
		}
		if(StringUtils.isNotEmpty(this.audience) && (!cacheKey || audienceCacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("formAudience:").append(this.audience);
		}
		if(StringUtils.isNotEmpty(this.formClientId) && (!cacheKey || formClientIdCacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("formClientId:").append(this.formClientId);
		}
		if(StringUtils.isNotEmpty(this.formResource) && (!cacheKey || formResourceCacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("formResource:").append(this.formResource);
		}
		if(StringUtils.isNotEmpty(this.parameters) && (!cacheKey || parametersCacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("parameters:").append(this.parameters);
		}
		
		if(this.httpMethod!=null && (!cacheKey || httpMethodCacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("httpMethod:").append(this.httpMethod.name());
		}
		if(StringUtils.isNotEmpty(this.httpContentType) && (!cacheKey || httpContentTypeCacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("httpContentType:").append(this.httpContentType);
		}
		if(StringUtils.isNotEmpty(this.httpHeaders) && (!cacheKey || httpHeadersCacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("httpHeaders:").append(this.httpHeaders);
		}
		if(StringUtils.isNotEmpty(this.httpPayloadTemplateType) && (!cacheKey || httpPayloadTemplateTypeCacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("httpPayloadTemplateType:").append(this.httpPayloadTemplateType);
		}
		if(StringUtils.isNotEmpty(this.httpPayload) && (!cacheKey || httpPayloadCacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("httpPayload:").append(this.httpPayload);
		}
		
		if(this.idApplicativoRichiedente!=null && (!cacheKey || applicativoRichiedenteCacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("applicativeRequester:").append(this.idApplicativoRichiedente.toFormatString());
		}
		
		if(this.idFruizione!=null && (!cacheKey || fruizioneCacheKey)) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("outbound:").append(this.idFruizione.toFormatString());
		}
		
		return sb.toString();
	}
	
	public String getEndpoint() {
		return this.endpoint;
	}

	public String getBasicUsername() {
		return this.basicUsername;
	}
	public String getBasicPassword() {
		return this.basicPassword;
	}
	
	public String getBearerToken() {
		return this.bearerToken;
	}
	
	public String getUsernamePasswordGrantUsername() {
		return this.usernamePasswordGrantUsername;
	}
	public String getUsernamePasswordGrantPassword() {
		return this.usernamePasswordGrantPassword;
	}
	
	public String getSignedJwtIssuer() {
		return this.signedJwtIssuer;
	}

	public String getSignedJwtClientId() {
		return this.signedJwtClientId;
	}

	public String getSignedJwtSubject() {
		return this.signedJwtSubject;
	}

	public String getSignedJwtAudience() {
		return this.signedJwtAudience;
	}

	public String getSignedJwtJti() {
		return this.signedJwtJti;
	}

	public String getSignedJwtPurposeId() {
		return this.signedJwtPurposeId;
	}

	public String getSignedJwtSessionInfo() {
		return this.signedJwtSessionInfo;
	}

	public String getSignedJwtClaims() {
		return this.signedJwtClaims;
	}
	
	public String getSignedJwtCustomId() {
		return this.signedJwtCustomId;
	}
	
	public String getSignedJwtX509Url() {
		return this.signedJwtX509Url;
	}
	
	public String getScope() {
		return this.scope;
	}
	
	public String getAudience() {
		return this.audience;
	}

	public String getFormClientId() {
		return this.formClientId;
	}
	
	public String getFormResource() {
		return this.formResource;
	}

	public String getParameters() {
		return this.parameters;
	}
	
	public HttpRequestMethod getHttpMethod() {
		return this.httpMethod;
	}
	public String getHttpContentType() {
		return this.httpContentType;
	}
	public String getHttpHeaders() {
		return this.httpHeaders;
	}
	public String getHttpPayloadTemplateType() {
		return this.httpPayloadTemplateType;
	}
	public String getHttpPayload() {
		return this.httpPayload;
	}
	
	public IDServizioApplicativo getIdApplicativoRichiedente() {
		return this.idApplicativoRichiedente;
	}
	public ServizioApplicativo getApplicativoRichiedente() {
		return this.applicativoRichiedente;
	}
	public String getKidApplicativoModI() {
		return this.kidApplicativoModI;
	}
	public org.openspcoop2.utils.certificate.KeyStore getKeystoreApplicativoModI() throws TokenException, SecurityException{
		if(this.keystoreApplicativoModI.getSecurityMessageKeystorePath()!=null || this.keystoreApplicativoModI.isSecurityMessageKeystoreHSM()) {
			MerlinKeystore merlinKs = GestoreKeystoreCache.getMerlinKeystore(this.getRequestInfo(), this.keystoreApplicativoModI.getSecurityMessageKeystorePath(), this.keystoreApplicativoModI.getSecurityMessageKeystoreType(), 
					this.keystoreApplicativoModI.getSecurityMessageKeystorePassword());
			if(merlinKs==null) {
				throw new TokenException("Accesso al keystore '"+this.keystoreApplicativoModI.getSecurityMessageKeystorePath()+"' non riuscito");
			}
			return merlinKs.getKeyStore();
		}
		else {
			MerlinKeystore merlinKs = GestoreKeystoreCache.getMerlinKeystore(this.getRequestInfo(), this.keystoreApplicativoModI.getSecurityMessageKeystoreArchive(), this.keystoreApplicativoModI.getSecurityMessageKeystoreType(), 
					this.keystoreApplicativoModI.getSecurityMessageKeystorePassword());
			if(merlinKs==null) {
				throw new TokenException("Accesso al keystore non riuscito");
			}
			return merlinKs.getKeyStore();
		}
	}
	public String getKeyAliasApplicativoModI() {
		return this.keystoreApplicativoModI.getSecurityMessageKeyAlias();
	}
	public String getKeyPasswordApplicativoModI() {
		return this.keystoreApplicativoModI.getSecurityMessageKeyPassword();
	}
	
	public IDFruizione getIdFruizione() {
		return this.idFruizione;
	}
	public String getKidFruizioneModI() {
		return this.kidFruizioneModI;
	}
	public org.openspcoop2.utils.certificate.KeyStore getKeystoreFruizioneModI() throws TokenException, SecurityException{
		if(this.keystoreFruizioneModI.getSecurityMessageKeystorePath()!=null || this.keystoreFruizioneModI.isSecurityMessageKeystoreHSM()) {
			MerlinKeystore merlinKs = GestoreKeystoreCache.getMerlinKeystore(this.getRequestInfo(), this.keystoreFruizioneModI.getSecurityMessageKeystorePath(), this.keystoreFruizioneModI.getSecurityMessageKeystoreType(), 
					this.keystoreFruizioneModI.getSecurityMessageKeystorePassword());
			if(merlinKs==null) {
				throw new TokenException("Accesso al keystore '"+this.keystoreFruizioneModI.getSecurityMessageKeystorePath()+"' non riuscito");
			}
			return merlinKs.getKeyStore();
		}
		else {
			MerlinKeystore merlinKs = GestoreKeystoreCache.getMerlinKeystore(this.getRequestInfo(), this.keystoreFruizioneModI.getSecurityMessageKeystoreArchive(), this.keystoreFruizioneModI.getSecurityMessageKeystoreType(), 
					this.keystoreFruizioneModI.getSecurityMessageKeystorePassword());
			if(merlinKs==null) {
				throw new TokenException("Accesso al keystore non riuscito");
			}
			return merlinKs.getKeyStore();
		}
	}
	public String getKeyAliasFruizioneModI() {
		return this.keystoreFruizioneModI.getSecurityMessageKeyAlias();
	}
	public String getKeyPasswordFruizioneModI() {
		return this.keystoreFruizioneModI.getSecurityMessageKeyPassword();
	}
	
}
