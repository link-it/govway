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


package org.openspcoop2.web.lib.users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import org.openspcoop2.web.lib.users.dao.InterfaceType;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;
import org.openspcoop2.web.lib.users.dao.User;

import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

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

	/** Connessione al Database */
	private Connection connectionDB;

	// Tipo database passato al momento della creazione dell'oggetto
	private String tipoDB = null;

	public DriverUsersDB(Connection con, String tipoDB) throws DriverUsersDBException {
		this.connectionDB = con;
		if (con == null)
			throw new DriverUsersDBException("Connessione al Database non definita");

		this.tipoDB = tipoDB;
		if (tipoDB == null)
			throw new DriverUsersDBException("Il tipoDatabase non puo essere null.");
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

		User user = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.USERS);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("login = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			stm.setString(1, login);
			rs = stm.executeQuery();
			if (rs.next()) {
				user = new User();
				user.setId(new Long(rs.getInt("id")));
				user.setLogin(login);
				user.setPassword(rs.getString("password"));
				
				String gui =rs.getString("tipo_interfaccia");
				if(gui==null || "".equals(gui))
					user.setInterfaceType(InterfaceType.AVANZATA);
				else
					user.setInterfaceType(InterfaceType.valueOf(gui.toUpperCase()));

				String perm =rs.getString("permessi");
				user.setPermessi(PermessiUtente.toPermessiUtente(perm));
			}
			rs.close();
			stm.close();

			if (user == null)
				throw new DriverUsersDBException("[DriverUsersDB::getUser] User [" + login + "] non esistente.");

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
		}
	}

	public User getUser(Long id) throws DriverUsersDBException {
		if (id == null || id<=0)
			throw new DriverUsersDBException("[getUser] Parametri Non Validi");

		User user = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.USERS);
			sqlQueryObject.addSelectField("login");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = this.connectionDB.prepareStatement(sqlQuery);
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

		PreparedStatement stmt = null;
		ResultSet risultato = null;
		ArrayList<User> lista = new ArrayList<User>();

		try {
			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.USERS);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereLikeCondition("login", search, true, true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.USERS);
				sqlQueryObject.addSelectCountField("*", "cont");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = this.connectionDB.prepareStatement(queryString);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista, risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.USERS);
				sqlQueryObject.addSelectField("login");
				sqlQueryObject.addSelectField("password");
				sqlQueryObject.addSelectField("tipo_interfaccia");
				sqlQueryObject.addSelectField("permessi");
				sqlQueryObject.addWhereLikeCondition("login", search, true, true);
				sqlQueryObject.addOrderBy("login");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.USERS);
				sqlQueryObject.addSelectField("login");
				sqlQueryObject.addSelectField("password");
				sqlQueryObject.addSelectField("tipo_interfaccia");
				sqlQueryObject.addSelectField("permessi");
				sqlQueryObject.addOrderBy("login");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = this.connectionDB.prepareStatement(queryString);
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
		}
	}

	public boolean existsUser(String login) throws DriverUsersDBException {
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		try {
			boolean esiste = false;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.USERS);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("login = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = this.connectionDB.prepareStatement(sqlQuery);
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
		}
	}

	public List<String> getUsersWithType(String permesso) throws DriverUsersDBException {
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		try {
			List<String> userWithType = new ArrayList<String>();
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.USERS);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereLikeCondition("permessi", permesso, true, true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = this.connectionDB.prepareStatement(sqlQuery);
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
		}
	}

	public void createUser(User user) throws DriverUsersDBException {
		PreparedStatement stm = null;
		String sqlQuery = "";

		if (user == null)
			throw new DriverUsersDBException("[DriverUsersDB::createUser] Parametro non valido.");

		String login = user.getLogin();
		if (login == null || login.equals(""))
			throw new DriverUsersDBException("[DriverUsersDB::createUser] Parametro Login non valido.");

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addInsertTable(CostantiDB.USERS);
			sqlQueryObject.addInsertField("login", "?");
			sqlQueryObject.addInsertField("password", "?");
			sqlQueryObject.addInsertField("tipo_interfaccia", "?");
			sqlQueryObject.addInsertField("permessi", "?");
			sqlQuery = sqlQueryObject.createSQLInsert();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			stm.setString(1, login);
			stm.setString(2, user.getPassword());
			stm.setString(3, user.getInterfaceType().toString());
			stm.setString(4, user.getPermessi().toString());
			stm.executeUpdate();
			stm.close();
		} catch (Exception qe) {
			throw new DriverUsersDBException(qe.getMessage(),qe);
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

	public void updateUser(User user) throws DriverUsersDBException {
		PreparedStatement stm = null;
		String sqlQuery = "";

		if (user == null)
			throw new DriverUsersDBException("[DriverUsersDB::updateUser] Parametro non valido.");

		String login = user.getLogin();
		if (login == null || login.equals(""))
			throw new DriverUsersDBException("[DriverUsersDB::updateUser] Parametro Login non valido.");

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addUpdateTable(CostantiDB.USERS);
			sqlQueryObject.addUpdateField("password", "?");
			sqlQueryObject.addUpdateField("tipo_interfaccia", "?");
			sqlQueryObject.addUpdateField("permessi", "?");
			sqlQueryObject.addWhereCondition("login=?");
			sqlQuery = sqlQueryObject.createSQLUpdate();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			stm.setString(1, user.getPassword());
			stm.setString(2,user.getInterfaceType().toString());
			stm.setString(3,user.getPermessi().toString());
			stm.setString(4, user.getLogin());
			stm.executeUpdate();
			stm.close();
		} catch (Exception qe) {
			throw new DriverUsersDBException(qe.getMessage(),qe);
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
	
	public void deleteUser(User user) throws DriverUsersDBException {
		PreparedStatement stm = null;
		String sqlQuery = "";

		if (user == null)
			throw new DriverUsersDBException("[DriverUsersDB::deleteUser] Parametro non valido.");

		String login = user.getLogin();
		if (login == null || login.equals(""))
			throw new DriverUsersDBException("[DriverUsersDB::deleteUser] Parametro Login non valido.");

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.USERS);
			sqlQueryObject.addWhereCondition("login = ?");
			sqlQuery = sqlQueryObject.createSQLDelete();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			stm.setString(1, login);
			stm.executeUpdate();
			stm.close();
		} catch (Exception qe) {
			throw new DriverUsersDBException(qe.getMessage(),qe);
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

	/**
	 * Reset delle tabelle del db gestito da questo driver
	 */
	public void reset() throws DriverUsersDBException {
		PreparedStatement stmt = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable("users");
			sqlQueryObject.addWhereCondition("login <> ?");
			sqlQueryObject.addWhereCondition("login <> ?");
			sqlQueryObject.setANDLogicOperator(true);
			String updateString = sqlQueryObject.createSQLDelete();
			stmt = this.connectionDB.prepareStatement(updateString);
			stmt.setString(1, "super");
			stmt.setString(2, "amministratore");
			stmt.executeUpdate();
			stmt.close();
		} catch (Exception qe) {
			throw new DriverUsersDBException(qe.getMessage(),qe);
		} finally {
			//Chiudo statement and resultset
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				//ignore
			}
		}
	}
}
