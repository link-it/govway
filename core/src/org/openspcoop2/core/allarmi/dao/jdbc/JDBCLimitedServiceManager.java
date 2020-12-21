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

import java.sql.Connection;

import javax.sql.DataSource;

import org.openspcoop2.core.allarmi.dao.IAllarmeServiceSearch;
import org.openspcoop2.core.allarmi.dao.IAllarmeService;
import org.openspcoop2.core.allarmi.dao.IAllarmeHistoryServiceSearch;
import org.openspcoop2.core.allarmi.dao.IAllarmeHistoryService;

/**     
 * Manager that allows you to obtain the services of research and management of objects
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class JDBCLimitedServiceManager extends JDBCServiceManager {

	private JDBCServiceManager unlimitedJdbcServiceManager;

	public JDBCLimitedServiceManager(JDBCServiceManager jdbcServiceManager) throws ServiceException {
		this.datasource = jdbcServiceManager.get_Datasource();
		this.connection = jdbcServiceManager.get_Connection();
		this.log = jdbcServiceManager.get_Logger();
		this.jdbcProperties = jdbcServiceManager.get_JdbcProperties();
		this.unlimitedJdbcServiceManager = jdbcServiceManager;
	}
	
	
	@Override
	public Connection getConnection() throws ServiceException {
		throw new ServiceException("Connection managed from framework");
	}
	@Override
	public void closeConnection(Connection connection) throws ServiceException {
		throw new ServiceException("Connection managed from framework");
	}
	@Override
	protected Connection get_Connection() throws ServiceException {
		throw new ServiceException("Connection managed from framework");
	}
	@Override
	protected DataSource get_Datasource() throws ServiceException {
		throw new ServiceException("Connection managed from framework");
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
		return new JDBCAllarmeServiceSearch(this.unlimitedJdbcServiceManager);
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
		return new JDBCAllarmeService(this.unlimitedJdbcServiceManager);
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
		return new JDBCAllarmeHistoryServiceSearch(this.unlimitedJdbcServiceManager);
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
		return new JDBCAllarmeHistoryService(this.unlimitedJdbcServiceManager);
	}
	
	
	
}