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

package org.openspcoop2.pdd.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.pdd.services.ServicesUtils;
import org.openspcoop2.pdd.services.connector.proxy.ProxyOperation;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.id.apache.serial.MaxReachedException;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;
import org.slf4j.Logger;

/**
 * DynamicClusterManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DynamicClusterManager {

	private static DynamicClusterManager staticInstance = null;
	public static synchronized void initStaticInstance() throws CoreException {
		if(staticInstance==null) {
			staticInstance = new DynamicClusterManager();
		}
	}
	public static DynamicClusterManager getInstance() throws CoreException {
		if(staticInstance==null) {
			initStaticInstance();
		}
		return staticInstance;
	}
	public static boolean isInitialized() {
		return staticInstance!=null;
	}
	
	private DriverConfigurazioneDB driverConfigurazioneDB = null;
	private OpenSPCoop2Properties op2Properties = null;
	
	protected DynamicClusterManager() throws CoreException {
		Object oConfig = ConfigurazionePdDReader.getDriverConfigurazionePdD();
		if(oConfig instanceof DriverConfigurazioneDB) {
			this.driverConfigurazioneDB = (DriverConfigurazioneDB) oConfig;
		}
		else {
			throw new CoreException("Modalità dinamica utilizzabile solamente con una configurazione su database");
		}
		this.op2Properties = OpenSPCoop2Properties.getInstance();
	}
	
	private boolean rateLimitingGestioneCluster = false;
	public boolean isRateLimitingGestioneCluster() {
		return this.rateLimitingGestioneCluster;
	}
	public void setRateLimitingGestioneCluster(boolean rateLimitingGestioneCluster) {
		this.rateLimitingGestioneCluster = rateLimitingGestioneCluster;
	}
	
	private int identificativoNumerico = -1;
	public int getIdentificativoNumerico() {
		return this.identificativoNumerico;
	}
	
	private String getSuffixMessageUpdate(int row) {
		return " nella tabella '"+CostantiDB.CONFIG_NODI_RUNTIME+"' (update-row: "+row+")";
	}
	
	public void register(Logger log) throws CoreException {
		Connection con = null;
		int oldTransactionIsolation = -1;
		try {
			con = this.driverConfigurazioneDB.getConnection("DynamicClusterManager.register");
			
			boolean autoCommit = false;
			con.setAutoCommit(autoCommit);
			
			/*
		      Viene realizzato con livello di isolamento SERIALIZABLE, per essere sicuri
		      che esecuzioni parallele non leggano dati inconsistenti.
		      Con il livello SERIALIZABLE, se ritorna una eccezione, deve essere riprovato
		      La sincronizzazione e' necessaria per via del possibile accesso simultaneo del servizio Gop
		      e del servizio che si occupa di eliminare destinatari di messaggi
			 */
			// setAutoCommit e livello Isolamento
			try{
				oldTransactionIsolation = con.getTransactionIsolation();
				JDBCUtilities.setTransactionIsolationSerializable(this.driverConfigurazioneDB.getTipoDB(), con);
			} catch(Exception er) {
				throw new CoreException("(setIsolation) "+er.getMessage(),er);
			}

			boolean updateEffettuato = false;
			
			long gestioneSerializableDBattesaAttiva = this.op2Properties.getGestioneSerializableDB_AttesaAttiva();
			int gestioneSerializableDBcheckInterval = this.op2Properties.getGestioneSerializableDB_CheckInterval();
			long scadenzaWhile = DateManager.getTimeMillis() + gestioneSerializableDBattesaAttiva;
			
			Exception eLast = null;
			
			while(!updateEffettuato && DateManager.getTimeMillis() < scadenzaWhile){

				try{
					this.registerEngine(con, log);

					updateEffettuato = true;

				} 
				catch(MaxReachedException maxReached) {
					throw maxReached;
				}
				catch(Exception e) {
					eLast = e;
					/**System.out.println("Serializable error:"+e.getMessage());*/
				}

				if(!updateEffettuato){
					// Per aiutare ad evitare conflitti
					try{
						Utilities.sleep((ServicesUtils.getRandom()).nextInt(gestioneSerializableDBcheckInterval)); // random da 0ms a checkIntervalms
					}catch(Exception eRandom){
						// ignore
					}
				}
			}
						
			if(!updateEffettuato){
				throw new CoreException("Registrazione fallita: "+eLast.getMessage(), eLast);
			}
		}
		catch(Exception e) {
			throw new CoreException("[DynamicClusterManager.register] failed: "+e.getMessage(), e);
		}
		finally {
			// Ripristino Transazione
			try{
				con.setTransactionIsolation(oldTransactionIsolation);
			} catch(Exception er) {
				if(log!=null) {
					log.error("(ripristinoIsolation) "+er.getMessage(),er);
				}
				else {
					LoggerWrapperFactory.getLogger(DynamicClusterManager.class).error("(ripristinoIsolation) "+er.getMessage(),er);
				}
			}
			try{
				con.setAutoCommit(true);
			}catch(Exception eRollback){
				/**if(log!=null) {
					log.error("(setAutoCommit) "+eRollback.getMessage(),eRollback);
				}
				else {
					LoggerWrapperFactory.getLogger(DynamicClusterManager.class).error("(setAutoCommit) "+eRollback.getMessage(),eRollback);
				}*/
			}
			try {
				if(con!=null) {
					this.driverConfigurazioneDB.releaseConnection(con);
				}
			}catch(Exception eClose) {
				// close
			}
		}
	}
	
	private void registerEngine(Connection con, Logger log) throws MaxReachedException, SQLException, SQLQueryObjectException {
		try {
			boolean find = registerExistsId(con);
			
			if(find) {
				
				// Update
				registerUpdate(con, log);
				
			}
			else {
				
				// Cerco idNumerico disponibile
				/**if(this.op2Properties.isClusterIdNumericoDinamico()) { FIX: va sempre calcolato per l'unique del db*/
				this.identificativoNumerico = this.getNextIdNumerico(con);
				
				// Insert
				registerInsert(con, log);
			}
			
			con.commit();
		}
		catch(Exception e) {
			try{
				con.rollback();
			} catch(Exception er) {
				// ignore
			}
			throw e;
		}
	}
	private boolean registerExistsId(Connection con) throws SQLException, SQLQueryObjectException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driverConfigurazioneDB.getTipoDB());
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_NODI_RUNTIME);
			sqlQueryObject.addSelectField(CostantiDB.CONFIG_NODI_RUNTIME_COLUMN_ID_NUMERICO);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.CONFIG_NODI_RUNTIME_COLUMN_HOSTNAME+"=?");
			String query = sqlQueryObject.createSQLQuery();
			pstmt = con.prepareStatement(query);
			int index = 1;
			pstmt.setString(index++, this.op2Properties.getClusterHostname());
			rs = pstmt.executeQuery();
			boolean find = rs.next();
			if(find) {
				this.identificativoNumerico = rs.getInt(CostantiDB.CONFIG_NODI_RUNTIME_COLUMN_ID_NUMERICO);
			}
			return find;
		}
		finally {
			try {
				if(rs!=null) {
					rs.close();
				}
			}catch(Exception eClose) {
				// close
			}
			try {
				if(pstmt!=null) {
					pstmt.close();
				}
			}catch(Exception eClose) {
				// close
			}
		}
	}
	private void registerInsert(Connection con, Logger log) throws SQLException, SQLQueryObjectException {
		PreparedStatement pstmt = null;
		try {
			// Insert
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driverConfigurazioneDB.getTipoDB());
			sqlQueryObject.addInsertTable(CostantiDB.CONFIG_NODI_RUNTIME);
			sqlQueryObject.addInsertField(CostantiDB.CONFIG_NODI_RUNTIME_COLUMN_HOSTNAME, "?");
			sqlQueryObject.addInsertField(CostantiDB.CONFIG_NODI_RUNTIME_COLUMN_GRUPPO, "?");
			sqlQueryObject.addInsertField(CostantiDB.CONFIG_NODI_RUNTIME_COLUMN_DATA_REGISTRAZIONE, "?");
			sqlQueryObject.addInsertField(CostantiDB.CONFIG_NODI_RUNTIME_COLUMN_DATA_REFRESH, "?");
			sqlQueryObject.addInsertField(CostantiDB.CONFIG_NODI_RUNTIME_COLUMN_ID_NUMERICO, "?");
			String insert = sqlQueryObject.createSQLInsert();
			pstmt = con.prepareStatement(insert);
			int index = 1;
			pstmt.setString(index++, this.op2Properties.getClusterHostname());
			pstmt.setString(index++, this.op2Properties.getGroupId(this.rateLimitingGestioneCluster));
			Timestamp now = DateManager.getTimestamp();
			pstmt.setTimestamp(index++, now);
			pstmt.setTimestamp(index++, now);
			pstmt.setInt(index++, this.identificativoNumerico);
			int row = pstmt.executeUpdate();
			
			String msg = "Registrato hostname '"+this.op2Properties.getClusterHostname()+"'"+getSuffixMessageUpdate(row);
			log.debug(msg);
		}
		finally {
			try {
				if(pstmt!=null) {
					pstmt.close();
				}
			}catch(Exception eClose) {
				// close
			}
		}
	}
	private void registerUpdate(Connection con, Logger log) throws SQLException, SQLQueryObjectException {
		PreparedStatement pstmt = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driverConfigurazioneDB.getTipoDB());
			sqlQueryObject.addUpdateTable(CostantiDB.CONFIG_NODI_RUNTIME);
			sqlQueryObject.addUpdateField(CostantiDB.CONFIG_NODI_RUNTIME_COLUMN_GRUPPO, "?");
			sqlQueryObject.addUpdateField(CostantiDB.CONFIG_NODI_RUNTIME_COLUMN_DATA_REGISTRAZIONE, "?");
			sqlQueryObject.addUpdateField(CostantiDB.CONFIG_NODI_RUNTIME_COLUMN_DATA_REFRESH, "?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.CONFIG_NODI_RUNTIME_COLUMN_HOSTNAME+"=?");
			String update = sqlQueryObject.createSQLUpdate();
			pstmt = con.prepareStatement(update);
			int index = 1;
			pstmt.setString(index++, this.op2Properties.getGroupId(this.rateLimitingGestioneCluster));
			Timestamp now = DateManager.getTimestamp();
			pstmt.setTimestamp(index++, now);
			pstmt.setTimestamp(index++, now);
			pstmt.setString(index++, this.op2Properties.getClusterHostname());
			int row = pstmt.executeUpdate();
			
			String msg = "Registrato con aggiormanento hostname '"+this.op2Properties.getClusterHostname()+"'"+getSuffixMessageUpdate(row);
			log.debug(msg);
		}
		finally {
			try {
				if(pstmt!=null) {
					pstmt.close();
				}
			}catch(Exception eClose) {
				// close
			}
		}
	}
	private int getNextIdNumerico(Connection con) throws MaxReachedException, SQLException, SQLQueryObjectException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			// Cerco idNumerico disponibile
			int idNumerico = -1;
			int max = getMax();
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driverConfigurazioneDB.getTipoDB());
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_NODI_RUNTIME);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addSelectCountField(CostantiDB.CONFIG_NODI_RUNTIME_COLUMN_ID_NUMERICO, "countid");
			sqlQueryObject.addSelectMaxField(CostantiDB.CONFIG_NODI_RUNTIME_COLUMN_ID_NUMERICO, "maxid");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.CONFIG_NODI_RUNTIME_COLUMN_GRUPPO+"=?");
			String query = sqlQueryObject.createSQLQuery();
			pstmt = con.prepareStatement(query);
			int index = 1;
			pstmt.setString(index++, this.op2Properties.getGroupId(this.rateLimitingGestioneCluster));
			rs = pstmt.executeQuery();
			if(rs.next()) {
				int countid = rs.getInt("countid");
				int findId = rs.getInt("maxid");
				if(countid == 0) {
					idNumerico = 0; // primo id numerico
				}
				else if((findId+1)<=max) {
					idNumerico = findId+1;
				}
			}
			else {
				idNumerico = 0; // primo id numerico
			}
			rs.close(); rs = null;
			pstmt.close(); pstmt = null;
			
			if(idNumerico<0) {
				idNumerico = getIdNumerico(con, max);
			}

			return idNumerico;
		}
		finally {
			try {
				if(rs!=null) {
					rs.close();
				}
			}catch(Exception eClose) {
				// close
			}
			try {
				if(pstmt!=null) {
					pstmt.close();
				}
			}catch(Exception eClose) {
				// close
			}
		}
	}
	private int getMax() {
		int cifre = this.op2Properties.getClusterDinamicoIdNumericoCifre(this.rateLimitingGestioneCluster);
		StringBuilder sbCifre = new StringBuilder();
		for (int i = 0; i < cifre; i++) {
			sbCifre.append("9");
		}
		// int max
		return Integer.parseInt(sbCifre.toString());
	}
	private int getIdNumerico(Connection con, int max) throws MaxReachedException, SQLException, SQLQueryObjectException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int idNumerico = -1;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driverConfigurazioneDB.getTipoDB());
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_NODI_RUNTIME);
			sqlQueryObject.addSelectField(CostantiDB.CONFIG_NODI_RUNTIME_COLUMN_ID_NUMERICO);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.CONFIG_NODI_RUNTIME_COLUMN_GRUPPO+"=?");
			sqlQueryObject.addOrderBy(CostantiDB.CONFIG_NODI_RUNTIME_COLUMN_ID_NUMERICO, true);
			String query = sqlQueryObject.createSQLQuery();
			pstmt = con.prepareStatement(query);
			int index = 1;
			pstmt.setString(index++, this.op2Properties.getGroupId(this.rateLimitingGestioneCluster));
			rs = pstmt.executeQuery();
			int search = 0;
			while(rs.next()) {
				int idNumericoExists = rs.getInt(CostantiDB.CONFIG_NODI_RUNTIME_COLUMN_ID_NUMERICO);
				if(search!=idNumericoExists) {
					idNumerico = search;
					break;
				}
				search++;
				if(search>max) {
					throw new MaxReachedException("Numero massimo di nodi registrabili ("+(max+1)+") raggiunto");
				}
			}
		}
		finally {
			try {
				if(rs!=null) {
					rs.close();
				}
			}catch(Exception eClose) {
				// close
			}
			try {
				if(pstmt!=null) {
					pstmt.close();
				}
			}catch(Exception eClose) {
				// close
			}
		}
		return idNumerico;
	}
	
	public void unregister(Logger log) throws CoreException {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = this.driverConfigurazioneDB.getConnection("DynamicClusterManager.unregister");
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driverConfigurazioneDB.getTipoDB());
			sqlQueryObject.addDeleteTable(CostantiDB.CONFIG_NODI_RUNTIME);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.CONFIG_NODI_RUNTIME_COLUMN_HOSTNAME+"=?");
			String query = sqlQueryObject.createSQLDelete();
			pstmt = con.prepareStatement(query);
			int index = 1;
			pstmt.setString(index++, this.op2Properties.getClusterHostname());
			int row = pstmt.executeUpdate();
			pstmt.close(); pstmt = null;
			
			String msg = "Eliminato hostname '"+this.op2Properties.getClusterHostname()+"' dalla tabella '"+CostantiDB.CONFIG_NODI_RUNTIME+"' (update-row: "+row+")";
			log.debug(msg);
			
			if(!con.getAutoCommit()) {
				con.commit();
			}
		}
		catch(Exception e) {
			
			try {
				if(con!=null && !con.getAutoCommit()) {
					con.rollback();
				}
			}catch(Exception eClose) {
				// close
			}
			
			throw new CoreException("[DynamicClusterManager.unregister] failed: "+e.getMessage(), e);
		}
		finally {
			try {
				if(pstmt!=null) {
					pstmt.close();
				}
			}catch(Exception eClose) {
				// close
			}
			try {
				if(con!=null) {
					this.driverConfigurazioneDB.releaseConnection(con);
				}
			}catch(Exception eClose) {
				// close
			}
		}
	}
	
	public void refresh(Logger log) throws CoreException {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = this.driverConfigurazioneDB.getConnection("DynamicClusterManager.refresh");
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driverConfigurazioneDB.getTipoDB());
			sqlQueryObject.addUpdateTable(CostantiDB.CONFIG_NODI_RUNTIME);
			sqlQueryObject.addUpdateField(CostantiDB.CONFIG_NODI_RUNTIME_COLUMN_DATA_REFRESH, "?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.CONFIG_NODI_RUNTIME_COLUMN_HOSTNAME+"=?");
			String update = sqlQueryObject.createSQLUpdate();
			pstmt = con.prepareStatement(update);
			int index = 1;
			Timestamp now = DateManager.getTimestamp();
			pstmt.setTimestamp(index++, now);
			pstmt.setString(index++, this.op2Properties.getClusterHostname());
			int row = pstmt.executeUpdate();
			pstmt.close(); pstmt = null;
			
			if(!con.getAutoCommit()) {
				con.commit();
			}
			
			String msg = "Aggiornato hostname '"+this.op2Properties.getClusterHostname()+"'"+getSuffixMessageUpdate(row);
			log.debug(msg);
		}
		catch(Exception e) {
			
			try {
				if(con!=null && !con.getAutoCommit()) {
					con.rollback();
				}
			}catch(Exception eClose) {
				// close
			}
			
			throw new CoreException("[DynamicClusterManager.refresh] failed: "+e.getMessage(), e);
		}
		finally {
			try {
				if(pstmt!=null) {
					pstmt.close();
				}
			}catch(Exception eClose) {
				// close
			}
			try {
				if(con!=null) {
					this.driverConfigurazioneDB.releaseConnection(con);
				}
			}catch(Exception eClose) {
				// close
			}
		}
	}
	
	public List<String> getHostnames(Logger log) throws CoreException {
		Connection con = null;
		String gruppo = null;
		String oldData = null;
		try {
			gruppo = this.op2Properties.getGroupId(this.rateLimitingGestioneCluster);
			Timestamp oldest = getTimestampRefresh(this.op2Properties.getClusterDinamicoRefreshSecondsInterval());
			oldData = DateUtils.getSimpleDateFormatMs().format(oldest);
			con = this.driverConfigurazioneDB.getConnection("DynamicClusterManager.refresh");
			
			List<String> l = getHostnames(con, this.driverConfigurazioneDB.getTipoDB(),
					gruppo, 
					oldest 
					);
			if(l.isEmpty()) {
				throw new CoreException("nodes not found");
			}
			return l;
		}
		catch(Exception e) {
			if(log!=null) {
				// per adesso rilancio l'eccezione
			}
			throw new CoreException("[DynamicClusterManager.getHostnames] (groupId:"+gruppo+" oldestDate:"+oldData+") failed: "+e.getMessage(), e);
		}
		finally {
			try {
				if(con!=null) {
					this.driverConfigurazioneDB.releaseConnection(con);
				}
			}catch(Exception eClose) {
				// close
			}
		}
	}
	
	private static Timestamp getTimestampRefresh(int refreshSecondsInterval) {
		int oldSeconds = refreshSecondsInterval*2; // raddoppio il tempo di refresh
		// Timestamp oldest
		return new Timestamp(System.currentTimeMillis()-(oldSeconds*1000));
	}
	
	public static List<String> getHostnames(Connection con, String tipoDB, String gruppo, int refreshSecondsInterval) throws CoreException {
		Timestamp oldest = getTimestampRefresh(refreshSecondsInterval);
		return getHostnames(con, tipoDB, gruppo, oldest);
	}
	public static List<String> getHostnames(Connection con, String tipoDB, String gruppo, Timestamp oldest) throws CoreException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			List<String> list = new ArrayList<>();
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_NODI_RUNTIME);
			sqlQueryObject.addSelectField(CostantiDB.CONFIG_NODI_RUNTIME_COLUMN_HOSTNAME);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.CONFIG_NODI_RUNTIME_COLUMN_GRUPPO+"=?");
			sqlQueryObject.addWhereCondition(CostantiDB.CONFIG_NODI_RUNTIME_COLUMN_DATA_REFRESH+">?");
			sqlQueryObject.addOrderBy(CostantiDB.CONFIG_NODI_RUNTIME_COLUMN_DATA_REFRESH, true); // parto dal piu' vecchio
			String update = sqlQueryObject.createSQLQuery();
			pstmt = con.prepareStatement(update);
			int index = 1;
			pstmt.setString(index++, gruppo);
			pstmt.setTimestamp(index++, oldest);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				String hostname = rs.getString(CostantiDB.CONFIG_NODI_RUNTIME_COLUMN_HOSTNAME);
				list.add(hostname);
			}
			rs.close(); rs = null;
			pstmt.close(); pstmt = null;
			return list;
		}
		catch(Exception e) {
			throw new CoreException("[DynamicClusterManager.refresh] failed: "+e.getMessage(), e);
		}
		finally {
			try {
				if(rs!=null) {
					rs.close();
				}
			}catch(Exception eClose) {
				// close
			}
			try {
				if(pstmt!=null) {
					pstmt.close();
				}
			}catch(Exception eClose) {
				// close
			}
		}
	}
	
	public static String hashClusterId(String id) {
		return DigestUtils.sha256Hex(id);
	}
	
	
	
	
	
	
	
	public void registerOperation(Logger log, String descrizione, String operazione) throws CoreException {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			if(descrizione==null) {
				throw new CoreException("Param 'descrizione' undefined");
			}
			if(operazione==null) {
				throw new CoreException("Param 'operazione' undefined");
			}
			
			con = this.driverConfigurazioneDB.getConnection("DynamicClusterManager.registerOperation");
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driverConfigurazioneDB.getTipoDB());
			sqlQueryObject.addInsertTable(CostantiDB.CONFIG_NODI_RUNTIME_OPERATIONS);
			sqlQueryObject.addInsertField(CostantiDB.CONFIG_NODI_RUNTIME_OPERATIONS_COLUMN_DESCRIZIONE, "?");
			sqlQueryObject.addInsertField(CostantiDB.CONFIG_NODI_RUNTIME_OPERATIONS_COLUMN_OPERAZIONE, "?");
			sqlQueryObject.addInsertField(CostantiDB.CONFIG_NODI_RUNTIME_OPERATIONS_COLUMN_DATA_REGISTRAZIONE, "?");
			String insert = sqlQueryObject.createSQLInsert();
			pstmt = con.prepareStatement(insert);
			int index = 1;
			pstmt.setString(index++, descrizione);
			pstmt.setString(index++, operazione);
			Timestamp now = DateManager.getTimestamp();
			pstmt.setTimestamp(index++, now);
			int row = pstmt.executeUpdate();
			
			String msg = "Registrata operazione '"+descrizione+"'"+getSuffixMessageUpdate(row);
			log.debug(msg);
			
			if(!con.getAutoCommit()) {
				con.commit();
			}
		}
		catch(Exception e) {
			
			try {
				if(con!=null && !con.getAutoCommit()) {
					con.rollback();
				}
			}catch(Exception eClose) {
				// close
			}
			
			throw new CoreException("[DynamicClusterManager.registerOperation] failed: "+e.getMessage(), e);
		}
		finally {
			try {
				if(pstmt!=null) {
					pstmt.close();
				}
			}catch(Exception eClose) {
				// close
			}
			try {
				if(con!=null) {
					this.driverConfigurazioneDB.releaseConnection(con);
				}
			}catch(Exception eClose) {
				// close
			}
		}
	}
	
	
	public List<ProxyOperation> findRemoteOperations(Date dataRegistrazione, Date now, int offset, int limit) throws CoreException {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			checkDate(dataRegistrazione, now);
			
			con = this.driverConfigurazioneDB.getConnection("DynamicClusterManager.findRemoteOperations");
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driverConfigurazioneDB.getTipoDB());
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_NODI_RUNTIME_OPERATIONS);
			sqlQueryObject.addSelectField(CostantiDB.CONFIG_NODI_RUNTIME_OPERATIONS_COLUMN_DESCRIZIONE);
			sqlQueryObject.addSelectField(CostantiDB.CONFIG_NODI_RUNTIME_OPERATIONS_COLUMN_OPERAZIONE);
			sqlQueryObject.addSelectField(CostantiDB.CONFIG_NODI_RUNTIME_OPERATIONS_COLUMN_DATA_REGISTRAZIONE);
			sqlQueryObject.addWhereCondition(CostantiDB.CONFIG_NODI_RUNTIME_OPERATIONS_COLUMN_DATA_REGISTRAZIONE+">=?");
			sqlQueryObject.addWhereCondition(CostantiDB.CONFIG_NODI_RUNTIME_OPERATIONS_COLUMN_DATA_REGISTRAZIONE+"<?");
			boolean asc = true;
			sqlQueryObject.addOrderBy(CostantiDB.CONFIG_NODI_RUNTIME_OPERATIONS_COLUMN_DATA_REGISTRAZIONE, asc);
			sqlQueryObject.setANDLogicOperator(true);
			if(offset>=0) {
				sqlQueryObject.setOffset(offset);
			}
			if(limit>0) {
				sqlQueryObject.setLimit(limit);
			}
			
			String query = sqlQueryObject.createSQLQuery();
			
			List<ProxyOperation> list = new ArrayList<>();
			
			pstmt = con.prepareStatement(query);
			int index = 1;
			pstmt.setTimestamp(index++, new Timestamp(dataRegistrazione.getTime()));
			pstmt.setTimestamp(index++, new Timestamp(now.getTime()));
			rs = pstmt.executeQuery();
			while(rs.next()) {
				list.add(readProxyOperation(rs));
			}
			
			return list;
		}
		catch(Exception e) {
			
			try {
				if(con!=null && !con.getAutoCommit()) {
					con.rollback();
				}
			}catch(Exception eClose) {
				// close
			}
			
			throw new CoreException("[DynamicClusterManager.findRemoteOperations] failed: "+e.getMessage(), e);
		}
		finally {
			try {
				if(rs!=null) {
					rs.close();
				}
			}catch(Exception eClose) {
				// close
			}
			try {
				if(pstmt!=null) {
					pstmt.close();
				}
			}catch(Exception eClose) {
				// close
			}
			try {
				if(con!=null) {
					this.driverConfigurazioneDB.releaseConnection(con);
				}
			}catch(Exception eClose) {
				// close
			}
		}
	}
	private void checkDate(Date dataRegistrazione, Date now) throws CoreException {
		if(dataRegistrazione==null) {
			throw new CoreException("Param 'dataRegistrazione' undefined");
		}
		if(now==null) {
			throw new CoreException("Param 'now' undefined");
		}
	}
	private ProxyOperation readProxyOperation(ResultSet rs) throws SQLException, CoreException {
		if(rs==null) {
			throw new CoreException("Param 'rs' undefined");
		}
		
		String descrizione = rs.getString(CostantiDB.CONFIG_NODI_RUNTIME_OPERATIONS_COLUMN_DESCRIZIONE);
		String operazione = rs.getString(CostantiDB.CONFIG_NODI_RUNTIME_OPERATIONS_COLUMN_OPERAZIONE);
		Date dataRegistrazioneReaded = rs.getTimestamp(CostantiDB.CONFIG_NODI_RUNTIME_OPERATIONS_COLUMN_DATA_REGISTRAZIONE);
		ProxyOperation po = new ProxyOperation();
		po.setCommand(operazione);
		po.setDescription(descrizione);
		po.setRegistrationTime(dataRegistrazioneReaded);
		return po;
	}
	
	public int deleteRemoteOperations(Date dataRegistrazione) throws CoreException {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			if(dataRegistrazione==null) {
				throw new CoreException("Param 'dataRegistrazione' undefined");
			}
			
			con = this.driverConfigurazioneDB.getConnection("DynamicClusterManager.deleteRemoteOperations");
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driverConfigurazioneDB.getTipoDB());
			sqlQueryObject.addDeleteTable(CostantiDB.CONFIG_NODI_RUNTIME_OPERATIONS);
			sqlQueryObject.addWhereCondition(CostantiDB.CONFIG_NODI_RUNTIME_OPERATIONS_COLUMN_DATA_REGISTRAZIONE+"<?");
			sqlQueryObject.setANDLogicOperator(true);
			
			String query = sqlQueryObject.createSQLDelete();
			
			pstmt = con.prepareStatement(query);
			int index = 1;
			pstmt.setTimestamp(index++, new Timestamp(dataRegistrazione.getTime()));
			return pstmt.executeUpdate();
		}
		catch(Exception e) {
			
			try {
				if(con!=null && !con.getAutoCommit()) {
					con.rollback();
				}
			}catch(Exception eClose) {
				// close
			}
			
			throw new CoreException("[DynamicClusterManager.deleteRemoteOperations] failed: "+e.getMessage(), e);
		}
		finally {
			try {
				if(pstmt!=null) {
					pstmt.close();
				}
			}catch(Exception eClose) {
				// close
			}
			try {
				if(con!=null) {
					this.driverConfigurazioneDB.releaseConnection(con);
				}
			}catch(Exception eClose) {
				// close
			}
		}
	}
}
