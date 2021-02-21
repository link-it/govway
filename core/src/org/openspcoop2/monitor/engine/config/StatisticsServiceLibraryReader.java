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
package org.openspcoop2.monitor.engine.config;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryException;
import org.openspcoop2.core.plugins.ConfigurazioneServizioAzione;
import org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica;
import org.openspcoop2.monitor.engine.config.statistiche.dao.IConfigurazioneStatisticaServiceSearch;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato;

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
 * StatisticsServiceLibraryReader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticsServiceLibraryReader {

	private Connection connection;
	private DAOFactory daoFactory = null;
	private ServiceManagerProperties daoFactoryServiceManagerPropertiesPluginsStatistiche = null;
	private Logger daoFactoryLogger = null;
		
	private org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager serviceManagerPluginsStatistiche;
		
	private boolean debug;
	
	public StatisticsServiceLibraryReader(Connection connection,
			DAOFactory daoFactory, ServiceManagerProperties daoFactoryServiceManagerPropertiesPluginsStatistiche,
			Logger daoFactoryLogger, boolean debug){
		this.connection = connection;
		this.daoFactory = daoFactory;
		this.daoFactoryServiceManagerPropertiesPluginsStatistiche = daoFactoryServiceManagerPropertiesPluginsStatistiche;
		this.daoFactoryLogger = daoFactoryLogger;
		this.debug = debug;
	}
	public StatisticsServiceLibraryReader(org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager serviceManagerPluginsStatistiche, boolean debug){
		this.serviceManagerPluginsStatistiche = serviceManagerPluginsStatistiche;
		this.debug = debug;
	}
	
	public StatisticsServiceLibrary readConfigurazioneStatistiche(BasicServiceLibrary basicServiceLibrary,  TransactionServiceLibrary transactionServiceLibrary, Logger log) throws Exception{
		
		StatisticsServiceLibrary searchServiceLibrary = new StatisticsServiceLibrary();
		searchServiceLibrary.setBasicServiceLibrary(basicServiceLibrary);
			
		String azione = basicServiceLibrary.getAzione();
		String portType = basicServiceLibrary.getPortType();
		IDAccordo idAccordo = basicServiceLibrary.getIdAccordoServizioParteComune();
		
		// ** Verifico se sono stati definiti degli stati (in tal caso indico di registrare il plugins)
		searchServiceLibrary.setPluginStatiTransazioniEnabled(false);
		if(transactionServiceLibrary!=null){
			List<ConfigurazioneTransazioneStato> stati = transactionServiceLibrary.mergeServiceActionTransactionLibrary_states();
			if(stati!=null && stati.size()>0){
				searchServiceLibrary.setPluginStatiTransazioniEnabled(true);
			}
		}
		
		// ** Verifico se sono stati definiti delle risorse (in tal caso indico di registrare il plugins)
		if(transactionServiceLibrary!=null){
			List<ConfigurazioneTransazioneRisorsaContenuto> risorse = transactionServiceLibrary.mergeServiceActionTransactionLibrary_resources();
			for (ConfigurazioneTransazioneRisorsaContenuto configurazioneTransazioneRisorsaContenuto : risorse) {
				if(configurazioneTransazioneRisorsaContenuto.isStatEnabled()){
					searchServiceLibrary.getPluginRisorseTransazioni().add(configurazioneTransazioneRisorsaContenuto.getNome());
				}
			}
		}
		
		
		// ** Verifico se esiste una azione conforme a quella arrivata.**
		List<ConfigurazioneStatistica> searchActionLibrary = null;
		if(basicServiceLibrary.getServiceActionLibrary()!=null){
			// Accedo alla sezione di statistica per l'azione
			try{
				searchActionLibrary = this.getServiceActionSearchLibrary(basicServiceLibrary.getServiceActionLibrary());
			}catch(NotFoundException notFound){
				// non e' stata configurata una libreria di servizio per l'azione specifica
				if(this.debug){
					log.debug("Non risulta configurata una statistica personalizzata per l'azione specifica ["+azione+"] per la libreria di servizio ["+portType+"] dell'accordo ["+idAccordo.toString()+"]",notFound);
				}
			}
		}
		searchServiceLibrary.setStatisticsActionLibrary(searchActionLibrary);
		
		// ** Recupero informazioni per azione '*'
		List<ConfigurazioneStatistica> searchActionAllLibrary = null;
		try{
			searchActionAllLibrary = this.getServiceActionSearchLibrary(basicServiceLibrary.getServiceActionAllLibrary());
		}catch(NotFoundException notFound){
			// non e' stata configurata una libreria di servizio per l'azione specifica
			if(this.debug){
				log.debug("Non risulta configurata una statistica personalizzata per l'azione specifica ["+"*"+"] per la libreria di servizio ["+portType+"] dell'accordo ["+idAccordo.toString()+"]",notFound);
			}
		}
		searchServiceLibrary.setStatisticsActionAllLibrary(searchActionAllLibrary);
		
		return searchServiceLibrary;
	}
	
	private List<ConfigurazioneStatistica> getServiceActionSearchLibrary(ConfigurazioneServizioAzione serviceActionLibrary) throws DAOFactoryException, ServiceException, NotImplementedException, NotFoundException, MultipleResultException, ExpressionNotImplementedException, ExpressionException {
		
		boolean autoCommit = true;
		
		org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager serviceManager = this.serviceManagerPluginsStatistiche;
		if(serviceManager==null){
			serviceManager = (org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager) this.daoFactory.getServiceManager(
					org.openspcoop2.monitor.engine.config.statistiche.utils.ProjectInfo.getInstance(), 
					this.connection, autoCommit,
						this.daoFactoryServiceManagerPropertiesPluginsStatistiche, this.daoFactoryLogger);
			if(serviceManager instanceof org.openspcoop2.monitor.engine.config.statistiche.dao.jdbc.JDBCServiceManager)
				((org.openspcoop2.monitor.engine.config.statistiche.dao.jdbc.JDBCServiceManager)serviceManager).getJdbcProperties().setShowSql(this.debug);
		}
		
		IConfigurazioneStatisticaServiceSearch serviceSearch = serviceManager.getConfigurazioneStatisticaServiceSearch();
		
		IPaginatedExpression pagExpr = serviceSearch.newPaginatedExpression();
		pagExpr.and();
		pagExpr.
			equals(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.ACCORDO,
					serviceActionLibrary.getIdConfigurazioneServizio().getAccordo());
		pagExpr.
			equals(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.TIPO_SOGGETTO_REFERENTE,
					serviceActionLibrary.getIdConfigurazioneServizio().getTipoSoggettoReferente());
		pagExpr.
			equals(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.NOME_SOGGETTO_REFERENTE,
					serviceActionLibrary.getIdConfigurazioneServizio().getNomeSoggettoReferente());
		pagExpr.
			equals(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.VERSIONE,
					serviceActionLibrary.getIdConfigurazioneServizio().getVersione());
		pagExpr.
			equals(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.SERVIZIO,
					serviceActionLibrary.getIdConfigurazioneServizio().getServizio());
		pagExpr.
			equals(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.AZIONE,
					serviceActionLibrary.getAzione());

		return serviceSearch.findAll(pagExpr);
		
	}
	
}
