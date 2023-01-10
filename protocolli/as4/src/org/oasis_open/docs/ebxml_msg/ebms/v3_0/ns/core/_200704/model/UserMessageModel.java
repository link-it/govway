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
package org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model;

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model UserMessage 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UserMessageModel extends AbstractModel<UserMessage> {

	public UserMessageModel(){
	
		super();
	
		this.MESSAGE_INFO = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.MessageInfoModel(new Field("MessageInfo",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo.class,"UserMessage",UserMessage.class));
		this.PARTY_INFO = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.PartyInfoModel(new Field("PartyInfo",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo.class,"UserMessage",UserMessage.class));
		this.COLLABORATION_INFO = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.CollaborationInfoModel(new Field("CollaborationInfo",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo.class,"UserMessage",UserMessage.class));
		this.MESSAGE_PROPERTIES = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.MessagePropertiesModel(new Field("MessageProperties",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties.class,"UserMessage",UserMessage.class));
		this.PAYLOAD_INFO = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.PayloadInfoModel(new Field("PayloadInfo",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo.class,"UserMessage",UserMessage.class));
		this.MPC = new Field("mpc",java.net.URI.class,"UserMessage",UserMessage.class);
	
	}
	
	public UserMessageModel(IField father){
	
		super(father);
	
		this.MESSAGE_INFO = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.MessageInfoModel(new ComplexField(father,"MessageInfo",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo.class,"UserMessage",UserMessage.class));
		this.PARTY_INFO = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.PartyInfoModel(new ComplexField(father,"PartyInfo",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo.class,"UserMessage",UserMessage.class));
		this.COLLABORATION_INFO = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.CollaborationInfoModel(new ComplexField(father,"CollaborationInfo",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo.class,"UserMessage",UserMessage.class));
		this.MESSAGE_PROPERTIES = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.MessagePropertiesModel(new ComplexField(father,"MessageProperties",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties.class,"UserMessage",UserMessage.class));
		this.PAYLOAD_INFO = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.PayloadInfoModel(new ComplexField(father,"PayloadInfo",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo.class,"UserMessage",UserMessage.class));
		this.MPC = new ComplexField(father,"mpc",java.net.URI.class,"UserMessage",UserMessage.class);
	
	}
	
	

	public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.MessageInfoModel MESSAGE_INFO = null;
	 
	public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.PartyInfoModel PARTY_INFO = null;
	 
	public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.CollaborationInfoModel COLLABORATION_INFO = null;
	 
	public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.MessagePropertiesModel MESSAGE_PROPERTIES = null;
	 
	public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.PayloadInfoModel PAYLOAD_INFO = null;
	 
	public IField MPC = null;
	 

	@Override
	public Class<UserMessage> getModeledClass(){
		return UserMessage.class;
	}
	
	@Override
	public String toString(){
		if(this.getModeledClass()!=null){
			return this.getModeledClass().getName();
		}else{
			return "N.D.";
		}
	}

}