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
package org.openspcoop2.generic_project.beans;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	private List<String> fields = new ArrayList<>();
	private Map<String, Function> mapFieldsToFunction = new HashMap<String, Function>();
	private Map<String, String> mapFieldsToAliasFunction = new HashMap<>();
	private Map<String, String> mapFieldsToCustomUnionField = new HashMap<>();
	
	private SortOrder sortOrder = null;
	private List<UnionOrderedColumn> orderByList = new ArrayList<UnionOrderedColumn>();
	
	private List<String> groupByList = new ArrayList<>();
	
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
	public String getCustomFieldValue(String alias) {
		return this.mapFieldsToCustomUnionField.get(alias);
	}
	public void addField(String alias) throws ExpressionException{
		if(this.fields.contains(alias)){
			throw new ExpressionException("Alias["+alias+"] already used");
		}
		this.fields.add(alias);
	}
	public void addCustomField(String alias,String customValue) throws ExpressionException{
		if(this.fields.contains(alias)){
			throw new ExpressionException("Alias["+alias+"] already used");
		}
		this.fields.add(alias);
		this.mapFieldsToCustomUnionField.put(alias, customValue);
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
