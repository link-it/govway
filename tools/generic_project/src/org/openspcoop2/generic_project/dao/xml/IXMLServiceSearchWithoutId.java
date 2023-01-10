/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.generic_project.dao.xml;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
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

/**
 * IXMLServiceSearchWithoutId
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IXMLServiceSearchWithoutId<XML,T,SM> extends IXMLExpressionConstructor<SM> {

	public List<T> findAll(Logger log,XML xmlRoot,XMLPaginatedExpression expression, IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception;
	
	public T find(Logger log,XML xmlRoot,XMLExpression expression, IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException,MultipleResultException,NotImplementedException,ServiceException,Exception;
	
	public NonNegativeNumber count(Logger log,XML xmlRoot,XMLExpression expression) throws NotImplementedException,ServiceException,Exception;

	public List<Object> select(Logger log,XML xmlRoot,XMLPaginatedExpression expression, IField field) throws ServiceException,NotFoundException,NotImplementedException,Exception;
	
	public List<Object> select(Logger log,XML xmlRoot,XMLPaginatedExpression expression, boolean distinct, IField field) throws ServiceException,NotFoundException,NotImplementedException,Exception;
		
	public List<Map<String,Object>> select(Logger log,XML xmlRoot,XMLPaginatedExpression expression, IField ... field) throws ServiceException,NotFoundException,NotImplementedException,Exception;
	
	public List<Map<String,Object>> select(Logger log,XML xmlRoot,XMLPaginatedExpression expression, boolean distinct, IField ... field) throws ServiceException,NotFoundException,NotImplementedException,Exception;
		
	public Object aggregate(Logger log,XML xmlRoot,XMLExpression expression, FunctionField functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception;
	
	public Map<String,Object> aggregate(Logger log,XML xmlRoot,XMLExpression expression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception;
		
	public List<Map<String,Object>> groupBy(Logger log,XML xmlRoot,XMLExpression expression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception;

	public List<Map<String,Object>> groupBy(Logger log,XML xmlRoot,XMLPaginatedExpression expression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception;

	public List<Map<String,Object>> union(Logger log,XML xmlRoot,Union union, UnionExpression ... unionExpression) throws ServiceException,NotFoundException,NotImplementedException,Exception;

	public NonNegativeNumber unionCount(Logger log,XML xmlRoot,Union union, UnionExpression ... unionExpression) throws ServiceException,NotFoundException,NotImplementedException,Exception;

	public List<List<Object>> nativeQuery(Logger log,XML xmlRoot, String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException, Exception;

}
