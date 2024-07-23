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
import org.openspcoop2.utils.certificate.remote.IRemoteStoreProvider;
import org.openspcoop2.utils.certificate.remote.RemoteStoreConfig;

/**
 * RemoteStoreClientInfo
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RemoteStoreClientInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String remoteStoreName;
	private String keyId;
	private String clientId;
	
	private org.openspcoop2.utils.certificate.remote.RemoteStoreClientInfo clientInfo;
	
	public RemoteStoreClientInfo(String keyId, String clientId, RemoteStoreConfig remoteStoreConfig, IRemoteStoreProvider provider, org.openspcoop2.utils.Map<Object> context) throws SecurityException {
		try {
			this.keyId = keyId;
			if(keyId==null) {
				throw new SecurityException("KeyId undefined");
			}
			this.clientId = clientId;
			if(clientId==null) {
				throw new SecurityException("ClientId undefined");
			}
			if(remoteStoreConfig==null) {
				throw new SecurityException("RemoteStoreConfig undefined");
			}
			this.remoteStoreName = remoteStoreConfig.getStoreName();
			if(this.remoteStoreName==null) {
				throw new SecurityException("RemoteStoreConfig name undefined");
			}
			this.clientInfo = provider.readClientInfo(this.keyId, this.clientId, remoteStoreConfig, context);
		}catch(Exception e) {
			throw new SecurityException(e.getMessage(),e);
		}
	}
	
	public String getRemoteStoreName() {
		return this.remoteStoreName;
	}
	public void setRemoteStoreName(String remoteStoreName) {
		this.remoteStoreName = remoteStoreName;
	}

	public String getKeyId() {
		return this.keyId;
	}
	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	public String getClientId() {
		return this.clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public org.openspcoop2.utils.certificate.remote.RemoteStoreClientInfo getClientInfo() {
		return this.clientInfo;
	}
	public void setClientInfo(org.openspcoop2.utils.certificate.remote.RemoteStoreClientInfo clientInfo) {
		this.clientInfo = clientInfo;
	}
	
	
}
