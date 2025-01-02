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
package org.openspcoop2.generic_project.dao.jdbc;


import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.openspcoop2.generic_project.beans.FunctionField;
import org.openspcoop2.generic_project.beans.IDMappingBehaviour;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.InUse;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.beans.Union;
import org.openspcoop2.generic_project.beans.UnionExpression;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.utils.sql.ISQLQueryObject;

/**
 * IJDBCServiceSearchWithId
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IJDBCServiceSearchWithId<T,K,SM> extends IJDBCServiceSearchWithId_DB<T,K,SM>{

	public K convertToId(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject,T id) throws NotImplementedException,ServiceException,Exception;
	
	public T get(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject, K id, IDMappingBehaviour idMappingResolutionBehaviour) throws ServiceException,NotFoundException,MultipleResultException,NotImplementedException, Exception;
	
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject,K id) throws MultipleResultException,NotImplementedException,ServiceException,Exception;
	
	public List<K> findAllIds(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject,JDBCPaginatedExpression expression, IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception;
	
	public List<T> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject,JDBCPaginatedExpression expression, IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception;
	
	public T find(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject,JDBCExpression expression, IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException,MultipleResultException,NotImplementedException,ServiceException,Exception;
	
	public NonNegativeNumber count(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject,JDBCExpression expression) throws NotImplementedException,ServiceException,Exception;
	
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject,K id) throws NotFoundException,NotImplementedException,ServiceException,Exception;
	
	public List<Object> select(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject, JDBCPaginatedExpression expression, IField field) throws ServiceException,NotFoundException,NotImplementedException,Exception;
	
	public List<Object> select(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject, JDBCPaginatedExpression expression, boolean distinct, IField field) throws ServiceException,NotFoundException,NotImplementedException,Exception;
		
	public List<Map<String,Object>> select(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject, JDBCPaginatedExpression expression, IField ... field) throws ServiceException,NotFoundException,NotImplementedException,Exception;
	
	public List<Map<String,Object>> select(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject, JDBCPaginatedExpression expression, boolean distinct, IField ... field) throws ServiceException,NotFoundException,NotImplementedException,Exception;
		
	public Object aggregate(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject, JDBCExpression expression, FunctionField functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception;
	
	public Map<String,Object> aggregate(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject, JDBCExpression expression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception;
		
	public List<Map<String,Object>> groupBy(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject,JDBCExpression expression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception;

	public List<Map<String,Object>> groupBy(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject,JDBCPaginatedExpression expression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception;

	public List<Map<String,Object>> union(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject,Union union, UnionExpression ... unionExpression) throws ServiceException,NotFoundException,NotImplementedException,Exception;

	public NonNegativeNumber unionCount(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject,Union union, UnionExpression ... unionExpression) throws ServiceException,NotFoundException,NotImplementedException,Exception;
	
	public List<List<Object>> nativeQuery(JDBCServiceManagerProperties jdbcProperties, Logger log,Connection connection,ISQLQueryObject sqlObject, String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException, Exception;

}
