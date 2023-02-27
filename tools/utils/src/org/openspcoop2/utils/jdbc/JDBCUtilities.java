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


package org.openspcoop2.utils.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

/**
 * JDBC Utilities
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class JDBCUtilities {

	/**
	 * Chiude tutte le PreparedStatement presenti nella tabella hash.
	 *
	 * @param tablePstmt Tabella Hash contenente PreparedStatement da chiudere.
	 * 
	 */
	public static void closePreparedStatement(Map<String,PreparedStatement> tablePstmt,Logger log){
		if(tablePstmt!=null && !tablePstmt.isEmpty()){
			java.util.ArrayList<String> listKeys = new ArrayList<String>();
			for (String key : tablePstmt.keySet()) {
				listKeys.add(key);
			}
			java.util.Collections.sort(listKeys);
			for(int i=0; i<listKeys.size(); i++ ) {
				String key = listKeys.get(i);
				PreparedStatement pstmt = tablePstmt.get(key);
				try{
					pstmt.close();
				}catch(Exception e){
					log.debug("Utilities.closePreparedStatement error: Riscontrato errore durante la chiusura della PreparedStatement ["+key+"]: "+e);
				}
				tablePstmt.remove(key);
			}
		}
	}


	/**
	 * Esegue e Chiude tutte le PreparedStatement presenti nella tabella hash.
	 *
	 * @param tablePstmt Tabella Hash contenente PreparedStatement da eseguire e chiudere.
	 * 
	 */
	public static void executePreparedStatement(Map<String,PreparedStatement> tablePstmt) throws UtilsException{
		if(tablePstmt!=null && !tablePstmt.isEmpty()){
			java.util.ArrayList<String> listKeys = new ArrayList<String>();
			for (String key : tablePstmt.keySet()) {
				listKeys.add(key);
			}
			java.util.Collections.sort(listKeys);

//			System.out.println("---------- ("+listKeys.size()+") ------------");
//			for(int i=0; i<listKeys.size(); i++ ) {
//				String key = listKeys.get(i);
//				System.out.println("Pos["+i+"]: "+key);
//			}
			
			for(int i=0; i<listKeys.size(); i++ ) {
				String key = listKeys.get(i);
				PreparedStatement pstmt = tablePstmt.get(key);
				//System.out.println("EXECUTE["+i+"]: "+key);
				try{
					pstmt.execute();
				}catch(Exception e){
					//System.out.println("ERRORE: "+key);
					throw new UtilsException("Utilities.executePreparedStatement error: Riscontrato errore durante l'esecuzione della PreparedStatement ["+key+"]: "+e,e);
				}
				try{
					pstmt.close();
				}catch(Exception e){
					//System.out.println("ERRORE: "+key);
					throw new UtilsException("Utilities.executePreparedStatement error: Riscontrato errore durante la chiusura della PreparedStatement ["+key+"]: "+e,e);
				}
				tablePstmt.remove(key);
			}
		}
	}

	/**
	 * Aggiunge prepared Statement
	 * 
	 * @param pstmtSorgente
	 * @param pstmtDestinazione
	 * @param log
	 */
	public static void addPreparedStatement(Map<String,PreparedStatement> pstmtSorgente, 
			Map<String,PreparedStatement> pstmtDestinazione,Logger log) throws UtilsException{ 
		if(pstmtSorgente!=null && !pstmtSorgente.isEmpty()){
			for (String key : pstmtSorgente.keySet()) {
				if(pstmtDestinazione.containsKey(key)==false){
					pstmtDestinazione.put(key,pstmtSorgente.get(key));
				}else{
					//log.debug("Prepared Statement ["+key+"] gia' presente");
					try{
						PreparedStatement pstmt = pstmtSorgente.get(key);
						pstmt.close();
					}catch(Exception e){
						throw new UtilsException("Utilities.closePreparedStatementGiaPresente error: Riscontrato errore durante la chiusura della PreparedStatement ["+key+"]: "+e,e);
					}
				}
			}
		}
	}
	
	
	
	
	public static void setSQLStringValue(PreparedStatement pstmt,int index,String value) throws SQLException{
		if(value!=null && ("".equals(value)==false))
			pstmt.setString(index,value);
		else
			pstmt.setString(index,null);
	}
	
	
	private static int SQL_SERVER_TRANSACTION_SNAPSHOT = 4096;
	
	public static boolean isTransactionIsolationNone(int transactionIsolationLevel){
		return transactionIsolationLevel == java.sql.Connection.TRANSACTION_NONE;
	}
	public static boolean isTransactionIsolationReadUncommitted(int transactionIsolationLevel){
		return transactionIsolationLevel == java.sql.Connection.TRANSACTION_READ_UNCOMMITTED;
	}
	public static boolean isTransactionIsolationReadCommitted(int transactionIsolationLevel){
		return transactionIsolationLevel == java.sql.Connection.TRANSACTION_READ_COMMITTED;
	}
	public static boolean isTransactionIsolationRepeatableRead(int transactionIsolationLevel){
		return transactionIsolationLevel == java.sql.Connection.TRANSACTION_REPEATABLE_READ;
	}
	public static boolean isTransactionIsolationSerializable(int transactionIsolationLevel){
		return transactionIsolationLevel == java.sql.Connection.TRANSACTION_SERIALIZABLE;
	}
	public static boolean isTransactionIsolationSqlServerSnapshot(int transactionIsolationLevel){
		return transactionIsolationLevel == JDBCUtilities.SQL_SERVER_TRANSACTION_SNAPSHOT;
	}
	public static boolean isTransactionIsolationSerializable(int transactionIsolationLevel,TipiDatabase tipoDatabase){
		if(tipoDatabase!=null && TipiDatabase.SQLSERVER.equals(tipoDatabase)){ 
			return JDBCUtilities.isTransactionIsolationSqlServerSnapshot(transactionIsolationLevel);
		}
		else {
			return JDBCUtilities.isTransactionIsolationSerializable(transactionIsolationLevel);
		}
	}
	
	public static void setTransactionIsolationSerializable(String tipoDatabase,Connection connection) throws SQLException{
		JDBCUtilities.setTransactionIsolationSerializable(TipiDatabase.toEnumConstant(tipoDatabase), connection);
	}
	public static void setTransactionIsolationSerializable(TipiDatabase tipoDatabase,Connection connection) throws SQLException{
		if(tipoDatabase!=null && TipiDatabase.SQLSERVER.equals(tipoDatabase)){ 
			connection.setTransactionIsolation(JDBCUtilities.SQL_SERVER_TRANSACTION_SNAPSHOT); //4096 corresponds to SQLServerConnection.TRANSACTION_SNAPSHOT }
		}
		else{ 
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); 
		} 
	}
	
	
	
	
	
	public static void addInformazioniDatabaseFromMetaData(Connection c, StringBuilder bf) throws SQLException{
		
		try{
			DatabaseMetaData dbMetaDati = c.getMetaData();
			if(dbMetaDati!=null){

				if(bf.length()>0){
					bf.append("\n");
				}

				try {
					String productName = dbMetaDati.getDatabaseProductName();
					bf.append("DatabaseProductName: "+productName);
					bf.append("\n");
				} catch (SQLException e) {
				}

				try {
					String productVersion = dbMetaDati.getDatabaseProductVersion();
					bf.append("DatabaseProductVersion: "+productVersion);
					bf.append("\n");
				} catch (SQLException e) {
				}

				try {
					int v = dbMetaDati.getDatabaseMajorVersion();
					bf.append("DatabaseMajorVersion: "+v);
					bf.append("\n");
				} catch (SQLException e) {
				}

				try {
					int v = dbMetaDati.getDatabaseMinorVersion();
					bf.append("DatabaseMinorVersion: "+v);
					bf.append("\n");
				} catch (SQLException e) {
				}

				try {
					String driverName = dbMetaDati.getDriverName();
					bf.append("DriverName: "+driverName);
					bf.append("\n");
				} catch (SQLException e) {
				}

				try {
					String productVersion = dbMetaDati.getDriverVersion();
					bf.append("DriverVersion: "+productVersion);
					bf.append("\n");
				} catch (SQLException e) {
				}

				int v = dbMetaDati.getDriverMajorVersion();
				bf.append("DriverMajorVersion: "+v);
				bf.append("\n");
				
				v = dbMetaDati.getDriverMinorVersion();
				bf.append("DriverMinorVersion: "+v);
				bf.append("\n");

				try {
					v = dbMetaDati.getJDBCMajorVersion();
					bf.append("JDBCMajorVersion: "+v);
					bf.append("\n");
				} catch (SQLException e) {
				}

				try {
					v = dbMetaDati.getJDBCMinorVersion();
					bf.append("JDBCMinorVersion: "+v);
					bf.append("\n");
				} catch (SQLException e) {
				}

				try {
					String username = dbMetaDati.getUserName();
					bf.append("Username: "+username);
					bf.append("\n");
				} catch (SQLException e) {
				}

				try {
					ResultSet catalogs = dbMetaDati.getCatalogs();
					int size = 0;
					while (catalogs.next()) {
						size++;
					}
					
					catalogs = dbMetaDati.getCatalogs();
					int index = 0;
					while (catalogs.next()) {
						if(size==1){
							bf.append("Catalog: " + catalogs.getString(1) );
						}
						else{
							bf.append("Catalogs["+index+"]: " + catalogs.getString(1) );
						}
						bf.append("\n");
						index++;
					}
					catalogs.close();
				} catch (SQLException e) {
				}

			}

		}finally{
		}

	}
	
	public static void closeResources(ResultSet rs, PreparedStatement stm) {
		try{
			if(rs!=null) 
				rs.close();
		}catch (Exception e) {
			//ignore
		}
		try{
			if(stm!=null) 
				stm.close();
		}catch (Exception e) {
			//ignore
		}
	}
	public static void closeResources(PreparedStatement stm) {
		try{
			if(stm!=null) 
				stm.close();
		}catch (Exception e) {
			//ignore
		}
	}
	public static void closeResources(Statement stm) {
		try{
			if(stm!=null) 
				stm.close();
		}catch (Exception e) {
			//ignore
		}
	}
}
