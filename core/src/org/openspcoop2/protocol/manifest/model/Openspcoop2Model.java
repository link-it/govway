/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
	
		this.PROTOCOL_NAME = new Field("protocolName",java.lang.String.class,"openspcoop2",Openspcoop2.class);
		this.FACTORY = new Field("factory",java.lang.String.class,"openspcoop2",Openspcoop2.class);
		this.WEB = new org.openspcoop2.protocol.manifest.model.WebModel(new Field("web",org.openspcoop2.protocol.manifest.Web.class,"openspcoop2",Openspcoop2.class));
		this.REGISTRO_SERVIZI = new org.openspcoop2.protocol.manifest.model.RegistroServiziModel(new Field("registroServizi",org.openspcoop2.protocol.manifest.RegistroServizi.class,"openspcoop2",Openspcoop2.class));
		this.URL_MAPPING = new org.openspcoop2.protocol.manifest.model.UrlMappingModel(new Field("urlMapping",org.openspcoop2.protocol.manifest.UrlMapping.class,"openspcoop2",Openspcoop2.class));
		this.BINDING = new org.openspcoop2.protocol.manifest.model.BindingModel(new Field("binding",org.openspcoop2.protocol.manifest.Binding.class,"openspcoop2",Openspcoop2.class));
	
	}
	
	public Openspcoop2Model(IField father){
	
		super(father);
	
		this.PROTOCOL_NAME = new ComplexField(father,"protocolName",java.lang.String.class,"openspcoop2",Openspcoop2.class);
		this.FACTORY = new ComplexField(father,"factory",java.lang.String.class,"openspcoop2",Openspcoop2.class);
		this.WEB = new org.openspcoop2.protocol.manifest.model.WebModel(new ComplexField(father,"web",org.openspcoop2.protocol.manifest.Web.class,"openspcoop2",Openspcoop2.class));
		this.REGISTRO_SERVIZI = new org.openspcoop2.protocol.manifest.model.RegistroServiziModel(new ComplexField(father,"registroServizi",org.openspcoop2.protocol.manifest.RegistroServizi.class,"openspcoop2",Openspcoop2.class));
		this.URL_MAPPING = new org.openspcoop2.protocol.manifest.model.UrlMappingModel(new ComplexField(father,"urlMapping",org.openspcoop2.protocol.manifest.UrlMapping.class,"openspcoop2",Openspcoop2.class));
		this.BINDING = new org.openspcoop2.protocol.manifest.model.BindingModel(new ComplexField(father,"binding",org.openspcoop2.protocol.manifest.Binding.class,"openspcoop2",Openspcoop2.class));
	
	}
	
	

	public IField PROTOCOL_NAME = null;
	 
	public IField FACTORY = null;
	 
	public org.openspcoop2.protocol.manifest.model.WebModel WEB = null;
	 
	public org.openspcoop2.protocol.manifest.model.RegistroServiziModel REGISTRO_SERVIZI = null;
	 
	public org.openspcoop2.protocol.manifest.model.UrlMappingModel URL_MAPPING = null;
	 
	public org.openspcoop2.protocol.manifest.model.BindingModel BINDING = null;
	 

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