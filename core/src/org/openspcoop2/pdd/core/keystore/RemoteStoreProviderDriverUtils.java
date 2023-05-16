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
import org.openspcoop2.utils.date.DateUtils;
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

	private static final String COLUMN_ID = "id";
	private static final String COLUMN_NOME = "nome";
	private static final String COLUMN_DATA_REGISTRAZIONE = "data_registrazione";
	private static final String COLUMN_DATA_AGGIORNAMENTO = "data_aggiornamento";
	private static final String COLUMN_LAST_EVENT = "last_event";
	
	private static final String COLUMN_ID_REMOTE_STORE = "id_remote_store";
	private static final String COLUMN_KID = "kid";
	private static final String COLUMN_KEY = "content_key";
	
	public static long registerIfNotExistsRemoteStore(DriverConfigurazioneDB driverConfigurazioneDB, RemoteStoreConfig remoteStoreConfig) throws KeystoreException {
		long idRemoteStore = getIdRemoteStore(driverConfigurazioneDB, remoteStoreConfig);
		if(idRemoteStore<=0) {
			return createRemoteStore(driverConfigurazioneDB, remoteStoreConfig);
		}
		return idRemoteStore;
	}
	public static long registerIfNotExistsRemoteStore(Connection con, String tipoDatabase, RemoteStoreConfig remoteStoreConfig) throws KeystoreException {
		long idRemoteStore = getIdRemoteStore(con, tipoDatabase, remoteStoreConfig);
		if(idRemoteStore<=0) {
			return createRemoteStore(con, tipoDatabase, remoteStoreConfig);
		}
		return idRemoteStore;
	}
	
	public static long createRemoteStore(DriverConfigurazioneDB driverConfigurazioneDB, RemoteStoreConfig remoteStoreConfig) throws KeystoreException {
		
		Connection con = null;
		try {
			con = driverConfigurazioneDB.getConnection("createRemoteStore", false);
			return createRemoteStore(con, driverConfigurazioneDB.getTipoDB(), remoteStoreConfig);
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			driverConfigurazioneDB.releaseConnection(con);
		}
		
	}
	public static long createRemoteStore(Connection con, String tipoDatabase, RemoteStoreConfig remoteStoreConfig) throws KeystoreException {
		
		try {
			String remoteStoreName = RemoteStoreProviderDriver.getRemoteStoreConfigName(remoteStoreConfig);
			
			Timestamp now = DateManager.getTimestamp();
			
			List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<>();
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(COLUMN_NOME, remoteStoreName, InsertAndGeneratedKeyJDBCType.STRING) );
			listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(COLUMN_DATA_AGGIORNAMENTO, now, InsertAndGeneratedKeyJDBCType.TIMESTAMP) );
			
			long idRemoteStore = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(tipoDatabase), 
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
		
	}
	
	public static long getIdRemoteStore(DriverConfigurazioneDB driverConfigurazioneDB, RemoteStoreConfig remoteStoreConfig) throws KeystoreException {
		Connection con = null;
		try {
			con = driverConfigurazioneDB.getConnection("getIdRemoteStore", false);
			return getIdRemoteStore(con, driverConfigurazioneDB.getTipoDB(), remoteStoreConfig);
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			driverConfigurazioneDB.releaseConnection(con);
		}
	}
	public static long getIdRemoteStore(Connection con, String tipoDatabase, RemoteStoreConfig remoteStoreConfig) throws KeystoreException {
		
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		long idRemoteStore = -1;
		try {
			String remoteStoreName = RemoteStoreProviderDriver.getRemoteStoreConfigName(remoteStoreConfig);
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.REMOTE_STORE);
			sqlQueryObject.addSelectField(COLUMN_ID);
			sqlQueryObject.addWhereCondition(COLUMN_NOME+"=?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			selectStmt = con.prepareStatement(sqlQuery);
			selectStmt.setString(1, remoteStoreName);
			selectRS = selectStmt.executeQuery();
			if(selectRS.next()) {
				idRemoteStore = selectRS.getLong(COLUMN_ID);
			}
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			
			JDBCUtilities.closeResources(selectRS, selectStmt);
		}
		
		return idRemoteStore;
	}
	
	
	
	
	public static RemoteStore getRemoteStore(DriverConfigurazioneDB driverConfigurazioneDB, RemoteStoreConfig remoteStoreConfig, boolean throwExceptionNotFound) throws KeystoreException {
		Connection con = null;
		try {
			con = driverConfigurazioneDB.getConnection("getRemoteStore", false);
			return getRemoteStore(con, driverConfigurazioneDB.getTipoDB(), remoteStoreConfig, throwExceptionNotFound);
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			driverConfigurazioneDB.releaseConnection(con);
		}
	}
	public static RemoteStore getRemoteStore(Connection con, String tipoDatabase, RemoteStoreConfig remoteStoreConfig, boolean throwExceptionNotFound) throws KeystoreException {
		
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		RemoteStore remoteStore = null;
		try {
			String remoteStoreName = RemoteStoreProviderDriver.getRemoteStoreConfigName(remoteStoreConfig);
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.REMOTE_STORE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(COLUMN_NOME+"=?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			selectStmt = con.prepareStatement(sqlQuery);
			selectStmt.setString(1, remoteStoreName);
			selectRS = selectStmt.executeQuery();
			if(selectRS.next()) {
				remoteStore = new RemoteStore();
				remoteStore.setId(selectRS.getLong(COLUMN_ID));
				remoteStore.setDataAggiornamento(selectRS.getTimestamp(COLUMN_DATA_AGGIORNAMENTO));
				remoteStore.setLastEvent(selectRS.getString(COLUMN_LAST_EVENT));
				remoteStore.setNome(selectRS.getString(COLUMN_NOME));
			}
			else if(throwExceptionNotFound){
				throw new KeystoreException("RemoteStore '"+remoteStoreName+"' not found");
			}
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			
			JDBCUtilities.closeResources(selectRS, selectStmt);
		}
		
		return remoteStore;
	}
	
	
	public static int updateRemoteStore(DriverConfigurazioneDB driverConfigurazioneDB, long idStore, String lastEventId) throws KeystoreException {
		Connection con = null;
		try {
			con = driverConfigurazioneDB.getConnection("updateRemoteStore", false);
			return updateRemoteStore(con, driverConfigurazioneDB.getTipoDB(), idStore, lastEventId);
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			driverConfigurazioneDB.releaseConnection(con);
		}
	}
	public static int updateRemoteStore(Connection con, String tipoDatabase, long idStore, String lastEventId) throws KeystoreException {
		PreparedStatement updateStmt = null;
		try {
			checkRemoteStoreParams(idStore, lastEventId);
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addUpdateTable(CostantiDB.REMOTE_STORE);
			sqlQueryObject.addUpdateField(COLUMN_LAST_EVENT, "?");
			sqlQueryObject.addUpdateField(COLUMN_DATA_AGGIORNAMENTO, "?");
			sqlQueryObject.addWhereCondition(COLUMN_ID+"=?");
			sqlQueryObject.setANDLogicOperator(true);
			String updateQuery = sqlQueryObject.createSQLUpdate();
			updateStmt = con.prepareStatement(updateQuery);
			int index = 1;
			Timestamp now = DateManager.getTimestamp();
			updateStmt.setString(index++, lastEventId);
			updateStmt.setTimestamp(index++, now);
			updateStmt.setLong(index++, idStore);
			int rows = updateStmt.executeUpdate();
			updateStmt.close();
			return rows;
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {	
			JDBCUtilities.closeResources(updateStmt);
		}
	}
	private static void checkRemoteStoreParams(long idStore, String lastEventId) throws KeystoreException {
		if(idStore<=0) {
			throw new KeystoreException("IdStore undefined");
		}
		if(lastEventId==null) {
			throw new KeystoreException("LastEventId undefined");
		}
	}
	
	
	
	
	
	
	public static int addRemoteStoreKey(DriverConfigurazioneDB driverConfigurazioneDB, long idStore, String kid, byte[] key) throws KeystoreException {
		Connection con = null;
		PreparedStatement updateStmt = null;
		try {
			con = driverConfigurazioneDB.getConnection("addRemoteStoreKey", false);
			
			checkParams(idStore, kid, key);
			
			IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(driverConfigurazioneDB.getTipoDB());
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(driverConfigurazioneDB.getTipoDB());
			sqlQueryObject.addInsertTable(CostantiDB.REMOTE_STORE_KEY);
			sqlQueryObject.addInsertField(COLUMN_ID_REMOTE_STORE, "?");
			sqlQueryObject.addInsertField(COLUMN_KID, "?");
			sqlQueryObject.addInsertField(COLUMN_KEY, "?");
			sqlQueryObject.addInsertField(COLUMN_DATA_REGISTRAZIONE, "?");
			sqlQueryObject.addInsertField(COLUMN_DATA_AGGIORNAMENTO, "?");
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
	
	public static int invalidRemoteStoreKey(DriverConfigurazioneDB driverConfigurazioneDB, long idStore, String kid) throws KeystoreException {
		return updateRemoteStoreKeyEngine(driverConfigurazioneDB, idStore, kid, null, true);
	}
	public static int invalidRemoteStoreKey(Connection con, String tipoDatabase, long idStore, String kid) throws KeystoreException {
		return updateRemoteStoreKeyEngine(con, tipoDatabase, idStore, kid, null, true);
	}
	public static int updateRemoteStoreKey(DriverConfigurazioneDB driverConfigurazioneDB, long idStore, String kid, byte[] key) throws KeystoreException {
		return updateRemoteStoreKeyEngine(driverConfigurazioneDB, idStore, kid, key, false);
	}
	private static Timestamp dataInvalidaScaduta = null;
	static{
		try {
			dataInvalidaScaduta = new Timestamp(DateUtils.getSimpleDateFormatDay().parse("2000-01-01").getTime());
		}catch(Exception e) {
			// ignore
		}
	}
	private static int updateRemoteStoreKeyEngine(DriverConfigurazioneDB driverConfigurazioneDB, long idStore, String kid, byte[] key, boolean invalid) throws KeystoreException {
		Connection con = null;
		try {
			con = driverConfigurazioneDB.getConnection(invalid ? "invalidRemoteStoreKey" : "updateRemoteStoreKey", false);
			return updateRemoteStoreKeyEngine(con, driverConfigurazioneDB.getTipoDB(), idStore, kid, key, invalid);
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			driverConfigurazioneDB.releaseConnection(con);
		}
	}
	private static int updateRemoteStoreKeyEngine(Connection con, String tipoDatabase, long idStore, String kid, byte[] key, boolean invalid) throws KeystoreException {
		PreparedStatement updateStmt = null;
		try {
			if(invalid) {
				checkParams(idStore, kid);	
			}
			else {
				checkParams(idStore, kid, key);
			}
			
			IJDBCAdapter jdbcAdapter = null;
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addUpdateTable(CostantiDB.REMOTE_STORE_KEY);
			if(!invalid) {
				sqlQueryObject.addUpdateField(COLUMN_KEY, "?");
				jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(tipoDatabase);
			}
			sqlQueryObject.addUpdateField(COLUMN_DATA_AGGIORNAMENTO, "?");
			addWhereKidConditions(sqlQueryObject);
			sqlQueryObject.setANDLogicOperator(true);
			String updateQuery = sqlQueryObject.createSQLUpdate();
			updateStmt = con.prepareStatement(updateQuery);
			int index = 1;
			Timestamp now = DateManager.getTimestamp();
			if(invalid) {
				updateStmt.setTimestamp(index++, dataInvalidaScaduta);
			}
			else {
				jdbcAdapter.setBinaryData(updateStmt, index++, key);
				updateStmt.setTimestamp(index++, now);
			}
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
		}
	}
	
	public static int deleteRemoteStoreKey(DriverConfigurazioneDB driverConfigurazioneDB, long idStore, String kid) throws KeystoreException {
		Connection con = null;
		try {
			con = driverConfigurazioneDB.getConnection("deleteRemoteStoreKey", false);
			return deleteRemoteStoreKey(con, driverConfigurazioneDB.getTipoDB(), idStore, kid);
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			driverConfigurazioneDB.releaseConnection(con);
		}
	}
	public static int deleteRemoteStoreKey(Connection con, String tipoDatabase, long idStore, String kid) throws KeystoreException {
		PreparedStatement updateStmt = null;
		try {
			checkParams(idStore, kid);
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addDeleteTable(CostantiDB.REMOTE_STORE_KEY);
			addWhereKidConditions(sqlQueryObject);
			sqlQueryObject.setANDLogicOperator(true);
			String updateQuery = sqlQueryObject.createSQLDelete();
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
		}
	}
	
	public static RemoteStoreKey getRemoteStoreKey(DriverConfigurazioneDB driverConfigurazioneDB, long idStore, String kid) throws KeystoreException,KeystoreNotFoundException {
		
		Connection con = null;
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		try {
			con = driverConfigurazioneDB.getConnection("getRemoteStoreKey", false);
			
			checkParams(idStore, kid);
			
			IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(driverConfigurazioneDB.getTipoDB());
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(driverConfigurazioneDB.getTipoDB());
			sqlQueryObject.addFromTable(CostantiDB.REMOTE_STORE_KEY);
			sqlQueryObject.addSelectField(COLUMN_KEY);
			sqlQueryObject.addSelectField(COLUMN_DATA_AGGIORNAMENTO);
			addWhereKidConditions(sqlQueryObject);
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			selectStmt = con.prepareStatement(sqlQuery);
			selectStmt.setLong(1, idStore);
			selectStmt.setString(2, kid);
			selectRS = selectStmt.executeQuery();
			if(selectRS.next()) {
				RemoteStoreKey rsk = new RemoteStoreKey();
				rsk.setKey(jdbcAdapter.getBinaryData(selectRS, COLUMN_KEY));
				rsk.setDataAggiornamento(selectRS.getTimestamp(COLUMN_DATA_AGGIORNAMENTO));
				rsk.setInvalid(dataInvalidaScaduta.equals(rsk.getDataAggiornamento()));
				return rsk;
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
	
	public static boolean existsRemoteStoreKey(DriverConfigurazioneDB driverConfigurazioneDB, long idStore, String kid, boolean checkDates) throws KeystoreException {
		
		Connection con = null;
		try {
			con = driverConfigurazioneDB.getConnection("existsRemoteStoreKey", false);
			return existsRemoteStoreKey(con, driverConfigurazioneDB.getTipoDB(), idStore, kid, checkDates);
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			driverConfigurazioneDB.releaseConnection(con);
		}
		
	}
	public static boolean existsRemoteStoreKey(Connection con, String tipoDatabase, long idStore, String kid, boolean checkDates) throws KeystoreException {
		
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		try {
			checkParams(idStore, kid);
			
			Timestamp tooOld = null;
			if(checkDates && RemoteStoreProviderDriver.getKeyMaxLifeMinutes()>0) {
				long maxLifeSeconds = RemoteStoreProviderDriver.getKeyMaxLifeMinutes() * 60l;
				long maxLifeMs = maxLifeSeconds * 1000l;
				tooOld = new Timestamp(DateManager.getTimeMillis()-maxLifeMs);
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.REMOTE_STORE_KEY);
			sqlQueryObject.addSelectField(COLUMN_KEY);
			addWhereKidConditions(sqlQueryObject);
			if(checkDates) {
				sqlQueryObject.addWhereCondition(COLUMN_DATA_AGGIORNAMENTO+">?");
			}
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			selectStmt = con.prepareStatement(sqlQuery);
			int index = 1;
			selectStmt.setLong(index++, idStore);
			selectStmt.setString(index++, kid);
			if(checkDates) {
				if(tooOld!=null) {
					selectStmt.setTimestamp(index++, tooOld);
				}
				else {
					selectStmt.setTimestamp(index++, dataInvalidaScaduta);
				}
			}
			selectRS = selectStmt.executeQuery();
			return selectRS.next();
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			JDBCUtilities.closeResources(selectRS, selectStmt);
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
		sqlQueryObject.addWhereCondition(COLUMN_ID_REMOTE_STORE+"=?");
		sqlQueryObject.addWhereCondition(COLUMN_KID+"=?");
	}
}
