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
* VaultDatabaseProperties
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class VaultDatabaseProperties {
	
	private static VaultDatabaseProperties staticInstance = null;
	private static synchronized void init() throws CoreException{
		if(VaultDatabaseProperties.staticInstance == null){
			VaultDatabaseProperties.staticInstance = new VaultDatabaseProperties();
		}
	}
	public static VaultDatabaseProperties getInstance() throws CoreException{
		if(VaultDatabaseProperties.staticInstance == null){
			VaultDatabaseProperties.init();
		}
		return VaultDatabaseProperties.staticInstance;
	}
	
	
	
	
	private static final String PROPERTIES_FILE = "/govway_vault.cli.database.properties";
	
	private String tipoDatabase = null;
	private String driver = null;
	private String connectionUrl = null;
	private String username = null;
	private String password = null;
	
	
	public VaultDatabaseProperties() throws CoreException {

		Properties props = new Properties();
		try {
			InputStream is = VaultDatabaseProperties.class.getResourceAsStream(VaultDatabaseProperties.PROPERTIES_FILE);
			props.load(is);
		} catch(Exception e) {
			throw new CoreException("Errore durante l'init delle properties", e);
		}
		
		// PROPERTIES
				
		this.tipoDatabase = this.getProperty(props, "tipoDatabase", true);
		
		this.driver = this.getProperty(props, "driver", true);
		this.connectionUrl = this.getProperty(props, "connection-url", true);
		this.username = this.getProperty(props, "username", true);
		this.password = this.getProperty(props, "password", true);

	}
	
	private String getProperty(Properties props,String name,boolean required) throws CoreException{
		String tmp = props.getProperty(name);
		if(tmp==null){
			if(required){
				throw new CoreException("Property '"+name+"' not found");
			}
			else{
				return null;
			}
		}
		else{
			return tmp.trim();
		}
	}

	public String getTipoDatabase() {
		return this.tipoDatabase;
	}
	public String getDriver() {
		return this.driver;
	}
	public String getConnectionUrl() {
		return this.connectionUrl;
	}
	public String getUsername() {
		return this.username;
	}
	public String getPassword() {
		return this.password;
	}
}
