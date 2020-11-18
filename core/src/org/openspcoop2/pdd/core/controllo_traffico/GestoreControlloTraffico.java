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

package org.openspcoop2.pdd.core.controllo_traffico;

import java.util.Date;

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
	private final Boolean semaphore = true; // Serve perche' senno cambiando i valori usando auto-box un-box, si perde il riferimento.
	private Long activeThreads = 0l;
	private Boolean pddCongestionata = false;
	private boolean erroreGenerico;
	public StatoTraffico getStatoControlloTraffico() {
		synchronized (this.semaphore) {	
			StatoTraffico stato = new StatoTraffico();
			stato.setActiveThreads(Long.valueOf(this.activeThreads));
			stato.setPddCongestionata(Boolean.valueOf(this.pddCongestionata));
			return stato;
		}
	}
	public void addThread(ServiceBinding serviceBinding, Long maxThreads, Integer threshold, Boolean warningOnly, PdDContext pddContext, MsgDiagnostico msgDiag, 
			TipoErrore tipoErrore, boolean includiDescrizioneErrore,Logger log) throws Exception{
		
		boolean emettiDiagnosticoMaxThreadRaggiunto = false;
		
		boolean emettiEventoMaxThreadsViolated = false;
		String descriptionEventoMaxThreadsViolated = null;
		Date dataEventoMaxThreadsViolated = null;
		
		boolean emettiEventoPddCongestionata = false;
		String descriptionEventoPddCongestionata = null;
		Date dataEventoPddCongestionata = null;
		
		try{
			synchronized (this.semaphore) {		
				
				//System.out.println("@@@addThread CONTROLLO ["+this.activeThreads+"]<["+maxThreads+"] ("+(!(this.activeThreads<maxThreads))+")");
				HandlerException he = null;
				if(!(this.activeThreads<maxThreads)){
					
					emettiDiagnosticoMaxThreadRaggiunto = true;
					msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_ACTIVE_THREADS, this.activeThreads+"");
					pddContext.addObject(GeneratoreMessaggiErrore.TEMPLATE_ACTIVE_THREADS, this.activeThreads);
					msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_MAX_THREADS_THRESHOLD, maxThreads+"");
					
					//System.out.println("@@@addThread ERR");
					emettiEventoMaxThreadsViolated = true;
					descriptionEventoMaxThreadsViolated = "Superato il numero di richieste complessive ("+maxThreads+") gestibili dalla PdD";
					dataEventoMaxThreadsViolated = DateManager.getDate();
					
					GeneratoreMessaggiErrore.addPddContextInfo_ControlloTrafficoMaxThreadsViolated(pddContext,warningOnly);
					
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
				this.activeThreads++;
				msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_ACTIVE_THREADS, this.activeThreads+""); // per policy applicabilità
				
				if(threshold!=null){
					boolean pddCongestionata = this._isPddCongestionata(maxThreads, threshold); 
					
					//System.out.println("ACTIVE THREADS TOTALI: "+this.activeThreads);
					//System.out.println("PDD CONGESTIONATA: "+pddCongestionata);
										
					// Aggiungo l'informazione se la pdd risulta congestionata nel pddContext.
					pddContext.addObject(CostantiControlloTraffico.PDD_CONTEXT_PDD_CONGESTIONATA, pddCongestionata);
					
					// Inoltre
					if(this.pddCongestionata){
						if(pddCongestionata==false){
							//System.out.println("@@ NON PIU' RICHIESTO");
							this.pddCongestionata = false;
						}
					}
					else{
						if(pddCongestionata){
							//System.out.println("@@ C.T. RICHIESTO ATTIVO");
							this.pddCongestionata = true;
							
							// Emetto un evento di congestione in corso
							emettiEventoPddCongestionata = true;
							descriptionEventoPddCongestionata = this._buildDescription(maxThreads, threshold, msgDiag);
							dataEventoPddCongestionata = DateManager.getDate();
						}
					}
					
					// Il timer dovra' vedere se esiste un evento di controllo del traffico.
					// Se non esiste utilizzera' il metodo 'isControlloTrafficoAttivo' per vedere che il controllo del traffico e' rientrato.
				}
				
				if(he!=null) {
					// caso di warning only
					throw he;
				}
				
				//System.out.println("@@@addThread (dopo): "+this.activeThreads);
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
		
	public void removeThread(Long maxThreads, Integer threshold) throws Exception{
		synchronized (this.semaphore) {			
			this.activeThreads--;
			
			if(threshold!=null && this.pddCongestionata){
//				System.out.println("AGGORNO CONGESTIONE");
//				boolean old = this.pddCongestionata;
				this.pddCongestionata = this._isPddCongestionata(maxThreads, threshold);
//				if(old!=this.pddCongestionata){
//					System.out.println("OLD["+old+"] NEW["+this.pddCongestionata+"]");
//				}
			}
			
			//System.out.println("@@@removeThread (dopo): "+this.activeThreads);
		}
	}
	
	public long sizeActiveThreads(){
		synchronized (this.semaphore) {
			//System.out.println("@@@SIZE: "+this.activeThreads);
			return this.activeThreads;
		}
	}
	
	public Boolean isPortaDominioCongestionata(Long maxThreads, Integer threshold) {
		synchronized (this.semaphore) {
			if(threshold!=null){
				this.pddCongestionata = this._isPddCongestionata(maxThreads, threshold); // refresh per evitare che l'ultimo thread abbia lasciato attivo il controllo
			}
			else{
				this.pddCongestionata = false; // controllo non attivo
			}
			return this.pddCongestionata;
		}
	}
	
	
	
	
	// Utilities
	
	private boolean _isPddCongestionata(Long maxThreads, Integer threshold){
		double dActiveT = maxThreads.doubleValue();
		double dThreshold = threshold.doubleValue();
		double t = dActiveT / 100d;
		Double tt = t * dThreshold;
		int numeroThreadSoglia = tt.intValue();
		return this.activeThreads > numeroThreadSoglia;  // non ci vuole >=, nella govwayConsole si dice chiaramente 'Il controllo del traffico verrà attivato oltre le <numeroThreadSoglia> richieste '
	}
	private String _buildDescription(Long maxThreads, Integer threshold, MsgDiagnostico msgDiag){
		StringBuilder bf = new StringBuilder();
		
		msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_MAX_THREADS_THRESHOLD, maxThreads.toString());
		msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_CONTROLLO_TRAFFICO_THRESHOLD, threshold.toString());
		bf.append(msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_ALL, GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_PDD_CONGESTIONATA));
		
		return bf.toString();
	}
	
	
}


