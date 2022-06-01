/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.id.apache.serial.MaxReachedException;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
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
			
			long gestioneSerializableDB_AttesaAttiva = this.op2Properties.getGestioneSerializableDB_AttesaAttiva();
			int gestioneSerializableDB_CheckInterval = this.op2Properties.getGestioneSerializableDB_CheckInterval();
			long scadenzaWhile = DateManager.getTimeMillis() + gestioneSerializableDB_AttesaAttiva;
			
			Exception eLast = null;
			
			while(updateEffettuato==false && DateManager.getTimeMillis() < scadenzaWhile){

				try{
					this._register(con, log);

					updateEffettuato = true;

				} 
				catch(MaxReachedException maxReached) {
					throw maxReached;
				}
				catch(Exception e) {
					eLast = e;
					//System.out.println("Serializable error:"+e.getMessage());
				}

				if(updateEffettuato == false){
					// Per aiutare ad evitare conflitti
					try{
						Utilities.sleep((new java.util.Random()).nextInt(gestioneSerializableDB_CheckInterval)); // random da 0ms a checkIntervalms
					}catch(Exception eRandom){}
				}
			}
						
			if(updateEffettuato == false){
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
				er.printStackTrace(System.out);
				//throw new CoreException("(ripristinoIsolation) "+er.getMessage(),er);
			}
			try{
				con.setAutoCommit(true);
			}catch(Exception eRollback){
				//eRollback.printStackTrace(System.out);
			}
			try {
				if(con!=null) {
					this.driverConfigurazioneDB.releaseConnection(con);
				}
			}catch(Throwable eClose) {}
		}
	}
	
	private void _register(Connection con, Logger log) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driverConfigurazioneDB.getTipoDB());
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_NODI_RUNTIME);
			sqlQueryObject.addSelectField("id_numerico");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition("hostname=?");
			String query = sqlQueryObject.createSQLQuery();
			pstmt = con.prepareStatement(query);
			int index = 1;
			pstmt.setString(index++, this.op2Properties.getClusterHostname());
			rs = pstmt.executeQuery();
			boolean find = rs.next();
			if(find) {
				this.identificativoNumerico = rs.getInt("id_numerico");
			}
			rs.close(); rs = null;
			pstmt.close(); pstmt = null;
			
			if(find) {
				// Update
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driverConfigurazioneDB.getTipoDB());
				sqlQueryObject.addUpdateTable(CostantiDB.CONFIG_NODI_RUNTIME);
				sqlQueryObject.addUpdateField("gruppo", "?");
				sqlQueryObject.addUpdateField("data_registrazione", "?");
				sqlQueryObject.addUpdateField("data_refresh", "?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addWhereCondition("hostname=?");
				String update = sqlQueryObject.createSQLUpdate();
				pstmt = con.prepareStatement(update);
				index = 1;
				pstmt.setString(index++, this.op2Properties.getGroupId(this.rateLimitingGestioneCluster));
				Timestamp now = DateManager.getTimestamp();
				pstmt.setTimestamp(index++, now);
				pstmt.setTimestamp(index++, now);
				pstmt.setString(index++, this.op2Properties.getClusterHostname());
				int row = pstmt.executeUpdate();
				pstmt.close(); pstmt = null;
				
				log.debug("Registrato con aggiormanento hostname '"+this.op2Properties.getClusterHostname()+"' nella tabella '"+CostantiDB.CONFIG_NODI_RUNTIME+"' (update-row: "+row+")");
			}
			else {
				
				// Cerco idNumerico disponibile
				//if(this.op2Properties.isClusterIdNumericoDinamico()) { FIX: va sempre calcolato per l'unique del db
				this.identificativoNumerico = this._getNextIdNumerico(con);
				//}
				
				// Insert
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driverConfigurazioneDB.getTipoDB());
				sqlQueryObject.addInsertTable(CostantiDB.CONFIG_NODI_RUNTIME);
				sqlQueryObject.addInsertField("hostname", "?");
				sqlQueryObject.addInsertField("gruppo", "?");
				sqlQueryObject.addInsertField("data_registrazione", "?");
				sqlQueryObject.addInsertField("data_refresh", "?");
				sqlQueryObject.addInsertField("id_numerico", "?");
				String insert = sqlQueryObject.createSQLInsert();
				pstmt = con.prepareStatement(insert);
				index = 1;
				pstmt.setString(index++, this.op2Properties.getClusterHostname());
				pstmt.setString(index++, this.op2Properties.getGroupId(this.rateLimitingGestioneCluster));
				Timestamp now = DateManager.getTimestamp();
				pstmt.setTimestamp(index++, now);
				pstmt.setTimestamp(index++, now);
				pstmt.setInt(index++, this.identificativoNumerico);
				int row = pstmt.executeUpdate();
				pstmt.close(); pstmt = null;
				
				log.debug("Registrato hostname '"+this.op2Properties.getClusterHostname()+"' nella tabella '"+CostantiDB.CONFIG_NODI_RUNTIME+"' (update-row: "+row+")");
			}
			
			con.commit();
		}
		catch(Exception e) {
			try {
				if(rs!=null) {
					rs.close(); rs = null;
				}
			}catch(Throwable eClose) {}
			try {
				if(pstmt!=null) {
					pstmt.close(); pstmt = null;
				}
			}catch(Throwable eClose) {}
			try{
				con.rollback();
			} catch(Throwable er) {}
			throw e;
		}
		finally {
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
	}
	private int _getNextIdNumerico(Connection con) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			// Cerco idNumerico disponibile
			int idNumerico = -1;
			int cifre = this.op2Properties.getClusterDinamicoIdNumericoCifre(this.rateLimitingGestioneCluster);
			StringBuilder sbCifre = new StringBuilder();
			for (int i = 0; i < cifre; i++) {
				sbCifre.append("9");
			}
			int max = Integer.valueOf(sbCifre.toString());
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driverConfigurazioneDB.getTipoDB());
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_NODI_RUNTIME);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addSelectCountField("id_numerico", "countid");
			sqlQueryObject.addSelectMaxField("id_numerico", "maxid");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition("gruppo=?");
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
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driverConfigurazioneDB.getTipoDB());
				sqlQueryObject.addFromTable(CostantiDB.CONFIG_NODI_RUNTIME);
				sqlQueryObject.addSelectField("id_numerico");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addWhereCondition("gruppo=?");
				sqlQueryObject.addOrderBy("id_numerico", true);
				query = sqlQueryObject.createSQLQuery();
				pstmt = con.prepareStatement(query);
				index = 1;
				pstmt.setString(index++, this.op2Properties.getGroupId(this.rateLimitingGestioneCluster));
				rs = pstmt.executeQuery();
				int search = 0;
				while(rs.next()) {
					int idNumericoExists = rs.getInt("id_numerico");
					if(search!=idNumericoExists) {
						idNumerico = search;
						break;
					}
					search++;
					if(search>max) {
						throw new MaxReachedException("Numero massimo di nodi registrabili ("+(max+1)+") raggiunto");
					}
				}
				rs.close(); rs = null;
				pstmt.close(); pstmt = null;
			}

			return idNumerico;
		}
		finally {
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
	}
	
	public void unregister(Logger log) throws CoreException {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = this.driverConfigurazioneDB.getConnection("DynamicClusterManager.unregister");
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driverConfigurazioneDB.getTipoDB());
			sqlQueryObject.addDeleteTable(CostantiDB.CONFIG_NODI_RUNTIME);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition("hostname=?");
			String query = sqlQueryObject.createSQLDelete();
			pstmt = con.prepareStatement(query);
			int index = 1;
			pstmt.setString(index++, this.op2Properties.getClusterHostname());
			int row = pstmt.executeUpdate();
			pstmt.close(); pstmt = null;
			log.debug("Eliminato hostname '"+this.op2Properties.getClusterHostname()+"' dalla tabella '"+CostantiDB.CONFIG_NODI_RUNTIME+"' (update-row: "+row+")");
			
			if(!con.getAutoCommit()) {
				con.commit();
			}
		}
		catch(Exception e) {
			
			try {
				if(!con.getAutoCommit()) {
					con.rollback();
				}
			}catch(Throwable eClose) {}
			
			throw new CoreException("[DynamicClusterManager.unregister] failed: "+e.getMessage(), e);
		}
		finally {
			try {
				if(pstmt!=null) {
					pstmt.close();
				}
			}catch(Throwable eClose) {}
			try {
				if(con!=null) {
					this.driverConfigurazioneDB.releaseConnection(con);
				}
			}catch(Throwable eClose) {}
		}
	}
	
	public void refresh(Logger log) throws CoreException {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = this.driverConfigurazioneDB.getConnection("DynamicClusterManager.refresh");
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driverConfigurazioneDB.getTipoDB());
			sqlQueryObject.addUpdateTable(CostantiDB.CONFIG_NODI_RUNTIME);
			sqlQueryObject.addUpdateField("data_refresh", "?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition("hostname=?");
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
			
			log.debug("Aggiornato hostname '"+this.op2Properties.getClusterHostname()+"' nella tabella '"+CostantiDB.CONFIG_NODI_RUNTIME+"' (update-row: "+row+")");
		}
		catch(Exception e) {
			
			try {
				if(!con.getAutoCommit()) {
					con.rollback();
				}
			}catch(Throwable eClose) {}
			
			throw new CoreException("[DynamicClusterManager.refresh] failed: "+e.getMessage(), e);
		}
		finally {
			try {
				if(pstmt!=null) {
					pstmt.close();
				}
			}catch(Throwable eClose) {}
			try {
				if(con!=null) {
					this.driverConfigurazioneDB.releaseConnection(con);
				}
			}catch(Throwable eClose) {}
		}
	}
	
	public List<String> getHostnames(Logger log) throws CoreException {
		Connection con = null;
		String gruppo = null;
		String oldData = null;
		try {
			gruppo = this.op2Properties.getGroupId(this.rateLimitingGestioneCluster);
			Timestamp oldest = _getTimestampRefresh(this.op2Properties.getClusterDinamicoRefreshSecondsInterval());
			oldData = DateUtils.getSimpleDateFormatMs().format(oldest);
			con = this.driverConfigurazioneDB.getConnection("DynamicClusterManager.refresh");
			
			List<String> l = getHostnames(con, this.driverConfigurazioneDB.getTipoDB(),
					gruppo, 
					oldest 
					);
			if(l==null || l.isEmpty()) {
				throw new Exception("nodes not found");
			}
			return l;
		}
		catch(Exception e) {
			throw new CoreException("[DynamicClusterManager.getHostnames] (groupId:"+gruppo+" oldestDate:"+oldData+") failed: "+e.getMessage(), e);
		}
		finally {
			try {
				if(con!=null) {
					this.driverConfigurazioneDB.releaseConnection(con);
				}
			}catch(Throwable eClose) {}
		}
	}
	
	private static Timestamp _getTimestampRefresh(int refreshSecondsInterval) {
		int oldSeconds = refreshSecondsInterval*2; // raddoppio il tempo di refresh
		Timestamp oldest = new Timestamp(System.currentTimeMillis()-(oldSeconds*1000));
		return oldest;
	}
	
	public static List<String> getHostnames(Connection con, String tipoDB, String gruppo, int refreshSecondsInterval) throws CoreException {
		Timestamp oldest = _getTimestampRefresh(refreshSecondsInterval);
		return getHostnames(con, tipoDB, gruppo, oldest);
	}
	public static List<String> getHostnames(Connection con, String tipoDB, String gruppo, Timestamp oldest) throws CoreException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			List<String> list = new ArrayList<>();
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_NODI_RUNTIME);
			sqlQueryObject.addSelectField("hostname");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition("gruppo=?");
			sqlQueryObject.addWhereCondition("data_refresh>?");
			sqlQueryObject.addOrderBy("data_refresh", true); // parto dal piu' vecchio
			String update = sqlQueryObject.createSQLQuery();
			pstmt = con.prepareStatement(update);
			int index = 1;
			pstmt.setString(index++, gruppo);
			pstmt.setTimestamp(index++, oldest);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				String hostname = rs.getString("hostname");
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
			}catch(Throwable eClose) {}
			try {
				if(pstmt!=null) {
					pstmt.close();
				}
			}catch(Throwable eClose) {}
		}
	}
	
	public static String hashClusterId(String id) {
		return DigestUtils.sha1Hex(id);
	}
}
