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

import org.openspcoop2.core.api.UrlParameters;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model UrlParameters 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UrlParametersModel extends AbstractModel<UrlParameters> {

	public UrlParametersModel(){
	
		super();
	
		this.URL_PARAMETER = new org.openspcoop2.core.api.model.UrlParameterModel(new Field("url-parameter",org.openspcoop2.core.api.UrlParameter.class,"url-parameters",UrlParameters.class));
	
	}
	
	public UrlParametersModel(IField father){
	
		super(father);
	
		this.URL_PARAMETER = new org.openspcoop2.core.api.model.UrlParameterModel(new ComplexField(father,"url-parameter",org.openspcoop2.core.api.UrlParameter.class,"url-parameters",UrlParameters.class));
	
	}
	
	

	public org.openspcoop2.core.api.model.UrlParameterModel URL_PARAMETER = null;
	 

	@Override
	public Class<UrlParameters> getModeledClass(){
		return UrlParameters.class;
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