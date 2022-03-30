/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

import org.openspcoop2.protocol.manifest.RestMediaTypeCollection;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model RestMediaTypeCollection 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RestMediaTypeCollectionModel extends AbstractModel<RestMediaTypeCollection> {

	public RestMediaTypeCollectionModel(){
	
		super();
	
		this.MEDIA_TYPE = new org.openspcoop2.protocol.manifest.model.RestMediaTypeMappingModel(new Field("mediaType",org.openspcoop2.protocol.manifest.RestMediaTypeMapping.class,"RestMediaTypeCollection",RestMediaTypeCollection.class));
		this.DEFAULT = new org.openspcoop2.protocol.manifest.model.RestMediaTypeDefaultMappingModel(new Field("default",org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping.class,"RestMediaTypeCollection",RestMediaTypeCollection.class));
		this.UNDEFINED = new org.openspcoop2.protocol.manifest.model.RestMediaTypeUndefinedMappingModel(new Field("undefined",org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping.class,"RestMediaTypeCollection",RestMediaTypeCollection.class));
	
	}
	
	public RestMediaTypeCollectionModel(IField father){
	
		super(father);
	
		this.MEDIA_TYPE = new org.openspcoop2.protocol.manifest.model.RestMediaTypeMappingModel(new ComplexField(father,"mediaType",org.openspcoop2.protocol.manifest.RestMediaTypeMapping.class,"RestMediaTypeCollection",RestMediaTypeCollection.class));
		this.DEFAULT = new org.openspcoop2.protocol.manifest.model.RestMediaTypeDefaultMappingModel(new ComplexField(father,"default",org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping.class,"RestMediaTypeCollection",RestMediaTypeCollection.class));
		this.UNDEFINED = new org.openspcoop2.protocol.manifest.model.RestMediaTypeUndefinedMappingModel(new ComplexField(father,"undefined",org.openspcoop2.protocol.manifest.RestMediaTypeUndefinedMapping.class,"RestMediaTypeCollection",RestMediaTypeCollection.class));
	
	}
	
	

	public org.openspcoop2.protocol.manifest.model.RestMediaTypeMappingModel MEDIA_TYPE = null;
	 
	public org.openspcoop2.protocol.manifest.model.RestMediaTypeDefaultMappingModel DEFAULT = null;
	 
	public org.openspcoop2.protocol.manifest.model.RestMediaTypeUndefinedMappingModel UNDEFINED = null;
	 

	@Override
	public Class<RestMediaTypeCollection> getModeledClass(){
		return RestMediaTypeCollection.class;
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