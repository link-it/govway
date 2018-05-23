/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
import java.util.ArrayList;
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
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.web.lib.users.dao.InterfaceType;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;
import org.openspcoop2.web.lib.users.dao.Stato;
import org.openspcoop2.web.lib.users.dao.User;
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
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("login = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connectionDB.prepareStatement(sqlQuery);
			stm.setString(1, login);
			rs = stm.executeQuery();
			if (rs.next()) {
				user = new User();
				user.setId(Long.valueOf(rs.getInt("id")));
				user.setLogin(login);
				user.setPassword(rs.getString("password"));
				
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
				
				String protocollo =rs.getString("protocollo");
				user.setProtocolloSelezionato(protocollo);
				
				int multiTenant = rs.getInt("multi_tenant");
				if(CostantiDB.TRUE == multiTenant) {
					user.setPermitMultiTenant(true);
				}
				else {
					user.setPermitMultiTenant(false);
				}
				
			}
			rs.close();
			stm.close();
			
			if (user == null)
				throw new DriverUsersDBException("[DriverUsersDB::getUser] User [" + login + "] non esistente.");
			
			JDBCServiceManagerProperties jdbcProperties = new JDBCServiceManagerProperties();
			jdbcProperties.setDatabaseType(this.tipoDatabase);
			jdbcProperties.setShowSql(true);
			JDBCServiceManager search = new JDBCServiceManager(connectionDB, jdbcProperties, this.log);
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
			stm = connectionDB.prepareStatement(sqlQuery);
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
			stm = connectionDB.prepareStatement(sqlQuery);
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
			stm = connectionDB.prepareStatement(sqlQuery);
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
			try{
				if(this.connection==null)
					connectionDB.close();
			}catch(Exception eClose){}
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
				user = this.getUser(rs.getString("login"));
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
			}catch(Exception eClose){}
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
				newU = this.getUser(risultato.getString("login"));
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
			}catch(Exception eClose){}
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
			}catch(Exception eClose){}
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
			}catch(Exception eClose){}
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
			}catch(Exception eClose){}
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
			sqlQueryObject.addInsertField("tipo_interfaccia", "?");
			sqlQueryObject.addInsertField("interfaccia_completa", "?");
			sqlQueryObject.addInsertField("permessi", "?");
			sqlQueryObject.addInsertField("protocolli", "?");
			sqlQueryObject.addInsertField("protocollo", "?");
			sqlQueryObject.addInsertField("multi_tenant", "?");
			sqlQuery = sqlQueryObject.createSQLInsert();
			stm = connectionDB.prepareStatement(sqlQuery);
			int index = 1;
			stm.setString(index++, login);
			stm.setString(index++, user.getPassword());
			stm.setString(index++, user.getInterfaceType().toString());
			stm.setInt(index++, user.isPermitInterfaceComplete()? CostantiDB.TRUE : CostantiDB.FALSE);
			stm.setString(index++, user.getPermessi().toString());
			stm.setString(index++, user.getProtocolliSupportatiAsString());
			stm.setString(index++, user.getProtocolloSelezionato());
			stm.setInt(index++, user.isPermitMultiTenant()? CostantiDB.TRUE : CostantiDB.FALSE);
			stm.executeUpdate();
			stm.close();
						
			if(user.getStati().size()>0 || user.getSoggetti().size()>0 || user.getServizi().size()>0) {
			
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
			}catch(Exception eClose){}
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
			sqlQueryObject.addUpdateField("tipo_interfaccia", "?");
			sqlQueryObject.addUpdateField("interfaccia_completa", "?");
			sqlQueryObject.addUpdateField("permessi", "?");
			sqlQueryObject.addUpdateField("protocolli", "?");
			sqlQueryObject.addUpdateField("protocollo", "?");
			sqlQueryObject.addUpdateField("multi_tenant", "?");
			sqlQueryObject.addWhereCondition("login=?");
			sqlQuery = sqlQueryObject.createSQLUpdate();
			stm = connectionDB.prepareStatement(sqlQuery);
			int index = 1;
			stm.setString(index++, user.getPassword());
			stm.setString(index++,user.getInterfaceType().toString());
			stm.setInt(index++, user.isPermitInterfaceComplete()? CostantiDB.TRUE : CostantiDB.FALSE);
			stm.setString(index++,user.getPermessi().toString());
			stm.setString(index++, user.getProtocolliSupportatiAsString());
			stm.setString(index++, user.getProtocolloSelezionato());
			stm.setInt(index++, user.isPermitMultiTenant()? CostantiDB.TRUE : CostantiDB.FALSE);
			stm.setString(index++, user.getLogin());
			stm.executeUpdate();
			stm.close();
			
			long idUser = this._getIdUser(connectionDB, user);
				
			_deleteListeUtente(connectionDB, idUser);
			
			if(user.getStati().size()>0 || user.getSoggetti().size()>0 || user.getServizi().size()>0) {
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
			}catch(Exception eClose){}
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
			}catch(Exception eClose){}
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
			}catch(Exception eClose){}
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
			}catch(Exception eClose){}
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
			}catch(Exception eClose){}
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
			sqlQueryObject.addUpdateTable(CostantiDB.USERS_STATI);
			sqlQueryObject.addUpdateField(CostantiDB.USERS_STATI+".stato",statoOggetto);
			sqlQueryObject.addWhereCondition(CostantiDB.USERS_STATI+".id_utente = "+CostantiDB.USERS+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.USERS +".login = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.USERS_STATI +".oggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connectionDB.prepareStatement(sqlQuery);
			stm.setString(1, login);
			stm.setString(2, nomeOggetto);
			stm.executeUpdate();
			stm.close();

		} catch (SQLException se) {
			throw new DriverUsersDBException("[DriverUsersDB::saveStato] SqlException: " + se.getMessage(),se);
		} catch (Exception ex) {
			throw new DriverUsersDBException("[DriverUsersDB::saveStato] Exception: " + ex.getMessage(),ex);
		} finally {
			try {
				stm.close();
			} catch (Exception e) {
				// ignore exception
			}
			try{
				if(this.connection==null)
					connectionDB.close();
			}catch(Exception eClose){}
		}
	}
}
