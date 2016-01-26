/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

import org.openspcoop2.core.api.HeaderParameter;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model HeaderParameter 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HeaderParameterModel extends AbstractModel<HeaderParameter> {

	public HeaderParameterModel(){
	
		super();
	
		this.BASE = new Field("base",java.lang.String.class,"header-parameter",HeaderParameter.class);
		this.NOME = new Field("nome",java.lang.String.class,"header-parameter",HeaderParameter.class);
	
	}
	
	public HeaderParameterModel(IField father){
	
		super(father);
	
		this.BASE = new ComplexField(father,"base",java.lang.String.class,"header-parameter",HeaderParameter.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"header-parameter",HeaderParameter.class);
	
	}
	
	

	public IField BASE = null;
	 
	public IField NOME = null;
	 

	@Override
	public Class<HeaderParameter> getModeledClass(){
		return HeaderParameter.class;
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