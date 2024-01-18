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

package org.openspcoop2.pdd.core.controllo_traffico;

import java.util.Date;

import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.controllo_traffico.constants.TipoErrore;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;

/**     
 * GestoreControlloTraffico
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreControlloTraffico {
	
	private static GestoreControlloTraffico staticInstance = null;
	public static synchronized void initialize(boolean erroreGenerico){
		if(staticInstance==null){
			staticInstance = new GestoreControlloTraffico(erroreGenerico);
		}
	}
	public static GestoreControlloTraffico getInstance() throws Exception{
		if(staticInstance==null){
			throw new Exception("GestorePolicyAttive non inizializzato");
		}
		return staticInstance;
	}
	
	
	public GestoreControlloTraffico(boolean erroreGenerico){
		this.erroreGenerico = erroreGenerico;
	}
	
	/** 
	 * Threads attivi complessivi sulla Porta
	 **/
	//private final Boolean semaphore = true; // Serve perche' senno cambiando i valori usando auto-box un-box, si perde il riferimento.
	private final org.openspcoop2.utils.Semaphore lock = new org.openspcoop2.utils.Semaphore("GestoreControlloTraffico");
	private long activeThreads = 0l;
	private boolean pddCongestionata = false;
	private boolean erroreGenerico;
	public StatoTraffico getStatoControlloTraffico(String idTransazione, boolean sync) {
		if(sync) {
			long syncActiveThreads = 0l;
			boolean syncPddCongestionata = false;
			//synchronized (this.semaphore) {
			this.lock.acquireThrowRuntime("getStatoControlloTraffico", idTransazione);
			try {
				syncActiveThreads = this.activeThreads;
				syncPddCongestionata = this.pddCongestionata;
			}finally {
				this.lock.release("getStatoControlloTraffico", idTransazione);
			}
			StatoTraffico stato = new StatoTraffico();
			stato.setActiveThreads(syncActiveThreads);
			stato.setPddCongestionata(syncPddCongestionata);
			return stato;
		}
		else {
			//Risolve problema di deadlock che scaturiva utilizzando solamente 1 connessione e facendo un test in cui più thread invocavano con più messaggi, senza avere alcuna informazione in cache
			// Si perde un pochino in precisione, ma risolve il problema del deadlock
			StatoTraffico stato = new StatoTraffico();
			stato.setActiveThreads(this.activeThreads);
			stato.setPddCongestionata(this.pddCongestionata);
			return stato;
		}
	}
	public void addThread(ServiceBinding serviceBinding, Long maxThreadsObj, Integer thresholdObj, Boolean warningOnly, PdDContext pddContext, MsgDiagnostico msgDiag, 
			TipoErrore tipoErrore, boolean includiDescrizioneErrore,Logger log) throws Exception{
		
		boolean emettiDiagnosticoMaxThreadRaggiunto = false;
		
		boolean emettiEventoMaxThreadsViolated = false;
		String descriptionEventoMaxThreadsViolated = null;
		Date dataEventoMaxThreadsViolated = null;
		
		boolean emettiEventoPddCongestionata = false;
		String descriptionEventoPddCongestionata = null;
		Date dataEventoPddCongestionata = null;
		
		try{
			long maxThreadsPrimitive = maxThreadsObj.longValue();
			int thresholdPrimitive = (thresholdObj!=null ? thresholdObj.intValue() : 0);
			String idTransazione = (pddContext!=null && pddContext.containsKey(Costanti.ID_TRANSAZIONE)) ? PdDContext.getValue(Costanti.ID_TRANSAZIONE, pddContext) : null;
			
			long activeThreadsSyncBeforeIncrement = -1;
			boolean errorSync = false;
			boolean pddCongestionataSync = false; 
			
			//synchronized (this.semaphore) {
			this.lock.acquire("addThread", idTransazione);
			try {
				activeThreadsSyncBeforeIncrement = this.activeThreads;
				//System.out.println("@@@addThread CONTROLLO ["+this.activeThreads+"]<["+maxThreads+"] ("+(!(this.activeThreads<maxThreads))+")");
				if(!(this.activeThreads<maxThreadsPrimitive)){
					errorSync = true;
				}
				if(!errorSync || warningOnly) {
					
					this.activeThreads++;
					
					if(thresholdObj!=null){
						pddCongestionataSync = this._isPddCongestionata(maxThreadsPrimitive, thresholdPrimitive); 
						
						//System.out.println("ACTIVE THREADS TOTALI: "+this.activeThreads);
						//System.out.println("PDD CONGESTIONATA: "+pddCongestionata);
						
						// verifica rispetto a variabile interna
						if(this.pddCongestionata){
							if(pddCongestionataSync==false){
								//System.out.println("@@ NON PIU' RICHIESTO");
								this.pddCongestionata = false;
							}
						}
						else{
							if(pddCongestionataSync){
								//System.out.println("@@ C.T. RICHIESTO ATTIVO");
								this.pddCongestionata = true;
								
								// Emetto un evento di congestione in corso
								emettiEventoPddCongestionata = true;
								dataEventoPddCongestionata = DateManager.getDate();
							}
						}
					}
				}
				//System.out.println("@@@addThread (dopo): "+this.activeThreads);
			}finally {
				this.lock.release("addThread", idTransazione);
			}
			
			HandlerException he = null;
			if(errorSync) {
				emettiDiagnosticoMaxThreadRaggiunto = true;
				msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_ACTIVE_THREADS, activeThreadsSyncBeforeIncrement+"");
				if(pddContext!=null) {
					pddContext.addObject(GeneratoreMessaggiErrore.PDD_CONTEXT_ACTIVE_THREADS, activeThreadsSyncBeforeIncrement);
				}
				msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_MAX_THREADS_THRESHOLD, maxThreadsPrimitive+"");
				
				//System.out.println("@@@addThread ERR");
				emettiEventoMaxThreadsViolated = true;
				descriptionEventoMaxThreadsViolated = "Superato il numero di richieste complessive ("+maxThreadsPrimitive+") gestibili dalla PdD";
				dataEventoMaxThreadsViolated = DateManager.getDate();
				
				if(pddContext!=null) {
					GeneratoreMessaggiErrore.addPddContextInfo_ControlloTrafficoMaxThreadsViolated(pddContext,warningOnly);
				}
				
				String msgDiagnostico = null;
				if(warningOnly) {
					msgDiag.getMessaggio_replaceKeywords(GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_MAXREQUESTS_VIOLATED_WARNING_ONLY);
				}
				else {
					msgDiag.getMessaggio_replaceKeywords(GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_MAXREQUESTS_VIOLATED);
				}
				he = GeneratoreMessaggiErrore.getMaxThreadsViolated(
						msgDiagnostico,
						this.erroreGenerico, pddContext
						);
				he.setEmettiDiagnostico(false);
				GeneratoreMessaggiErrore.configureHandlerExceptionByTipoErrore(serviceBinding, he, tipoErrore, includiDescrizioneErrore,log);
				if(warningOnly == false) {
					throw he;
				}
			}
			
			long activeThreadsSyncAfterIncrement = activeThreadsSyncBeforeIncrement+1;
			msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_ACTIVE_THREADS, activeThreadsSyncAfterIncrement+""); // per policy applicabilità
			
			if(thresholdObj!=null){
				// Aggiungo l'informazione se la pdd risulta congestionata nel pddContext.
				if(pddContext!=null) {
					pddContext.addObject(CostantiControlloTraffico.PDD_CONTEXT_PDD_CONGESTIONATA, pddCongestionataSync);
				}
				
				if(emettiEventoPddCongestionata) {
					descriptionEventoPddCongestionata = this._buildDescription(maxThreadsPrimitive, thresholdPrimitive, msgDiag);
				}
				
				// Il timer dovra' vedere se esiste un evento di controllo del traffico.
				// Se non esiste utilizzera' il metodo 'isControlloTrafficoAttivo' per vedere che il controllo del traffico e' rientrato.
			}
		
			if(he!=null) {
				// caso di warning only
				throw he;
			}
		}
		finally{
		
			// *** ATTIVITA DA FARE FUORI DAL SYNCHRONIZED **
						
			// fuori dal synchronized (per evitare deadlock)
			if(emettiEventoMaxThreadsViolated){
				CategoriaEventoControlloTraffico evento = null;
				if(warningOnly) {
					evento = CategoriaEventoControlloTraffico.LIMITE_GLOBALE_RICHIESTE_SIMULTANEE_WARNING_ONLY;
				}
				else {
					evento = CategoriaEventoControlloTraffico.LIMITE_GLOBALE_RICHIESTE_SIMULTANEE;
				}
				NotificatoreEventi.getInstance().log(evento, dataEventoMaxThreadsViolated, descriptionEventoMaxThreadsViolated); 
			}
			
			// fuori dal synchronized
			if(emettiDiagnosticoMaxThreadRaggiunto){
				if(warningOnly) {
					msgDiag.logPersonalizzato(GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_MAXREQUESTS_VIOLATED_WARNING_ONLY);
				}
				else {
					msgDiag.logPersonalizzato(GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_MAXREQUESTS_VIOLATED);
				}
			}
			
			// fuori dal synchronized (per evitare deadlock)
			if(emettiEventoPddCongestionata){
				NotificatoreEventi.getInstance().log(CategoriaEventoControlloTraffico.CONGESTIONE_PORTA_DOMINIO, dataEventoPddCongestionata, descriptionEventoPddCongestionata); 
			}
			
		}
	}
		
	public void removeThread(Long maxThreadsObj, Integer thresholdObj, String idTransazione) throws Exception{
		//synchronized (this.semaphore) {
		if(maxThreadsObj==null) {
			throw new Exception("MaxThreads param is null");
		}
		long maxThreadsPrimitive = maxThreadsObj.longValue();
		int thresholdPrimitive = (thresholdObj!=null ? thresholdObj.intValue() : 0);
		this.lock.acquire("removeThread", idTransazione);
		try {
			this.activeThreads--;
			
			if(thresholdObj!=null && this.pddCongestionata){
//				System.out.println("AGGORNO CONGESTIONE");
//				boolean old = this.pddCongestionata;
				this.pddCongestionata = this._isPddCongestionata(maxThreadsPrimitive, thresholdPrimitive);
//				if(old!=this.pddCongestionata){
//					System.out.println("OLD["+old+"] NEW["+this.pddCongestionata+"]");
//				}
			}
			
			//System.out.println("@@@removeThread (dopo): "+this.activeThreads);
		}finally {
			this.lock.release("removeThread", idTransazione);
		}
	}
	
	public long sizeActiveThreads(){
		//synchronized (this.semaphore) {
		this.lock.acquireThrowRuntime("sizeActiveThreads");
		try {
			//System.out.println("@@@SIZE: "+this.activeThreads);
			return this.activeThreads;
		}finally {
			this.lock.release("sizeActiveThreads");
		}
	}
	
	public Boolean isPortaDominioCongestionata(Long maxThreadsObj, Integer thresholdObj) {
		//synchronized (this.semaphore) {
		long maxThreadsPrimitive = maxThreadsObj.longValue();
		int thresholdPrimitive = (thresholdObj!=null ? thresholdObj.intValue() : 0);
		this.lock.acquireThrowRuntime("isPortaDominioCongestionata");
		try {
			if(thresholdObj!=null){
				this.pddCongestionata = this._isPddCongestionata(maxThreadsPrimitive, thresholdPrimitive); // refresh per evitare che l'ultimo thread abbia lasciato attivo il controllo
			}
			else{
				this.pddCongestionata = false; // controllo non attivo
			}
			return this.pddCongestionata;
		}finally {
			this.lock.release("isPortaDominioCongestionata");
		}
	}
	
	
	
	
	// Utilities
	
	private boolean _isPddCongestionata(long maxThreads, int threshold){
		double dActiveT = maxThreads;
		double dThreshold = threshold;
		double t = dActiveT / 100d;
		double tt = t * dThreshold;
		int numeroThreadSoglia = (int)tt;
		return this.activeThreads > numeroThreadSoglia;  // non ci vuole >=, nella govwayConsole si dice chiaramente 'Il controllo del traffico verrà attivato oltre le <numeroThreadSoglia> richieste '
	}
	private String _buildDescription(long maxThreads, int threshold, MsgDiagnostico msgDiag){
		StringBuilder bf = new StringBuilder();
		
		msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_MAX_THREADS_THRESHOLD, maxThreads+"");
		msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_CONTROLLO_TRAFFICO_THRESHOLD, threshold+"");
		bf.append(msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_ALL, GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_PDD_CONGESTIONATA));
		
		return bf.toString();
	}
	
	
}


