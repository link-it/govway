/*
 * OpenSPCoop - Customizable API Gateway 
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
import java.security.KeyStore;
import java.util.Properties;

import org.openspcoop2.security.SecurityException;

/**
 * MerlinTruststore
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MerlinTruststore {

	private KeyStore ks = null;
	private String tipoStore = null;
	private String pathStore = null;
	private String passwordStore = null;
	
	public MerlinTruststore(String propertyFilePath) throws SecurityException{
		
		InputStream isStore = null;
		try{
			if(propertyFilePath==null){
				throw new Exception("PropertyFilePath per lo Store non indicato");
			}
			
			File fStore = new File(propertyFilePath);
			if(fStore.exists()){
				isStore = new FileInputStream(fStore);
			}else{
				isStore = MerlinTruststore.class.getResourceAsStream("/"+propertyFilePath);
				if(isStore==null){
					throw new Exception("Store ["+propertyFilePath+"] not found");
				}
			}
			Properties propStore = new Properties();
			propStore.load(isStore);
			isStore.close();
			
			this.tipoStore = propStore.getProperty(KeystoreConstants.PROPERTY_KEYSTORE_TYPE);
			if(this.tipoStore!=null){
				this.tipoStore = this.tipoStore.trim();
			}else{
				this.tipoStore = KeyStore.getDefaultType();
			}
			
			this.pathStore = propStore.getProperty(KeystoreConstants.PROPERTY_KEYSTORE_PATH);
			
			this.passwordStore = propStore.getProperty(KeystoreConstants.PROPERTY_KEYSTORE_PASSWORD);
			
			init();
			
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
		
	public MerlinTruststore(String pathStore,String tipoStore,String passwordStore) throws SecurityException{
		
		this.pathStore = pathStore;
		this.tipoStore = tipoStore;
		this.passwordStore = passwordStore;
		
		init();
		
	}
	
	private void init() throws SecurityException{
		try{
			if(this.pathStore==null){
				throw new Exception("Path per lo Store non indicato");
			}
			if(this.tipoStore==null){
				throw new Exception("Tipo dello Store non indicato");
			}
			if(this.passwordStore==null){
				throw new Exception("Password dello Store non indicata");
			}
			
			File fPathStore = new File(this.pathStore);
			if(fPathStore.exists()==false){
				throw new Exception("keystore ["+fPathStore.getAbsolutePath()+"] not found");
			}
			FileInputStream finStore = new FileInputStream(fPathStore);
			
			this.ks = KeyStore.getInstance(this.tipoStore);
			this.ks.load(finStore, this.passwordStore.toCharArray());
			finStore.close();
			FixTrustAnchorsNotEmpty.addCertificate(this.ks);
			
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}
	
	
	public KeyStore getTrustStore() throws SecurityException {
		try{
			return this.ks;
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}

}
