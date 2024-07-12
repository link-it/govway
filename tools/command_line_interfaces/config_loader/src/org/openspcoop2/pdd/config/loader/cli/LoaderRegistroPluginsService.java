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
package org.openspcoop2.pdd.config.loader.cli;

import java.sql.Connection;
import java.util.Properties;

import javax.naming.Context;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.RegistroPlugins;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.monitor.engine.dynamic.IRegistroPluginsReader;
import org.slf4j.Logger;

/**
* LoaderRegistroPluginsService
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class LoaderRegistroPluginsService implements IRegistroPluginsReader {

	private static final String ERROR_PREFIX = "Errore durante la creazione del Service: ";
	
	private DriverConfigurazioneDB driverConfigDB = null;
		
	public LoaderRegistroPluginsService(Logger log, LoaderDatabaseProperties databaseProperties){
		try{
			this.initEngine(null, databaseProperties, null, log);
			
		}catch(Exception e){
			String msgError = ERROR_PREFIX + e.getMessage();
			log.error(msgError,e);
		}
	}
	public LoaderRegistroPluginsService(Connection con, boolean autoCommit, LoaderDatabaseProperties databaseProperties, Logger log){
		try{
			if(autoCommit) {
				// nop
			}
			this.initEngine(con, databaseProperties, null, log);
		}catch(Exception e){
			log.error(ERROR_PREFIX + e.getMessage(),e);
		}
	}
	public LoaderRegistroPluginsService(Connection con, boolean autoCommit, ServiceManagerProperties serviceManagerProperties, Logger log){
		try{
			if(autoCommit) {
				// nop
			}
			this.initEngine(con, null, serviceManagerProperties, log);
		}catch(Exception e){
			log.error(ERROR_PREFIX + e.getMessage(),e);
		}
	}
	private void initEngine(Connection con, LoaderDatabaseProperties databaseProperties, ServiceManagerProperties serviceManagerProperties, Logger log) {
		try{
			String tipoDatabase = null;
			if(serviceManagerProperties!=null) {
				tipoDatabase = serviceManagerProperties.getDatabaseType();
			}
			else {
				tipoDatabase = databaseProperties.getTipoDatabase();
			}
			
			if(con!=null) {
				this.driverConfigDB = new DriverConfigurazioneDB(con, log, tipoDatabase);
			}
			else {
				
				Properties datasourceJNDIContext = new Properties();
				datasourceJNDIContext.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
				datasourceJNDIContext.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
				
				this.driverConfigDB = new DriverConfigurazioneDB(Loader.DS_JNDI_NAME,datasourceJNDIContext, log, tipoDatabase);
			}

		}catch(Exception e){
			log.error(ERROR_PREFIX + e.getMessage(),e);
		}
	}
	
	@Override
	public RegistroPlugins getRegistroPlugins() throws NotFoundException, CoreException {
		try {
			return this.driverConfigDB.getRegistroPlugins();
		}catch(DriverConfigurazioneNotFound notFound) {
			throw new NotFoundException(notFound.getMessage(), notFound);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}


}
