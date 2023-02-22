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



package org.openspcoop2.pdd.core.trasformazioni;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPElement;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaServizioApplicativo;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaSoggetto;
import org.openspcoop2.core.config.TrasformazioneRegolaParametro;
import org.openspcoop2.core.config.TrasformazioneRegolaRichiesta;
import org.openspcoop2.core.config.TrasformazioneRegolaRisposta;
import org.openspcoop2.core.config.constants.TrasformazioneIdentificazioneRisorsaFallita;
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
import org.openspcoop2.message.xml.MessageXMLUtils;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.dynamic.DynamicException;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.core.dynamic.MessageContent;
import org.openspcoop2.pdd.core.dynamic.Template;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.dch.InputStreamDataSource;
import org.openspcoop2.utils.dch.MailcapActivationReader;
import org.openspcoop2.utils.io.ArchiveType;
import org.openspcoop2.utils.io.CompressorUtilities;
import org.openspcoop2.utils.io.Entry;
import org.openspcoop2.utils.mime.MimeTypes;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.TransportResponseContext;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;
import org.w3c.dom.Element;

/**
 * Gestione delle trasformazioni
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class GestoreTrasformazioniUtilities {

	
	public static boolean isMatchServizioApplicativo(IDSoggetto soggettoFruitore, String sa, List<TrasformazioneRegolaApplicabilitaServizioApplicativo> saCheckList) throws Exception {
		
		if(saCheckList==null || saCheckList.size()<=0) {
			return true;
		}
		
		if(soggettoFruitore==null || soggettoFruitore.getTipo()==null || soggettoFruitore.getNome()==null || sa==null) {
			//return true;
			return false; // se ho indicato una compatibilita attraverso una lista, e non ho l'informazione sul mittente non deve avere un match.
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
			//return true;
			return false; // se ho indicato una compatibilita attraverso una lista, e non ho l'informazione sul mittente non deve avere un match.
		}
		
		for (TrasformazioneRegolaApplicabilitaSoggetto soggettoCheck : soggettoCheckList) {
			if(soggettoFruitore.getTipo().equals(soggettoCheck.getTipo()) &&
					soggettoFruitore.getNome().equals(soggettoCheck.getNome()) ) {
				return true;
			}			
		}
		
		return false;
		
	}
	
	public static final String TRASFORMAZIONE_HEADER_HTTP_RICHIESTA = "RequestHeader";
	public static final String TRASFORMAZIONE_QUERY_PARAMETER = "QueryParameter";
	public static final String TRASFORMAZIONE_HEADER_HTTP_RISPOSTA = "ResponseHeader";
	
	public static final String TRASFORMAZIONE_SET_COOKIE = "${headerResponse:Set-Cookie}";

	
	public static void trasformazione(Logger log, List<TrasformazioneRegolaParametro> list, Map<String, List<String>> properties, Map<String, List<String>> forceAddProperties, String oggetto,
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
				nome = nome.trim();
				if(tipo==null) {
					throw new Exception("["+oggetto+"] Tipo di operazione da effettuare non indicata per '"+nome+"'");
				}
				
				boolean casoSpecialeForwardSetCookie = false; 
				if(valore!=null &&
						TRASFORMAZIONE_HEADER_HTTP_RISPOSTA.equals(oggetto) &&
						TrasformazioneRegolaParametroTipoAzione.UPDATE.equals(tipo) &&
						HttpConstants.SET_COOKIE.toLowerCase().equals(nome.toLowerCase()) &&
						TRASFORMAZIONE_SET_COOKIE.toLowerCase().equals(valore.trim().toLowerCase())) {
					casoSpecialeForwardSetCookie = true;
				}
				
				if(valore!=null) {
					try {
						valore = DynamicUtils.convertDynamicPropertyValue(oggetto+"-"+nome, valore, dynamicMap, pddContext);
					}catch(Exception e) {
						String msgErrore = "["+oggetto+"] Conversione valore per '"+nome+"' non riuscita (valore: "+valore+"): "+e.getMessage();
						if(parametro.getIdentificazioneFallita()!=null && TrasformazioneIdentificazioneRisorsaFallita.IGNORA.equals(parametro.getIdentificazioneFallita())) {
							log.debug(msgErrore,e);
							continue;
						}
						else {
							throw new Exception(msgErrore,e);
						}
					}
				}
				
				switch (tipo) {
				case ADD:
					if(valore==null) {
						throw new Exception("["+oggetto+"] Valore per '"+nome+"' non indicato");
					}
					List<String> vOld = null;
					if(properties!=null) {
						vOld = TransportUtils.removeRawObject(properties, nome);
						if(vOld!=null && !vOld.isEmpty()) {
							throw new Exception("["+oggetto+"] '"+nome+"' già esistente; utilizzare direttiva 'updateOrAdd' per aggiornarlo");
						}
					}
					if(properties!=null) {
						TransportUtils.put(properties, nome, valore, true);
					}
					TransportUtils.put(forceAddProperties, nome, valore, true);
					break;
				case UPDATE_OR_ADD:
					if(valore==null) {
						throw new Exception("["+oggetto+"] Valore per '"+nome+"' non indicato");
					}
					if(properties!=null) {
						TransportUtils.removeRawObject(properties, nome);
						TransportUtils.put(properties, nome, valore, true);
					}
					TransportUtils.put(forceAddProperties, nome, valore, true);
					break;
				case UPDATE:
					List<String> v = null;
					if(properties!=null) {
						v = TransportUtils.removeRawObject(properties, nome);
						if(v!=null && !v.isEmpty()) {
							if(casoSpecialeForwardSetCookie) {
								properties.put(nome, v);
								forceAddProperties.put(nome, v);
							}
							else {
								if(valore==null) {
									throw new Exception("["+oggetto+"] Valore del parametro '"+nome+"' non indicato");
								}
								TransportUtils.put(properties, nome, valore, false);
								TransportUtils.put(forceAddProperties, nome, valore, false);
							}
						}
					}
					break;
				case DELETE:
					if(properties!=null) {
						TransportUtils.removeRawObject(properties, nome);
					}
					break;
				default:
					break;
				}
			}
		}
	}
	
	private static String readCharset(Logger log, OpenSPCoop2Message msg, String forceContentType) throws Exception {
		String contentType = forceContentType;
		if(contentType==null || StringUtils.isEmpty(contentType)) {
			if(msg!=null) {
				contentType = msg.getContentType();
			}
		}
		String charset = null;
		if(contentType!=null) {
			try {
				boolean multipart = ContentTypeUtilities.isMultipartType(contentType);
				if(!multipart) {
					charset = ContentTypeUtilities.readCharsetFromContentType(contentType);
				}
				else {
					String internalCT = ContentTypeUtilities.getInternalMultipartContentType(contentType);
					charset = ContentTypeUtilities.readCharsetFromContentType(internalCT);
				}
			}catch(Throwable t) {
				log.error("Lettura charset non riuscita: "+t.getMessage(),t);
			}
		}
		return charset;
	}
	
	public static RisultatoTrasformazioneContenuto trasformazioneContenuto(Logger log, String tipoConversioneContenuto, 
			Template template, String oggetto, Map<String, Object> dynamicMap, OpenSPCoop2Message msg, MessageContent messageContent, PdDContext pddContext,
			String forceContentType, boolean readCharset) throws Exception {
		TipoTrasformazione tipoTrasformazione = null;
		if(tipoConversioneContenuto!=null) {
			tipoTrasformazione = TipoTrasformazione.toEnumConstant(tipoConversioneContenuto, true);
		}
		else {
			throw new Exception("Tipo di conversione contenuto non indicato");
		}
		
		log.debug("Trasformazione "+oggetto+" ["+tipoConversioneContenuto+"] ...");
		
		
		String charset = null;
		if(readCharset) {
			charset = readCharset(log, msg, forceContentType);
			if(charset==null) {
				charset = Charset.UTF_8.getValue();
			}
		}
		
		RisultatoTrasformazioneContenuto risultato = new RisultatoTrasformazioneContenuto();
		risultato.setTipoTrasformazione(tipoTrasformazione);
		
		// conversione formato

		switch (tipoTrasformazione) {

		case EMPTY:
			log.debug("trasformazione "+oggetto+" del contenuto empty");
			risultato.setEmpty(true);
			break;
			
		case TEMPLATE:
			if(template==null || template.getTemplate()==null) {
				throw new Exception("Template "+oggetto+" non definito");
			}
						
			log.debug("trasformazione "+oggetto+" ["+tipoTrasformazione+"], lettura template ...");
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			bout.write(template.getTemplate());
			bout.flush();
			bout.close();
			
			log.debug("trasformazione "+oggetto+" ["+tipoTrasformazione+"], risoluzione template ...");
			String newContent = DynamicUtils.convertDynamicPropertyValue("template", bout.toString(), dynamicMap, pddContext);
			log.debug("trasformazione "+oggetto+" ["+tipoTrasformazione+"], risoluzione template completata");
			if(newContent==null || "".equals(newContent)) {
				risultato.setEmpty(true);
			}
			else {
				risultato.setContenuto(newContent.getBytes(), newContent);
			}
			
			break;
			
		case FREEMARKER_TEMPLATE:
		case CONTEXT_FREEMARKER_TEMPLATE:
	    case FREEMARKER_TEMPLATE_ZIP:
	    	if(template==null || template.getTemplate()==null) {
				throw new Exception("Template "+oggetto+" non definito");
			}
						
			log.debug("trasformazione "+oggetto+" ["+tipoTrasformazione+"], risoluzione template ...");
			bout = new ByteArrayOutputStream();
			if(TipoTrasformazione.FREEMARKER_TEMPLATE.equals(tipoTrasformazione) || TipoTrasformazione.CONTEXT_FREEMARKER_TEMPLATE.equals(tipoTrasformazione)) {
				DynamicUtils.convertFreeMarkerTemplate(template, dynamicMap, bout, charset);
			}
			else {
				DynamicUtils.convertZipFreeMarkerTemplate(template.getZipTemplate(), dynamicMap, bout, charset);
			}
			bout.flush();
			bout.close();
			log.debug("trasformazione "+oggetto+" ["+tipoTrasformazione+"], risoluzione template completata");
			if(bout==null || bout.size()<=0) {
				risultato.setEmpty(true);
			}
			else {
				risultato.setContenuto(bout.toByteArray(), 
						charset!=null ? bout.toString(charset) : bout.toString());
			}
			
			break;
			
		case VELOCITY_TEMPLATE:
		case CONTEXT_VELOCITY_TEMPLATE:
	    case VELOCITY_TEMPLATE_ZIP:
	    	if(template==null || template.getTemplate()==null) {
				throw new Exception("Template "+oggetto+" non definito");
			}
						
			log.debug("trasformazione "+oggetto+" ["+tipoTrasformazione+"], risoluzione template ...");
			bout = new ByteArrayOutputStream();
			if(TipoTrasformazione.VELOCITY_TEMPLATE.equals(tipoTrasformazione) || TipoTrasformazione.CONTEXT_VELOCITY_TEMPLATE.equals(tipoTrasformazione)) {
				DynamicUtils.convertVelocityTemplate(template, dynamicMap, bout, charset);
			}
			else {
				DynamicUtils.convertZipVelocityTemplate(template.getZipTemplate(), dynamicMap, bout, charset);	
			}
			bout.flush();
			bout.close();
			log.debug("trasformazione "+oggetto+" ["+tipoTrasformazione+"], risoluzione template completata");
			if(bout==null || bout.size()<=0) {
				risultato.setEmpty(true);
			}
			else {
				risultato.setContenuto(bout.toByteArray(), 
						charset!=null ? bout.toString(charset) : bout.toString());
			}
			
			break;
			
		case XSLT:
			if(template==null || template.getTemplate()==null) {
				throw new Exception("Template "+oggetto+" non definito");
			}
			Element element = null;
			if(messageContent!=null && messageContent.isXml()) {
				element = messageContent.getElement();
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
			DynamicUtils.convertXSLTTemplate("template", template.getTemplate(), element, bout);
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
			
			
		case ZIP:
		case TGZ:
		case TAR:
			if(template==null || template.getTemplate()==null) {
				throw new Exception("Template "+oggetto+" non definito");
			}
		
			log.debug("trasformazione "+oggetto+" ["+tipoTrasformazione+"], risoluzione template ...");
			bout = new ByteArrayOutputStream();
			ArchiveType archiveType = ArchiveType.valueOf(tipoTrasformazione.name()); 
			DynamicUtils.convertCompressorTemplate("template", template.getTemplate(), dynamicMap, pddContext, archiveType, bout);
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
	
	
	public static OpenSPCoop2Message trasformaMessaggio(Logger log, OpenSPCoop2Message message, MessageContent messageContent, 
			RequestInfo requestInfo, Map<String, Object> dynamicMap, PdDContext pddContext, OpenSPCoop2Properties op2Properties,
			Map<String, List<String>> trasporto, Map<String, List<String>> forceAddTrasporto, 
			Map<String, List<String>> url, Map<String, List<String>> forceAddUrl,
			int status,
			String contentTypeInput, String returnCodeInput, // nota: contentTypeInput e returnCodeInput e' già convertito nei template prima della chiamata del metodo
			RisultatoTrasformazioneContenuto risultato,
			boolean trasformazioneRest, 
			String trasformazioneRest_method, String trasformazioneRest_path,
			boolean trasformazioneSoap, 
			VersioneSOAP trasformazioneSoap_versione, String trasformazioneSoap_soapAction, 
			boolean trasformazioneSoap_envelope, boolean trasformazioneSoap_envelopeAsAttachment,
			String trasformazioneSoap_tipoConversione, Template trasformazioneSoap_templateConversione) throws Throwable {
		
		try {
		
			OpenSPCoop2MessageFactory messageFactory = message.getFactory();
			
			// TransportRequest
			String forceResponseStatus = null;
			TransportRequestContext transportRequestContext = null;
			TransportResponseContext transportResponseContext = null;
			if(MessageRole.REQUEST.equals(message.getMessageRole())) {
				transportRequestContext = new TransportRequestContext(log);
				transportRequestContext.setHeaders(trasporto);
				transportRequestContext.setParameters(url);
			}
			else {
				transportResponseContext = new TransportResponseContext(log);
				transportResponseContext.setHeaders(trasporto);
				if(returnCodeInput!=null && StringUtils.isNotEmpty(returnCodeInput)) {
					// nota: returnCodeInput e' già convertito nei template prima della chiamata del metodo
					transportResponseContext.setCodiceTrasporto(returnCodeInput);
					forceResponseStatus = returnCodeInput;
				}
				else {
					transportResponseContext.setCodiceTrasporto(status+"");
					forceResponseStatus = status+"";
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
							transportRequestContext.removeHeader(HttpConstants.CONTENT_TYPE);
						}
						else {
							transportResponseContext.removeHeader(HttpConstants.CONTENT_TYPE);
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
							trasformazioneRest_path_real = DynamicUtils.convertDynamicPropertyValue("trasformazioneRest_path", trasformazioneRest_path, dynamicMap, pddContext);
						}
						transportRequestContext.setFunctionParameters(trasformazioneRest_path_real);
						messageType = requestInfo.getBindingConfig().getRequestMessageType(ServiceBinding.REST, transportRequestContext, contentType);
					}
					else {
						messageType = requestInfo.getBindingConfig().getResponseMessageType(ServiceBinding.REST, new TransportRequestContext(log), contentType, status);
					}
					if(risultato.getTipoTrasformazione().isBinaryMessage()) {
						messageType = MessageType.BINARY; 
					}
					
					OpenSPCoop2Message newMsg = null;
					OpenSPCoop2MessageParseResult pr = null;
					if(risultato.isEmpty()) {
						if(transportRequestContext!=null) {
							newMsg = messageFactory.createEmptyMessage(messageType, MessageRole.REQUEST);
							newMsg.setTransportRequestContext(transportRequestContext);
						}
						else {
							newMsg = messageFactory.createEmptyMessage(messageType, MessageRole.RESPONSE);
							newMsg.setTransportResponseContext(transportResponseContext);
						}
					}
					else {
						if(transportRequestContext!=null) {
							pr = messageFactory.createMessage(messageType, transportRequestContext, risultato.getContenuto());
						}
						else {
							pr = messageFactory.createMessage(messageType, transportResponseContext, risultato.getContenuto());
						}
						newMsg = pr.getMessage_throwParseThrowable();
					}
					
					message.copyResourcesTo(newMsg, skipTransportInfo);
					
					addTransportInfo(forceAddTrasporto, forceAddUrl, forceResponseStatus, newMsg);
					return newMsg;
					
				}
				else {
					
					if(risultato.getTipoTrasformazione().isBinaryMessage()) {
						
						// converto
						OpenSPCoop2MessageParseResult pr = null;
						if(transportRequestContext!=null) {
							pr = messageFactory.createMessage(MessageType.BINARY, transportRequestContext, risultato.getContenuto());
						}
						else {
							pr = messageFactory.createMessage(MessageType.BINARY, transportResponseContext, risultato.getContenuto());
						}
						OpenSPCoop2Message newMsg = pr.getMessage_throwParseThrowable();
						message.copyResourcesTo(newMsg, skipTransportInfo);
						addTransportInfo(forceAddTrasporto, forceAddUrl, forceResponseStatus, message);
						return newMsg;
						
					}
					else {
					
						OpenSPCoop2SoapMessage soapMessage = message.castAsSoap();
						
						if(risultato.isEmpty()) {
							soapMessage.getSOAPPart().getEnvelope().getBody().removeContents();
						}
						else {
							boolean rebuildWithAttachments = false;
							if(contentTypeInput!=null) {
								rebuildWithAttachments = ContentTypeUtilities.isMultipartRelated(contentTypeInput);
							}
							
							boolean changeSoapVersion = false;
							MessageType messageType_changeSoapVersion = null;
							if(contentTypeInput!=null) {
								if(transportRequestContext!=null) {
									messageType_changeSoapVersion = requestInfo.getBindingConfig().getRequestMessageType(ServiceBinding.SOAP, transportRequestContext, contentTypeInput);
								}
								else {
									messageType_changeSoapVersion = requestInfo.getBindingConfig().getResponseMessageType(ServiceBinding.REST, new TransportRequestContext(log), contentTypeInput, status);
								}
								if(messageType_changeSoapVersion!=null && !messageType_changeSoapVersion.equals(soapMessage.getMessageType())) {
									changeSoapVersion=true;
								}
							}
							
							if(rebuildWithAttachments) {
								OpenSPCoop2MessageParseResult pr = null;
								if(transportRequestContext!=null) {
									pr = messageFactory.createMessage(changeSoapVersion ? messageType_changeSoapVersion: message.getMessageType(), transportRequestContext, risultato.getContenuto());
								}
								else {
									pr = messageFactory.createMessage(changeSoapVersion ? messageType_changeSoapVersion: message.getMessageType(), transportResponseContext, risultato.getContenuto());
								}
								OpenSPCoop2Message newMsg = pr.getMessage_throwParseThrowable();
								message.copyResourcesTo(newMsg, skipTransportInfo);
								addTransportInfo(forceAddTrasporto, forceAddUrl, forceResponseStatus, message);
								return newMsg;
							}
							else {
								if(changeSoapVersion) {
									OpenSPCoop2MessageParseResult pr = null;
									if(transportRequestContext!=null) {
										pr = messageFactory.createMessage(messageType_changeSoapVersion, transportRequestContext, risultato.getContenuto());
									}
									else {
										pr = messageFactory.createMessage(messageType_changeSoapVersion, transportResponseContext, risultato.getContenuto());
									}
									OpenSPCoop2Message newMsg = pr.getMessage_throwParseThrowable();
									message.copyResourcesTo(newMsg, skipTransportInfo);
									addTransportInfo(forceAddTrasporto, forceAddUrl, forceResponseStatus, message);
									return newMsg;
								}
								else {
									SOAPElement newElement = soapMessage.createSOAPElement(risultato.getContenuto());
									Element element = null;
									if(messageContent!=null && messageContent.isXml()) {
										element = messageContent.getElement();
									}
									if(element!=null && element.getLocalName().equals(newElement.getLocalName()) && element.getNamespaceURI().equals(newElement.getNamespaceURI())) {
										// il nuovo elemento è una busta soap
										soapMessage.getSOAPPart().getEnvelope().detachNode();
										soapMessage.getSOAPPart().setContent(new DOMSource(newElement));
									}
									else {
										soapMessage.getSOAPPart().getEnvelope().getBody().removeContents();
										soapMessage.getSOAPPart().getEnvelope().getBody().addChildElement(newElement);
									}
								}
							}
						}
						
						addTransportInfo(forceAddTrasporto, forceAddUrl, forceResponseStatus, message);
						
						if(contentTypeInput!=null) {
							message.setContentType(contentTypeInput);
						}
						
						return message;
					}
					
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
						transportRequestContext.removeHeader(HttpConstants.CONTENT_TYPE);
						TransportUtils.setHeader(transportRequestContext.getHeaders(),HttpConstants.CONTENT_TYPE, contentTypeForEnvelope);
					}
					else {
						transportResponseContext.removeHeader(HttpConstants.CONTENT_TYPE);
						TransportUtils.setHeader(transportResponseContext.getHeaders(),HttpConstants.CONTENT_TYPE, contentTypeForEnvelope);
					}
					
					String soapAction = null;
					if(trasformazioneSoap_soapAction!=null) {
						soapAction = DynamicUtils.convertDynamicPropertyValue("soapAction", trasformazioneSoap_soapAction, dynamicMap, pddContext);
					}
					
					OpenSPCoop2Message messageSoap = null;
					if(risultato.isEmpty()) {
						if(transportRequestContext!=null) {
							messageSoap = messageFactory.createEmptyMessage(messageType, MessageRole.REQUEST);
							messageSoap.setTransportRequestContext(transportRequestContext);
						}
						else {
							messageSoap = messageFactory.createEmptyMessage(messageType, MessageRole.RESPONSE);
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
											"envelope-body", dynamicMap, message, messageContent, pddContext, 
											null, false);
							try {
								if(risultatoEnvelopeBody.isEmpty()) {
									if(transportRequestContext!=null) {
										messageSoap = messageFactory.createEmptyMessage(messageType, MessageRole.REQUEST);
										messageSoap.setTransportRequestContext(transportRequestContext);
									}
									else {
										messageSoap = messageFactory.createEmptyMessage(messageType, MessageRole.RESPONSE);
										messageSoap.setTransportResponseContext(transportResponseContext);
									}
								}
								else {
									if(trasformazioneSoap_envelope) {
										OpenSPCoop2MessageParseResult pr = null;
										if(transportRequestContext!=null) {
											pr = messageFactory.envelopingMessage(messageType, contentTypeForEnvelope, 
													soapAction, transportRequestContext, risultatoEnvelopeBody.getContenuto(), null, true,
													op2Properties.useSoapMessageReader(), op2Properties.getSoapMessageReaderBufferThresholdKb());
										}
										else {
											pr = messageFactory.envelopingMessage(messageType, contentTypeForEnvelope, 
													soapAction, transportResponseContext, risultatoEnvelopeBody.getContenuto(), null, true,
													op2Properties.useSoapMessageReader(), op2Properties.getSoapMessageReaderBufferThresholdKb());
										}
										messageSoap = pr.getMessage_throwParseThrowable();
									}
									else {
										OpenSPCoop2MessageParseResult pr = null;
										if(transportRequestContext!=null) {
											pr = messageFactory.createMessage(messageType, transportRequestContext, 
													risultatoEnvelopeBody.getContenuto());
										}
										else {
											pr = messageFactory.createMessage(messageType, transportResponseContext, 
													risultatoEnvelopeBody.getContenuto());
										}
										messageSoap = pr.getMessage_throwParseThrowable();
									}
								}
							}catch(Throwable t) {
								if(risultatoEnvelopeBody.getContenutoAsString()!=null) {
									log.error("Trasformazione non riuscita per il contenuto del body (SOAP With Attachments): ["+risultatoEnvelopeBody.getContenutoAsString()+"]",t);
								}
								throw t;
							}
															
							// esiste un contenuto nel body
							AttachmentPart ap = null;
	
							String contentType = contentTypeInput;
							if(contentType==null) {
								throw new Exception("Content-Type non indicato per l'attachment");
							}
							String ctBase = ContentTypeUtilities.readBaseTypeFromContentType(contentType);
							if(HttpConstants.CONTENT_TYPE_TEXT_XML.equals(ctBase)) {
								
								if(MailcapActivationReader.existsDataContentHandler(HttpConstants.CONTENT_TYPE_TEXT_XML)){
									ap = messageSoap.castAsSoap().createAttachmentPart();
									messageSoap.castAsSoap().updateAttachmentPart(ap, risultato.getContenuto(), contentType);
								}
								else {
									InputStreamDataSource isSource = new InputStreamDataSource("attach", contentType, risultato.getContenuto());
									ap = messageSoap.castAsSoap().createAttachmentPart(new DataHandler(isSource));
								}
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
									pr = messageFactory.envelopingMessage(messageType, contentTypeForEnvelope, 
											soapAction, transportRequestContext, risultato.getContenuto(), null, true,
											op2Properties.useSoapMessageReader(), op2Properties.getSoapMessageReaderBufferThresholdKb());
								}
								else {
									pr = messageFactory.envelopingMessage(messageType, contentTypeForEnvelope, 
											soapAction, transportResponseContext, risultato.getContenuto(), null, true,
											op2Properties.useSoapMessageReader(), op2Properties.getSoapMessageReaderBufferThresholdKb());
								}
								messageSoap = pr.getMessage_throwParseThrowable();
							}
							else {
								OpenSPCoop2MessageParseResult pr = null;
								if(transportRequestContext!=null) {
									pr = messageFactory.createMessage(messageType, transportRequestContext, risultato.getContenuto());
								}
								else {
									pr = messageFactory.createMessage(messageType, transportResponseContext, risultato.getContenuto());
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
						
						if(ServiceBinding.REST.equals(message.getServiceBinding())) {
							if(StringUtils.isNotEmpty(trasformazioneRest_method) || StringUtils.isNotEmpty(trasformazioneRest_path)) {
								GestoreTrasformazioniUtilities.injectNewRestParameter(message, 
										trasformazioneRest_method, 
										trasformazioneRest_path, 
										dynamicMap, pddContext);
							}
						}
						
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
						if(risultato.getTipoTrasformazione().isBinaryMessage()) {
							messageType = MessageType.BINARY; 
						}
						else {
							if(transportRequestContext!=null) {
								messageType = requestInfo.getBindingConfig().getRequestMessageType(ServiceBinding.REST, transportRequestContext, contentType);
							}
							else {
								messageType = requestInfo.getBindingConfig().getResponseMessageType(ServiceBinding.REST, new TransportRequestContext(log), contentType, status);
							}
						}
						if(messageType.equals(message.getMessageType())) {
							
							if(MessageType.XML.equals(messageType)) {
								
								message.castAsRestXml().updateContent(MessageXMLUtils.getInstance(messageFactory).newElement(risultato.getContenuto()));
																
								addTransportInfo(forceAddTrasporto, forceAddUrl, forceResponseStatus, message);
								
								if(contentTypeInput!=null) {
									message.setContentType(contentTypeInput);
								}
								
								if(ServiceBinding.REST.equals(message.getServiceBinding())) {
									if(StringUtils.isNotEmpty(trasformazioneRest_method) || StringUtils.isNotEmpty(trasformazioneRest_path)) {
										GestoreTrasformazioniUtilities.injectNewRestParameter(message, 
												trasformazioneRest_method, 
												trasformazioneRest_path, 
												dynamicMap, pddContext);
									}
								}
								
								return message;
								
							}
							else if(MessageType.JSON.equals(messageType)) {
								
								message.castAsRestJson().updateContent(risultato.getContenutoAsString());
								
								addTransportInfo(forceAddTrasporto, forceAddUrl, forceResponseStatus, message);
								
								if(contentTypeInput!=null) {
									message.setContentType(contentTypeInput);
								}
								
								if(ServiceBinding.REST.equals(message.getServiceBinding())) {
									if(StringUtils.isNotEmpty(trasformazioneRest_method) || StringUtils.isNotEmpty(trasformazioneRest_path)) {
										GestoreTrasformazioniUtilities.injectNewRestParameter(message, 
												trasformazioneRest_method, 
												trasformazioneRest_path, 
												dynamicMap, pddContext);
									}
								}
								
								return message;
								
							}
							
						}
						
						// converto
						OpenSPCoop2MessageParseResult pr = null;
						if(transportRequestContext!=null) {
							pr = messageFactory.createMessage(messageType, transportRequestContext, risultato.getContenuto());
						}
						else {
							pr = messageFactory.createMessage(messageType, transportResponseContext, risultato.getContenuto());
						}
						
						OpenSPCoop2Message newMsg = pr.getMessage_throwParseThrowable();
						message.copyResourcesTo(newMsg, !skipTransportInfo); // devo preservare gli header in questo caso
						
						addTransportInfo(forceAddTrasporto, forceAddUrl, forceResponseStatus, message);
						
						if(ServiceBinding.REST.equals(newMsg.getServiceBinding())) {
							if(StringUtils.isNotEmpty(trasformazioneRest_method) || StringUtils.isNotEmpty(trasformazioneRest_path)) {
								GestoreTrasformazioniUtilities.injectNewRestParameter(newMsg, 
										trasformazioneRest_method, 
										trasformazioneRest_path, 
										dynamicMap, pddContext);
							}
						}
						
						return newMsg;
						
					}
					
				}
				
			}
		}catch(Throwable t) {
			if(risultato.getContenutoAsString()!=null) {
				log.error("Trasformazione non riuscita per il contenuto: ["+risultato.getContenutoAsString()+"]",t);
			}
			throw t;
		}
	}
	
	public static void injectNewRestParameter(OpenSPCoop2Message message, String trasformazioneRest_method, String trasformazioneRest_path,
			Map<String, Object> dynamicMap, PdDContext pddContext) throws DynamicException {
		if(ServiceBinding.REST.equals(message.getServiceBinding())) {
			if(StringUtils.isNotEmpty(trasformazioneRest_method)) {
				String newMethod = DynamicUtils.convertDynamicPropertyValue("trasformazioneRest_method", trasformazioneRest_method, dynamicMap, pddContext);
				message.getTransportRequestContext().setRequestType(newMethod);
			}
			if(StringUtils.isNotEmpty(trasformazioneRest_path)) {
				String newPath = DynamicUtils.convertDynamicPropertyValue("trasformazioneRest_path", trasformazioneRest_path, dynamicMap, pddContext);
				message.getTransportRequestContext().setFunctionParameters(newPath);
			}
		}
	}
	
	public static void addTransportInfo(Map<String, List<String>> forceAddTrasporto, Map<String, List<String>> forceAddUrl, String forceResponseStatus, OpenSPCoop2Message msg) {
		if(forceAddTrasporto!=null && !forceAddTrasporto.isEmpty()) {
			Iterator<String> keys = forceAddTrasporto.keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				List<String> values = forceAddTrasporto.get(key);
				msg.forceTransportHeader(key, values);	
			}
		}
		if(forceAddUrl!=null && !forceAddUrl.isEmpty()) {
			Iterator<String> keys = forceAddUrl.keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				List<String> values = forceAddUrl.get(key);
				msg.forceUrlProperty(key, values);	
			}
		}
		if(forceResponseStatus!=null && StringUtils.isNotEmpty(forceResponseStatus)) {
			msg.setForcedResponseCode(forceResponseStatus);
		}
	}
	
	public static void checkReturnCode(String forceResponseStatus) throws Exception {
		int r = -1;
		try {
			r = Integer.valueOf(forceResponseStatus);
		}catch(Exception e) {
			throw new Exception("Codice HTTP di risposta deve essere un intero compreso tra 200 e 599, trovato: '"+forceResponseStatus+"'");
		}
		if(r<200 || r>599) {
			throw new Exception("Codice HTTP di risposta deve essere un intero compreso tra 200 e 599, trovato: "+forceResponseStatus+"");
		}
	}
	
	public static final String TIPO_TRASFORMAZIONE_SEPARATOR = " ";
	public static final String TIPO_TRASFORMAZIONE_CONVERSIONE_SOAP = "soap";
	public static final String TIPO_TRASFORMAZIONE_CONVERSIONE_REST = "rest";
	public static final String TIPO_TRASFORMAZIONE_CONVERSIONE_METHOD = "method";
	public static final String TIPO_TRASFORMAZIONE_CONVERSIONE_PATH = "path";
	public static final String TIPO_TRASFORMAZIONE_CONVERSIONE_HEADERS = "headers";
	public static final String TIPO_TRASFORMAZIONE_CONVERSIONE_QUERY_PARAMETERS = "queryParameters";
	public static final String TIPO_TRASFORMAZIONE_CONVERSIONE_RETURN_CODE = "returnCode";
	public static final String TIPO_TRASFORMAZIONE_NESSUNA = "nessuna";
	
	public static String getLabelTipoTrasformazioneRichiesta(TrasformazioneRegolaRichiesta richiesta, OpenSPCoop2Message message) {
		StringBuilder bf = new StringBuilder();
		if(richiesta.getConversione()) {
			if(richiesta.getTrasformazioneSoap()!=null) {
				bf.append(TIPO_TRASFORMAZIONE_CONVERSIONE_SOAP).append(TIPO_TRASFORMAZIONE_SEPARATOR);	
			}
			else if(richiesta.getTrasformazioneRest()!=null) {
				if(ServiceBinding.REST.equals(message.getServiceBinding())) {
					if(richiesta.getTrasformazioneRest()!=null) {
						if(StringUtils.isNotEmpty(richiesta.getTrasformazioneRest().getMetodo())) {
							bf.append(TIPO_TRASFORMAZIONE_CONVERSIONE_METHOD).append(TIPO_TRASFORMAZIONE_SEPARATOR);	
						}
						if(StringUtils.isNotEmpty(richiesta.getTrasformazioneRest().getPath())) {
							bf.append(TIPO_TRASFORMAZIONE_CONVERSIONE_PATH).append(TIPO_TRASFORMAZIONE_SEPARATOR);	
						}
					}
				}
				else {
					bf.append(TIPO_TRASFORMAZIONE_CONVERSIONE_REST).append(TIPO_TRASFORMAZIONE_SEPARATOR);	
				}
			}
			bf.append(richiesta.getConversioneTipo());	
		}
		else {
			if(ServiceBinding.REST.equals(message.getServiceBinding())) {
				if(richiesta.getTrasformazioneRest()!=null) {
					if(StringUtils.isNotEmpty(richiesta.getTrasformazioneRest().getMetodo())) {
						if(bf.length()>0) {
							bf.append(TIPO_TRASFORMAZIONE_SEPARATOR);	
						}
						bf.append(TIPO_TRASFORMAZIONE_CONVERSIONE_METHOD);	
					}
					if(StringUtils.isNotEmpty(richiesta.getTrasformazioneRest().getPath())) {
						if(bf.length()>0) {
							bf.append(TIPO_TRASFORMAZIONE_SEPARATOR);	
						}
						bf.append(TIPO_TRASFORMAZIONE_CONVERSIONE_PATH);	
					}
				}
			}
		}
		if(richiesta.getHeaderList()!=null && !richiesta.getHeaderList().isEmpty()) {
			if(bf.length()>0) {
				bf.append(TIPO_TRASFORMAZIONE_SEPARATOR);	
			}
			bf.append(TIPO_TRASFORMAZIONE_CONVERSIONE_HEADERS);	
		}
		if(richiesta.getParametroUrlList()!=null && !richiesta.getParametroUrlList().isEmpty()) {
			if(bf.length()>0) {
				bf.append(TIPO_TRASFORMAZIONE_SEPARATOR);	
			}
			bf.append(TIPO_TRASFORMAZIONE_CONVERSIONE_QUERY_PARAMETERS);	
		}
		if(bf.length()<=0) {
			bf.append(TIPO_TRASFORMAZIONE_NESSUNA);	
		}
		return bf.toString();
		
	}
	
	public static String getLabelTipoTrasformazioneRisposta(TrasformazioneRegolaRichiesta richiesta,TrasformazioneRegolaRisposta trasformazioneRisposta) {
		StringBuilder bf = new StringBuilder();
		if(trasformazioneRisposta.getConversione()) {
			// !inverto!
			if(richiesta.getTrasformazioneRest()!=null) {
				bf.append(TIPO_TRASFORMAZIONE_CONVERSIONE_SOAP).append(TIPO_TRASFORMAZIONE_SEPARATOR);	
			}
			else if(richiesta.getTrasformazioneSoap()!=null) {
				bf.append(TIPO_TRASFORMAZIONE_CONVERSIONE_REST).append(TIPO_TRASFORMAZIONE_SEPARATOR);	
			}
			bf.append(trasformazioneRisposta.getConversioneTipo());	
		}
		if(trasformazioneRisposta.getHeaderList()!=null && !trasformazioneRisposta.getHeaderList().isEmpty()) {
			if(bf.length()>0) {
				bf.append(TIPO_TRASFORMAZIONE_SEPARATOR);	
			}
			bf.append(TIPO_TRASFORMAZIONE_CONVERSIONE_HEADERS);	
		}
		if(trasformazioneRisposta.getReturnCode()!=null && StringUtils.isNotEmpty(trasformazioneRisposta.getReturnCode())) {
			if(bf.length()>0) {
				bf.append(TIPO_TRASFORMAZIONE_SEPARATOR);	
			}
			bf.append(TIPO_TRASFORMAZIONE_CONVERSIONE_RETURN_CODE);	
		}
		if(bf.length()<=0) {
			bf.append(TIPO_TRASFORMAZIONE_NESSUNA);	
		}
		return bf.toString();
	}
	
	public static OpenSPCoop2Message buildRequestResponseArchive(Logger log, OpenSPCoop2Message request, OpenSPCoop2Message response, ArchiveType archiveType) throws GestoreTrasformazioniException{
		
		List<Entry> listEntries = new ArrayList<>();
		
		OpenSPCoop2MessageFactory msgFactory = null;
		
		if(request!=null) {
			msgFactory = request.getFactory();
			_buildEntryZipArchive(listEntries, request, true);
		}
		
		if(response!=null) {
			if(msgFactory==null && request!=null) {
				msgFactory = request.getFactory();
			}
			_buildEntryZipArchive(listEntries, response, false);
		}
		
		if(msgFactory==null) {
			throw new GestoreTrasformazioniException("MessageFactory undefined");
		}
		
		byte [] archive = null;
		try {
			archive = CompressorUtilities.archive(listEntries, archiveType);
		}catch(Throwable t) {
			throw new GestoreTrasformazioniException("Generazione archive '"+archiveType+"' non riuscita: "+t.getMessage(),t);
		}
		
		String mimeType = null;
		try {
			switch (archiveType) {
			case ZIP:
				mimeType = MimeTypes.getInstance().getMimeType("zip");
				break;
			case TAR:
				mimeType = MimeTypes.getInstance().getMimeType("tar");
				break;
			case TGZ:
				mimeType = MimeTypes.getInstance().getMimeType("tgz");
				break;
			}
		}catch(Throwable t) {
			throw new GestoreTrasformazioniException("Generazione mime-type for archive '"+archiveType+"' non riuscita: "+t.getMessage(),t);
		}
		
		try {
			TransportRequestContext requestContext = new TransportRequestContext(log);
			requestContext.setHeaders(new HashMap<String, List<String>>());
			TransportUtils.addHeader(requestContext.getHeaders(), HttpConstants.CONTENT_TYPE, mimeType);
			OpenSPCoop2MessageParseResult msg = msgFactory.createMessage(MessageType.BINARY, requestContext, archive);
			return msg.getMessage_throwParseThrowable();
		}catch(Throwable t) {
			throw new GestoreTrasformazioniException("Generazione OpenSPCoop2 Message from archive '"+archiveType+"' non riuscita: "+t.getMessage(),t);
		}
	}
	private static void _buildEntryZipArchive(List<Entry> listEntries, OpenSPCoop2Message message, boolean request) throws GestoreTrasformazioniException{
		
		String name = request ? "request" : "response";
		
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			message.writeTo(bout, true);
			bout.flush();
			bout.close();
			Entry payloadRequest = new Entry(name+".bin", bout.toByteArray());
			listEntries.add(payloadRequest);
			
			Map<String, List<String>> hdr = null;
			
			if(request && message.getTransportRequestContext()!=null) {
				if(message.getTransportRequestContext().getHeaders()!=null &&
					!message.getTransportRequestContext().getHeaders().isEmpty()) {
					hdr = message.getTransportRequestContext().getHeaders();
				}
			}
			else if(!request && message.getTransportResponseContext()!=null) {
				if(message.getTransportResponseContext().getHeaders()!=null &&
						!message.getTransportResponseContext().getHeaders().isEmpty()) {
					hdr = message.getTransportResponseContext().getHeaders();
				}
			}
		
			if(hdr!=null) {
				StringBuilder sb = new StringBuilder();
				for (String hdrName : hdr.keySet()) {
					List<String> values = hdr.get(hdrName);
					if(values!=null) {
						for (String v : values) {
							if(v==null) {
								v="";
							}
							if(sb.length()>0) {
								sb.append("\n");
							}
							sb.append(hdrName).append(": ").append(v);
						}
					}
				}
				if(sb.length()>0) {
					bout = new ByteArrayOutputStream();
					bout.write(sb.toString().getBytes());
					bout.flush();
					bout.close();
					Entry payloadRequestHeaders = new Entry(name+"Headers.bin", bout.toByteArray());
					listEntries.add(payloadRequestHeaders);
				}	
			}
		}catch(Throwable t) {
			throw new GestoreTrasformazioniException("Generazione entry '"+name+"' non riuscita: "+t.getMessage(),t);
		}
		
	}
	
}

