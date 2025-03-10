/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import org.openspcoop2.protocol.manifest.IntegrationConfigurationName;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model IntegrationConfigurationName 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IntegrationConfigurationNameModel extends AbstractModel<IntegrationConfigurationName> {

	public IntegrationConfigurationNameModel(){
	
		super();
	
		this.PARAM = new org.openspcoop2.protocol.manifest.model.IntegrationConfigurationElementNameModel(new Field("param",org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName.class,"IntegrationConfigurationName",IntegrationConfigurationName.class));
		this.USE_IN_URL = new Field("useInUrl",boolean.class,"IntegrationConfigurationName",IntegrationConfigurationName.class);
	
	}
	
	public IntegrationConfigurationNameModel(IField father){
	
		super(father);
	
		this.PARAM = new org.openspcoop2.protocol.manifest.model.IntegrationConfigurationElementNameModel(new ComplexField(father,"param",org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName.class,"IntegrationConfigurationName",IntegrationConfigurationName.class));
		this.USE_IN_URL = new ComplexField(father,"useInUrl",boolean.class,"IntegrationConfigurationName",IntegrationConfigurationName.class);
	
	}
	
	

	public org.openspcoop2.protocol.manifest.model.IntegrationConfigurationElementNameModel PARAM = null;
	 
	public IField USE_IN_URL = null;
	 

	@Override
	public Class<IntegrationConfigurationName> getModeledClass(){
		return IntegrationConfigurationName.class;
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