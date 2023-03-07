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
package org.openspcoop2.generic_project.expression.impl;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.generic_project.beans.ComplexField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.expression.impl.formatter.IObjectFormatter;

/**
 * ComparatorExpressionImpl
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ComparatorExpressionImpl extends AbstractBaseExpressionImpl {

	private IField field;
	private Object object;
	private Comparator comparator;
	
	public ComparatorExpressionImpl(IObjectFormatter objectFormatter,IField field,Object object,Comparator comparator){
		super(objectFormatter);
		this.field = field;
		this.object = object;
		this.comparator = comparator;
	}
	
	public IField getField() {
		return this.field;
	}
	public void setField(IField field) {
		this.field = field;
	}
	public Object getObject() {
		return this.object;
	}
	public void setObject(Object object) {
		this.object = object;
	}
	public Comparator getComparator() {
		return this.comparator;
	}
	public void setComparator(Comparator comparator) {
		this.comparator = comparator;
	}
	
	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();
		if(isNot()){
			bf.append("( NOT ");
		}
		bf.append("( ");
		if(this.field instanceof ComplexField){
			ComplexField cf = (ComplexField) this.field;
			if(cf.getFather()!=null){
				bf.append(cf.getFather().getFieldName());
			}else{
				bf.append(this.field.getClassName());
			}
		}else{
			bf.append(this.field.getClassName());
		}
		bf.append(".");
		bf.append(this.field.getFieldName());
		bf.append(" ");
		bf.append(this.comparator.getOperatore());
		if(this.object!=null){
			bf.append(" ");
			try{
				bf.append(super.getObjectFormatter().toString(this.object));
			}catch(Exception e){
				return "ERROR: "+e.getMessage();
			}
		}
		bf.append(" )");
		if(isNot()){
			bf.append(" )");
		}
		return bf.toString();
	}

	@Override
	public boolean inUseField(IField field)
			throws ExpressionNotImplementedException, ExpressionException {
		if(this.field==null){
			return false;
		}
		return this.field.equals(field);
	}

	@Override
	public List<Object> getFieldValues(IField field) throws ExpressionNotImplementedException, ExpressionException{
		if(this.field==null){
			return null;
		}
		if(this.field.equals(field)){
			List<Object> lista = new ArrayList<Object>();
			lista.add(this.object);
			return lista;
		}
		return null;
	}
		
	@Override
	public boolean inUseModel(IModel<?> model)
			throws ExpressionNotImplementedException, ExpressionException {
		if(this.field==null){
			return false;
		}
		if(model.getBaseField()!=null){
			// Modello di un elemento non radice
			if(this.field instanceof ComplexField){
				ComplexField c = (ComplexField) this.field;
				return c.getFather().equals(model.getBaseField());
			}else{
				String modeClassName = model.getModeledClass().getName() + "";
				return modeClassName.equals(this.field.getClassType().getName());
			}
		}
		else{
			String modeClassName = model.getModeledClass().getName() + "";
			return modeClassName.equals(this.field.getClassType().getName());
		}
	}
	
	
	@Override
	public List<IField> getFields()
			throws ExpressionNotImplementedException, ExpressionException {
		if(this.field==null){
			return null;
		}
		List<IField> lista = new ArrayList<IField>();
		lista.add(this.field);
		return lista;
	}
}
