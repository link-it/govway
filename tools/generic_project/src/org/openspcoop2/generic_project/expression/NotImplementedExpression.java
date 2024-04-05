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
package org.openspcoop2.generic_project.expression;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;

/**
 * NotImplementedExpression
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NotImplementedExpression implements IExpression {

	private static final String NOT_IMPLEMENTED = "Not Implemented";
	
	@Override
	public IExpression equals(IField field, Object value)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression notEquals(IField field, Object value)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression allEquals(Map<IField, Object> propertyNameValues)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression allNotEquals(Map<IField, Object> propertyNameValues)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression allEquals(Map<IField, Object> propertyNameValues,boolean andConjunction)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression allNotEquals(Map<IField, Object> propertyNameValues,boolean andConjunction)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression greaterThan(IField field, Object value)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression greaterEquals(IField field, Object value)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression lessThan(IField field, Object value)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression lessEquals(IField field, Object value)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression between(IField field, Object lower, Object high)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression like(IField field, String value)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression like(IField field, String value, LikeMode mode)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression ilike(IField field, String value)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression ilike(IField field, String value, LikeMode mode)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}
	


	
	
	
	/* ************ DATE PART *********** */
	
	@Override
	public IExpression isYear(IField field, int value) throws ExpressionNotImplementedException,ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}
	@Override
	public IExpression isYear(IField field, String value) throws ExpressionNotImplementedException,ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}
	
	@Override
	public IExpression isMonth(IField field, int value) throws ExpressionNotImplementedException,ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}
	@Override
	public IExpression isMonth(IField field, String value) throws ExpressionNotImplementedException,ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}
	
	@Override
	public IExpression isDayOfMonth(IField field, int value) throws ExpressionNotImplementedException,ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}
	@Override
	public IExpression isDayOfMonth(IField field, String value) throws ExpressionNotImplementedException,ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}
	
	@Override
	public IExpression isDayOfYear(IField field, int value) throws ExpressionNotImplementedException,ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}
	@Override
	public IExpression isDayOfYear(IField field, String value) throws ExpressionNotImplementedException,ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}
	
	@Override
	public IExpression isDayOfWeek(IField field, int value) throws ExpressionNotImplementedException,ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}
	@Override
	public IExpression isDayOfWeek(IField field, String value) throws ExpressionNotImplementedException,ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}
	
	@Override
	public IExpression isHour(IField field, int value) throws ExpressionNotImplementedException,ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}
	@Override
	public IExpression isHour(IField field, String value) throws ExpressionNotImplementedException,ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}
	
	@Override
	public IExpression isMinute(IField field, int value) throws ExpressionNotImplementedException,ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}
	@Override
	public IExpression isMinute(IField field, String value) throws ExpressionNotImplementedException,ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}
	
	@Override
	public IExpression isSecond(IField field, int second) throws ExpressionNotImplementedException,ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}
	@Override
	public IExpression isSecond(IField field, double second) throws ExpressionNotImplementedException,ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}
	@Override
	public IExpression isSecond(IField field, String value) throws ExpressionNotImplementedException,ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}
	
	@Override
	public IExpression isFullDayName(IField field, String value) throws ExpressionNotImplementedException,ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression isShortDayName(IField field, String value) throws ExpressionNotImplementedException,ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression in(IField field, Object... values)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression in(IField field, Collection<?> values)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression isNull(IField field)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression isNotNull(IField field)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression isEmpty(IField field)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression isNotEmpty(IField field)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression and(IExpression f1, IExpression f2)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression and(IExpression... filtri)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression or(IExpression f1, IExpression f2)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression or(IExpression... filtri)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression not(IExpression filtro)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression not() throws ExpressionNotImplementedException,
	ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	public IExpression setANDLogicOperator(boolean andLogicOperator)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression and() throws ExpressionNotImplementedException,
	ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression or() throws ExpressionNotImplementedException,
	ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IPaginatedExpression sortOrder(SortOrder sortOrder)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IPaginatedExpression addOrder(IField field)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}
	
	@Override
	public IExpression addOrder(IField field, SortOrder sortOrder) 
	throws ExpressionNotImplementedException,ExpressionException{
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression addGroupBy(IField field)
			throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}
	
	@Override
	public boolean inUseField(IField field,boolean checkOnlyWhereCondition) throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public boolean inUseModel(IModel<?> model,boolean checkOnlyWhereCondition) throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public boolean isWhereConditionsPresent() throws ExpressionNotImplementedException,
			ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public List<Object> getWhereConditionFieldValues(IField field)
			throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public List<IField> getFields(boolean onlyWhereCondition) throws ExpressionNotImplementedException,
			ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public IExpression addForceIndex(Index index) throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException(NOT_IMPLEMENTED);
	}

	@Override
	public void addProperty(String name, Object value) {
		// nop
	}

	@Override
	public Object getProperty(String name) {
		// nop
		return null;
	}

}
