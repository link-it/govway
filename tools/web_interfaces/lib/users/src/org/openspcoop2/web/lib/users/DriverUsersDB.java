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
import java.util.ArrayList;
import java.util.List;

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
			sqlQueryObject.addFromTable(CostantiDB.USERS_STATI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_utente = ?");
			sqlQueryObject.addOrderBy(CostantiDB.USERS_STATI+".oggetto");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = this.connectionDB.prepareStatement(sqlQuery);
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
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
						
			if(user.getStati().size()>0 || user.getSoggetti().size()>0 || user.getServizi().size()>0) {
			
				// recupero id
				long idUser = this._getIdUser(login);
				
				_addListeUtente(user, idUser);
				
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
				
			_deleteListeUtente(idUser);
			
			if(user.getStati().size()>0 || user.getSoggetti().size()>0 || user.getServizi().size()>0) {
				_addListeUtente(user, idUser);
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
			
			_deleteListeUtente(idUser);
			
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

	
	
	private void _addListeUtente(User user, long idUser) throws Exception {
		PreparedStatement stm = null;
		ResultSet rs = null;
		try {
		
			if(user.getStati().size()>0) {
				for (Stato stato : user.getStati()) {
					
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.USERS_STATI);
					sqlQueryObject.addInsertField("id_utente", "?");
					sqlQueryObject.addInsertField("oggetto", "?");
					sqlQueryObject.addInsertField("stato", "?");
					String sqlQuery = sqlQueryObject.createSQLInsert();
					stm = this.connectionDB.prepareStatement(sqlQuery);
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
	
	private void _deleteListeUtente(long idUser) throws Exception {
		PreparedStatement stm = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.USERS_STATI);
			sqlQueryObject.addWhereCondition("id_utente = ?");
			String sqlQuery = sqlQueryObject.createSQLDelete();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			stm.setLong(1, idUser);
			stm.executeUpdate();
			stm.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.USERS_SOGGETTI);
			sqlQueryObject.addWhereCondition("id_utente = ?");
			sqlQuery = sqlQueryObject.createSQLDelete();
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
		
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		ArrayList<IDServizio> lista = new ArrayList<IDServizio>();

		try {
			
			ISQLQueryObject sqlQueryObjectSoggetti = null;
			if (!search.equals("")) {
				sqlQueryObjectSoggetti = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
				ISQLQueryObject sqlQueryObjectExistsPdd = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObjectExistsPdd.addSelectField(CostantiDB.PDD+".nome");
				sqlQueryObjectExistsPdd.addFromTable(CostantiDB.PDD);
				sqlQueryObjectExistsPdd.setANDLogicOperator(true);
				sqlQueryObjectExistsPdd.addWhereCondition(CostantiDB.PDD+".nome="+CostantiDB.SOGGETTI+".server");
				sqlQueryObjectExistsPdd.addWhereCondition(CostantiDB.PDD+".tipo=?");
				
				sqlQueryObjectPdd = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObjectPdd.setANDLogicOperator(false);
				sqlQueryObjectPdd.addWhereIsNullCondition(CostantiDB.SOGGETTI+".server");
				sqlQueryObjectPdd.addWhereExistsCondition(false, sqlQueryObjectExistsPdd);
			}
			
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
			stmt = this.connectionDB.prepareStatement(queryString);
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
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
			stmt = this.connectionDB.prepareStatement(queryString);
			index = 1;
			stmt.setString(index++, login);
			if(pddTipologia!=null) {
				stmt.setString(index++, pddTipologia.toString());
			}
			risultato = stmt.executeQuery();
			
			JDBCServiceManagerProperties jdbcProperties = new JDBCServiceManagerProperties();
			jdbcProperties.setDatabaseType(this.tipoDB);
			jdbcProperties.setShowSql(true);
			JDBCServiceManager manager = new JDBCServiceManager(this.connectionDB, jdbcProperties, this.log);
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
		
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		ArrayList<IDSoggetto> lista = new ArrayList<IDSoggetto>();

		try {
			
			ISQLQueryObject sqlQueryObjectPdd = null;
			if(pddTipologia!=null && PddTipologia.ESTERNO.equals(pddTipologia)) {
				ISQLQueryObject sqlQueryObjectExistsPdd = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObjectExistsPdd.addSelectField(CostantiDB.PDD+".nome");
				sqlQueryObjectExistsPdd.addFromTable(CostantiDB.PDD);
				sqlQueryObjectExistsPdd.setANDLogicOperator(true);
				sqlQueryObjectExistsPdd.addWhereCondition(CostantiDB.PDD+".nome="+CostantiDB.SOGGETTI+".server");
				sqlQueryObjectExistsPdd.addWhereCondition(CostantiDB.PDD+".tipo=?");
				
				sqlQueryObjectPdd = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObjectPdd.setANDLogicOperator(false);
				sqlQueryObjectPdd.addWhereIsNullCondition(CostantiDB.SOGGETTI+".server");
				sqlQueryObjectPdd.addWhereExistsCondition(false, sqlQueryObjectExistsPdd);
			}
			
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
			stmt = this.connectionDB.prepareStatement(queryString);
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
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
			stmt = this.connectionDB.prepareStatement(queryString);
			index = 1;
			stmt.setString(index++, login);
			if(pddTipologia!=null) {
				stmt.setString(index++, pddTipologia.toString());
			}
			risultato = stmt.executeQuery();
			
			JDBCServiceManagerProperties jdbcProperties = new JDBCServiceManagerProperties();
			jdbcProperties.setDatabaseType(this.tipoDB);
			jdbcProperties.setShowSql(true);
			JDBCServiceManager manager = new JDBCServiceManager(this.connectionDB, jdbcProperties, this.log);
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
		}
	}
}
