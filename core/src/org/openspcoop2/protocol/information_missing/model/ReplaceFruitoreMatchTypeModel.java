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
package org.openspcoop2.protocol.information_missing.model;

import org.openspcoop2.protocol.information_missing.ReplaceFruitoreMatchType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ReplaceFruitoreMatchType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ReplaceFruitoreMatchTypeModel extends AbstractModel<ReplaceFruitoreMatchType> {

	public ReplaceFruitoreMatchTypeModel(){
	
		super();
	
		this.NOME = new org.openspcoop2.protocol.information_missing.model.ReplaceMatchFieldTypeModel(new Field("nome",org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType.class,"replaceFruitoreMatchType",ReplaceFruitoreMatchType.class));
		this.TIPO = new org.openspcoop2.protocol.information_missing.model.ReplaceMatchFieldTypeModel(new Field("tipo",org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType.class,"replaceFruitoreMatchType",ReplaceFruitoreMatchType.class));
		this.NOME_SERVIZIO = new org.openspcoop2.protocol.information_missing.model.ReplaceMatchFieldTypeModel(new Field("nome-servizio",org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType.class,"replaceFruitoreMatchType",ReplaceFruitoreMatchType.class));
		this.TIPO_SERVIZIO = new org.openspcoop2.protocol.information_missing.model.ReplaceMatchFieldTypeModel(new Field("tipo-servizio",org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType.class,"replaceFruitoreMatchType",ReplaceFruitoreMatchType.class));
		this.NOME_EROGATORE = new org.openspcoop2.protocol.information_missing.model.ReplaceMatchFieldTypeModel(new Field("nome-erogatore",org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType.class,"replaceFruitoreMatchType",ReplaceFruitoreMatchType.class));
		this.TIPO_EROGATORE = new org.openspcoop2.protocol.information_missing.model.ReplaceMatchFieldTypeModel(new Field("tipo-erogatore",org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType.class,"replaceFruitoreMatchType",ReplaceFruitoreMatchType.class));
	
	}
	
	public ReplaceFruitoreMatchTypeModel(IField father){
	
		super(father);
	
		this.NOME = new org.openspcoop2.protocol.information_missing.model.ReplaceMatchFieldTypeModel(new ComplexField(father,"nome",org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType.class,"replaceFruitoreMatchType",ReplaceFruitoreMatchType.class));
		this.TIPO = new org.openspcoop2.protocol.information_missing.model.ReplaceMatchFieldTypeModel(new ComplexField(father,"tipo",org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType.class,"replaceFruitoreMatchType",ReplaceFruitoreMatchType.class));
		this.NOME_SERVIZIO = new org.openspcoop2.protocol.information_missing.model.ReplaceMatchFieldTypeModel(new ComplexField(father,"nome-servizio",org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType.class,"replaceFruitoreMatchType",ReplaceFruitoreMatchType.class));
		this.TIPO_SERVIZIO = new org.openspcoop2.protocol.information_missing.model.ReplaceMatchFieldTypeModel(new ComplexField(father,"tipo-servizio",org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType.class,"replaceFruitoreMatchType",ReplaceFruitoreMatchType.class));
		this.NOME_EROGATORE = new org.openspcoop2.protocol.information_missing.model.ReplaceMatchFieldTypeModel(new ComplexField(father,"nome-erogatore",org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType.class,"replaceFruitoreMatchType",ReplaceFruitoreMatchType.class));
		this.TIPO_EROGATORE = new org.openspcoop2.protocol.information_missing.model.ReplaceMatchFieldTypeModel(new ComplexField(father,"tipo-erogatore",org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType.class,"replaceFruitoreMatchType",ReplaceFruitoreMatchType.class));
	
	}
	
	

	public org.openspcoop2.protocol.information_missing.model.ReplaceMatchFieldTypeModel NOME = null;
	 
	public org.openspcoop2.protocol.information_missing.model.ReplaceMatchFieldTypeModel TIPO = null;
	 
	public org.openspcoop2.protocol.information_missing.model.ReplaceMatchFieldTypeModel NOME_SERVIZIO = null;
	 
	public org.openspcoop2.protocol.information_missing.model.ReplaceMatchFieldTypeModel TIPO_SERVIZIO = null;
	 
	public org.openspcoop2.protocol.information_missing.model.ReplaceMatchFieldTypeModel NOME_EROGATORE = null;
	 
	public org.openspcoop2.protocol.information_missing.model.ReplaceMatchFieldTypeModel TIPO_EROGATORE = null;
	 

	@Override
	public Class<ReplaceFruitoreMatchType> getModeledClass(){
		return ReplaceFruitoreMatchType.class;
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