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
package org.openspcoop2.generic_project.dao.jdbc;


import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.openspcoop2.generic_project.beans.IProjectInfo;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.GestoreJNDI;

/**
 * JDBCServiceManager
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCServiceManager {

	/** DataSource. */
	protected DataSource datasource;
	/** Connection */
	protected Connection connection = null;
	/** Logger */
	protected Logger log = null;
	/** JDBC Properties */
	protected JDBCServiceManagerProperties jdbcProperties = null;
	
	/** Tipo di costruttore */
	private JDBCServiceManagerTipoCostruttore tipoCostruttore = null;
	
	protected JDBCServiceManager(){}
	
	public JDBCServiceManager(String jndiName, Properties contextJNDI,ServiceManagerProperties serviceManagerProperties) throws ServiceException{
		this(jndiName, contextJNDI, new JDBCServiceManagerProperties(serviceManagerProperties), null);
	}
	public JDBCServiceManager(String jndiName, Properties contextJNDI,JDBCServiceManagerProperties jdbcProperties) throws ServiceException{
		this(jndiName, contextJNDI, jdbcProperties, null);
	}
	public JDBCServiceManager(String jndiName, Properties contextJNDI,ServiceManagerProperties serviceManagerProperties,Logger alog) throws ServiceException{
		this(jndiName, contextJNDI, new JDBCServiceManagerProperties(serviceManagerProperties), alog);
	}
	public JDBCServiceManager(String jndiName, Properties contextJNDI,JDBCServiceManagerProperties jdbcProperties,Logger alog) throws ServiceException{
		
		this.tipoCostruttore = JDBCServiceManagerTipoCostruttore.DATASOURCE_CFG;
		
		if(alog==null){
			this.log = LoggerWrapperFactory.getLogger(JDBCServiceManager.class);
		}else
			this.log = alog;
		
		try{
			
			GestoreJNDI gestoreJNDI = new GestoreJNDI(contextJNDI);
			this.datasource = (DataSource) gestoreJNDI.lookup(jndiName);
			if(this.datasource == null){
				throw new Exception("Datasource is null");
			}
			
			this.jdbcProperties = jdbcProperties;
			jdbcProperties.getDatabase(); // check tipoDatabase fornito
						
		} catch(Exception e) {
			this.log.error("Creating failure: "+e.getMessage(),e);
			throw new ServiceException("Creating failure: "+e.getMessage(),e);
		}
	}
	
	public JDBCServiceManager(DataSource ds,ServiceManagerProperties serviceManagerProperties) throws ServiceException{
		this(ds, new JDBCServiceManagerProperties(serviceManagerProperties), null);
	}
	public JDBCServiceManager(DataSource ds,JDBCServiceManagerProperties jdbcProperties) throws ServiceException{
		this(ds, jdbcProperties, null);
	}
	public JDBCServiceManager(DataSource ds,ServiceManagerProperties serviceManagerProperties,Logger alog) throws ServiceException{
		this(ds, new JDBCServiceManagerProperties(serviceManagerProperties), alog);
	}
	public JDBCServiceManager(DataSource ds,JDBCServiceManagerProperties jdbcProperties,Logger alog) throws ServiceException{
		
		this.tipoCostruttore = JDBCServiceManagerTipoCostruttore.DATASOURCE_OBJECT;
		
		if(alog==null){
			this.log = LoggerWrapperFactory.getLogger(JDBCServiceManager.class);
		}else
			this.log = alog;
		
		try{
			
			this.datasource = ds;
			if(this.datasource == null){
				throw new Exception("Datasource is null");
			}
			
			this.jdbcProperties = jdbcProperties;
			jdbcProperties.getDatabase(); // check tipoDatabase fornito
			
		} catch(Exception e) {
			this.log.error("Creating failure: "+e.getMessage(),e);
			throw new ServiceException("Creating failure: "+e.getMessage(),e);
		}
	}
	
	public JDBCServiceManager(String connectionUrl,String driverJDBC,String username,String password,ServiceManagerProperties serviceManagerProperties) throws ServiceException{
		this(connectionUrl,driverJDBC,username,password,new JDBCServiceManagerProperties(serviceManagerProperties),null);
	}
	public JDBCServiceManager(String connectionUrl,String driverJDBC,String username,String password,JDBCServiceManagerProperties jdbcProperties) throws ServiceException{
		this(connectionUrl,driverJDBC,username,password,jdbcProperties,null);
	}
	public JDBCServiceManager(String connectionUrl,String driverJDBC,String username,String password,ServiceManagerProperties serviceManagerProperties,Logger alog) throws ServiceException{
		this(connectionUrl,driverJDBC,username,password,new JDBCServiceManagerProperties(serviceManagerProperties),alog);
	}
	public JDBCServiceManager(String connectionUrl,String driverJDBC,String username,String password,JDBCServiceManagerProperties jdbcProperties,Logger alog) throws ServiceException{
		
		this.tipoCostruttore = JDBCServiceManagerTipoCostruttore.CONNECTION_CFG;
		
		if(alog==null){
			this.log = LoggerWrapperFactory.getLogger(JDBCServiceManager.class);
		}else
			this.log = alog;
		
		try{
			
			Class.forName(driverJDBC).newInstance();
			if(username!=null){
				this.connection = DriverManager.getConnection(connectionUrl,username,password);
			}else{
				this.connection = DriverManager.getConnection(connectionUrl);
			}
			if(this.connection == null){
				throw new Exception("Connection is null");
			}
			
			this.jdbcProperties = jdbcProperties;
			jdbcProperties.getDatabase(); // check tipoDatabase fornito
			
		} catch(Exception e) {
			this.log.error("Creating failure: "+e.getMessage(),e);
			throw new ServiceException("Creating failure: "+e.getMessage(),e);
		}
	}
	
	public JDBCServiceManager(Connection connection,ServiceManagerProperties serviceManagerProperties) throws ServiceException{
		this(connection, new JDBCServiceManagerProperties(serviceManagerProperties), null);
	}
	public JDBCServiceManager(Connection connection,JDBCServiceManagerProperties jdbcProperties) throws ServiceException{
		this(connection, jdbcProperties, null);
	}
	public JDBCServiceManager(Connection connection,ServiceManagerProperties serviceManagerProperties,Logger alog) throws ServiceException{
		this(connection, new JDBCServiceManagerProperties(serviceManagerProperties), alog);
	}
	public JDBCServiceManager(Connection connection,JDBCServiceManagerProperties jdbcProperties,Logger alog) throws ServiceException{
		
		this.tipoCostruttore = JDBCServiceManagerTipoCostruttore.CONNECTION_OBJECT;
		
		if(alog==null){
			this.log = LoggerWrapperFactory.getLogger(JDBCServiceManager.class);
		}else
			this.log = alog;
		
		try{
			
			this.connection = connection;
			if(this.connection == null){
				throw new Exception("Connection is null");
			}
			
			this.jdbcProperties = jdbcProperties;
			jdbcProperties.getDatabase(); // check tipoDatabase fornito
			
		} catch(Exception e) {
			this.log.error("Creating failure: "+e.getMessage(),e);
			throw new ServiceException("Creating failure: "+e.getMessage(),e);
		}
	}
	
	
	/* ** Destrory Service Manager */
	
	public void close() throws ServiceException{
		try{
			if(this.tipoCostruttore!=null){
				switch (this.tipoCostruttore) {
				case CONNECTION_CFG:
					if(this.connection!=null){
						this.connection.close();
					}
					break;
	
				default:
					break;
				}
			}
		}catch(Exception e){
			throw new ServiceException("Close failure: "+e.getMessage(),e);
		}
	}
	
	
	/* ** Utilities */
		
	// Metodi che non devono essere utilizzati dagli sviluppatori jdbc che implementano i metodi veri e propri
	
	protected Connection getConnection() throws ServiceException {
		try{
			if(this.datasource!=null){
				return this.datasource.getConnection();
			}
			else if(this.connection!=null){
				return this.connection;
			}else{
				throw new Exception("ServiceManager not initialized");
			}
		}catch(Exception e){
			throw new ServiceException("Get Connection failure: "+e.getMessage(),e);
		}
	}
	protected void closeConnection(Connection connection) throws ServiceException {
		try{
			if(connection==null){
				throw new ServiceException("Connection is null");
			}
			if(this.datasource!=null){
				// DATASOURCE_*
				connection.close();
			}
			else if(this.connection!=null){
				// CONNECTION_*
				// se la connessione e' stata fornita come parametro del costruttore del service manager (quindi salvata in this.connection instance variable)
				// non deve essere mai chiusa. La gestione e' a carico di chi ha inizializzato il service manager fornendo la connessione:
				// - se ha usato il costruttore fornendo l'oggetto connection, deve fare lui la close sulla clonnection
				// - se ha usato il costruttore fornendo i dati per creare una connessione (url,username,password) deve chiamare il metodo close del service manager.
				if(connection.isClosed()){
					throw new Exception("The connection provided shall not be closed");
				}
			}else{
				throw new Exception("ServiceManager not initialized");
			}
		}catch(Exception e){
			throw new ServiceException("Get Connection failure: "+e.getMessage(),e);
		}
	}
	public Logger getLog() {
		return this.log;
	}
	public JDBCServiceManagerProperties getJdbcProperties() {
		return this.jdbcProperties;
	}
	
	/* Logger */
	public static void configureDefaultLog4jProperties(IProjectInfo project) throws ServiceException{
		JDBCLoggerProperties loggerProperties = new JDBCLoggerProperties(project);
		loggerProperties.configureLog4j();
	}
	public static void configureLog4jProperties(File log4jProperties) throws ServiceException{
		JDBCLoggerProperties loggerProperties = new JDBCLoggerProperties(null,log4jProperties);
		loggerProperties.configureLog4j();
	}
}
