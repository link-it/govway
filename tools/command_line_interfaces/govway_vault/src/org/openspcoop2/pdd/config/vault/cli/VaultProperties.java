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


package org.openspcoop2.pdd.config.vault.cli;

import java.io.InputStream;
import java.util.Properties;

import org.openspcoop2.core.commons.CoreException;

/**
* VaultProperties
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class VaultProperties {
	
	private static VaultProperties staticInstance = null;
	private static synchronized void init() throws CoreException{
		if(VaultProperties.staticInstance == null){
			VaultProperties.staticInstance = new VaultProperties();
		}
	}
	public static VaultProperties getInstance() throws CoreException{
		if(VaultProperties.staticInstance == null){
			VaultProperties.init();
		}
		return VaultProperties.staticInstance;
	}
	
	private static String getPropertyPrefix(String name) {
		return "Property '"+name+"'"; 
	}
	
	
	
	private static final String PROPERTIES_FILE = "/govway_vault.cli.properties";
	
	private String protocolloDefault = null;
		
	private boolean securityLoadBouncyCastleProvider = false;
	
	private String envMapConfig = null;
	private boolean envMapConfigRequired = false;
	
	private String hsmConfig = null;
	private boolean hsmRequired = false;
	private boolean hsmKeyPasswordConfigurable = false;
	
	private String byokConfigurazione = null;
	private boolean byokRequired = false;
	private String byokEnvSecretsConfig = null;
	private boolean byokEnvSecretsConfigRequired = false;
	
	public VaultProperties() throws CoreException {

		Properties props = new Properties();
		try {
			InputStream is = VaultProperties.class.getResourceAsStream(VaultProperties.PROPERTIES_FILE);
			props.load(is);
		} catch(Exception e) {
			throw new CoreException("Errore durante l'init delle properties", e);
		}
		
		// PROPERTIES
				
		this.protocolloDefault = this.getProperty(props, "protocolloDefault", true);
		
		this.securityLoadBouncyCastleProvider = this.getBooleanProperty(props, "security.addBouncyCastleProvider", false);
		
		this.envMapConfig = this.getProperty(props, "env.map.config", false);
		this.envMapConfigRequired = this.getBooleanProperty(props, "env.map.required", false);
		
		this.hsmConfig = this.getProperty(props, "hsm.config", false);
		this.hsmRequired = this.getBooleanProperty(props, "hsm.required", false);
		this.hsmKeyPasswordConfigurable = this.getBooleanProperty(props, "hsm.keyPassword", false);
		
		this.byokConfigurazione = this.getProperty(props, "byok.config", false);
		this.byokRequired = this.getBooleanProperty(props, "byok.required", false);
		this.byokEnvSecretsConfig = this.getProperty(props, "byok.env.secrets.config", false);
		this.byokEnvSecretsConfigRequired = this.getBooleanProperty(props, "byok.env.secrets.required", false);
		
	}
	
	private String getProperty(Properties props,String name,boolean required) throws CoreException{
		String tmp = props.getProperty(name);
		if(tmp==null){
			if(required){
				throw new CoreException(getPropertyPrefix(name)+" not found");
			}
			else{
				return null;
			}
		}
		else{
			return tmp.trim();
		}
	}
	private boolean getBooleanProperty(Properties props,String name,boolean required) throws CoreException{
		String tmp = this.getProperty(props, name, required);
		if(tmp!=null){
			try{
				return Boolean.parseBoolean(tmp);
			}catch(Exception e){
				throw new CoreException(getPropertyPrefix(name)+" wrong int format: "+e.getMessage());
			}
		}
		else{
			return false;
		}
	}
	@SuppressWarnings("unused")
	private int getIntProperty(Properties props,String name,boolean required) throws CoreException{
		String tmp = this.getProperty(props, name, required);
		if(tmp!=null){
			try{
				return Integer.valueOf(tmp);
			}catch(Exception e){
				throw new CoreException(getPropertyPrefix(name)+" wrong int format: "+e.getMessage());
			}
		}
		else{
			return -1;
		}
	}
	
	
	public String getProtocolloDefault() {
		return this.protocolloDefault;
	}
	
	public boolean isSecurityLoadBouncyCastleProvider() {
		return this.securityLoadBouncyCastleProvider;
	}
	
	public String getEnvMapConfig() {
		return this.envMapConfig;
	}
	public boolean isEnvMapConfigRequired(){
		return this.envMapConfigRequired;
	}
	
	public String getHSMConfigurazione() {
		return this.hsmConfig;
	}
	public boolean isHSMRequired() {
		return this.hsmRequired;
	}
	public boolean isHSMKeyPasswordConfigurable() {
		return this.hsmKeyPasswordConfigurable;
	}
	
	public String getBYOKConfigurazione() {
		return this.byokConfigurazione;
	}
	public boolean isBYOKRequired() {
		return this.byokRequired;
	}
	public String getBYOKEnvSecretsConfig() {
		return this.byokEnvSecretsConfig;
	}
	public boolean isBYOKEnvSecretsConfigRequired() {
		return this.byokEnvSecretsConfigRequired;
	}
}
