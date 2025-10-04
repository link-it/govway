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

package org.openspcoop2.pdd.core.connettori;

import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.GestioneToken;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneGet;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.ConfigurazionePdDReader;
import org.openspcoop2.pdd.core.byok.BYOKUnwrapPolicyUtilities;
import org.openspcoop2.pdd.core.dynamic.DynamicMapBuilderUtils;
import org.openspcoop2.pdd.core.token.Costanti;
import org.openspcoop2.pdd.core.token.PolicyGestioneToken;
import org.openspcoop2.pdd.core.token.PolicyNegoziazioneToken;
import org.openspcoop2.pdd.core.token.TokenUtilities;
import org.openspcoop2.pdd.core.token.attribute_authority.AttributeAuthorityUtilities;
import org.openspcoop2.pdd.core.token.attribute_authority.PolicyAttributeAuthority;
import org.openspcoop2.protocol.registry.CertificateUtils;
import org.openspcoop2.protocol.registry.RegistroServiziReader;
import org.openspcoop2.security.keystore.cache.GestoreOCSPResource;
import org.openspcoop2.utils.LoggerBuffer;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeystoreParams;
import org.openspcoop2.utils.certificate.ocsp.OCSPValidatorImpl;
import org.openspcoop2.utils.regexp.RegExpUtilities;
import org.openspcoop2.utils.transport.http.HttpLibrary;
import org.openspcoop2.utils.transport.http.HttpLibraryConnection;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.transport.http.IBYOKUnwrapManager;
import org.openspcoop2.utils.transport.http.SSLConfig;
import org.openspcoop2.utils.transport.http.SSLUtilities;
import org.slf4j.Logger;

