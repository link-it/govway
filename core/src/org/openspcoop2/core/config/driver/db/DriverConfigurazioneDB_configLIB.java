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

import static org.openspcoop2.core.constants.CostantiDB.CREATE;
import static org.openspcoop2.core.constants.CostantiDB.DELETE;
import static org.openspcoop2.core.constants.CostantiDB.UPDATE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.IExtendedInfo;
import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoDatiAttributeAuthority;
import org.openspcoop2.core.config.AccessoDatiAutenticazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoDatiGestioneToken;
import org.openspcoop2.core.config.AccessoDatiKeystore;
import org.openspcoop2.core.config.AccessoDatiRichieste;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.Attachments;
import org.openspcoop2.core.config.Cache;
import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.ConfigurazioneGeneraleHandler;
import org.openspcoop2.core.config.ConfigurazioneMultitenant;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazioneRegola;
import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.Dump;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.IndirizzoRisposta;
import org.openspcoop2.core.config.InoltroBusteNonRiscontrate;
import org.openspcoop2.core.config.IntegrationManager;
import org.openspcoop2.core.config.MessaggiDiagnostici;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.config.OpenspcoopSorgenteDati;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneGenerale;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola;
import org.openspcoop2.core.config.Risposte;
import org.openspcoop2.core.config.StatoServiziPdd;
import org.openspcoop2.core.config.StatoServiziPddIntegrationManager;
import org.openspcoop2.core.config.StatoServiziPddPortaApplicativa;
import org.openspcoop2.core.config.StatoServiziPddPortaDelegata;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.Transazioni;
import org.openspcoop2.core.config.ValidazioneBuste;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaCacheDigestQueryParameter;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.core.constants.CRUDType;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKey;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKeyJDBCType;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKeyObject;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * DriverConfigurazioneDB_configLIB
 * 
 * @author Stefano Corallo - corallo@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_configLIB {


	public static long CRUDAccessoRegistro(int type, AccessoRegistroRegistro registro, Connection con) throws DriverConfigurazioneException {
		if (registro == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoRegistro] Il servizio non puo essere NULL");
		PreparedStatement updateStmt = null;
		String updateQuery = "";
		PreparedStatement selectStmt = null;
		String selectQuery = "";
		ResultSet selectRS = null;

		long idRegistro = 0;
		int n = 0;
		String nome = registro.getNome();
		String location = registro.getLocation();
		String tipo = registro.getTipo().toString();
		String user = registro.getUser();
		String password = registro.getPassword();

		try {
			switch (type) {
			case CREATE:
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.REGISTRI);
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("location", "?");
				sqlQueryObject.addInsertField("tipo", "?");
				sqlQueryObject.addInsertField("utente", "?");
				sqlQueryObject.addInsertField("password", "?");
				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setString(1, nome);
				updateStmt.setString(2, location);
				updateStmt.setString(3, tipo);
				updateStmt.setString(4, user);
				updateStmt.setString(5, password);

				DriverConfigurazioneDBLib.logDebug("eseguo query: " + DBUtils.formatSQLString(updateQuery, nome, location, tipo, user, password));

				n = updateStmt.executeUpdate();

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.REGISTRI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("nome = ?");
				sqlQueryObject.addWhereCondition("location = ?");
				sqlQueryObject.setANDLogicOperator(true);
				selectQuery = sqlQueryObject.createSQLQuery();
				selectStmt = con.prepareStatement(selectQuery);
				selectStmt.setString(1, nome);
				selectStmt.setString(2, location);
				selectRS = selectStmt.executeQuery();
				if (selectRS.next()) {
					idRegistro = selectRS.getLong("id");
					registro.setId(idRegistro);
				}

				break;
			case UPDATE:

				if (registro.getId() == null || registro.getId() <= 0)
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoRegistro(UPDATE)] L'id del Servizio e' necessario.");
				idRegistro = registro.getId();

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.ROUTING);
				sqlQueryObject.addUpdateField("nome", "?");
				sqlQueryObject.addUpdateField("location", "?");
				sqlQueryObject.addUpdateField("tipo", "?");
				sqlQueryObject.addUpdateField("utente", "?");
				sqlQueryObject.addUpdateField("password", "?");
				sqlQueryObject.addWhereCondition("id=?");
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setString(1, registro.getNome());
				updateStmt.setString(2, registro.getLocation());
				updateStmt.setString(3, registro.getTipo().toString());
				updateStmt.setString(4, registro.getUser());
				updateStmt.setString(5, registro.getPassword());
				updateStmt.setLong(6, idRegistro);

				DriverConfigurazioneDBLib.logDebug("eseguo query: " + DBUtils.formatSQLString(updateQuery, nome, location, tipo, user, password, idRegistro));
				n = updateStmt.executeUpdate();

				break;
			case DELETE:
				if (registro.getId() == null || registro.getId() <= 0)
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoRegistro(DELETE)] L'id del Servizio e' necessario.");
				idRegistro = registro.getId();

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.REGISTRI);
				sqlQueryObject.addWhereCondition("id=?");
				String sqlQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(sqlQuery);
				updateStmt.setLong(1, idRegistro);
				DriverConfigurazioneDBLib.logDebug("eseguo query: " + DBUtils.formatSQLString(updateQuery, idRegistro));
				n=updateStmt.executeUpdate();
				updateStmt.close();

				break;
			}

			if (type == CostantiDB.CREATE)
				return idRegistro;
			else
				return n;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoRegistro] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoRegistro] Exception [" + se.getMessage() + "].",se);
		} finally {

			JDBCUtilities.closeResources(selectRS, selectStmt);
			JDBCUtilities.closeResources(updateStmt);

		}

	}

	public static long CRUDAccessoRegistro(int type, AccessoRegistro registro, Connection con) throws DriverConfigurazioneException {
		if (registro == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoRegistro] Il registro non può essere NULL");
		PreparedStatement updateStmt = null;
		String updateQuery = "";
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;

		long idRegistro = 0;
		int n = 0;
		Cache arc = registro.getCache();
		String statoCache = "disabilitato";
		String dimensionecache = null;
		String algoritmocache = null;
		String idlecache = null;
		String lifecache = null;
		if (arc != null) {
			statoCache = "abilitato";
			dimensionecache = arc.getDimensione();
			if(arc.getAlgoritmo()!=null){
				algoritmocache = arc.getAlgoritmo().toString();
			}
			idlecache = arc.getItemIdleTime();
			lifecache = arc.getItemLifeSecond();
		}

		try {
			switch (type) {
			case CREATE:
			case UPDATE:

				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.CONFIGURAZIONE);
				sqlQueryObject.addUpdateField("statocache", "?");
				sqlQueryObject.addUpdateField("dimensionecache", "?");
				sqlQueryObject.addUpdateField("algoritmocache", "?");
				sqlQueryObject.addUpdateField("idlecache", "?");
				sqlQueryObject.addUpdateField("lifecache", "?");
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setString(1, statoCache);
				updateStmt.setString(2, dimensionecache);
				updateStmt.setString(3, algoritmocache);
				updateStmt.setString(4, idlecache);
				updateStmt.setString(5, lifecache);

				DriverConfigurazioneDBLib.logDebug("eseguo query :" + DBUtils.formatSQLString(updateQuery, statoCache, dimensionecache, algoritmocache, idlecache, lifecache));

				n = updateStmt.executeUpdate();
				updateStmt.close();

				// Elimino i registri e li ricreo
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.REGISTRI);
				String sqlQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(sqlQuery);
				DriverConfigurazioneDBLib.logDebug("eseguo query: " + DBUtils.formatSQLString(updateQuery));
				int risultato = updateStmt.executeUpdate();
				DriverConfigurazioneDBLib.logDebug("eseguo query risultato["+risultato+"]: " + DBUtils.formatSQLString(updateQuery));
				updateStmt.close();

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.REGISTRI);
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("location", "?");
				sqlQueryObject.addInsertField("tipo", "?");
				sqlQueryObject.addInsertField("utente", "?");
				sqlQueryObject.addInsertField("password", "?");
				updateQuery = sqlQueryObject.createSQLInsert();
				for (int i = 0; i < registro.sizeRegistroList(); i++) {
					updateStmt = con.prepareStatement(updateQuery);
					AccessoRegistroRegistro arr = registro.getRegistro(i);
					String nome = arr.getNome();
					String location = arr.getLocation();
					String tipo = arr.getTipo().toString();
					String utente = arr.getUser();
					String password = arr.getPassword();

					updateStmt.setString(1, nome);
					updateStmt.setString(2, location);
					updateStmt.setString(3, tipo);
					updateStmt.setString(4, utente);
					updateStmt.setString(5, password);
					DriverConfigurazioneDBLib.logDebug("eseguo query INSERT INTO " + CostantiDB.REGISTRI + "(nome, location, tipo, utente, password) VALUES ("+
							nome+", "+location+", "+tipo+", "+utente+", "+password+")");
					updateStmt.executeUpdate();
					updateStmt.close();
				}

				break;
			case DELETE:
				// non rimuovo nulla in quanto la tabella configurazione
				// contiene solo una riga con i valori
				// che vanno modificati con la update
				break;
			}

			if (type == CostantiDB.CREATE)
				return idRegistro;
			else
				return n;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoRegistro] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoRegistro] Exception [" + se.getMessage() + "].",se);
		} finally {

			JDBCUtilities.closeResources(selectRS, selectStmt);
			JDBCUtilities.closeResources(updateStmt);
			
		}

	}
	
	
	
	public static long CRUDAccessoConfigurazione(int type, AccessoConfigurazione accessoConfigurazione, Connection con) throws DriverConfigurazioneException {
		if (accessoConfigurazione == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoConfigurazione] Parametro accessoConfigurazione non può essere NULL");
		PreparedStatement updateStmt = null;
		String updateQuery = "";

		int n = 0;
		Cache cache = accessoConfigurazione.getCache();
		String statoCache = "disabilitato";
		String dimensionecache = null;
		String algoritmocache = null;
		String idlecache = null;
		String lifecache = null;
		if (cache != null) {
			statoCache = "abilitato";
			dimensionecache = cache.getDimensione();
			if(cache.getAlgoritmo()!=null){
				algoritmocache = cache.getAlgoritmo().toString();
			}
			idlecache = cache.getItemIdleTime();
			lifecache = cache.getItemLifeSecond();
		}

		try {
			switch (type) {
			case CREATE:
			case UPDATE:

				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.CONFIGURAZIONE);
				sqlQueryObject.addUpdateField("config_statocache", "?");
				sqlQueryObject.addUpdateField("config_dimensionecache", "?");
				sqlQueryObject.addUpdateField("config_algoritmocache", "?");
				sqlQueryObject.addUpdateField("config_idlecache", "?");
				sqlQueryObject.addUpdateField("config_lifecache", "?");
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setString(1, statoCache);
				updateStmt.setString(2, dimensionecache);
				updateStmt.setString(3, algoritmocache);
				updateStmt.setString(4, idlecache);
				updateStmt.setString(5, lifecache);

				DriverConfigurazioneDBLib.logDebug("eseguo query :" + DBUtils.formatSQLString(updateQuery, statoCache, dimensionecache, algoritmocache, idlecache, lifecache));

				n = updateStmt.executeUpdate();
				updateStmt.close();

				break;
			case DELETE:
				// non rimuovo nulla in quanto la tabella configurazione
				// contiene solo una riga con i valori
				// che vanno modificati con la update
				break;
			}

			return n;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoConfigurazione] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoConfigurazione] Exception [" + se.getMessage() + "].",se);
		} finally {
			JDBCUtilities.closeResources(updateStmt);
		}

	}
	
	
	
	public static long CRUDAccessoDatiAutorizzazione(int type, AccessoDatiAutorizzazione accessoDatiAutorizzazione, Connection con) throws DriverConfigurazioneException {
		if (accessoDatiAutorizzazione == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoDatiAutorizzazione] Parametro accessoDatiAutorizzazione non può essere NULL");
		PreparedStatement updateStmt = null;
		String updateQuery = "";

		int n = 0;
		Cache cache = accessoDatiAutorizzazione.getCache();
		String statoCache = "disabilitato";
		String dimensionecache = null;
		String algoritmocache = null;
		String idlecache = null;
		String lifecache = null;
		if (cache != null) {
			statoCache = "abilitato";
			dimensionecache = cache.getDimensione();
			if(cache.getAlgoritmo()!=null){
				algoritmocache = cache.getAlgoritmo().toString();
			}
			idlecache = cache.getItemIdleTime();
			lifecache = cache.getItemLifeSecond();
		}

		try {
			switch (type) {
			case CREATE:
			case UPDATE:

				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.CONFIGURAZIONE);
				sqlQueryObject.addUpdateField("auth_statocache", "?");
				sqlQueryObject.addUpdateField("auth_dimensionecache", "?");
				sqlQueryObject.addUpdateField("auth_algoritmocache", "?");
				sqlQueryObject.addUpdateField("auth_idlecache", "?");
				sqlQueryObject.addUpdateField("auth_lifecache", "?");
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setString(1, statoCache);
				updateStmt.setString(2, dimensionecache);
				updateStmt.setString(3, algoritmocache);
				updateStmt.setString(4, idlecache);
				updateStmt.setString(5, lifecache);

				DriverConfigurazioneDBLib.logDebug("eseguo query :" + DBUtils.formatSQLString(updateQuery, statoCache, dimensionecache, algoritmocache, idlecache, lifecache));

				n = updateStmt.executeUpdate();
				updateStmt.close();

				break;
			case DELETE:
				// non rimuovo nulla in quanto la tabella configurazione
				// contiene solo una riga con i valori
				// che vanno modificati con la update
				break;
			}

			return n;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoDatiAutorizzazione] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoDatiAutorizzazione] Exception [" + se.getMessage() + "].",se);
		} finally {
			JDBCUtilities.closeResources(updateStmt);
		}

	}
	
	
	public static void CRUDServiziPdD(int type, StatoServiziPdd statoServiziPdD, Connection con) throws DriverConfigurazioneException {
		if (statoServiziPdD == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDServiziPdD] Le configurazioni del servizio non possono essere NULL");
		PreparedStatement updateStmt = null;
		String updateQuery = "";
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		

		try {
			switch (type) {
			case CREATE:
			case UPDATE:

				// Elimino le configurazioni e le ricreo
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_PDD_FILTRI);
				String sqlQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(sqlQuery);
				DriverConfigurazioneDBLib.logDebug("eseguo query: " + DBUtils.formatSQLString(updateQuery));
				int risultato = updateStmt.executeUpdate();
				DriverConfigurazioneDBLib.logDebug("eseguo query risultato["+risultato+"]: " + DBUtils.formatSQLString(updateQuery));
				updateStmt.close();

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_PDD);
				sqlQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(sqlQuery);
				DriverConfigurazioneDBLib.logDebug("eseguo query: " + DBUtils.formatSQLString(updateQuery));
				risultato = updateStmt.executeUpdate();
				DriverConfigurazioneDBLib.logDebug("eseguo query risultato["+risultato+"]: " + DBUtils.formatSQLString(updateQuery));
				updateStmt.close();
				
				// Ricreo
				if(statoServiziPdD.getPortaDelegata()!=null){
				
					StatoServiziPddPortaDelegata sPD = statoServiziPdD.getPortaDelegata();
					
					int stato = CostantiDB.TRUE;
					if(sPD.getStato()!=null){
						stato = CostantiConfigurazione.DISABILITATO.equals(sPD.getStato()) ? CostantiDB.FALSE : CostantiDB.TRUE;
					}
					
					DriverConfigurazioneDB_configLIB.registraComponentePdD(CostantiDB.COMPONENTE_SERVIZIO_PD, stato, con, sPD.getFiltroAbilitazioneList(), sPD.getFiltroDisabilitazioneList());
					
				}
				if(statoServiziPdD.getPortaApplicativa()!=null){
					
					StatoServiziPddPortaApplicativa sPA = statoServiziPdD.getPortaApplicativa();
					
					int stato = CostantiDB.TRUE;
					if(sPA.getStato()!=null){
						stato = CostantiConfigurazione.DISABILITATO.equals(sPA.getStato()) ? CostantiDB.FALSE : CostantiDB.TRUE;
					}
					
					DriverConfigurazioneDB_configLIB.registraComponentePdD(CostantiDB.COMPONENTE_SERVIZIO_PA, stato, con, sPA.getFiltroAbilitazioneList(), sPA.getFiltroDisabilitazioneList());
					
				}
				if(statoServiziPdD.getIntegrationManager()!=null){
					
					StatoServiziPddIntegrationManager sIM = statoServiziPdD.getIntegrationManager();
					
					int stato = CostantiDB.TRUE;
					if(sIM.getStato()!=null){
						stato = CostantiConfigurazione.DISABILITATO.equals(sIM.getStato()) ? CostantiDB.FALSE : CostantiDB.TRUE;
					}
					
					DriverConfigurazioneDB_configLIB.registraComponentePdD(CostantiDB.COMPONENTE_SERVIZIO_IM, stato, con, null, null);
					
				}
				

				break;
			case DELETE:
				// non rimuovo nulla in quanto la tabella configurazione
				// contiene solo una riga con i valori
				// che vanno modificati con la update
				break;
			}


		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoRegistro] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoRegistro] Exception [" + se.getMessage() + "].",se);
		} finally {

			JDBCUtilities.closeResources(selectRS, selectStmt);
			JDBCUtilities.closeResources(updateStmt);
			
		}

	}

	private static void registraComponentePdD(String componente,int stato,Connection con,
			List<TipoFiltroAbilitazioneServizi> abilitazioni,
			List<TipoFiltroAbilitazioneServizi> disabilitazioni) throws Exception{
		PreparedStatement updateStmt = null;
		String updateQuery = "";
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		

		try {
			
			// registro componente
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
			sqlQueryObject.addInsertTable(CostantiDB.SERVIZI_PDD);
			sqlQueryObject.addInsertField("componente", "?");
			sqlQueryObject.addInsertField("stato", "?");
			updateQuery = sqlQueryObject.createSQLInsert();
			updateStmt = con.prepareStatement(updateQuery);
			DriverConfigurazioneDBLib.logDebug("eseguo query INSERT INTO " + CostantiDB.SERVIZI_PDD + "(componente, stato) VALUES ('"+
					componente+"', "+stato+")");
			updateStmt.setString(1, componente);
			updateStmt.setInt(2, stato);
			updateStmt.executeUpdate();
			updateStmt.close();
			
			// recuper id del componente
			long idComponente = -1;
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_PDD);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("componente=?");
			updateQuery = sqlQueryObject.createSQLQuery();
			selectStmt = con.prepareStatement(updateQuery);
			DriverConfigurazioneDBLib.logDebug("eseguo query ["+updateQuery+"] per il componente ["+componente+"]");
			selectStmt.setString(1, componente);
			selectRS = selectStmt.executeQuery();
			if(selectRS.next()){
				idComponente = selectRS.getLong("id");
			}else{
				throw new Exception("Query ["+updateQuery+"] per il componente ["+componente+"] non ha ritornato risultati");
			}
			selectRS.close();
			selectStmt.close();
			
			// registro i filtri
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
			sqlQueryObject.addInsertTable(CostantiDB.SERVIZI_PDD_FILTRI);
			sqlQueryObject.addInsertField("id_servizio_pdd", "?");
			sqlQueryObject.addInsertField("tipo_filtro", "?");
			sqlQueryObject.addInsertField("tipo_soggetto_fruitore", "?");
			sqlQueryObject.addInsertField("soggetto_fruitore", "?");
			sqlQueryObject.addInsertField("identificativo_porta_fruitore", "?");
			sqlQueryObject.addInsertField("tipo_soggetto_erogatore", "?");
			sqlQueryObject.addInsertField("soggetto_erogatore", "?");
			sqlQueryObject.addInsertField("identificativo_porta_erogatore", "?");
			sqlQueryObject.addInsertField("tipo_servizio", "?");
			sqlQueryObject.addInsertField("servizio", "?");
			sqlQueryObject.addInsertField("versione_servizio", "?");
			sqlQueryObject.addInsertField("azione", "?");
			updateQuery = sqlQueryObject.createSQLInsert();
			
			if(abilitazioni!=null){
				for (TipoFiltroAbilitazioneServizi filtro : abilitazioni) {
			
					updateStmt = con.prepareStatement(updateQuery);
					int index = 1;
					
					updateStmt.setLong(index++, idComponente);
					updateStmt.setString(index++,CostantiDB.TIPO_FILTRO_ABILITAZIONE_SERVIZIO_PDD);
					
					updateStmt.setString(index++,filtro.getTipoSoggettoFruitore());
					updateStmt.setString(index++,filtro.getSoggettoFruitore());
					updateStmt.setString(index++,filtro.getIdentificativoPortaFruitore());
					
					updateStmt.setString(index++,filtro.getTipoSoggettoErogatore());
					updateStmt.setString(index++,filtro.getSoggettoErogatore());
					updateStmt.setString(index++,filtro.getIdentificativoPortaErogatore());
					
					updateStmt.setString(index++,filtro.getTipoServizio());
					updateStmt.setString(index++,filtro.getServizio());
					if(filtro.getVersioneServizio()!=null){
						updateStmt.setInt(index++,filtro.getVersioneServizio());
					}
					else{
						updateStmt.setNull(index++, java.sql.Types.INTEGER);
					}
					
					updateStmt.setString(index++,filtro.getAzione());
					
					updateStmt.executeUpdate();
					updateStmt.close();
					
				}
			}
			
			if(disabilitazioni!=null){
				for (TipoFiltroAbilitazioneServizi filtro : disabilitazioni) {
			
					updateStmt = con.prepareStatement(updateQuery);
					int index = 1;
					
					updateStmt.setLong(index++, idComponente);
					updateStmt.setString(index++,CostantiDB.TIPO_FILTRO_DISABILITAZIONE_SERVIZIO_PDD);
					
					updateStmt.setString(index++,filtro.getTipoSoggettoFruitore());
					updateStmt.setString(index++,filtro.getSoggettoFruitore());
					updateStmt.setString(index++,filtro.getIdentificativoPortaFruitore());
					
					updateStmt.setString(index++,filtro.getTipoSoggettoErogatore());
					updateStmt.setString(index++,filtro.getSoggettoErogatore());
					updateStmt.setString(index++,filtro.getIdentificativoPortaErogatore());
					
					updateStmt.setString(index++,filtro.getTipoServizio());
					updateStmt.setString(index++,filtro.getServizio());
					if(filtro.getVersioneServizio()!=null){
						updateStmt.setInt(index++,filtro.getVersioneServizio());
					}
					else{
						updateStmt.setNull(index++, java.sql.Types.INTEGER);
					}
					
					updateStmt.setString(index++,filtro.getAzione());
					
					updateStmt.executeUpdate();
					updateStmt.close();
					
				}
			}
			 
		} finally {

			JDBCUtilities.closeResources(selectRS, selectStmt);
			JDBCUtilities.closeResources(updateStmt);
			
		}
	}
	
	
	
	public static void CRUDSystemPropertiesPdD(int type, SystemProperties systemProperties, Connection con) throws DriverConfigurazioneException {
		if (systemProperties == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDSystemPropertiesPdD] Le configurazioni per le system properties non possono essere NULL");
		PreparedStatement updateStmt = null;
		String updateQuery = "";
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		

		try {
			
			// Elimino le configurazioni e le ricreo per insert e update
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SYSTEM_PROPERTIES_PDD);
			String sqlQuery = sqlQueryObject.createSQLDelete();
			updateStmt = con.prepareStatement(sqlQuery);
			DriverConfigurazioneDBLib.logDebug("eseguo query: " + DBUtils.formatSQLString(updateQuery));
			int risultato = updateStmt.executeUpdate();
			DriverConfigurazioneDBLib.logDebug("eseguo query risultato["+risultato+"]: " + DBUtils.formatSQLString(updateQuery));
			updateStmt.close();
			
			switch (type) {
			case CREATE:
			case UPDATE:
		
				for (int i = 0; i < systemProperties.sizeSystemPropertyList(); i++) {
				
					Property sp = systemProperties.getSystemProperty(i);
					String nome = sp.getNome();
					String valore = sp.getValore();
					
					// Riga
					// registro componente
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.SYSTEM_PROPERTIES_PDD);
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);
					DriverConfigurazioneDBLib.logDebug("eseguo query INSERT INTO " + CostantiDB.SYSTEM_PROPERTIES_PDD + "(nome, valore) VALUES ('"+
							nome+"', "+valore+")");
					updateStmt.setString(1, nome);
					updateStmt.setString(2, valore);
					updateStmt.executeUpdate();
					updateStmt.close();
					
					// recuper id del componente
					long idComponente = -1;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.SYSTEM_PROPERTIES_PDD);
					sqlQueryObject.addSelectField("id");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQueryObject.addWhereCondition("nome=?");
					sqlQueryObject.addWhereCondition("valore=?");
					updateQuery = sqlQueryObject.createSQLQuery();
					selectStmt = con.prepareStatement(updateQuery);
					DriverConfigurazioneDBLib.logDebug("eseguo query ["+updateQuery+"] per la prop nome["+nome+"] valore["+valore+"]");
					selectStmt.setString(1, nome);
					selectStmt.setString(2, valore);
					selectRS = selectStmt.executeQuery();
					if(selectRS.next()){
						idComponente = selectRS.getLong("id");
					}else{
						throw new Exception("Query ["+updateQuery+"] per la prop nome["+nome+"] valore["+valore+"] non ha ritornato risultati");
					}
					selectRS.close();
					selectStmt.close();
					
					sp.setId(idComponente);
				}

				break;
			case DELETE:
				// non rimuovo in quanto gia fatto sopra.
				break;
			}


		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoRegistro] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoRegistro] Exception [" + se.getMessage() + "].",se);
		} finally {

			JDBCUtilities.closeResources(selectRS, selectStmt);
			JDBCUtilities.closeResources(updateStmt);
			
		}

	}
	
	
	
	
	public static void CRUDGenericProperties(int type, GenericProperties genericProperties, Connection con) throws DriverConfigurazioneException {
		if (genericProperties == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDGenericProperties] Le configurazioni per le generic properties non possono essere NULL");
		PreparedStatement updateStmt = null;
		String updateQuery = "";
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		

		try {
			
			if(genericProperties.getNome()==null) {
				throw new DriverConfigurazioneException("Nome non fornito");
			}
			if(genericProperties.getTipologia()==null) {
				throw new DriverConfigurazioneException("Tipologia non fornita");
			}
			
			// Recupero id generic properties
			long idParent = -1;
			if(type == CostantiDB.UPDATE || type == CostantiDB.DELETE) {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.CONFIG_GENERIC_PROPERTIES);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("nome=?");
				sqlQueryObject.addWhereCondition("tipologia=?");
				sqlQueryObject.setANDLogicOperator(true);
				String sqlQuery = sqlQueryObject.createSQLQuery();
				selectStmt = con.prepareStatement(sqlQuery);
				selectStmt.setString(1, genericProperties.getNome());
				selectStmt.setString(2, genericProperties.getTipologia());
				selectRS = selectStmt.executeQuery();
				
				if(selectRS.next()) {
					idParent = selectRS.getLong("id");
				}
				selectRS.close();
				selectStmt.close();
				
				if(idParent<=0) {
					throw new DriverConfigurazioneException("Configuration Property non trovato con nome ["+genericProperties.getNome()+"] e tipologia ["+genericProperties.getTipologia()+"]");
				}
				
				// Elimino anche le configurazioni (nell'update le ricreo)
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIG_GENERIC_PROPERTY);
				sqlQueryObject.addWhereCondition("id_props=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idParent);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIG_GENERIC_PROPERTIES);
				sqlQueryObject.addWhereCondition("id=?");
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idParent);
				updateStmt.executeUpdate();
				updateStmt.close();
			}

			String utenteRichiedente = null;
			Timestamp dataCreazione = null;
			if(type == CostantiDB.CREATE || type == CostantiDB.UPDATE) {
				if(genericProperties.getProprietaOggetto()!=null && genericProperties.getProprietaOggetto().getUtenteRichiedente()!=null) {
					utenteRichiedente = genericProperties.getProprietaOggetto().getUtenteRichiedente();
				}
				
				if(genericProperties.getProprietaOggetto()!=null && genericProperties.getProprietaOggetto().getDataCreazione()!=null) {
					dataCreazione = new Timestamp(genericProperties.getProprietaOggetto().getDataCreazione().getTime());
				}
				else {
					dataCreazione = DateManager.getTimestamp();
				}
			}
			
			String utenteUltimaModifica = null;
			Timestamp dataUltimaModifica = null;
			if(type == CostantiDB.UPDATE) {
				if(genericProperties.getProprietaOggetto()!=null && genericProperties.getProprietaOggetto().getUtenteUltimaModifica()!=null) {
					utenteUltimaModifica = genericProperties.getProprietaOggetto().getUtenteUltimaModifica();
				}
				
				if(genericProperties.getProprietaOggetto()!=null && genericProperties.getProprietaOggetto().getDataUltimaModifica()!=null) {
					dataUltimaModifica = new Timestamp(genericProperties.getProprietaOggetto().getDataUltimaModifica().getTime());
				}
				else {
					dataUltimaModifica = DateManager.getTimestamp();
				}
			}

			switch (type) {
			case CREATE:
			case UPDATE:
		
				// insert

				List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<>();
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome", genericProperties.getNome() , InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("descrizione", genericProperties.getDescrizione() , InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipologia", genericProperties.getTipologia() , InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo", genericProperties.getTipo() , InsertAndGeneratedKeyJDBCType.STRING) );
				
				if(type == CostantiDB.CREATE || type == CostantiDB.UPDATE) {
					if(utenteRichiedente!=null) {
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.PROPRIETA_OGGETTO_UTENTE_RICHIEDENTE, utenteRichiedente , InsertAndGeneratedKeyJDBCType.STRING) );
					}
					if(dataCreazione!=null) {
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.PROPRIETA_OGGETTO_DATA_CREAZIONE, dataCreazione , InsertAndGeneratedKeyJDBCType.TIMESTAMP) );
					}
				}
				if(type == CostantiDB.UPDATE) {
					if(utenteUltimaModifica!=null) {
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.PROPRIETA_OGGETTO_UTENTE_ULTIMA_MODIFICA, utenteUltimaModifica , InsertAndGeneratedKeyJDBCType.STRING) );
					}
					if(dataUltimaModifica!=null) {
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.PROPRIETA_OGGETTO_DATA_ULTIMA_MODIFICA, dataUltimaModifica , InsertAndGeneratedKeyJDBCType.TIMESTAMP) );
					}
				}
				
				long idProperties = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDBLib.tipoDB), 
						new CustomKeyGeneratorObject(CostantiDB.CONFIG_GENERIC_PROPERTIES, CostantiDB.CONFIG_GENERIC_PROPERTIES_COLUMN_ID, 
								CostantiDB.CONFIG_GENERIC_PROPERTIES_SEQUENCE, CostantiDB.CONFIG_GENERIC_PROPERTIES_TABLE_FOR_ID),
						listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
				if(idProperties<=0){
					throw new DriverConfigurazioneException("ID (Generic Properties) autoincrementale non ottenuto");
				}

				for(int l=0; l<genericProperties.sizePropertyList();l++){
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.CONFIG_GENERIC_PROPERTY);
					sqlQueryObject.addInsertField("id_props", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idProperties);
					updateStmt.setString(2, genericProperties.getProperty(l).getNome());
					updateStmt.setString(3, genericProperties.getProperty(l).getValore());
					updateStmt.executeUpdate();
					updateStmt.close();
				}

				break;
			case DELETE:
				// non rimuovo in quanto gia fatto sopra.
				break;
			}


		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDGenericProperties] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDGenericProperties] Exception [" + se.getMessage() + "].",se);
		} finally {

			JDBCUtilities.closeResources(selectRS, selectStmt);
			JDBCUtilities.closeResources(updateStmt);
			
		}

	}
	

	private static void createUrlInvocazioneRegolaEngine(ConfigurazioneUrlInvocazioneRegola regola, Connection con) throws SQLQueryObjectException, SQLException {
		PreparedStatement updateStmt = null;
		

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
			sqlQueryObject.addInsertTable(CostantiDB.CONFIG_URL_REGOLE);
			sqlQueryObject.addInsertField("nome", "?");
			sqlQueryObject.addInsertField("posizione", "?");
			sqlQueryObject.addInsertField("stato", "?");
			sqlQueryObject.addInsertField("descrizione", "?");
			sqlQueryObject.addInsertField("regexpr", "?");
			sqlQueryObject.addInsertField("regola", "?");
			sqlQueryObject.addInsertField("contesto_esterno", "?");
			sqlQueryObject.addInsertField("base_url", "?");
			sqlQueryObject.addInsertField("protocollo", "?");
			sqlQueryObject.addInsertField("ruolo", "?");
			sqlQueryObject.addInsertField("service_binding", "?");
			sqlQueryObject.addInsertField("tipo_soggetto", "?");
			sqlQueryObject.addInsertField("nome_soggetto", "?");
			String updateQuery = sqlQueryObject.createSQLInsert();
			updateStmt = con.prepareStatement(updateQuery);
			int indexP = 1;
			updateStmt.setString(indexP++, regola.getNome());
			updateStmt.setInt(indexP++, regola.getPosizione());
			updateStmt.setString(indexP++, DriverConfigurazioneDBLib.getValue(regola.getStato()));
			updateStmt.setString(indexP++, regola.getDescrizione());
			updateStmt.setInt(indexP++, regola.isRegexpr() ? CostantiDB.TRUE : CostantiDB.FALSE);
			updateStmt.setString(indexP++, regola.getRegola());
			// Fix stringa vuota in Oracle, impostato dalla console e non accettato da Oracle che lo traduce in null e fa schiantare per via del NOT NULL sul db
			String s = regola.getContestoEsterno();
			if("".equals(s)) {
				s = CostantiConfigurazione.REGOLA_PROXY_PASS_CONTESTO_VUOTO;
			}
			updateStmt.setString(indexP++, s);
			updateStmt.setString(indexP++, regola.getBaseUrl());
			updateStmt.setString(indexP++, regola.getProtocollo());
			updateStmt.setString(indexP++, DriverConfigurazioneDBLib.getValue(regola.getRuolo()));
			updateStmt.setString(indexP++, DriverConfigurazioneDBLib.getValue(regola.getServiceBinding()));
			updateStmt.setString(indexP++, regola.getSoggetto()!=null ? regola.getSoggetto().getTipo() : null);
			updateStmt.setString(indexP++, regola.getSoggetto()!=null ? regola.getSoggetto().getNome() : null);
			updateStmt.executeUpdate();
			updateStmt.close();
			
		} finally {
			JDBCUtilities.closeResources(updateStmt);
		}
	}
	
	public static void CRUDUrlInvocazioneRegola(int type, ConfigurazioneUrlInvocazioneRegola regola, Connection con) throws DriverConfigurazioneException {
		if (regola == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDUrlInvocazioneRegola] La regola non può essere NULL");
		PreparedStatement updateStmt = null;
		String updateQuery = "";
		

		try {
			
			if(regola.getNome()==null) {
				throw new DriverConfigurazioneException("Nome non fornito");
			}
			
			// Recupero id generic properties
			long idParent = -1;
			if(type == CostantiDB.UPDATE || type == CostantiDB.DELETE) {
				
				String oldNome = regola.getNome();
				if(type == CostantiDB.UPDATE && regola.getOldNome()!=null) {
					oldNome = regola.getOldNome();
				}
				
				idParent = DBUtils.getUrlInvocazioneRegola(oldNome, con, DriverConfigurazioneDBLib.tipoDB);
				if(idParent<=0) {
					throw new DriverConfigurazioneException("Regola con nome '"+regola.getNome()+"' non trovata");
				}

				// delete
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIG_URL_REGOLE);
				sqlQueryObject.addWhereCondition("id=?");
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idParent);
				updateStmt.executeUpdate();
				updateStmt.close();
			}
			

			switch (type) {
			case CREATE:
			case UPDATE:
		
				// insert

				createUrlInvocazioneRegolaEngine(regola, con);

				break;
			case DELETE:
				// non rimuovo in quanto gia fatto sopra.
				break;
			default:
				break;
			}


		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDRegistroPlugin] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDRegistroPlugin] Exception [" + se.getMessage() + "].",se);
		} finally {
			JDBCUtilities.closeResources(updateStmt);
		}

	}
	
	
	
	
	public static long CRUDConfigurazioneGenerale(int type, Configurazione config, Connection con) throws DriverConfigurazioneException {
		
		if(config.sizeExtendedInfoList()>0 && 
				config.getRoutingTable()==null && 
				config.getAccessoRegistro()==null &&
				config.getAccessoConfigurazione()==null && 
				config.getAccessoDatiAutorizzazione()==null &&
				config.getAccessoDatiAutenticazione()==null && 
				config.getMultitenant()==null &&
				config.getUrlInvocazione()==null &&
				config.getValidazioneBuste()==null && 
				config.getValidazioneContenutiApplicativi()==null &&
				config.getIndirizzoRisposta()==null &&	
				config.getAttachments()==null &&
				config.getRisposte()==null &&
				config.getInoltroBusteNonRiscontrate()==null && 
				config.getMessaggiDiagnostici()==null && 
				config.getTracciamento()==null &&
				config.getTransazioni()==null &&
				config.getDump()==null &&		
				config.getGestioneErrore()==null && 
				config.getIntegrationManager()==null &&
				config.getStatoServiziPdd()==null &&
				config.getSystemProperties()==null && 
				(config.getGenericPropertiesList()==null || config.getGenericPropertiesList().isEmpty()) &&
				config.getGestioneCors()==null && 
				config.getResponseCaching()==null &&
				config.getGestioneCanali()==null &&
				config.getRegistroPlugins()==null &&
				config.getConfigurazioneHandler()==null) {
						
			// caso speciale extended info
			ExtendedInfoManager extInfoManager = ExtendedInfoManager.getInstance();
			IExtendedInfo extInfoConfigurazioneDriver = extInfoManager.newInstanceExtendedInfoConfigurazione();
			if(extInfoConfigurazioneDriver!=null){
			
				try{
					CRUDType crudType = null;
					switch (type) {
					case CREATE:
						crudType = CRUDType.CREATE;
						break;
					case UPDATE:
						crudType = CRUDType.UPDATE;
						break;
					case DELETE:
						crudType = CRUDType.DELETE;
						break;
					default:
						break;
					}
					
					switch (type) {
					case CREATE:
					case UPDATE:
						if(config.sizeExtendedInfoList()>0){
							for(int l=0; l<config.sizeExtendedInfoList();l++){
								extInfoConfigurazioneDriver.deleteExtendedInfo(con, DriverConfigurazioneDBLib.log,  config, config.getExtendedInfo(l), crudType);
								extInfoConfigurazioneDriver.createExtendedInfo(con, DriverConfigurazioneDBLib.log,  config, config.getExtendedInfo(l), crudType);
							}
						}
						break;
					case DELETE:
						if(config.sizeExtendedInfoList()>0){
							for(int l=0; l<config.sizeExtendedInfoList();l++){
								extInfoConfigurazioneDriver.deleteExtendedInfo(con, DriverConfigurazioneDBLib.log,  config, config.getExtendedInfo(l), crudType);
							}
						}
						break;
					default:
						break;
					}
				}catch (Exception se) {
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConfigurazioneGenerale-Extended] Exception [" + se.getMessage() + "].",se);
				} 
				
			}
			
			return -1;
			
		}
		else{
			
			return engineCRUDConfigurazioneGenerale(type, config, con);
			
		}
		
	}

	@SuppressWarnings("deprecation")
	private static DumpConfigurazione getDumpConfigurazioneDeprecated(Dump dump) {
		return dump.getConfigurazione();
	}
	
	private static long engineCRUDConfigurazioneGenerale(int type, Configurazione config, Connection con) throws DriverConfigurazioneException {
		PreparedStatement updateStmt = null;
		String updateQuery = "";
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		int n = 0;
		long idConfigurazione = 0;

		IndirizzoRisposta indirizzoPerRisposta = config.getIndirizzoRisposta();
		InoltroBusteNonRiscontrate inoltroBusteNonRiscontrate = config.getInoltroBusteNonRiscontrate();
		IntegrationManager integrationManager = config.getIntegrationManager();
		MessaggiDiagnostici messaggiDiagnostici = config.getMessaggiDiagnostici();
		Risposte risposte = config.getRisposte();
		ValidazioneBuste validazioneBuste = config.getValidazioneBuste();
		AccessoRegistro car = config.getAccessoRegistro();
		AccessoConfigurazione aConfig = config.getAccessoConfigurazione();
		AccessoDatiAutorizzazione aDatiAuthz = config.getAccessoDatiAutorizzazione();
		AccessoDatiAutenticazione aDatiAuthn = config.getAccessoDatiAutenticazione();
		AccessoDatiGestioneToken aDatiGestioneToken = config.getAccessoDatiGestioneToken();
		AccessoDatiAttributeAuthority aDatiAttributeAuthority = config.getAccessoDatiAttributeAuthority();
		AccessoDatiKeystore aDatiKeystore = config.getAccessoDatiKeystore();
		AccessoDatiRichieste aDatiRichieste = config.getAccessoDatiRichieste();
		Attachments att = config.getAttachments();

		ConfigurazioneMultitenant multitenant = config.getMultitenant();
		
		CorsConfigurazione corsConfigurazione = config.getGestioneCors();
		String corsStato = null;
		String corsTipo = null; 
		String corsAllAllowOrigins = null; 
		String corsAllAllowMethods = null; 
		String corsAllAllowHeaders = null; 
		String corsAllowCredentials = null; 
		int corsAllowMaxAge = CostantiDB.FALSE;
		Integer corsAllowMaxAgeSeconds = null;
		String corsAllowOrigins = null; 
		String corsAllowHeaders = null; 
		String corsAllowMethods = null; 
		String corsAllowExposeHeaders = null; 
		if(corsConfigurazione!=null) {
			corsStato = DriverConfigurazioneDBLib.getValue(corsConfigurazione.getStato());
			corsTipo = DriverConfigurazioneDBLib.getValue(corsConfigurazione.getTipo());
			corsAllAllowOrigins = DriverConfigurazioneDBLib.getValue(corsConfigurazione.getAccessControlAllAllowOrigins());
			corsAllAllowMethods = DriverConfigurazioneDBLib.getValue(corsConfigurazione.getAccessControlAllAllowMethods());
			corsAllAllowHeaders = DriverConfigurazioneDBLib.getValue(corsConfigurazione.getAccessControlAllAllowHeaders());
			corsAllowCredentials = DriverConfigurazioneDBLib.getValue(corsConfigurazione.getAccessControlAllowCredentials());
			if(corsConfigurazione.getAccessControlMaxAge()!=null) {
				corsAllowMaxAge = CostantiDB.TRUE;
				corsAllowMaxAgeSeconds = corsConfigurazione.getAccessControlMaxAge();	
			}
			if(corsConfigurazione.getAccessControlAllowOrigins()!=null && corsConfigurazione.getAccessControlAllowOrigins().sizeOriginList()>0) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < corsConfigurazione.getAccessControlAllowOrigins().sizeOriginList(); i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(corsConfigurazione.getAccessControlAllowOrigins().getOrigin(i));
				}
				corsAllowOrigins = bf.toString();
			}
			if(corsConfigurazione.getAccessControlAllowHeaders()!=null && corsConfigurazione.getAccessControlAllowHeaders().sizeHeaderList()>0) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < corsConfigurazione.getAccessControlAllowHeaders().sizeHeaderList(); i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(corsConfigurazione.getAccessControlAllowHeaders().getHeader(i));
				}
				corsAllowHeaders = bf.toString();
			}
			if(corsConfigurazione.getAccessControlAllowMethods()!=null && corsConfigurazione.getAccessControlAllowMethods().sizeMethodList()>0) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < corsConfigurazione.getAccessControlAllowMethods().sizeMethodList(); i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(corsConfigurazione.getAccessControlAllowMethods().getMethod(i));
				}
				corsAllowMethods = bf.toString();
			}
			if(corsConfigurazione.getAccessControlExposeHeaders()!=null && corsConfigurazione.getAccessControlExposeHeaders().sizeHeaderList()>0) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < corsConfigurazione.getAccessControlExposeHeaders().sizeHeaderList(); i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(corsConfigurazione.getAccessControlExposeHeaders().getHeader(i));
				}
				corsAllowExposeHeaders = bf.toString();
			}
		}
		
		ResponseCachingConfigurazioneGenerale responseCachingConfigurazione = config.getResponseCaching();
		
		String responseCacheStato = null;
		Integer responseCacheSeconds = null;
		Long responseCacheMaxMsgSize = null;
		String responseCacheHashUrl = null;
		String responseCacheHashQuery = null;
		String responseCacheHashQueryList = null;
		String responseCacheHashHeaders = null;
		String responseCacheHashHeadersList = null;
		String responseCacheHashPayload = null;
		boolean responseCacheNoCache = true;
		boolean responseCacheMaxAge = true;
		boolean responseCacheNoStore = true;
		List<ResponseCachingConfigurazioneRegola> responseCacheRegole = null;
		if(responseCachingConfigurazione!=null && responseCachingConfigurazione.getConfigurazione()!=null) {
			responseCacheStato = DriverConfigurazioneDBLib.getValue(responseCachingConfigurazione.getConfigurazione().getStato());
			responseCacheSeconds = responseCachingConfigurazione.getConfigurazione().getCacheTimeoutSeconds();
			responseCacheMaxMsgSize = responseCachingConfigurazione.getConfigurazione().getMaxMessageSize();
			if(responseCachingConfigurazione.getConfigurazione().getControl()!=null) {
				responseCacheNoCache = responseCachingConfigurazione.getConfigurazione().getControl().getNoCache();
				responseCacheMaxAge = responseCachingConfigurazione.getConfigurazione().getControl().getMaxAge();
				responseCacheNoStore = responseCachingConfigurazione.getConfigurazione().getControl().getNoStore();
			}
			if(responseCachingConfigurazione.getConfigurazione().getHashGenerator()!=null) {
				responseCacheHashUrl = DriverConfigurazioneDBLib.getValue(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getRequestUri());
				
				responseCacheHashQuery = DriverConfigurazioneDBLib.getValue(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getQueryParameters());
				if(StatoFunzionalitaCacheDigestQueryParameter.SELEZIONE_PUNTUALE.equals(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getQueryParameters()) &&
					(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getQueryParameterList()!=null && responseCachingConfigurazione.getConfigurazione().getHashGenerator().sizeQueryParameterList()>0) 
					){
					StringBuilder bf = new StringBuilder();
					for (int i = 0; i < responseCachingConfigurazione.getConfigurazione().getHashGenerator().sizeQueryParameterList(); i++) {
						if(i>0) {
							bf.append(",");
						}
						bf.append(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getQueryParameter(i));
					}
					responseCacheHashQueryList = bf.toString();
				}
				
				responseCacheHashHeaders = DriverConfigurazioneDBLib.getValue(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getHeaders());
				if(StatoFunzionalita.ABILITATO.equals(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getHeaders()) &&
					(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getHeaderList()!=null && responseCachingConfigurazione.getConfigurazione().getHashGenerator().sizeHeaderList()>0) 
					){
					StringBuilder bf = new StringBuilder();
					for (int i = 0; i < responseCachingConfigurazione.getConfigurazione().getHashGenerator().sizeHeaderList(); i++) {
						if(i>0) {
							bf.append(",");
						}
						bf.append(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getHeader(i));
					}
					responseCacheHashHeadersList = bf.toString();
				}
				
				responseCacheHashPayload = DriverConfigurazioneDBLib.getValue(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getPayload());
			}
			responseCacheRegole = responseCachingConfigurazione.getConfigurazione().getRegolaList();
		}
		
		Cache responseCachingCache = null;
		String responseCachingDimensioneCache = null;
		String responseCachingAlgoritmoCache = null;
		String responseCachingIdleCache = null;
		String responseCachingLifeCache = null;
		String responseCachingStatoCache = null;
		if(responseCachingConfigurazione !=null){
			responseCachingCache = responseCachingConfigurazione.getCache();

		}
		responseCachingStatoCache = (responseCachingCache != null ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString());
		if (responseCachingStatoCache.equals(CostantiConfigurazione.ABILITATO.toString())) {
			responseCachingDimensioneCache = responseCachingCache.getDimensione();
			responseCachingAlgoritmoCache = DriverConfigurazioneDBLib.getValue(responseCachingCache.getAlgoritmo());
			responseCachingIdleCache = responseCachingCache.getItemIdleTime();
			responseCachingLifeCache = responseCachingCache.getItemLifeSecond();
		}
		
		
		Cache consegnaCache = null;
		String consegnaDimensioneCache = null;
		String consegnaAlgoritmoCache = null;
		String consegnaIdleCache = null;
		String consegnaLifeCache = null;
		String consegnaStatoCache = null;
		if(config.getAccessoDatiConsegnaApplicativi() !=null){
			consegnaCache = config.getAccessoDatiConsegnaApplicativi().getCache();

		}
		consegnaStatoCache = (consegnaCache != null ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString());
		if (consegnaStatoCache.equals(CostantiConfigurazione.ABILITATO.toString())) {
			consegnaDimensioneCache = consegnaCache.getDimensione();
			consegnaAlgoritmoCache = DriverConfigurazioneDBLib.getValue(consegnaCache.getAlgoritmo());
			consegnaIdleCache = consegnaCache.getItemIdleTime();
			consegnaLifeCache = consegnaCache.getItemLifeSecond();
		}
		
		String utilizzoIndTelematico = null;
		if(indirizzoPerRisposta!=null){
			utilizzoIndTelematico =	DriverConfigurazioneDBLib.getValue(indirizzoPerRisposta.getUtilizzo());
		}
		String cadenzaInoltro = null;
		if(inoltroBusteNonRiscontrate!=null){
			cadenzaInoltro = inoltroBusteNonRiscontrate.getCadenza();
		}
		String autenticazione = null;
		if(integrationManager!=null){
			autenticazione = integrationManager.getAutenticazione();
		}
		String msgDiagSeverita = null;
		String msgDiagSeveritaLog4j = null;
		if(messaggiDiagnostici!=null){
			msgDiagSeverita = DriverConfigurazioneDBLib.getValue(messaggiDiagnostici.getSeverita());
			msgDiagSeveritaLog4j = DriverConfigurazioneDBLib.getValue(messaggiDiagnostici.getSeveritaLog4j());
		}
		String valControllo = null;
		String valStato = null;
		String valManifest = null;
		String valProfiloCollaborazione = null;
		if(validazioneBuste!=null){
			valControllo = DriverConfigurazioneDBLib.getValue(validazioneBuste.getControllo());
			valStato = DriverConfigurazioneDBLib.getValue(validazioneBuste.getStato());
			valManifest = DriverConfigurazioneDBLib.getValue(validazioneBuste.getManifestAttachments());
			valProfiloCollaborazione = DriverConfigurazioneDBLib.getValue(validazioneBuste.getProfiloCollaborazione());
		}

		String gestioneManifest = null;
		if(att!=null){
			gestioneManifest = DriverConfigurazioneDBLib.getValue(att.getGestioneManifest());
		}

		Cache registroCache = null;
		String registroDimensioneCache = null;
		String registroAlgoritmoCache = null;
		String registroIdleCache = null;
		String registroLifeCache = null;
		String registroStatoCache = null;
		if(car !=null){
			registroCache = car.getCache();

		}
		registroStatoCache = (registroCache != null ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString());
		if (registroStatoCache.equals(CostantiConfigurazione.ABILITATO.toString())) {
			registroDimensioneCache = registroCache.getDimensione();
			registroAlgoritmoCache = DriverConfigurazioneDBLib.getValue(registroCache.getAlgoritmo());
			registroIdleCache = registroCache.getItemIdleTime();
			registroLifeCache = registroCache.getItemLifeSecond();
		}
		
		Cache configCache = null;
		String configDimensioneCache = null;
		String configAlgoritmoCache = null;
		String configIdleCache = null;
		String configLifeCache = null;
		String configStatoCache = null;
		if(aConfig !=null){
			configCache = aConfig.getCache();

		}
		configStatoCache = (configCache != null ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString());
		if (configStatoCache.equals(CostantiConfigurazione.ABILITATO.toString())) {
			configDimensioneCache = configCache.getDimensione();
			configAlgoritmoCache = DriverConfigurazioneDBLib.getValue(configCache.getAlgoritmo());
			configIdleCache = configCache.getItemIdleTime();
			configLifeCache = configCache.getItemLifeSecond();
		}
		
		Cache authzCache = null;
		String authzDimensioneCache = null;
		String authzAlgoritmoCache = null;
		String authzIdleCache = null;
		String authzLifeCache = null;
		String authzStatoCache = null;
		if(aDatiAuthz !=null){
			authzCache = aDatiAuthz.getCache();

		}
		authzStatoCache = (authzCache != null ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString());
		if (authzStatoCache.equals(CostantiConfigurazione.ABILITATO.toString())) {
			authzDimensioneCache = authzCache.getDimensione();
			authzAlgoritmoCache = DriverConfigurazioneDBLib.getValue(authzCache.getAlgoritmo());
			authzIdleCache = authzCache.getItemIdleTime();
			authzLifeCache = authzCache.getItemLifeSecond();
		}
		
		Cache authnCache = null;
		String authnDimensioneCache = null;
		String authnAlgoritmoCache = null;
		String authnIdleCache = null;
		String authnLifeCache = null;
		String authnStatoCache = null;
		if(aDatiAuthn !=null){
			authnCache = aDatiAuthn.getCache();

		}
		authnStatoCache = (authnCache != null ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString());
		if (authnStatoCache.equals(CostantiConfigurazione.ABILITATO.toString())) {
			authnDimensioneCache = authnCache.getDimensione();
			authnAlgoritmoCache = DriverConfigurazioneDBLib.getValue(authnCache.getAlgoritmo());
			authnIdleCache = authnCache.getItemIdleTime();
			authnLifeCache = authnCache.getItemLifeSecond();
		}
		
		Cache tokenCache = null;
		String tokenDimensioneCache = null;
		String tokenAlgoritmoCache = null;
		String tokenIdleCache = null;
		String tokenLifeCache = null;
		String tokenStatoCache = null;
		if(aDatiGestioneToken !=null){
			tokenCache = aDatiGestioneToken.getCache();
		}
		tokenStatoCache = (tokenCache != null ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString());
		if (tokenStatoCache.equals(CostantiConfigurazione.ABILITATO.toString())) {
			tokenDimensioneCache = tokenCache.getDimensione();
			tokenAlgoritmoCache = DriverConfigurazioneDBLib.getValue(tokenCache.getAlgoritmo());
			tokenIdleCache = tokenCache.getItemIdleTime();
			tokenLifeCache = tokenCache.getItemLifeSecond();
		}
		
		Cache aaCache = null;
		String aaDimensioneCache = null;
		String aaAlgoritmoCache = null;
		String aaIdleCache = null;
		String aaLifeCache = null;
		String aaStatoCache = null;
		if(aDatiAttributeAuthority !=null){
			aaCache = aDatiAttributeAuthority.getCache();
		}
		aaStatoCache = (aaCache != null ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString());
		if (aaStatoCache.equals(CostantiConfigurazione.ABILITATO.toString())) {
			aaDimensioneCache = aaCache.getDimensione();
			aaAlgoritmoCache = DriverConfigurazioneDBLib.getValue(aaCache.getAlgoritmo());
			aaIdleCache = aaCache.getItemIdleTime();
			aaLifeCache = aaCache.getItemLifeSecond();
		}
		
		Cache keystoreCache = null;
		String keystoreDimensioneCache = null;
		String keystoreAlgoritmoCache = null;
		String keystoreIdleCache = null;
		String keystoreLifeCache = null;
		String keystoreStatoCache = null;
		String keystoreCrlLifeCache = null;
		if(aDatiKeystore !=null){
			keystoreCache = aDatiKeystore.getCache();
			keystoreCrlLifeCache = aDatiKeystore.getCrlItemLifeSecond();

		}
		keystoreStatoCache = (keystoreCache != null ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString());
		if (keystoreStatoCache.equals(CostantiConfigurazione.ABILITATO.toString())) {
			keystoreDimensioneCache = keystoreCache.getDimensione();
			keystoreAlgoritmoCache = DriverConfigurazioneDBLib.getValue(keystoreCache.getAlgoritmo());
			keystoreIdleCache = keystoreCache.getItemIdleTime();
			keystoreLifeCache = keystoreCache.getItemLifeSecond();
		}
		
		Cache datiRichiesteCache = null;
		String datiRichiesteDimensioneCache = null;
		String datiRichiesteAlgoritmoCache = null;
		String datiRichiesteIdleCache = null;
		String datiRichiesteLifeCache = null;
		String datiRichiesteStatoCache = null;
		if(aDatiRichieste !=null){
			datiRichiesteCache = aDatiRichieste.getCache();
		}
		datiRichiesteStatoCache = (datiRichiesteCache != null ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString());
		if (datiRichiesteStatoCache.equals(CostantiConfigurazione.ABILITATO.toString())) {
			datiRichiesteDimensioneCache = datiRichiesteCache.getDimensione();
			datiRichiesteAlgoritmoCache = DriverConfigurazioneDBLib.getValue(datiRichiesteCache.getAlgoritmo());
			datiRichiesteIdleCache = datiRichiesteCache.getItemIdleTime();
			datiRichiesteLifeCache = datiRichiesteCache.getItemLifeSecond();
		}

		Tracciamento t = config.getTracciamento();
		String tracciamentoBuste = null;
		String tracciamentoEsiti = null;
		if (t != null) {
			tracciamentoBuste = DriverConfigurazioneDBLib.getValue(t.getStato());
			tracciamentoEsiti = t.getEsiti();
		}
		
		Dump d = config.getDump();
		String dumpApplicativo = null;
		String dumpPD = null;
		String dumpPA = null;
		DumpConfigurazione dumpConfigPA = null;
		DumpConfigurazione dumpConfigPD = null;
		if (d != null) {
			dumpApplicativo = DriverConfigurazioneDBLib.getValue(d.getStato());
			dumpPD = DriverConfigurazioneDBLib.getValue(d.getDumpBinarioPortaDelegata());
			dumpPA = DriverConfigurazioneDBLib.getValue(d.getDumpBinarioPortaApplicativa());
			
			DumpConfigurazione dumpConfig = getDumpConfigurazioneDeprecated(d); 
			if(dumpConfig!=null) {
				// backward compatibility, lo uso sia per erogazione che per fruizione (per import package)
				dumpConfigPA = dumpConfig;
				dumpConfigPD = dumpConfig;
			}
			else {
				dumpConfigPA = d.getConfigurazionePortaApplicativa();
				dumpConfigPD = d.getConfigurazionePortaDelegata();
			}
		}

		Transazioni transazioni = config.getTransazioni();
		String transazioniTempiElaborazione = null;
		String transazioniToken = null;
		if(transazioni!=null) {
			transazioniTempiElaborazione = DriverConfigurazioneDBLib.getValue(transazioni.getTempiElaborazione());
			transazioniToken = DriverConfigurazioneDBLib.getValue(transazioni.getToken());
		}
		
		String modRisposta = CostantiConfigurazione.CONNECTION_REPLY.toString();
		if(risposte!=null){
			modRisposta = (risposte.getConnessione().equals(CostantiConfigurazione.CONNECTION_REPLY) ? 
					CostantiConfigurazione.CONNECTION_REPLY.toString() : CostantiConfigurazione.NEW_CONNECTION.toString());
		}
		String routingEnabled =  CostantiConfigurazione.DISABILITATO.toString();
		if(config.getRoutingTable()!=null &&
			(config.getRoutingTable().getAbilitata()!=null && config.getRoutingTable().getAbilitata())
			){
			routingEnabled =  CostantiConfigurazione.ABILITATO.toString();
		}
		
		String validazioneContenutiStato = null;
		String validazioneContenutiTipo = null;
		String validazioneContenutiAcceptMtomMessage = null;
		if(config.getValidazioneContenutiApplicativi()!=null){
			validazioneContenutiStato = DriverConfigurazioneDBLib.getValue(config.getValidazioneContenutiApplicativi().getStato());
			validazioneContenutiTipo = DriverConfigurazioneDBLib.getValue(config.getValidazioneContenutiApplicativi().getTipo());
			validazioneContenutiAcceptMtomMessage = DriverConfigurazioneDBLib.getValue(config.getValidazioneContenutiApplicativi().getAcceptMtomMessage());
		}

		CanaliConfigurazione configurazioneCanali = config.getGestioneCanali();
		String configurazioneCanaliStato = null;
		if(configurazioneCanali!=null) {
			configurazioneCanaliStato = DriverConfigurazioneDBLib.getValue(configurazioneCanali.getStato());
		}
		
		ConfigurazioneGeneraleHandler configHandlers = config.getConfigurazioneHandler();
		
		ExtendedInfoManager extInfoManager = ExtendedInfoManager.getInstance();
		IExtendedInfo extInfoConfigurazioneDriver = extInfoManager.newInstanceExtendedInfoConfigurazione();
									
		try {
			switch (type) {
			case CREATE:
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.CONFIGURAZIONE);
				sqlQueryObject.addInsertField("cadenza_inoltro", "?");
				
				sqlQueryObject.addInsertField("validazione_stato", "?");
				sqlQueryObject.addInsertField("validazione_controllo", "?");
				
				sqlQueryObject.addInsertField("msg_diag_severita", "?");
				sqlQueryObject.addInsertField("msg_diag_severita_log4j", "?");
				sqlQueryObject.addInsertField("auth_integration_manager", "?");
				sqlQueryObject.addInsertField("validazione_profilo", "?");
				sqlQueryObject.addInsertField("mod_risposta", "?");
				sqlQueryObject.addInsertField("indirizzo_telematico", "?");
				sqlQueryObject.addInsertField("routing_enabled", "?");
				sqlQueryObject.addInsertField("gestione_manifest", "?");
				sqlQueryObject.addInsertField("validazione_manifest", "?");
				sqlQueryObject.addInsertField("tracciamento_buste", "?");
				sqlQueryObject.addInsertField("tracciamento_esiti", "?");
				sqlQueryObject.addInsertField("transazioni_tempi", "?");
				sqlQueryObject.addInsertField("transazioni_token", "?");
				sqlQueryObject.addInsertField("dump", "?");
				sqlQueryObject.addInsertField("dump_bin_pd", "?");
				sqlQueryObject.addInsertField("dump_bin_pa", "?");
				sqlQueryObject.addInsertField("validazione_contenuti_stato", "?");
				sqlQueryObject.addInsertField("validazione_contenuti_tipo", "?");
				sqlQueryObject.addInsertField("validazione_contenuti_mtom", "?");
				// registro cache
				sqlQueryObject.addInsertField("statocache", "?");
				sqlQueryObject.addInsertField("dimensionecache", "?");
				sqlQueryObject.addInsertField("algoritmocache", "?");
				sqlQueryObject.addInsertField("idlecache", "?");
				sqlQueryObject.addInsertField("lifecache", "?");
				// config cache
				sqlQueryObject.addInsertField("config_statocache", "?");
				sqlQueryObject.addInsertField("config_dimensionecache", "?");
				sqlQueryObject.addInsertField("config_algoritmocache", "?");
				sqlQueryObject.addInsertField("config_idlecache", "?");
				sqlQueryObject.addInsertField("config_lifecache", "?");
				// authz cache
				sqlQueryObject.addInsertField("auth_statocache", "?");
				sqlQueryObject.addInsertField("auth_dimensionecache", "?");
				sqlQueryObject.addInsertField("auth_algoritmocache", "?");
				sqlQueryObject.addInsertField("auth_idlecache", "?");
				sqlQueryObject.addInsertField("auth_lifecache", "?");
				// authn cache
				sqlQueryObject.addInsertField("authn_statocache", "?");
				sqlQueryObject.addInsertField("authn_dimensionecache", "?");
				sqlQueryObject.addInsertField("authn_algoritmocache", "?");
				sqlQueryObject.addInsertField("authn_idlecache", "?");
				sqlQueryObject.addInsertField("authn_lifecache", "?");
				// gestione token cache
				sqlQueryObject.addInsertField("token_statocache", "?");
				sqlQueryObject.addInsertField("token_dimensionecache", "?");
				sqlQueryObject.addInsertField("token_algoritmocache", "?");
				sqlQueryObject.addInsertField("token_idlecache", "?");
				sqlQueryObject.addInsertField("token_lifecache", "?");
				// gestione aa cache
				sqlQueryObject.addInsertField("aa_statocache", "?");
				sqlQueryObject.addInsertField("aa_dimensionecache", "?");
				sqlQueryObject.addInsertField("aa_algoritmocache", "?");
				sqlQueryObject.addInsertField("aa_idlecache", "?");
				sqlQueryObject.addInsertField("aa_lifecache", "?");
				// keystore cache
				sqlQueryObject.addInsertField("keystore_statocache", "?");
				sqlQueryObject.addInsertField("keystore_dimensionecache", "?");
				sqlQueryObject.addInsertField("keystore_algoritmocache", "?");
				sqlQueryObject.addInsertField("keystore_idlecache", "?");
				sqlQueryObject.addInsertField("keystore_lifecache", "?");
				sqlQueryObject.addInsertField("keystore_crl_lifecache", "?");
				// multitenant
				sqlQueryObject.addInsertField("multitenant_stato", "?");
				sqlQueryObject.addInsertField("multitenant_fruizioni", "?");
				sqlQueryObject.addInsertField("multitenant_erogazioni", "?");
				// cors
				sqlQueryObject.addInsertField("cors_stato", "?");
				sqlQueryObject.addInsertField("cors_tipo", "?");
				sqlQueryObject.addInsertField("cors_all_allow_origins", "?");
				sqlQueryObject.addInsertField("cors_all_allow_methods", "?");
				sqlQueryObject.addInsertField("cors_all_allow_headers", "?");
				sqlQueryObject.addInsertField("cors_allow_credentials", "?");
				sqlQueryObject.addInsertField("cors_allow_max_age", "?");
				sqlQueryObject.addInsertField("cors_allow_max_age_seconds", "?");
				sqlQueryObject.addInsertField("cors_allow_origins", "?");
				sqlQueryObject.addInsertField("cors_allow_headers", "?");
				sqlQueryObject.addInsertField("cors_allow_methods", "?");
				sqlQueryObject.addInsertField("cors_allow_expose_headers", "?");
				// responseCaching
				sqlQueryObject.addInsertField("response_cache_stato", "?");
				sqlQueryObject.addInsertField("response_cache_seconds", "?");
				sqlQueryObject.addInsertField("response_cache_max_msg_size", "?");
				sqlQueryObject.addInsertField("response_cache_control_nocache", "?");
				sqlQueryObject.addInsertField("response_cache_control_maxage", "?");
				sqlQueryObject.addInsertField("response_cache_control_nostore", "?");
				sqlQueryObject.addInsertField("response_cache_hash_url", "?");
				sqlQueryObject.addInsertField("response_cache_hash_query", "?");
				sqlQueryObject.addInsertField("response_cache_hash_query_list", "?");
				sqlQueryObject.addInsertField("response_cache_hash_headers", "?");
				sqlQueryObject.addInsertField("response_cache_hash_hdr_list", "?");
				sqlQueryObject.addInsertField("response_cache_hash_payload", "?");
				// responseCaching cache
				sqlQueryObject.addInsertField("response_cache_statocache", "?");
				sqlQueryObject.addInsertField("response_cache_dimensionecache", "?");
				sqlQueryObject.addInsertField("response_cache_algoritmocache", "?");
				sqlQueryObject.addInsertField("response_cache_idlecache", "?");
				sqlQueryObject.addInsertField("response_cache_lifecache", "?");
				// consegna applicativi cache
				sqlQueryObject.addInsertField("consegna_statocache", "?");
				sqlQueryObject.addInsertField("consegna_dimensionecache", "?");
				sqlQueryObject.addInsertField("consegna_algoritmocache", "?");
				sqlQueryObject.addInsertField("consegna_idlecache", "?");
				sqlQueryObject.addInsertField("consegna_lifecache", "?");
				// canali
				sqlQueryObject.addInsertField("canali_stato", "?");
				// dati richieste cache
				sqlQueryObject.addInsertField("dati_richieste_statocache", "?");
				sqlQueryObject.addInsertField("dati_richieste_dimensionecache", "?");
				sqlQueryObject.addInsertField("dati_richieste_algoritmocache", "?");
				sqlQueryObject.addInsertField("dati_richieste_idlecache", "?");
				sqlQueryObject.addInsertField("dati_richieste_lifecache", "?");

				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);

				int index = 1;
				
				updateStmt.setString(index++, cadenzaInoltro);
				updateStmt.setString(index++, valStato);
				updateStmt.setString(index++, valControllo);
				updateStmt.setString(index++, msgDiagSeverita);
				updateStmt.setString(index++, msgDiagSeveritaLog4j);
				updateStmt.setString(index++, autenticazione);
				updateStmt.setString(index++, valProfiloCollaborazione);
				updateStmt.setString(index++, modRisposta);
				updateStmt.setString(index++, utilizzoIndTelematico);
				updateStmt.setString(index++, routingEnabled);
				updateStmt.setString(index++, gestioneManifest);
				updateStmt.setString(index++, valManifest);
				updateStmt.setString(index++, tracciamentoBuste);
				updateStmt.setString(index++, tracciamentoEsiti);
				updateStmt.setString(index++, transazioniTempiElaborazione);
				updateStmt.setString(index++, transazioniToken);
				updateStmt.setString(index++, dumpApplicativo);
				updateStmt.setString(index++, dumpPD);
				updateStmt.setString(index++, dumpPA);
				updateStmt.setString(index++, validazioneContenutiStato);
				updateStmt.setString(index++, validazioneContenutiTipo);
				updateStmt.setString(index++, validazioneContenutiAcceptMtomMessage);
				// registro cache
				updateStmt.setString(index++, registroStatoCache);
				updateStmt.setString(index++, registroDimensioneCache);
				updateStmt.setString(index++, registroAlgoritmoCache);
				updateStmt.setString(index++, registroIdleCache);
				updateStmt.setString(index++, registroLifeCache);
				// config cache
				updateStmt.setString(index++, configStatoCache);
				updateStmt.setString(index++, configDimensioneCache);
				updateStmt.setString(index++, configAlgoritmoCache);
				updateStmt.setString(index++, configIdleCache);
				updateStmt.setString(index++, configLifeCache);
				// authz cache
				updateStmt.setString(index++, authzStatoCache);
				updateStmt.setString(index++, authzDimensioneCache);
				updateStmt.setString(index++, authzAlgoritmoCache);
				updateStmt.setString(index++, authzIdleCache);
				updateStmt.setString(index++, authzLifeCache);
				// authn cache
				updateStmt.setString(index++, authnStatoCache);
				updateStmt.setString(index++, authnDimensioneCache);
				updateStmt.setString(index++, authnAlgoritmoCache);
				updateStmt.setString(index++, authnIdleCache);
				updateStmt.setString(index++, authnLifeCache);
				// token cache
				updateStmt.setString(index++, tokenStatoCache);
				updateStmt.setString(index++, tokenDimensioneCache);
				updateStmt.setString(index++, tokenAlgoritmoCache);
				updateStmt.setString(index++, tokenIdleCache);
				updateStmt.setString(index++, tokenLifeCache);
				// aa cache
				updateStmt.setString(index++, aaStatoCache);
				updateStmt.setString(index++, aaDimensioneCache);
				updateStmt.setString(index++, aaAlgoritmoCache);
				updateStmt.setString(index++, aaIdleCache);
				updateStmt.setString(index++, aaLifeCache);
				// keystore cache
				updateStmt.setString(index++, keystoreStatoCache);
				updateStmt.setString(index++, keystoreDimensioneCache);
				updateStmt.setString(index++, keystoreAlgoritmoCache);
				updateStmt.setString(index++, keystoreIdleCache);
				updateStmt.setString(index++, keystoreLifeCache);
				updateStmt.setString(index++, keystoreCrlLifeCache);
				// multitenant
				updateStmt.setString(index++, multitenant!=null ? DriverConfigurazioneDBLib.getValue(multitenant.getStato()) : null);
				updateStmt.setString(index++, multitenant!=null ? DriverConfigurazioneDBLib.getValue(multitenant.getFruizioneSceltaSoggettiErogatori()) : null);
				updateStmt.setString(index++, multitenant!=null ? DriverConfigurazioneDBLib.getValue(multitenant.getErogazioneSceltaSoggettiFruitori()) : null);
				// cors
				updateStmt.setString(index++, corsStato);
				updateStmt.setString(index++, corsTipo);
				updateStmt.setString(index++, corsAllAllowOrigins);
				updateStmt.setString(index++, corsAllAllowMethods);
				updateStmt.setString(index++, corsAllAllowHeaders);
				updateStmt.setString(index++, corsAllowCredentials);
				updateStmt.setInt(index++, corsAllowMaxAge);
				if(corsAllowMaxAgeSeconds!=null) {
					updateStmt.setInt(index++, corsAllowMaxAgeSeconds);
				}
				else {
					updateStmt.setNull(index++, java.sql.Types.INTEGER);
				}
				updateStmt.setString(index++, corsAllowOrigins);
				updateStmt.setString(index++, corsAllowHeaders);
				updateStmt.setString(index++, corsAllowMethods);
				updateStmt.setString(index++, corsAllowExposeHeaders);				
				// responseCaching
				updateStmt.setString(index++, responseCacheStato);
				if(responseCacheSeconds!=null) {
					updateStmt.setInt(index++, responseCacheSeconds);
				}
				else {
					updateStmt.setNull(index++, java.sql.Types.INTEGER);
				}
				if(responseCacheMaxMsgSize!=null) {
					updateStmt.setLong(index++, responseCacheMaxMsgSize);
				}
				else {
					updateStmt.setNull(index++, java.sql.Types.BIGINT);
				}
				updateStmt.setInt(index++, responseCacheNoCache ? CostantiDB.TRUE : CostantiDB.FALSE);
				updateStmt.setInt(index++, responseCacheMaxAge ? CostantiDB.TRUE : CostantiDB.FALSE);
				updateStmt.setInt(index++, responseCacheNoStore ? CostantiDB.TRUE : CostantiDB.FALSE);
				updateStmt.setString(index++, responseCacheHashUrl);
				updateStmt.setString(index++, responseCacheHashQuery);
				updateStmt.setString(index++, responseCacheHashQueryList);
				updateStmt.setString(index++, responseCacheHashHeaders);
				updateStmt.setString(index++, responseCacheHashHeadersList);
				updateStmt.setString(index++, responseCacheHashPayload);
				// responseCaching cache
				updateStmt.setString(index++, responseCachingStatoCache);
				updateStmt.setString(index++, responseCachingDimensioneCache);
				updateStmt.setString(index++, responseCachingAlgoritmoCache);
				updateStmt.setString(index++, responseCachingIdleCache);
				updateStmt.setString(index++, responseCachingLifeCache);
				// consegna applicativi cache
				updateStmt.setString(index++, consegnaStatoCache);
				updateStmt.setString(index++, consegnaDimensioneCache);
				updateStmt.setString(index++, consegnaAlgoritmoCache);
				updateStmt.setString(index++, consegnaIdleCache);
				updateStmt.setString(index++, consegnaLifeCache);
				// canali
				updateStmt.setString(index++, configurazioneCanaliStato);
				// dati richieste cache
				updateStmt.setString(index++, datiRichiesteStatoCache);
				updateStmt.setString(index++, datiRichiesteDimensioneCache);
				updateStmt.setString(index++, datiRichiesteAlgoritmoCache);
				updateStmt.setString(index++, datiRichiesteIdleCache);
				updateStmt.setString(index++, datiRichiesteLifeCache);

				String msgDebug = "eseguo query :" + 
						DBUtils.formatSQLString(updateQuery, 
								cadenzaInoltro, 
								valStato, valControllo, 
								msgDiagSeverita, msgDiagSeveritaLog4j, 
								autenticazione, 
								valProfiloCollaborazione, 
								modRisposta, utilizzoIndTelematico, 
								routingEnabled, gestioneManifest, 
								valManifest, tracciamentoBuste, transazioniTempiElaborazione, transazioniToken, dumpApplicativo, dumpPD, dumpPA,
								validazioneContenutiStato,validazioneContenutiTipo,validazioneContenutiAcceptMtomMessage,
								registroStatoCache, registroDimensioneCache, registroAlgoritmoCache, registroIdleCache, registroLifeCache,
								configStatoCache, configDimensioneCache, configAlgoritmoCache, configIdleCache, configLifeCache,
								authzStatoCache, authzDimensioneCache, authzAlgoritmoCache, authzIdleCache, authzLifeCache,
								authnStatoCache, authnDimensioneCache, authnAlgoritmoCache, authnIdleCache, authnLifeCache,
								tokenStatoCache, tokenDimensioneCache, tokenAlgoritmoCache, tokenIdleCache, tokenLifeCache,
								aaStatoCache, aaDimensioneCache, aaAlgoritmoCache, aaIdleCache, aaLifeCache,
								keystoreStatoCache, keystoreDimensioneCache, keystoreAlgoritmoCache, keystoreIdleCache, keystoreLifeCache, keystoreCrlLifeCache,
								(multitenant!=null ? DriverConfigurazioneDBLib.getValue(multitenant.getStato()) : null),
								(multitenant!=null ? DriverConfigurazioneDBLib.getValue(multitenant.getFruizioneSceltaSoggettiErogatori()) : null),
								(multitenant!=null ? DriverConfigurazioneDBLib.getValue(multitenant.getErogazioneSceltaSoggettiFruitori()) : null),
								corsStato, corsTipo, corsAllAllowOrigins, corsAllAllowMethods, corsAllAllowHeaders, corsAllowCredentials, corsAllowMaxAge, corsAllowMaxAgeSeconds,
								corsAllowOrigins, corsAllowHeaders, corsAllowMethods, corsAllowExposeHeaders,
								responseCacheStato, responseCacheSeconds, responseCacheMaxMsgSize, 
								(responseCacheNoCache ? CostantiDB.TRUE : CostantiDB.FALSE),
								(responseCacheMaxAge ? CostantiDB.TRUE : CostantiDB.FALSE),
								(responseCacheNoStore ? CostantiDB.TRUE : CostantiDB.FALSE),
								responseCacheHashUrl, responseCacheHashQuery, responseCacheHashQueryList, responseCacheHashHeaders, responseCacheHashHeadersList, responseCacheHashPayload,
								responseCachingStatoCache, responseCachingDimensioneCache, responseCachingAlgoritmoCache, responseCachingIdleCache, responseCachingLifeCache,
								consegnaStatoCache, consegnaDimensioneCache, consegnaAlgoritmoCache, consegnaIdleCache, consegnaLifeCache,
								configurazioneCanaliStato,
								datiRichiesteStatoCache, datiRichiesteDimensioneCache, datiRichiesteAlgoritmoCache, datiRichiesteIdleCache, datiRichiesteLifeCache
								);
				DriverConfigurazioneDBLib.logDebug(msgDebug);

				n = updateStmt.executeUpdate();
				updateStmt.close();

				// delete from config_url_invocazione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIG_URL_REGOLE);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIG_URL_INVOCAZIONE);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				// insert into config_protocolli
				if(config.getUrlInvocazione()!=null){
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.CONFIG_URL_INVOCAZIONE);
					sqlQueryObject.addInsertField("base_url", "?");
					sqlQueryObject.addInsertField("base_url_fruizione", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);
					int indexP = 1;
					updateStmt.setString(indexP++, config.getUrlInvocazione().getBaseUrl());
					updateStmt.setString(indexP++, config.getUrlInvocazione().getBaseUrlFruizione());
					updateStmt.executeUpdate();
					updateStmt.close();
					
					if(config.getUrlInvocazione().sizeRegolaList()>0){
						for(int k=0; k<config.getUrlInvocazione().sizeRegolaList();k++){
							ConfigurazioneUrlInvocazioneRegola configUrlInvocazioneRegola = config.getUrlInvocazione().getRegola(k);
							createUrlInvocazioneRegolaEngine(configUrlInvocazioneRegola, con);
						}
					}
				}
				
				// delete from msg diag appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.MSG_DIAGN_APPENDER_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from msg diag appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.MSG_DIAGN_APPENDER);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into msg diag appender
				if(config.getMessaggiDiagnostici()!=null){
					for(int k=0; k<config.getMessaggiDiagnostici().sizeOpenspcoopAppenderList();k++){
						OpenspcoopAppender appender = config.getMessaggiDiagnostici().getOpenspcoopAppender(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo", appender.getTipo() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idMsgDiagAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDBLib.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.MSG_DIAGN_APPENDER, CostantiDB.MSG_DIAGN_APPENDER_COLUMN_ID, 
										CostantiDB.MSG_DIAGN_APPENDER_SEQUENCE, CostantiDB.MSG_DIAGN_APPENDER_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idMsgDiagAppender<=0){
							throw new DriverConfigurazioneException("ID (msg diag appender) autoincrementale non ottenuto");
						}
						
						for(int l=0; l<appender.sizePropertyList();l++){
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
							sqlQueryObject.addInsertTable(CostantiDB.MSG_DIAGN_APPENDER_PROP);
							sqlQueryObject.addInsertField("id_appender", "?");
							sqlQueryObject.addInsertField("nome", "?");
							sqlQueryObject.addInsertField("valore", "?");
							updateQuery = sqlQueryObject.createSQLInsert();
							updateStmt = con.prepareStatement(updateQuery);
							updateStmt.setLong(1, idMsgDiagAppender);
							updateStmt.setString(2, appender.getProperty(l).getNome());
							updateStmt.setString(3, appender.getProperty(l).getValore());
							updateStmt.executeUpdate();
							updateStmt.close();
						}

					}
				}

				// delete from tracciamento appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.TRACCIAMENTO_APPENDER_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from tracciamento appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.TRACCIAMENTO_APPENDER);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into tracciamento appender
				if(config.getTracciamento()!=null){
					for(int k=0; k<config.getTracciamento().sizeOpenspcoopAppenderList();k++){
						OpenspcoopAppender appender = config.getTracciamento().getOpenspcoopAppender(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo", appender.getTipo() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idTracceAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDBLib.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.TRACCIAMENTO_APPENDER, CostantiDB.TRACCIAMENTO_APPENDER_COLUMN_ID, 
										CostantiDB.TRACCIAMENTO_APPENDER_SEQUENCE, CostantiDB.TRACCIAMENTO_APPENDER_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idTracceAppender<=0){
							throw new DriverConfigurazioneException("ID (tracce appender) autoincrementale non ottenuto");
						}

						for(int l=0; l<appender.sizePropertyList();l++){
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
							sqlQueryObject.addInsertTable(CostantiDB.TRACCIAMENTO_APPENDER_PROP);
							sqlQueryObject.addInsertField("id_appender", "?");
							sqlQueryObject.addInsertField("nome", "?");
							sqlQueryObject.addInsertField("valore", "?");
							updateQuery = sqlQueryObject.createSQLInsert();
							updateStmt = con.prepareStatement(updateQuery);
							updateStmt.setLong(1, idTracceAppender);
							updateStmt.setString(2, appender.getProperty(l).getNome());
							updateStmt.setString(3, appender.getProperty(l).getValore());
							updateStmt.executeUpdate();
							updateStmt.close();
						}

					}
				}
				
				
				// delete from dump appender_prop
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.DUMP_APPENDER_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from dump appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.DUMP_APPENDER);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into dump appender
				if(config.getDump()!=null){
					for(int k=0; k<config.getDump().sizeOpenspcoopAppenderList();k++){
						OpenspcoopAppender appender = config.getDump().getOpenspcoopAppender(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo", appender.getTipo() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idDumpAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDBLib.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.DUMP_APPENDER, CostantiDB.DUMP_APPENDER_COLUMN_ID, 
										CostantiDB.DUMP_APPENDER_SEQUENCE, CostantiDB.DUMP_APPENDER_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idDumpAppender<=0){
							throw new DriverConfigurazioneException("ID (dump appender) autoincrementale non ottenuto");
						}

						for(int l=0; l<appender.sizePropertyList();l++){
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
							sqlQueryObject.addInsertTable(CostantiDB.DUMP_APPENDER_PROP);
							sqlQueryObject.addInsertField("id_appender", "?");
							sqlQueryObject.addInsertField("nome", "?");
							sqlQueryObject.addInsertField("valore", "?");
							updateQuery = sqlQueryObject.createSQLInsert();
							updateStmt = con.prepareStatement(updateQuery);
							updateStmt.setLong(1, idDumpAppender);
							updateStmt.setString(2, appender.getProperty(l).getNome());
							updateStmt.setString(3, appender.getProperty(l).getValore());
							updateStmt.executeUpdate();
							updateStmt.close();
						}

					}
				}
				
				// Cache Regole
				n=0;
				if(responseCacheRegole!=null && !responseCacheRegole.isEmpty()){
					for (int j = 0; j < responseCacheRegole.size(); j++) {
						ResponseCachingConfigurazioneRegola regola = responseCacheRegole.get(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.CONFIGURAZIONE_CACHE_REGOLE);
						if(regola.getReturnCodeMin()!=null && regola.getReturnCodeMin()>0) {
							sqlQueryObject.addInsertField("status_min", "?");
						}
						if(regola.getReturnCodeMax()!=null && regola.getReturnCodeMax()>0) {
							sqlQueryObject.addInsertField("status_max", "?");
						}
						sqlQueryObject.addInsertField("fault", "?");
						if(regola.getCacheTimeoutSeconds()!=null && regola.getCacheTimeoutSeconds()>0) {
							sqlQueryObject.addInsertField("cache_seconds", "?");
						}
						String sqlQuery = sqlQueryObject.createSQLInsert();
						updateStmt = con.prepareStatement(sqlQuery);
						int indexCR = 1;
						if(regola.getReturnCodeMin()!=null && regola.getReturnCodeMin()>0) {
							updateStmt.setInt(indexCR++, regola.getReturnCodeMin());
						}
						if(regola.getReturnCodeMax()!=null && regola.getReturnCodeMax()>0) {
							updateStmt.setInt(indexCR++, regola.getReturnCodeMax());
						}
						updateStmt.setInt(indexCR++, regola.getFault() ? CostantiDB.TRUE : CostantiDB.FALSE);
						if(regola.getCacheTimeoutSeconds()!=null && regola.getCacheTimeoutSeconds()>0) {
							updateStmt.setInt(indexCR++, regola.getCacheTimeoutSeconds());
						}
						updateStmt.executeUpdate();
						updateStmt.close();
						n++;
						DriverConfigurazioneDBLib.logDebug("Aggiunta regola di cache");
					}
				}
				
				DriverConfigurazioneDBLib.logDebug("Aggiunte " + n + " regole di cache");
				
				// dumpConfigurazione
				
				// per backward compatibility elimino una config esistente
				DumpConfigurazione dumpConfigOldBackwardCompatibility = DriverConfigurazioneDB_dumpLIB.readDumpConfigurazione(con, null, CostantiDB.OLD_BACKWARD_COMPATIBILITY_DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG);
				if(dumpConfigOldBackwardCompatibility!=null) {
					DriverConfigurazioneDB_dumpLIB.CRUDDumpConfigurazione(DELETE, con, dumpConfigOldBackwardCompatibility, null, CostantiDB.OLD_BACKWARD_COMPATIBILITY_DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG);
				}
				
				DriverConfigurazioneDB_dumpLIB.CRUDDumpConfigurazione(type, con, dumpConfigPA, null, CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG_PA);
				DriverConfigurazioneDB_dumpLIB.CRUDDumpConfigurazione(type, con, dumpConfigPD, null, CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG_PD);
				

				// delete from msgdiag ds prop
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.MSG_DIAGN_DS_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from msgdiag ds
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.MSG_DIAGN_DS);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into msgdiag ds
				if(config.getMessaggiDiagnostici()!=null){
					for(int k=0; k<config.getMessaggiDiagnostici().sizeOpenspcoopSorgenteDatiList();k++){
						OpenspcoopSorgenteDati ds = config.getMessaggiDiagnostici().getOpenspcoopSorgenteDati(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome", ds.getNome() , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome_jndi", ds.getNomeJndi() , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo_database", ds.getTipoDatabase() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idMsgDsAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDBLib.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.MSG_DIAGN_DS, CostantiDB.MSG_DIAGN_DS_COLUMN_ID, 
										CostantiDB.MSG_DIAGN_DS_SEQUENCE, CostantiDB.MSG_DIAGN_DS_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idMsgDsAppender<=0){
							throw new DriverConfigurazioneException("ID (msgdiag ds appender) autoincrementale non ottenuto");
						}

						for(int l=0; l<ds.sizePropertyList();l++){
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
							sqlQueryObject.addInsertTable(CostantiDB.MSG_DIAGN_DS_PROP);
							sqlQueryObject.addInsertField("id_prop", "?");
							sqlQueryObject.addInsertField("nome", "?");
							sqlQueryObject.addInsertField("valore", "?");
							updateQuery = sqlQueryObject.createSQLInsert();
							updateStmt = con.prepareStatement(updateQuery);
							updateStmt.setLong(1, idMsgDsAppender);
							updateStmt.setString(2, ds.getProperty(l).getNome());
							updateStmt.setString(3, ds.getProperty(l).getValore());
							updateStmt.executeUpdate();
							updateStmt.close();
						}

					}
				}

				// delete from tracce ds prop
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.TRACCIAMENTO_DS_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from tracce ds
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.TRACCIAMENTO_DS);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into tracce ds
				if(config.getTracciamento()!=null){
					for(int k=0; k<config.getTracciamento().sizeOpenspcoopSorgenteDatiList();k++){
						OpenspcoopSorgenteDati ds = config.getTracciamento().getOpenspcoopSorgenteDati(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome", ds.getNome() , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome_jndi", ds.getNomeJndi() , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo_database", ds.getTipoDatabase() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idTracceDsAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDBLib.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.TRACCIAMENTO_DS, CostantiDB.TRACCIAMENTO_DS_COLUMN_ID, 
										CostantiDB.TRACCIAMENTO_DS_SEQUENCE, CostantiDB.TRACCIAMENTO_DS_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idTracceDsAppender<=0){
							throw new DriverConfigurazioneException("ID (tracciamento ds appender) autoincrementale non ottenuto");
						}

						for(int l=0; l<ds.sizePropertyList();l++){
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
							sqlQueryObject.addInsertTable(CostantiDB.TRACCIAMENTO_DS_PROP);
							sqlQueryObject.addInsertField("id_prop", "?");
							sqlQueryObject.addInsertField("nome", "?");
							sqlQueryObject.addInsertField("valore", "?");
							updateQuery = sqlQueryObject.createSQLInsert();
							updateStmt = con.prepareStatement(updateQuery);
							updateStmt.setLong(1, idTracceDsAppender);
							updateStmt.setString(2, ds.getProperty(l).getNome());
							updateStmt.setString(3, ds.getProperty(l).getValore());
							updateStmt.executeUpdate();
							updateStmt.close();
						}

					}
				}
				
				// canali
				if(configurazioneCanali!=null) {
					DriverConfigurazioneDB_canaliLIB.CRUDCanaliConfigurazione(type, con, configurazioneCanali);
				}
				
				// handlers
				if(configHandlers!=null) {
					if(configHandlers.getRequest()!=null) {
						DriverConfigurazioneDB_handlerLIB.CRUDConfigurazioneMessageHandlers(type, con, null, null, true, configHandlers.getRequest());
					}
					if(configHandlers.getResponse()!=null) {
						DriverConfigurazioneDB_handlerLIB.CRUDConfigurazioneMessageHandlers(type, con, null, null, false, configHandlers.getResponse());
					}
					if(configHandlers.getService()!=null) {
						DriverConfigurazioneDB_handlerLIB.CRUDConfigurazioneServiceHandlers(type, con, null, null, false, configHandlers.getService());
					}
				}
				
				// ExtendedInfo
				if(extInfoConfigurazioneDriver!=null &&			
					config.sizeExtendedInfoList()>0){
					for(int l=0; l<config.sizeExtendedInfoList();l++){
						extInfoConfigurazioneDriver.deleteExtendedInfo(con, DriverConfigurazioneDBLib.log,  config, config.getExtendedInfo(l), CRUDType.CREATE);
						extInfoConfigurazioneDriver.createExtendedInfo(con, DriverConfigurazioneDBLib.log,  config, config.getExtendedInfo(l), CRUDType.CREATE);
					}
				}


				break;
			case UPDATE:
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.CONFIGURAZIONE);
				sqlQueryObject.addUpdateField("cadenza_inoltro", "?");
				sqlQueryObject.addUpdateField("validazione_stato", "?");
				sqlQueryObject.addUpdateField("validazione_controllo", "?");
				sqlQueryObject.addUpdateField("msg_diag_severita", "?");
				sqlQueryObject.addUpdateField("msg_diag_severita_log4j", "?");
				sqlQueryObject.addUpdateField("auth_integration_manager", "?");
				sqlQueryObject.addUpdateField("validazione_profilo", "?");
				sqlQueryObject.addUpdateField("mod_risposta", "?");
				sqlQueryObject.addUpdateField("indirizzo_telematico", "?");
				sqlQueryObject.addUpdateField("routing_enabled", "?");
				sqlQueryObject.addUpdateField("gestione_manifest", "?");
				sqlQueryObject.addUpdateField("validazione_manifest", "?");
				sqlQueryObject.addUpdateField("tracciamento_buste", "?");
				sqlQueryObject.addUpdateField("tracciamento_esiti", "?");
				sqlQueryObject.addUpdateField("transazioni_tempi", "?");
				sqlQueryObject.addUpdateField("transazioni_token", "?");
				sqlQueryObject.addUpdateField("dump", "?");
				sqlQueryObject.addUpdateField("dump_bin_pd", "?");
				sqlQueryObject.addUpdateField("dump_bin_pa", "?");
				sqlQueryObject.addUpdateField("validazione_contenuti_stato", "?");
				sqlQueryObject.addUpdateField("validazione_contenuti_tipo", "?");
				sqlQueryObject.addUpdateField("validazione_contenuti_mtom", "?");
				// registro cache
				sqlQueryObject.addUpdateField("statocache", "?");
				sqlQueryObject.addUpdateField("dimensionecache", "?");
				sqlQueryObject.addUpdateField("algoritmocache", "?");
				sqlQueryObject.addUpdateField("idlecache", "?");
				sqlQueryObject.addUpdateField("lifecache", "?");
				// config cache
				sqlQueryObject.addUpdateField("config_statocache", "?");
				sqlQueryObject.addUpdateField("config_dimensionecache", "?");
				sqlQueryObject.addUpdateField("config_algoritmocache", "?");
				sqlQueryObject.addUpdateField("config_idlecache", "?");
				sqlQueryObject.addUpdateField("config_lifecache", "?");
				// authz cache
				sqlQueryObject.addUpdateField("auth_statocache", "?");
				sqlQueryObject.addUpdateField("auth_dimensionecache", "?");
				sqlQueryObject.addUpdateField("auth_algoritmocache", "?");
				sqlQueryObject.addUpdateField("auth_idlecache", "?");
				sqlQueryObject.addUpdateField("auth_lifecache", "?");
				// authn cache
				sqlQueryObject.addUpdateField("authn_statocache", "?");
				sqlQueryObject.addUpdateField("authn_dimensionecache", "?");
				sqlQueryObject.addUpdateField("authn_algoritmocache", "?");
				sqlQueryObject.addUpdateField("authn_idlecache", "?");
				sqlQueryObject.addUpdateField("authn_lifecache", "?");
				// token cache
				sqlQueryObject.addUpdateField("token_statocache", "?");
				sqlQueryObject.addUpdateField("token_dimensionecache", "?");
				sqlQueryObject.addUpdateField("token_algoritmocache", "?");
				sqlQueryObject.addUpdateField("token_idlecache", "?");
				sqlQueryObject.addUpdateField("token_lifecache", "?");
				// aa cache
				sqlQueryObject.addUpdateField("aa_statocache", "?");
				sqlQueryObject.addUpdateField("aa_dimensionecache", "?");
				sqlQueryObject.addUpdateField("aa_algoritmocache", "?");
				sqlQueryObject.addUpdateField("aa_idlecache", "?");
				sqlQueryObject.addUpdateField("aa_lifecache", "?");
				// keystore cache
				sqlQueryObject.addUpdateField("keystore_statocache", "?");
				sqlQueryObject.addUpdateField("keystore_dimensionecache", "?");
				sqlQueryObject.addUpdateField("keystore_algoritmocache", "?");
				sqlQueryObject.addUpdateField("keystore_idlecache", "?");
				sqlQueryObject.addUpdateField("keystore_lifecache", "?");
				sqlQueryObject.addUpdateField("keystore_crl_lifecache", "?");
				// multitenant
				sqlQueryObject.addUpdateField("multitenant_stato", "?");
				sqlQueryObject.addUpdateField("multitenant_fruizioni", "?");
				sqlQueryObject.addUpdateField("multitenant_erogazioni", "?");
				// cors
				sqlQueryObject.addUpdateField("cors_stato", "?");
				sqlQueryObject.addUpdateField("cors_tipo", "?");
				sqlQueryObject.addUpdateField("cors_all_allow_origins", "?");
				sqlQueryObject.addUpdateField("cors_all_allow_methods", "?");
				sqlQueryObject.addUpdateField("cors_all_allow_headers", "?");
				sqlQueryObject.addUpdateField("cors_allow_credentials", "?");
				sqlQueryObject.addUpdateField("cors_allow_max_age", "?");
				sqlQueryObject.addUpdateField("cors_allow_max_age_seconds", "?");
				sqlQueryObject.addUpdateField("cors_allow_origins", "?");
				sqlQueryObject.addUpdateField("cors_allow_headers", "?");
				sqlQueryObject.addUpdateField("cors_allow_methods", "?");
				sqlQueryObject.addUpdateField("cors_allow_expose_headers", "?");
				// responseCaching
				sqlQueryObject.addUpdateField("response_cache_stato", "?");
				sqlQueryObject.addUpdateField("response_cache_seconds", "?");
				sqlQueryObject.addUpdateField("response_cache_max_msg_size", "?");
				sqlQueryObject.addUpdateField("response_cache_control_nocache", "?");
				sqlQueryObject.addUpdateField("response_cache_control_maxage", "?");
				sqlQueryObject.addUpdateField("response_cache_control_nostore", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_url", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_query", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_query_list", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_headers", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_hdr_list", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_payload", "?");
				// responseCaching cache
				sqlQueryObject.addUpdateField("response_cache_statocache", "?");
				sqlQueryObject.addUpdateField("response_cache_dimensionecache", "?");
				sqlQueryObject.addUpdateField("response_cache_algoritmocache", "?");
				sqlQueryObject.addUpdateField("response_cache_idlecache", "?");
				sqlQueryObject.addUpdateField("response_cache_lifecache", "?");
				// consegna applicativi cache
				sqlQueryObject.addUpdateField("consegna_statocache", "?");
				sqlQueryObject.addUpdateField("consegna_dimensionecache", "?");
				sqlQueryObject.addUpdateField("consegna_algoritmocache", "?");
				sqlQueryObject.addUpdateField("consegna_idlecache", "?");
				sqlQueryObject.addUpdateField("consegna_lifecache", "?");
				// canali
				sqlQueryObject.addUpdateField("canali_stato", "?");
				// dati richieste cache
				sqlQueryObject.addUpdateField("dati_richieste_statocache", "?");
				sqlQueryObject.addUpdateField("dati_richieste_dimensionecache", "?");
				sqlQueryObject.addUpdateField("dati_richieste_algoritmocache", "?");
				sqlQueryObject.addUpdateField("dati_richieste_idlecache", "?");
				sqlQueryObject.addUpdateField("dati_richieste_lifecache", "?");

				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);
				
				index = 1;
				
				updateStmt.setString(index++, cadenzaInoltro);
				updateStmt.setString(index++, valStato);
				updateStmt.setString(index++, valControllo);
				updateStmt.setString(index++, msgDiagSeverita);
				updateStmt.setString(index++, msgDiagSeveritaLog4j);
				updateStmt.setString(index++, autenticazione);
				updateStmt.setString(index++, valProfiloCollaborazione);
				updateStmt.setString(index++, modRisposta);
				updateStmt.setString(index++, utilizzoIndTelematico);
				updateStmt.setString(index++, routingEnabled);
				updateStmt.setString(index++, gestioneManifest);
				updateStmt.setString(index++, valManifest);
				updateStmt.setString(index++, tracciamentoBuste);
				updateStmt.setString(index++, tracciamentoEsiti);
				updateStmt.setString(index++, transazioniTempiElaborazione);
				updateStmt.setString(index++, transazioniToken);
				updateStmt.setString(index++, dumpApplicativo);
				updateStmt.setString(index++, dumpPD);
				updateStmt.setString(index++, dumpPA);
				updateStmt.setString(index++, validazioneContenutiStato);
				updateStmt.setString(index++, validazioneContenutiTipo);
				updateStmt.setString(index++, validazioneContenutiAcceptMtomMessage);
				// registro cache
				updateStmt.setString(index++, registroStatoCache);
				updateStmt.setString(index++, registroDimensioneCache);
				updateStmt.setString(index++, registroAlgoritmoCache);
				updateStmt.setString(index++, registroIdleCache);
				updateStmt.setString(index++, registroLifeCache);
				// config cache
				updateStmt.setString(index++, configStatoCache);
				updateStmt.setString(index++, configDimensioneCache);
				updateStmt.setString(index++, configAlgoritmoCache);
				updateStmt.setString(index++, configIdleCache);
				updateStmt.setString(index++, configLifeCache);
				// authz cache
				updateStmt.setString(index++, authzStatoCache);
				updateStmt.setString(index++, authzDimensioneCache);
				updateStmt.setString(index++, authzAlgoritmoCache);
				updateStmt.setString(index++, authzIdleCache);
				updateStmt.setString(index++, authzLifeCache);
				// authn cache
				updateStmt.setString(index++, authnStatoCache);
				updateStmt.setString(index++, authnDimensioneCache);
				updateStmt.setString(index++, authnAlgoritmoCache);
				updateStmt.setString(index++, authnIdleCache);
				updateStmt.setString(index++, authnLifeCache);
				// token cache
				updateStmt.setString(index++, tokenStatoCache);
				updateStmt.setString(index++, tokenDimensioneCache);
				updateStmt.setString(index++, tokenAlgoritmoCache);
				updateStmt.setString(index++, tokenIdleCache);
				updateStmt.setString(index++, tokenLifeCache);
				// aa cache
				updateStmt.setString(index++, aaStatoCache);
				updateStmt.setString(index++, aaDimensioneCache);
				updateStmt.setString(index++, aaAlgoritmoCache);
				updateStmt.setString(index++, aaIdleCache);
				updateStmt.setString(index++, aaLifeCache);
				// keystore cache
				updateStmt.setString(index++, keystoreStatoCache);
				updateStmt.setString(index++, keystoreDimensioneCache);
				updateStmt.setString(index++, keystoreAlgoritmoCache);
				updateStmt.setString(index++, keystoreIdleCache);
				updateStmt.setString(index++, keystoreLifeCache);
				updateStmt.setString(index++, keystoreCrlLifeCache);
				// multitenant
				updateStmt.setString(index++, multitenant!=null ? DriverConfigurazioneDBLib.getValue(multitenant.getStato()) : null);
				updateStmt.setString(index++, multitenant!=null ? DriverConfigurazioneDBLib.getValue(multitenant.getFruizioneSceltaSoggettiErogatori()) : null);
				updateStmt.setString(index++, multitenant!=null ? DriverConfigurazioneDBLib.getValue(multitenant.getErogazioneSceltaSoggettiFruitori()) : null);
				// cors
				updateStmt.setString(index++, corsStato);
				updateStmt.setString(index++, corsTipo);
				updateStmt.setString(index++, corsAllAllowOrigins);
				updateStmt.setString(index++, corsAllAllowMethods);
				updateStmt.setString(index++, corsAllAllowHeaders);
				updateStmt.setString(index++, corsAllowCredentials);
				updateStmt.setInt(index++, corsAllowMaxAge);
				if(corsAllowMaxAgeSeconds!=null) {
					updateStmt.setInt(index++, corsAllowMaxAgeSeconds);
				}
				else {
					updateStmt.setNull(index++, java.sql.Types.INTEGER);
				}
				updateStmt.setString(index++, corsAllowOrigins);
				updateStmt.setString(index++, corsAllowHeaders);
				updateStmt.setString(index++, corsAllowMethods);
				updateStmt.setString(index++, corsAllowExposeHeaders);				
				// responseCaching
				updateStmt.setString(index++, responseCacheStato);
				if(responseCacheSeconds!=null) {
					updateStmt.setInt(index++, responseCacheSeconds);
				}
				else {
					updateStmt.setNull(index++, java.sql.Types.INTEGER);
				}
				if(responseCacheMaxMsgSize!=null) {
					updateStmt.setLong(index++, responseCacheMaxMsgSize);
				}
				else {
					updateStmt.setNull(index++, java.sql.Types.BIGINT);
				}
				updateStmt.setInt(index++, responseCacheNoCache ? CostantiDB.TRUE : CostantiDB.FALSE);
				updateStmt.setInt(index++, responseCacheMaxAge ? CostantiDB.TRUE : CostantiDB.FALSE);
				updateStmt.setInt(index++, responseCacheNoStore ? CostantiDB.TRUE : CostantiDB.FALSE);
				updateStmt.setString(index++, responseCacheHashUrl);
				updateStmt.setString(index++, responseCacheHashQuery);
				updateStmt.setString(index++, responseCacheHashQueryList);
				updateStmt.setString(index++, responseCacheHashHeaders);
				updateStmt.setString(index++, responseCacheHashHeadersList);
				updateStmt.setString(index++, responseCacheHashPayload);
				// responseCaching cache
				updateStmt.setString(index++, responseCachingStatoCache);
				updateStmt.setString(index++, responseCachingDimensioneCache);
				updateStmt.setString(index++, responseCachingAlgoritmoCache);
				updateStmt.setString(index++, responseCachingIdleCache);
				updateStmt.setString(index++, responseCachingLifeCache);
				// consegna applicativi cache
				updateStmt.setString(index++, consegnaStatoCache);
				updateStmt.setString(index++, consegnaDimensioneCache);
				updateStmt.setString(index++, consegnaAlgoritmoCache);
				updateStmt.setString(index++, consegnaIdleCache);
				updateStmt.setString(index++, consegnaLifeCache);
				// canali
				updateStmt.setString(index++, configurazioneCanaliStato);
				// dati richieste cache
				updateStmt.setString(index++, datiRichiesteStatoCache);
				updateStmt.setString(index++, datiRichiesteDimensioneCache);
				updateStmt.setString(index++, datiRichiesteAlgoritmoCache);
				updateStmt.setString(index++, datiRichiesteIdleCache);
				updateStmt.setString(index++, datiRichiesteLifeCache);
				
				DriverConfigurazioneDBLib.logDebug("eseguo query :" + 
						DBUtils.formatSQLString(updateQuery, 
								cadenzaInoltro, 
								valStato, valControllo, 
								msgDiagSeverita, msgDiagSeveritaLog4j, 
								autenticazione, 
								valProfiloCollaborazione, 
								modRisposta, utilizzoIndTelematico, 
								routingEnabled, gestioneManifest, 
								valManifest, 
								tracciamentoBuste, transazioniTempiElaborazione, transazioniToken, dumpApplicativo, dumpPD, dumpPA,
								validazioneContenutiStato,validazioneContenutiTipo,
								registroStatoCache, registroDimensioneCache, registroAlgoritmoCache, registroIdleCache, registroLifeCache,
								configStatoCache, configDimensioneCache, configAlgoritmoCache, configIdleCache, configLifeCache,
								authzStatoCache, authzDimensioneCache, authzAlgoritmoCache, authzIdleCache, authzLifeCache,
								authnStatoCache, authnDimensioneCache, authnAlgoritmoCache, authnIdleCache, authnLifeCache,
								tokenStatoCache, tokenDimensioneCache, tokenAlgoritmoCache, tokenIdleCache, tokenLifeCache,
								keystoreStatoCache, keystoreDimensioneCache, keystoreAlgoritmoCache, keystoreIdleCache, keystoreLifeCache, keystoreCrlLifeCache,
								(multitenant!=null ? DriverConfigurazioneDBLib.getValue(multitenant.getStato()) : null),
								(multitenant!=null ? DriverConfigurazioneDBLib.getValue(multitenant.getFruizioneSceltaSoggettiErogatori()) : null),
								(multitenant!=null ? DriverConfigurazioneDBLib.getValue(multitenant.getErogazioneSceltaSoggettiFruitori()) : null),
								corsStato, corsTipo, corsAllAllowOrigins, corsAllAllowMethods, corsAllAllowHeaders, corsAllowCredentials, corsAllowMaxAge, corsAllowMaxAgeSeconds,
								corsAllowOrigins, corsAllowHeaders, corsAllowMethods, corsAllowExposeHeaders,
								responseCacheStato, responseCacheSeconds, responseCacheMaxMsgSize, 
								responseCacheHashUrl, responseCacheHashQuery, responseCacheHashQueryList, responseCacheHashHeaders, responseCacheHashHeadersList, responseCacheHashPayload,
								responseCachingStatoCache, responseCachingDimensioneCache, responseCachingAlgoritmoCache, responseCachingIdleCache, responseCachingLifeCache,
								consegnaStatoCache, consegnaDimensioneCache, consegnaAlgoritmoCache, consegnaIdleCache, consegnaLifeCache,
								configurazioneCanaliStato,
								datiRichiesteStatoCache, datiRichiesteDimensioneCache, datiRichiesteAlgoritmoCache, datiRichiesteIdleCache, datiRichiesteLifeCache
								));

				n = updateStmt.executeUpdate();
				updateStmt.close();

				// delete from config_url_invocazione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIG_URL_REGOLE);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIG_URL_INVOCAZIONE);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				// insert into config_protocolli
				if(config.getUrlInvocazione()!=null){
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.CONFIG_URL_INVOCAZIONE);
					sqlQueryObject.addInsertField("base_url", "?");
					sqlQueryObject.addInsertField("base_url_fruizione", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);
					int indexP = 1;
					updateStmt.setString(indexP++, config.getUrlInvocazione().getBaseUrl());
					updateStmt.setString(indexP++, config.getUrlInvocazione().getBaseUrlFruizione());
					updateStmt.executeUpdate();
					updateStmt.close();
					
					if(config.getUrlInvocazione().sizeRegolaList()>0){
						for(int k=0; k<config.getUrlInvocazione().sizeRegolaList();k++){
							ConfigurazioneUrlInvocazioneRegola configUrlInvocazioneRegola = config.getUrlInvocazione().getRegola(k);
							createUrlInvocazioneRegolaEngine(configUrlInvocazioneRegola, con);
						}
					}
				}
				
				// delete from msg diag appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.MSG_DIAGN_APPENDER_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from msg diag appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.MSG_DIAGN_APPENDER);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into msg diag appender
				if(config.getMessaggiDiagnostici()!=null){
					for(int k=0; k<config.getMessaggiDiagnostici().sizeOpenspcoopAppenderList();k++){
						OpenspcoopAppender appender = config.getMessaggiDiagnostici().getOpenspcoopAppender(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo", appender.getTipo() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idMsgDiagAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDBLib.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.MSG_DIAGN_APPENDER, CostantiDB.MSG_DIAGN_APPENDER_COLUMN_ID, 
										CostantiDB.MSG_DIAGN_APPENDER_SEQUENCE, CostantiDB.MSG_DIAGN_APPENDER_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idMsgDiagAppender<=0){
							throw new DriverConfigurazioneException("ID (msg diag appender) autoincrementale non ottenuto");
						}

						for(int l=0; l<appender.sizePropertyList();l++){
							DriverConfigurazioneDBLib.logDebug("INSERT INTO "+CostantiDB.MSG_DIAGN_APPENDER_PROP+" (id_appender,nome,valore) VALUES ("+idMsgDiagAppender+","+appender.getProperty(l).getNome()+","+appender.getProperty(l).getValore()+")");
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
							sqlQueryObject.addInsertTable(CostantiDB.MSG_DIAGN_APPENDER_PROP);
							sqlQueryObject.addInsertField("id_appender", "?");
							sqlQueryObject.addInsertField("nome", "?");
							sqlQueryObject.addInsertField("valore", "?");
							updateQuery = sqlQueryObject.createSQLInsert();
							updateStmt = con.prepareStatement(updateQuery);
							updateStmt.setLong(1, idMsgDiagAppender);
							updateStmt.setString(2, appender.getProperty(l).getNome());
							updateStmt.setString(3, appender.getProperty(l).getValore());
							updateStmt.executeUpdate();
							updateStmt.close();
						}

					}
				}

				// delete from tracciamento appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.TRACCIAMENTO_APPENDER_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from tracciamento appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.TRACCIAMENTO_APPENDER);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into tracciamento appender
				if(config.getTracciamento()!=null){
					for(int k=0; k<config.getTracciamento().sizeOpenspcoopAppenderList();k++){
						OpenspcoopAppender appender = config.getTracciamento().getOpenspcoopAppender(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo", appender.getTipo() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idTracceAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDBLib.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.TRACCIAMENTO_APPENDER, CostantiDB.TRACCIAMENTO_APPENDER_COLUMN_ID, 
										CostantiDB.TRACCIAMENTO_APPENDER_SEQUENCE, CostantiDB.TRACCIAMENTO_APPENDER_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idTracceAppender<=0){
							throw new DriverConfigurazioneException("ID (tracce appender) autoincrementale non ottenuto");
						}

						for(int l=0; l<appender.sizePropertyList();l++){
							DriverConfigurazioneDBLib.logDebug("INSERT INTO "+CostantiDB.TRACCIAMENTO_APPENDER_PROP+" (id_appender,nome,valore) VALUES ("+idTracceAppender+","+appender.getProperty(l).getNome()+","+appender.getProperty(l).getValore()+")");
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
							sqlQueryObject.addInsertTable(CostantiDB.TRACCIAMENTO_APPENDER_PROP);
							sqlQueryObject.addInsertField("id_appender", "?");
							sqlQueryObject.addInsertField("nome", "?");
							sqlQueryObject.addInsertField("valore", "?");
							updateQuery = sqlQueryObject.createSQLInsert();
							updateStmt = con.prepareStatement(updateQuery);
							updateStmt.setLong(1, idTracceAppender);
							updateStmt.setString(2, appender.getProperty(l).getNome());
							updateStmt.setString(3, appender.getProperty(l).getValore());
							updateStmt.executeUpdate();
							updateStmt.close();
						}

					}
				}
				
				
				// delete from dump appender_prop
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.DUMP_APPENDER_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from dump appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.DUMP_APPENDER);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into dump appender
				if(config.getDump()!=null){
					for(int k=0; k<config.getDump().sizeOpenspcoopAppenderList();k++){
						OpenspcoopAppender appender = config.getDump().getOpenspcoopAppender(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo", appender.getTipo() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idDumpAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDBLib.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.DUMP_APPENDER, CostantiDB.DUMP_APPENDER_COLUMN_ID, 
										CostantiDB.DUMP_APPENDER_SEQUENCE, CostantiDB.DUMP_APPENDER_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idDumpAppender<=0){
							throw new DriverConfigurazioneException("ID (dump appender) autoincrementale non ottenuto");
						}

						for(int l=0; l<appender.sizePropertyList();l++){
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
							sqlQueryObject.addInsertTable(CostantiDB.DUMP_APPENDER_PROP);
							sqlQueryObject.addInsertField("id_appender", "?");
							sqlQueryObject.addInsertField("nome", "?");
							sqlQueryObject.addInsertField("valore", "?");
							updateQuery = sqlQueryObject.createSQLInsert();
							updateStmt = con.prepareStatement(updateQuery);
							updateStmt.setLong(1, idDumpAppender);
							updateStmt.setString(2, appender.getProperty(l).getNome());
							updateStmt.setString(3, appender.getProperty(l).getValore());
							updateStmt.executeUpdate();
							updateStmt.close();
						}

					}
				}
				
				// Cache Regole
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIGURAZIONE_CACHE_REGOLE);
				String sqlQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(sqlQuery);
				n=updateStmt.executeUpdate();
				updateStmt.close();
				DriverConfigurazioneDBLib.logDebug("Cancellati "+n+" regole di cache");
				
				n=0;
				if(responseCacheRegole!=null && !responseCacheRegole.isEmpty()){
					for (int j = 0; j < responseCacheRegole.size(); j++) {
						ResponseCachingConfigurazioneRegola regola = responseCacheRegole.get(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.CONFIGURAZIONE_CACHE_REGOLE);
						if(regola.getReturnCodeMin()!=null && regola.getReturnCodeMin()>0) {
							sqlQueryObject.addInsertField("status_min", "?");
						}
						if(regola.getReturnCodeMax()!=null && regola.getReturnCodeMax()>0) {
							sqlQueryObject.addInsertField("status_max", "?");
						}
						sqlQueryObject.addInsertField("fault", "?");
						if(regola.getCacheTimeoutSeconds()!=null && regola.getCacheTimeoutSeconds()>0) {
							sqlQueryObject.addInsertField("cache_seconds", "?");
						}
						sqlQuery = sqlQueryObject.createSQLInsert();
						updateStmt = con.prepareStatement(sqlQuery);
						int indexCR = 1;
						if(regola.getReturnCodeMin()!=null && regola.getReturnCodeMin()>0) {
							updateStmt.setInt(indexCR++, regola.getReturnCodeMin());
						}
						if(regola.getReturnCodeMax()!=null && regola.getReturnCodeMax()>0) {
							updateStmt.setInt(indexCR++, regola.getReturnCodeMax());
						}
						updateStmt.setInt(indexCR++, regola.getFault() ? CostantiDB.TRUE : CostantiDB.FALSE);
						if(regola.getCacheTimeoutSeconds()!=null && regola.getCacheTimeoutSeconds()>0) {
							updateStmt.setInt(indexCR++, regola.getCacheTimeoutSeconds());
						}
						updateStmt.executeUpdate();
						updateStmt.close();
						n++;
						DriverConfigurazioneDBLib.logDebug("Aggiunta regola di cache");
					}
				}
				
				DriverConfigurazioneDBLib.logDebug("Aggiunte " + n + " regole di cache");
				
				// dumpConfigurazione
				
				// per backward compatibility elimino una config esistente
				dumpConfigOldBackwardCompatibility = DriverConfigurazioneDB_dumpLIB.readDumpConfigurazione(con, null, CostantiDB.OLD_BACKWARD_COMPATIBILITY_DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG);
				if(dumpConfigOldBackwardCompatibility!=null) {
					DriverConfigurazioneDB_dumpLIB.CRUDDumpConfigurazione(DELETE, con, dumpConfigOldBackwardCompatibility, null, CostantiDB.OLD_BACKWARD_COMPATIBILITY_DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG);
				}
				
				DriverConfigurazioneDB_dumpLIB.CRUDDumpConfigurazione(type, con, dumpConfigPA, null, CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG_PA);
				DriverConfigurazioneDB_dumpLIB.CRUDDumpConfigurazione(type, con, dumpConfigPD, null, CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG_PD);
				

				// delete from msgdiag ds prop
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.MSG_DIAGN_DS_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from msgdiag ds
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.MSG_DIAGN_DS);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into msgdiag ds
				if(config.getMessaggiDiagnostici()!=null){
					for(int k=0; k<config.getMessaggiDiagnostici().sizeOpenspcoopSorgenteDatiList();k++){
						OpenspcoopSorgenteDati ds = config.getMessaggiDiagnostici().getOpenspcoopSorgenteDati(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome", ds.getNome() , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome_jndi", ds.getNomeJndi() , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo_database", ds.getTipoDatabase() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idMsgDsAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDBLib.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.MSG_DIAGN_DS, CostantiDB.MSG_DIAGN_DS_COLUMN_ID, 
										CostantiDB.MSG_DIAGN_DS_SEQUENCE, CostantiDB.MSG_DIAGN_DS_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idMsgDsAppender<=0){
							throw new DriverConfigurazioneException("ID (msgdiag ds appender) autoincrementale non ottenuto");
						}

						for(int l=0; l<ds.sizePropertyList();l++){
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
							sqlQueryObject.addInsertTable(CostantiDB.MSG_DIAGN_DS_PROP);
							sqlQueryObject.addInsertField("id_prop", "?");
							sqlQueryObject.addInsertField("nome", "?");
							sqlQueryObject.addInsertField("valore", "?");
							updateQuery = sqlQueryObject.createSQLInsert();
							updateStmt = con.prepareStatement(updateQuery);
							updateStmt.setLong(1, idMsgDsAppender);
							updateStmt.setString(2, ds.getProperty(l).getNome());
							updateStmt.setString(3, ds.getProperty(l).getValore());
							updateStmt.executeUpdate();
							updateStmt.close();
						}

					}
				}

				// delete from tracce ds prop
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.TRACCIAMENTO_DS_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from tracce ds
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.TRACCIAMENTO_DS);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into tracce ds
				if(config.getTracciamento()!=null){
					for(int k=0; k<config.getTracciamento().sizeOpenspcoopSorgenteDatiList();k++){
						OpenspcoopSorgenteDati ds = config.getTracciamento().getOpenspcoopSorgenteDati(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome", ds.getNome() , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome_jndi", ds.getNomeJndi() , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo_database", ds.getTipoDatabase() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idTracceDsAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDBLib.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.TRACCIAMENTO_DS, CostantiDB.TRACCIAMENTO_DS_COLUMN_ID, 
										CostantiDB.TRACCIAMENTO_DS_SEQUENCE, CostantiDB.TRACCIAMENTO_DS_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idTracceDsAppender<=0){
							throw new DriverConfigurazioneException("ID (tracciamento ds appender) autoincrementale non ottenuto");
						}

						for(int l=0; l<ds.sizePropertyList();l++){
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
							sqlQueryObject.addInsertTable(CostantiDB.TRACCIAMENTO_DS_PROP);
							sqlQueryObject.addInsertField("id_prop", "?");
							sqlQueryObject.addInsertField("nome", "?");
							sqlQueryObject.addInsertField("valore", "?");
							updateQuery = sqlQueryObject.createSQLInsert();
							updateStmt = con.prepareStatement(updateQuery);
							updateStmt.setLong(1, idTracceDsAppender);
							updateStmt.setString(2, ds.getProperty(l).getNome());
							updateStmt.setString(3, ds.getProperty(l).getValore());
							updateStmt.executeUpdate();
							updateStmt.close();
						}

					}
				}

				// Canali
				DriverConfigurazioneDB_canaliLIB.CRUDCanaliConfigurazione(type, con, configurazioneCanali);
				
				
				// Handlers
				DriverConfigurazioneDB_handlerLIB.CRUDConfigurazioneMessageHandlers(type, con, null, null, true, (configHandlers!=null) ? configHandlers.getRequest() : null);
				DriverConfigurazioneDB_handlerLIB.CRUDConfigurazioneMessageHandlers(type, con, null, null, false, (configHandlers!=null) ? configHandlers.getResponse() : null);
				DriverConfigurazioneDB_handlerLIB.CRUDConfigurazioneServiceHandlers(type, con, null, null, false, (configHandlers!=null) ? configHandlers.getService() : null);

				
				// ExtendedInfo
				if(extInfoConfigurazioneDriver!=null &&
						config.sizeExtendedInfoList()>0){
					for(int l=0; l<config.sizeExtendedInfoList();l++){
						extInfoConfigurazioneDriver.deleteExtendedInfo(con, DriverConfigurazioneDBLib.log,  config, config.getExtendedInfo(l), CRUDType.UPDATE);
						extInfoConfigurazioneDriver.createExtendedInfo(con, DriverConfigurazioneDBLib.log,  config, config.getExtendedInfo(l), CRUDType.UPDATE);
					}
				}
				

				break;

			case DELETE:
				
				if(config.sizeExtendedInfoList()>0){
					for(int l=0; l<config.sizeExtendedInfoList();l++){
						extInfoConfigurazioneDriver.deleteExtendedInfo(con, DriverConfigurazioneDBLib.log,  config, config.getExtendedInfo(l), CRUDType.UPDATE);
						extInfoConfigurazioneDriver.createExtendedInfo(con, DriverConfigurazioneDBLib.log,  config, config.getExtendedInfo(l), CRUDType.UPDATE);
					}
				}
				
				// Canali
				DriverConfigurazioneDB_canaliLIB.CRUDCanaliConfigurazione(type, con, configurazioneCanali);
				
				// Handlers
				DriverConfigurazioneDB_handlerLIB.CRUDConfigurazioneMessageHandlers(type, con, null, null, true, (configHandlers!=null) ? configHandlers.getRequest() : null);
				DriverConfigurazioneDB_handlerLIB.CRUDConfigurazioneMessageHandlers(type, con, null, null, false, (configHandlers!=null) ? configHandlers.getResponse() : null);
				DriverConfigurazioneDB_handlerLIB.CRUDConfigurazioneServiceHandlers(type, con, null, null, false, (configHandlers!=null) ? configHandlers.getService() : null);
				
				// Dump
				if(config.getDump()!=null) {
					if(config.getDump().getConfigurazionePortaApplicativa()!=null) {
						DriverConfigurazioneDB_dumpLIB.CRUDDumpConfigurazione(DELETE, con, config.getDump().getConfigurazionePortaApplicativa(), null, CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG_PA);
					}
					if(config.getDump().getConfigurazionePortaDelegata()!=null) {
						DriverConfigurazioneDB_dumpLIB.CRUDDumpConfigurazione(DELETE, con, config.getDump().getConfigurazionePortaApplicativa(), null, CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG_PD);
					}
				}
				
				// Cache
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIGURAZIONE_CACHE_REGOLE);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIGURAZIONE);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();
				break;

			}

			if (type == CostantiDB.CREATE)
				return idConfigurazione;
			else
				return n;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConfigurazioneGenerale] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConfigurazioneGenerale] Exception [" + se.getMessage() + "].",se);
		} finally {
			JDBCUtilities.closeResources(selectRS, selectStmt);
			JDBCUtilities.closeResources(updateStmt);
		}
	}

	
}
