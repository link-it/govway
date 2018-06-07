package org.openspcoop2.pdd.core.token;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.mvc.properties.provider.IProvider;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.pdd.core.token.parser.TipologiaClaims;
import org.openspcoop2.security.message.jose.JOSECostanti;
import org.openspcoop2.security.message.jose.SecurityProvider;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
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
		
		boolean endpointSSL = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_ENDPOINT_HTTPS_STATO);
		if(endpointSSL) {
			Properties p = mapProperties.get(Costanti.POLICY_ENDPOINT_SSL_CONFIG);
			if(p==null || p.size()<=0) {
				throw new ProviderValidationException("Nonostante sia stato indicato un endpoint 'https', non è stata fornita una configurazione dei parametri ssl da utilizzare");
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
				throw new ProviderValidationException("E' stato indicato un tipo '"+tipoTokenRequest+"' di spedizione del token da inoltrare al servizio di Introspection sconosciuto");
			}
			switch (tipoTokenRequestEnum) {
			case authorization:
				break;
			case header:
				String tmp = pDefault.getProperty(Costanti.POLICY_INTROSPECTION_REQUEST_TOKEN_POSITION_HEADER_NAME);
				if(tmp==null) {
					throw new ProviderValidationException("Non è stato indicato il nome dell'header http su cui inoltrare il token al servizio di Introspection");
				}
				break;
			case url:
				tmp = pDefault.getProperty(Costanti.POLICY_INTROSPECTION_REQUEST_TOKEN_POSITION_URL_PROPERTY_NAME);
				if(tmp==null) {
					throw new ProviderValidationException("Non è stato indicato il nome della proprietà della URL su cui inoltrare il token al servizio di Introspection");
				}
				break;
			case form:
				tmp = pDefault.getProperty(Costanti.POLICY_INTROSPECTION_REQUEST_TOKEN_POSITION_URL_PROPERTY_NAME);
				if(tmp==null) {
					throw new ProviderValidationException("Non è stato indicato il nome della proprietà della FORM su cui inoltrare il token al servizio di Introspection");
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
				throw new ProviderValidationException("E' stato indicato un tipo '"+httpRequestMethod+"' di richiesta HTTP da utilizzare per inoltrare il token al servizio di Introspection sconosciuto");
			}
			
			String contentType = pDefault.getProperty(Costanti.POLICY_INTROSPECTION_CONTENT_TYPE);
			if(!TipoTokenRequest.form.equals(tipoTokenRequestEnum)) {
				if(HttpRequestMethod.POST.equals(httpRequestMethodEnum) || 
						HttpRequestMethod.PUT.equals(httpRequestMethodEnum) || 
						HttpRequestMethod.PATCH.equals(httpRequestMethodEnum) || 
						HttpRequestMethod.LINK.equals(httpRequestMethodEnum) || 
						HttpRequestMethod.UNLINK.equals(httpRequestMethodEnum) ) {
					if(contentType==null) {
						throw new ProviderValidationException("Non è stato indicato il ContentType da utilizzare nella richiesta HTTP prodotta per inoltrare il token al servizio di Introspection");
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
				throw new ProviderValidationException("E' stato indicato un parser '"+parserType+"', per i claims da utilizzare dopo la validazione del servizio di Introspection, sconosciuto");
			}
			if(TipologiaClaims.CUSTOM.equals(parserType)) {
				String parserTypeCustomClass = pDefault.getProperty(Costanti.POLICY_INTROSPECTION_CLAIMS_PARSER_CLASS_NAME);
				if(parserTypeCustomClass==null) {
					throw new ProviderValidationException("Non è stata fornita la classe del parser dei claims personalizzato da utilizzare dopo la validazione del servizio di Introspection");
				}
			}
			
			boolean basic = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_INTROSPECTION_AUTH_BASIC_STATO);
			if(basic) {
				String username = pDefault.getProperty(Costanti.POLICY_INTROSPECTION_AUTH_BASIC_USERNAME);
				if(username==null) {
					throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'HttpBasic', non è stato fornito un username da utilizzare durante la connessione verso il servizio di Introspection");
				}
				String password = pDefault.getProperty(Costanti.POLICY_INTROSPECTION_AUTH_BASIC_PASSWORD);
				if(password==null) {
					throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'HttpBasic', non è stato fornita una password da utilizzare durante la connessione verso il servizio di Introspection");
				}
			}
			
			boolean bearer = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_INTROSPECTION_AUTH_BEARER_STATO);
			if(bearer) {
				String token = pDefault.getProperty(Costanti.POLICY_INTROSPECTION_AUTH_BEARER_TOKEN);
				if(token==null) {
					throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'Authorization Bearer', non è stato fornito un token di autenticazione da inoltrare al servizio di Introspection");
				}
			}
			
			boolean ssl = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_INTROSPECTION_AUTH_SSL_STATO);
			if(ssl) {
				Properties p = mapProperties.get(Costanti.POLICY_ENDPOINT_SSL_CLIENT_CONFIG);
				if(p==null || p.size()<=0) {
					throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'Https', non sono stati forniti i parametri di connessione ssl client da utilizzare verso il servizio di Introspection");
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
				throw new ProviderValidationException("E' stato indicato un tipo '"+tipoTokenRequest+"' di spedizione del token da inoltrare al servizio di UserInfo sconosciuto");
			}
			switch (tipoTokenRequestEnum) {
			case authorization:
				break;
			case header:
				String tmp = pDefault.getProperty(Costanti.POLICY_USER_INFO_REQUEST_TOKEN_POSITION_HEADER_NAME);
				if(tmp==null) {
					throw new ProviderValidationException("Non è stato indicato il nome dell'header http su cui inoltrare il token al servizio di UserInfo");
				}
				break;
			case url:
				tmp = pDefault.getProperty(Costanti.POLICY_USER_INFO_REQUEST_TOKEN_POSITION_URL_PROPERTY_NAME);
				if(tmp==null) {
					throw new ProviderValidationException("Non è stato indicato il nome della proprietà della URL su cui inoltrare il token al servizio di UserInfo");
				}
				break;
			case form:
				tmp = pDefault.getProperty(Costanti.POLICY_USER_INFO_REQUEST_TOKEN_POSITION_URL_PROPERTY_NAME);
				if(tmp==null) {
					throw new ProviderValidationException("Non è stato indicato il nome della proprietà della FORM su cui inoltrare il token al servizio di UserInfo");
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
				throw new ProviderValidationException("E' stato indicato un tipo '"+httpRequestMethod+"' di richiesta HTTP da utilizzare per inoltrare il token al servizio di UserInfo sconosciuto");
			}
			
			String contentType = pDefault.getProperty(Costanti.POLICY_USER_INFO_CONTENT_TYPE);
			if(!TipoTokenRequest.form.equals(tipoTokenRequestEnum)) {
				if(HttpRequestMethod.POST.equals(httpRequestMethodEnum) || 
						HttpRequestMethod.PUT.equals(httpRequestMethodEnum) || 
						HttpRequestMethod.PATCH.equals(httpRequestMethodEnum) || 
						HttpRequestMethod.LINK.equals(httpRequestMethodEnum) || 
						HttpRequestMethod.UNLINK.equals(httpRequestMethodEnum) ) {
					if(contentType==null) {
						throw new ProviderValidationException("Non è stato indicato il ContentType da utilizzare nella richiesta HTTP prodotta per inoltrare il token al servizio di UserInfo");
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
				throw new ProviderValidationException("E' stato indicato un parser '"+parserType+"', per i claims da utilizzare dopo la validazione del servizio di UserInfo, sconosciuto");
			}
			if(TipologiaClaims.CUSTOM.equals(parserType)) {
				String parserTypeCustomClass = pDefault.getProperty(Costanti.POLICY_USER_INFO_CLAIMS_PARSER_CLASS_NAME);
				if(parserTypeCustomClass==null) {
					throw new ProviderValidationException("Non è stata fornita la classe del parser dei claims personalizzato da utilizzare dopo la validazione del servizio di UserInfo");
				}
			}
			
			boolean basic = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_USER_INFO_AUTH_BASIC_STATO);
			if(basic) {
				String username = pDefault.getProperty(Costanti.POLICY_USER_INFO_AUTH_BASIC_USERNAME);
				if(username==null) {
					throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'HttpBasic', non è stato fornito un username da utilizzare durante la connessione verso il servizio di UserInfo");
				}
				String password = pDefault.getProperty(Costanti.POLICY_USER_INFO_AUTH_BASIC_PASSWORD);
				if(password==null) {
					throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'HttpBasic', non è stato fornita una password da utilizzare durante la connessione verso il servizio di UserInfo");
				}
			}
			
			boolean bearer = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_USER_INFO_AUTH_BEARER_STATO);
			if(bearer) {
				String token = pDefault.getProperty(Costanti.POLICY_USER_INFO_AUTH_BEARER_TOKEN);
				if(token==null) {
					throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'Authorization Bearer', non è stato fornito un token di autenticazione da inoltrare al servizio di UserInfo");
				}
			}
			
			boolean ssl = TokenUtilities.isEnabled(pDefault, Costanti.POLICY_USER_INFO_AUTH_SSL_STATO);
			if(ssl) {
				Properties p = mapProperties.get(Costanti.POLICY_ENDPOINT_SSL_CLIENT_CONFIG);
				if(p==null || p.size()<=0) {
					throw new ProviderValidationException("Nonostante sia richiesta una autenticazione 'Https', non sono stati forniti i parametri di connessione ssl client da utilizzare verso il servizio di UserInfo");
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
					throw new ProviderValidationException("La modalità di forward trasparente indicata '"+mode+"' non e' supportata");	
				}
				if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_HEADER.equals(mode)) {
					String hdr = pDefault.getProperty(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_HEADER);
					if(hdr==null) {
						throw new ProviderValidationException("La modalità di forward trasparente indicata prevede l'indicazione del nome di un header http");
					}
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_URL.equals(mode)) {
					String url = pDefault.getProperty(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_URL);
					if(url==null) {
						throw new ProviderValidationException("La modalità di forward trasparente indicata prevede l'indicazione del nome di una proprietà della url");
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
					throw new ProviderValidationException("La modalità di forward, delle informazioni raccolte, indicata '"+mode+"' non e' supportata");	
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
							if(name==null) {
								throw new ProviderValidationException("(Validazione JWT) Il tipo di consegna (header), delle informazioni raccolte, richiede la definizione di un nome di un header http");
							}
						}
						else if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_URL.equals(modeForward)) {
							String name = pDefault.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_VALIDAZIONE_JWT_MODE_URL_PARAMETER_NAME);
							if(name==null) {
								throw new ProviderValidationException("(Validazione JWT) Il tipo di consegna (url), delle informazioni raccolte, richiede la definizione di un nome di un parametro della URL");
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
							if(name==null) {
								throw new ProviderValidationException("(Introspection) Il tipo di consegna (header), delle informazioni raccolte, richiede la definizione di un nome di un header http");
							}
						}
						else if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_URL.equals(modeForward)) {
							String name = pDefault.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_INTROSPECTION_MODE_URL_PARAMETER_NAME);
							if(name==null) {
								throw new ProviderValidationException("(Introspection) Il tipo di consegna (url), delle informazioni raccolte, richiede la definizione di un nome di un parametro della URL");
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
							throw new ProviderValidationException("(Introspection) Tipo di consegna '"+modeForward+"', delle informazioni raccolte, indicata sconosciuta");
						}
						if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_HEADER.equals(modeForward)) {
							String name = pDefault.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_USER_INFO_MODE_HEADER_NAME);
							if(name==null) {
								throw new ProviderValidationException("(Introspection) Il tipo di consegna (header), delle informazioni raccolte, richiede la definizione di un nome di un header http");
							}
						}
						else if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_URL.equals(modeForward)) {
							String name = pDefault.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_USER_INFO_MODE_URL_PARAMETER_NAME);
							if(name==null) {
								throw new ProviderValidationException("(Introspection) Il tipo di consegna (url), delle informazioni raccolte, richiede la definizione di un nome di un parametro della URL");
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
				}
				if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWE.equals(mode) 
							) {
					Properties p = mapProperties.get(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_ENCRYP_PROP_REF_ID);
					if(p==null || p.size()<=0) {
						throw new ProviderValidationException("La modalità di forward, delle informazioni raccolte, selezionata richiede una configurazione per poter attuare la cifratura JWE; configurazione non riscontrata");
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
		if(Costanti.ID_INTROSPECTION_HTTP_METHOD.equals(id) ||
				Costanti.ID_USER_INFO_HTTP_METHOD.equals(id)) {
			return HttpRequestMethod.GET.name();
		}
		else if(Costanti.ID_TIPOLOGIA_HTTPS.equals(id)) {
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
