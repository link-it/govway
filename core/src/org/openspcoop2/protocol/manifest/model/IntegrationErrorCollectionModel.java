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
package org.openspcoop2.protocol.manifest.model;

import org.openspcoop2.protocol.manifest.IntegrationErrorCollection;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model IntegrationErrorCollection 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IntegrationErrorCollectionModel extends AbstractModel<IntegrationErrorCollection> {

	public IntegrationErrorCollectionModel(){
	
		super();
	
		this.RFC_7807 = new org.openspcoop2.protocol.manifest.model.RFC7807Model(new Field("rfc7807",org.openspcoop2.protocol.manifest.RFC7807.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.AUTHENTICATION = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new Field("authentication",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.AUTHORIZATION = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new Field("authorization",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.NOT_FOUND = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new Field("notFound",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.BAD_REQUEST = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new Field("badRequest",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.CONFLICT = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new Field("conflict",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.REQUEST_TOO_LARGE = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new Field("requestTooLarge",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.LIMIT_EXCEEDED = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new Field("limitExceeded",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.TOO_MANY_REQUESTS = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new Field("tooManyRequests",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.SERVICE_UNAVAILABLE = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new Field("serviceUnavailable",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.ENDPOINT_REQUEST_TIMED_OUT = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new Field("endpointRequestTimedOut",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.BAD_RESPONSE = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new Field("badResponse",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.INTERNAL_REQUEST_ERROR = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new Field("internalRequestError",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.INTERNAL_RESPONSE_ERROR = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new Field("internalResponseError",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.DEFAULT = new org.openspcoop2.protocol.manifest.model.DefaultIntegrationErrorModel(new Field("default",org.openspcoop2.protocol.manifest.DefaultIntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.PROBLEM_TYPE = new Field("problemType",java.lang.String.class,"IntegrationErrorCollection",IntegrationErrorCollection.class);
		this.USE_INTERNAL_FAULT = new Field("useInternalFault",boolean.class,"IntegrationErrorCollection",IntegrationErrorCollection.class);
	
	}
	
	public IntegrationErrorCollectionModel(IField father){
	
		super(father);
	
		this.RFC_7807 = new org.openspcoop2.protocol.manifest.model.RFC7807Model(new ComplexField(father,"rfc7807",org.openspcoop2.protocol.manifest.RFC7807.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.AUTHENTICATION = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new ComplexField(father,"authentication",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.AUTHORIZATION = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new ComplexField(father,"authorization",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.NOT_FOUND = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new ComplexField(father,"notFound",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.BAD_REQUEST = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new ComplexField(father,"badRequest",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.CONFLICT = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new ComplexField(father,"conflict",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.REQUEST_TOO_LARGE = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new ComplexField(father,"requestTooLarge",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.LIMIT_EXCEEDED = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new ComplexField(father,"limitExceeded",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.TOO_MANY_REQUESTS = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new ComplexField(father,"tooManyRequests",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.SERVICE_UNAVAILABLE = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new ComplexField(father,"serviceUnavailable",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.ENDPOINT_REQUEST_TIMED_OUT = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new ComplexField(father,"endpointRequestTimedOut",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.BAD_RESPONSE = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new ComplexField(father,"badResponse",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.INTERNAL_REQUEST_ERROR = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new ComplexField(father,"internalRequestError",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.INTERNAL_RESPONSE_ERROR = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new ComplexField(father,"internalResponseError",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.DEFAULT = new org.openspcoop2.protocol.manifest.model.DefaultIntegrationErrorModel(new ComplexField(father,"default",org.openspcoop2.protocol.manifest.DefaultIntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.PROBLEM_TYPE = new ComplexField(father,"problemType",java.lang.String.class,"IntegrationErrorCollection",IntegrationErrorCollection.class);
		this.USE_INTERNAL_FAULT = new ComplexField(father,"useInternalFault",boolean.class,"IntegrationErrorCollection",IntegrationErrorCollection.class);
	
	}
	
	

	public org.openspcoop2.protocol.manifest.model.RFC7807Model RFC_7807 = null;
	 
	public org.openspcoop2.protocol.manifest.model.IntegrationErrorModel AUTHENTICATION = null;
	 
	public org.openspcoop2.protocol.manifest.model.IntegrationErrorModel AUTHORIZATION = null;
	 
	public org.openspcoop2.protocol.manifest.model.IntegrationErrorModel NOT_FOUND = null;
	 
	public org.openspcoop2.protocol.manifest.model.IntegrationErrorModel BAD_REQUEST = null;
	 
	public org.openspcoop2.protocol.manifest.model.IntegrationErrorModel CONFLICT = null;
	 
	public org.openspcoop2.protocol.manifest.model.IntegrationErrorModel REQUEST_TOO_LARGE = null;
	 
	public org.openspcoop2.protocol.manifest.model.IntegrationErrorModel LIMIT_EXCEEDED = null;
	 
	public org.openspcoop2.protocol.manifest.model.IntegrationErrorModel TOO_MANY_REQUESTS = null;
	 
	public org.openspcoop2.protocol.manifest.model.IntegrationErrorModel SERVICE_UNAVAILABLE = null;
	 
	public org.openspcoop2.protocol.manifest.model.IntegrationErrorModel ENDPOINT_REQUEST_TIMED_OUT = null;
	 
	public org.openspcoop2.protocol.manifest.model.IntegrationErrorModel BAD_RESPONSE = null;
	 
	public org.openspcoop2.protocol.manifest.model.IntegrationErrorModel INTERNAL_REQUEST_ERROR = null;
	 
	public org.openspcoop2.protocol.manifest.model.IntegrationErrorModel INTERNAL_RESPONSE_ERROR = null;
	 
	public org.openspcoop2.protocol.manifest.model.DefaultIntegrationErrorModel DEFAULT = null;
	 
	public IField PROBLEM_TYPE = null;
	 
	public IField USE_INTERNAL_FAULT = null;
	 

	@Override
	public Class<IntegrationErrorCollection> getModeledClass(){
		return IntegrationErrorCollection.class;
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