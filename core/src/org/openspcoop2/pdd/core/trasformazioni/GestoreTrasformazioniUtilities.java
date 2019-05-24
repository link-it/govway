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



package org.openspcoop2.pdd.core.trasformazioni;

import java.io.ByteArrayOutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPElement;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaServizioApplicativo;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaSoggetto;
import org.openspcoop2.core.config.TrasformazioneRegolaParametro;
import org.openspcoop2.core.config.constants.TrasformazioneRegolaParametroTipoAzione;
import org.openspcoop2.core.config.constants.VersioneSOAP;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.dynamic.Costanti;
import org.openspcoop2.pdd.core.dynamic.DynamicInfo;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.core.dynamic.PatternExtractor;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.utils.dch.DataContentHandlerManager;
import org.openspcoop2.utils.dch.InputStreamDataSource;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.TransportResponseContext;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;
import org.w3c.dom.Element;

/**
 * Gestione delle trasformazioni
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class GestoreTrasformazioniUtilities {

	// NOTA: uso volutamente le stesse costanti del connettore File
	
	public static void fillDynamicMapRequest(Logger log, Map<String, Object> dynamicMap, PdDContext pddContext, String urlInvocazione,
			Element element,
			String elementJson,
			Busta busta, Properties trasporto, Properties url) {
		_fillDynamicMap(log, dynamicMap, pddContext, urlInvocazione, 
				element, 
				elementJson, 
				busta, trasporto, url);	
    }
	public static void fillDynamicMapResponse(Logger log, Map<String, Object> dynamicMap, Map<String, Object> dynamicMapRequest, PdDContext pddContext,
			Element element,
			String elementJson,
			Busta busta, Properties trasporto) {
		Map<String, Object> dynamicMapResponse = new HashMap<>();
		_fillDynamicMap(log, dynamicMapResponse, pddContext, null, 
				element, 
				elementJson, 
				busta, trasporto, null);
		if(dynamicMapResponse!=null && !dynamicMapResponse.isEmpty()) {
			Iterator<String> it = dynamicMapResponse.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				Object o = dynamicMapResponse.get(key);
				dynamicMap.put(key+Costanti.MAP_SUFFIX_RESPONSE, o);
			}
		}
		if(dynamicMapRequest!=null && !dynamicMapRequest.isEmpty()) {
			Iterator<String> it = dynamicMapRequest.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				Object o = dynamicMapRequest.get(key);
				if(o instanceof PatternExtractor) {
					PatternExtractor pe = (PatternExtractor) o;
					pe.refreshContent();
				}
				dynamicMap.put(key, o);
			}
		}
	}
	public static void _fillDynamicMap(Logger log, Map<String, Object> dynamicMap, PdDContext pddContext, String urlInvocazione,
			Element element,
			String elementJson,
			Busta busta, Properties trasporto, Properties url) {
		DynamicInfo dInfo = new DynamicInfo();
		dInfo.setBusta(busta);
		dInfo.setPddContext(pddContext);
		if(trasporto!=null && !trasporto.isEmpty()) {
			Properties pNew = new Properties();
			pNew.putAll(trasporto);
			dInfo.setTrasporto(pNew);
		}
		if(url!=null && !url.isEmpty()) {
			Properties pNew = new Properties();
			pNew.putAll(url);
			dInfo.setQueryParameters(pNew);
		}
		if(urlInvocazione!=null) {
			dInfo.setUrl(urlInvocazione);
		}
		if(element!=null) {
			dInfo.setXml(element);
		}
		else if(elementJson!=null) {
			dInfo.setJson(elementJson);
		}
		DynamicUtils.fillDynamicMap(log, dynamicMap, dInfo);		
    }
	
	public static boolean isMatchContentType(String contentType, List<String> contentTypeCheckList) throws Exception {
		
		if(contentTypeCheckList==null || contentTypeCheckList.size()<=0) {
			return true;
		}
		
		boolean found = false;
		for (String checkContentType : contentTypeCheckList) {
			if("empty".equals(checkContentType)){
				if(contentType==null || "".equals(contentType)) {
					found = true;
					break;
				}
			}
			else {
				if(contentType==null) {
					continue;
				}
				if(checkContentType==null || "".equals(checkContentType) ||
						checkContentType.contains("/")==false ||
						checkContentType.startsWith("/") ||
						checkContentType.endsWith("/")) {
					throw new Exception("Configurazione errata, content type indicato ("+checkContentType+") possiede un formato non corretto (atteso: type/subtype)");
				}
				String [] ctVerifica = checkContentType.split("/");
				if(ctVerifica!=null && ctVerifica.length==2) {
					StringBuffer bf = new StringBuffer();
					String part1 = ctVerifica[0].trim();
					if("*".equals(part1)) {
						bf.append("(.+)");
					}
					else {
						bf.append(part1);
					}
					bf.append("/");
					String part2 = ctVerifica[1].trim();
					if("*".equals(part2)) {
						bf.append("(.+)");
					}
					else if(part2.startsWith("*")) {
						bf.append("(.+)");
						bf.append(part2.substring(1));
					}
					else {
						bf.append(part2);
					}
					checkContentType = bf.toString();
				}
				if(RegularExpressionEngine.isMatch(contentType, checkContentType)) {
					found = true;
					break;
				}
			}
			
		}
		return found;
	}
	
	public static boolean isMatchServizioApplicativo(IDSoggetto soggettoFruitore, String sa, List<TrasformazioneRegolaApplicabilitaServizioApplicativo> saCheckList) throws Exception {
		
		if(saCheckList==null || saCheckList.size()<=0) {
			return true;
		}
		
		if(soggettoFruitore==null || soggettoFruitore.getTipo()==null || soggettoFruitore.getNome()==null || sa==null) {
			return true;
		}
		
		for (TrasformazioneRegolaApplicabilitaServizioApplicativo saCheck : saCheckList) {
			if(soggettoFruitore.getTipo().equals(saCheck.getTipoSoggettoProprietario()) &&
					soggettoFruitore.getNome().equals(saCheck.getNomeSoggettoProprietario()) &&
					sa.equals(saCheck.getNome()) ) {
				return true;
			}			
		}
		
		return false;
		
	}
	
	public static boolean isMatchSoggetto(IDSoggetto soggettoFruitore, List<TrasformazioneRegolaApplicabilitaSoggetto> soggettoCheckList) throws Exception {
		
		if(soggettoCheckList==null || soggettoCheckList.size()<=0) {
			return true;
		}
		
		if(soggettoFruitore==null || soggettoFruitore.getTipo()==null || soggettoFruitore.getNome()==null) {
			return true;
		}
		
		for (TrasformazioneRegolaApplicabilitaSoggetto soggettoCheck : soggettoCheckList) {
			if(soggettoFruitore.getTipo().equals(soggettoCheck.getTipo()) &&
					soggettoFruitore.getNome().equals(soggettoCheck.getNome()) ) {
				return true;
			}			
		}
		
		return false;
		
	}
	
	public static void trasformazione(Logger log, List<TrasformazioneRegolaParametro> list, Properties properties, Properties forceAddProperties, String oggetto,
			Map<String, Object> dynamicMap, PdDContext pddContext) throws Exception {
		if(list!=null && list.size()>0) {
			for (TrasformazioneRegolaParametro parametro : list) {
				TrasformazioneRegolaParametroTipoAzione tipo = parametro.getConversioneTipo();
				String nome = parametro.getNome();
				String valore = parametro.getValore();
				log.debug("Trasformazione ["+oggetto+"] parametro '"+nome+"', tipo operazione '"+tipo+"' ...");
				if(nome==null) {
					throw new Exception("["+oggetto+"] Nome non indicato");
				}
				if(tipo==null) {
					throw new Exception("["+oggetto+"] Tipo di operazione da effettuare non indicata per '"+nome+"'");
				}
				if(valore!=null) {
					try {
						valore = DynamicUtils.convertDynamicPropertyValue(oggetto+"-"+nome, valore, dynamicMap, pddContext, true);
					}catch(Exception e) {
						throw new Exception("["+oggetto+"] Conversione valore per '"+nome+"' non riuscita (valore: "+valore+"): "+e.getMessage(),e);
					}
				}
				switch (tipo) {
				case ADD:
					if(valore==null) {
						throw new Exception("["+oggetto+"] Valore per '"+nome+"' non indicato");
					}
					Object vOld = null;
					if(properties!=null) {
						vOld = properties.remove(nome);
						if(vOld==null) {
							vOld = properties.remove(nome.toLowerCase());
						}
						if(vOld==null) {
							vOld = properties.remove(nome.toUpperCase());
						}
						if(vOld!=null) {
							throw new Exception("["+oggetto+"] '"+nome+"' già esistente; utilizzare direttiva 'updateOrAdd' per aggiornarlo");
						}
					}
					if(properties!=null) {
						properties.put(nome, valore);
					}
					forceAddProperties.put(nome, valore);
					break;
				case UPDATE_OR_ADD:
					if(valore==null) {
						throw new Exception("["+oggetto+"] Valore per '"+nome+"' non indicato");
					}
					if(properties!=null) {
						properties.remove(nome);
						properties.remove(nome.toLowerCase());
						properties.remove(nome.toUpperCase());
						properties.put(nome, valore);
					}
					forceAddProperties.put(nome, valore);
					break;
				case UPDATE:
					Object v = null;
					if(properties!=null) {
						v = properties.remove(nome);
						if(v==null) {
							v = properties.remove(nome.toLowerCase());
						}
						if(v==null) {
							v = properties.remove(nome.toUpperCase());
						}
						if(v!=null) {
							if(valore==null) {
								throw new Exception("["+oggetto+"] Valore del parametro '"+nome+"' non indicato");
							}
							properties.put(nome, valore);
							forceAddProperties.put(nome, valore);
						}
					}
					break;
				case DELETE:
					if(properties!=null) {
						properties.remove(nome);
						properties.remove(nome.toLowerCase());
						properties.remove(nome.toUpperCase());
					}
					break;
				default:
					break;
				}
			}
		}
	}
	
	public static RisultatoTrasformazioneContenuto trasformazioneContenuto(Logger log, String tipoConversioneContenuto, 
			byte[] contenuto, String oggetto, Map<String, Object> dynamicMap, OpenSPCoop2Message msg, Element element, PdDContext pddContext) throws Exception {
		TipoTrasformazione tipoTrasformazione = null;
		if(tipoConversioneContenuto!=null) {
			tipoTrasformazione = TipoTrasformazione.toEnumConstant(tipoConversioneContenuto, true);
		}
		else {
			throw new Exception("Tipo di conversione contenuto non indicato");
		}
		
		log.debug("Trasformazione "+oggetto+" ["+tipoConversioneContenuto+"] ...");
		
		RisultatoTrasformazioneContenuto risultato = new RisultatoTrasformazioneContenuto();
		
		// conversione formato

		switch (tipoTrasformazione) {

		case EMPTY:
			log.debug("trasformazione "+oggetto+" del contenuto empty");
			risultato.setEmpty(true);
			break;
			
		case TEMPLATE:
			if(contenuto==null) {
				throw new Exception("Template "+oggetto+" non definito");
			}
						
			log.debug("trasformazione "+oggetto+" ["+tipoTrasformazione+"], lettura template ...");
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			bout.write(contenuto);
			bout.flush();
			bout.close();
			
			log.debug("trasformazione "+oggetto+" ["+tipoTrasformazione+"], risoluzione template ...");
			String newContent = DynamicUtils.convertDynamicPropertyValue("template", bout.toString(), dynamicMap, pddContext, true);
			log.debug("trasformazione "+oggetto+" ["+tipoTrasformazione+"], risoluzione template completata");
			if(newContent==null || "".equals(newContent)) {
				risultato.setEmpty(true);
			}
			else {
				risultato.setContenuto(newContent.getBytes(), newContent);
			}
			
			break;
			
		case FREEMARKER_TEMPLATE:
			if(contenuto==null) {
				throw new Exception("Template "+oggetto+" non definito");
			}
						
			log.debug("trasformazione "+oggetto+" ["+tipoTrasformazione+"], risoluzione template ...");
			bout = new ByteArrayOutputStream();
			DynamicUtils.convertFreeMarkerTemplate("template", contenuto, dynamicMap, bout);
			bout.flush();
			bout.close();
			log.debug("trasformazione "+oggetto+" ["+tipoTrasformazione+"], risoluzione template completata");
			if(bout==null || bout.size()<=0) {
				risultato.setEmpty(true);
			}
			else {
				risultato.setContenuto(bout.toByteArray(), bout.toString());
			}
			
			break;
			
		case VELOCITY_TEMPLATE:
			if(contenuto==null) {
				throw new Exception("Template "+oggetto+" non definito");
			}
						
			log.debug("trasformazione "+oggetto+" ["+tipoTrasformazione+"], risoluzione template ...");
			bout = new ByteArrayOutputStream();
			DynamicUtils.convertVelocityTemplate("template", contenuto, dynamicMap, bout);
			bout.flush();
			bout.close();
			log.debug("trasformazione "+oggetto+" ["+tipoTrasformazione+"], risoluzione template completata");
			if(bout==null || bout.size()<=0) {
				risultato.setEmpty(true);
			}
			else {
				risultato.setContenuto(bout.toByteArray(), bout.toString());
			}
			
			break;
			
		case XSLT:
			if(contenuto==null) {
				throw new Exception("Template "+oggetto+" non definito");
			}
			if(element==null) {
				if(MessageType.XML.equals(msg.getMessageType()) ||
						MessageType.SOAP_11.equals(msg.getMessageType()) ||
						MessageType.SOAP_12.equals(msg.getMessageType())) {
					throw new Exception("Messaggio da convertire non presente");
				}
				else {
					throw new Exception("Template '"+tipoTrasformazione.getLabel()+"' non utilizzabile con messaggio di tipo '"+msg.getMessageType()+"'");
				}
			}
						
			log.debug("trasformazione "+oggetto+" ["+tipoTrasformazione+"], risoluzione template ...");
			bout = new ByteArrayOutputStream();
			DynamicUtils.convertXSLTTemplate("template", contenuto, element, bout);
			bout.flush();
			bout.close();
			log.debug("trasformazione "+oggetto+" ["+tipoTrasformazione+"], risoluzione template completata");
			if(bout==null || bout.size()<=0) {
				risultato.setEmpty(true);
			}
			else {
				risultato.setContenuto(bout.toByteArray(), bout.toString());
			}
			
			break;

		default:
			break;
		}
		return risultato;
	}
	
	
	public static OpenSPCoop2Message trasformaMessaggio(Logger log, OpenSPCoop2Message message, Element element, 
			RequestInfo requestInfo, Map<String, Object> dynamicMap, PdDContext pddContext, OpenSPCoop2Properties op2Properties,
			Properties trasporto, Properties forceAddTrasporto, 
			Properties url, Properties forceAddUrl,
			int status,
			String contentTypeInput, Integer returnCodeInput,
			RisultatoTrasformazioneContenuto risultato,
			boolean trasformazioneRest, 
			String trasformazioneRest_method, String trasformazioneRest_path,
			boolean trasformazioneSoap, 
			VersioneSOAP trasformazioneSoap_versione, String trasformazioneSoap_soapAction, 
			boolean trasformazioneSoap_envelope, boolean trasformazioneSoap_envelopeAsAttachment,
			String trasformazioneSoap_tipoConversione, byte[] trasformazioneSoap_templateConversione) throws Throwable {
		
		// TransportRequest
		Integer forceResponseStatus = null;
		TransportRequestContext transportRequestContext = null;
		TransportResponseContext transportResponseContext = null;
		if(MessageRole.REQUEST.equals(message.getMessageRole())) {
			transportRequestContext = new TransportRequestContext();
			transportRequestContext.setParametersTrasporto(trasporto);
			transportRequestContext.setParametersFormBased(url);
		}
		else {
			transportResponseContext = new TransportResponseContext();
			transportResponseContext.setParametersTrasporto(trasporto);
			if(returnCodeInput!=null) {
				transportResponseContext.setCodiceTrasporto(returnCodeInput.intValue()+"");
				forceResponseStatus = returnCodeInput;
			}
			else {
				transportResponseContext.setCodiceTrasporto(status+"");
				forceResponseStatus = status;
			}
		}
		
		log.debug("trasformazione conversione messaggio ...");
		boolean skipTransportInfo = true;
				
		if(ServiceBinding.SOAP.equals(message.getServiceBinding())){
			
			if(trasformazioneRest) {
			
				String contentType = contentTypeInput;
				if(risultato.isEmpty()) {
					contentType = null;
					// aggiorno contentType
					if(transportRequestContext!=null) {
						transportRequestContext.removeParameterTrasporto(HttpConstants.CONTENT_TYPE);
					}
					else {
						transportResponseContext.removeParameterTrasporto(HttpConstants.CONTENT_TYPE);
					}
				}
				else {
					if(contentType==null) {
						throw new Exception("Content-Type non indicato per la trasformazione REST");
					}
				}
				
				MessageType messageType = null;
				if(transportRequestContext!=null) {
					transportRequestContext.setRequestType(trasformazioneRest_method);
					String trasformazioneRest_path_real = null;
					if(trasformazioneRest_path!=null) {
						trasformazioneRest_path_real = DynamicUtils.convertDynamicPropertyValue("trasformazioneRest_path", trasformazioneRest_path, dynamicMap, pddContext, true);
					}
					transportRequestContext.setFunctionParameters(trasformazioneRest_path_real);
					messageType = requestInfo.getBindingConfig().getRequestMessageType(ServiceBinding.REST, transportRequestContext, contentType);
				}
				else {
					messageType = requestInfo.getBindingConfig().getResponseMessageType(ServiceBinding.REST, new TransportRequestContext(), contentType, status);
				}
				
				OpenSPCoop2Message newMsg = null;
				OpenSPCoop2MessageParseResult pr = null;
				if(risultato.isEmpty()) {
					if(transportRequestContext!=null) {
						newMsg = OpenSPCoop2MessageFactory.getMessageFactory().createEmptyMessage(messageType, MessageRole.REQUEST);
						newMsg.setTransportRequestContext(transportRequestContext);
					}
					else {
						newMsg = OpenSPCoop2MessageFactory.getMessageFactory().createEmptyMessage(messageType, MessageRole.RESPONSE);
						newMsg.setTransportResponseContext(transportResponseContext);
					}
				}
				else {
					if(transportRequestContext!=null) {
						pr = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(messageType, transportRequestContext, risultato.getContenuto());
					}
					else {
						pr = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(messageType, transportResponseContext, risultato.getContenuto());
					}
					newMsg = pr.getMessage_throwParseThrowable();
				}
				
				message.copyResourcesTo(newMsg, skipTransportInfo);
				
				addTransportInfo(forceAddTrasporto, forceAddUrl, forceResponseStatus, newMsg);
				return newMsg;
				
			}
			else {
				
				OpenSPCoop2SoapMessage soapMessage = message.castAsSoap();
				
				if(risultato.isEmpty()) {
					soapMessage.getSOAPPart().getEnvelope().getBody().removeContents();
				}
				else {
					SOAPElement newElement = soapMessage.createSOAPElement(risultato.getContenuto());
					if(element.getLocalName().equals(newElement.getLocalName()) && element.getNamespaceURI().equals(newElement.getNamespaceURI())) {
						// il nuovo elemento è una busta soap
						soapMessage.getSOAPPart().getEnvelope().detachNode();
						soapMessage.getSOAPPart().setContent(new DOMSource(newElement));
					}
					else {
						soapMessage.getSOAPPart().getEnvelope().getBody().removeContents();
						soapMessage.getSOAPPart().getEnvelope().getBody().addChildElement(newElement);
					}
				}
				
				addTransportInfo(forceAddTrasporto, forceAddUrl, forceResponseStatus, message);
				
				if(contentTypeInput!=null) {
					message.setContentType(contentTypeInput);
				}
				
				return message;
				
			}
			
		}
		else{
			
			if(trasformazioneSoap) {
				
				MessageType messageType = null;
				String contentTypeForEnvelope = null;
				if(VersioneSOAP._1_1.equals(trasformazioneSoap_versione)){
					messageType = MessageType.SOAP_11;
					if(contentTypeForEnvelope == null) {
						contentTypeForEnvelope = HttpConstants.CONTENT_TYPE_SOAP_1_1;
					}
				}
				else if(VersioneSOAP._1_2.equals(trasformazioneSoap_versione)){
					messageType = MessageType.SOAP_12;
					if(contentTypeForEnvelope == null) {
						contentTypeForEnvelope = HttpConstants.CONTENT_TYPE_SOAP_1_2;
					}
				}
				else {
					throw new Exception("Versione SOAP sconosciuta '"+trasformazioneSoap_versione+"'");
				}
				
				// aggiorno contentType
				if(transportRequestContext!=null) {
					transportRequestContext.removeParameterTrasporto(HttpConstants.CONTENT_TYPE);
					transportRequestContext.getParametersTrasporto().put(HttpConstants.CONTENT_TYPE, contentTypeForEnvelope);
				}
				else {
					transportResponseContext.removeParameterTrasporto(HttpConstants.CONTENT_TYPE);
					transportResponseContext.getParametersTrasporto().put(HttpConstants.CONTENT_TYPE, contentTypeForEnvelope);
				}
				
				String soapAction = null;
				if(trasformazioneSoap_soapAction!=null) {
					soapAction = DynamicUtils.convertDynamicPropertyValue("soapAction", trasformazioneSoap_soapAction, dynamicMap, pddContext, true);
				}
				
				OpenSPCoop2Message messageSoap = null;
				if(risultato.isEmpty()) {
					if(transportRequestContext!=null) {
						messageSoap = OpenSPCoop2MessageFactory.getMessageFactory().createEmptyMessage(messageType, MessageRole.REQUEST);
						messageSoap.setTransportRequestContext(transportRequestContext);
					}
					else {
						messageSoap = OpenSPCoop2MessageFactory.getMessageFactory().createEmptyMessage(messageType, MessageRole.RESPONSE);
						messageSoap.setTransportResponseContext(transportResponseContext);
					}
				}
				else {
												
					if(trasformazioneSoap_envelopeAsAttachment) {
						
						if(trasformazioneSoap_tipoConversione==null) {
							throw new Exception("Tipo di conversione del body interno all'envelope non fornito (SOAPWithAttachments)");
						}
						RisultatoTrasformazioneContenuto risultatoEnvelopeBody = 
								GestoreTrasformazioniUtilities.trasformazioneContenuto(log, 
										trasformazioneSoap_tipoConversione, 
										trasformazioneSoap_templateConversione, 
										"envelope-body", dynamicMap, message, element, pddContext);
						if(risultatoEnvelopeBody.isEmpty()) {
							if(transportRequestContext!=null) {
								messageSoap = OpenSPCoop2MessageFactory.getMessageFactory().createEmptyMessage(messageType, MessageRole.REQUEST);
								messageSoap.setTransportRequestContext(transportRequestContext);
							}
							else {
								messageSoap = OpenSPCoop2MessageFactory.getMessageFactory().createEmptyMessage(messageType, MessageRole.RESPONSE);
								messageSoap.setTransportResponseContext(transportResponseContext);
							}
						}
						else {
							if(trasformazioneSoap_envelope) {
								OpenSPCoop2MessageParseResult pr = null;
								if(transportRequestContext!=null) {
									pr = OpenSPCoop2MessageFactory.getMessageFactory().envelopingMessage(messageType, contentTypeForEnvelope, 
											soapAction, transportRequestContext, risultatoEnvelopeBody.getContenuto(), null, true);
								}
								else {
									pr = OpenSPCoop2MessageFactory.getMessageFactory().envelopingMessage(messageType, contentTypeForEnvelope, 
											soapAction, transportResponseContext, risultatoEnvelopeBody.getContenuto(), null, true);
								}
								messageSoap = pr.getMessage_throwParseThrowable();
							}
							else {
								OpenSPCoop2MessageParseResult pr = null;
								if(transportRequestContext!=null) {
									pr = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(messageType, transportRequestContext, 
											risultatoEnvelopeBody.getContenuto());
								}
								else {
									pr = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(messageType, transportResponseContext, 
											risultatoEnvelopeBody.getContenuto());
								}
								messageSoap = pr.getMessage_throwParseThrowable();
							}
						}
														
						// esiste un contenuto nel body
						AttachmentPart ap = null;

						String contentType = contentTypeInput;
						if(contentType==null) {
							throw new Exception("Content-Type non indicato per l'attachment");
						}
						String ctBase = ContentTypeUtilities.readBaseTypeFromContentType(contentType);
						if(HttpConstants.CONTENT_TYPE_TEXT_XML.equals(ctBase)) {
							
							// solo text/xml può essere costruito con DOMSource
							Source streamSource = null;
							DataContentHandlerManager dchManager = new DataContentHandlerManager(log);
							if(dchManager.readMimeTypesContentHandler().containsKey(ctBase)) {
								// Se è non registrato un content handler per text/xml
								// succede se dentro l'ear non c'e' il jar mailapi e l'application server non ha caricato il modulo mailapi (es. tramite versione standalone standard)
								// e si usa il metodo seguente DOMSource si ottiene il seguente errore:
								// javax.xml.soap.SOAPException: no object DCH for MIME type text/xml
								//    at com.sun.xml.messaging.saaj.soap.MessageImpl.writeTo(MessageImpl.java:1396) ~[saaj-impl-1.3.28.jar:?]
								//System.out.println("XML (DOMSource)");
								streamSource = new DOMSource(XMLUtils.getInstance().newElement(risultato.getContenuto()));
							}
							else {
								// Se è registrato un content handler per text/xml
								// e succede se dentro l'ear c'e' il jar mailapi oppure se l'application server ha caricato il modulo mailapi (es. tramite versione standalone full)
								// e si usa il metodo seguente StreamSource, si ottiene il seguente errore:
								//  Unable to run the JAXP transformer on a stream org.xml.sax.SAXParseException; Premature end of file. (sourceException: Error during saving a multipart message) 
								//  	com.sun.xml.messaging.saaj.SOAPExceptionImpl: Error during saving a multipart message
								//        at com.sun.xml.messaging.saaj.soap.MessageImpl.writeTo(MessageImpl.java:1396) ~[saaj-impl-1.3.28.jar:?]
								//        at org.openspcoop2.message.Message1_1_FIX_Impl.writeTo(Message1_1_FIX_Impl.java:172) ~[openspcoop2_message_BUILD-13516.jar:?]
								//        at org.openspcoop2.message.OpenSPCoop2Message_11_impl.writeTo
								//System.out.println("XML (StreamSource)");
								streamSource = new javax.xml.transform.stream.StreamSource(new java.io.ByteArrayInputStream(risultato.getContenuto()));
							}
							ap = messageSoap.castAsSoap().createAttachmentPart();
							ap.setContent(streamSource, contentType);
						}
						else {
							InputStreamDataSource isSource = new InputStreamDataSource("attach", contentType, risultato.getContenuto());
							ap = messageSoap.castAsSoap().createAttachmentPart(new DataHandler(isSource));
						}
						
						String contentID = messageSoap.castAsSoap().createContentID(op2Properties.getHeaderSoapActorIntegrazione());
						if(contentID.startsWith("<")){
							contentID = contentID.substring(1);
						}
						if(contentID.endsWith(">")){
							contentID = contentID.substring(0,contentID.length()-1);
						}
						
						ap.setContentId(contentID);
						messageSoap.castAsSoap().addAttachmentPart(ap);
							
					}
					else {
						
						if(trasformazioneSoap_envelope) {
							OpenSPCoop2MessageParseResult pr = null;
							if(transportRequestContext!=null) {
								pr = OpenSPCoop2MessageFactory.getMessageFactory().envelopingMessage(messageType, contentTypeForEnvelope, 
										soapAction, transportRequestContext, risultato.getContenuto(), null, true);
							}
							else {
								pr = OpenSPCoop2MessageFactory.getMessageFactory().envelopingMessage(messageType, contentTypeForEnvelope, 
										soapAction, transportResponseContext, risultato.getContenuto(), null, true);
							}
							messageSoap = pr.getMessage_throwParseThrowable();
						}
						else {
							OpenSPCoop2MessageParseResult pr = null;
							if(transportRequestContext!=null) {
								pr = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(messageType, transportRequestContext, risultato.getContenuto());
							}
							else {
								pr = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(messageType, transportResponseContext, risultato.getContenuto());
							}
							messageSoap = pr.getMessage_throwParseThrowable();
						}

					}
					
				}
				
				message.copyResourcesTo(messageSoap, skipTransportInfo);
				
				addTransportInfo(forceAddTrasporto, forceAddUrl, forceResponseStatus, messageSoap);
				
				if(messageSoap!=null && soapAction!=null) {
					messageSoap.castAsSoap().setSoapAction(soapAction);
				}
				
				return messageSoap;
				
			}
			else {
				
				if(risultato.isEmpty()) {
					
					message.castAsRest().updateContent(null);
					message.castAsRest().setContentType(null);
					
					addTransportInfo(forceAddTrasporto, forceAddUrl, forceResponseStatus, message);
					
					return message;
					
				}
				else {
					
					String contentType = null;
					if(contentTypeInput!=null) {
						contentType = contentTypeInput;
					}
					else {
						contentType = message.getContentType();
					}
					
					MessageType messageType = null;
					if(transportRequestContext!=null) {
						messageType = requestInfo.getBindingConfig().getRequestMessageType(ServiceBinding.REST, transportRequestContext, contentType);
					}
					else {
						messageType = requestInfo.getBindingConfig().getResponseMessageType(ServiceBinding.REST, new TransportRequestContext(), contentType, status);
					}
					if(messageType.equals(message.getMessageType())) {
						
						if(MessageType.XML.equals(messageType)) {
							
							message.castAsRestXml().updateContent(XMLUtils.getInstance().newElement(risultato.getContenuto()));
							
							addTransportInfo(forceAddTrasporto, forceAddUrl, forceResponseStatus, message);
							
							return message;
							
						}
						else if(MessageType.JSON.equals(messageType)) {
							
							message.castAsRestJson().updateContent(risultato.getContenutoAsString());
							
							addTransportInfo(forceAddTrasporto, forceAddUrl, forceResponseStatus, message);
							
							return message;
							
						}
						
					}
					
					// converto
					OpenSPCoop2MessageParseResult pr = null;
					if(transportRequestContext!=null) {
						pr = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(messageType, transportRequestContext, risultato.getContenuto());
					}
					else {
						pr = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(messageType, transportResponseContext, risultato.getContenuto());
					}
					OpenSPCoop2Message newMsg = pr.getMessage_throwParseThrowable();
					message.copyResourcesTo(newMsg, !skipTransportInfo); // devo preservare gli header in questo caso
					addTransportInfo(forceAddTrasporto, forceAddUrl, forceResponseStatus, message);
					return newMsg;
					
				}
				
			}
			
		}
	}
	
	public static void addTransportInfo(Properties forceAddTrasporto, Properties forceAddUrl, Integer forceResponseStatus, OpenSPCoop2Message msg) {
		if(forceAddTrasporto!=null && !forceAddTrasporto.isEmpty()) {
			Enumeration<?> keys = forceAddTrasporto.keys();
			while (keys.hasMoreElements()) {
				Object object = (Object) keys.nextElement();
				if(object instanceof String) {
					String key = (String) object;
					String value = forceAddTrasporto.getProperty(key);
					msg.forceTransportHeader(key, value);	
				}
			}
		}
		if(forceAddUrl!=null && !forceAddUrl.isEmpty()) {
			Enumeration<?> keys = forceAddUrl.keys();
			while (keys.hasMoreElements()) {
				Object object = (Object) keys.nextElement();
				if(object instanceof String) {
					String key = (String) object;
					String value = forceAddUrl.getProperty(key);
					msg.forceUrlProperty(key, value);	
				}
			}
		}
		if(forceResponseStatus!=null && forceResponseStatus.intValue()>0) {
			msg.setForcedResponseCode(forceResponseStatus.intValue()+"");
		}
	}
}

