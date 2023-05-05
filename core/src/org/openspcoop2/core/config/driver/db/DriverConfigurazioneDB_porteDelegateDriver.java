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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.IExtendedInfo;
import org.openspcoop2.core.config.AttributeAuthority;
import org.openspcoop2.core.config.AutorizzazioneRuoli;
import org.openspcoop2.core.config.AutorizzazioneScope;
import org.openspcoop2.core.config.ConfigurazioneMessageHandlers;
import org.openspcoop2.core.config.ConfigurazionePortaHandler;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.GestioneToken;
import org.openspcoop2.core.config.GestioneTokenAutenticazione;
import org.openspcoop2.core.config.MessageSecurity;
import org.openspcoop2.core.config.MessageSecurityFlow;
import org.openspcoop2.core.config.MessageSecurityFlowParameter;
import org.openspcoop2.core.config.MtomProcessor;
import org.openspcoop2.core.config.MtomProcessorFlow;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAutorizzazioneServiziApplicativi;
import org.openspcoop2.core.config.PortaDelegataAutorizzazioneToken;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.PortaDelegataLocalForward;
import org.openspcoop2.core.config.PortaDelegataServizio;
import org.openspcoop2.core.config.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegataSoggettoErogatore;
import org.openspcoop2.core.config.PortaTracciamento;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.Scope;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.config.constants.RuoloTipoMatch;
import org.openspcoop2.core.config.constants.RuoloTipologia;
import org.openspcoop2.core.config.constants.ScopeTipoMatch;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.config.driver.IDServizioUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.id.IdentificativiFruizione;
import org.openspcoop2.core.mapping.DBMappingUtils;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverConfigurazioneDB_porteDelegateDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_porteDelegateDriver {

	private DriverConfigurazioneDB driver = null;
	private DriverConfigurazioneDB_porteDriver porteDriver = null;
	private DriverConfigurazioneDB_soggettiDriver soggettiDriver = null;
	
	protected DriverConfigurazioneDB_porteDelegateDriver(DriverConfigurazioneDB driver) {
		this.driver = driver;
		this.porteDriver = new DriverConfigurazioneDB_porteDriver(driver);
		this.soggettiDriver = new DriverConfigurazioneDB_soggettiDriver(driver);
	}
	
	protected IDPortaDelegata getIDPortaDelegata(String nome) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		String nomeMetodo = "getIDPortaDelegata";
		
		if (nome == null)
			throw new DriverConfigurazioneException("["+nomeMetodo+"] Parametro Non Valido");

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::"+nomeMetodo+"] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome_porta = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setString(1, nome);

			this.driver.logDebug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, nome));
			rs = stm.executeQuery();

			IDPortaDelegata idPD = null;
			if (rs.next()) {
				
				idPD = new IDPortaDelegata();
				
				idPD.setNome(rs.getString("nome_porta"));
				
				IdentificativiFruizione idFruizione = new IdentificativiFruizione();
				
				try{
					long idSoggProprietario = rs.getLong("id_soggetto");
					IDSoggetto idSoggettoProprietario = this.soggettiDriver.getIdSoggetto(idSoggProprietario,con);
					idFruizione.setSoggettoFruitore(idSoggettoProprietario);
				} catch (Exception se) {
					throw new Exception(se.getMessage()); // mappo NotFound in una eccezione generica. Il Soggetto proprietario deve esistere
				}
				
				String nomeSoggettoErogatore = rs.getString("nome_soggetto_erogatore");
				String tipoSoggettoErogatore = rs.getString("tipo_soggetto_erogatore");
				long idSoggettoErogatoreDB = rs.getLong("id_soggetto_erogatore");
				long idSoggErogatore=-1;
				if( (idSoggettoErogatoreDB==-2) || (idSoggettoErogatoreDB>0) ){
					idSoggErogatore = idSoggettoErogatoreDB;
				}
				else{
					try {
						idSoggErogatore = DBUtils.getIdSoggetto(nomeSoggettoErogatore, tipoSoggettoErogatore, con, this.driver.tipoDB,this.driver.tabellaSoggetti);
					} catch (CoreException e) {
						this.driver.logDebug(e.getMessage(),e);
					}
				}
				IDSoggetto idSoggettoErogatore = null;
				if(idSoggErogatore>0){
					idSoggettoErogatore = new IDSoggetto();
					idSoggettoErogatore.setTipo(tipoSoggettoErogatore);
					idSoggettoErogatore.setNome(nomeSoggettoErogatore);
				}else{
					throw new DriverConfigurazioneException("Soggetto Erogatore della Porta Delegata ["+nome+"] non presente.");
				}

				String tipoServizio = rs.getString("tipo_servizio");
				String nomeServizio = rs.getString("nome_servizio");
				Integer versioneServizio = rs.getInt("versione_servizio");
				long idServizioDB = rs.getLong("id_servizio");
				long idServizio=-1;
				if( (idServizioDB==-2) || (idServizioDB>0) ){
					idServizio = idServizioDB;
				}
				else{
					try {
						idServizio = DBUtils.getIdServizio(nomeServizio, tipoServizio, versioneServizio, nomeSoggettoErogatore, tipoSoggettoErogatore, con, this.driver.tipoDB,this.driver.tabellaSoggetti);
					} catch (Exception e) {
						// NON Abilitare il log, poiche' la tabella servizi puo' non esistere per il driver di configurazione 
						// in un database che non ' quello della controlstation ma quello pdd.
						//this.driver.logDebug(e);
					}
				}
				IDServizio idServizioObject = null;
				if(idServizio>0){
					idServizioObject= IDServizioUtils.buildIDServizio(tipoServizio, nomeServizio, idSoggettoErogatore, versioneServizio);
				}

				String azione = rs.getString("nome_azione");
				if(azione!=null && (!"".equals(azione)) && idServizioObject!=null){
					idServizioObject.setAzione(azione);
				}
				idFruizione.setIdServizio(idServizioObject);

				
				idPD.setIdentificativiFruizione(idFruizione);	
			}
			else{
				throw new DriverConfigurazioneNotFound("PortaDelegata ["+nome+"] non esistente");
			}

			return idPD;

		} catch (SQLException se) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::"+nomeMetodo+"] SqlException: " + se.getMessage(),se);
		} catch (DriverConfigurazioneException se) {
			throw se;
		} catch (DriverConfigurazioneNotFound se) {
			throw se;
		} catch (Exception se) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::"+nomeMetodo+"] Exception: " + se.getMessage(),se);
		}
		finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}
	}
	

	protected PortaDelegata getPortaDelegata(IDPortaDelegata idPD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		if (idPD == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaDelegata] Parametro idPD Non Valido");

		String nome = idPD.getNome();

		if ((nome == null)){
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaDelegata] Parametri non Validi");
		}
		
		Connection con = null;
		PortaDelegata pd = null;
		long idPortaDelegata = 0;
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getPortaDelegata(idPortaDelegata)");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaDelegata] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		boolean trovato = false;
		try {

			try{
				idPortaDelegata = DBUtils.getIdPortaDelegata(nome, con, this.driver.tipoDB);
			} catch (Exception se) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaDelegata] Exception: " + se.getMessage(),se);
			}
			if(idPortaDelegata>0){
				trovato = true;
			}
			
		} catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaDelegata] Exception: " + se.getMessage(),se);
		} finally {

			this.driver.closeConnection(con);
		}

		if (trovato) {
			pd=this.driver.getPortaDelegata(idPortaDelegata);

		} else {
			throw new DriverConfigurazioneNotFound("PortaDelegata ["+nome+"] non esistente");
		}

		return pd;
	}

	protected void createPortaDelegata(PortaDelegata aPD) throws DriverConfigurazioneException {
		if (aPD == null)
			throw new DriverConfigurazioneException("Porta Delegata non valida");
		if (aPD.getNome() == null || aPD.getNome().equals(""))
			throw new DriverConfigurazioneException("Nome Porta Delegata non valido");
		if (aPD.getNomeSoggettoProprietario() == null || aPD.getNomeSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Nome Soggetto Proprietario Porta Delegata non valido");
		if (aPD.getTipoSoggettoProprietario() == null || aPD.getTipoSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Tipo Soggetto Proprietario Porta Delegata non valido");
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("createPortaDelegata");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createPortaDelegata] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDPortaDelegata type = 1");

			DriverConfigurazioneDB_porteDelegateLIB.CRUDPortaDelegata(1, aPD, con);

			this.driver.logDebug("Creazione PortaDelegata [" + aPD.getId() + "] completato.");

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createPortaDelegata] Errore durante la creazione della PortaDelegata : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
	
	protected void updatePortaDelegata(PortaDelegata aPD) throws DriverConfigurazioneException {
		if (aPD == null)
			throw new DriverConfigurazioneException("Porta Delegata non valida");
		if (aPD.getNome() == null || aPD.getNome().equals(""))
			throw new DriverConfigurazioneException("Nome Porta Delegata non valido");
		if (aPD.getNomeSoggettoProprietario() == null || aPD.getNomeSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Nome Soggetto Proprietario Porta Delegata non valido");
		if (aPD.getTipoSoggettoProprietario() == null || aPD.getTipoSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Tipo Soggetto Proprietario Porta Delegata non valido");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updatePortaDelegata");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updatePortaDelegata] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDPortaDelegata type = 2");

			//long id = DriverConfigurazioneDB_LIB.CRUDPortaDelegata(2, aPD, con);
			DriverConfigurazioneDB_porteDelegateLIB.CRUDPortaDelegata(2, aPD, con);

			this.driver.logDebug("Aggiornamento PortaDelegata [" + aPD.getId() + "] completato.");

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updatePortaDelegata] Errore durante l'aggiornamento della PortaDelegata : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected void deletePortaDelegata(PortaDelegata aPD) throws DriverConfigurazioneException {
		if (aPD == null)
			throw new DriverConfigurazioneException("Porta Delegata non valida");
		if (aPD.getNome() == null || aPD.getNome().equals(""))
			throw new DriverConfigurazioneException("Nome Porta Delegata non valido");
		if (aPD.getNomeSoggettoProprietario() == null || aPD.getNomeSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Nome Soggetto Proprietario Porta Delegata non valido");
		if (aPD.getTipoSoggettoProprietario() == null || aPD.getTipoSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Tipo Soggetto Proprietario Porta Delegata non valido");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("deletePortaDelegata");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deletePortaDelegata] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDPortaDelegata type = 3");

			long id = DriverConfigurazioneDB_porteDelegateLIB.CRUDPortaDelegata(3, aPD, con);

			this.driver.logDebug("Cancellazione PortaDelegata [" + id + "] completato.");
		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deletePortaDelegata] Errore durante la cancellazione della PortaDelegata : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
	
	
	protected PortaDelegata getPortaDelegata(long id) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return getPortaDelegata(id,null);
	}
	protected PortaDelegata getPortaDelegata(long id,Connection conParam) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		if (id <= 0)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaDelegata] L'id della Porta Delegata deve essere > 0.");

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		PreparedStatement stm1 = null;
		ResultSet rs1 = null;
		String sqlQuery;
		PortaDelegata pd = null;
		long idPortaDelegata = id;


		if(conParam!=null){
			con = conParam;
		}
		else  if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getPortaDelegata(longId)");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaDelegata] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addFromTable(this.driver.tabellaSoggetti);
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.addSelectField("autenticazione");
			sqlQueryObject.addSelectField("autenticazione_opzionale");
			sqlQueryObject.addSelectField("token_policy");
			sqlQueryObject.addSelectField("token_opzionale");
			sqlQueryObject.addSelectField("token_validazione");
			sqlQueryObject.addSelectField("token_introspection");
			sqlQueryObject.addSelectField("token_user_info");
			sqlQueryObject.addSelectField("token_forward");
			sqlQueryObject.addSelectField("token_options");
			sqlQueryObject.addSelectField("token_authn_issuer");
			sqlQueryObject.addSelectField("token_authn_client_id");
			sqlQueryObject.addSelectField("token_authn_subject");
			sqlQueryObject.addSelectField("token_authn_username");
			sqlQueryObject.addSelectField("token_authn_email");
			sqlQueryObject.addSelectField("autorizzazione");
			sqlQueryObject.addSelectField("autorizzazione_xacml");
			sqlQueryObject.addSelectField("autorizzazione_contenuto");
			sqlQueryObject.addSelectField("id_soggetto");
			sqlQueryObject.addSelectField("nome_porta");
			sqlQueryObject.addSelectField("id_soggetto_erogatore");
			sqlQueryObject.addSelectField("tipo_soggetto_erogatore");
			sqlQueryObject.addSelectField("nome_soggetto_erogatore");
			sqlQueryObject.addSelectField("id_servizio");
			sqlQueryObject.addSelectField("tipo_servizio");
			sqlQueryObject.addSelectField("nome_servizio");
			sqlQueryObject.addSelectField("versione_servizio");
			sqlQueryObject.addSelectField("id_azione");
			sqlQueryObject.addSelectField("nome_azione");
			sqlQueryObject.addSelectField("mode_azione");
			sqlQueryObject.addSelectField("pattern_azione");
			sqlQueryObject.addSelectField("nome_porta_delegante_azione");
			sqlQueryObject.addSelectField("force_interface_based_azione");
			sqlQueryObject.addSelectField("ricevuta_asincrona_asim");
			sqlQueryObject.addSelectField("ricevuta_asincrona_sim");
			sqlQueryObject.addSelectField("integrazione");
			sqlQueryObject.addSelectField("scadenza_correlazione_appl");
			sqlQueryObject.addSelectField("validazione_contenuti_stato");
			sqlQueryObject.addSelectField("validazione_contenuti_tipo");
			sqlQueryObject.addSelectField("validazione_contenuti_mtom");
			sqlQueryObject.addSelectField("mtom_request_mode");
			sqlQueryObject.addSelectField("mtom_response_mode");
			sqlQueryObject.addSelectField("security");
			sqlQueryObject.addSelectField("security_mtom_req");
			sqlQueryObject.addSelectField("security_mtom_res");
			sqlQueryObject.addSelectField("security_request_mode");
			sqlQueryObject.addSelectField("security_response_mode");
			sqlQueryObject.addSelectField("allega_body");
			sqlQueryObject.addSelectField("scarta_body");
			sqlQueryObject.addSelectField("gestione_manifest");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORTE_DELEGATE+".descrizione", "descrizionePD");
			sqlQueryObject.addSelectField("stateless");
			sqlQueryObject.addSelectField("local_forward");
			sqlQueryObject.addSelectField("local_forward_pa");
			sqlQueryObject.addSelectField("ruoli_match");
			sqlQueryObject.addSelectField("token_sa_stato");
			sqlQueryObject.addSelectField("token_ruoli_stato");
			sqlQueryObject.addSelectField("token_ruoli_match");
			sqlQueryObject.addSelectField("token_ruoli_tipologia");
			sqlQueryObject.addSelectField("scope_stato");
			sqlQueryObject.addSelectField("scope_match");
			sqlQueryObject.addSelectField("ricerca_porta_azione_delegata");
			sqlQueryObject.addSelectField("msg_diag_severita");
			sqlQueryObject.addSelectField("tracciamento_esiti");
			sqlQueryObject.addSelectField("stato");		
			sqlQueryObject.addSelectField("cors_stato");
			sqlQueryObject.addSelectField("cors_tipo");
			sqlQueryObject.addSelectField("cors_all_allow_origins");
			sqlQueryObject.addSelectField("cors_all_allow_methods");
			sqlQueryObject.addSelectField("cors_all_allow_headers");
			sqlQueryObject.addSelectField("cors_allow_credentials");
			sqlQueryObject.addSelectField("cors_allow_max_age");
			sqlQueryObject.addSelectField("cors_allow_max_age_seconds");
			sqlQueryObject.addSelectField("cors_allow_origins");
			sqlQueryObject.addSelectField("cors_allow_headers");
			sqlQueryObject.addSelectField("cors_allow_methods");
			sqlQueryObject.addSelectField("cors_allow_expose_headers");
			sqlQueryObject.addSelectField("response_cache_stato");
			sqlQueryObject.addSelectField("response_cache_seconds");
			sqlQueryObject.addSelectField("response_cache_max_msg_size");
			sqlQueryObject.addSelectField("response_cache_hash_url");
			sqlQueryObject.addSelectField("response_cache_hash_query");
			sqlQueryObject.addSelectField("response_cache_hash_query_list");
			sqlQueryObject.addSelectField("response_cache_hash_headers");
			sqlQueryObject.addSelectField("response_cache_hash_hdr_list");
			sqlQueryObject.addSelectField("response_cache_hash_payload");
			sqlQueryObject.addSelectField("response_cache_control_nocache");
			sqlQueryObject.addSelectField("response_cache_control_maxage");
			sqlQueryObject.addSelectField("response_cache_control_nostore");
			sqlQueryObject.addSelectField("id_accordo");
			sqlQueryObject.addSelectField("id_port_type");
			sqlQueryObject.addSelectField("options");
			sqlQueryObject.addSelectField("canale");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id_soggetto = "+this.driver.tabellaSoggetti+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, idPortaDelegata);

			rs = stm.executeQuery();

			if (rs.next()) {// ho trovato la porta delegata che cercavo

				pd = new PortaDelegata();

				pd.setOptions(rs.getString("options"));
				
				pd.setTipoSoggettoProprietario(rs.getString("tipo_soggetto"));
				pd.setNomeSoggettoProprietario(rs.getString("nome_soggetto"));

				pd.setId(idPortaDelegata);
				
				pd.setAutenticazione(rs.getString("autenticazione"));
				pd.setAutenticazioneOpzionale(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("autenticazione_opzionale")));
				
				String tokenPolicy = rs.getString("token_policy");
				if(tokenPolicy!=null && !"".equals(tokenPolicy)) {
					GestioneToken gestioneToken = new GestioneToken();
					gestioneToken.setPolicy(tokenPolicy);
					gestioneToken.setTokenOpzionale(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("token_opzionale")));
					gestioneToken.setValidazione(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalitaConWarning(rs.getString("token_validazione")));
					gestioneToken.setIntrospection(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalitaConWarning(rs.getString("token_introspection")));
					gestioneToken.setUserInfo(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalitaConWarning(rs.getString("token_user_info")));
					gestioneToken.setForward(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("token_forward")));
					gestioneToken.setOptions(rs.getString("token_options"));
					
					
					String token_authn_issuer = rs.getString("token_authn_issuer");
					String token_authn_client_id = rs.getString("token_authn_client_id");
					String token_authn_subject = rs.getString("token_authn_subject");
					String token_authn_username = rs.getString("token_authn_username");
					String token_authn_email = rs.getString("token_authn_email");
					if(token_authn_issuer!=null ||
							token_authn_client_id!=null ||
									token_authn_subject!=null ||
											token_authn_username!=null ||
													token_authn_email!=null) {
						gestioneToken.setAutenticazione(new GestioneTokenAutenticazione());
						gestioneToken.getAutenticazione().setIssuer(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(token_authn_issuer));
						gestioneToken.getAutenticazione().setClientId(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(token_authn_client_id));
						gestioneToken.getAutenticazione().setSubject(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(token_authn_subject));
						gestioneToken.getAutenticazione().setUsername(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(token_authn_username));
						gestioneToken.getAutenticazione().setEmail(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(token_authn_email));
					}
					
					pd.setGestioneToken(gestioneToken);
				}
				
				pd.setAutorizzazione(rs.getString("autorizzazione"));
				pd.setXacmlPolicy(rs.getString("autorizzazione_xacml"));
				pd.setAutorizzazioneContenuto(rs.getString("autorizzazione_contenuto"));

				pd.setDescrizione(rs.getString("descrizionePD"));
				pd.setIdSoggetto(rs.getLong("id_soggetto"));
				pd.setNome(rs.getString("nome_porta"));

				//idaccordo
				pd.setIdAccordo(rs.getLong("id_accordo"));
				//id port type
				pd.setIdPortType(rs.getLong("id_port_type"));

				//se mode e' settato allora creo oggetto
				String nomeSoggettoErogatore = rs.getString("nome_soggetto_erogatore");
				String tipoSoggettoErogatore = rs.getString("tipo_soggetto_erogatore");
				long idSoggettoErogatoreDB = rs.getLong("id_soggetto_erogatore");
				long idSoggErogatore=-1;
				if( (idSoggettoErogatoreDB==-2) || (idSoggettoErogatoreDB>0) ){
					idSoggErogatore = idSoggettoErogatoreDB;
				}
				else{
					try {
						idSoggErogatore = DBUtils.getIdSoggetto(nomeSoggettoErogatore, tipoSoggettoErogatore, con, this.driver.tipoDB,this.driver.tabellaSoggetti);
					} catch (CoreException e) {
						this.driver.logDebug(e.getMessage(),e);
					}
				}
				PortaDelegataSoggettoErogatore SoggettoErogatore = null;
				if(idSoggErogatore>0){
					SoggettoErogatore = new PortaDelegataSoggettoErogatore();
					SoggettoErogatore.setId(idSoggErogatore);
					SoggettoErogatore.setNome(nomeSoggettoErogatore);
					SoggettoErogatore.setTipo(tipoSoggettoErogatore);
					pd.setSoggettoErogatore(SoggettoErogatore);
				}else{
					throw new DriverConfigurazioneException("Soggetto Erogatore della Porta Delegata ["+pd.getNome()+"] non presente.");
				}

				//se mode e' settato allora creo oggetto
				String tipoServizio = rs.getString("tipo_servizio");
				String nomeServizio = rs.getString("nome_servizio");
				Integer versioneServizio = rs.getInt("versione_servizio");
				long idServizioDB = rs.getLong("id_servizio");
				long idServizio=-1;
				if( (idServizioDB==-2) || (idServizioDB>0) ){
					idServizio = idServizioDB;
				}
				else{
					try {
						idServizio = DBUtils.getIdServizio(nomeServizio, tipoServizio, versioneServizio, nomeSoggettoErogatore, tipoSoggettoErogatore, con, this.driver.tipoDB,this.driver.tabellaSoggetti);
					} catch (Exception e) {
						// NON Abilitare il log, poiche' la tabella servizi puo' non esistere per il driver di configurazione 
						// in un database che non ' quello della controlstation ma quello pdd.
						//this.driver.logDebug(e);
					}
				}
				PortaDelegataServizio pdServizio = null;
				if(idServizio>0){
					pdServizio=new PortaDelegataServizio();
					pdServizio.setId(idServizio);
					pdServizio.setNome(nomeServizio);
					pdServizio.setTipo(tipoServizio);
					pdServizio.setVersione(versioneServizio);
				}
				// fix
				else {
					pdServizio=new PortaDelegataServizio();
					pdServizio.setNome(nomeServizio);
					pdServizio.setTipo(tipoServizio);
					pdServizio.setVersione(versioneServizio);
				}
				pd.setServizio(pdServizio);

				PortaDelegataAzione pdAzione = null;
				String modeAzione = rs.getString("mode_azione");
				long idAzione = rs.getLong("id_azione");
				if (idAzione>0 || (modeAzione!=null && !"".equals(modeAzione)) ) {
					pdAzione=new PortaDelegataAzione();
					pdAzione.setId(rs.getLong("id_azione"));
					pdAzione.setNome(rs.getString("nome_azione"));
					pdAzione.setIdentificazione(PortaDelegataAzioneIdentificazione.toEnumConstant(modeAzione));
					pdAzione.setPattern(rs.getString("pattern_azione"));
					pdAzione.setNomePortaDelegante(rs.getString("nome_porta_delegante_azione"));
					pdAzione.setForceInterfaceBased(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("force_interface_based_azione")));
				}
				pd.setAzione(pdAzione);






				//ricevuta asincrona_(a)simmetrica
				pd.setRicevutaAsincronaAsimmetrica(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("ricevuta_asincrona_asim")));
				pd.setRicevutaAsincronaSimmetrica(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("ricevuta_asincrona_sim")));
				//integrazione
				pd.setIntegrazione(rs.getString("integrazione"));
				//scadenza correlazione applicativa
				String scadenzaCorrelazione = rs.getString("scadenza_correlazione_appl");
				CorrelazioneApplicativa corr= null;

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQueryObject.addOrderBy("id");
				sqlQueryObject.setSortType(true);
				String queryCorrApp = sqlQueryObject.createSQLQuery();
				PreparedStatement stmCorrApp = con.prepareStatement(queryCorrApp);
				stmCorrApp.setLong(1, idPortaDelegata);
				ResultSet rsCorrApp = stmCorrApp.executeQuery();
				while (rsCorrApp.next()) {
					if(corr==null) corr=new CorrelazioneApplicativa();
					CorrelazioneApplicativaElemento cae = new CorrelazioneApplicativaElemento();
					cae.setId(rsCorrApp.getLong("id"));
					cae.setNome(rsCorrApp.getString("nome_elemento"));
					String modeCA = rsCorrApp.getString("mode_correlazione");
					cae.setIdentificazione(DriverConfigurazioneDB_LIB.getEnumCorrelazioneApplicativaRichiestaIdentificazione(modeCA));
					//if (modeCA.equals("urlBased") || modeCA.equals("contentBased"))
					cae.setPattern(rsCorrApp.getString("pattern"));
					cae.setIdentificazioneFallita(DriverConfigurazioneDB_LIB.getEnumCorrelazioneApplicativaGestioneIdentificazioneFallita(rsCorrApp.getString("identificazione_fallita")));
					cae.setRiusoIdentificativo(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rsCorrApp.getString("riuso_id")));
					corr.addElemento(cae);
				}
				rsCorrApp.close();
				stmCorrApp.close();
				if(corr!=null && scadenzaCorrelazione!=null && !scadenzaCorrelazione.equals(""))
					corr.setScadenza(scadenzaCorrelazione);
				pd.setCorrelazioneApplicativa(corr);
				/*
				  if(scadenzaCorrelazione!=null && !scadenzaCorrelazione.equals("")) {
				  CorrelazioneApplicativa corr= new CorrelazioneApplicativa();
				  corr.setScadenza(scadenzaCorrelazione);
				  pd.setCorrelazioneApplicativa(corr);
				  }
				 */


				// correlazione applicativa risposta
				CorrelazioneApplicativaRisposta corrApplRisposta= null;

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE_RISPOSTA);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQueryObject.addOrderBy("id");
				sqlQueryObject.setSortType(true);
				queryCorrApp = sqlQueryObject.createSQLQuery();
				stmCorrApp = con.prepareStatement(queryCorrApp);
				stmCorrApp.setLong(1, idPortaDelegata);
				rsCorrApp = stmCorrApp.executeQuery();
				while (rsCorrApp.next()) {
					if(corrApplRisposta==null) corrApplRisposta=new CorrelazioneApplicativaRisposta();
					CorrelazioneApplicativaRispostaElemento cae = new CorrelazioneApplicativaRispostaElemento();
					cae.setId(rsCorrApp.getLong("id"));
					cae.setNome(rsCorrApp.getString("nome_elemento"));
					String modeCA = rsCorrApp.getString("mode_correlazione");
					cae.setIdentificazione(DriverConfigurazioneDB_LIB.getEnumCorrelazioneApplicativaRispostaIdentificazione(modeCA));
					//if (modeCA.equals("urlBased") || modeCA.equals("contentBased"))
					cae.setPattern(rsCorrApp.getString("pattern"));
					cae.setIdentificazioneFallita(DriverConfigurazioneDB_LIB.getEnumCorrelazioneApplicativaGestioneIdentificazioneFallita(rsCorrApp.getString("identificazione_fallita")));
					corrApplRisposta.addElemento(cae);
				}
				rsCorrApp.close();
				stmCorrApp.close();
				pd.setCorrelazioneApplicativaRisposta(corrApplRisposta);

				
				//mtom
				MtomProcessor mtomProcessor = null;
				String mtom_request = rs.getString("mtom_request_mode");
				String mtom_response = rs.getString("mtom_response_mode");
				if( (mtom_request!=null && !mtom_request.equals(""))  ||  (mtom_response!=null && !mtom_response.equals("")) ){
					mtomProcessor = new MtomProcessor();
					if((mtom_request!=null && !mtom_request.equals(""))  ){
						mtomProcessor.setRequestFlow(new MtomProcessorFlow());
						mtomProcessor.getRequestFlow().setMode(DriverConfigurazioneDB_LIB.getEnumMTOMProcessorType(mtom_request));
					}
					if((mtom_response!=null && !mtom_response.equals(""))  ){
						mtomProcessor.setResponseFlow(new MtomProcessorFlow());
						mtomProcessor.getResponseFlow().setMode(DriverConfigurazioneDB_LIB.getEnumMTOMProcessorType(mtom_response));
					}
				}
				

				//validazione xsd
				String validazioneContenuti_stato = rs.getString("validazione_contenuti_stato");
				String validazioneContenuti_tipo = rs.getString("validazione_contenuti_tipo");
				String validazioneContenuti_mtom = rs.getString("validazione_contenuti_mtom");
				if(  (validazioneContenuti_stato!=null && !validazioneContenuti_stato.equals(""))  
						||
						(validazioneContenuti_tipo!=null && !validazioneContenuti_tipo.equals(""))  	)
				{
					ValidazioneContenutiApplicativi val = new ValidazioneContenutiApplicativi();
					if((validazioneContenuti_stato!=null && !validazioneContenuti_stato.equals(""))  )
						val.setStato(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalitaConWarning(validazioneContenuti_stato));
					if((validazioneContenuti_tipo!=null && !validazioneContenuti_tipo.equals(""))  )
						val.setTipo(DriverConfigurazioneDB_LIB.getEnumValidazioneContenutiApplicativiTipo(validazioneContenuti_tipo));
					if((validazioneContenuti_mtom!=null && !validazioneContenuti_mtom.equals(""))  )
						val.setAcceptMtomMessage(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(validazioneContenuti_mtom));
					pd.setValidazioneContenutiApplicativi(val);
				}

				// Gestione funzionalita' Attachments
				pd.setAllegaBody(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("allega_body")));
				pd.setScartaBody(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("scarta_body")));
				pd.setGestioneManifest(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("gestione_manifest")));

				// Stateless
				pd.setStateless(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("stateless")));

				// LocalForward
				String statoLocalForward = rs.getString("local_forward");
				PortaDelegataLocalForward pdLocalForward = new PortaDelegataLocalForward();
				pdLocalForward.setStato(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(statoLocalForward));
				pdLocalForward.setPortaApplicativa(rs.getString("local_forward_pa"));
				if(pdLocalForward.getStato()!=null) {
					pd.setLocalForward(pdLocalForward);
				}
				else {
					pd.setLocalForward(null);
				}
				
				// Ricerca Porta Azione Delegata
				if(rs.getString("ricerca_porta_azione_delegata")!=null){
					pd.setRicercaPortaAzioneDelegata(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("ricerca_porta_azione_delegata")));
				}
				
				// Tracciamento
				String msg_diag_severita = rs.getString("msg_diag_severita");
				String tracciamento_esiti = rs.getString("tracciamento_esiti");
				if(msg_diag_severita!=null || tracciamento_esiti!=null) {
					PortaTracciamento tracciamento = new PortaTracciamento();
					tracciamento.setSeverita(DriverConfigurazioneDB_LIB.getEnumSeverita(msg_diag_severita));
					tracciamento.setEsiti(tracciamento_esiti);
					pd.setTracciamento(tracciamento);
				}
				
				// Stato
				if(rs.getString("stato")!=null){
					pd.setStato(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("stato")));
				}
				

				// messageSecurity			
				String security = rs.getString("security");
				String security_mtom_req = rs.getString("security_mtom_req");
				String security_mtom_res = rs.getString("security_mtom_res");
				String security_request_mode = rs.getString("security_request_mode");
				String security_response_mode = rs.getString("security_response_mode");
				MessageSecurity messageSecurity = null;
				if(  (security_mtom_req!=null && !security_mtom_req.equals(""))  
						||
						(security_request_mode!=null && !security_request_mode.equals(""))  
						||
						(security_mtom_res!=null && !security_mtom_res.equals(""))  	
						||
						(security_response_mode!=null && !security_response_mode.equals("")) 
						)
				{
					messageSecurity = new MessageSecurity();
					if((security_mtom_req!=null && !security_mtom_req.equals(""))  ){
						if(messageSecurity.getRequestFlow()==null) {
							messageSecurity.setRequestFlow(new MessageSecurityFlow());	
						}
						messageSecurity.getRequestFlow().setApplyToMtom(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(security_mtom_req));
					}
					if((security_mtom_res!=null && !security_mtom_res.equals(""))  ){
						if(messageSecurity.getResponseFlow()==null) {
							messageSecurity.setResponseFlow(new MessageSecurityFlow());	
						}
						messageSecurity.getResponseFlow().setApplyToMtom(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(security_mtom_res));
					}
					if((security_request_mode!=null && !security_request_mode.equals(""))  ){
						if(messageSecurity.getRequestFlow()==null) {
							messageSecurity.setRequestFlow(new MessageSecurityFlow());	
						}
						messageSecurity.getRequestFlow().setMode(security_request_mode);
					}
					if((security_response_mode!=null && !security_response_mode.equals(""))  ){
						if(messageSecurity.getResponseFlow()==null) {
							messageSecurity.setResponseFlow(new MessageSecurityFlow());	
						}
						messageSecurity.getResponseFlow().setMode(security_response_mode);
					}
				}

				
				// RuoliMatch
				String ruoliMatch = rs.getString("ruoli_match");
				if(ruoliMatch!=null && !"".equals(ruoliMatch)){
					if(pd.getRuoli()==null){
						pd.setRuoli(new AutorizzazioneRuoli());
					}
					pd.getRuoli().setMatch(RuoloTipoMatch.toEnumConstant(ruoliMatch));
				}
				
				// Token SA Stato
				String tokenSaStato = rs.getString("token_sa_stato");
				if( (tokenSaStato!=null && !"".equals(tokenSaStato)) ){
					if(pd.getAutorizzazioneToken()==null){
						pd.setAutorizzazioneToken(new PortaDelegataAutorizzazioneToken());
					}
					pd.getAutorizzazioneToken().setAutorizzazioneApplicativi((DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(tokenSaStato)));
				}
				
				// Token Ruoli
				String tokenRuoliStato = rs.getString("token_ruoli_stato");
				String tokenRuoliMatch = rs.getString("token_ruoli_match");
				String tokenRuoliTipologia = rs.getString("token_ruoli_tipologia");
				if( (tokenRuoliStato!=null && !"".equals(tokenRuoliStato)) ||
						(tokenRuoliMatch!=null && !"".equals(tokenRuoliMatch)) ||
						(tokenRuoliTipologia!=null && !"".equals(tokenRuoliTipologia)) ){
					if(pd.getAutorizzazioneToken()==null){
						pd.setAutorizzazioneToken(new PortaDelegataAutorizzazioneToken());
					}
					pd.getAutorizzazioneToken().setAutorizzazioneRuoli(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(tokenRuoliStato));
					if((tokenRuoliMatch!=null && !"".equals(tokenRuoliMatch))) {
						if(pd.getAutorizzazioneToken().getRuoli()==null){
							pd.getAutorizzazioneToken().setRuoli(new AutorizzazioneRuoli());
						}
						pd.getAutorizzazioneToken().getRuoli().setMatch(RuoloTipoMatch.toEnumConstant(tokenRuoliMatch));
					}
					if((tokenRuoliTipologia!=null && !"".equals(tokenRuoliTipologia))) {
						pd.getAutorizzazioneToken().setTipologiaRuoli(RuoloTipologia.toEnumConstant(tokenRuoliTipologia));
					}
				}
				
				// ScopeMatch
				String scopeStato = rs.getString("scope_stato");
				String scopeMatch = rs.getString("scope_match");
				if( (scopeStato!=null && !"".equals(scopeStato)) || (scopeMatch!=null && !"".equals(scopeMatch)) ){
					if(pd.getScope()==null){
						pd.setScope(new AutorizzazioneScope());
					}
					pd.getScope().setStato(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(scopeStato));
					pd.getScope().setMatch(ScopeTipoMatch.toEnumConstant(scopeMatch));
				}
				
				// Gestione CORS
				String corsStato = rs.getString("cors_stato");
				if(corsStato!=null && !"".equals(corsStato)) {
					pd.setGestioneCors(new CorsConfigurazione());
				 	this.porteDriver.readConfigurazioneCors(pd.getGestioneCors(), rs);
				}
				
				// Gestione CacheResponse
				String responseCacheStato = rs.getString("response_cache_stato");
				if(responseCacheStato!=null && !"".equals(responseCacheStato)) {
					pd.setResponseCaching(new ResponseCachingConfigurazione());
					this.porteDriver.readResponseCaching(idPortaDelegata, false, true, pd.getResponseCaching(), rs, con);
				}
				
				// Canali
				String canale = rs.getString("canale");
				pd.setCanale(canale);
				
				rs.close();
				stm.close();
						
				
				// Trasformazioni
				Trasformazioni trasformazioni = DriverConfigurazioneDB_trasformazioniLIB.readTrasformazioni(idPortaDelegata, true, con);
				if(trasformazioni!=null) {
					pd.setTrasformazioni(trasformazioni);
				}

				
				if(pdAzione!=null) {
					// lista azioni
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AZIONI);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_porta=?");
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm = con.prepareStatement(sqlQuery);
					stm.setLong(1, idPortaDelegata);

					this.driver.logDebug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPortaDelegata));
					rs = stm.executeQuery();

					// Request Flow Parameter
					while (rs.next()) {
						pdAzione.addAzioneDelegata(rs.getString("azione"));
					}
					rs.close();
					stm.close();
				}
				
				
				
				// stato security
				if (CostantiConfigurazione.ABILITATO.toString().equalsIgnoreCase(security)) {
					pd.setStatoMessageSecurity(CostantiConfigurazione.ABILITATO.toString());
				}else{
					pd.setStatoMessageSecurity(CostantiConfigurazione.DISABILITATO.toString());
				}
				
				// lista wss
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);

				this.driver.logDebug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPortaDelegata));
				rs = stm.executeQuery();

				// Request Flow Parameter
				MessageSecurityFlowParameter secRfp;
				while (rs.next()) {
					secRfp = new MessageSecurityFlowParameter();
					secRfp.setNome(rs.getString("nome"));
					secRfp.setValore(rs.getString("valore"));
					if(messageSecurity==null){
						messageSecurity = new MessageSecurity();
					}
					if(messageSecurity.getRequestFlow()==null){
						messageSecurity.setRequestFlow(new MessageSecurityFlow());
					}
					messageSecurity.getRequestFlow().addParameter(secRfp);
				}
				rs.close();
				stm.close();
				// Response Flow Parameter
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);

				this.driver.logDebug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPortaDelegata));
				rs = stm.executeQuery();

				MessageSecurityFlowParameter secResfp;
				while (rs.next()) {
					secResfp = new MessageSecurityFlowParameter();
					secResfp.setNome(rs.getString("nome"));
					secResfp.setValore(rs.getString("valore"));
					if(messageSecurity==null){
						messageSecurity = new MessageSecurity();
					}
					if(messageSecurity.getResponseFlow()==null){
						messageSecurity.setResponseFlow(new MessageSecurityFlow());
					}
					messageSecurity.getResponseFlow().addParameter(secResfp);
				}
				rs.close();
				stm.close();


				// setto il messageSecurity
				pd.setMessageSecurity(messageSecurity);

				
				
				// mtom
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MTOM_REQUEST);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				stm1.setLong(1, idPortaDelegata);

				this.driver.logDebug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPortaDelegata));
				rs1 = stm1.executeQuery();

				// Request Flow Parameter
				MtomProcessorFlowParameter rfp;
				while (rs1.next()) {
					rfp = new MtomProcessorFlowParameter();
					rfp.setNome(rs1.getString("nome"));
					rfp.setPattern(rs1.getString("pattern"));
					rfp.setContentType(rs1.getString("content_type"));
					int required = rs1.getInt("required");
					boolean isrequired = false;
					if (required == CostantiDB.TRUE)
						isrequired = true;
					rfp.setRequired(isrequired);
					
					if(mtomProcessor.getRequestFlow()==null){
						mtomProcessor.setRequestFlow(new MtomProcessorFlow());
					}
					mtomProcessor.getRequestFlow().addParameter(rfp);
				}
				rs1.close();
				stm1.close();

				// Response Flow Parameter
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MTOM_RESPONSE);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				stm1.setLong(1, idPortaDelegata);

				this.driver.logDebug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPortaDelegata));
				rs1 = stm1.executeQuery();

				MtomProcessorFlowParameter rsfp;
				while (rs1.next()) {
					rsfp = new MtomProcessorFlowParameter();
					rsfp.setNome(rs1.getString("nome"));
					rsfp.setPattern(rs1.getString("pattern"));
					rsfp.setContentType(rs1.getString("content_type"));
					int required = rs1.getInt("required");
					boolean isrequired = false;
					if (required == CostantiDB.TRUE)
						isrequired = true;
					rsfp.setRequired(isrequired);
					
					if(mtomProcessor.getResponseFlow()==null){
						mtomProcessor.setResponseFlow(new MtomProcessorFlow());
					}
					mtomProcessor.getResponseFlow().addParameter(rsfp);
				}
				rs1.close();
				stm1.close();
				
				// set mtom
				pd.setMtomProcessor(mtomProcessor);
				
				
				// servizi applicativi
				long idSA = 0;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_SA);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				rs = stm.executeQuery();

				// per ogni entry con id_porta == idPortaDelegata
				// prendo l'id del servizio applicativo associato, recupero il
				// nome e aggiungo
				// il servizio applicativo alla PortaDelegata da ritornare
				while (rs.next()) {
					idSA = rs.getLong("id_servizio_applicativo");

					if (idSA != 0) {
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
						sqlQueryObject.addSelectField("nome");
						sqlQueryObject.addWhereCondition("id=?");
						sqlQuery = sqlQueryObject.createSQLQuery();
						stm1 = con.prepareStatement(sqlQuery);
						stm1.setLong(1, idSA);

						this.driver.logDebug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idSA));

						rs1 = stm1.executeQuery();

						PortaDelegataServizioApplicativo servizioApplicativo = null;
						if (rs1.next()) {
							// setto solo il nome come da specifica
							servizioApplicativo = new PortaDelegataServizioApplicativo();
							servizioApplicativo.setId(idSA);
							servizioApplicativo.setNome(rs1.getString("nome"));
							pd.addServizioApplicativo(servizioApplicativo);
						}
						rs1.close();
						stm1.close();
					}

				}
				rs.close();
				stm.close();
				
				
				// proprieta autenticazione
				Proprieta prop = null;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				rs=stm.executeQuery();
				while (rs.next()) {
					prop = new Proprieta();
					prop.setId(rs.getLong("id"));
					prop.setNome(rs.getString("nome"));
					prop.setValore(rs.getString("valore"));
					pd.addProprietaAutenticazione(prop);
				}
				rs.close();
				stm.close();
				
				
				
				
				// proprieta autorizzazione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				rs=stm.executeQuery();
				while (rs.next()) {
					prop = new Proprieta();
					prop.setId(rs.getLong("id"));
					prop.setNome(rs.getString("nome"));
					prop.setValore(rs.getString("valore"));
					pd.addProprietaAutorizzazione(prop);
				}
				rs.close();
				stm.close();
				
				
				
				// proprieta autorizzazione contenuto
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				rs=stm.executeQuery();
				while (rs.next()) {
					prop = new Proprieta();
					prop.setId(rs.getLong("id"));
					prop.setNome(rs.getString("nome"));
					prop.setValore(rs.getString("valore"));
					pd.addProprietaAutorizzazioneContenuto(prop);
				}
				rs.close();
				stm.close();
				
				
				
				
				
				// proprieta rate limiting
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_RATE_LIMITING_PROP);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				rs=stm.executeQuery();
				while (rs.next()) {
					prop = new Proprieta();
					prop.setId(rs.getLong("id"));
					prop.setNome(rs.getString("nome"));
					prop.setValore(rs.getString("valore"));
					pd.addProprietaRateLimiting(prop);
				}
				rs.close();
				stm.close();
				
				
				
				
				// pd.addSetProperty(setProperty); .....
				prop = null;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_PROP);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				rs=stm.executeQuery();
				while (rs.next()) {
					prop = new Proprieta();
					prop.setId(rs.getLong("id"));
					prop.setNome(rs.getString("nome"));
					prop.setValore(rs.getString("valore"));
					pd.addProprieta(prop);
				}
				rs.close();
				stm.close();
				
							
				// ruoli
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_RUOLI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				rs = stm.executeQuery();

				while (rs.next()) {
					
					if(pd.getRuoli()==null){
						pd.setRuoli(new AutorizzazioneRuoli());
					}
					
					Ruolo ruolo = new Ruolo();
					ruolo.setNome(rs.getString("ruolo"));
					pd.getRuoli().addRuolo(ruolo);
				
				}
				rs.close();
				stm.close();
				
				
				
				// scope
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_SCOPE);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				rs = stm.executeQuery();

				while (rs.next()) {
					
					if(pd.getScope()==null){
						pd.setScope(new AutorizzazioneScope());
					}
					
					Scope scope = new Scope();
					scope.setNome(rs.getString("scope"));
					pd.getScope().addScope(scope);
				
				}
				rs.close();
				stm.close();
				
				
				// servizi applicativi (token)
				idSA = 0;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_TOKEN_SA);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				rs = stm.executeQuery();

				// per ogni entry con id_porta == idPortaDelegata
				// prendo l'id del servizio applicativo associato, recupero il
				// nome e aggiungo
				// il servizio applicativo alla PortaDelegata da ritornare
				while (rs.next()) {
					idSA = rs.getLong("id_servizio_applicativo");

					if (idSA != 0) {
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
						sqlQueryObject.addSelectField("nome");
						sqlQueryObject.addWhereCondition("id=?");
						sqlQuery = sqlQueryObject.createSQLQuery();
						stm1 = con.prepareStatement(sqlQuery);
						stm1.setLong(1, idSA);

						this.driver.logDebug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idSA));

						rs1 = stm1.executeQuery();

						PortaDelegataServizioApplicativo servizioApplicativo = null;
						if (rs1.next()) {
							// setto solo il nome come da specifica
							servizioApplicativo = new PortaDelegataServizioApplicativo();
							servizioApplicativo.setId(idSA);
							servizioApplicativo.setNome(rs1.getString("nome"));
							
							if(pd.getAutorizzazioneToken()==null) {
								pd.setAutorizzazioneToken(new PortaDelegataAutorizzazioneToken());
							}
							if(pd.getAutorizzazioneToken().getServiziApplicativi()==null) {
								pd.getAutorizzazioneToken().setServiziApplicativi(new PortaDelegataAutorizzazioneServiziApplicativi());
							}
							
							pd.getAutorizzazioneToken().getServiziApplicativi().addServizioApplicativo(servizioApplicativo);
						}
						rs1.close();
						stm1.close();
					}

				}
				rs.close();
				stm.close();
				
				
				// ruoli (token)
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_TOKEN_RUOLI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				rs = stm.executeQuery();

				while (rs.next()) {
					
					if(pd.getAutorizzazioneToken()==null) {
						pd.setAutorizzazioneToken(new PortaDelegataAutorizzazioneToken());
					}
					if(pd.getAutorizzazioneToken().getRuoli()==null) {
						pd.getAutorizzazioneToken().setRuoli(new AutorizzazioneRuoli());
					}
					
					Ruolo ruolo = new Ruolo();
					ruolo.setNome(rs.getString("ruolo"));
					pd.getAutorizzazioneToken().getRuoli().addRuolo(ruolo);
				
				}
				rs.close();
				stm.close();
				
				
				
				// dump_config
				DumpConfigurazione dumpConfig = DriverConfigurazioneDB_dumpLIB.readDumpConfigurazione(con, pd.getId(), CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_PD);
				pd.setDump(dumpConfig);
				
				
				// Handlers
				ConfigurazioneMessageHandlers requestHandlers = DriverConfigurazioneDB_handlerLIB.readConfigurazioneMessageHandlers(con, pd.getId(), null, true);
				ConfigurazioneMessageHandlers responseHandlers = DriverConfigurazioneDB_handlerLIB.readConfigurazioneMessageHandlers(con, pd.getId(), null, false);
				if(requestHandlers!=null || responseHandlers!=null) {
					pd.setConfigurazioneHandler(new ConfigurazionePortaHandler());
					pd.getConfigurazioneHandler().setRequest(requestHandlers);
					pd.getConfigurazioneHandler().setResponse(responseHandlers);
				}

				
				
				// Attribute Authority
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_ATTRIBUTE_AUTHORITY);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				rs = stm.executeQuery();

				while (rs.next()) {
					
					String nome = rs.getString("nome");
					String attributi = rs.getString("attributi");
					AttributeAuthority aa = new AttributeAuthority();
					aa.setNome(nome);
					aa.setAttributoList(DBUtils.convertToList(attributi));
					pd.addAttributeAuthority(aa);
				
				}
				rs.close();
				stm.close();
				
				
				
				
				// *** Aggiungo extInfo ***
				
				this.driver.logDebug("ExtendedInfo ...");
				ExtendedInfoManager extInfoManager = ExtendedInfoManager.getInstance();
				IExtendedInfo extInfoConfigurazioneDriver = extInfoManager.newInstanceExtendedInfoPortaDelegata();
				if(extInfoConfigurazioneDriver!=null){
					List<Object> listExtInfo = extInfoConfigurazioneDriver.getAllExtendedInfo(con, this.driver.log, pd);
					if(listExtInfo!=null && listExtInfo.size()>0){
						for (Object object : listExtInfo) {
							pd.addExtendedInfo(object);
						}
					}
				}
				
			} else {
				throw new DriverConfigurazioneNotFound("[DriverConfigurazioneDB::getPortaDelegata] Nessuna PortaDelegata trovata.");
			}

			return pd;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaDelegata] SqlException: " + se.getMessage(),se);
		} catch (DriverConfigurazioneNotFound de) {
			throw de;
		} catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaDelegata] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			JDBCUtilities.closeResources(rs1, stm1);
			this.driver.closeConnection(conParam, con);
		}
	}
	
	protected List<String> porteDelegateRateLimitingValoriUnivoci(String pName) throws DriverConfigurazioneException {
		
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<String> lista = new ArrayList<>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteDelegateRateLimitingValoriUnivoci");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteDelegateRateLimitingValoriUnivoci] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_RATE_LIMITING_PROP);
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addSelectField("valore");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String queryString = sqlQueryObject.createSQLQuery();
			
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, pName);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				lista.add(risultato.getString("valore"));
			}
			risultato.close();
			stmt.close();

			
			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteDelegateRateLimitingValoriUnivoci] Errore : " + qe.getMessage(),qe);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(error,con);
		}
	}
	
	
	protected List<String> nomiProprietaPD(String filterSoggettoTipo, String filterSoggettoNome, List<String> tipoServiziProtocollo) throws DriverConfigurazioneException {
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<String> lista = new ArrayList<>();
		String aliasSoggettiFruitori = "soggettoFruitore";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("nomiProprietaPD");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::nomiProprietaPD] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_PROP);
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE_PROP +".nome");
			sqlQueryObject.addOrderBy(CostantiDB.PORTE_DELEGATE_PROP +".nome"); 
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setANDLogicOperator(true);
			
			if((filterSoggettoNome!=null && !"".equals(filterSoggettoNome)) || (tipoServiziProtocollo != null && tipoServiziProtocollo.size() > 0)) {
				sqlQueryObject.addFromTable(CostantiDB.MAPPING_FRUIZIONE_PD);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_PROP+".id_porta="+CostantiDB.PORTE_DELEGATE+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".id_porta="+CostantiDB.PORTE_DELEGATE+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".id_fruizione="+CostantiDB.SERVIZI_FRUITORI+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_servizio="+CostantiDB.SERVIZI+".id");
				
			}
			
			if((filterSoggettoNome!=null && !"".equals(filterSoggettoNome))) {
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI, aliasSoggettiFruitori);
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_soggetto="+aliasSoggettiFruitori+".id");
				
				sqlQueryObject.addWhereCondition(aliasSoggettiFruitori+".tipo_soggetto=?");
				sqlQueryObject.addWhereCondition(aliasSoggettiFruitori+".nome_soggetto=?");
				
			}
			
			if((tipoServiziProtocollo != null && tipoServiziProtocollo.size() > 0)) {
				String [] tipiServiziProtocolloS = tipoServiziProtocollo.toArray(new String[tipoServiziProtocollo.size()]); 
				sqlQueryObject.addWhereINCondition(CostantiDB.SERVIZI+".tipo_servizio", true, tipiServiziProtocolloS);
			}
			
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			if((filterSoggettoNome!=null && !"".equals(filterSoggettoNome))) {
				stmt.setString(1, filterSoggettoTipo);
				stmt.setString(2, filterSoggettoNome);
			}
			
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				lista.add(risultato.getString("nome"));
			}

			return lista;
		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::nomiProprietaPD] Errore : " + qe.getMessage(),qe);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(error,con);
		}
	}
	
	protected List<PortaDelegata> porteDelegateWithSoggettoErogatoreList(long idSoggettoErogatore) throws DriverConfigurazioneException {
		String nomeMetodo = "porteDelegateWithSoggettoErogatoreList";
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<PortaDelegata> lista = new ArrayList<PortaDelegata>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteDelegateWithSoggettoErogatoreList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_soggetto_erogatore = ?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggettoErogatore);
			risultato = stmt.executeQuery();

			PortaDelegata pd;
			while (risultato.next()) {
				pd = getPortaDelegata(risultato.getLong("id"),con);
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

	protected List<PortaDelegata> porteDelegateWithTipoNomeErogatoreList(String tipoSoggettoErogatore, String nomeSoggettoErogatore) throws DriverConfigurazioneException {
		String nomeMetodo = "porteDelegateWithTipoNomeErogatoreList";
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<PortaDelegata> lista = new ArrayList<PortaDelegata>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteDelegateWithTipoNomeErogatoreList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("tipo_soggetto_erogatore = ?");
			sqlQueryObject.addWhereCondition("nome_soggetto_erogatore = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, tipoSoggettoErogatore);
			stmt.setString(2, nomeSoggettoErogatore);
			risultato = stmt.executeQuery();

			PortaDelegata pd;
			while (risultato.next()) {
				pd = getPortaDelegata(risultato.getLong("id"),con);
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
	
	protected boolean existsPortaDelegata(IDPortaDelegata idPD) throws DriverConfigurazioneException {

		try{
			return getPortaDelegata(idPD)!=null;
		}catch (DriverConfigurazioneNotFound e) {
			return false;
		}	
	}

	

	protected List<PortaDelegata> getPorteDelegateWithServizio(Long idServizio, String tiposervizio, String nomeservizio,
			Integer versioneServizio,
			Long idSoggetto, String tiposoggetto, String nomesoggetto) throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";
		ArrayList<PortaDelegata> lista = new ArrayList<PortaDelegata>();

		try {
			
			if (this.driver.atomica) {
				try {
					con = this.driver.getConnectionFromDatasource("getPorteDelegateWithServizio");
				} catch (Exception e) {
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPorteDelegateWithServizio] Exception accedendo al datasource :" + e.getMessage(),e);

				}

			} else
				con = this.driver.globalConnection;
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(false, "id_servizio = ?", "tipo_servizio = ? AND nome_servizio = ? AND versione_servizio = ?");
			sqlQueryObject.addWhereCondition(false, "id_soggetto_erogatore = ?", "tipo_soggetto_erogatore = ? AND nome_soggetto_erogatore = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			int index = 1;
			stm.setLong(index++, idServizio);
			stm.setString(index++, tiposervizio);
			stm.setString(index++, nomeservizio);
			stm.setInt(index++, versioneServizio);
			stm.setLong(index++, idSoggetto);
			stm.setString(index++, tiposoggetto);
			stm.setString(index++, nomesoggetto);

			this.driver.logDebug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idServizio, tiposervizio, nomeservizio, versioneServizio, idSoggetto, tiposoggetto, nomesoggetto));
			rs = stm.executeQuery();

			while (rs.next()) {
				lista.add(this.getPortaDelegata(rs.getLong("id")));
			}

			return lista;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			JDBCUtilities.closeResources(rs, stm);
			
			this.driver.closeConnection(con);

		}

	}

	protected List<PortaDelegata> getPorteDelegateWithServizio(Long idServizio) throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";
		ArrayList<PortaDelegata> lista = new ArrayList<PortaDelegata>();

		try {
			
			if (this.driver.atomica) {
				try {
					con = this.driver.getConnectionFromDatasource("getPorteDelegateWithServizio");
				} catch (Exception e) {
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPorteDelegateWithServizio] Exception accedendo al datasource :" + e.getMessage(),e);

				}

			} else
				con = this.driver.globalConnection;
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_servizio = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idServizio);

			this.driver.logDebug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idServizio));
			rs = stm.executeQuery();

			while (rs.next()) {
				PortaDelegata pde = this.getPortaDelegata(rs.getLong("id"));
				lista.add(pde);
			}

			return lista;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			JDBCUtilities.closeResources(rs, stm);
			
			this.driver.closeConnection(con);

		}

	}
	
	protected List<IDPortaDelegata> getPortaDelegataAzione(String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		List<IDPortaDelegata> id = new ArrayList<IDPortaDelegata>();
		try {

			if (this.driver.atomica) {
				try {
					con = this.driver.getConnectionFromDatasource("getPortaDelegataAzione");
				} catch (Exception e) {
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsPortaApplicativaAzione] Exception accedendo al datasource :" + e.getMessage(),e);

				}

			} else
				con = this.driver.globalConnection;
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition("nome_azione=?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setString(1, nome);

			this.driver.logDebug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, nome));
			rs = stm.executeQuery();

			while (rs.next()){
				IDPortaDelegata idPD = new IDPortaDelegata();
				idPD.setNome(rs.getString("nome_porta"));
				id.add(idPD);
			}

		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			JDBCUtilities.closeResources(rs, stm);
			
			this.driver.closeConnection(con);

		}

		if(id.size()>0){
			return id;
		}else{
			throw new DriverConfigurazioneNotFound("Porte Delegate che possiedono l'azione ["+nome+"] non esistenti");
		}

	}

	// NOTA: Metodo non sicuro!!! Possono esistere piu' azioni di port type diversi o accordi diversi !!!!!
	protected boolean existsPortaDelegataAzione(String nome) throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		try {
			
			if (this.driver.atomica) {
				try {
					con = this.driver.getConnectionFromDatasource("existsPortaDelegataAzione");
				} catch (Exception e) {
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsPortaDelegataAzione] Exception accedendo al datasource :" + e.getMessage(),e);

				}

			} else
				con = this.driver.globalConnection;
			
			boolean esiste = false;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome_azione=?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setString(1, nome);

			this.driver.logDebug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, nome));
			rs = stm.executeQuery();

			if (rs.next())
				esiste = true;

			return esiste;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			JDBCUtilities.closeResources(rs, stm);
			
			this.driver.closeConnection(con);

		}

	}
	
	protected List<PortaDelegata> getPorteDelegateBySoggetto(long idSoggetto) throws DriverConfigurazioneException {
		String nomeMetodo = "getPorteDelegateBySoggetto";

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<PortaDelegata> lista = new ArrayList<PortaDelegata>();

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
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id_soggetto=?");
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggetto);
			risultato = stmt.executeQuery();

			while (risultato.next()) {

				Long id = risultato.getLong("id");
				lista.add(this.getPortaDelegata(id));

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
	
	protected List<IDPortaDelegata> getAllIdPorteDelegate(
			FiltroRicercaPorteDelegate filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		List<String> nomiPD = null;
		
		this.driver.logDebug("getAllIdPorteDelegate...");

		try {
			this.driver.logDebug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("getAllIdPorteDelegate");
			else
				con = this.driver.globalConnection;

			String alias_SERVIZI_APPLICATIVI_autorizzati = "saauthz";
			String alias_SERVIZI_APPLICATIVI_token_autorizzati = "satokenauthz";
			String alias_SERVIZI_APPLICATIVI_traformazioni = "satrasf";
			String alias_SERVIZI_APPLICATIVI_TOKEN_traformazioni = "satokentrasf";
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			if(filtroRicerca!=null && (filtroRicerca.getTipoSoggetto()!=null || filtroRicerca.getNomeSoggetto()!=null) ){
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			}
			sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".nome_porta");
			if(filtroRicerca!=null && (filtroRicerca.getTipoSoggetto()!=null || filtroRicerca.getNomeSoggetto()!=null) ){
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			}
			if(filtroRicerca!=null){
				if(filtroRicerca.getIdRuolo()!=null){
					sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_RUOLI);
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_RUOLI+".id_porta = "+CostantiDB.PORTE_DELEGATE+".id");
				}
			}
			if(filtroRicerca!=null){
				if(filtroRicerca.getIdScope()!=null){
					sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_SCOPE);
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_SCOPE+".id_porta = "+CostantiDB.PORTE_DELEGATE+".id");
				}
			}
			if(filtroRicerca!=null){
				if(filtroRicerca.getNomeServizioApplicativo()!=null){
					sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_SA);
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI, alias_SERVIZI_APPLICATIVI_autorizzati);
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_SA+".id_porta = "+CostantiDB.PORTE_DELEGATE+".id");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_SA+".id_servizio_applicativo = "+alias_SERVIZI_APPLICATIVI_autorizzati+".id");
				}
			}
			if(filtroRicerca!=null){
				if(filtroRicerca.getNomeServizioApplicativoToken()!=null){
					sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_TOKEN_SA);
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI, alias_SERVIZI_APPLICATIVI_token_autorizzati);
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_TOKEN_SA+".id_porta = "+CostantiDB.PORTE_DELEGATE+".id");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_TOKEN_SA+".id_servizio_applicativo = "+alias_SERVIZI_APPLICATIVI_token_autorizzati+".id");
				}
			}
			if(filtroRicerca!=null){
				if(filtroRicerca.getIdRuoloToken()!=null){
					sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_TOKEN_RUOLI);
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_TOKEN_RUOLI+".id_porta = "+CostantiDB.PORTE_DELEGATE+".id");
				}
			}
			if(filtroRicerca!=null){
				if(filtroRicerca.getNomeServizioApplicativoRiferitoApplicabilitaTrasformazione()!=null){
					sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_SA);
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI,alias_SERVIZI_APPLICATIVI_traformazioni);
					sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI);
					sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SA);
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI+".id_porta = "+CostantiDB.PORTE_DELEGATE+".id");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SA+".id_trasformazione = "+CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI+".id");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SA+".id_servizio_applicativo = "+alias_SERVIZI_APPLICATIVI_traformazioni+".id");
				}
			}
			if(filtroRicerca!=null){
				if(filtroRicerca.getNomeServizioApplicativoTokenRiferitoApplicabilitaTrasformazione()!=null){
					sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_TOKEN_SA);
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI,alias_SERVIZI_APPLICATIVI_TOKEN_traformazioni);
					sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI);
					sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SA);
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI+".id_porta = "+CostantiDB.PORTE_DELEGATE+".id");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SA+".id_trasformazione = "+CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI+".id");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SA+".id_servizio_applicativo = "+alias_SERVIZI_APPLICATIVI_traformazioni+".id");
				}
			}
			boolean porteDelegatePerAzioni = false;
			if(filtroRicerca!=null && filtroRicerca.getNomePortaDelegante()!=null) {
				porteDelegatePerAzioni = true;
				if(filtroRicerca.getAzione()!=null) {
					sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AZIONI);
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_AZIONI+".id_porta = "+CostantiDB.PORTE_DELEGATE+".id");
				}
			}

			if(filtroRicerca!=null){
				// Filtro By Data
				if(filtroRicerca.getMinDate()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".ora_registrazione > ?");
				if(filtroRicerca.getMaxDate()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".ora_registrazione < ?");
				if(filtroRicerca.getTipoSoggetto()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto = ?");
				if(filtroRicerca.getNomeSoggetto()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto = ?");
				if(filtroRicerca.getTipoSoggettoErogatore()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".tipo_soggetto_erogatore = ?");
				if(filtroRicerca.getNomeSoggettoErogatore()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".nome_soggetto_erogatore = ?");
				if(filtroRicerca.getTipoServizio()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".tipo_servizio = ?");
				if(filtroRicerca.getNomeServizio()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".nome_servizio = ?");
				if(filtroRicerca.getVersioneServizio()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".versione_servizio = ?");
				if(!porteDelegatePerAzioni && filtroRicerca.getAzione()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".nome_azione = ?");
				if(filtroRicerca.getNome()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".nome_porta = ?");
				if(filtroRicerca.getIdRuolo()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_RUOLI+".ruolo = ?");
				if(filtroRicerca.getIdScope()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_SCOPE+".scope = ?");
				if(filtroRicerca.getNomeServizioApplicativo()!=null)
					sqlQueryObject.addWhereCondition(alias_SERVIZI_APPLICATIVI_autorizzati+".nome = ?");		
				if(filtroRicerca.getNomeServizioApplicativoToken()!=null)
					sqlQueryObject.addWhereCondition(alias_SERVIZI_APPLICATIVI_token_autorizzati+".nome = ?");	
				if(filtroRicerca.getIdRuoloToken()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_TOKEN_RUOLI+".ruolo = ?");
				if(filtroRicerca.getNomeServizioApplicativoRiferitoApplicabilitaTrasformazione()!=null)
					sqlQueryObject.addWhereCondition(alias_SERVIZI_APPLICATIVI_traformazioni+".nome = ?");	
				if(filtroRicerca.getNomeServizioApplicativoTokenRiferitoApplicabilitaTrasformazione()!=null)
					sqlQueryObject.addWhereCondition(alias_SERVIZI_APPLICATIVI_TOKEN_traformazioni+".nome = ?");	
				if(filtroRicerca.getStato()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".stato = ?");
				if(porteDelegatePerAzioni) {
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".nome_porta_delegante_azione = ?");
					if(filtroRicerca.getAzione()!=null)
						sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_AZIONI+".azione = ?");
				}
			}

			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			this.driver.logDebug("eseguo query : " + sqlQuery );
			stm = con.prepareStatement(sqlQuery);
			int indexStmt = 1;
			if(filtroRicerca!=null){
				if(filtroRicerca.getMinDate()!=null){
					this.driver.logDebug("minDate stmt.setTimestamp("+filtroRicerca.getMinDate()+")");
					stm.setTimestamp(indexStmt, new Timestamp(filtroRicerca.getMinDate().getTime()));
					indexStmt++;
				}
				if(filtroRicerca.getMaxDate()!=null){
					this.driver.logDebug("maxDate stmt.setTimestamp("+filtroRicerca.getMaxDate()+")");
					stm.setTimestamp(indexStmt, new Timestamp(filtroRicerca.getMaxDate().getTime()));
					indexStmt++;
				}	
				if(filtroRicerca.getTipoSoggetto()!=null){
					this.driver.logDebug("tipoSoggetto stmt.setString("+filtroRicerca.getTipoSoggetto()+")");
					stm.setString(indexStmt, filtroRicerca.getTipoSoggetto());
					indexStmt++;
				}
				if(filtroRicerca.getNomeSoggetto()!=null){
					this.driver.logDebug("nomeSoggetto stmt.setString("+filtroRicerca.getNomeSoggetto()+")");
					stm.setString(indexStmt, filtroRicerca.getNomeSoggetto());
					indexStmt++;
				}	
				if(filtroRicerca.getTipoSoggettoErogatore()!=null){
					this.driver.logDebug("tipoSoggettoErogatore stmt.setString("+filtroRicerca.getTipoSoggettoErogatore()+")");
					stm.setString(indexStmt, filtroRicerca.getTipoSoggettoErogatore());
					indexStmt++;
				}
				if(filtroRicerca.getNomeSoggettoErogatore()!=null){
					this.driver.logDebug("nomeSoggettoErogatore stmt.setString("+filtroRicerca.getNomeSoggettoErogatore()+")");
					stm.setString(indexStmt, filtroRicerca.getNomeSoggettoErogatore());
					indexStmt++;
				}	
				if(filtroRicerca.getTipoServizio()!=null){
					this.driver.logDebug("tipoServizio stmt.setString("+filtroRicerca.getTipoServizio()+")");
					stm.setString(indexStmt, filtroRicerca.getTipoServizio());
					indexStmt++;
				}
				if(filtroRicerca.getNomeServizio()!=null){
					this.driver.logDebug("nomeServizio stmt.setString("+filtroRicerca.getNomeServizio()+")");
					stm.setString(indexStmt, filtroRicerca.getNomeServizio());
					indexStmt++;
				}	
				if(filtroRicerca.getVersioneServizio()!=null){
					this.driver.logDebug("versioneServizio stmt.setInt("+filtroRicerca.getVersioneServizio()+")");
					stm.setInt(indexStmt, filtroRicerca.getVersioneServizio());
					indexStmt++;
				}	
				if(!porteDelegatePerAzioni && filtroRicerca.getAzione()!=null){
					this.driver.logDebug("azione stmt.setString("+filtroRicerca.getAzione()+")");
					stm.setString(indexStmt, filtroRicerca.getAzione());
					indexStmt++;
				}	
				if(filtroRicerca.getNome()!=null){
					this.driver.logDebug("nome stmt.setString("+filtroRicerca.getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getNome());
					indexStmt++;
				}
				if(filtroRicerca.getIdRuolo()!=null){
					this.driver.logDebug("ruolo stmt.setString("+filtroRicerca.getIdRuolo().getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getIdRuolo().getNome());
					indexStmt++;
				}
				if(filtroRicerca.getIdScope()!=null){
					this.driver.logDebug("scope stmt.setString("+filtroRicerca.getIdScope().getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getIdScope().getNome());
					indexStmt++;
				}
				if(filtroRicerca.getNomeServizioApplicativo()!=null){
					this.driver.logDebug("servizioApplicativoAuthz stmt.setString("+filtroRicerca.getNomeServizioApplicativo()+")");
					stm.setString(indexStmt, filtroRicerca.getNomeServizioApplicativo());
					indexStmt++;
				}
				if(filtroRicerca.getNomeServizioApplicativoToken()!=null){
					this.driver.logDebug("servizioApplicativoTokenAuthz stmt.setString("+filtroRicerca.getNomeServizioApplicativoToken()+")");
					stm.setString(indexStmt, filtroRicerca.getNomeServizioApplicativoToken());
					indexStmt++;
				}
				if(filtroRicerca.getIdRuoloToken()!=null){
					this.driver.logDebug("ruoloToken stmt.setString("+filtroRicerca.getIdRuoloToken().getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getIdRuoloToken().getNome());
					indexStmt++;
				}
				if(filtroRicerca.getNomeServizioApplicativoRiferitoApplicabilitaTrasformazione()!=null){
					this.driver.logDebug("servizioApplicativoTrasformazioni stmt.setString("+filtroRicerca.getNomeServizioApplicativoRiferitoApplicabilitaTrasformazione()+")");
					stm.setString(indexStmt, filtroRicerca.getNomeServizioApplicativoRiferitoApplicabilitaTrasformazione());
					indexStmt++;
				}
				if(filtroRicerca.getNomeServizioApplicativoTokenRiferitoApplicabilitaTrasformazione()!=null){
					this.driver.logDebug("servizioApplicativoTokenTrasformazioni stmt.setString("+filtroRicerca.getNomeServizioApplicativoTokenRiferitoApplicabilitaTrasformazione()+")");
					stm.setString(indexStmt, filtroRicerca.getNomeServizioApplicativoTokenRiferitoApplicabilitaTrasformazione());
					indexStmt++;
				}
				if(filtroRicerca.getStato()!=null){
					this.driver.logDebug("stato stmt.setString("+filtroRicerca.getStato().getValue()+")");
					stm.setString(indexStmt, filtroRicerca.getStato().getValue());
					indexStmt++;
				}
				if(porteDelegatePerAzioni) {
					this.driver.logDebug("nomePortaDelegata stmt.setString("+filtroRicerca.getNomePortaDelegante()+")");
					stm.setString(indexStmt, filtroRicerca.getNomePortaDelegante());
					indexStmt++;
					if(filtroRicerca.getAzione()!=null) {
						this.driver.logDebug("azione stmt.setString("+filtroRicerca.getAzione()+")");
						stm.setString(indexStmt, filtroRicerca.getAzione());
						indexStmt++;
					}
				}
			}
			rs = stm.executeQuery();
			nomiPD = new ArrayList<>();
			while (rs.next()) {
				nomiPD.add(rs.getString("nome_porta"));
			}
		}catch(DriverConfigurazioneNotFound de){
			throw de;
		}
		catch(Exception e){
			throw new DriverConfigurazioneException("getAllIdPorteDelegate error",e);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(con);

		}
		
		if(nomiPD == null || nomiPD.size()<=0){
			if(filtroRicerca!=null)
				throw new DriverConfigurazioneNotFound("PorteDelegate non trovate che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
			else
				throw new DriverConfigurazioneNotFound("PorteDelegate non trovate");
		}else{
			List<IDPortaDelegata> idsPD = new ArrayList<IDPortaDelegata>();
			for (String nomePortaDelegata : nomiPD) {
				idsPD.add(this.getIDPortaDelegata(nomePortaDelegata));
			}
			return idsPD;
		}
		
	}
	
	protected List<PortaDelegata> getPorteDelegateByPolicyGestioneToken(String nome) throws DriverConfigurazioneException{
		String nomeMetodo = "getPorteDelegateByPolicyGestioneToken";

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<PortaDelegata> lista = new ArrayList<PortaDelegata>();

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
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("token_policy=?");
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, nome);
			risultato = stmt.executeQuery();

			while (risultato.next()) {

				Long id = risultato.getLong("id");
				lista.add(this.getPortaDelegata(id));

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
	
	protected MappingFruizionePortaDelegata getMappingFruizione(IDServizio idServizio, IDSoggetto idSoggetto, IDPortaDelegata idPortaDelegata) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		
		Connection con = null;
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getMappingFruizione");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getMappingFruizione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		try {
			
			return DBMappingUtils.getMappingFruizione(idServizio, idSoggetto, idPortaDelegata, con, this.driver.tipoDB);

		} catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getMappingFruizione] Exception: " + se.getMessage(),se);
		} finally {
			this.driver.closeConnection(con);
		}
		
	}
}
