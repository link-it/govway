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
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.IExtendedInfo;
import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoDatiAttributeAuthority;
import org.openspcoop2.core.config.AccessoDatiAutenticazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoDatiConsegnaApplicativi;
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
import org.openspcoop2.core.config.ConfigurazioneGestioneErrore;
import org.openspcoop2.core.config.ConfigurazioneMessageHandlers;
import org.openspcoop2.core.config.ConfigurazioneMultitenant;
import org.openspcoop2.core.config.ConfigurazioneServiceHandlers;
import org.openspcoop2.core.config.ConfigurazioneTracciamentoPorta;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazione;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazioneRegola;
import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.Dump;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.IdSoggetto;
import org.openspcoop2.core.config.IndirizzoRisposta;
import org.openspcoop2.core.config.InoltroBusteNonRiscontrate;
import org.openspcoop2.core.config.IntegrationManager;
import org.openspcoop2.core.config.MessaggiDiagnostici;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.config.OpenspcoopSorgenteDati;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneGenerale;
import org.openspcoop2.core.config.Risposte;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.StatoServiziPdd;
import org.openspcoop2.core.config.StatoServiziPddIntegrationManager;
import org.openspcoop2.core.config.StatoServiziPddPortaApplicativa;
import org.openspcoop2.core.config.StatoServiziPddPortaDelegata;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.TracciamentoConfigurazione;
import org.openspcoop2.core.config.TracciamentoConfigurazioneFiletrace;
import org.openspcoop2.core.config.Transazioni;
import org.openspcoop2.core.config.ValidazioneBuste;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.RegistroTipo;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverConfigurazioneDB_configDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_configDriver {

	private DriverConfigurazioneDB driver = null;
	private DriverConfigurazioneDB_porteDriver porteDriver = null;
	private DriverConfigurazioneDB_gestioneErroreDriver gestioneErroreDriver = null;
	private DriverConfigurazioneDB_routingTableDriver routingTableDriver = null;
	private DriverConfigurazioneDB_genericPropertiesDriver genericPropertiesDriver = null;
	
	
	protected DriverConfigurazioneDB_configDriver(DriverConfigurazioneDB driver) {
		this.driver = driver;
		this.porteDriver = new DriverConfigurazioneDB_porteDriver(driver);
		this.gestioneErroreDriver = new DriverConfigurazioneDB_gestioneErroreDriver(driver);
		this.routingTableDriver = new DriverConfigurazioneDB_routingTableDriver(driver);
		this.genericPropertiesDriver = new DriverConfigurazioneDB_genericPropertiesDriver(driver);
	}
	
	protected AccessoRegistro getAccessoRegistro() throws DriverConfigurazioneException, DriverConfigurazioneNotFound {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getAccessoRegistro");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoRegistro] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			AccessoRegistro car = new AccessoRegistro();
			Cache cache = null;

			// aggiungo i registri
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.REGISTRI);
			sqlQueryObject.addSelectField("*");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			rs = stm.executeQuery();

			AccessoRegistroRegistro itemcar = null;

			while (rs.next()) {
				itemcar = new AccessoRegistroRegistro();

				// TIPO
				RegistroTipo tipoReg = CostantiConfigurazione.REGISTRO_XML;
				String tmpTipo = rs.getString("tipo");
				if (tmpTipo.equals("uddi"))
					tipoReg = CostantiConfigurazione.REGISTRO_UDDI;
				else if (tmpTipo.equals("web"))
					tipoReg = CostantiConfigurazione.REGISTRO_WEB;
				else if (tmpTipo.equals("db"))
					tipoReg = CostantiConfigurazione.REGISTRO_DB;
				else if (tmpTipo.equals("ws"))
					tipoReg = CostantiConfigurazione.REGISTRO_WS;

				itemcar.setTipo(tipoReg);

				// nome
				itemcar.setNome(rs.getString("nome"));

				// location
				itemcar.setLocation(rs.getString("location"));

				// USER e PASSWORD
				String tmpUser = rs.getString("utente");
				String tmpPw = rs.getString("password");
				if ((tmpUser != null) && (tmpPw != null) && !tmpUser.equals("") && !tmpPw.equals("")) {
					itemcar.setUser(tmpUser);
					itemcar.setPassword(tmpPw);
				}

				car.addRegistro(itemcar);
			}
			rs.close();
			stm.close();

			//fix bug 23/11/07
			//se nn trovo registri non va lanciata l'eccezione in quanto
			//possono esistere variabili di configurazione successive che vanno lette
			//se nn ho trovato registri allora lancio eccezione
			//if(itemcar==null) throw new DriverConfigurazioneNotFound("Nessun registro trovato");

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE);
			sqlQueryObject.addSelectField("*");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			this.driver.logDebug("eseguo query : " + sqlQuery);

			rs = stm.executeQuery();

			if (rs.next()) {
				String tmpCache = rs.getString("statocache");
				if (CostantiConfigurazione.ABILITATO.equals(tmpCache)) {
					cache = new Cache();

					String tmpDim = rs.getString("dimensionecache");
					if (tmpDim != null && !tmpDim.equals(""))
						cache.setDimensione(tmpDim);

					String tmpAlg = rs.getString("algoritmocache");
					if (tmpAlg.equalsIgnoreCase("LRU"))
						cache.setAlgoritmo(CostantiConfigurazione.CACHE_LRU);
					else
						cache.setAlgoritmo(CostantiConfigurazione.CACHE_MRU);

					String tmpIdle = rs.getString("idlecache");
					String tmpLife = rs.getString("lifecache");

					if (tmpIdle != null && !tmpIdle.equals(""))
						cache.setItemIdleTime(tmpIdle);
					if (tmpLife != null && !tmpLife.equals(""))
						cache.setItemLifeSecond(tmpLife);

					car.setCache(cache);

				}
				rs.close();
				stm.close();
			}
			//nel caso in cui non esiste nessuna configurazione non lancio eccezioni
			//perche' possono essere presenti dei registri
			/*else{
				rs.close();
				stm.close();
				throw new DriverConfigurazioneNotFound("Nessuna Configurazione trovata.");
			}*/



			return car;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoRegistro] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoRegistro] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}
	}

	protected void createAccessoRegistro(AccessoRegistroRegistro registro) throws DriverConfigurazioneException {

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("createAccessoRegistro");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createAccessoRegistro] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDAccessoRegistro type = 1");
			DriverConfigurazioneDB_configLIB.CRUDAccessoRegistro(1, registro, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createAccessoRegistro] Errore durante la createAccessoRegistro : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected void updateAccessoRegistro(AccessoRegistroRegistro registro) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updateAccessoRegistro");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateAccessoRegistro] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDAccessoRegistro type = 2");
			DriverConfigurazioneDB_configLIB.CRUDAccessoRegistro(2, registro, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateAccessoRegistro] Errore durante la updateAccessoRegistro : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected void deleteAccessoRegistro(AccessoRegistroRegistro registro) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("deleteAccessoRegistro");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteAccessoRegistro] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDAccessoRegistro type = 3");
			DriverConfigurazioneDB_configLIB.CRUDAccessoRegistro(3, registro, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteAccessoRegistro] Errore durante la deleteAccessoRegistro : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
	
	
	
	
	protected AccessoConfigurazione getAccessoConfigurazione() throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getAccessoConfigurazione");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoConfigurazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			AccessoConfigurazione accessoConfigurazione = new AccessoConfigurazione();
			Cache cache = null;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE);
			sqlQueryObject.addSelectField("*");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			this.driver.logDebug("eseguo query : " + sqlQuery);

			rs = stm.executeQuery();

			if (rs.next()) {
				String tmpCache = rs.getString("config_statocache");
				if (CostantiConfigurazione.ABILITATO.equals(tmpCache)) {
					cache = new Cache();

					String tmpDim = rs.getString("config_dimensionecache");
					if (tmpDim != null && !tmpDim.equals(""))
						cache.setDimensione(tmpDim);

					String tmpAlg = rs.getString("config_algoritmocache");
					if (tmpAlg.equalsIgnoreCase("LRU"))
						cache.setAlgoritmo(CostantiConfigurazione.CACHE_LRU);
					else
						cache.setAlgoritmo(CostantiConfigurazione.CACHE_MRU);

					String tmpIdle = rs.getString("config_idlecache");
					String tmpLife = rs.getString("config_lifecache");

					if (tmpIdle != null && !tmpIdle.equals(""))
						cache.setItemIdleTime(tmpIdle);
					if (tmpLife != null && !tmpLife.equals(""))
						cache.setItemLifeSecond(tmpLife);

					accessoConfigurazione.setCache(cache);

				}
				rs.close();
				stm.close();
			}

			return accessoConfigurazione;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoConfigurazione] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoConfigurazione] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}
	}

	protected void createAccessoConfigurazione(AccessoConfigurazione accessoConfigurazione) throws DriverConfigurazioneException {

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("createAccessoConfigurazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createAccessoConfigurazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDAccessoConfigurazione type = 1");
			DriverConfigurazioneDB_configLIB.CRUDAccessoConfigurazione(1, accessoConfigurazione, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createAccessoConfigurazione] Errore durante la createAccessoConfigurazione : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected void updateAccessoConfigurazione(AccessoConfigurazione accessoConfigurazione) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updateAccessoConfigurazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateAccessoConfigurazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDAccessoConfigurazione type = 2");
			DriverConfigurazioneDB_configLIB.CRUDAccessoConfigurazione(2, accessoConfigurazione, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateAccessoConfigurazione] Errore durante la updateAccessoConfigurazione : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected void deleteAccessoConfigurazione(AccessoConfigurazione accessoConfigurazione) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("deleteAccessoConfigurazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteAccessoConfigurazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDAccessoConfigurazione type = 3");
			DriverConfigurazioneDB_configLIB.CRUDAccessoConfigurazione(3, accessoConfigurazione, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteAccessoConfigurazione] Errore durante la deleteAccessoConfigurazione : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
	
	
	
	
	
	protected AccessoDatiAutorizzazione getAccessoDatiAutorizzazione() throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getAccessoDatiAutorizzazione");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiAutorizzazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			AccessoDatiAutorizzazione accessoDatiAutorizzazione = new AccessoDatiAutorizzazione();
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE);
			sqlQueryObject.addSelectField("*");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			this.driver.logDebug("eseguo query : " + sqlQuery);

			rs = stm.executeQuery();

			if (rs.next()) {
				readAccessoDatiAutorizzazione(rs, accessoDatiAutorizzazione);
				rs.close();
				stm.close();
			}

			return accessoDatiAutorizzazione;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiAutorizzazione] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiAutorizzazione] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}
	}
	private void readAccessoDatiAutorizzazione(ResultSet rs, AccessoDatiAutorizzazione accessoDatiAutorizzazione) throws SQLException {
		String tmpCache = rs.getString("auth_statocache");
		if (CostantiConfigurazione.ABILITATO.equals(tmpCache)) {
			Cache cache = new Cache();

			String tmpDim = rs.getString("auth_dimensionecache");
			if (tmpDim != null && !tmpDim.equals(""))
				cache.setDimensione(tmpDim);

			String tmpAlg = rs.getString("auth_algoritmocache");
			if (tmpAlg.equalsIgnoreCase("LRU"))
				cache.setAlgoritmo(CostantiConfigurazione.CACHE_LRU);
			else
				cache.setAlgoritmo(CostantiConfigurazione.CACHE_MRU);

			String tmpIdle = rs.getString("auth_idlecache");
			String tmpLife = rs.getString("auth_lifecache");

			if (tmpIdle != null && !tmpIdle.equals(""))
				cache.setItemIdleTime(tmpIdle);
			if (tmpLife != null && !tmpLife.equals(""))
				cache.setItemLifeSecond(tmpLife);

			accessoDatiAutorizzazione.setCache(cache);

		}
	}
	
	
	
	protected AccessoDatiAutenticazione getAccessoDatiAutenticazione() throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getAccessoDatiAutenticazione");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiAutenticazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			AccessoDatiAutenticazione accessoDatiAutenticazione = new AccessoDatiAutenticazione();
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE);
			sqlQueryObject.addSelectField("*");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			this.driver.logDebug("eseguo query : " + sqlQuery);

			rs = stm.executeQuery();

			if (rs.next()) {
				readAccessoDatiAutenticazione(rs, accessoDatiAutenticazione);
				rs.close();
				stm.close();
			}

			return accessoDatiAutenticazione;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiAutenticazione] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiAutenticazione] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}
	}
	private void readAccessoDatiAutenticazione(ResultSet rs, AccessoDatiAutenticazione accessoDatiAutenticazione) throws SQLException {
		String tmpCache = rs.getString("authn_statocache");
		if (CostantiConfigurazione.ABILITATO.equals(tmpCache)) {
			Cache cache = new Cache();

			String tmpDim = rs.getString("authn_dimensionecache");
			if (tmpDim != null && !tmpDim.equals(""))
				cache.setDimensione(tmpDim);

			String tmpAlg = rs.getString("authn_algoritmocache");
			if (tmpAlg.equalsIgnoreCase("LRU"))
				cache.setAlgoritmo(CostantiConfigurazione.CACHE_LRU);
			else
				cache.setAlgoritmo(CostantiConfigurazione.CACHE_MRU);

			String tmpIdle = rs.getString("authn_idlecache");
			String tmpLife = rs.getString("authn_lifecache");

			if (tmpIdle != null && !tmpIdle.equals(""))
				cache.setItemIdleTime(tmpIdle);
			if (tmpLife != null && !tmpLife.equals(""))
				cache.setItemLifeSecond(tmpLife);

			accessoDatiAutenticazione.setCache(cache);

		}
	}
	
	
	protected AccessoDatiGestioneToken getAccessoDatiGestioneToken() throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getAccessoDatiGestioneToken");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiGestioneToken] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			AccessoDatiGestioneToken accessoDatiGestioneToken = new AccessoDatiGestioneToken();
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE);
			sqlQueryObject.addSelectField("*");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			this.driver.logDebug("eseguo query : " + sqlQuery);

			rs = stm.executeQuery();

			if (rs.next()) {
				readAccessoDatiGestioneToken(rs, accessoDatiGestioneToken);
				rs.close();
				stm.close();
			}

			return accessoDatiGestioneToken;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiGestioneToken] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiGestioneToken] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}
	}
	private void readAccessoDatiGestioneToken(ResultSet rs, AccessoDatiGestioneToken accessoDatiGestioneToken) throws SQLException {
		String tmpCache = rs.getString("token_statocache");
		if (CostantiConfigurazione.ABILITATO.equals(tmpCache)) {
			Cache cache = new Cache();

			String tmpDim = rs.getString("token_dimensionecache");
			if (tmpDim != null && !tmpDim.equals(""))
				cache.setDimensione(tmpDim);

			String tmpAlg = rs.getString("token_algoritmocache");
			if (tmpAlg.equalsIgnoreCase("LRU"))
				cache.setAlgoritmo(CostantiConfigurazione.CACHE_LRU);
			else
				cache.setAlgoritmo(CostantiConfigurazione.CACHE_MRU);

			String tmpIdle = rs.getString("token_idlecache");
			String tmpLife = rs.getString("token_lifecache");

			if (tmpIdle != null && !tmpIdle.equals(""))
				cache.setItemIdleTime(tmpIdle);
			if (tmpLife != null && !tmpLife.equals(""))
				cache.setItemLifeSecond(tmpLife);

			accessoDatiGestioneToken.setCache(cache);

		}
	}
	
	
	
	
	
	
	protected AccessoDatiAttributeAuthority getAccessoDatiAttributeAuthority() throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getAccessoDatiAttributeAuthority");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiAttributeAuthority] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			AccessoDatiAttributeAuthority accessoDatiAttributeAuthority = new AccessoDatiAttributeAuthority();
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE);
			sqlQueryObject.addSelectField("*");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			this.driver.logDebug("eseguo query : " + sqlQuery);

			rs = stm.executeQuery();

			if (rs.next()) {
				readAccessoDatiAttributeAuthority(rs, accessoDatiAttributeAuthority);
				rs.close();
				stm.close();
			}

			return accessoDatiAttributeAuthority;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiAttributeAuthority] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiAttributeAuthority] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}
	}
	private void readAccessoDatiAttributeAuthority(ResultSet rs, AccessoDatiAttributeAuthority accessoDatiAttributeAuthority) throws SQLException {
		String tmpCache = rs.getString("aa_statocache");
		if (CostantiConfigurazione.ABILITATO.equals(tmpCache)) {
			Cache cache = new Cache();

			String tmpDim = rs.getString("aa_dimensionecache");
			if (tmpDim != null && !tmpDim.equals(""))
				cache.setDimensione(tmpDim);

			String tmpAlg = rs.getString("aa_algoritmocache");
			if (tmpAlg.equalsIgnoreCase("LRU"))
				cache.setAlgoritmo(CostantiConfigurazione.CACHE_LRU);
			else
				cache.setAlgoritmo(CostantiConfigurazione.CACHE_MRU);

			String tmpIdle = rs.getString("aa_idlecache");
			String tmpLife = rs.getString("aa_lifecache");

			if (tmpIdle != null && !tmpIdle.equals(""))
				cache.setItemIdleTime(tmpIdle);
			if (tmpLife != null && !tmpLife.equals(""))
				cache.setItemLifeSecond(tmpLife);

			accessoDatiAttributeAuthority.setCache(cache);

		}
	}
	
	
	
	
	
	
	protected AccessoDatiKeystore getAccessoDatiKeystore() throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getAccessoDatiKeystore");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiKeystore] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			AccessoDatiKeystore accessoDatiKeystore = new AccessoDatiKeystore();

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE);
			sqlQueryObject.addSelectField("*");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			this.driver.logDebug("eseguo query : " + sqlQuery);

			rs = stm.executeQuery();

			if (rs.next()) {
				readAccessoDatiKeystore(rs, accessoDatiKeystore);
				rs.close();
				stm.close();
			}

			return accessoDatiKeystore;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiKeystore] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiKeystore] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}
	}
	private void readAccessoDatiKeystore(ResultSet rs, AccessoDatiKeystore accessoDatiKeystore) throws SQLException {
		String tmpCache = rs.getString("keystore_statocache");
		if (CostantiConfigurazione.ABILITATO.equals(tmpCache)) {
			Cache cache = new Cache();

			String tmpDim = rs.getString("keystore_dimensionecache");
			if (tmpDim != null && !tmpDim.equals(""))
				cache.setDimensione(tmpDim);

			String tmpAlg = rs.getString("keystore_algoritmocache");
			if (tmpAlg.equalsIgnoreCase("LRU"))
				cache.setAlgoritmo(CostantiConfigurazione.CACHE_LRU);
			else
				cache.setAlgoritmo(CostantiConfigurazione.CACHE_MRU);

			String tmpIdle = rs.getString("keystore_idlecache");
			String tmpLife = rs.getString("keystore_lifecache");
			

			if (tmpIdle != null && !tmpIdle.equals(""))
				cache.setItemIdleTime(tmpIdle);
			if (tmpLife != null && !tmpLife.equals(""))
				cache.setItemLifeSecond(tmpLife);

			accessoDatiKeystore.setCache(cache);
			
			readAccessoDatiKeystoreCrlLife(rs, accessoDatiKeystore);
		}
	}
	private void readAccessoDatiKeystoreCrlLife(ResultSet rs, AccessoDatiKeystore accessoDatiKeystore) throws SQLException {
		String tmpCrlLife = rs.getString("keystore_crl_lifecache");
		
		if (tmpCrlLife != null && !tmpCrlLife.equals(""))
			accessoDatiKeystore.setCrlItemLifeSecond(tmpCrlLife);
	}
	
	
	
	protected AccessoDatiConsegnaApplicativi getAccessoDatiConsegnaApplicativi() throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getAccessoDatiConsegnaApplicativi");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiConsegnaApplicativi] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			AccessoDatiConsegnaApplicativi accessoDatiConsegnaApplicativi = new AccessoDatiConsegnaApplicativi();
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE);
			sqlQueryObject.addSelectField("*");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			this.driver.logDebug("eseguo query : " + sqlQuery);

			rs = stm.executeQuery();

			if (rs.next()) {
				readAccessoDatiConsegnaApplicativi(rs, accessoDatiConsegnaApplicativi);
				rs.close();
				stm.close();
			}

			return accessoDatiConsegnaApplicativi;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiConsegnaApplicativi] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiConsegnaApplicativi] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}
	}
	private void readAccessoDatiConsegnaApplicativi(ResultSet rs, AccessoDatiConsegnaApplicativi accessoDatiConsegnaApplicativi) throws SQLException {
		String tmpCache = rs.getString("consegna_statocache");
		if (CostantiConfigurazione.ABILITATO.equals(tmpCache)) {
			Cache cache = new Cache();

			String tmpDim = rs.getString("consegna_dimensionecache");
			if (tmpDim != null && !tmpDim.equals(""))
				cache.setDimensione(tmpDim);

			String tmpAlg = rs.getString("consegna_algoritmocache");
			if (tmpAlg.equalsIgnoreCase("LRU"))
				cache.setAlgoritmo(CostantiConfigurazione.CACHE_LRU);
			else
				cache.setAlgoritmo(CostantiConfigurazione.CACHE_MRU);

			String tmpIdle = rs.getString("consegna_idlecache");
			String tmpLife = rs.getString("consegna_lifecache");
			
			if (tmpIdle != null && !tmpIdle.equals(""))
				cache.setItemIdleTime(tmpIdle);
			if (tmpLife != null && !tmpLife.equals(""))
				cache.setItemLifeSecond(tmpLife);

			accessoDatiConsegnaApplicativi.setCache(cache);
			
		}
	}
	
	
	protected AccessoDatiRichieste getAccessoDatiRichieste() throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getAccessoDatiRichieste");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiRichieste] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			AccessoDatiRichieste accessoDatiRichieste = new AccessoDatiRichieste();
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE);
			sqlQueryObject.addSelectField("*");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			this.driver.logDebug("eseguo query : " + sqlQuery);

			rs = stm.executeQuery();

			if (rs.next()) {
				readAccessoDatiRichieste(rs, accessoDatiRichieste);
				rs.close();
				stm.close();
			}

			return accessoDatiRichieste;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiRichieste] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiRichieste] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}
	}
	private void readAccessoDatiRichieste(ResultSet rs, AccessoDatiRichieste accessoDatiRichieste) throws SQLException {
		String tmpCache = rs.getString("dati_richieste_statocache");
		if (CostantiConfigurazione.ABILITATO.equals(tmpCache)) {
			Cache cache = new Cache();

			String tmpDim = rs.getString("dati_richieste_dimensionecache");
			if (tmpDim != null && !tmpDim.equals(""))
				cache.setDimensione(tmpDim);

			String tmpAlg = rs.getString("dati_richieste_algoritmocache");
			if (tmpAlg.equalsIgnoreCase("LRU"))
				cache.setAlgoritmo(CostantiConfigurazione.CACHE_LRU);
			else
				cache.setAlgoritmo(CostantiConfigurazione.CACHE_MRU);

			String tmpIdle = rs.getString("dati_richieste_idlecache");
			String tmpLife = rs.getString("dati_richieste_lifecache");
			
			if (tmpIdle != null && !tmpIdle.equals(""))
				cache.setItemIdleTime(tmpIdle);
			if (tmpLife != null && !tmpLife.equals(""))
				cache.setItemLifeSecond(tmpLife);

			accessoDatiRichieste.setCache(cache);

		}
	}
	
	

	protected void createAccessoDatiAutorizzazione(AccessoDatiAutorizzazione accessoDatiAutorizzazione) throws DriverConfigurazioneException {

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("createAccessoDatiAutorizzazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createAccessoDatiAutorizzazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDAccessoDatiAutorizzazione type = 1");
			DriverConfigurazioneDB_configLIB.CRUDAccessoDatiAutorizzazione(1, accessoDatiAutorizzazione, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createAccessoDatiAutorizzazione] Errore durante la createAccessoDatiAutorizzazione : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected void updateAccessoDatiAutorizzazione(AccessoDatiAutorizzazione accessoDatiAutorizzazione) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updateAccessoDatiAutorizzazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateAccessoDatiAutorizzazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDAccessoDatiAutorizzazione type = 2");
			DriverConfigurazioneDB_configLIB.CRUDAccessoDatiAutorizzazione(2, accessoDatiAutorizzazione, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateAccessoDatiAutorizzazione] Errore durante la updateAccessoDatiAutorizzazione : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected void deleteAccessoDatiAutorizzazione(AccessoDatiAutorizzazione accessoDatiAutorizzazione) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("deleteAccessoDatiAutorizzazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteAccessoDatiAutorizzazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDAccessoDatiAutorizzazione type = 3");
			DriverConfigurazioneDB_configLIB.CRUDAccessoDatiAutorizzazione(3, accessoDatiAutorizzazione, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteAccessoDatiAutorizzazione] Errore durante la deleteAccessoDatiAutorizzazione : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
	

	/**
	 * Restituisce la gestione dell'errore di default definita nella Porta di
	 * Dominio
	 * 
	 * @return La gestione dell'errore
	 * 
	 */
	protected StatoServiziPdd getStatoServiziPdD() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		PreparedStatement stmFiltri = null;
		ResultSet rsFiltri = null;

		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getStatoServiziPdD");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[getServiziAttiviPdD] Exception accedendo al datasource :" + e.getMessage(),e);

			}
		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			StatoServiziPdd servizi = null;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_PDD);
			sqlQuery = sqlQueryObject.createSQLQuery();

			ISQLQueryObject sqlQueryObjectFiltri = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObjectFiltri.addFromTable(CostantiDB.SERVIZI_PDD_FILTRI);
			sqlQueryObjectFiltri.addWhereCondition("id_servizio_pdd=?");
			String sqlQueryFiltro = sqlQueryObjectFiltri.createSQLQuery();

			this.driver.logDebug("eseguo query: " + DBUtils.formatSQLString(sqlQuery));
			stm = con.prepareStatement(sqlQuery);
			rs = stm.executeQuery();


			while (rs.next()) {

				if(servizi==null){
					servizi = new StatoServiziPdd();
				}

				long id = rs.getLong("id");

				String tipo = rs.getString("componente");

				int servizio = rs.getInt("stato");
				StatoFunzionalita stato = null;
				if(servizio==0)
					stato = CostantiConfigurazione.DISABILITATO;
				else if(servizio==1)
					stato = CostantiConfigurazione.ABILITATO;
				else
					stato = CostantiConfigurazione.ABILITATO; // default

				if(CostantiDB.COMPONENTE_SERVIZIO_PD.equals(tipo)){

					StatoServiziPddPortaDelegata sPD = new StatoServiziPddPortaDelegata();
					sPD.setStato(stato);

					this.driver.logDebug("eseguo query filtro: " + DBUtils.formatSQLString(sqlQueryFiltro,id));
					stmFiltri = con.prepareStatement(sqlQueryFiltro);
					stmFiltri.setLong(1, id);
					rsFiltri = stmFiltri.executeQuery();
					riempiFiltriServiziPdD(rsFiltri, sPD.getFiltroAbilitazioneList(), sPD.getFiltroDisabilitazioneList());
					rsFiltri.close();
					stmFiltri.close();

					servizi.setPortaDelegata(sPD);
				}
				else if(CostantiDB.COMPONENTE_SERVIZIO_PA.equals(tipo)){

					StatoServiziPddPortaApplicativa sPA = new StatoServiziPddPortaApplicativa();
					sPA.setStato(stato);

					this.driver.logDebug("eseguo query filtro: " + DBUtils.formatSQLString(sqlQueryFiltro,id));
					stmFiltri = con.prepareStatement(sqlQueryFiltro);
					stmFiltri.setLong(1, id);
					rsFiltri = stmFiltri.executeQuery();
					riempiFiltriServiziPdD(rsFiltri, sPA.getFiltroAbilitazioneList(), sPA.getFiltroDisabilitazioneList());
					rsFiltri.close();
					stmFiltri.close();

					servizi.setPortaApplicativa(sPA);
				}
				else if(CostantiDB.COMPONENTE_SERVIZIO_IM.equals(tipo)){

					StatoServiziPddIntegrationManager sIM = new StatoServiziPddIntegrationManager();
					sIM.setStato(stato);

					servizi.setIntegrationManager(sIM);
				}
			} 

			if(servizi==null)
				throw new DriverConfigurazioneNotFound("Configurazione servizi attivi sulla PdD non presente");

			rs.close();
			stm.close();

			return servizi;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[getServiziAttiviPdD]  SqlException: " + se.getMessage(),se);
		}catch (DriverConfigurazioneNotFound e) {
			throw new DriverConfigurazioneNotFound(e);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[getServiziAttiviPdD]  Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rsFiltri, stmFiltri);
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}

	}

	private void riempiFiltriServiziPdD(ResultSet rsFiltri,List<TipoFiltroAbilitazioneServizi> listaAbilitazioni,List<TipoFiltroAbilitazioneServizi> listaDisabilitazioni) throws SQLException{
		while(rsFiltri.next()){

			String tipo = rsFiltri.getString("tipo_filtro");

			TipoFiltroAbilitazioneServizi tipoFiltro = new TipoFiltroAbilitazioneServizi();

			String tipoSoggettoFruitore = rsFiltri.getString("tipo_soggetto_fruitore");
			tipoFiltro.setTipoSoggettoFruitore(tipoSoggettoFruitore);
			String soggettoFruitore = rsFiltri.getString("soggetto_fruitore");
			tipoFiltro.setSoggettoFruitore(soggettoFruitore);
			String identificativoPortaFruitore = rsFiltri.getString("identificativo_porta_fruitore");
			tipoFiltro.setIdentificativoPortaFruitore(identificativoPortaFruitore);

			String tipoSoggettoErogatore = rsFiltri.getString("tipo_soggetto_erogatore");
			tipoFiltro.setTipoSoggettoErogatore(tipoSoggettoErogatore);
			String soggettoErogatore = rsFiltri.getString("soggetto_erogatore");
			tipoFiltro.setSoggettoErogatore(soggettoErogatore);
			String identificativoPortaErogatore = rsFiltri.getString("identificativo_porta_erogatore");
			tipoFiltro.setIdentificativoPortaErogatore(identificativoPortaErogatore);

			String tipoServizio = rsFiltri.getString("tipo_servizio");
			tipoFiltro.setTipoServizio(tipoServizio);
			String servizio = rsFiltri.getString("servizio");
			tipoFiltro.setServizio(servizio);
			Integer versioneServizio = rsFiltri.getInt("versione_servizio");
			if(rsFiltri.wasNull()==false){
				tipoFiltro.setVersioneServizio(versioneServizio);
			}

			String azione = rsFiltri.getString("azione");
			tipoFiltro.setAzione(azione);

			if(CostantiDB.TIPO_FILTRO_ABILITAZIONE_SERVIZIO_PDD.equals(tipo)){
				listaAbilitazioni.add(tipoFiltro);
			}
			else{
				listaDisabilitazioni.add(tipoFiltro);
			}

		}
	}



	/**
	 * Crea le informazioni sui servizi attivi della PdD
	 * 
	 * @param servizi
	 * @throws DriverConfigurazioneException
	 */
	protected void createStatoServiziPdD(StatoServiziPdd servizi) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("createStatoServiziPdD");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createServiziAttiviPdD] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("createServiziAttiviPdD type = 1");
			DriverConfigurazioneDB_configLIB.CRUDServiziPdD(1, servizi, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createServiziAttiviPdD] Errore durante la createServiziAttiviPdD : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	/**
	 * Aggiorna le informazioni sui servizi attivi della PdD
	 * 
	 * @param servizi
	 * @throws DriverConfigurazioneException
	 */
	protected void updateStatoServiziPdD(StatoServiziPdd servizi) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updateStatoServiziPdD");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateServiziAttiviPdD] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("createServiziAttiviPdD type = 2");
			DriverConfigurazioneDB_configLIB.CRUDServiziPdD(2, servizi, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateServiziAttiviPdD] Errore durante la updateServiziAttiviPdD : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}


	/**
	 * Elimina le informazioni sui servizi attivi della PdD
	 * 
	 * @param servizi
	 * @throws DriverConfigurazioneException
	 */
	protected void deleteStatoServiziPdD(StatoServiziPdd servizi) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("deleteStatoServiziPdD");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteServiziAttiviPdD] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("createServiziAttiviPdD type = 3");
			DriverConfigurazioneDB_configLIB.CRUDServiziPdD(3, servizi, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteServiziAttiviPdD] Errore durante la deleteServiziAttiviPdD : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}





	


	/**
	 * Restituisce le proprieta' di sistema utilizzate dalla PdD
	 *
	 * @return proprieta' di sistema
	 * 
	 */
	protected SystemProperties getSystemPropertiesPdD() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getSystemPropertiesPdD");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[getSystemPropertiesPdD] Exception accedendo al datasource :" + e.getMessage(),e);

			}
		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			SystemProperties systemProperties = null;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SYSTEM_PROPERTIES_PDD);
			sqlQuery = sqlQueryObject.createSQLQuery();

			this.driver.logDebug("eseguo query: " + DBUtils.formatSQLString(sqlQuery));
			stm = con.prepareStatement(sqlQuery);
			rs = stm.executeQuery();


			while (rs.next()) {

				if(systemProperties==null){
					systemProperties = new SystemProperties();
				}

				long id = rs.getLong("id");

				String nome = rs.getString("nome");
				String valore = rs.getString("valore");

				Property sp = new Property();
				sp.setNome(nome);
				sp.setValore(valore);
				sp.setId(id);
				systemProperties.addSystemProperty(sp);

			} 

			if(systemProperties==null)
				throw new DriverConfigurazioneNotFound("System Properties non presenti");

			rs.close();
			stm.close();

			return systemProperties;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[getSystemPropertiesPdD]  SqlException: " + se.getMessage(),se);
		}catch (DriverConfigurazioneNotFound e) {
			throw new DriverConfigurazioneNotFound(e);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[getSystemPropertiesPdD]  Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}

	}
	
	protected void createSystemPropertiesPdD(SystemProperties systemProperties) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("createSystemPropertiesPdD");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createSystemPropertiesPdD] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("createSystemPropertiesPdD type = 1");
			DriverConfigurazioneDB_configLIB.CRUDSystemPropertiesPdD(1, systemProperties, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createSystemPropertiesPdD] Errore durante la createSystemPropertiesPdD : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	/**
	 * Aggiorna le informazioni sulle proprieta' di sistema utilizzate dalla PdD
	 * 
	 * @param systemProperties
	 * @throws DriverConfigurazioneException
	 */
	protected void updateSystemPropertiesPdD(SystemProperties systemProperties) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updateSystemPropertiesPdD");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateSystemPropertiesPdD] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("updateSystemPropertiesPdD type = 2");
			DriverConfigurazioneDB_configLIB.CRUDSystemPropertiesPdD(2, systemProperties, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateSystemPropertiesPdD] Errore durante la updateSystemPropertiesPdD : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}


	/**
	 * Elimina le informazioni sulle proprieta' di sistema utilizzate dalla PdD
	 * 
	 * @param systemProperties
	 * @throws DriverConfigurazioneException
	 */
	protected void deleteSystemPropertiesPdD(SystemProperties systemProperties) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("deleteSystemPropertiesPdD");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteSystemPropertiesPdD] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("deleteSystemPropertiesPdD type = 3");
			DriverConfigurazioneDB_configLIB.CRUDSystemPropertiesPdD(3, systemProperties, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteSystemPropertiesPdD] Errore durante la deleteSystemPropertiesPdD : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
	
	protected ConfigurazioneUrlInvocazioneRegola getUrlInvocazioneRegola(String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		
		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getUrlInvocazioneRegola");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[getUrlInvocazioneRegola] Exception accedendo al datasource :" + e.getMessage(),e);

			}
		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		ConfigurazioneUrlInvocazioneRegola regola = null;
		
		try {
			
			if(nome==null) {
				throw new DriverConfigurazioneException("Nome regola non indicato");
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_URL_REGOLE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome=?");
			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);	
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setString(1, nome);
			rs = stm.executeQuery();
			while(rs.next()){
				
				regola = new ConfigurazioneUrlInvocazioneRegola();
				readRegola(regola, rs);
				
			}
			rs.close();
			stm.close();

	
		}catch (DriverConfigurazioneNotFound notFound) {
			throw notFound;
		}catch (SQLException se) {
			throw new DriverConfigurazioneException("[getUrlInvocazioneRegola]  SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[getUrlInvocazioneRegolas]  Exception: " + se.getMessage(),se);
		}
		finally {
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}

		if(regola!=null) {
			return regola;
		}
		throw new DriverConfigurazioneNotFound("Regola '"+nome+"' non esistente");
	}
	
	protected boolean existsUrlInvocazioneRegola(String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		
		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("existsUrlInvocazioneRegola");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[existsUrlInvocazioneRegola] Exception accedendo al datasource :" + e.getMessage(),e);

			}
		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			
			if(nome==null) {
				throw new DriverConfigurazioneException("Nome regola non indicato");
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_URL_REGOLE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome=?");
			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);	
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setString(1, nome);
			rs = stm.executeQuery();
			while(rs.next()){
				
				return true;
				
			}
			rs.close();
			stm.close();

	
		}catch (SQLException se) {
			throw new DriverConfigurazioneException("[existsUrlInvocazioneRegola]  SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[existsUrlInvocazioneRegola]  Exception: " + se.getMessage(),se);
		}
		finally {
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}

		return false;
	}

	private void readRegola(ConfigurazioneUrlInvocazioneRegola regola, ResultSet rs) throws Exception {
		regola.setId(rs.getLong("id"));
		regola.setNome(rs.getString("nome"));
		regola.setPosizione(rs.getInt("posizione"));
		regola.setStato(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(rs.getString("stato")));
		regola.setDescrizione(rs.getString("descrizione"));
		if(rs.getInt("regexpr") == CostantiDB.TRUE) {
			regola.setRegexpr(true);
		}else {
			regola.setRegexpr(false);
		}
		regola.setRegola(rs.getString("regola"));
		// Fix stringa vuota in Oracle, impostato dalla console e non accettato da Oracle che lo traduce in null e fa schiantare per via del NOT NULL sul db
		String s = rs.getString("contesto_esterno");
		if(CostantiConfigurazione.REGOLA_PROXY_PASS_CONTESTO_VUOTO.equals(s)) {
			s = "";
		}
		regola.setContestoEsterno(s);
		regola.setBaseUrl(rs.getString("base_url"));
		regola.setProtocollo(rs.getString("protocollo"));
		regola.setRuolo(DriverConfigurazioneDBLib.getEnumRuoloContesto(rs.getString("ruolo")));
		regola.setServiceBinding(DriverConfigurazioneDBLib.getEnumServiceBinding(rs.getString("service_binding")));
		String tipoSoggetto = rs.getString("tipo_soggetto");
		String nomeSoggetto = rs.getString("nome_soggetto");
		if(tipoSoggetto!=null && !"".equals(tipoSoggetto) && nomeSoggetto!=null && !"".equals(nomeSoggetto)) {
			regola.setSoggetto(new IdSoggetto(new IDSoggetto(tipoSoggetto, nomeSoggetto)));
		}
	}

	protected void createUrlInvocazioneRegola(ConfigurazioneUrlInvocazioneRegola regola) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("createUrlInvocazioneRegola");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createUrlInvocazioneRegola] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDUrlInvocazioneRegola type = 1");
			DriverConfigurazioneDB_configLIB.CRUDUrlInvocazioneRegola(1, regola, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createUrlInvocazioneRegola] Errore durante la create : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected void updateUrlInvocazioneRegola(ConfigurazioneUrlInvocazioneRegola regola) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updateUrlInvocazioneRegola");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateUrlInvocazioneRegola] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDUrlInvocazioneRegola type = 2");
			DriverConfigurazioneDB_configLIB.CRUDUrlInvocazioneRegola(2, regola, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateUrlInvocazioneRegola] Errore durante l'aggiornamento : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected void deleteUrlInvocazioneRegola(ConfigurazioneUrlInvocazioneRegola regola) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("deleteUrlInvocazioneRegola");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteUrlInvocazioneRegola] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDUrlInvocazioneRegola type = 3");
			DriverConfigurazioneDB_configLIB.CRUDUrlInvocazioneRegola(3, regola, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteUrlInvocazioneRegola] Errore durante l'eliminazione : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
	
	
	
	
	
	
	
	/**
	 * Restituisce la configurazione generale della Porta di Dominio
	 * 
	 * @return Configurazione
	 * 
	 */
	protected Configurazione getConfigurazioneGenerale() throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		// ritorna la configurazione generale della PdD

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		PreparedStatement stm1 = null;
		ResultSet rs1 = null;
		PreparedStatement stm2 = null;
		ResultSet rs2 = null;

		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getConfigurazioneGenerale");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getConfigurazioneGenerale] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);
		Configurazione config = new Configurazione();
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE);
			sqlQueryObject.addSelectField("*");
			sqlQuery = sqlQueryObject.createSQLQuery();

			this.driver.logDebug("eseguo query: " + DBUtils.formatSQLString(sqlQuery));
			stm = con.prepareStatement(sqlQuery);
			rs = stm.executeQuery();

			if (rs.next()) {

				Attachments attachments = new Attachments();
				attachments.setGestioneManifest(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(rs.getString("gestione_manifest")));
				config.setAttachments(attachments);

				//config.setId(rs.getLong("id"));

				IndirizzoRisposta indirizzoRisposta = new IndirizzoRisposta();
				indirizzoRisposta.setUtilizzo(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(rs.getString("indirizzo_telematico")));
				config.setIndirizzoRisposta(indirizzoRisposta);

				String cadenza_inoltro = rs.getString("cadenza_inoltro");
				InoltroBusteNonRiscontrate inoltroBusteNonRiscontrate = new InoltroBusteNonRiscontrate();
				inoltroBusteNonRiscontrate.setCadenza(cadenza_inoltro);
				config.setInoltroBusteNonRiscontrate(inoltroBusteNonRiscontrate);

				String autenticazione = rs.getString("auth_integration_manager");
				IntegrationManager integrationManager = new IntegrationManager();
				integrationManager.setAutenticazione(autenticazione);
				config.setIntegrationManager(integrationManager);

				//String stato_cache = rs.getString("statocache");
				//String dim_cache = rs.getString("dimensionecache");
				//String alog_cache = rs.getString("algoritmocache");
				//String idle_cache = rs.getString("idlecache");
				//String life_cache = rs.getString("lifecache");


				boolean routingEnabled =  false;
				if(CostantiConfigurazione.ABILITATO.equals(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(rs.getString("routing_enabled"))))
					routingEnabled = true;
				RoutingTable rt = new RoutingTable();
				rt.setAbilitata(routingEnabled);
				config.setRoutingTable(rt);


				String validazioneContenuti_stato = rs.getString("validazione_contenuti_stato");
				String validazioneContenuti_tipo = rs.getString("validazione_contenuti_tipo");
				String validazioneContenuti_acceptMtomMessage = rs.getString("validazione_contenuti_mtom");
				if(  (validazioneContenuti_stato!=null && !validazioneContenuti_stato.equals(""))  
						||
						(validazioneContenuti_tipo!=null && !validazioneContenuti_tipo.equals(""))  
						||
						(validazioneContenuti_acceptMtomMessage!=null && !validazioneContenuti_acceptMtomMessage.equals("")))
				{
					ValidazioneContenutiApplicativi val = new ValidazioneContenutiApplicativi();
					if((validazioneContenuti_stato!=null && !validazioneContenuti_stato.equals(""))  )
						val.setStato(DriverConfigurazioneDBLib.getEnumStatoFunzionalitaConWarning(validazioneContenuti_stato));
					if((validazioneContenuti_tipo!=null && !validazioneContenuti_tipo.equals(""))  )
						val.setTipo(DriverConfigurazioneDBLib.getEnumValidazioneContenutiApplicativiTipo(validazioneContenuti_tipo));
					if((validazioneContenuti_acceptMtomMessage!=null && !validazioneContenuti_acceptMtomMessage.equals(""))  )
						val.setAcceptMtomMessage(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(validazioneContenuti_acceptMtomMessage));
					config.setValidazioneContenutiApplicativi(val);
				}


				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.CONFIG_URL_INVOCAZIONE);
				sqlQueryObject.addSelectField("*");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				rs1 = stm1.executeQuery();
				if(rs1.next()){
					ConfigurazioneUrlInvocazione configurazioneUrlInvocazione = new ConfigurazioneUrlInvocazione();
					configurazioneUrlInvocazione.setBaseUrl(rs1.getString("base_url"));
					configurazioneUrlInvocazione.setBaseUrlFruizione(rs1.getString("base_url_fruizione"));
					config.setUrlInvocazione(configurazioneUrlInvocazione);
				}
				rs1.close();
				stm1.close();
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.CONFIG_URL_REGOLE);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addOrderBy("posizione");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);	
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				rs1 = stm1.executeQuery();
				while(rs1.next()){
					
					if(config.getUrlInvocazione()==null) {
						config.setUrlInvocazione(new ConfigurazioneUrlInvocazione());
					}
					
					ConfigurazioneUrlInvocazioneRegola regola = new ConfigurazioneUrlInvocazioneRegola();
					readRegola(regola, rs1);
					config.getUrlInvocazione().addRegola(regola);
				}
				rs1.close();
				stm1.close();

				
				String multitenantStato = rs.getString("multitenant_stato");
				String multitenantStatoSoggettiFruitori = rs.getString("multitenant_fruizioni");
				String multitenantStatoSoggettiErogatori = rs.getString("multitenant_erogazioni");
				config.setMultitenant(new ConfigurazioneMultitenant());
				config.getMultitenant().setStato(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(multitenantStato));
				config.getMultitenant().setFruizioneSceltaSoggettiErogatori(DriverConfigurazioneDBLib.getEnumPortaDelegataSoggettiErogatori(multitenantStatoSoggettiFruitori));
				config.getMultitenant().setErogazioneSceltaSoggettiFruitori(DriverConfigurazioneDBLib.getEnumPortaApplicativaSoggettiFruitori(multitenantStatoSoggettiErogatori));

				String msg_diag_severita = rs.getString("msg_diag_severita");
				String msg_diag_severita_log4j = rs.getString("msg_diag_severita_log4j");
				MessaggiDiagnostici messaggiDiagnostici = new MessaggiDiagnostici();
				messaggiDiagnostici.setSeveritaLog4j(DriverConfigurazioneDBLib.getEnumSeverita(msg_diag_severita_log4j));
				messaggiDiagnostici.setSeverita(DriverConfigurazioneDBLib.getEnumSeverita(msg_diag_severita));
				//messaggi diagnostici appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.MSG_DIAGN_APPENDER);
				sqlQueryObject.addSelectField("*");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				rs1 = stm1.executeQuery();

				while(rs1.next()){
					OpenspcoopAppender appender = new OpenspcoopAppender();
					//tipo appender
					appender.setTipo(rs1.getString("tipo"));
					long idAppender = rs1.getLong("id");
					appender.setId(idAppender);
					//prendo le proprieta
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.MSG_DIAGN_APPENDER_PROP);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_appender = ?");
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm2 = con.prepareStatement(sqlQuery);
					stm2.setLong(1, idAppender);
					rs2 = stm2.executeQuery();
					Property appender_prop = null;
					while(rs2.next())
					{
						appender_prop = new Property();
						//proprieta
						appender_prop.setId(rs2.getLong("id"));
						appender_prop.setNome(rs2.getString("nome"));
						appender_prop.setValore(rs2.getString("valore"));
						appender.addProperty(appender_prop);
					}
					rs2.close();
					stm2.close();
					messaggiDiagnostici.addOpenspcoopAppender(appender);
				}
				rs1.close();
				stm1.close();

				//messaggi diagnostici datasource
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.MSG_DIAGN_DS);
				sqlQueryObject.addSelectField("*");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				rs1 = stm1.executeQuery();

				while(rs1.next()){
					OpenspcoopSorgenteDati openspcoopDS = new OpenspcoopSorgenteDati();
					openspcoopDS.setNome(rs1.getString("nome"));
					openspcoopDS.setNomeJndi(rs1.getString("nome_jndi"));
					openspcoopDS.setTipoDatabase(rs1.getString("tipo_database"));
					long idDS = rs1.getLong("id");
					openspcoopDS.setId(idDS);
					//prendo le proprieta
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.MSG_DIAGN_DS_PROP);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_prop = ?");
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm2 = con.prepareStatement(sqlQuery);
					stm2.setLong(1, idDS);
					rs2 = stm2.executeQuery();
					Property ds_prop = null;
					while(rs2.next())
					{
						ds_prop = new Property();
						//proprieta
						ds_prop.setId(rs2.getLong("id"));
						ds_prop.setNome(rs2.getString("nome"));
						ds_prop.setValore(rs2.getString("valore"));
						openspcoopDS.addProperty(ds_prop);
					}
					rs2.close();
					stm2.close();
					messaggiDiagnostici.addOpenspcoopSorgenteDati(openspcoopDS);
				}
				rs1.close();
				stm1.close();

				config.setMessaggiDiagnostici(messaggiDiagnostici);

				
				
				//Tracciamento
				Tracciamento tracciamento = new Tracciamento();
								
				String traccBuste = rs.getString("tracciamento_buste");
				tracciamento.setStato(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(traccBuste));
				
							
				// porta applicativa
				String traccEsitiPA = rs.getString("tracciamento_esiti");
				String transazioniTempiElaborazionePA = rs.getString("transazioni_tempi");
				String transazioniTokenPA = rs.getString("transazioni_token");
				TracciamentoConfigurazione tracciamentoDatabasePA = DriverConfigurazioneDBTracciamentoLIB.readTracciamentoConfigurazione(con, null, 
						CostantiDB.TRACCIAMENTO_CONFIGURAZIONE_PROPRIETARIO_CONFIG_PA,
						CostantiDB.TRACCIAMENTO_CONFIGURAZIONE_TIPO_DB);
				TracciamentoConfigurazione tracciamentoFiletracePA = DriverConfigurazioneDBTracciamentoLIB.readTracciamentoConfigurazione(con, null, 
						CostantiDB.TRACCIAMENTO_CONFIGURAZIONE_PROPRIETARIO_CONFIG_PA,
						CostantiDB.TRACCIAMENTO_CONFIGURAZIONE_TIPO_FILETRACE);
				TracciamentoConfigurazioneFiletrace tracciamentoFiletraceDetailsPA = DriverConfigurazioneDBTracciamentoLIB.readTracciamentoConfigurazioneFiletrace(con, null, 
						CostantiDB.TRACCIAMENTO_CONFIGURAZIONE_PROPRIETARIO_CONFIG_PA);
				if( 
						(traccEsitiPA!=null && StringUtils.isNotEmpty(traccEsitiPA))
						||
						(transazioniTempiElaborazionePA!=null && StringUtils.isNotEmpty(transazioniTempiElaborazionePA))
						||
						(transazioniTokenPA!=null && StringUtils.isNotEmpty(transazioniTokenPA))
						||
						tracciamentoDatabasePA!=null
						||
						tracciamentoFiletracePA!=null
						||
						tracciamentoFiletraceDetailsPA!=null
						) {
					tracciamento.setPortaApplicativa(new ConfigurazioneTracciamentoPorta());
					tracciamento.getPortaApplicativa().setEsiti(traccEsitiPA);
					if( 
							(transazioniTempiElaborazionePA!=null && StringUtils.isNotEmpty(transazioniTempiElaborazionePA))
							||
							(transazioniTokenPA!=null && StringUtils.isNotEmpty(transazioniTokenPA))
							) {
						tracciamento.getPortaApplicativa().setTransazioni(new Transazioni());
						tracciamento.getPortaApplicativa().getTransazioni().setTempiElaborazione(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(transazioniTempiElaborazionePA));
						tracciamento.getPortaApplicativa().getTransazioni().setToken(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(transazioniTokenPA));
					}
					tracciamento.getPortaApplicativa().setDatabase(tracciamentoDatabasePA);
					tracciamento.getPortaApplicativa().setFiletrace(tracciamentoFiletracePA);
					tracciamento.getPortaApplicativa().setFiletraceConfig(tracciamentoFiletraceDetailsPA);
				}

				
				// porta delegata
				String traccEsitiPD = rs.getString("tracciamento_esiti_pd");
				String transazioniTempiElaborazionePD = rs.getString("transazioni_tempi_pd");
				String transazioniTokenPD = rs.getString("transazioni_token_pd");
				TracciamentoConfigurazione tracciamentoDatabasePD = DriverConfigurazioneDBTracciamentoLIB.readTracciamentoConfigurazione(con, null, 
						CostantiDB.TRACCIAMENTO_CONFIGURAZIONE_PROPRIETARIO_CONFIG_PD,
						CostantiDB.TRACCIAMENTO_CONFIGURAZIONE_TIPO_DB);
				TracciamentoConfigurazione tracciamentoFiletracePD = DriverConfigurazioneDBTracciamentoLIB.readTracciamentoConfigurazione(con, null, 
						CostantiDB.TRACCIAMENTO_CONFIGURAZIONE_PROPRIETARIO_CONFIG_PD,
						CostantiDB.TRACCIAMENTO_CONFIGURAZIONE_TIPO_FILETRACE);
				TracciamentoConfigurazioneFiletrace tracciamentoFiletraceDetailsPD = DriverConfigurazioneDBTracciamentoLIB.readTracciamentoConfigurazioneFiletrace(con, null, 
						CostantiDB.TRACCIAMENTO_CONFIGURAZIONE_PROPRIETARIO_CONFIG_PD);
				if( 
						(traccEsitiPD!=null && StringUtils.isNotEmpty(traccEsitiPD))
						||
						(transazioniTempiElaborazionePD!=null && StringUtils.isNotEmpty(transazioniTempiElaborazionePD))
						||
						(transazioniTokenPD!=null && StringUtils.isNotEmpty(transazioniTokenPD))
						||
						tracciamentoDatabasePD!=null
						||
						tracciamentoFiletracePD!=null
						||
						tracciamentoFiletraceDetailsPD!=null
						) {
					tracciamento.setPortaDelegata(new ConfigurazioneTracciamentoPorta());
					tracciamento.getPortaDelegata().setEsiti(traccEsitiPD);
					if( 
							(transazioniTempiElaborazionePD!=null && StringUtils.isNotEmpty(transazioniTempiElaborazionePD))
							||
							(transazioniTokenPD!=null && StringUtils.isNotEmpty(transazioniTokenPD))
							) {
						tracciamento.getPortaDelegata().setTransazioni(new Transazioni());
						tracciamento.getPortaDelegata().getTransazioni().setTempiElaborazione(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(transazioniTempiElaborazionePD));
						tracciamento.getPortaDelegata().getTransazioni().setToken(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(transazioniTokenPD));
					}
					tracciamento.getPortaDelegata().setDatabase(tracciamentoDatabasePD);
					tracciamento.getPortaDelegata().setFiletrace(tracciamentoFiletracePD);
					tracciamento.getPortaDelegata().setFiletraceConfig(tracciamentoFiletraceDetailsPD);
				}
				
				
				
				
				//appender tracciamento
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.TRACCIAMENTO_APPENDER);
				sqlQueryObject.addSelectField("*");
				sqlQuery = sqlQueryObject.createSQLQuery();

				stm1 = con.prepareStatement(sqlQuery);
				rs1 = stm1.executeQuery();
				//recuper tutti gli appender e le prop di ogni appender
				while(rs1.next()){
					OpenspcoopAppender trac_appender = new OpenspcoopAppender();
					//tipo appender
					trac_appender.setTipo(rs1.getString("tipo"));
					long idAppenderTrac = rs1.getLong("id");
					trac_appender.setId(idAppenderTrac);
					//prendo le proprieta
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.TRACCIAMENTO_APPENDER_PROP);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_appender = ?");
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm2 = con.prepareStatement(sqlQuery);
					stm2.setLong(1, idAppenderTrac);
					rs2 = stm2.executeQuery();
					Property trac_appender_prop = null;
					while(rs2.next())
					{
						//setto le prop di questo appender
						trac_appender_prop = new Property();
						//proprieta
						trac_appender_prop.setId(rs2.getLong("id"));
						trac_appender_prop.setNome(rs2.getString("nome"));
						trac_appender_prop.setValore(rs2.getString("valore"));
						//aggiungo la prop all'appender
						trac_appender.addProperty(trac_appender_prop);
					}
					rs2.close();
					stm2.close();
					tracciamento.addOpenspcoopAppender(trac_appender);
				}
				rs1.close();
				stm1.close();

				//datasource tracciamento
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.TRACCIAMENTO_DS);
				sqlQueryObject.addSelectField("*");
				sqlQuery = sqlQueryObject.createSQLQuery();

				stm1 = con.prepareStatement(sqlQuery);
				rs1 = stm1.executeQuery();
				//recuper tutti i datasource e le prop di ogni datasource
				while(rs1.next()){
					OpenspcoopSorgenteDati trac_ds = new OpenspcoopSorgenteDati();
					trac_ds.setNome(rs1.getString("nome"));
					trac_ds.setNomeJndi(rs1.getString("nome_jndi"));
					trac_ds.setTipoDatabase(rs1.getString("tipo_database"));
					long idDsTrac = rs1.getLong("id");
					trac_ds.setId(idDsTrac);
					//prendo le proprieta
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.TRACCIAMENTO_DS_PROP);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_prop = ?");
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm2 = con.prepareStatement(sqlQuery);
					stm2.setLong(1, idDsTrac);
					rs2 = stm2.executeQuery();
					Property trac_ds_prop = null;
					while(rs2.next())
					{
						//setto le prop di questo datasource
						trac_ds_prop = new Property();
						//proprieta
						trac_ds_prop.setId(rs2.getLong("id"));
						trac_ds_prop.setNome(rs2.getString("nome"));
						trac_ds_prop.setValore(rs2.getString("valore"));
						//aggiungo la prop al datasource
						trac_ds.addProperty(trac_ds_prop);
					}
					rs2.close();
					stm2.close();
					tracciamento.addOpenspcoopSorgenteDati(trac_ds);
				}
				rs1.close();
				stm1.close();

				config.setTracciamento(tracciamento);

				
				
				// Dump
				String dump_stato = rs.getString("dump");
				String dump_pd = rs.getString("dump_bin_pd");
				String dump_pa = rs.getString("dump_bin_pa");
				Dump dump = new Dump();
				dump.setStato(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(dump_stato));
				dump.setDumpBinarioPortaDelegata(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(dump_pd));
				if(dump.getDumpBinarioPortaDelegata()==null){
					dump.setDumpBinarioPortaDelegata(StatoFunzionalita.DISABILITATO); // default
				}
				dump.setDumpBinarioPortaApplicativa(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(dump_pa));
				if(dump.getDumpBinarioPortaApplicativa()==null){
					dump.setDumpBinarioPortaApplicativa(StatoFunzionalita.DISABILITATO); // default
				}
				//appender dump
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.DUMP_APPENDER);
				sqlQueryObject.addSelectField("*");
				sqlQuery = sqlQueryObject.createSQLQuery();

				stm1 = con.prepareStatement(sqlQuery);
				rs1 = stm1.executeQuery();
				//recuper tutti gli appender e le prop di ogni appender
				while(rs1.next()){
					OpenspcoopAppender dump_appender = new OpenspcoopAppender();
					//tipo appender
					dump_appender.setTipo(rs1.getString("tipo"));
					long idAppenderDump = rs1.getLong("id");
					dump_appender.setId(idAppenderDump);
					//prendo le proprieta
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.DUMP_APPENDER_PROP);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_appender = ?");
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm2 = con.prepareStatement(sqlQuery);
					stm2.setLong(1, idAppenderDump);
					rs2 = stm2.executeQuery();
					Property dump_appender_prop = null;
					while(rs2.next())
					{
						//setto le prop di questo appender
						dump_appender_prop = new Property();
						//proprieta
						dump_appender_prop.setId(rs2.getLong("id"));
						dump_appender_prop.setNome(rs2.getString("nome"));
						dump_appender_prop.setValore(rs2.getString("valore"));
						//aggiungo la prop all'appender
						dump_appender.addProperty(dump_appender_prop);
					}
					rs2.close();
					stm2.close();
					dump.addOpenspcoopAppender(dump_appender);
				}
				rs1.close();
				stm1.close();

				// dump_config
				DumpConfigurazione dumpConfig = DriverConfigurazioneDB_dumpLIB.readDumpConfigurazione(con, null, CostantiDB.OLD_BACKWARD_COMPATIBILITY_DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG);
				if(dumpConfig!=null) {
					// backward compatibility, lo uso sia per erogazione che per fruizione
					dump.setConfigurazionePortaApplicativa(dumpConfig);
					dump.setConfigurazionePortaDelegata(dumpConfig);
				}
				else {
					DumpConfigurazione dumpConfigPA = DriverConfigurazioneDB_dumpLIB.readDumpConfigurazione(con, null, CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG_PA);
					dump.setConfigurazionePortaApplicativa(dumpConfigPA);
					
					DumpConfigurazione dumpConfigPD = DriverConfigurazioneDB_dumpLIB.readDumpConfigurazione(con, null, CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG_PD);
					dump.setConfigurazionePortaDelegata(dumpConfigPD);
				}
				
				
				config.setDump(dump);
				
				
				Risposte risposte = new Risposte();
				risposte.setConnessione(DriverConfigurazioneDBLib.getEnumTipoConnessioneRisposte(rs.getString("mod_risposta")));
				config.setRisposte(risposte);

				String val_controllo = rs.getString("validazione_controllo");
				String val_stato = rs.getString("validazione_stato");
				String val_manifest = rs.getString("validazione_manifest");
				String val_profilo = rs.getString("validazione_profilo");
				ValidazioneBuste validazioneBuste = new ValidazioneBuste();
				validazioneBuste.setControllo(DriverConfigurazioneDBLib.getEnumValidazioneBusteTipoControllo(val_controllo));
				validazioneBuste.setManifestAttachments(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(val_manifest));
				validazioneBuste.setProfiloCollaborazione(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(val_profilo));
				validazioneBuste.setStato(DriverConfigurazioneDBLib.getEnumStatoFunzionalitaConWarning(val_stato));
				config.setValidazioneBuste(validazioneBuste);

				// Gestione CORS
				config.setGestioneCors(new CorsConfigurazione());
				this.porteDriver.readConfigurazioneCors(config.getGestioneCors(), rs);
				
				// Gestione CacheResponse
				config.setResponseCaching(new ResponseCachingConfigurazioneGenerale());
				
				config.getResponseCaching().setConfigurazione(new ResponseCachingConfigurazione());
				this.porteDriver.readResponseCaching(null, true, false, config.getResponseCaching().getConfigurazione(), rs, con);
				
				String tmpCache = rs.getString("response_cache_statocache");
				if (CostantiConfigurazione.ABILITATO.equals(tmpCache)) {
					Cache cache = new Cache();

					String tmpDim = rs.getString("response_cache_dimensionecache");
					if (tmpDim != null && !tmpDim.equals(""))
						cache.setDimensione(tmpDim);

					String tmpAlg = rs.getString("response_cache_algoritmocache");
					if (tmpAlg.equalsIgnoreCase("LRU"))
						cache.setAlgoritmo(CostantiConfigurazione.CACHE_LRU);
					else
						cache.setAlgoritmo(CostantiConfigurazione.CACHE_MRU);

					String tmpIdle = rs.getString("response_cache_idlecache");
					String tmpLife = rs.getString("response_cache_lifecache");

					if (tmpIdle != null && !tmpIdle.equals(""))
						cache.setItemIdleTime(tmpIdle);
					if (tmpLife != null && !tmpLife.equals(""))
						cache.setItemLifeSecond(tmpLife);

					config.getResponseCaching().setCache(cache);

				}
				
				// Canali
				String canali_stato = rs.getString("canali_stato");
				config.setGestioneCanali(new CanaliConfigurazione());
				config.getGestioneCanali().setStato(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(canali_stato));
				if(StatoFunzionalita.ABILITATO.equals(config.getGestioneCanali().getStato())) {
					DriverConfigurazioneDB_canaliLIB.readCanaliConfigurazione(con, config.getGestioneCanali(), true);
				}
				
				// Handlers
				ConfigurazioneMessageHandlers requestHandlers = DriverConfigurazioneDB_handlerLIB.readConfigurazioneMessageHandlers(con, null, null, true);
				ConfigurazioneMessageHandlers responseHandlers = DriverConfigurazioneDB_handlerLIB.readConfigurazioneMessageHandlers(con, null, null, false);
				ConfigurazioneServiceHandlers serviceHandlers = DriverConfigurazioneDB_handlerLIB.readConfigurazioneServiceHandlers(con, null, null, false);
				if(requestHandlers!=null || responseHandlers!=null || serviceHandlers!=null) {
					config.setConfigurazioneHandler(new ConfigurazioneGeneraleHandler());
					config.getConfigurazioneHandler().setRequest(requestHandlers);
					config.getConfigurazioneHandler().setResponse(responseHandlers);
					config.getConfigurazioneHandler().setService(serviceHandlers);
				}
				
				ExtendedInfoManager extInfoManager = ExtendedInfoManager.getInstance();
				IExtendedInfo extInfoConfigurazioneDriver = extInfoManager.newInstanceExtendedInfoConfigurazione();
				if(extInfoConfigurazioneDriver!=null){
					List<Object> list = extInfoConfigurazioneDriver.getAllExtendedInfo(con, this.driver.log, config);
					if(list!=null && !list.isEmpty()){
						config.setExtendedInfoList(list);
					}
				}
				
			} else {
				throw new DriverConfigurazioneNotFound("[DriverConfigurazioneDB::getConfigurazioneGenerale] Configurazione non presente.");
			}

		} catch (DriverConfigurazioneNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getConfigurazioneGenerale] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			JDBCUtilities.closeResources(rs1, stm1);
			JDBCUtilities.closeResources(rs2, stm2);
			this.driver.closeConnection(con);
		}


		// Altre parti di configurazione. Le prendo dopo aver rilasciato la connessione, per permettere di far funzionare il driver anche con pool di una sola connessione. 

		// - GestioneErrore
		ConfigurazioneGestioneErrore cge = null;

		try{
			GestioneErrore ge = this.gestioneErroreDriver.getGestioneErroreComponenteIntegrazione();

			if(ge!=null){
				if(cge==null) cge = new ConfigurazioneGestioneErrore();
				cge.setComponenteIntegrazione(ge);
			}
		}catch (Exception e) {
			// ignore
		}

		try{
			GestioneErrore ge = this.gestioneErroreDriver.getGestioneErroreComponenteCooperazione();
			if(ge!=null){
				if(cge==null) cge = new ConfigurazioneGestioneErrore();
				cge.setComponenteCooperazione(ge);
			}
		}catch (Exception e) {
			// ignore
		}

		if(cge!=null) config.setGestioneErrore(cge);

		// - AccessoRegistro
		try{
			config.setAccessoRegistro(getAccessoRegistro());
		}catch (Exception e) {
			// ignore
		}
		
		// - AccessoConfigurazione
		try{
			config.setAccessoConfigurazione(getAccessoConfigurazione());
		}catch (Exception e) {
			// ignore
		}
		
		// - AccessoDatiAutorizzazione
		try{
			config.setAccessoDatiAutorizzazione(getAccessoDatiAutorizzazione());
		}catch (Exception e) {
			// ignore
		}
		
		// - AccessoDatiAutenticazione
		try{
			config.setAccessoDatiAutenticazione(getAccessoDatiAutenticazione());
		}catch (Exception e) {
			// ignore
		}
		
		// - AccessoDatiGestioneToken
		try{
			config.setAccessoDatiGestioneToken(getAccessoDatiGestioneToken());
		}catch (Exception e) {
			// ignore
		}
		
		// - AccessoDatiAttributeAuthority
		try{
			config.setAccessoDatiAttributeAuthority(getAccessoDatiAttributeAuthority());
		}catch (Exception e) {
			// ignore
		}
		
		// - AccessoDatiKeystore
		try{
			config.setAccessoDatiKeystore(getAccessoDatiKeystore());
		}catch (Exception e) {
			// ignore
		}
		
		// - AccessoDatiConsegnaApplicativi
		try{
			config.setAccessoDatiConsegnaApplicativi(getAccessoDatiConsegnaApplicativi());
		}catch (Exception e) {
			// ignore
		}
		
		// - AccessoDatiRichieste
		try{
			config.setAccessoDatiRichieste(getAccessoDatiRichieste());
		}catch (Exception e) {
			// ignore
		}

		// - RoutingTable
		try{
			config.setRoutingTable(this.routingTableDriver.getRoutingTable());
		}catch (Exception e) {
			// ignore
		}

		// - ServiziPdD
		try{
			config.setStatoServiziPdd(getStatoServiziPdD());
		}catch (Exception e) {
			// ignore
		}

		// - SystemProperties
		try{
			config.setSystemProperties(getSystemPropertiesPdD());
		}catch (Exception e) {
			// ignore
		}

		// - GenericProperties
		try{
			config.getGenericPropertiesList().addAll(this.genericPropertiesDriver.getGenericProperties());
		}catch (Exception e) {
			// ignore
		}

		return config;
	}

	
	
	
	
	protected Object getConfigurazioneExtended(Configurazione config, String idExtendedConfiguration) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getConfigurazioneExtended");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getConfigurazioneExtended] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;
		
		try {
			
			this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);
			
			this.driver.logDebug("getConfigurazioneExtended("+idExtendedConfiguration+")");
			
			ExtendedInfoManager extInfoManager = ExtendedInfoManager.getInstance();
			IExtendedInfo extInfoConfigurazioneDriver = extInfoManager.newInstanceExtendedInfoConfigurazione();
			if(extInfoConfigurazioneDriver!=null){
				
				Object o = extInfoConfigurazioneDriver.getExtendedInfo(con, this.driver.log, config, idExtendedConfiguration);
				if(o==null){
					throw new DriverConfigurazioneNotFound("Oggetto non esistente");
				}
				return o;
				
			}	
			
			throw new DriverConfigurazioneException("Driver non inizializzato");

		} 
		catch (DriverConfigurazioneNotFound dNot) {
			throw dNot;
		}
		catch (Exception qe) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getConfigurazioneExtended] Errore: " + qe.getMessage(),qe);
		} 
		finally {
			
			this.driver.closeConnection(con);
		}
	}
	


	protected void createConfigurazione(Configurazione configurazione) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("createConfigurazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createConfigurazioneGenerale] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDConfigurazioneGenerale type = 1");
			DriverConfigurazioneDB_configLIB.CRUDConfigurazioneGenerale(1, configurazione, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createConfigurazioneGenerale] Errore durante la create ConfigurazioneGenerale : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	
	protected void updateConfigurazione(Configurazione configurazione) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updateConfigurazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateConfigurazioneGenerale] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDConfigurazioneGenerale type = 2");
			DriverConfigurazioneDB_configLIB.CRUDConfigurazioneGenerale(2, configurazione, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateConfigurazioneGenerale] Errore durante la update ConfigurazioneGenerale : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	
	protected void deleteConfigurazione(Configurazione configurazione) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("deleteConfigurazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteConfigurazioneGenerale] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDConfigurazioneGenerale type = 3");
			DriverConfigurazioneDB_configLIB.CRUDConfigurazioneGenerale(CostantiDB.DELETE, configurazione, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteConfigurazioneGenerale] Errore durante la delete ConfigurazioneGenerale : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
	

	// ACCESSO REGISTRO

	/**
	 * Crea le informazioni per l'accesso ai registri
	 * 
	 * @param registro
	 * @throws DriverConfigurazioneException
	 */

	protected void createAccessoRegistro(AccessoRegistro registro) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("createAccessoRegistro");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createAccessoRegistro] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDAccessoRegistro type = 1");
			DriverConfigurazioneDB_configLIB.CRUDAccessoRegistro(1, registro, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createAccessoRegistro] Errore durante la updateAccessoRegistro : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	/**
	 * Aggiorna le informazioni per l'accesso ai registri
	 * 
	 * @param registro
	 * @throws DriverConfigurazioneException
	 */
	protected void updateAccessoRegistro(AccessoRegistro registro) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updateAccessoRegistro");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateAccessoRegistro] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDAccessoRegistro type = 2");
			DriverConfigurazioneDB_configLIB.CRUDAccessoRegistro(2, registro, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateAccessoRegistro] Errore durante la updateAccessoRegistro : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	/**
	 * Elimina le informazioni per l'accesso ai registri
	 * 
	 * @param registro
	 * @throws DriverConfigurazioneException
	 */
	protected void deleteAccessoRegistro(AccessoRegistro registro) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("deleteAccessoRegistro");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteAccessoRegistro] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDAccessoRegistro type = 3");
			DriverConfigurazioneDB_configLIB.CRUDAccessoRegistro(3, registro, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteAccessoRegistro] Errore durante la updateAccessoRegistro : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
	
	protected boolean existsResponseCachingConfigurazioneRegola(Integer statusMin, Integer statusMax, boolean fault) throws DriverConfigurazioneException {
		String nomeMetodo = "existsResponseCachingConfigurazioneRegola";

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		String queryString;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("existsResponseCachingConfigurazioneRegola");
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
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE_CACHE_REGOLE);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.setANDLogicOperator(true);
			
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
	
	protected boolean existsProxyPassConfigurazioneRegola(String nome) throws DriverConfigurazioneException {
		String nomeMetodo = "existsResponseCachingConfigurazioneRegola";

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		String queryString;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("existsResponseCachingConfigurazioneRegola");
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
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_URL_REGOLE);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition("nome = ?");
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setString(parameterIndex ++, nome);
			
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
	
	protected boolean existsCanale(String nome) throws DriverConfigurazioneException {
		String nomeMetodo = "existsCanale";

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		String queryString;

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
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE_CANALI);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition("nome = ?");
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setString(parameterIndex ++, nome);
			
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
	
	protected boolean existsCanaleNodo(String nome) throws DriverConfigurazioneException {
		String nomeMetodo = "existsCanaleNodo";

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		String queryString;

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
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE_CANALI_NODI);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition("nome = ?");
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setString(parameterIndex ++, nome);
			
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
	
	/**
	 * Restituisce la configurazione dei canali
	 * 
	 * @return Configurazione
	 * 
	 */
	protected CanaliConfigurazione getCanaliConfigurazione() throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.getCanaliConfigurazione(true);
	}
	protected CanaliConfigurazione getCanaliConfigurazione(boolean readNodi) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
	
		// ritorna la configurazione generale della PdD

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		PreparedStatement stm1 = null;
		ResultSet rs1 = null;
		PreparedStatement stm2 = null;
		ResultSet rs2 = null;

		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getCanaliConfigurazione");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getCanaliConfigurazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);
		CanaliConfigurazione config = new CanaliConfigurazione();
		config.setStato(StatoFunzionalita.DISABILITATO);
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE);
			sqlQueryObject.addSelectField(CostantiDB.CONFIGURAZIONE + ".canali_stato");
			sqlQuery = sqlQueryObject.createSQLQuery();

			this.driver.logDebug("eseguo query: " + DBUtils.formatSQLString(sqlQuery));
			stm = con.prepareStatement(sqlQuery);
			rs = stm.executeQuery();

			if (rs.next()) {

				// Canali
				String canali_stato = rs.getString("canali_stato");
				config.setStato(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(canali_stato));
				if(config.getStato()==null) {
					config.setStato(StatoFunzionalita.DISABILITATO);
				}
				if(StatoFunzionalita.ABILITATO.equals(config.getStato())) {
					DriverConfigurazioneDB_canaliLIB.readCanaliConfigurazione(con, config, false);
				}
			} else {
				throw new DriverConfigurazioneNotFound("[DriverConfigurazioneDB::getCanaliConfigurazione] Configurazione non presente.");
			}

		} catch (DriverConfigurazioneNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getCanaliConfigurazione] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			JDBCUtilities.closeResources(rs1, stm1);
			JDBCUtilities.closeResources(rs2, stm2);
			this.driver.closeConnection(con);
		}
		return config;
	}
}
