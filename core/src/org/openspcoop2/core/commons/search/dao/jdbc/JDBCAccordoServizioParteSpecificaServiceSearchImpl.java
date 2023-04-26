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
import org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica;
import org.openspcoop2.core.commons.search.IdSoggetto;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.generic_project.utils.UtilsTemplate;
import org.openspcoop2.generic_project.beans.AliasField;
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
import org.openspcoop2.core.commons.search.dao.jdbc.converter.AccordoServizioParteSpecificaFieldConverter;
import org.openspcoop2.core.commons.search.dao.jdbc.fetch.AccordoServizioParteSpecificaFetch;
import org.openspcoop2.core.commons.search.dao.IDBSoggettoServiceSearch;
import org.openspcoop2.core.commons.search.dao.ISoggettoServiceSearch;
import org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComune;

/**     
 * JDBCAccordoServizioParteSpecificaServiceSearchImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCAccordoServizioParteSpecificaServiceSearchImpl implements IJDBCServiceSearchWithId<AccordoServizioParteSpecifica, IdAccordoServizioParteSpecifica, JDBCServiceManager> {

	private AccordoServizioParteSpecificaFieldConverter _accordoServizioParteSpecificaFieldConverter = null;
	public AccordoServizioParteSpecificaFieldConverter getAccordoServizioParteSpecificaFieldConverter() {
		if(this._accordoServizioParteSpecificaFieldConverter==null){
			this._accordoServizioParteSpecificaFieldConverter = new AccordoServizioParteSpecificaFieldConverter(this.jdbcServiceManager.getJdbcProperties().getDatabaseType());
		}		
		return this._accordoServizioParteSpecificaFieldConverter;
	}
	@Override
	public ISQLFieldConverter getFieldConverter() {
		return this.getAccordoServizioParteSpecificaFieldConverter();
	}
	
	private AccordoServizioParteSpecificaFetch accordoServizioParteSpecificaFetch = new AccordoServizioParteSpecificaFetch();
	public AccordoServizioParteSpecificaFetch getAccordoServizioParteSpecificaFetch() {
		return this.accordoServizioParteSpecificaFetch;
	}
	@Override
	public IJDBCFetch getFetch() {
		return getAccordoServizioParteSpecificaFetch();
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
	public IdAccordoServizioParteSpecifica convertToId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws NotImplementedException, ServiceException, Exception{
	
		IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica = new IdAccordoServizioParteSpecifica();
		idAccordoServizioParteSpecifica.setTipo(accordoServizioParteSpecifica.getTipo());
        idAccordoServizioParteSpecifica.setNome(accordoServizioParteSpecifica.getNome());
        idAccordoServizioParteSpecifica.setVersione(accordoServizioParteSpecifica.getVersione());
        idAccordoServizioParteSpecifica.setIdErogatore(accordoServizioParteSpecifica.getIdErogatore());
        return idAccordoServizioParteSpecifica;

	}
	
	@Override
	public AccordoServizioParteSpecifica get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAccordoServizioParteSpecifica id, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {
		Long id_accordoServizioParteSpecifica = ( (id!=null && id.getId()!=null && id.getId()>0) ? id.getId() : this.findIdAccordoServizioParteSpecifica(jdbcProperties, log, connection, sqlQueryObject, id, true));
		return this.getEngine(jdbcProperties, log, connection, sqlQueryObject, id_accordoServizioParteSpecifica,idMappingResolutionBehaviour);
		
		
	}
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAccordoServizioParteSpecifica id) throws MultipleResultException, NotImplementedException, ServiceException,Exception {

		Long id_accordoServizioParteSpecifica = this.findIdAccordoServizioParteSpecifica(jdbcProperties, log, connection, sqlQueryObject, id, false);
		return id_accordoServizioParteSpecifica != null && id_accordoServizioParteSpecifica > 0;
		
	}
	
	@Override
	public List<IdAccordoServizioParteSpecifica> findAllIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

		List<IdAccordoServizioParteSpecifica> list = new ArrayList<IdAccordoServizioParteSpecifica>();

		// TODO: implementazione non efficiente. 
		// Per ottenere una implementazione efficiente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari a create l'ID logico
		// 2. Usare metodo getAccordoServizioParteSpecificaFetch() sul risultato della select per ottenere un oggetto AccordoServizioParteSpecifica
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 
		// 3. Usare metodo convertToId per ottenere l'id

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	AccordoServizioParteSpecifica accordoServizioParteSpecifica = this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour);
			IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,accordoServizioParteSpecifica);
        	list.add(idAccordoServizioParteSpecifica);
        }

        return list;
		
	}
	
	private static boolean efficiente = true;
    
    @Override
	public List<AccordoServizioParteSpecifica> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

        List<AccordoServizioParteSpecifica> list = new ArrayList<AccordoServizioParteSpecifica>();
        
        if(efficiente){
        	
        	List<IField> fields = new ArrayList<IField>();
    		fields.add(AccordoServizioParteSpecifica.model().TIPO);
    		fields.add(AccordoServizioParteSpecifica.model().NOME);
    		fields.add(AccordoServizioParteSpecifica.model().VERSIONE);
    		fields.add(AccordoServizioParteSpecifica.model().PORT_TYPE);
    		String aliasAccordoNome = "accordoNome";
    		fields.add(new AliasField(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, aliasAccordoNome));
    		String aliasAccordoVersione = "accordoVersione";
    		fields.add(new AliasField(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, aliasAccordoVersione));
    		String aliasAccordoSoggettoReferenteTipo = "accordoSoggettoReferenteTipo";
    		fields.add(new AliasField(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, aliasAccordoSoggettoReferenteTipo));
    		String aliasAccordoSoggettoReferenteNome = "accordoSoggettoReferenteNome";
    		fields.add(new AliasField(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, aliasAccordoSoggettoReferenteNome));
    		String aliasSoggettoErogatoreTipo = "accordoSoggettoErogatoreTipo";
    		fields.add(new AliasField(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, aliasSoggettoErogatoreTipo));
    		String aliasSoggettoErogatoreNome = "accordoSoggettoErogatoreNome";
    		fields.add(new AliasField(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME, aliasSoggettoErogatoreNome));
        	
    		List<Map<String, Object>> returnMap = null;
    		try{
    			// non usare true altrimenti non funzionano alcuni meccanismi di ricerca, ad es. la valorizzazione dei select field nel servizio della govwayMonitor.
    			// Tanto le join non comportano righe multiple uguali
    			boolean distinct = false; 
    			returnMap = this.select(jdbcProperties, log, connection, sqlQueryObject, expression, distinct, fields.toArray(new IField[1]));
    			
	    		for(Map<String, Object> map: returnMap) {
	    			
	    			AccordoServizioParteSpecifica asps = (AccordoServizioParteSpecifica) this.getAccordoServizioParteSpecificaFetch().fetch(jdbcProperties.getDatabase(), AccordoServizioParteSpecifica.model(), map);
	    			
	    			Object apcNome = this.getObjectFromMap(map, aliasAccordoNome);
	    			Object apcVersione = this.getObjectFromMap(map, aliasAccordoVersione);
	    			Object apcSoggettoReferenteTipo = this.getObjectFromMap(map, aliasAccordoSoggettoReferenteTipo);
	    			Object apcSoggettoReferenteNome = this.getObjectFromMap(map, aliasAccordoSoggettoReferenteNome);
	    			Object soggettoErogatoreTipo = this.getObjectFromMap(map, aliasSoggettoErogatoreTipo);
	    			Object soggettoErogatoreNome = this.getObjectFromMap(map, aliasSoggettoErogatoreNome);
	    			if(apcNome!=null && apcVersione!=null) {
	    				IdAccordoServizioParteComune idAccordoServizioParteComune = new IdAccordoServizioParteComune();
	    				if(apcNome!=null && apcNome instanceof String) {
	    					idAccordoServizioParteComune.setNome((String) apcNome);
	    				}
	    				if(apcVersione!=null && apcVersione instanceof Integer) {
	    					idAccordoServizioParteComune.setVersione((Integer) apcVersione);
	    				}
	    				if(apcSoggettoReferenteTipo!=null && apcSoggettoReferenteNome!=null) {
	    					IdSoggetto idSoggetto = new IdSoggetto();
	    					if(apcSoggettoReferenteTipo!=null && apcSoggettoReferenteTipo instanceof String) {
	    						idSoggetto.setTipo((String) apcSoggettoReferenteTipo);
		    				}
	    					if(apcSoggettoReferenteNome!=null && apcSoggettoReferenteNome instanceof String) {
	    						idSoggetto.setNome((String) apcSoggettoReferenteNome);
		    				}
	    					idAccordoServizioParteComune.setIdSoggetto(idSoggetto);
	    				}
	    				asps.setIdAccordoServizioParteComune(idAccordoServizioParteComune);
	    			}
	    			if(soggettoErogatoreTipo!=null && soggettoErogatoreNome!=null) {
    					IdSoggetto idSoggetto = new IdSoggetto();
    					if(soggettoErogatoreTipo!=null && soggettoErogatoreTipo instanceof String) {
    						idSoggetto.setTipo((String) soggettoErogatoreTipo);
	    				}
    					if(soggettoErogatoreNome!=null && soggettoErogatoreNome instanceof String) {
    						idSoggetto.setNome((String) soggettoErogatoreNome);
	    				}
    					asps.setIdErogatore(idSoggetto);
    				} 			
	    			
	    			list.add(asps);
	    		}
    		}catch(NotFoundException notFound){}
        }

        else{

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
	public AccordoServizioParteSpecifica find(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) 
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
												this.getAccordoServizioParteSpecificaFieldConverter(), AccordoServizioParteSpecifica.model());
		
		sqlQueryObject.addSelectCountField(this.getAccordoServizioParteSpecificaFieldConverter().toTable(AccordoServizioParteSpecifica.model())+".id","tot",true);
		
		joinEngine(expression,sqlQueryObject);
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.count(jdbcProperties, log, connection, sqlQueryObject, expression,
																			this.getAccordoServizioParteSpecificaFieldConverter(), AccordoServizioParteSpecifica.model(),listaQuery);
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAccordoServizioParteSpecifica id) throws NotFoundException, NotImplementedException, ServiceException,Exception {
		
		Long id_accordoServizioParteSpecifica = this.findIdAccordoServizioParteSpecifica(jdbcProperties, log, connection, sqlQueryObject, id, true);
        return this.inUseEngine(jdbcProperties, log, connection, sqlQueryObject, id_accordoServizioParteSpecifica);
		
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
												this.getAccordoServizioParteSpecificaFieldConverter(), field);

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
        						expression, this.getAccordoServizioParteSpecificaFieldConverter(), AccordoServizioParteSpecifica.model(), 
        						listaQuery,listaParams);
		
		joinEngine(expression,sqlQueryObject);
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.select(jdbcProperties, log, connection,
        								org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareSqlQueryObjectForSelectDistinct(sqlQueryObject,sqlQueryObjectDistinct), 
        								expression, this.getAccordoServizioParteSpecificaFieldConverter(), AccordoServizioParteSpecifica.model(),
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
        						this.getAccordoServizioParteSpecificaFieldConverter(), AccordoServizioParteSpecifica.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				joinEngine(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.union(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getAccordoServizioParteSpecificaFieldConverter(), AccordoServizioParteSpecifica.model(), 
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
        						this.getAccordoServizioParteSpecificaFieldConverter(), AccordoServizioParteSpecifica.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				joinEngine(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        NonNegativeNumber number = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.unionCount(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getAccordoServizioParteSpecificaFieldConverter(), AccordoServizioParteSpecifica.model(), 
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
			return new JDBCExpression(this.getAccordoServizioParteSpecificaFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}


	@Override
	public JDBCPaginatedExpression newPaginatedExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(this.getAccordoServizioParteSpecificaFieldConverter());
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
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAccordoServizioParteSpecifica id, AccordoServizioParteSpecifica obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,id,null));
	}
	
	@Override
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, AccordoServizioParteSpecifica obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,tableId,null));
	}
	private void _mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, AccordoServizioParteSpecifica obj, AccordoServizioParteSpecifica imgSaved) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		if(imgSaved==null){
			return;
		}
		obj.setId(imgSaved.getId());
		if(obj.getIdErogatore()!=null && 
				imgSaved.getIdErogatore()!=null){
			obj.getIdErogatore().setId(imgSaved.getIdErogatore().getId());
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
	public AccordoServizioParteSpecifica get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this.getEngine(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId), idMappingResolutionBehaviour);
	}
	
	private AccordoServizioParteSpecifica getEngine(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
				
		AccordoServizioParteSpecifica accordoServizioParteSpecifica = new AccordoServizioParteSpecifica();
		

		// Object accordoServizioParteSpecifica
		ISQLQueryObject sqlQueryObjectGet_accordoServizioParteSpecifica = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_accordoServizioParteSpecifica.setANDLogicOperator(true);
		sqlQueryObjectGet_accordoServizioParteSpecifica.addFromTable(this.getAccordoServizioParteSpecificaFieldConverter().toTable(AccordoServizioParteSpecifica.model()));
		sqlQueryObjectGet_accordoServizioParteSpecifica.addSelectField("id");
		sqlQueryObjectGet_accordoServizioParteSpecifica.addSelectField(this.getAccordoServizioParteSpecificaFieldConverter().toColumn(AccordoServizioParteSpecifica.model().TIPO,true));
		sqlQueryObjectGet_accordoServizioParteSpecifica.addSelectField(this.getAccordoServizioParteSpecificaFieldConverter().toColumn(AccordoServizioParteSpecifica.model().NOME,true));
		sqlQueryObjectGet_accordoServizioParteSpecifica.addSelectField(this.getAccordoServizioParteSpecificaFieldConverter().toColumn(AccordoServizioParteSpecifica.model().VERSIONE,true));
		sqlQueryObjectGet_accordoServizioParteSpecifica.addSelectField(this.getAccordoServizioParteSpecificaFieldConverter().toColumn(AccordoServizioParteSpecifica.model().PORT_TYPE,true));
		sqlQueryObjectGet_accordoServizioParteSpecifica.addWhereCondition("id=?");

		// Get accordoServizioParteSpecifica
		accordoServizioParteSpecifica = (AccordoServizioParteSpecifica) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_accordoServizioParteSpecifica.createSQLQuery(), jdbcProperties.isShowSql(), AccordoServizioParteSpecifica.model(), this.getAccordoServizioParteSpecificaFetch(),
			new JDBCObject(tableId,Long.class));


		// Object _accordoServizioParteSpecifica_soggetto (recupero id)
		ISQLQueryObject sqlQueryObjectGet_accordoServizioParteSpecifica_soggetto_readFkId = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_accordoServizioParteSpecifica_soggetto_readFkId.addFromTable("servizi");
		sqlQueryObjectGet_accordoServizioParteSpecifica_soggetto_readFkId.addSelectField("id_soggetto");
		sqlQueryObjectGet_accordoServizioParteSpecifica_soggetto_readFkId.addWhereCondition("id=?");
		sqlQueryObjectGet_accordoServizioParteSpecifica_soggetto_readFkId.setANDLogicOperator(true);
		Long idFK_accordoServizioParteSpecifica_soggetto = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_accordoServizioParteSpecifica_soggetto_readFkId.createSQLQuery(), jdbcProperties.isShowSql(),Long.class,
				new JDBCObject(accordoServizioParteSpecifica.getId(),Long.class));
		
		// Object _accordoServizioParteSpecifica_soggetto
		ISQLQueryObject sqlQueryObjectGet_accordoServizioParteSpecifica_soggetto = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_accordoServizioParteSpecifica_soggetto.addFromTable("soggetti");
		sqlQueryObjectGet_accordoServizioParteSpecifica_soggetto.addSelectField("tipo_soggetto");
		sqlQueryObjectGet_accordoServizioParteSpecifica_soggetto.addSelectField("nome_soggetto");
		sqlQueryObjectGet_accordoServizioParteSpecifica_soggetto.setANDLogicOperator(true);
		sqlQueryObjectGet_accordoServizioParteSpecifica_soggetto.addWhereCondition("id=?");

		// Recupero _accordoServizioParteSpecifica_soggetto
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_accordoServizioParteSpecifica_soggetto = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(idFK_accordoServizioParteSpecifica_soggetto,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_accordoServizioParteSpecifica_soggetto = new ArrayList<Class<?>>();
		listaFieldIdReturnType_accordoServizioParteSpecifica_soggetto.add(String.class);
		listaFieldIdReturnType_accordoServizioParteSpecifica_soggetto.add(String.class);
		List<Object> listaFieldId_accordoServizioParteSpecifica_soggetto = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_accordoServizioParteSpecifica_soggetto.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_accordoServizioParteSpecifica_soggetto, searchParams_accordoServizioParteSpecifica_soggetto);
		// set _accordoServizioParteSpecifica_soggetto
		IdSoggetto id_accordoServizioParteSpecifica_soggetto = new IdSoggetto();
		id_accordoServizioParteSpecifica_soggetto.setTipo((String)listaFieldId_accordoServizioParteSpecifica_soggetto.get(0));
		id_accordoServizioParteSpecifica_soggetto.setNome((String)listaFieldId_accordoServizioParteSpecifica_soggetto.get(1));
		accordoServizioParteSpecifica.setIdErogatore(id_accordoServizioParteSpecifica_soggetto);

		
		
		// Object _accordoServizioParteSpecifica_accordoServizioParteComune (recupero id)
		ISQLQueryObject sqlQueryObjectGet_accordoServizioParteSpecifica_accordoServizioParteComune_readFkId = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_accordoServizioParteSpecifica_accordoServizioParteComune_readFkId.addFromTable("servizi");
		sqlQueryObjectGet_accordoServizioParteSpecifica_accordoServizioParteComune_readFkId.addSelectField("id_accordo");
		sqlQueryObjectGet_accordoServizioParteSpecifica_accordoServizioParteComune_readFkId.addWhereCondition("id=?");
		sqlQueryObjectGet_accordoServizioParteSpecifica_accordoServizioParteComune_readFkId.setANDLogicOperator(true);
		Long idFK_accordoServizioParteSpecifica_accordoServizioParteComune = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_accordoServizioParteSpecifica_accordoServizioParteComune_readFkId.createSQLQuery(), jdbcProperties.isShowSql(),Long.class,
				new JDBCObject(accordoServizioParteSpecifica.getId(),Long.class));
		
		// Object _accordoServizioParteSpecifica_accordoServizioParteComune
		ISQLQueryObject sqlQueryObjectGet_accordoServizioParteSpecifica_accordoServizioParteComune = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_accordoServizioParteSpecifica_accordoServizioParteComune.addFromTable("accordi");
		sqlQueryObjectGet_accordoServizioParteSpecifica_accordoServizioParteComune.addSelectField("nome");
		sqlQueryObjectGet_accordoServizioParteSpecifica_accordoServizioParteComune.addSelectField("id_referente");
		sqlQueryObjectGet_accordoServizioParteSpecifica_accordoServizioParteComune.addSelectField("versione");
		sqlQueryObjectGet_accordoServizioParteSpecifica_accordoServizioParteComune.addSelectField("service_binding");
		sqlQueryObjectGet_accordoServizioParteSpecifica_accordoServizioParteComune.setANDLogicOperator(true);
		sqlQueryObjectGet_accordoServizioParteSpecifica_accordoServizioParteComune.addWhereCondition("id=?");

		// Recupero _accordoServizioParteSpecifica_accordoServizioParteComune
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_accordoServizioParteSpecifica_accordoServizioParteComune = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(idFK_accordoServizioParteSpecifica_accordoServizioParteComune,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_accordoServizioParteSpecifica_accordoServizioParteComune = new ArrayList<Class<?>>();
		listaFieldIdReturnType_accordoServizioParteSpecifica_accordoServizioParteComune.add(String.class);
		listaFieldIdReturnType_accordoServizioParteSpecifica_accordoServizioParteComune.add(Long.class);
		listaFieldIdReturnType_accordoServizioParteSpecifica_accordoServizioParteComune.add(Integer.class);
		listaFieldIdReturnType_accordoServizioParteSpecifica_accordoServizioParteComune.add(String.class);

		List<Object> listaFieldId_accordoServizioParteSpecifica_accordoServizioParteComune = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_accordoServizioParteSpecifica_accordoServizioParteComune.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_accordoServizioParteSpecifica_accordoServizioParteComune, searchParams_accordoServizioParteSpecifica_accordoServizioParteComune);
		String accordo_nome = (String) listaFieldId_accordoServizioParteSpecifica_accordoServizioParteComune.get(0);
		Long accordo_id_referente = (Long) listaFieldId_accordoServizioParteSpecifica_accordoServizioParteComune.get(1);
		Integer accordo_versione = (Integer) listaFieldId_accordoServizioParteSpecifica_accordoServizioParteComune.get(2);
		String accordo_serviceBinding = (String) listaFieldId_accordoServizioParteSpecifica_accordoServizioParteComune.get(3);
		
		// Recupero SoggettoReferente accordo
		Soggetto accordo_id_referente_soggetto = null;
		if(accordo_id_referente!=null && accordo_id_referente>0){
			ISoggettoServiceSearch soggettoServiceSearch = this.getServiceManager(connection, jdbcProperties, log).getSoggettoServiceSearch();
			accordo_id_referente_soggetto = ((IDBSoggettoServiceSearch)soggettoServiceSearch).get(accordo_id_referente);
		}
		
		// set _accordoServizioParteSpecifica_accordoServizioParteComune
		IdAccordoServizioParteComune idAccordoServizioParteComune = new IdAccordoServizioParteComune();
		idAccordoServizioParteComune.setNome(accordo_nome);
		idAccordoServizioParteComune.setVersione(accordo_versione);
		idAccordoServizioParteComune.setServiceBinding(accordo_serviceBinding);
		IdSoggetto idAccordoServizioParteComune_referente = null;
		if(accordo_id_referente_soggetto!=null){
			idAccordoServizioParteComune_referente = new IdSoggetto();
			idAccordoServizioParteComune_referente.setTipo(accordo_id_referente_soggetto.getTipoSoggetto());
			idAccordoServizioParteComune_referente.setNome(accordo_id_referente_soggetto.getNomeSoggetto());
		}
		idAccordoServizioParteComune.setIdSoggetto(idAccordoServizioParteComune_referente);
		accordoServizioParteSpecifica.setIdAccordoServizioParteComune(idAccordoServizioParteComune);
		            
        return accordoServizioParteSpecifica;  
	
	} 
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._exists(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	private boolean _exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		boolean existsAccordoServizioParteSpecifica = false;

		sqlQueryObject = sqlQueryObject.newSQLQueryObject();
		sqlQueryObject.setANDLogicOperator(true);

		sqlQueryObject.addFromTable(this.getAccordoServizioParteSpecificaFieldConverter().toTable(AccordoServizioParteSpecifica.model()));
		sqlQueryObject.addSelectField(this.getAccordoServizioParteSpecificaFieldConverter().toColumn(AccordoServizioParteSpecifica.model().TIPO,true));
		sqlQueryObject.addWhereCondition("id=?");


		// Exists accordoServizioParteSpecifica
		existsAccordoServizioParteSpecifica = jdbcUtilities.exists(sqlQueryObject.createSQLQuery(), jdbcProperties.isShowSql(),
			new JDBCObject(tableId,Long.class));

		
        return existsAccordoServizioParteSpecifica;
	
	}
	
	private void joinEngine(IExpression expression, ISQLQueryObject sqlQueryObject) throws NotImplementedException, ServiceException, Exception{
	
		if(expression.inUseModel(AccordoServizioParteSpecifica.model().ID_EROGATORE,false)){
			String tableName1 = this.getAccordoServizioParteSpecificaFieldConverter().toTable(AccordoServizioParteSpecifica.model());
			//String tableName2 = "sogerog";
			String tableName2 = this.getAccordoServizioParteSpecificaFieldConverter().toAliasTable(AccordoServizioParteSpecifica.model().ID_EROGATORE);
			sqlQueryObject.addWhereCondition(tableName1+".id_soggetto="+tableName2+".id");
		}
		if(expression.inUseModel(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE,false)){
			String tableName1 = this.getAccordoServizioParteSpecificaFieldConverter().toTable(AccordoServizioParteSpecifica.model());
			String tableName2 = this.getAccordoServizioParteSpecificaFieldConverter().toTable(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE);
			sqlQueryObject.addWhereCondition(tableName1+".id_accordo="+tableName2+".id");
		}
		if(expression.inUseModel(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO,false)){
			String tableName1 = this.getAccordoServizioParteSpecificaFieldConverter().toTable(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE);
			//String tableName2 = "sogref";
			String tableName2 = this.getAccordoServizioParteSpecificaFieldConverter().toAliasTable(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO);
			sqlQueryObject.addWhereCondition(tableName1+".id_referente="+tableName2+".id");
		}
		
		if(expression.inUseModel(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO,false)){
			if(expression.inUseModel(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE,false)==false){
				sqlQueryObject.addFromTable(this.getAccordoServizioParteSpecificaFieldConverter().toTable(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE));
			}
		}
        
	}
	
	protected java.util.List<Object> getRootTablePrimaryKeyValuesEngine(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAccordoServizioParteSpecifica id) throws NotFoundException, ServiceException, NotImplementedException, Exception{
	    // Identificativi
        java.util.List<Object> rootTableIdValues = new java.util.ArrayList<>();
        Long longId = this.findIdAccordoServizioParteSpecifica(jdbcProperties, log, connection, sqlQueryObject.newSQLQueryObject(), id, true);
		rootTableIdValues.add(longId); 
        return rootTableIdValues;
	}
	
	protected Map<String, List<IField>> getMapTableToPKColumnEngine() throws NotImplementedException, Exception{
	
		AccordoServizioParteSpecificaFieldConverter converter = this.getAccordoServizioParteSpecificaFieldConverter();
		Map<String, List<IField>> mapTableToPKColumn = new java.util.HashMap<>();
		UtilsTemplate<IField> utilities = new UtilsTemplate<>();

		// AccordoServizioParteSpecifica.model()
		mapTableToPKColumn.put(converter.toTable(AccordoServizioParteSpecifica.model()),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AccordoServizioParteSpecifica.model()))
			));

		// AccordoServizioParteSpecifica.model().ID_EROGATORE
		mapTableToPKColumn.put(converter.toTable(AccordoServizioParteSpecifica.model().ID_EROGATORE),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AccordoServizioParteSpecifica.model().ID_EROGATORE))
			));

		// AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE
		mapTableToPKColumn.put(converter.toTable(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE))
			));

		// AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO
		mapTableToPKColumn.put(converter.toTable(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO))
			));

        return mapTableToPKColumn;		
	}
	
	@Override
	public List<Long> findAllTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression paginatedExpression) throws ServiceException, NotImplementedException, Exception {
		
		List<Long> list = new ArrayList<Long>();

		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getAccordoServizioParteSpecificaFieldConverter().toTable(AccordoServizioParteSpecifica.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareFindAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
												this.getAccordoServizioParteSpecificaFieldConverter(), AccordoServizioParteSpecifica.model());
		
		joinEngine(paginatedExpression,sqlQueryObject);
		
		List<Object> listObjects = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.findAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
																			this.getAccordoServizioParteSpecificaFieldConverter(), AccordoServizioParteSpecifica.model(), objectIdClass, listaQuery);
		for(Object object: listObjects) {
			list.add((Long)object);
		}

        return list;
		
	}
	
	@Override
	public long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, Exception {
	
		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getAccordoServizioParteSpecificaFieldConverter().toTable(AccordoServizioParteSpecifica.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareFind(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getAccordoServizioParteSpecificaFieldConverter(), AccordoServizioParteSpecifica.model());
		
		joinEngine(expression,sqlQueryObject);

		Object res = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.find(jdbcProperties, log, connection, sqlQueryObject, expression,
														this.getAccordoServizioParteSpecificaFieldConverter(), AccordoServizioParteSpecifica.model(), objectIdClass, listaQuery);
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
	public IdAccordoServizioParteSpecifica findId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();

		// Object _accordoServizioParteSpecifica
		sqlQueryObjectGet.addFromTable(this.getAccordoServizioParteSpecificaFieldConverter().toTable(AccordoServizioParteSpecifica.model()));
		sqlQueryObjectGet.addSelectField(this.getAccordoServizioParteSpecificaFieldConverter().toColumn(AccordoServizioParteSpecifica.model().TIPO,true));
		sqlQueryObjectGet.addSelectField(this.getAccordoServizioParteSpecificaFieldConverter().toColumn(AccordoServizioParteSpecifica.model().NOME,true));
		sqlQueryObjectGet.addSelectField(this.getAccordoServizioParteSpecificaFieldConverter().toColumn(AccordoServizioParteSpecifica.model().VERSIONE,true));
		sqlQueryObjectGet.addSelectField("id_soggetto");
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addWhereCondition("id=?");

		// Recupero _accordoServizioParteSpecifica
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_accordoServizioParteSpecifica = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(tableId,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_accordoServizioParteSpecifica = new ArrayList<Class<?>>();
		listaFieldIdReturnType_accordoServizioParteSpecifica.add(String.class);
		listaFieldIdReturnType_accordoServizioParteSpecifica.add(String.class);
		listaFieldIdReturnType_accordoServizioParteSpecifica.add(Integer.class);
		listaFieldIdReturnType_accordoServizioParteSpecifica.add(Long.class);
		org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica id_accordoServizioParteSpecifica = null;
		List<Object> listaFieldId_accordoServizioParteSpecifica = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_accordoServizioParteSpecifica, searchParams_accordoServizioParteSpecifica);
		if(listaFieldId_accordoServizioParteSpecifica==null || listaFieldId_accordoServizioParteSpecifica.size()<=0){
			if(throwNotFound){
				throw org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.newNotFoundException();
			}
		}
		else{
			// set _accordoServizioParteSpecifica
			id_accordoServizioParteSpecifica = new org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica();
			id_accordoServizioParteSpecifica.setTipo((String)listaFieldId_accordoServizioParteSpecifica.get(0));
			id_accordoServizioParteSpecifica.setNome((String)listaFieldId_accordoServizioParteSpecifica.get(1));
			id_accordoServizioParteSpecifica.setVersione((Integer)listaFieldId_accordoServizioParteSpecifica.get(2));
			Long idSoggettoFK = (Long) listaFieldId_accordoServizioParteSpecifica.get(3);
			id_accordoServizioParteSpecifica.
				setIdErogatore(((IDBSoggettoServiceSearch)this.getServiceManager(connection, jdbcProperties, log).
						getSoggettoServiceSearch()).findId(idSoggettoFK, true));
		}
		
		return id_accordoServizioParteSpecifica;
		
	}

	@Override
	public Long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAccordoServizioParteSpecifica id, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
	
		return this.findIdAccordoServizioParteSpecifica(jdbcProperties,log,connection,sqlQueryObject,id,throwNotFound);
			
	}
	
	@Override
	public List<List<Object>> nativeQuery(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
											String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException,Exception{
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.nativeQuery(jdbcProperties, log, connection, sqlQueryObject,
																							sql,returnClassTypes,param);
														
	}
	
	protected Long findIdAccordoServizioParteSpecifica(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAccordoServizioParteSpecifica id, boolean throwNotFound) throws NotFoundException, ServiceException, NotImplementedException, Exception {

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

		if(id.getIdErogatore()==null){
			throw new ServiceException("IdSoggettoErogatore non fornito");
		}
		
		// Recupero id soggetto
		ISoggettoServiceSearch soggettoServiceSearch = this.getServiceManager(connection, jdbcProperties, log).getSoggettoServiceSearch();
		Soggetto accordo_id_referente_soggetto = ((IDBSoggettoServiceSearch)soggettoServiceSearch).get(id.getIdErogatore());
			
		// Object _accordoServizioParteSpecifica
		sqlQueryObjectGet.addFromTable(this.getAccordoServizioParteSpecificaFieldConverter().toTable(AccordoServizioParteSpecifica.model()));
		sqlQueryObjectGet.addSelectField("id");
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.setSelectDistinct(true);
		sqlQueryObjectGet.addWhereCondition(this.getAccordoServizioParteSpecificaFieldConverter().toColumn(AccordoServizioParteSpecifica.model().TIPO,true)+"=?");
		sqlQueryObjectGet.addWhereCondition(this.getAccordoServizioParteSpecificaFieldConverter().toColumn(AccordoServizioParteSpecifica.model().NOME,true)+"=?");
		sqlQueryObjectGet.addWhereCondition(this.getAccordoServizioParteSpecificaFieldConverter().toColumn(AccordoServizioParteSpecifica.model().VERSIONE,true)+"=?");
		sqlQueryObjectGet.addWhereCondition("id_soggetto=?");

		// Recupero _accordoServizioParteSpecifica
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_accordoServizioParteSpecifica = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id.getTipo(),String.class),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id.getNome(),String.class),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id.getVersione(),Integer.class),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(accordo_id_referente_soggetto.getId(),Long.class)
		};
		Long id_accordoServizioParteSpecifica = null;
		try{
			id_accordoServizioParteSpecifica = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
						Long.class, searchParams_accordoServizioParteSpecifica);
		}catch(NotFoundException notFound){
			if(throwNotFound){
				throw new NotFoundException(notFound);
			}
		}
		if(id_accordoServizioParteSpecifica==null || id_accordoServizioParteSpecifica<=0){
			if(throwNotFound){
				throw org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.newNotFoundException();
			}
		}
		
		return id_accordoServizioParteSpecifica;
	}
}
