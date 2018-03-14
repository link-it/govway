/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From;
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
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Resource;
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
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.protocol.as4.AS4RawContent;
import org.openspcoop2.protocol.as4.constants.AS4Costanti;
import org.openspcoop2.protocol.as4.pmode.Translator;
import org.openspcoop2.protocol.as4.utils.AS4PropertiesUtils;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.ProtocolMessage;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.utils.dch.DataContentHandlerManager;
import org.openspcoop2.utils.dch.InputStreamDataSource;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;
import org.w3c.dom.Element;

import backend.ecodex.org._1_1.PayloadType;
import backend.ecodex.org._1_1.SendRequest;
import eu.domibus.configuration.Attachment;
import eu.domibus.configuration.Payload;
import eu.domibus.configuration.PayloadProfile;
import eu.domibus.configuration.PayloadProfiles;

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
						
			PayloadProfiles pps = AS4PayloadProfilesUtils.read(registryReader,protocolFactory,
					aspc, idAccordo, true);
			
			String azione = busta.getAzione();
			String actionProperty = null;
			String nomePortType = asps.getPortType();
			String payloadProfile = null;
			if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(aspc.getServiceBinding())) {
				for (Resource resource : aspc.getResourceList()) {
					if(resource.getNome().equals(azione)) {
						actionProperty = AS4PropertiesUtils.getRequiredStringValue(resource.getProtocolPropertyList(), 
								AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_ACTION);
						payloadProfile = AS4PropertiesUtils.getOptionalStringValue(resource.getProtocolPropertyList(), 
								AS4Costanti.AS4_PROTOCOL_PROPERTIES_ACTION_PAYLOAD_PROFILE);
						break;
					}
				}
			}
			else {
				if(nomePortType!=null) {
					for (PortType pt : aspc.getPortTypeList()) {
						if(pt.getNome().equals(nomePortType)) {
							for (Operation op : pt.getAzioneList()) {
								if(op.getNome().equals(azione)) {
									actionProperty = AS4PropertiesUtils.getRequiredStringValue(op.getProtocolPropertyList(), 
											AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_ACTION);
									payloadProfile = AS4PropertiesUtils.getOptionalStringValue(op.getProtocolPropertyList(), 
											AS4Costanti.AS4_PROTOCOL_PROPERTIES_ACTION_PAYLOAD_PROFILE);
									break;
								}
							}
							break;
						}
					}
				}
				else {
					for (Azione azioneAccordo : aspc.getAzioneList()) {
						if(azioneAccordo.getNome().equals(azione)) {
							actionProperty = AS4PropertiesUtils.getRequiredStringValue(azioneAccordo.getProtocolPropertyList(), 
									AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_ACTION);
							payloadProfile = AS4PropertiesUtils.getOptionalStringValue(azioneAccordo.getProtocolPropertyList(), 
									AS4Costanti.AS4_PROTOCOL_PROPERTIES_ACTION_PAYLOAD_PROFILE);
							break;
						}
					}
				}
			}
			if(actionProperty==null) {
				throw new ProtocolException("Action '"+azione+"' not found");
			}
			if(payloadProfile==null) {
				Translator t = new Translator(registryReader, protocolFactory);
				payloadProfile = t.translatePayloadProfileDefault().get(0).getName();
			}
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
			
			
			
			// **** Prepare *****
			
			Map<String,String> mapIdPartInfoToIdAttach = new Hashtable<>();
			
			Messaging ebmsV3_0Messagging = this.buildEbmsV3_0Messagging(ruoloMessaggio,busta,
					soggettoMittente, soggettoDestinatario, aspc, actionProperty,
					asps.getPortType(), msg.getServiceBinding());
			
			// PayloadInfo
			PayloadInfo payloadInfo = new PayloadInfo();
			ebmsV3_0Messagging.getUserMessage(0).setPayloadInfo(payloadInfo);
			
			// Send Request Body
			SendRequest sendRequest = new SendRequest();
						
			if(ServiceBinding.SOAP.equals(msg.getServiceBinding())){
				
				OpenSPCoop2SoapMessage soapMessage = msg.castAsSoap();
				
				if(MessageType.SOAP_12.equals(msg.getMessageType())){
					as4Message = soapMessage; // viene trasformato il messaggio ricevuto per avere benefici di performance
				}
				else{
					as4Message = OpenSPCoop2MessageFactory.getMessageFactory().createEmptyMessage(MessageType.SOAP_12, messageRole).castAsSoap(); // viene creato ad hoc
					msg.copyResourcesTo(as4Message);
					
					fillSoap12fromSoap11(as4Message, soapMessage);
				}
				
				mapAS4InfoFromSoapMessage(as4Message, payloadInfo, sendRequest, mapIdPartInfoToIdAttach,
						payloadConfig, payloadProfile, protocolFactory.getLogger());

			}
			else{
				
				OpenSPCoop2RestMessage<?> restMessage = msg.castAsRest();
				as4Message = OpenSPCoop2MessageFactory.getMessageFactory().createEmptyMessage(MessageType.SOAP_12, messageRole).castAsSoap(); // viene creato ad hoc
				msg.copyResourcesTo(as4Message);
				
				mapAS4InfoFromRestMessage(as4Message, restMessage, payloadInfo, sendRequest, mapIdPartInfoToIdAttach,
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
			byte[] as4Body = ecodexSerializer.toByteArray(sendRequest);
			SOAPElement soapElementAs4Body = as4Message.createSOAPElement(as4Body);
			List<SOAPElement> childAs4Body = SoapUtils.getNotEmptyChildSOAPElement(soapElementAs4Body);
			for (SOAPElement payloadInfoAs4Body : childAs4Body) {
				String value = payloadInfoAs4Body.getAttribute("payloadId");
				if(mapIdPartInfoToIdAttach.containsKey(value)==false){
					throw new Exception("Attachment with payloadInfo id ["+value+"] not found");
				}
				String attachmentId = mapIdPartInfoToIdAttach.get(value);
				Element xomReference = payloadInfoAs4Body.getOwnerDocument().createElementNS(org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_NAMESPACE, 
						"xop:"+org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_LOCAL_NAME);
				xomReference.setAttribute(org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF, 
						attachmentId);
				payloadInfoAs4Body.appendChild(xomReference);
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
		
		// MessageProperties
		
		// TODO DINAMICHE RISPETTO AD UN FILE CHE DEFINISCE COME ESTRARLE
		MessageProperties messageProperties = new MessageProperties();
		userMessage.setMessageProperties(messageProperties);
		
		Property propertyOriginalSender = new Property();
		propertyOriginalSender.setName("originalSender");
		propertyOriginalSender.setBase("urn:oasis:names:tc:ebcore:partyid-type:unregistered:C1");
		messageProperties.addProperty(propertyOriginalSender);
		busta.addProperty(AS4Costanti.AS4_BUSTA_SERVIZIO_COLLABORATION_MESSAGE_PROPERTY_PREFIX+"originalSender", 
				"urn:oasis:names:tc:ebcore:partyid-type:unregistered:C1");
		
		Property propertyFinalRecipient = new Property();
		propertyFinalRecipient.setName("finalRecipient");
		propertyFinalRecipient.setBase("urn:oasis:names:tc:ebcore:partyid-type:unregistered:C4");
		busta.addProperty(AS4Costanti.AS4_BUSTA_SERVIZIO_COLLABORATION_MESSAGE_PROPERTY_PREFIX+"finalRecipient", 
				"urn:oasis:names:tc:ebcore:partyid-type:unregistered:C4");
		messageProperties.addProperty(propertyFinalRecipient);
		
		return ebmsV3_0Messagging;
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
	
	private void mapAS4InfoFromSoapMessage(OpenSPCoop2SoapMessage soapMessage,PayloadInfo payloadInfo,SendRequest sendRequest,
			Map<String,String> mapIdPartInfoToIdAttach,
			List<Payload> payloadConfig, String payloadProfile, Logger log) throws Exception{
	
		
		// Attachments
		
		List<PartInfo> _listPartInfoUserMessage = null; 
		List<PayloadType> _listPayload = null;
		
		if(soapMessage.countAttachments()>0){
			
			_listPartInfoUserMessage = new ArrayList<PartInfo>();
			_listPayload = new ArrayList<PayloadType>();
			
			// Attachments
			java.util.Iterator<?> iter = soapMessage.getAttachments();
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
				
				PayloadType pBodyInfo = new PayloadType();
				pBodyInfo.setPayloadId(partInfoCid);
				pBodyInfo.setContentType(contentTypeUtilizzato);
				_listPayload.add(pBodyInfo);
			}
		}
		
		if(_listPartInfoUserMessage!=null && _listPartInfoUserMessage.size()>0){
			payloadInfo.getPartInfoList().addAll(_listPartInfoUserMessage);
			sendRequest.getPayloadList().addAll(_listPayload);
		}
		
		
		// Body
		
		if(payloadConfig.size()<1) {
			throw new ProtocolException("Payload profile '"+payloadProfile+"' non definisce alcun payload?");
		}
		Payload payload = payloadConfig.get(0);
		
		PartInfo _PartInfoUserMessageBody = null;
		PayloadType _PayloadBody = null;
		// DEVO spedire tutto il documento, altrimenti eventuali parti firmate vengono perse.
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		soapMessage.writeTo(bout, true);
		bout.flush();
		bout.close();
		byte[]xml = bout.toByteArray();
		if(xml!=null && xml.length>0){
			// esiste un contenuto nel body
			AttachmentPart ap = null;
			
			String contentTypeUtilizzato = payload.getMimeType();
			if(contentTypeUtilizzato==null) {
				if(MessageType.SOAP_11.equals(soapMessage.getMessageType())) {
					contentTypeUtilizzato = HttpConstants.CONTENT_TYPE_SOAP_1_1;
				}
				else {
					contentTypeUtilizzato = HttpConstants.CONTENT_TYPE_SOAP_1_2;
				}
			}
			
			Source streamSource = null;
			DataContentHandlerManager dchManager = new DataContentHandlerManager(log);
			if(dchManager.readMimeTypesContentHandler().containsKey(contentTypeUtilizzato)) {
				// Se è non registrato un content handler per text/xml
				// succede se dentro l'ear non c'e' il jar mailapi e l'application server non ha caricato il modulo mailapi (es. tramite versione standalone standard)
				// e si usa il metodo seguente DOMSource si ottiene il seguente errore:
				// javax.xml.soap.SOAPException: no object DCH for MIME type text/xml
				//    at com.sun.xml.messaging.saaj.soap.MessageImpl.writeTo(MessageImpl.java:1396) ~[saaj-impl-1.3.25.jar:?]
				//System.out.println("XML (DOMSource)");
				streamSource = new DOMSource(XMLUtils.getInstance().newElement(xml));
			}
			else {
				// Se è registrato un content handler per text/xml
				// e succede se dentro l'ear c'e' il jar mailapi oppure se l'application server ha caricato il modulo mailapi (es. tramite versione standalone full)
				// e si usa il metodo seguente StreamSource, si ottiene il seguente errore:
				//  Unable to run the JAXP transformer on a stream org.xml.sax.SAXParseException; Premature end of file. (sourceException: Error during saving a multipart message) 
				//  	com.sun.xml.messaging.saaj.SOAPExceptionImpl: Error during saving a multipart message
				//        at com.sun.xml.messaging.saaj.soap.MessageImpl.writeTo(MessageImpl.java:1396) ~[saaj-impl-1.3.25.jar:?]
				//        at org.openspcoop2.message.Message1_1_FIX_Impl.writeTo(Message1_1_FIX_Impl.java:172) ~[openspcoop2_message_BUILD-13516.jar:?]
				//        at org.openspcoop2.message.OpenSPCoop2Message_11_impl.writeTo
				//System.out.println("XML (StreamSource)");
				streamSource = new javax.xml.transform.stream.StreamSource(new java.io.ByteArrayInputStream(xml));
			}
			ap = soapMessage.createAttachmentPart();
			ap.setContent(streamSource, contentTypeUtilizzato);
			
			String contentID = soapMessage.createContentID(AS4Costanti.AS4_NAMESPACE_CID_MESSAGGIO);
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
			soapMessage.addAttachmentPart(ap);
						
			PartInfo pInfo = new PartInfo();
			pInfo.setHref(partInfoCid);
			PartProperties partProperties = new PartProperties();
			Property property = new Property();
			property.setName(AS4Costanti.AS4_USER_MESSAGE_PAYLOAD_INFO_PROPERTIES_MIME_TYPE);
			property.setBase(contentTypeUtilizzato);
			partProperties.addProperty(property);
			pInfo.setPartProperties(partProperties);
			_PartInfoUserMessageBody = pInfo;
			
			PayloadType pBodyInfo = new PayloadType();
			pBodyInfo.setPayloadId(partInfoCid);
			pBodyInfo.setContentType(contentTypeUtilizzato);
			_PayloadBody = pBodyInfo;
		}
		
		if(_PartInfoUserMessageBody!=null){
			payloadInfo.addPartInfo(_PartInfoUserMessageBody);
			sendRequest.addPayload(_PayloadBody);
		}
	}
	

	private void mapAS4InfoFromRestMessage(OpenSPCoop2SoapMessage as4Message,
			OpenSPCoop2RestMessage<?> restMessage,PayloadInfo payloadInfo,SendRequest sendRequest,
			Map<String,String> mapIdPartInfoToIdAttach,
			List<Payload> payloadConfig, String payloadProfile, Logger log) throws Exception{
		
		if(restMessage.hasContent()==false){
			throw new Exception("Messaggio non contiene dati da inoltrare");
		}
		
		if(MessageType.MIME_MULTIPART.equals(restMessage.getMessageType())){
					
			OpenSPCoop2RestMimeMultipartMessage msgMime = restMessage.castAsRestMimeMultipart();
			int index = 1;
			for (int i = 0; i < msgMime.getContent().countBodyParts(); i++) {
				BodyPart bodyPart = msgMime.getContent().getBodyPart(i);
				
				if(payloadConfig.size()<(i+1)) {
					throw new ProtocolException("Attachment-"+index+" non previsto in payload profile '"+payloadProfile+"'");
				}
				Payload payload = payloadConfig.get(i);
				
				String contentID = msgMime.getContent().getContentID(bodyPart);
				if(contentID==null){
					throw new ProtocolException("BodyPart without ContentID");
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
				
				PayloadType pBodyInfo = new PayloadType();
				pBodyInfo.setPayloadId(partInfoCid);
				pBodyInfo.setContentType(contentTypeUtilizzato);
				sendRequest.addPayload(pBodyInfo);
				
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
				// solo text/xml può essere costruito con DOMSource

				byte [] xml = restMessage.getAsByte(restMessage.castAsRestXml().getContent(), true);
				Source streamSource = null;
				DataContentHandlerManager dchManager = new DataContentHandlerManager(log);
				if(dchManager.readMimeTypesContentHandler().containsKey(contentTypeUtilizzato)) {
					// Se è non registrato un content handler per text/xml
					// succede se dentro l'ear non c'e' il jar mailapi e l'application server non ha caricato il modulo mailapi (es. tramite versione standalone standard)
					// e si usa il metodo seguente DOMSource si ottiene il seguente errore:
					// javax.xml.soap.SOAPException: no object DCH for MIME type text/xml
					//    at com.sun.xml.messaging.saaj.soap.MessageImpl.writeTo(MessageImpl.java:1396) ~[saaj-impl-1.3.25.jar:?]
					//System.out.println("XML (DOMSource)");
					streamSource = new DOMSource(XMLUtils.getInstance().newElement(xml));
				}
				else {
					// Se è registrato un content handler per text/xml
					// e succede se dentro l'ear c'e' il jar mailapi oppure se l'application server ha caricato il modulo mailapi (es. tramite versione standalone full)
					// e si usa il metodo seguente StreamSource, si ottiene il seguente errore:
					//  Unable to run the JAXP transformer on a stream org.xml.sax.SAXParseException; Premature end of file. (sourceException: Error during saving a multipart message) 
					//  	com.sun.xml.messaging.saaj.SOAPExceptionImpl: Error during saving a multipart message
					//        at com.sun.xml.messaging.saaj.soap.MessageImpl.writeTo(MessageImpl.java:1396) ~[saaj-impl-1.3.25.jar:?]
					//        at org.openspcoop2.message.Message1_1_FIX_Impl.writeTo(Message1_1_FIX_Impl.java:172) ~[openspcoop2_message_BUILD-13516.jar:?]
					//        at org.openspcoop2.message.OpenSPCoop2Message_11_impl.writeTo
					//System.out.println("XML (StreamSource)");
					streamSource = new javax.xml.transform.stream.StreamSource(new java.io.ByteArrayInputStream(xml));
				}
				ap = as4Message.createAttachmentPart();
				ap.setContent(streamSource, contentTypeUtilizzato);
			}
			else if(MessageType.JSON.equals(restMessage.getMessageType())){
				ap = as4Message.createAttachmentPart();
				ap.setContent(restMessage.castAsRestJson().getContent(), contentTypeUtilizzato);
			}
			else {
				InputStreamDataSource isSource = new InputStreamDataSource("RestBinary", contentTypeUtilizzato, restMessage.castAsRestBinary().getContent());
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
				
			PayloadType pBodyInfo = new PayloadType();
			pBodyInfo.setPayloadId(partInfoCid);
			pBodyInfo.setContentType(contentTypeUtilizzato);
			sendRequest.addPayload(pBodyInfo);

		}
	}
}
