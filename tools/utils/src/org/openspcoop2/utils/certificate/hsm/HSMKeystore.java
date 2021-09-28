/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

import java.io.File;
import java.io.Serializable;
import java.security.Provider;
import java.security.Security;
import java.util.Properties;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeyStore;
import org.slf4j.Logger;

/**
 * HSMKeystore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HSMKeystore implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3572589461109860459L;

	private transient Boolean _uniqueProviderInstance;
	private transient Provider _providerInstance;
		
	private String id;

	private String provider;
	private boolean providerAdd = false;
	private String configFile;
	private String config;
	private String pin;
	private String keystoreTypeLabel;
	private String keystoreType;
	private boolean usableAsTrustStore = false;
	private boolean usableAsSecretKeyStore = false;
	
	protected HSMKeystore(String id, Properties p, Logger log) throws UtilsException {
		this.id = id;
		
		if(p==null || p.isEmpty()) {
			throw new UtilsException("Properties '"+HSMCostanti.PROPERTY_PREFIX+id+".*' undefined");
		}
		
		this.provider = getProperty(id, p, HSMCostanti.PROPERTY_SUFFIX_PROVIDER, true);	
		
		String tmp = getProperty(id, p, HSMCostanti.PROPERTY_SUFFIX_PROVIDER_ADD, false);
		if(tmp!=null) {
			try {
				this.providerAdd = Boolean.parseBoolean(tmp);
			}catch(Throwable e) {
				throw new UtilsException("Property '"+HSMCostanti.PROPERTY_PREFIX+id+"."+HSMCostanti.PROPERTY_SUFFIX_PROVIDER_ADD+"' uncorrect: "+e.getMessage(),e);
			}
		}
		
		this.configFile = getProperty(id, p, HSMCostanti.PROPERTY_SUFFIX_PROVIDER_CONFIG_FILE, false);
		this.config = getProperty(id, p, HSMCostanti.PROPERTY_SUFFIX_PROVIDER_CONFIG, false);
		
		this.pin = getProperty(id, p, HSMCostanti.PROPERTY_SUFFIX_PIN, true);
		
		this.keystoreType = getProperty(id, p, HSMCostanti.PROPERTY_SUFFIX_KEYSTORE_TYPE, true);	
		this.keystoreTypeLabel = getProperty(id, p, HSMCostanti.PROPERTY_SUFFIX_KEYSTORE_TYPE_LABEL, true);
		
		tmp = getProperty(id, p, HSMCostanti.PROPERTY_SUFFIX_USABLE_AS_TRUST_STORE, false);
		if(tmp!=null) {
			try {
				this.usableAsTrustStore = Boolean.parseBoolean(tmp);
			}catch(Throwable e) {
				throw new UtilsException("Property '"+HSMCostanti.PROPERTY_PREFIX+id+"."+HSMCostanti.PROPERTY_SUFFIX_USABLE_AS_TRUST_STORE+"' uncorrect: "+e.getMessage(),e);
			}
		}
		
		tmp = getProperty(id, p, HSMCostanti.PROPERTY_SUFFIX_USABLE_AS_SECRET_KEY_STORE, false);
		if(tmp!=null) {
			try {
				this.usableAsSecretKeyStore = Boolean.parseBoolean(tmp);
			}catch(Throwable e) {
				throw new UtilsException("Property '"+HSMCostanti.PROPERTY_PREFIX+id+"."+HSMCostanti.PROPERTY_SUFFIX_USABLE_AS_SECRET_KEY_STORE+"' uncorrect: "+e.getMessage(),e);
			}
		}
	}

	private static String getProperty(String id, Properties p, String name, boolean required) throws UtilsException {
		String tmp = p.getProperty(name);
		if(tmp!=null) {
			return tmp.trim();
		}
		else {
			if(required) {
				throw new UtilsException("Property '"+HSMCostanti.PROPERTY_PREFIX+id+"."+name+"' notFound");
			}
			return null;
		}
	}
	
	public String getId() {
		return this.id;
	}
	
	public String getProvider() {
		return this.provider;
	}

	public boolean isProviderAdd() {
		return this.providerAdd;
	}

	public String getConfigFile() {
		return this.configFile;
	}

	public String getConfig() {
		return this.config;
	}

	public String getKeystoreTypeLabel() {
		return this.keystoreTypeLabel;
	}

	public String getKeystoreType() {
		return this.keystoreType;
	}	
	
	public String getPrefixForLog() {
		return "[Keystore '"+this.getId()+"' type:"+this.getKeystoreTypeLabel()+"] ";
	}
	
	// ******* Keystore engine ********
	
	public void init(Logger log, boolean uniqueProviderInstance) throws UtilsException {
		if(this._uniqueProviderInstance==null) {
			initInstance(log, uniqueProviderInstance);
		}
	}
	private synchronized void initInstance(Logger log, boolean uniqueProviderInstance) throws UtilsException {
		if(this._uniqueProviderInstance==null) {
			Provider provider = newProviderInstance();
			if(this.isProviderAdd()) {
				Provider providerCheck = null;
				try {
					providerCheck = Security.getProvider(provider.getName());
				}catch(Throwable t) {}
				if(providerCheck==null) {
					Security.addProvider(provider);
					log.info("Registered provider: "+provider.getName());
				}
				else {
					log.info("Loaded provider (not registered, already exists): "+provider.getName());
				}
			}
			else {
				log.info("Loaded provider: "+provider.getName());
			}
			
			this._uniqueProviderInstance = uniqueProviderInstance;
			if(this._uniqueProviderInstance) {
				this._providerInstance = provider;
			}
		}
	}
	private Provider newProviderInstance() throws UtilsException{
		Provider provider = Security.getProvider(this.getProvider());
		String prefix = this.getPrefixForLog();
		if(this.getConfigFile()!=null) {
			File f = new File(this.getConfigFile());
			if(!f.exists()) {
				throw new UtilsException(prefix+"Configuration file '"+f.getAbsolutePath()+"' not exists");
			}
			else {
				if(!f.canRead()) {
					throw new UtilsException(prefix+"Configuration file '"+f.getAbsolutePath()+"' cannot read");
				}
			}
			provider = provider.configure(this.getConfigFile());
		}
		else if(this.getConfig()!=null) {
			provider = provider.configure(this.getConfig());
		}
		return provider;
	}
	
	public KeyStore getInstance() throws UtilsException {
		String prefix = this.getPrefixForLog();
		
		Provider providerInstance = null;
		if(this._uniqueProviderInstance==null) {
			throw new UtilsException(prefix+"Provider not initialized");
		}
		if(this._uniqueProviderInstance) {
			if(this._providerInstance==null) {
				throw new UtilsException(prefix+"Provider not initialized");
			}
			providerInstance = this._providerInstance;
		}
		else {
			providerInstance = newProviderInstance();
		}
		
		java.security.KeyStore hsmKeyStore = null;
		try {
			hsmKeyStore = java.security.KeyStore.getInstance(this.keystoreType, providerInstance);
			hsmKeyStore.load(null, this.pin.toCharArray());
		}catch(Throwable t) {
			throw new UtilsException(prefix+t.getMessage(),t);
		}
		return new KeyStore(hsmKeyStore, true);
	}
	
	public boolean isUsableAsTrustStore() {
		return this.usableAsTrustStore;
	}
	public boolean isUsableAsSecretKeyStore() {
		return this.usableAsSecretKeyStore;
	}
}
