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

package org.openspcoop2.security.keystore;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.cert.CertStore;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509CRL;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.security.SecurityException;

/**
 * CRLCertstore
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CRLCertstore {

	private CertificateFactory certFactory = null;
	private X509CRL caCrl = null;
	private CertStore certStore = null;
	
	public CRLCertstore(String crlPath) throws SecurityException{
		
		InputStream isStore = null;
		try{
			if(crlPath==null){
				throw new Exception("crlPath non indicato");
			}
			
			File fStore = new File(crlPath);
			if(fStore.exists()){
				isStore = new FileInputStream(fStore);
			}else{
				isStore = CRLCertstore.class.getResourceAsStream("/"+crlPath);
				if(isStore==null){
					throw new Exception("Store ["+crlPath+"] not found");
				}
			}
			
			// create a X509 certificate factory for later use
	        this.certFactory = CertificateFactory.getInstance("X.509");
			
	        // Carica CRL
			this.caCrl = (X509CRL) this.certFactory.generateCRL(isStore); 
			List<X509CRL> crlList = new ArrayList<X509CRL>();
			crlList.add(this.caCrl);
			CollectionCertStoreParameters certStoreParams =
		                new CollectionCertStoreParameters(crlList);
			this.certStore =
		                CertStore.getInstance("Collection", certStoreParams);
			
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}finally{
			try{
				if(isStore!=null){
					isStore.close();
				}
			}catch(Exception eClose){}
		}
		
	}

	public CertificateFactory getCertFactory() {
		return this.certFactory;
	}

	public X509CRL getCaCrl() {
		return this.caCrl;
	}

	public CertStore getCertStore() {
		return this.certStore;
	}
	
}
