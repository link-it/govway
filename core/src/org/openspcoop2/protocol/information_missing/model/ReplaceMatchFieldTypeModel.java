/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

import org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ReplaceMatchFieldType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ReplaceMatchFieldTypeModel extends AbstractModel<ReplaceMatchFieldType> {

	public ReplaceMatchFieldTypeModel(){
	
		super();
	
		this.VALORE = new Field("valore",java.lang.String.class,"ReplaceMatchFieldType",ReplaceMatchFieldType.class);
		this.TIPO = new Field("tipo",java.lang.String.class,"ReplaceMatchFieldType",ReplaceMatchFieldType.class);
	
	}
	
	public ReplaceMatchFieldTypeModel(IField father){
	
		super(father);
	
		this.VALORE = new ComplexField(father,"valore",java.lang.String.class,"ReplaceMatchFieldType",ReplaceMatchFieldType.class);
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"ReplaceMatchFieldType",ReplaceMatchFieldType.class);
	
	}
	
	

	public IField VALORE = null;
	 
	public IField TIPO = null;
	 

	@Override
	public Class<ReplaceMatchFieldType> getModeledClass(){
		return ReplaceMatchFieldType.class;
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