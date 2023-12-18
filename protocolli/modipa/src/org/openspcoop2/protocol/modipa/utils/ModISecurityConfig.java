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

package org.openspcoop2.protocol.modipa.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.RuoloContesto;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.CostantiProprieta;
import org.openspcoop2.pdd.config.UrlInvocazioneAPI;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.autorizzazione.canali.CanaliUtils;
import org.openspcoop2.pdd.core.dynamic.DynamicHelperCostanti;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.core.token.parser.Claims;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.modipa.config.ModIAuditClaimConfig;
import org.openspcoop2.protocol.modipa.config.ModIAuditConfig;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModIConsoleCostanti;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.message.constants.SignatureDigestAlgorithm;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.digest.DigestEncoding;
import org.openspcoop2.utils.properties.PropertiesUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * ModIKeystoreConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModISecurityConfig {

	private String algorithm = null;
	private String apiSoapAlgorithmUsedOnlyForAudit = null;
	private String digestAlgorithm = null;
	private DigestEncoding digestEncoding = null;
	private List<DigestEncoding> digestEncodingAccepted = null;
	private String c14nAlgorithm = null;
	private boolean kid = false;
	private boolean x5c = false;
	private boolean x5t = false;
	private boolean x5u = false;
	private String x5url = null;
	private String keyIdentifierMode = null;
	private boolean useSingleCertificate = true;
	private boolean includeSignatureToken = false;
	private int ttl;
	private Long checkTtlIatMs;
	private String audience;
	private boolean checkAudience;
	private String clientId;
	private String issuer;
	private String subject;
	private List<String> httpHeaders = new ArrayList<>();
	private List<SOAPHeader> soapHeaders = new ArrayList<>();
	
	private boolean multipleHeaderUseSameJti = true;
	private boolean multipleHeaderUseJtiAuthorizationAsIdMessaggio = true;
	private Boolean multipleHeaderAuthorizationConfig = null;

	private Properties claims;
	private Properties multipleHeaderClaims;
	
	private List<String> corniceSicurezzaCodiceEnteRule = null;
	private List<String> corniceSicurezzaUserRule = null;
	private List<String> corniceSicurezzaIpUserRule = null;
	
	private ModIAuditConfig corniceSicurezzaSchemaConfig = null;
	private String corniceSicurezzaAudience;
	
	private boolean fruizione;
	private List<ProtocolProperty> listProtocolPropertiesInternal = null;
	
	private String tokenKid;
	private String tokenClientId;

	private static final String AUDIENCE_UNDEFINED = "Audience undefined";
	private static final String CONFIGURAZIONE_SICUREZZA_INCOMPLETA = "Configurazione della sicurezza incompleta";
	
	public ModISecurityConfig(OpenSPCoop2Message msg, Context context, IProtocolFactory<?> protocolFactory, IState state, RequestInfo requestInfo,
			IDSoggetto soggettoFruitore, AccordoServizioParteSpecifica aspsParam, ServizioApplicativo sa, 
			boolean rest, boolean fruizione,  boolean request, 
			String patternCorniceSicurezza, String schemaCorniceSicurezza, 
			Busta busta, Busta bustaRichiesta,
			Boolean multipleHeaderAuthorizationConfig,
			boolean keystoreDefinitoInFruizione, 
			boolean keystoreDefinitoInTokenPolicy, String tokenPolicyKid, String tokenPolicyClientId, 
			boolean kidMode,
			boolean addSecurity, boolean addAudit) throws ProtocolException {
		
		// METODO USATO IN IMBUSTAMENTO
		
		if(msg!=null && busta!=null) {
			// nop
		}
		
		this.kid = kidMode;
		
		this.fruizione = fruizione;
		
		this.multipleHeaderAuthorizationConfig = multipleHeaderAuthorizationConfig;
		
		ModIProperties modiProperties = ModIProperties.getInstance();
		
		IDServizio idServizio = null;
		try {
			idServizio = IDServizioFactory.getInstance().getIDServizioFromAccordo(aspsParam);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		
		Fruitore fruitore = null;
		if(fruizione) {
			if(soggettoFruitore==null) {
				throw new ProtocolException("Fruitore non fornito");
			}
			boolean find = false;
			for (Fruitore fruitoreCheck : aspsParam.getFruitoreList()) {
				if(fruitoreCheck.getTipo().equals(soggettoFruitore.getTipo()) && fruitoreCheck.getNome().equals(soggettoFruitore.getNome())) {
					fruitore = fruitoreCheck;
					this.listProtocolPropertiesInternal = fruitoreCheck.getProtocolPropertyList();
					find = true;
					break;
				}
			}
			if(!find) {
				throw new ProtocolException("Fruitore '"+soggettoFruitore+"' non registrato come fruitore dell'accordo parte specifica");
			}
		}
		else {
			this.listProtocolPropertiesInternal = aspsParam.getProtocolPropertyList();
		}
		
		if(this.listProtocolPropertiesInternal==null || this.listProtocolPropertiesInternal.isEmpty()) {
			throw new ProtocolException(CONFIGURAZIONE_SICUREZZA_INCOMPLETA);
		}
		
		if(addAudit && !addSecurity) {
			this.apiSoapAlgorithmUsedOnlyForAudit = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_ALG);
		}
		
		if(rest) {
			
			/* Opzioni REST */
						
			this.initSharedRest(modiProperties, this.listProtocolPropertiesInternal,sa,fruizione,request,keystoreDefinitoInFruizione, keystoreDefinitoInTokenPolicy);
			
			// HTTP Header da firmare
			
			String httpHeadersPropRegistry = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST);
			if(httpHeadersPropRegistry!=null && httpHeadersPropRegistry.length()>0) {
				this.httpHeaders = Arrays.asList(httpHeadersPropRegistry.split(","));
			}
			
			// Opzioni 'jti' in header contemporanei
			
			String idPropertyMultipleHeaderUseSameJti = (fruizione ? ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_JTI : ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_JTI);
			String multipleHeaderUseSameJtiPropRegistry = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, idPropertyMultipleHeaderUseSameJti);
			if(multipleHeaderUseSameJtiPropRegistry!=null) {
				this.multipleHeaderUseSameJti = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JTI_VALUE_SAME.equals(multipleHeaderUseSameJtiPropRegistry);
			}
			
			String idPropertyMultipleHeaderUseJtiAuthorizationAsIdMessaggio = (fruizione ? ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO : ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO);
			String multipleHeaderUseJtiAuthorizationAsIdMessaggioPropRegistry = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, idPropertyMultipleHeaderUseJtiAuthorizationAsIdMessaggio);
			if(multipleHeaderUseJtiAuthorizationAsIdMessaggioPropRegistry!=null) {
				this.multipleHeaderUseJtiAuthorizationAsIdMessaggio = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_VALUE_AUTHORIZATION.equals(multipleHeaderUseJtiAuthorizationAsIdMessaggioPropRegistry);
			}
			
			// Claims custom
			
			String idPropertyClaims = (fruizione ? ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_JWT_CLAIMS : ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_JWT_CLAIMS);
			String claimsPropRegistry = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, idPropertyClaims);
			if(claimsPropRegistry!=null && claimsPropRegistry.length()>0) {
				this.claims = PropertiesUtilities.convertTextToProperties(claimsPropRegistry);
			}
			
			// Custom claims per header contemporanei
			if(this.multipleHeaderAuthorizationConfig!=null) {

				String idPropertyMultipleHeaderClaims = null;
				if(this.multipleHeaderAuthorizationConfig.booleanValue()) {
					idPropertyMultipleHeaderClaims = (this.fruizione ? ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION : ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION);
				}
				else {
					idPropertyMultipleHeaderClaims = (this.fruizione ? ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_JWT_CLAIMS_MODI : ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_JWT_CLAIMS_MODI);
				}
				String multipleHeaderClaimsAuthorization = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, idPropertyMultipleHeaderClaims);
				if(multipleHeaderClaimsAuthorization!=null && multipleHeaderClaimsAuthorization.length()>0) {
					this.multipleHeaderClaims = PropertiesUtilities.convertTextToProperties(multipleHeaderClaimsAuthorization);
				}
				
			}
						
		}
		else {
			
			/* Opzioni SOAP */
			
			this.initSharedSoap(this.listProtocolPropertiesInternal,fruizione,request);
			
			// Header SOAP da firmare
			
			String soapHeadersPropRegistry = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_SOAP);
			if(soapHeadersPropRegistry!=null && soapHeadersPropRegistry.length()>0) {
				this.soapHeaders = SOAPHeader.parse(soapHeadersPropRegistry);
			}
		}
				
		/* TTL */
		
		if(fruizione) {
			if(request) {
				boolean greatherThanZero = true;
				this.ttl = ProtocolPropertiesUtils.getRequiredNumberValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_EXPIRED, greatherThanZero).intValue();
			}
		}
		else {
			if(!request) {
				boolean greatherThanZero = true;
				this.ttl = ProtocolPropertiesUtils.getRequiredNumberValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_EXPIRED, greatherThanZero).intValue();
			}
		}
		
		if(fruizione && request) {
			
			/* Audience */
			
			if(this.multipleHeaderAuthorizationConfig!=null && !this.multipleHeaderAuthorizationConfig) {
				String modalita = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_AUDIENCE);
				if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_AUDIENCE_VALUE_DIFFERENT.equals(modalita)) {
					this.audience = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_AUDIENCE_INTEGRITY);
				}
			}
			if(this.audience==null) {
				this.audience = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE);
			}
			if(this.audience==null &&
				fruitore.getConnettore()!=null && fruitore.getConnettore().sizePropertyList()>0) {
				for (Property p : fruitore.getConnettore().getPropertyList()) {
					if(CostantiDB.CONNETTORE_HTTP_LOCATION.equals(p.getNome())) {
						this.audience = p.getValore();
					}
				}
			}
			if(this.audience==null) {
				throw new ProtocolException(AUDIENCE_UNDEFINED);
			}
			
			
			/* ClientId */
			
			if(rest) {
				// 0. Vedo se definito in claims
				if(this.multipleHeaderAuthorizationConfig!=null &&
						ModIUtilities.exists(this.multipleHeaderClaims, Claims.INTROSPECTION_RESPONSE_RFC_7662_CLIENT_ID)) {
					this.clientId = ModIUtilities.get(this.multipleHeaderClaims, Claims.INTROSPECTION_RESPONSE_RFC_7662_CLIENT_ID, 
							ModIUtilities.REMOVE);
					if(this.clientId!=null && !DynamicHelperCostanti.NOT_GENERATE.equalsIgnoreCase(this.clientId)) {
						// non utilizzabile nella richiesta. Si imposta dall'applicativo
						this.clientId = null;
					}
				}
				if(this.clientId==null &&
					ModIUtilities.exists(this.claims, Claims.INTROSPECTION_RESPONSE_RFC_7662_CLIENT_ID)) {
					this.clientId = ModIUtilities.get(this.claims, Claims.INTROSPECTION_RESPONSE_RFC_7662_CLIENT_ID, 
							ModIUtilities.REMOVE);
					if(this.clientId!=null && !DynamicHelperCostanti.NOT_GENERATE.equalsIgnoreCase(this.clientId)) {
						// non utilizzabile nella richiesta. Si imposta dall'applicativo
						this.clientId = null;
					}
				}
			}
			if(this.clientId==null) {
				if(keystoreDefinitoInFruizione || keystoreDefinitoInTokenPolicy) {
					try {
						this.clientId = NamingUtils.getLabelAccordoServizioParteSpecificaSenzaErogatore(idServizio).replace(" ", "/");
					}catch(Exception e) {
						throw new ProtocolException(e.getMessage(),e);
					}
				}
				else if(sa!=null) {
					this.clientId = ProtocolPropertiesUtils.getOptionalStringValuePropertyConfig(sa.getProtocolPropertyList(), ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE);
					if(this.clientId==null) {
						try {
							this.clientId = NamingUtils.getLabelSoggetto(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()))+"/"+sa.getNome();
						}catch(Exception e) {
							throw new ProtocolException(e.getMessage(),e);
						}
					}
				}
			}
			
			
			/* OAuth - ClientId */
			if(this.tokenClientId==null) {
				if(keystoreDefinitoInFruizione) {
					this.tokenClientId = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_OAUTH_IDENTIFICATIVO);
				}
				else if(keystoreDefinitoInTokenPolicy) {
					this.tokenClientId = tokenPolicyClientId;
				}
				else if(sa!=null) {
					this.tokenClientId = ProtocolPropertiesUtils.getOptionalStringValuePropertyConfig(sa.getProtocolPropertyList(), ModICostanti.MODIPA_SICUREZZA_TOKEN_CLIENT_ID);
				}
			}
			
			/* OAuth - KID */
			
			if(this.tokenKid==null) {
				if(keystoreDefinitoInFruizione) {
					this.tokenKid = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_OAUTH_KID);
				}
				else if(keystoreDefinitoInTokenPolicy) {
					this.tokenKid = tokenPolicyKid;
				}
				else if(sa!=null) {
					this.tokenKid = ProtocolPropertiesUtils.getOptionalStringValuePropertyConfig(sa.getProtocolPropertyList(), ModICostanti.MODIPA_SICUREZZA_TOKEN_KID_ID);
				}
			}
			
			
			
			/* Issuer */
			
			try {
				if(this.multipleHeaderAuthorizationConfig!=null && 
						ModIUtilities.exists(this.multipleHeaderClaims, Claims.INTROSPECTION_RESPONSE_RFC_7662_ISSUER)) {
					this.issuer = ModIUtilities.get(this.multipleHeaderClaims, Claims.INTROSPECTION_RESPONSE_RFC_7662_ISSUER, 
							!ModIUtilities.REMOVE); // potrebbe servire nella cornice di sicurezza 
				}
				else if(ModIUtilities.exists(this.claims, Claims.INTROSPECTION_RESPONSE_RFC_7662_ISSUER)) {
					this.issuer = ModIUtilities.get(this.claims, Claims.INTROSPECTION_RESPONSE_RFC_7662_ISSUER, 
							!ModIUtilities.REMOVE); // potrebbe servire nella cornice di sicurezza 
				}
				/**else if(kidMode && this.tokenClientId!=null && StringUtils.isNotEmpty(this.tokenClientId)) {
					this.issuer = this.tokenClientId;
				}*/
				else if(modiProperties.isRestSecurityTokenClaimsIssuerEnabled()) {
					String valore = modiProperties.getRestSecurityTokenClaimsIssuerHeaderValue();
					if(valore!=null && !"".equals(valore)) {
						this.issuer = valore;
					}
					else {
						if(keystoreDefinitoInFruizione || keystoreDefinitoInTokenPolicy) {
							this.issuer = NamingUtils.getLabelSoggetto(soggettoFruitore);
						}
						else if(sa!=null) {
							this.issuer = NamingUtils.getLabelSoggetto(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
						}
					}
				}
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
			
			
			/* Subject */
			
			try {
				if(this.multipleHeaderAuthorizationConfig!=null && 
						ModIUtilities.exists(this.multipleHeaderClaims, Claims.INTROSPECTION_RESPONSE_RFC_7662_SUBJECT)) {
					this.subject = ModIUtilities.get(this.multipleHeaderClaims, Claims.INTROSPECTION_RESPONSE_RFC_7662_SUBJECT, 
							!ModIUtilities.REMOVE); // potrebbe servire nella cornice di sicurezza 
				}
				else if(ModIUtilities.exists(this.claims, Claims.INTROSPECTION_RESPONSE_RFC_7662_SUBJECT)) {
					this.subject = ModIUtilities.get(this.claims, Claims.INTROSPECTION_RESPONSE_RFC_7662_SUBJECT, 
							!ModIUtilities.REMOVE); // potrebbe servire nella cornice di sicurezza 
				}
				else if(modiProperties.isRestSecurityTokenClaimsSubjectEnabled()) {
					String valore = modiProperties.getRestSecurityTokenClaimsSubjectHeaderValue();
					if(valore!=null && !"".equals(valore)) {
						this.subject = valore;
					}
					else {
						if(keystoreDefinitoInFruizione || keystoreDefinitoInTokenPolicy) {
							this.subject = NamingUtils.getLabelAccordoServizioParteSpecificaSenzaErogatore(idServizio).replace(" ", "/");
						}
						else if(sa!=null) {
							this.subject = sa.getNome();
						}
					}
				}
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
			
			
			/* Cornice Sicurezza */
			if(patternCorniceSicurezza!=null){
				if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_OLD.equals(patternCorniceSicurezza)) {
					readCorniceSicurezzaCodiceEnteLegacy(modiProperties);
					readCorniceSicurezzaUserLegacy(modiProperties);
					readCorniceSicurezzaIpUserLegacy(modiProperties);
				}
				else {
					
					IConfigIntegrationReader configReader = protocolFactory.getCachedConfigIntegrationReader(state, requestInfo);
					
					PortaDelegata pd = null;
					if(msg!=null) {
						Object nomePortaInvocataObject = msg.getContextProperty(CostantiPdD.NOME_PORTA_INVOCATA);
						String nomePorta = null;
						if(nomePortaInvocataObject instanceof String) {
							nomePorta = (String) nomePortaInvocataObject;
						}
						if(nomePorta==null && context!=null && context.containsKey(CostantiPdD.NOME_PORTA_INVOCATA)) {
							nomePorta = (String) context.getObject(CostantiPdD.NOME_PORTA_INVOCATA);
						}
						if(nomePorta==null && requestInfo!=null && requestInfo.getProtocolContext()!=null) {
							nomePorta = requestInfo.getProtocolContext().getInterfaceName();
						}
						if(nomePorta!=null) {
							pd = getPortaDelegata(nomePorta, configReader);
						}
					}
					
					readCorniceSicurezzaSchema(modiProperties, schemaCorniceSicurezza, pd);
				}
			}
			
		}
		else if(!fruizione && !request) {
			
			/* Audience */
			
			// 0. Vedo se definito in claim
			if(this.multipleHeaderAuthorizationConfig!=null && 
					ModIUtilities.exists(this.multipleHeaderClaims, Claims.INTROSPECTION_RESPONSE_RFC_7662_AUDIENCE)) {
				this.audience = ModIUtilities.get(this.multipleHeaderClaims, Claims.INTROSPECTION_RESPONSE_RFC_7662_AUDIENCE, 
						ModIUtilities.REMOVE);
			}
			else if(ModIUtilities.exists(this.claims, Claims.INTROSPECTION_RESPONSE_RFC_7662_AUDIENCE)) {
				this.audience = ModIUtilities.get(this.claims, Claims.INTROSPECTION_RESPONSE_RFC_7662_AUDIENCE, 
						ModIUtilities.REMOVE);
			}
			
			// 1. Provo ad utilizzare quello definito nell'applicativo
			if(this.audience==null &&
				sa!=null) {
				this.audience = ProtocolPropertiesUtils.getOptionalStringValuePropertyConfig(sa.getProtocolPropertyList(), ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE);
			}
			
			// 2. Provo ad utilizzare il clientId presente nel token
			if(this.audience==null &&
				bustaRichiesta!=null) {
				this.audience = bustaRichiesta.getProperty(rest ? 
						ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_CLIENT_ID :
						ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_SOAP_WSA_FROM);
			}
			
			// 3. Provo ad utilizzare il sub presente nel token
			if(this.audience==null &&
				bustaRichiesta!=null) {
				this.audience = bustaRichiesta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_SUBJECT);
			}
			
			// Nel caso di sorgente non locale, ripeto i punti 2 e 3 per il token ricevuto
			if(kidMode) {
				// guardo 
				boolean sicurezzaToken = context!=null && context.containsKey(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE);
				if(sicurezzaToken) {
					InformazioniToken informazioniTokenNormalizzate = null;
					Object oInformazioniTokenNormalizzate = context.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE);
					if(oInformazioniTokenNormalizzate!=null) {
						informazioniTokenNormalizzate = (InformazioniToken) oInformazioniTokenNormalizzate;
					}
					if(informazioniTokenNormalizzate!=null) {
						if(informazioniTokenNormalizzate.getClientId()!=null && StringUtils.isNotEmpty(informazioniTokenNormalizzate.getClientId())) {
							this.audience = informazioniTokenNormalizzate.getClientId();
						}
						else if(informazioniTokenNormalizzate.getSub()!=null && StringUtils.isNotEmpty(informazioniTokenNormalizzate.getSub())) {
							this.audience = informazioniTokenNormalizzate.getSub();
						}
					}
				}
			}
			
			// 4. Utilizzo quello di default associato al 'sa' se identificato
			if(this.audience==null && 
				sa!=null) {
				try {
					this.audience=NamingUtils.getLabelSoggetto(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()))+"/"+sa.getNome();
				}catch(Exception e) {
					throw new ProtocolException(e.getMessage(),e);
				}
			}
			
			// 5. Utilizzo audience anonimo
			if(this.audience==null) {
				String soggettoMittente = null;
				if(soggettoFruitore!=null && soggettoFruitore.getNome()!=null) {
					try {
						this.audience=NamingUtils.getLabelSoggetto(soggettoFruitore);
					}catch(Exception e) {
						throw new ProtocolException(e.getMessage(),e);
					}
				}
				if(rest) {
					this.audience=modiProperties.getRestResponseSecurityTokenAudienceDefault(soggettoMittente);
				}
				else {
					this.audience=modiProperties.getSoapResponseSecurityTokenAudienceDefault(soggettoMittente);
				}
			}
			
			
			/* ClientId */
			try {
				// 0. Vedo se definito in claims
				if(this.multipleHeaderAuthorizationConfig!=null && 
						ModIUtilities.exists(this.multipleHeaderClaims, Claims.INTROSPECTION_RESPONSE_RFC_7662_CLIENT_ID)) {
					this.clientId = ModIUtilities.get(this.multipleHeaderClaims, Claims.INTROSPECTION_RESPONSE_RFC_7662_CLIENT_ID, 
							ModIUtilities.REMOVE);
				}
				else if(ModIUtilities.exists(this.claims, Claims.INTROSPECTION_RESPONSE_RFC_7662_CLIENT_ID)) {
					this.clientId = ModIUtilities.get(this.claims, Claims.INTROSPECTION_RESPONSE_RFC_7662_CLIENT_ID, 
							ModIUtilities.REMOVE);
				}
				else {
					this.clientId = NamingUtils.getLabelAccordoServizioParteSpecificaSenzaErogatore(idServizio).replace(" ", "/");
				}
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
			
			
			/* Issuer */
			
			try {
				if(this.multipleHeaderAuthorizationConfig!=null && 
						ModIUtilities.exists(this.multipleHeaderClaims, Claims.INTROSPECTION_RESPONSE_RFC_7662_ISSUER)) {
					this.issuer = ModIUtilities.get(this.multipleHeaderClaims, Claims.INTROSPECTION_RESPONSE_RFC_7662_ISSUER, ModIUtilities.REMOVE);
				}
				else if(ModIUtilities.exists(this.claims, Claims.INTROSPECTION_RESPONSE_RFC_7662_ISSUER)) {
					this.issuer = ModIUtilities.get(this.claims, Claims.INTROSPECTION_RESPONSE_RFC_7662_ISSUER, ModIUtilities.REMOVE);
				}
				else if(modiProperties.isRestSecurityTokenClaimsIssuerEnabled()) {
					String valore = modiProperties.getRestSecurityTokenClaimsIssuerHeaderValue();
					if(valore!=null && !"".equals(valore)) {
						this.issuer = valore;
					}
					else {
						this.issuer = NamingUtils.getLabelSoggetto(new IDSoggetto(aspsParam.getTipoSoggettoErogatore(), aspsParam.getNomeSoggettoErogatore()));
					}
				}
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
			
			/* Subject */
			
			try {
				if(this.multipleHeaderAuthorizationConfig!=null && 
						ModIUtilities.exists(this.multipleHeaderClaims, Claims.INTROSPECTION_RESPONSE_RFC_7662_SUBJECT)) {
					this.subject = ModIUtilities.get(this.multipleHeaderClaims, Claims.INTROSPECTION_RESPONSE_RFC_7662_SUBJECT, ModIUtilities.REMOVE);
				}
				else if(ModIUtilities.exists(this.claims, Claims.INTROSPECTION_RESPONSE_RFC_7662_SUBJECT)) {
					this.subject = ModIUtilities.get(this.claims, Claims.INTROSPECTION_RESPONSE_RFC_7662_SUBJECT, ModIUtilities.REMOVE);
				}
				else if(modiProperties.isRestSecurityTokenClaimsSubjectEnabled()) {
					String valore = modiProperties.getRestSecurityTokenClaimsSubjectHeaderValue();
					if(valore!=null && !"".equals(valore)) {
						this.subject = valore;
					}
					else {
						this.subject = NamingUtils.getLabelAccordoServizioParteSpecificaSenzaErogatore(idServizio).replace(" ", "/");
					}
				}
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
			
			/* Opzioni REST */
			
			if(rest) {

				String idPropertyMultipleHeaderUseJtiAuthorizationAsIdMessaggio = 
							ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_FILTRO_DUPLICATI;
				String multipleHeaderUseJtiAuthorizationAsIdMessaggioPropRegistry = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, idPropertyMultipleHeaderUseJtiAuthorizationAsIdMessaggio);
				if(multipleHeaderUseJtiAuthorizationAsIdMessaggioPropRegistry!=null) {
					this.multipleHeaderUseJtiAuthorizationAsIdMessaggio = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_FILTRO_DUPLICATI_VALUE_AUTHORIZATION.equals(multipleHeaderUseJtiAuthorizationAsIdMessaggioPropRegistry);
				}
				
				String keystoreMode = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, 
						CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE);
				boolean keystoreRidefinito = CostantiDB.MODIPA_PROFILO_RIDEFINISCI.equals(keystoreMode);
				
				/* OAuth - ClientId */
				
				if(this.tokenClientId==null) {
					if(keystoreRidefinito) {
						this.tokenClientId = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_OAUTH_IDENTIFICATIVO);
					}
					else {
						this.tokenClientId = modiProperties.getSicurezzaMessaggioCertificatiKeyClientId();
					}
				}
				
				/* OAuth - KID */
				
				if(this.tokenKid==null) {
					if(keystoreRidefinito) {
						this.tokenKid = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_OAUTH_KID);
					}
					else {
						this.tokenKid = modiProperties.getSicurezzaMessaggioCertificatiKeyKid();
					}
				}
				
			}
		}
		else {
			
			throw new ProtocolException("Use unsupported");
			
		}
		
		if(rest) {
			// Rimuovo iss e sub gestiti tramite proprieta specifiche
			if(this.multipleHeaderAuthorizationConfig!=null) {
				ModIUtilities.remove(this.multipleHeaderClaims, Claims.INTROSPECTION_RESPONSE_RFC_7662_AUDIENCE);
				ModIUtilities.remove(this.multipleHeaderClaims, Claims.INTROSPECTION_RESPONSE_RFC_7662_CLIENT_ID);
				ModIUtilities.remove(this.multipleHeaderClaims, Claims.INTROSPECTION_RESPONSE_RFC_7662_ISSUER);
				ModIUtilities.remove(this.multipleHeaderClaims, Claims.INTROSPECTION_RESPONSE_RFC_7662_SUBJECT);
			}
			ModIUtilities.remove(this.claims, Claims.INTROSPECTION_RESPONSE_RFC_7662_AUDIENCE);
			ModIUtilities.remove(this.claims, Claims.INTROSPECTION_RESPONSE_RFC_7662_CLIENT_ID);
			ModIUtilities.remove(this.claims, Claims.INTROSPECTION_RESPONSE_RFC_7662_ISSUER);
			ModIUtilities.remove(this.claims, Claims.INTROSPECTION_RESPONSE_RFC_7662_SUBJECT);
		}
		
	}
	private void readCorniceSicurezzaSchemaAudience(String schemaCorniceSicurezza) throws ProtocolException {
		if(schemaCorniceSicurezza!=null && StringUtils.isNotEmpty(schemaCorniceSicurezza)) {
			String modalita = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIT_AUDIENCE);
			if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIT_AUDIENCE_VALUE_DIFFERENT.equals(modalita)) {
				this.corniceSicurezzaAudience = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE_CUSTOM_AUDIT);
			}
			
			if(this.corniceSicurezzaAudience==null) {
				this.corniceSicurezzaAudience = this.audience; // inizializzato precedentemnte
			}
			if(this.corniceSicurezzaAudience==null) {
				throw new ProtocolException("Audit Audience undefined");
			}
		}
	}
	private void readCorniceSicurezzaSchema(ModIProperties modiProperties, String schemaCorniceSicurezza, PortaDelegata pd) throws ProtocolException {
		try {
			if(schemaCorniceSicurezza!=null && StringUtils.isNotEmpty(schemaCorniceSicurezza)) {
				
				/* Audience */
				readCorniceSicurezzaSchemaAudience(schemaCorniceSicurezza);

				/* Claims */
				readCorniceSicurezzaSchemaClaims(modiProperties, schemaCorniceSicurezza, pd);
			}
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	private void readCorniceSicurezzaSchemaClaimsReadPAProperties(ModIProperties modiProperties, String schemaCorniceSicurezza, PortaApplicativa pa) throws ProtocolException {
		if(modiProperties.getAuditConfig()!=null && !modiProperties.getAuditConfig().isEmpty()) {
			for (ModIAuditConfig modIAuditConfig : modiProperties.getAuditConfig()) {
				if(modIAuditConfig.getNome().equals(schemaCorniceSicurezza)) {
					this.corniceSicurezzaSchemaConfig = modIAuditConfig.copyNewInstance();
					readCorniceSicurezzaSchemaClaimsRuleReadPAProperties(this.corniceSicurezzaSchemaConfig.getClaims(), pa);
					break;
				}
			}
		}
	}
	private void readCorniceSicurezzaSchemaClaimsRuleReadPAProperties(List<ModIAuditClaimConfig> list, PortaApplicativa pa) {
		if(list!=null && !list.isEmpty()) {
			for (ModIAuditClaimConfig modIAuditClaimConfig : list) {
				readCorniceSicurezzaSchemaClaimRuleReadPAProperties(modIAuditClaimConfig, pa);
			}
		}
	}
	private void readCorniceSicurezzaSchemaClaimRuleReadPAProperties(ModIAuditClaimConfig claimConfig, PortaApplicativa pa) {
		
		boolean traceEnabled = claimConfig.isTrace();
		if(pa!=null) {
			traceEnabled = CostantiProprieta.isModIAuditTraceEnabled(pa.getProprietaList(), claimConfig.getNome(), traceEnabled);
		}
		claimConfig.setTrace(traceEnabled);
		
		boolean forwardEnabled = claimConfig.getForwardBackend()!=null && StringUtils.isNotEmpty(claimConfig.getForwardBackend());
		if(pa!=null) {
			forwardEnabled = CostantiProprieta.isModIAuditForwardBackendEnabled(pa.getProprietaList(), claimConfig.getNome(), forwardEnabled);
		}
		if(forwardEnabled) {
			String header = claimConfig.getForwardBackend();
			if(pa!=null) {
				header = CostantiProprieta.getModIAuditForwardBackend(pa.getProprietaList(), claimConfig.getNome(), header);
			}
			claimConfig.setForwardBackend(header);
		}
		else {
			claimConfig.setForwardBackend(null);
		}
	}
	
	private void readCorniceSicurezzaSchemaClaims(ModIProperties modiProperties, String schemaCorniceSicurezza, PortaDelegata pd) throws ProtocolException {
		if(modiProperties.getAuditConfig()!=null && !modiProperties.getAuditConfig().isEmpty()) {
			for (ModIAuditConfig modIAuditConfig : modiProperties.getAuditConfig()) {
				if(modIAuditConfig.getNome().equals(schemaCorniceSicurezza)) {
					this.corniceSicurezzaSchemaConfig = modIAuditConfig.copyNewInstance();
					readCorniceSicurezzaSchemaClaimsRule(this.corniceSicurezzaSchemaConfig.getClaims(), pd);
					break;
				}
			}
		}
	}
	private void readCorniceSicurezzaSchemaClaimsRule(List<ModIAuditClaimConfig> list, PortaDelegata pd) throws ProtocolException {
		if(list!=null && !list.isEmpty()) {
			for (ModIAuditClaimConfig modIAuditClaimConfig : list) {
				
				boolean traceEnabled = modIAuditClaimConfig.isTrace();
				if(pd!=null) {
					traceEnabled = CostantiProprieta.isModIAuditTraceEnabled(pd.getProprietaList(), modIAuditClaimConfig.getNome(), traceEnabled);
				}
				modIAuditClaimConfig.setTrace(traceEnabled);
				
				readCorniceSicurezzaSchemaClaimRule(modIAuditClaimConfig);
				
			}
		}
	}
	private void readCorniceSicurezzaSchemaClaimRule(ModIAuditClaimConfig claimConfig) throws ProtocolException {
		try {
			String claimName = claimConfig.getNome();
			String claimMode = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, 
					ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_MODE_PREFIX+claimName);
			List<String> rules = null;
			if(ModICostanti.MODIPA_PROFILO_RIDEFINISCI.equals(claimMode)) {
				String claimValue = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, 
						ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_PREFIX+claimName);
				rules = convertToList(claimValue);
				ModIUtilities.remove(this.claims, claimName);
			}
			else {
				rules = claimConfig.getRules();
			}
			claimConfig.setRules(rules);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	private void readCorniceSicurezzaCodiceEnteLegacy(ModIProperties modiProperties) throws ProtocolException {
		try {
			String codiceEnteMode = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_MODE);
			String claimNameCodiceEnte = modiProperties.getSicurezzaMessaggioCorniceSicurezzaRestCodiceEnte();
			if(ModICostanti.MODIPA_PROFILO_RIDEFINISCI.equals(codiceEnteMode)) {
				this.corniceSicurezzaCodiceEnteRule = convertToList(ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE));
				if(this.multipleHeaderAuthorizationConfig!=null) {
					ModIUtilities.remove(this.multipleHeaderClaims, claimNameCodiceEnte);
				}
				ModIUtilities.remove(this.claims, claimNameCodiceEnte);
			}
			else {
				if(this.multipleHeaderAuthorizationConfig!=null && 
						ModIUtilities.exists(this.multipleHeaderClaims, claimNameCodiceEnte)) {
					this.corniceSicurezzaCodiceEnteRule = convertToList(ModIUtilities.get(this.multipleHeaderClaims, claimNameCodiceEnte, ModIUtilities.REMOVE));
				}
				else if(ModIUtilities.exists(this.claims, claimNameCodiceEnte)) {
					this.corniceSicurezzaCodiceEnteRule = convertToList(ModIUtilities.get(this.claims, claimNameCodiceEnte, ModIUtilities.REMOVE));
				}
				else {
					this.corniceSicurezzaCodiceEnteRule = modiProperties.getSicurezzaMessaggioCorniceSicurezzaDynamicCodiceEnte();
				}
			}
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	private void readCorniceSicurezzaUserLegacy(ModIProperties modiProperties) throws ProtocolException {
		try {
			String userMode = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE);
			String claimNameUser = modiProperties.getSicurezzaMessaggioCorniceSicurezzaRestUser();
			if(ModICostanti.MODIPA_PROFILO_RIDEFINISCI.equals(userMode)) {
				this.corniceSicurezzaUserRule = convertToList(ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER));
				if(this.multipleHeaderAuthorizationConfig!=null) {
					ModIUtilities.remove(this.multipleHeaderClaims, claimNameUser);
				}
				ModIUtilities.remove(this.claims, claimNameUser);
			}
			else {
				if(this.multipleHeaderAuthorizationConfig!=null && 
						ModIUtilities.exists(this.multipleHeaderClaims, claimNameUser)) {
					this.corniceSicurezzaUserRule = convertToList(ModIUtilities.get(this.multipleHeaderClaims, claimNameUser, ModIUtilities.REMOVE));
				}
				else if(ModIUtilities.exists(this.claims, claimNameUser)) {
					this.corniceSicurezzaUserRule = convertToList(ModIUtilities.get(this.claims, claimNameUser, ModIUtilities.REMOVE));
				}
				else {
					this.corniceSicurezzaUserRule = modiProperties.getSicurezzaMessaggioCorniceSicurezzaDynamicUser();
				}
			}
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	private void readCorniceSicurezzaIpUserLegacy(ModIProperties modiProperties) throws ProtocolException {
		try {
			String ipUserMode = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE);
			String claimNameIpUser = modiProperties.getSicurezzaMessaggioCorniceSicurezzaRestIpuser();
			if(ModICostanti.MODIPA_PROFILO_RIDEFINISCI.equals(ipUserMode)) {
				this.corniceSicurezzaIpUserRule = convertToList(ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER));
				if(this.multipleHeaderAuthorizationConfig!=null) {
					ModIUtilities.remove(this.multipleHeaderClaims, claimNameIpUser);
				}
				ModIUtilities.remove(this.claims, claimNameIpUser);
			}
			else {
				if(this.multipleHeaderAuthorizationConfig!=null && 
						ModIUtilities.exists(this.multipleHeaderClaims, claimNameIpUser)) {
					this.corniceSicurezzaIpUserRule = convertToList(ModIUtilities.get(this.multipleHeaderClaims, claimNameIpUser, ModIUtilities.REMOVE));
				}
				else if(ModIUtilities.exists(this.claims, claimNameIpUser)) {
					this.corniceSicurezzaIpUserRule = convertToList(ModIUtilities.get(this.claims, claimNameIpUser, ModIUtilities.REMOVE));
				}
				else {
					this.corniceSicurezzaIpUserRule = modiProperties.getSicurezzaMessaggioCorniceSicurezzaDynamicIpuser();
				}
			}
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	
	public ModISecurityConfig(OpenSPCoop2Message msg, IProtocolFactory<?> protocolFactory, IState state, RequestInfo requestInfo, 
			boolean fruizione, boolean request,
			PortaApplicativa paDefault) throws ProtocolException {

		// METODO USATO IN VALIDAZIONE SEMANTICA
		
		try {
			
			if(!fruizione && request) {
					
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(paDefault.getServizio().getTipo(), paDefault.getServizio().getNome(),
						paDefault.getTipoSoggettoProprietario(), paDefault.getNomeSoggettoProprietario(), 
						paDefault.getServizio().getVersione());
				
				IRegistryReader registryReader = protocolFactory.getCachedRegistryReader(state, requestInfo);
				
				AccordoServizioParteSpecifica asps = registryReader.getAccordoServizioParteSpecifica(idServizio);
				
				this.listProtocolPropertiesInternal = ModIPropertiesUtils.getProtocolProperties(false, null, asps);
				
				if(this.listProtocolPropertiesInternal==null || this.listProtocolPropertiesInternal.isEmpty()) {
					throw new ProtocolException(CONFIGURAZIONE_SICUREZZA_INCOMPLETA);
				}
				
				this.audience = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE);

				if(this.audience==null) {
					
					IConfigIntegrationReader configReader = protocolFactory.getCachedConfigIntegrationReader(state, requestInfo);
					
					IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
					AccordoServizioParteComune aspc = registryReader.getAccordoServizioParteComune(idAccordo);
					boolean rest = org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(aspc.getServiceBinding());

					this.audience =  buildDefaultAudience(msg, protocolFactory, aspc, asps,
							configReader, rest,
							requestInfo, paDefault);
				}
				if(this.audience==null) {
					throw new ProtocolException(AUDIENCE_UNDEFINED);
				}
				
			}
			
		}catch(Exception e) {
			throw new ProtocolException(e);
		}
		
	}
	private String buildDefaultAudience(OpenSPCoop2Message msg, IProtocolFactory<?> protocolFactory, AccordoServizioParteComune aspc, AccordoServizioParteSpecifica asps,
			IConfigIntegrationReader configReader, boolean rest,
			RequestInfo requestInfo, PortaApplicativa paDefault) throws ProtocolException {
		
		try {
		
			List<String> tags = new ArrayList<>();
			if(aspc!=null && aspc.getGruppi()!=null && aspc.getGruppi().sizeGruppoList()>0) {
				for (int i = 0; i < aspc.getGruppi().sizeGruppoList(); i++) {
					tags.add(aspc.getGruppi().getGruppo(i).getNome());
				}
			}
			
			CanaliConfigurazione canaliConfigurazione = configReader.getCanaliConfigurazione();
			String canaleApi = null;
			if(aspc!=null) {
				canaleApi = aspc.getCanale();
			}
			String canalePorta = paDefault!=null ? paDefault.getCanale() : null;
			String canale = CanaliUtils.getCanale(canaliConfigurazione, canaleApi, canalePorta);
			
			String nomePorta = null;
			if(paDefault!=null) {
				nomePorta = paDefault.getNome();
			}
			else {
				nomePorta = msg!=null ? msg.getTransportRequestContext().getInterfaceName() : null;
			}
			
			UrlInvocazioneAPI urlInvocazioneApi = ConfigurazionePdDManager.getInstance().getConfigurazioneUrlInvocazione(protocolFactory, 
					RuoloContesto.PORTA_APPLICATIVA,
					rest ? org.openspcoop2.message.constants.ServiceBinding.REST : org.openspcoop2.message.constants.ServiceBinding.SOAP,
							nomePorta,
							new IDSoggetto(asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore()),
							tags, canale, 
							requestInfo);		 
			String prefixGatewayUrl = urlInvocazioneApi.getBaseUrl();
			String contesto = urlInvocazioneApi.getContext();
			return Utilities.buildUrl(prefixGatewayUrl, contesto);
			
		}catch(Exception e) {
			throw new ProtocolException(e);
		}
	}
	
	public ModISecurityConfig(OpenSPCoop2Message msg, Context context, IProtocolFactory<?> protocolFactory, IState state, RequestInfo requestInfo, IDSoggetto soggettoFruitore, 
			AccordoServizioParteComune aspc, AccordoServizioParteSpecifica aspsParam, ServizioApplicativo sa, boolean rest, boolean fruizione, boolean request,
			Boolean multipleHeaderAuthorizationConfig, boolean kidMode,
			String patternCorniceSicurezza, String schemaCorniceSicurezza) throws ProtocolException {
		
		// METODO USATO IN VALIDAZIONE SINTATTICA
		
		ModIProperties modiProperties = ModIProperties.getInstance();
		
		this.kid = kidMode;
		
		this.fruizione = fruizione;
		
		boolean keystoreDefinitoInFruizione = false;
		if(this.fruizione) {
			try {
				keystoreDefinitoInFruizione = ModIKeystoreConfig.isKeystoreDefinitoInFruizione(soggettoFruitore, aspsParam);
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
		}
		
		this.multipleHeaderAuthorizationConfig = multipleHeaderAuthorizationConfig;
		
		this.listProtocolPropertiesInternal = ModIPropertiesUtils.getProtocolProperties(fruizione, soggettoFruitore, aspsParam);
		
		if(this.listProtocolPropertiesInternal==null || this.listProtocolPropertiesInternal.isEmpty()) {
			throw new ProtocolException(CONFIGURAZIONE_SICUREZZA_INCOMPLETA);
		}
		
		if(rest) {
			this.initSharedRest(modiProperties, this.listProtocolPropertiesInternal,null, fruizione,request,false,false);
		}
		else {
			this.initSharedSoap(this.listProtocolPropertiesInternal,fruizione,request);
		}
		
		try {
		
			/* TTL */
			
			if(fruizione) {
				if(!request) {
					boolean greatherThanZero = true;
					String mode = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_IAT);
					if(ModICostanti.MODIPA_PROFILO_RIDEFINISCI.equals(mode)) {
						Long l = ProtocolPropertiesUtils.getRequiredNumberValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_IAT_SECONDS, greatherThanZero);
						if(l!=null && l.longValue()>0) {
							this.checkTtlIatMs = l.longValue() * 1000; // trasformo in ms
						}
					}
				}
			}
			else {
				if(request) {
					boolean greatherThanZero = true;
					String mode = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_IAT);
					if(ModICostanti.MODIPA_PROFILO_RIDEFINISCI.equals(mode)) {
						Long l = ProtocolPropertiesUtils.getRequiredNumberValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_IAT_SECONDS, greatherThanZero);
						if(l!=null && l.longValue()>0) {
							this.checkTtlIatMs = l.longValue() * 1000; // trasformo in ms
						}
					}
				}
			}
			
			if(rest &&
				
				this.multipleHeaderAuthorizationConfig!=null) {
				
				// Opzioni 'jti' in header contemporanei
				
				String idPropertyMultipleHeaderUseJtiAuthorizationAsIdMessaggio = (fruizione ? ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_FILTRO_DUPLICATI : ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_FILTRO_DUPLICATI);
				String multipleHeaderUseJtiAuthorizationAsIdMessaggioPropRegistry = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, idPropertyMultipleHeaderUseJtiAuthorizationAsIdMessaggio);
				if(multipleHeaderUseJtiAuthorizationAsIdMessaggioPropRegistry!=null) {
					this.multipleHeaderUseJtiAuthorizationAsIdMessaggio = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_FILTRO_DUPLICATI_VALUE_AUTHORIZATION.equals(multipleHeaderUseJtiAuthorizationAsIdMessaggioPropRegistry);
				}
					
			}
			
			if(!fruizione && request) {
				
				IConfigIntegrationReader configReader = protocolFactory.getCachedConfigIntegrationReader(state, requestInfo);
				
				PortaApplicativa paDefault = null;
				if(msg!=null) {
					Object nomePortaInvocataObject = msg.getContextProperty(CostantiPdD.NOME_PORTA_INVOCATA);
					String nomePorta = null;
					if(nomePortaInvocataObject instanceof String) {
						nomePorta = (String) nomePortaInvocataObject;
					}
					if(nomePorta==null && context!=null && context.containsKey(CostantiPdD.NOME_PORTA_INVOCATA)) {
						nomePorta = (String) context.getObject(CostantiPdD.NOME_PORTA_INVOCATA);
					}
					if(nomePorta==null && requestInfo!=null && requestInfo.getProtocolContext()!=null && requestInfo.getProtocolContext().getInterfaceName()!=null) {
						nomePorta = requestInfo.getProtocolContext().getInterfaceName(); // se non e' presente 'NOME_PORTA_INVOCATA' significa che non e' stato invocato un gruppo specifico
					}
					if(nomePorta!=null) {
						paDefault = getPortaApplicativa(nomePorta, configReader);
					}
				}
						
				this.checkAudience = true;
				
				if(this.multipleHeaderAuthorizationConfig!=null && !this.multipleHeaderAuthorizationConfig) {
					String modalita = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_AUDIENCE);
					if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_AUDIENCE_VALUE_DIFFERENT.equals(modalita)) {
						this.audience = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_AUDIENCE_INTEGRITY);
					}
				}
				if(this.audience==null) {
					this.audience = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE);
				}
				if(this.audience==null) {
					
					this.audience =  buildDefaultAudience(msg, protocolFactory, aspc, aspsParam,
							configReader, rest,
							requestInfo, paDefault);
					
				}
				if(this.audience==null) {
					throw new ProtocolException(AUDIENCE_UNDEFINED);
				}
				
				
				/* Cornice Sicurezza */
				
				PortaApplicativa paInvocata = null;
				if(requestInfo!=null && requestInfo.getProtocolContext()!=null) {
					String nomePorta = requestInfo.getProtocolContext().getInterfaceName();
					if(nomePorta!=null) {
						paInvocata = getPortaApplicativa(nomePorta, configReader);
					}
				}
				
				if(patternCorniceSicurezza!=null && !CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_OLD.equals(patternCorniceSicurezza) && schemaCorniceSicurezza!=null) {
					readCorniceSicurezzaSchemaAudience(schemaCorniceSicurezza);
					
					readCorniceSicurezzaSchemaClaimsReadPAProperties(modiProperties, schemaCorniceSicurezza, paInvocata);
				}
			}
			else if(fruizione && !request) {
			
				if(this.multipleHeaderAuthorizationConfig!=null && !this.multipleHeaderAuthorizationConfig) {
					String modalita = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_AUDIENCE);
					if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_AUDIENCE_VALUE_DIFFERENT.equals(modalita)) {
						this.audience = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_AUDIENCE_INTEGRITY);
					}
				}
				if(this.audience==null) {
					this.audience = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE_VALORE);
				}
				if(this.audience==null &&
					sa!=null) {
					this.audience = ProtocolPropertiesUtils.getOptionalStringValuePropertyConfig(sa.getProtocolPropertyList(), ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE);
					// 	Se non definito sull'applicativo, non viene serializzato.
					if(this.audience==null) {
						// provo a vedere se viene utilizzato il valore di default
						this.audience=NamingUtils.getLabelSoggetto(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()))+"/"+sa.getNome();
					}
				}
				this.checkAudience = ProtocolPropertiesUtils.getBooleanValuePropertyRegistry(this.listProtocolPropertiesInternal, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE, false);
				if(this.checkAudience &&
					this.audience==null) {
					if(keystoreDefinitoInFruizione) {
						throw new ProtocolException("Configurazione errata; Audience della risposta non definito nonostante sia richiesta una verifica");
					}
					else {
						throw new ProtocolException("Configurazione errata; Audience non definito sull'applicativo e verifica abilitata sulla fruizione");
					}
				}
				
				/* OAuth - ClientId */
				
				if(this.tokenClientId==null && sa!=null) {
					this.tokenClientId = ProtocolPropertiesUtils.getOptionalStringValuePropertyConfig(sa.getProtocolPropertyList(), ModICostanti.MODIPA_SICUREZZA_TOKEN_CLIENT_ID);
				}
			}
			else {
				
				throw new ProtocolException("Use unsupported");
				
			}
			
		}catch(Exception e) {
			throw new ProtocolException(e);
		}
		
	}
	
	private static PortaApplicativa getPortaApplicativa(String nomePorta, IConfigIntegrationReader configReader) {
		try {
			IDPortaApplicativa idPA = new IDPortaApplicativa();
			idPA.setNome(nomePorta);
			return configReader.getPortaApplicativa(idPA);
		}catch(Exception t) {
			// ignore
			return null;
		}
	}
	private static PortaDelegata getPortaDelegata(String nomePorta, IConfigIntegrationReader configReader) {
		try {
			IDPortaDelegata idPD = new IDPortaDelegata();
			idPD.setNome(nomePorta);
			return configReader.getPortaDelegata(idPD);
		}catch(Exception t) {
			// ignore
			return null;
		}
	}
	
	
	private void initSharedRest(ModIProperties modiProperties, List<ProtocolProperty> listProtocolProperties, ServizioApplicativo sa, boolean fruizione, boolean request, 
			boolean keystoreDefinitoInFruizione, boolean keystoreDefinitoInTokenPolicy) throws ProtocolException {
		
		if(fruizione) {
			if(request) {
				this.algorithm = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_ALG);
				try {
					this.useSingleCertificate = !ProtocolPropertiesUtils.getBooleanValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN, true);
				}catch(ProtocolException pNotFound) {
					// lascio il default true 
				}
			}
		}
		else {
			if(!request) {
				this.algorithm = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_ALG);
				try{
					this.useSingleCertificate = !ProtocolPropertiesUtils.getBooleanValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN, true);
				}catch(ProtocolException pNotFound) {
					// lascio il default true 
				}
			}
		}
		
		if(this.algorithm!=null) {
			if(this.algorithm.contains("256")) {
				this.digestAlgorithm = HttpConstants.DIGEST_ALGO_SHA_256;
			}
			else if(this.algorithm.contains("384")) {
				this.digestAlgorithm = HttpConstants.DIGEST_ALGO_SHA_384;
			}
			else if(this.algorithm.contains("512")) {
				this.digestAlgorithm = HttpConstants.DIGEST_ALGO_SHA_512;
			}
			else {
				throw new ProtocolException("Digest algorithm compatible with signature '"+this.algorithm+"' not found");
			}
			
			if(this.digestAlgorithm!=null) {
				boolean creazioneDigest = (fruizione && request) || (!fruizione && !request);
				if(creazioneDigest) {
					try {
						this.digestEncoding = modiProperties.getRestSecurityTokenDigestDefaultEncoding(); // default
						
						// proviamo a vedere se esiste una proprietà
						
						String digestEncodingPropRegistry = null;
						if(request) {
							digestEncodingPropRegistry = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_DIGEST_ENCODING);
						}
						else {
							digestEncodingPropRegistry = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_DIGEST_ENCODING);
						}
						if(digestEncodingPropRegistry!=null) {
							this.digestEncoding = DigestEncoding.valueOf(digestEncodingPropRegistry.trim().toUpperCase());
						}
												
					}catch(Exception e) {
						throw new ProtocolException("Digest encoding; configuration error: "+e.getMessage(),e);
					}
				}	
			}
		}
		
		if( (fruizione && !request) || (!fruizione && request) ) {
			try {
				this.digestEncodingAccepted = modiProperties.getRestSecurityTokenDigestEncodingAccepted();
			}catch(Exception e) {
				throw new ProtocolException("Digest algorithm accepted; configuration error: "+e.getMessage(),e);
			}
		}
		
		if(!this.kid) {
			String x509 = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_RIFERIMENTO_X509);
			if(!request) {
				String profiloSicurezzaMessaggioRifX509AsRequestItemValue =  ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_AS_REQUEST_ID);
				if(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_AS_REQUEST_VALUE_FALSE.equals(profiloSicurezzaMessaggioRifX509AsRequestItemValue)){
					x509 = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509);
				}
			}
			List<String> vX509 = ProtocolPropertiesUtils.getListFromMultiSelectValue(x509);
			if(vX509.contains(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_VALUE_X5U)) {
				this.x5u = true;
				if(fruizione && request) {
					if(keystoreDefinitoInFruizione || keystoreDefinitoInTokenPolicy) {
						try {
							this.x5url = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_X509_VALUE_X5URL);
						}catch(Exception e) {
							ProtocolException pe = new ProtocolException("Nella configurazione di sicurezza (x5u) associata alla fruizione non è stata definita una URL che riferisce un certificato (o certificate chain) X.509 corrispondente alla chiave firmataria del security token");
							pe.setInteroperabilityError(true);
							throw pe;
						}
					}
					else {
						try {
							this.x5url = ProtocolPropertiesUtils.getRequiredStringValuePropertyConfig(sa.getProtocolPropertyList(), ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_SA_RICHIESTA_X509_VALUE_X5URL);
						}catch(Exception e) {
							ProtocolException pe = new ProtocolException("Applicativo '"+sa.getNome()+"' non utilizzabile con la configurazione di sicurezza (x5u) associata alla fruizione richiesta, poichè non contiene la definizione di una URL che riferisce un certificato (o certificate chain) X.509 corrispondente alla chiave firmataria del security token");
							pe.setInteroperabilityError(true);
							throw pe;
						}
					}
				}
				else if(!fruizione && !request) {
					this.x5url = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_X509_VALUE_X5URL);
				}
			}
			if(vX509.contains(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_VALUE_X5C)) {
				this.x5c = true;
			}
			if(vX509.contains(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_VALUE_X5T)) {
				this.x5t = true;
			}
		}
		
	}
	
	private void initSharedSoap(List<ProtocolProperty> listProtocolProperties, boolean fruizione, boolean request) throws ProtocolException {
		
		if(fruizione) {
			if(request) {
				boolean auditConfig = false;
				try {
					this.algorithm = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_ALG);
				}catch(Exception e) {
					if(this.apiSoapAlgorithmUsedOnlyForAudit!=null && StringUtils.isNotEmpty(this.apiSoapAlgorithmUsedOnlyForAudit)) {
						auditConfig = true;
					}
					else {
						throw e;
					}
				}
				if(!auditConfig) {
					this.c14nAlgorithm = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_CANONICALIZATION_ALG);
					this.keyIdentifierMode = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_RIFERIMENTO_X509);
					try {
						this.useSingleCertificate = !ProtocolPropertiesUtils.getBooleanValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN, true);
					}catch(ProtocolException pNotFound) {
						// lascio il default true 
					}
					try {
						this.includeSignatureToken = ProtocolPropertiesUtils.getBooleanValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN, true);
					}catch(ProtocolException pNotFound) {
						// lascio il default false 
					}
				}
			}
		}
		else {
			if(!request) {
				this.algorithm = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_ALG);
				this.c14nAlgorithm = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_CANONICALIZATION_ALG);
				this.keyIdentifierMode = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509);
				try{
					this.useSingleCertificate = !ProtocolPropertiesUtils.getBooleanValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN, true);
				}catch(ProtocolException pNotFound) {
					// lascio il default true 
				}
				try{
					this.includeSignatureToken = ProtocolPropertiesUtils.getBooleanValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN, true);
				}catch(ProtocolException pNotFound) {
					// lascio il default false 
				}
			}
		}
		
		if(this.algorithm!=null) {
			if(this.algorithm.contains("224")) {
				this.digestAlgorithm = SignatureDigestAlgorithm.SHA224.getUri();
			}
			else if(this.algorithm.contains("256")) {
				this.digestAlgorithm = SignatureDigestAlgorithm.SHA256.getUri();
			}
			else if(this.algorithm.contains("384")) {
				this.digestAlgorithm = SignatureDigestAlgorithm.SHA384.getUri();
			}
			else if(this.algorithm.contains("512")) {
				this.digestAlgorithm = SignatureDigestAlgorithm.SHA512.getUri();
			}
			else {
				throw new ProtocolException("Digest algorithm compatible with signature '"+this.algorithm+"' not found");
			}
		}
		
	}
		
	public static List<String> convertToList(String value) throws ProtocolException{
		String nonDefinita = "non definita";
		List<String> l = null;
		if (value != null){
			value = value.trim();
			if("".equals(value)) {
				throw new ProtocolException(nonDefinita);
			}
			l = new ArrayList<>();
			if(value.contains(",")) {
				converToList(value, nonDefinita, l);
			}
			else {
				l.add(value);
			}
		}
		else {
			throw new ProtocolException(nonDefinita);
		}
		return l;
	}
	private static void converToList(String value, String nonDefinita, List<String> l) throws ProtocolException {
		String [] tmp = value.split(",");
		if(tmp==null || tmp.length<=0) {
			throw new ProtocolException(nonDefinita);
		}
		for (String s : tmp) {
			if(s!=null) {
				s = s.trim();
				if(!"".equals(s)) {
					l.add(s);
				}
			}
		}
		if(l.isEmpty()) {
			throw new ProtocolException(nonDefinita);
		}
	}
	

	public String getAlgorithm() {
		return this.algorithm;
	}
	
	private static final String RSA = "RSA";
	private static final String ECDSA = "ECDSA";
	public String getAlgorithmConvertForREST() throws ProtocolException {
		
		if(this.algorithm!=null) {
			String algo = convertAlgorithmForREST();
			if(algo!=null) {
				return algo;
			}
		}
		
		if(this.apiSoapAlgorithmUsedOnlyForAudit!=null && StringUtils.isNotEmpty(this.apiSoapAlgorithmUsedOnlyForAudit)) {
			return this.apiSoapAlgorithmUsedOnlyForAudit;
		}
		
		throw new ProtocolException("Convert signature algorithm '"+this.algorithm+"' for audit token failed");
	}
	private String convertAlgorithmForREST() {
		String algoLowerCase = this.algorithm.toLowerCase();
		String size256 = "256";
		String size384 = "384";
		String size512 = "512";
		if(algoLowerCase.contains(RSA.toLowerCase())) {
			if(this.algorithm.contains(size256)) {
				return ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_RS256;
			}
			else if(this.algorithm.contains(size384)) {
				return ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_RS384;
			}
			else if(this.algorithm.contains(size512)) {
				return ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_RS512;
			}
		}
		else if(algoLowerCase.contains(ECDSA.toLowerCase())) {
			if(this.algorithm.contains(size256)) {
				return ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_ES256;
			}
			else if(this.algorithm.contains(size384)) {
				return ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_ES384;
			}
			else if(this.algorithm.contains(size512)) {
				return ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_ES512;
			}
		}
		return null;
	}
	
	public String getDigestAlgorithm() {
		return this.digestAlgorithm;
	}
	
	public DigestEncoding getDigestEncoding() {
		return this.digestEncoding;
	}
	
	public List<DigestEncoding> getDigestEncodingAccepted() {
		return this.digestEncodingAccepted;
	}
	
	public String getC14nAlgorithm() {
		return this.c14nAlgorithm;
	}

	public boolean isKid() {
		return this.kid;
	}
	
	public boolean isX5c() {
		return this.x5c;
	}

	public boolean isX5t() {
		return this.x5t;
	}

	public boolean isX5u() {
		return this.x5u;
	}
	public String getX5url() {
		return this.x5url;
	}

	public String getKeyIdentifierMode() {
		return this.keyIdentifierMode;
	}

	public boolean isUseSingleCertificate() {
		return this.useSingleCertificate;
	}
	
	public boolean isIncludeSignatureToken() {
		return this.includeSignatureToken;
	}
	
	public int getTtlSeconds() {
		return this.ttl;
	}

	public Long getCheckTtlIatMilliseconds() {
		return this.checkTtlIatMs;
	}
	
	public String getAudience() {
		return this.audience;
	}
	
	public String getClientId() {
		return this.clientId;
	}
	
	public String getIssuer() {
		return this.issuer;
	}

	public String getSubject() {
		return this.subject;
	}
	
	public List<String> getHttpHeaders() {
		return this.httpHeaders;
	}
	
	public boolean isMultipleHeaderUseSameJti() {
		return this.multipleHeaderUseSameJti;
	}

	public boolean isMultipleHeaderUseJtiAuthorizationAsIdMessaggio() {
		return this.multipleHeaderUseJtiAuthorizationAsIdMessaggio;
	}
	
	public List<SOAPHeader> getSoapHeaders() {
		return this.soapHeaders;
	}
	
	public List<String> getCorniceSicurezzaUserRule() {
		return this.corniceSicurezzaUserRule;
	}

	public List<String> getCorniceSicurezzaIpUserRule() {
		return this.corniceSicurezzaIpUserRule;
	}
	
	public List<String> getCorniceSicurezzaCodiceEnteRule() {
		return this.corniceSicurezzaCodiceEnteRule;
	}
	
	public ModIAuditConfig getCorniceSicurezzaSchemaConfig() {
		return this.corniceSicurezzaSchemaConfig;
	}
	
	public String getCorniceSicurezzaAudience() {
		return this.corniceSicurezzaAudience;
	}
	
	public boolean isCheckAudience() {
		return this.checkAudience;
	}

	public Properties getClaims() {
		return this.claims;
	}

	public Properties getMultipleHeaderClaims() {
		return this.multipleHeaderClaims;
	}
	
	public String getTokenKid() {
		return this.tokenKid;
	}

	public String getTokenClientId() {
		return this.tokenClientId;
	}
}
