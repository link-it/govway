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


package org.openspcoop2.web.ctrlstat.driver;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.openspcoop2.core.commons.DBOggettiInUsoUtils;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB_LIB;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationLogger;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.dao.PoliticheSicurezza;
import org.openspcoop2.web.ctrlstat.dao.Ruolo;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.lib.audit.DriverAudit;
import org.openspcoop2.web.lib.audit.DriverAuditDBAppender;
import org.openspcoop2.web.lib.users.DriverUsersDB;

/**
 * DriverControlStationDB
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class DriverControlStationDB  {

	/*  F I E L D S P R I V A T I */

	/** Indicazione di una corretta creazione */
	public boolean create = false;

	// Datasource per la connessione al DB
	public DataSource datasource = null;

	// Connection passata al momento della creazione dell'oggetto
	private Connection globalConnection = null;
	// Variabile di controllo del tipo di operazione da effettuare
	// l'autoCommit viene gestito internamente a questa classe
	private boolean atomica = true;

	/** Logger utilizzato per debug. */
	private org.slf4j.Logger log = null;

	// Tipo database passato al momento della creazione dell'oggetto
	private String tipoDB = null;

	/**
	 * Questo driver incapsula i driver DriverConfiguarazioneDB e
	 * DriverRegistroDB utilizza i loro metodi e ne implementa di nuovi
	 */

	private DriverConfigurazioneDB configDB = null;
	private DriverRegistroServiziDB regservDB = null;
	private DriverUsersDB usersDB = null;
	private DriverAudit auditDB = null;
	private DriverAuditDBAppender auditDBappender = null;

	private IDAccordoFactory idAccordoFactory = null;
	@SuppressWarnings("unused")
	private IDAccordoCooperazioneFactory idAccordoCooperazioneFactory = null;
	
	public String getTipoDatabase() {
		return this.tipoDB;
	}

	public DriverConfigurazioneDB getDriverConfigurazioneDB() {
		return this.configDB;
	}

	public DriverRegistroServiziDB getDriverRegistroServiziDB() {
		return this.regservDB;
	}

	public DriverUsersDB getDriverUsersDB() {
		return this.usersDB;
	}

	public DriverAudit getDriverAuditDB() {
		return this.auditDB;
	}

	public DriverAuditDBAppender getDriverAuditDBAppender() {
		return this.auditDBappender;
	}

	public DriverControlStationDB(Connection connection, Properties context, String tipoDB) throws DriverControlStationException {
		this.log = ControlStationLogger.getDriverDBPddConsoleLogger();

		if (connection == null) {
			this.create = false;
			throw new DriverControlStationException("[DriverControlStationDB::DriverControlStation(Connection con, Properties context, String tipoDB) La connection non puo essere null.");
		}

		if (tipoDB == null) {
			this.create = false;
			throw new DriverControlStationException("[DriverControlStationDB::DriverControlStation(Connection con, Properties context, String tipoDB) Il tipoDatabase non puo essere null.");
		}

		// InitialContext initCtx = new InitialContext(context);
		this.globalConnection = connection;
		this.create = true;
		this.atomica = false;
		this.tipoDB = tipoDB;

		try {
			this.configDB = new DriverConfigurazioneDB(connection, this.tipoDB);
			this.regservDB = new DriverRegistroServiziDB(connection, this.tipoDB);
			this.usersDB = new DriverUsersDB(connection, this.tipoDB);
			this.auditDB = new DriverAudit(connection, this.tipoDB);
			this.auditDBappender = new DriverAuditDBAppender(connection, this.tipoDB);
			this.idAccordoFactory = IDAccordoFactory.getInstance();
			this.idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();
		} catch (Exception e) {
			throw new DriverControlStationException("[DriverControlStationDB::DriverControlStation(Connection con, Properties context, String tipoDB) Errore creando i driver ausiliari.");
		}

		// Setto il tipoDB anche in DriverControlStationDB_LIB
		DriverControlStationDB_LIB.setTipoDB(tipoDB);

		this.log.info("Creato DriverControlStationDB");
	}

	
	
	
	
	/* ***** PDD **** */
	
	public void createPdDControlStation(PdDControlStation pdd) throws DriverControlStationException {
		String nomeMetodo = "createPdd";

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] SQLException accedendo al datasource :" + e.getMessage());

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			DriverControlStationDB_LIB.CRUDPdd(CostantiDB.CREATE, pdd, con);

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}

	}

	public void deletePdDControlStation(PdDControlStation pdd) throws DriverControlStationException {
		String nomeMetodo = "deletePdd";

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] SQLException accedendo al datasource :" + e.getMessage());

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			DriverControlStationDB_LIB.CRUDPdd(CostantiDB.DELETE, pdd, con);

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {

			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}

	}

	public void updatePdDControlStation(PdDControlStation pdd) throws DriverControlStationException {
		String nomeMetodo = "updatePdd";

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] SQLException accedendo al datasource :" + e.getMessage());

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			DriverControlStationDB_LIB.CRUDPdd(CostantiDB.UPDATE, pdd, con);

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}

	}
	
	public List<PdDControlStation> pddList(String superuser, ISearch ricerca) throws DriverControlStationException {
		String nomeMetodo = "pddList";
		int idLista = Liste.PDD;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		ricerca.getSearchString(idLista);

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] SQLException accedendo al datasource :" + e.getMessage());

			}

		} else {
			con = this.globalConnection;
		}

		try {

			if (!search.equals("")) {
				// query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PDD);
				sqlQueryObject.addSelectCountField("*", "cont");
				if (superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PDD);
				sqlQueryObject.addSelectCountField("*", "cont");
				if (superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			if (superuser!=null && (!superuser.equals("")))
				stmt.setString(1, superuser);
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				ricerca.setNumEntries(idLista, risultato.getInt(1));
			}
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) {
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			}
			if (!search.equals("")) {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PDD);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome");
				if (superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PDD);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome");
				if (superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			if (superuser!=null && (!superuser.equals("")))
				stmt.setString(1, superuser);
			risultato = stmt.executeQuery();

			ArrayList<PdDControlStation> lista = new ArrayList<PdDControlStation>();
			PdDControlStation pdd = null;
			while (risultato.next()) {
				long id = risultato.getLong("id");
				pdd = this.getPdDControlStation(id);
				lista.add(pdd);
			}
			risultato.close();
			stmt.close();

			return lista;

		} catch (Exception se) {

			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {

			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}

	}

	/**
	 * 
	 * @param idPdd
	 * @return Pdd
	 * @throws DriverControlStationException
	 */
	public PdDControlStation getPdDControlStation(long idPdd) throws DriverControlStationException, DriverControlStationNotFound {
		String nomeMetodo = "pddList(int idPdd)";

		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] SQLException accedendo al datasource :" + e.getMessage());

			}

		} else {
			con = this.globalConnection;
		}

		try {

			PdDControlStation pdd = new PdDControlStation();

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PDD);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPdd);
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				pdd.setId(risultato.getLong("id"));
				pdd.setNome(risultato.getString("nome"));
				pdd.setDescrizione(risultato.getString("descrizione"));

				pdd.setIp(risultato.getString("ip"));
				pdd.setPorta(risultato.getInt("porta"));
				pdd.setIpGestione(risultato.getString("ip_gestione"));
				pdd.setPortaGestione(risultato.getInt("porta_gestione"));
				pdd.setProtocollo(risultato.getString("protocollo"));
				pdd.setProtocolloGestione(risultato.getString("protocollo_gestione"));

				pdd.setTipo(risultato.getString("tipo"));
				pdd.setImplementazione(risultato.getString("implementazione"));

				pdd.setPassword(risultato.getString("password"));
				pdd.setSubject(risultato.getString("subject"));
				pdd.setClientAuth(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(risultato.getString("client_auth")));

				// Ora Registrazione
				if (risultato.getTimestamp("ora_registrazione") != null) {
					pdd.setOraRegistrazione(new Date(risultato.getTimestamp("ora_registrazione").getTime()));
				}

				pdd.setSuperUser(risultato.getString("superuser"));

			} else {
				throw new DriverControlStationNotFound("Porta di dominio con id " + idPdd + " non trovata.");
			}

			risultato.close();
			stmt.close();

			return pdd;

		} catch (DriverControlStationNotFound e) {
			throw e;
		} catch (Exception se) {

			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}

	}

	public List<org.openspcoop2.core.config.Soggetto> pddSoggettiList(int idPdd, ISearch ricerca) throws DriverControlStationException {
		String nomeMetodo = "pddSoggettiList";
		int idLista = Liste.PDD_SOGGETTI;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		ricerca.getSearchString(idLista);

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] SQLException accedendo al datasource :" + e.getMessage());

			}

		} else {
			con = this.globalConnection;
		}

		ArrayList<org.openspcoop2.core.config.Soggetto> lista = null;

		try {

			// Prendo il nome del pdd
			String nomePdd = "";
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PDD);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setInt(1, idPdd);
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				nomePdd = risultato.getString("nome");
			}
			risultato.close();
			stmt.close();

			if (!search.equals("")) {
				// query con search
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("server = ?");
				sqlQueryObject.addWhereCondition(false, sqlQueryObject.getWhereLikeCondition("nome_soggetto", search, true, true), sqlQueryObject.getWhereLikeCondition("tipo_soggetto", search, true, true));
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("server = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, nomePdd);
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				ricerca.setNumEntries(idLista, risultato.getInt(1));
			}
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) {
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			}
			if (!search.equals("")) {
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField("server");
				sqlQueryObject.addSelectField("nome_soggetto");
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("descrizione");
				sqlQueryObject.addSelectField("identificativo_porta");
				sqlQueryObject.addWhereCondition("server = ?");
				sqlQueryObject.addWhereCondition(false, sqlQueryObject.getWhereLikeCondition("nome_soggetto", search, true, true), sqlQueryObject.getWhereLikeCondition("tipo_soggetto", search, true, true));
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("tipo_soggetto");
				sqlQueryObject.addOrderBy("nome_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField("server");
				sqlQueryObject.addSelectField("nome_soggetto");
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("descrizione");
				sqlQueryObject.addSelectField("identificativo_porta");
				sqlQueryObject.addWhereCondition("server = ?");
				sqlQueryObject.addOrderBy("tipo_soggetto");
				sqlQueryObject.addOrderBy("nome_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, nomePdd);
			risultato = stmt.executeQuery();

			lista = new ArrayList<org.openspcoop2.core.config.Soggetto>();
			org.openspcoop2.core.config.Soggetto sog = null;
			while (risultato.next()) {
				sog = new org.openspcoop2.core.config.Soggetto();

				sog.setId(risultato.getLong("id"));
				sog.setNome(risultato.getString("nome_soggetto"));
				sog.setTipo(risultato.getString("tipo_soggetto"));
				sog.setDescrizione(risultato.getString("descrizione"));
				sog.setIdentificativoPorta(risultato.getString("identificativo_porta"));

				lista.add(sog);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}

	}


	/**
	 * Ritorna una Porta di Dominio tramite il parametro passato
	 * 
	 * @param nomePdd
	 * @return Pdd se esiste una Porta di Dominio con il nome passato, null
	 *         altrimenti
	 * @throws DriverControlStationException
	 */
	public PdDControlStation getPdDControlStation(String nomePdd) throws DriverControlStationException, DriverControlStationNotFound {
		String nomeMetodo = "pddList(String nomePdd)";

		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] SQLException accedendo al datasource :" + e.getMessage());

			}

		} else {
			con = this.globalConnection;
		}

		try {

			PdDControlStation pdd = null;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PDD);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome = ?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, nomePdd);
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				long id = risultato.getLong("id");
				pdd = this.getPdDControlStation(id);
			} else {
				throw new DriverControlStationNotFound("Porta di dominio con nome " + nomePdd + " non trovata.");
			}

			risultato.close();
			stmt.close();

			return pdd;

		} catch (DriverControlStationNotFound e) {
			throw e;
		} catch (Exception se) {

			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	public List<SoggettoCtrlStat> soggettiWithServer(String nomePdD) throws DriverControlStationException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString = "";

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException(e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			SoggettoCtrlStat scs = null;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("server=?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, nomePdD);
			List<SoggettoCtrlStat> scsList = new ArrayList<SoggettoCtrlStat>();
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				org.openspcoop2.core.config.Soggetto soggConf = this.getDriverConfigurazioneDB().getSoggetto(risultato.getLong("id"));
				org.openspcoop2.core.registry.Soggetto soggReg = this.getDriverRegistroServiziDB().getSoggetto(risultato.getLong("id"));
				scs = new SoggettoCtrlStat(soggReg, soggConf);
				scsList.add(scs);
			}
			risultato.close();
			stmt.close();

			return scsList;

		} catch (Exception se) {
			throw new DriverControlStationException(se);
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	
	
	
	
	
	
	
	
	
	
	/* ***** POLITICHE DI SICUREZZA **** */
	
	public Long createPoliticheSicurezza(PoliticheSicurezza ps) throws DriverControlStationException {
		if (ps == null)
			throw new DriverControlStationException("La politica di sicurezza non puo essere nulla");
		if (ps.getIdFruitore() <= 0)
			throw new DriverControlStationException("ID Fruitore della politica di sicurezza non puo essere <=0");
		if (ps.getIdServizio() <= 0)
			throw new DriverControlStationException("ID Servizio della politica di sicurezza non puo essere <=0");

		if (ps.getIdServizioApplicativo() <= 0) {
			if (ps.getNomeServizioApplicativo() == null || "".equals(ps.getNomeServizioApplicativo()))
				throw new DriverControlStationException("ID e Nome Servizio Applicativo della politica di sicurezza sono invalidi");
		}

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		boolean error = false;

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();
				con.setAutoCommit(false);
			} catch (SQLException e) {
				throw new DriverControlStationException("[DriverConfigurazioneDB::createSoggetto] SQLException accedendo al datasource :" + e.getMessage());

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			String queryString = "";
			long idSA = ps.getIdServizioApplicativo();
			if (idSA <= 0) {
				// recuper l'id del servizio applicativo che appartiene al
				// fruitore della politica di sicurezza
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.addWhereCondition("nome = ?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setLong(1, ps.getIdFruitore());
				stmt.setString(2, ps.getNomeServizioApplicativo());
				risultato = stmt.executeQuery();
				if (risultato.next()) {
					idSA = risultato.getInt("id");
				} else {
					throw new DriverControlStationException("Impossibile recuperare l'id del Servizio Applicativo [" + ps.getNomeServizioApplicativo() + "] appartenente a Soggetto con id=" + ps.getIdFruitore());
				}
				risultato.close();
				stmt.close();

				ps.setIdServizioApplicativo(idSA);
			}

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addInsertTable(CostantiDB.POLITICHE_SICUREZZA);
			sqlQueryObject.addInsertField("id_fruitore", "?");
			sqlQueryObject.addInsertField("id_servizio", "?");
			sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
			queryString = sqlQueryObject.createSQLInsert();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, ps.getIdFruitore());
			stmt.setLong(2, ps.getIdServizio());
			stmt.setLong(3, ps.getIdServizioApplicativo());
			stmt.executeUpdate();
			stmt.close();

			long idPS = 0;
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.POLITICHE_SICUREZZA);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_fruitore = ?");
			sqlQueryObject.addWhereCondition("id_servizio = ?");
			sqlQueryObject.addWhereCondition("id_servizio_applicativo = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, ps.getIdFruitore());
			stmt.setLong(2, ps.getIdServizio());
			stmt.setLong(3, ps.getIdServizioApplicativo());
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				idPS = risultato.getLong("id");
			}
			risultato.close();
			stmt.close();

			ps.setId(idPS);

			return ps.getId();

		} catch (Exception qe) {
			error = true;
			throw new DriverControlStationException("[DriverConfigurazioneDB::createSoggetto] Errore durante la creazione della Politica Di Sicurezza : " + qe.getMessage(), qe);
		} finally {

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
					con.commit();
					con.setAutoCommit(true);
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public void deletePoliticheSicurezza(PoliticheSicurezza ps) throws DriverControlStationException {
		if (ps == null)
			throw new DriverControlStationException("La politica di sicurezza non puo essere nulla");
		if (ps.getIdFruitore() <= 0)
			throw new DriverControlStationException("ID Fruitore della politica di sicurezza non puo essere <=0");
		if (ps.getIdServizio() <= 0)
			throw new DriverControlStationException("ID Servizio della politica di sicurezza non puo essere <=0");

		if (ps.getIdServizioApplicativo() <= 0) {
			if (ps.getNomeServizioApplicativo() == null || "".equals(ps.getNomeServizioApplicativo()))
				throw new DriverControlStationException("ID e Nome Servizio Applicativo della politica di sicurezza sono invalidi");
		}

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		boolean error = false;

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();
				con.setAutoCommit(false);
			} catch (SQLException e) {
				throw new DriverControlStationException("[DriverControlStationDB::deletePoliticheSicurezza] SQLException accedendo al datasource :" + e.getMessage());

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			String queryString = "";
			long idSA = ps.getIdServizioApplicativo();
			if (idSA <= 0) {
				// recuper l'id del servizio applicativo che appartiene al
				// fruitore della politica di sicurezza
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.addWhereCondition("nome = ?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setLong(1, ps.getIdFruitore());
				stmt.setString(2, ps.getNomeServizioApplicativo());
				risultato = stmt.executeQuery();
				if (risultato.next()) {
					idSA = risultato.getInt("id");
				} else {
					throw new DriverControlStationException("Impossibile recuperare l'id del Servizio Applicativo [" + ps.getNomeServizioApplicativo() + "] appartenente a Soggetto con id=" + ps.getIdFruitore());
				}
				risultato.close();
				stmt.close();

				ps.setIdServizioApplicativo(idSA);
			}

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.POLITICHE_SICUREZZA);
			sqlQueryObject.addWhereCondition("id_fruitore=?");
			sqlQueryObject.addWhereCondition("id_servizio=?");
			sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, ps.getIdFruitore());
			stmt.setLong(2, ps.getIdServizio());
			stmt.setLong(3, ps.getIdServizioApplicativo());
			stmt.executeUpdate();
			stmt.close();

		} catch (Exception qe) {
			error = true;
			throw new DriverControlStationException("[DriverControlStationDB::deletePoliticheSicurezza] Errore durante la rimozione della Politica Di Sicurezza : " + qe.getMessage(), qe);
		} finally {

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
					con.commit();
					con.setAutoCommit(true);
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public void deleteAllPoliticheSicurezza(long idServizio, long idFruitore) throws DriverControlStationException {

		Connection con = null;
		PreparedStatement stmt = null;
		// ResultSet risultato = null;

		boolean error = false;

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();
				con.setAutoCommit(false);
			} catch (SQLException e) {
				throw new DriverControlStationException("[DriverControlStationDB::deletePoliticheSicurezza] SQLException accedendo al datasource :" + e.getMessage());

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			String queryString = "";

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.POLITICHE_SICUREZZA);
			sqlQueryObject.addWhereCondition("id_fruitore=?");
			sqlQueryObject.addWhereCondition("id_servizio=?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idFruitore);
			stmt.setLong(2, idServizio);
			stmt.executeUpdate();
			stmt.close();

		} catch (Exception qe) {
			error = true;
			throw new DriverControlStationException("[DriverControlStationDB::deleteAllPoliticheSicurezza] Errore durante la rimozione della Politica Di Sicurezza : " + qe.getMessage(), qe);
		} finally {

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
					con.commit();
					con.setAutoCommit(true);
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	public List<ServizioApplicativo> accordiServizioApplicativoList(int idSoggettoFruitoreDelServizio, int idServizio, ISearch ricerca) throws DriverControlStationException {
		String nomeMetodo = "accordiServizioApplicativoList";
		int idLista = Liste.ACCORDI_SERVIZIO_APPLICATIVO;
		// int offset;
		int limit;
		// String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		// offset = ricerca.getIndexIniziale(idLista);
		// search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? ""
		// : ricerca.getSearchString(idLista));
		ricerca.getSearchString(idLista);

		Connection con = null;
		PreparedStatement stmt = null, stmt1 = null;
		ResultSet risultato = null, risultato1 = null;

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] SQLException accedendo al datasource :" + e.getMessage());

			}

		} else {
			con = this.globalConnection;
		}

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.POLITICHE_SICUREZZA);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.addWhereCondition("id_fruitore = ?");
			sqlQueryObject.addWhereCondition("id_servizio = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setInt(1, idSoggettoFruitoreDelServizio);
			stmt.setInt(2, idServizio);
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				ricerca.setNumEntries(idLista, risultato.getInt(1));
			}
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) {
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			}
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.POLITICHE_SICUREZZA);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_fruitore = ?");
			sqlQueryObject.addWhereCondition("id_servizio = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setInt(1, idSoggettoFruitoreDelServizio);
			stmt.setInt(2, idServizio);
			risultato = stmt.executeQuery();

			ArrayList<ServizioApplicativo> lista = new ArrayList<ServizioApplicativo>();
			ServizioApplicativo sa = null;
			while (risultato.next()) {
				// ho la lista degli id del servizio applicativo
				// per ogni id devo recuperare il nome del servizio applicativo
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addWhereCondition("id = ?");
				queryString = sqlQueryObject.createSQLQuery();
				stmt1 = con.prepareStatement(queryString);
				stmt1.setInt(1, risultato.getInt("id_servizio_applicativo"));
				risultato1 = stmt1.executeQuery();
				if (risultato1.next()) {
					sa = new ServizioApplicativo();
					sa.setId(risultato1.getLong("id"));
					sa.setNome(risultato1.getString("nome"));
					lista.add(sa);
				}

				risultato1.close();
				stmt1.close();
			}
			risultato.close();
			stmt.close();

			return lista;

		} catch (Exception se) {

			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {

			// Chiudo statement and resultset
			try {
				if (risultato1 != null) {
					risultato1.close();
				}
				if (stmt1 != null) {
					stmt1.close();
				}
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}

	}

	public List<ServizioApplicativo> serviziServizioApplicativoList(int idSoggettoFruitoreDelServizio, int idServizio, ISearch ricerca) throws DriverControlStationException {
		String nomeMetodo = "serviziServizioApplicativo";
		int idLista = Liste.SERVIZI_SERVIZIO_APPLICATIVO;
		// int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		// offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		ricerca.getSearchString(idLista);
		

		Connection con = null;
		PreparedStatement stmt = null, stmt1 = null;
		ResultSet risultato = null, risultato1 = null;

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] SQLException accedendo al datasource :" + e.getMessage());

			}

		} else {
			con = this.globalConnection;
		}

		try {

			ISQLQueryObject sqlQueryObjectLike = null;
			if(search!=null && !"".equals(search)){
				sqlQueryObjectLike = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObjectLike.addSelectField("id");
				sqlQueryObjectLike.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObjectLike.addWhereLikeCondition("nome", search, true, true);
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.POLITICHE_SICUREZZA);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.addWhereCondition("id_fruitore = ?");
			sqlQueryObject.addWhereCondition("id_servizio = ?");
			if(search!=null && !"".equals(search)){
				sqlQueryObject.addWhereINSelectSQLCondition(false, "id_servizio_applicativo", sqlQueryObjectLike);
			}
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setInt(1, idSoggettoFruitoreDelServizio);
			stmt.setInt(2, idServizio);
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				ricerca.setNumEntries(idLista, risultato.getInt(1));
			}
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) {
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			}
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.POLITICHE_SICUREZZA);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_fruitore = ?");
			sqlQueryObject.addWhereCondition("id_servizio = ?");
			if(search!=null && !"".equals(search)){
				sqlQueryObject.addWhereINSelectSQLCondition(false, "id_servizio_applicativo", sqlQueryObjectLike);
			}
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setInt(1, idSoggettoFruitoreDelServizio);
			stmt.setInt(2, idServizio);
			risultato = stmt.executeQuery();

			ArrayList<ServizioApplicativo> lista = new ArrayList<ServizioApplicativo>();
			ServizioApplicativo sa = null;
			while (risultato.next()) {
				// ho la lista degli id del servizio applicativo
				// per ogni id devo recuperare il nome del servizio applicativo
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addWhereCondition("id = ?");
				queryString = sqlQueryObject.createSQLQuery();
				stmt1 = con.prepareStatement(queryString);
				stmt1.setInt(1, risultato.getInt("id_servizio_applicativo"));
				risultato1 = stmt1.executeQuery();
				if (risultato1.next()) {
					sa = new ServizioApplicativo();
					sa.setId(risultato1.getLong("id"));
					sa.setNome(risultato1.getString("nome"));
					lista.add(sa);
				}

				risultato1.close();
				stmt1.close();
			}
			risultato.close();
			stmt.close();

			return lista;

		} catch (Exception se) {

			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {

			// Chiudo statement and resultset
			try {
				if (risultato1 != null) {
					risultato1.close();
				}
				if (stmt1 != null) {
					stmt1.close();
				}
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}

	}

	

	/**
	 * Ritorna la Politica di Sicurezza {@link PoliticheSicurezza} con id = idPS
	 */
	public PoliticheSicurezza getPoliticheSicurezza(long idPS) throws DriverControlStationException {
		String nomeMetodo = "politicheSicurezza";
		PoliticheSicurezza ps = null;

		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] SQLException accedendo al datasource :" + e.getMessage());

			}

		} else {
			con = this.globalConnection;
		}

		try {

			// mi servono anche tutte lo politicheSicurezza (servizi
			// applicativi) di questo fruitore
			// associato a questo servizio
			risultato = null;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.POLITICHE_SICUREZZA);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);

			stmt.setLong(1, idPS);

			risultato = stmt.executeQuery();

			if (risultato.next()) {
				ps = new PoliticheSicurezza();
				ps.setId(idPS);
				ps.setIdFruitore(risultato.getLong("id_fruitore"));
				ps.setIdServizio(risultato.getLong("id_servizio"));
				ps.setIdServizioApplicativo(risultato.getLong("id_servizio_applicativo"));

			}

			return ps;

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {

			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	/**
	 * Ritorna la Politica di Sicurezza {@link PoliticheSicurezza} con
	 * id_fruitore = idFruitore, id_servizio = idServizio,
	 * id_servizio_applicativo = idSA
	 */
	public PoliticheSicurezza getPoliticheSicurezza(long idFruitore, long idServizio, long idSA) throws DriverControlStationException {
		String nomeMetodo = "politicheSicurezza";
		PoliticheSicurezza ps = null;

		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] SQLException accedendo al datasource :" + e.getMessage());

			}

		} else {
			con = this.globalConnection;
		}

		try {

			risultato = null;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.POLITICHE_SICUREZZA);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_fruitore = ?");
			sqlQueryObject.addWhereCondition("id_servizio = ?");
			sqlQueryObject.addWhereCondition("id_servizio_applicativo = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);

			stmt.setLong(1, idFruitore);
			stmt.setLong(2, idServizio);
			stmt.setLong(3, idSA);

			risultato = stmt.executeQuery();

			if (risultato.next()) {
				ps = new PoliticheSicurezza();
				ps.setId(risultato.getLong("id"));
				ps.setIdFruitore(idFruitore);
				ps.setIdServizio(idServizio);
				ps.setIdServizioApplicativo(idSA);
			}

			return ps;

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {

			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	/**
	 * Ritorna le politiche di sicurezza del fruitore (idFruitore) di un dato
	 * servizio (idServizio)
	 * 
	 * @param idServizio
	 * @param idFruitore
	 * @return La lista delle politiche di sicurezza (Servizi Applicativi),
	 *         oppure null se non e' presente nessun associazione.
	 * @throws DriverControlStationException
	 */
	public List<String> getPoliticheSicurezza(long idServizio, long idFruitore) throws DriverControlStationException {
		String nomeMetodo = "politicheSicurezza";
		List<String> lista = new ArrayList<String>();

		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] SQLException accedendo al datasource :" + e.getMessage());

			}

		} else {
			con = this.globalConnection;
		}

		try {

			// mi servono anche tutte lo politicheSicurezza (servizi
			// applicativi) di questo fruitore
			// associato a questo servizio
			risultato = null;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.POLITICHE_SICUREZZA);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_servizio = ?");
			sqlQueryObject.addWhereCondition("id_fruitore = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);

			stmt.setLong(1, idServizio);
			stmt.setLong(2, idFruitore);
			risultato = stmt.executeQuery();

			// org.openspcoop2.core.config.ServizioApplicativo sa =
			// null;
			while (risultato.next()) {
				long idSA = risultato.getLong("id_servizio_applicativo");
				// sa = new
				// org.openspcoop2.core.config.ServizioApplicativo();
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id = ?");
				queryString = sqlQueryObject.createSQLQuery();
				PreparedStatement stmt1 = con.prepareStatement(queryString);
				stmt1.setLong(1, idSA);
				ResultSet risultato1 = stmt1.executeQuery();
				if (risultato1.next()) {
					// sa.setId(new Long(idSA));
					// sa.setNome(risultato.getString("nome"));//mi
					// serve solo il nome per il momento
					// aggiungo il servizio alla lista
					lista.add(risultato1.getString("nome"));
				}
				risultato1.close();
				stmt1.close();
			}

			return lista;

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	/* *********** RUOLI ****************** */
	
	public void createRuolo(Ruolo ruolo) throws DriverControlStationException, DriverControlStationNotFound {

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString = "";

		if ((ruolo == null) || (ruolo.getNome() == null) || ruolo.getNome().equals("")) {
			throw new DriverControlStationException("Il nome del ruolo e' necessario.");
		}

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException(e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addInsertTable(CostantiDB.RUOLI_SA);
			sqlQueryObject.addInsertField("id_accordo", "?");
			sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
			sqlQueryObject.addInsertField("servizio_correlato", "?");
			queryString = sqlQueryObject.createSQLInsert();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, ruolo.getIdAccordo());
			stmt.setLong(2, ruolo.getIdServizioApplicativo());
			stmt.setInt(3, ruolo.isCorrelato() ? CostantiDB.TRUE : CostantiDB.FALSE);
			stmt.executeUpdate();
			stmt.close();

			// recupero id del ruolo appena inserito
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.RUOLI_SA);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id_accordo = ?");
			sqlQueryObject.addWhereCondition("id_servizio_applicativo = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, ruolo.getIdAccordo());
			stmt.setLong(2, ruolo.getIdServizioApplicativo());
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				ruolo.setId(risultato.getLong("id"));
			} else {
				throw new DriverConfigurazioneException("Impossibile recuperare l'id del Ruolo appena inserito.");
			}
		} catch (Exception se) {
			throw new DriverControlStationException(se);
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}

	}


	public void updateRuolo(Ruolo ruolo) throws DriverControlStationException, DriverControlStationNotFound {

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if ((ruolo == null) || (ruolo.getIdAccordo() <= 0)) {
			throw new DriverControlStationException("L'id del Ruolo e' necessario");
		}
		if ((ruolo == null) || (ruolo.getNome() == null) || ruolo.getNome().equals("")) {
			throw new DriverControlStationException("Il nome del ruolo e' necessario.");
		}

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException(e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			throw new DriverControlStationException("Not implemented yet.");

		} catch (Exception se) {
			throw new DriverControlStationException(se);
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}

	}


	public void deleteRuolo(Ruolo ruolo) throws DriverControlStationException, DriverControlStationNotFound {

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString = "";

		if ((ruolo == null) || (ruolo.getIdAccordo() <= 0)) {
			throw new DriverControlStationException("L'id Accordo e' necessario per cancellare il Ruolo");
		}
		if ((ruolo == null) || (ruolo.getIdServizioApplicativo() <= 0)) {
			throw new DriverControlStationException("L'id Servizio Applicativo e' necessario per cancellare il Ruolo.");
		}

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException(e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.RUOLI_SA);
			sqlQueryObject.addWhereCondition("id_accordo=?");
			sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
			sqlQueryObject.addWhereCondition("servizio_correlato=?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, ruolo.getIdAccordo());
			stmt.setLong(2, ruolo.getIdServizioApplicativo());
			stmt.setInt(3, ruolo.isCorrelato() ? CostantiDB.TRUE : CostantiDB.FALSE);
			stmt.executeUpdate();
			stmt.close();

		} catch (Exception se) {
			throw new DriverControlStationException(se);
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}

	}


	public Ruolo getRuolo(long idRuolo) throws DriverControlStationException, DriverControlStationNotFound {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString = "";

		if (idRuolo <= 0) {
			throw new DriverControlStationException("L'id del Ruolo e' necessario");
		}

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException(e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			Ruolo ruolo = null;
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.RUOLI_SA);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addSelectField(CostantiDB.RUOLI_SA + ".id_accordo");
			sqlQueryObject.addSelectField(CostantiDB.RUOLI_SA + ".id_servizio_applicativo");
			sqlQueryObject.addSelectField(CostantiDB.RUOLI_SA + ".servizio_correlato");
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".nome", "nomeAccordo");
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".versione", "versioneAccordo");
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".id_referente", "referenteAccordo");
			sqlQueryObject.addWhereCondition(CostantiDB.RUOLI_SA + ".id = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.RUOLI_SA + ".id_accordo = " + CostantiDB.ACCORDI + ".id");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idRuolo);
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				ruolo = new Ruolo();
				ruolo.setIdAccordo(risultato.getLong("id_accordo"));
				ruolo.setIdServizioApplicativo(risultato.getLong("id_servizio_applicativo"));
				ruolo.setCorrelato(risultato.getInt("servizio_correlato") == CostantiDB.TRUE ? true : false);
				ruolo.setId(idRuolo);
				
				String accordoNome = risultato.getString("nomeAccordo");
				String accordoVersione = risultato.getString("versioneAccordo");
				long referenteAccordo = risultato.getLong("referenteAccordo");
				IDSoggetto soggettoReferente = null;
				if(referenteAccordo>0){
					Soggetto s = this.getDriverRegistroServiziDB().getSoggetto(referenteAccordo, con);
					if(s==null){
						throw new Exception("Soggetto referente ["+referenteAccordo+"] non presente?");
					}
					soggettoReferente = new IDSoggetto(s.getTipo(),s.getNome());
				}
				
				ruolo.setNome(this.idAccordoFactory.getUriFromValues(accordoNome, soggettoReferente, accordoVersione));
				risultato.close();
				stmt.close();
			} else {
				throw new DriverControlStationNotFound("Nessun ruolo trovato con id=" + idRuolo);
			}

			// setto informazioni su proprietario sa
			ServizioApplicativo sa = this.getServizioApplicativoWithRuolo(ruolo.getIdServizioApplicativo());
			ruolo.setTipoProprietarioSA(sa.getTipoSoggettoProprietario());
			ruolo.setNomeProprietarioSA(sa.getNomeSoggettoProprietario());
			ruolo.setNomeServizioApplicativo(sa.getNome());

			return ruolo;

		} catch (DriverControlStationNotFound nf) {
			throw new DriverControlStationNotFound(nf);
		} catch (Exception se) {
			throw new DriverControlStationException(se);
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public List<Ruolo> ruoliWithIdServizioApplicativo(long idServizioApplicativo) throws DriverControlStationException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString = "";

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException(e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			Ruolo ruolo = null;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.RUOLI_SA);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idServizioApplicativo);
			List<Ruolo> ruoli = new ArrayList<Ruolo>();
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				ruolo = this.getRuolo(risultato.getLong("id"));
				ruoli.add(ruolo);
			}
			risultato.close();
			stmt.close();

			return ruoli;

		} catch (Exception se) {
			throw new DriverControlStationException(se);
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	public List<Ruolo> ruoliList(ISearch ricerca) throws DriverControlStationException {
		String nomeMetodo = "ruoliList";
		int idLista = Liste.RUOLI;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		ricerca.getSearchString(idLista);

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] SQLException accedendo al datasource :" + e.getMessage());

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObjectSoggetti = null;
			if (!search.equals("")) {
				sqlQueryObjectSoggetti = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObjectSoggetti.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObjectSoggetti.addSelectField(CostantiDB.SOGGETTI, "nome_soggetto");
				sqlQueryObjectSoggetti.addWhereCondition(true,
						sqlQueryObjectSoggetti.getWhereLikeCondition("nome_soggetto", search, true, true),
						CostantiDB.ACCORDI+".id_referente="+CostantiDB.SOGGETTI+".id");
			}
					
			if (!search.equals("")) {
				// query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
				sqlQueryObject.addFromTable(CostantiDB.RUOLI_SA);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(false, 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.ACCORDI+".nome", search, true, true),
						sqlQueryObject.getWhereLikeCondition(CostantiDB.ACCORDI+".versione", search, true, true),
						sqlQueryObject.getWhereExistsCondition(false, sqlQueryObjectSoggetti));
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI + "id = " + CostantiDB.RUOLI_SA + ".id_accordo");
				sqlQueryObject.addWhereCondition(CostantiDB.RUOLI_SA + "id_servizio_applicativo = " + CostantiDB.SERVIZI_APPLICATIVI + ".id");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
				sqlQueryObject.addFromTable(CostantiDB.RUOLI_SA);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI + "id = " + CostantiDB.RUOLI_SA + ".id_accordo");
				sqlQueryObject.addWhereCondition(CostantiDB.RUOLI_SA + "id_servizio_applicativo = " + CostantiDB.SERVIZI_APPLICATIVI + ".id");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}

			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				ricerca.setNumEntries(idLista, risultato.getInt(1));
			}
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) {
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			}
			if (!search.equals("")) {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
				sqlQueryObject.addFromTable(CostantiDB.RUOLI_SA);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField(CostantiDB.ACCORDI + ".id");
				sqlQueryObject.addSelectField(CostantiDB.RUOLI_SA + ".servizio_correlato");
				sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".nome", "nomeAccordo");
				sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".versione", "versioneAccordo");
				sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".id_referente", "referenteAccordo");
				sqlQueryObject.addWhereCondition(false, 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.ACCORDI+".nome", search, true, true),
						sqlQueryObject.getWhereLikeCondition(CostantiDB.ACCORDI+".versione", search, true, true),
						sqlQueryObject.getWhereExistsCondition(false, sqlQueryObjectSoggetti));
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI + "id = " + CostantiDB.RUOLI_SA + ".id_accordo");
				sqlQueryObject.addWhereCondition(CostantiDB.RUOLI_SA + "id_servizio_applicativo = " + CostantiDB.SERVIZI_APPLICATIVI + ".id");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.ACCORDI + ".nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
				sqlQueryObject.addFromTable(CostantiDB.RUOLI_SA);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField(CostantiDB.ACCORDI + ".id");
				sqlQueryObject.addSelectField(CostantiDB.RUOLI_SA + ".servizio_correlato");
				sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".nome", "nomeAccordo");
				sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".versione", "versioneAccordo");
				sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".id_referente", "referenteAccordo");
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI + "id = " + CostantiDB.RUOLI_SA + ".id_accordo");
				sqlQueryObject.addWhereCondition(CostantiDB.RUOLI_SA + "id_servizio_applicativo = " + CostantiDB.SERVIZI_APPLICATIVI + ".id");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.ACCORDI + ".nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}

			queryString = MessageFormat.format(queryString, CostantiDB.ACCORDI, CostantiDB.RUOLI_SA, CostantiDB.SERVIZI_APPLICATIVI);

			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();

			ArrayList<Ruolo> lista = new ArrayList<Ruolo>();
			Ruolo ruolo = null;
			while (risultato.next()) {
				ruolo = new Ruolo();
				ruolo.setIdAccordo(risultato.getLong("id"));
				ruolo.setCorrelato(risultato.getInt("servizio_correlato") == CostantiDB.TRUE ? true : false);
				
				String accordoNome = risultato.getString("nomeAccordo");
				String accordoVersione = risultato.getString("versioneAccordo");
				long referenteAccordo = risultato.getLong("referenteAccordo");
				IDSoggetto soggettoReferente = null;
				if(referenteAccordo>0){
					Soggetto s = this.getDriverRegistroServiziDB().getSoggetto(referenteAccordo, con);
					if(s==null){
						throw new Exception("Soggetto referente ["+referenteAccordo+"] non presente?");
					}
					soggettoReferente = new IDSoggetto(s.getTipo(),s.getNome());
				}
				
				ruolo.setNome(this.idAccordoFactory.getUriFromValues(accordoNome, soggettoReferente, accordoVersione));
				
				lista.add(ruolo);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public List<Ruolo> getRuoli(String superuser) throws DriverControlStationException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString = "";

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException(e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			
			Ruolo ruolo = null;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".nome", "nomeAccordo");
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".versione", "versioneAccordo");
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".id_referente", "referenteAccordo");
			if(superuser!=null && (!"".equals(superuser))){
				sqlQueryObject.addWhereCondition("superuser=?");
			}
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			if(superuser!=null && (!"".equals(superuser))){
				stmt.setString(1, superuser);
			}
			List<Ruolo> ruoli = new ArrayList<Ruolo>();
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				ruolo = new Ruolo();
				ruolo.setIdAccordo(risultato.getLong("id"));
				
				String accordoNome = risultato.getString("nomeAccordo");
				String accordoVersione = risultato.getString("versioneAccordo");
				long referenteAccordo = risultato.getLong("referenteAccordo");
				IDSoggetto soggettoReferente = null;
				if(referenteAccordo>0){
					Soggetto s = this.getDriverRegistroServiziDB().getSoggetto(referenteAccordo, con);
					if(s==null){
						try {
							if (risultato != null) {
								risultato.close(); risultato=null;
							}
						} catch (Exception e) {}
						try {
							if (stmt != null) {
								stmt.close(); stmt=null;
							}
						} catch (Exception e) {
							// ignore
						}
						throw new Exception("Soggetto referente ["+referenteAccordo+"] non presente?");
					}
					soggettoReferente = new IDSoggetto(s.getTipo(),s.getNome());
				}
				
				ruolo.setNome(this.idAccordoFactory.getUriFromValues(accordoNome, soggettoReferente, accordoVersione));
				
				ruoli.add(ruolo);
			}
			risultato.close();
			stmt.close();

			return ruoli;

		} catch (Exception se) {
			throw new DriverControlStationException(se);
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
			} catch (Exception e) {}
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	
	/**
	 * Recupera il servizio applicativo tramite l'id
	 */
	public ServizioApplicativo getServizioApplicativoWithRuolo(long idServizioApplicativo) throws DriverControlStationException, DriverControlStationNotFound {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString = "";

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException(e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			ServizioApplicativo sa = this.configDB.getServizioApplicativo(idServizioApplicativo);

			// aggiungo i ruoli a questo servizio applicativo
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addFromTable(CostantiDB.RUOLI_SA);
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".nome", "nomeAccordo");
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".versione", "versioneAccordo");
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".id_referente", "referenteAccordo");
			sqlQueryObject.addSelectField(CostantiDB.RUOLI_SA + ".servizio_correlato");
			sqlQueryObject.addWhereCondition(CostantiDB.RUOLI_SA + ".id_servizio_applicativo = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.RUOLI_SA + ".id_accordo = " + CostantiDB.ACCORDI + ".id");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, sa.getId());
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				
				String accordoNome = risultato.getString("nomeAccordo");
				String accordoVersione = risultato.getString("versioneAccordo");
				long referenteAccordo = risultato.getLong("referenteAccordo");
				IDSoggetto soggettoReferente = null;
				if(referenteAccordo>0){
					Soggetto s = this.getDriverRegistroServiziDB().getSoggetto(referenteAccordo, con);
					if(s==null){
						try {
							if (risultato != null) {
								risultato.close(); risultato=null;
							}
						} catch (Exception e) {}
						try {
							if (stmt != null) {
								stmt.close(); stmt=null;
							}
						} catch (Exception e) {
							// ignore
						}
						throw new Exception("Soggetto referente ["+referenteAccordo+"] non presente?");
					}
					soggettoReferente = new IDSoggetto(s.getTipo(),s.getNome());
				}
				
				String nomeRuolo = this.idAccordoFactory.getUriFromValues(accordoNome, soggettoReferente, accordoVersione);
				
				int isCorrelato = risultato.getInt("servizio_correlato");
				if (isCorrelato == CostantiDB.TRUE) {
					nomeRuolo += "Correlato";
				}
				sa.addRuolo(nomeRuolo);
			}
			risultato.close();
			stmt.close();

			return sa;
		} catch (DriverConfigurazioneException e) {
			throw new DriverControlStationException(e);
		} catch (DriverConfigurazioneNotFound e) {
			throw new DriverControlStationNotFound(e.getMessage(), e);
		} catch (Exception se) {
			throw new DriverControlStationException(se);
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public ServizioApplicativo getServizioApplicativoWithRuolo(String nomeServizioApplicativo, String nomeProprietario, String tipoProprietario) throws DriverControlStationException, DriverControlStationNotFound {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString = "";

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException(e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			long idProprietario = DBUtils.getIdSoggetto(nomeProprietario, tipoProprietario, con, this.tipoDB, CostantiDB.SOGGETTI);
			if (idProprietario < 0) {
				this.log.error("Il soggetto " + tipoProprietario + "/" + nomeProprietario + " non esiste");
				throw new DriverControlStationException("Il soggetto " + tipoProprietario + "/" + nomeProprietario + " non esiste");
			}
			ServizioApplicativo sa = null;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, nomeServizioApplicativo);
			stmt.setLong(2, idProprietario);
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				sa = this.getServizioApplicativoWithRuolo(risultato.getLong("id"));
			} else {
				throw new DriverConfigurazioneNotFound("Nessun Servizio Applicativo trovato con nome " + nomeServizioApplicativo + " appartenente a " + tipoProprietario + "/" + nomeProprietario);
			}
			risultato.close();
			stmt.close();

			return sa;
		} catch (DriverConfigurazioneNotFound e) {
			throw new DriverControlStationException(e);
		} catch (Exception se) {
			throw new DriverControlStationException(se);
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public ServizioApplicativo getServizioApplicativoWithRuolo(IDPortaDelegata idPD, String servizioApplicativo) throws DriverControlStationException, DriverControlStationNotFound {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException(e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			ServizioApplicativo sa = this.configDB.getServizioApplicativo(idPD, servizioApplicativo);

			// aggiungo i ruoli a questo servizio
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addFromTable(CostantiDB.RUOLI_SA);
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".nome", "nomeAccordo");
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".versione", "versioneAccordo");
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".id_referente", "referenteAccordo");
			sqlQueryObject.addSelectField(CostantiDB.RUOLI_SA + ".servizio_correlato");
			sqlQueryObject.addWhereCondition(CostantiDB.RUOLI_SA + ".id_servizio_applicativo = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.RUOLI_SA + ".id_accordo = " + CostantiDB.ACCORDI + ".id");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(sqlQuery);
			stmt.setLong(1, sa.getId());
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				
				String accordoNome = risultato.getString("nomeAccordo");
				String accordoVersione = risultato.getString("versioneAccordo");
				long referenteAccordo = risultato.getLong("referenteAccordo");
				IDSoggetto soggettoReferente = null;
				if(referenteAccordo>0){
					Soggetto s = this.getDriverRegistroServiziDB().getSoggetto(referenteAccordo, con);
					if(s==null){
						try {
							if (risultato != null) {
								risultato.close(); risultato=null;
							}
						} catch (Exception e) {}
						try {
							if (stmt != null) {
								stmt.close(); stmt=null;
							}
						} catch (Exception e) {
							// ignore
						}
						throw new Exception("Soggetto referente ["+referenteAccordo+"] non presente?");
					}
					soggettoReferente = new IDSoggetto(s.getTipo(),s.getNome());
				}
				
				String nomeRuolo = this.idAccordoFactory.getUriFromValues(accordoNome, soggettoReferente, accordoVersione);

				int isCorrelato = risultato.getInt("servizio_correlato");
				if (isCorrelato == CostantiDB.TRUE) {
					nomeRuolo += "Correlato";
				}
				sa.addRuolo(nomeRuolo);
			}
			risultato.close();
			stmt.close();

			return sa;
		} catch (DriverConfigurazioneException e) {
			throw new DriverControlStationException(e);
		} catch (DriverConfigurazioneNotFound e) {
			throw new DriverControlStationException(e);
		} catch (Exception se) {
			throw new DriverControlStationException(se);
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public ServizioApplicativo getServizioApplicativoWithRuolo(IDPortaApplicativa idPA, String servizioApplicativo) throws DriverControlStationException, DriverControlStationNotFound {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException(e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			ServizioApplicativo sa = this.configDB.getServizioApplicativo(idPA, servizioApplicativo);

			
			// aggiungo i ruoli a questo servizio
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addFromTable(CostantiDB.RUOLI_SA);
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".nome", "nomeAccordo");
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".versione", "versioneAccordo");
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".id_referente", "referenteAccordo");
			sqlQueryObject.addSelectField(CostantiDB.RUOLI_SA + ".servizio_correlato");
			sqlQueryObject.addWhereCondition(CostantiDB.RUOLI_SA + ".id_servizio_applicativo = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.RUOLI_SA + ".id_accordo = " + CostantiDB.ACCORDI + ".id");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(sqlQuery);
			stmt.setLong(1, sa.getId());
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				
				String accordoNome = risultato.getString("nomeAccordo");
				String accordoVersione = risultato.getString("versioneAccordo");
				long referenteAccordo = risultato.getLong("referenteAccordo");
				IDSoggetto soggettoReferente = null;
				if(referenteAccordo>0){
					Soggetto s = this.getDriverRegistroServiziDB().getSoggetto(referenteAccordo, con);
					if(s==null){
						try {
							if (risultato != null) {
								risultato.close(); risultato=null;
							}
						} catch (Exception e) {}
						try {
							if (stmt != null) {
								stmt.close(); stmt=null;
							}
						} catch (Exception e) {
							// ignore
						}
						throw new Exception("Soggetto referente ["+referenteAccordo+"] non presente?");
					}
					soggettoReferente = new IDSoggetto(s.getTipo(),s.getNome());
				}
				
				String nomeRuolo = this.idAccordoFactory.getUriFromValues(accordoNome, soggettoReferente, accordoVersione);
				
				int isCorrelato = risultato.getInt("servizio_correlato");
				if (isCorrelato == CostantiDB.TRUE) {
					nomeRuolo += "Correlato";
				}
				sa.addRuolo(nomeRuolo);
			}
			risultato.close();
			stmt.close();

			return sa;
		} catch (DriverConfigurazioneException e) {
			throw new DriverControlStationException(e);
		} catch (DriverConfigurazioneNotFound e) {
			throw new DriverControlStationException(e);
		} catch (Exception se) {
			throw new DriverControlStationException(se);
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public ServizioApplicativo getServizioApplicativoAutenticatoWithRuolo(IDPortaDelegata idPD, String user, String password) throws DriverControlStationException, DriverControlStationNotFound {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException(e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			ServizioApplicativo sa = this.configDB.getServizioApplicativoAutenticato(idPD, user, password);

			// aggiungo i ruoli a questo servizio
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addFromTable(CostantiDB.RUOLI_SA);
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".nome", "nomeAccordo");
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".versione", "versioneAccordo");
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".id_referente", "referenteAccordo");
			sqlQueryObject.addSelectField(CostantiDB.RUOLI_SA + ".servizio_correlato");
			sqlQueryObject.addWhereCondition(CostantiDB.RUOLI_SA + ".id_servizio_applicativo = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.RUOLI_SA + ".id_accordo = " + CostantiDB.ACCORDI + ".id");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(sqlQuery);
			stmt.setLong(1, sa.getId());
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				
				String accordoNome = risultato.getString("nomeAccordo");
				String accordoVersione = risultato.getString("versioneAccordo");
				long referenteAccordo = risultato.getLong("referenteAccordo");
				IDSoggetto soggettoReferente = null;
				if(referenteAccordo>0){
					Soggetto s = this.getDriverRegistroServiziDB().getSoggetto(referenteAccordo, con);
					if(s==null){
						try {
							if (risultato != null) {
								risultato.close(); risultato=null;
							}
						} catch (Exception e) {}
						try {
							if (stmt != null) {
								stmt.close(); stmt=null;
							}
						} catch (Exception e) {
							// ignore
						}
						throw new Exception("Soggetto referente ["+referenteAccordo+"] non presente?");
					}
					soggettoReferente = new IDSoggetto(s.getTipo(),s.getNome());
				}
				
				String nomeRuolo = this.idAccordoFactory.getUriFromValues(accordoNome, soggettoReferente, accordoVersione);
				
				int isCorrelato = risultato.getInt("servizio_correlato");
				if (isCorrelato == CostantiDB.TRUE) {
					nomeRuolo += "Correlato";
				}
				sa.addRuolo(nomeRuolo);
			}
			risultato.close();
			stmt.close();

			return sa;
		} catch (DriverConfigurazioneException e) {
			throw new DriverControlStationException(e);
		} catch (DriverConfigurazioneNotFound e) {
			throw new DriverControlStationException(e);
		} catch (Exception se) {
			throw new DriverControlStationException(se);
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public ServizioApplicativo getServizioApplicativoAutenticatoWithRuolo(String user, String password) throws DriverControlStationException, DriverControlStationNotFound {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException(e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			ServizioApplicativo sa = this.configDB.getServizioApplicativoAutenticato(user, password);

			// aggiungo i ruoli a questo servizio
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addFromTable(CostantiDB.RUOLI_SA);
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".nome", "nomeAccordo");
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".versione", "versioneAccordo");
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".id_referente", "referenteAccordo");
			sqlQueryObject.addSelectField(CostantiDB.RUOLI_SA + ".servizio_correlato");
			sqlQueryObject.addWhereCondition(CostantiDB.RUOLI_SA + ".id_servizio_applicativo = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.RUOLI_SA + ".id_accordo = " + CostantiDB.ACCORDI + ".id");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(sqlQuery);
			stmt.setLong(1, sa.getId());
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				
				String accordoNome = risultato.getString("nomeAccordo");
				String accordoVersione = risultato.getString("versioneAccordo");
				long referenteAccordo = risultato.getLong("referenteAccordo");
				IDSoggetto soggettoReferente = null;
				if(referenteAccordo>0){
					Soggetto s = this.getDriverRegistroServiziDB().getSoggetto(referenteAccordo, con);
					if(s==null){
						try {
							if (risultato != null) {
								risultato.close(); risultato=null;
							}
						} catch (Exception e) {}
						try {
							if (stmt != null) {
								stmt.close(); stmt=null;
							}
						} catch (Exception e) {
							// ignore
						}
						throw new Exception("Soggetto referente ["+referenteAccordo+"] non presente?");
					}
					soggettoReferente = new IDSoggetto(s.getTipo(),s.getNome());
				}
				
				String nomeRuolo = this.idAccordoFactory.getUriFromValues(accordoNome, soggettoReferente, accordoVersione);
				
				int isCorrelato = risultato.getInt("servizio_correlato");
				if (isCorrelato == CostantiDB.TRUE) {
					nomeRuolo += "Correlato";
				}
				sa.addRuolo(nomeRuolo);
			}
			risultato.close();
			stmt.close();

			return sa;
		} catch (DriverConfigurazioneException e) {
			throw new DriverControlStationException(e);
		} catch (DriverConfigurazioneNotFound e) {
			throw new DriverControlStationException(e);
		} catch (Exception se) {
			throw new DriverControlStationException(se);
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public ServizioApplicativo getServizioApplicativoAutenticatoWithRuolo(IDPortaDelegata idPD, String subject) throws DriverControlStationException, DriverControlStationNotFound {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException(e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			ServizioApplicativo sa = this.configDB.getServizioApplicativoAutenticato(idPD, subject);

			// aggiungo i ruoli a questo servizio
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addFromTable(CostantiDB.RUOLI_SA);
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".nome", "nomeAccordo");
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".versione", "versioneAccordo");
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".id_referente", "referenteAccordo");
			sqlQueryObject.addSelectField(CostantiDB.RUOLI_SA + ".servizio_correlato");
			sqlQueryObject.addWhereCondition(CostantiDB.RUOLI_SA + ".id_servizio_applicativo = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.RUOLI_SA + ".id_accordo = " + CostantiDB.ACCORDI + ".id");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(sqlQuery);
			stmt.setLong(1, sa.getId());
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				
				String accordoNome = risultato.getString("nomeAccordo");
				String accordoVersione = risultato.getString("versioneAccordo");
				long referenteAccordo = risultato.getLong("referenteAccordo");
				IDSoggetto soggettoReferente = null;
				if(referenteAccordo>0){
					Soggetto s = this.getDriverRegistroServiziDB().getSoggetto(referenteAccordo, con);
					if(s==null){
						try {
							if (risultato != null) {
								risultato.close(); risultato=null;
							}
						} catch (Exception e) {}
						try {
							if (stmt != null) {
								stmt.close(); stmt=null;
							}
						} catch (Exception e) {
							// ignore
						}
						throw new Exception("Soggetto referente ["+referenteAccordo+"] non presente?");
					}
					soggettoReferente = new IDSoggetto(s.getTipo(),s.getNome());
				}
				
				String nomeRuolo = this.idAccordoFactory.getUriFromValues(accordoNome, soggettoReferente, accordoVersione);
				
				int isCorrelato = risultato.getInt("servizio_correlato");
				if (isCorrelato == CostantiDB.TRUE) {
					nomeRuolo += "Correlato";
				}
				sa.addRuolo(nomeRuolo);
			}
			risultato.close();
			stmt.close();

			return sa;
		} catch (DriverConfigurazioneException e) {
			throw new DriverControlStationException(e);
		} catch (DriverConfigurazioneNotFound e) {
			throw new DriverControlStationException(e);
		} catch (Exception se) {
			throw new DriverControlStationException(se);
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public ServizioApplicativo getServizioApplicativoAutenticatoWithRuolo(String subject) throws DriverControlStationException, DriverControlStationNotFound {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException(e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			ServizioApplicativo sa = this.configDB.getServizioApplicativoAutenticato(subject);

			// aggiungo i ruoli a questo servizio
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addFromTable(CostantiDB.RUOLI_SA);
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".nome", "nomeAccordo");
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".versione", "versioneAccordo");
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI + ".id_referente", "referenteAccordo");
			sqlQueryObject.addSelectField(CostantiDB.RUOLI_SA + ".servizio_correlato");
			sqlQueryObject.addWhereCondition(CostantiDB.RUOLI_SA + ".id_servizio_applicativo = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.RUOLI_SA + ".id_accordo = " + CostantiDB.ACCORDI + ".id");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(sqlQuery);
			stmt.setLong(1, sa.getId());
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				
				String accordoNome = risultato.getString("nomeAccordo");
				String accordoVersione = risultato.getString("versioneAccordo");
				long referenteAccordo = risultato.getLong("referenteAccordo");
				IDSoggetto soggettoReferente = null;
				if(referenteAccordo>0){
					Soggetto s = this.getDriverRegistroServiziDB().getSoggetto(referenteAccordo, con);
					if(s==null){
						try {
							if (risultato != null) {
								risultato.close(); risultato=null;
							}
						} catch (Exception e) {}
						try {
							if (stmt != null) {
								stmt.close(); stmt=null;
							}
						} catch (Exception e) {
							// ignore
						}
						throw new Exception("Soggetto referente ["+referenteAccordo+"] non presente?");
					}
					soggettoReferente = new IDSoggetto(s.getTipo(),s.getNome());
				}
				
				String nomeRuolo = this.idAccordoFactory.getUriFromValues(accordoNome, soggettoReferente, accordoVersione);
				
				int isCorrelato = risultato.getInt("servizio_correlato");
				if (isCorrelato == CostantiDB.TRUE) {
					nomeRuolo += "Correlato";
				}
				sa.addRuolo(nomeRuolo);
			}
			risultato.close();
			stmt.close();

			return sa;
		} catch (DriverConfigurazioneException e) {
			throw new DriverControlStationException(e);
		} catch (DriverConfigurazioneNotFound e) {
			throw new DriverControlStationException(e);
		} catch (Exception se) {
			throw new DriverControlStationException(se);
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
/* *********** METODI IS IN USO ****************** */

	
	
	public boolean isAccordoInUso(AccordoServizioParteComune as, Map<ErrorsHandlerCostant, List<String>> whereIsInUso) throws DriverControlStationException {
		String nomeMetodo = "isAccordoInUso";

		Connection con = null;

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] SQLException accedendo al datasource :" + e.getMessage());

			}

		} else {
			con = this.globalConnection;
		}

		try {
			return DBOggettiInUsoUtils.isAccordoServizioParteComuneInUso(con, this.tipoDB, this.idAccordoFactory.getIDAccordoFromAccordo(as), whereIsInUso);

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public boolean isPddInUso(PdDControlStation pdd, List<String> whereIsInUso) throws DriverControlStationException {
		String nomeMetodo = "pddInUso";

		Connection con = null;
		
		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] SQLException accedendo al datasource :" + e.getMessage());

			}

		} else {
			con = this.globalConnection;
		}

		try {

			return DBOggettiInUsoUtils.isPddInUso(con, this.tipoDB, pdd.getNome(), whereIsInUso);

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public boolean isSoggettoInUso(org.openspcoop2.core.config.Soggetto soggettoConfig, Map<ErrorsHandlerCostant, List<String>> whereIsInUso) throws DriverControlStationException {
		return isSoggettoInUso(soggettoConfig,null,whereIsInUso);
	}
	public boolean isSoggettoInUso(Soggetto soggettoRegistro, Map<ErrorsHandlerCostant, List<String>> whereIsInUso) throws DriverControlStationException {
		return isSoggettoInUso(null,soggettoRegistro,whereIsInUso);
	}
	private boolean isSoggettoInUso(org.openspcoop2.core.config.Soggetto soggettoConfig, Soggetto soggettoRegistro, Map<ErrorsHandlerCostant, List<String>> whereIsInUso) throws DriverControlStationException {
		String nomeMetodo = "isSoggettoInUso";

		Connection con = null;
		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] SQLException accedendo al datasource :" + e.getMessage());

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			String tipoSoggetto = null;
			String nomeSoggetto = null;
			if(soggettoRegistro!=null){
				tipoSoggetto = soggettoRegistro.getTipo();
				nomeSoggetto = soggettoRegistro.getNome();
			}else{
				tipoSoggetto = soggettoConfig.getTipo();
				nomeSoggetto = soggettoConfig.getNome();
			}
			IDSoggetto idSoggetto = new IDSoggetto(tipoSoggetto, nomeSoggetto);
			
			if(soggettoRegistro!=null){
				
				return DBOggettiInUsoUtils.isSoggettoRegistryInUso(con, this.tipoDB, idSoggetto, whereIsInUso);
			
			}
			else{
				
				return DBOggettiInUsoUtils.isSoggettoConfigInUso(con, this.tipoDB, idSoggetto, whereIsInUso);
				
			}

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	/* *********** METODI EXISTS ****************** */
	
	public long existServizio(String nomeServizio, String tipoServizio, long idSoggettoErogatore) throws DriverControlStationException {
				String nomeMetodo = "existServizio";
		
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString;
		
		if (this.atomica) {
			try {
				con = this.datasource.getConnection();
			} catch (SQLException e) {
				throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] SQLException accedendo al datasource :" + e.getMessage());
			}
		} else {
			con = this.globalConnection;
		}
		
		try {
		
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.addWhereCondition("nome_servizio = ?");
			sqlQueryObject.addWhereCondition("tipo_servizio = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
		
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggettoErogatore);
			stmt.setString(2, nomeServizio);
			stmt.setString(3, tipoServizio);
		
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				return risultato.getLong("id");
			}
		
			return 0;
		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
		
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
		
	public long existServizio_implementazioneAccordoCheck(String nomeServizio, String tipoServizio, long idSoggettoErogatore, long idAccordo, boolean servizioCorrelato) throws DriverControlStationException {
		String nomeMetodo = "existServizio_implementazioneAccordoCheck";
		
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString;
		
		if (this.atomica) {
			try {
				con = this.datasource.getConnection();
			} catch (SQLException e) {
				throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] SQLException accedendo al datasource :" + e.getMessage());
		
			}
		} else {
			con = this.globalConnection;
		}
		
		try {
		
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.addWhereCondition("servizio_correlato = ?");
			sqlQueryObject.addWhereCondition("id_accordo = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
		
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggettoErogatore);
			stmt.setString(2, (servizioCorrelato ? CostantiRegistroServizi.ABILITATO.toString() : CostantiRegistroServizi.DISABILITATO.toString()));
			stmt.setLong(3, idAccordo);
		
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				if (tipoServizio.equals(risultato.getString("tipo_servizio")) && nomeServizio.equals(risultato.getString("nome_servizio")))
					continue;
				return risultato.getLong("id");
			}
		
			return 0;
		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
		
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	
	
	
	

	public boolean existsPoliticaSicurezzaServizioApplicativo(Long idServizioApplicativo) throws DriverControlStationException {
		String nomeMetodo = "existsPoliticaSicurezzaServizioApplicativo";

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] SQLException accedendo al datasource :" + e.getMessage());

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			boolean esiste = false;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.POLITICHE_SICUREZZA);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(CostantiDB.POLITICHE_SICUREZZA + ".id_servizio_applicativo=?");
			sqlQueryObject.addWhereCondition(CostantiDB.POLITICHE_SICUREZZA + ".id_fruitore = " + CostantiDB.SOGGETTI + ".id");
			sqlQueryObject.addWhereCondition(CostantiDB.POLITICHE_SICUREZZA + ".id_servizio = " + CostantiDB.SERVIZI + ".id");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(sqlQuery);

			stmt.setLong(1, idServizioApplicativo);

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idServizioApplicativo));
			risultato = stmt.executeQuery();

			if (risultato.next())
				esiste = true;

			risultato.close();
			stmt.close();

			return esiste;
		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public long existPdd(String nomePdd) throws DriverControlStationException {
		String nomeMetodo = "existPdd";

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString = "";

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] SQLException accedendo al datasource :" + e.getMessage());

			}

		} else {
			con = this.globalConnection;
		}

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PDD);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome = ?");
			queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);
			stmt.setString(1, nomePdd);
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				return risultato.getLong("id");
			}

			return 0;
		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	public boolean existsRuoloServizioApplicativo(Long idServizioApplicativo) throws DriverControlStationException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();
			} catch (SQLException e) {
				throw new DriverControlStationException("[DriverControlStationDB::existsRuoloServizioApplicativo] SQLException accedendo al datasource :" + e.getMessage());

			}

		} else
			con = this.globalConnection;

		try {
			
			boolean esiste = false;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.RUOLI_SA);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(CostantiDB.RUOLI_SA + ".id_servizio_applicativo=?");
			sqlQueryObject.addWhereCondition(CostantiDB.RUOLI_SA + ".id_accordo = " + CostantiDB.ACCORDI + ".id");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idServizioApplicativo);

			rs = stm.executeQuery();

			if (rs.next())
				esiste = true;

			rs.close();
			stm.close();

			return esiste;
		} catch (Exception qe) {
			throw new DriverControlStationException(qe);
		} finally {

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {

			}

		}

	}

	public boolean existsRuoloServizioApplicativo(Long idAccordo, Long idServizioApplicativo, int servizioCorrelato) throws DriverControlStationException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();
			} catch (SQLException e) {
				throw new DriverControlStationException("[DriverControlStationDB::existsRuoloServizioApplicativo] SQLException accedendo al datasource :" + e.getMessage());

			}

		} else
			con = this.globalConnection;

		try {
			boolean esiste = false;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.RUOLI_SA);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_accordo=?");
			sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
			sqlQueryObject.addWhereCondition("servizio_correlato=?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idAccordo);
			stm.setLong(2, idServizioApplicativo);
			stm.setInt(3, servizioCorrelato);

			rs = stm.executeQuery();

			if (rs.next())
				esiste = true;

			rs.close();
			stm.close();

			return esiste;
		} catch (Exception qe) {
			throw new DriverControlStationException(qe);
		} finally {

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {

			}

		}

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* *********** UTILITY PER PORTE DELEGATE ************ */
	
	public List<PortaDelegata> getPorteDelegateByAccordoRuolo(IDAccordo idAccordo) throws DriverControlStationException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString = "";

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();

			} catch (SQLException e) {
				throw new DriverControlStationException(e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			
			AccordoServizioParteComune as = this.getDriverRegistroServiziDB().getAccordoServizioParteComune(idAccordo);
			
			IDAccordo idAccordoCorrelato = this.idAccordoFactory.getIDAccordoFromValues(idAccordo.getNome()+"Correlato", idAccordo.getSoggettoReferente(), idAccordo.getVersione());
			
			List<PortaDelegata> pd = new ArrayList<PortaDelegata>();
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.RUOLI_SA);
			sqlQueryObject.addSelectField(CostantiDB.RUOLI_SA,"servizio_correlato");
			sqlQueryObject.addSelectField(CostantiDB.RUOLI_SA,"id_servizio_applicativo");
			sqlQueryObject.addWhereCondition(CostantiDB.RUOLI_SA+".id_accordo=?");
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, as.getId());
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				
				long idSA = risultato.getLong("id_servizio_applicativo");
				ServizioApplicativo sa = this.getServizioApplicativoWithRuolo(idSA);
				
				boolean saCorrelato = false;
				if(risultato.getInt("servizio_correlato")==1){
					saCorrelato = true;
				}
				
				String nomePD = sa.getNome() +"/" + sa.getTipoSoggettoProprietario()+sa.getNomeSoggettoProprietario()+ "/";
				if(saCorrelato){
					nomePD = nomePD + Ruolo.getNomeRuoloByIDAccordo(idAccordo);
				}else{
					nomePD = nomePD + Ruolo.getNomeRuoloByIDAccordo(idAccordoCorrelato);
				}
				
				IDPortaDelegata idPD = new IDPortaDelegata();
				idPD.setLocationPD(nomePD);
				idPD.setSoggettoFruitore(new IDSoggetto(sa.getTipoSoggettoProprietario(),sa.getNomeSoggettoProprietario()));
				
				if(this.getDriverConfigurazioneDB().existsPortaDelegata(idPD)){
					pd.add(this.getDriverConfigurazioneDB().getPortaDelegata(idPD));
				}
			}
			risultato.close();
			stmt.close();

			return pd;

		} catch (Exception se) {
			throw new DriverControlStationException(se);
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	

	


	
}
