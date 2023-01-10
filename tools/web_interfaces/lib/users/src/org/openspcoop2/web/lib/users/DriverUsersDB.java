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


package org.openspcoop2.web.lib.users;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.commons.search.dao.IDBAccordoServizioParteSpecificaServiceSearch;
import org.openspcoop2.core.commons.search.dao.IDBSoggettoServiceSearch;
import org.openspcoop2.core.commons.search.dao.jdbc.JDBCServiceManager;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerProperties;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.web.lib.users.dao.InterfaceType;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;
import org.openspcoop2.web.lib.users.dao.Stato;
import org.openspcoop2.web.lib.users.dao.UserObjects;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.lib.users.dao.UserPassword;
import org.slf4j.Logger;

/**
 * Sono forniti metodi per la lettura dei dati di Users
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */

public class DriverUsersDB {

	 private static final String LOG_DRIVER_DB_USER = "DRIVER_DB_USER";
	
	/** 
	 * DataSource
	 */
	private DataSource datasource = null;
	/** 
	 * Connessione
	 */
	private Connection connection = null;
	private boolean connectionOpenViaJDBCInCostructor = false;
	/**
	 * SQLQueryObject
	 */
	String tipoDatabase = null;
	/** 
	 * Logger
	 */
	private Logger log;

	
	public void close() throws DriverTracciamentoException {
		try{
			if(this.connectionOpenViaJDBCInCostructor){
				if(this.connection!=null && this.connection.isClosed()==false){
					this.connection.close();
				}
			}
		}catch(Exception e){
			throw new DriverTracciamentoException(e.getMessage(),e);
		}
	}
	
	
	
	public DriverUsersDB(String nomeDataSource, String tipoDatabase, Properties jndiContext) throws DriverUsersDBException {
		this(nomeDataSource, tipoDatabase, jndiContext, null);
	}
	public DriverUsersDB(String nomeDataSource, String tipoDatabase, Properties jndiContext, Logger log) throws DriverUsersDBException {
		
		// Logger
		try {
			if(log==null)
				this.log = LoggerWrapperFactory.getLogger(LOG_DRIVER_DB_USER);
			else
				this.log = log;
		} catch (Exception e) {
			throw new DriverUsersDBException("Errore durante l'inizializzazione del logger...",e);
		}

		// Datasource
		try {
			this.log.info("Inizializzo DriverUsersDB...");
			GestoreJNDI gestoreJNDI = new GestoreJNDI(jndiContext);
			this.datasource = (DataSource) gestoreJNDI.lookup(nomeDataSource);
			if (this.datasource == null)
				throw new Exception ("datasource is null");

			this.log.info("Inizializzo DriverUsersDB terminata.");
		} catch (Exception e) {
			this.log.error("Errore durante la ricerca del datasource...",e);
			throw new DriverUsersDBException("Errore durante la ricerca del datasource...",e);
		}

		// ISQLQueryObject
		try {
			this.log.info("Inizializzo ISQLQueryObject...");
			if (TipiDatabase.isAMember(tipoDatabase)) {
				this.tipoDatabase = tipoDatabase;				
			} else {
				throw new Exception("Tipo database non gestito");
			}
			this.log.info("Inizializzo ISQLQueryObject terminata.");

		} catch (Exception e) {
			this.log.error("Errore durante la ricerca del SQLQueryObject...",e);
			throw new DriverUsersDBException("Errore durante la ricerca del SQLQueryObject...",e);
		}
		
	}
	
	public DriverUsersDB(DataSource dataSourceObject, String tipoDatabase) throws DriverUsersDBException {
		this(dataSourceObject, tipoDatabase, null);
	}
	public DriverUsersDB(DataSource dataSourceObject, String tipoDatabase, Logger log) throws DriverUsersDBException {
		
		// Logger
		try {
			if(log==null)
				this.log = LoggerWrapperFactory.getLogger(LOG_DRIVER_DB_USER);
			else
				this.log = log;
		} catch (Exception e) {
			throw new DriverUsersDBException("Errore durante l'inizializzazione del logger...",e);
		}

		// Datasource
		try {
			this.datasource = dataSourceObject;
			if (this.datasource == null)
				throw new Exception ("datasource is null");
		} catch (Exception e) {
			this.log.error("Errore durante la ricerca del datasource...",e);
			throw new DriverUsersDBException("Errore durante la ricerca del datasource...",e);
		}

		// ISQLQueryObject
		try {
			this.log.info("Inizializzo ISQLQueryObject...");
			if (TipiDatabase.isAMember(tipoDatabase)) {
				this.tipoDatabase = tipoDatabase;				
			} else {
				throw new Exception("Tipo database non gestito");
			}
			this.log.info("Inizializzo ISQLQueryObject terminata.");

		} catch (Exception e) {
			this.log.error("Errore durante la ricerca del SQLQueryObject...",e);
			throw new DriverUsersDBException("Errore durante la ricerca del SQLQueryObject...",e);
		}
	}
	
	public DriverUsersDB(Connection connection, String tipoDatabase) throws DriverUsersDBException {
		this(connection, tipoDatabase, null);
	}
	public DriverUsersDB(Connection connection, String tipoDatabase, Logger log) throws DriverUsersDBException {
		
		// Logger
		try {
			if(log==null)
				this.log = LoggerWrapperFactory.getLogger(LOG_DRIVER_DB_USER);
			else
				this.log = log;
		} catch (Exception e) {
			throw new DriverUsersDBException("Errore durante l'inizializzazione del logger...",e);
		}

		// connection
		this.connection = connection;
		
		// ISQLQueryObject
		try {
			this.log.info("Inizializzo ISQLQueryObject...");
			if (TipiDatabase.isAMember(tipoDatabase)) {
				this.tipoDatabase = tipoDatabase;				
			} else {
				throw new Exception("Tipo database non gestito");
			}
			this.log.info("Inizializzo ISQLQueryObject terminata.");

		} catch (Exception e) {
			this.log.error("Errore durante la ricerca del SQLQueryObject...",e);
			throw new DriverUsersDBException("Errore durante la ricerca del SQLQueryObject...",e);
		}
	}
	
	public DriverUsersDB(String urlJDBC,String driverJDBC,
			String username,String password, 
			String tipoDatabase) throws DriverUsersDBException {
		this(urlJDBC, driverJDBC, username, password, tipoDatabase, null);
	}
	public DriverUsersDB(String urlJDBC,String driverJDBC,
			String username,String password, 
			String tipoDatabase, Logger log) throws DriverUsersDBException {
		
		// Logger
		try {
			if(log==null)
				this.log = LoggerWrapperFactory.getLogger(LOG_DRIVER_DB_USER);
			else
				this.log = log;
		} catch (Exception e) {
			throw new DriverUsersDBException("Errore durante l'inizializzazione del logger...",e);
		}

		// connection
		try {
			Class.forName(driverJDBC);
			
			if(username!=null){
				this.connection = DriverManager.getConnection(urlJDBC,username,password);
			}else{
				this.connection = DriverManager.getConnection(urlJDBC);
			}
			this.connectionOpenViaJDBCInCostructor = true;
			
		} catch (Exception e) {
			this.log.error("Errore durante l'inizializzazione della connessione...",e);
			throw new DriverUsersDBException("Errore durante l'inizializzazione della connessione...",e);
		}
		
		// ISQLQueryObject
		try {
			this.log.info("Inizializzo ISQLQueryObject...");
			if (TipiDatabase.isAMember(tipoDatabase)) {
				this.tipoDatabase = tipoDatabase;				
			} else {
				throw new Exception("Tipo database non gestito");
			}
			this.log.info("Inizializzo ISQLQueryObject terminata.");

		} catch (Exception e) {
			this.log.error("Errore durante la ricerca del SQLQueryObject...",e);
			throw new DriverUsersDBException("Errore durante la ricerca del SQLQueryObject...",e);
		}
		
	}
	
	
	


