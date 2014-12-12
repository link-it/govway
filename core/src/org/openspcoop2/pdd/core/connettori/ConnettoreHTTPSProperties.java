/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved. 
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
		if(properties.get("trustStoreLocation")!=null){
			String tmp = properties.get("trustStoreLocation").trim();
			propertiesHTTPS.setTrustStoreLocation(tmp);
			
			if(properties.get("trustStorePassword")!=null){
				tmp = properties.get("trustStorePassword").trim();
				propertiesHTTPS.setTrustStorePassword(tmp);
			}
			else{
				throw new Exception("Valore non definito per la proprieta' 'trustStorePassword' nonostante sia stato definito un trustStore attraverso la proprieta' 'trustStoreLocation'");
			}
			if(properties.get("trustManagementAlgorithm")!=null){
				tmp = properties.get("trustManagementAlgorithm").trim();
				propertiesHTTPS.setTrustManagementAlgorithm(tmp);
			}else{
				propertiesHTTPS.setTrustManagementAlgorithm(TrustManagerFactory.getDefaultAlgorithm());
			}
			if(properties.get("trustStoreType")!=null){
				tmp = properties.get("trustStoreType").trim();
				propertiesHTTPS.setTrustStoreType(tmp);
			}else{
				propertiesHTTPS.setTrustStoreType(KeyStore.getDefaultType()); // JKS
			}
		}
		
		// AUTENTICAZIONE CLIENT
		if(properties.get("keyStoreLocation")!=null){
			String tmp = properties.get("keyStoreLocation").trim();
			propertiesHTTPS.setKeyStoreLocation(tmp);
			
			if(properties.get("keyStorePassword")!=null){
				tmp = properties.get("keyStorePassword").trim();
				propertiesHTTPS.setKeyStorePassword(tmp);
			}
			else{
				throw new Exception("Valore non definito per la proprieta' 'keyStorePassword' nonostante sia stato definito un trustStore attraverso la proprieta' 'keyStoreLocation'");
			}
			if(properties.get("keyPassword")!=null){
				tmp = properties.get("keyPassword").trim();
				propertiesHTTPS.setKeyPassword(tmp);
			}else{
				throw new Exception("Valore non definito per la proprieta' 'keyPassword' nonostante sia stato definito un trustStore attraverso la proprieta' 'keyStoreLocation'");
			}
			if(properties.get("keyManagementAlgorithm")!=null){
				tmp = properties.get("keyManagementAlgorithm").trim();
				propertiesHTTPS.setKeyManagementAlgorithm(tmp);
			}else{
				propertiesHTTPS.setKeyManagementAlgorithm(KeyManagerFactory.getDefaultAlgorithm());
			}
			if(properties.get("keyStoreType")!=null){
				tmp = properties.get("keyStoreType").trim();
				propertiesHTTPS.setKeyStoreType(tmp);
			}else{
				propertiesHTTPS.setKeyStoreType(KeyStore.getDefaultType()); // JKS
			}
		}	
		
		// HostName verifier
		if(properties.get("hostnameVerifier")!=null){
			String tmp = properties.get("hostnameVerifier").trim();
			try{
				propertiesHTTPS.setHostnameVerifier(Boolean.parseBoolean(tmp));
			}catch(Exception e){
				throw new Exception("Valore definito per la proprieta' 'hostnameVerifier' non valido, valori accettati true/false");
			}
		}
		if(properties.get("classNameHostnameVerifier")!=null){
			String tmp = properties.get("classNameHostnameVerifier").trim();
			propertiesHTTPS.setClassNameHostnameVerifier(tmp);
		}
		
		// TipologiaSSL: SSL, SSLv3, TLS, TLSv1
		if(properties.get("sslType")!=null){
			String tmp = properties.get("sslType").trim();
			propertiesHTTPS.setSslType(tmp);
		}else{
			propertiesHTTPS.setSslType("SSLv3"); 
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
