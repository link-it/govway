/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.core.statistiche.dao.jdbc;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import java.sql.Connection;

import org.slf4j.Logger;

import org.openspcoop2.utils.sql.ISQLQueryObject;

import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.generic_project.dao.jdbc.utils.IJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject;
import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceSearchWithoutId;
import org.openspcoop2.generic_project.utils.UtilsTemplate;
import org.openspcoop2.generic_project.beans.CustomField;
import org.openspcoop2.generic_project.beans.InUse;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.beans.UnionExpression;
import org.openspcoop2.generic_project.beans.Union;
import org.openspcoop2.generic_project.beans.FunctionField;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCPaginatedExpression;

import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerProperties;
import org.openspcoop2.core.statistiche.dao.jdbc.converter.StatisticaSettimanaleFieldConverter;
import org.openspcoop2.core.statistiche.dao.jdbc.fetch.StatisticaSettimanaleFetch;
import org.openspcoop2.core.statistiche.dao.jdbc.JDBCServiceManager;

import org.openspcoop2.core.statistiche.StatisticaSettimanale;
import org.openspcoop2.core.statistiche.Statistica;
import org.openspcoop2.core.statistiche.StatisticaContenuti;

/**     
 * JDBCStatisticaSettimanaleServiceSearchImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCStatisticaSettimanaleServiceSearchImpl implements IJDBCServiceSearchWithoutId<StatisticaSettimanale, JDBCServiceManager> {

	private StatisticaSettimanaleFieldConverter _statisticaSettimanaleFieldConverter = null;
	public StatisticaSettimanaleFieldConverter getStatisticaSettimanaleFieldConverter() {
		if(this._statisticaSettimanaleFieldConverter==null){
			this._statisticaSettimanaleFieldConverter = new StatisticaSettimanaleFieldConverter(this.jdbcServiceManager.getJdbcProperties().getDatabaseType());
		}		
		return this._statisticaSettimanaleFieldConverter;
	}
	@Override
	public ISQLFieldConverter getFieldConverter() {
		return this.getStatisticaSettimanaleFieldConverter();
	}
	
	private StatisticaSettimanaleFetch statisticaSettimanaleFetch = new StatisticaSettimanaleFetch();
	public StatisticaSettimanaleFetch getStatisticaSettimanaleFetch() {
		return this.statisticaSettimanaleFetch;
	}
	@Override
	public IJDBCFetch getFetch() {
		return getStatisticaSettimanaleFetch();
	}
	
	
	private JDBCServiceManager jdbcServiceManager = null;

	@Override
	public void setServiceManager(JDBCServiceManager serviceManager) throws ServiceException{
		this.jdbcServiceManager = serviceManager;
	}
	
	@Override
	public JDBCServiceManager getServiceManager() throws ServiceException{
		return this.jdbcServiceManager;
	}
	

	
	
	
	
	@Override
	public List<StatisticaSettimanale> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

        List<StatisticaSettimanale> list = new ArrayList<StatisticaSettimanale>();
        
        // TODO: implementazione non efficente. 
		// Per ottenere una implementazione efficente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari
		// 2. Usare metodo getStatisticaSettimanaleFetch() sul risultato della select per ottenere un oggetto StatisticaSettimanale
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	list.add(this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour));
        }

        return list;      
		
	}
	
	@Override
	public StatisticaSettimanale find(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) 
		throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {

        long id = this.findTableId(jdbcProperties, log, connection, sqlQueryObject, expression);
        if(id>0){
        	return this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour);
        }else{
        	throw new NotFoundException("Entry with id["+id+"] not found");
        }
		
	}
	
	@Override
	public NonNegativeNumber count(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws NotImplementedException, ServiceException,Exception {
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareCount(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getStatisticaSettimanaleFieldConverter(), StatisticaSettimanale.model());
		
		sqlQueryObject.addSelectCountField(this.getStatisticaSettimanaleFieldConverter().toTable(StatisticaSettimanale.model())+".id","tot",true);
		
		_join(expression,sqlQueryObject);
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.count(jdbcProperties, log, connection, sqlQueryObject, expression,
																			this.getStatisticaSettimanaleFieldConverter(), StatisticaSettimanale.model(),listaQuery);
	}


	@Override
	public List<Object> select(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCPaginatedExpression paginatedExpression, IField field) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		return this.select(jdbcProperties, log, connection, sqlQueryObject,
								paginatedExpression, false, field);
	}
	
	@Override
	public List<Object> select(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCPaginatedExpression paginatedExpression, boolean distinct, IField field) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		List<Map<String,Object>> map = 
			this.select(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression, distinct, new IField[]{field});
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.selectSingleObject(map);
	}
	
	@Override
	public List<Map<String,Object>> select(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCPaginatedExpression paginatedExpression, IField ... field) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		return this.select(jdbcProperties, log, connection, sqlQueryObject,
								paginatedExpression, false, field);
	}
	
	@Override
	public List<Map<String,Object>> select(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCPaginatedExpression paginatedExpression, boolean distinct, IField ... field) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.setFields(sqlQueryObject,paginatedExpression,field);
		try{
		
			ISQLQueryObject sqlQueryObjectDistinct = 
						org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareSqlQueryObjectForSelectDistinct(distinct,sqlQueryObject, paginatedExpression, log,
												this.getStatisticaSettimanaleFieldConverter(), field);

			return _select(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression, sqlQueryObjectDistinct);
			
		}finally{
			org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.removeFields(sqlQueryObject,paginatedExpression,field);
		}
	}

	@Override
	public Object aggregate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCExpression expression, FunctionField functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		Map<String,Object> map = 
			this.aggregate(jdbcProperties, log, connection, sqlQueryObject, expression, new FunctionField[]{functionField});
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.selectAggregateObject(map,functionField);
	}
	
	@Override
	public Map<String,Object> aggregate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCExpression expression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception {													
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.setFields(sqlQueryObject,expression,functionField);
		try{
			List<Map<String,Object>> list = _select(jdbcProperties, log, connection, sqlQueryObject, expression);
			return list.get(0);
		}finally{
			org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.removeFields(sqlQueryObject,expression,functionField);
		}
	}

	@Override
	public List<Map<String,Object>> groupBy(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCExpression expression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		
		if(expression.getGroupByFields().size()<=0){
			throw new ServiceException("GroupBy conditions not found in expression");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.setFields(sqlQueryObject,expression,functionField);
		try{
			return _select(jdbcProperties, log, connection, sqlQueryObject, expression);
		}finally{
			org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.removeFields(sqlQueryObject,expression,functionField);
		}
	}
	

	@Override
	public List<Map<String,Object>> groupBy(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCPaginatedExpression paginatedExpression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		
		if(paginatedExpression.getGroupByFields().size()<=0){
			throw new ServiceException("GroupBy conditions not found in expression");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.setFields(sqlQueryObject,paginatedExpression,functionField);
		try{
			return _select(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression);
		}finally{
			org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.removeFields(sqlQueryObject,paginatedExpression,functionField);
		}
	}
	
	protected List<Map<String,Object>> _select(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
												IExpression expression) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		return _select(jdbcProperties, log, connection, sqlQueryObject, expression, null);
	}
	protected List<Map<String,Object>> _select(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
												IExpression expression, ISQLQueryObject sqlQueryObjectDistinct) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		
		List<Object> listaQuery = new ArrayList<Object>();
		List<JDBCObject> listaParams = new ArrayList<JDBCObject>();
		List<Object> returnField = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareSelect(jdbcProperties, log, connection, sqlQueryObject, 
        						expression, this.getStatisticaSettimanaleFieldConverter(), StatisticaSettimanale.model(), 
        						listaQuery,listaParams);
		
		_join(expression,sqlQueryObject);
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.select(jdbcProperties, log, connection,
        								org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareSqlQueryObjectForSelectDistinct(sqlQueryObject,sqlQueryObjectDistinct), 
        								expression, this.getStatisticaSettimanaleFieldConverter(), StatisticaSettimanale.model(),
        								listaQuery,listaParams,returnField);
		if(list!=null && list.size()>0){
			return list;
		}
		else{
			throw new NotFoundException("Not Found");
		}
	}
	
	@Override
	public List<Map<String,Object>> union(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
												Union union, UnionExpression ... unionExpression) throws ServiceException,NotFoundException,NotImplementedException,Exception {		
		
		List<ISQLQueryObject> sqlQueryObjectInnerList = new ArrayList<ISQLQueryObject>();
		List<JDBCObject> jdbcObjects = new ArrayList<JDBCObject>();
		List<Class<?>> returnClassTypes = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareUnion(jdbcProperties, log, connection, sqlQueryObject, 
        						this.getStatisticaSettimanaleFieldConverter(), StatisticaSettimanale.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.union(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getStatisticaSettimanaleFieldConverter(), StatisticaSettimanale.model(), 
        								sqlQueryObjectInnerList, jdbcObjects, returnClassTypes, union, unionExpression);
        if(list!=null && list.size()>0){
			return list;
		}
		else{
			throw new NotFoundException("Not Found");
		}								
	}
	
	@Override
	public NonNegativeNumber unionCount(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
												Union union, UnionExpression ... unionExpression) throws ServiceException,NotFoundException,NotImplementedException,Exception {		
		
		List<ISQLQueryObject> sqlQueryObjectInnerList = new ArrayList<ISQLQueryObject>();
		List<JDBCObject> jdbcObjects = new ArrayList<JDBCObject>();
		List<Class<?>> returnClassTypes = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareUnionCount(jdbcProperties, log, connection, sqlQueryObject, 
        						this.getStatisticaSettimanaleFieldConverter(), StatisticaSettimanale.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        NonNegativeNumber number = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.unionCount(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getStatisticaSettimanaleFieldConverter(), StatisticaSettimanale.model(), 
        								sqlQueryObjectInnerList, jdbcObjects, returnClassTypes, union, unionExpression);
        if(number!=null && number.longValue()>=0){
			return number;
		}
		else{
			throw new NotFoundException("Not Found");
		}
	}



	// -- ConstructorExpression	

	@Override
	public JDBCExpression newExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCExpression(this.getStatisticaSettimanaleFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}


	@Override
	public JDBCPaginatedExpression newPaginatedExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(this.getStatisticaSettimanaleFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}
	
	@Override
	public JDBCExpression toExpression(JDBCPaginatedExpression paginatedExpression, Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCExpression(paginatedExpression);
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}

	@Override
	public JDBCPaginatedExpression toPaginatedExpression(JDBCExpression expression, Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(expression);
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}
	
	
	
	// -- DB

	
	@Override
	public StatisticaSettimanale get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._get(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId), idMappingResolutionBehaviour);
	}
	
	private StatisticaSettimanale _get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
				
		StatisticaSettimanale statisticaSettimanale = new StatisticaSettimanale();
		

		// Object statisticaSettimanale
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addFromTable(this.getStatisticaSettimanaleFieldConverter().toTable(StatisticaSettimanale.model().STATISTICA_BASE));
		sqlQueryObjectGet.addSelectField("id");
		sqlQueryObjectGet.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.DATA,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.ID_PORTA,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.TIPO_PORTA,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.TIPO_MITTENTE,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.MITTENTE,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.TIPO_DESTINATARIO,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.DESTINATARIO,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.TIPO_SERVIZIO,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.SERVIZIO,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.VERSIONE_SERVIZIO,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.AZIONE,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.SERVIZIO_APPLICATIVO,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.ESITO,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.ESITO_CONTESTO,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.NUMERO_TRANSAZIONI,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_INTERNA,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_ESTERNA,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.LATENZA_TOTALE,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.LATENZA_PORTA,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_BASE.LATENZA_SERVIZIO,true));
		sqlQueryObjectGet.addWhereCondition("id=?");

		// Get statisticaSettimanale
		statisticaSettimanale = (StatisticaSettimanale) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(), StatisticaSettimanale.model(), this.getStatisticaSettimanaleFetch(),
			new JDBCObject(tableId,Long.class));



		// Object statisticaSettimanale_statisticaContenuti
		ISQLQueryObject sqlQueryObjectGet_statisticaSettimanaleContenuti = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_statisticaSettimanaleContenuti.setANDLogicOperator(true);
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addFromTable(this.getStatisticaSettimanaleFieldConverter().toTable(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField("id");
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.DATA,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.RISORSA_NOME,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.RISORSA_VALORE,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_1,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_1,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_2,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_2,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_3,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_3,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_4,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_4,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_5,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_5,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_6,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_6,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_7,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_7,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_8,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_8,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_9,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_9,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_NOME_10,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.FILTRO_VALORE_10,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.NUMERO_TRANSAZIONI,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.LATENZA_TOTALE,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.LATENZA_PORTA,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addSelectField(this.getStatisticaSettimanaleFieldConverter().toColumn(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.LATENZA_SERVIZIO,true));
		sqlQueryObjectGet_statisticaSettimanaleContenuti.addWhereCondition("id_stat=?");

		// Get statisticaSettimanale_statisticaContenuti
		java.util.List<Object> statisticaSettimanale_statisticaContenuti_list = (java.util.List<Object>) jdbcUtilities.executeQuery(sqlQueryObjectGet_statisticaSettimanaleContenuti.createSQLQuery(), jdbcProperties.isShowSql(), StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI, this.getStatisticaSettimanaleFetch(),
			new JDBCObject(statisticaSettimanale.getId(),Long.class));

		if(statisticaSettimanale_statisticaContenuti_list != null) {
			for (Object statisticaSettimanale_statisticaContenuti_object: statisticaSettimanale_statisticaContenuti_list) {
				StatisticaContenuti statisticaSettimanale_statisticaContenuti = (StatisticaContenuti) statisticaSettimanale_statisticaContenuti_object;


				statisticaSettimanale.addStatisticaSettimanaleContenuti(statisticaSettimanale_statisticaContenuti);
			}
		}

		
        return statisticaSettimanale;  
	
	} 
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._exists(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	private boolean _exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		boolean existsStatisticaSettimanale = false;

		sqlQueryObject = sqlQueryObject.newSQLQueryObject();
		sqlQueryObject.setANDLogicOperator(true);

		sqlQueryObject.addWhereCondition("id=?");


		// Exists statisticaSettimanale
		existsStatisticaSettimanale = jdbcUtilities.exists(sqlQueryObject.createSQLQuery(), jdbcProperties.isShowSql(),
			new JDBCObject(tableId,Long.class));

		
        return existsStatisticaSettimanale;
	
	}
	
	private void _join(IExpression expression, ISQLQueryObject sqlQueryObject) throws NotImplementedException, ServiceException, Exception{
	
		/* 
		 * TODO: implement code that implement the join condition
		*/
		/*
		if(expression.inUseModel(StatisticaSettimanale.model().XXXX,false)){
			String tableName1 = this.getStatisticaSettimanaleFieldConverter().toAliasTable(StatisticaSettimanale.model());
			String tableName2 = this.getStatisticaSettimanaleFieldConverter().toAliasTable(StatisticaSettimanale.model().XXX);
			sqlQueryObject.addWhereCondition(tableName1+".id="+tableName2+".id_table1");
		}
		*/
		
		/* 
         * TODO: implementa il codice che aggiunge la condizione FROM Table per le condizioni di join di oggetti annidati dal secondo livello in poi 
         *       La addFromTable deve essere aggiunta solo se l'oggetto del livello precedente non viene utilizzato nella espressione 
         *		 altrimenti il metodo sopra 'toSqlForPreparedStatementWithFromCondition' si occupa gia' di aggiungerla
        */
        /*
        if(expression.inUseModel(StatisticaSettimanale.model().LEVEL1.LEVEL2,false)){
			if(expression.inUseModel(StatisticaSettimanale.model().LEVEL1,false)==false){
				sqlQueryObject.addFromTable(this.getStatisticaSettimanaleFieldConverter().toTable(StatisticaSettimanale.model().LEVEL1));
			}
		}
		...
		if(expression.inUseModel(StatisticaSettimanale.model()....LEVELN.LEVELN+1,false)){
			if(expression.inUseModel(StatisticaSettimanale.model().LEVELN,false)==false){
				sqlQueryObject.addFromTable(this.getStatisticaSettimanaleFieldConverter().toTable(StatisticaSettimanale.model().LEVELN));
			}
		}
		*/
		
		// Delete this line when you have implemented the join condition
		int throwNotImplemented = 1;
		if(throwNotImplemented==1){
		        throw new NotImplementedException("NotImplemented");
		}
		// Delete this line when you have implemented the join condition
        
	}
	
	protected java.util.List<Object> _getRootTablePrimaryKeyValues(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaSettimanale statisticaSettimanale) throws NotFoundException, ServiceException, NotImplementedException, Exception{
	    // Identificativi
        java.util.List<Object> rootTableIdValues = new java.util.ArrayList<Object>();
        // TODO: Define the column values used to identify the primary key
		rootTableIdValues.add(statisticaSettimanale.getId());
        
        // Delete this line when you have verified the method
		int throwNotImplemented = 1;
		if(throwNotImplemented==1){
		        throw new NotImplementedException("NotImplemented");
		}
		// Delete this line when you have verified the method
        
        return rootTableIdValues;
	}
	
	protected Map<String, List<IField>> _getMapTableToPKColumn() throws NotImplementedException, Exception{
	
		StatisticaSettimanaleFieldConverter converter = this.getStatisticaSettimanaleFieldConverter();
		Map<String, List<IField>> mapTableToPKColumn = new java.util.Hashtable<String, List<IField>>();
		UtilsTemplate<IField> utilities = new UtilsTemplate<IField>();

		// TODO: Define the columns used to identify the primary key
		//		  If a table doesn't have a primary key, don't add it to this map

		// StatisticaSettimanale.model()
		mapTableToPKColumn.put(converter.toTable(StatisticaSettimanale.model()),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(StatisticaSettimanale.model()))
			));

		// StatisticaSettimanale.model().STATISTICA_BASE
		mapTableToPKColumn.put(converter.toTable(StatisticaSettimanale.model().STATISTICA_BASE),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(StatisticaSettimanale.model().STATISTICA_BASE))
			));

		// StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI
		mapTableToPKColumn.put(converter.toTable(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI))
			));


        // Delete this line when you have verified the method
		int throwNotImplemented = 1;
		if(throwNotImplemented==1){
		        throw new NotImplementedException("NotImplemented");
		}
		// Delete this line when you have verified the method
        
        return mapTableToPKColumn;		
	}
	
	@Override
	public List<Long> findAllTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression paginatedExpression) throws ServiceException, NotImplementedException, Exception {
		
		List<Long> list = new ArrayList<Long>();

		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getStatisticaSettimanaleFieldConverter().toTable(StatisticaSettimanale.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFindAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
												this.getStatisticaSettimanaleFieldConverter(), StatisticaSettimanale.model());
		
		_join(paginatedExpression,sqlQueryObject);
		
		List<Object> listObjects = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.findAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
																			this.getStatisticaSettimanaleFieldConverter(), StatisticaSettimanale.model(), objectIdClass, listaQuery);
		for(Object object: listObjects) {
			list.add((Long)object);
		}

        return list;
		
	}
	
	@Override
	public long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, Exception {
	
		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getStatisticaSettimanaleFieldConverter().toTable(StatisticaSettimanale.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFind(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getStatisticaSettimanaleFieldConverter(), StatisticaSettimanale.model());
		
		_join(expression,sqlQueryObject);

		Object res = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.find(jdbcProperties, log, connection, sqlQueryObject, expression,
														this.getStatisticaSettimanaleFieldConverter(), StatisticaSettimanale.model(), objectIdClass, listaQuery);
		if(res!=null && (((Long) res).longValue()>0) ){
			return ((Long) res).longValue();
		}
		else{
			throw new NotFoundException("Not Found");
		}
		
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws ServiceException, NotFoundException, NotImplementedException, Exception {
		return this._inUse(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}

	private InUse _inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws ServiceException, NotFoundException, NotImplementedException, Exception {

		InUse inUse = new InUse();
		inUse.setInUse(false);
		
		/* 
		 * TODO: implement code that checks whether the object identified by the id parameter is used by other objects
		*/
		
		// Delete this line when you have implemented the method
		int throwNotImplemented = 1;
		if(throwNotImplemented==1){
		        throw new NotImplementedException("NotImplemented");
		}
		// Delete this line when you have implemented the method

        return inUse;

	}
	

	
	@Override
	public List<List<Object>> nativeQuery(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
											String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException,Exception{
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.nativeQuery(jdbcProperties, log, connection, sqlQueryObject,
																							sql,returnClassTypes,param);
														
	}
	
}
