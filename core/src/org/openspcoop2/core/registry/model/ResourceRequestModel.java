/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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
package org.openspcoop2.core.registry.model;

import org.openspcoop2.core.registry.ResourceRequest;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ResourceRequest 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ResourceRequestModel extends AbstractModel<ResourceRequest> {

	public ResourceRequestModel(){
	
		super();
	
		this.REPRESENTATION = new org.openspcoop2.core.registry.model.ResourceRepresentationModel(new Field("representation",org.openspcoop2.core.registry.ResourceRepresentation.class,"resource-request",ResourceRequest.class));
		this.ID_RESOURCE = new Field("id-resource",java.lang.Long.class,"resource-request",ResourceRequest.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"resource-request",ResourceRequest.class);
		this.MESSAGE_TYPE = new Field("message-type",java.lang.String.class,"resource-request",ResourceRequest.class);
	
	}
	
	public ResourceRequestModel(IField father){
	
		super(father);
	
		this.REPRESENTATION = new org.openspcoop2.core.registry.model.ResourceRepresentationModel(new ComplexField(father,"representation",org.openspcoop2.core.registry.ResourceRepresentation.class,"resource-request",ResourceRequest.class));
		this.ID_RESOURCE = new ComplexField(father,"id-resource",java.lang.Long.class,"resource-request",ResourceRequest.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"resource-request",ResourceRequest.class);
		this.MESSAGE_TYPE = new ComplexField(father,"message-type",java.lang.String.class,"resource-request",ResourceRequest.class);
	
	}
	
	

	public org.openspcoop2.core.registry.model.ResourceRepresentationModel REPRESENTATION = null;
	 
	public IField ID_RESOURCE = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField MESSAGE_TYPE = null;
	 

	@Override
	public Class<ResourceRequest> getModeledClass(){
		return ResourceRequest.class;
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