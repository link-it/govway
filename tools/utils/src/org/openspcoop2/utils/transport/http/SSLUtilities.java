/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.utils.transport.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Provider.Service;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.Loader;

/**
 * SSLUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SSLUtilities {
	
	public static List<String> getSSLEnabledProtocols(String sslType) throws UtilsException{
		try{
			List<String> p = new ArrayList<String>();
			SSLContext context = SSLContext.getInstance(sslType);
			context.init(null,null,null);
			SSLSocket socket = (SSLSocket)context.getSocketFactory().createSocket();
			String[] protocols = socket.getEnabledProtocols();
			for (int i = 0; i < protocols.length; i++) {
				p.add(protocols[i]);
			}
			return p;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(), e);
		}
	}
	public static List<String> getSSLSupportedProtocols() throws UtilsException{
		try{
			List<String> p = new ArrayList<String>();
			SSLContext defaultContext = SSLContext.getDefault();
			SSLSocket socket = (SSLSocket)defaultContext.getSocketFactory().createSocket();
			String[] protocols = socket.getSupportedProtocols();
			for (int i = 0; i < protocols.length; i++) {
				p.add(protocols[i]);
			}
			return p;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(), e);
		}
	}
	public static String getSafeDefaultProtocol(){
		try{
			return getDefaultProtocol();
		}catch(Exception e){
			return SSLConstants.PROTOCOL_TLS;
		}
	}
	public static String getDefaultProtocol() throws UtilsException{
		// Ritorno l'ultima versione disponibile
		List<String> p = getSSLSupportedProtocols();
		if(p.contains(SSLConstants.PROTOCOL_TLS_V1_2)){
			return SSLConstants.PROTOCOL_TLS_V1_2;
		}
		else if(p.contains(SSLConstants.PROTOCOL_TLS_V1_1)){
			return SSLConstants.PROTOCOL_TLS_V1_1;
		}
		else if(p.contains(SSLConstants.PROTOCOL_TLS_V1)){
			return SSLConstants.PROTOCOL_TLS_V1;
		}
		else if(p.contains(SSLConstants.PROTOCOL_TLS)){
			return SSLConstants.PROTOCOL_TLS;
		}
		else if(p.contains(SSLConstants.PROTOCOL_SSL_V3)){
			return SSLConstants.PROTOCOL_SSL_V3;
		}
		else if(p.contains(SSLConstants.PROTOCOL_SSL)){
			return SSLConstants.PROTOCOL_SSL;
		}
		else if(p.contains(SSLConstants.PROTOCOL_SSL_V2_HELLO)){
			return SSLConstants.PROTOCOL_SSL_V2_HELLO;
		}
		else{
			return p.get(0);
		}
	}
	public static List<String> getAllSslProtocol() {
		// ritorno in ordine dal più recente al meno recento, più altri eventuali protocolli.
		List<String> p = new ArrayList<String>();
		p.add(SSLConstants.PROTOCOL_TLS_V1_2);
		p.add(SSLConstants.PROTOCOL_TLS_V1_1);
		p.add(SSLConstants.PROTOCOL_TLS_V1);
		p.add(SSLConstants.PROTOCOL_TLS); // per retrocompatibilità
		p.add(SSLConstants.PROTOCOL_SSL_V3);
		p.add(SSLConstants.PROTOCOL_SSL); // per retrocompatibilità
		p.add(SSLConstants.PROTOCOL_SSL_V2_HELLO);
		
		try{
			List<String> pTmp = getSSLSupportedProtocols();
			for (String s : pTmp) {
				if(p.contains(s)==false){
					p.add(s);
				}
			}
		}catch(Exception e){
			// non dovrebbe mai accadere una eccezione
			e.printStackTrace(System.err);
		}
			
		return  p;
	}
	
	public static List<String> getSSLEnabledCipherSuites(String sslType) throws UtilsException{
		try{
			List<String> l = new ArrayList<String>();
			SSLContext context = SSLContext.getInstance(sslType);
			context.init(null,null,null);
			SSLSocket socket = (SSLSocket)context.getSocketFactory().createSocket();
			String[] cs = socket.getEnabledCipherSuites();
			for (int i = 0; i < cs.length; i++) {
				l.add(cs[i]);
			}
			return l;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(), e);
		}
	}
	public static List<String> getSSLSupportedCipherSuites() throws UtilsException{
		try{
			List<String> l = new ArrayList<String>();
			SSLContext defaultContext = SSLContext.getDefault();
			SSLSocket socket = (SSLSocket)defaultContext.getSocketFactory().createSocket();
			String[] cs = socket.getSupportedCipherSuites();
			for (int i = 0; i < cs.length; i++) {
				l.add(cs[i]);
			}
			return l;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(), e);
		}
	}
		
	
	public static List<Provider> getSSLProviders() throws UtilsException{
		try{
			List<Provider> p = new ArrayList<Provider>();
			for (Provider provider : Security.getProviders()){
				p.add(provider);
			}
			return p;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(), e);
		}
	}
	public static List<String> getSSLProvidersName() throws UtilsException{
		try{
			List<String> p = new ArrayList<String>();
			for (Provider provider : Security.getProviders()){
				p.add(provider.getName());
			}
			return p;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(), e);
		}
	}
	public static List<String> getServiceTypes(Provider provider) throws UtilsException{
		try{
			List<String> p = new ArrayList<String>();
			for (Service service : provider.getServices()){
				if(p.contains(service.getType())==false){
					p.add(service.getType());
				}
			}
			return p;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(), e);
		}
	}
	public static List<String> getServiceTypeAlgorithms(Provider provider,String serviceType) throws UtilsException{
		try{
			List<String> p = new ArrayList<String>();
			for (Service service : provider.getServices()){
				if(serviceType.equals(service.getType())){
					p.add(service.getAlgorithm());
				}
			}
			return p;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(), e);
		}
	}
	
	public static SSLContext generateSSLContext(SSLConfig sslConfig, StringBuffer bfLog) throws UtilsException{

		// Gestione https
		SSLContext sslContext = null;

		bfLog.append("Creo contesto SSL...\n");
		KeyManager[] km = null;
		TrustManager[] tm = null;

		InputStream finKeyStore = null;
		InputStream finTrustStore = null;
		try{
		
			// Autenticazione CLIENT
			if(sslConfig.getKeyStoreLocation()!=null){
				bfLog.append("Gestione keystore...\n");
				bfLog.append("\tKeystore type["+sslConfig.getKeyStoreType()+"]\n");
				bfLog.append("\tKeystore location["+sslConfig.getKeyStoreLocation()+"]\n");
				//bfLog.append("\tKeystore password["+sslConfig.getKeyStorePassword()+"]\n");
				bfLog.append("\tKeystore keyManagementAlgorithm["+sslConfig.getKeyManagementAlgorithm()+"]\n");
				//bfLog.append("\tKeystore keyPassword["+sslConfig.getKeyPassword()+"]\n");
				KeyStore keystore = KeyStore.getInstance(sslConfig.getKeyStoreType()); // JKS,PKCS12,jceks,bks,uber,gkr
				File file = new File(sslConfig.getKeyStoreLocation());
				if(file.exists()) {
					finKeyStore = new FileInputStream(file);
				}
				else {
					finKeyStore = SSLUtilities.class.getResourceAsStream(sslConfig.getKeyStoreLocation());
				}
				if(finKeyStore == null) {
					throw new Exception("Keystore ["+sslConfig.getKeyStoreLocation()+"] not found");
				}				
				keystore.load(finKeyStore, sslConfig.getKeyStorePassword().toCharArray());
				KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(sslConfig.getKeyManagementAlgorithm());
				keyManagerFactory.init(keystore, sslConfig.getKeyPassword().toCharArray());
				km = keyManagerFactory.getKeyManagers();
				bfLog.append("Gestione keystore effettuata\n");
			}
	
	
			// Autenticazione SERVER
			if(sslConfig.getTrustStoreLocation()!=null){
				bfLog.append("Gestione truststore...\n");
				bfLog.append("\tTruststore type["+sslConfig.getTrustStoreType()+"]\n");
				bfLog.append("\tTruststore location["+sslConfig.getTrustStoreLocation()+"]\n");
				//bfLog.append("\tTruststore password["+sslConfig.getTrustStorePassword()+"]\n");
				bfLog.append("\tTruststore trustManagementAlgorithm["+sslConfig.getTrustManagementAlgorithm()+"]\n");
				KeyStore truststore = KeyStore.getInstance(sslConfig.getTrustStoreType()); // JKS,PKCS12,jceks,bks,uber,gkr
				File file = new File(sslConfig.getTrustStoreLocation());
				if(file.exists()) {
					finTrustStore = new FileInputStream(file);
				}
				else {
					finTrustStore = SSLUtilities.class.getResourceAsStream(sslConfig.getTrustStoreLocation());
				}
				if(finTrustStore == null) {
					throw new Exception("Keystore ["+sslConfig.getTrustStoreLocation()+"] not found");
				}		
				truststore.load(finTrustStore, sslConfig.getTrustStorePassword().toCharArray());
				TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(sslConfig.getTrustManagementAlgorithm());
				trustManagerFactory.init(truststore);
				tm = trustManagerFactory.getTrustManagers();
				bfLog.append("Gestione truststore effettuata\n");
			}
	
			// Creo contesto SSL
			bfLog.append("Init SSLContext type["+sslConfig.getSslType()+"] ...\n");
			sslContext = SSLContext.getInstance(sslConfig.getSslType());
			sslContext.init(km, tm, null);	
			bfLog.append("Init SSLContext type["+sslConfig.getSslType()+"] effettuato\n");
			
			return sslContext;
			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(), e);
		}
		finally{
			try{
				if(finKeyStore!=null){
					finKeyStore.close();
				}
			}catch(Exception e){}
			try{
				if(finTrustStore!=null){
					finTrustStore.close();
				}
			}catch(Exception e){}
		}
	}

	
	public static HostnameVerifier generateHostnameVerifier(SSLConfig sslConfig, StringBuffer bfLog, Logger log, Loader loader) throws UtilsException{
		try{
			if(sslConfig.isHostnameVerifier()){
				if(sslConfig.getClassNameHostnameVerifier()!=null){
					bfLog.append("HostNamve verifier enabled ["+sslConfig.getClassNameHostnameVerifier()+"]\n");
					HostnameVerifier verifica = (HostnameVerifier) loader.newInstance(sslConfig.getClassNameHostnameVerifier());
					return verifica;
				}else{
					bfLog.append("HostName verifier enabled\n");
					return null;
				}
			}else{
				bfLog.append("HostName verifier disabled\n");
				SSLHostNameVerifierDisabled disabilitato = new SSLHostNameVerifierDisabled(log);
				return disabilitato;
			}
		}catch(Exception e){
			throw new UtilsException(e.getMessage(), e);
		}
		
	}
	
	
	public static void setSSLContextIntoJavaProperties(SSLConfig sslConfig, StringBuffer bfLog) throws UtilsException{

		bfLog.append("Creo contesto SSL...\n");

		try{
		
			// Autenticazione CLIENT
			if(sslConfig.getKeyStoreLocation()!=null){
				bfLog.append("Gestione keystore...\n");
				
				System.setProperty("javax.net.ssl.keyStore", sslConfig.getKeyStoreLocation());
				if(sslConfig.getKeyStoreType()!=null){
					System.setProperty("javax.net.ssl.keyStoreType", sslConfig.getKeyStoreType());
				}
				if(sslConfig.getKeyStorePassword()!=null){
					System.setProperty("javax.net.ssl.keyStorePassword", sslConfig.getKeyStorePassword());
				}
				if(sslConfig.getKeyPassword()!=null){
					System.setProperty("javax.net.ssl.keyStorePassword", sslConfig.getKeyStorePassword());
					if(sslConfig.getKeyStorePassword()!=null){
						if(!sslConfig.getKeyPassword().equals(sslConfig.getKeyStorePassword())){
							throw new UtilsException("Keystore and key password in java must be equals");
						}
					}
				}
				
				bfLog.append("Gestione keystore effettuata\n");
			}
	
	
			// Autenticazione SERVER
			if(sslConfig.getTrustStoreLocation()!=null){
				bfLog.append("Gestione truststore...\n");
				
				System.setProperty("javax.net.ssl.trustStore", sslConfig.getTrustStoreLocation());
				if(sslConfig.getKeyStoreType()!=null){
					System.setProperty("javax.net.ssl.trustStoreType", sslConfig.getTrustStoreType());
				}
				if(sslConfig.getKeyStorePassword()!=null){
					System.setProperty("javax.net.ssl.trustStorePassword", sslConfig.getTrustStorePassword());
				}
				
				bfLog.append("Gestione truststore effettuata\n");
			}
			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(), e);
		}

	}
}
