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
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverConfigurazioneDB_soggettiSearchDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_soggettiSearchDriver {

	private DriverConfigurazioneDB driver = null;
	
	protected DriverConfigurazioneDB_soggettiSearchDriver(DriverConfigurazioneDB driver) {
		this.driver = driver;
	}
	
	protected List<Soggetto> soggettiList(String superuser, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "soggettiList";
		int idLista = Liste.SOGGETTI;
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
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		
		String filtroProprietaNome = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROPRIETA_NOME);
		String filtroProprietaValore = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROPRIETA_VALORE);
		if((filtroProprietaNome!=null && "".equals(filtroProprietaNome))) {
			filtroProprietaNome=null;
		}
		if((filtroProprietaValore!=null && "".equals(filtroProprietaValore))) {
			filtroProprietaValore=null;
		}
		boolean filtroProprieta = filtroProprietaNome!=null || filtroProprietaValore!=null;

		this.driver.logDebug("search : " + search);
		this.driver.logDebug("filterProtocollo : " + filterProtocollo);
		this.driver.logDebug("filterProtocolli : " + filterProtocolli);
		this.driver.logDebug("filtroProprietaNome : " + filtroProprietaNome);
		this.driver.logDebug("filtroProprietaValore : " + filtroProprietaValore);
		
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Soggetto> lista = new ArrayList<Soggetto>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("soggettiList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(this.driver.tabellaSoggetti);
				sqlQueryObject.addSelectCountField("*", "cont");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(this.driver.useSuperUser && superuser!=null && !superuser.equals(""))
					sqlQueryObject.addWhereCondition("superuser = ?");
				sqlQueryObject.addWhereLikeCondition("nome_soggetto", search, true, true);
				if(this.driver.useSuperUser && superuser!=null && !superuser.equals(""))
					sqlQueryObject.setANDLogicOperator(true);
				if(filtroProprieta) {
					DBUtils.setFiltriProprietaSoggetto(sqlQueryObject, this.driver.tipoDB, 
							filtroProprietaNome, filtroProprietaValore);
				}
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(this.driver.tabellaSoggetti);
				sqlQueryObject.addSelectCountField("*", "cont");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(this.driver.useSuperUser && superuser!=null && !superuser.equals(""))
					sqlQueryObject.addWhereCondition("superuser = ?");
				if(filtroProprieta) {
					DBUtils.setFiltriProprietaSoggetto(sqlQueryObject, this.driver.tipoDB, 
							filtroProprietaNome, filtroProprietaValore);
				}
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			if(this.driver.useSuperUser && superuser!=null && !superuser.equals(""))
				stmt.setString(1, superuser);
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
				sqlQueryObject.addFromTable(this.driver.tabellaSoggetti);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_soggetto");
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addSelectField("descrizione");
				sqlQueryObject.addSelectField("identificativo_porta");
				sqlQueryObject.addSelectField("is_router");
				sqlQueryObject.addSelectField("is_default");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(this.driver.useSuperUser && superuser!=null && !superuser.equals(""))
					sqlQueryObject.addWhereCondition("superuser = ?");
				if(filtroProprieta) {
					DBUtils.setFiltriProprietaSoggetto(sqlQueryObject, this.driver.tipoDB, 
							filtroProprietaNome, filtroProprietaValore);
				}
				sqlQueryObject.addWhereLikeCondition("nome_soggetto", search, true, true);
				if(this.driver.useSuperUser && superuser!=null && !superuser.equals(""))
					sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome_soggetto");
				sqlQueryObject.addOrderBy("tipo_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(this.driver.tabellaSoggetti);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_soggetto");
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addSelectField("descrizione");
				sqlQueryObject.addSelectField("identificativo_porta");
				sqlQueryObject.addSelectField("is_router");
				sqlQueryObject.addSelectField("is_default");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(this.driver.useSuperUser && superuser!=null && !superuser.equals(""))
					sqlQueryObject.addWhereCondition("superuser = ?");
				if(filtroProprieta) {
					DBUtils.setFiltriProprietaSoggetto(sqlQueryObject, this.driver.tipoDB, 
							filtroProprietaNome, filtroProprietaValore);
				}
				sqlQueryObject.addOrderBy("nome_soggetto");
				sqlQueryObject.addOrderBy("tipo_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			if(this.driver.useSuperUser && superuser!=null && !superuser.equals(""))
				stmt.setString(1, superuser);
			risultato = stmt.executeQuery();

			Soggetto sog;
			while (risultato.next()) {

				sog = new Soggetto();
				sog.setId(risultato.getLong("id"));
				sog.setNome(risultato.getString("nome_soggetto"));
				sog.setTipo(risultato.getString("tipo_soggetto"));
				sog.setDescrizione(risultato.getString("descrizione"));
				sog.setIdentificativoPorta(risultato.getString("identificativo_porta"));
				sog.setRouter(risultato.getInt("is_router") == CostantiDB.TRUE ? true : false);
				sog.setDominioDefault(risultato.getInt("is_default") == CostantiDB.TRUE ? true : false);
				lista.add(sog);
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

	protected List<Soggetto> soggettiWithServiziList(ISearch ricerca) throws DriverConfigurazioneException {
		return soggettiWithServiziList(null,ricerca);
	}
	protected List<Soggetto> soggettiWithServiziList(String superuser,ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "soggettiWithServiziList";
		int idLista = Liste.SOGGETTI;
		int offset;
		int limit;
		//String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		//search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Soggetto> lista = new ArrayList<Soggetto>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("soggettiWithServiziList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(this.driver.tabellaSoggetti);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectCountField(this.driver.tabellaSoggetti+".id", "cont", true);
			sqlQueryObject.addWhereCondition(this.driver.tabellaSoggetti+".id = "+CostantiDB.SERVIZI+".id_soggetto");
			if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser))){
				sqlQueryObject.addWhereCondition(this.driver.tabellaSoggetti+".superuser=?");
			}
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser))){
				stmt.setString(1, superuser);
			}
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(this.driver.tabellaSoggetti);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addSelectField(this.driver.tabellaSoggetti+".id");
			sqlQueryObject.addSelectField(this.driver.tabellaSoggetti+".tipo_soggetto");
			sqlQueryObject.addSelectField(this.driver.tabellaSoggetti+".nome_soggetto");
			sqlQueryObject.addWhereCondition(this.driver.tabellaSoggetti+".id = "+CostantiDB.SERVIZI+".id_soggetto");
			if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser))){
				sqlQueryObject.addWhereCondition(this.driver.tabellaSoggetti+".superuser=?");
			}
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(this.driver.tabellaSoggetti+".tipo_soggetto");
			sqlQueryObject.addOrderBy(this.driver.tabellaSoggetti+".nome_soggetto");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser))){
				stmt.setString(1, superuser);
			}
			risultato = stmt.executeQuery();

			Soggetto sog;
			while (risultato.next()) {

				sog = new Soggetto();
				sog.setId(risultato.getLong("id"));
				sog.setNome(risultato.getString("nome_soggetto"));
				sog.setTipo(risultato.getString("tipo_soggetto"));
				lista.add(sog);
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
