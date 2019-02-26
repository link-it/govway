/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import org.openspcoop2.core.registry.ResourceRepresentation;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ResourceRepresentation 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ResourceRepresentationModel extends AbstractModel<ResourceRepresentation> {

	public ResourceRepresentationModel(){
	
		super();
	
		this.XML = new org.openspcoop2.core.registry.model.ResourceRepresentationXmlModel(new Field("xml",org.openspcoop2.core.registry.ResourceRepresentationXml.class,"resource-representation",ResourceRepresentation.class));
		this.JSON = new org.openspcoop2.core.registry.model.ResourceRepresentationJsonModel(new Field("json",org.openspcoop2.core.registry.ResourceRepresentationJson.class,"resource-representation",ResourceRepresentation.class));
		this.MEDIA_TYPE = new Field("media-type",java.lang.String.class,"resource-representation",ResourceRepresentation.class);
		this.NOME = new Field("nome",java.lang.String.class,"resource-representation",ResourceRepresentation.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"resource-representation",ResourceRepresentation.class);
		this.MESSAGE_TYPE = new Field("message-type",java.lang.String.class,"resource-representation",ResourceRepresentation.class);
		this.REPRESENTATION_TYPE = new Field("representation-type",java.lang.String.class,"resource-representation",ResourceRepresentation.class);
	
	}
	
	public ResourceRepresentationModel(IField father){
	
		super(father);
	
		this.XML = new org.openspcoop2.core.registry.model.ResourceRepresentationXmlModel(new ComplexField(father,"xml",org.openspcoop2.core.registry.ResourceRepresentationXml.class,"resource-representation",ResourceRepresentation.class));
		this.JSON = new org.openspcoop2.core.registry.model.ResourceRepresentationJsonModel(new ComplexField(father,"json",org.openspcoop2.core.registry.ResourceRepresentationJson.class,"resource-representation",ResourceRepresentation.class));
		this.MEDIA_TYPE = new ComplexField(father,"media-type",java.lang.String.class,"resource-representation",ResourceRepresentation.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"resource-representation",ResourceRepresentation.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"resource-representation",ResourceRepresentation.class);
		this.MESSAGE_TYPE = new ComplexField(father,"message-type",java.lang.String.class,"resource-representation",ResourceRepresentation.class);
		this.REPRESENTATION_TYPE = new ComplexField(father,"representation-type",java.lang.String.class,"resource-representation",ResourceRepresentation.class);
	
	}
	
	

	public org.openspcoop2.core.registry.model.ResourceRepresentationXmlModel XML = null;
	 
	public org.openspcoop2.core.registry.model.ResourceRepresentationJsonModel JSON = null;
	 
	public IField MEDIA_TYPE = null;
	 
	public IField NOME = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField MESSAGE_TYPE = null;
	 
	public IField REPRESENTATION_TYPE = null;
	 

	@Override
	public Class<ResourceRepresentation> getModeledClass(){
		return ResourceRepresentation.class;
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