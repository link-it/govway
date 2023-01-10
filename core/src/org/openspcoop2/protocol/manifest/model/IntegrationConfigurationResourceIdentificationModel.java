/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

import org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentification;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model IntegrationConfigurationResourceIdentification 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IntegrationConfigurationResourceIdentificationModel extends AbstractModel<IntegrationConfigurationResourceIdentification> {

	public IntegrationConfigurationResourceIdentificationModel(){
	
		super();
	
		this.IDENTIFICATION_MODES = new org.openspcoop2.protocol.manifest.model.IntegrationConfigurationResourceIdentificationModesModel(new Field("identificationModes",org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes.class,"IntegrationConfigurationResourceIdentification",IntegrationConfigurationResourceIdentification.class));
		this.IDENTIFICATION_PARAMETER = new org.openspcoop2.protocol.manifest.model.IntegrationConfigurationNameModel(new Field("identificationParameter",org.openspcoop2.protocol.manifest.IntegrationConfigurationName.class,"IntegrationConfigurationResourceIdentification",IntegrationConfigurationResourceIdentification.class));
		this.SPECIFIC_RESOURCE = new org.openspcoop2.protocol.manifest.model.IntegrationConfigurationResourceIdentificationSpecificResourceModel(new Field("specificResource",org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource.class,"IntegrationConfigurationResourceIdentification",IntegrationConfigurationResourceIdentification.class));
	
	}
	
	public IntegrationConfigurationResourceIdentificationModel(IField father){
	
		super(father);
	
		this.IDENTIFICATION_MODES = new org.openspcoop2.protocol.manifest.model.IntegrationConfigurationResourceIdentificationModesModel(new ComplexField(father,"identificationModes",org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes.class,"IntegrationConfigurationResourceIdentification",IntegrationConfigurationResourceIdentification.class));
		this.IDENTIFICATION_PARAMETER = new org.openspcoop2.protocol.manifest.model.IntegrationConfigurationNameModel(new ComplexField(father,"identificationParameter",org.openspcoop2.protocol.manifest.IntegrationConfigurationName.class,"IntegrationConfigurationResourceIdentification",IntegrationConfigurationResourceIdentification.class));
		this.SPECIFIC_RESOURCE = new org.openspcoop2.protocol.manifest.model.IntegrationConfigurationResourceIdentificationSpecificResourceModel(new ComplexField(father,"specificResource",org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationSpecificResource.class,"IntegrationConfigurationResourceIdentification",IntegrationConfigurationResourceIdentification.class));
	
	}
	
	

	public org.openspcoop2.protocol.manifest.model.IntegrationConfigurationResourceIdentificationModesModel IDENTIFICATION_MODES = null;
	 
	public org.openspcoop2.protocol.manifest.model.IntegrationConfigurationNameModel IDENTIFICATION_PARAMETER = null;
	 
	public org.openspcoop2.protocol.manifest.model.IntegrationConfigurationResourceIdentificationSpecificResourceModel SPECIFIC_RESOURCE = null;
	 

	@Override
	public Class<IntegrationConfigurationResourceIdentification> getModeledClass(){
		return IntegrationConfigurationResourceIdentification.class;
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