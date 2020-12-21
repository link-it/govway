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
package org.openspcoop2.web.monitor.core.dao;

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
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.slf4j.Logger;

/**
 * RegistroPluginsService
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class RegistroPluginsService implements IRegistroPluginsReader {

	private static Logger log =  LoggerManager.getPddMonitorSqlLogger();
	
	private transient DriverConfigurazioneDB driverConfigDB = null;
		
	public RegistroPluginsService(){
		try{
			this._init(null, null);
			
		}catch(Exception e){
			RegistroPluginsService.log.error("Errore durante la creazione del Service: " + e.getMessage(),e);
		}
	}
	public RegistroPluginsService(Connection con, boolean autoCommit){
		this(con, autoCommit, null, RegistroPluginsService.log);
	}
	public RegistroPluginsService(Connection con, boolean autoCommit, Logger log){
		this(con, autoCommit, null, log);
	}
	public RegistroPluginsService(Connection con, boolean autoCommit, ServiceManagerProperties serviceManagerProperties){
		this(con, autoCommit, serviceManagerProperties, RegistroPluginsService.log);
	}
	public RegistroPluginsService(Connection con, boolean autoCommit, ServiceManagerProperties serviceManagerProperties, Logger log){
		try{
			this._init(con, serviceManagerProperties);
			
		}catch(Exception e){
			RegistroPluginsService.log.error("Errore durante la creazione del Service: " + e.getMessage(),e);
		}
	}
	private void _init(Connection con, ServiceManagerProperties serviceManagerProperties) {
		try{
			String tipoDatabase = null;
			if(serviceManagerProperties!=null) {
				tipoDatabase = serviceManagerProperties.getDatabaseType();
			}
			else {
				tipoDatabase = DAOFactoryProperties.getInstance(RegistroPluginsService.log).getTipoDatabase(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
			}
			
			if(con!=null) {
				this.driverConfigDB = new DriverConfigurazioneDB(con, RegistroPluginsService.log, tipoDatabase);
			}
			else {
				String datasourceJNDIName = DAOFactoryProperties.getInstance(RegistroPluginsService.log).getDatasourceJNDIName(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
				Properties datasourceJNDIContext = DAOFactoryProperties.getInstance(RegistroPluginsService.log).getDatasourceJNDIContext(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
	
				this.driverConfigDB = new DriverConfigurazioneDB(datasourceJNDIName,datasourceJNDIContext, RegistroPluginsService.log, tipoDatabase);
			}

		}catch(Exception e){
			RegistroPluginsService.log.error("Errore durante la creazione del Service: " + e.getMessage(),e);
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
