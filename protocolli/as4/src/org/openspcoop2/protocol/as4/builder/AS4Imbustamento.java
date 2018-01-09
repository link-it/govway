/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
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
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.protocol.as4.AS4RawContent;
import org.openspcoop2.protocol.as4.constants.AS4Costanti;
import org.openspcoop2.protocol.as4.utils.AS4PropertiesUtils;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.ProtocolMessage;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.utils.dch.InputStreamDataSource;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import backend.ecodex.org._1_1.PayloadType;
import backend.ecodex.org._1_1.SendRequest;

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
			IRegistryReader registryReader) throws ProtocolException{
		
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
			IDPortType idPT = new IDPortType();
			idPT.setIdAccordo(idAccordo);
			idPT.setNome(asps.getPortType());
			PortType portType = registryReader.getPortType(idPT);
			
			Operation operation = null;
			for (Operation opCheck : portType.getAzioneList()) {
				if(opCheck.getNome().equals(busta.getAzione())){
					operation = opCheck;
					break;
				}
			}
			if(operation==null){
				throw new ProtocolException("Azione ["+busta.getAzione()+"] non trovata");
			}
			
			
			// **** Prepare *****
			
			Map<String,String> mapIdPartInfoToIdAttach = new Hashtable<>();
			
			Messaging ebmsV3_0Messagging = this.buildEbmsV3_0Messagging(ruoloMessaggio,busta,
					soggettoMittente, soggettoDestinatario, portType, operation);
			
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
				
				mapAS4InfoFromSoapMessage(as4Message, payloadInfo, sendRequest, mapIdPartInfoToIdAttach);

			}
			else{
				
				OpenSPCoop2RestMessage<?> restMessage = msg.castAsRest();
				as4Message = OpenSPCoop2MessageFactory.getMessageFactory().createEmptyMessage(MessageType.SOAP_12, messageRole).castAsSoap(); // viene creato ad hoc
				msg.copyResourcesTo(as4Message);
				
				mapAS4InfoFromRestMessage(as4Message, restMessage, payloadInfo, sendRequest, mapIdPartInfoToIdAttach);
				
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
			Soggetto soggettoMittente, Soggetto soggettoDestinatario, PortType portType, Operation operation) throws RegistryNotFound, ProtocolException, DriverRegistroServiziException{
		
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
				AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_BASE, true));
		partyIdFrom.setType(AS4PropertiesUtils.getStringValue(soggettoMittente.getProtocolPropertyList(), 
				AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_TYPE_VALUE, false));
		from.addPartyId(partyIdFrom);
		if(RuoloMessaggio.RICHIESTA.equals(ruoloMessaggio)){
			from.setRole(AS4Costanti.AS4_USER_MESSAGE_FROM_ROLE_INITIATOR);
		}
		else{
			from.setRole(AS4Costanti.AS4_USER_MESSAGE_FROM_ROLE_RESPONDER);
		}
		partyInfo.setFrom(from);
		
		// PartyInfo (To)

		To destinatario = new To();
		PartyId partyIdTo = new PartyId();
		partyIdTo.setBase(AS4PropertiesUtils.getRequiredStringValue(soggettoDestinatario.getProtocolPropertyList(), 
				AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_BASE, true));
		partyIdTo.setType(AS4PropertiesUtils.getStringValue(soggettoDestinatario.getProtocolPropertyList(), 
				AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_TYPE_VALUE, false));
		destinatario.addPartyId(partyIdTo);
		if(RuoloMessaggio.RICHIESTA.equals(ruoloMessaggio)){
			destinatario.setRole(AS4Costanti.AS4_USER_MESSAGE_FROM_ROLE_RESPONDER);
		}
		else{
			destinatario.setRole(AS4Costanti.AS4_USER_MESSAGE_FROM_ROLE_INITIATOR);
		}
		partyInfo.setTo(destinatario);
		
		// CollaborationInfo

		CollaborationInfo collaborationInfo = new CollaborationInfo();
		userMessage.setCollaborationInfo(collaborationInfo);
		
		// CollaborationInfo (Service)
		
		Service service = new Service();
		service.setBase(AS4PropertiesUtils.getRequiredStringValue(portType.getProtocolPropertyList(), 
				AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_SERVICE_BASE, true));
		service.setType(AS4PropertiesUtils.getRequiredStringValue(portType.getProtocolPropertyList(), 
				AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_SERVICE_TYPE, true));
		collaborationInfo.setService(service);
		
		// CollaborationInfo (Action)
		
		collaborationInfo.setAction(AS4PropertiesUtils.getRequiredStringValue(operation.getProtocolPropertyList(), 
				AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_ACTION, true));
		
		// MessageProperties
		
		// TODO DINAMICHE RISPETTO AD UN FILE CHE DEFINISCE COME ESTRARLE
		MessageProperties messageProperties = new MessageProperties();
		userMessage.setMessageProperties(messageProperties);
		
		Property propertyOriginalSender = new Property();
		propertyOriginalSender.setName("originalSender");
		propertyOriginalSender.setBase("urn:oasis:names:tc:ebcore:partyid-type:unregistered:C1");
		messageProperties.addProperty(propertyOriginalSender);
		
		Property propertyFinalRecipient = new Property();
		propertyFinalRecipient.setName("finalRecipient");
		propertyFinalRecipient.setBase("urn:oasis:names:tc:ebcore:partyid-type:unregistered:C4");
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
			Map<String,String> mapIdPartInfoToIdAttach) throws Exception{
	
		
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
				
				String partInfoCid = "cid:attach"+(index++);
				String cid = "cid:"+contentID;
				mapIdPartInfoToIdAttach.put(partInfoCid, cid);
				
				String contentType = p.getContentType();
				if(contentType==null){
					throw new ProtocolException("Attachment without ContentType");
				}
				
				PartInfo pInfo = new PartInfo();
				pInfo.setHref(partInfoCid);
				PartProperties partProperties = new PartProperties();
				Property property = new Property();
				property.setName(AS4Costanti.AS4_USER_MESSAGE_PAYLOAD_INFO_PROPERTIES_MIME_TYPE);
				property.setBase(contentType);
				partProperties.addProperty(property);
				pInfo.setPartProperties(partProperties);
				_listPartInfoUserMessage.add(pInfo);
				
				PayloadType pBodyInfo = new PayloadType();
				pBodyInfo.setPayloadId(partInfoCid);
				pBodyInfo.setContentType(contentType);
				_listPayload.add(pBodyInfo);
			}
		}
		
		if(_listPartInfoUserMessage!=null && _listPartInfoUserMessage.size()>0){
			payloadInfo.getPartInfoList().addAll(_listPartInfoUserMessage);
			sendRequest.getPayloadList().addAll(_listPayload);
		}
		
		
		// Body
		
		PartInfo _PartInfoUserMessageBody = null;
		PayloadType _PayloadBody = null;
		byte [] body = TunnelSoapUtils.sbustamentoSOAPEnvelope(soapMessage.getSOAPPart().getEnvelope());
		if(body!=null && body.length>0){
			// esiste un contenuto nel body
			AttachmentPart ap = null;
			boolean bodyWithMultiRootElement = false;
			List<Node> listNode = SoapUtils.getNotEmptyChildNodes(soapMessage.getSOAPPart().getEnvelope().getBody(), false);
			if(listNode!=null && listNode.size()>1){
				bodyWithMultiRootElement = true;
			}
			String contentType = null;
			if(bodyWithMultiRootElement){
				contentType = HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM;
				InputStreamDataSource isSource = new InputStreamDataSource("SoapBodyContent", contentType, body);
				ap = soapMessage.createAttachmentPart(new DataHandler(isSource));
			}else{
				Source streamSource = new DOMSource(XMLUtils.getInstance().newElement(body));
				ap = soapMessage.createAttachmentPart();
				contentType = HttpConstants.CONTENT_TYPE_TEXT_XML;
				ap.setContent(streamSource, contentType);
			}
			String contentID = soapMessage.createContentID(AS4Costanti.AS4_NAMESPACE_CID_MESSAGGIO);
			if(contentID.startsWith("<")){
				contentID = contentID.substring(1);
			}
			if(contentID.endsWith(">")){
				contentID = contentID.substring(0,contentID.length()-1);
			}
			
			String partInfoCid = "cid:message";
			String cid = "cid:"+contentID;
			mapIdPartInfoToIdAttach.put(partInfoCid, cid);
			
			ap.setContentId(contentID);
			soapMessage.addAttachmentPart(ap);
			
			PartInfo pInfo = new PartInfo();
			pInfo.setHref(partInfoCid);
			PartProperties partProperties = new PartProperties();
			Property property = new Property();
			property.setName(AS4Costanti.AS4_USER_MESSAGE_PAYLOAD_INFO_PROPERTIES_MIME_TYPE);
			property.setBase(contentType);
			partProperties.addProperty(property);
			pInfo.setPartProperties(partProperties);
			_PartInfoUserMessageBody = pInfo;
			
			PayloadType pBodyInfo = new PayloadType();
			pBodyInfo.setPayloadId(partInfoCid);
			pBodyInfo.setContentType(contentType);
			_PayloadBody = pBodyInfo;
		}
		
		if(_PartInfoUserMessageBody!=null){
			payloadInfo.addPartInfo(_PartInfoUserMessageBody);
			sendRequest.addPayload(_PayloadBody);
		}
	}
	

	private void mapAS4InfoFromRestMessage(OpenSPCoop2SoapMessage as4Message,
			OpenSPCoop2RestMessage<?> restMessage,PayloadInfo payloadInfo,SendRequest sendRequest,
			Map<String,String> mapIdPartInfoToIdAttach) throws Exception{
		
		if(restMessage.hasContent()==false){
			throw new Exception("Messaggio non contiene dati da inoltrare");
		}
		
		if(MessageType.MIME_MULTIPART.equals(restMessage.getMessageType())){
					
			OpenSPCoop2RestMimeMultipartMessage msgMime = restMessage.castAsRestMimeMultipart();
			int index = 1;
			for (int i = 0; i < msgMime.getContent().countBodyParts(); i++) {
				BodyPart bodyPart = msgMime.getContent().getBodyPart(i);
				
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
				
				String partInfoCid = "cid:attach"+(index++);
				String cid = "cid:"+contentID;
				mapIdPartInfoToIdAttach.put(partInfoCid, cid);
				
				String contentType = bodyPart.getContentType();
				if(contentType==null){
					throw new ProtocolException("BodyPart without ContentType");
				}
				
				AttachmentPart ap = as4Message.createAttachmentPart(bodyPart.getDataHandler());
				ap.setContentId(contentID);
				as4Message.addAttachmentPart(ap);
				
				PartInfo pInfo = new PartInfo();
				pInfo.setHref(partInfoCid);
				PartProperties partProperties = new PartProperties();
				Property property = new Property();
				property.setName(AS4Costanti.AS4_USER_MESSAGE_PAYLOAD_INFO_PROPERTIES_MIME_TYPE);
				property.setBase(contentType);
				partProperties.addProperty(property);
				pInfo.setPartProperties(partProperties);
				payloadInfo.addPartInfo(pInfo);
				
				PayloadType pBodyInfo = new PayloadType();
				pBodyInfo.setPayloadId(partInfoCid);
				pBodyInfo.setContentType(contentType);
				sendRequest.addPayload(pBodyInfo);
				
			}
			
		}
		else{
		
			// esiste un contenuto nel body
			AttachmentPart ap = null;

			String contentType = restMessage.getContentType();
			if(MessageType.XML.equals(restMessage.getMessageType())){
				// solo text/xml può essere costruito con DOMSource
				String baseType = ContentTypeUtilities.readBaseTypeFromContentType(contentType);
				if(HttpConstants.CONTENT_TYPE_TEXT_XML.equals(baseType)){
					Source streamSource = new DOMSource(restMessage.castAsRestXml().getContent());
					ap = as4Message.createAttachmentPart();
					ap.setContent(streamSource, contentType);
				}
				else{
					byte [] content = restMessage.getAsByte(restMessage.castAsRestXml().getContent(), true);
					InputStreamDataSource isSource = new InputStreamDataSource("RestXml", contentType, content);
					ap = as4Message.createAttachmentPart(new DataHandler(isSource));
				}
			}
			else if(MessageType.JSON.equals(restMessage.getMessageType())){
				ap = as4Message.createAttachmentPart();
				ap.setContent(restMessage.castAsRestJson().getContent(), contentType);
			}
			else {
				InputStreamDataSource isSource = new InputStreamDataSource("RestBinary", contentType, restMessage.castAsRestBinary().getContent());
				ap = as4Message.createAttachmentPart(new DataHandler(isSource));
			}

			String contentID = as4Message.createContentID(AS4Costanti.AS4_NAMESPACE_CID_MESSAGGIO);
			if(contentID.startsWith("<")){
				contentID = contentID.substring(1);
			}
			if(contentID.endsWith(">")){
				contentID = contentID.substring(0,contentID.length()-1);
			}
			String partInfoCid = "cid:message";
			String cid = "cid:"+contentID;
			mapIdPartInfoToIdAttach.put(partInfoCid, cid);
			
			ap.setContentId(contentID);
			as4Message.addAttachmentPart(ap);
				
			PartInfo pInfo = new PartInfo();
			pInfo.setHref(partInfoCid);
			PartProperties partProperties = new PartProperties();
			Property property = new Property();
			property.setName(AS4Costanti.AS4_USER_MESSAGE_PAYLOAD_INFO_PROPERTIES_MIME_TYPE);
			property.setBase(contentType);
			partProperties.addProperty(property);
			pInfo.setPartProperties(partProperties);
			payloadInfo.addPartInfo(pInfo);
				
			PayloadType pBodyInfo = new PayloadType();
			pBodyInfo.setPayloadId(partInfoCid);
			pBodyInfo.setContentType(contentType);
			sendRequest.addPayload(pBodyInfo);

		}
	}
}
