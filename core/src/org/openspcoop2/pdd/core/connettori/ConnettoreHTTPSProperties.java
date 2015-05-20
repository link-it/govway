/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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

import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import org.openspcoop2.core.constants.CostantiConnettori;

/**
 * Classe utilizzata per interprare le proprieta' https
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPSProperties  {

	// AUTENTICAZIONE SERVER:
	// Path del trustStore che contiene il certificato del server.
	private String trustStoreLocation;
	// Password del trustStore che contiene il certificato del server.
	private String trustStorePassword;
	// the standard name of the requested trust management algorithm
	private String trustManagementAlgorithm;
	// tipo del truststore
	private String trustStoreType;
	
	// AUTENTICAZIONE CLIENT:
	// Path del keyStore che contiene il certificato del client e la chiave privata del client.
	private String keyStoreLocation;
	// Password del keyStore che contiene il certificato del client
	private String keyStorePassword;
	// Password della chiave privata
	private String keyPassword;
	// the standard name of the requested key management algorithm
	private String keyManagementAlgorithm;
	// tipo del keystore
	private String keyStoreType;

	// HostName verifier
	private boolean hostnameVerifier = true;
	// Eventuale classe da utilizzare per effettuare hostnameVerifier al posto di quella di default
	private String classNameHostnameVerifier;

	// TipologiaSSL
	private String sslType= null;
	
	
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
	
	
	public String getTrustStoreLocation() {
		return this.trustStoreLocation;
	}

	public void setTrustStoreLocation(String trustStoreLocation) {
		this.trustStoreLocation = trustStoreLocation;
	}

	public String getTrustStorePassword() {
		return this.trustStorePassword;
	}

	public void setTrustStorePassword(String trustStorePassword) {
		this.trustStorePassword = trustStorePassword;
	}

	public String getTrustManagementAlgorithm() {
		return this.trustManagementAlgorithm;
	}

	public void setTrustManagementAlgorithm(String trustManagementAlgorithm) {
		this.trustManagementAlgorithm = trustManagementAlgorithm;
	}

	public String getTrustStoreType() {
		return this.trustStoreType;
	}

	public void setTrustStoreType(String trustStoreType) {
		this.trustStoreType = trustStoreType;
	}

	public String getKeyStoreLocation() {
		return this.keyStoreLocation;
	}

	public void setKeyStoreLocation(String keyStoreLocation) {
		this.keyStoreLocation = keyStoreLocation;
	}

	public String getKeyStorePassword() {
		return this.keyStorePassword;
	}

	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

	public String getKeyPassword() {
		return this.keyPassword;
	}

	public void setKeyPassword(String keyPassword) {
		this.keyPassword = keyPassword;
	}

	public String getKeyManagementAlgorithm() {
		return this.keyManagementAlgorithm;
	}

	public void setKeyManagementAlgorithm(String keyManagementAlgorithm) {
		this.keyManagementAlgorithm = keyManagementAlgorithm;
	}

	public String getKeyStoreType() {
		return this.keyStoreType;
	}

	public void setKeyStoreType(String keyStoreType) {
		this.keyStoreType = keyStoreType;
	}

	public boolean isHostnameVerifier() {
		return this.hostnameVerifier;
	}

	public void setHostnameVerifier(boolean hostnameVerifier) {
		this.hostnameVerifier = hostnameVerifier;
	}

	public String getClassNameHostnameVerifier() {
		return this.classNameHostnameVerifier;
	}

	public void setClassNameHostnameVerifier(String classNameHostnameVerifier) {
		this.classNameHostnameVerifier = classNameHostnameVerifier;
	}

	public String getSslType() {
		return this.sslType;
	}

	public void setSslType(String sslType) {
		this.sslType = sslType;
	}
}
