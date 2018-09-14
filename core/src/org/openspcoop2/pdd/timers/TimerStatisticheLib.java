/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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

package org.openspcoop2.pdd.timers;


import java.sql.Connection;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.core.statistiche.constants.TipoIntervalloStatistico;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.monitor.engine.statistic.StatisticsConfig;
import org.openspcoop2.monitor.engine.statistic.StatisticsLibrary;
import org.openspcoop2.pdd.config.DBTransazioniManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.id.serial.InfoStatistics;
import org.openspcoop2.utils.semaphore.Semaphore;
import org.openspcoop2.utils.semaphore.SemaphoreConfiguration;
import org.openspcoop2.utils.semaphore.SemaphoreMapping;
import org.slf4j.Logger;


/**     
 * TimerStatisticheLib
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimerStatisticheLib {

	
	/** Logger utilizzato per debug. */
	private Logger logCore = null;
	private Logger logSql = null;
	private Logger logTimer = null;
	private MsgDiagnostico msgDiag = null;

	/** OpenSPCoop2Properties */
	private OpenSPCoop2Properties op2Properties = null;
	
	/** Indicazione se devono essere generate le statistiche custom */
	private boolean generazioneStatisticheCustom = false;
	private boolean analisiTransazioniCustom = false;
	
	/** Indicazione se deve essere effettuato il log delle query */
	private boolean debug = false;	
	
	/** Tipologie di statistiche */
	private boolean statisticheOrarie = false;
	private boolean statisticheGiornaliere = false;
	private boolean statisticheSettimanali = false;
	private boolean statisticheMensili = false;
	
	/** Tipologie di statistiche: gestione ultimo intervallo */
	private boolean statisticheOrarie_gestioneUltimoIntervallo = false;
	private boolean statisticheGiornaliere_gestioneUltimoIntervallo = false;
	private boolean statisticheSettimanali_gestioneUltimoIntervallo = false;
	private boolean statisticheMensili_gestioneUltimoIntervallo = false;
	
	/** Database */
