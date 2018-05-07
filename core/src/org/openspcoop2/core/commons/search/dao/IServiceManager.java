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
package org.openspcoop2.core.commons.search.dao;

import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.exception.NotImplementedException;


/**	
 * Manager with which 'can get the service for the management of the objects defined in the package org.openspcoop2.core.commons.search 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IServiceManager {

	/*
	 =====================================================================================================================
	 Services relating to the object with name:soggetto type:soggetto
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.commons.search.Soggetto}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.commons.search.Soggetto}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public ISoggettoServiceSearch getSoggettoServiceSearch() throws ServiceException,NotImplementedException;
	
	
	
	
	/*
	 =====================================================================================================================
	 Services relating to the object with name:porta-dominio type:porta-dominio
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.commons.search.PortaDominio}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.commons.search.PortaDominio}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public IPortaDominioServiceSearch getPortaDominioServiceSearch() throws ServiceException,NotImplementedException;
	
	
	
	
	/*
	 =====================================================================================================================
	 Services relating to the object with name:accordo-servizio-parte-comune type:accordo-servizio-parte-comune
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComune}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComune}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public IAccordoServizioParteComuneServiceSearch getAccordoServizioParteComuneServiceSearch() throws ServiceException,NotImplementedException;
	
	
	
	
	/*
	 =====================================================================================================================
	 Services relating to the object with name:accordo-servizio-parte-comune-azione type:accordo-servizio-parte-comune-azione
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public IAccordoServizioParteComuneAzioneServiceSearch getAccordoServizioParteComuneAzioneServiceSearch() throws ServiceException,NotImplementedException;
	
	
	
	
	/*
	 =====================================================================================================================
	 Services relating to the object with name:port-type type:port-type
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.commons.search.PortType}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.commons.search.PortType}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public IPortTypeServiceSearch getPortTypeServiceSearch() throws ServiceException,NotImplementedException;
	
	
	
	
	/*
	 =====================================================================================================================
	 Services relating to the object with name:operation type:operation
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.commons.search.Operation}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.commons.search.Operation}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public IOperationServiceSearch getOperationServiceSearch() throws ServiceException,NotImplementedException;
	
	
	
	
	/*
	 =====================================================================================================================
	 Services relating to the object with name:resource type:resource
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.commons.search.Resource}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.commons.search.Resource}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public IResourceServiceSearch getResourceServiceSearch() throws ServiceException,NotImplementedException;
	
	
	
	
	/*
	 =====================================================================================================================
	 Services relating to the object with name:accordo-servizio-parte-specifica type:accordo-servizio-parte-specifica
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public IAccordoServizioParteSpecificaServiceSearch getAccordoServizioParteSpecificaServiceSearch() throws ServiceException,NotImplementedException;
	
	
	
	
	/*
	 =====================================================================================================================
	 Services relating to the object with name:fruitore type:fruitore
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.commons.search.Fruitore}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.commons.search.Fruitore}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public IFruitoreServiceSearch getFruitoreServiceSearch() throws ServiceException,NotImplementedException;
	
	
	
	
	/*
	 =====================================================================================================================
	 Services relating to the object with name:servizio-applicativo type:servizio-applicativo
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.commons.search.ServizioApplicativo}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.commons.search.ServizioApplicativo}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public IServizioApplicativoServiceSearch getServizioApplicativoServiceSearch() throws ServiceException,NotImplementedException;
	
	
	
	
	/*
	 =====================================================================================================================
	 Services relating to the object with name:porta-delegata type:porta-delegata
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.commons.search.PortaDelegata}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.commons.search.PortaDelegata}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public IPortaDelegataServiceSearch getPortaDelegataServiceSearch() throws ServiceException,NotImplementedException;
	
	
	
	
	/*
	 =====================================================================================================================
	 Services relating to the object with name:porta-applicativa type:porta-applicativa
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.commons.search.PortaApplicativa}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.commons.search.PortaApplicativa}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public IPortaApplicativaServiceSearch getPortaApplicativaServiceSearch() throws ServiceException,NotImplementedException;
	
	
	
	
	
}
