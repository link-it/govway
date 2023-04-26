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

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.search.IdPortaDelegata;
import org.openspcoop2.core.commons.search.IdServizioApplicativo;
import org.openspcoop2.core.commons.search.IdSoggetto;
import org.openspcoop2.core.commons.search.PortaDelegata;
import org.openspcoop2.core.commons.search.PortaDelegataAzione;
import org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.commons.search.dao.IDBSoggettoServiceSearch;
import org.openspcoop2.core.commons.search.dao.jdbc.converter.PortaDelegataFieldConverter;
import org.openspcoop2.core.commons.search.dao.jdbc.fetch.PortaDelegataFetch;
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
 * JDBCPortaDelegataServiceSearchImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCPortaDelegataServiceSearchImpl implements IJDBCServiceSearchWithId<PortaDelegata, IdPortaDelegata, JDBCServiceManager> {

	private PortaDelegataFieldConverter _portaDelegataFieldConverter = null;
	public PortaDelegataFieldConverter getPortaDelegataFieldConverter() {
		if(this._portaDelegataFieldConverter==null){
			this._portaDelegataFieldConverter = new PortaDelegataFieldConverter(this.jdbcServiceManager.getJdbcProperties().getDatabaseType());
		}		
		return this._portaDelegataFieldConverter;
	}
	@Override
	public ISQLFieldConverter getFieldConverter() {
		return this.getPortaDelegataFieldConverter();
	}
	
	private PortaDelegataFetch portaDelegataFetch = new PortaDelegataFetch();
	public PortaDelegataFetch getPortaDelegataFetch() {
		return this.portaDelegataFetch;
	}
	@Override
	public IJDBCFetch getFetch() {
		return getPortaDelegataFetch();
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
	public IdPortaDelegata convertToId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, PortaDelegata portaDelegata) throws NotImplementedException, ServiceException, Exception{
	
		IdPortaDelegata idPortaDelegata = new IdPortaDelegata();
		idPortaDelegata.setNome(portaDelegata.getNome());
		return idPortaDelegata;
	}
	
	@Override
	public PortaDelegata get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPortaDelegata id, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {
		Long id_portaDelegata = ( (id!=null && id.getId()!=null && id.getId()>0) ? id.getId() : this.findIdPortaDelegata(jdbcProperties, log, connection, sqlQueryObject, id, true));
		return this.getEngine(jdbcProperties, log, connection, sqlQueryObject, id_portaDelegata,idMappingResolutionBehaviour);
		
		
	}
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPortaDelegata id) throws MultipleResultException, NotImplementedException, ServiceException,Exception {

		Long id_portaDelegata = this.findIdPortaDelegata(jdbcProperties, log, connection, sqlQueryObject, id, false);
		return id_portaDelegata != null && id_portaDelegata > 0;
		
	}
	
	@Override
	public List<IdPortaDelegata> findAllIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

		List<IdPortaDelegata> list = new ArrayList<IdPortaDelegata>();

		// TODO: implementazione non efficiente. 
		// Per ottenere una implementazione efficiente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari a create l'ID logico
		// 2. Usare metodo getPortaDelegataFetch() sul risultato della select per ottenere un oggetto PortaDelegata
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 
		// 3. Usare metodo convertToId per ottenere l'id

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	PortaDelegata portaDelegata = this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour);
			IdPortaDelegata idPortaDelegata = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,portaDelegata);
        	list.add(idPortaDelegata);
        }

        return list;
		
	}
	
	@Override
	public List<PortaDelegata> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, 
			org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

        List<PortaDelegata> list = new ArrayList<PortaDelegata>();
        
        boolean soloDatiIdentificativiServizio = ExpressionProperties.isEnabledSoloDatiIdentificativiServizio(expression); 
        
        if(soloDatiIdentificativiServizio){
        	
        	List<IField> fields = new ArrayList<IField>();
    		fields.add(PortaDelegata.model().NOME);
    		fields.add(PortaDelegata.model().STATO);
    		fields.add(PortaDelegata.model().TIPO_SOGGETTO_EROGATORE);
    		fields.add(PortaDelegata.model().NOME_SOGGETTO_EROGATORE);
    		fields.add(PortaDelegata.model().TIPO_SERVIZIO);
    		fields.add(PortaDelegata.model().NOME_SERVIZIO);
    		fields.add(PortaDelegata.model().VERSIONE_SERVIZIO);
    		fields.add(PortaDelegata.model().MODE_AZIONE);
    		fields.add(PortaDelegata.model().NOME_AZIONE);
    		fields.add(PortaDelegata.model().NOME_PORTA_DELEGANTE_AZIONE);
    		
    		String aliasSoggettoTipo = "proprietarioSoggettoTipo";
    		fields.add(new AliasField(PortaDelegata.model().ID_SOGGETTO.TIPO, aliasSoggettoTipo));
    		String aliasSoggettoNome = "proprietarioSoggettoNome";
    		fields.add(new AliasField(PortaDelegata.model().ID_SOGGETTO.NOME, aliasSoggettoNome));
        	
    		List<Map<String, Object>> returnMap = null;
    		try{
    			// non usare true altrimenti non funzionano alcuni meccanismi di ricerca, ad es. la valorizzazione dei select field nel servizio della govwayMonitor.
    			// Tanto le join non comportano righe multiple uguali
    			boolean distinct = false; 
    			returnMap = this.select(jdbcProperties, log, connection, sqlQueryObject, expression, distinct, fields.toArray(new IField[1]));
    			
	    		for(Map<String, Object> map: returnMap) {
	    			
	    			PortaDelegata pd = (PortaDelegata) this.getPortaDelegataFetch().fetch(jdbcProperties.getDatabase(), PortaDelegata.model(), map);
	    			
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
    					pd.setIdSoggetto(idSoggetto);
    				} 			
	    			
	    			list.add(pd);
	    		}
    		}catch(NotFoundException notFound){}
        }
        else {
	        // TODO: implementazione non efficiente. 
			// Per ottenere una implementazione efficiente:
			// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari
			// 2. Usare metodo getPortaDelegataFetch() sul risultato della select per ottenere un oggetto PortaDelegata
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
	public PortaDelegata find(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) 
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
												this.getPortaDelegataFieldConverter(), PortaDelegata.model());
		
		sqlQueryObject.addSelectCountField(this.getPortaDelegataFieldConverter().toTable(PortaDelegata.model())+".id","tot",true);
		
		joinEngine(expression,sqlQueryObject);
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.count(jdbcProperties, log, connection, sqlQueryObject, expression,
																			this.getPortaDelegataFieldConverter(), PortaDelegata.model(),listaQuery);
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPortaDelegata id) throws NotFoundException, NotImplementedException, ServiceException,Exception {
		
		Long id_portaDelegata = this.findIdPortaDelegata(jdbcProperties, log, connection, sqlQueryObject, id, true);
        return this.inUseEngine(jdbcProperties, log, connection, sqlQueryObject, id_portaDelegata);
		
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
												this.getPortaDelegataFieldConverter(), field);

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
        						expression, this.getPortaDelegataFieldConverter(), PortaDelegata.model(), 
        						listaQuery,listaParams);
		
		joinEngine(expression,sqlQueryObject);
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.select(jdbcProperties, log, connection,
        								org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareSqlQueryObjectForSelectDistinct(sqlQueryObject,sqlQueryObjectDistinct), 
        								expression, this.getPortaDelegataFieldConverter(), PortaDelegata.model(),
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
        						this.getPortaDelegataFieldConverter(), PortaDelegata.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				joinEngine(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.union(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getPortaDelegataFieldConverter(), PortaDelegata.model(), 
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
        						this.getPortaDelegataFieldConverter(), PortaDelegata.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				joinEngine(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        NonNegativeNumber number = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.unionCount(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getPortaDelegataFieldConverter(), PortaDelegata.model(), 
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
			return new JDBCExpression(this.getPortaDelegataFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}


	@Override
	public JDBCPaginatedExpression newPaginatedExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(this.getPortaDelegataFieldConverter());
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
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPortaDelegata id, PortaDelegata obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,id,null));
	}
	
	@Override
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, PortaDelegata obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,tableId,null));
	}
	private void _mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, PortaDelegata obj, PortaDelegata imgSaved) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		if(imgSaved==null){
			return;
		}
		obj.setId(imgSaved.getId());
		if(obj.getIdSoggetto()!=null && 
				imgSaved.getIdSoggetto()!=null){
			obj.getIdSoggetto().setId(imgSaved.getIdSoggetto().getId());
		}
		if(obj.getPortaDelegataServizioApplicativoList()!=null){
			List<org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo> listObj_ = obj.getPortaDelegataServizioApplicativoList();
			for(org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo itemObj_ : listObj_){
				org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo itemAlreadySaved_ = null;
				if(imgSaved.getPortaDelegataServizioApplicativoList()!=null){
					List<org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo> listImgSaved_ = imgSaved.getPortaDelegataServizioApplicativoList();
					for(org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo itemImgSaved_ : listImgSaved_){
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
		if(obj.getPortaDelegataAzioneList()!=null){
			List<org.openspcoop2.core.commons.search.PortaDelegataAzione> listObj_ = obj.getPortaDelegataAzioneList();
			for(org.openspcoop2.core.commons.search.PortaDelegataAzione itemObj_ : listObj_){
				org.openspcoop2.core.commons.search.PortaDelegataAzione itemAlreadySaved_ = null;
				if(imgSaved.getPortaDelegataAzioneList()!=null){
					List<org.openspcoop2.core.commons.search.PortaDelegataAzione> listImgSaved_ = imgSaved.getPortaDelegataAzioneList();
					for(org.openspcoop2.core.commons.search.PortaDelegataAzione itemImgSaved_ : listImgSaved_){
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
	public PortaDelegata get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this.getEngine(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId), idMappingResolutionBehaviour);
	}
	
	private PortaDelegata getEngine(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
				
		PortaDelegata portaDelegata = new PortaDelegata();
		

		// Object portaDelegata
		ISQLQueryObject sqlQueryObjectGet_portaDelegata = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_portaDelegata.setANDLogicOperator(true);
		sqlQueryObjectGet_portaDelegata.addFromTable(this.getPortaDelegataFieldConverter().toTable(PortaDelegata.model()));
		sqlQueryObjectGet_portaDelegata.addSelectField("id");
		sqlQueryObjectGet_portaDelegata.addSelectField(this.getPortaDelegataFieldConverter().toColumn(PortaDelegata.model().NOME,true));
		sqlQueryObjectGet_portaDelegata.addSelectField(this.getPortaDelegataFieldConverter().toColumn(PortaDelegata.model().STATO,true));
		sqlQueryObjectGet_portaDelegata.addSelectField(this.getPortaDelegataFieldConverter().toColumn(PortaDelegata.model().TIPO_SOGGETTO_EROGATORE,true));
		sqlQueryObjectGet_portaDelegata.addSelectField(this.getPortaDelegataFieldConverter().toColumn(PortaDelegata.model().NOME_SOGGETTO_EROGATORE,true));
		sqlQueryObjectGet_portaDelegata.addSelectField(this.getPortaDelegataFieldConverter().toColumn(PortaDelegata.model().TIPO_SERVIZIO,true));
		sqlQueryObjectGet_portaDelegata.addSelectField(this.getPortaDelegataFieldConverter().toColumn(PortaDelegata.model().NOME_SERVIZIO,true));
		sqlQueryObjectGet_portaDelegata.addSelectField(this.getPortaDelegataFieldConverter().toColumn(PortaDelegata.model().VERSIONE_SERVIZIO,true));
		sqlQueryObjectGet_portaDelegata.addSelectField(this.getPortaDelegataFieldConverter().toColumn(PortaDelegata.model().MODE_AZIONE,true));
		sqlQueryObjectGet_portaDelegata.addSelectField(this.getPortaDelegataFieldConverter().toColumn(PortaDelegata.model().NOME_AZIONE,true));
		sqlQueryObjectGet_portaDelegata.addSelectField(this.getPortaDelegataFieldConverter().toColumn(PortaDelegata.model().NOME_PORTA_DELEGANTE_AZIONE,true));
		sqlQueryObjectGet_portaDelegata.addSelectField(this.getPortaDelegataFieldConverter().toColumn(PortaDelegata.model().CANALE,true));
		sqlQueryObjectGet_portaDelegata.addWhereCondition("id=?");

		// Get portaDelegata
		portaDelegata = (PortaDelegata) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_portaDelegata.createSQLQuery(), jdbcProperties.isShowSql(), PortaDelegata.model(), this.getPortaDelegataFetch(),
			new JDBCObject(tableId,Long.class));


		// Object _portaDelegata_soggetto (recupero id)
		ISQLQueryObject sqlQueryObjectGet_portaDelegata_soggetto_readFkId = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_portaDelegata_soggetto_readFkId.addFromTable(this.getPortaDelegataFieldConverter().toTable(org.openspcoop2.core.commons.search.PortaDelegata.model()));
		sqlQueryObjectGet_portaDelegata_soggetto_readFkId.addSelectField("id_soggetto");
		sqlQueryObjectGet_portaDelegata_soggetto_readFkId.addWhereCondition("id=?");
		sqlQueryObjectGet_portaDelegata_soggetto_readFkId.setANDLogicOperator(true);
		Long idFK_portaDelegata_soggetto = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_portaDelegata_soggetto_readFkId.createSQLQuery(), jdbcProperties.isShowSql(),Long.class,
				new JDBCObject(portaDelegata.getId(),Long.class));
		
		// Object _portaDelegata_soggetto
		ISQLQueryObject sqlQueryObjectGet_portaDelegata_soggetto = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_portaDelegata_soggetto.addFromTable("soggetti");
		sqlQueryObjectGet_portaDelegata_soggetto.addSelectField("tipo_soggetto");
		sqlQueryObjectGet_portaDelegata_soggetto.addSelectField("nome_soggetto");
		sqlQueryObjectGet_portaDelegata_soggetto.setANDLogicOperator(true);
		sqlQueryObjectGet_portaDelegata_soggetto.addWhereCondition("id=?");

		// Recupero _portaDelegata_soggetto
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_portaDelegata_soggetto = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(idFK_portaDelegata_soggetto,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_portaDelegata_soggetto = new ArrayList<Class<?>>();
		listaFieldIdReturnType_portaDelegata_soggetto.add(String.class);
		listaFieldIdReturnType_portaDelegata_soggetto.add(String.class);
		List<Object> listaFieldId_portaDelegata_soggetto = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_portaDelegata_soggetto.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_portaDelegata_soggetto, searchParams_portaDelegata_soggetto);
		IdSoggetto idSoggetto = new IdSoggetto();
		idSoggetto.setTipo((String)listaFieldId_portaDelegata_soggetto.get(0));
		idSoggetto.setNome((String)listaFieldId_portaDelegata_soggetto.get(1));
		portaDelegata.setIdSoggetto(idSoggetto);


		// Object portaDelegata_portaDelegataServizioApplicativo
		ISQLQueryObject sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo.setANDLogicOperator(true);
		sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo.addFromTable(this.getPortaDelegataFieldConverter().toTable(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO));
		sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo.addSelectField("id");
		sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo.addWhereCondition("id_porta=?");

		// Get portaDelegata_portaDelegataServizioApplicativo
		java.util.List<Object> portaDelegata_portaDelegataServizioApplicativo_list = jdbcUtilities.executeQuery(sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo.createSQLQuery(), jdbcProperties.isShowSql(), PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO, this.getPortaDelegataFetch(),
			new JDBCObject(portaDelegata.getId(),Long.class));

		if(portaDelegata_portaDelegataServizioApplicativo_list != null) {
			for (Object portaDelegata_portaDelegataServizioApplicativo_object: portaDelegata_portaDelegataServizioApplicativo_list) {
				PortaDelegataServizioApplicativo portaDelegata_portaDelegataServizioApplicativo = (PortaDelegataServizioApplicativo) portaDelegata_portaDelegataServizioApplicativo_object;


				// Object _portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo (recupero id)
				ISQLQueryObject sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo_readFkId = sqlQueryObjectGet.newSQLQueryObject();
				sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo_readFkId.addFromTable(this.getPortaDelegataFieldConverter().toTable(org.openspcoop2.core.commons.search.PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO));
				sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo_readFkId.addSelectField("id_servizio_applicativo");
				sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo_readFkId.addWhereCondition("id=?");
				sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo_readFkId.setANDLogicOperator(true);
				Long idFK_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo_readFkId.createSQLQuery(), jdbcProperties.isShowSql(),Long.class,
						new JDBCObject(portaDelegata_portaDelegataServizioApplicativo.getId(),Long.class));
				
				// Object _portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo
				ISQLQueryObject sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo = sqlQueryObjectGet.newSQLQueryObject();
				sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo.addFromTable("servizi_applicativi");
				sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo.addSelectField("nome");
				sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo.addSelectField("id_soggetto");
				sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo.setANDLogicOperator(true);
				sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo.addWhereCondition("id=?");

				// Recupero _portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo
				org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(idFK_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo,Long.class)
				};
				List<Class<?>> listaFieldIdReturnType_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo = new ArrayList<Class<?>>();
				listaFieldIdReturnType_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo.add(String.class);
				listaFieldIdReturnType_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo.add(Long.class);
				List<Object> listaFieldId_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo.createSQLQuery(), jdbcProperties.isShowSql(),
						listaFieldIdReturnType_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo, searchParams_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo);
				IdServizioApplicativo idServizioApplicativo = new IdServizioApplicativo();
				idServizioApplicativo.setNome((String)listaFieldId_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo.get(0));
				Long idSoggettoLong = (Long) listaFieldId_portaDelegata_portaDelegataServizioApplicativo_servizioApplicativo.get(1);
				IdSoggetto idSoggettoSA = this.jdbcServiceManager.getSoggettoServiceSearch().convertToId(
						((IDBSoggettoServiceSearch)this.jdbcServiceManager.getSoggettoServiceSearch()).get(idSoggettoLong)
						);
				idServizioApplicativo.setIdSoggetto(idSoggettoSA);
				portaDelegata_portaDelegataServizioApplicativo.setIdServizioApplicativo(idServizioApplicativo);
				
				portaDelegata.addPortaDelegataServizioApplicativo(portaDelegata_portaDelegataServizioApplicativo);
			}
		}

		// Object portaDelegata_portaDelegataAzione
		ISQLQueryObject sqlQueryObjectGet_portaDelegata_portaDelegataAzione = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_portaDelegata_portaDelegataAzione.setANDLogicOperator(true);
		sqlQueryObjectGet_portaDelegata_portaDelegataAzione.addFromTable(this.getPortaDelegataFieldConverter().toTable(PortaDelegata.model().PORTA_DELEGATA_AZIONE));
		sqlQueryObjectGet_portaDelegata_portaDelegataAzione.addSelectField("id");
		sqlQueryObjectGet_portaDelegata_portaDelegataAzione.addSelectField(this.getPortaDelegataFieldConverter().toColumn(PortaDelegata.model().PORTA_DELEGATA_AZIONE.NOME,true));
		sqlQueryObjectGet_portaDelegata_portaDelegataAzione.addWhereCondition("id_porta=?");

		// Get portaDelegata_portaDelegataAzione
		java.util.List<Object> portaDelegata_portaDelegataAzione_list = jdbcUtilities.executeQuery(sqlQueryObjectGet_portaDelegata_portaDelegataAzione.createSQLQuery(), jdbcProperties.isShowSql(), PortaDelegata.model().PORTA_DELEGATA_AZIONE, this.getPortaDelegataFetch(),
			new JDBCObject(portaDelegata.getId(),Long.class));

		if(portaDelegata_portaDelegataAzione_list != null) {
			for (Object portaDelegata_portaDelegataAzione_object: portaDelegata_portaDelegataAzione_list) {
				PortaDelegataAzione portaDelegata_portaDelegataAzione = (PortaDelegataAzione) portaDelegata_portaDelegataAzione_object;


				portaDelegata.addPortaDelegataAzione(portaDelegata_portaDelegataAzione);
			}
		}      
		
        return portaDelegata;  
	
	} 
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._exists(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	private boolean _exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		boolean existsPortaDelegata = false;

		sqlQueryObject = sqlQueryObject.newSQLQueryObject();
		sqlQueryObject.setANDLogicOperator(true);

		sqlQueryObject.addFromTable(this.getPortaDelegataFieldConverter().toTable(PortaDelegata.model()));
		sqlQueryObject.addSelectField(this.getPortaDelegataFieldConverter().toColumn(PortaDelegata.model().NOME,true));
		sqlQueryObject.addWhereCondition("id=?");


		// Exists portaDelegata
		existsPortaDelegata = jdbcUtilities.exists(sqlQueryObject.createSQLQuery(), jdbcProperties.isShowSql(),
			new JDBCObject(tableId,Long.class));

		
        return existsPortaDelegata;
	
	}
	
	private void joinEngine(IExpression expression, ISQLQueryObject sqlQueryObject) throws NotImplementedException, ServiceException, Exception{
	
		if(expression.inUseModel(PortaDelegata.model().ID_SOGGETTO,false)){
			String tableName1 = this.getPortaDelegataFieldConverter().toAliasTable(PortaDelegata.model());
			String tableName2 = this.getPortaDelegataFieldConverter().toAliasTable(PortaDelegata.model().ID_SOGGETTO);
			sqlQueryObject.addWhereCondition(tableName1+".id_soggetto="+tableName2+".id");
		}
		if(expression.inUseModel(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO,false)){
			String tableName1 = this.getPortaDelegataFieldConverter().toAliasTable(PortaDelegata.model());
			String tableName2 = this.getPortaDelegataFieldConverter().toAliasTable(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO);
			sqlQueryObject.addWhereCondition(tableName1+".id="+tableName2+".id_porta");
		}
		if(expression.inUseModel(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO,false)){
			String tableName1 = this.getPortaDelegataFieldConverter().toAliasTable(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO);
			String tableName2 = this.getPortaDelegataFieldConverter().toAliasTable(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO);
			sqlQueryObject.addWhereCondition(tableName1+".id_servizio_applicativo="+tableName2+".id");
		}
		if(expression.inUseModel(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.ID_SOGGETTO,false)){
			String tableName1 = this.getPortaDelegataFieldConverter().toAliasTable(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO);
			String tableName2 = this.getPortaDelegataFieldConverter().toAliasTable(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.ID_SOGGETTO);
			sqlQueryObject.addWhereCondition(tableName1+".id_soggetto="+tableName2+".id");
		}
		if(expression.inUseModel(PortaDelegata.model().PORTA_DELEGATA_AZIONE,false)){
			String tableName1 = this.getPortaDelegataFieldConverter().toAliasTable(PortaDelegata.model());
			String tableName2 = this.getPortaDelegataFieldConverter().toAliasTable(PortaDelegata.model().PORTA_DELEGATA_AZIONE);
			sqlQueryObject.addWhereCondition(tableName1+".id="+tableName2+".id_porta");
		}
		
		
		boolean addFromPDSA = false;
        if(expression.inUseModel(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO,false)){
			if(expression.inUseModel(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO,false)==false){
				
				sqlQueryObject.addFromTable(this.getPortaDelegataFieldConverter().toTable(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO));
				
				String tableName1 = this.getPortaDelegataFieldConverter().toAliasTable(PortaDelegata.model());
				String tableName2 = this.getPortaDelegataFieldConverter().toAliasTable(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO);
				try{
					sqlQueryObject.addWhereCondition(tableName1+".id="+tableName2+".id_porta");
				}catch(Exception e){
					// exception se gia' esiste
				}
				
				addFromPDSA = true;
			}
		}
        if(expression.inUseModel(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.ID_SOGGETTO,false)){
			if(expression.inUseModel(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO,false)==false){
				
				sqlQueryObject.addFromTable(this.getPortaDelegataFieldConverter().toTable(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO));
				
				String tableName1 = this.getPortaDelegataFieldConverter().toAliasTable(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO);
				String tableName2 = this.getPortaDelegataFieldConverter().toAliasTable(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO);
				try{
					sqlQueryObject.addWhereCondition(tableName1+".id_servizio_applicativo="+tableName2+".id");
				}catch(Exception e){
					// exception se gia' esiste
				}
			
			}
			if(addFromPDSA==false){
				sqlQueryObject.addFromTable(this.getPortaDelegataFieldConverter().toTable(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO));
			}
		}
        
	}
	
	protected java.util.List<Object> getRootTablePrimaryKeyValuesEngine(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPortaDelegata id) throws NotFoundException, ServiceException, NotImplementedException, Exception{
	    // Identificativi
        java.util.List<Object> rootTableIdValues = new java.util.ArrayList<>();
        Long longId = this.findIdPortaDelegata(jdbcProperties, log, connection, sqlQueryObject.newSQLQueryObject(), id, true);
		rootTableIdValues.add(longId);        
        return rootTableIdValues;
	}
	
	protected Map<String, List<IField>> getMapTableToPKColumnEngine() throws NotImplementedException, Exception{
	
		PortaDelegataFieldConverter converter = this.getPortaDelegataFieldConverter();
		Map<String, List<IField>> mapTableToPKColumn = new java.util.HashMap<>();
		UtilsTemplate<IField> utilities = new UtilsTemplate<>();

		// PortaDelegata.model()
		mapTableToPKColumn.put(converter.toTable(PortaDelegata.model()),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(PortaDelegata.model()))
			));

		// PortaDelegata.model().ID_SOGGETTO
		mapTableToPKColumn.put(converter.toTable(PortaDelegata.model().ID_SOGGETTO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(PortaDelegata.model().ID_SOGGETTO))
			));

		// PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO
		mapTableToPKColumn.put(converter.toTable(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO))
			));

		// PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO
		mapTableToPKColumn.put(converter.toTable(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO))
			));

		// PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.ID_SOGGETTO
		mapTableToPKColumn.put(converter.toTable(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.ID_SOGGETTO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.ID_SOGGETTO))
			));

		// PortaDelegata.model().PORTA_DELEGATA_AZIONE
		mapTableToPKColumn.put(converter.toTable(PortaDelegata.model().PORTA_DELEGATA_AZIONE),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(PortaDelegata.model().PORTA_DELEGATA_AZIONE))
			));
        
        return mapTableToPKColumn;		
	}
	
	@Override
	public List<Long> findAllTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression paginatedExpression) throws ServiceException, NotImplementedException, Exception {
		
		List<Long> list = new ArrayList<Long>();

		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getPortaDelegataFieldConverter().toTable(PortaDelegata.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareFindAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
												this.getPortaDelegataFieldConverter(), PortaDelegata.model());
		
		joinEngine(paginatedExpression,sqlQueryObject);
		
		List<Object> listObjects = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.findAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
																			this.getPortaDelegataFieldConverter(), PortaDelegata.model(), objectIdClass, listaQuery);
		for(Object object: listObjects) {
			list.add((Long)object);
		}

        return list;
		
	}
	
	@Override
	public long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, Exception {
	
		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getPortaDelegataFieldConverter().toTable(PortaDelegata.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareFind(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getPortaDelegataFieldConverter(), PortaDelegata.model());
		
		joinEngine(expression,sqlQueryObject);

		Object res = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.find(jdbcProperties, log, connection, sqlQueryObject, expression,
														this.getPortaDelegataFieldConverter(), PortaDelegata.model(), objectIdClass, listaQuery);
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
	public IdPortaDelegata findId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();            

		// Object _portaDelegata
		sqlQueryObjectGet.addFromTable(this.getPortaDelegataFieldConverter().toTable(PortaDelegata.model()));
		sqlQueryObjectGet.addSelectField(this.getPortaDelegataFieldConverter().toColumn(PortaDelegata.model().NOME,true));
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addWhereCondition("id=?");

		// Recupero _portaDelegata
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_portaDelegata = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(tableId,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_portaDelegata = new ArrayList<Class<?>>();
		listaFieldIdReturnType_portaDelegata.add(String.class);
		org.openspcoop2.core.commons.search.IdPortaDelegata id_portaDelegata = null;
		List<Object> listaFieldId_portaDelegata = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_portaDelegata, searchParams_portaDelegata);
		if(listaFieldId_portaDelegata==null || listaFieldId_portaDelegata.size()<=0){
			if(throwNotFound){
				throw org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.newNotFoundException();
			}
		}
		else{
			// set _portaDelegata
			id_portaDelegata = new org.openspcoop2.core.commons.search.IdPortaDelegata();
			id_portaDelegata.setNome((String)listaFieldId_portaDelegata.get(0));
			
		}
		
		return id_portaDelegata;
		
	}

	@Override
	public Long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPortaDelegata id, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
	
		return this.findIdPortaDelegata(jdbcProperties,log,connection,sqlQueryObject,id,throwNotFound);
			
	}
	
	@Override
	public List<List<Object>> nativeQuery(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
											String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException,Exception{
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.nativeQuery(jdbcProperties, log, connection, sqlQueryObject,
																							sql,returnClassTypes,param);
														
	}
	
	protected Long findIdPortaDelegata(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPortaDelegata id, boolean throwNotFound) throws NotFoundException, ServiceException, NotImplementedException, Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();


		if(id==null){
			throw new ServiceException("Id not defined");
		}
		if(id.getNome()==null){
			throw new ServiceException("Id.nome not defined");
		}

		// Object _portaDelegata
		sqlQueryObjectGet.addFromTable(this.getPortaDelegataFieldConverter().toTable(PortaDelegata.model()));
		sqlQueryObjectGet.addSelectField("id");
		// Devono essere mappati nella where condition i metodi dell'oggetto id.getXXX
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.setSelectDistinct(true);
		sqlQueryObjectGet.addWhereCondition(this.getPortaDelegataFieldConverter().toColumn(PortaDelegata.model().NOME,true)+"=?");

		// Recupero _portaDelegata
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_portaDelegata = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id.getNome(),String.class)
		};
		Long id_portaDelegata = null;
		try{
			id_portaDelegata = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
						Long.class, searchParams_portaDelegata);
		}catch(NotFoundException notFound){
			if(throwNotFound){
				throw new NotFoundException(notFound);
			}
		}
		if(id_portaDelegata==null || id_portaDelegata<=0){
			if(throwNotFound){
				throw org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.newNotFoundException();
			}
		}
		
		return id_portaDelegata;
	}
}
