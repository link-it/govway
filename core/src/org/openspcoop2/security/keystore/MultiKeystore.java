/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import java.security.Key;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.openspcoop2.security.SecurityException;

/**
 * MultiKeystore
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MultiKeystore {

	private final static String ALIASES = "aliases";
	private final static String KEYSTORE_TYPE = ".keystore.type";
	private final static String KEYSTORE_PATH = ".keystore.path";
	private final static String KEYSTORE_PASSWORD = ".keystore.password";
	private final static String KEY_ALIAS = ".key.alias";
	private final static String KEY_PASSWORD = ".key.password";
	private final static String KEY_VALUE = ".key.value";
	private final static String KEY_ALGORITHM = ".key.algorithm";
	
	private List<String> aliases = new ArrayList<String>();
	private Hashtable<String, Object> keystores = new Hashtable<String, Object>();
	private Hashtable<String, String> mappingAliasToKeyAlias = new Hashtable<String, String>();
	private Hashtable<String, String> mappingAliasToKeyPassword = new Hashtable<String, String>();
	
	public MultiKeystore(String propertyFilePath) throws SecurityException{
		
		InputStream isStore = null;
		try{
			File fStore = new File(propertyFilePath);
			if(fStore.exists()){
				isStore = new FileInputStream(fStore);
			}else{
				isStore = MerlinKeystore.class.getResourceAsStream("/"+propertyFilePath);
				if(isStore==null){
					throw new Exception("Store ["+propertyFilePath+"] not found");
				}
			}
			Properties multiProperties = new Properties();
			multiProperties.load(isStore);
			isStore.close();
			
			String keyAliases = getProperty(multiProperties, MultiKeystore.ALIASES);
			String [] tmp = keyAliases.split(",");
			for (int i = 0; i < tmp.length; i++) {
				if(this.aliases.contains(tmp[i].trim())==false)
					this.aliases.add(tmp[i].trim());
			}
			
			for (String alias : this.aliases) {
				String keyAlias = null;
				try{
					keyAlias = getProperty(multiProperties, alias+MultiKeystore.KEY_ALIAS);
				}catch(Exception e){
					keyAlias = alias;
				}
				String keyPassword = getProperty(multiProperties, alias+MultiKeystore.KEY_PASSWORD);
				
				String keyValue = multiProperties.getProperty(alias+MultiKeystore.KEY_VALUE);
				
				if(keyValue!=null){
					// Configurazione con keyValue fornita direttamente
					String keyAlgorithm = getProperty(multiProperties, alias+MultiKeystore.KEY_ALGORITHM);
					this.keystores.put(alias, new SymmetricKeystore(keyAlias, keyValue, keyAlgorithm));
				}
				else{
					// Configurazione con MerlinKeystore
					String keystoreType = getProperty(multiProperties, alias+MultiKeystore.KEYSTORE_TYPE);
					String keystorePath = getProperty(multiProperties, alias+MultiKeystore.KEYSTORE_PATH);
					String keystorePassword = getProperty(multiProperties, alias+MultiKeystore.KEYSTORE_PASSWORD);
					this.keystores.put(alias, new MerlinKeystore(keystorePath, keystoreType, keystorePassword, keyPassword));
				}
				
				this.mappingAliasToKeyAlias.put(alias, keyAlias);
				this.mappingAliasToKeyPassword.put(alias, keyPassword);
			}
			
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
		finally{
			try{
				if(isStore!=null)
					isStore.close();
			}catch(Exception eClose){}
		}
	}
	
	private String getProperty(Properties multiProperties,String property)throws SecurityException{
		if(!multiProperties.containsKey(property)){
			throw new SecurityException("Proprieta' ["+property+"] non trovata nel MultiProperties file");
		}
		else{
			return multiProperties.getProperty(property).trim();
		}
	}
	
	public Key getKey(String alias) throws SecurityException {
		try{
			if(this.aliases.contains(alias)==false){
				throw new Exception("Alias["+alias+"] non trovato nella configurazione MultiKeystore");
			}
			
			Object keystore = this.keystores.get(alias);
			if(keystore instanceof MerlinKeystore){
				return ((MerlinKeystore)keystore).getKey(this.mappingAliasToKeyAlias.get(alias));
			}
			else if(keystore instanceof MerlinKeystore){
				return ((SymmetricKeystore)keystore).getKey();
			}
			else{
				throw new Exception("Tipo di Keystore non supportato: "+keystore.getClass().getName());
			}
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}

	public boolean existsAlias(String alias){
		return this.aliases.contains(alias);
	}
	
	public KeyStore getKeyStore(String alias) throws SecurityException {
		try{
			if(this.aliases.contains(alias)==false){
				throw new Exception("Alias["+alias+"] non trovato nella configurazione MultiKeystore");
			}
			
			Object keystore = this.keystores.get(alias);
			if(keystore instanceof MerlinKeystore){
				return ((MerlinKeystore)keystore).getKeyStore();
			}
			else if(keystore instanceof MerlinKeystore){
				return ((SymmetricKeystore)keystore).getKeyStore();
			}
			else{
				throw new Exception("Tipo di Keystore non supportato: "+keystore.getClass().getName());
			}
			
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}

	public String getKeyAlias(String alias) throws SecurityException {
		try{
			if(this.aliases.contains(alias)==false){
				throw new Exception("Alias["+alias+"] non trovato nella configurazione MultiKeystore");
			}
			
			return this.mappingAliasToKeyAlias.get(alias);
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}
	
	public String getKeyPassword(String alias) throws SecurityException {
		try{
			if(this.aliases.contains(alias)==false){
				throw new Exception("Alias["+alias+"] non trovato nella configurazione MultiKeystore");
			}
			
			return this.mappingAliasToKeyPassword.get(alias);
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}
}
