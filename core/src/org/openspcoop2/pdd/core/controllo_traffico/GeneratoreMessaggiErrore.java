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

package org.openspcoop2.pdd.core.controllo_traffico;

import java.util.List;

import org.openspcoop2.core.controllo_traffico.constants.TipoErrore;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.utils.ErroriProperties;
import org.openspcoop2.utils.Map;
import org.openspcoop2.utils.MapKey;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;

/**     
 * GeneratoreMessaggiErrore
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GeneratoreMessaggiErrore {

	
	// identificativi diagnostici parziali per policy violate
	public static final String MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_VIOLATA_NUMERO_RICHIESTE_SIMULTANEE = "controlloTraffico.policy.violata.risorsaNumeroRichieste.simultaneo";
	public static final String MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_VIOLATA_NUMERO_RICHIESTE = "controlloTraffico.policy.violata.risorsaNumeroRichieste";
	public static final String MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_VIOLATA_OCCUPAZIONE_BANDA = "controlloTraffico.policy.violata.risorsaOccupazioneBanda";
	public static final String MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_VIOLATA_TEMPO_COMPLESSIVO = "controlloTraffico.policy.violata.risorsaTempoComplessivo";
	public static final String MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_VIOLATA_TEMPO_MEDIO = "controlloTraffico.policy.violata.risorsaTempoMedio";
	public static final String MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_VIOLATA_NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO = "controlloTraffico.policy.violata.risorsaNumeroRichiesteCompletateConSuccesso";
	public static final String MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_VIOLATA_NUMERO_RICHIESTE_FALLITE = "controlloTraffico.policy.violata.risorsaNumeroRichiesteFallite";
	public static final String MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_VIOLATA_NUMERO_FAULT_APPLICATIVI = "controlloTraffico.policy.violata.risorsaNumeroFaultApplicativi";
	public static final String MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_VIOLATA_NUMERO_RICHIESTE_FALLITE_O_FAULT_APPLICATIVI= "controlloTraffico.policy.violata.risorsaNumeroRichiesteFalliteOFaultApplicativi";
	
	// identificativi diagnostici parziali per applicabilità policy
	public static final String MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_APPLICABILITA_PDD_NON_CONGESTIONATA = "controlloTraffico.policy.applicabilita.nonCongestionato";
	public static final String MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_APPLICABILITA_PDD_NON_CONGESTIONATA_CONTROLLO_DISABILITATO = "controlloTraffico.policy.applicabilita.controlloCongestioneDisabilitato";
	public static final String MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_APPLICABILITA_DEGRADO_PRESTAZIONALE_RILEVATO = "controlloTraffico.policy.applicabilita.degradoPrestazionale.rilevato";
	public static final String MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_APPLICABILITA_DEGRADO_PRESTAZIONALE_NON_RILEVATO = "controlloTraffico.policy.applicabilita.degradoPrestazionale.nonRilevato";
	public static final String MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_APPLICABILITA_STATO_ALLARME_RILEVATO = "controlloTraffico.policy.applicabilita.statoAllarme.rilevato";
	public static final String MSG_DIAGNOSTICO_INTERCEPTOR_POLICY_APPLICABILITA_STATO_ALLARME_NON_RILEVATO = "controlloTraffico.policy.applicabilita.statoAllarme.nonRilevato";
	
	// identificativi diagnostici
	public static final String MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_MAXREQUESTS_VIOLATED = "controlloTraffico.maxRequestsViolated";
	public static final String MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_MAXREQUESTS_VIOLATED_WARNING_ONLY = "controlloTraffico.maxRequestsViolatedWarningOnly";
	public static final String MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_PDD_CONGESTIONATA = "controlloTraffico.pddCongestionata";
	public static final String MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_CONTROLLO_IN_CORSO = "controlloTraffico.policy.controlloInCorso";
	public static final String MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_CONTROLLO_TERMINATO_CON_SUCCESSO = "controlloTraffico.policy.controlloTerminato.richiestaNonBloccata";
	public static final String MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_CONTROLLO_TERMINATO_CON_ERRORE = "controlloTraffico.policy.controlloTerminato.richiestaBloccata";
	public static final String MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_DISABILITATA = "controlloTraffico.policy.disabilitata";
	public static final String MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_FILTRATA = "controlloTraffico.policy.filtrata";
	public static final String MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_NON_APPLICABILE = "controlloTraffico.policy.nonApplicabile";
	public static final String MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_VIOLATA = "controlloTraffico.policy.violata";
	public static final String MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_VIOLATA_WARNING_ONLY = "controlloTraffico.policy.violataWarningOnly";
	public static final String MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_RISPETTATA = "controlloTraffico.policy.rispettata";
	public static final String MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_IN_ERRORE = "controlloTraffico.policy.inErrore";
	
	
	
	
	// ** KEYWORDS
	public static final String TEMPLATE_MAX_THREADS_THRESHOLD = "@CT_MAX_THREADS_THRESHOLD@"; // numero massimo di richieste simultanee configurate sul sistema
	public static final String TEMPLATE_CONTROLLO_TRAFFICO_THRESHOLD = "@CT_THRESHOLD@"; // soglia in % che indica quando attivare il controllo del traffico
	public static final String TEMPLATE_ACTIVE_THREADS = "@CT_ACTIVE_THREADS@"; // threads attivi
	public static final String TEMPLATE_NUMERO_POLICY = "@CT_POLICIES@"; // numero di policy configurate
	public static final String TEMPLATE_NUMERO_POLICY_DISABILITATE = "@CT_DISABLED_POLICIES@"; // numero di policy disabilitate
	public static final String TEMPLATE_NUMERO_POLICY_FILTRATE = "@CT_FILTERED_POLICIES@"; // numero di policy filtrate
	public static final String TEMPLATE_NUMERO_POLICY_NON_APPLICATE = "@CT_NOT_APPLICABLED_POLICIES@"; // numero di policy non applicabili
	public static final String TEMPLATE_NUMERO_POLICY_RISPETTATE = "@CT_RESPECTED_POLICIES@"; // numero di policy rispettate
	public static final String TEMPLATE_NUMERO_POLICY_VIOLATE = "@CT_VIOLATED_POLICIES@"; // numero di policy violate
	public static final String TEMPLATE_NUMERO_POLICY_VIOLATE_WARNING_ONLY = "@CT_VIOLATED_WARNING_ONLY_POLICIES@"; // numero di policy violate in warning only mode
	public static final String TEMPLATE_NUMERO_POLICY_IN_ERRORE = "@CT_ERROR_POLICIES@"; // numero di policy la cui verifica ha provocato un errore
	
	// ** KEYWORDS UTILIZZATE SOLAMENTE IN CONDIZIONI DI ERRORE O DEBUG
	// sono i diagnostici emessi dai moduli ricezione contenuti o buste
	public static final String TEMPLATE_POLICY_ACTIVE_ID = "@CT_POLICY_ACTIVE_ID@"; // identificativo della policy attivata
	public static final String TEMPLATE_POLICY_ACTIVE_TIPO = "@CT_POLICY_ACTIVE_TIPO@"; // tipo della policy attivata (Globale/API)
	public static final String TEMPLATE_POLICY_ACTIVE_ALIAS = "@CT_POLICY_ACTIVE_ALIAS@"; // alias della policy attivata
	public static final String TEMPLATE_POLICY_ACTIVE_RISORSA = "@CT_POLICY_ACTIVE_RISORSA@"; // risorsa in fase di valutazione (es. NumeroRichieste, OccupazioneBanda...)
	public static final String TEMPLATE_POLICY_ACTIVE_CONTINUE = "@CT_POLICY_ACTIVE_CONTINUE@"; //  true/false; indica se la valutazione delle policy per la stessa risorsa deve continuare o meno
	public static final String TEMPLATE_POLICY_VIOLATA_MOTIVO = "@CT_POLICY_ERROR_MSG@"; // errore di violazione riscontrato sulla policy
	public static final String TEMPLATE_POLICY_FILTRATA_MOTIVO = "@CT_POLICY_FILTERED_REASON@"; // motivo di filtro della policy
	public static final String TEMPLATE_POLICY_NON_APPLICABILE_MOTIVO = "@CT_POLICY_NOT_APPLICABLED_REASON@"; // motivo della mancata applicabilità
	
	public static final String TEMPLATE_POLICY_FILTRATA_MOTIVO_FILTRO = "i dati della transazione non rispettano i criteri di filtro impostati: ";
	public static final String TEMPLATE_POLICY_FILTRATA_MOTIVO_BREAK = "rilevata precedente policy soddisfatta";
	public static final String TEMPLATE_POLICY_FILTRATA_MOTIVO_BREAK_VIOLATA = "rilevata precedente policy violata";
	
	// sono i diagnostici di "appoggio" registrati nel modulo 'all' utilizzati per generare l'errore
	public static final String TEMPLATE_POLICY_VALORE_SOGLIA = "@CT_SOGLIA@"; // valore della soglia di una policy
	public static final String TEMPLATE_POLICY_TIPOLOGIA_TEMPO_MEDIO = "@CT_TIPOLOGIA_TEMPO_MEDIO@"; // tipologia di tempo medio (servizio, totale...)
	public static final String TEMPLATE_POLICY_VALORE_RILEVATO =  "@CT_RILEVATO@"; // contatore rilevato
	public static final String TEMPLATE_POLICY_AVG_TIME_RILEVATO = "@CT_AVG_TIME_RILEVATO@"; // tempo medio rilevato
	public static final String TEMPLATE_POLICY_VALORE_SOGLIA_DEGRADO_PRESTAZIONALE = "@CT_DEGRADO_SOGLIA@"; // valore di soglia per il degrado prestazionale
	public static final String TEMPLATE_POLICY_GRUPPO = "@CT_DATI_IDENTIFICATIVI_GRUPPO@"; // raggruppamento
	public static final String TEMPLATE_POLICY_INTERVALLO_TEMPORALE = "@CT_INTERVALLO_TEMPORALE@"; // intervallo temporale configurato
	public static final String TEMPLATE_POLICY_NOME_ALLARME = "@CT_NOME_ALLARME@"; // nome dell'allarme utilizzata nelle condizioni di applicabilità
	public static final String TEMPLATE_POLICY_STATO_ALLARME = "@CT_STATO_ALLARME@"; // stato dell'allarme
	public static final String TEMPLATE_POLICY_STATO_ALLARME_ATTESO = "@CT_STATO_ALLARME_ATTESO@"; // stato dell'allarme atteso
	

	
	private static final String SISTEMA_NON_DISPONIBILE = "Servizio Temporaneamente Non Erogabile";
		
	private static final String ERRORE_PROCESSAMENTO_CODE = "CC00";
	private static final String MAX_THREADS_VIOLATED_CODE = "CC01";
	
	private static final String ERRORE_GENERICO_DURANTE_VERIFICA = "ERR-";
	private static final String CONTROLLO_TRAFFICO_POLICY_VIOLATED_UNKNOW_TIPO = "CP00";
	private static final String CONTROLLO_TRAFFICO_POLICY_VIOLATED_NUMERO_RICHIESTE_SIMULTANEE_CODE = "CP01";
	private static final String CONTROLLO_TRAFFICO_POLICY_VIOLATED_NUMERO_RICHIESTE_CODE = "CP02";
	private static final String CONTROLLO_TRAFFICO_POLICY_VIOLATED_OCCUPAZIONE_BANDA_CODE = "CP03";
	private static final String CONTROLLO_TRAFFICO_POLICY_VIOLATED_TEMPO_COMPLESSIVO_CODE = "CP04";
	private static final String CONTROLLO_TRAFFICO_POLICY_VIOLATED_TEMPO_MEDIO_CODE = "CP05";
	private static final String CONTROLLO_TRAFFICO_POLICY_VIOLATED_NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO_CODE = "CP06";
	private static final String CONTROLLO_TRAFFICO_POLICY_VIOLATED_NUMERO_RICHIESTE_FALLITE_CODE = "CP07";
	private static final String CONTROLLO_TRAFFICO_POLICY_VIOLATED_NUMERO_FAULT_APPLICATIVI_CODE = "CP08";
	private static final String CONTROLLO_TRAFFICO_POLICY_VIOLATED_NUMERO_RICHIESTE_FALLITE_O_FAULT_APPLICATIVI_CODE = "CP09";
	private static final String CONTROLLO_TRAFFICO_POLICY_VIOLATED_DIMENSIONE_MASSIMA_MESSAGGIO_CODE = "CP10";
	private static final String APPLICABILITA_CONGESTIONE_CODE = "-CC";
	private static final String APPLICABILITA_DEGRADO_CODE = "-DP";
	private static final String APPLICABILITA_STATO_ALLARME_CODE = "-SA";
	
	
	
	/* ***** EVENTI UTILI PER RICONOSCERE L'ESITO DELLA TRANSAZIONE ****** */
	
	public static final MapKey<String> PDD_CONTEXT_NAME_CONTROLLO_TRAFFICO_VIOLAZIONE = org.openspcoop2.core.controllo_traffico.constants.Costanti.PDD_CONTEXT_NAME_CONTROLLO_TRAFFICO_VIOLAZIONE;
	
	private static final String PDD_CONTEXT_VALUE_GENERIC_ERROR = "controlloTrafficoGenericError";
	public static void addPddContextInfo_ControlloTrafficoGenericError(PdDContext pddContext){
		pddContext.addObject(PDD_CONTEXT_NAME_CONTROLLO_TRAFFICO_VIOLAZIONE, PDD_CONTEXT_VALUE_GENERIC_ERROR);
	}
	
	private static final String PDD_CONTEXT_VALUE_MAX_THREADS_VIOLATO = "controlloTrafficoNumeroMassimoRichiesteSimultaneeViolato";
	private static final String PDD_CONTEXT_VALUE_MAX_THREADS_VIOLATO_WARNING_ONLY = "controlloTrafficoNumeroMassimoRichiesteSimultaneeViolatoWarningOnly";
	public static void addPddContextInfo_ControlloTrafficoMaxThreadsViolated(PdDContext pddContext, boolean warningOnly){
		if(warningOnly){
			pddContext.addObject(PDD_CONTEXT_NAME_CONTROLLO_TRAFFICO_VIOLAZIONE, PDD_CONTEXT_VALUE_MAX_THREADS_VIOLATO_WARNING_ONLY);
		}
		else{
			pddContext.addObject(PDD_CONTEXT_NAME_CONTROLLO_TRAFFICO_VIOLAZIONE, PDD_CONTEXT_VALUE_MAX_THREADS_VIOLATO);
		}
	}
	
	private static final String PDD_CONTEXT_VALUE_POLICY_VIOLATA = "controlloTrafficoRateLimitingPolicyViolata";
	private static final String PDD_CONTEXT_VALUE_POLICY_VIOLATA_WARNING_ONLY = "controlloTrafficoRateLimitingPolicyViolataWarningOnly";
	public static void addPddContextInfo_ControlloTrafficoPolicyViolated(PdDContext pddContext, boolean warningOnly){
		addContextInfo_ControlloTrafficoPolicyViolated(pddContext, warningOnly);
	}
	public static void addContextInfo_ControlloTrafficoPolicyViolated(Context context, boolean warningOnly){
		if(warningOnly){
			context.addObject(PDD_CONTEXT_NAME_CONTROLLO_TRAFFICO_VIOLAZIONE, PDD_CONTEXT_VALUE_POLICY_VIOLATA_WARNING_ONLY);
		}
		else{
			context.addObject(PDD_CONTEXT_NAME_CONTROLLO_TRAFFICO_VIOLAZIONE, PDD_CONTEXT_VALUE_POLICY_VIOLATA);
		}
	}
	
	public static final MapKey<String> PDD_CONTEXT_MAX_THREADS_THRESHOLD = Map.newMapKey("CT_MAX_THREADS_THRESHOLD"); // numero massimo di richieste simultanee configurate sul sistema
	public static final MapKey<String> PDD_CONTEXT_CONTROLLO_TRAFFICO_THRESHOLD = Map.newMapKey("CT_THRESHOLD"); // soglia in % che indica quando attivare il controllo del traffico
	
	public static final MapKey<String> PDD_CONTEXT_ACTIVE_THREADS = Map.newMapKey("CT_ACTIVE_THREADS"); // threads attivi
	public static final MapKey<String> PDD_CONTEXT_NUMERO_POLICY = Map.newMapKey("CT_POLICIES"); // numero di policy configurate
	public static final MapKey<String> PDD_CONTEXT_NUMERO_POLICY_DISABILITATE = Map.newMapKey("CT_DISABLED_POLICIES"); // numero di policy disabilitate
	public static final MapKey<String> PDD_CONTEXT_NUMERO_POLICY_FILTRATE = Map.newMapKey("CT_FILTERED_POLICIES"); // numero di policy filtrate
	public static final MapKey<String> PDD_CONTEXT_NUMERO_POLICY_NON_APPLICATE = Map.newMapKey("CT_NOT_APPLICABLED_POLICIES"); // numero di policy non applicabili
	public static final MapKey<String> PDD_CONTEXT_NUMERO_POLICY_RISPETTATE = Map.newMapKey("CT_RESPECTED_POLICIES"); // numero di policy rispettate
	public static final MapKey<String> PDD_CONTEXT_NUMERO_POLICY_VIOLATE = Map.newMapKey("CT_VIOLATED_POLICIES"); // numero di policy violate
	public static final MapKey<String> PDD_CONTEXT_NUMERO_POLICY_VIOLATE_WARNING_ONLY = Map.newMapKey("CT_VIOLATED_WARNING_ONLY_POLICIES"); // numero di policy violate in warning only mode
	public static final MapKey<String> PDD_CONTEXT_NUMERO_POLICY_IN_ERRORE = Map.newMapKey("CT_ERROR_POLICIES"); // numero di policy la cui verifica ha provocato un errore
	
	private static final String PDD_CONTEXT_VALUE_CONNECTION_TIMEOUT = org.openspcoop2.core.controllo_traffico.constants.Costanti.PDD_CONTEXT_VALUE_CONNECTION_TIMEOUT;
	private static final String PDD_CONTEXT_VALUE_REQUEST_READ_TIMEOUT = org.openspcoop2.core.controllo_traffico.constants.Costanti.PDD_CONTEXT_VALUE_REQUEST_READ_TIMEOUT;
	private static final String PDD_CONTEXT_VALUE_RESPONSE_READ_TIMEOUT = org.openspcoop2.core.controllo_traffico.constants.Costanti.PDD_CONTEXT_VALUE_RESPONSE_READ_TIMEOUT;
	private static final String PDD_CONTEXT_VALUE_READ_TIMEOUT = org.openspcoop2.core.controllo_traffico.constants.Costanti.PDD_CONTEXT_VALUE_READ_TIMEOUT;
	public static boolean addPddContextInfoControlloTrafficoConnectionTimeout(Context pddContext){
		if(alreadyExistsPddContextInfoControlloTrafficoTimeout(pddContext, PDD_CONTEXT_VALUE_CONNECTION_TIMEOUT)) {
			return false;
		}
		pddContext.addObject(PDD_CONTEXT_NAME_CONTROLLO_TRAFFICO_VIOLAZIONE, PDD_CONTEXT_VALUE_CONNECTION_TIMEOUT);
		return true;
	}
	public static boolean addPddContextInfoControlloTrafficoReadRequestTimeout(Context pddContext, boolean checkAll){
		if(checkAll) {
			if(alreadyExistsPddContextInfoControlloTrafficoTimeout(pddContext, 
					PDD_CONTEXT_VALUE_READ_TIMEOUT, PDD_CONTEXT_VALUE_RESPONSE_READ_TIMEOUT, PDD_CONTEXT_VALUE_REQUEST_READ_TIMEOUT)) {
				return false;
			}
		}
		else if(alreadyExistsPddContextInfoControlloTrafficoTimeout(pddContext, PDD_CONTEXT_VALUE_REQUEST_READ_TIMEOUT)) {
			return false;
		}
		pddContext.addObject(PDD_CONTEXT_NAME_CONTROLLO_TRAFFICO_VIOLAZIONE, PDD_CONTEXT_VALUE_REQUEST_READ_TIMEOUT);
		return true;
	}
	public static boolean addPddContextInfoControlloTrafficoReadResponseTimeout(Context pddContext, boolean checkAll){
		if(checkAll) {
			if(alreadyExistsPddContextInfoControlloTrafficoTimeout(pddContext, 
					PDD_CONTEXT_VALUE_READ_TIMEOUT, PDD_CONTEXT_VALUE_RESPONSE_READ_TIMEOUT, PDD_CONTEXT_VALUE_REQUEST_READ_TIMEOUT)) {
				return false;
			}
		}
		else if(alreadyExistsPddContextInfoControlloTrafficoTimeout(pddContext, PDD_CONTEXT_VALUE_RESPONSE_READ_TIMEOUT)) {
			return false;
		}
		pddContext.addObject(PDD_CONTEXT_NAME_CONTROLLO_TRAFFICO_VIOLAZIONE, PDD_CONTEXT_VALUE_RESPONSE_READ_TIMEOUT);
		return true;
	}
	public static boolean addPddContextInfoControlloTrafficoReadTimeout(Context pddContext, boolean checkAll){
		if(checkAll) {
			if(alreadyExistsPddContextInfoControlloTrafficoTimeout(pddContext, 
					PDD_CONTEXT_VALUE_READ_TIMEOUT, PDD_CONTEXT_VALUE_RESPONSE_READ_TIMEOUT, PDD_CONTEXT_VALUE_REQUEST_READ_TIMEOUT)) {
				return false;
			}
		}
		else if(alreadyExistsPddContextInfoControlloTrafficoTimeout(pddContext, PDD_CONTEXT_VALUE_READ_TIMEOUT)) {
			return false;
		}
		pddContext.addObject(PDD_CONTEXT_NAME_CONTROLLO_TRAFFICO_VIOLAZIONE, PDD_CONTEXT_VALUE_READ_TIMEOUT);
		return true;
	}
	private static boolean alreadyExistsPddContextInfoControlloTrafficoTimeout(Context pddContext, String ... valore) {
		if(valore!=null && valore.length>0) {
			for (String v : valore) {
				if(alreadyExistsPddContextInfoControlloTrafficoTimeout(pddContext, v)) {
					return true;
				}
			}
		}
		return false;
	}
	private static boolean alreadyExistsPddContextInfoControlloTrafficoTimeout(Context pddContext, String valore) {
		if(pddContext.containsKey(PDD_CONTEXT_NAME_CONTROLLO_TRAFFICO_VIOLAZIONE)) {
			String tipo = (String) pddContext.get(PDD_CONTEXT_NAME_CONTROLLO_TRAFFICO_VIOLAZIONE);
			if(tipo!=null) {
				return valore.equals(tipo);
			}
		}
		return false;
	}
	
	/* ***** FAULT ****** */
	
	public static HandlerException getErroreProcessamento(Exception e, 
			PdDContext pddContext){
				
		HandlerException he = new HandlerException( SISTEMA_NON_DISPONIBILE + " [" + ERRORE_PROCESSAMENTO_CODE + "]" , e);
		he.setCustomizedResponse(true);
		he.setCustomizedResponseAs4xxCode(false);
		he.setCustomizedResponseCode(ERRORE_PROCESSAMENTO_CODE);
		
		return he;

	} 
	
	public static HandlerException getMaxThreadsViolated(String messaggioErroreInChiaro, boolean erroreGenerico, 
			PdDContext pddContext){
		
		HandlerException he =  null;
		
		if(erroreGenerico){
			he = new HandlerException( SISTEMA_NON_DISPONIBILE + " [" + MAX_THREADS_VIOLATED_CODE + "]" );
		}
		else{
			he = new HandlerException( messaggioErroreInChiaro );
		}
		
		he.setCustomizedResponse(true);
		he.setCustomizedResponseAs4xxCode(true);
		he.setCustomizedResponseCode(MAX_THREADS_VIOLATED_CODE);
		he.setIntegrationFunctionError(IntegrationFunctionError.TOO_MANY_REQUESTS);
						
		return he;
	} 
	
	public static HandlerException getControlloTrafficoPolicyViolated(List<RisultatoVerificaPolicy> policyViolate, boolean erroreGenerico, 
			PdDContext pddContext){
		
		HandlerException he =  null;
		
		StringBuilder bf = new StringBuilder();
		if(erroreGenerico==false){
			bf.append("Rilevate "+policyViolate.size()+" policy violate:\n");
		}
		boolean limitExceeded_conditionalCongestion = false; 
		boolean limitExceeded_conditionalDeteriorationPerformance = false; 
		boolean limitExceeded = false; 
		boolean tooManyRequests_conditionalCongestion = false; 
		boolean tooManyRequests_conditionalDeteriorationPerformance = false; 
		boolean tooManyRequests = false; 
				
		for (int i = 0; i < policyViolate.size(); i++) {
			RisultatoVerificaPolicy risultato = policyViolate.get(i);
			
			if(!risultato.isErroreGenerico()) {
				if(risultato.isSimultanee()) {
					if(risultato.isApplicabilitaCongestione()) {
						tooManyRequests_conditionalCongestion = true;
					}
					else if(risultato.isApplicabilitaDegradoPrestazionale()) {
						tooManyRequests_conditionalDeteriorationPerformance = true;
					}
					else if(risultato.isApplicabilitaStatoAllarme()) {
						tooManyRequests_conditionalDeteriorationPerformance = true; // TODO IntegrationFunctionError apposito
					}
					else {
						tooManyRequests = true;
					}
				}
				else {
					if(risultato.isApplicabilitaCongestione()) {
						limitExceeded_conditionalCongestion = true;
					}
					else if(risultato.isApplicabilitaDegradoPrestazionale()) {
						limitExceeded_conditionalDeteriorationPerformance = true;
					}
					else if(risultato.isApplicabilitaStatoAllarme()) {
						limitExceeded_conditionalDeteriorationPerformance = true; // TODO IntegrationFunctionError apposito
					}
					else {
						limitExceeded = true;
					}
				}
			}
			else {
				tooManyRequests = true; // viene scelto questo 
			}
			
			if(erroreGenerico){
				
				if(i>0){
					bf.append(",");
				}
				
				bf.append(toCode(risultato));
				
			}
			else{
				
				if(i>0){
					bf.append("\n");
				}
				
				if(risultato.isErroreGenerico()){
					bf.append("\tPolicy-"+(i+1)+" (Errore interno) "+risultato.getDescrizione());
				}
				else{
					bf.append("\tPolicy-"+(i+1)+" (Violata) "+risultato.getDescrizione());
				}
			}
		}
		
		if(erroreGenerico){
			he = new HandlerException( SISTEMA_NON_DISPONIBILE + " [" + bf.toString() + "]" );
		}
		else{
			he = new HandlerException( bf.toString() );
		}
		
		he.setCustomizedResponse(true);
		he.setCustomizedResponseAs4xxCode(true);
		StringBuilder bfCode = new StringBuilder();
		for (int i = 0; i < policyViolate.size(); i++) {
			RisultatoVerificaPolicy risultato = policyViolate.get(i);
			if(i>0){
				bfCode.append("_");
			}
			bfCode.append(toCode(risultato));
		}
		he.setCustomizedResponseCode(bfCode.toString());
		
		if(limitExceeded) {
			// prevale poiche' il client non potra rispedire
			he.setIntegrationFunctionError(IntegrationFunctionError.LIMIT_EXCEEDED);
		}
		else if(limitExceeded_conditionalCongestion) {
			he.setIntegrationFunctionError(IntegrationFunctionError.LIMIT_EXCEEDED_CONDITIONAL_CONGESTION);
		}
		else if(limitExceeded_conditionalDeteriorationPerformance) {
			he.setIntegrationFunctionError(IntegrationFunctionError.LIMIT_EXCEEDED_CONDITIONAL_DETERIORATION_PERFORMANCE);
		}
		else if(tooManyRequests) {
			he.setIntegrationFunctionError(IntegrationFunctionError.TOO_MANY_REQUESTS);
		}
		else if(tooManyRequests_conditionalCongestion) {
			he.setIntegrationFunctionError(IntegrationFunctionError.TOO_MANY_REQUESTS_CONDITIONAL_CONGESTION);
		}
		else if(tooManyRequests_conditionalDeteriorationPerformance) {
			he.setIntegrationFunctionError(IntegrationFunctionError.TOO_MANY_REQUESTS_CONDITIONAL_DETERIORATION_PERFORMANCE);
		}
		else {
			he.setIntegrationFunctionError(IntegrationFunctionError.TOO_MANY_REQUESTS);
		}
		
		return he;
	} 

	private static String toCode(RisultatoVerificaPolicy risultato) {
		StringBuilder bf = new StringBuilder();
		if(risultato.isErroreGenerico()){
			bf.append(ERRORE_GENERICO_DURANTE_VERIFICA);
		}
		
		if(risultato.getRisorsa()==null){
			bf.append(CONTROLLO_TRAFFICO_POLICY_VIOLATED_UNKNOW_TIPO);
		}
		else{
			switch (risultato.getRisorsa()) {
			case NUMERO_RICHIESTE:
				if(risultato.isSimultanee()){
					bf.append(CONTROLLO_TRAFFICO_POLICY_VIOLATED_NUMERO_RICHIESTE_SIMULTANEE_CODE);
				}
				else{
					bf.append(CONTROLLO_TRAFFICO_POLICY_VIOLATED_NUMERO_RICHIESTE_CODE);
				}
				break;
			case DIMENSIONE_MASSIMA_MESSAGGIO:
				bf.append(CONTROLLO_TRAFFICO_POLICY_VIOLATED_DIMENSIONE_MASSIMA_MESSAGGIO_CODE);
				break;
			case OCCUPAZIONE_BANDA:
				bf.append(CONTROLLO_TRAFFICO_POLICY_VIOLATED_OCCUPAZIONE_BANDA_CODE);
				break;
			case TEMPO_COMPLESSIVO_RISPOSTA:
				bf.append(CONTROLLO_TRAFFICO_POLICY_VIOLATED_TEMPO_COMPLESSIVO_CODE);
				break;
			case TEMPO_MEDIO_RISPOSTA:
				bf.append(CONTROLLO_TRAFFICO_POLICY_VIOLATED_TEMPO_MEDIO_CODE);
				break;
			case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
				bf.append(CONTROLLO_TRAFFICO_POLICY_VIOLATED_NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO_CODE);
				break;
			case NUMERO_RICHIESTE_FALLITE:
				bf.append(CONTROLLO_TRAFFICO_POLICY_VIOLATED_NUMERO_RICHIESTE_FALLITE_CODE);
				break;
			case NUMERO_FAULT_APPLICATIVI:
				bf.append(CONTROLLO_TRAFFICO_POLICY_VIOLATED_NUMERO_FAULT_APPLICATIVI_CODE);
				break;
			case NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI:
				bf.append(CONTROLLO_TRAFFICO_POLICY_VIOLATED_NUMERO_RICHIESTE_FALLITE_O_FAULT_APPLICATIVI_CODE);
				break;
			}
			
			if(risultato.isApplicabilitaCongestione()){
				bf.append(APPLICABILITA_CONGESTIONE_CODE);
			}
			if(risultato.isApplicabilitaDegradoPrestazionale()){
				bf.append(APPLICABILITA_DEGRADO_CODE);
			}
			if(risultato.isApplicabilitaStatoAllarme()){
				bf.append(APPLICABILITA_STATO_ALLARME_CODE);
			}
		}
		return bf.toString();
	}
	
	public static void configureHandlerExceptionByTipoErrore(ServiceBinding serviceBinding, HandlerException he, TipoErrore tipoErroreParam,boolean includiDescrizioneErrore,Logger log) throws ProtocolException {

		TipoErrore tipoErrore = tipoErroreParam;
		if(ServiceBinding.REST.equals(serviceBinding)) {
			tipoErrore = TipoErrore.FAULT; // in rest il codice http viene auto-gestito correttamente
		}
		
		if(he==null) {
			throw new ProtocolException("HandlerException is null");
		}
		
		switch (tipoErrore) {
		case HTTP_429:
			if(includiDescrizioneErrore) {
				he.setResponseContentType(HttpConstants.CONTENT_TYPE_HTML);
				
				String tipo = CostantiControlloTraffico.HTML_429_TOO_MANY_REQUESTS_ERROR;
				if(he.getIntegrationFunctionError()!=null) {
					switch (he.getIntegrationFunctionError()) {
					case LIMIT_EXCEEDED:
					case LIMIT_EXCEEDED_CONDITIONAL_CONGESTION:
					case LIMIT_EXCEEDED_CONDITIONAL_DETERIORATION_PERFORMANCE:
						tipo = CostantiControlloTraffico.HTML_429_LIMIT_EXCEEDED_ERROR;
						break;
					case TOO_MANY_REQUESTS:
					case TOO_MANY_REQUESTS_CONDITIONAL_CONGESTION:
					case TOO_MANY_REQUESTS_CONDITIONAL_DETERIORATION_PERFORMANCE:
						tipo = CostantiControlloTraffico.HTML_429_TOO_MANY_REQUESTS_ERROR;
						break;
					default:
						break;
					}
				}
				
				String msgErrore = null;
				ErroriProperties erroriProperties = ErroriProperties.getInstance(log);
				boolean genericDetails = true;
				IntegrationFunctionError functionError = IntegrationFunctionError.TOO_MANY_REQUESTS;
				if(he.getIntegrationFunctionError()!=null) {
					functionError = he.getIntegrationFunctionError();
				}
//				if(!genericDetails && erroriProperties.isForceGenericDetails(functionError)) {
//					genericDetails = true;
//				}
				if (Costanti.isTRANSACTION_FORCE_SPECIFIC_ERROR_DETAILS()) {
					genericDetails = false;
				}
				if(!genericDetails && he!=null) {
					msgErrore = he.getMessage();
				}
				else {
					msgErrore = erroriProperties.getGenericDetails(functionError);
				}
				
				String html = tipo
						.replace(CostantiControlloTraffico.HTML_ERROR_MESSAGE_TEMPLATE, msgErrore);
				he.setResponse(html.getBytes());
			}
			else {
				he.setEmptyResponse(true);
			}
			he.setResponseCode("429");
			break;
		case HTTP_503:
			if(includiDescrizioneErrore) {
				he.setResponseContentType(HttpConstants.CONTENT_TYPE_HTML);
				String html = CostantiControlloTraffico.HTML_503_ERROR
						.replace(CostantiControlloTraffico.HTML_ERROR_MESSAGE_TEMPLATE, he.getMessage());
				he.setResponse(html.getBytes());
			}
			else {
				he.setEmptyResponse(true);
			}
			he.setResponseCode("503");
			break;
		case HTTP_500:
			if(includiDescrizioneErrore) {
				he.setResponseContentType(HttpConstants.CONTENT_TYPE_HTML);
				String html = CostantiControlloTraffico.HTML_500_ERROR
						.replace(CostantiControlloTraffico.HTML_ERROR_MESSAGE_TEMPLATE, he.getMessage());
				he.setResponse(html.getBytes());
			}
			else {
				he.setEmptyResponse(true);
			}
			he.setResponseCode("500");
			break;
		case FAULT:
			if(includiDescrizioneErrore==false) {
				he.setCustomizedResponse(false);
			}
			break;
		}
	}
	
}
