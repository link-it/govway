/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.as4.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.OpenSPCoop2RestMimeMultipartMessage;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.protocol.as4.AS4RawContent;
import org.openspcoop2.protocol.as4.constants.AS4Costanti;
import org.openspcoop2.protocol.as4.pmode.TranslatorPayloadProfilesDefault;
import org.openspcoop2.protocol.as4.pmode.TranslatorPropertiesDefault;
import org.openspcoop2.protocol.as4.utils.AS4PropertiesUtils;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.ProtocolMessage;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.utils.dch.InputStreamDataSource;
import org.openspcoop2.utils.dch.MailcapActivationReader;
import org.openspcoop2.utils.mime.MimeMultipart;
import org.openspcoop2.utils.regexp.RegExpNotFoundException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;
import org.w3c.dom.Element;

import backend.ecodex.org._1_1.LargePayloadType;
import backend.ecodex.org._1_1.SubmitRequest;
import eu.domibus.configuration.Attachment;
import eu.domibus.configuration.Payload;
import eu.domibus.configuration.PayloadProfile;
import eu.domibus.configuration.PayloadProfiles;
import eu.domibus.configuration.Properties;
import eu.domibus.configuration.PropertyRef;
import eu.domibus.configuration.PropertySet;
import eu.domibus.configuration.PropertyValueHeader;
import eu.domibus.configuration.PropertyValueUrl;

