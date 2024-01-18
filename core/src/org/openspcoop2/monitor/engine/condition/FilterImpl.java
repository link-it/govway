/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.engine.condition;

import org.openspcoop2.monitor.engine.utils.ContentFormatter;
import org.openspcoop2.monitor.sdk.condition.IFilter;
import org.openspcoop2.monitor.sdk.condition.IStatisticFilter;
import org.openspcoop2.monitor.sdk.constants.LikeMode;
import org.openspcoop2.monitor.sdk.constants.MessageType;
import org.openspcoop2.monitor.sdk.exceptions.SearchException;
import org.openspcoop2.monitor.sdk.statistic.StatisticFilterName;
import org.openspcoop2.monitor.sdk.statistic.StatisticResourceFilter;
import org.openspcoop2.monitor.sdk.statistic.StatisticFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TipiDatabase;


/**
 * FilterImpl
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class FilterImpl implements IStatisticFilter {
	

	protected IExpression expression;
	protected TipiDatabase databaseType;
	protected ISQLFieldConverter fieldConverter;
	private String idStatistic;
	protected FilterImpl(IExpression expression,TipiDatabase databaseType, ISQLFieldConverter fieldConverter) {
		this.expression = expression;
		this.databaseType = databaseType;
		this.fieldConverter = fieldConverter;
	}
	public IExpression getExpression() {
		return this.expression;
	}
	
	protected static Logger logger = LoggerWrapperFactory.getLogger(FilterImpl.class);
	
	
	
	/* ************ UTILS *********** */
	
	protected abstract IStatisticFilter newIFilter() throws SearchException;
	
	protected abstract IExpression newIExpression() throws SearchException;
	
	protected abstract IField getIFieldForMessageType() throws SearchException;
	
	protected abstract List<IField> getIFieldForResourceName(StatisticFilterName statisticFilter) throws SearchException;

	protected abstract IField getIFieldForResourceValue(IField fieldResourceName) throws SearchException;
	
	private void check(StatisticResourceFilter resourceStatID) throws SearchException{
		if(resourceStatID.getStatisticFilterName()==null){
			throw new SearchException("Required StatisticFilterName undefined");
		}
		if(resourceStatID.getResourceID()==null){
			throw new SearchException("Required ResourceID undefined");
		}
	}
	private void check(StatisticFilter resource) throws SearchException{
		if(resource.getStatisticFilterName()==null){
			throw new SearchException("Required StatisticFilterName undefined");
		}
		if(resource.getResourceID()==null){
			throw new SearchException("Required ResourceID undefined");
		}
		if(resource.getValue()==null){
			throw new SearchException("Required Value undefined");
		}
	}
	
	
	/* ************ ID STATISTICA *********** */
	
	/**
	 * Return optional statistic id
	 * 
	 * @return statistic id
	 */
	public String getIdStatistic() {
		return this.idStatistic;
	}
	@Override
	public void setIdStatistic(String idStatistic) {
		this.idStatistic = idStatistic;
	}
	
	
	
	
	/* ************ STATE OF EXPRESSION *********** */
	
	/**
	 * Make the negation of the expression
	 * 
	 * @return the instance of itself
	 * @throws ExpressionNotImplementedException Method not implemented
	 * @throws ExpressionException Error Processing
	 */
	@Override
	public IStatisticFilter not() throws SearchException{
		try{
			this.expression.not();
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	/**
	 * Use the conjunction "AND" for all expressions
	 * 
	 * @return the instance of itself
	 * @throws ExpressionNotImplementedException Method not implemented
	 * @throws ExpressionException Error Processing
	 */
	@Override
	public IStatisticFilter and() throws SearchException{
		try{
			this.expression.and();
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}

	/**
	 * Use the conjunction "OR" for all expressions
	 * 
	 * @return the instance of itself
	 * @throws ExpressionNotImplementedException Method not implemented
	 * @throws ExpressionException Error Processing
	 */
	@Override
	public IStatisticFilter or() throws SearchException{
		try{
			this.expression.or();
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	
	
	
	
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
	@Override
	public IFilter equals(String resourceID, Object value) throws SearchException{
		return this._equals(null, resourceID, value);
	}
	@Override
	public IStatisticFilter equals(StatisticResourceFilter resourceID, Object value) throws SearchException{
		return this._equals(resourceID, null, value);
	}
	private IStatisticFilter _equals(StatisticResourceFilter resourceStatID, String resourceID, Object value) throws SearchException{
		try{	
			StatisticFilterName statisticFilter = null;
			if(resourceStatID!=null){
				this.check(resourceStatID);
				statisticFilter = resourceStatID.getStatisticFilterName();
				resourceID = resourceStatID.getResourceID();
			}
			List<IField> fieldsResourceName = this.getIFieldForResourceName(statisticFilter);
			if(fieldsResourceName.size()==1){
				IExpression expr = this.newIExpression();
				expr.and();
				IField fieldResourceName = fieldsResourceName.get(0);
				expr.equals(fieldResourceName, resourceID);
				expr.equals(this.getIFieldForResourceValue(fieldResourceName), ContentFormatter.toString(value));
				this.expression.and(expr);
			}
			else{
				IExpression exprEsterna = this.newIExpression();
				exprEsterna.or();
				for (IField fieldResourceName : fieldsResourceName) {
					IExpression expr = this.newIExpression();
					expr.and();
					expr.equals(fieldResourceName, resourceID);
					expr.equals(this.getIFieldForResourceValue(fieldResourceName), ContentFormatter.toString(value));
					exprEsterna.or(expr);
				}
				this.expression.and(exprEsterna);
			}
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}

	/**
	 * Create an expression of inequality
	 * Example:  (field <> value)
	 * 
	 * @param resourceID Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	@Override
	public IFilter notEquals(String resourceID, Object value) throws SearchException{
		return this._notEquals(null, resourceID, value);
	}
	@Override
	public IStatisticFilter notEquals(StatisticResourceFilter resourceID, Object value) throws SearchException{
		return this._notEquals(resourceID, null, value);
	}
	private IStatisticFilter _notEquals(StatisticResourceFilter resourceStatID, String resourceID, Object value) throws SearchException{
		try{
			StatisticFilterName statisticFilter = null;
			if(resourceStatID!=null){
				this.check(resourceStatID);
				statisticFilter = resourceStatID.getStatisticFilterName();
				resourceID = resourceStatID.getResourceID();
			}
			List<IField> fieldsResourceName = this.getIFieldForResourceName(statisticFilter);
			if(fieldsResourceName.size()==1){
				IExpression expr = this.newIExpression();
				expr.and();
				IField fieldResourceName = fieldsResourceName.get(0);
				expr.equals(fieldResourceName, resourceID);
				expr.notEquals(this.getIFieldForResourceValue(fieldResourceName), ContentFormatter.toString(value));
				this.expression.and(expr);
			}
			else{
				IExpression exprEsterna = this.newIExpression();
				exprEsterna.or();
				for (IField fieldResourceName : fieldsResourceName) {
					IExpression expr = this.newIExpression();
					expr.and();
					expr.equals(fieldResourceName, resourceID);
					expr.notEquals(this.getIFieldForResourceValue(fieldResourceName), ContentFormatter.toString(value));
					exprEsterna.or(expr);
				}
				this.expression.and(exprEsterna);
			}
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}

	/**
	 * Create an expression of equality to each resource in the collection of the keys of the Map
	 * Use the conjunction "AND" for all expressions
	 * Example:  ( field[0]=values[0] AND field[1]=values[1] ..... AND field[N]=values[N] )
	 * 
	 * @param propertyNameValues Map that contains identifiers and their values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	@Override
	public IFilter allEquals(Map<String, Object> propertyNameValues) throws SearchException{
		return this.allEquals(propertyNameValues, true);
	}
	@Override
	public IStatisticFilter allEquals(List<StatisticFilter> propertyNameValues) throws SearchException{
		return this.allEquals(propertyNameValues, true);
	}

	/**
	 * Create an expression of inequality to each resource in the collection of the keys of the Map
	 * Use the conjunction "AND" for all expressions
	 * Example:  ( field[0]<>values[0] AND field[1]<>values[1] ..... AND field[N]<>values[N] )
	 * 
	 * @param propertyNameValues Map that contains identifiers and their values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	@Override
	public IFilter allNotEquals(Map<String, Object> propertyNameValues)
	throws SearchException{
		return this.allNotEquals(propertyNameValues, true);
	}
	@Override
	public IStatisticFilter allNotEquals(List<StatisticFilter> propertyNameValues)
	throws SearchException{
		return this.allEquals(propertyNameValues, true);
	}
	
	/**
	 * Create an expression of equality to each resource in the collection of the keys of the Map
	 * Use the conjunction defined by <var>andConjunction</var> for all expressions
	 * Example:  ( field[0]=values[0] <var>andConjunction</var> field[1]=values[1] ..... <var>andConjunction</var> field[N]=values[N] )
	 * 
	 * @param propertyNameValues Map that contains identifiers and their values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	@Override
	public IFilter allEquals(Map<String, Object> propertyNameValues,boolean andConjunction) throws SearchException{
		try{
			if(propertyNameValues.size()>0){
				IExpression exprAll = this.newIExpression();
				if(andConjunction)
					exprAll.and();
				else
					exprAll.or();
				Iterator<String> keys = propertyNameValues.keySet().iterator();
				while (keys.hasNext()) {
					String resourceID = (String) keys.next();
					Object value = propertyNameValues.get(resourceID);
					FilterImpl f = (FilterImpl) this.newFilter();
					f.equals(resourceID, value);
					exprAll.and(f.getExpression());
				}
				this.expression.and(exprAll);
			}
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	@Override
	public IStatisticFilter allEquals(List<StatisticFilter> propertyNameValues,boolean andConjunction) throws SearchException{
		try{
			if(propertyNameValues.size()>0){
				IExpression exprAll = this.newIExpression();
				if(andConjunction)
					exprAll.and();
				else
					exprAll.or();
				for (StatisticFilter statisticsFilter : propertyNameValues) {
					this.check(statisticsFilter);
					FilterImpl f = (FilterImpl) this.newFilter();
					StatisticResourceFilter sf = new StatisticResourceFilter();
					sf.setResourceID(statisticsFilter.getResourceID());
					sf.setStatisticFilterName(statisticsFilter.getStatisticFilterName());
					f.equals(sf, statisticsFilter.getValue());
					exprAll.and(f.getExpression());
				}
				this.expression.and(exprAll);
			}
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}

	/**
	 * Create an expression of inequality to each resource in the collection of the keys of the Map
	 * Use the conjunction defined by <var>andConjunction</var> for all expressions
	 * Example:  ( field[0]<>values[0] <var>andConjunction</var> field[1]<>values[1] ..... <var>andConjunction</var> field[N]<>values[N] )
	 * 
	 * @param propertyNameValues Map that contains identifiers and their values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	@Override
	public IFilter allNotEquals(Map<String, Object> propertyNameValues,boolean andConjunction)
	throws SearchException{
		try{
			if(propertyNameValues.size()>0){
				IExpression exprAll = this.newIExpression();
				if(andConjunction)
					exprAll.and();
				else
					exprAll.or();
				Iterator<String> keys = propertyNameValues.keySet().iterator();
				while (keys.hasNext()) {
					String resourceID = (String) keys.next();
					Object value = propertyNameValues.get(resourceID);
					FilterImpl f = (FilterImpl) this.newFilter();
					f.notEquals(resourceID, value);
					exprAll.and(f.getExpression());
				}
				this.expression.and(exprAll);
			}
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	@Override
	public IStatisticFilter allNotEquals(List<StatisticFilter> propertyNameValues,boolean andConjunction)
	throws SearchException{
		try{
			if(propertyNameValues.size()>0){
				IExpression exprAll = this.newIExpression();
				if(andConjunction)
					exprAll.and();
				else
					exprAll.or();
				for (StatisticFilter statisticsFilter : propertyNameValues) {
					this.check(statisticsFilter);
					FilterImpl f = (FilterImpl) this.newFilter();
					StatisticResourceFilter sf = new StatisticResourceFilter();
					sf.setResourceID(statisticsFilter.getResourceID());
					sf.setStatisticFilterName(statisticsFilter.getStatisticFilterName());
					f.notEquals(sf, statisticsFilter.getValue());
					exprAll.and(f.getExpression());
				}
				this.expression.and(exprAll);
			}
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}

	/**
	 * Create an expression "greaterThan"
	 * Example:  (field > value)
	 * 
	 * @param resourceID Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	@Override
	public IFilter greaterThan(String resourceID, Object value)
	throws SearchException{
		return this._greaterThan(null, resourceID, value);
	}
	@Override
	public IStatisticFilter greaterThan(StatisticResourceFilter resourceID, Object value)
	throws SearchException{
		return this._greaterThan(resourceID, null, value);
	}
	private IStatisticFilter _greaterThan(StatisticResourceFilter resourceStatID, String resourceID, Object value)
	throws SearchException{
		try{
			StatisticFilterName statisticFilter = null;
			if(resourceStatID!=null){
				this.check(resourceStatID);
				statisticFilter = resourceStatID.getStatisticFilterName();
				resourceID = resourceStatID.getResourceID();
			}
			List<IField> fieldsResourceName = this.getIFieldForResourceName(statisticFilter);
			if(fieldsResourceName.size()==1){
				IExpression expr = this.newIExpression();
				expr.and();
				IField fieldResourceName = fieldsResourceName.get(0);
				expr.equals(fieldResourceName, resourceID);
				expr.greaterThan(this.getIFieldForResourceValue(fieldResourceName), ContentFormatter.toString(value));
				this.expression.and(expr);
			}
			else{
				IExpression exprEsterna = this.newIExpression();
				exprEsterna.or();
				for (IField fieldResourceName : fieldsResourceName) {
					IExpression expr = this.newIExpression();
					expr.and();
					expr.equals(fieldResourceName, resourceID);
					expr.greaterThan(this.getIFieldForResourceValue(fieldResourceName), ContentFormatter.toString(value));
					exprEsterna.or(expr);
				}
				this.expression.and(exprEsterna);
			}
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	/**
	 * Create an expression "greaterEquals"
	 * Example:  (field >= value)
	 * 
	 * @param resourceID Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	@Override
	public IFilter greaterEquals(String resourceID, Object value)
	throws SearchException{
		return this._greaterEquals(null, resourceID, value);
	}
	@Override
	public IStatisticFilter greaterEquals(StatisticResourceFilter resourceID, Object value)
	throws SearchException{
		return this._greaterEquals(resourceID, null, value);
	}
	private IStatisticFilter _greaterEquals(StatisticResourceFilter resourceStatID,String resourceID, Object value)
			throws SearchException{
		try{
			StatisticFilterName statisticFilter = null;
			if(resourceStatID!=null){
				this.check(resourceStatID);
				statisticFilter = resourceStatID.getStatisticFilterName();
				resourceID = resourceStatID.getResourceID();
			}
			List<IField> fieldsResourceName = this.getIFieldForResourceName(statisticFilter);
			if(fieldsResourceName.size()==1){
				IExpression expr = this.newIExpression();
				expr.and();
				IField fieldResourceName = fieldsResourceName.get(0);
				expr.equals(fieldResourceName, resourceID);
				expr.greaterEquals(this.getIFieldForResourceValue(fieldResourceName), ContentFormatter.toString(value));
				this.expression.and(expr);
			}
			else{
				IExpression exprEsterna = this.newIExpression();
				exprEsterna.or();
				for (IField fieldResourceName : fieldsResourceName) {
					IExpression expr = this.newIExpression();
					expr.and();
					expr.equals(fieldResourceName, resourceID);
					expr.greaterEquals(this.getIFieldForResourceValue(fieldResourceName), ContentFormatter.toString(value));
					exprEsterna.or(expr);
				}
				this.expression.and(exprEsterna);
			}
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	/**
	 * Create an expression "lessThan"
	 * Example:  (field < value)
	 * 
	 * @param resourceID Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	@Override
	public IFilter lessThan(String resourceID, Object value)
	throws SearchException{
		return this._lessThan(null, resourceID, value);
	}
	@Override
	public IStatisticFilter lessThan(StatisticResourceFilter resourceID, Object value)
	throws SearchException{
		return this._lessThan(resourceID, null, value);
	}
	private IStatisticFilter _lessThan(StatisticResourceFilter resourceStatID,String resourceID, Object value)
			throws SearchException{
		try{
			StatisticFilterName statisticFilter = null;
			if(resourceStatID!=null){
				this.check(resourceStatID);
				statisticFilter = resourceStatID.getStatisticFilterName();
				resourceID = resourceStatID.getResourceID();
			}
			List<IField> fieldsResourceName = this.getIFieldForResourceName(statisticFilter);
			if(fieldsResourceName.size()==1){
				IExpression expr = this.newIExpression();
				expr.and();
				IField fieldResourceName = fieldsResourceName.get(0);
				expr.equals(fieldResourceName, resourceID);
				expr.lessThan(this.getIFieldForResourceValue(fieldResourceName), ContentFormatter.toString(value));
				this.expression.and(expr);
			}
			else{
				IExpression exprEsterna = this.newIExpression();
				exprEsterna.or();
				for (IField fieldResourceName : fieldsResourceName) {
					IExpression expr = this.newIExpression();
					expr.and();
					expr.equals(fieldResourceName, resourceID);
					expr.lessThan(this.getIFieldForResourceValue(fieldResourceName), ContentFormatter.toString(value));
					exprEsterna.or(expr);
				}
				this.expression.and(exprEsterna);
			}
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	/**
	 * Create an expression "lessEquals"
	 * Example:  (field <= value)
	 * 
	 * @param resourceID Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	@Override
	public IFilter lessEquals(String resourceID, Object value)
	throws SearchException{
		return this._lessEquals(null, resourceID, value);
	}
	@Override
	public IStatisticFilter lessEquals(StatisticResourceFilter resourceID, Object value)
	throws SearchException{
		return this._lessEquals(resourceID, null, value);
	}
	private IStatisticFilter _lessEquals(StatisticResourceFilter resourceStatID,String resourceID, Object value)
			throws SearchException{
		try{
			StatisticFilterName statisticFilter = null;
			if(resourceStatID!=null){
				this.check(resourceStatID);
				statisticFilter = resourceStatID.getStatisticFilterName();
				resourceID = resourceStatID.getResourceID();
			}
			List<IField> fieldsResourceName = this.getIFieldForResourceName(statisticFilter);
			if(fieldsResourceName.size()==1){
				IExpression expr = this.newIExpression();
				expr.and();
				IField fieldResourceName = fieldsResourceName.get(0);
				expr.equals(fieldResourceName, resourceID);
				expr.lessEquals(this.getIFieldForResourceValue(fieldResourceName), ContentFormatter.toString(value));
				this.expression.and(expr);
			}
			else{
				IExpression exprEsterna = this.newIExpression();
				exprEsterna.or();
				for (IField fieldResourceName : fieldsResourceName) {
					IExpression expr = this.newIExpression();
					expr.and();
					expr.equals(fieldResourceName, resourceID);
					expr.lessEquals(this.getIFieldForResourceValue(fieldResourceName), ContentFormatter.toString(value));
					exprEsterna.or(expr);
				}
				this.expression.and(exprEsterna);
			}
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	
	
	/* ************ SPECIAL COMPARATOR *********** */
	
	/**
	 * Create an expression "is null"
	 * Example:  ( field is null )
	 * 
	 * @param resourceID Resource identifier
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	@Override
	public IFilter isNull(String resourceID) throws SearchException{
		return this._isNull(null, resourceID);
	}
	@Override
	public IStatisticFilter isNull(StatisticResourceFilter resourceID) throws SearchException{
		return this._isNull(resourceID, null);
	}
	private IStatisticFilter _isNull(StatisticResourceFilter resourceStatID,String resourceID) throws SearchException{
		try{
			StatisticFilterName statisticFilter = null;
			if(resourceStatID!=null){
				this.check(resourceStatID);
				statisticFilter = resourceStatID.getStatisticFilterName();
				resourceID = resourceStatID.getResourceID();
			}
			List<IField> fieldsResourceName = this.getIFieldForResourceName(statisticFilter);
			if(fieldsResourceName.size()==1){
				IExpression expr = this.newIExpression();
				expr.and();
				IField fieldResourceName = fieldsResourceName.get(0);
				expr.equals(fieldResourceName, resourceID);
				expr.isNull(this.getIFieldForResourceValue(fieldResourceName));
				this.expression.and(expr);
			}
			else{
				IExpression exprEsterna = this.newIExpression();
				exprEsterna.or();
				for (IField fieldResourceName : fieldsResourceName) {
					IExpression expr = this.newIExpression();
					expr.and();
					expr.equals(fieldResourceName, resourceID);
					expr.isNull(this.getIFieldForResourceValue(fieldResourceName));
					exprEsterna.or(expr);
				}
				this.expression.and(exprEsterna);
			}
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	/**
	 * Create an expression "is not null"
	 * Example:  ( field is not null )
	 * 
	 * @param resourceID Resource identifier
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	@Override
	public IFilter isNotNull(String resourceID) throws SearchException{
		return this._isNotNull(null, resourceID);
	}
	@Override
	public IStatisticFilter isNotNull(StatisticResourceFilter resourceID) throws SearchException{
		return this._isNotNull(resourceID,null);
	}
	private IStatisticFilter _isNotNull(StatisticResourceFilter resourceStatID,String resourceID) throws SearchException{
		try{
			StatisticFilterName statisticFilter = null;
			if(resourceStatID!=null){
				this.check(resourceStatID);
				statisticFilter = resourceStatID.getStatisticFilterName();
				resourceID = resourceStatID.getResourceID();
			}
			List<IField> fieldsResourceName = this.getIFieldForResourceName(statisticFilter);
			if(fieldsResourceName.size()==1){
				IExpression expr = this.newIExpression();
				expr.and();
				IField fieldResourceName = fieldsResourceName.get(0);
				expr.equals(fieldResourceName, resourceID);
				expr.isNotNull(this.getIFieldForResourceValue(fieldResourceName));
				this.expression.and(expr);
			}
			else{
				IExpression exprEsterna = this.newIExpression();
				exprEsterna.or();
				for (IField fieldResourceName : fieldsResourceName) {
					IExpression expr = this.newIExpression();
					expr.and();
					expr.equals(fieldResourceName, resourceID);
					expr.isNotNull(this.getIFieldForResourceValue(fieldResourceName));
					exprEsterna.or(expr);
				}
				this.expression.and(exprEsterna);
			}
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	/**
	 * Create an expression "is empty"
	 * Example:  ( field = '' )
	 * 
	 * @param resourceID Resource identifier
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	@Override
	public IFilter isEmpty(String resourceID) throws SearchException{
		return this._isEmpty(null, resourceID);
	}
	@Override
	public IStatisticFilter isEmpty(StatisticResourceFilter resourceID) throws SearchException{
		return this._isEmpty(resourceID,null);
	}
	private IStatisticFilter _isEmpty(StatisticResourceFilter resourceStatID,String resourceID) throws SearchException{
		try{
			StatisticFilterName statisticFilter = null;
			if(resourceStatID!=null){
				this.check(resourceStatID);
				statisticFilter = resourceStatID.getStatisticFilterName();
				resourceID = resourceStatID.getResourceID();
			}
			List<IField> fieldsResourceName = this.getIFieldForResourceName(statisticFilter);
			if(fieldsResourceName.size()==1){
				IExpression expr = this.newIExpression();
				expr.and();
				IField fieldResourceName = fieldsResourceName.get(0);
				expr.equals(fieldResourceName, resourceID);
				expr.isEmpty(this.getIFieldForResourceValue(fieldResourceName));
				this.expression.and(expr);
			}
			else{
				IExpression exprEsterna = this.newIExpression();
				exprEsterna.or();
				for (IField fieldResourceName : fieldsResourceName) {
					IExpression expr = this.newIExpression();
					expr.and();
					expr.equals(fieldResourceName, resourceID);
					expr.isEmpty(this.getIFieldForResourceValue(fieldResourceName));
					exprEsterna.or(expr);
				}
				this.expression.and(exprEsterna);
			}
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	/**
	 * Create an expression "is not empty"
	 * Example:  ( field <> '' )
	 * 
	 * @param resourceID Resource identifier
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	@Override
	public IFilter isNotEmpty(String resourceID) throws SearchException{
		return this._isNotEmpty(null, resourceID);
	}
	@Override
	public IStatisticFilter isNotEmpty(StatisticResourceFilter resourceID) throws SearchException{
		return this._isNotEmpty(resourceID, null);
	}
	private IStatisticFilter _isNotEmpty(StatisticResourceFilter resourceStatID,String resourceID) throws SearchException{
		try{
			StatisticFilterName statisticFilter = null;
			if(resourceStatID!=null){
				this.check(resourceStatID);
				statisticFilter = resourceStatID.getStatisticFilterName();
				resourceID = resourceStatID.getResourceID();
			}
			List<IField> fieldsResourceName = this.getIFieldForResourceName(statisticFilter);
			if(fieldsResourceName.size()==1){
				IExpression expr = this.newIExpression();
				expr.and();
				IField fieldResourceName = fieldsResourceName.get(0);
				expr.equals(fieldResourceName, resourceID);
				expr.isNotEmpty(this.getIFieldForResourceValue(fieldResourceName));
				this.expression.and(expr);
			}
			else{
				IExpression exprEsterna = this.newIExpression();
				exprEsterna.or();
				for (IField fieldResourceName : fieldsResourceName) {
					IExpression expr = this.newIExpression();
					expr.and();
					expr.equals(fieldResourceName, resourceID);
					expr.isNotEmpty(this.getIFieldForResourceValue(fieldResourceName));
					exprEsterna.or(expr);
				}
				this.expression.and(exprEsterna);
			}
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	
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
	@Override
	public IFilter between(String resourceID, Object lower, Object high)
	throws SearchException{
		return this._between(null, resourceID, lower, high);
	}
	@Override
	public IStatisticFilter between(StatisticResourceFilter resourceID, Object lower, Object high)
	throws SearchException{
		return this._between(resourceID, null, lower, high);
	}
	private IStatisticFilter _between(StatisticResourceFilter resourceStatID,String resourceID, Object lower, Object high)
			throws SearchException{
		try{
			StatisticFilterName statisticFilter = null;
			if(resourceStatID!=null){
				this.check(resourceStatID);
				statisticFilter = resourceStatID.getStatisticFilterName();
				resourceID = resourceStatID.getResourceID();
			}
			List<IField> fieldsResourceName = this.getIFieldForResourceName(statisticFilter);
			if(fieldsResourceName.size()==1){
				IExpression expr = this.newIExpression();
				expr.and();
				IField fieldResourceName = fieldsResourceName.get(0);
				expr.equals(fieldResourceName, resourceID);
				expr.between(this.getIFieldForResourceValue(fieldResourceName),ContentFormatter.toString(lower),ContentFormatter.toString(high));
				this.expression.and(expr);
			}
			else{
				IExpression exprEsterna = this.newIExpression();
				exprEsterna.or();
				for (IField fieldResourceName : fieldsResourceName) {
					IExpression expr = this.newIExpression();
					expr.and();
					expr.equals(fieldResourceName, resourceID);
					expr.between(this.getIFieldForResourceValue(fieldResourceName),ContentFormatter.toString(lower),ContentFormatter.toString(high));
					exprEsterna.or(expr);
				}
				this.expression.and(exprEsterna);
			}
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
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
	@Override
	public IFilter like(String resourceID, String value) throws SearchException{
		return this._like(null, resourceID, value);
	}
	@Override
	public IStatisticFilter like(StatisticResourceFilter resourceID, String value) throws SearchException{
		return this._like(resourceID, null, value);
	}
	private IStatisticFilter _like(StatisticResourceFilter resourceStatID,String resourceID, String value) throws SearchException{
		try{
			StatisticFilterName statisticFilter = null;
			if(resourceStatID!=null){
				this.check(resourceStatID);
				statisticFilter = resourceStatID.getStatisticFilterName();
				resourceID = resourceStatID.getResourceID();
			}
			List<IField> fieldsResourceName = this.getIFieldForResourceName(statisticFilter);
			if(fieldsResourceName.size()==1){
				IExpression expr = this.newIExpression();
				expr.and();
				IField fieldResourceName = fieldsResourceName.get(0);
				expr.equals(fieldResourceName, resourceID);
				expr.like(this.getIFieldForResourceValue(fieldResourceName),value);
				this.expression.and(expr);
			}
			else{
				IExpression exprEsterna = this.newIExpression();
				exprEsterna.or();
				for (IField fieldResourceName : fieldsResourceName) {
					IExpression expr = this.newIExpression();
					expr.and();
					expr.equals(fieldResourceName, resourceID);
					expr.like(this.getIFieldForResourceValue(fieldResourceName),value);
					exprEsterna.or(expr);
				}
				this.expression.and(exprEsterna);
			}
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	/**
	 * Create an expression "like"
	 * Example:  
	 * 	 LikeMode.ANYWHERE -> ( field like '%value%' )
	 *   LikeMode.EXACT -> ( field like 'value' )
	 *   LikeMode.END -> ( field like '%value' )
	 *   LikeMode.START -> ( field like 'value%' )
	 * 
	 * @param resourceID Resource identifier
	 * @param value Value
	 * @param mode LikeMode
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	@Override
	public IFilter like(String resourceID, String value, LikeMode mode) throws SearchException{
		return this._like(null, resourceID, value, mode);
	}
	@Override
	public IStatisticFilter like(StatisticResourceFilter resourceID, String value, LikeMode mode) throws SearchException{
		return this._like(resourceID, null, value, mode);
	}
	private IStatisticFilter _like(StatisticResourceFilter resourceStatID,String resourceID, String value, LikeMode mode) throws SearchException{
		try{
			StatisticFilterName statisticFilter = null;
			if(resourceStatID!=null){
				this.check(resourceStatID);
				statisticFilter = resourceStatID.getStatisticFilterName();
				resourceID = resourceStatID.getResourceID();
			}
			List<IField> fieldsResourceName = this.getIFieldForResourceName(statisticFilter);
			if(fieldsResourceName.size()==1){
				IExpression expr = this.newIExpression();
				expr.and();
				IField fieldResourceName = fieldsResourceName.get(0);
				expr.equals(fieldResourceName, resourceID);
				expr.like(this.getIFieldForResourceValue(fieldResourceName),value,mode.getLikeGenericProjectValue());
				this.expression.and(expr);
			}
			else{
				IExpression exprEsterna = this.newIExpression();
				exprEsterna.or();
				for (IField fieldResourceName : fieldsResourceName) {
					IExpression expr = this.newIExpression();
					expr.and();
					expr.equals(fieldResourceName, resourceID);
					expr.like(this.getIFieldForResourceValue(fieldResourceName),value,mode.getLikeGenericProjectValue());
					exprEsterna.or(expr);
				}
				this.expression.and(exprEsterna);
			}
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	/**
	 * Create an expression "like" case-insensitive
	 * Example:  ( toLower(field) like '%toLower(value)%' )  
	 * 
	 * @param resourceID Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	@Override
	public IFilter ilike(String resourceID, String value) throws SearchException{
		return this._ilike(null, resourceID, value);
	}
	@Override
	public IStatisticFilter ilike(StatisticResourceFilter resourceID, String value) throws SearchException{
		return this._ilike(resourceID, null, value);
	}
	private IStatisticFilter _ilike(StatisticResourceFilter resourceStatID,String resourceID, String value) throws SearchException{
		try{
			StatisticFilterName statisticFilter = null;
			if(resourceStatID!=null){
				this.check(resourceStatID);
				statisticFilter = resourceStatID.getStatisticFilterName();
				resourceID = resourceStatID.getResourceID();
			}
			List<IField> fieldsResourceName = this.getIFieldForResourceName(statisticFilter);
			if(fieldsResourceName.size()==1){
				IExpression expr = this.newIExpression();
				expr.and();
				IField fieldResourceName = fieldsResourceName.get(0);
				expr.equals(fieldResourceName, resourceID);
				expr.ilike(this.getIFieldForResourceValue(fieldResourceName),value);
				this.expression.and(expr);
			}
			else{
				IExpression exprEsterna = this.newIExpression();
				exprEsterna.or();
				for (IField fieldResourceName : fieldsResourceName) {
					IExpression expr = this.newIExpression();
					expr.and();
					expr.equals(fieldResourceName, resourceID);
					expr.ilike(this.getIFieldForResourceValue(fieldResourceName),value);
					exprEsterna.or(expr);
				}
				this.expression.and(exprEsterna);
			}
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	/**
	 * Create an expression "like" case-insensitive
	 * Example:  
	 *   LikeMode.ANYWHERE -> ( toLower(field) like '%toLower(value)%' )
	 *   LikeMode.EXACT -> ( toLower(field) like 'toLower(value)' )
	 *   LikeMode.END -> ( toLower(field) like '%toLower(value)' )
	 *   LikeMode.START -> ( toLower(field) like 'toLower(value)%' )
	 * 
	 * @param resourceID Resource identifier
	 * @param value Value
	 * @param mode LikeMode
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	@Override
	public IFilter ilike(String resourceID, String value, LikeMode mode) throws SearchException{
		return this._ilike(null, resourceID, value, mode);
	}
	@Override
	public IStatisticFilter ilike(StatisticResourceFilter resourceID, String value, LikeMode mode) throws SearchException{
		return this._ilike(resourceID, null, value, mode);
	}
	private IStatisticFilter _ilike(StatisticResourceFilter resourceStatID,String resourceID, String value, LikeMode mode) throws SearchException{
		try{
			StatisticFilterName statisticFilter = null;
			if(resourceStatID!=null){
				this.check(resourceStatID);
				statisticFilter = resourceStatID.getStatisticFilterName();
				resourceID = resourceStatID.getResourceID();
			}
			List<IField> fieldsResourceName = this.getIFieldForResourceName(statisticFilter);
			if(fieldsResourceName.size()==1){
				IExpression expr = this.newIExpression();
				expr.and();
				IField fieldResourceName = fieldsResourceName.get(0);
				expr.equals(fieldResourceName, resourceID);
				expr.ilike(this.getIFieldForResourceValue(fieldResourceName),value,mode.getLikeGenericProjectValue());
				this.expression.and(expr);
			}
			else{
				IExpression exprEsterna = this.newIExpression();
				exprEsterna.or();
				for (IField fieldResourceName : fieldsResourceName) {
					IExpression expr = this.newIExpression();
					expr.and();
					expr.equals(fieldResourceName, resourceID);
					expr.ilike(this.getIFieldForResourceValue(fieldResourceName),value,mode.getLikeGenericProjectValue());
					exprEsterna.or(expr);
				}
				this.expression.and(exprEsterna);
			}
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}

	
	

	
	

	/* ************ IN *********** */
	
	/**
	 * Create an expression "in" verifying that the value of <var>field</var> is one of those given in the param <var>values</var>
	 * Example:  ( field IN ('values[0]', 'values[1]', ... ,'values[n]')  )
	 * 
	 * @param resourceID Resource identifier
	 * @param values Values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	@Override
	public IFilter in(String resourceID, Object ... values)
	throws SearchException{
		return this._in(null, resourceID, values);
	}
	@Override
	public IStatisticFilter in(StatisticResourceFilter resourceID, Object ... values)
	throws SearchException{
		return this._in(resourceID, null, values);
	}
	private IStatisticFilter _in(StatisticResourceFilter resourceStatID,String resourceID, Object ... values)
			throws SearchException{
		try{
			StatisticFilterName statisticFilter = null;
			if(resourceStatID!=null){
				this.check(resourceStatID);
				statisticFilter = resourceStatID.getStatisticFilterName();
				resourceID = resourceStatID.getResourceID();
			}
			List<IField> fieldsResourceName = this.getIFieldForResourceName(statisticFilter);
			if(fieldsResourceName.size()==1){
				IExpression expr = this.newIExpression();
				expr.and();
				IField fieldResourceName = fieldsResourceName.get(0);
				expr.equals(fieldResourceName, resourceID);
				expr.in(this.getIFieldForResourceValue(fieldResourceName),ContentFormatter.toString(values));
				this.expression.and(expr);
			}
			else{
				IExpression exprEsterna = this.newIExpression();
				exprEsterna.or();
				for (IField fieldResourceName : fieldsResourceName) {
					IExpression expr = this.newIExpression();
					expr.and();
					expr.equals(fieldResourceName, resourceID);
					expr.in(this.getIFieldForResourceValue(fieldResourceName),ContentFormatter.toString(values));
					exprEsterna.or(expr);
				}
				this.expression.and(exprEsterna);
			}
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
		
	/**
	 * Create an expression "in" verifying that the value of <var>field</var> is one of those given in the param <var>values</var>
	 * Example:  ( field IN ('values[0]', 'values[1]', ... ,'values[n]')  )
	 * 
	 * @param resourceID Resource identifier
	 * @param values Values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	@Override
	public IFilter in(String resourceID, Collection<?> values)
	throws SearchException {
		return this._in(null, resourceID, values);
	}
	@Override
	public IStatisticFilter in(StatisticResourceFilter resourceID, Collection<?> values)
	throws SearchException {
		return this._in(resourceID, null, values);
	}
	private IStatisticFilter _in(StatisticResourceFilter resourceStatID,String resourceID, Collection<?> values)
			throws SearchException {
		try{
			
			List<String> collectionString = new ArrayList<>();
			for (Object o : values) {
				collectionString.add(ContentFormatter.toString(o));	
			}
			
			StatisticFilterName statisticFilter = null;
			if(resourceStatID!=null){
				this.check(resourceStatID);
				statisticFilter = resourceStatID.getStatisticFilterName();
				resourceID = resourceStatID.getResourceID();
			}
			List<IField> fieldsResourceName = this.getIFieldForResourceName(statisticFilter);
			if(fieldsResourceName.size()==1){
				IExpression expr = this.newIExpression();
				expr.and();
				IField fieldResourceName = fieldsResourceName.get(0);
				expr.equals(fieldResourceName, resourceID);
				expr.in(this.getIFieldForResourceValue(fieldResourceName),collectionString);
				this.expression.and(expr);
			}
			else{
				IExpression exprEsterna = this.newIExpression();
				exprEsterna.or();
				for (IField fieldResourceName : fieldsResourceName) {
					IExpression expr = this.newIExpression();
					expr.and();
					expr.equals(fieldResourceName, resourceID);
					expr.in(this.getIFieldForResourceValue(fieldResourceName),collectionString);
					exprEsterna.or(expr);
				}
				this.expression.and(exprEsterna);
			}
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	/* ************ CONJUNCTION *********** */
	
	/**
	 *  Adds to the expression in the conjunction "AND" of the espressions given as a parameters
	 *  Example: ( exp1 AND exp2  )
	 *  
	 * @param exp1 Filter combined in AND with <var>exp1</var>
	 * @param exp2 Filter combined in AND with <var>exp2</var>
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	@Override
	public IStatisticFilter and(IFilter exp1, IFilter exp2) throws SearchException{
		try{
			FilterImpl f1 = (FilterImpl) exp1;
			FilterImpl f2 = (FilterImpl) exp2;
			this.expression.and(f1.expression,f2.expression);
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	/**
	 *  Adds to the expression in the conjunction "AND" of the espressions given as a parameters
	 *  Example: ( expressions[1] AND expressions[2] AND ... expressions[N] )
	 *  
	 * @param expressions Expressions combined in AND
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	@Override
	public IStatisticFilter and(IFilter... expressions) throws SearchException{
		try{
			if(expressions!=null && expressions.length>0){
				IExpression [] fs  = new IExpression[expressions.length];
				for (int i = 0; i < expressions.length; i++) {
					fs[i] = ((FilterImpl) expressions[i]).expression;
				}
				this.expression.and(fs);
			}
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	/**
	 *  Adds to the expression in the conjunction "OR" of the espressions given as a parameters
	 *  Example: ( exp1 OR exp2  )
	 *  
	 * @param exp1 Filter combined in OR with <var>exp1</var>
	 * @param exp2 Filter combined in OR with <var>exp2</var>
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	@Override
	public IStatisticFilter or(IFilter exp1, IFilter exp2) throws SearchException{
		try{
			FilterImpl f1 = (FilterImpl) exp1;
			FilterImpl f2 = (FilterImpl) exp2;
			this.expression.or(f1.expression,f2.expression);
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	/**
	 *  Adds to the expression in the conjunction "OR" of the espressions given as a parameters
	 *  Example: ( expressions[1] OR expressions[2] OR ... expressions[N] )
	 *  
	 * @param expressions Expressions combined in OR
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	@Override
	public IStatisticFilter or(IFilter... expressions) throws SearchException{
		try{
			if(expressions!=null && expressions.length>0){
				IExpression [] fs  = new IExpression[expressions.length];
				for (int i = 0; i < expressions.length; i++) {
					fs[i] = ((FilterImpl) expressions[i]).expression;
				}
				this.expression.or(fs);
			}
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	
	/* ************ NEGATION *********** */
	
	/**
	 * Adding to the negation of the expression passed as parameter
	 * Example: ( NOT expression )
	 * 
	 * @param expression Expression
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	@Override
	public IStatisticFilter not(IFilter expression) throws SearchException{
		try{
			FilterImpl f = (FilterImpl) expression;
			this.expression.not(f.expression);
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}

	
	
	
	
	
	
	/* ************ MESSAGE TYPE *********** */

	@Override
	public IStatisticFilter setMessageType(MessageType messageType) throws SearchException{
		try{
			IField f = this.getIFieldForMessageType();
			if(f!=null){
				// Le statistiche non classificano il messaggio rispetto al tipo
				this.expression.equals(f, messageType.name());
			}
			return this;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	
	
	
	/* ************ NEW FILTER *********** */
	
	@Override
	public IStatisticFilter newFilter() throws SearchException{
		return this.newIFilter();
	}
	

	
}
