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

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.CanaleConfigurazione;
import org.openspcoop2.core.config.CanaleConfigurazioneNodo;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazioneRegola;
import org.openspcoop2.core.config.IdSoggetto;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.RegistroTipo;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverConfigurazioneDB_configSearchDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_configSearchDriver {

	private DriverConfigurazioneDB driver = null;
	
	protected DriverConfigurazioneDB_configSearchDriver(DriverConfigurazioneDB driver) {
		this.driver = driver;
	}
	
	protected List<Property> systemPropertyList(ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "systemPropertyList";
		int idLista = Liste.SYSTEM_PROPERTIES;

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
		ArrayList<Property> lista = new ArrayList<Property>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("systemPropertyList");
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
				sqlQueryObject.addFromTable(CostantiDB.SYSTEM_PROPERTIES_PDD);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SYSTEM_PROPERTIES_PDD);
				sqlQueryObject.addSelectCountField("*", "cont");
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
				sqlQueryObject.addFromTable(CostantiDB.SYSTEM_PROPERTIES_PDD);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
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
				sqlQueryObject.addFromTable(CostantiDB.SYSTEM_PROPERTIES_PDD);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();

			Property sp;
			while (risultato.next()) {

				sp = new Property();

				sp.setId(risultato.getLong("id"));
				sp.setNome(risultato.getString("nome"));
				sp.setValore(risultato.getString("valore"));

				lista.add(sp);
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
	
	protected List<AccessoRegistroRegistro> registriList(ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "registriList";
		int idLista = Liste.REGISTRI;
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
		ArrayList<AccessoRegistroRegistro> lista = new ArrayList<AccessoRegistroRegistro>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("registriList");
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
				sqlQueryObject.addFromTable(CostantiDB.REGISTRI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.REGISTRI);
				sqlQueryObject.addSelectCountField("*", "cont");
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
				sqlQueryObject.addFromTable(CostantiDB.REGISTRI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("tipo");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.REGISTRI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("tipo");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();

			AccessoRegistroRegistro arr;
			while (risultato.next()) {

				arr = new AccessoRegistroRegistro();

				arr.setId(risultato.getLong("id"));
				arr.setNome(risultato.getString("nome"));
				arr.setTipo(RegistroTipo.toEnumConstant(risultato.getString("tipo")));

				lista.add(arr);
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
	
	protected List<ResponseCachingConfigurazioneRegola> responseCachingConfigurazioneRegolaList(ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "responseCachingConfigurazioneRegolaList";
		int idLista = Liste.CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA;
		int offset;
		int limit;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<ResponseCachingConfigurazioneRegola> lista = new ArrayList<ResponseCachingConfigurazioneRegola>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("responseCachingConfigurazioneRegolaList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE_CACHE_REGOLE);
			sqlQueryObject.addSelectCountField("id", "cont");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE_CACHE_REGOLE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addOrderBy("id");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();

			List<Long> idLong = new ArrayList<>();
			while (risultato.next()) { 
				idLong.add(risultato.getLong("id"));
			}
			risultato.close();
			stmt.close();
			
			if(!idLong.isEmpty()) {
				
				for (Long idLongRegola : idLong) {
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE_CACHE_REGOLE);
					sqlQueryObject.addSelectField("id");
					sqlQueryObject.addSelectField("status_min");
					sqlQueryObject.addSelectField("status_max");
					sqlQueryObject.addSelectField("fault");
					sqlQueryObject.addSelectField("cache_seconds");
					sqlQueryObject.addWhereCondition("id=?");
					queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					stmt.setLong(1, idLongRegola);
					risultato = stmt.executeQuery();
					
					if(risultato.next()) {
						ResponseCachingConfigurazioneRegola regola = new ResponseCachingConfigurazioneRegola();
						
						regola.setId(risultato.getLong("id"));
						int status_min = risultato.getInt("status_min");
						int status_max = risultato.getInt("status_max");
						if(status_min>0) {
							regola.setReturnCodeMin(status_min);
						}
						if(status_max>0) {
							regola.setReturnCodeMax(status_max);
						}

						int fault = risultato.getInt("fault");
						if(CostantiDB.TRUE == fault) {
							regola.setFault(true);
						}
						else if(CostantiDB.FALSE == fault) {
							regola.setFault(false);
						}
						
						int cacheSeconds = risultato.getInt("cache_seconds");
						if(cacheSeconds>0) {
							regola.setCacheTimeoutSeconds(cacheSeconds);
						}

						lista.add(regola);
					}
					
					risultato.close();
					stmt.close();
				}
				
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
	
	protected List<ConfigurazioneUrlInvocazioneRegola> proxyPassConfigurazioneRegolaList(ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "proxyPassConfigurazioneRegolaList";
		int idLista = Liste.CONFIGURAZIONE_PROXY_PASS_REGOLA;
		int offset;
		int limit;
		String queryString;
		String search;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<ConfigurazioneUrlInvocazioneRegola> lista = new ArrayList<ConfigurazioneUrlInvocazioneRegola>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("responseCachingConfigurazioneRegolaList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_URL_REGOLE);
			sqlQueryObject.addSelectCountField("id", "cont");
			
			if (!search.equals("")) {
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
			}
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_URL_REGOLE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("nome");            
			sqlQueryObject.addSelectField("posizione");       
			sqlQueryObject.addSelectField("stato");           
			sqlQueryObject.addSelectField("descrizione");     
			sqlQueryObject.addSelectField("regexpr");         
			sqlQueryObject.addSelectField("regola");          
			sqlQueryObject.addSelectField("contesto_esterno");
			sqlQueryObject.addSelectField("base_url");        
			sqlQueryObject.addSelectField("protocollo");      
			sqlQueryObject.addSelectField("ruolo");           
			sqlQueryObject.addSelectField("service_binding"); 
			sqlQueryObject.addSelectField("tipo_soggetto");   
			sqlQueryObject.addSelectField("nome_soggetto");
			
			if (!search.equals("")) {
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
			}
			sqlQueryObject.addOrderBy("posizione");
			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();

			
			while (risultato.next()) { 
				ConfigurazioneUrlInvocazioneRegola regola = new ConfigurazioneUrlInvocazioneRegola();
				regola.setId(risultato.getLong("id"));
				regola.setNome(risultato.getString("nome"));
				regola.setPosizione(risultato.getInt("posizione"));
				regola.setStato(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(risultato.getString("stato")));
				regola.setDescrizione(risultato.getString("descrizione"));
				if(risultato.getInt("regexpr") == CostantiDB.TRUE) {
					regola.setRegexpr(true);
				}else {
					regola.setRegexpr(false);
				}
				regola.setRegola(risultato.getString("regola"));
				// Fix stringa vuota in Oracle, impostato dalla console e non accettato da Oracle che lo traduce in null e fa schiantare per via del NOT NULL sul db
				String s = risultato.getString("contesto_esterno");
				if(CostantiConfigurazione.REGOLA_PROXY_PASS_CONTESTO_VUOTO.equals(s)) {
					s = "";
				}
				regola.setContestoEsterno(s);
				regola.setBaseUrl(risultato.getString("base_url"));
				regola.setProtocollo(risultato.getString("protocollo"));
				regola.setRuolo(DriverConfigurazioneDBLib.getEnumRuoloContesto(risultato.getString("ruolo")));
				regola.setServiceBinding(DriverConfigurazioneDBLib.getEnumServiceBinding(risultato.getString("service_binding")));
				String tipoSoggetto = risultato.getString("tipo_soggetto");
				String nomeSoggetto = risultato.getString("nome_soggetto");
				if(tipoSoggetto!=null && !"".equals(tipoSoggetto) && nomeSoggetto!=null && !"".equals(nomeSoggetto)) {
					regola.setSoggetto(new IdSoggetto(new IDSoggetto(tipoSoggetto, nomeSoggetto)));
				}
				
				lista.add(regola);
			
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

			this.driver.closeConnection(error,con);
		}
	}
	
	protected List<CanaleConfigurazione> canaleConfigurazioneList(ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "canaleConfigurazioneList";
		int idLista = Liste.CONFIGURAZIONE_CANALI;
		int offset;
		int limit;
		String queryString;
		String search;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<CanaleConfigurazione> lista = new ArrayList<CanaleConfigurazione>();

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

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE_CANALI);
			sqlQueryObject.addSelectCountField("id", "cont");
			
			if (!search.equals("")) {
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
			}
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE_CANALI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("nome");            
			sqlQueryObject.addSelectField("descrizione");     
			sqlQueryObject.addSelectField("canale_default");         
			
			if (!search.equals("")) {
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
			}
			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();

			
			while (risultato.next()) { 
				CanaleConfigurazione canale = new CanaleConfigurazione();
				canale.setId(risultato.getLong("id"));
				canale.setNome(risultato.getString("nome"));
				canale.setDescrizione(risultato.getString("descrizione"));
				int v = risultato.getInt("canale_default");
				if(v == CostantiDB.TRUE) {
					canale.setCanaleDefault(true);
				}
				else {
					canale.setCanaleDefault(false);
				}
				
				lista.add(canale);
			
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

			this.driver.closeConnection(error,con);
		}
	}
	
	protected List<CanaleConfigurazioneNodo> canaleNodoConfigurazioneList(ISearch ricerca) throws DriverConfigurazioneException { 
		String nomeMetodo = "canaleNodoConfigurazioneList";
		int idLista = Liste.CONFIGURAZIONE_CANALI_NODI;
		int offset;
		int limit;
		String queryString;
		String search;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<CanaleConfigurazioneNodo> lista = new ArrayList<CanaleConfigurazioneNodo>();

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

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE_CANALI_NODI);
			sqlQueryObject.addSelectCountField("id", "cont");
			
			if (!search.equals("")) {
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
			}
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE_CANALI_NODI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("nome");            
			sqlQueryObject.addSelectField("descrizione");     
			sqlQueryObject.addSelectField("canali");         
			
			if (!search.equals("")) {
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
			}
			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();

			
			while (risultato.next()) { 
				CanaleConfigurazioneNodo canaleNodo = new CanaleConfigurazioneNodo();
				canaleNodo.setId(risultato.getLong("id"));
				canaleNodo.setNome(risultato.getString("nome"));
				canaleNodo.setDescrizione(risultato.getString("descrizione"));
				List<String> l = DBUtils.convertToList(risultato.getString("canali"));
				canaleNodo.setCanaleList(l);
				
				lista.add(canaleNodo);
			
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

			this.driver.closeConnection(error,con);
		}
	}
}
