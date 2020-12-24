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
package org.openspcoop2.pdd.core.controllo_traffico.policy;

import java.util.Date;
import java.util.List;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.DatiTransazione;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.RisultatoStatistico;
import org.openspcoop2.core.controllo_traffico.beans.RisultatoStato;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.controllo_traffico.constants.TipoApplicabilita;
import org.openspcoop2.core.controllo_traffico.constants.TipoControlloPeriodo;
import org.openspcoop2.core.controllo_traffico.constants.TipoFinestra;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsa;
import org.openspcoop2.core.controllo_traffico.driver.IGestorePolicyAttive;
import org.openspcoop2.core.controllo_traffico.utils.PolicyUtilities;
import org.openspcoop2.core.eventi.constants.CodiceEventoControlloTraffico;
import org.openspcoop2.core.eventi.constants.TipoEvento;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.controllo_traffico.CategoriaEventoControlloTraffico;
import org.openspcoop2.pdd.core.controllo_traffico.ConfigurazioneControlloTraffico;
import org.openspcoop2.pdd.core.controllo_traffico.DatiTempiRisposta;
import org.openspcoop2.pdd.core.controllo_traffico.GeneratoreMessaggiErrore;
import org.openspcoop2.pdd.core.controllo_traffico.NotificatoreEventi;
import org.openspcoop2.pdd.core.controllo_traffico.RisultatoVerificaPolicy;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.utils.PorteNamingUtils;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;

