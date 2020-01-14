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
package backend.ecodex.org._1_1.model;

import backend.ecodex.org._1_1.Collection;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Collection 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CollectionModel extends AbstractModel<Collection> {

	public CollectionModel(){
	
		super();
	
		this.FAULT_DETAIL = new backend.ecodex.org._1_1.model.FaultDetailModel(new Field("FaultDetail",backend.ecodex.org._1_1.FaultDetail.class,"collection",Collection.class));
		this.RETRIEVE_MESSAGE_REQUEST = new backend.ecodex.org._1_1.model.RetrieveMessageRequestModel(new Field("retrieveMessageRequest",backend.ecodex.org._1_1.RetrieveMessageRequest.class,"collection",Collection.class));
		this.RETRIEVE_MESSAGE_RESPONSE = new backend.ecodex.org._1_1.model.RetrieveMessageResponseModel(new Field("retrieveMessageResponse",backend.ecodex.org._1_1.RetrieveMessageResponse.class,"collection",Collection.class));
		this.LIST_PENDING_MESSAGES_RESPONSE = new backend.ecodex.org._1_1.model.ListPendingMessagesResponseModel(new Field("listPendingMessagesResponse",backend.ecodex.org._1_1.ListPendingMessagesResponse.class,"collection",Collection.class));
		this.MESSAGE_ERRORS_REQUEST = new backend.ecodex.org._1_1.model.MessageErrorsRequestModel(new Field("messageErrorsRequest",backend.ecodex.org._1_1.MessageErrorsRequest.class,"collection",Collection.class));
		this.MESSAGE_STATUS_REQUEST = new backend.ecodex.org._1_1.model.MessageStatusRequestModel(new Field("messageStatusRequest",backend.ecodex.org._1_1.MessageStatusRequest.class,"collection",Collection.class));
		this.SUBMIT_REQUEST = new backend.ecodex.org._1_1.model.SubmitRequestModel(new Field("submitRequest",backend.ecodex.org._1_1.SubmitRequest.class,"collection",Collection.class));
		this.SUBMIT_RESPONSE = new backend.ecodex.org._1_1.model.SubmitResponseModel(new Field("submitResponse",backend.ecodex.org._1_1.SubmitResponse.class,"collection",Collection.class));
		this.GET_STATUS_REQUEST = new backend.ecodex.org._1_1.model.GetStatusRequestModel(new Field("getStatusRequest",backend.ecodex.org._1_1.GetStatusRequest.class,"collection",Collection.class));
		this.GET_ERRORS_REQUEST = new backend.ecodex.org._1_1.model.GetErrorsRequestModel(new Field("getErrorsRequest",backend.ecodex.org._1_1.GetErrorsRequest.class,"collection",Collection.class));
		this.GET_MESSAGE_ERRORS_RESPONSE = new backend.ecodex.org._1_1.model.ErrorResultImplArrayModel(new Field("getMessageErrorsResponse",backend.ecodex.org._1_1.ErrorResultImplArray.class,"collection",Collection.class));
	
	}
	
	public CollectionModel(IField father){
	
		super(father);
	
		this.FAULT_DETAIL = new backend.ecodex.org._1_1.model.FaultDetailModel(new ComplexField(father,"FaultDetail",backend.ecodex.org._1_1.FaultDetail.class,"collection",Collection.class));
		this.RETRIEVE_MESSAGE_REQUEST = new backend.ecodex.org._1_1.model.RetrieveMessageRequestModel(new ComplexField(father,"retrieveMessageRequest",backend.ecodex.org._1_1.RetrieveMessageRequest.class,"collection",Collection.class));
		this.RETRIEVE_MESSAGE_RESPONSE = new backend.ecodex.org._1_1.model.RetrieveMessageResponseModel(new ComplexField(father,"retrieveMessageResponse",backend.ecodex.org._1_1.RetrieveMessageResponse.class,"collection",Collection.class));
		this.LIST_PENDING_MESSAGES_RESPONSE = new backend.ecodex.org._1_1.model.ListPendingMessagesResponseModel(new ComplexField(father,"listPendingMessagesResponse",backend.ecodex.org._1_1.ListPendingMessagesResponse.class,"collection",Collection.class));
		this.MESSAGE_ERRORS_REQUEST = new backend.ecodex.org._1_1.model.MessageErrorsRequestModel(new ComplexField(father,"messageErrorsRequest",backend.ecodex.org._1_1.MessageErrorsRequest.class,"collection",Collection.class));
		this.MESSAGE_STATUS_REQUEST = new backend.ecodex.org._1_1.model.MessageStatusRequestModel(new ComplexField(father,"messageStatusRequest",backend.ecodex.org._1_1.MessageStatusRequest.class,"collection",Collection.class));
		this.SUBMIT_REQUEST = new backend.ecodex.org._1_1.model.SubmitRequestModel(new ComplexField(father,"submitRequest",backend.ecodex.org._1_1.SubmitRequest.class,"collection",Collection.class));
		this.SUBMIT_RESPONSE = new backend.ecodex.org._1_1.model.SubmitResponseModel(new ComplexField(father,"submitResponse",backend.ecodex.org._1_1.SubmitResponse.class,"collection",Collection.class));
		this.GET_STATUS_REQUEST = new backend.ecodex.org._1_1.model.GetStatusRequestModel(new ComplexField(father,"getStatusRequest",backend.ecodex.org._1_1.GetStatusRequest.class,"collection",Collection.class));
		this.GET_ERRORS_REQUEST = new backend.ecodex.org._1_1.model.GetErrorsRequestModel(new ComplexField(father,"getErrorsRequest",backend.ecodex.org._1_1.GetErrorsRequest.class,"collection",Collection.class));
		this.GET_MESSAGE_ERRORS_RESPONSE = new backend.ecodex.org._1_1.model.ErrorResultImplArrayModel(new ComplexField(father,"getMessageErrorsResponse",backend.ecodex.org._1_1.ErrorResultImplArray.class,"collection",Collection.class));
	
	}
	
	

	public backend.ecodex.org._1_1.model.FaultDetailModel FAULT_DETAIL = null;
	 
	public backend.ecodex.org._1_1.model.RetrieveMessageRequestModel RETRIEVE_MESSAGE_REQUEST = null;
	 
	public backend.ecodex.org._1_1.model.RetrieveMessageResponseModel RETRIEVE_MESSAGE_RESPONSE = null;
	 
	public backend.ecodex.org._1_1.model.ListPendingMessagesResponseModel LIST_PENDING_MESSAGES_RESPONSE = null;
	 
	public backend.ecodex.org._1_1.model.MessageErrorsRequestModel MESSAGE_ERRORS_REQUEST = null;
	 
	public backend.ecodex.org._1_1.model.MessageStatusRequestModel MESSAGE_STATUS_REQUEST = null;
	 
	public backend.ecodex.org._1_1.model.SubmitRequestModel SUBMIT_REQUEST = null;
	 
	public backend.ecodex.org._1_1.model.SubmitResponseModel SUBMIT_RESPONSE = null;
	 
	public backend.ecodex.org._1_1.model.GetStatusRequestModel GET_STATUS_REQUEST = null;
	 
	public backend.ecodex.org._1_1.model.GetErrorsRequestModel GET_ERRORS_REQUEST = null;
	 
	public backend.ecodex.org._1_1.model.ErrorResultImplArrayModel GET_MESSAGE_ERRORS_RESPONSE = null;
	 

	@Override
	public Class<Collection> getModeledClass(){
		return Collection.class;
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