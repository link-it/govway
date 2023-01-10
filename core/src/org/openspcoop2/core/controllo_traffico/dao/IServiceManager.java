/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.core.controllo_traffico.dao;

import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.exception.NotImplementedException;


/**	
 * Manager with which 'can get the service for the management of the objects defined in the package org.openspcoop2.core.controllo_traffico 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IServiceManager {

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
	public IConfigurazioneGeneraleServiceSearch getConfigurazioneGeneraleServiceSearch() throws ServiceException,NotImplementedException;
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public IConfigurazioneGeneraleService getConfigurazioneGeneraleService() throws ServiceException,NotImplementedException;
	
	
	
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
	public IConfigurazioneRateLimitingProprietaServiceSearch getConfigurazioneRateLimitingProprietaServiceSearch() throws ServiceException,NotImplementedException;
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimitingProprieta}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimitingProprieta}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public IConfigurazioneRateLimitingProprietaService getConfigurazioneRateLimitingProprietaService() throws ServiceException,NotImplementedException;
	
	
	
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
	public IConfigurazionePolicyServiceSearch getConfigurazionePolicyServiceSearch() throws ServiceException,NotImplementedException;
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public IConfigurazionePolicyService getConfigurazionePolicyService() throws ServiceException,NotImplementedException;
	
	
	
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
	public IAttivazionePolicyServiceSearch getAttivazionePolicyServiceSearch() throws ServiceException,NotImplementedException;
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.controllo_traffico.AttivazionePolicy}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public IAttivazionePolicyService getAttivazionePolicyService() throws ServiceException,NotImplementedException;
	
	
	
	
}
