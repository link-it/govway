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
import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerProperties;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;

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

import org.openspcoop2.core.commons.search.dao.IServiceManager;

import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;

/**     
 * Manager that allows you to obtain the services of research and management of objects
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class JDBCServiceManager extends org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManager implements IServiceManager {

	protected Connection get_Connection() throws ServiceException {
		return this.connection;
	}
	protected DataSource get_Datasource() throws ServiceException {
		return this.datasource;
	}
	protected JDBCServiceManagerProperties get_JdbcProperties(){
		return this.jdbcProperties;
	}
	protected Logger get_Logger(){
		return this.log;
	}
	@Override
	protected Connection getConnection() throws ServiceException {
		return super.getConnection();
	}
	@Override
	protected void closeConnection(Connection connection) throws ServiceException {
		super.closeConnection(connection);
	}

	protected JDBCServiceManager(){}

	public JDBCServiceManager(String jndiName, Properties contextJNDI,
			ServiceManagerProperties smProperties) throws ServiceException {
		super(jndiName, contextJNDI, smProperties);
	}
	public JDBCServiceManager(String jndiName, Properties contextJNDI,
			JDBCServiceManagerProperties jdbcProperties) throws ServiceException {
		super(jndiName, contextJNDI, jdbcProperties);
	}
	public JDBCServiceManager(String jndiName, Properties contextJNDI,
			ServiceManagerProperties smProperties, Logger alog) throws ServiceException {
		super(jndiName, contextJNDI, smProperties, alog);
	}
	public JDBCServiceManager(String jndiName, Properties contextJNDI,
			JDBCServiceManagerProperties jdbcProperties, Logger alog) throws ServiceException {
		super(jndiName, contextJNDI, jdbcProperties, alog);
	}
	
	
	public JDBCServiceManager(DataSource ds, ServiceManagerProperties smProperties)
			throws ServiceException {
		super(ds, smProperties);
	}
	public JDBCServiceManager(DataSource ds, JDBCServiceManagerProperties jdbcProperties)
			throws ServiceException {
		super(ds, jdbcProperties);
	}
	public JDBCServiceManager(DataSource ds, ServiceManagerProperties smProperties, Logger alog)
			throws ServiceException {
		super(ds, smProperties, alog);
	}
	public JDBCServiceManager(DataSource ds, JDBCServiceManagerProperties jdbcProperties, Logger alog)
			throws ServiceException {
		super(ds, jdbcProperties, alog);
	}
	
	
	public JDBCServiceManager(String connectionUrl, String driverJDBC,
			String username, String password, ServiceManagerProperties smProperties)
			throws ServiceException {
		super(connectionUrl, driverJDBC, username, password, smProperties);
	}
	public JDBCServiceManager(String connectionUrl, String driverJDBC,
			String username, String password, JDBCServiceManagerProperties jdbcProperties)
			throws ServiceException {
		super(connectionUrl, driverJDBC, username, password, jdbcProperties);
	}
	public JDBCServiceManager(String connectionUrl, String driverJDBC,
			String username, String password, ServiceManagerProperties smProperties, Logger alog)
			throws ServiceException {
		super(connectionUrl, driverJDBC, username, password, smProperties, alog);
	}
	public JDBCServiceManager(String connectionUrl, String driverJDBC,
			String username, String password, JDBCServiceManagerProperties jdbcProperties, Logger alog)
			throws ServiceException {
		super(connectionUrl, driverJDBC, username, password, jdbcProperties, alog);
	}
	
	
	public JDBCServiceManager(Connection connection, ServiceManagerProperties smProperties)
			throws ServiceException {
		super(connection, smProperties);
	}
	public JDBCServiceManager(Connection connection, JDBCServiceManagerProperties jdbcProperties)
			throws ServiceException {
		super(connection, jdbcProperties);
	}
	public JDBCServiceManager(Connection connection, ServiceManagerProperties smProperties, Logger alog) throws ServiceException {
		super(connection, smProperties, alog);
	}
	public JDBCServiceManager(Connection connection, JDBCServiceManagerProperties jdbcProperties, Logger alog) throws ServiceException {
		super(connection, jdbcProperties, alog);
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
		return new JDBCSoggettoServiceSearch(this);
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
		return new JDBCPortaDominioServiceSearch(this);
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
		return new JDBCGruppoServiceSearch(this);
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
		return new JDBCAccordoServizioParteComuneServiceSearch(this);
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
		return new JDBCAccordoServizioParteComuneGruppoServiceSearch(this);
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
		return new JDBCAccordoServizioParteComuneAzioneServiceSearch(this);
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
		return new JDBCPortTypeServiceSearch(this);
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
		return new JDBCOperationServiceSearch(this);
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
		return new JDBCResourceServiceSearch(this);
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
		return new JDBCAccordoServizioParteSpecificaServiceSearch(this);
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
		return new JDBCFruitoreServiceSearch(this);
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
		return new JDBCServizioApplicativoServiceSearch(this);
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
		return new JDBCPortaDelegataServiceSearch(this);
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
		return new JDBCPortaApplicativaServiceSearch(this);
	}
	
	
	
	
	

}
