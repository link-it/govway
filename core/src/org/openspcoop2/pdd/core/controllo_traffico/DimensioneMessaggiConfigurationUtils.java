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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive;
import org.openspcoop2.core.controllo_traffico.IdActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiTransazione;
import org.openspcoop2.core.controllo_traffico.beans.UniqueIdentifierUtilities;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsaPolicyAttiva;
import org.openspcoop2.core.controllo_traffico.utils.PolicyUtilities;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.CostantiProprieta;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.controllo_traffico.policy.InterceptorPolicyUtilities;
import org.openspcoop2.pdd.core.controllo_traffico.policy.PolicyFiltroApplicativoUtilities;
import org.openspcoop2.pdd.core.controllo_traffico.policy.PolicyVerifier;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.URLProtocolContext;
import org.slf4j.Logger;

/**     
 * DimensioneMessaggiConfigurationUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DimensioneMessaggiConfigurationUtils {

	private DimensioneMessaggiConfigurationUtils() {}
	
	private static void logDebug(Logger log, String msg) {
		if(log!=null) {
			log.debug(msg);
		}
	}
	
	public static SoglieDimensioneMessaggi readSoglieDimensioneMessaggi(TipoPdD tipoPdD, String nomePorta, 
			DatiTransazione datiTransazione, Logger log,
			URLProtocolContext urlProtocolContext, RequestInfo requestInfo, PdDContext pddContext,
			IProtocolFactory<?> protocolFactory) throws CoreException {
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		ConfigurazioneGatewayControlloTraffico configurazioneControlloTraffico =  op2Properties.getConfigurazioneControlloTraffico();
		ConfigurazionePdDManager configPdDManager = ConfigurazionePdDManager.getInstance();
		
		SoglieDimensioneMessaggi soglie = null;
		
		Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> mapPolicyAttive = null;
		boolean mapFromRequestInfo = false;
		try{
		
			if(requestInfo!=null && requestInfo.getRequestRateLimitingConfig()!=null) {
				if(requestInfo.getRequestRateLimitingConfig().getNomePorta()==null && nomePorta!=null) {
					requestInfo.getRequestRateLimitingConfig().setDatiPorta(tipoPdD, nomePorta, 
							datiTransazione!=null ? datiTransazione.getIdServizio() :  null,
							datiTransazione!=null ? datiTransazione.getSoggettoFruitore() : null);
				}
				if(requestInfo.getRequestRateLimitingConfig().getMapPolicyAttive_dimensioneMessaggi()!=null) {
					mapPolicyAttive = requestInfo.getRequestRateLimitingConfig().getMapPolicyAttive_dimensioneMessaggi();
					mapFromRequestInfo = true;
				}
			}
			
			if(!mapFromRequestInfo) {
				
				mapPolicyAttive = new HashMap<>();
				
				if(tipoPdD!=null && nomePorta!=null) {
					Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> mapPolicyAttiveAPI = null;
					try {
						mapPolicyAttiveAPI = configPdDManager.getElencoIdPolicyAttiveAPIDimensioneMessaggio(configurazioneControlloTraffico.isPolicyReadedWithDynamicCache(),
								tipoPdD, nomePorta);
					}catch(DriverConfigurazioneNotFound notFound) {
						// ignore
					}
					if(mapPolicyAttiveAPI!=null && !mapPolicyAttiveAPI.isEmpty()) {
						Iterator<TipoRisorsaPolicyAttiva> it = mapPolicyAttiveAPI.keySet().iterator();
						while (it.hasNext()) {
							TipoRisorsaPolicyAttiva tipoRisorsaPolicyAttiva = it.next();
							ElencoIdPolicyAttive elencoPolicyAttiveAPI = mapPolicyAttiveAPI.get(tipoRisorsaPolicyAttiva);
							if(elencoPolicyAttiveAPI!=null) {
								ElencoIdPolicyAttive cloned = (ElencoIdPolicyAttive) elencoPolicyAttiveAPI.clone(); // altrimenti dopo aggiungendo quelle globali si modifica l'oggetto in cache
								mapPolicyAttive.put(tipoRisorsaPolicyAttiva, cloned);
							}
						}
					}
				}
		
				Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> mapPolicyAttiveGlobali = null;
				try {
					mapPolicyAttiveGlobali = configPdDManager.getElencoIdPolicyAttiveGlobaliDimensioneMessaggio(configurazioneControlloTraffico.isPolicyReadedWithDynamicCache());
				}catch(DriverConfigurazioneNotFound notFound) {
					// ignore
				}
				if(mapPolicyAttiveGlobali!=null && !mapPolicyAttiveGlobali.isEmpty()) {
					Iterator<TipoRisorsaPolicyAttiva> it = mapPolicyAttiveGlobali.keySet().iterator();
					while (it.hasNext()) {
						TipoRisorsaPolicyAttiva tipoRisorsaPolicyAttiva = it.next();
						ElencoIdPolicyAttive elencoPolicyAttiveGlobali = mapPolicyAttiveGlobali.get(tipoRisorsaPolicyAttiva);
						
						if(mapPolicyAttive.containsKey(tipoRisorsaPolicyAttiva)) {
							ElencoIdPolicyAttive elencoPolicyAttiveCloned = mapPolicyAttive.get(tipoRisorsaPolicyAttiva); // gia' clonato per l'api sopra
							for (IdActivePolicy idActivePolicyGlobale : elencoPolicyAttiveGlobali.getIdActivePolicyList()) {
								elencoPolicyAttiveCloned.addIdActivePolicy(idActivePolicyGlobale);
							}
						}
						else {
							ElencoIdPolicyAttive cloned = (ElencoIdPolicyAttive) elencoPolicyAttiveGlobali.clone(); // per non modificare l'oggetto in cache
							mapPolicyAttive.put(tipoRisorsaPolicyAttiva, cloned);
						}
					}
				}
				
				if(requestInfo!=null && requestInfo.getRequestRateLimitingConfig()!=null) {
					requestInfo.getRequestRateLimitingConfig().setMapPolicyAttive_dimensioneMessaggi(mapPolicyAttive);
				}
			}
		}catch(Exception e){
			throw new CoreException("Configurazione non disponibile: "+e.getMessage(), e);
		}
		
		try{
			if(mapPolicyAttive!=null && !mapPolicyAttive.isEmpty()){
				
				int policyTotali = 0;
				int policyDisabilitate = 0;
				int policyFiltrate = 0;
				/**int policyNonApplicabili = 0;*/
				int policyRispettate = 0;
				/**int policyViolate = 0;*/
				/**int policyViolateWarningOnly = 0;*/
				int policyInErrore = 0;
				
				Iterator<TipoRisorsaPolicyAttiva> it = mapPolicyAttive.keySet().iterator();
				while (it.hasNext()) {
					TipoRisorsaPolicyAttiva tipoRisorsaPolicyAttiva = it.next();
					
					if(!TipoRisorsaPolicyAttiva.DIMENSIONE_MASSIMA_MESSAGGIO.equals(tipoRisorsaPolicyAttiva)) {
						// non dovrebbe capitare mai
						continue;
					}
					
					ElencoIdPolicyAttive elencoPolicyAttivePerRisorsa = mapPolicyAttive.get(tipoRisorsaPolicyAttiva);
					
					boolean policyRispettataCheRichiedeBreak = false;
					boolean policyViolataBreak = false;
										
					for (IdActivePolicy idActive : elencoPolicyAttivePerRisorsa.getIdActivePolicyList()) {
						
						policyTotali++;
						
						try{
							
							if(idActive.isEnabled()){
							
								// Prelevo la configurazione del Controllo del Traffico per quanto concerne le policy attive
								AttivazionePolicy attivazionePolicy = null;
								try {
									String id = UniqueIdentifierUtilities.getUniqueId(idActive);
									if(mapFromRequestInfo) {
										attivazionePolicy = requestInfo.getRequestRateLimitingConfig().getAttivazionePolicy_dimensioneMessaggi(id);
									}
									if(attivazionePolicy==null) {
										attivazionePolicy = configPdDManager.getAttivazionePolicy(configurazioneControlloTraffico.isPolicyReadedWithDynamicCache(), id);
										if(requestInfo!=null && requestInfo.getRequestRateLimitingConfig()!=null) {
											requestInfo.getRequestRateLimitingConfig().addAttivazionePolicy_dimensioneMessaggi(id, attivazionePolicy);
										}
									}
								}catch(DriverConfigurazioneNotFound notFound) {
									throw new CoreException("Istanza di Policy con id ["+idActive.getNome()+"] non esistente: "+notFound.getMessage(),notFound);
								}
								if(attivazionePolicy==null){
									throw new CoreException("Istanza di Policy con id ["+idActive.getNome()+"] non esistente?");
								}
								String alias = PolicyUtilities.getNomeActivePolicy(attivazionePolicy.getAlias(), attivazionePolicy.getIdActivePolicy());
								
								// Verifico se un eventuale filtro configurato per la policy matcha con i dati della transazione
								boolean matchFiltro = !policyRispettataCheRichiedeBreak && !policyViolataBreak && InterceptorPolicyUtilities.filter(attivazionePolicy.getFiltro(), datiTransazione, null, requestInfo);
								if(matchFiltro){
									
									if(attivazionePolicy.getFiltro().isEnabled() && attivazionePolicy.getFiltro().isInformazioneApplicativaEnabled()){
									
										// Verifico Filtro Applicativo
										OpenSPCoop2Message message = null;
										String soapActionParam = null;
										String valorePresente = PolicyFiltroApplicativoUtilities.getValore(log,
												attivazionePolicy.getFiltro().getInformazioneApplicativaTipo(), 
												attivazionePolicy.getFiltro().getInformazioneApplicativaNome(), 
												datiTransazione, true,
												message, urlProtocolContext, soapActionParam, pddContext,
												null);

										if(valorePresente==null || !valorePresente.equals(attivazionePolicy.getFiltro().getInformazioneApplicativaValore())){
											
											policyFiltrate++;
											
											// Emetto Diagnostico Policy Filtrata
											if(valorePresente==null){
												valorePresente = "n.d.";
											}
											logDebug(log,"Filtro Applicativo atteso ["+
													attivazionePolicy.getFiltro().getInformazioneApplicativaValore()+"] differente da quello estratto dalla transazione ["+
													valorePresente+"]");
											
											matchFiltro = false;
											
										}
										
									}
									
									if(matchFiltro){
											
										if(soglie==null) {
											soglie = new SoglieDimensioneMessaggi();
										}
										long sogliaRichiesta = -1;
										long sogliaRisposta = -1;
										if(attivazionePolicy.isRidefinisci()) {
											if(attivazionePolicy.getValore2()!=null) {
												sogliaRichiesta = attivazionePolicy.getValore2();
											}
											if(attivazionePolicy.getValore()!=null) {
												sogliaRisposta = attivazionePolicy.getValore();
											}
										}
										else {
										
											// Prelevo la configurazione del Controllo del Traffico per quanto concerne le policy istanziata
											ConfigurazionePolicy configurazionePolicy = null;
											try {
												String id = idActive.getIdPolicy();
												if(mapFromRequestInfo) {
													configurazionePolicy = requestInfo.getRequestRateLimitingConfig().getConfigurazionePolicy_dimensioneMessaggi(id);
												}
												if(configurazionePolicy==null) {
													configurazionePolicy = configPdDManager.getConfigurazionePolicy(configurazioneControlloTraffico.isPolicyReadedWithDynamicCache(), id);
													if(requestInfo!=null && requestInfo.getRequestRateLimitingConfig()!=null) {
														requestInfo.getRequestRateLimitingConfig().addConfigurazionePolicy_dimensioneMessaggi(id, configurazionePolicy);
													}
												}
											}catch(DriverConfigurazioneNotFound notFound) {
												throw new CoreException("Policy con id ["+idActive.getIdPolicy()+"] non esistente: "+notFound.getMessage(),notFound);
											}
											if(configurazionePolicy==null){
												throw new CoreException("Policy con id ["+idActive.getIdPolicy()+"] non esistente?");
											}
										
											if(configurazionePolicy.getValore2()!=null) {
												sogliaRichiesta = configurazionePolicy.getValore2();
											}
											if(configurazionePolicy.getValore()!=null) {
												sogliaRisposta = configurazionePolicy.getValore();
											}
										}
										
										if(sogliaRichiesta>0) {
											boolean updateRichiesta = false;
											if(soglie.getRichiesta()==null 
													||
													(sogliaRichiesta<soglie.getRichiesta().getSogliaKb())
													) {
												updateRichiesta = true;
											}
											if(updateRichiesta) {
												soglie.setRichiesta(build(sogliaRichiesta, attivazionePolicy, protocolFactory, configPdDManager, requestInfo, tipoPdD, nomePorta));
											}
										}
										
										if(sogliaRisposta>0) {
											boolean updateRisposta = false;
											if(soglie.getRisposta()==null
													||
													(sogliaRisposta<soglie.getRisposta().getSogliaKb())
													) {
												updateRisposta = true;
											}
											if(updateRisposta) {
												soglie.setRisposta(build(sogliaRisposta, attivazionePolicy, protocolFactory, configPdDManager, requestInfo, tipoPdD, nomePorta));
											}
										}
										
										policyRispettate++;
											
										if(!attivazionePolicy.isContinuaValutazione()) {
											policyRispettataCheRichiedeBreak = true;
										}

									}
									
								}
								else{
									
									policyFiltrate++;
									
									// Emetto Diagnostico Policy Filtrata
									if(policyRispettataCheRichiedeBreak) {
										logDebug(log,"[policy: "+alias+"] "+GeneratoreMessaggiErrore.TEMPLATE_POLICY_FILTRATA_MOTIVO_BREAK);
									}
									else if(policyViolataBreak) {
										logDebug(log,"[policy: "+alias+"] "+GeneratoreMessaggiErrore.TEMPLATE_POLICY_FILTRATA_MOTIVO_BREAK_VIOLATA);
									}
									else {
										logDebug(log,"[policy: "+alias+"] "+GeneratoreMessaggiErrore.TEMPLATE_POLICY_FILTRATA_MOTIVO_FILTRO+ PolicyUtilities.toStringFilter(attivazionePolicy.getFiltro()));
									}
									
								}
								
							}
							else{
								
								policyDisabilitate++;
								
								// Emetto Diagnostico Policy Disabilitata
								logDebug(log,"[policy: "+idActive.getNome()+"] policy disabilitata");
								
							}
							
						}
						catch(Throwable e){
							
							policyInErrore++;

							// Emetto Diagnostico Policy in Errore
							log.error("Errore durante il controllo della policy con id["+idActive.getNome()+"]: "+e.getMessage(),e);
							
						}
					}
				
				}
			
				logDebug(log,"Valutazione policy totali["+policyTotali+"] disabilitate["+policyDisabilitate+"] filtrate["+policyFiltrate+"] utilizzate["+policyRispettate+"] inErrore["+policyInErrore+"]");
								
			}
		}catch(Exception e){
			throw new CoreException("Errore durante l'identificazione dei limiti sulla dimensione dei messaggi: "+e.getMessage(), e);
		}
		
		if(soglie!=null &&
			(soglie.getRichiesta()==null || soglie.getRisposta()==null) 
			){
			soglie = null;
		}
		
		return soglie;
	}
	
	private static SogliaDimensioneMessaggio build(long sogliaKb, AttivazionePolicy attivazionePolicy, IProtocolFactory<?> protocolFactory, ConfigurazionePdDManager configPdDManager, RequestInfo requestInfo,
			TipoPdD tipoPdD, String nomePorta) throws ProtocolException, DriverConfigurazioneException {
		SogliaDimensioneMessaggio soglia = new SogliaDimensioneMessaggio();
		soglia.setSogliaKb(sogliaKb);
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		soglia.setUseContentLengthHeader(op2Properties.isLimitedInputStreamUseContentLength());
		soglia.setUseContentLengthHeaderAcceptZeroValue(op2Properties.isLimitedInputStreamUseContentLengthAcceptZeroValue());
		
		boolean policyPorta = attivazionePolicy.getFiltro()!=null && attivazionePolicy.getFiltro().getEnabled() &&
				attivazionePolicy.getFiltro().getNomePorta()!=null &&
				attivazionePolicy.getFiltro().getRuoloPorta()!=null;
		soglia.setPolicyGlobale(!policyPorta);

		soglia.setNomePolicy(PolicyUtilities.getNomeActivePolicy(attivazionePolicy.getAlias(), attivazionePolicy.getIdActivePolicy()));
		
		String idPolicyConGruppo = null;
		String configurazione = null;
		String idAPI = null;
		if(!soglia.isPolicyGlobale()) {
			idAPI = PolicyVerifier.getIdAPI(attivazionePolicy, protocolFactory, configPdDManager, requestInfo);
		}
		idPolicyConGruppo = PolicyUtilities.buildIdConfigurazioneEventoPerPolicy(attivazionePolicy, null, idAPI);
		configurazione = PolicyUtilities.buildConfigurazioneEventoPerPolicy(attivazionePolicy, soglia.isPolicyGlobale());
		soglia.setIdPolicyConGruppo(idPolicyConGruppo);
		soglia.setConfigurazione(configurazione);
		
		IDPortaDelegata idPD = null;
		IDPortaApplicativa idPA = null;
		if(tipoPdD!=null && nomePorta!=null && StringUtils.isNotEmpty(nomePorta)) {
			if(TipoPdD.DELEGATA.equals(tipoPdD)) {
				idPD = new IDPortaDelegata();
				idPD.setNome(nomePorta);
			}
			else {
				idPA = new IDPortaApplicativa();
				idPA.setNome(nomePorta);
			}
		}
		else if(policyPorta) {
			if(org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy.DELEGATA.equals(attivazionePolicy.getFiltro().getRuoloPorta())) {
				idPD = new IDPortaDelegata();
				idPD.setNome(attivazionePolicy.getFiltro().getNomePorta());
			}
			else {
				idPA = new IDPortaApplicativa();
				idPA.setNome(attivazionePolicy.getFiltro().getNomePorta());
			}
		}
		
		List<Proprieta> listP = null;
		if(idPD!=null) {
			listP = readProperties(idPD, configPdDManager, requestInfo);
		}
		else {
			listP = readProperties(idPA, configPdDManager, requestInfo);
		}
		
		if(listP!=null && !listP.isEmpty()) {
			soglia.setUseContentLengthHeader(CostantiProprieta.isRateLimitingUseHttpContentLength(listP, soglia.isUseContentLengthHeader()));
			soglia.setUseContentLengthHeaderAcceptZeroValue(CostantiProprieta.isRateLimitingUseHttpContentLengthAcceptZeroValue(listP, soglia.isUseContentLengthHeaderAcceptZeroValue()));
		}
		
		return soglia;
	}
	private static List<Proprieta> readProperties(IDPortaDelegata idPD, ConfigurazionePdDManager configPdDManager, RequestInfo requestInfo) throws DriverConfigurazioneException {
		List<Proprieta> listP = null;
		PortaDelegata pd = configPdDManager.getPortaDelegataSafeMethod(idPD, requestInfo);
		if(pd!=null) {
			listP = pd.getProprieta();
		}
		return listP;
	}
	private static List<Proprieta> readProperties(IDPortaApplicativa idPA, ConfigurazionePdDManager configPdDManager, RequestInfo requestInfo) throws DriverConfigurazioneException {
		List<Proprieta> listP = null;
		PortaApplicativa pa = configPdDManager.getPortaApplicativaSafeMethod(idPA, requestInfo);
		if(pa!=null) {
			listP = pa.getProprieta();
		}
		return listP;
	}
	
}
