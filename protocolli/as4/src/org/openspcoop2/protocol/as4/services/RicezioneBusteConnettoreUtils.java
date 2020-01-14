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

package org.openspcoop2.protocol.as4.services;

import java.util.Enumeration;
import java.util.HashMap;

import javax.jms.MapMessage;

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import org.openspcoop2.protocol.as4.constants.AS4Costanti;
import org.openspcoop2.utils.threads.RunnableLogger;

/**
 * RicezioneBusteConnettoreUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RicezioneBusteConnettoreUtils extends BaseConnettoreUtils {

	public RicezioneBusteConnettoreUtils(RunnableLogger log) {
		super(log);
	}
	
	
	public void fillUserMessage(MapMessage map, UserMessage userMessage,HashMap<String, byte[]> content) throws Exception {
		// PartyInfo
		
		PartyInfo partyInfo = new PartyInfo();
		userMessage.setPartyInfo(partyInfo);
		
		// PartyInfo (From)

		From from = new From();
		PartyId partyIdFrom = new PartyId();
		partyIdFrom.setBase(this.getPropertyJms(map, AS4Costanti.JMS_FROM_PARTY_ID, true));
		partyIdFrom.setType(this.getPropertyJms(map, AS4Costanti.JMS_FROM_PARTY_TYPE, true));
		from.addPartyId(partyIdFrom);
		from.setRole(this.getPropertyJms(map, AS4Costanti.JMS_FROM_ROLE, true));
		partyInfo.setFrom(from);
		
		// PartyInfo (To)

		To destinatario = new To();
		PartyId partyIdTo = new PartyId();
		partyIdTo.setBase(this.getPropertyJms(map, AS4Costanti.JMS_TO_PARTY_ID, true));
		partyIdTo.setType(this.getPropertyJms(map, AS4Costanti.JMS_TO_PARTY_TYPE, true));
		destinatario.addPartyId(partyIdTo);
		destinatario.setRole(this.getPropertyJms(map, AS4Costanti.JMS_TO_ROLE, true));
		partyInfo.setTo(destinatario);
						
		// CollaborationInfo

		CollaborationInfo collaborationInfo = new CollaborationInfo();
		userMessage.setCollaborationInfo(collaborationInfo);
		
		// CollaborationInfo (Service)
		
		Service service = new Service();
		service.setBase(this.getPropertyJms(map, AS4Costanti.JMS_SERVICE, true));
		service.setType(this.getPropertyJms(map, AS4Costanti.JMS_SERVICE_TYPE, false));
		collaborationInfo.setService(service);
		
		// CollaborationInfo (Action)
		
		collaborationInfo.setAction(this.getPropertyJms(map, AS4Costanti.JMS_ACTION, true));
		
		// CollaborationInfo (Conversation)
		
		collaborationInfo.setConversationId(this.getPropertyJms(map, AS4Costanti.JMS_CONVERSATION_ID, true));
		
		// MessageInfo
		
		MessageInfo messageInfo = new MessageInfo();
		messageInfo.setMessageId(this.getPropertyJms(map, AS4Costanti.JMS_MESSAGE_ID, true));
		messageInfo.setRefToMessageId(this.getPropertyJms(map, AS4Costanti.JMS_REF_TO_MESSAGE_ID, false));
		userMessage.setMessageInfo(messageInfo);
		
		// PayloadInfo
		
		PayloadInfo payloadInfo = new PayloadInfo();
		int payloadNumber = this.getIntPropertyJms(map, AS4Costanti.JMS_PAYLOADS_NUMBER, true);
		for (int i = 0; i < payloadNumber; i++) {
			int index = i+1;
			PartInfo partInfo = new PartInfo();
			
			String contentIdKey = AS4Costanti.JMS_PAYLOAD_PREFIX+index+AS4Costanti.JMS_PAYLOAD_MIME_CONTENT_ID_SUFFIX;
			String hrefCid = this.getPropertyJms(map, contentIdKey, true);
			if(hrefCid.startsWith("cid:")==false) {
				hrefCid = "cid:" + hrefCid;
			}
			partInfo.setHref(hrefCid);
			
			PartProperties partProperties = new PartProperties();
			Property property = new Property();
			property.setName(AS4Costanti.AS4_USER_MESSAGE_PAYLOAD_INFO_PROPERTIES_MIME_TYPE);
			String contentTypeKey = AS4Costanti.JMS_PAYLOAD_PREFIX+index+AS4Costanti.JMS_PAYLOAD_MIME_TYPE_SUFFIX;
			property.setBase(this.getPropertyJms(map, contentTypeKey, true));
			partProperties.addProperty(property);
			partInfo.setPartProperties(partProperties);
			
			String contentPayloadKey = AS4Costanti.JMS_PAYLOAD_PREFIX+index;
			content.put(partInfo.getHref(),map.getBytes(contentPayloadKey));
			
			payloadInfo.addPartInfo(partInfo);
		}
		userMessage.setPayloadInfo(payloadInfo);
		
		// Metto dentro properties tutte le restanti proprieta'
		// Se valorizzata viene compresa anche AS4Costanti.JMS_AGREEMENT_REF
		// escludo JMS_PROTOCOL (valorizzata come 'AS4' e JMS_MESSAGE_TYPE valorizzata come 'incomingMessage')
		
		MessageProperties messageProperties = new MessageProperties();
		
		Enumeration<?> en = map.getPropertyNames();
		while (en.hasMoreElements()) {
			Object name = (Object) en.nextElement();
			if(name instanceof String) {
				String key = (String) name;
				Object value = map.getObjectProperty(key);
				if(value!=null && value instanceof String) {
					if( !AS4Costanti.JMS_FROM_PARTY_ID.equals(key) &&
							!AS4Costanti.JMS_FROM_PARTY_TYPE.equals(key) &&
							!AS4Costanti.JMS_FROM_ROLE.equals(key) &&
							!AS4Costanti.JMS_TO_PARTY_ID.equals(key) &&
							!AS4Costanti.JMS_TO_PARTY_TYPE.equals(key) &&
							!AS4Costanti.JMS_TO_ROLE.equals(key) &&
							!AS4Costanti.JMS_SERVICE.equals(key) &&
							!AS4Costanti.JMS_SERVICE_TYPE.equals(key) &&
							!AS4Costanti.JMS_ACTION.equals(key) &&
							!AS4Costanti.JMS_CONVERSATION_ID.equals(key) &&
							!AS4Costanti.JMS_MESSAGE_ID.equals(key) &&
							!AS4Costanti.JMS_REF_TO_MESSAGE_ID.equals(key) &&
							!AS4Costanti.JMS_PAYLOADS_NUMBER.equals(key) &&
							!key.startsWith(AS4Costanti.JMS_PAYLOAD_PREFIX) &&
							!AS4Costanti.JMS_PROTOCOL.equals(key)&&
							!AS4Costanti.JMS_MESSAGE_TYPE.equals(key)) {
						Property propertyOriginalSender = new Property();
						propertyOriginalSender.setName(key);
						propertyOriginalSender.setBase((String)value);
						messageProperties.addProperty(propertyOriginalSender);
					}
				}
			}
		}
		
		userMessage.setMessageProperties(messageProperties);
		
	}
}
