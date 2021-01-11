/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model CollaborationInfo 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CollaborationInfoModel extends AbstractModel<CollaborationInfo> {

	public CollaborationInfoModel(){
	
		super();
	
		this.AGREEMENT_REF = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.AgreementRefModel(new Field("AgreementRef",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef.class,"CollaborationInfo",CollaborationInfo.class));
		this.SERVICE = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.ServiceModel(new Field("Service",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service.class,"CollaborationInfo",CollaborationInfo.class));
		this.ACTION = new Field("Action",java.lang.String.class,"CollaborationInfo",CollaborationInfo.class);
		this.CONVERSATION_ID = new Field("ConversationId",java.lang.String.class,"CollaborationInfo",CollaborationInfo.class);
	
	}
	
	public CollaborationInfoModel(IField father){
	
		super(father);
	
		this.AGREEMENT_REF = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.AgreementRefModel(new ComplexField(father,"AgreementRef",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef.class,"CollaborationInfo",CollaborationInfo.class));
		this.SERVICE = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.ServiceModel(new ComplexField(father,"Service",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service.class,"CollaborationInfo",CollaborationInfo.class));
		this.ACTION = new ComplexField(father,"Action",java.lang.String.class,"CollaborationInfo",CollaborationInfo.class);
		this.CONVERSATION_ID = new ComplexField(father,"ConversationId",java.lang.String.class,"CollaborationInfo",CollaborationInfo.class);
	
	}
	
	

	public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.AgreementRefModel AGREEMENT_REF = null;
	 
	public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.ServiceModel SERVICE = null;
	 
	public IField ACTION = null;
	 
	public IField CONVERSATION_ID = null;
	 

	@Override
	public Class<CollaborationInfo> getModeledClass(){
		return CollaborationInfo.class;
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