//	private DataSource ds = null;
//	private String datasource = null;
	private String tipoDatabase = null; //tipoDatabase
	private DAOFactory daoFactory = null;
    private Logger daoFactoryLogger = null;
	private ServiceManagerProperties daoFactoryServiceManagerPropertiesTransazioni = null;
	private ServiceManagerProperties daoFactoryServiceManagerPropertiesStatistiche = null;
	private ServiceManagerProperties daoFactoryServiceManagerPropertiesPluginsStatistiche = null;
	private ServiceManagerProperties daoFactoryServiceManagerPropertiesPluginsBase = null;
	private ServiceManagerProperties daoFactoryServiceManagerPropertiesUtils = null;
	private ServiceManagerProperties daoFactoryServiceManagerPropertiesPluginsTransazioni = null;

	/** StatisticsConfig */
	private StatisticsConfig statisticsConfig;
	
	/** Timer */
	private TimerLock timerLock = null;

	/** Semaforo */
	private Semaphore semaphore = null;
	private InfoStatistics semaphore_statistics;

	
	
	/** Costruttore */
	public TimerStatisticheLib(MsgDiagnostico msgDiag,Logger logTimer,OpenSPCoop2Properties p) throws Exception{
	
		this.msgDiag = msgDiag;
		this.op2Properties = p;
		
		this.debug = this.op2Properties.isStatisticheGenerazioneDebug();
		
		this.logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopStatistiche(this.debug);
		this.logSql = OpenSPCoop2Logger.getLoggerOpenSPCoopStatisticheSql(this.debug);
		this.logTimer = logTimer;
		
		this.generazioneStatisticheCustom = this.op2Properties.isStatisticheGenerazioneCustomEnabled();
		this.analisiTransazioniCustom = this.op2Properties.isStatisticheGenerazioneCustomSdkEnabled();
		
		this.statisticheOrarie = this.op2Properties.isStatisticheGenerazioneBaseOrariaEnabled();
		this.statisticheGiornaliere = this.op2Properties.isStatisticheGenerazioneBaseGiornalieraEnabled();
		this.statisticheSettimanali = this.op2Properties.isStatisticheGenerazioneBaseSettimanaleEnabled();
		this.statisticheMensili = this.op2Properties.isStatisticheGenerazioneBaseMensileEnabled();
		
		this.statisticheOrarie_gestioneUltimoIntervallo = this.op2Properties.isStatisticheGenerazioneBaseOrariaEnabledUltimaOra();
		this.statisticheGiornaliere_gestioneUltimoIntervallo = this.op2Properties.isStatisticheGenerazioneBaseGiornalieraEnabledUltimoGiorno();
		this.statisticheSettimanali_gestioneUltimoIntervallo = this.op2Properties.isStatisticheGenerazioneBaseSettimanaleEnabledUltimaSettimana();
		this.statisticheMensili_gestioneUltimoIntervallo = this.op2Properties.isStatisticheGenerazioneBaseMensileEnabledUltimoMese();
				
		try{
			
			// se avviato il thread le statistiche devono essere nella base dati delle transazioni (altrimenti si usa il batch)
			this.tipoDatabase = this.op2Properties.getDatabaseType();
			if(this.tipoDatabase==null){
				throw new Exception("Tipo Database non definito");
			}
			
			String tipoDatabaseTransazioni = DBTransazioniManager.getInstance().getTipoDatabase();
			if(tipoDatabaseTransazioni==null){
				throw new Exception("Tipo Database Transazioni non definito");
			}

			// Inizializzazione datasource
//			GestoreJNDI jndi = new GestoreJNDI();
//			this.ds = (DataSource) jndi.lookup(this.datasource);
		
			// DAOFactory
			DAOFactoryProperties daoFactoryProperties = null;
			this.daoFactoryLogger = this.logSql;
			this.daoFactory = DAOFactory.getInstance(this.daoFactoryLogger);
			daoFactoryProperties = DAOFactoryProperties.getInstance(this.daoFactoryLogger);
			
			this.daoFactoryServiceManagerPropertiesTransazioni = 
					daoFactoryProperties.getServiceManagerProperties(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
			this.daoFactoryServiceManagerPropertiesTransazioni.setShowSql(this.debug);	
			this.daoFactoryServiceManagerPropertiesTransazioni.setDatabaseType(tipoDatabaseTransazioni);
			
			this.daoFactoryServiceManagerPropertiesStatistiche = 
					daoFactoryProperties.getServiceManagerProperties(org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance());
			this.daoFactoryServiceManagerPropertiesStatistiche.setShowSql(this.debug);	
			this.daoFactoryServiceManagerPropertiesStatistiche.setDatabaseType(tipoDatabaseTransazioni);
			
			if(this.generazioneStatisticheCustom){
			
				this.daoFactoryServiceManagerPropertiesPluginsStatistiche = 
						daoFactoryProperties.getServiceManagerProperties(org.openspcoop2.monitor.engine.config.statistiche.utils.ProjectInfo.getInstance());
				this.daoFactoryServiceManagerPropertiesPluginsStatistiche.setShowSql(this.debug);	
				this.daoFactoryServiceManagerPropertiesPluginsStatistiche.setDatabaseType(this.tipoDatabase);
				
				this.daoFactoryServiceManagerPropertiesPluginsBase = 
						daoFactoryProperties.getServiceManagerProperties(org.openspcoop2.monitor.engine.config.base.utils.ProjectInfo.getInstance());
				this.daoFactoryServiceManagerPropertiesPluginsBase.setShowSql(this.debug);	
				this.daoFactoryServiceManagerPropertiesPluginsBase.setDatabaseType(this.tipoDatabase);
				
				this.daoFactoryServiceManagerPropertiesUtils = 
						daoFactoryProperties.getServiceManagerProperties(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
				this.daoFactoryServiceManagerPropertiesUtils.setShowSql(this.debug);	
				this.daoFactoryServiceManagerPropertiesUtils.setDatabaseType(this.tipoDatabase);
				
				if(this.analisiTransazioniCustom){
				
					this.daoFactoryServiceManagerPropertiesPluginsTransazioni = 
							daoFactoryProperties.getServiceManagerProperties(org.openspcoop2.monitor.engine.config.transazioni.utils.ProjectInfo.getInstance());
					this.daoFactoryServiceManagerPropertiesPluginsTransazioni.setShowSql(this.debug);	
					this.daoFactoryServiceManagerPropertiesPluginsTransazioni.setDatabaseType(this.tipoDatabase);
					
				}
				
			}
				
		}catch(Exception e){
			throw new Exception("Errore durante l'inizializzazione del datasource: "+e.getMessage(),e);
		}
		
		
		try{
			this.statisticsConfig = new StatisticsConfig(false);
			
			this.statisticsConfig.setLogCore(this.logCore);
			this.statisticsConfig.setLogSql(this.logSql);
			this.statisticsConfig.setGenerazioneStatisticheCustom(this.generazioneStatisticheCustom);
			this.statisticsConfig.setAnalisiTransazioniCustom(this.analisiTransazioniCustom);
			this.statisticsConfig.setDebug(this.debug);
			this.statisticsConfig.setStatisticheOrarie(this.statisticheOrarie);
			this.statisticsConfig.setStatisticheGiornaliere(this.statisticheGiornaliere);
			this.statisticsConfig.setStatisticheSettimanali(this.statisticheSettimanali);
			this.statisticsConfig.setStatisticheMensili(this.statisticheMensili);
			this.statisticsConfig.setStatisticheOrarie_gestioneUltimoIntervallo(this.statisticheOrarie_gestioneUltimoIntervallo);
			this.statisticsConfig.setStatisticheGiornaliere_gestioneUltimoIntervallo(this.statisticheGiornaliere_gestioneUltimoIntervallo);
			this.statisticsConfig.setStatisticheSettimanali_gestioneUltimoIntervallo(this.statisticheSettimanali_gestioneUltimoIntervallo);
			this.statisticsConfig.setStatisticheMensili_gestioneUltimoIntervallo(this.statisticheMensili_gestioneUltimoIntervallo);
		}catch(Exception e){
			throw new Exception("Errore durante la generazione delle statistiche (InitConfigurazione): "+e.getMessage(),e);
		}
		
		this.timerLock = new TimerLock(TipoLock.GENERAZIONE_STATISTICHE); 
		
		if(this.op2Properties.isTimerLockByDatabase()) {
			this.semaphore_statistics = new InfoStatistics();

			SemaphoreConfiguration config = GestoreMessaggi.newSemaphoreConfiguration(this.op2Properties.getStatisticheGenerazioneTimer_lockMaxLife(), 
					this.op2Properties.getStatisticheGenerazioneTimer_lockIdleTime());

			TipiDatabase databaseType = TipiDatabase.toEnumConstant(this.tipoDatabase);
			try {
				this.semaphore = new Semaphore(this.semaphore_statistics, SemaphoreMapping.newInstance(this.timerLock.getIdLock()), 
						config, databaseType, this.logTimer);
			}catch(Exception e) {
				throw new TimerException(e.getMessage(),e);
			}
		}
	}
	
	public void check() throws TimerException {
		
		// Controllo che il sistema non sia andando in shutdown
		if(OpenSPCoop2Startup.contextDestroyed){
			this.logTimer.error("["+TimerStatisticheThread.ID_MODULO+"] Rilevato sistema in shutdown");
			return;
		}

		// Controllo che l'inizializzazione corretta delle risorse sia effettuata
		if(OpenSPCoop2Startup.initialize==false){
			this.msgDiag.logFatalError("inizializzazione di OpenSPCoop non effettuata", "Check Inizializzazione");
			String msgErrore = "Riscontrato errore: inizializzazione del Timer o di OpenSPCoop non effettuata";
			this.logTimer.error(msgErrore);
			throw new TimerException(msgErrore);
		}

		// Controllo risorse di sistema disponibili
		if( TimerMonitoraggioRisorseThread.risorseDisponibili == false){
			this.logTimer.error("["+TimerStatisticheThread.ID_MODULO+"] Risorse di sistema non disponibili: "+TimerMonitoraggioRisorseThread.risorsaNonDisponibile.getMessage(),TimerMonitoraggioRisorseThread.risorsaNonDisponibile);
			return;
		}
		if( MsgDiagnostico.gestoreDiagnosticaDisponibile == false){
			this.logTimer.error("["+TimerStatisticheThread.ID_MODULO+"] Sistema di diagnostica non disponibile: "+MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage(),MsgDiagnostico.motivoMalfunzionamentoDiagnostici);
			return;
		}
		
		
		this.msgDiag.logPersonalizzato("generazioneStatistiche");
		this.logTimer.info(this.msgDiag.getMessaggio_replaceKeywords("generazioneStatistiche"));
		long startControlloTimer = DateManager.getTimeMillis();
			
			
		DBTransazioniManager dbManager = null;
    	Resource r = null;
		try{

			dbManager = DBTransazioniManager.getInstance();
			r = dbManager.getResource(this.op2Properties.getIdentitaPortaDefault(null), TimerStatisticheThread.ID_MODULO, null);
			if(r==null){
				throw new Exception("Risorsa al database non disponibile");
			}
			Connection con = (Connection) r.getResource();
			if(con == null)
				throw new Exception("Connessione non disponibile");	

			org.openspcoop2.core.statistiche.dao.IServiceManager statisticheSM = 
					(org.openspcoop2.core.statistiche.dao.IServiceManager) 
					this.daoFactory.getServiceManager(org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance(), con,
						this.daoFactoryServiceManagerPropertiesStatistiche, this.daoFactoryLogger);
				
			org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM = 
					(org.openspcoop2.core.transazioni.dao.IServiceManager) 
						this.daoFactory.getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(), con,
						this.daoFactoryServiceManagerPropertiesTransazioni, this.daoFactoryLogger);
			
			org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager pluginsStatisticheSM = null;
			org.openspcoop2.monitor.engine.config.base.dao.IServiceManager pluginsBaseSM = null;
			org.openspcoop2.core.commons.search.dao.IServiceManager utilsSM = null;
			org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager pluginsTransazioniSM = null;
			if(this.generazioneStatisticheCustom){
				pluginsStatisticheSM = (org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager) 
						this.daoFactory.getServiceManager(
								org.openspcoop2.monitor.engine.config.statistiche.utils.ProjectInfo.getInstance(), con,
								this.daoFactoryServiceManagerPropertiesPluginsStatistiche, this.daoFactoryLogger);
				pluginsBaseSM = (org.openspcoop2.monitor.engine.config.base.dao.IServiceManager) 
						this.daoFactory.getServiceManager(
								org.openspcoop2.monitor.engine.config.base.utils.ProjectInfo.getInstance(), con,
								this.daoFactoryServiceManagerPropertiesPluginsBase, this.daoFactoryLogger);
				utilsSM = (org.openspcoop2.core.commons.search.dao.IServiceManager) 
						this.daoFactory.getServiceManager(
								org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance(), con,
								this.daoFactoryServiceManagerPropertiesUtils, this.daoFactoryLogger);
				if(this.analisiTransazioniCustom){
					pluginsTransazioniSM = (org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager) 
							this.daoFactory.getServiceManager(
									org.openspcoop2.monitor.engine.config.transazioni.utils.ProjectInfo.getInstance(), con,
									this.daoFactoryServiceManagerPropertiesPluginsTransazioni, this.daoFactoryLogger);
				}
			}
			
			// aggiorno configurazione per forceIndex
			this.statisticsConfig.setForceIndexConfig(this.op2Properties.getStatisticheGenerazioneExternalForceIndexRepository());
			
			String causa = "Generazione Statistiche";
			try {
				GestoreMessaggi.acquireLock(
						this.semaphore, con, this.timerLock,
						this.msgDiag, causa, 
						this.op2Properties.getMsgGiaInProcessamento_AttesaAttiva(), 
						this.op2Properties.getMsgGiaInProcessamento_CheckInterval());
				
				StatisticsLibrary sLibrary = new StatisticsLibrary(this.statisticsConfig, statisticheSM, transazioniSM, 
						pluginsStatisticheSM, pluginsBaseSM, utilsSM, pluginsTransazioniSM);
				
				
				// ORARIE
				
				if(generaStatistica("orario", con, sLibrary, TipoIntervalloStatistico.STATISTICHE_ORARIE) == false) {
					return; // problemi con il lock
				}
					
				
				// GIORNALIERE
				
				if(generaStatistica("giornaliero", con, sLibrary, TipoIntervalloStatistico.STATISTICHE_GIORNALIERE) == false) {
					return; // problemi con il lock
				}
				
				
				// SETTIMANALE
				
				if(generaStatistica("settimanale", con, sLibrary, TipoIntervalloStatistico.STATISTICHE_SETTIMANALI) == false) {
					return; // problemi con il lock
				}
							
				
				// MENSILE
				
				if(generaStatistica("mensile", con, sLibrary, TipoIntervalloStatistico.STATISTICHE_MENSILI) == false) {
					return; // problemi con il lock
				}
				
				
			}finally{
				try{
					GestoreMessaggi.releaseLock(
							this.semaphore, con, this.timerLock,
							this.msgDiag, causa);
				}catch(Exception e){}
			}
			
			
			
			// end
			long endControlloTimer = DateManager.getTimeMillis();
			long diff = (endControlloTimer-startControlloTimer);
			this.logTimer.info("Generazione Statistiche terminato in "+Utilities.convertSystemTimeIntoString_millisecondi(diff, true));
			
			
		}
		catch(TimerLockNotAvailableException t) {
			// msg diagnostico emesso durante l'emissione dell'eccezione
			this.logTimer.info(t.getMessage(),t);
		}
		catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"GenerazioneStatistiche");
			this.logTimer.error("Riscontrato errore durante la generazione delle statistiche: "+ e.getMessage(),e);
		}finally{
			try{
				if(r!=null)
					dbManager.releaseResource(this.op2Properties.getIdentitaPortaDefault(null), TimerStatisticheThread.ID_MODULO, r);
			}catch(Exception eClose){}
		}
			
	}

	
	private boolean generaStatistica(String intervallo, Connection con, StatisticsLibrary sLibrary, TipoIntervalloStatistico tipoIntervalloStatistico) {
		
		long startGenerazione = DateManager.getTimeMillis();
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_STATISTICA, intervallo); // riferito a intervallo
		this.msgDiag.logPersonalizzato("generazioneStatistiche.inCorso");
		this.logTimer.info(this.msgDiag.getMessaggio_replaceKeywords("generazioneStatistiche.inCorso"));
		
		try{
			GestoreMessaggi.updateLock(
					this.semaphore, con, this.timerLock,
					this.msgDiag, "Generazione statistiche intervallo '"+intervallo+"' ...");
		}catch(Throwable e){
			this.msgDiag.logErroreGenerico(e,"TimerStatistiche-UpdateLock");
			this.logTimer.error("TimerStatistiche-UpdateLock: "+e.getMessage(),e);
			return false;
		}
		
		switch (tipoIntervalloStatistico) {
		case STATISTICHE_ORARIE:
			sLibrary.generateStatisticaOraria();
			break;
		case STATISTICHE_GIORNALIERE:
			sLibrary.generateStatisticaGiornaliera();
			break;
		case STATISTICHE_SETTIMANALI:
			sLibrary.generateStatisticaSettimanale();
			break;
		case STATISTICHE_MENSILI:
			sLibrary.generateStatisticaMensile();
			break;
		}	
				
		long endGenerazione = DateManager.getTimeMillis();
		String tempoImpiegato = Utilities.convertSystemTimeIntoString_millisecondi((endGenerazione-startGenerazione), true);
		this.msgDiag.addKeyword(CostantiPdD.KEY_TEMPO_GENERAZIONE, tempoImpiegato); 
		this.msgDiag.logPersonalizzato("generazioneStatistiche.effettuata");
		this.logTimer.info(this.msgDiag.getMessaggio_replaceKeywords("generazioneStatistiche.effettuata"));
		
		return true;
	}
}
