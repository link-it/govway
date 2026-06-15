package org.openspcoop2.core.transazioni.dao.jdbc;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.transazioni.IdTransazioneLlm;
import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;
import org.openspcoop2.core.transazioni.TransazioneLlm;
import org.openspcoop2.core.transazioni.dao.jdbc.converter.TransazioneLlmFieldConverter;
import org.openspcoop2.core.transazioni.dao.jdbc.fetch.TransazioneLlmFetch;
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
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.generic_project.utils.UtilsTemplate;
import org.openspcoop2.utils.jdbc.JDBCAdapterException;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLQueryObjectCore;
import org.openspcoop2.utils.sql.SQLQueryObjectException;
import org.slf4j.Logger;

/**     
 * JDBCTransazioneLlmServiceSearchImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCTransazioneLlmServiceSearchImpl implements IJDBCServiceSearchWithId<TransazioneLlm, IdTransazioneLlm, JDBCServiceManager> {

	private TransazioneLlmFieldConverter transazioneLlmFieldConverterPrivateInstance = null;
	public TransazioneLlmFieldConverter getTransazioneLlmFieldConverter() {
		if(this.transazioneLlmFieldConverterPrivateInstance==null){
			this.transazioneLlmFieldConverterPrivateInstance = new TransazioneLlmFieldConverter(this.jdbcServiceManager.getJdbcProperties().getDatabaseType());
		}		
		return this.transazioneLlmFieldConverterPrivateInstance;
	}
	@Override
	public ISQLFieldConverter getFieldConverter() {
		return this.getTransazioneLlmFieldConverter();
	}
	
	private TransazioneLlmFetch transazioneLlmFetch = new TransazioneLlmFetch();
	public TransazioneLlmFetch getTransazioneLlmFetch() {
		return this.transazioneLlmFetch;
	}
	@Override
	public IJDBCFetch getFetch() {
		return getTransazioneLlmFetch();
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
	public IdTransazioneLlm convertToId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, TransazioneLlm transazioneLlm) throws NotImplementedException, ServiceException{
	
		IdTransazioneLlm idTransazioneLlm = new IdTransazioneLlm();
		idTransazioneLlm.setIdTransazione(transazioneLlm.getIdTransazione());
		return idTransazioneLlm;
	}
	
	private static boolean efficiente = true;
	public static boolean isEfficiente() {
		return efficiente;
	}
	public static void setEfficiente(boolean efficiente) {
		JDBCTransazioneLlmServiceSearchImpl.efficiente = efficiente;
	}
	@Override
	public TransazioneLlm get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdTransazioneLlm id, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, ExpressionNotImplementedException, ExpressionException, SQLQueryObjectException, JDBCAdapterException {
		
		long idLong = (id!=null && id.getId()!=null && id.getId()>0) ? id.getId() : -1;
		
        if(idLong<=0 && efficiente){
        	if(id==null) {
    			throw new ServiceException("Id non definito");
    		}
    		if(id.getIdTransazione()==null) {
    			throw new ServiceException("IdTransazione non definito");
    		}
    		
    		JDBCPaginatedExpression pagExpr = this.newPaginatedExpression(log);
        	pagExpr.equals(TransazioneLlm.model().ID_TRANSAZIONE, id.getIdTransazione());
			//pagExpr.limit(2); Inefficiente, per implementare il multipleresult che poi non può succedere
			if(sqlQueryObject==null || !(sqlQueryObject instanceof SQLQueryObjectCore) || !((SQLQueryObjectCore)sqlQueryObject).isSelectForUpdate()) {
				pagExpr.limit(1);
				// essendoci lo unique comunque il limit serve a poco, e nello stesso tempo la select for update non lo vuole
			}
			        	
        	List<TransazioneLlm> list = findAll(jdbcProperties, log, connection, sqlQueryObject, pagExpr, idMappingResolutionBehaviour);
        	
        	if(list==null || list.isEmpty()) {
        		throw new NotFoundException();
        	}
        	// C'è lo unique sulle due colonne
        	/**else if(list.size()>1) {
        		throw new MultipleResultException();
        	}*/
        	else {
        		return list.get(0);
        	}
        }
        else {
				Long idTransazioneLlm = ( (id!=null && id.getId()!=null && id.getId()>0) ? id.getId() : this.findIdTransazioneLlm(jdbcProperties, log, connection, sqlQueryObject, id, true));
				return this.getEngine(jdbcProperties, log, connection, sqlQueryObject, idTransazioneLlm,idMappingResolutionBehaviour);
        }
		
	}
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdTransazioneLlm id) throws MultipleResultException, NotImplementedException, ServiceException, NotFoundException, SQLQueryObjectException, ExpressionException, JDBCAdapterException {

		Long idTransazioneLlm = this.findIdTransazioneLlm(jdbcProperties, log, connection, sqlQueryObject, id, false);
		return idTransazioneLlm != null && idTransazioneLlm > 0;
		
	}
	
	@Override
	public List<IdTransazioneLlm> findAllIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException, SQLQueryObjectException, ExpressionException, JDBCAdapterException, ExpressionNotImplementedException, NotFoundException, MultipleResultException {

		List<IdTransazioneLlm> list = new ArrayList<>();

		// !Attenzione! Implementazione non efficiente. 
		// Per ottenere una implementazione efficiente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari a create l'ID logico
		// 2. Usare metodo getTransazioneLlmFetch() sul risultato della select per ottenere un oggetto TransazioneLlm
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 
		// 3. Usare metodo convertToId per ottenere l'id

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	TransazioneLlm transazioneLlm = this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour);
			IdTransazioneLlm idTransazioneLlm = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,transazioneLlm);
        	list.add(idTransazioneLlm);
        }

        return list;
		
	}
	
	private static boolean distinct = false;
	
	public static boolean isDistinct() {
		return distinct;
	}
	public static void setDistinct(boolean distinct) {
		JDBCTransazioneLlmServiceSearchImpl.distinct = distinct;
	}
	@Override
	public List<TransazioneLlm> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException, ExpressionException, SQLQueryObjectException, JDBCAdapterException, ExpressionNotImplementedException, MultipleResultException, NotFoundException {

        List<TransazioneLlm> list = new ArrayList<>();
        
        if(efficiente){
        
        	List<IField> fields = new ArrayList<>();
        	fields.add(new CustomField("id", Long.class, "id", this.getTransazioneLlmFieldConverter().toTable(TransazioneLlm.model())));  
    		fields.add(TransazioneLlm.model().ID_TRANSAZIONE);
    		fields.add(TransazioneLlm.model().DATA_INGRESSO_RICHIESTA);
    		fields.add(TransazioneLlm.model().LLM_PROVIDER);
    		fields.add(TransazioneLlm.model().LLM_MODEL);
    		fields.add(TransazioneLlm.model().LLM_PROVIDER_BINDING);
    		fields.add(TransazioneLlm.model().TOKEN_INPUT);
    		fields.add(TransazioneLlm.model().TOKEN_OUTPUT);
    		fields.add(TransazioneLlm.model().COST_ESTIMATED);
    		
    		List<Map<String, Object>> returnMap = null;
    		try{
    			 // Il distinct serve solo se ci sono le ricerche con contenuto.
    	        // NOTA: il distinct rende le ricerce inefficienti (ed inoltre non e' utilizzabile con campi clob in oracle)
    	        
    	        
    	        // BUG FIX: Siccome tra le colonne lette ci sono dei CLOB, in oracle non e' consentito utilizzare il DISTINCT.
    	        // Per questo motivo se c'e' da usare il distinct viene utilizzato il vecchio metodo
    	        if(distinct) {
    	        	/**System.out.println("NON EFFICIENTE");*/
    	        	
    	        	 List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
    	 	        
    	 	        for(Long id: ids) {
    	 	        	list.add(this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour));
    	 	        }
    	        	
    	        }
    	        else {
    	        
    	        	/**System.out.println("EFFICIENTE");*/
    	        	
		    		returnMap = this.select(jdbcProperties, log, connection, sqlQueryObject, expression, distinct, fields.toArray(new IField[1]));
		
		    		for(Map<String, Object> map: returnMap) {
		    			list.add((TransazioneLlm)this.getTransazioneLlmFetch().fetch(jdbcProperties.getDatabase(), TransazioneApplicativoServer.model(), map));
		    		}
		    		
    	        }
		    		
    		}catch(NotFoundException notFound){
    			// ignore
    		}

        }else {

	        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
	        
	        for(Long id: ids) {
	        	list.add(this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour));
	        }
	        
        }

        return list;      
		
	}
	
	@Override
	public TransazioneLlm find(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) 
		throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, SQLQueryObjectException, ExpressionException, JDBCAdapterException, ExpressionNotImplementedException {

		if(efficiente){
			
        	JDBCPaginatedExpression pagExpr = this.toPaginatedExpression(expression, log);
        	pagExpr.limit(2);
        	
        	List<TransazioneLlm> list = findAll(jdbcProperties, log, connection, sqlQueryObject, pagExpr, idMappingResolutionBehaviour);
        	
        	if(list==null || list.isEmpty()) {
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
	public NonNegativeNumber count(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws NotImplementedException, ServiceException, SQLQueryObjectException, JDBCAdapterException, ExpressionException, ExpressionNotImplementedException {
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareCount(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getTransazioneLlmFieldConverter(), TransazioneLlm.model());
		
		sqlQueryObject.addSelectCountField(this.getTransazioneLlmFieldConverter().toTable(TransazioneLlm.model())+".id","tot",true);
		
		joinEngine(expression,sqlQueryObject);
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.count(jdbcProperties, log, connection, sqlQueryObject, expression,
																			this.getTransazioneLlmFieldConverter(), TransazioneLlm.model(),listaQuery);
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdTransazioneLlm id) throws NotFoundException, NotImplementedException, ServiceException, SQLQueryObjectException, ExpressionException, MultipleResultException, JDBCAdapterException {
		
		Long idTransazioneLlm = this.findIdTransazioneLlm(jdbcProperties, log, connection, sqlQueryObject, id, true);
        return this.inUseEngine(jdbcProperties, log, connection, sqlQueryObject, idTransazioneLlm);
		
	}

	@Override
	public List<Object> select(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCPaginatedExpression paginatedExpression, IField field) throws ServiceException,NotFoundException,NotImplementedException, ExpressionException, SQLQueryObjectException, JDBCAdapterException, ExpressionNotImplementedException {
		return this.select(jdbcProperties, log, connection, sqlQueryObject,
								paginatedExpression, false, field);
	}
	
	@Override
	public List<Object> select(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCPaginatedExpression paginatedExpression, boolean distinct, IField field) throws ServiceException,NotFoundException,NotImplementedException, ExpressionException, SQLQueryObjectException, JDBCAdapterException, ExpressionNotImplementedException {
		IField[] fields = new IField[]{field};
		List<Map<String,Object>> map = 
			this.select(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression, distinct, fields);
		return org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.selectSingleObject(map);
	}
	
	@Override
	public List<Map<String,Object>> select(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCPaginatedExpression paginatedExpression, IField ... field) throws ServiceException,NotFoundException,NotImplementedException, ExpressionException, SQLQueryObjectException, JDBCAdapterException, ExpressionNotImplementedException {
		return this.select(jdbcProperties, log, connection, sqlQueryObject,
								paginatedExpression, false, field);
	}
	
	@Override
	public List<Map<String,Object>> select(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCPaginatedExpression paginatedExpression, boolean distinct, IField ... field) throws ServiceException,NotFoundException,NotImplementedException, ExpressionException, SQLQueryObjectException, JDBCAdapterException, ExpressionNotImplementedException {
		
		org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.setFields(sqlQueryObject,paginatedExpression,field);
		try{
		
			ISQLQueryObject sqlQueryObjectDistinct = 
						org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareSqlQueryObjectForSelectDistinct(distinct,sqlQueryObject, paginatedExpression, log,
												this.getTransazioneLlmFieldConverter(), field);

			return selectEngine(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression, sqlQueryObjectDistinct);
			
		}finally{
			org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.removeFields(sqlQueryObject,paginatedExpression,field);
		}
	}

	@Override
	public Object aggregate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCExpression expression, FunctionField functionField) throws ServiceException,NotFoundException,NotImplementedException, ExpressionException, SQLQueryObjectException, JDBCAdapterException, ExpressionNotImplementedException {
		FunctionField[] functionFields = new FunctionField[]{functionField};
		Map<String,Object> map = 
			this.aggregate(jdbcProperties, log, connection, sqlQueryObject, expression, functionFields);
		return org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.selectAggregateObject(map,functionField);
	}
	
	@Override
	public Map<String,Object> aggregate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCExpression expression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException, ExpressionException, SQLQueryObjectException, JDBCAdapterException, ExpressionNotImplementedException {													
		
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
													JDBCExpression expression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException, ExpressionException, SQLQueryObjectException, JDBCAdapterException, ExpressionNotImplementedException {
		
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
													JDBCPaginatedExpression paginatedExpression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException, ExpressionException, SQLQueryObjectException, JDBCAdapterException, ExpressionNotImplementedException {
		
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
												IExpression expression) throws ServiceException,NotFoundException, SQLQueryObjectException, JDBCAdapterException, ExpressionException, ExpressionNotImplementedException {
		return selectEngine(jdbcProperties, log, connection, sqlQueryObject, expression, null);
	}
	protected List<Map<String,Object>> selectEngine(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
												IExpression expression, ISQLQueryObject sqlQueryObjectDistinct) throws ServiceException,NotFoundException, SQLQueryObjectException, JDBCAdapterException, ExpressionException, ExpressionNotImplementedException {
		
		List<Object> listaQuery = new ArrayList<>();
		List<JDBCObject> listaParams = new ArrayList<>();
		List<Object> returnField = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareSelect(jdbcProperties, log, connection, sqlQueryObject, 
        						expression, this.getTransazioneLlmFieldConverter(), TransazioneLlm.model(), 
        						listaQuery,listaParams);
		
		joinEngine(expression,sqlQueryObject);
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.select(jdbcProperties, log, connection,
        								org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareSqlQueryObjectForSelectDistinct(sqlQueryObject,sqlQueryObjectDistinct), 
        								expression, this.getTransazioneLlmFieldConverter(), TransazioneLlm.model(),
        								listaQuery,listaParams,returnField);
		if(list!=null && !list.isEmpty()){
			return list;
		}
		else{
			throw new NotFoundException("Not Found");
		}
	}
	
	@Override
	public List<Map<String,Object>> union(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
												Union union, UnionExpression ... unionExpression) throws ServiceException,NotFoundException,NotImplementedException, ExpressionException, ExpressionNotImplementedException, SQLQueryObjectException, JDBCAdapterException {		
		
		List<ISQLQueryObject> sqlQueryObjectInnerList = new ArrayList<>();
		List<JDBCObject> jdbcObjects = new ArrayList<>();
		List<Class<?>> returnClassTypes = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareUnion(jdbcProperties, log, connection, sqlQueryObject, 
        						this.getTransazioneLlmFieldConverter(), TransazioneLlm.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				joinEngine(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.union(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getTransazioneLlmFieldConverter(), TransazioneLlm.model(), 
        								sqlQueryObjectInnerList, jdbcObjects, returnClassTypes, union, unionExpression);
        if(list!=null && !list.isEmpty()){
			return list;
		}
		else{
			throw new NotFoundException("Not Found");
		}								
	}
	
	@Override
	public NonNegativeNumber unionCount(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
												Union union, UnionExpression ... unionExpression) throws ServiceException,NotFoundException,NotImplementedException, ExpressionException, ExpressionNotImplementedException, SQLQueryObjectException, JDBCAdapterException {		
		
		List<ISQLQueryObject> sqlQueryObjectInnerList = new ArrayList<>();
		List<JDBCObject> jdbcObjects = new ArrayList<>();
		List<Class<?>> returnClassTypes = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareUnionCount(jdbcProperties, log, connection, sqlQueryObject, 
        						this.getTransazioneLlmFieldConverter(), TransazioneLlm.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				joinEngine(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        NonNegativeNumber number = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.unionCount(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getTransazioneLlmFieldConverter(), TransazioneLlm.model(), 
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
			return new JDBCExpression(this.getTransazioneLlmFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}


	@Override
	public JDBCPaginatedExpression newPaginatedExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(this.getTransazioneLlmFieldConverter());
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
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdTransazioneLlm id, TransazioneLlm obj) throws NotFoundException,NotImplementedException,ServiceException, MultipleResultException, ExpressionNotImplementedException, ExpressionException, SQLQueryObjectException, JDBCAdapterException{
		mappingTableIdsEngine(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,id,null));
	}
	
	@Override
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, TransazioneLlm obj) throws NotFoundException,NotImplementedException,ServiceException, MultipleResultException, SQLQueryObjectException, JDBCAdapterException, ExpressionException{
		mappingTableIdsEngine(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,tableId,null));
	}
	private void mappingTableIdsEngine(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, TransazioneLlm obj, TransazioneLlm imgSaved) {
		if(imgSaved==null){
			return;
		}
		obj.setId(imgSaved.getId());

		if(jdbcProperties==null || log==null || connection==null || sqlQueryObject==null) {
			// nop
		}
	}
	
	@Override
	public TransazioneLlm get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, SQLQueryObjectException, JDBCAdapterException, ExpressionException {
		return this.getEngine(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId), idMappingResolutionBehaviour);
	}
	
	private TransazioneLlm getEngine(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, ServiceException, SQLQueryObjectException, JDBCAdapterException, ExpressionException {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
				
		TransazioneLlm transazioneLlm = null;
		

		// Object transazioneLlm
		ISQLQueryObject sqlQueryObjectGetTransazioneLlm = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGetTransazioneLlm.setANDLogicOperator(true);
		sqlQueryObjectGetTransazioneLlm.addFromTable(this.getTransazioneLlmFieldConverter().toTable(TransazioneLlm.model()));
		sqlQueryObjectGetTransazioneLlm.addSelectField("id");
		sqlQueryObjectGetTransazioneLlm.addSelectField(this.getTransazioneLlmFieldConverter().toColumn(TransazioneLlm.model().ID_TRANSAZIONE,true));
		sqlQueryObjectGetTransazioneLlm.addSelectField(this.getTransazioneLlmFieldConverter().toColumn(TransazioneLlm.model().DATA_INGRESSO_RICHIESTA,true));
		sqlQueryObjectGetTransazioneLlm.addSelectField(this.getTransazioneLlmFieldConverter().toColumn(TransazioneLlm.model().LLM_PROVIDER,true));
		sqlQueryObjectGetTransazioneLlm.addSelectField(this.getTransazioneLlmFieldConverter().toColumn(TransazioneLlm.model().LLM_MODEL,true));
		sqlQueryObjectGetTransazioneLlm.addSelectField(this.getTransazioneLlmFieldConverter().toColumn(TransazioneLlm.model().LLM_PROVIDER_BINDING,true));
		sqlQueryObjectGetTransazioneLlm.addSelectField(this.getTransazioneLlmFieldConverter().toColumn(TransazioneLlm.model().TOKEN_INPUT,true));
		sqlQueryObjectGetTransazioneLlm.addSelectField(this.getTransazioneLlmFieldConverter().toColumn(TransazioneLlm.model().TOKEN_OUTPUT,true));
		sqlQueryObjectGetTransazioneLlm.addSelectField(this.getTransazioneLlmFieldConverter().toColumn(TransazioneLlm.model().COST_ESTIMATED,true));
		sqlQueryObjectGetTransazioneLlm.addWhereCondition("id=?");

		// Get transazioneLlm
		transazioneLlm = (TransazioneLlm) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGetTransazioneLlm.createSQLQuery(), jdbcProperties.isShowSql(), TransazioneLlm.model(), this.getTransazioneLlmFetch(),
			new JDBCObject(tableId,Long.class));

		if(idMappingResolutionBehaviour==null) {
			// no
		}

		
        return transazioneLlm;  
	
	} 
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws MultipleResultException, NotImplementedException, ServiceException, SQLQueryObjectException, JDBCAdapterException, ExpressionException {
		return this.existsEngine(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	private boolean existsEngine(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws ServiceException, SQLQueryObjectException, JDBCAdapterException, ExpressionException {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		boolean existsTransazioneLlm = false;

		sqlQueryObject = sqlQueryObject.newSQLQueryObject();
		sqlQueryObject.setANDLogicOperator(true);

		sqlQueryObject.addFromTable(this.getTransazioneLlmFieldConverter().toTable(TransazioneLlm.model()));
		sqlQueryObject.addSelectField(this.getTransazioneLlmFieldConverter().toColumn(TransazioneLlm.model().ID_TRANSAZIONE,true));
		sqlQueryObject.addWhereCondition("id=?");


		// Exists transazioneLlm
		existsTransazioneLlm = jdbcUtilities.exists(sqlQueryObject.createSQLQuery(), jdbcProperties.isShowSql(),
			new JDBCObject(tableId,Long.class));

		
        return existsTransazioneLlm;
	
	}
	
	private void joinEngine(IExpression expression, ISQLQueryObject sqlQueryObject) {
	
		if(expression!=null && sqlQueryObject!=null) {
			// 	NOP
		}
	}
	
	protected java.util.List<Object> getRootTablePrimaryKeyValuesEngine(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdTransazioneLlm id) throws NotFoundException, ServiceException, SQLQueryObjectException, ExpressionException, MultipleResultException, JDBCAdapterException {
	    // Identificativi
        java.util.List<Object> rootTableIdValues = new java.util.ArrayList<>();
        // Define the column values used to identify the primary key
		Long longId = this.findIdTransazioneLlm(jdbcProperties, log, connection, sqlQueryObject.newSQLQueryObject(), id, true);
		rootTableIdValues.add(longId);
                
        return rootTableIdValues;
	}
	
	protected Map<String, List<IField>> getMapTableToPKColumnEngine() throws ExpressionException{
	
		TransazioneLlmFieldConverter converter = this.getTransazioneLlmFieldConverter();
		Map<String, List<IField>> mapTableToPKColumn = new java.util.HashMap<>();
		UtilsTemplate<IField> utilities = new UtilsTemplate<>();

		// Define the columns used to identify the primary key
		//		  If a table doesn't have a primary key, don't add it to this map

		// TransazioneLlm.model()
		mapTableToPKColumn.put(converter.toTable(TransazioneLlm.model()),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(TransazioneLlm.model()))
			));
        
        return mapTableToPKColumn;		
	}
	
	@Override
	public List<Long> findAllTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression paginatedExpression) throws ServiceException, NotImplementedException, SQLQueryObjectException, ExpressionException, JDBCAdapterException, ExpressionNotImplementedException {
		
		List<Long> list = new ArrayList<>();

		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getTransazioneLlmFieldConverter().toTable(TransazioneLlm.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareFindAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
												this.getTransazioneLlmFieldConverter(), TransazioneLlm.model());
		
		joinEngine(paginatedExpression,sqlQueryObject);
		
		List<Object> listObjects = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.findAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
																			this.getTransazioneLlmFieldConverter(), TransazioneLlm.model(), objectIdClass, listaQuery);
		for(Object object: listObjects) {
			list.add((Long)object);
		}

        return list;
		
	}
	
	@Override
	public long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, SQLQueryObjectException, ExpressionException, JDBCAdapterException, ExpressionNotImplementedException {
	
		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getTransazioneLlmFieldConverter().toTable(TransazioneLlm.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.prepareFind(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getTransazioneLlmFieldConverter(), TransazioneLlm.model());
		
		joinEngine(expression,sqlQueryObject);

		Object res = org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.find(jdbcProperties, log, connection, sqlQueryObject, expression,
														this.getTransazioneLlmFieldConverter(), TransazioneLlm.model(), objectIdClass, listaQuery);
		if(res!=null && (((Long) res).longValue()>0) ){
			return ((Long) res).longValue();
		}
		else{
			throw new NotFoundException("Not Found");
		}
		
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) {
		return this.inUseEngine(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}

	private InUse inUseEngine(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) {

		InUse inUse = new InUse();
		inUse.setInUse(false);
		
		if(jdbcProperties==null || log==null || connection==null || sqlQueryObject==null || tableId==null) {
			// nop
		}
		
        return inUse;

	}
	
	@Override
	public IdTransazioneLlm findId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, SQLQueryObjectException, JDBCAdapterException, ExpressionException, MultipleResultException {
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
          
		// Object _transazioneLlm
		sqlQueryObjectGet.addFromTable(this.getTransazioneLlmFieldConverter().toTable(TransazioneLlm.model()));
		sqlQueryObjectGet.addSelectField(this.getTransazioneLlmFieldConverter().toColumn(TransazioneLlm.model().ID_TRANSAZIONE,true));
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addWhereCondition("id=?");

		// Recupero _transazioneLlm
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParamsTransazioneLlm = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(tableId,Long.class)
		};
		List<Class<?>> listaFieldIdReturnTypeTransazioneLlm = new ArrayList<>();
		listaFieldIdReturnTypeTransazioneLlm.add(String.class);
		org.openspcoop2.core.transazioni.IdTransazioneLlm idTransazioneLlm = null;
		List<Object> listaFieldIdTransazioneLlm = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnTypeTransazioneLlm, searchParamsTransazioneLlm);
		if(listaFieldIdTransazioneLlm==null || listaFieldIdTransazioneLlm.isEmpty()){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		else{
			// set _transazioneLlm
			idTransazioneLlm = new org.openspcoop2.core.transazioni.IdTransazioneLlm();
			idTransazioneLlm.setIdTransazione((String)listaFieldIdTransazioneLlm.get(0));
		}
		
		return idTransazioneLlm;
		
	}

	@Override
	public Long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdTransazioneLlm id, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, SQLQueryObjectException, ExpressionException, MultipleResultException, JDBCAdapterException {
	
		return this.findIdTransazioneLlm(jdbcProperties,log,connection,sqlQueryObject,id,throwNotFound);
			
	}
	
	@Override
	public List<List<Object>> nativeQuery(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
											String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException, SQLQueryObjectException, JDBCAdapterException{
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities.nativeQuery(jdbcProperties, log, connection, sqlQueryObject,
																							sql,returnClassTypes,param);
														
	}
	
	protected Long findIdTransazioneLlm(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdTransazioneLlm id, boolean throwNotFound) throws NotFoundException, ServiceException, SQLQueryObjectException, ExpressionException, MultipleResultException, JDBCAdapterException {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();

		
		if(id==null) {
			throw new ServiceException("Id non definito");
		}
		if(id.getIdTransazione()==null) {
			throw new ServiceException("IdTransazione non definito");
		}

		// Object _transazioneLlm
		sqlQueryObjectGet.addFromTable(this.getTransazioneLlmFieldConverter().toTable(TransazioneLlm.model()));
		sqlQueryObjectGet.addSelectField("id");
		// Devono essere mappati nella where condition i metodi dell'oggetto id.getXXX
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.setSelectDistinct(true);
		sqlQueryObjectGet.addWhereCondition(this.getTransazioneLlmFieldConverter().toColumn(TransazioneLlm.model().ID_TRANSAZIONE,true)+"=?");

		// Recupero _transazioneLlm
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParamsTransazioneLlm = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id.getIdTransazione(),String.class),
		};
		Long idTransazioneLlm = null;
		try{
			idTransazioneLlm = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
						Long.class, searchParamsTransazioneLlm);
		}catch(NotFoundException notFound){
			if(throwNotFound){
				throw new NotFoundException(notFound);
			}
		}
		if( (idTransazioneLlm==null || idTransazioneLlm<=0) 
				&&
			throwNotFound){
			throw new NotFoundException("Not Found");
		}
		
		return idTransazioneLlm;
	}
}