/**
 * AS4Imbustamento
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AS4Imbustamento {

	public ProtocolMessage buildASMessage(OpenSPCoop2Message msg, Busta busta,
			RuoloMessaggio ruoloMessaggio,
			ProprietaManifestAttachments proprietaManifestAttachments,
			IRegistryReader registryReader, IProtocolFactory<?> protocolFactory) throws ProtocolException{
		
		try{
			OpenSPCoop2MessageFactory messageFactory = msg!=null ? msg.getFactory() : OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			
			ProtocolMessage protocolMessage = new ProtocolMessage();
			
			OpenSPCoop2SoapMessage as4Message = null;
			MessageRole messageRole = MessageRole.REQUEST;
			if(RuoloMessaggio.RISPOSTA.equals(ruoloMessaggio)){
				messageRole = MessageRole.RESPONSE; 
			}
			
			
			// **** Read object from Registry *****
			
			IDSoggetto idSoggettoMittente = new IDSoggetto(busta.getTipoMittente(),busta.getMittente());
			Soggetto soggettoMittente = registryReader.getSoggetto(idSoggettoMittente);
			
			IDSoggetto idSoggettoDestinatario = new IDSoggetto(busta.getTipoDestinatario(),busta.getDestinatario());
			Soggetto soggettoDestinatario = registryReader.getSoggetto(idSoggettoDestinatario);
			
			IDSoggetto idSoggettoErogatore = null;
			if(RuoloMessaggio.RICHIESTA.equals(ruoloMessaggio)){
				idSoggettoErogatore = idSoggettoDestinatario;
			}
			else{
				idSoggettoErogatore = idSoggettoMittente;
			}
			IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(busta.getTipoServizio(), busta.getServizio(), 
					idSoggettoErogatore, busta.getVersioneServizio());
			AccordoServizioParteSpecifica asps = registryReader.getAccordoServizioParteSpecifica(idServizio);
			
			IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
			AccordoServizioParteComune aspc = registryReader.getAccordoServizioParteComune(idAccordo);
			
			TranslatorPayloadProfilesDefault tPayloadProfiles = TranslatorPayloadProfilesDefault.getTranslator();
			TranslatorPropertiesDefault tProperties = TranslatorPropertiesDefault.getTranslator();
			
			String azione = busta.getAzione();
			String nomePortType = asps.getPortType();
			String actionProperty = AS4BuilderUtils.readPropertyInfoAction(aspc, nomePortType, azione);
			String payloadProfile = AS4BuilderUtils.readPropertyPayloadProfile(aspc, nomePortType, azione);
			String propertySet = AS4BuilderUtils.readPropertyPropertySet(aspc, nomePortType, azione);
			if(actionProperty==null) {
				throw new ProtocolException("Action '"+azione+"' not found");
			}
			if(payloadProfile==null) {
				payloadProfile = tPayloadProfiles.getListPayloadProfileDefault().get(0).getName();
			}
			if(propertySet==null) {
				propertySet = tProperties.getListPropertySetDefault().get(0).getName();
			}
			
			PayloadProfiles pps = AS4BuilderUtils.readPayloadProfiles(tPayloadProfiles, aspc, idAccordo, true);
			
			List<Payload> payloadConfig = new ArrayList<>();
			boolean foundP = false;
			for (PayloadProfile p : pps.getPayloadProfileList()) {
				if(p.getName().equals(payloadProfile)) {
					for (Attachment a : p.getAttachmentList()) {
						boolean found = false;
						for (Payload payload : pps.getPayloadList()) {
							if(payload.getName().equals(a.getName())) {
								payloadConfig.add(payload);
								found = true;
								break;
							}
						}
						if(!found) {
							throw new ProtocolException("Action '"+azione+"' (payload profile '"+payloadProfile+"') with payload '"+a.getName()+"' unknown");
						}
					}
					foundP = true;
					break;
				}
			}
			if(!foundP) {
				throw new ProtocolException("Action '"+azione+"' with payload profile '"+payloadProfile+"' unknown");
			}
			
			Properties properties = AS4BuilderUtils.readProperties(tProperties, aspc, idAccordo, true);
			
			List<eu.domibus.configuration.Property> propertyConfig = new ArrayList<eu.domibus.configuration.Property>();
			foundP = false;
			for (PropertySet p : properties.getPropertySetList()) {
				if(p.getName().equals(propertySet)) {
					for (PropertyRef a : p.getPropertyRefList()) {
						boolean found = false;
						for (eu.domibus.configuration.Property property : properties.getPropertyList()) {
							if(property.getName().equals(a.getProperty())) {
								propertyConfig.add(property);
								found = true;
								break;
							}
						}
						if(!found) {
							throw new ProtocolException("Action '"+azione+"' (propertySet '"+propertySet+"') with property '"+a.getProperty()+"' unknown");
						}
					}
					foundP = true;
					break;
				}
			}
			if(!foundP) {
				throw new ProtocolException("Action '"+azione+"' with propertySet '"+propertySet+"' unknown");
			}
			
			
			
			// **** Prepare *****
			
			Map<String,String> mapIdPartInfoToIdAttach = new HashMap<>();
			
			Messaging ebmsV3_0Messagging = this.buildEbmsV3_0Messagging(ruoloMessaggio,busta,
					soggettoMittente, soggettoDestinatario, aspc, actionProperty,
					asps.getPortType(), msg.getServiceBinding());
			
			// Properties
			this.addMessageProperties(ebmsV3_0Messagging, busta, msg, propertyConfig);
			
			// PayloadInfo
			PayloadInfo payloadInfo = new PayloadInfo();
			ebmsV3_0Messagging.getUserMessage(0).setPayloadInfo(payloadInfo);
			
			// Submit Request Body
			SubmitRequest submitRequest = new SubmitRequest();
						
			if(ServiceBinding.SOAP.equals(msg.getServiceBinding())){
				
				OpenSPCoop2SoapMessage soapMessage = msg.castAsSoap();
				
				if(MessageType.SOAP_12.equals(msg.getMessageType())){
					as4Message = soapMessage; // viene trasformato il messaggio ricevuto per avere benefici di performance
				}
				else{
					as4Message = messageFactory.createEmptyMessage(MessageType.SOAP_12, messageRole).castAsSoap(); // viene creato ad hoc
					msg.copyResourcesTo(as4Message);
					
					fillSoap12fromSoap11(as4Message, soapMessage);
				}
				
				mapAS4InfoFromSoapMessage(as4Message, soapMessage, payloadInfo, submitRequest, mapIdPartInfoToIdAttach,
						payloadConfig, payloadProfile, protocolFactory.getLogger());

			}
			else{
				
				OpenSPCoop2RestMessage<?> restMessage = msg.castAsRest();
				as4Message = messageFactory.createEmptyMessage(MessageType.SOAP_12, messageRole).castAsSoap(); // viene creato ad hoc
				msg.copyResourcesTo(as4Message);
				
				mapAS4InfoFromRestMessage(as4Message, restMessage, payloadInfo, submitRequest, mapIdPartInfoToIdAttach,
						payloadConfig, payloadProfile, protocolFactory.getLogger());
				
			}
			
			as4Message.setSoapAction("");
			
			
			
			// **** Build AS4 *****
					
			// Serializzo Header
			org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.utils.serializer.JaxbSerializer as4Serializer = 
					new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.utils.serializer.JaxbSerializer();
			byte[] as4Header = as4Serializer.toByteArray(ebmsV3_0Messagging);
			SOAPElement soapElementAs4Header = as4Message.createSOAPElement(as4Header);
			SOAPHeader hdr = as4Message.getSOAPHeader();
			if(hdr==null){
				hdr = as4Message.getSOAPPart().getEnvelope().addHeader(); 
			}
			hdr.addChildElement(soapElementAs4Header);
			
			// Serializzo Body
			backend.ecodex.org._1_1.utils.serializer.JaxbSerializer ecodexSerializer =
					new backend.ecodex.org._1_1.utils.serializer.JaxbSerializer();
			byte[] as4Body = ecodexSerializer.toByteArray(submitRequest);
			SOAPElement soapElementAs4Body = as4Message.createSOAPElement(as4Body);
			List<SOAPElement> childAs4Body = SoapUtils.getNotEmptyChildSOAPElement(soapElementAs4Body);
			for (SOAPElement payloadInfoAs4Body : childAs4Body) {
				String value = payloadInfoAs4Body.getAttribute("payloadId");
				if(mapIdPartInfoToIdAttach.containsKey(value)==false){
					throw new Exception("Attachment with payloadInfo id ["+value+"] not found");
				}
				String attachmentId = mapIdPartInfoToIdAttach.get(value);
				boolean xop = false;
				if(xop) {
					// versione 3 di domibus
					Element xomReference = payloadInfoAs4Body.getOwnerDocument().createElementNS(org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_NAMESPACE, 
							"xop:"+org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_LOCAL_NAME);
					xomReference.setAttribute(org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF, 
							attachmentId);
					payloadInfoAs4Body.appendChild(xomReference);
				}
				else {
					// versione 4 di domibus
					Element cidReference = payloadInfoAs4Body.getOwnerDocument().createElement("value");
					cidReference.setTextContent(attachmentId);
					payloadInfoAs4Body.appendChild(cidReference);
				}
				
			}
			as4Message.getSOAPBody().removeContents();
			as4Message.getSOAPBody().addChildElement(soapElementAs4Body);
			
			protocolMessage.setBustaRawContent(new AS4RawContent(as4Message.getSOAPPart().getEnvelope()));
			protocolMessage.setMessage(as4Message);
			return protocolMessage;
		
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
		
	}
	
	private Messaging buildEbmsV3_0Messagging(RuoloMessaggio ruoloMessaggio, Busta busta, 
			Soggetto soggettoMittente, Soggetto soggettoDestinatario, AccordoServizioParteComune aspc,String actionProperty,
			String nomePortType, ServiceBinding serviceBinding) throws RegistryNotFound, ProtocolException, DriverRegistroServiziException{
		
		Messaging ebmsV3_0Messagging = new Messaging();
		
		UserMessage userMessage = new UserMessage();
		ebmsV3_0Messagging.addUserMessage(userMessage);
		
		// MessageInfo
		
		MessageInfo messageInfo = new MessageInfo();
		messageInfo.setMessageId(busta.getID());
		messageInfo.setTimestamp(busta.getOraRegistrazione());
		if(busta.getRiferimentoMessaggio()!=null) {
			messageInfo.setRefToMessageId(busta.getRiferimentoMessaggio());
		}
		userMessage.setMessageInfo(messageInfo);
		
		
		// PartyInfo
		
		PartyInfo partyInfo = new PartyInfo();
		userMessage.setPartyInfo(partyInfo);
		
		// PartyInfo (From)

		From from = new From();
		PartyId partyIdFrom = new PartyId();
		partyIdFrom.setBase(AS4PropertiesUtils.getRequiredStringValue(soggettoMittente.getProtocolPropertyList(), 
				AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_BASE));
		partyIdFrom.setType(AS4PropertiesUtils.getRequiredStringValue(soggettoMittente.getProtocolPropertyList(), 
				AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_TYPE_VALUE));
		from.addPartyId(partyIdFrom);
		if(RuoloMessaggio.RICHIESTA.equals(ruoloMessaggio)){
			from.setRole(AS4Costanti.AS4_USER_MESSAGE_FROM_ROLE_INITIATOR);
		}
		else{
			from.setRole(AS4Costanti.AS4_USER_MESSAGE_FROM_ROLE_RESPONDER);
		}
		partyInfo.setFrom(from);
		busta.addProperty(AS4Costanti.AS4_BUSTA_MITTENTE_PARTY_ID_BASE, partyIdFrom.getBase());
		busta.addProperty(AS4Costanti.AS4_BUSTA_MITTENTE_PARTY_ID_TYPE, partyIdFrom.getType());
		
		// PartyInfo (To)

		To destinatario = new To();
		PartyId partyIdTo = new PartyId();
		partyIdTo.setBase(AS4PropertiesUtils.getRequiredStringValue(soggettoDestinatario.getProtocolPropertyList(), 
				AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_BASE));
		partyIdTo.setType(AS4PropertiesUtils.getRequiredStringValue(soggettoDestinatario.getProtocolPropertyList(), 
				AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_TYPE_VALUE));
		destinatario.addPartyId(partyIdTo);
		if(RuoloMessaggio.RICHIESTA.equals(ruoloMessaggio)){
			destinatario.setRole(AS4Costanti.AS4_USER_MESSAGE_FROM_ROLE_RESPONDER);
		}
		else{
			destinatario.setRole(AS4Costanti.AS4_USER_MESSAGE_FROM_ROLE_INITIATOR);
		}
		partyInfo.setTo(destinatario);
		busta.addProperty(AS4Costanti.AS4_BUSTA_DESTINATARIO_PARTY_ID_BASE, partyIdTo.getBase());
		busta.addProperty(AS4Costanti.AS4_BUSTA_DESTINATARIO_PARTY_ID_TYPE, partyIdTo.getType());
		
		// CollaborationInfo

		CollaborationInfo collaborationInfo = new CollaborationInfo();
		userMessage.setCollaborationInfo(collaborationInfo);
		
		// CollaborationInfo (Service)
		
		Service service = new Service();
		service.setBase(AS4PropertiesUtils.getRequiredStringValue(aspc.getProtocolPropertyList(), 
				AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_SERVICE_BASE));
		service.setType(AS4PropertiesUtils.getOptionalStringValue(aspc.getProtocolPropertyList(), 
				AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_SERVICE_TYPE));
		collaborationInfo.setService(service);
		busta.addProperty(AS4Costanti.AS4_BUSTA_SERVIZIO_COLLABORATION_INFO_SERVICE_BASE, service.getBase());
		busta.addProperty(AS4Costanti.AS4_BUSTA_SERVIZIO_COLLABORATION_INFO_SERVICE_TYPE, service.getType());
		
		// CollaborationInfo (Action)
			
		collaborationInfo.setAction(actionProperty);
		busta.addProperty(AS4Costanti.AS4_BUSTA_SERVIZIO_COLLABORATION_INFO_ACTION, actionProperty);
		
		// CollaborationInfo (ConversationId)
		if(busta.getCollaborazione()!=null) {
			collaborationInfo.setConversationId(busta.getCollaborazione());
		}
				
		return ebmsV3_0Messagging;
	}
	
	private void addMessageProperties(Messaging ebmsV3_0Messagging, Busta busta, OpenSPCoop2Message message, List<eu.domibus.configuration.Property> propertyConfig) throws Exception {
		
		UserMessage userMessage = ebmsV3_0Messagging.getUserMessage(0);
		
		TransportRequestContext transport = message.getTransportRequestContext();
		if(transport==null) {
			throw new Exception("TransportRequestContext undefined");	
		}
		
		// MessageProperties
		
		MessageProperties messageProperties = null;
		
		if(propertyConfig!=null && propertyConfig.size()>0) {
			for (eu.domibus.configuration.Property property : propertyConfig) {
				
				if(property.getValue()==null) {
					throw new Exception("Found property '"+property.getName()+"' without value configuration");
				}
				PropertyValueHeader hdr = property.getValue().getHeader();
				PropertyValueUrl url = property.getValue().getUrl();
				if(hdr==null && url==null) {
					throw new Exception("Found property '"+property.getName()+"' without value configuration (both url and header undefined)");	
				}
				
				String value = null;
				StringBuilder bfErrore = new StringBuilder();
				if(hdr!=null) {
					value = transport.getHeaderFirstValue(hdr.getName());
					if(value==null) {
						bfErrore.append("Header http '"+hdr.getName()+"' not found");
					}
					else {
						if(hdr.getPattern()!=null) {
							try {
								String newValue = RegularExpressionEngine.getStringMatchPattern(value, hdr.getPattern());
								if(newValue!=null) {
									value = newValue;
								}
							}
							catch(RegExpNotFoundException notFound) {}
							catch(Exception e) {
								bfErrore.append("\n\t");
								bfErrore.append("(Header http '"+hdr.getName()+"' value '"+value+"') Valuate regularExpr '"+hdr.getPattern()+"' error: "+e.getMessage());
							}
						}
					}
				}
				if(url!=null) {
					String urlInvocazionePD = transport.getUrlInvocazione_formBased();
					try {
						String newValue = RegularExpressionEngine.getStringMatchPattern(urlInvocazionePD, url.getPattern());
						if(newValue!=null) {
							value = newValue;
						}
					}
					catch(RegExpNotFoundException notFound) {}
					catch(Exception e) {
						bfErrore.append("\n\t");
						bfErrore.append("- (URL '"+urlInvocazionePD+"') Valuate regularExpr '"+url.getPattern()+"' error: "+e.getMessage());
					}
				}
				if(property.isRequired()) {
					if(value==null) {
						throw new Exception("It's not possible to extract a value from the request context for required property '"+property.getName()+"'. "+bfErrore.toString());	
					}
				}
				
				if(value!=null) {
					if(messageProperties==null) {
						messageProperties = new MessageProperties();
						userMessage.setMessageProperties(messageProperties);
					}
					
					Property propertyOriginalSender = new Property();
					propertyOriginalSender.setName(property.getKey());
					propertyOriginalSender.setBase(value);
					messageProperties.addProperty(propertyOriginalSender);
					busta.addProperty(AS4Costanti.AS4_BUSTA_SERVIZIO_COLLABORATION_MESSAGE_PROPERTY_PREFIX+property.getKey(), 
							value);
				}
				
				

			}
		}
		
	}
	
	private void fillSoap12fromSoap11(OpenSPCoop2SoapMessage soapMessage12, OpenSPCoop2SoapMessage soapMessage11) throws Exception{
		if(soapMessage11.getSOAPHeader()!=null){
			List<SOAPElement> listNodeSoapHeader = SoapUtils.getNotEmptyChildSOAPElement(soapMessage11.getSOAPHeader());
			if(listNodeSoapHeader!=null && listNodeSoapHeader.size()>0){
				SOAPHeader hdr = soapMessage12.getSOAPHeader();
				if(hdr==null){
					hdr = soapMessage12.getSOAPPart().getEnvelope().addHeader(); 
				}
				for (SOAPElement soapElement : listNodeSoapHeader) {
					hdr.addChildElement(soapElement);
				}
			}
		}
		List<SOAPElement> listNodeSoapBody = SoapUtils.getNotEmptyChildSOAPElement(soapMessage11.getSOAPBody());
		if(listNodeSoapBody!=null && listNodeSoapBody.size()>0){
			for (SOAPElement soapElement : listNodeSoapBody) {
				soapMessage12.getSOAPBody().addChildElement(soapElement);
			}
		}
		if(soapMessage11.countAttachments()>0){
			java.util.Iterator<?> iter = soapMessage11.getAttachments();
			while( iter.hasNext() ){
				AttachmentPart p = (AttachmentPart) iter.next();
				soapMessage12.addAttachmentPart(p);
			}
		}
	}
	
	private void mapAS4InfoFromSoapMessage(OpenSPCoop2SoapMessage as4Message,OpenSPCoop2SoapMessage soapMessage,
			PayloadInfo payloadInfo,SubmitRequest submitRequest,
			Map<String,String> mapIdPartInfoToIdAttach,
			List<Payload> payloadConfig, String payloadProfile, Logger log) throws Exception{
	
		
		// Attachments
		
		List<PartInfo> _listPartInfoUserMessage = null; 
		List<LargePayloadType> _listPayload = null;
		List<AttachmentPart> _listAP = null;
		
		boolean reOrderAttachments = false; // se abilitato gli attachments li riaggiungo dopo per averli dopo il body element
		
		int countAttach = as4Message.countAttachments();
		if(countAttach>0){
			
			_listPartInfoUserMessage = new ArrayList<PartInfo>();
			_listPayload = new ArrayList<LargePayloadType>();
			if(reOrderAttachments) {
				_listAP = new ArrayList<AttachmentPart>();
			}
			
			// Attachments
			java.util.Iterator<?> iter = as4Message.getAttachments();
			int index = 1;
			while( iter.hasNext() ){
				AttachmentPart p = (AttachmentPart) iter.next();
				
				if(payloadConfig.size()<(index+1)) {
					throw new ProtocolException("Attachment-"+index+" non previsto in payload profile '"+payloadProfile+"'");
				}
				Payload payload = payloadConfig.get(index);
				
				String contentID = p.getContentId();
				if(contentID==null){
					throw new ProtocolException("Attachment without ContentID");
				}
				if(contentID.startsWith("<")){
					contentID = contentID.substring(1);
				}
				if(contentID.endsWith(">")){
					contentID = contentID.substring(0,contentID.length()-1);
				}
				
				//String partInfoCid = "cid:attach"+(index++);
				String partInfoCid = payload.getCid();
				String cid = "cid:"+contentID;
				mapIdPartInfoToIdAttach.put(partInfoCid, cid);
				
				String contentType = p.getContentType();
				if(contentType==null){
					throw new ProtocolException("Attachment without ContentType");
				}
				String contentTypeUtilizzato = payload.getMimeType();
				if(contentTypeUtilizzato==null) {
					contentTypeUtilizzato = ContentTypeUtilities.readBaseTypeFromContentType(contentType);
				}
				
				PartInfo pInfo = new PartInfo();
				pInfo.setHref(partInfoCid);
				PartProperties partProperties = new PartProperties();
				Property property = new Property();
				property.setName(AS4Costanti.AS4_USER_MESSAGE_PAYLOAD_INFO_PROPERTIES_MIME_TYPE);
				property.setBase(contentTypeUtilizzato);
				partProperties.addProperty(property);
				pInfo.setPartProperties(partProperties);
				_listPartInfoUserMessage.add(pInfo);
				
				LargePayloadType pBodyInfo = new LargePayloadType();
				pBodyInfo.setPayloadId(partInfoCid);
				pBodyInfo.setContentType(contentTypeUtilizzato);
				_listPayload.add(pBodyInfo);
				
				if(reOrderAttachments) {
					_listAP.add(p);
				}
				
				index++;
			}
						
			if(reOrderAttachments) {
				as4Message.removeAllAttachments(); 
			}
		}
		
		int attachRequired = 0;
		// inizio da 1, ignoro il primo payload
		for (int i = 1; i < payloadConfig.size(); i++) {
			Payload payload = payloadConfig.get(i);
			if(payload.isRequired()) {
				attachRequired++;
			}
		}
		if(countAttach<attachRequired) {
			throw new ProtocolException("Il payload profile '"+payloadProfile+"' richiede la presenza obbligatoria di "+attachRequired+" attachments mentre ne sono stati riscontrati "+countAttach+".");
		}
		
		
		
		// Body
		
		if(payloadConfig.size()<1) {
			throw new ProtocolException("Payload profile '"+payloadProfile+"' non definisce alcun payload?");
		}
		Payload payload = payloadConfig.get(0);
		
		PartInfo _PartInfoUserMessageBody = null;
		LargePayloadType _PayloadBody = null;
		AttachmentPart apBody = null;
		// DEVO spedire tutto il documento, altrimenti eventuali parti firmate vengono perse.
		//java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
		//soapMessage.writeTo(bout, true);
		//bout.flush();
		//bout.close();
		//byte[]xml = bout.toByteArray();
		byte[]xml = as4Message.getAsByte(soapMessage.getSOAPPart().getEnvelope(), true); // prendo soapMessage per avere l'originale
		if(xml!=null && xml.length>0){
			// esiste un contenuto nel body
			
			String contentTypeUtilizzato = payload.getMimeType();
			if(contentTypeUtilizzato==null) {
				if(MessageType.SOAP_11.equals(as4Message.getMessageType())) {
					contentTypeUtilizzato = HttpConstants.CONTENT_TYPE_SOAP_1_1;
				}
				else {
					contentTypeUtilizzato = HttpConstants.CONTENT_TYPE_SOAP_1_2;
				}
			}
			
			String ctBase = ContentTypeUtilities.readBaseTypeFromContentType(contentTypeUtilizzato);
			if(HttpConstants.CONTENT_TYPE_TEXT_XML.equals(ctBase)) {
				if(MailcapActivationReader.existsDataContentHandler(HttpConstants.CONTENT_TYPE_TEXT_XML)){
					apBody = as4Message.createAttachmentPart();
					as4Message.updateAttachmentPart(apBody, xml, contentTypeUtilizzato);
				}
				else {
					InputStreamDataSource isSource = new InputStreamDataSource("eDeliveryPayload", contentTypeUtilizzato, xml);
					apBody = as4Message.createAttachmentPart(new DataHandler(isSource));
				}
			}
			else {
				InputStreamDataSource isSource = new InputStreamDataSource("eDeliveryPayload", contentTypeUtilizzato, xml);
				apBody = as4Message.createAttachmentPart(new DataHandler(isSource));
			}
			
			String contentID = as4Message.createContentID(AS4Costanti.AS4_NAMESPACE_CID_MESSAGGIO);
			apBody.setContentId(contentID);
						
			String contentIDforHRef = contentID; 
			if(contentIDforHRef.startsWith("<")){
				contentIDforHRef = contentIDforHRef.substring(1);
			}
			if(contentIDforHRef.endsWith(">")){
				contentIDforHRef = contentIDforHRef.substring(0,contentIDforHRef.length()-1);
			}
			
			//String partInfoCid = "cid:message";
			String partInfoCid = payload.getCid();
			String cid = "cid:"+contentIDforHRef;
			mapIdPartInfoToIdAttach.put(partInfoCid, cid);
									
			PartInfo pInfo = new PartInfo();
			pInfo.setHref(partInfoCid);
			PartProperties partProperties = new PartProperties();
			Property property = new Property();
			property.setName(AS4Costanti.AS4_USER_MESSAGE_PAYLOAD_INFO_PROPERTIES_MIME_TYPE);
			property.setBase(contentTypeUtilizzato);
			partProperties.addProperty(property);
			pInfo.setPartProperties(partProperties);
			_PartInfoUserMessageBody = pInfo;
			
			LargePayloadType pBodyInfo = new LargePayloadType();
			pBodyInfo.setPayloadId(partInfoCid);
			pBodyInfo.setContentType(contentTypeUtilizzato);
			_PayloadBody = pBodyInfo;
		}
		
		
		if(_PartInfoUserMessageBody!=null){
			payloadInfo.addPartInfo(_PartInfoUserMessageBody);
			submitRequest.addPayload(_PayloadBody);
			as4Message.addAttachmentPart(apBody);
		}
		if(_listPartInfoUserMessage!=null && _listPartInfoUserMessage.size()>0){
			payloadInfo.getPartInfoList().addAll(_listPartInfoUserMessage);
			submitRequest.getPayloadList().addAll(_listPayload);
			if(reOrderAttachments) {
				if(_listAP!=null && _listAP.size()>0) {
					for (AttachmentPart ap : _listAP) {
						as4Message.addAttachmentPart(ap);
					}
				}
			}
		}
	}
	

	private void mapAS4InfoFromRestMessage(OpenSPCoop2SoapMessage as4Message,
			OpenSPCoop2RestMessage<?> restMessage,PayloadInfo payloadInfo,SubmitRequest submitRequest,
			Map<String,String> mapIdPartInfoToIdAttach,
			List<Payload> payloadConfig, String payloadProfile, Logger log) throws Exception{
		
		if(restMessage.hasContent()==false){
			throw new Exception("Messaggio non contiene dati da inoltrare");
		}
		
		int countAttach = 0;
		
		if(MessageType.MIME_MULTIPART.equals(restMessage.getMessageType())){
					
			OpenSPCoop2RestMimeMultipartMessage msgMime = restMessage.castAsRestMimeMultipart();
			MimeMultipart mimeMultipart = msgMime.getContent()!=null ? msgMime.getContent().getMimeMultipart() : null;
			if(mimeMultipart!=null) {
			
				countAttach = mimeMultipart.countBodyParts();
				
				int index = 1;
				for (int i = 0; i < countAttach; i++) {
					BodyPart bodyPart = mimeMultipart.getBodyPart(i);
					
					if(payloadConfig.size()<(i+1)) {
						throw new ProtocolException("Attachment-"+index+" non previsto in payload profile '"+payloadProfile+"'");
					}
					Payload payload = payloadConfig.get(i);
	
					String contentID = mimeMultipart.getContentID(bodyPart);
					if(contentID==null){
						throw new ProtocolException("BodyPart without ContentID");
					}
					
					String contentIDforHRef = contentID; 
					if(contentIDforHRef.startsWith("<")){
						contentIDforHRef = contentIDforHRef.substring(1);
					}
					if(contentIDforHRef.endsWith(">")){
						contentIDforHRef = contentIDforHRef.substring(0,contentIDforHRef.length()-1);
					}
					
					//String partInfoCid = "cid:attach"+(index++);
					String partInfoCid = payload.getCid();
					String cid = "cid:"+contentIDforHRef;
					mapIdPartInfoToIdAttach.put(partInfoCid, cid);
					
					String contentType = bodyPart.getContentType();
					if(contentType==null){
						throw new ProtocolException("BodyPart without ContentType");
					}
					String contentTypeUtilizzato = payload.getMimeType();
					if(contentTypeUtilizzato==null) {
						contentTypeUtilizzato = ContentTypeUtilities.readBaseTypeFromContentType(contentType);
					}
					
					AttachmentPart ap = as4Message.createAttachmentPart(bodyPart.getDataHandler());
					ap.setContentId(contentID);
					as4Message.addAttachmentPart(ap);
					
					PartInfo pInfo = new PartInfo();
					pInfo.setHref(partInfoCid);
					PartProperties partProperties = new PartProperties();
					Property property = new Property();
					property.setName(AS4Costanti.AS4_USER_MESSAGE_PAYLOAD_INFO_PROPERTIES_MIME_TYPE);
					property.setBase(contentTypeUtilizzato);
					partProperties.addProperty(property);
					pInfo.setPartProperties(partProperties);
					payloadInfo.addPartInfo(pInfo);
					
					LargePayloadType pBodyInfo = new LargePayloadType();
					pBodyInfo.setPayloadId(partInfoCid);
					pBodyInfo.setContentType(contentTypeUtilizzato);
					submitRequest.addPayload(pBodyInfo);
					
					index++;
				}
			}
			
		}
		else{
		
			if(payloadConfig.size()<1) {
				throw new ProtocolException("Payload profile '"+payloadProfile+"' non definisce alcun payload?");
			}
			Payload payload = payloadConfig.get(0);
			
			// esiste un contenuto nel body
			AttachmentPart ap = null;

			String contentType = restMessage.getContentType();
			String contentTypeUtilizzato = payload.getMimeType();
			if(contentTypeUtilizzato==null) {
				contentTypeUtilizzato = ContentTypeUtilities.readBaseTypeFromContentType(contentType);
			}
			
			if(MessageType.XML.equals(restMessage.getMessageType())){		
				
				String ctBase = ContentTypeUtilities.readBaseTypeFromContentType(contentTypeUtilizzato);
				byte [] xml = restMessage.getAsByte(restMessage.castAsRestXml().getContent(), true);
				if(HttpConstants.CONTENT_TYPE_TEXT_XML.equals(ctBase)) {
					if(MailcapActivationReader.existsDataContentHandler(HttpConstants.CONTENT_TYPE_TEXT_XML)){
						ap = as4Message.createAttachmentPart();
						as4Message.updateAttachmentPart(ap, xml, contentTypeUtilizzato);
					}
					else {
						InputStreamDataSource isSource = new InputStreamDataSource("eDeliveryPayload", contentTypeUtilizzato, xml);
						ap = as4Message.createAttachmentPart(new DataHandler(isSource));
					}
				}
				else {
					InputStreamDataSource isSource = new InputStreamDataSource("eDeliveryPayload", contentTypeUtilizzato, xml);
					ap = as4Message.createAttachmentPart(new DataHandler(isSource));
				}
			}
			else if(MessageType.JSON.equals(restMessage.getMessageType())){
				ap = as4Message.createAttachmentPart();
				ap.setContent(restMessage.castAsRestJson().getContent(), contentTypeUtilizzato);
			}
			else {
				InputStreamDataSource isSource = new InputStreamDataSource("eDeliveryPayload", contentTypeUtilizzato, restMessage.castAsRestBinary().getContent().getContent());
				ap = as4Message.createAttachmentPart(new DataHandler(isSource));
			}

			String contentID = as4Message.createContentID(AS4Costanti.AS4_NAMESPACE_CID_MESSAGGIO);
			if(contentID.startsWith("<")){
				contentID = contentID.substring(1);
			}
			if(contentID.endsWith(">")){
				contentID = contentID.substring(0,contentID.length()-1);
			}
			//String partInfoCid = "cid:message";
			String partInfoCid = payload.getCid();
			String cid = "cid:"+contentID;
			mapIdPartInfoToIdAttach.put(partInfoCid, cid);
			
			ap.setContentId(contentID);
			as4Message.addAttachmentPart(ap);
				
			PartInfo pInfo = new PartInfo();
			pInfo.setHref(partInfoCid);
			PartProperties partProperties = new PartProperties();
			Property property = new Property();
			property.setName(AS4Costanti.AS4_USER_MESSAGE_PAYLOAD_INFO_PROPERTIES_MIME_TYPE);
			property.setBase(contentTypeUtilizzato);
			partProperties.addProperty(property);
			pInfo.setPartProperties(partProperties);
			payloadInfo.addPartInfo(pInfo);
				
			LargePayloadType pBodyInfo = new LargePayloadType();
			pBodyInfo.setPayloadId(partInfoCid);
			pBodyInfo.setContentType(contentTypeUtilizzato);
			submitRequest.addPayload(pBodyInfo);

		}
		
		int attachRequired = 0;
		// inizio da 1, ignoro il primo payload
		for (int i = 1; i < payloadConfig.size(); i++) {
			Payload payload = payloadConfig.get(i);
			if(payload.isRequired()) {
				attachRequired++;
			}
		}
		if(attachRequired>0) {
			int attachTrovati = 0;
			if(countAttach>1) { // maggiore di 1 perche' salto il payload
				attachTrovati = countAttach-1;
			}
			if(attachTrovati<attachRequired) {
				throw new ProtocolException("Il payload profile '"+payloadProfile+"' richiede la presenza obbligatoria di "+attachRequired+" attachments mentre ne sono stati riscontrati "+attachTrovati+".");
			}
		}
	}
}
