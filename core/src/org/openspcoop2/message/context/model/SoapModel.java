/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

import org.openspcoop2.message.context.Soap;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Soap 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoapModel extends AbstractModel<Soap> {

	public SoapModel(){
	
		super();
	
		this.SOAP_ACTION = new Field("soap-action",java.lang.String.class,"soap",Soap.class);
	
	}
	
	public SoapModel(IField father){
	
		super(father);
	
		this.SOAP_ACTION = new ComplexField(father,"soap-action",java.lang.String.class,"soap",Soap.class);
	
	}
	
	

	public IField SOAP_ACTION = null;
	 

	@Override
	public Class<Soap> getModeledClass(){
		return Soap.class;
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