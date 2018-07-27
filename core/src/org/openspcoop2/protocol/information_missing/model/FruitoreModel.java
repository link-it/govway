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
package org.openspcoop2.protocol.information_missing.model;

import org.openspcoop2.protocol.information_missing.Fruitore;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Fruitore 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FruitoreModel extends AbstractModel<Fruitore> {

	public FruitoreModel(){
	
		super();
	
		this.CONDITIONS = new org.openspcoop2.protocol.information_missing.model.ConditionsTypeModel(new Field("conditions",org.openspcoop2.protocol.information_missing.ConditionsType.class,"fruitore",Fruitore.class));
		this.REPLACE_MATCH = new org.openspcoop2.protocol.information_missing.model.ReplaceFruitoreMatchTypeModel(new Field("replace-match",org.openspcoop2.protocol.information_missing.ReplaceFruitoreMatchType.class,"fruitore",Fruitore.class));
		this.HEADER = new org.openspcoop2.protocol.information_missing.model.DescriptionModel(new Field("header",org.openspcoop2.protocol.information_missing.Description.class,"fruitore",Fruitore.class));
		this.FOOTER = new org.openspcoop2.protocol.information_missing.model.DescriptionModel(new Field("footer",org.openspcoop2.protocol.information_missing.Description.class,"fruitore",Fruitore.class));
		this.DEFAULT = new org.openspcoop2.protocol.information_missing.model.DefaultModel(new Field("default",org.openspcoop2.protocol.information_missing.Default.class,"fruitore",Fruitore.class));
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"fruitore",Fruitore.class);
		this.TIPO = new Field("tipo",java.lang.String.class,"fruitore",Fruitore.class);
		this.STATO = new Field("stato",java.lang.String.class,"fruitore",Fruitore.class);
		this.PROTOCOLLO = new Field("protocollo",java.lang.String.class,"fruitore",Fruitore.class);
	
	}
	
	public FruitoreModel(IField father){
	
		super(father);
	
		this.CONDITIONS = new org.openspcoop2.protocol.information_missing.model.ConditionsTypeModel(new ComplexField(father,"conditions",org.openspcoop2.protocol.information_missing.ConditionsType.class,"fruitore",Fruitore.class));
		this.REPLACE_MATCH = new org.openspcoop2.protocol.information_missing.model.ReplaceFruitoreMatchTypeModel(new ComplexField(father,"replace-match",org.openspcoop2.protocol.information_missing.ReplaceFruitoreMatchType.class,"fruitore",Fruitore.class));
		this.HEADER = new org.openspcoop2.protocol.information_missing.model.DescriptionModel(new ComplexField(father,"header",org.openspcoop2.protocol.information_missing.Description.class,"fruitore",Fruitore.class));
		this.FOOTER = new org.openspcoop2.protocol.information_missing.model.DescriptionModel(new ComplexField(father,"footer",org.openspcoop2.protocol.information_missing.Description.class,"fruitore",Fruitore.class));
		this.DEFAULT = new org.openspcoop2.protocol.information_missing.model.DefaultModel(new ComplexField(father,"default",org.openspcoop2.protocol.information_missing.Default.class,"fruitore",Fruitore.class));
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"fruitore",Fruitore.class);
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"fruitore",Fruitore.class);
		this.STATO = new ComplexField(father,"stato",java.lang.String.class,"fruitore",Fruitore.class);
		this.PROTOCOLLO = new ComplexField(father,"protocollo",java.lang.String.class,"fruitore",Fruitore.class);
	
	}
	
	

	public org.openspcoop2.protocol.information_missing.model.ConditionsTypeModel CONDITIONS = null;
	 
	public org.openspcoop2.protocol.information_missing.model.ReplaceFruitoreMatchTypeModel REPLACE_MATCH = null;
	 
	public org.openspcoop2.protocol.information_missing.model.DescriptionModel HEADER = null;
	 
	public org.openspcoop2.protocol.information_missing.model.DescriptionModel FOOTER = null;
	 
	public org.openspcoop2.protocol.information_missing.model.DefaultModel DEFAULT = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField TIPO = null;
	 
	public IField STATO = null;
	 
	public IField PROTOCOLLO = null;
	 

	@Override
	public Class<Fruitore> getModeledClass(){
		return Fruitore.class;
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
