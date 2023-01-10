/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import java.util.Date;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.ejb.TimerService;

import org.slf4j.Logger;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.utils.Semaphore;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;

/**
 * Implementazione dell'interfaccia {@link TimerGestorePuliziaMessaggiAnomali} del Gestore
 * dei threads di servizio di OpenSPCoop.
 * 
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class TimerGestorePuliziaMessaggiAnomaliImpl implements SessionBean, TimedObject {

	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/* ******** F I E L D S P R I V A T I ******** */

	/** Contesto */
	private SessionContext sessionContext;
	/** Timer associato a questo EJB */
	private Timer timer;
	/**
	 * Timeout che definisce la cadenza di avvio di questo timer. 
	 */
	private long timeout = 10; // ogni 10 secondi avvio il Thread
	/** Properties Reader */
	private OpenSPCoop2Properties propertiesReader;
	/** MsgDiagnostico */
	private MsgDiagnostico msgDiag;
	
	/** Logger utilizzato per debug. */
	private Logger logTimer = null;

	/** Indicazione se l'istanza in questione e' autoDeployata da JBoss o creata da OpenSPCoop */
	private boolean deployFromOpenSPCoop = false;
	/** Indicazione se la gestione e' attualmente in esecuzione */
	private boolean gestioneAttiva = false;
	/** Indicazione se deve essere effettuato il log delle query */
	private boolean logQuery = false;
	/** Numero di messaggi prelevati sulla singola query */
	private int limit = CostantiPdD.LIMIT_MESSAGGI_GESTORI;
	/** Indicazione se deve essere effettuato l'order by nelle query */
	private boolean orderByQuery = false;





	/* ********  M E T O D I   ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader al primo
	 * utilizzo del thread che implementa l'EJBean definito in questa classe.
	 * 
	 * 
	 */
	public void ejbCreate() throws CreateException {

		
		// Aspetto inizializzazione di OpenSPCoop (aspetto mezzo minuto e poi segnalo errore)
		int attesa = 90;
		int secondi = 0;
		while( (OpenSPCoop2Startup.initialize==false) && (secondi<attesa) ){
			Utilities.sleep(1000);
			secondi++;
		}
		if(secondi>= 90){
			throw new CreateException("Riscontrata inizializzazione OpenSPCoop non effettuata");
		}   

		this.logTimer = OpenSPCoop2Logger.getLoggerOpenSPCoopTimers();
		
		try {
			this.msgDiag = MsgDiagnostico.newInstance(TimerGestorePuliziaMessaggiAnomali.ID_MODULO);
			this.msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TIMER_GESTORE_MESSAGGI_INCONSISTENTI);
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_GESTORE_MESSAGGI_INCONSISTENTI, TimerGestorePuliziaMessaggiAnomali.ID_MODULO);
		} catch (Exception e) {
			String msgErrore = "Riscontrato Errore durante l'inizializzazione del MsgDiagnostico: "+e.getMessage();
			this.logTimer.error(msgErrore,e);
			throw new CreateException(msgErrore);
		}

		try {
			this.propertiesReader = OpenSPCoop2Properties.getInstance();
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"InizializzazioneProperties");
			String msgErrore = "Riscontrato errore durante l'inizializzazione del Reader delle Properties di OpenSPCoop: "+e.getMessage();
			this.logTimer.error(msgErrore,e);
			throw new CreateException(msgErrore);
		}

		this.timeout = this.propertiesReader.getRepositoryIntervalloEliminazioneMessaggi();
		String s = "secondi";
		if(this.timeout == 1)
			s = "secondo";
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIMEOUT, this.timeout+" "+s);
		
		this.logQuery = this.propertiesReader.isTimerGestorePuliziaMessaggiAnomaliAbilitatoLog();
		this.orderByQuery = this.propertiesReader.isTimerGestorePuliziaMessaggiAnomaliAbilitatoOrderBy();
		
		this.limit = this.propertiesReader.getTimerGestorePuliziaMessaggiAnomaliLimit();
		if(this.limit<=0){
			this.limit = CostantiPdD.LIMIT_MESSAGGI_GESTORI;
		}
		this.msgDiag.addKeyword(CostantiPdD.KEY_LIMIT, this.limit+"");
	}

	/**
	 * Metodo necessario per l'implementazione dell'interfaccia
	 * <code>SessionBean</code>.
	 * 
	 * 
	 */
	@Override
	public void ejbRemove() throws EJBException {
	}

	/**
	 * Metodo necessario per l'implementazione dell'interfaccia
	 * <code>SessionBean</code>.
	 * 
	 * 
	 */
	@Override
	public void ejbActivate() throws EJBException {
	}

	/**
	 * Metodo necessario per l'implementazione dell'interfaccia
	 * <code>SessionBean</code>.
	 * 
	 * 
	 */
	@Override
	public void ejbPassivate() throws EJBException {
	}

	private static Semaphore SEMAPHORE = new Semaphore(TimerGestorePuliziaMessaggiAnomali.ID_MODULO);
	private static Boolean LOCK = false;
	
	/**
	 * Metodo necessario per l'implementazione dell'interfaccia
	 * <code>TimedObject</code>.
	 * 
	 * 
	 */
	@Override
	public void ejbTimeout(Timer timer) throws EJBException {

		
		// Nelle nuove versioni, jboss avviava automaticamente un EJB Timer registrato. Questo comportamento, insieme 
		// al codice che avviava manualmente il timer in versione thread provocava un doppio avvio.
		if(this.propertiesReader.isServerJ2EE()==false){
			//System.out.println("GestoreMessaggiPulizia STOPPED");
			stop(timer);
			return;
		}
		
		
		// Solo il thread avviato da OpenSPCoop deve essere eseguito
		if( (this.deployFromOpenSPCoop == false)){
			if(this.propertiesReader.isTimerAutoStart_StopTimer()){
				stop(timer);
				return;
			}else{
				// Viene sempre richiamato ejbCreate() e quindi la variabile deployFromOpenSPCoop è sempre null.
				// La single instance viene gestiti quindi con un lock
				SEMAPHORE.acquireThrowRuntime("ejbTimeout");
				try {
					if(LOCK){
						this.msgDiag.logPersonalizzato("precedenteEsecuzioneInCorso.stopTimer");
						this.logTimer.info(this.msgDiag.getMessaggio_replaceKeywords("precedenteEsecuzioneInCorso.stopTimer"));
						stop(timer);
						return;
					}
					else{
						LOCK = true;
						
						/**
						 * Aggiungo una sleep di 5 secondi per far provocare il LOCK sopra presente, per le altre istanze di timer
						 * in modo da avere solamente una istanza in esecuzione
						 */
						for (int i = 0; i < 10; i++) {
							Utilities.sleep(500);
						}
					}
				}finally {
					SEMAPHORE.release("ejbTimeout");
				}
			}
		}

		try{
			
			// Controllo se OpenSPCoop desidera avviare il Timer
			if(this.propertiesReader.isTimerGestorePuliziaMessaggiAnomaliAbilitato()==false){
				this.msgDiag.logPersonalizzato("disabilitato");
				this.logTimer.warn(this.msgDiag.getMessaggio_replaceKeywords("disabilitato"));
				stop(timer);
				return;
			}
	
			// Controllo che l'inizializzazione corretta delle risorse sia effettuata
			if(timer == null){
				String msgErrore = "inizializzazione del Timer non effettuata";
				this.msgDiag.logFatalError(msgErrore, "Check inizializzazione");
				this.logTimer.error(msgErrore);
				stop(timer);
				return;
			}
			if(OpenSPCoop2Startup.initialize==false){
				String msgErrore = "inizializzazione di OpenSPCoop non effettuata";
				this.msgDiag.logFatalError(msgErrore, "Check inizializzazione");
				this.logTimer.error(msgErrore);
				stop(timer);
				return;
			}
			
			if(this.gestioneAttiva){
				this.msgDiag.logPersonalizzato("precedenteEsecuzioneInCorso");
				this.logTimer.info(this.msgDiag.getMessaggio_replaceKeywords("precedenteEsecuzioneInCorso"));
				return;
			}
	
			try{
				// Prendo la gestione
				this.gestioneAttiva = true;
				TimerGestorePuliziaMessaggiAnomaliLib gestoreMessaggiLib = 
					new TimerGestorePuliziaMessaggiAnomaliLib(this.msgDiag,this.logTimer,this.propertiesReader,this.logQuery,this.limit,this.orderByQuery);
				
				gestoreMessaggiLib.check();
				
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e,"GestorePuliziaMessaggiTimerEJB");
				this.logTimer.error("Errore generale: "+e.getMessage(),e);
			}finally{
				// Rilascio la gestione
				this.gestioneAttiva = false;
			}
						
		}finally{
			SEMAPHORE.acquireThrowRuntime("ejbTimeoutCheck2");
			try {
				LOCK = false;
			}finally {
				SEMAPHORE.release("ejbTimeoutCheck2");
			}	
		}
		
	}

	/**
	 * Imposta il Contesto del Session Bean. Metodo necessario per
	 * l'implementazione dell'interfaccia <code>SessionBean</code>.
	 * 
	 * @param aContext
	 *            Contesto del Session Bean.
	 * 
	 */
	@Override
	public void setSessionContext(SessionContext aContext) throws EJBException {
		this.sessionContext = aContext;
	}

	/**
	 * Inizializza il Timer di gestione
	 * 
	 * 
	 */
	public boolean start() throws EJBException {
		if( this.timer != null ){

			this.msgDiag.logPersonalizzato("timerGiaAvviato");
			this.logTimer.info(this.msgDiag.getMessaggio_replaceKeywords("timerGiaAvviato"));
			return false;

		} else {

			if(this.propertiesReader.isTimerGestorePuliziaMessaggiAnomaliAbilitato()){
				
				this.deployFromOpenSPCoop = true;	    
				this.msgDiag.logPersonalizzato("avvioInCorso");
				this.logTimer.info(this.msgDiag.getMessaggio_replaceKeywords("avvioInCorso"));
				Utilities.sleep(1000); // tempo necessario per un corretto avvio in JBoss 4.0.3...
				Date now = DateManager.getDate();
				long timeout = 1000 * this.timeout;
				try {
					// creo il timer
					TimerService ts = this.sessionContext.getTimerService();
					this.timer = ts.createTimer(now, timeout, TimerGestorePuliziaMessaggiAnomali.ID_MODULO);
					this.msgDiag.logPersonalizzato("avvioEffettuato");
					this.logTimer.info(this.msgDiag.getMessaggio_replaceKeywords("avvioEffettuato"));
				} catch (Exception e) {
					stop();
					this.msgDiag.logFatalError(e, "Creazione timer EJB gestore della pulizia dei messaggi non consistenti");
					this.logTimer.error("Errore durante la creazione del timer: "+e.getMessage(),e);
				}
				return this.timer != null;
		
			}else{
				this.msgDiag.logPersonalizzato("disabilitato");
				this.logTimer.warn(this.msgDiag.getMessaggio_replaceKeywords("disabilitato"));
				return false;
			}
		}
	}

	/**
	 * Restituisce lo stato del timer di gestione
	 * 
	 * 
	 */
	public boolean isStarted() throws EJBException {
		return this.timer != null;
	}

	/**
	 * Annulla il timer di gestione
	 * 
	 * 
	 */
	public void stop(Timer atimer) throws EJBException {
		if (atimer != null) {
			atimer.cancel();
		}
	}

	/**
	 * Annulla il timer di gestione interno
	 * 
	 * 
	 */
	public void stop() throws EJBException {
		if (this.timer != null) {
			this.timer.cancel();
			this.timer = null;
		}
	}

}
