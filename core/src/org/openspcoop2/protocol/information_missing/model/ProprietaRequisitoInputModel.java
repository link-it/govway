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

import org.openspcoop2.protocol.information_missing.ProprietaRequisitoInput;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ProprietaRequisitoInput 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProprietaRequisitoInputModel extends AbstractModel<ProprietaRequisitoInput> {

	public ProprietaRequisitoInputModel(){
	
		super();
	
		this.CONDITIONS = new org.openspcoop2.protocol.information_missing.model.ConditionsTypeModel(new Field("conditions",org.openspcoop2.protocol.information_missing.ConditionsType.class,"ProprietaRequisitoInput",ProprietaRequisitoInput.class));
		this.HEADER = new org.openspcoop2.protocol.information_missing.model.DescriptionModel(new Field("header",org.openspcoop2.protocol.information_missing.Description.class,"ProprietaRequisitoInput",ProprietaRequisitoInput.class));
		this.FOOTER = new org.openspcoop2.protocol.information_missing.model.DescriptionModel(new Field("footer",org.openspcoop2.protocol.information_missing.Description.class,"ProprietaRequisitoInput",ProprietaRequisitoInput.class));
		this.TIPO = new Field("tipo",java.lang.String.class,"ProprietaRequisitoInput",ProprietaRequisitoInput.class);
		this.NOME = new Field("nome",java.lang.String.class,"ProprietaRequisitoInput",ProprietaRequisitoInput.class);
		this.LABEL = new Field("label",java.lang.String.class,"ProprietaRequisitoInput",ProprietaRequisitoInput.class);
		this.DEFAULT = new Field("default",java.lang.String.class,"ProprietaRequisitoInput",ProprietaRequisitoInput.class);
		this.USE_IN_DELETE = new Field("use-in-delete",boolean.class,"ProprietaRequisitoInput",ProprietaRequisitoInput.class);
		this.STEP_INCREMENT_CONDITION = new Field("step-increment-condition",java.lang.String.class,"ProprietaRequisitoInput",ProprietaRequisitoInput.class);
		this.STEP_INCREMENT = new Field("step-increment",int.class,"ProprietaRequisitoInput",ProprietaRequisitoInput.class);
		this.RELOAD_ON_CHANGE = new Field("reload-on-change",boolean.class,"ProprietaRequisitoInput",ProprietaRequisitoInput.class);
	
	}
	
	public ProprietaRequisitoInputModel(IField father){
	
		super(father);
	
		this.CONDITIONS = new org.openspcoop2.protocol.information_missing.model.ConditionsTypeModel(new ComplexField(father,"conditions",org.openspcoop2.protocol.information_missing.ConditionsType.class,"ProprietaRequisitoInput",ProprietaRequisitoInput.class));
		this.HEADER = new org.openspcoop2.protocol.information_missing.model.DescriptionModel(new ComplexField(father,"header",org.openspcoop2.protocol.information_missing.Description.class,"ProprietaRequisitoInput",ProprietaRequisitoInput.class));
		this.FOOTER = new org.openspcoop2.protocol.information_missing.model.DescriptionModel(new ComplexField(father,"footer",org.openspcoop2.protocol.information_missing.Description.class,"ProprietaRequisitoInput",ProprietaRequisitoInput.class));
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"ProprietaRequisitoInput",ProprietaRequisitoInput.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"ProprietaRequisitoInput",ProprietaRequisitoInput.class);
		this.LABEL = new ComplexField(father,"label",java.lang.String.class,"ProprietaRequisitoInput",ProprietaRequisitoInput.class);
		this.DEFAULT = new ComplexField(father,"default",java.lang.String.class,"ProprietaRequisitoInput",ProprietaRequisitoInput.class);
		this.USE_IN_DELETE = new ComplexField(father,"use-in-delete",boolean.class,"ProprietaRequisitoInput",ProprietaRequisitoInput.class);
		this.STEP_INCREMENT_CONDITION = new ComplexField(father,"step-increment-condition",java.lang.String.class,"ProprietaRequisitoInput",ProprietaRequisitoInput.class);
		this.STEP_INCREMENT = new ComplexField(father,"step-increment",int.class,"ProprietaRequisitoInput",ProprietaRequisitoInput.class);
		this.RELOAD_ON_CHANGE = new ComplexField(father,"reload-on-change",boolean.class,"ProprietaRequisitoInput",ProprietaRequisitoInput.class);
	
	}
	
	

	public org.openspcoop2.protocol.information_missing.model.ConditionsTypeModel CONDITIONS = null;
	 
	public org.openspcoop2.protocol.information_missing.model.DescriptionModel HEADER = null;
	 
	public org.openspcoop2.protocol.information_missing.model.DescriptionModel FOOTER = null;
	 
	public IField TIPO = null;
	 
	public IField NOME = null;
	 
	public IField LABEL = null;
	 
	public IField DEFAULT = null;
	 
	public IField USE_IN_DELETE = null;
	 
	public IField STEP_INCREMENT_CONDITION = null;
	 
	public IField STEP_INCREMENT = null;
	 
	public IField RELOAD_ON_CHANGE = null;
	 

	@Override
	public Class<ProprietaRequisitoInput> getModeledClass(){
		return ProprietaRequisitoInput.class;
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