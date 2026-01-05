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

package org.openspcoop2.pdd.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;

import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.utils.SemaphoreLock;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.digest.DigestType;
import org.openspcoop2.utils.id.serial.InfoStatistics;
import org.openspcoop2.utils.semaphore.Semaphore;
import org.openspcoop2.utils.semaphore.SemaphoreConfiguration;
import org.openspcoop2.utils.semaphore.SemaphoreMapping;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DigestServiceParamsDriver
 *
 * @author Burlon Tommaso (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DigestServiceParamsDriver {

	private static final String DB_LOCK_ID = "ServiziDigestParamsUpdate";
	private static final org.openspcoop2.utils.Semaphore THREAD_LOCK = new org.openspcoop2.utils.Semaphore("DigestServiceParamsDriver-threadLock");
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private DriverConfigurazioneDB driverConfigurazioneDB;

	private String getKey(IDServizio idServizio, Long serialNumber) {
		return "DigestServiceParams" + idServizio.toString() + "-" + serialNumber;
	}
	
	private Long getIdServizio(Connection conn, String tipoDB, IDServizio idServizio) throws SQLQueryObjectException, SQLException {
		ISQLQueryObject query = SQLObjectFactory.createSQLQueryObject(tipoDB); 
		query.addFromTable(CostantiDB.SOGGETTI);
		query.addSelectField("id");
		query.setANDLogicOperator(true);
		query.addWhereCondition(CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO + "= ?");
		query.addWhereCondition(CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO + "= ?");
		
		Long idSoggetto = null;
		try( PreparedStatement stmt = conn.prepareStatement(query.createSQLQuery())) {
			int index = 1;
			stmt.setString(index++, idServizio.getSoggettoErogatore().getNome());
			stmt.setString(index++, idServizio.getSoggettoErogatore().getTipo());
			
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					idSoggetto = rs.getLong("id");
				}
			}
		}
		
		if (idSoggetto == null)
			return null;
		
		query = SQLObjectFactory.createSQLQueryObject(tipoDB); 
		query.addFromTable(CostantiDB.SERVIZI);
		query.addSelectField("id");
		query.setANDLogicOperator(true);
		query.addWhereCondition(CostantiDB.SERVIZI_COLUMN_NOME_SERVIZIO + "= ?");
		query.addWhereCondition(CostantiDB.SERVIZI_COLUMN_TIPO_SERVIZIO + "= ?");
		query.addWhereCondition(CostantiDB.SERVIZI_COLUMN_VERSIONE_SERVIZIO + "= ?");
		query.addWhereCondition(CostantiDB.SERVIZI_COLUMN_ID_SOGGETTO_REF + "= ?");
		try( PreparedStatement stmt = conn.prepareStatement(query.createSQLQuery())) {
			int index = 1;
			stmt.setString(index++, idServizio.getNome());
			stmt.setString(index++, idServizio.getTipo());
			stmt.setInt(index++, idServizio.getVersione());
			stmt.setLong(index++, idSoggetto);
			
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getLong("id");
				}
			}
		}
		
		return null;
	}
	
	private DigestServiceParams paramsFromResultSet(IDServizio idServizio, ResultSet rs) throws SQLException {
		DigestServiceParams params = new DigestServiceParams();
		params.setIdServizio(idServizio);
		params.setSerialNumber(rs.getLong(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_SERIAL_NUMBER));
		params.setDataRegistrazione(rs.getTimestamp(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_DATE).toInstant());
		params.setDurata(rs.getInt(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_PERIOD));
		params.setSeed(rs.getString(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_SEED).getBytes());
		params.setDigestAlgorithm(DigestType.valueOf(rs.getString(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_ALGORITHM)));
		return params;
	}
	
	public DigestServiceParamsDriver(DriverConfigurazioneDB driverConfigurazioneDB) {
		this.driverConfigurazioneDB = driverConfigurazioneDB;
	}
	
	/**
	 * Rimuove tutte le informazioni crittografiche per la generazione dei digest per un determinato
	 * servizio.
	 * @param idServizio servizio di cui rimuovere le informazioni crittografiche
	 * @return
	 * @throws DriverConfigurazioneException nel caso la rimozione non avvenga correttamente
	 */
	public boolean removeEntries(IDServizio idServizio) throws DriverConfigurazioneException {
		Connection conn = null;
		String tipoDB = this.driverConfigurazioneDB.getTipoDB();
		try { 
			conn = this.driverConfigurazioneDB.getConnection("removeEntries");
			
			// ottengo l'id di riferimento del servizio
			Long idServizioRef = this.getIdServizio(conn, tipoDB, idServizio);
			if (idServizioRef == null)
				return false;
			
			ISQLQueryObject query = SQLObjectFactory.createSQLQueryObject(tipoDB); 
			query.addDeleteTable(CostantiDB.SERVIZI_DIGEST_PARAMS);
			query.addWhereCondition(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_ID_SERVIZIO_REF + "= ?");
			
			try (PreparedStatement stmt = conn.prepareStatement(query.createSQLDelete())) {
				stmt.setLong(1, idServizioRef);
				stmt.execute();
			}
		} catch (SQLQueryObjectException | SQLException e) {
			throw new DriverConfigurazioneException("Errore nella rimozione di un record di tipo DigestServiceParam", e);
		} finally {
			this.driverConfigurazioneDB.closeConnection(conn);
		}
		
		return true;
	}
	
	
	/**
	 * Rimuove partendo dalle informazioni piu vecchie tutte le informazioni crittografiche relative ad un determinato 
	 * servizio fino ad averne al piu n
	 * @param idServizio: id del servizio relativo
	 * @param n: numero di informazioni massimo da tenere in memoria
	 * @return
	 * @throws DriverConfigurazioneException: nel caso la rimozione non vada a buon fine
	 */
	public boolean removeOldEntries(IDServizio idServizio, int n) throws DriverConfigurazioneException {
		Connection conn = null;
		String tipoDB = this.driverConfigurazioneDB.getTipoDB();
		try { 
			conn = this.driverConfigurazioneDB.getConnection("removeEntries");
			
			// ottengo l'id di riferimento del servizio
			Long idServizioRef = this.getIdServizio(conn, tipoDB, idServizio);
			if (idServizioRef == null)
				return false;
			
			// ottengo il timestamp della n-esimo record
			Timestamp lastQuery = null;
			ISQLQueryObject query = SQLObjectFactory.createSQLQueryObject(tipoDB); 
			query.addFromTable(CostantiDB.SERVIZI_DIGEST_PARAMS);
			query.addSelectField(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_DATE);
			query.addWhereCondition(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_ID_SERVIZIO_REF + "= ?");
			query.addOrderBy(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_DATE, false);
			query.setANDLogicOperator(true);
			query.setLimit(1);
			query.setOffset(n);
			try (PreparedStatement stmt = conn.prepareStatement(query.createSQLQuery())) {
				stmt.setLong(1, idServizioRef);
				
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						lastQuery = rs.getTimestamp(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_DATE);
					}
				}
			}
			
			// se non esiste ci sono meno di n records
			if (lastQuery == null)
				return true;
			
			// elimino tutti i record con un timestamp precedente
			query = SQLObjectFactory.createSQLQueryObject(tipoDB); 
			query.addDeleteTable(CostantiDB.SERVIZI_DIGEST_PARAMS);
			query.setANDLogicOperator(true);
			query.addWhereCondition(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_ID_SERVIZIO_REF + "= ?");
			query.addWhereCondition(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_DATE + "<= ?");
			
			try (PreparedStatement stmt = conn.prepareStatement(query.createSQLDelete())) {
				int index = 1;
				stmt.setLong(index++, idServizioRef);
				stmt.setTimestamp(index++, lastQuery);
				stmt.execute();
			}
		} catch (SQLQueryObjectException | SQLException e) {
			throw new DriverConfigurazioneException("Errore nell'aggiunta di un record di tipo DigestServiceParam", e);
		} finally {
			this.driverConfigurazioneDB.closeConnection(conn);
		}
		
		return true;
	}
	
	/**
	 * Aggiunge una nuova informazioni crittografica, questa funzione deve essere chiamata usando il lock fornito dalla classe,
	 * nel caso l'aggiunta vada a buon fine la nuova informazione aggiunta verra registrata in cache come informazione piu
	 * recente
	 * @param params: informazione crittografica da aggiungere
	 * @return
	 * @throws DriverConfigurazioneException: nel caso l'aggiunta non vada a buon fine
	 */
	public boolean addNewEntry(DigestServiceParams params)  throws DriverConfigurazioneException {
		Connection conn = null;
		String tipoDB = this.driverConfigurazioneDB.getTipoDB();
	
		try { 
			conn = this.driverConfigurazioneDB.getConnection("addEntry");
			
			// ottengo l'id di riferimento del servizio
			Long idServizioRef = this.getIdServizio(conn, tipoDB, params.getIdServizio());
			if (idServizioRef == null)
				return false;
			
			// aggiungo il nuovo elemento nel db
			ISQLQueryObject query = SQLObjectFactory.createSQLQueryObject(tipoDB); 
			query.addInsertTable(CostantiDB.SERVIZI_DIGEST_PARAMS);
			if (params.getSerialNumber() != null)
				query.addInsertField(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_SERIAL_NUMBER, "?");
			query.addInsertField(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_ALGORITHM, "?");
			query.addInsertField(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_DATE, "?");
			query.addInsertField(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_ID_SERVIZIO_REF, "?");
			query.addInsertField(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_PERIOD, "?");
			query.addInsertField(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_SEED, "?");
			
			try (PreparedStatement stmt = conn.prepareStatement(query.createSQLInsert())) {
				int index = 1;
				if (params.getSerialNumber() != null)
					stmt.setLong(index++, params.getSerialNumber());
				stmt.setString(index++, params.getDigestAlgorithm().toString());
				stmt.setTimestamp(index++, Timestamp.from(params.getDataRegistrazione()));
				stmt.setLong(index++, idServizioRef);
				stmt.setInt(index++, params.getDurata());
				stmt.setString(index++, new String(params.getSeed()));
				stmt.execute();
			}
		} catch (SQLQueryObjectException | SQLException e) {
			throw new DriverConfigurazioneException (e);
		} finally {
			this.driverConfigurazioneDB.closeConnection(conn);
		}
		
		// aggiungo i parametri in cache (se ottengo un exception posso ignorarla in quanto non invalida il processo)
		try {
			ConfigurazionePdDReader.getCache().put(getKey(params.getIdServizio(), null), params);
		} catch (UtilsException e) {
			this.logger.warn("Errore nell'aggiunta di un valore in cache", e);
		}
		
		return true;
	}
	
	public boolean isValid(DigestServiceParams param) {
		if (param == null)
			return false;
		Instant now = Instant.now();
		Instant expiration = param
				.getDataRegistrazione()
				.plus(Duration.ofDays(param.getDurata()));
		
		return now.isBefore(expiration);
	}
	
	/**
	 * Funzione che se possible ritorna l'informazione crittografica piu recente se e solo se e' ancora valida
	 * @param idServizio
	 * @return null se l'informazione crittografica piu recente non risulta valida
	 * @throws DriverConfigurazioneException
	 */
	public DigestServiceParams getValidEntry(IDServizio idServizio) throws DriverConfigurazioneException {
		DigestServiceParams param = this.getLastEntry(idServizio);
		if (isValid(param))
			return param;
		return null;
	}
	
	/**
	 * Funzione che ritorna l'informazione crittografica piu recente
	 * @param idServizio
	 * @return
	 * @throws DriverConfigurazioneException
	 */
	public DigestServiceParams getLastEntry(IDServizio idServizio) throws DriverConfigurazioneException {
		return this.getEntry(idServizio, null);
	}
	
	/**
	 * Metodo che ritorna l'informazione crittografica relativa ad un servizio dato un determinato numero seriale
	 * @param idServizio
	 * @param serialNumber
	 * @return
	 * @throws DriverConfigurazioneException
	 */
	public DigestServiceParams getEntry(IDServizio idServizio, Long serialNumber) throws DriverConfigurazioneException {
		Connection conn = null;
		String tipoDB = this.driverConfigurazioneDB.getTipoDB();
		DigestServiceParams param  = null;
		
		try {
			// cerco di ottenere i parametri dalla cache se e solo se il numero seriale e' definito o se il record piu recente e' ancora valido
			param = (DigestServiceParams) ConfigurazionePdDReader.getRawObjectCache(getKey(idServizio, serialNumber));
			if (param != null && (serialNumber != null || isValid(param)))
				return param;
			
			conn = this.driverConfigurazioneDB.getConnection("getEntry");
			
			// ottengo l'id di riferimento del servizio
			Long idServizioRef = this.getIdServizio(conn, tipoDB, idServizio);
			if (idServizioRef == null)
				throw new DriverConfigurazioneException("idServizio riferito non presente nella tabella servizio");
			
			ISQLQueryObject query = SQLObjectFactory.createSQLQueryObject(tipoDB); 
			query.addFromTable(CostantiDB.SERVIZI_DIGEST_PARAMS);
			query.setANDLogicOperator(true);
			query.addWhereCondition(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_ID_SERVIZIO_REF + "= ?");
			if (serialNumber != null)
				query.addWhereCondition(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_SERIAL_NUMBER + "= ?");
			query.addOrderBy(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_DATE, false);
			query.setLimit(1);
			
			try (PreparedStatement stmt = conn.prepareStatement(query.createSQLQuery())) {
				int index = 1;
				stmt.setLong(index++, idServizioRef);
				if (serialNumber != null)
					stmt.setLong(index++, serialNumber);
				stmt.execute();
				
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						param = this.paramsFromResultSet(idServizio, rs);
					}
				}
			}
			
		} catch (SQLQueryObjectException | SQLException e) {
			throw new DriverConfigurazioneException("Errore nella get del DigestServiceParam", e);
		} finally {
			this.driverConfigurazioneDB.closeConnection(conn);
		}
	
		// aggiorno la cache se e solo se il numero seriale e' esplicitato o il record e' valido
		try {
			if (param != null && (serialNumber != null || isValid(param)))
				ConfigurazionePdDReader.getCache().put(getKey(idServizio, serialNumber), param);
		} catch (UtilsException e) {
			this.logger.warn("Errore nell'aggiunta di un valore in cache", e);
		}
		
		return param;
	}
	
	private Semaphore semaphore;
	private SemaphoreLock lock;
	
	private Semaphore getSemaphore() throws UtilsException {
		if (this.semaphore == null) {
			InfoStatistics semaphoreStatistics = new InfoStatistics();
			
			SemaphoreConfiguration config = GestoreMessaggi.newSemaphoreConfiguration(1000, 1000);
			
			this.semaphore = new Semaphore(semaphoreStatistics, SemaphoreMapping.newInstance(DB_LOCK_ID), 
				config,
				TipiDatabase.toEnumConstant(this.driverConfigurazioneDB.getTipoDB()),
				DriverConfigurazioneDB.getCheckLogger());
		}
		
		return this.semaphore;
	}
	
	/**
	 * Ottiene un lock (globale e locale) sulla tabella delle informazioni crittografiche
	 * @param idTransazione, id della transazione corrente
	 * @throws UtilsException
	 * @throws DriverConfigurazioneException
	 */
	public void acquireLock(String idTransazione) throws UtilsException, DriverConfigurazioneException {
		Connection con = null;
		try {
			con = this.driverConfigurazioneDB.getConnection("acquireLockDigestService");
			
			while(!this.getSemaphore().newLock(con, "acquireLockDigestService")) {
				Utilities.sleep(1);
			}
			
			this.lock = THREAD_LOCK.acquire("acquireLock", idTransazione);
			
		} catch (UtilsException | DriverConfigurazioneException e) {
			this.releaseLock();
		} finally {
			this.driverConfigurazioneDB.releaseConnection(con);
		}
	}
	
	/**
	 * Rilascia il lock (globale e locale) sulla tabella delle informazioni crittografiche
	 * @throws UtilsException
	 * @throws DriverConfigurazioneException
	 */
	public void releaseLock() throws UtilsException, DriverConfigurazioneException {
		Connection con = null;
		try {
			THREAD_LOCK.release(this.lock, "acquireLock");
			con = this.driverConfigurazioneDB.getConnection("releaseLockDigestService");
			this.getSemaphore().releaseLock(con, "releaseLockDigestService");
		} finally {
			this.driverConfigurazioneDB.releaseConnection(con);
		}
	}
	
	
	
}
