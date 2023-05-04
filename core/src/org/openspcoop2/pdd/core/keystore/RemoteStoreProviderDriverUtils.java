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

package org.openspcoop2.pdd.core.keystore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.certificate.remote.RemoteStoreConfig;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject;
import org.openspcoop2.utils.jdbc.IJDBCAdapter;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKey;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKeyJDBCType;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKeyObject;
import org.openspcoop2.utils.jdbc.JDBCAdapterFactory;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * RemoteStoreProviderDriverUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RemoteStoreProviderDriverUtils {
	
	private RemoteStoreProviderDriverUtils() {}

	public static long registerIfNotExistsRemoteStore(DriverConfigurazioneDB driverConfigurazioneDB, RemoteStoreConfig remoteStoreConfig) throws KeystoreException {
		long idRemoteStore = getIdRemoteStore(driverConfigurazioneDB, remoteStoreConfig);
		if(idRemoteStore<=0) {
			return createRemoteStore(driverConfigurazioneDB, remoteStoreConfig);
		}
		return idRemoteStore;
	}
	
	public static long createRemoteStore(DriverConfigurazioneDB driverConfigurazioneDB, RemoteStoreConfig remoteStoreConfig) throws KeystoreException {
		
		Connection con = null;
		try {
			con = driverConfigurazioneDB.getConnection("createRemoteStore");
			
			String remoteStoreName = RemoteStoreProviderDriver.getRemoteStoreConfigName(remoteStoreConfig);
			
			List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<>();
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome", remoteStoreName, InsertAndGeneratedKeyJDBCType.STRING) );
			
			long idRemoteStore = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(driverConfigurazioneDB.getTipoDB()), 
					new CustomKeyGeneratorObject(CostantiDB.REMOTE_STORE, CostantiDB.REMOTE_STORE_COLUMN_ID, 
							CostantiDB.REMOTE_STORE_SEQUENCE, CostantiDB.REMOTE_STORE_TABLE_FOR_ID),
					listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
			if(idRemoteStore<=0){
				throw new KeystoreException("ID (RemoteStore) autoincrementale non ottenuto");
			}
			return idRemoteStore;
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			driverConfigurazioneDB.releaseConnection(con);
		}
		
	}
	
	public static long getIdRemoteStore(DriverConfigurazioneDB driverConfigurazioneDB, RemoteStoreConfig remoteStoreConfig) throws KeystoreException {
		
		Connection con = null;
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		long idRemoteStore = -1;
		try {
			con = driverConfigurazioneDB.getConnection("getIdRemoteStore");
			
			String remoteStoreName = RemoteStoreProviderDriver.getRemoteStoreConfigName(remoteStoreConfig);
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(driverConfigurazioneDB.getTipoDB());
			sqlQueryObject.addFromTable(CostantiDB.REMOTE_STORE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("nome=?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			selectStmt = con.prepareStatement(sqlQuery);
			selectStmt.setString(1, remoteStoreName);
			selectRS = selectStmt.executeQuery();
			if(selectRS.next()) {
				idRemoteStore = selectRS.getLong("id");
			}
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			
			JDBCUtilities.closeResources(selectRS, selectStmt);
			
			driverConfigurazioneDB.releaseConnection(con);
		}
		
		return idRemoteStore;
	}
	
	
	public static int addRemoteStoreKey(DriverConfigurazioneDB driverConfigurazioneDB, long idStore, String kid, byte[] key) throws KeystoreException {
		Connection con = null;
		PreparedStatement updateStmt = null;
		try {
			con = driverConfigurazioneDB.getConnection("addRemoteStoreKey");
			
			checkParams(idStore, kid, key);
			
			IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(driverConfigurazioneDB.getTipoDB());
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(driverConfigurazioneDB.getTipoDB());
			sqlQueryObject.addInsertTable(CostantiDB.REMOTE_STORE_KEY);
			sqlQueryObject.addInsertField("id_remote_store", "?");
			sqlQueryObject.addInsertField("kid", "?");
			sqlQueryObject.addInsertField("key", "?");
			sqlQueryObject.addInsertField("data_registrazione", "?");
			sqlQueryObject.addInsertField("data_aggiornamento", "?");
			String updateQuery = sqlQueryObject.createSQLInsert();
			updateStmt = con.prepareStatement(updateQuery);
			int index = 1;
			updateStmt.setLong(index++, idStore);
			updateStmt.setString(index++, kid);
			jdbcAdapter.setBinaryData(updateStmt, index++, key);
			Timestamp now = DateManager.getTimestamp();
			updateStmt.setTimestamp(index++, now);
			updateStmt.setTimestamp(index++, now);
			int rows = updateStmt.executeUpdate();
			updateStmt.close();
			return rows;
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			
			JDBCUtilities.closeResources(updateStmt);
			
			driverConfigurazioneDB.releaseConnection(con);
		}
	}
	
	public static int updateRemoteStoreKey(DriverConfigurazioneDB driverConfigurazioneDB, long idStore, String kid, byte[] key) throws KeystoreException {
		Connection con = null;
		PreparedStatement updateStmt = null;
		try {
			con = driverConfigurazioneDB.getConnection("updateRemoteStoreKey");
			
			checkParams(idStore, kid, key);
			
			IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(driverConfigurazioneDB.getTipoDB());
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(driverConfigurazioneDB.getTipoDB());
			sqlQueryObject.addUpdateTable(CostantiDB.REMOTE_STORE_KEY);
			sqlQueryObject.addUpdateField("key", "?");
			sqlQueryObject.addInsertField("data_aggiornamento", "?");
			addWhereKidConditions(sqlQueryObject);
			sqlQueryObject.setANDLogicOperator(true);
			String updateQuery = sqlQueryObject.createSQLUpdate();
			updateStmt = con.prepareStatement(updateQuery);
			int index = 1;
			jdbcAdapter.setBinaryData(updateStmt, index++, key);
			Timestamp now = DateManager.getTimestamp();
			updateStmt.setTimestamp(index++, now);
			updateStmt.setLong(index++, idStore);
			updateStmt.setString(index++, kid);
			int rows = updateStmt.executeUpdate();
			updateStmt.close();
			return rows;
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			
			JDBCUtilities.closeResources(updateStmt);
			
			driverConfigurazioneDB.releaseConnection(con);
		}
	}
	
	public static int deleteRemoteStoreKey(DriverConfigurazioneDB driverConfigurazioneDB, long idStore, String kid) throws KeystoreException {
		Connection con = null;
		PreparedStatement updateStmt = null;
		try {
			con = driverConfigurazioneDB.getConnection("deleteRemoteStoreKey");
			
			checkParams(idStore, kid);
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(driverConfigurazioneDB.getTipoDB());
			sqlQueryObject.addDeleteTable(CostantiDB.REMOTE_STORE_KEY);
			addWhereKidConditions(sqlQueryObject);
			sqlQueryObject.setANDLogicOperator(true);
			String updateQuery = sqlQueryObject.createSQLUpdate();
			updateStmt = con.prepareStatement(updateQuery);
			updateStmt.setLong(1, idStore);
			updateStmt.setString(2, kid);
			int rows = updateStmt.executeUpdate();
			updateStmt.close();
			return rows;
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			
			JDBCUtilities.closeResources(updateStmt);
			
			driverConfigurazioneDB.releaseConnection(con);
		}
	}
	
	public static byte[] getRemoteStoreKey(DriverConfigurazioneDB driverConfigurazioneDB, long idStore, String kid) throws KeystoreException,KeystoreNotFoundException {
		
		Connection con = null;
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		try {
			con = driverConfigurazioneDB.getConnection("getRemoteStoreKey");
			
			checkParams(idStore, kid);
			
			IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(driverConfigurazioneDB.getTipoDB());
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(driverConfigurazioneDB.getTipoDB());
			sqlQueryObject.addFromTable(CostantiDB.REMOTE_STORE_KEY);
			sqlQueryObject.addSelectField("key");
			addWhereKidConditions(sqlQueryObject);
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			selectStmt = con.prepareStatement(sqlQuery);
			selectStmt.setLong(1, idStore);
			selectStmt.setString(2, kid);
			selectRS = selectStmt.executeQuery();
			if(selectRS.next()) {
				return jdbcAdapter.getBinaryData(selectRS, "key");
			}
			throw new KeystoreNotFoundException("Key with kid '"+kid+"' not found");
		}
		catch(KeystoreNotFoundException e) {
			throw e;
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			
			JDBCUtilities.closeResources(selectRS, selectStmt);
			
			driverConfigurazioneDB.releaseConnection(con);
		}
		
	}
	
	private static void checkParams(long idStore, String kid, byte[] key) throws KeystoreException {
		checkParams(idStore, kid);
		
		if(key==null || key.length<=0) {
			throw new KeystoreException("Key undefined");
		}
	}
	private static void checkParams(long idStore, String kid) throws KeystoreException {
		if(idStore<=0) {
			throw new KeystoreException("IdStore undefined");
		}
		if(kid==null) {
			throw new KeystoreException("Kid undefined");
		}
	}
	
	private static void addWhereKidConditions(ISQLQueryObject sqlQueryObject) throws SQLQueryObjectException {
		sqlQueryObject.addWhereCondition("id_remote_store=?");
		sqlQueryObject.addWhereCondition("kid=?");
	}
}
