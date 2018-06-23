/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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
package org.openspcoop2.protocol.manifest.model;

import org.openspcoop2.protocol.manifest.IntegrationError;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model IntegrationError 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IntegrationErrorModel extends AbstractModel<IntegrationError> {

	public IntegrationErrorModel(){
	
		super();
	
		this.HTTP_RETURN_CODE = new Field("httpReturnCode",int.class,"IntegrationError",IntegrationError.class);
		this.MESSAGE_TYPE = new Field("messageType",java.lang.String.class,"IntegrationError",IntegrationError.class);
	
	}
	
	public IntegrationErrorModel(IField father){
	
		super(father);
	
		this.HTTP_RETURN_CODE = new ComplexField(father,"httpReturnCode",int.class,"IntegrationError",IntegrationError.class);
		this.MESSAGE_TYPE = new ComplexField(father,"messageType",java.lang.String.class,"IntegrationError",IntegrationError.class);
	
	}
	
	

	public IField HTTP_RETURN_CODE = null;
	 
	public IField MESSAGE_TYPE = null;
	 

	@Override
	public Class<IntegrationError> getModeledClass(){
		return IntegrationError.class;
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