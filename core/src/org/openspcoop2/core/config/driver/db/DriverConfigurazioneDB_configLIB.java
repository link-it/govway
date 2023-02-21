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
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.IExtendedInfo;
import org.openspcoop2.core.config.AccessoConfigurazione;
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
import org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKey;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKeyJDBCType;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKeyObject;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

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
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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

				DriverConfigurazioneDB_LIB.log.debug("eseguo query: " + DBUtils.formatSQLString(updateQuery, nome, location, tipo, user, password));

				n = updateStmt.executeUpdate();

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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

				DriverConfigurazioneDB_LIB.log.debug("eseguo query: " + DBUtils.formatSQLString(updateQuery, nome, location, tipo, user, password, idRegistro));
				n = updateStmt.executeUpdate();

				break;
			case DELETE:
				if (registro.getId() == null || registro.getId() <= 0)
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoRegistro(DELETE)] L'id del Servizio e' necessario.");
				idRegistro = registro.getId();

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.REGISTRI);
				sqlQueryObject.addWhereCondition("id=?");
				String sqlQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(sqlQuery);
				updateStmt.setLong(1, idRegistro);
				DriverConfigurazioneDB_LIB.log.debug("eseguo query: " + DBUtils.formatSQLString(updateQuery, idRegistro));
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

			try {
				if(selectRS!=null)
					selectRS.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(selectStmt!=null)
					selectStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(updateStmt!=null)
					updateStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
		}

	}

	public static long CRUDAccessoRegistro(int type, AccessoRegistro registro, Connection con) throws DriverConfigurazioneException {
		if (registro == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoRegistro] Il registro non può essere NULL");
		PreparedStatement updateStmt = null;
		String updateQuery = "";
		PreparedStatement selectStmt = null;
		//String selectQuery = "";
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

				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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

				DriverConfigurazioneDB_LIB.log.debug("eseguo query :" + DBUtils.formatSQLString(updateQuery, statoCache, dimensionecache, algoritmocache, idlecache, lifecache));

				n = updateStmt.executeUpdate();
				updateStmt.close();

				// Elimino i registri e li ricreo
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.REGISTRI);
				String sqlQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(sqlQuery);
				DriverConfigurazioneDB_LIB.log.debug("eseguo query: " + DBUtils.formatSQLString(updateQuery));
				int risultato = updateStmt.executeUpdate();
				DriverConfigurazioneDB_LIB.log.debug("eseguo query risultato["+risultato+"]: " + DBUtils.formatSQLString(updateQuery));
				updateStmt.close();

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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
					DriverConfigurazioneDB_LIB.log.debug("eseguo query INSERT INTO " + CostantiDB.REGISTRI + "(nome, location, tipo, utente, password) VALUES ("+
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

			try {
				if(selectRS!=null)
					selectRS.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(selectStmt!=null)
					selectStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(updateStmt!=null)
					updateStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
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

				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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

				DriverConfigurazioneDB_LIB.log.debug("eseguo query :" + DBUtils.formatSQLString(updateQuery, statoCache, dimensionecache, algoritmocache, idlecache, lifecache));

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
			try {
				if(updateStmt!=null)
					updateStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
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

				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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

				DriverConfigurazioneDB_LIB.log.debug("eseguo query :" + DBUtils.formatSQLString(updateQuery, statoCache, dimensionecache, algoritmocache, idlecache, lifecache));

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
			try {
				if(updateStmt!=null)
					updateStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
		}

	}
	
	
	public static void CRUDServiziPdD(int type, StatoServiziPdd statoServiziPdD, Connection con) throws DriverConfigurazioneException {
		if (statoServiziPdD == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDServiziPdD] Le configurazioni del servizio non possono essere NULL");
		PreparedStatement updateStmt = null;
		String updateQuery = "";
		PreparedStatement selectStmt = null;
		//String selectQuery = "";
		ResultSet selectRS = null;
		

		try {
			switch (type) {
			case CREATE:
			case UPDATE:

				// Elimino le configurazioni e le ricreo
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_PDD_FILTRI);
				String sqlQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(sqlQuery);
				DriverConfigurazioneDB_LIB.log.debug("eseguo query: " + DBUtils.formatSQLString(updateQuery));
				int risultato = updateStmt.executeUpdate();
				DriverConfigurazioneDB_LIB.log.debug("eseguo query risultato["+risultato+"]: " + DBUtils.formatSQLString(updateQuery));
				updateStmt.close();

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_PDD);
				sqlQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(sqlQuery);
				DriverConfigurazioneDB_LIB.log.debug("eseguo query: " + DBUtils.formatSQLString(updateQuery));
				risultato = updateStmt.executeUpdate();
				DriverConfigurazioneDB_LIB.log.debug("eseguo query risultato["+risultato+"]: " + DBUtils.formatSQLString(updateQuery));
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

			try {
				if(selectRS!=null)
					selectRS.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(selectStmt!=null)
					selectStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(updateStmt!=null)
					updateStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
		}

	}

	private static void registraComponentePdD(String componente,int stato,Connection con,
			List<TipoFiltroAbilitazioneServizi> abilitazioni,
			List<TipoFiltroAbilitazioneServizi> disabilitazioni) throws Exception{
		PreparedStatement updateStmt = null;
		String updateQuery = "";
		PreparedStatement selectStmt = null;
		//String selectQuery = "";
		ResultSet selectRS = null;
		

		try {
			
			// registro componente
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
			sqlQueryObject.addInsertTable(CostantiDB.SERVIZI_PDD);
			sqlQueryObject.addInsertField("componente", "?");
			sqlQueryObject.addInsertField("stato", "?");
			updateQuery = sqlQueryObject.createSQLInsert();
			updateStmt = con.prepareStatement(updateQuery);
			DriverConfigurazioneDB_LIB.log.debug("eseguo query INSERT INTO " + CostantiDB.SERVIZI_PDD + "(componente, stato) VALUES ('"+
					componente+"', "+stato+")");
			updateStmt.setString(1, componente);
			updateStmt.setInt(2, stato);
			updateStmt.executeUpdate();
			updateStmt.close();
			
			// recuper id del componente
			long idComponente = -1;
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_PDD);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("componente=?");
			updateQuery = sqlQueryObject.createSQLQuery();
			selectStmt = con.prepareStatement(updateQuery);
			DriverConfigurazioneDB_LIB.log.debug("eseguo query ["+updateQuery+"] per il componente ["+componente+"]");
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
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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

			try {
				if(selectRS!=null)
					selectRS.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(selectStmt!=null)
					selectStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(updateStmt!=null)
					updateStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	
	
	public static void CRUDSystemPropertiesPdD(int type, SystemProperties systemProperties, Connection con) throws DriverConfigurazioneException {
		if (systemProperties == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDSystemPropertiesPdD] Le configurazioni per le system properties non possono essere NULL");
		PreparedStatement updateStmt = null;
		String updateQuery = "";
		PreparedStatement selectStmt = null;
		//String selectQuery = "";
		ResultSet selectRS = null;
		

		try {
			
			// Elimino le configurazioni e le ricreo per insert e update
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SYSTEM_PROPERTIES_PDD);
			String sqlQuery = sqlQueryObject.createSQLDelete();
			updateStmt = con.prepareStatement(sqlQuery);
			DriverConfigurazioneDB_LIB.log.debug("eseguo query: " + DBUtils.formatSQLString(updateQuery));
			int risultato = updateStmt.executeUpdate();
			DriverConfigurazioneDB_LIB.log.debug("eseguo query risultato["+risultato+"]: " + DBUtils.formatSQLString(updateQuery));
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
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.SYSTEM_PROPERTIES_PDD);
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);
					DriverConfigurazioneDB_LIB.log.debug("eseguo query INSERT INTO " + CostantiDB.SYSTEM_PROPERTIES_PDD + "(nome, valore) VALUES ('"+
							nome+"', "+valore+")");
					updateStmt.setString(1, nome);
					updateStmt.setString(2, valore);
					updateStmt.executeUpdate();
					updateStmt.close();
					
					// recuper id del componente
					long idComponente = -1;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.SYSTEM_PROPERTIES_PDD);
					sqlQueryObject.addSelectField("id");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQueryObject.addWhereCondition("nome=?");
					sqlQueryObject.addWhereCondition("valore=?");
					updateQuery = sqlQueryObject.createSQLQuery();
					selectStmt = con.prepareStatement(updateQuery);
					DriverConfigurazioneDB_LIB.log.debug("eseguo query ["+updateQuery+"] per la prop nome["+nome+"] valore["+valore+"]");
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

			try {
				if(selectRS!=null)
					selectRS.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(selectStmt!=null)
					selectStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(updateStmt!=null)
					updateStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
		}

	}
	
	
	
	
	public static void CRUDGenericProperties(int type, GenericProperties genericProperties, Connection con) throws DriverConfigurazioneException {
		if (genericProperties == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDGenericProperties] Le configurazioni per le generic properties non possono essere NULL");
		PreparedStatement updateStmt = null;
		String updateQuery = "";
		PreparedStatement selectStmt = null;
		//String selectQuery = "";
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
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIG_GENERIC_PROPERTY);
				sqlQueryObject.addWhereCondition("id_props=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idParent);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIG_GENERIC_PROPERTIES);
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

				List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome", genericProperties.getNome() , InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("descrizione", genericProperties.getDescrizione() , InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipologia", genericProperties.getTipologia() , InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo", genericProperties.getTipo() , InsertAndGeneratedKeyJDBCType.STRING) );
				
				long idProperties = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDB_LIB.tipoDB), 
						new CustomKeyGeneratorObject(CostantiDB.CONFIG_GENERIC_PROPERTIES, CostantiDB.CONFIG_GENERIC_PROPERTIES_COLUMN_ID, 
								CostantiDB.CONFIG_GENERIC_PROPERTIES_SEQUENCE, CostantiDB.CONFIG_GENERIC_PROPERTIES_TABLE_FOR_ID),
						listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
				if(idProperties<=0){
					throw new Exception("ID (Generic Properties) autoincrementale non ottenuto");
				}

				for(int l=0; l<genericProperties.sizePropertyList();l++){
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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

			try {
				if(selectRS!=null)
					selectRS.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(selectStmt!=null)
					selectStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(updateStmt!=null)
					updateStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
		}

	}
	

	private static void _createUrlInvocazioneRegola(ConfigurazioneUrlInvocazioneRegola regola, Connection con) throws Exception {
		PreparedStatement updateStmt = null;
		

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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
			updateStmt.setString(indexP++, DriverConfigurazioneDB_LIB.getValue(regola.getStato()));
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
			updateStmt.setString(indexP++, DriverConfigurazioneDB_LIB.getValue(regola.getRuolo()));
			updateStmt.setString(indexP++, DriverConfigurazioneDB_LIB.getValue(regola.getServiceBinding()));
			updateStmt.setString(indexP++, regola.getSoggetto()!=null ? regola.getSoggetto().getTipo() : null);
			updateStmt.setString(indexP++, regola.getSoggetto()!=null ? regola.getSoggetto().getNome() : null);
			updateStmt.executeUpdate();
			updateStmt.close();
			
		} finally {

			try {
				if(updateStmt!=null)
					updateStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	public static void CRUDUrlInvocazioneRegola(int type, ConfigurazioneUrlInvocazioneRegola regola, Connection con) throws DriverConfigurazioneException {
		if (regola == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDUrlInvocazioneRegola] La regola non può essere NULL");
		PreparedStatement updateStmt = null;
		String updateQuery = "";
		//String selectQuery = "";
		

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
				
				idParent = DBUtils.getUrlInvocazioneRegola(oldNome, con, DriverConfigurazioneDB_LIB.tipoDB);
				if(idParent<=0) {
					throw new DriverConfigurazioneException("Regola con nome '"+regola.getNome()+"' non trovata");
				}

				// delete
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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

				_createUrlInvocazioneRegola(regola, con);

				break;
			case DELETE:
				// non rimuovo in quanto gia fatto sopra.
				break;
			}


		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDRegistroPlugin] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDRegistroPlugin] Exception [" + se.getMessage() + "].",se);
		} finally {

			try {
				if(updateStmt!=null)
					updateStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
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
				(config.getGenericPropertiesList()==null || config.getGenericPropertiesList().size()<=0) &&
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
					}
					
					switch (type) {
					case CREATE:
					case UPDATE:
						//extInfoConfigurazioneDriver.deleteAllExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, crudType);												
						if(config.sizeExtendedInfoList()>0){
							for(int l=0; l<config.sizeExtendedInfoList();l++){
								extInfoConfigurazioneDriver.deleteExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, config.getExtendedInfo(l), crudType);
								extInfoConfigurazioneDriver.createExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, config.getExtendedInfo(l), crudType);
							}
						}
						break;
					case DELETE:
						//extInfoConfigurazioneDriver.deleteAllExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, crudType);
						if(config.sizeExtendedInfoList()>0){
							for(int l=0; l<config.sizeExtendedInfoList();l++){
								extInfoConfigurazioneDriver.deleteExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, config.getExtendedInfo(l), crudType);
							}
						}
						break;
					}
				}catch (Exception se) {
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConfigurazioneGenerale-Extended] Exception [" + se.getMessage() + "].",se);
				} 
				
			}
			
			return -1;
			
		}
		else{
			
			return _CRUDConfigurazioneGenerale(type, config, con);
			
		}
		
	}

	@SuppressWarnings("deprecation")
	private static DumpConfigurazione getDumpConfigurazioneDeprecated(Dump dump) {
		return dump.getConfigurazione();
	}
	
	private static long _CRUDConfigurazioneGenerale(int type, Configurazione config, Connection con) throws DriverConfigurazioneException {
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
		AccessoDatiKeystore aDatiKeystore = config.getAccessoDatiKeystore();
		AccessoDatiRichieste aDatiRichieste = config.getAccessoDatiRichieste();
		Attachments att = config.getAttachments();

		ConfigurazioneMultitenant multitenant = config.getMultitenant();
		
		CorsConfigurazione corsConfigurazione = config.getGestioneCors();
		String cors_stato = null;
		String cors_tipo = null; 
		String cors_all_allow_origins = null; 
		String cors_all_allow_methods = null; 
		String cors_all_allow_headers = null; 
		String cors_allow_credentials = null; 
		int cors_allow_max_age = CostantiDB.FALSE;
		Integer cors_allow_max_age_seconds = null;
		String cors_allow_origins = null; 
		String cors_allow_headers = null; 
		String cors_allow_methods = null; 
		String cors_allow_expose_headers = null; 
		if(corsConfigurazione!=null) {
			cors_stato = DriverConfigurazioneDB_LIB.getValue(corsConfigurazione.getStato());
			cors_tipo = DriverConfigurazioneDB_LIB.getValue(corsConfigurazione.getTipo());
			cors_all_allow_origins = DriverConfigurazioneDB_LIB.getValue(corsConfigurazione.getAccessControlAllAllowOrigins());
			cors_all_allow_methods = DriverConfigurazioneDB_LIB.getValue(corsConfigurazione.getAccessControlAllAllowMethods());
			cors_all_allow_headers = DriverConfigurazioneDB_LIB.getValue(corsConfigurazione.getAccessControlAllAllowHeaders());
			cors_allow_credentials = DriverConfigurazioneDB_LIB.getValue(corsConfigurazione.getAccessControlAllowCredentials());
			if(corsConfigurazione.getAccessControlMaxAge()!=null) {
				cors_allow_max_age = CostantiDB.TRUE;
				cors_allow_max_age_seconds = corsConfigurazione.getAccessControlMaxAge();	
			}
			if(corsConfigurazione.getAccessControlAllowOrigins()!=null && corsConfigurazione.getAccessControlAllowOrigins().sizeOriginList()>0) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < corsConfigurazione.getAccessControlAllowOrigins().sizeOriginList(); i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(corsConfigurazione.getAccessControlAllowOrigins().getOrigin(i));
				}
				cors_allow_origins = bf.toString();
			}
			if(corsConfigurazione.getAccessControlAllowHeaders()!=null && corsConfigurazione.getAccessControlAllowHeaders().sizeHeaderList()>0) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < corsConfigurazione.getAccessControlAllowHeaders().sizeHeaderList(); i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(corsConfigurazione.getAccessControlAllowHeaders().getHeader(i));
				}
				cors_allow_headers = bf.toString();
			}
			if(corsConfigurazione.getAccessControlAllowMethods()!=null && corsConfigurazione.getAccessControlAllowMethods().sizeMethodList()>0) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < corsConfigurazione.getAccessControlAllowMethods().sizeMethodList(); i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(corsConfigurazione.getAccessControlAllowMethods().getMethod(i));
				}
				cors_allow_methods = bf.toString();
			}
			if(corsConfigurazione.getAccessControlExposeHeaders()!=null && corsConfigurazione.getAccessControlExposeHeaders().sizeHeaderList()>0) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < corsConfigurazione.getAccessControlExposeHeaders().sizeHeaderList(); i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(corsConfigurazione.getAccessControlExposeHeaders().getHeader(i));
				}
				cors_allow_expose_headers = bf.toString();
			}
		}
		
		ResponseCachingConfigurazioneGenerale responseCachingConfigurazione = config.getResponseCaching();
		
		String response_cache_stato = null;
		Integer response_cache_seconds = null;
		Long response_cache_max_msg_size = null;
		String response_cache_hash_url = null;
		String response_cache_hash_query = null;
		String response_cache_hash_query_list = null;
		String response_cache_hash_headers = null;
		String response_cache_hash_headers_list = null;
		String response_cache_hash_payload = null;
		boolean response_cache_noCache = true;
		boolean response_cache_maxAge = true;
		boolean response_cache_noStore = true;
		List<ResponseCachingConfigurazioneRegola> response_cache_regole = null;
		if(responseCachingConfigurazione!=null && responseCachingConfigurazione.getConfigurazione()!=null) {
			response_cache_stato = DriverConfigurazioneDB_LIB.getValue(responseCachingConfigurazione.getConfigurazione().getStato());
			response_cache_seconds = responseCachingConfigurazione.getConfigurazione().getCacheTimeoutSeconds();
			response_cache_max_msg_size = responseCachingConfigurazione.getConfigurazione().getMaxMessageSize();
			if(responseCachingConfigurazione.getConfigurazione().getControl()!=null) {
				response_cache_noCache = responseCachingConfigurazione.getConfigurazione().getControl().getNoCache();
				response_cache_maxAge = responseCachingConfigurazione.getConfigurazione().getControl().getMaxAge();
				response_cache_noStore = responseCachingConfigurazione.getConfigurazione().getControl().getNoStore();
			}
			if(responseCachingConfigurazione.getConfigurazione().getHashGenerator()!=null) {
				response_cache_hash_url = DriverConfigurazioneDB_LIB.getValue(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getRequestUri());
				
				response_cache_hash_query = DriverConfigurazioneDB_LIB.getValue(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getQueryParameters());
				if(StatoFunzionalitaCacheDigestQueryParameter.SELEZIONE_PUNTUALE.equals(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getQueryParameters())) {
					if(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getQueryParameterList()!=null && responseCachingConfigurazione.getConfigurazione().getHashGenerator().sizeQueryParameterList()>0) {
						StringBuilder bf = new StringBuilder();
						for (int i = 0; i < responseCachingConfigurazione.getConfigurazione().getHashGenerator().sizeQueryParameterList(); i++) {
							if(i>0) {
								bf.append(",");
							}
							bf.append(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getQueryParameter(i));
						}
						response_cache_hash_query_list = bf.toString();
					}
				}
				
				response_cache_hash_headers = DriverConfigurazioneDB_LIB.getValue(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getHeaders());
				if(StatoFunzionalita.ABILITATO.equals(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getHeaders())) {
					if(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getHeaderList()!=null && responseCachingConfigurazione.getConfigurazione().getHashGenerator().sizeHeaderList()>0) {
						StringBuilder bf = new StringBuilder();
						for (int i = 0; i < responseCachingConfigurazione.getConfigurazione().getHashGenerator().sizeHeaderList(); i++) {
							if(i>0) {
								bf.append(",");
							}
							bf.append(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getHeader(i));
						}
						response_cache_hash_headers_list = bf.toString();
					}
				}
				
				response_cache_hash_payload = DriverConfigurazioneDB_LIB.getValue(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getPayload());
			}
			response_cache_regole = responseCachingConfigurazione.getConfigurazione().getRegolaList();
		}
		
		Cache responseCaching_cache = null;
		String responseCaching_dimensioneCache = null;
		String responseCaching_algoritmoCache = null;
		String responseCaching_idleCache = null;
		String responseCaching_lifeCache = null;
		String responseCaching_statoCache = null;
		if(responseCachingConfigurazione !=null){
			responseCaching_cache = responseCachingConfigurazione.getCache();

		}
		responseCaching_statoCache = (responseCaching_cache != null ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString());
		if (responseCaching_statoCache.equals(CostantiConfigurazione.ABILITATO.toString())) {
			responseCaching_dimensioneCache = responseCaching_cache.getDimensione();
			responseCaching_algoritmoCache = DriverConfigurazioneDB_LIB.getValue(responseCaching_cache.getAlgoritmo());
			responseCaching_idleCache = responseCaching_cache.getItemIdleTime();
			responseCaching_lifeCache = responseCaching_cache.getItemLifeSecond();
		}
		
		
		Cache consegna_cache = null;
		String consegna_dimensioneCache = null;
		String consegna_algoritmoCache = null;
		String consegna_idleCache = null;
		String consegna_lifeCache = null;
		String consegna_statoCache = null;
		if(config.getAccessoDatiConsegnaApplicativi() !=null){
			consegna_cache = config.getAccessoDatiConsegnaApplicativi().getCache();

		}
		consegna_statoCache = (consegna_cache != null ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString());
		if (consegna_statoCache.equals(CostantiConfigurazione.ABILITATO.toString())) {
			consegna_dimensioneCache = consegna_cache.getDimensione();
			consegna_algoritmoCache = DriverConfigurazioneDB_LIB.getValue(consegna_cache.getAlgoritmo());
			consegna_idleCache = consegna_cache.getItemIdleTime();
			consegna_lifeCache = consegna_cache.getItemLifeSecond();
		}
		
		String utilizzoIndTelematico = null;
		if(indirizzoPerRisposta!=null){
			utilizzoIndTelematico =	DriverConfigurazioneDB_LIB.getValue(indirizzoPerRisposta.getUtilizzo());
		}
		String cadenza_inoltro = null;
		if(inoltroBusteNonRiscontrate!=null){
			cadenza_inoltro = inoltroBusteNonRiscontrate.getCadenza();
		}
		String autenticazione = null;
		if(integrationManager!=null){
			autenticazione = integrationManager.getAutenticazione();
		}
		String msg_diag_severita = null;
		String msg_diag_severita_log4j = null;
		if(messaggiDiagnostici!=null){
			msg_diag_severita = DriverConfigurazioneDB_LIB.getValue(messaggiDiagnostici.getSeverita());
			msg_diag_severita_log4j = DriverConfigurazioneDB_LIB.getValue(messaggiDiagnostici.getSeveritaLog4j());
		}
		String val_controllo = null;
		String val_stato = null;
		String val_manifest = null;
		String val_profiloCollaborazione = null;
		if(validazioneBuste!=null){
			val_controllo = DriverConfigurazioneDB_LIB.getValue(validazioneBuste.getControllo());
			val_stato = DriverConfigurazioneDB_LIB.getValue(validazioneBuste.getStato());
			val_manifest = DriverConfigurazioneDB_LIB.getValue(validazioneBuste.getManifestAttachments());
			val_profiloCollaborazione = DriverConfigurazioneDB_LIB.getValue(validazioneBuste.getProfiloCollaborazione());
		}

		String gestioneManifest = null;
		if(att!=null){
			gestioneManifest = DriverConfigurazioneDB_LIB.getValue(att.getGestioneManifest());
		}

		Cache registro_cache = null;
		String registro_dimensioneCache = null;
		String registro_algoritmoCache = null;
		String registro_idleCache = null;
		String registro_lifeCache = null;
		String registro_statoCache = null;
		if(car !=null){
			registro_cache = car.getCache();

		}
		registro_statoCache = (registro_cache != null ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString());
		if (registro_statoCache.equals(CostantiConfigurazione.ABILITATO.toString())) {
			registro_dimensioneCache = registro_cache.getDimensione();
			registro_algoritmoCache = DriverConfigurazioneDB_LIB.getValue(registro_cache.getAlgoritmo());
			registro_idleCache = registro_cache.getItemIdleTime();
			registro_lifeCache = registro_cache.getItemLifeSecond();
		}
		
		Cache config_cache = null;
		String config_dimensioneCache = null;
		String config_algoritmoCache = null;
		String config_idleCache = null;
		String config_lifeCache = null;
		String config_statoCache = null;
		if(aConfig !=null){
			config_cache = aConfig.getCache();

		}
		config_statoCache = (config_cache != null ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString());
		if (config_statoCache.equals(CostantiConfigurazione.ABILITATO.toString())) {
			config_dimensioneCache = config_cache.getDimensione();
			config_algoritmoCache = DriverConfigurazioneDB_LIB.getValue(config_cache.getAlgoritmo());
			config_idleCache = config_cache.getItemIdleTime();
			config_lifeCache = config_cache.getItemLifeSecond();
		}
		
		Cache authz_cache = null;
		String authz_dimensioneCache = null;
		String authz_algoritmoCache = null;
		String authz_idleCache = null;
		String authz_lifeCache = null;
		String authz_statoCache = null;
		if(aDatiAuthz !=null){
			authz_cache = aDatiAuthz.getCache();

		}
		authz_statoCache = (authz_cache != null ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString());
		if (authz_statoCache.equals(CostantiConfigurazione.ABILITATO.toString())) {
			authz_dimensioneCache = authz_cache.getDimensione();
			authz_algoritmoCache = DriverConfigurazioneDB_LIB.getValue(authz_cache.getAlgoritmo());
			authz_idleCache = authz_cache.getItemIdleTime();
			authz_lifeCache = authz_cache.getItemLifeSecond();
		}
		
		Cache authn_cache = null;
		String authn_dimensioneCache = null;
		String authn_algoritmoCache = null;
		String authn_idleCache = null;
		String authn_lifeCache = null;
		String authn_statoCache = null;
		if(aDatiAuthn !=null){
			authn_cache = aDatiAuthn.getCache();

		}
		authn_statoCache = (authn_cache != null ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString());
		if (authn_statoCache.equals(CostantiConfigurazione.ABILITATO.toString())) {
			authn_dimensioneCache = authn_cache.getDimensione();
			authn_algoritmoCache = DriverConfigurazioneDB_LIB.getValue(authn_cache.getAlgoritmo());
			authn_idleCache = authn_cache.getItemIdleTime();
			authn_lifeCache = authn_cache.getItemLifeSecond();
		}
		
		Cache token_cache = null;
		String token_dimensioneCache = null;
		String token_algoritmoCache = null;
		String token_idleCache = null;
		String token_lifeCache = null;
		String token_statoCache = null;
		if(aDatiGestioneToken !=null){
			token_cache = aDatiGestioneToken.getCache();

		}
		token_statoCache = (token_cache != null ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString());
		if (token_statoCache.equals(CostantiConfigurazione.ABILITATO.toString())) {
			token_dimensioneCache = token_cache.getDimensione();
			token_algoritmoCache = DriverConfigurazioneDB_LIB.getValue(token_cache.getAlgoritmo());
			token_idleCache = token_cache.getItemIdleTime();
			token_lifeCache = token_cache.getItemLifeSecond();
		}
		
		Cache keystore_cache = null;
		String keystore_dimensioneCache = null;
		String keystore_algoritmoCache = null;
		String keystore_idleCache = null;
		String keystore_lifeCache = null;
		String keystore_statoCache = null;
		String keystore_crlLifeCache = null;
		if(aDatiKeystore !=null){
			keystore_cache = aDatiKeystore.getCache();
			keystore_crlLifeCache = aDatiKeystore.getCrlItemLifeSecond();

		}
		keystore_statoCache = (keystore_cache != null ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString());
		if (keystore_statoCache.equals(CostantiConfigurazione.ABILITATO.toString())) {
			keystore_dimensioneCache = keystore_cache.getDimensione();
			keystore_algoritmoCache = DriverConfigurazioneDB_LIB.getValue(keystore_cache.getAlgoritmo());
			keystore_idleCache = keystore_cache.getItemIdleTime();
			keystore_lifeCache = keystore_cache.getItemLifeSecond();
		}
		
		Cache dati_richieste_cache = null;
		String dati_richieste_dimensioneCache = null;
		String dati_richieste_algoritmoCache = null;
		String dati_richieste_idleCache = null;
		String dati_richieste_lifeCache = null;
		String dati_richieste_statoCache = null;
		if(aDatiRichieste !=null){
			dati_richieste_cache = aDatiRichieste.getCache();
		}
		dati_richieste_statoCache = (dati_richieste_cache != null ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString());
		if (dati_richieste_statoCache.equals(CostantiConfigurazione.ABILITATO.toString())) {
			dati_richieste_dimensioneCache = dati_richieste_cache.getDimensione();
			dati_richieste_algoritmoCache = DriverConfigurazioneDB_LIB.getValue(dati_richieste_cache.getAlgoritmo());
			dati_richieste_idleCache = dati_richieste_cache.getItemIdleTime();
			dati_richieste_lifeCache = dati_richieste_cache.getItemLifeSecond();
		}

		Tracciamento t = config.getTracciamento();
		String tracciamentoBuste = null;
		String tracciamentoEsiti = null;
		if (t != null) {
			tracciamentoBuste = DriverConfigurazioneDB_LIB.getValue(t.getStato());
			tracciamentoEsiti = t.getEsiti();
		}
		
		Dump d = config.getDump();
		String dumpApplicativo = null;
		String dumpPD = null;
		String dumpPA = null;
		DumpConfigurazione dumpConfigPA = null;
		DumpConfigurazione dumpConfigPD = null;
		if (d != null) {
			dumpApplicativo = DriverConfigurazioneDB_LIB.getValue(d.getStato());
			dumpPD = DriverConfigurazioneDB_LIB.getValue(d.getDumpBinarioPortaDelegata());
			dumpPA = DriverConfigurazioneDB_LIB.getValue(d.getDumpBinarioPortaApplicativa());
			
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
			transazioniTempiElaborazione = DriverConfigurazioneDB_LIB.getValue(transazioni.getTempiElaborazione());
			transazioniToken = DriverConfigurazioneDB_LIB.getValue(transazioni.getToken());
		}
		
		String modRisposta = CostantiConfigurazione.CONNECTION_REPLY.toString();
		if(risposte!=null){
			modRisposta = (risposte.getConnessione().equals(CostantiConfigurazione.CONNECTION_REPLY) ? 
					CostantiConfigurazione.CONNECTION_REPLY.toString() : CostantiConfigurazione.NEW_CONNECTION.toString());
		}
		String routingEnabled =  CostantiConfigurazione.DISABILITATO.toString();
		if(config.getRoutingTable()!=null){
			if(config.getRoutingTable().getAbilitata()!=null && config.getRoutingTable().getAbilitata())
				routingEnabled =  CostantiConfigurazione.ABILITATO.toString();
		}
		
		String validazione_contenuti_stato = null;
		String validazione_contenuti_tipo = null;
		String validazione_contenuti_acceptMtomMessage = null;
		if(config.getValidazioneContenutiApplicativi()!=null){
			validazione_contenuti_stato = DriverConfigurazioneDB_LIB.getValue(config.getValidazioneContenutiApplicativi().getStato());
			validazione_contenuti_tipo = DriverConfigurazioneDB_LIB.getValue(config.getValidazioneContenutiApplicativi().getTipo());
			validazione_contenuti_acceptMtomMessage = DriverConfigurazioneDB_LIB.getValue(config.getValidazioneContenutiApplicativi().getAcceptMtomMessage());
		}

		CanaliConfigurazione configurazioneCanali = config.getGestioneCanali();
		String configurazioneCanali_stato = null;
		if(configurazioneCanali!=null) {
			configurazioneCanali_stato = DriverConfigurazioneDB_LIB.getValue(configurazioneCanali.getStato());
		}
		
		ConfigurazioneGeneraleHandler configHandlers = config.getConfigurazioneHandler();
		
		ExtendedInfoManager extInfoManager = ExtendedInfoManager.getInstance();
		IExtendedInfo extInfoConfigurazioneDriver = extInfoManager.newInstanceExtendedInfoConfigurazione();
									
		try {
			switch (type) {
			case CREATE:
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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
				
				updateStmt.setString(index++, cadenza_inoltro);
				updateStmt.setString(index++, val_stato);
				updateStmt.setString(index++, val_controllo);
				updateStmt.setString(index++, msg_diag_severita);
				updateStmt.setString(index++, msg_diag_severita_log4j);
				updateStmt.setString(index++, autenticazione);
				updateStmt.setString(index++, val_profiloCollaborazione);
				updateStmt.setString(index++, modRisposta);
				updateStmt.setString(index++, utilizzoIndTelematico);
				updateStmt.setString(index++, routingEnabled);
				updateStmt.setString(index++, gestioneManifest);
				updateStmt.setString(index++, val_manifest);
				updateStmt.setString(index++, tracciamentoBuste);
				updateStmt.setString(index++, tracciamentoEsiti);
				updateStmt.setString(index++, transazioniTempiElaborazione);
				updateStmt.setString(index++, transazioniToken);
				updateStmt.setString(index++, dumpApplicativo);
				updateStmt.setString(index++, dumpPD);
				updateStmt.setString(index++, dumpPA);
				updateStmt.setString(index++, validazione_contenuti_stato);
				updateStmt.setString(index++, validazione_contenuti_tipo);
				updateStmt.setString(index++, validazione_contenuti_acceptMtomMessage);
				// registro cache
				updateStmt.setString(index++, registro_statoCache);
				updateStmt.setString(index++, registro_dimensioneCache);
				updateStmt.setString(index++, registro_algoritmoCache);
				updateStmt.setString(index++, registro_idleCache);
				updateStmt.setString(index++, registro_lifeCache);
				// config cache
				updateStmt.setString(index++, config_statoCache);
				updateStmt.setString(index++, config_dimensioneCache);
				updateStmt.setString(index++, config_algoritmoCache);
				updateStmt.setString(index++, config_idleCache);
				updateStmt.setString(index++, config_lifeCache);
				// authz cache
				updateStmt.setString(index++, authz_statoCache);
				updateStmt.setString(index++, authz_dimensioneCache);
				updateStmt.setString(index++, authz_algoritmoCache);
				updateStmt.setString(index++, authz_idleCache);
				updateStmt.setString(index++, authz_lifeCache);
				// authn cache
				updateStmt.setString(index++, authn_statoCache);
				updateStmt.setString(index++, authn_dimensioneCache);
				updateStmt.setString(index++, authn_algoritmoCache);
				updateStmt.setString(index++, authn_idleCache);
				updateStmt.setString(index++, authn_lifeCache);
				// token cache
				updateStmt.setString(index++, token_statoCache);
				updateStmt.setString(index++, token_dimensioneCache);
				updateStmt.setString(index++, token_algoritmoCache);
				updateStmt.setString(index++, token_idleCache);
				updateStmt.setString(index++, token_lifeCache);
				// keystore cache
				updateStmt.setString(index++, keystore_statoCache);
				updateStmt.setString(index++, keystore_dimensioneCache);
				updateStmt.setString(index++, keystore_algoritmoCache);
				updateStmt.setString(index++, keystore_idleCache);
				updateStmt.setString(index++, keystore_lifeCache);
				updateStmt.setString(index++, keystore_crlLifeCache);
				// multitenant
				updateStmt.setString(index++, multitenant!=null ? DriverConfigurazioneDB_LIB.getValue(multitenant.getStato()) : null);
				updateStmt.setString(index++, multitenant!=null ? DriverConfigurazioneDB_LIB.getValue(multitenant.getFruizioneSceltaSoggettiErogatori()) : null);
				updateStmt.setString(index++, multitenant!=null ? DriverConfigurazioneDB_LIB.getValue(multitenant.getErogazioneSceltaSoggettiFruitori()) : null);
				// cors
				updateStmt.setString(index++, cors_stato);
				updateStmt.setString(index++, cors_tipo);
				updateStmt.setString(index++, cors_all_allow_origins);
				updateStmt.setString(index++, cors_all_allow_methods);
				updateStmt.setString(index++, cors_all_allow_headers);
				updateStmt.setString(index++, cors_allow_credentials);
				updateStmt.setInt(index++, cors_allow_max_age);
				if(cors_allow_max_age_seconds!=null) {
					updateStmt.setInt(index++, cors_allow_max_age_seconds);
				}
				else {
					updateStmt.setNull(index++, java.sql.Types.INTEGER);
				}
				updateStmt.setString(index++, cors_allow_origins);
				updateStmt.setString(index++, cors_allow_headers);
				updateStmt.setString(index++, cors_allow_methods);
				updateStmt.setString(index++, cors_allow_expose_headers);				
				// responseCaching
				updateStmt.setString(index++, response_cache_stato);
				if(response_cache_seconds!=null) {
					updateStmt.setInt(index++, response_cache_seconds);
				}
				else {
					updateStmt.setNull(index++, java.sql.Types.INTEGER);
				}
				if(response_cache_max_msg_size!=null) {
					updateStmt.setLong(index++, response_cache_max_msg_size);
				}
				else {
					updateStmt.setNull(index++, java.sql.Types.BIGINT);
				}
				updateStmt.setInt(index++, response_cache_noCache ? CostantiDB.TRUE : CostantiDB.FALSE);
				updateStmt.setInt(index++, response_cache_maxAge ? CostantiDB.TRUE : CostantiDB.FALSE);
				updateStmt.setInt(index++, response_cache_noStore ? CostantiDB.TRUE : CostantiDB.FALSE);
				updateStmt.setString(index++, response_cache_hash_url);
				updateStmt.setString(index++, response_cache_hash_query);
				updateStmt.setString(index++, response_cache_hash_query_list);
				updateStmt.setString(index++, response_cache_hash_headers);
				updateStmt.setString(index++, response_cache_hash_headers_list);
				updateStmt.setString(index++, response_cache_hash_payload);
				// responseCaching cache
				updateStmt.setString(index++, responseCaching_statoCache);
				updateStmt.setString(index++, responseCaching_dimensioneCache);
				updateStmt.setString(index++, responseCaching_algoritmoCache);
				updateStmt.setString(index++, responseCaching_idleCache);
				updateStmt.setString(index++, responseCaching_lifeCache);
				// consegna applicativi cache
				updateStmt.setString(index++, consegna_statoCache);
				updateStmt.setString(index++, consegna_dimensioneCache);
				updateStmt.setString(index++, consegna_algoritmoCache);
				updateStmt.setString(index++, consegna_idleCache);
				updateStmt.setString(index++, consegna_lifeCache);
				// canali
				updateStmt.setString(index++, configurazioneCanali_stato);
				// dati richieste cache
				updateStmt.setString(index++, dati_richieste_statoCache);
				updateStmt.setString(index++, dati_richieste_dimensioneCache);
				updateStmt.setString(index++, dati_richieste_algoritmoCache);
				updateStmt.setString(index++, dati_richieste_idleCache);
				updateStmt.setString(index++, dati_richieste_lifeCache);

				DriverConfigurazioneDB_LIB.log.debug("eseguo query :" + 
						DBUtils.formatSQLString(updateQuery, 
								cadenza_inoltro, 
								val_stato, val_controllo, 
								msg_diag_severita, msg_diag_severita_log4j, 
								autenticazione, 
								val_profiloCollaborazione, 
								modRisposta, utilizzoIndTelematico, 
								routingEnabled, gestioneManifest, 
								val_manifest, tracciamentoBuste, transazioniTempiElaborazione, transazioniToken, dumpApplicativo, dumpPD, dumpPA,
								validazione_contenuti_stato,validazione_contenuti_tipo,validazione_contenuti_acceptMtomMessage,
								registro_statoCache, registro_dimensioneCache, registro_algoritmoCache, registro_idleCache, registro_lifeCache,
								config_statoCache, config_dimensioneCache, config_algoritmoCache, config_idleCache, config_lifeCache,
								authz_statoCache, authz_dimensioneCache, authz_algoritmoCache, authz_idleCache, authz_lifeCache,
								authn_statoCache, authn_dimensioneCache, authn_algoritmoCache, authn_idleCache, authn_lifeCache,
								token_statoCache, token_dimensioneCache, token_algoritmoCache, token_idleCache, token_lifeCache,
								keystore_statoCache, keystore_dimensioneCache, keystore_algoritmoCache, keystore_idleCache, keystore_lifeCache, keystore_crlLifeCache,
								(multitenant!=null ? DriverConfigurazioneDB_LIB.getValue(multitenant.getStato()) : null),
								(multitenant!=null ? DriverConfigurazioneDB_LIB.getValue(multitenant.getFruizioneSceltaSoggettiErogatori()) : null),
								(multitenant!=null ? DriverConfigurazioneDB_LIB.getValue(multitenant.getErogazioneSceltaSoggettiFruitori()) : null),
								cors_stato, cors_tipo, cors_all_allow_origins, cors_all_allow_methods, cors_all_allow_headers, cors_allow_credentials, cors_allow_max_age, cors_allow_max_age_seconds,
								cors_allow_origins, cors_allow_headers, cors_allow_methods, cors_allow_expose_headers,
								response_cache_stato, response_cache_seconds, response_cache_max_msg_size, 
								(response_cache_noCache ? CostantiDB.TRUE : CostantiDB.FALSE),
								(response_cache_maxAge ? CostantiDB.TRUE : CostantiDB.FALSE),
								(response_cache_noStore ? CostantiDB.TRUE : CostantiDB.FALSE),
								response_cache_hash_url, response_cache_hash_query, response_cache_hash_query_list, response_cache_hash_headers, response_cache_hash_headers_list, response_cache_hash_payload,
								responseCaching_statoCache, responseCaching_dimensioneCache, responseCaching_algoritmoCache, responseCaching_idleCache, responseCaching_lifeCache,
								consegna_statoCache, consegna_dimensioneCache, consegna_algoritmoCache, consegna_idleCache, consegna_lifeCache,
								configurazioneCanali_stato,
								dati_richieste_statoCache, dati_richieste_dimensioneCache, dati_richieste_algoritmoCache, dati_richieste_idleCache, dati_richieste_lifeCache
								));

				n = updateStmt.executeUpdate();
				updateStmt.close();

				// delete from config_url_invocazione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIG_URL_REGOLE);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIG_URL_INVOCAZIONE);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				// insert into config_protocolli
				if(config.getUrlInvocazione()!=null){
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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
							_createUrlInvocazioneRegola(configUrlInvocazioneRegola, con);
						}
					}
				}
				
				// delete from msg diag appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.MSG_DIAGN_APPENDER_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from msg diag appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.MSG_DIAGN_APPENDER);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into msg diag appender
				if(config.getMessaggiDiagnostici()!=null){
					for(int k=0; k<config.getMessaggiDiagnostici().sizeOpenspcoopAppenderList();k++){
						OpenspcoopAppender appender = config.getMessaggiDiagnostici().getOpenspcoopAppender(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo", appender.getTipo() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idMsgDiagAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDB_LIB.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.MSG_DIAGN_APPENDER, CostantiDB.MSG_DIAGN_APPENDER_COLUMN_ID, 
										CostantiDB.MSG_DIAGN_APPENDER_SEQUENCE, CostantiDB.MSG_DIAGN_APPENDER_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idMsgDiagAppender<=0){
							throw new Exception("ID (msg diag appender) autoincrementale non ottenuto");
						}
						
						for(int l=0; l<appender.sizePropertyList();l++){
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.TRACCIAMENTO_APPENDER_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from tracciamento appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.TRACCIAMENTO_APPENDER);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into tracciamento appender
				if(config.getTracciamento()!=null){
					for(int k=0; k<config.getTracciamento().sizeOpenspcoopAppenderList();k++){
						OpenspcoopAppender appender = config.getTracciamento().getOpenspcoopAppender(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo", appender.getTipo() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idTracceAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDB_LIB.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.TRACCIAMENTO_APPENDER, CostantiDB.TRACCIAMENTO_APPENDER_COLUMN_ID, 
										CostantiDB.TRACCIAMENTO_APPENDER_SEQUENCE, CostantiDB.TRACCIAMENTO_APPENDER_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idTracceAppender<=0){
							throw new Exception("ID (tracce appender) autoincrementale non ottenuto");
						}

						for(int l=0; l<appender.sizePropertyList();l++){
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.DUMP_APPENDER_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from dump appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.DUMP_APPENDER);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into dump appender
				if(config.getDump()!=null){
					for(int k=0; k<config.getDump().sizeOpenspcoopAppenderList();k++){
						OpenspcoopAppender appender = config.getDump().getOpenspcoopAppender(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo", appender.getTipo() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idDumpAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDB_LIB.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.DUMP_APPENDER, CostantiDB.DUMP_APPENDER_COLUMN_ID, 
										CostantiDB.DUMP_APPENDER_SEQUENCE, CostantiDB.DUMP_APPENDER_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idDumpAppender<=0){
							throw new Exception("ID (dump appender) autoincrementale non ottenuto");
						}

						for(int l=0; l<appender.sizePropertyList();l++){
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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
				if(response_cache_regole!=null && response_cache_regole.size()>0){
					for (int j = 0; j < response_cache_regole.size(); j++) {
						ResponseCachingConfigurazioneRegola regola = response_cache_regole.get(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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
						DriverConfigurazioneDB_LIB.log.debug("Aggiunta regola di cache");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunte " + n + " regole di cache");
				
				// dumpConfigurazione
				
				// per backward compatibility elimino una config esistente
				DumpConfigurazione dumpConfigOldBackwardCompatibility = DriverConfigurazioneDB_dumpLIB.readDumpConfigurazione(con, null, CostantiDB._DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG);
				if(dumpConfigOldBackwardCompatibility!=null) {
					DriverConfigurazioneDB_dumpLIB.CRUDDumpConfigurazione(DELETE, con, dumpConfigOldBackwardCompatibility, null, CostantiDB._DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG);
				}
				
				DriverConfigurazioneDB_dumpLIB.CRUDDumpConfigurazione(type, con, dumpConfigPA, null, CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG_PA);
				DriverConfigurazioneDB_dumpLIB.CRUDDumpConfigurazione(type, con, dumpConfigPD, null, CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG_PD);
				

				// delete from msgdiag ds prop
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.MSG_DIAGN_DS_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from msgdiag ds
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.MSG_DIAGN_DS);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into msgdiag ds
				if(config.getMessaggiDiagnostici()!=null){
					for(int k=0; k<config.getMessaggiDiagnostici().sizeOpenspcoopSorgenteDatiList();k++){
						OpenspcoopSorgenteDati ds = config.getMessaggiDiagnostici().getOpenspcoopSorgenteDati(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome", ds.getNome() , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome_jndi", ds.getNomeJndi() , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo_database", ds.getTipoDatabase() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idMsgDsAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDB_LIB.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.MSG_DIAGN_DS, CostantiDB.MSG_DIAGN_DS_COLUMN_ID, 
										CostantiDB.MSG_DIAGN_DS_SEQUENCE, CostantiDB.MSG_DIAGN_DS_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idMsgDsAppender<=0){
							throw new Exception("ID (msgdiag ds appender) autoincrementale non ottenuto");
						}

						for(int l=0; l<ds.sizePropertyList();l++){
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.TRACCIAMENTO_DS_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from tracce ds
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.TRACCIAMENTO_DS);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into tracce ds
				if(config.getTracciamento()!=null){
					for(int k=0; k<config.getTracciamento().sizeOpenspcoopSorgenteDatiList();k++){
						OpenspcoopSorgenteDati ds = config.getTracciamento().getOpenspcoopSorgenteDati(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome", ds.getNome() , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome_jndi", ds.getNomeJndi() , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo_database", ds.getTipoDatabase() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idTracceDsAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDB_LIB.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.TRACCIAMENTO_DS, CostantiDB.TRACCIAMENTO_DS_COLUMN_ID, 
										CostantiDB.TRACCIAMENTO_DS_SEQUENCE, CostantiDB.TRACCIAMENTO_DS_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idTracceDsAppender<=0){
							throw new Exception("ID (tracciamento ds appender) autoincrementale non ottenuto");
						}

						for(int l=0; l<ds.sizePropertyList();l++){
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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
				if(extInfoConfigurazioneDriver!=null){
					
					//extInfoConfigurazioneDriver.deleteAllExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, CRUDType.CREATE);
										
					if(config.sizeExtendedInfoList()>0){
						for(int l=0; l<config.sizeExtendedInfoList();l++){
							extInfoConfigurazioneDriver.deleteExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, config.getExtendedInfo(l), CRUDType.CREATE);
							extInfoConfigurazioneDriver.createExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, config.getExtendedInfo(l), CRUDType.CREATE);
						}
					}
				}


				break;
			case UPDATE:
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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
				
				updateStmt.setString(index++, cadenza_inoltro);
				updateStmt.setString(index++, val_stato);
				updateStmt.setString(index++, val_controllo);
				updateStmt.setString(index++, msg_diag_severita);
				updateStmt.setString(index++, msg_diag_severita_log4j);
				updateStmt.setString(index++, autenticazione);
				updateStmt.setString(index++, val_profiloCollaborazione);
				updateStmt.setString(index++, modRisposta);
				updateStmt.setString(index++, utilizzoIndTelematico);
				updateStmt.setString(index++, routingEnabled);
				updateStmt.setString(index++, gestioneManifest);
				updateStmt.setString(index++, val_manifest);
				updateStmt.setString(index++, tracciamentoBuste);
				updateStmt.setString(index++, tracciamentoEsiti);
				updateStmt.setString(index++, transazioniTempiElaborazione);
				updateStmt.setString(index++, transazioniToken);
				updateStmt.setString(index++, dumpApplicativo);
				updateStmt.setString(index++, dumpPD);
				updateStmt.setString(index++, dumpPA);
				updateStmt.setString(index++, validazione_contenuti_stato);
				updateStmt.setString(index++, validazione_contenuti_tipo);
				updateStmt.setString(index++, validazione_contenuti_acceptMtomMessage);
				// registro cache
				updateStmt.setString(index++, registro_statoCache);
				updateStmt.setString(index++, registro_dimensioneCache);
				updateStmt.setString(index++, registro_algoritmoCache);
				updateStmt.setString(index++, registro_idleCache);
				updateStmt.setString(index++, registro_lifeCache);
				// config cache
				updateStmt.setString(index++, config_statoCache);
				updateStmt.setString(index++, config_dimensioneCache);
				updateStmt.setString(index++, config_algoritmoCache);
				updateStmt.setString(index++, config_idleCache);
				updateStmt.setString(index++, config_lifeCache);
				// authz cache
				updateStmt.setString(index++, authz_statoCache);
				updateStmt.setString(index++, authz_dimensioneCache);
				updateStmt.setString(index++, authz_algoritmoCache);
				updateStmt.setString(index++, authz_idleCache);
				updateStmt.setString(index++, authz_lifeCache);
				// authn cache
				updateStmt.setString(index++, authn_statoCache);
				updateStmt.setString(index++, authn_dimensioneCache);
				updateStmt.setString(index++, authn_algoritmoCache);
				updateStmt.setString(index++, authn_idleCache);
				updateStmt.setString(index++, authn_lifeCache);
				// token cache
				updateStmt.setString(index++, token_statoCache);
				updateStmt.setString(index++, token_dimensioneCache);
				updateStmt.setString(index++, token_algoritmoCache);
				updateStmt.setString(index++, token_idleCache);
				updateStmt.setString(index++, token_lifeCache);
				// keystore cache
				updateStmt.setString(index++, keystore_statoCache);
				updateStmt.setString(index++, keystore_dimensioneCache);
				updateStmt.setString(index++, keystore_algoritmoCache);
				updateStmt.setString(index++, keystore_idleCache);
				updateStmt.setString(index++, keystore_lifeCache);
				updateStmt.setString(index++, keystore_crlLifeCache);
				// multitenant
				updateStmt.setString(index++, multitenant!=null ? DriverConfigurazioneDB_LIB.getValue(multitenant.getStato()) : null);
				updateStmt.setString(index++, multitenant!=null ? DriverConfigurazioneDB_LIB.getValue(multitenant.getFruizioneSceltaSoggettiErogatori()) : null);
				updateStmt.setString(index++, multitenant!=null ? DriverConfigurazioneDB_LIB.getValue(multitenant.getErogazioneSceltaSoggettiFruitori()) : null);
				// cors
				updateStmt.setString(index++, cors_stato);
				updateStmt.setString(index++, cors_tipo);
				updateStmt.setString(index++, cors_all_allow_origins);
				updateStmt.setString(index++, cors_all_allow_methods);
				updateStmt.setString(index++, cors_all_allow_headers);
				updateStmt.setString(index++, cors_allow_credentials);
				updateStmt.setInt(index++, cors_allow_max_age);
				if(cors_allow_max_age_seconds!=null) {
					updateStmt.setInt(index++, cors_allow_max_age_seconds);
				}
				else {
					updateStmt.setNull(index++, java.sql.Types.INTEGER);
				}
				updateStmt.setString(index++, cors_allow_origins);
				updateStmt.setString(index++, cors_allow_headers);
				updateStmt.setString(index++, cors_allow_methods);
				updateStmt.setString(index++, cors_allow_expose_headers);				
				// responseCaching
				updateStmt.setString(index++, response_cache_stato);
				if(response_cache_seconds!=null) {
					updateStmt.setInt(index++, response_cache_seconds);
				}
				else {
					updateStmt.setNull(index++, java.sql.Types.INTEGER);
				}
				if(response_cache_max_msg_size!=null) {
					updateStmt.setLong(index++, response_cache_max_msg_size);
				}
				else {
					updateStmt.setNull(index++, java.sql.Types.BIGINT);
				}
				updateStmt.setInt(index++, response_cache_noCache ? CostantiDB.TRUE : CostantiDB.FALSE);
				updateStmt.setInt(index++, response_cache_maxAge ? CostantiDB.TRUE : CostantiDB.FALSE);
				updateStmt.setInt(index++, response_cache_noStore ? CostantiDB.TRUE : CostantiDB.FALSE);
				updateStmt.setString(index++, response_cache_hash_url);
				updateStmt.setString(index++, response_cache_hash_query);
				updateStmt.setString(index++, response_cache_hash_query_list);
				updateStmt.setString(index++, response_cache_hash_headers);
				updateStmt.setString(index++, response_cache_hash_headers_list);
				updateStmt.setString(index++, response_cache_hash_payload);
				// responseCaching cache
				updateStmt.setString(index++, responseCaching_statoCache);
				updateStmt.setString(index++, responseCaching_dimensioneCache);
				updateStmt.setString(index++, responseCaching_algoritmoCache);
				updateStmt.setString(index++, responseCaching_idleCache);
				updateStmt.setString(index++, responseCaching_lifeCache);
				// consegna applicativi cache
				updateStmt.setString(index++, consegna_statoCache);
				updateStmt.setString(index++, consegna_dimensioneCache);
				updateStmt.setString(index++, consegna_algoritmoCache);
				updateStmt.setString(index++, consegna_idleCache);
				updateStmt.setString(index++, consegna_lifeCache);
				// canali
				updateStmt.setString(index++, configurazioneCanali_stato);
				// dati richieste cache
				updateStmt.setString(index++, dati_richieste_statoCache);
				updateStmt.setString(index++, dati_richieste_dimensioneCache);
				updateStmt.setString(index++, dati_richieste_algoritmoCache);
				updateStmt.setString(index++, dati_richieste_idleCache);
				updateStmt.setString(index++, dati_richieste_lifeCache);
				
				DriverConfigurazioneDB_LIB.log.debug("eseguo query :" + 
						DBUtils.formatSQLString(updateQuery, 
								cadenza_inoltro, 
								val_stato, val_controllo, 
								msg_diag_severita, msg_diag_severita_log4j, 
								autenticazione, 
								val_profiloCollaborazione, 
								modRisposta, utilizzoIndTelematico, 
								routingEnabled, gestioneManifest, 
								val_manifest, 
								tracciamentoBuste, transazioniTempiElaborazione, transazioniToken, dumpApplicativo, dumpPD, dumpPA,
								validazione_contenuti_stato,validazione_contenuti_tipo,
								registro_statoCache, registro_dimensioneCache, registro_algoritmoCache, registro_idleCache, registro_lifeCache,
								config_statoCache, config_dimensioneCache, config_algoritmoCache, config_idleCache, config_lifeCache,
								authz_statoCache, authz_dimensioneCache, authz_algoritmoCache, authz_idleCache, authz_lifeCache,
								authn_statoCache, authn_dimensioneCache, authn_algoritmoCache, authn_idleCache, authn_lifeCache,
								token_statoCache, token_dimensioneCache, token_algoritmoCache, token_idleCache, token_lifeCache,
								keystore_statoCache, keystore_dimensioneCache, keystore_algoritmoCache, keystore_idleCache, keystore_lifeCache, keystore_crlLifeCache,
								(multitenant!=null ? DriverConfigurazioneDB_LIB.getValue(multitenant.getStato()) : null),
								(multitenant!=null ? DriverConfigurazioneDB_LIB.getValue(multitenant.getFruizioneSceltaSoggettiErogatori()) : null),
								(multitenant!=null ? DriverConfigurazioneDB_LIB.getValue(multitenant.getErogazioneSceltaSoggettiFruitori()) : null),
								cors_stato, cors_tipo, cors_all_allow_origins, cors_all_allow_methods, cors_all_allow_headers, cors_allow_credentials, cors_allow_max_age, cors_allow_max_age_seconds,
								cors_allow_origins, cors_allow_headers, cors_allow_methods, cors_allow_expose_headers,
								response_cache_stato, response_cache_seconds, response_cache_max_msg_size, 
								response_cache_hash_url, response_cache_hash_query, response_cache_hash_query_list, response_cache_hash_headers, response_cache_hash_headers_list, response_cache_hash_payload,
								responseCaching_statoCache, responseCaching_dimensioneCache, responseCaching_algoritmoCache, responseCaching_idleCache, responseCaching_lifeCache,
								consegna_statoCache, consegna_dimensioneCache, consegna_algoritmoCache, consegna_idleCache, consegna_lifeCache,
								configurazioneCanali_stato,
								dati_richieste_statoCache, dati_richieste_dimensioneCache, dati_richieste_algoritmoCache, dati_richieste_idleCache, dati_richieste_lifeCache
								));

				n = updateStmt.executeUpdate();
				updateStmt.close();

				// delete from config_url_invocazione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIG_URL_REGOLE);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIG_URL_INVOCAZIONE);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				// insert into config_protocolli
				if(config.getUrlInvocazione()!=null){
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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
							_createUrlInvocazioneRegola(configUrlInvocazioneRegola, con);
						}
					}
				}
				
				// delete from msg diag appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.MSG_DIAGN_APPENDER_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from msg diag appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.MSG_DIAGN_APPENDER);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into msg diag appender
				if(config.getMessaggiDiagnostici()!=null){
					for(int k=0; k<config.getMessaggiDiagnostici().sizeOpenspcoopAppenderList();k++){
						OpenspcoopAppender appender = config.getMessaggiDiagnostici().getOpenspcoopAppender(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo", appender.getTipo() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idMsgDiagAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDB_LIB.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.MSG_DIAGN_APPENDER, CostantiDB.MSG_DIAGN_APPENDER_COLUMN_ID, 
										CostantiDB.MSG_DIAGN_APPENDER_SEQUENCE, CostantiDB.MSG_DIAGN_APPENDER_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idMsgDiagAppender<=0){
							throw new Exception("ID (msg diag appender) autoincrementale non ottenuto");
						}

						for(int l=0; l<appender.sizePropertyList();l++){
							DriverConfigurazioneDB_LIB.log.debug("INSERT INTO "+CostantiDB.MSG_DIAGN_APPENDER_PROP+" (id_appender,nome,valore) VALUES ("+idMsgDiagAppender+","+appender.getProperty(l).getNome()+","+appender.getProperty(l).getValore()+")");
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.TRACCIAMENTO_APPENDER_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from tracciamento appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.TRACCIAMENTO_APPENDER);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into tracciamento appender
				if(config.getTracciamento()!=null){
					for(int k=0; k<config.getTracciamento().sizeOpenspcoopAppenderList();k++){
						OpenspcoopAppender appender = config.getTracciamento().getOpenspcoopAppender(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo", appender.getTipo() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idTracceAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDB_LIB.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.TRACCIAMENTO_APPENDER, CostantiDB.TRACCIAMENTO_APPENDER_COLUMN_ID, 
										CostantiDB.TRACCIAMENTO_APPENDER_SEQUENCE, CostantiDB.TRACCIAMENTO_APPENDER_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idTracceAppender<=0){
							throw new Exception("ID (tracce appender) autoincrementale non ottenuto");
						}

						for(int l=0; l<appender.sizePropertyList();l++){
							DriverConfigurazioneDB_LIB.log.debug("INSERT INTO "+CostantiDB.TRACCIAMENTO_APPENDER_PROP+" (id_appender,nome,valore) VALUES ("+idTracceAppender+","+appender.getProperty(l).getNome()+","+appender.getProperty(l).getValore()+")");
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.DUMP_APPENDER_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from dump appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.DUMP_APPENDER);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into dump appender
				if(config.getDump()!=null){
					for(int k=0; k<config.getDump().sizeOpenspcoopAppenderList();k++){
						OpenspcoopAppender appender = config.getDump().getOpenspcoopAppender(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo", appender.getTipo() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idDumpAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDB_LIB.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.DUMP_APPENDER, CostantiDB.DUMP_APPENDER_COLUMN_ID, 
										CostantiDB.DUMP_APPENDER_SEQUENCE, CostantiDB.DUMP_APPENDER_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idDumpAppender<=0){
							throw new Exception("ID (dump appender) autoincrementale non ottenuto");
						}

						for(int l=0; l<appender.sizePropertyList();l++){
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIGURAZIONE_CACHE_REGOLE);
				String sqlQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(sqlQuery);
				n=updateStmt.executeUpdate();
				updateStmt.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" regole di cache");
				
				n=0;
				if(response_cache_regole!=null && response_cache_regole.size()>0){
					for (int j = 0; j < response_cache_regole.size(); j++) {
						ResponseCachingConfigurazioneRegola regola = response_cache_regole.get(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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
						DriverConfigurazioneDB_LIB.log.debug("Aggiunta regola di cache");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunte " + n + " regole di cache");
				
				// dumpConfigurazione
				
				// per backward compatibility elimino una config esistente
				dumpConfigOldBackwardCompatibility = DriverConfigurazioneDB_dumpLIB.readDumpConfigurazione(con, null, CostantiDB._DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG);
				if(dumpConfigOldBackwardCompatibility!=null) {
					DriverConfigurazioneDB_dumpLIB.CRUDDumpConfigurazione(DELETE, con, dumpConfigOldBackwardCompatibility, null, CostantiDB._DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG);
				}
				
				DriverConfigurazioneDB_dumpLIB.CRUDDumpConfigurazione(type, con, dumpConfigPA, null, CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG_PA);
				DriverConfigurazioneDB_dumpLIB.CRUDDumpConfigurazione(type, con, dumpConfigPD, null, CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG_PD);
				

				// delete from msgdiag ds prop
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.MSG_DIAGN_DS_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from msgdiag ds
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.MSG_DIAGN_DS);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into msgdiag ds
				if(config.getMessaggiDiagnostici()!=null){
					for(int k=0; k<config.getMessaggiDiagnostici().sizeOpenspcoopSorgenteDatiList();k++){
						OpenspcoopSorgenteDati ds = config.getMessaggiDiagnostici().getOpenspcoopSorgenteDati(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome", ds.getNome() , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome_jndi", ds.getNomeJndi() , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo_database", ds.getTipoDatabase() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idMsgDsAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDB_LIB.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.MSG_DIAGN_DS, CostantiDB.MSG_DIAGN_DS_COLUMN_ID, 
										CostantiDB.MSG_DIAGN_DS_SEQUENCE, CostantiDB.MSG_DIAGN_DS_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idMsgDsAppender<=0){
							throw new Exception("ID (msgdiag ds appender) autoincrementale non ottenuto");
						}

						for(int l=0; l<ds.sizePropertyList();l++){
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.TRACCIAMENTO_DS_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from tracce ds
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.TRACCIAMENTO_DS);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into tracce ds
				if(config.getTracciamento()!=null){
					for(int k=0; k<config.getTracciamento().sizeOpenspcoopSorgenteDatiList();k++){
						OpenspcoopSorgenteDati ds = config.getTracciamento().getOpenspcoopSorgenteDati(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome", ds.getNome() , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome_jndi", ds.getNomeJndi() , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo_database", ds.getTipoDatabase() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idTracceDsAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDB_LIB.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.TRACCIAMENTO_DS, CostantiDB.TRACCIAMENTO_DS_COLUMN_ID, 
										CostantiDB.TRACCIAMENTO_DS_SEQUENCE, CostantiDB.TRACCIAMENTO_DS_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idTracceDsAppender<=0){
							throw new Exception("ID (tracciamento ds appender) autoincrementale non ottenuto");
						}

						for(int l=0; l<ds.sizePropertyList();l++){
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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
				if(extInfoConfigurazioneDriver!=null){
					
					//extInfoConfigurazioneDriver.deleteAllExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, CRUDType.UPDATE);
										
					if(config.sizeExtendedInfoList()>0){
						for(int l=0; l<config.sizeExtendedInfoList();l++){
							extInfoConfigurazioneDriver.deleteExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, config.getExtendedInfo(l), CRUDType.UPDATE);
							extInfoConfigurazioneDriver.createExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, config.getExtendedInfo(l), CRUDType.UPDATE);
						}
					}
				}
				

				break;

			case DELETE:
				
				//extInfoConfigurazioneDriver.deleteAllExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, CRUDType.DELETE);
				if(config.sizeExtendedInfoList()>0){
					for(int l=0; l<config.sizeExtendedInfoList();l++){
						extInfoConfigurazioneDriver.deleteExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, config.getExtendedInfo(l), CRUDType.UPDATE);
						extInfoConfigurazioneDriver.createExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, config.getExtendedInfo(l), CRUDType.UPDATE);
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
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIGURAZIONE_CACHE_REGOLE);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
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

			try {
				if(selectRS!=null)
					selectRS.close();
				if(selectStmt!=null)
					selectStmt.close();
				if(updateStmt!=null)
					updateStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	
}
