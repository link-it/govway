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

import org.openspcoop2.message.context.ForcedResponse;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ForcedResponse 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ForcedResponseModel extends AbstractModel<ForcedResponse> {

	public ForcedResponseModel(){
	
		super();
	
		this.RESPONSE_CODE = new Field("response-code",java.lang.String.class,"forced-response",ForcedResponse.class);
		this.EMPTY_RESPONSE = new Field("empty-response",boolean.class,"forced-response",ForcedResponse.class);
		this.RESPONSE_MESSAGE = new org.openspcoop2.message.context.model.ForcedResponseMessageModel(new Field("response-message",org.openspcoop2.message.context.ForcedResponseMessage.class,"forced-response",ForcedResponse.class));
	
	}
	
	public ForcedResponseModel(IField father){
	
		super(father);
	
		this.RESPONSE_CODE = new ComplexField(father,"response-code",java.lang.String.class,"forced-response",ForcedResponse.class);
		this.EMPTY_RESPONSE = new ComplexField(father,"empty-response",boolean.class,"forced-response",ForcedResponse.class);
		this.RESPONSE_MESSAGE = new org.openspcoop2.message.context.model.ForcedResponseMessageModel(new ComplexField(father,"response-message",org.openspcoop2.message.context.ForcedResponseMessage.class,"forced-response",ForcedResponse.class));
	
	}
	
	

	public IField RESPONSE_CODE = null;
	 
	public IField EMPTY_RESPONSE = null;
	 
	public org.openspcoop2.message.context.model.ForcedResponseMessageModel RESPONSE_MESSAGE = null;
	 

	@Override
	public Class<ForcedResponse> getModeledClass(){
		return ForcedResponse.class;
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