/**
 * ConnettoreCheck
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreCheck {
	
	private ConnettoreCheck() {}

	public static boolean checkSupported(org.openspcoop2.core.registry.Connettore connettore) {
		return checkSupported(connettore.mappingIntoConnettoreConfigurazione());
	}
	public static boolean checkSupported(Connettore connettore) {
		
		TipiConnettore tipo = null;
		try{
			tipo = TipiConnettore.valueOf(connettore.getTipo().toUpperCase());
			if(tipo!=null) {
				switch (tipo) {
				case HTTP, HTTPS, STATUS:
					return true;

				default:
					return false;
				}
			}
		}catch(Exception e) {
			// ignore
		}
		return false;
	}
	
	public static void check(long idConnettore, boolean registro, Logger log) throws ConnettoreException{
		if(registro) {
			for (IDriverRegistroServiziGet iDriverRegistroServiziGet : RegistroServiziReader.getDriverRegistroServizi().values()) {
				if(iDriverRegistroServiziGet instanceof DriverRegistroServiziDB driverRegistryDB) {
					try {
						org.openspcoop2.core.registry.Connettore connettore = driverRegistryDB.getConnettore(idConnettore);
						check(connettore, log);
						return;
					}catch(Throwable e) {
						throw new ConnettoreException(e.getMessage(),e);
					}
				}
			}
		}
		else {
			IDriverConfigurazioneGet iDriverConfigurazioneGet = ConfigurazionePdDReader.getDriverConfigurazionePdD();
			if(iDriverConfigurazioneGet instanceof DriverConfigurazioneDB driverConfigDB) {
				try {
					Connettore connettore = driverConfigDB.getConnettore(idConnettore);
					check(connettore, log);
					return;
				}catch(Throwable e) {
					throw new ConnettoreException(e.getMessage(),e);
				}
			}
		}
		throw new ConnettoreException("Connettore con id '"+idConnettore+"' non trovato");
	}
	public static void check(String nomeConnettore, boolean registro, Logger log) throws ConnettoreException{
		if(registro) {
			for (IDriverRegistroServiziGet iDriverRegistroServiziGet : RegistroServiziReader.getDriverRegistroServizi().values()) {
				if(iDriverRegistroServiziGet instanceof DriverRegistroServiziDB driverRegistryDB) {
					try {
						org.openspcoop2.core.registry.Connettore connettore = driverRegistryDB.getConnettore(nomeConnettore);
						check(connettore, log);
						return;
					}catch(Throwable e) {
						throw new ConnettoreException(e.getMessage(),e);
					}
				}
			}
		}
		else {
			IDriverConfigurazioneGet iDriverConfigurazioneGet = ConfigurazionePdDReader.getDriverConfigurazionePdD();
			if(iDriverConfigurazioneGet instanceof DriverConfigurazioneDB driverConfigDB) {
				try {
					Connettore connettore = driverConfigDB.getConnettore(nomeConnettore);
					check(connettore, log);
					return;
				}catch(Throwable e) {
					throw new ConnettoreException(e.getMessage(),e);
				}
			}
		}
		
		throw new ConnettoreException("Connettore con nome '"+nomeConnettore+"' non trovato");
	}

	public static final String POLICY_TIPO_ENDPOINT = "__POLICY_TIPO_ENDPOINT";
	public static List<Connettore> convertPolicyToConnettore(GenericProperties gp, Logger log) throws ConnettoreException{
		
		if(org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA.equals(gp.getTipologia())) {
			// PolicyValidazione
			return convertTokenPolicyValidazioneToConnettore(gp, log);
		}
		else if(org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA_RETRIEVE.equals(gp.getTipologia())) {
			// PolicyNegoziazione
			return convertTokenPolicyNegoziazioneToConnettore(gp, log);
		}
		else if(org.openspcoop2.pdd.core.token.Costanti.ATTRIBUTE_AUTHORITY.equals(gp.getTipologia())) {
			// ATTRIBUTE_AUTHORITY
			return convertAttributeAuthorityToConnettore(gp, log);
		}
		else {
			throw new ConnettoreException("Tipologia '"+gp.getTipologia()+"' non gestita");
		}
		
	}
	
	public static final String POLICY_TIPO_ENDPOINT_DYNAMIC_DISCOVERY = "DynamicDiscovery";
	public static final String POLICY_TIPO_ENDPOINT_VALIDAZIONE_JWT = "ValidazioneJWT";
	public static final String POLICY_TIPO_ENDPOINT_INTROSPECTION = "Introspection";
	public static final String POLICY_TIPO_ENDPOINT_USERINFO = "UserInfo";
	public static List<Connettore> convertTokenPolicyValidazioneToConnettore(GenericProperties gp, Logger log) throws ConnettoreException{
		try {
			if(log!=null) {
				// nop
			}
			
			List<Connettore> lNull = null;
			
			GestioneToken gestione = new GestioneToken();
			gestione.setValidazione(StatoFunzionalitaConWarning.ABILITATO);
			gestione.setIntrospection(StatoFunzionalitaConWarning.ABILITATO);
			gestione.setUserInfo(StatoFunzionalitaConWarning.ABILITATO);
			PolicyGestioneToken policy = TokenUtilities.convertTo(gp, gestione);
			
			Connettore connettoreDynamicDiscovery = null;
			Connettore connettoreValidazioneJWT = null;
			Connettore connettoreIntrospection = null;
			Connettore connettoreUserInfo = null;
			
			String dynamicDiscoveryEndpoint = policy.getDynamicDiscoveryEndpoint();
			if(StringUtils.isNotEmpty(dynamicDiscoveryEndpoint)) {
				connettoreDynamicDiscovery = new Connettore();
				addProperty(connettoreDynamicDiscovery, POLICY_TIPO_ENDPOINT, POLICY_TIPO_ENDPOINT_DYNAMIC_DISCOVERY);
				addProperty(connettoreDynamicDiscovery, CostantiConnettori.CONNETTORE_LOCATION, dynamicDiscoveryEndpoint);
			}
			
			String validazioneJWTEndpoint = policy.isValidazioneJWTLocationHttp() ? policy.getValidazioneJWTLocation() : null;
			if(StringUtils.isNotEmpty(validazioneJWTEndpoint)) {
				connettoreValidazioneJWT = new Connettore();
				addProperty(connettoreValidazioneJWT, POLICY_TIPO_ENDPOINT, POLICY_TIPO_ENDPOINT_VALIDAZIONE_JWT);
				addProperty(connettoreValidazioneJWT, CostantiConnettori.CONNETTORE_LOCATION, validazioneJWTEndpoint);
			}
			
			String introspectionEndpoint = policy.getIntrospectionEndpoint();
			if(StringUtils.isNotEmpty(introspectionEndpoint)) {
				connettoreIntrospection = new Connettore();
				addProperty(connettoreIntrospection, POLICY_TIPO_ENDPOINT, POLICY_TIPO_ENDPOINT_INTROSPECTION);
				addProperty(connettoreIntrospection, CostantiConnettori.CONNETTORE_LOCATION, introspectionEndpoint);
				
				if(policy.isIntrospectionBasicAuthentication() && StringUtils.isNotEmpty(policy.getIntrospectionBasicAuthenticationUsername()) && policy.getIntrospectionBasicAuthenticationPassword()!=null) {
					addProperty(connettoreIntrospection, CostantiConnettori.CONNETTORE_USERNAME, policy.getIntrospectionBasicAuthenticationUsername());
					addProperty(connettoreIntrospection, CostantiConnettori.CONNETTORE_PASSWORD, policy.getIntrospectionBasicAuthenticationPassword());
				}
				if(policy.isIntrospectionBearerAuthentication() && StringUtils.isNotEmpty(policy.getIntrospectionBeareAuthenticationToken())) {
					addProperty(connettoreIntrospection, CostantiConnettori.CONNETTORE_BEARER_TOKEN, policy.getIntrospectionBeareAuthenticationToken());
				}
			}
			
			String userInfoEndpoint = policy.getUserInfoEndpoint();
			if(StringUtils.isNotEmpty(userInfoEndpoint)) {
				connettoreUserInfo = new Connettore();
				addProperty(connettoreUserInfo, POLICY_TIPO_ENDPOINT, POLICY_TIPO_ENDPOINT_USERINFO);
				addProperty(connettoreUserInfo, CostantiConnettori.CONNETTORE_LOCATION, userInfoEndpoint);
				
				if(policy.isUserInfoBasicAuthentication() && StringUtils.isNotEmpty(policy.getUserInfoBasicAuthenticationUsername()) && policy.getUserInfoBasicAuthenticationPassword()!=null) {
					addProperty(connettoreUserInfo, CostantiConnettori.CONNETTORE_USERNAME, policy.getUserInfoBasicAuthenticationUsername());
					addProperty(connettoreUserInfo, CostantiConnettori.CONNETTORE_PASSWORD, policy.getUserInfoBasicAuthenticationPassword());
				}
				if(policy.isUserInfoBearerAuthentication() && StringUtils.isNotEmpty(policy.getUserInfoBeareAuthenticationToken())) {
					addProperty(connettoreUserInfo, CostantiConnettori.CONNETTORE_BEARER_TOKEN, policy.getUserInfoBeareAuthenticationToken());
				}
			}
			
			if(connettoreDynamicDiscovery!=null || connettoreValidazioneJWT!=null || connettoreIntrospection!=null || connettoreUserInfo!=null) {
				
				if(connettoreDynamicDiscovery!=null) {
					Map<String,String> mapProperties = connettoreDynamicDiscovery.getProperties();
									
					Properties endpointConfig = policy.getProperties().get(Costanti.POLICY_ENDPOINT_CONFIG);
					if(endpointConfig!=null && !endpointConfig.isEmpty()) {
						putAll(endpointConfig, mapProperties);
					}
					
					connettoreDynamicDiscovery.setProperties(mapProperties);
				}
				if(connettoreValidazioneJWT!=null) {
					Map<String,String> mapProperties = connettoreValidazioneJWT.getProperties();
									
					Properties endpointConfig = policy.getProperties().get(Costanti.POLICY_ENDPOINT_CONFIG);
					if(endpointConfig!=null && !endpointConfig.isEmpty()) {
						putAll(endpointConfig, mapProperties);
					}
					
					connettoreValidazioneJWT.setProperties(mapProperties);
				}
				if(connettoreIntrospection!=null) {
					Map<String,String> mapProperties = connettoreIntrospection.getProperties();
									
					Properties endpointConfig = policy.getProperties().get(Costanti.POLICY_ENDPOINT_CONFIG);
					if(endpointConfig!=null && !endpointConfig.isEmpty()) {
						putAll(endpointConfig, mapProperties);
					}
					
					connettoreIntrospection.setProperties(mapProperties);
				}
				if(connettoreUserInfo!=null) {
					Map<String,String> mapProperties = connettoreUserInfo.getProperties();
									
					Properties endpointConfig = policy.getProperties().get(Costanti.POLICY_ENDPOINT_CONFIG);
					if(endpointConfig!=null && !endpointConfig.isEmpty()) {
						putAll(endpointConfig, mapProperties);
					}
					
					connettoreUserInfo.setProperties(mapProperties);
				}
				
				boolean https = policy.isEndpointHttps();
				if(https) {
					Properties sslConfig = policy.getProperties().get(Costanti.POLICY_ENDPOINT_SSL_CONFIG);
					
					if(connettoreDynamicDiscovery!=null) {
						Map<String,String> mapProperties = connettoreDynamicDiscovery.getProperties();
						putAll(sslConfig, mapProperties);
																		
						connettoreDynamicDiscovery.setProperties(mapProperties);
					}
					
					if(connettoreValidazioneJWT!=null) {
						Map<String,String> mapProperties = connettoreValidazioneJWT.getProperties();
						putAll(sslConfig, mapProperties);
																		
						connettoreValidazioneJWT.setProperties(mapProperties);
					}
					
					if(connettoreIntrospection!=null) {
						Map<String,String> mapProperties = connettoreIntrospection.getProperties();
						putAll(sslConfig, mapProperties);
												
						boolean introspectionHttpsClient = policy.isIntrospectionHttpsAuthentication();
						if(introspectionHttpsClient) {
							Properties introspectionSslClientConfig = policy.getProperties().get(Costanti.POLICY_ENDPOINT_SSL_CLIENT_CONFIG);
							putAll(introspectionSslClientConfig, mapProperties);
							injectSameKeystoreForHttpsClient(sslConfig, introspectionSslClientConfig, mapProperties);
						}
						
						connettoreIntrospection.setProperties(mapProperties);
					}
					
					if(connettoreUserInfo!=null) {
						Map<String,String> mapProperties = connettoreUserInfo.getProperties();
						putAll(sslConfig, mapProperties);
						
						boolean userInfoHttpsClient = policy.isUserInfoHttpsAuthentication();
						if(userInfoHttpsClient) {
							Properties userInfoSslClientConfig = policy.getProperties().get(Costanti.POLICY_ENDPOINT_SSL_CLIENT_CONFIG);
							putAll(userInfoSslClientConfig, mapProperties);
							injectSameKeystoreForHttpsClient(sslConfig, userInfoSslClientConfig, mapProperties);
						}
						
						connettoreUserInfo.setProperties(mapProperties);
					}
				}
				
				List<Connettore> l = new ArrayList<>();
				if(connettoreDynamicDiscovery!=null) {
					connettoreDynamicDiscovery.setTipo(https ? TipiConnettore.HTTPS.getNome() : TipiConnettore.HTTP.getNome());
					l.add(connettoreDynamicDiscovery);
				}
				if(connettoreValidazioneJWT!=null) {
					connettoreValidazioneJWT.setTipo(https ? TipiConnettore.HTTPS.getNome() : TipiConnettore.HTTP.getNome());
					l.add(connettoreValidazioneJWT);
				}
				if(connettoreIntrospection!=null) {
					connettoreIntrospection.setTipo(https ? TipiConnettore.HTTPS.getNome() : TipiConnettore.HTTP.getNome());
					l.add(connettoreIntrospection);
				}
				if(connettoreUserInfo!=null) {
					connettoreUserInfo.setTipo(https ? TipiConnettore.HTTPS.getNome() : TipiConnettore.HTTP.getNome());
					l.add(connettoreUserInfo);
				}
				return l;
			}
			
			return lNull;
			
		}catch(Exception t) {
			throw new ConnettoreException(t.getMessage(),t);
		}
	}
	public static List<Connettore> convertTokenPolicyNegoziazioneToConnettore(GenericProperties gp, Logger log) throws ConnettoreException{
		try {
			if(log!=null) {
				// nop
			}
			
			PolicyNegoziazioneToken policy = TokenUtilities.convertTo(gp);
			List<Connettore> lNull = null;
			
			Connettore connettore = null;
			
			String endpoint = policy.getEndpoint();
			if(StringUtils.isNotEmpty(endpoint)) {
				connettore = new Connettore();
				//addProperty(connettore, POLICY_TIPO_ENDPOINT, "Negoziazione"); // solo 1 tipo
				addProperty(connettore, CostantiConnettori.CONNETTORE_LOCATION, endpoint);
				
				if(policy.isBasicAuthentication() && StringUtils.isNotEmpty(policy.getBasicAuthenticationUsername()) && policy.getBasicAuthenticationPassword()!=null) {
					addProperty(connettore, CostantiConnettori.CONNETTORE_USERNAME, policy.getBasicAuthenticationUsername());
					addProperty(connettore, CostantiConnettori.CONNETTORE_PASSWORD, policy.getBasicAuthenticationPassword());
				}
				if(policy.isBearerAuthentication() && StringUtils.isNotEmpty(policy.getBeareAuthenticationToken())) {
					addProperty(connettore, CostantiConnettori.CONNETTORE_BEARER_TOKEN, policy.getBeareAuthenticationToken());
				}

				Map<String,String> mapProperties = connettore.getProperties();
								
				Properties endpointConfig = policy.getProperties().get(Costanti.POLICY_ENDPOINT_CONFIG);
				if(endpointConfig!=null && !endpointConfig.isEmpty()) {
					putAll(endpointConfig, mapProperties);
				}
				
				boolean https = policy.isEndpointHttps();
				if(https) {
					Properties sslConfig = policy.getProperties().get(Costanti.POLICY_ENDPOINT_SSL_CONFIG);
					putAll(sslConfig, mapProperties);
											
					boolean httpsClient = policy.isHttpsAuthentication();
					if(httpsClient) {
						Properties sslClientConfig = policy.getProperties().get(Costanti.POLICY_ENDPOINT_SSL_CLIENT_CONFIG);
						putAll(sslClientConfig, mapProperties);
						injectSameKeystoreForHttpsClient(sslConfig, sslClientConfig, mapProperties);
					}
					
					connettore.setProperties(mapProperties);
				}
				
				connettore.setTipo(https ? TipiConnettore.HTTPS.getNome() : TipiConnettore.HTTP.getNome());
				
				List<Connettore> l = new ArrayList<>();
				l.add(connettore);
				return l;
			}
			
			return lNull;
			
		}catch(Throwable t) {
			throw new ConnettoreException(t.getMessage(),t);
		}
	}
	public static List<Connettore> convertAttributeAuthorityToConnettore(GenericProperties gp, Logger log) throws ConnettoreException{
		try {
			if(log!=null) {
				// nop
			}
			PolicyAttributeAuthority policy = AttributeAuthorityUtilities.convertTo(gp);
			
			Connettore connettore = null;
			Connettore connettoreJwtResponse = null;
			
			String endpoint = policy.getEndpoint();
			if(StringUtils.isNotEmpty(endpoint)) {
				connettore = new Connettore();
				//addProperty(connettore, POLICY_TIPO_ENDPOINT, "Negoziazione"); // solo 1 tipo
				addProperty(connettore, CostantiConnettori.CONNETTORE_LOCATION, endpoint);
				
				if(policy.isBasicAuthentication() && StringUtils.isNotEmpty(policy.getBasicAuthenticationUsername()) && policy.getBasicAuthenticationPassword()!=null) {
					addProperty(connettore, CostantiConnettori.CONNETTORE_USERNAME, policy.getBasicAuthenticationUsername());
					addProperty(connettore, CostantiConnettori.CONNETTORE_PASSWORD, policy.getBasicAuthenticationPassword());
				}
				if(policy.isBearerAuthentication() && StringUtils.isNotEmpty(policy.getBeareAuthenticationToken())) {
					addProperty(connettore, CostantiConnettori.CONNETTORE_BEARER_TOKEN, policy.getBeareAuthenticationToken());
				}
				
				Map<String,String> mapProperties = connettore.getProperties();
								
				Properties endpointConfig = policy.getProperties().get(Costanti.POLICY_ENDPOINT_CONFIG);
				if(endpointConfig!=null && !endpointConfig.isEmpty()) {
					putAll(endpointConfig, mapProperties);
				}

				boolean https = policy.isEndpointHttps();
				if(https) {
					Properties sslConfig = policy.getProperties().get(Costanti.POLICY_ENDPOINT_SSL_CONFIG);
					putAll(sslConfig, mapProperties);
											
					boolean httpsClient = policy.isHttpsAuthentication();
					if(httpsClient) {
						Properties sslClientConfig = policy.getProperties().get(Costanti.POLICY_ENDPOINT_SSL_CLIENT_CONFIG);
						putAll(sslClientConfig, mapProperties);
						injectSameKeystoreForHttpsClient(sslConfig, sslClientConfig, mapProperties);
					}
					
					connettore.setProperties(mapProperties);
				}
				
				connettore.setTipo(https ? TipiConnettore.HTTPS.getNome() : TipiConnettore.HTTP.getNome());
			}
			
			String endpointJwtResponse = null;
			if(policy.isResponseJws() && policy.isResponseJwsLocationHttp()) {
				endpointJwtResponse = policy.getResponseJwsLocation();
			}
			if(StringUtils.isNotEmpty(endpointJwtResponse)) {
				connettoreJwtResponse = new Connettore();
				//addProperty(connettoreJwtResponse, POLICY_TIPO_ENDPOINT, "Negoziazione"); // solo 1 tipo
				addProperty(connettoreJwtResponse, CostantiConnettori.CONNETTORE_LOCATION, endpointJwtResponse);
								
				Map<String,String> mapProperties = connettoreJwtResponse.getProperties();
								
				Properties endpointConfig = policy.getProperties().get(Costanti.POLICY_ENDPOINT_CONFIG);
				if(endpointConfig!=null && !endpointConfig.isEmpty()) {
					putAll(endpointConfig, mapProperties);
				}

				boolean https = policy.isEndpointHttps();
				if(https) {
					Properties sslConfig = policy.getProperties().get(Costanti.POLICY_ENDPOINT_SSL_CONFIG);
					putAll(sslConfig, mapProperties);
																
					connettoreJwtResponse.setProperties(mapProperties);
				}
				
				connettoreJwtResponse.setTipo(https ? TipiConnettore.HTTPS.getNome() : TipiConnettore.HTTP.getNome());
			}
				
			if(connettore!=null && connettoreJwtResponse!=null) {
				addProperty(connettore, POLICY_TIPO_ENDPOINT, "Endpoint");
				addProperty(connettoreJwtResponse, POLICY_TIPO_ENDPOINT, "Risposta - TrustStore");
			}
			
			List<Connettore> l = null;
			if(connettore!=null || connettoreJwtResponse!=null) {
				l = new ArrayList<>();
				if(connettore!=null) {
					l.add(connettore);
				}
				if(connettoreJwtResponse!=null) {
					l.add(connettoreJwtResponse);
				}
			}
			return l;
			
		}catch(Throwable t) {
			throw new ConnettoreException(t.getMessage(),t);
		}
	}
	private static void injectSameKeystoreForHttpsClient(Properties sslConfig, Properties sslClientConfig, Map<String,String> mapProperties) {
		if(!sslClientConfig.containsKey(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_LOCATION) && !sslClientConfig.containsKey(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_PASSWORD)) {
			// modalita usa valori del truststore
			String trustStoreLocation = sslConfig.getProperty(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_LOCATION); 
			String trustStorePassword = sslConfig.getProperty(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			if(trustStoreLocation!=null) {
				mapProperties.put(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_LOCATION, trustStoreLocation);
				if(trustStorePassword!=null) {
					mapProperties.put(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD, trustStorePassword);
				}
			}
		}
	}
	public static void checkTokenPolicyValidazione(String nome, Logger log) throws ConnettoreException{
		checkPolicy(org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA, nome, log, null);
	}
	public static void checkTokenPolicyValidazione(String nome, String tipoConnettore, Logger log) throws ConnettoreException{
		checkPolicy(org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA, nome, log, tipoConnettore);
	}
	public static void checkTokenPolicyValidazioneDynamicDiscovery(String nome, Logger log) throws ConnettoreException{
		checkPolicy(org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA, nome, log, POLICY_TIPO_ENDPOINT_DYNAMIC_DISCOVERY);
	}
	public static void checkTokenPolicyValidazioneValidazioneJWT(String nome, Logger log) throws ConnettoreException{
		checkPolicy(org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA, nome, log, POLICY_TIPO_ENDPOINT_VALIDAZIONE_JWT);
	}
	public static void checkTokenPolicyValidazioneIntrospection(String nome, Logger log) throws ConnettoreException{
		checkPolicy(org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA, nome, log, POLICY_TIPO_ENDPOINT_INTROSPECTION);
	}
	public static void checkTokenPolicyValidazioneUserInfo(String nome, Logger log) throws ConnettoreException{
		checkPolicy(org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA, nome, log, POLICY_TIPO_ENDPOINT_USERINFO);
	}
	public static void checkTokenPolicyNegoziazione(String nome, Logger log) throws ConnettoreException{
		checkPolicy(org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA_RETRIEVE, nome, log, null);
	}
	public static void checkAttributeAuthority(String nome, Logger log) throws ConnettoreException{
		checkPolicy(org.openspcoop2.pdd.core.token.Costanti.ATTRIBUTE_AUTHORITY, nome, log, null);
	}
	private static void checkPolicy(String tipologia, String nome, Logger log,
			String tipoConnettore) throws ConnettoreException{
		IDriverConfigurazioneGet iDriverConfigurazioneGet = ConfigurazionePdDReader.getDriverConfigurazionePdD();
		if(iDriverConfigurazioneGet instanceof DriverConfigurazioneDB driverConfigDB) {
			try {
				GenericProperties gp = driverConfigDB.getGenericProperties(tipologia, nome);
				List<Connettore> l = convertPolicyToConnettore(gp, log);
				if(l!=null && !l.isEmpty()) {
					for (Connettore connettore : l) {
						if(tipoConnettore!=null) {
							String tipo = getPropertyValue(connettore, POLICY_TIPO_ENDPOINT);
							if(!tipoConnettore.equalsIgnoreCase(tipo)) {
								continue;
							}
						}
						try {
							check(connettore, log);
						}catch(Throwable e) {
							// lascio l'errore puro, il tipo di endpoint verrà gestito in altri log
							/**String tipo = getPropertyValue(connettore, POLICY_TIPO_ENDPOINT);
							//String prefixConnettore = tipo!=null ?  ("["+tipo+"] ") : "";
							//throw new ConnettoreException(prefixConnettore+e.getMessage(),e);*/
							throw new ConnettoreException(e.getMessage(),e);					
						}
					}
				}
				return;
			}
			catch(Throwable e) {
				throw new ConnettoreException(e.getMessage(),e);
			}
		}
				
		throw new ConnettoreException("Configurazione con tipologia '"+tipologia+"' e nome '"+nome+"' non trovata");
	}
	
	public static Connettore convertConfigProxyJvmToConnettore(Logger log) throws ConnettoreException{
		
		Connettore connettore = null;
		
		String httpProxyHost = System.getProperty("http.proxyHost");
		String httpProxyPort = System.getProperty("http.proxyPort");
		if(httpProxyHost!=null) {
			connettore = new Connettore();
			
			String url = org.openspcoop2.utils.Costanti.PROTOCOL_HTTP_PREFIX+httpProxyHost+":"+(httpProxyPort!=null ? httpProxyPort : 80+"");
			addProperty(connettore, CostantiConnettori.CONNETTORE_LOCATION, url);
			
			/**
			addProperty(connettore, CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME, httpProxyHost);
			addProperty(connettore, CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT, httpProxyPort!=null ? httpProxyPort : 80+"");
			
			String httpProxyUser = System.getProperty("http.proxyUser");
			String httpProxyPassword = System.getProperty("http.proxyPassword");
			if(httpProxyUser!=null && httpProxyPassword!=null) {
				addProperty(connettore, CostantiConnettori.CONNETTORE_HTTP_PROXY_USERNAME, httpProxyUser);
				addProperty(connettore, CostantiConnettori.CONNETTORE_HTTP_PROXY_PASSWORD, httpProxyPassword);
			}
			*/
		}
		else {
			String httpsProxyHost = System.getProperty("https.proxyHost");
			String httpsProxyPort = System.getProperty("https.proxyPort");
			if(httpsProxyHost!=null) {
				connettore = new Connettore();
				
				String url = org.openspcoop2.utils.Costanti.PROTOCOL_HTTP_PREFIX+httpsProxyHost+":"+(httpsProxyPort!=null ? httpsProxyPort : 80+"");
				addProperty(connettore, CostantiConnettori.CONNETTORE_LOCATION, url);
				
				/**
				addProperty(connettore, CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME, httpsProxyHost);
				addProperty(connettore, CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT, httpsProxyPort!=null ? httpsProxyPort : 443+"");
				
				String httpsProxyUser = System.getProperty("https.proxyUser");
				String httpsProxyPassword = System.getProperty("https.proxyPassword");
				if(httpsProxyUser!=null && httpsProxyPassword!=null) {
					addProperty(connettore, CostantiConnettori.CONNETTORE_HTTP_PROXY_USERNAME, httpsProxyUser);
					addProperty(connettore, CostantiConnettori.CONNETTORE_HTTP_PROXY_PASSWORD, httpsProxyPassword);
				}*/
			}
		}
		
		if(connettore!=null) {
			KeystoreParams truststoreParams = CertificateUtils.readTrustStoreParamsJVM();
			if(truststoreParams!=null) {
				addProperty(connettore, CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_LOCATION, truststoreParams.getPath());
				addProperty(connettore, CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_TYPE, truststoreParams.getType());
				addProperty(connettore, CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD, truststoreParams.getPassword());
			}
		}
		
		if(connettore!=null) {
			connettore.setTipo(TipiConnettore.HTTP.getNome());
		}
	
		return connettore;
	}
	
	public static void checkProxyJvm(Logger log) throws ConnettoreException{
		
		Connettore connettore = convertConfigProxyJvmToConnettore(log);
		
		if(connettore!=null) {
			try {
				checkHTTPEngine(TipiConnettore.HTTP, connettore, log);
			}catch(Throwable e) {
				throw new ConnettoreException(e.getMessage(),e);
			}
		}
		
	}
	private static void addProperty(Connettore connettore, String nome, String valore) {
		Property p = new Property();
		p.setNome(nome);
		p.setValore(valore);
		connettore.addProperty(p);
	}
	public static String getPropertyValue(Connettore connettore, String nome) {
		if(connettore!=null && connettore.sizePropertyList()>0) {
			for (Property p : connettore.getPropertyList()) {
				if(p.getNome().equals(nome)) {
					return p.getValore();
				}
			}
		}
		return null;
	}
	
	public static void check(org.openspcoop2.core.registry.Connettore connettore, Logger log) throws ConnettoreException{
		checkEngine(connettore.mappingIntoConnettoreConfigurazione(), log);
	}
	public static void check(Connettore connettore, Logger log) throws ConnettoreException{
		checkEngine(connettore, log);
	}
	private static void checkEngine(Connettore connettore, Logger log) throws ConnettoreException{
		
		if(!checkSupported(connettore)) {
			throw new ConnettoreException("Tipo '"+connettore.getTipo()+"' non supportato");
		}
		
		try {
			checkTokenPolicyEngine(connettore, log);
		}catch(Throwable e) {
			throw new ConnettoreException(e.getMessage(),e);
		}
		
		TipiConnettore tipo = TipiConnettore.valueOf(connettore.getTipo().toUpperCase());
		switch (tipo) {
		case HTTP, HTTPS:
			try {
				checkHTTPEngine(tipo, connettore, log);
			}catch(Throwable e) {
				throw new ConnettoreException(e.getMessage(),e);
			}
			break;

		default:
			break;
		}
		
	}
	
	private static void checkTokenPolicyEngine(Connettore connettore, Logger log) throws ConnettoreException, DriverConfigurazioneException, DriverConfigurazioneNotFound {
		
		Map<String,String> properties = connettore.getProperties();
		
		PolicyNegoziazioneToken policyNegoziazioneToken = null;
		if(properties!=null && !properties.isEmpty()) {
			Iterator<String> it = properties.keySet().iterator();
			while (it.hasNext()) {
				String propertyName = it.next();
				if(CostantiConnettori.CONNETTORE_TOKEN_POLICY.equals(propertyName)) {
					String tokenPolicy = properties.get(propertyName);
					if(tokenPolicy!=null && !"".equals(tokenPolicy)) {
						boolean forceNoCache = true;
						policyNegoziazioneToken = ConfigurazionePdDManager.getInstance().getPolicyNegoziazioneToken(forceNoCache, tokenPolicy, null);
					}
				}
			}
		}
		
		if(policyNegoziazioneToken!=null) {
			
			String endpoint = policyNegoziazioneToken.getEndpoint();
			
			// Nell'endpoint config ci finisce i timeout e la configurazione proxy
			Properties endpointConfig = policyNegoziazioneToken.getProperties().get(Costanti.POLICY_ENDPOINT_CONFIG);
			
			boolean https = policyNegoziazioneToken.isEndpointHttps();
			boolean httpsClient = false;
			Properties sslConfig = null;
			Properties sslClientConfig = null;
			if(https) {
				sslConfig = policyNegoziazioneToken.getProperties().get(Costanti.POLICY_ENDPOINT_SSL_CONFIG);
				httpsClient = policyNegoziazioneToken.isHttpsAuthentication();
				if(httpsClient) {
					sslClientConfig = policyNegoziazioneToken.getProperties().get(Costanti.POLICY_ENDPOINT_SSL_CLIENT_CONFIG);
				}
			}
			boolean basic = policyNegoziazioneToken.isBasicAuthentication();
			String username = null;
			String password = null;
			if(basic) {
				username = policyNegoziazioneToken.getBasicAuthenticationUsername();
				password = policyNegoziazioneToken.getBasicAuthenticationPassword();
			}
			
			Connettore connettoreTestPolicy = new Connettore();
			Map<String, String> mapProperties = new HashMap<>();
			mapProperties.put(CostantiConnettori.CONNETTORE_LOCATION, endpoint);
			putAll(endpointConfig, mapProperties);
			if(https) {
				putAll(sslConfig, mapProperties);
				if(httpsClient) {
					putAll(sslClientConfig, mapProperties);
				}
			}
			if(basic) {
				mapProperties.put(CostantiConnettori.CONNETTORE_USERNAME, username);
				mapProperties.put(CostantiConnettori.CONNETTORE_PASSWORD, password);
			}
			connettoreTestPolicy.setProperties(mapProperties);

			try {
				checkHTTPEngine(https ? TipiConnettore.HTTPS : TipiConnettore.HTTP, connettoreTestPolicy, log);
			}catch(Exception e) {
				String prefixConnettore = "[EndpointNegoziazioneToken: "+endpoint+"] ";
				if(endpointConfig.containsKey(CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME)) {
					String hostProxy = endpointConfig.getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME);
					String portProxy = endpointConfig.getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT);
					prefixConnettore = prefixConnettore+" [via Proxy: "+hostProxy+":"+portProxy+"] ";
				}
				throw new ConnettoreException(prefixConnettore+e.getMessage(),e);
			}
		}
	}
	public static void putAll(Properties config, Map<String, String> mapProperties) {
		if(config!=null && !config.isEmpty()) {
			Iterator<?> it = config.keySet().iterator();
			while (it.hasNext()) {
				Object object = it.next();
				if(object instanceof String key) {
					mapProperties.put(key, config.getProperty(key));			
				}
			}
		}
	}
		
	private static void checkHTTPEngine(TipiConnettore tipoConnettore, Connettore connettore, Logger log) throws ConnettoreException, UtilsException, MalformedURLException {
		
		SSLConfig sslContextProperties = null;
		Map<String,Object> dynamicMap = null;
		
		Map<String,String> properties = connettore.getProperties();
		
		if(TipiConnettore.HTTPS.equals(tipoConnettore)){
			sslContextProperties = ConnettoreHTTPSProperties.readProperties(properties);
			dynamicMap = DynamicMapBuilderUtils.buildDynamicMap(null, null, null, 
					log);
			sslContextProperties.setDynamicMap(dynamicMap);
		}
		
		Proxy.Type proxyType = null;
		String proxyHostname = null;
		int proxyPort = -1;
		String proxyUsername= null;
		String proxyPassword= null;
		if(properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE)!=null){
			
			String tipo = properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE).trim();
			if(CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE_VALUE_HTTP.equals(tipo)){
				proxyType = Proxy.Type.HTTP;
			}
			else if(CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE_VALUE_HTTPS.equals(tipo)){
				proxyType = Proxy.Type.HTTP;
			}
			else{
				throw new ConnettoreException( "Proprieta' '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE
						+"' non corretta. Impostato un tipo sconosciuto ["+tipo+"] (valori ammessi: "+CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE_VALUE_HTTP
						+","+CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE_VALUE_HTTPS+")");
			}
			
			proxyHostname = properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME);
			if(proxyHostname!=null){
				proxyHostname = proxyHostname.trim();
			}else{
				throw new ConnettoreException( "Proprieta' '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME+
						"' non impostata, obbligatoria in presenza della proprietà '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE+"'");
			}
			
			String proxyPortTmp = properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT);
			if(proxyPortTmp!=null){
				proxyPortTmp = proxyPortTmp.trim();
			}else{
				throw new ConnettoreException( "Proprieta' '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT+
						"' non impostata, obbligatoria in presenza della proprietà '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE+"'");
			}
			try{
				proxyPort = Integer.parseInt(proxyPortTmp);
			}catch(Exception e){
				throw new ConnettoreException( "Proprieta' '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT+"' non corretta: "+ConnettoreBase.readConnectionExceptionMessageFromException(e));
			}
			
			
			proxyUsername = properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_USERNAME);
			if(proxyUsername!=null){
				proxyUsername = proxyUsername.trim();
			}
			
			proxyPassword = properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_PASSWORD);
			if(proxyPassword!=null){
				proxyPassword = proxyPassword.trim();
			}else{
				if(proxyUsername!=null){
					throw new ConnettoreException(  "Proprieta' '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_PASSWORD
							+"' non impostata, obbligatoria in presenza della proprietà '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_USERNAME+"'");
				}
			}
		}
		
		boolean debug = false;
		if(properties.get(CostantiConnettori.CONNETTORE_DEBUG)!=null){
			String debugString = properties.get(CostantiConnettori.CONNETTORE_DEBUG);
			try{
				debug = Boolean.valueOf(debugString);
			}catch(Exception e){
				throw new ConnettoreException( "Proprieta' '"+CostantiConnettori.CONNETTORE_DEBUG+"' non corretta ("+debugString+"): "+ConnettoreBase.readConnectionExceptionMessageFromException(e));
			}
		}
		
		// Gestione https
		SSLContext sslContext = null;
		OCSPValidatorImpl ocspValidator = null;
		if(sslContextProperties!=null){
			
			if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY)!=null){
				String policyType = properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY);
				if(policyType!=null && StringUtils.isNotEmpty(policyType)) {
					GestoreOCSPResource ocspResourceReader = new GestoreOCSPResource(null);
					String crlInputConfig = properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_CRLS);
					LoggerBuffer lb = new LoggerBuffer();
					lb.setLogDebug(log);
					lb.setLogError(log);
					ocspValidator = new OCSPValidatorImpl(lb, crlInputConfig, policyType, ocspResourceReader);
				}
			}

			IBYOKUnwrapManager byokManager = null;
			if(sslContextProperties.getKeyStoreLocation()!=null) {
				try {
					byokManager = BYOKUnwrapPolicyUtilities.getBYOKUnwrapManager(sslContextProperties.getKeyStoreBYOKPolicy(), dynamicMap);
				}catch(Exception e) {
					throw new UtilsException(e.getMessage(),e);
				}
			}
				
			StringBuilder bfSSLConfig = new StringBuilder();
			sslContext = SSLUtilities.generateSSLContext(sslContextProperties, ocspValidator, byokManager, bfSSLConfig);
			
		}
		

		// Creazione URL
		String location = properties.get(CostantiConnettori.CONNETTORE_LOCATION);	
		if(locationDefinedByVariable(location)) {
			return;
		}
		
		// timeout
		int connectionTimeout = -1;
		int readConnectionTimeout = -1;
		if(properties.get(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT)!=null){
			try{
				connectionTimeout = Integer.parseInt(properties.get(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT));
			}catch(Exception e){
				// ignore
			}
		}
		if(connectionTimeout==-1){
			connectionTimeout = HttpUtilities.HTTP_CONNECTION_TIMEOUT;
		}
		if(properties.get(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT)!=null){
			try{
				readConnectionTimeout = Integer.parseInt(properties.get(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT));
			}catch(Exception e){
				// ignore
			}
		}
		if(readConnectionTimeout==-1){
			readConnectionTimeout = HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT;
		}
		
		// Authentication BASIC
		String user = properties.get(CostantiConnettori.CONNETTORE_USERNAME);
		String password = properties.get(CostantiConnettori.CONNETTORE_PASSWORD);
		
		// Authentication Bearer Token
		String bearerToken = properties.get(CostantiConnettori.CONNETTORE_BEARER_TOKEN);
		
		// Authentication Api Key
		String apiKey = properties.get(CostantiConnettori.CONNETTORE_APIKEY);
		String apiKeyHeader = null;
		String appId = null;
		String appIdHeader = null;
		if(apiKey!=null && StringUtils.isNotEmpty(apiKey)){
			apiKeyHeader = properties.get(CostantiConnettori.CONNETTORE_APIKEY_HEADER);
			if(apiKeyHeader==null || StringUtils.isEmpty(apiKeyHeader)) {
				apiKeyHeader = CostantiConnettori.DEFAULT_HEADER_API_KEY;
			}
			
			appId = properties.get(CostantiConnettori.CONNETTORE_APIKEY_APPID);
			if(appId!=null && StringUtils.isNotEmpty(appId)){
				appIdHeader = properties.get(CostantiConnettori.CONNETTORE_APIKEY_APPID_HEADER);
				if(appIdHeader==null || StringUtils.isEmpty(appIdHeader)) {
					appIdHeader = CostantiConnettori.DEFAULT_HEADER_APP_ID;
				}
			}
		}
		
		String httpImpl = properties.get(CostantiConnettori.CONNETTORE_HTTP_IMPL);
		
		HttpRequest request = new HttpRequest();
		request.setUrl(location);
		request.setDebug(debug);
		
		request.setProxyType(proxyType);
		request.setProxyHostname(proxyHostname);
		request.setProxyPort(proxyPort);
		request.setProxyUsername(proxyUsername);
		request.setProxyPassword(proxyPassword);
		
		if(sslContextProperties!=null) {
			request.setKeyStorePath(sslContextProperties.getKeyStoreLocation());
		}
		
		request.setConnectTimeout(connectionTimeout);
		request.setReadTimeout(readConnectionTimeout);
		
		request.setUsername(user);
		request.setPassword(password);
		
		request.setBearerToken(bearerToken);
		
		request.setApiKey(apiKey);
		request.setApiKeyHeader(apiKeyHeader);
		request.setAppId(appId);
		request.setAppIdHeader(appIdHeader);
		
		request.setCheckConnection(true);
		request.setMethod(HttpRequestMethod.GET); // Uso GET come metodo di test
		
		HttpLibrary httpLibrary = HttpLibraryConnection.getDefaultLibrary();
		HttpLibrary cImpl = HttpLibrary.getHttpLibrarySafe(httpImpl);
		if(cImpl!=null) {
			httpLibrary = cImpl;
		}
		HttpLibraryConnection conn = HttpLibraryConnection.fromLibrary(httpLibrary);
		try {
			conn.send(request, sslContext, ocspValidator!=null ? ocspValidator.getOCSPTrustManager() : null);
		}catch(Exception e) {
			String msgException = ConnettoreBase.readConnectionExceptionMessageFromException(e);
			throw new ConnettoreException(msgException, e);
		}
		
	}
	
	
	public static boolean locationDefinedByVariable(String location) throws MalformedURLException {
		if(StringUtils.isNotEmpty(location) && RegExpUtilities.isUrlDefinedByVariable(location)) {
			boolean check = true;
			if(location.contains("://")) {
				if(!location.startsWith("://") && !location.endsWith("://")) {
					int indexOf = location.indexOf("://");
					if(indexOf>0) {
						String protocol = location.substring(0, indexOf);
						if(protocol!=null && RegExpUtilities.isDefinedByVariable(protocol)) {
							check = false;
							/**System.out.println("\n\n=================");
							System.out.println("PROTOCOL '"+protocol+"'");
							System.out.println("NON CONTROLLO, PROTOCOL CONTIENE VARIABILI");*/
						}
						else {
							String other = location.substring((indexOf+"://".length()), location.length());
							if(other.contains("/")) {
								int indexOfHostname = other.indexOf("/");
								if(indexOfHostname>0) {
									String hostname = other.substring(0, indexOfHostname);
									check = hostname==null || !RegExpUtilities.isDefinedByVariable(hostname);
									/**if(!check) {
										System.out.println("\n\n=================");
										System.out.println("PROTOCOL '"+protocol+"'");
										System.out.println("OTHER '"+other+"'");
										System.out.println("HOSTNAME '"+hostname+"'");
										System.out.println("NON CONTROLLO, HOSTNAME CONTIENE VARIABILI");
									}*/
								}
								else {
									// la faccio controllare, è fatta male
								}
							}
							else {
								// non vi è un contesto
								check = !RegExpUtilities.isDefinedByVariable(location);
								/**if(!check) {
									System.out.println("\n\n=================");
									System.out.println("PROTOCOL '"+protocol+"'");
									System.out.println("OTHER '"+other+"'");
									System.out.println("NON CONTROLLO, OTHER CONTIENE VARIABILI");
								}*/
							}
						}
					}
					else {
						// la faccio controllare, è fatta male
					}
				}
				else {
					// la faccio controllare, è fatta male
				}
			}
			else {
				/**System.out.println("\n\n=================");
				System.out.println("COMPLETAMENTE VARIABILI");*/
				check = false; // url completamente definita da variabili, non vi è nemmeno il protocol
			}
			if(!check) {
				/**System.out.println("NON CONTROLLO '"+location+"'!!!");*/
				return true;
			}
		}
		
		return false;
	}
	
	
	
	
	public static String getCertificati(long idConnettore, boolean registro) throws ConnettoreException{
		if(registro) {
			for (IDriverRegistroServiziGet iDriverRegistroServiziGet : RegistroServiziReader.getDriverRegistroServizi().values()) {
				if(iDriverRegistroServiziGet instanceof DriverRegistroServiziDB driverRegistryDB) {
					try {
						org.openspcoop2.core.registry.Connettore connettore = driverRegistryDB.getConnettore(idConnettore);
						return getCertificati(connettore);
					}catch(Throwable e) {
						throw new ConnettoreException(e.getMessage(),e);
					}
				}
			}
		}
		else {
			IDriverConfigurazioneGet iDriverConfigurazioneGet = ConfigurazionePdDReader.getDriverConfigurazionePdD();
			if(iDriverConfigurazioneGet instanceof DriverConfigurazioneDB driverConfigDB) {
				try {
					Connettore connettore = driverConfigDB.getConnettore(idConnettore);
					return getCertificati(connettore);
				}catch(Throwable e) {
					throw new ConnettoreException(e.getMessage(),e);
				}
			}
		}
		throw new ConnettoreException("Connettore con id '"+idConnettore+"' non trovato");
	}
	public static String getCertificati(String nomeConnettore, boolean registro) throws ConnettoreException{
		if(registro) {
			for (IDriverRegistroServiziGet iDriverRegistroServiziGet : RegistroServiziReader.getDriverRegistroServizi().values()) {
				if(iDriverRegistroServiziGet instanceof DriverRegistroServiziDB driverRegistryDB) {
					try {
						org.openspcoop2.core.registry.Connettore connettore = driverRegistryDB.getConnettore(nomeConnettore);
						return getCertificati(connettore);
					}catch(Throwable e) {
						throw new ConnettoreException(e.getMessage(),e);
					}
				}
			}
		}
		else {
			IDriverConfigurazioneGet iDriverConfigurazioneGet = ConfigurazionePdDReader.getDriverConfigurazionePdD();
			if(iDriverConfigurazioneGet instanceof DriverConfigurazioneDB driverConfigDB) {
				try {
					Connettore connettore = driverConfigDB.getConnettore(nomeConnettore);
					return getCertificati(connettore);
				}catch(Throwable e) {
					throw new ConnettoreException(e.getMessage(),e);
				}
			}
		}
		
		throw new ConnettoreException("Connettore con nome '"+nomeConnettore+"' non trovato");
	}

	public static String getCertificati(org.openspcoop2.core.registry.Connettore connettore) throws ConnettoreException{
		return getCertificatiEngine(connettore.mappingIntoConnettoreConfigurazione());
	}
	public static String getCertificati(Connettore connettore) throws ConnettoreException{
		return getCertificatiEngine(connettore);
	}
	
	public static String getCertificatiTokenPolicyValidazione(String nome, Logger log) throws ConnettoreException{
		return getCertificatiPolicy(org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA, nome, log, null);
	}
	public static String getCertificatiTokenPolicyValidazione(String nome, String tipoConnettore, Logger log) throws ConnettoreException{
		return getCertificatiPolicy(org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA, nome, log, tipoConnettore);
	}
	public static String getCertificatiTokenPolicyValidazioneDynamicDiscovery(String nome, Logger log) throws ConnettoreException{
		return getCertificatiPolicy(org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA, nome, log, POLICY_TIPO_ENDPOINT_DYNAMIC_DISCOVERY);
	}
	public static String getCertificatiTokenPolicyValidazioneJwt(String nome, Logger log) throws ConnettoreException{
		return getCertificatiPolicy(org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA, nome, log, POLICY_TIPO_ENDPOINT_VALIDAZIONE_JWT);
	}
	public static String getCertificatiTokenPolicyValidazioneIntrospection(String nome, Logger log) throws ConnettoreException{
		return getCertificatiPolicy(org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA, nome, log, POLICY_TIPO_ENDPOINT_INTROSPECTION);
	}
	public static String getCertificatiTokenPolicyValidazioneUserInfo(String nome, Logger log) throws ConnettoreException{
		return getCertificatiPolicy(org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA, nome, log, POLICY_TIPO_ENDPOINT_USERINFO);
	}
	public static String getCertificatiTokenPolicyNegoziazione(String nome, Logger log) throws ConnettoreException{
		return getCertificatiPolicy(org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA_RETRIEVE, nome, log, null);
	}
	public static String getCertificatiAttributeAuthority(String nome, Logger log) throws ConnettoreException{
		return getCertificatiPolicy(org.openspcoop2.pdd.core.token.Costanti.ATTRIBUTE_AUTHORITY, nome, log, null);
	}
	private static String getCertificatiPolicy(String tipologia, String nome, Logger log,
			String tipoConnettore) throws ConnettoreException{
		IDriverConfigurazioneGet iDriverConfigurazioneGet = ConfigurazionePdDReader.getDriverConfigurazionePdD();
		if(iDriverConfigurazioneGet instanceof DriverConfigurazioneDB driverConfigDB) {
			try {
				GenericProperties gp = driverConfigDB.getGenericProperties(tipologia, nome);
				List<Connettore> l = convertPolicyToConnettore(gp, log);
				List<String> hostPort = new ArrayList<>();
				if(l!=null && !l.isEmpty()) {
					StringBuilder sb = new StringBuilder();
					for (Connettore connettore : l) {
						if(tipoConnettore!=null) {
							String tipo = getPropertyValue(connettore, POLICY_TIPO_ENDPOINT);
							if(!tipoConnettore.equalsIgnoreCase(tipo)) {
								continue;
							}
						}
						
						if(l.size()>1 && !hostPort.isEmpty()) {
							String endpoint = getPropertyValue(connettore, CostantiConnettori.CONNETTORE_LOCATION);
							if(endpoint!=null) {
								try {
									URL url = new URI(endpoint).toURL();
									int port = url.getPort();
									if(port<=0) {
										if("https".equals(url.getProtocol())){
											port = 443;
										}
										else {
											port = 80;
										}
									}
									String check = url.getHost()+":"+port; 
									if(hostPort.contains(check)) {
										continue;
									}
									else {
										hostPort.add(check);
									}
								}catch(Throwable t) {
									// ignore
								}
							}
						}
						
						try {
							String s = getCertificatiEngine(connettore);
							sb.append(s);
						}catch(Throwable e) {
							/**String tipo = getPropertyValue(connettore, POLICY_TIPO_ENDPOINT);*/
							String tipo = null; // lascio l'errore puro, il tipo di endpoint verrà gestito in altri log
							String prefixConnettore = tipo!=null ?  ("["+tipo+"] ") : "";
							throw new ConnettoreException(prefixConnettore+e.getMessage(),e);
						}
					}
					if(!sb.isEmpty()) {
						return sb.toString();
					}
				}
			}
			catch(Throwable e) {
				throw new ConnettoreException(e.getMessage(),e);
			}
		}
				
		throw new ConnettoreException("Configurazione con tipologia '"+tipologia+"' e nome '"+nome+"' non trovata");
	}
	
	private static String getCertificatiEngine(Connettore connettore) throws ConnettoreException {
		
		try {
		
			Map<String,String> properties = connettore.getProperties();
			String location = properties!=null ? properties.get(CostantiConnettori.CONNETTORE_LOCATION) : null;	
			if(location==null || "".equals(location)) {
				throw new ConnettoreException("Il connettore non possiede un endpoint");
			}
			URL url = new URI( location ).toURL();
			String host = url.getHost();
			if(host==null || "".equals(host)) {
				throw new ConnettoreException("L'endpoint '"+host+"' non contiene un host");
			}
			int port = url.getPort();
			if(port<=0) {
				if("https".equalsIgnoreCase(url.getProtocol())) {
					port = 443;
				}
				else if("http".equalsIgnoreCase(url.getProtocol())) {
					port = 80;
				}
				else {
					throw new ConnettoreException("L'endpoint '"+host+"' contiene un protocollo '"+url.getProtocol()+"' non supportato");
				}
			}
			
			return SSLUtilities.readPeerCertificates(host, port);
		
		}catch(Throwable e) {
			throw new ConnettoreException(e.getMessage(),e);
		}
			
	}
	
}
