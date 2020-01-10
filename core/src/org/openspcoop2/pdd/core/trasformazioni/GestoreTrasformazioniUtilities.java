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
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
import org.openspcoop2.pdd.core.dynamic.DynamicException;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.utils.dch.InputStreamDataSource;
import org.openspcoop2.utils.dch.MailcapActivationReader;
import org.openspcoop2.utils.io.ArchiveType;
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
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class GestoreTrasformazioniUtilities {

	
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
						vOld = TransportUtils.remove(properties, nome);
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
						TransportUtils.remove(properties, nome);
						properties.put(nome, valore);
					}
					forceAddProperties.put(nome, valore);
					break;
				case UPDATE:
					Object v = null;
					if(properties!=null) {
						v = TransportUtils.remove(properties, nome);
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
						TransportUtils.remove(properties, nome);
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
		risultato.setTipoTrasformazione(tipoTrasformazione);
		
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
		case FREEMARKER_TEMPLATE_ZIP:
			if(contenuto==null) {
				throw new Exception("Template "+oggetto+" non definito");
			}
						
			log.debug("trasformazione "+oggetto+" ["+tipoTrasformazione+"], risoluzione template ...");
			bout = new ByteArrayOutputStream();
			if(TipoTrasformazione.FREEMARKER_TEMPLATE.equals(tipoTrasformazione)) {
				DynamicUtils.convertFreeMarkerTemplate("template.ftl", contenuto, dynamicMap, bout);
			}
			else {
				DynamicUtils.convertZipFreeMarkerTemplate("template.ftl.zip", contenuto, dynamicMap, bout);
			}
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
		case VELOCITY_TEMPLATE_ZIP:
			if(contenuto==null) {
				throw new Exception("Template "+oggetto+" non definito");
			}
						
			log.debug("trasformazione "+oggetto+" ["+tipoTrasformazione+"], risoluzione template ...");
			bout = new ByteArrayOutputStream();
			if(TipoTrasformazione.VELOCITY_TEMPLATE.equals(tipoTrasformazione)) {
				DynamicUtils.convertVelocityTemplate("template.vm", contenuto, dynamicMap, bout);
			}
			else {
				DynamicUtils.convertZipVelocityTemplate("template.vm.zip", contenuto, dynamicMap, bout);	
			}
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
			
			
		case ZIP:
		case TGZ:
		case TAR:
			if(contenuto==null) {
				throw new Exception("Template "+oggetto+" non definito");
			}
		
			log.debug("trasformazione "+oggetto+" ["+tipoTrasformazione+"], risoluzione template ...");
			bout = new ByteArrayOutputStream();
			ArchiveType archiveType = ArchiveType.valueOf(tipoTrasformazione.name()); 
			DynamicUtils.convertCompressorTemplate("template", contenuto, dynamicMap, pddContext, archiveType, bout);
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
		
		try {
		
			OpenSPCoop2MessageFactory messageFactory = message.getFactory();
			
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
											"envelope-body", dynamicMap, message, element, pddContext);
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
													soapAction, transportRequestContext, risultatoEnvelopeBody.getContenuto(), null, true);
										}
										else {
											pr = messageFactory.envelopingMessage(messageType, contentTypeForEnvelope, 
													soapAction, transportResponseContext, risultatoEnvelopeBody.getContenuto(), null, true);
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
											soapAction, transportRequestContext, risultato.getContenuto(), null, true);
								}
								else {
									pr = messageFactory.envelopingMessage(messageType, contentTypeForEnvelope, 
											soapAction, transportResponseContext, risultato.getContenuto(), null, true);
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
								messageType = requestInfo.getBindingConfig().getResponseMessageType(ServiceBinding.REST, new TransportRequestContext(), contentType, status);
							}
						}
						if(messageType.equals(message.getMessageType())) {
							
							if(MessageType.XML.equals(messageType)) {
								
								message.castAsRestXml().updateContent(XMLUtils.getInstance(messageFactory).newElement(risultato.getContenuto()));
								
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
							else if(MessageType.JSON.equals(messageType)) {
								
								message.castAsRestJson().updateContent(risultato.getContenutoAsString());
								
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
				String newMethod = DynamicUtils.convertDynamicPropertyValue("trasformazioneRest_method", trasformazioneRest_method, dynamicMap, pddContext, true);
				message.getTransportRequestContext().setRequestType(newMethod);
			}
			if(StringUtils.isNotEmpty(trasformazioneRest_path)) {
				String newPath = DynamicUtils.convertDynamicPropertyValue("trasformazioneRest_path", trasformazioneRest_path, dynamicMap, pddContext, true);
				message.getTransportRequestContext().setFunctionParameters(newPath);
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
		StringBuffer bf = new StringBuffer();
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
		StringBuffer bf = new StringBuffer();
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
		if(trasformazioneRisposta.getReturnCode()!=null && trasformazioneRisposta.getReturnCode()>0) {
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
}

