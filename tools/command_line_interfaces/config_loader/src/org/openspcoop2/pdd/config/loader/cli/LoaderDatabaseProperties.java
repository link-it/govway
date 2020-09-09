/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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


package org.openspcoop2.pdd.config.loader.cli;

import java.io.InputStream;
import java.util.Properties;

/**
* LoaderDatabaseProperties
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class LoaderDatabaseProperties {
	
	private static LoaderDatabaseProperties staticInstance = null;
	private static synchronized void init() throws Exception{
		if(LoaderDatabaseProperties.staticInstance == null){
			LoaderDatabaseProperties.staticInstance = new LoaderDatabaseProperties();
		}
	}
	public static LoaderDatabaseProperties getInstance() throws Exception{
		if(LoaderDatabaseProperties.staticInstance == null){
			LoaderDatabaseProperties.init();
		}
		return LoaderDatabaseProperties.staticInstance;
	}
	
	
	
	
	private static String PROPERTIES_FILE = "/config_loader.cli.database.properties";
	
	private String tipoDatabase = null;
	private String driver = null;
	private String connectionUrl = null;
	private String username = null;
	private String password = null;
	
	
	public LoaderDatabaseProperties() throws Exception {

		Properties props = new Properties();
		try {
			InputStream is = LoaderDatabaseProperties.class.getResourceAsStream(LoaderDatabaseProperties.PROPERTIES_FILE);
			props.load(is);
		} catch(Exception e) {
			throw new Exception("Errore durante l'init delle properties", e);
		}
		
		// PROPERTIES
				
		this.tipoDatabase = this.getProperty(props, "tipoDatabase", true);
		
		this.driver = this.getProperty(props, "driver", true);
		this.connectionUrl = this.getProperty(props, "connection-url", true);
		this.username = this.getProperty(props, "username", true);
		this.password = this.getProperty(props, "password", true);

	}
	
	private String getProperty(Properties props,String name,boolean required) throws Exception{
		String tmp = props.getProperty(name);
		if(tmp==null){
			if(required){
				throw new Exception("Property '"+name+"' not found");
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
