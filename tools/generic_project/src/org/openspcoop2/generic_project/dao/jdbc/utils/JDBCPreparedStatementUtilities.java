/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.generic_project.dao.jdbc.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IKeyGenerator;
import org.openspcoop2.utils.jdbc.IKeyGeneratorObject;
import org.openspcoop2.utils.jdbc.JDBCAdapterException;
import org.openspcoop2.utils.jdbc.KeyGeneratorException;
import org.openspcoop2.utils.jdbc.KeyGeneratorFactory;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * JDBCPreparedStatementUtilities
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCPreparedStatementUtilities {

	private Logger log;
	private Connection connection;
	private JDBCSqlLogger sqlLogger;
	private TipiDatabase tipoDatabase = null;
	private JDBCParameterUtilities jdbcParameterUtilities = null;
	
	public JDBCPreparedStatementUtilities(TipiDatabase tipoDatabase,Logger log,Connection connection) throws SQLQueryObjectException, JDBCAdapterException{
		this.log = log;
		this.connection = connection;
		this.sqlLogger = new JDBCSqlLogger(this.log);
		this.tipoDatabase = tipoDatabase;
		this.jdbcParameterUtilities = new JDBCParameterUtilities(this.tipoDatabase);
	}
	
	public long insertAndReturnGeneratedKey(ISQLQueryObject sqlQueryObject,IKeyGeneratorObject object,boolean showSql,JDBCObject ... params) throws KeyGeneratorException{

		PreparedStatement pstmt = null;
		try{
			
			// KeyGenerator
			IKeyGenerator keyGenerator = KeyGeneratorFactory.createKeyGeneratorFactory(this.tipoDatabase.getNome(), this.connection, object);

			// Parametri di insert
			List<JDBCObject> p = new ArrayList<JDBCObject>();
			if(params!=null){
				for (int i = 0; i < params.length; i++) {
					p.add(params[i]);
				}
			}
			if(keyGenerator.isReturnGeneratedKeySupported()==false){
				JDBCObject jdbcObject = new JDBCObject(keyGenerator.generateKey(),Long.class);
				p.add(jdbcObject);
			}
			JDBCObject[]paramsWithId = null;
			if(p.size()>0)
				paramsWithId = p.toArray(new JDBCObject[1]);
			
			// Query di insert
			sqlQueryObject.addInsertTable(object.getTable());
			if(keyGenerator.isReturnGeneratedKeySupported()==false){
				sqlQueryObject.addInsertField(keyGenerator.getColunmKeyName(), "?");
			}
			String insertString = sqlQueryObject.createSQLInsert();
			if(showSql){
				this.sqlLogger.infoSql(insertString, paramsWithId);
			}
			
			// Eseguo Prepared Statement
			if(keyGenerator.isReturnGeneratedKeySupported()){
				pstmt = this.connection.prepareStatement(insertString,Statement.RETURN_GENERATED_KEYS);
			}else{
				pstmt = this.connection.prepareStatement(insertString);
			}
			this.jdbcParameterUtilities.setParameters(pstmt, paramsWithId);
			pstmt.executeUpdate();

			
			// Ritorno id generato
			return keyGenerator.getReturnGeneratedKey(pstmt);

		}catch(Exception e){
			throw new KeyGeneratorException ("insertAndReturnGeneratedKey failed: "+e.getMessage(),e);
		}finally{
			try{
				if(pstmt!=null)
					pstmt.close();
			}catch(Exception eClose){}
		}
	}
	
	public boolean execute(String sql,boolean showSql,JDBCObject ... params ) throws ServiceException{
		
		PreparedStatement pstmt = null;
		try{
			
			if(showSql)
				this.sqlLogger.infoSql(sql, params);
			
			pstmt = this.connection.prepareStatement(sql);
			this.jdbcParameterUtilities.setParameters(pstmt, params);
			
			return pstmt.execute();
			
		}catch(Exception e){
			throw new ServiceException(e.getMessage(),e);
		}
		finally{
			try{
				if(pstmt!=null){
					pstmt.close();
				}
			}catch(Exception eClose){}
		}
	}
	
	public int executeUpdate(String sql,boolean showSql,JDBCObject ... params ) throws ServiceException{
		
		PreparedStatement pstmt = null;
		try{
			
			if(showSql)
				this.sqlLogger.infoSql(sql, params);
			
			pstmt = this.connection.prepareStatement(sql);
			this.jdbcParameterUtilities.setParameters(pstmt, params);
			
			return pstmt.executeUpdate();
			
		}catch(Exception e){
			throw new ServiceException(e.getMessage(),e);
		}
		finally{
			try{
				if(pstmt!=null){
					pstmt.close();
				}
			}catch(Exception eClose){}
		}
	}
	
	public Object executeQuerySingleResult(String sql,boolean showSql,IModel<?> model, IJDBCFetch fetch, JDBCObject ... params ) throws ServiceException, MultipleResultException, NotFoundException{
		List<Object> list = this.executeQuery(sql, showSql, model, fetch, params);
		if(list.size()==1){
			Object o = list.get(0);
			if(o!=null){
				return o;
			}
			else{
				throw new NotFoundException("Not found");
			}
		}
		else if(list.size()<=0){
			throw new NotFoundException("Not found");
		}
		else{
			throw new MultipleResultException("More than one result found (result: "+list.size()+")");
		}
	}
	
	public List<Object> executeQuery(String sql,boolean showSql,IModel<?> model, IJDBCFetch fetch, JDBCObject ... params ) throws ServiceException{
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Object> lista = new ArrayList<Object>();
		try{
			
			if(showSql)
				this.sqlLogger.infoSql(sql, params);
			
			pstmt = this.connection.prepareStatement(sql);
			this.jdbcParameterUtilities.setParameters(pstmt, params);
			
			rs =  pstmt.executeQuery();
			while(rs.next()){
				lista.add(fetch.fetch(this.tipoDatabase,model,rs));
			}
			return lista;
			
		}catch(Exception e){
			throw new ServiceException(e.getMessage(),e);
		}
		finally{
			try{
				if(rs!=null){
					rs.close();
				}
			}catch(Exception eClose){}
			try{
				if(pstmt!=null){
					pstmt.close();
				}
			}catch(Exception eClose){}
		}
	}
	
	public Object executeQuerySingleResult(String sql,boolean showSql,Class<?> returnType,JDBCObject ... params ) throws NotFoundException, ServiceException, MultipleResultException{
		List<Object> list = this.executeQuery(sql, showSql, returnType, params);
		if(list.size()==1){
			Object o = list.get(0);
			if(o!=null){
				return o;
			}
			else{
				throw new NotFoundException("Not found");
			}
		}
		else if(list.size()<=0){
			throw new NotFoundException("Not found");
		}
		else{
			throw new MultipleResultException("More than one result found (result: "+list.size()+")");
		}
	}
	public List<Object> executeQuery(String sql,boolean showSql,Class<?> returnType,JDBCObject ... params ) throws ServiceException{
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Object> lista = new ArrayList<Object>();
		try{
			
			if(showSql)
				this.sqlLogger.infoSql(sql, params);
			
			pstmt = this.connection.prepareStatement(sql);
			this.jdbcParameterUtilities.setParameters(pstmt, params);
			
			rs =  pstmt.executeQuery();
			while(rs.next()){
				lista.add(this.jdbcParameterUtilities.readParameter(rs, 1, returnType));
			}
			return lista;
			
		}catch(Exception e){
			throw new ServiceException(e.getMessage(),e);
		}
		finally{
			try{
				if(rs!=null){
					rs.close();
				}
			}catch(Exception eClose){}
			try{
				if(pstmt!=null){
					pstmt.close();
				}
			}catch(Exception eClose){}
		}
	}
	
	public List<Object> executeQuerySingleResult(String sql,boolean showSql,List<Class<?>> returnType,JDBCObject ... params ) throws ServiceException, MultipleResultException{
		List<List<Object>> list = this.executeQuery(sql, showSql, returnType, params);
		if(list.size()==1){
			return list.get(0);
		}
		else if(list.size()<=0){
			return new ArrayList<Object>();
		}
		else{
			throw new MultipleResultException("More than one result found (result: "+list.size()+")");
		}
	}
	public List<List<Object>> executeQuery(String sql,boolean showSql,List<Class<?>> returnType,JDBCObject ... params ) throws ServiceException{
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<List<Object>> lista = new ArrayList<List<Object>>();
		try{
			
			if(showSql)
				this.sqlLogger.infoSql(sql, params);
			
			pstmt = this.connection.prepareStatement(sql);
			this.jdbcParameterUtilities.setParameters(pstmt, params);
			
			rs =  pstmt.executeQuery();
			while(rs.next()){
				List<Object> result = new ArrayList<Object>();
				for (int i = 0; i < returnType.size(); i++) {
					result.add(this.jdbcParameterUtilities.readParameter(rs, (i+1), returnType.get(i)));	
				}
				lista.add(result);
			}
			return lista;
			
		}catch(Exception e){
			throw new ServiceException(e.getMessage(),e);
		}
		finally{
			try{
				if(rs!=null){
					rs.close();
				}
			}catch(Exception eClose){}
			try{
				if(pstmt!=null){
					pstmt.close();
				}
			}catch(Exception eClose){}
		}
	}
	
	public boolean exists(String sql,boolean showSql,JDBCObject ... params ) throws ServiceException{
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			
			if(showSql)
				this.sqlLogger.infoSql(sql, params);
			
			pstmt = this.connection.prepareStatement(sql);
			this.jdbcParameterUtilities.setParameters(pstmt, params);
			
			rs =  pstmt.executeQuery();
			return rs.next();
			
		}catch(Exception e){
			throw new ServiceException(e.getMessage(),e);
		}
		finally{
			try{
				if(rs!=null){
					rs.close();
				}
			}catch(Exception eClose){}
			try{
				if(pstmt!=null){
					pstmt.close();
				}
			}catch(Exception eClose){}
		}
	}
	
	public boolean deleteById(String sql,boolean showSql,JDBCObject ... params ) throws ServiceException{
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			
			if(showSql)
				this.sqlLogger.infoSql(sql, params);
			
			pstmt = this.connection.prepareStatement(sql);
			this.jdbcParameterUtilities.setParameters(pstmt, params);
			
			rs =  pstmt.executeQuery();
			return rs.next();
			
		}catch(Exception e){
			throw new ServiceException(e.getMessage(),e);
		}
		finally{
			try{
				if(rs!=null){
					rs.close();
				}
			}catch(Exception eClose){}
			try{
				if(pstmt!=null){
					pstmt.close();
				}
			}catch(Exception eClose){}
		}
	}
	
	public long count(String sql,boolean showSql,JDBCObject ... params ) throws ServiceException{
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			
			if(showSql)
				this.sqlLogger.infoSql(sql, params);
			
			pstmt = this.connection.prepareStatement(sql);
			this.jdbcParameterUtilities.setParameters(pstmt, params);
			
			rs =  pstmt.executeQuery();
			if(rs.next())
				return rs.getLong(1);
			else
				return 0;
			
		}catch(Exception e){
			throw new ServiceException(e.getMessage(),e);
		}
		finally{
			try{
				if(rs!=null){
					rs.close();
				}
			}catch(Exception eClose){}
			try{
				if(pstmt!=null){
					pstmt.close();
				}
			}catch(Exception eClose){}
		}
	}
	
}
