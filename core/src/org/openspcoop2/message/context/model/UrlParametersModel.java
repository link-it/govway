/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.message.context.model;

import org.openspcoop2.message.context.UrlParameters;

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
	
		this.URL_PARAMETER = new org.openspcoop2.message.context.model.StringParameterModel(new Field("url-parameter",org.openspcoop2.message.context.StringParameter.class,"url-parameters",UrlParameters.class));
	
	}
	
	public UrlParametersModel(IField father){
	
		super(father);
	
		this.URL_PARAMETER = new org.openspcoop2.message.context.model.StringParameterModel(new ComplexField(father,"url-parameter",org.openspcoop2.message.context.StringParameter.class,"url-parameters",UrlParameters.class));
	
	}
	
	

	public org.openspcoop2.message.context.model.StringParameterModel URL_PARAMETER = null;
	 

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