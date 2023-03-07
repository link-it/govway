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
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo;
import org.openspcoop2.core.commons.search.IdGruppo;
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
import org.openspcoop2.core.commons.search.dao.jdbc.converter.AccordoServizioParteComuneGruppoFieldConverter;
import org.openspcoop2.core.commons.search.dao.jdbc.fetch.AccordoServizioParteComuneGruppoFetch;
import org.openspcoop2.core.commons.search.dao.IDBAccordoServizioParteComuneServiceSearch;
import org.openspcoop2.core.commons.search.dao.IDBGruppoServiceSearch;
import org.openspcoop2.core.commons.search.AccordoServizioParteComune;
import org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo;
import org.openspcoop2.core.commons.search.Gruppo;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComune;

/**     
 * JDBCAccordoServizioParteComuneGruppoServiceSearchImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCAccordoServizioParteComuneGruppoServiceSearchImpl implements IJDBCServiceSearchWithId<AccordoServizioParteComuneGruppo, IdAccordoServizioParteComuneGruppo, JDBCServiceManager> {

	private AccordoServizioParteComuneGruppoFieldConverter _accordoServizioParteComuneGruppoFieldConverter = null;
	public AccordoServizioParteComuneGruppoFieldConverter getAccordoServizioParteComuneGruppoFieldConverter() {
		if(this._accordoServizioParteComuneGruppoFieldConverter==null){
			this._accordoServizioParteComuneGruppoFieldConverter = new AccordoServizioParteComuneGruppoFieldConverter(this.jdbcServiceManager.getJdbcProperties().getDatabaseType());
		}		
		return this._accordoServizioParteComuneGruppoFieldConverter;
	}
	@Override
	public ISQLFieldConverter getFieldConverter() {
		return this.getAccordoServizioParteComuneGruppoFieldConverter();
	}
	
	private AccordoServizioParteComuneGruppoFetch accordoServizioParteComuneGruppoFetch = new AccordoServizioParteComuneGruppoFetch();
	public AccordoServizioParteComuneGruppoFetch getAccordoServizioParteComuneGruppoFetch() {
		return this.accordoServizioParteComuneGruppoFetch;
	}
	@Override
	public IJDBCFetch getFetch() {
		return getAccordoServizioParteComuneGruppoFetch();
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
	public JDBCServiceManager getServiceManager(Connection connection, JDBCServiceManagerProperties jdbcProperties, Logger log) throws ServiceException{
		return new JDBCServiceManager(connection, jdbcProperties, log);
	}
	

	@Override
	public IdAccordoServizioParteComuneGruppo convertToId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, AccordoServizioParteComuneGruppo accordoServizioParteComuneGruppo) throws NotImplementedException, ServiceException, Exception{
	
		IdAccordoServizioParteComuneGruppo idAccordoServizioParteComuneGruppo = new IdAccordoServizioParteComuneGruppo();
		idAccordoServizioParteComuneGruppo.setIdGruppo(accordoServizioParteComuneGruppo.getIdGruppo());
		idAccordoServizioParteComuneGruppo.setIdAccordoServizioParteComune(accordoServizioParteComuneGruppo.getIdAccordoServizioParteComune());
		
		return idAccordoServizioParteComuneGruppo;
	}
	
	@Override
	public AccordoServizioParteComuneGruppo get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAccordoServizioParteComuneGruppo id, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {
		Long id_accordoServizioParteComuneGruppo = ( (id!=null && id.getId()!=null && id.getId()>0) ? id.getId() : this.findIdAccordoServizioParteComuneGruppo(jdbcProperties, log, connection, sqlQueryObject, id, true));
		return this._get(jdbcProperties, log, connection, sqlQueryObject, id_accordoServizioParteComuneGruppo,idMappingResolutionBehaviour);
		
		
	}
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAccordoServizioParteComuneGruppo id) throws MultipleResultException, NotImplementedException, ServiceException,Exception {

		Long id_accordoServizioParteComuneGruppo = this.findIdAccordoServizioParteComuneGruppo(jdbcProperties, log, connection, sqlQueryObject, id, false);
		return id_accordoServizioParteComuneGruppo != null && id_accordoServizioParteComuneGruppo > 0;
		
	}
	
	@Override
	public List<IdAccordoServizioParteComuneGruppo> findAllIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

		List<IdAccordoServizioParteComuneGruppo> list = new ArrayList<IdAccordoServizioParteComuneGruppo>();

		// TODO: implementazione non efficiente. 
		// Per ottenere una implementazione efficiente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari a create l'ID logico
		// 2. Usare metodo getAccordoServizioParteComuneGruppoFetch() sul risultato della select per ottenere un oggetto AccordoServizioParteComuneGruppo
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 
		// 3. Usare metodo convertToId per ottenere l'id

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	AccordoServizioParteComuneGruppo accordoServizioParteComuneGruppo = this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour);
			IdAccordoServizioParteComuneGruppo idAccordoServizioParteComuneGruppo = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,accordoServizioParteComuneGruppo);
        	list.add(idAccordoServizioParteComuneGruppo);
        }

        return list;
		
	}
	
	@Override
	public List<AccordoServizioParteComuneGruppo> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

        List<AccordoServizioParteComuneGruppo> list = new ArrayList<AccordoServizioParteComuneGruppo>();
        
        // TODO: implementazione non efficiente. 
		// Per ottenere una implementazione efficiente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari
		// 2. Usare metodo getAccordoServizioParteComuneGruppoFetch() sul risultato della select per ottenere un oggetto AccordoServizioParteComuneGruppo
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	list.add(this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour));
        }

        return list;      
		
	}
	
	@Override
	public AccordoServizioParteComuneGruppo find(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) 
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
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareCount(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getAccordoServizioParteComuneGruppoFieldConverter(), AccordoServizioParteComuneGruppo.model());
		
		sqlQueryObject.addSelectCountField(this.getAccordoServizioParteComuneGruppoFieldConverter().toTable(AccordoServizioParteComuneGruppo.model())+".id","tot",true);
		
		_join(expression,sqlQueryObject);
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.count(jdbcProperties, log, connection, sqlQueryObject, expression,
																			this.getAccordoServizioParteComuneGruppoFieldConverter(), AccordoServizioParteComuneGruppo.model(),listaQuery);
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAccordoServizioParteComuneGruppo id) throws NotFoundException, NotImplementedException, ServiceException,Exception {
		
		Long id_accordoServizioParteComuneGruppo = this.findIdAccordoServizioParteComuneGruppo(jdbcProperties, log, connection, sqlQueryObject, id, true);
        return this._inUse(jdbcProperties, log, connection, sqlQueryObject, id_accordoServizioParteComuneGruppo);
		
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
		return org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.selectSingleObject(map);
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
		
		org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.setFields(sqlQueryObject,paginatedExpression,field);
		try{
		
			ISQLQueryObject sqlQueryObjectDistinct = 
						org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareSqlQueryObjectForSelectDistinct(distinct,sqlQueryObject, paginatedExpression, log,
												this.getAccordoServizioParteComuneGruppoFieldConverter(), field);

			return _select(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression, sqlQueryObjectDistinct);
			
		}finally{
			org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.removeFields(sqlQueryObject,paginatedExpression,field);
		}
	}

	@Override
	public Object aggregate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCExpression expression, FunctionField functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		Map<String,Object> map = 
			this.aggregate(jdbcProperties, log, connection, sqlQueryObject, expression, new FunctionField[]{functionField});
		return org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.selectAggregateObject(map,functionField);
	}
	
	@Override
	public Map<String,Object> aggregate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCExpression expression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception {													
		
		org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.setFields(sqlQueryObject,expression,functionField);
		try{
			List<Map<String,Object>> list = _select(jdbcProperties, log, connection, sqlQueryObject, expression);
			return list.get(0);
		}finally{
			org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.removeFields(sqlQueryObject,expression,functionField);
		}
	}

	@Override
	public List<Map<String,Object>> groupBy(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCExpression expression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		
		if(expression.getGroupByFields().size()<=0){
			throw new ServiceException("GroupBy conditions not found in expression");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.setFields(sqlQueryObject,expression,functionField);
		try{
			return _select(jdbcProperties, log, connection, sqlQueryObject, expression);
		}finally{
			org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.removeFields(sqlQueryObject,expression,functionField);
		}
	}
	

	@Override
	public List<Map<String,Object>> groupBy(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCPaginatedExpression paginatedExpression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		
		if(paginatedExpression.getGroupByFields().size()<=0){
			throw new ServiceException("GroupBy conditions not found in expression");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.setFields(sqlQueryObject,paginatedExpression,functionField);
		try{
			return _select(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression);
		}finally{
			org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.removeFields(sqlQueryObject,paginatedExpression,functionField);
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
		List<Object> returnField = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareSelect(jdbcProperties, log, connection, sqlQueryObject, 
        						expression, this.getAccordoServizioParteComuneGruppoFieldConverter(), AccordoServizioParteComuneGruppo.model(), 
        						listaQuery,listaParams);
		
		_join(expression,sqlQueryObject);
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.select(jdbcProperties, log, connection,
        								org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareSqlQueryObjectForSelectDistinct(sqlQueryObject,sqlQueryObjectDistinct), 
        								expression, this.getAccordoServizioParteComuneGruppoFieldConverter(), AccordoServizioParteComuneGruppo.model(),
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
		List<Class<?>> returnClassTypes = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareUnion(jdbcProperties, log, connection, sqlQueryObject, 
        						this.getAccordoServizioParteComuneGruppoFieldConverter(), AccordoServizioParteComuneGruppo.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.union(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getAccordoServizioParteComuneGruppoFieldConverter(), AccordoServizioParteComuneGruppo.model(), 
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
		List<Class<?>> returnClassTypes = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareUnionCount(jdbcProperties, log, connection, sqlQueryObject, 
        						this.getAccordoServizioParteComuneGruppoFieldConverter(), AccordoServizioParteComuneGruppo.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        NonNegativeNumber number = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.unionCount(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getAccordoServizioParteComuneGruppoFieldConverter(), AccordoServizioParteComuneGruppo.model(), 
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
			return new JDBCExpression(this.getAccordoServizioParteComuneGruppoFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}


	@Override
	public JDBCPaginatedExpression newPaginatedExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(this.getAccordoServizioParteComuneGruppoFieldConverter());
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
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAccordoServizioParteComuneGruppo id, AccordoServizioParteComuneGruppo obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,id,null));
	}
	
	@Override
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, AccordoServizioParteComuneGruppo obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,tableId,null));
	}
	private void _mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, AccordoServizioParteComuneGruppo obj, AccordoServizioParteComuneGruppo imgSaved) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		if(imgSaved==null){
			return;
		}
		obj.setId(imgSaved.getId());
		if(obj.getIdGruppo()!=null && 
				imgSaved.getIdGruppo()!=null){
			obj.getIdGruppo().setId(imgSaved.getIdGruppo().getId());
		}
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
	public AccordoServizioParteComuneGruppo get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._get(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId), idMappingResolutionBehaviour);
	}
	
	private AccordoServizioParteComuneGruppo _get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
				
		AccordoServizioParteComuneGruppo accordoServizioParteComuneGruppo = new AccordoServizioParteComuneGruppo();
		

		// Object accordoServizioParteComuneGruppo
		ISQLQueryObject sqlQueryObjectGet_accordoServizioParteComuneGruppo = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_accordoServizioParteComuneGruppo.setANDLogicOperator(true);
		sqlQueryObjectGet_accordoServizioParteComuneGruppo.addFromTable(this.getAccordoServizioParteComuneGruppoFieldConverter().toTable(AccordoServizioParteComuneGruppo.model()));
		sqlQueryObjectGet_accordoServizioParteComuneGruppo.addSelectField("id");
		sqlQueryObjectGet_accordoServizioParteComuneGruppo.addWhereCondition("id=?");

		// Get accordoServizioParteComuneGruppo
		accordoServizioParteComuneGruppo = (AccordoServizioParteComuneGruppo) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_accordoServizioParteComuneGruppo.createSQLQuery(), jdbcProperties.isShowSql(), AccordoServizioParteComuneGruppo.model(), this.getAccordoServizioParteComuneGruppoFetch(),
			new JDBCObject(tableId,Long.class));


		if(idMappingResolutionBehaviour==null ||
			(org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour) || org.openspcoop2.generic_project.beans.IDMappingBehaviour.USE_TABLE_ID.equals(idMappingResolutionBehaviour))
		){
			// Object _accordoServizioParteComuneGruppo_gruppo (recupero id)
			ISQLQueryObject sqlQueryObjectGet_accordoServizioParteComuneGruppo_gruppo_readFkId = sqlQueryObjectGet.newSQLQueryObject();
			sqlQueryObjectGet_accordoServizioParteComuneGruppo_gruppo_readFkId.addFromTable(this.getAccordoServizioParteComuneGruppoFieldConverter().toTable(org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo.model()));
			sqlQueryObjectGet_accordoServizioParteComuneGruppo_gruppo_readFkId.addSelectField("id_gruppo");
			sqlQueryObjectGet_accordoServizioParteComuneGruppo_gruppo_readFkId.addWhereCondition("id=?");
			sqlQueryObjectGet_accordoServizioParteComuneGruppo_gruppo_readFkId.setANDLogicOperator(true);
			Long idFK_accordoServizioParteComuneGruppo_gruppo = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_accordoServizioParteComuneGruppo_gruppo_readFkId.createSQLQuery(), jdbcProperties.isShowSql(),Long.class,
					new JDBCObject(accordoServizioParteComuneGruppo.getId(),Long.class));
			
			org.openspcoop2.core.commons.search.IdGruppo id_accordoServizioParteComuneGruppo_gruppo = null;
			if(idMappingResolutionBehaviour==null || org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour)){
				id_accordoServizioParteComuneGruppo_gruppo = ((JDBCGruppoServiceSearch)(this.getServiceManager(connection, jdbcProperties, log).getGruppoServiceSearch())).findId(idFK_accordoServizioParteComuneGruppo_gruppo, false);
			}else{
				id_accordoServizioParteComuneGruppo_gruppo = new org.openspcoop2.core.commons.search.IdGruppo();
			}
			id_accordoServizioParteComuneGruppo_gruppo.setId(idFK_accordoServizioParteComuneGruppo_gruppo);
			
			Gruppo gruppo = ((IDBGruppoServiceSearch)this.getServiceManager(connection, jdbcProperties, log).getGruppoServiceSearch()).get(id_accordoServizioParteComuneGruppo_gruppo.getId());
			id_accordoServizioParteComuneGruppo_gruppo.setNome(gruppo.getNome());
			
			accordoServizioParteComuneGruppo.setIdGruppo(id_accordoServizioParteComuneGruppo_gruppo);
		}

		if(idMappingResolutionBehaviour==null ||
			(org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour) || org.openspcoop2.generic_project.beans.IDMappingBehaviour.USE_TABLE_ID.equals(idMappingResolutionBehaviour))
		){
			// Object _accordoServizioParteComuneGruppo_accordoServizioParteComune (recupero id)
			ISQLQueryObject sqlQueryObjectGet_accordoServizioParteComuneGruppo_accordoServizioParteComune_readFkId = sqlQueryObjectGet.newSQLQueryObject();
			sqlQueryObjectGet_accordoServizioParteComuneGruppo_accordoServizioParteComune_readFkId.addFromTable(this.getAccordoServizioParteComuneGruppoFieldConverter().toTable(org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo.model()));
			sqlQueryObjectGet_accordoServizioParteComuneGruppo_accordoServizioParteComune_readFkId.addSelectField("id_accordo");
			sqlQueryObjectGet_accordoServizioParteComuneGruppo_accordoServizioParteComune_readFkId.addWhereCondition("id=?");
			sqlQueryObjectGet_accordoServizioParteComuneGruppo_accordoServizioParteComune_readFkId.setANDLogicOperator(true);
			Long idFK_accordoServizioParteComuneGruppo_accordoServizioParteComune = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_accordoServizioParteComuneGruppo_accordoServizioParteComune_readFkId.createSQLQuery(), jdbcProperties.isShowSql(),Long.class,
					new JDBCObject(accordoServizioParteComuneGruppo.getId(),Long.class));
			
			org.openspcoop2.core.commons.search.IdAccordoServizioParteComune id_accordoServizioParteComuneGruppo_accordoServizioParteComune = null;
			if(idMappingResolutionBehaviour==null || org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour)){
				id_accordoServizioParteComuneGruppo_accordoServizioParteComune = ((JDBCAccordoServizioParteComuneServiceSearch)(this.getServiceManager(connection, jdbcProperties, log).getAccordoServizioParteComuneServiceSearch())).findId(idFK_accordoServizioParteComuneGruppo_accordoServizioParteComune, false);
			}else{
				id_accordoServizioParteComuneGruppo_accordoServizioParteComune = new org.openspcoop2.core.commons.search.IdAccordoServizioParteComune();
			}
			id_accordoServizioParteComuneGruppo_accordoServizioParteComune.setId(idFK_accordoServizioParteComuneGruppo_accordoServizioParteComune);
			
			AccordoServizioParteComune aspc = ((IDBAccordoServizioParteComuneServiceSearch)this.getServiceManager(connection, jdbcProperties, log).getAccordoServizioParteComuneServiceSearch()).get(id_accordoServizioParteComuneGruppo_accordoServizioParteComune.getId());
			id_accordoServizioParteComuneGruppo_accordoServizioParteComune.setIdSoggetto(aspc.getIdReferente());
			id_accordoServizioParteComuneGruppo_accordoServizioParteComune.setNome(aspc.getNome());
			id_accordoServizioParteComuneGruppo_accordoServizioParteComune.setServiceBinding(aspc.getServiceBinding());
			id_accordoServizioParteComuneGruppo_accordoServizioParteComune.setVersione(aspc.getVersione());
			
			accordoServizioParteComuneGruppo.setIdAccordoServizioParteComune(id_accordoServizioParteComuneGruppo_accordoServizioParteComune);
		}


        return accordoServizioParteComuneGruppo;  
	
	} 
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._exists(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	private boolean _exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		boolean existsAccordoServizioParteComuneGruppo = false;

		sqlQueryObject = sqlQueryObject.newSQLQueryObject();
		sqlQueryObject.setANDLogicOperator(true);

		sqlQueryObject.addWhereCondition("id=?");


		// Exists accordoServizioParteComuneGruppo
		existsAccordoServizioParteComuneGruppo = jdbcUtilities.exists(sqlQueryObject.createSQLQuery(), jdbcProperties.isShowSql(),
			new JDBCObject(tableId,Long.class));

		
        return existsAccordoServizioParteComuneGruppo;
	
	}
	
	private void _join(IExpression expression, ISQLQueryObject sqlQueryObject) throws NotImplementedException, ServiceException, Exception{
	
		if(expression.inUseModel(AccordoServizioParteComuneGruppo.model().ID_GRUPPO,false)){
			String tableName1 = this.getAccordoServizioParteComuneGruppoFieldConverter().toAliasTable(AccordoServizioParteComuneGruppo.model());
			String tableName2 = this.getAccordoServizioParteComuneGruppoFieldConverter().toAliasTable(AccordoServizioParteComuneGruppo.model().ID_GRUPPO);
			sqlQueryObject.addWhereCondition(tableName1+".id_gruppo="+tableName2+".id");
		}
		if(expression.inUseModel(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE,false)){
			String tableName1 = this.getAccordoServizioParteComuneGruppoFieldConverter().toAliasTable(AccordoServizioParteComuneGruppo.model());
			String tableName2 = this.getAccordoServizioParteComuneGruppoFieldConverter().toAliasTable(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE);
			sqlQueryObject.addWhereCondition(tableName1+".id_accordo="+tableName2+".id");
		}
		
		
        if(expression.inUseModel(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO,false)){
			if(expression.inUseModel(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE,false)==false){
				sqlQueryObject.addFromTable(this.getAccordoServizioParteComuneGruppoFieldConverter().toTable(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE));
			}
		}
        
	}
	
	protected java.util.List<Object> _getRootTablePrimaryKeyValues(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAccordoServizioParteComuneGruppo id) throws NotFoundException, ServiceException, NotImplementedException, Exception{
	    // Identificativi
        java.util.List<Object> rootTableIdValues = new java.util.ArrayList<Object>();
        // TODO: Define the column values used to identify the primary key
		Long longId = this.findIdAccordoServizioParteComuneGruppo(jdbcProperties, log, connection, sqlQueryObject.newSQLQueryObject(), id, true);
		rootTableIdValues.add(longId);
        
        // Delete this line when you have verified the method
		int throwNotImplemented = 1;
		if(throwNotImplemented==1){
		        throw new NotImplementedException("NotImplemented");
		}
		// Delete this line when you have verified the method
        
        return rootTableIdValues;
	}
	
	protected Map<String, List<IField>> _getMapTableToPKColumn() throws NotImplementedException, Exception{
	
		AccordoServizioParteComuneGruppoFieldConverter converter = this.getAccordoServizioParteComuneGruppoFieldConverter();
		Map<String, List<IField>> mapTableToPKColumn = new java.util.HashMap<String, List<IField>>();
		UtilsTemplate<IField> utilities = new UtilsTemplate<IField>();

		// TODO: Define the columns used to identify the primary key
		//		  If a table doesn't have a primary key, don't add it to this map

		// AccordoServizioParteComuneGruppo.model()
		mapTableToPKColumn.put(converter.toTable(AccordoServizioParteComuneGruppo.model()),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AccordoServizioParteComuneGruppo.model()))
			));

		// AccordoServizioParteComuneGruppo.model().ID_GRUPPO
		mapTableToPKColumn.put(converter.toTable(AccordoServizioParteComuneGruppo.model().ID_GRUPPO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AccordoServizioParteComuneGruppo.model().ID_GRUPPO))
			));

		// AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE
		mapTableToPKColumn.put(converter.toTable(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE))
			));

		// AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO
		mapTableToPKColumn.put(converter.toTable(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO))
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
		sqlQueryObject.addSelectField(this.getAccordoServizioParteComuneGruppoFieldConverter().toTable(AccordoServizioParteComuneGruppo.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareFindAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
												this.getAccordoServizioParteComuneGruppoFieldConverter(), AccordoServizioParteComuneGruppo.model());
		
		_join(paginatedExpression,sqlQueryObject);
		
		List<Object> listObjects = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.findAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
																			this.getAccordoServizioParteComuneGruppoFieldConverter(), AccordoServizioParteComuneGruppo.model(), objectIdClass, listaQuery);
		for(Object object: listObjects) {
			list.add((Long)object);
		}

        return list;
		
	}
	
	@Override
	public long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, Exception {
	
		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getAccordoServizioParteComuneGruppoFieldConverter().toTable(AccordoServizioParteComuneGruppo.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareFind(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getAccordoServizioParteComuneGruppoFieldConverter(), AccordoServizioParteComuneGruppo.model());
		
		_join(expression,sqlQueryObject);

		Object res = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.find(jdbcProperties, log, connection, sqlQueryObject, expression,
														this.getAccordoServizioParteComuneGruppoFieldConverter(), AccordoServizioParteComuneGruppo.model(), objectIdClass, listaQuery);
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
	public IdAccordoServizioParteComuneGruppo findId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();

		// Object _accordoServizioParteComuneGruppo
		sqlQueryObjectGet.addFromTable(this.getAccordoServizioParteComuneGruppoFieldConverter().toTable(AccordoServizioParteComuneGruppo.model()));
		sqlQueryObjectGet.addSelectField("id_accordo");
		sqlQueryObjectGet.addSelectField("id_gruppo");
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addWhereCondition("id=?");

		// Recupero _accordoServizioParteComuneGruppo
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_accordoServizioParteComuneGruppo = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(tableId,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_accordoServizioParteComuneGruppo = new ArrayList<Class<?>>();
		listaFieldIdReturnType_accordoServizioParteComuneGruppo.add(Long.class);
		listaFieldIdReturnType_accordoServizioParteComuneGruppo.add(Long.class);
		org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo id_accordoServizioParteComuneGruppo = null;
		List<Object> listaFieldId_accordoServizioParteComuneGruppo = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_accordoServizioParteComuneGruppo, searchParams_accordoServizioParteComuneGruppo);
		if(listaFieldId_accordoServizioParteComuneGruppo==null || listaFieldId_accordoServizioParteComuneGruppo.size()<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		else{
			// set _accordoServizioParteComuneGruppo
			id_accordoServizioParteComuneGruppo = new org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo();
			
			IdAccordoServizioParteComune idApsc = new IdAccordoServizioParteComune();
			idApsc.setId((Long) listaFieldId_accordoServizioParteComuneGruppo.get(0));
			AccordoServizioParteComune aspc = ((IDBAccordoServizioParteComuneServiceSearch)this.getServiceManager(connection, jdbcProperties, log).getAccordoServizioParteComuneServiceSearch()).get(idApsc.getId());
			idApsc.setIdSoggetto(aspc.getIdReferente());
			idApsc.setNome(aspc.getNome());
			idApsc.setServiceBinding(aspc.getServiceBinding());
			idApsc.setVersione(aspc.getVersione());
			id_accordoServizioParteComuneGruppo.setIdAccordoServizioParteComune(idApsc);
			
			IdGruppo idGruppo = new IdGruppo();
			idGruppo.setId((Long) listaFieldId_accordoServizioParteComuneGruppo.get(1));
			Gruppo gruppo = ((IDBGruppoServiceSearch)this.getServiceManager(connection, jdbcProperties, log).getGruppoServiceSearch()).get(idGruppo.getId());
			idGruppo.setNome(gruppo.getNome());
			id_accordoServizioParteComuneGruppo.setIdGruppo(idGruppo);

		}
		
		return id_accordoServizioParteComuneGruppo;
		
	}

	@Override
	public Long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAccordoServizioParteComuneGruppo id, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
	
		return this.findIdAccordoServizioParteComuneGruppo(jdbcProperties,log,connection,sqlQueryObject,id,throwNotFound);
			
	}
	
	@Override
	public List<List<Object>> nativeQuery(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
											String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException,Exception{
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.nativeQuery(jdbcProperties, log, connection, sqlQueryObject,
																							sql,returnClassTypes,param);
														
	}
	
	protected Long findIdAccordoServizioParteComuneGruppo(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAccordoServizioParteComuneGruppo id, boolean throwNotFound) throws NotFoundException, ServiceException, NotImplementedException, Exception {

		if(jdbcProperties==null) {
			throw new ServiceException("Param jdbcProperties is null");
		}
		if(sqlQueryObject==null) {
			throw new ServiceException("Param sqlQueryObject is null");
		}
		if(id==null) {
			throw new ServiceException("Param id is null");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();

		// Object _accordoServizioParteComuneGruppo
		sqlQueryObjectGet.addFromTable(this.getAccordoServizioParteComuneGruppoFieldConverter().toTable(AccordoServizioParteComuneGruppo.model()));
		sqlQueryObjectGet.addSelectField("id");
		// Devono essere mappati nella where condition i metodi dell'oggetto id.getXXX
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.setSelectDistinct(true);
		sqlQueryObjectGet.addWhereCondition("id_accordo=?");
		sqlQueryObjectGet.addWhereCondition("id_gruppo=?");

		long idAccordo = ((IDBAccordoServizioParteComuneServiceSearch)this.getServiceManager(connection, jdbcProperties, log).getAccordoServizioParteComuneServiceSearch()).findTableId(id.getIdAccordoServizioParteComune(), true);
		long idGruppo = ((IDBGruppoServiceSearch)this.getServiceManager(connection, jdbcProperties, log).getGruppoServiceSearch()).findTableId(id.getIdGruppo(), true);
		
		// Recupero _accordoServizioParteComuneGruppo
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_accordoServizioParteComuneGruppo = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(idAccordo,long.class),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(idGruppo,long.class)
		};
		Long id_accordoServizioParteComuneGruppo = null;
		try{
			id_accordoServizioParteComuneGruppo = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
						Long.class, searchParams_accordoServizioParteComuneGruppo);
		}catch(NotFoundException notFound){
			if(throwNotFound){
				throw new NotFoundException(notFound);
			}
		}
		if(id_accordoServizioParteComuneGruppo==null || id_accordoServizioParteComuneGruppo<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		
		return id_accordoServizioParteComuneGruppo;
	}
}
