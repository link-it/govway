/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.mvc.properties.Item;
import org.openspcoop2.core.mvc.properties.provider.IProvider;
import org.openspcoop2.core.mvc.properties.provider.InputValidationUtils;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.pdd.core.token.parser.TipologiaClaims;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.jose.JOSECostanti;
import org.openspcoop2.security.message.jose.SecurityProvider;
import org.openspcoop2.security.message.utils.AbstractSecurityProvider;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.SSLUtilities;

/**     
 * TokenProvider
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TokenProvider implements IProvider {

	@Override
	public void validate(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		
		Properties pDefault = TokenUtilities.getDefaultProperties(mapProperties);
		
		
		// ***** TOKEN SOURCE *******
		
		String position = pDefault.getProperty(Costanti.POLICY_TOKEN_SOURCE);
		if(position==null) {
			throw new ProviderValidationException("Non è stata indicata la posizione del token");
		}
		if(!Costanti.POLICY_TOKEN_SOURCE_RFC6750.equals(position) &&
				!Costanti.POLICY_TOKEN_SOURCE_RFC6750_HEADER.equals(position) &&
				!Costanti.POLICY_TOKEN_SOURCE_RFC6750_FORM.equals(position) && 
				!Costanti.POLICY_TOKEN_SOURCE_RFC6750_URL.equals(position) &&
				!Costanti.POLICY_TOKEN_SOURCE_CUSTOM_HEADER.equals(position) &&
				!Costanti.POLICY_TOKEN_SOURCE_CUSTOM_URL.equals(position) ) {
			throw new ProviderValidationException("La posizione del token indicata '"+position+"' non è supportata");
		}
		
		if(Costanti.POLICY_TOKEN_SOURCE_CUSTOM_HEADER.equals(position)) {
			String headerName = pDefault.getProperty(Costanti.POLICY_TOKEN_SOURCE_CUSTOM_HEADER_NAME);
			if(headerName==null || "".equals(headerName)) {
				throw new ProviderValidationException("Non è stato indicato il nome dell'header http che deve contenere il token");
			}
			if(headerName.contains(" ")) {
				throw new ProviderValidationException("Il nome fornito per l'header http che deve contenere il token non deve possedere spazi");
			}
		}
		else if(Costanti.POLICY_TOKEN_SOURCE_CUSTOM_URL.equals(position)) {
			String pName = pDefault.getProperty(Costanti.POLICY_TOKEN_SOURCE_CUSTOM_URL_PROPERTY_NAME);
			if(pName==null) {
				throw new ProviderValidationException("Non è stato indicato il nome della proprietà della URL che deve contenere il token");
			}
			if(pName.contains(" ")) {
				throw new ProviderValidationException("Il nome fornito per la proprietà della URL che deve contenere il token non deve possedere spazi");
			}
		}
		
		String tokenType = pDefault.getProperty(Costanti.POLICY_TOKEN_TYPE);
		if(tokenType==null) {
			throw new ProviderValidationException("Non è stata indicato il tipo del token");
		}
		if(!Costanti.POLICY_TOKEN_TYPE_OPAQUE.equals(tokenType) &&
				!Costanti.POLICY_TOKEN_TYPE_JWS.equals(tokenType) &&
				!Costanti.POLICY_TOKEN_TYPE_JWE.equals(tokenType)) {
			throw new ProviderValidationException("Il tipo di token indicato '"+tokenType+"' non è supportato");
		}
		
		boolean validazione = TokenUtilities.isValidazioneEnabled(pDefault);
		boolean introspection = TokenUtilities.isIntrospectionEnabled(pDefault);
		boolean userInfo = TokenUtilities.isUserInfoEnabled(pDefault);
		boolean forward = TokenUtilities.isTokenForwardEnabled(pDefault);
				
		if(!validazione &&  !introspection && !userInfo && !forward) {
			throw new ProviderValidationException("Almeno una modalità di elaborazione del token deve essere selezionata");
		}
		
		if(validazione) {
			if(!Costanti.POLICY_TOKEN_TYPE_JWS.equals(tokenType) &&
					!Costanti.POLICY_TOKEN_TYPE_JWE.equals(tokenType)) {
				throw new ProviderValidationException("Il tipo di token indicato '"+tokenType+"' non è utilizzabile con una validazione JWT");
			}
			
			if(Costanti.POLICY_TOKEN_TYPE_JWS.equals(tokenType)) {
				Properties p = mapProperties.get(Costanti.POLICY_VALIDAZIONE_JWS_VERIFICA_PROP_REF_ID);
				if(p==null || p.size()<=0) {
					throw new ProviderValidationException("Non è stata fornita una configurazione per effettuare la validazione JWS");
				}
				
				if(!p.containsKey("rs.security.keystore") && !p.containsKey("rs.security.keystore.jwkset")) {
					// altrimenti è stato fatto inject del keystore
					String file = p.getProperty("rs.security.keystore.file");
					InputValidationUtils.validateTextAreaInput(file, "Validazione JWT - TrustStore - File");
				}
			}
			else if(Costanti.POLICY_TOKEN_TYPE_JWE.equals(tokenType)) {
				Properties p = mapProperties.get(Costanti.POLICY_VALIDAZIONE_JWE_DECRYPT_PROP_REF_ID);
				if(p==null || p.size()<=0) {
					throw new ProviderValidationException("Non è stata fornita una configurazione per effettuare la validazione JWE");
				}
				
				if(!p.containsKey("rs.security.keystore") && !p.containsKey("rs.security.keystore.jwkset")) {
					// altrimenti è stato fatto inject del keystore
					String file = p.getProperty("rs.security.keystore.file");
					InputValidationUtils.validateTextAreaInput(file, "Validazione JWT - KeyStore - File");
				}
			}
			
			String parserType = pDefault.getProperty(Costanti.POLICY_VALIDAZIONE_CLAIMS_PARSER_TYPE);
			if(parserType==null) {
				throw new ProviderValidationException("Non è stato indicato il parser per i claims da utilizzare dopo la validazione JWT");
			}
			TipologiaClaims tipologiaClaims = null;
			try {
				tipologiaClaims = TipologiaClaims.valueOf(parserType);
				if(tipologiaClaims==null) {
					throw new Exception("Sconosciuto");
				}
			}catch(Exception e) {
				throw new ProviderValidationException("È stato indicato un parser '"+parserType+"', per i claims da utilizzare dopo la validazione JWT, sconosciuto");
			}
			if(TipologiaClaims.CUSTOM.name().equals(parserType)) {
				String parserTypeCustomClass = pDefault.getProperty(Costanti.POLICY_VALIDAZIONE_CLAIMS_PARSER_CLASS_NAME);
				if(parserTypeCustomClass==null || "".equals(parserTypeCustomClass)) {
					throw new ProviderValidationException("Non è stata fornita la classe del parser dei claims da utilizzare dopo la validazione JWT");
				}
				if(parserTypeCustomClass.contains(" ")) {
					throw new ProviderValidationException("Non indicare spazi nella classe del parser dei claims da utilizzare dopo la validazione JWT");
				}
			}
		}
		
		boolean endpointSSL = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_ENDPOINT_HTTPS_STATO);
		if(endpointSSL) {
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
				InputValidationUtils.validateTextAreaInput(location, "Https - Autenticazione Server - File (TrustStore per l'autenticazione server)");
				
				String algo = p.getProperty(CostantiConnettori.CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITHM);
				if(algo==null || "".equals(algo)) {
					throw new ProviderValidationException("Indicare un algoritmo per l'autenticazione server");
				}
				if(algo.contains(" ")) {
					throw new ProviderValidationException("Non indicare spazi nell'algoritmo per l'autenticazione server");
				}
				
				String location_crl = p.getProperty(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_CRLs);
				if(location_crl!=null && !"".equals(location_crl)) {
					InputValidationUtils.validateTextAreaInput(location_crl, "Https - Autenticazione Server - CRL File(s)");
				}
			}
		}
		
		if(introspection) {
			
			String url = pDefault.getProperty(Costanti.POLICY_INTROSPECTION_URL);
			InputValidationUtils.validateTextAreaInput(url, "Token Introspection - URL");
			try{
				org.openspcoop2.utils.regexp.RegExpUtilities.validateUrl(url);
			}catch(Exception e){
				throw new ProviderValidationException("La URL fornita per il servizio 'Introspection' non è valida: "+e.getMessage());
			}	
			
			String tipoTokenRequest = pDefault.getProperty(Costanti.POLICY_INTROSPECTION_REQUEST_TOKEN_POSITION);
			if(tipoTokenRequest==null) {
				throw new ProviderValidationException("Non è stato indicato il tipo di spedizione del token da inoltrare al servizio di Introspection");
			}
			TipoTokenRequest tipoTokenRequestEnum = null;
			try {
				tipoTokenRequestEnum = TipoTokenRequest.valueOf(tipoTokenRequest);
				if(tipoTokenRequestEnum==null) {
					throw new Exception("Sconosciuto");
				}
			}catch(Exception e) {
				throw new ProviderValidationException("È stato indicato un tipo '"+tipoTokenRequest+"' di spedizione del token da inoltrare al servizio di Introspection sconosciuto");
			}
			switch (tipoTokenRequestEnum) {
			case authorization:
				break;
			case header:
				String tmp = pDefault.getProperty(Costanti.POLICY_INTROSPECTION_REQUEST_TOKEN_POSITION_HEADER_NAME);
				if(tmp==null || "".equals(url)) {
					throw new ProviderValidationException("Non è stato indicato il nome dell'header http su cui inoltrare il token al servizio di Introspection");
				}
				if(tmp.contains(" ")) {
					throw new ProviderValidationException("Non indicare spazi nel nome dell'header http su cui inoltrare il token al servizio di Introspection");
				}
				break;
			case url:
				tmp = pDefault.getProperty(Costanti.POLICY_INTROSPECTION_REQUEST_TOKEN_POSITION_URL_PROPERTY_NAME);
				if(tmp==null || "".equals(url)) {
					throw new ProviderValidationException("Non è stato indicato il nome della proprietà della URL su cui inoltrare il token al servizio di Introspection");
				}
				if(tmp.contains(" ")) {
					throw new ProviderValidationException("Non indicare spazi nel nome della proprietà della URL su cui inoltrare il token al servizio di Introspection");
				}
				break;
			case form:
				tmp = pDefault.getProperty(Costanti.POLICY_INTROSPECTION_REQUEST_TOKEN_POSITION_FORM_PROPERTY_NAME);
				if(tmp==null || "".equals(url)) {
					throw new ProviderValidationException("Non è stato indicato il nome della proprietà della FORM su cui inoltrare il token al servizio di Introspection");
				}
				if(tmp.contains(" ")) {
					throw new ProviderValidationException("Non indicare spazi nel nome della proprietà della FORM su cui inoltrare il token al servizio di Introspection");
				}
				break;
			}
			
			String httpRequestMethod = pDefault.getProperty(Costanti.POLICY_INTROSPECTION_HTTP_METHOD);
			if(httpRequestMethod==null) {
				throw new ProviderValidationException("Non è stato indicato il tipo di richiesta HTTP da utilizzare per inoltrare il token al servizio di Introspection");
			}
			HttpRequestMethod httpRequestMethodEnum = null;
			try {
				httpRequestMethodEnum = HttpRequestMethod.valueOf(httpRequestMethod);
				if(httpRequestMethodEnum==null) {
					throw new Exception("Sconosciuto");
				}
			}catch(Exception e) {
				throw new ProviderValidationException("È stato indicato un tipo '"+httpRequestMethod+"' di richiesta HTTP da utilizzare per inoltrare il token al servizio di Introspection sconosciuto");
			}
			
			String contentType = pDefault.getProperty(Costanti.POLICY_INTROSPECTION_CONTENT_TYPE);
			if(!TipoTokenRequest.form.equals(tipoTokenRequestEnum)) {
				if(HttpRequestMethod.POST.equals(httpRequestMethodEnum) || 
						HttpRequestMethod.PUT.equals(httpRequestMethodEnum) || 
						HttpRequestMethod.PATCH.equals(httpRequestMethodEnum) || 
						HttpRequestMethod.LINK.equals(httpRequestMethodEnum) || 
						HttpRequestMethod.UNLINK.equals(httpRequestMethodEnum) ) {
					if(contentType==null || "".equals(contentType)) {
						throw new ProviderValidationException("Non è stato indicato il ContentType da utilizzare nella richiesta HTTP prodotta per inoltrare il token al servizio di Introspection");
					}
					if(contentType.contains(" ")) {
						throw new ProviderValidationException("Non indicare spazi nel ContentType da utilizzare nella richiesta HTTP prodotta per inoltrare il token al servizio di Introspection");
					}
				}
			}
			
			String parserType = pDefault.getProperty(Costanti.POLICY_INTROSPECTION_CLAIMS_PARSER_TYPE);
			if(parserType==null) {
				throw new ProviderValidationException("Non è stato indicato il parser per i claims da utilizzare dopo la validazione tramite il servizio di Introspection");
			}
			TipologiaClaims tipologiaClaims = null;
			try {
				tipologiaClaims = TipologiaClaims.valueOf(parserType);
				if(tipologiaClaims==null) {
					throw new Exception("Sconosciuto");
				}
			}catch(Exception e) {
				throw new ProviderValidationException("È stato indicato un parser '"+parserType+"', per i claims da utilizzare dopo la validazione del servizio di Introspection, sconosciuto");
			}
			if(TipologiaClaims.CUSTOM.name().equals(parserType)) {
				String parserTypeCustomClass = pDefault.getProperty(Costanti.POLICY_INTROSPECTION_CLAIMS_PARSER_CLASS_NAME);
				if(parserTypeCustomClass==null || "".equals(parserTypeCustomClass)) {
					throw new ProviderValidationException("Non è stata fornita la classe del parser dei claims da utilizzare dopo la validazione del servizio di Introspection");
				}
				if(parserTypeCustomClass.contains(" ")) {
					throw new ProviderValidationException("Non indicare spazi nella classe del parser dei claims da utilizzare dopo la validazione del servizio di Introspection");
				}
			}
			
			boolean basic = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_INTROSPECTION_AUTH_BASIC_STATO);
			if(basic) {
				String username = pDefault.getProperty(Costanti.POLICY_INTROSPECTION_AUTH_BASIC_USERNAME);
				if(username==null || "".equals(username)) {
					throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'HttpBasic', non è stato fornito un username da utilizzare durante la connessione verso il servizio di Introspection");
				}
				if(username.contains(" ")) {
					throw new ProviderValidationException("Non indicare spazi nell'username da utilizzare durante la connessione verso il servizio di Introspection");
				}
				String password = pDefault.getProperty(Costanti.POLICY_INTROSPECTION_AUTH_BASIC_PASSWORD);
				if(password==null || "".equals(password)) {
					throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'HttpBasic', non è stato fornita una password da utilizzare durante la connessione verso il servizio di Introspection");
				}
				if(password.contains(" ")) {
					throw new ProviderValidationException("Non indicare spazi nella password da utilizzare durante la connessione verso il servizio di Introspection");
				}
			}
			
			boolean bearer = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_INTROSPECTION_AUTH_BEARER_STATO);
			if(bearer) {
				String token = pDefault.getProperty(Costanti.POLICY_INTROSPECTION_AUTH_BEARER_TOKEN);
				InputValidationUtils.validateTextAreaInput(token, "Token Introspection - Autenticazione Bearer - Token");
			}
			
			boolean ssl = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_INTROSPECTION_AUTH_SSL_STATO);
			if(ssl) {
				Properties p = mapProperties.get(Costanti.POLICY_ENDPOINT_SSL_CLIENT_CONFIG);
				if(p==null || p.size()<=0) {
					throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'Https', non sono stati forniti i parametri di connessione ssl client da utilizzare verso il servizio di Introspection");
				}
				
				String location = p.getProperty(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_LOCATION);
				if(location!=null && !"".equals(location)) {
					InputValidationUtils.validateTextAreaInput(location, "Https - Autenticazione Client - File (KeyStore per l'autenticazione client)");
				}
				
				String algo = p.getProperty(CostantiConnettori.CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITHM);
				if(algo==null || "".equals(algo)) {
					throw new ProviderValidationException("Indicare un algoritmo per l'autenticazione client");
				}
				if(algo.contains(" ")) {
					throw new ProviderValidationException("Non indicare spazi nell'algoritmo per l'autenticazione client");
				}
			}
		}
		
		if(userInfo) {
			String url = pDefault.getProperty(Costanti.POLICY_USER_INFO_URL);
			InputValidationUtils.validateTextAreaInput(url, "OIDC - UserInfo - URL");
			try{
				org.openspcoop2.utils.regexp.RegExpUtilities.validateUrl(pDefault.getProperty(Costanti.POLICY_USER_INFO_URL));
			}catch(Exception e){
				throw new ProviderValidationException("La URL fornita per il servizio 'OIDC UserInfo' non è valida: "+e.getMessage());
			}	
			
			String tipoTokenRequest = pDefault.getProperty(Costanti.POLICY_USER_INFO_REQUEST_TOKEN_POSITION);
			if(tipoTokenRequest==null) {
				throw new ProviderValidationException("Non è stato indicato il tipo di spedizione del token da inoltrare al servizio di UserInfo");
			}
			TipoTokenRequest tipoTokenRequestEnum = null;
			try {
				tipoTokenRequestEnum = TipoTokenRequest.valueOf(tipoTokenRequest);
				if(tipoTokenRequestEnum==null) {
					throw new Exception("Sconosciuto");
				}
			}catch(Exception e) {
				throw new ProviderValidationException("È stato indicato un tipo '"+tipoTokenRequest+"' di spedizione del token da inoltrare al servizio di UserInfo sconosciuto");
			}
			switch (tipoTokenRequestEnum) {
			case authorization:
				break;
			case header:
				String tmp = pDefault.getProperty(Costanti.POLICY_USER_INFO_REQUEST_TOKEN_POSITION_HEADER_NAME);
				if(tmp==null || "".equals(tmp)) {
					throw new ProviderValidationException("Non è stato indicato il nome dell'header http su cui inoltrare il token al servizio di UserInfo");
				}
				if(tmp.contains(" ")) {
					throw new ProviderValidationException("Non indicare spazi nel nome dell'header http su cui inoltrare il token al servizio di UserInfo");
				}
				break;
			case url:
				tmp = pDefault.getProperty(Costanti.POLICY_USER_INFO_REQUEST_TOKEN_POSITION_URL_PROPERTY_NAME);
				if(tmp==null || "".equals(tmp)) {
					throw new ProviderValidationException("Non è stato indicato il nome della proprietà della URL su cui inoltrare il token al servizio di UserInfo");
				}
				if(tmp.contains(" ")) {
					throw new ProviderValidationException("Non indicare spazi nel nome della proprietà della URL su cui inoltrare il token al servizio di UserInfo");
				}
				break;
			case form:
				tmp = pDefault.getProperty(Costanti.POLICY_USER_INFO_REQUEST_TOKEN_POSITION_FORM_PROPERTY_NAME);
				if(tmp==null || "".equals(tmp)) {
					throw new ProviderValidationException("Non è stato indicato il nome della proprietà della FORM su cui inoltrare il token al servizio di UserInfo");
				}
				if(tmp.contains(" ")) {
					throw new ProviderValidationException("Non indicare spazi nel nome della proprietà della FORM su cui inoltrare il token al servizio di UserInfo");
				}
				break;
			}
			
			String httpRequestMethod = pDefault.getProperty(Costanti.POLICY_USER_INFO_HTTP_METHOD);
			if(httpRequestMethod==null) {
				throw new ProviderValidationException("Non è stato indicato il tipo di richiesta HTTP da utilizzare per inoltrare il token al servizio di UserInfo");
			}
			HttpRequestMethod httpRequestMethodEnum = null;
			try {
				httpRequestMethodEnum = HttpRequestMethod.valueOf(httpRequestMethod);
				if(httpRequestMethodEnum==null) {
					throw new Exception("Sconosciuto");
				}
			}catch(Exception e) {
				throw new ProviderValidationException("È stato indicato un tipo '"+httpRequestMethod+"' di richiesta HTTP da utilizzare per inoltrare il token al servizio di UserInfo sconosciuto");
			}
			
			String contentType = pDefault.getProperty(Costanti.POLICY_USER_INFO_CONTENT_TYPE);
			if(!TipoTokenRequest.form.equals(tipoTokenRequestEnum)) {
				if(HttpRequestMethod.POST.equals(httpRequestMethodEnum) || 
						HttpRequestMethod.PUT.equals(httpRequestMethodEnum) || 
						HttpRequestMethod.PATCH.equals(httpRequestMethodEnum) || 
						HttpRequestMethod.LINK.equals(httpRequestMethodEnum) || 
						HttpRequestMethod.UNLINK.equals(httpRequestMethodEnum) ) {
					if(contentType==null || "".equals(contentType)) {
						throw new ProviderValidationException("Non è stato indicato il ContentType da utilizzare nella richiesta HTTP prodotta per inoltrare il token al servizio di UserInfo");
					}
					if(contentType.contains(" ")) {
						throw new ProviderValidationException("Non indicare spazi nel ContentType da utilizzare nella richiesta HTTP prodotta per inoltrare il token al servizio di UserInfo");
					}
				}
			}
			
			String parserType = pDefault.getProperty(Costanti.POLICY_USER_INFO_CLAIMS_PARSER_TYPE);
			if(parserType==null) {
				throw new ProviderValidationException("Non è stato indicato il parser per i claims da utilizzare dopo la validazione tramite il servizio di UserInfo");
			}
			TipologiaClaims tipologiaClaims = null;
			try {
				tipologiaClaims = TipologiaClaims.valueOf(parserType);
				if(tipologiaClaims==null) {
					throw new Exception("Sconosciuto");
				}
			}catch(Exception e) {
				throw new ProviderValidationException("È stato indicato un parser '"+parserType+"', per i claims da utilizzare dopo la validazione del servizio di UserInfo, sconosciuto");
			}
			if(TipologiaClaims.CUSTOM.name().equals(parserType)) {
				String parserTypeCustomClass = pDefault.getProperty(Costanti.POLICY_USER_INFO_CLAIMS_PARSER_CLASS_NAME);
				if(parserTypeCustomClass==null || "".equals(parserTypeCustomClass)) {
					throw new ProviderValidationException("Non è stata fornita la classe del parser dei claims da utilizzare dopo la validazione del servizio di UserInfo");
				}
				if(parserTypeCustomClass.contains(" ")) {
					throw new ProviderValidationException("Non indicare spazi nella classe del parser dei claims da utilizzare dopo la validazione del servizio di UserInfo");
				}
			}
			
			boolean basic = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_USER_INFO_AUTH_BASIC_STATO);
			if(basic) {
				String username = pDefault.getProperty(Costanti.POLICY_USER_INFO_AUTH_BASIC_USERNAME);
				if(username==null || "".equals(username)) {
					throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'HttpBasic', non è stato fornito un username da utilizzare durante la connessione verso il servizio di UserInfo");
				}
				if(username.contains(" ")) {
					throw new ProviderValidationException("Non indicare spazi nell'username da utilizzare durante la connessione verso il servizio di UserInfo");
				}
				String password = pDefault.getProperty(Costanti.POLICY_USER_INFO_AUTH_BASIC_PASSWORD);
				if(password==null || "".equals(password)) {
					throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'HttpBasic', non è stato fornita una password da utilizzare durante la connessione verso il servizio di UserInfo");
				}
				if(password.contains(" ")) {
					throw new ProviderValidationException("Non indicare spazi nella password da utilizzare durante la connessione verso il servizio di UserInfo");
				}
			}
			
			boolean bearer = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_USER_INFO_AUTH_BEARER_STATO);
			if(bearer) {
				String token = pDefault.getProperty(Costanti.POLICY_USER_INFO_AUTH_BEARER_TOKEN);
				InputValidationUtils.validateTextAreaInput(token, "OIDC - UserInfo - Autenticazione Bearer - Token");
			}
			
			boolean ssl = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_USER_INFO_AUTH_SSL_STATO);
			if(ssl) {
				Properties p = mapProperties.get(Costanti.POLICY_ENDPOINT_SSL_CLIENT_CONFIG);
				if(p==null || p.size()<=0) {
					throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'Https', non sono stati forniti i parametri di connessione ssl client da utilizzare verso il servizio di UserInfo");
				}
				
				String location = p.getProperty(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_LOCATION);
				if(location!=null && !"".equals(location)) {
					InputValidationUtils.validateTextAreaInput(location, "Https - Autenticazione Client - File (KeyStore per l'autenticazione client)");
				}
				
				String algo = p.getProperty(CostantiConnettori.CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITHM);
				if(algo==null || "".equals(algo)) {
					throw new ProviderValidationException("Indicare un algoritmo per l'autenticazione client");
				}
				if(algo.contains(" ")) {
					throw new ProviderValidationException("Non indicare spazi nell'algoritmo per l'autenticazione client");
				}
			}
		}
				
		if(forward) {
			
			boolean forwardTrasparente = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_STATO);
			boolean forwardInformazioniRaccolte = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_STATO);

			if(!forwardTrasparente && !forwardInformazioniRaccolte) {
				throw new ProviderValidationException("Almeno una modalità di forward del token deve essere selezionata");
			}
			
			if(forwardTrasparente) {
				String mode = pDefault.getProperty(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE);
				if(mode==null) {
					throw new ProviderValidationException("Nessuna modalità di forward trasparente indicata");
				}
				if(!Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_AS_RECEIVED.equals(mode) &&
						!Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_RFC6750_HEADER.equals(mode) &&
						!Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_RFC6750_URL.equals(mode) &&
						!Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_HEADER.equals(mode) &&
						!Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_URL.equals(mode)
						) {
					throw new ProviderValidationException("La modalità di forward trasparente indicata '"+mode+"' non è supportata");	
				}
				if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_HEADER.equals(mode)) {
					String hdr = pDefault.getProperty(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_HEADER_NAME);
					if(hdr==null || "".equals(hdr)) {
						throw new ProviderValidationException("La modalità di forward trasparente indicata prevede l'indicazione del nome di un header http");
					}
					if(hdr.contains(" ")) {
						throw new ProviderValidationException("Non indicare spazi nel nome dell'header HTTP indicato per la modalità di forward trasparente");
					}
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_URL.equals(mode)) {
					String url = pDefault.getProperty(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_URL_PARAMETER_NAME);
					if(url==null || "".equals(url)) {
						throw new ProviderValidationException("La modalità di forward trasparente indicata prevede l'indicazione del nome di una proprietà della url");
					}
					if(url.contains(" ")) {
						throw new ProviderValidationException("Non indicare spazi nel nome della proprietà della url indicata per la modalità di forward trasparente");
					}
				}
			}
			
			if(forwardInformazioniRaccolte) {
				String mode = pDefault.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE);
				if(mode==null) {
					throw new ProviderValidationException("Nessuna modalità di forward, delle informazioni raccolte, indicata");
				}
				if(!Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_HEADERS.equals(mode) &&
						!Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JSON.equals(mode) &&
						!Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JWS.equals(mode) &&
						!Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWS.equals(mode) &&
						!Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWE.equals(mode) &&
						!Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JSON.equals(mode) 
						) {
					throw new ProviderValidationException("La modalità di forward, delle informazioni raccolte, indicata '"+mode+"' non è supportata");	
				}
				if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JSON.equals(mode) ||
					Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JSON.equals(mode) 
						) {
					String base64 = pDefault.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_ENCODE_BASE64);
					if(base64==null) {
						throw new ProviderValidationException("Nessuna indicazione sull'encoding 'Base64' fornito; Tale indicazione è richiesta dalla modalità di forward, delle informazioni raccolte, indicata: "+mode);
					}
				}
				if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWS.equals(mode) ||
						Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWE.equals(mode) ||
						Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JSON.equals(mode) 
							) {
					boolean forwardValidazioneJWT = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_VALIDAZIONE_JWT);	
					boolean forwardIntrospection = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_INTROSPECTION);
					boolean forwardUserInfo = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_USER_INFO);
					if(!forwardValidazioneJWT && !forwardIntrospection && !forwardUserInfo) {
						throw new ProviderValidationException("Almeno una scelta tra 'Validazione JWT', 'Introspection' o 'OIDC UserInfo' deve essere selezionata per inoltrare le informazioni raccolte all'applicativo erogatore tramite la modalità scelta: "+mode);
					}
					
					if(forwardValidazioneJWT) {
						String modeForward = pDefault.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_VALIDAZIONE_JWT_MODE);
						if(modeForward==null) {
							throw new ProviderValidationException("Nessuna tipo di consegna (header/url), delle informazioni raccolte, indicata");
						}
						if(!Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_HEADER.equals(modeForward) &&
								!Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_URL.equals(modeForward) 
									) {
							throw new ProviderValidationException("(Validazione JWT) Tipo di consegna '"+modeForward+"', delle informazioni raccolte, indicata sconosciuta");
						}
						if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_HEADER.equals(modeForward)) {
							String name = pDefault.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_VALIDAZIONE_JWT_MODE_HEADER_NAME);
							if(name==null || "".equals(name)) {
								throw new ProviderValidationException("(Validazione JWT) Il tipo di consegna (header), delle informazioni raccolte, richiede la definizione di un nome di un header http");
							}
							if(name.contains(" ")) {
								throw new ProviderValidationException("Non indicare spazi nel nome dell'header http (Forward ValidazioneJWT-Header)");
							}
						}
						else if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_URL.equals(modeForward)) {
							String name = pDefault.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_VALIDAZIONE_JWT_MODE_URL_PARAMETER_NAME);
							if(name==null || "".equals(name)) {
								throw new ProviderValidationException("(Validazione JWT) Il tipo di consegna (url), delle informazioni raccolte, richiede la definizione di un nome di un parametro della URL");
							}
							if(name.contains(" ")) {
								throw new ProviderValidationException("Non indicare spazi nel nome del parametro della url (Forward ValidazioneJWT-ParametroURL)");
							}
						}
					}
					
					if(forwardIntrospection) {
						String modeForward = pDefault.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_INTROSPECTION_MODE);
						if(modeForward==null) {
							throw new ProviderValidationException("Nessuna tipo di consegna (header/url), delle informazioni raccolte, indicata");
						}
						if(!Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_HEADER.equals(modeForward) &&
								!Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_URL.equals(modeForward) 
									) {
							throw new ProviderValidationException("(Introspection) Tipo di consegna '"+modeForward+"', delle informazioni raccolte, indicata sconosciuta");
						}
						if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_HEADER.equals(modeForward)) {
							String name = pDefault.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_INTROSPECTION_MODE_HEADER_NAME);
							if(name==null || "".equals(name)) {
								throw new ProviderValidationException("(Introspection) Il tipo di consegna (header), delle informazioni raccolte, richiede la definizione di un nome di un header http");
							}
							if(name.contains(" ")) {
								throw new ProviderValidationException("Non indicare spazi nel nome dell'header http (Forward Introspection-Header)");
							}
						}
						else if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_URL.equals(modeForward)) {
							String name = pDefault.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_INTROSPECTION_MODE_URL_PARAMETER_NAME);
							if(name==null || "".equals(name)) {
								throw new ProviderValidationException("(Introspection) Il tipo di consegna (url), delle informazioni raccolte, richiede la definizione di un nome di un parametro della URL");
							}
							if(name.contains(" ")) {
								throw new ProviderValidationException("Non indicare spazi nel nome del parametro della url (Forward Introspection-ParametroURL)");
							}
						}
					}
					
					if(forwardUserInfo) {
						String modeForward = pDefault.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_USER_INFO_MODE);
						if(modeForward==null) {
							throw new ProviderValidationException("Nessuna tipo di consegna (header/url), delle informazioni raccolte, indicata");
						}
						if(!Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_HEADER.equals(modeForward) &&
								!Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_URL.equals(modeForward) 
									) {
							throw new ProviderValidationException("(UserInfo) Tipo di consegna '"+modeForward+"', delle informazioni raccolte, indicata sconosciuta");
						}
						if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_HEADER.equals(modeForward)) {
							String name = pDefault.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_USER_INFO_MODE_HEADER_NAME);
							if(name==null || "".equals(name)) {
								throw new ProviderValidationException("(UserInfo) Il tipo di consegna (header), delle informazioni raccolte, richiede la definizione di un nome di un header http");
							}
							if(name.contains(" ")) {
								throw new ProviderValidationException("Non indicare spazi nel nome dell'header http (Forward UserInfo-Header)");
							}
						}
						else if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_URL.equals(modeForward)) {
							String name = pDefault.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_USER_INFO_MODE_URL_PARAMETER_NAME);
							if(name==null || "".equals(name)) {
								throw new ProviderValidationException("(UserInfo) Il tipo di consegna (url), delle informazioni raccolte, richiede la definizione di un nome di un parametro della URL");
							}
							if(name.contains(" ")) {
								throw new ProviderValidationException("Non indicare spazi nel nome del parametro della url (Forward UserInfo-ParametroURL)");
							}
						}
					}
				}
				if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JWS.equals(mode) ||
						Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWS.equals(mode) 
							) {
					Properties p = mapProperties.get(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_SIGNATURE_PROP_REF_ID);
					if(p==null || p.size()<=0) {
						throw new ProviderValidationException("La modalità di forward, delle informazioni raccolte, selezionata richiede una configurazione per poter attuare la firma JWS; configurazione non riscontrata");
					}
					
					if(!p.containsKey("rs.security.keystore") && !p.containsKey("rs.security.keystore.jwkset")) {
						// altrimenti è stato fatto inject del keystore
						String file = p.getProperty("rs.security.keystore.file");
						InputValidationUtils.validateTextAreaInput(file, "Token Forward - JWS KeyStore - File");
					}
				}
				if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWE.equals(mode) 
							) {
					Properties p = mapProperties.get(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_ENCRYP_PROP_REF_ID);
					if(p==null || p.size()<=0) {
						throw new ProviderValidationException("La modalità di forward, delle informazioni raccolte, selezionata richiede una configurazione per poter attuare la cifratura JWE; configurazione non riscontrata");
					}
					
					if(!p.containsKey("rs.security.keystore") && !p.containsKey("rs.security.keystore.jwkset")) {
						// altrimenti è stato fatto inject del keystore
						String file = p.getProperty("rs.security.keystore.file");
						InputValidationUtils.validateTextAreaInput(file, "Token Forward - JWE KeyStore - File");
					}
				}
				
			}
			
		}
	}

	@Override
	public List<String> getValues(String id) throws ProviderException {
		if(Costanti.ID_INTROSPECTION_HTTP_METHOD.equals(id) ||
				Costanti.ID_USER_INFO_HTTP_METHOD.equals(id)) {
			List<String> methodsList = new ArrayList<>();
			HttpRequestMethod [] methods = HttpRequestMethod.values();
			for (int i = 0; i < methods.length; i++) {
				methodsList.add(methods[i].name());
			}
			return methodsList;
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
		else if(JOSECostanti.ID_ENCRYPT_KEY_ALGORITHM.equals(id) ||
				JOSECostanti.ID_ENCRYPT_CONTENT_ALGORITHM.equals(id) ||
				Costanti.ID_JWS_SIGNATURE_ALGORITHM.equals(id) ||
				Costanti.ID_JWS_ENCRYPT_KEY_ALGORITHM.equals(id) ||
				Costanti.ID_JWS_ENCRYPT_CONTENT_ALGORITHM.equals(id)) {
			SecurityProvider secProvider = new SecurityProvider();
			if(Costanti.ID_JWS_SIGNATURE_ALGORITHM.equals(id)) {
				return secProvider.getValues(JOSECostanti.ID_SIGNATURE_ALGORITHM);
			}
			else if(Costanti.ID_JWS_ENCRYPT_KEY_ALGORITHM.equals(id) || JOSECostanti.ID_ENCRYPT_KEY_ALGORITHM.equals(id)) {
				return secProvider.getValues(JOSECostanti.ID_ENCRYPT_KEY_ALGORITHM);
			}
			else if(Costanti.ID_JWS_ENCRYPT_CONTENT_ALGORITHM.equals(id) || JOSECostanti.ID_ENCRYPT_CONTENT_ALGORITHM.equals(id)) {
				return secProvider.getValues(JOSECostanti.ID_ENCRYPT_CONTENT_ALGORITHM);
			}
		}
		else if(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE.equals(id) || 
				Costanti.ID_VALIDAZIONE_JWT_KEYSTORE_TYPE.equals(id) ||
				Costanti.ID_HTTPS_TRUSTSTORE_TYPE.equals(id) ||
				Costanti.ID_HTTPS_KEYSTORE_TYPE.equals(id)  ||
				Costanti.ID_TOKEN_FORWARD_JWS_KEYSTORE_TYPE.equals(id)  ||
				Costanti.ID_TOKEN_FORWARD_JWE_KEYSTORE_TYPE.equals(id)) {
			return getStoreType(id,true);
		}
		return null;
	}

	@Override
	public List<String> getLabels(String id) throws ProviderException {
		if(JOSECostanti.ID_ENCRYPT_KEY_ALGORITHM.equals(id) ||
				JOSECostanti.ID_ENCRYPT_CONTENT_ALGORITHM.equals(id) ||
				Costanti.ID_JWS_SIGNATURE_ALGORITHM.equals(id) ||
				Costanti.ID_JWS_ENCRYPT_KEY_ALGORITHM.equals(id) ||
				Costanti.ID_JWS_ENCRYPT_CONTENT_ALGORITHM.equals(id)) {
			SecurityProvider secProvider = new SecurityProvider();
			if(Costanti.ID_JWS_SIGNATURE_ALGORITHM.equals(id)) {
				return secProvider.getLabels(JOSECostanti.ID_SIGNATURE_ALGORITHM);
			}
			else if(Costanti.ID_JWS_ENCRYPT_KEY_ALGORITHM.equals(id) || JOSECostanti.ID_ENCRYPT_KEY_ALGORITHM.equals(id)) {
				return secProvider.getLabels(JOSECostanti.ID_ENCRYPT_KEY_ALGORITHM);
			}
			else if(Costanti.ID_JWS_ENCRYPT_CONTENT_ALGORITHM.equals(id) || JOSECostanti.ID_ENCRYPT_CONTENT_ALGORITHM.equals(id)) {
				return secProvider.getLabels(JOSECostanti.ID_ENCRYPT_CONTENT_ALGORITHM);
			}
		}
		else if(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE.equals(id) || 
				Costanti.ID_VALIDAZIONE_JWT_KEYSTORE_TYPE.equals(id) ||
				Costanti.ID_HTTPS_TRUSTSTORE_TYPE.equals(id) ||
				Costanti.ID_HTTPS_KEYSTORE_TYPE.equals(id) ||
				Costanti.ID_TOKEN_FORWARD_JWS_KEYSTORE_TYPE.equals(id) ||
				Costanti.ID_TOKEN_FORWARD_JWE_KEYSTORE_TYPE.equals(id)) {
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
		if(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE.equals(id) || 
				Costanti.ID_VALIDAZIONE_JWT_KEYSTORE_TYPE.equals(id) ||
				Costanti.ID_TOKEN_FORWARD_JWS_KEYSTORE_TYPE.equals(id) ||
				Costanti.ID_TOKEN_FORWARD_JWE_KEYSTORE_TYPE.equals(id)) {
			l.add(value ? SecurityConstants.KEYSTORE_TYPE_JWK_VALUE: SecurityConstants.KEYSTORE_TYPE_JWK_LABEL);
		}
		if(Costanti.ID_VALIDAZIONE_JWT_KEYSTORE_TYPE.equals(id) ||
				Costanti.ID_TOKEN_FORWARD_JWE_KEYSTORE_TYPE.equals(id)) {
			l.add(value ? SecurityConstants.KEYSTORE_TYPE_JCEKS_VALUE : SecurityConstants.KEYSTORE_TYPE_JCEKS_LABEL);
			secret = true;
		}
		
		if(Costanti.ID_VALIDAZIONE_JWT_KEYSTORE_TYPE.equals(id) ||
				Costanti.ID_HTTPS_KEYSTORE_TYPE.equals(id) ||
				Costanti.ID_TOKEN_FORWARD_JWS_KEYSTORE_TYPE.equals(id) ||
				Costanti.ID_TOKEN_FORWARD_JWE_KEYSTORE_TYPE.equals(id)) {
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
		if(Costanti.ID_INTROSPECTION_HTTP_METHOD.equals(id) ||
				Costanti.ID_USER_INFO_HTTP_METHOD.equals(id)) {
			return HttpRequestMethod.GET.name();
		}
		else if(Costanti.ID_TIPOLOGIA_HTTPS.equals(id)) {
			return SSLUtilities.getSafeDefaultProtocol();
		}
		else if(JOSECostanti.ID_ENCRYPT_KEY_ALGORITHM.equals(id) ||
				JOSECostanti.ID_ENCRYPT_CONTENT_ALGORITHM.equals(id) ||
				Costanti.ID_JWS_SIGNATURE_ALGORITHM.equals(id) ||
				Costanti.ID_JWS_ENCRYPT_KEY_ALGORITHM.equals(id) ||
				Costanti.ID_JWS_ENCRYPT_CONTENT_ALGORITHM.equals(id)) {
			SecurityProvider secProvider = new SecurityProvider();
			if(Costanti.ID_JWS_SIGNATURE_ALGORITHM.equals(id)) {
				return secProvider.getDefault(JOSECostanti.ID_SIGNATURE_ALGORITHM);
			}
			else if(Costanti.ID_JWS_ENCRYPT_KEY_ALGORITHM.equals(id) || JOSECostanti.ID_ENCRYPT_KEY_ALGORITHM.equals(id)) {
				return secProvider.getDefault(JOSECostanti.ID_ENCRYPT_KEY_ALGORITHM);
			}
			else if(Costanti.ID_JWS_ENCRYPT_CONTENT_ALGORITHM.equals(id) || JOSECostanti.ID_ENCRYPT_CONTENT_ALGORITHM.equals(id)) {
				return secProvider.getDefault(JOSECostanti.ID_ENCRYPT_CONTENT_ALGORITHM);
			}
		}
		return null;
	}

	@Override
	public String dynamicUpdate(List<?> items, Map<String, String> mapNameValue, Item item, String actualValue) {
	
		if(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_FILE.equals(item.getName()) ||
				Costanti.ID_VALIDAZIONE_JWT_KEYSTORE_FILE.equals(item.getName()) ||
				Costanti.ID_HTTPS_TRUSTSTORE_FILE.equals(item.getName()) ||
				Costanti.ID_HTTPS_KEYSTORE_FILE.equals(item.getName()) ||
				Costanti.ID_TOKEN_FORWARD_JWS_KEYSTORE_FILE.equals(item.getName()) ||
				Costanti.ID_TOKEN_FORWARD_JWE_KEYSTORE_FILE.equals(item.getName())) {
			
			String type = Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE;
			if(Costanti.ID_VALIDAZIONE_JWT_KEYSTORE_FILE.equals(item.getName())) {
				type = Costanti.ID_VALIDAZIONE_JWT_KEYSTORE_TYPE;
			}
			else if(Costanti.ID_HTTPS_TRUSTSTORE_FILE.equals(item.getName())) {
				type = Costanti.ID_HTTPS_TRUSTSTORE_TYPE;
			}
			else if(Costanti.ID_HTTPS_KEYSTORE_FILE.equals(item.getName())) {
				type = Costanti.ID_HTTPS_KEYSTORE_TYPE;
			}
			else if(Costanti.ID_TOKEN_FORWARD_JWS_KEYSTORE_FILE.equals(item.getName())) {
				type = Costanti.ID_TOKEN_FORWARD_JWS_KEYSTORE_TYPE;
			}
			else if(Costanti.ID_TOKEN_FORWARD_JWE_KEYSTORE_FILE.equals(item.getName())) {
				type = Costanti.ID_TOKEN_FORWARD_JWE_KEYSTORE_TYPE;
			}
			
			return AbstractSecurityProvider.processStoreFile(type, items, mapNameValue, item, actualValue);
		}
		else if(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_PASSWORD.equals(item.getName()) ||
				Costanti.ID_VALIDAZIONE_JWT_KEYSTORE_PASSWORD.equals(item.getName()) ||
				Costanti.ID_HTTPS_TRUSTSTORE_PASSWORD.equals(item.getName()) ||
				Costanti.ID_HTTPS_KEYSTORE_PASSWORD.equals(item.getName()) ||
				Costanti.ID_TOKEN_FORWARD_JWS_KEYSTORE_PASSWORD.equals(item.getName()) ||
				Costanti.ID_TOKEN_FORWARD_JWE_KEYSTORE_PASSWORD.equals(item.getName())) {
			
			String type = Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE;
			if(Costanti.ID_VALIDAZIONE_JWT_KEYSTORE_PASSWORD.equals(item.getName())) {
				type = Costanti.ID_VALIDAZIONE_JWT_KEYSTORE_TYPE;
			}
			else if(Costanti.ID_HTTPS_TRUSTSTORE_PASSWORD.equals(item.getName())) {
				type = Costanti.ID_HTTPS_TRUSTSTORE_TYPE;
			}
			else if(Costanti.ID_HTTPS_KEYSTORE_PASSWORD.equals(item.getName())) {
				type = Costanti.ID_HTTPS_KEYSTORE_TYPE;
			}
			else if(Costanti.ID_TOKEN_FORWARD_JWS_KEYSTORE_PASSWORD.equals(item.getName())) {
				type = Costanti.ID_TOKEN_FORWARD_JWS_KEYSTORE_TYPE;
			}
			else if(Costanti.ID_TOKEN_FORWARD_JWE_KEYSTORE_PASSWORD.equals(item.getName())) {
				type = Costanti.ID_TOKEN_FORWARD_JWE_KEYSTORE_TYPE;
			}
			
			return AbstractSecurityProvider.processStorePassword(type, items, mapNameValue, item, actualValue);
		}
		else if(Costanti.ID_VALIDAZIONE_JWT_KEYSTORE_PASSWORD_PRIVATE_KEY.equals(item.getName()) ||
				Costanti.ID_HTTPS_KEYSTORE_PASSWORD_PRIVATE_KEY.equals(item.getName()) ||
				Costanti.ID_TOKEN_FORWARD_JWS_KEYSTORE_PASSWORD_PRIVATE_KEY.equals(item.getName()) ||
				Costanti.ID_TOKEN_FORWARD_JWE_KEYSTORE_PASSWORD_PRIVATE_KEY.equals(item.getName())) {
			
			String type = Costanti.ID_VALIDAZIONE_JWT_KEYSTORE_TYPE;
			if(Costanti.ID_HTTPS_KEYSTORE_PASSWORD_PRIVATE_KEY.equals(item.getName())) {
				type = Costanti.ID_HTTPS_KEYSTORE_TYPE;
			}
			else if(Costanti.ID_TOKEN_FORWARD_JWS_KEYSTORE_PASSWORD_PRIVATE_KEY.equals(item.getName())) {
				type = Costanti.ID_TOKEN_FORWARD_JWS_KEYSTORE_TYPE;
			}
			else if(Costanti.ID_TOKEN_FORWARD_JWE_KEYSTORE_PASSWORD_PRIVATE_KEY.equals(item.getName())) {
				type = Costanti.ID_TOKEN_FORWARD_JWE_KEYSTORE_TYPE;
			}
			
			return AbstractSecurityProvider.processStoreKeyPassword(type, items, mapNameValue, item, actualValue);
		}
		
		return actualValue;
	}
	
	
}
