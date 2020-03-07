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
package org.openspcoop2.pdd.core.behaviour.conditional;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.ModalitaIdentificazione;
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
import org.openspcoop2.pdd.core.behaviour.BehaviourPropertiesUtils;
import org.openspcoop2.pdd.core.behaviour.built_in.BehaviourType;
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
			BehaviourType behaviourType) throws BehaviourException, BehaviourEmitDiagnosticException {
		
		if(isConfigurazioneCondizionale(pa, log)==false) {
			return null; // non vi è da fare alcun filtro condizionale
		}
		
		ConditionalFilterResult result = new ConditionalFilterResult();
		
		ConfigurazioneCondizionale config = read(pa, log);
		
		
		TipoSelettore tipoSelettore = null;
		String patternSelettore = null;
		String prefixSelettore = null;
		String suffixSelettore = null;
		String staticInfo = null;
		if(busta.getAzione()!=null && !"".equals(busta.getAzione())) {
			ConfigurazioneSelettoreCondizioneRegola regola = null;
			try {
				regola = config.getRegolaByOperazione(busta.getAzione());
			}catch(Exception e) {
				throw new BehaviourException(e.getMessage(),e);
			}
			if(regola!=null) {
				tipoSelettore = regola.getTipoSelettore();
				patternSelettore = regola.getPattern();
				prefixSelettore = regola.getPrefix();
				suffixSelettore = regola.getSuffix();
				staticInfo = regola.getStaticInfo();
				result.setRegola(regola.getRegola());
			}
		}
		if(tipoSelettore==null) {
			ConfigurazioneSelettoreCondizione c = config.getDefaultConfig();
			tipoSelettore = c.getTipoSelettore();
			patternSelettore = c.getPattern();
			prefixSelettore = c.getPrefix();
			suffixSelettore = c.getSuffix();
		}
		
		result.setByFilter(config.isByFilter());
		
		String condition = null;
		String nomeConnettoreDaUsare = null;
		if(staticInfo!=null && !"".equals(staticInfo)) {
			condition = staticInfo;
			msgDiag.addKeyword(CostantiPdD.KEY_TIPO_SELETTORE, ModalitaIdentificazione.STATIC.getLabel());
			msgDiag.addKeyword(CostantiPdD.KEY_PATTERN_SELETTORE, ""); // per eliminare @@ dove non serve
		}
		else {
			String pattern = "";
			try {
				Map<String, String> pTrasporto = null;
				String urlInvocazione = null;
				Map<String, String> pForm = null;
				if(requestInfo!=null && requestInfo.getProtocolContext()!=null) {
					pTrasporto = requestInfo.getProtocolContext().getParametersTrasporto();
					urlInvocazione = requestInfo.getProtocolContext().getUrlInvocazione_formBased();
					pForm = requestInfo.getProtocolContext().getParametersFormBased();
				}
				Element element = null;
				String elementJson = null;
				if(TipoSelettore.CONTENT_BASED.equals(tipoSelettore) || tipoSelettore.isTemplate()) {
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
							throw new Exception("Selettore '"+tipoSelettore.getValue()+"' non supportato per il message-type '"+message.getMessageType()+"'");
						}
					}
				}
				
				msgDiag.addKeyword(CostantiPdD.KEY_TIPO_SELETTORE, tipoSelettore.getValue());
				msgDiag.addKeyword(CostantiPdD.KEY_PATTERN_SELETTORE, pattern); // per eliminare @@ dove non serve
				
				switch (tipoSelettore) {
				
				case HEADER_BASED:
					pattern = " (Header HTTP: "+patternSelettore+")";
					msgDiag.addKeyword(CostantiPdD.KEY_PATTERN_SELETTORE, pattern);
					condition = TransportUtils.get(pTrasporto, patternSelettore);
					if(condition==null) {
						throw new Exception("header non presente");
					}
					break;
					
				case URLBASED:
					pattern = " (Espressione Regolare: "+patternSelettore+")";
					msgDiag.addKeyword(CostantiPdD.KEY_PATTERN_SELETTORE, pattern);
					try{
						condition = RegularExpressionEngine.getStringMatchPattern(urlInvocazione, patternSelettore);
					}catch(RegExpNotFoundException notFound){}
					break;
					
				case FORM_BASED:
					pattern = " (Parametro URL: "+patternSelettore+")";
					msgDiag.addKeyword(CostantiPdD.KEY_PATTERN_SELETTORE, pattern);
					condition = TransportUtils.get(pForm, patternSelettore);
					if(condition==null) {
						throw new Exception("parametro della url non presente");
					}
					break;
					
				case CONTENT_BASED:
					AbstractXPathExpressionEngine xPathEngine = null;
					if(element!=null) {
						pattern = " (xPath: "+patternSelettore+")";
						msgDiag.addKeyword(CostantiPdD.KEY_PATTERN_SELETTORE, pattern);
						xPathEngine = new org.openspcoop2.message.xml.XPathExpressionEngine(message.getFactory());
						condition = AbstractXPathExpressionEngine.extractAndConvertResultAsString(element, xPathEngine, patternSelettore,  log);
					}
					else {
						pattern = " (jsonPath: "+patternSelettore+")";
						msgDiag.addKeyword(CostantiPdD.KEY_PATTERN_SELETTORE, pattern);
						condition = JsonXmlPathExpressionEngine.extractAndConvertResultAsString(elementJson, patternSelettore, log);
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
					
				case TEMPLATE:
					if(patternSelettore.length()<50) {
						pattern = " ("+patternSelettore+")";
					}
					else {
						pattern = "";
					}
					msgDiag.addKeyword(CostantiPdD.KEY_PATTERN_SELETTORE, pattern);
					Map<String, Object> dynamicMap = new HashMap<String, Object>();
					ErrorHandler errorHandler = new ErrorHandler();
					DynamicUtils.fillDynamicMapRequest(log, dynamicMap, pddContext, urlInvocazione,
							message,
							element, elementJson, 
							busta, pTrasporto, pForm,
							errorHandler);
					condition = DynamicUtils.convertDynamicPropertyValue("ConditionalConfig.gwt", patternSelettore, dynamicMap, pddContext, true);
					break;
					
				case FREEMARKER_TEMPLATE:
					if(patternSelettore.length()<50) {
						pattern = " ("+patternSelettore+")";
					}
					else {
						pattern = "";
					}
					msgDiag.addKeyword(CostantiPdD.KEY_PATTERN_SELETTORE, pattern);
					dynamicMap = new HashMap<String, Object>();
					errorHandler = new ErrorHandler();
					DynamicUtils.fillDynamicMapRequest(log, dynamicMap, pddContext, urlInvocazione,
							message,
							element, elementJson, 
							busta, pTrasporto, pForm,
							errorHandler);
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					DynamicUtils.convertFreeMarkerTemplate("ConditionalConfig.ftl", patternSelettore.getBytes(), dynamicMap, bout);
					bout.flush();
					bout.close();
					condition = bout.toString();
					break;
					
				case VELOCITY_TEMPLATE:
					if(patternSelettore.length()<50) {
						pattern = " ("+patternSelettore+")";
					}
					else {
						pattern = "";
					}
					msgDiag.addKeyword(CostantiPdD.KEY_PATTERN_SELETTORE, pattern);
					dynamicMap = new HashMap<String, Object>();
					errorHandler = new ErrorHandler();
					DynamicUtils.fillDynamicMapRequest(log, dynamicMap, pddContext, urlInvocazione,
							message,
							element, elementJson, 
							busta, pTrasporto, pForm,
							errorHandler);
					bout = new ByteArrayOutputStream();
					DynamicUtils.convertVelocityTemplate("ConditionalConfig.vm", patternSelettore.getBytes(), dynamicMap, bout);
					bout.flush();
					bout.close();
					condition = bout.toString();
					break;
				}
			
				if(condition==null || "".equals(condition)) {
					throw new Exception("Nessuna condizione estratta");
				}
				else {
					msgDiag.addKeyword(CostantiPdD.KEY_CONDIZIONE_CONNETTORE, condition);
					
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
							"connettoriMultipli.consegnaCondizionale.identificazioneRiuscita");
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
					
					if(BehaviourType.CONSEGNA_LOAD_BALANCE.equals(behaviourType)) {
						// li usero tutti senza filtro sulla condizionalità
						
						msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
								"connettoriMultipli.consegnaCondizionale.loadBalancer.tuttiConnettori");
						
						result.setListServiziApplicativi(getAllEnabled(pa.getServizioApplicativoList()));
						return result;
					}
					
					// se arrivo qua, sicuramente non sono in load balance mode
					nomeConnettoreDaUsare = config.getCondizioneNonIdentificata().getNomeConnettore();
					if(nomeConnettoreDaUsare==null || "".equals(nomeConnettoreDaUsare)) {
						
						if(BehaviourType.CONSEGNA_MULTIPLA.equals(behaviourType)) {
							
							msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
									"connettoriMultipli.consegnaCondizionale.tuttiConnettori");
							
							result.setListServiziApplicativi(getAllEnabled(pa.getServizioApplicativoList()));
							return result;
						}
						else if(BehaviourType.CONSEGNA_CONDIZIONALE.equals(behaviourType)) {
							// non può succedere
							throw new BehaviourException("Connettore da utilizzare in caso di identificazione fallita non indicato");
						}
						else if(BehaviourType.CONSEGNA_CON_NOTIFICHE.equals(behaviourType)) {
							
							msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
									"connettoriMultipli.consegnaCondizionale.tuttiConnettori");
							
							result.setListServiziApplicativi(getAllEnabled(pa.getServizioApplicativoList()));
							return result;
						} 
												
					}
					else if(BehaviourType.CONSEGNA_CON_NOTIFICHE.equals(behaviourType) && 
							Costanti.CONDITIONAL_NOME_CONNETTORE_VALORE_NESSUNO.equals(nomeConnettoreDaUsare)) {
													
						msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
								"connettoriMultipli.consegnaCondizionale.nessunConnettore");
						
						result.setListServiziApplicativi(new ArrayList<>());
						return result;
												
					}
					
					result.setListServiziApplicativi(filter(pa.getServizioApplicativoList(),false,nomeConnettoreDaUsare));
					if(result.getListServiziApplicativi().isEmpty()) {
						throw new BehaviourException("Connettore '"+nomeConnettoreDaUsare+"' indicato, da utilizzare in caso di identificazione fallita, non esistente");
					}
					
					msgDiag.addKeyword(CostantiPdD.KEY_NOME_CONNETTORE, nomeConnettoreDaUsare);
					if(BehaviourType.CONSEGNA_CON_NOTIFICHE.equals(behaviourType)) {
						msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
								"connettoriMultipli.consegnaCondizionale.connettoreNotificaDefault");
					}
					else {
						msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
								"connettoriMultipli.consegnaCondizionale.connettoreDefault");
					}
					
					return result;
				}
			}
		}
		
		String conditionFinal = condition;
		if(prefixSelettore!=null) {
			conditionFinal = prefixSelettore+conditionFinal;
		}
		if(suffixSelettore!=null) {
			conditionFinal = conditionFinal+suffixSelettore;
		}
		result.setCondition(conditionFinal);
		msgDiag.addKeyword(CostantiPdD.KEY_CONDIZIONE_CONNETTORE, conditionFinal);
		
		List<PortaApplicativaServizioApplicativo> l = filter(pa.getServizioApplicativoList(), config.isByFilter(), conditionFinal);
		if(!l.isEmpty()) {
			
			if(BehaviourType.CONSEGNA_CONDIZIONALE.equals(behaviourType)) {
				if(l.size()>1) {
					throw new BehaviourEmitDiagnosticException(msgDiag, MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
							"connettoriMultipli.servizioSincrono.consegnaVersoNServiziApplicativi");
				}
			}
			
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
				
				if(BehaviourType.CONSEGNA_LOAD_BALANCE.equals(behaviourType)) {
					// li usero tutti senza filtro sulla condizionalità
					
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
							"connettoriMultipli.consegnaCondizionale.loadBalancer.tuttiConnettori");
					
					result.setListServiziApplicativi(getAllEnabled(pa.getServizioApplicativoList()));
					return result;
				}
				
				// se arrivo qua, sicuramente non sono in load balance mode
				nomeConnettoreDaUsare = config.getNessunConnettoreTrovato().getNomeConnettore();
				if(nomeConnettoreDaUsare==null || "".equals(nomeConnettoreDaUsare)) {
					
					if(BehaviourType.CONSEGNA_MULTIPLA.equals(behaviourType)) {
						
						msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
								"connettoriMultipli.consegnaCondizionale.tuttiConnettori");
						
						result.setListServiziApplicativi(getAllEnabled(pa.getServizioApplicativoList()));
						return result;
					}
					else if(BehaviourType.CONSEGNA_CONDIZIONALE.equals(behaviourType)) {
						// non può succedere
						throw new BehaviourException("Connettore da utilizzare in caso di identificazione del connettore fallita non indicato");
					}
					else if(BehaviourType.CONSEGNA_CON_NOTIFICHE.equals(behaviourType)) {
						
						msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
								"connettoriMultipli.consegnaCondizionale.tuttiConnettori");
						
						result.setListServiziApplicativi(getAllEnabled(pa.getServizioApplicativoList()));
						return result;
					} 
											
				}
				else if(BehaviourType.CONSEGNA_CON_NOTIFICHE.equals(behaviourType) && 
						Costanti.CONDITIONAL_NOME_CONNETTORE_VALORE_NESSUNO.equals(nomeConnettoreDaUsare)) {
												
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
							"connettoriMultipli.consegnaCondizionale.nessunConnettore");
					
					result.setListServiziApplicativi(new ArrayList<>());
					return result;
											
				}
				
				result.setListServiziApplicativi(filter(pa.getServizioApplicativoList(),false,nomeConnettoreDaUsare));
				if(result.getListServiziApplicativi().isEmpty()) {
					throw new BehaviourException("Connettore '"+nomeConnettoreDaUsare+"' indicato, da utilizzare in caso di identificazione condizionale fallita, non esistente");
				}
				
				msgDiag.addKeyword(CostantiPdD.KEY_NOME_CONNETTORE, nomeConnettoreDaUsare);
				if(BehaviourType.CONSEGNA_CON_NOTIFICHE.equals(behaviourType)) {
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
							"connettoriMultipli.consegnaCondizionale.connettoreNotificaDefault");
				}
				else {
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
							"connettoriMultipli.consegnaCondizionale.connettoreDefault");
				}
				
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
	private static List<PortaApplicativaServizioApplicativo> getAllEnabled(List<PortaApplicativaServizioApplicativo> listPASA){
		
		List<PortaApplicativaServizioApplicativo> l = new ArrayList<>();
		
		for (PortaApplicativaServizioApplicativo servizioApplicativo : listPASA) {
			
			if(servizioApplicativo.getDatiConnettore()==null || servizioApplicativo.getDatiConnettore().getStato()==null || 
					StatoFunzionalita.ABILITATO.equals(servizioApplicativo.getDatiConnettore().getStato())) {
				l.add(servizioApplicativo);
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
		
		List<String> idRegole = new ArrayList<String>();
		
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
				
				else if(nome.startsWith(Costanti.CONDITIONAL_RULE) && nome.endsWith(Costanti.CONDITIONAL_RULE_NAME) ) {
					String idRegola = nome.substring(Costanti.CONDITIONAL_RULE.length(), (nome.length()-Costanti.CONDITIONAL_RULE_NAME.length()));
					idRegole.add(idRegola);
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
		
		if(!idRegole.isEmpty()) {
			for (String idRegola : idRegole) {
				
				String prefixGruppo = Costanti.CONDITIONAL_RULE+idRegola;
				String prefixGruppoConUnderscore = prefixGruppo+"_";
				ConfigurazioneSelettoreCondizioneRegola selettoreConfigurazioneRegola = new ConfigurazioneSelettoreCondizioneRegola();
				
				for (Proprieta p : pa.getBehaviour().getProprietaList()) {
					
					String nome = p.getNome();
					String valore = p.getValore().trim();
					
					try {
						if((prefixGruppo+Costanti.CONDITIONAL_RULE_NAME).equals(nome)) {
							selettoreConfigurazioneRegola.setRegola(valore);
						}
						else if((prefixGruppoConUnderscore+Costanti.CONDITIONAL_RULE_PATTERN_OPERAZIONE).equals(nome)) {
							selettoreConfigurazioneRegola.setPatternOperazione(valore);
						}
						else if((prefixGruppoConUnderscore+Costanti.CONDITIONAL_RULE_STATIC_INFO).equals(nome)) {
							selettoreConfigurazioneRegola.setStaticInfo(valore);
						}
						
						else if((prefixGruppoConUnderscore+Costanti.CONDITIONAL_TIPO_SELETTORE).equals(nome)) {
							selettoreConfigurazioneRegola.setTipoSelettore(TipoSelettore.toEnumConstant(valore, true));
						}
						else if((prefixGruppoConUnderscore+Costanti.CONDITIONAL_PATTERN).equals(nome)) {
							selettoreConfigurazioneRegola.setPattern(valore);
						}
						else if((prefixGruppoConUnderscore+Costanti.CONDITIONAL_PREFIX).equals(nome)) {
							selettoreConfigurazioneRegola.setPrefix(valore);
						}
						else if((prefixGruppoConUnderscore+Costanti.CONDITIONAL_SUFFIX).equals(nome)) {
							selettoreConfigurazioneRegola.setSuffix(valore);
						}
					}catch(Exception e) {
						throw new BehaviourException("Configurazione condizionale non corretta (proprietà:"+p.getNome()+" valore:'"+p.getValore()+"'): "+e.getMessage(),e);
					}					
					
				}
				
				config.addRegola(selettoreConfigurazioneRegola);
				
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
		BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),Costanti.CONDITIONAL_ENABLED, true+"");
		
		BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),Costanti.CONDITIONAL_BY_FILTER, configurazione.isByFilter()+"");
				
		if(configurazione.getDefaultConfig()==null) {
			throw new BehaviourException("Configurazione selettore condizione di default non fornita");
		}
		BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),Costanti.CONDITIONAL_TIPO_SELETTORE, configurazione.getDefaultConfig().getTipoSelettore().getValue());
		if(StringUtils.isNotEmpty(configurazione.getDefaultConfig().getPattern())) {
			BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),Costanti.CONDITIONAL_PATTERN, configurazione.getDefaultConfig().getPattern());
		}else {
			BehaviourPropertiesUtils.removeProprieta(pa.getBehaviour(),Costanti.CONDITIONAL_PATTERN);
		}
		if(StringUtils.isNotEmpty(configurazione.getDefaultConfig().getPrefix())) {
			BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),Costanti.CONDITIONAL_PREFIX, configurazione.getDefaultConfig().getPrefix());
		}else {
			BehaviourPropertiesUtils.removeProprieta(pa.getBehaviour(),Costanti.CONDITIONAL_PREFIX);
		}
		if(StringUtils.isNotEmpty(configurazione.getDefaultConfig().getSuffix())) {
			BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),Costanti.CONDITIONAL_SUFFIX, configurazione.getDefaultConfig().getSuffix());
		}else {
			BehaviourPropertiesUtils.removeProprieta(pa.getBehaviour(),Costanti.CONDITIONAL_SUFFIX);
		}
		
		List<String> listProprietaDaRimuovere = new ArrayList<>();
		for (Proprieta p : pa.getBehaviour().getProprietaList()) {
			if(p.getNome().startsWith(Costanti.CONDITIONAL_RULE)) {
				listProprietaDaRimuovere.add(p.getNome());
			}
		}
		if(!listProprietaDaRimuovere.isEmpty()) {
			for (String propertyName : listProprietaDaRimuovere) {
				BehaviourPropertiesUtils.removeProprieta(pa.getBehaviour(), propertyName);
			}
		}
		if(configurazione.getRegoleOrdinate()!=null && !configurazione.getRegoleOrdinate().isEmpty()) {
			int indexRegola = 1;
			for (String nomeRegola : configurazione.getRegoleOrdinate()) {
				
				String prefixRegola = Costanti.CONDITIONAL_RULE+indexRegola;
				BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),(prefixRegola+Costanti.CONDITIONAL_RULE_NAME),nomeRegola);
				
				ConfigurazioneSelettoreCondizioneRegola regola = configurazione.getRegola(nomeRegola);
				
				BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),(prefixRegola+"_"+Costanti.CONDITIONAL_RULE_PATTERN_OPERAZIONE),regola.getPatternOperazione());
				if(StringUtils.isNotEmpty(regola.getStaticInfo())) {
					BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),(prefixRegola+"_"+Costanti.CONDITIONAL_RULE_STATIC_INFO),regola.getStaticInfo());
				}
				
				if(regola.getTipoSelettore()!=null) {
					BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),(prefixRegola+"_"+Costanti.CONDITIONAL_TIPO_SELETTORE),regola.getTipoSelettore().getValue());
				}
				if(StringUtils.isNotEmpty(regola.getPattern())) {
					BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),(prefixRegola+"_"+Costanti.CONDITIONAL_PATTERN),regola.getPattern());
				}
				if(StringUtils.isNotEmpty(regola.getPrefix())) {
					BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),(prefixRegola+"_"+Costanti.CONDITIONAL_PREFIX),regola.getPrefix());
				}
				if(StringUtils.isNotEmpty(regola.getSuffix())) {
					BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),(prefixRegola+"_"+Costanti.CONDITIONAL_SUFFIX), regola.getSuffix());
				}
				
				indexRegola++;
			}
		}
		
		if(configurazione.getCondizioneNonIdentificata()==null) {
			throw new BehaviourException("Configurazione 'condizione non identificata' non fornita");
		}
		BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),(Costanti.CONDITIONAL_CONDIZIONE_NON_IDENTIFICATA+Costanti.CONDITIONAL_ABORT_TRANSACTION),
				configurazione.getCondizioneNonIdentificata().isAbortTransaction()+"");
		BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),(Costanti.CONDITIONAL_CONDIZIONE_NON_IDENTIFICATA+Costanti.CONDITIONAL_EMIT_DIAGNOSTIC_INFO),
				configurazione.getCondizioneNonIdentificata().isEmitDiagnosticInfo()+"");
		BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),(Costanti.CONDITIONAL_CONDIZIONE_NON_IDENTIFICATA+Costanti.CONDITIONAL_EMIT_DIAGNOSTIC_ERROR),
				configurazione.getCondizioneNonIdentificata().isEmitDiagnosticError()+"");
		if(StringUtils.isNotEmpty(configurazione.getCondizioneNonIdentificata().getNomeConnettore())) {
			BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),(Costanti.CONDITIONAL_CONDIZIONE_NON_IDENTIFICATA+Costanti.CONDITIONAL_NOME_CONNETTORE),
					configurazione.getCondizioneNonIdentificata().getNomeConnettore());
		}
		
		if(configurazione.getNessunConnettoreTrovato()==null) {
			throw new BehaviourException("Configurazione 'nessun connettore trovato' non fornita");
		}
		BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),(Costanti.CONDITIONAL_NESSUN_CONNETTORE_TROVATO+Costanti.CONDITIONAL_ABORT_TRANSACTION),
				configurazione.getNessunConnettoreTrovato().isAbortTransaction()+"");
		BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),(Costanti.CONDITIONAL_NESSUN_CONNETTORE_TROVATO+Costanti.CONDITIONAL_EMIT_DIAGNOSTIC_INFO),
				configurazione.getNessunConnettoreTrovato().isEmitDiagnosticInfo()+"");
		BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),(Costanti.CONDITIONAL_NESSUN_CONNETTORE_TROVATO+Costanti.CONDITIONAL_EMIT_DIAGNOSTIC_ERROR),
				configurazione.getNessunConnettoreTrovato().isEmitDiagnosticError()+"");
		if(StringUtils.isNotEmpty(configurazione.getNessunConnettoreTrovato().getNomeConnettore())) {
			BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),(Costanti.CONDITIONAL_NESSUN_CONNETTORE_TROVATO+Costanti.CONDITIONAL_NOME_CONNETTORE),
					configurazione.getNessunConnettoreTrovato().getNomeConnettore());
		}
	}
	
}
