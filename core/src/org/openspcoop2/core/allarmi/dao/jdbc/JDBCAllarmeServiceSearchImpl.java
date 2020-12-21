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
package org.openspcoop2.core.allarmi.dao.jdbc;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.AllarmeParametro;
import org.openspcoop2.core.allarmi.IdAllarme;
import org.openspcoop2.core.allarmi.dao.jdbc.converter.AllarmeFieldConverter;
import org.openspcoop2.core.allarmi.dao.jdbc.fetch.AllarmeFetch;
import org.openspcoop2.generic_project.beans.CustomField;
import org.openspcoop2.generic_project.beans.FunctionField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.InUse;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.beans.Union;
import org.openspcoop2.generic_project.beans.UnionExpression;
import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceSearchWithId;
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
import org.slf4j.Logger;

/**     
 * JDBCAllarmeServiceSearchImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCAllarmeServiceSearchImpl implements IJDBCServiceSearchWithId<Allarme, IdAllarme, JDBCServiceManager> {

	private AllarmeFieldConverter _allarmeFieldConverter = null;
	public AllarmeFieldConverter getAllarmeFieldConverter() {
		if(this._allarmeFieldConverter==null){
			this._allarmeFieldConverter = new AllarmeFieldConverter(this.jdbcServiceManager.getJdbcProperties().getDatabaseType());
		}		
		return this._allarmeFieldConverter;
	}
	@Override
	public ISQLFieldConverter getFieldConverter() {
		return this.getAllarmeFieldConverter();
	}
	
	private AllarmeFetch allarmeFetch = new AllarmeFetch();
	public AllarmeFetch getAllarmeFetch() {
		return this.allarmeFetch;
	}
	@Override
	public IJDBCFetch getFetch() {
		return getAllarmeFetch();
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
	public IdAllarme convertToId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Allarme allarme) throws NotImplementedException, ServiceException, Exception{
	
		IdAllarme idAllarme = new IdAllarme();
		idAllarme.setNome(allarme.getNome());
		idAllarme.setTipo(allarme.getTipo());
		
		return idAllarme;
	}
	
	@Override
	public Allarme get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAllarme id, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {
		Long id_allarme = ( (id!=null && id.getId()!=null && id.getId()>0) ? id.getId() : this.findIdAllarme(jdbcProperties, log, connection, sqlQueryObject, id, true));
		return this._get(jdbcProperties, log, connection, sqlQueryObject, id_allarme,idMappingResolutionBehaviour);
		
		
	}
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAllarme id) throws MultipleResultException, NotImplementedException, ServiceException,Exception {

		Long id_allarme = this.findIdAllarme(jdbcProperties, log, connection, sqlQueryObject, id, false);
		return id_allarme != null && id_allarme > 0;
		
	}
	
	@Override
	public List<IdAllarme> findAllIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

		List<IdAllarme> list = new ArrayList<IdAllarme>();

		// TODO: implementazione non efficente. 
		// Per ottenere una implementazione efficente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari a create l'ID logico
		// 2. Usare metodo getAllarmeFetch() sul risultato della select per ottenere un oggetto Allarme
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 
		// 3. Usare metodo convertToId per ottenere l'id

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	Allarme allarme = this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour);
			IdAllarme idAllarme = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,allarme);
        	list.add(idAllarme);
        }

        return list;
		
	}
	
	@Override
	public List<Allarme> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

        List<Allarme> list = new ArrayList<Allarme>();
        
        // TODO: implementazione non efficente. 
		// Per ottenere una implementazione efficente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari
		// 2. Usare metodo getAllarmeFetch() sul risultato della select per ottenere un oggetto Allarme
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	list.add(this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour));
        }

        return list;      
		
	}
	
	@Override
	public Allarme find(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) 
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
												this.getAllarmeFieldConverter(), Allarme.model());
		
		sqlQueryObject.addSelectCountField(this.getAllarmeFieldConverter().toTable(Allarme.model())+".id","tot",true);
		
		_join(expression,sqlQueryObject);
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.count(jdbcProperties, log, connection, sqlQueryObject, expression,
																			this.getAllarmeFieldConverter(), Allarme.model(),listaQuery);
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAllarme id) throws NotFoundException, NotImplementedException, ServiceException,Exception {
		
		Long id_allarme = this.findIdAllarme(jdbcProperties, log, connection, sqlQueryObject, id, true);
        return this._inUse(jdbcProperties, log, connection, sqlQueryObject, id_allarme);
		
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
												this.getAllarmeFieldConverter(), field);

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
        						expression, this.getAllarmeFieldConverter(), Allarme.model(), 
        						listaQuery,listaParams);
		
		_join(expression,sqlQueryObject);
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.select(jdbcProperties, log, connection,
        								org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareSqlQueryObjectForSelectDistinct(sqlQueryObject,sqlQueryObjectDistinct), 
        								expression, this.getAllarmeFieldConverter(), Allarme.model(),
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
        						this.getAllarmeFieldConverter(), Allarme.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.union(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getAllarmeFieldConverter(), Allarme.model(), 
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
        						this.getAllarmeFieldConverter(), Allarme.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        NonNegativeNumber number = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.unionCount(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getAllarmeFieldConverter(), Allarme.model(), 
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
			return new JDBCExpression(this.getAllarmeFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}


	@Override
	public JDBCPaginatedExpression newPaginatedExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(this.getAllarmeFieldConverter());
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
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAllarme id, Allarme obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,id,null));
	}
	
	@Override
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, Allarme obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,tableId,null));
	}
	private void _mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Allarme obj, Allarme imgSaved) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		if(imgSaved==null){
			return;
		}
		obj.setId(imgSaved.getId());
		if(obj.getMail()!=null && 
				imgSaved.getMail()!=null){
			obj.getMail().setId(imgSaved.getMail().getId());
		}
		if(obj.getScript()!=null && 
				imgSaved.getScript()!=null){
			obj.getScript().setId(imgSaved.getScript().getId());
		}
		if(obj.getFiltro()!=null && 
				imgSaved.getFiltro()!=null){
			obj.getFiltro().setId(imgSaved.getFiltro().getId());
		}
		if(obj.getGroupBy()!=null && 
				imgSaved.getGroupBy()!=null){
			obj.getGroupBy().setId(imgSaved.getGroupBy().getId());
		}
		if(obj.getAllarmeParametroList()!=null){
			List<org.openspcoop2.core.allarmi.AllarmeParametro> listObj_ = obj.getAllarmeParametroList();
			for(org.openspcoop2.core.allarmi.AllarmeParametro itemObj_ : listObj_){
				org.openspcoop2.core.allarmi.AllarmeParametro itemAlreadySaved_ = null;
				if(imgSaved.getAllarmeParametroList()!=null){
					List<org.openspcoop2.core.allarmi.AllarmeParametro> listImgSaved_ = imgSaved.getAllarmeParametroList();
					for(org.openspcoop2.core.allarmi.AllarmeParametro itemImgSaved_ : listImgSaved_){
						boolean objEqualsToImgSaved_ = false;
						objEqualsToImgSaved_ = org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getIdParametro(),itemImgSaved_.getIdParametro());
						if(objEqualsToImgSaved_){
							itemAlreadySaved_=itemImgSaved_;
							break;
						}
					}
				}
				if(itemAlreadySaved_!=null){
					itemObj_.setId(itemAlreadySaved_.getId());
				}
			}
		}
              
	}
	
	@Override
	public Allarme get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._get(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId), idMappingResolutionBehaviour);
	}
	
	private Allarme _get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
				
		Allarme allarme = new Allarme();
		

		// Object allarme
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addFromTable(this.getAllarmeFieldConverter().toTable(Allarme.model()));
		sqlQueryObjectGet.addSelectField("id");
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().NOME,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().TIPO,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().TIPO_ALLARME,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().MAIL.ACK_MODE,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().MAIL.INVIA_WARNING,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().MAIL.INVIA_ALERT,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().MAIL.DESTINATARI,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().MAIL.SUBJECT,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().MAIL.BODY,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().SCRIPT.ACK_MODE,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().SCRIPT.INVOCA_WARNING,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().SCRIPT.INVOCA_ALERT,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().SCRIPT.COMMAND,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().SCRIPT.ARGS,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().STATO_PRECEDENTE,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().STATO,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().DETTAGLIO_STATO,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().LASTTIMESTAMP_CREATE,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().LASTTIMESTAMP_UPDATE,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().ENABLED,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().ACKNOWLEDGED,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().TIPO_PERIODO,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().PERIODO,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.ENABLED,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.PROTOCOLLO,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.RUOLO_PORTA,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.NOME_PORTA,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.TIPO_FRUITORE,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.NOME_FRUITORE,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.RUOLO_FRUITORE,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.SERVIZIO_APPLICATIVO_FRUITORE,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.TIPO_EROGATORE,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.NOME_EROGATORE,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.RUOLO_EROGATORE,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.TAG,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.TIPO_SERVIZIO,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.NOME_SERVIZIO,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.VERSIONE_SERVIZIO,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.AZIONE,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.ENABLED,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.RUOLO_PORTA,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.PROTOCOLLO,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.FRUITORE,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.SERVIZIO_APPLICATIVO_FRUITORE,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.IDENTIFICATIVO_AUTENTICATO,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.TOKEN,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.EROGATORE,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.SERVIZIO,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.AZIONE,true));
		sqlQueryObjectGet.addWhereCondition("id=?");

		// Get allarme
		allarme = (Allarme) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(), Allarme.model(), this.getAllarmeFetch(),
			new JDBCObject(tableId,Long.class));



		// Object allarme_allarmeParametro
		ISQLQueryObject sqlQueryObjectGet_allarmeParametro = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_allarmeParametro.setANDLogicOperator(true);
		sqlQueryObjectGet_allarmeParametro.addFromTable(this.getAllarmeFieldConverter().toTable(Allarme.model().ALLARME_PARAMETRO));
		sqlQueryObjectGet_allarmeParametro.addSelectField("chk_param_id");
		sqlQueryObjectGet_allarmeParametro.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().ALLARME_PARAMETRO.ID_PARAMETRO,true));
		sqlQueryObjectGet_allarmeParametro.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().ALLARME_PARAMETRO.VALORE,true));
		sqlQueryObjectGet_allarmeParametro.addWhereCondition("id_allarme=?");

		// Get allarme_allarmeParametro
		java.util.List<Object> allarme_allarmeParametro_list = (java.util.List<Object>) jdbcUtilities.executeQuery(sqlQueryObjectGet_allarmeParametro.createSQLQuery(), jdbcProperties.isShowSql(), Allarme.model().ALLARME_PARAMETRO, this.getAllarmeFetch(),
			new JDBCObject(allarme.getId(),Long.class));

		if(allarme_allarmeParametro_list != null) {
			for (Object allarme_allarmeParametro_object: allarme_allarmeParametro_list) {
				AllarmeParametro allarme_allarmeParametro = (AllarmeParametro) allarme_allarmeParametro_object;


				allarme.addAllarmeParametro(allarme_allarmeParametro);
			}
		}

		
        return allarme;  
	
	} 
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._exists(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	private boolean _exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		boolean existsAllarme = false;

		sqlQueryObject = sqlQueryObject.newSQLQueryObject();
		sqlQueryObject.setANDLogicOperator(true);

		sqlQueryObject.addFromTable(this.getAllarmeFieldConverter().toTable(Allarme.model()));
		sqlQueryObject.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().NOME,true));
		sqlQueryObject.addWhereCondition("id=?");


		// Exists allarme
		existsAllarme = jdbcUtilities.exists(sqlQueryObject.createSQLQuery(), jdbcProperties.isShowSql(),
			new JDBCObject(tableId,Long.class));

		
        return existsAllarme;
	
	}
	
	private void _join(IExpression expression, ISQLQueryObject sqlQueryObject) throws NotImplementedException, ServiceException, Exception{
	
		if(expression.inUseModel(Allarme.model().ALLARME_PARAMETRO,false)){
			String tableName1 = this.getAllarmeFieldConverter().toTable(Allarme.model().ALLARME_PARAMETRO);
			String tableName2 = this.getAllarmeFieldConverter().toTable(Allarme.model());
			sqlQueryObject.addWhereCondition(tableName1+".id_allarme="+tableName2+".id");
		}
        
	}
	
	protected java.util.List<Object> _getRootTablePrimaryKeyValues(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAllarme id) throws NotFoundException, ServiceException, NotImplementedException, Exception{
	    // Identificativi
        java.util.List<Object> rootTableIdValues = new java.util.ArrayList<Object>();
        Long longId = this.findIdAllarme(jdbcProperties, log, connection, sqlQueryObject.newSQLQueryObject(), id, true);
		rootTableIdValues.add(longId);
        
        return rootTableIdValues;
	}
	
	protected Map<String, List<IField>> _getMapTableToPKColumn() throws NotImplementedException, Exception{
	
		AllarmeFieldConverter converter = this.getAllarmeFieldConverter();
		Map<String, List<IField>> mapTableToPKColumn = new java.util.Hashtable<String, List<IField>>();
		UtilsTemplate<IField> utilities = new UtilsTemplate<IField>();

		// Allarme.model()
		mapTableToPKColumn.put(converter.toTable(Allarme.model()),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Allarme.model()))
			));

		// Allarme.model().MAIL
		mapTableToPKColumn.put(converter.toTable(Allarme.model().MAIL),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Allarme.model().MAIL))
			));

		// Allarme.model().SCRIPT
		mapTableToPKColumn.put(converter.toTable(Allarme.model().SCRIPT),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Allarme.model().SCRIPT))
			));

		// Allarme.model().FILTRO
		mapTableToPKColumn.put(converter.toTable(Allarme.model().FILTRO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Allarme.model().FILTRO))
			));

		// Allarme.model().GROUP_BY
		mapTableToPKColumn.put(converter.toTable(Allarme.model().GROUP_BY),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Allarme.model().GROUP_BY))
			));

		// Allarme.model().ALLARME_PARAMETRO
		mapTableToPKColumn.put(converter.toTable(Allarme.model().ALLARME_PARAMETRO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Allarme.model().ALLARME_PARAMETRO))
			));
        
        return mapTableToPKColumn;		
	}
	
	@Override
	public List<Long> findAllTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression paginatedExpression) throws ServiceException, NotImplementedException, Exception {
		
		List<Long> list = new ArrayList<Long>();

		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getAllarmeFieldConverter().toTable(Allarme.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFindAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
												this.getAllarmeFieldConverter(), Allarme.model());
		
		_join(paginatedExpression,sqlQueryObject);
		
		List<Object> listObjects = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.findAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
																			this.getAllarmeFieldConverter(), Allarme.model(), objectIdClass, listaQuery);
		for(Object object: listObjects) {
			list.add((Long)object);
		}

        return list;
		
	}
	
	@Override
	public long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, Exception {
	
		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getAllarmeFieldConverter().toTable(Allarme.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFind(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getAllarmeFieldConverter(), Allarme.model());
		
		_join(expression,sqlQueryObject);

		Object res = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.find(jdbcProperties, log, connection, sqlQueryObject, expression,
														this.getAllarmeFieldConverter(), Allarme.model(), objectIdClass, listaQuery);
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
	public IdAllarme findId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
		
		if(tableId<=0) {
			throw new ServiceException("Id undefined");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();              

		// Object _allarme
		sqlQueryObjectGet.addFromTable(this.getAllarmeFieldConverter().toTable(Allarme.model()));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().NOME,true));
		sqlQueryObjectGet.addSelectField(this.getAllarmeFieldConverter().toColumn(Allarme.model().TIPO,true));
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addWhereCondition("id=?");

		// Recupero _allarme
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_allarme = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(tableId,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_allarme = new ArrayList<Class<?>>();
		listaFieldIdReturnType_allarme.add(String.class);
		listaFieldIdReturnType_allarme.add(String.class);
		org.openspcoop2.core.allarmi.IdAllarme id_allarme = null;
		List<Object> listaFieldId_allarme = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_allarme, searchParams_allarme);
		if(listaFieldId_allarme==null || listaFieldId_allarme.size()<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		else{
			// set _allarme
			id_allarme = new org.openspcoop2.core.allarmi.IdAllarme();
			id_allarme.setNome((String)listaFieldId_allarme.get(0));
			id_allarme.setTipo((String)listaFieldId_allarme.get(1));
		}
		
		return id_allarme;
		
	}

	@Override
	public Long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAllarme id, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
	
		return this.findIdAllarme(jdbcProperties,log,connection,sqlQueryObject,id,throwNotFound);
			
	}
	
	@Override
	public List<List<Object>> nativeQuery(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
											String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException,Exception{
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.nativeQuery(jdbcProperties, log, connection, sqlQueryObject,
																							sql,returnClassTypes,param);
														
	}
	
	protected Long findIdAllarme(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAllarme id, boolean throwNotFound) throws NotFoundException, ServiceException, NotImplementedException, Exception {

		if(id==null || id.getNome()==null) {
			throw new ServiceException("Id undefined");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();              

		// Object _allarme
		sqlQueryObjectGet.addFromTable(this.getAllarmeFieldConverter().toTable(Allarme.model()));
		sqlQueryObjectGet.addSelectField("id");
		// Devono essere mappati nella where condition i metodi dell'oggetto id.getXXX
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.setSelectDistinct(true);
		sqlQueryObjectGet.addWhereCondition(this.getAllarmeFieldConverter().toColumn(Allarme.model().NOME,true)+"=?");
		
		// Recupero _allarme
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_allarme = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id.getNome(),String.class)
		};
		Long id_allarme = null;
		try{
			id_allarme = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
						Long.class, searchParams_allarme);
		}catch(NotFoundException notFound){
			if(throwNotFound){
				throw new NotFoundException(notFound);
			}
		}
		if(id_allarme==null || id_allarme<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		
		return id_allarme;
	}
}
