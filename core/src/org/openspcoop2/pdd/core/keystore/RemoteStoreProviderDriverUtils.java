/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.pdd.config.PDNDConfigUtilities;
import org.openspcoop2.pdd.config.PDNDResolver;
import org.openspcoop2.protocol.sdk.ModIPDNDClientConfig;
import org.openspcoop2.protocol.sdk.ModIPDNDOrganizationConfig;
import org.openspcoop2.protocol.utils.ModIUtils;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.remote.RemoteStoreClientInfo;
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
import org.openspcoop2.utils.sql.LikeConfig;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;
import org.slf4j.Logger;

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
	
	private static final String COLUMN_CLIENT_ID = "client_id";
	private static final String COLUMN_CLIENT_DETAILS = "client_details";
	private static final String COLUMN_ORGANIZATION_DETAILS = "organization_details";
	private static final String COLUMN_CLIENT_DATA_AGGIORNAMENTO = "client_data_aggiornamento";
	
	private static final String SUFFIX_NOT_FOUND = " not found";
	
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
			con = driverConfigurazioneDB.getConnection("createRemoteStore");
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
			con = driverConfigurazioneDB.getConnection("getIdRemoteStore");
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
			con = driverConfigurazioneDB.getConnection("getRemoteStore");
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
				throw new KeystoreException("RemoteStore '"+remoteStoreName+"'"+SUFFIX_NOT_FOUND);
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
	
	
	
	public static String getLastEventIdRemoteStore(Connection con, String tipoDatabase, long idRemoteStore) throws KeystoreException {
		
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		String lastEventiId = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.REMOTE_STORE);
			sqlQueryObject.addSelectField(COLUMN_LAST_EVENT);
			sqlQueryObject.addWhereCondition(COLUMN_ID+"=?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			selectStmt = con.prepareStatement(sqlQuery);
			selectStmt.setLong(1, idRemoteStore);
			selectRS = selectStmt.executeQuery();
			if(selectRS.next()) {
				lastEventiId = selectRS.getString(COLUMN_LAST_EVENT);
			}
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			
			JDBCUtilities.closeResources(selectRS, selectStmt);
		}
		
		return lastEventiId;
	}
	public static void resetLastEventIdRemoteStore(Connection con, String tipoDatabase, long idRemoteStore) throws KeystoreException {
		
		PreparedStatement selectStmt = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addUpdateTable(CostantiDB.REMOTE_STORE);
			sqlQueryObject.addUpdateField(COLUMN_LAST_EVENT,"?");
			sqlQueryObject.addWhereCondition(COLUMN_ID+"=?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlUpdate = sqlQueryObject.createSQLUpdate();
			selectStmt = con.prepareStatement(sqlUpdate);
			String resetNullValue = null;
			selectStmt.setString(1, resetNullValue);
			selectStmt.setLong(2, idRemoteStore);
			int row = selectStmt.executeUpdate();
			if(row<=0) {
				throw new KeystoreException("Update failed");
			}
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			
			JDBCUtilities.closeResources(selectStmt);
		}

	}
	
	
	
	public static int updateRemoteStore(DriverConfigurazioneDB driverConfigurazioneDB, long idStore, String lastEventId) throws KeystoreException {
		Connection con = null;
		try {
			con = driverConfigurazioneDB.getConnection("updateRemoteStore");
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
	
	
	public static List<RemoteStore> getRemoteStores(DriverConfigurazioneDB driverConfigurazioneDB) throws KeystoreException {
		Connection con = null;
		try {
			con = driverConfigurazioneDB.getConnection("getRemoteStores");
			return getRemoteStores(con, driverConfigurazioneDB.getTipoDB());
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			driverConfigurazioneDB.releaseConnection(con);
		}
	}
	public static List<RemoteStore> getRemoteStores(Connection con, String tipoDatabase) throws KeystoreException {
		
		List<RemoteStore> list = new ArrayList<>();
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.REMOTE_STORE);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(COLUMN_NOME);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			selectStmt = con.prepareStatement(sqlQuery);
			selectRS = selectStmt.executeQuery();
			while(selectRS.next()) {
				long idRemoteStore = selectRS.getLong(COLUMN_ID);
				Date dataAggiornamento = selectRS.getTimestamp(COLUMN_DATA_AGGIORNAMENTO);
				String lastEvent =  selectRS.getString(COLUMN_LAST_EVENT);
				String nome = selectRS.getString(COLUMN_NOME);
				RemoteStore rs = new RemoteStore();
				rs.setId(idRemoteStore);
				rs.setDataAggiornamento(dataAggiornamento);
				rs.setLastEvent(lastEvent);
				rs.setNome(nome);
				list.add(rs);
			}
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			
			JDBCUtilities.closeResources(selectRS, selectStmt);
		}
		
		return list;
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
			con = driverConfigurazioneDB.getConnection(invalid ? "invalidRemoteStoreKey" : "updateRemoteStoreKey");
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
			if(invalid) {
				sqlQueryObject.addUpdateField(COLUMN_CLIENT_DATA_AGGIORNAMENTO, "?");
			}
			addWhereKidConditions(sqlQueryObject);
			sqlQueryObject.setANDLogicOperator(true);
			String updateQuery = sqlQueryObject.createSQLUpdate();
			updateStmt = con.prepareStatement(updateQuery);
			int index = 1;
			Timestamp now = DateManager.getTimestamp();
			if(invalid) {
				updateStmt.setTimestamp(index++, dataInvalidaScaduta);
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
			con = driverConfigurazioneDB.getConnection("deleteRemoteStoreKey");
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
			con = driverConfigurazioneDB.getConnection("getRemoteStoreKey");
			
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
			throw new KeystoreNotFoundException("Key with kid '"+kid+"'"+SUFFIX_NOT_FOUND);
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
			con = driverConfigurazioneDB.getConnection("existsRemoteStoreKey");
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
	
	
	public static RemoteStoreClientDetails getRemoteStoreClientDetails(DriverConfigurazioneDB driverConfigurazioneDB, long idStore, String kid, Logger log,
			boolean createEntryIfNotExists, RemoteStoreConfig remoteConfig) throws KeystoreException,KeystoreNotFoundException {
		
		Connection con = null;
		try {
			con = driverConfigurazioneDB.getConnection("getRemoteStoreClientDetails");
			
			return getRemoteStoreClientDetails(con, driverConfigurazioneDB.getTipoDB(), idStore, kid, log,
					createEntryIfNotExists, remoteConfig);
		}
		catch(KeystoreNotFoundException e) {
			throw e;
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			driverConfigurazioneDB.releaseConnection(con);
		}
		
	}
	public static RemoteStoreClientDetails getRemoteStoreClientDetails(Connection con, String tipoDatabase, long idStore, String kid, Logger log, 
			boolean createEntryIfNotExists, RemoteStoreConfig remoteConfig) throws KeystoreException,KeystoreNotFoundException {
		
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		try {
			checkParams(idStore, kid);
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.REMOTE_STORE_KEY);
			sqlQueryObject.addSelectField(COLUMN_CLIENT_ID);
			sqlQueryObject.addSelectField(COLUMN_CLIENT_DETAILS);
			sqlQueryObject.addSelectField(COLUMN_ORGANIZATION_DETAILS);
			sqlQueryObject.addSelectField(COLUMN_CLIENT_DATA_AGGIORNAMENTO);
			addWhereKidConditions(sqlQueryObject);
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			selectStmt = con.prepareStatement(sqlQuery);
			selectStmt.setLong(1, idStore);
			selectStmt.setString(2, kid);
			selectRS = selectStmt.executeQuery();
			if(selectRS.next()) {
				RemoteStoreClientDetails rsk = new RemoteStoreClientDetails();
				rsk.setClientInfo(new RemoteStoreClientInfo());
				rsk.getClientInfo().setClientId(selectRS.getString(COLUMN_CLIENT_ID));
				rsk.getClientInfo().setClientDetails(selectRS.getString(COLUMN_CLIENT_DETAILS));
				if(rsk.getClientInfo().getClientId()!=null && rsk.getClientInfo().getClientDetails()!=null) {
					String jsonPath = ModIUtils.extractInfoFromMetadati(remoteConfig.getMetadati(), ModIUtils.API_PDND_CLIENTS_ORGANIZATION_JSON_PATH, "Clients organization json path");
					boolean readErrorAbortTransaction = false;
					String organizationId = PDNDConfigUtilities.readOrganizationId(jsonPath, readErrorAbortTransaction, rsk.getClientInfo().getClientDetails(), log);
					rsk.getClientInfo().setOrganizationId(organizationId);
				}
				rsk.getClientInfo().setOrganizationDetails(selectRS.getString(COLUMN_ORGANIZATION_DETAILS));
				rsk.setDataAggiornamento(selectRS.getTimestamp(COLUMN_CLIENT_DATA_AGGIORNAMENTO));
				rsk.setInvalid(dataInvalidaScaduta.equals(rsk.getDataAggiornamento()));
				return rsk;
			}
			
			if(createEntryIfNotExists) {
				int row = initializeEmptyEntry(con, tipoDatabase, idStore, kid, log);
				if(row>0) {
					return new RemoteStoreClientDetails();
				}
			}
			
			throw new KeystoreNotFoundException("Key with kid '"+kid+"'"+SUFFIX_NOT_FOUND);
		}
		catch(KeystoreNotFoundException e) {
			throw e;
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			JDBCUtilities.closeResources(selectRS, selectStmt);
		}
		
	}
	
	private static final byte[] KEY_UNDEFINED = "KEY_UNDEFINED".getBytes();
	private static int initializeEmptyEntry(Connection con, String tipoDB, long idStore, String kid, Logger log) {
		PreparedStatement updateStmt = null;
		try {
			IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(tipoDB);
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
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
			jdbcAdapter.setBinaryData(updateStmt, index++, KEY_UNDEFINED);
			Timestamp now = DateManager.getTimestamp();
			updateStmt.setTimestamp(index++, now);
			updateStmt.setTimestamp(index++, dataInvalidaScaduta);
			int rows = updateStmt.executeUpdate();
			updateStmt.close();
			return rows;
		}
		catch(Exception e) {
			log.error("initializeEmptyEntry failed: "+e.getMessage(),e);
			return -1;
		}
		finally {
			JDBCUtilities.closeResources(updateStmt);
		}
	}
	
	public static int updateRemoteStoreClientDetails(DriverConfigurazioneDB driverConfigurazioneDB, long idStore, String kid, RemoteStoreClientDetails clientDetails) throws KeystoreException {
		Connection con = null;
		try {
			con = driverConfigurazioneDB.getConnection("updateRemoteStoreClientDetails");
			return updateRemoteStoreClientDetails(con, driverConfigurazioneDB.getTipoDB(), idStore, kid, clientDetails);
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			driverConfigurazioneDB.releaseConnection(con);
		}
	}
	public static int updateRemoteStoreClientDetails(Connection con, String tipoDatabase, long idStore, String kid, RemoteStoreClientDetails clientDetails) throws KeystoreException {
		PreparedStatement updateStmt = null;
		try {
			checkParams(idStore, kid);	

			if(clientDetails==null) {
				throw new KeystoreException("ClientDetails undefined");
			}
			if(clientDetails.getClientInfo()==null) {
				throw new KeystoreException("ClientInfo undefined");
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addUpdateTable(CostantiDB.REMOTE_STORE_KEY);
			sqlQueryObject.addUpdateField(COLUMN_CLIENT_ID, "?");
			sqlQueryObject.addUpdateField(COLUMN_CLIENT_DETAILS, "?");
			sqlQueryObject.addUpdateField(COLUMN_ORGANIZATION_DETAILS, "?");
			sqlQueryObject.addUpdateField(COLUMN_CLIENT_DATA_AGGIORNAMENTO, "?");
			addWhereKidConditions(sqlQueryObject);
			sqlQueryObject.setANDLogicOperator(true);
			String updateQuery = sqlQueryObject.createSQLUpdate();
			updateStmt = con.prepareStatement(updateQuery);
			int index = 1;
			updateStmt.setString(index++, clientDetails.getClientInfo().getClientId());
			String clientDetailsValue = clientDetails.getClientInfo().getClientDetails();
			String organizationDetailsValue = clientDetails.getClientInfo().getOrganizationDetails();
			if(org.openspcoop2.utils.jdbc.NullByteTextColumnSanitizer.needsSanitization(tipoDatabase)){
				clientDetailsValue = org.openspcoop2.utils.jdbc.NullByteTextColumnSanitizer.sanitize(tipoDatabase, clientDetailsValue);
				organizationDetailsValue = org.openspcoop2.utils.jdbc.NullByteTextColumnSanitizer.sanitize(tipoDatabase, organizationDetailsValue);
			}
			updateStmt.setString(index++, clientDetailsValue);
			updateStmt.setString(index++, organizationDetailsValue);
			updateStmt.setTimestamp(index++, clientDetails.getDataAggiornamento()!=null ? new Timestamp(clientDetails.getDataAggiornamento().getTime()) : DateManager.getTimestamp());
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
	
	
	
	
	
	
	
	
	
	
	
	
	public static List<RemoteStoreKeyEntry> getRemoteStoreKeyEntries(DriverConfigurazioneDB driverConfigurazioneDB, ISearch ricerca, long idRemoteStore) throws KeystoreException {
		Connection con = null;
		try {
			con = driverConfigurazioneDB.getConnection("getRemoteStoreKeyEntries");
			return getRemoteStoreKeyEntries(con, driverConfigurazioneDB.getTipoDB(), ricerca, idRemoteStore);
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			driverConfigurazioneDB.releaseConnection(con);
		}
	}
	public static List<RemoteStoreKeyEntry> getRemoteStoreKeyEntries(Connection con, String tipoDatabase, ISearch ricerca, long idRemoteStore) throws KeystoreException {
		
		int idLista = Liste.REMOTE_STORE_KEY;
		int offset;
		int limit;
		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		
		if (limit == 0) // con limit
			limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
		
		String filtroKid = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_REMOTE_STORE_KEY_KID);
		if((filtroKid!=null && "".equals(filtroKid))) {
			filtroKid=null;
		}
		
		String filtroClientId = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_REMOTE_STORE_KEY_CLIENT_ID);
		if((filtroClientId!=null && "".equals(filtroClientId))) {
			filtroClientId=null;
		}
		
		String filtroOrganizzazione = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_REMOTE_STORE_KEY_ORGANIZZAZIONE);
		if((filtroOrganizzazione!=null && "".equals(filtroOrganizzazione))) {
			filtroOrganizzazione=null;
		}

		// count
		PreparedStatement selectStmtCount = null;
		ResultSet selectRSCount = null;
		int size = 0;
		try {
			ISQLQueryObject sqlQueryObject = buildSqlRemoteStoreKeyEntries(tipoDatabase, filtroKid, filtroClientId, filtroOrganizzazione);
			sqlQueryObject.addSelectCountField("somma");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			selectStmtCount = con.prepareStatement(sqlQuery);
			selectStmtCount.setLong(1, idRemoteStore);
			selectRSCount = selectStmtCount.executeQuery();
			if(selectRSCount.next()) {
				size = selectRSCount.getInt("somma");
				if(size<0) {
					size=0;
				}
				ricerca.setNumEntries(idLista, size);
			}
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			JDBCUtilities.closeResources(selectRSCount, selectStmtCount);
		}
		
		List<RemoteStoreKeyEntry> list = new ArrayList<>();
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		try {
			IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(tipoDatabase);
			
			ISQLQueryObject sqlQueryObject = buildSqlRemoteStoreKeyEntries(tipoDatabase, filtroKid, filtroClientId, filtroOrganizzazione);
			sqlQueryObject.addOrderBy(COLUMN_DATA_REGISTRAZIONE, false);
			sqlQueryObject.setOffset(offset);
			sqlQueryObject.setLimit(limit);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			selectStmt = con.prepareStatement(sqlQuery);
			selectStmt.setLong(1, idRemoteStore);
			selectRS = selectStmt.executeQuery();
			while(selectRS.next()) {
				list.add(readKeyEntry(selectRS, jdbcAdapter));
			}
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			
			JDBCUtilities.closeResources(selectRS, selectStmt);
		}
		
		return list;
	}
	
	private static ISQLQueryObject buildSqlRemoteStoreKeyEntries(String tipoDatabase, String filtroKid, String filtroClientId, String filtroOrganizzazione) throws KeystoreException {
		ISQLQueryObject sqlQueryObject = null;
		try {
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.REMOTE_STORE_KEY);
			sqlQueryObject.addWhereCondition(COLUMN_ID_REMOTE_STORE+"=?");
			if(filtroKid!=null) {
				if(filtroKid.equals("-")) {
					sqlQueryObject.addWhereLikeCondition(COLUMN_KID, PDNDResolver.REMOTE_STORE_KEY_KID_STARTS_WITH_CLIENT_ID, LikeConfig.startsWith(false));
				}
				else {
					sqlQueryObject.addWhereLikeCondition(COLUMN_KID, filtroKid, LikeConfig.contains(true));
				}
			}
			if(filtroClientId!=null) {
				if(filtroClientId.equals("-")) {
					sqlQueryObject.addWhereIsNullCondition(COLUMN_CLIENT_ID);
				}
				else {
					sqlQueryObject.addWhereLikeCondition(COLUMN_CLIENT_ID, filtroClientId, LikeConfig.contains(true));
				}
			}
			if(filtroOrganizzazione!=null) {
				if(filtroOrganizzazione.equals("-")) {
					sqlQueryObject.addWhereIsNullCondition(COLUMN_ORGANIZATION_DETAILS);
				}
				else {
					sqlQueryObject.addWhereLikeCondition(COLUMN_ORGANIZATION_DETAILS, filtroOrganizzazione, LikeConfig.contains(true));
				}
			}
			sqlQueryObject.setANDLogicOperator(true);
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		return sqlQueryObject;
	}
	
	private static RemoteStoreKeyEntry readKeyEntry(ResultSet selectRS, IJDBCAdapter jdbcAdapter) throws SQLException, UtilsException {
		long id = selectRS.getLong(COLUMN_ID);
		long idStore = selectRS.getLong(COLUMN_ID_REMOTE_STORE);
		Date dataRegistrazione = selectRS.getTimestamp(COLUMN_DATA_REGISTRAZIONE);
		
		String kid = selectRS.getString(COLUMN_KID);
		Date dataAggiornamento = selectRS.getTimestamp(COLUMN_DATA_AGGIORNAMENTO);
		
		String clientId =  selectRS.getString(COLUMN_CLIENT_ID);
		String clientDetails = selectRS.getString(COLUMN_CLIENT_DETAILS);
		String organizationDetails = selectRS.getString(COLUMN_ORGANIZATION_DETAILS);
		Date clientDataAggiornamento = selectRS.getTimestamp(COLUMN_CLIENT_DATA_AGGIORNAMENTO);
		
		RemoteStoreKeyEntry rs = new RemoteStoreKeyEntry();
		rs.setId(id);
		rs.setDataRegistrazione(dataRegistrazione);
		rs.setIdRemoteStore(idStore);
		
		rs.setContentKey(jdbcAdapter.getBinaryData(selectRS, COLUMN_KEY));
		rs.setKid(kid);
		rs.setDataAggiornamento(dataAggiornamento);
		
		rs.setClientId(clientId);
		rs.setClientDetails(clientDetails);
		rs.setOrganizationDetails(organizationDetails);
		rs.setClientDataAggiornamento(clientDataAggiornamento);
		
		enrichClientInfo(clientDetails, rs);
		enrichOrganizationInfo(organizationDetails, rs);
		return rs;
	}
	

	private static void enrichClientInfo(String clientDetails, RemoteStoreKeyEntry rs) {
		if(clientDetails!=null) {
			ModIPDNDClientConfig config = null;
			try {
				config = ModIUtils.getAPIPDNDClientConfig(clientDetails);
			}catch(Exception e) {
				// ignore
			}
			if(config==null) {
				return;
			}
			
			rs.setClientIdPdnd(config.getId());
			rs.setClientConsumerId(config.getOrganization());
			rs.setClientName(config.getName());
			rs.setClientDescription(config.getDescription());
			
		}
	}
	private static void enrichOrganizationInfo(String organizationDetails, RemoteStoreKeyEntry rs) {
		if(organizationDetails!=null) {
			ModIPDNDOrganizationConfig config = null;
			try {
				config = ModIUtils.getAPIPDNDOrganizationConfig(organizationDetails);
			}catch(Exception e) {
				// ignore
			}
			if(config==null) {
				return;
			}
			
			rs.setOrganizationIdPdnd(config.getId());
			rs.setOrganizationName(config.getName());
			rs.setOrganizationExternalOrigin(config.getExternalOrigin());
			rs.setOrganizationExternalId(config.getExternalId());
			rs.setOrganizationCategory(config.getCategory());
			rs.setOrganizationSubunit(config.getSubUnit());
			
		}
	}
	
	
	public static int deleteRemoteStoreKeyEntry(DriverConfigurazioneDB driverConfigurazioneDB, long idRemoteStore, long idEntry) throws KeystoreException {
		Connection con = null;
		try {
			con = driverConfigurazioneDB.getConnection("deleteRemoteStoreKeyEntry");
			return deleteRemoteStoreKeyEntry(con, driverConfigurazioneDB.getTipoDB(), idRemoteStore, idEntry);
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			driverConfigurazioneDB.releaseConnection(con);
		}
	}
	public static int deleteRemoteStoreKeyEntry(Connection con, String tipoDatabase, long idRemoteStore, long idEntry) throws KeystoreException {
		PreparedStatement updateStmt = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addDeleteTable(CostantiDB.REMOTE_STORE_KEY);
			sqlQueryObject.addWhereCondition(COLUMN_ID_REMOTE_STORE+"=?");
			sqlQueryObject.addWhereCondition(COLUMN_ID+"=?");
			sqlQueryObject.setANDLogicOperator(true);
			String updateQuery = sqlQueryObject.createSQLDelete();
			updateStmt = con.prepareStatement(updateQuery);
			updateStmt.setLong(1, idRemoteStore);
			updateStmt.setLong(2, idEntry);
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
	
	public static RemoteStoreKeyEntry getRemoteStoreKeyEntry(Logger log, DriverConfigurazioneDB driverConfigurazioneDB, long idRemoteStoreKey) throws KeystoreException, KeystoreNotFoundException {
		Connection con = null;
		try {
			if(log!=null) {
				// nop
			}
			con = driverConfigurazioneDB.getConnection("getRemoteStoreKeyEntry");
			return getRemoteStoreKeyEntry(con, driverConfigurazioneDB.getTipoDB(), idRemoteStoreKey);
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			driverConfigurazioneDB.releaseConnection(con);
		}
	}
	
	public static RemoteStoreKeyEntry getRemoteStoreKeyEntry(Connection con, String tipoDatabase, long idRemoteStoreKey) throws KeystoreException,KeystoreNotFoundException {
		
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		try {
			IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(tipoDatabase);
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.REMOTE_STORE_KEY);
			sqlQueryObject.addWhereCondition(COLUMN_ID+"=?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			selectStmt = con.prepareStatement(sqlQuery);
			selectStmt.setLong(1, idRemoteStoreKey);
			selectRS = selectStmt.executeQuery();
			if(selectRS.next()) {
				return readKeyEntry(selectRS, jdbcAdapter);
			}
			throw new KeystoreNotFoundException("Key with id '"+idRemoteStoreKey+"'"+SUFFIX_NOT_FOUND);
		}
		catch(KeystoreNotFoundException e) {
			throw e;
		}
		catch(Exception e) {
			throw new KeystoreException(e.getMessage(),e);
		}
		finally {
			JDBCUtilities.closeResources(selectRS, selectStmt);
		}
	}
}
