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
		if(endpointSSL) {
			Properties p = mapProperties.get(Costanti.POLICY_ENDPOINT_SSL_CONFIG);
			if(p==null || p.size()<=0) {
				throw new ProviderValidationException("Nonostante sia stato indicato un endpoint 'https', non è stata fornita una configurazione dei parametri ssl da utilizzare");
			}
		}
		
		String url = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_URL);
		if(url==null) {
			throw new ProviderValidationException("Non e' stata fornita la url dove reperire il token");
		}
		try{
			org.openspcoop2.utils.regexp.RegExpUtilities.validateUrl(url);
		}catch(Exception e){
			throw new ProviderValidationException("La URL fornita non è valida: "+e.getMessage());
		}	
					
		boolean basic = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_RETRIEVE_TOKEN_AUTH_BASIC_STATO);
		if(basic) {
			String username = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_AUTH_BASIC_USERNAME);
			if(username==null) {
				throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'HttpBasic', non è stato fornito un 'Client ID' da utilizzare durante la connessione verso il servizio");
			}
			String password = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_AUTH_BASIC_PASSWORD);
			if(password==null) {
				throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'HttpBasic', non è stato fornita un 'Client Secret' da utilizzare durante la connessione verso il servizio");
			}
		}
		
		boolean bearer = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_RETRIEVE_TOKEN_AUTH_BEARER_STATO);
		if(bearer) {
			String token = pDefault.getProperty(Costanti.POLICY_RETRIEVE_TOKEN_AUTH_BEARER_TOKEN);
			if(token==null) {
				throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'Authorization Bearer', non è stato fornito un token di autenticazione da inoltrare al servizio");
			}
		}
		
		boolean ssl = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_RETRIEVE_TOKEN_AUTH_SSL_STATO);
		if(ssl) {
			Properties p = mapProperties.get(Costanti.POLICY_ENDPOINT_SSL_CLIENT_CONFIG);
			if(p==null || p.size()<=0) {
				throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'Https', non sono stati forniti i parametri di connessione ssl client da utilizzare verso il servizio");
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
