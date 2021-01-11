/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.core.transazioni.dao;

import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.exception.NotImplementedException;


/**	
 * Manager with which 'can get the service for the management of the objects defined in the package org.openspcoop2.core.transazioni 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IServiceManager {

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
	public ICredenzialeMittenteServiceSearch getCredenzialeMittenteServiceSearch() throws ServiceException,NotImplementedException;
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.transazioni.CredenzialeMittente}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.transazioni.CredenzialeMittente}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public ICredenzialeMittenteService getCredenzialeMittenteService() throws ServiceException,NotImplementedException;
	
	
	
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
	public ITransazioneServiceSearch getTransazioneServiceSearch() throws ServiceException,NotImplementedException;
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.transazioni.Transazione}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.transazioni.Transazione}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public ITransazioneService getTransazioneService() throws ServiceException,NotImplementedException;
	
	
	
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
	public ITransazioneApplicativoServerServiceSearch getTransazioneApplicativoServerServiceSearch() throws ServiceException,NotImplementedException;
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.transazioni.TransazioneApplicativoServer}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.transazioni.TransazioneApplicativoServer}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public ITransazioneApplicativoServerService getTransazioneApplicativoServerService() throws ServiceException,NotImplementedException;
	
	
	
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
	public ITransazioneInfoServiceSearch getTransazioneInfoServiceSearch() throws ServiceException,NotImplementedException;
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.transazioni.TransazioneInfo}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.transazioni.TransazioneInfo}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public ITransazioneInfoService getTransazioneInfoService() throws ServiceException,NotImplementedException;
	
	
	
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
	public ITransazioneExportServiceSearch getTransazioneExportServiceSearch() throws ServiceException,NotImplementedException;
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.transazioni.TransazioneExport}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.transazioni.TransazioneExport}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public ITransazioneExportService getTransazioneExportService() throws ServiceException,NotImplementedException;
	
	
	
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
	public IDumpMessaggioServiceSearch getDumpMessaggioServiceSearch() throws ServiceException,NotImplementedException;
	
	/**
	 * Return a service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.transazioni.DumpMessaggio}
	 *
	 * @return Service used to research and manage on the backend on objects of type {@link org.openspcoop2.core.transazioni.DumpMessaggio}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	public IDumpMessaggioService getDumpMessaggioService() throws ServiceException,NotImplementedException;
	
	
	
	
}
