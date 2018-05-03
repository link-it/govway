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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import org.openspcoop2.web.lib.users.dao.InterfaceType;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;
import org.openspcoop2.web.lib.users.dao.User;
import org.slf4j.Logger;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.commons.search.dao.IDBAccordoServizioParteSpecificaServiceSearch;
import org.openspcoop2.core.commons.search.dao.IDBSoggettoServiceSearch;
import org.openspcoop2.core.commons.search.dao.jdbc.JDBCServiceManager;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerProperties;
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
	
	private Logger log;

	public DriverUsersDB(Connection con, String tipoDB, Logger log) throws DriverUsersDBException {
		this.connectionDB = con;
		if (con == null)
			throw new DriverUsersDBException("Connessione al Database non definita");

		this.tipoDB = tipoDB;
		if (tipoDB == null)
			throw new DriverUsersDBException("Il tipoDatabase non puo essere null.");
		
		this.log = log;
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
			jdbcProperties.setDatabaseType(this.tipoDB);
			jdbcProperties.setShowSql(true);
			JDBCServiceManager search = new JDBCServiceManager(this.connectionDB, jdbcProperties, this.log);
			IDBSoggettoServiceSearch soggettiSearch = (IDBSoggettoServiceSearch) search.getSoggettoServiceSearch();
			IDBAccordoServizioParteSpecificaServiceSearch serviziSearch = (IDBAccordoServizioParteSpecificaServiceSearch) search.getAccordoServizioParteSpecificaServiceSearch();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.USERS_SOGGETTI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_utente = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = this.connectionDB.prepareStatement(sqlQuery);
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
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.USERS_SERVIZI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_utente = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = this.connectionDB.prepareStatement(sqlQuery);
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

	public List<String> getUsersByPermesso(String permesso) throws DriverUsersDBException {
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		try {
			List<String> userWithType = new ArrayList<String>();
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.USERS);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.setANDLogicOperator(true);
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
	
	public List<String> getUsersByProtocolloSupportato(String protocollo) throws DriverUsersDBException {
		return getUsersByProtocolloSupportato(protocollo, false);
	}
	
	public List<String> getUsersByProtocolloSupportato(String protocollo, boolean esclusiUtentiConSoloPermessoUtente) throws DriverUsersDBException {
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		try {
			List<String> userWithType = new ArrayList<String>();
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
			stm = this.connectionDB.prepareStatement(sqlQuery);
			
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
		}
	}

	public void createUser(User user) throws DriverUsersDBException {
		PreparedStatement stm = null;
		ResultSet rs = null;
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
			sqlQueryObject.addInsertField("interfaccia_completa", "?");
			sqlQueryObject.addInsertField("permessi", "?");
			sqlQueryObject.addInsertField("protocolli", "?");
			sqlQueryObject.addInsertField("protocollo", "?");
			sqlQueryObject.addInsertField("multi_tenant", "?");
			sqlQuery = sqlQueryObject.createSQLInsert();
			stm = this.connectionDB.prepareStatement(sqlQuery);
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
						
			if(user.getSoggetti().size()>0 || user.getServizi().size()>0) {
			
				// recupero id
				long idUser = this._getIdUser(login);
				
				_addListaSoggettiServizi(user, idUser);
				
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
		}
	}

	public void updateUser(User user) throws DriverUsersDBException {
		PreparedStatement stm = null;
		ResultSet rs = null;
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
			sqlQueryObject.addUpdateField("interfaccia_completa", "?");
			sqlQueryObject.addUpdateField("permessi", "?");
			sqlQueryObject.addUpdateField("protocolli", "?");
			sqlQueryObject.addUpdateField("protocollo", "?");
			sqlQueryObject.addUpdateField("multi_tenant", "?");
			sqlQueryObject.addWhereCondition("login=?");
			sqlQuery = sqlQueryObject.createSQLUpdate();
			stm = this.connectionDB.prepareStatement(sqlQuery);
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
			
			long idUser = this._getIdUser(user);
				
			_deleteListaSoggettiServizi(idUser);
			
			if(user.getSoggetti().size()>0 || user.getServizi().size()>0) {
				_addListaSoggettiServizi(user, idUser);
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
		}
	}
	
	public void deleteUser(User user) throws DriverUsersDBException {
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (user == null)
			throw new DriverUsersDBException("[DriverUsersDB::deleteUser] Parametro non valido.");

		String login = user.getLogin();
		if (login == null || login.equals(""))
			throw new DriverUsersDBException("[DriverUsersDB::deleteUser] Parametro Login non valido.");

		try {
			
			long idUser = this._getIdUser(user);
			
			_deleteListaSoggettiServizi(idUser);
			
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

	
	
	private void _addListaSoggettiServizi(User user, long idUser) throws Exception {
		PreparedStatement stm = null;
		ResultSet rs = null;
		try {
		
			if(user.getSoggetti().size()>0) {
				for (IDSoggetto idSoggetto : user.getSoggetti()) {
					
					long idSoggettoLong = DBUtils.getIdSoggetto(idSoggetto.getNome(), idSoggetto.getTipo(), this.connectionDB, this.tipoDB);
					if(idSoggettoLong<=0) {
						throw new Exception("Impossibile recuperare id soggetto ["+idSoggetto+"]");
					}
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.USERS_SOGGETTI);
					sqlQueryObject.addInsertField("id_utente", "?");
					sqlQueryObject.addInsertField("id_soggetto", "?");
					String sqlQuery = sqlQueryObject.createSQLInsert();
					stm = this.connectionDB.prepareStatement(sqlQuery);
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
							this.connectionDB, this.tipoDB);
					if(idServizioLong<=0) {
						throw new Exception("Impossibile recuperare id soggetto ["+idServizio+"]");
					}
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.USERS_SERVIZI);
					sqlQueryObject.addInsertField("id_utente", "?");
					sqlQueryObject.addInsertField("id_servizio", "?");
					String sqlQuery = sqlQueryObject.createSQLInsert();
					stm = this.connectionDB.prepareStatement(sqlQuery);
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
	
	private void _deleteListaSoggettiServizi(long idUser) throws Exception {
		PreparedStatement stm = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.USERS_SOGGETTI);
			sqlQueryObject.addWhereCondition("id_utente = ?");
			String sqlQuery = sqlQueryObject.createSQLDelete();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			stm.setLong(1, idUser);
			stm.executeUpdate();
			stm.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.USERS_SERVIZI);
			sqlQueryObject.addWhereCondition("id_utente = ?");
			sqlQuery = sqlQueryObject.createSQLDelete();
			stm = this.connectionDB.prepareStatement(sqlQuery);
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
	
	private long _getIdUser(User user) throws Exception {
		
		if (user == null)
			throw new DriverUsersDBException("[DriverUsersDB::_getIdUser] Parametro non valido.");

		String login = user.getLogin();
		if (login == null || login.equals(""))
			throw new DriverUsersDBException("[DriverUsersDB::_getIdUser] Parametro Login non valido.");

		long idUser = -1;
		if(user.getId()==null || user.getId().longValue()<=0) {
			idUser = this._getIdUser(login);
		}
		else {
			idUser = user.getId().longValue();
		}
		
		return idUser;
			
	}
	private long _getIdUser(String login) throws Exception {
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (login == null || login.equals(""))
			throw new DriverUsersDBException("[DriverUsersDB::_getIdUser] Parametro Login non valido.");

		try {
			
			long idUser = -1;
			// recupero id
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.USERS);
			sqlQueryObject.addWhereCondition("login = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = this.connectionDB.prepareStatement(sqlQuery);
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
}
