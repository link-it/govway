/*
 * OpenSPCoop - Customizable API Gateway 
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
package org.openspcoop2.protocol.information_missing.model;

import org.openspcoop2.protocol.information_missing.ReplaceMatchType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ReplaceMatchType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ReplaceMatchTypeModel extends AbstractModel<ReplaceMatchType> {

	public ReplaceMatchTypeModel(){
	
		super();
	
		this.NOME = new org.openspcoop2.protocol.information_missing.model.ReplaceMatchFieldTypeModel(new Field("nome",org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType.class,"replaceMatchType",ReplaceMatchType.class));
		this.TIPO = new org.openspcoop2.protocol.information_missing.model.ReplaceMatchFieldTypeModel(new Field("tipo",org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType.class,"replaceMatchType",ReplaceMatchType.class));
	
	}
	
	public ReplaceMatchTypeModel(IField father){
	
		super(father);
	
		this.NOME = new org.openspcoop2.protocol.information_missing.model.ReplaceMatchFieldTypeModel(new ComplexField(father,"nome",org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType.class,"replaceMatchType",ReplaceMatchType.class));
		this.TIPO = new org.openspcoop2.protocol.information_missing.model.ReplaceMatchFieldTypeModel(new ComplexField(father,"tipo",org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType.class,"replaceMatchType",ReplaceMatchType.class));
	
	}
	
	

	public org.openspcoop2.protocol.information_missing.model.ReplaceMatchFieldTypeModel NOME = null;
	 
	public org.openspcoop2.protocol.information_missing.model.ReplaceMatchFieldTypeModel TIPO = null;
	 

	@Override
	public Class<ReplaceMatchType> getModeledClass(){
		return ReplaceMatchType.class;
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