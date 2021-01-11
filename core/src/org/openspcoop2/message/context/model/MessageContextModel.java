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
package org.openspcoop2.message.context.model;

import org.openspcoop2.message.context.MessageContext;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model MessageContext 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessageContextModel extends AbstractModel<MessageContext> {

	public MessageContextModel(){
	
		super();
	
		this.TRANSPORT_REQUEST_CONTEXT = new org.openspcoop2.message.context.model.TransportRequestContextModel(new Field("transport-request-context",org.openspcoop2.message.context.TransportRequestContext.class,"message-context",MessageContext.class));
		this.TRANSPORT_RESPONSE_CONTEXT = new org.openspcoop2.message.context.model.TransportResponseContextModel(new Field("transport-response-context",org.openspcoop2.message.context.TransportResponseContext.class,"message-context",MessageContext.class));
		this.FORCED_RESPONSE = new org.openspcoop2.message.context.model.ForcedResponseModel(new Field("forced-response",org.openspcoop2.message.context.ForcedResponse.class,"message-context",MessageContext.class));
		this.SERIALIZED_CONTEXT = new org.openspcoop2.message.context.model.SerializedContextModel(new Field("serialized-context",org.openspcoop2.message.context.SerializedContext.class,"message-context",MessageContext.class));
		this.CONTENT_TYPE_PARAMETERS = new org.openspcoop2.message.context.model.ContentTypeParametersModel(new Field("content-type-parameters",org.openspcoop2.message.context.ContentTypeParameters.class,"message-context",MessageContext.class));
		this.CONTENT_LENGTH = new org.openspcoop2.message.context.model.ContentLengthModel(new Field("content-length",org.openspcoop2.message.context.ContentLength.class,"message-context",MessageContext.class));
		this.SOAP = new org.openspcoop2.message.context.model.SoapModel(new Field("soap",org.openspcoop2.message.context.Soap.class,"message-context",MessageContext.class));
		this.MESSAGE_TYPE = new Field("message-type",java.lang.String.class,"message-context",MessageContext.class);
		this.MESSAGE_ROLE = new Field("message-role",java.lang.String.class,"message-context",MessageContext.class);
		this.PROTOCOL = new Field("protocol",java.lang.String.class,"message-context",MessageContext.class);
	
	}
	
	public MessageContextModel(IField father){
	
		super(father);
	
		this.TRANSPORT_REQUEST_CONTEXT = new org.openspcoop2.message.context.model.TransportRequestContextModel(new ComplexField(father,"transport-request-context",org.openspcoop2.message.context.TransportRequestContext.class,"message-context",MessageContext.class));
		this.TRANSPORT_RESPONSE_CONTEXT = new org.openspcoop2.message.context.model.TransportResponseContextModel(new ComplexField(father,"transport-response-context",org.openspcoop2.message.context.TransportResponseContext.class,"message-context",MessageContext.class));
		this.FORCED_RESPONSE = new org.openspcoop2.message.context.model.ForcedResponseModel(new ComplexField(father,"forced-response",org.openspcoop2.message.context.ForcedResponse.class,"message-context",MessageContext.class));
		this.SERIALIZED_CONTEXT = new org.openspcoop2.message.context.model.SerializedContextModel(new ComplexField(father,"serialized-context",org.openspcoop2.message.context.SerializedContext.class,"message-context",MessageContext.class));
		this.CONTENT_TYPE_PARAMETERS = new org.openspcoop2.message.context.model.ContentTypeParametersModel(new ComplexField(father,"content-type-parameters",org.openspcoop2.message.context.ContentTypeParameters.class,"message-context",MessageContext.class));
		this.CONTENT_LENGTH = new org.openspcoop2.message.context.model.ContentLengthModel(new ComplexField(father,"content-length",org.openspcoop2.message.context.ContentLength.class,"message-context",MessageContext.class));
		this.SOAP = new org.openspcoop2.message.context.model.SoapModel(new ComplexField(father,"soap",org.openspcoop2.message.context.Soap.class,"message-context",MessageContext.class));
		this.MESSAGE_TYPE = new ComplexField(father,"message-type",java.lang.String.class,"message-context",MessageContext.class);
		this.MESSAGE_ROLE = new ComplexField(father,"message-role",java.lang.String.class,"message-context",MessageContext.class);
		this.PROTOCOL = new ComplexField(father,"protocol",java.lang.String.class,"message-context",MessageContext.class);
	
	}
	
	

	public org.openspcoop2.message.context.model.TransportRequestContextModel TRANSPORT_REQUEST_CONTEXT = null;
	 
	public org.openspcoop2.message.context.model.TransportResponseContextModel TRANSPORT_RESPONSE_CONTEXT = null;
	 
	public org.openspcoop2.message.context.model.ForcedResponseModel FORCED_RESPONSE = null;
	 
	public org.openspcoop2.message.context.model.SerializedContextModel SERIALIZED_CONTEXT = null;
	 
	public org.openspcoop2.message.context.model.ContentTypeParametersModel CONTENT_TYPE_PARAMETERS = null;
	 
	public org.openspcoop2.message.context.model.ContentLengthModel CONTENT_LENGTH = null;
	 
	public org.openspcoop2.message.context.model.SoapModel SOAP = null;
	 
	public IField MESSAGE_TYPE = null;
	 
	public IField MESSAGE_ROLE = null;
	 
	public IField PROTOCOL = null;
	 

	@Override
	public Class<MessageContext> getModeledClass(){
		return MessageContext.class;
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