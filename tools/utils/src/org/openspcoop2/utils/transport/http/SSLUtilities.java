/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
import java.lang.reflect.Method;
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
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.hsm.HSMManager;
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
			SSLSocket socket = null;
			try {
				socket = (SSLSocket)context.getSocketFactory().createSocket();
				String[] protocols = socket.getEnabledProtocols();
				for (int i = 0; i < protocols.length; i++) {
					p.add(protocols[i]);
				}
			}finally {
				try {
					if(socket!=null) {
						socket.close();
					}
				}catch(Throwable t) {
					// ignore
				}
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
			SSLSocket socket = null;
			try {
				socket = (SSLSocket)defaultContext.getSocketFactory().createSocket();
				String[] protocols = socket.getSupportedProtocols();
				for (int i = 0; i < protocols.length; i++) {
					p.add(protocols[i]);
				}
			}finally {
				try {
					if(socket!=null) {
						socket.close();
					}
				}catch(Throwable t) {
					// ignore
				}
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
			SSLSocket socket = null;
			try {
				socket = (SSLSocket)context.getSocketFactory().createSocket();
				String[] cs = socket.getEnabledCipherSuites();
				for (int i = 0; i < cs.length; i++) {
					l.add(cs[i]);
				}
			}finally {
				try {
					if(socket!=null) {
						socket.close();
					}
				}catch(Throwable t) {
					// ignore
				}
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
			SSLSocket socket = null;
			try {
				socket = (SSLSocket)defaultContext.getSocketFactory().createSocket();
				String[] cs = socket.getSupportedCipherSuites();
				for (int i = 0; i < cs.length; i++) {
					l.add(cs[i]);
				}
			}finally {
				try {
					if(socket!=null) {
						socket.close();
					}
				}catch(Throwable t) {
					// ignore
				}
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
	private static synchronized void initTrustAllCertsManager() {
		if(trustAllCertsManager==null) {
			// Create a trust manager that does not validate certificate chains
			trustAllCertsManager = new TrustManager[]{
			    new SSLTrustAllManager()
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
		return generateSSLContext(sslConfig, null, bfLog);
	}
	public static SSLContext generateSSLContext(SSLConfig sslConfig, IOCSPValidator ocspValidator, StringBuilder bfLog) throws UtilsException{

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
				
					boolean hsmKeystore = false;
					HSMManager hsmManager = HSMManager.getInstance();
					if(hsmManager!=null) {
						if(sslConfig.getKeyStore()!=null) {
							hsmKeystore = sslConfig.isKeyStoreHsm();
						}
						else {
							if(sslConfig.getKeyStoreType()!=null && hsmManager.existsKeystoreType(sslConfig.getKeyStoreType())) {
								hsmKeystore = true;
							}
						}
					}
					bfLog.append("\tKeystore HSM["+hsmManager+"]\n");
					
					KeyStore keystore = null;
					KeyStore keystoreParam = null;
					@SuppressWarnings("unused")
					Provider keystoreProvider = null;
					if(sslConfig.getKeyStore()!=null) {
						keystoreParam = sslConfig.getKeyStore();
						if(hsmKeystore) {
							keystoreProvider = keystoreParam.getProvider();
						}
					}
					else {
						if(hsmKeystore) {
							org.openspcoop2.utils.certificate.KeyStore ks = hsmManager.getKeystore(sslConfig.getKeyStoreType());
							if(ks==null) {
								throw new Exception("Keystore not found");
							}
							keystoreParam = ks.getKeystore();
							keystoreProvider = keystoreParam.getProvider();
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
					}
					
					boolean oldMethodKeyAlias = false; // Questo metodo non funzionava con PKCS11
					if(oldMethodKeyAlias && sslConfig.getKeyAlias()!=null) {
						Key key = keystoreParam.getKey(sslConfig.getKeyAlias(), sslConfig.getKeyPassword().toCharArray());
						if(key==null) {
							throw new Exception("Key with alias '"+sslConfig.getKeyAlias()+"' not found");
						}
						if(hsmKeystore) {
							// uso un JKS come tmp
							keystore = KeyStore.getInstance("JKS");
						}
						else {
							keystore = KeyStore.getInstance(sslConfig.getKeyStoreType());
						}
						keystore.load(null); // inizializza il keystore
						keystore.setKeyEntry(sslConfig.getKeyAlias(), key, 
								sslConfig.getKeyPassword().toCharArray(), keystoreParam.getCertificateChain(sslConfig.getKeyAlias()));
					}
					else {
						keystore = keystoreParam;
					}
					
					KeyManagerFactory keyManagerFactory = null;
					// NO: no such algorithm: SunX509 for provider SunPKCS11-xxx
					//if(keystoreProvider!=null) {
					//	keyManagerFactory = KeyManagerFactory.getInstance(sslConfig.getKeyManagementAlgorithm(), keystoreProvider);
					//}
					//else {
					keyManagerFactory = KeyManagerFactory.getInstance(sslConfig.getKeyManagementAlgorithm());
					//}
					
					keyManagerFactory.init(keystore, sslConfig.getKeyPassword().toCharArray());
					km = keyManagerFactory.getKeyManagers();
					if(!oldMethodKeyAlias && sslConfig.getKeyAlias()!=null) {
						if(km!=null && km.length>0 && km[0]!=null && km[0] instanceof X509KeyManager) {
							
							String alias = sslConfig.getKeyAlias();
							
							// Fix case insensitive
							Enumeration<String> enAliases = keystore.aliases();
							if(enAliases!=null) {
								while (enAliases.hasMoreElements()) {
									String a = (String) enAliases.nextElement();
									if(a.equalsIgnoreCase(alias)) {
										alias = a; // uso quello presente nel keystore
										break;
									}
								}
							}
							
							X509KeyManager wrapperX509KeyManager = new SSLX509ManagerForcedClientAlias(alias, (X509KeyManager)km[0] );
							km[0] = wrapperX509KeyManager;
						}
					}
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
			KeyStore truststoreParam = null;
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
					
					boolean hsmTruststore = false;
					HSMManager hsmManager = HSMManager.getInstance();
					if(hsmManager!=null) {
						if(sslConfig.getTrustStore()!=null) {
							hsmTruststore = sslConfig.isTrustStoreHsm();
						}
						else {
							if(sslConfig.getTrustStoreType()!=null && hsmManager.existsKeystoreType(sslConfig.getTrustStoreType())) {
								hsmTruststore = true;
							}
						}
					}
					bfLog.append("\tTruststore HSM["+hsmTruststore+"]\n");
					
					@SuppressWarnings("unused")
					Provider truststoreProvider = null;
					if(sslConfig.getTrustStore()!=null) {
						truststoreParam = sslConfig.getTrustStore();
						if(hsmTruststore) {
							truststoreProvider = truststoreParam.getProvider();
						}
					}
					else {
						if(hsmTruststore) {
							org.openspcoop2.utils.certificate.KeyStore ks = hsmManager.getKeystore(sslConfig.getTrustStoreType());
							if(ks==null) {
								throw new Exception("Keystore not found");
							}
							truststoreParam = ks.getKeystore();
							truststoreProvider = truststoreParam.getProvider();
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
					}
					
					TrustManagerFactory trustManagerFactory = null;
					// NO: no such algorithm: PKIX for provider SunPKCS11-xxx
					//if(truststoreProvider!=null) {
					//	trustManagerFactory = TrustManagerFactory.getInstance(sslConfig.getTrustManagementAlgorithm(), truststoreProvider);
					//}
					//else {
					trustManagerFactory = TrustManagerFactory.getInstance(sslConfig.getTrustManagementAlgorithm());
					//}
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
						
						Provider sigProvider = null;
						CertPathTrustManagerParameters params = buildCertPathTrustManagerParameters(truststoreParam, sslConfig.getTrustStoreCRLs(), sslConfig.getTrustStoreCRLsLocation(), sigProvider);
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
			
			if(ocspValidator!=null) {
				if(ocspValidator.getTrustStore()==null && truststoreParam!=null) {
					ocspValidator.setTrustStore(new org.openspcoop2.utils.certificate.KeyStore(truststoreParam));
				}
				tm = OCSPTrustManager.wrap(tm, ocspValidator);
				ocspValidator.setOCSPTrustManager(OCSPTrustManager.read(tm));
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
			
			
			if(sslContext.getClientSessionContext()!=null) {
				bfLog.append("ClientSessionContext size:").
					append(sslContext.getClientSessionContext().getSessionCacheSize()).
					append(" timeout:").
					append(sslContext.getClientSessionContext().getSessionTimeout()).append("\n");
			}
			if(sslContext.getServerSessionContext()!=null) {
				bfLog.append("ServerSessionContext size:").
					append(sslContext.getServerSessionContext().getSessionCacheSize()).
					append(" timeout:").
					append(sslContext.getServerSessionContext().getSessionTimeout()).append("\n");
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
			}catch(Exception e){
				// close
			}
		}
	}
	
	public static CertPathTrustManagerParameters buildCertPathTrustManagerParameters(KeyStore truststoreParam,
			CertStore crlStore, String crlLocation, Provider provider) throws Exception {
		
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
		
		if(provider!=null) {
			pkixParams.setSigProvider(provider.getName());
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
				}catch(Exception eClose){
					// close
				}
			}
		}
		List<X509CRL> caCrls = new ArrayList<>();
		CertificateFactory certFactory = org.openspcoop2.utils.certificate.CertificateFactory.getCertificateFactory();
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
            
            SSLSocket sslSocket = null;
			try {
				sslSocket = (SSLSocket) sslSocketFactory.createSocket(host, port);
	    		sslSocket.setSoTimeout(10000);
	    		sslSocket.setWantClientAuth(false);
	    		try {
	    			sslSocket.startHandshake();
	    		}catch(Throwable t) {
	    			// ignore
	    		}
			}finally {
				try {
					if(sslSocket!=null) {
						sslSocket.close();
					}
				}catch(Throwable t) {
					// ignore
				}
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
	
	public static java.security.cert.X509Certificate[] readCertificatesFromUndertowServlet(HttpServletRequest req) {
		return readCertificatesFromUndertowServlet(req, null);
	}
	public static java.security.cert.X509Certificate[] readCertificatesFromUndertowServlet(HttpServletRequest reqParam, Logger log) {
		try {
			HttpServletRequest req = reqParam;
			if(reqParam instanceof WrappedHttpServletRequest) {
				req = ((WrappedHttpServletRequest)reqParam).httpServletRequest;
			}
			
			String undertow = "io.undertow.servlet.spec.HttpServletRequestImpl";
			String actual = req.getClass().getName() + "";
			boolean isUndertow = undertow.equals(actual);
			
			if(isUndertow) {
				
				// io/undertow/server/HttpServerExchange.java
				Method mExchange = req.getClass().getMethod("getExchange");
				if(mExchange!=null) {
					Object exchange = mExchange.invoke(req);
					if(exchange!=null) {
						//System.out.println("Exchange ["+exchange.getClass().getName()+"]");
						
						// io/undertow/server/ServerConnection.java
						Method mConnection = exchange.getClass().getMethod("getConnection");
						if(mConnection!=null) {
							Object connection = mConnection.invoke(exchange);
							if(connection!=null) {
								//System.out.println("Connection ["+connection.getClass().getName()+"]");
								
								// io/undertow/server/SSLSessionInfo.java
								Method mSSLSessionInfo = connection.getClass().getMethod("getSslSessionInfo");
								if(mSSLSessionInfo!=null) {
									Object sslSessionInfo = mSSLSessionInfo.invoke(connection);
									if(sslSessionInfo!=null) {
										//System.out.println("sslSessionInfo ["+sslSessionInfo.getClass().getName()+"]");
										
										Method mPeerCertificates = sslSessionInfo.getClass().getMethod("getPeerCertificates");
										if(mPeerCertificates!=null) {
											try {
												Object peerCertificates = mPeerCertificates.invoke(sslSessionInfo);
												if(peerCertificates instanceof java.security.cert.X509Certificate[]) {
													//System.out.println("FOUND!");
													return (java.security.cert.X509Certificate[]) peerCertificates;
												}
											}catch(Throwable t) {
												// a volte lancia eccezione null .... 
												if(Utilities.existsInnerException(t, "io.undertow.server.RenegotiationRequiredException")) {
													return null; // certificati non sono disponibili a causa di una rinegoziazione
												}
												else {
													if(log!=null) {
														log.error("readCertificatesFromUndertowServlet 'get peer certificates' failed: "+t.getMessage(),t);
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}catch(Throwable t) {
			if(log!=null) {
				log.error("readCertificatesFromUndertowServlet failed: "+t.getMessage(),t);
			}
		}
		return null;
	}
}
