/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
package org.openspcoop2.utils.certificate.remote;

import java.io.ByteArrayOutputStream;
import java.security.PublicKey;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.certificate.JWK;

/**
 * RemoteStoreProvider
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RemoteStoreProvider implements IRemoteStoreProvider {

	@Override
	public JWK readJWK(String keyId, RemoteStoreConfig remoteConfig) throws UtilsException {
		return RemoteStoreUtils.readJWK(keyId, remoteConfig);
	}
	@Override
	public JWK readJWK(String keyId, RemoteStoreConfig remoteConfig, ByteArrayOutputStream bout) throws UtilsException{
		return RemoteStoreUtils.readJWK(keyId, remoteConfig, bout);
	}

	@Override
	public Certificate readX509(String keyId, RemoteStoreConfig remoteConfig) throws UtilsException {
		return RemoteStoreUtils.readX509(keyId, remoteConfig);
	}
	@Override
	public Certificate readX509(String keyId, RemoteStoreConfig remoteConfig, ByteArrayOutputStream bout) throws UtilsException{
		return RemoteStoreUtils.readX509(keyId, remoteConfig, bout);
	}

	@Override
	public PublicKey readPublicKey(String keyId, RemoteStoreConfig remoteConfig) throws UtilsException {
		return RemoteStoreUtils.readPublicKey(keyId, remoteConfig);
	}
	@Override
	public PublicKey readPublicKey(String keyId, RemoteStoreConfig remoteConfig, ByteArrayOutputStream bout) throws UtilsException{
		return RemoteStoreUtils.readPublicKey(keyId, remoteConfig, bout);
	}
	
	@Override
	public RemoteStoreClientInfo readClientInfo(String keyId, String clientId, RemoteStoreConfig remoteConfig)
			throws UtilsException {
		throw new UtilsException("Not Implemented");
	}

}
