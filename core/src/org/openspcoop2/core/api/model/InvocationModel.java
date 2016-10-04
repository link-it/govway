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
package org.openspcoop2.core.api.model;

import org.openspcoop2.core.api.Invocation;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Invocation 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InvocationModel extends AbstractModel<Invocation> {

	public InvocationModel(){
	
		super();
	
		this.RESOURCE = new org.openspcoop2.core.api.model.ResourceModel(new Field("resource",org.openspcoop2.core.api.Resource.class,"invocation",Invocation.class));
		this.URL_PARAMETERS = new org.openspcoop2.core.api.model.UrlParametersModel(new Field("url-parameters",org.openspcoop2.core.api.UrlParameters.class,"invocation",Invocation.class));
		this.HEADER_PARAMETERS = new org.openspcoop2.core.api.model.HeaderParametersModel(new Field("header-parameters",org.openspcoop2.core.api.HeaderParameters.class,"invocation",Invocation.class));
	
	}
	
	public InvocationModel(IField father){
	
		super(father);
	
		this.RESOURCE = new org.openspcoop2.core.api.model.ResourceModel(new ComplexField(father,"resource",org.openspcoop2.core.api.Resource.class,"invocation",Invocation.class));
		this.URL_PARAMETERS = new org.openspcoop2.core.api.model.UrlParametersModel(new ComplexField(father,"url-parameters",org.openspcoop2.core.api.UrlParameters.class,"invocation",Invocation.class));
		this.HEADER_PARAMETERS = new org.openspcoop2.core.api.model.HeaderParametersModel(new ComplexField(father,"header-parameters",org.openspcoop2.core.api.HeaderParameters.class,"invocation",Invocation.class));
	
	}
	
	

	public org.openspcoop2.core.api.model.ResourceModel RESOURCE = null;
	 
	public org.openspcoop2.core.api.model.UrlParametersModel URL_PARAMETERS = null;
	 
	public org.openspcoop2.core.api.model.HeaderParametersModel HEADER_PARAMETERS = null;
	 

	@Override
	public Class<Invocation> getModeledClass(){
		return Invocation.class;
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