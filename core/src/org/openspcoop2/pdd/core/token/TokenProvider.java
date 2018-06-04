package org.openspcoop2.pdd.core.token;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.mvc.properties.provider.IProvider;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.utils.transport.http.SSLUtilities;

public class TokenProvider implements IProvider {

	@Override
	public void validate(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		
		Properties pDefault = mapProperties.get(org.openspcoop2.core.mvc.properties.utils.Costanti.NOME_MAPPA_PROPERTIES_DEFAULT);
				
		boolean validazione = false;
		boolean introspection = false;
		boolean userInfo = false;
		boolean forward = false;
		
		if(pDefault.containsKey(Costanti.POLICY_VALIDAZIONE_STATO)) {
			validazione = Boolean.valueOf(pDefault.getProperty(Costanti.POLICY_VALIDAZIONE_STATO));
		}
		if(pDefault.containsKey(Costanti.POLICY_INTROSPECTION_STATO)) {
			introspection = Boolean.valueOf(pDefault.getProperty(Costanti.POLICY_INTROSPECTION_STATO));
		}
		if(pDefault.containsKey(Costanti.POLICY_USER_INFO_STATO)) {
			userInfo = Boolean.valueOf(pDefault.getProperty(Costanti.POLICY_USER_INFO_STATO));
		}
		if(pDefault.containsKey(Costanti.POLICY_TOKEN_FORWARD_STATO)) {
			forward = Boolean.valueOf(pDefault.getProperty(Costanti.POLICY_TOKEN_FORWARD_STATO));
		}
		
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
			
			boolean forwardTrasparente = false;
			boolean forwardInformazioniRaccolte = false;
			
			if(pDefault.containsKey(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_STATO)) {
				forwardTrasparente = Boolean.valueOf(pDefault.getProperty(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_STATO));
			}
			if(pDefault.containsKey(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_STATO)) {
				forwardInformazioniRaccolte = Boolean.valueOf(pDefault.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_STATO));
			}
			
			if(!forwardTrasparente && !forwardInformazioniRaccolte) {
				throw new ProviderValidationException("Almeno una modalità di forward del token deve essere selezionata");
			}
			
			if(forwardInformazioniRaccolte) {
				boolean forwardIntrospection = false;
				boolean forwardUserInfo = false;
				
				if(pDefault.containsKey(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_INTROSPECTION)) {
					forwardIntrospection = Boolean.valueOf(pDefault.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_INTROSPECTION));
				}
				if(pDefault.containsKey(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_USER_INFO)) {
					forwardUserInfo = Boolean.valueOf(pDefault.getProperty(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_USER_INFO));
				}
				
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
		else if(Costanti.ID_JWS_SIGNATURE_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm [] tmp = org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].name());
			}
			return l;
		}
		else if(Costanti.ID_JWS_ENCRYPT_KEY_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm [] tmp = org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].name());
			}
			return l;
		}
		else if(Costanti.ID_JWS_ENCRYPT_CONTENT_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm [] tmp = org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].name());
			}
			return l;
		}
		return null;
	}

	@Override
	public List<String> getLabels(String id) throws ProviderException {
		return this.getValues(id);
	}

	@Override
	public String getDefault(String id) throws ProviderException {
		if(Costanti.ID_TIPOLOGIA_HTTPS.equals(id)) {
			return SSLUtilities.getSafeDefaultProtocol();
		}
		else if(Costanti.ID_JWS_SIGNATURE_ALGORITHM.equals(id)) {
			return org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.RS256.name();
		}
		else if(Costanti.ID_JWS_ENCRYPT_KEY_ALGORITHM.equals(id)) {
			return org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm.RSA_OAEP_256.name();
		}
		else if(Costanti.ID_JWS_ENCRYPT_CONTENT_ALGORITHM.equals(id)) {
			return org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm.A256GCM.name();
		}
		return null;
	}

}
