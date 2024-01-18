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

import org.openspcoop2.message.context.TransportResponseContext;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model TransportResponseContext 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransportResponseContextModel extends AbstractModel<TransportResponseContext> {

	public TransportResponseContextModel(){
	
		super();
	
		this.HEADER_PARAMETERS = new org.openspcoop2.message.context.model.HeaderParametersModel(new Field("header-parameters",org.openspcoop2.message.context.HeaderParameters.class,"transport-response-context",TransportResponseContext.class));
		this.TRANSPORT_CODE = new Field("transport-code",java.lang.String.class,"transport-response-context",TransportResponseContext.class);
		this.CONTENT_LENGTH = new Field("content-length",java.lang.Long.class,"transport-response-context",TransportResponseContext.class);
		this.ERROR = new Field("error",java.lang.String.class,"transport-response-context",TransportResponseContext.class);
	
	}
	
	public TransportResponseContextModel(IField father){
	
		super(father);
	
		this.HEADER_PARAMETERS = new org.openspcoop2.message.context.model.HeaderParametersModel(new ComplexField(father,"header-parameters",org.openspcoop2.message.context.HeaderParameters.class,"transport-response-context",TransportResponseContext.class));
		this.TRANSPORT_CODE = new ComplexField(father,"transport-code",java.lang.String.class,"transport-response-context",TransportResponseContext.class);
		this.CONTENT_LENGTH = new ComplexField(father,"content-length",java.lang.Long.class,"transport-response-context",TransportResponseContext.class);
		this.ERROR = new ComplexField(father,"error",java.lang.String.class,"transport-response-context",TransportResponseContext.class);
	
	}
	
	

	public org.openspcoop2.message.context.model.HeaderParametersModel HEADER_PARAMETERS = null;
	 
	public IField TRANSPORT_CODE = null;
	 
	public IField CONTENT_LENGTH = null;
	 
	public IField ERROR = null;
	 

	@Override
	public Class<TransportResponseContext> getModeledClass(){
		return TransportResponseContext.class;
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