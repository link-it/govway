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


package org.openspcoop2.pdd.core.token.attribute_authority;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.mvc.properties.Item;
import org.openspcoop2.core.mvc.properties.provider.IProvider;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderInfo;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.pdd.core.dynamic.DynamicHelperCostanti;
import org.openspcoop2.pdd.core.token.Costanti;
import org.openspcoop2.pdd.core.token.TokenUtilities;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.utils.AbstractSecurityProvider;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.transport.http.SSLUtilities;

/**     
 * GetTokenProvider
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AttributeAuthorityProvider implements IProvider {
	
	@Override
	public void validateId(String name) throws ProviderException, ProviderValidationException {	
		if(name==null || StringUtils.isEmpty(name)) {
			throw new ProviderValidationException("Deve essere indicato un nome che identifica la policy per verso l'Attribute Authority");
		}
		if(name.contains(" ")) {
			throw new ProviderValidationException("Il nome associato alla policy non deve contenere spazi");
		}
		boolean match = false;
		try {
			match = RegularExpressionEngine.isMatch(name,"^[_A-Za-z][\\-_A-Za-z0-9]*$"); 
		}catch(Throwable e) {
			throw new ProviderException(e.getMessage(),e);
		}
		if (!match) {
			throw new ProviderValidationException("Il nome associato alla policy può iniziare solo con un carattere [A-Za-z] o il simbolo '_' e dev'essere formato solo da caratteri, cifre, '_' , e '-'");
		}
	}
	
	@Override
	public void validate(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		
		Properties pDefault = TokenUtilities.getDefaultProperties(mapProperties);
			
		boolean endpointSSL = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_ENDPOINT_HTTPS_STATO);
		boolean ssl = TokenUtilities.isEnabled(pDefault, org.openspcoop2.pdd.core.token.attribute_authority.Costanti.AA_AUTH_SSL_CLIENT_STATO);
		
		if(endpointSSL || ssl) {
			Properties p = mapProperties.get(Costanti.POLICY_ENDPOINT_SSL_CONFIG);
			if(p==null || p.size()<=0) {
				throw new ProviderValidationException("Nonostante sia stato indicato un endpoint 'https', non è stata fornita una configurazione dei parametri ssl da utilizzare");
			}
			
			String trustAllCerts = p.getProperty(CostantiConnettori.CONNETTORE_HTTPS_TRUST_ALL_CERTS);
			boolean trustAll = false;
			if(trustAllCerts!=null && StringUtils.isNotEmpty(trustAllCerts)) {
				trustAll = Boolean.valueOf(trustAllCerts);
			}
			
			if(!trustAll) {
				String location = p.getProperty(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
				if(location==null || "".equals(location)) {
					throw new ProviderValidationException("Indicare un path del TrustStore per l'autenticazione server");
				}
				if(location.contains(" ")) {
					throw new ProviderValidationException("Non indicare spazi nel path del TrustStore per l'autenticazione server");
				}
				
				String algo = p.getProperty(CostantiConnettori.CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITHM);
				if(algo==null || "".equals(algo)) {
					throw new ProviderValidationException("Indicare un algoritmo per l'autenticazione server");
				}
				if(algo.contains(" ")) {
					throw new ProviderValidationException("Non indicare spazi nell'algoritmo per l'autenticazione server");
				}
			}
		}
		
		String url = pDefault.getProperty(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.AA_URL);
		if(url==null || "".equals(url)) {
			throw new ProviderValidationException("Non e' stata fornita la url dell'Attribute Authority");
		}
		if(url.contains(" ")) {
			throw new ProviderValidationException("Non indicare spazi nella url");
		}
		try{
			org.openspcoop2.utils.regexp.RegExpUtilities.validateUrl(url);
		}catch(Exception e){
			throw new ProviderValidationException("La URL fornita non è valida: "+e.getMessage());
		}	
		
					
		boolean basic = TokenUtilities.isEnabled(pDefault, org.openspcoop2.pdd.core.token.attribute_authority.Costanti.AA_AUTH_BASIC_STATO);
		if(basic) {
			String username = pDefault.getProperty(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.AA_AUTH_BASIC_USERNAME);
			if(username==null || "".equals(username)) {
				throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'HttpBasic', non è stato fornito un 'Username' da utilizzare durante la connessione verso il servizio");
			}
			if(username.contains(" ")) {
				throw new ProviderValidationException("Non indicare spazi nel 'Username'");
			}
			String password = pDefault.getProperty(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.AA_AUTH_BASIC_PASSWORD);
			if(password==null || "".equals(password)) {
				throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'HttpBasic', non è stato fornita una 'Password' da utilizzare durante la connessione verso il servizio");
			}
			if(password.contains(" ")) {
				throw new ProviderValidationException("Non indicare spazi nella 'Password'");
			}
		}
		
		boolean bearer = TokenUtilities.isEnabled(pDefault, org.openspcoop2.pdd.core.token.attribute_authority.Costanti.AA_AUTH_BEARER_STATO);
		if(bearer) {
			String token = pDefault.getProperty(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.AA_AUTH_BEARER_TOKEN);
			if(token==null || "".equals(token)) {
				throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'Authorization Bearer', non è stato fornito un token di autenticazione da inoltrare al servizio");
			}
			if(token.contains(" ")) {
				throw new ProviderValidationException("Non indicare spazi nel token di autenticazione da inoltrare al servizio");
			}
		}
		
		if(ssl) {
			Properties p = mapProperties.get(Costanti.POLICY_ENDPOINT_SSL_CLIENT_CONFIG);
			if(p==null || p.size()<=0) {
				throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'Https', non sono stati forniti i parametri di connessione ssl client da utilizzare verso il servizio");
			}
			
			String location = p.getProperty(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			if(location!=null && !"".equals(location)) {
				if(location.contains(" ")) {
					throw new ProviderValidationException("Non indicare spazi nel path del KeyStore per l'autenticazione client");
				}
			}
			
			String algo = p.getProperty(CostantiConnettori.CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITHM);
			if(algo==null || "".equals(algo)) {
				throw new ProviderValidationException("Indicare un algoritmo per l'autenticazione client");
			}
			if(algo.contains(" ")) {
				throw new ProviderValidationException("Non indicare spazi nell'algoritmo per l'autenticazione client");
			}

		}
		
		boolean proxy = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_ENDPOINT_PROXY_STATO);
		if(proxy) {
			Properties p = mapProperties.get(Costanti.POLICY_ENDPOINT_CONFIG);
			if(p==null || p.size()<=0) {
				throw new ProviderValidationException("Nonostante sia richiesta un proxy, non sono stati forniti i parametri di connessione");
			}
			
			String hostname = p.getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME);
			if(hostname==null || "".equals(hostname)) {
				throw new ProviderValidationException("Indicare un hostname per il Proxy");
			}
			if(hostname.contains(" ")) {
				throw new ProviderValidationException("Non indicare spazi nell'hostname del Proxy");
			}
			
			String username = p.getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_USERNAME);
			if(username!=null && !"".equals(username)) {
				if(username.contains(" ")) {
					throw new ProviderValidationException("Non indicare spazi nell'username del Proxy");
				}
			}
			
			String password = p.getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_PASSWORD);
			if(password!=null && !"".equals(password)) {
				if(password.contains(" ")) {
					throw new ProviderValidationException("Non indicare spazi nella password del Proxy");
				}
			}
		}
		
//		String scopes = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_SCOPES);
//		if(scopes!=null && !"".equals(scopes)) {
//			if(scopes.contains(" ")) {
//				throw new ProviderValidationException("Non indicare spazi tra gli scope forniti");
//			}
//		}
//		
//		String audience = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_AUDIENCE);
//		if(audience!=null && !"".equals(audience)) {
//			if(audience.contains(" ")) {
//				throw new ProviderValidationException("Non indicare spazi nel valore dell'audience");
//			}
//		}
//		
//		String mode = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE);
//		if(mode==null) {
//			throw new ProviderValidationException("Nessuna modalità di forward indicata");
//		}
//		if(!Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE_RFC6750_HEADER.equals(mode) &&
//				!Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE_RFC6750_URL.equals(mode) &&
//				!Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE_CUSTOM_HEADER.equals(mode) &&
//				!Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE_CUSTOM_URL.equals(mode)
//				) {
//			throw new ProviderValidationException("La modalità di forward indicata '"+mode+"' non è supportata");	
//		}
//		if(Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE_CUSTOM_HEADER.equals(mode)) {
//			String hdr = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE_CUSTOM_HEADER_NAME);
//			if(hdr==null || "".equals(hdr)) {
//				throw new ProviderValidationException("La modalità di forward indicata prevede l'indicazione del nome di un header http");
//			}
//			if(hdr.contains(" ")) {
//				throw new ProviderValidationException("Non indicare spazi nel nome dell'header HTTP indicato per la modalità di forward");
//			}
//		}
//		else if(Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE_CUSTOM_URL.equals(mode)) {
//			String urlP = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE_CUSTOM_URL_PARAMETER_NAME);
//			if(urlP==null || "".equals(urlP)) {
//				throw new ProviderValidationException("La modalità di forward indicata prevede l'indicazione del nome di una proprietà della url");
//			}
//			if(urlP.contains(" ")) {
//				throw new ProviderValidationException("Non indicare spazi nel nome della proprietà della url indicata per la modalità di forward");
//			}
//		}
//		
	}

	@Override
	public List<String> getValues(String id) throws ProviderException {
		if(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_SIGNATURE_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm [] tmp = org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				if(org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.NONE.equals(tmp[i])) {
					continue;
				}
				if(!tmp[i].name().toLowerCase().startsWith("hs")) {
					l.add(tmp[i].name());
				}
			}
			return l;
		}
		else if(Costanti.ID_TIPOLOGIA_HTTPS.equals(id)) {
			List<String> tipologie = null;
			try{
				tipologie = SSLUtilities.getSSLSupportedProtocols();
			}catch(Exception e){
				tipologie = SSLUtilities.getAllSslProtocol();
			}
			return tipologie;
		}
		else if(Costanti.ID_AA_JWS_TRUSTSTORE_TYPE.equals(id) || 
				Costanti.ID_AA_JWS_KEYSTORE_TYPE.equals(id) || 
				Costanti.ID_HTTPS_TRUSTSTORE_TYPE.equals(id) ||
				Costanti.ID_HTTPS_KEYSTORE_TYPE.equals(id)) {
			return getStoreType(id,true);
		}
		return null;
	}

	@Override
	public List<String> getLabels(String id) throws ProviderException {
		if(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_SIGNATURE_ALGORITHM.equals(id)) {
			List<String> l = this.getValues(id);
			List<String> labels = new ArrayList<>();
			for (String value : l) {
				if(value.contains("_")) {
					String t = new String(value);
					while(t.contains("_")) {
						t = t.replace("_", "-");
					}
					labels.add(t);
				}
				else {
					labels.add(value);
				}
			}
			return labels;
		}
		else if(Costanti.ID_AA_JWS_TRUSTSTORE_TYPE.equals(id) || 
				Costanti.ID_AA_JWS_KEYSTORE_TYPE.equals(id) || 
				Costanti.ID_HTTPS_TRUSTSTORE_TYPE.equals(id) ||
				Costanti.ID_HTTPS_KEYSTORE_TYPE.equals(id)) {
			return getStoreType(id,false);
		}
		return this.getValues(id); // torno uguale ai valori negli altri casi
	}

	private List<String> getStoreType(String id,boolean value){
		boolean trustStore = true;
		boolean secret = false;
		List<String> l = new ArrayList<String>();
		l.add(value ? SecurityConstants.KEYSTORE_TYPE_JKS_VALUE : SecurityConstants.KEYSTORE_TYPE_JKS_LABEL);
		l.add(value ? SecurityConstants.KEYSTORE_TYPE_PKCS12_VALUE : SecurityConstants.KEYSTORE_TYPE_PKCS12_LABEL);
		if(Costanti.ID_AA_JWS_TRUSTSTORE_TYPE.equals(id) || 
				Costanti.ID_AA_JWS_KEYSTORE_TYPE.equals(id)) {
			l.add(value ? SecurityConstants.KEYSTORE_TYPE_JWK_VALUE: SecurityConstants.KEYSTORE_TYPE_JWK_LABEL);
		}
//		if(Costanti.ID_AA_JWS_TRUSTSTORE_TYPE.equals(id)) {
//			l.add(value ? SecurityConstants.KEYSTORE_TYPE_JCEKS_VALUE : SecurityConstants.KEYSTORE_TYPE_JCEKS_LABEL);
//			secret = true;
//		}
		
		if(Costanti.ID_AA_JWS_KEYSTORE_TYPE.equals(id) ||
				Costanti.ID_HTTPS_KEYSTORE_TYPE.equals(id)) {
			trustStore = false;
		}
		HSMUtils.fillTIPOLOGIE_KEYSTORE(trustStore, false, l);
		
		if(secret) {
			// aggiunto info mancanti come secret
			List<String> lSecret = new ArrayList<String>();
			HSMUtils.fillTIPOLOGIE_KEYSTORE(false, true, l);
			if(lSecret!=null && !lSecret.isEmpty()) {
				for (String type : lSecret) {
					if(!l.contains(type)) {
						l.add(type);
					}
				}
			}
		}
		return l;
	}
	
	@Override
	public String getDefault(String id) throws ProviderException {
		if(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_SIGNATURE_ALGORITHM.equals(id)) {
			return org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.RS256.name();
		}
		else if(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_TOKEN_JWT_EXPIRED_TTL_SECONDS.equals(id)) {
			return Costanti.POLICY_RETRIEVE_TOKEN_JWT_EXPIRED_TTL_SECONDS_DEFAULT_VALUE;
		}
		else if(Costanti.ID_TIPOLOGIA_HTTPS.equals(id)) {
			return SSLUtilities.getSafeDefaultProtocol();
		}
		return null;
	}
	
	@Override
	public ProviderInfo getProviderInfo(String id) throws ProviderException{
		if(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_ENDPOINT_URL.equals(id) ||
				org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_AUTENTICAZIONE_ENDPOINT_BASIC_USERNAME.equals(id) ||
				//org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_AUTENTICAZIONE_ENDPOINT_BEARER_TOKEN.equals(id) ||
				org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_RICHIESTA_JWS_PAYLOAD_ISSUER.equals(id) ||
				org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_RICHIESTA_JWS_PAYLOAD_SUBJECT.equals(id) ||
				org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_RICHIESTA_JWS_PAYLOAD_AUDIENCE.equals(id) ||
				org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_RISPOSTA_JWS_PAYLOAD_AUDIENCE.equals(id)
				) {
			ProviderInfo pInfo = new ProviderInfo();
			pInfo.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASPORTO);
			pInfo.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_VALORI);
			return pInfo;
		}
		else if(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_RICHIESTA_JWS_PAYLOAD_TEMPLATE.equals(id) ||
				org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_RICHIESTA_JWS_PAYLOAD_CLAIMS.equals(id) ||
				org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_RICHIESTA_PAYLOAD_TEMPLATE.equals(id)
				) {
			ProviderInfo pInfo = new ProviderInfo();
			pInfo.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASPORTO);
			pInfo.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_VALORI_CON_REQUIRED_ATTRIBUTES);
			return pInfo;
		}
		else if(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_RICHIESTA_JWS_PAYLOAD_TEMPLATE_FREEMARKER.equals(id) ||
				org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_RICHIESTA_PAYLOAD_TEMPLATE_FREEMARKER.equals(id)
				) {
			ProviderInfo pInfo = new ProviderInfo();
			pInfo.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_FREEMARKER);
			pInfo.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_OBJECT_VALORI);
			return pInfo;
		}
		else if(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_RICHIESTA_JWS_PAYLOAD_TEMPLATE_VELOCITY.equals(id) ||
				org.openspcoop2.pdd.core.token.attribute_authority.Costanti.ID_AA_RICHIESTA_PAYLOAD_TEMPLATE_VELOCITY.equals(id)
				) {
			ProviderInfo pInfo = new ProviderInfo();
			pInfo.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_VELOCITY);
			pInfo.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_INFO_OBJECT_VALORI);
			return pInfo;
		}
		
		
		
		return null;
	}
	
	@Override
	public String dynamicUpdate(List<?> items, Map<String, String> mapNameValue, Item item, String actualValue) {
	
		if(Costanti.ID_AA_JWS_TRUSTSTORE_FILE.equals(item.getName()) ||
				Costanti.ID_AA_JWS_KEYSTORE_FILE.equals(item.getName()) ||
				Costanti.ID_HTTPS_TRUSTSTORE_FILE.equals(item.getName()) ||
				Costanti.ID_HTTPS_KEYSTORE_FILE.equals(item.getName())) {
			
			String type = Costanti.ID_AA_JWS_TRUSTSTORE_TYPE;
			if(Costanti.ID_AA_JWS_KEYSTORE_FILE.equals(item.getName())) {
				type = Costanti.ID_AA_JWS_KEYSTORE_TYPE;
			}
			else if(Costanti.ID_HTTPS_TRUSTSTORE_FILE.equals(item.getName())) {
				type = Costanti.ID_HTTPS_TRUSTSTORE_TYPE;
			}
			else if(Costanti.ID_HTTPS_KEYSTORE_FILE.equals(item.getName())) {
				type = Costanti.ID_HTTPS_KEYSTORE_TYPE;
			}
			
			return AbstractSecurityProvider.processStoreFile(type, items, mapNameValue, item, actualValue);
		}
		else if(Costanti.ID_AA_JWS_TRUSTSTORE_PASSWORD.equals(item.getName()) ||
				Costanti.ID_AA_JWS_KEYSTORE_PASSWORD.equals(item.getName()) ||
				Costanti.ID_HTTPS_TRUSTSTORE_PASSWORD.equals(item.getName()) ||
				Costanti.ID_HTTPS_KEYSTORE_PASSWORD.equals(item.getName())) {
			
			String type = Costanti.ID_AA_JWS_TRUSTSTORE_TYPE;
			if(Costanti.ID_AA_JWS_KEYSTORE_PASSWORD.equals(item.getName())) {
				type = Costanti.ID_AA_JWS_KEYSTORE_TYPE;
			}
			else if(Costanti.ID_HTTPS_TRUSTSTORE_PASSWORD.equals(item.getName())) {
				type = Costanti.ID_HTTPS_TRUSTSTORE_TYPE;
			}
			else if(Costanti.ID_HTTPS_KEYSTORE_PASSWORD.equals(item.getName())) {
				type = Costanti.ID_HTTPS_KEYSTORE_TYPE;
			}
			
			return AbstractSecurityProvider.processStorePassword(type, items, mapNameValue, item, actualValue);
		}
		else if(Costanti.ID_AA_JWS_KEYSTORE_PASSWORD_PRIVATE_KEY.equals(item.getName()) ||
				Costanti.ID_HTTPS_KEYSTORE_PASSWORD_PRIVATE_KEY.equals(item.getName()) ) {
			
			String type = Costanti.ID_AA_JWS_KEYSTORE_TYPE;
			if(Costanti.ID_HTTPS_KEYSTORE_PASSWORD_PRIVATE_KEY.equals(item.getName())) {
				type = Costanti.ID_HTTPS_KEYSTORE_TYPE;
			}
			
			return AbstractSecurityProvider.processStoreKeyPassword(type, items, mapNameValue, item, actualValue);
		}
		
		return actualValue;
	}

}