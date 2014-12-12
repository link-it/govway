/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved.
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
package org.openspcoop2.generic_project.beans;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.SortOrder;

/**
 * Union
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Union {

	private boolean unionAll;
	
	private List<String> fields = new ArrayList<String>();
	private Hashtable<String, Function> mapFieldsToFunction = new Hashtable<String, Function>();
	private Hashtable<String, String> mapFieldsToAliasFunction = new Hashtable<String, String>();
	
	private SortOrder sortOrder = null;
	private List<String> orderByList = new ArrayList<String>();
	
	private List<String> groupByList = new ArrayList<String>();
	
	private Integer offset = null;
	private Integer limit = null;
	
	public boolean isUnionAll() {
		return this.unionAll;
	}
	public void setUnionAll(boolean unionAll) {
		this.unionAll = unionAll;
	}
	
	public List<String> getFields() {
		return this.fields;
	}
	public Function getFunction(String alias){
		return this.mapFieldsToFunction.get(alias);
	}
	public String getParamAliasFunction(String alias){
		return this.mapFieldsToAliasFunction.get(alias);
	}
	public void addField(String alias) throws ExpressionException{
		if(this.fields.contains(alias)){
			throw new ExpressionException("Alias["+alias+"] already used");
		}
		this.fields.add(alias);
	}
	public void addField(String alias,Function function,String functionParamAlias) throws ExpressionException{
		if(this.fields.contains(alias)){
			throw new ExpressionException("Alias["+alias+"] already used");
		}
		this.fields.add(alias);
		this.mapFieldsToFunction.put(alias, function);
		this.mapFieldsToAliasFunction.put(alias, functionParamAlias);
	}
	
	public SortOrder getSortOrder() {
		return this.sortOrder;
	}
	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	public void addOrderBy(String alias){
		this.orderByList.add(alias);
	}
	public List<String> getOrderByList() {
		return this.orderByList;
	}
	
	public void addGroupBy(String alias){
		this.groupByList.add(alias);
	}
	public List<String> getGroupByList() {
		return this.groupByList;
	}
	
	public Integer getOffset() {
		return this.offset;
	}
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	
	public Integer getLimit() {
		return this.limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
}
