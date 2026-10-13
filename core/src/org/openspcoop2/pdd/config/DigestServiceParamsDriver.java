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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
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
	
	// intervallo massimo (in secondi) entro cui le informazioni crittografiche correnti presenti in cache vengono riverificate sulla base dati;
	// rappresenta anche il ritardo con cui diventa effettiva una rotazione forzata, in modo che tutti i nodi la conoscano prima che avvenga
	private int cacheRefreshSeconds;

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
	
	private static DigestServiceParams paramsFromResultSet(IDServizio idServizio, ResultSet rs) throws SQLException {
		DigestServiceParams params = new DigestServiceParams();
		params.setIdServizio(idServizio);
		params.setId(rs.getLong(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_ID));
		params.setSerialNumber(rs.getLong(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_SERIAL_NUMBER));
		params.setDataRegistrazione(rs.getTimestamp(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_DATE).toInstant());
		params.setDurata(rs.getInt(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_PERIOD));
		params.setSeed(rs.getString(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_SEED).getBytes());
		params.setDigestAlgorithm(DigestType.valueOf(rs.getString(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_ALGORITHM)));
		Timestamp richiestaRotazione = rs.getTimestamp(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_ROTATION_REQUEST);
		if(richiestaRotazione!=null && richiestaRotazione.getTime()>0) {
			params.setDataRichiestaRotazione(richiestaRotazione.toInstant());
		}
		params.setDataLettura(Instant.now());
		return params;
	}
	
	public DigestServiceParamsDriver(DriverConfigurazioneDB driverConfigurazioneDB, int cacheRefreshSeconds) {
		this.driverConfigurazioneDB = driverConfigurazioneDB;
		this.cacheRefreshSeconds = cacheRefreshSeconds;
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
			query.addInsertField(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_ROTATION_REQUEST, "?");
			
			try (PreparedStatement stmt = conn.prepareStatement(query.createSQLInsert())) {
				int index = 1;
				if (params.getSerialNumber() != null)
					stmt.setLong(index++, params.getSerialNumber());
				stmt.setString(index++, params.getDigestAlgorithm().toString());
				stmt.setTimestamp(index++, Timestamp.from(params.getDataRegistrazione()));
				stmt.setLong(index++, idServizioRef);
				stmt.setInt(index++, params.getDurata());
				stmt.setString(index++, new String(params.getSeed()));
				stmt.setNull(index++, java.sql.Types.TIMESTAMP);
				stmt.execute();
			}
		} catch (SQLQueryObjectException | SQLException e) {
			throw new DriverConfigurazioneException (e);
		} finally {
			this.driverConfigurazioneDB.closeConnection(conn);
		}
		
		// aggiungo i parametri in cache (se ottengo un exception posso ignorarla in quanto non invalida il processo)
		params.setDataLettura(Instant.now());
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
		return Instant.now().isBefore(getScadenza(param, this.cacheRefreshSeconds));
	}
	
	/**
	 * Ritorna la scadenza effettiva delle informazioni crittografiche: la scadenza naturale (data di registrazione + durata)
	 * oppure, se precedente, quella derivante da una rotazione forzata. La rotazione forzata diventa effettiva dopo l'intervallo
	 * di riverifica della cache, in modo che tutti i nodi ne siano a conoscenza prima che avvenga.
	 * @param param
	 * @param cacheRefreshSeconds
	 * @return
	 */
	public static Instant getScadenza(DigestServiceParams param, int cacheRefreshSeconds) {
		Instant expiration = param
				.getDataRegistrazione()
				.plus(Duration.ofDays(param.getDurata()));
		if(param.getDataRichiestaRotazione()!=null) {
			Instant expirationRotazione = param.getDataRichiestaRotazione().plusSeconds(cacheRefreshSeconds);
			if(expirationRotazione.isBefore(expiration)) {
				expiration = expirationRotazione;
			}
		}
		return expiration;
	}
	
	private boolean isCacheRefreshRequired(DigestServiceParams param) {
		// le informazioni crittografiche correnti vengono riverificate periodicamente sulla base dati per conoscere eventuali rotazioni forzate
		return param.getDataLettura()==null || 
				!Instant.now().isBefore(param.getDataLettura().plusSeconds(this.cacheRefreshSeconds));
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
			// e non e' trascorso l'intervallo di riverifica sulla base dati
			param = (DigestServiceParams) ConfigurazionePdDReader.getRawObjectCache(getKey(idServizio, serialNumber));
			if (param != null && (serialNumber != null || (isValid(param) && !isCacheRefreshRequired(param))))
				return param;
			
			// la base dati e' la fonte autorevole: l'eventuale informazione presente in cache non deve essere riutilizzata
			param = null;
			
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
						param = paramsFromResultSet(idServizio, rs);
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
	
	
	
	
	
	
	/* ******** CONSULTAZIONE E ROTAZIONE FORZATA (console) ******** */
	
	// Metodi utilizzati dalla console: accedono solamente alla base dati, senza utilizzare la cache del gateway
	
	private static final String ALIAS_ID_SERVIZIO = "id_servizio_ref";
	
	private static ISQLQueryObject newQueryServiziConSeme(String tipoDB, IDSoggetto soggettoErogatore, String search) throws SQLQueryObjectException {
		ISQLQueryObject existsQuery = SQLObjectFactory.createSQLQueryObject(tipoDB);
		existsQuery.addFromTable(CostantiDB.SERVIZI_DIGEST_PARAMS);
		existsQuery.addSelectField(CostantiDB.SERVIZI_DIGEST_PARAMS, CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_ID);
		existsQuery.addWhereCondition(CostantiDB.SERVIZI_DIGEST_PARAMS+"."+CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_ID_SERVIZIO_REF+" = "+CostantiDB.SERVIZI+".id");
		
		ISQLQueryObject query = SQLObjectFactory.createSQLQueryObject(tipoDB);
		query.addFromTable(CostantiDB.SERVIZI);
		query.addFromTable(CostantiDB.SOGGETTI);
		query.addWhereCondition(CostantiDB.SOGGETTI+".id = "+CostantiDB.SERVIZI+"."+CostantiDB.SERVIZI_COLUMN_ID_SOGGETTO_REF);
		query.addWhereExistsCondition(false, existsQuery);
		if(soggettoErogatore!=null) {
			query.addWhereCondition(CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO+" = ?");
			query.addWhereCondition(CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO+" = ?");
		}
		if(search!=null && !"".equals(search)) {
			query.addWhereCondition(false, 
					query.getWhereLikeCondition(CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO, search, true, true),
					query.getWhereLikeCondition(CostantiDB.SERVIZI+"."+CostantiDB.SERVIZI_COLUMN_NOME_SERVIZIO, search, true, true));
		}
		query.setANDLogicOperator(true);
		return query;
	}
	private static void setParametriQueryServiziConSeme(PreparedStatement stmt, IDSoggetto soggettoErogatore) throws SQLException {
		if(soggettoErogatore!=null) {
			stmt.setString(1, soggettoErogatore.getTipo());
			stmt.setString(2, soggettoErogatore.getNome());
		}
	}
	
	/**
	 * Ritorna il numero di servizi per cui sono presenti informazioni crittografiche
	 * @param driverConfigurazioneDB
	 * @param soggettoErogatore filtro sul soggetto erogatore (opzionale)
	 * @param search filtro sul nome del servizio o del soggetto erogatore (opzionale)
	 * @return
	 * @throws DriverConfigurazioneException
	 */
	public static long countServizi(DriverConfigurazioneDB driverConfigurazioneDB, IDSoggetto soggettoErogatore, String search) throws DriverConfigurazioneException {
		Connection conn = null;
		try {
			conn = driverConfigurazioneDB.getConnection("countServiziDigestParams");
			ISQLQueryObject query = newQueryServiziConSeme(driverConfigurazioneDB.getTipoDB(), soggettoErogatore, search);
			query.addSelectCountField("cont");
			try (PreparedStatement stmt = conn.prepareStatement(query.createSQLQuery())) {
				setParametriQueryServiziConSeme(stmt, soggettoErogatore);
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						return rs.getLong("cont");
					}
				}
			}
			return 0;
		} catch (SQLQueryObjectException | SQLException e) {
			throw new DriverConfigurazioneException("Errore nel conteggio dei servizi con informazioni crittografiche: "+e.getMessage(), e);
		} finally {
			driverConfigurazioneDB.closeConnection(conn);
		}
	}
	
	/**
	 * Ritorna i servizi per cui sono presenti informazioni crittografiche, ognuno con le informazioni piu' recenti (senza il seme)
	 * @param driverConfigurazioneDB
	 * @param soggettoErogatore filtro sul soggetto erogatore (opzionale)
	 * @param search filtro sul nome del servizio o del soggetto erogatore (opzionale)
	 * @param offset
	 * @param limit
	 * @return
	 * @throws DriverConfigurazioneException
	 */
	public static List<DigestServiceParams> listServizi(DriverConfigurazioneDB driverConfigurazioneDB, IDSoggetto soggettoErogatore, String search, int offset, int limit) throws DriverConfigurazioneException {
		Connection conn = null;
		String tipoDB = driverConfigurazioneDB.getTipoDB();
		List<DigestServiceParams> list = new ArrayList<>();
		try {
			conn = driverConfigurazioneDB.getConnection("listServiziDigestParams");
			ISQLQueryObject query = newQueryServiziConSeme(tipoDB, soggettoErogatore, search);
			query.addSelectAliasField(CostantiDB.SERVIZI, "id", ALIAS_ID_SERVIZIO);
			query.addSelectField(CostantiDB.SOGGETTI, CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO);
			query.addSelectField(CostantiDB.SOGGETTI, CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO);
			query.addSelectField(CostantiDB.SERVIZI, CostantiDB.SERVIZI_COLUMN_TIPO_SERVIZIO);
			query.addSelectField(CostantiDB.SERVIZI, CostantiDB.SERVIZI_COLUMN_NOME_SERVIZIO);
			query.addSelectField(CostantiDB.SERVIZI, CostantiDB.SERVIZI_COLUMN_VERSIONE_SERVIZIO);
			query.addOrderBy(CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO);
			query.addOrderBy(CostantiDB.SERVIZI_COLUMN_NOME_SERVIZIO);
			query.addOrderBy(CostantiDB.SERVIZI_COLUMN_VERSIONE_SERVIZIO);
			query.setSortType(true);
			query.setLimit(limit > 0 ? limit : ISQLQueryObject.LIMIT_DEFAULT_VALUE);
			query.setOffset(offset);
			
			List<Long> idServizi = new ArrayList<>();
			List<IDServizio> servizi = new ArrayList<>();
			try (PreparedStatement stmt = conn.prepareStatement(query.createSQLQuery())) {
				setParametriQueryServiziConSeme(stmt, soggettoErogatore);
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						idServizi.add(rs.getLong(ALIAS_ID_SERVIZIO));
						servizi.add(toIDServizio(rs));
					}
				}
			}
			
			for (int i = 0; i < idServizi.size(); i++) {
				List<DigestServiceParams> l = readEntries(conn, tipoDB, idServizi.get(i), servizi.get(i), true);
				if(!l.isEmpty()) {
					list.add(l.get(0));
				}
			}
			return list;
		} catch (SQLQueryObjectException | SQLException | DriverRegistroServiziException e) {
			throw new DriverConfigurazioneException("Errore nella lettura dei servizi con informazioni crittografiche: "+e.getMessage(), e);
		} finally {
			driverConfigurazioneDB.closeConnection(conn);
		}
	}
	
	/**
	 * Ritorna tutte le informazioni crittografiche conservate per un servizio (senza il seme), dalla piu' recente
	 * @param driverConfigurazioneDB
	 * @param idServizioRef identificativo del servizio sulla base dati
	 * @return lista vuota se il servizio non esiste o non possiede informazioni crittografiche
	 * @throws DriverConfigurazioneException
	 */
	public static List<DigestServiceParams> getStorico(DriverConfigurazioneDB driverConfigurazioneDB, long idServizioRef) throws DriverConfigurazioneException {
		Connection conn = null;
		String tipoDB = driverConfigurazioneDB.getTipoDB();
		try {
			conn = driverConfigurazioneDB.getConnection("getStoricoDigestParams");
			IDServizio idServizio = readIDServizio(conn, tipoDB, idServizioRef);
			if(idServizio==null) {
				return new ArrayList<>();
			}
			return readEntries(conn, tipoDB, idServizioRef, idServizio, false);
		} catch (SQLQueryObjectException | SQLException | DriverRegistroServiziException e) {
			throw new DriverConfigurazioneException("Errore nella lettura delle informazioni crittografiche: "+e.getMessage(), e);
		} finally {
			driverConfigurazioneDB.closeConnection(conn);
		}
	}
	
	/**
	 * Richiede la rotazione forzata delle informazioni crittografiche in uso per un servizio.
	 * La rotazione diventa effettiva (su tutti i nodi) trascorso l'intervallo di riverifica della cache del gateway
	 * e viene effettuata, con l'invio del segnale SEEDUPDATE, alla pubblicazione del primo segnale successivo.
	 * @param driverConfigurazioneDB
	 * @param idServizioRef identificativo del servizio sulla base dati
	 * @param idEntry identificativo delle informazioni crittografiche che si intende ruotare; deve corrispondere a quelle in uso
	 * @param dataRichiesta
	 * @return esito della richiesta
	 * @throws DriverConfigurazioneException
	 */
	public static DigestServiceParamsRotationResult richiediRotazione(DriverConfigurazioneDB driverConfigurazioneDB, long idServizioRef, long idEntry, Instant dataRichiesta) throws DriverConfigurazioneException {
		Connection conn = null;
		String tipoDB = driverConfigurazioneDB.getTipoDB();
		try {
			conn = driverConfigurazioneDB.getConnection("richiediRotazioneDigestParams");
			
			IDServizio idServizio = readIDServizio(conn, tipoDB, idServizioRef);
			if(idServizio==null) {
				return DigestServiceParamsRotationResult.SEME_NON_PRESENTE;
			}
			List<DigestServiceParams> l = readEntries(conn, tipoDB, idServizioRef, idServizio, true);
			if(l.isEmpty()) {
				return DigestServiceParamsRotationResult.SEME_NON_PRESENTE;
			}
			DigestServiceParams inUso = l.get(0);
			if(inUso.getId().longValue() != idEntry) {
				return DigestServiceParamsRotationResult.SEME_GIA_RUOTATO;
			}
			if(inUso.getDataRichiestaRotazione()!=null) {
				return DigestServiceParamsRotationResult.ROTAZIONE_GIA_RICHIESTA;
			}
			
			// l'aggiornamento avviene solo se la rotazione non e' stata nel frattempo richiesta da un'altra operazione
			ISQLQueryObject query = SQLObjectFactory.createSQLQueryObject(tipoDB);
			query.addUpdateTable(CostantiDB.SERVIZI_DIGEST_PARAMS);
			query.addUpdateField(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_ROTATION_REQUEST, "?");
			query.addWhereCondition(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_ID+" = ?");
			query.addWhereCondition(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_ID_SERVIZIO_REF+" = ?");
			query.addWhereIsNullCondition(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_ROTATION_REQUEST);
			query.setANDLogicOperator(true);
			int updated = 0;
			try (PreparedStatement stmt = conn.prepareStatement(query.createSQLUpdate())) {
				int index = 1;
				stmt.setTimestamp(index++, Timestamp.from(dataRichiesta));
				stmt.setLong(index++, idEntry);
				stmt.setLong(index++, idServizioRef);
				updated = stmt.executeUpdate();
			}
			return updated > 0 ? DigestServiceParamsRotationResult.ROTAZIONE_RICHIESTA : DigestServiceParamsRotationResult.ROTAZIONE_GIA_RICHIESTA;
		} catch (SQLQueryObjectException | SQLException | DriverRegistroServiziException e) {
			throw new DriverConfigurazioneException("Errore nella richiesta di rotazione delle informazioni crittografiche: "+e.getMessage(), e);
		} finally {
			driverConfigurazioneDB.closeConnection(conn);
		}
	}
	
	private static IDServizio toIDServizio(ResultSet rs) throws SQLException, DriverRegistroServiziException {
		return IDServizioFactory.getInstance().getIDServizioFromValues(
				rs.getString(CostantiDB.SERVIZI_COLUMN_TIPO_SERVIZIO), 
				rs.getString(CostantiDB.SERVIZI_COLUMN_NOME_SERVIZIO), 
				rs.getString(CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO), 
				rs.getString(CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO), 
				rs.getInt(CostantiDB.SERVIZI_COLUMN_VERSIONE_SERVIZIO));
	}
	
	private static IDServizio readIDServizio(Connection conn, String tipoDB, long idServizioRef) throws SQLQueryObjectException, SQLException, DriverRegistroServiziException {
		ISQLQueryObject query = SQLObjectFactory.createSQLQueryObject(tipoDB);
		query.addFromTable(CostantiDB.SERVIZI);
		query.addFromTable(CostantiDB.SOGGETTI);
		query.addSelectField(CostantiDB.SOGGETTI, CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO);
		query.addSelectField(CostantiDB.SOGGETTI, CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO);
		query.addSelectField(CostantiDB.SERVIZI, CostantiDB.SERVIZI_COLUMN_TIPO_SERVIZIO);
		query.addSelectField(CostantiDB.SERVIZI, CostantiDB.SERVIZI_COLUMN_NOME_SERVIZIO);
		query.addSelectField(CostantiDB.SERVIZI, CostantiDB.SERVIZI_COLUMN_VERSIONE_SERVIZIO);
		query.addWhereCondition(CostantiDB.SOGGETTI+".id = "+CostantiDB.SERVIZI+"."+CostantiDB.SERVIZI_COLUMN_ID_SOGGETTO_REF);
		query.addWhereCondition(CostantiDB.SERVIZI+".id = ?");
		query.setANDLogicOperator(true);
		try (PreparedStatement stmt = conn.prepareStatement(query.createSQLQuery())) {
			stmt.setLong(1, idServizioRef);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return toIDServizio(rs);
				}
			}
		}
		return null;
	}
	
	private static List<DigestServiceParams> readEntries(Connection conn, String tipoDB, long idServizioRef, IDServizio idServizio, boolean soloInUso) throws SQLQueryObjectException, SQLException {
		ISQLQueryObject query = SQLObjectFactory.createSQLQueryObject(tipoDB); 
		query.addFromTable(CostantiDB.SERVIZI_DIGEST_PARAMS);
		query.addWhereCondition(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_ID_SERVIZIO_REF + "= ?");
		query.addOrderBy(CostantiDB.SERVIZI_DIGEST_PARAMS_COLUMN_DATE, false);
		query.setANDLogicOperator(true);
		if(soloInUso) {
			query.setLimit(1);
		}
		List<DigestServiceParams> list = new ArrayList<>();
		try (PreparedStatement stmt = conn.prepareStatement(query.createSQLQuery())) {
			stmt.setLong(1, idServizioRef);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					DigestServiceParams param = paramsFromResultSet(idServizio, rs);
					// il seme non viene restituito: viene riportata solamente la sua dimensione
					param.setSeedSize(getSeedSize(param.getSeed()));
					param.setSeed(null);
					param.setIdServizioRef(idServizioRef);
					list.add(param);
				}
			}
		}
		return list;
	}
	
	private static Integer getSeedSize(byte[] seed) {
		// il seme e' memorizzato codificato in base64
		try {
			return Base64.getDecoder().decode(seed).length;
		}catch(Exception e) {
			return null;
		}
	}
	
}
