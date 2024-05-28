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
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.MessageSecurityFlowParameter;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverConfigurazioneDB_porteDelegateSearchDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_porteDelegateSearchDriver {

	private DriverConfigurazioneDB driver = null;
	private DriverConfigurazioneDB_porteDelegateDriver porteDelegateDriver = null;
	
	protected DriverConfigurazioneDB_porteDelegateSearchDriver(DriverConfigurazioneDB driver) {
		this.driver = driver;
		this.porteDelegateDriver = new DriverConfigurazioneDB_porteDelegateDriver(driver);
	}
	
	protected List<String> portaDelegataRuoliList(long idPD, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "portaDelegataRuoliList";
		int idLista = Liste.PORTE_DELEGATE_RUOLI;
		String nomeTabella = CostantiDB.PORTE_DELEGATE_RUOLI;
		return _portaDelegataRuoliList(idPD, ricerca,
				nomeMetodo, nomeTabella, idLista);
	}
	protected List<String> portaDelegataRuoliTokenList(long idPD, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "portaDelegataRuoliTokenList";
		int idLista = Liste.PORTE_DELEGATE_TOKEN_RUOLI;
		String nomeTabella = CostantiDB.PORTE_DELEGATE_TOKEN_RUOLI;
		return _portaDelegataRuoliList(idPD, ricerca,
				nomeMetodo, nomeTabella, idLista);
	}
	private List<String> _portaDelegataRuoliList(long idPD, ISearch ricerca,
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(nomeTabella);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id="+nomeTabella+".id_porta");
				sqlQueryObject.addWhereLikeCondition(nomeTabella+".ruolo", search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(nomeTabella);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id="+nomeTabella+".id_porta");

				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPD);

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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(nomeTabella);
				sqlQueryObject.addSelectField(nomeTabella+".ruolo");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id="+nomeTabella+".id_porta");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(nomeTabella);
				sqlQueryObject.addSelectField(nomeTabella+".ruolo");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id="+nomeTabella+".id_porta");
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(nomeTabella+".ruolo");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPD);

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
	
	
	
	protected List<String> portaDelegataScopeList(long idPD, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "portaDelegataScopeList";
		int idLista = Liste.PORTE_DELEGATE_SCOPE;
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_SCOPE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id="+CostantiDB.PORTE_DELEGATE_SCOPE+".id_porta");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.PORTE_DELEGATE_SCOPE+".scope", search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_SCOPE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id="+CostantiDB.PORTE_DELEGATE_SCOPE+".id_porta");

				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPD);

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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_SCOPE);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE_SCOPE+".scope");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id="+CostantiDB.PORTE_DELEGATE_SCOPE+".id_porta");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.PORTE_DELEGATE_SCOPE+".scope", search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.PORTE_DELEGATE_SCOPE+".scope");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_SCOPE);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE_SCOPE+".scope");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id="+CostantiDB.PORTE_DELEGATE_SCOPE+".id_porta");
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.PORTE_DELEGATE_SCOPE+".scope");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPD);

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
	
	protected List<PortaDelegata> porteDelegateList(long idSoggetto, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteDelegateList";
		int idLista = Liste.PORTE_DELEGATE_BY_SOGGETTO;
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
		ArrayList<PortaDelegata> lista = new ArrayList<PortaDelegata>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteDelegateList");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.addWhereLikeCondition("nome_porta", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggetto);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_porta");
				sqlQueryObject.addSelectField("id_soggetto");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_porta");
				sqlQueryObject.addSelectField("id_soggetto");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome_porta");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggetto);
			risultato = stmt.executeQuery();

			PortaDelegata pd;
			while (risultato.next()) {
				pd = this.porteDelegateDriver.getPortaDelegata(risultato.getLong("id"),con);
				lista.add(pd);
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
	
	
	protected List<Proprieta> porteDelegatePropList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		int offset;
		int limit;
		int idLista = Liste.PORTE_DELEGATE_PROP;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));		


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Proprieta> lista = new ArrayList<Proprieta>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteDelegatePropList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteDelegatePropList] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_PROP);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_PROP);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
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
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteDelegatePropList] Errore : " + qe.getMessage(),qe);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(error,con);
		}
	}
	
	
	
	protected List<Proprieta> porteDelegateAutenticazioneCustomPropList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		int offset;
		int limit;
		int idLista = Liste.PORTE_DELEGATE_PROPRIETA_AUTENTICAZIONE;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));		


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Proprieta> lista = new ArrayList<Proprieta>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteDelegatePropList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteDelegatePropList] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
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
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteDelegatePropList] Errore : " + qe.getMessage(),qe);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(error,con);
		}
	}
	
	protected List<Proprieta> porteDelegateAutorizzazioneCustomPropList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		int offset;
		int limit;
		int idLista = Liste.PORTE_DELEGATE_PROPRIETA_AUTORIZZAZIONE;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));		


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Proprieta> lista = new ArrayList<Proprieta>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteDelegatePropList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteDelegatePropList] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
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
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteDelegatePropList] Errore : " + qe.getMessage(),qe);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(error,con);
		}
	}
	
	protected List<Proprieta> porteDelegateAutorizzazioneContenutoCustomPropList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		int offset;
		int limit;
		int idLista = Liste.PORTE_DELEGATE_PROPRIETA_AUTORIZZAZIONE_CONTENUTO;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));		


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Proprieta> lista = new ArrayList<Proprieta>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteDelegatePropList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteDelegatePropList] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
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
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteDelegatePropList] Errore : " + qe.getMessage(),qe);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(error,con);
		}
	}

	protected List<PortaDelegata> porteDelegateList(String superuser, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteDelegateList";
		int idLista = Liste.PORTE_DELEGATE;
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
		
		this.driver.logDebug("search : " + search);
		this.driver.logDebug("filterProtocollo : " + filterProtocollo);
		this.driver.logDebug("filterProtocolli : " + filterProtocolli);

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<PortaDelegata> lista = new ArrayList<PortaDelegata>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteDelegateList");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				sqlQueryObject.addWhereLikeCondition("nome_porta", search, true, true);
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
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
			stmt = con.prepareStatement(queryString);
			if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser)))
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".id");
				sqlQueryObject.addSelectField("nome_porta");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				sqlQueryObject.addWhereLikeCondition("nome_porta", search, true, true);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".id");
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
			stmt = con.prepareStatement(queryString);
			if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser)))
				stmt.setString(1, superuser);
			risultato = stmt.executeQuery();

			PortaDelegata pd;
			while (risultato.next()) {

				pd = this.porteDelegateDriver.getPortaDelegata(risultato.getLong("id"),con);
				lista.add(pd);
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
	
	
	protected List<ServizioApplicativo> porteDelegateServizioApplicativoList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteDelegateServizioApplicativoList";
		int idLista = Liste.PORTE_DELEGATE_SERVIZIO_APPLICATIVO;
		String nomeTabella = CostantiDB.PORTE_DELEGATE_SA;
		return _porteDelegateServizioApplicativoList(idPortaDelegata, ricerca,
				nomeMetodo, nomeTabella, idLista);
	}
	protected List<ServizioApplicativo> porteDelegateServizioApplicativoTokenList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteDelegateServizioApplicativoTokenList";
		int idLista = Liste.PORTE_DELEGATE_TOKEN_SERVIZIO_APPLICATIVO;
		String nomeTabella = CostantiDB.PORTE_DELEGATE_TOKEN_SA;
		return _porteDelegateServizioApplicativoList(idPortaDelegata, ricerca,
				nomeMetodo, nomeTabella, idLista);
	}
	private List<ServizioApplicativo> _porteDelegateServizioApplicativoList(long idPortaDelegata, ISearch ricerca,
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
		ResultSet rs1=null;
		ArrayList<ServizioApplicativo> lista = new ArrayList<ServizioApplicativo>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteDelegateServizioApplicativoList");
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
				sqlQueryObject.addFromTable(nomeTabella);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereCondition(nomeTabella+".id_servizio_applicativo="+CostantiDB.SERVIZI_APPLICATIVI+".id");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(nomeTabella);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
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
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField(nomeTabella,"id_servizio_applicativo");
				sqlQueryObject.addSelectField(nomeTabella,"id_porta");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereCondition(nomeTabella+".id_servizio_applicativo="+CostantiDB.SERVIZI_APPLICATIVI+".id");
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
				sqlQueryObject.addFromTable(nomeTabella);
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
			stmt.setLong(1, idPortaDelegata);
			risultato = stmt.executeQuery();

			ServizioApplicativo sa = null;

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

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			JDBCUtilities.closeResources(rs1, stmt1);
			
			this.driver.closeConnection(error,con);
		}
	}

	protected List<MessageSecurityFlowParameter> porteDelegateMessageSecurityRequestList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteDelegateMessageSecurityRequestList";
		int idLista = Liste.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST;
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
				con = this.driver.getConnectionFromDatasource("porteDelegateMessageSecurityRequestList");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
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

	protected List<MessageSecurityFlowParameter> porteDelegateMessageSecurityResponseList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteDelegateMessageSecurityResponseList";
		int idLista = Liste.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE;
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
				con = this.driver.getConnectionFromDatasource("porteDelegateMessageSecurityResponseList");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
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

	protected List<CorrelazioneApplicativaElemento> porteDelegateCorrelazioneApplicativaList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteDelegateCorrelazioneApplicativaList";
		int idLista = Liste.PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA;
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
				con = this.driver.getConnectionFromDatasource("porteDelegateCorrelazioneApplicativaList");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome_elemento", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_elemento");
				sqlQueryObject.addSelectField("mode_correlazione");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("identificazione_fallita");
				sqlQueryObject.addSelectField("riuso_id");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_elemento");
				sqlQueryObject.addSelectField("mode_correlazione");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("identificazione_fallita");
				sqlQueryObject.addSelectField("riuso_id");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("id");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
			risultato = stmt.executeQuery();

			CorrelazioneApplicativaElemento cae = null;

			//long idServizioApplicativo = 0;
			while (risultato.next()) {
				cae = new CorrelazioneApplicativaElemento();
				cae.setId(risultato.getLong("id"));
				cae.setNome(risultato.getString("nome_elemento"));
				cae.setIdentificazione(DriverConfigurazioneDBLib.getEnumCorrelazioneApplicativaRichiestaIdentificazione(risultato.getString("mode_correlazione")));
				cae.setIdentificazioneFallita(DriverConfigurazioneDBLib.getEnumCorrelazioneApplicativaGestioneIdentificazioneFallita(risultato.getString("identificazione_fallita")));
				cae.setRiusoIdentificativo(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(risultato.getString("riuso_id")));
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

	protected List<CorrelazioneApplicativaRispostaElemento> porteDelegateCorrelazioneApplicativaRispostaList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteDelegateCorrelazioneApplicativaRispostaList";
		int idLista = Liste.PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RISPOSTA;
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
				con = (Connection) this.driver.getConnectionFromDatasource("porteDelegateCorrelazioneApplicativaRispostaList");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE_RISPOSTA);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome_elemento", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE_RISPOSTA);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE_RISPOSTA);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE_RISPOSTA);
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
			stmt.setLong(1, idPortaDelegata);
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
	
	protected List<PortaDelegata> serviziFruitoriPorteDelegateList(long idSoggetto, String tipoServizio,String nomeServizio,Long idServizio, 
			String tipoSoggettoErogatore, String nomeSoggettoErogatore, Long idSoggettoErogatore, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "serviziFruitoriPorteDelegateList";
		int idLista = Liste.SERVIZI_FRUITORI_PORTE_DELEGATE;
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

		ArrayList<PortaDelegata> lista = new ArrayList<PortaDelegata>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);

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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				if(idSoggettoErogatore!=null && idSoggettoErogatore > 0){
					sqlQueryObject.addWhereCondition(false, "id_soggetto_erogatore=?" , "tipo_soggetto_erogatore=? AND nome_soggetto_erogatore=?");
				}
				else{
					sqlQueryObject.addWhereCondition("tipo_soggetto_erogatore=?");
					sqlQueryObject.addWhereCondition("nome_soggetto_erogatore=?");
				}
				if(idServizio!=null && idServizio > 0){
					sqlQueryObject.addWhereCondition(false, "id_servizio=?" , "tipo_servizio=? AND nome_servizio=?");
				}
				else{
					sqlQueryObject.addWhereCondition("tipo_servizio=?");
					sqlQueryObject.addWhereCondition("nome_servizio=?");
				}
				sqlQueryObject.addWhereLikeCondition("nome_porta", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				if(idSoggettoErogatore!=null && idSoggettoErogatore > 0){
					sqlQueryObject.addWhereCondition(false, "id_soggetto_erogatore=?" , "tipo_soggetto_erogatore=? AND nome_soggetto_erogatore=?");
				}
				else{
					sqlQueryObject.addWhereCondition("tipo_soggetto_erogatore=?");
					sqlQueryObject.addWhereCondition("nome_soggetto_erogatore=?");
				}
				if(idServizio!=null && idServizio > 0){
					sqlQueryObject.addWhereCondition(false, "id_servizio=?" , "tipo_servizio=? AND nome_servizio=?");
				}
				else{
					sqlQueryObject.addWhereCondition("tipo_servizio=?");
					sqlQueryObject.addWhereCondition("nome_servizio=?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);

			int index = 1;
			
			stmt.setLong(index++, idSoggetto);
			
			if(idSoggettoErogatore!=null && idSoggettoErogatore > 0){
				stmt.setLong(index++, idSoggettoErogatore);
			}
			stmt.setString(index++, tipoSoggettoErogatore);
			stmt.setString(index++, nomeSoggettoErogatore);
			
			if(idServizio!=null && idServizio > 0){
				stmt.setLong(index++, idServizio);
			}
			stmt.setString(index++, tipoServizio);
			stmt.setString(index++, nomeServizio);

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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_porta");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				if(idSoggettoErogatore!=null && idSoggettoErogatore > 0){
					sqlQueryObject.addWhereCondition(false, "id_soggetto_erogatore=?" , "tipo_soggetto_erogatore=? AND nome_soggetto_erogatore=?");
				}
				else{
					sqlQueryObject.addWhereCondition("tipo_soggetto_erogatore=?");
					sqlQueryObject.addWhereCondition("nome_soggetto_erogatore=?");
				}
				if(idServizio!=null && idServizio > 0){
					sqlQueryObject.addWhereCondition(false, "id_servizio=?" , "tipo_servizio=? AND nome_servizio=?");
				}
				else{
					sqlQueryObject.addWhereCondition("tipo_servizio=?");
					sqlQueryObject.addWhereCondition("nome_servizio=?");
				}
				sqlQueryObject.addWhereLikeCondition("nome_porta", search, true, true);
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome_porta");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_porta");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				if(idSoggettoErogatore!=null && idSoggettoErogatore > 0){
					sqlQueryObject.addWhereCondition(false, "id_soggetto_erogatore=?" , "tipo_soggetto_erogatore=? AND nome_soggetto_erogatore=?");
				}
				else{
					sqlQueryObject.addWhereCondition("tipo_soggetto_erogatore=?");
					sqlQueryObject.addWhereCondition("nome_soggetto_erogatore=?");
				}
				if(idServizio!=null && idServizio > 0){
					sqlQueryObject.addWhereCondition(false, "id_servizio=?" , "tipo_servizio=? AND nome_servizio=?");
				}
				else{
					sqlQueryObject.addWhereCondition("tipo_servizio=?");
					sqlQueryObject.addWhereCondition("nome_servizio=?");
				}
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome_porta");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);

			index = 1;
			
			stmt.setLong(index++, idSoggetto);
			
			if(idSoggettoErogatore!=null && idSoggettoErogatore > 0){
				stmt.setLong(index++, idSoggettoErogatore);
			}
			stmt.setString(index++, tipoSoggettoErogatore);
			stmt.setString(index++, nomeSoggettoErogatore);
			
			if(idServizio!=null && idServizio > 0){
				stmt.setLong(index++, idServizio);
			}
			stmt.setString(index++, tipoServizio);
			stmt.setString(index++, nomeServizio);

			risultato = stmt.executeQuery();

			PortaDelegata pd = null;

			while (risultato.next()) {

				pd = this.porteDelegateDriver.getPortaDelegata(risultato.getLong("id"));

				lista.add(pd);

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
	
	protected List<PortaDelegata> getPorteDelegateByFruitore(IDSoggetto fruitore,ISearch filters) throws DriverConfigurazioneException{
		return getPorteDelegate(null, fruitore,filters);
	}

	/**
	 * Recupera tutte le porte delegate del soggetto fruitore, relative al servizio idServizio (se diverso da null), erogato dal soggetto erogatore del servizio.
	 * @param idSE L'id del servizio, puo essere null in tal caso si comporta come il metodo getPorteDelegateByFruitore
	 * @return List<PortaDelegata>
	 * @throws DriverConfigurazioneException
	 */
	protected List<PortaDelegata> getPorteDelegate(IDServizio idSE,IDSoggetto fruitore,ISearch filters) throws DriverConfigurazioneException{
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString = "";
		int idLista = Liste.PORTE_DELEGATE;


		int limit = filters.getPageSize(idLista);
		int offset = filters.getIndexIniziale(idLista);
		String search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(filters.getSearchString(idLista)) ? "" : filters.getSearchString(idLista));

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getPorteDelegate(idServizio,fruitore)");
			} catch (Exception e) {
				throw new DriverConfigurazioneException(
						"[DriverConfigurazioneDB::getPorteDelegate] Exception accedendo al datasource :"
								+ e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;
		ArrayList<PortaDelegata> lista = null;
		try {

			IDSoggetto erogatore = null;
			String nomeErogatore = null;
			String tipoErogatore = null;
			String tipoServizio = null;
			String nomeServizio = null;
			Integer versioneServizio = null;

			if(idSE!=null){
				erogatore = idSE.getSoggettoErogatore();
				nomeErogatore = erogatore.getNome();
				tipoErogatore = erogatore.getTipo();
				tipoServizio = idSE.getTipo();
				nomeServizio = idSE.getNome();
				versioneServizio = idSE.getVersione();
			}

			long idSoggettoFruitore = DBUtils.getIdSoggetto(fruitore.getNome(), fruitore.getTipo(), con, this.driver.tipoDB);

			long idSoggettoErogatore =-1;
			long idServizio = -1;
			if(idSE!=null){
				idSoggettoErogatore = DBUtils.getIdSoggetto(erogatore.getNome(), erogatore.getTipo(), con, this.driver.tipoDB);
				idServizio = DBUtils.getIdServizio(nomeServizio, tipoServizio, versioneServizio, erogatore.getNome(), erogatore.getTipo(), con, this.driver.tipoDB);
			}


			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.setANDLogicOperator(true);
			if (!search.equals("")) {
				//query con search
				sqlQueryObject.addWhereLikeCondition("nome_porta", search, true, true);
			} 
			queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();
			if (risultato.next())
				filters.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;


			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("nome_porta");
			sqlQueryObject.addWhereCondition("id_soggetto=?");
			sqlQueryObject.setANDLogicOperator(true);

			if(idSE!=null){
				sqlQueryObject.addWhereCondition(false,"id_servizio=?","tipo_servizio=? AND nome_servizio=?");
				sqlQueryObject.addWhereCondition(false,"id_soggetto_erogatore=?","nome_soggetto_erogatore=? AND tipo_soggetto_erogatore=?");
			}

			if (!search.equals("")) { 
				// con search
				sqlQueryObject.addWhereLikeCondition("nome_porta", search, true, true);				
			}
			//limit and order by
			sqlQueryObject.addOrderBy("nome_porta");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);

			stmt.setLong(1, idSoggettoFruitore);
			if(idSE!=null){
				stmt.setLong(2, idServizio);
				stmt.setString(3, tipoServizio);
				stmt.setString(4, nomeServizio);
				stmt.setLong(5, idSoggettoErogatore);
				stmt.setString(6, nomeErogatore);
				stmt.setString(7, tipoErogatore);
			}

			risultato = stmt.executeQuery();
			lista=new ArrayList<PortaDelegata>();
			while(risultato.next()){
				long id=risultato.getLong("id");
				PortaDelegata pd = this.porteDelegateDriver.getPortaDelegata(id);
				lista.add(pd);
			}

			return lista;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			JDBCUtilities.closeResources(risultato, stmt);
			
			this.driver.closeConnection(con);

		}

	}
	
	protected List<MtomProcessorFlowParameter> porteDelegateMTOMRequestList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteDelegateMTOMRequestList";
		int idLista = Liste.PORTE_DELEGATE_MTOM_REQUEST;
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MTOM_REQUEST);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MTOM_REQUEST);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MTOM_REQUEST);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MTOM_REQUEST);
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
			stmt.setLong(1, idPortaDelegata);
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

	protected List<MtomProcessorFlowParameter> porteDelegateMTOMResponseList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteDelegateMTOMResponseList";
		int idLista = Liste.PORTE_DELEGATE_MTOM_RESPONSE;
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MTOM_RESPONSE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MTOM_RESPONSE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MTOM_RESPONSE);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MTOM_RESPONSE);
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
			stmt.setLong(1, idPortaDelegata);
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
}
