/*
 * AdroitLogic UltraESB Enterprise Service Bus
 *
 * Copyright (c) 2010-2012 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 *
 * GNU Affero General Public License Usage
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program (See LICENSE-AGPL.TXT).
 * If not, see http://www.gnu.org/licenses/agpl-3.0.html
 *
 * Commercial Usage
 *
 * Licensees holding valid UltraESB Commercial licenses may use this file in accordance with the UltraESB Commercial
 * License Agreement provided with the Software or, alternatively, in accordance with the terms contained in a written
 * agreement between you and AdroitLogic.
 *
 * If you are unsure which license is appropriate for your use, or have questions regarding the use of this file,
 * please contact AdroitLogic at info@adroitlogic.com
 */
/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: 
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, 
 * either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope 
 * that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openspcoop2.security.message.soapbox;

import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertPathValidator;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;

import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.CRLCertstore;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.utils.date.DateManager;

/**
 * SecurityConfig
 *
 * @author Andrea Poli <apoli@link.it>
 * @author Giovanni Bussu <bussu@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SecurityConfig extends org.adroitlogic.soapbox.SecurityConfig {

    
	private boolean symmetricSharedKey = false;
	public boolean isSymmetricSharedKey() {
		return this.symmetricSharedKey;
	}
	public void setSymmetricSharedKey(boolean symmetricSharedKey) {
		this.symmetricSharedKey = symmetricSharedKey;
	}
	
	
	private KeyStore identityStore = null;
	
	public SecurityConfig(KeyStore identityStore, KeyStore trustStore, Map<String, String> keyPasswords)
	        throws KeyStoreException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, CertificateException {
		this(identityStore, trustStore, keyPasswords, null);
	}
    public SecurityConfig(KeyStore identityStore, KeyStore trustStore, Map<String, String> keyPasswords,String crlPath)
        throws KeyStoreException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, CertificateException {

    	super(identityStore, trustStore, keyPasswords);

    	this.identityStore = identityStore;
    	
        // create the parameters for the validator
        this.validatorParams = new PKIXParameters(trustStore);
        // disable CRL checking since we are not supplying any CRLs
        this.validatorParams.setRevocationEnabled(false);
       	// create the validator and validate the path
        this.certPathValidator = CertPathValidator.getInstance(CertPathValidator.getDefaultType());
        // create a X509 certificate factory for later use
        this.certFactory = CertificateFactory.getInstance("X.509");

        if(crlPath!=null){
	        try{
	        	CRLCertstore crlCertstore = GestoreKeystoreCache.getCRLCertstore(crlPath);
	        	this.validatorParams.addCertStore(crlCertstore.getCertStore());
		        this.validatorParams.setRevocationEnabled(true);
		        this.validatorParams.setDate(DateManager.getDate());
		        //System.out.println("INIT ["+crlPath+"]");
	        }catch(Exception e){
	        	throw new CertificateException(e.getMessage(),e);
	        }
        }
        
        /*try{
        	this.validatorParams.setRevocationEnabled(true);
        	Security.setProperty("ocsp.enable", "true");
        	Security.setProperty("ocsp.responderURL","http://siipki.acquirenteunico.it/ocsp");
        	//System.out.println("SETTING [http://siipki.acquirenteunico.it/ocsp]");
	     }catch(Exception e){
        	System.out.println(e.getMessage());
        	e.printStackTrace(System.out);
        }*/
        
    }


    public Key getSymmetricKey(String alias) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException{  
    	String password = this.getKeyPassword(alias);
    	Key key = this.identityStore.getKey(alias, password.toCharArray());
    	return key;
    }


    private final WeakHashMap<X509Certificate[], Boolean> validCerts = new WeakHashMap<X509Certificate[], Boolean>();
    private final CertPathValidator certPathValidator;
    private final CertificateFactory certFactory;
    private final PKIXParameters validatorParams;
    
   public void validateX509Certificate(X509Certificate[] certs) throws SecurityException {

        Boolean validity = this.validCerts.get(certs);
        if (validity == null) {
            synchronized (this) {
            	
            	StringBuffer bf = new StringBuffer();
            	if(certs!=null){
	            	for (int i = 0; i < certs.length; i++) {
	            		if(i>0){
	            			bf.append(" ");
	            		}
	            		bf.append("(Certificate["+i+"]: DN="+certs[i].getSubjectDN().toString()+")");
					}
            	}
            	bf.append(" Does not validate against the trusted CA certificates ");
            	
                try {
                	//System.out.println("Validate...");
                	this.certPathValidator.validate(this.certFactory.generateCertPath(Arrays.asList(certs)), this.validatorParams);
                    this.validCerts.put(certs, Boolean.TRUE);
                    //System.out.println("Validate OK");
                } catch (CertificateNotYetValidException e) {
                	this.validCerts.put(certs, Boolean.FALSE);
               	  	throw new SecurityException(bf.toString()+"[Certificate is not yet valid] - " + e.getMessage());
                } catch (CertificateExpiredException e) {
                	this.validCerts.put(certs, Boolean.FALSE);
               	  	throw new SecurityException(bf.toString()+"[Certificate has expired] - " + e.getMessage());
                } catch (Exception e) {
                	  this.validCerts.put(certs, Boolean.FALSE);
                	  throw new SecurityException(bf.toString()+"[Errore generico] - " + e.getMessage());
                	//return false;
                }
            }
        } else {
        	 throw new SecurityException("Certificate not found in validCerts");
        }
    }

}
