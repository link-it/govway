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
package org.openspcoop2.pdd.core.controllo_traffico.handler;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.MisurazioniTransazione;
import org.openspcoop2.core.controllo_traffico.driver.IGestorePolicyAttive;
import org.openspcoop2.core.controllo_traffico.driver.IPolicyGroupByActiveThreads;
import org.openspcoop2.core.controllo_traffico.driver.PolicyNotFoundException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyShutdownException;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.controllo_traffico.CostantiControlloTraffico;
import org.openspcoop2.pdd.core.controllo_traffico.GeneratoreMessaggiErrore;
import org.openspcoop2.pdd.core.controllo_traffico.GestoreControlloTraffico;
import org.openspcoop2.pdd.core.controllo_traffico.policy.config.PolicyConfiguration;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.GestorePolicyAttive;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.logger.transazioni.InformazioniTransazione;
import org.openspcoop2.pdd.logger.transazioni.TransazioniProcessTimes;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;

/**     
 * PostOutResponseHandlerGestioneControlloTraffico
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PostOutResponseHandlerGestioneControlloTraffico {

	public void process(Boolean controlloTrafficoMaxRequestThreadRegistrato, Logger logger, String idTransazione,
			Transazione transazioneDTO, InformazioniTransazione info, EsitoTransazione esito,
			TransazioniProcessTimes times){
		
		Context context = info.getContext();
		
		long timeStart = -1;
		
		if(controlloTrafficoMaxRequestThreadRegistrato!=null && controlloTrafficoMaxRequestThreadRegistrato){
			// significa che sono entrato nel motore di anti-congestionamento
			if(times!=null) {
				timeStart = DateManager.getTimeMillis();
			}
			try {
				
				Long maxThreads = null;
				Integer threshold = null;
				
				if(context!=null){
					Object maxThreadsObject = context.getObject(GeneratoreMessaggiErrore.PDD_CONTEXT_MAX_THREADS_THRESHOLD);
					if(maxThreadsObject instanceof Long){
						maxThreads = (Long) maxThreadsObject;
					}
					
					Object thresholdObject = context.getObject(GeneratoreMessaggiErrore.PDD_CONTEXT_CONTROLLO_TRAFFICO_THRESHOLD);
					if(thresholdObject!=null && maxThreadsObject instanceof Long){
						threshold = (Integer) thresholdObject;
					}
				}
				
				GestoreControlloTraffico.getInstance().removeThread(maxThreads,threshold,idTransazione);
			} catch (Exception e) {
				logger.error("["+idTransazione+"] Errore durante la rimozione del thread all'interno della lista dei threads attivi",e);
			}
			if(times!=null) {
				long timeEnd =  DateManager.getTimeMillis();
				long timeProcess = timeEnd-timeStart;
				times.setControlloTrafficoRemoveThread(timeProcess);
			}
		}
		try {
			Logger logControlloTraffico = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(OpenSPCoop2Properties.getInstance().isControlloTrafficoDebug());
			if(transazioneDTO!=null){
			
				if(times!=null) {
					timeStart = DateManager.getTimeMillis();
				}
				
				Object objectId = null;
				if(context!=null) {
					objectId = context.removeObject(CostantiControlloTraffico.PDD_CONTEXT_LIST_UNIQUE_ID_POLICY);
				}
				if(context!=null &&
						objectId!=null && (objectId instanceof List<?>) ){
					@SuppressWarnings("unchecked")
					List<String> uniqueIdsPolicies = (List<String>) objectId;
					if(uniqueIdsPolicies!=null && !uniqueIdsPolicies.isEmpty()){
						Object object = context.removeObject(CostantiControlloTraffico.PDD_CONTEXT_LIST_GROUP_BY_CONDITION);
						Object objectIncrementCounterPolicyApplicabile = context.removeObject(CostantiControlloTraffico.PDD_CONTEXT_LIST_POLICY_APPLICABILE);
						Object objectIncrementCounterPolicyViolata = context.removeObject(CostantiControlloTraffico.PDD_CONTEXT_LIST_POLICY_VIOLATA);
						Object objectPolicyTypes = context.removeObject(CostantiControlloTraffico.PDD_CONTEXT_LIST_API_OR_GLOBAL_OR_DEFAULT);
						Object objectPolicyConfigurationPorta = context.removeObject(CostantiControlloTraffico.PDD_CONTEXT_POLICY_CONFIG_PORTA);
						Object objectPolicyConfigurationGlobale = context.removeObject(CostantiControlloTraffico.PDD_CONTEXT_POLICY_CONFIG_GLOBALE);
						if((object instanceof List<?>) &&
								objectIncrementCounterPolicyApplicabile!=null && (objectIncrementCounterPolicyApplicabile instanceof List<?>) &&
								objectIncrementCounterPolicyViolata!=null && (objectIncrementCounterPolicyViolata instanceof List<?>) &&
								objectPolicyTypes!=null && (objectPolicyTypes instanceof List<?>) &&
								objectPolicyConfigurationGlobale!=null && (objectPolicyConfigurationGlobale instanceof PolicyConfiguration) &&
								objectPolicyConfigurationPorta!=null && (objectPolicyConfigurationPorta instanceof PolicyConfiguration)){
							@SuppressWarnings("unchecked")
							List<IDUnivocoGroupByPolicy> groupByPolicies = (List<IDUnivocoGroupByPolicy>) object; 
							@SuppressWarnings("unchecked")
							List<Boolean> incrementCounterPolicyApplicabile = (List<Boolean>) objectIncrementCounterPolicyApplicabile; 
							@SuppressWarnings("unchecked")
							List<Boolean> incrementCounterPolicyViolata = (List<Boolean>) objectIncrementCounterPolicyViolata; 
							@SuppressWarnings("unchecked")
							List<String> policyTypes = (List<String>) objectPolicyTypes; 
							PolicyConfiguration policyConfigurationGlobale = (PolicyConfiguration) objectPolicyConfigurationGlobale;
							PolicyConfiguration policyConfigurationPorta = (PolicyConfiguration) objectPolicyConfigurationPorta;
							if(groupByPolicies!=null && groupByPolicies.size()==uniqueIdsPolicies.size() &&
									incrementCounterPolicyApplicabile!=null && incrementCounterPolicyApplicabile.size()==uniqueIdsPolicies.size() &&
									incrementCounterPolicyViolata!=null && incrementCounterPolicyViolata.size()==uniqueIdsPolicies.size() &&
									policyTypes!=null && policyTypes.size()==uniqueIdsPolicies.size() &&
									policyConfigurationGlobale!=null &&
									policyConfigurationPorta!=null) {
								
								MisurazioniTransazione misurazioniTransazione = new MisurazioniTransazione();
								misurazioniTransazione.setTipoPdD(info.getTipoPorta());
								misurazioniTransazione.setProtocollo( info.getProtocolFactory().getProtocol());
								misurazioniTransazione.setEsitoTransazione(esito.getCode());
								
								misurazioniTransazione.setDataIngressoRichiesta(transazioneDTO.getDataIngressoRichiesta());
								misurazioniTransazione.setDataUscitaRichiesta(transazioneDTO.getDataUscitaRichiesta());
								misurazioniTransazione.setDataIngressoRisposta(transazioneDTO.getDataIngressoRisposta());
								misurazioniTransazione.setDataUscitaRisposta(transazioneDTO.getDataUscitaRisposta());
								
								misurazioniTransazione.setRichiestaIngressoBytes(transazioneDTO.getRichiestaIngressoBytes());
								misurazioniTransazione.setRichiestaUscitaBytes(transazioneDTO.getRichiestaUscitaBytes());
								misurazioniTransazione.setRispostaIngressoBytes(transazioneDTO.getRispostaIngressoBytes());
								misurazioniTransazione.setRispostaUscitaBytes(transazioneDTO.getRispostaUscitaBytes());
								
								if(times!=null) {
									long timeEnd =  DateManager.getTimeMillis();
									long timeProcess = timeEnd-timeStart;
									times.setControlloTrafficoPreparePolicy(timeProcess);
								
									if(!uniqueIdsPolicies.isEmpty()) {
										times.setControlloTrafficoPolicyTimes(new ArrayList<>());
									}
								}
								
								for (int i = 0; i < uniqueIdsPolicies.size(); i++) {
									String uniqueIdMap = null;
									try{
										uniqueIdMap = uniqueIdsPolicies.get(i);
										
										StringBuilder sb = null;
										if(times!=null) {
											timeStart = DateManager.getTimeMillis();
										}
										
										String policyAPI = policyTypes.get(i);
										PolicyConfiguration policyConfiguration = null;
										if(CostantiControlloTraffico.PDD_CONTEXT_LIST_API_OR_GLOBAL_OR_DEFAULT_VALUE_API.equals(policyAPI)) {
											policyConfiguration = policyConfigurationPorta;
										}
										else if(CostantiControlloTraffico.PDD_CONTEXT_LIST_API_OR_GLOBAL_OR_DEFAULT_VALUE_GLOBAL.equals(policyAPI)) {
											policyConfiguration = policyConfigurationGlobale;
										}
										else {
											/**CostantiControlloTraffico.PDD_CONTEXT_LIST_API_OR_GLOBAL_OR_DEFAULT_VALUE_DEFAULT;*/
											policyConfiguration = new PolicyConfiguration(true);
										} 
										IGestorePolicyAttive gestorePolicy = GestorePolicyAttive.getInstance(policyConfiguration.getType());
										
										IPolicyGroupByActiveThreads policyGroupByActiveThreads = gestorePolicy.getActiveThreadsPolicy(uniqueIdMap);
										
										if(times!=null) {
											sb = new StringBuilder(uniqueIdMap);
											sb.append("[");
											long timeEnd =  DateManager.getTimeMillis();
											long timeProcess = timeEnd-timeStart;
											sb.append(timeProcess);
											timeStart = DateManager.getTimeMillis();
										}
										
										org.openspcoop2.utils.Map<Object> mapContext = null;
										if(context!=null) {
											mapContext = context;
										}
										
										policyGroupByActiveThreads.
											registerStopRequest(logControlloTraffico, idTransazione, groupByPolicies.get(i),mapContext, 
													misurazioniTransazione, 
													incrementCounterPolicyApplicabile.get(i),
													incrementCounterPolicyViolata.get(i));
										
										if(times!=null) {
											sb.append(",");
											long timeEnd =  DateManager.getTimeMillis();
											long timeProcess = timeEnd-timeStart;
											sb.append(timeProcess);
											sb.append("]");
											times.getControlloTrafficoPolicyTimes().add(sb.toString());
										}
										
									}catch(PolicyNotFoundException notFound){
										logControlloTraffico.debug("NotFoundException durante la registrazione di terminazione del thread (policy inspection: "+uniqueIdMap+")",notFound);
									}catch(PolicyShutdownException shutdown){
										logControlloTraffico.debug("PolicyShutdownException durante la registrazione di terminazione del thread (policy inspection: "+uniqueIdMap+")",shutdown);
									}	
									catch(Throwable error){
										logControlloTraffico.error("Errore durante la registrazione di terminazione del thread (policy inspection: "+uniqueIdMap+"): "+error.getMessage(),error);
									}	
								}
							}									

						}
					}
				}
				
			}
		} catch (Exception e) {
			logger.error("["+idTransazione+"] Errore durante la registrazione di terminazione del thread (policy inspection)",e);
		}
	}
	
}
