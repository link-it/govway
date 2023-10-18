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


import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.pdd.config.DBTransazioniManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.core.controllo_traffico.NotificatoreEventi;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.GestorePolicyAttive;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.threads.BaseThread;
import org.slf4j.Logger;


/**     
 * TimerEventiThread
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimerEventiThread extends BaseThread{

	private static TimerState STATE = TimerState.OFF; // abilitato in OpenSPCoop2Startup al momento dell'avvio
	
	public static TimerState getSTATE() {
		return STATE;
	}
	public static void setSTATE(TimerState sTATE) {
		STATE = sTATE;
	}

	public static final String ID_MODULO = "TimerEventi";
		
	/** Logger utilizzato per debug. */
	private Logger log = null;
	
	/** Indicazione se deve essere effettuato il log delle query */
	private boolean debug = false;	
	
	
	private OpenSPCoop2Properties properties;
	
	/** NotificatoreEventi */
	private NotificatoreEventi notificatoreEventi = null;
	
	/** LastInterval */
	private Date lastInterval;
	
	/** ConnectionTimeout */
	private int checkConnectionTimeoutEveryXTimes = 1;
	private int offsetConnectionTimeoutEveryXTimes = 0;
	private Date lastIntervalConnectionTimeout;
	
	/** RequestReadTimeout */
	private int checkRequestReadTimeoutEveryXTimes = 1;
	private int offsetRequestReadTimeoutEveryXTimes = 0;
	private Date lastIntervalRequestReadTimeout;
	
	private int checkReadTimeoutEveryXTimes = 1;
	private int offsetReadTimeoutEveryXTimes = 0;
	private Date lastIntervalReadTimeout;
	
	/** Immagine */
	@SuppressWarnings("unused")
	private boolean forceCheckPrimoAvvio = false;
	
	private static boolean inizializzazioneAttiva = false;
	public static boolean isInizializzazioneAttiva() {
		return inizializzazioneAttiva;
	}
	public static void setInizializzazioneAttiva(boolean inizializzazioneAttiva) {
		TimerEventiThread.inizializzazioneAttiva = inizializzazioneAttiva;
	}
	
	/** Costruttore */
	@SuppressWarnings("deprecation")
	public TimerEventiThread(Logger log) throws Exception{
	
		this.log = log;
	
		this.properties = OpenSPCoop2Properties.getInstance();
		this.setTimeout(this.properties.getEventiTimerIntervalSeconds());
		
		this.checkConnectionTimeoutEveryXTimes = this.properties.getEventiTimerIntervalConnectionTimeoutEveryXTimes();
		this.checkRequestReadTimeoutEveryXTimes = this.properties.getEventiTimerIntervalRequestReadTimeoutEveryXTimes();
		this.checkReadTimeoutEveryXTimes = this.properties.getEventiTimerIntervalReadTimeoutEveryXTimes();
		
		// Eventi per Controllo Traffico
		if(this.properties.isControlloTrafficoEnabled()){
			this.notificatoreEventi = NotificatoreEventi.getInstance();
			
			// Il meccanismo di ripristino dell'immagine degli eventi non sembra funzionare
			// Lascio comunque il codice se in futuro si desidera approfindire la questione
			if(inizializzazioneAttiva) {
				List<PolicyGroupByActiveThreadsType> tipiGestorePolicyRateLimiting = null;
				try{
					tipiGestorePolicyRateLimiting = GestorePolicyAttive.getTipiGestoriAttivi();
				}catch(Throwable e){
					this.log.error("Errore durante l'inizializzazione dell'immagine degli eventi per il Controllo del Traffico: "+e.getMessage(),e);
				}
				if(tipiGestorePolicyRateLimiting!=null && !tipiGestorePolicyRateLimiting.isEmpty()) {
					for (PolicyGroupByActiveThreadsType type : tipiGestorePolicyRateLimiting) {
						File fDati = null;
						try{
							File fRepository = this.properties.getControlloTrafficoGestorePolicyFileSystemRecoveryRepository();
							if(fRepository!=null){
								if(fRepository.exists()==false){
									throw new Exception("Directory ["+fRepository.getAbsolutePath()+"] not exists");
								}
								if(fRepository.isDirectory()==false){
									throw new Exception("File ["+fRepository.getAbsolutePath()+"] is not directory");
								}
								if(fRepository.canRead()==false){
									throw new Exception("File ["+fRepository.getAbsolutePath()+"] cannot read");
								}
								if(fRepository.canWrite()==false){
									throw new Exception("File ["+fRepository.getAbsolutePath()+"] cannot write");
								}
								fDati = new File(fRepository, GestorePolicyAttive.getControlloTrafficoEventiImage(type));
								if(fDati.exists() && fDati.canRead() && fDati.length()>0){
									FileInputStream fin = new FileInputStream(fDati);
									this.notificatoreEventi.initialize(fin);
									if(!fDati.delete()) {
										// ignore
									}
									this.forceCheckPrimoAvvio = true;
								}
							}
						}catch(Exception e){
							String img = null;
							if(fDati!=null){
								img = fDati.getAbsolutePath();
							}
							throw new HandlerException("Inizializzazione dell'immagine degli eventi ["+img+"] per il Controllo del Traffico non riuscita: "+e.getMessage(),e);
						}
					}
				}
			}
		}
		
		this.lastInterval = DateManager.getDate();
		this.lastIntervalConnectionTimeout = DateManager.getDate();
		this.lastIntervalRequestReadTimeout = DateManager.getDate();
		this.lastIntervalReadTimeout = DateManager.getDate();
		
		this.debug = this.properties.isEventiDebug();

	}
	
	
	@Override
	public void process(){
	
		if(TimerState.ENABLED.equals(STATE)) {
		
			DBTransazioniManager dbManager = null;
	    	Resource r = null;
	    	try{
	    		dbManager = DBTransazioniManager.getInstance();
				r = dbManager.getResource(this.properties.getIdentitaPortaDefaultWithoutProtocol(), ID_MODULO, null);
				if(r==null){
					throw new UtilsException("Risorsa al database non disponibile");
				}
				Connection con = (Connection) r.getResource();
				if(con == null)
					throw new UtilsException("Connessione non disponibile");	
				
				if(this.properties.isControlloTrafficoEnabled()){
					
					try{
						this.lastInterval = this.notificatoreEventi.process(this.log, this.getTimeout(), this.lastInterval, con, this.debug);
					}catch(Exception e){
						this.log.error("Errore durante la generazione degli eventi per il controllo del traffico: "+e.getMessage(),e);
					}
					
					// Comprensione offset per analisi connection timeout events
					try{
						boolean analyzeConnectionTimeout = isAnalyzeConnectionTimeout();
						if(analyzeConnectionTimeout) {
							this.lastIntervalConnectionTimeout = this.notificatoreEventi.processConnectionTimeout(this.log, (this.getTimeout()*this.checkConnectionTimeoutEveryXTimes), this.lastIntervalConnectionTimeout, con, this.debug);
						}
						else {
							this.notificatoreEventi.emitProcessConnectionTimeoutSkip(this.log, this.debug, this.offsetConnectionTimeoutEveryXTimes, this.checkConnectionTimeoutEveryXTimes);
						}
					}catch(Exception e){
						this.log.error("Errore durante la generazione degli eventi di connection timeout: "+e.getMessage(),e);
					}
					
					// Comprensione offset per analisi request read timeout events
					try {
						boolean analyzeRequestReadTimeout = isAnalyzeRequestReadTimeout();
						if(analyzeRequestReadTimeout) {
							this.lastIntervalRequestReadTimeout = this.notificatoreEventi.processRequestReadTimeout(this.log, (this.getTimeout()*this.checkRequestReadTimeoutEveryXTimes), this.lastIntervalRequestReadTimeout, con, this.debug);
						}
						else {
							this.notificatoreEventi.emitProcessRequestReadTimeoutSkip(this.log, this.debug, this.offsetRequestReadTimeoutEveryXTimes, this.checkRequestReadTimeoutEveryXTimes);
						}
					}catch(Exception e){
						this.log.error("Errore durante la generazione degli eventi di request read timeout: "+e.getMessage(),e);
					}
					
					// Comprensione offset per analisi read timeout events
					try {
						boolean analyzeReadTimeout = isAnalyzeReadTimeout();
						if(analyzeReadTimeout) {
							this.lastIntervalReadTimeout = this.notificatoreEventi.processReadTimeout(this.log, (this.getTimeout()*this.checkReadTimeoutEveryXTimes), this.lastIntervalReadTimeout, con, this.debug);
						}
						else {
							this.notificatoreEventi.emitProcessReadTimeoutSkip(this.log, this.debug, this.offsetReadTimeoutEveryXTimes, this.checkReadTimeoutEveryXTimes);
						}
					}catch(Exception e){
						this.log.error("Errore durante la generazione degli eventi di request read timeout: "+e.getMessage(),e);
					}
					
				}
								
				// Aggiungere in futuro altre gestione degli eventi
				
			}catch(Exception e){
				this.log.error("Errore durante la generazione degli eventi: "+e.getMessage(),e);
			}finally{
				try{
					if(r!=null)
						dbManager.releaseResource(this.properties.getIdentitaPortaDefaultWithoutProtocol(), ID_MODULO, r);
				}catch(Exception eClose){
					// ignore
				}
			}
	    	
		}
		else {
			this.log.info("Timer "+ID_MODULO+" disabilitato");
		}
				
	}
	
	@Override
	public void close(){
		this.log.info("Thread per la generazione degli eventi terminato");
	}
			
	private boolean isAnalyzeConnectionTimeout() {
		this.offsetConnectionTimeoutEveryXTimes++;
		boolean esito = false;
		if(this.offsetConnectionTimeoutEveryXTimes==this.checkConnectionTimeoutEveryXTimes) {
			/**System.out.println("CONNECTION TIMEOUT CHECK '"+this.offsetConnectionTimeoutEveryXTimes+"'=='"+this.checkConnectionTimeoutEveryXTimes+"' TRUE");*/
			esito = true;
			this.offsetConnectionTimeoutEveryXTimes = 0;
		}
		/**else {
			System.out.println("CONNECTION TIMEOUT CHECK '"+this.offsetConnectionTimeoutEveryXTimes+"'<>'"+this.checkConnectionTimeoutEveryXTimes+"' FALSE");
		}*/
		return esito;
	}
	private boolean isAnalyzeRequestReadTimeout() {
		this.offsetRequestReadTimeoutEveryXTimes++;
		boolean esito = false;
		if(this.offsetRequestReadTimeoutEveryXTimes==this.checkRequestReadTimeoutEveryXTimes) {
			/**System.out.println("REQUEST READ TIMEOUT CHECK '"+this.offsetRequestReadTimeoutEveryXTimes+"'=='"+this.checkRequestReadTimeoutEveryXTimes+"' TRUE");*/
			esito = true;
			this.offsetRequestReadTimeoutEveryXTimes = 0;
		}
		/**else {
			System.out.println("REQUEST READ TIMEOUT CHECK '"+this.offsetRequestReadTimeoutEveryXTimes+"'<>'"+this.checkRequestReadTimeoutEveryXTimes+"' FALSE");
		}*/
		return esito;
	}
	private boolean isAnalyzeReadTimeout() {
		this.offsetReadTimeoutEveryXTimes++;
		boolean esito = false;
		if(this.offsetReadTimeoutEveryXTimes==this.checkReadTimeoutEveryXTimes) {
			/**System.out.println("READ TIMEOUT CHECK '"+this.offsetReadTimeoutEveryXTimes+"'=='"+this.checkReadTimeoutEveryXTimes+"' TRUE");*/
			esito = true;
			this.offsetReadTimeoutEveryXTimes = 0;
		}
		else {
			/**System.out.println("READ TIMEOUT CHECK '"+this.offsetReadTimeoutEveryXTimes+"'<>'"+this.checkReadTimeoutEveryXTimes+"' FALSE");*/
		}
		return esito;
	}
}
