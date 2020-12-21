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
package org.openspcoop2.monitor.engine.config.base.dao;

import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.exception.NotImplementedException;


/**	
 * Manager with which 'can get the service for the management of the objects defined in the package org.openspcoop2.monitor.engine.config.base 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IServiceManager {

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
	public IPluginServiceSearch getPluginServiceSearch() throws ServiceException,NotImplementedException;
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.monitor.engine.config.base.Plugin}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.monitor.engine.config.base.Plugin}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public IPluginService getPluginService() throws ServiceException,NotImplementedException;
	
	
	
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
	public IConfigurazioneServizioServiceSearch getConfigurazioneServizioServiceSearch() throws ServiceException,NotImplementedException;
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizio}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizio}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public IConfigurazioneServizioService getConfigurazioneServizioService() throws ServiceException,NotImplementedException;
	
	
	
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
	public IConfigurazioneServizioAzioneServiceSearch getConfigurazioneServizioAzioneServiceSearch() throws ServiceException,NotImplementedException;
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizioAzione}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizioAzione}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public IConfigurazioneServizioAzioneService getConfigurazioneServizioAzioneService() throws ServiceException,NotImplementedException;
	
	
	
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
	public IConfigurazioneFiltroServiceSearch getConfigurazioneFiltroServiceSearch() throws ServiceException,NotImplementedException;
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneFiltro}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.monitor.engine.config.base.ConfigurazioneFiltro}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public IConfigurazioneFiltroService getConfigurazioneFiltroService() throws ServiceException,NotImplementedException;
	
	
	
	
}
