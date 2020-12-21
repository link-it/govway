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
package org.openspcoop2.core.allarmi.dao.jdbc;

import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerProperties;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;

import org.openspcoop2.core.allarmi.dao.IAllarmeServiceSearch;
import org.openspcoop2.core.allarmi.dao.IAllarmeService;
import org.openspcoop2.core.allarmi.dao.IAllarmeHistoryServiceSearch;
import org.openspcoop2.core.allarmi.dao.IAllarmeHistoryService;

import org.openspcoop2.core.allarmi.dao.IServiceManager;

import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;

/**     
 * Manager that allows you to obtain the services of research and management of objects
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class JDBCServiceManager extends org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManager implements IServiceManager {

	protected Connection get_Connection() throws ServiceException {
		return this.connection;
	}
	protected DataSource get_Datasource() throws ServiceException {
		return this.datasource;
	}
	protected JDBCServiceManagerProperties get_JdbcProperties(){
		return this.jdbcProperties;
	}
	protected Logger get_Logger(){
		return this.log;
	}
	@Override
	protected Connection getConnection() throws ServiceException {
		return super.getConnection();
	}
	@Override
	protected void closeConnection(Connection connection) throws ServiceException {
		super.closeConnection(connection);
	}

	protected JDBCServiceManager(){}

	public JDBCServiceManager(String jndiName, Properties contextJNDI,
			ServiceManagerProperties smProperties) throws ServiceException {
		super(jndiName, contextJNDI, smProperties);
	}
	public JDBCServiceManager(String jndiName, Properties contextJNDI,
			JDBCServiceManagerProperties jdbcProperties) throws ServiceException {
		super(jndiName, contextJNDI, jdbcProperties);
	}
	public JDBCServiceManager(String jndiName, Properties contextJNDI,
			ServiceManagerProperties smProperties, Logger alog) throws ServiceException {
		super(jndiName, contextJNDI, smProperties, alog);
	}
	public JDBCServiceManager(String jndiName, Properties contextJNDI,
			JDBCServiceManagerProperties jdbcProperties, Logger alog) throws ServiceException {
		super(jndiName, contextJNDI, jdbcProperties, alog);
	}
	
	
	public JDBCServiceManager(DataSource ds, ServiceManagerProperties smProperties)
			throws ServiceException {
		super(ds, smProperties);
	}
	public JDBCServiceManager(DataSource ds, JDBCServiceManagerProperties jdbcProperties)
			throws ServiceException {
		super(ds, jdbcProperties);
	}
	public JDBCServiceManager(DataSource ds, ServiceManagerProperties smProperties, Logger alog)
			throws ServiceException {
		super(ds, smProperties, alog);
	}
	public JDBCServiceManager(DataSource ds, JDBCServiceManagerProperties jdbcProperties, Logger alog)
			throws ServiceException {
		super(ds, jdbcProperties, alog);
	}
	
	
	public JDBCServiceManager(String connectionUrl, String driverJDBC,
			String username, String password, ServiceManagerProperties smProperties)
			throws ServiceException {
		super(connectionUrl, driverJDBC, username, password, smProperties);
	}
	public JDBCServiceManager(String connectionUrl, String driverJDBC,
			String username, String password, JDBCServiceManagerProperties jdbcProperties)
			throws ServiceException {
		super(connectionUrl, driverJDBC, username, password, jdbcProperties);
	}
	public JDBCServiceManager(String connectionUrl, String driverJDBC,
			String username, String password, ServiceManagerProperties smProperties, Logger alog)
			throws ServiceException {
		super(connectionUrl, driverJDBC, username, password, smProperties, alog);
	}
	public JDBCServiceManager(String connectionUrl, String driverJDBC,
			String username, String password, JDBCServiceManagerProperties jdbcProperties, Logger alog)
			throws ServiceException {
		super(connectionUrl, driverJDBC, username, password, jdbcProperties, alog);
	}
	
	
	public JDBCServiceManager(Connection connection, ServiceManagerProperties smProperties)
			throws ServiceException {
		super(connection, smProperties);
	}
	public JDBCServiceManager(Connection connection, JDBCServiceManagerProperties jdbcProperties)
			throws ServiceException {
		super(connection, jdbcProperties);
	}
	public JDBCServiceManager(Connection connection, ServiceManagerProperties smProperties, Logger alog) throws ServiceException {
		super(connection, smProperties, alog);
	}
	public JDBCServiceManager(Connection connection, JDBCServiceManagerProperties jdbcProperties, Logger alog) throws ServiceException {
		super(connection, jdbcProperties, alog);
	}


	


	/*
	 =====================================================================================================================
	 Services relating to the object with name:allarme type:allarme
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.allarmi.Allarme}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.allarmi.Allarme}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IAllarmeServiceSearch getAllarmeServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCAllarmeServiceSearch(this);
	}
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.allarmi.Allarme}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.allarmi.Allarme}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IAllarmeService getAllarmeService() throws ServiceException,NotImplementedException{
		return new JDBCAllarmeService(this);
	}
	
	
	
	/*
	 =====================================================================================================================
	 Services relating to the object with name:allarme-history type:allarme-history
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.allarmi.AllarmeHistory}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.allarmi.AllarmeHistory}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IAllarmeHistoryServiceSearch getAllarmeHistoryServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCAllarmeHistoryServiceSearch(this);
	}
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.allarmi.AllarmeHistory}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.allarmi.AllarmeHistory}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IAllarmeHistoryService getAllarmeHistoryService() throws ServiceException,NotImplementedException{
		return new JDBCAllarmeHistoryService(this);
	}
	
	
	
	

}
