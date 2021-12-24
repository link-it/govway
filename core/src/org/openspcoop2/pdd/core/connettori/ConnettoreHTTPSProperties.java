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


package org.openspcoop2.pdd.core.connettori;

import java.io.Serializable;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.utils.transport.http.SSLConfig;

/**
 * Classe utilizzata per interprare le proprieta' https
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPSProperties extends SSLConfig implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static ConnettoreHTTPSProperties readProperties(java.util.Map<String,String> properties) throws Exception{
		ConnettoreHTTPSProperties propertiesHTTPS = new ConnettoreHTTPSProperties();
		
		// AUTENTICAZIONE SERVER
		if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_ALL_CERTS)!=null){
			String tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_ALL_CERTS).trim();
			try{
				propertiesHTTPS.setTrustAllCerts(Boolean.parseBoolean(tmp));
			}catch(Exception e){
				throw new Exception("Valore definito per la proprieta' '"+CostantiConnettori.CONNETTORE_HTTPS_TRUST_ALL_CERTS+"' non valido, valori accettati true/false");
			}
		}
		if(!propertiesHTTPS.isTrustAllCerts() && properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_LOCATION)!=null){
			String tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_LOCATION).trim();
			propertiesHTTPS.setTrustStoreLocation(tmp);
			
			if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD)!=null){
				tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD).trim();
				propertiesHTTPS.setTrustStorePassword(tmp);
			}
			else{
				throw new Exception("Valore non definito per la proprieta' '"+CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD+
						"' nonostante sia stato definito un trustStore attraverso la proprieta' '"+CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_LOCATION+"'");
			}
			if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITHM)!=null){
				tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITHM).trim();
				propertiesHTTPS.setTrustManagementAlgorithm(tmp);
			}else{
				propertiesHTTPS.setTrustManagementAlgorithm(TrustManagerFactory.getDefaultAlgorithm());
			}
			if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_TYPE)!=null){
				tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_TYPE).trim();
				propertiesHTTPS.setTrustStoreType(tmp);
			}else{
				propertiesHTTPS.setTrustStoreType(KeyStore.getDefaultType()); // JKS
			}
			if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_CRLs)!=null){
				tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_CRLs).trim();
				propertiesHTTPS.setTrustStoreCRLsLocation(tmp);
			}
		}
		
		// AUTENTICAZIONE CLIENT
		if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_LOCATION)!=null){
			String tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_LOCATION).trim();
			propertiesHTTPS.setKeyStoreLocation(tmp);
			
			if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_PASSWORD)!=null){
				tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_PASSWORD).trim();
				propertiesHTTPS.setKeyStorePassword(tmp);
			}
			else{
				throw new Exception("Valore non definito per la proprieta' '"+CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_PASSWORD
						+"' nonostante sia stato definito un trustStore attraverso la proprieta' '"+CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_LOCATION+"'");
			}
			if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_KEY_PASSWORD)!=null){
				tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_KEY_PASSWORD).trim();
				propertiesHTTPS.setKeyPassword(tmp);
			}else{
				throw new Exception("Valore non definito per la proprieta' '"+CostantiConnettori.CONNETTORE_HTTPS_KEY_PASSWORD
						+"' nonostante sia stato definito un trustStore attraverso la proprieta' '"+CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_LOCATION+"'");
			}
			if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_KEY_ALIAS)!=null){
				tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_KEY_ALIAS).trim();
				propertiesHTTPS.setKeyAlias(tmp);
			}
			if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITHM)!=null){
				tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITHM).trim();
				propertiesHTTPS.setKeyManagementAlgorithm(tmp);
			}else{
				propertiesHTTPS.setKeyManagementAlgorithm(KeyManagerFactory.getDefaultAlgorithm());
			}
			if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_TYPE)!=null){
				tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_TYPE).trim();
				propertiesHTTPS.setKeyStoreType(tmp);
			}else{
				propertiesHTTPS.setKeyStoreType(KeyStore.getDefaultType()); // JKS
			}
		}	
		
		// HostName verifier
		if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_HOSTNAME_VERIFIER)!=null){
			String tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_HOSTNAME_VERIFIER).trim();
			try{
				propertiesHTTPS.setHostnameVerifier(Boolean.parseBoolean(tmp));
			}catch(Exception e){
				throw new Exception("Valore definito per la proprieta' '"+CostantiConnettori.CONNETTORE_HTTPS_HOSTNAME_VERIFIER+"' non valido, valori accettati true/false");
			}
		}
		if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_CLASSNAME_HOSTNAME_VERIFIER)!=null){
			String tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_CLASSNAME_HOSTNAME_VERIFIER).trim();
			propertiesHTTPS.setClassNameHostnameVerifier(tmp);
		}
		
		// TipologiaSSL: SSL, SSLv3, TLS, TLSv1
		if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_SSL_TYPE)!=null){
			String tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_SSL_TYPE).trim();
			propertiesHTTPS.setSslType(tmp);
		}else{
			propertiesHTTPS.setSslType(CostantiConnettori.CONNETTORE_HTTPS_SSL_TYPE_DEFAULT_VALUE); 
		}
		
		// SecureRandom
		if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_SECURE_RANDOM)!=null){
			String tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_SECURE_RANDOM).trim();
			try{
				propertiesHTTPS.setSecureRandom(Boolean.parseBoolean(tmp));
			}catch(Exception e){
				throw new Exception("Valore definito per la proprieta' '"+CostantiConnettori.CONNETTORE_HTTPS_SECURE_RANDOM+"' non valido, valori accettati true/false");
			}
			if(propertiesHTTPS.isSecureRandom()) {
				if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_SECURE_RANDOM_ALGORITHM)!=null){
					propertiesHTTPS.setSecureRandomAlgorithm(properties.get(CostantiConnettori.CONNETTORE_HTTPS_SECURE_RANDOM_ALGORITHM).trim());
				}
			}
		}
		
		return propertiesHTTPS;
	}
	
	
}
