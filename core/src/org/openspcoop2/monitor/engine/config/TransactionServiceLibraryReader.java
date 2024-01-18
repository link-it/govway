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
import org.openspcoop2.core.plugins.ConfigurazioneServizioAzione;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione;
import org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione;
import org.openspcoop2.monitor.engine.config.transazioni.dao.IConfigurazioneTransazioneServiceSearch;

import java.sql.Connection;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;

/**
 * TransactionServiceLibraryReader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransactionServiceLibraryReader {

	private Connection connection;
	private DAOFactory daoFactory = null;
	private ServiceManagerProperties daoFactoryServiceManagerPropertiesPluginsTransazioni = null;
	private Logger daoFactoryLogger = null;
		
	private org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager serviceManagerPluginsTransazioni;
		
	private boolean debug;
	
	public TransactionServiceLibraryReader(Connection connection,
			DAOFactory daoFactory, ServiceManagerProperties daoFactoryServiceManagerPropertiesPluginsTransazioni,
			Logger daoFactoryLogger, boolean debug){
		this.connection = connection;
		this.daoFactory = daoFactory;
		this.daoFactoryServiceManagerPropertiesPluginsTransazioni = daoFactoryServiceManagerPropertiesPluginsTransazioni;
		this.daoFactoryLogger = daoFactoryLogger;
		this.debug = debug;
	}
	public TransactionServiceLibraryReader(org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager serviceManagerPluginsTransazioni, boolean debug){
		this.serviceManagerPluginsTransazioni = serviceManagerPluginsTransazioni;
		this.debug = debug;
	}
	
	public TransactionServiceLibrary readConfigurazioneTransazione(BasicServiceLibrary basicServiceLibrary, Logger log) throws Exception{
		
		TransactionServiceLibrary transactionServiceLibrary = new TransactionServiceLibrary();
		transactionServiceLibrary.setBasicServiceLibrary(basicServiceLibrary);
			
		String azione = basicServiceLibrary.getAzione();
		String portType = basicServiceLibrary.getPortType();
		IDAccordo idAccordo = basicServiceLibrary.getIdAccordoServizioParteComune();
		
		// ** Verifico se esiste una azione conforme a quella arrivata.**
		ConfigurazioneTransazione transactionActionLibrary = null;
		if(basicServiceLibrary.getServiceActionLibrary()!=null){
			// Accedo alla sezione di transazione per l'azione
			try{
				transactionActionLibrary = this.getServiceActionTransactionLibrary(basicServiceLibrary.getServiceActionLibrary());
			}catch(NotFoundException notFound){
				// non e' stata configurata una libreria di servizio per l'azione specifica
				if(this.debug){
					log.debug("Non risulta configurata una transazione personalizzata per l'azione specifica ["+azione+"] per la libreria di servizio ["+portType+"] dell'accordo ["+idAccordo.toString()+"]",notFound);
				}
			}
		}
		if(transactionActionLibrary!=null){
			if(transactionActionLibrary.isEnabled()){
				transactionServiceLibrary.setTransactionActionLibrary(transactionActionLibrary);
			}
			else{
				if(this.debug){
					log.debug("Risulta configurata, ma non abilitata, una transazione personalizzata per l'azione specifica ["+azione+"] per la libreria di servizio ["+portType+"] dell'accordo ["+idAccordo.toString()+"]");
				}
			}
		}
		
		// ** Recupero informazioni per azione '*'
		ConfigurazioneTransazione transactionActionAllLibrary = null;
		try{
			transactionActionAllLibrary = this.getServiceActionTransactionLibrary(basicServiceLibrary.getServiceActionAllLibrary());
		}catch(NotFoundException notFound){
			// non e' stata configurata una libreria di servizio per l'azione specifica
			if(this.debug){
				log.debug("Non risulta configurata una transazione personalizzata per l'azione specifica ["+"*"+"] per la libreria di servizio ["+portType+"] dell'accordo ["+idAccordo.toString()+"]",notFound);
			}
		}
		if(transactionActionAllLibrary!=null){
			if(transactionActionAllLibrary.isEnabled()){
				transactionServiceLibrary.setTransactionActionAllLibrary(transactionActionAllLibrary);
			}
			else{
				if(this.debug){
					log.debug("Risulta configurata, ma non abilitata, una transazione personalizzata per l'azione specifica ["+"*"+"] per la libreria di servizio ["+portType+"] dell'accordo ["+idAccordo.toString()+"]");
				}
			}
		}
		
		return transactionServiceLibrary;
	}
	
	private ConfigurazioneTransazione getServiceActionTransactionLibrary(ConfigurazioneServizioAzione serviceActionLibrary) throws DAOFactoryException, ServiceException, NotImplementedException, NotFoundException, MultipleResultException {
		
		boolean autoCommit = true;
		
		org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager serviceManager = this.serviceManagerPluginsTransazioni;
		if(serviceManager==null){
			serviceManager = (org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager) this.daoFactory.getServiceManager(
					org.openspcoop2.monitor.engine.config.transazioni.utils.ProjectInfo.getInstance(), 
					this.connection, autoCommit,
						this.daoFactoryServiceManagerPropertiesPluginsTransazioni, this.daoFactoryLogger);
			if(serviceManager instanceof org.openspcoop2.monitor.engine.config.transazioni.dao.jdbc.JDBCServiceManager)
				((org.openspcoop2.monitor.engine.config.transazioni.dao.jdbc.JDBCServiceManager)serviceManager).getJdbcProperties().setShowSql(this.debug);
		}
		
		IConfigurazioneTransazioneServiceSearch serviceSearch = serviceManager.getConfigurazioneTransazioneServiceSearch();
		
		IdConfigurazioneTransazione idConfigurazioneTransazione = new IdConfigurazioneTransazione();
		org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione idConfigurazioneServizioAzione = new org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione();
		org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio idConfigurazioneServizio = new org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio();
		idConfigurazioneServizio.setAccordo(serviceActionLibrary.getIdConfigurazioneServizio().getAccordo());
		idConfigurazioneServizio.setTipoSoggettoReferente(serviceActionLibrary.getIdConfigurazioneServizio().getTipoSoggettoReferente());
		idConfigurazioneServizio.setNomeSoggettoReferente(serviceActionLibrary.getIdConfigurazioneServizio().getNomeSoggettoReferente());
		idConfigurazioneServizio.setVersione(serviceActionLibrary.getIdConfigurazioneServizio().getVersione());
		idConfigurazioneServizio.setServizio(serviceActionLibrary.getIdConfigurazioneServizio().getServizio());
		idConfigurazioneServizioAzione.setIdConfigurazioneServizio(idConfigurazioneServizio);
		idConfigurazioneServizioAzione.setAzione(serviceActionLibrary.getAzione());
		idConfigurazioneTransazione.setIdConfigurazioneServizioAzione(idConfigurazioneServizioAzione);
		
		return serviceSearch.get(idConfigurazioneTransazione);
		
	}
	
}
