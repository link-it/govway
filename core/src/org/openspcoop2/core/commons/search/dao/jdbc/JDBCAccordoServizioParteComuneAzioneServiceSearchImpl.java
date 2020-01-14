/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione;
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
import org.openspcoop2.core.commons.search.dao.jdbc.converter.AccordoServizioParteComuneAzioneFieldConverter;
import org.openspcoop2.core.commons.search.dao.jdbc.fetch.AccordoServizioParteComuneAzioneFetch;
import org.openspcoop2.core.commons.search.dao.IDBAccordoServizioParteComuneServiceSearch;
import org.openspcoop2.core.commons.search.dao.jdbc.JDBCServiceManager;
import org.openspcoop2.core.commons.search.AccordoServizioParteComune;
import org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComune;

/**     
 * JDBCAccordoServizioParteComuneAzioneServiceSearchImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCAccordoServizioParteComuneAzioneServiceSearchImpl implements IJDBCServiceSearchWithId<AccordoServizioParteComuneAzione, IdAccordoServizioParteComuneAzione, JDBCServiceManager> {

	private AccordoServizioParteComuneAzioneFieldConverter _accordoServizioParteComuneAzioneFieldConverter = null;
	public AccordoServizioParteComuneAzioneFieldConverter getAccordoServizioParteComuneAzioneFieldConverter() {
		if(this._accordoServizioParteComuneAzioneFieldConverter==null){
			this._accordoServizioParteComuneAzioneFieldConverter = new AccordoServizioParteComuneAzioneFieldConverter(this.jdbcServiceManager.getJdbcProperties().getDatabaseType());
		}		
		return this._accordoServizioParteComuneAzioneFieldConverter;
	}
	@Override
	public ISQLFieldConverter getFieldConverter() {
		return this.getAccordoServizioParteComuneAzioneFieldConverter();
	}
	
	private AccordoServizioParteComuneAzioneFetch accordoServizioParteComuneAzioneFetch = new AccordoServizioParteComuneAzioneFetch();
	public AccordoServizioParteComuneAzioneFetch getAccordoServizioParteComuneAzioneFetch() {
		return this.accordoServizioParteComuneAzioneFetch;
	}
	@Override
	public IJDBCFetch getFetch() {
		return getAccordoServizioParteComuneAzioneFetch();
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
	public IdAccordoServizioParteComuneAzione convertToId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, AccordoServizioParteComuneAzione accordoServizioParteComuneAzione) throws NotImplementedException, ServiceException, Exception{
	
		IdAccordoServizioParteComuneAzione idAccordoServizioParteComuneAzione = new IdAccordoServizioParteComuneAzione();
		idAccordoServizioParteComuneAzione.setNome(accordoServizioParteComuneAzione.getNome());
		idAccordoServizioParteComuneAzione.setIdAccordoServizioParteComune(accordoServizioParteComuneAzione.getIdAccordoServizioParteComune());
		return idAccordoServizioParteComuneAzione;
	}
	
	@Override
	public AccordoServizioParteComuneAzione get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAccordoServizioParteComuneAzione id, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {
		Long id_accordoServizioParteComuneAzione = ( (id!=null && id.getId()!=null && id.getId()>0) ? id.getId() : this.findIdAccordoServizioParteComuneAzione(jdbcProperties, log, connection, sqlQueryObject, id, true));
		return this._get(jdbcProperties, log, connection, sqlQueryObject, id_accordoServizioParteComuneAzione,idMappingResolutionBehaviour);
		
		
	}
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAccordoServizioParteComuneAzione id) throws MultipleResultException, NotImplementedException, ServiceException,Exception {

		Long id_accordoServizioParteComuneAzione = this.findIdAccordoServizioParteComuneAzione(jdbcProperties, log, connection, sqlQueryObject, id, false);
		return id_accordoServizioParteComuneAzione != null && id_accordoServizioParteComuneAzione > 0;
		
	}
	
	@Override
	public List<IdAccordoServizioParteComuneAzione> findAllIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

		List<IdAccordoServizioParteComuneAzione> list = new ArrayList<IdAccordoServizioParteComuneAzione>();

		// TODO: implementazione non efficente. 
		// Per ottenere una implementazione efficente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari a create l'ID logico
		// 2. Usare metodo getAccordoServizioParteComuneAzioneFetch() sul risultato della select per ottenere un oggetto AccordoServizioParteComuneAzione
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 
		// 3. Usare metodo convertToId per ottenere l'id

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	AccordoServizioParteComuneAzione accordoServizioParteComuneAzione = this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour);
			IdAccordoServizioParteComuneAzione idAccordoServizioParteComuneAzione = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,accordoServizioParteComuneAzione);
        	list.add(idAccordoServizioParteComuneAzione);
        }

        return list;
		
	}
	
	@Override
	public List<AccordoServizioParteComuneAzione> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

        List<AccordoServizioParteComuneAzione> list = new ArrayList<AccordoServizioParteComuneAzione>();
        
        // TODO: implementazione non efficente. 
		// Per ottenere una implementazione efficente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari
		// 2. Usare metodo getAccordoServizioParteComuneAzioneFetch() sul risultato della select per ottenere un oggetto AccordoServizioParteComuneAzione
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	list.add(this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour));
        }

        return list;      
		
	}
	
	@Override
	public AccordoServizioParteComuneAzione find(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) 
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
												this.getAccordoServizioParteComuneAzioneFieldConverter(), AccordoServizioParteComuneAzione.model());
		
		sqlQueryObject.addSelectCountField(this.getAccordoServizioParteComuneAzioneFieldConverter().toTable(AccordoServizioParteComuneAzione.model())+".id","tot",true);
		
		_join(expression,sqlQueryObject);
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.count(jdbcProperties, log, connection, sqlQueryObject, expression,
																			this.getAccordoServizioParteComuneAzioneFieldConverter(), AccordoServizioParteComuneAzione.model(),listaQuery);
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAccordoServizioParteComuneAzione id) throws NotFoundException, NotImplementedException, ServiceException,Exception {
		
		Long id_accordoServizioParteComuneAzione = this.findIdAccordoServizioParteComuneAzione(jdbcProperties, log, connection, sqlQueryObject, id, true);
        return this._inUse(jdbcProperties, log, connection, sqlQueryObject, id_accordoServizioParteComuneAzione);
		
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
												this.getAccordoServizioParteComuneAzioneFieldConverter(), field);

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
        						expression, this.getAccordoServizioParteComuneAzioneFieldConverter(), AccordoServizioParteComuneAzione.model(), 
        						listaQuery,listaParams);
		
		_join(expression,sqlQueryObject);
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.select(jdbcProperties, log, connection,
        								org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareSqlQueryObjectForSelectDistinct(sqlQueryObject,sqlQueryObjectDistinct), 
        								expression, this.getAccordoServizioParteComuneAzioneFieldConverter(), AccordoServizioParteComuneAzione.model(),
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
        						this.getAccordoServizioParteComuneAzioneFieldConverter(), AccordoServizioParteComuneAzione.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.union(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getAccordoServizioParteComuneAzioneFieldConverter(), AccordoServizioParteComuneAzione.model(), 
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
        						this.getAccordoServizioParteComuneAzioneFieldConverter(), AccordoServizioParteComuneAzione.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        NonNegativeNumber number = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.unionCount(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getAccordoServizioParteComuneAzioneFieldConverter(), AccordoServizioParteComuneAzione.model(), 
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
			return new JDBCExpression(this.getAccordoServizioParteComuneAzioneFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}


	@Override
	public JDBCPaginatedExpression newPaginatedExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(this.getAccordoServizioParteComuneAzioneFieldConverter());
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
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAccordoServizioParteComuneAzione id, AccordoServizioParteComuneAzione obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,id,null));
	}
	
	@Override
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, AccordoServizioParteComuneAzione obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,tableId,null));
	}
	private void _mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, AccordoServizioParteComuneAzione obj, AccordoServizioParteComuneAzione imgSaved) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		if(imgSaved==null){
			return;
		}
		obj.setId(imgSaved.getId());
		if(obj.getIdAccordoServizioParteComune()!=null && 
				imgSaved.getIdAccordoServizioParteComune()!=null){
			obj.getIdAccordoServizioParteComune().setId(imgSaved.getIdAccordoServizioParteComune().getId());
			if(obj.getIdAccordoServizioParteComune().getIdSoggetto()!=null && 
					imgSaved.getIdAccordoServizioParteComune().getIdSoggetto()!=null){
				obj.getIdAccordoServizioParteComune().getIdSoggetto().setId(imgSaved.getIdAccordoServizioParteComune().getIdSoggetto().getId());
			}
		}

	}
	
	@Override
	public AccordoServizioParteComuneAzione get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._get(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId), idMappingResolutionBehaviour);
	}
	
	private AccordoServizioParteComuneAzione _get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
				
		AccordoServizioParteComuneAzione accordoServizioParteComuneAzione = new AccordoServizioParteComuneAzione();
		

		// Object accordoServizioParteComuneAzione
		ISQLQueryObject sqlQueryObjectGet_accordoServizioParteComuneAzione = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_accordoServizioParteComuneAzione.setANDLogicOperator(true);
		sqlQueryObjectGet_accordoServizioParteComuneAzione.addFromTable(this.getAccordoServizioParteComuneAzioneFieldConverter().toTable(AccordoServizioParteComuneAzione.model()));
		sqlQueryObjectGet_accordoServizioParteComuneAzione.addSelectField("id");
		sqlQueryObjectGet_accordoServizioParteComuneAzione.addSelectField(this.getAccordoServizioParteComuneAzioneFieldConverter().toColumn(AccordoServizioParteComuneAzione.model().NOME,true));
		sqlQueryObjectGet_accordoServizioParteComuneAzione.addWhereCondition("id=?");

		// Get accordoServizioParteComuneAzione
		accordoServizioParteComuneAzione = (AccordoServizioParteComuneAzione) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_accordoServizioParteComuneAzione.createSQLQuery(), jdbcProperties.isShowSql(), AccordoServizioParteComuneAzione.model(), this.getAccordoServizioParteComuneAzioneFetch(),
			new JDBCObject(tableId,Long.class));


		// Recupero idAccordo
		ISQLQueryObject sqlQueryObjectGet_accordoServizioParteComune = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_accordoServizioParteComune.addFromTable(this.getAccordoServizioParteComuneAzioneFieldConverter().toTable(AccordoServizioParteComuneAzione.model()));
		sqlQueryObjectGet_accordoServizioParteComune.addSelectField("id_accordo");
		sqlQueryObjectGet_accordoServizioParteComune.setANDLogicOperator(true);
		sqlQueryObjectGet_accordoServizioParteComune.addWhereCondition("id=?");
		
		// Recupero _accordoServizioParteComune_soggetto
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_accordoServizioParteComune = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
				new JDBCObject(accordoServizioParteComuneAzione.getId(), Long.class)
		};
		Long id_accordoServizioParteComune = 
			(Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_accordoServizioParteComune.createSQLQuery(), jdbcProperties.isShowSql(),
			Long.class, searchParams_accordoServizioParteComune);
		
		IDBAccordoServizioParteComuneServiceSearch search = ((IDBAccordoServizioParteComuneServiceSearch)this.getServiceManager().getAccordoServizioParteComuneServiceSearch());
		AccordoServizioParteComune as = search.get(id_accordoServizioParteComune);
		IdAccordoServizioParteComune idAccordo = search.convertToId(as);
		accordoServizioParteComuneAzione.setIdAccordoServizioParteComune(idAccordo);
		
        return accordoServizioParteComuneAzione;  
	
	} 
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._exists(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	private boolean _exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		boolean existsAccordoServizioParteComuneAzione = false;

		sqlQueryObject = sqlQueryObject.newSQLQueryObject();
		sqlQueryObject.setANDLogicOperator(true);

		sqlQueryObject.addFromTable(this.getAccordoServizioParteComuneAzioneFieldConverter().toTable(AccordoServizioParteComuneAzione.model()));
		sqlQueryObject.addSelectField(this.getAccordoServizioParteComuneAzioneFieldConverter().toColumn(AccordoServizioParteComuneAzione.model().NOME,true));
		sqlQueryObject.addWhereCondition("id=?");


		// Exists accordoServizioParteComuneAzione
		existsAccordoServizioParteComuneAzione = jdbcUtilities.exists(sqlQueryObject.createSQLQuery(), jdbcProperties.isShowSql(),
			new JDBCObject(tableId,Long.class));

		
        return existsAccordoServizioParteComuneAzione;
	
	}
	
	private void _join(IExpression expression, ISQLQueryObject sqlQueryObject) throws NotImplementedException, ServiceException, Exception{
	
		if(expression.inUseModel(AccordoServizioParteComuneAzione.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE,false)){
			String tableName1 = this.getAccordoServizioParteComuneAzioneFieldConverter().toAliasTable(AccordoServizioParteComuneAzione.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE);
			String tableName2 = this.getAccordoServizioParteComuneAzioneFieldConverter().toAliasTable(AccordoServizioParteComuneAzione.model());
			sqlQueryObject.addWhereCondition(tableName1+".id="+tableName2+".id_accordo");
		}
		if(expression.inUseModel(AccordoServizioParteComuneAzione.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO,false)){
			String tableName1 = this.getAccordoServizioParteComuneAzioneFieldConverter().toAliasTable(AccordoServizioParteComuneAzione.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE);
			String tableName2 = this.getAccordoServizioParteComuneAzioneFieldConverter().toAliasTable(AccordoServizioParteComuneAzione.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO);
			sqlQueryObject.addWhereCondition(tableName1+".id_referente="+tableName2+".id");
		}
		
        if(expression.inUseModel(AccordoServizioParteComuneAzione.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO,false)){
			if(expression.inUseModel(AccordoServizioParteComuneAzione.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE,false)==false){
				sqlQueryObject.addFromTable(this.getAccordoServizioParteComuneAzioneFieldConverter().toTable(AccordoServizioParteComuneAzione.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE));
			}
		}
        
	}
	
	protected java.util.List<Object> _getRootTablePrimaryKeyValues(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAccordoServizioParteComuneAzione id) throws NotFoundException, ServiceException, NotImplementedException, Exception{
	    // Identificativi
        java.util.List<Object> rootTableIdValues = new java.util.ArrayList<Object>();
        Long longId = this.findIdAccordoServizioParteComuneAzione(jdbcProperties, log, connection, sqlQueryObject.newSQLQueryObject(), id, true);
		rootTableIdValues.add(longId);
        return rootTableIdValues;
	}
	
	protected Map<String, List<IField>> _getMapTableToPKColumn() throws NotImplementedException, Exception{
	
		AccordoServizioParteComuneAzioneFieldConverter converter = this.getAccordoServizioParteComuneAzioneFieldConverter();
		Map<String, List<IField>> mapTableToPKColumn = new java.util.Hashtable<String, List<IField>>();
		UtilsTemplate<IField> utilities = new UtilsTemplate<IField>();

		// AccordoServizioParteComuneAzione.model()
		mapTableToPKColumn.put(converter.toTable(AccordoServizioParteComuneAzione.model()),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AccordoServizioParteComuneAzione.model()))
			));

		// AccordoServizioParteComuneAzione.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE
		mapTableToPKColumn.put(converter.toTable(AccordoServizioParteComuneAzione.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AccordoServizioParteComuneAzione.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE))
			));

		// AccordoServizioParteComuneAzione.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO
		mapTableToPKColumn.put(converter.toTable(AccordoServizioParteComuneAzione.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AccordoServizioParteComuneAzione.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO))
			));
        
        return mapTableToPKColumn;		
	}
	
	@Override
	public List<Long> findAllTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression paginatedExpression) throws ServiceException, NotImplementedException, Exception {
		
		List<Long> list = new ArrayList<Long>();

		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getAccordoServizioParteComuneAzioneFieldConverter().toTable(AccordoServizioParteComuneAzione.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFindAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
												this.getAccordoServizioParteComuneAzioneFieldConverter(), AccordoServizioParteComuneAzione.model());
		
		_join(paginatedExpression,sqlQueryObject);
		
		List<Object> listObjects = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.findAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
																			this.getAccordoServizioParteComuneAzioneFieldConverter(), AccordoServizioParteComuneAzione.model(), objectIdClass, listaQuery);
		for(Object object: listObjects) {
			list.add((Long)object);
		}

        return list;
		
	}
	
	@Override
	public long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, Exception {
	
		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getAccordoServizioParteComuneAzioneFieldConverter().toTable(AccordoServizioParteComuneAzione.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFind(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getAccordoServizioParteComuneAzioneFieldConverter(), AccordoServizioParteComuneAzione.model());
		
		_join(expression,sqlQueryObject);

		Object res = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.find(jdbcProperties, log, connection, sqlQueryObject, expression,
														this.getAccordoServizioParteComuneAzioneFieldConverter(), AccordoServizioParteComuneAzione.model(), objectIdClass, listaQuery);
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
	public IdAccordoServizioParteComuneAzione findId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();               

		// Object _accordoServizioParteComuneAzione
		sqlQueryObjectGet.addFromTable(this.getAccordoServizioParteComuneAzioneFieldConverter().toTable(AccordoServizioParteComuneAzione.model()));
		sqlQueryObjectGet.addSelectField(this.getAccordoServizioParteComuneAzioneFieldConverter().toColumn(AccordoServizioParteComuneAzione.model().NOME,true));
		sqlQueryObjectGet.addSelectField("id_accordo");
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addWhereCondition("id=?");

		// Recupero _accordoServizioParteComuneAzione
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_accordoServizioParteComuneAzione = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(tableId,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_accordoServizioParteComuneAzione = new ArrayList<Class<?>>();
		listaFieldIdReturnType_accordoServizioParteComuneAzione.add(String.class);
		listaFieldIdReturnType_accordoServizioParteComuneAzione.add(Long.class);
		org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione id_accordoServizioParteComuneAzione = null;
		List<Object> listaFieldId_accordoServizioParteComuneAzione = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_accordoServizioParteComuneAzione, searchParams_accordoServizioParteComuneAzione);
		if(listaFieldId_accordoServizioParteComuneAzione==null || listaFieldId_accordoServizioParteComuneAzione.size()<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		else{
			// set _accordoServizioParteComuneAzione
			id_accordoServizioParteComuneAzione = new org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneAzione();
			id_accordoServizioParteComuneAzione.setNome((String)listaFieldId_accordoServizioParteComuneAzione.get(0));
			Long idAccordoFK = (Long) listaFieldId_accordoServizioParteComuneAzione.get(1);
			id_accordoServizioParteComuneAzione.
				setIdAccordoServizioParteComune(((IDBAccordoServizioParteComuneServiceSearch)this.getServiceManager().
						getAccordoServizioParteComuneServiceSearch()).findId(idAccordoFK, true));
		}
		
		return id_accordoServizioParteComuneAzione;
		
	}

	@Override
	public Long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAccordoServizioParteComuneAzione id, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
	
		return this.findIdAccordoServizioParteComuneAzione(jdbcProperties,log,connection,sqlQueryObject,id,throwNotFound);
			
	}
	
	@Override
	public List<List<Object>> nativeQuery(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
											String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException,Exception{
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.nativeQuery(jdbcProperties, log, connection, sqlQueryObject,
																							sql,returnClassTypes,param);
														
	}
	
	protected Long findIdAccordoServizioParteComuneAzione(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAccordoServizioParteComuneAzione id, boolean throwNotFound) throws NotFoundException, ServiceException, NotImplementedException, Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();

		if(id.getIdAccordoServizioParteComune()==null){
			throw new ServiceException("IdAccordoServizioParteComune non fornito");
		}
		if(id.getIdAccordoServizioParteComune().getIdSoggetto()==null){
			throw new ServiceException("IdAccordoServizioParteComune.getIdSoggetto non fornito");
		}
		
		// Recupero idAccordo
		AccordoServizioParteComune as = this.getServiceManager().getAccordoServizioParteComuneServiceSearch().get(id.getIdAccordoServizioParteComune());
		
             
		// Object _accordoServizioParteComuneAzione
		sqlQueryObjectGet.addFromTable(this.getAccordoServizioParteComuneAzioneFieldConverter().toTable(AccordoServizioParteComuneAzione.model()));
		sqlQueryObjectGet.addSelectField("id");
		// Devono essere mappati nella where condition i metodi dell'oggetto id.getXXX
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.setSelectDistinct(true);
		sqlQueryObjectGet.addWhereCondition(this.getAccordoServizioParteComuneAzioneFieldConverter().toColumn(AccordoServizioParteComuneAzione.model().NOME,true)+"=?");
		sqlQueryObjectGet.addWhereCondition("id_accordo=?");
		
		// Recupero _accordoServizioParteComuneAzione
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_accordoServizioParteComuneAzione = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
				new JDBCObject(id.getNome(), String.class),
				new JDBCObject(as.getId(), Long.class)
		};
		Long id_accordoServizioParteComuneAzione = null;
		try{
			id_accordoServizioParteComuneAzione = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
						Long.class, searchParams_accordoServizioParteComuneAzione);
		}catch(NotFoundException notFound){
			if(throwNotFound){
				throw new NotFoundException(notFound);
			}
		}
		if(id_accordoServizioParteComuneAzione==null || id_accordoServizioParteComuneAzione<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		
		return id_accordoServizioParteComuneAzione;
	}
}
