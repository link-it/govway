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

package org.openspcoop2.pdd.timers.pdnd;


import java.sql.Connection;
import java.util.Date;

import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.eventi.Evento;
import org.openspcoop2.core.eventi.constants.TipoSeverita;
import org.openspcoop2.core.eventi.utils.SeveritaConverter;
import org.openspcoop2.pdd.config.ConfigurazionePdDReader;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.eventi.GestoreEventi;
import org.openspcoop2.pdd.core.keystore.KeystoreException;
import org.openspcoop2.pdd.core.keystore.RemoteStore;
import org.openspcoop2.pdd.core.keystore.RemoteStoreProviderDriverUtils;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.pdd.timers.TimerException;
import org.openspcoop2.pdd.timers.TimerLock;
import org.openspcoop2.pdd.timers.TimerLockNotAvailableException;
import org.openspcoop2.pdd.timers.TimerMonitoraggioRisorseThread;
import org.openspcoop2.pdd.timers.TimerState;
import org.openspcoop2.pdd.timers.TipoLock;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.certificate.remote.RemoteStoreConfig;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.id.serial.InfoStatistics;
import org.openspcoop2.utils.semaphore.Semaphore;
import org.openspcoop2.utils.semaphore.SemaphoreConfiguration;
import org.openspcoop2.utils.semaphore.SemaphoreMapping;
import org.slf4j.Logger;