	/**
	 * Restituisce l'utente identificato da <var>login</var>
	 * 
	 * @param login
	 *                Identificatore di un utente
	 * @return L'utente identificato dal parametro.
	 */
	public User getUser(String login) throws DriverUsersDBException {
		if (login == null)
			throw new DriverUsersDBException("[getUser] Parametri Non Validi");

		Connection connectionDB = null;
		try {
			// Get Connection
			if(this.connection!=null)
				connectionDB = this.connection;
			else{
				connectionDB = this.datasource.getConnection();
				if(connectionDB==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
			
			return getUser(connectionDB, login);
			
		} catch (SQLException se) {
			throw new DriverUsersDBException("[DriverUsersDB::getUser] SqlException: " + se.getMessage(),se);
		} catch (Exception ex) {
			throw new DriverUsersDBException("[DriverUsersDB::getUser] Exception: " + ex.getMessage(),ex);
		} finally {
			try{
				if(this.connection==null)
					connectionDB.close();
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public User getUser(Connection conParam, String login) throws DriverUsersDBException {
		if (login == null)
			throw new DriverUsersDBException("[getUser] Parametri Non Validi");

		User user = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.USERS);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("login = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = conParam.prepareStatement(sqlQuery);
			stm.setString(1, login);
			rs = stm.executeQuery();
			if (rs.next()) {
				user = new User();
				user.setId(Long.valueOf(rs.getInt("id")));
				user.setLogin(login);
				user.setPassword(rs.getString("password"));
				user.setLastUpdatePassword(rs.getTimestamp("data_password"));
				user.setCheckLastUpdatePassword(rs.getInt("check_data_password") == CostantiDB.TRUE);
				
				String gui =rs.getString("tipo_interfaccia");
				if(gui==null || "".equals(gui))
					user.setInterfaceType(InterfaceType.AVANZATA);
				else
					user.setInterfaceType(InterfaceType.valueOf(gui.toUpperCase()));

				int interfaceCompletePermit = rs.getInt("interfaccia_completa");
				if(CostantiDB.TRUE == interfaceCompletePermit) {
					user.setPermitInterfaceComplete(true);
				}
				else {
					user.setPermitInterfaceComplete(false);
				}
				
				String perm =rs.getString("permessi");
				user.setPermessi(PermessiUtente.toPermessiUtente(perm));
				
				String protocolli =rs.getString("protocolli");
				user.setProtocolliSupportatiFromString(protocolli);
				
				String protocollo_pddconsole =rs.getString("protocollo_pddconsole");
				user.setProtocolloSelezionatoPddConsole(protocollo_pddconsole);
				
				String protocollo_pddmonitor =rs.getString("protocollo_pddmonitor");
				user.setProtocolloSelezionatoPddMonitor(protocollo_pddmonitor);
				
				String soggetto_pddconsole =rs.getString("soggetto_pddconsole");
				user.setSoggettoSelezionatoPddConsole(soggetto_pddconsole);
				
				String soggetto_pddmonitor =rs.getString("soggetto_pddmonitor");
				user.setSoggettoSelezionatoPddMonitor(soggetto_pddmonitor);
				
				int permitAllSoggetti = rs.getInt("soggetti_all");
				if(CostantiDB.TRUE == permitAllSoggetti) {
					user.setPermitAllSoggetti(true);
				}
				else {
					user.setPermitAllSoggetti(false);
				}
				
				int permitAllServizi = rs.getInt("servizi_all");
				if(CostantiDB.TRUE == permitAllServizi) {
					user.setPermitAllServizi(true);
				}
				else {
					user.setPermitAllServizi(false);
				}
				
				// Fix: se completa, siamo per forza in modalità completa
				if(user.isPermitInterfaceComplete()) {
					user.setInterfaceType(InterfaceType.COMPLETA);
				}
				
			}
			rs.close();
			stm.close();
			
			if (user == null)
				throw new DriverUsersDBException("[DriverUsersDB::getUser] User [" + login + "] non esistente.");
			
			JDBCServiceManagerProperties jdbcProperties = new JDBCServiceManagerProperties();
			jdbcProperties.setDatabaseType(this.tipoDatabase);
			jdbcProperties.setShowSql(true);
			JDBCServiceManager search = new JDBCServiceManager(conParam, jdbcProperties, this.log);
			IDBSoggettoServiceSearch soggettiSearch = (IDBSoggettoServiceSearch) search.getSoggettoServiceSearch();
			IDBAccordoServizioParteSpecificaServiceSearch serviziSearch = (IDBAccordoServizioParteSpecificaServiceSearch) search.getAccordoServizioParteSpecificaServiceSearch();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.USERS_STATI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_utente = ?");
			sqlQueryObject.addOrderBy(CostantiDB.USERS_STATI+".oggetto");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = conParam.prepareStatement(sqlQuery);
			stm.setLong(1, user.getId());
			rs = stm.executeQuery();
			while (rs.next()) {
				String oggetto = rs.getString("oggetto");
				String stato = rs.getString("stato");
				Stato statoObject = new Stato();
				statoObject.setOggetto(oggetto);
				statoObject.setStato(stato);
				user.getStati().add(statoObject);
			}
			rs.close();
			stm.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.USERS_PASSWORD);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_utente = ?");
			sqlQueryObject.addOrderBy(CostantiDB.USERS_PASSWORD+".data_password");
			sqlQueryObject.setSortType(false);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = conParam.prepareStatement(sqlQuery);
			stm.setLong(1, user.getId());
			rs = stm.executeQuery();
			while (rs.next()) {
				UserPassword userPassword = new UserPassword();
				userPassword.setPassword(rs.getString("password"));
				userPassword.setDatePassword(rs.getTimestamp("data_password"));
				user.getPrecedentiPassword().add(userPassword);
			}
			rs.close();
			stm.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.USERS_SOGGETTI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField(CostantiDB.USERS_SOGGETTI+".id_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".nome_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".tipo_soggetto");
			sqlQueryObject.addWhereCondition("id_utente = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.USERS_SOGGETTI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI+".nome_soggetto");
			sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI+".tipo_soggetto");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = conParam.prepareStatement(sqlQuery);
			stm.setLong(1, user.getId());
			rs = stm.executeQuery();
			while (rs.next()) {
				long id = rs.getLong("id_soggetto");
				Soggetto soggetto = soggettiSearch.get(id);
				IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipoSoggetto(), soggetto.getNomeSoggetto(), soggetto.getIdentificativoPorta());
				user.getSoggetti().add(idSoggetto);
			}
			rs.close();
			stm.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.USERS_SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField(CostantiDB.USERS_SERVIZI+".id_servizio");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".tipo_servizio");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".nome_servizio");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".versione_servizio");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".tipo_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".nome_soggetto");
			sqlQueryObject.addWhereCondition("id_utente = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.USERS_SERVIZI+".id_servizio = "+CostantiDB.SERVIZI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.addOrderBy(CostantiDB.SERVIZI+".nome_servizio");
			sqlQueryObject.addOrderBy(CostantiDB.SERVIZI+".versione_servizio");
			sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI+".nome_soggetto");
			sqlQueryObject.addOrderBy(CostantiDB.SERVIZI+".tipo_servizio");
			sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI+".tipo_soggetto");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = conParam.prepareStatement(sqlQuery);
			stm.setLong(1, user.getId());
			rs = stm.executeQuery();
			while (rs.next()) {
				long id = rs.getLong("id_servizio");
				AccordoServizioParteSpecifica servizio = serviziSearch.get(id);
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(servizio.getTipo(), 
						servizio.getNome(), 
						servizio.getIdErogatore().getTipo(),
						servizio.getIdErogatore().getNome(), 
						servizio.getVersione());
				user.getServizi().add(idServizio);
			}
			rs.close();
			stm.close();


