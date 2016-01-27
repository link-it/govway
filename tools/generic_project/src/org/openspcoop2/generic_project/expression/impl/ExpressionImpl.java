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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openspcoop2.generic_project.beans.ComplexField;
import org.openspcoop2.generic_project.beans.ConstantField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotFoundException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.LikeMode;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.generic_project.expression.impl.formatter.IObjectFormatter;
import org.openspcoop2.generic_project.expression.impl.formatter.ObjectFormatter;

/**
 * ExpressionImpl
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExpressionImpl implements IExpression {

	private boolean throwExpressionNotInitialized = false;
	
	protected IObjectFormatter objectFormatter;
	public IObjectFormatter getObjectFormatter() {
		return this.objectFormatter;
	}
	public void setObjectFormatter(IObjectFormatter objectFormatter) {
		this.objectFormatter = objectFormatter;
	}
	
	public ExpressionImpl() throws ExpressionException {
		this.objectFormatter = new ObjectFormatter();
	}
	public ExpressionImpl(IObjectFormatter objectFormatter) throws ExpressionException{
		if(objectFormatter==null){ 
			throw new ExpressionException("IObjectFormatter parameter is null");
		}
		this.objectFormatter = objectFormatter;
	}
	public ExpressionImpl(ExpressionImpl expr){
		this.andLogicOperator = expr.andLogicOperator;
		this.expressionEngine = expr.expressionEngine;
		this.notOperator = expr.notOperator;
		this.objectFormatter = expr.objectFormatter;
		this.orderedFields = expr.orderedFields;
		this._sortOrder = expr.getSortOrder();
		this.groupByFields = expr.groupByFields;
		this.throwExpressionNotInitialized = expr.throwExpressionNotInitialized;
	}
	
	protected boolean andLogicOperator = true;
	
	protected AbstractBaseExpressionImpl expressionEngine;
	public AbstractBaseExpressionImpl getExpressionEngine() {
		return this.expressionEngine;
	}
	
	protected boolean notOperator = false;
	
	private SortOrder _sortOrder = SortOrder.UNSORTED;
	protected List<OrderedField> orderedFields = new ArrayList<OrderedField>();
	
	protected List<IField> groupByFields = new ArrayList<IField>();
	
	
	
	
	/* ************ STATE OF EXPRESSION *********** */
	
	/**
	 * Make the negation of the expression
	 * 
	 * @return the instance of itself
	 * @throws ExpressionNotImplementedException Method not implemented
	 * @throws ExpressionException Error Processing
	 */
	@Override
	public IExpression not() throws ExpressionNotImplementedException,ExpressionException{
		/*if(this.expressionEngine==null){
			throw new ExpressionException("You can not use the NOT operation on an uninitialized expression");
		}*/
		this.notOperator = true;
		if(this.expressionEngine!=null){
			this.expressionEngine.setNot(true);
		}
		return this;
	}
	
	/**
	 * Use the conjunction "AND" for all expressions
	 * 
	 * @return the instance of itself
	 * @throws ExpressionNotImplementedException Method not implemented
	 * @throws ExpressionException Error Processing
	 */
	@Override
	public IExpression and() throws ExpressionNotImplementedException, ExpressionException{
		this.andLogicOperator = true;
		if(this.expressionEngine!=null){
			if(this.expressionEngine instanceof ConjunctionExpressionImpl){
				((ConjunctionExpressionImpl)this.expressionEngine).setAndConjunction(this.andLogicOperator);
			}
		}
		return this;
	}

	/**
	 * Use the conjunction "OR" for all expressions
	 * 
	 * @return the instance of itself
	 * @throws ExpressionNotImplementedException Method not implemented
	 * @throws ExpressionException Error Processing
	 */
	@Override
	public IExpression or() throws ExpressionNotImplementedException, ExpressionException{
		this.andLogicOperator = false;
		if(this.expressionEngine!=null){
			if(this.expressionEngine instanceof ConjunctionExpressionImpl){
				((ConjunctionExpressionImpl)this.expressionEngine).setAndConjunction(this.andLogicOperator);
			}
		}
		return this;
	}
	
	
	
	
	/* ************ COMPARATOR *********** */
	
	/**
	 * Create an expression of equality
	 * Example:  (field = value)
	 * 
	 * @param field Resource identifier
	 * @param value Value
	 * @return Expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	@Override
	public IExpression equals(IField field, Object value)
		throws ExpressionNotImplementedException, ExpressionException {
		checkArgoments(field, value);
		this.buildComparatorExpression(field, value, Comparator.EQUALS);
		return this;
	}
	
	/**
	 * Create an expression of inequality
	 * Example:  (field <> value)
	 * 
	 * @param field Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	@Override
	public IExpression notEquals(IField field, Object value)
	throws ExpressionNotImplementedException, ExpressionException {
		checkArgoments(field, value);
		this.buildComparatorExpression(field, value, Comparator.NOT_EQUALS);
		return this;
	}

	/**
	 * Create an expression of equality to each resource in the collection of the keys of the Map
	 * Use the conjunction "AND" for all expressions
	 * Example:  ( field[0]=values[0] AND field[1]=values[1] ..... AND field[N]=values[N] )
	 * 
	 * @param propertyNameValues Map that contains identifiers and their values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	@Override
	public IExpression allEquals(Map<IField, Object> propertyNameValues)
	throws ExpressionNotImplementedException, ExpressionException {
		checkArgoments(propertyNameValues);
		buildComparatorExpression(propertyNameValues, Comparator.EQUALS, true);
		return this;
	}

	/**
	 * Create an expression of inequality to each resource in the collection of the keys of the Map
	 * Use the conjunction "AND" for all expressions
	 * Example:  ( field[0]<>values[0] AND field[1]<>values[1] ..... AND field[N]<>values[N] )
	 * 
	 * @param propertyNameValues Map that contains identifiers and their values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	@Override
	public IExpression allNotEquals(Map<IField, Object> propertyNameValues)
	throws ExpressionNotImplementedException, ExpressionException {
		checkArgoments(propertyNameValues);
		buildComparatorExpression(propertyNameValues, Comparator.NOT_EQUALS, true);
		return this;
	}

	/**
	 * Create an expression of equality to each resource in the collection of the keys of the Map
	 * Use the conjunction defined by <var>andConjunction</var> for all expressions
	 * Example:  ( field[0]=values[0] <var>andConjunction</var> field[1]=values[1] ..... <var>andConjunction</var> field[N]=values[N] )
	 * 
	 * @param propertyNameValues Map that contains identifiers and their values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	@Override
	public IExpression allEquals(Map<IField, Object> propertyNameValues,boolean andConjunction)
	throws ExpressionNotImplementedException, ExpressionException {
		checkArgoments(propertyNameValues);
		buildComparatorExpression(propertyNameValues, Comparator.EQUALS, andConjunction);
		return this;
	}

	/**
	 * Create an expression of inequality to each resource in the collection of the keys of the Map
	 * Use the conjunction defined by <var>andConjunction</var> for all expressions
	 * Example:  ( field[0]<>values[0] <var>andConjunction</var> field[1]<>values[1] ..... <var>andConjunction</var> field[N]<>values[N] )
	 * 
	 * @param propertyNameValues Map that contains identifiers and their values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	@Override
	public IExpression allNotEquals(Map<IField, Object> propertyNameValues,boolean andConjunction)
	throws ExpressionNotImplementedException, ExpressionException {
		checkArgoments(propertyNameValues);
		buildComparatorExpression(propertyNameValues, Comparator.NOT_EQUALS, andConjunction);
		return this;
	}
	
	/**
	 * Create an expression "greaterThan"
	 * Example:  (field > value)
	 * 
	 * @param field Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	@Override
	public IExpression greaterThan(IField field, Object value)
	throws ExpressionNotImplementedException, ExpressionException {
		checkArgoments(field, value);
		this.buildComparatorExpression(field, value, Comparator.GREATER_THAN);
		return this;
	}

	/**
	 * Create an expression "greaterEquals"
	 * Example:  (field >= value)
	 * 
	 * @param field Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	@Override
	public IExpression greaterEquals(IField field, Object value)
	throws ExpressionNotImplementedException, ExpressionException {
		checkArgoments(field, value);
		this.buildComparatorExpression(field, value, Comparator.GREATER_EQUALS);
		return this;
	}

	/**
	 * Create an expression "lessThan"
	 * Example:  (field < value)
	 * 
	 * @param field Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	@Override
	public IExpression lessThan(IField field, Object value)
	throws ExpressionNotImplementedException, ExpressionException {
		checkArgoments(field, value);
		this.buildComparatorExpression(field, value, Comparator.LESS_THAN);
		return this;
	}

	/**
	 * Create an expression "lessEquals"
	 * Example:  (field <= value)
	 * 
	 * @param field Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	@Override
	public IExpression lessEquals(IField field, Object value)
	throws ExpressionNotImplementedException, ExpressionException {
		checkArgoments(field, value);
		this.buildComparatorExpression(field, value, Comparator.LESS_EQUALS);
		return this;
	}

	
	
	
	/* ************ SPECIAL COMPARATOR *********** */
	
	/**
	 * Create an expression "is null"
	 * Example:  ( field is null )
	 * 
	 * @param field Resource identifier
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	@Override
	public IExpression isNull(IField field) throws ExpressionNotImplementedException,ExpressionException{
		checkArgoments(field,false);
		this.buildComparatorExpression(field, null, Comparator.IS_NULL);
		return this;
	}
	
	/**
	 * Create an expression "is not null"
	 * Example:  ( field is not null )
	 * 
	 * @param field Resource identifier
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	@Override
	public IExpression isNotNull(IField field) throws ExpressionNotImplementedException,ExpressionException{
		checkArgoments(field,false);
		this.buildComparatorExpression(field, null, Comparator.IS_NOT_NULL);
		return this;
	}
	
	/**
	 * Create an expression "is empty"
	 * Example:  ( field = '' )
	 * 
	 * @param field Resource identifier
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	@Override
	public IExpression isEmpty(IField field) throws ExpressionNotImplementedException,ExpressionException{
		checkArgoments(field,false);
		this.buildComparatorExpression(field, null, Comparator.IS_EMPTY);
		return this;
	}

	/**
	 * Create an expression "is not empty"
	 * Example:  ( field <> '' )
	 * 
	 * @param field Resource identifier
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	@Override
	public IExpression isNotEmpty(IField field) throws ExpressionNotImplementedException,ExpressionException{
		checkArgoments(field,false);
		this.buildComparatorExpression(field, null, Comparator.IS_NOT_EMPTY);
		return this;
	}
	
	
	
	
	
	/* ************ BETWEEN *********** */
	
	/**
	 * Create an expression "between"
	 * Example:  ( field BEETWEEN lower AND high )
	 * 
	 * @param field Resource identifier
	 * @param lower Lower limit value to be applied to the constraint
	 * @param high Higher limit value to be applied to the constraint
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	@Override
	public IExpression between(IField field, Object lower, Object high)
	throws ExpressionNotImplementedException, ExpressionException {
		checkArgoments(field, lower, high);
		buildBeetweenExpression(field, lower, high);
		return this;
	}

	
	
	
	/* ************ LIKE *********** */
	
	/**
	 * Create an expression "like"
	 * Example:  ( field like '%value%' )
	 * 
	 * @param field Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	@Override
	public IExpression like(IField field, String value)
	throws ExpressionNotImplementedException, ExpressionException {
		checkArgoments(field, value);
		buildLikeExpression(field, value, LikeMode.ANYWHERE, false);
		return this;
	}

	/**
	 * Create an expression "like"
	 * Example:  
	 * 	 LikeMode.ANYWHERE -> ( field like '%value%' )
	 *   LikeMode.EXACT -> ( field like 'value' )
	 *   LikeMode.END -> ( field like '%value' )
	 *   LikeMode.START -> ( field like 'value%' )
	 * 
	 * @param field Resource identifier
	 * @param value Value
	 * @param mode LikeMode
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	@Override
	public IExpression like(IField field, String value, LikeMode mode)
	throws ExpressionNotImplementedException, ExpressionException {
		checkArgoments(field, value);
		buildLikeExpression(field, value, mode, false);
		return this;
	}

	/**
	 * Create an expression "like" case-insensitive
	 * Example:  ( toLower(field) like '%toLower(value)%' )  
	 * 
	 * @param field Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	@Override
	public IExpression ilike(IField field, String value)
	throws ExpressionNotImplementedException, ExpressionException {
		checkArgoments(field, value);
		buildLikeExpression(field, value, LikeMode.ANYWHERE, true);
		return this;
	}

	/**
	 * Create an expression "like" case-insensitive
	 * Example:  
	 *   LikeMode.ANYWHERE -> ( toLower(field) like '%toLower(value)%' )
	 *   LikeMode.EXACT -> ( toLower(field) like 'toLower(value)' )
	 *   LikeMode.END -> ( toLower(field) like '%toLower(value)' )
	 *   LikeMode.START -> ( toLower(field) like 'toLower(value)%' )
	 * 
	 * @param field Resource identifier
	 * @param value Value
	 * @param mode LikeMode
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	@Override
	public IExpression ilike(IField field, String value, LikeMode mode)
	throws ExpressionNotImplementedException, ExpressionException {
		checkArgoments(field, value);
		buildLikeExpression(field, value, mode, true);
		return this;
	}

	
	
	
	
	
	/* ************ IN *********** */
	
	/**
	 * Create an expression "in" verifying that the value of <var>field</var> is one of those given in the param <var>values</var>
	 * Example:  ( field IN ('values[0]', 'values[1]', ... ,'values[n]')  )
	 * 
	 * @param field Resource identifier
	 * @param values Values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	@Override
	public IExpression in(IField field, Object... values)
	throws ExpressionNotImplementedException, ExpressionException {
		checkArgoments(field, values);
		buildInExpression(field, values);
		return this;
	}

	/**
	 * Create an expression "in" verifying that the value of <var>field</var> is one of those given in the param <var>values</var>
	 * Example:  ( field IN ('values[0]', 'values[1]', ... ,'values[n]')  )
	 * 
	 * @param field Resource identifier
	 * @param values Values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	@Override
	public IExpression in(IField field, Collection<?> values)
	throws ExpressionNotImplementedException, ExpressionException {
		if(values==null){
			throw new ExpressionException("Values is null");
		}
		checkArgoments(field,  values.toArray(new Object[1]));
		buildInExpression(field, values.toArray(new Object[1]));
		return this;
	}



	
	
	
	/* ************ CONJUNCTION *********** */
	
	/**
	 *  Adds to the expression in the conjunction "AND" of the espressions given as a parameters
	 *  Example: ( exp1 AND exp2  )
	 *  
	 * @param exp1 Filter combined in AND with <var>exp1</var>
	 * @param exp2 Filter combined in AND with <var>exp2</var>
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	@Override
	public IExpression and(IExpression exp1, IExpression exp2)
	throws ExpressionNotImplementedException, ExpressionException {
		IExpression [] expr = new IExpression[2];
		expr[0] = exp1;
		expr[1] = exp2;
		return this.and(expr);
	}

	/**
	 *  Adds to the expression in the conjunction "AND" of the espressions given as a parameters
	 *  Example: ( expressions[1] AND expressions[2] AND ... expressions[N] )
	 *  
	 * @param expressions Expressions combined in AND
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	@Override
	public IExpression and(IExpression... expressions)
		throws ExpressionNotImplementedException, ExpressionException {
		checkConjunctionOperation(expressions);
		buildConjunctionExpression(true, expressions);
		return this;
	}

	/**
	 *  Adds to the expression in the conjunction "OR" of the espressions given as a parameters
	 *  Example: ( exp1 OR exp2  )
	 *  
	 * @param exp1 Filter combined in OR with <var>exp1</var>
	 * @param exp2 Filter combined in OR with <var>exp2</var>
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	@Override
	public IExpression or(IExpression exp1, IExpression exp2)
	throws ExpressionNotImplementedException, ExpressionException {
		IExpression [] expr = new IExpression[2];
		expr[0] = exp1;
		expr[1] = exp2;
		return this.or(expr);
	}

	/**
	 *  Adds to the expression in the conjunction "OR" of the espressions given as a parameters
	 *  Example: ( expressions[1] OR expressions[2] OR ... expressions[N] )
	 *  
	 * @param expressions Expressions combined in OR
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	@Override
	public IExpression or(IExpression... expressions)
	throws ExpressionNotImplementedException, ExpressionException {
		checkConjunctionOperation(expressions);
		buildConjunctionExpression(false, expressions);
		return this;
	}


	
	
	
	/* ************ NEGATION *********** */
	
	/**
	 * Adding to the negation of the expression passed as parameter
	 * Example: ( NOT expression )
	 * 
	 * @param expression Expression
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	@Override
	public IExpression not(IExpression expression)
	throws ExpressionNotImplementedException, ExpressionException {
		if(! (expression instanceof ExpressionImpl) ){
			throw new ExpressionException("Expression has wrong type expect: "+ExpressionImpl.class.getName()+"  found: "+expression.getClass().getName());
		}
		buildNotExpression(((ExpressionImpl)expression).expressionEngine);
		return this;
	}
	
	
	
	
	
	
	/* ************ ORDER *********** */
	
	/**
	 * Sets the sort of research 
	 * 
	 * @param sortOrder sort of research 
	 * @return the instance of itself
	 * @throws ExpressionNotImplementedException Method not implemented
	 * @throws ExpressionException Error Processing
	 */
	@Override
	public IExpression sortOrder(SortOrder sortOrder)
			throws ExpressionNotImplementedException, ExpressionException {
		this._sortOrder = sortOrder;
		return this;
	}

	/**
	 * Adds a sort field order
	 * 
	 * @param field sort field order
	 * @return the instance of itself
	 * @throws ExpressionNotImplementedException Method not implemented
	 * @throws ExpressionException Error Processing
	 */
	@Override
	public IExpression addOrder(IField field)
			throws ExpressionNotImplementedException, ExpressionException {
		this.checkArgoments(field,false);
		if(this._sortOrder==null || SortOrder.UNSORTED.equals(this._sortOrder)){
			throw new ExpressionException("To add order by conditions must first be defined the sort order (by sortOrder method)"); 
		}
		this.orderedFields.add(new OrderedField(field, this._sortOrder));
		return this;
	}
	
	/**
	 * Adds a sort field order
	 * 
	 * @param field sort field order
	 * @param sortOrder sort of research 
	 * @return the instance of itself
	 * @throws ExpressionNotImplementedException Method not implemented
	 * @throws ExpressionException Error Processing
	 */
	@Override
	public IExpression addOrder(IField field, SortOrder sortOrder) throws ExpressionNotImplementedException,ExpressionException{
		this.checkArgoments(field,false);
		if(sortOrder==null){
			throw new ExpressionException("Sort order parameter undefined"); 
		}
		if(SortOrder.UNSORTED.equals(sortOrder)){
			throw new ExpressionException("Sort order parameter not valid (use ASC or DESC)"); 
		}
		this.orderedFields.add(new OrderedField(field, sortOrder));
		return this;
	}
	
	public List<OrderedField> getOrderedFields() {
		return this.orderedFields;
	}
	
	public SortOrder getSortOrder(){
		if(
			( this._sortOrder==null || SortOrder.UNSORTED.equals(this._sortOrder) ) 
				&& 
			( this.orderedFields!=null && this.orderedFields.size()>0 )
		){
			// ritorno un sortOrder a caso tra i orderedFields, tanto poi viene usato sempre quello indicato per ogni field.
			return this.orderedFields.get(0).getSortOrder();
		}
		return this._sortOrder;
	}
	
	
	
	
	
	
	/* ************ GROUP BY *********** */
	
	@Override
	public IExpression addGroupBy(IField field)
			throws ExpressionNotImplementedException, ExpressionException {
		this.checkArgoments(field,false);
		this.groupByFields.add(field);
		return this;
	}
	
	public List<IField> getGroupByFields() {
		return this.groupByFields;
	}
	
	
	
	
	
	
	
	
		
	/* ************ IN USE MODEL/FIELD EXPRESSION *********** */
	
	/**
	 * Return the information as the <var>field</var> is used in the expression
	 * 
	 * @param field field test
	 * @return information as the <var>field</var> is used in the expression
	 */
	@Override
	public boolean inUseField(IField field,boolean checkOnlyWhereCondition) throws ExpressionNotImplementedException, ExpressionException {
		if(this.expressionEngine==null){
			if(checkOnlyWhereCondition)
				return false;
		}
		if( this.expressionEngine!=null && this.expressionEngine.inUseField(field) ){
			return true;
		}
		if(checkOnlyWhereCondition==false){
			if(this.orderedFields!=null){
				for (int i = 0; i < this.orderedFields.size(); i++) {
					if(field.equals(this.orderedFields.get(i).getField())){
						return true;
					}
				}
			}
			if(this.groupByFields!=null){
				for (int i = 0; i < this.groupByFields.size(); i++) {
					if(field.equals(this.groupByFields.get(i))){
						return true;
					}
				}
			}
		}
		return false;
	}
		
	/**
	 * Return the information as there are few fields in the expression belonging to the <var>model</var>
	 * 
	 * @param model model test
	 * @return information as there are few fields in the expression belonging to the <var>model</var>
	 */
	@Override
	public boolean inUseModel(IModel<?> model,boolean checkOnlyWhereCondition) throws ExpressionNotImplementedException, ExpressionException {
		if(this.expressionEngine==null){
			if(checkOnlyWhereCondition)
				return false;
		}
		if( this.expressionEngine!=null && this.expressionEngine.inUseModel(model) ){
			return true;
		}
		if(checkOnlyWhereCondition==false){
			if(this.orderedFields!=null){
				for (int i = 0; i < this.orderedFields.size(); i++) {
					IField field = this.orderedFields.get(i).getField();
					boolean inUseModel = false;
					if(model.getBaseField()!=null){
						// Modello di un elemento non radice
						if(field instanceof ComplexField){
							ComplexField c = (ComplexField) field;
							inUseModel = c.getFather().equals(model.getBaseField());
						}else{
							inUseModel = model.getModeledClass().getName().equals(field.getClassType().getName());
						}
					}
					else{
						inUseModel = model.getModeledClass().getName().equals(field.getClassType().getName());
					}
					if(inUseModel){
						return true;
					}
				}
			}
			if(this.groupByFields!=null){
				for (int i = 0; i < this.groupByFields.size(); i++) {
					IField field = this.groupByFields.get(i);
					boolean inUseModel = false;
					if(model.getBaseField()!=null){
						// Modello di un elemento non radice
						if(field instanceof ComplexField){
							ComplexField c = (ComplexField) field;
							inUseModel = c.getFather().equals(model.getBaseField());
						}else{
							inUseModel = model.getModeledClass().getName().equals(field.getClassType().getName());
						}
					}
					else{
						inUseModel = model.getModeledClass().getName().equals(field.getClassType().getName());
					}
					if(inUseModel){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	
	
	
	
	
	
	
	
	
	/* ************ GET FIELDS INTO EXPRESSION *********** */
	
	/**
	 * Return the fields userd in the expression
	 * 
	 * @return Return the fields userd in the expression
	 * @throws ExpressionNotImplementedException
	 * @throws ExpressionException
	 */
	@Override
	public List<IField> getFields(boolean onlyWhereCondition) throws ExpressionNotImplementedException, ExpressionException{
		List<IField> o = this.expressionEngine!=null ? this.expressionEngine.getFields() : null;
		if(onlyWhereCondition==false){
			if(this.orderedFields!=null){
				for (int i = 0; i < this.orderedFields.size(); i++) {
					IField field = this.orderedFields.get(i).getField();
					if(o==null){
						o = new ArrayList<IField>();
					}
					if(o.contains(field)==false){
						o.add(field);
					}
				}
			}
			if(this.groupByFields!=null){
				for (int i = 0; i < this.groupByFields.size(); i++) {
					IField field = this.groupByFields.get(i);
					if(o==null){
						o = new ArrayList<IField>();
					}
					if(o.contains(field)==false){
						o.add(field);
					}
				}
			}
		}
		return o; 
	}
	
	/**
	 * Return the object associated with the <var>field</var> present in the expression
	 * 
	 * @param field field test
	 * @return the object associated with the <var>field</var> present in the expression
	 */
	@Override
	public List<Object> getWhereConditionFieldValues(IField field) throws ExpressionNotImplementedException, ExpressionNotFoundException, ExpressionException{
		if(this.expressionEngine==null){
			throw new ExpressionNotFoundException("Field is not used in expression (Expression is empty)");
		}
		if(!this.inUseField(field,true)){
			throw new ExpressionNotFoundException("Field is not used in expression ["+field.toString()+"]");
		}
		List<Object> o = this.expressionEngine.getFieldValues(field);
		if(o==null){
			// non dovrei mai entrarci
			throw new ExpressionNotFoundException("Field is not used in expression ["+field.toString()+"] (null??)");
		}
		return o; 
	}
	
	
	
	
	
	
	
	/* ************ EXPRESSION STATE *********** */
	
	/**
	 * Return an indication if the expression contains 'where conditions' 
	 * 
	 * @return indication if the expression contains 'where conditions' 
	 * @throws ExpressionNotImplementedException Method not implemented
	 * @throws ExpressionException Error Processing
	 */
	@Override
	public boolean isWhereConditionsPresent() throws ExpressionNotImplementedException, ExpressionException{
		return this.expressionEngine==null;
	}
	
	
	
	
	

	/* ************ TO STRING *********** */
	
	@Override
	public String toString(){
		String s = super.toString();
		if(s==null){
			return s;
		}
		else{
			
			if(this.throwExpressionNotInitialized){
				throw new RuntimeException("Expression is not initialized");
			}
			
			StringBuffer bf = new StringBuffer();
			if(this.expressionEngine==null){
				bf.append("");
			}else{
				bf.append(this.expressionEngine.toString());
			}
			
			
			if(this.groupByFields.size()>0){
				
				bf.append(" GROUP BY ");
				
				int index = 0;
				for (Iterator<IField> iterator = this.groupByFields.iterator(); iterator.hasNext();) {
					IField field = iterator.next();
					if(index>0){
						bf.append(" , ");
					}
					if(field instanceof ComplexField){
						ComplexField cf = (ComplexField) field;
						if(cf.getFather()!=null){
							bf.append(cf.getFather().getFieldName());
						}else{
							bf.append(field.getClassName());
						}
					}else{
						bf.append(field.getClassName());
					}
					bf.append(".");
					bf.append(field.getFieldName());
					index++;
				}
			}
			
			
			if(!SortOrder.UNSORTED.equals(this.getSortOrder())){
				
				bf.append(" ORDER BY ");
				if(this.orderedFields.size()>0){
					int index = 0;
					for (Iterator<OrderedField> iterator = this.orderedFields.iterator(); iterator.hasNext();) {
						OrderedField orderedField = iterator.next();
						if(index>0){
							bf.append(" , ");
						}
						IField field = orderedField.getField();
						if(field instanceof ComplexField){
							ComplexField cf = (ComplexField) field;
							if(cf.getFather()!=null){
								bf.append(cf.getFather().getFieldName());
							}else{
								bf.append(field.getClassName());
							}
						}else{
							bf.append(field.getClassName());
						}
						bf.append(".");
						bf.append(field.getFieldName());
						
						bf.append(" ");
						bf.append(orderedField.getSortOrder().name());
						index++;
					}
				}else{
					bf.append(" DEFAULT_ORDER_FIELD");
					
					bf.append(" ");
					bf.append(this.getSortOrder().name());
				}
			}
						
			return bf.toString();
		}
	}
	
	
	
	
	
	/* ************ OBJECTS ************ */
	protected ComparatorExpressionImpl getComparatorExpression(IField field, Object value, Comparator c)throws ExpressionException {
		return new ComparatorExpressionImpl(this.objectFormatter,field,value,c);
	}
	protected BetweenExpressionImpl getBetweenExpression(IField field, Object lower, Object high)throws ExpressionException {
		return new BetweenExpressionImpl(this.objectFormatter,field,lower,high);
	}
	protected InExpressionImpl getInExpression(IField field, Object... values) throws ExpressionException {
		List<Object> lista = new ArrayList<Object>();
		if(values!=null){
			for (int i = 0; i < values.length; i++) {
				lista.add(values[i]);
			}
		}
		return new InExpressionImpl(this.objectFormatter,field, lista);
	}
	protected LikeExpressionImpl getLikeExpression(IField field, String value, LikeMode mode, boolean caseInsensitive) throws ExpressionException {
		return new LikeExpressionImpl(this.objectFormatter,field, value, mode, caseInsensitive);
	}
	protected ConjunctionExpressionImpl getConjunctionExpression() throws ExpressionException {
		return new ConjunctionExpressionImpl(this.objectFormatter);
	}
		
	
	
	/* ************ UTILITY - BUILD ************ */
	protected void buildComparatorExpression(IField field, Object value, Comparator c) throws ExpressionException {
		ComparatorExpressionImpl cExp = getComparatorExpression(field,value,c);
		if(this.expressionEngine==null){
			this.expressionEngine = cExp;
		}else{
			conjunctionWithInternalInstance(cExp);
		}
		this.expressionEngine.setNot(this.notOperator);
	}
	protected void buildComparatorExpression(Map<IField, Object> propertyNameValues,Comparator c,boolean and) throws ExpressionException {
		Iterator<IField> fieldsIt = propertyNameValues.keySet().iterator();
		ConjunctionExpressionImpl newConjunctionExpr = getConjunctionExpression();
		newConjunctionExpr.setAndConjunction(and);
		while (fieldsIt.hasNext()) {
			IField field = fieldsIt.next();
			Object value = propertyNameValues.get(field);
			newConjunctionExpr.addExpression(getComparatorExpression(field,value,c));
		}
		this.conjunctionWithInternalInstance(newConjunctionExpr);
		this.expressionEngine.setNot(this.notOperator);
	}
	protected void buildBeetweenExpression(IField field, Object lower, Object high) throws ExpressionException {
		BetweenExpressionImpl cExp = getBetweenExpression(field,lower,high);
		if(this.expressionEngine==null){
			this.expressionEngine = cExp;
		}else{
			conjunctionWithInternalInstance(cExp);
		}
		this.expressionEngine.setNot(this.notOperator);
	}
	protected void buildInExpression(IField field, Object... values) throws ExpressionException {
		InExpressionImpl cExp = getInExpression(field, values);
		if(this.expressionEngine==null){
			this.expressionEngine = cExp;
		}else{
			conjunctionWithInternalInstance(cExp);
		}
		this.expressionEngine.setNot(this.notOperator);
	}
	protected void buildLikeExpression(IField field, String value, LikeMode mode, boolean caseInsensitive) throws ExpressionException {
		LikeExpressionImpl cExp = getLikeExpression(field, value, mode, caseInsensitive);
		if(this.expressionEngine==null){
			this.expressionEngine = cExp;
		}else{
			conjunctionWithInternalInstance(cExp);
		}
		this.expressionEngine.setNot(this.notOperator);
	}
	protected void buildNotExpression(AbstractBaseExpressionImpl expr) throws ExpressionException {
		expr.setNot(true);
		if(this.expressionEngine==null){
			this.expressionEngine = expr;
		}else{
			String s1 = this.expressionEngine.toString();
			String s2 = expr.toString();
			if(s1.equals(s2)){
				// same
				this.expressionEngine = expr;
			}else{
				conjunctionWithInternalInstance(expr);
			}
		}
	}
	protected void buildConjunctionExpression(boolean and,IExpression... expressions) throws ExpressionException {
		ConjunctionExpressionImpl cExp = getConjunctionExpression();
		cExp.setAndConjunction(and);
		boolean add = false;
		for (int i = 0; i < expressions.length; i++) {
			ExpressionImpl xmlExpression = (ExpressionImpl) expressions[i];
			if(xmlExpression.expressionEngine!=null){
				cExp.addExpression(xmlExpression.expressionEngine);
				add = true;
			}
		}
		if(add){
			if(this.expressionEngine==null){
				this.expressionEngine = cExp;
			}else{
				conjunctionWithInternalInstance(cExp);
			}
			this.expressionEngine.setNot(this.notOperator);
		}
	}
	protected void conjunctionWithInternalInstance(AbstractBaseExpressionImpl newExpr) throws ExpressionException {
		ConjunctionExpressionImpl newConjunctionExpr = getConjunctionExpression();
		newConjunctionExpr.setAndConjunction(this.andLogicOperator);
		if(this.expressionEngine!=null){
			if(! (this.expressionEngine instanceof ConjunctionExpressionImpl) ){
				// se non e' una congiunzione e' una singola operazione.
				newConjunctionExpr.addExpression(this.expressionEngine);
			}else{
				// se e' una congiunzione verifico che al suo interno vi siano solo semplici operazioni e non altre congiunzioni e che l'operatore utilizzato sia quello di default.
				// se cosi' e' evito di creare una struttura simile alla seguente: ((A & B) & C)
				// Preferisco invece ricreare un nuovo oggetto equivalente: A & B & C
				boolean simpleConjunction = true;
				ConjunctionExpressionImpl cTest = (ConjunctionExpressionImpl) this.expressionEngine;
				if(cTest.isAndConjunction() != this.andLogicOperator){
					simpleConjunction = false;
				}
				if(simpleConjunction){
					for (Iterator<AbstractBaseExpressionImpl> iterator = cTest.getLista().iterator(); iterator.hasNext();) {
						AbstractBaseExpressionImpl expr = iterator.next();
						if( expr instanceof ConjunctionExpressionImpl ){
							simpleConjunction = false;
							break;
						}
					}
				}
				if(simpleConjunction){
					for (Iterator<AbstractBaseExpressionImpl> iterator = cTest.getLista().iterator(); iterator.hasNext();) {
						AbstractBaseExpressionImpl expr = iterator.next();
						// Perchè ??? expr.setNot(false); Se si setta sempre a false una operazione scritta come and().not(expr).and().equals(a,2) resetta il not.
						newConjunctionExpr.addExpression(expr);
					}
				}else{
					newConjunctionExpr.addExpression(cTest);
				}
			}
		}
		newConjunctionExpr.addExpression(newExpr);
		this.expressionEngine = newConjunctionExpr;
		this.expressionEngine.setNot(this.notOperator);
	}
	
	
	
	
	
	/* ************ UTILITY - CHECK ************ */
	protected void checkArgoments(IField field) throws ExpressionException {
		this.checkArgoments(field, true);
	}
	protected void checkArgoments(IField field, boolean constantPermit) throws ExpressionException {
		if(field==null){
			throw new ExpressionException("Field is null");
		}
		if(!constantPermit){
			if(field instanceof ConstantField){
				throw new ExpressionException("The field type 'ConstantField' can not be used with this method");
			}
		}
	}
	
	protected void checkArgoments(IField field, Object value) throws ExpressionException {
		this.checkArgoments(field, value, true);
	}
	protected void checkArgoments(IField field, Object value, boolean constantPermit) throws ExpressionException {
		if(field==null){
			throw new ExpressionException("Field is null");
		}
		if(value==null){
			throw new ExpressionException("Value is null for field: "+field.toString());
		}
		try{
			this.objectFormatter.isSupported(value);
		}catch(Exception e){
			throw new ExpressionException("[Value] "+e.getMessage(),e);
		}
		if(!constantPermit){
			if(field instanceof ConstantField){
				throw new ExpressionException("The field type 'ConstantField' can not be used with this method");
			}
		}
	}
	
	protected void checkArgoments(IField field, Object [] values) throws ExpressionException {
		this.checkArgoments(field, values, true);
	}
	protected void checkArgoments(IField field, Object [] values, boolean constantPermit) throws ExpressionException {
		if(field==null){
			throw new ExpressionException("Field is null");
		}
		if(values==null){
			throw new ExpressionException("Values is null");
		}
		for (int i = 0; i < values.length; i++) {
			if(values[i]==null){
				throw new ExpressionException("Values["+i+"] is null for field: "+field.toString());
			}
			try{
				this.objectFormatter.isSupported(values[i]);
			}catch(Exception e){
				throw new ExpressionException("[Value["+i+"]] "+e.getMessage(),e);
			}
		}
		if(!constantPermit){
			if(field instanceof ConstantField){
				throw new ExpressionException("The field type 'ConstantField' can not be used with this method");
			}
		}
	}
	
	protected void checkArgoments(IField field, Object lower, Object high) throws ExpressionException {
		this.checkArgoments(field, lower, high, true);
	}
	protected void checkArgoments(IField field, Object lower, Object high, boolean constantPermit) throws ExpressionException {
		if(field==null){
			throw new ExpressionException("Field is null");
		}
		if(lower==null){
			throw new ExpressionException("Lower is null for field: "+field.toString());
		}
		if(high==null){
			throw new ExpressionException("High is null for field: "+field.toString());
		}
		try{
			this.objectFormatter.isSupported(lower);
		}catch(Exception e){
			throw new ExpressionException("[Lower] "+e.getMessage(),e);
		}
		try{
			this.objectFormatter.isSupported(high);
		}catch(Exception e){
			throw new ExpressionException("[High] "+e.getMessage(),e);
		}
		if(!constantPermit){
			if(field instanceof ConstantField){
				throw new ExpressionException("The field type 'ConstantField' can not be used with this method");
			}
		}
	}
	
	protected void checkArgoments(Map<IField, Object> propertyNameValues) throws ExpressionException {
		this.checkArgoments(propertyNameValues, true);
	}
	protected void checkArgoments(Map<IField, Object> propertyNameValues, boolean constantPermit) throws ExpressionException {
		if(propertyNameValues == null){
			throw new ExpressionException("PropertyNameValues is null");
		}
		Iterator<IField> fieldsIt = propertyNameValues.keySet().iterator();
		while (fieldsIt.hasNext()) {
			IField iField = fieldsIt.next();
			if(iField==null){
				throw new ExpressionException("Field is null");
			}
			if(propertyNameValues.get(iField)==null){
				throw new ExpressionException("Value for Field["+iField+"] is null");	
			}
			try{
				this.objectFormatter.isSupported(propertyNameValues.get(iField));
			}catch(Exception e){
				throw new ExpressionException("[Value for Field["+iField+"]] "+e.getMessage(),e);
			}
			if(!constantPermit){
				if(iField instanceof ConstantField){
					throw new ExpressionException("The field type 'ConstantField' can not be used with this method");
				}
			}
		}
	}
	
	protected void checkConjunctionOperation(IExpression... expressions) throws ExpressionException {
		if(expressions==null || expressions.length<=0){
			throw new ExpressionException("Expressions is null or is empty");
		}
		for (int i = 0; i < expressions.length; i++) {
			if(expressions[i] == null){
				throw new ExpressionException("Expression["+i+"] is null");
			}
			if( ! (expressions[i] instanceof ExpressionImpl)){
				throw new ExpressionException("Expression["+i+"] has wrong type expect: "+ExpressionImpl.class.getName()+"  found: "+expressions[i].getClass().getName());
			}
		}
	}
	

}
