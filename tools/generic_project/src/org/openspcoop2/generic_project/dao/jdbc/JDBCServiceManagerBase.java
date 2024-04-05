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
package org.openspcoop2.generic_project.dao.jdbc;


import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Date;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.openspcoop2.generic_project.beans.IProjectInfo;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.ClassLoaderUtilities;
import org.openspcoop2.utils.resources.GestoreJNDI;

/**
 * JDBCServiceManager
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCServiceManagerBase {

	/** DataSource. */
	protected DataSource datasource;
	/** Connection */
	protected Connection connection = null;
	/** Logger */
	protected Logger log = null;
	/** JDBC Properties */
	protected JDBCServiceManagerProperties jdbcProperties = null;
	
	/** Parametri per rinegoziare la connessione */
	private String connectionUrl;
	private String username;
	private String password;
	private int secondsToRefreshConnection = -1;
	private Date connectionDate;
	
	/** Tipo di costruttore */
	private JDBCServiceManagerTipoCostruttore tipoCostruttore = null;
	
	protected JDBCServiceManagerBase(){}
	
	public JDBCServiceManagerBase(String jndiName, Properties contextJNDI,ServiceManagerProperties serviceManagerProperties) throws ServiceException{
		this(jndiName, contextJNDI, new JDBCServiceManagerProperties(serviceManagerProperties), null);
	}
	public JDBCServiceManagerBase(String jndiName, Properties contextJNDI,JDBCServiceManagerProperties jdbcProperties) throws ServiceException{
		this(jndiName, contextJNDI, jdbcProperties, null);
	}
	public JDBCServiceManagerBase(String jndiName, Properties contextJNDI,ServiceManagerProperties serviceManagerProperties,Logger alog) throws ServiceException{
		this(jndiName, contextJNDI, new JDBCServiceManagerProperties(serviceManagerProperties), alog);
	}
	public JDBCServiceManagerBase(String jndiName, Properties contextJNDI,JDBCServiceManagerProperties jdbcProperties,Logger alog) throws ServiceException{
		
		this.tipoCostruttore = JDBCServiceManagerTipoCostruttore.DATASOURCE_CFG;
		
		if(alog==null){
			this.log = LoggerWrapperFactory.getLogger(JDBCServiceManagerBase.class);
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
	
	public JDBCServiceManagerBase(DataSource ds,ServiceManagerProperties serviceManagerProperties) throws ServiceException{
		this(ds, new JDBCServiceManagerProperties(serviceManagerProperties), null);
	}
	public JDBCServiceManagerBase(DataSource ds,JDBCServiceManagerProperties jdbcProperties) throws ServiceException{
		this(ds, jdbcProperties, null);
	}
	public JDBCServiceManagerBase(DataSource ds,ServiceManagerProperties serviceManagerProperties,Logger alog) throws ServiceException{
		this(ds, new JDBCServiceManagerProperties(serviceManagerProperties), alog);
	}
	public JDBCServiceManagerBase(DataSource ds,JDBCServiceManagerProperties jdbcProperties,Logger alog) throws ServiceException{
		
		this.tipoCostruttore = JDBCServiceManagerTipoCostruttore.DATASOURCE_OBJECT;
		
		if(alog==null){
			this.log = LoggerWrapperFactory.getLogger(JDBCServiceManagerBase.class);
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
	
	public JDBCServiceManagerBase(String connectionUrl,String driverJDBC,String username,String password,ServiceManagerProperties serviceManagerProperties) throws ServiceException{
		this(connectionUrl,driverJDBC,username,password,new JDBCServiceManagerProperties(serviceManagerProperties),null);
	}
	public JDBCServiceManagerBase(String connectionUrl,String driverJDBC,String username,String password,JDBCServiceManagerProperties jdbcProperties) throws ServiceException{
		this(connectionUrl,driverJDBC,username,password,jdbcProperties,null);
	}
	public JDBCServiceManagerBase(String connectionUrl,String driverJDBC,String username,String password,ServiceManagerProperties serviceManagerProperties,Logger alog) throws ServiceException{
		this(connectionUrl,driverJDBC,username,password,new JDBCServiceManagerProperties(serviceManagerProperties),alog);
	}
	public JDBCServiceManagerBase(String connectionUrl,String driverJDBC,String username,String password,JDBCServiceManagerProperties jdbcProperties,Logger alog) throws ServiceException{
		
		this.tipoCostruttore = JDBCServiceManagerTipoCostruttore.CONNECTION_CFG;
		
		if(alog==null){
			this.log = LoggerWrapperFactory.getLogger(JDBCServiceManagerBase.class);
		}else
			this.log = alog;
		
		try{
			
			this.connectionUrl = connectionUrl;
			this.username = username;
			this.password = password;
			this.secondsToRefreshConnection = jdbcProperties.getSecondsToRefreshConnection();
			
			ClassLoaderUtilities.newInstance(driverJDBC);
			
			setConnection();
			
			this.jdbcProperties = jdbcProperties;
			jdbcProperties.getDatabase(); // check tipoDatabase fornito
			
		} catch(Exception e) {
			this.log.error("Creating failure: "+e.getMessage(),e);
			throw new ServiceException("Creating failure: "+e.getMessage(),e);
		}
	}
	private void setConnection() throws Exception {
		if(this.username!=null){
			this.connection = DriverManager.getConnection(this.connectionUrl,this.username,this.password);
		}else{
			this.connection = DriverManager.getConnection(this.connectionUrl);
		}
		/*if(this.connection == null){
			throw new Exception("Connection is null");
		}*/
		this.connectionDate = DateManager.getDate();
	}
	private boolean isConnectionExpired() {
		if(this.secondsToRefreshConnection>0) {
			Date now = DateManager.getDate();
			Date expireDate = new Date(this.connectionDate.getTime()+(this.secondsToRefreshConnection*1000));
			if(expireDate.before(now)) {
				return true;
			}
		}
		return false;
	}
	private void refreshConnection() throws Exception {
		if(this.isConnectionExpired()) {
			this._refreshConnection();
		}
	}
	private synchronized void _refreshConnection() throws Exception {
		if(this.isConnectionExpired()) {
			//System.out.println("REFRESH (user:"+this.username+" connection-url: "+this.connectionUrl+")");
			try {
				if(this.connection!=null) {
					this.connection.close();
				}
			}catch(Throwable t) {}
			setConnection();
		}
	}
	
	public JDBCServiceManagerBase(Connection connection,ServiceManagerProperties serviceManagerProperties) throws ServiceException{
		this(connection, new JDBCServiceManagerProperties(serviceManagerProperties), null);
	}
	public JDBCServiceManagerBase(Connection connection,JDBCServiceManagerProperties jdbcProperties) throws ServiceException{
		this(connection, jdbcProperties, null);
	}
	public JDBCServiceManagerBase(Connection connection,ServiceManagerProperties serviceManagerProperties,Logger alog) throws ServiceException{
		this(connection, new JDBCServiceManagerProperties(serviceManagerProperties), alog);
	}
	public JDBCServiceManagerBase(Connection connection,JDBCServiceManagerProperties jdbcProperties,Logger alog) throws ServiceException{
		
		this.tipoCostruttore = JDBCServiceManagerTipoCostruttore.CONNECTION_OBJECT;
		
		if(alog==null){
			this.log = LoggerWrapperFactory.getLogger(JDBCServiceManagerBase.class);
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
						//System.out.println("CLOSE (user:"+this.username+" connection-url: "+this.connectionUrl+")");
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
				if(JDBCServiceManagerTipoCostruttore.CONNECTION_CFG.equals(this.tipoCostruttore)) {
					this.refreshConnection();
				}
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
