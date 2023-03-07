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
package org.openspcoop2.core.config.driver.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.Route;
import org.openspcoop2.core.config.RouteGateway;
import org.openspcoop2.core.config.RouteRegistro;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.RoutingTableDefault;
import org.openspcoop2.core.config.RoutingTableDestinazione;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverConfigurazioneDB_routingTableDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_routingTableDriver {

	private DriverConfigurazioneDB driver = null;
	
	protected DriverConfigurazioneDB_routingTableDriver(DriverConfigurazioneDB driver) {
		this.driver = driver;
	}

	
	protected RoutingTable getRoutingTable() throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		PreparedStatement stmSearch = null;
		ResultSet rsSearch = null;
		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getRoutingTable");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getRoutingTable] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			boolean routingEnabled = false;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE);
			sqlQueryObject.addSelectField("*");
			sqlQuery = sqlQueryObject.createSQLQuery();
			this.driver.log.debug("eseguo query per routing enabled : " + DBUtils.formatSQLString(sqlQuery));
			stm = con.prepareStatement(sqlQuery);

			rs = stm.executeQuery();

			if (rs.next()) {
				this.driver.log.debug("ConfigurazionePresente");
				this.driver.log.debug("Risultato query per routing enabled ["+rs.getString("routing_enabled")+"]");
				routingEnabled = CostantiConfigurazione.ABILITATO.equals(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("routing_enabled")));
			}
			rs.close();
			stm.close();
			this.driver.log.debug("RoutingEnabled: "+routingEnabled);

			RoutingTable rt = new RoutingTable();
			rt.setAbilitata(routingEnabled);
			//sia che il routing sia abilitato/disabilitato
			//le rotte possono essere comunque presenti
			//if (routingEnabled) {

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ROUTING);
			sqlQueryObject.addSelectField("*");
			sqlQuery = sqlQueryObject.createSQLQuery();
			this.driver.log.debug("eseguo query per routing table : " + DBUtils.formatSQLString(sqlQuery));
			stm = con.prepareStatement(sqlQuery);

			rs = stm.executeQuery();

			String tipo = null;
			String nome = null;
			String tipoRotta = null;
			String tiposoggrotta = null;
			String nomesoggrotta = null;
			long id_registrorotta = 0;
			boolean is_default = false;
			long idR = 0;

			RoutingTableDefault rtdefault = null;
			Route route = null;
			RouteGateway routeGateway = null;
			RouteRegistro routeRegistro = null;
			RoutingTableDestinazione rtd = null;

			int nroute = 0;
			this.driver.log.debug("Check esistenza rotte....");
			while (rs.next()) {

				this.driver.log.debug("Nuova rotta....["+rs.getInt("is_default")+"]");

				nroute++;
				// nuova route
				route = new Route();

				idR = rs.getLong("id");
				tipo = rs.getString("tipo");
				nome = rs.getString("nome");
				tipoRotta = rs.getString("tiporotta");
				nomesoggrotta = rs.getString("nomesoggrotta");
				tiposoggrotta = rs.getString("tiposoggrotta");
				id_registrorotta = rs.getLong("registrorotta");
				if(rs.getInt("is_default")==1)
					is_default = true;
				else
					is_default = false;

				if (tipoRotta.equalsIgnoreCase("registro")) {
					// e' una rotta registro
					routeRegistro = new RouteRegistro();

					// se e' 0 allora significa ke voglio tutte le rotte
					if (id_registrorotta != 0) {
						// mi serve il nome di questa rotta
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.REGISTRI);
						sqlQueryObject.addSelectField("*");
						sqlQueryObject.addWhereCondition("id = ?");
						sqlQuery = sqlQueryObject.createSQLQuery();
						stmSearch = con.prepareStatement(sqlQuery);
						stmSearch.setLong(1, id_registrorotta);

						this.driver.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, id_registrorotta));

						rsSearch = stmSearch.executeQuery();
						if (rsSearch.next()) {
							routeRegistro.setNome(rsSearch.getString("nome"));

						}
						rsSearch.close();
						stmSearch.close();
					}

					route.setRegistro(routeRegistro);

				} else if (tipoRotta.equalsIgnoreCase("gateway")) {
					// e' una rotta gw
					routeGateway = new RouteGateway();

					routeGateway.setNome(nomesoggrotta);
					routeGateway.setTipo(tiposoggrotta);

					route.setGateway(routeGateway);
				}

				route.setId(idR);

				// e' di default
				if (is_default){
					if(rtdefault==null){
						rtdefault = new RoutingTableDefault();
						rt.setDefault(rtdefault);
					}
					rt.getDefault().addRoute(route);
				}
				else {// allora e' di destinazione
					rtd = new RoutingTableDestinazione();
					rtd.setNome(nome);
					rtd.setTipo(tipo);
					rtd.addRoute(route);
					rt.addDestinazione(rtd);
				}
			}

			this.driver.log.debug("Ci sono " + nroute + " rotte configurate.");
			rs.close();
			stm.close();
			//if (nroute == 0)
			//	throw new DriverConfigurazioneNotFound("[DriverConfigurazioneDB::getRoutingTable] Routing Abilitato ma nessuna route trovata.]");



			//}
			//else {
			//	throw new DriverConfigurazioneNotFound("[DriverConfigurazioneDB::getRoutingTable] Routing Disabilitato]");
			//}

			return rt;


		} catch (SQLException se) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getRoutingTable] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getRoutingTable] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rsSearch, stmSearch);
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(con);
		}

	}

	protected void createRoutingTable(RoutingTable routingTable) throws DriverConfigurazioneException {

		if (routingTable == null ||  routingTable.getDefault() == null || routingTable.getDefault().sizeRouteList() == 0)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createRoutingTable] Parametri non validi.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("createRoutingTable");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createRoutingTable] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.log.debug("CRUDRoutingTable type = 1");
			// creo soggetto
			DriverConfigurazioneDB_routingTableLIB.CRUDRoutingTable(1, routingTable, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createRoutingTable] Errore durante la creazione della RoutingTable : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected void updateRoutingTable(RoutingTable routingTable) throws DriverConfigurazioneException {
		if (routingTable == null ||  routingTable.getDefault() == null || routingTable.getDefault().sizeRouteList() == 0)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateRoutingTable] Parametri non validi.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updateRoutingTable");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createRoutingTable] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.log.debug("CRUDRoutingTable type = 2");
			// creo soggetto
			DriverConfigurazioneDB_routingTableLIB.CRUDRoutingTable(2, routingTable, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateRoutingTable] Errore durante la update della RoutingTable : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected void deleteRoutingTable(RoutingTable routingTable) throws DriverConfigurazioneException {
		if (routingTable == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteRoutingTable] Parametri non validi.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("deleteRoutingTable");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteRoutingTable] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.log.debug("CRUDRoutingTable type = 3");
			// creo soggetto
			DriverConfigurazioneDB_routingTableLIB.CRUDRoutingTable(3, routingTable, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteRoutingTable] Errore durante la delete della RoutingTable : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected List<RoutingTableDestinazione> routingList(ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "routingList";
		int idLista = Liste.ROUTING;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<RoutingTableDestinazione> lista = new ArrayList<RoutingTableDestinazione>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("routingList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ROUTING);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("is_default = 0");
				sqlQueryObject.addWhereCondition(false, sqlQueryObject.getWhereLikeCondition("tipo",search,true,true), sqlQueryObject.getWhereLikeCondition("nome",search,true,true));
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ROUTING);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("is_default = 0");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ROUTING);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("tipo");
				sqlQueryObject.addSelectField("tiporotta");
				sqlQueryObject.addSelectField("is_default");
				sqlQueryObject.addWhereCondition("is_default = 0");
				sqlQueryObject.addWhereCondition(false, sqlQueryObject.getWhereLikeCondition("tipo",search,true,true), sqlQueryObject.getWhereLikeCondition("nome",search,true,true));
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("tipo");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ROUTING);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("tipo");
				sqlQueryObject.addSelectField("tiporotta");
				sqlQueryObject.addSelectField("is_default");
				sqlQueryObject.addWhereCondition("is_default = 0");
				sqlQueryObject.addOrderBy("tipo");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();

			RoutingTableDestinazione rtd;
			while (risultato.next()) {

				rtd = new RoutingTableDestinazione();

				rtd.setId(risultato.getLong("id"));
				rtd.setNome(risultato.getString("nome"));
				rtd.setTipo(risultato.getString("tipo"));
				// Non è necessario popolare rg e rr
				// perchè tanto il metodo che prepara la lista
				// deve solo decidere il tiporotta scelto
				Route tmpR = new Route();
				if ("gateway".equals(risultato.getString("tiporotta"))) {
					RouteGateway rg = new RouteGateway();
					tmpR.setGateway(rg);
				} else {
					RouteRegistro rr = new RouteRegistro();
					tmpR.setRegistro(rr);
				}
				rtd.addRoute(tmpR);

				lista.add(rtd);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}
}
