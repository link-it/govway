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

package org.openspcoop2.utils.resources;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.openspcoop2.utils.UtilsException;

/**
 * SSLUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SSLUtilities {

	public static SSLContext generateSSLContext(SSLConfig sslConfig, StringBuffer bfLog) throws UtilsException{

		// Gestione https
		SSLContext sslContext = null;

		bfLog.append("Creo contesto SSL...\n");
		KeyManager[] km = null;
		TrustManager[] tm = null;

		FileInputStream finKeyStore = null;
		FileInputStream finTrustStore = null;
		try{
		
			// Autenticazione CLIENT
			if(sslConfig.getKeyStoreLocation()!=null){
				bfLog.append("Gestione keystore...\n");
				KeyStore keystore = KeyStore.getInstance(sslConfig.getKeyStoreType()); // JKS,PKCS12,jceks,bks,uber,gkr
				finKeyStore = new FileInputStream(sslConfig.getKeyStoreLocation());
				keystore.load(finKeyStore, sslConfig.getKeyStorePassword().toCharArray());
				KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(sslConfig.getKeyManagementAlgorithm());
				keyManagerFactory.init(keystore, sslConfig.getKeyPassword().toCharArray());
				km = keyManagerFactory.getKeyManagers();
				bfLog.append("Gestione keystore effettuata\n");
			}
	
	
			// Autenticazione SERVER
			if(sslConfig.getTrustStoreLocation()!=null){
				bfLog.append("Gestione truststore...\n");
				KeyStore truststore = KeyStore.getInstance(sslConfig.getTrustStoreType()); // JKS,PKCS12,jceks,bks,uber,gkr
				finTrustStore = new FileInputStream(sslConfig.getTrustStoreLocation());
				truststore.load(finTrustStore, sslConfig.getTrustStorePassword().toCharArray());
				TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(sslConfig.getTrustManagementAlgorithm());
				trustManagerFactory.init(truststore);
				tm = trustManagerFactory.getTrustManagers();
				bfLog.append("Gestione truststore effettuata\n");
			}
	
			// Creo contesto SSL
			sslContext = SSLContext.getInstance(sslConfig.getSslType());
			sslContext.init(km, tm, null);	
			
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
