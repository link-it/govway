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
package org.openspcoop2.core.transazioni.dao.jdbc;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.dao.jdbc.converter.TransazioneFieldConverter;
import org.openspcoop2.core.transazioni.dao.jdbc.fetch.TransazioneFetch;
import org.openspcoop2.core.transazioni.utils.AliasTableRicerchePersonalizzate;
import org.openspcoop2.core.transazioni.utils.TransazioniIndexUtils;
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
 * JDBCTransazioneServiceSearchImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCTransazioneServiceSearchImpl implements IJDBCServiceSearchWithId<Transazione, String, JDBCServiceManager> {

	private TransazioneFieldConverter _transazioneFieldConverter = null;
	public TransazioneFieldConverter getTransazioneFieldConverter() {
		if(this._transazioneFieldConverter==null){
			this._transazioneFieldConverter = new TransazioneFieldConverter(this.jdbcServiceManager.getJdbcProperties().getDatabaseType());
		}		
		return this._transazioneFieldConverter;
	}
	@Override
	public ISQLFieldConverter getFieldConverter() {
		return this.getTransazioneFieldConverter();
	}
	
	private TransazioneFetch transazioneFetch = new TransazioneFetch();
	public TransazioneFetch getTransazioneFetch() {
		return this.transazioneFetch;
	}
	@Override
	public IJDBCFetch getFetch() {
		return getTransazioneFetch();
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
	public String convertToId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Transazione transazione) throws NotImplementedException, ServiceException, Exception{

		return transazione.getIdTransazione();
	}
	
	@Override
	public Transazione get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, String id, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {
		Object id_transazione = this.findIdTransazione(jdbcProperties, log, connection, sqlQueryObject, id, true, true);
		return this._get(jdbcProperties, log, connection, sqlQueryObject, id_transazione,idMappingResolutionBehaviour);
		
		
	}
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, String id) throws MultipleResultException, NotImplementedException, ServiceException,Exception {

		Object id_transazione = this.findIdTransazione(jdbcProperties, log, connection, sqlQueryObject, id, false, true);
		return id_transazione != null;	
		
	}
	
	@Override
	public List<String> findAllIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

		List<String> list = new ArrayList<String>();

		List<Object> ids = this._findAllObjectIds(jdbcProperties, log, connection, sqlQueryObject, expression);

		for(Object id: ids) {
			list.add((String)id);
		}

        return list;
		
	}
	
	@Override
	public List<Transazione> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

		List<Transazione> list = new ArrayList<Transazione>();
        
        boolean efficente = true;
        
        boolean soloColonneIndicizzateFullIndexSearch = TransazioniIndexUtils.isEnabledSoloColonneIndicizzateFullIndexSearch(expression); 
        boolean soloColonneIndicizzateFullIndexStats = TransazioniIndexUtils.isEnabledSoloColonneIndicizzateFullIndexStats(expression); 

		if(efficente || soloColonneIndicizzateFullIndexSearch || soloColonneIndicizzateFullIndexStats) {
        
    		List<IField> fields = new ArrayList<IField>();
    		if(soloColonneIndicizzateFullIndexSearch){
    			fields = TransazioniIndexUtils.LISTA_COLONNE_INDEX_TR_SEARCH;
    		}
    		else if(soloColonneIndicizzateFullIndexStats){
    			fields = TransazioniIndexUtils.LISTA_COLONNE_INDEX_TR_STATS;
    		}
    		else {
	    		fields.add(Transazione.model().ID_TRANSAZIONE);
	    		fields.add(Transazione.model().STATO);
	    		fields.add(Transazione.model().RUOLO_TRANSAZIONE);
	    		fields.add(Transazione.model().ESITO);
	    		fields.add(Transazione.model().ESITO_SINCRONO);
	    		fields.add(Transazione.model().CONSEGNE_MULTIPLE_IN_CORSO);
	    		fields.add(Transazione.model().ESITO_CONTESTO);
	    		fields.add(Transazione.model().PROTOCOLLO);
	    		fields.add(Transazione.model().TIPO_RICHIESTA);
	    		fields.add(Transazione.model().CODICE_RISPOSTA_INGRESSO);
	    		fields.add(Transazione.model().CODICE_RISPOSTA_USCITA);
	    		fields.add(Transazione.model().DATA_ACCETTAZIONE_RICHIESTA);
	    		fields.add(Transazione.model().DATA_INGRESSO_RICHIESTA);
	    		fields.add(Transazione.model().DATA_USCITA_RICHIESTA);
	    		fields.add(Transazione.model().DATA_ACCETTAZIONE_RISPOSTA);
	    		fields.add(Transazione.model().DATA_INGRESSO_RISPOSTA);
	    		fields.add(Transazione.model().DATA_USCITA_RISPOSTA);
	    		fields.add(Transazione.model().RICHIESTA_INGRESSO_BYTES);
	    		fields.add(Transazione.model().RICHIESTA_USCITA_BYTES);
	    		fields.add(Transazione.model().RISPOSTA_INGRESSO_BYTES);
	    		fields.add(Transazione.model().RISPOSTA_USCITA_BYTES);
	    		fields.add(Transazione.model().PDD_CODICE);
	    		fields.add(Transazione.model().PDD_TIPO_SOGGETTO);
	    		fields.add(Transazione.model().PDD_NOME_SOGGETTO);
	    		fields.add(Transazione.model().PDD_RUOLO);
	    		fields.add(Transazione.model().FAULT_INTEGRAZIONE);
	    		fields.add(Transazione.model().FORMATO_FAULT_INTEGRAZIONE);
	    		fields.add(Transazione.model().FAULT_COOPERAZIONE);
	    		fields.add(Transazione.model().FORMATO_FAULT_COOPERAZIONE);
	    		fields.add(Transazione.model().TIPO_SOGGETTO_FRUITORE);
	    		fields.add(Transazione.model().NOME_SOGGETTO_FRUITORE);
	    		fields.add(Transazione.model().IDPORTA_SOGGETTO_FRUITORE);
	    		fields.add(Transazione.model().INDIRIZZO_SOGGETTO_FRUITORE);
	    		fields.add(Transazione.model().TIPO_SOGGETTO_EROGATORE);
	    		fields.add(Transazione.model().NOME_SOGGETTO_EROGATORE);
	    		fields.add(Transazione.model().IDPORTA_SOGGETTO_EROGATORE);
	    		fields.add(Transazione.model().INDIRIZZO_SOGGETTO_EROGATORE);
	    		fields.add(Transazione.model().ID_MESSAGGIO_RICHIESTA);
	    		fields.add(Transazione.model().ID_MESSAGGIO_RISPOSTA);
	    		fields.add(Transazione.model().DATA_ID_MSG_RICHIESTA);
	    		fields.add(Transazione.model().DATA_ID_MSG_RISPOSTA);
	    		fields.add(Transazione.model().PROFILO_COLLABORAZIONE_OP_2);
	    		fields.add(Transazione.model().PROFILO_COLLABORAZIONE_PROT);
	    		fields.add(Transazione.model().ID_COLLABORAZIONE);
	    		fields.add(Transazione.model().URI_ACCORDO_SERVIZIO);
	    		fields.add(Transazione.model().TIPO_SERVIZIO);
	    		fields.add(Transazione.model().NOME_SERVIZIO);
	    		fields.add(Transazione.model().VERSIONE_SERVIZIO);
	    		fields.add(Transazione.model().AZIONE);
	    		fields.add(Transazione.model().ID_ASINCRONO);
	    		fields.add(Transazione.model().TIPO_SERVIZIO_CORRELATO);
	    		fields.add(Transazione.model().NOME_SERVIZIO_CORRELATO);
	    		fields.add(Transazione.model().HEADER_PROTOCOLLO_RICHIESTA);
	    		fields.add(Transazione.model().DIGEST_RICHIESTA);
	    		fields.add(Transazione.model().PROTOCOLLO_EXT_INFO_RICHIESTA);
	    		fields.add(Transazione.model().HEADER_PROTOCOLLO_RISPOSTA);
	    		fields.add(Transazione.model().DIGEST_RISPOSTA);
	    		fields.add(Transazione.model().PROTOCOLLO_EXT_INFO_RISPOSTA);
	    		fields.add(Transazione.model().TRACCIA_RICHIESTA);
	    		fields.add(Transazione.model().TRACCIA_RISPOSTA);
	    		fields.add(Transazione.model().DIAGNOSTICI);
	    		fields.add(Transazione.model().DIAGNOSTICI_LIST_1);
	    		fields.add(Transazione.model().DIAGNOSTICI_LIST_2);
	    		fields.add(Transazione.model().DIAGNOSTICI_LIST_EXT);
	    		fields.add(Transazione.model().DIAGNOSTICI_EXT);
	    		fields.add(Transazione.model().ID_CORRELAZIONE_APPLICATIVA);
	    		fields.add(Transazione.model().ID_CORRELAZIONE_APPLICATIVA_RISPOSTA);
	    		fields.add(Transazione.model().SERVIZIO_APPLICATIVO_FRUITORE);
	    		fields.add(Transazione.model().SERVIZIO_APPLICATIVO_EROGATORE);
	    		fields.add(Transazione.model().OPERAZIONE_IM);
	    		fields.add(Transazione.model().LOCATION_RICHIESTA);
	    		fields.add(Transazione.model().LOCATION_RISPOSTA);
	    		fields.add(Transazione.model().NOME_PORTA);
	    		fields.add(Transazione.model().CREDENZIALI);
	    		fields.add(Transazione.model().LOCATION_CONNETTORE);
	    		fields.add(Transazione.model().URL_INVOCAZIONE);
	    		fields.add(Transazione.model().TRASPORTO_MITTENTE);
	    		fields.add(Transazione.model().TOKEN_ISSUER);
	    		fields.add(Transazione.model().TOKEN_CLIENT_ID);
	    		fields.add(Transazione.model().TOKEN_SUBJECT);
	    		fields.add(Transazione.model().TOKEN_USERNAME);
	    		fields.add(Transazione.model().TOKEN_MAIL);
	    		fields.add(Transazione.model().TOKEN_INFO);
	    		fields.add(Transazione.model().DUPLICATI_RICHIESTA);
	    		fields.add(Transazione.model().DUPLICATI_RISPOSTA);
	    		fields.add(Transazione.model().CLUSTER_ID);
	    		fields.add(Transazione.model().SOCKET_CLIENT_ADDRESS);
	    		fields.add(Transazione.model().TRANSPORT_CLIENT_ADDRESS);
	    		fields.add(Transazione.model().CLIENT_ADDRESS);
	    		fields.add(Transazione.model().EVENTI_GESTIONE);
	    		fields.add(Transazione.model().TIPO_API);
	    		fields.add(Transazione.model().URI_API);
	    		fields.add(Transazione.model().GRUPPI);
    		}
    		
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
    	        	
    				List<Object> ids = this._findAllObjectIds(jdbcProperties, log, connection, sqlQueryObject, expression);
    				
    				for(Object id: ids) {
    					list.add(this._get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour));
    				}
    	        	
    	        }
    	        else {
    	        
    	        	//System.out.println("EFFICENTE");
    	        	
		    		returnMap = this.select(jdbcProperties, log, connection, sqlQueryObject, expression, distinct, fields.toArray(new IField[1]));
		
		    		for(Map<String, Object> map: returnMap) {
		    			list.add((Transazione)this.getTransazioneFetch().fetch(jdbcProperties.getDatabase(), Transazione.model(), map));
		    		}
		    		
    	        }
		    		
    		}catch(NotFoundException notFound){}
        	
        }
        else{
        
			List<Object> ids = this._findAllObjectIds(jdbcProperties, log, connection, sqlQueryObject, expression);
	
			for(Object id: ids) {
				list.add(this._get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour));
			}
        }
		
        return list;      
		
	}
	
	@Override
	public Transazione find(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) 
		throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {

        Object id = this._findObjectId(jdbcProperties, log, connection, sqlQueryObject, expression);
        if(id!=null){
        	return this._get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour);
        }else{
        	throw new NotFoundException("Entry with id["+id+"] not found");
        }
		
	}
	
	@Override
	public NonNegativeNumber count(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws NotImplementedException, ServiceException,Exception {
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareCount(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getTransazioneFieldConverter(), Transazione.model());
		
        // Il distinct serve solo se ci sono le ricerche con contenuto.
        // NOTA: il distinct rende le ricerce inefficenti (ed inoltre non e' utilizzabile con campi clob in oracle)
        boolean distinct = false;
        ISQLQueryObject sqlQueryObjectCheckJoin = sqlQueryObject.newSQLQueryObject();
        _join(expression, sqlQueryObjectCheckJoin);
        distinct = ((SQLQueryObjectCore)sqlQueryObjectCheckJoin).sizeConditions()>0;
		
        IField countField = Transazione.model().ID_TRANSAZIONE;
        if(!distinct && expression.inUseField(Transazione.model().DATA_INGRESSO_RICHIESTA, true)){
        	countField = Transazione.model().DATA_INGRESSO_RICHIESTA; // uso la prima colonna dell'indice (se c'è la data e non è distinct)
        }

		sqlQueryObject.addSelectCountField(this.getTransazioneFieldConverter().toColumn(countField,true),"tot",distinct);
		
		_join(expression,sqlQueryObject);
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.count(jdbcProperties, log, connection, sqlQueryObject, expression,
																			this.getTransazioneFieldConverter(), Transazione.model(),listaQuery);
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, String id) throws NotFoundException, NotImplementedException, ServiceException,Exception {
		
		Object id_transazione = this.findIdTransazione(jdbcProperties, log, connection, sqlQueryObject, id, true, true);
        return this._inUse(jdbcProperties, log, connection, sqlQueryObject, id_transazione);
		
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
												this.getTransazioneFieldConverter(), field);

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
        						expression, this.getTransazioneFieldConverter(), Transazione.model(), 
        						listaQuery,listaParams);
		
		_join(expression,sqlQueryObject);
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.select(jdbcProperties, log, connection,
        								org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareSqlQueryObjectForSelectDistinct(sqlQueryObject,sqlQueryObjectDistinct), 
        								expression, this.getTransazioneFieldConverter(), Transazione.model(),
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
        						this.getTransazioneFieldConverter(), Transazione.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.union(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getTransazioneFieldConverter(), Transazione.model(), 
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
        						this.getTransazioneFieldConverter(), Transazione.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        NonNegativeNumber number = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.unionCount(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getTransazioneFieldConverter(), Transazione.model(), 
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
			return new JDBCExpression(this.getTransazioneFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}


	@Override
	public JDBCPaginatedExpression newPaginatedExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(this.getTransazioneFieldConverter());
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
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, String id, Transazione obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,id,null));
	}
	
	@Override
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, Transazione obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,tableId,null));
	}
	private void _mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Transazione obj, Transazione imgSaved) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		if(imgSaved==null){
			return;
		}
		obj.setId(imgSaved.getId());
		if(obj.getDumpMessaggioList()!=null){
			List<org.openspcoop2.core.transazioni.DumpMessaggio> listObj_ = obj.getDumpMessaggioList();
			for(org.openspcoop2.core.transazioni.DumpMessaggio itemObj_ : listObj_){
				org.openspcoop2.core.transazioni.DumpMessaggio itemAlreadySaved_ = null;
				if(imgSaved.getDumpMessaggioList()!=null){
					List<org.openspcoop2.core.transazioni.DumpMessaggio> listImgSaved_ = imgSaved.getDumpMessaggioList();
					for(org.openspcoop2.core.transazioni.DumpMessaggio itemImgSaved_ : listImgSaved_){
						boolean objEqualsToImgSaved_ = false;
						// TODO verify equals
						// objEqualsToImgSaved_ = org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getXXX(),itemImgSaved_.getXXX()) &&
						// 						 			...
						//						 			org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getYYY(),itemImgSaved_.getYYY());
						if(objEqualsToImgSaved_){
							itemAlreadySaved_=itemImgSaved_;
							break;
						}
					}
				}
				if(itemAlreadySaved_!=null){
					itemObj_.setId(itemAlreadySaved_.getId());
					if(itemObj_.getMultipartHeaderList()!=null){
						List<org.openspcoop2.core.transazioni.DumpMultipartHeader> listObj_dumpMessaggio = itemObj_.getMultipartHeaderList();
						for(org.openspcoop2.core.transazioni.DumpMultipartHeader itemObj_dumpMessaggio : listObj_dumpMessaggio){
							org.openspcoop2.core.transazioni.DumpMultipartHeader itemAlreadySaved_dumpMessaggio = null;
							if(itemAlreadySaved_.getMultipartHeaderList()!=null){
								List<org.openspcoop2.core.transazioni.DumpMultipartHeader> listImgSaved_dumpMessaggio = itemAlreadySaved_.getMultipartHeaderList();
								for(org.openspcoop2.core.transazioni.DumpMultipartHeader itemImgSaved_dumpMessaggio : listImgSaved_dumpMessaggio){
									boolean objEqualsToImgSaved_dumpMessaggio = false;
									// TODO verify equals
									// objEqualsToImgSaved_dumpMessaggio = org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_dumpMessaggio.getXXX(),itemImgSaved_dumpMessaggio.getXXX()) &&
									// 						 			...
									//						 			org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_dumpMessaggio.getYYY(),itemImgSaved_dumpMessaggio.getYYY());
									if(objEqualsToImgSaved_dumpMessaggio){
										itemAlreadySaved_dumpMessaggio=itemImgSaved_dumpMessaggio;
										break;
									}
								}
							}
							if(itemAlreadySaved_dumpMessaggio!=null){
								itemObj_dumpMessaggio.setId(itemAlreadySaved_dumpMessaggio.getId());
							}
						}
					}
					if(itemObj_.getHeaderTrasportoList()!=null){
						List<org.openspcoop2.core.transazioni.DumpHeaderTrasporto> listObj_dumpMessaggio = itemObj_.getHeaderTrasportoList();
						for(org.openspcoop2.core.transazioni.DumpHeaderTrasporto itemObj_dumpMessaggio : listObj_dumpMessaggio){
							org.openspcoop2.core.transazioni.DumpHeaderTrasporto itemAlreadySaved_dumpMessaggio = null;
							if(itemAlreadySaved_.getHeaderTrasportoList()!=null){
								List<org.openspcoop2.core.transazioni.DumpHeaderTrasporto> listImgSaved_dumpMessaggio = itemAlreadySaved_.getHeaderTrasportoList();
								for(org.openspcoop2.core.transazioni.DumpHeaderTrasporto itemImgSaved_dumpMessaggio : listImgSaved_dumpMessaggio){
									boolean objEqualsToImgSaved_dumpMessaggio = false;
									// TODO verify equals
									// objEqualsToImgSaved_dumpMessaggio = org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_dumpMessaggio.getXXX(),itemImgSaved_dumpMessaggio.getXXX()) &&
									// 						 			...
									//						 			org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_dumpMessaggio.getYYY(),itemImgSaved_dumpMessaggio.getYYY());
									if(objEqualsToImgSaved_dumpMessaggio){
										itemAlreadySaved_dumpMessaggio=itemImgSaved_dumpMessaggio;
										break;
									}
								}
							}
							if(itemAlreadySaved_dumpMessaggio!=null){
								itemObj_dumpMessaggio.setId(itemAlreadySaved_dumpMessaggio.getId());
							}
						}
					}
					if(itemObj_.getAllegatoList()!=null){
						List<org.openspcoop2.core.transazioni.DumpAllegato> listObj_dumpMessaggio = itemObj_.getAllegatoList();
						for(org.openspcoop2.core.transazioni.DumpAllegato itemObj_dumpMessaggio : listObj_dumpMessaggio){
							org.openspcoop2.core.transazioni.DumpAllegato itemAlreadySaved_dumpMessaggio = null;
							if(itemAlreadySaved_.getAllegatoList()!=null){
								List<org.openspcoop2.core.transazioni.DumpAllegato> listImgSaved_dumpMessaggio = itemAlreadySaved_.getAllegatoList();
								for(org.openspcoop2.core.transazioni.DumpAllegato itemImgSaved_dumpMessaggio : listImgSaved_dumpMessaggio){
									boolean objEqualsToImgSaved_dumpMessaggio = false;
									// TODO verify equals
									// objEqualsToImgSaved_dumpMessaggio = org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_dumpMessaggio.getXXX(),itemImgSaved_dumpMessaggio.getXXX()) &&
									// 						 			...
									//						 			org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_dumpMessaggio.getYYY(),itemImgSaved_dumpMessaggio.getYYY());
									if(objEqualsToImgSaved_dumpMessaggio){
										itemAlreadySaved_dumpMessaggio=itemImgSaved_dumpMessaggio;
										break;
									}
								}
							}
							if(itemAlreadySaved_dumpMessaggio!=null){
								itemObj_dumpMessaggio.setId(itemAlreadySaved_dumpMessaggio.getId());
								if(itemObj_dumpMessaggio.getHeaderList()!=null){
									List<org.openspcoop2.core.transazioni.DumpHeaderAllegato> listObj_dumpMessaggio_allegato = itemObj_dumpMessaggio.getHeaderList();
									for(org.openspcoop2.core.transazioni.DumpHeaderAllegato itemObj_dumpMessaggio_allegato : listObj_dumpMessaggio_allegato){
										org.openspcoop2.core.transazioni.DumpHeaderAllegato itemAlreadySaved_dumpMessaggio_allegato = null;
										if(itemAlreadySaved_dumpMessaggio.getHeaderList()!=null){
											List<org.openspcoop2.core.transazioni.DumpHeaderAllegato> listImgSaved_dumpMessaggio_allegato = itemAlreadySaved_dumpMessaggio.getHeaderList();
											for(org.openspcoop2.core.transazioni.DumpHeaderAllegato itemImgSaved_dumpMessaggio_allegato : listImgSaved_dumpMessaggio_allegato){
												boolean objEqualsToImgSaved_dumpMessaggio_allegato = false;
												// TODO verify equals
												// objEqualsToImgSaved_dumpMessaggio_allegato = org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_dumpMessaggio_allegato.getXXX(),itemImgSaved_dumpMessaggio_allegato.getXXX()) &&
												// 						 			...
												//						 			org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_dumpMessaggio_allegato.getYYY(),itemImgSaved_dumpMessaggio_allegato.getYYY());
												if(objEqualsToImgSaved_dumpMessaggio_allegato){
													itemAlreadySaved_dumpMessaggio_allegato=itemImgSaved_dumpMessaggio_allegato;
													break;
												}
											}
										}
										if(itemAlreadySaved_dumpMessaggio_allegato!=null){
											itemObj_dumpMessaggio_allegato.setId(itemAlreadySaved_dumpMessaggio_allegato.getId());
										}
									}
								}
							}
						}
					}
					if(itemObj_.getContenutoList()!=null){
						List<org.openspcoop2.core.transazioni.DumpContenuto> listObj_dumpMessaggio = itemObj_.getContenutoList();
						for(org.openspcoop2.core.transazioni.DumpContenuto itemObj_dumpMessaggio : listObj_dumpMessaggio){
							org.openspcoop2.core.transazioni.DumpContenuto itemAlreadySaved_dumpMessaggio = null;
							if(itemAlreadySaved_.getContenutoList()!=null){
								List<org.openspcoop2.core.transazioni.DumpContenuto> listImgSaved_dumpMessaggio = itemAlreadySaved_.getContenutoList();
								for(org.openspcoop2.core.transazioni.DumpContenuto itemImgSaved_dumpMessaggio : listImgSaved_dumpMessaggio){
									boolean objEqualsToImgSaved_dumpMessaggio = false;
									// TODO verify equals
									// objEqualsToImgSaved_dumpMessaggio = org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_dumpMessaggio.getXXX(),itemImgSaved_dumpMessaggio.getXXX()) &&
									// 						 			...
									//						 			org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_dumpMessaggio.getYYY(),itemImgSaved_dumpMessaggio.getYYY());
									if(objEqualsToImgSaved_dumpMessaggio){
										itemAlreadySaved_dumpMessaggio=itemImgSaved_dumpMessaggio;
										break;
									}
								}
							}
							if(itemAlreadySaved_dumpMessaggio!=null){
								itemObj_dumpMessaggio.setId(itemAlreadySaved_dumpMessaggio.getId());
							}
						}
					}
				}
			}
		}
		if(obj.getTransazioneApplicativoServerList()!=null){
			List<org.openspcoop2.core.transazioni.TransazioneApplicativoServer> listObj_ = obj.getTransazioneApplicativoServerList();
			for(org.openspcoop2.core.transazioni.TransazioneApplicativoServer itemObj_ : listObj_){
				org.openspcoop2.core.transazioni.TransazioneApplicativoServer itemAlreadySaved_ = null;
				if(imgSaved.getTransazioneApplicativoServerList()!=null){
					List<org.openspcoop2.core.transazioni.TransazioneApplicativoServer> listImgSaved_ = imgSaved.getTransazioneApplicativoServerList();
					for(org.openspcoop2.core.transazioni.TransazioneApplicativoServer itemImgSaved_ : listImgSaved_){
						boolean objEqualsToImgSaved_ = false;
						// TODO verify equals
						// objEqualsToImgSaved_ = org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getXXX(),itemImgSaved_.getXXX()) &&
						// 						 			...
						//						 			org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getYYY(),itemImgSaved_.getYYY());
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
		if(obj.getTransazioneExtendedInfoList()!=null){
			List<org.openspcoop2.core.transazioni.TransazioneExtendedInfo> listObj_ = obj.getTransazioneExtendedInfoList();
			for(org.openspcoop2.core.transazioni.TransazioneExtendedInfo itemObj_ : listObj_){
				org.openspcoop2.core.transazioni.TransazioneExtendedInfo itemAlreadySaved_ = null;
				if(imgSaved.getTransazioneExtendedInfoList()!=null){
					List<org.openspcoop2.core.transazioni.TransazioneExtendedInfo> listImgSaved_ = imgSaved.getTransazioneExtendedInfoList();
					for(org.openspcoop2.core.transazioni.TransazioneExtendedInfo itemImgSaved_ : listImgSaved_){
						boolean objEqualsToImgSaved_ = false;
						// TODO verify equals
						// objEqualsToImgSaved_ = org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getXXX(),itemImgSaved_.getXXX()) &&
						// 						 			...
						//						 			org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getYYY(),itemImgSaved_.getYYY());
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

		/* 
         * TODO: implement code for id mapping
        */

        // Delete this line when you have implemented the method
        int throwNotImplemented = 1;
        if(throwNotImplemented==1){
                throw new NotImplementedException("NotImplemented");
        }
        // Delete this line when you have implemented the method                
	}
	
	@Override
	public Transazione get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
		throw new NotImplementedException("Table without long id column PK");
	}
	
	protected Transazione _get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Object objectId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
				
		Transazione transazione = new Transazione();
		

		// Object transazione
		ISQLQueryObject sqlQueryObjectGet_transazione = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_transazione.setANDLogicOperator(true);
		sqlQueryObjectGet_transazione.addFromTable(this.getTransazioneFieldConverter().toTable(Transazione.model()));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ID_TRANSAZIONE,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().STATO,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().RUOLO_TRANSAZIONE,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ESITO,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ESITO_SINCRONO,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().CONSEGNE_MULTIPLE_IN_CORSO,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ESITO_CONTESTO,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().PROTOCOLLO,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TIPO_RICHIESTA,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().CODICE_RISPOSTA_INGRESSO,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().CODICE_RISPOSTA_USCITA,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DATA_ACCETTAZIONE_RICHIESTA,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DATA_INGRESSO_RICHIESTA,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DATA_USCITA_RICHIESTA,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DATA_ACCETTAZIONE_RISPOSTA,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DATA_INGRESSO_RISPOSTA,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DATA_USCITA_RISPOSTA,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().RICHIESTA_INGRESSO_BYTES,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().RICHIESTA_USCITA_BYTES,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().RISPOSTA_INGRESSO_BYTES,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().RISPOSTA_USCITA_BYTES,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().PDD_CODICE,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().PDD_TIPO_SOGGETTO,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().PDD_NOME_SOGGETTO,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().PDD_RUOLO,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().FAULT_INTEGRAZIONE,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().FORMATO_FAULT_INTEGRAZIONE,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().FAULT_COOPERAZIONE,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().FORMATO_FAULT_COOPERAZIONE,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TIPO_SOGGETTO_FRUITORE,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().NOME_SOGGETTO_FRUITORE,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().IDPORTA_SOGGETTO_FRUITORE,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().INDIRIZZO_SOGGETTO_FRUITORE,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TIPO_SOGGETTO_EROGATORE,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().NOME_SOGGETTO_EROGATORE,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().IDPORTA_SOGGETTO_EROGATORE,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().INDIRIZZO_SOGGETTO_EROGATORE,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ID_MESSAGGIO_RICHIESTA,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ID_MESSAGGIO_RISPOSTA,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DATA_ID_MSG_RICHIESTA,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DATA_ID_MSG_RISPOSTA,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().PROFILO_COLLABORAZIONE_OP_2,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().PROFILO_COLLABORAZIONE_PROT,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ID_COLLABORAZIONE,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().URI_ACCORDO_SERVIZIO,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TIPO_SERVIZIO,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().NOME_SERVIZIO,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().VERSIONE_SERVIZIO,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().AZIONE,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ID_ASINCRONO,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TIPO_SERVIZIO_CORRELATO,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().NOME_SERVIZIO_CORRELATO,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().HEADER_PROTOCOLLO_RICHIESTA,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DIGEST_RICHIESTA,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().PROTOCOLLO_EXT_INFO_RICHIESTA,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().HEADER_PROTOCOLLO_RISPOSTA,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DIGEST_RISPOSTA,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().PROTOCOLLO_EXT_INFO_RISPOSTA,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TRACCIA_RICHIESTA,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TRACCIA_RISPOSTA,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DIAGNOSTICI,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DIAGNOSTICI_LIST_1,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DIAGNOSTICI_LIST_2,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DIAGNOSTICI_LIST_EXT,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DIAGNOSTICI_EXT,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ID_CORRELAZIONE_APPLICATIVA,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ID_CORRELAZIONE_APPLICATIVA_RISPOSTA,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().SERVIZIO_APPLICATIVO_FRUITORE,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().SERVIZIO_APPLICATIVO_EROGATORE,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().OPERAZIONE_IM,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().LOCATION_RICHIESTA,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().LOCATION_RISPOSTA,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().NOME_PORTA,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().CREDENZIALI,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().LOCATION_CONNETTORE,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().URL_INVOCAZIONE,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TRASPORTO_MITTENTE,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TOKEN_ISSUER,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TOKEN_CLIENT_ID,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TOKEN_SUBJECT,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TOKEN_USERNAME,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TOKEN_MAIL,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TOKEN_INFO,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TEMPI_ELABORAZIONE,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DUPLICATI_RICHIESTA,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().DUPLICATI_RISPOSTA,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().CLUSTER_ID,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().SOCKET_CLIENT_ADDRESS,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TRANSPORT_CLIENT_ADDRESS,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().CLIENT_ADDRESS,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().EVENTI_GESTIONE,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().TIPO_API,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().URI_API,true));
		sqlQueryObjectGet_transazione.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().GRUPPI,true));
		
		sqlQueryObjectGet_transazione.addWhereCondition(this.getTransazioneFieldConverter().toColumn(Transazione.model().ID_TRANSAZIONE,true)+"=?");

		// Get transazione
		transazione = (Transazione) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_transazione.createSQLQuery(), jdbcProperties.isShowSql(), Transazione.model(), this.getTransazioneFetch(),
				new JDBCObject(objectId,String.class));

        return transazione;  
	
	} 
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
		throw new NotImplementedException("Table without long id column PK");
	}
	
	protected boolean _exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Object objectId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		boolean existsTransazione = false;

		sqlQueryObject = sqlQueryObject.newSQLQueryObject();
		sqlQueryObject.setANDLogicOperator(true);

		sqlQueryObject.addFromTable(this.getTransazioneFieldConverter().toTable(Transazione.model()));
		sqlQueryObject.addSelectField(this.getTransazioneFieldConverter().toColumn(Transazione.model().ID_TRANSAZIONE,true));
		sqlQueryObject.addWhereCondition(this.getTransazioneFieldConverter().toColumn(Transazione.model().ID_TRANSAZIONE,true)+"=?");

		// Exists transazione
		existsTransazione = jdbcUtilities.exists(sqlQueryObject.createSQLQuery(), jdbcProperties.isShowSql(),
				new JDBCObject(objectId,String.class));

        return existsTransazione;
	
	}
	
	private void _join(IExpression expression, ISQLQueryObject sqlQueryObject) throws NotImplementedException, ServiceException, Exception{

		if(expression.inUseModel(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER,false)){
			String tableName1 = this.getTransazioneFieldConverter().toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER);
			String tableName2 = this.getTransazioneFieldConverter().toTable(Transazione.model());
			sqlQueryObject.addWhereCondition(tableName1+".id_transazione="+tableName2+".id");
		}
		
		
		
		boolean joinTransazioniRequired = false;
		if(expression.inUseModel(Transazione.model().DUMP_MESSAGGIO.ALLEGATO,false)){
			String tableName1 = this.getTransazioneFieldConverter().toTable(Transazione.model().DUMP_MESSAGGIO.ALLEGATO);
			String tableName2 = this.getTransazioneFieldConverter().toTable(Transazione.model().DUMP_MESSAGGIO);
			sqlQueryObject.addWhereCondition(tableName1+".id_messaggio="+tableName2+".id");
			joinTransazioniRequired = true;
		}
		if(expression.inUseModel(Transazione.model().DUMP_MESSAGGIO.ALLEGATO.HEADER,false)){
			String tableName1 = this.getTransazioneFieldConverter().toTable(Transazione.model().DUMP_MESSAGGIO.ALLEGATO.HEADER);
			String tableName2 = this.getTransazioneFieldConverter().toTable(Transazione.model().DUMP_MESSAGGIO.ALLEGATO);
			sqlQueryObject.addWhereCondition(tableName1+".id_allegato="+tableName2+".id");
			joinTransazioniRequired = true;
		}
		if(expression.inUseModel(Transazione.model().DUMP_MESSAGGIO.HEADER_TRASPORTO,false)){
			String tableName1 = this.getTransazioneFieldConverter().toTable(Transazione.model().DUMP_MESSAGGIO.HEADER_TRASPORTO);
			String tableName2 = this.getTransazioneFieldConverter().toTable(Transazione.model().DUMP_MESSAGGIO);
			sqlQueryObject.addWhereCondition(tableName1+".id_messaggio="+tableName2+".id");
			joinTransazioniRequired = true;
		}
		if(expression.inUseModel(Transazione.model().DUMP_MESSAGGIO.MULTIPART_HEADER,false)){
			String tableName1 = this.getTransazioneFieldConverter().toTable(Transazione.model().DUMP_MESSAGGIO.MULTIPART_HEADER);
			String tableName2 = this.getTransazioneFieldConverter().toTable(Transazione.model().DUMP_MESSAGGIO);
			sqlQueryObject.addWhereCondition(tableName1+".id_messaggio="+tableName2+".id");
			joinTransazioniRequired = true;
		}
		
		List<String> tableWithAlias = AliasTableRicerchePersonalizzate.addFromTable(expression, sqlQueryObject, this.getFieldConverter());
		boolean useDumpContenuto = false;
		if(tableWithAlias!=null && tableWithAlias.size()>0){
			useDumpContenuto = true;
			String tableName2 = this.getTransazioneFieldConverter().toTable(Transazione.model().DUMP_MESSAGGIO);
			for (String aliasTable : tableWithAlias) {
				sqlQueryObject.addWhereCondition(aliasTable+".id_messaggio="+tableName2+".id");	
			}
		}
		
		// per le ricerche puntuali sul tipo/nome
		if(expression.inUseField(Transazione.model().DUMP_MESSAGGIO.CONTENUTO.NOME,false)){
			useDumpContenuto = true;
			String tableName1 = this.getTransazioneFieldConverter().toTable(Transazione.model().DUMP_MESSAGGIO.CONTENUTO);
			String tableName2 = this.getTransazioneFieldConverter().toTable(Transazione.model().DUMP_MESSAGGIO);
			sqlQueryObject.addWhereCondition(tableName1+".id_messaggio="+tableName2+".id");
		}
	


		if(expression.inUseModel(Transazione.model().DUMP_MESSAGGIO.ALLEGATO,false)){
			if(expression.inUseModel(Transazione.model().DUMP_MESSAGGIO,false)==false){
				sqlQueryObject.addFromTable(this.getTransazioneFieldConverter().toTable(Transazione.model().DUMP_MESSAGGIO));
			}
		}
		if(expression.inUseModel(Transazione.model().DUMP_MESSAGGIO.ALLEGATO.HEADER,false)){
			if(expression.inUseModel(Transazione.model().DUMP_MESSAGGIO.ALLEGATO,false)==false){
				sqlQueryObject.addFromTable(this.getTransazioneFieldConverter().toTable(Transazione.model().DUMP_MESSAGGIO.ALLEGATO));
			}
		}
		if(expression.inUseModel(Transazione.model().DUMP_MESSAGGIO.HEADER_TRASPORTO,false)){
			if(expression.inUseModel(Transazione.model().DUMP_MESSAGGIO,false)==false){
				sqlQueryObject.addFromTable(this.getTransazioneFieldConverter().toTable(Transazione.model().DUMP_MESSAGGIO));
			}
		}
		if(expression.inUseModel(Transazione.model().DUMP_MESSAGGIO.MULTIPART_HEADER,false)){
			if(expression.inUseModel(Transazione.model().DUMP_MESSAGGIO,false)==false){
				sqlQueryObject.addFromTable(this.getTransazioneFieldConverter().toTable(Transazione.model().DUMP_MESSAGGIO));
			}
		}
		if(useDumpContenuto){
			if(expression.inUseModel(Transazione.model().DUMP_MESSAGGIO,false)==false){
				sqlQueryObject.addFromTable(this.getTransazioneFieldConverter().toTable(Transazione.model().DUMP_MESSAGGIO));
			}
			joinTransazioniRequired = true;
		}
		
		if(joinTransazioniRequired || expression.inUseModel(Transazione.model().DUMP_MESSAGGIO,false)){
			String tableName1 = this.getTransazioneFieldConverter().toTable(Transazione.model().DUMP_MESSAGGIO);
			String tableName2 = this.getTransazioneFieldConverter().toTable(Transazione.model());
			sqlQueryObject.addWhereCondition(tableName1+".id_transazione="+tableName2+".id");
		}

	}

	protected java.util.List<Object> _getRootTablePrimaryKeyValues(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, String id) throws NotFoundException, ServiceException, NotImplementedException, Exception{
		// Identificativi
		java.util.List<Object> rootTableIdValues = new java.util.ArrayList<Object>();
		Object objectId = this.findIdTransazione(jdbcProperties, log, connection, sqlQueryObject.newSQLQueryObject(), id, true, false);
		rootTableIdValues.add(objectId);
		return rootTableIdValues;
	}
	
	protected Map<String, List<IField>> _getMapTableToPKColumn() throws NotImplementedException, Exception{
	
		TransazioneFieldConverter converter = this.getTransazioneFieldConverter();
		Map<String, List<IField>> mapTableToPKColumn = new java.util.HashMap<String, List<IField>>();
		UtilsTemplate<IField> utilities = new UtilsTemplate<IField>();

		// TODO: Define the columns used to identify the primary key
		//		  If a table doesn't have a primary key, don't add it to this map

		// Transazione.model()
		mapTableToPKColumn.put(converter.toTable(Transazione.model()),
			utilities.newList(
				new CustomField("id", String.class, "id", converter.toTable(Transazione.model()))
			));

		// Transazione.model().DUMP_MESSAGGIO
		mapTableToPKColumn.put(converter.toTable(Transazione.model().DUMP_MESSAGGIO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Transazione.model().DUMP_MESSAGGIO))
			));

		// Transazione.model().DUMP_MESSAGGIO.ALLEGATO
		mapTableToPKColumn.put(converter.toTable(Transazione.model().DUMP_MESSAGGIO.ALLEGATO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Transazione.model().DUMP_MESSAGGIO.ALLEGATO))
			));

		// Transazione.model().DUMP_MESSAGGIO.CONTENUTO
		mapTableToPKColumn.put(converter.toTable(Transazione.model().DUMP_MESSAGGIO.CONTENUTO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Transazione.model().DUMP_MESSAGGIO.CONTENUTO))
			));

		// Transazione.model().DUMP_MESSAGGIO.HEADER_TRASPORTO
		mapTableToPKColumn.put(converter.toTable(Transazione.model().DUMP_MESSAGGIO.HEADER_TRASPORTO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Transazione.model().DUMP_MESSAGGIO.HEADER_TRASPORTO))
			));

        return mapTableToPKColumn;		
	}
	
	@Override
	public List<Long> findAllTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression paginatedExpression) throws ServiceException, NotImplementedException, Exception {
		
		throw new NotImplementedException("Table without long id column PK");
		
	}
	public List<Object> _findAllObjectIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression paginatedExpression) throws ServiceException, NotImplementedException, Exception {

        // Il distinct serve solo se ci sono le ricerche con contenuto.
        // NOTA: il distinct rende le ricerce inefficenti (ed inoltre non e' utilizzabile con campi clob in oracle)
        boolean distinct = false;
        ISQLQueryObject sqlQueryObjectCheckJoin = sqlQueryObject.newSQLQueryObject();
        _join(paginatedExpression, sqlQueryObjectCheckJoin);
        distinct = ((SQLQueryObjectCore)sqlQueryObjectCheckJoin).sizeConditions()>0;

		sqlQueryObject.setSelectDistinct(distinct);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getTransazioneFieldConverter().toTable(Transazione.model())+".id");
		Class<?> objectIdClass = String.class; 		

		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFindAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
												this.getTransazioneFieldConverter(), Transazione.model());
		
		_join(paginatedExpression,sqlQueryObject);
		
		List<Object> listObjects = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.findAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
																			this.getTransazioneFieldConverter(), Transazione.model(), objectIdClass, listaQuery);
        return listObjects;
		
	}
	
	@Override
	public long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, Exception {
	
		throw new NotImplementedException("Table without long id column PK");
		
	}
	
	public Object _findObjectId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, Exception {
		
        // Il distinct serve solo se ci sono le ricerche con contenuto.
        // NOTA: il distinct rende le ricerce inefficenti (ed inoltre non e' utilizzabile con campi clob in oracle)
        boolean distinct = false;
        ISQLQueryObject sqlQueryObjectCheckJoin = sqlQueryObject.newSQLQueryObject();
        _join(expression, sqlQueryObjectCheckJoin);
        distinct = ((SQLQueryObjectCore)sqlQueryObjectCheckJoin).sizeConditions()>0;
		
		sqlQueryObject.setSelectDistinct(distinct);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getTransazioneFieldConverter().toTable(Transazione.model())+".id");
		Class<?> objectIdClass = String.class; 		

		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFind(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getTransazioneFieldConverter(), Transazione.model());
		
		_join(expression,sqlQueryObject);

		Object res = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.find(jdbcProperties, log, connection, sqlQueryObject, expression,
														this.getTransazioneFieldConverter(), Transazione.model(), objectIdClass, listaQuery);
		if(res!=null){
			return res;
		}
		else{
			throw new NotFoundException("Not Found");
		}
		
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws ServiceException, NotFoundException, NotImplementedException, Exception {
		throw new NotImplementedException("Table without long id column PK");
	}

	protected InUse _inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Object objectId) throws ServiceException, NotFoundException, NotImplementedException, Exception {

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
	public String findId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
		
		throw new NotImplementedException("Table without long id column PK");
		
	}

	@Override
	public Long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, String id, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
	
		throw new NotImplementedException("Table without long id column PK");
			
	}
	
	@Override
	public List<List<Object>> nativeQuery(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
											String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException,Exception{
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.nativeQuery(jdbcProperties, log, connection, sqlQueryObject,
																							sql,returnClassTypes,param);
														
	}
	
	protected Object findIdTransazione(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
			String id, boolean throwNotFound, boolean executeQueryForFound) throws NotFoundException, ServiceException, NotImplementedException, Exception {

		if(executeQueryForFound){
		
			// utile per metodo exists
			
			org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

			ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();

			// Object _transazione
			sqlQueryObjectGet.addFromTable(this.getTransazioneFieldConverter().toTable(Transazione.model()));
			sqlQueryObjectGet.addSelectField("id");
			sqlQueryObjectGet.setANDLogicOperator(true);
			sqlQueryObjectGet.setSelectDistinct(false); // non serve il distinct, vado gia' puntuale con id di transazione
			sqlQueryObjectGet.addWhereCondition(this.getTransazioneFieldConverter().toColumn(Transazione.model().ID_TRANSAZIONE,true)+"=?");

			// Recupero _transazione
			org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_transazione = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id,String.class)
			};
			String id_transazione = null;
			try{
				id_transazione = (String) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
						String.class, searchParams_transazione);
			}catch(NotFoundException notFound){
				if(throwNotFound){
					throw new NotFoundException(notFound);
				}
			}
			if(id_transazione==null){
				if(throwNotFound){
					throw new NotFoundException("Not Found");
				}
			}
			
			return id_transazione;
			
		}
		else{
			return id; // L'id e' anche la PK
		}
	}

}
