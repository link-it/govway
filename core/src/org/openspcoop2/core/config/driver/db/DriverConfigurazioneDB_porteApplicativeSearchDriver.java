/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.MessageSecurityFlowParameter;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetto;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverConfigurazioneDB_porteApplicativeSearchDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_porteApplicativeSearchDriver {

	private DriverConfigurazioneDB driver = null;
	private DriverConfigurazioneDB_porteApplicativeDriver porteApplicativeDriver = null;
	
	protected DriverConfigurazioneDB_porteApplicativeSearchDriver(DriverConfigurazioneDB driver) {
		this.driver = driver;
		this.porteApplicativeDriver = new DriverConfigurazioneDB_porteApplicativeDriver(driver);
	}
	
	protected List<String> portaApplicativaRuoliList(long idPA, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "portaApplicativaRuoliList";
		int idLista = Liste.PORTE_APPLICATIVE_RUOLI;
		String nomeTabella = CostantiDB.PORTE_APPLICATIVE_RUOLI;
		return _portaApplicativaRuoliList(idPA, ricerca,
				nomeMetodo, nomeTabella, idLista);
	}
	protected List<String> portaApplicativaRuoliTokenList(long idPA, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "portaApplicativaRuoliTokenList";
		int idLista = Liste.PORTE_APPLICATIVE_TOKEN_RUOLI;
		String nomeTabella = CostantiDB.PORTE_APPLICATIVE_TOKEN_RUOLI;
		return _portaApplicativaRuoliList(idPA, ricerca,
				nomeMetodo, nomeTabella, idLista);
	}
	private List<String> _portaApplicativaRuoliList(long idPA, ISearch ricerca,
			String nomeMetodo, String nomeTabella, int idLista) throws DriverConfigurazioneException {
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		List<String> listIdRuoli = null;
		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(nomeTabella);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
				sqlQueryObject.addWhereLikeCondition(nomeTabella+".ruolo", search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(nomeTabella);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");

				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);

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
				sqlQueryObject.addFromTable(nomeTabella);
				sqlQueryObject.addSelectField(nomeTabella+".ruolo");
				sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
				sqlQueryObject.addWhereLikeCondition(nomeTabella+".ruolo", search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(nomeTabella+".ruolo");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(nomeTabella);
				sqlQueryObject.addSelectField(nomeTabella+".ruolo");
				sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(nomeTabella+".ruolo");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);

			risultato = stmt.executeQuery();

			listIdRuoli = new ArrayList<>();
			while (risultato.next()) {

				listIdRuoli.add(risultato.getString(1));

			}

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
		
		return listIdRuoli;
	}
	
	protected List<String> portaApplicativaScopeList(long idPA, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "portaApplicativaScopeList";
		int idLista = Liste.PORTE_APPLICATIVE_SCOPE;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		List<String> listIdScope = null;
		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SCOPE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SCOPE+".id_porta=?");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.PORTE_APPLICATIVE_SCOPE+".scope", search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SCOPE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SCOPE+".id_porta=?");

				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);

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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SCOPE);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE_SCOPE+".scope");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SCOPE+".id_porta=?");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.PORTE_APPLICATIVE_SCOPE+".scope", search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.PORTE_APPLICATIVE_SCOPE+".scope");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SCOPE);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE_SCOPE+".scope");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SCOPE+".id_porta=?");
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.PORTE_APPLICATIVE_SCOPE+".scope");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);

			risultato = stmt.executeQuery();

			listIdScope = new ArrayList<>();
			while (risultato.next()) {

				listIdScope.add(risultato.getString(1));

			}

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
		
		return listIdScope;
	}
	
	
	protected List<PortaApplicativa> porteAppList(long idSoggetto, ISearch ricerca) throws DriverConfigurazioneException {
		int offset;
		int limit;
		int idLista=Liste.PORTE_APPLICATIVE_BY_SOGGETTO;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		Connection con = null;
		boolean error = false;
		PreparedStatement stm=null;
		ResultSet rs=null;
		ArrayList<PortaApplicativa> lista = new ArrayList<PortaApplicativa>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteAppList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppList] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.addWhereLikeCondition("nome_porta",search,true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stm = con.prepareStatement(queryString);
			stm.setLong(1, idSoggetto);
			rs = stm.executeQuery();
			if (rs.next())
				ricerca.setNumEntries(idLista,rs.getInt(1));
			rs.close();
			stm.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_porta");
				sqlQueryObject.addSelectField("id_soggetto");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.addWhereLikeCondition("nome_porta",search,true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome_porta");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_porta");
				sqlQueryObject.addSelectField("id_soggetto");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.addOrderBy("nome_porta");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}

			stm = con.prepareStatement(queryString);
			stm.setLong(1, idSoggetto);
			rs = stm.executeQuery();

			PortaApplicativa pa = null;
			while (rs.next()) {
				pa = this.porteApplicativeDriver.getPortaApplicativa(rs.getLong("id"),con);
				lista.add(pa);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppList] Errore : " + qe.getMessage(),qe);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(error,con);
		}

	}
	/**
	 * Ritorna la lista di tutte le Porte Applicative
	 * @param ricerca
	 * @return Una lista di Porte Applicative
	 * @throws DriverConfigurazioneException
	 */
	protected List<PortaApplicativa> porteAppList(String superuser, ISearch ricerca) throws DriverConfigurazioneException {
		int offset;
		int limit;
		int idLista=Liste.PORTE_APPLICATIVE;
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
		
		this.driver.logDebug("search : " + search);
		this.driver.logDebug("filterProtocollo : " + filterProtocollo);
		this.driver.logDebug("filterProtocolli : " + filterProtocolli);
		
		Connection con = null;
		boolean error = false;
		PreparedStatement stm=null;
		ResultSet rs=null;
		ArrayList<PortaApplicativa> lista = new ArrayList<PortaApplicativa>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteAppList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppList] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				sqlQueryObject.addWhereLikeCondition("nome_porta",search,true, true);
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stm = con.prepareStatement(queryString);
			if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser)))
				stm.setString(1, superuser);
			rs = stm.executeQuery();
			if (rs.next())
				ricerca.setNumEntries(idLista,rs.getInt(1));
			rs.close();
			stm.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".id");
				sqlQueryObject.addSelectField("nome_porta");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				sqlQueryObject.addWhereLikeCondition("nome_porta",search,true, true);
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome_porta");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".id");
				sqlQueryObject.addSelectField("nome_porta");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome_porta");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}

			stm = con.prepareStatement(queryString);
			if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser)))
				stm.setString(1, superuser);
			rs = stm.executeQuery();

			PortaApplicativa pa = null;
			while (rs.next()) {

				pa = this.porteApplicativeDriver.getPortaApplicativa(rs.getLong("id"),con);

				lista.add(pa);

			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppList] Errore : " + qe.getMessage(),qe);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(error,con);
		}

	}

	/**
	 * Ritorna la lista di proprieta di una Porta Applicativa
	 */
	protected List<Proprieta> porteAppPropList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		int offset;
		int limit;
		int idLista = Liste.PORTE_APPLICATIVE_PROP;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));		


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Proprieta> lista = new ArrayList<>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteAppPropList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppPropList] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_PROP);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addSelectField("enc_value");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_PROP);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addSelectField("enc_value");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			Proprieta prop = null;
			while (risultato.next()) {

				prop = new Proprieta();

				prop.setId(risultato.getLong("id_porta"));
				prop.setNome(risultato.getString("nome"));
				
				String plainValue = risultato.getString("valore");
				String encValue = risultato.getString("enc_value");
				if(encValue!=null && StringUtils.isNotEmpty(encValue)) {
					prop.setValore(encValue);
				}
				else {
					prop.setValore(plainValue);
				}

				lista.add(prop);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppPropList] Errore : " + qe.getMessage(),qe);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(error,con);
		}
	}
	
	protected List<Proprieta> porteApplicativeAutenticazioneCustomPropList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		int offset;
		int limit;
		int idLista = Liste.PORTE_APPLICATIVE_PROPRIETA_AUTENTICAZIONE;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));		


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Proprieta> lista = new ArrayList<>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteApplicativeAutorizzazioneCustomPropList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppPropList] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addSelectField("enc_value");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addSelectField("enc_value");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			Proprieta prop = null;
			while (risultato.next()) {

				prop = new Proprieta();

				prop.setId(risultato.getLong("id"));
				prop.setNome(risultato.getString("nome"));
				
				String plainValue = risultato.getString("valore");
				String encValue = risultato.getString("enc_value");
				if(encValue!=null && StringUtils.isNotEmpty(encValue)) {
					prop.setValore(encValue);
				}
				else {
					prop.setValore(plainValue);
				}

				lista.add(prop);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppPropList] Errore : " + qe.getMessage(),qe);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(error,con);
		}
	}
	
	/**
	 * Ritorna la lista di proprieta per l'autorizzazione custom di una Porta Applicativa
	 */
	protected List<Proprieta> porteApplicativeAutorizzazioneCustomPropList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		int offset;
		int limit;
		int idLista = Liste.PORTE_APPLICATIVE_PROPRIETA_AUTORIZZAZIONE;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));		


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Proprieta> lista = new ArrayList<>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteApplicativeAutorizzazioneCustomPropList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppPropList] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addSelectField("enc_value");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addSelectField("enc_value");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			Proprieta prop = null;
			while (risultato.next()) {

				prop = new Proprieta();

				prop.setId(risultato.getLong("id"));
				prop.setNome(risultato.getString("nome"));
				
				String plainValue = risultato.getString("valore");
				String encValue = risultato.getString("enc_value");
				if(encValue!=null && StringUtils.isNotEmpty(encValue)) {
					prop.setValore(encValue);
				}
				else {
					prop.setValore(plainValue);
				}

				lista.add(prop);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppPropList] Errore : " + qe.getMessage(),qe);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(error,con);
		}
	}
	
	protected List<Proprieta> porteApplicativeAutorizzazioneContenutoCustomPropList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		int offset;
		int limit;
		int idLista = Liste.PORTE_APPLICATIVE_PROPRIETA_AUTORIZZAZIONE_CONTENUTO;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));		


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Proprieta> lista = new ArrayList<>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteApplicativeAutorizzazioneCustomPropList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppPropList] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addSelectField("enc_value");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addSelectField("enc_value");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			Proprieta prop = null;
			while (risultato.next()) {

				prop = new Proprieta();

				prop.setId(risultato.getLong("id"));
				prop.setNome(risultato.getString("nome"));
				
				String plainValue = risultato.getString("valore");
				String encValue = risultato.getString("enc_value");
				if(encValue!=null && StringUtils.isNotEmpty(encValue)) {
					prop.setValore(encValue);
				}
				else {
					prop.setValore(plainValue);
				}

				lista.add(prop);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppPropList] Errore : " + qe.getMessage(),qe);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(error,con);
		}
	}
	
	/**
	 * Ritorna la lista di Azioni di una  Porta Applicativa
	 */
	protected List<PortaApplicativaAzione> porteAppAzioneList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		int offset;
		int limit;
		int idLista = Liste.PORTE_APPLICATIVE_AZIONI;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));		


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<PortaApplicativaAzione> lista = new ArrayList<PortaApplicativaAzione>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteAppPropList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppPropList] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AZIONI);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.addWhereCondition("id_porta = ?");
			if (!search.equals("")) {
				//query con search
				sqlQueryObject.addWhereLikeCondition("azione", search, true, true);
			}
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AZIONI);
			sqlQueryObject.addSelectField("id_porta");
			sqlQueryObject.addSelectField("azione");
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id_porta = ?");
			
			if (!search.equals("")) { // con search
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
			} 
			
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy("azione");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			PortaApplicativaAzione azione = null;
			while (risultato.next()) {

				azione = new PortaApplicativaAzione();
				
				azione.setId(risultato.getLong("id"));
				azione.setNome(risultato.getString("azione")) ;
				
				String nomePortaDelegante = null;
				azione.setNomePortaDelegante(nomePortaDelegante);
				PortaApplicativaAzioneIdentificazione identificazione = null;
				azione.setIdentificazione(identificazione);
				StatoFunzionalita forceInterfaceBased = StatoFunzionalita.ABILITATO; 
				azione.setForceInterfaceBased(forceInterfaceBased);

				lista.add(azione);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppPropList] Errore : " + qe.getMessage(),qe);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(error,con);
		}
	}

	protected List<ServizioApplicativo> porteAppServizioApplicativoList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteAppServizioApplicativoList";
		int idLista = Liste.PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO;
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
		PreparedStatement stmt1=null;
		ResultSet risultato=null;
		ArrayList<ServizioApplicativo> lista = new ArrayList<ServizioApplicativo>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteAppServizioApplicativoList");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SA+".id_servizio_applicativo="+CostantiDB.SERVIZI_APPLICATIVI+".id");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("id_servizio_applicativo");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SA+".id_servizio_applicativo="+CostantiDB.SERVIZI_APPLICATIVI+".id");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("id_servizio_applicativo");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObject.addSelectField("id_servizio_applicativo");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("id_servizio_applicativo");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			ServizioApplicativo sa = null;
			ResultSet rs1;
			long idServizioApplicativo = 0;
			while (risultato.next()) {
				idServizioApplicativo = risultato.getLong("id_servizio_applicativo");

				sa = new ServizioApplicativo();

				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id = ?");
				queryString = sqlQueryObject.createSQLQuery();

				stmt1 = con.prepareStatement(queryString);
				stmt1.setLong(1, idServizioApplicativo);

				rs1 = stmt1.executeQuery();

				// recupero i dati del servizio applicativo
				if (rs1.next()) {
					sa.setId(idServizioApplicativo);
					sa.setNome(rs1.getString("nome"));
					sa.setDescrizione(rs1.getString("descrizione"));
					sa.setIdSoggetto(rs1.getLong("id_soggetto"));
				} else
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore recuperando i dati del Servizio Applicativo.");

				rs1.close();
				stmt1.close();
				lista.add(sa);

			}
			risultato.close();
			stmt.close();
			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			JDBCUtilities.closeResources(stmt1);
			
			this.driver.closeConnection(error,con);
		}
	}
	
	protected List<PortaApplicativaAutorizzazioneSoggetto> porteAppSoggettoList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteAppSoggettoList";
		int idLista = Liste.PORTE_APPLICATIVE_SOGGETTO;
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
		PreparedStatement stmt1=null;
		ResultSet risultato=null;
		ArrayList<PortaApplicativaAutorizzazioneSoggetto> lista = new ArrayList<PortaApplicativaAutorizzazioneSoggetto>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteAppSoggettoList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			//query con search
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SOGGETTI);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.addWhereCondition("id_porta = ?");
			if (!search.equals("")) {
				sqlQueryObject.addWhereLikeCondition(CostantiDB.PORTE_APPLICATIVE_SOGGETTI+".nome_soggetto", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
			} 
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SOGGETTI);
			sqlQueryObject.addSelectField("id_porta");
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.addWhereCondition("id_porta = ?");
			if (!search.equals("")) {
				sqlQueryObject.addWhereLikeCondition(CostantiDB.PORTE_APPLICATIVE_SOGGETTI+".nome_soggetto", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
			}
			sqlQueryObject.addOrderBy("tipo_soggetto");
			sqlQueryObject.addOrderBy("nome_soggetto");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			PortaApplicativaAutorizzazioneSoggetto paAuthSoggetto = null;
			while (risultato.next()) {
				paAuthSoggetto = new PortaApplicativaAutorizzazioneSoggetto();
				paAuthSoggetto.setNome(risultato.getString("nome_soggetto"));
				paAuthSoggetto.setTipo(risultato.getString("tipo_soggetto"));
				
				lista.add(paAuthSoggetto);
			}
			risultato.close();
			stmt.close();
			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			JDBCUtilities.closeResources(stmt1);
			this.driver.closeConnection(error,con);
		}
	}
	
	protected List<PortaApplicativaAutorizzazioneServizioApplicativo> porteAppServiziApplicativiAutorizzatiList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteAppServiziApplicativiAutorizzatiList";
		int idLista = Liste.PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_AUTORIZZATO;
		String nomeTabella = CostantiDB.PORTE_APPLICATIVE_SA_AUTORIZZATI;
		return _porteAppServiziApplicativiAutorizzatiTokenList(idPortaApplicativa, ricerca,
				nomeMetodo, nomeTabella, idLista);
	}
	
	protected List<PortaApplicativaAutorizzazioneServizioApplicativo> porteAppServiziApplicativiAutorizzatiTokenList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteAppServiziApplicativiAutorizzatiTokenList";
		int idLista = Liste.PORTE_APPLICATIVE_TOKEN_SERVIZIO_APPLICATIVO;
		String nomeTabella = CostantiDB.PORTE_APPLICATIVE_TOKEN_SA;
		return _porteAppServiziApplicativiAutorizzatiTokenList(idPortaApplicativa, ricerca,
				nomeMetodo, nomeTabella, idLista);
	}
	private List<PortaApplicativaAutorizzazioneServizioApplicativo> _porteAppServiziApplicativiAutorizzatiTokenList(long idPortaApplicativa, ISearch ricerca,
			String nomeMetodo, String nomeTabella, int idLista) throws DriverConfigurazioneException {
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
		PreparedStatement stmt1=null;
		ResultSet risultato=null;
		ArrayList<PortaApplicativaAutorizzazioneServizioApplicativo> lista = new ArrayList<PortaApplicativaAutorizzazioneServizioApplicativo>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteAppServiziApplicativiAutorizzatiTokenList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			//query con search
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.addWhereCondition("id_porta = ?");
			if (!search.equals("")) {
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id="+nomeTabella+".id_servizio_applicativo");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".nome", search, true, true);
			}
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("id_porta");
			sqlQueryObject.addSelectField("id_servizio_applicativo");
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "id_soggetto", "idSoggettoProprietarioSA");
			sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI, "tipo_soggetto", "tipoSoggettoProprietarioSA");
			sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI, "nome_soggetto", "nomeSoggettoProprietarioSA");
			sqlQueryObject.addWhereCondition("id_porta = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id="+nomeTabella+".id_servizio_applicativo");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.setANDLogicOperator(true);
			if (!search.equals("")) {
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".nome", search, true, true);
			}
			sqlQueryObject.addOrderBy(CostantiDB.SERVIZI_APPLICATIVI+".nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			PortaApplicativaAutorizzazioneServizioApplicativo paAuthSa = null;
			while (risultato.next()) {
				paAuthSa = new PortaApplicativaAutorizzazioneServizioApplicativo();
				paAuthSa.setNome(risultato.getString("nome"));
				paAuthSa.setTipoSoggettoProprietario(risultato.getString("tipoSoggettoProprietarioSA"));
				paAuthSa.setNomeSoggettoProprietario(risultato.getString("nomeSoggettoProprietarioSA"));
				lista.add(paAuthSa);
			}
			risultato.close();
			stmt.close();
			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			JDBCUtilities.closeResources(stmt1);
			this.driver.closeConnection(error,con);
		}
	}

	protected List<MessageSecurityFlowParameter> porteAppMessageSecurityRequestList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteAppMessageSecurityRequestList";
		int idLista = Liste.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST;

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
		ArrayList<MessageSecurityFlowParameter> lista = new ArrayList<MessageSecurityFlowParameter>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteAppMessageSecurityRequestList");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addSelectField("enc_value");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addSelectField("enc_value");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			MessageSecurityFlowParameter wsreq;
			while (risultato.next()) {

				wsreq = new MessageSecurityFlowParameter();

				wsreq.setId(risultato.getLong("id_porta"));
				wsreq.setNome(risultato.getString("nome"));
				
				String plainValue = risultato.getString("valore");
				String encValue = risultato.getString("enc_value");
				if(encValue!=null && StringUtils.isNotEmpty(encValue)) {
					wsreq.setValore(encValue);
				}
				else {
					wsreq.setValore(plainValue);
				}

				lista.add(wsreq);
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

	protected List<MessageSecurityFlowParameter> porteAppMessageSecurityResponseList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteAppMessageSecurityResponseList";
		int idLista = Liste.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE;
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
		ArrayList<MessageSecurityFlowParameter> lista = new ArrayList<MessageSecurityFlowParameter>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteAppMessageSecurityResponseList");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addSelectField("enc_value");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addSelectField("enc_value");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			MessageSecurityFlowParameter wsresp;
			while (risultato.next()) {

				wsresp = new MessageSecurityFlowParameter();

				wsresp.setId(risultato.getLong("id_porta"));
				wsresp.setNome(risultato.getString("nome"));
				
				String plainValue = risultato.getString("valore");
				String encValue = risultato.getString("enc_value");
				if(encValue!=null && StringUtils.isNotEmpty(encValue)) {
					wsresp.setValore(encValue);
				}
				else {
					wsresp.setValore(plainValue);
				}

				lista.add(wsresp);
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
	
	protected List<CorrelazioneApplicativaElemento> porteApplicativeCorrelazioneApplicativaList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteApplicativeCorrelazioneApplicativaList";
		int idLista = Liste.PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA;
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
		ArrayList<CorrelazioneApplicativaElemento> lista = new ArrayList<CorrelazioneApplicativaElemento>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteApplicativeCorrelazioneApplicativaList");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome_elemento", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_elemento");
				sqlQueryObject.addSelectField("mode_correlazione");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("identificazione_fallita");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome_elemento", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("id");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_elemento");
				sqlQueryObject.addSelectField("mode_correlazione");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("identificazione_fallita");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("id");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			CorrelazioneApplicativaElemento cae = null;

			//long idServizioApplicativo = 0;
			while (risultato.next()) {
				cae = new CorrelazioneApplicativaElemento();
				cae.setId(risultato.getLong("id"));
				cae.setNome(risultato.getString("nome_elemento"));
				cae.setIdentificazione(DriverConfigurazioneDBLib.getEnumCorrelazioneApplicativaRichiestaIdentificazione(risultato.getString("mode_correlazione")));
				cae.setIdentificazioneFallita(DriverConfigurazioneDBLib.getEnumCorrelazioneApplicativaGestioneIdentificazioneFallita(risultato.getString("identificazione_fallita")));
				lista.add(cae);
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



	protected List<CorrelazioneApplicativaRispostaElemento> porteApplicativeCorrelazioneApplicativaRispostaList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteApplicativeCorrelazioneApplicativaRispostaList";
		int idLista = Liste.PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RISPOSTA;
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
		ArrayList<CorrelazioneApplicativaRispostaElemento> lista = new ArrayList<CorrelazioneApplicativaRispostaElemento>();

		if (this.driver.atomica) {
			try {
				con = (Connection) this.driver.getConnectionFromDatasource("porteApplicativeCorrelazioneApplicativaRispostaList");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE_RISPOSTA);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome_elemento", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE_RISPOSTA);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE_RISPOSTA);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_elemento");
				sqlQueryObject.addSelectField("mode_correlazione");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("identificazione_fallita");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome_elemento", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("id");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE_RISPOSTA);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_elemento");
				sqlQueryObject.addSelectField("mode_correlazione");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("identificazione_fallita");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("id");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			CorrelazioneApplicativaRispostaElemento cae = null;

			//long idServizioApplicativo = 0;
			while (risultato.next()) {
				cae = new CorrelazioneApplicativaRispostaElemento();
				cae.setId(risultato.getLong("id"));
				cae.setNome(risultato.getString("nome_elemento"));
				cae.setIdentificazione(DriverConfigurazioneDBLib.getEnumCorrelazioneApplicativaRispostaIdentificazione(risultato.getString("mode_correlazione")));
				cae.setIdentificazioneFallita(DriverConfigurazioneDBLib.getEnumCorrelazioneApplicativaGestioneIdentificazioneFallita(risultato.getString("identificazione_fallita")));
				lista.add(cae);
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
	
	protected List<PortaApplicativa> serviziPorteAppList(String tipoServizio,String nomeServizio, Integer versioneServizio,
			long idServizio, long idSoggettoErogatore, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "serviziPorteAppList";
		int idLista = Liste.CONFIGURAZIONE_EROGAZIONE;
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

		ArrayList<PortaApplicativa> lista = new ArrayList<PortaApplicativa>();

		try {

			if (this.driver.atomica) {
				try {
					con = this.driver.getConnectionFromDatasource(nomeMetodo);

				} catch (Exception e) {
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

				}

			} else
				con = this.driver.globalConnection;

			this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);
			
			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(false, "id_soggetto = ? AND tipo_servizio = ? AND servizio=? AND versione_servizio=? AND id_soggetto_virtuale <= ?",
						"id_soggetto = ? AND id_servizio = ? AND id_soggetto_virtuale <= ?", 
						"id_soggetto_virtuale = ? AND tipo_servizio = ? AND servizio=?  AND versione_servizio=?",
						"id_soggetto_virtuale = ? AND id_servizio = ?");
				sqlQueryObject.addWhereLikeCondition("nome_porta", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(false, "id_soggetto = ? AND tipo_servizio = ? AND servizio=? AND versione_servizio=? AND id_soggetto_virtuale <= ?",
						"id_soggetto = ? AND id_servizio = ? AND id_soggetto_virtuale <= ?", 
						"id_soggetto_virtuale = ? AND tipo_servizio = ? AND servizio=? AND versione_servizio=?",
						"id_soggetto_virtuale = ? AND id_servizio = ?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);

			int index = 1;
			
			stmt.setLong(index++, idSoggettoErogatore);
			stmt.setString(index++, tipoServizio);
			stmt.setString(index++, nomeServizio);
			stmt.setInt(index++, versioneServizio);
			stmt.setLong(index++, 0);

			stmt.setLong(index++, idSoggettoErogatore);
			stmt.setLong(index++, idServizio);
			stmt.setLong(index++, 0);

			stmt.setLong(index++, idSoggettoErogatore);
			stmt.setString(index++, tipoServizio);
			stmt.setString(index++, nomeServizio);
			stmt.setInt(index++, versioneServizio);

			stmt.setLong(index++, idSoggettoErogatore);
			stmt.setLong(index++, idServizio);

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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addSelectField("id_soggetto");
				sqlQueryObject.addSelectField("id_servizio");
				sqlQueryObject.addSelectField("id_soggetto_virtuale");
				sqlQueryObject.addSelectField("nome_porta");
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition(false, "id_soggetto = ? AND tipo_servizio = ? AND servizio=? AND versione_servizio=? AND id_soggetto_virtuale <= ?",
						"id_soggetto = ? AND id_servizio = ? AND id_soggetto_virtuale <= ?", 
						"id_soggetto_virtuale = ? AND tipo_servizio = ? AND servizio=? AND versione_servizio=?",
						"id_soggetto_virtuale = ? AND id_servizio = ?");
				sqlQueryObject.addWhereLikeCondition("nome_porta", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome_porta");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addSelectField("id_soggetto");
				sqlQueryObject.addSelectField("id_servizio");
				sqlQueryObject.addSelectField("id_soggetto_virtuale");
				sqlQueryObject.addSelectField("nome_porta");
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition(false, "id_soggetto = ? AND tipo_servizio = ? AND servizio=? AND versione_servizio=? AND id_soggetto_virtuale <= ?",
						"id_soggetto = ? AND id_servizio = ? AND id_soggetto_virtuale <= ?", 
						"id_soggetto_virtuale = ? AND tipo_servizio = ? AND servizio=? AND versione_servizio=?",
						"id_soggetto_virtuale = ? AND id_servizio = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome_porta");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);

			index = 1;
			
			stmt.setLong(index++, idSoggettoErogatore);
			stmt.setString(index++, tipoServizio);
			stmt.setString(index++, nomeServizio);
			stmt.setInt(index++, versioneServizio);
			stmt.setLong(index++, 0);

			stmt.setLong(index++, idSoggettoErogatore);
			stmt.setLong(index++, idServizio);
			stmt.setLong(index++, 0);

			stmt.setLong(index++, idSoggettoErogatore);
			stmt.setString(index++, tipoServizio);
			stmt.setString(index++, nomeServizio);
			stmt.setInt(index++, versioneServizio);

			stmt.setLong(index++, idSoggettoErogatore);
			stmt.setLong(index++, idServizio);

			risultato = stmt.executeQuery();

			PortaApplicativa pa = null;

			while (risultato.next()) {

				pa = this.porteApplicativeDriver.getPortaApplicativa(risultato.getLong("id"));

				lista.add(pa);

			}

			return lista;

		} catch (Exception se) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(con);
		}
	}
	
	protected List<MtomProcessorFlowParameter> porteApplicativeMTOMRequestList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteApplicativeMTOMRequestList";
		int idLista = Liste.PORTE_APPLICATIVE_MTOM_REQUEST;
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
		ArrayList<MtomProcessorFlowParameter> lista = new ArrayList<MtomProcessorFlowParameter>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MTOM_REQUEST);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MTOM_REQUEST);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MTOM_REQUEST);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("pattern");
				sqlQueryObject.addSelectField("content_type");
				sqlQueryObject.addSelectField("required");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MTOM_REQUEST);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("pattern");
				sqlQueryObject.addSelectField("content_type");
				sqlQueryObject.addSelectField("required");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			MtomProcessorFlowParameter reqPar;
			while (risultato.next()) {

				reqPar = new MtomProcessorFlowParameter();
				
				reqPar.setId(risultato.getLong("id_porta"));
				reqPar.setNome(risultato.getString("nome"));
				reqPar.setPattern(risultato.getString("pattern"));
				reqPar.setContentType(risultato.getString("content_type"));
				int required = risultato.getInt("required");
				boolean isrequired = false;
				if (required == CostantiDB.TRUE)
					isrequired = true;
				reqPar.setRequired(isrequired);

				

				lista.add(reqPar);
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

	protected List<MtomProcessorFlowParameter> porteApplicativeMTOMResponseList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteApplicativeMTOMResponseList";
		int idLista = Liste.PORTE_APPLICATIVE_MTOM_RESPONSE;
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
		ArrayList<MtomProcessorFlowParameter> lista = new ArrayList<MtomProcessorFlowParameter>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MTOM_RESPONSE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MTOM_RESPONSE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MTOM_RESPONSE);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("pattern");
				sqlQueryObject.addSelectField("content_type");
				sqlQueryObject.addSelectField("required");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MTOM_RESPONSE);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("pattern");
				sqlQueryObject.addSelectField("content_type");
				sqlQueryObject.addSelectField("required");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			MtomProcessorFlowParameter resPar;
			while (risultato.next()) {
				
				resPar = new MtomProcessorFlowParameter();
				
				resPar.setId(risultato.getLong("id_porta"));
				resPar.setNome(risultato.getString("nome"));
				resPar.setPattern(risultato.getString("pattern"));
				resPar.setContentType(risultato.getString("content_type"));
				int required = risultato.getInt("required");
				boolean isrequired = false;
				if (required == CostantiDB.TRUE)
					isrequired = true;
				resPar.setRequired(isrequired);

				

				lista.add(resPar);
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
	
	/**
	 * Ritorna la lista di proprieta per la configurazione custom dei connettori multipli di una Porta Applicativa
	 */
	protected List<Proprieta> porteApplicativeConnettoriMultipliConfigPropList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		int offset;
		int limit;
		int idLista = Liste.PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_PROPRIETA;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));		


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Proprieta> lista = new ArrayList<>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteApplicativeConnettoriMultipliConfigPropList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteApplicativeConnettoriMultipliConfigPropList] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_BEHAVIOUR_PROPS);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.addWhereCondition("id_porta = ?");
			
			if (!search.equals("")) {
				//query con search
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
			} 
			
			queryString = sqlQueryObject.createSQLQuery();
			
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_BEHAVIOUR_PROPS);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("id_porta");
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("valore");
			sqlQueryObject.addWhereCondition("id_porta = ?");
			
			if (!search.equals("")) { // con search
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
			} 
			
			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			Proprieta prop = null;
			while (risultato.next()) {

				prop = new Proprieta();

				prop.setId(risultato.getLong("id"));
				prop.setNome(risultato.getString("nome"));
				prop.setValore(risultato.getString("valore"));

				lista.add(prop);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteApplicativeConnettoriMultipliConfigPropList] Errore : " + qe.getMessage(),qe);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(error,con);
		}
	}
	
	/**
	 * Ritorna la lista di proprieta di un connettore multiplo di una Porta Applicativa 
	 */
	protected List<Proprieta> porteApplicativeConnettoriMultipliPropList(long idPaSa, ISearch ricerca) throws DriverConfigurazioneException {
		int offset;
		int limit;
		int idLista = Liste.PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_PROPRIETA;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));		


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Proprieta> lista = new ArrayList<>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteApplicativeAutorizzazioneCustomPropList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppPropList] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA_PROPS);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.addWhereCondition("id_porta = ?");
			
			if (!search.equals("")) {
				//query con search
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
			} 
			
			queryString = sqlQueryObject.createSQLQuery();
			
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPaSa);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA_PROPS);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("id_porta");
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("valore");
			sqlQueryObject.addWhereCondition("id_porta = ?");
			
			if (!search.equals("")) { // con search
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
			} 
			
			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPaSa);
			risultato = stmt.executeQuery();

			Proprieta prop = null;
			while (risultato.next()) {

				prop = new Proprieta();

				prop.setId(risultato.getLong("id"));
				prop.setNome(risultato.getString("nome"));
				prop.setValore(risultato.getString("valore"));

				lista.add(prop);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppPropList] Errore : " + qe.getMessage(),qe);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(error,con);
		}
	}
}
