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
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.security.SecurityException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.certificate.byok.BYOKRequestParams;

/**
 * MultiKeystore
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MultiKeystore implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String ALIASES = "aliases";
	private static final String KEYSTORE_TYPE = ".keystore.type";
	private static final String KEYSTORE_PATH = ".keystore.path";
	private static final String KEYSTORE_PASSWORD = ".keystore.password";
	private static final String KEY_ALIAS = ".key.alias";
	private static final String KEY_PASSWORD = ".key.password";
	private static final String KEY_VALUE = ".key.value";
	private static final String KEY_ALGORITHM = ".key.algorithm";
	
	private List<String> aliasesList = new ArrayList<>();
	private Map<String, Serializable> keystores = new HashMap<>();
	private Map<String, String> mappingAliasToKeystorePath = new HashMap<>();
	private Map<String, String> mappingAliasToKeystorePassword = new HashMap<>();
	private Map<String, String> mappingAliasToKeystoreType = new HashMap<>();
	private Map<String, String> mappingAliasToKeyAlias = new HashMap<>();
	private Map<String, String> mappingAliasToKeyPassword = new HashMap<>();
	
	@Override
	public String toString() {
		StringBuilder bf = new StringBuilder();
		bf.append("MultiKeystore ").append(this.aliasesList);
		return bf.toString();
	}
	
	public MultiKeystore(String propertyFilePath) throws SecurityException{
		this(propertyFilePath, null);
	}
	public MultiKeystore(String propertyFilePath, BYOKRequestParams requestParams) throws SecurityException{
		
		try{
			Properties multiProperties = StoreUtils.readProperties("MultiProperties", propertyFilePath);
			
			String keyAliases = getProperty(multiProperties, MultiKeystore.ALIASES);
			String [] tmp = keyAliases.split(",");
			for (int i = 0; i < tmp.length; i++) {
				if(!this.aliasesList.contains(tmp[i].trim()))
					this.aliasesList.add(tmp[i].trim());
			}
			
			for (String alias : this.aliasesList) {
				String keyAlias = getSafeKeyAlias(alias, multiProperties);
				String keyPassword = getProperty(multiProperties, alias+MultiKeystore.KEY_PASSWORD);
				
				String keyValue = multiProperties.getProperty(alias+MultiKeystore.KEY_VALUE);
				
				if(keyValue!=null){
					// Configurazione con keyValue fornita direttamente
					String keyAlgorithm = getProperty(multiProperties, alias+MultiKeystore.KEY_ALGORITHM);
					addSymmetricKeystore(alias, keyAlias, keyValue, keyAlgorithm, requestParams);
				}
				else{
					// Configurazione con MerlinKeystore
					String keystoreType = getProperty(multiProperties, alias+MultiKeystore.KEYSTORE_TYPE);
					String keystorePath = getProperty(multiProperties, alias+MultiKeystore.KEYSTORE_PATH);
					String keystorePassword = getProperty(multiProperties, alias+MultiKeystore.KEYSTORE_PASSWORD);
					addMerlinKeystore(alias, keyAlias, keystoreType, keystorePath, keystorePassword, keyPassword, requestParams);
				}
				
				this.mappingAliasToKeyAlias.put(alias, keyAlias);
				this.mappingAliasToKeyPassword.put(alias, keyPassword);
			}
			
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}
	
	private String getSafeKeyAlias(String alias, Properties multiProperties) {
		String keyAlias = null;
		try{
			keyAlias = getProperty(multiProperties, alias+MultiKeystore.KEY_ALIAS);
		}catch(Exception e){
			keyAlias = alias;
		}
		return keyAlias;
	}
	
	private void addSymmetricKeystore(String alias, String keyAlias, String keyValue, String keyAlgorithm, BYOKRequestParams requestParams) {
		try {
			this.keystores.put(alias, new SymmetricKeystore(keyAlias, keyValue, keyAlgorithm, requestParams));
		}catch(Exception e) {
			String idKeystore = "!!! Errore durante il caricamento del SymmetricKeystore !!! [keyAlias:"+keyAlias+"] ";
			LoggerWrapperFactory.getLogger(MultiKeystore.class).error(idKeystore+e.getMessage(),e);
		}
	}
	private void addMerlinKeystore(String alias, String keyAlias, String keystoreType, String keystorePath, String keystorePassword, String keyPassword, BYOKRequestParams requestParams) {
		try {
			this.keystores.put(alias, new MerlinKeystore(keystorePath, keystoreType, keystorePassword, keyPassword, requestParams));
			this.mappingAliasToKeystoreType.put(alias, keystoreType);
			this.mappingAliasToKeystorePath.put(alias, keystorePath);
			this.mappingAliasToKeystorePassword.put(alias, keystorePassword);
		}catch(Exception e) {
			String idKeystore = "!!! Errore durante il caricamento del MerlinKeystore !!! [keyAlias:"+keyAlias+"] ";
			LoggerWrapperFactory.getLogger(MultiKeystore.class).error(idKeystore+e.getMessage(),e);
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
	
	private String getErrorAlias(String alias) {
		return "Alias["+alias+"] non trovato nella configurazione MultiKeystore";
	}
	
	public Key getKey(String alias) throws SecurityException {
		try{
			if(!this.aliasesList.contains(alias)){
				throw new SecurityException(getErrorAlias(alias));
			}
			
			Object keystore = this.keystores.get(alias);
			if(keystore==null) {
				throw new SecurityException("Non esiste un keystore per l'alias["+alias+"]; verificare che non sia avvenuti errori durante l'inizializzazione (MultiKeystore)");
			}
			if(keystore instanceof MerlinKeystore){
				return ((MerlinKeystore)keystore).getKey(this.mappingAliasToKeyAlias.get(alias));
			}
			else if(keystore instanceof SymmetricKeystore){
				return ((SymmetricKeystore)keystore).getKey();
			}
			else{
				throw new SecurityException("Tipo di Keystore non supportato: "+keystore.getClass().getName());
			}
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}

	public boolean existsAlias(String alias){
		return this.aliasesList.contains(alias);
	}
	
	public KeyStore getKeyStore(String alias) throws SecurityException {
		try{
			if(!this.aliasesList.contains(alias)){
				throw new SecurityException(getErrorAlias(alias));
			}
			
			Object keystore = this.keystores.get(alias);
			if(keystore==null) {
				throw new SecurityException("Non esiste un keystore per l'alias["+alias+"]; verificare che non sia avvenuti errori durante l'inizializzazione (MultiKeystore)");
			}
			if(keystore instanceof MerlinKeystore){
				return ((MerlinKeystore)keystore).getKeyStore();
			}
			else if(keystore instanceof SymmetricKeystore){
				return ((SymmetricKeystore)keystore).getKeyStore();
			}
			else{
				throw new SecurityException("Tipo di Keystore non supportato: "+keystore.getClass().getName());
			}
			
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}

	public String getKeyAlias(String alias) throws SecurityException {
		try{
			if(!this.aliasesList.contains(alias)){
				throw new SecurityException(getErrorAlias(alias));
			}
			
			return this.mappingAliasToKeyAlias.get(alias);
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}
	
	public String getKeyPassword(String alias) throws SecurityException {
		try{
			if(!this.aliasesList.contains(alias)){
				throw new SecurityException(getErrorAlias(alias));
			}
			
			return this.mappingAliasToKeyPassword.get(alias);
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}
	
	public String getKeystorePath(String alias) throws SecurityException {
		try{
			if(!this.aliasesList.contains(alias)){
				throw new SecurityException(getErrorAlias(alias));
			}
			
			return this.mappingAliasToKeystorePath.get(alias);
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}
	
	public String getKeystorePassword(String alias) throws SecurityException {
		try{
			if(!this.aliasesList.contains(alias)){
				throw new SecurityException(getErrorAlias(alias));
			}
			
			return this.mappingAliasToKeystorePassword.get(alias);
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}
	
	public String getKeystoreType(String alias) throws SecurityException {
		try{
			if(!this.aliasesList.contains(alias)){
				throw new SecurityException(getErrorAlias(alias));
			}
			
			return this.mappingAliasToKeystoreType.get(alias);
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}
	
	public String getInternalConfigAlias(String keyAlias) throws SecurityException {
		try{
			if(!this.mappingAliasToKeyAlias.containsValue(keyAlias)){
				throw new SecurityException(getErrorAlias(keyAlias));
			}
			for (Map.Entry<String,String> entry : this.mappingAliasToKeyAlias.entrySet()) {
				if(entry.getValue().equals(keyAlias)) {
					return entry.getKey();
				}
			}
			
			throw new SecurityException(getErrorAlias(keyAlias));
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}
	
}
