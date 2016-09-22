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
package org.openspcoop2.generic_project.dao.jdbc.utils;


import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.openspcoop2.generic_project.beans.AliasField;
import org.openspcoop2.generic_project.beans.ComplexField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.Function;
import org.openspcoop2.generic_project.beans.FunctionField;
import org.openspcoop2.generic_project.beans.IAliasTableField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.beans.Union;
import org.openspcoop2.generic_project.beans.UnionExpression;
import org.openspcoop2.generic_project.beans.UnionOrderedColumn;
import org.openspcoop2.generic_project.beans.UpdateField;
import org.openspcoop2.generic_project.beans.UpdateModel;
import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceSearchSingleObject;
import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceSearchWithId;
import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceSearchWithoutId;
import org.openspcoop2.generic_project.dao.jdbc.JDBCExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCPaginatedExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerProperties;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.generic_project.expression.impl.OrderedField;
import org.openspcoop2.generic_project.expression.impl.sql.ExpressionSQL;
import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.utils.jdbc.JDBCAdapterException;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLQueryObjectCore;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * JDBCUtilities
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCUtilities {

	/* *** OTHER *** */
	
	public static void setFields(ISQLQueryObject sqlQueryObject,JDBCPaginatedExpression paginatedExpression, List<String> aliasField, IField ... field) throws ExpressionException{
		setFields(sqlQueryObject, paginatedExpression, aliasField, true, field);
	}
	public static void setFields(ISQLQueryObject sqlQueryObject,JDBCPaginatedExpression paginatedExpression, List<String> aliasField, boolean appendTablePrefix , IField ... field) throws ExpressionException{
		if(field!=null){
			for (int i = 0; i < field.length; i++) {
				paginatedExpression.addField(sqlQueryObject, field[i], aliasField.get(i), appendTablePrefix);
			}
		}
	}
	public static void setFields(ISQLQueryObject sqlQueryObject,JDBCExpression expression, List<String> aliasField, IField ... field) throws ExpressionException{
		setFields(sqlQueryObject, expression, aliasField, true, field);
	}
	public static void setFields(ISQLQueryObject sqlQueryObject,JDBCExpression expression, List<String> aliasField, boolean appendTablePrefix, IField ... field) throws ExpressionException{
		if(field!=null){
			for (int i = 0; i < field.length; i++) {
				expression.addField(sqlQueryObject, field[i], aliasField.get(i), appendTablePrefix);
			}
		}
	}
	
	public static void setFields(ISQLQueryObject sqlQueryObject,JDBCPaginatedExpression paginatedExpression, IField ... field) throws ExpressionException{
		setFields(sqlQueryObject, paginatedExpression, true, field);
	}
	public static void setFields(ISQLQueryObject sqlQueryObject,JDBCPaginatedExpression paginatedExpression, boolean appendTablePrefix, IField ... field) throws ExpressionException{
		if(field!=null){
			for (int i = 0; i < field.length; i++) {
				paginatedExpression.addField(sqlQueryObject, field[i], appendTablePrefix);
			}
		}
	}
	public static void setFields(ISQLQueryObject sqlQueryObject,JDBCExpression expression, IField ... field) throws ExpressionException{
		setFields(sqlQueryObject, expression, true, field);
	}
	public static void setFields(ISQLQueryObject sqlQueryObject,JDBCExpression expression, boolean appendTablePrefix, IField ... field) throws ExpressionException{
		if(field!=null){
			for (int i = 0; i < field.length; i++) {
				expression.addField(sqlQueryObject, field[i], appendTablePrefix);
			}
		}
	}
	
	public static void setAliasFields(ISQLQueryObject sqlQueryObject,JDBCPaginatedExpression paginatedExpression, IField ... field) throws ExpressionException{
		setAliasFields(sqlQueryObject, paginatedExpression, true, field);
	}
	public static void setAliasFields(ISQLQueryObject sqlQueryObject,JDBCPaginatedExpression paginatedExpression, boolean appendTablePrefix, IField ... field) throws ExpressionException{
		if(field!=null){
			for (int i = 0; i < field.length; i++) {
				paginatedExpression.addAliasField(sqlQueryObject, field[i], appendTablePrefix);
			}
		}
	}
	public static void setAliasFields(ISQLQueryObject sqlQueryObject,JDBCExpression expression, IField ... field) throws ExpressionException{
		setAliasFields(sqlQueryObject, expression, true, field);
	}
	public static void setAliasFields(ISQLQueryObject sqlQueryObject,JDBCExpression expression, boolean appendTablePrefix, IField ... field) throws ExpressionException{
		if(field!=null){
			for (int i = 0; i < field.length; i++) {
				expression.addAliasField(sqlQueryObject, field[i], appendTablePrefix);
			}
		}
	}
	
	public static void removeFields(ISQLQueryObject sqlQueryObject,JDBCPaginatedExpression paginatedExpression, IField ... field) throws ExpressionException{
		if(field!=null){
			for (int i = 0; i < field.length; i++) {
				paginatedExpression.removeFieldManuallyAdd(field[i]);
			}
		}
	}
	public static void removeFields(ISQLQueryObject sqlQueryObject,JDBCExpression expression, IField ... field) throws ExpressionException{
		if(field!=null){
			for (int i = 0; i < field.length; i++) {
				expression.removeFieldManuallyAdd(field[i]);
			}
		}
	}
	
	
	public static void setFields(ISQLQueryObject sqlQueryObject,JDBCPaginatedExpression paginatedExpression, FunctionField ... functionField) throws ExpressionException{
		setFields(sqlQueryObject, paginatedExpression, true, functionField);
	}
	public static void setFields(ISQLQueryObject sqlQueryObject,JDBCPaginatedExpression paginatedExpression, boolean appendTablePrefix, FunctionField ... functionField) throws ExpressionException{
		if(functionField!=null){
			for (int i = 0; i < functionField.length; i++) {
				paginatedExpression.addField(sqlQueryObject, functionField[i], appendTablePrefix);
			}
		}
	}
	public static void setFields(ISQLQueryObject sqlQueryObject,JDBCExpression expression, FunctionField ... functionField) throws ExpressionException{
		setFields(sqlQueryObject, expression, true, functionField);
	}
	public static void setFields(ISQLQueryObject sqlQueryObject,JDBCExpression expression, boolean appendTablePrefix, FunctionField ... functionField) throws ExpressionException{
		if(functionField!=null){
			for (int i = 0; i < functionField.length; i++) {
				expression.addField(sqlQueryObject, functionField[i], appendTablePrefix);
			}
		}
	}
	
	public static void removeFields(ISQLQueryObject sqlQueryObject,JDBCPaginatedExpression paginatedExpression, FunctionField ... functionField) throws ExpressionException{
		if(functionField!=null){
			for (int i = 0; i < functionField.length; i++) {
				paginatedExpression.removeFieldManuallyAdd(functionField[i]);
			}
		}
	}
	public static void removeFields(ISQLQueryObject sqlQueryObject,JDBCExpression expression, FunctionField ... functionField) throws ExpressionException{
		if(functionField!=null){
			for (int i = 0; i < functionField.length; i++) {
				expression.removeFieldManuallyAdd(functionField[i]);
			}
		}
	}
	
	
	
	private static List<OrderedField> getOrderedFields(IExpression expression){
		List<OrderedField> listOrderedFields = null;
		if(expression instanceof JDBCPaginatedExpression){
			listOrderedFields = ((JDBCPaginatedExpression)expression).getOrderedFields();
		}
		else if(expression instanceof JDBCExpression){
			listOrderedFields = ((JDBCExpression)expression).getOrderedFields();
		}
		return listOrderedFields;
	}
	private static SortOrder getSortOrder(IExpression expression){
		SortOrder sortOrder = null;
		if(expression instanceof JDBCPaginatedExpression){
			sortOrder = ((JDBCPaginatedExpression)expression).getSortOrder();
		}
		else if(expression instanceof JDBCExpression){
			sortOrder = ((JDBCExpression)expression).getSortOrder();
		}
		return sortOrder;
	}
	
	private static List<IField> getGroupByFields(IExpression expression){
		List<IField> listGroupByFields = null;
		if(expression instanceof JDBCPaginatedExpression){
			listGroupByFields = ((JDBCPaginatedExpression)expression).getGroupByFields();
		}
		else if(expression instanceof JDBCExpression){
			listGroupByFields = ((JDBCExpression)expression).getGroupByFields();
		}
		return listGroupByFields;
	}
	
	private static boolean isUsedForCountExpression(IExpression expression){
		boolean isUsed = false;
		if(expression instanceof JDBCPaginatedExpression){
			isUsed = ((JDBCPaginatedExpression)expression).isUsedForCountExpression();
		}
		else if(expression instanceof JDBCExpression){
			isUsed = ((JDBCExpression)expression).isUsedForCountExpression();
		}
		return isUsed;
	}
	
	private static void setUsedForCountExpression(IExpression expression, boolean value){
		if(expression instanceof JDBCPaginatedExpression){
			((JDBCPaginatedExpression)expression).setUsedForCountExpression(value);
		}
		else if(expression instanceof JDBCExpression){
			((JDBCExpression)expression).setUsedForCountExpression(value);
		}
	}
	
	private static void toSqlForPreparedStatementWithFromCondition(IExpression expression,ISQLQueryObject sqlQueryObject,List<Object> listaQuery,String table) throws ExpressionException, ExpressionNotImplementedException{
		if(expression instanceof JDBCPaginatedExpression){
			((JDBCPaginatedExpression)expression).toSqlForPreparedStatementWithFromCondition(sqlQueryObject,listaQuery,table);
		}
		else if(expression instanceof JDBCExpression){
			((JDBCExpression)expression).toSqlForPreparedStatementWithFromCondition(sqlQueryObject,listaQuery,table);
		}
	}
	
	private static List<Object> getFieldsManuallyAdd(IExpression expression) {
		List<Object> list = null;
		if(expression instanceof JDBCPaginatedExpression){
			list = ((JDBCPaginatedExpression)expression).getFieldsManuallyAdd();
		}
		else if(expression instanceof JDBCExpression){
			list = ((JDBCExpression)expression).getFieldsManuallyAdd();
		}
		return list;
	}
	
	
	
	
	

	
	
	
	
	
	/* *** COUNT *** */
	
	public static List<Object> prepareCount(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject,IExpression expression,
			ISQLFieldConverter sqlConverter,IModel<?> model) throws SQLQueryObjectException, JDBCAdapterException, ExpressionException, ExpressionNotImplementedException, ServiceException{
	
		List<OrderedField> listOrderedFields = getOrderedFields(expression);
		SortOrder sortOrder = getSortOrder(expression);
		
		if(listOrderedFields!=null && listOrderedFields.size()>0 && sortOrder!=null && !sortOrder.equals(SortOrder.UNSORTED)){
			throw new ServiceException("OrderBy conditions not allowed in count expression");
		}
	
		List<Object> listaQuery = new ArrayList<Object>();
	
		boolean oldValue = isUsedForCountExpression(expression);
		setUsedForCountExpression(expression,true);
		
		try{
		
			toSqlForPreparedStatementWithFromCondition(expression,sqlQueryObject,listaQuery,
					sqlConverter.toTable(model));
			
			return listaQuery;
		}finally{
			setUsedForCountExpression(expression,oldValue);
		}
	}
	
	public static NonNegativeNumber count(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject,IExpression expression,
			ISQLFieldConverter sqlConverter,IModel<?> model,List<Object> listaQuery) throws SQLQueryObjectException, JDBCAdapterException, ExpressionException, ExpressionNotImplementedException, ServiceException{
		long totale = 0;

		// check group by per creare una ulteriore select count(*) from ( sql originale )
		String sql = null;
		List<IField> listGroupByFields = getGroupByFields(expression);
		if(listGroupByFields!=null && listGroupByFields.size()>0){
			
			// serve per far aggiungere le colonne di group by all'espressione sqlQueryObject
			for (IField iField : listGroupByFields) {
				sqlQueryObject.addSelectField(sqlConverter.toColumn(iField,true));
			}
			
			ISQLQueryObject sqlCountGroup = sqlQueryObject.newSQLQueryObject();
			sqlCountGroup.addSelectCountField("groupCount");
			sqlCountGroup.addFromTable(sqlQueryObject);
			
			sql = sqlCountGroup.createSQLQuery();
		}
		else{
			sql = sqlQueryObject.createSQLQuery();
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		JDBCObject[] params = new JDBCObject[listaQuery.size()];
		int index = 0;
		for (Object param : listaQuery) {
			params[index++] = new JDBCObject(param, param.getClass());
		}

		totale = jdbcUtilities.count(sql, jdbcProperties.isShowSql(), params);

		return new NonNegativeNumber(totale);
	}
	
	
	/* *** SELECT *** */
	
	public static List<Object> selectSingleObject(List<Map<String,Object>> map) throws NotFoundException {
		if(map.size()<=0){
			throw new NotFoundException("No result founds");
		}
		else{
			List<Object> results = new ArrayList<Object>();
			for(int i=0; i<map.size(); i++){
				results.add(map.get(i).values().iterator().next());		
			}
			return results;
		}
	}
	
	
	/* *** AGGREGATE *** */
	
	public static Object selectAggregateObject(Map<String,Object> map,FunctionField functionField) throws NotFoundException {
		if(map.size()<=0){
			throw new NotFoundException("No result founds");
		}
		else{
			return map.get(functionField.getAlias());
		}
	}
	
	
	/* *** _SELECT *** */
	
	private static List<Object> toSqlForPreparedStatementFromCondition(IExpression expression,ISQLQueryObject sqlQueryObject,
			List<Object> listaQuery,List<JDBCObject> params,ISQLFieldConverter sqlConverter,IModel<?> model) throws ExpressionException, ExpressionNotImplementedException{
		
		toSqlForPreparedStatementWithFromCondition(expression,sqlQueryObject,listaQuery,sqlConverter.toTable(model));
		List<Object> returnField = getFieldsManuallyAdd(expression);
		for (Object param : listaQuery) {
			params.add( new JDBCObject(param, param.getClass()) );
		}
		
		return returnField;
	}
	
	private static List<Object> eliminaDuplicati(Collection<Object> returnField){
		List<Object> listSenzaDuplicati = new ArrayList<Object>();
		for (Object o : returnField) {
			if(o instanceof IField){
				IField field = (IField) o;
				
				boolean found = false;
				for (Object check : listSenzaDuplicati) {
					if(check instanceof IField){
						IField iFieldCheck = (IField) check;
						if(iFieldCheck.equals(field)){
							// gia esiste
							found = true;
							break;
						}
					}
				}
				
				if(!found){
					listSenzaDuplicati.add(o);
				}
			}
			else if(o instanceof FunctionField){
				
				FunctionField field = (FunctionField) o;
				
				boolean found = false;
				for (Object check : listSenzaDuplicati) {
					if(check instanceof FunctionField){
						FunctionField functionFieldCheck = (FunctionField) check;
						if(functionFieldCheck.equals(field)){
							// gia esiste
							found = true;
							break;
						}
					}
				}
				
				if(!found){
					listSenzaDuplicati.add(o);
				}
			
			}
		}
		return listSenzaDuplicati;
	}
	
	private static List<Class<?>> readClassTypes(Collection<Object> returnField){
		List<Class<?>> returnClassTypes = new ArrayList<Class<?>>();
		for (Object o : returnField) {
			if(o instanceof IField){
				IField field = (IField) o;
				returnClassTypes.add(field.getFieldType());
			}
			else if(o instanceof FunctionField){
				FunctionField field = (FunctionField) o;
				if(field.getFieldType().toString().equals(Date.class.toString()) ||
						field.getFieldType().toString().equals(java.sql.Date.class.toString()) ||
						field.getFieldType().toString().equals(Timestamp.class.toString()) ||
						field.getFieldType().toString().equals(Calendar.class.toString())){
					returnClassTypes.add(Long.class);
				}
				else{
					returnClassTypes.add(field.getFieldType());
				}			
			}
		}
		return returnClassTypes;
	}
	
	private static List<List<String>> readAliases(Collection<Object> returnField) throws ServiceException{
		
		// inizializzo
		List<List<String>> returnClassAliases = new ArrayList<List<String>>();
		for (int i = 0; i < returnField.size(); i++) {
			returnClassAliases.add(new ArrayList<String>());
		}
		
		// aggiungo i field semplici (foglie, vale per Field,ConstantField,CustomField,NullConstantField)
		List<String> returnClassAliases_simple = new ArrayList<String>();
		int index = 0;
		for (Object o : returnField) {
			if(o instanceof Field){
				IField field = (IField) o;
				String nome = field.getFieldName();
				returnClassAliases_simple.add(nome);
				returnClassAliases.get(index).add(nome);
			}
			index++;
		}
		
		// gestisco tutti gli oggetti
		index = 0;
		for (Object o : returnField) {
			if(o instanceof IField){
				
				if(o instanceof AliasField){
					AliasField af = (AliasField) o;
					
					String nome = af.getAlias();
					if(returnClassAliases_simple.contains(nome)){
						throw new ServiceException("AliasField contains alias ["+nome+"] is already used for simple select field. Choose different alias name");
					}
					returnClassAliases.get(index).add(nome);
					
					IField field = af.getField();
					StringBuffer bf = new StringBuffer();
					buildAliasesPrefix(field,bf);
					String nomeField = bf.toString();
					if(!returnClassAliases_simple.contains(nome)){
						returnClassAliases.get(index).add(nomeField);
					}
				}
				else if(o instanceof IAliasTableField){
					StringBuffer bf = new StringBuffer();
					buildAliasesPrefix((IField)o,bf);
					returnClassAliases.get(index).add(bf.toString());
				}
				else if(o instanceof ComplexField){
					StringBuffer bf = new StringBuffer();
					buildAliasesPrefix((IField)o,bf);
					returnClassAliases.get(index).add(bf.toString());
				}

			}
			else if(o instanceof FunctionField){
				FunctionField field = (FunctionField) o;
				String nome = field.getAlias();	
				if(returnClassAliases_simple.contains(nome)){
					throw new ServiceException("FunctionAliasName ["+nome+"] is already used for simple select field. Choose different alias name");
				}
				returnClassAliases.get(index).add(nome);
			}
			index++;
		}

		return returnClassAliases;
	}
	
	public static String getAlias(Object object) throws ServiceException{
		List<Object> l = new ArrayList<Object>();
		l.add(object);
		List<List<String>> ll = readAliases(l);
		if( (ll==null) || ll.size()<=0){
			throw new ServiceException("Build alias error");
		}
		if(ll.get(0)==null || ll.get(0).size()<=0){
			throw new ServiceException("Build alias error (internal)");
		}
		return ll.get(0).get(0);
	}
	
	private static void buildAliasesPrefix(IField field,StringBuffer prefix){
		
		if(field instanceof IAliasTableField){
			prefix.append(((IAliasTableField)field).getAliasTable()).
				append(".").
				append(field.getFieldName());
		}
		else{
		
			if(field instanceof ComplexField){
				ComplexField c = (ComplexField) field;
				buildAliasesPrefix(c.getFather(), prefix);
			}
			
			if(prefix.length()>0){
				prefix.append(".");
			}
			prefix.append(field.getFieldName());
				
		}
	}

	public static List<Object> prepareSelect(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject,
			IExpression expression,ISQLFieldConverter sqlConverter,IModel<?> model,
			List<Object> listaQuery,List<JDBCObject> listaParams) throws NotFoundException, ServiceException, SQLQueryObjectException, JDBCAdapterException, ExpressionException, ExpressionNotImplementedException{
		
		List<Object> returnField = 
			org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.toSqlForPreparedStatementFromCondition(expression,sqlQueryObject,listaQuery,listaParams,
							sqlConverter,model);
			
		return returnField;
	}
		
	public static ISQLQueryObject prepareSqlQueryObjectForSelectDistinct(boolean distinct, ISQLQueryObject sqlQueryObject, IExpression expression,
			Logger log, ISQLFieldConverter sqlFieldConverter,IField ... field) throws SQLQueryObjectException, ExpressionException{
		ISQLQueryObject sqlQueryObjectDistinct = null;
		if(distinct){
			
			if(((SQLQueryObjectCore)sqlQueryObject).isSelectForUpdate()){
				throw new SQLQueryObjectException("Non è possibile abilitare il comando 'selectForUpdate' se viene utilizzata la clausola DISTINCT");
			}
			
			sqlQueryObjectDistinct = sqlQueryObject.newSQLQueryObject();
			JDBCPaginatedExpression paginatedExpressionTmp = new JDBCPaginatedExpression(sqlFieldConverter);
			org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.setAliasFields(sqlQueryObjectDistinct,paginatedExpressionTmp,false,field);
			sqlQueryObjectDistinct.setSelectDistinct(true);
			List<OrderedField> listOrderedFields = getOrderedFields(expression);
			SortOrder sortOrder = getSortOrder(expression);
			if(listOrderedFields!=null &&  listOrderedFields.size()>0 &&
					sortOrder!=null &&
					!SortOrder.UNSORTED.equals(sortOrder)){
				boolean orderBy = false;
				for (OrderedField orderedFieldBean : listOrderedFields) {
					IField orderedField = orderedFieldBean.getField();
					boolean contains = false;
					for (int i = 0; i < field.length; i++) {
						if(orderedField.equals(field[i])){
							contains = true;
							break;
						}
					}
					if(contains){
						sqlQueryObjectDistinct.addOrderBy(sqlFieldConverter.toColumn(orderedField, false), SortOrder.ASC.equals(orderedFieldBean.getSortOrder()));
						orderBy = true;
					}
				}
				if(orderBy)
					sqlQueryObjectDistinct.setSortType(SortOrder.ASC.equals(sortOrder));
			}
		}
		return sqlQueryObjectDistinct;
	}
	
	public static ISQLQueryObject prepareSqlQueryObjectForSelectDistinct(ISQLQueryObject sqlQueryObject, ISQLQueryObject sqlQueryObjectDistinct) throws SQLQueryObjectException{
		ISQLQueryObject sqlQueryObjectExecute = sqlQueryObject;
		if(sqlQueryObjectDistinct!=null){
			sqlQueryObjectDistinct.addFromTable(sqlQueryObject);
			sqlQueryObjectExecute = sqlQueryObjectDistinct;
		}
		return sqlQueryObjectExecute;
	}
	
	public static List<Map<String,Object>> select(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject,
			IExpression expression,ISQLFieldConverter sqlConverter,IModel<?> model,
			List<Object> listaQuery,List<JDBCObject> listaParams, List<Object> returnField) throws NotFoundException, ServiceException, SQLQueryObjectException, JDBCAdapterException, ExpressionException, ExpressionNotImplementedException{
		
		// Se sono impostati groupBy, le condizioni di order by devono essere colonne definite anche nel group by
		List<IField> listGroupByFields = getGroupByFields(expression);
		if(listGroupByFields!=null && listGroupByFields.size()>0){
			List<OrderedField> listOrderedFields = getOrderedFields(expression);
			if(listOrderedFields!=null && listOrderedFields.size()>0){
				for (OrderedField orderedField : listOrderedFields) {
					IField iField = orderedField.getField();
					if(listGroupByFields.contains(iField)==false){
						throw new ServiceException("The field used for order by condition is invalid because it is not contained in GROUP BY clause. Field: ["+iField.toString()+"]");
					}
				}
			}
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		String sql = sqlQueryObject.createSQLQuery();

		JDBCObject[] params = null;
		if(listaParams.size()>0){
			params = listaParams.toArray(new JDBCObject[1]);
		}
		
		returnField = eliminaDuplicati(returnField);
		
		List<Class<?>> returnClassTypes = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.readClassTypes(returnField);
		List<List<String>> returnClassAliases = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.readAliases(returnField);
				
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		
		List<List<Object>> resultExecuteQuery = jdbcUtilities.executeQuery(sql, jdbcProperties.isShowSql(), returnClassTypes ,params);
		if(resultExecuteQuery.size()>0){			
			for (int i = 0; i < resultExecuteQuery.size(); i++) {
				List<Object> lista = resultExecuteQuery.get(i);
				if(lista==null){
					throw new ServiceException("Result["+i+"] is null?");
				}
				if(lista.size()!=returnField.size()){
					throw new ServiceException("Result["+i+"] has wrong length (expected:"+returnField.size()+" founded:"+lista.size()+")");
				}
				Hashtable<String,Object> resultList = new Hashtable<String, Object>();
				for (int j = 0; j < lista.size(); j++) {
					Object object = lista.get(j);
					for (String key : returnClassAliases.get(j)) {
						if(object!=null)
							resultList.put(key, object);
						else
							resultList.put(key, org.apache.commons.lang.ObjectUtils.NULL);	
					}
				}
				result.add(resultList);
			}		
		}
		
		if(result.size()<=0){
			throw new NotFoundException("No result founds");
		}
		
		return result;
	}
	
	
	/* *** UNION *** */
	
	public static List<Class<?>> checkUnionExpression(UnionExpression ... expression) throws ServiceException{
		if(expression==null || expression.length<=0){
			throw new ServiceException("UnionExpression not found");
		}
		if(expression.length==1){
			throw new ServiceException("At least two expressions are required");
		}
		
		List<Class<?>> listClassReturnType = new ArrayList<Class<?>>();
		
		for (int i = 0; i < expression.length; i++) {
			
			UnionExpression ue = expression[i];
			List<String> aliasUe = ue.getReturnFieldAliases();
			
			if(aliasUe.size()<=0){
				throw new ServiceException("The expression n."+(i+1)+" contains no select field");
			}
			
			// check with other expression
			for (int j = 0; j < expression.length; j++) {
				if(j==i){
					continue;
				}
				UnionExpression checkUe = expression[j];
				List<String> checkAliasUe = checkUe.getReturnFieldAliases();
				
				if(checkAliasUe.size()<=0){
					throw new ServiceException("The expression n."+(j+1)+" contains no select field");
				}
				
				String errorMsg = "All expressions must contain the same number of select field with the same alias in the same order. ";
				
				if(checkAliasUe.size()!=aliasUe.size()){
					throw new ServiceException(errorMsg+"Found expression that contains a different number of select field ("+
												(i+1)+"-exp:"+aliasUe.size()+" "+(j+1)+"-exp:"+checkAliasUe.size()+")");
				}
				
				int index = 0;
				for (String checkAlias : checkAliasUe) {
					int index_interno = 0;
					for (String ueAlias : aliasUe) {
						if(index==index_interno){
							if(checkAlias.equals(ueAlias)==false){
								throw new ServiceException(errorMsg+"Found expression that contains a different alias for select field n."+index+" ("+
										(i+1)+"-exp alias:"+ueAlias+" "+(j+1)+"-exp alias:"+checkAlias+")");
							}
						}
						index_interno++;
					}
					index++;
				}
				
//				index = 0;
//				for (Object checkObject : checkObjectUe) {
//					int index_interno = 0;
//					for (Object ueObject : objectUe) {
//						if(index==index_interno){
							
							// TODO Eventuale check sui tipi, pero' non dovrebbe servire, e' il SQL stesso che darebbe errore.
//						}
//					}
//				}
				
			}
			
			if(i==0){
				// Uso i tipi della prima espressione. Dovrebbero essere tutti compatibili tra loro
				List<Object> listaObject = new ArrayList<Object>();
				for (String alias : aliasUe) {
					listaObject.add(ue.getReturnField(alias));
				}
				listClassReturnType = readClassTypes(listaObject);
			}
		}
		return listClassReturnType;
	}
	
	public static void checkUnionExpression(Union union,UnionExpression expressionComparator,ISQLQueryObject sqlQueryObject) throws ServiceException, SQLQueryObjectException{
		
		// Check select field
		List<String> listaFields = union.getFields();
		if(listaFields!=null && listaFields.size()>0){
			
			for (String alias : listaFields) {
				
				Function function = union.getFunction(alias);
				String paramAliasFunction = union.getParamAliasFunction(alias);
				String aliasCheck = null;
				
				if(paramAliasFunction==null){
					// selezionato direttamente una colonna risultato delle espressioni interne
					// quando arrivo in questo metodo mi viene garantito dal metodo checkUnionExpression(expression ... )
					// che ogni espressione interna possiede lo stesso alias
					
					aliasCheck = alias;
				}
				else{
					aliasCheck = paramAliasFunction;
				}
				
				if(expressionComparator.getReturnFieldAliases()==null || !expressionComparator.getReturnFieldAliases().contains(aliasCheck)){
					throw new ServiceException("The alias ["+aliasCheck+"] is unknown. Check the alias used in the internal union expression");
				}
				
				if(paramAliasFunction==null){
					sqlQueryObject.addSelectField(alias);
				}else{
					ExpressionSQL.setFunction(function, false, paramAliasFunction, alias, sqlQueryObject);
				}
			}

		}
		
		// Check eventuali group by siano alias che corrispondano ad alias reali
		if(union.getGroupByList()!=null && union.getGroupByList().size()>0){
			List<String> aliasExpression = expressionComparator.getReturnFieldAliases();
			for (String aliasGroupBy : union.getGroupByList()) {
				if(aliasExpression.contains(aliasGroupBy)==false){
					throw new ServiceException("The alias used in the condition of group by the union must be one of the aliases used in internal expressions");
				}
				sqlQueryObject.addGroupBy(aliasGroupBy);
			}
		}
		
		// Check eventuali order by siano alias che corrispondano ad alias reali
		if(union.getOrderByList()!=null && union.getOrderByList().size()>0){
			SortOrder sortOrderUnion = null;
			try{
				sortOrderUnion = union.getSortOrder();
			}catch(Exception e){
				throw new ServiceException(e.getMessage(),e);
			}
			if(!SortOrder.UNSORTED.equals(sortOrderUnion)){
				List<String> aliasExpression = expressionComparator.getReturnFieldAliases();
				List<String> aliasExternalExpression = union.getFields();
				for (UnionOrderedColumn uoo : union.getOrderByList()) {
					String aliasOrderBy = uoo.getAlias();
					SortOrder sortOrder = uoo.getSortOrder();
					if(aliasExpression.contains(aliasOrderBy)==false && aliasExternalExpression.contains(aliasOrderBy)==false){
						throw new ServiceException("The alias used in the condition of order by the union must be one of the aliases used in internal or external expressions");
					}
					if(sortOrder==null){
						sqlQueryObject.addOrderBy(aliasOrderBy);
					}
					else{
						sqlQueryObject.addOrderBy(aliasOrderBy,SortOrder.ASC.equals(sortOrder));
					}
				}
				sqlQueryObject.setSortType(SortOrder.ASC.equals(sortOrderUnion));
			}
		}
		
		// Check Limit/Offset
		if(union.getLimit()!=null){
			sqlQueryObject.setLimit(union.getLimit());
		}
		if(union.getOffset()!=null){
			sqlQueryObject.setOffset(union.getOffset());
		}
		
	}
	
	protected static void setFields(List<ISQLQueryObject> sqlQueryObjectInnerList,UnionExpression ... unionExpression) throws ExpressionException, ServiceException{
		manageFields(sqlQueryObjectInnerList, true, false, unionExpression);
	}
	protected static void removeFields(List<ISQLQueryObject> sqlQueryObjectInnerList,UnionExpression ... unionExpression) throws ExpressionException, ServiceException{
		manageFields(sqlQueryObjectInnerList, false, true, unionExpression);
	}
	private static void manageFields(List<ISQLQueryObject> sqlQueryObjectInnerList,boolean set,boolean remove,UnionExpression ... unionExpression) throws ExpressionException, ServiceException{
		if(unionExpression==null || unionExpression.length<=0){
			throw new ServiceException("UnionExpression not found");
		}
		if(unionExpression.length==1){
			throw new ServiceException("At least two expressions are required");
		}
		
		for (int i = 0; i < unionExpression.length; i++) {
			
			ISQLQueryObject sqlQueryObject = sqlQueryObjectInnerList.get(i);
			
			UnionExpression ue = unionExpression[i];
			IExpression exp = ue.getExpression();
			List<IField> listField = new ArrayList<IField>();
			List<FunctionField> listFunctionField = new ArrayList<FunctionField>();		
			List<String> aliasUe = ue.getReturnFieldAliases();
			for (String alias : aliasUe) {
				Object o = ue.getReturnField(alias);
				if(o instanceof IField){
					IField field = (IField) o;
					listField.add(field);
				}
				else if(o instanceof FunctionField){
					FunctionField field = (FunctionField) o;
					listFunctionField.add(field);		
				}
			}
			
			IField[] fields = null;
			if(listField.size()>0){
				fields = listField.toArray(new IField[1]);
			}
			if(set){
				if(exp instanceof JDBCExpression){
					setFields(sqlQueryObject, ((JDBCExpression)exp), aliasUe, fields);
				}else{
					setFields(sqlQueryObject, ((JDBCPaginatedExpression)exp), aliasUe, fields);
				}
			}
			else{
				if(exp instanceof JDBCExpression){
					removeFields(sqlQueryObject, ((JDBCExpression)exp), fields);
				}else{
					removeFields(sqlQueryObject, ((JDBCPaginatedExpression)exp), fields);
				}
			}
			
			FunctionField[] functionFields = null;
			if(listFunctionField.size()>0){
				functionFields = listFunctionField.toArray(new FunctionField[1]);
			}
			if(set){
				if(exp instanceof JDBCExpression){
					setFields(sqlQueryObject, ((JDBCExpression)exp), functionFields);
				}else{
					setFields(sqlQueryObject, ((JDBCPaginatedExpression)exp), functionFields);
				}
			}
			else{
				if(exp instanceof JDBCExpression){
					removeFields(sqlQueryObject, ((JDBCExpression)exp), functionFields);
				}else{
					removeFields(sqlQueryObject, ((JDBCPaginatedExpression)exp), functionFields);
				}
			}
		}
	}
	
	public static List<Class<?>> prepareUnion(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject,
			ISQLFieldConverter sqlConverter,IModel<?> model,
			List<ISQLQueryObject> sqlQueryObjectInnerList,List<JDBCObject> jdbcObjects,
			Union union,UnionExpression ... unionExpression) throws NotFoundException, ExpressionException, ExpressionNotImplementedException, SQLQueryObjectException, JDBCAdapterException, ServiceException{
			
		// check expression
		List<Class<?>> returnClassTypes = checkUnionExpression(unionExpression);
	
		// check union external expression
		// quando arrivo in questo metodo mi viene garantito dal metodo checkUnionExpression(expression ... )
		// che ogni espressione interna possiede lo stesso alias e che cmq esistano almeno 2 espressioni
		checkUnionExpression(union,unionExpression[0],sqlQueryObject);
		
		// set fields in expression
		for (int i = 0; i < unionExpression.length; i++) {
			ISQLQueryObject sqlQueryObjectInner = sqlQueryObject.newSQLQueryObject();
			sqlQueryObjectInner.setANDLogicOperator(true);
			sqlQueryObjectInnerList.add(sqlQueryObjectInner);
		}
		setFields(sqlQueryObjectInnerList, unionExpression);
		// non serve il remove,  SONO SQL FIELD CREATI INTERNAMENTE
		
		// Create sql query object
		for (int i = 0; i < unionExpression.length; i++) {

			IExpression expr = unionExpression[i].getExpression();
			
			ISQLQueryObject sqlQueryObjectInner = sqlQueryObjectInnerList.get(i);
			
			List<Object> listaQuery = new ArrayList<Object>();
			toSqlForPreparedStatementWithFromCondition(expr,sqlQueryObjectInner,listaQuery,sqlConverter.toTable(model));
			for (Object param : listaQuery) {
				jdbcObjects.add(new JDBCObject(param, param.getClass()));
			}
			
		}
		
		return returnClassTypes;
	}
		
	public static List<Map<String,Object>> union(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject,
			ISQLFieldConverter sqlConverter,IModel<?> model,
			List<ISQLQueryObject> sqlQueryObjectInnerList,List<JDBCObject> jdbcObjects, List<Class<?>> returnClassTypes,
			Union union,UnionExpression ... unionExpression) throws NotFoundException, ExpressionException, ExpressionNotImplementedException, SQLQueryObjectException, JDBCAdapterException, ServiceException{
					
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		// Aggiunto un livello intermedio agli inner sql, in modo da essere sicuro di avere gli alias nei select field.
		// !non serve!
//		List<ISQLQueryObject> sqlQueryObjectUnionExpression = new ArrayList<ISQLQueryObject>();
//		for (int i = 0; i < unionExpression.length; i++) {
//			ISQLQueryObject sqlQueryObjectUnion = sqlQueryObject.newSQLQueryObject(); 
//			for (String alias : unionExpression[i].getReturnFieldAliases()) {
//				sqlQueryObjectUnion.addSelectField(alias);
//			}
//			sqlQueryObjectUnion.addFromTable(sqlQueryObjectInnerList.get(i));
//			sqlQueryObjectUnionExpression.add(sqlQueryObjectUnion);
//		}
		
		// Create and execute sql union
		//String sql = sqlQueryObject.createSQLUnion(union.isUnionAll(), sqlQueryObjectUnionExpression.toArray(new ISQLQueryObject[1]));
		String sql = sqlQueryObject.createSQLUnion(union.isUnionAll(), sqlQueryObjectInnerList.toArray(new ISQLQueryObject[1]));
				
		// Per gli alias uso quelli della prima espressione, tanto nel check ho verificato che siano identici a meno di non averli ridefiniti nella union esterna.
		List<String> returnClassAliases = unionExpression[0].getReturnFieldAliases();
		if(union.getFields()!=null && union.getFields().size()>0){
			returnClassAliases = union.getFields();
		}
		
		// Eseguo la query
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		
		JDBCObject [] params = null;
		if(jdbcObjects.size()>0)
			params = jdbcObjects.toArray(new JDBCObject[1]);
		List<List<Object>> resultExecuteQuery = jdbcUtilities.executeQuery(sql, jdbcProperties.isShowSql(), returnClassTypes ,params);
		if(resultExecuteQuery.size()>0){			
			for (int i = 0; i < resultExecuteQuery.size(); i++) {
				List<Object> lista = resultExecuteQuery.get(i);
				if(lista==null){
					throw new ServiceException("Result["+i+"] is null?");
				}
				if(lista.size()!=returnClassTypes.size()){
					throw new ServiceException("Result["+i+"] has wrong length (expected:"+returnClassTypes.size()+" founded:"+lista.size()+")");
				}
				// controllo seguente non dovrebbe servire... 
				if(lista.size()!=returnClassAliases.size()){
					throw new ServiceException("Result["+i+"] has wrong length (alias) (expected:"+returnClassAliases.size()+" founded:"+lista.size()+")");
				}
				Hashtable<String,Object> resultList = new Hashtable<String, Object>();
				int j = 0;
				for (String alias : returnClassAliases) {
					Object object = lista.get(j);
					if(object!=null)
						resultList.put(alias, object);
					else
						resultList.put(alias, org.apache.commons.lang.ObjectUtils.NULL);
					j++;
				}
				result.add(resultList);
			}		
		}
		
		if(result.size()<=0){
			throw new NotFoundException("No result founds");
		}
		
		return result;
			
	}
	
	
	/* *** UNION COUNT *** */
	
	public static List<Class<?>> prepareUnionCount(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject,
			ISQLFieldConverter sqlConverter,IModel<?> model,
			List<ISQLQueryObject> sqlQueryObjectInnerList,List<JDBCObject> jdbcObjects,
			Union union,UnionExpression ... unionExpression) throws NotFoundException, ExpressionException, ExpressionNotImplementedException, SQLQueryObjectException, JDBCAdapterException, ServiceException{
	
		if(union.getOrderByList().size()>0 && union.getSortOrder()!=null && !union.getSortOrder().equals(SortOrder.UNSORTED)){
			throw new ServiceException("OrderBy conditions not allowed in count expression");
		}
		if(union.getOffset()!=null){
			throw new ServiceException("Offset condition not allowed in count expression");
		}
		if(union.getLimit()!=null){
			throw new ServiceException("Limit condition not allowed in count expression");
		}
		
		return prepareUnion(jdbcProperties, log, connection, sqlQueryObject, sqlConverter, model, sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
	}
	
	public static NonNegativeNumber unionCount(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject,
			ISQLFieldConverter sqlConverter,IModel<?> model,
			List<ISQLQueryObject> sqlQueryObjectInnerList,List<JDBCObject> jdbcObjects, List<Class<?>> returnClassTypes,
			Union union,UnionExpression ... unionExpression) throws NotFoundException, ExpressionException, ExpressionNotImplementedException, SQLQueryObjectException, JDBCAdapterException, ServiceException{
					
		long totale = 0;
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		// Create and execute sql union
		String sql = sqlQueryObject .createSQLUnionCount(union.isUnionAll(), "unionCount" , sqlQueryObjectInnerList.toArray(new ISQLQueryObject[1]));
		
		JDBCObject [] params = null;
		if(jdbcObjects.size()>0)
			params = jdbcObjects.toArray(new JDBCObject[1]);
		
		totale = jdbcUtilities.count(sql, jdbcProperties.isShowSql(), params);

		return new NonNegativeNumber(totale);
			
	}
	
	
	/* *** FIND ALL *** */
	
	public static List<Object> prepareFindAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject,
			IExpression paginatedExpression,ISQLFieldConverter sqlConverter,IModel<?> model) throws SQLQueryObjectException, JDBCAdapterException, ExpressionException, ExpressionNotImplementedException, ServiceException{
	
		List<Object> listaQuery = new ArrayList<Object>();
		
		toSqlForPreparedStatementWithFromCondition(paginatedExpression,sqlQueryObject,listaQuery,
				sqlConverter.toTable(model));
		
		return listaQuery;
	}
	
	public static List<Object> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject,
			IExpression paginatedExpression,ISQLFieldConverter sqlConverter,IModel<?> model,Class<?> objectIdClass,List<Object> listaQuery) throws SQLQueryObjectException, JDBCAdapterException, ExpressionException, ExpressionNotImplementedException, ServiceException{
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		JDBCObject[] params = new JDBCObject[listaQuery.size()];
		int index = 0;
		for (Object param : listaQuery) {
			params[index++] = new JDBCObject(param, param.getClass());
		}
		
		String sql = sqlQueryObject.createSQLQuery();

		return jdbcUtilities.executeQuery(sql, jdbcProperties.isShowSql(), objectIdClass, params);
	}
	
	public static List<List<Object>> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject,
			IExpression paginatedExpression,ISQLFieldConverter sqlConverter,IModel<?> model,List<Class<?>> objectIdsClass,List<Object> listaQuery) throws SQLQueryObjectException, JDBCAdapterException, ExpressionException, ExpressionNotImplementedException, ServiceException{
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		JDBCObject[] params = new JDBCObject[listaQuery.size()];
		int index = 0;
		for (Object param : listaQuery) {
			params[index++] = new JDBCObject(param, param.getClass());
		}
		
		String sql = sqlQueryObject.createSQLQuery();

		return jdbcUtilities.executeQuery(sql, jdbcProperties.isShowSql(), objectIdsClass, params);
	}
	
	
	/* *** FIND *** */
	
	public static List<Object> prepareFind(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject,
			IExpression expression,ISQLFieldConverter sqlConverter,IModel<?> model) throws SQLQueryObjectException, JDBCAdapterException, ExpressionException, ExpressionNotImplementedException, ServiceException, MultipleResultException{
	
		List<Object> listaQuery = new ArrayList<Object>();
		
		toSqlForPreparedStatementWithFromCondition(expression,sqlQueryObject,listaQuery,
				sqlConverter.toTable(model));
		
		return listaQuery;
	}
	
	public static Object find(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject,
			IExpression expression,ISQLFieldConverter sqlConverter,IModel<?> model,Class<?> objectIdClass,List<Object> listaQuery) throws SQLQueryObjectException, JDBCAdapterException, ExpressionException, ExpressionNotImplementedException, NotFoundException, ServiceException, MultipleResultException{
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		JDBCObject[] params = new JDBCObject[listaQuery.size()];
		int index = 0;
		for (Object param : listaQuery) {
			params[index++] = new JDBCObject(param, param.getClass());
		}
		
		String sql = sqlQueryObject.createSQLQuery();

		return jdbcUtilities.executeQuerySingleResult(sql, jdbcProperties.isShowSql(), objectIdClass, params);
	}
	
	public static List<Object> find(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject,
			IExpression expression,ISQLFieldConverter sqlConverter,IModel<?> model,List<Class<?>> objectIdsClass,List<Object> listaQuery) throws SQLQueryObjectException, JDBCAdapterException, ExpressionException, ExpressionNotImplementedException, NotFoundException, ServiceException, MultipleResultException{
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		JDBCObject[] params = new JDBCObject[listaQuery.size()];
		int index = 0;
		for (Object param : listaQuery) {
			params[index++] = new JDBCObject(param, param.getClass());
		}
		
		String sql = sqlQueryObject.createSQLQuery();

		return jdbcUtilities.executeQuerySingleResult(sql, jdbcProperties.isShowSql(), objectIdsClass, params);
	}
	
	
	
	/* *** UPDATE  ** */

	public static void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject,
			String rootTable, Map<String, List<IField>> mapTableToPKColumn, List<Object> rootTableIdValues,
			ISQLFieldConverter sqlConverter,
			IJDBCServiceSearchSingleObject<?, ?> serviceSingleObject,
			UpdateModel ... updateModels) throws ServiceException, NotImplementedException, Exception{
		updateFields(jdbcProperties, log, connection, sqlQueryObject, rootTable, mapTableToPKColumn, rootTableIdValues, sqlConverter, 
				serviceSingleObject, null, null, updateModels);
	}
	public static void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject,
			String rootTable, Map<String, List<IField>> mapTableToPKColumn, List<Object> rootTableIdValues,
			ISQLFieldConverter sqlConverter,
			IJDBCServiceSearchWithId<?, ?, ?> serviceWithId,
			UpdateModel ... updateModels) throws ServiceException, NotImplementedException, Exception{
		updateFields(jdbcProperties, log, connection, sqlQueryObject, rootTable, mapTableToPKColumn, rootTableIdValues, sqlConverter, 
				null, serviceWithId, null, updateModels);
	}
	public static void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject,
			String rootTable, Map<String, List<IField>> mapTableToPKColumn, List<Object> rootTableIdValues,
			ISQLFieldConverter sqlConverter,
			IJDBCServiceSearchWithoutId<?, ?> serviceWithoutId,
			UpdateModel ... updateModels) throws ServiceException, NotImplementedException, Exception{
		updateFields(jdbcProperties, log, connection, sqlQueryObject, rootTable, mapTableToPKColumn, rootTableIdValues, sqlConverter, 
				null, null, serviceWithoutId, updateModels);
	}
	
	private static void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject,
			String rootTable, Map<String, List<IField>> mapTableToPKColumn, List<Object> rootTableIdValues,
			ISQLFieldConverter sqlConverter,
			IJDBCServiceSearchSingleObject<?, ?> serviceSingleObject,
			IJDBCServiceSearchWithId<?, ?, ?> serviceWithId,
			IJDBCServiceSearchWithoutId<?, ?> serviceWithoutId,
			UpdateModel ... updateModels) throws ServiceException, NotImplementedException, Exception{
		
		if(updateModels==null || updateModels.length<=0){
			throw new ServiceException("Parameter updateModels is not defined");
		}
		List<String> tableUpdated = new ArrayList<String>();
		for (UpdateModel updateModel : updateModels) {
			
			IModel<?> model = updateModel.getModel();
			IExpression condition = updateModel.getCondition();
			List<UpdateField> updateFields = updateModel.getUpdateFiels();
			if(updateFields==null || updateFields.size()<=0){
				throw new ServiceException("Parameter updateFields of model ["+model+"] is not defined");
			}
			String table = sqlConverter.toTable(model);
			if(table==null){
				throw new ServiceException("Model ["+model+"] is not defined in sql converter");
			}
			for (UpdateField updateField : updateFields) {
				String tableField = sqlConverter.toTable(updateField.getField());
				if(table.equals(tableField)==false){
					throw new ServiceException("Different update table: UpdateField ["+updateField.getField().getFieldName()+" of "+updateField.getField().getClassName()+"] (table:"+tableField+") and Model ["+model+"] (table:"+table+") ");
				}
			}
			if(tableUpdated.contains(table)==false){
				
				_engineUpdateFields(jdbcProperties, log, connection, sqlQueryObject, sqlConverter, serviceSingleObject, 
						serviceWithId, serviceWithoutId, rootTable, mapTableToPKColumn, rootTableIdValues, table, condition, updateFields);
				tableUpdated.add(table);
			}
					
		}
		
	}
	
	public static void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject,
			String rootTable, Map<String, List<IField>> mapTableToPKColumn, List<Object> rootTableIdValues,
			ISQLFieldConverter sqlConverter,
			IJDBCServiceSearchSingleObject<?, ?> serviceSingleObject,
			IExpression condition, UpdateField ... updateFields) throws ServiceException, NotImplementedException, Exception{
		updateFields(jdbcProperties, log, connection, sqlQueryObject, rootTable, mapTableToPKColumn, rootTableIdValues, sqlConverter, 
				serviceSingleObject, null, null, condition, updateFields);
	}
	public static void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject,
			String rootTable, Map<String, List<IField>> mapTableToPKColumn, List<Object> rootTableIdValues,
			ISQLFieldConverter sqlConverter,
			IJDBCServiceSearchWithId<?, ?, ?> serviceWithId,
			IExpression condition, UpdateField ... updateFields) throws ServiceException, NotImplementedException, Exception{
		updateFields(jdbcProperties, log, connection, sqlQueryObject, rootTable, mapTableToPKColumn, rootTableIdValues, sqlConverter, 
				null, serviceWithId, null, condition, updateFields);
	}
	public static void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject,
			String rootTable, Map<String, List<IField>> mapTableToPKColumn, List<Object> rootTableIdValues,
			ISQLFieldConverter sqlConverter,
			IJDBCServiceSearchWithoutId<?, ?> serviceWithoutId,
			IExpression condition, UpdateField ... updateFields) throws ServiceException, NotImplementedException, Exception{
		updateFields(jdbcProperties, log, connection, sqlQueryObject, rootTable, mapTableToPKColumn, rootTableIdValues, sqlConverter, 
				null, null, serviceWithoutId, condition, updateFields);
	}
	private static void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject,
			String rootTable, Map<String, List<IField>> mapTableToPKColumn, List<Object> rootTableIdValues,
			ISQLFieldConverter sqlConverter,
			IJDBCServiceSearchSingleObject<?, ?> serviceSingleObject,
			IJDBCServiceSearchWithId<?, ?, ?> serviceWithId,
			IJDBCServiceSearchWithoutId<?, ?> serviceWithoutId,
			IExpression condition, UpdateField ... updateFields) throws ServiceException, NotImplementedException, Exception{
		
		if(updateFields==null || updateFields.length<=0){
			throw new ServiceException("Parameter updateFields is not defined");
		}
		
		// *** Suddivisione per tabella ***
		
		Hashtable<String, List<UpdateField>> updateMap = new Hashtable<String, List<UpdateField>>();
		for (int i = 0; i < updateFields.length; i++) {
			String tableName = sqlConverter.toTable(updateFields[i].getField());
			if(tableName==null){
				throw new ServiceException("Field ["+updateFields[i].getField().getFieldName()+" of "+updateFields[i].getField().getClassName()+"] is not defined in sql converter");
			}
			List<UpdateField> list = updateMap.remove(tableName);
			if(list==null){
				list = new ArrayList<UpdateField>();
			}
			list.add(updateFields[i]);
			updateMap.put(tableName, list);
		}
		
		Enumeration<String> tables = updateMap.keys();
		while (tables.hasMoreElements()) {
			String table = (String) tables.nextElement();
			
			_engineUpdateFields(jdbcProperties, log, connection, sqlQueryObject, sqlConverter, serviceSingleObject, 
					serviceWithId, serviceWithoutId, rootTable, mapTableToPKColumn, rootTableIdValues, table, condition, updateMap.get(table));
		}
	}
				
	private static void _engineUpdateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject,
			ISQLFieldConverter sqlConverter,
			IJDBCServiceSearchSingleObject<?, ?> serviceSingleObject,
			IJDBCServiceSearchWithId<?, ?, ?> serviceWithId,
			IJDBCServiceSearchWithoutId<?, ?> serviceWithoutId,
			String rootTable, Map<String, List<IField>> mapTableToPKColumn, List<Object> rootTableIdValues,
			String table, IExpression condition, List<UpdateField> updateFields) throws ServiceException, NotImplementedException, Exception{		
			
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		// Una tabella può non avere una PK
//		if(mapTableToPKColumn.containsKey(table)==false){
//			throw new ServiceException("PrimaryKey columns not defined for table ["+table+"]");
//		}
		
		
		// *** Update per tabella ***
					
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObject.newSQLQueryObject();
		sqlQueryObjectUpdate.setANDLogicOperator(true);
		sqlQueryObjectUpdate.addUpdateTable(table);
		
		// update fields
		java.util.List<JDBCObject> lstObjectsUpdate = new java.util.ArrayList<JDBCObject>();
		for (UpdateField updateField : updateFields) {
            sqlQueryObjectUpdate.addUpdateField(sqlConverter.toColumn(updateField.getField(),false), "?");
            lstObjectsUpdate.add(new JDBCObject(updateField.getValue(), updateField.getField().getFieldType()));
		}
		
		boolean update = true;
		
		// rootTable pk fields
		JDBCPaginatedExpression rootTablePKExpresion = new JDBCPaginatedExpression(sqlConverter);
		rootTablePKExpresion.and();
		List<IField> rootTableListPKColumns = mapTableToPKColumn.get(rootTable);
		if(rootTableListPKColumns!=null && rootTableIdValues!=null){
			if(rootTableListPKColumns.size()!=rootTableIdValues.size()){
				throw new ServiceException("Number of primary key column ("+rootTableListPKColumns.size()+") and numbero of column values ("+
						rootTableIdValues.size()+") isn't equals");
			}
			for (int i = 0; i < rootTableListPKColumns.size(); i++) {
				rootTablePKExpresion.equals(rootTableListPKColumns.get(i), rootTableIdValues.get(i));
			}
		}
		// where condition custom
		if(condition!=null){
			rootTablePKExpresion.and(condition);
		}
		// for generate join condition
		rootTablePKExpresion.sortOrder(SortOrder.ASC).addOrder(updateFields.get(0).getField()); 
		
		// column ids
		if(rootTable.equals(table) && condition==null){
			
			// Se sto aggiornando la root table, e non vi sono condizioni dinamiche (le quali possono riferire anche tabelle interne)
			// non serve effettuare una ulteriore query che identifica gli id, ma posso applicare direttamente il where delle root columns.
			if(rootTableListPKColumns!=null && rootTableIdValues!=null){
				StringBuffer bfRowIdentification = new StringBuffer();
				bfRowIdentification.append("( ");
				for (int i = 0; i < rootTableListPKColumns.size(); i++) {
					if(i>0){
						bfRowIdentification.append(" AND ");
					}
					IField columnId = rootTableListPKColumns.get(i);
					bfRowIdentification.append(sqlConverter.toColumn(columnId, true)).append("=?");
					lstObjectsUpdate.add(new JDBCObject(rootTableIdValues.get(i), columnId.getFieldType()));
				}
				bfRowIdentification.append(" )");
				sqlQueryObjectUpdate.addWhereCondition(bfRowIdentification.toString());
			}
		}
		else{
			ISQLQueryObject sqlQueryObjectSelect = sqlQueryObjectUpdate.newSQLQueryObject();
			sqlQueryObjectSelect.setANDLogicOperator(true);
			try{
				List<IField> columnIds = mapTableToPKColumn.get(table);
				if(columnIds!=null && columnIds.size()>0){
					List<Map<String, Object>> valueIds = null;
					if(serviceSingleObject!=null){
						valueIds = serviceSingleObject.select(jdbcProperties, log, connection, sqlQueryObjectSelect, rootTablePKExpresion, 
								true, columnIds.toArray(new IField[]{}));
					}
					else if(serviceWithoutId!=null){
						valueIds = serviceWithoutId.select(jdbcProperties, log, connection, sqlQueryObjectSelect, rootTablePKExpresion, 
								true, columnIds.toArray(new IField[]{}));
					}
					else if(serviceWithId!=null){
						valueIds = serviceWithId.select(jdbcProperties, log, connection, sqlQueryObjectSelect, rootTablePKExpresion, 
								true, columnIds.toArray(new IField[]{}));
					}
					// Questo controllo e' sbagliato. Devo poter aggiornare dei singoli campi su piu' righe di oggetti interni. 
					// Se voglio identificare esattamente una sola riga di un oggetto interno devo usare la condition
					// Mentre l'oggetto "daoInterface" lo identifico esattamente (o grazie all'id, o grazie all'oggetto stesso e quindi l'id long, o grazie alla singola istanza) 
	//				if(valueIds.size()>1){
	//					throw new ServiceException("Cannot exists more columns with PK column ids (table:"+table+"), found: "+valueIds.size());
	//				}
					StringBuffer bfRowIdentification = new StringBuffer();
					bfRowIdentification.append("( ");
					for (int i = 0; i < valueIds.size(); i++) {
						if(i>0){
							bfRowIdentification.append(" OR ");
						}
						Map<String, Object> mapColumnValue = valueIds.get(i);
						bfRowIdentification.append("( ");
						for (int j = 0; j < columnIds.size(); j++) {
							if(j>0){
								bfRowIdentification.append(" AND ");
							}
							IField columnId = columnIds.get(j);
							bfRowIdentification.append(sqlConverter.toColumn(columnId, true)).append("=?");
							lstObjectsUpdate.add(new JDBCObject(mapColumnValue.get(columnId.getFieldName()), columnId.getFieldType()));
						}	
						bfRowIdentification.append(" )");
					}
					bfRowIdentification.append(" )");
					sqlQueryObjectUpdate.addWhereCondition(bfRowIdentification.toString());
				}
			}catch(NotFoundException notFound){
				update = false;
				log.debug("UpdateField["+table+"]: NotFound");
			}
		}
			
		if(update){
			int updateRow = jdbcUtilities.executeUpdate(sqlQueryObjectUpdate.createSQLUpdate(), jdbcProperties.isShowSql(), 
					lstObjectsUpdate.toArray(new JDBCObject[]{}));
			log.debug("UpdateField["+table+"]: "+updateRow +" rows");
		}
				
	}
	

	
	/* *** NATIVE ** */
	
	public static List<List<Object>> nativeQuery(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
			String sql,List<Class<?>> returnClassTypes,Object ... param) throws SQLQueryObjectException, JDBCAdapterException, ServiceException{
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		java.util.List<org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject> listParams = 
			new java.util.ArrayList<org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject>();
		for (Object jdbcObject : param) {
			org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject jdbc =
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(jdbcObject,jdbcObject.getClass());
			listParams.add(jdbc);
		}
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] paramArray = null;
		if(listParams.size()>0){
			paramArray = listParams.toArray(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [1]);
		}
		
		return jdbcUtilities.executeQuery(sql, jdbcProperties.isShowSql(), returnClassTypes ,paramArray);
	}
	
}
