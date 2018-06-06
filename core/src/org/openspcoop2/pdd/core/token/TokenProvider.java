package org.openspcoop2.pdd.core.token;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.mvc.properties.provider.IProvider;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.pdd.core.token.parser.TipologiaClaims;
import org.openspcoop2.security.message.jose.JOSECostanti;
import org.openspcoop2.security.message.jose.SecurityProvider;
import org.openspcoop2.utils.transport.http.SSLUtilities;

public class TokenProvider implements IProvider {

	@Override
	public void validate(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		
		Properties pDefault = TokenUtilities.getDefaultProperties(mapProperties);
		
		
		// ***** TOKEN SOURCE *******
		
		String position = pDefault.getProperty(Costanti.POLICY_TOKEN_SOURCE);
		if(position==null) {
			throw new ProviderValidationException("Non e' stata indicata la posizione del token");
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
			if(headerName==null) {
				throw new ProviderValidationException("Non e' stata indicata il nome dell'header http che deve contenere il token");
			}
		}
		else if(Costanti.POLICY_TOKEN_SOURCE_CUSTOM_URL.equals(position)) {
			String pName = pDefault.getProperty(Costanti.POLICY_TOKEN_SOURCE_CUSTOM_URL_PROPERTY_NAME);
			if(pName==null) {
				throw new ProviderValidationException("Non e' stata indicata il nome della proprietà della URL che deve contenere il token");
			}
		}
		
		String tokenType = pDefault.getProperty(Costanti.POLICY_TOKEN_TYPE);
		if(tokenType==null) {
			throw new ProviderValidationException("Non e' stata indicato il tipo del token");
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
			}
			else if(Costanti.POLICY_TOKEN_TYPE_JWE.equals(tokenType)) {
				Properties p = mapProperties.get(Costanti.POLICY_VALIDAZIONE_JWE_DECRYPT_PROP_REF_ID);
				if(p==null || p.size()<=0) {
					throw new ProviderValidationException("Non è stata fornita una configurazione per effettuare la validazione JWE");
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
				throw new ProviderValidationException("E' stato indicato un parser '"+parserType+"', per i claims da utilizzare dopo la validazione JWT, sconosciuto");
			}
			if(TipologiaClaims.CUSTOM.equals(parserType)) {
				String parserTypeCustomClass = pDefault.getProperty(Costanti.POLICY_VALIDAZIONE_CLAIMS_PARSER_CLASS_NAME);
				if(parserTypeCustomClass==null) {
					throw new ProviderValidationException("Non è stata fornita la classe del parser dei claims personalizzato da utilizzare dopo la validazione JWT");
				}
			}
		}
		
		if(introspection) {
			
			String url = pDefault.getProperty(Costanti.POLICY_INTROSPECTION_URL);
			if(url==null) {
				throw new ProviderValidationException("Non e' stata fornita la url del servizio 'Introspection'");
			}
			try{
				org.openspcoop2.utils.regexp.RegExpUtilities.validateUrl(url);
			}catch(Exception e){
				throw new ProviderValidationException("La URL fornita per il servizio 'Introspection' non è valida: "+e.getMessage());
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
				throw new ProviderValidationException("E' stato indicato un parser '"+parserType+"', per i claims da utilizzare dopo la validazione del servizio di Introspection, sconosciuto");
			}
			if(TipologiaClaims.CUSTOM.equals(parserType)) {
				String parserTypeCustomClass = pDefault.getProperty(Costanti.POLICY_INTROSPECTION_CLAIMS_PARSER_CLASS_NAME);
				if(parserTypeCustomClass==null) {
					throw new ProviderValidationException("Non è stata fornita la classe del parser dei claims personalizzato da utilizzare dopo la validazione del servizio di Introspection");
				}
			}
		}
		
		if(userInfo) {
			String url = pDefault.getProperty(Costanti.POLICY_USER_INFO_URL);
			if(url==null) {
				throw new ProviderValidationException("Non e' stata fornita la url del servizio 'OIDC UserInfo'");
			}
			try{
				org.openspcoop2.utils.regexp.RegExpUtilities.validateUrl(pDefault.getProperty(Costanti.POLICY_USER_INFO_URL));
			}catch(Exception e){
				throw new ProviderValidationException("La URL fornita per il servizio 'OIDC UserInfo' non è valida: "+e.getMessage());
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
				throw new ProviderValidationException("E' stato indicato un parser '"+parserType+"', per i claims da utilizzare dopo la validazione del servizio di UserInfo, sconosciuto");
			}
			if(TipologiaClaims.CUSTOM.equals(parserType)) {
				String parserTypeCustomClass = pDefault.getProperty(Costanti.POLICY_USER_INFO_CLAIMS_PARSER_CLASS_NAME);
				if(parserTypeCustomClass==null) {
					throw new ProviderValidationException("Non è stata fornita la classe del parser dei claims personalizzato da utilizzare dopo la validazione del servizio di UserInfo");
				}
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
