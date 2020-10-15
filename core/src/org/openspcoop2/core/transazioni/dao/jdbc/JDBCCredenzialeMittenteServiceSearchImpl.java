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

package org.openspcoop2.core.transazioni.dao.jdbc;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.transazioni.CredenzialeMittente;
import org.openspcoop2.core.transazioni.IdCredenzialeMittente;
import org.openspcoop2.core.transazioni.dao.jdbc.converter.CredenzialeMittenteFieldConverter;
import org.openspcoop2.core.transazioni.dao.jdbc.fetch.CredenzialeMittenteFetch;
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
import org.openspcoop2.utils.sql.SQLQueryObjectCore;
import org.slf4j.Logger;

/**     
 * JDBCCredenzialeMittenteServiceSearchImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCCredenzialeMittenteServiceSearchImpl implements IJDBCServiceSearchWithId<CredenzialeMittente, IdCredenzialeMittente, JDBCServiceManager> {

	private CredenzialeMittenteFieldConverter _credenzialeMittenteFieldConverter = null;
	public CredenzialeMittenteFieldConverter getCredenzialeMittenteFieldConverter() {
		if(this._credenzialeMittenteFieldConverter==null){
			this._credenzialeMittenteFieldConverter = new CredenzialeMittenteFieldConverter(this.jdbcServiceManager.getJdbcProperties().getDatabaseType());
		}		
		return this._credenzialeMittenteFieldConverter;
	}
	@Override
	public ISQLFieldConverter getFieldConverter() {
		return this.getCredenzialeMittenteFieldConverter();
	}
	
	private CredenzialeMittenteFetch credenzialeMittenteFetch = new CredenzialeMittenteFetch();
	public CredenzialeMittenteFetch getCredenzialeMittenteFetch() {
		return this.credenzialeMittenteFetch;
	}
	@Override
	public IJDBCFetch getFetch() {
		return getCredenzialeMittenteFetch();
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
	public IdCredenzialeMittente convertToId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, CredenzialeMittente credenzialeMittente) throws NotImplementedException, ServiceException, Exception{
	
		IdCredenzialeMittente idCredenzialeMittente = new IdCredenzialeMittente();
		idCredenzialeMittente.setTipo(credenzialeMittente.getTipo());
		idCredenzialeMittente.setCredenziale(credenzialeMittente.getCredenziale());
		return idCredenzialeMittente;
	}
	
	@Override
	public CredenzialeMittente get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdCredenzialeMittente id, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {
		Long id_credenzialeMittente = ( (id!=null && id.getId()!=null && id.getId()>0) ? id.getId() : this.findIdCredenzialeMittente(jdbcProperties, log, connection, sqlQueryObject, id, true));
		return this._get(jdbcProperties, log, connection, sqlQueryObject, id_credenzialeMittente,idMappingResolutionBehaviour);
		
		
	}
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdCredenzialeMittente id) throws MultipleResultException, NotImplementedException, ServiceException,Exception {

		Long id_credenzialeMittente = this.findIdCredenzialeMittente(jdbcProperties, log, connection, sqlQueryObject, id, false);
		return id_credenzialeMittente != null && id_credenzialeMittente > 0;
		
	}
	
	@Override
	public List<IdCredenzialeMittente> findAllIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

		List<IdCredenzialeMittente> list = new ArrayList<IdCredenzialeMittente>();

		boolean efficente = true;
	        
        if(efficente) {
        
        	List<IField> fields = new ArrayList<IField>();
        	fields.add(CredenzialeMittente.model().TIPO);
        	fields.add(CredenzialeMittente.model().CREDENZIALE);
        	fields.add(CredenzialeMittente.model().ORA_REGISTRAZIONE);
        	fields.add(new CustomField("id", Long.class, "id", this.getCredenzialeMittenteFieldConverter().toTable(CredenzialeMittente.model())));
        	
        	List<Map<String, Object>> returnMap = null;
    		try{
    			 // Il distinct serve solo se ci sono le ricerche con contenuto.
    	        // NOTA: il distinct rende le ricerce inefficenti (ed inoltre non e' utilizzabile con campi clob in oracle)
    	        boolean distinct = false;
    	        ISQLQueryObject sqlQueryObjectCheckJoin = sqlQueryObject.newSQLQueryObject();
    	        _join(expression, sqlQueryObjectCheckJoin);
    	        distinct = ((SQLQueryObjectCore)sqlQueryObjectCheckJoin).sizeConditions()>0;
    	        
    	        // BUG FIX: Siccome tra le colonne lette ci sono dei CLOB, in oracle non e' consentito utilizzare il DISTINCT.
    	        // Per questo motivo se c'e' da usare il distinct viene utilizzato il vecchio metodo
    	        if(distinct) {
    	        	//System.out.println("NON EFFICENTE");
    	        	
    		        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
    		        
    		        for(Long id: ids) {
    		        	CredenzialeMittente credenzialeMittente = this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour);
    					IdCredenzialeMittente idCredenzialeMittente = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,credenzialeMittente);
    		        	list.add(idCredenzialeMittente);
    		        }
    	        	
    	        }
    	        else {
    	        
    	        	//System.out.println("EFFICENTE");
    	        	
		    		returnMap = this.select(jdbcProperties, log, connection, sqlQueryObject, expression, distinct, fields.toArray(new IField[1]));
		
		    		for(Map<String, Object> map: returnMap) {
		    			CredenzialeMittente credenzialeMittente = (CredenzialeMittente)this.getCredenzialeMittenteFetch().fetch(jdbcProperties.getDatabase(), CredenzialeMittente.model(), map);
		    			IdCredenzialeMittente idCredenzialeMittente = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,credenzialeMittente);
    		        	list.add(idCredenzialeMittente);
		    		}
		    		
    	        }
		    		
    		}catch(NotFoundException notFound){}
        	
        }
        else {
		
			// TODO: implementazione non efficente. 
			// Per ottenere una implementazione efficente:
			// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari a create l'ID logico
			// 2. Usare metodo getCredenzialeMittenteFetch() sul risultato della select per ottenere un oggetto CredenzialeMittente
			//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 
			// 3. Usare metodo convertToId per ottenere l'id
	
	        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
	        
	        for(Long id: ids) {
	        	CredenzialeMittente credenzialeMittente = this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour);
				IdCredenzialeMittente idCredenzialeMittente = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,credenzialeMittente);
	        	list.add(idCredenzialeMittente);
	        }
	        
        }

        return list;
		
	}
	
	@Override
	public List<CredenzialeMittente> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

        List<CredenzialeMittente> list = new ArrayList<CredenzialeMittente>();
        
        boolean efficente = true;
        
        if(efficente) {
        
        	List<IField> fields = new ArrayList<IField>();
        	fields.add(CredenzialeMittente.model().TIPO);
        	fields.add(CredenzialeMittente.model().CREDENZIALE);
        	fields.add(CredenzialeMittente.model().ORA_REGISTRAZIONE);
        	fields.add(new CustomField("id", Long.class, "id", this.getCredenzialeMittenteFieldConverter().toTable(CredenzialeMittente.model())));
        	
        	List<Map<String, Object>> returnMap = null;
    		try{
    			 // Il distinct serve solo se ci sono le ricerche con contenuto.
    	        // NOTA: il distinct rende le ricerce inefficenti (ed inoltre non e' utilizzabile con campi clob in oracle)
    	        boolean distinct = false;
    	        ISQLQueryObject sqlQueryObjectCheckJoin = sqlQueryObject.newSQLQueryObject();
    	        _join(expression, sqlQueryObjectCheckJoin);
    	        distinct = ((SQLQueryObjectCore)sqlQueryObjectCheckJoin).sizeConditions()>0;
    	        
    	        // BUG FIX: Siccome tra le colonne lette ci sono dei CLOB, in oracle non e' consentito utilizzare il DISTINCT.
    	        // Per questo motivo se c'e' da usare il distinct viene utilizzato il vecchio metodo
    	        if(distinct) {
    	        	//System.out.println("NON EFFICENTE");
    	        	
    	        	List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
    		        
    		        for(Long id: ids) {
    		        	list.add(this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour));
    		        }
    	        	
    	        }
    	        else {
    	        
    	        	//System.out.println("EFFICENTE");
    	        	
		    		returnMap = this.select(jdbcProperties, log, connection, sqlQueryObject, expression, distinct, fields.toArray(new IField[1]));
		
		    		for(Map<String, Object> map: returnMap) {
		    			list.add((CredenzialeMittente)this.getCredenzialeMittenteFetch().fetch(jdbcProperties.getDatabase(), CredenzialeMittente.model(), map));
		    		}
		    		
    	        }
		    		
    		}catch(NotFoundException notFound){}
        	
        }
        else {
	        // TODO: implementazione non efficente. 
			// Per ottenere una implementazione efficente:
			// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari
			// 2. Usare metodo getCredenzialeMittenteFetch() sul risultato della select per ottenere un oggetto CredenzialeMittente
			//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 
	
	        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
	        
	        for(Long id: ids) {
	        	list.add(this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour));
	        }
        }

        return list;      
		
	}
	
	@Override
	public CredenzialeMittente find(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) 
		throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {

		boolean efficente = true;
		
		if(efficente) {
			JDBCPaginatedExpression pagExpr = this.toPaginatedExpression(expression, log);
			pagExpr.limit(2);// dovrebbe esisterne uno solo
			List<CredenzialeMittente>  list = this.findAll(jdbcProperties, log, connection, sqlQueryObject, pagExpr, idMappingResolutionBehaviour);
			if(list==null || list.isEmpty()) {
				throw new NotFoundException("Ricerca non ha trovato entries");
			}
			else if(list.size()>1) {
				throw new NotFoundException("Ricerca ha trovato più entries");
			}
			else {
				return list.get(0);
			}
		}
		else {
		
			long id = this.findTableId(jdbcProperties, log, connection, sqlQueryObject, expression);
			if(id>0){
				return this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour);
			}else{
				throw new NotFoundException("Entry with id["+id+"] not found");
			}
		}
		
	}
	
	@Override
	public NonNegativeNumber count(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws NotImplementedException, ServiceException,Exception {
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareCount(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getCredenzialeMittenteFieldConverter(), CredenzialeMittente.model());
		
		sqlQueryObject.addSelectCountField(this.getCredenzialeMittenteFieldConverter().toTable(CredenzialeMittente.model())+".id","tot",true);
		
		_join(expression,sqlQueryObject);
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.count(jdbcProperties, log, connection, sqlQueryObject, expression,
																			this.getCredenzialeMittenteFieldConverter(), CredenzialeMittente.model(),listaQuery);
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdCredenzialeMittente id) throws NotFoundException, NotImplementedException, ServiceException,Exception {
		
		Long id_credenzialeMittente = this.findIdCredenzialeMittente(jdbcProperties, log, connection, sqlQueryObject, id, true);
        return this._inUse(jdbcProperties, log, connection, sqlQueryObject, id_credenzialeMittente);
		
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
												this.getCredenzialeMittenteFieldConverter(), field);

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
        						expression, this.getCredenzialeMittenteFieldConverter(), CredenzialeMittente.model(), 
        						listaQuery,listaParams);
		
		_join(expression,sqlQueryObject);
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.select(jdbcProperties, log, connection,
        								org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareSqlQueryObjectForSelectDistinct(sqlQueryObject,sqlQueryObjectDistinct), 
        								expression, this.getCredenzialeMittenteFieldConverter(), CredenzialeMittente.model(),
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
        						this.getCredenzialeMittenteFieldConverter(), CredenzialeMittente.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.union(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getCredenzialeMittenteFieldConverter(), CredenzialeMittente.model(), 
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
        						this.getCredenzialeMittenteFieldConverter(), CredenzialeMittente.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        NonNegativeNumber number = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.unionCount(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getCredenzialeMittenteFieldConverter(), CredenzialeMittente.model(), 
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
			return new JDBCExpression(this.getCredenzialeMittenteFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}


	@Override
	public JDBCPaginatedExpression newPaginatedExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(this.getCredenzialeMittenteFieldConverter());
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
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdCredenzialeMittente id, CredenzialeMittente obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,id,null));
	}
	
	@Override
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, CredenzialeMittente obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,tableId,null));
	}
	private void _mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, CredenzialeMittente obj, CredenzialeMittente imgSaved) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		if(imgSaved==null){
			return;
		}
		obj.setId(imgSaved.getId());

	}
	
	@Override
	public CredenzialeMittente get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._get(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId), idMappingResolutionBehaviour);
	}
	
	private CredenzialeMittente _get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
				
		CredenzialeMittente credenzialeMittente = new CredenzialeMittente();
		

		// Object credenzialeMittente
		ISQLQueryObject sqlQueryObjectGet_credenzialeMittente = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_credenzialeMittente.setANDLogicOperator(true);
		sqlQueryObjectGet_credenzialeMittente.addFromTable(this.getCredenzialeMittenteFieldConverter().toTable(CredenzialeMittente.model()));
		sqlQueryObjectGet_credenzialeMittente.addSelectField("id");
		sqlQueryObjectGet_credenzialeMittente.addSelectField(this.getCredenzialeMittenteFieldConverter().toColumn(CredenzialeMittente.model().TIPO,true));
		sqlQueryObjectGet_credenzialeMittente.addSelectField(this.getCredenzialeMittenteFieldConverter().toColumn(CredenzialeMittente.model().CREDENZIALE,true));
		sqlQueryObjectGet_credenzialeMittente.addSelectField(this.getCredenzialeMittenteFieldConverter().toColumn(CredenzialeMittente.model().ORA_REGISTRAZIONE,true));
		sqlQueryObjectGet_credenzialeMittente.addWhereCondition("id=?");

		// Get credenzialeMittente
		credenzialeMittente = (CredenzialeMittente) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_credenzialeMittente.createSQLQuery(), jdbcProperties.isShowSql(), CredenzialeMittente.model(), this.getCredenzialeMittenteFetch(),
			new JDBCObject(tableId,Long.class));



		
        return credenzialeMittente;  
	
	} 
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._exists(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	private boolean _exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		boolean existsCredenzialeMittente = false;

		sqlQueryObject = sqlQueryObject.newSQLQueryObject();
		sqlQueryObject.setANDLogicOperator(true);

		sqlQueryObject.addFromTable(this.getCredenzialeMittenteFieldConverter().toTable(CredenzialeMittente.model()));
		sqlQueryObject.addSelectField(this.getCredenzialeMittenteFieldConverter().toColumn(CredenzialeMittente.model().TIPO,true));
		sqlQueryObject.addWhereCondition("id=?");


		// Exists credenzialeMittente
		existsCredenzialeMittente = jdbcUtilities.exists(sqlQueryObject.createSQLQuery(), jdbcProperties.isShowSql(),
			new JDBCObject(tableId,Long.class));

		
        return existsCredenzialeMittente;
	
	}
	
	private void _join(IExpression expression, ISQLQueryObject sqlQueryObject) throws NotImplementedException, ServiceException, Exception{
	
        
	}
	
	protected java.util.List<Object> _getRootTablePrimaryKeyValues(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdCredenzialeMittente id) throws NotFoundException, ServiceException, NotImplementedException, Exception{
	    // Identificativi
        java.util.List<Object> rootTableIdValues = new java.util.ArrayList<Object>();
        Long longId = this.findIdCredenzialeMittente(jdbcProperties, log, connection, sqlQueryObject.newSQLQueryObject(), id, true);
		rootTableIdValues.add(longId);        
        return rootTableIdValues;
	}
	
	protected Map<String, List<IField>> _getMapTableToPKColumn() throws NotImplementedException, Exception{
	
		CredenzialeMittenteFieldConverter converter = this.getCredenzialeMittenteFieldConverter();
		Map<String, List<IField>> mapTableToPKColumn = new java.util.Hashtable<String, List<IField>>();
		UtilsTemplate<IField> utilities = new UtilsTemplate<IField>();

		// CredenzialeMittente.model()
		mapTableToPKColumn.put(converter.toTable(CredenzialeMittente.model()),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(CredenzialeMittente.model()))
			));
        
        return mapTableToPKColumn;		
	}
	
	@Override
	public List<Long> findAllTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression paginatedExpression) throws ServiceException, NotImplementedException, Exception {
		
		List<Long> list = new ArrayList<Long>();

		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getCredenzialeMittenteFieldConverter().toTable(CredenzialeMittente.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFindAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
												this.getCredenzialeMittenteFieldConverter(), CredenzialeMittente.model());
		
		_join(paginatedExpression,sqlQueryObject);
		
		List<Object> listObjects = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.findAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
																			this.getCredenzialeMittenteFieldConverter(), CredenzialeMittente.model(), objectIdClass, listaQuery);
		for(Object object: listObjects) {
			list.add((Long)object);
		}

        return list;
		
	}
	
	@Override
	public long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, Exception {
	
		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getCredenzialeMittenteFieldConverter().toTable(CredenzialeMittente.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFind(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getCredenzialeMittenteFieldConverter(), CredenzialeMittente.model());
		
		_join(expression,sqlQueryObject);

		Object res = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.find(jdbcProperties, log, connection, sqlQueryObject, expression,
														this.getCredenzialeMittenteFieldConverter(), CredenzialeMittente.model(), objectIdClass, listaQuery);
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
	public IdCredenzialeMittente findId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();

		// Object _credenzialeMittente
		sqlQueryObjectGet.addFromTable(this.getCredenzialeMittenteFieldConverter().toTable(CredenzialeMittente.model()));
		sqlQueryObjectGet.addSelectField(this.getCredenzialeMittenteFieldConverter().toColumn(CredenzialeMittente.model().TIPO,true));
		sqlQueryObjectGet.addSelectField(this.getCredenzialeMittenteFieldConverter().toColumn(CredenzialeMittente.model().CREDENZIALE,true));
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addWhereCondition("id=?");

		// Recupero _credenzialeMittente
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_credenzialeMittente = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(tableId,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_credenzialeMittente = new ArrayList<Class<?>>();
		listaFieldIdReturnType_credenzialeMittente.add(String.class);
		listaFieldIdReturnType_credenzialeMittente.add(String.class);
		org.openspcoop2.core.transazioni.IdCredenzialeMittente id_credenzialeMittente = null;
		List<Object> listaFieldId_credenzialeMittente = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_credenzialeMittente, searchParams_credenzialeMittente);
		if(listaFieldId_credenzialeMittente==null || listaFieldId_credenzialeMittente.size()<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		else{
			// set _credenzialeMittente
			id_credenzialeMittente = new org.openspcoop2.core.transazioni.IdCredenzialeMittente();
			id_credenzialeMittente.setTipo((String)listaFieldId_credenzialeMittente.get(0));
			id_credenzialeMittente.setCredenziale((String)listaFieldId_credenzialeMittente.get(1));
		}
		
		return id_credenzialeMittente;
		
	}

	@Override
	public Long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdCredenzialeMittente id, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
	
		return this.findIdCredenzialeMittente(jdbcProperties,log,connection,sqlQueryObject,id,throwNotFound);
			
	}
	
	@Override
	public List<List<Object>> nativeQuery(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
											String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException,Exception{
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.nativeQuery(jdbcProperties, log, connection, sqlQueryObject,
																							sql,returnClassTypes,param);
														
	}
	
	protected Long findIdCredenzialeMittente(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdCredenzialeMittente id, boolean throwNotFound) throws NotFoundException, ServiceException, NotImplementedException, Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		if(id==null) {
			throw new ServiceException("Id undefined");
		}
		if(id.getTipo()==null) {
			throw new ServiceException("Id.tipo undefined");
		}
		if(id.getCredenziale()==null) {
			throw new ServiceException("Id.credenziale undefined");
		}
		
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();

		// Object _credenzialeMittente
		sqlQueryObjectGet.addFromTable(this.getCredenzialeMittenteFieldConverter().toTable(CredenzialeMittente.model()));
		sqlQueryObjectGet.addSelectField("id");
		// Devono essere mappati nella where condition i metodi dell'oggetto id.getXXX
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.setSelectDistinct(true);
		sqlQueryObjectGet.addWhereCondition(this.getCredenzialeMittenteFieldConverter().toColumn(CredenzialeMittente.model().TIPO,true)+"=?");
		sqlQueryObjectGet.addWhereCondition(this.getCredenzialeMittenteFieldConverter().toColumn(CredenzialeMittente.model().CREDENZIALE,true)+"=?");

		// Recupero _credenzialeMittente
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_credenzialeMittente = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id.getTipo(),String.class),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id.getCredenziale(),String.class)
		};
		Long id_credenzialeMittente = null;
		try{
			id_credenzialeMittente = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
						Long.class, searchParams_credenzialeMittente);
		}catch(NotFoundException notFound){
			if(throwNotFound){
				throw new NotFoundException(notFound);
			}
		}
		if(id_credenzialeMittente==null || id_credenzialeMittente<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		
		return id_credenzialeMittente;
	}
}
