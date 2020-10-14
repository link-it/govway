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


package org.openspcoop2.pdd.core.token;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.mvc.properties.provider.IProvider;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.utils.transport.http.SSLUtilities;

/**     
 * GetTokenProvider
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NegoziazioneTokenProvider implements IProvider {

	@Override
	public void validate(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		
		Properties pDefault = TokenUtilities.getDefaultProperties(mapProperties);
			
		boolean endpointSSL = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_ENDPOINT_HTTPS_STATO);
		boolean ssl = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_RETRIEVE_TOKEN_AUTH_SSL_STATO);
		
		if(endpointSSL || ssl) {
			Properties p = mapProperties.get(Costanti.POLICY_ENDPOINT_SSL_CONFIG);
			if(p==null || p.size()<=0) {
				throw new ProviderValidationException("Nonostante sia stato indicato un endpoint 'https', non è stata fornita una configurazione dei parametri ssl da utilizzare");
			}
			
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
		
		String url = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_URL);
		if(url==null || "".equals(url)) {
			throw new ProviderValidationException("Non e' stata fornita la url dove reperire il token");
		}
		if(url.contains(" ")) {
			throw new ProviderValidationException("Non indicare spazi nella url");
		}
		try{
			org.openspcoop2.utils.regexp.RegExpUtilities.validateUrl(url);
		}catch(Exception e){
			throw new ProviderValidationException("La URL fornita non è valida: "+e.getMessage());
		}	
		
		String retMode = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_MODE);
		if(Costanti.ID_RETRIEVE_TOKEN_METHOD_USERNAME_PASSWORD.equals(retMode)) {
			String username = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_USERNAME);
			if(username==null || "".equals(username)) {
				throw new ProviderValidationException("Non è stato fornito l'username");
			}
			if(username.contains(" ")) {
				throw new ProviderValidationException("Non indicare spazi nel'username");
			}
			String password = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_PASSWORD);
			if(password==null || "".equals(password)) {
				throw new ProviderValidationException("Non è stato fornita una password");
			}
			if(password.contains(" ")) {
				throw new ProviderValidationException("Non indicare spazi nella password");
			}
		}
		
					
		boolean basic = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_RETRIEVE_TOKEN_AUTH_BASIC_STATO);
		if(basic) {
			String username = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_AUTH_BASIC_USERNAME);
			if(username==null || "".equals(username)) {
				throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'HttpBasic', non è stato fornito un 'Client ID' da utilizzare durante la connessione verso il servizio");
			}
			if(username.contains(" ")) {
				throw new ProviderValidationException("Non indicare spazi nel 'Client ID'");
			}
			String password = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_AUTH_BASIC_PASSWORD);
			if(password==null || "".equals(password)) {
				throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'HttpBasic', non è stato fornita un 'Client Secret' da utilizzare durante la connessione verso il servizio");
			}
			if(password.contains(" ")) {
				throw new ProviderValidationException("Non indicare spazi nel 'Client Secret'");
			}
		}
		
		boolean bearer = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_RETRIEVE_TOKEN_AUTH_BEARER_STATO);
		if(bearer) {
			String token = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_AUTH_BEARER_TOKEN);
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
		
		String scopes = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_SCOPES);
		if(scopes!=null && !"".equals(scopes)) {
			if(scopes.contains(" ")) {
				throw new ProviderValidationException("Non indicare spazi tra gli scope forniti");
			}
		}
		
		String audience = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_AUDIENCE);
		if(audience!=null && !"".equals(audience)) {
			if(audience.contains(" ")) {
				throw new ProviderValidationException("Non indicare spazi nel valore dell'audience");
			}
		}
		
		String mode = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE);
		if(mode==null) {
			throw new ProviderValidationException("Nessuna modalità di forward indicata");
		}
		if(!Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE_RFC6750_HEADER.equals(mode) &&
				!Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE_RFC6750_URL.equals(mode) &&
				!Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE_CUSTOM_HEADER.equals(mode) &&
				!Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE_CUSTOM_URL.equals(mode)
				) {
			throw new ProviderValidationException("La modalità di forward indicata '"+mode+"' non è supportata");	
		}
		if(Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE_CUSTOM_HEADER.equals(mode)) {
			String hdr = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE_CUSTOM_HEADER_NAME);
			if(hdr==null || "".equals(hdr)) {
				throw new ProviderValidationException("La modalità di forward indicata prevede l'indicazione del nome di un header http");
			}
			if(hdr.contains(" ")) {
				throw new ProviderValidationException("Non indicare spazi nel nome dell'header HTTP indicato per la modalità di forward");
			}
		}
		else if(Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE_CUSTOM_URL.equals(mode)) {
			String urlP = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_FORWARD_MODE_CUSTOM_URL_PARAMETER_NAME);
			if(urlP==null || "".equals(urlP)) {
				throw new ProviderValidationException("La modalità di forward indicata prevede l'indicazione del nome di una proprietà della url");
			}
			if(urlP.contains(" ")) {
				throw new ProviderValidationException("Non indicare spazi nel nome della proprietà della url indicata per la modalità di forward");
			}
		}
		
	}

	@Override
	public List<String> getValues(String id) throws ProviderException {
		if(Costanti.ID_RETRIEVE_TOKEN_METHOD.equals(id)) {
			List<String> methodsList = new ArrayList<>();
			methodsList.add(Costanti.ID_RETRIEVE_TOKEN_METHOD_CLIENT_CREDENTIAL);
			methodsList.add(Costanti.ID_RETRIEVE_TOKEN_METHOD_USERNAME_PASSWORD);
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
		return null;
	}

	@Override
	public List<String> getLabels(String id) throws ProviderException {
		if(Costanti.ID_RETRIEVE_TOKEN_METHOD.equals(id)) {
			List<String> methodsList = new ArrayList<>();
			methodsList.add(Costanti.ID_RETRIEVE_TOKEN_METHOD_CLIENT_CREDENTIAL_LABEL);
			methodsList.add(Costanti.ID_RETRIEVE_TOKEN_METHOD_USERNAME_PASSWORD_LABEL);
			return methodsList;
		}
		return this.getValues(id); // torno uguale ai valori negli altri casi
	}

	@Override
	public String getDefault(String id) throws ProviderException {
		if(Costanti.ID_RETRIEVE_TOKEN_METHOD.equals(id)) {
			return Costanti.ID_RETRIEVE_TOKEN_METHOD_CLIENT_CREDENTIAL;
		}
		else if(Costanti.ID_TIPOLOGIA_HTTPS.equals(id)) {
			return SSLUtilities.getSafeDefaultProtocol();
		}
		return null;
	}

}
