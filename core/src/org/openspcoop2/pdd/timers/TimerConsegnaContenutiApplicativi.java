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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.MessaggioServizioApplicativo;
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

	public static TimerState STATE = TimerState.OFF; // abilitato in OpenSPCoop2Startup al momento dell'avvio

	private MsgDiagnostico msgDiag = null;
	private RunnableLogger log;
	private RunnableLogger logSql;
	private OpenSPCoop2Properties propertiesReader = null;
	private int limit = CostantiPdD.LIMIT_MESSAGGI_GESTORI;
	private boolean debug;
	private String clusterId;

	private RegistroServiziManager registroServiziReader;
	private ConfigurazionePdDManager configurazionePdDReader;

	private TimerLock timerLock = null;

	/** Semaforo */
	private Semaphore semaphore = null;
	private InfoStatistics semaphore_statistics;

	public TimerConsegnaContenutiApplicativi(MsgDiagnostico msgDiag,
			RunnableLogger log, RunnableLogger logSql,
			OpenSPCoop2Properties p,
			ConfigurazionePdDManager configurazionePdDReader,RegistroServiziManager registroServiziReader) throws TimerException{
		this.msgDiag = msgDiag;
		this.log = log;
		this.logSql = logSql;
		this.propertiesReader = p;
		this.debug = p.isTimerConsegnaContenutiApplicativiDebug();
		this.limit = p.getTimerConsegnaContenutiApplicativiLimit();
		this.clusterId = p.getClusterId(false);
		
		this.configurazionePdDReader = configurazionePdDReader;
		this.registroServiziReader = registroServiziReader;

		// deve essere utilizzato lo stesso lock per GestoreMessaggi, ConsegnaContenuti, GestoreBuste per risolvere problema di eliminazione descritto in GestoreMessaggi metodo deleteMessageWithLock 
		this.timerLock = new TimerLock(TipoLock.GESTIONE_REPOSITORY_MESSAGGI);

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

			this.log.debug("Rilascio eventuali messaggi con lock appesi da riconsegnare verso il modulo ConsegnaContenutiApplicativi ...");
			
			openspcoopstateGestore.initResource(this.propertiesReader.getIdentitaPortaDefault(null),TimerConsegnaContenutiApplicativiThread.ID_MODULO, "initialize");
			Connection connectionDB = ((StateMessage)openspcoopstateGestore.getStatoRichiesta()).getConnectionDB();

			// GestoreMessaggi da Ricercare
			GestoreMessaggi gestoreMsgSearch = new GestoreMessaggi(openspcoopstateGestore, true,this.logSql.getLog(),this.msgDiag, null);
	
			String causaMessaggiINBOXDaRiconsegnare = "Rilascio eventuali messaggi con lock appesi da riconsegnare verso il modulo ConsegnaContenutiApplicativi";
			try{
				GestoreMessaggi.acquireLock(
						this.semaphore, connectionDB, this.timerLock,
						this.msgDiag, causaMessaggiINBOXDaRiconsegnare, 
						this.propertiesReader.getMsgGiaInProcessamento_AttesaAttiva(), 
						this.propertiesReader.getMsgGiaInProcessamento_CheckInterval());
			
				gestoreMsgSearch.releaseMessaggiPresaInCosegna(this.clusterId, this.debug, this.logSql);
				
			}finally{
				try{
					GestoreMessaggi.releaseLock(
							this.semaphore, connectionDB, this.timerLock,
							this.msgDiag, causaMessaggiINBOXDaRiconsegnare);
				}catch(Exception e){}
			}
			
			this.log.debug("Rilascio effettuato di eventuali messaggi con lock appesi da riconsegnare verso il modulo ConsegnaContenutiApplicativi");
		}
		catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"GestioneMessaggiRilascioLockRiconsegnaConsegnaContenutiApplicativi");
			this.log.error("Riscontrato errore durante la gestione del repository dei messaggi (Rilascio lock per riconsegna verso ConsegnaContenutiApplicativi): "+ e.getMessage(),e);
		}finally{
			if(openspcoopstateGestore!=null)
				openspcoopstateGestore.releaseResource();
		}
	}
	
	private final static String DATA_START = "DATA_START";
	
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
		this.log.info("Controllo Repository Messaggi (Riconsegna verso ConsegnaContenutiApplicativi) terminato in "+Utilities.convertSystemTimeIntoString_millisecondi(diff, true));

	}
	
	
	@Override
	public List<Runnable> nextRunnable(int limit) throws UtilsException{
			
		// Controllo che il sistema non sia andando in shutdown
		if(OpenSPCoop2Startup.contextDestroyed){
			this.log.error("Rilevato sistema in shutdown");
			return null;
		}

		// Controllo che l'inizializzazione corretta delle risorse sia effettuata
		if(OpenSPCoop2Startup.initialize==false){
			this.msgDiag.logFatalError("inizializzazione di OpenSPCoop non effettuata", "Check Inizializzazione");
			String msgErrore = "Riscontrato errore: inizializzazione del Timer o di OpenSPCoop non effettuata";
			this.log.error(msgErrore);
			throw new UtilsException(msgErrore);
		}

		// Controllo risorse di sistema disponibili
		if( TimerMonitoraggioRisorseThread.risorseDisponibili == false){
			this.log.error("Risorse di sistema non disponibili: "+TimerMonitoraggioRisorseThread.risorsaNonDisponibile.getMessage(),TimerMonitoraggioRisorseThread.risorsaNonDisponibile);
			return null;
		}
		if( MsgDiagnostico.gestoreDiagnosticaDisponibile == false){
			this.log.error("Sistema di diagnostica non disponibile: "+MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage(),MsgDiagnostico.motivoMalfunzionamentoDiagnostici);
			return null;
		}
		
		// Controllo che il timer non sia stato momentaneamente disabilitato
		if(!TimerState.ENABLED.equals(STATE)) {
			this.msgDiag.logPersonalizzato("disabilitato");
			this.log.info(this.msgDiag.getMessaggio_replaceKeywords("disabilitato"));
			return null;
		}
		
		OpenSPCoopStateful openspcoopstateGestore = new OpenSPCoopStateful();
		try {

			openspcoopstateGestore.initResource(this.propertiesReader.getIdentitaPortaDefault(null),TimerConsegnaContenutiApplicativiThread.ID_MODULO, "nextRunnable");
			Connection connectionDB = ((StateMessage)openspcoopstateGestore.getStatoRichiesta()).getConnectionDB();

			// GestoreMessaggi da Ricercare
			GestoreMessaggi gestoreMsgSearch = new GestoreMessaggi(openspcoopstateGestore, true,this.logSql.getLog(),this.msgDiag, null);
			
			Date now = DateManager.getDate();
			
			String causaMessaggiINBOXDaRiconsegnare = "Messaggi da riconsegnare verso il modulo ConsegnaContenutiApplicativi";
			List<MessaggioServizioApplicativo> msgDaRiconsegnareINBOX = null;
			try{
				GestoreMessaggi.acquireLock(
						this.semaphore, connectionDB, this.timerLock,
						this.msgDiag, causaMessaggiINBOXDaRiconsegnare, 
						this.propertiesReader.getMsgGiaInProcessamento_AttesaAttiva(), 
						this.propertiesReader.getMsgGiaInProcessamento_CheckInterval());
				
				msgDaRiconsegnareINBOX = 
						gestoreMsgSearch.readMessaggiDaRiconsegnareIntoBox(this.limit,now,this.propertiesReader.getTimerConsegnaContenutiApplicativi_presaInConsegnaMaxLife(),
								this.debug,this.logSql);
				if(msgDaRiconsegnareINBOX!=null && msgDaRiconsegnareINBOX.size()>0) {
					
					for (MessaggioServizioApplicativo messaggioServizioApplicativo : msgDaRiconsegnareINBOX) {
					
						GestoreMessaggi messaggioDaInviare = new GestoreMessaggi(openspcoopstateGestore,true,messaggioServizioApplicativo.getIdMessaggio(),Costanti.INBOX,
								this.log.getLog(),this.msgDiag,null);
						messaggioDaInviare.updateMessaggioPresaInCosegna(messaggioServizioApplicativo.getServizioApplicativo(), 
								this.clusterId, this.debug, this.logSql);
						
					}
				}
				
			}finally{
				try{
					GestoreMessaggi.releaseLock(
							this.semaphore, connectionDB, this.timerLock,
							this.msgDiag, causaMessaggiINBOXDaRiconsegnare);
				}catch(Exception e){}
			}
			
			
			List<Runnable> listRunnable = null;
			if(msgDaRiconsegnareINBOX!=null && msgDaRiconsegnareINBOX.size()>0) {
				listRunnable = new ArrayList<Runnable>();
				for (MessaggioServizioApplicativo messaggioServizioApplicativo : msgDaRiconsegnareINBOX) {
					TimerConsegnaContenutiApplicativiSender sender = 
							new TimerConsegnaContenutiApplicativiSender(messaggioServizioApplicativo, 
									this.registroServiziReader, this.configurazionePdDReader, 
									this.clusterId);
					listRunnable.add(new Runnable(sender, -1));
				}
			}
			
			return listRunnable;
		} 
		catch(TimerLockNotAvailableException t) {
			// msg diagnostico emesso durante l'emissione dell'eccezione
			this.log.info(t.getMessage(),t);
		}
		catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"GestioneMessaggiRiconsegnaConsegnaContenutiApplicativi");
			this.log.error("Riscontrato errore durante la gestione del repository dei messaggi (Riconsegna verso ConsegnaContenutiApplicativi): "+ e.getMessage(),e);
		}finally{
			if(openspcoopstateGestore!=null)
				openspcoopstateGestore.releaseResource();
		}
		
		return null;
	}

}
