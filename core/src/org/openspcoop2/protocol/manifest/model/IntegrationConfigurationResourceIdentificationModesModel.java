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

import org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationModes;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model IntegrationConfigurationResourceIdentificationModes 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IntegrationConfigurationResourceIdentificationModesModel extends AbstractModel<IntegrationConfigurationResourceIdentificationModes> {

	public IntegrationConfigurationResourceIdentificationModesModel(){
	
		super();
	
		this.MODE = new org.openspcoop2.protocol.manifest.model.IntegrationConfigurationResourceIdentificationModeModel(new Field("mode",org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode.class,"IntegrationConfigurationResourceIdentificationModes",IntegrationConfigurationResourceIdentificationModes.class));
		this.DEFAULT = new Field("default",java.lang.String.class,"IntegrationConfigurationResourceIdentificationModes",IntegrationConfigurationResourceIdentificationModes.class);
		this.FORCE_INTERFACE_MODE = new Field("forceInterfaceMode",boolean.class,"IntegrationConfigurationResourceIdentificationModes",IntegrationConfigurationResourceIdentificationModes.class);
	
	}
	
	public IntegrationConfigurationResourceIdentificationModesModel(IField father){
	
		super(father);
	
		this.MODE = new org.openspcoop2.protocol.manifest.model.IntegrationConfigurationResourceIdentificationModeModel(new ComplexField(father,"mode",org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode.class,"IntegrationConfigurationResourceIdentificationModes",IntegrationConfigurationResourceIdentificationModes.class));
		this.DEFAULT = new ComplexField(father,"default",java.lang.String.class,"IntegrationConfigurationResourceIdentificationModes",IntegrationConfigurationResourceIdentificationModes.class);
		this.FORCE_INTERFACE_MODE = new ComplexField(father,"forceInterfaceMode",boolean.class,"IntegrationConfigurationResourceIdentificationModes",IntegrationConfigurationResourceIdentificationModes.class);
	
	}
	
	

	public org.openspcoop2.protocol.manifest.model.IntegrationConfigurationResourceIdentificationModeModel MODE = null;
	 
	public IField DEFAULT = null;
	 
	public IField FORCE_INTERFACE_MODE = null;
	 

	@Override
	public Class<IntegrationConfigurationResourceIdentificationModes> getModeledClass(){
		return IntegrationConfigurationResourceIdentificationModes.class;
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