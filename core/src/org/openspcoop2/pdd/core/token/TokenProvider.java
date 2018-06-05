package org.openspcoop2.pdd.core.token;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.mvc.properties.provider.IProvider;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.security.message.jose.JOSECostanti;
import org.openspcoop2.security.message.jose.SecurityProvider;
import org.openspcoop2.utils.transport.http.SSLUtilities;

public class TokenProvider implements IProvider {

	@Override
	public void validate(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		
		Properties pDefault = TokenUtilities.getDefaultProperties(mapProperties);
				
		boolean validazione = TokenUtilities.isValidazioneEnabled(pDefault);
		boolean introspection = TokenUtilities.isIntrospectionEnabled(pDefault);
		boolean userInfo = TokenUtilities.isUserInfoEnabled(pDefault);
		boolean forward = TokenUtilities.isTokenForwardEnabled(pDefault);
				
		if(!validazione &&  !introspection && !userInfo && !forward) {
			throw new ProviderValidationException("Almeno una modalità di elaborazione del token deve essere selezionata");
		}
		
		if(introspection) {
			try{
				org.openspcoop2.utils.regexp.RegExpUtilities.validateUrl(pDefault.getProperty(Costanti.POLICY_INTROSPECTION_URL));
			}catch(Exception e){
				throw new ProviderValidationException("La URL fornita per il servizio 'Introspection' non è valida: "+e.getMessage());
			}	
		}
		
		if(userInfo) {
			try{
				org.openspcoop2.utils.regexp.RegExpUtilities.validateUrl(pDefault.getProperty(Costanti.POLICY_USER_INFO_URL));
			}catch(Exception e){
				throw new ProviderValidationException("La URL fornita per il servizio 'OIDC UserInfo' non è valida: "+e.getMessage());
			}	
		}
				
		if(forward) {
			
			boolean forwardTrasparente = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_STATO);
			boolean forwardInformazioniRaccolte = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_STATO);

			if(!forwardTrasparente && !forwardInformazioniRaccolte) {
				throw new ProviderValidationException("Almeno una modalità di forward del token deve essere selezionata");
			}
			
			if(forwardInformazioniRaccolte) {
				boolean forwardIntrospection = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_INTROSPECTION);
				boolean forwardUserInfo = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_USER_INFO);
								
				if(!forwardIntrospection && !forwardUserInfo) {
					throw new ProviderValidationException("Almeno una scelta tra 'Introspection' o tramite 'OIDC UserInfo' deve essere selezionata per inoltrare le informazioni raccolte all'applicativo erogatore.");
				}
			}
		}
	}

	@Override
	public List<String> getValues(String id) throws ProviderException {
		if(Costanti.ID_TIPOLOGIA_HTTPS.equals(id)) {
			List<String> tipologie = null;
			try{
				tipologie = SSLUtilities.getSSLSupportedProtocols();
			}catch(Exception e){
				tipologie = SSLUtilities.getAllSslProtocol();
			}
			return tipologie;
		}
		else if(Costanti.ID_JWS_SIGNATURE_ALGORITHM.equals(id) ||
				Costanti.ID_JWS_ENCRYPT_KEY_ALGORITHM.equals(id) ||
				Costanti.ID_JWS_ENCRYPT_CONTENT_ALGORITHM.equals(id)) {
			SecurityProvider secProvider = new SecurityProvider();
			if(Costanti.ID_JWS_SIGNATURE_ALGORITHM.equals(id)) {
				return secProvider.getValues(JOSECostanti.ID_SIGNATURE_ALGORITHM);
			}
			else if(Costanti.ID_JWS_ENCRYPT_KEY_ALGORITHM.equals(id)) {
				return secProvider.getValues(JOSECostanti.ID_ENCRYPT_KEY_ALGORITHM);
			}
			else if(Costanti.ID_JWS_ENCRYPT_CONTENT_ALGORITHM.equals(id)) {
				return secProvider.getValues(JOSECostanti.ID_ENCRYPT_CONTENT_ALGORITHM);
			}
		}
		return null;
	}

	@Override
	public List<String> getLabels(String id) throws ProviderException {
		if(Costanti.ID_JWS_SIGNATURE_ALGORITHM.equals(id) ||
				Costanti.ID_JWS_ENCRYPT_KEY_ALGORITHM.equals(id) ||
				Costanti.ID_JWS_ENCRYPT_CONTENT_ALGORITHM.equals(id)) {
			SecurityProvider secProvider = new SecurityProvider();
			if(Costanti.ID_JWS_SIGNATURE_ALGORITHM.equals(id)) {
				return secProvider.getLabels(JOSECostanti.ID_SIGNATURE_ALGORITHM);
			}
			else if(Costanti.ID_JWS_ENCRYPT_KEY_ALGORITHM.equals(id)) {
				return secProvider.getLabels(JOSECostanti.ID_ENCRYPT_KEY_ALGORITHM);
			}
			else if(Costanti.ID_JWS_ENCRYPT_CONTENT_ALGORITHM.equals(id)) {
				return secProvider.getLabels(JOSECostanti.ID_ENCRYPT_CONTENT_ALGORITHM);
			}
		}
		return this.getValues(id); // torno uguale ai valori negli altri casi
	}

	@Override
	public String getDefault(String id) throws ProviderException {
		if(Costanti.ID_TIPOLOGIA_HTTPS.equals(id)) {
			return SSLUtilities.getSafeDefaultProtocol();
		}
		else if(Costanti.ID_JWS_SIGNATURE_ALGORITHM.equals(id) ||
				Costanti.ID_JWS_ENCRYPT_KEY_ALGORITHM.equals(id) ||
				Costanti.ID_JWS_ENCRYPT_CONTENT_ALGORITHM.equals(id)) {
			SecurityProvider secProvider = new SecurityProvider();
			if(Costanti.ID_JWS_SIGNATURE_ALGORITHM.equals(id)) {
				return secProvider.getDefault(JOSECostanti.ID_SIGNATURE_ALGORITHM);
			}
			else if(Costanti.ID_JWS_ENCRYPT_KEY_ALGORITHM.equals(id)) {
				return secProvider.getDefault(JOSECostanti.ID_ENCRYPT_KEY_ALGORITHM);
			}
			else if(Costanti.ID_JWS_ENCRYPT_CONTENT_ALGORITHM.equals(id)) {
				return secProvider.getDefault(JOSECostanti.ID_ENCRYPT_CONTENT_ALGORITHM);
			}
		}
		return null;
	}

}
