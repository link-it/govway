/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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
package org.openspcoop2.monitor.engine.config.base.dao.jdbc;

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
import org.openspcoop2.monitor.engine.config.base.IdPlugin;
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
import org.openspcoop2.monitor.engine.config.base.dao.jdbc.converter.PluginFieldConverter;
import org.openspcoop2.monitor.engine.config.base.dao.jdbc.fetch.PluginFetch;
import org.openspcoop2.monitor.engine.config.base.dao.jdbc.JDBCServiceManager;

import org.openspcoop2.monitor.engine.config.base.PluginFiltroCompatibilita;
import org.openspcoop2.monitor.engine.config.base.Plugin;
import org.openspcoop2.monitor.engine.config.base.PluginServizioAzioneCompatibilita;
import org.openspcoop2.monitor.engine.config.base.PluginServizioCompatibilita;
import org.openspcoop2.monitor.engine.config.base.constants.TipoPlugin;

/**     
 * JDBCPluginServiceSearchImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCPluginServiceSearchImpl implements IJDBCServiceSearchWithId<Plugin, IdPlugin, JDBCServiceManager> {

	private PluginFieldConverter _pluginFieldConverter = null;
	public PluginFieldConverter getPluginFieldConverter() {
		if(this._pluginFieldConverter==null){
			this._pluginFieldConverter = new PluginFieldConverter(this.jdbcServiceManager.getJdbcProperties().getDatabaseType());
		}		
		return this._pluginFieldConverter;
	}
	@Override
	public ISQLFieldConverter getFieldConverter() {
		return this.getPluginFieldConverter();
	}
	
	private PluginFetch pluginFetch = new PluginFetch();
	public PluginFetch getPluginFetch() {
		return this.pluginFetch;
	}
	@Override
	public IJDBCFetch getFetch() {
		return getPluginFetch();
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
	public IdPlugin convertToId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Plugin plugin) throws NotImplementedException, ServiceException, Exception{
	
		IdPlugin idPlugin = new IdPlugin();
		idPlugin.setTipo(plugin.getTipo());
		idPlugin.setClassName(plugin.getClassName());
		return idPlugin;
	}
	
	@Override
	public Plugin get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPlugin id, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {
		Long id_plugin = ( (id!=null && id.getId()!=null && id.getId()>0) ? id.getId() : this.findIdPlugin(jdbcProperties, log, connection, sqlQueryObject, id, true));
		return this._get(jdbcProperties, log, connection, sqlQueryObject, id_plugin,idMappingResolutionBehaviour);
		
		
	}
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPlugin id) throws MultipleResultException, NotImplementedException, ServiceException,Exception {

		Long id_plugin = this.findIdPlugin(jdbcProperties, log, connection, sqlQueryObject, id, false);
		return id_plugin != null && id_plugin > 0;
		
	}
	
	@Override
	public List<IdPlugin> findAllIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

		List<IdPlugin> list = new ArrayList<IdPlugin>();

		// TODO: implementazione non efficente. 
		// Per ottenere una implementazione efficente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari a create l'ID logico
		// 2. Usare metodo getPluginFetch() sul risultato della select per ottenere un oggetto Plugin
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 
		// 3. Usare metodo convertToId per ottenere l'id

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	Plugin plugin = this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour);
			IdPlugin idPlugin = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,plugin);
        	list.add(idPlugin);
        }

        return list;
		
	}
	
	@Override
	public List<Plugin> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

        List<Plugin> list = new ArrayList<Plugin>();
        
        // TODO: implementazione non efficente. 
		// Per ottenere una implementazione efficente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari
		// 2. Usare metodo getPluginFetch() sul risultato della select per ottenere un oggetto Plugin
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	list.add(this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour));
        }

        return list;      
		
	}
	
	@Override
	public Plugin find(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) 
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
												this.getPluginFieldConverter(), Plugin.model());
		
		sqlQueryObject.addSelectCountField(this.getPluginFieldConverter().toTable(Plugin.model())+".id","tot",true);
		
		_join(expression,sqlQueryObject);
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.count(jdbcProperties, log, connection, sqlQueryObject, expression,
																			this.getPluginFieldConverter(), Plugin.model(),listaQuery);
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPlugin id) throws NotFoundException, NotImplementedException, ServiceException,Exception {
		
		Long id_plugin = this.findIdPlugin(jdbcProperties, log, connection, sqlQueryObject, id, true);
        return this._inUse(jdbcProperties, log, connection, sqlQueryObject, id_plugin);
		
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
												this.getPluginFieldConverter(), field);

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
        						expression, this.getPluginFieldConverter(), Plugin.model(), 
        						listaQuery,listaParams);
		
		_join(expression,sqlQueryObject);
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.select(jdbcProperties, log, connection,
        								org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareSqlQueryObjectForSelectDistinct(sqlQueryObject,sqlQueryObjectDistinct), 
        								expression, this.getPluginFieldConverter(), Plugin.model(),
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
        						this.getPluginFieldConverter(), Plugin.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.union(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getPluginFieldConverter(), Plugin.model(), 
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
        						this.getPluginFieldConverter(), Plugin.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        NonNegativeNumber number = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.unionCount(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getPluginFieldConverter(), Plugin.model(), 
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
			return new JDBCExpression(this.getPluginFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}


	@Override
	public JDBCPaginatedExpression newPaginatedExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(this.getPluginFieldConverter());
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
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPlugin id, Plugin obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,id,null));
	}
	
	@Override
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, Plugin obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,tableId,null));
	}
	private void _mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Plugin obj, Plugin imgSaved) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		if(imgSaved==null){
			return;
		}
		obj.setId(imgSaved.getId());
		if(obj.getPluginServizioCompatibilitaList()!=null){
			List<org.openspcoop2.monitor.engine.config.base.PluginServizioCompatibilita> listObj_ = obj.getPluginServizioCompatibilitaList();
			for(org.openspcoop2.monitor.engine.config.base.PluginServizioCompatibilita itemObj_ : listObj_){
				org.openspcoop2.monitor.engine.config.base.PluginServizioCompatibilita itemAlreadySaved_ = null;
				if(imgSaved.getPluginServizioCompatibilitaList()!=null){
					List<org.openspcoop2.monitor.engine.config.base.PluginServizioCompatibilita> listImgSaved_ = imgSaved.getPluginServizioCompatibilitaList();
					for(org.openspcoop2.monitor.engine.config.base.PluginServizioCompatibilita itemImgSaved_ : listImgSaved_){
						boolean objEqualsToImgSaved_ = false;
						objEqualsToImgSaved_ = org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getUriAccordo(),itemImgSaved_.getUriAccordo()) &&
												org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getServizio(),itemImgSaved_.getServizio());
						if(objEqualsToImgSaved_){
							itemAlreadySaved_=itemImgSaved_;
							break;
						}
					}
				}
				if(itemAlreadySaved_!=null){
					itemObj_.setId(itemAlreadySaved_.getId());
					if(itemObj_.getPluginServizioAzioneCompatibilitaList()!=null){
						List<org.openspcoop2.monitor.engine.config.base.PluginServizioAzioneCompatibilita> listObj_pluginServizioCompatibilita = itemObj_.getPluginServizioAzioneCompatibilitaList();
						for(org.openspcoop2.monitor.engine.config.base.PluginServizioAzioneCompatibilita itemObj_pluginServizioCompatibilita : listObj_pluginServizioCompatibilita){
							org.openspcoop2.monitor.engine.config.base.PluginServizioAzioneCompatibilita itemAlreadySaved_pluginServizioCompatibilita = null;
							if(itemAlreadySaved_.getPluginServizioAzioneCompatibilitaList()!=null){
								List<org.openspcoop2.monitor.engine.config.base.PluginServizioAzioneCompatibilita> listImgSaved_pluginServizioCompatibilita = itemAlreadySaved_.getPluginServizioAzioneCompatibilitaList();
								for(org.openspcoop2.monitor.engine.config.base.PluginServizioAzioneCompatibilita itemImgSaved_pluginServizioCompatibilita : listImgSaved_pluginServizioCompatibilita){
									boolean objEqualsToImgSaved_pluginServizioCompatibilita = false;
									objEqualsToImgSaved_pluginServizioCompatibilita = org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_pluginServizioCompatibilita.getAzione(),itemImgSaved_pluginServizioCompatibilita.getAzione());
									if(objEqualsToImgSaved_pluginServizioCompatibilita){
										itemAlreadySaved_pluginServizioCompatibilita=itemImgSaved_pluginServizioCompatibilita;
										break;
									}
								}
							}
							if(itemAlreadySaved_pluginServizioCompatibilita!=null){
								itemObj_pluginServizioCompatibilita.setId(itemAlreadySaved_pluginServizioCompatibilita.getId());
							}
						}
					}
				}
			}
		}
		if(obj.getPluginFiltroCompatibilitaList()!=null){
			List<org.openspcoop2.monitor.engine.config.base.PluginFiltroCompatibilita> listObj_ = obj.getPluginFiltroCompatibilitaList();
			for(org.openspcoop2.monitor.engine.config.base.PluginFiltroCompatibilita itemObj_ : listObj_){
				org.openspcoop2.monitor.engine.config.base.PluginFiltroCompatibilita itemAlreadySaved_ = null;
				if(imgSaved.getPluginFiltroCompatibilitaList()!=null){
					List<org.openspcoop2.monitor.engine.config.base.PluginFiltroCompatibilita> listImgSaved_ = imgSaved.getPluginFiltroCompatibilitaList();
					for(org.openspcoop2.monitor.engine.config.base.PluginFiltroCompatibilita itemImgSaved_ : listImgSaved_){
						boolean objEqualsToImgSaved_ = false;
						objEqualsToImgSaved_ = org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getIdportaMittente(),itemImgSaved_.getIdportaMittente()) &&
												org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getTipoMittente(),itemImgSaved_.getTipoMittente()) &&
												org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getNomeMittente(),itemImgSaved_.getNomeMittente()) &&
												org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getIdportaDestinatario(),itemImgSaved_.getIdportaDestinatario()) &&
												org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getTipoDestinatario(),itemImgSaved_.getTipoDestinatario()) &&
												org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getNomeDestinatario(),itemImgSaved_.getNomeDestinatario()) &&
												org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getTipoServizio(),itemImgSaved_.getTipoServizio()) &&
												org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getNomeServizio(),itemImgSaved_.getNomeServizio()) &&
												org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getVersioneServizio(),itemImgSaved_.getVersioneServizio()) &&
												org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getAzione(),itemImgSaved_.getAzione());
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
	public Plugin get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._get(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId), idMappingResolutionBehaviour);
	}
	
	private Plugin _get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
				
		Plugin plugin = new Plugin();
		

		// Object plugin
		ISQLQueryObject sqlQueryObjectGet_plugin = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_plugin.setANDLogicOperator(true);
		sqlQueryObjectGet_plugin.addFromTable(this.getPluginFieldConverter().toTable(Plugin.model()));
		sqlQueryObjectGet_plugin.addSelectField("id");
		sqlQueryObjectGet_plugin.addSelectField(this.getPluginFieldConverter().toColumn(Plugin.model().TIPO,true));
		sqlQueryObjectGet_plugin.addSelectField(this.getPluginFieldConverter().toColumn(Plugin.model().CLASS_NAME,true));
		sqlQueryObjectGet_plugin.addSelectField(this.getPluginFieldConverter().toColumn(Plugin.model().DESCRIZIONE,true));
		sqlQueryObjectGet_plugin.addSelectField(this.getPluginFieldConverter().toColumn(Plugin.model().LABEL,true));
		sqlQueryObjectGet_plugin.addWhereCondition("id=?");

		// Get plugin
		plugin = (Plugin) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_plugin.createSQLQuery(), jdbcProperties.isShowSql(), Plugin.model(), this.getPluginFetch(),
			new JDBCObject(tableId,Long.class));



		// Object plugin_pluginServizioCompatibilita
		ISQLQueryObject sqlQueryObjectGet_plugin_pluginServizioCompatibilita = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_plugin_pluginServizioCompatibilita.setANDLogicOperator(true);
		sqlQueryObjectGet_plugin_pluginServizioCompatibilita.addFromTable(this.getPluginFieldConverter().toTable(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA));
		sqlQueryObjectGet_plugin_pluginServizioCompatibilita.addSelectField("id");
		sqlQueryObjectGet_plugin_pluginServizioCompatibilita.addSelectField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.URI_ACCORDO,true));
		sqlQueryObjectGet_plugin_pluginServizioCompatibilita.addSelectField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.SERVIZIO,true));
		sqlQueryObjectGet_plugin_pluginServizioCompatibilita.addWhereCondition("id_plugin=?");

		// Get plugin_pluginServizioCompatibilita
		java.util.List<Object> plugin_pluginServizioCompatibilita_list = (java.util.List<Object>) jdbcUtilities.executeQuery(sqlQueryObjectGet_plugin_pluginServizioCompatibilita.createSQLQuery(), jdbcProperties.isShowSql(), Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA, this.getPluginFetch(),
			new JDBCObject(plugin.getId(),Long.class));

		if(plugin_pluginServizioCompatibilita_list != null) {
			for (Object plugin_pluginServizioCompatibilita_object: plugin_pluginServizioCompatibilita_list) {
				PluginServizioCompatibilita plugin_pluginServizioCompatibilita = (PluginServizioCompatibilita) plugin_pluginServizioCompatibilita_object;



				// Object plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita
				ISQLQueryObject sqlQueryObjectGet_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita = sqlQueryObjectGet.newSQLQueryObject();
				sqlQueryObjectGet_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.setANDLogicOperator(true);
				sqlQueryObjectGet_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.addFromTable(this.getPluginFieldConverter().toTable(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA));
				sqlQueryObjectGet_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.addSelectField("id");
				sqlQueryObjectGet_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.addSelectField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA.AZIONE,true));
				sqlQueryObjectGet_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.addWhereCondition("id_plugin_servizio_comp=?");

				// Get plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita
				java.util.List<Object> plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_list = (java.util.List<Object>) jdbcUtilities.executeQuery(sqlQueryObjectGet_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.createSQLQuery(), jdbcProperties.isShowSql(), Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA, this.getPluginFetch(),
					new JDBCObject(plugin_pluginServizioCompatibilita.getId(),Long.class));

				if(plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_list != null) {
					for (Object plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_object: plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_list) {
						PluginServizioAzioneCompatibilita plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita = (PluginServizioAzioneCompatibilita) plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_object;


						plugin_pluginServizioCompatibilita.addPluginServizioAzioneCompatibilita(plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita);
					}
				}
				plugin.addPluginServizioCompatibilita(plugin_pluginServizioCompatibilita);
			}
		}

		// Object plugin_pluginFiltroCompatibilita
		ISQLQueryObject sqlQueryObjectGet_plugin_pluginFiltroCompatibilita = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_plugin_pluginFiltroCompatibilita.setANDLogicOperator(true);
		sqlQueryObjectGet_plugin_pluginFiltroCompatibilita.addFromTable(this.getPluginFieldConverter().toTable(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA));
		sqlQueryObjectGet_plugin_pluginFiltroCompatibilita.addSelectField("id");
		sqlQueryObjectGet_plugin_pluginFiltroCompatibilita.addSelectField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_MITTENTE,true));
		sqlQueryObjectGet_plugin_pluginFiltroCompatibilita.addSelectField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_MITTENTE,true));
		sqlQueryObjectGet_plugin_pluginFiltroCompatibilita.addSelectField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.IDPORTA_MITTENTE,true));
		sqlQueryObjectGet_plugin_pluginFiltroCompatibilita.addSelectField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_DESTINATARIO,true));
		sqlQueryObjectGet_plugin_pluginFiltroCompatibilita.addSelectField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_DESTINATARIO,true));
		sqlQueryObjectGet_plugin_pluginFiltroCompatibilita.addSelectField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.IDPORTA_DESTINATARIO,true));
		sqlQueryObjectGet_plugin_pluginFiltroCompatibilita.addSelectField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_SERVIZIO,true));
		sqlQueryObjectGet_plugin_pluginFiltroCompatibilita.addSelectField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_SERVIZIO,true));
		sqlQueryObjectGet_plugin_pluginFiltroCompatibilita.addSelectField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.VERSIONE_SERVIZIO,true));
		sqlQueryObjectGet_plugin_pluginFiltroCompatibilita.addSelectField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.AZIONE,true));
		sqlQueryObjectGet_plugin_pluginFiltroCompatibilita.addWhereCondition("id_plugin=?");

		// Get plugin_pluginFiltroCompatibilita
		java.util.List<Object> plugin_pluginFiltroCompatibilita_list = (java.util.List<Object>) jdbcUtilities.executeQuery(sqlQueryObjectGet_plugin_pluginFiltroCompatibilita.createSQLQuery(), jdbcProperties.isShowSql(), Plugin.model().PLUGIN_FILTRO_COMPATIBILITA, this.getPluginFetch(),
			new JDBCObject(plugin.getId(),Long.class));

		if(plugin_pluginFiltroCompatibilita_list != null) {
			for (Object plugin_pluginFiltroCompatibilita_object: plugin_pluginFiltroCompatibilita_list) {
				PluginFiltroCompatibilita plugin_pluginFiltroCompatibilita = (PluginFiltroCompatibilita) plugin_pluginFiltroCompatibilita_object;


				plugin.addPluginFiltroCompatibilita(plugin_pluginFiltroCompatibilita);
			}
		}

		
        return plugin;  
	
	} 
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._exists(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	private boolean _exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		boolean existsPlugin = false;

		sqlQueryObject = sqlQueryObject.newSQLQueryObject();
		sqlQueryObject.setANDLogicOperator(true);

		sqlQueryObject.addFromTable(this.getPluginFieldConverter().toTable(Plugin.model()));
		sqlQueryObject.addSelectField(this.getPluginFieldConverter().toColumn(Plugin.model().TIPO,true));
		sqlQueryObject.addWhereCondition("id=?");


		// Exists plugin
		existsPlugin = jdbcUtilities.exists(sqlQueryObject.createSQLQuery(), jdbcProperties.isShowSql(),
			new JDBCObject(tableId,Long.class));

		
        return existsPlugin;
	
	}
	
	private void _join(IExpression expression, ISQLQueryObject sqlQueryObject) throws NotImplementedException, ServiceException, Exception{
	
		if(expression.inUseModel(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA,false)){
			String tableName1 = this.getPluginFieldConverter().toAliasTable(Plugin.model());
			String tableName2 = this.getPluginFieldConverter().toAliasTable(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA);
			sqlQueryObject.addWhereCondition(tableName1+".id="+tableName2+".id_plugin");
		}
		if(expression.inUseModel(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA,false)){
			String tableName1 = this.getPluginFieldConverter().toAliasTable(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA);
			String tableName2 = this.getPluginFieldConverter().toAliasTable(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA);
			sqlQueryObject.addWhereCondition(tableName1+".id="+tableName2+".id_plugin_servizio_comp");
		}
		if(expression.inUseModel(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA,false)){
			String tableName1 = this.getPluginFieldConverter().toAliasTable(Plugin.model());
			String tableName2 = this.getPluginFieldConverter().toAliasTable(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA);
			sqlQueryObject.addWhereCondition(tableName1+".id="+tableName2+".id_plugin");
		}
		
        if(expression.inUseModel(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA,false)){
			if(expression.inUseModel(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA,false)==false){
				sqlQueryObject.addFromTable(this.getPluginFieldConverter().toTable(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA));
			}
		}
        
	}
	
	protected java.util.List<Object> _getRootTablePrimaryKeyValues(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPlugin id) throws NotFoundException, ServiceException, NotImplementedException, Exception{
	    // Identificativi
        java.util.List<Object> rootTableIdValues = new java.util.ArrayList<Object>();
        Long longId = this.findIdPlugin(jdbcProperties, log, connection, sqlQueryObject.newSQLQueryObject(), id, true);
		rootTableIdValues.add(longId);
        
        return rootTableIdValues;
	}
	
	protected Map<String, List<IField>> _getMapTableToPKColumn() throws NotImplementedException, Exception{
	
		PluginFieldConverter converter = this.getPluginFieldConverter();
		Map<String, List<IField>> mapTableToPKColumn = new java.util.Hashtable<String, List<IField>>();
		UtilsTemplate<IField> utilities = new UtilsTemplate<IField>();

		// Plugin.model()
		mapTableToPKColumn.put(converter.toTable(Plugin.model()),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Plugin.model()))
			));

		// Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA
		mapTableToPKColumn.put(converter.toTable(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA))
			));

		// Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA
		mapTableToPKColumn.put(converter.toTable(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA))
			));

		// Plugin.model().PLUGIN_FILTRO_COMPATIBILITA
		mapTableToPKColumn.put(converter.toTable(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA))
			));
        
        return mapTableToPKColumn;		
	}
	
	@Override
	public List<Long> findAllTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression paginatedExpression) throws ServiceException, NotImplementedException, Exception {
		
		List<Long> list = new ArrayList<Long>();

		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getPluginFieldConverter().toTable(Plugin.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFindAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
												this.getPluginFieldConverter(), Plugin.model());
		
		_join(paginatedExpression,sqlQueryObject);
		
		List<Object> listObjects = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.findAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
																			this.getPluginFieldConverter(), Plugin.model(), objectIdClass, listaQuery);
		for(Object object: listObjects) {
			list.add((Long)object);
		}

        return list;
		
	}
	
	@Override
	public long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, Exception {
	
		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getPluginFieldConverter().toTable(Plugin.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFind(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getPluginFieldConverter(), Plugin.model());
		
		_join(expression,sqlQueryObject);

		Object res = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.find(jdbcProperties, log, connection, sqlQueryObject, expression,
														this.getPluginFieldConverter(), Plugin.model(), objectIdClass, listaQuery);
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
	public IdPlugin findId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();              

		// Object _plugin
		sqlQueryObjectGet.addFromTable(this.getPluginFieldConverter().toTable(Plugin.model()));
		sqlQueryObjectGet.addSelectField(this.getPluginFieldConverter().toColumn(Plugin.model().TIPO,true));
		sqlQueryObjectGet.addSelectField(this.getPluginFieldConverter().toColumn(Plugin.model().CLASS_NAME,true));
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addWhereCondition("id=?");

		// Recupero _plugin
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_plugin = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(tableId,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_plugin = new ArrayList<Class<?>>();
		listaFieldIdReturnType_plugin.add(String.class);
		listaFieldIdReturnType_plugin.add(String.class);
		org.openspcoop2.monitor.engine.config.base.IdPlugin id_plugin = null;
		List<Object> listaFieldId_plugin = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_plugin, searchParams_plugin);
		if(listaFieldId_plugin==null || listaFieldId_plugin.size()<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		else{
			// set _plugin
			id_plugin = new org.openspcoop2.monitor.engine.config.base.IdPlugin();
			id_plugin.setTipo(TipoPlugin.toEnumConstant((String)listaFieldId_plugin.get(0)));
			id_plugin.setClassName((String)listaFieldId_plugin.get(1));
		}
		
		return id_plugin;
		
	}

	@Override
	public Long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPlugin id, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
	
		return this.findIdPlugin(jdbcProperties,log,connection,sqlQueryObject,id,throwNotFound);
			
	}
	
	@Override
	public List<List<Object>> nativeQuery(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
											String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException,Exception{
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.nativeQuery(jdbcProperties, log, connection, sqlQueryObject,
																							sql,returnClassTypes,param);
														
	}
	
	protected Long findIdPlugin(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPlugin id, boolean throwNotFound) throws NotFoundException, ServiceException, NotImplementedException, Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();

        
		// Object _plugin
		sqlQueryObjectGet.addFromTable(this.getPluginFieldConverter().toTable(Plugin.model()));
		sqlQueryObjectGet.addSelectField("id");
		// Devono essere mappati nella where condition i metodi dell'oggetto id.getXXX
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.setSelectDistinct(true);
		sqlQueryObjectGet.addWhereCondition(this.getPluginFieldConverter().toColumn(Plugin.model().TIPO,true)+"=?");
		sqlQueryObjectGet.addWhereCondition(this.getPluginFieldConverter().toColumn(Plugin.model().CLASS_NAME,true)+"=?");

		// Recupero _plugin
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_plugin = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id.getTipo().getValue(),String.class),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id.getClassName(),String.class)
		};
		Long id_plugin = null;
		try{
			id_plugin = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
						Long.class, searchParams_plugin);
		}catch(NotFoundException notFound){
			if(throwNotFound){
				throw new NotFoundException(notFound);
			}
		}
		if(id_plugin==null || id_plugin<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		
		return id_plugin;
	}
}