			return user;
		} catch (SQLException se) {
			throw new DriverUsersDBException("[DriverUsersDB::getUser] SqlException: " + se.getMessage(),se);
		} catch (Exception ex) {
			throw new DriverUsersDBException("[DriverUsersDB::getUser] Exception: " + ex.getMessage(),ex);
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				stm.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public User getUser(Long id) throws DriverUsersDBException {
		if (id == null || id<=0)
			throw new DriverUsersDBException("[getUser] Parametri Non Validi");

		Connection connectionDB = null;
		User user = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			// Get Connection
			if(this.connection!=null)
				connectionDB = this.connection;
			else{
				connectionDB = this.datasource.getConnection();
				if(connectionDB==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.USERS);
			sqlQueryObject.addSelectField("login");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connectionDB.prepareStatement(sqlQuery);
			stm.setLong(1, id);
			rs = stm.executeQuery();
			if (rs.next()) {
				user = this.getUser(connectionDB, rs.getString("login"));
			}
			rs.close();
			stm.close();

			if (user == null)
				throw new DriverUsersDBException("[DriverUsersDB::getUser] User [id:" + id + "] non esistente.");

			return user;
		} catch (SQLException se) {
			throw new DriverUsersDBException("[DriverUsersDB::getUser] SqlException: " + se.getMessage(),se);
		} catch (Exception ex) {
			throw new DriverUsersDBException("[DriverUsersDB::getUser] Exception: " + ex.getMessage(),ex);
		} finally {
			try {
				rs.close();
				stm.close();
			} catch (Exception e) {
				// ignore exception
			}
			try{
				if(this.connection==null)
					connectionDB.close();
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public List<User> userList(ISearch ricerca) throws DriverUsersDBException {
		return userList(ricerca,Liste.SU);
	}
	public List<User> userList(ISearch ricerca, int IDLISTA) throws DriverUsersDBException {
		
		String nomeMetodo = "userList";
		int idLista = IDLISTA;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		Connection connectionDB = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		ArrayList<User> lista = new ArrayList<User>();

		try {
			// Get Connection
			if(this.connection!=null)
				connectionDB = this.connection;
			else{
				connectionDB = this.datasource.getConnection();
				if(connectionDB==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
			
			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
				sqlQueryObject.addFromTable(CostantiDB.USERS);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereLikeCondition("login", search, true, true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
				sqlQueryObject.addFromTable(CostantiDB.USERS);
				sqlQueryObject.addSelectCountField("*", "cont");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = connectionDB.prepareStatement(queryString);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista, risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
				sqlQueryObject.addFromTable(CostantiDB.USERS);
				sqlQueryObject.addSelectField("login");
				sqlQueryObject.addWhereLikeCondition("login", search, true, true);
				sqlQueryObject.addOrderBy("login");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
				sqlQueryObject.addFromTable(CostantiDB.USERS);
				sqlQueryObject.addSelectField("login");
				sqlQueryObject.addOrderBy("login");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = connectionDB.prepareStatement(queryString);
			risultato = stmt.executeQuery();

			User newU = null;
			while (risultato.next()) {
				newU = this.getUser(connectionDB, risultato.getString("login"));
				lista.add(newU);
			}
			risultato.close();
			return lista;

		} catch (Exception qe) {
			throw new DriverUsersDBException("[DriverUsersDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try {
				if (risultato != null)
					risultato.close();
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				//ignore
			}
			try{
				if(this.connection==null)
					connectionDB.close();
			}catch(Exception eClose){
				// close
			}
		}
	}

	public boolean existsUser(String login) throws DriverUsersDBException {
		
		Connection connectionDB = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		try {
			// Get Connection
			if(this.connection!=null)
				connectionDB = this.connection;
			else{
				connectionDB = this.datasource.getConnection();
				if(connectionDB==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
						
			boolean esiste = false;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.USERS);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("login = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connectionDB.prepareStatement(sqlQuery);
			stm.setString(1, login);
			rs = stm.executeQuery();
			if (rs.next())
				esiste = true;
			rs.close();
			stm.close();

			return esiste;
		} catch (Exception qe) {
			throw new DriverUsersDBException(qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try {
				if (rs != null)
					rs.close();
				if (stm != null)
					stm.close();
			} catch (Exception e) {
				//ignore
			}
			
			try{
				if(this.connection==null)
					connectionDB.close();
			}catch(Exception eClose){
				// close
			}
		}
	}

	public List<String> getUsersByPermesso(String permesso) throws DriverUsersDBException {
		
		Connection connectionDB = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		try {
			// Get Connection
			if(this.connection!=null)
				connectionDB = this.connection;
			else{
				connectionDB = this.datasource.getConnection();
				if(connectionDB==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
			
			List<String> userWithType = new ArrayList<String>();
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.USERS);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereLikeCondition("permessi", permesso, true, true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connectionDB.prepareStatement(sqlQuery);
			rs = stm.executeQuery();
			while (rs.next())
				userWithType.add(rs.getString("login"));
			rs.close();
			stm.close();

			return userWithType;
		} catch (Exception qe) {
			throw new DriverUsersDBException(qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try {
				if (rs != null)
					rs.close();
				if (stm != null)
					stm.close();
			} catch (Exception e) {
				//ignore
			}
			
			try{
				if(this.connection==null)
					connectionDB.close();
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public List<String> getUsersByProtocolloSupportato(String protocollo) throws DriverUsersDBException {
		return getUsersByProtocolloSupportato(protocollo, false);
	}
	
	public List<String> getUsersByProtocolloSupportato(String protocollo, boolean esclusiUtentiConSoloPermessoUtente) throws DriverUsersDBException {
		
		Connection connectionDB = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		try {
			// Get Connection
			if(this.connection!=null)
				connectionDB = this.connection;
			else{
				connectionDB = this.datasource.getConnection();
				if(connectionDB==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
			
			List<String> userWithType = new ArrayList<String>();
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.USERS);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.setANDLogicOperator(true);
			
			ISQLQueryObject sqlQueryObjectPermit = sqlQueryObject.newSQLQueryObject();
			sqlQueryObjectPermit.addWhereIsNullCondition("protocolli"); // significa che li supporta tutti
			sqlQueryObjectPermit.addWhereLikeCondition("protocolli", protocollo, true, true);
			sqlQueryObjectPermit.setANDLogicOperator(false);
			sqlQueryObject.addWhereCondition(sqlQueryObjectPermit.createSQLConditions());
			if(esclusiUtentiConSoloPermessoUtente)
				sqlQueryObject.addWhereCondition("permessi <> ?");
			
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connectionDB.prepareStatement(sqlQuery);
			
			if(esclusiUtentiConSoloPermessoUtente)
				stm.setString(1, "U");
			
			rs = stm.executeQuery();
			while (rs.next())
				userWithType.add(rs.getString("login"));
			rs.close();
			stm.close();

			return userWithType;
		} catch (Exception qe) {
			throw new DriverUsersDBException(qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try {
				if (rs != null)
					rs.close();
				if (stm != null)
					stm.close();
			} catch (Exception e) {
				//ignore
			}
			
			try{
				if(this.connection==null)
					connectionDB.close();
			}catch(Exception eClose){
				// close
			}
		}
	}

	public void createUser(User user) throws DriverUsersDBException {
		
		Connection connectionDB = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (user == null)
			throw new DriverUsersDBException("[DriverUsersDB::createUser] Parametro non valido.");

		String login = user.getLogin();
		if (login == null || login.equals(""))
			throw new DriverUsersDBException("[DriverUsersDB::createUser] Parametro Login non valido.");

		try {
			// Get Connection
			if(this.connection!=null)
				connectionDB = this.connection;
			else{
				connectionDB = this.datasource.getConnection();
				if(connectionDB==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addInsertTable(CostantiDB.USERS);
			sqlQueryObject.addInsertField("login", "?");
			sqlQueryObject.addInsertField("password", "?");
			sqlQueryObject.addInsertField("data_password", "?");
			sqlQueryObject.addInsertField("check_data_password", "?");
			sqlQueryObject.addInsertField("tipo_interfaccia", "?");
			sqlQueryObject.addInsertField("interfaccia_completa", "?");
			sqlQueryObject.addInsertField("permessi", "?");
			sqlQueryObject.addInsertField("protocolli", "?");
			sqlQueryObject.addInsertField("protocollo_pddconsole", "?");
			sqlQueryObject.addInsertField("protocollo_pddmonitor", "?");
			sqlQueryObject.addInsertField("soggetto_pddconsole", "?");
			sqlQueryObject.addInsertField("soggetto_pddmonitor", "?");
			sqlQueryObject.addInsertField("soggetti_all", "?");
			sqlQueryObject.addInsertField("servizi_all", "?");
			sqlQuery = sqlQueryObject.createSQLInsert();
			stm = connectionDB.prepareStatement(sqlQuery);
			int index = 1;
			stm.setString(index++, login);
			stm.setString(index++, user.getPassword());
			Timestamp dataPassword = DateManager.getTimestamp();
			if(user.getLastUpdatePassword()!=null) {
				dataPassword = new Timestamp(user.getLastUpdatePassword().getTime());
			}
			stm.setTimestamp(index++, dataPassword);
			stm.setInt(index++, user.isCheckLastUpdatePassword()? CostantiDB.TRUE : CostantiDB.FALSE);
			stm.setString(index++, user.getInterfaceType().toString());
			stm.setInt(index++, user.isPermitInterfaceComplete()? CostantiDB.TRUE : CostantiDB.FALSE);
			stm.setString(index++, user.getPermessi().toString());
			stm.setString(index++, user.getProtocolliSupportatiAsString());
			stm.setString(index++, user.getProtocolloSelezionatoPddConsole());
			stm.setString(index++, user.getProtocolloSelezionatoPddMonitor());
			stm.setString(index++, user.getSoggettoSelezionatoPddConsole());
			stm.setString(index++, user.getSoggettoSelezionatoPddMonitor());
			stm.setInt(index++, user.isPermitAllSoggetti()? CostantiDB.TRUE : CostantiDB.FALSE);
			stm.setInt(index++, user.isPermitAllServizi()? CostantiDB.TRUE : CostantiDB.FALSE);
			stm.executeUpdate();
			stm.close();
						
			if(user.getStati().size()>0 || user.getPrecedentiPassword().size()>0 || user.getSoggetti().size()>0 || user.getServizi().size()>0) {
			
				// recupero id
				long idUser = this._getIdUser(connectionDB, login);
				
				_addListeUtente(connectionDB, user, idUser);
				
			}
			
		} catch (Exception qe) {
			throw new DriverUsersDBException(qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				//ignore
			}
			try {
				if (stm != null)
					stm.close();
			} catch (Exception e) {
				//ignore
			}
			
			try{
				if(this.connection==null) {
					connectionDB.close();
				}
			}catch(Exception eClose){
				// close
			}
		}
	}

	public void updateUser(User user) throws DriverUsersDBException {
		
		Connection connectionDB = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (user == null)
			throw new DriverUsersDBException("[DriverUsersDB::updateUser] Parametro non valido.");

		String login = user.getLogin();
		if (login == null || login.equals(""))
			throw new DriverUsersDBException("[DriverUsersDB::updateUser] Parametro Login non valido.");

		try {
			// Get Connection
			if(this.connection!=null)
				connectionDB = this.connection;
			else{
				connectionDB = this.datasource.getConnection();
				if(connectionDB==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addUpdateTable(CostantiDB.USERS);
			sqlQueryObject.addUpdateField("password", "?");
			if(user.getLastUpdatePassword()!=null) {
				sqlQueryObject.addUpdateField("data_password", "?");
			}
			sqlQueryObject.addUpdateField("check_data_password", "?");
			sqlQueryObject.addUpdateField("tipo_interfaccia", "?");
			sqlQueryObject.addUpdateField("interfaccia_completa", "?");
			sqlQueryObject.addUpdateField("permessi", "?");
			sqlQueryObject.addUpdateField("protocolli", "?");
			sqlQueryObject.addUpdateField("protocollo_pddconsole", "?");
			sqlQueryObject.addUpdateField("protocollo_pddmonitor", "?");
			sqlQueryObject.addUpdateField("soggetto_pddconsole", "?");
			sqlQueryObject.addUpdateField("soggetto_pddmonitor", "?");
			sqlQueryObject.addUpdateField("soggetti_all", "?");
			sqlQueryObject.addUpdateField("servizi_all", "?");
			sqlQueryObject.addWhereCondition("login=?");
			sqlQuery = sqlQueryObject.createSQLUpdate();
			stm = connectionDB.prepareStatement(sqlQuery);
			int index = 1;
			stm.setString(index++, user.getPassword());
			if(user.getLastUpdatePassword()!=null) {
				stm.setTimestamp(index++, new Timestamp(user.getLastUpdatePassword().getTime()));
			}
			stm.setInt(index++, user.isCheckLastUpdatePassword()? CostantiDB.TRUE : CostantiDB.FALSE);
			stm.setString(index++,user.getInterfaceType().toString());
			stm.setInt(index++, user.isPermitInterfaceComplete()? CostantiDB.TRUE : CostantiDB.FALSE);
			stm.setString(index++,user.getPermessi().toString());
			stm.setString(index++, user.getProtocolliSupportatiAsString());
			stm.setString(index++, user.getProtocolloSelezionatoPddConsole());
			stm.setString(index++, user.getProtocolloSelezionatoPddMonitor());
			stm.setString(index++, user.getSoggettoSelezionatoPddConsole());
			stm.setString(index++, user.getSoggettoSelezionatoPddMonitor());
			stm.setInt(index++, user.isPermitAllSoggetti()? CostantiDB.TRUE : CostantiDB.FALSE);
			stm.setInt(index++, user.isPermitAllServizi()? CostantiDB.TRUE : CostantiDB.FALSE);
			stm.setString(index++, user.getLogin());
			stm.executeUpdate();
			stm.close();
			
			long idUser = this._getIdUser(connectionDB, user);
				
			_deleteListeUtente(connectionDB, idUser);
			
			if(user.getStati().size()>0 || user.getPrecedentiPassword().size()>0 || user.getSoggetti().size()>0 || user.getServizi().size()>0) {
				_addListeUtente(connectionDB, user, idUser);
			}
			
		} catch (Exception qe) {
			throw new DriverUsersDBException(qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				//ignore
			}
			try {
				if (stm != null)
					stm.close();
			} catch (Exception e) {
				//ignore
			}
			
			try{
				if(this.connection==null)
					connectionDB.close();
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public void deleteUser(User user) throws DriverUsersDBException {
		
		Connection connectionDB = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (user == null)
			throw new DriverUsersDBException("[DriverUsersDB::deleteUser] Parametro non valido.");

		String login = user.getLogin();
		if (login == null || login.equals(""))
			throw new DriverUsersDBException("[DriverUsersDB::deleteUser] Parametro Login non valido.");

		try {
			// Get Connection
			if(this.connection!=null)
				connectionDB = this.connection;
			else{
				connectionDB = this.datasource.getConnection();
				if(connectionDB==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
			
			long idUser = this._getIdUser(connectionDB, user);
			
			_deleteListeUtente(connectionDB, idUser);
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addDeleteTable(CostantiDB.USERS);
			sqlQueryObject.addWhereCondition("login = ?");
			sqlQuery = sqlQueryObject.createSQLDelete();
			stm = connectionDB.prepareStatement(sqlQuery);
			stm.setString(1, login);
			stm.executeUpdate();
			stm.close();
		} catch (Exception qe) {
			throw new DriverUsersDBException(qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				//ignore
			}
			try {
				if (stm != null)
					stm.close();
			} catch (Exception e) {
				//ignore
			}
			
			try{
				if(this.connection==null)
					connectionDB.close();
			}catch(Exception eClose){
				// close
			}
		}
	}

	
	
	private void _addListeUtente(Connection connectionDB, User user, long idUser) throws Exception {
		
		PreparedStatement stm = null;
		ResultSet rs = null;
		try {
			if(user.getStati().size()>0) {
				for (Stato stato : user.getStati()) {
					
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
					sqlQueryObject.addInsertTable(CostantiDB.USERS_STATI);
					sqlQueryObject.addInsertField("id_utente", "?");
					sqlQueryObject.addInsertField("oggetto", "?");
					sqlQueryObject.addInsertField("stato", "?");
					String sqlQuery = sqlQueryObject.createSQLInsert();
					stm = connectionDB.prepareStatement(sqlQuery);
					int index = 1;
					stm.setLong(index++, idUser);
					stm.setString(index++, stato.getOggetto());
					stm.setString(index++, stato.getStato());
					stm.executeUpdate();
					stm.close();
				}
			}
			
			if(user.getPrecedentiPassword().size()>0) {
				for (UserPassword userPassword : user.getPrecedentiPassword()) {
					
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
					sqlQueryObject.addInsertTable(CostantiDB.USERS_PASSWORD);
					sqlQueryObject.addInsertField("id_utente", "?");
					sqlQueryObject.addInsertField("password", "?");
					sqlQueryObject.addInsertField("data_password", "?");
					String sqlQuery = sqlQueryObject.createSQLInsert();
					stm = connectionDB.prepareStatement(sqlQuery);
					int index = 1;
					stm.setLong(index++, idUser);
					stm.setString(index++, userPassword.getPassword());
					stm.setTimestamp(index++, new Timestamp(userPassword.getDatePassword().getTime()));
					stm.executeUpdate();
					stm.close();
				}
			}
			
			if(user.getSoggetti().size()>0) {
				for (IDSoggetto idSoggetto : user.getSoggetti()) {
					
					long idSoggettoLong = DBUtils.getIdSoggetto(idSoggetto.getNome(), idSoggetto.getTipo(), connectionDB, this.tipoDatabase);
					if(idSoggettoLong<=0) {
						throw new Exception("Impossibile recuperare id soggetto ["+idSoggetto+"]");
					}
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
					sqlQueryObject.addInsertTable(CostantiDB.USERS_SOGGETTI);
					sqlQueryObject.addInsertField("id_utente", "?");
					sqlQueryObject.addInsertField("id_soggetto", "?");
					String sqlQuery = sqlQueryObject.createSQLInsert();
					stm = connectionDB.prepareStatement(sqlQuery);
					int index = 1;
					stm.setLong(index++, idUser);
					stm.setLong(index++, idSoggettoLong);
					stm.executeUpdate();
					stm.close();
				}
			}
			
			if(user.getServizi().size()>0) {
				for (IDServizio idServizio : user.getServizi()) {
					
					long idServizioLong = DBUtils.getIdServizio(idServizio.getNome(), idServizio.getTipo(), idServizio.getVersione(),
							idServizio.getSoggettoErogatore().getNome(), idServizio.getSoggettoErogatore().getTipo(), 
							connectionDB, this.tipoDatabase);
					if(idServizioLong<=0) {
						throw new Exception("Impossibile recuperare id soggetto ["+idServizio+"]");
					}
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
					sqlQueryObject.addInsertTable(CostantiDB.USERS_SERVIZI);
					sqlQueryObject.addInsertField("id_utente", "?");
					sqlQueryObject.addInsertField("id_servizio", "?");
					String sqlQuery = sqlQueryObject.createSQLInsert();
					stm = connectionDB.prepareStatement(sqlQuery);
					int index = 1;
					stm.setLong(index++, idUser);
					stm.setLong(index++, idServizioLong);
					stm.executeUpdate();
					stm.close();
				}
			}
		} finally {

			//Chiudo statement and resultset
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				//ignore
			}
			try {
				if (stm != null)
					stm.close();
			} catch (Exception e) {
				//ignore
			}
			
		}
	}
	
	private void _deleteListeUtente(Connection connectionDB, long idUser) throws Exception {
		
		PreparedStatement stm = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addDeleteTable(CostantiDB.USERS_STATI);
			sqlQueryObject.addWhereCondition("id_utente = ?");
			String sqlQuery = sqlQueryObject.createSQLDelete();
			stm = connectionDB.prepareStatement(sqlQuery);
			stm.setLong(1, idUser);
			stm.executeUpdate();
			stm.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addDeleteTable(CostantiDB.USERS_PASSWORD);
			sqlQueryObject.addWhereCondition("id_utente = ?");
			sqlQuery = sqlQueryObject.createSQLDelete();
			stm = connectionDB.prepareStatement(sqlQuery);
			stm.setLong(1, idUser);
			stm.executeUpdate();
			stm.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addDeleteTable(CostantiDB.USERS_SOGGETTI);
			sqlQueryObject.addWhereCondition("id_utente = ?");
			sqlQuery = sqlQueryObject.createSQLDelete();
			stm = connectionDB.prepareStatement(sqlQuery);
			stm.setLong(1, idUser);
			stm.executeUpdate();
			stm.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addDeleteTable(CostantiDB.USERS_SERVIZI);
			sqlQueryObject.addWhereCondition("id_utente = ?");
			sqlQuery = sqlQueryObject.createSQLDelete();
			stm = connectionDB.prepareStatement(sqlQuery);
			stm.setLong(1, idUser);
			stm.executeUpdate();
			stm.close();
		} finally {

			//Chiudo statement and resultset
			try {
				if (stm != null)
					stm.close();
			} catch (Exception e) {
				//ignore
			}
		}
	}
	
	private long _getIdUser(Connection connectionDB, User user) throws Exception {
		
		if (user == null)
			throw new DriverUsersDBException("[DriverUsersDB::_getIdUser] Parametro non valido.");

		String login = user.getLogin();
		if (login == null || login.equals(""))
			throw new DriverUsersDBException("[DriverUsersDB::_getIdUser] Parametro Login non valido.");

		long idUser = -1;
		if(user.getId()==null || user.getId().longValue()<=0) {
			idUser = this._getIdUser(connectionDB, login);
		}
		else {
			idUser = user.getId().longValue();
		}
		
		return idUser;
			
	}
	private long _getIdUser(Connection connectionDB, String login) throws Exception {
		
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (login == null || login.equals(""))
			throw new DriverUsersDBException("[DriverUsersDB::_getIdUser] Parametro Login non valido.");

		try {
			
			long idUser = -1;
			// recupero id
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.USERS);
			sqlQueryObject.addWhereCondition("login = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connectionDB.prepareStatement(sqlQuery);
			stm.setString(1, login);
			rs = stm.executeQuery();
			if(rs.next()) {
				idUser = rs.getLong("id");
			}
			if(idUser<=0) {
				throw new Exception("Impossibile recuperare id utente con login ["+login+"]");
			}
			rs.close();
			stm.close();
			
			return idUser;
			
		} finally {

			//Chiudo statement and resultset
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				//ignore
			}
			try {
				if (stm != null)
					stm.close();
			} catch (Exception e) {
				//ignore
			}
		}
	}
	
	public List<IDServizio> utentiServiziList(String login, ISearch ricerca) throws DriverUsersDBException {
		String nomeMetodo = "utentiServiziList";
		int idLista = Liste.UTENTI_SERVIZI;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		String filterProtocollo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLO);
		String filterProtocolli = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLI);
		List<String> tipoServiziProtocollo = null;
		try {
			tipoServiziProtocollo = Filtri.convertToTipiServizi(filterProtocollo, filterProtocolli);
		}catch(Exception e) {
			throw new DriverUsersDBException(e.getMessage(),e);
		}

		String filterDominio = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_DOMINIO);
		PddTipologia pddTipologia = null;
		if(filterDominio!=null && !"".equals(filterDominio)) {
			pddTipologia = PddTipologia.toPddTipologia(filterDominio);
		}
		
		this.log.debug("search : " + search);
		this.log.debug("filterProtocollo : " + filterProtocollo);
		this.log.debug("filterProtocolli : " + filterProtocolli);
		this.log.debug("filterDominio : " + filterDominio);
		
		Connection connectionDB = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		ArrayList<IDServizio> lista = new ArrayList<IDServizio>();

		try {
			// Get Connection
			if(this.connection!=null)
				connectionDB = this.connection;
			else{
				connectionDB = this.datasource.getConnection();
				if(connectionDB==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
			
			ISQLQueryObject sqlQueryObjectSoggetti = null;
			if (!search.equals("")) {
				sqlQueryObjectSoggetti = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
				sqlQueryObjectSoggetti.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObjectSoggetti.addSelectField(CostantiDB.SOGGETTI, "tipo_soggetto");
				sqlQueryObjectSoggetti.addSelectField(CostantiDB.SOGGETTI, "nome_soggetto");
				sqlQueryObjectSoggetti.setANDLogicOperator(true);
				sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
				sqlQueryObjectSoggetti.addWhereCondition(false,
						//sqlQueryObjectSoggetti.getWhereLikeCondition("tipo_soggetto", search, true, true),
						sqlQueryObjectSoggetti.getWhereLikeCondition("nome_soggetto", search, true, true));
			}

			ISQLQueryObject sqlQueryObjectPdd = null;
			if(pddTipologia!=null && PddTipologia.ESTERNO.equals(pddTipologia)) {
				ISQLQueryObject sqlQueryObjectExistsPdd = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
				sqlQueryObjectExistsPdd.addSelectField(CostantiDB.PDD+".nome");
				sqlQueryObjectExistsPdd.addFromTable(CostantiDB.PDD);
				sqlQueryObjectExistsPdd.setANDLogicOperator(true);
				sqlQueryObjectExistsPdd.addWhereCondition(CostantiDB.PDD+".nome="+CostantiDB.SOGGETTI+".server");
				sqlQueryObjectExistsPdd.addWhereCondition(CostantiDB.PDD+".tipo=?");
				
				sqlQueryObjectPdd = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
				sqlQueryObjectPdd.setANDLogicOperator(false);
				sqlQueryObjectPdd.addWhereIsNullCondition(CostantiDB.SOGGETTI+".server");
				sqlQueryObjectPdd.addWhereExistsCondition(false, sqlQueryObjectExistsPdd);
			}
			
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.addFromTable(CostantiDB.USERS);
			sqlQueryObject.addFromTable(CostantiDB.USERS_SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addWhereCondition(CostantiDB.USERS+".login = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.USERS_SERVIZI+".id_utente = "+CostantiDB.USERS+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.USERS_SERVIZI+".id_servizio = "+CostantiDB.SERVIZI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			
			if (!search.equals("")) {
				//query con search
				sqlQueryObject.addWhereCondition(false, 
						// - ricerca su tipo/nome servizio
						//sqlQueryObject.getWhereLikeCondition("tipo_servizio", search, true, true), 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI+".nome_servizio", search, true, true),
						//sqlQueryObject.getWhereLikeCondition("versione_servizio", search, true, true),
						sqlQueryObject.getWhereExistsCondition(false, sqlQueryObjectSoggetti));
			}
			
			if(tipoServiziProtocollo!=null && tipoServiziProtocollo.size()>0) {
				sqlQueryObject.addWhereINCondition(CostantiDB.SERVIZI+".tipo_servizio", true, tipoServiziProtocollo.toArray(new String[1]));
			}
			if(pddTipologia!=null) {
				if(PddTipologia.ESTERNO.equals(pddTipologia)) {
					sqlQueryObject.addWhereCondition(sqlQueryObjectPdd.createSQLConditions());							
				}
				else {
					sqlQueryObject.addFromTable(CostantiDB.PDD);
					sqlQueryObject.addWhereCondition(true,CostantiDB.PDD+".nome="+CostantiDB.SOGGETTI+".server",CostantiDB.PDD+".tipo=?");
				}
			}
			
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = connectionDB.prepareStatement(queryString);
			int index = 1;
			stmt.setString(index++, login);
			if(pddTipologia!=null) {
				stmt.setString(index++, pddTipologia.toString());
			}
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista, risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addSelectField(CostantiDB.USERS_SERVIZI+".id_servizio");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".tipo_servizio");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".nome_servizio");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".versione_servizio");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".tipo_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".nome_soggetto");
			sqlQueryObject.addFromTable(CostantiDB.USERS);
			sqlQueryObject.addFromTable(CostantiDB.USERS_SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addWhereCondition(CostantiDB.USERS+".login = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.USERS_SERVIZI+".id_utente = "+CostantiDB.USERS+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.USERS_SERVIZI+".id_servizio = "+CostantiDB.SERVIZI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			
			if (!search.equals("")) {
				//query con search
				sqlQueryObject.addWhereCondition(false, 
						// - ricerca su tipo/nome servizio
						//sqlQueryObject.getWhereLikeCondition("tipo_servizio", search, true, true), 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI+".nome_servizio", search, true, true),
						//sqlQueryObject.getWhereLikeCondition("versione_servizio", search, true, true),
						sqlQueryObject.getWhereExistsCondition(false, sqlQueryObjectSoggetti));
			} 
			
			if(tipoServiziProtocollo!=null && tipoServiziProtocollo.size()>0) {
				sqlQueryObject.addWhereINCondition(CostantiDB.SERVIZI+".tipo_servizio", true, tipoServiziProtocollo.toArray(new String[1]));
			}
			if(pddTipologia!=null) {
				if(PddTipologia.ESTERNO.equals(pddTipologia)) {
					sqlQueryObject.addWhereCondition(sqlQueryObjectPdd.createSQLConditions());							
				}
				else {
					sqlQueryObject.addFromTable(CostantiDB.PDD);
					sqlQueryObject.addWhereCondition(true,CostantiDB.PDD+".nome="+CostantiDB.SOGGETTI+".server",CostantiDB.PDD+".tipo=?");
				}
			}
			
			sqlQueryObject.addOrderBy(CostantiDB.SERVIZI+".nome_servizio");
			sqlQueryObject.addOrderBy(CostantiDB.SERVIZI+".versione_servizio");
			sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI+".nome_soggetto");
			sqlQueryObject.addOrderBy(CostantiDB.SERVIZI+".tipo_servizio");
			sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI+".tipo_soggetto");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = connectionDB.prepareStatement(queryString);
			index = 1;
			stmt.setString(index++, login);
			if(pddTipologia!=null) {
				stmt.setString(index++, pddTipologia.toString());
			}
			risultato = stmt.executeQuery();
			
			JDBCServiceManagerProperties jdbcProperties = new JDBCServiceManagerProperties();
			jdbcProperties.setDatabaseType(this.tipoDatabase);
			jdbcProperties.setShowSql(true);
			JDBCServiceManager manager = new JDBCServiceManager(connectionDB, jdbcProperties, this.log);
//			IDBSoggettoServiceSearch soggettiSearch = (IDBSoggettoServiceSearch) manager.getSoggettoServiceSearch();
			IDBAccordoServizioParteSpecificaServiceSearch serviziSearch = (IDBAccordoServizioParteSpecificaServiceSearch) manager.getAccordoServizioParteSpecificaServiceSearch();
			
			while (risultato.next()) {
				long id = risultato.getLong("id_servizio");
				AccordoServizioParteSpecifica servizio = serviziSearch.get(id);
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(servizio.getTipo(), 
						servizio.getNome(), 
						servizio.getIdErogatore().getTipo(),
						servizio.getIdErogatore().getNome(), 
						servizio.getVersione());
				lista.add(idServizio);
			}
			risultato.close();
			return lista;

		} catch (Exception qe) {
			throw new DriverUsersDBException("[DriverUsersDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try {
				if (risultato != null)
					risultato.close();
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				//ignore
			}
			
			try{
				if(this.connection==null)
					connectionDB.close();
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	
	public List<IDSoggetto> utentiSoggettiList(String login, ISearch ricerca) throws DriverUsersDBException {
		String nomeMetodo = "utentiSoggettiList";
		int idLista = Liste.UTENTI_SOGGETTI;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		String filterProtocollo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLO);
		String filterProtocolli = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLI);
		List<String> tipoSoggettiProtocollo = null;
		try {
			tipoSoggettiProtocollo = Filtri.convertToTipiSoggetti(filterProtocollo, filterProtocolli);
		}catch(Exception e) {
			throw new DriverUsersDBException(e.getMessage(),e);
		}
		
		String filterDominio = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_DOMINIO);
		PddTipologia pddTipologia = null;
		if(filterDominio!=null && !"".equals(filterDominio)) {
			pddTipologia = PddTipologia.toPddTipologia(filterDominio);
		}
		
		this.log.debug("search : " + search);
		this.log.debug("filterProtocollo : " + filterProtocollo);
		this.log.debug("filterProtocolli : " + filterProtocolli);
		this.log.debug("filterDominio : " + filterDominio);
		
		Connection connectionDB = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		ArrayList<IDSoggetto> lista = new ArrayList<IDSoggetto>();

		try {
			
			// Get Connection
			if(this.connection!=null)
				connectionDB = this.connection;
			else{
				connectionDB = this.datasource.getConnection();
				if(connectionDB==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
			
			ISQLQueryObject sqlQueryObjectPdd = null;
			if(pddTipologia!=null && PddTipologia.ESTERNO.equals(pddTipologia)) {
				ISQLQueryObject sqlQueryObjectExistsPdd = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
				sqlQueryObjectExistsPdd.addSelectField(CostantiDB.PDD+".nome");
				sqlQueryObjectExistsPdd.addFromTable(CostantiDB.PDD);
				sqlQueryObjectExistsPdd.setANDLogicOperator(true);
				sqlQueryObjectExistsPdd.addWhereCondition(CostantiDB.PDD+".nome="+CostantiDB.SOGGETTI+".server");
				sqlQueryObjectExistsPdd.addWhereCondition(CostantiDB.PDD+".tipo=?");
				
				sqlQueryObjectPdd = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
				sqlQueryObjectPdd.setANDLogicOperator(false);
				sqlQueryObjectPdd.addWhereIsNullCondition(CostantiDB.SOGGETTI+".server");
				sqlQueryObjectPdd.addWhereExistsCondition(false, sqlQueryObjectExistsPdd);
			}
			
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.addFromTable(CostantiDB.USERS);
			sqlQueryObject.addFromTable(CostantiDB.USERS_SOGGETTI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addWhereCondition(CostantiDB.USERS+".login = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.USERS_SOGGETTI+".id_utente = "+CostantiDB.USERS+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.USERS_SOGGETTI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			
			if (!search.equals("")) {
				//query con search
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SOGGETTI+".nome_soggetto", search, true, true);
			}
			
			if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
				sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
			}
			if(pddTipologia!=null) {
				if(PddTipologia.ESTERNO.equals(pddTipologia)) {
					sqlQueryObject.addWhereCondition(sqlQueryObjectPdd.createSQLConditions());							
				}
				else {
					sqlQueryObject.addFromTable(CostantiDB.PDD);
					sqlQueryObject.addWhereCondition(true,CostantiDB.PDD+".nome="+CostantiDB.SOGGETTI+".server",CostantiDB.PDD+".tipo=?");
				}
			}
			
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = connectionDB.prepareStatement(queryString);
			int index = 1;
			stmt.setString(index++, login);
			if(pddTipologia!=null) {
				stmt.setString(index++, pddTipologia.toString());
			}
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista, risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addSelectField(CostantiDB.USERS_SOGGETTI+".id_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".nome_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".tipo_soggetto");
			sqlQueryObject.addFromTable(CostantiDB.USERS);
			sqlQueryObject.addFromTable(CostantiDB.USERS_SOGGETTI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addWhereCondition(CostantiDB.USERS+".login = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.USERS_SOGGETTI+".id_utente = "+CostantiDB.USERS+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.USERS_SOGGETTI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			
			if (!search.equals("")) { // con search
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SOGGETTI+".nome_soggetto", search, true, true);
			}  
			
			if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
				sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
			}
			if(pddTipologia!=null) {
				if(PddTipologia.ESTERNO.equals(pddTipologia)) {
					sqlQueryObject.addWhereCondition(sqlQueryObjectPdd.createSQLConditions());									
				}
				else {
					sqlQueryObject.addFromTable(CostantiDB.PDD);
					sqlQueryObject.addWhereCondition(true,CostantiDB.PDD+".nome="+CostantiDB.SOGGETTI+".server",CostantiDB.PDD+".tipo=?");
				}
			}
			
			sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI+".nome_soggetto");
			sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI+".tipo_soggetto");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = connectionDB.prepareStatement(queryString);
			index = 1;
			stmt.setString(index++, login);
			if(pddTipologia!=null) {
				stmt.setString(index++, pddTipologia.toString());
			}
			risultato = stmt.executeQuery();
			
			JDBCServiceManagerProperties jdbcProperties = new JDBCServiceManagerProperties();
			jdbcProperties.setDatabaseType(this.tipoDatabase);
			jdbcProperties.setShowSql(true);
			JDBCServiceManager manager = new JDBCServiceManager(connectionDB, jdbcProperties, this.log);
			IDBSoggettoServiceSearch soggettiSearch = (IDBSoggettoServiceSearch) manager.getSoggettoServiceSearch();
			
			while (risultato.next()) {
				long id = risultato.getLong("id_soggetto");
				Soggetto soggetto = soggettiSearch.get(id);
				IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipoSoggetto(), soggetto.getNomeSoggetto(), soggetto.getIdentificativoPorta());
				lista.add(idSoggetto);
			}
			risultato.close();
			return lista;

		} catch (Exception qe) {
			throw new DriverUsersDBException("[DriverUsersDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try {
				if (risultato != null)
					risultato.close();
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				//ignore
			}
			
			try{
				if(this.connection==null)
					connectionDB.close();
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	/**
	 * Restituisce lo stato identificato da <var>login</var> e da <var>nomeOggetto</var>
	 * 
	 * @param login Identificatore di un utente
	 * @param nomeOggetto Oggetto da ricercare
	 *               
	 * @return Lo stato individuato
	 */
	public Stato getStato(String login, String nomeOggetto) throws DriverUsersDBException {
		if (login == null || nomeOggetto == null)
			throw new DriverUsersDBException("[getStato] Parametri Non Validi");

		Connection connectionDB = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		Stato statoObject = null;
		try {
			// Get Connection
			if(this.connection!=null)
				connectionDB = this.connection;
			else{
				connectionDB = this.datasource.getConnection();
				if(connectionDB==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.USERS);
			sqlQueryObject.addFromTable(CostantiDB.USERS_STATI);
			sqlQueryObject.addSelectField(CostantiDB.USERS_STATI+".stato");
			sqlQueryObject.addSelectField(CostantiDB.USERS_STATI+".oggetto");
			sqlQueryObject.addWhereCondition(CostantiDB.USERS_STATI+".id_utente = "+CostantiDB.USERS+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.USERS +".login = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.USERS_STATI +".oggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connectionDB.prepareStatement(sqlQuery);
			stm.setString(1, login);
			stm.setString(2, nomeOggetto);
			rs = stm.executeQuery();
			while (rs.next()) {
				String oggetto = rs.getString("oggetto");
				String stato = rs.getString("stato");
				statoObject = new Stato();
				statoObject.setOggetto(oggetto);
				statoObject.setStato(stato);
			}
			rs.close();
			stm.close();

			return statoObject;
		} catch (SQLException se) {
			throw new DriverUsersDBException("[DriverUsersDB::getStato] SqlException: " + se.getMessage(),se);
		} catch (Exception ex) {
			throw new DriverUsersDBException("[DriverUsersDB::getStato] Exception: " + ex.getMessage(),ex);
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				stm.close();
			} catch (Exception e) {
				// ignore exception
			}
			try{
				if(this.connection==null)
					connectionDB.close();
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	/**
	 * Aggiorna il protocollo utilizzato dall'utente <var>login</var>
	 * 
	 * @param login Identificatore di un utente
	 * @param protocollo Protocollo
	 *               
	 */
	public void saveProtocolloUtilizzatoPddConsole(String login, String protocollo) throws DriverUsersDBException {
		if (login == null)
			throw new DriverUsersDBException("[saveProtocolloUtilizzatoPddConsole] Parametri Non Validi");

		Connection connectionDB = null;
		PreparedStatement stm = null;
		try {
			// Get Connection
			if(this.connection!=null)
				connectionDB = this.connection;
			else{
				connectionDB = this.datasource.getConnection();
				if(connectionDB==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addUpdateTable(CostantiDB.USERS);
			sqlQueryObject.addUpdateField("protocollo_pddconsole", "?");
			sqlQueryObject.addWhereCondition(CostantiDB.USERS +".login = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLUpdate();
			stm = connectionDB.prepareStatement(sqlQuery);
			stm.setString(1, protocollo);
			stm.setString(2, login);
			stm.executeUpdate();
			stm.close();

		} catch (SQLException se) {
			throw new DriverUsersDBException("[DriverUsersDB::saveProtocolloUtilizzatoPddConsole] SqlException: " + se.getMessage(),se);
		} catch (Exception ex) {
			throw new DriverUsersDBException("[DriverUsersDB::saveProtocolloUtilizzatoPddConsole] Exception: " + ex.getMessage(),ex);
		} finally {
			try {
				stm.close();
			} catch (Exception e) {
				// ignore exception
			}
			try{
				if(this.connection==null)
					connectionDB.close();
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	/**
	 * Aggiorna il protocollo utilizzato dall'utente <var>login</var>
	 * 
	 * @param login Identificatore di un utente
	 * @param protocollo Protocollo
	 *               
	 */
	public void saveProtocolloUtilizzatoPddMonitor(String login, String protocollo) throws DriverUsersDBException {
		if (login == null)
			throw new DriverUsersDBException("[saveProtocolloUtilizzatoPddMonitor] Parametri Non Validi");

		Connection connectionDB = null;
		PreparedStatement stm = null;
		try {
			// Get Connection
			if(this.connection!=null)
				connectionDB = this.connection;
			else{
				connectionDB = this.datasource.getConnection();
				if(connectionDB==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addUpdateTable(CostantiDB.USERS);
			sqlQueryObject.addUpdateField("protocollo_pddmonitor", "?");
			sqlQueryObject.addWhereCondition(CostantiDB.USERS +".login = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLUpdate();
			stm = connectionDB.prepareStatement(sqlQuery);
			stm.setString(1, protocollo);
			stm.setString(2, login);
			stm.executeUpdate();
			stm.close();

		} catch (SQLException se) {
			throw new DriverUsersDBException("[DriverUsersDB::saveProtocolloUtilizzatoPddMonitor] SqlException: " + se.getMessage(),se);
		} catch (Exception ex) {
			throw new DriverUsersDBException("[DriverUsersDB::saveProtocolloUtilizzatoPddMonitor] Exception: " + ex.getMessage(),ex);
		} finally {
			try {
				stm.close();
			} catch (Exception e) {
				// ignore exception
			}
			try{
				if(this.connection==null)
					connectionDB.close();
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	/**
	 * Aggiorna il soggetto utilizzato dall'utente <var>login</var>
	 * 
	 * @param login Identificatore di un utente
	 * @param soggetto Soggetto
	 *               
	 */
	public void saveSoggettoUtilizzatoPddConsole(String login, String soggetto) throws DriverUsersDBException {
		if (login == null)
			throw new DriverUsersDBException("[saveSoggettoUtilizzatoPddConsole] Parametri Non Validi");

		Connection connectionDB = null;
		try {
			// Get Connection
			if(this.connection!=null)
				connectionDB = this.connection;
			else{
				connectionDB = this.datasource.getConnection();
				if(connectionDB==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
			
			saveSoggettoUtilizzatoPddConsole(connectionDB, login, soggetto);

		} catch (SQLException se) {
			throw new DriverUsersDBException("[DriverUsersDB::saveSoggettoUtilizzatoPddConsole] SqlException: " + se.getMessage(),se);
		} catch (Exception ex) {
			throw new DriverUsersDBException("[DriverUsersDB::saveSoggettoUtilizzatoPddConsole] Exception: " + ex.getMessage(),ex);
		} finally {
			try{
				if(this.connection==null)
					connectionDB.close();
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public void saveSoggettoUtilizzatoPddConsole(Connection conParam, String login, String soggetto) throws DriverUsersDBException {
		if (login == null)
			throw new DriverUsersDBException("[saveSoggettoUtilizzatoPddConsole] Parametri Non Validi");

		PreparedStatement stm = null;
		try {			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addUpdateTable(CostantiDB.USERS);
			sqlQueryObject.addUpdateField("soggetto_pddconsole", "?");
			sqlQueryObject.addWhereCondition(CostantiDB.USERS +".login = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLUpdate();
			stm = conParam.prepareStatement(sqlQuery);
			stm.setString(1, soggetto);
			stm.setString(2, login);
			stm.executeUpdate();
			stm.close();

		} catch (SQLException se) {
			throw new DriverUsersDBException("[DriverUsersDB::saveSoggettoUtilizzatoPddConsole] SqlException: " + se.getMessage(),se);
		} catch (Exception ex) {
			throw new DriverUsersDBException("[DriverUsersDB::saveSoggettoUtilizzatoPddConsole] Exception: " + ex.getMessage(),ex);
		} finally {
			try {
				stm.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	/**
	 * Aggiorna il soggetto utilizzato dall'utente <var>login</var>
	 * 
	 * @param login Identificatore di un utente
	 * @param soggetto Soggetto
	 *               
	 */
	public void saveSoggettoUtilizzatoPddMonitor(String login, String soggetto) throws DriverUsersDBException {
		if (login == null)
			throw new DriverUsersDBException("[saveSoggettoUtilizzatoPddMonitor] Parametri Non Validi");

		Connection connectionDB = null;
		try {
			// Get Connection
			if(this.connection!=null)
				connectionDB = this.connection;
			else{
				connectionDB = this.datasource.getConnection();
				if(connectionDB==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
			
			saveSoggettoUtilizzatoPddMonitor(connectionDB, login, soggetto);

		} catch (SQLException se) {
			throw new DriverUsersDBException("[DriverUsersDB::saveSoggettoUtilizzatoPddMonitor] SqlException: " + se.getMessage(),se);
		} catch (Exception ex) {
			throw new DriverUsersDBException("[DriverUsersDB::saveSoggettoUtilizzatoPddMonitor] Exception: " + ex.getMessage(),ex);
		} finally {
			try{
				if(this.connection==null)
					connectionDB.close();
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public void saveSoggettoUtilizzatoPddMonitor(Connection conParam, String login, String soggetto) throws DriverUsersDBException {
		if (login == null)
			throw new DriverUsersDBException("[saveSoggettoUtilizzatoPddMonitor] Parametri Non Validi");

		PreparedStatement stm = null;
		try {			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addUpdateTable(CostantiDB.USERS);
			sqlQueryObject.addUpdateField("soggetto_pddmonitor", "?");
			sqlQueryObject.addWhereCondition(CostantiDB.USERS +".login = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLUpdate();
			stm = conParam.prepareStatement(sqlQuery);
			stm.setString(1, soggetto);
			stm.setString(2, login);
			stm.executeUpdate();
			stm.close();

		} catch (SQLException se) {
			throw new DriverUsersDBException("[DriverUsersDB::saveSoggettoUtilizzatoPddMonitor] SqlException: " + se.getMessage(),se);
		} catch (Exception ex) {
			throw new DriverUsersDBException("[DriverUsersDB::saveSoggettoUtilizzatoPddMonitor] Exception: " + ex.getMessage(),ex);
		} finally {
			try {
				stm.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	/**
	 * Aggiorna il soggetto utilizzato dall'utente <var>login</var>
	 * 
	 * @param oldSoggetto Identita precedentemente associata
	 * @param newSoggetto Nuova identita associata
	 *               
	 */
	public void modificaSoggettoUtilizzatoConsole(String oldSoggetto, String newSoggetto) throws DriverUsersDBException {
		if (oldSoggetto == null) // || newSoggetto==null)
			throw new DriverUsersDBException("[modificaSoggettoUtilizzatoConsole] Parametri Non Validi");

		Connection connectionDB = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		try {
			// Get Connection
			if(this.connection!=null)
				connectionDB = this.connection;
			else{
				connectionDB = this.datasource.getConnection();
				if(connectionDB==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
			
			List<String> users = new ArrayList<>();
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.USERS);
			sqlQueryObject.addWhereCondition("soggetto_pddconsole=?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connectionDB.prepareStatement(sqlQuery);
			stm.setString(1, oldSoggetto);
			rs = stm.executeQuery();
			while(rs.next()) {
				String login = rs.getString("login");
				users.add(login);
			}
			rs.close();
			stm.close();
			if(!users.isEmpty()) {
				for (String user : users) {
					this.saveSoggettoUtilizzatoPddConsole(connectionDB, user, newSoggetto);
				}
			}
			
			users = new ArrayList<>();
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.USERS);
			sqlQueryObject.addWhereCondition("soggetto_pddmonitor=?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connectionDB.prepareStatement(sqlQuery);
			stm.setString(1, oldSoggetto);
			rs = stm.executeQuery();
			while(rs.next()) {
				String login = rs.getString("login");
				users.add(login);
			}
			rs.close();
			stm.close();
			if(!users.isEmpty()) {
				for (String user : users) {
					this.saveSoggettoUtilizzatoPddMonitor(connectionDB, user, newSoggetto);
				}
			}


		} catch (SQLException se) {
			throw new DriverUsersDBException("[DriverUsersDB::saveSoggettoUtilizzatoPddMonitor] SqlException: " + se.getMessage(),se);
		} catch (Exception ex) {
			throw new DriverUsersDBException("[DriverUsersDB::saveSoggettoUtilizzatoPddMonitor] Exception: " + ex.getMessage(),ex);
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				stm.close();
			} catch (Exception e) {
				// ignore exception
			}
			try{
				if(this.connection==null)
					connectionDB.close();
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	/**
	 * Aggiorna lo stato identificato da <var>login</var> e da <var>nomeOggetto</var> 
	 * 
	 * @param login Identificatore di un utente
	 * @param nomeOggetto Oggetto da ricercare
	 * @param statoOggetto Stato da salvare
	 *               
	 */
	public void saveStato(String login, String nomeOggetto, String statoOggetto) throws DriverUsersDBException {
		if (login == null || nomeOggetto == null)
			throw new DriverUsersDBException("[saveStato] Parametri Non Validi");

		Connection connectionDB = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		try {
			// Get Connection
			if(this.connection!=null)
				connectionDB = this.connection;
			else{
				connectionDB = this.datasource.getConnection();
				if(connectionDB==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addFromTable(CostantiDB.USERS);
			sqlQueryObject.addWhereCondition(CostantiDB.USERS +".login = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connectionDB.prepareStatement(sqlQuery);
			stm.setString(1, login);
			rs = stm.executeQuery();
			
			Long idUtente = null; 
			while (rs.next()) {
				idUtente = rs.getLong("id");
			}
			rs.close();
			stm.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addUpdateTable(CostantiDB.USERS_STATI);
			sqlQueryObject.addUpdateField("stato","?");
			sqlQueryObject.addWhereCondition(CostantiDB.USERS_STATI+".id_utente = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.USERS_STATI +".oggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLUpdate();
			stm = connectionDB.prepareStatement(sqlQuery);
			stm.setString(1, statoOggetto);
			stm.setLong(2, idUtente);
			stm.setString(3, nomeOggetto);
			int update = stm.executeUpdate();
			stm.close();
			
			// nuovo stato
			if(update == 0) {
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
				sqlQueryObject.addInsertTable(CostantiDB.USERS_STATI);
				sqlQueryObject.addInsertField("stato","?");
				sqlQueryObject.addInsertField("id_utente","?");
				sqlQueryObject.addInsertField("oggetto","?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = connectionDB.prepareStatement(sqlQuery);
				stm.setString(1, statoOggetto);
				stm.setLong(2, idUtente);
				stm.setString(3, nomeOggetto);
				update = stm.executeUpdate();
				stm.close();
			}

		} catch (SQLException se) {
			throw new DriverUsersDBException("[DriverUsersDB::saveStato] SqlException: " + se.getMessage(),se);
		} catch (Exception ex) {
			throw new DriverUsersDBException("[DriverUsersDB::saveStato] Exception: " + ex.getMessage(),ex);
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				stm.close();
			} catch (Exception e) {
				// ignore exception
			}
			try{
				if(this.connection==null)
					connectionDB.close();
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	
	public void savePassword(Long idUser, String login, String newPassword, Date dataAggiornamentoPassword) throws DriverUsersDBException {
		this.savePassword(idUser, login, newPassword, dataAggiornamentoPassword, null);
	}
	
	public void savePassword(Long idUser, String login, String password, Date dataAggiornamentoPassword, List<UserPassword> storicoPassword) throws DriverUsersDBException {
		if (login == null || password==null)
			throw new DriverUsersDBException("[savePassword] Parametri Non Validi");

		Connection connectionDB = null;
		PreparedStatement stm = null;
		try {
			// Get Connection
			if(this.connection!=null)
				connectionDB = this.connection;
			else{
				connectionDB = this.datasource.getConnection();
				if(connectionDB==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addUpdateTable(CostantiDB.USERS);
			sqlQueryObject.addUpdateField("password", "?");
			if(dataAggiornamentoPassword!=null) {
				sqlQueryObject.addUpdateField("data_password", "?");
			}
			sqlQueryObject.addWhereCondition(CostantiDB.USERS +".login = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLUpdate();
			stm = connectionDB.prepareStatement(sqlQuery);
			int index = 1;
			stm.setString(index++, password);
			if(dataAggiornamentoPassword!=null) {
				stm.setTimestamp(index++, new Timestamp(dataAggiornamentoPassword.getTime()));
			}
			stm.setString(index++, login);
			stm.executeUpdate();
			stm.close();
			
			// salvataggio dello storico password
			if(storicoPassword != null) {
				try {
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
					sqlQueryObject.addDeleteTable(CostantiDB.USERS_PASSWORD);
					sqlQueryObject.addWhereCondition("id_utente = ?");
					sqlQuery = sqlQueryObject.createSQLDelete();
					stm = connectionDB.prepareStatement(sqlQuery);
					stm.setLong(1, idUser);
					stm.executeUpdate();
					stm.close();
				} finally {

					//Chiudo statement and resultset
					try {
						if (stm != null)
							stm.close();
					} catch (Exception e) {
						//ignore
					}
				}
				
				if(storicoPassword.size()>0) {
					stm = null;
					ResultSet rs = null;
					try {
						for (UserPassword userPassword : storicoPassword) {
							
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
							sqlQueryObject.addInsertTable(CostantiDB.USERS_PASSWORD);
							sqlQueryObject.addInsertField("id_utente", "?");
							sqlQueryObject.addInsertField("password", "?");
							sqlQueryObject.addInsertField("data_password", "?");
							sqlQuery = sqlQueryObject.createSQLInsert();
							stm = connectionDB.prepareStatement(sqlQuery);
							index = 1;
							stm.setLong(index++, idUser);
							stm.setString(index++, userPassword.getPassword());
							stm.setTimestamp(index++, new Timestamp(userPassword.getDatePassword().getTime()));
							stm.executeUpdate();
							stm.close();
						}
					} finally {

						//Chiudo statement and resultset
						try {
							if (rs != null)
								rs.close();
						} catch (Exception e) {
							//ignore
						}
						try {
							if (stm != null)
								stm.close();
						} catch (Exception e) {
							//ignore
						}
						
					}
				}
			}

		} catch (SQLException se) {
			throw new DriverUsersDBException("[DriverUsersDB::savePassword] SqlException: " + se.getMessage(),se);
		} catch (Exception ex) {
			throw new DriverUsersDBException("[DriverUsersDB::savePassword] Exception: " + ex.getMessage(),ex);
		} finally {
			try {
				stm.close();
			} catch (Exception e) {
				// ignore exception
			}
			try{
				if(this.connection==null)
					connectionDB.close();
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public UserObjects countUserServizi(String user) throws DriverUsersDBException {
		return countUser(user, false);
	}
	public UserObjects countUserCooperazione(String user) throws DriverUsersDBException {
		return countUser(user, true);
	}
	public UserObjects countUser(String user, boolean cooperazione) throws DriverUsersDBException {
		if (user==null)
			throw new DriverUsersDBException("[countUser] Parametri Non Validi");

		Connection connectionDB = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		try {
			// Get Connection
			if(this.connection!=null)
				connectionDB = this.connection;
			else{
				connectionDB = this.datasource.getConnection();
				if(connectionDB==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
			
			List<String> tables = new ArrayList<String>();
			if(cooperazione) {
				tables.add(CostantiDB.ACCORDI_COOPERAZIONE);
				tables.add(CostantiDB.ACCORDI);
			}
			else {
				tables.add(CostantiDB.PDD);
				tables.add(CostantiDB.GRUPPI);
				tables.add(CostantiDB.RUOLI);
				tables.add(CostantiDB.SCOPE);
				tables.add(CostantiDB.SOGGETTI);
				tables.add(CostantiDB.ACCORDI);
				tables.add(CostantiDB.SERVIZI);
			}
		
			UserObjects results = new UserObjects();
			
			for (String table : tables) {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
				sqlQueryObject.addFromTable(table);
				sqlQueryObject.addSelectCountField("id", "somma");
				sqlQueryObject.addWhereCondition("superuser = ?");
				if(CostantiDB.ACCORDI.equals(table)) {
					
					ISQLQueryObject sqlQueryObjectExclude = null;
					sqlQueryObjectExclude = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
					sqlQueryObjectExclude.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
					sqlQueryObjectExclude.addSelectField(CostantiDB.ACCORDI_SERVIZI_COMPOSTO, "id_accordo");
					sqlQueryObjectExclude.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo="+CostantiDB.ACCORDI+".id");
					if(cooperazione){
						sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectExclude);
					}
					else{
						sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectExclude);
					}
					
				}
				sqlQueryObject.setANDLogicOperator(true);
				String sqlQuery = sqlQueryObject.createSQLQuery();
				stm = connectionDB.prepareStatement(sqlQuery);
				int index = 1;
				stm.setString(index++, user);
				rs = stm.executeQuery();
				int result = rs.getInt("somma");
				rs.close();
				stm.close();
				
				if(CostantiDB.PDD.equals(table)) {
					results.pdd = result;
				}
				else if(CostantiDB.GRUPPI.equals(table)) {
					results.gruppi = result;
				}
				else if(CostantiDB.RUOLI.equals(table)) {
					results.ruoli = result;
				}
				else if(CostantiDB.SCOPE.equals(table)) {
					results.scope = result;
				}
				else if(CostantiDB.SOGGETTI.equals(table)) {
					results.soggetti = result;
				}
				else if(CostantiDB.ACCORDI.equals(table)) {
					results.accordi_parte_comune = result;
				}
				else if(CostantiDB.ACCORDI_COOPERAZIONE.equals(table)) {
					results.accordi_accoperazione = result;
				}
				else if(CostantiDB.SERVIZI.equals(table)) {
					results.accordi_parte_specifica = result;
				}
			}
			
			return results;
			
		} catch (SQLException se) {
			throw new DriverUsersDBException("[DriverUsersDB::countUser] SqlException: " + se.getMessage(),se);
		} catch (Exception ex) {
			throw new DriverUsersDBException("[DriverUsersDB::countUser] Exception: " + ex.getMessage(),ex);
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				stm.close();
			} catch (Exception e) {
				// ignore exception
			}
			try{
				if(this.connection==null)
					connectionDB.close();
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public UserObjects updateUserServizi(String oldUser, String newUser) throws DriverUsersDBException {
		return updateUser(oldUser, newUser, false);
	}
	public UserObjects updateUserCooperazione(String oldUser, String newUser) throws DriverUsersDBException {
		return updateUser(oldUser, newUser, true);
	}
	public UserObjects updateUser(String oldUser, String newUser, boolean cooperazione) throws DriverUsersDBException {
		if (oldUser == null || newUser==null)
			throw new DriverUsersDBException("[updateUser] Parametri Non Validi");

		Connection connectionDB = null;
		PreparedStatement stm = null;
		try {
			// Get Connection
			if(this.connection!=null)
				connectionDB = this.connection;
			else{
				connectionDB = this.datasource.getConnection();
				if(connectionDB==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
			
			List<String> tables = new ArrayList<String>();
			if(cooperazione) {
				tables.add(CostantiDB.ACCORDI_COOPERAZIONE);
				tables.add(CostantiDB.ACCORDI);
			}
			else {
				tables.add(CostantiDB.PDD);
				tables.add(CostantiDB.GRUPPI);
				tables.add(CostantiDB.RUOLI);
				tables.add(CostantiDB.SCOPE);
				tables.add(CostantiDB.SOGGETTI);
				tables.add(CostantiDB.ACCORDI);
				tables.add(CostantiDB.SERVIZI);
			}
		
			UserObjects results = new UserObjects();
			
			for (String table : tables) {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
				sqlQueryObject.addUpdateTable(table);
				sqlQueryObject.addUpdateField("superuser", "?");
				sqlQueryObject.addWhereCondition("superuser = ?");
				if(CostantiDB.ACCORDI.equals(table)) {
					
					ISQLQueryObject sqlQueryObjectExclude = null;
					sqlQueryObjectExclude = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
					sqlQueryObjectExclude.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
					sqlQueryObjectExclude.addSelectField(CostantiDB.ACCORDI_SERVIZI_COMPOSTO, "id_accordo");
					sqlQueryObjectExclude.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo="+CostantiDB.ACCORDI+".id");
					if(cooperazione){
						sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectExclude);
					}
					else{
						sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectExclude);
					}
					
				}
				sqlQueryObject.setANDLogicOperator(true);
				String sqlQuery = sqlQueryObject.createSQLUpdate();
				stm = connectionDB.prepareStatement(sqlQuery);
				int index = 1;
				stm.setString(index++, newUser);
				stm.setString(index++, oldUser);
				int result = stm.executeUpdate();
				stm.close();
				
				if(CostantiDB.PDD.equals(table)) {
					results.pdd = result;
				}
				else if(CostantiDB.GRUPPI.equals(table)) {
					results.gruppi = result;
				}
				else if(CostantiDB.RUOLI.equals(table)) {
					results.ruoli = result;
				}
				else if(CostantiDB.SCOPE.equals(table)) {
					results.scope = result;
				}
				else if(CostantiDB.SOGGETTI.equals(table)) {
					results.soggetti = result;
				}
				else if(CostantiDB.ACCORDI.equals(table)) {
					results.accordi_parte_comune = result;
				}
				else if(CostantiDB.ACCORDI_COOPERAZIONE.equals(table)) {
					results.accordi_accoperazione = result;
				}
				else if(CostantiDB.SERVIZI.equals(table)) {
					results.accordi_parte_specifica = result;
				}
			}
			
			return results;
			
		} catch (SQLException se) {
			throw new DriverUsersDBException("[DriverUsersDB::updateUser] SqlException: " + se.getMessage(),se);
		} catch (Exception ex) {
			throw new DriverUsersDBException("[DriverUsersDB::updateUser] Exception: " + ex.getMessage(),ex);
		} finally {
			try {
				stm.close();
			} catch (Exception e) {
				// ignore exception
			}
			try{
				if(this.connection==null)
					connectionDB.close();
			}catch(Exception eClose){
				// close
			}
		}
	}
}
