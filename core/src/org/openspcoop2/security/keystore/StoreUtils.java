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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.openspcoop2.core.byok.BYOKUtilities;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.certificate.byok.BYOKEncryptionMode;
import org.openspcoop2.utils.certificate.byok.BYOKInstance;
import org.openspcoop2.utils.certificate.byok.BYOKRequestParams;
import org.slf4j.Logger;

/**
 * StoreUtils
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StoreUtils {

	private StoreUtils() {}
	
	public static byte[] readContent(String pName, String path) throws SecurityException {
		
		if(path==null){
			throw new SecurityException("Property "+pName+" non indicata");
		}
		
		File fStore = new File(path);
		boolean fExists = fStore.exists();
		byte[] array = null;
		try {
			try(InputStream isStore = fExists ? new FileInputStream(fStore) : StoreUtils.class.getResourceAsStream(path)){
				if(isStore!=null){
					array = Utilities.getAsByteArray(isStore);
				}
			}
			
			if(array==null && !fExists) {
				try(InputStream isStore = StoreUtils.class.getResourceAsStream("/"+path)){
					if(isStore!=null){
						array = Utilities.getAsByteArray(isStore);
					}
				}
			}
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
		
		if(array==null){
			throw new SecurityException("Store ["+path+"] not found");
		}
		
		return array;
		
	}
	
	public static Properties readProperties(String pName, String path) throws SecurityException {
		
		if(path==null){
			throw new SecurityException("Property "+pName+" non indicata");
		}
		
		File fStore = new File(path);
		boolean fExists = fStore.exists();
		Properties propStore = null;
		try {
			try(InputStream isStore = fExists ? new FileInputStream(fStore) : StoreUtils.class.getResourceAsStream(path)){
				if(isStore!=null){
					propStore = new Properties();
					propStore.load(isStore);
				}
			}
			
			if(propStore==null && !fStore.exists()) {
				try(InputStream isStore = StoreUtils.class.getResourceAsStream("/"+path)){
					if(isStore!=null){
						propStore = new Properties();
						propStore.load(isStore);
					}
				}
			}
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
		
		if(propStore==null){
			throw new SecurityException("Store ["+path+"] not found");
		}
		
		return propStore;
		
	}
	
	public static byte[] unwrapBYOK(byte[] archive, BYOKRequestParams requestParams) throws SecurityException {
		if(requestParams!=null) {
			
			if(archive!=null && BYOKUtilities.isWrappedValue(archive)) {
				archive = BYOKUtilities.extractPrefixWrappedValue(archive).getBytes();
			}
			
			Logger log = LoggerWrapperFactory.getLogger(StoreUtils.class);
			BYOKInstance instance = null;
			try {
				if(requestParams.getConfig()!=null && BYOKEncryptionMode.LOCAL.equals(requestParams.getConfig().getEncryptionMode())) {
					instance = BYOKInstance.newLocalInstance(log, requestParams, archive);
				}
				else {
					instance = BYOKInstance.newRemoteInstance(log, requestParams, archive);
				}
			}catch(Exception e){
				throw new SecurityException(e.getMessage(),e);
			}
			BYOKStore store = new BYOKStore(instance.getKeyCache(), instance);
			return store.getStoreBytes();
		}
		return archive;
	}
}
