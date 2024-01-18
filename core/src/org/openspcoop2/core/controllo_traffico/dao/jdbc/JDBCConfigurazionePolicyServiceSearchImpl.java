/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.IdPolicy;
import org.openspcoop2.core.controllo_traffico.dao.jdbc.converter.ConfigurazionePolicyFieldConverter;
import org.openspcoop2.core.controllo_traffico.dao.jdbc.fetch.ConfigurazionePolicyFetch;

/**     
 * JDBCConfigurazionePolicyServiceSearchImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCConfigurazionePolicyServiceSearchImpl implements IJDBCServiceSearchWithId<ConfigurazionePolicy, IdPolicy, JDBCServiceManager> {

	private ConfigurazionePolicyFieldConverter _configurazionePolicyFieldConverter = null;
	public ConfigurazionePolicyFieldConverter getConfigurazionePolicyFieldConverter() {
		if(this._configurazionePolicyFieldConverter==null){
			this._configurazionePolicyFieldConverter = new ConfigurazionePolicyFieldConverter(this.jdbcServiceManager.getJdbcProperties().getDatabaseType());
		}		
		return this._configurazionePolicyFieldConverter;
	}
	@Override
	public ISQLFieldConverter getFieldConverter() {
		return this.getConfigurazionePolicyFieldConverter();
	}
	
	private ConfigurazionePolicyFetch configurazionePolicyFetch = new ConfigurazionePolicyFetch();
	public ConfigurazionePolicyFetch getConfigurazionePolicyFetch() {
		return this.configurazionePolicyFetch;
	}
	@Override
	public IJDBCFetch getFetch() {
		return getConfigurazionePolicyFetch();
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
	public IdPolicy convertToId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazionePolicy configurazionePolicy) throws NotImplementedException, ServiceException, Exception{
	
		IdPolicy idConfigurazionePolicy = new IdPolicy();
		idConfigurazionePolicy.setNome(configurazionePolicy.getIdPolicy());
		return idConfigurazionePolicy;
	}
	
	@Override
	public ConfigurazionePolicy get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPolicy id, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {
		Long id_configurazionePolicy = ( (id!=null && id.getId()!=null && id.getId()>0) ? id.getId() : this.findIdConfigurazionePolicy(jdbcProperties, log, connection, sqlQueryObject, id, true));
		return this.getEngine(jdbcProperties, log, connection, sqlQueryObject, id_configurazionePolicy,idMappingResolutionBehaviour);
		
		
	}
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPolicy id) throws MultipleResultException, NotImplementedException, ServiceException,Exception {

		Long id_configurazionePolicy = this.findIdConfigurazionePolicy(jdbcProperties, log, connection, sqlQueryObject, id, false);
		return id_configurazionePolicy != null && id_configurazionePolicy > 0;
		
	}
	
	@Override
	public List<IdPolicy> findAllIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

		List<IdPolicy> list = new ArrayList<IdPolicy>();

		// TODO: implementazione non efficiente. 
		// Per ottenere una implementazione efficiente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari a create l'ID logico
		// 2. Usare metodo getConfigurazionePolicyFetch() sul risultato della select per ottenere un oggetto ConfigurazionePolicy
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 
		// 3. Usare metodo convertToId per ottenere l'id

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	ConfigurazionePolicy configurazionePolicy = this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour);
			IdPolicy idConfigurazionePolicy = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,configurazionePolicy);
        	list.add(idConfigurazionePolicy);
        }

        return list;
		
	}
	
	@Override
	public List<ConfigurazionePolicy> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

        List<ConfigurazionePolicy> list = new ArrayList<ConfigurazionePolicy>();
        
        // TODO: implementazione non efficiente. 
		// Per ottenere una implementazione efficiente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari
		// 2. Usare metodo getConfigurazionePolicyFetch() sul risultato della select per ottenere un oggetto ConfigurazionePolicy
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	list.add(this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour));
        }

        return list;      
		
	}
	
	@Override
	public ConfigurazionePolicy find(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) 
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
												this.getConfigurazionePolicyFieldConverter(), ConfigurazionePolicy.model());
		
		sqlQueryObject.addSelectCountField(this.getConfigurazionePolicyFieldConverter().toTable(ConfigurazionePolicy.model())+".id","tot",true);
		
		joinEngine(expression,sqlQueryObject);
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.count(jdbcProperties, log, connection, sqlQueryObject, expression,
																			this.getConfigurazionePolicyFieldConverter(), ConfigurazionePolicy.model(),listaQuery);
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPolicy id) throws NotFoundException, NotImplementedException, ServiceException,Exception {
		
		Long id_configurazionePolicy = this.findIdConfigurazionePolicy(jdbcProperties, log, connection, sqlQueryObject, id, true);
        return this.inUseEngine(jdbcProperties, log, connection, sqlQueryObject, id_configurazionePolicy);
		
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
												this.getConfigurazionePolicyFieldConverter(), field);

			return selectEngine(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression, sqlQueryObjectDistinct);
			
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
			List<Map<String,Object>> list = selectEngine(jdbcProperties, log, connection, sqlQueryObject, expression);
			return list.get(0);
		}finally{
			org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.removeFields(sqlQueryObject,expression,functionField);
		}
	}

	@Override
	public List<Map<String,Object>> groupBy(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCExpression expression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		
		if(expression.getGroupByFields().isEmpty()){
			throw new ServiceException("GroupBy conditions not found in expression");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.setFields(sqlQueryObject,expression,functionField);
		try{
			return selectEngine(jdbcProperties, log, connection, sqlQueryObject, expression);
		}finally{
			org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.removeFields(sqlQueryObject,expression,functionField);
		}
	}
	

	@Override
	public List<Map<String,Object>> groupBy(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCPaginatedExpression paginatedExpression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		
		if(paginatedExpression.getGroupByFields().isEmpty()){
			throw new ServiceException("GroupBy conditions not found in expression");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.setFields(sqlQueryObject,paginatedExpression,functionField);
		try{
			return selectEngine(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression);
		}finally{
			org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.removeFields(sqlQueryObject,paginatedExpression,functionField);
		}
	}
	
	protected List<Map<String,Object>> selectEngine(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
												IExpression expression) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		return selectEngine(jdbcProperties, log, connection, sqlQueryObject, expression, null);
	}
	protected List<Map<String,Object>> selectEngine(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
												IExpression expression, ISQLQueryObject sqlQueryObjectDistinct) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		
		List<Object> listaQuery = new ArrayList<>();
		List<JDBCObject> listaParams = new ArrayList<>();
		List<Object> returnField = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareSelect(jdbcProperties, log, connection, sqlQueryObject, 
        						expression, this.getConfigurazionePolicyFieldConverter(), ConfigurazionePolicy.model(), 
        						listaQuery,listaParams);
		
		joinEngine(expression,sqlQueryObject);
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.select(jdbcProperties, log, connection,
        								org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareSqlQueryObjectForSelectDistinct(sqlQueryObject,sqlQueryObjectDistinct), 
        								expression, this.getConfigurazionePolicyFieldConverter(), ConfigurazionePolicy.model(),
        								listaQuery,listaParams,returnField);
		if(list!=null && !list.isEmpty()){
			return list;
		}
		else{
			throw org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.newNotFoundException();
		}
	}
	
	@Override
	public List<Map<String,Object>> union(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
												Union union, UnionExpression ... unionExpression) throws ServiceException,NotFoundException,NotImplementedException,Exception {		
		
		List<ISQLQueryObject> sqlQueryObjectInnerList = new ArrayList<>();
		List<JDBCObject> jdbcObjects = new ArrayList<>();
		List<Class<?>> returnClassTypes = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareUnion(jdbcProperties, log, connection, sqlQueryObject, 
        						this.getConfigurazionePolicyFieldConverter(), ConfigurazionePolicy.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				joinEngine(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.union(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getConfigurazionePolicyFieldConverter(), ConfigurazionePolicy.model(), 
        								sqlQueryObjectInnerList, jdbcObjects, returnClassTypes, union, unionExpression);
        if(list!=null && !list.isEmpty()){
			return list;
		}
		else{
			throw org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.newNotFoundException();
		}								
	}
	
	@Override
	public NonNegativeNumber unionCount(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
												Union union, UnionExpression ... unionExpression) throws ServiceException,NotFoundException,NotImplementedException,Exception {		
		
		List<ISQLQueryObject> sqlQueryObjectInnerList = new ArrayList<>();
		List<JDBCObject> jdbcObjects = new ArrayList<>();
		List<Class<?>> returnClassTypes = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareUnionCount(jdbcProperties, log, connection, sqlQueryObject, 
        						this.getConfigurazionePolicyFieldConverter(), ConfigurazionePolicy.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				joinEngine(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        NonNegativeNumber number = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.unionCount(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getConfigurazionePolicyFieldConverter(), ConfigurazionePolicy.model(), 
        								sqlQueryObjectInnerList, jdbcObjects, returnClassTypes, union, unionExpression);
        if(number!=null && number.longValue()>=0){
			return number;
		}
		else{
			throw org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.newNotFoundException();
		}
	}



	// -- ConstructorExpression	

	@Override
	public JDBCExpression newExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCExpression(this.getConfigurazionePolicyFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}


	@Override
	public JDBCPaginatedExpression newPaginatedExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(this.getConfigurazionePolicyFieldConverter());
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
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPolicy id, ConfigurazionePolicy obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,id,null));
	}
	
	@Override
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, ConfigurazionePolicy obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,tableId,null));
	}
	private void _mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazionePolicy obj, ConfigurazionePolicy imgSaved) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		if(imgSaved==null){
			return;
		}
		obj.setId(imgSaved.getId());

	}
	
	@Override
	public ConfigurazionePolicy get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this.getEngine(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId), idMappingResolutionBehaviour);
	}
	
	private ConfigurazionePolicy getEngine(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
				
		ConfigurazionePolicy configurazionePolicy = new ConfigurazionePolicy();
		

		// Object configurazionePolicy
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addFromTable(this.getConfigurazionePolicyFieldConverter().toTable(ConfigurazionePolicy.model()));
		sqlQueryObjectGet.addSelectField("id");
		sqlQueryObjectGet.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().ID_POLICY,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().BUILT_IN,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().DESCRIZIONE,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().RISORSA,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().SIMULTANEE,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().VALORE,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().VALORE_2,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().VALORE_TIPO_BANDA,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().VALORE_TIPO_LATENZA,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().MODALITA_CONTROLLO,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().TIPO_INTERVALLO_OSSERVAZIONE_REALTIME,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().TIPO_INTERVALLO_OSSERVAZIONE_STATISTICO,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().INTERVALLO_OSSERVAZIONE,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().FINESTRA_OSSERVAZIONE,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().TIPO_APPLICABILITA,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().APPLICABILITA_CON_CONGESTIONE,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().APPLICABILITA_DEGRADO_PRESTAZIONALE,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_MODALITA_CONTROLLO,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_TIPO_INTERVALLO_OSSERVAZIONE_REALTIME,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_TIPO_INTERVALLO_OSSERVAZIONE_STATISTICO,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_INTERVALLO_OSSERVAZIONE,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_FINESTRA_OSSERVAZIONE,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_TIPO_LATENZA,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().APPLICABILITA_STATO_ALLARME,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().ALLARME_NOME,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().ALLARME_STATO,true));
		sqlQueryObjectGet.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().ALLARME_NOT_STATO,true));
		sqlQueryObjectGet.addWhereCondition("id=?");

		// Get configurazionePolicy
		configurazionePolicy = (ConfigurazionePolicy) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(), ConfigurazionePolicy.model(), this.getConfigurazionePolicyFetch(),
			new JDBCObject(tableId,Long.class));



		
        return configurazionePolicy;  
	
	} 
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._exists(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	private boolean _exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		boolean existsConfigurazionePolicy = false;

		sqlQueryObject = sqlQueryObject.newSQLQueryObject();
		sqlQueryObject.setANDLogicOperator(true);

		sqlQueryObject.addFromTable(this.getConfigurazionePolicyFieldConverter().toTable(ConfigurazionePolicy.model()));
		sqlQueryObject.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().ID_POLICY,true));
		sqlQueryObject.addWhereCondition("id=?");


		// Exists configurazionePolicy
		existsConfigurazionePolicy = jdbcUtilities.exists(sqlQueryObject.createSQLQuery(), jdbcProperties.isShowSql(),
			new JDBCObject(tableId,Long.class));

		
        return existsConfigurazionePolicy;
	
	}
	
	private void joinEngine(IExpression expression, ISQLQueryObject sqlQueryObject) throws NotImplementedException, ServiceException, Exception{
        
	}
	
	protected java.util.List<Object> getRootTablePrimaryKeyValuesEngine(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPolicy id) throws NotFoundException, ServiceException, NotImplementedException, Exception{
	    // Identificativi
        java.util.List<Object> rootTableIdValues = new java.util.ArrayList<>();
        Long longId = this.findIdConfigurazionePolicy(jdbcProperties, log, connection, sqlQueryObject.newSQLQueryObject(), id, true);
		rootTableIdValues.add(longId);
        
        return rootTableIdValues;
	}
	
	protected Map<String, List<IField>> getMapTableToPKColumnEngine() throws NotImplementedException, Exception{
	
		ConfigurazionePolicyFieldConverter converter = this.getConfigurazionePolicyFieldConverter();
		Map<String, List<IField>> mapTableToPKColumn = new java.util.HashMap<>();
		UtilsTemplate<IField> utilities = new UtilsTemplate<>();

		// ConfigurazionePolicy.model()
		mapTableToPKColumn.put(converter.toTable(ConfigurazionePolicy.model()),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(ConfigurazionePolicy.model()))
			));
        
        return mapTableToPKColumn;		
	}
	
	@Override
	public List<Long> findAllTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression paginatedExpression) throws ServiceException, NotImplementedException, Exception {
		
		List<Long> list = new ArrayList<Long>();

		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getConfigurazionePolicyFieldConverter().toTable(ConfigurazionePolicy.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareFindAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
												this.getConfigurazionePolicyFieldConverter(), ConfigurazionePolicy.model());
		
		joinEngine(paginatedExpression,sqlQueryObject);
		
		List<Object> listObjects = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.findAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
																			this.getConfigurazionePolicyFieldConverter(), ConfigurazionePolicy.model(), objectIdClass, listaQuery);
		for(Object object: listObjects) {
			list.add((Long)object);
		}

        return list;
		
	}
	
	@Override
	public long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, Exception {
	
		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getConfigurazionePolicyFieldConverter().toTable(ConfigurazionePolicy.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareFind(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getConfigurazionePolicyFieldConverter(), ConfigurazionePolicy.model());
		
		joinEngine(expression,sqlQueryObject);

		Object res = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.find(jdbcProperties, log, connection, sqlQueryObject, expression,
														this.getConfigurazionePolicyFieldConverter(), ConfigurazionePolicy.model(), objectIdClass, listaQuery);
		if(res!=null && (((Long) res).longValue()>0) ){
			return ((Long) res).longValue();
		}
		else{
			throw org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.newNotFoundException();
		}
		
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws ServiceException, NotFoundException, NotImplementedException, Exception {
		return this.inUseEngine(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}

	private InUse inUseEngine(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws ServiceException, NotFoundException, NotImplementedException, Exception {

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
	public IdPolicy findId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
               

		// Object _configurazionePolicy
		sqlQueryObjectGet.addFromTable(this.getConfigurazionePolicyFieldConverter().toTable(ConfigurazionePolicy.model()));
		sqlQueryObjectGet.addSelectField(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().ID_POLICY,true));
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addWhereCondition("id=?");

		// Recupero _configurazionePolicy
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_configurazionePolicy = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(tableId,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_configurazionePolicy = new ArrayList<Class<?>>();
		listaFieldIdReturnType_configurazionePolicy.add(ConfigurazionePolicy.model().ID_POLICY.getFieldType());
		org.openspcoop2.core.controllo_traffico.IdPolicy id_configurazionePolicy = null;
		List<Object> listaFieldId_configurazionePolicy = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_configurazionePolicy, searchParams_configurazionePolicy);
		if(listaFieldId_configurazionePolicy==null || listaFieldId_configurazionePolicy.size()<=0){
			if(throwNotFound){
				throw org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.newNotFoundException();
			}
		}
		else{
			// set _configurazionePolicy
			id_configurazionePolicy = new org.openspcoop2.core.controllo_traffico.IdPolicy();
			id_configurazionePolicy.setNome((String)listaFieldId_configurazionePolicy.get(0));
		}
		
		return id_configurazionePolicy;
		
	}

	@Override
	public Long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPolicy id, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
	
		return this.findIdConfigurazionePolicy(jdbcProperties,log,connection,sqlQueryObject,id,throwNotFound);
			
	}
	
	@Override
	public List<List<Object>> nativeQuery(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
											String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException,Exception{
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.nativeQuery(jdbcProperties, log, connection, sqlQueryObject,
																							sql,returnClassTypes,param);
														
	}
	
	protected Long findIdConfigurazionePolicy(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPolicy id, boolean throwNotFound) throws NotFoundException, ServiceException, NotImplementedException, Exception {

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

		// Object _configurazionePolicy
		sqlQueryObjectGet.addFromTable(this.getConfigurazionePolicyFieldConverter().toTable(ConfigurazionePolicy.model()));
		sqlQueryObjectGet.addSelectField("id");
		// Devono essere mappati nella where condition i metodi dell'oggetto id.getXXX
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.setSelectDistinct(true);
		sqlQueryObjectGet.addWhereCondition(this.getConfigurazionePolicyFieldConverter().toColumn(ConfigurazionePolicy.model().ID_POLICY,true)+"=?");
		
		// Recupero _configurazionePolicy
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_configurazionePolicy = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id.getNome(),String.class),
		};
		Long id_configurazionePolicy = null;
		try{
			id_configurazionePolicy = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
						Long.class, searchParams_configurazionePolicy);
		}catch(NotFoundException notFound){
			if(throwNotFound){
				throw new NotFoundException(notFound);
			}
		}
		if(id_configurazionePolicy==null || id_configurazionePolicy<=0){
			if(throwNotFound){
				throw org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.newNotFoundException();
			}
		}
		
		return id_configurazionePolicy;
	}
}
