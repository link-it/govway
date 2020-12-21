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

import java.sql.Connection;

import javax.sql.DataSource;

import org.openspcoop2.monitor.engine.config.base.dao.IPluginServiceSearch;
import org.openspcoop2.monitor.engine.config.base.dao.IPluginService;
import org.openspcoop2.monitor.engine.config.base.dao.IConfigurazioneServizioServiceSearch;
import org.openspcoop2.monitor.engine.config.base.dao.IConfigurazioneServizioService;
import org.openspcoop2.monitor.engine.config.base.dao.IConfigurazioneServizioAzioneServiceSearch;
import org.openspcoop2.monitor.engine.config.base.dao.IConfigurazioneServizioAzioneService;
import org.openspcoop2.monitor.engine.config.base.dao.IConfigurazioneFiltroServiceSearch;
import org.openspcoop2.monitor.engine.config.base.dao.IConfigurazioneFiltroService;

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
		return new JDBCPluginServiceSearch(this.unlimitedJdbcServiceManager);
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
		return new JDBCPluginService(this.unlimitedJdbcServiceManager);
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
		return new JDBCConfigurazioneServizioServiceSearch(this.unlimitedJdbcServiceManager);
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
		return new JDBCConfigurazioneServizioService(this.unlimitedJdbcServiceManager);
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
		return new JDBCConfigurazioneServizioAzioneServiceSearch(this.unlimitedJdbcServiceManager);
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
		return new JDBCConfigurazioneServizioAzioneService(this.unlimitedJdbcServiceManager);
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
		return new JDBCConfigurazioneFiltroServiceSearch(this.unlimitedJdbcServiceManager);
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
		return new JDBCConfigurazioneFiltroService(this.unlimitedJdbcServiceManager);
	}
	
	
	
}