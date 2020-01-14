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
package org.openspcoop2.monitor.engine.config;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryException;
import org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizioAzione;
import org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca;
import org.openspcoop2.monitor.engine.config.ricerche.dao.IConfigurazioneRicercaServiceSearch;

import java.sql.Connection;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;

/**
 * SearchServiceLibraryReader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SearchServiceLibraryReader {

	private Connection connection;
	private DAOFactory daoFactory = null;
	private ServiceManagerProperties daoFactoryServiceManagerPropertiesPluginsRicerche = null;
	private Logger daoFactoryLogger = null;
		
	private org.openspcoop2.monitor.engine.config.ricerche.dao.IServiceManager serviceManagerPluginsRicerche;
		
	private boolean debug;
	
	public SearchServiceLibraryReader(Connection connection,
			DAOFactory daoFactory, ServiceManagerProperties daoFactoryServiceManagerPropertiesPluginsRicerche,
			Logger daoFactoryLogger, boolean debug){
		this.connection = connection;
		this.daoFactory = daoFactory;
		this.daoFactoryServiceManagerPropertiesPluginsRicerche = daoFactoryServiceManagerPropertiesPluginsRicerche;
		this.daoFactoryLogger = daoFactoryLogger;
		this.debug = debug;
	}
	public SearchServiceLibraryReader(org.openspcoop2.monitor.engine.config.ricerche.dao.IServiceManager serviceManagerPluginsRicerche, boolean debug){
		this.serviceManagerPluginsRicerche = serviceManagerPluginsRicerche;
		this.debug = debug;
	}
	
	public SearchServiceLibrary readConfigurazioneRicerche(BasicServiceLibrary basicServiceLibrary, Logger log) throws Exception{
		
		SearchServiceLibrary searchServiceLibrary = new SearchServiceLibrary();
		searchServiceLibrary.setBasicServiceLibrary(basicServiceLibrary);
			
		String azione = basicServiceLibrary.getAzione();
		String portType = basicServiceLibrary.getPortType();
		IDAccordo idAccordo = basicServiceLibrary.getIdAccordoServizioParteComune();
		
		// ** Verifico se esiste una azione conforme a quella arrivata.**
		List<ConfigurazioneRicerca> searchActionLibrary = null;
		if(basicServiceLibrary.getServiceActionLibrary()!=null){
			// Accedo alla sezione di ricerca per l'azione
			try{
				searchActionLibrary = this.getServiceActionSearchLibrary(basicServiceLibrary.getServiceActionLibrary());
			}catch(NotFoundException notFound){
				// non e' stata configurata una libreria di servizio per l'azione specifica
				if(this.debug){
					log.debug("Non risulta configurata una ricerca personalizzata per l'azione specifica ["+azione+"] per la libreria di servizio ["+portType+"] dell'accordo ["+idAccordo.toString()+"]",notFound);
				}
			}
		}
		searchServiceLibrary.setSearchActionLibrary(searchActionLibrary);
		
		// ** Recupero informazioni per azione '*'
		List<ConfigurazioneRicerca> searchActionAllLibrary = null;
		try{
			searchActionAllLibrary = this.getServiceActionSearchLibrary(basicServiceLibrary.getServiceActionAllLibrary());
		}catch(NotFoundException notFound){
			// non e' stata configurata una libreria di servizio per l'azione specifica
			if(this.debug){
				log.debug("Non risulta configurata una ricerca personalizzata per l'azione specifica ["+"*"+"] per la libreria di servizio ["+portType+"] dell'accordo ["+idAccordo.toString()+"]",notFound);
			}
		}
		searchServiceLibrary.setSearchActionAllLibrary(searchActionAllLibrary);
		
		return searchServiceLibrary;
	}
	
	private List<ConfigurazioneRicerca> getServiceActionSearchLibrary(ConfigurazioneServizioAzione serviceActionLibrary) throws DAOFactoryException, ServiceException, NotImplementedException, NotFoundException, MultipleResultException, ExpressionNotImplementedException, ExpressionException {
		
		boolean autoCommit = true;
		
		org.openspcoop2.monitor.engine.config.ricerche.dao.IServiceManager serviceManager = this.serviceManagerPluginsRicerche;
		if(serviceManager==null){
			serviceManager = (org.openspcoop2.monitor.engine.config.ricerche.dao.IServiceManager) this.daoFactory.getServiceManager(
					org.openspcoop2.monitor.engine.config.ricerche.utils.ProjectInfo.getInstance(), 
					this.connection, autoCommit,
						this.daoFactoryServiceManagerPropertiesPluginsRicerche, this.daoFactoryLogger);
			if(serviceManager instanceof org.openspcoop2.monitor.engine.config.ricerche.dao.jdbc.JDBCServiceManager)
				((org.openspcoop2.monitor.engine.config.ricerche.dao.jdbc.JDBCServiceManager)serviceManager).getJdbcProperties().setShowSql(this.debug);
		}
		
		IConfigurazioneRicercaServiceSearch serviceSearch = serviceManager.getConfigurazioneRicercaServiceSearch();
		
		IPaginatedExpression pagExpr = serviceSearch.newPaginatedExpression();
		pagExpr.and();
		pagExpr.
			equals(ConfigurazioneRicerca.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.ACCORDO,
					serviceActionLibrary.getIdConfigurazioneServizio().getAccordo());
		pagExpr.
			equals(ConfigurazioneRicerca.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.TIPO_SOGGETTO_REFERENTE,
					serviceActionLibrary.getIdConfigurazioneServizio().getTipoSoggettoReferente());
		pagExpr.
			equals(ConfigurazioneRicerca.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.NOME_SOGGETTO_REFERENTE,
					serviceActionLibrary.getIdConfigurazioneServizio().getNomeSoggettoReferente());
		pagExpr.
			equals(ConfigurazioneRicerca.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.VERSIONE,
					serviceActionLibrary.getIdConfigurazioneServizio().getVersione());
		pagExpr.
			equals(ConfigurazioneRicerca.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.SERVIZIO,
					serviceActionLibrary.getIdConfigurazioneServizio().getServizio());
		pagExpr.
			equals(ConfigurazioneRicerca.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.AZIONE,
					serviceActionLibrary.getAzione());

		return serviceSearch.findAll(pagExpr);
		
	}
	
}
