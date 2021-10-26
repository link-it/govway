package org.openspcoop2.pdd.core.controllo_traffico;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive;
import org.openspcoop2.core.controllo_traffico.IdActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiTransazione;
import org.openspcoop2.core.controllo_traffico.beans.UniqueIdentifierUtilities;
/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsaPolicyAttiva;
import org.openspcoop2.core.controllo_traffico.utils.PolicyUtilities;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.controllo_traffico.policy.InterceptorPolicyUtilities;
import org.openspcoop2.pdd.core.controllo_traffico.policy.PolicyFiltroApplicativoUtilities;
import org.openspcoop2.pdd.core.controllo_traffico.policy.PolicyVerifier;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.slf4j.Logger;

/**     
 * DimensioneMessaggiConfigurationUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DimensioneMessaggiConfigurationUtils {

	public static SoglieDimensioneMessaggi readSoglieDimensioneMessaggi(TipoPdD tipoPdD, String nomePorta, 
			DatiTransazione datiTransazione, Logger log,
			URLProtocolContext urlProtocolContext, PdDContext pddContext,
			IProtocolFactory<?> protocolFactory) throws Exception {
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		ConfigurazioneControlloTraffico configurazioneControlloTraffico =  op2Properties.getConfigurazioneControlloTraffico();
		ConfigurazionePdDManager configPdDManager = ConfigurazionePdDManager.getInstance();
		
		SoglieDimensioneMessaggi soglie = null;
		
		Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> mapPolicyAttive = null;
		try{
		
			mapPolicyAttive = new HashMap<>();
			
			if(tipoPdD!=null && nomePorta!=null) {
				Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> mapPolicyAttiveAPI = null;
				try {
					mapPolicyAttiveAPI = configPdDManager.getElencoIdPolicyAttiveAPI_dimensioneMessaggio(configurazioneControlloTraffico.isPolicyReadedWithDynamicCache(),
							tipoPdD, nomePorta);
				}catch(DriverConfigurazioneNotFound notFound) {}
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
				mapPolicyAttiveGlobali = configPdDManager.getElencoIdPolicyAttiveGlobali_dimensioneMessaggio(configurazioneControlloTraffico.isPolicyReadedWithDynamicCache());
			}catch(DriverConfigurazioneNotFound notFound) {}
			if(mapPolicyAttiveGlobali!=null && !mapPolicyAttiveGlobali.isEmpty()) {
				Iterator<TipoRisorsaPolicyAttiva> it = mapPolicyAttiveGlobali.keySet().iterator();
				while (it.hasNext()) {
					TipoRisorsaPolicyAttiva tipoRisorsaPolicyAttiva = (TipoRisorsaPolicyAttiva) it.next();
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
		}catch(Exception e){
			throw new Exception("Configurazione non disponibile: "+e.getMessage(), e);
		}
		
		try{
			if(mapPolicyAttive!=null && !mapPolicyAttive.isEmpty()){
				
				int policyTotali = 0;
				int policyDisabilitate = 0;
				int policyFiltrate = 0;
				//int policyNonApplicabili = 0;
				int policyRispettate = 0;
				//int policyViolate = 0;
				//int policyViolateWarningOnly = 0;
				int policyInErrore = 0;
				
				Iterator<TipoRisorsaPolicyAttiva> it = mapPolicyAttive.keySet().iterator();
				while (it.hasNext()) {
					TipoRisorsaPolicyAttiva tipoRisorsaPolicyAttiva = (TipoRisorsaPolicyAttiva) it.next();
					
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
									attivazionePolicy = configPdDManager.getAttivazionePolicy(configurazioneControlloTraffico.isPolicyReadedWithDynamicCache(), 
										UniqueIdentifierUtilities.getUniqueId(idActive));
								}catch(DriverConfigurazioneNotFound notFound) {
									throw new Exception("Istanza di Policy con id ["+idActive.getNome()+"] non esistente: "+notFound.getMessage(),notFound);
								}
								if(attivazionePolicy==null){
									throw new Exception("Istanza di Policy con id ["+idActive.getNome()+"] non esistente?");
								}
								String alias = PolicyUtilities.getNomeActivePolicy(attivazionePolicy.getAlias(), attivazionePolicy.getIdActivePolicy());
								
								// Verifico se un eventuale filtro configurato per la policy matcha con i dati della transazione
								boolean matchFiltro = !policyRispettataCheRichiedeBreak && !policyViolataBreak && InterceptorPolicyUtilities.filter(attivazionePolicy.getFiltro(), datiTransazione, null);
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

										if(valorePresente==null || valorePresente.equals(attivazionePolicy.getFiltro().getInformazioneApplicativaValore())==false){
											
											policyFiltrate++;
											
											// Emetto Diagnostico Policy Filtrata
											if(valorePresente==null){
												valorePresente = "n.d.";
											}
											log.debug("Filtro Applicativo atteso ["+
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
												configurazionePolicy = configPdDManager.getConfigurazionePolicy(configurazioneControlloTraffico.isPolicyReadedWithDynamicCache(), idActive.getIdPolicy());
											}catch(DriverConfigurazioneNotFound notFound) {
												throw new Exception("Policy con id ["+idActive.getIdPolicy()+"] non esistente: "+notFound.getMessage(),notFound);
											}
											if(configurazionePolicy==null){
												throw new Exception("Policy con id ["+idActive.getIdPolicy()+"] non esistente?");
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
											if(soglie.getRichiesta()==null) {
												updateRichiesta = true;
											}
											else if(sogliaRichiesta<soglie.getRichiesta().getSogliaKb()) {
												updateRichiesta = true;
											}
											if(updateRichiesta) {
												soglie.setRichiesta(build(sogliaRichiesta, attivazionePolicy, protocolFactory, configPdDManager));
											}
										}
										
										if(sogliaRisposta>0) {
											boolean updateRisposta = false;
											if(soglie.getRisposta()==null) {
												updateRisposta = true;
											}
											else if(sogliaRisposta<soglie.getRisposta().getSogliaKb()) {
												updateRisposta = true;
											}
											if(updateRisposta) {
												soglie.setRisposta(build(sogliaRisposta, attivazionePolicy, protocolFactory, configPdDManager));
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
										log.debug("[policy: "+alias+"] "+GeneratoreMessaggiErrore.TEMPLATE_POLICY_FILTRATA_MOTIVO_BREAK);
									}
									else if(policyViolataBreak) {
										log.debug("[policy: "+alias+"] "+GeneratoreMessaggiErrore.TEMPLATE_POLICY_FILTRATA_MOTIVO_BREAK_VIOLATA);
									}
									else {
										log.debug("[policy: "+alias+"] "+GeneratoreMessaggiErrore.TEMPLATE_POLICY_FILTRATA_MOTIVO_FILTRO+ PolicyUtilities.toStringFilter(attivazionePolicy.getFiltro()));
									}
									
								}
								
							}
							else{
								
								policyDisabilitate++;
								
								// Emetto Diagnostico Policy Disabilitata
								log.debug("[policy: "+idActive.getNome()+"] policy disabilitata");
								
							}
							
						}
						catch(Throwable e){
							
							policyInErrore++;

							// Emetto Diagnostico Policy in Errore
							log.error("Errore durante il controllo della policy con id["+idActive.getNome()+"]: "+e.getMessage(),e);
							
						}
					}
				
				}
			
				log.debug("Valutazione policy totali["+policyTotali+"] disabilitate["+policyDisabilitate+"] filtrate["+policyFiltrate+"] utilizzate["+policyRispettate+"] inErrore["+policyInErrore+"]");
								
			}
		}catch(Exception e){
			throw new Exception("Errore durante l'identificazione dei limiti sulla dimensione dei messaggi: "+e.getMessage(), e);
		}
		
		if(soglie!=null) {
			if(soglie.getRichiesta()==null || soglie.getRisposta()==null) {
				soglie = null;
			}
		}
		
		return soglie;
	}
	
	private static SogliaDimensioneMessaggio build(long sogliaKb, AttivazionePolicy attivazionePolicy, IProtocolFactory<?> protocolFactory, ConfigurazionePdDManager configPdDManager) throws Exception {
		SogliaDimensioneMessaggio soglia = new SogliaDimensioneMessaggio();
		soglia.setSogliaKb(sogliaKb);
		if(attivazionePolicy.getFiltro()!=null && attivazionePolicy.getFiltro().getEnabled() &&
				attivazionePolicy.getFiltro().getNomePorta()!=null &&
				attivazionePolicy.getFiltro().getRuoloPorta()!=null) {
			soglia.setPolicyGlobale(false);
		}
		else {
			soglia.setPolicyGlobale(true);
		}
		soglia.setNomePolicy(PolicyUtilities.getNomeActivePolicy(attivazionePolicy.getAlias(), attivazionePolicy.getIdActivePolicy()));
		
		String idPolicyConGruppo = null;
		String configurazione = null;
		String API = null;
		if(!soglia.isPolicyGlobale()) {
			API = PolicyVerifier.getIdAPI(attivazionePolicy, protocolFactory, configPdDManager);
		}
		idPolicyConGruppo = PolicyUtilities.buildIdConfigurazioneEventoPerPolicy(attivazionePolicy, null, API);
		configurazione = PolicyUtilities.buildConfigurazioneEventoPerPolicy(attivazionePolicy, soglia.isPolicyGlobale());
		soglia.setIdPolicyConGruppo(idPolicyConGruppo);
		soglia.setConfigurazione(configurazione);
		
		return soglia;
	}
}
