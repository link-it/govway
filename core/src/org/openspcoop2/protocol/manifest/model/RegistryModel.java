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

import org.openspcoop2.protocol.manifest.Registry;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Registry 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RegistryModel extends AbstractModel<Registry> {

	public RegistryModel(){
	
		super();
	
		this.ORGANIZATION = new org.openspcoop2.protocol.manifest.model.OrganizationModel(new Field("organization",org.openspcoop2.protocol.manifest.Organization.class,"registry",Registry.class));
		this.SERVICE = new org.openspcoop2.protocol.manifest.model.ServiceModel(new Field("service",org.openspcoop2.protocol.manifest.Service.class,"registry",Registry.class));
		this.VERSIONS = new org.openspcoop2.protocol.manifest.model.VersionsModel(new Field("versions",org.openspcoop2.protocol.manifest.Versions.class,"registry",Registry.class));
	
	}
	
	public RegistryModel(IField father){
	
		super(father);
	
		this.ORGANIZATION = new org.openspcoop2.protocol.manifest.model.OrganizationModel(new ComplexField(father,"organization",org.openspcoop2.protocol.manifest.Organization.class,"registry",Registry.class));
		this.SERVICE = new org.openspcoop2.protocol.manifest.model.ServiceModel(new ComplexField(father,"service",org.openspcoop2.protocol.manifest.Service.class,"registry",Registry.class));
		this.VERSIONS = new org.openspcoop2.protocol.manifest.model.VersionsModel(new ComplexField(father,"versions",org.openspcoop2.protocol.manifest.Versions.class,"registry",Registry.class));
	
	}
	
	

	public org.openspcoop2.protocol.manifest.model.OrganizationModel ORGANIZATION = null;
	 
	public org.openspcoop2.protocol.manifest.model.ServiceModel SERVICE = null;
	 
	public org.openspcoop2.protocol.manifest.model.VersionsModel VERSIONS = null;
	 

	@Override
	public Class<Registry> getModeledClass(){
		return Registry.class;
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