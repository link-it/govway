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

import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.RuoloContesto;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.UrlInvocazioneAPI;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.autorizzazione.canali.CanaliUtils;
import org.openspcoop2.pdd.core.dynamic.DynamicHelperCostanti;
import org.openspcoop2.pdd.core.token.parser.Claims;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModIConsoleCostanti;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
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
	private String digestAlgorithm = null;
	private DigestEncoding digestEncoding = null;
	private List<DigestEncoding> digestEncodingAccepted = null;
	private String c14nAlgorithm = null;
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
	private List<String> httpHeaders = new ArrayList<String>();
	private List<SOAPHeader> soapHeaders = new ArrayList<SOAPHeader>();
	
	private boolean multipleHeaderUseSameJti = true;
	private boolean multipleHeaderUseJtiAuthorizationAsIdMessaggio = true;
	private Boolean multipleHeaderAuthorizationConfig = null;

	private Properties claims;
	private Properties multipleHeaderClaims;
	
	private List<String> corniceSicurezzaCodiceEnteRule = null;
	private List<String> corniceSicurezzaUserRule = null;
	private List<String> corniceSicurezzaIpUserRule = null;
	
	private boolean fruizione;
	private List<ProtocolProperty> _listProtocolProperties = null;
	
	public ModISecurityConfig(OpenSPCoop2Message msg, IDSoggetto soggettoFruitore, AccordoServizioParteSpecifica aspsParam, ServizioApplicativo sa, 
			boolean rest, boolean fruizione,  boolean request, boolean corniceSicurezza,
			Busta busta, Busta bustaRichiesta,
			Boolean multipleHeaderAuthorizationConfig) throws ProtocolException {
		
		// METODO USATO IN IMBUSTAMENTO
		
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
					this._listProtocolProperties = fruitoreCheck.getProtocolPropertyList();
					find = true;
					break;
				}
			}
			if(!find) {
				throw new ProtocolException("Fruitore '"+soggettoFruitore+"' non registrato come fruitore dell'accordo parte specifica");
			}
		}
		else {
			this._listProtocolProperties = aspsParam.getProtocolPropertyList();
		}
		
		if(this._listProtocolProperties==null || this._listProtocolProperties.isEmpty()) {
			throw new ProtocolException("Configurazione della sicurezza incompleta");
		}
		
		if(rest) {
			
			/* Opzioni REST */
						
			this.initSharedRest(modiProperties, this._listProtocolProperties,sa,fruizione,request);
			
			// HTTP Header da firmare
			
			String httpHeaders = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this._listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST);
			if(httpHeaders!=null && httpHeaders.length()>0) {
				this.httpHeaders = Arrays.asList(httpHeaders.split(","));
			}
			
			// Opzioni 'jti' in header contemporanei
			
			String idPropertyMultipleHeaderUseSameJti = (fruizione ? ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_JTI : ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_JTI);
			String multipleHeaderUseSameJti = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this._listProtocolProperties, idPropertyMultipleHeaderUseSameJti);
			if(multipleHeaderUseSameJti!=null) {
				this.multipleHeaderUseSameJti = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JTI_VALUE_SAME.equals(multipleHeaderUseSameJti);
			}
			
			String idPropertyMultipleHeaderUseJtiAuthorizationAsIdMessaggio = (fruizione ? ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO : ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO);
			String multipleHeaderUseJtiAuthorizationAsIdMessaggio = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this._listProtocolProperties, idPropertyMultipleHeaderUseJtiAuthorizationAsIdMessaggio);
			if(multipleHeaderUseJtiAuthorizationAsIdMessaggio!=null) {
				this.multipleHeaderUseJtiAuthorizationAsIdMessaggio = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_VALUE_AUTHORIZATION.equals(multipleHeaderUseJtiAuthorizationAsIdMessaggio);
			}
			
			// Claims custom
			
			String idPropertyClaims = (fruizione ? ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_JWT_CLAIMS : ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_JWT_CLAIMS);
			String claims = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this._listProtocolProperties, idPropertyClaims);
			if(claims!=null && claims.length()>0) {
				this.claims = PropertiesUtilities.convertTextToProperties(claims);
			}
			
			// Custom claims per header contemporanei
			if(this.multipleHeaderAuthorizationConfig!=null) {

				String idPropertyMultipleHeaderClaims = null;
				if(this.multipleHeaderAuthorizationConfig) {
					idPropertyMultipleHeaderClaims = (this.fruizione ? ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION : ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION);
				}
				else {
					idPropertyMultipleHeaderClaims = (this.fruizione ? ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_JWT_CLAIMS_MODI : ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_JWT_CLAIMS_MODI);
				}
				String multipleHeaderClaimsAuthorization = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this._listProtocolProperties, idPropertyMultipleHeaderClaims);
				if(multipleHeaderClaimsAuthorization!=null && multipleHeaderClaimsAuthorization.length()>0) {
					this.multipleHeaderClaims = PropertiesUtilities.convertTextToProperties(multipleHeaderClaimsAuthorization);
				}
				
			}
						
		}
		else {
			
			/* Opzioni SOAP */
			
			this.initSharedSoap(this._listProtocolProperties,fruizione,request);
			
			// Header SOAP da firmare
			
			String soapHeaders = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this._listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_SOAP);
			if(soapHeaders!=null && soapHeaders.length()>0) {
				this.soapHeaders = SOAPHeader.parse(soapHeaders);
			}
		}
				
		/* TTL */
		
		if(fruizione) {
			if(request) {
				boolean greatherThanZero = true;
				this.ttl = ProtocolPropertiesUtils.getRequiredNumberValuePropertyRegistry(this._listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_EXPIRED, greatherThanZero).intValue();
			}
		}
		else {
			if(!request) {
				boolean greatherThanZero = true;
				this.ttl = ProtocolPropertiesUtils.getRequiredNumberValuePropertyRegistry(this._listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_EXPIRED, greatherThanZero).intValue();
			}
		}
		
		if(fruizione && request) {
			
			/* Audience */
			
			if(this.multipleHeaderAuthorizationConfig!=null && !this.multipleHeaderAuthorizationConfig) {
				String modalita = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this._listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_AUDIENCE);
				if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_AUDIENCE_VALUE_DIFFERENT.equals(modalita)) {
					this.audience = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this._listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_AUDIENCE_INTEGRITY);
				}
			}
			if(this.audience==null) {
				this.audience = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this._listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE);
			}
			if(this.audience==null) {
				if(fruitore.getConnettore()!=null && fruitore.getConnettore().sizePropertyList()>0) {
					for (Property p : fruitore.getConnettore().getPropertyList()) {
						if(CostantiDB.CONNETTORE_HTTP_LOCATION.equals(p.getNome())) {
							this.audience = p.getValore();
						}
					}
				}
			}
			if(this.audience==null) {
				throw new ProtocolException("Audience undefined");
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
				if(this.clientId==null) {
					if(ModIUtilities.exists(this.claims, Claims.INTROSPECTION_RESPONSE_RFC_7662_CLIENT_ID)) {
						this.clientId = ModIUtilities.get(this.claims, Claims.INTROSPECTION_RESPONSE_RFC_7662_CLIENT_ID, 
								ModIUtilities.REMOVE);
						if(this.clientId!=null && !DynamicHelperCostanti.NOT_GENERATE.equalsIgnoreCase(this.clientId)) {
							// non utilizzabile nella richiesta. Si imposta dall'applicativo
							this.clientId = null;
						}
					}
				}
			}
			if(this.clientId==null) {
				if(sa!=null) {
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
				else if(modiProperties.isRestSecurityTokenClaimsIssuerEnabled()) {
					String valore = modiProperties.getRestSecurityTokenClaimsIssuerHeaderValue();
					if(valore!=null && !"".equals(valore)) {
						this.issuer = valore;
					}
					else {
						this.issuer = NamingUtils.getLabelSoggetto(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
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
						this.subject = sa.getNome();
					}
				}
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
			
			/* Cornice Sicurezza */
			if(corniceSicurezza) {
				try {
					String codiceEnteMode = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this._listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_MODE);
					String claimNameCodiceEnte = modiProperties.getSicurezzaMessaggio_corniceSicurezza_rest_codice_ente();
					if(ModICostanti.MODIPA_PROFILO_RIDEFINISCI.equals(codiceEnteMode)) {
						this.corniceSicurezzaCodiceEnteRule = convertToList(ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(this._listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE));
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
							this.corniceSicurezzaCodiceEnteRule = modiProperties.getSicurezzaMessaggio_corniceSicurezza_dynamic_codice_ente();
						}
					}
					
					String userMode = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this._listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE);
					String claimNameUser = modiProperties.getSicurezzaMessaggio_corniceSicurezza_rest_user();
					if(ModICostanti.MODIPA_PROFILO_RIDEFINISCI.equals(userMode)) {
						this.corniceSicurezzaUserRule = convertToList(ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(this._listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER));
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
							this.corniceSicurezzaUserRule = modiProperties.getSicurezzaMessaggio_corniceSicurezza_dynamic_user();
						}
					}
					
					String ipUserMode = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this._listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE);
					String claimNameIpUser = modiProperties.getSicurezzaMessaggio_corniceSicurezza_rest_ipuser();
					if(ModICostanti.MODIPA_PROFILO_RIDEFINISCI.equals(ipUserMode)) {
						this.corniceSicurezzaIpUserRule = convertToList(ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(this._listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER));
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
							this.corniceSicurezzaIpUserRule = modiProperties.getSicurezzaMessaggio_corniceSicurezza_dynamic_ipuser();
						}
					}
				}catch(Exception e) {
					throw new ProtocolException(e.getMessage(),e);
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
			if(this.audience==null) {
				if(sa!=null) {
					this.audience = ProtocolPropertiesUtils.getOptionalStringValuePropertyConfig(sa.getProtocolPropertyList(), ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE);
				}
			}
			
			// 2. Provo ad utilizzare il clientId presente nel token
			if(this.audience==null) {
				if(bustaRichiesta!=null) {
					this.audience = bustaRichiesta.getProperty(rest ? 
							ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_CLIENT_ID :
							ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_SOAP_WSA_FROM);
				}
			}
			
			// 3. Provo ad utilizzare il sub presente nel token
			if(this.audience==null) {
				if(bustaRichiesta!=null) {
					this.audience = bustaRichiesta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_SUBJECT);
				}
			}
			
			// 4. Utilizzo quello di default associato al 'sa' se identificato
			if(this.audience==null && sa!=null) {
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
						//(fruizione ? ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_FILTRO_DUPLICATI : 
							ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_FILTRO_DUPLICATI;
						//	);
				String multipleHeaderUseJtiAuthorizationAsIdMessaggio = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this._listProtocolProperties, idPropertyMultipleHeaderUseJtiAuthorizationAsIdMessaggio);
				if(multipleHeaderUseJtiAuthorizationAsIdMessaggio!=null) {
					this.multipleHeaderUseJtiAuthorizationAsIdMessaggio = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_FILTRO_DUPLICATI_VALUE_AUTHORIZATION.equals(multipleHeaderUseJtiAuthorizationAsIdMessaggio);
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
	
	public ModISecurityConfig(OpenSPCoop2Message msg, IProtocolFactory<?> protocolFactory, IState state, RequestInfo requestInfo, IDSoggetto soggettoFruitore, 
			AccordoServizioParteComune aspc, AccordoServizioParteSpecifica aspsParam, ServizioApplicativo sa, boolean rest, boolean fruizione, boolean request,
			Boolean multipleHeaderAuthorizationConfig) throws ProtocolException {
		
		// METODO USATO IN VALIDAZIONE
		
		ModIProperties modiProperties = ModIProperties.getInstance();
		
		this.fruizione = fruizione;
		
		this.multipleHeaderAuthorizationConfig = multipleHeaderAuthorizationConfig;
		
		this._listProtocolProperties = ModIPropertiesUtils.getProtocolProperties(fruizione, soggettoFruitore, aspsParam);
		
		if(this._listProtocolProperties==null || this._listProtocolProperties.isEmpty()) {
			throw new ProtocolException("Configurazione della sicurezza incompleta");
		}
		
		if(rest) {
			this.initSharedRest(modiProperties, this._listProtocolProperties,null, fruizione,request);
		}
		else {
			this.initSharedSoap(this._listProtocolProperties,fruizione,request);
		}
		
		try {
		
			/* TTL */
			
			if(fruizione) {
				if(!request) {
					boolean greatherThanZero = true;
					String mode = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this._listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_IAT);
					if(ModICostanti.MODIPA_PROFILO_RIDEFINISCI.equals(mode)) {
						Long l = ProtocolPropertiesUtils.getRequiredNumberValuePropertyRegistry(this._listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_IAT_SECONDS, greatherThanZero);
						if(l!=null && l.longValue()>0) {
							this.checkTtlIatMs = l.longValue() * 1000; // trasformo in ms
						}
					}
				}
			}
			else {
				if(request) {
					boolean greatherThanZero = true;
					String mode = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this._listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_IAT);
					if(ModICostanti.MODIPA_PROFILO_RIDEFINISCI.equals(mode)) {
						Long l = ProtocolPropertiesUtils.getRequiredNumberValuePropertyRegistry(this._listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_IAT_SECONDS, greatherThanZero);
						if(l!=null && l.longValue()>0) {
							this.checkTtlIatMs = l.longValue() * 1000; // trasformo in ms
						}
					}
				}
			}
			
			if(rest) {
				
				if(this.multipleHeaderAuthorizationConfig!=null) {
				
					// Opzioni 'jti' in header contemporanei
					
					String idPropertyMultipleHeaderUseJtiAuthorizationAsIdMessaggio = (fruizione ? ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_FILTRO_DUPLICATI : ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_FILTRO_DUPLICATI);
					String multipleHeaderUseJtiAuthorizationAsIdMessaggio = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this._listProtocolProperties, idPropertyMultipleHeaderUseJtiAuthorizationAsIdMessaggio);
					if(multipleHeaderUseJtiAuthorizationAsIdMessaggio!=null) {
						this.multipleHeaderUseJtiAuthorizationAsIdMessaggio = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_FILTRO_DUPLICATI_VALUE_AUTHORIZATION.equals(multipleHeaderUseJtiAuthorizationAsIdMessaggio);
					}
					
				}
			}
			
			if(!fruizione && request) {
				this.checkAudience = true;
				
				if(this.multipleHeaderAuthorizationConfig!=null && !this.multipleHeaderAuthorizationConfig) {
					String modalita = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this._listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_AUDIENCE);
					if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_AUDIENCE_VALUE_DIFFERENT.equals(modalita)) {
						this.audience = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this._listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_AUDIENCE_INTEGRITY);
					}
				}
				if(this.audience==null) {
					this.audience = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this._listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE);
				}
				if(this.audience==null) {
					
					List<String> tags = new ArrayList<String>();
					if(aspc!=null && aspc.getGruppi()!=null && aspc.getGruppi().sizeGruppoList()>0) {
						for (int i = 0; i < aspc.getGruppi().sizeGruppoList(); i++) {
							tags.add(aspc.getGruppi().getGruppo(i).getNome());
						}
					}
					
					IConfigIntegrationReader configReader = protocolFactory.getCachedConfigIntegrationReader(state, requestInfo);
					CanaliConfigurazione canaliConfigurazione = configReader.getCanaliConfigurazione();
					String canaleApi = null;
					if(aspc!=null) {
						canaleApi = aspc.getCanale();
					}
					String canalePorta = null;
					if(msg!=null) {
						Object nomePortaInvocataObject = msg.getContextProperty(CostantiPdD.NOME_PORTA_INVOCATA);
						if(nomePortaInvocataObject!=null && nomePortaInvocataObject instanceof String) {
							String nomePorta = (String) nomePortaInvocataObject;
							try {
								IDPortaApplicativa idPA = new IDPortaApplicativa();
								idPA.setNome(nomePorta);
								PortaApplicativa pa = configReader.getPortaApplicativa(idPA);
								canalePorta = pa.getNome();
							}catch(Throwable t) {}
						}
					}
					String canale = CanaliUtils.getCanale(canaliConfigurazione, canaleApi, canalePorta);
					
					UrlInvocazioneAPI urlInvocazioneApi = ConfigurazionePdDManager.getInstance().getConfigurazioneUrlInvocazione(protocolFactory, 
							RuoloContesto.PORTA_APPLICATIVA,
							rest ? org.openspcoop2.message.constants.ServiceBinding.REST : org.openspcoop2.message.constants.ServiceBinding.SOAP,
									msg!=null ? msg.getTransportRequestContext().getInterfaceName() : null,
									new IDSoggetto(aspsParam.getTipoSoggettoErogatore(), aspsParam.getNomeSoggettoErogatore()),
									tags, canale, 
									requestInfo);		 
					String prefixGatewayUrl = urlInvocazioneApi.getBaseUrl();
					String contesto = urlInvocazioneApi.getContext();
					this.audience = Utilities.buildUrl(prefixGatewayUrl, contesto);
				}
				if(this.audience==null) {
					throw new ProtocolException("Audience undefined");
				}
			}
			else if(fruizione && !request) {
			
				if(this.multipleHeaderAuthorizationConfig!=null && !this.multipleHeaderAuthorizationConfig) {
					String modalita = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this._listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_AUDIENCE);
					if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_AUDIENCE_VALUE_DIFFERENT.equals(modalita)) {
						this.audience = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this._listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_AUDIENCE_INTEGRITY);
					}
				}
				if(this.audience==null) {
					this.audience = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(this._listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE_VALORE);
				}
				if(this.audience==null) {
					if(sa!=null) {
						this.audience = ProtocolPropertiesUtils.getOptionalStringValuePropertyConfig(sa.getProtocolPropertyList(), ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE);
						// 	Se non definito sull'applicativo, non viene serializzato.
						if(this.audience==null) {
							// provo a vedere se viene utilizzato il valore di default
							this.audience=NamingUtils.getLabelSoggetto(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()))+"/"+sa.getNome();
						}
					}
				}
				this.checkAudience = ProtocolPropertiesUtils.getBooleanValuePropertyRegistry(this._listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE, false);
				if(this.checkAudience) {
					if(this.audience==null) {
						throw new ProtocolException("Configurazione errata; Audience non definito sull'applicativo e verifica abilitata sulla fruizione");
					}
				}
			}
			else {
				
				throw new ProtocolException("Use unsupported");
				
			}
			
		}catch(Exception e) {
			throw new ProtocolException(e);
		}
		
	}
	
	private void initSharedRest(ModIProperties modiProperties, List<ProtocolProperty> listProtocolProperties, ServizioApplicativo sa, boolean fruizione, boolean request) throws ProtocolException {
		
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
						
						String digestEncoding = null;
						if(request) {
							digestEncoding = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_DIGEST_ENCODING);
						}
						else {
							digestEncoding = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_DIGEST_ENCODING);
						}
						if(digestEncoding!=null) {
							this.digestEncoding = DigestEncoding.valueOf(digestEncoding.trim().toUpperCase());
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
				try {
					this.x5url = ProtocolPropertiesUtils.getRequiredStringValuePropertyConfig(sa.getProtocolPropertyList(), ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_SA_RICHIESTA_X509_VALUE_X5URL);
				}catch(Exception e) {
					ProtocolException pe = new ProtocolException("Applicativo '"+sa.getNome()+"' non utilizzabile con la configurazione di sicurezza (x5u) associata alla fruizione richiesta, poichè non contiene la definizione di una URL che riferisce un certificato (o certificate chain) X.509 corrispondente alla chiave firmataria del security token");
					pe.setInteroperabilityError(true);
					throw pe;
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
	
	private void initSharedSoap(List<ProtocolProperty> listProtocolProperties, boolean fruizione, boolean request) throws ProtocolException {
		
		if(fruizione) {
			if(request) {
				this.algorithm = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_ALG);
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
		
//		String x509 = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_RIFERIMENTO_X509);
//		if(!request) {
//			String profiloSicurezzaMessaggioRifX509AsRequestItemValue =  ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_AS_REQUEST_ID);
//			if(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_AS_REQUEST_VALUE_FALSE.equals(profiloSicurezzaMessaggioRifX509AsRequestItemValue)){
//				x509 = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509);
//			}
//		}
//		List<String> vX509 = ProtocolPropertiesUtils.getListFromMultiSelectValue(x509);
//		if(vX509.contains(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_VALUE_X5U)) {
//			this.x5u = true;
//			if(fruizione && request) {
//				this.x5url = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_X509_VALUE_X5URL);
//			}
//			else if(!fruizione && !request) {
//				this.x5url = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_X509_VALUE_X5URL);
//			}
//		}
//		if(vX509.contains(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_VALUE_X5C)) {
//			this.x5c = true;
//		}
//		if(vX509.contains(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_VALUE_X5T)) {
//			this.x5t = true;
//		}
		
	}
	
	public static List<String> convertToList(String value) throws Exception{
		List<String> l = null;
		if (value != null){
			value = value.trim();
			if("".equals(value)) {
				throw new Exception("non definita");
			}
			l = new ArrayList<String>();
			if(value.contains(",")) {
				String [] tmp = value.split(",");
				if(tmp==null || tmp.length<=0) {
					throw new Exception("non definita");
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
					throw new Exception("non definita");
				}
			}
			else {
				l.add(value);
			}
		}
		else {
			throw new Exception("non definita");
		}
		return l;
	}
	

	public String getAlgorithm() {
		return this.algorithm;
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
	
	public boolean isCheckAudience() {
		return this.checkAudience;
	}

	public Properties getClaims() {
		return this.claims;
	}

	public Properties getMultipleHeaderClaims() {
		return this.multipleHeaderClaims;
	}
}
