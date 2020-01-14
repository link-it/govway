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

import org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model IntegrationConfigurationResourceIdentificationMode 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IntegrationConfigurationResourceIdentificationModeModel extends AbstractModel<IntegrationConfigurationResourceIdentificationMode> {

	public IntegrationConfigurationResourceIdentificationModeModel(){
	
		super();
	
		this.NAME = new Field("name",java.lang.String.class,"IntegrationConfigurationResourceIdentificationMode",IntegrationConfigurationResourceIdentificationMode.class);
		this.ONLY_ADVANCED_MODE = new Field("onlyAdvancedMode",boolean.class,"IntegrationConfigurationResourceIdentificationMode",IntegrationConfigurationResourceIdentificationMode.class);
	
	}
	
	public IntegrationConfigurationResourceIdentificationModeModel(IField father){
	
		super(father);
	
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"IntegrationConfigurationResourceIdentificationMode",IntegrationConfigurationResourceIdentificationMode.class);
		this.ONLY_ADVANCED_MODE = new ComplexField(father,"onlyAdvancedMode",boolean.class,"IntegrationConfigurationResourceIdentificationMode",IntegrationConfigurationResourceIdentificationMode.class);
	
	}
	
	

	public IField NAME = null;
	 
	public IField ONLY_ADVANCED_MODE = null;
	 

	@Override
	public Class<IntegrationConfigurationResourceIdentificationMode> getModeledClass(){
		return IntegrationConfigurationResourceIdentificationMode.class;
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