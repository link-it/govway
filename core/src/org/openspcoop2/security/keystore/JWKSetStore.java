/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import java.io.Serializable;

import org.openspcoop2.security.SecurityException;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.certificate.byok.BYOKRequestParams;
import org.openspcoop2.utils.resources.Charset;

/**
 * JWKSetStore
 *
 * @author Andrea Poli (apoli@link.it)
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
		StringBuilder bf = new StringBuilder();
		bf.append("JWKSetStore (").append(this.jwkSetPath).append(") ");
		return bf.toString();
	}
	
	public JWKSetStore(String path) throws SecurityException{
		this(path, null);
	}
	public JWKSetStore(String path, BYOKRequestParams requestParams) throws SecurityException{

		this.jwkSetPath = path;
						
		byte [] archive = StoreUtils.readContent("FilePath", this.jwkSetPath);
		try {
			archive = readBytes(archive, requestParams);
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
		try {
			this.jwkSetContent = new String(archive, Charset.UTF_8.getValue());
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
		
	}
	
	public JWKSetStore(byte[] archive) throws SecurityException{
		this(archive, null);
	}
	public JWKSetStore(byte[] archiveParam, BYOKRequestParams requestParams) throws SecurityException{

		try{
			if(archiveParam==null){
				throw new SecurityException("Store non indicato");
			}
			
			byte [] archive = readBytes(archiveParam, requestParams);
			this.jwkSetContent = new String(archive, Charset.UTF_8.getValue());
			
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
		
	}

	private byte[] readBytes(byte[] archive, BYOKRequestParams requestParams) throws SecurityException {
		if(requestParams!=null) {
			return StoreUtils.unwrapBYOK(archive, requestParams);
		}
		return archive;
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
