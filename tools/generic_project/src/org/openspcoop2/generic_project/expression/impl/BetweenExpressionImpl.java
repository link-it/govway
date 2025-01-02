/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
public class BetweenExpressionImpl extends AbstractCommonFieldBaseExpressionImpl {

	private Object lower;
	private Object high;
	
	public BetweenExpressionImpl(IObjectFormatter objectFormatter,IField field,Object lower,Object high){
		super(objectFormatter, field);
		this.lower = lower;
		this.high = high;
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
	public List<Object> getFieldValues(IField field) throws ExpressionNotImplementedException, ExpressionException{
		List<Object> lista = null;
		if(this.field==null){
			return lista;
		}
		if(this.field.equals(field)){
			lista = new ArrayList<>();
			lista.add(this.lower);
			lista.add(this.high);
		}
		return lista;
	}
	
}
