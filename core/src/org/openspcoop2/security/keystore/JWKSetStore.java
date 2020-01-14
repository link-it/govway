/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;

import org.openspcoop2.security.SecurityException;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.resources.Charset;

/**
 * JWKSetStore
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JWKSetStore implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String jwkSetPath;
	private String jwkSetContent;

	private transient JWKSet jwkSet;

	@Override
	public String toString() {
		StringBuffer bf = new StringBuffer();
		bf.append("JWKSetStore (").append(this.jwkSetPath).append(") ");
		return bf.toString();
	}
	
	public JWKSetStore(String path) throws SecurityException{

		this.jwkSetPath = path;
				
		InputStream isStore = null;
		try{
			if(this.jwkSetPath==null){
				throw new Exception("PropertyFilePath per lo Store non indicato");
			}
			
			File fStore = new File(this.jwkSetPath);
			if(fStore.exists()){
				isStore = new FileInputStream(fStore);
			}else{
				isStore = MerlinTruststore.class.getResourceAsStream(this.jwkSetPath);
				if(isStore==null){
					isStore = MerlinTruststore.class.getResourceAsStream("/"+this.jwkSetPath);
				}
				if(isStore==null){
					throw new Exception("Store ["+this.jwkSetPath+"] not found");
				}
			}
			
			this.jwkSetContent = Utilities.getAsString(isStore, Charset.UTF_8.getValue());
			
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

	public String getJwkSetPath() {
		return this.jwkSetPath;
	}
	public String getJwkSetContent() {
		return this.jwkSetContent;
	}
	
	public JWKSet getJwkSet() {
		if(this.jwkSet==null) {
			initialize();
		}
		return this.jwkSet;
	}
	private synchronized void initialize() {
		if(this.jwkSet==null) {
			this.jwkSet = new JWKSet(this.jwkSetContent);
		}
	}
}
