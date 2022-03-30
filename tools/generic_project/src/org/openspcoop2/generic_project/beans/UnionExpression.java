/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.generic_project.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;

/**
 * UnionExpression
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UnionExpression {

	private IExpression expression;
	private Map<String,Object> returnFieldMap = new HashMap<String, Object>();
	private List<String> keys = new ArrayList<String>(); // contiene le keys della mappa per preservare l'ordine di inserimento.
	
	public UnionExpression(IExpression expression){
		this.expression = expression;
	}
	public UnionExpression(IPaginatedExpression paginatedExpression){
		this.expression = paginatedExpression;
	}

	public void addSelectField(IField field,String alias) throws ExpressionException{
		if(this.returnFieldMap.containsKey(alias)){
			throw new ExpressionException("Alias ["+alias+"] already used");
		}
		this.returnFieldMap.put(alias, field);
		this.keys.add(alias);
	}
	
	public void addSelectFunctionField(FunctionField functionField) throws ExpressionException{
		String alias = functionField.getAlias();
		if(this.returnFieldMap.containsKey(alias)){
			throw new ExpressionException("Alias ["+alias+"] already used");
		}
		this.returnFieldMap.put(alias, functionField);
		this.keys.add(alias);
	}
	
	public IExpression getExpression() {
		return this.expression;
	}
	public List<String> getReturnFieldAliases(){
		return this.keys;
	}
	public Object getReturnField(String alias){
		return this.returnFieldMap.get(alias);
	}
	
}
