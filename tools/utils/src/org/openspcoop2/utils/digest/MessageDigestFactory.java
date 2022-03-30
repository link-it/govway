/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
package org.openspcoop2.utils.digest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;

/**
 * MessageDigestFactory
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessageDigestFactory {

	private static boolean useBouncyCastleProvider = false;
	public static boolean isUseBouncyCastleProvider() {
		return useBouncyCastleProvider;
	}
	public static void setUseBouncyCastleProvider(boolean useBouncyCastleProvider) {
		MessageDigestFactory.useBouncyCastleProvider = useBouncyCastleProvider;
	}
	
	private static Provider provider = null;
	private synchronized static Provider _getProvider() {
		if ( provider == null )
			provider = Security.getProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
		return provider;
	}
	private static Provider getProvider() {
		if(!useBouncyCastleProvider) {
			return null;
		}
		if ( provider == null )
			return _getProvider();
		return provider;
	}
	
	public static MessageDigest getMessageDigest(String algoritmo) throws NoSuchAlgorithmException {
		MessageDigest digest = null;
		if(useBouncyCastleProvider) {
			digest = MessageDigest.getInstance(algoritmo, getProvider());
		}
		else {
			digest = MessageDigest.getInstance(algoritmo);
		}
		return digest;
	}	
	
}
