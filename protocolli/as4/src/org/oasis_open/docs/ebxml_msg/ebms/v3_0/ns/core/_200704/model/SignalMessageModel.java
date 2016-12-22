/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model SignalMessage 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SignalMessageModel extends AbstractModel<SignalMessage> {

	public SignalMessageModel(){
	
		super();
	
		this.MESSAGE_INFO = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.MessageInfoModel(new Field("MessageInfo",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo.class,"SignalMessage",SignalMessage.class));
		this.PULL_REQUEST = new Field("PullRequest",java.lang.String.class,"SignalMessage",SignalMessage.class);
		this.RECEIPT = new Field("Receipt",java.lang.String.class,"SignalMessage",SignalMessage.class);
		this.ERROR = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.ErrorModel(new Field("Error",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error.class,"SignalMessage",SignalMessage.class));
	
	}
	
	public SignalMessageModel(IField father){
	
		super(father);
	
		this.MESSAGE_INFO = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.MessageInfoModel(new ComplexField(father,"MessageInfo",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo.class,"SignalMessage",SignalMessage.class));
		this.PULL_REQUEST = new ComplexField(father,"PullRequest",java.lang.String.class,"SignalMessage",SignalMessage.class);
		this.RECEIPT = new ComplexField(father,"Receipt",java.lang.String.class,"SignalMessage",SignalMessage.class);
		this.ERROR = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.ErrorModel(new ComplexField(father,"Error",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error.class,"SignalMessage",SignalMessage.class));
	
	}
	
	

	public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.MessageInfoModel MESSAGE_INFO = null;
	 
	public IField PULL_REQUEST = null;
	 
	public IField RECEIPT = null;
	 
	public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.ErrorModel ERROR = null;
	 

	@Override
	public Class<SignalMessage> getModeledClass(){
		return SignalMessage.class;
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