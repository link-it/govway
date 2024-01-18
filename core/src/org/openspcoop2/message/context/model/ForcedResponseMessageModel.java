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
package org.openspcoop2.message.context.model;

import org.openspcoop2.message.context.ForcedResponseMessage;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ForcedResponseMessage 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ForcedResponseMessageModel extends AbstractModel<ForcedResponseMessage> {

	public ForcedResponseMessageModel(){
	
		super();
	
		this.CONTENT = new Field("content",byte[].class,"forced-response-message",ForcedResponseMessage.class);
		this.CONTENT_TYPE = new Field("content-type",java.lang.String.class,"forced-response-message",ForcedResponseMessage.class);
		this.RESPONSE_CODE = new Field("response-code",java.lang.String.class,"forced-response-message",ForcedResponseMessage.class);
		this.HEADER_PARAMETERS = new org.openspcoop2.message.context.model.HeaderParametersModel(new Field("header-parameters",org.openspcoop2.message.context.HeaderParameters.class,"forced-response-message",ForcedResponseMessage.class));
	
	}
	
	public ForcedResponseMessageModel(IField father){
	
		super(father);
	
		this.CONTENT = new ComplexField(father,"content",byte[].class,"forced-response-message",ForcedResponseMessage.class);
		this.CONTENT_TYPE = new ComplexField(father,"content-type",java.lang.String.class,"forced-response-message",ForcedResponseMessage.class);
		this.RESPONSE_CODE = new ComplexField(father,"response-code",java.lang.String.class,"forced-response-message",ForcedResponseMessage.class);
		this.HEADER_PARAMETERS = new org.openspcoop2.message.context.model.HeaderParametersModel(new ComplexField(father,"header-parameters",org.openspcoop2.message.context.HeaderParameters.class,"forced-response-message",ForcedResponseMessage.class));
	
	}
	
	

	public IField CONTENT = null;
	 
	public IField CONTENT_TYPE = null;
	 
	public IField RESPONSE_CODE = null;
	 
	public org.openspcoop2.message.context.model.HeaderParametersModel HEADER_PARAMETERS = null;
	 

	@Override
	public Class<ForcedResponseMessage> getModeledClass(){
		return ForcedResponseMessage.class;
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