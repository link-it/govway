/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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
package org.openspcoop2.monitor.engine.config.ricerche.dao;

import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.exception.NotImplementedException;


/**	
 * Manager with which 'can get the service for the management of the objects defined in the package org.openspcoop2.monitor.engine.config.ricerche 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IServiceManager {

	/*
	 =====================================================================================================================
	 Services relating to the object with name:configurazione-ricerca type:configurazione-ricerca
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public IConfigurazioneRicercaServiceSearch getConfigurazioneRicercaServiceSearch() throws ServiceException,NotImplementedException;
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public IConfigurazioneRicercaService getConfigurazioneRicercaService() throws ServiceException,NotImplementedException;
	
	
	
	
}
