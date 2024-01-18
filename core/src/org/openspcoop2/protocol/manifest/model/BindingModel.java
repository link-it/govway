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

import org.openspcoop2.protocol.manifest.Binding;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Binding 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BindingModel extends AbstractModel<Binding> {

	public BindingModel(){
	
		super();
	
		this.SOAP = new org.openspcoop2.protocol.manifest.model.SoapConfigurationModel(new Field("soap",org.openspcoop2.protocol.manifest.SoapConfiguration.class,"binding",Binding.class));
		this.REST = new org.openspcoop2.protocol.manifest.model.RestConfigurationModel(new Field("rest",org.openspcoop2.protocol.manifest.RestConfiguration.class,"binding",Binding.class));
		this.DEFAULT = new Field("default",java.lang.String.class,"binding",Binding.class);
	
	}
	
	public BindingModel(IField father){
	
		super(father);
	
		this.SOAP = new org.openspcoop2.protocol.manifest.model.SoapConfigurationModel(new ComplexField(father,"soap",org.openspcoop2.protocol.manifest.SoapConfiguration.class,"binding",Binding.class));
		this.REST = new org.openspcoop2.protocol.manifest.model.RestConfigurationModel(new ComplexField(father,"rest",org.openspcoop2.protocol.manifest.RestConfiguration.class,"binding",Binding.class));
		this.DEFAULT = new ComplexField(father,"default",java.lang.String.class,"binding",Binding.class);
	
	}
	
	

	public org.openspcoop2.protocol.manifest.model.SoapConfigurationModel SOAP = null;
	 
	public org.openspcoop2.protocol.manifest.model.RestConfigurationModel REST = null;
	 
	public IField DEFAULT = null;
	 

	@Override
	public Class<Binding> getModeledClass(){
		return Binding.class;
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