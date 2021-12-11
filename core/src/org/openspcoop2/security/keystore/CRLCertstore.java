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

package org.openspcoop2.security.keystore;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.security.cert.CertStore;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509CRL;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.security.SecurityException;
import org.openspcoop2.utils.Utilities;

/**
 * CRLCertstore
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CRLCertstore implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<byte []> crlBytes = null;
	private List<String> crlPaths = null;
	
	private transient CertificateFactory certFactory = null;
	private transient List<X509CRL> caCrls = null;
	private transient CertStore certStore = null;
	
	@Override
	public String toString() {
		StringBuilder bf = new StringBuilder();
		bf.append("CRLCertstore (");
		boolean first = true;
		if(this.crlPaths!=null) {
			for (String crlPath : this.crlPaths) {
				if(!first) {
					bf.append(", ");
				}
				bf.append(crlPath);	
				first=false;
			}
		}
		else {
			bf.append("Nessuna crl definita");
		}
		bf.append(")");
		return bf.toString();
	}
	
	public CRLCertstore(String crlPaths) throws SecurityException {
		List<String> crlPathsList = new ArrayList<String>();
		if(crlPaths.contains(",")) {
			String [] tmp = crlPaths.split(",");
			for (String crlPath : tmp) {
				crlPathsList.add(crlPath.trim());
			}
		}
		else {
			crlPathsList.add(crlPaths.trim());
		}
		this._init(crlPathsList);
	}
	public CRLCertstore(List<String> crlPaths) throws SecurityException{
		this._init(crlPaths);
	}
	
	private void _init(List<String> crlPaths) throws SecurityException{
		
		try{
			if(crlPaths==null || crlPaths.isEmpty()){
				throw new Exception("crlPaths non indicato");
			}
		
			this.crlPaths = crlPaths;
			this.crlBytes = new ArrayList<>();
			
			for (String crlPath : crlPaths) {

				InputStream isStore = null;
				try{
					File fStore = new File(crlPath);
					if(fStore.exists()){
						isStore = new FileInputStream(fStore);
					}else{
						isStore = CRLCertstore.class.getResourceAsStream(crlPath);
						if(isStore==null){
							isStore = CRLCertstore.class.getResourceAsStream("/"+crlPath);
						}
						if(isStore==null){
							throw new Exception("Store ["+crlPath+"] not found");
						}
					}
					
					byte[] crlBytes = Utilities.getAsByteArray(isStore);
					this.crlBytes.add(crlBytes);
				}finally{
					try{
						if(isStore!=null){
							isStore.close();
						}
					}catch(Exception eClose){}
				}
			}
			

			this.initCRL();
			
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
		
	}
	
	private void checkInit() throws SecurityException{
		if(this.caCrls==null) {
			this.initCRL();
		}
	}
	private synchronized void initCRL() throws SecurityException{
		if(this.caCrls==null) {
			
			// create a X509 certificate factory for later use
			try {
				this.certFactory = org.openspcoop2.utils.certificate.CertificateFactory.getCertificateFactory();
			}catch(Exception e){
				throw new SecurityException("Error getInstance CertificateFactory: "+e.getMessage(),e);
			}
			
			this.caCrls = new ArrayList<>();
			for (int i = 0; i < this.crlBytes.size(); i++) {
				byte [] crl = this.crlBytes.get(i);
				try(ByteArrayInputStream bin = new ByteArrayInputStream(crl)){
					X509CRL caCrl = (X509CRL) this.certFactory.generateCRL(bin); 
					this.caCrls.add(caCrl);
				}
				catch(Exception e){
					throw new SecurityException("Error loading CRL '"+this.crlPaths.get(i)+"': "+e.getMessage(),e);
				}
			}
			
			try {
				CollectionCertStoreParameters certStoreParams =
			                new CollectionCertStoreParameters(this.caCrls);
				this.certStore =
			                CertStore.getInstance("Collection", certStoreParams);
			}catch(Exception e){
				throw new SecurityException("Build CertStore failed: "+e.getMessage(),e);
			}
		}
	}
	

	public CertificateFactory getCertFactory() throws SecurityException {
		this.checkInit(); // per ripristino da Serializable
		return this.certFactory;
	}

	public List<X509CRL> getCaCrls() throws SecurityException {
		this.checkInit(); // per ripristino da Serializable
		return this.caCrls;
	}

	public CertStore getCertStore() throws SecurityException {
		this.checkInit(); // per ripristino da Serializable
		return this.certStore;
	}
	
}
