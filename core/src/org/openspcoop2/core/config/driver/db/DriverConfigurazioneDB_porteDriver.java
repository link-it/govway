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
import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.CorsConfigurazioneHeaders;
import org.openspcoop2.core.config.CorsConfigurazioneMethods;
import org.openspcoop2.core.config.CorsConfigurazioneOrigin;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneControl;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneHashGenerator;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaCacheDigestQueryParameter;
import org.openspcoop2.core.config.constants.TipoGestioneCORS;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverConfigurazioneDB_porteDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_porteDriver {

	private DriverConfigurazioneDB driver = null;
	
	protected DriverConfigurazioneDB_porteDriver(DriverConfigurazioneDB driver) {
		this.driver = driver;
	}
	
	protected List<ResponseCachingConfigurazioneRegola> portaApplicativaResponseCachingConfigurazioneRegolaList(long idPA, ISearch ricerca) throws DriverConfigurazioneException {
		int idLista = Liste.PORTE_APPLICATIVE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA;
		String nomeMetodo = "portaApplicativaResponseCachingConfigurazioneRegolaList";
		boolean delegata = false;
		return getEngineResponseCachingConfigurazioneRegolaList(idPA, ricerca, idLista, nomeMetodo, delegata);
	}
	
	protected List<ResponseCachingConfigurazioneRegola> portaDelegataResponseCachingConfigurazioneRegolaList(long idPD, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "portaDelegataResponseCachingConfigurazioneRegolaList";
		int idLista = Liste.PORTE_DELEGATE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA;
		boolean delegata = true;
		return getEngineResponseCachingConfigurazioneRegolaList(idPD, ricerca, idLista, nomeMetodo, delegata);
	}
	
	private List<ResponseCachingConfigurazioneRegola> getEngineResponseCachingConfigurazioneRegolaList(long idPA, ISearch ricerca, int idLista, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		int offset; 
		int limit;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_CACHE_REGOLE : CostantiDB.PORTE_APPLICATIVE_CACHE_REGOLE;

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

		List<ResponseCachingConfigurazioneRegola> lista = new ArrayList<ResponseCachingConfigurazioneRegola>();
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addSelectCountField(nomeTabella+".id", "cont");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
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
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(nomeTabella+".id");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);

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
					sqlQueryObject.addFromTable(nomeTabella);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition(nomeTabella+".id=?");
					sqlQueryObject.setANDLogicOperator(true);
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

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
		
		return lista;
	}
	
	protected boolean existsPortaApplicativaResponseCachingConfigurazioneRegola(long idPA, Integer statusMin, Integer statusMax, boolean fault) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaApplicativaResponseCachingConfigurazioneRegola";
		boolean delegata = false;
		return _existsResponseCachingConfigurazioneRegola(idPA, statusMin, statusMax, fault, nomeMetodo, delegata);
	}
	
	protected boolean existsPortaDelegataResponseCachingConfigurazioneRegola(long idPA, Integer statusMin, Integer statusMax, boolean fault) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaDelegataResponseCachingConfigurazioneRegola";
		boolean delegata = true;
		return _existsResponseCachingConfigurazioneRegola(idPA, statusMin, statusMax, fault, nomeMetodo, delegata);
	}
	
	private boolean _existsResponseCachingConfigurazioneRegola(long idPA, Integer statusMin, Integer statusMax, boolean fault,String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_CACHE_REGOLE : CostantiDB.PORTE_APPLICATIVE_CACHE_REGOLE;

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

			int count = 0;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addSelectCountField(nomeTabella+".id", "cont");
			sqlQueryObject.setANDLogicOperator(true);
			
			sqlQueryObject.addWhereCondition("id_porta = ?");
			if(statusMin != null) {
				sqlQueryObject.addWhereCondition("status_min = ?");
			} else {
				sqlQueryObject.addWhereIsNullCondition("status_min");
			}
			
			if(statusMax != null) {
				sqlQueryObject.addWhereCondition("status_max = ?");
			} else {
				sqlQueryObject.addWhereIsNullCondition("status_max");
			}
			
			//if(fault) {
			//	sqlQueryObject.addWhereCondition("fault = ?");
			//} else {
			sqlQueryObject.addWhereCondition("fault = ?");
			//}
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPA);
			if(statusMin != null)
				stmt.setInt(parameterIndex ++, statusMin);
			if(statusMax != null)
				stmt.setInt(parameterIndex ++, statusMax);
			if(fault) {
				stmt.setInt(parameterIndex ++, CostantiDB.TRUE);
			} else {
				stmt.setInt(parameterIndex ++, CostantiDB.FALSE);
			}
			
			risultato = stmt.executeQuery();
			if (risultato.next())
				count = risultato.getInt(1);
			risultato.close();
			stmt.close();

			return count > 0;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}
	
	protected void readConfigurazioneCors(CorsConfigurazione configurazione, ResultSet rs) throws SQLException {
		
		String corsStato = rs.getString("cors_stato");
		if(corsStato!=null && !"".equals(corsStato)) {
			configurazione.setStato(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(corsStato));
		}
		
		if(StatoFunzionalita.ABILITATO.equals(configurazione.getStato())) {
			
			String corsTipo = rs.getString("cors_tipo");
			if(corsTipo!=null && !"".equals(corsTipo)) {
				configurazione.setTipo(DriverConfigurazioneDB_LIB.getEnumTipoGestioneCORS(corsTipo));
			}
			
			if(TipoGestioneCORS.GATEWAY.equals(configurazione.getTipo())) {
				
				String corsAllAllowOrigins = rs.getString("cors_all_allow_origins");
				if(corsAllAllowOrigins!=null && !"".equals(corsAllAllowOrigins)) {
					configurazione.setAccessControlAllAllowOrigins(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(corsAllAllowOrigins));
				}
				if(StatoFunzionalita.DISABILITATO.equals(configurazione.getAccessControlAllAllowOrigins())) {
					List<String> l = DBUtils.convertToList(rs.getString("cors_allow_origins"));
					if(!l.isEmpty()) {
						configurazione.setAccessControlAllowOrigins(new CorsConfigurazioneOrigin());
					}
					for (String v : l) {
						configurazione.getAccessControlAllowOrigins().addOrigin(v);
					}
				}
			
				String corsAllowCredentials = rs.getString("cors_allow_credentials");
				if(corsAllowCredentials!=null && !"".equals(corsAllowCredentials)) {
					configurazione.setAccessControlAllowCredentials(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(corsAllowCredentials));
				}
				
				int corsAllowMaxAge = rs.getInt("cors_allow_max_age");
				if(CostantiDB.TRUE == corsAllowMaxAge) {
					int corsAllowMaxAgeSeconds = rs.getInt("cors_allow_max_age_seconds");
					configurazione.setAccessControlMaxAge(corsAllowMaxAgeSeconds);
				}
				
				String corsAllAllowHeader = rs.getString("cors_all_allow_headers");
				if(corsAllAllowHeader!=null && !"".equals(corsAllAllowHeader)) {
					configurazione.setAccessControlAllAllowHeaders(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(corsAllAllowHeader));
				}
				if(StatoFunzionalita.DISABILITATO.equals(configurazione.getAccessControlAllAllowHeaders())) {
					List<String> l = DBUtils.convertToList(rs.getString("cors_allow_headers"));
					if(!l.isEmpty()) {
						configurazione.setAccessControlAllowHeaders(new CorsConfigurazioneHeaders());
					}
					for (String v : l) {
						configurazione.getAccessControlAllowHeaders().addHeader(v);
					}
				}
				
				String corsAllAllowMethods = rs.getString("cors_all_allow_methods");
				if(corsAllAllowMethods!=null && !"".equals(corsAllAllowMethods)) {
					configurazione.setAccessControlAllAllowMethods(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(corsAllAllowMethods));
				}
				if(StatoFunzionalita.DISABILITATO.equals(configurazione.getAccessControlAllAllowMethods())) {
					List<String> l = DBUtils.convertToList(rs.getString("cors_allow_methods"));
					if(!l.isEmpty()) {
						configurazione.setAccessControlAllowMethods(new CorsConfigurazioneMethods());
					}
					for (String v : l) {
						configurazione.getAccessControlAllowMethods().addMethod(v);
					}
				}
				
				List<String> l = DBUtils.convertToList(rs.getString("cors_allow_expose_headers"));
				if(!l.isEmpty()) {
					configurazione.setAccessControlExposeHeaders(new CorsConfigurazioneHeaders());
				}
				for (String v : l) {
					configurazione.getAccessControlExposeHeaders().addHeader(v);
				}
			}
			
		}
		
	}
	
	protected void readResponseCaching(Long idPorta, boolean config, boolean portaDelegata, ResponseCachingConfigurazione configurazione, ResultSet rs, Connection con) throws Exception {
		
		String responseCacheStato = rs.getString("response_cache_stato");
		if(responseCacheStato!=null && !"".equals(responseCacheStato)) {
			configurazione.setStato(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(responseCacheStato));
		}
		
		if(StatoFunzionalita.ABILITATO.equals(configurazione.getStato())) {
	
			int responseCacheSeconds = rs.getInt("response_cache_seconds");
			if(responseCacheSeconds>0) {
				configurazione.setCacheTimeoutSeconds(responseCacheSeconds);
			}
			
			long responseCacheMaxMsgBytes = rs.getLong("response_cache_max_msg_size");
			if(responseCacheMaxMsgBytes>0) {
				configurazione.setMaxMessageSize(responseCacheMaxMsgBytes);
			}
			
			configurazione.setHashGenerator(new ResponseCachingConfigurazioneHashGenerator());
			
			String responseCacheHashUrl = rs.getString("response_cache_hash_url");
			if(responseCacheHashUrl!=null && !"".equals(responseCacheHashUrl)) {
				configurazione.getHashGenerator().setRequestUri(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(responseCacheHashUrl));
			}
			
			String responseCacheHashQuery = rs.getString("response_cache_hash_query");
			if(responseCacheHashQuery!=null && !"".equals(responseCacheHashQuery)) {
				configurazione.getHashGenerator().setQueryParameters(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalitaCacheDigestQueryParameter(responseCacheHashQuery));
			}
			
			if(StatoFunzionalitaCacheDigestQueryParameter.SELEZIONE_PUNTUALE.equals(configurazione.getHashGenerator().getQueryParameters())) {
				List<String> l = DBUtils.convertToList(rs.getString("response_cache_hash_query_list"));
				for (String v : l) {
					configurazione.getHashGenerator().addQueryParameter(v);
				}
			}
			
			String responseCacheHashHeaders = rs.getString("response_cache_hash_headers");
			if(responseCacheHashHeaders!=null && !"".equals(responseCacheHashHeaders)) {
				configurazione.getHashGenerator().setHeaders(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(responseCacheHashHeaders));
			}
			
			if(StatoFunzionalita.ABILITATO.equals(configurazione.getHashGenerator().getHeaders())) {
				List<String> l = DBUtils.convertToList(rs.getString("response_cache_hash_hdr_list"));
				for (String v : l) {
					configurazione.getHashGenerator().addHeader(v);
				}
			}
			
			String responseCacheHashPayload = rs.getString("response_cache_hash_payload");
			if(responseCacheHashPayload!=null && !"".equals(responseCacheHashPayload)) {
				configurazione.getHashGenerator().setPayload(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(responseCacheHashPayload));
			}
			
			configurazione.setControl(new ResponseCachingConfigurazioneControl());
			
			int response_cache_control_nocache = rs.getInt("response_cache_control_nocache");
			if(CostantiDB.TRUE == response_cache_control_nocache) {
				configurazione.getControl().setNoCache(true);
			}
			else if(CostantiDB.FALSE == response_cache_control_nocache) {
				configurazione.getControl().setNoCache(false);
			}
			
			int response_cache_control_maxage = rs.getInt("response_cache_control_maxage");
			if(CostantiDB.TRUE == response_cache_control_maxage) {
				configurazione.getControl().setMaxAge(true);
			}
			else if(CostantiDB.FALSE == response_cache_control_maxage) {
				configurazione.getControl().setMaxAge(false);
			}
			
			int response_cache_control_nostore = rs.getInt("response_cache_control_nostore");
			if(CostantiDB.TRUE == response_cache_control_nostore) {
				configurazione.getControl().setNoStore(true);
			}
			else if(CostantiDB.FALSE == response_cache_control_nostore) {
				configurazione.getControl().setNoStore(false);
			}
			
			PreparedStatement stmRegole = null;
			ResultSet rsRegole = null;
			try {
				String nomeTabella = null;
				if(config) {
					nomeTabella = CostantiDB.CONFIGURAZIONE_CACHE_REGOLE;
				}
				else {
					nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_CACHE_REGOLE : CostantiDB.PORTE_APPLICATIVE_CACHE_REGOLE;
				}
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(nomeTabella);
				sqlQueryObject.addSelectField("*");
				if(!config) {
					sqlQueryObject.addWhereCondition("id_porta=?");
				}
				String sqlQuery = sqlQueryObject.createSQLQuery();
				stmRegole = con.prepareStatement(sqlQuery);
				if(!config) {
					stmRegole.setLong(1, idPorta);
				}
		
				this.driver.logDebug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPorta));
				rsRegole = stmRegole.executeQuery();
		
				while (rsRegole.next()) {
				
					ResponseCachingConfigurazioneRegola regola = new ResponseCachingConfigurazioneRegola();
					
					regola.setId(rsRegole.getLong("id"));
					int status_min = rsRegole.getInt("status_min");
					int status_max = rsRegole.getInt("status_max");
					if(status_min>0) {
						regola.setReturnCodeMin(status_min);
					}
					if(status_max>0) {
						regola.setReturnCodeMax(status_max);
					}

					int fault = rsRegole.getInt("fault");
					if(CostantiDB.TRUE == fault) {
						regola.setFault(true);
					}
					else if(CostantiDB.FALSE == fault) {
						regola.setFault(false);
					}
					
					int cacheSeconds = rsRegole.getInt("cache_seconds");
					if(cacheSeconds>0) {
						regola.setCacheTimeoutSeconds(cacheSeconds);
					}
					
					configurazione.addRegola(regola);
					
				}
				
				
			}finally {
				JDBCUtilities.closeResources(rsRegole, stmRegole);
			}
		}

	}
}