/**     
 * PolicyVerifier
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PolicyVerifier {

	
	public static RisultatoVerificaPolicy verifica(
			ConfigurazionePdDManager configPdDManager, IProtocolFactory<?> protocolFactory,
			IGestorePolicyAttive gestorePolicyAttive, Logger logCC,
			ActivePolicy activePolicy,
			IDUnivocoGroupByPolicy datiGroupBy, PdDContext pddContext,
			MsgDiagnostico msgDiag, 
			Transaction tr,
			DatiTransazione datiTransazione, boolean isPddCongestionata, DatiTempiRisposta tempiRisposta,
			List<Boolean> pddContext_policyApplicabile, List<Boolean> pddContext_policyViolata,
			IState state) throws Exception{
				
		// Tutti i restanti controlli sono effettuati usando il valore di datiCollezionatiReaded, che e' gia' stato modificato
		// Inoltre e' stato re-inserito nella map come oggetto nuovo, quindi il valore dentro il metodo non subira' trasformazioni (essendo stato fatto il clone)
		// E' possibile procedere con l'analisi rispetto al valore che possiedono il counter dentro questo scope.
		
		boolean policyAPI = activePolicy.getInstanceConfiguration().getFiltro()!=null && 
				activePolicy.getInstanceConfiguration().getFiltro().getNomePorta()!=null &&
				!"".equals(activePolicy.getInstanceConfiguration().getFiltro().getNomePorta());
		boolean policyGlobale = !policyAPI;
		
		// Registro Threads (NOTA: non i contatori, quelli vegono aggiornati )
		DatiCollezionati datiCollezionatiReaded = gestorePolicyAttive.getActiveThreadsPolicy(activePolicy).
				registerStartRequest(logCC,datiTransazione.getIdTransazione(),datiGroupBy);
		
	
		// Configuro Risultato
		RisultatoVerificaPolicy risultatoVerificaPolicy = new RisultatoVerificaPolicy();
		risultatoVerificaPolicy.setRisorsa(activePolicy.getTipoRisorsaPolicy());
		risultatoVerificaPolicy.setSimultanee(activePolicy.getConfigurazionePolicy().isSimultanee());
		risultatoVerificaPolicy.setWarningOnly(activePolicy.getInstanceConfiguration().isWarningOnly());
		if(TipoApplicabilita.CONDIZIONALE.equals(activePolicy.getConfigurazionePolicy().getTipoApplicabilita())){
			risultatoVerificaPolicy.setApplicabilitaCongestione(activePolicy.getConfigurazionePolicy().isApplicabilitaConCongestione());
			risultatoVerificaPolicy.setApplicabilitaDegradoPrestazionale(activePolicy.getConfigurazionePolicy().isApplicabilitaDegradoPrestazionale());
			risultatoVerificaPolicy.setApplicabilitaStatoAllarme(activePolicy.getConfigurazionePolicy().isApplicabilitaStatoAllarme());
		}
		
		
		GestoreCacheControlloTraffico gestoreCacheControlloTraffico = GestoreCacheControlloTraffico.getInstance();
		
		
		boolean violazionePolicy = false;
		boolean violazionePolicy_warningOnly = false;
		Date dataEventoPolicyViolated = null;
		String descriptionPolicyViolated = null; // non è definibile una descrizione, poichè si riferisce ad un evento qualsiasi di questo tipo
	
		//System.out.println("ATTUALI THREADS: "+counter);
	
		try{
		
			Date now = datiCollezionatiReaded.getCloneDate(); // Data in cui sono stati prelevati gli intervalli.
			
			
			// Imposto Dati Comuni
			// questa info finisce nel diagnostico di una transazione, che possiede già tutte le informazioni di istanza (fruitore, erogatore, ...)
			// Non serve quindi che aggiungo anche i dati relativi allo specifico group by
			boolean printDati = false;
			String toStringRaggruppamentoSenzaDatiIstanza = PolicyUtilities.toStringGroupBy(activePolicy.getInstanceConfiguration().getGroupBy(), datiGroupBy, printDati);
			msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_GRUPPO, toStringRaggruppamentoSenzaDatiIstanza);
			
			
			
						
			// ************ APPLICABILITA *************
			
			boolean isApplicabile = true;
			String descrizioneNonApplicabile = null;
			
			// verifico che il sistema risulti congestionato prima di applicare la policy
			if(risultatoVerificaPolicy.isApplicabilitaCongestione()){
				
				if(isPddCongestionata==false){
					
					isApplicabile = false;
					
					Object oThreshold = pddContext.getObject(GeneratoreMessaggiErrore.TEMPLATE_CONTROLLO_TRAFFICO_THRESHOLD);
					if(oThreshold!=null){
						descrizioneNonApplicabile = msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_ALL, 
								GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_APPLICABILITA_PDD_NON_CONGESTIONATA);
					}
					else{
						descrizioneNonApplicabile = msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_ALL, 
								GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_APPLICABILITA_PDD_NON_CONGESTIONATA_CONTROLLO_DISABILITATO);
					}
				}
				
			}
			
			// verifico che il sistema risulti in degrado prestazionale
			String descrizioneDegradoPrestazionaleRilevato = null;
			if(isApplicabile && risultatoVerificaPolicy.isApplicabilitaDegradoPrestazionale()){
				
				Date leftDate = null;
				Date rightDate = null;
				if(TipoFinestra.CORRENTE.equals(activePolicy.getConfigurazionePolicy().getDegradoAvgTimeFinestraOsservazione())){
					leftDate = datiCollezionatiReaded.getDegradoPrestazionaleLeftDateWindowCurrentInterval();
					rightDate = datiCollezionatiReaded.getDegradoPrestazionaleRightDateWindowCurrentInterval();
				}
				else if(TipoFinestra.PRECEDENTE.equals(activePolicy.getConfigurazionePolicy().getDegradoAvgTimeFinestraOsservazione())){
					leftDate = datiCollezionatiReaded.getDegradoPrestazionaleLeftDateWindowPrecedentInterval();
					rightDate = datiCollezionatiReaded.getDegradoPrestazionaleRightDateWindowPrecedentInterval();
				}
				else if(TipoFinestra.SCORREVOLE.equals(activePolicy.getConfigurazionePolicy().getDegradoAvgTimeFinestraOsservazione())){
					leftDate = datiCollezionatiReaded.getDegradoPrestazionaleLeftDateWindowSlidingInterval(now);
					rightDate = datiCollezionatiReaded.getDegradoPrestazionaleRightDateWindowSlidingInterval(now);
				}
				Date checkDate = null;
				
				long valoreAttuale = 0;
				if(TipoControlloPeriodo.REALTIME.equals(activePolicy.getConfigurazionePolicy().getDegradoAvgTimeModalitaControllo())){
					checkDate = now;
					if(TipoFinestra.CORRENTE.equals(activePolicy.getConfigurazionePolicy().getDegradoAvgTimeFinestraOsservazione())){
						if(datiCollezionatiReaded.getPolicyDegradoPrestazionaleAvgValue()!=null){
							valoreAttuale = datiCollezionatiReaded.getPolicyDegradoPrestazionaleAvgValue().longValue();
						}
					}
					else{
						// TipoFinestra.PRECEDENTE
						if(datiCollezionatiReaded.getOldPolicyDegradoPrestazionaleAvgValue()!=null){
							valoreAttuale = datiCollezionatiReaded.getOldPolicyDegradoPrestazionaleAvgValue().longValue();
						}
					}
				}
				else{
					RisultatoStatistico risultatoStatistico = gestoreCacheControlloTraffico.readLatenza(
							TipoRisorsa.TEMPO_MEDIO_RISPOSTA, // Di fatto se gia' è presente in cache, è la stessa informazione calcolata per la risorsa, se i parametri sono gli stessi
							leftDate,rightDate,
							activePolicy.getConfigurazionePolicy().getDegradoAvgTimeFinestraOsservazione(),
							activePolicy.getConfigurazionePolicy().getDegradoAvgTimeTipoIntervalloOsservazioneStatistico(), 
							activePolicy.getConfigurazionePolicy().getDegradoAvgTimeTipoLatenza(),
							datiTransazione, datiGroupBy,
							state);
					valoreAttuale = risultatoStatistico.getRisultato();
					checkDate = risultatoStatistico.getDateCheck();
				}
				
				msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_TIPOLOGIA_TEMPO_MEDIO, "Latenza "+
						activePolicy.getConfigurazionePolicy().getDegradoAvgTimeTipoLatenza().getValue());
				
				msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_AVG_TIME_RILEVATO, valoreAttuale+"");
				
				msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_VALORE_SOGLIA_DEGRADO_PRESTAZIONALE, tempiRisposta.getAvgResponseTime()+"");
				
				msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_INTERVALLO_TEMPORALE,
						PolicyDateUtils.toStringIntervalloTemporale(activePolicy.getConfigurazionePolicy().getDegradoAvgTimeFinestraOsservazione(), 
								leftDate, rightDate, checkDate, 
								TipoControlloPeriodo.STATISTIC.equals(activePolicy.getConfigurazionePolicy().getDegradoAvgTimeModalitaControllo())));
				
				if(valoreAttuale<=tempiRisposta.getAvgResponseTime()){
					
					isApplicabile = false;
										
					descrizioneNonApplicabile = msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_ALL, 
							GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_APPLICABILITA_DEGRADO_PRESTAZIONALE_NON_RILEVATO);
				}
				else{
					descrizioneDegradoPrestazionaleRilevato = msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_ALL, 
							GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_APPLICABILITA_DEGRADO_PRESTAZIONALE_RILEVATO);
				}
				
			}
			
			// verifico sullo stato di un allarme
			String descrizioneStatoAllarmeRilevato = null;
			if(isApplicabile && risultatoVerificaPolicy.isApplicabilitaStatoAllarme()){
				
				if(((ConfigurazioneControlloTraffico)activePolicy.getConfigurazioneControlloTraffico()).isNotifierEnabled()==false){
					throw new Exception("Modulo Allarmi non abilitato. La Policy deve essere applicata condizionalmente allo stato di un allarme");
				}
				
				RisultatoStato statoAttuale = null;
				String nomeAllarme = activePolicy.getConfigurazionePolicy().getAllarmeNome();
				msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_NOME_ALLARME, nomeAllarme);
				Integer statoIndicato = activePolicy.getConfigurazionePolicy().getAllarmeStato();
				boolean notStato = activePolicy.getConfigurazionePolicy().isAllarmeNotStato();
				try{
					statoAttuale = gestoreCacheControlloTraffico.getStato(datiTransazione,nomeAllarme);
				}catch(Exception e){
					isApplicabile = false;
					descrizioneNonApplicabile = "Recupero stato dell'allarme ["+nomeAllarme+"] non riuscito: "+e.getMessage();
				}
				if(statoAttuale!=null){
					
					msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_STATO_ALLARME, PolicyUtilities.toString(statoAttuale));
					
					boolean match = false;
					if(notStato){
						match = statoAttuale.getStato().intValue()!=statoIndicato.intValue();
						msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_STATO_ALLARME_ATTESO, "stato differente da '"+PolicyUtilities.toString(statoIndicato)+"'");
					}else{
						match = statoAttuale.getStato().intValue()==statoIndicato.intValue();
						msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_STATO_ALLARME_ATTESO, "stato uguale a '"+PolicyUtilities.toString(statoIndicato)+"'");
					}
					if(!match){
						isApplicabile = false;
						
						descrizioneNonApplicabile = msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_ALL, 
								GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_APPLICABILITA_STATO_ALLARME_NON_RILEVATO);
					}
					else{
						descrizioneStatoAllarmeRilevato = msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_ALL, 
								GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_APPLICABILITA_STATO_ALLARME_RILEVATO);
					}
				}
				
			}
			
			// aggiorno contatori
			if(isApplicabile || !activePolicy.getConfigurazioneControlloTraffico().isElaborazioneRealtime_incrementaSoloPolicyApplicabile()){
			
				datiCollezionatiReaded = gestorePolicyAttive.getActiveThreadsPolicy(activePolicy).updateDatiStartRequestApplicabile(logCC, datiTransazione.getIdTransazione(), datiGroupBy);
				now = datiCollezionatiReaded.getCloneDate(); // Data in cui sono stati prelevati gli intervalli.
				
				pddContext_policyApplicabile.add(true);
			}
			else{
				pddContext_policyApplicabile.add(false);
			}
			
			// resoconto finale
			if(isApplicabile==false){
				risultatoVerificaPolicy.setNonApplicabile(true);
				risultatoVerificaPolicy.setDescrizione(descrizioneNonApplicabile);
								
				pddContext_policyViolata.add(false);
				
				return risultatoVerificaPolicy;
			}

			

			
			
			
			
			
			// ************ VERIFICA POLICY *************
			
			long valoreSoglia = activePolicy.getConfigurazionePolicy().getValore();
			if (activePolicy.getInstanceConfiguration().isRidefinisci()){
				valoreSoglia = activePolicy.getInstanceConfiguration().getValore();
			}
			msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_VALORE_SOGLIA, valoreSoglia+"");

			boolean rilevataViolazione = false;
			
			switch (activePolicy.getTipoRisorsaPolicy()) {
			case NUMERO_RICHIESTE:
			case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
			case NUMERO_RICHIESTE_FALLITE:
			case NUMERO_FAULT_APPLICATIVI:
			case NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI:
				
				if(activePolicy.getConfigurazionePolicy().isSimultanee()){
					
					long valoreAttuale = datiCollezionatiReaded.getActiveRequestCounter();
					risultatoVerificaPolicy.setActualValue(valoreAttuale);
					risultatoVerificaPolicy.setMaxValue(valoreSoglia);
					if(datiCollezionatiReaded.getRightDateWindowCurrentInterval()!=null) {
						risultatoVerificaPolicy.setMsBeforeResetCounters(datiCollezionatiReaded.getRightDateWindowCurrentInterval().getTime()-DateManager.getTimeMillis());
					}
					if(!(valoreAttuale<=valoreSoglia)){
						
						//System.out.println("@@@addThread ERR NUMERO_RICHIESTE SIMULTANEE ["+datiCollezionatiReaded.getActiveRequestCounter()+"]<=["+valoreSoglia
						//	+"] WarningOnly["+risultatoVerificaPolicy.isWarningOnly()+"]");
						
						rilevataViolazione = true;
						risultatoVerificaPolicy.
							setDescrizione(msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_ALL, 
									GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_VIOLATA_NUMERO_RICHIESTE_SIMULTANEE));
						
					}
					
				}
				else{
									
					Date leftDate = null;
					Date rightDate = null;
					if(TipoFinestra.CORRENTE.equals(activePolicy.getConfigurazionePolicy().getFinestraOsservazione())){
						leftDate = datiCollezionatiReaded.getLeftDateWindowCurrentInterval();
						rightDate = datiCollezionatiReaded.getRightDateWindowCurrentInterval();
					}
					else if(TipoFinestra.PRECEDENTE.equals(activePolicy.getConfigurazionePolicy().getFinestraOsservazione())){
						leftDate = datiCollezionatiReaded.getLeftDateWindowPrecedentInterval();
						rightDate = datiCollezionatiReaded.getRightDateWindowPrecedentInterval();
					}
					else if(TipoFinestra.SCORREVOLE.equals(activePolicy.getConfigurazionePolicy().getFinestraOsservazione())){
						leftDate = datiCollezionatiReaded.getLeftDateWindowSlidingInterval(now);
						rightDate = datiCollezionatiReaded.getRightDateWindowSlidingInterval(now);
					}
					Date checkDate = null;
					
					long valoreAttuale = 0;
					if(TipoControlloPeriodo.REALTIME.equals(activePolicy.getConfigurazionePolicy().getModalitaControllo())){
						checkDate = now;
						if(TipoFinestra.CORRENTE.equals(activePolicy.getConfigurazionePolicy().getFinestraOsservazione())){
							if(datiCollezionatiReaded.getPolicyRequestCounter()!=null){
								valoreAttuale = datiCollezionatiReaded.getPolicyRequestCounter().longValue();
								switch (activePolicy.getTipoRisorsaPolicy()) {
								case NUMERO_RICHIESTE:
									break;
								case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
								case NUMERO_RICHIESTE_FALLITE:
								case NUMERO_FAULT_APPLICATIVI:
								case NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI:
									// se sono in uno tra questi 4 casi il contatore delle richieste in essere viene incrementato solo dopo aver consegnato la risposta (check esito).
									// poichè però il controllo sotto è con <= devo considerare la richiesta in essere con '+1'
									// altrimenti se ad es. il limite è 3, e sono già passate in passato 3 richieste, la 4 passa.
									valoreAttuale = valoreAttuale +1;
									break;
								default:
									break;
								}
							}
						}
						else{
							// TipoFinestra.PRECEDENTE
							if(datiCollezionatiReaded.getOldPolicyRequestCounter()!=null){
								valoreAttuale = datiCollezionatiReaded.getOldPolicyRequestCounter().longValue();
							}
						}
					}
					else{
						RisultatoStatistico risultatoStatistico = gestoreCacheControlloTraffico.readNumeroRichieste(activePolicy.getTipoRisorsaPolicy(),
								leftDate,rightDate,
								activePolicy.getConfigurazionePolicy().getFinestraOsservazione(),
								activePolicy.getConfigurazionePolicy().getTipoIntervalloOsservazioneStatistico(), 
								datiTransazione, datiGroupBy,
								state);
						valoreAttuale = risultatoStatistico.getRisultato();
						checkDate = risultatoStatistico.getDateCheck();
					}
							
					risultatoVerificaPolicy.setActualValue(valoreAttuale);
					risultatoVerificaPolicy.setMaxValue(valoreSoglia);
					if(TipoFinestra.CORRENTE.equals(activePolicy.getConfigurazionePolicy().getFinestraOsservazione()) || 
							TipoFinestra.SCORREVOLE.equals(activePolicy.getConfigurazionePolicy().getFinestraOsservazione())){
						if(rightDate!=null) {
							risultatoVerificaPolicy.setMsBeforeResetCounters(rightDate.getTime()-DateManager.getTimeMillis());
						}
						if(leftDate!=null && rightDate!=null) {
							risultatoVerificaPolicy.setMsWindow((rightDate.getTime()+1)-leftDate.getTime());
						}
					}
					
					if(!(valoreAttuale<=valoreSoglia)){
						
						//System.out.println("@@@addThread ERR NUMERO_RICHIESTE ["+valoreAttuale+"]<=["+valoreSoglia
						//	+"] WarningOnly["+risultatoVerificaPolicy.isWarningOnly()+"]");
						
						rilevataViolazione = true;
						
						msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_VALORE_RILEVATO, valoreAttuale+"");
						
						msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_INTERVALLO_TEMPORALE,
								PolicyDateUtils.toStringIntervalloTemporale(activePolicy.getConfigurazionePolicy().getFinestraOsservazione(), 
										leftDate, rightDate, checkDate, 
										TipoControlloPeriodo.STATISTIC.equals(activePolicy.getConfigurazionePolicy().getModalitaControllo())));
						
						String codeDiagnostico = null;
						switch (activePolicy.getTipoRisorsaPolicy()) {
							case NUMERO_RICHIESTE:
								codeDiagnostico = GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_VIOLATA_NUMERO_RICHIESTE;
								break;
							case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
								codeDiagnostico = GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_VIOLATA_NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO;
								break;
							case NUMERO_RICHIESTE_FALLITE:
								codeDiagnostico = GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_VIOLATA_NUMERO_RICHIESTE_FALLITE;
								break;
							case NUMERO_FAULT_APPLICATIVI:
								codeDiagnostico = GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_VIOLATA_NUMERO_FAULT_APPLICATIVI;
								break;
							case NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI:
								codeDiagnostico = GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_VIOLATA_NUMERO_RICHIESTE_FALLITE_O_FAULT_APPLICATIVI;
								break;
							default:
								// non può succedere
								break;
						}
						
						risultatoVerificaPolicy.
							setDescrizione(msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_ALL, codeDiagnostico));
						
					}
					
				}
				
				break;
				
			case OCCUPAZIONE_BANDA:
				
				Date leftDate = null;
				Date rightDate = null;
				if(TipoFinestra.CORRENTE.equals(activePolicy.getConfigurazionePolicy().getFinestraOsservazione())){
					leftDate = datiCollezionatiReaded.getLeftDateWindowCurrentInterval();
					rightDate = datiCollezionatiReaded.getRightDateWindowCurrentInterval();
				}
				else if(TipoFinestra.PRECEDENTE.equals(activePolicy.getConfigurazionePolicy().getFinestraOsservazione())){
					leftDate = datiCollezionatiReaded.getLeftDateWindowPrecedentInterval();
					rightDate = datiCollezionatiReaded.getRightDateWindowPrecedentInterval();
				}
				else if(TipoFinestra.SCORREVOLE.equals(activePolicy.getConfigurazionePolicy().getFinestraOsservazione())){
					leftDate = datiCollezionatiReaded.getLeftDateWindowSlidingInterval(now);
					rightDate = datiCollezionatiReaded.getRightDateWindowSlidingInterval(now);
				}
				Date checkDate = null;
				
				long valoreAttuale = 0;
				if(TipoControlloPeriodo.REALTIME.equals(activePolicy.getConfigurazionePolicy().getModalitaControllo())){
					checkDate = now;
					if(TipoFinestra.CORRENTE.equals(activePolicy.getConfigurazionePolicy().getFinestraOsservazione())){
						if(datiCollezionatiReaded.getPolicyCounter()!=null){
							valoreAttuale = datiCollezionatiReaded.getPolicyCounter().longValue();
						}
					}
					else{
						// TipoFinestra.PRECEDENTE
						if(datiCollezionatiReaded.getOldPolicyCounter()!=null){
							valoreAttuale = datiCollezionatiReaded.getOldPolicyCounter().longValue();
						}
					}
				}
				else{
					RisultatoStatistico risultatoStatistico = gestoreCacheControlloTraffico.readOccupazioneBanda(activePolicy.getTipoRisorsaPolicy(),
							leftDate,rightDate,
							activePolicy.getConfigurazionePolicy().getFinestraOsservazione(),
							activePolicy.getConfigurazionePolicy().getTipoIntervalloOsservazioneStatistico(), 
							activePolicy.getConfigurazionePolicy().getValoreTipoBanda(),
							datiTransazione, datiGroupBy,
							state);
					valoreAttuale = risultatoStatistico.getRisultato();
					checkDate = risultatoStatistico.getDateCheck();
				}
						
				long kb = DatiCollezionati.translateToKb(valoreAttuale);
				
				risultatoVerificaPolicy.setActualValue(kb);
				risultatoVerificaPolicy.setMaxValue(valoreSoglia);
				if(TipoFinestra.CORRENTE.equals(activePolicy.getConfigurazionePolicy().getFinestraOsservazione()) || 
						TipoFinestra.SCORREVOLE.equals(activePolicy.getConfigurazionePolicy().getFinestraOsservazione())){
					if(rightDate!=null) {
						risultatoVerificaPolicy.setMsBeforeResetCounters(rightDate.getTime()-DateManager.getTimeMillis());
					}
					if(leftDate!=null && rightDate!=null) {
						risultatoVerificaPolicy.setMsWindow((rightDate.getTime()+1)-leftDate.getTime());
					}
				}
				
				if(!(kb<=valoreSoglia)){ 
					
					//System.out.println("@@@addThread ERR OCCUPAZIONE BANDA ["+kb+"]<=["+valoreSoglia
					//	+"] WarningOnly["+risultatoVerificaPolicy.isWarningOnly()+"]");
					
					rilevataViolazione = true;
					
					msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_VALORE_RILEVATO, kb+"");
					
					msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_INTERVALLO_TEMPORALE,
							PolicyDateUtils.toStringIntervalloTemporale(activePolicy.getConfigurazionePolicy().getFinestraOsservazione(), 
									leftDate, rightDate, checkDate, 
									TipoControlloPeriodo.STATISTIC.equals(activePolicy.getConfigurazionePolicy().getModalitaControllo())));
					
					risultatoVerificaPolicy.
						setDescrizione(msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_ALL, 
								GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_VIOLATA_OCCUPAZIONE_BANDA));
					
				}
				
				break;
	
				
			case TEMPO_COMPLESSIVO_RISPOSTA:
				
				leftDate = null;
				rightDate = null;
				if(TipoFinestra.CORRENTE.equals(activePolicy.getConfigurazionePolicy().getFinestraOsservazione())){
					leftDate = datiCollezionatiReaded.getLeftDateWindowCurrentInterval();
					rightDate = datiCollezionatiReaded.getRightDateWindowCurrentInterval();
				}
				else if(TipoFinestra.PRECEDENTE.equals(activePolicy.getConfigurazionePolicy().getFinestraOsservazione())){
					leftDate = datiCollezionatiReaded.getLeftDateWindowPrecedentInterval();
					rightDate = datiCollezionatiReaded.getRightDateWindowPrecedentInterval();
				}
				else if(TipoFinestra.SCORREVOLE.equals(activePolicy.getConfigurazionePolicy().getFinestraOsservazione())){
					leftDate = datiCollezionatiReaded.getLeftDateWindowSlidingInterval(now);
					rightDate = datiCollezionatiReaded.getRightDateWindowSlidingInterval(now);
				}
				checkDate = null;
				
				valoreAttuale = 0;
				if(TipoControlloPeriodo.REALTIME.equals(activePolicy.getConfigurazionePolicy().getModalitaControllo())){
					checkDate = now;
					if(TipoFinestra.CORRENTE.equals(activePolicy.getConfigurazionePolicy().getFinestraOsservazione())){
						if(datiCollezionatiReaded.getPolicyCounter()!=null){
							valoreAttuale = datiCollezionatiReaded.getPolicyCounter().longValue();
						}
					}
					else{
						// TipoFinestra.PRECEDENTE
						if(datiCollezionatiReaded.getOldPolicyCounter()!=null){
							valoreAttuale = datiCollezionatiReaded.getOldPolicyCounter().longValue();
						}
					}
				}
				else{
					throw new Exception("Risorsa non utilizzabile con campionamento statistico");
				}
						
				long secondi = DatiCollezionati.translateToSeconds(valoreAttuale);
				
				risultatoVerificaPolicy.setActualValue(secondi);
				risultatoVerificaPolicy.setMaxValue(valoreSoglia);
				if(TipoFinestra.CORRENTE.equals(activePolicy.getConfigurazionePolicy().getFinestraOsservazione()) || 
						TipoFinestra.SCORREVOLE.equals(activePolicy.getConfigurazionePolicy().getFinestraOsservazione())){
					if(rightDate!=null) {
						risultatoVerificaPolicy.setMsBeforeResetCounters(rightDate.getTime()-DateManager.getTimeMillis());
					}
					if(leftDate!=null && rightDate!=null) {
						risultatoVerificaPolicy.setMsWindow((rightDate.getTime()+1)-leftDate.getTime());
					}
				}
				
				if(!(secondi<=valoreSoglia)){ 
					
					//System.out.println("@@@addThread ERR TEMPO COMPLESSIVO ["+secondi+"]<=["+valoreSoglia
					//	+"] WarningOnly["+risultatoVerificaPolicy.isWarningOnly()+"]");
					
					rilevataViolazione = true;
					
					msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_VALORE_RILEVATO, secondi+"");
					
					msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_INTERVALLO_TEMPORALE,
							PolicyDateUtils.toStringIntervalloTemporale(activePolicy.getConfigurazionePolicy().getFinestraOsservazione(), 
									leftDate, rightDate, checkDate, 
									TipoControlloPeriodo.STATISTIC.equals(activePolicy.getConfigurazionePolicy().getModalitaControllo())));
										
					risultatoVerificaPolicy.
						setDescrizione(msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_ALL, 
								GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_VIOLATA_TEMPO_COMPLESSIVO));
					
				}
				
				break;
				
				
			case TEMPO_MEDIO_RISPOSTA:
				
				leftDate = null;
				rightDate = null;
				if(TipoFinestra.CORRENTE.equals(activePolicy.getConfigurazionePolicy().getFinestraOsservazione())){
					leftDate = datiCollezionatiReaded.getLeftDateWindowCurrentInterval();
					rightDate = datiCollezionatiReaded.getRightDateWindowCurrentInterval();
				}
				else if(TipoFinestra.PRECEDENTE.equals(activePolicy.getConfigurazionePolicy().getFinestraOsservazione())){
					leftDate = datiCollezionatiReaded.getLeftDateWindowPrecedentInterval();
					rightDate = datiCollezionatiReaded.getRightDateWindowPrecedentInterval();
				}
				else if(TipoFinestra.SCORREVOLE.equals(activePolicy.getConfigurazionePolicy().getFinestraOsservazione())){
					leftDate = datiCollezionatiReaded.getLeftDateWindowSlidingInterval(now);
					rightDate = datiCollezionatiReaded.getRightDateWindowSlidingInterval(now);
				}
				checkDate = null;
				
				valoreAttuale = 0;
				if(TipoControlloPeriodo.REALTIME.equals(activePolicy.getConfigurazionePolicy().getModalitaControllo())){
					checkDate = now;
					if(TipoFinestra.CORRENTE.equals(activePolicy.getConfigurazionePolicy().getFinestraOsservazione())){
						if(datiCollezionatiReaded.getPolicyAvgValue()!=null){
							valoreAttuale = datiCollezionatiReaded.getPolicyAvgValue().longValue();
						}
					}
					else{
						// TipoFinestra.PRECEDENTE
						if(datiCollezionatiReaded.getOldPolicyAvgValue()!=null){
							valoreAttuale = datiCollezionatiReaded.getOldPolicyAvgValue().longValue();
						}
					}
				}
				else{
					RisultatoStatistico risultatoStatistico = gestoreCacheControlloTraffico.readLatenza(activePolicy.getTipoRisorsaPolicy(),
							leftDate,rightDate,
							activePolicy.getConfigurazionePolicy().getFinestraOsservazione(),
							activePolicy.getConfigurazionePolicy().getTipoIntervalloOsservazioneStatistico(), 
							activePolicy.getConfigurazionePolicy().getValoreTipoLatenza(),
							datiTransazione, datiGroupBy,
							state);
					valoreAttuale = risultatoStatistico.getRisultato();
					checkDate = risultatoStatistico.getDateCheck();
				}
					
				risultatoVerificaPolicy.setActualValue(valoreAttuale);
				risultatoVerificaPolicy.setMaxValue(valoreSoglia);
				if(TipoFinestra.CORRENTE.equals(activePolicy.getConfigurazionePolicy().getFinestraOsservazione()) || 
						TipoFinestra.SCORREVOLE.equals(activePolicy.getConfigurazionePolicy().getFinestraOsservazione())){
					if(rightDate!=null) {
						risultatoVerificaPolicy.setMsBeforeResetCounters(rightDate.getTime()-DateManager.getTimeMillis());
					}
					if(leftDate!=null && rightDate!=null) {
						risultatoVerificaPolicy.setMsWindow((rightDate.getTime()+1)-leftDate.getTime());
					}
				}
				
				if(!(valoreAttuale<=valoreSoglia)){
					
					//System.out.println("@@@addThread ERR TEMPO MEDIO ["+valoreAttuale+"]<=["+valoreSoglia
					//	+"] WarningOnly["+risultatoVerificaPolicy.isWarningOnly()+"]");
					
					rilevataViolazione = true;
					
					msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_TIPOLOGIA_TEMPO_MEDIO, "Latenza "+activePolicy.getConfigurazionePolicy().getValoreTipoLatenza().getValue());
										
					msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_AVG_TIME_RILEVATO, valoreAttuale+"");
						
					msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_INTERVALLO_TEMPORALE,
							PolicyDateUtils.toStringIntervalloTemporale(activePolicy.getConfigurazionePolicy().getFinestraOsservazione(), 
									leftDate, rightDate, checkDate, 
									TipoControlloPeriodo.STATISTIC.equals(activePolicy.getConfigurazionePolicy().getModalitaControllo())));
					
					risultatoVerificaPolicy.
						setDescrizione(msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_ALL, 
								GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_VIOLATA_TEMPO_MEDIO));
					
				}
				
				break;
	
			}
			
			if(rilevataViolazione){
				TipoEvento tipoEvento = null;
				if(policyGlobale) {
					tipoEvento = TipoEvento.RATE_LIMITING_POLICY_GLOBALE;
				}
				else {
					tipoEvento = TipoEvento.RATE_LIMITING_POLICY_API;
				}
				if(risultatoVerificaPolicy.isWarningOnly()){
					violazionePolicy_warningOnly = true;
					tr.addEventoGestione(tipoEvento.getValue()
							+"_"+
							CodiceEventoControlloTraffico.VIOLAZIONE_WARNING_ONLY.getValue()
							+"_"+
							PolicyUtilities.getNomeActivePolicy(activePolicy.getInstanceConfiguration().getAlias(), activePolicy.getInstanceConfiguration().getIdActivePolicy())
						);
				}
				else{
					violazionePolicy = true;
					tr.addEventoGestione(tipoEvento.getValue()
							+"_"+
							CodiceEventoControlloTraffico.VIOLAZIONE.getValue()
							+"_"+
							PolicyUtilities.getNomeActivePolicy(activePolicy.getInstanceConfiguration().getAlias(), activePolicy.getInstanceConfiguration().getIdActivePolicy())
						);
				}
				dataEventoPolicyViolated = DateManager.getDate();
				
				// arricchisco descrizioni con eventuali descrizioni sulla applicabilità
				if(descrizioneDegradoPrestazionaleRilevato!=null){
					String separatore = "\n";
					if(risultatoVerificaPolicy.getDescrizione().endsWith(".")==false){
						separatore = ".\n";
					}
					risultatoVerificaPolicy.setDescrizione(risultatoVerificaPolicy.getDescrizione()+separatore+descrizioneDegradoPrestazionaleRilevato);
				}
				if(descrizioneStatoAllarmeRilevato!=null){
					String separatore = "\n";
					if(risultatoVerificaPolicy.getDescrizione().endsWith(".")==false){
						separatore = ".\n";
					}
					risultatoVerificaPolicy.setDescrizione(risultatoVerificaPolicy.getDescrizione()+separatore+descrizioneStatoAllarmeRilevato);
				}
			}
			
			risultatoVerificaPolicy.setViolata(rilevataViolazione);
			
			pddContext_policyViolata.add(rilevataViolazione);
			
			return risultatoVerificaPolicy;
				
		}finally{
			
			// L'obiettivo è di generare un evento differente per ogni raggruppamento violato di una stessa policy
			// All'interno di una stessa policy ci possono essere gruppi che non sono violati ed altri che lo sono
			// Ad esempio se si raggruppa per soggetto fruitore, ci potranno essere soggetti che la violano, altri no.
			// Si vuole un evento per ogni soggetto che viola la policy
			String idPolicyConGruppo = null;
			if(violazionePolicy || violazionePolicy_warningOnly) {
				
				String API = null;
				if(policyAPI) {
					
					PorteNamingUtils namingUtils = new PorteNamingUtils(protocolFactory);
					
					String nomePorta = activePolicy.getInstanceConfiguration().getFiltro().getNomePorta();
					if(RuoloPolicy.DELEGATA.equals(activePolicy.getInstanceConfiguration().getFiltro().getRuoloPorta())) {
						IDPortaDelegata idPD = new IDPortaDelegata();
						idPD.setNome(nomePorta);
						PortaDelegata pd = configPdDManager.getPortaDelegata_SafeMethod(idPD);
						if(pd!=null) {
							IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValuesWithoutCheck(pd.getServizio().getTipo(), pd.getServizio().getNome(), 
									pd.getSoggettoErogatore().getTipo(), pd.getSoggettoErogatore().getNome(), 
									pd.getServizio().getVersione());
							IDSoggetto idFruitore = new IDSoggetto(pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario());
							List<MappingFruizionePortaDelegata> list = configPdDManager.getMappingFruizionePortaDelegataList(idFruitore, idServizio);
							if(list.size()<=1) {
								API = namingUtils.normalizePD(nomePorta);
							}
							else {
								String gruppo = null;
								for (MappingFruizionePortaDelegata mapping : list) {
									if(mapping.isDefault()) {
										API = namingUtils.normalizePD(mapping.getIdPortaDelegata().getNome());
									}
									if(nomePorta.equals(mapping.getIdPortaDelegata().getNome())) {
										gruppo = mapping.getDescrizione();
									}
								}
								API = API + " (gruppo '"+gruppo+"'"+") ";
							}
						}
					}
					else if(RuoloPolicy.APPLICATIVA.equals(activePolicy.getInstanceConfiguration().getFiltro().getRuoloPorta())) {
						IDPortaApplicativa idPA = new IDPortaApplicativa();
						idPA.setNome(nomePorta);
						PortaApplicativa pa = configPdDManager.getPortaApplicativa_SafeMethod(idPA);
						if(pa!=null) {
							IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValuesWithoutCheck(pa.getServizio().getTipo(), pa.getServizio().getNome(), 
									pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario(),
									pa.getServizio().getVersione());
							List<MappingErogazionePortaApplicativa> list = configPdDManager.getMappingErogazionePortaApplicativaList(idServizio);
							if(list.size()<=1) {
								API = namingUtils.normalizePA(nomePorta);
							}
							else {
								String gruppo = null;
								for (MappingErogazionePortaApplicativa mapping : list) {
									if(mapping.isDefault()) {
										API = namingUtils.normalizePA(mapping.getIdPortaApplicativa().getNome());
									}
									if(nomePorta.equals(mapping.getIdPortaApplicativa().getNome())) {
										gruppo = mapping.getDescrizione();
									}
								}
								API = API + " (gruppo '"+gruppo+"'"+") ";
							}
						}
					}
				}
				
				idPolicyConGruppo = PolicyUtilities.buildIdConfigurazioneEventoPerPolicy(activePolicy, datiGroupBy, API);
			
				
			}
			
			if(violazionePolicy){
				CategoriaEventoControlloTraffico tipoEvento = null;
				if(policyGlobale) {
					tipoEvento = CategoriaEventoControlloTraffico.POLICY_GLOBALE;
				}
				else {
					tipoEvento = CategoriaEventoControlloTraffico.POLICY_API;
				}
				NotificatoreEventi.getInstance().log(tipoEvento, 
						idPolicyConGruppo,
						dataEventoPolicyViolated, descriptionPolicyViolated); 
			}
			if(violazionePolicy_warningOnly){
				CategoriaEventoControlloTraffico tipoEvento = null;
				if(policyGlobale) {
					tipoEvento = CategoriaEventoControlloTraffico.POLICY_GLOBALE_WARNING_ONLY;
				}
				else {
					tipoEvento = CategoriaEventoControlloTraffico.POLICY_API_WARNING_ONLY;
				}
				NotificatoreEventi.getInstance().log(tipoEvento, 
						idPolicyConGruppo,
						dataEventoPolicyViolated, descriptionPolicyViolated); 
			}
			
		}

	}

}