/**     
 * TimerGestoreChiaviPDNDLib
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimerGestoreChiaviPDNDLib {

	private static TimerState state = TimerState.OFF; // abilitato in OpenSPCoop2Startup al momento dell'avvio
	public static TimerState getState() {
		return state;
	}
	public static void setState(TimerState stateParam) {
		state = stateParam;
	}
	
	private OpenSPCoop2Properties op2Properties;
	private String tipoDatabase = null;
	
	private Logger logCore = null;
	private Logger logTimer = null;
	private MsgDiagnostico msgDiag = null;
	
	private void logCoreInfo(String msg) {
		if(this.logCore!=null) {
			this.logCore.info(msg);
		}
	}
	private void logCoreError(String msgErrore, Throwable e) {
		if(this.logCore!=null) {
			this.logCore.error(msgErrore,e);
		}
	}
	
	private void logTimerError(String msgErrore, Throwable e) {
		if(this.logTimer!=null) {
			this.logTimer.error(msgErrore,e);
		}
	}
	private void logTimerError(String msgErrore) {
		if(this.logTimer!=null) {
			this.logTimer.error(msgErrore);
		}
	}
	private void logTimerInfo(String msg) {
		if(this.logTimer!=null) {
			this.logTimer.info(msg);
		}
	}
	private void logTimerInfo(String msg, Exception e) {
		if(this.logTimer!=null) {
			this.logTimer.info(msg, e);
		}
	}
	
	private RemoteStoreConfig remoteStore;
	private String urlCheckEventi;
	
	private String parameterLastEventId;
	private String parameterLimit;
	private int limit;
	
	private int timeoutSeconds = -1;

	/** Timer */
	private TimerLock timerLock = null;

	/** Semaforo */
	private Semaphore semaphore = null;
	private InfoStatistics semaphoreStatistics;
	
	/** Costruttore */
	public TimerGestoreChiaviPDNDLib(Logger logTimer, MsgDiagnostico msgDiag, RemoteStoreConfig remoteStore, 
			String urlCheckEventi, int timeoutSeconds) throws TimerException{
		
		this.op2Properties = OpenSPCoop2Properties.getInstance();
		this.tipoDatabase = this.op2Properties.getDatabaseType();
		if(this.tipoDatabase==null){
			throw new TimerException("Tipo Database non definito");
		}
		
		boolean debug = this.op2Properties.isGestoreChiaviPDNDDebug();
		
		this.logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopGestoreChiaviPDND(debug);
		this.logTimer = logTimer;
		this.msgDiag = msgDiag;
		
		this.remoteStore = remoteStore;
		this.urlCheckEventi = urlCheckEventi;
		
		this.timeoutSeconds = timeoutSeconds;
		
		try {
			this.parameterLastEventId = this.op2Properties.getGestoreChiaviPDNDeventsKeysParameterLastEventId();
			this.parameterLimit = this.op2Properties.getGestoreChiaviPDNDeventsKeysParameterLimit();
			this.limit = this.op2Properties.getGestoreChiaviPDNDeventsKeysLimit();
		}catch(Exception e) {
			throw new TimerException(e.getMessage(),e);
		}
		
		this.timerLock = new TimerLock(TipoLock.GESTORE_CHIAVI_PDND); 
		
		if(this.op2Properties.isTimerLockByDatabase()) {
			this.semaphoreStatistics = new InfoStatistics();

			SemaphoreConfiguration config = GestoreMessaggi.newSemaphoreConfiguration(this.op2Properties.getGestoreChiaviPDNDTimerLockMaxLife(), 
					this.op2Properties.getGestoreChiaviPDNDTimerLockIdleTime());

			TipiDatabase databaseType = TipiDatabase.toEnumConstant(this.tipoDatabase);
			try {
				this.semaphore = new Semaphore(this.semaphoreStatistics, SemaphoreMapping.newInstance(this.timerLock.getIdLock()), 
						config, databaseType, this.logTimer);
			}catch(Exception e) {
				throw new TimerException(e.getMessage(),e);
			}
		}
	}
	
	public void check() throws TimerException {
		
		// Controllo che il sistema non sia andando in shutdown
		if(OpenSPCoop2Startup.contextDestroyed){
			this.logTimerError("["+TimerGestoreChiaviPDND.ID_MODULO+"] Rilevato sistema in shutdown");
			return;
		}

		// Controllo che l'inizializzazione corretta delle risorse sia effettuata
		if(!OpenSPCoop2Startup.initialize){
			this.msgDiag.logFatalError("inizializzazione di OpenSPCoop non effettuata", "Check Inizializzazione");
			String msgErrore = "Riscontrato errore: inizializzazione del Timer o di OpenSPCoop non effettuata";
			this.logTimerError(msgErrore);
			throw new TimerException(msgErrore);
		}

		// Controllo risorse di sistema disponibili
		if( !TimerMonitoraggioRisorseThread.isRisorseDisponibili()){
			this.logTimerError("["+TimerGestoreChiaviPDND.ID_MODULO+"] Risorse di sistema non disponibili: "+TimerMonitoraggioRisorseThread.getRisorsaNonDisponibile().getMessage(),TimerMonitoraggioRisorseThread.getRisorsaNonDisponibile());
			return;
		}
		if( !MsgDiagnostico.gestoreDiagnosticaDisponibile){
			this.logTimerError("["+TimerGestoreChiaviPDND.ID_MODULO+"] Sistema di diagnostica non disponibile: "+MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage(),MsgDiagnostico.motivoMalfunzionamentoDiagnostici);
			return;
		}
		
		boolean enabled = TimerState.ENABLED.equals(TimerGestoreChiaviPDNDLib.state);
		if(!enabled) {
			emitDiagnosticLog("disabilitato");
			return;
		}
		
		emitDiagnosticLog("letturaEventi");

		long startControlloTimer = DateManager.getTimeMillis();
			
		Connection conConfigurazione = null;
    	DriverConfigurazioneDB driverConfigurazioneDbGestoreConnection = null;
		try{

			Object oConfig = ConfigurazionePdDReader.getDriverConfigurazionePdD();
			if(oConfig instanceof DriverConfigurazioneDB) {
				driverConfigurazioneDbGestoreConnection = (DriverConfigurazioneDB) oConfig;
				conConfigurazione = driverConfigurazioneDbGestoreConnection.getConnection(TimerGestoreChiaviPDND.ID_MODULO, false);
				if(conConfigurazione == null)
					throw new TimerException("Connessione al database della configurazione non disponibile");	
			}
			else {
				throw new TimerException("Gestore utilizzabile solamente con una configurazione su database");
			}
			
			
			String causa = "Gestione chiavi PDND";
			try {
				
				GestoreMessaggi.acquireLock(
						this.semaphore, conConfigurazione, this.timerLock,
						this.msgDiag, causa, 
						this.op2Properties.getGestoreChiaviPDNDTimerLockAttesaAttiva(), 
						this.op2Properties.getGestoreChiaviPDNDTimerLockCheckInterval());
				
				process(conConfigurazione);
				
			}finally{
				GestoreMessaggi.releaseSafeLock(
						this.semaphore, conConfigurazione, this.timerLock,
						this.msgDiag, causa);
			}
			
			// end
			long endControlloTimer = DateManager.getTimeMillis();
			long diff = (endControlloTimer-startControlloTimer);
			this.logTimerInfo("Gestione eventi per le chiavi della PDND terminata in "+Utilities.convertSystemTimeIntoStringMillisecondi(diff, true));
			
			
		}
		catch(TimerLockNotAvailableException t) {
			// msg diagnostico emesso durante l'emissione dell'eccezione
			this.logTimerInfo(t.getMessage(),t);
		}
		catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"TimerGestoreChiaviPDNDLib");
			this.logTimerError("Riscontrato errore durante la gestione eventi per le chiavi della PDND: "+ e.getMessage(),e);
		}finally{
			try{
				if(conConfigurazione!=null)
					driverConfigurazioneDbGestoreConnection.releaseConnection(conConfigurazione);
			}catch(Exception eClose){
				// ignore
			}
		}
			
	}

	private long lastEventId = 0;
		
	private void process(Connection conConfigurazione) throws KeystoreException {
		
		// Recupero id del Remote Store
		boolean created = readRemoteStoreDbImage(conConfigurazione);
		
		TimerGestoreChiaviPDNDUtilities pdndUtilities = new TimerGestoreChiaviPDNDUtilities(this.remoteStore, this.urlCheckEventi, this.parameterLastEventId, this.parameterLimit, this.limit);
		
		this.msgDiag.addKeyword(CostantiPdD.KEY_LIMIT, this.limit+"");
		
		boolean updateLasteEventId = false;
		
		if(created || this.remoteStoreDbImage.getLastEvent()==null) {
			// Initialize
			updateLasteEventId = initialize(conConfigurazione, pdndUtilities);
		}
		else {
			// Verifico ultima data di aggiornamento
			
			long now = DateManager.getTimeMillis();
			int msTimeout = this.timeoutSeconds * 1000;
			Date check = new Date(now-msTimeout);
			if(check.before(this.remoteStoreDbImage.getDataAggiornamento())) {
				emitDiagnosticLog("letturaEventi.nonNecessaria");
				return;
			}
			
			// leggo lastEventId
			long lastEventIdDbImage = Long.parseLong(this.remoteStoreDbImage.getLastEvent());
			this.lastEventId = lastEventIdDbImage;
			
			int letti = 1;
			long prec = 0; 
			while(letti > 0) {
				prec = this.lastEventId; 
				letti = gestione(conConfigurazione, pdndUtilities, true);
				if(this.lastEventId<=prec) {
					// l'ultima gestione non ha aggiornato lastEventId
					break;
				}
			}
			
			if(this.lastEventId>lastEventIdDbImage) {
				updateLasteEventId = true;
			}

		}
				
		// aggiorno lastEventId sul database
		if(updateLasteEventId) {
			RemoteStoreProviderDriverUtils.updateRemoteStore(conConfigurazione, this.tipoDatabase, this.remoteStoreDbImage.getId(), this.lastEventId+"");
		}
		
	}
	private boolean initialize(Connection conConfigurazione, TimerGestoreChiaviPDNDUtilities pdndUtilities) {
		
		boolean updateLasteEventId = false;
		
		emitDiagnosticLog("inizializzazione.inCorso");
		try {
			int letti = 1;
			long prec = 0; 
			while(letti > 0) {
				prec = this.lastEventId; 
				letti = gestione(conConfigurazione, pdndUtilities, false);
				if(this.lastEventId<=prec) {
					// l'ultima gestione non ha aggiornato lastEventId
					break;
				}
			}
			
			if(letti==-1) {
				throw new TimerException("Recupero eventi dalla PDND non riuscito");
			}
							
			this.msgDiag.addKeyword(CostantiPdD.KEY_OFFSET, this.lastEventId+"");
			emitDiagnosticLog("inizializzazione.effettuata");
			
			updateLasteEventId = true;
			
		}catch(Exception e) {
			this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.getMessage());
			emitDiagnosticLog("inizializzazione.fallita");
		}
		
		return updateLasteEventId;
	}
	
	private RemoteStore remoteStoreDbImage = null;
	private boolean readRemoteStoreDbImage(Connection conConfigurazione) throws KeystoreException {
		this.remoteStoreDbImage = RemoteStoreProviderDriverUtils.getRemoteStore(conConfigurazione, this.tipoDatabase, this.remoteStore, false);
		boolean created = false;
		if(this.remoteStoreDbImage==null) {
			// lo creo
			try {
				RemoteStoreProviderDriverUtils.createRemoteStore(conConfigurazione, this.tipoDatabase, this.remoteStore);
				created = true;
			}catch(KeystoreException e) {
				// un errore potrebbe essere dovuto ad un altro thread su un altro nodo che ha creato l'entry
				// provo a recuperare l'id prima di lanciare l'eccezione
				this.remoteStoreDbImage = RemoteStoreProviderDriverUtils.getRemoteStore(conConfigurazione, this.tipoDatabase, this.remoteStore, false);
				if(this.remoteStoreDbImage==null) {
					throw e;
				}
			}
			
			if(this.remoteStoreDbImage==null) {
				this.remoteStoreDbImage = RemoteStoreProviderDriverUtils.getRemoteStore(conConfigurazione, this.tipoDatabase, this.remoteStore, true); // ora dovrebbe esistere
			}
		}
		return created;
	}
	
	private String getEventPrefix(TimerGestoreChiaviPDNDEvent event) {
		return "Evento '"+event.getEventId()+"' ";
	}
	
	private int gestione(Connection conConfigurazione, TimerGestoreChiaviPDNDUtilities pdndUtilities, boolean gestioneEvento) {
		try {
			long startGenerazione = DateManager.getTimeMillis();
			
			int letti = 0;
			
			this.msgDiag.addKeyword(CostantiPdD.KEY_OFFSET, this.lastEventId+"");
			emitDiagnosticLog("gestioneEventi.inCorso");
			
			TimerGestoreChiaviPDNDEvents events = pdndUtilities.readNextEvents(this.lastEventId);
			if(events==null || events.getEvents()==null || events.getEvents().isEmpty()) {
				letti = 0;
			}
			else {
				letti = events.getEvents().size();
			}
			this.msgDiag.addKeyword(CostantiPdD.KEY_NUMERO_EVENTI, letti+"");
			emitDiagnosticLog("gestioneEventi.analisi");
			
			if(letti>0) {
				for (TimerGestoreChiaviPDNDEvent event : events.getEvents()) {					
					if(event.getEventId()>this.lastEventId) {
						this.lastEventId = event.getEventId();
						if(gestioneEvento) {
							gestioneEvento(conConfigurazione, event);
						}
					}
					else {
						emitLog(getEventPrefix(event)+"con un identificativo precedente o uguale all'ultimo evento gestito '"+this.lastEventId+"' (tipo di operazione: '"+event.getObjectType()+"'; tipo di evento: '"+event.getEventType()+"')");
					}
				}
			}
			
			long endGenerazione = DateManager.getTimeMillis();
			String tempoImpiegato = Utilities.convertSystemTimeIntoStringMillisecondi((endGenerazione-startGenerazione), true);
			this.msgDiag.addKeyword(CostantiPdD.KEY_TEMPO_GESTIONE, tempoImpiegato);
			emitDiagnosticLog("gestioneEventi.effettuata");
			
			return letti;
			
		}catch(Exception e) {
			this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.getMessage());
			emitDiagnosticLog("gestioneEventi.fallita");
			return -1;
		}
	}
	
	private void gestioneEvento(Connection conConfigurazione, TimerGestoreChiaviPDNDEvent event) throws KeystoreException, TimerException {
		if(TimerGestoreChiaviPDNDEvent.OBJECT_TYPE_KEY.equals(event.getObjectType()) &&
			(
					TimerGestoreChiaviPDNDEvent.EVENT_TYPE_DELETED.equals(event.getEventType())
					||
					TimerGestoreChiaviPDNDEvent.EVENT_TYPE_UPDATED.equals(event.getEventType())
			)
		) {
			
			this.msgDiag.addKeyword(CostantiPdD.KEY_ID_EVENTO, event.getEventId()+"");
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_EVENTO, event.getEventType());
			
			String kid = null;
			if(event.getObjectId()!=null) {
				String details = event.getObjectId().toString();
				this.msgDiag.addKeyword(CostantiPdD.KEY_DETTAGLI_EVENTO, details);
				
				kid = event.getObjectId().get("kid");
			}
			else {
				String details = "object id undefined";
				this.msgDiag.addKeyword(CostantiPdD.KEY_DETTAGLI_EVENTO, details);
			}
						
			if(kid!=null) {
				gestioneEvento(conConfigurazione, event, kid);
			}
			
			emitDiagnosticLog("gestioneEventi.evento");
		}
		else {
			if(TimerGestoreChiaviPDNDEvent.OBJECT_TYPE_KEY.equals(event.getObjectType())){
				emitLog(getEventPrefix(event)+"relativo ad una chiave; tipo di evento '"+event.getEventType()+"' ignorato");
			}
			else {
				emitLog(getEventPrefix(event)+"relativo ad un tipo di operazione '"+event.getObjectType()+"' non gestita; (tipo di evento '"+event.getEventType()+"')");
			}
		}
	}
	private void gestioneEvento(Connection conConfigurazione, TimerGestoreChiaviPDNDEvent event, String kid) throws KeystoreException, TimerException {
		Evento evento = null;
		if(TimerGestoreChiaviPDNDEvent.EVENT_TYPE_DELETED.equals(event.getEventType())) {
			int deleted = RemoteStoreProviderDriverUtils.deleteRemoteStoreKey(conConfigurazione, this.tipoDatabase, this.remoteStoreDbImage.getId(), kid);
			if(deleted>0 && this.op2Properties.isGestoreChiaviPDNDEventiDelete()) {
				// significa che la chiave era stata registrata sulla base dati
				evento = buildEvento(event.getEventType(), kid, "La chiave è stata eliminata dal repository locale");
			}
		}
		else {
			int updated = RemoteStoreProviderDriverUtils.invalidRemoteStoreKey(conConfigurazione, this.tipoDatabase, this.remoteStoreDbImage.getId(), kid);
			if(updated>0 && this.op2Properties.isGestoreChiaviPDNDEventiUpdate()) {
				// significa che la chiave era stata registrata sulla base dati
				evento = buildEvento(event.getEventType(), kid, "La chiave è stata invalidata sul repository locale");
			}
		}
		if(evento!=null) {
			try {
				GestoreEventi.getInstance().log(evento);
			}catch(Exception e) {
				String msgError = "Registrazione evento per kid '"+kid+"' (eventType:"+event.getEventType()+") non riuscita: "+e.getMessage();
				this.logCoreError(msgError,e);
				this.logTimerError(msgError,e);
			}
		}
	}
	
	private void emitDiagnosticLog(String code) {
		this.msgDiag.logPersonalizzato(code);
		String msg = this.msgDiag.getMessaggio_replaceKeywords(code);
		emitLog(msg);
	}
	private void emitLog(String msg) {
		this.logCoreInfo(msg);
		this.logTimerInfo(msg);
	}
	
	public static Evento buildEvento(String eventType, String objectDetail, String descrizione) throws TimerException{
		Evento evento = new Evento();
		evento.setTipo("GestioneChiaviPDND");
		if(TimerGestoreChiaviPDNDEvent.EVENT_TYPE_ADDED.equals(eventType)) {
			evento.setCodice(TIPO_EVENTO_ADD);
		}
		else if(TimerGestoreChiaviPDNDEvent.EVENT_TYPE_UPDATED.equals(eventType)) {
			evento.setCodice(TIPO_EVENTO_UPDATED);
		}
		else if(TimerGestoreChiaviPDNDEvent.EVENT_TYPE_DELETED.equals(eventType)) {
			evento.setCodice(TIPO_EVENTO_DELETED);
		}
		else {
			evento.setCodice(eventType);
		}
		if(objectDetail!=null){
			evento.setIdConfigurazione(objectDetail);
		}		
		evento.setDescrizione(descrizione);
		evento.setOraRegistrazione(DateManager.getDate());
		try {
			evento.setSeverita(SeveritaConverter.toIntValue(TipoSeverita.INFO));
		}catch(Exception e) {
			throw new TimerException(e.getMessage(),e);
		}
		evento.setClusterId(OpenSPCoop2Properties.getInstance().getClusterId(false));
		return evento;
	}
	
	private static final String TIPO_EVENTO_ADD = "Add";
	private static final String TIPO_EVENTO_DELETED = "Delete";
	private static final String TIPO_EVENTO_UPDATED = "Update";
}
