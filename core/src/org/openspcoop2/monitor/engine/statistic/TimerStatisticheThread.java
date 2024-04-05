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
package org.openspcoop2.monitor.engine.statistic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import javax.sql.DataSource;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.monitor.engine.config.MonitorProperties;
import org.openspcoop2.monitor.engine.constants.CostantiConfigurazione;
import org.openspcoop2.monitor.engine.exceptions.EngineException;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.GestoreJNDI;

/**
 * TimerStatisticheThread
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimerStatisticheThread extends Thread{

	/**
	 * Timeout che definisce la cadenza di avvio di questo timer. 
	 */
	private long timeout = 10; // ogni 10 secondi avvio il Thread

	/** Configurazione */
	private StatisticsConfig statisticsConfig;
	
	/** DataSource */
	private DataSource ds;
	
	/** Connection */
	private Connection connection;
	
	/** DAOFactory */
	private DAOFactory daoFactory;
	private org.openspcoop2.core.statistiche.dao.IServiceManager statisticheSM = null;
	private org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM = null;
	private org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager pluginsStatisticheSM = null;
	private org.openspcoop2.core.plugins.dao.IServiceManager pluginsBaseSM;
	private org.openspcoop2.core.commons.search.dao.IServiceManager utilsSM;
	private org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager pluginsTransazioniSM;
	
    // VARIABILE PER STOP
	private boolean stop = false;
	
	public boolean isStop() {
		return this.stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}
	
	
	/** Costruttore Datasource */
	public TimerStatisticheThread(DataSource ds, StatisticsConfig statisticsConfig) throws EngineException{
		this(statisticsConfig);
		this.ds = ds;
	}
	public TimerStatisticheThread(String ds,Properties dsContext, StatisticsConfig statisticsConfig) throws EngineException{
		this(statisticsConfig);
		GestoreJNDI jndi = new GestoreJNDI(dsContext);
		try{
			this.ds = (DataSource) jndi.lookup(ds);
		}catch(Exception e){
			throw new EngineException(e.getMessage(),e);
		}
	}
	
	/** Costruttore Connection */
	public TimerStatisticheThread(Connection connection, StatisticsConfig statisticsConfig) throws EngineException{
		this(statisticsConfig);
		this.connection = connection;
	}
	public TimerStatisticheThread(String connectionUrl,String driverJDBC,String username, String password, StatisticsConfig statisticsConfig) throws EngineException{
		this(statisticsConfig);
		try{
			Class.forName(driverJDBC);
			this.connection = DriverManager.getConnection(connectionUrl,username,password);
		}catch(Exception e){
			throw new EngineException(e.getMessage(),e);
		}
	}
	
	/** Costruttore */
	public TimerStatisticheThread(StatisticsConfig statisticsConfig) throws EngineException{
	
		try{
		
			MonitorProperties props = MonitorProperties.getInstance(statisticsConfig.getLogCore());
	//		
	//		this.tipoDatabase = props.getProperty(CostantiConfigurazione.PROP_DBTYPE);
	//		if (!"postgresql".equals(this.tipoDatabase) && !"mysql".equals(this.tipoDatabase) && !"oracle".equals(this.tipoDatabase))
	//			throw new FrameworkCoreException("Tipo di database non valido indicato in configurazione");
			
			try {
				this.timeout = Integer.parseInt(props.getProperty(CostantiConfigurazione.STAT_TIMEOUT, "10", true));
			} catch (NumberFormatException e) {
				this.timeout=10;
			}
			
			this.statisticsConfig = statisticsConfig;
			
			this.daoFactory = DAOFactory.getInstance(this.statisticsConfig.getLogSql());
			
		}catch(Exception e){
			throw new EngineException(e.getMessage(),e);
		}
	}
	
	/**
	 * Metodo che fa partire il Thread. 
	 *
	 */
	@Override
	public void run(){
		
		this.initResources();
		
		while(this.stop == false){
					
			StatisticsLibrary sLibrary = new StatisticsLibrary(this.statisticsConfig, this.statisticheSM, this.transazioniSM, 
					this.pluginsStatisticheSM, this.pluginsBaseSM, this.utilsSM, this.pluginsTransazioniSM);
			
			sLibrary.generateStatisticaOraria();
			
			sLibrary.generateStatisticaGiornaliera();
			
			sLibrary.generateStatisticaSettimanale();
			
			sLibrary.generateStatisticaMensile();
					
			// CheckInterval
			if(this.stop==false){
				int i=0;
				while(i<this.timeout){
					Utilities.sleep(1000);		
					if(this.stop){
						break; // thread terminato, non lo devo far piu' dormire
					}
					i++;
				}
			}
		} 
		
		this.statisticsConfig.getLogCore().info("Thread per la generazione delle statistiche terminato");

	}
	
	private void initResources(){
		
		try{
		 	
			if(this.ds!=null){
				this.statisticheSM = (org.openspcoop2.core.statistiche.dao.IServiceManager) this.daoFactory.getServiceManager(
						org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance(),
						this.ds);
			}
			else if(this.connection!=null){
				this.statisticheSM = (org.openspcoop2.core.statistiche.dao.IServiceManager) this.daoFactory.getServiceManager(
						org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance(),
						this.connection);
			}
			else{
				this.statisticheSM = (org.openspcoop2.core.statistiche.dao.IServiceManager) this.daoFactory.getServiceManager(
						org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance());
			}
			
		}catch(Exception e){
			this.statisticsConfig.getLogCore().error("Errore durante l'inizializzazione del Service Manager per le statistiche: "+e.getMessage(),e);
		} 
		
		
		try{
		 	
			if(this.ds!=null){
				this.transazioniSM = (org.openspcoop2.core.transazioni.dao.IServiceManager) this.daoFactory.getServiceManager(
						org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(),
						this.ds);
			}
			else if(this.connection!=null){
				this.transazioniSM = (org.openspcoop2.core.transazioni.dao.IServiceManager) this.daoFactory.getServiceManager(
						org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(),
						this.connection);
			}
			else{
				this.transazioniSM = (org.openspcoop2.core.transazioni.dao.IServiceManager) this.daoFactory.getServiceManager(
						org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
			}
			
		}catch(Exception e){
			this.statisticsConfig.getLogCore().error("Errore durante l'inizializzazione del Service Manager per le transazioni: "+e.getMessage(),e);
		} 
		
		
		if(this.statisticsConfig.isGenerazioneStatisticheCustom()){
			try{
			 	
				if(this.ds!=null){
					this.pluginsStatisticheSM = (org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager) this.daoFactory.getServiceManager(
							org.openspcoop2.monitor.engine.config.statistiche.utils.ProjectInfo.getInstance(),
							this.ds);
				}
				else if(this.connection!=null){
					this.pluginsStatisticheSM = (org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager) this.daoFactory.getServiceManager(
							org.openspcoop2.monitor.engine.config.statistiche.utils.ProjectInfo.getInstance(),
							this.connection);
				}
				else{
					this.pluginsStatisticheSM = (org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager) this.daoFactory.getServiceManager(
							org.openspcoop2.monitor.engine.config.statistiche.utils.ProjectInfo.getInstance());
				}
				
				if(this.ds!=null){
					this.pluginsBaseSM = (org.openspcoop2.core.plugins.dao.IServiceManager) this.daoFactory.getServiceManager(
							org.openspcoop2.core.plugins.utils.ProjectInfo.getInstance(),
							this.ds);
				}
				else if(this.connection!=null){
					this.pluginsBaseSM = (org.openspcoop2.core.plugins.dao.IServiceManager) this.daoFactory.getServiceManager(
							org.openspcoop2.core.plugins.utils.ProjectInfo.getInstance(),
							this.connection);
				}
				else{
					this.pluginsBaseSM = (org.openspcoop2.core.plugins.dao.IServiceManager) this.daoFactory.getServiceManager(
							org.openspcoop2.core.plugins.utils.ProjectInfo.getInstance());
				}
				
				if(this.ds!=null){
					this.utilsSM = (org.openspcoop2.core.commons.search.dao.IServiceManager) this.daoFactory.getServiceManager(
							org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance(),
							this.ds);
				}
				else if(this.connection!=null){
					this.utilsSM = (org.openspcoop2.core.commons.search.dao.IServiceManager) this.daoFactory.getServiceManager(
							org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance(),
							this.connection);
				}
				else{
					this.utilsSM = (org.openspcoop2.core.commons.search.dao.IServiceManager) this.daoFactory.getServiceManager(
							org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
				}
				
				if(this.statisticsConfig.isAnalisiTransazioniCustom()){
					if(this.ds!=null){
						this.pluginsTransazioniSM = (org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager) this.daoFactory.getServiceManager(
								org.openspcoop2.monitor.engine.config.transazioni.utils.ProjectInfo.getInstance(),
								this.ds);
					}
					else if(this.connection!=null){
						this.pluginsTransazioniSM = (org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager) this.daoFactory.getServiceManager(
								org.openspcoop2.monitor.engine.config.transazioni.utils.ProjectInfo.getInstance(),
								this.connection);
					}
					else{
						this.pluginsTransazioniSM = (org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager) this.daoFactory.getServiceManager(
								org.openspcoop2.monitor.engine.config.transazioni.utils.ProjectInfo.getInstance());
					}
				}
				
			}catch(Exception e){
				this.statisticsConfig.getLogCore().error("Errore durante l'inizializzazione del Service Manager per i plugins: "+e.getMessage(),e);
			} 
		}
	}
}
