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
package org.openspcoop2.generic_project.dao;


import java.util.List;
import java.util.Map;

import org.openspcoop2.generic_project.beans.FunctionField;
import org.openspcoop2.generic_project.beans.IDMappingBehaviour;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.beans.Union;
import org.openspcoop2.generic_project.beans.UnionExpression;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.exception.ValidationException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;

/**
 * IServiceSearchWithoutId
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IServiceSearchWithoutId<T> extends IExpressionConstructor {

	public void validate(T object) throws ServiceException,ValidationException,NotImplementedException;
	
	public List<T> findAll(IPaginatedExpression expression) throws ServiceException,NotImplementedException;
	
	public List<T> findAll(IPaginatedExpression expression, IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException,NotImplementedException;
	
	public T find(IExpression expression) throws ServiceException,NotFoundException,MultipleResultException,NotImplementedException;
	
	public T find(IExpression expression, IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException,NotFoundException,MultipleResultException,NotImplementedException;
	
	public NonNegativeNumber count(IExpression expression) throws ServiceException,NotImplementedException;
		
	public List<Object> select(IPaginatedExpression expression, IField field) throws ServiceException,NotFoundException,NotImplementedException;
	
	public List<Object> select(IPaginatedExpression expression, boolean distinct, IField field) throws ServiceException,NotFoundException,NotImplementedException;
	
	public List<Map<String,Object>> select(IPaginatedExpression expression, IField ... field) throws ServiceException,NotFoundException,NotImplementedException;
	
	public List<Map<String,Object>> select(IPaginatedExpression expression, boolean distinct, IField ... field) throws ServiceException,NotFoundException,NotImplementedException;
	
	public Object aggregate(IExpression expression, FunctionField functionField) throws ServiceException,NotFoundException,NotImplementedException;
	
	public Map<String,Object> aggregate(IExpression expression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException;
		
	public List<Map<String,Object>> groupBy(IExpression expression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException;

	public List<Map<String,Object>> groupBy(IPaginatedExpression expression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException;

	public List<Map<String,Object>> union(Union union, UnionExpression ... unionExpression) throws ServiceException,NotFoundException,NotImplementedException;

	public NonNegativeNumber unionCount(Union union, UnionExpression ... unionExpression) throws ServiceException,NotFoundException,NotImplementedException;
	
	public List<List<Object>> nativeQuery(String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException;

}
