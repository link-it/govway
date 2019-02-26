/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.statistiche.StatisticaContenuti;
import org.openspcoop2.core.statistiche.StatisticaGiornaliera;
import org.openspcoop2.core.statistiche.dao.jdbc.converter.StatisticaGiornalieraFieldConverter;
import org.openspcoop2.core.statistiche.dao.jdbc.fetch.StatisticaGiornalieraFetch;
import org.openspcoop2.core.statistiche.utils.AliasTableRicerchePersonalizzate;
import org.openspcoop2.generic_project.beans.CustomField;
import org.openspcoop2.generic_project.beans.FunctionField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.InUse;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.beans.Union;
import org.openspcoop2.generic_project.beans.UnionExpression;
import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceSearchWithoutId;
import org.openspcoop2.generic_project.dao.jdbc.JDBCExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCPaginatedExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerProperties;
import org.openspcoop2.generic_project.dao.jdbc.utils.IJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.generic_project.utils.UtilsTemplate;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLQueryObjectCore;
import org.slf4j.Logger;

/**     
 * JDBCStatisticaGiornalieraServiceSearchImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCStatisticaGiornalieraServiceSearchImpl implements IJDBCServiceSearchWithoutId<StatisticaGiornaliera, JDBCServiceManager> {

	private StatisticaGiornalieraFieldConverter _statisticaGiornalieraFieldConverter = null;
	public StatisticaGiornalieraFieldConverter getStatisticaGiornalieraFieldConverter() {
		if(this._statisticaGiornalieraFieldConverter==null){
			this._statisticaGiornalieraFieldConverter = new StatisticaGiornalieraFieldConverter(this.jdbcServiceManager.getJdbcProperties().getDatabaseType());
		}		
		return this._statisticaGiornalieraFieldConverter;
	}
	@Override
	public ISQLFieldConverter getFieldConverter() {
		return this.getStatisticaGiornalieraFieldConverter();
	}
	
	private StatisticaGiornalieraFetch statisticaGiornalieraFetch = new StatisticaGiornalieraFetch();
	public StatisticaGiornalieraFetch getStatisticaGiornalieraFetch() {
		return this.statisticaGiornalieraFetch;
	}
	@Override
	public IJDBCFetch getFetch() {
		return getStatisticaGiornalieraFetch();
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
	public List<StatisticaGiornaliera> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

        List<StatisticaGiornaliera> list = new ArrayList<StatisticaGiornaliera>();
        
        // TODO: implementazione non efficente. 
		// Per ottenere una implementazione efficente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari
		// 2. Usare metodo getStatisticaGiornalieraFetch() sul risultato della select per ottenere un oggetto StatisticaGiornaliera
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	list.add(this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour));
        }

        return list;      
		
	}
	
	@Override
	public StatisticaGiornaliera find(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) 
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
												this.getStatisticaGiornalieraFieldConverter(), StatisticaGiornaliera.model());
		
        // Il distinct serve solo se ci sono le statistiche con contenuto.
        // NOTA: il distinct rende le ricerce inefficenti (ed inoltre non e' utilizzabile con campi clob in oracle)
        boolean distinct = false;
        ISQLQueryObject sqlQueryObjectCheckJoin = sqlQueryObject.newSQLQueryObject();
        _join(expression, sqlQueryObjectCheckJoin);
        distinct = ((SQLQueryObjectCore)sqlQueryObjectCheckJoin).sizeConditions()>0;
		
        if(!distinct && expression.inUseField(StatisticaGiornaliera.model().STATISTICA_BASE.DATA, true)){
        	// uso la prima colonna dell'indice (se c'è la data e non è distinct)
        	sqlQueryObject.addSelectCountField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.DATA, true),"tot",distinct);
        }
        else{
        	sqlQueryObject.addSelectCountField(this.getStatisticaGiornalieraFieldConverter().toTable(StatisticaGiornaliera.model())+".id","tot",distinct);
        }
		
		_join(expression,sqlQueryObject);
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.count(jdbcProperties, log, connection, sqlQueryObject, expression,
																			this.getStatisticaGiornalieraFieldConverter(), StatisticaGiornaliera.model(),listaQuery);
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
												this.getStatisticaGiornalieraFieldConverter(), field);

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
        						expression, this.getStatisticaGiornalieraFieldConverter(), StatisticaGiornaliera.model(), 
        						listaQuery,listaParams);
		
		_join(expression,sqlQueryObject);
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.select(jdbcProperties, log, connection,
        								org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareSqlQueryObjectForSelectDistinct(sqlQueryObject,sqlQueryObjectDistinct), 
        								expression, this.getStatisticaGiornalieraFieldConverter(), StatisticaGiornaliera.model(),
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
        						this.getStatisticaGiornalieraFieldConverter(), StatisticaGiornaliera.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.union(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getStatisticaGiornalieraFieldConverter(), StatisticaGiornaliera.model(), 
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
        						this.getStatisticaGiornalieraFieldConverter(), StatisticaGiornaliera.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        NonNegativeNumber number = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.unionCount(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getStatisticaGiornalieraFieldConverter(), StatisticaGiornaliera.model(), 
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
			return new JDBCExpression(this.getStatisticaGiornalieraFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}


	@Override
	public JDBCPaginatedExpression newPaginatedExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(this.getStatisticaGiornalieraFieldConverter());
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
	public StatisticaGiornaliera get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._get(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId), idMappingResolutionBehaviour);
	}
	
	private StatisticaGiornaliera _get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
				
		StatisticaGiornaliera statisticaGiornaliera = new StatisticaGiornaliera();
		

		// Object statisticaGiornaliera
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addFromTable(this.getStatisticaGiornalieraFieldConverter().toTable(StatisticaGiornaliera.model().STATISTICA_BASE));
		sqlQueryObjectGet.addSelectField("id");
		sqlQueryObjectGet.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.DATA,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.ID_PORTA,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_PORTA,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_MITTENTE,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.MITTENTE,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_DESTINATARIO,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.DESTINATARIO,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_SERVIZIO,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.SERVIZIO,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.VERSIONE_SERVIZIO,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.AZIONE,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.SERVIZIO_APPLICATIVO,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TRASPORTO_MITTENTE,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_ISSUER,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_CLIENT_ID,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_SUBJECT,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_USERNAME,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_MAIL,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.ESITO,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.ESITO_CONTESTO,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.NUMERO_TRANSAZIONI,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_INTERNA,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_ESTERNA,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.LATENZA_TOTALE,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.LATENZA_PORTA,true));
		sqlQueryObjectGet.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_BASE.LATENZA_SERVIZIO,true));
		sqlQueryObjectGet.addWhereCondition("id=?");

		// Get statisticaGiornaliera
		statisticaGiornaliera = (StatisticaGiornaliera) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(), StatisticaGiornaliera.model(), this.getStatisticaGiornalieraFetch(),
			new JDBCObject(tableId,Long.class));



		// Object statisticaGiornaliera_statisticaContenuti
		ISQLQueryObject sqlQueryObjectGet_statisticaGiornalieraContenuti = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_statisticaGiornalieraContenuti.setANDLogicOperator(true);
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addFromTable(this.getStatisticaGiornalieraFieldConverter().toTable(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField("id");
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DATA,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.RISORSA_NOME,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.RISORSA_VALORE,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_1,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_1,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_2,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_2,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_3,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_3,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_4,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_4,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_5,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_5,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_6,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_6,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_7,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_7,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_8,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_8,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_9,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_9,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_10,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_10,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.NUMERO_TRANSAZIONI,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_TOTALE,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_PORTA,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addSelectField(this.getStatisticaGiornalieraFieldConverter().toColumn(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_SERVIZIO,true));
		sqlQueryObjectGet_statisticaGiornalieraContenuti.addWhereCondition("id_stat=?");

		// Get statisticaGiornaliera_statisticaContenuti
		java.util.List<Object> statisticaGiornaliera_statisticaContenuti_list = (java.util.List<Object>) jdbcUtilities.executeQuery(sqlQueryObjectGet_statisticaGiornalieraContenuti.createSQLQuery(), jdbcProperties.isShowSql(), StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI, this.getStatisticaGiornalieraFetch(),
			new JDBCObject(statisticaGiornaliera.getId(),Long.class));

		if(statisticaGiornaliera_statisticaContenuti_list != null) {
			for (Object statisticaGiornaliera_statisticaContenuti_object: statisticaGiornaliera_statisticaContenuti_list) {
				StatisticaContenuti statisticaGiornaliera_statisticaContenuti = (StatisticaContenuti) statisticaGiornaliera_statisticaContenuti_object;


				statisticaGiornaliera.addStatisticaGiornalieraContenuti(statisticaGiornaliera_statisticaContenuti);
			}
		}

		
        return statisticaGiornaliera;  
	
	} 
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._exists(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	private boolean _exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		boolean existsStatisticaGiornaliera = false;

		sqlQueryObject = sqlQueryObject.newSQLQueryObject();
		sqlQueryObject.setANDLogicOperator(true);

		sqlQueryObject.addWhereCondition("id=?");


		// Exists statisticaGiornaliera
		existsStatisticaGiornaliera = jdbcUtilities.exists(sqlQueryObject.createSQLQuery(), jdbcProperties.isShowSql(),
			new JDBCObject(tableId,Long.class));

		
        return existsStatisticaGiornaliera;
	
	}
	
	private void _join(IExpression expression, ISQLQueryObject sqlQueryObject) throws NotImplementedException, ServiceException, Exception{
	
		AliasTableRicerchePersonalizzate.join(expression, sqlQueryObject, StatisticaGiornaliera.model().STATISTICA_BASE, 
				StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI, this.getFieldConverter());
		      
	}
	
	protected java.util.List<Object> _getRootTablePrimaryKeyValues(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, StatisticaGiornaliera statisticaGiornaliera) throws NotFoundException, ServiceException, NotImplementedException, Exception{
	    // Identificativi
        java.util.List<Object> rootTableIdValues = new java.util.ArrayList<Object>();
        rootTableIdValues.add(statisticaGiornaliera.getId());
        return rootTableIdValues;
	}
	
	protected Map<String, List<IField>> _getMapTableToPKColumn() throws NotImplementedException, Exception{
	
		StatisticaGiornalieraFieldConverter converter = this.getStatisticaGiornalieraFieldConverter();
		Map<String, List<IField>> mapTableToPKColumn = new java.util.Hashtable<String, List<IField>>();
		UtilsTemplate<IField> utilities = new UtilsTemplate<IField>();

		// StatisticaGiornaliera.model()
		mapTableToPKColumn.put(converter.toTable(StatisticaGiornaliera.model()),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(StatisticaGiornaliera.model()))
			));

		// StatisticaGiornaliera.model().STATISTICA_BASE
		mapTableToPKColumn.put(converter.toTable(StatisticaGiornaliera.model().STATISTICA_BASE),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(StatisticaGiornaliera.model().STATISTICA_BASE))
			));

		// StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI
		mapTableToPKColumn.put(converter.toTable(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI))
			));
        
        return mapTableToPKColumn;		
	}
	
	@Override
	public List<Long> findAllTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression paginatedExpression) throws ServiceException, NotImplementedException, Exception {
		
		List<Long> list = new ArrayList<Long>();

		// Il distinct serve solo se ci sono le statistiche con contenuto.
        // NOTA: il distinct rende le ricerce inefficenti (ed inoltre non e' utilizzabile con campi clob in oracle)
        boolean distinct = false;
        ISQLQueryObject sqlQueryObjectCheckJoin = sqlQueryObject.newSQLQueryObject();
        _join(paginatedExpression, sqlQueryObjectCheckJoin);
        distinct = ((SQLQueryObjectCore)sqlQueryObjectCheckJoin).sizeConditions()>0;
		
		sqlQueryObject.setSelectDistinct(distinct);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getStatisticaGiornalieraFieldConverter().toTable(StatisticaGiornaliera.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFindAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
												this.getStatisticaGiornalieraFieldConverter(), StatisticaGiornaliera.model());
		
		_join(paginatedExpression,sqlQueryObject);
		
		List<Object> listObjects = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.findAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
																			this.getStatisticaGiornalieraFieldConverter(), StatisticaGiornaliera.model(), objectIdClass, listaQuery);
		for(Object object: listObjects) {
			list.add((Long)object);
		}

        return list;
		
	}
	
	@Override
	public long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, Exception {

		// Il distinct serve solo se ci sono le statistiche con contenuto.
        // NOTA: il distinct rende le ricerce inefficenti (ed inoltre non e' utilizzabile con campi clob in oracle)
        boolean distinct = false;
        ISQLQueryObject sqlQueryObjectCheckJoin = sqlQueryObject.newSQLQueryObject();
        _join(expression, sqlQueryObjectCheckJoin);
        distinct = ((SQLQueryObjectCore)sqlQueryObjectCheckJoin).sizeConditions()>0;
		
		sqlQueryObject.setSelectDistinct(distinct);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getStatisticaGiornalieraFieldConverter().toTable(StatisticaGiornaliera.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFind(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getStatisticaGiornalieraFieldConverter(), StatisticaGiornaliera.model());
		
		_join(expression,sqlQueryObject);

		Object res = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.find(jdbcProperties, log, connection, sqlQueryObject, expression,
														this.getStatisticaGiornalieraFieldConverter(), StatisticaGiornaliera.model(), objectIdClass, listaQuery);
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
