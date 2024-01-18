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
package org.openspcoop2.core.transazioni.dao.jdbc;

import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.Connection;

import javax.sql.DataSource;

import org.openspcoop2.core.transazioni.dao.ICredenzialeMittenteServiceSearch;
import org.openspcoop2.core.transazioni.dao.ICredenzialeMittenteService;
import org.openspcoop2.core.transazioni.dao.ITransazioneServiceSearch;
import org.openspcoop2.core.transazioni.dao.ITransazioneService;
import org.openspcoop2.core.transazioni.dao.ITransazioneApplicativoServerServiceSearch;
import org.openspcoop2.core.transazioni.dao.ITransazioneApplicativoServerService;
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

	public JDBCLimitedServiceManager(JDBCServiceManager jdbcServiceManager) {
		this.datasource = jdbcServiceManager.getDatasourceInternalResource();
		this.connection = jdbcServiceManager.getConnectionInternalResource();
		this.log = jdbcServiceManager.getLoggerInternalResource();
		this.jdbcProperties = jdbcServiceManager.getJdbcPropertiesInternalResource();
		this.unlimitedJdbcServiceManager = jdbcServiceManager;
	}
	
	private static final String CONNNECTION_MANAGED = "Connection managed from framework";
	
	@Override
	public Connection getConnection() throws ServiceException {
		throw new ServiceException(CONNNECTION_MANAGED);
	}
	@Override
	public void closeConnection(Connection connection) throws ServiceException {
		throw new ServiceException(CONNNECTION_MANAGED);
	}
	@Override
	protected Connection getConnectionInternalResource() {
		throw new org.openspcoop2.utils.UtilsRuntimeException(CONNNECTION_MANAGED);
	}
	@Override
	protected DataSource getDatasourceInternalResource() {
		throw new org.openspcoop2.utils.UtilsRuntimeException(CONNNECTION_MANAGED);
	}
	
	
	
	/*
	 =====================================================================================================================
	 Services relating to the object with name:credenziale-mittente type:credenziale-mittente
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.transazioni.CredenzialeMittente}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.transazioni.CredenzialeMittente}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public ICredenzialeMittenteServiceSearch getCredenzialeMittenteServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCCredenzialeMittenteServiceSearch(this.unlimitedJdbcServiceManager);
	}
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.transazioni.CredenzialeMittente}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.transazioni.CredenzialeMittente}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public ICredenzialeMittenteService getCredenzialeMittenteService() throws ServiceException,NotImplementedException{
		return new JDBCCredenzialeMittenteService(this.unlimitedJdbcServiceManager);
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
	 Services relating to the object with name:transazione-applicativo-server type:transazione-applicativo-server
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.transazioni.TransazioneApplicativoServer}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.transazioni.TransazioneApplicativoServer}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public ITransazioneApplicativoServerServiceSearch getTransazioneApplicativoServerServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCTransazioneApplicativoServerServiceSearch(this.unlimitedJdbcServiceManager);
	}
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.transazioni.TransazioneApplicativoServer}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.transazioni.TransazioneApplicativoServer}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public ITransazioneApplicativoServerService getTransazioneApplicativoServerService() throws ServiceException,NotImplementedException{
		return new JDBCTransazioneApplicativoServerService(this.unlimitedJdbcServiceManager);
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