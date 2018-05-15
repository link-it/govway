/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.core.transazioni.dao.jdbc;

import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.Connection;

import javax.sql.DataSource;

import org.openspcoop2.core.transazioni.dao.ITransazioneServiceSearch;
import org.openspcoop2.core.transazioni.dao.ITransazioneService;
import org.openspcoop2.core.transazioni.dao.ITransazioneInfoServiceSearch;
import org.openspcoop2.core.transazioni.dao.ITransazioneInfoService;
import org.openspcoop2.core.transazioni.dao.ITransazioneExportServiceSearch;
import org.openspcoop2.core.transazioni.dao.ITransazioneExportService;
import org.openspcoop2.core.transazioni.dao.IDumpMessaggioServiceSearch;
import org.openspcoop2.core.transazioni.dao.IDumpMessaggioService;

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
	 Services relating to the object with name:transazione type:transazione
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.transazioni.Transazione}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.transazioni.Transazione}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public ITransazioneServiceSearch getTransazioneServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCTransazioneServiceSearch(this.unlimitedJdbcServiceManager);
	}
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.transazioni.Transazione}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.transazioni.Transazione}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public ITransazioneService getTransazioneService() throws ServiceException,NotImplementedException{
		return new JDBCTransazioneService(this.unlimitedJdbcServiceManager);
	}
	
	
	
	/*
	 =====================================================================================================================
	 Services relating to the object with name:transazione-info type:transazione-info
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.transazioni.TransazioneInfo}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.transazioni.TransazioneInfo}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public ITransazioneInfoServiceSearch getTransazioneInfoServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCTransazioneInfoServiceSearch(this.unlimitedJdbcServiceManager);
	}
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.transazioni.TransazioneInfo}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.transazioni.TransazioneInfo}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public ITransazioneInfoService getTransazioneInfoService() throws ServiceException,NotImplementedException{
		return new JDBCTransazioneInfoService(this.unlimitedJdbcServiceManager);
	}
	
	
	
	/*
	 =====================================================================================================================
	 Services relating to the object with name:transazione-export type:transazione-export
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.transazioni.TransazioneExport}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.transazioni.TransazioneExport}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public ITransazioneExportServiceSearch getTransazioneExportServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCTransazioneExportServiceSearch(this.unlimitedJdbcServiceManager);
	}
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.transazioni.TransazioneExport}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.transazioni.TransazioneExport}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public ITransazioneExportService getTransazioneExportService() throws ServiceException,NotImplementedException{
		return new JDBCTransazioneExportService(this.unlimitedJdbcServiceManager);
	}
	
	
	
	/*
	 =====================================================================================================================
	 Services relating to the object with name:dump-messaggio type:dump-messaggio
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.transazioni.DumpMessaggio}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IDumpMessaggioServiceSearch getDumpMessaggioServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCDumpMessaggioServiceSearch(this.unlimitedJdbcServiceManager);
	}
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.transazioni.DumpMessaggio}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IDumpMessaggioService getDumpMessaggioService() throws ServiceException,NotImplementedException{
		return new JDBCDumpMessaggioService(this.unlimitedJdbcServiceManager);
	}
	
	
	
}