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
package org.openspcoop2.monitor.engine.config.transazioni.dao.jdbc;

import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import java.sql.Connection;

import org.slf4j.Logger;

import org.openspcoop2.utils.sql.ISQLQueryObject;

import org.openspcoop2.monitor.engine.config.base.IdConfigurazioneServizioAzione;
import org.openspcoop2.monitor.engine.config.base.Plugin;
import org.openspcoop2.monitor.engine.config.base.dao.jdbc.JDBCConfigurazioneServizioAzioneBaseLib;
import org.openspcoop2.monitor.engine.config.base.dao.jdbc.JDBCPluginsBaseLib;

import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.generic_project.dao.jdbc.utils.IJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject;
import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceSearchWithId;
import org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione;
import org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato;
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
import org.openspcoop2.monitor.engine.config.transazioni.dao.jdbc.converter.ConfigurazioneTransazioneFieldConverter;
import org.openspcoop2.monitor.engine.config.transazioni.dao.jdbc.fetch.ConfigurazioneTransazioneFetch;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto;

/**     
 * JDBCConfigurazioneTransazioneServiceSearchImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCConfigurazioneTransazioneServiceSearchImpl implements IJDBCServiceSearchWithId<ConfigurazioneTransazione, IdConfigurazioneTransazione, JDBCServiceManager> {

	private ConfigurazioneTransazioneFieldConverter _configurazioneTransazioneFieldConverter = null;
	public ConfigurazioneTransazioneFieldConverter getConfigurazioneTransazioneFieldConverter() {
		if(this._configurazioneTransazioneFieldConverter==null){
			this._configurazioneTransazioneFieldConverter = new ConfigurazioneTransazioneFieldConverter(this.jdbcServiceManager.getJdbcProperties().getDatabaseType());
		}		
		return this._configurazioneTransazioneFieldConverter;
	}
	@Override
	public ISQLFieldConverter getFieldConverter() {
		return this.getConfigurazioneTransazioneFieldConverter();
	}
	
	private ConfigurazioneTransazioneFetch configurazioneTransazioneFetch = new ConfigurazioneTransazioneFetch();
	public ConfigurazioneTransazioneFetch getConfigurazioneTransazioneFetch() {
		return this.configurazioneTransazioneFetch;
	}
	@Override
	public IJDBCFetch getFetch() {
		return getConfigurazioneTransazioneFetch();
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
	public IdConfigurazioneTransazione convertToId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneTransazione configurazioneTransazione) throws NotImplementedException, ServiceException, Exception{
	
		IdConfigurazioneTransazione idTransazione = new IdConfigurazioneTransazione();
		idTransazione.setIdConfigurazioneServizioAzione(configurazioneTransazione.getIdConfigurazioneServizioAzione());
		return idTransazione;
	}
	
	@Override
	public ConfigurazioneTransazione get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneTransazione id, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {
		Long id_configurazioneTransazione = ( (id!=null && id.getId()!=null && id.getId()>0) ? id.getId() : this.findIdConfigurazioneTransazione(jdbcProperties, log, connection, sqlQueryObject, id, true));
		return this._get(jdbcProperties, log, connection, sqlQueryObject, id_configurazioneTransazione,idMappingResolutionBehaviour);
		
		
	}
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneTransazione id) throws MultipleResultException, NotImplementedException, ServiceException,Exception {

		Long id_configurazioneTransazione = this.findIdConfigurazioneTransazione(jdbcProperties, log, connection, sqlQueryObject, id, false);
		return id_configurazioneTransazione != null && id_configurazioneTransazione > 0;
		
	}
	
	@Override
	public List<IdConfigurazioneTransazione> findAllIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

		List<IdConfigurazioneTransazione> list = new ArrayList<IdConfigurazioneTransazione>();

		// TODO: implementazione non efficente. 
		// Per ottenere una implementazione efficente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari a create l'ID logico
		// 2. Usare metodo getConfigurazioneTransazioneFetch() sul risultato della select per ottenere un oggetto ConfigurazioneTransazione
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 
		// 3. Usare metodo convertToId per ottenere l'id

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	ConfigurazioneTransazione configurazioneTransazione = this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour);
			IdConfigurazioneTransazione idConfigurazioneTransazione = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,configurazioneTransazione);
        	list.add(idConfigurazioneTransazione);
        }

        return list;
		
	}
	
	@Override
	public List<ConfigurazioneTransazione> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

        List<ConfigurazioneTransazione> list = new ArrayList<ConfigurazioneTransazione>();
        
        // TODO: implementazione non efficente. 
		// Per ottenere una implementazione efficente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari
		// 2. Usare metodo getConfigurazioneTransazioneFetch() sul risultato della select per ottenere un oggetto ConfigurazioneTransazione
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	list.add(this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour));
        }

        return list;      
		
	}
	
	@Override
	public ConfigurazioneTransazione find(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) 
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
												this.getConfigurazioneTransazioneFieldConverter(), ConfigurazioneTransazione.model());
		
		sqlQueryObject.addSelectCountField(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model())+".id","tot",true);
		
		_join(expression,sqlQueryObject);
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.count(jdbcProperties, log, connection, sqlQueryObject, expression,
																			this.getConfigurazioneTransazioneFieldConverter(), ConfigurazioneTransazione.model(),listaQuery);
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneTransazione id) throws NotFoundException, NotImplementedException, ServiceException,Exception {
		
		Long id_configurazioneTransazione = this.findIdConfigurazioneTransazione(jdbcProperties, log, connection, sqlQueryObject, id, true);
        return this._inUse(jdbcProperties, log, connection, sqlQueryObject, id_configurazioneTransazione);
		
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
												this.getConfigurazioneTransazioneFieldConverter(), field);

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
        						expression, this.getConfigurazioneTransazioneFieldConverter(), ConfigurazioneTransazione.model(), 
        						listaQuery,listaParams);
		
		_join(expression,sqlQueryObject);
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.select(jdbcProperties, log, connection,
        								org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareSqlQueryObjectForSelectDistinct(sqlQueryObject,sqlQueryObjectDistinct), 
        								expression, this.getConfigurazioneTransazioneFieldConverter(), ConfigurazioneTransazione.model(),
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
        						this.getConfigurazioneTransazioneFieldConverter(), ConfigurazioneTransazione.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.union(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getConfigurazioneTransazioneFieldConverter(), ConfigurazioneTransazione.model(), 
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
        						this.getConfigurazioneTransazioneFieldConverter(), ConfigurazioneTransazione.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        NonNegativeNumber number = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.unionCount(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getConfigurazioneTransazioneFieldConverter(), ConfigurazioneTransazione.model(), 
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
			return new JDBCExpression(this.getConfigurazioneTransazioneFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}


	@Override
	public JDBCPaginatedExpression newPaginatedExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(this.getConfigurazioneTransazioneFieldConverter());
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
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneTransazione id, ConfigurazioneTransazione obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,id,null));
	}
	
	@Override
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, ConfigurazioneTransazione obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,tableId,null));
	}
	private void _mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneTransazione obj, ConfigurazioneTransazione imgSaved) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		if(imgSaved==null){
			return;
		}
		obj.setId(imgSaved.getId());
		if(obj.getIdConfigurazioneServizioAzione()!=null && 
				imgSaved.getIdConfigurazioneServizioAzione()!=null){
			obj.getIdConfigurazioneServizioAzione().setId(imgSaved.getIdConfigurazioneServizioAzione().getId());
			if(obj.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio()!=null && 
					imgSaved.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio()!=null){
				obj.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().setId(imgSaved.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().getId());
			}
		}
		if(obj.getConfigurazioneTransazionePluginList()!=null){
			List<org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin> listObj_ = obj.getConfigurazioneTransazionePluginList();
			for(org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin itemObj_ : listObj_){
				org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin itemAlreadySaved_ = null;
				if(imgSaved.getConfigurazioneTransazionePluginList()!=null){
					List<org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin> listImgSaved_ = imgSaved.getConfigurazioneTransazionePluginList();
					for(org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin itemImgSaved_ : listImgSaved_){
						boolean objEqualsToImgSaved_ = false;
						objEqualsToImgSaved_ = org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getIdConfigurazioneTransazionePlugin(),itemImgSaved_.getIdConfigurazioneTransazionePlugin());
						if(objEqualsToImgSaved_){
							itemAlreadySaved_=itemImgSaved_;
							break;
						}
					}
				}
				if(itemAlreadySaved_!=null){
					itemObj_.setId(itemAlreadySaved_.getId());
					if(itemObj_.getPlugin()!=null && 
							itemAlreadySaved_.getPlugin()!=null){
						itemObj_.getPlugin().setId(itemAlreadySaved_.getPlugin().getId());
					}
				}
			}
		}
		if(obj.getConfigurazioneTransazioneStatoList()!=null){
			List<org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato> listObj_ = obj.getConfigurazioneTransazioneStatoList();
			for(org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato itemObj_ : listObj_){
				org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato itemAlreadySaved_ = null;
				if(imgSaved.getConfigurazioneTransazioneStatoList()!=null){
					List<org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato> listImgSaved_ = imgSaved.getConfigurazioneTransazioneStatoList();
					for(org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato itemImgSaved_ : listImgSaved_){
						boolean objEqualsToImgSaved_ = false;
						objEqualsToImgSaved_ = org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getNome(),itemImgSaved_.getNome()) &&
								org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getTipoMessaggio(),itemImgSaved_.getTipoMessaggio());
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
		if(obj.getConfigurazioneTransazioneRisorsaContenutoList()!=null){
			List<org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto> listObj_ = obj.getConfigurazioneTransazioneRisorsaContenutoList();
			for(org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto itemObj_ : listObj_){
				org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto itemAlreadySaved_ = null;
				if(imgSaved.getConfigurazioneTransazioneRisorsaContenutoList()!=null){
					List<org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto> listImgSaved_ = imgSaved.getConfigurazioneTransazioneRisorsaContenutoList();
					for(org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto itemImgSaved_ : listImgSaved_){
						boolean objEqualsToImgSaved_ = false;
						objEqualsToImgSaved_ = org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getNome(),itemImgSaved_.getNome()) &&
									org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getTipoMessaggio(),itemImgSaved_.getTipoMessaggio());
						if(objEqualsToImgSaved_){
							itemAlreadySaved_=itemImgSaved_;
							break;
						}
					}
				}
				if(itemAlreadySaved_!=null){
					itemObj_.setId(itemAlreadySaved_.getId());
					if(itemObj_.getIdConfigurazioneTransazioneStato()!=null && 
							itemAlreadySaved_.getIdConfigurazioneTransazioneStato()!=null){
						itemObj_.getIdConfigurazioneTransazioneStato().setId(itemAlreadySaved_.getIdConfigurazioneTransazioneStato().getId());
					}
				}
			}
		}           
	}
	
	@Override
	public ConfigurazioneTransazione get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._get(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId), idMappingResolutionBehaviour);
	}
	
	private ConfigurazioneTransazione _get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
				
		ConfigurazioneTransazione configurazioneTransazione = new ConfigurazioneTransazione();
		

		// Object configurazioneTransazione
		ISQLQueryObject sqlQueryObjectGet_configurazioneTransazione = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_configurazioneTransazione.setANDLogicOperator(true);
		sqlQueryObjectGet_configurazioneTransazione.addFromTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model()));
		sqlQueryObjectGet_configurazioneTransazione.addSelectField("id");
		sqlQueryObjectGet_configurazioneTransazione.addSelectField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().ENABLED,true));
		sqlQueryObjectGet_configurazioneTransazione.addWhereCondition("id=?");

		// Get configurazioneTransazione
		configurazioneTransazione = (ConfigurazioneTransazione) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_configurazioneTransazione.createSQLQuery(), jdbcProperties.isShowSql(), ConfigurazioneTransazione.model(), this.getConfigurazioneTransazioneFetch(),
			new JDBCObject(tableId,Long.class));


		if(idMappingResolutionBehaviour==null ||
			(org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour) || org.openspcoop2.generic_project.beans.IDMappingBehaviour.USE_TABLE_ID.equals(idMappingResolutionBehaviour))
		){
			// Recupero idConfigurazioneServizioAzione
				
			ISQLQueryObject sqlQueryObjectGet_configurazioneTransazione_idConfigurazioneServizioAzione = sqlQueryObjectGet.newSQLQueryObject();
			sqlQueryObjectGet_configurazioneTransazione_idConfigurazioneServizioAzione.addFromTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model()));
			sqlQueryObjectGet_configurazioneTransazione_idConfigurazioneServizioAzione.addSelectField("id_conf_servizio_azione");
			sqlQueryObjectGet_configurazioneTransazione_idConfigurazioneServizioAzione.addWhereCondition("id=?");
			sqlQueryObjectGet_configurazioneTransazione_idConfigurazioneServizioAzione.setANDLogicOperator(true);
			Long id_configurazioneTransazione_idConfigurazioneServizioAzione = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_configurazioneTransazione_idConfigurazioneServizioAzione.createSQLQuery(), jdbcProperties.isShowSql(),Long.class,
					new JDBCObject(configurazioneTransazione.getId(),Long.class));
		
			IdConfigurazioneServizioAzione _tmpIdConfigurazioneServizioAzione = 
					JDBCConfigurazioneServizioAzioneBaseLib.getIdConfigurazioneServizioAzione(connection, jdbcProperties, log, id_configurazioneTransazione_idConfigurazioneServizioAzione);
			org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione idConfigurazioneServizioAzione = new org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione();
			idConfigurazioneServizioAzione.setAzione(_tmpIdConfigurazioneServizioAzione.getAzione());
			org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio idConfigurazioneServizio = new org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio();
			idConfigurazioneServizio.setAccordo(_tmpIdConfigurazioneServizioAzione.getIdConfigurazioneServizio().getAccordo());
			idConfigurazioneServizio.setTipoSoggettoReferente(_tmpIdConfigurazioneServizioAzione.getIdConfigurazioneServizio().getTipoSoggettoReferente());
			idConfigurazioneServizio.setNomeSoggettoReferente(_tmpIdConfigurazioneServizioAzione.getIdConfigurazioneServizio().getNomeSoggettoReferente());
			idConfigurazioneServizio.setVersione(_tmpIdConfigurazioneServizioAzione.getIdConfigurazioneServizio().getVersione());
			idConfigurazioneServizio.setServizio(_tmpIdConfigurazioneServizioAzione.getIdConfigurazioneServizio().getServizio());
			idConfigurazioneServizioAzione.setIdConfigurazioneServizio(idConfigurazioneServizio);
			configurazioneTransazione.setIdConfigurazioneServizioAzione(idConfigurazioneServizioAzione);
		}


		// Object configurazioneTransazione_configurazioneTransazionePlugin
		ISQLQueryObject sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazionePlugin = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazionePlugin.setANDLogicOperator(true);
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazionePlugin.addFromTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN));
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazionePlugin.addFromTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.PLUGIN));
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazionePlugin.addSelectField(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN)+".id");
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazionePlugin.addSelectField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.ID_CONFIGURAZIONE_TRANSAZIONE_PLUGIN,true));
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazionePlugin.addSelectField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.ENABLED,true));
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazionePlugin.addWhereCondition("id_configurazione_transazione=?");
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazionePlugin.addWhereCondition(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN)+".id_plugin="+
				this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.PLUGIN)+".id");
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazionePlugin.addOrderBy(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.PLUGIN.LABEL,true));
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazionePlugin.setSortType(true);
		
		
		
		// Get configurazioneTransazione_configurazioneTransazionePlugin
		java.util.List<Object> configurazioneTransazione_configurazioneTransazionePlugin_list = jdbcUtilities.executeQuery(sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazionePlugin.createSQLQuery(), jdbcProperties.isShowSql(), ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN, this.getConfigurazioneTransazioneFetch(),
			new JDBCObject(configurazioneTransazione.getId(),Long.class));

		if(configurazioneTransazione_configurazioneTransazionePlugin_list != null) {
			for (Object configurazioneTransazione_configurazioneTransazionePlugin_object: configurazioneTransazione_configurazioneTransazionePlugin_list) {
				ConfigurazioneTransazionePlugin configurazioneTransazione_configurazioneTransazionePlugin = (ConfigurazioneTransazionePlugin) configurazioneTransazione_configurazioneTransazionePlugin_object;


				if(idMappingResolutionBehaviour==null ||
					(org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour) || org.openspcoop2.generic_project.beans.IDMappingBehaviour.USE_TABLE_ID.equals(idMappingResolutionBehaviour))
				){
					// Object _configurazioneTransazione_configurazioneTransazionePlugin_plugin (recupero id)
					ISQLQueryObject sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazionePlugin_plugin_readFkId = sqlQueryObjectGet.newSQLQueryObject();
					sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazionePlugin_plugin_readFkId.addFromTable(this.getConfigurazioneTransazioneFieldConverter().toTable(org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN));
					sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazionePlugin_plugin_readFkId.addSelectField("id_plugin");
					sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazionePlugin_plugin_readFkId.addWhereCondition("id=?");
					sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazionePlugin_plugin_readFkId.setANDLogicOperator(true);
					Long idFK_configurazioneTransazione_configurazioneTransazionePlugin_plugin = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazionePlugin_plugin_readFkId.createSQLQuery(), jdbcProperties.isShowSql(),Long.class,
							new JDBCObject(configurazioneTransazione_configurazioneTransazionePlugin.getId(),Long.class));
				
					Plugin plugin = JDBCPluginsBaseLib.getPlugin(connection, jdbcProperties, log, idFK_configurazioneTransazione_configurazioneTransazionePlugin_plugin);
					org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin info = new org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin();
					info.setTipoPlugin(plugin.getTipoPlugin());
					info.setTipo(plugin.getTipo());
					info.setClassName(plugin.getClassName());
					info.setDescrizione(plugin.getDescrizione());
					info.setLabel(plugin.getLabel());
					configurazioneTransazione_configurazioneTransazionePlugin.setPlugin(info);
				
					configurazioneTransazione.addConfigurazioneTransazionePlugin(configurazioneTransazione_configurazioneTransazionePlugin);
				}

				configurazioneTransazione.addConfigurazioneTransazionePlugin(configurazioneTransazione_configurazioneTransazionePlugin);
			}
		}

		// Object configurazioneTransazione_configurazioneTransazioneStato
		ISQLQueryObject sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneStato = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneStato.setANDLogicOperator(true);
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneStato.addFromTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO));
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneStato.addSelectField("id");
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneStato.addSelectField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.ENABLED,true));
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneStato.addSelectField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.NOME,true));
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneStato.addSelectField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.TIPO_CONTROLLO,true));
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneStato.addSelectField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.TIPO_MESSAGGIO,true));
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneStato.addSelectField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.VALORE,true));
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneStato.addSelectField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.XPATH,true));
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneStato.addWhereCondition("id_configurazione_transazione=?");
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneStato.addOrderBy(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.NOME,true));
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneStato.setSortType(true);
		
		
		
		// Get configurazioneTransazione_configurazioneTransazioneStato
		java.util.List<Object> configurazioneTransazione_configurazioneTransazioneStato_list = jdbcUtilities.executeQuery(sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneStato.createSQLQuery(), jdbcProperties.isShowSql(), ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO, this.getConfigurazioneTransazioneFetch(),
			new JDBCObject(configurazioneTransazione.getId(),Long.class));

		Hashtable<Long, String> mapIdTransazioneStatoToName = new Hashtable<Long, String>();
		
		if(configurazioneTransazione_configurazioneTransazioneStato_list != null) {
			for (Object configurazioneTransazione_configurazioneTransazioneStato_object: configurazioneTransazione_configurazioneTransazioneStato_list) {
				ConfigurazioneTransazioneStato configurazioneTransazione_configurazioneTransazioneStato = (ConfigurazioneTransazioneStato) configurazioneTransazione_configurazioneTransazioneStato_object;
				configurazioneTransazione.addConfigurazioneTransazioneStato(configurazioneTransazione_configurazioneTransazioneStato);
				mapIdTransazioneStatoToName.put(configurazioneTransazione_configurazioneTransazioneStato.getId(), configurazioneTransazione_configurazioneTransazioneStato.getNome());
			}
		}

		// Object configurazioneTransazione_configurazioneTransazioneRisorsaContenuto
		ISQLQueryObject sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.setANDLogicOperator(true);
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addFromTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO));
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addSelectField("id");
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addSelectField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ABILITA_ANONIMIZZAZIONE,true));
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addSelectField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ABILITA_COMPRESSIONE,true));
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addSelectField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_COMPRESSIONE,true));
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addSelectField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.CARATTERE_MASCHERA,true));
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addSelectField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.NUMERO_CARATTERI_MASCHERA,true));
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addSelectField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.POSIZIONAMENTO_MASCHERA,true));
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addSelectField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_MASCHERAMENTO,true));
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addSelectField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ENABLED,true));
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addSelectField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.NOME,true));
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addSelectField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_MESSAGGIO,true));
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addSelectField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.XPATH,true));
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addSelectField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.STAT_ENABLED,true));
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addWhereCondition("id_conf_transazione=?");
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addOrderBy(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.NOME,true));
		sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.setSortType(true);
				
		
		// Get configurazioneTransazione_configurazioneTransazioneRisorsaContenuto
		java.util.List<Object> configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_list = jdbcUtilities.executeQuery(sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.createSQLQuery(), jdbcProperties.isShowSql(), ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO, this.getConfigurazioneTransazioneFetch(),
			new JDBCObject(configurazioneTransazione.getId(),Long.class));

		if(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_list != null) {
			for (Object configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_object: configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_list) {
				ConfigurazioneTransazioneRisorsaContenuto configurazioneTransazione_configurazioneTransazioneRisorsaContenuto = (ConfigurazioneTransazioneRisorsaContenuto) configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_object;


				if(idMappingResolutionBehaviour==null ||
					(org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour) || org.openspcoop2.generic_project.beans.IDMappingBehaviour.USE_TABLE_ID.equals(idMappingResolutionBehaviour))
				){
					// Recupero idConfigurazioneTransazioneStato
					ISQLQueryObject sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_idConfigurazioneTransazioneStato = 
							sqlQueryObjectGet.newSQLQueryObject();
					sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_idConfigurazioneTransazioneStato.addFromTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO));
					sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_idConfigurazioneTransazioneStato.addSelectField("id_configurazione_stato");
					sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_idConfigurazioneTransazioneStato.addWhereCondition("id=?");
					sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_idConfigurazioneTransazioneStato.setANDLogicOperator(true);
					Long id_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_idConfigurazioneTransazioneStato = null;
					try{
						id_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_idConfigurazioneTransazioneStato = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_idConfigurazioneTransazioneStato.createSQLQuery(), jdbcProperties.isShowSql(),Long.class,
							new JDBCObject(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getId(),Long.class));
					}catch(NotFoundException  e){
						id_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_idConfigurazioneTransazioneStato = null;
					}
				
					IdConfigurazioneTransazioneStato idConfigurazioneTransazioneStato = new IdConfigurazioneTransazioneStato();
					if(id_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_idConfigurazioneTransazioneStato != null)
						idConfigurazioneTransazioneStato.setStato(mapIdTransazioneStatoToName.get(id_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_idConfigurazioneTransazioneStato));
					configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.setIdConfigurazioneTransazioneStato(idConfigurazioneTransazioneStato);
				
				}

				configurazioneTransazione.addConfigurazioneTransazioneRisorsaContenuto(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto);
			}
		}
             
        return configurazioneTransazione;  
	
	} 
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._exists(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	private boolean _exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		boolean existsConfigurazioneTransazione = false;

		sqlQueryObject = sqlQueryObject.newSQLQueryObject();
		sqlQueryObject.setANDLogicOperator(true);

		sqlQueryObject.addFromTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model()));
		sqlQueryObject.addSelectField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().ENABLED,true));
		sqlQueryObject.addWhereCondition("id=?");


		// Exists configurazioneTransazione
		existsConfigurazioneTransazione = jdbcUtilities.exists(sqlQueryObject.createSQLQuery(), jdbcProperties.isShowSql(),
			new JDBCObject(tableId,Long.class));

		
        return existsConfigurazioneTransazione;
	
	}
	
	private void _join(IExpression expression, ISQLQueryObject sqlQueryObject) throws NotImplementedException, ServiceException, Exception{
	
		if(expression.inUseModel(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE,false)){
			String tableName1 = this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model());
			String tableName2 = this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE);
			sqlQueryObject.addWhereCondition(tableName1+".id_conf_servizio_azione="+tableName2+".id");
		}
		if(expression.inUseModel(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO,false)){
			String tableName1 = this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE);
			String tableName2 = this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO);
			sqlQueryObject.addWhereCondition(tableName1+".id_config_servizio="+tableName2+".id");
		}
		if(expression.inUseModel(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO,false)){
			String tableName1 = this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO);
			String tableName2 = this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model());
			sqlQueryObject.addWhereCondition(tableName1+".id_configurazione_transazione="+tableName2+".id");
		}
		if(expression.inUseModel(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO,false)){
			String tableName1 = this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO);
			String tableName2 = this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model());
			sqlQueryObject.addWhereCondition(tableName1+".id_conf_transazione="+tableName2+".id");
		}
		if(expression.inUseModel(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ID_CONFIGURAZIONE_TRANSAZIONE_STATO,false)){
			String tableName1 = this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO);
			String tableName2 = this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ID_CONFIGURAZIONE_TRANSAZIONE_STATO);
			sqlQueryObject.addWhereCondition(tableName1+".id_configurazione_stato="+tableName2+".id");
		}
		
		// Check FROM Table necessarie per le join di oggetti annidati dal secondo livello in poi dove pero' non viene poi utilizzato l'oggetto del livello precedente nella espressione
		if(expression.inUseModel(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO,false)){
			if(expression.inUseModel(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE,false)==false){
				sqlQueryObject.addFromTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE));
			}
		}
		if(expression.inUseModel(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ID_CONFIGURAZIONE_TRANSAZIONE_STATO,false)){
			if(expression.inUseModel(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO,false)==false){
				sqlQueryObject.addFromTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO));
			}
		}
        
	}
	
	protected java.util.List<Object> _getRootTablePrimaryKeyValues(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneTransazione id) throws NotFoundException, ServiceException, NotImplementedException, Exception{
	    // Identificativi
        java.util.List<Object> rootTableIdValues = new java.util.ArrayList<Object>();
        // TODO: Define the column values used to identify the primary key
		Long longId = this.findIdConfigurazioneTransazione(jdbcProperties, log, connection, sqlQueryObject.newSQLQueryObject(), id, true);
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
	
		ConfigurazioneTransazioneFieldConverter converter = this.getConfigurazioneTransazioneFieldConverter();
		Map<String, List<IField>> mapTableToPKColumn = new java.util.Hashtable<String, List<IField>>();
		UtilsTemplate<IField> utilities = new UtilsTemplate<IField>();

		// ConfigurazioneTransazione.model()
		mapTableToPKColumn.put(converter.toTable(ConfigurazioneTransazione.model()),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(ConfigurazioneTransazione.model()))
			));

		// ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE
		mapTableToPKColumn.put(converter.toTable(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE))
			));

		// ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO
		mapTableToPKColumn.put(converter.toTable(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO))
			));

		// ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO
		mapTableToPKColumn.put(converter.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO))
			));

		// ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO
		mapTableToPKColumn.put(converter.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO))
			));

		// ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ID_CONFIGURAZIONE_TRANSAZIONE_STATO
		mapTableToPKColumn.put(converter.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ID_CONFIGURAZIONE_TRANSAZIONE_STATO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ID_CONFIGURAZIONE_TRANSAZIONE_STATO))
			));
        
        return mapTableToPKColumn;		
	}
	
	@Override
	public List<Long> findAllTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression paginatedExpression) throws ServiceException, NotImplementedException, Exception {
		
		List<Long> list = new ArrayList<Long>();

		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFindAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
												this.getConfigurazioneTransazioneFieldConverter(), ConfigurazioneTransazione.model());
		
		_join(paginatedExpression,sqlQueryObject);
		
		List<Object> listObjects = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.findAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
																			this.getConfigurazioneTransazioneFieldConverter(), ConfigurazioneTransazione.model(), objectIdClass, listaQuery);
		for(Object object: listObjects) {
			list.add((Long)object);
		}

        return list;
		
	}
	
	@Override
	public long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, Exception {
	
		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFind(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getConfigurazioneTransazioneFieldConverter(), ConfigurazioneTransazione.model());
		
		_join(expression,sqlQueryObject);

		Object res = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.find(jdbcProperties, log, connection, sqlQueryObject, expression,
														this.getConfigurazioneTransazioneFieldConverter(), ConfigurazioneTransazione.model(), objectIdClass, listaQuery);
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
	public IdConfigurazioneTransazione findId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();             

		// Object _configurazioneTransazione
		sqlQueryObjectGet.addFromTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model()));
		sqlQueryObjectGet.addSelectField("id_conf_servizio_azione");
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addWhereCondition("id=?");

		// Recupero _configurazioneTransazione
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_configurazioneTransazione = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(tableId,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_configurazioneTransazione = new ArrayList<Class<?>>();
		listaFieldIdReturnType_configurazioneTransazione.add(Long.class);
		org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione id_configurazioneTransazione = null;
		List<Object> listaFieldId_configurazioneTransazione = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_configurazioneTransazione, searchParams_configurazioneTransazione);
		if(listaFieldId_configurazioneTransazione==null || listaFieldId_configurazioneTransazione.size()<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		else{
			// set _configurazioneTransazione
			id_configurazioneTransazione = new org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione();
			Long idFK_configurazioneTransazione_configurazioneServizioAzione = (Long) listaFieldId_configurazioneTransazione.get(1);
			IdConfigurazioneServizioAzione _tmpIdConfigurazioneServizioAzione = 
					JDBCConfigurazioneServizioAzioneBaseLib.getIdConfigurazioneServizioAzione(connection, jdbcProperties, log, idFK_configurazioneTransazione_configurazioneServizioAzione);
			
			org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione idConfigurazioneServizioAzione = new org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione();
			idConfigurazioneServizioAzione.setAzione(_tmpIdConfigurazioneServizioAzione.getAzione());
			org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio idConfigurazioneServizio = new org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizio();
			idConfigurazioneServizio.setAccordo(_tmpIdConfigurazioneServizioAzione.getIdConfigurazioneServizio().getAccordo());
			idConfigurazioneServizio.setTipoSoggettoReferente(_tmpIdConfigurazioneServizioAzione.getIdConfigurazioneServizio().getTipoSoggettoReferente());
			idConfigurazioneServizio.setNomeSoggettoReferente(_tmpIdConfigurazioneServizioAzione.getIdConfigurazioneServizio().getNomeSoggettoReferente());
			idConfigurazioneServizio.setVersione(_tmpIdConfigurazioneServizioAzione.getIdConfigurazioneServizio().getVersione());
			idConfigurazioneServizio.setServizio(_tmpIdConfigurazioneServizioAzione.getIdConfigurazioneServizio().getServizio());
			idConfigurazioneServizioAzione.setIdConfigurazioneServizio(idConfigurazioneServizio);
			id_configurazioneTransazione.setIdConfigurazioneServizioAzione(idConfigurazioneServizioAzione);
		}
		
		return id_configurazioneTransazione;
		
	}

	@Override
	public Long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneTransazione id, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
	
		return this.findIdConfigurazioneTransazione(jdbcProperties,log,connection,sqlQueryObject,id,throwNotFound);
			
	}
	
	@Override
	public List<List<Object>> nativeQuery(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
											String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException,Exception{
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.nativeQuery(jdbcProperties, log, connection, sqlQueryObject,
																							sql,returnClassTypes,param);
														
	}
	
	protected Long findIdConfigurazioneTransazione(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneTransazione id, boolean throwNotFound) throws NotFoundException, ServiceException, NotImplementedException, Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();

		
		// Object _configurazioneServizioAzione
		
		if(id==null){
			throw new ServiceException("Id is null");
		}
		if(id.getIdConfigurazioneServizioAzione()==null){
			throw new ServiceException("IdConfigurazioneServizioAzione is null");
		}
		if(id.getIdConfigurazioneServizioAzione().getAzione()==null){
			throw new ServiceException("IdConfigurazioneServizioAzione.azione is null");
		}
		if(id.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio()==null){
			throw new ServiceException("IdConfigurazioneServizioAzione.idConfigurazioneServizio is null");
		}
				
		Long id_configurazioneServizioAzione = 
				JDBCConfigurazioneServizioAzioneBaseLib.getIdConfigurazioneServizioAzione(connection, jdbcProperties, log, 
						id.getIdConfigurazioneServizioAzione().getAzione(),
						id.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().getAccordo(), 
						id.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().getTipoSoggettoReferente(), 
						id.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().getNomeSoggettoReferente(), 
						id.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().getVersione(), 
						id.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().getServizio(),
						throwNotFound);
		if(id_configurazioneServizioAzione==null){
			return null; // permesso se throwNotFound==false
		}
		
		
		// Object _configurazioneTransazione
		sqlQueryObjectGet.addFromTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model()));
		sqlQueryObjectGet.addSelectField("id");
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.setSelectDistinct(true);
		sqlQueryObjectGet.addWhereCondition("id_conf_servizio_azione=?");

		// Recupero _configurazioneTransazione
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_configurazioneTransazione = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
				new JDBCObject( id_configurazioneServizioAzione, Long.class )
		};
		Long id_configurazioneTransazione = null;
		try{
			id_configurazioneTransazione = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
						Long.class, searchParams_configurazioneTransazione);
		}catch(NotFoundException notFound){
			if(throwNotFound){
				throw new NotFoundException(notFound);
			}
		}
		if(id_configurazioneTransazione==null || id_configurazioneTransazione<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		
		return id_configurazioneTransazione;
	}
}
