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
package org.openspcoop2.core.statistiche.dao.jdbc;

import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.Connection;

import javax.sql.DataSource;

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
		return new JDBCStatisticaInfoServiceSearch(this.unlimitedJdbcServiceManager);
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
		return new JDBCStatisticaInfoService(this.unlimitedJdbcServiceManager);
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
		return new JDBCStatisticaOrariaServiceSearch(this.unlimitedJdbcServiceManager);
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
		return new JDBCStatisticaOrariaService(this.unlimitedJdbcServiceManager);
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
		return new JDBCStatisticaGiornalieraServiceSearch(this.unlimitedJdbcServiceManager);
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
		return new JDBCStatisticaGiornalieraService(this.unlimitedJdbcServiceManager);
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
		return new JDBCStatisticaSettimanaleServiceSearch(this.unlimitedJdbcServiceManager);
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
		return new JDBCStatisticaSettimanaleService(this.unlimitedJdbcServiceManager);
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
		return new JDBCStatisticaMensileServiceSearch(this.unlimitedJdbcServiceManager);
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
		return new JDBCStatisticaMensileService(this.unlimitedJdbcServiceManager);
	}
	
	
	
}