/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model IntegrationErrorConfiguration 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IntegrationErrorConfigurationModel extends AbstractModel<IntegrationErrorConfiguration> {

	public IntegrationErrorConfigurationModel(){
	
		super();
	
		this.INTERNAL = new org.openspcoop2.protocol.manifest.model.IntegrationErrorCollectionModel(new Field("internal",org.openspcoop2.protocol.manifest.IntegrationErrorCollection.class,"IntegrationErrorConfiguration",IntegrationErrorConfiguration.class));
		this.EXTERNAL = new org.openspcoop2.protocol.manifest.model.IntegrationErrorCollectionModel(new Field("external",org.openspcoop2.protocol.manifest.IntegrationErrorCollection.class,"IntegrationErrorConfiguration",IntegrationErrorConfiguration.class));
	
	}
	
	public IntegrationErrorConfigurationModel(IField father){
	
		super(father);
	
		this.INTERNAL = new org.openspcoop2.protocol.manifest.model.IntegrationErrorCollectionModel(new ComplexField(father,"internal",org.openspcoop2.protocol.manifest.IntegrationErrorCollection.class,"IntegrationErrorConfiguration",IntegrationErrorConfiguration.class));
		this.EXTERNAL = new org.openspcoop2.protocol.manifest.model.IntegrationErrorCollectionModel(new ComplexField(father,"external",org.openspcoop2.protocol.manifest.IntegrationErrorCollection.class,"IntegrationErrorConfiguration",IntegrationErrorConfiguration.class));
	
	}
	
	

	public org.openspcoop2.protocol.manifest.model.IntegrationErrorCollectionModel INTERNAL = null;
	 
	public org.openspcoop2.protocol.manifest.model.IntegrationErrorCollectionModel EXTERNAL = null;
	 

	@Override
	public Class<IntegrationErrorConfiguration> getModeledClass(){
		return IntegrationErrorConfiguration.class;
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