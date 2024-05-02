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

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
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
		
	public LoaderRegistroPluginsService(Logger log){
		try{
			this.initEngine(null, null, log);
			
		}catch(Exception e){
			String msgError = ERROR_PREFIX + e.getMessage();
			log.error(msgError,e);
		}
	}
	public LoaderRegistroPluginsService(Connection con, boolean autoCommit, Logger log){
		this(con, autoCommit, null, log);
	}
	public LoaderRegistroPluginsService(Connection con, boolean autoCommit, ServiceManagerProperties serviceManagerProperties, Logger log){
		try{
			if(autoCommit) {
				// nop
			}
			this.initEngine(con, serviceManagerProperties, log);
		}catch(Exception e){
			log.error(ERROR_PREFIX + e.getMessage(),e);
		}
	}
	private void initEngine(Connection con, ServiceManagerProperties serviceManagerProperties, Logger log) {
		try{
			String tipoDatabase = null;
			if(serviceManagerProperties!=null) {
				tipoDatabase = serviceManagerProperties.getDatabaseType();
			}
			else {
				tipoDatabase = DAOFactoryProperties.getInstance(log).getTipoDatabase(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
			}
			
			if(con!=null) {
				this.driverConfigDB = new DriverConfigurazioneDB(con, log, tipoDatabase);
			}
			else {
				String datasourceJNDIName = DAOFactoryProperties.getInstance(log).getDatasourceJNDIName(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
				Properties datasourceJNDIContext = DAOFactoryProperties.getInstance(log).getDatasourceJNDIContext(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
	
				this.driverConfigDB = new DriverConfigurazioneDB(datasourceJNDIName,datasourceJNDIContext, log, tipoDatabase);
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
