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
package org.openspcoop2.protocol.information_missing.model;

import org.openspcoop2.protocol.information_missing.ConditionsType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ConditionsType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConditionsTypeModel extends AbstractModel<ConditionsType> {

	public ConditionsTypeModel(){
	
		super();
	
		this.PROPRIETA = new org.openspcoop2.protocol.information_missing.model.ConditionTypeModel(new Field("proprieta",org.openspcoop2.protocol.information_missing.ConditionType.class,"ConditionsType",ConditionsType.class));
		this.AND = new Field("and",boolean.class,"ConditionsType",ConditionsType.class);
		this.NOT = new Field("not",boolean.class,"ConditionsType",ConditionsType.class);
	
	}
	
	public ConditionsTypeModel(IField father){
	
		super(father);
	
		this.PROPRIETA = new org.openspcoop2.protocol.information_missing.model.ConditionTypeModel(new ComplexField(father,"proprieta",org.openspcoop2.protocol.information_missing.ConditionType.class,"ConditionsType",ConditionsType.class));
		this.AND = new ComplexField(father,"and",boolean.class,"ConditionsType",ConditionsType.class);
		this.NOT = new ComplexField(father,"not",boolean.class,"ConditionsType",ConditionsType.class);
	
	}
	
	

	public org.openspcoop2.protocol.information_missing.model.ConditionTypeModel PROPRIETA = null;
	 
	public IField AND = null;
	 
	public IField NOT = null;
	 

	@Override
	public Class<ConditionsType> getModeledClass(){
		return ConditionsType.class;
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