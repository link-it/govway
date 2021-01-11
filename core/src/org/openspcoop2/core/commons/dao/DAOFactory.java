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
package org.openspcoop2.core.commons.dao;

import org.openspcoop2.core.commons.IDAOFactory;
import org.openspcoop2.generic_project.beans.IProjectInfo;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

/**
 * DAOFactory
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DAOFactory implements IDAOFactory {

	/** Copia Statica */
	private static DAOFactory daoFactory = null;

	private static synchronized void initialize(Logger log) throws DAOFactoryException{

		if(DAOFactory.daoFactory==null)
			DAOFactory.daoFactory = new DAOFactory(log);	

	}

	public static DAOFactory getInstance(Logger log) throws DAOFactoryException{

		if(DAOFactory.daoFactory==null)
			DAOFactory.initialize(log);

		return DAOFactory.daoFactory;
	}


	private Logger log = null;
	private DAOFactoryProperties daoFactoryProperties = null;

	public DAOFactory(Logger log) throws DAOFactoryException{
		try{
			this.log = log;
			this.daoFactoryProperties = DAOFactoryProperties.getInstance(log); 
		}catch(Exception e){
			throw new DAOFactoryException(e.getMessage(),e);
		}
	}

	private Object getServiceManager(IProjectInfo tipoDAO,DataSource ds,Connection connection,Boolean isAutocommit, 
			ServiceManagerProperties smProperties, Logger log) throws DAOFactoryException{

		String daoClassName = null;
		String mode = "";
		org.openspcoop2.core.diagnostica.utils.ProjectInfo daoDiagnostica = org.openspcoop2.core.diagnostica.utils.ProjectInfo.getInstance();
		boolean isDiagnostica = daoDiagnostica.getProjectName().equals(tipoDAO.getProjectName());
		org.openspcoop2.core.tracciamento.utils.ProjectInfo daoTracciamento = org.openspcoop2.core.tracciamento.utils.ProjectInfo.getInstance();
		boolean isTracciamento = daoTracciamento.getProjectName().equals(tipoDAO.getProjectName());
		boolean isUtenti = "utenti".equals(tipoDAO.getProjectName());
		try{

			daoClassName = this.daoFactoryProperties.getDAOClassName(tipoDAO);
			Class<?> cServiceManager = Class.forName(daoClassName);

			if(!isTracciamento && !isDiagnostica && !isUtenti){
			
				Constructor<?> constrServiceManager = null;
				Object object = null;
				if(smProperties==null){
					smProperties =this.daoFactoryProperties.getServiceManagerProperties(tipoDAO);
				}
				if(isAutocommit!=null){
					smProperties.setAutomaticTransactionManagement(isAutocommit);
				}
	
				if(log==null){
					log = this.log;
				}
	
				if(ds!=null){
					mode = "DSMode";
					constrServiceManager = cServiceManager
							.getConstructor(DataSource.class,
									ServiceManagerProperties.class, Logger.class);
					object = constrServiceManager
							.newInstance(ds,
									smProperties,log);
				}	
				else if(connection!=null){
					mode = "ConnectionMode";
					constrServiceManager = cServiceManager
							.getConstructor(Connection.class,
									ServiceManagerProperties.class, Logger.class);
					object = constrServiceManager
							.newInstance(connection,
									smProperties,log);
				}
				else{	
					if(this.daoFactoryProperties.isTipoAccessoTramiteDatasource(tipoDAO)){
						mode = "LookupDSMode";
						constrServiceManager = cServiceManager
								.getConstructor(String.class, 
										Properties.class,
										ServiceManagerProperties.class, Logger.class);
						object = constrServiceManager
								.newInstance(this.daoFactoryProperties.getDatasourceJNDIName(tipoDAO),
										this.daoFactoryProperties.getDatasourceJNDIContext(tipoDAO),
										smProperties,log);
					}
					else{
						mode = "LookupConnectionMode";
						constrServiceManager = cServiceManager
								.getConstructor(String.class, 
										String.class, 
										String.class, 
										String.class,
										ServiceManagerProperties.class, Logger.class);
						object = constrServiceManager
								.newInstance(this.daoFactoryProperties.getConnectionUrl(tipoDAO),
										this.daoFactoryProperties.getConnectionDriverJDBC(tipoDAO),
										this.daoFactoryProperties.getConnectionAuthUsername(tipoDAO),
										this.daoFactoryProperties.getConnectionAuthPassword(tipoDAO),
										smProperties,log);
					}		
				}
	
				return object;
				
			}
			else{
			
				
				Constructor<?> constrServiceManager = null;
				Object object = null;
				if(smProperties==null){
					smProperties =this.daoFactoryProperties.getServiceManagerProperties(tipoDAO);
				}
				if(isAutocommit!=null){
					smProperties.setAutomaticTransactionManagement(isAutocommit);
				}
	
				if(log==null){
					log = this.log;
				}
	
				if(ds!=null){
					mode = "DSMode";
					constrServiceManager = cServiceManager
							.getConstructor(DataSource.class,
									String.class, Logger.class);
					object = constrServiceManager
							.newInstance(ds,
									smProperties.getDatabaseType(),log);
				}	
				else if(connection!=null){
					mode = "ConnectionMode";
					constrServiceManager = cServiceManager
							.getConstructor(Connection.class,
									String.class, Logger.class);
					object = constrServiceManager
							.newInstance(connection,
									smProperties.getDatabaseType(),log);
				}
				else{	
					if(this.daoFactoryProperties.isTipoAccessoTramiteDatasource(tipoDAO)){
						mode = "LookupDSMode";
						constrServiceManager = cServiceManager
								.getConstructor(String.class, 
										String.class, 
										Properties.class,
										Logger.class);
						object = constrServiceManager
								.newInstance(this.daoFactoryProperties.getDatasourceJNDIName(tipoDAO),
										smProperties.getDatabaseType(),
										this.daoFactoryProperties.getDatasourceJNDIContext(tipoDAO),
										log);
					}
					else{
						mode = "LookupConnectionMode";
						constrServiceManager = cServiceManager
								.getConstructor(String.class, 
										String.class, 
										String.class, 
										String.class,
										String.class, Logger.class);
						object = constrServiceManager
								.newInstance(this.daoFactoryProperties.getConnectionUrl(tipoDAO),
										this.daoFactoryProperties.getConnectionDriverJDBC(tipoDAO),
										this.daoFactoryProperties.getConnectionAuthUsername(tipoDAO),
										this.daoFactoryProperties.getConnectionAuthPassword(tipoDAO),
										smProperties.getDatabaseType(),log);
					}		
				}
	
				return object;
				
			}

		}catch(Exception e){
			throw new DAOFactoryException("[class:"+daoClassName+"][mode:"+mode+"]:"+e.getMessage(),e);
		}
	}






	@Override
	public Object getServiceManager(IProjectInfo dao) throws DAOFactoryException{
		return this.getServiceManager(dao, null, null, null, null, null);
	}
	@Override
	public Object getServiceManager(IProjectInfo dao, boolean autoCommit) throws DAOFactoryException{
		return this.getServiceManager(dao, null, null, autoCommit, null, null);
	}
	@Override
	public Object getServiceManager(IProjectInfo dao, DataSource ds) throws DAOFactoryException{
		return this.getServiceManager(dao, ds, null, null, null, null);
	}
	@Override
	public Object getServiceManager(IProjectInfo dao, DataSource ds,boolean autoCommit) throws DAOFactoryException{
		return this.getServiceManager(dao, ds, null, autoCommit, null, null);
	}
	@Override
	public Object getServiceManager(IProjectInfo dao, Connection connection) throws DAOFactoryException{
		return this.getServiceManager(dao, null, connection, null, null, null);
	}
	@Override
	public Object getServiceManager(IProjectInfo dao, Connection connection,boolean autoCommit) throws DAOFactoryException{
		return this.getServiceManager(dao, null, connection, autoCommit, null, null);
	}

	@Override
	public Object getServiceManager(IProjectInfo dao, ServiceManagerProperties smProperties) throws DAOFactoryException{
		return this.getServiceManager(dao, null, null, null, smProperties, null);
	}
	@Override
	public Object getServiceManager(IProjectInfo dao, boolean autoCommit, ServiceManagerProperties smProperties) throws DAOFactoryException{
		return this.getServiceManager(dao, null, null, autoCommit, smProperties, null);
	}
	@Override
	public Object getServiceManager(IProjectInfo dao, DataSource ds, ServiceManagerProperties smProperties) throws DAOFactoryException{
		return this.getServiceManager(dao, ds, null, null, smProperties, null);
	}
	@Override
	public Object getServiceManager(IProjectInfo dao, DataSource ds,boolean autoCommit, ServiceManagerProperties smProperties) throws DAOFactoryException{
		return this.getServiceManager(dao, ds, null, autoCommit, smProperties, null);
	}
	@Override
	public Object getServiceManager(IProjectInfo dao, Connection connection, ServiceManagerProperties smProperties) throws DAOFactoryException{
		return this.getServiceManager(dao, null, connection, null, smProperties, null);
	}
	@Override
	public Object getServiceManager(IProjectInfo dao, Connection connection,boolean autoCommit, ServiceManagerProperties smProperties) throws DAOFactoryException{
		return this.getServiceManager(dao, null, connection, autoCommit, smProperties, null);
	}

	@Override
	public Object getServiceManager(IProjectInfo dao, Logger log) throws DAOFactoryException{
		return this.getServiceManager(dao, null, null, null, null, log);
	}
	@Override
	public Object getServiceManager(IProjectInfo dao, boolean autoCommit, Logger log) throws DAOFactoryException{
		return this.getServiceManager(dao, null, null, autoCommit, null, log);
	}
	@Override
	public Object getServiceManager(IProjectInfo dao, DataSource ds, Logger log) throws DAOFactoryException{
		return this.getServiceManager(dao, ds, null, null, null, log);
	}
	@Override
	public Object getServiceManager(IProjectInfo dao, DataSource ds,boolean autoCommit, Logger log) throws DAOFactoryException{
		return this.getServiceManager(dao, ds, null, autoCommit, null, log);
	}
	@Override
	public Object getServiceManager(IProjectInfo dao, Connection connection, Logger log) throws DAOFactoryException{
		return this.getServiceManager(dao, null, connection, null, null, log);
	}
	@Override
	public Object getServiceManager(IProjectInfo dao, Connection connection,boolean autoCommit, Logger log) throws DAOFactoryException{
		return this.getServiceManager(dao, null, connection, autoCommit, null, log);
	}

	@Override
	public Object getServiceManager(IProjectInfo dao, ServiceManagerProperties smProperties, Logger log) throws DAOFactoryException{
		return this.getServiceManager(dao, null, null, null, smProperties, log);
	}
	@Override
	public Object getServiceManager(IProjectInfo dao, boolean autoCommit, ServiceManagerProperties smProperties, Logger log) throws DAOFactoryException{
		return this.getServiceManager(dao, null, null, autoCommit, smProperties, log);
	}
	@Override
	public Object getServiceManager(IProjectInfo dao, DataSource ds, ServiceManagerProperties smProperties, Logger log) throws DAOFactoryException{
		return this.getServiceManager(dao, ds, null, null, smProperties, log);
	}
	@Override
	public Object getServiceManager(IProjectInfo dao, DataSource ds,boolean autoCommit, ServiceManagerProperties smProperties, Logger log) throws DAOFactoryException{
		return this.getServiceManager(dao, ds, null, autoCommit, smProperties, log);
	}
	@Override
	public Object getServiceManager(IProjectInfo dao, Connection connection, ServiceManagerProperties smProperties, Logger log) throws DAOFactoryException{
		return this.getServiceManager(dao, null, connection, null, smProperties, log);
	}
	@Override
	public Object getServiceManager(IProjectInfo dao, Connection connection,boolean autoCommit, ServiceManagerProperties smProperties, Logger log) throws DAOFactoryException{
		return this.getServiceManager(dao, null, connection, autoCommit, smProperties, log);
	}

}
