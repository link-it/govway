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
package org.openspcoop2.protocol.manifest.model;

import org.openspcoop2.protocol.manifest.RestMediaTypeDefaultMapping;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model RestMediaTypeDefaultMapping 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RestMediaTypeDefaultMappingModel extends AbstractModel<RestMediaTypeDefaultMapping> {

	public RestMediaTypeDefaultMappingModel(){
	
		super();
	
		this.MESSAGE_TYPE = new Field("messageType",java.lang.String.class,"RestMediaTypeDefaultMapping",RestMediaTypeDefaultMapping.class);
	
	}
	
	public RestMediaTypeDefaultMappingModel(IField father){
	
		super(father);
	
		this.MESSAGE_TYPE = new ComplexField(father,"messageType",java.lang.String.class,"RestMediaTypeDefaultMapping",RestMediaTypeDefaultMapping.class);
	
	}
	
	

	public IField MESSAGE_TYPE = null;
	 

	@Override
	public Class<RestMediaTypeDefaultMapping> getModeledClass(){
		return RestMediaTypeDefaultMapping.class;
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