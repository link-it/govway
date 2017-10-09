/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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

import org.openspcoop2.message.context.SerializedContext;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model SerializedContext 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SerializedContextModel extends AbstractModel<SerializedContext> {

	public SerializedContextModel(){
	
		super();
	
		this.PROPERTY = new org.openspcoop2.message.context.model.SerializedParameterModel(new Field("property",org.openspcoop2.message.context.SerializedParameter.class,"serialized-context",SerializedContext.class));
	
	}
	
	public SerializedContextModel(IField father){
	
		super(father);
	
		this.PROPERTY = new org.openspcoop2.message.context.model.SerializedParameterModel(new ComplexField(father,"property",org.openspcoop2.message.context.SerializedParameter.class,"serialized-context",SerializedContext.class));
	
	}
	
	

	public org.openspcoop2.message.context.model.SerializedParameterModel PROPERTY = null;
	 

	@Override
	public Class<SerializedContext> getModeledClass(){
		return SerializedContext.class;
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