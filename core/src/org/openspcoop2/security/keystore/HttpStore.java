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

import java.io.InputStream;
import java.io.Serializable;

import org.openspcoop2.security.SecurityException;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * HttpStore
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpStore implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private byte[] storeBytes;
	private String endpoint;
	private MerlinTruststore trustStoreSsl = null;
	private CRLCertstore crlTrustStoreSsl = null;
	private int connectionTimeout = HttpUtilities.HTTP_CONNECTION_TIMEOUT;
	private int readTimeout = HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT;
	
	@Override
	public String toString() {
		StringBuffer bf = new StringBuffer();
		bf.append("HttpStore (").append(this.endpoint).append(") ");
		if(this.trustStoreSsl!=null) {
			bf.append(this.trustStoreSsl.toString());
		}
		if(this.crlTrustStoreSsl!=null) {
			bf.append(this.crlTrustStoreSsl.toString());
		}
		return bf.toString();
	}
	
	public HttpStore(String endpoint) throws SecurityException{
		this(endpoint,
				null, null, 
				null, null);
	}
	public HttpStore(String endpoint, 
			Integer connectionTimeout, Integer readTimeout) throws SecurityException{
		this(endpoint,
				connectionTimeout, readTimeout, 
				null, null);
	}
	public HttpStore(String endpoint, 
			Integer connectionTimeout, Integer readTimeout,
			MerlinTruststore trustStoreSsl) throws SecurityException{
		this(endpoint,
				connectionTimeout, readTimeout, 
				trustStoreSsl, null);
	}
	public HttpStore(String endpoint, 
			MerlinTruststore trustStoreSsl) throws SecurityException{
		this(endpoint,
				null, null, 
				trustStoreSsl, null);
	}
	public HttpStore(String endpoint, 
			MerlinTruststore trustStoreSsl, CRLCertstore crlTrustStoreSsl) throws SecurityException{
		this(endpoint,
				null, null, 
				trustStoreSsl, crlTrustStoreSsl);
	}
	public HttpStore(String endpoint, 
			Integer connectionTimeout, Integer readTimeout,
			MerlinTruststore trustStoreSsl, CRLCertstore crlTrustStoreSsl) throws SecurityException{
		
		this.endpoint = endpoint;
		if(connectionTimeout!=null) {
			this.connectionTimeout = connectionTimeout.intValue();
		}
		if(readTimeout!=null) {
			this.connectionTimeout = readTimeout.intValue();
		}
		this.trustStoreSsl = trustStoreSsl;
		this.crlTrustStoreSsl = crlTrustStoreSsl;
		
		InputStream isStore = null;
		try{
			if(endpoint==null){
				throw new Exception("Endpoint per lo Store non indicato");
			}
			
			HttpResponse httpResponse = null;
			if( (this.endpoint.startsWith("https:") && this.trustStoreSsl==null) || this.endpoint.startsWith("http:") ) {
				//System.out.println("http");
				httpResponse = HttpUtilities.getHTTPResponse(this.endpoint,this.readTimeout, this.connectionTimeout);
			}
			else {
				//System.out.println("https");
				httpResponse = HttpUtilities.getHTTPSResponse(this.endpoint, this.readTimeout, this.connectionTimeout, 
						this.trustStoreSsl.getTrustStore(), 
						this.crlTrustStoreSsl!=null ? this.crlTrustStoreSsl.getCertStore() : null);
			}
			if(httpResponse==null || httpResponse.getContent()==null) {
				throw new Exception("Store '"+this.endpoint+"' unavailable");
			}
			if(httpResponse.getResultHTTPOperation()!=200) {
				throw new Exception("Retrieve store '"+this.endpoint+"' failed (returnCode:"+httpResponse.getResultHTTPOperation()+")");
			}
			this.storeBytes = httpResponse.getContent();

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
	
	public byte[] getStoreBytes() {
		return this.storeBytes;
	}

}
