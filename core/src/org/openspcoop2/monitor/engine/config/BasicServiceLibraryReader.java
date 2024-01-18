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
package org.openspcoop2.monitor.engine.config;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryException;
import org.openspcoop2.core.plugins.ConfigurazioneServizio;
import org.openspcoop2.core.plugins.ConfigurazioneServizioAzione;
import org.openspcoop2.core.plugins.IdConfigurazioneServizio;
import org.openspcoop2.core.plugins.IdConfigurazioneServizioAzione;
import org.openspcoop2.core.plugins.dao.IConfigurazioneServizioAzioneServiceSearch;
import org.openspcoop2.core.plugins.dao.IConfigurazioneServizioServiceSearch;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica;
import org.openspcoop2.core.commons.search.IdSoggetto;
import org.openspcoop2.core.commons.search.dao.IAccordoServizioParteSpecificaServiceSearch;

import java.sql.Connection;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;

/**
 * BasicServiceLibraryReader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BasicServiceLibraryReader {

	private Connection connection;
	private DAOFactory daoFactory = null;
	private ServiceManagerProperties daoFactoryServiceManagerPropertiesPlugins = null;
	private ServiceManagerProperties daoFactoryServiceManagerPropertiesUtils = null;
	private Logger daoFactoryLogger = null;
	
	private org.openspcoop2.core.plugins.dao.IServiceManager serviceManagerPluginsBase;
	private org.openspcoop2.core.commons.search.dao.IServiceManager serviceManagerUtils;
	
	private boolean debug;
	
	public BasicServiceLibraryReader(Connection connection,
			DAOFactory daoFactory, 
			ServiceManagerProperties daoFactoryServiceManagerPropertiesPlugins,
			ServiceManagerProperties daoFactoryServiceManagerPropertiesUtils,
			Logger daoFactoryLogger, boolean debug){
		this.connection = connection;
		this.daoFactory = daoFactory;
		this.daoFactoryServiceManagerPropertiesPlugins = daoFactoryServiceManagerPropertiesPlugins;
		this.daoFactoryServiceManagerPropertiesUtils = daoFactoryServiceManagerPropertiesUtils;
		this.daoFactoryLogger = daoFactoryLogger;
		this.debug = debug;
	}
	public BasicServiceLibraryReader(org.openspcoop2.core.plugins.dao.IServiceManager jdbcServiceManagerPluginsBase, 
			org.openspcoop2.core.commons.search.dao.IServiceManager jdbcServiceManagerUtils,
			boolean debug){
		this.serviceManagerPluginsBase = jdbcServiceManagerPluginsBase;
		this.serviceManagerUtils = jdbcServiceManagerUtils;
		this.debug = debug;
	}
	
	public BasicServiceLibrary read(IDServizio idServizio,Logger log) throws Exception{
		
		String azione = idServizio.getAzione();
		
		// Ottengo dati Servizio SPCoop
		
		org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica s = null;
		try{
			s = this.getAccordoServizioParteSpecifica(idServizio);
			if(s==null){
				throw new NotFoundException("Null instance return");
			}
		}catch(NotFoundException notFound){
			// non e' stata configurata una parte specifica
			if(this.debug){
				log.debug("Non è stata trovata una parte specifica con id ["+idServizio.toString()+"]: "+notFound.getMessage(),notFound);
			}
			return null;
		}
		
	
		
		String portType = null;
		if(s.getPortType()!=null && !"".equals(s.getPortType())){
			portType = s.getPortType();
		}
		else{
			portType = idServizio.getNome();
		}
		
		if(s.getIdAccordoServizioParteComune().getIdSoggetto()==null){
			log.warn("Il servizio indicato ["+idServizio+"] implementa un accordo che non contiene un soggetto referente. Opzione non supportata");
			return null;
		}
		
		IDSoggetto soggettoReferente = new IDSoggetto(s.getIdAccordoServizioParteComune().getIdSoggetto().getTipo(), s.getIdAccordoServizioParteComune().getIdSoggetto().getNome());
		IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromValues(s.getIdAccordoServizioParteComune().getNome(), 
				soggettoReferente, 
				s.getIdAccordoServizioParteComune().getVersione());
		
		BasicServiceLibrary basicServiceLibrary = this.read(idAccordo, portType, azione, log);
		if(basicServiceLibrary!=null){
			basicServiceLibrary.setIdServizio(idServizio);
			basicServiceLibrary.setAccordoServizioParteSpecifica(s);
		}
		
		return basicServiceLibrary;
	}

	public boolean existsAlmostOneBasicServiceLibrary(Logger log) throws Exception{
		return this._existsAlmostOneBasicServiceLibrary();
	}
	
	public BasicServiceLibrary read(IDAccordo idAccordo,String portType,String azione,Logger log) throws Exception{
		
		BasicServiceLibrary basicServiceLibrary = new BasicServiceLibrary();
		basicServiceLibrary.setIdAccordoServizioParteComune(idAccordo);
		basicServiceLibrary.setPortType(portType);
		basicServiceLibrary.setAzione(azione);
		
		// Ottengo dati Accordo di Servizio
		
		// Leggo libreria servizio
		ConfigurazioneServizio serviceLibrary = null;
		try{
			serviceLibrary = this.getServiceLibrary(idAccordo, portType);
			if(serviceLibrary==null){
				throw new NotFoundException("Null instance return");
			}
		}catch(NotFoundException notFound){
			// non e' stata configurata una libreria di servizio
			if(this.debug){
				log.debug("NON e' stata configurata una libreria di servizio ["+portType+"] dell'accordo ["+idAccordo.toString()+"]");
			}
			return null;
		}
		basicServiceLibrary.setServiceLibrary(serviceLibrary);

			
		// ** Verifico se esiste una azione conforme a quella arrivata.**
		ConfigurazioneServizioAzione serviceActionLibrary = null;
		if(azione!=null){
			try{
				serviceActionLibrary = getServiceActionLibrary(serviceLibrary, azione);
				if(serviceActionLibrary==null){
					throw new NotFoundException("Null instance return");
				}
			}catch(NotFoundException notFound){
				// non e' stata configurata una libreria di servizio per l'azione specifica
				if(this.debug){
					log.debug("Non risulta configurata configurata l'azione specifica ["+azione+"] per la libreria di servizio ["+portType+"] dell'accordo ["+idAccordo.toString()+"]",notFound);
				}
			}
		}
		basicServiceLibrary.setServiceActionLibrary(serviceActionLibrary);
		
		// ** Recupero informazioni per azione '*'
		ConfigurazioneServizioAzione serviceActionAllLibrary = null;
		try{
			serviceActionAllLibrary = getServiceActionLibrary(serviceLibrary, "*");
			if(serviceActionAllLibrary==null){
				throw new NotFoundException("Null instance return");
			}
		}catch(NotFoundException notFound){
			// non e' stata configurata una libreria di servizio per l'azione * ????
			String errorMsg = "Non risulta configurata configurata l'azione specifica ["+"*"+"] per la libreria di servizio ["+portType+"] dell'accordo ["+idAccordo.toString()+"]";
			log.error(errorMsg,notFound);
			throw new NotFoundException(errorMsg,notFound);
		}
		basicServiceLibrary.setServiceActionAllLibrary(serviceActionAllLibrary);
		
		return basicServiceLibrary;
	}
	
	private org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idServizio) throws DAOFactoryException, ServiceException, NotImplementedException, NotFoundException, MultipleResultException {
		
		boolean autoCommit = true;
		
		org.openspcoop2.core.commons.search.dao.IServiceManager serviceManager = this.serviceManagerUtils;
		if(serviceManager==null){
			serviceManager = 
					(org.openspcoop2.core.commons.search.dao.IServiceManager) this.daoFactory.getServiceManager(
							org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance(), 
							this.connection, autoCommit,
							this.daoFactoryServiceManagerPropertiesUtils, this.daoFactoryLogger);
			if(serviceManager instanceof org.openspcoop2.core.commons.search.dao.jdbc.JDBCServiceManager)
				((org.openspcoop2.core.commons.search.dao.jdbc.JDBCServiceManager)serviceManager).getJdbcProperties().setShowSql(this.debug);
		}
		
		IAccordoServizioParteSpecificaServiceSearch search = serviceManager.getAccordoServizioParteSpecificaServiceSearch();
		
		IdAccordoServizioParteSpecifica idAS = new IdAccordoServizioParteSpecifica();
		IdSoggetto idErogatore = new IdSoggetto();
		idErogatore.setTipo(idServizio.getSoggettoErogatore().getTipo());
		idErogatore.setNome(idServizio.getSoggettoErogatore().getNome());
		idAS.setIdErogatore(idErogatore);
		idAS.setTipo(idServizio.getTipo());
		idAS.setNome(idServizio.getNome());
		idAS.setVersione(idServizio.getVersione());
		
		return search.get(idAS);
		
	}
	
	private boolean _existsAlmostOneBasicServiceLibrary() throws DAOFactoryException, ServiceException, NotImplementedException {
		
		boolean autoCommit = true;
		
		org.openspcoop2.core.plugins.dao.IServiceManager serviceManager = this.serviceManagerPluginsBase;
		if(serviceManager==null){
			serviceManager = (org.openspcoop2.core.plugins.dao.IServiceManager) this.daoFactory.getServiceManager(
					org.openspcoop2.core.plugins.utils.ProjectInfo.getInstance(),
					this.connection, autoCommit,
						this.daoFactoryServiceManagerPropertiesPlugins, this.daoFactoryLogger);
			if(serviceManager instanceof org.openspcoop2.core.plugins.dao.jdbc.JDBCServiceManager)
				((org.openspcoop2.core.plugins.dao.jdbc.JDBCServiceManager)serviceManager).getJdbcProperties().setShowSql(this.debug);
		}
		
		IConfigurazioneServizioServiceSearch search = serviceManager.getConfigurazioneServizioServiceSearch();
		NonNegativeNumber nn = search.count(search.newExpression());
		return nn!=null && nn.longValue()>0;

	}
	
	private ConfigurazioneServizio getServiceLibrary(IDAccordo idAccordo,String portType) throws DAOFactoryException, ServiceException, NotImplementedException, NotFoundException, MultipleResultException {
		
		boolean autoCommit = true;
		
		org.openspcoop2.core.plugins.dao.IServiceManager serviceManager = this.serviceManagerPluginsBase;
		if(serviceManager==null){
			serviceManager = (org.openspcoop2.core.plugins.dao.IServiceManager) this.daoFactory.getServiceManager(
					org.openspcoop2.core.plugins.utils.ProjectInfo.getInstance(),
					this.connection, autoCommit,
						this.daoFactoryServiceManagerPropertiesPlugins, this.daoFactoryLogger);
			if(serviceManager instanceof org.openspcoop2.core.plugins.dao.jdbc.JDBCServiceManager)
				((org.openspcoop2.core.plugins.dao.jdbc.JDBCServiceManager)serviceManager).getJdbcProperties().setShowSql(this.debug);
		}
		
		IConfigurazioneServizioServiceSearch search = serviceManager.getConfigurazioneServizioServiceSearch();
		
		IdConfigurazioneServizio idConfigurazioneServizio = new IdConfigurazioneServizio();
		idConfigurazioneServizio.setAccordo(idAccordo.getNome());
		if(idAccordo.getSoggettoReferente()!=null){
			idConfigurazioneServizio.setTipoSoggettoReferente(idAccordo.getSoggettoReferente().getTipo());
			idConfigurazioneServizio.setNomeSoggettoReferente(idAccordo.getSoggettoReferente().getNome());
		}
		idConfigurazioneServizio.setServizio(portType);
		if(idAccordo.getVersione()!=null){
			idConfigurazioneServizio.setVersione(idAccordo.getVersione());
		}
		
		return search.get(idConfigurazioneServizio);
		
	}
	
	private ConfigurazioneServizioAzione getServiceActionLibrary(ConfigurazioneServizio serviceLibrary,String azione) throws DAOFactoryException, ServiceException, NotImplementedException, NotFoundException, MultipleResultException {
		
		boolean autoCommit = true;
		
		org.openspcoop2.core.plugins.dao.IServiceManager serviceManager = this.serviceManagerPluginsBase;
		if(serviceManager==null){
			serviceManager = (org.openspcoop2.core.plugins.dao.IServiceManager) this.daoFactory.getServiceManager(
					org.openspcoop2.core.plugins.utils.ProjectInfo.getInstance(),
					this.connection, autoCommit,
					this.daoFactoryServiceManagerPropertiesPlugins, this.daoFactoryLogger);
			if(serviceManager instanceof org.openspcoop2.core.plugins.dao.jdbc.JDBCServiceManager)
				((org.openspcoop2.core.plugins.dao.jdbc.JDBCServiceManager)serviceManager).getJdbcProperties().setShowSql(this.debug);
		}
		
		IConfigurazioneServizioServiceSearch serviceSearch = serviceManager.getConfigurazioneServizioServiceSearch();
		IdConfigurazioneServizio idConfigurazioneServizio = serviceSearch.convertToId(serviceLibrary);
		
		IConfigurazioneServizioAzioneServiceSearch search = serviceManager.getConfigurazioneServizioAzioneServiceSearch();
		
		IdConfigurazioneServizioAzione idConfigurazioneServizioAzione = new IdConfigurazioneServizioAzione();
		idConfigurazioneServizioAzione.setIdConfigurazioneServizio(idConfigurazioneServizio);
		idConfigurazioneServizioAzione.setAzione(azione);
		
		return search.get(idConfigurazioneServizioAzione);
		
	}
}
