/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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


package org.openspcoop2.utils.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.UtilsException;

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
	public static void closePreparedStatement(Hashtable<String,PreparedStatement> tablePstmt,Logger log){
		if(tablePstmt!=null && tablePstmt.keys().hasMoreElements()){
			java.util.ArrayList<String> listKeys = java.util.Collections.list(tablePstmt.keys());
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
	public static void executePreparedStatement(Hashtable<String,PreparedStatement> tablePstmt) throws UtilsException{
		if(tablePstmt!=null && tablePstmt.keys().hasMoreElements()){
			java.util.ArrayList<String> listKeys = java.util.Collections.list(tablePstmt.keys());
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
	public static void addPreparedStatement(Hashtable<String,PreparedStatement> pstmtSorgente, 
			Hashtable<String,PreparedStatement> pstmtDestinazione,Logger log) throws UtilsException{ 
		Enumeration<String> keys = pstmtSorgente.keys();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
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
		return transactionIsolationLevel == SQL_SERVER_TRANSACTION_SNAPSHOT;
	}
	
	public static void setTransactionIsolationSerializable(String tipoDatabase,Connection connection) throws SQLException{
		setTransactionIsolationSerializable(TipiDatabase.toEnumConstant(tipoDatabase), connection);
	}
	public static void setTransactionIsolationSerializable(TipiDatabase tipoDatabase,Connection connection) throws SQLException{
		if(tipoDatabase!=null && TipiDatabase.SQLSERVER.equals(tipoDatabase)){ 
			connection.setTransactionIsolation(SQL_SERVER_TRANSACTION_SNAPSHOT); //4096 corresponds to SQLServerConnection.TRANSACTION_SNAPSHOT }
		}
		else{ 
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); 
		} 
	}
}
