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
import java.util.Iterator;
import java.util.List;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.expression.impl.formatter.IObjectFormatter;

/**
 * ConjunctionExpressionImpl
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConjunctionExpressionImpl extends AbstractBaseExpressionImpl {

	private List<AbstractBaseExpressionImpl> lista = new ArrayList<AbstractBaseExpressionImpl>();
	private boolean andConjunction = false;

	public ConjunctionExpressionImpl(IObjectFormatter objectFormatter){
		super(objectFormatter);
	}
	
	public void addExpression(AbstractBaseExpressionImpl xml){
		this.lista.add(xml);
	}
	
	public List<AbstractBaseExpressionImpl> getLista() {
		return this.lista;
	}

	public void setLista(List<AbstractBaseExpressionImpl> lista) {
		this.lista = lista;
	}

	public boolean isAndConjunction() {
		return this.andConjunction;
	}

	public void setAndConjunction(boolean andConjunction) {
		this.andConjunction = andConjunction;
	}

	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer();
		if(isNot()){
			bf.append("( NOT ");
		}
		bf.append("( ");
		int index = 0;
		for (Iterator<AbstractBaseExpressionImpl> iterator = this.lista.iterator(); iterator.hasNext();) {
			AbstractBaseExpressionImpl exp = iterator.next();
			if(index>0){
				if(this.andConjunction)
					bf.append(" AND ");
				else
					bf.append(" OR ");
			}
			bf.append(exp.toString());
			index++;
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
		if(this.lista==null){
			return false;
		}
		for (Iterator<AbstractBaseExpressionImpl> iterator = this.lista.iterator(); iterator.hasNext();) {
			AbstractBaseExpressionImpl expr = iterator.next();
			if(expr.inUseField(field)){
				return true;
			}			
		}
		return false;
	}

	@Override
	public List<Object> getFieldValues(IField field) throws ExpressionNotImplementedException, ExpressionException{
		if(this.lista==null){
			return null;
		}
		List<Object> fields = new ArrayList<Object>();
		for (Iterator<AbstractBaseExpressionImpl> iterator = this.lista.iterator(); iterator.hasNext();) {
			AbstractBaseExpressionImpl expr = iterator.next();
			List<Object> tmp = expr.getFieldValues(field);
			if(tmp!=null){
				fields.addAll(tmp);
			}			
		}
		if(fields.size()>0){
			return fields;
		}
		return null;
	}
		
	@Override
	public boolean inUseModel(IModel<?> model)
			throws ExpressionNotImplementedException, ExpressionException {
		if(this.lista==null){
			return false;
		}
		for (Iterator<AbstractBaseExpressionImpl> iterator = this.lista.iterator(); iterator.hasNext();) {
			AbstractBaseExpressionImpl expr = iterator.next();
			if(expr.inUseModel(model)){
				return true;
			}			
		}
		return false;
	}

	
	
	@Override
	public List<IField> getFields()
			throws ExpressionNotImplementedException, ExpressionException {
		if(this.lista==null){
			return null;
		}
		List<IField> fields = new ArrayList<IField>();
		for (Iterator<AbstractBaseExpressionImpl> iterator = this.lista.iterator(); iterator.hasNext();) {
			AbstractBaseExpressionImpl expr = iterator.next();
			List<IField> tmp = expr.getFields();
			if(tmp!=null){
				fields.addAll(tmp);
			}
		}
		if(fields.size()>0){
			return fields;
		}
		return null;
	}
}
