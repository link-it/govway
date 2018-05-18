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


package org.openspcoop2.utils.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Enumeration;

import org.openspcoop2.utils.UtilsException;

/**	
 * Keystore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class KeyStore {

	private java.security.KeyStore keystore;
	
	public KeyStore(String keystorePath,String passwordKeystore) throws UtilsException{
		this(keystorePath,"JKS",passwordKeystore);
	}
	public KeyStore(String keystorePath,String tipoKeystore, String passwordKeystore) throws UtilsException{
		this(new File(keystorePath),tipoKeystore,passwordKeystore);
	}
	public KeyStore(File keystorePath,String passwordKeystore) throws UtilsException{
		this(keystorePath,"JKS",passwordKeystore);
	}
	public KeyStore(File keystorePath,String tipoKeystore, String passwordKeystore) throws UtilsException{
		
		if(keystorePath.exists()==false){
			throw new UtilsException("Keystore ["+keystorePath+"] not exists");
		}
		if(keystorePath.canRead()==false){
			throw new UtilsException("Keystore ["+keystorePath+"] cannot read");
		}
		
		InputStream fin = null;
		try{
			java.security.KeyStore keystore = java.security.KeyStore.getInstance(tipoKeystore);
			fin = new FileInputStream(keystorePath);
			keystore.load(fin, passwordKeystore.toCharArray());
			this.keystore = keystore;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
		finally{
			try{
				if(fin!=null){
					fin.close();
				}
			}catch(Exception eClose){}
		}
		
	}
	public KeyStore(java.security.KeyStore keystore) {
		this.keystore = keystore;
	}
	
	public PrivateKey getPrivateKey(String alias,String passwordPrivateKey) throws UtilsException{
		try{
			return (PrivateKey) this.keystore.getKey(alias, passwordPrivateKey.toCharArray());
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}	
	}

	public Certificate getCertificate() throws UtilsException{
		try{
			Enumeration<String> aliases = this.keystore.aliases();
			Certificate cer = null;
			while (aliases.hasMoreElements()) {
				String alias = (String) aliases.nextElement();
				if(cer!=null){
					throw new Exception("More than one certificate, use alias");
				}
				cer = this.keystore.getCertificate(alias);
			}
			return cer;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}	
	}
	public Certificate getCertificate(String alias) throws UtilsException{
		try{
			return this.keystore.getCertificate(alias);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}	
	}
	
	public java.security.KeyStore getKeystore() {
		return this.keystore;
	}
}
