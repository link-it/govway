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
package org.openspcoop2.core.commons.search.dao.jdbc;

import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.Connection;

import javax.sql.DataSource;

import org.openspcoop2.core.commons.search.dao.ISoggettoServiceSearch;
import org.openspcoop2.core.commons.search.dao.IPortaDominioServiceSearch;
import org.openspcoop2.core.commons.search.dao.IGruppoServiceSearch;
import org.openspcoop2.core.commons.search.dao.IAccordoServizioParteComuneServiceSearch;
import org.openspcoop2.core.commons.search.dao.IAccordoServizioParteComuneGruppoServiceSearch;
import org.openspcoop2.core.commons.search.dao.IAccordoServizioParteComuneAzioneServiceSearch;
import org.openspcoop2.core.commons.search.dao.IPortTypeServiceSearch;
import org.openspcoop2.core.commons.search.dao.IOperationServiceSearch;
import org.openspcoop2.core.commons.search.dao.IResourceServiceSearch;
import org.openspcoop2.core.commons.search.dao.IAccordoServizioParteSpecificaServiceSearch;
import org.openspcoop2.core.commons.search.dao.IFruitoreServiceSearch;
import org.openspcoop2.core.commons.search.dao.IServizioApplicativoServiceSearch;
import org.openspcoop2.core.commons.search.dao.IPortaDelegataServiceSearch;
import org.openspcoop2.core.commons.search.dao.IPortaApplicativaServiceSearch;

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
	@Override
	public ISoggettoServiceSearch getSoggettoServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCSoggettoServiceSearch(this.unlimitedJdbcServiceManager);
	}
	
	
	
	
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
	@Override
	public IPortaDominioServiceSearch getPortaDominioServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCPortaDominioServiceSearch(this.unlimitedJdbcServiceManager);
	}
	
	
	
	
	/*
	 =====================================================================================================================
	 Services relating to the object with name:gruppo type:gruppo
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.commons.search.Gruppo}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.commons.search.Gruppo}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IGruppoServiceSearch getGruppoServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCGruppoServiceSearch(this.unlimitedJdbcServiceManager);
	}
	
	
	
	
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
	@Override
	public IAccordoServizioParteComuneServiceSearch getAccordoServizioParteComuneServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCAccordoServizioParteComuneServiceSearch(this.unlimitedJdbcServiceManager);
	}
	
	
	
	
	/*
	 =====================================================================================================================
	 Services relating to the object with name:accordo-servizio-parte-comune-gruppo type:accordo-servizio-parte-comune-gruppo
	 =====================================================================================================================
	*/
	
	/**
	 * Return a service used to research on the backend on objects of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo}
	 *
	 * @return Service used to research on the backend on objects of type {@link org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo}	
	 * @throws ServiceException Exception thrown when an error occurs during processing of the request
	 * @throws NotImplementedException Exception thrown when the method is not implemented
	 */
	@Override
	public IAccordoServizioParteComuneGruppoServiceSearch getAccordoServizioParteComuneGruppoServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCAccordoServizioParteComuneGruppoServiceSearch(this.unlimitedJdbcServiceManager);
	}
	
	
	
	
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
	@Override
	public IAccordoServizioParteComuneAzioneServiceSearch getAccordoServizioParteComuneAzioneServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCAccordoServizioParteComuneAzioneServiceSearch(this.unlimitedJdbcServiceManager);
	}
	
	
	
	
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
	@Override
	public IPortTypeServiceSearch getPortTypeServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCPortTypeServiceSearch(this.unlimitedJdbcServiceManager);
	}
	
	
	
	
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
	@Override
	public IOperationServiceSearch getOperationServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCOperationServiceSearch(this.unlimitedJdbcServiceManager);
	}
	
	
	
	
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
	@Override
	public IResourceServiceSearch getResourceServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCResourceServiceSearch(this.unlimitedJdbcServiceManager);
	}
	
	
	
	
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
	@Override
	public IAccordoServizioParteSpecificaServiceSearch getAccordoServizioParteSpecificaServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCAccordoServizioParteSpecificaServiceSearch(this.unlimitedJdbcServiceManager);
	}
	
	
	
	
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
	@Override
	public IFruitoreServiceSearch getFruitoreServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCFruitoreServiceSearch(this.unlimitedJdbcServiceManager);
	}
	
	
	
	
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
	@Override
	public IServizioApplicativoServiceSearch getServizioApplicativoServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCServizioApplicativoServiceSearch(this.unlimitedJdbcServiceManager);
	}
	
	
	
	
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
	@Override
	public IPortaDelegataServiceSearch getPortaDelegataServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCPortaDelegataServiceSearch(this.unlimitedJdbcServiceManager);
	}
	
	
	
	
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
	@Override
	public IPortaApplicativaServiceSearch getPortaApplicativaServiceSearch() throws ServiceException,NotImplementedException{
		return new JDBCPortaApplicativaServiceSearch(this.unlimitedJdbcServiceManager);
	}
	
	
	
	
}