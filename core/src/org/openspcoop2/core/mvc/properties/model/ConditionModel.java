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
package org.openspcoop2.core.mvc.properties.model;

import org.openspcoop2.core.mvc.properties.Condition;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Condition 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConditionModel extends AbstractModel<Condition> {

	public ConditionModel(){
	
		super();
	
		this.DEFINED = new org.openspcoop2.core.mvc.properties.model.DefinedModel(new Field("defined",org.openspcoop2.core.mvc.properties.Defined.class,"condition",Condition.class));
		this.SELECTED = new org.openspcoop2.core.mvc.properties.model.SelectedModel(new Field("selected",org.openspcoop2.core.mvc.properties.Selected.class,"condition",Condition.class));
		this.EQUALS = new org.openspcoop2.core.mvc.properties.model.EqualsModel(new Field("equals",org.openspcoop2.core.mvc.properties.Equals.class,"condition",Condition.class));
		this.LESS_THEN = new org.openspcoop2.core.mvc.properties.model.EqualsModel(new Field("lessThen",org.openspcoop2.core.mvc.properties.Equals.class,"condition",Condition.class));
		this.LESS_EQUALS = new org.openspcoop2.core.mvc.properties.model.EqualsModel(new Field("lessEquals",org.openspcoop2.core.mvc.properties.Equals.class,"condition",Condition.class));
		this.GREATER_THEN = new org.openspcoop2.core.mvc.properties.model.EqualsModel(new Field("greaterThen",org.openspcoop2.core.mvc.properties.Equals.class,"condition",Condition.class));
		this.GREATER_EQUALS = new org.openspcoop2.core.mvc.properties.model.EqualsModel(new Field("greaterEquals",org.openspcoop2.core.mvc.properties.Equals.class,"condition",Condition.class));
		this.STARTS_WITH = new org.openspcoop2.core.mvc.properties.model.EqualsModel(new Field("startsWith",org.openspcoop2.core.mvc.properties.Equals.class,"condition",Condition.class));
		this.ENDS_WITH = new org.openspcoop2.core.mvc.properties.model.EqualsModel(new Field("endsWith",org.openspcoop2.core.mvc.properties.Equals.class,"condition",Condition.class));
		this.AND = new Field("and",boolean.class,"condition",Condition.class);
		this.NOT = new Field("not",boolean.class,"condition",Condition.class);
	
	}
	
	public ConditionModel(IField father){
	
		super(father);
	
		this.DEFINED = new org.openspcoop2.core.mvc.properties.model.DefinedModel(new ComplexField(father,"defined",org.openspcoop2.core.mvc.properties.Defined.class,"condition",Condition.class));
		this.SELECTED = new org.openspcoop2.core.mvc.properties.model.SelectedModel(new ComplexField(father,"selected",org.openspcoop2.core.mvc.properties.Selected.class,"condition",Condition.class));
		this.EQUALS = new org.openspcoop2.core.mvc.properties.model.EqualsModel(new ComplexField(father,"equals",org.openspcoop2.core.mvc.properties.Equals.class,"condition",Condition.class));
		this.LESS_THEN = new org.openspcoop2.core.mvc.properties.model.EqualsModel(new ComplexField(father,"lessThen",org.openspcoop2.core.mvc.properties.Equals.class,"condition",Condition.class));
		this.LESS_EQUALS = new org.openspcoop2.core.mvc.properties.model.EqualsModel(new ComplexField(father,"lessEquals",org.openspcoop2.core.mvc.properties.Equals.class,"condition",Condition.class));
		this.GREATER_THEN = new org.openspcoop2.core.mvc.properties.model.EqualsModel(new ComplexField(father,"greaterThen",org.openspcoop2.core.mvc.properties.Equals.class,"condition",Condition.class));
		this.GREATER_EQUALS = new org.openspcoop2.core.mvc.properties.model.EqualsModel(new ComplexField(father,"greaterEquals",org.openspcoop2.core.mvc.properties.Equals.class,"condition",Condition.class));
		this.STARTS_WITH = new org.openspcoop2.core.mvc.properties.model.EqualsModel(new ComplexField(father,"startsWith",org.openspcoop2.core.mvc.properties.Equals.class,"condition",Condition.class));
		this.ENDS_WITH = new org.openspcoop2.core.mvc.properties.model.EqualsModel(new ComplexField(father,"endsWith",org.openspcoop2.core.mvc.properties.Equals.class,"condition",Condition.class));
		this.AND = new ComplexField(father,"and",boolean.class,"condition",Condition.class);
		this.NOT = new ComplexField(father,"not",boolean.class,"condition",Condition.class);
	
	}
	
	

	public org.openspcoop2.core.mvc.properties.model.DefinedModel DEFINED = null;
	 
	public org.openspcoop2.core.mvc.properties.model.SelectedModel SELECTED = null;
	 
	public org.openspcoop2.core.mvc.properties.model.EqualsModel EQUALS = null;
	 
	public org.openspcoop2.core.mvc.properties.model.EqualsModel LESS_THEN = null;
	 
	public org.openspcoop2.core.mvc.properties.model.EqualsModel LESS_EQUALS = null;
	 
	public org.openspcoop2.core.mvc.properties.model.EqualsModel GREATER_THEN = null;
	 
	public org.openspcoop2.core.mvc.properties.model.EqualsModel GREATER_EQUALS = null;
	 
	public org.openspcoop2.core.mvc.properties.model.EqualsModel STARTS_WITH = null;
	 
	public org.openspcoop2.core.mvc.properties.model.EqualsModel ENDS_WITH = null;
	 
	public IField AND = null;
	 
	public IField NOT = null;
	 

	@Override
	public Class<Condition> getModeledClass(){
		return Condition.class;
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