/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.core.commons.search.dao.jdbc;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import java.sql.Connection;

import org.slf4j.Logger;

import org.openspcoop2.utils.sql.ISQLQueryObject;

import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.generic_project.dao.jdbc.utils.IJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject;
import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceSearchWithId;
import org.openspcoop2.core.commons.search.IdSoggetto;
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
import org.openspcoop2.core.commons.search.dao.jdbc.converter.SoggettoFieldConverter;
import org.openspcoop2.core.commons.search.dao.jdbc.fetch.SoggettoFetch;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.commons.search.SoggettoRuolo;

/**     
 * JDBCSoggettoServiceSearchImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCSoggettoServiceSearchImpl implements IJDBCServiceSearchWithId<Soggetto, IdSoggetto, JDBCServiceManager> {

	private SoggettoFieldConverter _soggettoFieldConverter = null;
	public SoggettoFieldConverter getSoggettoFieldConverter() {
		if(this._soggettoFieldConverter==null){
			this._soggettoFieldConverter = new SoggettoFieldConverter(this.jdbcServiceManager.getJdbcProperties().getDatabaseType());
		}		
		return this._soggettoFieldConverter;
	}
	@Override
	public ISQLFieldConverter getFieldConverter() {
		return this.getSoggettoFieldConverter();
	}
	
	private SoggettoFetch soggettoFetch = new SoggettoFetch();
	public SoggettoFetch getSoggettoFetch() {
		return this.soggettoFetch;
	}
	@Override
	public IJDBCFetch getFetch() {
		return getSoggettoFetch();
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
	public IdSoggetto convertToId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Soggetto soggetto) throws NotImplementedException, ServiceException, Exception{
	
		IdSoggetto idSoggetto = new IdSoggetto();
		idSoggetto.setNome(soggetto.getNomeSoggetto());
        idSoggetto.setTipo(soggetto.getTipoSoggetto());
        return idSoggetto;

	}
	
	@Override
	public Soggetto get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdSoggetto id, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {
		Long id_soggetto = ( (id!=null && id.getId()!=null && id.getId()>0) ? id.getId() : this.findIdSoggetto(jdbcProperties, log, connection, sqlQueryObject, id, true));
		return this._get(jdbcProperties, log, connection, sqlQueryObject, id_soggetto,idMappingResolutionBehaviour);
		
		
	}
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdSoggetto id) throws MultipleResultException, NotImplementedException, ServiceException,Exception {

		Long id_soggetto = this.findIdSoggetto(jdbcProperties, log, connection, sqlQueryObject, id, false);
		return id_soggetto != null && id_soggetto > 0;
		
	}
	
	@Override
	public List<IdSoggetto> findAllIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

		List<IdSoggetto> list = new ArrayList<IdSoggetto>();

		// TODO: implementazione non efficente. 
		// Per ottenere una implementazione efficente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari a create l'ID logico
		// 2. Usare metodo getSoggettoFetch() sul risultato della select per ottenere un oggetto Soggetto
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 
		// 3. Usare metodo convertToId per ottenere l'id

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	Soggetto soggetto = this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour);
			IdSoggetto idSoggetto = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,soggetto);
        	list.add(idSoggetto);
        }

        return list;
		
	}
	
	@Override
	public List<Soggetto> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

        List<Soggetto> list = new ArrayList<Soggetto>();
        
        // TODO: implementazione non efficente. 
		// Per ottenere una implementazione efficente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari
		// 2. Usare metodo getSoggettoFetch() sul risultato della select per ottenere un oggetto Soggetto
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	list.add(this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour));
        }

        return list;      
		
	}
	
	@Override
	public Soggetto find(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) 
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
												this.getSoggettoFieldConverter(), Soggetto.model());
		
		sqlQueryObject.addSelectCountField(this.getSoggettoFieldConverter().toTable(Soggetto.model())+".id","tot",true);
		
		_join(expression,sqlQueryObject);
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.count(jdbcProperties, log, connection, sqlQueryObject, expression,
																			this.getSoggettoFieldConverter(), Soggetto.model(),listaQuery);
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdSoggetto id) throws NotFoundException, NotImplementedException, ServiceException,Exception {
		
		Long id_soggetto = this.findIdSoggetto(jdbcProperties, log, connection, sqlQueryObject, id, true);
        return this._inUse(jdbcProperties, log, connection, sqlQueryObject, id_soggetto);
		
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
												this.getSoggettoFieldConverter(), field);

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
        						expression, this.getSoggettoFieldConverter(), Soggetto.model(), 
        						listaQuery,listaParams);
		
		_join(expression,sqlQueryObject);
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.select(jdbcProperties, log, connection,
        								org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareSqlQueryObjectForSelectDistinct(sqlQueryObject,sqlQueryObjectDistinct), 
        								expression, this.getSoggettoFieldConverter(), Soggetto.model(),
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
        						this.getSoggettoFieldConverter(), Soggetto.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.union(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getSoggettoFieldConverter(), Soggetto.model(), 
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
        						this.getSoggettoFieldConverter(), Soggetto.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        NonNegativeNumber number = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.unionCount(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getSoggettoFieldConverter(), Soggetto.model(), 
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
			return new JDBCExpression(this.getSoggettoFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}


	@Override
	public JDBCPaginatedExpression newPaginatedExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(this.getSoggettoFieldConverter());
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
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdSoggetto id, Soggetto obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,id,null));
	}
	
	@Override
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, Soggetto obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,tableId,null));
	}
	private void _mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Soggetto obj, Soggetto imgSaved) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		if(imgSaved==null){
			return;
		}
		obj.setId(imgSaved.getId());
		if(obj.getSoggettoRuoloList()!=null){
			List<org.openspcoop2.core.commons.search.SoggettoRuolo> listObj_ = obj.getSoggettoRuoloList();
			for(org.openspcoop2.core.commons.search.SoggettoRuolo itemObj_ : listObj_){
				org.openspcoop2.core.commons.search.SoggettoRuolo itemAlreadySaved_ = null;
				if(imgSaved.getSoggettoRuoloList()!=null){
					List<org.openspcoop2.core.commons.search.SoggettoRuolo> listImgSaved_ = imgSaved.getSoggettoRuoloList();
					for(org.openspcoop2.core.commons.search.SoggettoRuolo itemImgSaved_ : listImgSaved_){
						boolean objEqualsToImgSaved_ = false;
						objEqualsToImgSaved_ = org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getIdRuolo().getNome(),itemImgSaved_.getIdRuolo().getNome());
						if(objEqualsToImgSaved_){
							itemAlreadySaved_=itemImgSaved_;
							break;
						}
					}
				}
				if(itemAlreadySaved_!=null){
					itemObj_.setId(itemAlreadySaved_.getId());
					if(itemObj_.getIdRuolo()!=null && 
							itemAlreadySaved_.getIdRuolo()!=null){
						itemObj_.getIdRuolo().setId(itemAlreadySaved_.getIdRuolo().getId());
					}
					if(itemObj_.getIdSoggetto()!=null && 
							itemAlreadySaved_.getIdSoggetto()!=null){
						itemObj_.getIdSoggetto().setId(itemAlreadySaved_.getIdSoggetto().getId());
					}
				}
			}
		}

	}
	
	@Override
	public Soggetto get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._get(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId), idMappingResolutionBehaviour);
	}
	
	private Soggetto _get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
				
		Soggetto soggetto = new Soggetto();
		

		// Object soggetto
		ISQLQueryObject sqlQueryObjectGet_soggetto = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_soggetto.setANDLogicOperator(true);
		sqlQueryObjectGet_soggetto.addFromTable(this.getSoggettoFieldConverter().toTable(Soggetto.model()));
		sqlQueryObjectGet_soggetto.addSelectField("id");
		sqlQueryObjectGet_soggetto.addSelectField(this.getSoggettoFieldConverter().toColumn(Soggetto.model().NOME_SOGGETTO,true));
		sqlQueryObjectGet_soggetto.addSelectField(this.getSoggettoFieldConverter().toColumn(Soggetto.model().TIPO_SOGGETTO,true));
		sqlQueryObjectGet_soggetto.addSelectField(this.getSoggettoFieldConverter().toColumn(Soggetto.model().SERVER,true));
		sqlQueryObjectGet_soggetto.addSelectField(this.getSoggettoFieldConverter().toColumn(Soggetto.model().IDENTIFICATIVO_PORTA,true));
		sqlQueryObjectGet_soggetto.addWhereCondition("id=?");

		// Get soggetto
		soggetto = (Soggetto) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_soggetto.createSQLQuery(), jdbcProperties.isShowSql(), Soggetto.model(), this.getSoggettoFetch(),
			new JDBCObject(tableId,Long.class));



		// Object soggetto_soggettoRuolo
		ISQLQueryObject sqlQueryObjectGet_soggetto_soggettoRuolo = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_soggetto_soggettoRuolo.setANDLogicOperator(true);
		sqlQueryObjectGet_soggetto_soggettoRuolo.addFromTable(this.getSoggettoFieldConverter().toTable(Soggetto.model().SOGGETTO_RUOLO));
		sqlQueryObjectGet_soggetto_soggettoRuolo.addSelectField("id");
		sqlQueryObjectGet_soggetto_soggettoRuolo.addWhereCondition("id_soggetto=?");

		// Get soggetto_soggettoRuolo
		java.util.List<Object> soggetto_soggettoRuolo_list = (java.util.List<Object>) jdbcUtilities.executeQuery(sqlQueryObjectGet_soggetto_soggettoRuolo.createSQLQuery(), jdbcProperties.isShowSql(), Soggetto.model().SOGGETTO_RUOLO, this.getSoggettoFetch(),
			new JDBCObject(soggetto.getId(),Long.class));

		if(soggetto_soggettoRuolo_list != null) {
			for (Object soggetto_soggettoRuolo_object: soggetto_soggettoRuolo_list) {
				SoggettoRuolo soggetto_soggettoRuolo = (SoggettoRuolo) soggetto_soggettoRuolo_object;


				if(idMappingResolutionBehaviour==null ||
					(org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour) || org.openspcoop2.generic_project.beans.IDMappingBehaviour.USE_TABLE_ID.equals(idMappingResolutionBehaviour))
				){
					// Object _soggetto_soggettoRuolo_ruolo (recupero id)
					ISQLQueryObject sqlQueryObjectGet_soggetto_soggettoRuolo_ruolo_readFkId = sqlQueryObjectGet.newSQLQueryObject();
					sqlQueryObjectGet_soggetto_soggettoRuolo_ruolo_readFkId.addFromTable(this.getSoggettoFieldConverter().toTable(org.openspcoop2.core.commons.search.Soggetto.model().SOGGETTO_RUOLO));
					sqlQueryObjectGet_soggetto_soggettoRuolo_ruolo_readFkId.addSelectField("id_ruolo");
					sqlQueryObjectGet_soggetto_soggettoRuolo_ruolo_readFkId.addWhereCondition("id=?");
					sqlQueryObjectGet_soggetto_soggettoRuolo_ruolo_readFkId.setANDLogicOperator(true);
					Long idFK_soggetto_soggettoRuolo_ruolo = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_soggetto_soggettoRuolo_ruolo_readFkId.createSQLQuery(), jdbcProperties.isShowSql(),Long.class,
							new JDBCObject(soggetto_soggettoRuolo.getId(),Long.class));
					
					org.openspcoop2.core.commons.search.IdRuolo id_soggetto_soggettoRuolo_ruolo = null;
					if(idMappingResolutionBehaviour==null || org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour)){
						id_soggetto_soggettoRuolo_ruolo = ((JDBCRuoloServiceSearch)(this.getServiceManager().getRuoloServiceSearch())).findId(idFK_soggetto_soggettoRuolo_ruolo, false);
					}else{
						id_soggetto_soggettoRuolo_ruolo = new org.openspcoop2.core.commons.search.IdRuolo();
					}
					id_soggetto_soggettoRuolo_ruolo.setId(idFK_soggetto_soggettoRuolo_ruolo);
					soggetto_soggettoRuolo.setIdRuolo(id_soggetto_soggettoRuolo_ruolo);
				}

//				if(idMappingResolutionBehaviour==null ||
//					(org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour) || org.openspcoop2.generic_project.beans.IDMappingBehaviour.USE_TABLE_ID.equals(idMappingResolutionBehaviour))
//				){
//					// Object _soggetto_soggettoRuolo_soggetto (recupero id)
//					ISQLQueryObject sqlQueryObjectGet_soggetto_soggettoRuolo_soggetto_readFkId = sqlQueryObjectGet.newSQLQueryObject();
//					sqlQueryObjectGet_soggetto_soggettoRuolo_soggetto_readFkId.addFromTable(this.getSoggettoFieldConverter().toTable(org.openspcoop2.core.commons.search.Soggetto.model().SOGGETTO_RUOLO));
//					sqlQueryObjectGet_soggetto_soggettoRuolo_soggetto_readFkId.addSelectField("id_soggetto");
//					sqlQueryObjectGet_soggetto_soggettoRuolo_soggetto_readFkId.addWhereCondition("id=?");
//					sqlQueryObjectGet_soggetto_soggettoRuolo_soggetto_readFkId.setANDLogicOperator(true);
//					Long idFK_soggetto_soggettoRuolo_soggetto = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_soggetto_soggettoRuolo_soggetto_readFkId.createSQLQuery(), jdbcProperties.isShowSql(),Long.class,
//							new JDBCObject(soggetto_soggettoRuolo.getId(),Long.class));
//					
//					org.openspcoop2.core.commons.search.IdSoggetto id_soggetto_soggettoRuolo_soggetto = null;
//					if(idMappingResolutionBehaviour==null || org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour)){
//						id_soggetto_soggettoRuolo_soggetto = ((JDBCSoggettoServiceSearch)(this.getServiceManager().getSoggettoServiceSearch())).findId(idFK_soggetto_soggettoRuolo_soggetto, false);
//					}else{
//						id_soggetto_soggettoRuolo_soggetto = new org.openspcoop2.core.commons.search.IdSoggetto();
//					}
//					id_soggetto_soggettoRuolo_soggetto.setId(idFK_soggetto_soggettoRuolo_soggetto);
//					soggetto_soggettoRuolo.setIdSoggetto(id_soggetto_soggettoRuolo_soggetto);
//				}
				IdSoggetto idSoggetto = new IdSoggetto();
				idSoggetto.setTipo(soggetto.getTipoSoggetto());
				idSoggetto.setNome(soggetto.getNomeSoggetto());
				soggetto_soggettoRuolo.setIdSoggetto(idSoggetto);

				soggetto.addSoggettoRuolo(soggetto_soggettoRuolo);
			}
		}
               
		
        return soggetto;  
	
	} 
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._exists(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	private boolean _exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		boolean existsSoggetto = false;

		sqlQueryObject = sqlQueryObject.newSQLQueryObject();
		sqlQueryObject.setANDLogicOperator(true);

		sqlQueryObject.addFromTable(this.getSoggettoFieldConverter().toTable(Soggetto.model()));
		sqlQueryObject.addSelectField(this.getSoggettoFieldConverter().toColumn(Soggetto.model().NOME_SOGGETTO,true));
		sqlQueryObject.addWhereCondition("id=?");


		// Exists soggetto
		existsSoggetto = jdbcUtilities.exists(sqlQueryObject.createSQLQuery(), jdbcProperties.isShowSql(),
			new JDBCObject(tableId,Long.class));

		
        return existsSoggetto;
	
	}
	
	private void _join(IExpression expression, ISQLQueryObject sqlQueryObject) throws NotImplementedException, ServiceException, Exception{
	
		if(expression.inUseModel(Soggetto.model().SOGGETTO_RUOLO,false) || expression.inUseModel(Soggetto.model().SOGGETTO_RUOLO.ID_RUOLO,false)){
			String tableName1 = this.getSoggettoFieldConverter().toAliasTable(Soggetto.model());
			String tableName2 = this.getSoggettoFieldConverter().toAliasTable(Soggetto.model().SOGGETTO_RUOLO);
			sqlQueryObject.addWhereCondition(tableName1+".id="+tableName2+".id_soggetto");
			
			tableName1 = this.getSoggettoFieldConverter().toAliasTable(Soggetto.model().SOGGETTO_RUOLO.ID_RUOLO);
			tableName2 = this.getSoggettoFieldConverter().toAliasTable(Soggetto.model().SOGGETTO_RUOLO);
			sqlQueryObject.addWhereCondition(tableName1+".id="+tableName2+".id_ruolo");
		}
		
        
	}
	
	protected java.util.List<Object> _getRootTablePrimaryKeyValues(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdSoggetto id) throws NotFoundException, ServiceException, NotImplementedException, Exception{
	    // Identificativi
        java.util.List<Object> rootTableIdValues = new java.util.ArrayList<Object>();
        Long longId = this.findIdSoggetto(jdbcProperties, log, connection, sqlQueryObject.newSQLQueryObject(), id, true);
		rootTableIdValues.add(longId);
        return rootTableIdValues;
	}
	
	protected Map<String, List<IField>> _getMapTableToPKColumn() throws NotImplementedException, Exception{
	
		SoggettoFieldConverter converter = this.getSoggettoFieldConverter();
		Map<String, List<IField>> mapTableToPKColumn = new java.util.Hashtable<String, List<IField>>();
		UtilsTemplate<IField> utilities = new UtilsTemplate<IField>();

		// Soggetto.model()
		mapTableToPKColumn.put(converter.toTable(Soggetto.model()),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Soggetto.model()))
			));

		// Soggetto.model().SOGGETTO_RUOLO
		mapTableToPKColumn.put(converter.toTable(Soggetto.model().SOGGETTO_RUOLO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Soggetto.model().SOGGETTO_RUOLO))
			));

		// Soggetto.model().SOGGETTO_RUOLO.ID_RUOLO
		mapTableToPKColumn.put(converter.toTable(Soggetto.model().SOGGETTO_RUOLO.ID_RUOLO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Soggetto.model().SOGGETTO_RUOLO.ID_RUOLO))
			));

		// Soggetto.model().SOGGETTO_RUOLO.ID_SOGGETTO
		mapTableToPKColumn.put(converter.toTable(Soggetto.model().SOGGETTO_RUOLO.ID_SOGGETTO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Soggetto.model().SOGGETTO_RUOLO.ID_SOGGETTO))
			));

        return mapTableToPKColumn;		
	}
	
	@Override
	public List<Long> findAllTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression paginatedExpression) throws ServiceException, NotImplementedException, Exception {
		
		List<Long> list = new ArrayList<Long>();

		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getSoggettoFieldConverter().toTable(Soggetto.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFindAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
												this.getSoggettoFieldConverter(), Soggetto.model());
		
		_join(paginatedExpression,sqlQueryObject);
		
		List<Object> listObjects = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.findAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
																			this.getSoggettoFieldConverter(), Soggetto.model(), objectIdClass, listaQuery);
		for(Object object: listObjects) {
			list.add((Long)object);
		}

        return list;
		
	}
	
	@Override
	public long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, Exception {
	
		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getSoggettoFieldConverter().toTable(Soggetto.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFind(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getSoggettoFieldConverter(), Soggetto.model());
		
		_join(expression,sqlQueryObject);

		Object res = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.find(jdbcProperties, log, connection, sqlQueryObject, expression,
														this.getSoggettoFieldConverter(), Soggetto.model(), objectIdClass, listaQuery);
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
	public IdSoggetto findId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();              

		// Object _soggetto
		sqlQueryObjectGet.addFromTable(this.getSoggettoFieldConverter().toTable(Soggetto.model()));
		sqlQueryObjectGet.addSelectField(this.getSoggettoFieldConverter().toColumn(Soggetto.model().TIPO_SOGGETTO,true));
		sqlQueryObjectGet.addSelectField(this.getSoggettoFieldConverter().toColumn(Soggetto.model().NOME_SOGGETTO,true));
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addWhereCondition("id=?");

		// Recupero _soggetto
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_soggetto = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(tableId,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_soggetto = new ArrayList<Class<?>>();
		listaFieldIdReturnType_soggetto.add(String.class);
		listaFieldIdReturnType_soggetto.add(String.class);
		org.openspcoop2.core.commons.search.IdSoggetto id_soggetto = null;
		List<Object> listaFieldId_soggetto = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_soggetto, searchParams_soggetto);
		if(listaFieldId_soggetto==null || listaFieldId_soggetto.size()<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		else{
			// set _soggetto
			id_soggetto = new org.openspcoop2.core.commons.search.IdSoggetto();
			id_soggetto.setTipo((String)listaFieldId_soggetto.get(0));
			id_soggetto.setNome((String)listaFieldId_soggetto.get(1));
		}
		
		return id_soggetto;
		
	}

	@Override
	public Long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdSoggetto id, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
	
		return this.findIdSoggetto(jdbcProperties,log,connection,sqlQueryObject,id,throwNotFound);
			
	}
	
	@Override
	public List<List<Object>> nativeQuery(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
											String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException,Exception{
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.nativeQuery(jdbcProperties, log, connection, sqlQueryObject,
																							sql,returnClassTypes,param);
														
	}
	
	protected Long findIdSoggetto(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdSoggetto id, boolean throwNotFound) throws NotFoundException, ServiceException, NotImplementedException, Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();

		// Object _soggetto
		sqlQueryObjectGet.addFromTable(this.getSoggettoFieldConverter().toTable(Soggetto.model()));
		sqlQueryObjectGet.addSelectField("id");
		sqlQueryObjectGet.addWhereCondition(this.getSoggettoFieldConverter().toColumn(Soggetto.model().NOME_SOGGETTO,true)+"=?");
		sqlQueryObjectGet.addWhereCondition(this.getSoggettoFieldConverter().toColumn(Soggetto.model().TIPO_SOGGETTO,true)+"=?");
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.setSelectDistinct(true);
		
		// Recupero _soggetto
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_soggetto = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
				new JDBCObject(id.getNome(), id.getNome().getClass()),
				new JDBCObject(id.getTipo(), id.getTipo().getClass()),
		};
		Long id_soggetto = null;
		try{
			id_soggetto = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
						Long.class, searchParams_soggetto);
		}catch(NotFoundException notFound){
			if(throwNotFound){
				throw new NotFoundException(notFound);
			}
		}
		if(id_soggetto==null || id_soggetto<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		
		return id_soggetto;
	}
}
