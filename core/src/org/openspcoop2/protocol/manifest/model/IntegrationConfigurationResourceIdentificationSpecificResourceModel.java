/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model IntegrationConfigurationResourceIdentificationSpecificResource 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IntegrationConfigurationResourceIdentificationSpecificResourceModel extends AbstractModel<IntegrationConfigurationResourceIdentificationSpecificResource> {

	public IntegrationConfigurationResourceIdentificationSpecificResourceModel(){
	
		super();
	
		this.NAME = new org.openspcoop2.protocol.manifest.model.IntegrationConfigurationNameModel(new Field("name",org.openspcoop2.protocol.manifest.IntegrationConfigurationName.class,"IntegrationConfigurationResourceIdentificationSpecificResource",IntegrationConfigurationResourceIdentificationSpecificResource.class));
	
	}
	
	public IntegrationConfigurationResourceIdentificationSpecificResourceModel(IField father){
	
		super(father);
	
		this.NAME = new org.openspcoop2.protocol.manifest.model.IntegrationConfigurationNameModel(new ComplexField(father,"name",org.openspcoop2.protocol.manifest.IntegrationConfigurationName.class,"IntegrationConfigurationResourceIdentificationSpecificResource",IntegrationConfigurationResourceIdentificationSpecificResource.class));
	
	}
	
	

	public org.openspcoop2.protocol.manifest.model.IntegrationConfigurationNameModel NAME = null;
	 

	@Override
	public Class<IntegrationConfigurationResourceIdentificationSpecificResource> getModeledClass(){
		return IntegrationConfigurationResourceIdentificationSpecificResource.class;
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