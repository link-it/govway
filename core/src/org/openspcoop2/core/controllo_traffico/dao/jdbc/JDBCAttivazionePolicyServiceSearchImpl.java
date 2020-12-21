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
package org.openspcoop2.core.controllo_traffico.dao.jdbc;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.IdActivePolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.controllo_traffico.dao.jdbc.converter.AttivazionePolicyFieldConverter;
import org.openspcoop2.core.controllo_traffico.dao.jdbc.fetch.AttivazionePolicyFetch;
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
 * JDBCAttivazionePolicyServiceSearchImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCAttivazionePolicyServiceSearchImpl implements IJDBCServiceSearchWithId<AttivazionePolicy, IdActivePolicy, JDBCServiceManager> {

	private AttivazionePolicyFieldConverter _attivazionePolicyFieldConverter = null;
	public AttivazionePolicyFieldConverter getAttivazionePolicyFieldConverter() {
		if(this._attivazionePolicyFieldConverter==null){
			this._attivazionePolicyFieldConverter = new AttivazionePolicyFieldConverter(this.jdbcServiceManager.getJdbcProperties().getDatabaseType());
		}		
		return this._attivazionePolicyFieldConverter;
	}
	@Override
	public ISQLFieldConverter getFieldConverter() {
		return this.getAttivazionePolicyFieldConverter();
	}
	
	private AttivazionePolicyFetch attivazionePolicyFetch = new AttivazionePolicyFetch();
	public AttivazionePolicyFetch getAttivazionePolicyFetch() {
		return this.attivazionePolicyFetch;
	}
	@Override
	public IJDBCFetch getFetch() {
		return getAttivazionePolicyFetch();
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
	public IdActivePolicy convertToId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, AttivazionePolicy attivazionePolicy) throws NotImplementedException, ServiceException, Exception{
	
		IdActivePolicy idAttivazionePolicy = new IdActivePolicy();
		idAttivazionePolicy.setNome(attivazionePolicy.getIdActivePolicy());
		
		// opzionali
		idAttivazionePolicy.setIdPolicy(attivazionePolicy.getIdPolicy());
		idAttivazionePolicy.setEnabled(attivazionePolicy.isEnabled());
		idAttivazionePolicy.setUpdateTime(attivazionePolicy.getUpdateTime());
		idAttivazionePolicy.setPosizione(attivazionePolicy.getPosizione());
		idAttivazionePolicy.setContinuaValutazione(attivazionePolicy.isContinuaValutazione());
		if(attivazionePolicy.getFiltro()!=null) {
			idAttivazionePolicy.setFiltroRuoloPorta(attivazionePolicy.getFiltro().getRuoloPorta());
			idAttivazionePolicy.setFiltroNomePorta(attivazionePolicy.getFiltro().getNomePorta());
		}
		
		return idAttivazionePolicy;
		
	}
	
	@Override
	public AttivazionePolicy get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdActivePolicy id, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {
		Long id_attivazionePolicy = ( (id!=null && id.getId()!=null && id.getId()>0) ? id.getId() : this.findIdAttivazionePolicy(jdbcProperties, log, connection, sqlQueryObject, id, true));
		return this._get(jdbcProperties, log, connection, sqlQueryObject, id_attivazionePolicy,idMappingResolutionBehaviour);
		
		
	}
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdActivePolicy id) throws MultipleResultException, NotImplementedException, ServiceException,Exception {

		Long id_attivazionePolicy = this.findIdAttivazionePolicy(jdbcProperties, log, connection, sqlQueryObject, id, false);
		return id_attivazionePolicy != null && id_attivazionePolicy > 0;
		
	}
	
	@Override
	public List<IdActivePolicy> findAllIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

		List<IdActivePolicy> list = new ArrayList<IdActivePolicy>();

		// TODO: implementazione non efficente. 
		// Per ottenere una implementazione efficente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari a create l'ID logico
		// 2. Usare metodo getAttivazionePolicyFetch() sul risultato della select per ottenere un oggetto AttivazionePolicy
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 
		// 3. Usare metodo convertToId per ottenere l'id

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	AttivazionePolicy attivazionePolicy = this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour);
			IdActivePolicy idAttivazionePolicy = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,attivazionePolicy);
        	list.add(idAttivazionePolicy);
        }

        return list;
		
	}
	
	@Override
	public List<AttivazionePolicy> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

        List<AttivazionePolicy> list = new ArrayList<AttivazionePolicy>();
        
        // TODO: implementazione non efficente. 
		// Per ottenere una implementazione efficente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari
		// 2. Usare metodo getAttivazionePolicyFetch() sul risultato della select per ottenere un oggetto AttivazionePolicy
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	list.add(this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour));
        }

        return list;      
		
	}
	
	@Override
	public AttivazionePolicy find(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) 
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
												this.getAttivazionePolicyFieldConverter(), AttivazionePolicy.model());
		
		sqlQueryObject.addSelectCountField(this.getAttivazionePolicyFieldConverter().toTable(AttivazionePolicy.model())+".id","tot",true);
		
		_join(expression,sqlQueryObject);
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.count(jdbcProperties, log, connection, sqlQueryObject, expression,
																			this.getAttivazionePolicyFieldConverter(), AttivazionePolicy.model(),listaQuery);
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdActivePolicy id) throws NotFoundException, NotImplementedException, ServiceException,Exception {
		
		Long id_attivazionePolicy = this.findIdAttivazionePolicy(jdbcProperties, log, connection, sqlQueryObject, id, true);
        return this._inUse(jdbcProperties, log, connection, sqlQueryObject, id_attivazionePolicy);
		
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
												this.getAttivazionePolicyFieldConverter(), field);

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
        						expression, this.getAttivazionePolicyFieldConverter(), AttivazionePolicy.model(), 
        						listaQuery,listaParams);
		
		_join(expression,sqlQueryObject);
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.select(jdbcProperties, log, connection,
        								org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareSqlQueryObjectForSelectDistinct(sqlQueryObject,sqlQueryObjectDistinct), 
        								expression, this.getAttivazionePolicyFieldConverter(), AttivazionePolicy.model(),
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
        						this.getAttivazionePolicyFieldConverter(), AttivazionePolicy.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.union(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getAttivazionePolicyFieldConverter(), AttivazionePolicy.model(), 
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
        						this.getAttivazionePolicyFieldConverter(), AttivazionePolicy.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        NonNegativeNumber number = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.unionCount(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getAttivazionePolicyFieldConverter(), AttivazionePolicy.model(), 
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
			return new JDBCExpression(this.getAttivazionePolicyFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}


	@Override
	public JDBCPaginatedExpression newPaginatedExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(this.getAttivazionePolicyFieldConverter());
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
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdActivePolicy id, AttivazionePolicy obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,id,null));
	}
	
	@Override
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, AttivazionePolicy obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,tableId,null));
	}
	private void _mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, AttivazionePolicy obj, AttivazionePolicy imgSaved) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		if(imgSaved==null){
			return;
		}
		obj.setId(imgSaved.getId());
		if(obj.getFiltro()!=null && 
				imgSaved.getFiltro()!=null){
			obj.getFiltro().setId(imgSaved.getFiltro().getId());
		}
		if(obj.getGroupBy()!=null && 
				imgSaved.getGroupBy()!=null){
			obj.getGroupBy().setId(imgSaved.getGroupBy().getId());
		}

	}
	
	@Override
	public AttivazionePolicy get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._get(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId), idMappingResolutionBehaviour);
	}
	
	private AttivazionePolicy _get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
				
		AttivazionePolicy attivazionePolicy = new AttivazionePolicy();
		

		// Object attivazionePolicy
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addFromTable(this.getAttivazionePolicyFieldConverter().toTable(AttivazionePolicy.model()));
		sqlQueryObjectGet.addSelectField("id");
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().ID_ACTIVE_POLICY,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().ALIAS,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().UPDATE_TIME,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().POSIZIONE,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().CONTINUA_VALUTAZIONE,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().ID_POLICY,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().ENABLED,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().WARNING_ONLY,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().RIDEFINISCI,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().VALORE,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.ENABLED,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.PROTOCOLLO,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.RUOLO_PORTA,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.NOME_PORTA,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.TIPO_FRUITORE,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.NOME_FRUITORE,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.RUOLO_FRUITORE,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.SERVIZIO_APPLICATIVO_FRUITORE,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.TIPO_EROGATORE,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.NOME_EROGATORE,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.RUOLO_EROGATORE,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.SERVIZIO_APPLICATIVO_EROGATORE,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.TAG,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.TIPO_SERVIZIO,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.NOME_SERVIZIO,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.VERSIONE_SERVIZIO,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.AZIONE,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_ENABLED,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_TIPO,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_NOME,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_VALORE,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.ENABLED,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.RUOLO_PORTA,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.PROTOCOLLO,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.FRUITORE,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.SERVIZIO_APPLICATIVO_FRUITORE,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.IDENTIFICATIVO_AUTENTICATO,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.TOKEN,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.EROGATORE,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.SERVIZIO_APPLICATIVO_EROGATORE,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.SERVIZIO,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.AZIONE,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_ENABLED,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_TIPO,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_NOME,true));
		sqlQueryObjectGet.addWhereCondition("id=?");

		// Get attivazionePolicy
		attivazionePolicy = (AttivazionePolicy) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(), AttivazionePolicy.model(), this.getAttivazionePolicyFetch(),
			new JDBCObject(tableId,Long.class));



		
        return attivazionePolicy;  
	
	} 
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._exists(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	private boolean _exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		boolean existsAttivazionePolicy = false;

		sqlQueryObject = sqlQueryObject.newSQLQueryObject();
		sqlQueryObject.setANDLogicOperator(true);

		sqlQueryObject.addFromTable(this.getAttivazionePolicyFieldConverter().toTable(AttivazionePolicy.model()));
		sqlQueryObject.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().ID_ACTIVE_POLICY,true));
		sqlQueryObject.addWhereCondition("id=?");


		// Exists attivazionePolicy
		existsAttivazionePolicy = jdbcUtilities.exists(sqlQueryObject.createSQLQuery(), jdbcProperties.isShowSql(),
			new JDBCObject(tableId,Long.class));

		
        return existsAttivazionePolicy;
	
	}
	
	private void _join(IExpression expression, ISQLQueryObject sqlQueryObject) throws NotImplementedException, ServiceException, Exception{
	
        
	}
	
	protected java.util.List<Object> _getRootTablePrimaryKeyValues(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdActivePolicy id) throws NotFoundException, ServiceException, NotImplementedException, Exception{
	    // Identificativi
        java.util.List<Object> rootTableIdValues = new java.util.ArrayList<Object>();
        Long longId = this.findIdAttivazionePolicy(jdbcProperties, log, connection, sqlQueryObject.newSQLQueryObject(), id, true);
		rootTableIdValues.add(longId);
        
        return rootTableIdValues;
	}
	
	protected Map<String, List<IField>> _getMapTableToPKColumn() throws NotImplementedException, Exception{
	
		AttivazionePolicyFieldConverter converter = this.getAttivazionePolicyFieldConverter();
		Map<String, List<IField>> mapTableToPKColumn = new java.util.Hashtable<String, List<IField>>();
		UtilsTemplate<IField> utilities = new UtilsTemplate<IField>();

		// AttivazionePolicy.model()
		mapTableToPKColumn.put(converter.toTable(AttivazionePolicy.model()),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AttivazionePolicy.model()))
			));
        
        return mapTableToPKColumn;		
	}
	
	@Override
	public List<Long> findAllTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression paginatedExpression) throws ServiceException, NotImplementedException, Exception {
		
		List<Long> list = new ArrayList<Long>();

		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getAttivazionePolicyFieldConverter().toTable(AttivazionePolicy.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFindAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
												this.getAttivazionePolicyFieldConverter(), AttivazionePolicy.model());
		
		_join(paginatedExpression,sqlQueryObject);
		
		List<Object> listObjects = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.findAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
																			this.getAttivazionePolicyFieldConverter(), AttivazionePolicy.model(), objectIdClass, listaQuery);
		for(Object object: listObjects) {
			list.add((Long)object);
		}

        return list;
		
	}
	
	@Override
	public long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, Exception {
	
		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getAttivazionePolicyFieldConverter().toTable(AttivazionePolicy.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFind(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getAttivazionePolicyFieldConverter(), AttivazionePolicy.model());
		
		_join(expression,sqlQueryObject);

		Object res = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.find(jdbcProperties, log, connection, sqlQueryObject, expression,
														this.getAttivazionePolicyFieldConverter(), AttivazionePolicy.model(), objectIdClass, listaQuery);
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
	public IdActivePolicy findId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();

		// Object _attivazionePolicy
		sqlQueryObjectGet.addFromTable(this.getAttivazionePolicyFieldConverter().toTable(AttivazionePolicy.model()));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().ID_ACTIVE_POLICY,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().ID_POLICY,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().ENABLED,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().UPDATE_TIME,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().POSIZIONE,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().CONTINUA_VALUTAZIONE,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.RUOLO_PORTA,true));
		sqlQueryObjectGet.addSelectField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.NOME_PORTA,true));
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addWhereCondition("id=?");

		// Recupero _attivazionePolicy
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_attivazionePolicy = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(tableId,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_attivazionePolicy = new ArrayList<Class<?>>();
		listaFieldIdReturnType_attivazionePolicy.add(AttivazionePolicy.model().ID_ACTIVE_POLICY.getFieldType());
		listaFieldIdReturnType_attivazionePolicy.add(AttivazionePolicy.model().ID_POLICY.getFieldType());
		listaFieldIdReturnType_attivazionePolicy.add(AttivazionePolicy.model().ENABLED.getFieldType());
		listaFieldIdReturnType_attivazionePolicy.add(AttivazionePolicy.model().UPDATE_TIME.getFieldType());
		listaFieldIdReturnType_attivazionePolicy.add(AttivazionePolicy.model().POSIZIONE.getFieldType());
		listaFieldIdReturnType_attivazionePolicy.add(AttivazionePolicy.model().CONTINUA_VALUTAZIONE.getFieldType());
		org.openspcoop2.core.controllo_traffico.IdActivePolicy id_attivazionePolicy = null;
		List<Object> listaFieldId_attivazionePolicy = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_attivazionePolicy, searchParams_attivazionePolicy);
		if(listaFieldId_attivazionePolicy==null || listaFieldId_attivazionePolicy.size()<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		else{
			// set _attivazionePolicy
			id_attivazionePolicy = new org.openspcoop2.core.controllo_traffico.IdActivePolicy();
			id_attivazionePolicy.setNome((String)listaFieldId_attivazionePolicy.get(0));
			id_attivazionePolicy.setIdPolicy((String)listaFieldId_attivazionePolicy.get(1));
			id_attivazionePolicy.setEnabled((Boolean)listaFieldId_attivazionePolicy.get(2));
			id_attivazionePolicy.setUpdateTime((Date)listaFieldId_attivazionePolicy.get(3));
			Object posizione = listaFieldId_attivazionePolicy.get(4);
			if(posizione instanceof Integer) {
				id_attivazionePolicy.setPosizione((Integer)posizione);
			}
			id_attivazionePolicy.setContinuaValutazione((Boolean)listaFieldId_attivazionePolicy.get(5));
			Object ruoloPorta = listaFieldId_attivazionePolicy.get(6);
			if(ruoloPorta!=null) {
				if(ruoloPorta instanceof RuoloPolicy) {
					id_attivazionePolicy.setFiltroRuoloPorta((RuoloPolicy)ruoloPorta);
				}
				else if(ruoloPorta instanceof String) {
					id_attivazionePolicy.setFiltroRuoloPorta(RuoloPolicy.toEnumConstant((String)ruoloPorta));
				}
			}
			Object nomePorta = listaFieldId_attivazionePolicy.get(7);
			if(nomePorta!=null) {
				if(nomePorta instanceof String) {
					id_attivazionePolicy.setFiltroNomePorta((String)nomePorta);
				}
			}
		}
		
		return id_attivazionePolicy;
		
	}

	@Override
	public Long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdActivePolicy id, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
	
		return this.findIdAttivazionePolicy(jdbcProperties,log,connection,sqlQueryObject,id,throwNotFound);
			
	}
	
	@Override
	public List<List<Object>> nativeQuery(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
											String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException,Exception{
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.nativeQuery(jdbcProperties, log, connection, sqlQueryObject,
																							sql,returnClassTypes,param);
														
	}
	
	protected Long findIdAttivazionePolicy(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdActivePolicy id, boolean throwNotFound) throws NotFoundException, ServiceException, NotImplementedException, Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();

		// Object _attivazionePolicy
		sqlQueryObjectGet.addFromTable(this.getAttivazionePolicyFieldConverter().toTable(AttivazionePolicy.model()));
		sqlQueryObjectGet.addSelectField("id");
		// Devono essere mappati nella where condition i metodi dell'oggetto id.getXXX
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.setSelectDistinct(true);
		sqlQueryObjectGet.addWhereCondition(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().ID_ACTIVE_POLICY,true)+"=?");

		// Recupero _attivazionePolicy
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_attivazionePolicy = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id.getNome(),String.class)
		};
		Long id_attivazionePolicy = null;
		try{
			id_attivazionePolicy = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
						Long.class, searchParams_attivazionePolicy);
		}catch(NotFoundException notFound){
			if(throwNotFound){
				throw new NotFoundException(notFound);
			}
		}
		if(id_attivazionePolicy==null || id_attivazionePolicy<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		
		return id_attivazionePolicy;
	}
}
