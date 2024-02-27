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

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive;
import org.openspcoop2.core.controllo_traffico.IdActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiTransazione;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.UniqueIdentifierUtilities;
import org.openspcoop2.core.controllo_traffico.constants.Costanti;
import org.openspcoop2.core.controllo_traffico.constants.TipoErrore;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsa;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsaPolicyAttiva;
import org.openspcoop2.core.controllo_traffico.driver.IGestorePolicyAttive;
import org.openspcoop2.core.controllo_traffico.utils.PolicyUtilities;
import org.openspcoop2.core.eventi.constants.CodiceEventoControlloTraffico;
import org.openspcoop2.core.eventi.constants.TipoEvento;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.controllo_traffico.ConfigurazioneGatewayControlloTraffico;
import org.openspcoop2.pdd.core.controllo_traffico.ConnettoreUtilities;
import org.openspcoop2.pdd.core.controllo_traffico.CostantiControlloTraffico;
import org.openspcoop2.pdd.core.controllo_traffico.DatiTempiRisposta;
import org.openspcoop2.pdd.core.controllo_traffico.GeneratoreMessaggiDiagnostici;
import org.openspcoop2.pdd.core.controllo_traffico.GeneratoreMessaggiErrore;
import org.openspcoop2.pdd.core.controllo_traffico.GestoreControlloTraffico;
import org.openspcoop2.pdd.core.controllo_traffico.RisultatoVerificaPolicy;
import org.openspcoop2.pdd.core.controllo_traffico.StatoTraffico;
import org.openspcoop2.pdd.core.controllo_traffico.policy.InterceptorPolicyUtilities;
import org.openspcoop2.pdd.core.controllo_traffico.policy.PolicyFiltroApplicativoUtilities;
import org.openspcoop2.pdd.core.controllo_traffico.policy.PolicyVerifier;
import org.openspcoop2.pdd.core.controllo_traffico.policy.config.PolicyConfiguration;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.GestorePolicyAttive;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.InRequestProtocolContext;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionDeletedException;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.utils.Utilities;
import org.slf4j.Logger;

