/*
 * OpenSPCoop - Customizable API Gateway 
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
	private List<UnionOrderedColumn> orderByList = new ArrayList<UnionOrderedColumn>();
	
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
	
	public SortOrder getSortOrder() throws ExpressionException{
		if(
				( this.sortOrder==null || SortOrder.UNSORTED.equals(this.sortOrder) ) 
					&& 
				( this.orderByList!=null && this.orderByList.size()>0 )
			){
			// ritorno un sortOrder a caso tra i orderedFields, tanto poi viene usato sempre quello indicato per ogni field.
			for (UnionOrderedColumn unionOrderedColumn : this.orderByList) {
				if(unionOrderedColumn.getSortOrder()!=null){
					return unionOrderedColumn.getSortOrder();
				}
			}
			// Se non ho trovato un sort order ma sono state impostate condizioni di order by sollevo una eccezione.
			throw new ExpressionException("To add order by conditions must first be defined the sort order (by sortOrder method or as parameter by addOrderBy method)"); 
		}
		return this.sortOrder;
	}
	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	public void addOrderBy(String alias){
		UnionOrderedColumn uoo = new UnionOrderedColumn(alias);
		this.orderByList.add(uoo);
	}
	public void addOrderBy(String alias, SortOrder sortOrder) throws ExpressionException{
		if(sortOrder==null){
			throw new ExpressionException("Sort order parameter undefined"); 
		}
		if(SortOrder.UNSORTED.equals(sortOrder)){
			throw new ExpressionException("Sort order parameter not valid (use ASC or DESC)"); 
		}
		UnionOrderedColumn uoo = new UnionOrderedColumn(alias, sortOrder);
		this.orderByList.add(uoo);
	}
	public List<UnionOrderedColumn> getOrderByList() {
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
