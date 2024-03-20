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
import org.openspcoop2.utils.transport.http.HttpOptions;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * HttpStore
 *
 * @author Andrea Poli (apoli@link.it)
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
	private Boolean trustAll = null;
	private MerlinTruststore trustStoreSsl = null;
	private CRLCertstore crlTrustStoreSsl = null;
	private int connectionTimeout = HttpUtilities.HTTP_CONNECTION_TIMEOUT;
	private int readTimeout = HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT;
	private HttpOptions [] options;
	
	@Override
	public String toString() {
		StringBuilder bf = new StringBuilder();
		bf.append("HttpStore (").append(this.endpoint).append(")");
		if(this.trustAll!=null) {
			bf.append(" ").append(this.trustAll);
		}
		if(this.trustStoreSsl!=null) {
			bf.append(" ").append(this.trustStoreSsl.toString());
		}
		if(this.crlTrustStoreSsl!=null) {
			bf.append(" ").append(this.crlTrustStoreSsl.getWrappedCRLCertStore()!=null ? this.crlTrustStoreSsl.getWrappedCRLCertStore().toString() : this.crlTrustStoreSsl.toString());
		}
		if(this.options!=null && this.options.length>0) {
			for (HttpOptions httpOptions : this.options) {
				bf.append(" ").append(httpOptions.toString());
			}
		}
		return bf.toString();
	}
	
	public HttpStore(String endpoint,
			HttpOptions ... options) throws SecurityException{
		this(endpoint,
				null, null, 
				null, null,
				options);
	}
	public HttpStore(String endpoint, 
			Integer connectionTimeout, Integer readTimeout,
			HttpOptions ... options) throws SecurityException{
		this(endpoint,
				connectionTimeout, readTimeout, 
				null, null,
				options);
	}
	public HttpStore(String endpoint, 
			Integer connectionTimeout, Integer readTimeout,
			MerlinTruststore trustStoreSsl,
			HttpOptions ... options) throws SecurityException{
		this(endpoint,
				connectionTimeout, readTimeout, 
				trustStoreSsl, null,
				options);
	}
	public HttpStore(String endpoint, 
			MerlinTruststore trustStoreSsl,
			HttpOptions ... options) throws SecurityException{
		this(endpoint,
				null, null, 
				trustStoreSsl, null,
				options);
	}
	public HttpStore(String endpoint, 
			MerlinTruststore trustStoreSsl, CRLCertstore crlTrustStoreSsl,
			HttpOptions ... options) throws SecurityException{
		this(endpoint,
				null, null, 
				trustStoreSsl, crlTrustStoreSsl,
				options);
	}
	public HttpStore(String endpoint, 
			Integer connectionTimeout, Integer readTimeout,
			MerlinTruststore trustStoreSsl, CRLCertstore crlTrustStoreSsl,
			HttpOptions ... options) throws SecurityException{
		this(endpoint, 
				connectionTimeout, readTimeout,
				null,
				trustStoreSsl, crlTrustStoreSsl,
				options);
	}
	public HttpStore(String endpoint, 
			Boolean trustAll,
			HttpOptions ... options) throws SecurityException{
		this(endpoint,
				null, null, 
				trustAll,
				null, null,
				options);
	}
	public HttpStore(String endpoint, 
			Integer connectionTimeout, Integer readTimeout,
			Boolean trustAll,
			HttpOptions ... options) throws SecurityException{
		this(endpoint,
				connectionTimeout, readTimeout, 
				trustAll,
				null, null,
				options);
	}
	
	private HttpStore(String endpoint, 
			Integer connectionTimeout, Integer readTimeout,
			Boolean trustAll,
			MerlinTruststore trustStoreSsl, CRLCertstore crlTrustStoreSsl,
			HttpOptions ... options) throws SecurityException{
		
		this.endpoint = endpoint;
		if(connectionTimeout!=null) {
			this.connectionTimeout = connectionTimeout.intValue();
		}
		if(readTimeout!=null) {
			this.readTimeout = readTimeout.intValue();
		}
		this.trustAll = trustAll;
		this.trustStoreSsl = trustStoreSsl;
		this.crlTrustStoreSsl = crlTrustStoreSsl;
		
		this.options = options;
		
		try{
			if(endpoint==null){
				throw new SecurityException("Endpoint per lo Store non indicato");
			}
			
			HttpResponse httpResponse = null;
			if( 
					(
							this.endpoint.startsWith("https:") && 
							this.trustStoreSsl==null &&
							(this.trustAll==null || !this.trustAll.booleanValue())
					) 
					|| 
					this.endpoint.startsWith("http:") ) {
				httpResponse = HttpUtilities.getHTTPResponse(this.endpoint,this.readTimeout, this.connectionTimeout, this.options);
			}
			else if(this.trustAll!=null) {
				httpResponse = HttpUtilities.getHTTPSResponse_trustAllCerts(this.endpoint, this.readTimeout, this.connectionTimeout, this.options);
			}
			else {
				httpResponse = HttpUtilities.getHTTPSResponse(this.endpoint, this.readTimeout, this.connectionTimeout, 
						this.trustStoreSsl.getTrustStore().getKeystore(), 
						this.crlTrustStoreSsl!=null ? this.crlTrustStoreSsl.getCertStore() : null, 
						this.options);
			}
			if(httpResponse==null || httpResponse.getContent()==null) {
				throw new SecurityException("Store '"+this.endpoint+"' unavailable");
			}
			if(httpResponse.getResultHTTPOperation()!=200) {
				throw new SecurityException("Retrieve store '"+this.endpoint+"' failed (returnCode:"+httpResponse.getResultHTTPOperation()+")");
			}
			this.storeBytes = httpResponse.getContent();

		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
		
	}
	
	public byte[] getStoreBytes() {
		return this.storeBytes;
	}

}
