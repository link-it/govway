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
package org.openspcoop2.generic_project.expression;


import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotFoundException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;

/**
 * IExpression
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IExpression {

	
	/* ************ STATE OF EXPRESSION *********** */
	
	/**
	 * Make the negation of the expression
	 * 
	 * @return the instance of itself
	 * @throws ExpressionNotImplementedException Method not implemented
	 * @throws ExpressionException Error Processing
	 */
	public IExpression not() throws ExpressionNotImplementedException,ExpressionException;
	
	/**
	 * Use the conjunction "AND" for all expressions
	 * 
	 * @return the instance of itself
	 * @throws ExpressionNotImplementedException Method not implemented
	 * @throws ExpressionException Error Processing
	 */
	public IExpression and() throws ExpressionNotImplementedException, ExpressionException;

	/**
	 * Use the conjunction "OR" for all expressions
	 * 
	 * @return the instance of itself
	 * @throws ExpressionNotImplementedException Method not implemented
	 * @throws ExpressionException Error Processing
	 */
	public IExpression or() throws ExpressionNotImplementedException, ExpressionException;

	
	
	
	/* ************ COMPARATOR *********** */
	
	/**
	 * Create an expression of equality
	 * Example:  (field = value)
	 * 
	 * @param field Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	public IExpression equals(IField field, Object value) throws ExpressionNotImplementedException,ExpressionException;

	/**
	 * Create an expression of inequality
	 * Example:  (field <> value)
	 * 
	 * @param field Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	public IExpression notEquals(IField field, Object value) throws ExpressionNotImplementedException,ExpressionException;

	/**
	 * Create an expression of equality to each resource in the collection of the keys of the Map
	 * Use the conjunction "AND" for all expressions
	 * Example:  ( field[0]=values[0] AND field[1]=values[1] ..... AND field[N]=values[N] )
	 * 
	 * @param propertyNameValues Map that contains identifiers and their values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	public IExpression allEquals(Map<IField, Object> propertyNameValues) throws ExpressionNotImplementedException,ExpressionException;

	/**
	 * Create an expression of inequality to each resource in the collection of the keys of the Map
	 * Use the conjunction "AND" for all expressions
	 * Example:  ( field[0]<>values[0] AND field[1]<>values[1] ..... AND field[N]<>values[N] )
	 * 
	 * @param propertyNameValues Map that contains identifiers and their values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	public IExpression allNotEquals(Map<IField, Object> propertyNameValues)
	throws ExpressionNotImplementedException,ExpressionException;
	
	/**
	 * Create an expression of equality to each resource in the collection of the keys of the Map
	 * Use the conjunction defined by <var>andConjunction</var> for all expressions
	 * Example:  ( field[0]=values[0] <var>andConjunction</var> field[1]=values[1] ..... <var>andConjunction</var> field[N]=values[N] )
	 * 
	 * @param propertyNameValues Map that contains identifiers and their values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	public IExpression allEquals(Map<IField, Object> propertyNameValues,boolean andConjunction) throws ExpressionNotImplementedException,ExpressionException;

	/**
	 * Create an expression of inequality to each resource in the collection of the keys of the Map
	 * Use the conjunction defined by <var>andConjunction</var> for all expressions
	 * Example:  ( field[0]<>values[0] <var>andConjunction</var> field[1]<>values[1] ..... <var>andConjunction</var> field[N]<>values[N] )
	 * 
	 * @param propertyNameValues Map that contains identifiers and their values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	public IExpression allNotEquals(Map<IField, Object> propertyNameValues,boolean andConjunction)
	throws ExpressionNotImplementedException,ExpressionException;

	/**
	 * Create an expression "greaterThan"
	 * Example:  (field > value)
	 * 
	 * @param field Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	public IExpression greaterThan(IField field, Object value)
	throws ExpressionNotImplementedException,ExpressionException;
	
	/**
	 * Create an expression "greaterEquals"
	 * Example:  (field >= value)
	 * 
	 * @param field Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	public IExpression greaterEquals(IField field, Object value)
	throws ExpressionNotImplementedException,ExpressionException;
	
	/**
	 * Create an expression "lessThan"
	 * Example:  (field < value)
	 * 
	 * @param field Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	public IExpression lessThan(IField field, Object value)
	throws ExpressionNotImplementedException,ExpressionException;
	
	/**
	 * Create an expression "lessEquals"
	 * Example:  (field <= value)
	 * 
	 * @param field Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	public IExpression lessEquals(IField field, Object value)
	throws ExpressionNotImplementedException,ExpressionException;
	
	
	
	
	/* ************ SPECIAL COMPARATOR *********** */
	
	/**
	 * Create an expression "is null"
	 * Example:  ( field is null )
	 * 
	 * @param field Resource identifier
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	public IExpression isNull(IField field) throws ExpressionNotImplementedException,ExpressionException;
	
	/**
	 * Create an expression "is not null"
	 * Example:  ( field is not null )
	 * 
	 * @param field Resource identifier
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	public IExpression isNotNull(IField field) throws ExpressionNotImplementedException,ExpressionException;
	
	/**
	 * Create an expression "is empty"
	 * Example:  ( field = '' )
	 * 
	 * @param field Resource identifier
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	public IExpression isEmpty(IField field) throws ExpressionNotImplementedException,ExpressionException;
	
	/**
	 * Create an expression "is not empty"
	 * Example:  ( field <> '' )
	 * 
	 * @param field Resource identifier
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	public IExpression isNotEmpty(IField field) throws ExpressionNotImplementedException,ExpressionException;
	
	
	
	
	
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
	public IExpression between(IField field, Object lower, Object high)
	throws ExpressionNotImplementedException,ExpressionException;
	
	
	
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
	public IExpression like(IField field, String value) throws ExpressionNotImplementedException,ExpressionException;
	
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
	public IExpression like(IField field, String value, LikeMode mode) throws ExpressionNotImplementedException,ExpressionException;
	
	/**
	 * Create an expression "like" case-insensitive
	 * Example:  ( toLower(field) like '%toLower(value)%' )  
	 * 
	 * @param field Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	public IExpression ilike(IField field, String value) throws ExpressionNotImplementedException,ExpressionException;
	
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
	public IExpression ilike(IField field, String value, LikeMode mode) throws ExpressionNotImplementedException,ExpressionException;

	
	
	
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
	public IExpression in(IField field, Object ... values)
	throws ExpressionNotImplementedException,ExpressionException;
		
	/**
	 * Create an expression "in" verifying that the value of <var>field</var> is one of those given in the param <var>values</var>
	 * Example:  ( field IN ('values[0]', 'values[1]', ... ,'values[n]')  )
	 * 
	 * @param field Resource identifier
	 * @param values Values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	public IExpression in(IField field, Collection<?> values)
	throws ExpressionNotImplementedException,ExpressionException;
	
	
	
	
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
	public IExpression and(IExpression exp1, IExpression exp2) throws ExpressionNotImplementedException,ExpressionException;
	
	/**
	 *  Adds to the expression in the conjunction "AND" of the espressions given as a parameters
	 *  Example: ( expressions[1] AND expressions[2] AND ... expressions[N] )
	 *  
	 * @param expressions Expressions combined in AND
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	public IExpression and(IExpression... expressions) throws ExpressionNotImplementedException,ExpressionException;
	
	/**
	 *  Adds to the expression in the conjunction "OR" of the espressions given as a parameters
	 *  Example: ( exp1 OR exp2  )
	 *  
	 * @param exp1 Filter combined in OR with <var>exp1</var>
	 * @param exp2 Filter combined in OR with <var>exp2</var>
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	public IExpression or(IExpression exp1, IExpression exp2) throws ExpressionNotImplementedException,ExpressionException;
	
	/**
	 *  Adds to the expression in the conjunction "OR" of the espressions given as a parameters
	 *  Example: ( expressions[1] OR expressions[2] OR ... expressions[N] )
	 *  
	 * @param expressions Expressions combined in OR
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	public IExpression or(IExpression... expressions) throws ExpressionNotImplementedException,ExpressionException;
	
	
	
	
	/* ************ NEGATION *********** */
	
	/**
	 * Adding to the negation of the expression passed as parameter
	 * Example: ( NOT expression )
	 * 
	 * @param expression Expression
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws ExpressionNotImplementedException,ExpressionException
	 */
	public IExpression not(IExpression expression) throws ExpressionNotImplementedException,ExpressionException;

	
	
	
	/* ************ ORDER *********** */
	
	/**
	 * Sets the sort of research 
	 * 
	 * @param sortOrder sort of research 
	 * @return the instance of itself
	 * @throws ExpressionNotImplementedException Method not implemented
	 * @throws ExpressionException Error Processing
	 */
	public IExpression sortOrder(SortOrder sortOrder) throws ExpressionNotImplementedException,ExpressionException;
	
	/**
	 * Adds a sort field order
	 * 
	 * @param field sort field order
	 * @return the instance of itself
	 * @throws ExpressionNotImplementedException Method not implemented
	 * @throws ExpressionException Error Processing
	 */
	public IExpression addOrder(IField field) throws ExpressionNotImplementedException,ExpressionException;
	
	/**
	 * Adds a sort field order
	 * 
	 * @param field sort field order
	 * @param sortOrder sort of research 
	 * @return the instance of itself
	 * @throws ExpressionNotImplementedException Method not implemented
	 * @throws ExpressionException Error Processing
	 */
	public IExpression addOrder(IField field, SortOrder sortOrder) throws ExpressionNotImplementedException,ExpressionException;

	
	
	
	
	
	
	/* ************ GROUP BY *********** */
	
	/**
	 * Adds a group by field
	 * 
	 * @param field group by field order
	 * @return the instance of itself
	 * @throws ExpressionNotImplementedException Method not implemented
	 * @throws ExpressionException Error Processing
	 */
	public IExpression addGroupBy(IField field) throws ExpressionNotImplementedException,ExpressionException;

	
	
	
	
	
	
	
	
	/* ************ IN USE MODEL/FIELD EXPRESSION *********** */
	
	/**
	 * Return the information as the <var>field</var> is used in the expression
	 * 
	 * @param field field test
	 * @param checkOnlyWhereCondition
	 * @return information as the <var>field</var> is used in the expression
	 */
	public boolean inUseField(IField field,boolean checkOnlyWhereCondition) throws ExpressionNotImplementedException, ExpressionException;
			
	/**
	 * Return the information as there are few fields in the expression belonging to the <var>model</var>
	 * 
	 * @param model model test
	 * @param checkOnlyWhereCondition
	 * @return information as there are few fields in the expression belonging to the <var>model</var>
	 */
	public boolean inUseModel(IModel<?> model,boolean checkOnlyWhereCondition) throws ExpressionNotImplementedException, ExpressionException;
	
	
	
	
	/* ************ GET FIELDS INTO EXPRESSION *********** */
	
	/**
	 * Return the fields used in the expression
	 * 
	 * @param onlyWhereCondition
	 * @return Return the fields userd in the expression
	 * @throws ExpressionNotImplementedException
	 * @throws ExpressionException
	 */
	public List<IField> getFields(boolean onlyWhereCondition) throws ExpressionNotImplementedException, ExpressionException;
	
	/**
	 * Return the object associated with the <var>field</var> present in the expression
	 * 
	 * @param field field test
	 * @return the object associated with the <var>field</var> present in the expression
	 */
	public List<Object> getWhereConditionFieldValues(IField field) throws ExpressionNotImplementedException, ExpressionNotFoundException, ExpressionException;

	
	
	
	
	/* ************ EXPRESSION STATE *********** */
	
	/**
	 * Return an indication if the expression contains 'where conditions' 
	 * 
	 * @return indication if the expression contains 'where conditions' 
	 * @throws ExpressionNotImplementedException Method not implemented
	 * @throws ExpressionException Error Processing
	 */
	public boolean isWhereConditionsPresent() throws ExpressionNotImplementedException, ExpressionException;
}
