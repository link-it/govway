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

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.search.IdPortaApplicativa;
import org.openspcoop2.core.commons.search.IdServizioApplicativo;
import org.openspcoop2.core.commons.search.IdSoggetto;
import org.openspcoop2.core.commons.search.PortaApplicativa;
import org.openspcoop2.core.commons.search.PortaApplicativaAzione;
import org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.commons.search.dao.IDBSoggettoServiceSearch;
import org.openspcoop2.core.commons.search.dao.jdbc.converter.PortaApplicativaFieldConverter;
import org.openspcoop2.core.commons.search.dao.jdbc.fetch.PortaApplicativaFetch;
import org.openspcoop2.core.commons.search.utils.ExpressionProperties;
import org.openspcoop2.generic_project.beans.AliasField;
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
 * JDBCPortaApplicativaServiceSearchImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCPortaApplicativaServiceSearchImpl implements IJDBCServiceSearchWithId<PortaApplicativa, IdPortaApplicativa, JDBCServiceManager> {

	private PortaApplicativaFieldConverter _portaApplicativaFieldConverter = null;
	public PortaApplicativaFieldConverter getPortaApplicativaFieldConverter() {
		if(this._portaApplicativaFieldConverter==null){
			this._portaApplicativaFieldConverter = new PortaApplicativaFieldConverter(this.jdbcServiceManager.getJdbcProperties().getDatabaseType());
		}		
		return this._portaApplicativaFieldConverter;
	}
	@Override
	public ISQLFieldConverter getFieldConverter() {
		return this.getPortaApplicativaFieldConverter();
	}
	
	private PortaApplicativaFetch portaApplicativaFetch = new PortaApplicativaFetch();
	public PortaApplicativaFetch getPortaApplicativaFetch() {
		return this.portaApplicativaFetch;
	}
	@Override
	public IJDBCFetch getFetch() {
		return getPortaApplicativaFetch();
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
	public IdPortaApplicativa convertToId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, PortaApplicativa portaApplicativa) throws NotImplementedException, ServiceException, Exception{
	
		IdPortaApplicativa idPortaApplicativa = new IdPortaApplicativa();
		idPortaApplicativa.setNome(portaApplicativa.getNome());
		return idPortaApplicativa;
	}
	
	@Override
	public PortaApplicativa get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPortaApplicativa id, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {
		Long id_portaApplicativa = ( (id!=null && id.getId()!=null && id.getId()>0) ? id.getId() : this.findIdPortaApplicativa(jdbcProperties, log, connection, sqlQueryObject, id, true));
		return this._get(jdbcProperties, log, connection, sqlQueryObject, id_portaApplicativa,idMappingResolutionBehaviour);
		
		
	}
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPortaApplicativa id) throws MultipleResultException, NotImplementedException, ServiceException,Exception {

		Long id_portaApplicativa = this.findIdPortaApplicativa(jdbcProperties, log, connection, sqlQueryObject, id, false);
		return id_portaApplicativa != null && id_portaApplicativa > 0;
		
	}
	
	@Override
	public List<IdPortaApplicativa> findAllIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

		List<IdPortaApplicativa> list = new ArrayList<IdPortaApplicativa>();

		// TODO: implementazione non efficente. 
		// Per ottenere una implementazione efficente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari a create l'ID logico
		// 2. Usare metodo getPortaApplicativaFetch() sul risultato della select per ottenere un oggetto PortaApplicativa
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 
		// 3. Usare metodo convertToId per ottenere l'id

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	PortaApplicativa portaApplicativa = this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour);
			IdPortaApplicativa idPortaApplicativa = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,portaApplicativa);
        	list.add(idPortaApplicativa);
        }

        return list;
		
	}
	
	@Override
	public List<PortaApplicativa> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

        List<PortaApplicativa> list = new ArrayList<PortaApplicativa>();
        
        boolean soloDatiIdentificativiServizio = ExpressionProperties.isEnabledSoloDatiIdentificativiServizio(expression); 
        
        if(soloDatiIdentificativiServizio){
        	
        	List<IField> fields = new ArrayList<IField>();
    		fields.add(PortaApplicativa.model().NOME);
    		fields.add(PortaApplicativa.model().STATO);
    		fields.add(PortaApplicativa.model().TIPO_SERVIZIO);
    		fields.add(PortaApplicativa.model().NOME_SERVIZIO);
    		fields.add(PortaApplicativa.model().VERSIONE_SERVIZIO);
    		fields.add(PortaApplicativa.model().MODE_AZIONE);
    		fields.add(PortaApplicativa.model().NOME_AZIONE);
    		fields.add(PortaApplicativa.model().NOME_PORTA_DELEGANTE_AZIONE);
    		    		
    		String aliasSoggettoTipo = "proprietarioSoggettoTipo";
    		fields.add(new AliasField(PortaApplicativa.model().ID_SOGGETTO.TIPO, aliasSoggettoTipo));
    		String aliasSoggettoNome = "proprietarioSoggettoNome";
    		fields.add(new AliasField(PortaApplicativa.model().ID_SOGGETTO.NOME, aliasSoggettoNome));
        	
    		List<Map<String, Object>> returnMap = null;
    		try{
    			// non usare true altrimenti non funzionano alcuni meccanismi di ricerca, ad es. la valorizzazione dei select field nel servizio della govwayMonitor.
    			// Tanto le join non comportano righe multiple uguali
    			boolean distinct = false; 
    			returnMap = this.select(jdbcProperties, log, connection, sqlQueryObject, expression, distinct, fields.toArray(new IField[1]));
    			
	    		for(Map<String, Object> map: returnMap) {
	    			
	    			PortaApplicativa pa = (PortaApplicativa) this.getPortaApplicativaFetch().fetch(jdbcProperties.getDatabase(), PortaApplicativa.model(), map);
	    			
	    			Object proprietarioSoggettoTipo = this.getObjectFromMap(map, aliasSoggettoTipo);
	    			Object proprietarioSoggettoNome = this.getObjectFromMap(map, aliasSoggettoNome);
	    			if(proprietarioSoggettoTipo!=null && proprietarioSoggettoNome!=null) {
    					IdSoggetto idSoggetto = new IdSoggetto();
    					if(proprietarioSoggettoTipo!=null && proprietarioSoggettoTipo instanceof String) {
    						idSoggetto.setTipo((String) proprietarioSoggettoTipo);
	    				}
    					if(proprietarioSoggettoNome!=null && proprietarioSoggettoNome instanceof String) {
    						idSoggetto.setNome((String) proprietarioSoggettoNome);
	    				}
    					pa.setIdSoggetto(idSoggetto);
    				} 			
	    			
	    			list.add(pa);
	    		}
    		}catch(NotFoundException notFound){}
        }
        else {
        
	        // TODO: implementazione non efficente. 
			// Per ottenere una implementazione efficente:
			// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari
			// 2. Usare metodo getPortaApplicativaFetch() sul risultato della select per ottenere un oggetto PortaApplicativa
			//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 
	
	        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
	        
	        for(Long id: ids) {
	        	list.add(this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour));
	        }
	        
        }

        return list;      
		
	}
	
	private Object getObjectFromMap(Map<String,Object> map,String name){
		if(map==null){
			return null;
		}
		else if(map.containsKey(name)){
			Object o = map.get(name);
			if(o instanceof org.apache.commons.lang.ObjectUtils.Null){
				return null;
			}
			else{
				return o;
			}
		}
		else{
			return null;
		}
	}
	
	@Override
	public PortaApplicativa find(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) 
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
												this.getPortaApplicativaFieldConverter(), PortaApplicativa.model());
		
		sqlQueryObject.addSelectCountField(this.getPortaApplicativaFieldConverter().toTable(PortaApplicativa.model())+".id","tot",true);
		
		_join(expression,sqlQueryObject);
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.count(jdbcProperties, log, connection, sqlQueryObject, expression,
																			this.getPortaApplicativaFieldConverter(), PortaApplicativa.model(),listaQuery);
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPortaApplicativa id) throws NotFoundException, NotImplementedException, ServiceException,Exception {
		
		Long id_portaApplicativa = this.findIdPortaApplicativa(jdbcProperties, log, connection, sqlQueryObject, id, true);
        return this._inUse(jdbcProperties, log, connection, sqlQueryObject, id_portaApplicativa);
		
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
												this.getPortaApplicativaFieldConverter(), field);

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
        						expression, this.getPortaApplicativaFieldConverter(), PortaApplicativa.model(), 
        						listaQuery,listaParams);
		
		_join(expression,sqlQueryObject);
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.select(jdbcProperties, log, connection,
        								org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareSqlQueryObjectForSelectDistinct(sqlQueryObject,sqlQueryObjectDistinct), 
        								expression, this.getPortaApplicativaFieldConverter(), PortaApplicativa.model(),
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
        						this.getPortaApplicativaFieldConverter(), PortaApplicativa.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.union(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getPortaApplicativaFieldConverter(), PortaApplicativa.model(), 
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
        						this.getPortaApplicativaFieldConverter(), PortaApplicativa.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        NonNegativeNumber number = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.unionCount(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getPortaApplicativaFieldConverter(), PortaApplicativa.model(), 
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
			return new JDBCExpression(this.getPortaApplicativaFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}


	@Override
	public JDBCPaginatedExpression newPaginatedExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(this.getPortaApplicativaFieldConverter());
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
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPortaApplicativa id, PortaApplicativa obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,id,null));
	}
	
	@Override
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, PortaApplicativa obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,tableId,null));
	}
	private void _mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, PortaApplicativa obj, PortaApplicativa imgSaved) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		if(imgSaved==null){
			return;
		}
		obj.setId(imgSaved.getId());
		if(obj.getIdSoggetto()!=null && 
				imgSaved.getIdSoggetto()!=null){
			obj.getIdSoggetto().setId(imgSaved.getIdSoggetto().getId());
		}
		if(obj.getPortaApplicativaServizioApplicativoList()!=null){
			List<org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo> listObj_ = obj.getPortaApplicativaServizioApplicativoList();
			for(org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo itemObj_ : listObj_){
				org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo itemAlreadySaved_ = null;
				if(imgSaved.getPortaApplicativaServizioApplicativoList()!=null){
					List<org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo> listImgSaved_ = imgSaved.getPortaApplicativaServizioApplicativoList();
					for(org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo itemImgSaved_ : listImgSaved_){
						boolean objEqualsToImgSaved_ = false;
						if(itemObj_.getIdServizioApplicativo()!=null && itemObj_.getIdServizioApplicativo().getIdSoggetto()!=null &&
								itemImgSaved_.getIdServizioApplicativo()!=null && itemImgSaved_.getIdServizioApplicativo().getIdSoggetto()!=null){
							objEqualsToImgSaved_ = org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getIdServizioApplicativo().getNome(),itemImgSaved_.getIdServizioApplicativo().getNome()) &&
													 			org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getIdServizioApplicativo().getIdSoggetto().getTipo(),itemImgSaved_.getIdServizioApplicativo().getIdSoggetto().getTipo()) &&
													 			org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getIdServizioApplicativo().getIdSoggetto().getNome(),itemImgSaved_.getIdServizioApplicativo().getIdSoggetto().getNome());
						}
						if(objEqualsToImgSaved_){
							itemAlreadySaved_=itemImgSaved_;
							break;
						}
					}
				}
				if(itemAlreadySaved_!=null){
					itemObj_.setId(itemAlreadySaved_.getId());
					if(itemObj_.getIdServizioApplicativo()!=null && 
							itemAlreadySaved_.getIdServizioApplicativo()!=null){
						itemObj_.getIdServizioApplicativo().setId(itemAlreadySaved_.getIdServizioApplicativo().getId());
						if(itemObj_.getIdServizioApplicativo().getIdSoggetto()!=null && 
								itemAlreadySaved_.getIdServizioApplicativo().getIdSoggetto()!=null){
							itemObj_.getIdServizioApplicativo().getIdSoggetto().setId(itemAlreadySaved_.getIdServizioApplicativo().getIdSoggetto().getId());
						}
					}
				}
			}
		}
		if(obj.getPortaApplicativaAzioneList()!=null){
			List<org.openspcoop2.core.commons.search.PortaApplicativaAzione> listObj_ = obj.getPortaApplicativaAzioneList();
			for(org.openspcoop2.core.commons.search.PortaApplicativaAzione itemObj_ : listObj_){
				org.openspcoop2.core.commons.search.PortaApplicativaAzione itemAlreadySaved_ = null;
				if(imgSaved.getPortaApplicativaAzioneList()!=null){
					List<org.openspcoop2.core.commons.search.PortaApplicativaAzione> listImgSaved_ = imgSaved.getPortaApplicativaAzioneList();
					for(org.openspcoop2.core.commons.search.PortaApplicativaAzione itemImgSaved_ : listImgSaved_){
						boolean objEqualsToImgSaved_ = false;
						objEqualsToImgSaved_ = org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getNome(),itemImgSaved_.getNome());
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
	public PortaApplicativa get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._get(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId), idMappingResolutionBehaviour);
	}
	
	private PortaApplicativa _get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
				
		PortaApplicativa portaApplicativa = new PortaApplicativa();
		

		// Object portaApplicativa
		ISQLQueryObject sqlQueryObjectGet_portaApplicativa = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_portaApplicativa.setANDLogicOperator(true);
		sqlQueryObjectGet_portaApplicativa.addFromTable(this.getPortaApplicativaFieldConverter().toTable(PortaApplicativa.model()));
		sqlQueryObjectGet_portaApplicativa.addSelectField("id");
		sqlQueryObjectGet_portaApplicativa.addSelectField(this.getPortaApplicativaFieldConverter().toColumn(PortaApplicativa.model().NOME,true));
		sqlQueryObjectGet_portaApplicativa.addSelectField(this.getPortaApplicativaFieldConverter().toColumn(PortaApplicativa.model().STATO,true));
		sqlQueryObjectGet_portaApplicativa.addSelectField(this.getPortaApplicativaFieldConverter().toColumn(PortaApplicativa.model().TIPO_SERVIZIO,true));
		sqlQueryObjectGet_portaApplicativa.addSelectField(this.getPortaApplicativaFieldConverter().toColumn(PortaApplicativa.model().NOME_SERVIZIO,true));
		sqlQueryObjectGet_portaApplicativa.addSelectField(this.getPortaApplicativaFieldConverter().toColumn(PortaApplicativa.model().VERSIONE_SERVIZIO,true));
		sqlQueryObjectGet_portaApplicativa.addSelectField(this.getPortaApplicativaFieldConverter().toColumn(PortaApplicativa.model().MODE_AZIONE,true));
		sqlQueryObjectGet_portaApplicativa.addSelectField(this.getPortaApplicativaFieldConverter().toColumn(PortaApplicativa.model().NOME_AZIONE,true));
		sqlQueryObjectGet_portaApplicativa.addSelectField(this.getPortaApplicativaFieldConverter().toColumn(PortaApplicativa.model().NOME_PORTA_DELEGANTE_AZIONE,true));
		sqlQueryObjectGet_portaApplicativa.addSelectField(this.getPortaApplicativaFieldConverter().toColumn(PortaApplicativa.model().CANALE,true));
		sqlQueryObjectGet_portaApplicativa.addWhereCondition("id=?");

		// Get portaApplicativa
		portaApplicativa = (PortaApplicativa) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_portaApplicativa.createSQLQuery(), jdbcProperties.isShowSql(), PortaApplicativa.model(), this.getPortaApplicativaFetch(),
			new JDBCObject(tableId,Long.class));


		// Object _portaApplicativa_soggetto (recupero id)
		ISQLQueryObject sqlQueryObjectGet_portaApplicativa_soggetto_readFkId = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_portaApplicativa_soggetto_readFkId.addFromTable(this.getPortaApplicativaFieldConverter().toTable(org.openspcoop2.core.commons.search.PortaApplicativa.model()));
		sqlQueryObjectGet_portaApplicativa_soggetto_readFkId.addSelectField("id_soggetto");
		sqlQueryObjectGet_portaApplicativa_soggetto_readFkId.addWhereCondition("id=?");
		sqlQueryObjectGet_portaApplicativa_soggetto_readFkId.setANDLogicOperator(true);
		Long idFK_portaApplicativa_soggetto = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_portaApplicativa_soggetto_readFkId.createSQLQuery(), jdbcProperties.isShowSql(),Long.class,
				new JDBCObject(portaApplicativa.getId(),Long.class));
		
		// Object _portaApplicativa_soggetto
		ISQLQueryObject sqlQueryObjectGet_portaApplicativa_soggetto = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_portaApplicativa_soggetto.addFromTable("soggetti");
		sqlQueryObjectGet_portaApplicativa_soggetto.addSelectField("tipo_soggetto");
		sqlQueryObjectGet_portaApplicativa_soggetto.addSelectField("nome_soggetto");
		sqlQueryObjectGet_portaApplicativa_soggetto.setANDLogicOperator(true);
		sqlQueryObjectGet_portaApplicativa_soggetto.addWhereCondition("id=?");

		// Recupero _portaApplicativa_soggetto
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_portaApplicativa_soggetto = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(idFK_portaApplicativa_soggetto,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_portaApplicativa_soggetto = new ArrayList<Class<?>>();
		listaFieldIdReturnType_portaApplicativa_soggetto.add(String.class);
		listaFieldIdReturnType_portaApplicativa_soggetto.add(String.class);
		List<Object> listaFieldId_portaApplicativa_soggetto = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_portaApplicativa_soggetto.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_portaApplicativa_soggetto, searchParams_portaApplicativa_soggetto);
		IdSoggetto idSoggetto = new IdSoggetto();
		idSoggetto.setTipo((String)listaFieldId_portaApplicativa_soggetto.get(0));
		idSoggetto.setNome((String)listaFieldId_portaApplicativa_soggetto.get(1));
		portaApplicativa.setIdSoggetto(idSoggetto);


		// Object portaApplicativa_portaApplicativaServizioApplicativo
		ISQLQueryObject sqlQueryObjectGet_portaApplicativa_portaApplicativaServizioApplicativo = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_portaApplicativa_portaApplicativaServizioApplicativo.setANDLogicOperator(true);
		sqlQueryObjectGet_portaApplicativa_portaApplicativaServizioApplicativo.addFromTable(this.getPortaApplicativaFieldConverter().toTable(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO));
		sqlQueryObjectGet_portaApplicativa_portaApplicativaServizioApplicativo.addSelectField("id");
		sqlQueryObjectGet_portaApplicativa_portaApplicativaServizioApplicativo.addWhereCondition("id_porta=?");

		// Get portaApplicativa_portaApplicativaServizioApplicativo
		java.util.List<Object> portaApplicativa_portaApplicativaServizioApplicativo_list = jdbcUtilities.executeQuery(sqlQueryObjectGet_portaApplicativa_portaApplicativaServizioApplicativo.createSQLQuery(), jdbcProperties.isShowSql(), PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO, this.getPortaApplicativaFetch(),
			new JDBCObject(portaApplicativa.getId(),Long.class));

		if(portaApplicativa_portaApplicativaServizioApplicativo_list != null) {
			for (Object portaApplicativa_portaApplicativaServizioApplicativo_object: portaApplicativa_portaApplicativaServizioApplicativo_list) {
				PortaApplicativaServizioApplicativo portaApplicativa_portaApplicativaServizioApplicativo = (PortaApplicativaServizioApplicativo) portaApplicativa_portaApplicativaServizioApplicativo_object;


				// Object _portaApplicativa_portaApplicativaServizioApplicativo_servizioApplicativo (recupero id)
				ISQLQueryObject sqlQueryObjectGet_portaApplicativa_portaApplicativaServizioApplicativo_servizioApplicativo_readFkId = sqlQueryObjectGet.newSQLQueryObject();
				sqlQueryObjectGet_portaApplicativa_portaApplicativaServizioApplicativo_servizioApplicativo_readFkId.addFromTable(this.getPortaApplicativaFieldConverter().toTable(org.openspcoop2.core.commons.search.PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO));
				sqlQueryObjectGet_portaApplicativa_portaApplicativaServizioApplicativo_servizioApplicativo_readFkId.addSelectField("id_servizio_applicativo");
				sqlQueryObjectGet_portaApplicativa_portaApplicativaServizioApplicativo_servizioApplicativo_readFkId.addWhereCondition("id=?");
				sqlQueryObjectGet_portaApplicativa_portaApplicativaServizioApplicativo_servizioApplicativo_readFkId.setANDLogicOperator(true);
				Long idFK_portaApplicativa_portaApplicativaServizioApplicativo_servizioApplicativo = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_portaApplicativa_portaApplicativaServizioApplicativo_servizioApplicativo_readFkId.createSQLQuery(), jdbcProperties.isShowSql(),Long.class,
						new JDBCObject(portaApplicativa_portaApplicativaServizioApplicativo.getId(),Long.class));
				
				// Object _portaApplicativa_portaApplicativaServizioApplicativo_servizioApplicativo
				ISQLQueryObject sqlQueryObjectGet_portaApplicativa_portaApplicativaServizioApplicativo_servizioApplicativo = sqlQueryObjectGet.newSQLQueryObject();
				sqlQueryObjectGet_portaApplicativa_portaApplicativaServizioApplicativo_servizioApplicativo.addFromTable("servizi_applicativi");
				sqlQueryObjectGet_portaApplicativa_portaApplicativaServizioApplicativo_servizioApplicativo.addSelectField("nome");
				sqlQueryObjectGet_portaApplicativa_portaApplicativaServizioApplicativo_servizioApplicativo.addSelectField("id_soggetto");
				sqlQueryObjectGet_portaApplicativa_portaApplicativaServizioApplicativo_servizioApplicativo.setANDLogicOperator(true);
				sqlQueryObjectGet_portaApplicativa_portaApplicativaServizioApplicativo_servizioApplicativo.addWhereCondition("id=?");

				// Recupero _portaApplicativa_portaApplicativaServizioApplicativo_servizioApplicativo
				org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_portaApplicativa_portaApplicativaServizioApplicativo_servizioApplicativo = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(idFK_portaApplicativa_portaApplicativaServizioApplicativo_servizioApplicativo,Long.class)
				};
				List<Class<?>> listaFieldIdReturnType_portaApplicativa_portaApplicativaServizioApplicativo_servizioApplicativo = new ArrayList<Class<?>>();
				listaFieldIdReturnType_portaApplicativa_portaApplicativaServizioApplicativo_servizioApplicativo.add(String.class);
				listaFieldIdReturnType_portaApplicativa_portaApplicativaServizioApplicativo_servizioApplicativo.add(Long.class);
				List<Object> listaFieldId_portaApplicativa_portaApplicativaServizioApplicativo_servizioApplicativo = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_portaApplicativa_portaApplicativaServizioApplicativo_servizioApplicativo.createSQLQuery(), jdbcProperties.isShowSql(),
						listaFieldIdReturnType_portaApplicativa_portaApplicativaServizioApplicativo_servizioApplicativo, searchParams_portaApplicativa_portaApplicativaServizioApplicativo_servizioApplicativo);
				IdServizioApplicativo idServizioApplicativo = new IdServizioApplicativo();
				idServizioApplicativo.setNome((String)listaFieldId_portaApplicativa_portaApplicativaServizioApplicativo_servizioApplicativo.get(0));
				Long idSoggettoLong = (Long) listaFieldId_portaApplicativa_portaApplicativaServizioApplicativo_servizioApplicativo.get(1);
				IdSoggetto idSoggettoSA = this.jdbcServiceManager.getSoggettoServiceSearch().convertToId(
						((IDBSoggettoServiceSearch)this.jdbcServiceManager.getSoggettoServiceSearch()).get(idSoggettoLong)
						);
				idServizioApplicativo.setIdSoggetto(idSoggettoSA);
				portaApplicativa_portaApplicativaServizioApplicativo.setIdServizioApplicativo(idServizioApplicativo);
				
				portaApplicativa.addPortaApplicativaServizioApplicativo(portaApplicativa_portaApplicativaServizioApplicativo);
			}
		}

		// Object portaApplicativa_portaApplicativaAzione
		ISQLQueryObject sqlQueryObjectGet_portaApplicativa_portaApplicativaAzione = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_portaApplicativa_portaApplicativaAzione.setANDLogicOperator(true);
		sqlQueryObjectGet_portaApplicativa_portaApplicativaAzione.addFromTable(this.getPortaApplicativaFieldConverter().toTable(PortaApplicativa.model().PORTA_APPLICATIVA_AZIONE));
		sqlQueryObjectGet_portaApplicativa_portaApplicativaAzione.addSelectField("id");
		sqlQueryObjectGet_portaApplicativa_portaApplicativaAzione.addSelectField(this.getPortaApplicativaFieldConverter().toColumn(PortaApplicativa.model().PORTA_APPLICATIVA_AZIONE.NOME,true));
		sqlQueryObjectGet_portaApplicativa_portaApplicativaAzione.addWhereCondition("id_porta=?");

		// Get portaApplicativa_portaApplicativaAzione
		java.util.List<Object> portaApplicativa_portaApplicativaAzione_list = jdbcUtilities.executeQuery(sqlQueryObjectGet_portaApplicativa_portaApplicativaAzione.createSQLQuery(), jdbcProperties.isShowSql(), PortaApplicativa.model().PORTA_APPLICATIVA_AZIONE, this.getPortaApplicativaFetch(),
			new JDBCObject(portaApplicativa.getId(),Long.class));

		if(portaApplicativa_portaApplicativaAzione_list != null) {
			for (Object portaApplicativa_portaApplicativaAzione_object: portaApplicativa_portaApplicativaAzione_list) {
				PortaApplicativaAzione portaApplicativa_portaApplicativaAzione = (PortaApplicativaAzione) portaApplicativa_portaApplicativaAzione_object;


				portaApplicativa.addPortaApplicativaAzione(portaApplicativa_portaApplicativaAzione);
			}
		}             
		
        return portaApplicativa;  
	
	} 
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._exists(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	private boolean _exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		boolean existsPortaApplicativa = false;

		sqlQueryObject = sqlQueryObject.newSQLQueryObject();
		sqlQueryObject.setANDLogicOperator(true);

		sqlQueryObject.addFromTable(this.getPortaApplicativaFieldConverter().toTable(PortaApplicativa.model()));
		sqlQueryObject.addSelectField(this.getPortaApplicativaFieldConverter().toColumn(PortaApplicativa.model().NOME,true));
		sqlQueryObject.addWhereCondition("id=?");


		// Exists portaApplicativa
		existsPortaApplicativa = jdbcUtilities.exists(sqlQueryObject.createSQLQuery(), jdbcProperties.isShowSql(),
			new JDBCObject(tableId,Long.class));

		
        return existsPortaApplicativa;
	
	}
	
	private void _join(IExpression expression, ISQLQueryObject sqlQueryObject) throws NotImplementedException, ServiceException, Exception{
	
		if(expression.inUseModel(PortaApplicativa.model().ID_SOGGETTO,false)){
			String tableName1 = this.getPortaApplicativaFieldConverter().toAliasTable(PortaApplicativa.model());
			String tableName2 = this.getPortaApplicativaFieldConverter().toAliasTable(PortaApplicativa.model().ID_SOGGETTO);
			sqlQueryObject.addWhereCondition(tableName1+".id_soggetto="+tableName2+".id");
		}
		if(expression.inUseModel(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO,false)){
			String tableName1 = this.getPortaApplicativaFieldConverter().toAliasTable(PortaApplicativa.model());
			String tableName2 = this.getPortaApplicativaFieldConverter().toAliasTable(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO);
			sqlQueryObject.addWhereCondition(tableName1+".id="+tableName2+".id_porta");
		}
		if(expression.inUseModel(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO,false)){
			String tableName1 = this.getPortaApplicativaFieldConverter().toAliasTable(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO);
			String tableName2 = this.getPortaApplicativaFieldConverter().toAliasTable(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO);
			sqlQueryObject.addWhereCondition(tableName1+".id_servizio_applicativo="+tableName2+".id");
		}
		if(expression.inUseModel(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.ID_SOGGETTO,false)){
			String tableName1 = this.getPortaApplicativaFieldConverter().toAliasTable(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO);
			String tableName2 = this.getPortaApplicativaFieldConverter().toAliasTable(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.ID_SOGGETTO);
			sqlQueryObject.addWhereCondition(tableName1+".id_soggetto="+tableName2+".id");
		}
		if(expression.inUseModel(PortaApplicativa.model().PORTA_APPLICATIVA_AZIONE,false)){
			String tableName1 = this.getPortaApplicativaFieldConverter().toAliasTable(PortaApplicativa.model());
			String tableName2 = this.getPortaApplicativaFieldConverter().toAliasTable(PortaApplicativa.model().PORTA_APPLICATIVA_AZIONE);
			sqlQueryObject.addWhereCondition(tableName1+".id="+tableName2+".id_porta");
		}
		
		
		boolean addFromPDSA = false;
        if(expression.inUseModel(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO,false)){
			if(expression.inUseModel(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO,false)==false){
				
				sqlQueryObject.addFromTable(this.getPortaApplicativaFieldConverter().toTable(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO));
				
				String tableName1 = this.getPortaApplicativaFieldConverter().toAliasTable(PortaApplicativa.model());
				String tableName2 = this.getPortaApplicativaFieldConverter().toAliasTable(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO);
				try{
					sqlQueryObject.addWhereCondition(tableName1+".id="+tableName2+".id_porta");
				}catch(Exception e){
					// exception se gia' esiste
				}
				
				addFromPDSA = true;
			}
		}
        if(expression.inUseModel(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.ID_SOGGETTO,false)){
			if(expression.inUseModel(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO,false)==false){
				
				sqlQueryObject.addFromTable(this.getPortaApplicativaFieldConverter().toTable(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO));
				
				String tableName1 = this.getPortaApplicativaFieldConverter().toAliasTable(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO);
				String tableName2 = this.getPortaApplicativaFieldConverter().toAliasTable(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO);
				try{
					sqlQueryObject.addWhereCondition(tableName1+".id_servizio_applicativo="+tableName2+".id");
				}catch(Exception e){
					// exception se gia' esiste
				}
				
			}
			if(addFromPDSA==false){
				sqlQueryObject.addFromTable(this.getPortaApplicativaFieldConverter().toTable(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO));
			}
		}
        
	}
	
	protected java.util.List<Object> _getRootTablePrimaryKeyValues(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPortaApplicativa id) throws NotFoundException, ServiceException, NotImplementedException, Exception{
	    // Identificativi
        java.util.List<Object> rootTableIdValues = new java.util.ArrayList<Object>();
        Long longId = this.findIdPortaApplicativa(jdbcProperties, log, connection, sqlQueryObject.newSQLQueryObject(), id, true);
		rootTableIdValues.add(longId);
        
        return rootTableIdValues;
	}
	
	protected Map<String, List<IField>> _getMapTableToPKColumn() throws NotImplementedException, Exception{
	
		PortaApplicativaFieldConverter converter = this.getPortaApplicativaFieldConverter();
		Map<String, List<IField>> mapTableToPKColumn = new java.util.Hashtable<String, List<IField>>();
		UtilsTemplate<IField> utilities = new UtilsTemplate<IField>();

		// PortaApplicativa.model()
		mapTableToPKColumn.put(converter.toTable(PortaApplicativa.model()),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(PortaApplicativa.model()))
			));

		// PortaApplicativa.model().ID_SOGGETTO
		mapTableToPKColumn.put(converter.toTable(PortaApplicativa.model().ID_SOGGETTO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(PortaApplicativa.model().ID_SOGGETTO))
			));

		// PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO
		mapTableToPKColumn.put(converter.toTable(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO))
			));

		// PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO
		mapTableToPKColumn.put(converter.toTable(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO))
			));

		// PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.ID_SOGGETTO
		mapTableToPKColumn.put(converter.toTable(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.ID_SOGGETTO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.ID_SOGGETTO))
			));

		// PortaApplicativa.model().PORTA_APPLICATIVA_AZIONE
		mapTableToPKColumn.put(converter.toTable(PortaApplicativa.model().PORTA_APPLICATIVA_AZIONE),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(PortaApplicativa.model().PORTA_APPLICATIVA_AZIONE))
			));
        
        return mapTableToPKColumn;		
	}
	
	@Override
	public List<Long> findAllTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression paginatedExpression) throws ServiceException, NotImplementedException, Exception {
		
		List<Long> list = new ArrayList<Long>();

		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getPortaApplicativaFieldConverter().toTable(PortaApplicativa.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFindAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
												this.getPortaApplicativaFieldConverter(), PortaApplicativa.model());
		
		_join(paginatedExpression,sqlQueryObject);
		
		List<Object> listObjects = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.findAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
																			this.getPortaApplicativaFieldConverter(), PortaApplicativa.model(), objectIdClass, listaQuery);
		for(Object object: listObjects) {
			list.add((Long)object);
		}

        return list;
		
	}
	
	@Override
	public long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, Exception {
	
		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getPortaApplicativaFieldConverter().toTable(PortaApplicativa.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFind(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getPortaApplicativaFieldConverter(), PortaApplicativa.model());
		
		_join(expression,sqlQueryObject);

		Object res = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.find(jdbcProperties, log, connection, sqlQueryObject, expression,
														this.getPortaApplicativaFieldConverter(), PortaApplicativa.model(), objectIdClass, listaQuery);
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
	public IdPortaApplicativa findId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
               
		// Object _portaApplicativa
		sqlQueryObjectGet.addFromTable(this.getPortaApplicativaFieldConverter().toTable(PortaApplicativa.model()));
		sqlQueryObjectGet.addSelectField(this.getPortaApplicativaFieldConverter().toColumn(PortaApplicativa.model().NOME,true));
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addWhereCondition("id=?");

		// Recupero _portaApplicativa
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_portaApplicativa = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(tableId,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_portaApplicativa = new ArrayList<Class<?>>();
		listaFieldIdReturnType_portaApplicativa.add(String.class);
		org.openspcoop2.core.commons.search.IdPortaApplicativa id_portaApplicativa = null;
		List<Object> listaFieldId_portaApplicativa = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_portaApplicativa, searchParams_portaApplicativa);
		if(listaFieldId_portaApplicativa==null || listaFieldId_portaApplicativa.size()<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		else{
			// set _portaApplicativa
			id_portaApplicativa = new org.openspcoop2.core.commons.search.IdPortaApplicativa();
			id_portaApplicativa.setNome((String)listaFieldId_portaApplicativa.get(0));

		}
		
		return id_portaApplicativa;
		
	}

	@Override
	public Long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPortaApplicativa id, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
	
		return this.findIdPortaApplicativa(jdbcProperties,log,connection,sqlQueryObject,id,throwNotFound);
			
	}
	
	@Override
	public List<List<Object>> nativeQuery(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
											String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException,Exception{
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.nativeQuery(jdbcProperties, log, connection, sqlQueryObject,
																							sql,returnClassTypes,param);
														
	}
	
	protected Long findIdPortaApplicativa(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPortaApplicativa id, boolean throwNotFound) throws NotFoundException, ServiceException, NotImplementedException, Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();


		if(id==null){
			throw new ServiceException("Id not defined");
		}
		if(id.getNome()==null){
			throw new ServiceException("Id.nome not defined");
		}

		// Object _portaApplicativa
		sqlQueryObjectGet.addFromTable(this.getPortaApplicativaFieldConverter().toTable(PortaApplicativa.model()));
		sqlQueryObjectGet.addSelectField("id");
		// Devono essere mappati nella where condition i metodi dell'oggetto id.getXXX
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.setSelectDistinct(true);
		sqlQueryObjectGet.addWhereCondition(this.getPortaApplicativaFieldConverter().toColumn(PortaApplicativa.model().NOME,true)+"=?");

		// Recupero _portaApplicativa
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_portaApplicativa = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id.getNome(),String.class)
		};
		Long id_portaApplicativa = null;
		try{
			id_portaApplicativa = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
						Long.class, searchParams_portaApplicativa);
		}catch(NotFoundException notFound){
			if(throwNotFound){
				throw new NotFoundException(notFound);
			}
		}
		if(id_portaApplicativa==null || id_portaApplicativa<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		
		return id_portaApplicativa;
	}
}
