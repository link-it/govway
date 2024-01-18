/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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


package org.openspcoop2.core.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.security.KeyStore;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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

	private static final String VALORE_DEFINITO_PER_PROPRIETA = "Valore definito per la proprietà '";
	private static final String VALORE_NON_DEFINITO_PER_PROPRIETA = "Valore non definito per la proprietà '";
	private static final String NON_VALIDO_TRUE_FALSE = "' non valido, valori accettati true/false";
	private static final String DEFINITO_TRAMITE_PROPRIETA = "' nonostante sia stato definito un trustStore attraverso la proprietà '";
	
	public static ConnettoreHTTPSProperties readProperties(java.util.Map<String,String> properties) throws CoreException{
		ConnettoreHTTPSProperties propertiesHTTPS = new ConnettoreHTTPSProperties();
		
		// AUTENTICAZIONE SERVER
		if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_ALL_CERTS)!=null){
			String tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_ALL_CERTS).trim();
			try{
				propertiesHTTPS.setTrustAllCerts(Boolean.parseBoolean(tmp));
			}catch(Exception e){
				throw new CoreException(VALORE_DEFINITO_PER_PROPRIETA+CostantiConnettori.CONNETTORE_HTTPS_TRUST_ALL_CERTS+NON_VALIDO_TRUE_FALSE);
			}
		}
		if(!propertiesHTTPS.isTrustAllCerts() && properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_LOCATION)!=null){
			readTrustStoreConfig(properties, propertiesHTTPS);
		}
		else if(propertiesHTTPS.isTrustAllCerts()){
			// potrei accettare qualsiasi certificato ma validarlo rispetto a OCSP
			readTrustStoreAllConfig(properties, propertiesHTTPS);
		}
		
		// AUTENTICAZIONE CLIENT
		if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_LOCATION)!=null){
			readKeyStoreConfig(properties, propertiesHTTPS);
		}	
		
		// HostName verifier
		if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_HOSTNAME_VERIFIER)!=null){
			String tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_HOSTNAME_VERIFIER).trim();
			try{
				propertiesHTTPS.setHostnameVerifier(Boolean.parseBoolean(tmp));
			}catch(Exception e){
				throw new CoreException(VALORE_DEFINITO_PER_PROPRIETA+CostantiConnettori.CONNETTORE_HTTPS_HOSTNAME_VERIFIER+NON_VALIDO_TRUE_FALSE);
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
			readSecureRandomConfig(properties, propertiesHTTPS);
		}
		
		return propertiesHTTPS;
	}
	private static void readTrustStoreConfig(java.util.Map<String,String> properties, ConnettoreHTTPSProperties propertiesHTTPS) throws CoreException {
		String tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_LOCATION).trim();
		propertiesHTTPS.setTrustStoreLocation(tmp);
		
		if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD)!=null){
			tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD).trim();
			propertiesHTTPS.setTrustStorePassword(tmp);
		}
		else{
			throw new CoreException(VALORE_NON_DEFINITO_PER_PROPRIETA+CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD+
					DEFINITO_TRAMITE_PROPRIETA+CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_LOCATION+"'");
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
		if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY)!=null){
			tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY).trim();
			propertiesHTTPS.setTrustStoreOCSPPolicy(tmp);
		}
	}
	private static void readTrustStoreAllConfig(java.util.Map<String,String> properties, ConnettoreHTTPSProperties propertiesHTTPS) {
		// potrei accettare qualsiasi certificato ma validarlo rispetto a OCSP
		if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY)!=null){
			String tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY).trim();
			propertiesHTTPS.setTrustStoreOCSPPolicy(tmp);
			
			if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_CRLs)!=null){
				tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_CRLs).trim();
				propertiesHTTPS.setTrustStoreCRLsLocation(tmp);
			}
		}
	}
	private static void readKeyStoreConfig(java.util.Map<String,String> properties, ConnettoreHTTPSProperties propertiesHTTPS) throws CoreException {
		String tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_LOCATION).trim();
		propertiesHTTPS.setKeyStoreLocation(tmp);
		
		if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_PASSWORD)!=null){
			tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_PASSWORD).trim();
			propertiesHTTPS.setKeyStorePassword(tmp);
		}
		else{
			throw new CoreException(VALORE_NON_DEFINITO_PER_PROPRIETA+CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_PASSWORD
					+DEFINITO_TRAMITE_PROPRIETA+CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_LOCATION+"'");
		}
		if(properties.get(CostantiConnettori.CONNETTORE_HTTPS_KEY_PASSWORD)!=null){
			tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_KEY_PASSWORD).trim();
			propertiesHTTPS.setKeyPassword(tmp);
		}else{
			throw new CoreException(VALORE_NON_DEFINITO_PER_PROPRIETA+CostantiConnettori.CONNETTORE_HTTPS_KEY_PASSWORD
					+DEFINITO_TRAMITE_PROPRIETA+CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_LOCATION+"'");
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
	private static void readSecureRandomConfig(java.util.Map<String,String> properties, ConnettoreHTTPSProperties propertiesHTTPS) throws CoreException {
		String tmp = properties.get(CostantiConnettori.CONNETTORE_HTTPS_SECURE_RANDOM).trim();
		try{
			propertiesHTTPS.setSecureRandom(Boolean.parseBoolean(tmp));
		}catch(Exception e){
			throw new CoreException(VALORE_DEFINITO_PER_PROPRIETA+CostantiConnettori.CONNETTORE_HTTPS_SECURE_RANDOM+NON_VALIDO_TRUE_FALSE);
		}
		if(propertiesHTTPS.isSecureRandom() &&
			properties.get(CostantiConnettori.CONNETTORE_HTTPS_SECURE_RANDOM_ALGORITHM)!=null){
			propertiesHTTPS.setSecureRandomAlgorithm(properties.get(CostantiConnettori.CONNETTORE_HTTPS_SECURE_RANDOM_ALGORITHM).trim());
		}
	}
	
	public static ConnettoreHTTPSProperties readPropertyFile(String file, boolean sslConfigRequired) throws CoreException{
		return readPropertyFile(new File(file), sslConfigRequired);
	}
	public static ConnettoreHTTPSProperties readPropertyFile(File file, boolean sslConfigRequired) throws CoreException{
		
		String prefix = "Config file ["+file.getAbsolutePath()+"] ";
		if(!file.exists()) {
			if(sslConfigRequired) {
				throw new CoreException(prefix+"not exists");
			}
			return null;
		}
		if(file.isDirectory()) {
			throw new CoreException(prefix+"is directory");
		}
		if(!file.canRead()) {
			throw new CoreException(prefix+"cannot read");
		}
		
		Properties p = new Properties();
		try (FileInputStream fin = new FileInputStream(file)){
			p.load(fin);
		}
		catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
		Map<String, String> pMap = new HashMap<>();
		Enumeration<?> enP = p.keys();
		while (enP.hasMoreElements()) {
			String key = (String) enP.nextElement();
			pMap.put(key, p.getProperty(key));
		}
		return readProperties(pMap); 
	}
}
