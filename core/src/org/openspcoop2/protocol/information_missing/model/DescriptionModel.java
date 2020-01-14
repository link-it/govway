/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.information_missing.model;

import org.openspcoop2.protocol.information_missing.Description;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Description 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DescriptionModel extends AbstractModel<Description> {

	public DescriptionModel(){
	
		super();
	
		this.ITEM = new org.openspcoop2.protocol.information_missing.model.DescriptionTypeModel(new Field("item",org.openspcoop2.protocol.information_missing.DescriptionType.class,"Description",Description.class));
	
	}
	
	public DescriptionModel(IField father){
	
		super(father);
	
		this.ITEM = new org.openspcoop2.protocol.information_missing.model.DescriptionTypeModel(new ComplexField(father,"item",org.openspcoop2.protocol.information_missing.DescriptionType.class,"Description",Description.class));
	
	}
	
	

	public org.openspcoop2.protocol.information_missing.model.DescriptionTypeModel ITEM = null;
	 

	@Override
	public Class<Description> getModeledClass(){
		return Description.class;
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