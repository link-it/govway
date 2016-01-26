/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import org.openspcoop2.utils.resources.SSLConfig;

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

	public static ConnettoreHTTPSProperties readProperties(java.util.Hashtable<String,String> properties) throws Exception{
		ConnettoreHTTPSProperties propertiesHTTPS = new ConnettoreHTTPSProperties();
		
		// AUTENTICAZIONE SERVER
		if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_LOCATION)!=null){
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
			if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM)!=null){
				tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM).trim();
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
			if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM)!=null){
				tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM).trim();
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
			propertiesHTTPS.setSslType(CostantiConnettori.CONNETTORE_HTTPS_SSL_TYPE_VALUE_SSLv3); 
		}
		
		return propertiesHTTPS;
	}
	
	
}
