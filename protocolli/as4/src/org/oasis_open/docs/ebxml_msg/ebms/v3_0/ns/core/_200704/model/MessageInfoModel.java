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
package org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model;

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model MessageInfo 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessageInfoModel extends AbstractModel<MessageInfo> {

	public MessageInfoModel(){
	
		super();
	
		this.TIMESTAMP = new Field("Timestamp",java.util.Date.class,"MessageInfo",MessageInfo.class);
		this.MESSAGE_ID = new Field("MessageId",java.lang.String.class,"MessageInfo",MessageInfo.class);
		this.REF_TO_MESSAGE_ID = new Field("RefToMessageId",java.lang.String.class,"MessageInfo",MessageInfo.class);
	
	}
	
	public MessageInfoModel(IField father){
	
		super(father);
	
		this.TIMESTAMP = new ComplexField(father,"Timestamp",java.util.Date.class,"MessageInfo",MessageInfo.class);
		this.MESSAGE_ID = new ComplexField(father,"MessageId",java.lang.String.class,"MessageInfo",MessageInfo.class);
		this.REF_TO_MESSAGE_ID = new ComplexField(father,"RefToMessageId",java.lang.String.class,"MessageInfo",MessageInfo.class);
	
	}
	
	

	public IField TIMESTAMP = null;
	 
	public IField MESSAGE_ID = null;
	 
	public IField REF_TO_MESSAGE_ID = null;
	 

	@Override
	public Class<MessageInfo> getModeledClass(){
		return MessageInfo.class;
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