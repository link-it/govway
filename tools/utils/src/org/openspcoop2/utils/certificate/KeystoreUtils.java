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
package org.openspcoop2.utils.certificate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;


/**	
 * KeystoreUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class KeystoreUtils {

	private KeystoreUtils() {}
	
	public static KeyStore readKeystore(InputStream is, String tipoKeystore, String passwordKeystore) throws UtilsException{
		byte [] keystoreBytes = Utilities.getAsByteArray(is);
		return readKeystore(keystoreBytes, tipoKeystore, passwordKeystore);
	}
	public static KeyStore readKeystore(byte [] keystoreBytes, String tipoKeystore, String passwordKeystore) throws UtilsException{
		
		try (ByteArrayInputStream bin = new ByteArrayInputStream(keystoreBytes)){
			java.security.KeyStore keystore = java.security.KeyStore.getInstance(tipoKeystore);
			keystore.load(bin, passwordKeystore!=null ? passwordKeystore.toCharArray() : null);
			return keystore;
		}catch(Exception e){
			
			// Fix #128
			try (ByteArrayInputStream bin = new ByteArrayInputStream(keystoreBytes)){
				Provider p = Security.getProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
				if(p!=null) {
					java.security.KeyStore keystore = java.security.KeyStore.getInstance(tipoKeystore, p);
					keystore.load(bin, passwordKeystore!=null ? passwordKeystore.toCharArray() : null);
					return keystore;
				}
			}catch(Exception eFix){
				// ignore
			}
			
			// rilancio eccezione originale
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
}
