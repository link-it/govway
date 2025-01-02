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
package org.openspcoop2.monitor.sdk.condition;

import org.openspcoop2.monitor.sdk.constants.LikeMode;
import org.openspcoop2.monitor.sdk.constants.MessageType;
import org.openspcoop2.monitor.sdk.exceptions.SearchException;

import java.util.Collection;
import java.util.Map;

/**
 * IFilter
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IFilter {

	
	/* ************ STATE OF EXPRESSION *********** */
	
	/**
	 * Make the negation of the expression
	 * 
	 * @return the instance of itself
	 * @throws ExpressionNotImplementedException Method not implemented
	 * @throws ExpressionException Error Processing
	 */
	public IFilter not() throws SearchException;
	
	/**
	 * Use the conjunction "AND" for all expressions
	 * 
	 * @return the instance of itself
	 * @throws ExpressionNotImplementedException Method not implemented
	 * @throws ExpressionException Error Processing
	 */
	public IFilter and() throws SearchException;

	/**
	 * Use the conjunction "OR" for all expressions
	 * 
	 * @return the instance of itself
	 * @throws ExpressionNotImplementedException Method not implemented
	 * @throws ExpressionException Error Processing
	 */
	public IFilter or() throws SearchException;
	
	
	
	
	
	/* ************ COMPARATOR *********** */
	
	/**
	 * Create an expression of equality
	 * Example:  (field = value)
	 * 
	 * @param resourceID Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IFilter equals(String resourceID, Object value) throws SearchException;

	/**
	 * Create an expression of inequality
	 * Example:  (field &lt;&gt; value)
	 * 
	 * @param resourceID Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IFilter notEquals(String resourceID, Object value) throws SearchException;

	/**
	 * Create an expression of equality to each resource in the collection of the keys of the Map
	 * Use the conjunction "AND" for all expressions
	 * Example:  ( field[0]=values[0] AND field[1]=values[1] ..... AND field[N]=values[N] )
	 * 
	 * @param propertyNameValues Map that contains identifiers and their values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IFilter allEquals(Map<String, Object> propertyNameValues) throws SearchException;

	/**
	 * Create an expression of inequality to each resource in the collection of the keys of the Map
	 * Use the conjunction "AND" for all expressions
	 * Example:  ( field[0]&lt;&gt;values[0] AND field[1]&lt;&gt;values[1] ..... AND field[N]&lt;&gt;values[N] )
	 * 
	 * @param propertyNameValues Map that contains identifiers and their values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IFilter allNotEquals(Map<String, Object> propertyNameValues)
	throws SearchException;
	
	/**
	 * Create an expression of equality to each resource in the collection of the keys of the Map
	 * Use the conjunction defined by 'andConjunction' for all expressions
	 * Example:  ( field[0]=values[0] 'andConjunction' field[1]=values[1] ..... 'andConjunction' field[N]=values[N] )
	 * 
	 * @param propertyNameValues Map that contains identifiers and their values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IFilter allEquals(Map<String, Object> propertyNameValues,boolean andConjunction) throws SearchException;

	/**
	 * Create an expression of inequality to each resource in the collection of the keys of the Map
	 * Use the conjunction defined by 'andConjunction' for all expressions
	 * Example:  ( field[0]&lt;&gt;values[0] 'andConjunction' field[1]&lt;&gt;values[1] ..... 'andConjunction' field[N]&lt;&gt;values[N] )
	 * 
	 * @param propertyNameValues Map that contains identifiers and their values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IFilter allNotEquals(Map<String, Object> propertyNameValues,boolean andConjunction)
	throws SearchException;

	/**
	 * Create an expression "greaterThan"
	 * Example:  (field &gt; value)
	 * 
	 * @param resourceID Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IFilter greaterThan(String resourceID, Object value)
	throws SearchException;
	
	/**
	 * Create an expression "greaterEquals"
	 * Example:  (field &gt;= value)
	 * 
	 * @param resourceID Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IFilter greaterEquals(String resourceID, Object value)
	throws SearchException;
	
	/**
	 * Create an expression "lessThan"
	 * Example:  (field &lt; value)
	 * 
	 * @param resourceID Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IFilter lessThan(String resourceID, Object value)
	throws SearchException;
	
	/**
	 * Create an expression "lessEquals"
	 * Example:  (field &lt;= value)
	 * 
	 * @param resourceID Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IFilter lessEquals(String resourceID, Object value)
	throws SearchException;
	
	
	
	
	
	
	
	
	
	/* ************ SPECIAL COMPARATOR *********** */
	
	/**
	 * Create an expression "is null"
	 * Example:  ( field is null )
	 * 
	 * @param resourceID Resource identifier
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IFilter isNull(String resourceID) throws SearchException;
	
	/**
	 * Create an expression "is not null"
	 * Example:  ( field is not null )
	 * 
	 * @param resourceID Resource identifier
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IFilter isNotNull(String resourceID) throws SearchException;
	
	/**
	 * Create an expression "is empty"
	 * Example:  ( field = '' )
	 * 
	 * @param resourceID Resource identifier
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IFilter isEmpty(String resourceID) throws SearchException;
	
	/**
	 * Create an expression "is not empty"
	 * Example:  ( field &lt;&gt; '' )
	 * 
	 * @param resourceID Resource identifier
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IFilter isNotEmpty(String resourceID) throws SearchException;
	
	
	
	
	
	
	
	
	/* ************ BETWEEN *********** */
	
	/**
	 * Create an expression "between"
	 * Example:  ( field BEETWEEN lower AND high )
	 * 
	 * @param resourceID Resource identifier
	 * @param lower Lower limit value to be applied to the constraint
	 * @param high Higher limit value to be applied to the constraint
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IFilter between(String resourceID, Object lower, Object high)
	throws SearchException;
	
	
	
	
	
	
	
	/* ************ LIKE *********** */
	
	/**
	 * Create an expression "like"
	 * Example:  ( field like '%value%' )
	 * 
	 * @param resourceID Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IFilter like(String resourceID, String value) throws SearchException;
	
	/**
	 * Create an expression "like"
	 * Example:  
	 * 	 LikeMode.ANYWHERE -&gt; ( field like '%value%' )
	 *   LikeMode.EXACT -&gt; ( field like 'value' )
	 *   LikeMode.END -&gt; ( field like '%value' )
	 *   LikeMode.START -&gt; ( field like 'value%' )
	 * 
	 * @param resourceID Resource identifier
	 * @param value Value
	 * @param mode LikeMode
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IFilter like(String resourceID, String value, LikeMode mode) throws SearchException;
	
	/**
	 * Create an expression "like" case-insensitive
	 * Example:  ( toLower(field) like '%toLower(value)%' )  
	 * 
	 * @param resourceID Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IFilter ilike(String resourceID, String value) throws SearchException;
	
	/**
	 * Create an expression "like" case-insensitive
	 * Example:  
	 *   LikeMode.ANYWHERE -&gt; ( toLower(field) like '%toLower(value)%' )
	 *   LikeMode.EXACT -&gt; ( toLower(field) like 'toLower(value)' )
	 *   LikeMode.END -&gt; ( toLower(field) like '%toLower(value)' )
	 *   LikeMode.START -&gt; ( toLower(field) like 'toLower(value)%' )
	 * 
	 * @param resourceID Resource identifier
	 * @param value Value
	 * @param mode LikeMode
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IFilter ilike(String resourceID, String value, LikeMode mode) throws SearchException;

	
	
	
	
	

	/* ************ IN *********** */
	
	/**
	 * Create an expression "in" verifying that the value of 'field' is one of those given in the param 'values'
	 * Example:  ( field IN ('values[0]', 'values[1]', ... ,'values[n]')  )
	 * 
	 * @param resourceID Resource identifier
	 * @param values Values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IFilter in(String resourceID, Object ... values)
	throws SearchException;
		
	/**
	 * Create an expression "in" verifying that the value of 'field' is one of those given in the param 'values'
	 * Example:  ( field IN ('values[0]', 'values[1]', ... ,'values[n]')  )
	 * 
	 * @param resourceID Resource identifier
	 * @param values Values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IFilter in(String resourceID, Collection<?> values)
	throws SearchException;
	
	
	
	
	
	/* ************ CONJUNCTION *********** */
	
	/**
	 *  Adds to the expression in the conjunction "AND" of the espressions given as a parameters
	 *  Example: ( exp1 AND exp2  )
	 *  
	 * @param exp1 Filter combined in AND with 'exp1'
	 * @param exp2 Filter combined in AND with 'exp2'
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IFilter and(IFilter exp1, IFilter exp2) throws SearchException;
	
	/**
	 *  Adds to the expression in the conjunction "AND" of the espressions given as a parameters
	 *  Example: ( expressions[1] AND expressions[2] AND ... expressions[N] )
	 *  
	 * @param expressions Expressions combined in AND
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IFilter and(IFilter... expressions) throws SearchException;
	
	/**
	 *  Adds to the expression in the conjunction "OR" of the espressions given as a parameters
	 *  Example: ( exp1 OR exp2  )
	 *  
	 * @param exp1 Filter combined in OR with 'exp1'
	 * @param exp2 Filter combined in OR with 'exp2'
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IFilter or(IFilter exp1, IFilter exp2) throws SearchException;
	
	/**
	 *  Adds to the expression in the conjunction "OR" of the espressions given as a parameters
	 *  Example: ( expressions[1] OR expressions[2] OR ... expressions[N] )
	 *  
	 * @param expressions Expressions combined in OR
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IFilter or(IFilter... expressions) throws SearchException;
	
	
	
	
	
	
	
	
	/* ************ NEGATION *********** */
	
	/**
	 * Adding to the negation of the expression passed as parameter
	 * Example: ( NOT expression )
	 * 
	 * @param expression Expression
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IFilter not(IFilter expression) throws SearchException;

	
	
	
	
	
	
	/* ************ MESSAGE TYPE *********** */

	public IFilter setMessageType(MessageType messageType) throws SearchException;
	
	
	
	
	/* ************ NEW FILTER *********** */
	
	public IFilter newFilter() throws SearchException;

}
