/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

import org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer;
import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;
import org.openspcoop2.core.transazioni.dao.jdbc.converter.TransazioneApplicativoServerFieldConverter;
import org.openspcoop2.core.transazioni.dao.jdbc.fetch.TransazioneApplicativoServerFetch;
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
 * JDBCTransazioneApplicativoServerServiceSearchImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCTransazioneApplicativoServerServiceSearchImpl implements IJDBCServiceSearchWithId<TransazioneApplicativoServer, IdTransazioneApplicativoServer, JDBCServiceManager> {

	private TransazioneApplicativoServerFieldConverter _transazioneApplicativoServerFieldConverter = null;
	public TransazioneApplicativoServerFieldConverter getTransazioneApplicativoServerFieldConverter() {
		if(this._transazioneApplicativoServerFieldConverter==null){
			this._transazioneApplicativoServerFieldConverter = new TransazioneApplicativoServerFieldConverter(this.jdbcServiceManager.getJdbcProperties().getDatabaseType());
		}		
		return this._transazioneApplicativoServerFieldConverter;
	}
	@Override
	public ISQLFieldConverter getFieldConverter() {
		return this.getTransazioneApplicativoServerFieldConverter();
	}
	
	private TransazioneApplicativoServerFetch transazioneApplicativoServerFetch = new TransazioneApplicativoServerFetch();
	public TransazioneApplicativoServerFetch getTransazioneApplicativoServerFetch() {
		return this.transazioneApplicativoServerFetch;
	}
	@Override
	public IJDBCFetch getFetch() {
		return getTransazioneApplicativoServerFetch();
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
	public IdTransazioneApplicativoServer convertToId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, TransazioneApplicativoServer transazioneApplicativoServer) throws NotImplementedException, ServiceException, Exception{
	
		IdTransazioneApplicativoServer idTransazioneApplicativoServer = new IdTransazioneApplicativoServer();
		idTransazioneApplicativoServer.setIdTransazione(transazioneApplicativoServer.getIdTransazione());
		idTransazioneApplicativoServer.setServizioApplicativoErogatore(transazioneApplicativoServer.getServizioApplicativoErogatore());
		return idTransazioneApplicativoServer;
	}
	
	@Override
	public TransazioneApplicativoServer get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdTransazioneApplicativoServer id, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {
		
		boolean efficente = true;
        
		long id_long = (id!=null && id.getId()!=null && id.getId()>0) ? id.getId() : -1;
		
        if(id_long<=0 && efficente){
		
        	if(id==null) {
    			throw new ServiceException("Id non definito");
    		}
    		if(id.getIdTransazione()==null) {
    			throw new ServiceException("IdTransazione non definito");
    		}
    		if(id.getServizioApplicativoErogatore()==null) {
    			throw new ServiceException("IdServizioApplicativoErogatore non definito");
    		}
        	
    		JDBCPaginatedExpression pagExpr = this.newPaginatedExpression(log);
        	pagExpr.equals(TransazioneApplicativoServer.model().ID_TRANSAZIONE, id.getIdTransazione());
			pagExpr.and();
			pagExpr.equals(TransazioneApplicativoServer.model().SERVIZIO_APPLICATIVO_EROGATORE, id.getServizioApplicativoErogatore());
			//pagExpr.limit(2); Inefficente, per implementare il multipleresult che poi non può succedere
			if(sqlQueryObject==null || !(sqlQueryObject instanceof SQLQueryObjectCore) || !((SQLQueryObjectCore)sqlQueryObject).isSelectForUpdate()) {
				pagExpr.limit(1);
				// essendoci lo unique comunque il limit serve a poco, e nello stesso tempo la select for update non lo vuole
			}
			        	
        	List<TransazioneApplicativoServer> list = findAll(jdbcProperties, log, connection, sqlQueryObject, pagExpr, idMappingResolutionBehaviour);
        	
        	if(list==null || list.size()<1) {
        		throw new NotFoundException();
        	}
        	// C'è lo unique sulle due colonne
//        	else if(list.size()>1) {
//        		throw new MultipleResultException();
//        	}
        	else {
        		return list.get(0);
        	}
        	
        }
        else {
		
        	Long id_transazioneApplicativoServer = ( (id!=null && id.getId()!=null && id.getId()>0) ? id.getId() : this.findIdTransazioneApplicativoServer(jdbcProperties, log, connection, sqlQueryObject, id, true));
        	return this._get(jdbcProperties, log, connection, sqlQueryObject, id_transazioneApplicativoServer,idMappingResolutionBehaviour);
		
        }
	}
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdTransazioneApplicativoServer id) throws MultipleResultException, NotImplementedException, ServiceException,Exception {

		Long id_transazioneApplicativoServer = this.findIdTransazioneApplicativoServer(jdbcProperties, log, connection, sqlQueryObject, id, false);
		return id_transazioneApplicativoServer != null && id_transazioneApplicativoServer > 0;
		
	}
	
	@Override
	public List<IdTransazioneApplicativoServer> findAllIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

		List<IdTransazioneApplicativoServer> list = new ArrayList<IdTransazioneApplicativoServer>();

		// TODO: implementazione non efficente. 
		// Per ottenere una implementazione efficente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari a create l'ID logico
		// 2. Usare metodo getTransazioneApplicativoServerFetch() sul risultato della select per ottenere un oggetto TransazioneApplicativoServer
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 
		// 3. Usare metodo convertToId per ottenere l'id

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	TransazioneApplicativoServer transazioneApplicativoServer = this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour);
			IdTransazioneApplicativoServer idTransazioneApplicativoServer = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,transazioneApplicativoServer);
        	list.add(idTransazioneApplicativoServer);
        }

        return list;
		
	}
	
	@Override
	public List<TransazioneApplicativoServer> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

        List<TransazioneApplicativoServer> list = new ArrayList<TransazioneApplicativoServer>();

        boolean efficente = true;
        
        if(efficente){
        
        	List<IField> fields = new ArrayList<IField>();
        	fields.add(new CustomField("id", Long.class, "id", this.getTransazioneApplicativoServerFieldConverter().toTable(TransazioneApplicativoServer.model())));  
    		fields.add(TransazioneApplicativoServer.model().ID_TRANSAZIONE);
    		fields.add(TransazioneApplicativoServer.model().SERVIZIO_APPLICATIVO_EROGATORE);
    		fields.add(TransazioneApplicativoServer.model().CONNETTORE_NOME);
    		fields.add(TransazioneApplicativoServer.model().DATA_REGISTRAZIONE);
    		// NONSERIALIZZATO SU DB fields.add(TransazioneApplicativoServer.model().PROTOCOLLO);
    		fields.add(TransazioneApplicativoServer.model().CONSEGNA_TERMINATA);
    		fields.add(TransazioneApplicativoServer.model().DATA_MESSAGGIO_SCADUTO);
    		fields.add(TransazioneApplicativoServer.model().DETTAGLIO_ESITO);
    		fields.add(TransazioneApplicativoServer.model().CONSEGNA_TRASPARENTE);
    		fields.add(TransazioneApplicativoServer.model().CONSEGNA_INTEGRATION_MANAGER);
    		fields.add(TransazioneApplicativoServer.model().IDENTIFICATIVO_MESSAGGIO);
    		fields.add(TransazioneApplicativoServer.model().DATA_ACCETTAZIONE_RICHIESTA);
    		fields.add(TransazioneApplicativoServer.model().DATA_USCITA_RICHIESTA);
    		fields.add(TransazioneApplicativoServer.model().DATA_ACCETTAZIONE_RISPOSTA);
    		fields.add(TransazioneApplicativoServer.model().DATA_INGRESSO_RISPOSTA);
    		fields.add(TransazioneApplicativoServer.model().RICHIESTA_USCITA_BYTES);
    		fields.add(TransazioneApplicativoServer.model().RISPOSTA_INGRESSO_BYTES);
    		fields.add(TransazioneApplicativoServer.model().LOCATION_CONNETTORE);
    		fields.add(TransazioneApplicativoServer.model().CODICE_RISPOSTA);
    		fields.add(TransazioneApplicativoServer.model().FAULT);
    		fields.add(TransazioneApplicativoServer.model().FORMATO_FAULT);
    		fields.add(TransazioneApplicativoServer.model().DATA_PRIMO_TENTATIVO);
    		fields.add(TransazioneApplicativoServer.model().NUMERO_TENTATIVI);
    		fields.add(TransazioneApplicativoServer.model().CLUSTER_ID_PRESA_IN_CARICO);
    		fields.add(TransazioneApplicativoServer.model().CLUSTER_ID_CONSEGNA);
    		fields.add(TransazioneApplicativoServer.model().DATA_ULTIMO_ERRORE);
    		fields.add(TransazioneApplicativoServer.model().DETTAGLIO_ESITO_ULTIMO_ERRORE);
    		fields.add(TransazioneApplicativoServer.model().CODICE_RISPOSTA_ULTIMO_ERRORE);
    		fields.add(TransazioneApplicativoServer.model().ULTIMO_ERRORE);
    		fields.add(TransazioneApplicativoServer.model().LOCATION_ULTIMO_ERRORE);
    		fields.add(TransazioneApplicativoServer.model().CLUSTER_ID_ULTIMO_ERRORE);
    		fields.add(TransazioneApplicativoServer.model().FAULT_ULTIMO_ERRORE);
    		fields.add(TransazioneApplicativoServer.model().FORMATO_FAULT_ULTIMO_ERRORE);
    		fields.add(TransazioneApplicativoServer.model().DATA_PRIMO_PRELIEVO_IM);
    		fields.add(TransazioneApplicativoServer.model().DATA_PRELIEVO_IM);
    		fields.add(TransazioneApplicativoServer.model().NUMERO_PRELIEVI_IM);
    		fields.add(TransazioneApplicativoServer.model().DATA_ELIMINAZIONE_IM);
    		fields.add(TransazioneApplicativoServer.model().CLUSTER_ID_PRELIEVO_IM);
    		fields.add(TransazioneApplicativoServer.model().CLUSTER_ID_ELIMINAZIONE_IM);
        	
    		List<Map<String, Object>> returnMap = null;
    		try{
    			 // Il distinct serve solo se ci sono le ricerche con contenuto.
    	        // NOTA: il distinct rende le ricerce inefficenti (ed inoltre non e' utilizzabile con campi clob in oracle)
    	        boolean distinct = false;
    	        
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
		    			list.add((TransazioneApplicativoServer)this.getTransazioneApplicativoServerFetch().fetch(jdbcProperties.getDatabase(), TransazioneApplicativoServer.model(), map));
		    		}
		    		
    	        }
		    		
    		}catch(NotFoundException notFound){}
        }
        else {
       
	        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
	        
	        for(Long id: ids) {
	        	list.add(this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour));
	        }
	        
        }

        return list;      
		
	}
	
	@Override
	public TransazioneApplicativoServer find(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) 
		throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {

		boolean efficente = true;
        
        if(efficente){
		
        	JDBCPaginatedExpression pagExpr = this.toPaginatedExpression(expression, log);
        	pagExpr.limit(2);
        	
        	List<TransazioneApplicativoServer> list = findAll(jdbcProperties, log, connection, sqlQueryObject, pagExpr, idMappingResolutionBehaviour);
        	
        	if(list==null || list.size()<1) {
        		throw new NotFoundException();
        	}
        	else if(list.size()>1) {
        		throw new MultipleResultException();
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
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareCount(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getTransazioneApplicativoServerFieldConverter(), TransazioneApplicativoServer.model());
		
		sqlQueryObject.addSelectCountField(this.getTransazioneApplicativoServerFieldConverter().toTable(TransazioneApplicativoServer.model())+".id","tot",true);
		
		_join(expression,sqlQueryObject);
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.count(jdbcProperties, log, connection, sqlQueryObject, expression,
																			this.getTransazioneApplicativoServerFieldConverter(), TransazioneApplicativoServer.model(),listaQuery);
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdTransazioneApplicativoServer id) throws NotFoundException, NotImplementedException, ServiceException,Exception {
		
		Long id_transazioneApplicativoServer = this.findIdTransazioneApplicativoServer(jdbcProperties, log, connection, sqlQueryObject, id, true);
        return this._inUse(jdbcProperties, log, connection, sqlQueryObject, id_transazioneApplicativoServer);
		
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
												this.getTransazioneApplicativoServerFieldConverter(), field);

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
        						expression, this.getTransazioneApplicativoServerFieldConverter(), TransazioneApplicativoServer.model(), 
        						listaQuery,listaParams);
		
		_join(expression,sqlQueryObject);
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.select(jdbcProperties, log, connection,
        								org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareSqlQueryObjectForSelectDistinct(sqlQueryObject,sqlQueryObjectDistinct), 
        								expression, this.getTransazioneApplicativoServerFieldConverter(), TransazioneApplicativoServer.model(),
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
        						this.getTransazioneApplicativoServerFieldConverter(), TransazioneApplicativoServer.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.union(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getTransazioneApplicativoServerFieldConverter(), TransazioneApplicativoServer.model(), 
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
        						this.getTransazioneApplicativoServerFieldConverter(), TransazioneApplicativoServer.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        NonNegativeNumber number = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.unionCount(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getTransazioneApplicativoServerFieldConverter(), TransazioneApplicativoServer.model(), 
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
			return new JDBCExpression(this.getTransazioneApplicativoServerFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}


	@Override
	public JDBCPaginatedExpression newPaginatedExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(this.getTransazioneApplicativoServerFieldConverter());
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
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdTransazioneApplicativoServer id, TransazioneApplicativoServer obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,id,null));
	}
	
	@Override
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, TransazioneApplicativoServer obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,tableId,null));
	}
	private void _mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, TransazioneApplicativoServer obj, TransazioneApplicativoServer imgSaved) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		if(imgSaved==null){
			return;
		}
		obj.setId(imgSaved.getId());

	}
	
	@Override
	public TransazioneApplicativoServer get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._get(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId), idMappingResolutionBehaviour);
	}
	
	private TransazioneApplicativoServer _get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
				
		TransazioneApplicativoServer transazioneApplicativoServer = new TransazioneApplicativoServer();
		

		// Object transazioneApplicativoServer
		ISQLQueryObject sqlQueryObjectGet_transazioneApplicativoServer = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_transazioneApplicativoServer.setANDLogicOperator(true);
		sqlQueryObjectGet_transazioneApplicativoServer.addFromTable(this.getTransazioneApplicativoServerFieldConverter().toTable(TransazioneApplicativoServer.model()));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField("id");
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().ID_TRANSAZIONE,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().SERVIZIO_APPLICATIVO_EROGATORE,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CONNETTORE_NOME,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_REGISTRAZIONE,true));
		// NONSERIALIZZATO SU DB sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().PROTOCOLLO,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CONSEGNA_TERMINATA,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_MESSAGGIO_SCADUTO,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DETTAGLIO_ESITO,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CONSEGNA_TRASPARENTE,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CONSEGNA_INTEGRATION_MANAGER,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().IDENTIFICATIVO_MESSAGGIO,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_ACCETTAZIONE_RICHIESTA,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_USCITA_RICHIESTA,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_ACCETTAZIONE_RISPOSTA,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_INGRESSO_RISPOSTA,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().RICHIESTA_USCITA_BYTES,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().RISPOSTA_INGRESSO_BYTES,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().LOCATION_CONNETTORE,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CODICE_RISPOSTA,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().FAULT,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().FORMATO_FAULT,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_PRIMO_TENTATIVO,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().NUMERO_TENTATIVI,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CLUSTER_ID_PRESA_IN_CARICO,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CLUSTER_ID_CONSEGNA,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_ULTIMO_ERRORE,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DETTAGLIO_ESITO_ULTIMO_ERRORE,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CODICE_RISPOSTA_ULTIMO_ERRORE,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().ULTIMO_ERRORE,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().LOCATION_ULTIMO_ERRORE,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CLUSTER_ID_ULTIMO_ERRORE,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().FAULT_ULTIMO_ERRORE,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().FORMATO_FAULT_ULTIMO_ERRORE,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_PRIMO_PRELIEVO_IM,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_PRELIEVO_IM,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().NUMERO_PRELIEVI_IM,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().DATA_ELIMINAZIONE_IM,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CLUSTER_ID_PRELIEVO_IM,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().CLUSTER_ID_ELIMINAZIONE_IM,true));
		sqlQueryObjectGet_transazioneApplicativoServer.addWhereCondition("id=?");

		// Get transazioneApplicativoServer
		transazioneApplicativoServer = (TransazioneApplicativoServer) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_transazioneApplicativoServer.createSQLQuery(), jdbcProperties.isShowSql(), TransazioneApplicativoServer.model(), this.getTransazioneApplicativoServerFetch(),
			new JDBCObject(tableId,Long.class));



		
        return transazioneApplicativoServer;  
	
	} 
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._exists(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	private boolean _exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		boolean existsTransazioneApplicativoServer = false;

		sqlQueryObject = sqlQueryObject.newSQLQueryObject();
		sqlQueryObject.setANDLogicOperator(true);

		sqlQueryObject.addFromTable(this.getTransazioneApplicativoServerFieldConverter().toTable(TransazioneApplicativoServer.model()));
		sqlQueryObject.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().ID_TRANSAZIONE,true));
		sqlQueryObject.addWhereCondition("id=?");


		// Exists transazioneApplicativoServer
		existsTransazioneApplicativoServer = jdbcUtilities.exists(sqlQueryObject.createSQLQuery(), jdbcProperties.isShowSql(),
			new JDBCObject(tableId,Long.class));

		
        return existsTransazioneApplicativoServer;
	
	}
	
	private void _join(IExpression expression, ISQLQueryObject sqlQueryObject) throws NotImplementedException, ServiceException, Exception{
	
		// NOP;
        
	}
	
	protected java.util.List<Object> _getRootTablePrimaryKeyValues(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdTransazioneApplicativoServer id) throws NotFoundException, ServiceException, NotImplementedException, Exception{
	    // Identificativi
        java.util.List<Object> rootTableIdValues = new java.util.ArrayList<Object>();
        Long longId = this.findIdTransazioneApplicativoServer(jdbcProperties, log, connection, sqlQueryObject.newSQLQueryObject(), id, true);
		rootTableIdValues.add(longId);
        return rootTableIdValues;
	}
	
	protected Map<String, List<IField>> _getMapTableToPKColumn() throws NotImplementedException, Exception{
	
		TransazioneApplicativoServerFieldConverter converter = this.getTransazioneApplicativoServerFieldConverter();
		Map<String, List<IField>> mapTableToPKColumn = new java.util.HashMap<String, List<IField>>();
		UtilsTemplate<IField> utilities = new UtilsTemplate<IField>();

		// TransazioneApplicativoServer.model()
		mapTableToPKColumn.put(converter.toTable(TransazioneApplicativoServer.model()),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(TransazioneApplicativoServer.model()))
			));

        
        return mapTableToPKColumn;		
	}
	
	@Override
	public List<Long> findAllTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression paginatedExpression) throws ServiceException, NotImplementedException, Exception {
		
		List<Long> list = new ArrayList<Long>();

		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toTable(TransazioneApplicativoServer.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareFindAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
												this.getTransazioneApplicativoServerFieldConverter(), TransazioneApplicativoServer.model());
		
		_join(paginatedExpression,sqlQueryObject);
		
		List<Object> listObjects = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.findAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
																			this.getTransazioneApplicativoServerFieldConverter(), TransazioneApplicativoServer.model(), objectIdClass, listaQuery);
		for(Object object: listObjects) {
			list.add((Long)object);
		}

        return list;
		
	}
	
	@Override
	public long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, Exception {
	
		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toTable(TransazioneApplicativoServer.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareFind(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getTransazioneApplicativoServerFieldConverter(), TransazioneApplicativoServer.model());
		
		_join(expression,sqlQueryObject);

		Object res = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.find(jdbcProperties, log, connection, sqlQueryObject, expression,
														this.getTransazioneApplicativoServerFieldConverter(), TransazioneApplicativoServer.model(), objectIdClass, listaQuery);
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
	public IdTransazioneApplicativoServer findId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();            

		// Object _transazioneApplicativoServer
		sqlQueryObjectGet.addFromTable(this.getTransazioneApplicativoServerFieldConverter().toTable(TransazioneApplicativoServer.model()));
		sqlQueryObjectGet.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().ID_TRANSAZIONE,true));
		sqlQueryObjectGet.addSelectField(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().SERVIZIO_APPLICATIVO_EROGATORE,true));
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addWhereCondition("id=?");

		// Recupero _transazioneApplicativoServer
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_transazioneApplicativoServer = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(tableId,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_transazioneApplicativoServer = new ArrayList<Class<?>>();
		listaFieldIdReturnType_transazioneApplicativoServer.add(String.class);
		listaFieldIdReturnType_transazioneApplicativoServer.add(String.class);
		org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer id_transazioneApplicativoServer = null;
		List<Object> listaFieldId_transazioneApplicativoServer = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_transazioneApplicativoServer, searchParams_transazioneApplicativoServer);
		if(listaFieldId_transazioneApplicativoServer==null || listaFieldId_transazioneApplicativoServer.size()<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		else{
			// set _transazioneApplicativoServer
			id_transazioneApplicativoServer = new org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer();
			id_transazioneApplicativoServer.setIdTransazione((String)listaFieldId_transazioneApplicativoServer.get(0));
			id_transazioneApplicativoServer.setServizioApplicativoErogatore((String)listaFieldId_transazioneApplicativoServer.get(1));
		}
		
		return id_transazioneApplicativoServer;
		
	}

	@Override
	public Long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdTransazioneApplicativoServer id, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
	
		return this.findIdTransazioneApplicativoServer(jdbcProperties,log,connection,sqlQueryObject,id,throwNotFound);
			
	}
	
	@Override
	public List<List<Object>> nativeQuery(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
											String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException,Exception{
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.nativeQuery(jdbcProperties, log, connection, sqlQueryObject,
																							sql,returnClassTypes,param);
														
	}
	
	protected Long findIdTransazioneApplicativoServer(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdTransazioneApplicativoServer id, boolean throwNotFound) throws NotFoundException, ServiceException, NotImplementedException, Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();

		if(id==null) {
			throw new ServiceException("Id non definito");
		}
		if(id.getIdTransazione()==null) {
			throw new ServiceException("IdTransazione non definito");
		}
		if(id.getServizioApplicativoErogatore()==null) {
			throw new ServiceException("IdServizioApplicativoErogatore non definito");
		}
		
		// Object _transazioneApplicativoServer
		sqlQueryObjectGet.addFromTable(this.getTransazioneApplicativoServerFieldConverter().toTable(TransazioneApplicativoServer.model()));
		sqlQueryObjectGet.addSelectField("id");
		// Devono essere mappati nella where condition i metodi dell'oggetto id.getXXX
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.setSelectDistinct(true);
		sqlQueryObjectGet.addWhereCondition(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().ID_TRANSAZIONE,true)+"=?");
		sqlQueryObjectGet.addWhereCondition(this.getTransazioneApplicativoServerFieldConverter().toColumn(TransazioneApplicativoServer.model().SERVIZIO_APPLICATIVO_EROGATORE,true)+"=?");

		// Recupero _transazioneApplicativoServer
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_transazioneApplicativoServer = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id.getIdTransazione(),String.class),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id.getServizioApplicativoErogatore(),String.class)
		};
		Long id_transazioneApplicativoServer = null;
		try{
			id_transazioneApplicativoServer = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
						Long.class, searchParams_transazioneApplicativoServer);
		}catch(NotFoundException notFound){
			if(throwNotFound){
				throw new NotFoundException(notFound);
			}
		}
		if(id_transazioneApplicativoServer==null || id_transazioneApplicativoServer<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		
		return id_transazioneApplicativoServer;
	}
}
