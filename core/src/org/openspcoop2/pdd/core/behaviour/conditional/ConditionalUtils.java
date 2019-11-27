/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
package org.openspcoop2.pdd.core.behaviour.conditional;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.behaviour.BehaviourEmitDiagnosticException;
import org.openspcoop2.pdd.core.behaviour.BehaviourException;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.core.dynamic.ErrorHandler;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.utils.regexp.RegExpNotFoundException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.openspcoop2.utils.xml2json.JsonXmlPathExpressionEngine;
import org.slf4j.Logger;
import org.w3c.dom.Element;

/**
 * ConditionalUtils
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConditionalUtils  {
	
	public static ConditionalFilterResult filter(PortaApplicativa pa, OpenSPCoop2Message message, Busta busta, 
			RequestInfo requestInfo, PdDContext pddContext, 
			MsgDiagnostico msgDiag, Logger log,
			boolean loadBalancer) throws BehaviourException, BehaviourEmitDiagnosticException {
		
		if(isConfigurazioneCondizionale(pa, log)==false) {
			return null; // non vi è da fare alcun filtro condizionale
		}
		
		ConditionalFilterResult result = new ConditionalFilterResult();
		
		ConfigurazioneCondizionale config = read(pa, log);
		
		ConfigurazioneSelettoreCondizione selettore = null;
		
		String azione = null;
		String staticInfo = null;
		if(busta.getAzione()!=null && !"".equals(busta.getAzione())) {
			selettore = config.getConfigurazioneSelettoreCondizioneByAzione(azione);
			if(selettore!=null) {
				staticInfo = selettore.getStaticInfo();
				result.setNomeGruppoAzioni(config.getGruppoByAzione(azione));
			}
		}
		if(selettore==null) {
			selettore = config.getDefaultConfig();
		}
		
		result.setByFilter(config.isByFilter());
		
		String condition = null;
		String nomeConnettoreDaUsare = null;
		if(staticInfo!=null && !"".equals(staticInfo)) {
			condition = staticInfo;
			msgDiag.addKeyword(CostantiPdD.KEY_TIPO_SELETTORE, "static");
		}
		else {
			String pattern = "";
			try {
				Properties pTrasporto = null;
				String urlInvocazione = null;
				Properties pForm = null;
				if(requestInfo!=null && requestInfo.getProtocolContext()!=null) {
					pTrasporto = requestInfo.getProtocolContext().getParametersTrasporto();
					urlInvocazione = requestInfo.getProtocolContext().getUrlInvocazione_formBased();
					pForm = requestInfo.getProtocolContext().getParametersFormBased();
				}
				Element element = null;
				String elementJson = null;
				if(TipoSelettore.CONTENT_BASED.equals(selettore.getTipoSelettore()) || TipoSelettore.GOVWAY_EXPRESSION.equals(selettore.getTipoSelettore())) {
					if(ServiceBinding.SOAP.equals(message.getServiceBinding())){
						element =message.castAsSoap().getSOAPPart().getEnvelope();
					}
					else{
						if(MessageType.XML.equals(message.getMessageType())){
							element = message.castAsRestXml().getContent();
						}
						else if(MessageType.JSON.equals(message.getMessageType())){
							elementJson = message.castAsRestJson().getContent();
						}
						else{
							throw new Exception("Selettore '"+selettore.getTipoSelettore().getValue()+"' non supportato per il message-type '"+message.getMessageType()+"'");
						}
					}
				}
				
				msgDiag.addKeyword(CostantiPdD.KEY_TIPO_SELETTORE, selettore.getTipoSelettore().getValue());
				msgDiag.addKeyword(CostantiPdD.KEY_PATTERN_SELETTORE, pattern); // per eliminare @@ dove non serve
				
				switch (selettore.getTipoSelettore()) {
				
				case HEADER_BASED:
					pattern = " (Header HTTP: "+selettore.getPattern()+")";
					msgDiag.addKeyword(CostantiPdD.KEY_PATTERN_SELETTORE, pattern);
					condition = TransportUtils.get(pTrasporto, selettore.getPattern());
					if(condition==null) {
						throw new Exception("header non presente");
					}
					break;
					
				case URLBASED:
					pattern = " (RegExpr: "+selettore.getPattern()+")";
					msgDiag.addKeyword(CostantiPdD.KEY_PATTERN_SELETTORE, pattern);
					try{
						condition = RegularExpressionEngine.getStringMatchPattern(urlInvocazione, selettore.getPattern());
					}catch(RegExpNotFoundException notFound){}
					break;
					
				case FORM_BASED:
					pattern = " (Parametro URL: "+selettore.getPattern()+")";
					msgDiag.addKeyword(CostantiPdD.KEY_PATTERN_SELETTORE, pattern);
					condition = TransportUtils.get(pForm, selettore.getPattern());
					if(condition==null) {
						throw new Exception("parametro della url non presente");
					}
					break;
					
				case CONTENT_BASED:
					AbstractXPathExpressionEngine xPathEngine = null;
					if(element!=null) {
						pattern = " (xPath: "+selettore.getPattern()+")";
						msgDiag.addKeyword(CostantiPdD.KEY_PATTERN_SELETTORE, pattern);
						xPathEngine = new org.openspcoop2.message.xml.XPathExpressionEngine();
						condition = AbstractXPathExpressionEngine.extractAndConvertResultAsString(element, xPathEngine, selettore.getPattern(),  log);
					}
					else {
						pattern = " (jsonPath: "+selettore.getPattern()+")";
						msgDiag.addKeyword(CostantiPdD.KEY_PATTERN_SELETTORE, pattern);
						condition = JsonXmlPathExpressionEngine.extractAndConvertResultAsString(elementJson, selettore.getPattern(), log);
					}
					break;
					
				case INDIRIZZO_IP:
					if(pddContext!=null && pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CLIENT_IP_REMOTE_ADDRESS)) {
						condition = (String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLIENT_IP_REMOTE_ADDRESS);
					}
					break;
					
				case INDIRIZZO_IP_FORWARDED:
					if(pddContext!=null && pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CLIENT_IP_TRANSPORT_ADDRESS)) {
						condition = (String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLIENT_IP_TRANSPORT_ADDRESS);
					}
					break;
					
				case SOAPACTION_BASED:
					if(ServiceBinding.SOAP.equals(message.getServiceBinding())){
						condition = message.castAsSoap().getSoapAction();
						if(condition!=null) {
							condition = condition.trim();
							if(condition.startsWith("\"") && condition.length()>1){
								condition = condition.substring(1);
							}
							if(condition.endsWith("\"")  && condition.length()>1){
								condition = condition.substring(0, (condition.length()-1));
							}
						}
					}
					break;
					
				case GOVWAY_EXPRESSION:
					pattern = " (govwayExpr: "+selettore.getPattern()+")";
					msgDiag.addKeyword(CostantiPdD.KEY_PATTERN_SELETTORE, pattern);
					Map<String, Object> dynamicMap = new Hashtable<String, Object>();
					ErrorHandler errorHandler = new ErrorHandler();
					DynamicUtils.fillDynamicMapRequest(log, dynamicMap, pddContext, urlInvocazione,
							message,
							element, elementJson, 
							busta, pTrasporto, pForm,
							errorHandler);
					condition = DynamicUtils.convertDynamicPropertyValue("ConditionalConfig", selettore.getPattern(), dynamicMap, pddContext, true);
					break;
				}
			
				if(condition==null) {
					throw new Exception("Nessuna condizione estratta");
				}
				else {
					msgDiag.addKeyword(CostantiPdD.KEY_CONDIZIONE_CONNETTORE, condition);
				}
				
			}catch(Exception e) {
				
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.getMessage());
				
				if(config.getCondizioneNonIdentificata().isAbortTransaction()) {
					throw new BehaviourEmitDiagnosticException(msgDiag, MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
							"connettoriMultipli.consegnaCondizionale.identificazioneFallita.error", e);
				}
				else {
					if(config.getCondizioneNonIdentificata().isEmitDiagnosticError()) {
						msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
								"connettoriMultipli.consegnaCondizionale.identificazioneFallita.error");
					}
					else if(config.getCondizioneNonIdentificata().isEmitDiagnosticInfo()) {
						msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
								"connettoriMultipli.consegnaCondizionale.identificazioneFallita.info");
					}
					
					if(loadBalancer) {
						// li usero tutti senza filtro sulla condizionalità
						
						msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
								"connettoriMultipli.consegnaCondizionale.tuttiConnettori");
						
						result.setListServiziApplicativi(pa.getServizioApplicativoList());
						return result;
						
					}
					
					nomeConnettoreDaUsare = config.getCondizioneNonIdentificata().getNomeConnettore();
					if(nomeConnettoreDaUsare==null) {
						// può succedere per le comunicazioni sincrone dove non vi saranno notifiche
						
						if(!loadBalancer) {
							msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
									"connettoriMultipli.consegnaCondizionale.nessunConnettore");
						}
						
						result.setListServiziApplicativi(new ArrayList<>());
						return result;
					}
					
					result.setListServiziApplicativi(filter(pa.getServizioApplicativoList(),false,nomeConnettoreDaUsare));
					if(result.getListServiziApplicativi().isEmpty()) {
						throw new BehaviourException("Connettore '"+nomeConnettoreDaUsare+"' indicato, da utilizzare in caso di identificazione fallita, non esistente");
					}
					
					msgDiag.addKeyword(CostantiPdD.KEY_NOME_CONNETTORE, nomeConnettoreDaUsare);
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
							"connettoriMultipli.consegnaCondizionale.connettoreDefault");
					
					return result;
				}
			}
		}
		
		String conditionFinal = condition;
		if(selettore.getPrefix()!=null) {
			conditionFinal = selettore.getPrefix()+conditionFinal;
		}
		if(selettore.getSuffix()!=null) {
			conditionFinal = conditionFinal+selettore.getSuffix();
		}
		result.setCondition(conditionFinal);
		msgDiag.addKeyword(CostantiPdD.KEY_CONDIZIONE_CONNETTORE, conditionFinal);
		
		List<PortaApplicativaServizioApplicativo> l = filter(pa.getServizioApplicativoList(), config.isByFilter(), conditionFinal);
		if(!l.isEmpty()) {
			result.setListServiziApplicativi(l);
			return result;
		}
		else {
			if(config.getNessunConnettoreTrovato().isAbortTransaction()) {
				if(config.isByFilter()) {
					throw new BehaviourEmitDiagnosticException(msgDiag, MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
							"connettoriMultipli.consegnaCondizionale.connettoreNonEsistente.filtro.error");
				}
				else {
					throw new BehaviourEmitDiagnosticException(msgDiag, MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
							"connettoriMultipli.consegnaCondizionale.connettoreNonEsistente.nomeConnettore.error");
				}
			}
			else {
				if(config.getNessunConnettoreTrovato().isEmitDiagnosticError()) {
					if(config.isByFilter()) {
						msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
								"connettoriMultipli.consegnaCondizionale.connettoreNonEsistente.filtro.error");
					}
					else {
						msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
								"connettoriMultipli.consegnaCondizionale.connettoreNonEsistente.nomeConnettore.error");
					}
				}
				else if(config.getNessunConnettoreTrovato().isEmitDiagnosticInfo()) {
					if(config.isByFilter()) {
						msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
								"connettoriMultipli.consegnaCondizionale.connettoreNonEsistente.filtro.info");
					}
					else {
						msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
								"connettoriMultipli.consegnaCondizionale.connettoreNonEsistente.nomeConnettore.info");
					}
				}
				
				if(loadBalancer) {
					// li usero tutti senza filtro sulla condizionalità
					
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
							"connettoriMultipli.consegnaCondizionale.tuttiConnettori");
					
					result.setListServiziApplicativi(pa.getServizioApplicativoList());
					return result;
				}
				
				nomeConnettoreDaUsare = config.getNessunConnettoreTrovato().getNomeConnettore();
				if(nomeConnettoreDaUsare==null) {
					// può succedere per le comunicazioni sincrone dove non vi saranno notifiche
					
					if(!loadBalancer) {
						msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
								"connettoriMultipli.consegnaCondizionale.nessunConnettore");
					}					
					
					result.setListServiziApplicativi(new ArrayList<>());
					return result;
				}
				
				result.setListServiziApplicativi(filter(pa.getServizioApplicativoList(),false,nomeConnettoreDaUsare));
				if(result.getListServiziApplicativi().isEmpty()) {
					throw new BehaviourException("Connettore '"+nomeConnettoreDaUsare+"' indicato, da utilizzare in caso di identificazione condizionale fallita, non esistente");
				}
				
				msgDiag.addKeyword(CostantiPdD.KEY_NOME_CONNETTORE, nomeConnettoreDaUsare);
				msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
						"connettoriMultipli.consegnaCondizionale.connettoreDefault");
				
				return result;
			}
			
		}
		
	}
	private static List<PortaApplicativaServizioApplicativo> filter(List<PortaApplicativaServizioApplicativo> listPASA, boolean filter, String condition){
		List<PortaApplicativaServizioApplicativo> l = new ArrayList<>();
		for (PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo : listPASA) {
			
			if(portaApplicativaServizioApplicativo.getDatiConnettore()==null || portaApplicativaServizioApplicativo.getDatiConnettore().getStato()==null || 
					StatoFunzionalita.ABILITATO.equals(portaApplicativaServizioApplicativo.getDatiConnettore().getStato())) {
			
				if(filter) {
					if(portaApplicativaServizioApplicativo.getDatiConnettore()!=null) {
						if(portaApplicativaServizioApplicativo.getDatiConnettore().getFiltroList()!=null &&
								portaApplicativaServizioApplicativo.getDatiConnettore().getFiltroList().contains(condition)) {
							l.add(portaApplicativaServizioApplicativo);
						}
					}
				}
				else {
					String nomeConnettore = org.openspcoop2.pdd.core.behaviour.built_in.Costanti.NOME_CONNETTORE_DEFAULT;
					if(portaApplicativaServizioApplicativo.getDatiConnettore()!=null &&
							portaApplicativaServizioApplicativo.getDatiConnettore().getNome()!=null) {
						nomeConnettore = portaApplicativaServizioApplicativo.getDatiConnettore().getNome();
					}
							 
					if(condition.equals(nomeConnettore)) {
						l.add(portaApplicativaServizioApplicativo);
					}
				}
				
			}
		}
		return l;
	}
	
	public static boolean isConfigurazioneCondizionale(PortaApplicativa pa, Logger log) {
		if(pa.getBehaviour()==null || pa.getBehaviour().sizeProprietaList()<=0) {
			return false;
		}
		String type = null;
		for (Proprieta p : pa.getBehaviour().getProprietaList()) {
			if(Costanti.CONDITIONAL_ENABLED.equals(p.getNome())) {
				type = p.getValore();
				break;
			}
		}
		if(type==null) {
			return false;
		}
		return "true".equals(type);
	}
	
	public static boolean isConfigurazioneCondizionaleByFilter(PortaApplicativa pa, Logger log) {
		if(pa.getBehaviour()==null || pa.getBehaviour().sizeProprietaList()<=0) {
			return false;
		}
		String type = null;
		String byFilter = null;
		for (Proprieta p : pa.getBehaviour().getProprietaList()) {
			if(Costanti.CONDITIONAL_ENABLED.equals(p.getNome())) {
				type = p.getValore();
			}
			else if(Costanti.CONDITIONAL_BY_FILTER.equals(p.getNome())) {
				byFilter = p.getValore();
			}
		}
		if(type==null || byFilter==null) {
			return false;
		}
		return "true".equals(type) && "true".equals(byFilter);
	}
	
	public static ConfigurazioneCondizionale read(PortaApplicativa pa, Logger log) throws BehaviourException {
		ConfigurazioneCondizionale config = new ConfigurazioneCondizionale();
		if(pa.getBehaviour()==null || pa.getBehaviour().sizeProprietaList()<=0) {
			throw new BehaviourException("Configurazione condizionale non disponibile");
		}
		
		ConfigurazioneSelettoreCondizione selettoreConfigurazioneDefault = new ConfigurazioneSelettoreCondizione();
		config.setDefaultConfig(selettoreConfigurazioneDefault);
		
		IdentificazioneFallitaConfigurazione condizioneNonIdentificata = new IdentificazioneFallitaConfigurazione();
		config.setCondizioneNonIdentificata(condizioneNonIdentificata);
		
		IdentificazioneFallitaConfigurazione nessunConnettoreTrovato = new IdentificazioneFallitaConfigurazione();
		config.setNessunConnettoreTrovato(nessunConnettoreTrovato);
		
		List<String> gruppi = new ArrayList<String>();
		
		for (Proprieta p : pa.getBehaviour().getProprietaList()) {
			
			String nome = p.getNome();
			String valore = p.getValore().trim();
			
			try {
				if(Costanti.CONDITIONAL_BY_FILTER.equals(nome)) {
					config.setByFilter("true".equals(valore));
				}
				
				else if(Costanti.CONDITIONAL_TIPO_SELETTORE.equals(nome)) {
					selettoreConfigurazioneDefault.setTipoSelettore(TipoSelettore.toEnumConstant(valore, true));
				}
				else if(Costanti.CONDITIONAL_PATTERN.equals(nome)) {
					selettoreConfigurazioneDefault.setPattern(valore);
				}
				else if(Costanti.CONDITIONAL_PREFIX.equals(nome)) {
					selettoreConfigurazioneDefault.setPrefix(valore);
				}
				else if(Costanti.CONDITIONAL_SUFFIX.equals(nome)) {
					selettoreConfigurazioneDefault.setSuffix(valore);
				}
				
				else if(nome.startsWith(Costanti.CONDITIONAL_GROUP) && nome.endsWith(Costanti.CONDITIONAL_GROUP_NAME) ) {
					String gruppoNome = nome.substring(Costanti.CONDITIONAL_GROUP.length(), (nome.length()-Costanti.CONDITIONAL_GROUP_NAME.length()));
					gruppi.add(gruppoNome);
				}
				
				else if((Costanti.CONDITIONAL_CONDIZIONE_NON_IDENTIFICATA+Costanti.CONDITIONAL_ABORT_TRANSACTION).equals(nome)) {
					condizioneNonIdentificata.setAbortTransaction("true".equals(valore));
				}
				else if((Costanti.CONDITIONAL_CONDIZIONE_NON_IDENTIFICATA+Costanti.CONDITIONAL_EMIT_DIAGNOSTIC_INFO).equals(nome)) {
					condizioneNonIdentificata.setEmitDiagnosticInfo("true".equals(valore));
				}
				else if((Costanti.CONDITIONAL_CONDIZIONE_NON_IDENTIFICATA+Costanti.CONDITIONAL_EMIT_DIAGNOSTIC_ERROR).equals(nome)) {
					condizioneNonIdentificata.setEmitDiagnosticError("true".equals(valore));
				}
				else if((Costanti.CONDITIONAL_CONDIZIONE_NON_IDENTIFICATA+Costanti.CONDITIONAL_NOME_CONNETTORE).equals(nome)) {
					condizioneNonIdentificata.setNomeConnettore(valore);
				}
				
				else if((Costanti.CONDITIONAL_NESSUN_CONNETTORE_TROVATO+Costanti.CONDITIONAL_ABORT_TRANSACTION).equals(nome)) {
					nessunConnettoreTrovato.setAbortTransaction("true".equals(valore));
				}
				else if((Costanti.CONDITIONAL_NESSUN_CONNETTORE_TROVATO+Costanti.CONDITIONAL_EMIT_DIAGNOSTIC_INFO).equals(nome)) {
					nessunConnettoreTrovato.setEmitDiagnosticInfo("true".equals(valore));
				}
				else if((Costanti.CONDITIONAL_NESSUN_CONNETTORE_TROVATO+Costanti.CONDITIONAL_EMIT_DIAGNOSTIC_ERROR).equals(nome)) {
					nessunConnettoreTrovato.setEmitDiagnosticError("true".equals(valore));
				}
				else if((Costanti.CONDITIONAL_NESSUN_CONNETTORE_TROVATO+Costanti.CONDITIONAL_NOME_CONNETTORE).equals(nome)) {
					nessunConnettoreTrovato.setNomeConnettore(valore);
				}
			}catch(Exception e) {
				throw new BehaviourException("Configurazione condizionale non corretta (proprietà:"+p.getNome()+" valore:'"+p.getValore()+"'): "+e.getMessage(),e);
			}
			
		}
		
		if(!gruppi.isEmpty()) {
			for (String gruppo : gruppi) {
				
				String prefixGruppo = Costanti.CONDITIONAL_GROUP+gruppo;
				String prefixGruppoConUnderscore = prefixGruppo+"_";
				ConfigurazioneSelettoreCondizione selettoreConfigurazionePerAzioni = new ConfigurazioneSelettoreCondizione();
				
				String nomeGruppoEsteso = null;
				List<String> azioni = new ArrayList<>();
				
				for (Proprieta p : pa.getBehaviour().getProprietaList()) {
					
					String nome = p.getNome();
					String valore = p.getValore().trim();
					
					try {
						if((prefixGruppo+Costanti.CONDITIONAL_GROUP_NAME).equals(nome)) {
							nomeGruppoEsteso = valore;
						}
						else if((prefixGruppoConUnderscore+Costanti.CONDITIONAL_TIPO_SELETTORE).equals(nome)) {
							selettoreConfigurazionePerAzioni.setTipoSelettore(TipoSelettore.toEnumConstant(valore, true));
						}
						else if((prefixGruppoConUnderscore+Costanti.CONDITIONAL_STATIC_INFO).equals(nome)) {
							selettoreConfigurazionePerAzioni.setStaticInfo(valore);
						}
						else if((prefixGruppoConUnderscore+Costanti.CONDITIONAL_PATTERN).equals(nome)) {
							selettoreConfigurazionePerAzioni.setPattern(valore);
						}
						else if((prefixGruppoConUnderscore+Costanti.CONDITIONAL_PREFIX).equals(nome)) {
							selettoreConfigurazionePerAzioni.setPrefix(valore);
						}
						else if((prefixGruppoConUnderscore+Costanti.CONDITIONAL_SUFFIX).equals(nome)) {
							selettoreConfigurazionePerAzioni.setSuffix(valore);
						}
						else if(nome.startsWith(prefixGruppo+Costanti.CONDITIONAL_GROUP_ACTION_NAME) ) {
							String azione = valore;
							if(!azioni.contains(azione)) {
								azioni.add(azione);
							}
						}
					}catch(Exception e) {
						throw new BehaviourException("Configurazione condizionale non corretta (proprietà:"+p.getNome()+" valore:'"+p.getValore()+"'): "+e.getMessage(),e);
					}					
					
				}
				
				config.addActionConfig(nomeGruppoEsteso, selettoreConfigurazionePerAzioni, azioni);
				
			}
		}
		

		return config;
	}
	

	public static void save(PortaApplicativa pa, ConfigurazioneCondizionale configurazione) throws BehaviourException {
		
		if(pa.getBehaviour()==null) {
			throw new BehaviourException("Configurazione behaviour non abilitata");
		}
		if(configurazione==null) {
			throw new BehaviourException("Configurazione condizionale non fornita");
		}
		pa.getBehaviour().addProprieta(newP(Costanti.CONDITIONAL_ENABLED, true+""));
		
		pa.getBehaviour().addProprieta(newP(Costanti.CONDITIONAL_BY_FILTER, configurazione.isByFilter()+""));
				
		if(configurazione.getDefaultConfig()==null) {
			throw new BehaviourException("Configurazione selettore condizione di default non fornita");
		}
		pa.getBehaviour().addProprieta(newP(Costanti.CONDITIONAL_TIPO_SELETTORE, configurazione.getDefaultConfig().getTipoSelettore().getValue()));
		if(StringUtils.isNotEmpty(configurazione.getDefaultConfig().getPattern())) {
			pa.getBehaviour().addProprieta(newP(Costanti.CONDITIONAL_PATTERN, configurazione.getDefaultConfig().getPattern()));
		}
		if(StringUtils.isNotEmpty(configurazione.getDefaultConfig().getPrefix())) {
			pa.getBehaviour().addProprieta(newP(Costanti.CONDITIONAL_PREFIX, configurazione.getDefaultConfig().getPrefix()));
		}
		if(StringUtils.isNotEmpty(configurazione.getDefaultConfig().getSuffix())) {
			pa.getBehaviour().addProprieta(newP(Costanti.CONDITIONAL_SUFFIX, configurazione.getDefaultConfig().getSuffix()));
		}
		
		if(configurazione.getGruppiConfigurazioneSelettoreCondizione()!=null && !configurazione.getGruppiConfigurazioneSelettoreCondizione().isEmpty()) {
			int indexGruppo = 1;
			for (String gruppo : configurazione.getGruppiConfigurazioneSelettoreCondizione()) {
				
				String prefixGruppo = Costanti.CONDITIONAL_GROUP+indexGruppo;
				pa.getBehaviour().addProprieta(newP((prefixGruppo+Costanti.CONDITIONAL_GROUP_NAME),gruppo));
				
				ConfigurazioneSelettoreCondizione selettore = configurazione.getConfigurazioneSelettoreCondizioneByGroupName(gruppo);
				List<String> azioni = configurazione.getAzioniByGroupName(gruppo);
				
				if(selettore==null) {
					throw new BehaviourException("Configurazione selettore condizione per il gruppo '"+gruppo+"' non fornita");
				}
				if(azioni==null || azioni.isEmpty()) {
					throw new BehaviourException("Configurazione lista di azioni per il gruppo '"+gruppo+"' non fornita");
				}
				
				String prefixGruppoAzione = prefixGruppo+Costanti.CONDITIONAL_GROUP_ACTION_NAME;
				int indexAzione = 1;
				for (String azione : azioni) {
					pa.getBehaviour().addProprieta(newP((prefixGruppoAzione+indexAzione),azione));
					indexAzione++;
				}
				
				pa.getBehaviour().addProprieta(newP((prefixGruppo+"_"+Costanti.CONDITIONAL_TIPO_SELETTORE),selettore.getTipoSelettore().getValue()));
				if(StringUtils.isNotEmpty(selettore.getStaticInfo())) {
					pa.getBehaviour().addProprieta(newP((prefixGruppo+"_"+Costanti.CONDITIONAL_STATIC_INFO),selettore.getStaticInfo()));
				}
				if(StringUtils.isNotEmpty(selettore.getPattern())) {
					pa.getBehaviour().addProprieta(newP((prefixGruppo+"_"+Costanti.CONDITIONAL_PATTERN),selettore.getPattern()));
				}
				if(StringUtils.isNotEmpty(selettore.getPrefix())) {
					pa.getBehaviour().addProprieta(newP((prefixGruppo+"_"+Costanti.CONDITIONAL_PREFIX),selettore.getPrefix()));
				}
				if(StringUtils.isNotEmpty(selettore.getSuffix())) {
					pa.getBehaviour().addProprieta(newP((prefixGruppo+"_"+Costanti.CONDITIONAL_SUFFIX), selettore.getSuffix()));
				}
				
				indexGruppo++;
			}
		}
		
		if(configurazione.getCondizioneNonIdentificata()==null) {
			throw new BehaviourException("Configurazione 'condizione non identificata' non fornita");
		}
		pa.getBehaviour().addProprieta(newP((Costanti.CONDITIONAL_CONDIZIONE_NON_IDENTIFICATA+Costanti.CONDITIONAL_ABORT_TRANSACTION),
				configurazione.getCondizioneNonIdentificata().isAbortTransaction()+""));
		pa.getBehaviour().addProprieta(newP((Costanti.CONDITIONAL_CONDIZIONE_NON_IDENTIFICATA+Costanti.CONDITIONAL_EMIT_DIAGNOSTIC_INFO),
				configurazione.getCondizioneNonIdentificata().isEmitDiagnosticInfo()+""));
		pa.getBehaviour().addProprieta(newP((Costanti.CONDITIONAL_CONDIZIONE_NON_IDENTIFICATA+Costanti.CONDITIONAL_EMIT_DIAGNOSTIC_ERROR),
				configurazione.getCondizioneNonIdentificata().isEmitDiagnosticError()+""));
		if(StringUtils.isNotEmpty(configurazione.getCondizioneNonIdentificata().getNomeConnettore())) {
			pa.getBehaviour().addProprieta(newP((Costanti.CONDITIONAL_CONDIZIONE_NON_IDENTIFICATA+Costanti.CONDITIONAL_NOME_CONNETTORE),
					configurazione.getCondizioneNonIdentificata().getNomeConnettore()));
		}
		
		if(configurazione.getNessunConnettoreTrovato()==null) {
			throw new BehaviourException("Configurazione 'nessun connettore trovato' non fornita");
		}
		pa.getBehaviour().addProprieta(newP((Costanti.CONDITIONAL_NESSUN_CONNETTORE_TROVATO+Costanti.CONDITIONAL_ABORT_TRANSACTION),
				configurazione.getNessunConnettoreTrovato().isAbortTransaction()+""));
		pa.getBehaviour().addProprieta(newP((Costanti.CONDITIONAL_NESSUN_CONNETTORE_TROVATO+Costanti.CONDITIONAL_EMIT_DIAGNOSTIC_INFO),
				configurazione.getNessunConnettoreTrovato().isEmitDiagnosticInfo()+""));
		pa.getBehaviour().addProprieta(newP((Costanti.CONDITIONAL_NESSUN_CONNETTORE_TROVATO+Costanti.CONDITIONAL_EMIT_DIAGNOSTIC_ERROR),
				configurazione.getNessunConnettoreTrovato().isEmitDiagnosticError()+""));
		if(StringUtils.isNotEmpty(configurazione.getNessunConnettoreTrovato().getNomeConnettore())) {
			pa.getBehaviour().addProprieta(newP((Costanti.CONDITIONAL_NESSUN_CONNETTORE_TROVATO+Costanti.CONDITIONAL_NOME_CONNETTORE),
					configurazione.getNessunConnettoreTrovato().getNomeConnettore()));
		}
	}
	
	private static Proprieta newP(String nome, String valore) {
		Proprieta p = new Proprieta();
		p.setNome(nome);
		p.setValore(valore);
		return p;
	}
}
