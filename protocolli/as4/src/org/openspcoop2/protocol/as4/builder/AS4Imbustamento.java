package org.openspcoop2.protocol.as4.builder;

import java.util.ArrayList;
import java.util.List;

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
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Soggetto;
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
import org.openspcoop2.message.soap.mtom.MTOMUtilities;
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
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.w3c.dom.Node;

import backend.ecodex.org._1_1.PayloadType;
import backend.ecodex.org._1_1.SendRequest;

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
			
			
			// **** Prepare *****
			
			Messaging ebmsV3_0Messagging = this.buildEbmsV3_0Messagging(ruoloMessaggio,busta,registryReader);
			
			// PayloadInfo
			PayloadInfo payloadInfo = new PayloadInfo();
			ebmsV3_0Messagging.getUserMessage(0).setPayloadInfo(payloadInfo);
			
			// Send Request Body
			SendRequest sendRequest = new SendRequest();
						
			if(ServiceBinding.SOAP.equals(msg.getServiceBinding())){
				
				OpenSPCoop2SoapMessage soapMessage = msg.castAsSoap();
				as4Message = soapMessage; // viene trasformato il messaggio ricevuto per avere benefici di performance
				
				mapAS4InfoFromSoapMessage(soapMessage, payloadInfo, sendRequest);

			}
			else{
				
				OpenSPCoop2RestMessage<?> restMessage = msg.castAsRest();
				as4Message = OpenSPCoop2MessageFactory.getMessageFactory().createEmptyMessage(MessageType.SOAP_12, messageRole).castAsSoap(); // viene creato ad hoc
				msg.copyResourcesTo(as4Message);
				
				mapAS4InfoFromRestMessage(as4Message, restMessage, payloadInfo, sendRequest);
				
			}
			
			
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
			as4Message.getSOAPBody().removeContents();
			as4Message.getSOAPBody().addChildElement(soapElementAs4Body);
			
			protocolMessage.setBustaRawContent(new AS4RawContent(as4Message.getSOAPPart().getEnvelope()));
			protocolMessage.setMessage(as4Message);
			return protocolMessage;
		
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
		
	}
	
	private Messaging buildEbmsV3_0Messagging(RuoloMessaggio ruoloMessaggio, Busta busta, IRegistryReader registryReader) throws RegistryNotFound, ProtocolException{
		
		Messaging ebmsV3_0Messagging = new Messaging();
		
		UserMessage userMessage = new UserMessage();
		ebmsV3_0Messagging.addUserMessage(userMessage);
		
		// PartyInfo
		
		PartyInfo partyInfo = new PartyInfo();
		userMessage.setPartyInfo(partyInfo);
		
		From from = new From();
		Soggetto soggettoMittente = registryReader.getSoggetto(new IDSoggetto(busta.getTipoMittente(),busta.getMittente()));
		PartyId partyIdFrom = new PartyId();
		partyIdFrom.setBase(AS4PropertiesUtils.getRequiredStringValue(soggettoMittente.getProtocolPropertyList(), 
				AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_FROM_PARTY_ID_BASE, true));
		partyIdFrom.setType(AS4PropertiesUtils.getStringValue(soggettoMittente.getProtocolPropertyList(), 
				AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_FROM_PARTY_ID_TYPE, false));
		from.addPartyId(partyIdFrom);
		if(RuoloMessaggio.RICHIESTA.equals(ruoloMessaggio)){
			from.setRole(AS4Costanti.AS4_USER_MESSAGE_FROM_ROLE_INITIATOR);
		}
		else{
			from.setRole(AS4Costanti.AS4_USER_MESSAGE_FROM_ROLE_RESPONDER);
		}
		partyInfo.setFrom(from);
		
		To destinatario = new To();
		Soggetto soggettoDestinatario = registryReader.getSoggetto(new IDSoggetto(busta.getTipoDestinatario(),busta.getDestinatario()));
		PartyId partyIdTo = new PartyId();
		partyIdTo.setBase(AS4PropertiesUtils.getRequiredStringValue(soggettoDestinatario.getProtocolPropertyList(), 
				AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_TO_PARTY_ID_BASE, true));
		partyIdTo.setType(AS4PropertiesUtils.getStringValue(soggettoDestinatario.getProtocolPropertyList(), 
				AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_TO_PARTY_ID_TYPE, false));
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
		
		Service service = new Service();
		service.setBase(AS4PropertiesUtils.getRequiredStringValue(soggettoDestinatario.getProtocolPropertyList(), 
				AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_SERVICE_BASE, true));
		service.setType(AS4PropertiesUtils.getRequiredStringValue(soggettoDestinatario.getProtocolPropertyList(), 
				AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_SERVICE_TYPE, true));
		collaborationInfo.setService(service);
		
		collaborationInfo.setAction(AS4PropertiesUtils.getRequiredStringValue(soggettoDestinatario.getProtocolPropertyList(), 
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
	
	
	private void mapAS4InfoFromSoapMessage(OpenSPCoop2SoapMessage soapMessage,PayloadInfo payloadInfo,SendRequest sendRequest) throws Exception{
		List<PartInfo> _listPartInfoUserMessage = null; 
		List<PayloadType> _listPayload = null;
		boolean isMtom = MTOMUtilities.isMtom(soapMessage.getContentType());
		
		if(soapMessage.countAttachments()>0){
			
			_listPartInfoUserMessage = new ArrayList<PartInfo>();
			_listPayload = new ArrayList<PayloadType>();
			
			// Attachments
			java.util.Iterator<?> iter = soapMessage.getAttachments();
			while( iter.hasNext() ){
				AttachmentPart p = (AttachmentPart) iter.next();
				
				String contentID = p.getContentId();
				if(contentID==null){
					throw new ProtocolException("Attachment without ContentID");
				}
				String cid = "cid:"+contentID;
				
				String contentType = p.getContentType();
				if(contentType==null){
					throw new ProtocolException("Attachment without ContentType");
				}
				
				PartInfo pInfo = new PartInfo();
				pInfo.setHref(cid);
				PartProperties partProperties = new PartProperties();
				Property property = new Property();
				property.setName(AS4Costanti.AS4_USER_MESSAGE_PAYLOAD_INFO_PROPERTIES_MIME_TYPE);
				property.setBase(contentType);
				partProperties.addProperty(property);
				pInfo.setPartProperties(partProperties);
				_listPartInfoUserMessage.add(pInfo);
				
				PayloadType pBodyInfo = new PayloadType();
				pBodyInfo.setPayloadId(cid);
				if(isMtom){
					pBodyInfo.setBase(("<inc:Include href=\""+cid+"\" xmlns:inc=\"http://www.w3.org/2004/08/xop/include\"/>").getBytes());
				}
				else{
					pBodyInfo.setBase(cid.getBytes());
				}
				_listPayload.add(pBodyInfo);
			}
		}
		
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
			String cid = "cid:"+contentID;
			ap.setContentId(contentID);
			soapMessage.addAttachmentPart(ap);
			
			PartInfo pInfo = new PartInfo();
			pInfo.setHref(cid);
			PartProperties partProperties = new PartProperties();
			Property property = new Property();
			property.setName(AS4Costanti.AS4_USER_MESSAGE_PAYLOAD_INFO_PROPERTIES_MIME_TYPE);
			property.setBase(contentType);
			partProperties.addProperty(property);
			pInfo.setPartProperties(partProperties);
			_PartInfoUserMessageBody = pInfo;
			
			PayloadType pBodyInfo = new PayloadType();
			pBodyInfo.setPayloadId(cid);
			if(isMtom){
				pBodyInfo.setBase(("<inc:Include href=\""+cid+"\" xmlns:inc=\"http://www.w3.org/2004/08/xop/include\"/>").getBytes());
			}
			else{
				pBodyInfo.setBase(cid.getBytes());
			}
			_PayloadBody = pBodyInfo;
		}


		if(_PartInfoUserMessageBody!=null){
			payloadInfo.addPartInfo(_PartInfoUserMessageBody);
			sendRequest.addPayload(_PayloadBody);
		}
		if(_listPartInfoUserMessage!=null && _listPartInfoUserMessage.size()>0){
			payloadInfo.getPartInfoList().addAll(_listPartInfoUserMessage);
			sendRequest.getPayloadList().addAll(_listPayload);
		}
	}
	

	private void mapAS4InfoFromRestMessage(OpenSPCoop2SoapMessage as4Message,
			OpenSPCoop2RestMessage<?> restMessage,PayloadInfo payloadInfo,SendRequest sendRequest) throws Exception{
		
		if(restMessage.hasContent()==false){
			throw new Exception("Messaggio non contiene dati da inoltrare");
		}
		
		if(MessageType.MIME_MULTIPART.equals(restMessage.getMessageType())){
					
			OpenSPCoop2RestMimeMultipartMessage msgMime = restMessage.castAsRestMimeMultipart();
			for (int i = 0; i < msgMime.getContent().countBodyParts(); i++) {
				BodyPart bodyPart = msgMime.getContent().getBodyPart(i);
				
				String contentID = msgMime.getContent().getContentID(bodyPart);
				if(contentID==null){
					throw new ProtocolException("BodyPart without ContentID");
				}
				String cid = "cid:"+contentID;
				
				String contentType = bodyPart.getContentType();
				if(contentType==null){
					throw new ProtocolException("BodyPart without ContentType");
				}
				
				PartInfo pInfo = new PartInfo();
				pInfo.setHref(cid);
				PartProperties partProperties = new PartProperties();
				Property property = new Property();
				property.setName(AS4Costanti.AS4_USER_MESSAGE_PAYLOAD_INFO_PROPERTIES_MIME_TYPE);
				property.setBase(contentType);
				partProperties.addProperty(property);
				pInfo.setPartProperties(partProperties);
				payloadInfo.addPartInfo(pInfo);
				
				PayloadType pBodyInfo = new PayloadType();
				pBodyInfo.setPayloadId(cid);
				pBodyInfo.setBase(cid.getBytes());
				sendRequest.addPayload(pBodyInfo);
			}
			
		}
		else{
		
			// esiste un contenuto nel body
			AttachmentPart ap = null;

			String contentType = restMessage.getContentType();
			if(MessageType.XML.equals(restMessage.getMessageType())){
				Source streamSource = new DOMSource(restMessage.castAsRestXml().getContent());
				ap = as4Message.createAttachmentPart();
				ap.setContent(streamSource, contentType);
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
			String cid = "cid:"+contentID;
			ap.setContentId(contentID);
			as4Message.addAttachmentPart(ap);
				
			PartInfo pInfo = new PartInfo();
			pInfo.setHref(cid);
			PartProperties partProperties = new PartProperties();
			Property property = new Property();
			property.setName(AS4Costanti.AS4_USER_MESSAGE_PAYLOAD_INFO_PROPERTIES_MIME_TYPE);
			property.setBase(contentType);
			partProperties.addProperty(property);
			pInfo.setPartProperties(partProperties);
			payloadInfo.addPartInfo(pInfo);
				
			PayloadType pBodyInfo = new PayloadType();
			pBodyInfo.setPayloadId(cid);
			pBodyInfo.setBase(cid.getBytes());
			sendRequest.addPayload(pBodyInfo);

		}
	}
}
