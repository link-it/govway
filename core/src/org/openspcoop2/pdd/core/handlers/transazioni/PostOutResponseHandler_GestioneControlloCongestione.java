package org.openspcoop2.pdd.core.handlers.transazioni;

import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.pdd.core.handlers.PostOutResponseContext;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.MisurazioniTransazione;
import org.openspcoop2.core.controllo_traffico.driver.PolicyNotFoundException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyShutdownException;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.controllo_traffico.CostantiControlloTraffico;
import org.openspcoop2.pdd.core.controllo_traffico.GeneratoreMessaggiErrore;
import org.openspcoop2.pdd.core.controllo_traffico.GestoreControlloTraffico;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.GestorePolicyAttive;

public class PostOutResponseHandler_GestioneControlloCongestione {

	public void process(Boolean controlloCongestioneMaxRequestThreadRegistrato, Logger logger, String idTransazione,
			Transazione transazioneDTO, PostOutResponseContext context){
		if(controlloCongestioneMaxRequestThreadRegistrato!=null && controlloCongestioneMaxRequestThreadRegistrato){
			// significa che sono entrato nel motore di anti-congestionamento
			try {
				
				Long maxThreads = null;
				Integer threshold = null;
				
				if(context!=null && context.getPddContext()!=null){
					Object maxThreadsObject = context.getPddContext().getObject(GeneratoreMessaggiErrore.TEMPLATE_MAX_THREADS_THRESHOLD);
					if(maxThreadsObject!=null && maxThreadsObject instanceof Long){
						maxThreads = (Long) maxThreadsObject;
					}
					
					Object thresholdObject = context.getPddContext().getObject(GeneratoreMessaggiErrore.TEMPLATE_CONTROLLO_TRAFFICO_THRESHOLD);
					if(thresholdObject!=null && maxThreadsObject instanceof Long){
						threshold = (Integer) thresholdObject;
					}
				}
				
				GestoreControlloTraffico.getInstance().removeThread(maxThreads,threshold);
			} catch (Exception e) {
				logger.error("["+idTransazione+"] Errore durante la rimozione del thread tra i threads attivi sulla Porta di Dominio",e);
			}
		}
		try {
			Logger logCongestione = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(OpenSPCoop2Properties.getInstance().isControlloTrafficoDebug());
			if(transazioneDTO!=null){
			
				Object objectId = context.getPddContext().removeObject(CostantiControlloTraffico.PDD_CONTEXT_LIST_UNIQUE_ID_POLICY);
				if(objectId!=null && (objectId instanceof List<?>) ){
					@SuppressWarnings("unchecked")
					List<String> uniqueIdsPolicies = (List<String>) objectId;
					if(uniqueIdsPolicies!=null && uniqueIdsPolicies.size()>0){
						Object object = context.getPddContext().removeObject(CostantiControlloTraffico.PDD_CONTEXT_LIST_GROUP_BY_CONDITION);
						Object objectIncrementCounter = context.getPddContext().removeObject(CostantiControlloTraffico.PDD_CONTEXT_LIST_POLICY_APPLICABILE);
						if(object!=null && (object instanceof List<?>) &&
								objectIncrementCounter!=null && (objectIncrementCounter instanceof List<?>)){
							@SuppressWarnings("unchecked")
							List<IDUnivocoGroupByPolicy> groupByPolicies = (List<IDUnivocoGroupByPolicy>) object; 
							@SuppressWarnings("unchecked")
							List<Boolean> incrementCounter = (List<Boolean>) objectIncrementCounter; 
							if(groupByPolicies!=null && groupByPolicies.size()==uniqueIdsPolicies.size() &&
									incrementCounter!=null && incrementCounter.size()==uniqueIdsPolicies.size()){
								
								MisurazioniTransazione misurazioniTransazione = new MisurazioniTransazione();
								misurazioniTransazione.setTipoPdD(context.getTipoPorta());
								misurazioniTransazione.setEsitoTransazione(context.getEsito().getCode());
								
								misurazioniTransazione.setDataIngressoRichiesta(transazioneDTO.getDataIngressoRichiesta());
								misurazioniTransazione.setDataUscitaRichiesta(transazioneDTO.getDataUscitaRichiesta());
								misurazioniTransazione.setDataIngressoRisposta(transazioneDTO.getDataIngressoRisposta());
								misurazioniTransazione.setDataUscitaRisposta(transazioneDTO.getDataUscitaRisposta());
								
								misurazioniTransazione.setRichiestaIngressoBytes(transazioneDTO.getRichiestaIngressoBytes());
								misurazioniTransazione.setRichiestaUscitaBytes(transazioneDTO.getRichiestaUscitaBytes());
								misurazioniTransazione.setRispostaIngressoBytes(transazioneDTO.getRispostaIngressoBytes());
								misurazioniTransazione.setRispostaUscitaBytes(transazioneDTO.getRispostaUscitaBytes());
								
								for (int i = 0; i < uniqueIdsPolicies.size(); i++) {
									String uniqueIdMap = null;
									try{
										uniqueIdMap = uniqueIdsPolicies.get(i);
										GestorePolicyAttive.getInstance().getActiveThreadsPolicy(uniqueIdMap).
											registerStopRequest(logCongestione, groupByPolicies.get(i), misurazioniTransazione, incrementCounter.get(i));
									}catch(PolicyNotFoundException notFound){
										logCongestione.debug("NotFoundException durante la registrazione di terminazione del thread (policy inspection: "+uniqueIdMap+")",notFound);
									}catch(PolicyShutdownException shutdown){
										logCongestione.debug("PolicyShutdownException durante la registrazione di terminazione del thread (policy inspection: "+uniqueIdMap+")",shutdown);
									}	
									catch(Throwable error){
										logCongestione.error("Errore durante la registrazione di terminazione del thread (policy inspection: "+uniqueIdMap+"): "+error.getMessage(),error);
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
