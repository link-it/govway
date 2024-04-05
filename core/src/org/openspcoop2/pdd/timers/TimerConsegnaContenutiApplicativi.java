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



package org.openspcoop2.pdd.timers;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openspcoop2.pdd.config.ConfigurazioneCoda;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.ConfigurazionePriorita;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.MessaggioServizioApplicativo;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateDBManager;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.id.serial.InfoStatistics;
import org.openspcoop2.utils.semaphore.Semaphore;
import org.openspcoop2.utils.semaphore.SemaphoreConfiguration;
import org.openspcoop2.utils.semaphore.SemaphoreMapping;
import org.openspcoop2.utils.threads.IGestoreCodaRunnableInstance;
import org.openspcoop2.utils.threads.Runnable;
import org.openspcoop2.utils.threads.RunnableLogger;



/**
 * Timer che si occupa di re-inoltrare i messaggi in riconsegna
 * 
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class TimerConsegnaContenutiApplicativi implements IGestoreCodaRunnableInstance  {

	private static TimerState STATE = TimerState.OFF; // abilitato in OpenSPCoop2Startup al momento dell'avvio

	public static TimerState getSTATE() {
		return STATE;
	}
	public static void setSTATE(TimerState sTATE) {
		STATE = sTATE;
	}

	private MsgDiagnostico msgDiag = null;
	private RunnableLogger log;
	private RunnableLogger logSql;
	private OpenSPCoop2Properties propertiesReader = null;
	private boolean debug;
	private String clusterId;

	private RegistroServiziManager registroServiziReader;
	private ConfigurazionePdDManager configurazionePdDReader;

	private ConfigurazioneCoda configurazioneCoda;
	
	private List<ConfigurazionePriorita> configurazioniPriorita;
	
	private TimerLock timerLock = null;

	/** Semaforo */
	private Semaphore semaphore = null;
	private InfoStatistics semaphore_statistics;

	private Date lastCheckMessaggiDaRispedire = null;
	
	public TimerConsegnaContenutiApplicativi(ConfigurazioneCoda configurazioneCoda, MsgDiagnostico msgDiag,
			RunnableLogger log, RunnableLogger logSql,
			OpenSPCoop2Properties p,
			ConfigurazionePdDManager configurazionePdDReader,RegistroServiziManager registroServiziReader) throws TimerException{
		this.configurazioneCoda = configurazioneCoda;
		this.msgDiag = msgDiag;
		this.log = log;
		this.logSql = logSql;
		this.propertiesReader = p;
		this.debug = configurazioneCoda.isDebug();
		this.clusterId = p.getClusterId(false);
		
		this.configurazioniPriorita = new ArrayList<ConfigurazionePriorita>();
		List<String> prioritaList = this.propertiesReader.getTimerConsegnaContenutiApplicativiPriorita();
		for (int i = 0; i < prioritaList.size(); i++) {
			String priorita = prioritaList.get(i);
			this.configurazioniPriorita.add(this.propertiesReader.getTimerConsegnaContenutiApplicativiConfigurazionePriorita(priorita));
		}
		
		this.configurazionePdDReader = configurazionePdDReader;
		this.registroServiziReader = registroServiziReader;

		// deve essere utilizzato lo stesso lock per GestoreMessaggi, ConsegnaContenuti, GestoreBuste per risolvere problema di eliminazione descritto in GestoreMessaggi metodo deleteMessageWithLock 
		//this.timerLock = new TimerLock(TipoLock.GESTIONE_REPOSITORY_MESSAGGI);
		// l'utilizzo commentato sopra era ERRATO: i messaggi salvati con il timer di consegna contenuti applicativi vengono salvati con un nuovo identificativo 'gw-0-e20ae327-791c-40ef-84ad-4b141a0ef93f' e quindi non impattano sul problema descritto sopra.
		this.timerLock = new TimerLock(TipoLock.CONSEGNA_NOTIFICHE, configurazioneCoda.getName());
		
		if(this.propertiesReader.isTimerLockByDatabase()) {
			this.semaphore_statistics = new InfoStatistics();

			SemaphoreConfiguration config = GestoreMessaggi.newSemaphoreConfiguration(this.propertiesReader.getTimerConsegnaContenutiApplicativi_lockMaxLife(), 
					this.propertiesReader.getTimerConsegnaContenutiApplicativi_lockIdleTime());

			TipiDatabase databaseType = TipiDatabase.toEnumConstant(this.propertiesReader.getDatabaseType());
			try {
				this.semaphore = new Semaphore(this.semaphore_statistics, SemaphoreMapping.newInstance(this.timerLock.getIdLock()), 
						config, databaseType, this.log.getLog());
			}catch(Exception e) {
				throw new TimerException(e.getMessage(),e);
			}
		}
	}

	@Override
	public void initialize(RunnableLogger log) throws UtilsException{
		
		OpenSPCoopStateful openspcoopstateGestore = new OpenSPCoopStateful();
		try {

			this.logDebug("Rilascio eventuali messaggi con lock appesi da riconsegnare verso il modulo ConsegnaContenutiApplicativi ...");
			
			openspcoopstateGestore.initResource(this.propertiesReader.getIdentitaPortaDefaultWithoutProtocol(),TimerConsegnaContenutiApplicativiThread.ID_MODULO, "initialize",
					OpenSPCoopStateDBManager.smistatoreMessaggiPresiInCarico);
			Connection connectionDB = ((StateMessage)openspcoopstateGestore.getStatoRichiesta()).getConnectionDB();

			// GestoreMessaggi da Ricercare
			GestoreMessaggi gestoreMsgSearch = new GestoreMessaggi(openspcoopstateGestore, true,this.logSql.getLog(),this.msgDiag, null);
	
			String causaMessaggiINBOXDaRiconsegnare = "Rilascio eventuali messaggi con lock appesi da riconsegnare verso il modulo ConsegnaContenutiApplicativi";
			try{
				GestoreMessaggi.acquireLock(
						this.semaphore, connectionDB, this.timerLock,
						this.msgDiag, causaMessaggiINBOXDaRiconsegnare, 
						this.propertiesReader.getTimerConsegnaContenutiApplicativi_getLockAttesaAttiva(), 
						this.propertiesReader.getTimerConsegnaContenutiApplicativi_getLockCheckInterval());
			
				gestoreMsgSearch.releaseMessaggiPresaInCosegna(this.configurazioneCoda.getName(), this.clusterId, this.debug, this.logSql);
				
			}finally{
				try{
					GestoreMessaggi.releaseLock(
							this.semaphore, connectionDB, this.timerLock,
							this.msgDiag, causaMessaggiINBOXDaRiconsegnare);
				}catch(Exception e){
					// ignore
				}
			}
			
			this.logDebug("Rilascio effettuato di eventuali messaggi con lock appesi da riconsegnare verso il modulo ConsegnaContenutiApplicativi");
		}
		catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"GestioneMessaggiRilascioLockRiconsegnaConsegnaContenutiApplicativi");
			this.logError("Riscontrato errore durante la gestione del repository dei messaggi (Rilascio lock per riconsegna verso ConsegnaContenutiApplicativi): "+ e.getMessage(),e);
		}finally{
			if(openspcoopstateGestore!=null)
				openspcoopstateGestore.releaseResource();
		}
	}
	
	private static final String DATA_START = "DATA_START";
	
	@Override
	public void logCheckInProgress(Map<String, Object> context) {
		// Prendo la gestione
		this.msgDiag.logPersonalizzato("controlloInCorso");
		this.log.info(this.msgDiag.getMessaggio_replaceKeywords("controlloInCorso"));
		long startControlloRepositoryMessaggi = DateManager.getTimeMillis();
		context.put(DATA_START, startControlloRepositoryMessaggi);
	}
	
	@Override
	public void logRegisteredThreads(Map<String, Object> context, int nuoviThreadsAttivati) {
		if(nuoviThreadsAttivati>0){
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_CONSEGNA_CONTENUTI_APPLICATIVI_NUMERO_MESSAGGI_INOLTRATI,nuoviThreadsAttivati+"");
			this.msgDiag.logPersonalizzato("ricercaMessaggiDaInoltrare");
		}
	}
	
	@Override
	public void logCheckFinished(Map<String, Object> context) {
		
		// end
		long endControlloRepositoryMessaggi = DateManager.getTimeMillis();
		long startControlloRepositoryMessaggi = (Long) context.get(DATA_START);
		long diff = (endControlloRepositoryMessaggi-startControlloRepositoryMessaggi);
		this.log.info("Controllo Repository Messaggi (Riconsegna verso ConsegnaContenutiApplicativi) terminato in "+Utilities.convertSystemTimeIntoStringMillisecondi(diff, true));

	}
	
	
	@Override
	public List<Runnable> nextRunnable(int limit) throws UtilsException{
			
		List<Runnable> returnNull = null;
		
		// Controllo che il sistema non sia andando in shutdown
		if(OpenSPCoop2Startup.contextDestroyed){
			this.logError("Rilevato sistema in shutdown");
			return returnNull;
		}

		// Controllo che l'inizializzazione corretta delle risorse sia effettuata
		if(!OpenSPCoop2Startup.initialize){
			this.msgDiag.logFatalError("inizializzazione di OpenSPCoop non effettuata", "Check Inizializzazione");
			String msgErrore = "Riscontrato errore: inizializzazione del Timer o di OpenSPCoop non effettuata";
			this.logError(msgErrore);
			throw new UtilsException(msgErrore);
		}

		// Controllo risorse di sistema disponibili
		if( !TimerMonitoraggioRisorseThread.isRisorseDisponibili()){
			this.logError("Risorse di sistema non disponibili: "+TimerMonitoraggioRisorseThread.getRisorsaNonDisponibile().getMessage(),TimerMonitoraggioRisorseThread.getRisorsaNonDisponibile());
			return returnNull;
		}
		if( !MsgDiagnostico.gestoreDiagnosticaDisponibile){
			this.logError("Sistema di diagnostica non disponibile: "+MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage(),MsgDiagnostico.motivoMalfunzionamentoDiagnostici);
			return returnNull;
		}
		
		// Controllo che il timer non sia stato momentaneamente disabilitato
		if(!TimerState.ENABLED.equals(STATE)) {
			this.msgDiag.logPersonalizzato("disabilitato");
			this.log.info(this.msgDiag.getMessaggio_replaceKeywords("disabilitato"));
			return returnNull;
		}
		
		OpenSPCoopStateful openspcoopstateGestore = new OpenSPCoopStateful();
		try {

			this.logDebug("Inizializzazione connessione al db per ricercare nuovi threads da attivare (limit: "+limit+") ...");
			openspcoopstateGestore.initResource(this.propertiesReader.getIdentitaPortaDefaultWithoutProtocol(),TimerConsegnaContenutiApplicativiThread.ID_MODULO, "nextRunnable",
					OpenSPCoopStateDBManager.smistatoreMessaggiPresiInCarico);
			Connection connectionDB = ((StateMessage)openspcoopstateGestore.getStatoRichiesta()).getConnectionDB();

			boolean verificaPresenzaMessaggiDaRispedire = false;
			boolean calcolaDataMinimaMessaggiRispedire = false;
			Integer secondiAnzianitaPerIniziareSpedireNuovoMessaggio = this.configurazioneCoda.getScheduleMessageAfter();
			if(this.lastCheckMessaggiDaRispedire==null) {
				this.lastCheckMessaggiDaRispedire=DateManager.getDate();
				verificaPresenzaMessaggiDaRispedire = true;
			}
			else {
				Date expired = new Date(DateManager.getTimeMillis()-(1000*this.configurazioneCoda.getNextMessages_consegnaFallita_intervalloControllo()));
				if(this.lastCheckMessaggiDaRispedire.before(expired)) {
					verificaPresenzaMessaggiDaRispedire = true;
					this.lastCheckMessaggiDaRispedire=DateManager.getDate();
				}
			}
			if(verificaPresenzaMessaggiDaRispedire) {
				calcolaDataMinimaMessaggiRispedire = this.configurazioneCoda.isNextMessages_consegnaFallita_calcolaDataMinimaRiconsegna();
			}

			
			// GestoreMessaggi da Ricercare
			GestoreMessaggi gestoreMsgSearch = new GestoreMessaggi(openspcoopstateGestore, true,this.logSql.getLog(),this.msgDiag, null);
			
			Date now = DateManager.getDate();
			List<MessaggioServizioApplicativo> msgDaRiconsegnareINBOX = new ArrayList<>();
			
			// APPLICATIVI PRIORITARI
			List<String> serviziApplicativiPrioritari = this.configurazionePdDReader.getServiziApplicativiConsegnaNotifichePrioritarie(this.configurazioneCoda.getName());
			if(serviziApplicativiPrioritari!=null && !serviziApplicativiPrioritari.isEmpty()) {
				
				String prefix = "[Applicativi Prioritari] ";
				String causale = prefix+"Messaggi da riconsegnare verso il modulo ConsegnaContenutiApplicativi";
				try {
					this.logDebug(prefix+"Acquisizione lock per ricercare nuovi threads da attivare (limit: "+limit+") ...");
					this.lock(connectionDB, causale);
					
					this.logDebug(prefix+"Lock acquisito, ricerca nuovi threads da attivare (limit: "+limit+") ...");
					List<MessaggioServizioApplicativo> msgDaRiconsegnareINBOX_priorita = 
							gestoreMsgSearch.readMessaggiDaRiconsegnareIntoBoxByServiziApplicativPrioritari(limit,
									verificaPresenzaMessaggiDaRispedire, calcolaDataMinimaMessaggiRispedire,secondiAnzianitaPerIniziareSpedireNuovoMessaggio,
									now,
									this.propertiesReader.getTimerConsegnaContenutiApplicativi_presaInConsegnaMaxLife(),
									this.debug,this.logSql,
									this.configurazioneCoda.getName(),
									serviziApplicativiPrioritari.toArray(new String[1]));
					
					if(msgDaRiconsegnareINBOX_priorita!=null && !msgDaRiconsegnareINBOX_priorita.isEmpty()) {
						this.logDebug(prefix+"Ricerca nuovi threads da attivare terminata (limit: "+limit+"); prendo in carico "+
								msgDaRiconsegnareINBOX_priorita.size()+" messaggi ...");
						
						for (MessaggioServizioApplicativo messaggioServizioApplicativo : msgDaRiconsegnareINBOX_priorita) {
							GestoreMessaggi messaggioDaInviare = new GestoreMessaggi(openspcoopstateGestore,true,messaggioServizioApplicativo.getIdMessaggio(),Costanti.INBOX,
									this.log.getLog(),this.msgDiag,null);
							messaggioDaInviare.updateMessaggioPresaInCosegna(messaggioServizioApplicativo.getServizioApplicativo(), 
									this.clusterId, this.debug, this.logSql);
							
							msgDaRiconsegnareINBOX.add(messaggioServizioApplicativo);
						}
						
						this.logDebug(prefix+"Presa in carico "+msgDaRiconsegnareINBOX_priorita.size()+" messaggi terminata, rilascio lock...");
					}
					else {
						this.logDebug(prefix+"Ricerca nuovi threads da attivare terminata (limit: "+limit+"); non sono presenti messaggi, rilascio lock...");
					}
					
				}finally{
					this.releaseLock(connectionDB, causale);
				}
				this.logDebug(prefix+"Lock rilasciato");
			}
			
			int finestraAncoraDisponibileDopoApplicativiPrioritari = limit - msgDaRiconsegnareINBOX.size();
						
			if(finestraAncoraDisponibileDopoApplicativiPrioritari>0) {
				for (ConfigurazionePriorita configurazionePriorita : this.configurazioniPriorita) {
	
					String prefix = "[P-"+configurazionePriorita.getLabel()+"] ";
					
					int limitPriorita = 0;
					if(configurazionePriorita.isNessunaPriorita()) {
						limitPriorita = limit - msgDaRiconsegnareINBOX.size();
						this.logDebug(prefix+"Calcolo limit; cerco entries rimaste senza guardare la priorità ("+limitPriorita+") ...");
					}
					else {
						limitPriorita = (finestraAncoraDisponibileDopoApplicativiPrioritari * configurazionePriorita.getPercentuale()) / 100;
						this.logDebug(prefix+"Calcolo limit; "+configurazionePriorita.getPercentuale()+"% di "+finestraAncoraDisponibileDopoApplicativiPrioritari+": "+limitPriorita+" ...");
					}
					if(limitPriorita<=0) {
						this.logDebug(prefix+"Per la priorità non è necessario cercare alcun messaggio");
						continue;
					}
					
					String causale = prefix+"Messaggi da riconsegnare verso il modulo ConsegnaContenutiApplicativi";
					try {
						this.logDebug(prefix+"Acquisizione lock per ricercare nuovi threads da attivare (limit: "+limitPriorita+") ...");
						this.lock(connectionDB, causale);
						
						this.logDebug(prefix+"Lock acquisito, ricerca nuovi threads da attivare (limit: "+limitPriorita+") ...");
						List<MessaggioServizioApplicativo> msgDaRiconsegnareINBOXpriorita = 
								gestoreMsgSearch.readMessaggiDaRiconsegnareIntoBoxByPriorita(limitPriorita,
										verificaPresenzaMessaggiDaRispedire, calcolaDataMinimaMessaggiRispedire, secondiAnzianitaPerIniziareSpedireNuovoMessaggio,
										now,
										this.propertiesReader.getTimerConsegnaContenutiApplicativi_presaInConsegnaMaxLife(),
										this.debug,this.logSql,
										this.configurazioneCoda.getName(),
										configurazionePriorita.isNessunaPriorita() ? null : configurazionePriorita.getName());
						
						if(msgDaRiconsegnareINBOXpriorita!=null && !msgDaRiconsegnareINBOXpriorita.isEmpty()) {
							this.logDebug(prefix+"Ricerca nuovi threads da attivare terminata (limit: "+limitPriorita+"); prendo in carico "+
									msgDaRiconsegnareINBOXpriorita.size()+" messaggi ...");
							
							// La finestra non deve essere ricalcolata
							/**finestraAncoraDisponibileDopoApplicativiPrioritari = finestraAncoraDisponibileDopoApplicativiPrioritari - msgDaRiconsegnareINBOX_priorita.size();*/
							
							for (MessaggioServizioApplicativo messaggioServizioApplicativo : msgDaRiconsegnareINBOXpriorita) {
								GestoreMessaggi messaggioDaInviare = new GestoreMessaggi(openspcoopstateGestore,true,messaggioServizioApplicativo.getIdMessaggio(),Costanti.INBOX,
										this.log.getLog(),this.msgDiag,null);
								messaggioDaInviare.updateMessaggioPresaInCosegna(messaggioServizioApplicativo.getServizioApplicativo(), 
										this.clusterId, this.debug, this.logSql);
								
								msgDaRiconsegnareINBOX.add(messaggioServizioApplicativo);
							}
							
							this.logDebug(prefix+"Presa in carico "+msgDaRiconsegnareINBOXpriorita.size()+" messaggi terminata, rilascio lock...");
						}
						else {
							this.logDebug(prefix+"Ricerca nuovi threads da attivare terminata (limit: "+limitPriorita+"); non sono presenti messaggi, rilascio lock...");
						}
						
					}finally{
						this.releaseLock(connectionDB, causale);
					}
					this.logDebug(prefix+"Lock rilasciato");
					
				}
			}
			
			this.logDebug("Creazione Runnable ...");
			List<Runnable> listRunnable = null;
			if(msgDaRiconsegnareINBOX!=null && !msgDaRiconsegnareINBOX.isEmpty()) {
				listRunnable = new ArrayList<>();
				for (MessaggioServizioApplicativo messaggioServizioApplicativo : msgDaRiconsegnareINBOX) {
					TimerConsegnaContenutiApplicativiSender sender = 
							new TimerConsegnaContenutiApplicativiSender(messaggioServizioApplicativo, 
									this.registroServiziReader, this.configurazionePdDReader, 
									this.clusterId, this.configurazioneCoda);
					listRunnable.add(new Runnable(sender, -1));
				}
			}
			this.logDebug("Creazione Runnable terminata");
			
			return listRunnable;
		} 
		catch(TimerLockNotAvailableException t) {
			// msg diagnostico emesso durante l'emissione dell'eccezione
			this.log.info(t.getMessage(),t);
		}
		catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"GestioneMessaggiRiconsegnaConsegnaContenutiApplicativi");
			this.logError("Riscontrato errore durante la gestione del repository dei messaggi (Riconsegna verso ConsegnaContenutiApplicativi): "+ e.getMessage(),e);
		}finally{
			if(openspcoopstateGestore!=null)
				openspcoopstateGestore.releaseResource();
		}
		
		return returnNull;
	}

	private void lock(Connection connectionDB, String causale) throws UtilsException, TimerLockNotAvailableException {
		GestoreMessaggi.acquireLock(
				this.semaphore, connectionDB, this.timerLock,
				this.msgDiag, causale, 
				this.propertiesReader.getTimerConsegnaContenutiApplicativi_getLockAttesaAttiva(), 
				this.propertiesReader.getTimerConsegnaContenutiApplicativi_getLockCheckInterval());
	}
	private void releaseLock(Connection connectionDB, String causale) {
		try{
			GestoreMessaggi.releaseLock(
					this.semaphore, connectionDB, this.timerLock,
					this.msgDiag, causale);
		}catch(Exception e){
			// ignore
		}
	}
	
	private void logDebug(String msg) {
		this.logDebug(msg, null);
	}
	private void logDebug(String msg, Throwable e) {
		if(e!=null) {
			this.log.debug(getPrefix()+msg, e);
		}
		else {
			this.log.debug(getPrefix()+msg);
		}
	}
	
	private void logError(String msg) {
		this.logError(msg, null);
	}
	private void logError(String msg, Throwable e) {
		if(e!=null) {
			this.log.error(getPrefix()+msg, e);
		}
		else {
			this.log.error(getPrefix()+msg);
		}
	}
	
	private String getPrefix() {
		if(this.configurazioneCoda!=null) {
			return "["+this.configurazioneCoda.getName()+"] ";
		}
		return "";
	}
}
