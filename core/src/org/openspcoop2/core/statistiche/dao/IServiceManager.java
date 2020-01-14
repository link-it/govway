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
package org.openspcoop2.core.statistiche.dao;

import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.exception.NotImplementedException;


/**	
 * Manager with which 'can get the service for the management of the objects defined in the package org.openspcoop2.core.statistiche 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IServiceManager {

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
	public IStatisticaInfoServiceSearch getStatisticaInfoServiceSearch() throws ServiceException,NotImplementedException;
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaInfo}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaInfo}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public IStatisticaInfoService getStatisticaInfoService() throws ServiceException,NotImplementedException;
	
	
	
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
	public IStatisticaOrariaServiceSearch getStatisticaOrariaServiceSearch() throws ServiceException,NotImplementedException;
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaOraria}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaOraria}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public IStatisticaOrariaService getStatisticaOrariaService() throws ServiceException,NotImplementedException;
	
	
	
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
	public IStatisticaGiornalieraServiceSearch getStatisticaGiornalieraServiceSearch() throws ServiceException,NotImplementedException;
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaGiornaliera}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public IStatisticaGiornalieraService getStatisticaGiornalieraService() throws ServiceException,NotImplementedException;
	
	
	
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
	public IStatisticaSettimanaleServiceSearch getStatisticaSettimanaleServiceSearch() throws ServiceException,NotImplementedException;
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaSettimanale}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public IStatisticaSettimanaleService getStatisticaSettimanaleService() throws ServiceException,NotImplementedException;
	
	
	
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
	public IStatisticaMensileServiceSearch getStatisticaMensileServiceSearch() throws ServiceException,NotImplementedException;
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaMensile}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.statistiche.StatisticaMensile}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public IStatisticaMensileService getStatisticaMensileService() throws ServiceException,NotImplementedException;
	
	
	
	
}
