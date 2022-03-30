/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

package org.openspcoop2.pdd.timers;

import java.sql.Connection;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.pdd.config.DBTransazioniManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.transazioni.GestoreTransazioniStateful;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.threads.BaseThread;
import org.slf4j.Logger;

/**     
 * TimerRepositoryStatefulThread
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimerRepositoryStatefulThread extends BaseThread{

	public static TimerState STATE = TimerState.OFF; // abilitato in OpenSPCoop2Startup al momento dell'avvio
	
	public static final String ID_MODULO = "TimerRepositoryStateful";
	
	
	/** Logger utilizzato per debug. */
	private Logger log = null;
	private Logger logSql = null;

	/** Indicazione se deve essere effettuato il log delle query */
	private boolean debug = false;	
		
	/** Database */
	private String tipoDatabaseRuntime = null; //tipoDatabase
	
	private DAOFactory daoFactory = null;
    private ServiceManagerProperties daoFactoryServiceManagerPropertiesTransazioni = null;
    private Logger daoFactoryLoggerTransazioni = null;
    
	/**
	 * OpenSPCoop2Properties resources
	 */
	private OpenSPCoop2Properties openspcoopProperties = null;
	
	/** Gestore */
	private GestoreTransazioniStateful gestore = null;
	
	
	
	/** Costruttore */
	public TimerRepositoryStatefulThread() throws Exception{
	
		this.openspcoopProperties = OpenSPCoop2Properties.getInstance();
		
		this.setTimeout(this.openspcoopProperties.getTransazioniStatefulTimerIntervalSeconds());
		
		this.debug = this.openspcoopProperties.isTransazioniStatefulDebug();

		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioniStateful(this.debug);
		this.logSql = OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioniStatefulSql(this.debug);
		
		// DB
		try{
			this.tipoDatabaseRuntime = this.openspcoopProperties.getDatabaseType();
			if(this.tipoDatabaseRuntime==null){
				throw new Exception("Tipo Database non definito");
			}

			DAOFactoryProperties daoFactoryProperties = null;
			this.daoFactoryLoggerTransazioni = this.logSql;
			this.daoFactory = DAOFactory.getInstance(this.daoFactoryLoggerTransazioni);
			daoFactoryProperties = DAOFactoryProperties.getInstance(this.daoFactoryLoggerTransazioni);
			this.daoFactoryServiceManagerPropertiesTransazioni = daoFactoryProperties.getServiceManagerProperties(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
			this.daoFactoryServiceManagerPropertiesTransazioni.setShowSql(this.debug);	
			this.daoFactoryServiceManagerPropertiesTransazioni.setDatabaseType(DBTransazioniManager.getInstance().getTipoDatabase());

		}catch(Exception e){
			throw new HandlerException("Errore durante l'inizializzazione delle risorse per l'accesso al database: "+e.getMessage(),e);
		}
				
		try{
			this.gestore = new GestoreTransazioniStateful(this.log, this.logSql,
					this.tipoDatabaseRuntime, this.debug);
		}catch(Exception e){
			throw new Exception("Errore durante l'inizializzazione del gestore: "+e.getMessage(),e);
		}
		
		
	}
	
	@Override
	public boolean initialize(){
		if(this.gestore==null){
			this.log.error("Gestore non correttamente inizializzato");
			return false;
		}
		return true;
	}
	@Override
	public void process(){
		if(TimerState.ENABLED.equals(STATE)) {
			
			DBTransazioniManager dbManager = null;
	    	Resource r = null;
			try{
				dbManager = DBTransazioniManager.getInstance();
				r = dbManager.getResource(this.openspcoopProperties.getIdentitaPortaDefault(null), ID_MODULO, null);
				if(r==null){
					throw new Exception("Risorsa al database non disponibile");
				}
				Connection con = (Connection) r.getResource();
				if(con == null)
					throw new Exception("Connessione non disponibile");	
				
				if(this.debug){
					this.log.debug("Esecuzione thread per gestione delle transazioni stateful....");
				}
				
				this.gestore.verificaOggettiPresentiRepository(this.daoFactory,this.daoFactoryServiceManagerPropertiesTransazioni, this.daoFactoryLoggerTransazioni, con);
				if(this.debug){
					this.log.debug("Esecuzione thread per gestione delle transazioni stateful terminata");
				}
				
			}catch(Exception e){
				this.log.error("Errore durante la gestione delle transazioni stateful: "+e.getMessage(),e);
			}finally{
				try{
					if(r!=null)
						dbManager.releaseResource(this.openspcoopProperties.getIdentitaPortaDefault(null), ID_MODULO, r);
				}catch(Exception eClose){}
			}
			
		}
		else {
			this.log.info("Timer "+ID_MODULO+" disabilitato");
		}
	}
}
