/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

import org.openspcoop2.protocol.manifest.IntegrationErrorCode;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model IntegrationErrorCode 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IntegrationErrorCodeModel extends AbstractModel<IntegrationErrorCode> {

	public IntegrationErrorCodeModel(){
	
		super();
	
		this.HTTP = new Field("http",int.class,"IntegrationErrorCode",IntegrationErrorCode.class);
		this.GOVWAY = new Field("govway",int.class,"IntegrationErrorCode",IntegrationErrorCode.class);
	
	}
	
	public IntegrationErrorCodeModel(IField father){
	
		super(father);
	
		this.HTTP = new ComplexField(father,"http",int.class,"IntegrationErrorCode",IntegrationErrorCode.class);
		this.GOVWAY = new ComplexField(father,"govway",int.class,"IntegrationErrorCode",IntegrationErrorCode.class);
	
	}
	
	

	public IField HTTP = null;
	 
	public IField GOVWAY = null;
	 

	@Override
	public Class<IntegrationErrorCode> getModeledClass(){
		return IntegrationErrorCode.class;
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