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

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.openspcoop2.utils.jdbc.IJDBCAdapter;
import org.openspcoop2.utils.jdbc.JDBCAdapterFactory;
import org.openspcoop2.utils.sql.ISQLQueryObject;

import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.generic_project.dao.jdbc.utils.IJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject;
import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceSearchWithId;
import org.openspcoop2.core.transazioni.IdDumpMessaggio;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
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
import org.openspcoop2.core.transazioni.dao.jdbc.converter.DumpMessaggioFieldConverter;
import org.openspcoop2.core.transazioni.dao.jdbc.fetch.DumpMessaggioFetch;
import org.openspcoop2.core.transazioni.utils.DumpUtils;
import org.openspcoop2.core.transazioni.DumpHeaderTrasporto;
import org.openspcoop2.core.transazioni.DumpHeaderAllegato;
import org.openspcoop2.core.transazioni.DumpContenuto;
import org.openspcoop2.core.transazioni.DumpAllegato;
import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.DumpMultipartHeader;

/**     
 * JDBCDumpMessaggioServiceSearchImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCDumpMessaggioServiceSearchImpl implements IJDBCServiceSearchWithId<DumpMessaggio, IdDumpMessaggio, JDBCServiceManager> {

	private DumpMessaggioFieldConverter _dumpMessaggioFieldConverter = null;
	public DumpMessaggioFieldConverter getDumpMessaggioFieldConverter() {
		if(this._dumpMessaggioFieldConverter==null){
			this._dumpMessaggioFieldConverter = new DumpMessaggioFieldConverter(this.jdbcServiceManager.getJdbcProperties().getDatabaseType());
		}		
		return this._dumpMessaggioFieldConverter;
	}
	@Override
	public ISQLFieldConverter getFieldConverter() {
		return this.getDumpMessaggioFieldConverter();
	}
	
	private DumpMessaggioFetch dumpMessaggioFetch = new DumpMessaggioFetch();
	public DumpMessaggioFetch getDumpMessaggioFetch() {
		return this.dumpMessaggioFetch;
	}
	@Override
	public IJDBCFetch getFetch() {
		return getDumpMessaggioFetch();
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
	public IdDumpMessaggio convertToId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, DumpMessaggio dumpMessaggio) throws NotImplementedException, ServiceException, Exception{
	
		IdDumpMessaggio idDumpMessaggio = new IdDumpMessaggio();
		idDumpMessaggio.setIdTransazione(dumpMessaggio.getIdTransazione());
        idDumpMessaggio.setTipoMessaggio(dumpMessaggio.getTipoMessaggio());
        idDumpMessaggio.setServizioApplicativoErogatore(dumpMessaggio.getServizioApplicativoErogatore());
        return idDumpMessaggio;
	}
	
	@Override
	public DumpMessaggio get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdDumpMessaggio id, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {
		Long id_dumpMessaggio = ( (id!=null && id.getId()!=null && id.getId()>0) ? id.getId() : this.findIdDumpMessaggio(jdbcProperties, log, connection, sqlQueryObject, id, true));
		return this._get(jdbcProperties, log, connection, sqlQueryObject, id_dumpMessaggio,idMappingResolutionBehaviour);
		
		
	}
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdDumpMessaggio id) throws MultipleResultException, NotImplementedException, ServiceException,Exception {

		Long id_dumpMessaggio = this.findIdDumpMessaggio(jdbcProperties, log, connection, sqlQueryObject, id, false);
		return id_dumpMessaggio != null && id_dumpMessaggio > 0;
		
	}
	
	@Override
	public List<IdDumpMessaggio> findAllIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

		List<IdDumpMessaggio> list = new ArrayList<IdDumpMessaggio>();

		// TODO: implementazione non efficente. 
		// Per ottenere una implementazione efficente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari a create l'ID logico
		// 2. Usare metodo getDumpMessaggioFetch() sul risultato della select per ottenere un oggetto DumpMessaggio
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 
		// 3. Usare metodo convertToId per ottenere l'id

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
        
        for(Long id: ids) {
        	
        	ISQLQueryObject sqlQueryObjectReadIds = sqlQueryObject.newSQLQueryObject();
        	sqlQueryObjectReadIds.addFromTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model()));
        	sqlQueryObjectReadIds.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ID_TRANSAZIONE, false));
        	sqlQueryObjectReadIds.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().TIPO_MESSAGGIO, false));
        	sqlQueryObjectReadIds.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().SERVIZIO_APPLICATIVO_EROGATORE, false));
        	sqlQueryObjectReadIds.addWhereCondition("id=?");
        	
        	List<Class<?>> returnTypes = new ArrayList<Class<?>>();
        	returnTypes.add(String.class);
        	returnTypes.add(String.class);
        	returnTypes.add(String.class);
        	JDBCObject param = new JDBCObject(id, Long.class);
        	List<Object> result = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectReadIds.toString(), jdbcProperties.isShowSql(), returnTypes, param);
        	
        	IdDumpMessaggio idDumpMessaggio = new IdDumpMessaggio();
        	idDumpMessaggio.setIdTransazione((String)result.get(0));
        	idDumpMessaggio.setTipoMessaggio(TipoMessaggio.toEnumConstant((String)result.get(1)));
        	Object sa = result.get(2);
        	if(sa!=null && sa instanceof String) {
        		idDumpMessaggio.setServizioApplicativoErogatore((String)sa);
        	}
        	list.add(idDumpMessaggio);
        }

        return list;
		
	}
	
	@Override
	public List<DumpMessaggio> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

        List<DumpMessaggio> list = new ArrayList<DumpMessaggio>();
        
        // TODO: implementazione non efficente. 
		// Per ottenere una implementazione efficente:
		// 1. Usare metodo select di questa classe indirizzando esattamente i field necessari
		// 2. Usare metodo getDumpMessaggioFetch() sul risultato della select per ottenere un oggetto DumpMessaggio
		//	  La fetch con la map inserirà nell'oggetto solo i valori estratti 

        List<Long> ids = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, expression);
        
        for(Long id: ids) {
        	list.add(this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour));
        }

        return list;      
		
	}
	
	@Override
	public DumpMessaggio find(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) 
		throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {

        long id = this.findTableId(jdbcProperties, log, connection, sqlQueryObject, expression);
        if(id>0){
        	return this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour);
        }else{
        	throw new NotFoundException("Entry with id["+id+"] not found");
        }
		
	}
	
	public InputStream getContentInputStream(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) 
			throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {

        long id = this.findTableId(jdbcProperties, log, connection, sqlQueryObject, expression);
        if(id>0){
        	
            IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(this.getDumpMessaggioFieldConverter().getDatabaseType());
            ISQLQueryObject sqlQueryObjectGet_dumpMessaggio = sqlQueryObject.newSQLQueryObject();
            sqlQueryObjectGet_dumpMessaggio.setANDLogicOperator(true);
    		sqlQueryObjectGet_dumpMessaggio.addFromTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model()));
    		sqlQueryObjectGet_dumpMessaggio.addSelectAliasField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().BODY,true),"contentBodyStream");
    		sqlQueryObjectGet_dumpMessaggio.addWhereCondition("id=?");
            ResultSet rs = null;
            PreparedStatement pstmt = null;
            try {
            	String query = sqlQueryObjectGet_dumpMessaggio.createSQLQuery();
            	pstmt = connection.prepareStatement(query);
            	pstmt.setLong(1, id);
            	rs = pstmt.executeQuery();
            	if(rs.next()) {
            		return jdbcAdapter.getBinaryStream(rs, "contentBodyStream");
            	}
            	return null;
            }finally {
            	try {
            		if(rs!=null) {
            			rs.close();
            		}
            	}catch(Throwable eClose) {}
            	try {
            		if(pstmt!=null) {
            			pstmt.close();
            		}
            	}catch(Throwable eClose) {}
            }
        	
        }else{
        	throw new NotFoundException("Entry with id["+id+"] not found");
        }
        

	}
	
	@Override
	public NonNegativeNumber count(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws NotImplementedException, ServiceException,Exception {
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareCount(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getDumpMessaggioFieldConverter(), DumpMessaggio.model());
		
		sqlQueryObject.addSelectCountField(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model())+".id","tot",true);
		
		_join(expression,sqlQueryObject);
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.count(jdbcProperties, log, connection, sqlQueryObject, expression,
																			this.getDumpMessaggioFieldConverter(), DumpMessaggio.model(),listaQuery);
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdDumpMessaggio id) throws NotFoundException, NotImplementedException, ServiceException,Exception {
		
		Long id_dumpMessaggio = this.findIdDumpMessaggio(jdbcProperties, log, connection, sqlQueryObject, id, true);
        return this._inUse(jdbcProperties, log, connection, sqlQueryObject, id_dumpMessaggio);
		
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
												this.getDumpMessaggioFieldConverter(), field);

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
        						expression, this.getDumpMessaggioFieldConverter(), DumpMessaggio.model(), 
        						listaQuery,listaParams);
		
		_join(expression,sqlQueryObject);
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.select(jdbcProperties, log, connection,
        								org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareSqlQueryObjectForSelectDistinct(sqlQueryObject,sqlQueryObjectDistinct), 
        								expression, this.getDumpMessaggioFieldConverter(), DumpMessaggio.model(),
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
        						this.getDumpMessaggioFieldConverter(), DumpMessaggio.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.union(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getDumpMessaggioFieldConverter(), DumpMessaggio.model(), 
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
        						this.getDumpMessaggioFieldConverter(), DumpMessaggio.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        NonNegativeNumber number = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.unionCount(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getDumpMessaggioFieldConverter(), DumpMessaggio.model(), 
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
			return new JDBCExpression(this.getDumpMessaggioFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}


	@Override
	public JDBCPaginatedExpression newPaginatedExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(this.getDumpMessaggioFieldConverter());
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
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdDumpMessaggio id, DumpMessaggio obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,id,null));
	}
	
	@Override
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, DumpMessaggio obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,tableId,null));
	}
	private void _mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, DumpMessaggio obj, DumpMessaggio imgSaved) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		if(imgSaved==null){
			return;
		}
		obj.setId(imgSaved.getId());
		if(obj.getMultipartHeaderList()!=null){
			List<org.openspcoop2.core.transazioni.DumpMultipartHeader> listObj_ = obj.getMultipartHeaderList();
			for(org.openspcoop2.core.transazioni.DumpMultipartHeader itemObj_ : listObj_){
				org.openspcoop2.core.transazioni.DumpMultipartHeader itemAlreadySaved_ = null;
				if(imgSaved.getMultipartHeaderList()!=null){
					List<org.openspcoop2.core.transazioni.DumpMultipartHeader> listImgSaved_ = imgSaved.getMultipartHeaderList();
					for(org.openspcoop2.core.transazioni.DumpMultipartHeader itemImgSaved_ : listImgSaved_){
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
		if(obj.getHeaderTrasportoList()!=null){
			List<org.openspcoop2.core.transazioni.DumpHeaderTrasporto> listObj_ = obj.getHeaderTrasportoList();
			for(org.openspcoop2.core.transazioni.DumpHeaderTrasporto itemObj_ : listObj_){
				org.openspcoop2.core.transazioni.DumpHeaderTrasporto itemAlreadySaved_ = null;
				if(imgSaved.getHeaderTrasportoList()!=null){
					List<org.openspcoop2.core.transazioni.DumpHeaderTrasporto> listImgSaved_ = imgSaved.getHeaderTrasportoList();
					for(org.openspcoop2.core.transazioni.DumpHeaderTrasporto itemImgSaved_ : listImgSaved_){
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
		if(obj.getAllegatoList()!=null){
			List<org.openspcoop2.core.transazioni.DumpAllegato> listObj_ = obj.getAllegatoList();
			for(org.openspcoop2.core.transazioni.DumpAllegato itemObj_ : listObj_){
				org.openspcoop2.core.transazioni.DumpAllegato itemAlreadySaved_ = null;
				if(imgSaved.getAllegatoList()!=null){
					List<org.openspcoop2.core.transazioni.DumpAllegato> listImgSaved_ = imgSaved.getAllegatoList();
					for(org.openspcoop2.core.transazioni.DumpAllegato itemImgSaved_ : listImgSaved_){
						boolean objEqualsToImgSaved_ = false;
						objEqualsToImgSaved_ = org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getContentId(),itemImgSaved_.getContentId());
						if(objEqualsToImgSaved_){
							itemAlreadySaved_=itemImgSaved_;
							break;
						}
					}
				}
				if(itemAlreadySaved_!=null){
					itemObj_.setId(itemAlreadySaved_.getId());
					if(itemObj_.getHeaderList()!=null){
						List<org.openspcoop2.core.transazioni.DumpHeaderAllegato> listObj_allegato = itemObj_.getHeaderList();
						for(org.openspcoop2.core.transazioni.DumpHeaderAllegato itemObj_allegato : listObj_allegato){
							org.openspcoop2.core.transazioni.DumpHeaderAllegato itemAlreadySaved_allegato = null;
							if(itemAlreadySaved_.getHeaderList()!=null){
								List<org.openspcoop2.core.transazioni.DumpHeaderAllegato> listImgSaved_allegato = itemAlreadySaved_.getHeaderList();
								for(org.openspcoop2.core.transazioni.DumpHeaderAllegato itemImgSaved_allegato : listImgSaved_allegato){
									boolean objEqualsToImgSaved_allegato = false;
									objEqualsToImgSaved_allegato = org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_allegato.getNome(),itemImgSaved_allegato.getNome());
									if(objEqualsToImgSaved_allegato){
										itemAlreadySaved_allegato=itemImgSaved_allegato;
										break;
									}
								}
							}
							if(itemAlreadySaved_allegato!=null){
								itemObj_allegato.setId(itemAlreadySaved_allegato.getId());
							}
						}
					}
				}
			}
		}
		if(obj.getContenutoList()!=null){
			List<org.openspcoop2.core.transazioni.DumpContenuto> listObj_ = obj.getContenutoList();
			for(org.openspcoop2.core.transazioni.DumpContenuto itemObj_ : listObj_){
				org.openspcoop2.core.transazioni.DumpContenuto itemAlreadySaved_ = null;
				if(imgSaved.getContenutoList()!=null){
					List<org.openspcoop2.core.transazioni.DumpContenuto> listImgSaved_ = imgSaved.getContenutoList();
					for(org.openspcoop2.core.transazioni.DumpContenuto itemImgSaved_ : listImgSaved_){
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
	public DumpMessaggio get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._get(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId), idMappingResolutionBehaviour);
	}
	
	protected DumpMessaggio _get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();
				
		DumpMessaggio dumpMessaggio = new DumpMessaggio();
		
		List<JDBCObject> listJDBCObject = new ArrayList<JDBCObject>();
		
		// Object dumpMessaggio
		ISQLQueryObject sqlQueryObjectGet_dumpMessaggio = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_dumpMessaggio.setANDLogicOperator(true);
		sqlQueryObjectGet_dumpMessaggio.addFromTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model()));
		sqlQueryObjectGet_dumpMessaggio.addSelectField("id");
		sqlQueryObjectGet_dumpMessaggio.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ID_TRANSAZIONE,true));
		sqlQueryObjectGet_dumpMessaggio.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().PROTOCOLLO,true));
		sqlQueryObjectGet_dumpMessaggio.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().SERVIZIO_APPLICATIVO_EROGATORE,true));
		sqlQueryObjectGet_dumpMessaggio.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().DATA_CONSEGNA_EROGATORE,true));
		sqlQueryObjectGet_dumpMessaggio.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().TIPO_MESSAGGIO,true));
		sqlQueryObjectGet_dumpMessaggio.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().FORMATO_MESSAGGIO,true));
		sqlQueryObjectGet_dumpMessaggio.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().CONTENT_TYPE,true));
		sqlQueryObjectGet_dumpMessaggio.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().CONTENT_LENGTH,true));
		sqlQueryObjectGet_dumpMessaggio.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().MULTIPART_CONTENT_TYPE,true));
		sqlQueryObjectGet_dumpMessaggio.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().MULTIPART_CONTENT_ID,true));
		sqlQueryObjectGet_dumpMessaggio.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().MULTIPART_CONTENT_LOCATION,true));
		DumpUtils.selectContentByThreshold(sqlQueryObjectGet_dumpMessaggio, this.getDumpMessaggioFieldConverter(), listJDBCObject);
		sqlQueryObjectGet_dumpMessaggio.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().DUMP_TIMESTAMP,true));
		sqlQueryObjectGet_dumpMessaggio.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().POST_PROCESS_HEADER,true));
		sqlQueryObjectGet_dumpMessaggio.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().POST_PROCESS_FILENAME,true));
		sqlQueryObjectGet_dumpMessaggio.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().POST_PROCESS_CONTENT,true));
		sqlQueryObjectGet_dumpMessaggio.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().POST_PROCESS_CONFIG_ID,true));
		sqlQueryObjectGet_dumpMessaggio.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().POST_PROCESS_TIMESTAMP,true));
		sqlQueryObjectGet_dumpMessaggio.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().POST_PROCESSED,true));
		sqlQueryObjectGet_dumpMessaggio.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().MULTIPART_HEADER_EXT,true));
		sqlQueryObjectGet_dumpMessaggio.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().HEADER_EXT,true));
		sqlQueryObjectGet_dumpMessaggio.addWhereCondition("id=?");

		// Get dumpMessaggio
		listJDBCObject.add(new JDBCObject(tableId,Long.class));
		dumpMessaggio = (DumpMessaggio) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_dumpMessaggio.createSQLQuery(), jdbcProperties.isShowSql(), DumpMessaggio.model(), this.getDumpMessaggioFetch(),
				listJDBCObject.toArray(new JDBCObject[1]));



		// Object dumpMessaggio_dumpMultipartHeader
		ISQLQueryObject sqlQueryObjectGet_dumpMessaggio_dumpMultipartHeader = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_dumpMessaggio_dumpMultipartHeader.setANDLogicOperator(true);
		sqlQueryObjectGet_dumpMessaggio_dumpMultipartHeader.addFromTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().MULTIPART_HEADER));
		sqlQueryObjectGet_dumpMessaggio_dumpMultipartHeader.addSelectField("id");
		sqlQueryObjectGet_dumpMessaggio_dumpMultipartHeader.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().MULTIPART_HEADER.NOME,true));
		sqlQueryObjectGet_dumpMessaggio_dumpMultipartHeader.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().MULTIPART_HEADER.VALORE,true));
		sqlQueryObjectGet_dumpMessaggio_dumpMultipartHeader.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().MULTIPART_HEADER.DUMP_TIMESTAMP,true));
		sqlQueryObjectGet_dumpMessaggio_dumpMultipartHeader.addWhereCondition("id_messaggio=?");

		// Get dumpMessaggio_dumpMultipartHeader
		java.util.List<Object> dumpMessaggio_dumpMultipartHeader_list = jdbcUtilities.executeQuery(sqlQueryObjectGet_dumpMessaggio_dumpMultipartHeader.createSQLQuery(), jdbcProperties.isShowSql(), DumpMessaggio.model().MULTIPART_HEADER, this.getDumpMessaggioFetch(),
			new JDBCObject(dumpMessaggio.getId(),Long.class));

		if(dumpMessaggio_dumpMultipartHeader_list != null) {
			for (Object dumpMessaggio_dumpMultipartHeader_object: dumpMessaggio_dumpMultipartHeader_list) {
				DumpMultipartHeader dumpMessaggio_dumpMultipartHeader = (DumpMultipartHeader) dumpMessaggio_dumpMultipartHeader_object;

				// Bug fix OPPT-466 per gestione empty string as null on oracle
				// Rilasciare il vincolo di not null 'fisico' sul database.
				// Imporlo logicamente, in modo che non siano permessi insert o update con valori null.
				// Dopodichè, se nella get viene recuperato un valore null, deve essere trasformato in stringa vuota.
				if(dumpMessaggio_dumpMultipartHeader.getValore()==null){
					dumpMessaggio_dumpMultipartHeader.setValore("");
				}

				dumpMessaggio.addMultipartHeader(dumpMessaggio_dumpMultipartHeader);
			}
		}

		// Object dumpMessaggio_dumpHeaderTrasporto
		ISQLQueryObject sqlQueryObjectGet_dumpMessaggio_dumpHeaderTrasporto = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_dumpMessaggio_dumpHeaderTrasporto.setANDLogicOperator(true);
		sqlQueryObjectGet_dumpMessaggio_dumpHeaderTrasporto.addFromTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().HEADER_TRASPORTO));
		sqlQueryObjectGet_dumpMessaggio_dumpHeaderTrasporto.addSelectField("id");
		sqlQueryObjectGet_dumpMessaggio_dumpHeaderTrasporto.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().HEADER_TRASPORTO.NOME,true));
		sqlQueryObjectGet_dumpMessaggio_dumpHeaderTrasporto.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().HEADER_TRASPORTO.VALORE,true));
		sqlQueryObjectGet_dumpMessaggio_dumpHeaderTrasporto.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().HEADER_TRASPORTO.DUMP_TIMESTAMP,true));
		sqlQueryObjectGet_dumpMessaggio_dumpHeaderTrasporto.addWhereCondition("id_messaggio=?");

		// Get dumpMessaggio_dumpHeaderTrasporto
		java.util.List<Object> dumpMessaggio_dumpHeaderTrasporto_list = jdbcUtilities.executeQuery(sqlQueryObjectGet_dumpMessaggio_dumpHeaderTrasporto.createSQLQuery(), jdbcProperties.isShowSql(), DumpMessaggio.model().HEADER_TRASPORTO, this.getDumpMessaggioFetch(),
			new JDBCObject(dumpMessaggio.getId(),Long.class));

		if(dumpMessaggio_dumpHeaderTrasporto_list != null) {
			for (Object dumpMessaggio_dumpHeaderTrasporto_object: dumpMessaggio_dumpHeaderTrasporto_list) {
				DumpHeaderTrasporto dumpMessaggio_dumpHeaderTrasporto = (DumpHeaderTrasporto) dumpMessaggio_dumpHeaderTrasporto_object;

				// Bug fix OPPT-466 per gestione empty string as null on oracle
				// Rilasciare il vincolo di not null 'fisico' sul database.
				// Imporlo logicamente, in modo che non siano permessi insert o update con valori null.
				// Dopodichè, se nella get viene recuperato un valore null, deve essere trasformato in stringa vuota.
				if(dumpMessaggio_dumpHeaderTrasporto.getValore()==null){
					dumpMessaggio_dumpHeaderTrasporto.setValore("");
				}

				dumpMessaggio.addHeaderTrasporto(dumpMessaggio_dumpHeaderTrasporto);
			}
		}

		// Object dumpMessaggio_dumpAllegato
		ISQLQueryObject sqlQueryObjectGet_dumpMessaggio_dumpAllegato = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_dumpMessaggio_dumpAllegato.setANDLogicOperator(true);
		sqlQueryObjectGet_dumpMessaggio_dumpAllegato.addFromTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().ALLEGATO));
		sqlQueryObjectGet_dumpMessaggio_dumpAllegato.addSelectField("id");
		sqlQueryObjectGet_dumpMessaggio_dumpAllegato.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.CONTENT_TYPE,true));
		sqlQueryObjectGet_dumpMessaggio_dumpAllegato.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.CONTENT_ID,true));
		sqlQueryObjectGet_dumpMessaggio_dumpAllegato.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.CONTENT_LOCATION,true));
		sqlQueryObjectGet_dumpMessaggio_dumpAllegato.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.ALLEGATO,true));
		sqlQueryObjectGet_dumpMessaggio_dumpAllegato.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.DUMP_TIMESTAMP,true));
		sqlQueryObjectGet_dumpMessaggio_dumpAllegato.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.HEADER_EXT,true));
		sqlQueryObjectGet_dumpMessaggio_dumpAllegato.addWhereCondition("id_messaggio=?");

		// Get dumpMessaggio_dumpAllegato
		java.util.List<Object> dumpMessaggio_dumpAllegato_list = jdbcUtilities.executeQuery(sqlQueryObjectGet_dumpMessaggio_dumpAllegato.createSQLQuery(), jdbcProperties.isShowSql(), DumpMessaggio.model().ALLEGATO, this.getDumpMessaggioFetch(),
			new JDBCObject(dumpMessaggio.getId(),Long.class));

		if(dumpMessaggio_dumpAllegato_list != null) {
			for (Object dumpMessaggio_dumpAllegato_object: dumpMessaggio_dumpAllegato_list) {
				DumpAllegato dumpMessaggio_dumpAllegato = (DumpAllegato) dumpMessaggio_dumpAllegato_object;



				// Object dumpMessaggio_dumpAllegato_dumpHeaderAllegato
				ISQLQueryObject sqlQueryObjectGet_dumpMessaggio_dumpAllegato_dumpHeaderAllegato = sqlQueryObjectGet.newSQLQueryObject();
				sqlQueryObjectGet_dumpMessaggio_dumpAllegato_dumpHeaderAllegato.setANDLogicOperator(true);
				sqlQueryObjectGet_dumpMessaggio_dumpAllegato_dumpHeaderAllegato.addFromTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().ALLEGATO.HEADER));
				sqlQueryObjectGet_dumpMessaggio_dumpAllegato_dumpHeaderAllegato.addSelectField("id");
				sqlQueryObjectGet_dumpMessaggio_dumpAllegato_dumpHeaderAllegato.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.HEADER.NOME,true));
				sqlQueryObjectGet_dumpMessaggio_dumpAllegato_dumpHeaderAllegato.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.HEADER.VALORE,true));
				sqlQueryObjectGet_dumpMessaggio_dumpAllegato_dumpHeaderAllegato.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ALLEGATO.HEADER.DUMP_TIMESTAMP,true));
				sqlQueryObjectGet_dumpMessaggio_dumpAllegato_dumpHeaderAllegato.addWhereCondition("id_allegato=?");

				// Get dumpMessaggio_dumpAllegato_dumpHeaderAllegato
				java.util.List<Object> dumpMessaggio_dumpAllegato_dumpHeaderAllegato_list = jdbcUtilities.executeQuery(sqlQueryObjectGet_dumpMessaggio_dumpAllegato_dumpHeaderAllegato.createSQLQuery(), jdbcProperties.isShowSql(), DumpMessaggio.model().ALLEGATO.HEADER, this.getDumpMessaggioFetch(),
					new JDBCObject(dumpMessaggio_dumpAllegato.getId(),Long.class));

				if(dumpMessaggio_dumpAllegato_dumpHeaderAllegato_list != null) {
					for (Object dumpMessaggio_dumpAllegato_dumpHeaderAllegato_object: dumpMessaggio_dumpAllegato_dumpHeaderAllegato_list) {
						DumpHeaderAllegato dumpMessaggio_dumpAllegato_dumpHeaderAllegato = (DumpHeaderAllegato) dumpMessaggio_dumpAllegato_dumpHeaderAllegato_object;

						// Bug fix OPPT-466 per gestione empty string as null on oracle
						// Rilasciare il vincolo di not null 'fisico' sul database.
						// Imporlo logicamente, in modo che non siano permessi insert o update con valori null.
						// Dopodichè, se nella get viene recuperato un valore null, deve essere trasformato in stringa vuota.
						if(dumpMessaggio_dumpAllegato_dumpHeaderAllegato.getValore()==null){
							dumpMessaggio_dumpAllegato_dumpHeaderAllegato.setValore("");
						}

						dumpMessaggio_dumpAllegato.addHeader(dumpMessaggio_dumpAllegato_dumpHeaderAllegato);
					}
				}
				dumpMessaggio.addAllegato(dumpMessaggio_dumpAllegato);
			}
		}

		// Object dumpMessaggio_dumpContenuto
		ISQLQueryObject sqlQueryObjectGet_dumpMessaggio_dumpContenuto = sqlQueryObjectGet.newSQLQueryObject();
		sqlQueryObjectGet_dumpMessaggio_dumpContenuto.setANDLogicOperator(true);
		sqlQueryObjectGet_dumpMessaggio_dumpContenuto.addFromTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().CONTENUTO));
		sqlQueryObjectGet_dumpMessaggio_dumpContenuto.addSelectField("id");
		sqlQueryObjectGet_dumpMessaggio_dumpContenuto.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().CONTENUTO.NOME,true));
		sqlQueryObjectGet_dumpMessaggio_dumpContenuto.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().CONTENUTO.VALORE,true));
		sqlQueryObjectGet_dumpMessaggio_dumpContenuto.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().CONTENUTO.VALORE_AS_BYTES,true));
		sqlQueryObjectGet_dumpMessaggio_dumpContenuto.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().CONTENUTO.DUMP_TIMESTAMP,true));
		sqlQueryObjectGet_dumpMessaggio_dumpContenuto.addWhereCondition("id_messaggio=?");

		// Get dumpMessaggio_dumpContenuto
		java.util.List<Object> dumpMessaggio_dumpContenuto_list = jdbcUtilities.executeQuery(sqlQueryObjectGet_dumpMessaggio_dumpContenuto.createSQLQuery(), jdbcProperties.isShowSql(), DumpMessaggio.model().CONTENUTO, this.getDumpMessaggioFetch(),
			new JDBCObject(dumpMessaggio.getId(),Long.class));

		if(dumpMessaggio_dumpContenuto_list != null) {
			for (Object dumpMessaggio_dumpContenuto_object: dumpMessaggio_dumpContenuto_list) {
				DumpContenuto dumpMessaggio_dumpContenuto = (DumpContenuto) dumpMessaggio_dumpContenuto_object;


				dumpMessaggio.addContenuto(dumpMessaggio_dumpContenuto);
			}
		}

		
        return dumpMessaggio;  
	
	} 
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._exists(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	private boolean _exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		boolean existsDumpMessaggio = false;

		sqlQueryObject = sqlQueryObject.newSQLQueryObject();
		sqlQueryObject.setANDLogicOperator(true);

		sqlQueryObject.addFromTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model()));
		sqlQueryObject.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ID_TRANSAZIONE,true));
		sqlQueryObject.addWhereCondition("id=?");


		// Exists dumpMessaggio
		existsDumpMessaggio = jdbcUtilities.exists(sqlQueryObject.createSQLQuery(), jdbcProperties.isShowSql(),
			new JDBCObject(tableId,Long.class));

		
        return existsDumpMessaggio;
	
	}
	
	private void _join(IExpression expression, ISQLQueryObject sqlQueryObject) throws NotImplementedException, ServiceException, Exception{
	
		if(expression.inUseModel(DumpMessaggio.model().ALLEGATO,false)){
			String tableName1 = this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().ALLEGATO);
			String tableName2 = this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model());
			sqlQueryObject.addWhereCondition(tableName1+".id_messaggio="+tableName2+".id");
		}
		if(expression.inUseModel(DumpMessaggio.model().ALLEGATO.HEADER,false)){
			String tableName1 = this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().ALLEGATO.HEADER);
			String tableName2 = this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().ALLEGATO);
			sqlQueryObject.addWhereCondition(tableName1+".id_allegato="+tableName2+".id");
		}
		if(expression.inUseModel(DumpMessaggio.model().CONTENUTO,false)){
			String tableName1 = this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().CONTENUTO);
			String tableName2 = this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model());
			sqlQueryObject.addWhereCondition(tableName1+".id_messaggio="+tableName2+".id");
		}
		if(expression.inUseModel(DumpMessaggio.model().HEADER_TRASPORTO,false)){
			String tableName1 = this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().HEADER_TRASPORTO);
			String tableName2 = this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model());
			sqlQueryObject.addWhereCondition(tableName1+".id_messaggio="+tableName2+".id");
		}
		if(expression.inUseModel(DumpMessaggio.model().MULTIPART_HEADER,false)){
			String tableName1 = this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().MULTIPART_HEADER);
			String tableName2 = this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model());
			sqlQueryObject.addWhereCondition(tableName1+".id_messaggio="+tableName2+".id");
		}
		
		if(expression.inUseModel(DumpMessaggio.model().ALLEGATO.HEADER,false)){
			if(expression.inUseModel(DumpMessaggio.model().ALLEGATO,false)==false){
				sqlQueryObject.addFromTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model().ALLEGATO));
			}
		}
        
	}
	
	protected java.util.List<Object> _getRootTablePrimaryKeyValues(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdDumpMessaggio id) throws NotFoundException, ServiceException, NotImplementedException, Exception{
	    // Identificativi
        java.util.List<Object> rootTableIdValues = new java.util.ArrayList<Object>();
        Long longId = this.findIdDumpMessaggio(jdbcProperties, log, connection, sqlQueryObject.newSQLQueryObject(), id, true);
		rootTableIdValues.add(longId);
        return rootTableIdValues;
	}
	
	protected Map<String, List<IField>> _getMapTableToPKColumn() throws NotImplementedException, Exception{
	
		DumpMessaggioFieldConverter converter = this.getDumpMessaggioFieldConverter();
		Map<String, List<IField>> mapTableToPKColumn = new java.util.Hashtable<String, List<IField>>();
		UtilsTemplate<IField> utilities = new UtilsTemplate<IField>();

		// DumpMessaggio.model()
		mapTableToPKColumn.put(converter.toTable(DumpMessaggio.model()),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(DumpMessaggio.model()))
			));

		// DumpMessaggio.model().ALLEGATO
		mapTableToPKColumn.put(converter.toTable(DumpMessaggio.model().ALLEGATO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(DumpMessaggio.model().ALLEGATO))
			));

		// DumpMessaggio.model().CONTENUTO
		mapTableToPKColumn.put(converter.toTable(DumpMessaggio.model().CONTENUTO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(DumpMessaggio.model().CONTENUTO))
			));

		// DumpMessaggio.model().HEADER_TRASPORTO
		mapTableToPKColumn.put(converter.toTable(DumpMessaggio.model().HEADER_TRASPORTO),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(DumpMessaggio.model().HEADER_TRASPORTO))
			));

        
        return mapTableToPKColumn;		
	}
	
	@Override
	public List<Long> findAllTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression paginatedExpression) throws ServiceException, NotImplementedException, Exception {
		
		List<Long> list = new ArrayList<Long>();

		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFindAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
												this.getDumpMessaggioFieldConverter(), DumpMessaggio.model());
		
		_join(paginatedExpression,sqlQueryObject);
		
		List<Object> listObjects = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.findAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
																			this.getDumpMessaggioFieldConverter(), DumpMessaggio.model(), objectIdClass, listaQuery);
		for(Object object: listObjects) {
			list.add((Long)object);
		}

        return list;
		
	}
	
	@Override
	public long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, Exception {
	
		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFind(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getDumpMessaggioFieldConverter(), DumpMessaggio.model());
		
		_join(expression,sqlQueryObject);

		Object res = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.find(jdbcProperties, log, connection, sqlQueryObject, expression,
														this.getDumpMessaggioFieldConverter(), DumpMessaggio.model(), objectIdClass, listaQuery);
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
	public IdDumpMessaggio findId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();             

		// Object _dumpMessaggio
		sqlQueryObjectGet.addFromTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model()));
		sqlQueryObjectGet.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ID_TRANSAZIONE,true));
		sqlQueryObjectGet.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().TIPO_MESSAGGIO,true));
		sqlQueryObjectGet.addSelectField(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().SERVIZIO_APPLICATIVO_EROGATORE,true));
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addWhereCondition("id=?");

		// Recupero _dumpMessaggio
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_dumpMessaggio = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(tableId,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_dumpMessaggio = new ArrayList<Class<?>>();
		listaFieldIdReturnType_dumpMessaggio.add(String.class);
		listaFieldIdReturnType_dumpMessaggio.add(String.class);
		listaFieldIdReturnType_dumpMessaggio.add(String.class);
		IdDumpMessaggio id_dumpMessaggio = null;
		List<Object> listaFieldId_dumpMessaggio = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_dumpMessaggio, searchParams_dumpMessaggio);
		if(listaFieldId_dumpMessaggio==null || listaFieldId_dumpMessaggio.size()<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		else{
			// set _dumpMessaggio
			id_dumpMessaggio = new IdDumpMessaggio();
			id_dumpMessaggio.setIdTransazione((String)listaFieldId_dumpMessaggio.get(0));
			id_dumpMessaggio.setTipoMessaggio(TipoMessaggio.toEnumConstant((String)listaFieldId_dumpMessaggio.get(1)));
			Object sa = listaFieldId_dumpMessaggio.get(2);
        	if(sa!=null && sa instanceof String) {
        		id_dumpMessaggio.setServizioApplicativoErogatore((String)sa);
        	}
		}
		
		return id_dumpMessaggio;
		
	}

	@Override
	public Long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdDumpMessaggio id, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
	
		return this.findIdDumpMessaggio(jdbcProperties,log,connection,sqlQueryObject,id,throwNotFound);
			
	}
	
	@Override
	public List<List<Object>> nativeQuery(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
											String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException,Exception{
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.nativeQuery(jdbcProperties, log, connection, sqlQueryObject,
																							sql,returnClassTypes,param);
														
	}
	
	protected Long findIdDumpMessaggio(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdDumpMessaggio id, boolean throwNotFound) throws NotFoundException, ServiceException, NotImplementedException, Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();

		// Object _dumpMessaggio
		sqlQueryObjectGet.addFromTable(this.getDumpMessaggioFieldConverter().toTable(DumpMessaggio.model()));
		sqlQueryObjectGet.addSelectField("id");
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.setSelectDistinct(true);
		sqlQueryObjectGet.addWhereCondition(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().ID_TRANSAZIONE,true)+"=?");
		sqlQueryObjectGet.addWhereCondition(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().TIPO_MESSAGGIO,true)+"=?");
		if(id.getServizioApplicativoErogatore() == null) {
			sqlQueryObjectGet.addWhereIsNotNullCondition(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().SERVIZIO_APPLICATIVO_EROGATORE,true));
		}
		else {
			sqlQueryObjectGet.addWhereCondition(this.getDumpMessaggioFieldConverter().toColumn(DumpMessaggio.model().SERVIZIO_APPLICATIVO_EROGATORE,true)+"=?");
		}
		
		// Recupero _dumpMessaggio
		List<org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject> searchParams_dumpMessaggio_list = new ArrayList<>();
		searchParams_dumpMessaggio_list.add(new JDBCObject(id.getIdTransazione(), String.class));
		searchParams_dumpMessaggio_list.add(new JDBCObject(id.get_value_tipoMessaggio(), String.class));
		if(id.getServizioApplicativoErogatore() != null) {
			searchParams_dumpMessaggio_list.add(new JDBCObject(id.getServizioApplicativoErogatore(), String.class));
		}
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_dumpMessaggio = searchParams_dumpMessaggio_list.toArray(new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [1]);

		Long id_dumpMessaggio = null;
		try{
			id_dumpMessaggio = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
						Long.class, searchParams_dumpMessaggio);
		}catch(NotFoundException notFound){
			if(throwNotFound){
				throw new NotFoundException(notFound);
			}
		}
		if(id_dumpMessaggio==null || id_dumpMessaggio<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		
		return id_dumpMessaggio;
	}
}
