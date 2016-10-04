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
package org.openspcoop2.generic_project.utils;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.openspcoop2.generic_project.exception.ServiceException;

/**
 * DatabaseFactory
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatabaseFactory<T> {

	private Logger log = null;
	private DatabaseProperties databaseProperties = null;
	private Class<T> cServiceManager = null;
	
	public DatabaseFactory(Class<T> cServiceManager,String PROPERTIES_LOCAL_PATH, String PROPERTIES_NAME, String nomeFileProperties,org.slf4j.Logger log) throws ServiceException{
		try{
			this.cServiceManager = cServiceManager;
			org.openspcoop2.generic_project.utils.DatabaseProperties.initialize(PROPERTIES_LOCAL_PATH, PROPERTIES_NAME, nomeFileProperties, log);
			this.log = log;
			this.databaseProperties = DatabaseProperties.getInstance(log); 
		}catch(Exception e){
			throw new ServiceException(e.getMessage(),e);
		}
	}
	

	
	private T getServiceManager(DataSource ds,Connection connection,Boolean isAutocommit) throws ServiceException{
		
		try{
		
			Constructor<T> constrServiceManager = null;
			T object = null;
			ServiceManagerProperties smProperties = this.databaseProperties.getServiceManagerProperties();
			if(isAutocommit!=null){
				smProperties.setAutomaticTransactionManagement(isAutocommit);
			}
			
			if(ds!=null){
				constrServiceManager = this.cServiceManager
						.getConstructor(DataSource.class,
								ServiceManagerProperties.class, Logger.class);
				object = constrServiceManager
						.newInstance(ds,
								smProperties,this.log);
			}	
			else if(connection!=null){
				constrServiceManager = this.cServiceManager
						.getConstructor(Connection.class,
								ServiceManagerProperties.class, Logger.class);
				object = constrServiceManager
						.newInstance(connection,
								smProperties,this.log);
			}
			else{	
				if(this.databaseProperties.isTipoAccessoTramiteDatasource()){
					constrServiceManager = this.cServiceManager
							.getConstructor(String.class, 
									Properties.class,
									ServiceManagerProperties.class, Logger.class);
					object = constrServiceManager
							.newInstance(this.databaseProperties.getDatasourceJNDIName(),
									this.databaseProperties.getDatasourceJNDIContext(),
									smProperties,this.log);
				}
				else{
					constrServiceManager = this.cServiceManager
							.getConstructor(String.class, 
									String.class, 
									String.class, 
									String.class,
									ServiceManagerProperties.class, Logger.class);
					object = constrServiceManager
							.newInstance(this.databaseProperties.getConnectionUrl(),
									this.databaseProperties.getConnectionDriverJDBC(),
									this.databaseProperties.getConnectionAuthUsername(),
									this.databaseProperties.getConnectionAuthPassword(),
									smProperties,this.log);
				}		
			}
			
			return object;
			
		}catch(Exception e){
			throw new ServiceException(e.getMessage(),e);
		}
	}
	
	
	
	// Metodi
	public Object getServiceManager() throws ServiceException{
		return this.getServiceManager(null, null, null);
	}
	public Object getServiceManager(boolean autoCommit) throws ServiceException{
		return this.getServiceManager(null, null, autoCommit);
	}
	public Object getServiceManager(DataSource ds) throws ServiceException{
		return this.getServiceManager(ds, null, null);
	}
	public Object getServiceManager(DataSource ds,boolean autoCommit) throws ServiceException{
		return this.getServiceManager(ds, null, autoCommit);
	}
	public Object getServiceManager(Connection connection) throws ServiceException{
		return this.getServiceManager(null, connection, null);
	}
	public Object getServiceManager(Connection connection,boolean autoCommit) throws ServiceException{
		return this.getServiceManager(null, connection, autoCommit);
	}
	
}
