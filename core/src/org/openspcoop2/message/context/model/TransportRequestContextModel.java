/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

import org.openspcoop2.message.context.TransportRequestContext;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model TransportRequestContext 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransportRequestContextModel extends AbstractModel<TransportRequestContext> {

	public TransportRequestContextModel(){
	
		super();
	
		this.URL_PARAMETERS = new org.openspcoop2.message.context.model.UrlParametersModel(new Field("url-parameters",org.openspcoop2.message.context.UrlParameters.class,"transport-request-context",TransportRequestContext.class));
		this.HEADER_PARAMETERS = new org.openspcoop2.message.context.model.HeaderParametersModel(new Field("header-parameters",org.openspcoop2.message.context.HeaderParameters.class,"transport-request-context",TransportRequestContext.class));
		this.CREDENTIALS = new org.openspcoop2.message.context.model.CredentialsModel(new Field("credentials",org.openspcoop2.message.context.Credentials.class,"transport-request-context",TransportRequestContext.class));
		this.WEB_CONTEXT = new Field("web-context",java.lang.String.class,"transport-request-context",TransportRequestContext.class);
		this.REQUEST_URI = new Field("request-uri",java.lang.String.class,"transport-request-context",TransportRequestContext.class);
		this.REQUEST_TYPE = new Field("request-type",java.lang.String.class,"transport-request-context",TransportRequestContext.class);
		this.SOURCE = new Field("source",java.lang.String.class,"transport-request-context",TransportRequestContext.class);
		this.PROTOCOL_NAME = new Field("protocol-name",java.lang.String.class,"transport-request-context",TransportRequestContext.class);
		this.PROTOCOL_WEB_CONTEXT = new Field("protocol-web-context",java.lang.String.class,"transport-request-context",TransportRequestContext.class);
		this.FUNCTION = new Field("function",java.lang.String.class,"transport-request-context",TransportRequestContext.class);
		this.FUNCTION_PARAMETERS = new Field("function-parameters",java.lang.String.class,"transport-request-context",TransportRequestContext.class);
		this.INTERFACE_NAME = new Field("interface-name",java.lang.String.class,"transport-request-context",TransportRequestContext.class);
	
	}
	
	public TransportRequestContextModel(IField father){
	
		super(father);
	
		this.URL_PARAMETERS = new org.openspcoop2.message.context.model.UrlParametersModel(new ComplexField(father,"url-parameters",org.openspcoop2.message.context.UrlParameters.class,"transport-request-context",TransportRequestContext.class));
		this.HEADER_PARAMETERS = new org.openspcoop2.message.context.model.HeaderParametersModel(new ComplexField(father,"header-parameters",org.openspcoop2.message.context.HeaderParameters.class,"transport-request-context",TransportRequestContext.class));
		this.CREDENTIALS = new org.openspcoop2.message.context.model.CredentialsModel(new ComplexField(father,"credentials",org.openspcoop2.message.context.Credentials.class,"transport-request-context",TransportRequestContext.class));
		this.WEB_CONTEXT = new ComplexField(father,"web-context",java.lang.String.class,"transport-request-context",TransportRequestContext.class);
		this.REQUEST_URI = new ComplexField(father,"request-uri",java.lang.String.class,"transport-request-context",TransportRequestContext.class);
		this.REQUEST_TYPE = new ComplexField(father,"request-type",java.lang.String.class,"transport-request-context",TransportRequestContext.class);
		this.SOURCE = new ComplexField(father,"source",java.lang.String.class,"transport-request-context",TransportRequestContext.class);
		this.PROTOCOL_NAME = new ComplexField(father,"protocol-name",java.lang.String.class,"transport-request-context",TransportRequestContext.class);
		this.PROTOCOL_WEB_CONTEXT = new ComplexField(father,"protocol-web-context",java.lang.String.class,"transport-request-context",TransportRequestContext.class);
		this.FUNCTION = new ComplexField(father,"function",java.lang.String.class,"transport-request-context",TransportRequestContext.class);
		this.FUNCTION_PARAMETERS = new ComplexField(father,"function-parameters",java.lang.String.class,"transport-request-context",TransportRequestContext.class);
		this.INTERFACE_NAME = new ComplexField(father,"interface-name",java.lang.String.class,"transport-request-context",TransportRequestContext.class);
	
	}
	
	

	public org.openspcoop2.message.context.model.UrlParametersModel URL_PARAMETERS = null;
	 
	public org.openspcoop2.message.context.model.HeaderParametersModel HEADER_PARAMETERS = null;
	 
	public org.openspcoop2.message.context.model.CredentialsModel CREDENTIALS = null;
	 
	public IField WEB_CONTEXT = null;
	 
	public IField REQUEST_URI = null;
	 
	public IField REQUEST_TYPE = null;
	 
	public IField SOURCE = null;
	 
	public IField PROTOCOL_NAME = null;
	 
	public IField PROTOCOL_WEB_CONTEXT = null;
	 
	public IField FUNCTION = null;
	 
	public IField FUNCTION_PARAMETERS = null;
	 
	public IField INTERFACE_NAME = null;
	 

	@Override
	public Class<TransportRequestContext> getModeledClass(){
		return TransportRequestContext.class;
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