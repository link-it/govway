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
package org.openspcoop2.utils.certificate.remote;

import java.io.ByteArrayOutputStream;
import java.security.PublicKey;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.certificate.JWK;

/**
 * IRemoteStoreProvider
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IRemoteStoreProvider {

	public JWK readJWK(String keyId, RemoteStoreConfig remoteConfig) throws UtilsException;
	public JWK readJWK(String keyId, RemoteStoreConfig remoteConfig, ByteArrayOutputStream bout) throws UtilsException;
	
	public Certificate readX509(String keyId, RemoteStoreConfig remoteConfig) throws UtilsException;
	public Certificate readX509(String keyId, RemoteStoreConfig remoteConfig, ByteArrayOutputStream bout) throws UtilsException;
	
	public PublicKey readPublicKey(String keyId, RemoteStoreConfig remoteConfig) throws UtilsException;
	public PublicKey readPublicKey(String keyId, RemoteStoreConfig remoteConfig, ByteArrayOutputStream bout) throws UtilsException;
	
	public RemoteStoreClientInfo readClientInfo(String keyId, String clientId, RemoteStoreConfig remoteConfig, org.openspcoop2.utils.Map<Object> context) throws UtilsException;
	
}
