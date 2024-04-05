/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Messaging 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessagingModel extends AbstractModel<Messaging> {

	public MessagingModel(){
	
		super();
	
		this.SIGNAL_MESSAGE = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.SignalMessageModel(new Field("SignalMessage",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage.class,"Messaging",Messaging.class));
		this.USER_MESSAGE = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.UserMessageModel(new Field("UserMessage",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage.class,"Messaging",Messaging.class));
		this.ID = new Field("id",java.lang.String.class,"Messaging",Messaging.class);
	
	}
	
	public MessagingModel(IField father){
	
		super(father);
	
		this.SIGNAL_MESSAGE = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.SignalMessageModel(new ComplexField(father,"SignalMessage",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage.class,"Messaging",Messaging.class));
		this.USER_MESSAGE = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.UserMessageModel(new ComplexField(father,"UserMessage",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage.class,"Messaging",Messaging.class));
		this.ID = new ComplexField(father,"id",java.lang.String.class,"Messaging",Messaging.class);
	
	}
	
	

	public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.SignalMessageModel SIGNAL_MESSAGE = null;
	 
	public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.UserMessageModel USER_MESSAGE = null;
	 
	public IField ID = null;
	 

	@Override
	public Class<Messaging> getModeledClass(){
		return Messaging.class;
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