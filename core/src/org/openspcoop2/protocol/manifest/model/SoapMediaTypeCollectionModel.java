/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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
package org.openspcoop2.protocol.manifest.model;

import org.openspcoop2.protocol.manifest.SoapMediaTypeCollection;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model SoapMediaTypeCollection 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoapMediaTypeCollectionModel extends AbstractModel<SoapMediaTypeCollection> {

	public SoapMediaTypeCollectionModel(){
	
		super();
	
		this.MEDIA_TYPE = new org.openspcoop2.protocol.manifest.model.SoapMediaTypeMappingModel(new Field("mediaType",org.openspcoop2.protocol.manifest.SoapMediaTypeMapping.class,"SoapMediaTypeCollection",SoapMediaTypeCollection.class));
		this.DEFAULT = new org.openspcoop2.protocol.manifest.model.SoapMediaTypeDefaultMappingModel(new Field("default",org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping.class,"SoapMediaTypeCollection",SoapMediaTypeCollection.class));
		this.UNDEFINED = new org.openspcoop2.protocol.manifest.model.SoapMediaTypeUndefinedMappingModel(new Field("undefined",org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping.class,"SoapMediaTypeCollection",SoapMediaTypeCollection.class));
	
	}
	
	public SoapMediaTypeCollectionModel(IField father){
	
		super(father);
	
		this.MEDIA_TYPE = new org.openspcoop2.protocol.manifest.model.SoapMediaTypeMappingModel(new ComplexField(father,"mediaType",org.openspcoop2.protocol.manifest.SoapMediaTypeMapping.class,"SoapMediaTypeCollection",SoapMediaTypeCollection.class));
		this.DEFAULT = new org.openspcoop2.protocol.manifest.model.SoapMediaTypeDefaultMappingModel(new ComplexField(father,"default",org.openspcoop2.protocol.manifest.SoapMediaTypeDefaultMapping.class,"SoapMediaTypeCollection",SoapMediaTypeCollection.class));
		this.UNDEFINED = new org.openspcoop2.protocol.manifest.model.SoapMediaTypeUndefinedMappingModel(new ComplexField(father,"undefined",org.openspcoop2.protocol.manifest.SoapMediaTypeUndefinedMapping.class,"SoapMediaTypeCollection",SoapMediaTypeCollection.class));
	
	}
	
	

	public org.openspcoop2.protocol.manifest.model.SoapMediaTypeMappingModel MEDIA_TYPE = null;
	 
	public org.openspcoop2.protocol.manifest.model.SoapMediaTypeDefaultMappingModel DEFAULT = null;
	 
	public org.openspcoop2.protocol.manifest.model.SoapMediaTypeUndefinedMappingModel UNDEFINED = null;
	 

	@Override
	public Class<SoapMediaTypeCollection> getModeledClass(){
		return SoapMediaTypeCollection.class;
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