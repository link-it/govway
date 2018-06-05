package org.openspcoop2.pdd.core.token;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.message.ForcedResponseMessage;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.pdd.services.connector.FormUrlEncodedHttpServletRequest;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.utils.transport.http.HttpConstants;

public class GestoreToken {

	public static void validazioneConfigurazione(AbstractDatiInvocazione datiInvocazione) throws ProviderException, ProviderValidationException {
		TokenProvider p = new TokenProvider();
		p.validate(datiInvocazione.getPolicyGestioneToken().getProperties());
	}
	
	public static void verificaPosizioneToken(AbstractDatiInvocazione datiInvocazione, EsitoPresenzaToken esitoPresenzaToken) {
		
		esitoPresenzaToken.setPresente(false);
		try{
    		Properties properties = datiInvocazione.getPolicyGestioneToken().getDefaultProperties();
    		String source = properties.getProperty(Costanti.POLICY_TOKEN_SOURCE);
    		
    		String detailsError = null;
			String token = null;
			    		
    		if(Costanti.POLICY_TOKEN_SOURCE_RFC6750.equals(source) ||
    				Costanti.POLICY_TOKEN_SOURCE_RFC6750_HEADER.equals(source) ||
    				Costanti.POLICY_TOKEN_SOURCE_CUSTOM_HEADER.equals(source)) {
    			if(datiInvocazione.getInfoConnettoreIngresso()==null || 
    					datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext()==null) {
    				detailsError = "Informazioni di trasporto non presenti";
    			}
    			else {
    				URLProtocolContext urlProtocolContext = datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext();
    				if(urlProtocolContext.getParametersTrasporto()==null || urlProtocolContext.getParametersTrasporto().size()<=0) {
        				detailsError = "Header di trasporto non presenti";
        			}
    				if(Costanti.POLICY_TOKEN_SOURCE_RFC6750.equals(source) ||
    	    				Costanti.POLICY_TOKEN_SOURCE_RFC6750_HEADER.equals(source)) {
    					if(urlProtocolContext.getCredential()==null || urlProtocolContext.getCredential().getBearerToken()==null) {
    						if(urlProtocolContext.getCredential()!=null && urlProtocolContext.getCredential().getUsername()!=null) {
    							detailsError = "Riscontrato header http '"+HttpConstants.AUTHORIZATION+"' valorizzato tramite autenticazione '"+HttpConstants.AUTHORIZATION_PREFIX_BASIC+
    									"'; la configurazione richiede invece la presenza di un token valorizzato tramite autenticazione '"+HttpConstants.AUTHORIZATION_PREFIX_BEARER+"' ";
    						}
    						else {
    							detailsError = "Non è stato riscontrato un header http '"+HttpConstants.AUTHORIZATION+"' valorizzato tramite autenticazione '"+HttpConstants.AUTHORIZATION_PREFIX_BEARER+"' e contenente un token";
    						}
    					}
    					else {
    						token = urlProtocolContext.getCredential().getBearerToken();
    						esitoPresenzaToken.setHeaderHttp(HttpConstants.AUTHORIZATION);
    					}
    				}
    				else {
    					String headerName = properties.getProperty(Costanti.POLICY_TOKEN_SOURCE_CUSTOM_HEADER_NAME);					
    					token =  urlProtocolContext.getParameterTrasporto(headerName);
    					if(token==null) {
    						detailsError = "Non è stato riscontrato l'header http '"+headerName+"' contenente il token";
    					}
    					else {
    						esitoPresenzaToken.setHeaderHttp(headerName);
    					}
    				}
    			}
    		}
    		
    		if( (token==null && Costanti.POLICY_TOKEN_SOURCE_RFC6750.equals(source)) ||
    				Costanti.POLICY_TOKEN_SOURCE_RFC6750_URL.equals(source) ||
    				Costanti.POLICY_TOKEN_SOURCE_CUSTOM_URL.equals(source)) {
    			if(datiInvocazione.getInfoConnettoreIngresso()==null || 
    					datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext()==null) {
    				detailsError = "Informazioni di trasporto non presenti";
    			}
    			else {
    				URLProtocolContext urlProtocolContext = datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext();
    				if(urlProtocolContext.getParametersFormBased()==null || urlProtocolContext.getParametersFormBased().size()<=0) {
        				detailsError = "Parametri nella URL non presenti";
        			}
    				String propertyUrlName = null;
    				if(Costanti.POLICY_TOKEN_SOURCE_RFC6750.equals(source) ||
    	    				Costanti.POLICY_TOKEN_SOURCE_RFC6750_URL.equals(source)) {
    					propertyUrlName = Costanti.RFC6750_URI_QUERY_PARAMETER_ACCESS_TOKEN;
    				}
    				else {
    					propertyUrlName = properties.getProperty(Costanti.POLICY_TOKEN_SOURCE_CUSTOM_URL_PROPERTY_NAME);	
    				}
    				token =  urlProtocolContext.getParameterFormBased(propertyUrlName);
					if(token==null) {
						detailsError = "Non è stato riscontrata la proprietà della URL '"+propertyUrlName+"' contenente il token";
					}
					else {
						esitoPresenzaToken.setPropertyUrl(propertyUrlName);
					}
    			}
    		}
    		
    		if( (token==null && Costanti.POLICY_TOKEN_SOURCE_RFC6750.equals(source)) ||
    				Costanti.POLICY_TOKEN_SOURCE_RFC6750_FORM.equals(source)) {
    			if(datiInvocazione.getInfoConnettoreIngresso()==null || 
    					datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext()==null ||
    					datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext().getHttpServletRequest()==null) {
    				detailsError = "Informazioni di trasporto non presenti";
    			}
    			else {
    				HttpServletRequest httpServletRequest = datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext().getHttpServletRequest();
    				if(httpServletRequest instanceof FormUrlEncodedHttpServletRequest) {
    					FormUrlEncodedHttpServletRequest form = (FormUrlEncodedHttpServletRequest) httpServletRequest;
    					token = form.getFormUrlEncodedParameter(Costanti.RFC6750_FORM_PARAMETER_ACCESS_TOKEN);
    					if(token==null) {
    						detailsError = "Non è stato riscontrata la proprietà della Form '"+Costanti.RFC6750_FORM_PARAMETER_ACCESS_TOKEN+"' contenente il token";
    					}
    					else {
    						esitoPresenzaToken.setPropertyFormBased(Costanti.RFC6750_FORM_PARAMETER_ACCESS_TOKEN);
    					}
    				}
    				else {
    					detailsError = "Non è stato riscontrata la presenza di un contenuto 'Form-Encoded'";
    				}
	    		}
    		}
    		
    		
    		if(token!=null) {
				esitoPresenzaToken.setToken(token);
				esitoPresenzaToken.setPresente(true);
			}
    		else {
    			if(detailsError!=null) {
					esitoPresenzaToken.setDetails(detailsError);	
				}
				else {
					esitoPresenzaToken.setDetails("Token non individuato tramite la configurazione indicata");	
				}
    			
    			
    		}
    		
    	}catch(Exception e){
    		esitoPresenzaToken.setDetails(e.getMessage());
    		esitoPresenzaToken.setEccezioneProcessamento(e);
    	}
		
	}
	
	
	private OpenSPCoop2Message buildErrorMessage(WWW_Authenticate_ErrorCode errorCode, String realm, boolean genericError, String error) {
		
		// AGGIUNGERE IL REALM tra le PROPRIETA
		OpenSPCoop2Message errorMessage = OpenSPCoop2MessageFactory.getMessageFactory().createEmptyMessage(MessageType.BINARY, MessageRole.FAULT);
		ForcedResponseMessage forcedResponseMessage = new ForcedResponseMessage();
		forcedResponseMessage.setContent(null); // vuoto
		forcedResponseMessage.setContentType(null); // vuoto
		StringBuffer bf = new StringBuffer(HttpConstants.AUTHORIZATION_PREFIX_BEARER);
		bf.append("realm=\"");
		bf.append(realm);
		bf.append("\", error=\"");
		bf.append(errorCode.name());
		bf.append("\", error_description=\"");
		if(genericError==false) {
			bf.append(error);
		}
		switch (errorCode) {
		case invalid_request:
			forcedResponseMessage.setResponseCode("400");
			break;
		case invalid_token:
			forcedResponseMessage.setResponseCode("401");
			break;
		case insufficient_scope:
			forcedResponseMessage.setResponseCode("403");
			break;
		}
		bf.append("\"");
		
		forcedResponseMessage.setHeaders(new Properties());
		forcedResponseMessage.getHeaders().put(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE, bf.toString());
		errorMessage.forceResponse(forcedResponseMessage);
		
		return errorMessage;
	}
}

enum WWW_Authenticate_ErrorCode {
	
	 invalid_request,
//     The request is missing a required parameter, includes an
//     unsupported parameter or parameter value, repeats the same
//     parameter, uses more than one method for including an access
//     token, or is otherwise malformed.  The resource server SHOULD
//     respond with the HTTP 400 (Bad Request) status code.

	 invalid_token,
//     The access token provided is expired, revoked, malformed, or
//     invalid for other reasons.  The resource SHOULD respond with
//     the HTTP 401 (Unauthorized) status code.  The client MAY
//     request a new access token and retry the protected resource
//     request.

	 insufficient_scope;
//     The request requires higher privileges than provided by the
//     access token.  The resource server SHOULD respond with the HTTP
//     403 (Forbidden) status code and MAY include the "scope"
//     attribute with the scope necessary to access the protected
//     resource.
}
