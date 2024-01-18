/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import org.openspcoop2.protocol.manifest.Integration;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Integration 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IntegrationModel extends AbstractModel<Integration> {

	public IntegrationModel(){
	
		super();
	
		this.IMPLEMENTATION = new org.openspcoop2.protocol.manifest.model.IntegrationConfigurationModel(new Field("implementation",org.openspcoop2.protocol.manifest.IntegrationConfiguration.class,"Integration",Integration.class));
		this.SUBSCRIPTION = new org.openspcoop2.protocol.manifest.model.IntegrationConfigurationModel(new Field("subscription",org.openspcoop2.protocol.manifest.IntegrationConfiguration.class,"Integration",Integration.class));
	
	}
	
	public IntegrationModel(IField father){
	
		super(father);
	
		this.IMPLEMENTATION = new org.openspcoop2.protocol.manifest.model.IntegrationConfigurationModel(new ComplexField(father,"implementation",org.openspcoop2.protocol.manifest.IntegrationConfiguration.class,"Integration",Integration.class));
		this.SUBSCRIPTION = new org.openspcoop2.protocol.manifest.model.IntegrationConfigurationModel(new ComplexField(father,"subscription",org.openspcoop2.protocol.manifest.IntegrationConfiguration.class,"Integration",Integration.class));
	
	}
	
	

	public org.openspcoop2.protocol.manifest.model.IntegrationConfigurationModel IMPLEMENTATION = null;
	 
	public org.openspcoop2.protocol.manifest.model.IntegrationConfigurationModel SUBSCRIPTION = null;
	 

	@Override
	public Class<Integration> getModeledClass(){
		return Integration.class;
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