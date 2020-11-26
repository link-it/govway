/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.driver;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.search.utils.RegistroCore;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.beans.InfoPolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsaPolicyAttiva;
import org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager;
import org.openspcoop2.core.controllo_traffico.utils.ControlloTrafficoDriverUtils;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.DBMappingUtils;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB_LIB;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.protocol.engine.archive.UtilitiesMappingFruizioneErogazione;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.web.ctrlstat.config.ConsoleProperties;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ControlStationLogger;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.lib.audit.DriverAudit;
import org.openspcoop2.web.lib.audit.DriverAuditDBAppender;
import org.openspcoop2.web.lib.users.DriverUsersDB;
import org.slf4j.Logger;

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
	private JDBCServiceManager jdbcServiceManagerControlloTraffico = null;

	private IDAccordoFactory idAccordoFactory = null;
//	@SuppressWarnings("unused")
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
	
	public JDBCServiceManager getJdbcServiceManagerControlloTraffico() {
		return this.jdbcServiceManagerControlloTraffico;
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
			boolean useSuperUser = false;
			if(ControlStationCore.isAPIMode()==false) {
				useSuperUser = !ConsoleProperties.getInstance().isVisibilitaOggettiGlobale();
			}
			this.configDB.setUseSuperUser(useSuperUser);
			this.regservDB.setUseSuperUser(useSuperUser);
			this.usersDB = new DriverUsersDB(connection, this.tipoDB, this.log);
			this.auditDB = new DriverAudit(connection, this.tipoDB);
			this.auditDBappender = new DriverAuditDBAppender(connection, this.tipoDB);
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(this.tipoDB);
			properties.setShowSql(true);
			this.jdbcServiceManagerControlloTraffico = new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(connection, properties, this.log);
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
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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

			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
		String nomeMetodo = "pddList(long idPdd)";

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

			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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

	public List<org.openspcoop2.core.config.Soggetto> pddSoggettiList(long idPdd, ISearch ricerca) throws DriverControlStationException {
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
			stmt.setLong(1, idPdd);
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

			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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

			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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

	
	
	
	
	
	
	
	
	/* ***** MAPPING **** */
	
	public void createMappingFruizionePortaDelegata(MappingFruizionePortaDelegata mapping) throws DriverControlStationException {
		String nomeMetodo = "createMappingFruizionePortaDelegata";

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

			DBMappingUtils.createMappingFruizione(mapping.getNome(), mapping.getDescrizione(), mapping.isDefault(), mapping.getIdServizio(), mapping.getIdFruitore(), mapping.getIdPortaDelegata(), con, this.tipoDB);

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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

		mapping.setTableId(this.getTableIdMappingFruizionePortaDelegata(mapping));
	}
	
	public void deleteMappingFruizionePortaDelegata(MappingFruizionePortaDelegata mapping) throws DriverControlStationException {
		String nomeMetodo = "deleteMappingFruizionePortaDelegata";

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

			DBMappingUtils.deleteMappingFruizione(mapping.getIdServizio(), mapping.getIdFruitore(), mapping.getIdPortaDelegata(), con, this.tipoDB);

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	public IDPortaDelegata getIDPortaDelegataAssociataDefault(IDServizio idServizio,IDSoggetto idFruitore) throws DriverControlStationException {
		String nomeMetodo = "getIDPortaDelegataAssociataDefault";

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

			return DBMappingUtils.getIDPortaDelegataAssociataDefault(idServizio, idFruitore, con, this.tipoDB);

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	public IDPortaDelegata getIDPortaDelegataAssociataAzione(IDServizio idServizio,IDSoggetto idFruitore) throws DriverControlStationException {
		String nomeMetodo = "getIDPortaDelegataAssociataAzione";

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

			return DBMappingUtils.getIDPortaDelegataAssociataAzione(idServizio, idFruitore, con, this.tipoDB);

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	public List<IDPortaDelegata> getIDPorteDelegateAssociate(IDServizio idServizio,IDSoggetto idFruitore) throws DriverControlStationException {
		String nomeMetodo = "getIDPorteDelegateAssociate";

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

			return DBMappingUtils.getIDPorteDelegateAssociate(idServizio, idFruitore, con, this.tipoDB);

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	public long getTableIdMappingFruizionePortaDelegata(MappingFruizionePortaDelegata mapping) throws DriverControlStationException {
		String nomeMetodo = "getTableIdMappingFruizionePortaDelegata";

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

			return DBMappingUtils.getTableIdMappingFruizione(mapping.getIdServizio(), mapping.getIdFruitore(), mapping.getIdPortaDelegata(), con, this.tipoDB);

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	public boolean existsMappingFruizionePortaDelegata(MappingFruizionePortaDelegata mapping) throws DriverControlStationException {
		String nomeMetodo = "existsMappingFruizionePortaDelegata";

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

			return DBMappingUtils.existsMappingFruizione(mapping.getIdServizio(), mapping.getIdFruitore(), mapping.getIdPortaDelegata(), con, this.tipoDB);

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	
	public void createMappingErogazionePortaApplicativa(MappingErogazionePortaApplicativa mapping) throws DriverControlStationException {
		String nomeMetodo = "createMappingErogazionePortaApplicativa";

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

			DBMappingUtils.createMappingErogazione(mapping.getNome(), mapping.getDescrizione(), mapping.isDefault(), mapping.getIdServizio(), mapping.getIdPortaApplicativa(), con, this.tipoDB);

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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

		mapping.setTableId(mapping.getTableId());
	}
	
	
	public void deleteMappingErogazionePortaApplicativa(MappingErogazionePortaApplicativa mapping) throws DriverControlStationException {
		String nomeMetodo = "deleteMappingErogazionePortaApplicativa";

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

			DBMappingUtils.deleteMappingErogazione(mapping.getIdServizio(), mapping.getIdPortaApplicativa(), con, this.tipoDB);

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	public IDPortaApplicativa getIDPortaApplicativaAssociataDefault(IDServizio idServizio) throws DriverControlStationException {
		String nomeMetodo = "getIDPortaApplicativaAssociataDefault";

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

			return DBMappingUtils.getIDPortaApplicativaAssociataDefault(idServizio, con, this.tipoDB);

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	public IDPortaApplicativa getIDPortaApplicativaAssociataAzione(IDServizio idServizio) throws DriverControlStationException {
		String nomeMetodo = "getIDPortaApplicativaAssociataAzione";

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

			return DBMappingUtils.getIDPortaApplicativaAssociataAzione(idServizio, con, this.tipoDB);

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	public List<IDPortaApplicativa> getIDPorteApplicativeAssociate(IDServizio idServizio) throws DriverControlStationException {
		String nomeMetodo = "getIDPorteApplicativeAssociate";

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

			return DBMappingUtils.getIDPorteApplicativeAssociate(idServizio, con, this.tipoDB);

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	public long getTableIdMappingErogazionePortaApplicativa(MappingErogazionePortaApplicativa mapping) throws DriverControlStationException {
		String nomeMetodo = "getTableIdMappingErogazionePortaApplicativa";

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

			return DBMappingUtils.getTableIdMappingErogazione(mapping.getIdServizio(), mapping.getIdPortaApplicativa(), con, this.tipoDB);

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	public boolean existsMappingErogazionePortaApplicativa(MappingErogazionePortaApplicativa mapping) throws DriverControlStationException {
		String nomeMetodo = "existsMappingErogazionePortaApplicativa";

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

			return DBMappingUtils.existsMappingErogazione(mapping.getIdServizio(), mapping.getIdPortaApplicativa(), con, this.tipoDB);

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	public void initMappingErogazione(Logger log) throws DriverControlStationException {
		this._initMapping(true,false,log);
	}	
	public void initMappingErogazione(boolean forceMapping, Logger log) throws DriverControlStationException {
		this._initMapping(true,forceMapping,log);
	}
	public void initMappingFruizione(Logger log) throws DriverControlStationException {
		this._initMapping(false,false,log);
	}	
	public void initMappingFruizione(boolean forceMapping, Logger log) throws DriverControlStationException {
		this._initMapping(false,forceMapping,log);
	}	
	private void _initMapping(boolean erogazione, boolean forceMapping, Logger log) throws DriverControlStationException {
		String nomeMetodo = "initMappingErogazione";
		if(!erogazione){
			nomeMetodo = "initMappingFruizione";
		}

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = this.datasource.getConnection();
				con.setAutoCommit(false);
			} catch (SQLException e) {
				throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] SQLException accedendo al datasource :" + e.getMessage());

			}

		} else {
			con = this.globalConnection;
		}

		log.debug("operazione this.atomica = " + this.atomica);

		try {

			int countMapping = -1;
			if(erogazione){
				countMapping = DBMappingUtils.countMappingErogazione(con, this.tipoDB);
			}
			else{
				countMapping = DBMappingUtils.countMappingFruizione(con, this.tipoDB);
			}
			log.debug("Count ["+nomeMetodo+"]: "+countMapping);
			if(countMapping<=0 || forceMapping){
				
				log.debug("Controllo Consistenza Dati ["+nomeMetodo+"] ...");
				
				DriverRegistroServiziDB driverRegistry = new DriverRegistroServiziDB(con, this.tipoDB);
				DriverConfigurazioneDB driverConfig = new DriverConfigurazioneDB(con, this.tipoDB);
				
				UtilitiesMappingFruizioneErogazione utilities = new UtilitiesMappingFruizioneErogazione(driverConfig, driverRegistry, log);
				if(erogazione){
					utilities.initMappingErogazione();
				}else{
					utilities.initMappingFruizione();
				}
	
				log.debug("Controllo Consistenza Dati ["+nomeMetodo+"] terminato");
			}			
			
		} catch (Throwable se) {
			log.error("Controllo Consistenza Dati ["+nomeMetodo+"] terminato con errore: "+se.getMessage(),se);
			error = true;
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* *********** DB UTILS ****************** */
	
	public long getTableIdSoggetto(IDSoggetto idSoggetto) throws DriverControlStationException {
		String nomeMetodo = "getTableIdSoggetto";

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

			return DBUtils.getIdSoggetto(idSoggetto.getNome(), idSoggetto.getTipo(), con, this.tipoDB);

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	public long getTableIdAccordoServizioParteSpecifica(IDServizio idAccordoServizioParteSpecifica) throws DriverControlStationException {
		String nomeMetodo = "getTableIdAccordoServizioParteSpecifica";

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

			return DBUtils.getIdAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica, con, this.tipoDB);

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	public long getTableIdPortaDelegata(IDPortaDelegata idPD) throws DriverControlStationException {
		String nomeMetodo = "getTableIdPortaDelegata";

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

			return DBUtils.getIdPortaDelegata(idPD.getNome(), con, this.tipoDB);

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	public long getTableIdPortaApplicativa(IDPortaApplicativa idPA) throws DriverControlStationException {
		String nomeMetodo = "getTableIdPortaApplicativa";

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

			return DBUtils.getIdPortaApplicativa(idPA.getNome(), con, this.tipoDB);

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* *********** METODI IS IN USO ****************** */

	public boolean isAccordoInUso(IDAccordo idAccordo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverControlStationException {
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
			return DBOggettiInUsoUtils.isAccordoServizioParteComuneInUso(con, this.tipoDB, idAccordo, whereIsInUso, normalizeObjectIds);

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	public boolean isAccordoInUso(AccordoServizioParteComune as, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverControlStationException {
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
			return DBOggettiInUsoUtils.isAccordoServizioParteComuneInUso(con, this.tipoDB, this.idAccordoFactory.getIDAccordoFromAccordo(as), whereIsInUso, normalizeObjectIds);

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	public boolean isAccordoCooperazioneInUso(AccordoCooperazione as, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverControlStationException {
		String nomeMetodo = "isAccordoCooperazioneInUso";

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
			return DBOggettiInUsoUtils.isAccordoCooperazioneInUso(con, this.tipoDB, this.idAccordoCooperazioneFactory.getIDAccordoFromAccordo(as), whereIsInUso, normalizeObjectIds);

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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

	public boolean isPddInUso(PdDControlStation pdd, List<String> whereIsInUso, boolean normalizeObjectIds) throws DriverControlStationException {
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

			return DBOggettiInUsoUtils.isPddInUso(con, this.tipoDB, pdd.getNome(), whereIsInUso, normalizeObjectIds);

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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

	public boolean isSoggettoInUso(org.openspcoop2.core.config.Soggetto soggettoConfig, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverControlStationException {
		return isSoggettoInUso(soggettoConfig,null,whereIsInUso, normalizeObjectIds);
	}
	public boolean isSoggettoInUso(Soggetto soggettoRegistro, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverControlStationException {
		return isSoggettoInUso(null,soggettoRegistro,whereIsInUso, normalizeObjectIds);
	}
	private boolean isSoggettoInUso(org.openspcoop2.core.config.Soggetto soggettoConfig, Soggetto soggettoRegistro, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverControlStationException {
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
				
				return DBOggettiInUsoUtils.isSoggettoRegistryInUso(con, this.tipoDB, idSoggetto, true, whereIsInUso, normalizeObjectIds);
			
			}
			else{
				
				return DBOggettiInUsoUtils.isSoggettoConfigInUso(con, this.tipoDB, idSoggetto, true, whereIsInUso, normalizeObjectIds);
				
			}

		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	public boolean isRuoloInUso(IDRuolo idRuolo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverControlStationException {
		String nomeMetodo = "isRuoloInUso";

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

			return DBOggettiInUsoUtils.isRuoloInUso(con, this.tipoDB, idRuolo, whereIsInUso, normalizeObjectIds);
			
		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	public boolean isRuoloConfigInUso(IDRuolo idRuolo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverControlStationException {
		String nomeMetodo = "isRuoloConfigInUso";

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

			return DBOggettiInUsoUtils.isRuoloConfigInUso(con, this.tipoDB, idRuolo, whereIsInUso, normalizeObjectIds);
			
		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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

	
	public boolean isScopeInUso(IDScope idScope, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverControlStationException {
		String nomeMetodo = "isScopeInUso";

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

			return DBOggettiInUsoUtils.isScopeInUso(con, this.tipoDB, idScope, whereIsInUso, normalizeObjectIds);
			
		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	public boolean isScopeConfigInUso(IDScope idScope, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverControlStationException {
		String nomeMetodo = "isScopeConfigInUso";

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

			return DBOggettiInUsoUtils.isScopeConfigInUso(con, this.tipoDB, idScope, whereIsInUso, normalizeObjectIds);
			
		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	public long existServizio(String nomeServizio, String tipoServizio, int versioneServizio, long idSoggettoErogatore) throws DriverControlStationException {
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
			sqlQueryObject.addWhereCondition("versione_servizio = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
		
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggettoErogatore);
			stmt.setString(2, nomeServizio);
			stmt.setString(3, tipoServizio);
			stmt.setInt(4, versioneServizio);
		
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				return risultato.getLong("id");
			}
		
			return 0;
		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
				try{
					return risultato.getLong("id");
				}finally{
					try {
						if (risultato != null) {
							risultato.close();
						}
					} catch (Exception e) {
						// ignore
					}
				}
			}
		
			return 0;
		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	

	// Controllo Traffico
	/**
	 * Restituisce la configurazione generale
	 * 
	 * @return Configurazione
	 * 
	 */
	public ConfigurazioneGenerale getConfigurazioneControlloTraffico() throws DriverControlStationException,DriverControlStationNotFound {
		String nomeMetodo = "getConfigurazioneControlloTraffico";
		// ritorna la configurazione controllo del traffico della PdD
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
		ConfigurazioneGenerale config = null;
		try {
			config = ControlloTrafficoDriverUtils.getConfigurazioneControlloTraffico(con, this.log, this.tipoDB);
		}catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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

		return config;
	}

	/***
	 * Aggiorna la configurazione del controllo del traffico
	 * 
	 * @param configurazioneControlloTraffico
	 * @throws DriverControlStationException
	 */
	public void updateConfigurazioneControlloTraffico(ConfigurazioneGenerale configurazioneControlloTraffico) throws DriverControlStationException {
		String nomeMetodo = "updateConfigurazioneControlloTraffico";
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
			ControlloTrafficoDriverUtils.updateConfigurazioneControlloTraffico(configurazioneControlloTraffico, con, this.log, this.tipoDB);
		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	/**
	 * Restituisce il numero di Configurazione Policy
	 * 
	 * @return Configurazione
	 * 
	 */
	public long countConfigurazioneControlloTrafficoConfigurazionePolicy(ISearch ricerca) throws DriverControlStationException {
		String nomeMetodo = "countConfigurazioneControlloTrafficoConfigurazionePolicy";
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
		long count = 0;
		try {
			count = ControlloTrafficoDriverUtils.countConfigurazioneControlloTrafficoConfigurazionePolicy(ricerca, con, this.log, this.tipoDB);
		}catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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

		return count;
	}
	
	/**
	 * Restituisce il numero di Attivazione Policy
	 * 
	 * @return Configurazione
	 * 
	 */
	public long countConfigurazioneControlloTrafficoAttivazionePolicy(ISearch ricerca, RuoloPolicy ruoloPorta, String nomePorta) throws DriverControlStationException {
		String nomeMetodo = "countConfigurazioneControlloTrafficoAttivazionePolicy";
		// ritorna la configurazione controllo del traffico della PdD
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
		long count = 0;
		try {
			count = ControlloTrafficoDriverUtils.countConfigurazioneControlloTrafficoAttivazionePolicy(ricerca, ruoloPorta, nomePorta, con, this.log, this.tipoDB);
		}catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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

		return count;
	}

	public List<ConfigurazionePolicy> configurazioneControlloTrafficoConfigurazionePolicyList(Search ricerca) throws DriverControlStationException{
		String nomeMetodo = "configurazioneControlloTrafficoConfigurazionePolicyList";
		// ritorna la configurazione controllo del traffico della PdD
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
		List<ConfigurazionePolicy> listaPolicy = new ArrayList<ConfigurazionePolicy>();
		
		try {
			listaPolicy = ControlloTrafficoDriverUtils.configurazioneControlloTrafficoConfigurazionePolicyList(ricerca, con, this.log, this.tipoDB);				
		} catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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
		
		return listaPolicy;
	}

	@SuppressWarnings("unchecked")
	public List<AttivazionePolicy> configurazioneControlloTrafficoAttivazionePolicyList(Search ricerca, RuoloPolicy ruoloPorta, String nomePorta) throws DriverControlStationException{
		return (List<AttivazionePolicy>) this._configurazioneControlloTrafficoAttivazionePolicyList(ricerca, ruoloPorta, nomePorta, 
				false, "configurazioneControlloTrafficoAttivazionePolicyList",
				null, null, null,
				null, null,
				null, null);
	}
	@SuppressWarnings("unchecked")
	public List<AttivazionePolicy> configurazioneControlloTrafficoAttivazionePolicyListByFilter(Search ricerca, RuoloPolicy ruoloPorta, String nomePorta,
			IDSoggetto filtroSoggettoFruitore, IDServizioApplicativo filtroApplicativoFruitore,String filtroRuoloFruitore,
			IDSoggetto filtroSoggettoErogatore, String filtroRuoloErogatore,
			IDServizio filtroServizioAzione, String filtroRuolo) throws DriverControlStationException{
		return (List<AttivazionePolicy>) this._configurazioneControlloTrafficoAttivazionePolicyList(ricerca, ruoloPorta, nomePorta, 
				false, "configurazioneControlloTrafficoAttivazionePolicyListByFilter",
				filtroSoggettoFruitore, filtroApplicativoFruitore, filtroRuoloFruitore,
				filtroSoggettoErogatore, filtroRuoloErogatore,
				filtroServizioAzione, filtroRuolo);
	}
	@SuppressWarnings("unchecked")
	public List<TipoRisorsaPolicyAttiva> configurazioneControlloTrafficoAttivazionePolicyTipoRisorsaList(Search ricerca, RuoloPolicy ruoloPorta, String nomePorta) throws DriverControlStationException{
		return (List<TipoRisorsaPolicyAttiva>) this._configurazioneControlloTrafficoAttivazionePolicyList(ricerca, ruoloPorta, nomePorta, 
				true, "configurazioneControlloTrafficoAttivazionePolicyTipoRisorsaList",
				null, null, null,
				null, null,
				null, null);
	}
	@SuppressWarnings("unchecked")
	private Object _configurazioneControlloTrafficoAttivazionePolicyList(Search ricerca, RuoloPolicy ruoloPorta, String nomePorta, boolean tipoRisorsa, String nomeMetodo,
			IDSoggetto filtroSoggettoFruitore, IDServizioApplicativo filtroApplicativoFruitore,String filtroRuoloFruitore,
			IDSoggetto filtroSoggettoErogatore, String filtroRuoloErogatore,
			IDServizio filtroServizioAzione, String filtroRuolo) throws DriverControlStationException{
		// ritorna la configurazione controllo del traffico della PdD
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
		List<AttivazionePolicy> listaPolicy = null;
		List<TipoRisorsaPolicyAttiva> listaTipoRisorsa = null;
		
		try {
		
			Object o = ControlloTrafficoDriverUtils._configurazioneControlloTrafficoAttivazionePolicyList(ricerca, ruoloPorta, nomePorta, 
					con, this.log, this.tipoDB,
					tipoRisorsa, nomeMetodo, 
					filtroSoggettoFruitore, filtroApplicativoFruitore, filtroRuoloFruitore,
					filtroSoggettoErogatore, filtroRuoloErogatore,
					filtroServizioAzione, filtroRuolo);
			
			if(tipoRisorsa) {
				listaTipoRisorsa = (List<TipoRisorsaPolicyAttiva>) o;
			}
			else {
				listaPolicy = (List<AttivazionePolicy>) o;
			}
						
		} catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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
		
		if(tipoRisorsa) {
			return listaTipoRisorsa;
		}
		else {
			return listaPolicy;
		}
	}
	
	public AttivazionePolicy getAttivazionePolicy(String alias, RuoloPolicy ruoloPorta, String nomePorta) throws DriverControlStationException, NotFoundException{
		String nomeMetodo = "configurazioneControlloTrafficoAttivazionePolicyList";
		// ritorna la configurazione controllo del traffico della PdD
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
		AttivazionePolicy policy = null;
		
		try {
			policy = ControlloTrafficoDriverUtils.getAttivazionePolicy(alias, ruoloPorta, nomePorta, con, this.log, this.tipoDB);			
		} catch(NotFoundException notFound) {
			throw notFound;
		} 
		catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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
		
		return policy;
	}
	
	
	public List<InfoPolicy> getInfoPolicyList(Boolean builtIn, String idPolicyParam) throws DriverControlStationException{
		String nomeMetodo = "getInfoPolicyList";
		// ritorna la configurazione controllo del traffico della PdD
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
		List<InfoPolicy> listaPolicy = new ArrayList<InfoPolicy>();
		
		try {
			listaPolicy = ControlloTrafficoDriverUtils.getInfoPolicyList(builtIn, idPolicyParam, con, this.log, this.tipoDB); 			
		} catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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
		
		return listaPolicy;
	}
	
	public List<AttivazionePolicy> findInUseAttivazioni(String idPolicy, boolean escludiDisabilitate) throws DriverControlStationException{
		String nomeMetodo = "findInUseAttivazioni";
		// ritorna la configurazione controllo del traffico della PdD
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
		List<AttivazionePolicy> listaPolicy = new ArrayList<AttivazionePolicy>();
		
		try {
			listaPolicy = ControlloTrafficoDriverUtils.findInUseAttivazioni(idPolicy, escludiDisabilitate, con, this.log, this.tipoDB);
		} catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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
		
		return listaPolicy;
	}
	
	public long countInUseAttivazioni(String idPolicy, boolean escludiDisabilitate) throws DriverControlStationException{
		String nomeMetodo = "countInUseAttivazioni";
		// ritorna la configurazione controllo del traffico della PdD
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
			return ControlloTrafficoDriverUtils.countInUseAttivazioni(idPolicy, escludiDisabilitate, con, this.log, this.tipoDB);
		} catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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
	
	public ConfigurazionePolicy getConfigurazionePolicy(long id) throws DriverControlStationException,DriverControlStationNotFound{
		String nomeMetodo = "getConfigurazionePolicy";
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
		ConfigurazionePolicy policy = null;
		
		try {
			policy = ControlloTrafficoDriverUtils.getConfigurazionePolicy(id, con, this.log, this.tipoDB);
		}catch (NotFoundException e) {
			throw new DriverControlStationNotFound("[DriverControlStationDB::" + nomeMetodo + "] Configurazione Policy non presente.");
		} catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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
		
		return policy;
	}
	
	public ConfigurazionePolicy getConfigurazionePolicy(String nomePolicy) throws DriverControlStationException,DriverControlStationNotFound{
		String nomeMetodo = "getConfigurazionePolicy";
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
		ConfigurazionePolicy policy = null;
		
		try {
			policy = ControlloTrafficoDriverUtils.getConfigurazionePolicy(nomePolicy, con, this.log, this.tipoDB);
		}catch (NotFoundException e) {
			throw new DriverControlStationNotFound("[DriverControlStationDB::" + nomeMetodo + "] Configurazione Policy non presente.");
		} catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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
		
		return policy;
	}

	public void createConfigurazionePolicy(ConfigurazionePolicy policy) throws DriverControlStationException{
		String nomeMetodo = "createConfigurazionePolicy";
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
			ControlloTrafficoDriverUtils.createConfigurazionePolicy(policy, con, this.log, this.tipoDB);
		} catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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

	public void createAttivazionePolicy(AttivazionePolicy policy) throws DriverControlStationException {
		String nomeMetodo = "createAttivazionePolicy";
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
			ControlloTrafficoDriverUtils.createAttivazionePolicy(policy, con, this.log, this.tipoDB);		
		} catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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

	public void updateConfigurazionePolicy(ConfigurazionePolicy policy)throws DriverControlStationException,DriverControlStationNotFound{
		String nomeMetodo = "updateConfigurazionePolicy";
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
			ControlloTrafficoDriverUtils.updateConfigurazionePolicy(policy, con, this.log, this.tipoDB);
		}catch (NotFoundException e) {
			throw new DriverControlStationNotFound("[DriverControlStationDB::" + nomeMetodo + "] Configurazione Policy non presente.");
		} catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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

	public void updateAttivazionePolicy(AttivazionePolicy policy) throws DriverControlStationException,DriverControlStationNotFound{
		String nomeMetodo = "updateAttivazionePolicy";
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
			ControlloTrafficoDriverUtils.updateAttivazionePolicy(policy, con, this.log, this.tipoDB);
		}catch (NotFoundException e) {
			throw new DriverControlStationNotFound("[DriverControlStationDB::" + nomeMetodo + "] Configurazione Policy non presente.");
		} catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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

	public void deleteConfigurazionePolicy(ConfigurazionePolicy policy) throws DriverControlStationException {
		String nomeMetodo = "deleteConfigurazionePolicy";
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
			ControlloTrafficoDriverUtils.deleteConfigurazionePolicy(policy, con, this.log, this.tipoDB);			
		} catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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

	public void deleteAttivazionePolicy(AttivazionePolicy policy) throws DriverControlStationException,DriverControlStationNotFound{
		String nomeMetodo = "deleteAttivazionePolicy";
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
			ControlloTrafficoDriverUtils.deleteAttivazionePolicy(policy, con, this.log, this.tipoDB);
		} catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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
	
	public AttivazionePolicy getAttivazionePolicy(long id) throws DriverControlStationException,DriverControlStationNotFound{
		String nomeMetodo = "getAttivazionePolicy"; 
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
		AttivazionePolicy policy = null;
		
		try {
			policy = ControlloTrafficoDriverUtils.getAttivazionePolicy(id, con, this.log, this.tipoDB);
		}catch (NotFoundException e) {
			throw new DriverControlStationNotFound("[DriverControlStationDB::" + nomeMetodo + "] Configurazione Policy non presente.");
		} catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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
		
		return policy;
	}
	
	public AttivazionePolicy getAttivazionePolicy(String nomePolicy) throws DriverControlStationException,DriverControlStationNotFound{
		String nomeMetodo = "getAttivazionePolicy"; 
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
		AttivazionePolicy policy = null;
		
		try {
			policy = ControlloTrafficoDriverUtils.getAttivazionePolicy(nomePolicy, con, this.log, this.tipoDB);
		}catch (NotFoundException e) {
			throw new DriverControlStationNotFound("[DriverControlStationDB::" + nomeMetodo + "] Configurazione Policy non presente.");
		} catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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
		
		return policy;
	}
	
	public Integer getFreeCounterForGlobalPolicy(String policyId) throws DriverControlStationException{
		String nomeMetodo = "getFreeCounterForGlobalPolicy"; 
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
		
		try{
			return ControlloTrafficoDriverUtils.getFreeCounterForGlobalPolicy(policyId, con, this.log, this.tipoDB);		
		} catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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
	
	public AttivazionePolicy getPolicy(String policyId, AttivazionePolicyFiltro filtroParam, AttivazionePolicyRaggruppamento groupBy,
			RuoloPolicy ruoloPorta, String nomePorta) throws DriverControlStationException,DriverControlStationNotFound{
		String nomeMetodo = "getPolicy"; 
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
		try{
			return ControlloTrafficoDriverUtils.getPolicy(policyId, filtroParam, groupBy,
					ruoloPorta, nomePorta, con, this.log, this.tipoDB);
		}catch (NotFoundException e) {
			throw new DriverControlStationNotFound("[DriverControlStationDB::" + nomeMetodo + "] Attivazione Policy non presente.");	
		} catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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
	
	public AttivazionePolicy getPolicyByAlias(String alias,
			RuoloPolicy ruoloPorta, String nomePorta) throws DriverControlStationException,DriverControlStationNotFound{
		String nomeMetodo = "getPolicyByAlias"; 
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
		try{
			return ControlloTrafficoDriverUtils.getPolicyByAlias(alias,
					ruoloPorta, nomePorta, con, this.log, this.tipoDB);
		}catch (NotFoundException e) {
			throw new DriverControlStationNotFound("[DriverControlStationDB::" + nomeMetodo + "] Attivazione Policy non presente.");
		} catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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
	
	public List<AttivazionePolicy> getPolicyByServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws DriverControlStationException,DriverControlStationNotFound{
		String nomeMetodo = "getPolicyByServizioApplicativo"; 
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
		try{
			return ControlloTrafficoDriverUtils.getPolicyByServizioApplicativo(idServizioApplicativo, con, this.log, this.tipoDB);
		}catch (NotFoundException e) {
			throw new DriverControlStationNotFound("[DriverControlStationDB::" + nomeMetodo + "] Attivazione Policy non presente.");
		} catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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
	
	public boolean usedInConfigurazioneControlloTrafficoAttivazionePolicy(RuoloPolicy ruoloPorta, String nomePorta, String azione) throws DriverControlStationException{
		String nomeMetodo = "usedInConfigurazioneControlloTrafficoAttivazionePolicy";
		// ritorna la configurazione controllo del traffico della PdD
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
			return ControlloTrafficoDriverUtils.usedInConfigurazioneControlloTrafficoAttivazionePolicy(ruoloPorta, nomePorta, azione, con, this.log, this.tipoDB);			
		} catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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
	
	public List<IDSoggetto> getSoggettiErogatori(String protocolloSelezionato,List<String> protocolliSupportati) throws DriverControlStationException{
		String nomeMetodo = "getSoggettiErogatori"; 
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
		
		try{
			org.openspcoop2.core.commons.search.dao.jdbc.JDBCServiceManager serviceManager = RegistroCore.getServiceManager(this.log, this.tipoDB, con); 
			
			if(protocolloSelezionato!=null) {
				return RegistroCore.getSoggettiErogatori(serviceManager, protocolloSelezionato);
			}
			else {
				return RegistroCore.getSoggettiErogatori(serviceManager, protocolliSupportati);
			}
		}catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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
	public List<IDServizio> getServizi(String protocolloSelezionato,List<String> protocolliSupportati, 
			String tipoErogatore, String nomeErogatore) throws DriverControlStationException{
		String nomeMetodo = "getServizi"; 
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
		try{
			org.openspcoop2.core.commons.search.dao.jdbc.JDBCServiceManager serviceManager = RegistroCore.getServiceManager(this.log, this.tipoDB, con);
			if(protocolloSelezionato!=null) {
				return RegistroCore.getServizi(serviceManager, protocolloSelezionato, tipoErogatore, nomeErogatore);
			}
			else{
				return RegistroCore.getServizi(serviceManager, protocolliSupportati, tipoErogatore, nomeErogatore);
			}
		}catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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
	public List<String> getAzioni(String protocolloSelezionato,List<String> protocolliSupportati, 
			String tipoErogatore, String nomeErogatore, String tipoServizio, String nomeServizio, Integer versioneServizio) throws DriverControlStationException{
		String nomeMetodo = "getAzioni"; 
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
		try{
			org.openspcoop2.core.commons.search.dao.jdbc.JDBCServiceManager serviceManager = RegistroCore.getServiceManager(this.log, this.tipoDB, con);
			if(protocolloSelezionato!=null) {
				return RegistroCore.getAzioni(serviceManager, protocolloSelezionato, 
						tipoErogatore, nomeErogatore, tipoServizio, nomeServizio, versioneServizio);
			}
			else {
				return RegistroCore.getAzioni(serviceManager, protocolliSupportati, 
						tipoErogatore, nomeErogatore, tipoServizio, nomeServizio, versioneServizio);
			}
		}catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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
	public List<IDServizioApplicativo> getServiziApplicativiErogatori(String protocolloSelezionato,List<String> protocolliSupportati, 
			String tipoErogatore, String nomeErogatore, String tipoServizio, String nomeServizio, Integer versioneServizio, 
			String azione) throws DriverControlStationException {
		String nomeMetodo = "getServiziApplicativiErogatori"; 
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
		try{
			org.openspcoop2.core.commons.search.dao.jdbc.JDBCServiceManager serviceManager = RegistroCore.getServiceManager(this.log, this.tipoDB, con);
			if(protocolloSelezionato!=null) {
				return RegistroCore.getServiziApplicativiErogatori(serviceManager, protocolloSelezionato, 
						tipoErogatore, nomeErogatore, tipoServizio, nomeServizio, versioneServizio, 
						azione);
			}
			else {
				return RegistroCore.getServiziApplicativiErogatori(serviceManager, protocolliSupportati, 
						tipoErogatore, nomeErogatore, tipoServizio, nomeServizio, versioneServizio, 
						azione);
			}
		}catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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
	public List<IDSoggetto> getSoggetti(String protocolloSelezionato,List<String> protocolliSupportati) throws DriverControlStationException{
		String nomeMetodo = "getSoggetti"; 
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
		try{
			org.openspcoop2.core.commons.search.dao.jdbc.JDBCServiceManager serviceManager = RegistroCore.getServiceManager(this.log, this.tipoDB, con);
			if(protocolloSelezionato!=null) {
				return RegistroCore.getSoggetti(serviceManager, protocolloSelezionato);
			}
			else {
				return RegistroCore.getSoggetti(serviceManager, protocolliSupportati);
			}
		}catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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
	public List<IDSoggetto> getSoggettiFruitori(String protocolloSelezionato,List<String> protocolliSupportati, 
			String tipoErogatore, String nomeErogatore, String tipoServizio, String nomeServizio, Integer versioneServizio) throws DriverControlStationException{
		String nomeMetodo = "getSoggettiFruitori"; 
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
		try{
			org.openspcoop2.core.commons.search.dao.jdbc.JDBCServiceManager serviceManager = RegistroCore.getServiceManager(this.log, this.tipoDB, con);
			if(protocolloSelezionato!=null) {
				return RegistroCore.getSoggettiFruitori(serviceManager, protocolloSelezionato, 
						tipoErogatore, nomeErogatore, tipoServizio, nomeServizio, versioneServizio);
			}
			else {
				return RegistroCore.getSoggettiFruitori(serviceManager, protocolliSupportati, 
						tipoErogatore, nomeErogatore, tipoServizio, nomeServizio, versioneServizio);
			}
		}catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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
	public List<IDServizioApplicativo> getServiziApplicativiFruitore(String protocolloSelezionato,List<String> protocolliSupportati, 
			String tipoFruitore, String nomeFruitore,	
			String tipoErogatore, String nomeErogatore, String tipoServizio, String nomeServizio, Integer versioneServizio,
			String azione) throws DriverControlStationException{
		String nomeMetodo = "getServiziApplicativiFruitore"; 
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
		try{
			org.openspcoop2.core.commons.search.dao.jdbc.JDBCServiceManager serviceManager = RegistroCore.getServiceManager(this.log, this.tipoDB, con);
			if(protocolloSelezionato!=null) {
				return RegistroCore.getServiziApplicativiFruitore(serviceManager, protocolloSelezionato, 
						tipoFruitore, nomeFruitore, 
						tipoErogatore, nomeErogatore, tipoServizio, nomeServizio, versioneServizio, 
						azione);
			}
			else {
				return RegistroCore.getServiziApplicativiFruitore(serviceManager, protocolliSupportati, 
						tipoFruitore, nomeFruitore, 
						tipoErogatore, nomeErogatore, tipoServizio, nomeServizio, versioneServizio, 
						azione);
			}
		}catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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
	public List<IDServizioApplicativo> getServiziApplicativiFruitore(String protocolloSelezionato,List<String> protocolliSupportati, 
			String tipoFruitore, String nomeFruitore) throws DriverControlStationException{
		String nomeMetodo = "getServiziApplicativiFruitore"; 
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
		try{
			org.openspcoop2.core.commons.search.dao.jdbc.JDBCServiceManager serviceManager = RegistroCore.getServiceManager(this.log, this.tipoDB, con);
			if(protocolloSelezionato!=null) {
				return RegistroCore.getServiziApplicativiFruitore(serviceManager, protocolloSelezionato, 
						tipoFruitore, nomeFruitore);
			}
			else {
				return RegistroCore.getServiziApplicativiFruitore(serviceManager, protocolliSupportati, 
						tipoFruitore, nomeFruitore);
			}
		}catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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
	public List<IDServizioApplicativo> getServiziApplicativi(String protocolloSelezionato,List<String> protocolliSupportati, 
			String tipoProprietario, String nomeProprietario) throws DriverControlStationException{
		String nomeMetodo = "getServiziApplicativi"; 
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
		try{
			org.openspcoop2.core.commons.search.dao.jdbc.JDBCServiceManager serviceManager = RegistroCore.getServiceManager(this.log, this.tipoDB, con);
			if(protocolloSelezionato!=null) {
				return RegistroCore.getServiziApplicativi(serviceManager, protocolloSelezionato, 
						tipoProprietario, nomeProprietario);
			}
			else {
				return RegistroCore.getServiziApplicativi(serviceManager, protocolliSupportati, 
						tipoProprietario, nomeProprietario);
			}
		}catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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
	
	public List<IDServizio> getErogazioni(List<String> protocolli, 
			String gruppo,
			String tipoSoggetto, String nomeSoggetto) throws Exception {
		return  getErogazioni(protocolli, 
				gruppo,
				tipoSoggetto, nomeSoggetto,
				null, null, null,
				null);
	}
	public List<IDServizio> getErogazioni(List<String> protocolli, 
			String gruppo,
			String tipoSoggetto, String nomeSoggetto,
			String tipoServizio, String nomeServizio, Integer versioneServizio,
			String nomeAzione) throws DriverControlStationException{
		String nomeMetodo = "getErogazioni"; 
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
		try{
			org.openspcoop2.core.commons.search.dao.jdbc.JDBCServiceManager serviceManager = RegistroCore.getServiceManager(this.log, this.tipoDB, con);
			return RegistroCore.getErogazioni(serviceManager, protocolli, gruppo, tipoSoggetto, nomeSoggetto, tipoServizio, nomeServizio, versioneServizio, nomeAzione);
		}catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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
	
	public List<IDFruizione> getFruizioni(List<String> protocolli, 
			String gruppo,
			String tipoSoggetto, String nomeSoggetto) throws Exception {
		return getFruizioni(protocolli, 
				gruppo,
				tipoSoggetto, nomeSoggetto, 
				null, null,
				null ,null, null, 
				null);
	}
	public List<IDFruizione> getFruizioni(List<String> protocolli, 
			String gruppo,
			String tipoSoggetto, String nomeSoggetto, 
			String tipoErogatore, String nomeErogatore,
			String tipoServizio ,String nomeServizio, Integer versioneServizio, 
			String nomeAzione) throws DriverControlStationException{
		String nomeMetodo = "getFruizioni"; 
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
		try{
			org.openspcoop2.core.commons.search.dao.jdbc.JDBCServiceManager serviceManager = RegistroCore.getServiceManager(this.log, this.tipoDB, con);
			return RegistroCore.getFruizioni(serviceManager, protocolli, gruppo, tipoSoggetto, nomeSoggetto, tipoErogatore, nomeErogatore, tipoServizio, nomeServizio, versioneServizio, nomeAzione);
		}catch (Exception qe) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
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
	
	public boolean isGruppoInUso(IDGruppo idGruppo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverControlStationException {
		String nomeMetodo = "isGruppoInUso";

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

			return DBOggettiInUsoUtils.isGruppoInUso(con, this.tipoDB, idGruppo, whereIsInUso, normalizeObjectIds);
			
		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	public boolean isGruppoConfigInUso(IDGruppo idRuolo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverControlStationException {
		String nomeMetodo = "isGruppoConfigInUso";

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

			return DBOggettiInUsoUtils.isGruppoConfigInUso(con, this.tipoDB, idRuolo, whereIsInUso, normalizeObjectIds);
			
		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	public boolean isRisorsaInUso(IDResource idRisorsa, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverControlStationException {
		String nomeMetodo = "isRisorsaInUso";

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

			return DBOggettiInUsoUtils.isRisorsaInUso(con, this.tipoDB, idRisorsa, whereIsInUso, normalizeObjectIds);
			
		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	public boolean isPortTypeInUso(IDPortType idPT, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverControlStationException {
		String nomeMetodo = "isPortTypeInUso";

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

			return DBOggettiInUsoUtils.isPortTypeInUso(con, this.tipoDB, idPT, whereIsInUso, normalizeObjectIds);
			
		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	public boolean isOperazioneInUso(IDPortTypeAzione idOperazione, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverControlStationException {
		String nomeMetodo = "isOperazioneInUso";

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

			return DBOggettiInUsoUtils.isOperazioneInUso(con, this.tipoDB, idOperazione, whereIsInUso, normalizeObjectIds);
			
		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
}
