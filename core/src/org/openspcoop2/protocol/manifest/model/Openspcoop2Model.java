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

import org.openspcoop2.protocol.manifest.Openspcoop2;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Openspcoop2 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Openspcoop2Model extends AbstractModel<Openspcoop2> {

	public Openspcoop2Model(){
	
		super();
	
		this.PROTOCOL = new org.openspcoop2.protocol.manifest.model.ProtocolModel(new Field("protocol",org.openspcoop2.protocol.manifest.Protocol.class,"openspcoop2",Openspcoop2.class));
		this.BINDING = new org.openspcoop2.protocol.manifest.model.BindingModel(new Field("binding",org.openspcoop2.protocol.manifest.Binding.class,"openspcoop2",Openspcoop2.class));
		this.WEB = new org.openspcoop2.protocol.manifest.model.WebModel(new Field("web",org.openspcoop2.protocol.manifest.Web.class,"openspcoop2",Openspcoop2.class));
		this.REGISTRY = new org.openspcoop2.protocol.manifest.model.RegistryModel(new Field("registry",org.openspcoop2.protocol.manifest.Registry.class,"openspcoop2",Openspcoop2.class));
		this.URL_MAPPING = new org.openspcoop2.protocol.manifest.model.UrlMappingModel(new Field("urlMapping",org.openspcoop2.protocol.manifest.UrlMapping.class,"openspcoop2",Openspcoop2.class));
	
	}
	
	public Openspcoop2Model(IField father){
	
		super(father);
	
		this.PROTOCOL = new org.openspcoop2.protocol.manifest.model.ProtocolModel(new ComplexField(father,"protocol",org.openspcoop2.protocol.manifest.Protocol.class,"openspcoop2",Openspcoop2.class));
		this.BINDING = new org.openspcoop2.protocol.manifest.model.BindingModel(new ComplexField(father,"binding",org.openspcoop2.protocol.manifest.Binding.class,"openspcoop2",Openspcoop2.class));
		this.WEB = new org.openspcoop2.protocol.manifest.model.WebModel(new ComplexField(father,"web",org.openspcoop2.protocol.manifest.Web.class,"openspcoop2",Openspcoop2.class));
		this.REGISTRY = new org.openspcoop2.protocol.manifest.model.RegistryModel(new ComplexField(father,"registry",org.openspcoop2.protocol.manifest.Registry.class,"openspcoop2",Openspcoop2.class));
		this.URL_MAPPING = new org.openspcoop2.protocol.manifest.model.UrlMappingModel(new ComplexField(father,"urlMapping",org.openspcoop2.protocol.manifest.UrlMapping.class,"openspcoop2",Openspcoop2.class));
	
	}
	
	

	public org.openspcoop2.protocol.manifest.model.ProtocolModel PROTOCOL = null;
	 
	public org.openspcoop2.protocol.manifest.model.BindingModel BINDING = null;
	 
	public org.openspcoop2.protocol.manifest.model.WebModel WEB = null;
	 
	public org.openspcoop2.protocol.manifest.model.RegistryModel REGISTRY = null;
	 
	public org.openspcoop2.protocol.manifest.model.UrlMappingModel URL_MAPPING = null;
	 

	@Override
	public Class<Openspcoop2> getModeledClass(){
		return Openspcoop2.class;
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