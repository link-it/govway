/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.byok.BYOKConfig;
import org.openspcoop2.utils.certificate.byok.BYOKInstance;
import org.openspcoop2.utils.certificate.byok.BYOKMode;
import org.openspcoop2.utils.certificate.byok.BYOKRemoteUtils;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * BYOKStore
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BYOKStore implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private byte[] storeBytes;
	
	private BYOKConfig config;
	private String key;
	
	@Override
	public String toString() {
		StringBuilder bf = new StringBuilder();
		bf.append("BYOKStore (").append(this.config.getLabel()).append(") ").append(this.key);
		return bf.toString();
	}
	
	public BYOKStore(String key,
			BYOKInstance instance) throws SecurityException{
		
		this.key = key;
		
		try{
			if(instance==null){
				throw new SecurityException("Instance non fornita");
			}
			this.config = instance.getConfig();
			
			if(instance.getHttpRequest()!=null) {
				remoteProcess(instance);
			}
			else {
				BYOKLocalEncrypt localEncrypt = new BYOKLocalEncrypt();
				if(BYOKMode.WRAP.equals(this.config.getMode())) {
					this.storeBytes = localEncrypt.wrap(instance.getLocalConfigResolved(), instance.getLocalKey()).getBytes();
				}
				else {
					this.storeBytes = localEncrypt.unwrap(instance.getLocalConfigResolved(), instance.getLocalKey());
				}
			}

		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
		
	}
	private void remoteProcess(BYOKInstance instance) throws UtilsException, SecurityException{
		HttpResponse httpResponse = HttpUtilities.httpInvoke(instance.getHttpRequest());
		if(httpResponse==null || httpResponse.getContent()==null) {
			throw new SecurityException("Store '"+this.config.getLabel()+"' (endpoint:"+instance.getHttpRequest().getUrl()+") unavailable");
		}
		if(httpResponse.getResultHTTPOperation()!=200) {
			throw new SecurityException("Retrieve store '"+this.config.getLabel()+"' (endpoint:"+instance.getHttpRequest().getUrl()+") failed (returnCode:"+httpResponse.getResultHTTPOperation()+")");
		}
		this.storeBytes = httpResponse.getContent();
		
		if(this.storeBytes!=null && this.storeBytes.length>0) {
			this.storeBytes = BYOKRemoteUtils.normalizeResponse(instance, this.storeBytes, LoggerWrapperFactory.getLogger(BYOKStore.class));
		}
	}
	
	public byte[] getStoreBytes() {
		return this.storeBytes;
	}

}
