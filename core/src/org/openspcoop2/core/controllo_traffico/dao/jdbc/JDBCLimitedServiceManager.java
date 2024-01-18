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
package org.openspcoop2.core.controllo_traffico.dao.jdbc;

import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.Connection;

import javax.sql.DataSource;

import org.openspcoop2.core.controllo_traffico.dao.IConfigurazioneGeneraleServiceSearch;
import org.openspcoop2.core.controllo_traffico.dao.IConfigurazioneGeneraleService;
import org.openspcoop2.core.controllo_traffico.dao.IConfigurazioneRateLimitingProprietaServiceSearch;
import org.openspcoop2.core.controllo_traffico.dao.IConfigurazioneRateLimitingProprietaService;
import org.openspcoop2.core.controllo_traffico.dao.IConfigurazionePolicyServiceSearch;
import org.openspcoop2.core.controllo_traffico.dao.IConfigurazionePolicyService;
import org.openspcoop2.core.controllo_traffico.dao.IAttivazionePolicyServiceSearch;
import org.openspcoop2.core.controllo_traffico.dao.IAttivazionePolicyService;

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
	 Services relating to the object with name:configurazione-generale type:configurazione-generale
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IConfigurazioneGeneraleServiceSearch getConfigurazioneGeneraleServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCConfigurazioneGeneraleServiceSearch(this.unlimitedJdbcServiceManager);
	}
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IConfigurazioneGeneraleService getConfigurazioneGeneraleService() throws ServiceException,NotImplementedException{
		return new JDBCConfigurazioneGeneraleService(this.unlimitedJdbcServiceManager);
	}
	
	
	
	/*
	 =====================================================================================================================
	 Services relating to the object with name:configurazione-rate-limiting-proprieta type:configurazione-rate-limiting-proprieta
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimitingProprieta}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimitingProprieta}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IConfigurazioneRateLimitingProprietaServiceSearch getConfigurazioneRateLimitingProprietaServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCConfigurazioneRateLimitingProprietaServiceSearch(this.unlimitedJdbcServiceManager);
	}
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimitingProprieta}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimitingProprieta}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IConfigurazioneRateLimitingProprietaService getConfigurazioneRateLimitingProprietaService() throws ServiceException,NotImplementedException{
		return new JDBCConfigurazioneRateLimitingProprietaService(this.unlimitedJdbcServiceManager);
	}
	
	
	
	/*
	 =====================================================================================================================
	 Services relating to the object with name:configurazione-policy type:configurazione-policy
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IConfigurazionePolicyServiceSearch getConfigurazionePolicyServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCConfigurazionePolicyServiceSearch(this.unlimitedJdbcServiceManager);
	}
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IConfigurazionePolicyService getConfigurazionePolicyService() throws ServiceException,NotImplementedException{
		return new JDBCConfigurazionePolicyService(this.unlimitedJdbcServiceManager);
	}
	
	
	
	/*
	 =====================================================================================================================
	 Services relating to the object with name:attivazione-policy type:attivazione-policy
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IAttivazionePolicyServiceSearch getAttivazionePolicyServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCAttivazionePolicyServiceSearch(this.unlimitedJdbcServiceManager);
	}
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IAttivazionePolicyService getAttivazionePolicyService() throws ServiceException,NotImplementedException{
		return new JDBCAttivazionePolicyService(this.unlimitedJdbcServiceManager);
	}
	
	
	
}