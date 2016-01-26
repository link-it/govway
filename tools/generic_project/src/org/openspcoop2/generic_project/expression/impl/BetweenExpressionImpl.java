/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
 * BetweenExpressionImpl
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BetweenExpressionImpl extends AbstractBaseExpressionImpl {

	private IField field;
	private Object lower;
	private Object high;
	
	public BetweenExpressionImpl(IObjectFormatter objectFormatter,IField field,Object lower,Object high){
		super(objectFormatter);
		this.field = field;
		this.lower = lower;
		this.high = high;
	}
	
	public IField getField() {
		return this.field;
	}
	public void setField(IField field) {
		this.field = field;
	}

	public Object getLower() {
		return this.lower;
	}

	public void setLower(Object lower) {
		this.lower = lower;
	}

	public Object getHigh() {
		return this.high;
	}

	public void setHigh(Object high) {
		this.high = high;
	}
	
	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer();
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
		bf.append(" BETWEEN ");
		bf.append(" ");
		try{
			bf.append(super.getObjectFormatter().toString(this.lower));
		}catch(Exception e){
			return "ERROR-LOWER: "+e.getMessage();
		}
		bf.append(" AND ");
		try{
			bf.append(super.getObjectFormatter().toString(this.high));
		}catch(Exception e){
			return "ERROR-HIGH: "+e.getMessage();
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
			lista.add(this.lower);
			lista.add(this.high);
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
				return model.getModeledClass().getName().equals(this.field.getClassType().getName());
			}
		}
		else{
			return model.getModeledClass().getName().equals(this.field.getClassType().getName());
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
