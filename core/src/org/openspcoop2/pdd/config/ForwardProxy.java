/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.config;

import java.io.Serializable;

import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDGenericProperties;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.GruppoAccordo;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.state.RequestInfo;

/**
 * ForwardProxy
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ForwardProxy implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String GOVWAY_SEPARATORE = "-"; 
	
	public static final String GOVWAY_PREFISSO_TAG_PROXY = "tag"+GOVWAY_SEPARATORE;
	public static final String GOVWAY_PREFISSO_DOMINIO_PROXY = "dominio"+GOVWAY_SEPARATORE;
	public static final String GOVWAY_PREFISSO_PROTOCOLLO_PROXY = "profilo"+GOVWAY_SEPARATORE;
	
	public static final String GOVWAY_PROXY_ENABLED = "govway-proxy-enable";
	
	public static final String GOVWAY_PROXY = "govway-proxy";
	public static final String GOVWAY_FRUIZIONI_PROXY = "govway-fruizioni-proxy";
	public static final String GOVWAY_EROGAZIONI_PROXY = "govway-erogazioni-proxy";
	
	public static final String GOVWAY_PROXY_HEADER = "govway-proxy-header";
	public static final String GOVWAY_FRUIZIONI_PROXY_HEADER = "govway-fruizioni-proxy-header";
	public static final String GOVWAY_EROGAZIONI_PROXY_HEADER = "govway-erogazioni-proxy-header";
	public static final String GOVWAY_PROXY_HEADER_BASE64 = "govway-proxy-header-base64";
	public static final String GOVWAY_FRUIZIONI_PROXY_HEADER_BASE64 = "govway-fruizioni-proxy-header-base64";
	public static final String GOVWAY_EROGAZIONI_PROXY_HEADER_BASE64 = "govway-erogazioni-proxy-header-base64";
	
	public static final String GOVWAY_PROXY_QUERY = "govway-proxy-query";
	public static final String GOVWAY_FRUIZIONI_PROXY_QUERY = "govway-fruizioni-proxy-query";
	public static final String GOVWAY_EROGAZIONI_PROXY_QUERY = "govway-erogazioni-proxy-query";
	public static final String GOVWAY_PROXY_QUERY_BASE64 = "govway-proxy-query-base64";
	public static final String GOVWAY_FRUIZIONI_PROXY_QUERY_BASE64 = "govway-fruizioni-proxy-query-base64";
	public static final String GOVWAY_EROGAZIONI_PROXY_QUERY_BASE64 = "govway-erogazioni-proxy-query-base64";
	
	public static final String DEFAULT_GOVWAY_PROXY_HEADER = "GovWay-APIAddress";
	public static final boolean DEFAULT_GOVWAY_PROXY_HEADER_BASE64 = false; 
	public static final String DEFAULT_GOVWAY_PROXY_QUERY = "govway_api_address";
	public static final boolean DEFAULT_GOVWAY_PROXY_QUERY_BASE64 = false; 
	
	public static final String GOVWAY_PROXY_TOKEN_DYNAMIC_DISCOVERY = "govway-proxy-token-dynamic-discovery";
	public static final String GOVWAY_FRUIZIONI_PROXY_TOKEN_DYNAMIC_DISCOVERY = "govway-fruizioni-proxy-token-dynamic-discovery";
	public static final String GOVWAY_EROGAZIONI_PROXY_TOKEN_DYNAMIC_DISCOVERY = "govway-erogazioni-proxy-token-dynamic-discovery";
	public static final String GOVWAY_PROXY_TOKEN_VALIDAZIONE_JWT = "govway-proxy-token-jwt-validation";
	public static final String GOVWAY_FRUIZIONI_PROXY_TOKEN_VALIDAZIONE_JWT = "govway-fruizioni-proxy-token-jwt-validation";
	public static final String GOVWAY_EROGAZIONI_PROXY_TOKEN_VALIDAZIONE_JWT = "govway-erogazioni-proxy-token-jwt-validation";
	public static final String GOVWAY_PROXY_TOKEN_INTROSPECTION = "govway-proxy-token-introspection";
	public static final String GOVWAY_FRUIZIONI_PROXY_TOKEN_INTROSPECTION = "govway-fruizioni-proxy-token-introspection";
	public static final String GOVWAY_EROGAZIONI_PROXY_TOKEN_INTROSPECTION = "govway-erogazioni-proxy-token-introspection";
	public static final String GOVWAY_PROXY_TOKEN_USERINFO = "govway-proxy-token-userinfo";
	public static final String GOVWAY_FRUIZIONI_PROXY_TOKEN_USERINFO = "govway-fruizioni-proxy-token-userinfo";
	public static final String GOVWAY_EROGAZIONI_PROXY_TOKEN_USERINFO = "govway-erogazioni-proxy-token-userinfo";
	
	public static final String GOVWAY_PROXY_TOKEN_RETRIEVE = "govway-proxy-token-retrieve";
	public static final String GOVWAY_FRUIZIONI_PROXY_TOKEN_RETRIEVE = "govway-fruizioni-proxy-token-retrieve";
	public static final String GOVWAY_EROGAZIONI_PROXY_TOKEN_RETRIEVE = "govway-erogazioni-proxy-token-retrieve";
	
	public static final String GOVWAY_PROXY_ATTRIBUTE_AUTHORITY = "govway-proxy-attribute-authority";
	public static final String GOVWAY_FRUIZIONI_PROXY_ATTRIBUTE_AUTHORITY = "govway-fruizioni-proxy-attribute-authority";
	public static final String GOVWAY_EROGAZIONI_PROXY_ATTRIBUTE_AUTHORITY = "govway-erogazioni-proxy-attribute-authority";
	public static final String GOVWAY_PROXY_ATTRIBUTE_AUTHORITY_RESPONSE_JWT_VALIDATION = "govway-proxy-attribute-authority-response-jwt-validation";
	public static final String GOVWAY_FRUIZIONI_PROXY_ATTRIBUTE_AUTHORITY_RESPONSE_JWT_VALIDATION = "govway-fruizioni-proxy-attribute-authority-response-jwt-validation";
	public static final String GOVWAY_EROGAZIONI_PROXY_ATTRIBUTE_AUTHORITY_RESPONSE_JWT_VALIDATION = "govway-erogazioni-proxy-attribute-authority-response-jwt-validation";
	
	public static final boolean DEFAULT_GOVWAY_PROXY_TOKEN_DYNAMIC_DISCOVERY = false;
	public static final boolean DEFAULT_GOVWAY_PROXY_TOKEN_VALIDAZIONE_JWT = false;
	public static final boolean DEFAULT_GOVWAY_PROXY_TOKEN_INTROSPECTION = false;
	public static final boolean DEFAULT_GOVWAY_PROXY_TOKEN_USERINFO = false;
	
	public static final boolean DEFAULT_GOVWAY_PROXY_TOKEN_RETRIEVE = false;
	
	public static final boolean DEFAULT_GOVWAY_PROXY_ATTRIBUTE_AUTHORITY = false;
	public static final boolean DEFAULT_GOVWAY_PROXY_ATTRIBUTE_AUTHORITY_RESPONSE_JWT_VALIDATION = false;
	
	private static final String TRUE = "true";
	private static final String FALSE = "false";
	
	public static boolean isProxyEnabled() {
		String enable = System.getProperty(GOVWAY_PROXY_ENABLED);
		if(enable!=null && !"".equals(enable)) {
			return TRUE.equalsIgnoreCase(enable);
		}
		return OpenSPCoop2Properties.getInstance().isForwardProxyEnable();
	}
	
	
	public static ForwardProxy getProxyConfigurazione(boolean fruizione, IDSoggetto dominio, IDServizio idServizio, IDGenericProperties policy, RequestInfo requestInfo) throws DriverConfigurazioneException{
		
		try {
			
			// **** Configurazione di default ****
			
			// Cerco se ne esiste una specifica per il protocollo e la fruizione/erogazione
			ForwardProxyConfigurazione defaultConfig = readForwardProxyConfigurazione(true, dominio.getTipo(), fruizione);
			
			// Cerco se ne esiste una specifica per il protocollo che vale sia per la fruizione che per l'erogazione
			if(defaultConfig==null) {
				defaultConfig = readForwardProxyConfigurazione(true, dominio.getTipo(), null);
			}
			
			// Cerco se ne esiste una specifica per la fruizione/erogazione
			if(defaultConfig==null) {
				defaultConfig = readForwardProxyConfigurazione(false, null, fruizione);
			}
			
			// Cerco se ne esiste una che vale sia per la fruizione che per l'erogazione
			if(defaultConfig==null) {
				defaultConfig = readForwardProxyConfigurazione(false, null, null);
			}
			
			// Configurazione di default
			if(defaultConfig==null) {
				defaultConfig = OpenSPCoop2Properties.getInstance().getForwardProxyConfigurazioneDefault();
			}
			
			
			// **** Configurazione di default per la gestione dei token ****
			
			ForwardProxyConfigurazioneToken defaultConfigToken = null;
			
			if(policy!=null && policy.getNome()!=null) { // chiamata arriva da una token policy
			
				// Cerco se ne esiste una specifica per il protocollo e la fruizione/erogazione con il nome della policy
				defaultConfigToken = readForwardProxyConfigurazioneToken(true, dominio.getTipo(), fruizione, policy);
				
				// Cerco se ne esiste una specifica per il protocollo che vale sia per la fruizione che per l'erogazione con il nome della policy
				if(defaultConfigToken==null) {
					defaultConfigToken = readForwardProxyConfigurazioneToken(true, dominio.getTipo(), null, policy);
				}
				
				// Cerco se ne esiste una specifica per la fruizione/erogazione con il nome della policy
				if(defaultConfigToken==null) {
					defaultConfigToken = readForwardProxyConfigurazioneToken(false, null, fruizione, policy);
				}
				
				// Cerco se ne esiste una che vale sia per la fruizione che per l'erogazione con il nome della policy
				if(defaultConfigToken==null) {
					defaultConfigToken = readForwardProxyConfigurazioneToken(false, null, null, policy);
				}
				
			}
			
			// Cerco se ne esiste una specifica per il protocollo e la fruizione/erogazione per qualsiasi policy
			if(defaultConfigToken==null) {
				defaultConfigToken = readForwardProxyConfigurazioneToken(true, dominio.getTipo(), fruizione, null);
			}
			
			// Cerco se ne esiste una specifica per il protocollo che vale sia per la fruizione che per l'erogazione per qualsiasi policy
			if(defaultConfigToken==null) {
				defaultConfigToken = readForwardProxyConfigurazioneToken(true, dominio.getTipo(), null, null);
			}
			
			// Cerco se ne esiste una specifica per la fruizione/erogazione per qualsiasi policy
			if(defaultConfigToken==null) {
				defaultConfigToken = readForwardProxyConfigurazioneToken(false, null, fruizione, null);
			}
			
			// Cerco se ne esiste una che vale sia per la fruizione che per l'erogazione per qualsiasi policy
			if(defaultConfigToken==null) {
				defaultConfigToken = readForwardProxyConfigurazioneToken(false, null, null, null);
			}
			
			// Configurazione di default
			if(defaultConfigToken==null) {
				defaultConfigToken = OpenSPCoop2Properties.getInstance().getForwardProxyConfigurazioneTokenDefault();
			}
					
			
			
			// **** Regola ****
			
			// primo cerco per tag
			RegistroServiziManager registroServiziManager = RegistroServiziManager.getInstance();
			AccordoServizioParteSpecifica asps = registroServiziManager.getAccordoServizioParteSpecifica(idServizio, null, false, requestInfo);
			IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
			AccordoServizioParteComune aspc = registroServiziManager.getAccordoServizioParteComune(idAccordo, null, false, false, requestInfo);
			if(aspc.getGruppi()!=null && aspc.getGruppi().sizeGruppoList()>0) {
				for (GruppoAccordo gruppo : aspc.getGruppi().getGruppoList()) {
					// prima per specifica fruizione/erogazione
					ForwardProxy regola = ForwardProxy.readForwardProxyTag(gruppo.getNome(), fruizione, policy,
							defaultConfig, defaultConfigToken);
					if(regola!=null && regola.isEnabled()) {
						return regola;
					}
					// poi per configurazione che vale sia per la fruizione che per l'erogazione
					regola = ForwardProxy.readForwardProxyTag(gruppo.getNome(), null, policy,
							defaultConfig, defaultConfigToken);
					if(regola!=null && regola.isEnabled()) {
						return regola;
					}
				}
			}
			
			// poi cerco per dominio
			
			// prima per specifico tipo (es. modipa, gw, aoo, spc) e nome soggetto per specifica fruizione/erogazione
			ForwardProxy regola = ForwardProxy.readConfigurazioneDominioByTipo(dominio.getTipo(), dominio.getNome(), fruizione, policy,
					defaultConfig, defaultConfigToken);
			if(regola!=null && regola.isEnabled()) {
				return regola;
			}
			// poi per specifico tipo (es. modipa, gw, aoo, spc) e nome soggetto che vale sia per la fruizione che per l'erogazione
			regola = ForwardProxy.readConfigurazioneDominioByTipo(dominio.getTipo(), dominio.getNome(), null, policy,
					defaultConfig, defaultConfigToken);
			if(regola!=null && regola.isEnabled()) {
				return regola;
			}
			
			// poi più generico sul protocollo e nome soggetto per specifica fruizione/erogazione
			regola = ForwardProxy.readConfigurazioneDominioByProtocollo(dominio.getTipo(), dominio.getNome(), fruizione, policy,
					defaultConfig, defaultConfigToken);
			if(regola!=null && regola.isEnabled()) {
				return regola;
			}
			// poi più generico sul protocollo e nome soggetto che vale sia per la fruizione che per l'erogazione
			regola = ForwardProxy.readConfigurazioneDominioByProtocollo(dominio.getTipo(), dominio.getNome(), null, policy,
					defaultConfig, defaultConfigToken);
			if(regola!=null && regola.isEnabled()) {
				return regola;
			}
			
			// poi solamente per nome soggetto per specifica fruizione/erogazione
			regola = ForwardProxy.readConfigurazioneDominio(dominio.getNome(), fruizione, policy,
					defaultConfig, defaultConfigToken);
			if(regola!=null && regola.isEnabled()) {
				return regola;
			}
			// poi solamente per nome soggetto che vale sia per la fruizione che per l'erogazione
			regola = ForwardProxy.readConfigurazioneDominio(dominio.getNome(), null, policy,
					defaultConfig, defaultConfigToken);
			if(regola!=null && regola.isEnabled()) {
				return regola;
			}
			
			
			// poi cerco per protocollo per specifica fruizione/erogazione
			regola = ForwardProxy.readConfigurazioneProtocollo(dominio.getTipo(), fruizione, policy,
					defaultConfig, defaultConfigToken);
			if(regola!=null && regola.isEnabled()) {
				return regola;
			}
			// poi cerco per protocollo che vale sia per la fruizione che per l'erogazione
			regola = ForwardProxy.readConfigurazioneProtocollo(dominio.getTipo(), null, policy,
					defaultConfig, defaultConfigToken);
			if(regola!=null && regola.isEnabled()) {
				return regola;
			}
			
			
			// poi cerco configurazione generica per specifica fruizione/erogazione
			regola = ForwardProxy.readConfigurazione(fruizione, policy,
					defaultConfig, defaultConfigToken);
			if(regola!=null && regola.isEnabled()) {
				return regola;
			}
			// altrimenti ritorno regola di default (eventualmente disabilitata per motivi di caching)
			return ForwardProxy.readConfigurazione(null, policy,
					defaultConfig, defaultConfigToken);
			
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		
	}
	
	
	private static ForwardProxyConfigurazione readForwardProxyConfigurazione(boolean byProtocollo, String tipoSoggetto, Boolean fruizione) throws ProtocolException {
		
		String systemPropertyProxyHeader = GOVWAY_PROXY_HEADER;
		String systemPropertyProxyHeaderBase64 = GOVWAY_PROXY_HEADER_BASE64;
		String systemPropertyProxyQuery = GOVWAY_PROXY_QUERY;
		String systemPropertyProxyQueryBase64 = GOVWAY_PROXY_QUERY_BASE64;
		
		if(fruizione!=null) {
			systemPropertyProxyHeader = fruizione ? GOVWAY_FRUIZIONI_PROXY_HEADER : GOVWAY_EROGAZIONI_PROXY_HEADER;
			systemPropertyProxyHeaderBase64 = fruizione ? GOVWAY_FRUIZIONI_PROXY_HEADER_BASE64 : GOVWAY_EROGAZIONI_PROXY_HEADER_BASE64;
			systemPropertyProxyQuery = fruizione ? GOVWAY_FRUIZIONI_PROXY_QUERY : GOVWAY_EROGAZIONI_PROXY_QUERY;
			systemPropertyProxyQueryBase64 = fruizione ? GOVWAY_FRUIZIONI_PROXY_QUERY_BASE64 : GOVWAY_EROGAZIONI_PROXY_QUERY_BASE64;
		}
		
		if(byProtocollo) {
			String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(tipoSoggetto);
			systemPropertyProxyHeader= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyHeader;
			systemPropertyProxyHeaderBase64= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyHeaderBase64;
			systemPropertyProxyQuery= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyQuery;
			systemPropertyProxyQueryBase64= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyQueryBase64;
		}
		
		String header = System.getProperty(systemPropertyProxyHeader);
		String query = System.getProperty(systemPropertyProxyQuery);
			
		if( 
				(header==null || "".equals(header)) &&
				(query==null || "".equals(query)) 
			) {
			// verrà usata quella di default
			return null;
		}
		
		ForwardProxyConfigurazione config = new ForwardProxyConfigurazione();
				
		if(header!=null && !"".equals(header)) {
			config.setHeader(header);
			
			String base64 = System.getProperty(systemPropertyProxyHeaderBase64);
			if(base64!=null && !"".equals(base64)) {
				if(TRUE.equalsIgnoreCase(base64)) {
					config.setHeaderBase64(true);
				}
				else if(FALSE.equalsIgnoreCase(base64)) {
					config.setHeaderBase64(false);
				}
				else {
					config.setHeaderBase64(DEFAULT_GOVWAY_PROXY_HEADER_BASE64);
				}
			}
			else {
				config.setHeaderBase64(DEFAULT_GOVWAY_PROXY_HEADER_BASE64);
			}
		}
		
		if(query!=null && !"".equals(query)) {
			config.setQuery(query);
			
			String base64 = System.getProperty(systemPropertyProxyQueryBase64);
			if(base64!=null && !"".equals(base64)) {
				if(TRUE.equalsIgnoreCase(base64)) {
					config.setQueryBase64(true);
				}
				else if(FALSE.equalsIgnoreCase(base64)) {
					config.setQueryBase64(false);
				}
				else {
					config.setQueryBase64(DEFAULT_GOVWAY_PROXY_QUERY_BASE64);
				}
			}
			else {
				config.setQueryBase64(DEFAULT_GOVWAY_PROXY_QUERY_BASE64);
			}
		}
		
		return config;
		
	}
	
	private static ForwardProxyConfigurazioneToken readForwardProxyConfigurazioneToken(boolean byProtocollo, String tipoSoggetto, Boolean fruizione, IDGenericProperties policy) throws ProtocolException {
		
		String systemPropertyProxyTokenDynamicDiscovery = GOVWAY_PROXY_TOKEN_DYNAMIC_DISCOVERY;
		String systemPropertyProxyTokenValidazioneJwt = GOVWAY_PROXY_TOKEN_VALIDAZIONE_JWT;
		String systemPropertyProxyTokenIntrospection = GOVWAY_PROXY_TOKEN_INTROSPECTION;
		String systemPropertyProxyTokenUserInfo = GOVWAY_PROXY_TOKEN_USERINFO;
		String systemPropertyProxyTokenRetrieve = GOVWAY_PROXY_TOKEN_RETRIEVE;
		String systemPropertyProxyAttributeAuthority = GOVWAY_PROXY_ATTRIBUTE_AUTHORITY;
		String systemPropertyProxyAttributeAuthorityResponseJwtValidation = GOVWAY_PROXY_ATTRIBUTE_AUTHORITY_RESPONSE_JWT_VALIDATION;
		
		if(fruizione!=null) {
			systemPropertyProxyTokenDynamicDiscovery = fruizione ? GOVWAY_FRUIZIONI_PROXY_TOKEN_DYNAMIC_DISCOVERY : GOVWAY_EROGAZIONI_PROXY_TOKEN_DYNAMIC_DISCOVERY;
			systemPropertyProxyTokenValidazioneJwt = fruizione ? GOVWAY_FRUIZIONI_PROXY_TOKEN_VALIDAZIONE_JWT : GOVWAY_EROGAZIONI_PROXY_TOKEN_VALIDAZIONE_JWT;
			systemPropertyProxyTokenIntrospection = fruizione ? GOVWAY_FRUIZIONI_PROXY_TOKEN_INTROSPECTION : GOVWAY_EROGAZIONI_PROXY_TOKEN_INTROSPECTION;
			systemPropertyProxyTokenUserInfo = fruizione ? GOVWAY_FRUIZIONI_PROXY_TOKEN_USERINFO : GOVWAY_EROGAZIONI_PROXY_TOKEN_USERINFO;
			systemPropertyProxyTokenRetrieve = fruizione ? GOVWAY_FRUIZIONI_PROXY_TOKEN_RETRIEVE : GOVWAY_EROGAZIONI_PROXY_TOKEN_RETRIEVE;
			systemPropertyProxyAttributeAuthority = fruizione ? GOVWAY_FRUIZIONI_PROXY_ATTRIBUTE_AUTHORITY : GOVWAY_EROGAZIONI_PROXY_ATTRIBUTE_AUTHORITY;
			systemPropertyProxyAttributeAuthorityResponseJwtValidation = fruizione ? GOVWAY_FRUIZIONI_PROXY_ATTRIBUTE_AUTHORITY_RESPONSE_JWT_VALIDATION : GOVWAY_EROGAZIONI_PROXY_ATTRIBUTE_AUTHORITY_RESPONSE_JWT_VALIDATION;
		}
		
		if(byProtocollo) {
			String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(tipoSoggetto);
			systemPropertyProxyTokenDynamicDiscovery= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyTokenDynamicDiscovery;
			systemPropertyProxyTokenValidazioneJwt= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyTokenValidazioneJwt;
			systemPropertyProxyTokenIntrospection= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyTokenIntrospection;
			systemPropertyProxyTokenUserInfo= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyTokenUserInfo;
			systemPropertyProxyTokenRetrieve= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyTokenRetrieve;
			systemPropertyProxyAttributeAuthority= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyAttributeAuthority;
			systemPropertyProxyAttributeAuthorityResponseJwtValidation= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyAttributeAuthorityResponseJwtValidation;
		}
		
		if(policy!=null && policy.getNome()!=null) {
			systemPropertyProxyTokenDynamicDiscovery+=GOVWAY_SEPARATORE+policy.getNome();
			systemPropertyProxyTokenValidazioneJwt+=GOVWAY_SEPARATORE+policy.getNome();
			systemPropertyProxyTokenIntrospection+=GOVWAY_SEPARATORE+policy.getNome();
			systemPropertyProxyTokenUserInfo+=GOVWAY_SEPARATORE+policy.getNome();
			systemPropertyProxyTokenRetrieve+=GOVWAY_SEPARATORE+policy.getNome();
			systemPropertyProxyAttributeAuthority+=GOVWAY_SEPARATORE+policy.getNome();
			systemPropertyProxyAttributeAuthorityResponseJwtValidation+=GOVWAY_SEPARATORE+policy.getNome();
		}
		
		String dynamicDiscovery = System.getProperty(systemPropertyProxyTokenDynamicDiscovery);
		String validazioneJwt = System.getProperty(systemPropertyProxyTokenValidazioneJwt);
		String introspection = System.getProperty(systemPropertyProxyTokenIntrospection);
		String userInfo = System.getProperty(systemPropertyProxyTokenUserInfo);
		String retrieve = System.getProperty(systemPropertyProxyTokenRetrieve);
		String attributeAuthority = System.getProperty(systemPropertyProxyAttributeAuthority);
		String attributeAuthorityResponseJwtValidation = System.getProperty(systemPropertyProxyAttributeAuthorityResponseJwtValidation);
			
		if( 
				(dynamicDiscovery==null || "".equals(dynamicDiscovery)) &&
				(validazioneJwt==null || "".equals(validazioneJwt)) &&
				(introspection==null || "".equals(introspection)) &&
				(userInfo==null || "".equals(userInfo)) &&
				(retrieve==null || "".equals(retrieve)) &&
				(attributeAuthority==null || "".equals(attributeAuthority)) &&
				(attributeAuthorityResponseJwtValidation==null || "".equals(attributeAuthorityResponseJwtValidation)) 
			) {
			// verrà usata quella di default
			return null;
		}
		
		ForwardProxyConfigurazioneToken config = new ForwardProxyConfigurazioneToken();
			
		if(dynamicDiscovery!=null && !"".equals(dynamicDiscovery)) {
			if(TRUE.equalsIgnoreCase(dynamicDiscovery)) {
				config.tokenDynamicDiscoveryEnabled = true;
			}
			else if(FALSE.equalsIgnoreCase(dynamicDiscovery)) {
				config.tokenDynamicDiscoveryEnabled = false;
			}
			else {
				config.tokenDynamicDiscoveryEnabled = DEFAULT_GOVWAY_PROXY_TOKEN_DYNAMIC_DISCOVERY;
			}
		}
		else {
			config.tokenDynamicDiscoveryEnabled = DEFAULT_GOVWAY_PROXY_TOKEN_DYNAMIC_DISCOVERY;
		}
		
		if(validazioneJwt!=null && !"".equals(validazioneJwt)) {
			if(TRUE.equalsIgnoreCase(validazioneJwt)) {
				config.tokenJwtValidationEnabled = true;
			}
			else if(FALSE.equalsIgnoreCase(validazioneJwt)) {
				config.tokenJwtValidationEnabled = false;
			}
			else {
				config.tokenJwtValidationEnabled = DEFAULT_GOVWAY_PROXY_TOKEN_VALIDAZIONE_JWT;
			}
		}
		else {
			config.tokenJwtValidationEnabled = DEFAULT_GOVWAY_PROXY_TOKEN_VALIDAZIONE_JWT;
		}
		
		if(introspection!=null && !"".equals(introspection)) {
			if(TRUE.equalsIgnoreCase(introspection)) {
				config.tokenIntrospectionEnabled = true;
			}
			else if(FALSE.equalsIgnoreCase(introspection)) {
				config.tokenIntrospectionEnabled = false;
			}
			else {
				config.tokenIntrospectionEnabled = DEFAULT_GOVWAY_PROXY_TOKEN_INTROSPECTION;
			}
		}
		else {
			config.tokenIntrospectionEnabled = DEFAULT_GOVWAY_PROXY_TOKEN_INTROSPECTION;
		}
				
		if(userInfo!=null && !"".equals(userInfo)) {
			if(TRUE.equalsIgnoreCase(userInfo)) {
				config.tokenUserInfoEnabled = true;
			}
			else if(FALSE.equalsIgnoreCase(userInfo)) {
				config.tokenUserInfoEnabled = false;
			}
			else {
				config.tokenUserInfoEnabled = DEFAULT_GOVWAY_PROXY_TOKEN_USERINFO;
			}
		}
		else {
			config.tokenUserInfoEnabled = DEFAULT_GOVWAY_PROXY_TOKEN_USERINFO;
		}
		
		if(retrieve!=null && !"".equals(retrieve)) {
			if(TRUE.equalsIgnoreCase(retrieve)) {
				config.tokenRetrieveEnabled = true;
			}
			else if(FALSE.equalsIgnoreCase(retrieve)) {
				config.tokenRetrieveEnabled = false;
			}
			else {
				config.tokenRetrieveEnabled = DEFAULT_GOVWAY_PROXY_TOKEN_RETRIEVE;
			}
		}
		else {
			config.tokenRetrieveEnabled = DEFAULT_GOVWAY_PROXY_TOKEN_RETRIEVE;
		}
		
		if(attributeAuthority!=null && !"".equals(attributeAuthority)) {
			if(TRUE.equalsIgnoreCase(attributeAuthority)) {
				config.attributeAuthorityEnabled = true;
			}
			else if(FALSE.equalsIgnoreCase(attributeAuthority)) {
				config.attributeAuthorityEnabled = false;
			}
			else {
				config.attributeAuthorityEnabled = DEFAULT_GOVWAY_PROXY_ATTRIBUTE_AUTHORITY;
			}
		}
		else {
			config.attributeAuthorityEnabled = DEFAULT_GOVWAY_PROXY_ATTRIBUTE_AUTHORITY;
		}
		
		if(attributeAuthorityResponseJwtValidation!=null && !"".equals(attributeAuthorityResponseJwtValidation)) {
			if(TRUE.equalsIgnoreCase(attributeAuthorityResponseJwtValidation)) {
				config.attributeAuthorityResponseJwtValidationEnabled = true;
			}
			else if(FALSE.equalsIgnoreCase(attributeAuthorityResponseJwtValidation)) {
				config.attributeAuthorityResponseJwtValidationEnabled = false;
			}
			else {
				config.attributeAuthorityResponseJwtValidationEnabled = DEFAULT_GOVWAY_PROXY_ATTRIBUTE_AUTHORITY_RESPONSE_JWT_VALIDATION;
			}
		}
		else {
			config.attributeAuthorityResponseJwtValidationEnabled = DEFAULT_GOVWAY_PROXY_ATTRIBUTE_AUTHORITY_RESPONSE_JWT_VALIDATION;
		}
		
		return config;
		
	}
	
	private static ForwardProxy readForwardProxyTag(String tag, Boolean fruizione, IDGenericProperties policy,
			ForwardProxyConfigurazione defaultConfig, ForwardProxyConfigurazioneToken defaultConfigToken) throws ProtocolException {
		return readForwardProxy(tag, false, null, null, fruizione, policy,
				defaultConfig, defaultConfigToken);
	}
	private static ForwardProxy readConfigurazioneDominioByTipo(String tipoSoggetto, String nomeSoggetto, Boolean fruizione, IDGenericProperties policy,
			ForwardProxyConfigurazione defaultConfig, ForwardProxyConfigurazioneToken defaultConfigToken) throws ProtocolException {
		return readForwardProxy(null, false, tipoSoggetto, nomeSoggetto, fruizione, policy,
				defaultConfig, defaultConfigToken);
	}
	private static ForwardProxy readConfigurazioneDominioByProtocollo(String tipoSoggetto, String nomeSoggetto, Boolean fruizione, IDGenericProperties policy,
			ForwardProxyConfigurazione defaultConfig, ForwardProxyConfigurazioneToken defaultConfigToken) throws ProtocolException {
		return readForwardProxy(null, true, tipoSoggetto, nomeSoggetto, fruizione, policy,
				defaultConfig, defaultConfigToken);
	}
	private static ForwardProxy readConfigurazioneDominio(String nomeSoggetto, Boolean fruizione, IDGenericProperties policy,
			ForwardProxyConfigurazione defaultConfig, ForwardProxyConfigurazioneToken defaultConfigToken) throws ProtocolException {
		return readForwardProxy(null, false, null, nomeSoggetto, fruizione, policy,
				defaultConfig, defaultConfigToken);
	}
	private static ForwardProxy readConfigurazioneProtocollo(String tipoSoggetto, Boolean fruizione, IDGenericProperties policy,
			ForwardProxyConfigurazione defaultConfig, ForwardProxyConfigurazioneToken defaultConfigToken) throws ProtocolException {
		return readForwardProxy(null, true, tipoSoggetto, null, fruizione, policy,
				defaultConfig, defaultConfigToken);
	}
	private static ForwardProxy readConfigurazione(Boolean fruizione, IDGenericProperties policy,
			ForwardProxyConfigurazione defaultConfig, ForwardProxyConfigurazioneToken defaultConfigToken) throws ProtocolException {
		return readForwardProxy(null, false, null, null, fruizione, policy,
				defaultConfig, defaultConfigToken);
	}
	private static ForwardProxy readForwardProxy(String tag, boolean byProtocollo, String tipoSoggetto, String nomeSoggetto, Boolean fruizione, IDGenericProperties policy,
			ForwardProxyConfigurazione defaultConfig, ForwardProxyConfigurazioneToken defaultConfigToken) throws ProtocolException {
		
		String systemPropertyProxy = GOVWAY_PROXY;
		String systemPropertyProxyHeader = GOVWAY_PROXY_HEADER;
		String systemPropertyProxyHeaderBase64 = GOVWAY_PROXY_HEADER_BASE64;
		String systemPropertyProxyQuery = GOVWAY_PROXY_QUERY;
		String systemPropertyProxyQueryBase64 = GOVWAY_PROXY_QUERY_BASE64;
		
		if(fruizione!=null) {
			systemPropertyProxy = fruizione ? GOVWAY_FRUIZIONI_PROXY : GOVWAY_EROGAZIONI_PROXY;
			systemPropertyProxyHeader = fruizione ? GOVWAY_FRUIZIONI_PROXY_HEADER : GOVWAY_EROGAZIONI_PROXY_HEADER;
			systemPropertyProxyHeaderBase64 = fruizione ? GOVWAY_FRUIZIONI_PROXY_HEADER_BASE64 : GOVWAY_EROGAZIONI_PROXY_HEADER_BASE64;
			systemPropertyProxyQuery = fruizione ? GOVWAY_FRUIZIONI_PROXY_QUERY : GOVWAY_EROGAZIONI_PROXY_QUERY;
			systemPropertyProxyQueryBase64 = fruizione ? GOVWAY_FRUIZIONI_PROXY_QUERY_BASE64 : GOVWAY_EROGAZIONI_PROXY_QUERY_BASE64;
		}
		
		if(tag!=null) {
			systemPropertyProxy= GOVWAY_PREFISSO_TAG_PROXY+ tag +GOVWAY_SEPARATORE+ systemPropertyProxy;
			systemPropertyProxyHeader= GOVWAY_PREFISSO_TAG_PROXY+ tag +GOVWAY_SEPARATORE+ systemPropertyProxyHeader;
			systemPropertyProxyHeaderBase64= GOVWAY_PREFISSO_TAG_PROXY+ tag +GOVWAY_SEPARATORE+ systemPropertyProxyHeaderBase64;
			systemPropertyProxyQuery= GOVWAY_PREFISSO_TAG_PROXY+ tag +GOVWAY_SEPARATORE+ systemPropertyProxyQuery;
			systemPropertyProxyQueryBase64= GOVWAY_PREFISSO_TAG_PROXY+ tag +GOVWAY_SEPARATORE+ systemPropertyProxyQueryBase64;
		}
		else if(nomeSoggetto!=null) {
			if(tipoSoggetto==null) {
				systemPropertyProxy= GOVWAY_PREFISSO_DOMINIO_PROXY + nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxy;
				systemPropertyProxyHeader= GOVWAY_PREFISSO_DOMINIO_PROXY + nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyHeader;
				systemPropertyProxyHeaderBase64= GOVWAY_PREFISSO_DOMINIO_PROXY + nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyHeaderBase64;
				systemPropertyProxyQuery= GOVWAY_PREFISSO_DOMINIO_PROXY + nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyQuery;
				systemPropertyProxyQueryBase64= GOVWAY_PREFISSO_DOMINIO_PROXY + nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyQueryBase64;
			}
			else {
				if(byProtocollo) {
					String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(tipoSoggetto);
					systemPropertyProxy= GOVWAY_PREFISSO_DOMINIO_PROXY + protocollo+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxy;
					systemPropertyProxyHeader= GOVWAY_PREFISSO_DOMINIO_PROXY + protocollo+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyHeader;
					systemPropertyProxyHeaderBase64= GOVWAY_PREFISSO_DOMINIO_PROXY + protocollo+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyHeaderBase64;
					systemPropertyProxyQuery= GOVWAY_PREFISSO_DOMINIO_PROXY + protocollo+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyQuery;
					systemPropertyProxyQueryBase64= GOVWAY_PREFISSO_DOMINIO_PROXY + protocollo+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyQueryBase64;
				}
				else {
					systemPropertyProxy= GOVWAY_PREFISSO_DOMINIO_PROXY + tipoSoggetto+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxy;
					systemPropertyProxyHeader= GOVWAY_PREFISSO_DOMINIO_PROXY + tipoSoggetto+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyHeader;
					systemPropertyProxyHeaderBase64= GOVWAY_PREFISSO_DOMINIO_PROXY + tipoSoggetto+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyHeaderBase64;
					systemPropertyProxyQuery= GOVWAY_PREFISSO_DOMINIO_PROXY + tipoSoggetto+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyQuery;
					systemPropertyProxyQueryBase64= GOVWAY_PREFISSO_DOMINIO_PROXY + tipoSoggetto+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyQueryBase64;
				}
			}
		}
		else if(byProtocollo) {
			String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(tipoSoggetto);
			systemPropertyProxy= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxy;
			systemPropertyProxyHeader= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyHeader;
			systemPropertyProxyHeaderBase64= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyHeaderBase64;
			systemPropertyProxyQuery= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyQuery;
			systemPropertyProxyQueryBase64= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyQueryBase64;
		}
		
		
		
		String systemPropertyProxyTokenDynamicDiscovery = GOVWAY_PROXY_TOKEN_DYNAMIC_DISCOVERY;
		String systemPropertyProxyTokenValidazioneJwt = GOVWAY_PROXY_TOKEN_VALIDAZIONE_JWT;
		String systemPropertyProxyTokenIntrospection = GOVWAY_PROXY_TOKEN_INTROSPECTION;
		String systemPropertyProxyTokenUserInfo = GOVWAY_PROXY_TOKEN_USERINFO;
		String systemPropertyProxyTokenRetrieve = GOVWAY_PROXY_TOKEN_RETRIEVE;
		String systemPropertyProxyAttributeAuthority = GOVWAY_PROXY_ATTRIBUTE_AUTHORITY;
		String systemPropertyProxyAttributeAuthorityResponseJwtValidation = GOVWAY_PROXY_ATTRIBUTE_AUTHORITY_RESPONSE_JWT_VALIDATION;
		
		if(fruizione!=null) {
			systemPropertyProxyTokenDynamicDiscovery = fruizione ? GOVWAY_FRUIZIONI_PROXY_TOKEN_DYNAMIC_DISCOVERY : GOVWAY_EROGAZIONI_PROXY_TOKEN_DYNAMIC_DISCOVERY;
			systemPropertyProxyTokenValidazioneJwt = fruizione ? GOVWAY_FRUIZIONI_PROXY_TOKEN_VALIDAZIONE_JWT : GOVWAY_EROGAZIONI_PROXY_TOKEN_VALIDAZIONE_JWT;
			systemPropertyProxyTokenIntrospection = fruizione ? GOVWAY_FRUIZIONI_PROXY_TOKEN_INTROSPECTION : GOVWAY_EROGAZIONI_PROXY_TOKEN_INTROSPECTION;
			systemPropertyProxyTokenUserInfo = fruizione ? GOVWAY_FRUIZIONI_PROXY_TOKEN_USERINFO : GOVWAY_EROGAZIONI_PROXY_TOKEN_USERINFO;
			systemPropertyProxyTokenRetrieve = fruizione ? GOVWAY_FRUIZIONI_PROXY_TOKEN_RETRIEVE : GOVWAY_EROGAZIONI_PROXY_TOKEN_RETRIEVE;
			systemPropertyProxyAttributeAuthority = fruizione ? GOVWAY_FRUIZIONI_PROXY_ATTRIBUTE_AUTHORITY : GOVWAY_EROGAZIONI_PROXY_ATTRIBUTE_AUTHORITY;
			systemPropertyProxyAttributeAuthorityResponseJwtValidation = fruizione ? GOVWAY_FRUIZIONI_PROXY_ATTRIBUTE_AUTHORITY_RESPONSE_JWT_VALIDATION : GOVWAY_EROGAZIONI_PROXY_ATTRIBUTE_AUTHORITY_RESPONSE_JWT_VALIDATION;
		}
		
		if(tag!=null) {
			systemPropertyProxyTokenDynamicDiscovery= GOVWAY_PREFISSO_TAG_PROXY+ tag +GOVWAY_SEPARATORE+ systemPropertyProxyTokenDynamicDiscovery;
			systemPropertyProxyTokenValidazioneJwt= GOVWAY_PREFISSO_TAG_PROXY+ tag +GOVWAY_SEPARATORE+ systemPropertyProxyTokenValidazioneJwt;
			systemPropertyProxyTokenIntrospection= GOVWAY_PREFISSO_TAG_PROXY+ tag +GOVWAY_SEPARATORE+ systemPropertyProxyTokenIntrospection;
			systemPropertyProxyTokenUserInfo= GOVWAY_PREFISSO_TAG_PROXY+ tag +GOVWAY_SEPARATORE+ systemPropertyProxyTokenUserInfo;
			systemPropertyProxyTokenRetrieve= GOVWAY_PREFISSO_TAG_PROXY+ tag +GOVWAY_SEPARATORE+ systemPropertyProxyTokenRetrieve;
			systemPropertyProxyAttributeAuthority= GOVWAY_PREFISSO_TAG_PROXY+ tag +GOVWAY_SEPARATORE+ systemPropertyProxyAttributeAuthority;
			systemPropertyProxyAttributeAuthorityResponseJwtValidation= GOVWAY_PREFISSO_TAG_PROXY+ tag +GOVWAY_SEPARATORE+ systemPropertyProxyAttributeAuthorityResponseJwtValidation;
		}
		else if(nomeSoggetto!=null) {
			if(tipoSoggetto==null) {
				systemPropertyProxyTokenDynamicDiscovery= GOVWAY_PREFISSO_DOMINIO_PROXY + nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyTokenDynamicDiscovery;
				systemPropertyProxyTokenValidazioneJwt= GOVWAY_PREFISSO_DOMINIO_PROXY + nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyTokenValidazioneJwt;
				systemPropertyProxyTokenIntrospection= GOVWAY_PREFISSO_DOMINIO_PROXY + nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyTokenIntrospection;
				systemPropertyProxyTokenUserInfo= GOVWAY_PREFISSO_DOMINIO_PROXY + nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyTokenUserInfo;
				systemPropertyProxyTokenRetrieve= GOVWAY_PREFISSO_DOMINIO_PROXY + nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyTokenRetrieve;
				systemPropertyProxyAttributeAuthority= GOVWAY_PREFISSO_DOMINIO_PROXY + nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyAttributeAuthority;
				systemPropertyProxyAttributeAuthorityResponseJwtValidation= GOVWAY_PREFISSO_DOMINIO_PROXY + nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyAttributeAuthorityResponseJwtValidation;
			}
			else {
				if(byProtocollo) {
					String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(tipoSoggetto);
					systemPropertyProxyTokenDynamicDiscovery= GOVWAY_PREFISSO_DOMINIO_PROXY + protocollo+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyTokenDynamicDiscovery;
					systemPropertyProxyTokenValidazioneJwt= GOVWAY_PREFISSO_DOMINIO_PROXY + protocollo+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyTokenValidazioneJwt;
					systemPropertyProxyTokenIntrospection= GOVWAY_PREFISSO_DOMINIO_PROXY + protocollo+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyTokenIntrospection;
					systemPropertyProxyTokenUserInfo= GOVWAY_PREFISSO_DOMINIO_PROXY + protocollo+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyTokenUserInfo;
					systemPropertyProxyTokenRetrieve= GOVWAY_PREFISSO_DOMINIO_PROXY + protocollo+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyTokenRetrieve;
					systemPropertyProxyAttributeAuthority= GOVWAY_PREFISSO_DOMINIO_PROXY + protocollo+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyAttributeAuthority;
					systemPropertyProxyAttributeAuthorityResponseJwtValidation= GOVWAY_PREFISSO_DOMINIO_PROXY + protocollo+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyAttributeAuthorityResponseJwtValidation;
				}
				else {
					systemPropertyProxyTokenDynamicDiscovery= GOVWAY_PREFISSO_DOMINIO_PROXY + tipoSoggetto+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyTokenDynamicDiscovery;
					systemPropertyProxyTokenValidazioneJwt= GOVWAY_PREFISSO_DOMINIO_PROXY + tipoSoggetto+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyTokenValidazioneJwt;
					systemPropertyProxyTokenIntrospection= GOVWAY_PREFISSO_DOMINIO_PROXY + tipoSoggetto+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyTokenIntrospection;
					systemPropertyProxyTokenUserInfo= GOVWAY_PREFISSO_DOMINIO_PROXY + tipoSoggetto+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyTokenUserInfo;
					systemPropertyProxyTokenRetrieve= GOVWAY_PREFISSO_DOMINIO_PROXY + tipoSoggetto+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyTokenRetrieve;
					systemPropertyProxyAttributeAuthority= GOVWAY_PREFISSO_DOMINIO_PROXY + tipoSoggetto+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyAttributeAuthority;
					systemPropertyProxyAttributeAuthorityResponseJwtValidation= GOVWAY_PREFISSO_DOMINIO_PROXY + tipoSoggetto+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyAttributeAuthorityResponseJwtValidation;
				}
			}
		}
		else if(byProtocollo) {
			String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(tipoSoggetto);
			systemPropertyProxyTokenDynamicDiscovery= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyTokenDynamicDiscovery;
			systemPropertyProxyTokenValidazioneJwt= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyTokenValidazioneJwt;
			systemPropertyProxyTokenIntrospection= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyTokenIntrospection;
			systemPropertyProxyTokenUserInfo= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyTokenUserInfo;
			systemPropertyProxyTokenRetrieve= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyTokenRetrieve;
			systemPropertyProxyAttributeAuthority= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyAttributeAuthority;
			systemPropertyProxyAttributeAuthorityResponseJwtValidation= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyAttributeAuthorityResponseJwtValidation;
		}
		
		
		String systemPropertyProxyTokenDynamicDiscoveryNomePolicy = null;
		String systemPropertyProxyTokenValidazioneJwtNomePolicy = null;
		String systemPropertyProxyTokenIntrospectionNomePolicy = null;
		String systemPropertyProxyTokenUserInfoNomePolicy = null;
		String systemPropertyProxyTokenRetrieveNomePolicy = null;
		String systemPropertyProxyAttributeAuthorityNomePolicy = null;
		String systemPropertyProxyAttributeAuthorityResponseJwtValidationNomePolicy = null;
		
		if(policy!=null && policy.getNome()!=null) {
			systemPropertyProxyTokenDynamicDiscoveryNomePolicy = systemPropertyProxyTokenDynamicDiscovery+GOVWAY_SEPARATORE+policy.getNome();
			systemPropertyProxyTokenValidazioneJwtNomePolicy = systemPropertyProxyTokenValidazioneJwt+GOVWAY_SEPARATORE+policy.getNome();
			systemPropertyProxyTokenIntrospectionNomePolicy = systemPropertyProxyTokenIntrospection+GOVWAY_SEPARATORE+policy.getNome();
			systemPropertyProxyTokenUserInfoNomePolicy = systemPropertyProxyTokenUserInfo+GOVWAY_SEPARATORE+policy.getNome();
			systemPropertyProxyTokenRetrieveNomePolicy = systemPropertyProxyTokenRetrieve+GOVWAY_SEPARATORE+policy.getNome();
			systemPropertyProxyAttributeAuthorityNomePolicy = systemPropertyProxyAttributeAuthority+GOVWAY_SEPARATORE+policy.getNome();
			systemPropertyProxyAttributeAuthorityResponseJwtValidationNomePolicy = systemPropertyProxyAttributeAuthorityResponseJwtValidation+GOVWAY_SEPARATORE+policy.getNome();
		}
		
		
		
		
		ForwardProxy sConfig = new ForwardProxy();
		
		String url = System.getProperty(systemPropertyProxy);
		if(url!=null && !"".equals(url)) {
			sConfig.enabled=true;
			sConfig.url = url;
			
			
			// gestione header/query
			
			String header = System.getProperty(systemPropertyProxyHeader);
			String query = System.getProperty(systemPropertyProxyQuery);
			
			if( 
				(header==null || "".equals(header)) &&
				(query==null || "".equals(query)) 
			) {
				// verrà usata quella di default
				sConfig.config = defaultConfig;
			}
			else {
				
				ForwardProxyConfigurazione config = new ForwardProxyConfigurazione();
				sConfig.config = config;
				
				if(header!=null && !"".equals(header)) {
					config.setHeader(header);
					
					String base64 = System.getProperty(systemPropertyProxyHeaderBase64);
					if(base64!=null && !"".equals(base64)) {
						if(TRUE.equalsIgnoreCase(base64)) {
							config.setHeaderBase64(true);
						}
						else if(FALSE.equalsIgnoreCase(base64)) {
							config.setHeaderBase64(false);
						}
						else {
							config.setHeaderBase64(DEFAULT_GOVWAY_PROXY_HEADER_BASE64);
						}
					}
					else {
						config.setHeaderBase64(DEFAULT_GOVWAY_PROXY_HEADER_BASE64);
					}
				}
				
				if(query!=null && !"".equals(query)) {
					config.setQuery(query);
					
					String base64 = System.getProperty(systemPropertyProxyQueryBase64);
					if(base64!=null && !"".equals(base64)) {
						if(TRUE.equalsIgnoreCase(base64)) {
							config.setQueryBase64(true);
						}
						else if(FALSE.equalsIgnoreCase(base64)) {
							config.setQueryBase64(false);
						}
						else {
							config.setQueryBase64(DEFAULT_GOVWAY_PROXY_QUERY_BASE64);
						}
					}
					else {
						config.setQueryBase64(DEFAULT_GOVWAY_PROXY_QUERY_BASE64);
					}
				}
				
			}
			
			
			// gestione token
			
			String dynamicDiscovery = null;
			if(systemPropertyProxyTokenDynamicDiscoveryNomePolicy!=null) {
				dynamicDiscovery = System.getProperty(systemPropertyProxyTokenDynamicDiscoveryNomePolicy);
			}
			if((dynamicDiscovery==null || "".equals(dynamicDiscovery))) {
				dynamicDiscovery = System.getProperty(systemPropertyProxyTokenDynamicDiscovery);
			}
			
			String validazioneJwt = null;
			if(systemPropertyProxyTokenValidazioneJwtNomePolicy!=null) {
				validazioneJwt = System.getProperty(systemPropertyProxyTokenValidazioneJwtNomePolicy);
			}
			if((validazioneJwt==null || "".equals(validazioneJwt))) {
				validazioneJwt = System.getProperty(systemPropertyProxyTokenValidazioneJwt);
			}
			
			String introspection = null;
			if(systemPropertyProxyTokenIntrospectionNomePolicy!=null) {
				introspection = System.getProperty(systemPropertyProxyTokenIntrospectionNomePolicy);
			}
			if((introspection==null || "".equals(introspection))) {
				introspection = System.getProperty(systemPropertyProxyTokenIntrospection);
			}
			
			String userInfo = null;
			if(systemPropertyProxyTokenUserInfoNomePolicy!=null) {
				userInfo = System.getProperty(systemPropertyProxyTokenUserInfoNomePolicy);
			}
			if((userInfo==null || "".equals(userInfo))) {
				userInfo = System.getProperty(systemPropertyProxyTokenUserInfo);
			}
			
			String retrieve = null;
			if(systemPropertyProxyTokenRetrieveNomePolicy!=null) {
				retrieve = System.getProperty(systemPropertyProxyTokenRetrieveNomePolicy);
			}
			if((retrieve==null || "".equals(retrieve))) {
				retrieve = System.getProperty(systemPropertyProxyTokenRetrieve);
			}
			
			String attributeAuthority = null;
			if(systemPropertyProxyAttributeAuthorityNomePolicy!=null) {
				attributeAuthority = System.getProperty(systemPropertyProxyAttributeAuthorityNomePolicy);
			}
			if((attributeAuthority==null || "".equals(attributeAuthority))) {
				attributeAuthority = System.getProperty(systemPropertyProxyAttributeAuthority);
			}
			
			String attributeAuthorityResponseJwtValidation = null;
			if(systemPropertyProxyAttributeAuthorityResponseJwtValidationNomePolicy!=null) {
				attributeAuthorityResponseJwtValidation = System.getProperty(systemPropertyProxyAttributeAuthorityResponseJwtValidationNomePolicy);
			}
			if((attributeAuthorityResponseJwtValidation==null || "".equals(attributeAuthorityResponseJwtValidation))) {
				attributeAuthorityResponseJwtValidation = System.getProperty(systemPropertyProxyAttributeAuthorityResponseJwtValidation);
			}
						
			if( 
					(dynamicDiscovery==null || "".equals(dynamicDiscovery)) &&
					(validazioneJwt==null || "".equals(validazioneJwt)) &&
					(introspection==null || "".equals(introspection)) &&
					(userInfo==null || "".equals(userInfo)) &&
					(retrieve==null || "".equals(retrieve)) &&
					(attributeAuthority==null || "".equals(attributeAuthority))  &&
					(attributeAuthorityResponseJwtValidation==null || "".equals(attributeAuthorityResponseJwtValidation)) 
				) {
				// verrà usata quella di default
				sConfig.configToken = defaultConfigToken;
			}
			else{
			
				ForwardProxyConfigurazioneToken config = new ForwardProxyConfigurazioneToken();
				sConfig.configToken = config;
						
				if(dynamicDiscovery!=null && !"".equals(dynamicDiscovery)) {
					if(TRUE.equalsIgnoreCase(dynamicDiscovery)) {
						config.tokenDynamicDiscoveryEnabled = true;
					}
					else if(FALSE.equalsIgnoreCase(dynamicDiscovery)) {
						config.tokenDynamicDiscoveryEnabled = false;
					}
					else {
						config.tokenDynamicDiscoveryEnabled = DEFAULT_GOVWAY_PROXY_TOKEN_DYNAMIC_DISCOVERY;
					}
				}
				else {
					config.tokenDynamicDiscoveryEnabled = DEFAULT_GOVWAY_PROXY_TOKEN_DYNAMIC_DISCOVERY;
				}
				
				if(validazioneJwt!=null && !"".equals(validazioneJwt)) {
					if(TRUE.equalsIgnoreCase(validazioneJwt)) {
						config.tokenJwtValidationEnabled = true;
					}
					else if(FALSE.equalsIgnoreCase(validazioneJwt)) {
						config.tokenJwtValidationEnabled = false;
					}
					else {
						config.tokenJwtValidationEnabled = DEFAULT_GOVWAY_PROXY_TOKEN_VALIDAZIONE_JWT;
					}
				}
				else {
					config.tokenJwtValidationEnabled = DEFAULT_GOVWAY_PROXY_TOKEN_VALIDAZIONE_JWT;
				}
				
				if(introspection!=null && !"".equals(introspection)) {
					if(TRUE.equalsIgnoreCase(introspection)) {
						config.tokenIntrospectionEnabled = true;
					}
					else if(FALSE.equalsIgnoreCase(introspection)) {
						config.tokenIntrospectionEnabled = false;
					}
					else {
						config.tokenIntrospectionEnabled = DEFAULT_GOVWAY_PROXY_TOKEN_INTROSPECTION;
					}
				}
				else {
					config.tokenIntrospectionEnabled = DEFAULT_GOVWAY_PROXY_TOKEN_INTROSPECTION;
				}
						
				if(userInfo!=null && !"".equals(userInfo)) {
					if(TRUE.equalsIgnoreCase(userInfo)) {
						config.tokenUserInfoEnabled = true;
					}
					else if(FALSE.equalsIgnoreCase(userInfo)) {
						config.tokenUserInfoEnabled = false;
					}
					else {
						config.tokenUserInfoEnabled = DEFAULT_GOVWAY_PROXY_TOKEN_USERINFO;
					}
				}
				else {
					config.tokenUserInfoEnabled = DEFAULT_GOVWAY_PROXY_TOKEN_USERINFO;
				}
				
				if(retrieve!=null && !"".equals(retrieve)) {
					if(TRUE.equalsIgnoreCase(retrieve)) {
						config.tokenRetrieveEnabled = true;
					}
					else if(FALSE.equalsIgnoreCase(retrieve)) {
						config.tokenRetrieveEnabled = false;
					}
					else {
						config.tokenRetrieveEnabled = DEFAULT_GOVWAY_PROXY_TOKEN_RETRIEVE;
					}
				}
				else {
					config.tokenRetrieveEnabled = DEFAULT_GOVWAY_PROXY_TOKEN_RETRIEVE;
				}
				
				if(attributeAuthority!=null && !"".equals(attributeAuthority)) {
					if(TRUE.equalsIgnoreCase(attributeAuthority)) {
						config.attributeAuthorityEnabled = true;
					}
					else if(FALSE.equalsIgnoreCase(attributeAuthority)) {
						config.attributeAuthorityEnabled = false;
					}
					else {
						config.attributeAuthorityEnabled = DEFAULT_GOVWAY_PROXY_ATTRIBUTE_AUTHORITY;
					}
				}
				else {
					config.attributeAuthorityEnabled = DEFAULT_GOVWAY_PROXY_ATTRIBUTE_AUTHORITY;
				}
				
				if(attributeAuthorityResponseJwtValidation!=null && !"".equals(attributeAuthorityResponseJwtValidation)) {
					if(TRUE.equalsIgnoreCase(attributeAuthorityResponseJwtValidation)) {
						config.attributeAuthorityResponseJwtValidationEnabled = true;
					}
					else if(FALSE.equalsIgnoreCase(attributeAuthorityResponseJwtValidation)) {
						config.attributeAuthorityResponseJwtValidationEnabled = false;
					}
					else {
						config.attributeAuthorityResponseJwtValidationEnabled = DEFAULT_GOVWAY_PROXY_ATTRIBUTE_AUTHORITY_RESPONSE_JWT_VALIDATION;
					}
				}
				else {
					config.attributeAuthorityResponseJwtValidationEnabled = DEFAULT_GOVWAY_PROXY_ATTRIBUTE_AUTHORITY_RESPONSE_JWT_VALIDATION;
				}
			}
		}
		
		return sConfig;
		
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("enabled: ").append(this.enabled);
		if(this.enabled) {
			sb.append("\n").append("url: ").append(this.url);
		}
		if(this.config!=null) {
			sb.append("\n").append(this.config.toString());
		}
		if(this.configToken!=null) {
			sb.append("\n").append(this.configToken.toString());
		}
		return sb.toString();
	}
		
	
	private boolean enabled = false;
	private String url;
	private ForwardProxyConfigurazione config;
	private ForwardProxyConfigurazioneToken configToken;
	
	public ForwardProxy() {
		// nop
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public ForwardProxyConfigurazione getConfig() {
		return this.config;
	}

	public void setConfig(ForwardProxyConfigurazione config) {
		this.config = config;
	}
	
	public ForwardProxyConfigurazioneToken getConfigToken() {
		return this.configToken;
	}

	public void setConfigToken(ForwardProxyConfigurazioneToken configToken) {
		this.configToken = configToken;
	}

	
}
