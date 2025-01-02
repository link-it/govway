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
package org.openspcoop2.message.context.model;

import org.openspcoop2.message.context.HeaderParameters;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model HeaderParameters 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HeaderParametersModel extends AbstractModel<HeaderParameters> {

	public HeaderParametersModel(){
	
		super();
	
		this.HEADER_PARAMETER = new org.openspcoop2.message.context.model.StringParameterModel(new Field("header-parameter",org.openspcoop2.message.context.StringParameter.class,"header-parameters",HeaderParameters.class));
	
	}
	
	public HeaderParametersModel(IField father){
	
		super(father);
	
		this.HEADER_PARAMETER = new org.openspcoop2.message.context.model.StringParameterModel(new ComplexField(father,"header-parameter",org.openspcoop2.message.context.StringParameter.class,"header-parameters",HeaderParameters.class));
	
	}
	
	

	public org.openspcoop2.message.context.model.StringParameterModel HEADER_PARAMETER = null;
	 

	@Override
	public Class<HeaderParameters> getModeledClass(){
		return HeaderParameters.class;
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