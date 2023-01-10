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

package org.openspcoop2.utils.certificate.hsm;

import java.util.List;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeyStore;

/**
 * HSMUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HSMUtils {

	public static final String KEYSTORE_HSM_PREFIX = "HSM-";
	public static final String KEYSTORE_HSM_STORE_PASSWORD_UNDEFINED = "-";
	public static final String KEYSTORE_HSM_PRIVATE_KEY_PASSWORD_UNDEFINED = "-";
    public static boolean HSM_CONFIGURABLE_KEY_PASSWORD = false;
	
    public static void fillTIPOLOGIE_KEYSTORE(boolean trustStore, boolean secretKeyStore, List<String> l){
		HSMManager hsmManager = HSMManager.getInstance();
		if(hsmManager!=null) {
			List<String> typeHsm = hsmManager.getKeystoreTypes();
			if(typeHsm!=null && !typeHsm.isEmpty()) {
				if(secretKeyStore) {
					for (String type : typeHsm) {
						try {
							if(hsmManager.isUsableAsSecretKeyStore(type)) {
								l.add(type);
							}
						}catch(Exception e) { // ignore 
						}
					}
				}
				else if(trustStore) {
					for (String type : typeHsm) {
						try {
							if(hsmManager.isUsableAsTrustStore(type)) {
								l.add(type);
							}
						}catch(Exception e) { // ignore 
						}
					}
				}
				else {
					l.addAll(typeHsm);
				}
			}
		}
	}
	public static boolean existsTIPOLOGIE_KEYSTORE_HSM(boolean trustStore, boolean secretKeyStore){
		HSMManager hsmManager = HSMManager.getInstance();
		if(hsmManager!=null) {
			List<String> typeHsm = hsmManager.getKeystoreTypes();
			if(typeHsm!=null && !typeHsm.isEmpty()) {
				if(secretKeyStore) {
					for (String type : typeHsm) {
						try {
							if(hsmManager.isUsableAsSecretKeyStore(type)) {
								return true;
							}
						}catch(Exception e) { // ignore 
						}
					}
				}
				else if(trustStore) {
					for (String type : typeHsm) {
						try {
							if(hsmManager.isUsableAsTrustStore(type)) {
								return true;
							}
						}catch(Exception e) { // ignore 
						}
					}
				}
				else {
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean isKeystoreHSM(String tipo) {
		if(HSMManager.getInstance()!=null) {
			return HSMManager.getInstance().existsKeystoreType(tipo);
		}
		return false;
	}
	
	public static KeyStore getKeystoreHSM(String tipo) throws UtilsException {
		if(HSMManager.getInstance()!=null) {
			return HSMManager.getInstance().getKeystore(tipo);
		}
		return null;
	}
	
}
