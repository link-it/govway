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

package org.openspcoop2.pdd.timers;


import java.sql.Connection;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneGet;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.statistiche.constants.TipoIntervalloStatistico;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.monitor.engine.statistic.StatisticsConfig;
import org.openspcoop2.monitor.engine.statistic.StatisticsLibrary;
import org.openspcoop2.pdd.config.ConfigurazionePdDReader;
import org.openspcoop2.pdd.config.DBStatisticheManager;
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
	private TipoIntervalloStatistico tipoStatistica;
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
	public TimerStatisticheLib(TipoIntervalloStatistico tipoStatistica, MsgDiagnostico msgDiag,Logger logTimer,OpenSPCoop2Properties p) throws Exception{
	
		this.msgDiag = msgDiag;
		this.op2Properties = p;
		
		this.debug = this.op2Properties.isStatisticheGenerazioneDebug();
		
		this.logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopStatistiche(this.debug);
		this.logSql = OpenSPCoop2Logger.getLoggerOpenSPCoopStatisticheSql(this.debug);
		this.logTimer = logTimer;
		
		this.generazioneStatisticheCustom = this.op2Properties.isStatisticheGenerazioneCustomEnabled();
		this.analisiTransazioniCustom = this.op2Properties.isStatisticheGenerazioneCustomSdkEnabled();
		
		this.tipoStatistica = tipoStatistica;
		
		switch (this.tipoStatistica) {
		case STATISTICHE_ORARIE:
			this.statisticheOrarie = this.op2Properties.isStatisticheGenerazioneBaseOrariaEnabled();
			this.statisticheOrarie_gestioneUltimoIntervallo = this.op2Properties.isStatisticheGenerazioneBaseOrariaEnabledUltimaOra();
			break;
		case STATISTICHE_GIORNALIERE:
			this.statisticheGiornaliere = this.op2Properties.isStatisticheGenerazioneBaseGiornalieraEnabled();
			this.statisticheGiornaliere_gestioneUltimoIntervallo = this.op2Properties.isStatisticheGenerazioneBaseGiornalieraEnabledUltimoGiorno();
			break;
		case STATISTICHE_SETTIMANALI:
			this.statisticheSettimanali = this.op2Properties.isStatisticheGenerazioneBaseSettimanaleEnabled();
			this.statisticheSettimanali_gestioneUltimoIntervallo = this.op2Properties.isStatisticheGenerazioneBaseSettimanaleEnabledUltimaSettimana();
			break;
		case STATISTICHE_MENSILI:
			this.statisticheMensili = this.op2Properties.isStatisticheGenerazioneBaseMensileEnabled();
			this.statisticheMensili_gestioneUltimoIntervallo = this.op2Properties.isStatisticheGenerazioneBaseMensileEnabledUltimoMese();
			break;
		default:
			break;
		}
		
				
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
		
		switch (this.tipoStatistica) {
		case STATISTICHE_ORARIE:
			this.timerLock = new TimerLock(TipoLock.GENERAZIONE_STATISTICHE_ORARIE); 
			break;
		case STATISTICHE_GIORNALIERE:
			this.timerLock = new TimerLock(TipoLock.GENERAZIONE_STATISTICHE_GIORNALIERE); 
			break;
		case STATISTICHE_SETTIMANALI:
			this.timerLock = new TimerLock(TipoLock.GENERAZIONE_STATISTICHE_SETTIMANALI); 
			break;
		case STATISTICHE_MENSILI:
			this.timerLock = new TimerLock(TipoLock.GENERAZIONE_STATISTICHE_MENSILI); 
			break;
		default:
			break;
		}
		
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
			
			
		DBTransazioniManager dbTransazioniManager = null;
		DBStatisticheManager dbStatisticheManager = null;
    	Resource rTransazioni = null;
    	Resource rStatistiche = null;
    	Connection conConfig = null;
    	DriverConfigurazioneDB driverConfigurazioneDB = null;
		try{

			dbTransazioniManager = DBTransazioniManager.getInstance();
			rTransazioni = dbTransazioniManager.getResource(this.op2Properties.getIdentitaPortaDefault(null), TimerStatisticheThread.ID_MODULO, null);
			if(rTransazioni==null){
				throw new Exception("Risorsa al database delle transazioni non disponibile");
			}
			Connection conTransazioni = (Connection) rTransazioni.getResource();
			if(conTransazioni == null)
				throw new Exception("Connessione al database delle transazioni non disponibile");	

			Connection conStatistiche = null;
			if(this.op2Properties.isStatisticheUseTransazioniDatasource()) {
				conStatistiche = conTransazioni; // non prendo due connessioni per "atterrare" sul solito database
			}
			else if(this.op2Properties.isStatisticheUsePddRuntimeDatasource() && this.op2Properties.isTransazioniUsePddRuntimeDatasource()) {
				conStatistiche = conTransazioni; // non prendo due connessioni per "atterrare" sul solito database
			}
			else {
				dbStatisticheManager = DBStatisticheManager.getInstance();
				rStatistiche = dbStatisticheManager.getResource(this.op2Properties.getIdentitaPortaDefault(null), TimerStatisticheThread.ID_MODULO, null);
				if(rStatistiche==null){
					throw new Exception("Risorsa al database delle statistiche non disponibile");
				}
				conStatistiche = (Connection) rStatistiche.getResource();
				if(conStatistiche == null)
					throw new Exception("Connessione al database delle statistiche non disponibile");	
			}
			
			org.openspcoop2.core.statistiche.dao.IServiceManager statisticheSM = 
					(org.openspcoop2.core.statistiche.dao.IServiceManager) 
					this.daoFactory.getServiceManager(org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance(), conStatistiche,
						this.daoFactoryServiceManagerPropertiesStatistiche, this.daoFactoryLogger);
				
			org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM = 
					(org.openspcoop2.core.transazioni.dao.IServiceManager) 
						this.daoFactory.getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(), conTransazioni,
						this.daoFactoryServiceManagerPropertiesTransazioni, this.daoFactoryLogger);
			
			org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager pluginsStatisticheSM = null;
			org.openspcoop2.monitor.engine.config.base.dao.IServiceManager pluginsBaseSM = null;
			org.openspcoop2.core.commons.search.dao.IServiceManager utilsSM = null;
			org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager pluginsTransazioniSM = null;
			if(this.generazioneStatisticheCustom){
				
				IDriverConfigurazioneGet driverConfigurazione = ConfigurazionePdDReader.getDriverConfigurazionePdD();
				if(driverConfigurazione instanceof DriverConfigurazioneDB) {
					driverConfigurazioneDB = (DriverConfigurazioneDB) driverConfigurazione;
				}
				else {
					throw new Exception("La generazione delle statistiche custom richiede una configurazione di tipo 'db', trovato: "+driverConfigurazione.getClass().getName());
				}
				conConfig = driverConfigurazioneDB.getConnection(TimerStatisticheThread.ID_MODULO+".customStats");
				
				pluginsStatisticheSM = (org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager) 
						this.daoFactory.getServiceManager(
								org.openspcoop2.monitor.engine.config.statistiche.utils.ProjectInfo.getInstance(), conConfig,
								this.daoFactoryServiceManagerPropertiesPluginsStatistiche, this.daoFactoryLogger);
				pluginsBaseSM = (org.openspcoop2.monitor.engine.config.base.dao.IServiceManager) 
						this.daoFactory.getServiceManager(
								org.openspcoop2.monitor.engine.config.base.utils.ProjectInfo.getInstance(), conConfig,
								this.daoFactoryServiceManagerPropertiesPluginsBase, this.daoFactoryLogger);
				utilsSM = (org.openspcoop2.core.commons.search.dao.IServiceManager) 
						this.daoFactory.getServiceManager(
								org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance(), conConfig,
								this.daoFactoryServiceManagerPropertiesUtils, this.daoFactoryLogger);
				if(this.analisiTransazioniCustom){
					pluginsTransazioniSM = (org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager) 
							this.daoFactory.getServiceManager(
									org.openspcoop2.monitor.engine.config.transazioni.utils.ProjectInfo.getInstance(), conConfig,
									this.daoFactoryServiceManagerPropertiesPluginsTransazioni, this.daoFactoryLogger);
				}
			}
			
			// aggiorno configurazione per forceIndex
			this.statisticsConfig.setForceIndexConfig(this.op2Properties.getStatisticheGenerazioneExternalForceIndexRepository());
			
			String causa = "Generazione Statistiche";
			try {
				GestoreMessaggi.acquireLock(
						this.semaphore, conStatistiche, this.timerLock,
						this.msgDiag, causa, 
						this.op2Properties.getMsgGiaInProcessamento_AttesaAttiva(), 
						this.op2Properties.getMsgGiaInProcessamento_CheckInterval());
				
				StatisticsLibrary sLibrary = new StatisticsLibrary(this.statisticsConfig, statisticheSM, transazioniSM, 
						pluginsStatisticheSM, pluginsBaseSM, utilsSM, pluginsTransazioniSM);
				
				
				switch (this.tipoStatistica) {
				case STATISTICHE_ORARIE:
					if(generaStatistica("orario", conStatistiche, sLibrary, TipoIntervalloStatistico.STATISTICHE_ORARIE) == false) {
						return; // problemi con il lock
					}
					break;
				case STATISTICHE_GIORNALIERE:
					if(generaStatistica("giornaliero", conStatistiche, sLibrary, TipoIntervalloStatistico.STATISTICHE_GIORNALIERE) == false) {
						return; // problemi con il lock
					}
					break;
				case STATISTICHE_SETTIMANALI:
					if(generaStatistica("settimanale", conStatistiche, sLibrary, TipoIntervalloStatistico.STATISTICHE_SETTIMANALI) == false) {
						return; // problemi con il lock
					}
					break;
				case STATISTICHE_MENSILI:
					if(generaStatistica("mensile", conStatistiche, sLibrary, TipoIntervalloStatistico.STATISTICHE_MENSILI) == false) {
						return; // problemi con il lock
					}
					break;
				default:
					break;
				}
				
			}finally{
				try{
					GestoreMessaggi.releaseLock(
							this.semaphore, conStatistiche, this.timerLock,
							this.msgDiag, causa);
				}catch(Exception e){}
			}
			
			
			
			// end
			long endControlloTimer = DateManager.getTimeMillis();
			long diff = (endControlloTimer-startControlloTimer);
			this.logTimer.info("Generazione '"+this.tipoStatistica.getValue()+"' terminato in "+Utilities.convertSystemTimeIntoString_millisecondi(diff, true));
			
			
		}
		catch(TimerLockNotAvailableException t) {
			// msg diagnostico emesso durante l'emissione dell'eccezione
			this.logTimer.info(t.getMessage(),t);
		}
		catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"GenerazioneStatistiche");
			this.logTimer.error("Riscontrato errore durante la generazione delle statistiche ("+this.tipoStatistica.getValue()+"): "+ e.getMessage(),e);
		}finally{
			try{
				if(rTransazioni!=null)
					dbTransazioniManager.releaseResource(this.op2Properties.getIdentitaPortaDefault(null), TimerStatisticheThread.ID_MODULO, rTransazioni);
			}catch(Throwable eClose){}
			try{
				if(rStatistiche!=null)
					dbStatisticheManager.releaseResource(this.op2Properties.getIdentitaPortaDefault(null), TimerStatisticheThread.ID_MODULO, rStatistiche);
			}catch(Throwable eClose){}
			try{
				if(conConfig!=null)
					driverConfigurazioneDB.releaseConnection(conConfig);
			}catch(Throwable eClose){}
		}
			
	}

	
	private boolean generaStatistica(String intervallo, Connection conStatistiche, StatisticsLibrary sLibrary, TipoIntervalloStatistico tipoIntervalloStatistico) {
		
		long startGenerazione = DateManager.getTimeMillis();
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_STATISTICA, intervallo); // riferito a intervallo
		this.msgDiag.logPersonalizzato("generazioneStatistiche.inCorso");
		this.logTimer.info(this.msgDiag.getMessaggio_replaceKeywords("generazioneStatistiche.inCorso"));
		
		try{
			GestoreMessaggi.updateLock(
					this.semaphore, conStatistiche, this.timerLock,
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
