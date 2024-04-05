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
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.certificate.JWK;
import org.openspcoop2.utils.certificate.KeyUtils;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.ExternalResourceUtils;

/**
 * RemoteStoreUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RemoteStoreUtils {

	private RemoteStoreUtils() {}

	public static JWK readJWK(String keyId, RemoteStoreConfig remoteConfig) throws UtilsException {
		return (JWK) readResource(keyId, remoteConfig, RemoteKeyType.JWK, null);
	}
	public static JWK readJWK(String keyId, RemoteStoreConfig remoteConfig, ByteArrayOutputStream bout) throws UtilsException {
		return (JWK) readResource(keyId, remoteConfig, RemoteKeyType.JWK, bout);
	}
	public static Certificate readX509(String keyId, RemoteStoreConfig remoteConfig) throws UtilsException {
		return (Certificate) readResource(keyId, remoteConfig, RemoteKeyType.X509, null);
	}
	public static Certificate readX509(String keyId, RemoteStoreConfig remoteConfig, ByteArrayOutputStream bout) throws UtilsException {
		return (Certificate) readResource(keyId, remoteConfig, RemoteKeyType.X509, bout);
	}
	public static PublicKey readPublicKey(String keyId, RemoteStoreConfig remoteConfig) throws UtilsException {
		return (PublicKey) readResource(keyId, remoteConfig, RemoteKeyType.PUBLIC_KEY, null);
	}
	public static PublicKey readPublicKey(String keyId, RemoteStoreConfig remoteConfig, ByteArrayOutputStream bout) throws UtilsException {
		return (PublicKey) readResource(keyId, remoteConfig, RemoteKeyType.PUBLIC_KEY, bout);
	}
	private static Object readResource(String keyId, RemoteStoreConfig remoteConfig, RemoteKeyType keyType, ByteArrayOutputStream bout) throws UtilsException {
		try {

			checkParams(keyId, remoteConfig, keyType);
			
			String url = remoteConfig.getBaseUrl();
			
			switch (remoteConfig.getIdMode()) {
			case URL_SUFFIX:
				if(!remoteConfig.getBaseUrl().endsWith("/")) {
					url = url + "/";
				}
				url = url + TransportUtils.urlEncodePath(keyId, Charset.UTF_8.getValue());
				break;
			case URL_PARAMETER:
				remoteConfig.setQueryParameters(new HashMap<>());
				remoteConfig.getQueryParameters().put(remoteConfig.getParameterName(), 
						TransportUtils.urlEncodeParam(keyId, Charset.UTF_8.getValue()));
				break;
			case HEADER:
				remoteConfig.setHeaders(new HashMap<>());
				remoteConfig.getHeaders().put(remoteConfig.getParameterName(), keyId);
				break;
			default:
				break;
			}
			
			byte [] resource = ExternalResourceUtils.readResource(url, remoteConfig);
			
			if(resource==null || resource.length<=0) {
				throw new UtilsException("Retrieved empty key?");
			}
			
			if(bout!=null) {
				bout.write(resource);
			}
			
			switch (keyType) {
			case JWK:
				return new JWK(new String(resource));
			case X509:
				return ArchiveLoader.load(resource);
			case PUBLIC_KEY:
				if(remoteConfig.getKeyAlgorithm()==null) {
					throw new UtilsException("Key algorithm undefined");
				}
				return KeyUtils.getInstance(remoteConfig.getKeyAlgorithm()).getPublicKey(resource);
			}
			
			throw new UtilsException("Key unknown");
			
		}catch(Exception t) {
			throw new UtilsException("Retrieve remote key '"+keyId+"' failed: "+t.getMessage(),t);
		}
	}
	
	private static void checkParams(String keyId, RemoteStoreConfig remoteConfig, RemoteKeyType keyType) throws UtilsException {
		if(keyId==null) {
			throw new UtilsException("KeyId undefined");
		}
		if(remoteConfig==null) {
			throw new UtilsException("Config undefined");
		}
		if(keyType==null) {
			throw new UtilsException("KeyType undefined");
		}
		
		if(remoteConfig.getBaseUrl()==null || StringUtils.isEmpty(remoteConfig.getBaseUrl())) {
			throw new UtilsException("BaseUrl undefined");
		}

		if(remoteConfig.getIdMode()==null) {
			throw new UtilsException("KeyId mode undefined");
		}
	}
	
}
