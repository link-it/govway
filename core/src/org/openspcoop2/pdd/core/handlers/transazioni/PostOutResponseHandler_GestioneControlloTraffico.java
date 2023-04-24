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
package org.openspcoop2.pdd.core.handlers.transazioni;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.pdd.core.handlers.PostOutResponseContext;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.date.DateManager;
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

/**     
 * PostOutResponseHandler_GestioneControlloTraffico
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PostOutResponseHandler_GestioneControlloTraffico {

	public void process(Boolean controlloTrafficoMaxRequestThreadRegistrato, Logger logger, String idTransazione,
			Transazione transazioneDTO, PostOutResponseContext context,
			TransazioniProcessTimes times){
		
		long timeStart = -1;
		
		if(controlloTrafficoMaxRequestThreadRegistrato!=null && controlloTrafficoMaxRequestThreadRegistrato){
			// significa che sono entrato nel motore di anti-congestionamento
			if(times!=null) {
				timeStart = DateManager.getTimeMillis();
			}
			try {
				
				Long maxThreads = null;
				Integer threshold = null;
				
				if(context!=null && context.getPddContext()!=null){
					Object maxThreadsObject = context.getPddContext().getObject(GeneratoreMessaggiErrore.PDD_CONTEXT_MAX_THREADS_THRESHOLD);
					if(maxThreadsObject!=null && maxThreadsObject instanceof Long){
						maxThreads = (Long) maxThreadsObject;
					}
					
					Object thresholdObject = context.getPddContext().getObject(GeneratoreMessaggiErrore.PDD_CONTEXT_CONTROLLO_TRAFFICO_THRESHOLD);
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
				times.controlloTraffico_removeThread = timeProcess;
			}
		}
		try {
			Logger logControlloTraffico = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(OpenSPCoop2Properties.getInstance().isControlloTrafficoDebug());
			if(transazioneDTO!=null){
			
				if(times!=null) {
					timeStart = DateManager.getTimeMillis();
				}
				
				Object objectId = null;
				if(context!=null && context.getPddContext()!=null) {
					objectId = context.getPddContext().removeObject(CostantiControlloTraffico.PDD_CONTEXT_LIST_UNIQUE_ID_POLICY);
				}
				if(context!=null && context.getPddContext()!=null &&
						objectId!=null && (objectId instanceof List<?>) ){
					@SuppressWarnings("unchecked")
					List<String> uniqueIdsPolicies = (List<String>) objectId;
					if(uniqueIdsPolicies!=null && uniqueIdsPolicies.size()>0){
						Object object = context.getPddContext().removeObject(CostantiControlloTraffico.PDD_CONTEXT_LIST_GROUP_BY_CONDITION);
						Object objectIncrementCounter_policyApplicabile = context.getPddContext().removeObject(CostantiControlloTraffico.PDD_CONTEXT_LIST_POLICY_APPLICABILE);
						Object objectIncrementCounter_policyViolata = context.getPddContext().removeObject(CostantiControlloTraffico.PDD_CONTEXT_LIST_POLICY_VIOLATA);
						Object object_policyTypes = context.getPddContext().removeObject(CostantiControlloTraffico.PDD_CONTEXT_LIST_API_OR_GLOBAL_OR_DEFAULT);
						Object object_policyConfiguration_porta = context.getPddContext().removeObject(CostantiControlloTraffico.PDD_CONTEXT_POLICY_CONFIG_PORTA);
						Object object_policyConfiguration_globale = context.getPddContext().removeObject(CostantiControlloTraffico.PDD_CONTEXT_POLICY_CONFIG_GLOBALE);
						if(object!=null && (object instanceof List<?>) &&
								objectIncrementCounter_policyApplicabile!=null && (objectIncrementCounter_policyApplicabile instanceof List<?>) &&
								objectIncrementCounter_policyViolata!=null && (objectIncrementCounter_policyViolata instanceof List<?>) &&
								object_policyTypes!=null && (object_policyTypes instanceof List<?>) &&
								object_policyConfiguration_globale!=null && (object_policyConfiguration_globale instanceof PolicyConfiguration) &&
								object_policyConfiguration_porta!=null && (object_policyConfiguration_porta instanceof PolicyConfiguration)){
							@SuppressWarnings("unchecked")
							List<IDUnivocoGroupByPolicy> groupByPolicies = (List<IDUnivocoGroupByPolicy>) object; 
							@SuppressWarnings("unchecked")
							List<Boolean> incrementCounter_policyApplicabile = (List<Boolean>) objectIncrementCounter_policyApplicabile; 
							@SuppressWarnings("unchecked")
							List<Boolean> incrementCounter_policyViolata = (List<Boolean>) objectIncrementCounter_policyViolata; 
							@SuppressWarnings("unchecked")
							List<String> policyTypes = (List<String>) object_policyTypes; 
							PolicyConfiguration policyConfiguration_globale = (PolicyConfiguration) object_policyConfiguration_globale;
							PolicyConfiguration policyConfiguration_porta = (PolicyConfiguration) object_policyConfiguration_porta;
							if(groupByPolicies!=null && groupByPolicies.size()==uniqueIdsPolicies.size() &&
									incrementCounter_policyApplicabile!=null && incrementCounter_policyApplicabile.size()==uniqueIdsPolicies.size() &&
									incrementCounter_policyViolata!=null && incrementCounter_policyViolata.size()==uniqueIdsPolicies.size() &&
									policyTypes!=null && policyTypes.size()==uniqueIdsPolicies.size() &&
									policyConfiguration_globale!=null &&
									policyConfiguration_porta!=null) {
								
								MisurazioniTransazione misurazioniTransazione = new MisurazioniTransazione();
								misurazioniTransazione.setTipoPdD(context.getTipoPorta());
								misurazioniTransazione.setProtocollo( context.getProtocolFactory().getProtocol());
								misurazioniTransazione.setEsitoTransazione(context.getEsito().getCode());
								
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
									times.controlloTraffico_preparePolicy = timeProcess;
								
									if(!uniqueIdsPolicies.isEmpty()) {
										times.controlloTraffico_policyTimes = new ArrayList<>();
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
											policyConfiguration = policyConfiguration_porta;
										}
										else if(CostantiControlloTraffico.PDD_CONTEXT_LIST_API_OR_GLOBAL_OR_DEFAULT_VALUE_GLOBAL.equals(policyAPI)) {
											policyConfiguration = policyConfiguration_globale;
										}
										else {
											//CostantiControlloTraffico.PDD_CONTEXT_LIST_API_OR_GLOBAL_OR_DEFAULT_VALUE_DEFAULT;
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
											mapContext = context.getPddContext();
										}
										
										policyGroupByActiveThreads.
											registerStopRequest(logControlloTraffico, idTransazione, groupByPolicies.get(i),mapContext, 
													misurazioniTransazione, 
													incrementCounter_policyApplicabile.get(i),
													incrementCounter_policyViolata.get(i));
										
										if(times!=null) {
											sb.append(",");
											long timeEnd =  DateManager.getTimeMillis();
											long timeProcess = timeEnd-timeStart;
											sb.append(timeProcess);
											sb.append("]");
											times.controlloTraffico_policyTimes.add(sb.toString());
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
