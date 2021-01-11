/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.Key;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Provider.Service;
import java.security.Security;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.X509CRL;
import java.security.cert.X509CertSelector;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.net.ssl.CertPathTrustManagerParameters;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.random.RandomGenerator;
import org.openspcoop2.utils.resources.Loader;
import org.slf4j.Logger;

/**
 * SSLUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SSLUtilities {
	
	
	private static String jvmHttpsClientCertificateConfigurated = null;
	private static synchronized void initJvmHttpsClientCertificateConfigurated() {
		if(jvmHttpsClientCertificateConfigurated==null) {
			jvmHttpsClientCertificateConfigurated = System.getProperty("javax.net.ssl.keyStore");
		}
	}
	public static boolean isJvmHttpsClientCertificateConfigurated() {
		if(jvmHttpsClientCertificateConfigurated==null) {
			initJvmHttpsClientCertificateConfigurated();
		}
		return jvmHttpsClientCertificateConfigurated!=null;
	}
	public static String getJvmHttpsClientCertificateConfigurated() {
		if(jvmHttpsClientCertificateConfigurated==null) {
			initJvmHttpsClientCertificateConfigurated();
		}
		return jvmHttpsClientCertificateConfigurated;
	}
	
	
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
		if(p.contains(SSLConstants.PROTOCOL_TLS_V1_3)){
			return SSLConstants.PROTOCOL_TLS_V1_3;
		}
		else if(p.contains(SSLConstants.PROTOCOL_TLS_V1_2)){
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
	
	private static TrustManager[] trustAllCertsManager;
	private synchronized static void initTrustAllCertsManager() {
		if(trustAllCertsManager==null) {
			// Create a trust manager that does not validate certificate chains
			trustAllCertsManager = new TrustManager[]{
			    new X509TrustManager() {
			        @Override
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			            return null;
			        }
			        @Override
					public void checkClientTrusted(
			            java.security.cert.X509Certificate[] certs, String authType) {
			        }
			        @Override
					public void checkServerTrusted(
			            java.security.cert.X509Certificate[] certs, String authType) {
			        }
			    }
			};
		}
	}
	public static TrustManager[] getTrustAllCertsManager() {
		if(trustAllCertsManager==null) {
			initTrustAllCertsManager();
		}
		return trustAllCertsManager;
	}
	
	public static SSLContext generateSSLContext(SSLConfig sslConfig, StringBuilder bfLog) throws UtilsException{

		// Gestione https
		SSLContext sslContext = null;

		bfLog.append("Creo contesto SSL...\n");
		KeyManager[] km = null;
		TrustManager[] tm = null;

		InputStream finKeyStore = null;
		InputStream finTrustStore = null;
		try{
		
			// Autenticazione CLIENT
			if(sslConfig.getKeyStore()!=null || sslConfig.getKeyStoreLocation()!=null){
				bfLog.append("Gestione keystore...\n");
				bfLog.append("\tKeystore type["+sslConfig.getKeyStoreType()+"]\n");
				bfLog.append("\tKeystore location["+sslConfig.getKeyStoreLocation()+"]\n");
				//bfLog.append("\tKeystore password["+sslConfig.getKeyStorePassword()+"]\n");
				bfLog.append("\tKeystore keyManagementAlgorithm["+sslConfig.getKeyManagementAlgorithm()+"]\n");
				//bfLog.append("\tKeystore keyPassword["+sslConfig.getKeyPassword()+"]\n");
				String location = null;
				try {
					location = sslConfig.getKeyStoreLocation(); // per debug
				
					KeyStore keystore = null;
					KeyStore keystoreParam = null;
					if(sslConfig.getKeyStore()!=null) {
						keystoreParam = sslConfig.getKeyStore();
					}
					else {
						keystoreParam = KeyStore.getInstance(sslConfig.getKeyStoreType()); // JKS,PKCS12,jceks,bks,uber,gkr 
						File file = new File(location);
						if(file.exists()) {
							finKeyStore = new FileInputStream(file);
						}
						else {
							finKeyStore = SSLUtilities.class.getResourceAsStream(location);
						}
						if(finKeyStore == null) {
							throw new Exception("Keystore not found");
						}
						keystoreParam.load(finKeyStore, sslConfig.getKeyStorePassword().toCharArray());
					}
					
					if(sslConfig.getKeyAlias()!=null) {
						Key key = keystoreParam.getKey(sslConfig.getKeyAlias(), sslConfig.getKeyPassword().toCharArray());
						if(key==null) {
							throw new Exception("Key with alias '"+sslConfig.getKeyAlias()+"' not found");
						}
						keystore = KeyStore.getInstance(sslConfig.getKeyStoreType());
						keystore.load(null); // inizializza il keystore
						keystore.setKeyEntry(sslConfig.getKeyAlias(), key, 
								sslConfig.getKeyPassword().toCharArray(), keystoreParam.getCertificateChain(sslConfig.getKeyAlias()));
					}
					else {
						keystore = keystoreParam;
					}
					
					KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(sslConfig.getKeyManagementAlgorithm());
					keyManagerFactory.init(keystore, sslConfig.getKeyPassword().toCharArray());
					km = keyManagerFactory.getKeyManagers();
					bfLog.append("Gestione keystore effettuata\n");
				}catch(Throwable e) {
					if(location!=null) {
						throw new UtilsException("["+location+"] "+e.getMessage(),e);
					}
					else {
						throw new UtilsException(e.getMessage(),e);
					}
				}
			}
	
	
			// Autenticazione SERVER
			if(sslConfig.isTrustAllCerts()) {
				bfLog.append("Gestione trust all certs...\n");
				tm = getTrustAllCertsManager();
				bfLog.append("Gestione trust all certs effettuata\n");
			}
			else if(sslConfig.getTrustStore()!=null || sslConfig.getTrustStoreLocation()!=null){
				bfLog.append("Gestione truststore...\n");
				bfLog.append("\tTruststore type["+sslConfig.getTrustStoreType()+"]\n");
				bfLog.append("\tTruststore location["+sslConfig.getTrustStoreLocation()+"]\n");
				//bfLog.append("\tTruststore password["+sslConfig.getTrustStorePassword()+"]\n");
				bfLog.append("\tTruststore trustManagementAlgorithm["+sslConfig.getTrustManagementAlgorithm()+"]\n");
				String location = null;
				try {
					location = sslConfig.getTrustStoreLocation(); // per debug
					
					KeyStore truststoreParam = null;
					if(sslConfig.getTrustStore()!=null) {
						truststoreParam = sslConfig.getTrustStore();
					}
					else {
						truststoreParam = KeyStore.getInstance(sslConfig.getTrustStoreType()); // JKS,PKCS12,jceks,bks,uber,gkr
						File file = new File(location);
						if(file.exists()) {
							finTrustStore = new FileInputStream(file);
						}
						else {
							finTrustStore = SSLUtilities.class.getResourceAsStream(location);
						}
						if(finTrustStore == null) {
							throw new Exception("Keystore not found");
						}		
						truststoreParam.load(finTrustStore, sslConfig.getTrustStorePassword().toCharArray());
					}
					
					TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(sslConfig.getTrustManagementAlgorithm());
					String trustManagementAlgo = sslConfig.getTrustManagementAlgorithm();
					if(trustManagementAlgo!=null) {
						trustManagementAlgo = trustManagementAlgo.trim();
					}
					if("PKIX".equalsIgnoreCase(trustManagementAlgo)) {
					
						 // create the parameters for the validator
						if(sslConfig.getTrustStoreCRLs()!=null) {
							bfLog.append("\tTruststore CRLs\n");
						}
						else if(sslConfig.getTrustStoreCRLsLocation()!=null) {
							bfLog.append("\tTruststore CRLs["+sslConfig.getTrustStoreCRLsLocation()+"]\n");
						}
						CertPathTrustManagerParameters params = buildCertPathTrustManagerParameters(truststoreParam, sslConfig.getTrustStoreCRLs(), sslConfig.getTrustStoreCRLsLocation());
						trustManagerFactory.init(params);
						
					}
					else {
					
						trustManagerFactory.init(truststoreParam);
						
					}
					tm = trustManagerFactory.getTrustManagers();
					bfLog.append("Gestione truststore effettuata\n");
				}catch(Throwable e) {
					if(location!=null) {
						throw new UtilsException("["+location+"] "+e.getMessage(),e);
					}
					else {
						throw new UtilsException(e.getMessage(),e);
					}
				}
			}
	
			// Creo contesto SSL
			bfLog.append("Init SSLContext type["+sslConfig.getSslType()+"] ...\n");
			sslContext = SSLContext.getInstance(sslConfig.getSslType());
			if(sslConfig.isSecureRandom()) {
				RandomGenerator randomGenerator = null;
				if(sslConfig.getSecureRandomAlgorithm()!=null && !"".equals(sslConfig.getSecureRandomAlgorithm())) {
					bfLog.append("Creazione Secure Random con algoritmo '"+sslConfig.getSecureRandomAlgorithm()+"' ...\n");
					randomGenerator = new RandomGenerator(true, sslConfig.getSecureRandomAlgorithm());
					bfLog.append("Creazione Secure Random con algoritmo '"+sslConfig.getSecureRandomAlgorithm()+"' effettuata\n");
				}
				else {
					bfLog.append("Creazione Secure Random ...\n");
					randomGenerator = new RandomGenerator(true);
					bfLog.append("Creazione Secure Random effettuata\n");
				}
				java.security.SecureRandom secureRandom = (java.security.SecureRandom) randomGenerator.getRandomEngine();
				bfLog.append("Inizializzazione SSLContext con Secure Random ...\n");
				sslContext.init(km, tm, secureRandom);
			}
			else {
				sslContext.init(km, tm, null);
			}
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
	
	public static CertPathTrustManagerParameters buildCertPathTrustManagerParameters(KeyStore truststoreParam,
			CertStore crlStore, String crlLocation) throws Exception {
		
		 // create the parameters for the validator
		
		// Ricreo il truststore altrimenti i certificati presenti come coppia (chiave privata e pubblica non sono trusted)
		KeyStore truststore = KeyStore.getInstance(KeyStore.getDefaultType()); // JKS
		truststore.load(null); // inizializza il truststore
		Enumeration<String> aliases = truststoreParam.aliases();
		while (aliases.hasMoreElements()) {
			String alias = (String) aliases.nextElement();
			//System.out.println("ALIAS: '"+alias+"'");
			truststore.setCertificateEntry(alias, truststoreParam.getCertificate(alias));
		}
		
		PKIXBuilderParameters pkixParams = new PKIXBuilderParameters(truststore, new X509CertSelector());
		pkixParams.setRevocationEnabled(false);
		pkixParams.setDate(DateManager.getDate());
		
		/*
		java.security.cert.PKIXRevocationChecker revocationChecker =
	            (java.security.cert.PKIXRevocationChecker) java.security.cert.CertPathBuilder.getInstance(trustManagementAlgo).getRevocationChecker();
	    pkixParams.addCertPathChecker(revocationChecker);
	    revocationChecker.setOcspResponder(uriResponse);
	    */
				        
		if(crlStore!=null || crlLocation!=null) {
	        CertStore crlsCertstore = null;
	        if(crlStore!=null) {
	        	crlsCertstore = crlStore;
	        }else {
	        	crlsCertstore = _buildCRLCertStore(crlLocation);
	        }
	        pkixParams.addCertStore(crlsCertstore);
	        pkixParams.setRevocationEnabled(true);
		}
		
		return new CertPathTrustManagerParameters(pkixParams);
		
	}
	
	private static CertStore _buildCRLCertStore(String crlsPath) throws Exception {
    	List<byte[]> crlBytes = new ArrayList<>();
    	List<String> crlPathsList = new ArrayList<String>();
		if(crlsPath.contains(",")) {
			String [] tmp = crlsPath.split(",");
			for (String crlPath : tmp) {
				crlPathsList.add(crlPath.trim());
			}
		}
		else {
			crlPathsList.add(crlsPath.trim());
		}
		for (String crlPath : crlPathsList) {
			InputStream isStore = null;
			try{
				File fStore = new File(crlPath);
				if(fStore.exists()){
					isStore = new FileInputStream(fStore);
				}else{
					isStore = SSLUtilities.class.getResourceAsStream(crlPath);
				}
				if(isStore==null){
					throw new Exception("CRL ["+crlPath+"] not found");
				}
				crlBytes.add(Utilities.getAsByteArray(isStore));
			}finally{
				try{
					if(isStore!=null){
						isStore.close();
					}
				}catch(Exception eClose){}
			}
		}
		List<X509CRL> caCrls = new ArrayList<>();
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
    	for (int i = 0; i < crlBytes.size(); i++) {
			byte [] crl = crlBytes.get(i);
			try(ByteArrayInputStream bin = new ByteArrayInputStream(crl)){
				X509CRL caCrl = (X509CRL) certFactory.generateCRL(bin); 
				caCrls.add(caCrl);
			}
			catch(Exception e){
				throw new SecurityException("Error loading CRL '"+crlPathsList.get(i)+"': "+e.getMessage(),e);
			}
		}
		try {
			CollectionCertStoreParameters certStoreParams =
		                new CollectionCertStoreParameters(caCrls);
			return CertStore.getInstance("Collection", certStoreParams);
		}catch(Exception e){
			throw new SecurityException("Build CertStore failed: "+e.getMessage(),e);
		}
	}

	
	public static HostnameVerifier generateHostnameVerifier(SSLConfig sslConfig, StringBuilder bfLog, Logger log, Loader loader) throws UtilsException{
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
	
	
	public static void setSSLContextIntoJavaProperties(SSLConfig sslConfig, StringBuilder bfLog) throws UtilsException{

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
	
	public static String readPeerCertificates(String host, int port) throws UtilsException{
		try {
			
			SSLContext sslContext = SSLContext.getInstance(SSLUtilities.getDefaultProtocol());
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			TrustManagerFactory tmf =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);
            X509TrustManager defaultTrustManager = (X509TrustManager)tmf.getTrustManagers()[0];
            SSLSavingTrustManager   tm = new SSLSavingTrustManager(defaultTrustManager);
            sslContext.init(null, new TrustManager[] {tm}, null);
			
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            
            SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(host, port);
    		sslSocket.setSoTimeout(10000);
    		sslSocket.setWantClientAuth(false);
    		try {
    			sslSocket.startHandshake();
    			sslSocket.close();
    		}catch(Throwable t) {
    			// ignore
    		}
    		//Certificate [] certs = sslSocket.getSession().getPeerCertificates();
    		Certificate [] certs = tm.getPeerCertificates();
    		if(certs == null || certs.length<=0) {
    			throw new Exception("Peer Certificates not found");
    		}
    		StringBuilder sb = new StringBuilder();
    		for (Certificate certificate : certs) {

    			StringWriter sw = new StringWriter();
    			JcaPEMWriter pemWriter = new JcaPEMWriter(sw);
    			pemWriter.writeObject(certificate);
    			pemWriter.close();
    			sw.close();
    			sb.append(sw.toString());

    		}
    		
    		return sb.toString();
            
		}catch(Exception e){
			throw new UtilsException(e.getMessage(), e);
		}
	}
}
