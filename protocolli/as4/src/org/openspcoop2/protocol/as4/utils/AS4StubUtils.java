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

package org.openspcoop2.protocol.as4.utils;

import java.net.URI;
import java.net.URISyntaxException;

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef;
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
import org.openspcoop2.protocol.as4.stub.backend_ecodex.v1_1.Messaging;

/**
 * AS4StubUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AS4StubUtils {

	public static UserMessage convertTo(Messaging msg) throws URISyntaxException{
		UserMessage user = new UserMessage();
		
		if(msg.getUserMessage().getPartyInfo()!=null){
			PartyInfo partyInfo = new PartyInfo();
			if(msg.getUserMessage().getPartyInfo().getFrom()!=null){
				From from = new From();
				from.setRole(msg.getUserMessage().getPartyInfo().getFrom().getRole());
				if(msg.getUserMessage().getPartyInfo().getFrom().getPartyId()!=null){
					PartyId partyId = new PartyId();
					partyId.setBase(msg.getUserMessage().getPartyInfo().getFrom().getPartyId().getValue());
					partyId.setType(msg.getUserMessage().getPartyInfo().getFrom().getPartyId().getType());
					from.addPartyId(partyId);
				}
				partyInfo.setFrom(from);
			}
			if(msg.getUserMessage().getPartyInfo().getTo()!=null){
				To to = new To();
				to.setRole(msg.getUserMessage().getPartyInfo().getTo().getRole());
				if(msg.getUserMessage().getPartyInfo().getTo().getPartyId()!=null){
					PartyId partyId = new PartyId();
					partyId.setBase(msg.getUserMessage().getPartyInfo().getTo().getPartyId().getValue());
					partyId.setType(msg.getUserMessage().getPartyInfo().getTo().getPartyId().getType());
					to.addPartyId(partyId);
				}
				partyInfo.setTo(to);
			}
			user.setPartyInfo(partyInfo);
		}
		
		if(msg.getUserMessage().getCollaborationInfo()!=null){
			CollaborationInfo collaborationInfo = new CollaborationInfo();
			if(msg.getUserMessage().getCollaborationInfo().getService()!=null){
				Service service = new Service();
				service.setBase(msg.getUserMessage().getCollaborationInfo().getService().getValue());
				service.setType(msg.getUserMessage().getCollaborationInfo().getService().getType());
				collaborationInfo.setService(service);
			}
			collaborationInfo.setAction(msg.getUserMessage().getCollaborationInfo().getAction());
			collaborationInfo.setConversationId(msg.getUserMessage().getCollaborationInfo().getConversationId());
			if(msg.getUserMessage().getCollaborationInfo().getAgreementRef()!=null){
				AgreementRef agreementRef = new AgreementRef();
				agreementRef.setBase(msg.getUserMessage().getCollaborationInfo().getAgreementRef().getValue());
				agreementRef.setType(msg.getUserMessage().getCollaborationInfo().getAgreementRef().getType());
				agreementRef.setPmode(msg.getUserMessage().getCollaborationInfo().getAgreementRef().getPmode());
				collaborationInfo.setAgreementRef(agreementRef);
			}
			user.setCollaborationInfo(collaborationInfo);
		}
		
		if(msg.getUserMessage().getMpc()!=null){
			user.setMpc(new URI(msg.getUserMessage().getMpc()));
		}
		
		if(msg.getUserMessage().getMessageInfo()!=null){
			MessageInfo messageInfo = new MessageInfo();
			messageInfo.setMessageId(msg.getUserMessage().getMessageInfo().getMessageId());
			messageInfo.setRefToMessageId(msg.getUserMessage().getMessageInfo().getRefToMessageId());
			if(msg.getUserMessage().getMessageInfo().getTimestamp()!=null){
				messageInfo.setTimestamp(msg.getUserMessage().getMessageInfo().getTimestamp().toGregorianCalendar().getTime());
			}
			user.setMessageInfo(messageInfo);
		}
		
		if(msg.getUserMessage().getMessageProperties()!=null && msg.getUserMessage().getMessageProperties().getProperty()!=null &&
				msg.getUserMessage().getMessageProperties().getProperty().size()>0){
			MessageProperties messageProperties = new MessageProperties();
			for (int i = 0; i < msg.getUserMessage().getMessageProperties().getProperty().size(); i++) {
				Property property = new Property();
				property.setBase(msg.getUserMessage().getMessageProperties().getProperty().get(i).getValue());
				property.setName(msg.getUserMessage().getMessageProperties().getProperty().get(i).getName());
				messageProperties.addProperty(property);	
			}
			user.setMessageProperties(messageProperties);
		}
		
		if(msg.getUserMessage().getPayloadInfo()!=null && msg.getUserMessage().getPayloadInfo().getPartInfo()!=null &&
				msg.getUserMessage().getPayloadInfo().getPartInfo().size()>0){
			PayloadInfo payloadInfo = new PayloadInfo();
			for (int i = 0; i < msg.getUserMessage().getPayloadInfo().getPartInfo().size(); i++) {
				PartInfo partInfo = new PartInfo();
				partInfo.setHref(msg.getUserMessage().getPayloadInfo().getPartInfo().get(i).getHref());
				if(msg.getUserMessage().getPayloadInfo().getPartInfo().get(i).getPartProperties()!=null && 
						msg.getUserMessage().getPayloadInfo().getPartInfo().get(i).getPartProperties().getProperty()!=null && 
						msg.getUserMessage().getPayloadInfo().getPartInfo().get(i).getPartProperties().getProperty().size()>0){
					PartProperties partProperties = new PartProperties();
					for (int j = 0; j < msg.getUserMessage().getPayloadInfo().getPartInfo().get(i).getPartProperties().getProperty().size(); j++) {
						Property property = new Property();
						property.setBase(msg.getUserMessage().getPayloadInfo().getPartInfo().get(i).getPartProperties().getProperty().get(j).getValue());
						property.setName(msg.getUserMessage().getPayloadInfo().getPartInfo().get(i).getPartProperties().getProperty().get(j).getName());
						partProperties.addProperty(property);
					}
					partInfo.setPartProperties(partProperties);
				}
				payloadInfo.addPartInfo(partInfo);
			}
			user.setPayloadInfo(payloadInfo);
		}
		
		return user;
			
	}
	
}
