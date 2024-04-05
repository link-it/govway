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

import org.openspcoop2.protocol.manifest.ServiceType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ServiceType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServiceTypeModel extends AbstractModel<ServiceType> {

	public ServiceTypeModel(){
	
		super();
	
		this.SOAP_MEDIA_TYPE_COLLECTION = new org.openspcoop2.protocol.manifest.model.SoapMediaTypeCollectionModel(new Field("soapMediaTypeCollection",org.openspcoop2.protocol.manifest.SoapMediaTypeCollection.class,"ServiceType",ServiceType.class));
		this.REST_MEDIA_TYPE_COLLECTION = new org.openspcoop2.protocol.manifest.model.RestMediaTypeCollectionModel(new Field("restMediaTypeCollection",org.openspcoop2.protocol.manifest.RestMediaTypeCollection.class,"ServiceType",ServiceType.class));
		this.NAME = new Field("name",java.lang.String.class,"ServiceType",ServiceType.class);
		this.PROTOCOL = new Field("protocol",java.lang.String.class,"ServiceType",ServiceType.class);
		this.MESSAGE_TYPE = new Field("messageType",java.lang.String.class,"ServiceType",ServiceType.class);
		this.BINDING = new Field("binding",java.lang.String.class,"ServiceType",ServiceType.class);
	
	}
	
	public ServiceTypeModel(IField father){
	
		super(father);
	
		this.SOAP_MEDIA_TYPE_COLLECTION = new org.openspcoop2.protocol.manifest.model.SoapMediaTypeCollectionModel(new ComplexField(father,"soapMediaTypeCollection",org.openspcoop2.protocol.manifest.SoapMediaTypeCollection.class,"ServiceType",ServiceType.class));
		this.REST_MEDIA_TYPE_COLLECTION = new org.openspcoop2.protocol.manifest.model.RestMediaTypeCollectionModel(new ComplexField(father,"restMediaTypeCollection",org.openspcoop2.protocol.manifest.RestMediaTypeCollection.class,"ServiceType",ServiceType.class));
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"ServiceType",ServiceType.class);
		this.PROTOCOL = new ComplexField(father,"protocol",java.lang.String.class,"ServiceType",ServiceType.class);
		this.MESSAGE_TYPE = new ComplexField(father,"messageType",java.lang.String.class,"ServiceType",ServiceType.class);
		this.BINDING = new ComplexField(father,"binding",java.lang.String.class,"ServiceType",ServiceType.class);
	
	}
	
	

	public org.openspcoop2.protocol.manifest.model.SoapMediaTypeCollectionModel SOAP_MEDIA_TYPE_COLLECTION = null;
	 
	public org.openspcoop2.protocol.manifest.model.RestMediaTypeCollectionModel REST_MEDIA_TYPE_COLLECTION = null;
	 
	public IField NAME = null;
	 
	public IField PROTOCOL = null;
	 
	public IField MESSAGE_TYPE = null;
	 
	public IField BINDING = null;
	 

	@Override
	public Class<ServiceType> getModeledClass(){
		return ServiceType.class;
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