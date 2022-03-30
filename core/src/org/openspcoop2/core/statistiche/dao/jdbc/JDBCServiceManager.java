/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.core.statistiche.dao.jdbc;

import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerProperties;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;

import org.openspcoop2.core.statistiche.dao.IStatisticaInfoServiceSearch;
import org.openspcoop2.core.statistiche.dao.IStatisticaInfoService;
import org.openspcoop2.core.statistiche.dao.IStatisticaOrariaServiceSearch;
import org.openspcoop2.core.statistiche.dao.IStatisticaOrariaService;
import org.openspcoop2.core.statistiche.dao.IStatisticaGiornalieraServiceSearch;
import org.openspcoop2.core.statistiche.dao.IStatisticaGiornalieraService;
import org.openspcoop2.core.statistiche.dao.IStatisticaSettimanaleServiceSearch;
import org.openspcoop2.core.statistiche.dao.IStatisticaSettimanaleService;
import org.openspcoop2.core.statistiche.dao.IStatisticaMensileServiceSearch;
import org.openspcoop2.core.statistiche.dao.IStatisticaMensileService;

import org.openspcoop2.core.statistiche.dao.IServiceManager;

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
	 Services relating to the object with name:statistica-info type:statistica-info
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaInfo}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaInfo}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IStatisticaInfoServiceSearch getStatisticaInfoServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCStatisticaInfoServiceSearch(this);
	}
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaInfo}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaInfo}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IStatisticaInfoService getStatisticaInfoService() throws ServiceException,NotImplementedException{
		return new JDBCStatisticaInfoService(this);
	}
	
	
	
	/*
	 =====================================================================================================================
	 Services relating to the object with name:statistica-oraria type:statistica-oraria
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaOraria}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaOraria}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IStatisticaOrariaServiceSearch getStatisticaOrariaServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCStatisticaOrariaServiceSearch(this);
	}
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaOraria}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaOraria}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IStatisticaOrariaService getStatisticaOrariaService() throws ServiceException,NotImplementedException{
		return new JDBCStatisticaOrariaService(this);
	}
	
	
	
	/*
	 =====================================================================================================================
	 Services relating to the object with name:statistica-giornaliera type:statistica-giornaliera
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IStatisticaGiornalieraServiceSearch getStatisticaGiornalieraServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCStatisticaGiornalieraServiceSearch(this);
	}
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IStatisticaGiornalieraService getStatisticaGiornalieraService() throws ServiceException,NotImplementedException{
		return new JDBCStatisticaGiornalieraService(this);
	}
	
	
	
	/*
	 =====================================================================================================================
	 Services relating to the object with name:statistica-settimanale type:statistica-settimanale
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IStatisticaSettimanaleServiceSearch getStatisticaSettimanaleServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCStatisticaSettimanaleServiceSearch(this);
	}
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IStatisticaSettimanaleService getStatisticaSettimanaleService() throws ServiceException,NotImplementedException{
		return new JDBCStatisticaSettimanaleService(this);
	}
	
	
	
	/*
	 =====================================================================================================================
	 Services relating to the object with name:statistica-mensile type:statistica-mensile
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaMensile}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaMensile}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IStatisticaMensileServiceSearch getStatisticaMensileServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCStatisticaMensileServiceSearch(this);
	}
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaMensile}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaMensile}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IStatisticaMensileService getStatisticaMensileService() throws ServiceException,NotImplementedException{
		return new JDBCStatisticaMensileService(this);
	}
	
	
	
	

}
