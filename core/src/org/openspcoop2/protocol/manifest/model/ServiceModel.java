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
package org.openspcoop2.protocol.manifest.model;

import org.openspcoop2.protocol.manifest.Service;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Service 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServiceModel extends AbstractModel<Service> {

	public ServiceModel(){
	
		super();
	
		this.TYPES = new org.openspcoop2.protocol.manifest.model.ServiceTypesModel(new Field("types",org.openspcoop2.protocol.manifest.ServiceTypes.class,"Service",Service.class));
		this.API_REFERENT = new Field("apiReferent",boolean.class,"Service",Service.class);
		this.VERSION = new Field("version",boolean.class,"Service",Service.class);
		this.PROTOCOL_ENVELOPE_MANAGEMENT = new Field("protocolEnvelopeManagement",boolean.class,"Service",Service.class);
		this.FAULT_CHOICE = new Field("faultChoice",boolean.class,"Service",Service.class);
		this.CORRELATION_REUSE_PROTOCOL_ID = new Field("correlationReuseProtocolId",boolean.class,"Service",Service.class);
		this.TRACE = new Field("trace",boolean.class,"Service",Service.class);
	
	}
	
	public ServiceModel(IField father){
	
		super(father);
	
		this.TYPES = new org.openspcoop2.protocol.manifest.model.ServiceTypesModel(new ComplexField(father,"types",org.openspcoop2.protocol.manifest.ServiceTypes.class,"Service",Service.class));
		this.API_REFERENT = new ComplexField(father,"apiReferent",boolean.class,"Service",Service.class);
		this.VERSION = new ComplexField(father,"version",boolean.class,"Service",Service.class);
		this.PROTOCOL_ENVELOPE_MANAGEMENT = new ComplexField(father,"protocolEnvelopeManagement",boolean.class,"Service",Service.class);
		this.FAULT_CHOICE = new ComplexField(father,"faultChoice",boolean.class,"Service",Service.class);
		this.CORRELATION_REUSE_PROTOCOL_ID = new ComplexField(father,"correlationReuseProtocolId",boolean.class,"Service",Service.class);
		this.TRACE = new ComplexField(father,"trace",boolean.class,"Service",Service.class);
	
	}
	
	

	public org.openspcoop2.protocol.manifest.model.ServiceTypesModel TYPES = null;
	 
	public IField API_REFERENT = null;
	 
	public IField VERSION = null;
	 
	public IField PROTOCOL_ENVELOPE_MANAGEMENT = null;
	 
	public IField FAULT_CHOICE = null;
	 
	public IField CORRELATION_REUSE_PROTOCOL_ID = null;
	 
	public IField TRACE = null;
	 

	@Override
	public Class<Service> getModeledClass(){
		return Service.class;
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