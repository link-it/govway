/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.core.mvc.properties.model;

import org.openspcoop2.core.mvc.properties.ItemValue;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ItemValue 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ItemValueModel extends AbstractModel<ItemValue> {

	public ItemValueModel(){
	
		super();
	
		this.VALUE = new Field("value",java.lang.String.class,"itemValue",ItemValue.class);
		this.LABEL = new Field("label",java.lang.String.class,"itemValue",ItemValue.class);
	
	}
	
	public ItemValueModel(IField father){
	
		super(father);
	
		this.VALUE = new ComplexField(father,"value",java.lang.String.class,"itemValue",ItemValue.class);
		this.LABEL = new ComplexField(father,"label",java.lang.String.class,"itemValue",ItemValue.class);
	
	}
	
	

	public IField VALUE = null;
	 
	public IField LABEL = null;
	 

	@Override
	public Class<ItemValue> getModeledClass(){
		return ItemValue.class;
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