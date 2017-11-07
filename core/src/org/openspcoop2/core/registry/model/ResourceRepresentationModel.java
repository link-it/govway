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
	
		this.MEDIA_TYPE = new Field("media-type",java.lang.String.class,"resource-representation",ResourceRepresentation.class);
		this.MESSAGE_TYPE = new Field("message-type",java.lang.String.class,"resource-representation",ResourceRepresentation.class);
	
	}
	
	public ResourceRepresentationModel(IField father){
	
		super(father);
	
		this.MEDIA_TYPE = new ComplexField(father,"media-type",java.lang.String.class,"resource-representation",ResourceRepresentation.class);
		this.MESSAGE_TYPE = new ComplexField(father,"message-type",java.lang.String.class,"resource-representation",ResourceRepresentation.class);
	
	}
	
	

	public IField MEDIA_TYPE = null;
	 
	public IField MESSAGE_TYPE = null;
	 

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