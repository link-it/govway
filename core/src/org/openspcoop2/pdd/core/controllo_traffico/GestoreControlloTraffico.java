/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
import java.util.concurrent.atomic.AtomicLong;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.controllo_traffico.constants.TipoErrore;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.UtilsException;
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
	public static GestoreControlloTraffico getInstance() throws CoreException{
		if(staticInstance==null){
			// spotbugs warning 'SING_SINGLETON_GETTER_NOT_SYNCHRONIZED': l'istanza viene creata allo startup
			synchronized (GestoreControlloTraffico.class) {
				throw new CoreException("GestorePolicyAttive non inizializzato");
			}
		}
		return staticInstance;
	}
	
	
	private GestoreControlloTraffico(boolean erroreGenerico){
		this.erroreGenerico = erroreGenerico;
	}
	
	/** 
	 * Threads attivi complessivi sulla Porta
	 **/
	private AtomicLong activeThreads = new AtomicLong(0l);
	private boolean erroreGenerico;
	private Long maxThreads = null;
	private Integer threshold = null;
	
	public StatoTraffico getStatoControlloTraffico() {
		long currentActiveThreads = this.activeThreads.get();
		
		StatoTraffico stato = new StatoTraffico();
		stato.setActiveThreads(currentActiveThreads);
		stato.setPddCongestionata(this.isPddCongestionata(currentActiveThreads));
		return stato;
	}
	
	
	public void addThread(ServiceBinding serviceBinding, 
			Long maxThreadsObj, Integer thresholdObj, Boolean warningOnly, 
			PdDContext pddContext, MsgDiagnostico msgDiag, TipoErrore tipoErrore, 
			boolean includiDescrizioneErrore,Logger log) throws ProtocolException, HandlerException, CoreException, UtilsException, DriverConfigurazioneException  {
		
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
			
			long activeThreadsSyncBeforeIncrement = -1;
			boolean errorSync = false;
			boolean pddCongestionataSync = false; 
			
			/**
			 * Gestione della concorrenza lock-free, la gestione dei thread massimi
			 * consentiti viene fatta usando una variabile atomica, in questo caso 
			 * l'unica situazione negativa si verifica nel caso n thread contemporaneamente
			 * prendano 
			 */
			
			/**String idTransazione = null;
			if(pddContext!=null) {
				idTransazione = (String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
			}
			System.out.println("["+idTransazione+"] PRIMA: "+this.activeThreads.get());*/
			
			// utilizzo una variabile atomica per gestire la concorrenza
			long currentActiveThreads = this.activeThreads.incrementAndGet();
			
			/**System.out.println("["+idTransazione+"] DOPO: "+currentActiveThreads);*/
			
			// nel caso l'incremento abbia superato il massimo di thread consentiti devo decrementare il contatore
			if (currentActiveThreads > maxThreadsPrimitive) {
				errorSync = true;
				
				// nel caso warningOnly posso continuare indisturbato
				if (!warningOnly.booleanValue())
					this.activeThreads.decrementAndGet();
			}
			
			// nel caso il thread sia stato correttamente aggiunto controllo la congestione
			if((!errorSync || warningOnly.booleanValue()) && thresholdObj!=null){
				boolean prePddCongestionata = this.isPddCongestionata(maxThreadsPrimitive, thresholdPrimitive, currentActiveThreads - 1);
				boolean curPddCongestionata = this.isPddCongestionata(maxThreadsPrimitive, thresholdPrimitive, currentActiveThreads);
			
				// se l'aggiunta del thread corrente ha congestionato il sistema mando il segnale
				if (!prePddCongestionata && curPddCongestionata) {
					emettiEventoPddCongestionata = true;
					dataEventoPddCongestionata = DateManager.getDate();
				}
			}
			
			
			HandlerException he = null;
			if(errorSync) {
				emettiDiagnosticoMaxThreadRaggiunto = true;
				msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_ACTIVE_THREADS, activeThreadsSyncBeforeIncrement+"");
				if(pddContext!=null) {
					pddContext.addObject(GeneratoreMessaggiErrore.PDD_CONTEXT_ACTIVE_THREADS, activeThreadsSyncBeforeIncrement);
				}
				msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_MAX_THREADS_THRESHOLD, maxThreadsPrimitive+"");
				
				emettiEventoMaxThreadsViolated = true;
				descriptionEventoMaxThreadsViolated = "Superato il numero di richieste complessive ("+maxThreadsPrimitive+") gestibili dalla PdD";
				dataEventoMaxThreadsViolated = DateManager.getDate();
				
				if(pddContext!=null) {
					GeneratoreMessaggiErrore.addPddContextInfoControlloTrafficoMaxThreadsViolated(pddContext,warningOnly);
				}
				
				String msgDiagnostico = null;
				if(warningOnly.booleanValue()) {
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
				if(!warningOnly.booleanValue()) {
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
					descriptionEventoPddCongestionata = this.buildDescription(maxThreadsPrimitive, thresholdPrimitive, msgDiag);
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
			
			if(emettiEventoMaxThreadsViolated){
				CategoriaEventoControlloTraffico evento = null;
				if(warningOnly.booleanValue()) {
					evento = CategoriaEventoControlloTraffico.LIMITE_GLOBALE_RICHIESTE_SIMULTANEE_WARNING_ONLY;
				}
				else {
					evento = CategoriaEventoControlloTraffico.LIMITE_GLOBALE_RICHIESTE_SIMULTANEE;
				}
				NotificatoreEventi.getInstance().log(evento, dataEventoMaxThreadsViolated, descriptionEventoMaxThreadsViolated); 
			}
			
			// fuori dal synchronized
			if(emettiDiagnosticoMaxThreadRaggiunto){
				if(warningOnly.booleanValue()) {
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
		
	public void removeThread() {		
		this.activeThreads.decrementAndGet();
	}
	
	public long sizeActiveThreads(){
			return this.activeThreads.get();
	}
	
	public Boolean isPortaDominioCongestionata(Long maxThreadsObj, Integer thresholdObj) {
		return this.isPddCongestionata(maxThreadsObj, thresholdObj, this.activeThreads.get());
	}
	
	
	
	
	// Utilities
	
	/**
	 * Restituisce l'ultimo valore calcolato di PddCongestionata
	 * @param activeThreads: numero di thread attualmente attivi
	 * @return
	 */
	private boolean isPddCongestionata(long activeThreads) {
		return this.isPddCongestionata(this.maxThreads, this.threshold, activeThreads);
	}
	
	/**
	 * Restituisce se la Pdd risulta congestionata
	 * @param maxThreads: numero massimo di thread attivi
	 * @param threshold: valore percentuale sul numero di thread massimo oltre il quale
	 * la pdd risulta congestionata (null se non attivo)
	 * @param activeThreads: numero di thread attivi
	 * @return true se il controllo della congestione della pdd é abilitato e risulta 
	 * congestionata
	 */
	private boolean isPddCongestionata(Long maxThreads, Integer threshold, long activeThreads){
		// mi salvo i valori di maxThreads e threshold in caso volessi restituire l'ultimo valore calcolato
		this.maxThreads = maxThreads;
		this.threshold = threshold;
		if (threshold == null || maxThreads == null)
			return false;
		double dActiveT = maxThreads;
		double dThreshold = threshold;
		double t = dActiveT / 100d;
		double tt = t * dThreshold;
		int numeroThreadSoglia = (int)tt;
		return activeThreads > numeroThreadSoglia;  // non ci vuole >=, nella govwayConsole si dice chiaramente 'Il controllo del traffico verrà attivato oltre le <numeroThreadSoglia> richieste '
	}
	
	private String buildDescription(long maxThreads, int threshold, MsgDiagnostico msgDiag){
		StringBuilder bf = new StringBuilder();
		
		msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_MAX_THREADS_THRESHOLD, maxThreads+"");
		msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_CONTROLLO_TRAFFICO_THRESHOLD, threshold+"");
		bf.append(msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_ALL, GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_PDD_CONGESTIONATA));
		
		return bf.toString();
	}
	
	
}


