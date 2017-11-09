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
		this.PROFILE = new org.openspcoop2.protocol.manifest.model.CollaborationProfileModel(new Field("profile",org.openspcoop2.protocol.manifest.CollaborationProfile.class,"Service",Service.class));
		this.FUNCTIONALITY = new org.openspcoop2.protocol.manifest.model.FunctionalityModel(new Field("functionality",org.openspcoop2.protocol.manifest.Functionality.class,"Service",Service.class));
	
	}
	
	public ServiceModel(IField father){
	
		super(father);
	
		this.TYPES = new org.openspcoop2.protocol.manifest.model.ServiceTypesModel(new ComplexField(father,"types",org.openspcoop2.protocol.manifest.ServiceTypes.class,"Service",Service.class));
		this.PROFILE = new org.openspcoop2.protocol.manifest.model.CollaborationProfileModel(new ComplexField(father,"profile",org.openspcoop2.protocol.manifest.CollaborationProfile.class,"Service",Service.class));
		this.FUNCTIONALITY = new org.openspcoop2.protocol.manifest.model.FunctionalityModel(new ComplexField(father,"functionality",org.openspcoop2.protocol.manifest.Functionality.class,"Service",Service.class));
	
	}
	
	

	public org.openspcoop2.protocol.manifest.model.ServiceTypesModel TYPES = null;
	 
	public org.openspcoop2.protocol.manifest.model.CollaborationProfileModel PROFILE = null;
	 
	public org.openspcoop2.protocol.manifest.model.FunctionalityModel FUNCTIONALITY = null;
	 

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