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

import java.util.List;

import org.openspcoop2.generic_project.beans.ComplexField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.expression.impl.formatter.IObjectFormatter;

/**
 * InExpressionImpl
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InExpressionImpl extends AbstractCommonFieldBaseExpressionImpl {

	private List<Object> objects;
	
	public InExpressionImpl(IObjectFormatter objectFormatter,IField field,List<Object> objects){
		super(objectFormatter, field);
		this.objects = objects;
	}
	
	public List<Object> getObjects() {
		return this.objects;
	}

	public void setObjects(List<Object> objects) {
		this.objects = objects;
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
		bf.append(" IN (");
		for (int i = 0; i < this.objects.size(); i++) {
			bf.append(" ");
			if(i>0){
				bf.append(", ");
			}
			try{
				bf.append(super.getObjectFormatter().toString(this.objects.get(i)));
			}catch(Exception e){
				return "ERROR["+i+"]: "+e.getMessage();
			}
		}
		bf.append(" )"); // in
		bf.append(" )");
		if(isNot()){
			bf.append(" )");
		}
		return bf.toString();
	}
	


	@Override
	public List<Object> getFieldValues(IField field) throws ExpressionNotImplementedException, ExpressionException{
		List<Object> l = null;
		if(this.field==null){
			return l;
		}
		if(this.field.equals(field)){
			return this.objects;
		}
		return l;
	}


}
