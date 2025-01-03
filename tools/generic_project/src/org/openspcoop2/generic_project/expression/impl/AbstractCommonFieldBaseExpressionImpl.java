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
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.expression.impl.formatter.IObjectFormatter;

/**
 * AbstractCommonFieldBaseExpressionImpl
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractCommonFieldBaseExpressionImpl extends AbstractBaseExpressionImpl {

	
	protected IField field;
	
	protected AbstractCommonFieldBaseExpressionImpl(IObjectFormatter objectFormatter,IField field){
		super(objectFormatter);
		this.field = field;
	}
	
	public IField getField() {
		return this.field;
	}
	public void setField(IField field) {
		this.field = field;
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
		List<IField> lista = null;
		if(this.field==null){
			return lista;
		}
		lista = new ArrayList<>();
		lista.add(this.field);
		return lista;
	}
}
