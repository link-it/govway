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
package org.openspcoop2.monitor.engine.config.base.dao.jdbc;

import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerProperties;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;

import org.openspcoop2.monitor.engine.config.base.dao.IPluginServiceSearch;
import org.openspcoop2.monitor.engine.config.base.dao.IPluginService;
import org.openspcoop2.monitor.engine.config.base.dao.IConfigurazioneServizioServiceSearch;
import org.openspcoop2.monitor.engine.config.base.dao.IConfigurazioneServizioService;
import org.openspcoop2.monitor.engine.config.base.dao.IConfigurazioneServizioAzioneServiceSearch;
import org.openspcoop2.monitor.engine.config.base.dao.IConfigurazioneServizioAzioneService;
import org.openspcoop2.monitor.engine.config.base.dao.IConfigurazioneFiltroServiceSearch;
import org.openspcoop2.monitor.engine.config.base.dao.IConfigurazioneFiltroService;

import org.openspcoop2.monitor.engine.config.base.dao.IServiceManager;

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
	 Services relating to the object with name:plugin type:plugin
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.monitor.engine.config.base.Plugin}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.monitor.engine.config.base.Plugin}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IPluginServiceSearch getPluginServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCPluginServiceSearch(this);
	}
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.monitor.engine.config.base.Plugin}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.monitor.engine.config.base.Plugin}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IPluginService getPluginService() throws ServiceException,NotImplementedException{
		return new JDBCPluginService(this);
	}
	
	
	
	/*
	 =====================================================================================================================
	 Services relating to the object with name:configurazione-servizio type:configurazione-servizio
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizio}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizio}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IConfigurazioneServizioServiceSearch getConfigurazioneServizioServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCConfigurazioneServizioServiceSearch(this);
	}
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizio}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizio}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IConfigurazioneServizioService getConfigurazioneServizioService() throws ServiceException,NotImplementedException{
		return new JDBCConfigurazioneServizioService(this);
	}
	
	
	
	/*
	 =====================================================================================================================
	 Services relating to the object with name:configurazione-servizio-azione type:configurazione-servizio-azione
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizioAzione}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizioAzione}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IConfigurazioneServizioAzioneServiceSearch getConfigurazioneServizioAzioneServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCConfigurazioneServizioAzioneServiceSearch(this);
	}
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizioAzione}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizioAzione}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IConfigurazioneServizioAzioneService getConfigurazioneServizioAzioneService() throws ServiceException,NotImplementedException{
		return new JDBCConfigurazioneServizioAzioneService(this);
	}
	
	
	
	/*
	 =====================================================================================================================
	 Services relating to the object with name:configurazione-filtro type:configurazione-filtro
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneFiltro}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneFiltro}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IConfigurazioneFiltroServiceSearch getConfigurazioneFiltroServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCConfigurazioneFiltroServiceSearch(this);
	}
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneFiltro}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneFiltro}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IConfigurazioneFiltroService getConfigurazioneFiltroService() throws ServiceException,NotImplementedException{
		return new JDBCConfigurazioneFiltroService(this);
	}
	
	
	
	

}