/**     
 * InRequestProtocolHandlerGestioneControlloTraffico
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InRequestProtocolHandlerGestioneControlloTraffico {

	private static java.util.Random rndEngine = null;
	private static synchronized void initRandom() {
		if(rndEngine==null) {
			rndEngine = new SecureRandom();
		}
	}
	public static java.util.Random getRandom() {
		if(rndEngine==null) {
			initRandom();
		}
		return rndEngine;
	}
	
	public void process(InRequestProtocolContext context, Transaction tr) throws HandlerException{
		
		Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> mapPolicyAttive = null;
		boolean mapFromRequestInfo = false;
		
		DatiTransazione datiTransazione = null;
				
		IGestorePolicyAttive gestorePolicyGlobale = null;
		IGestorePolicyAttive gestorePolicyPorta = null;
		PolicyConfiguration policyConfigurationGlobale = null;
		PolicyConfiguration policyConfigurationPorta = null;
		PolicyConfiguration policyConfigurationHeaderHttp = null;
		
		GestoreControlloTraffico gestoreControlloTraffico = null;
		ConfigurazionePdDManager configPdDManager = null;
		
		TipoErrore tipoErrore = TipoErrore.FAULT;
		boolean includiDescrizioneErrore = true;
		
		ConfigurazioneGatewayControlloTraffico configurazioneControlloTraffico = null;
		
		Logger log = null;
		
		ServiceBinding serviceBinding = ServiceBinding.REST;
		
		RequestInfo requestInfo = null;
		if(context!=null && context.getPddContext()!=null && context.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.REQUEST_INFO)){
			requestInfo = (RequestInfo) context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		}
		
		boolean registerThread = true;
		OpenSPCoop2Properties op2Properties = null;
		try{
			
			// ConfigurazioneControlloTraffico
			op2Properties = OpenSPCoop2Properties.getInstance();
			configurazioneControlloTraffico =  op2Properties.getConfigurazioneControlloTraffico();
			
			// Logger
			log = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(op2Properties.isControlloTrafficoDebug());
			
			if(context==null) {
				throw new CoreException("InRequestProtocolContext is null");
			}
			
			// Leggo i dati del servizio
			datiTransazione = InterceptorPolicyUtilities.readDatiTransazione(context);
			registerThread = InterceptorPolicyUtilities.checkRegisterThread(datiTransazione);
			// Se registerThread == false, sicuramente la richiesta non verra' veicolata fuori e terminerà con un errore all'interno della PdD.

			if(context!=null && context.getMessaggio()!=null && context.getMessaggio().getServiceBinding()!=null) {
				serviceBinding = context.getMessaggio().getServiceBinding();
			}
			
			if(registerThread){				
				
				configPdDManager = ConfigurazionePdDManager.getInstance(context.getStato());
								
				// Prelevo la configurazione del Controllo del Traffico per quanto concerne le policy attive
				
				if(requestInfo!=null && requestInfo.getRequestRateLimitingConfig()!=null) {
					if(requestInfo.getRequestRateLimitingConfig().getNomePorta()==null && datiTransazione!=null) {
						requestInfo.getRequestRateLimitingConfig().setDatiPorta(context.getTipoPorta(), datiTransazione.getNomePorta(), 
								datiTransazione.getIdServizio(),
								datiTransazione.getSoggettoFruitore());
					}
					if(requestInfo.getRequestRateLimitingConfig().getMapPolicyAttive()!=null) {
						mapPolicyAttive = requestInfo.getRequestRateLimitingConfig().getMapPolicyAttive();
						mapFromRequestInfo = true;
					}
				}
				
				if(!mapFromRequestInfo) {
				
					mapPolicyAttive = new HashMap<>();
					
					Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> mapPolicyAttiveAPI = null;
					try {
						mapPolicyAttiveAPI = configPdDManager.getElencoIdPolicyAttiveAPI(configurazioneControlloTraffico.isPolicyReadedWithDynamicCache(),
								context.getTipoPorta(), datiTransazione.getNomePorta());
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
	
					Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> mapPolicyAttiveGlobali = null;
					try {
						mapPolicyAttiveGlobali = configPdDManager.getElencoIdPolicyAttiveGlobali(configurazioneControlloTraffico.isPolicyReadedWithDynamicCache());
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
						requestInfo.getRequestRateLimitingConfig().setMapPolicyAttive(mapPolicyAttive);
					}
				
				}

				
				// ** ConfigurazionePolicy per gestore **
				
				policyConfigurationGlobale = configPdDManager.getConfigurazionePolicyRateLimitingGlobali(requestInfo);
				context.getPddContext().addObject(CostantiControlloTraffico.PDD_CONTEXT_POLICY_CONFIG_GLOBALE, policyConfigurationGlobale);
								
				PolicyConfiguration policyConfigurationApiInternal = null;
				if(context.getIntegrazione()!=null && context.getIntegrazione().getIdPD()!=null){
					PortaDelegata pd = configPdDManager.getPortaDelegata(context.getIntegrazione().getIdPD(),requestInfo);
					policyConfigurationApiInternal = new PolicyConfiguration(pd.getProprietaRateLimitingList());
				}
				else if(context.getIntegrazione()!=null && context.getIntegrazione().getIdPA()!=null){
					PortaApplicativa pa = configPdDManager.getPortaApplicativa(context.getIntegrazione().getIdPA(),requestInfo);
					policyConfigurationApiInternal = new PolicyConfiguration(pa.getProprietaRateLimitingList());
				}
				if(policyConfigurationApiInternal==null) {
					throw new CoreException("Policy configuration not found");
				}
				else {
					if(policyConfigurationApiInternal.isGestionePolicyRidefinita()) {
						policyConfigurationPorta = policyConfigurationApiInternal;
					}
					else {
						policyConfigurationPorta = policyConfigurationGlobale;
					}
				}
				context.getPddContext().addObject(CostantiControlloTraffico.PDD_CONTEXT_POLICY_CONFIG_PORTA, policyConfigurationPorta);
				
				// ** ConfigurazionePolicy per header http **
				if(policyConfigurationApiInternal.isGestioneHttpHeadersRidefinita()) {
					policyConfigurationHeaderHttp = policyConfigurationApiInternal;
				}
				else {
					policyConfigurationHeaderHttp = policyConfigurationGlobale;
				}
				
				// Gestore delle Policy
				gestorePolicyPorta = GestorePolicyAttive.getInstance(policyConfigurationPorta.getType());
				gestorePolicyGlobale = GestorePolicyAttive.getInstance(policyConfigurationGlobale.getType());
				
				// Gestore del Traffico
				gestoreControlloTraffico = GestoreControlloTraffico.getInstance();
				
				// Configurazione Generale del RateLimiting
				ConfigurazioneGenerale configurazioneGenerale = configPdDManager.getConfigurazioneControlloTraffico(requestInfo);		
				if(configurazioneGenerale.getRateLimiting()!=null){
					if(configurazioneGenerale.getRateLimiting().getTipoErrore()!=null) {
						TipoErrore tipoErrorTmp = TipoErrore.toEnumConstant(configurazioneGenerale.getRateLimiting().getTipoErrore());
						if(tipoErrorTmp!=null) {
							tipoErrore = tipoErrorTmp;
						}
					}
					includiDescrizioneErrore = configurazioneGenerale.getRateLimiting().isTipoErroreIncludiDescrizione();
				}
				
			}
			
			/**System.out.println("PROCESS registerThread["+registerThread+"] datiControlloTraffico["+(datiControlloTraffico!=null)+"]");*/
			
		}catch(Exception e){
			throw new HandlerException("Configurazione non disponibile: "+e.getMessage(), e);
		}
		
		if(registerThread){ 

			
			try{
				

				MsgDiagnostico msgDiag = this.buildMsgDiagnostico(context);
				
				// Verifico se la Pdd è congestionata ed emetto l'opportuno diagnostico
				boolean pddCongestionata = 
						isCondizioneCongestionamentoPortaDominio(context, gestoreControlloTraffico, msgDiag, tr);
				
				
				// Effettuo controllo di rate limiting
				if(mapPolicyAttive!=null && !mapPolicyAttive.isEmpty()){
									
					// Emetto Diagnostico  Controllo di RateLimiting
					msgDiag.logPersonalizzato(GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_CONTROLLO_IN_CORSO);
					
					int policyTotali = 0;
					int policyDisabilitate = 0;
					int policyFiltrate = 0;
					int policyNonApplicabili = 0;
					int policyRispettate = 0;
					int policyViolate = 0;
					int policyViolateWarningOnly = 0;
					int policyInErrore = 0;
					
					List<RisultatoVerificaPolicy> policyBloccanti = new ArrayList<>();
					
					List<String> pddContextUniqueIdsPolicy = new ArrayList<>();
					List<IDUnivocoGroupByPolicy> pddContextIdUnivociGroupBy = new ArrayList<>();
					List<Boolean> pddContextPolicyApplicabile = new ArrayList<>();
					List<Boolean> pddContextPolicyViolata = new ArrayList<>();
					List<String> pddContextApi = new ArrayList<>();
					
					DatiTempiRisposta datiTempiRisposta = null;
					
					// NOTA: Questo blocco che gestisce la raccolta delle policy soddisfatte e violate per generare gli opportuni header potrebbe essere riscritto utilizzando TipoRisorsaPolicyAttiva
					// Per adesso non viene modificato.
					List<RisultatoVerificaPolicy> risultatiVerificaPolicyViolate = new ArrayList<>();
					Map<TipoRisorsa,List<RisultatoVerificaPolicy>> risultatiVerificaPolicyRispettate = new HashMap<>();
					for (TipoRisorsa tipoRisorsa : TipoRisorsa.values()) {
						risultatiVerificaPolicyRispettate.put(tipoRisorsa, new ArrayList<>());
					}
					
					try{
						
						Iterator<TipoRisorsaPolicyAttiva> it = mapPolicyAttive.keySet().iterator();
						while (it.hasNext()) {
							TipoRisorsaPolicyAttiva tipoRisorsaPolicyAttiva = it.next();
							ElencoIdPolicyAttive elencoPolicyAttivePerRisorsa = mapPolicyAttive.get(tipoRisorsaPolicyAttiva);
						
							msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_ACTIVE_RISORSA, tipoRisorsaPolicyAttiva.getValue());
							
							boolean policyRispettataCheRichiedeBreak = false;
							boolean policyViolataBreak = false;
							
							for (IdActivePolicy idActive : elencoPolicyAttivePerRisorsa.getIdActivePolicyList()) {
								
								policyTotali++;
								
								msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_ACTIVE_ID, idActive.getNome());
														
								try{
								
									if(idActive.isEnabled()){
									
										// Prelevo la configurazione del Controllo del Traffico per quanto concerne le policy attive
										AttivazionePolicy attivazionePolicy = null;
										try {
											String id = UniqueIdentifierUtilities.getUniqueId(idActive);
											if(mapFromRequestInfo) {
												attivazionePolicy = requestInfo.getRequestRateLimitingConfig().getAttivazionePolicy(id);
											}
											if(attivazionePolicy==null) {
												attivazionePolicy = configPdDManager.getAttivazionePolicy(configurazioneControlloTraffico.isPolicyReadedWithDynamicCache(), id);
												if(requestInfo!=null && requestInfo.getRequestRateLimitingConfig()!=null) {
													requestInfo.getRequestRateLimitingConfig().addAttivazionePolicy(id, attivazionePolicy);
												}
											}
										}catch(DriverConfigurazioneNotFound notFound) {
											msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_ACTIVE_ALIAS, idActive.getNome());
											msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_ACTIVE_TIPO, "");
											throw new CoreException("Istanza di Policy con id ["+idActive.getNome()+"] non esistente: "+notFound.getMessage(),notFound);
										}
										if(attivazionePolicy==null){
											msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_ACTIVE_ALIAS, idActive.getNome());
											msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_ACTIVE_TIPO, "");
											throw new CoreException("Istanza di Policy con id ["+idActive.getNome()+"] non esistente?");
										}
										
										msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_ACTIVE_ALIAS,
												PolicyUtilities.getNomeActivePolicy(attivazionePolicy.getAlias(), attivazionePolicy.getIdActivePolicy()));
										if(attivazionePolicy.getFiltro()!=null && attivazionePolicy.getFiltro().getNomePorta()!=null && !"".equals(attivazionePolicy.getFiltro().getNomePorta())) {
											msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_ACTIVE_TIPO, Costanti.POLICY_API);
										}
										else {
											msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_ACTIVE_TIPO, Costanti.POLICY_GLOBALE);
										}
										
										
										// Verifico se un eventuale filtro configurato per la policy matcha con i dati della transazione
										boolean matchFiltro = !policyRispettataCheRichiedeBreak && !policyViolataBreak && InterceptorPolicyUtilities.filter(attivazionePolicy.getFiltro(), datiTransazione, context.getStato(), requestInfo);
										if(matchFiltro){
											
											if(attivazionePolicy.getFiltro().isEnabled() && attivazionePolicy.getFiltro().isInformazioneApplicativaEnabled()){
											
												// Verifico Filtro Applicativo
												String valorePresente = PolicyFiltroApplicativoUtilities.getValore(log,
														attivazionePolicy.getFiltro().getInformazioneApplicativaTipo(), 
														attivazionePolicy.getFiltro().getInformazioneApplicativaNome(), 
														context, datiTransazione, true);
												if(valorePresente==null || valorePresente.equals(attivazionePolicy.getFiltro().getInformazioneApplicativaValore())==false){
													
													policyFiltrate++;
													
													// Emetto Diagnostico Policy Filtrata
													if(valorePresente==null){
														valorePresente = "n.d.";
													}
													msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_FILTRATA_MOTIVO, "Filtro Applicativo atteso ["+
															attivazionePolicy.getFiltro().getInformazioneApplicativaValore()+"] differente da quello estratto dalla transazione ["+
															valorePresente+"]");
													GeneratoreMessaggiDiagnostici.emitDiagnostic(msgDiag, 
															GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_FILTRATA, log);
													
													matchFiltro = false;
													
												}
												
											}
											
											if(matchFiltro){
													
												// Prelevo la configurazione del Controllo del Traffico per quanto concerne le policy istanziata
												ConfigurazionePolicy configurazionePolicy = null;
												try {
													String id = idActive.getIdPolicy();
													if(mapFromRequestInfo) {
														configurazionePolicy = requestInfo.getRequestRateLimitingConfig().getConfigurazionePolicy(id);
													}
													if(configurazionePolicy==null) {
														configurazionePolicy = configPdDManager.getConfigurazionePolicy(configurazioneControlloTraffico.isPolicyReadedWithDynamicCache(), id);
														if(requestInfo!=null && requestInfo.getRequestRateLimitingConfig()!=null) {
															requestInfo.getRequestRateLimitingConfig().addConfigurazionePolicy(id, configurazionePolicy);
														}
													}
												}catch(DriverConfigurazioneNotFound notFound) {
													throw new CoreException("Policy con id ["+idActive.getIdPolicy()+"] non esistente: "+notFound.getMessage(),notFound);
												}
												if(configurazionePolicy==null){
													throw new CoreException("Policy con id ["+idActive.getIdPolicy()+"] non esistente?");
												}
												
												org.openspcoop2.core.controllo_traffico.beans.ActivePolicy activePolicy = 
														new org.openspcoop2.core.controllo_traffico.beans.ActivePolicy();
												activePolicy.setInstanceConfiguration(attivazionePolicy);
												activePolicy.setConfigurazionePolicy(configurazionePolicy);
												activePolicy.setTipoRisorsaPolicy(TipoRisorsa.toEnumConstant(configurazionePolicy.getRisorsa(), true));
												activePolicy.setConfigurazioneControlloTraffico(configurazioneControlloTraffico);
												
												
												// Creo Gruppo
												IDUnivocoGroupByPolicy idUnivocoGroupBy = InterceptorPolicyUtilities.convertToID(log,
														datiTransazione, attivazionePolicy.getGroupBy(), context);
												pddContextIdUnivociGroupBy.add(idUnivocoGroupBy);
												pddContextUniqueIdsPolicy.add(UniqueIdentifierUtilities.getUniqueId(attivazionePolicy));
												
												// Se la Policy richiede una condizione di applicabilità per degrado prestazionale
												// e non ho ancora recuperato l'informazione sui tempi lo faccio adesso
												if(datiTempiRisposta==null){
													datiTempiRisposta = ConnettoreUtilities.getDatiTempiRisposta(context, datiTransazione, requestInfo);
												}
																							
												// Verifico rispetto ai dati collezionati
												
												IGestorePolicyAttive gestorePolicy = null;
												PolicyConfiguration policyConfiguration = null;
												if(attivazionePolicy.getFiltro()!=null && attivazionePolicy.getFiltro().getNomePorta()!=null && !"".equals(attivazionePolicy.getFiltro().getNomePorta())) {
													gestorePolicy = gestorePolicyPorta;
													policyConfiguration = policyConfigurationPorta;
													TipoRisorsaPolicyAttiva tipoRisorsaPolicy = TipoRisorsaPolicyAttiva.getTipo(activePolicy.getTipoRisorsaPolicy(), configurazionePolicy.isSimultanee());
													if(!gestorePolicy.getType().isSupportedResource(tipoRisorsaPolicy)) {
														// caso in cui viene configurata una policy su una specifica erogazione/fruizione, senza che nella porta stessa venga ridefinito un gestore specifico, e a livello di configurazione globale viene poi impostato un gestore non compatibile
														log.error("Riscontrata policy '"+attivazionePolicy.getIdActivePolicy()+"' (alias:"+attivazionePolicy.getAlias()+") non compatibile con il tipo di gestore '"+gestorePolicy.getType()+"'; verrà utilizzato il gestore di default");
														policyConfiguration = new PolicyConfiguration(true);
														gestorePolicy = GestorePolicyAttive.getInstance(policyConfiguration.getType());
														pddContextApi.add(CostantiControlloTraffico.PDD_CONTEXT_LIST_API_OR_GLOBAL_OR_DEFAULT_VALUE_DEFAULT);
													}
													else {
														pddContextApi.add(CostantiControlloTraffico.PDD_CONTEXT_LIST_API_OR_GLOBAL_OR_DEFAULT_VALUE_API);
													}
												}
												else {
													gestorePolicy = gestorePolicyGlobale;
													policyConfiguration = policyConfigurationGlobale;
													pddContextApi.add(CostantiControlloTraffico.PDD_CONTEXT_LIST_API_OR_GLOBAL_OR_DEFAULT_VALUE_GLOBAL);
												}
												
												RisultatoVerificaPolicy risultatoVerifica = PolicyVerifier.verifica(
														configPdDManager, context.getProtocolFactory(),
														gestorePolicy, policyConfiguration, log,
														activePolicy,
														idUnivocoGroupBy, context.getPddContext(), 
														msgDiag, tr, datiTransazione, pddCongestionata, datiTempiRisposta,
														pddContextPolicyApplicabile, pddContextPolicyViolata,
														context.getStato());
												
												// Gestisco Risultato Verifica
												if(risultatoVerifica.isErroreGenerico()){
													
													policyInErrore++;
													
													// Emetto Diagnostico Policy in Errore
													log.error("Errore durante il controllo della policy con id["+idActive.getNome()+"]: "+risultatoVerifica.getDescrizione());
													msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_VIOLATA_MOTIVO, risultatoVerifica.getDescrizione());
													GeneratoreMessaggiDiagnostici.emitDiagnostic(msgDiag, 
															GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_IN_ERRORE, log);
													
													// Aggiungo tra le policy bloccanti
													policyBloccanti.add(risultatoVerifica);
													
												}
												else if(risultatoVerifica.isNonApplicabile()){
													
													policyNonApplicabili++;
													
													// Emetto Diagnostico Policy  Non Applicabile
													msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_NON_APPLICABILE_MOTIVO, risultatoVerifica.getDescrizione());
													GeneratoreMessaggiDiagnostici.emitDiagnostic(msgDiag, 
															GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_NON_APPLICABILE, log);
													
												}
												else if(risultatoVerifica.isViolata()){
												
													risultatiVerificaPolicyViolate.add(risultatoVerifica);
													
													if(risultatoVerifica.isWarningOnly()){
		
														policyViolateWarningOnly++;
														
														// Emetto Diagnostico Policy Violata in Warning Only
														msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_VIOLATA_MOTIVO, risultatoVerifica.getDescrizione());
														GeneratoreMessaggiDiagnostici.emitDiagnostic(msgDiag, 
																GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_VIOLATA_WARNING_ONLY, log);
														
													}
													else{
													
														policyViolate++;
														
														// Emetto Diagnostico Policy Violata
														msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_VIOLATA_MOTIVO, risultatoVerifica.getDescrizione());
														GeneratoreMessaggiDiagnostici.emitDiagnostic(msgDiag, 
																GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_VIOLATA, log);
														
														// Aggiungo tra le policy bloccanti
														policyBloccanti.add(risultatoVerifica);
														
													}
													
													policyViolataBreak = true;
												}
												else{
													
													msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_ACTIVE_CONTINUE, attivazionePolicy.isContinuaValutazione()+"");
																										
													risultatiVerificaPolicyRispettate.get(risultatoVerifica.getRisorsa()).add(risultatoVerifica);
													
													// Emetto Diagnostico Policy Rispettata
													GeneratoreMessaggiDiagnostici.emitDiagnostic(msgDiag, 
															GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_RISPETTATA, log);
													
													policyRispettate++;
													
													if(!attivazionePolicy.isContinuaValutazione()) {
														policyRispettataCheRichiedeBreak = true;
													}
													
												}
												
											}
											
										}
										else{
											
											policyFiltrate++;
											
											// Emetto Diagnostico Policy Filtrata
											if(policyRispettataCheRichiedeBreak) {
												msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_FILTRATA_MOTIVO, 
														GeneratoreMessaggiErrore.TEMPLATE_POLICY_FILTRATA_MOTIVO_BREAK);
											}
											else if(policyViolataBreak) {
												msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_FILTRATA_MOTIVO, 
														GeneratoreMessaggiErrore.TEMPLATE_POLICY_FILTRATA_MOTIVO_BREAK_VIOLATA);
											}
											else {
												msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_FILTRATA_MOTIVO, 
														GeneratoreMessaggiErrore.TEMPLATE_POLICY_FILTRATA_MOTIVO_FILTRO+ PolicyUtilities.toStringFilter(attivazionePolicy.getFiltro()));
											}
											GeneratoreMessaggiDiagnostici.emitDiagnostic(msgDiag, 
													GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_FILTRATA, log);
											
										}
										
									}
									else{
										
										policyDisabilitate++;
										
										// Emetto Diagnostico Policy Disabilitata
										GeneratoreMessaggiDiagnostici.emitDiagnostic(msgDiag, 
												GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_DISABILITATA, log);
										
									}
									
								}
								catch(Throwable e){
									
									policyInErrore++;
									
									// Messaggio di Errore
									Throwable tConMessaggio = Utilities.getInnerNotEmptyMessageException(e);
									String eMessaggio = tConMessaggio.getMessage();
									if(eMessaggio==null || "".equals(eMessaggio) || "null".equalsIgnoreCase(eMessaggio)){
										eMessaggio = "Internal Error"; // molto probabilmente si tratta di un null Pointer. Lo si vedra' nel logger
									}
									
									// Emetto Diagnostico Policy in Errore
									log.error("Errore durante il controllo della policy con id["+idActive.getNome()+"]: "+eMessaggio,e);
									msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_POLICY_VIOLATA_MOTIVO, eMessaggio);
									GeneratoreMessaggiDiagnostici.emitDiagnostic(msgDiag, 
											GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_IN_ERRORE, log);
									
									// Aggiungo tra le policy bloccanti
									RisultatoVerificaPolicy policyError = new RisultatoVerificaPolicy();
									policyError.setErroreGenerico(true);
									policyError.setDescrizione(eMessaggio);
									policyBloccanti.add(policyError);
									
								}
								
								// Effettuo clean dei dati dinamici della policy
								GeneratoreMessaggiDiagnostici.cleanPolicyValues(msgDiag);
								
								
							} // -- fine for delle policy di una risorsa
							
						} // -- fine iterazione per tipo di risorsa
						
					}finally{
						if(!pddContextIdUnivociGroupBy.isEmpty()){
							context.getPddContext().addObject(CostantiControlloTraffico.PDD_CONTEXT_LIST_GROUP_BY_CONDITION, pddContextIdUnivociGroupBy);
							context.getPddContext().addObject(CostantiControlloTraffico.PDD_CONTEXT_LIST_UNIQUE_ID_POLICY,pddContextUniqueIdsPolicy);
							context.getPddContext().addObject(CostantiControlloTraffico.PDD_CONTEXT_LIST_POLICY_APPLICABILE,pddContextPolicyApplicabile);
							context.getPddContext().addObject(CostantiControlloTraffico.PDD_CONTEXT_LIST_POLICY_VIOLATA,pddContextPolicyViolata);
							context.getPddContext().addObject(CostantiControlloTraffico.PDD_CONTEXT_LIST_API_OR_GLOBAL_OR_DEFAULT,pddContextApi);
						}
					}

					// Gestione header HTTP da aggiungere alla transazione
					
					Properties headerTrasportoRateLimiting = new Properties();
					if(!risultatiVerificaPolicyViolate.isEmpty()) {
						// calcolo tempo maggiore
						long ms = -1;
						Map<TipoRisorsa,List<RisultatoVerificaPolicy>> risultatiVerificaPolicyViolateMap = new HashMap<>();
						for (RisultatoVerificaPolicy risultatoVerificaPolicy : risultatiVerificaPolicyViolate) {
							
							List<RisultatoVerificaPolicy> l = null;
							if(risultatiVerificaPolicyViolateMap.containsKey(risultatoVerificaPolicy.getRisorsa())) {
								l = risultatiVerificaPolicyViolateMap.get(risultatoVerificaPolicy.getRisorsa());
							}
							else {
								l = new ArrayList<>();
								risultatiVerificaPolicyViolateMap.put(risultatoVerificaPolicy.getRisorsa(), l);
							}
							l.add(risultatoVerificaPolicy);
							
							if(risultatoVerificaPolicy.getMsBeforeResetCounters()!=null) {
								long tmp = risultatoVerificaPolicy.getMsBeforeResetCounters().longValue();
								if(tmp>ms) {
									ms = tmp;
								}
							}
						}
						if(ms>0) {
							// trasformo in secondi
							long sec = -1;
							if(ms>1000) {
								// trasformo in secondi
								sec = ms / 1000;
							}
							else if(ms>0) {
								// genero comunque l'header approssimando l'intervallo al secondo + backoff
								sec=1;
							}

							if(!policyConfigurationHeaderHttp.isDisabledHttpHeaders_retryAfter()) {
							
								// aggiungo backoff
								if(!policyConfigurationHeaderHttp.isForceDisabledHttpHeaders_retryAfter_backoff()) {
									if(policyConfigurationHeaderHttp.getForceHttpHeaders_retryAfter_backoff()>0) {
										sec = sec + getRandom().nextInt(policyConfigurationHeaderHttp.getForceHttpHeaders_retryAfter_backoff());
									}
									else {
										if(op2Properties.getControlloTrafficoRetryAfterHeader_randomBackoff()!=null && op2Properties.getControlloTrafficoRetryAfterHeader_randomBackoff()>0) {
											sec = sec + getRandom().nextInt(op2Properties.getControlloTrafficoRetryAfterHeader_randomBackoff());
										}
									}
								}
								String [] headers = op2Properties.getControlloTrafficoRetryAfterHeader();
								if(headers!=null && headers.length>0) {
									for (String header : headers) {
										headerTrasportoRateLimiting.put(header, sec+"");
									}
								}
							}
						}
						if(risultatiVerificaPolicyViolateMap.size()>0) {
							// produco anche i normali header
							gestioneHeaderRateLimit(risultatiVerificaPolicyViolateMap, headerTrasportoRateLimiting, op2Properties, policyConfigurationHeaderHttp);
						}
					}
					else if(risultatiVerificaPolicyRispettate.size()>0) {
						gestioneHeaderRateLimit(risultatiVerificaPolicyRispettate, headerTrasportoRateLimiting, op2Properties, policyConfigurationHeaderHttp);
					}
						
					//Aggiungo nel pddContext e poi li recupero nei servizi
					if(headerTrasportoRateLimiting.size()>0) {
						context.getPddContext().addObject(CostantiControlloTraffico.PDD_CONTEXT_HEADER_RATE_LIMITING,headerTrasportoRateLimiting);
					}
					
					
					// Emetto Diagnostico Terminazione Controllo Rate limiting
					
					msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY, policyTotali+"");
					context.getPddContext().addObject(GeneratoreMessaggiErrore.PDD_CONTEXT_NUMERO_POLICY, policyTotali);
										
					msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY_DISABILITATE, policyDisabilitate+"");
					context.getPddContext().addObject(GeneratoreMessaggiErrore.PDD_CONTEXT_NUMERO_POLICY_DISABILITATE, policyDisabilitate);
					
					msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY_FILTRATE, policyFiltrate+"");
					context.getPddContext().addObject(GeneratoreMessaggiErrore.PDD_CONTEXT_NUMERO_POLICY_FILTRATE, policyFiltrate);
					
					msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY_NON_APPLICATE, policyNonApplicabili+"");
					context.getPddContext().addObject(GeneratoreMessaggiErrore.PDD_CONTEXT_NUMERO_POLICY_NON_APPLICATE, policyNonApplicabili);
					
					msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY_RISPETTATE, policyRispettate+"");
					context.getPddContext().addObject(GeneratoreMessaggiErrore.PDD_CONTEXT_NUMERO_POLICY_RISPETTATE, policyRispettate);
					
					msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY_VIOLATE, policyViolate+"");
					context.getPddContext().addObject(GeneratoreMessaggiErrore.PDD_CONTEXT_NUMERO_POLICY_VIOLATE, policyViolate);
					
					msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY_VIOLATE_WARNING_ONLY, policyViolateWarningOnly+"");
					context.getPddContext().addObject(GeneratoreMessaggiErrore.PDD_CONTEXT_NUMERO_POLICY_VIOLATE_WARNING_ONLY, policyViolateWarningOnly);
					
					msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY_IN_ERRORE, policyInErrore+"");
					context.getPddContext().addObject(GeneratoreMessaggiErrore.PDD_CONTEXT_NUMERO_POLICY_IN_ERRORE, policyInErrore);
					
					if(policyViolate>0 || policyInErrore>0){
						msgDiag.logPersonalizzato(GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_CONTROLLO_TERMINATO_CON_ERRORE);
						
						GeneratoreMessaggiErrore.addPddContextInfo_ControlloTrafficoPolicyViolated(context.getPddContext(), false);
						
						HandlerException he = GeneratoreMessaggiErrore.getControlloTrafficoPolicyViolated(policyBloccanti,
								configurazioneControlloTraffico.isErroreGenerico(), context.getPddContext());
						he.setEmettiDiagnostico(false);
						GeneratoreMessaggiErrore.configureHandlerExceptionByTipoErrore(serviceBinding, he, tipoErrore, includiDescrizioneErrore,log);
						throw he;
					}
					else if(policyViolateWarningOnly>0){
						
						GeneratoreMessaggiErrore.addPddContextInfo_ControlloTrafficoPolicyViolated(context.getPddContext(), true);
						
						msgDiag.logPersonalizzato(GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_CONTROLLO_TERMINATO_CON_SUCCESSO);
						
					}
					else{
						msgDiag.logPersonalizzato(GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_POLICY_CONTROLLO_TERMINATO_CON_SUCCESSO);
					}
				}
					
			}
			catch(HandlerException e){
				throw e;
			}
			catch(Exception e){
					
				GeneratoreMessaggiErrore.addPddContextInfo_ControlloTrafficoGenericError(context.getPddContext());
				
				throw GeneratoreMessaggiErrore.getErroreProcessamento(e, context.getPddContext());

			}
			
		}

	}
	
	private void gestioneHeaderRateLimit(Map<TipoRisorsa,List<RisultatoVerificaPolicy>> risultatiVerificaPolicy, Properties headerTrasportoRateLimiting,
			OpenSPCoop2Properties op2Properties,
			PolicyConfiguration policyConfigurationPorta) {
		
		if(policyConfigurationPorta.isDisabledHttpHeaders()) {
			return;
		}
		
		for (TipoRisorsa tipoRisorsa : risultatiVerificaPolicy.keySet()) {

			List<RisultatoVerificaPolicy> risultato = risultatiVerificaPolicy.get(tipoRisorsa);
			// in presenza di piu' policy dello stesso tipo considero la policy che termina più lontano nel tempo: NO Sbagliato poiche' se una ha un numero minore non va bene.
			// Prendo a questo punto quella con il minore limite.
			
			RisultatoVerificaPolicy risultatoUtilizzato = null;
			RisultatoVerificaPolicy risultatoUtilizzatoSimultanee = null;
			List<RisultatoVerificaPolicy> altrePolicy = new ArrayList<>();
			List<RisultatoVerificaPolicy> altrePolicySimultanee = new ArrayList<>();
			
			for (RisultatoVerificaPolicy risultatoVerificaPolicy : risultato) {
				if(TipoRisorsa.NUMERO_RICHIESTE.equals(tipoRisorsa) && risultatoVerificaPolicy.isSimultanee()) {
					if(risultatoUtilizzatoSimultanee==null) {
						risultatoUtilizzatoSimultanee = risultatoVerificaPolicy;
					}
					else {
						long numeroRimasti = -1;
						if(risultatoUtilizzatoSimultanee.getMaxValue()!=null) {
							numeroRimasti = risultatoUtilizzatoSimultanee.getMaxValue().longValue();
						}
						long numeroCheck = -1;
						if(risultatoVerificaPolicy.getMaxValue()!=null) {
							numeroCheck = risultatoVerificaPolicy.getMaxValue().longValue();
						}
						boolean switchPolicy = false;
						if(numeroCheck>0) {
							if(numeroRimasti>0) {
								if(numeroCheck<numeroRimasti) {
									altrePolicySimultanee.add(risultatoUtilizzatoSimultanee);
									risultatoUtilizzatoSimultanee = risultatoVerificaPolicy;
									switchPolicy = true;
								}
							}
							else {
								altrePolicySimultanee.add(risultatoUtilizzatoSimultanee);
								risultatoUtilizzatoSimultanee = risultatoVerificaPolicy;
								switchPolicy = true;
							}
						}
						if(!switchPolicy) {
							altrePolicySimultanee.add(risultatoVerificaPolicy);
						}
					}
				}
				else {
					if(risultatoUtilizzato==null) {
						risultatoUtilizzato = risultatoVerificaPolicy;
					}
					else {
						long numeroRimasti = -1;
						if(risultatoUtilizzato.getMaxValue()!=null) {
							numeroRimasti = risultatoUtilizzato.getMaxValue().longValue();
						}
						long numeroCheck = -1;
						if(risultatoVerificaPolicy.getMaxValue()!=null) {
							numeroCheck = risultatoVerificaPolicy.getMaxValue().longValue();
						}
						boolean switchPolicy = false;
						if(numeroCheck>0) {
							if(numeroRimasti>0) {
								if(numeroCheck<numeroRimasti) {
									altrePolicy.add(risultatoUtilizzato);
									risultatoUtilizzato = risultatoVerificaPolicy;
									switchPolicy = true;
								}
							}
							else {
								altrePolicy.add(risultatoUtilizzato);
								risultatoUtilizzato = risultatoVerificaPolicy;
								switchPolicy = true;
							}
						}
						if(!switchPolicy) {
							altrePolicy.add(risultatoVerificaPolicy);
						}
					}
				}
			}
			
			if(!policyConfigurationPorta.isDisabledHttpHeaders_limit() && risultatoUtilizzatoSimultanee!=null &&
				risultatoUtilizzatoSimultanee.getMaxValue()!=null) {
				try {
					String [] headers = op2Properties.getControlloTrafficoNumeroRichiesteSimultaneeHeaderLimit();
					if(headers!=null && headers.length>0) {
						for (String header : headers) {
							headerTrasportoRateLimiting.put(header, risultatoUtilizzatoSimultanee.getMaxValue().longValue()+"");
						}
					}
				}catch(Exception e) { 
					// errore non dovrebbe succedere
				}
				
				if(risultatoUtilizzatoSimultanee.getActualValue()!=null) {
					long rimanenti = risultatoUtilizzatoSimultanee.getMaxValue().longValue() - risultatoUtilizzatoSimultanee.getActualValue().longValue();
					if(rimanenti<0) {  // gli attuali conteggia anche quelle bloccate
						rimanenti = 0;
					}
					try {
						String [] headers = op2Properties.getControlloTrafficoNumeroRichiesteSimultaneeHeaderRemaining();
						if(headers!=null && headers.length>0) {
							for (String header : headers) {
								headerTrasportoRateLimiting.put(header, rimanenti+"");
							}
						}
					}catch(Exception e) { 
						// errore non dovrebbe succedere
					}
				}
			}
			
			if(risultatoUtilizzato!=null &&
				risultatoUtilizzato.getMaxValue()!=null) {
					
				if(!policyConfigurationPorta.isDisabledHttpHeaders_limit()) {
					try {
						String [] headers = null;
						boolean windows = false;
						switch (tipoRisorsa) {
						case DIMENSIONE_MASSIMA_MESSAGGIO:
							// non viene usata
							break;
						case NUMERO_RICHIESTE:
							headers = op2Properties.getControlloTrafficoNumeroRichiesteHeaderLimit();
							windows = op2Properties.getControlloTrafficoNumeroRichiesteHeaderLimitWindows();
							break;
						case OCCUPAZIONE_BANDA:
							headers = op2Properties.getControlloTrafficoOccupazioneBandaHeaderLimit();
							windows = op2Properties.getControlloTrafficoOccupazioneBandaHeaderLimitWindows();
							break;
						case TEMPO_MEDIO_RISPOSTA:
							headers = op2Properties.getControlloTrafficoTempoMedioRispostaHeaderLimit();
							windows = op2Properties.getControlloTrafficoTempoMedioRispostaHeaderLimitWindows();
							break;
						case TEMPO_COMPLESSIVO_RISPOSTA:
							headers = op2Properties.getControlloTrafficoTempoComplessivoRispostaHeaderLimit();
							windows = op2Properties.getControlloTrafficoTempoComplessivoRispostaHeaderLimitWindows();
							break;
						case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
							headers = op2Properties.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderLimit();
							windows = op2Properties.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderLimitWindows();
							break;
						case NUMERO_RICHIESTE_FALLITE:
							headers = op2Properties.getControlloTrafficoNumeroRichiesteFalliteHeaderLimit();
							windows = op2Properties.getControlloTrafficoNumeroRichiesteFalliteHeaderLimitWindows();
							break;
						case NUMERO_FAULT_APPLICATIVI:
							headers = op2Properties.getControlloTrafficoNumeroFaultApplicativiHeaderLimit();
							windows = op2Properties.getControlloTrafficoNumeroFaultApplicativiHeaderLimitWindows();
							break;
						case NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI:
							headers = op2Properties.getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderLimit();
							windows = op2Properties.getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderLimitWindows();
							break;
						}
						
						if(policyConfigurationPorta.isForceHttpHeaders_limit_no_windows()) {
							windows = false;
						}
						else if(policyConfigurationPorta.isForceHttpHeaders_limit_windows()) {
							windows = true;
						}
						
						if(headers!=null && headers.length>0) {
							
							Long maxValue = risultatoUtilizzato.getMaxValueBeforeNormalizing()!=null ? risultatoUtilizzato.getMaxValueBeforeNormalizing() : risultatoUtilizzato.getMaxValue();
							
							StringBuilder sb = new StringBuilder("");
							if(windows && risultatoUtilizzato.getMsWindow()!=null && maxValue!=null) {
								long ms = risultatoUtilizzato.getMsWindow();
								long sec = -1;
								if(ms>1000) {
									// trasformo in secondi
									sec = ms / 1000;
								}
								if(sec>0) {
									sb.append(", ").append(maxValue.longValue()).append(";w=").append(sec);
								}
								if(!altrePolicy.isEmpty()) {
									for (RisultatoVerificaPolicy r : altrePolicy) {
										
										Long maxValueR = r.getMaxValueBeforeNormalizing()!=null ? r.getMaxValueBeforeNormalizing() : r.getMaxValue();
										
										if(r.getMsWindow()!=null && maxValueR!=null) {
											ms = r.getMsWindow();
											sec = -1;
											if(ms>1000) {
												// trasformo in secondi
												sec = ms / 1000;
											}
											if(sec>0) {
												sb.append(", ").append(maxValueR.longValue()).append(";w=").append(sec);
											}
										}
									}
								}
							}
							
							for (String header : headers) {
								headerTrasportoRateLimiting.put(header, maxValue.longValue()+""+sb.toString());
							}
						}
					}catch(Exception e) { 
						// errore non dovrebbe succedere
					}
				}
				
				if(!policyConfigurationPorta.isDisabledHttpHeaders_remaining() && risultatoUtilizzato.getActualValue()!=null) {
					long rimanenti = risultatoUtilizzato.getMaxValue().longValue() - risultatoUtilizzato.getActualValue().longValue();
					if(rimanenti<0) {  // gli attuali conteggia anche quelle bloccate
						rimanenti = 0;
					}
					if(rimanenti==0 && !risultatoUtilizzato.isViolata() && !risultatoUtilizzato.isRemainingZeroValue()) {
						rimanenti = 1; // gestione cluster
					}
					try {
						String [] headers = null;
						switch (tipoRisorsa) {
						case DIMENSIONE_MASSIMA_MESSAGGIO:
							// non viene usata
							break;
						case NUMERO_RICHIESTE:
							headers = op2Properties.getControlloTrafficoNumeroRichiesteHeaderRemaining();
							break;
						case OCCUPAZIONE_BANDA:
							headers = op2Properties.getControlloTrafficoOccupazioneBandaHeaderRemaining();
							break;
						case TEMPO_MEDIO_RISPOSTA:
							// non ha senso
							/**headers = op2Properties.getControlloTrafficoTempoMedioRispostaHeaderRemaining();*/
							break;
						case TEMPO_COMPLESSIVO_RISPOSTA:
							headers = op2Properties.getControlloTrafficoTempoComplessivoRispostaHeaderRemaining();
							break;
						case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
							headers = op2Properties.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderRemaining();
							break;
						case NUMERO_RICHIESTE_FALLITE:
							headers = op2Properties.getControlloTrafficoNumeroRichiesteFalliteHeaderRemaining();
							break;
						case NUMERO_FAULT_APPLICATIVI:
							headers = op2Properties.getControlloTrafficoNumeroFaultApplicativiHeaderRemaining();
							break;
						case NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI:
							headers = op2Properties.getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderRemaining();
							break;
						}
						if(headers!=null && headers.length>0) {
							for (String header : headers) {
								headerTrasportoRateLimiting.put(header, rimanenti+"");
							}
						}
					}catch(Exception e) { 
						// errore non dovrebbe succedere
					}
				}
			}
			if(!policyConfigurationPorta.isDisabledHttpHeaders_reset() && risultatoUtilizzato!=null && risultatoUtilizzato.getMsBeforeResetCounters()!=null) {
				
				long ms = risultatoUtilizzato.getMsBeforeResetCounters().longValue();
				long sec = -1;
				if(ms>1000) {
					// trasformo in secondi
					sec = ms / 1000;
				}
				else if(ms>0) {
					// genero comunque l'header approssimando l'intervallo al secondo
					sec=1;
				}
				
				if(sec>0) {
					try {
						String [] headers = null;
						switch (tipoRisorsa) {
						case DIMENSIONE_MASSIMA_MESSAGGIO:
							// non viene usata
							break;
						case NUMERO_RICHIESTE:
							headers = op2Properties.getControlloTrafficoNumeroRichiesteHeaderReset();
							break;
						case OCCUPAZIONE_BANDA:
							headers = op2Properties.getControlloTrafficoOccupazioneBandaHeaderReset();
							break;
						case TEMPO_MEDIO_RISPOSTA:
							headers = op2Properties.getControlloTrafficoTempoMedioRispostaHeaderReset();
							break;
						case TEMPO_COMPLESSIVO_RISPOSTA:
							headers = op2Properties.getControlloTrafficoTempoComplessivoRispostaHeaderReset();
							break;
						case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
							headers = op2Properties.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderReset();
							break;
						case NUMERO_RICHIESTE_FALLITE:
							headers = op2Properties.getControlloTrafficoNumeroRichiesteFalliteHeaderReset();
							break;
						case NUMERO_FAULT_APPLICATIVI:
							headers = op2Properties.getControlloTrafficoNumeroFaultApplicativiHeaderReset();
							break;
						case NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI:
							headers = op2Properties.getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderReset();
							break;
						}
						
						if(headers!=null && headers.length>0) {
							for (String header : headers) {
								headerTrasportoRateLimiting.put(header, sec+"");
							}
						}
					}catch(Exception e) { 
						// errore non dovrebbe succedere
					}
				}
			}
		}
		
	}

	
	private boolean isCondizioneCongestionamentoPortaDominio(InRequestProtocolContext context, 
			GestoreControlloTraffico gestoreControlloCongestione,
			MsgDiagnostico msgDiag, Transaction tr) throws TransactionDeletedException{
		
		if(context==null) {
			throw new TransactionDeletedException("Context is null");
		}
		
		PdDContext pddContext = context.getPddContext();
		
		boolean isPddCongestionataInformazioneIdentificataDalThread = false;
		Object oPddCongestionataThread = pddContext.getObject(CostantiControlloTraffico.PDD_CONTEXT_PDD_CONGESTIONATA);
		if(oPddCongestionataThread instanceof Boolean){
			isPddCongestionataInformazioneIdentificataDalThread = (Boolean) oPddCongestionataThread;
		}
		
		String idTransazione = null;
		if(context.getPddContext()!=null && context.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)) {
			idTransazione = (String) context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
		}
		
		boolean sync = true;
		if(context.getStato() instanceof StateMessage) {
			StateMessage state = (StateMessage) context.getStato();
			if(state.getConnectionDB()!=null) {
				/**System.out.println("SYNC FALSE CONNECTION FOUND!");*/
				sync=false;
			}
		}
		StatoTraffico statoControlloCongestione = gestoreControlloCongestione.getStatoControlloTraffico(idTransazione,sync);
		if(statoControlloCongestione.isPddCongestionata()!=isPddCongestionataInformazioneIdentificataDalThread){
			/**System.out.println("Rilevata differenza tra controllo del traffico attivo PREINHANDLER ["+controlloTrafficoAttivoThread+"] e stato attuale["+statoControlloCongestione.getControlloTraffico()+"]");*/
			pddContext.removeObject(CostantiControlloTraffico.PDD_CONTEXT_PDD_CONGESTIONATA);
			pddContext.addObject(CostantiControlloTraffico.PDD_CONTEXT_PDD_CONGESTIONATA, statoControlloCongestione.isPddCongestionata());
		}
		
		// Aggiungo al msgDiag i valori delle tre costanti poichè mi servirà anche per diagnostici durante la verifica delle policy.
		// Vanno aggiunti anche se la pdd non risulta congestionata
		
		msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_ACTIVE_THREADS, statoControlloCongestione.getActiveThreads()+"");
		pddContext.addObject(GeneratoreMessaggiErrore.PDD_CONTEXT_ACTIVE_THREADS, statoControlloCongestione.getActiveThreads());
		
		Object oMaxThreads = pddContext.getObject(GeneratoreMessaggiErrore.PDD_CONTEXT_MAX_THREADS_THRESHOLD);
		if(oMaxThreads!=null){
			msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_MAX_THREADS_THRESHOLD, oMaxThreads.toString());
		}
		
		Object oThreshold = pddContext.getObject(GeneratoreMessaggiErrore.PDD_CONTEXT_CONTROLLO_TRAFFICO_THRESHOLD);
		if(oThreshold!=null){
			msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_CONTROLLO_TRAFFICO_THRESHOLD, oThreshold.toString());
		}
		
		if(statoControlloCongestione.isPddCongestionata()){
			// Emetto qua il diagnostico che la porta è congestionata.
			// Potrei emetterlo anche nel preInHandler, però lo emetto anzi qua perche viene poi usato qua la condizione per capire se attivare o meno una policy.
			
			
			// Aggiunto come evento della transazione l'informazione che la PdD è congestionata
			tr.addEventoGestione(TipoEvento.CONTROLLO_TRAFFICO_SOGLIA_CONGESTIONE.getValue()+"_"+CodiceEventoControlloTraffico.VIOLAZIONE.getValue());
			
			
			// Emetto Diagnostico Controllo del Traffico Attivato
			msgDiag.logPersonalizzato(GeneratoreMessaggiErrore.MSG_DIAGNOSTICO_INTERCEPTOR_CONTROLLO_TRAFFICO_PDD_CONGESTIONATA);
			
		}
		
		return statoControlloCongestione.isPddCongestionata();
	}
	
	
	// Ritorna un MsgDiagnostico generator
	private MsgDiagnostico buildMsgDiagnostico(InRequestProtocolContext context) throws HandlerException{
		MsgDiagnostico msgDiag = null;
		try{
			String nomePorta = null;
			if(context.getIntegrazione()!=null) {
				if(context.getIntegrazione().getIdPA()!=null) {
					nomePorta = context.getIntegrazione().getIdPA().getNome();
				}
				else if(context.getIntegrazione().getIdPD()!=null) {
					nomePorta = context.getIntegrazione().getIdPD().getNome();
				}
			}
			
			RequestInfo requestInfo = null;
			if(context.getPddContext()!=null && context.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.REQUEST_INFO)) {
				requestInfo = (RequestInfo) context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
			}
			
			msgDiag = MsgDiagnostico.newInstance(context.getTipoPorta(),context.getIdModulo(),nomePorta, requestInfo);
			msgDiag.setPddContext(context.getPddContext(), context.getProtocolFactory());
			if(org.openspcoop2.core.constants.TipoPdD.DELEGATA.equals(context.getTipoPorta())){
				msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI);
			}
			else{
				msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE);
			}
		}catch(Exception e){
			throw new HandlerException("Generazione Messaggio Diagnostico non riuscita: "+e.getMessage(), e);
		}
		return msgDiag;
	}
	

}
