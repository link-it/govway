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
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;

/**
 * NotImplementedExpression
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NotImplementedExpression implements IExpression {

	@Override
	public IExpression equals(IField field, Object value)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression notEquals(IField field, Object value)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression allEquals(Map<IField, Object> propertyNameValues)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression allNotEquals(Map<IField, Object> propertyNameValues)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression allEquals(Map<IField, Object> propertyNameValues,boolean andConjunction)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression allNotEquals(Map<IField, Object> propertyNameValues,boolean andConjunction)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression greaterThan(IField field, Object value)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression greaterEquals(IField field, Object value)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression lessThan(IField field, Object value)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression lessEquals(IField field, Object value)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression between(IField field, Object lower, Object high)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression like(IField field, String value)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression like(IField field, String value, LikeMode mode)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression ilike(IField field, String value)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression ilike(IField field, String value, LikeMode mode)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression in(IField field, Object... values)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression in(IField field, Collection<?> values)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression isNull(IField field)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression isNotNull(IField field)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression isEmpty(IField field)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression isNotEmpty(IField field)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression and(IExpression f1, IExpression f2)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression and(IExpression... filtri)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression or(IExpression f1, IExpression f2)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression or(IExpression... filtri)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression not(IExpression filtro)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression not() throws ExpressionNotImplementedException,
	ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	public IExpression setANDLogicOperator(boolean andLogicOperator)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression and() throws ExpressionNotImplementedException,
	ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression or() throws ExpressionNotImplementedException,
	ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IPaginatedExpression sortOrder(SortOrder sortOrder)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IPaginatedExpression addOrder(IField field)
	throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}
	
	@Override
	public IExpression addOrder(IField field, SortOrder sortOrder) 
	throws ExpressionNotImplementedException,ExpressionException{
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public IExpression addGroupBy(IField field)
			throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}
	
	@Override
	public boolean inUseField(IField field,boolean checkOnlyWhereCondition) throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public boolean inUseModel(IModel<?> model,boolean checkOnlyWhereCondition) throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public boolean isWhereConditionsPresent() throws ExpressionNotImplementedException,
			ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public List<Object> getWhereConditionFieldValues(IField field)
			throws ExpressionNotImplementedException, ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

	@Override
	public List<IField> getFields(boolean onlyWhereCondition) throws ExpressionNotImplementedException,
			ExpressionException {
		throw new ExpressionNotImplementedException("Not Implemented");
	}

}
